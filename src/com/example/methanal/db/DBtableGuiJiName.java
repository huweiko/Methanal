package com.example.methanal.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBtableGuiJiName extends DBHelper {
	private final static String TABLE_NAME="device_guiji_name";//����
	public final static String DEVICE_NAME="device_mName";//�켣��
	
	public DBtableGuiJiName(Context context)
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
          		String sql0="Create table "+TABLE_NAME+"("+
          				DEVICE_NAME+" TEXT)";
        		super.createDBtable(sql0);
             }
         }

	}

	public Cursor select()
	{
		return super.select(TABLE_NAME);
	}
	
	public long insert(String time)
	{
		ContentValues cv=new ContentValues(); 
		cv.put(DEVICE_NAME, time);
		
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
	
	public void update(String Name,String name, String value)
	{
		ContentValues cv=new ContentValues(); 
		cv.put(DEVICE_NAME, Name);
		super.update(TABLE_NAME, DEVICE_NAME, name, cv);
	}
}
