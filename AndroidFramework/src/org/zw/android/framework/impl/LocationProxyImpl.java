package org.zw.android.framework.impl;

import java.util.List;

import org.zw.android.framework.ILocationProxy;
import org.zw.android.framework.log.Debug;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * 
 * @author zhouwei
 * 
 */
public final class LocationProxyImpl implements ILocationProxy {

	private static final String TAG = "LocationFactory" ;
	private static final int TWO_MINUTES = 1000 * 60 * 2;

	private Location 			mLastLocation ;
	private LocationListener 	mNetworkListener ;
	private LocationListener 	mGPSListener ;
	private LocationCallback	mCallback ;
	private Geocoder 			mGeocoder ;
	private Address				mAddress ;
	private String				mAddressText ;
	private Handler				mHandler ;
	private Context 			mContext ;

	protected LocationProxyImpl(Context context) {
		mGeocoder 			= new Geocoder(context);
		mContext			= context ;
		mHandler			= new Handler(){

			@Override
			public void handleMessage(Message msg) {
				switch(msg.what){
				case 1 :
					mCallback.callback(mLastLocation, mAddress, mAddressText);
					break ;
				}
			}
			
		} ;
	}
	
	/** get Location Manager*/
	public LocationManager getLocationManager(){
		return (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
	}

	/**
	 * get latitude
	 * @return
	 */
	public final double getLatitude(){
		return mLastLocation != null ? mLastLocation.getLatitude() : 0.00000000;
	}
	
	/**
	 * get longitude
	 * @return
	 */
	public final double getLongitude(){
		return mLastLocation != null ? mLastLocation.getLongitude() : 0.00000000;
	}

	/**
	 * start location
	 * 
	 * @param callback
	 */
	public void startLocation(LocationCallback callback) {
		
		// get LocationManager
		final LocationManager lm	= getLocationManager();
		
		// init callback
		mCallback	= callback ;
		// create network listener
		mNetworkListener = new LocationListener() {

			@Override
			public void onStatusChanged(String provider, int status,Bundle extras) {
				Debug.d(TAG, "Network Listener: onStatusChanged()");
			}

			@Override
			public void onProviderEnabled(String provider) {
				Debug.d(TAG, "Network Listener: onProviderEnabled()");
			}

			@Override
			public void onProviderDisabled(String provider) {
				Debug.d(TAG, "Network Listener: onProviderDisabled()");
			}

			@Override
			public void onLocationChanged(Location location) {
				
				// Called when a new location is found by the network location
				// provider.
				
				Debug.d(TAG, "Network Listener: onLocationChanged()");
				
				makeUseOfNewLocation(location);
				
				// remove network listener
				lm.removeUpdates(mNetworkListener);
				// 
				callback(location);
			}
		};

		// request network provider
		lm.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, mNetworkListener);
	}
	
	/**
	 * 
	 * @param location
	 */
	private void callback(Location location){
		
		// callback
		if(mCallback != null){
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try{
						List<Address> list = mGeocoder.getFromLocation(getLatitude(), getLongitude(), 1);
						for(Address add : list){
							mAddress = add ;
							mAddressText = add.getAddressLine(0) + add.getAddressLine(1) +  add.getAddressLine(2);
							break ;
						}
					}catch(Exception e){
						e.printStackTrace() ;
					}
					//
					mHandler.obtainMessage(1).sendToTarget() ;
				}
			}).start() ;
		}
	}
	
	/**
	 * remove all location listener
	 */
	public void removeAllListener(){
		
		// get LocationManager
		final LocationManager lm	= getLocationManager();
		
		if(lm != null){
			
			if(mNetworkListener != null) 
				lm.removeUpdates(mNetworkListener);
			
			if(mGPSListener != null) 
				lm.removeUpdates(mGPSListener);
			
			mNetworkListener 	= null ;
			mGPSListener 		= null ;
		}
	}
	
	/**
	 * 
	 * @param location
	 */
	private void makeUseOfNewLocation(Location location){
		
		if(location != null){
			mLastLocation	= location ;
		}
		
		// get LocationManager
		final LocationManager lm	= getLocationManager();
		
		// create GPS listener
		mGPSListener	= new LocationListener() {
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				Debug.d(TAG, "GPS Listener: onStatusChanged()");
			}
			
			@Override
			public void onProviderEnabled(String provider) {
				Debug.d(TAG, "GPS Listener: onProviderEnabled()");
			}
			
			@Override
			public void onProviderDisabled(String provider) {
				Debug.d(TAG, "GPS Listener: onProviderDisabled()");
			}
			
			@Override
			public void onLocationChanged(Location location) {
				
				// for debug
				Debug.d(TAG, "GPS Listener: onLocationChanged()");
				
				// is better location
				if(isBetterLocation(mLastLocation,location)){
					mLastLocation	= location ;
					
					// callback
					callback(location);
				}
				
				// remove listener
				lm.removeUpdates(mGPSListener);
			}
		};
		
		// request GPS provider
		lm.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 0, 0, mGPSListener);
	}

	/**
	 * Determines whether one Location reading is better than the current
	 * Location fix
	 * 
	 * @param location
	 *            The new Location that you want to evaluate
	 * @param currentBestLocation
	 *            The current Location fix, to which you want to compare the new
	 *            one
	 */
	private boolean isBetterLocation(Location location,Location currentBestLocation) {

		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}
}
