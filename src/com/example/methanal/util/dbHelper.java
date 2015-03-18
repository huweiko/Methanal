package com.example.methanal.util;

import java.io.ByteArrayOutputStream;
import com.example.methanal.struct.ShareDevice;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

public class dbHelper extends SQLiteOpenHelper {

	private final static String DATABASE_NAME="device_db";//数据库名
	private final static int DATABASE_VERSION=1;
	private final static String TABLE_NAME="device_info";//表名
	public final static String DEVICE_TYPE="device_mType";//质量类型
	public final static String DEVICE_TIME="device_mTime";//时间
	public final static String DEVICE_NAME="device_mName";//设备描述
	public final static String DEVICE_VALUE="device_mValue";//甲醛浓度
	public final static String DEVICE_COMMENT_TEXT="device_CommentText";//分享文本
	public final static String DEVICE_LATITUDE="device_Latitude";//纬度
	public final static String DEVICE_LONGITUDE="device_Longitude";//经度
	public final static String DEVICE_PICTURE="device_picture";//分享图片
//	public final static String FIELD_ID="_id"; 
	
	public dbHelper(Context context)
	{
		super(context, DATABASE_NAME,null, DATABASE_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql="Create table "+TABLE_NAME+"("
				+DEVICE_TYPE+" TEXT,"+
				DEVICE_TIME+" TEXT,"+
				DEVICE_NAME+" TEXT primary key,"+
				DEVICE_VALUE+" TEXT,"+
				DEVICE_COMMENT_TEXT+" TEXT,"+
				DEVICE_LATITUDE+" TEXT,"+
				DEVICE_LONGITUDE+" TEXT,"+
				DEVICE_PICTURE+" TEXT)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		String sql=" DROP TABLE IF EXISTS "+TABLE_NAME;
		db.execSQL(sql);
		onCreate(db);
	}

	public Cursor select()
	{
		SQLiteDatabase db=this.getReadableDatabase();
		Cursor cursor=db.query(TABLE_NAME, null, null, null, null, null, null);
		return cursor;
	}
	
	public long insert(ShareDevice mShareDevice)
	{
		SQLiteDatabase db=this.getWritableDatabase();
		ContentValues cv=new ContentValues(); 
/*		if(mShareDevice.getmPicture()!=null){
			// BLOB类型   
			ByteArrayOutputStream os = new ByteArrayOutputStream();  
			// 将Bitmap压缩成PNG编码，质量为100%存储           
			mShareDevice.getmPicture().compress(Bitmap.CompressFormat.PNG, 100, os);   
			cv.put(DEVICE_PICTURE, os.toByteArray());		
		}*/
		cv.put(DEVICE_PICTURE, mShareDevice.getmPicture());
		cv.put(DEVICE_TYPE, mShareDevice.getmType());
		cv.put(DEVICE_TIME, mShareDevice.getmTime());
		cv.put(DEVICE_NAME, mShareDevice.getmName());
		cv.put(DEVICE_VALUE, mShareDevice.getmValue());
		cv.put(DEVICE_COMMENT_TEXT, mShareDevice.getmCommentText());
		cv.put(DEVICE_LATITUDE, mShareDevice.getmLatitude());
		cv.put(DEVICE_LONGITUDE, mShareDevice.getmLongitude());
		
		long row=db.insert(TABLE_NAME, null, cv);
		return row;
	}
	
	public void delete(String name)
	{
		SQLiteDatabase db=this.getWritableDatabase();
		String where=DEVICE_NAME+"=?";
		String[] whereValue={name};
		db.delete(TABLE_NAME, where, whereValue);
	}
	public void deleteAll()
	{
		SQLiteDatabase db=this.getWritableDatabase();
		db.delete(TABLE_NAME, null, null);
	}
	
	public void update(String type,String time,String name,String value,String CommentText,String Latitude,String Longitude,String picture)
	{
		SQLiteDatabase db=this.getWritableDatabase();
		String where=DEVICE_NAME+"=?";
		String[] whereValue={name};
		ContentValues cv=new ContentValues(); 
		cv.put(DEVICE_NAME, name);
		cv.put(DEVICE_TIME, time);
		cv.put(DEVICE_VALUE, value);
		cv.put(DEVICE_TYPE, type);
		cv.put(DEVICE_COMMENT_TEXT, CommentText);
		cv.put(DEVICE_LATITUDE, Latitude);
		cv.put(DEVICE_LONGITUDE, Longitude);
		cv.put(DEVICE_PICTURE, picture);
		db.update(TABLE_NAME, cv, where, whereValue);
	}
}
