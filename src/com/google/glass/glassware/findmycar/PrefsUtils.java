package com.google.glass.glassware.findmycar;

import java.util.UUID;

import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;


public class PrefsUtils {

	private static 	String TAG 	= StartFindMyCarActivity.TAG;;

	/**
	 * Init Preferences.
	 **/
	public static boolean initPreferences (SharedPreferences prefs) {

		if (null == prefs) {
			Log.e(TAG, "[ERROR] SharedPreferences were not initialized properly.");
			return false;
		}

		// Check if init already took place
		String initialized = prefs.getString("FindMyCar.init", "");
		if ("true".equalsIgnoreCase(initialized)) {
			Log.i(TAG, "[INFO] Preferences are initialized.");
			return true;
		}

		// Unique user id: 
		UUID id = UUID.randomUUID();
		Log.d(TAG, "[DEBUG] User unique id is: " + String.valueOf(id));
		SharedPreferences.Editor editor = prefs.edit();

		/* User Id */
		editor.putString("FindMyCar.UserId", String.valueOf(id));		
		/* User Longtitude*/
		editor.putString("FindMyCar.LastKnown.Longtitude", "");
		/* User Latitude*/
		editor.putString("FindMyCar.LastKnown.Latitude", "");
		/* User Address*/
		editor.putString("FindMyCar.LastKnown.Address", "");
		/* Is initialized*/
		editor.putString("FindMyCar.init", "true");
		/* Is Error*/
		editor.putString("FindMyCar.error", "false");
		/* Glasswar status progress*/
		editor.putString("FindMyCar.status", GlasswareStatus.LOADING.toString());
		Log.i(TAG, "[STATUS] Glassware Status is LOADING.");
		editor.commit();
		return true;	
	}

	/**
	 * Saves Pinned Location.
	 **/
	public static boolean saveLocation(SharedPreferences prefs, Location location) {
		if (null == prefs) {
			Log.e(TAG, "[ERROR] SharedPreferences were not initialized properly.");
			return false;
		}

		if (null == location) {
			Log.e(TAG, "[ERROR] Failed to save current location (location is empty)."); 
			return false;
		}
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("FindMyCar.LastKnown.Longtitude", String.valueOf(location.getLongitude()));
		editor.putString("FindMyCar.LastKnown.Latitude", String.valueOf(location.getLatitude()));
		editor.commit();
		return true;	
	}

	/**
	 * Saves address.
	 **/
	public static boolean saveAddress(SharedPreferences prefs, String address) {
		if (null == prefs) {
			Log.e(TAG, "[ERROR] SharedPreferences were not initialized properly.");
			return false;
		}

		if (null == address) {
			Log.e(TAG, "[ERROR] Failed to save current address (address is empty)."); 
			return false;
		}
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("FindMyCar.LastKnown.Address", address);
		editor.commit();
		return true;	
	}

	/**
	 * Reset saved location. 
	 **/
	public static boolean resetSavedLocationAndAddress(SharedPreferences prefs) {

		if (null == prefs) {
			return false;
		}

		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("FindMyCar.LastKnown.Longtitude", "");
		editor.putString("FindMyCar.LastKnown.Latitude", "");
		editor.putString("FindMyCar.LastKnown.Address", "");
		editor.putString("FindMyCar.init", "false");
		editor.putString("FindMyCar.error", "false");
		editor.putString("FindMyCar.status", GlasswareStatus.LOADING.toString());

		editor.commit();
		return true;	
	}

	/**
	 * Checks whether a location was stored already. 
	 **/
	public static boolean isLocationStoredAlready(SharedPreferences prefs) {
		if (null == prefs) {
			Log.e(TAG, "[ERROR] SharedPreferences were not initialized properly.");
			return false;
		}

		String longtitude = prefs.getString("FindMyCar.LastKnown.Longtitude", "");
		if (null == longtitude || longtitude.equals("")) {
			return false;
		}
		return true;
	}


	/**
	 * Get Stored Longtitde/Latitude.
	 **/
	public static String[] getLongtitudeLatitude (SharedPreferences prefs) {
		if (null == prefs) {
			Log.e(TAG, "[ERROR] SharedPreferences were not initialized properly.");
			return null;
		}
		if (!isLocationStoredAlready(prefs)) {
			return null;
		}

		String[] longlat = new String[2];
		longlat[0] = prefs.getString("FindMyCar.LastKnown.Longtitude", "");
		longlat[1] = prefs.getString("FindMyCar.LastKnown.Latitude", "");

		return longlat;
	}

	/**
	 * Get Stored Address.
	 **/
	public static String getAddress (SharedPreferences prefs) {
		if (null == prefs) {
			Log.e(TAG, "[ERROR] SharedPreferences were not initialized properly.");
			return null;
		}
		if (!isLocationStoredAlready(prefs)) {
			return null;
		}
		return prefs.getString("FindMyCar.LastKnown.Address", "");
	}

	/**
	 * Did Last run fail
	 **/
	public static boolean didLastRunFail (SharedPreferences prefs) {
		if (null == prefs) {
			Log.e(TAG, "[ERROR] SharedPreferences were not initialized properly.");
			return false;
		}

		String isError = prefs.getString("FindMyCar.error", "");
		if ("true".equalsIgnoreCase(isError)) {
			return true;
		} 
		return false;
	}
	
	/**
	 * Set Last Run Failed
	 **/
	public static boolean setLastRunFailed(SharedPreferences prefs, boolean failed) {
		if (null == prefs) {
			Log.e(TAG, "[ERROR] SharedPreferences were not initialized properly.");
			return false;
		}

		String failedAsString = (failed? "true" : "false");
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("FindMyCar.error", failedAsString);
		editor.commit();
		return true;	
	}

	/**
	 * Get Glassware Status.
	 **/
	public static String getGlasswareStatus (SharedPreferences prefs) {
		if (null == prefs) {
			Log.e(TAG, "[ERROR] SharedPreferences were not initialized properly.");
			return "";
		}
		return prefs.getString("FindMyCar.status", "");
	}
	
	/**
	 * Set Glassware Status.
	 **/
	public static void setGlasswareStatus(SharedPreferences prefs, String status) {
		if (null == prefs) {
			Log.e(TAG, "[ERROR] SharedPreferences were not initialized properly.");
			return;
		}
		
		if (null == status || status.length() == 0) {
			return;
		}
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("FindMyCar.status", status);
		editor.commit();	
	}

	
	
	
	
	
	
	/**
	 * Log the Shared Preferences
	 **/
	public static void logSharedPreferences (SharedPreferences prefs ) {

		if (null == prefs) {
			Log.e(TAG, "[ERROR] SharedPreferences were not initialized properly.");
			return;
		}

		String initialized = prefs.getString("FindMyCar.init", "");
		Log.d(TAG, "[DEBUG] FindMyCar.init= "+ initialized );

		String error = prefs.getString("FindMyCar.error", "");
		Log.d(TAG, "[DEBUG] FindMyCar.error= "+ error );

		String longtitude = prefs.getString("FindMyCar.LastKnown.Longtitude", "");
		Log.d(TAG, "[DEBUG] FindMyCar.LastKnown.Longtitude= "+ longtitude );
		String latitude = prefs.getString("FindMyCar.LastKnown.Latitude", "");
		Log.d(TAG, "[DEBUG] FindMyCar.LastKnown.Latitude= "+ latitude );
		String address = prefs.getString("FindMyCar.LastKnown.Address", "");
		Log.d(TAG, "[DEBUG] FindMyCar.LastKnown.Latitude= "+  address);

	}
}
