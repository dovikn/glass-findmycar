package com.google.glass.glassware.findmycar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;

import com.google.android.glass.timeline.DirectRenderingCallback;
import com.google.android.glass.timeline.LiveCard;


public class FindMyCarDrawer implements DirectRenderingCallback {

	public  static    		String	 				TAG 				= StartFindMyCarActivity.TAG; 
	private 	 			FindMyCarLoadingView	mLoadingView;
	private 	 			FindMyCarParkingView 	mParkingView;
	private 				SurfaceHolder 			mHolder;
	private 				boolean					mLoadingDone;
	private 				boolean 				mIsRenderingPaused 	= false;
	private	static			boolean 				mIsRefreshRequested	= false;

	private final FindMyCarLoadingView.Listener mLoadingListener = new FindMyCarLoadingView.Listener() {

		@Override
		public void OnChange() {
			if (mIsRenderingPaused) {
				return;
			}
			if (mHolder != null) {
				draw(mLoadingView);
			}
		}

		@Override
		public void onFinish(Bitmap downloadedBitmap, String addressLine) {
			mLoadingDone = true;
			mLoadingView.stop();
			mParkingView.setBitmap(downloadedBitmap);
			mParkingView.setAddressline(addressLine);
			mParkingView.start();
		}
	};

	private final FindMyCarParkingView.Listener mParkingListener = new FindMyCarParkingView.Listener() {

		@Override
		public void onChange() {
			if (mIsRenderingPaused) {
				return;
			}
	
			if (mIsRefreshRequested) {
				setIsRefreshRequested(false);
				mParkingView.stop();
				mLoadingDone = false;
				mLoadingView.restart();
				return;
			}
			
			
			if (mHolder != null) {
				draw(mParkingView);
			}
		}
	};

	public FindMyCarDrawer(Context context, Location location, String addressline) {
		this(new FindMyCarLoadingView(context, location, addressline),
				new FindMyCarParkingView(context));
	}

	public FindMyCarDrawer(FindMyCarLoadingView loadingView, FindMyCarParkingView parkingView) {
		mLoadingView = loadingView;
		mLoadingView.setListener(mLoadingListener);
		mParkingView = parkingView;
		mParkingView.setListener(mParkingListener);
	}

	/**
	 * Uses the provided {@code width} and {@code height} to measure and layout the inflated
	 * {@link CountDownView} and {@link ChronometerView}.
	 */
	 @Override
	 public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		 // Measure and layout the view with the canvas dimensions.
		 int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
		 int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);

		 mLoadingView.measure(measuredWidth, measuredHeight);
		 mLoadingView.layout(
				 0, 0, mLoadingView.getMeasuredWidth(), mLoadingView.getMeasuredHeight());

		 mParkingView.measure(measuredWidth, measuredHeight);
		 mParkingView.layout(
				 0, 0, mParkingView.getMeasuredWidth(), mParkingView.getMeasuredHeight());
		 
		 // Workaround: Once the user cancels navigation, We need the LiveCard to render
		 // itself again, this ensures that this will happen.
		 renderingPaused(null, true);
		 renderingPaused(null, false);
	 }

	 /**
	  * Keeps the created {@link SurfaceHolder} and updates this class' rendering state.
	  */
	 @Override
	 public void surfaceCreated(SurfaceHolder holder) {
		 // The creation of a new Surface implicitly resumes the rendering.
		 mHolder = holder;
		 updateRenderingState();
	 }

	 /**
	  * Removes the {@link SurfaceHolder} used for drawing and stops rendering.
	  */
	 @Override
	 public void surfaceDestroyed(SurfaceHolder holder) {
		 mHolder = null;
		 updateRenderingState();
	 }

	 /**
	  * Updates this class' rendering state according to the provided {@code paused} flag.
	  */
	 @Override
	 public void renderingPaused(SurfaceHolder holder, boolean paused) {
		 mIsRenderingPaused = paused;
		 updateRenderingState();
	 }

	 /**
	  * Starts or stops rendering according to the {@link LiveCard}'s state.
	  */
	 private void updateRenderingState() {
		 if (mHolder != null) {
			 if (!mLoadingDone) {
				 mLoadingView.start();
			 } else {
				 mParkingView.start();
			 }
		 } 
	 }

	 /**
	  * Draws the view in the SurfaceHolder's canvas.
	  */
	 private void draw(View view) {
		 Canvas canvas;
		 try {
			 canvas = mHolder.lockCanvas();
		 } catch (Exception e) {
			 Log.e(TAG, "Unable to lock canvas: " + e);
			 return;
		 }
		 if (canvas != null) {
			 view.draw(canvas);
			 mHolder.unlockCanvasAndPost(canvas);
		 }
	 }
	 
	 public void stopDrawing () {

		 if (mLoadingView != null) {
			 mLoadingView.stop();
		 }

		 if (mParkingView != null) {
			 mParkingView.stop();
		 }
	 }
	 
	 public boolean isRefreshRequested() {
		 return mIsRefreshRequested;
	 }

	 public void setIsRefreshRequested(boolean mIsRefresh) {
		 mIsRefreshRequested = mIsRefresh;
	 }
	 
	 public void clearCanvas() {
		 Canvas canvas;
		 try {
			 canvas = mHolder.lockCanvas();
		 } catch (Exception e) {
			 Log.e(TAG, "Unable to lock canvas: " + e);
			 return;
		 }
		 if (canvas != null) {
			 canvas.drawColor(Color.BLACK);
			 mHolder.unlockCanvasAndPost(canvas);
		 }
	 }
}
