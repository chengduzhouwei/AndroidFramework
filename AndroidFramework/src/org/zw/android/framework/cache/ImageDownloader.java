package org.zw.android.framework.cache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

import org.zw.android.framework.log.Debug;
import org.zw.android.framework.util.BitmapUtils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;


/**
 * 
 * @author zhouwei
 *
 */
public final class ImageDownloader {
	
	private static final String TAG 		= "CacheManager" ;
	private static final int FADE_IN_TIME	= 200 ;
	private static final int TIME_OUT		= 6 * 1000 ;
	private static final int MAX_QUEUE		= 100 ;
	private static final String HTTP_HEADER = "http://" ;
	
	public static enum State{STATE_ON,STATE_OFF}
	
	private static ImageDownloader _instance ;
	
	private int mMemCacheSize = 5 * 1024;
	private String mCachePath ;
	private ImageCache mImageCache ;
	
	private boolean mExitTasksEarly = false;
    protected boolean mPauseWork = false;
    private final Object mPauseWorkLock = new Object();
    private final Object mDiskCacheLock = new Object();
    private final ResultDrawable mResult = new ResultDrawable() ;
    
    /** default Bitmap */
	private final HashMap<Integer, Bitmap> mDefaultBitmap = new HashMap<Integer, Bitmap>() ;
	private final ArrayBlockingQueue<BitmapWorkerTask> mQueue = new ArrayBlockingQueue<ImageDownloader.BitmapWorkerTask>(MAX_QUEUE);
    
    private Context 	mContext ;
    private Resources 	mResources ;
    private State		mState = State.STATE_ON ;
    private boolean 	mWifiConnected ;
    private ConnectivityManager mConnectManager ;
    private static boolean 	mDownHigh = true ;
	
	ImageDownloader(Context context,String cacheName,float percent,int maxWidth,int maxHeight){
		
		 if (percent < 0.05f || percent > 0.8f) {
             throw new IllegalArgumentException("setMemCacheSizePercent - percent must be "
                     + "between 0.05 and 0.8 (inclusive)");
         }
		 
		 if(context == null){
			 throw new IllegalArgumentException("Context is null");
		 }
		 
		try {
			mConnectManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		} catch (Exception e) {
			e.printStackTrace() ;
		}
		 
		 mContext			= context ;
		 mResources			= context.getResources() ;
		 mWifiConnected		= isWifiConnected();
		 
		 mCachePath 	 	=  getDiskCacheDir(context,cacheName);
		 mMemCacheSize 		=  Math.round(percent * Runtime.getRuntime().maxMemory() / 1024);
		 mImageCache		= new ImageCache(mMemCacheSize,mCachePath);
		 
		 BitmapUtils.MAX_WIDTH	= maxWidth <= 0 ? BitmapUtils.MAX_WIDTH : maxWidth ;
		 BitmapUtils.MAX_HEIGHT	= maxHeight <= 0 ? BitmapUtils.MAX_HEIGHT : maxHeight ;
		 
		 Debug.d(TAG, String.format("CachePath=%s;memCacheSize=%d", mCachePath,mMemCacheSize));
	}
	
	private boolean isWifiConnected() {

		if (mConnectManager != null) {

			NetworkInfo mWiFiNetworkInfo = mConnectManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

			if (mWiFiNetworkInfo != null) {
				return mWiFiNetworkInfo.isAvailable();
			}
		}

		return false;
	}
	
	private static String getDiskCacheDir(Context context, String uniqueName) {
		final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ? 
                				getExternalCacheDir(context).getPath()
                                : context.getCacheDir().getPath();

        return cachePath + File.separator + uniqueName ;
    }
    
