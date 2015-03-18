package com.example.methanal.db;

import com.example.methanal.struct.ShareDevice;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBtableShareHistory extends DBHelper {

	private final static String TABLE_NAME="device_info";//����
	public final static String DEVICE_TYPE="device_mType";//��������
	public final static String DEVICE_TIME="device_mTime";//ʱ��
	public final static String DEVICE_NAME="device_mName";//�豸����
	public final static String DEVICE_VALUE="device_mValue";//��ȩŨ��
	public final static String DEVICE_COMMENT_TEXT="device_CommentText";//�����ı�
	public final static String DEVICE_LATITUDE="device_Latitude";//γ��
	public final static String DEVICE_LONGITUDE="device_Longitude";//����
	public final static String DEVICE_PICTURE="device_picture";//����ͼƬ
	
	public DBtableShareHistory(Context context)
	{
		super(context);
	}
	public void createDBtable(){
		SQLiteDatabase db=this.getWritableDatabase(); 
		String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"+TABLE_NAME+"';";
		Cursor cursor = db.rawQuery(sql, null);
         if(cursor.moveToNext()){
             int count = cursor.getInt(0);
             if(count>0){

             }else{
          		String sql0="Create table "+TABLE_NAME+"("
        				+DEVICE_TYPE+" TEXT,"+
        				DEVICE_TIME+" TEXT,"+
        				DEVICE_NAME+" TEXT primary key,"+
        				DEVICE_VALUE+" TEXT,"+
        				DEVICE_COMMENT_TEXT+" TEXT,"+
        				DEVICE_LATITUDE+" TEXT,"+
        				DEVICE_LONGITUDE+" TEXT,"+
        				DEVICE_PICTURE+" TEXT)";
        		super.createDBtable(sql0);
             }
         }

	}

	public Cursor select()
	{
		return super.select(TABLE_NAME);
	}
	
	public long insert(ShareDevice mShareDevice)
	{
		ContentValues cv=new ContentValues(); 
		cv.put(DEVICE_PICTURE, mShareDevice.getmPicture());
		cv.put(DEVICE_TYPE, mShareDevice.getmType());
		cv.put(DEVICE_TIME, mShareDevice.getmTime());
		cv.put(DEVICE_NAME, mShareDevice.getmName());
		cv.put(DEVICE_VALUE, mShareDevice.getmValue());
		cv.put(DEVICE_COMMENT_TEXT, mShareDevice.getmCommentText());
		cv.put(DEVICE_LATITUDE, mShareDevice.getmLatitude());
		cv.put(DEVICE_LONGITUDE, mShareDevice.getmLongitude());
		
		return super.insert(TABLE_NAME, cv);
	}
	
	public void delete(String name)
	{
		super.delete(TABLE_NAME, DEVICE_NAME, name);
	}
	public void deleteAll()
	{
		super.deleteAll(TABLE_NAME);
	}
	
	public void update(String type,String time,String name,String value,String CommentText,String Latitude,String Longitude,String picture)
	{
		ContentValues cv=new ContentValues(); 
		cv.put(DEVICE_NAME, name);
		cv.put(DEVICE_TIME, time);
		cv.put(DEVICE_VALUE, value);
		cv.put(DEVICE_TYPE, type);
		cv.put(DEVICE_COMMENT_TEXT, CommentText);
		cv.put(DEVICE_LATITUDE, Latitude);
		cv.put(DEVICE_LONGITUDE, Longitude);
		cv.put(DEVICE_PICTURE, picture);
		super.update(TABLE_NAME, DEVICE_NAME, name, cv);
	}
}
