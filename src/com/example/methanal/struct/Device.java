package com.example.methanal.struct;

public class Device{
	private String mType;
	private String mTime;
	private String mName;
	private String mValue;
	public static final String Device_Type = "device_type";
	public static final String Device_Time = "device_time";
	public static final String Device_Name = "device_name";
	public static final String Device_Value = "device_value";
	
	//设备类型
	public static final String DEVICE_TYPE_NORMAL = "com.DEVICE_TYPE_NORMAL";
	public static final String DEVICE_TYPE_OVERPROOF = "com.DEVICE_TYPE_OVERPROOF";
	public static final String DEVICE_TYPE_DANGEROUS = "com.DEVICE_TYPE_DANGEROUS";
	
	
	public static final int Device_Type_Empty = 0;
	public static final int Device_Type_Normal = 1;
	
	public Device(String mType, String mTime, String mName, String mValue){
		this.mType = mType;
		this.mTime = mTime;
		this.mName = mName;
		this.mValue = mValue;
	}
	public String getmType() {
		return mType;
	}
	public void setmType(String mType) {
		this.mType = mType;
	}
	public String getmTime() {
		return mTime;
	}
	public void setmTime(String mTime) {
		this.mTime = mTime;
	}
	public String getmName() {
		return mName;
	}
	public void setmName(String mName) {
		this.mName = mName;
	}
	public String getmValue() {
		return mValue;
	}
	public void setmValue(String mValue) {
		this.mValue = mValue;
	}
}