	private static File getExternalCacheDir(Context context) {
		// "/Android/data/"
        final String cacheDir = "/" + context.getPackageName() + "/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }
	
	public static ImageDownloader create(Context context,String cacheName,float percent,int maxWidth,int maxHeight){
		
		if(_instance == null){
			_instance = new ImageDownloader(context, cacheName, percent,maxWidth,maxHeight);
		}
		
		return _instance ;
	}
	
	public static ImageDownloader getImageDownloader(){
		return _instance ;
	}
	
	/**
	 * 
	 * @param high
	 */
	public static void setDownloadHD(boolean high){
		mDownHigh	= high ;
	}
	
	/** clear all disk cache file */
	public void clearDiskCacheFile() {

		Debug.d(TAG, "clearDiskCache, It will remove all cache file from disk");
		
		mDefaultBitmap.clear() ;

		if (mImageCache != null) {
			mImageCache.clearCacheFile() ;
		}
	}
	
	/** get cache local path */
	public String getCacheLocalPath(String url) {
		
		if(url == null || url.isEmpty()) return null ;
		
		return mImageCache != null ? mImageCache.getDiskCachPath(url) : null ;
	}
	
	public final void downloadBitmap(ImageItem item,IDownloaderCallback callback){
		downloadBitmap(item,new ImageView(mContext),-1,callback,false);
	}
	
	public final void downloadBitmap(ImageItem item,ImageView imageView,int rid,IDownloaderCallback callback,boolean fadeInBitmap){
		
		final HashMap<Integer, Bitmap> map = mDefaultBitmap ;
		Bitmap bitmap = null ;
		
		if(rid > 0 ){
			bitmap = map.get(rid) ;
			if(bitmap == null){
				bitmap	= BitmapFactory.decodeResource(mResources, rid);
				map.put(rid, bitmap) ;
			}
		}
		
		// download bitmap for imageview
		downloadBitmap(item, imageView, bitmap, callback, fadeInBitmap ? bitmap != null : false);
	}
	
	public final void downloadBitmap(ImageItem item,ImageView imageView,Bitmap defaultBitmap,IDownloaderCallback callback,boolean fadeInBitmap){
		
		// check ImageItem
		if (item == null) {
			
			// Debug.e(TAG, "Item is null");
			
			// 设置默认图片
        	if(defaultBitmap != null){
        		imageView.setImageBitmap(defaultBitmap) ;
        	}
        	
            return;
        }
		
		// 1. 检测本地高清图片是否存在? (是-->2) | (否-->6)
		// 2. 从缓存中查找高清图片 --> 3
		// 3. 如果存在高清 (是--> 4) | (否-->5)
		// 4. 从缓存中取高清
		// 5. 下载高清
		// 6. 检测下载高清是否打开
		
		// 选择图片地址
		final ImageCache imageCache = mImageCache ;
		final String hdUrl		= item.getHighUrl() ;
		final String lowUrl		= item.getLowUrl() ;
		String unique 			= item.getValidUrl() ;
		BitmapDrawable value 	= null ;
		
		// 检测有效地址
		if(unique == null || unique.isEmpty()){
			// 是否已经存在高清,优先选择高清
			if(imageCache != null && hdUrl != null && imageCache.checkURLInDisk(hdUrl)){
				item.setValidUrl(hdUrl);
			} else {
				// 高清标记是否打开
				if(mWifiConnected){
					item.setValidUrl(hdUrl);
				} else {
					if(mDownHigh){
						item.setValidUrl(hdUrl);
					}else {
						item.setValidUrl(lowUrl);
					}
				}
			}
		}
		
		// 使用地址
		unique 		= item.getValidUrl() ;
        
        // 是否有效地址
        if(unique == null || unique.isEmpty()){
        	
        	// Debug.e(TAG, "Item Valid error");
        	
        	// 设置默认图片
        	if(defaultBitmap != null){
        		imageView.setImageBitmap(defaultBitmap) ;
        	}
        	
        	return ;
        }
        
        // 从缓存中查找
        if (imageCache != null) {
        	// 自定义图片
        	if(callback != null){
        		// 从内存中查找图片
        		BitmapDrawable src = imageCache.getBitmapFromMemCache(unique);
        		// 图片是否存在
        		if(src != null && src.getBitmap() != null){
        			// call download finish
        			callback.downloadFinish(item, src.getBitmap());
        			// call handle bitmap
        			Bitmap des = callback.handleBitmap(item, src.getBitmap(), unique.equals(hdUrl));
        			// create bitmap Drawable
        			if(des != null){
        				value = new BitmapDrawable(des);
        			}
        		}
        	} else {
        		value = imageCache.getBitmapFromMemCache(unique);
        	}
        }

        // 设置图片
        if (value != null) {
        	
        	// set auto adjust
        	autoAdjustment(imageView,value,item.getViewWidth(),item.getViewHeight()); 
        	
        	// null background
        	imageView.setBackgroundDrawable(null);
        	
        	// set image view
            imageView.setImageDrawable(value);
            
            return ;
        } 
        
        // download bitmap
        if (cancelPotentialWork(item, imageView)) 
        {
        	final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            final AsyncDrawable asyncDrawable = new AsyncDrawable(mResources, defaultBitmap,task);
            imageView.setImageDrawable(asyncDrawable);
            
            // insert task
            insertTaskIntoQueue(task);
            
            // parameter list : item,defaultBitmap,callback,FadeIn
            task.executeOnExecutor(AsyncTask.DUAL_THREAD_EXECUTOR, item,defaultBitmap,callback,fadeInBitmap);
        }
	}
	
	public Bitmap getBitmapFromCache(String url){
		
		if(url == null || url.isEmpty()) return null ;
		
		Bitmap bitmap = null ;
		
		final ImageCache imageCache = mImageCache ;
		
		if(imageCache != null){
			
			BitmapDrawable drawable = imageCache.getBitmapFromMemCache(url) ;
			
			if(drawable != null){
				return drawable.getBitmap() ;
			}
		}
		
		return bitmap ;
	}
	
	/**
	 * 
	 * @param data
	 * @param imageView
	 * @return
	 */
	private static boolean cancelPotentialWork(ImageItem data, ImageView imageView) {
    	
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
        	
            final ImageItem bitmapData = bitmapWorkerTask.data;
            
            if (bitmapData == null || !bitmapData.getValidUrl().equals(data.getValidUrl())) {
                bitmapWorkerTask.cancel(true);
            } else {
            	return false;
            }
        }
        
        return true;
    }
	
