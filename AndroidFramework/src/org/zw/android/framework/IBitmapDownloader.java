package org.zw.android.framework;

import org.zw.android.framework.cache.IDownloaderCallback;
import org.zw.android.framework.cache.ImageDownloader.State;
import org.zw.android.framework.cache.ImageItem;
import org.zw.android.framework.cache.RecyclingImageView;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Asynchronous loading Bitmap
 * 
 * @author zhouwei
 *
 */
public interface IBitmapDownloader {
	
	public void downloadBitmap(String url,IDownloaderCallback callback);
	
	public void downloadBitmap(String url,RecyclingImageView imageView);
	
	public void downloadBitmap(String url,RecyclingImageView imageView, int rid);
	
	public void downloadBitmap(String url,RecyclingImageView 
									imageView, 
									int rid, 
									int viewWidth,
									int viewHeight);
	
	public void downloadBitmapByDecode(String url,RecyclingImageView 
									imageView, 
									int rid, 
									int decodeWidth,
									int decodeHeight);
	
	public void downloadBitmap(String url,RecyclingImageView 
									imageView, 
									int rid, 
									int decodeWidth ,
									int decodeHeight ,
									int viewWidth,
									int viewHeight);
	
	public void downloadBitmap(String url,
							RecyclingImageView imageView, 
							int rid,
							boolean fadeIn);
	
	public void downloadBitmap(String url,
							RecyclingImageView imageView, 
							int rid,
							Object tag,
							IDownloaderCallback callback, 
							boolean fadeIn);
	
	public void downloadBitmap(String url,RecyclingImageView imageView, Bitmap defaultBitmap);
	
	public void downloadBitmap(String url,
							RecyclingImageView imageView, 
							Bitmap defaultBitmap,
							boolean fadeIn);
	
	public void downloadBitmap(String url,
							RecyclingImageView imageView, 
							Bitmap defaultBitmap,
							IDownloaderCallback callback);

	public void downloadBitmap(String url,
							RecyclingImageView imageView, 
							Bitmap defaultBitmap,
							IDownloaderCallback callback, 
							boolean fadeInBitmap);
	
	public void downloadBitmap(String url,
							RecyclingImageView imageView, 
							Bitmap defaultBitmap,
							IDownloaderCallback callback, 
							Object tag, 
							boolean fadeIn);
	
	public void downloadBitmap(ImageItem item,
							RecyclingImageView imageView, 
							int rid,
							IDownloaderCallback callback, 
							boolean fadeIn);

	public Bitmap getBitmap(String url);
	
	public String getCacheLocalPath(String url);

	/** start download task*/
	public void onStart();

	/** pause download task*/
	public void onPause();

	/** resume download task*/
	public void onResume();

	/** exit download task*/
	public void onDestory();
	
	/** clear cache memory and reload */
	public void onReset();
	
	/** clear current task */
	public void onCancelTask() ;
	
	/** clear disk cache*/
	public void clearDiskCache();
	
	public void setState(State newState);

	public State getState();

	public Context getContext();
}
