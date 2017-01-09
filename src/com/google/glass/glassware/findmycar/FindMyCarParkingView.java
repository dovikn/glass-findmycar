package com.google.glass.glassware.findmycar;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * View used to display draw a the parking view.
 **/
public class FindMyCarParkingView extends FrameLayout {
	
	private static final int LEFT_MARGIN_IN_PX = 40;
	private static final int PARKING_LOCATION_BOTTOM_MARGIN_IN_PX = 95;
	private static final int PARKING_LOCATION_DETAILS_BOTTOM_MARGIN_IN_PX = 40;
	/**
	 * Interface to listen for changes on the view layout.
	 */
	public interface Listener {
		public void onChange();
	}

	static final 			long 					DELAY_MILLIS				= 41;
	public static    		String	 				TAG 						= "findmycarforglass";
	public 					SharedPreferences 		mPrefs 						= null;
	private 				ImageView				mImageView					= null;	
	private 				ImageView				mBottomRightLogo			= null;	
	private 				TextView				mParkingLocation			= null;
	private					TextView				mParkingLocationDetails		= null;
	private					Bitmap					mBitmap;
	private					String 					mAddressline				= null;
	private 				boolean 				mRunning;
	private 				Listener 				mListener;
	private final 			Handler 				mHandler 					= new Handler();
	private final 			Runnable 				mUpdateViewRunnable 		= new Runnable() {

		@Override
		public void run() {
			if (mRunning) {
				updateView();
				postDelayed(mUpdateViewRunnable, DELAY_MILLIS);
			}
		}
	};
	
	public FindMyCarParkingView(Context context) {
		this(context, null, 0);
	}

	public FindMyCarParkingView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FindMyCarParkingView(Context context, AttributeSet attrs, int style) {
		super(context, attrs, style);
		LayoutInflater.from(context).inflate(R.layout.find_my_car_livecard, this);

		mImageView = (ImageView) findViewById(R.id.liveCardMapImageView);
		Bitmap staticMap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.staticmap);
		mImageView.setImageBitmap(staticMap);
		mImageView.setVisibility(View.VISIBLE);

		mBottomRightLogo = (ImageView) findViewById(R.id.liveCardBottomRightLogo);
		mBottomRightLogo.setVisibility(View.VISIBLE);

		mParkingLocation  = (TextView) findViewById(R.id.liveCardParkingLocationTextView);
		mParkingLocation.setTextSize(TypedValue.COMPLEX_UNIT_PX, 40);
		mParkingLocation.setTextColor(Color.WHITE);
		mParkingLocation.setVisibility(View.VISIBLE);

		LayoutParams paramsParkingLocation = (LayoutParams) mParkingLocation.getLayoutParams();
		paramsParkingLocation.setMargins(LEFT_MARGIN_IN_PX, 0, 0, PARKING_LOCATION_BOTTOM_MARGIN_IN_PX);

		mParkingLocationDetails  = (TextView) findViewById(R.id.liveCardParkingLocationDetailsTextView);
		mParkingLocationDetails.setText("this will be the address");
		mParkingLocationDetails.setVisibility(View.VISIBLE);

		LayoutParams paramsParkingLocationDetails = (LayoutParams) mParkingLocationDetails.getLayoutParams();
		paramsParkingLocationDetails.setMargins(LEFT_MARGIN_IN_PX, 0, 0, PARKING_LOCATION_DETAILS_BOTTOM_MARGIN_IN_PX);
		
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

	public Bitmap getBitmap() {
		return mBitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		mBitmap = bitmap;
	}

	public String getAddressline() {
		return mAddressline;
	}

	public void setAddressline(String mAddressline) {
		this.mAddressline = mAddressline;
	}

	public void start() {
		if (!mRunning) {
			postDelayed(mUpdateViewRunnable, DELAY_MILLIS);
		}
		mRunning = true;
	}
	
	public void stop() {
		if (mRunning) {
			removeCallbacks(mUpdateViewRunnable);
		}
		mRunning = false;
	}

	@Override
	public boolean postDelayed(Runnable action, long delayMillis) {
		return mHandler.postDelayed(action, delayMillis);
	}

	@Override
	public boolean removeCallbacks(Runnable action) {
		mHandler.removeCallbacks(action);
		return true;
	}

	void updateView() {
		if (mBitmap == null) {
			Log.w(TAG, "[WARN] Bitmap is null");
		}
		if (mAddressline == null) {
			Log.w(TAG, "[WARN] Addressline is null");
		}

		mImageView.setImageBitmap(mBitmap);
		mImageView.setVisibility(View.VISIBLE);
		mParkingLocationDetails.setText(mAddressline);
		mParkingLocationDetails.setTextSize(TypedValue.COMPLEX_UNIT_PX, 26);
		mParkingLocationDetails.setTextColor(Color.WHITE);
		mParkingLocationDetails.setVisibility(View.VISIBLE);
		if (mListener != null) {
			mListener.onChange();
		}
	}
		
	/**
	 * This method converts device specific pixels to density independent pixels.
	 **/
	public static int pxToDp(int px){
		return (int) (px / Resources.getSystem().getDisplayMetrics().density);
	}
	
	public static int dpToPx(int dp)
	{
	    return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
	}
}
