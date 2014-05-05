package org.zw.android.framework.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

/**
 * NETWORK_TYPE_CDMA 网络类型为CDMA 
 * NETWORK_TYPE_EDGE 网络类型为EDGE 
 * NETWORK_TYPE_EVDO_0 网络类型为EVDO0 
 * NETWORK_TYPE_EVDO_A 网络类型为EVDOA 
 * NETWORK_TYPE_GPRS 网络类型为GPRS
 * NETWORK_TYPE_HSDPA 网络类型为HSDPA 
 * NETWORK_TYPE_HSPA 网络类型为HSPA 
 * NETWORK_TYPE_HSUPA 网络类型为HSUPA 
 * NETWORK_TYPE_UMTS 网络类型为UMTS
 * 
 * 移动和联通的2G为GPRS或EDGE，电信的2G为CDMA，
 * 
 * 联通的3G为UMTS或HSDPA，
 * 电信 的3G为EVDO
 * 
 * @author mac
 * 
 */
public final class NetworkUtil {
	
	public static final String DEFAULT_WIFI_ADDRESS 			= "00-00-00-00-00-00";
	public static final String WIFI 							= "Wi-Fi";
	public static final String TWO_OR_THREE_G 					= "2G/3G";
	public static final String UNKNOWN 							= "Unknown";
	
	private NetworkUtil(){
		
	}

	public static boolean isNetworkConnected(Context context){
		
		if(context == null){
			return false ;
		}
		
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}
	
	public static boolean isWifi(Context context){
		
		if(context == null){
			return false ;
		}
		
		ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		 
		NetworkInfo info = connectMgr.getActiveNetworkInfo();
		 
		if(info != null && info.getType() == ConnectivityManager.TYPE_WIFI){
			return true ;
		}
		
		return false ;
	}
	
	public static boolean is2G(Context context){
		
		if(context == null){
			return false ;
		}
		
		ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		 
		NetworkInfo info = connectMgr.getActiveNetworkInfo();
		
		int type = info != null ? info.getSubtype() : -10 ;
		
		return (type == TelephonyManager.NETWORK_TYPE_GPRS) // 移动
				|| (type == TelephonyManager.NETWORK_TYPE_EDGE) // 联通
				|| (type == TelephonyManager.NETWORK_TYPE_CDMA); // 电信
	}
	
	public static boolean is3G(Context context){
		
		if(context == null){
			return false ;
		}
		
		ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		 
		State info = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		
		String str = info != null ? info.toString() : null ;
		
		return str != null ? str.equals("") : false ;
	}
	
	public static String convertIntToIp(int paramInt) {
		
		return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "."
				+ (0xFF & paramInt >> 16) + "." + (0xFF & paramInt >> 24);
	}

	/***
	 获取当前网络类型
	 * 
	 * @param pContext
	 * @return type[0] WIFI , TWO_OR_THREE_G , UNKNOWN type[0] SubtypeName
	 */
	public static String[] getNetworkState(Context context) {
		
		if(context == null){
			return null ;
		}
		
		String[] type = new String[2];
		type[0] = "Unknown";
		type[1] = "Unknown";
		
		if (context.getPackageManager().checkPermission(
				"android.permission.ACCESS_NETWORK_STATE",
				context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
			ConnectivityManager localConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (localConnectivityManager == null)
				return type;

			NetworkInfo localNetworkInfo1 = localConnectivityManager
					.getNetworkInfo(1);
			if ((localNetworkInfo1 != null)
					&& (localNetworkInfo1.getState() == NetworkInfo.State.CONNECTED)) {
				type[0] = "Wi-Fi";
				type[1] = localNetworkInfo1.getSubtypeName();
				return type;
			}
			NetworkInfo localNetworkInfo2 = localConnectivityManager
					.getNetworkInfo(0);
			if ((localNetworkInfo2 == null)
					|| (localNetworkInfo2.getState() != NetworkInfo.State.CONNECTED))
				type[0] = "2G/3G";
			type[1] = localNetworkInfo2.getSubtypeName();
			return type;
		}
		return type;
	}

	/***
	 *获取wifi 地址
	 * 
	 * @param pContext
	 * @return
	 */

	public static String getWifiAddress(Context context) {
		
		String address 			= DEFAULT_WIFI_ADDRESS;
		WifiManager wifim 		= getWifiManager(context);
		
		if (wifim != null) {
			WifiInfo localWifiInfo = wifim.getConnectionInfo();
			
			if (localWifiInfo != null) {
				address = localWifiInfo.getMacAddress();
				if (address == null || address.trim().equals(""))
					address = DEFAULT_WIFI_ADDRESS;
				return address;
			}

		}
		
		return DEFAULT_WIFI_ADDRESS;
	}

	/***
	 *获取wifi ip地址
	 * 
	 * @param pContext
	 * @return
	 */
	public static String getWifiIpAddress(Context context) {
		
		WifiInfo localWifiInfo 	= null;
		WifiManager wifim 		= getWifiManager(context);
		
		if (wifim != null) {
			
			localWifiInfo = wifim.getConnectionInfo();
			
			if (localWifiInfo != null) {
				String str = convertIntToIp(localWifiInfo.getIpAddress());
				return str;
			}
		}
		
		return "";
	}

	/**
	 * 获取WifiManager
	 * 
	 * @param pContext
	 * @return
	 */
	public static WifiManager getWifiManager(Context context) {
		
		if(context == null){
			return null ;
		}
		
		return (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	}

	/**
	 * 网络可用
	 * android:name="android.permission.ACCESS_NETWORK_STATE"/>
	 * 
	 * @param ctx
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		
		if(context == null){
			return false ;
		}
	
		ConnectivityManager cm = (ConnectivityManager) 	context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		
		return (info != null && info.isConnected());
	}
}
