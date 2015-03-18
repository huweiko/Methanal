package com.example.methanal.service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.methanal.AppContext;
import com.example.methanal.R;
import com.example.methanal.audioLSTter.audioLSTter;
import com.example.methanal.audioLSTter.protocolAnalyse;
import com.example.methanal.db.DBtableGuiJiItem;
import com.example.methanal.db.DBtableGuiJiName;
import com.example.methanal.struct.DeviceGuiJi;
import com.example.methanal.ui.CheckFragment;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

@SuppressLint("SimpleDateFormat") public class GuiJiService extends Service{
	AppContext appContext;
	boolean ServiceRunning = false;
	RefreshThread mRefreshThread = null;
//	audioLSTter audiosrlter=null;
//	Handler handler;
	public static String mMethanal = "0.0";
	DBtableGuiJiItem mDBtableGuiJiItem;
	DBtableGuiJiName mDBtableGuiJiName;
	String mGuiJiTableName = null;
	private int guiji_interval = 10;
	public class RefreshThread extends Thread{
    	
		@Override
		public void run(){
    		while(ServiceRunning)
        	{
    			DeviceGuiJi deviceGuiJi = new DeviceGuiJi(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()), mMethanal);
    			mDBtableGuiJiItem.insert(mGuiJiTableName, deviceGuiJi);
    			deviceGuiJi = null;
    			Log.i("GuiJiService", "current mMethanal: "+mMethanal);
    			try {
					Thread.sleep(guiji_interval*1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
    		
    	}
    }
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub

		return null;
	}
	@SuppressLint("HandlerLeak") 
	public void onCreate(){
		super.onCreate();
		appContext = (AppContext) getApplication();
		mGuiJiTableName = "A"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		ServiceRunning = true;
		mDBtableGuiJiItem = new DBtableGuiJiItem(appContext);
		mDBtableGuiJiItem.createDBtable(mGuiJiTableName);
		mDBtableGuiJiName = new DBtableGuiJiName(appContext);
		mDBtableGuiJiName.createDBtable();
		mDBtableGuiJiName.insert(mGuiJiTableName);
/*		audiosrlter=new audioLSTter(44100);//采样率，最大数据长度（字节）
		audiosrlter.inGtrig=2500;//中信N76:500,中信U880:100
		audiosrlter.enToReceive((int)1200, (byte)10);//TODO enable
		audiosrlter.inGmax=0.8f;//中信N76:0.9f,中信U880:0.75f
		audiosrlter.inGmin=0.2f;//中信N76:0.1f,中信U880:0.25f
		audiosrlter.inPolarity=true;
		
		handler=new Handler(){
			public void handleMessage(Message msg) {
				if(msg.what==1000)
				{
					if(audiosrlter.available() <=0){
						return;
					}
					byte[] buffer = new byte[audiosrlter.available()];
					byte[] data = new byte[audiosrlter.available()];
					int i =0;
					while(audiosrlter.available()!=0)
					{
						buffer[i++] = audiosrlter.read();
					}
					if(protocolAnalyse.decode_protocal(buffer, data) != -1){
						int h = buffer[1]-2;
						byte aa[] = new byte[h];
						System.arraycopy(data, 0, aa, 0, h);
						String valueString = new String(aa);
						int index = CheckFragment._getStringIndex(valueString,";");
						String xTextViewMethanal = valueString.substring(0, index);
						valueString = CheckFragment._getString(valueString,";");
						index = CheckFragment._getStringIndex(valueString,";");
						String xTextViewTemperature = valueString.substring(0, index);
						int arrayIndex = (Integer.parseInt(xTextViewTemperature)+5)/10;
						arrayIndex = (arrayIndex < 0) ? 0 : arrayIndex;
						arrayIndex = (arrayIndex > 49) ? 49 : arrayIndex;
						Double MethanalValue =	Double.parseDouble(xTextViewMethanal)*CheckFragment.methanal_ratio[arrayIndex];
						DecimalFormat df = new DecimalFormat("#0.00");   
						mMethanal = df.format(MethanalValue)+"";
					}
					else{
					}
				}
	        } 
		};
		audiosrlter.clientHandler=this.handler;*/
		
	}
	public int onStartCommand(Intent intent , int flags , int startId){
		if(intent == null){
			return Service.START_REDELIVER_INTENT;
		}
		Bundle extras = intent.getExtras();
		if(extras != null){
			guiji_interval = extras.getInt(appContext.getString(R.string.guiji_interval));
			ServiceRunning = true;
			if(mRefreshThread == null){
	    		mRefreshThread = new RefreshThread();
	    		try {
	    			mRefreshThread.start(); 
				} catch (IllegalThreadStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}	
		}

		return Service.START_STICKY;
	}
	public void onDestroy(){
		super.onDestroy();
/*		if(audiosrlter != null){
			audiosrlter.release();
		}*/
		if(mDBtableGuiJiName != null){
			mDBtableGuiJiName.close();
		}
		if(mDBtableGuiJiItem != null){
			mDBtableGuiJiItem.close();
		}
		ServiceRunning = false;
		if(mRefreshThread != null){
    		try {
				mRefreshThread.join(15);
				mRefreshThread = null;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	}
}