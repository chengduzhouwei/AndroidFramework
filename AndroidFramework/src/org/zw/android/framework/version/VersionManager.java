package org.zw.android.framework.version;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Xml;

/**
 * 版本管理
 * 
 * @author zhouwei
 * 
 */
public final class VersionManager {
	
	static final String TAG = "VersionManager" ;
	
	private static final int MSG_UPDATE 			= 1 ;
	private static final int MSG_DOWN_PROGRESS 		= 2 ;
	private static final int MSG_HIDE_DOWN_PROGRESS = 3 ;
	private static final int TIME_OUT				= 5000 ;
	private static final String CHECK_URL = "http://120.192.31.185/cardora/update.xml" ;
	
	private static final String TEXT_NEW_VERSION		= "检测到新版本,是否现在升级?" ;
	private static final String TEXT_BNT_YES			= "确定" ;
	private static final String TEXT_BNT_NO				= "取消" ;
	private static final String TEXT_PROGRESS_TITLE		= "正在下载更新" ;
	
	private static VersionListener mListener ;
	private static ProgressDialog mProgressDialog ;
	private static Handler 		 mHandler ;
	
	/** 检测版本 */
	public static void checkVersion(final Activity activity,VersionListener listener){
		
		// 在非wifi情况下,不检测更新
		/*
		int type 	= getConnectedType(activity);
		if (type != ConnectivityManager.TYPE_WIFI) {
			listener.notUpdate();
			return;
		}
		*/
		//
		mListener	= listener ;
		//
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case MSG_UPDATE:
					showUpdateDialog(activity,(VersionInfo)msg.obj);
					break;
				case MSG_DOWN_PROGRESS :
					
					if(mProgressDialog == null){
						mProgressDialog	= new ProgressDialog(activity);
						mProgressDialog.setCancelable(false);
						mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
						mProgressDialog.setMessage(TEXT_PROGRESS_TITLE);
					}
					
					if(!mProgressDialog.isShowing())
						mProgressDialog.show();
					
					mProgressDialog.setMax(msg.arg1/1024);
					mProgressDialog.setProgress(msg.arg2/1024);
					break ;
				case MSG_HIDE_DOWN_PROGRESS :
					
					if(mProgressDialog != null){
						mProgressDialog.dismiss() ;
					}
					mProgressDialog = null ;
					break ;
				}
			}
		};
		
		//
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				InputStream input = null ;
				
				try{
					URL url = new URL(CHECK_URL);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setConnectTimeout(TIME_OUT);
					input 				= conn.getInputStream();
					VersionInfo info 	= parserVersionXml(input);
					
					int serverVersion = Integer.valueOf(info.getMajorVersion());
					
					PackageInfo pack = getVersionName(activity);
					int clientVersion = Integer.valueOf(pack.versionCode);
					
					//
					if(serverVersion > clientVersion){
						downloadAndInstall(activity,info);
					} else {
						//
						float sv = Float.valueOf(info.getAccessoryVersion());
						float lv = Float.valueOf(pack.versionName);
						// 服务器版本大于本地版本
						if(sv > lv){
							mHandler.obtainMessage(MSG_UPDATE, info).sendToTarget();
						} else {
							mListener.notUpdate() ;
						}
					}
				} catch(Exception e){
					e.printStackTrace() ;
					// 连接超时 和 XML 进行错误
					mListener.notUpdate() ;
				} finally {
					if(input != null){
						try {
							input.close() ;
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}).start() ;
		
	}
	
	/** 网络类型 */
	public static int getConnectedType(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
				return mNetworkInfo.getType();
			}
		}
		return -1;
	}

	public static PackageInfo getVersionName(Context context) {
		try{
			PackageManager packageManager = context.getPackageManager();
			return packageManager.getPackageInfo(context.getPackageName(), 0);
		} catch(NameNotFoundException e){
			return null ;
		}
	}
	
	/** 下载并安装 */
	private static void downloadAndInstall(final Activity activity,final VersionInfo info){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				try{
					
					File file = downloadApkFile(info.getUrl());
					
					mHandler.obtainMessage(MSG_HIDE_DOWN_PROGRESS).sendToTarget() ;
					
					Thread.sleep(1500);
					
					if(file != null){
						installApk(activity,file);
					} else {
						mListener.notUpdate() ;
					}
				}catch(Exception e){
					e.printStackTrace() ;
				}
			}
		}).start() ;
	}

	/** 下载文件 */
	private static File downloadApkFile(String path){

		try{
			
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
	
				URL url = new URL(path);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(TIME_OUT);
				// apk length
				final int length = conn.getContentLength() ;
				
				InputStream is = conn.getInputStream();
				File file = new File(Environment.getExternalStorageDirectory(),"Carduola" + System.currentTimeMillis() + ".apk");
				FileOutputStream fos = new FileOutputStream(file);
				BufferedInputStream bis = new BufferedInputStream(is);
				
				byte[] buffer = new byte[1024];
				int len;
				int total = 0;
				while ((len = bis.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
					total += len;
					// progress
					mHandler.obtainMessage(MSG_DOWN_PROGRESS, length, total).sendToTarget();
				}
				
				fos.close();
				bis.close();
				is.close();
				return file;
			} else {
				return null;
			}
		}catch(Exception e){
			e.printStackTrace() ;
			return null ;
		}
	}
	
	/** new version dialog hint */
	private static void showUpdateDialog(final Activity activity,final VersionInfo info) {
		AlertDialog.Builder builer = new Builder(activity);
		builer.setTitle(TEXT_NEW_VERSION);
		//builer.setIcon(R.drawable.ic_launcher_small);
		builer.setMessage(info.getDescription());
		builer.setCancelable(false);
		builer.setPositiveButton(TEXT_BNT_YES, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				downloadAndInstall(activity, info);
			}
		});
		builer.setNegativeButton(TEXT_BNT_NO, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				mListener.notUpdate() ;
			}
		}).create()
		.show() ;
	}
	
	/** xml parser */
	private static VersionInfo parserVersionXml(InputStream is) throws Exception{
		
		final XmlPullParser  parser = Xml.newPullParser();  
		parser.setInput(is, "utf-8");
		int type = parser.getEventType();
		VersionInfo info = new VersionInfo();
		
		while(type != XmlPullParser.END_DOCUMENT ){
			switch (type) {
			case XmlPullParser.START_TAG:
				String tag = parser.getName() ;
				if(tag.equals("majorversion")){
					info.setMajorVersion(parser.nextText());
				} else if (tag.equals("accessoryversion")){
					info.setAccessoryVersion(parser.nextText());
				} else if (tag.equals("description")){
					info.setDescription(parser.nextText());
				} else if (tag.equals("url")){
					info.setUrl(parser.nextText());
				}
				break;
			}
			
			type = parser.next();
		}
		
		return info;
	}

	/** install new apk*/
	private static void installApk(Activity context,File file) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
		context.startActivity(intent);
		context.finish() ;
	}
	
}
