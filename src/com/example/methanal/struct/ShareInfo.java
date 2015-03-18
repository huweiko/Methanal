package com.example.methanal.struct;
public class ShareInfo extends Device{
	public ShareInfo(String mType, String mTime, String mName, String mValue, String mShareText, String [] mPictureList, String mLocation) {
		super(mType, mTime, mName, mValue);
		// TODO Auto-generated constructor stub
		this.mShareText = mShareText;
		this.mPictureList = mPictureList;
		this.mLocation = mLocation;
	}
	private String mShareText;
	private String [] mPictureList;
	private String mLocation;
	public String getmShareText() {
		return mShareText;
	}
	public void setmShareText(String mShareText) {
		this.mShareText = mShareText;
	}
	public String getmLocation() {
		return mLocation;
	}
	public void setmLocation(String mLocation) {
		this.mLocation = mLocation;
	}
	public String [] getmPictureList() {
		return mPictureList;
	}
	public void setmPictureList(String [] mPictureList) {
		this.mPictureList = mPictureList;
	}
}