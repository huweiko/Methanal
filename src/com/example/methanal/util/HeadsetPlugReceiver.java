package com.example.methanal.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class HeadsetPlugReceiver extends BroadcastReceiver {

	public static final int HEADSET_CONNECTED = 10;
	public static final int HEADSET_NOT_CONNECTED = 11;
	private Handler Headhandler;
	public void setHeadhandler(Handler handler){
		this.Headhandler = handler;
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		  if (intent.hasExtra("state")){
			   if (intent.getIntExtra("state", 0) == 0){	
				   Toast.makeText(context, "headset not connected", Toast.LENGTH_LONG).show();
					Message message = Headhandler.obtainMessage(HEADSET_NOT_CONNECTED);
					Headhandler.sendMessage(message);
			   }
			   else if (intent.getIntExtra("state", 0) == 1){
				   Toast.makeText(context, "headset connected", Toast.LENGTH_LONG).show();
					Message message = Headhandler.obtainMessage(HEADSET_CONNECTED);
					Headhandler.sendMessage(message);
			   }
		  }
		
	}

}