	private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
    	
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        
        return null;
    }
    
    public void setState(State newState){
    	
    	if(mState != newState){
    		mState	= newState ;
    	}
    }
    
    public State getState(){
    	return mState ;
    }
    
	public void onStart() {
		mExitTasksEarly = false;
		setPause(false);
	}
	
	public void onPause(){
		setPause(true);
	}
	
	public void onResume(){
		setPause(false);
	}
	
	public void onCancelTask(){
		
		Debug.d(TAG, "onCancelTask clear all task ");
		
		final ArrayBlockingQueue<BitmapWorkerTask> queue = mQueue ;
		
		for(BitmapWorkerTask task : queue){
			task.cancel(true);
		}
		
		queue.clear() ;
	}
	
	private void removeTaskFromQueue(BitmapWorkerTask task){
		mQueue.remove(task);
	}
	
	private void insertTaskIntoQueue(BitmapWorkerTask task){
		
		final ArrayBlockingQueue<BitmapWorkerTask> queue = mQueue ;
		
		if(queue.remainingCapacity() <= 0){
			onCancelTask();
		}
		
		queue.offer(task);
	}
	
	/**
	 * 重置缓存
	 */
	public synchronized void onReset(){
		
		Debug.d(TAG, "onReset Cache app=" + mContext.getPackageName());
		
		// 停止
		setState(State.STATE_OFF) ;
		onDestory();
		
		if(mImageCache != null){
			mImageCache.clearMemoryCache() ;
		}
		
		// clear downloading
		onCancelTask() ;
		
		// 开始
		setState(State.STATE_ON) ;
		onStart() ;
		
		// 重新加载磁盘缓存
		if (mImageCache != null) {
			mImageCache.loadDiskCache();
		}
	}
	
	public void onDestory(){
		
		 mExitTasksEarly = true;
		 
		 setPause(true);
	}
	
	private final void setPause(boolean pause) {

		if (mPauseWork == pause)
			return;

		synchronized (mPauseWorkLock) {
			mPauseWork = pause;
			if (!mPauseWork) {
				mPauseWorkLock.notifyAll();
			}
		}
	}
	
	private void autoAdjustment(ImageView imageView, BitmapDrawable drawable,int vw,int vh){
		
		int width 		= drawable.getBitmap().getWidth();
		int height 		= drawable.getBitmap().getHeight() ;
		
		if(vw <= 0 || vw > 480 ){
			return ;
		}
		
		LayoutParams lp = imageView.getLayoutParams();
		lp.height 	=  (height *vw) / width; // auto adjust height
		
		imageView.setLayoutParams(lp);
	}
	
	private void setImageDrawable(boolean fadeIn,
			ImageView imageView, 
			BitmapDrawable drawable,
			Bitmap defBitmap ,
			int vw,int vh) {
    	
		// auto
		autoAdjustment(imageView,drawable,vw,vh);
        
        if (fadeIn) {
        	
            // 淡入效果
            final TransitionDrawable td =
                    new TransitionDrawable(new Drawable[] {
                    		new ColorDrawable(android.R.color.transparent),
                            drawable
                    });
            
            // 设置默认背景
            imageView.setImageDrawable(new BitmapDrawable(mResources, defBitmap));

            imageView.setImageDrawable(td);
            
            //
            td.startTransition(FADE_IN_TIME);
        } else {
        	imageView.setBackgroundDrawable(null);
            imageView.setImageDrawable(drawable);
        }
    }
    
    /** 后台线程处理的对象 */
    private static final class ResultDrawable implements Cloneable {
    	
    	public ImageItem data ;
    	public BitmapDrawable drawable ;
    	public Bitmap defBitmap ;
    	public IDownloaderCallback callback ;
    	public boolean fadeIn ;
    	public int  viewWidth ;
    	public int  viewHeight ;
    	
    	public ResultDrawable(){
    		data		= null ;
    		drawable	= null ;
    		defBitmap	= null ;
    		callback	= null ;
    		fadeIn		= false ;
    		viewWidth	= 0 ;
    		viewHeight	= 0 ;
    	}
    	
		@Override
		public ResultDrawable clone(){
			try{
				return (ResultDrawable)super.clone();
			} catch(CloneNotSupportedException e){
				return new ResultDrawable() ;
			}
		}
    }
	
	////////////////////////////////////////////////////////////////////////////////////////
	 /**
     * The actual AsyncTask that will asynchronously process the image.
     */
    private final class BitmapWorkerTask extends AsyncTask<Object, Void, ResultDrawable> {
    	
        private ImageItem data;
        private final WeakReference<ImageView> imageViewReference;

        public BitmapWorkerTask(ImageView imageView) {
            imageViewReference 	= new WeakReference<ImageView>(imageView);
        }

        /**
         * Background processing.
         */
        @Override
        protected ResultDrawable doInBackground(Object... params) {
        	
        	final ResultDrawable out 	= mResult.clone() ;
        	final ImageCache imageCache = mImageCache ;
        	
        	out.data 		= (ImageItem) params[0];
        	out.defBitmap 	= (Bitmap) params[1];
        	out.callback 	= (IDownloaderCallback) params[2];
        	out.fadeIn 		= (Boolean) params[3];
        	out.viewWidth	= out.data.getViewWidth() ;
        	out.viewHeight	= out.data.getViewHeight() ;
        	
        	data 			= out.data ;
        	
        	// 默认选择高清
        	final  String dataString = data.getValidUrl() ;
            
            final String key		= dataString ;
            Bitmap bitmap 			= null ;
            BitmapDrawable drawable = null;
            final boolean isNetRes	= dataString.indexOf(HTTP_HEADER) == 0 ;

            // 如果暂停的时候,等待
            synchronized (mPauseWorkLock) {
                while (mPauseWork && !isCancelled()) {
                    try {
                        mPauseWorkLock.wait();
                    } catch (InterruptedException e) {}
                }
            }
            
            // 从磁盘中查找文件是否存在
            if (imageCache != null && !isCancelled() && getAttachedImageView() != null
                    && !mExitTasksEarly) {
            	// decode bitmap from disk
            	if(isNetRes) {
            		bitmap = imageCache.getBitmapFromDiskCache(dataString);
            	} 
            	// 本地资源
            	else {
            		bitmap = BitmapUtils.createBitmap(dataString, out.data.getDecodeWidth(), out.data.getDecodeHeight());
            	}
            }
            
            // debug
            if(isNetRes && mState == State.STATE_OFF){
            	Debug.d(TAG, "Download OFF");
            }
            
            // 从网络上下图
            if (bitmap == null && isNetRes && !isCancelled() && getAttachedImageView() != null
                    && !mExitTasksEarly) {
            	
            	// network connect type
            	boolean isWifi = isWifiConnected();
            	
            	// download on | off
            	if(isWifi || mState == State.STATE_ON){
            		
            		Debug.d(TAG, "Start Download Bitmap from Network");
            		
            		bitmap = downloadResourecAndDecode(dataString, out.callback,data,imageCache.getDiskCachPath(dataString));
            	} else {
            		Debug.d(TAG, "WIFI not connected and 2G/3G download closed ");
            	}
            }
            
            // call finish
			if(out.callback != null){
				 out.callback.downloadFinish(data, bitmap);
			}
            
            // 添加到缓存中
            if (bitmap != null) {
            	
                drawable = new RecyclingBitmapDrawable(mResources, bitmap);
                
                if (imageCache != null) {
                	imageCache.putBitmapToCache(key, drawable);
                }
            } else {
            	Debug.e(TAG, "Error(" + dataString + ") download failed");
            }
            
            out.drawable = drawable ;
            
            // 从队列中删除任务
            removeTaskFromQueue(this);

            return out ;
        }

        /** 后台线程处理完成后,显示到UI上*/
        @Override
        protected void onPostExecute(ResultDrawable value) {
        	
            // 检测任务是否退出
            if (isCancelled() || mExitTasksEarly) {
                value = null;
            }
            
            final ImageView imageView = getAttachedImageView();
            
            if(value != null && imageView != null && value.drawable != null){
            	// 自定义
            	if(value.callback != null){
            		boolean hd = value.data.getValidUrl().equals(value.data.getHighUrl());
            		Bitmap des = value.callback.handleBitmap(value.data, value.drawable.getBitmap(), hd);
            		if(des != null){
            			setImageDrawable(value.fadeIn,imageView, 
            					new BitmapDrawable(des),value.defBitmap,
            					value.viewWidth,value.viewHeight);
            		}
            		
            	} else {
            		setImageDrawable(value.fadeIn,imageView, 
            				value.drawable,value.defBitmap,
            				value.viewWidth,value.viewHeight);
            	}
            }
        }

        @Override
        protected void onCancelled(ResultDrawable value) {
            super.onCancelled(value);
            
            synchronized (mPauseWorkLock) {
                mPauseWorkLock.notifyAll();
            }
        }

        /** 得到当前ImageView 对应的同步任务 */
        private ImageView getAttachedImageView() {
        	
            final ImageView imageView = imageViewReference.get();
            
            final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

            if (this == bitmapWorkerTask) {
                return imageView;
            }

            return null;
        }
    }
    
    /**
     * 自定义同步Drawable,代理BitmapDrawable(显示默认图片)
     * 
     * @author zhouwei
     *
     */
    private final static class AsyncDrawable extends BitmapDrawable {
    	
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res,Bitmap bitmap,BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }
    
    /** 下载资源 */
	private final Bitmap downloadResourecAndDecode(String url,IDownloaderCallback callback,ImageItem item,String outFile) {
		
		if(url == null || url.isEmpty()) return null ;
		
		Bitmap bitmap = null ;
		
		try{
			
			URL u = new URL(url.trim());
			HttpURLConnection connection = (HttpURLConnection)u.openConnection() ;
			connection.setConnectTimeout(TIME_OUT);
			connection.setReadTimeout(TIME_OUT);
			connection.setRequestMethod("GET");
			connection.connect() ;
			int size = connection.getContentLength() ;
			InputStream input = connection.getInputStream() ;
			
			Debug.d(TAG, String.format("URL=%s;Size=%d", url,size));
			
			// download file and decode bitmap
			if(readInputStream(url,size,input,callback,item,outFile)){
				
				// decode bitmap
				bitmap = BitmapUtils.createBitmap(outFile, item.getDecodeWidth(), item.getDecodeHeight());
			
				// add file to disk list
				if (bitmap != null && mImageCache != null) {
					mImageCache.addDiskCacheFile(ImageCache.getUniqueName(url));
				}
			}
		} catch(Exception e){
			Debug.e(TAG, e.toString());
		}
		
		return bitmap ;
	}
	
	/** */
    private final boolean readInputStream(String url,int total,InputStream input,IDownloaderCallback callback,ImageItem item,String outFile) throws Exception {
       
    	synchronized (mDiskCacheLock) {
    		
    		FileOutputStream output 	= new FileOutputStream(outFile);
    		boolean readFinish			= false ;
    		
            try {
                // read byte[]
                int read = -1;
                int down = 0 ,parent = 0 ,pre_parent = 0;
                byte[] buffer = new byte[8 * 1024];// 8KB
                
                // download start
                if(callback != null){
                	callback.downloadStart(item, total) ;
                }
                
                // start read
                while ((read = input.read(buffer)) != -1) {
                	
                	// download counter
                	down += read ;
                	
                	// download parent
                	if(total > 0) parent 	= (int)(((float) down / total) * 100) ;
                	
                	// save source to local sdcard
                	if(output != null){
                		output.write(buffer, 0, read);
                		output.flush();
                	}
                	
                	// call back
                	if(parent != pre_parent && callback != null){
                		callback.downloadProgress(item, total, parent, buffer, read) ;
                		pre_parent = parent ;
                	}
                	
                	// set read stream flag
                	readFinish = true ;
                }
            } catch (Exception e) {
                throw e;
            } finally {
                // release resource
                if (output != null) {
                	output.close();
                	output = null;
                }
                //release input stream
                if(input != null){
                    input.close();
                    input = null ;
                }
            }
            // return byte[]
            return readFinish;
		}
    }
    
}
