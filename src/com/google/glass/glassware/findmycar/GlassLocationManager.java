package com.google.glass.glassware.findmycar;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class GlassLocationManager extends Service implements LocationListener {

	private final Context mContext;
	
	private static String TAG = StartFindMyCarActivity.TAG;

	// flag for GPS status
	boolean isGPSEnabled = false;

	// flag for network status
	boolean isNetworkEnabled = false;

	boolean canGetLocation = false;

	Location location; // location
	double latitude; // latitude
	double longitude; // longitude

	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

	// Declaring a Location Manager
	protected LocationManager locationManager;

	public GlassLocationManager(Context context) {
		this.mContext = context;
	}

	public Location getLastKnownLocation() {
		try {
			locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
			if (null == locationManager) {
				Log.e(TAG, "[ERROR] Location Manager is null");
				return null;
			}
		
			// getting GPS status
			isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
				Log.w(TAG,"[ERROR] Failed to  find an enabled network provider.");
			} else {
				this.canGetLocation = true;
				
				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {
					Log.i(TAG, "[INFO] GPS is enabled.");
					if (location == null) {
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						if (locationManager != null) {
							location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null) {
								latitude = location.getLatitude();
								longitude = location.getLongitude();
							}
						}
					}
				}
				
				//  get location from Network Provider
				if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					if (locationManager != null) {
						location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}
				}
			}

		} catch (Exception e) {
			Log.e(TAG, "[ERROR] Exception thrown while getting the last known location: " + e.getMessage());
		}
		
		// Remove Location updates 
		locationManager.removeUpdates(this);
		return location;
	}
	/**
	 * Get the last known Address. 
	 **/
	public Address getLastKnownAddress(Location myLocation) {

		Geocoder 		geocoder;
		List<Address> 	addresses;
		geocoder = new Geocoder(mContext, Locale.getDefault());

		try {
			addresses = geocoder.getFromLocation(myLocation.getLatitude(), myLocation.getLongitude(), 1);
		} catch (IOException e) {
			Log.w(TAG, "[WARNING] Failed to get address from location due to IOException: " + e.getMessage());
			return null;
		}
		//Log.i(TAG, "[INFO] Address: " + addresses.get(0).getAddressLine(0) + " " +  addresses.get(0).getAddressLine(1) + " " +  addresses.get(0).getAddressLine(2));
		return addresses.get(0);	
	}
	
	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}


	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

	}



}
