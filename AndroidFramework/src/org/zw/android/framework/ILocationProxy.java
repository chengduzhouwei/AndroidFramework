package org.zw.android.framework;

import android.location.Address;
import android.location.Location;
import android.location.LocationManager;

/**
 * Location
 * 
 * @author zhouwei
 *
 */
public interface ILocationProxy {
	
	public interface LocationCallback {

		public void callback(Location location,Address address, String addText);
	}

	public LocationManager getLocationManager() ;
	
	public void startLocation(LocationCallback callback);
	
	public double getLatitude() ;
	
	public double getLongitude() ;
	
	public void removeAllListener() ;
}
