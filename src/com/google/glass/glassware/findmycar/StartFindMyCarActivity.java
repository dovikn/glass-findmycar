package com.google.glass.glassware.findmycar;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.LiveCard.PublishMode;


public class StartFindMyCarActivity extends Activity {

	public  static final String 				LIVE_CARD_TAG			    = "findmycarforglass";
	public  static    	 String	 				TAG 						= "findmycarforglass";
	public 	     		 SharedPreferences 		mPrefs 						= null;
	public  static 		 LiveCard 		    	mLiveCard;
	private static		 FindMyCarDrawer 		mDrawer						= null;
	private static		 boolean 				mIsNetworkConnected			= false;
	private static		 boolean 				mIsLocationAvailable 		= false;
	private 			 Location				mLocation					= null;
	private				 String					mAddressline				= null; 
	
	
	Thread exitThread = new Thread() {

		public void run() {
			int timer = 0;
			try {
				while (timer < 3000) {
					sleep(250);
					timer = timer + 250;
				}
				exitGlassware();
			} catch (Exception e) {
				Log.e(TAG, "[ERROR] Exception while exiting: " + e.getMessage());
			} finally {
				finish();
			}
		}
	};

	private final DialogInterface.OnClickListener mOnClickListener =
			new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int button) {

			if (!mIsNetworkConnected) {
				startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
				return;
			}

			if (!mIsLocationAvailable) {
				exitGlassware();
				return;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		// Announce.
		announce();
		
		// Set Preferences.
		mPrefs = getSharedPreferences("findmycar-prefs", Context.MODE_PRIVATE);
		PrefsUtils.initPreferences(mPrefs);

		// Check network connection
		mIsNetworkConnected = isNetworkConnected();
		if (!mIsNetworkConnected) {
			Log.e(TAG, "[ERROR] No Network Connection");
			
			// Open an alert dialog.
			new FindMyCarAlertDialog(this, 
					R.drawable.ic_cloud_sad_100,
					R.string.no_connectivity,
					R.string.no_connectivity_footnote,
					mOnClickListener).show();
			PrefsUtils.setGlasswareStatus(mPrefs, GlasswareStatus.ERROR.toString());
			return;
		}

		mIsLocationAvailable = isLocationAvailable();
		if(!mIsLocationAvailable) {
			Log.e(TAG, "[ERROR] Location is unavailable!");
			
			// Open an alert dialog.
			new FindMyCarAlertDialog(this, 
					R.drawable.ic_warning_100,
					R.string.locatation_unavailable,
					R.string.locatation_unavailable_footnote, 
					mOnClickListener).show();
			PrefsUtils.setGlasswareStatus(mPrefs, GlasswareStatus.ERROR.toString());
			return;
		}
		
		//Create a LiveCard
		publishLiveCard();
	
		// Keep Glass Awake.
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	
	/*
	 * Create a new LiveCard.
	 */
	private void publishLiveCard() {

		// Handling an extreme case (LiveCard exists, but unpublished) 
		if (mLiveCard != null && !mLiveCard.isPublished()) {
			setLiveCard(null);
		}

		if (mLiveCard == null) {

			mLiveCard = new LiveCard(this, LIVE_CARD_TAG);
			
			// Inflate a layout into a remote view
			mDrawer = new FindMyCarDrawer(this, mLocation, mAddressline);
			
			// Enable direct rendering.
			mLiveCard.setDirectRenderingEnabled(true);

			// Set callback
			mLiveCard.getSurfaceHolder().addCallback(mDrawer);

			Intent menuIntent = new Intent(this, MenuActivity.class);
			menuIntent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK |
					Intent.FLAG_ACTIVITY_CLEAR_TASK);
			mLiveCard.setAction(PendingIntent.getActivity(this, 0, menuIntent, 0));

			mLiveCard.publish(PublishMode.REVEAL);
			Log.i(TAG, "[INFO] Published the LiveCard.");
		} else {	
			// LiveCard already published
			Log.w(TAG, "[WARN] Card was published already.");
			// Navigate to the card 
			if (!mLiveCard.isPublished()) {
				mLiveCard.publish(PublishMode.REVEAL);
			} else {
				mLiveCard.navigate();
			}
			return;
		}
	}

	/**
	 * Announce Launch.
	 */
	private void announce() {
		Log.i(TAG, "*****************************");
		Log.i(TAG, "**  Launched Find My Car   **");
		Log.i(TAG, "*****************************");
	}

	public static LiveCard getLiveCard() {
		return mLiveCard;
	}
	public static void setLiveCard(LiveCard liveCard) {
		mLiveCard = liveCard;
	}

	public static void stopDrawing() {
		if (mDrawer != null) {
			mDrawer.stopDrawing();
		}
	}
	
	public static void clearCanvas() {
		if (mDrawer != null) {
			mDrawer.clearCanvas();
		}
	}
	
	public static void setIsRefreshRequested (boolean refreshRequested) {
		if (mDrawer != null) {
			mDrawer.setIsRefreshRequested(refreshRequested);
		}
	}

	/**
	 * Check if the network is connected. 
	 **/
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
			Log.e(TAG, "[ERROR] Failed to find network connectivity.");
			return false;
		} 
		Log.i(TAG, "[INFO] Glass is connected to the network.");
		return true;
	}

	/**
	 * Check is location available 
	 **/
	public boolean isLocationAvailable() {
		GlassLocationManager locationManager =  new GlassLocationManager(this);
		mLocation = locationManager.getLastKnownLocation();
		if(mLocation == null) {
			Log.w(TAG, "[WARN] Location is unavailable");
			return false;
		}

		Log.i(TAG, "[INFO] Location Identified: " + mLocation.getLongitude() + ", " + mLocation.getLatitude() + ".");

		// Get the address	
		Address a = locationManager.getLastKnownAddress(mLocation);
		if (a == null) {
			Log.w(TAG, "[WARN] Address is unavailable");
			return false;
		}
		mAddressline = (a == null)? "": a.getAddressLine(0);
		Log.i(TAG, "[INFO] Address Identified: " + mAddressline + ".");

		// Save Location & addressLine
		PrefsUtils.saveLocation(this.mPrefs, mLocation);
		PrefsUtils.saveAddress(mPrefs, mAddressline);
		return true;
	}

	/**
	 * Exit Glassware
	 */
	private void exitGlassware() {
		Intent homeIntent= new Intent(Intent.ACTION_MAIN);
		homeIntent.addCategory(Intent.CATEGORY_HOME);
		homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(homeIntent);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	public boolean onKeyDown(int keycode, KeyEvent event) {
		Log.i(TAG, "[INFO] StartActivity onKeyDown.");
		if (keycode == KeyEvent.KEYCODE_BACK) {
			Log.d(TAG, "[DEBUG] onKeyDown");
			onBackPressed();
			return true;
		}
		return false;
	}
	

	@Override
	public void onBackPressed() {
		Log.i(TAG, "[INFO] StartActivity OnBackPressed");
		super.onBackPressed();
	}
}
	
	