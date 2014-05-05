package org.zw.android.framework.cache;

import java.io.File;
import java.security.MessageDigest;
import java.util.Vector;

import org.android.framework.BuildConfig;
import org.zw.android.framework.util.BitmapUtils;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

/**
 * 
 * @author zhouwei
 * 
 */
public final class ImageCache {

	private static final String TAG 					= "ImageCache";

	/** drawable cache*/
	private LruCache<String, BitmapDrawable> mMemoryCache;

	/** bitmap file cache log */
	protected final Vector<String> mDiskFileList = new Vector<String>();

	/** cache path */
	private String mDiskCachePath = "mnt/sdcard/org.framework.cache/";
	
	protected ImageCache(int maxSize, String diskPath) {

		Log.d(TAG, String.format("Init ImageCache dir=%s", diskPath));

		if (maxSize <= 100 || diskPath == null || diskPath.equals("")) {
			throw new RuntimeException("Image Cache parameter error");
		}

		mDiskCachePath = diskPath;
		mDiskFileList.clear();

		// create directory
		File cache = new File(mDiskCachePath);
		if (!cache.isDirectory()) {
			cache.mkdirs();
		}

		mMemoryCache = new LruCache<String, BitmapDrawable>(maxSize) {

			/**
			 * Notify the removed entry that is no longer being cached
			 */
			@Override
			protected void entryRemoved(boolean evicted, String key,
					BitmapDrawable oldValue, BitmapDrawable newValue) {

				if (RecyclingBitmapDrawable.class.isInstance(oldValue)) {
					// The removed entry is a recycling drawable, so notify it
					// that it has been removed from the memory cache
					((RecyclingBitmapDrawable) oldValue).setIsCached(false);
				}
				
				if(BuildConfig.DEBUG) {
					Log.w(TAG, "zhouwei Image Cache count = " + getCounter() + "  mem Size=" + getUsedSpace());
				}
			}

			/**
			 * Measure item size in kilobytes rather than units which is more
			 * practical for a bitmap cache
			 */
			@Override
			protected int sizeOf(String key, BitmapDrawable value) {
				final int bitmapSize = getBitmapSize(value) / 1024;
				return bitmapSize == 0 ? 1 : bitmapSize;
			}
		};
		
		// load cache file list from disk
		loadDiskCache() ;

		Log.d(TAG, "Init ImageCache");
	}
	
	/**
	 * Get the size in bytes of a bitmap in a BitmapDrawable.
	 * 
	 * @param value
	 * @return size in bytes
	 */
	protected static int getBitmapSize(BitmapDrawable value) {
		Bitmap bitmap = value.getBitmap();
		return bitmap.getRowBytes() * bitmap.getHeight();
	}

	/**
	 * Adds a bitmap to both memory and disk cache.
	 * 
	 * @param data
	 *            Unique identifier for the bitmap to store
	 * @param value
	 *            The bitmap drawable to store
	 */
	public void putBitmapToCache(String data, BitmapDrawable value) {

		if (data == null || value == null) {
			return;
		}

		final String key 	= getUniqueName(data);
		final LruCache<String, BitmapDrawable> cache = mMemoryCache ;

		// Add to memory cache
		if (cache != null) {

			if (RecyclingBitmapDrawable.class.isInstance(value)) {
				// The removed entry is a recycling drawable, so notify it
				// that it has been added into the memory cache
				((RecyclingBitmapDrawable) value).setIsCached(true);
			}

			// add Drawable to cache
			cache.put(key, value);
		}
	}

	/**
	 * Get from memory cache.
	 * 
	 * @param data
	 *            Unique identifier for which item to get
	 * @return The bitmap drawable if found in cache, null otherwise
	 */
	protected BitmapDrawable getBitmapFromMemCache(String url) {

		BitmapDrawable memValue = null;

		final String key 	= getUniqueName(url);
		final LruCache<String, BitmapDrawable> cache = mMemoryCache ;

		if (cache != null) {
			memValue = cache.get(key);
		}

		return memValue;
	}

	/**
	 * Get from disk cache.
	 * 
	 * @param data
	 *            Unique identifier for which item to get
	 * @return The bitmap if found in cache, null otherwise
	 */
	protected Bitmap getBitmapFromDiskCache(String url) {
		
		if (checkURLInDisk(url)) {
			
			Bitmap bitmap = BitmapUtils.createBitmap(getDiskCachPath(url), 0, 0) ;
			
			// file error
			if(bitmap == null){
				removeURLInDisk(url);
			}
			
			return bitmap ;
		} else {
			return null;
		}
	}

	/**
	 * Get local disk path
	 * 
	 * @param url
	 * @return
	 */
	protected final String getDiskCachPath(String url) {
		final String key = getUniqueName(url);
		return mDiskCachePath + File.separator + key;
	}

	/**
	 * load disk cache
	 */
	protected void loadDiskCache() {

		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				final File cache = new File(mDiskCachePath);
				// load work file list
				if (cache.isDirectory()) {
					File[] wfs = cache.listFiles();
					for (int index = 0, size = wfs.length; index < size; index++) {
						String fname = wfs[index].getName();
						addDiskCacheFile(fname);
					}
				}
			}
		}) ;
		
		t.setDaemon(true);
		t.setName("#Load Disk Cache");
		t.start() ;
	}

	/**
	 * build unique name
	 * 
	 * @param fileName
	 *            : file name
	 * @return : md5 file name
	 */
	public static final String getUniqueName(String fileName) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			return byteArray2HexStr(digest.digest(fileName.getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param bytes
	 * @return
	 */
	private static final String byteArray2HexStr(final byte[] bytes) {
		final StringBuffer sb = new StringBuffer();
		final int size = bytes.length;
		for (int i = 0; i < size; i++) {
			String hex = Integer.toHexString(bytes[i] + 128);
			// if the value is smaller than 16, the hex string has only one
			// character, add a "0".
			if (hex.length() == 1) {
				hex = "0" + hex;
			}
			sb.append(hex);
		}
		return sb.toString();
	}

	/**
	 * add cache log
	 * 
	 * @param key
	 */
	protected void addDiskCacheFile(String key) {

		if (!mDiskFileList.contains(key)) {
			mDiskFileList.add(key);
		}
	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	protected boolean checkURLInDisk(String url){
		return mDiskFileList.contains(getUniqueName(url));
	}
	
	/**
	 * 
	 * @param url
	 * @return
	 */
	protected void removeURLInDisk(String url){
		
		String path = getDiskCachPath(url) ;
		
		try{
			File file = new File(path);
			file.delete() ;
		}catch(Exception e){
			//
		}
		
		mDiskFileList.remove(getUniqueName(url));
	}
	
	/**
	 * Clear Memory cache
	 */
	protected void clearMemoryCache(){
		mMemoryCache.evictAll() ;
		mDiskFileList.clear() ;
	}
	
	/**
	 * clear cache file, Note, it will delete all cache file
	 */
	protected void clearCacheFile() {

		if (mDiskCachePath == null || mDiskCachePath.equals(""))
			return;

		try {
			File cache = new File(mDiskCachePath);
			if (cache.isDirectory()) {
				File[] files = cache.listFiles();
				final int length = files.length;
				for (int i = 0; i < length; i++) {
					files[i].delete();
				}
			} else {
				cache.delete();
			}
			//
			cache = null;
		} catch (Exception e) {
			e.printStackTrace();
		}

		mDiskFileList.clear();
	}
}
