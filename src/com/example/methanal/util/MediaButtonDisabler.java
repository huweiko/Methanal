package com.example.methanal.util;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
/*阻止别的应用程序接收到媒体按键、如音量键和线控键*/
public class MediaButtonDisabler extends BroadcastReceiver {

    private static final String TAG = "MediaButtonDisabler";

    private static final BroadcastReceiver INSTANCE = new MediaButtonDisabler();

    @Override
    public void onReceive(Context context, Intent intent) {
    	String action = intent.getAction();
    	if (action.equalsIgnoreCase(Intent.ACTION_MEDIA_BUTTON)){
        	Log.d(TAG, "Intercepted media button.");
//            abortBroadcast();	
    	}
    }

    public static void register(Context context) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
        filter.setPriority(Integer.MAX_VALUE);
        context.registerReceiver(INSTANCE, filter);
    }

    public static void unregister(Context context) {
        context.unregisterReceiver(INSTANCE);
    }
}