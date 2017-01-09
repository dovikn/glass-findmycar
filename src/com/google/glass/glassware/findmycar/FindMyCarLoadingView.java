package com.google.glass.glassware.findmycar;

import java.io.InputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class FindMyCarLoadingView extends FrameLayout {

	/**
	 * Interface to listen for changes during Loading.
	 */
	public interface Listener {

		/**
		 *  On Change
		 */
		public void OnChange();

		/**
		 *  On Finish
		 */
		public void onFinish(Bitmap downloadedBitmap, String addressLine);
	}

	public 	static    		String	 			TAG 				= StartFindMyCarActivity.TAG; 
	private static final 	long 				DELAY_MILLIS		= 41;
	private 				ImageView			mLoadingImageView	= null; 
	private					TextView			mMessageTextView	= null;
	private 				Listener 			mListener;
	private 				boolean 			mStarted;
	private 				Context 			mContext 			= null;
	private 				long 				mBaseMillis 		= 0;
	private 		final 	Handler				mHandler			= new Handler();
	public 					SharedPreferences 	mPrefs 				= null;
	private					Location			mLocation;
	private					Bitmap				mBitmap;
	private 				long				mStartDownloadTime	= 0;
	private 				long				mEndDownloadTime   	= 0;
	private					String 				mAddressline		= null;
	private 				boolean 			mLocationRetrieved  = false;
	private					boolean				mIsDownloading		= false;
	private 				FrameLayout			mFrameLayout		= null;
	private 			 	Runnable 			mUpdateTextRunnable = new Runnable() {

		@Override
		public void run() {
			if (mStarted) {
				updateView();
				postDelayed(mUpdateTextRunnable, DELAY_MILLIS);
			}
		}
	};

	public FindMyCarLoadingView(Context context, Location location, String addressline) {
		this(context, null, 0, location, addressline);
	}

	public FindMyCarLoadingView(Context context, AttributeSet attrs, Location location, String addressline) {
		this(context, attrs, 0, location, addressline);
	}
	
	public FindMyCarLoadingView(Context context, AttributeSet attrs) {
		this(context, attrs, 0, null, "");
	}
	
	public FindMyCarLoadingView(Context context, AttributeSet attrs, int style,  Location location, String addressline) {
		super(context, attrs, style);
		mContext = context;
		mLocation = location;
		mAddressline = addressline;

		// Get Preferences.
		mPrefs = mContext.getSharedPreferences("findmycar-prefs", Context.MODE_PRIVATE);

		LayoutInflater.from(context).inflate(R.layout.find_my_car_launch, this);
		
		mFrameLayout = (FrameLayout) findViewById(R.id.launchFrameLayout); 

		mLoadingImageView = (ImageView) findViewById(R.id.findMyCarLogo);

		mMessageTextView = (TextView) findViewById(R.id.launchPinTextView);
		mMessageTextView.setVisibility(View.VISIBLE);
	
		setBaseMillis(SystemClock.elapsedRealtime());
	}

	/**
	 * Sets a {@link Listener}.
	 */
	public void setListener(Listener listener) {
		mListener = listener;
	}

	/**
	 * Returns the set {@link Listener}.
	 */
	public Listener getListener() {
		return mListener;
	}

	public long getBaseMillis() {
		return mBaseMillis;
	}

	public void setBaseMillis(long mBaseMillis) {
		this.mBaseMillis = mBaseMillis;
	}

	public void start() {
		if (!mStarted) {
			postDelayed(mUpdateTextRunnable, DELAY_MILLIS);
		}
		mStarted = true;
	} 

	public void restart() {
		mIsDownloading = false;
		setBaseMillis(SystemClock.elapsedRealtime());
		if (!mStarted) {
			postDelayed(mUpdateTextRunnable, DELAY_MILLIS);
		}
		mStarted = true;
	} 
	
	public void stop() {
		if (mStarted) {
			removeCallbacks(mUpdateTextRunnable);
		}
		mStarted = false;
	}

	@Override
	public boolean postDelayed(Runnable action, long delayMillis) {
		return mHandler.postDelayed(action, delayMillis);
	}

	/**
	 * Updates the view to reflect the current state of animation, visible for testing.
	 **/
	private void updateView() {
		
		long millis = SystemClock.elapsedRealtime() - mBaseMillis;

		
		if (millis < 1000) {
			//Log.e(TAG, "[DEBUG] update view render BLACK");
			mFrameLayout.setBackgroundColor(Color.BLACK);
			if (mLoadingImageView.getVisibility() == View.GONE) {
				mLoadingImageView.setVisibility(View.VISIBLE);
			}
		} else {
		
			// get Location: 
			validateLocationAndAddress();

			// Download Map
			downloadMap();
		}
		mListener.OnChange();
	}

	private void downloadMap() {
		if (!mIsDownloading) {
			mIsDownloading = true;
			downloadAndDisplayBackground(mLocation);
		}
	}
	
	public void setIsDownloading (boolean isDownloading) {
		mIsDownloading = isDownloading;
	}

	/**
	 * Check is location available 
	 **/
	private void validateLocationAndAddress() {

		if (!mLocationRetrieved) {

			mLocationRetrieved =true;
			if(mLocation == null) {
				Log.w(TAG, "[WARN] Location is unavailable!");
			}

			if (mAddressline == null) {
				Log.w(TAG, "[WARN] Specific address is unavailable!");
			}
			//     		THIS SECTION IS USED fOR TAKING SCREENSHOTS FOR THE GLASSWARE LISTING.	
			//			Location newLocation = new Location("");
			//			newLocation.setLatitude(Double.valueOf("37.807797").doubleValue());
			//			newLocation.setLongitude(Double.valueOf("-122.426531").doubleValue());
			//			Address a = locationManager.getLastKnownAddress(newLocation);
		}
	}

	/**
	 * Download and display a static map in the background. 
	 **/
	private void downloadAndDisplayBackground (Location location) {

		//validation
		if (null == location) {
			return;
		}

		// Use Glass style static maps.
		String staticMapURL = "https://maps.googleapis.com/maps/api/staticmap?"
				+ "sensor=true&"
				+ "style=element:geometry|invert_lightness:true&"
				+ "style=feature:landscape.natural.terrain|element:geometry|visibility:off&"
				+ "style=feature:landscape|element:geometry.fill|color:0x303030&"
				+ "style=feature:poi|element:geometry.fill|color:0x303030&"
				+ "style=feature:poi.park|element:geometry.fill|color:0x0a330a&"
				+ "style=feature:water|element:geometry|color:0x00003a&"
				+ "style=feature:transit|element:geometry|visibility:off&"
				+ "style=feature:administrative|element:geometry|visibility:off&"
				+ "style=feature:administrative.country|element:geometry.stroke|visibility:on|color:0x101010&"
				+ "style=feature:administrative.province|element:geometry.stroke|visibility:on|color:0x181818&"
				+ "style=feature:road|element:geometry.stroke|visibility:off&"
				+ "style=feature:road.local|element:geometry.fill|color:0x606060&"
				+ "style=feature:road.arterial|element:geometry.fill|color:0x999999&"
				+ "size=640x360&"
				+ "zoom=17&"
				+ "scale=1&"
				+ "center="+ location.getLatitude() + "," + location.getLongitude() + "&" 
				+ "markers=size:large%7Ccolor:blue%7Clabel:S%7C" + location.getLatitude() + "," + location.getLongitude();
		
		
// 		THIS SECTION IS USED fOR TAKING SCREENSHOTS FOR THE GLASSWARE LISTING.		
//		String staticMapURL = "https://maps.googleapis.com/maps/api/staticmap?"
//				+ "sensor=true&"
//				+ "style=element:all|label.text|weight:1|visibility:on|invert_lightness:true&"
//				+ "style=element:geometry|invert_lightness:true&"
//				+ "style=feature:landscape.natural.terrain|element:geometry|visibility:off&"
//				+ "style=feature:landscape|element:geometry.fill|color:0x303030&"
//				+ "style=feature:poi|element:geometry.fill|color:0x303030&"
//				+ "style=feature:poi.park|element:geometry.fill|color:0x0a330a&"
//				+ "style=feature:water|element:geometry|color:0x00003a&"
//				+ "style=feature:transit|element:geometry|visibility:off&"
//				+ "style=feature:administrative|element:geometry|visibility:off&"
//				+ "style=feature:administrative.country|element:geometry.stroke|visibility:on|color:0x101010&"
//				+ "style=feature:administrative.province|element:geometry.stroke|visibility:on|color:0x181818&"
//				+ "style=feature:road|element:geometry.stroke|visibility:off&"
//				+ "style=feature:road.local|element:geometry.fill|color:0x606060&"
//				+ "style=feature:road.arterial|element:geometry.fill|color:0x999999&"
//				+ "size=640x360&"
//				+ "zoom=17&"
//				+ "scale=1&"
//				+ "center=37.807797,-122.426531&" 
//				+ "markers=size:large%7Ccolor:blue%7Clabel:S%7C"+"37.807797,-122.426531";

//		THIS CODE IS BACKUP.
//		// Use Glass style static maps.
//		String staticMapURL = "https://maps.googleapis.com/maps/api/staticmap?sensor=true&scale=2&style=element:geometry|invert_lightness:true&style=feature:landscape.natural.terrain|element:geometry|visibility:off&style=feature:landscape|element:geometry.fill|color:0x303030&style=feature:poi|element:geometry.fill|color:0x303030&style=feature:poi.park|element:geometry.fill|color:0x0a330a&style=feature:water|element:geometry|color:0x00003a&style=feature:transit|element:geometry|visibility:off&style=feature:administrative|element:geometry|visibility:off&style=feature:administrative.country|element:geometry.stroke|visibility:on|color:0x101010&style=feature:administrative.province|element:geometry.stroke|visibility:on|color:0x181818&style=feature:road|element:geometry.stroke|visibility:off&style=feature:road.local|element:geometry.fill|color:0x606060&style=feature:road.arterial|element:geometry.fill|color:0x999999&size=640x360&zoom=17&center=" +location.getLatitude()+","+location.getLongitude() + 
//				"&markers=size:large%7Ccolor:red%7Clabel:S%7C"+location.getLatitude()+","+location.getLongitude();

		
		// download and show The Image
		new DownloadImageTask(mBitmap).execute(staticMapURL);	
	}

	/**
	 * Inner class to download the static image
	 */
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

		public DownloadImageTask(Bitmap bm) {
			mStartDownloadTime = System.currentTimeMillis();
			mBitmap = bm;
		}

		protected Bitmap doInBackground(String... urls) {
			Log.i(TAG, "[INFO] Downloading map...");
			String urldisplay = urls[0];
			Bitmap bitmap = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				bitmap = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				PrefsUtils.setLastRunFailed(mPrefs, true);
				return null;
			}
			return bitmap;
		}

		protected void onPostExecute(Bitmap result) {
			mBitmap = result;	
			mEndDownloadTime = System.currentTimeMillis();
			double  diff = ((mEndDownloadTime-mStartDownloadTime)/1000);
			Log.i(TAG, "[INFO] Map downloaded. ( Download Time: " + diff + " seconds)");

			if (null != mBitmap && null != mAddressline) {
				PrefsUtils.setGlasswareStatus(mPrefs, GlasswareStatus.DISPLAYING_LIVECARD.toString());
				mListener.onFinish(mBitmap, mAddressline);
			}
		}
	}
}
