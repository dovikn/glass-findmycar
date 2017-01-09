package com.google.glass.glassware.findmycar;

public enum GlasswareStatus {

	LOADING("loading"), 
	DISPLAYING_LIVECARD("displaying_livecard"),
	ERROR("error"),
	LAUNCHED_NAVIGATION("launched_navigation");

	private final String mStatus; 

	private GlasswareStatus(String status) {
		mStatus = status;
	}

	public boolean equalsName(String otherStatus){
		return (otherStatus == null)? false:mStatus.equals(otherStatus);
	}

	public String toString(){
		return mStatus;
	}
}
