package org.zw.android.framework.log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;

/**
 * 
 * @author zhouwei
 *
 */
public final class DebugLogService extends Service {
	
	private static final String DateStringFormat = "yyyy-MM-dd";

	private static ScheduledExecutorService logcatfileService = null;
	
	private long enter ;
	
	private static final int 	TIMER				= 30 * 1000 ;
	
	private static DebugLogService mInstance ;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	/**
	 * @param context
	 */
	public static final void startLogService(Context context){
		if(context == null) return ;
		Intent service = new Intent();
        service.setClass(context, DebugLogService.class);
        context.startService(service);
	}

	/**
	 * @param context
	 */
	public static final void stopLogService(){
		//
		shutdown();
		//
		if(mInstance != null) mInstance.stopSelf();
		mInstance = null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		try{
			// clear
			Runtime.getRuntime().exec("logcat -c");
		}catch(Exception e){
			e.printStackTrace();
		}
		//
		deleteHistoryLogFiles();
		//
		enter = System.currentTimeMillis() ;
		//
		logcatfileService = Executors.newSingleThreadScheduledExecutor();
		logcatfileService.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				System.out.println("Time difference = " + ((System.currentTimeMillis() - enter) / 1000));
				System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				System.out.println("+++++++++++++++++++++++++    log service   +++++++++++++++++++++++++");
				System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				writeLogFile();
				enter = System.currentTimeMillis();
			}
		}, TIMER, TIMER, TimeUnit.MILLISECONDS);
		//
		mInstance = this ;
		//
		return START_STICKY;
	}

	/**
	 * 
	 */
	private static void shutdown(){
		
		if (logcatfileService != null) {
			if (!logcatfileService.isShutdown()) {
				logcatfileService.shutdown();
				logcatfileService = null;
			}
		}
		
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("+++++++++++++++++++++++++    stop service   +++++++++++++++++++++++++");
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	}
	
	private final void deleteHistoryLogFiles() {
		String log_file_path = getLogFilePath();
		if (log_file_path == null)
			return;
		File dir = new File(log_file_path);
		File[] folders = dir.listFiles();
		if (folders == null)
			return;
		// delete legacy log
		long time = System.currentTimeMillis();
		Date date = new Date(time);
		SimpleDateFormat FormatedDate = new SimpleDateFormat(DateStringFormat);
		String cur_log_floder = FormatedDate.format(date);
		// 
		for (int i = 0; i < folders.length; i++) {
			String name = folders[i].getName() ;
			if(!name.equals(cur_log_floder)){
				// 
				if(folders[i].isDirectory()){
					//
					File[] sub = folders[i].listFiles();
					for(int index = 0 ; index < sub.length ; index++){
						sub[index].delete();
					}
				}
				//
				folders[i].delete();
			}
		}
	}

	/**
	 * logcat -v time System.out:v MyTag:v *:S adb logcat -v time
	 * *:error
	 */
	private final void writeLogFile() {
		try {
			//
			String log_file_path = getLogFilePath();
			if (log_file_path == null)
				return;
			long time = System.currentTimeMillis();
			Date date = new Date(time);
			SimpleDateFormat FormatedDate = new SimpleDateFormat(DateStringFormat);
			String parent_path = log_file_path + FormatedDate.format(date);
			//
			File file = new File(parent_path);
			if (!file.exists())
				file.mkdirs();
			//
			String cur_log_file_project = parent_path + "/log-" + FormatedDate.format(date) + "-normal";
			String cur_log_file_all 	= parent_path + "/log-" + FormatedDate.format(date) + "-all";
			//
			String command = "logcat -v time -f " + cur_log_file_project + " " + " Framework " +":v MyTag:v *:S";
			Runtime.getRuntime().exec(command);
			//
			command = "logcat -v time -f " + cur_log_file_all;
			Runtime.getRuntime().exec(command);
			//
			Runtime.getRuntime().exec("logcat -c");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * 
	 * @return
	 */
	private final String getLogFilePath() {
		if (!Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			return null;
		String sdcard_path = Environment.getExternalStorageDirectory().getPath();
		String app_name = getApplicationContext().getPackageName();
		return sdcard_path + "/" + app_name + "/";
	}
}
