package org.zw.android.framework.impl;

import org.zw.android.framework.IBitmapDownloader;
import org.zw.android.framework.cache.IDownloaderCallback;
import org.zw.android.framework.cache.ImageDownloader;
import org.zw.android.framework.cache.ImageItem;
import org.zw.android.framework.cache.RecyclingImageView;
import org.zw.android.framework.cache.ImageDownloader.State;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Asynchronous loading Bitmap Implements
 * 
 * @author zhouwei
 *
 */
public final class BitmapDownloaderImpl implements IBitmapDownloader {
	
	private ImageDownloader mCacheManager ;
	private final ImageItem mImageItem = new ImageItem() ;
	private Context mContext ;
	
	protected BitmapDownloaderImpl(Context context,String cacheName,float percent,int maxWidth,int maxHeight){
		
		if(cacheName == null){
			throw new RuntimeException("BitmapDownloaderImpl CacheName is null");
		}
		
		mContext 	  = context ;
		mCacheManager = ImageDownloader.create(context, cacheName, percent < 0.01f ? 0.01f : percent,maxWidth,maxHeight);
	}
	
	@Override
	public Context getContext() {
		return mContext;
	}

	@Override
	public void downloadBitmap(String url, IDownloaderCallback callback) {
		
		final ImageItem item = mImageItem.clone() ;
		item.setHighUrl(url);
		item.setLowUrl(url);
		item.setTag(url);
		
		mCacheManager.downloadBitmap(item, callback);
	}

	@Override
	public void downloadBitmap(String url, RecyclingImageView imageView) {
		downloadBitmap(url,imageView,null);
	}
	
	@Override
	public void downloadBitmap(String url, RecyclingImageView imageView, int rid) {
		downloadBitmap(url,imageView,rid,false);
	}

	@Override
	public void downloadBitmap(String url, RecyclingImageView imageView, int rid,boolean fadeIn) {
		downloadBitmap(url,imageView,rid,null,null,fadeIn);
	}

	@Override
	public void downloadBitmap(String url, RecyclingImageView imageView,
			int rid, int viewWidth, int viewHeight) {
		
		final ImageItem item = mImageItem.clone() ;
		item.setHighUrl(url);
		item.setLowUrl(url);
		item.setTag(null);
		item.setViewWidth(viewWidth) ;
		item.setViewHeight(viewHeight) ;
		
		mCacheManager.downloadBitmap(item, imageView, rid, null, false);
	}

	@Override
	public void downloadBitmapByDecode(String url,
			RecyclingImageView imageView, int rid, int decodeWidth,
			int decodeHeight) {
		
		final ImageItem item = mImageItem.clone() ;
		item.setHighUrl(url);
		item.setLowUrl(url);
		item.setTag(null);
		item.setDecodeWidth(decodeWidth);
		item.setDecodeHeight(decodeHeight);
		
		mCacheManager.downloadBitmap(item, imageView, rid, null, false);
	}

	@Override
	public void downloadBitmap(String url, RecyclingImageView imageView,
			int rid, int decodeWidth, int decodeHeight, int viewWidth,
			int viewHeight) {
		
		final ImageItem item = mImageItem.clone() ;
		item.setHighUrl(url);
		item.setLowUrl(url);
		item.setTag(null);
		item.setDecodeWidth(decodeWidth);
		item.setDecodeHeight(decodeHeight);
		item.setViewWidth(viewWidth) ;
		item.setViewHeight(viewHeight) ;
		
		mCacheManager.downloadBitmap(item, imageView, rid, null, false);
	}

	@Override
	public void downloadBitmap(String url, 
			RecyclingImageView imageView, 
			int rid,
			Object tag, 
			IDownloaderCallback callback, 
			boolean fadeIn) {
		
		final ImageItem item = mImageItem.clone() ;
		item.setHighUrl(url);
		item.setLowUrl(url);
		item.setTag(tag);
		
		mCacheManager.downloadBitmap(item, imageView, rid, callback, fadeIn);
	}

	@Override
	public void downloadBitmap(String url, RecyclingImageView imageView,Bitmap defaultBitmap) {
		downloadBitmap(url,imageView,defaultBitmap,null);
	}
	
	@Override
	public void downloadBitmap(String url, RecyclingImageView imageView, Bitmap defaultBitmap,boolean fadeIn) {
		downloadBitmap(url,imageView,defaultBitmap,null,fadeIn,false);
	}
	
	@Override
	public void downloadBitmap(String url, RecyclingImageView imageView,Bitmap defaultBitmap, IDownloaderCallback callback) {
		downloadBitmap(url,imageView,defaultBitmap,callback,false,false);
	}

	@Override
	public void downloadBitmap(String url, 
				RecyclingImageView imageView,
				Bitmap defaultBitmap, 
				IDownloaderCallback callback,
				boolean fadeInBitmap) {
		downloadBitmap(url,imageView,defaultBitmap,callback,null,fadeInBitmap);
	}
	
	@Override
	public void downloadBitmap(String url, 
			RecyclingImageView imageView,
			Bitmap defaultBitmap, 
			IDownloaderCallback callback,
			Object tag, 
			boolean fadeIn) {
		
		final ImageItem item = mImageItem.clone() ;
		item.setHighUrl(url);
		item.setLowUrl(url);
		item.setTag(tag);
		
		mCacheManager.downloadBitmap(item, imageView, defaultBitmap, callback, fadeIn);
	}

	@Override
	public void downloadBitmap(ImageItem item, RecyclingImageView imageView,int rid,
			IDownloaderCallback callback, boolean fadeIn) {
		
		if(item == null || imageView == null){
			return ;
		}
		
		mCacheManager.downloadBitmap(item, imageView, rid, callback, fadeIn);
	}

	@Override
	public Bitmap getBitmap(String url) {
		return mCacheManager != null ? mCacheManager.getBitmapFromCache(url) : null;
	}

	@Override
	public String getCacheLocalPath(String url) {
		return mCacheManager != null ? mCacheManager.getCacheLocalPath(url) : null;
	}

	@Override
	public void onStart() {
		if(mCacheManager != null){
			mCacheManager.onStart() ;
		}
	}

	@Override
	public void onPause() {
		if(mCacheManager != null){
			mCacheManager.onPause() ;
		}
	}

	@Override
	public void onResume() {
		if(mCacheManager != null){
			mCacheManager.onResume() ;
		}
	}

	@Override
	public void onDestory() {
		if(mCacheManager != null){
			mCacheManager.onDestory() ;
		}
	}

	@Override
	public void setState(State newState) {
		if(mCacheManager != null){
			mCacheManager.setState(newState) ;
		}
	}

	@Override
	public State getState() {
		return mCacheManager != null ? mCacheManager.getState() : State.STATE_OFF ;
	}

	@Override
	public void clearDiskCache() {
		if(mCacheManager != null){
			mCacheManager.clearDiskCacheFile() ;
		}
	}

	@Override
	public void onReset() {
		if(mCacheManager != null){
			mCacheManager.onReset() ;
		}
	}

	@Override
	public void onCancelTask() {
		if(mCacheManager != null){
			mCacheManager.onCancelTask() ;
		}
	}
	
}
