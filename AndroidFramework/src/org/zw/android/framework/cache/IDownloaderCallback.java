package org.zw.android.framework.cache;

import android.graphics.Bitmap;

/**
 * Download Bitmap callback
 * 
 * @author zhouwei
 *
 */
public abstract class IDownloaderCallback {
	
	/** download start */
	public void downloadStart(ImageItem item ,int total){
		
	}
	
	/** download progress */
	public void downloadProgress(ImageItem item ,int total,int parent,byte[] data,int length){
		
	}
	
	/** download finish*/
	public abstract void downloadFinish(ImageItem item , Bitmap src) ;
	
	public Bitmap handleBitmap(ImageItem item,Bitmap src,boolean hd){
		return src ;
	}
}
