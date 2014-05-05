package org.zw.android.framework.log;

import android.util.Log;

/**
 * @author zhouwei
 *
 */
public final class Debug {

	/** tag size*/
	static final int TAG_SIZE			= 30 ;
	public static boolean 	debug		= true ;
	
	/**
	 * @param Tag
	 * @param msg
	 */
	public static void d(String tag,String msg){
		if(debug){
			Log.d(tag, "zhouwei >> " + msg) ;
		}
	}
	
	/**
	 * @param Tag
	 * @param msg
	 */
	public static void e(String tag,String msg){
		if(debug){
			Log.e(tag, "zhouwei >> " + msg);
		}
	}
	
	/**
	 * @param Tag
	 * @param msg
	 */
	public static void w(String tag,String msg){
		if(debug){
			Log.w(tag, "zhouwei >> " + msg);
		}
	}
	
	/**
	 * @param Tag
	 * @param msg
	 */
	public static void i(String tag,String msg){
		if(debug){
			Log.i(tag, "zhouwei >> " + msg);
		}
	}
}
