package com.google.glass.glassware.findmycar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.touchpad.GestureDetector.BaseListener;
import com.google.android.glass.media.Sounds;


public class LaunchNavigationActivity extends Activity  implements BaseListener{

	public 					SharedPreferences 		mPrefs 			= null;
	public 		static		String	 				TAG 			= StartFindMyCarActivity.TAG;
	private 				GlassSliderView			mSliderView		= null;
	private 				GestureDetector 		mGestureDetector;
	private 				AudioManager			mAudioManager 	= null;
	private 				boolean 				mIsCancelled	= false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.find_my_car_launch);
		setContentView(new GlassTuggableView(this,R.layout.find_my_car_launch));

		// Initiate the Gesture Detector.
		mGestureDetector = new GestureDetector(this);
		mGestureDetector.setBaseListener(this);
		
		// Indeterminate Progress Bar.
		mSliderView = (GlassSliderView) findViewById(R.id.slider);
		mSliderView.setVisibility(View.VISIBLE);
		mSliderView.startIndeterminate();

		// Set Preferences.
		mPrefs = getSharedPreferences("findmycar-prefs", Context.MODE_PRIVATE);
		PrefsUtils.setGlasswareStatus(mPrefs, GlasswareStatus.LAUNCHED_NAVIGATION.toString());
			
		// Initiate the audio manager.
		mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		
		// wait 2.5 seconds 
		Thread thread = new Thread() {

			public void run() {
				int timer = 0;
				try {
					while (timer < 1750) {
						sleep(250);
						timer = timer + 250;
					}

					// Start the navigation sequence.
					startingSequence(mPrefs);
				} catch (Exception e) {
					Log.e(TAG, "[ERROR] Exception thrown while launching navigation: " + e.getMessage());
				} finally {
					finish();
				}
			}
		};
		thread.start();

		// Keep Glass Awake.
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	@Override
	public void onBackPressed() {
		mIsCancelled = true;
		finish();
		super.onBackPressed();
		return;
	}

	/**
	 * Starting sequence
	 */
	private void startingSequence(SharedPreferences prefs) {

		// In case there isn't a pinned location, redirect the user to the pin flow.
		boolean isLocationStored = PrefsUtils.isLocationStoredAlready(mPrefs); 
		String[] longlat = PrefsUtils.getLongtitudeLatitude(prefs);

		if (!isLocationStored || (null == longlat)) {
			startActivity(new Intent(this, StartFindMyCarActivity.class));
			return;
		}


		if (mIsCancelled) {
			finish();
			return;
		}
		
		// Go to Navigation.
		Log.i(TAG, "[INFO] Navigating to longtitude: " + longlat[0] + ", latitude: " + longlat[1]);
		goToNavigation(longlat[0], longlat[1]);
		return;
	}

	/**
	 * Navigate to the target location.
	 */
	private void goToNavigation(String targetLongtitude, String  targetLatitude ) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" +targetLatitude+","+targetLongtitude)); 
		startActivity(intent);
	}

	@Override
	public boolean onGesture(Gesture gesture) {
		if (gesture == Gesture.TAP) {
			Log.w(TAG, "[WARN] User tapped while launching navigation. Playing the Disallowed sound.");
			//Play the tap sound.
			mAudioManager.playSoundEffect(Sounds.DISALLOWED);
			return true;
		} else if (gesture == Gesture.TWO_TAP) {
			// do something on two finger tap
			Log.w(TAG, "[WARN] User tapped with two fingers while launching navigation. Playing the Disallowed sound.");
			mAudioManager.playSoundEffect(Sounds.DISALLOWED);
			return true;			
		} else if (gesture == Gesture.TWO_SWIPE_RIGHT) {
			// do something on right (forward) swipe
			Log.w(TAG, "[WARN] User Swiped forward using two fingers while launching navigation. Playing the Disallowed sound.");
			mAudioManager.playSoundEffect(Sounds.DISALLOWED);
			return true;
		} else if (gesture == Gesture.TWO_SWIPE_LEFT) {
			// do something on left (backwards) swipe
			Log.i(TAG, "[WARN] User Swiped back using two fingers while launching navigation. Playing the Disallowed sound.");
			mAudioManager.playSoundEffect(Sounds.DISALLOWED);
			return true;
		} else if (gesture == Gesture.SWIPE_DOWN) {
			// do something on left (backwards) swipe
			Log.i(TAG, "[WARN] User Swiped down.");
			onBackPressed();
			return true;
		}
		return false;
	}
	
	@Override
	public boolean onGenericMotionEvent(MotionEvent event) {

		if (mGestureDetector != null) {
			return mGestureDetector.onMotionEvent(event);
		}
		return false;
	}
}