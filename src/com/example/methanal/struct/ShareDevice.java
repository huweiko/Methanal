package com.example.methanal.struct;


import android.graphics.Bitmap;

public class ShareDevice extends Device{
	private String mCommentText;
	private String mLatitude;
	private String mLongitude;
	private String mPicture;
	
	public ShareDevice(String mType, String mTime, String mName, String mValue,String mCommentText,String mLatitude,String mLongitude,String mPicture) {
		super(mType, mTime, mName, mValue);
		// TODO Auto-generated constructor stub
		this.mCommentText = mCommentText;
		this.mLatitude = mLatitude;
		this.mLongitude = mLongitude;
		this.mPicture = mPicture;
	}
	
	public String getmCommentText() {
		return mCommentText;
	}
	public void setmCommentText(String mCommentText) {
		this.mCommentText = mCommentText;
	}
	public String getmLatitude() {
		return mLatitude;
	}
	public void setmLatitude(String mLatitude) {
		this.mLatitude = mLatitude;
	}
	public String getmLongitude() {
		return mLongitude;
	}
	public void setmLongitude(String mLongitude) {
		this.mLongitude = mLongitude;
	}
	public String getmPicture() {
		return mPicture;
	}
	public void setmPicture(String mPicture) {
		this.mPicture = mPicture;
	}
}