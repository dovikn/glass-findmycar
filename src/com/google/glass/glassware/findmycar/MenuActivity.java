package com.google.glass.glassware.findmycar;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Location;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.timeline.LiveCard;

/**
 * Menu Activity to manage the Options Menu.
 */
public class MenuActivity extends Activity {

	public 	static 	String 				TAG = StartFindMyCarActivity.TAG;
	private 		LiveCard 			mLiveCard;
	private 		SharedPreferences 	mPrefs;
	private 		Handler 			mHandler 				= null;
	private 		Context				mContext 				= null;
	private 		AudioManager		mAudioManager 			= null;
	private 		String 				mGlasswareStatus 		= null;
	private 		boolean 			mIsNetworkConnected		= false;
	private 		boolean 			mIsLocationAvailable 	= false;
	private 		Location			mLocation				= null;
	private			String				mAddressline			= null; 

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

		// Save context for use in internal classes.
		mContext = this;

		// Preferences.
		mPrefs = getSharedPreferences("findmycar-prefs", Context.MODE_PRIVATE);

		// prevent Glass from Sleeping.
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}


	@Override
	public void onAttachedToWindow() {
		mGlasswareStatus = PrefsUtils.getGlasswareStatus(mPrefs);
		if (mGlasswareStatus != null &&mGlasswareStatus.equals(GlasswareStatus.LOADING.toString())) {
			Log.w(TAG,"[WARN] Glassware is Loading, Opening the Menu is Disallowed.");
			mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
			mAudioManager.playSoundEffect(Sounds.DISALLOWED);
			finish();
			return;
		}
		super.onAttachedToWindow();
		openOptionsMenu();
	}

	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.findmycarmenu, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		// Show get directions, if there was no error in retrieving location.
		boolean lastRunFailed = PrefsUtils.didLastRunFail(mPrefs);
		menu.findItem(R.id.op_getdirections).setVisible(!lastRunFailed);
		menu.findItem(R.id.op_overidelocation).setVisible(!lastRunFailed);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.op_getdirections:
			Log.i(TAG, "[INFO] User selected to get directions.");
			Intent intent = new Intent(mContext, LaunchNavigationActivity.class);
			intent.addFlags( 
					Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
			finish();
			return true;
		case R.id.op_dismiss:
			Log.i(TAG, "[INFO] User dismissed Find My Car.");
			PrefsUtils.resetSavedLocationAndAddress(mPrefs);
			stopDrawing();
			unpublishCard(mContext);
			exitGlassware();
			return true;
		case R.id.op_overidelocation:
			Log.i(TAG, "[INFO] User selected to refresh location.");
			clearCanvas();
			refresh();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}

	@Override
	public void openOptionsMenu() {
		super.openOptionsMenu();
	}
	
	@Override
	public void onOptionsMenuClosed(Menu menu){
		super.onOptionsMenuClosed(menu);
		finish();
		// Changing the Activity's exit animation to fade_out.
		overridePendingTransition(0, android.R.anim.fade_out);
	}
	
	/**
	 * Un-publish LiveCard.
	 **/
	private void unpublishCard(Context context) {
		mLiveCard = StartFindMyCarActivity.getLiveCard();
		if (mLiveCard != null && mLiveCard.isPublished()) {
			Log.i(TAG, "[INFO] Unpublishing LiveCard");
			mLiveCard.unpublish();
			mLiveCard = null;
			StartFindMyCarActivity.setLiveCard(null);
		} else {
			Log.d(TAG, "[DEBUG] LiveCard is null or Unpublished");
		}
	}

	private void stopDrawing() {
		StartFindMyCarActivity.stopDrawing();
	}
	
	private void setIsRefreshRequested(boolean refreshRequested) {
		StartFindMyCarActivity.setIsRefreshRequested(refreshRequested);
	}
	
	private void clearCanvas() {
		StartFindMyCarActivity.clearCanvas();
	}

	/**
	 * Exit Glassware
	 */
	private void exitGlassware() {
		Intent homeIntent = new Intent(Intent.ACTION_MAIN);
		homeIntent.addCategory(Intent.CATEGORY_HOME);
		homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(homeIntent);
	}

	protected void post(Runnable runnable) {
		mHandler.post(runnable);
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
		
	private boolean refresh() {
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
			return false;
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
			return false;
		}
		
		// Set GlasswareStatus to 'Loading'.
		PrefsUtils.setGlasswareStatus(mPrefs, GlasswareStatus.LOADING.toString());
		
		// Turn on the refresh flag.
		setIsRefreshRequested(true);
		return true;
	}
	
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	public boolean onKeyDown(int keycode, KeyEvent event) {
		if (keycode == KeyEvent.KEYCODE_BACK) {
			Log.d(TAG, "[DEBUG] onKeyDown");
			onBackPressed();
			return true;
		}
		return false;
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}
