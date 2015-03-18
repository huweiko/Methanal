package com.example.methanal.ui;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.methanal.AppContext;
import com.example.methanal.R;
import com.example.methanal.audioLSTter.audioLSTter;
import com.example.methanal.audioLSTter.protocolAnalyse;
import com.example.methanal.custom.CustomDialog;
import com.example.methanal.service.GuiJiService;
import com.example.methanal.struct.Device;
import com.example.methanal.util.HeadsetPlugReceiver;
import com.example.methanal.util.MediaButtonDisabler;
import com.example.methanal.util.UIHealper;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat") public class CheckFragment extends Fragment implements OnClickListener{
	private View mCheckFragmentView;
	private Button mButtonCheck;
	private TextView mTextViewMethanal;
	private TextView mTextViewBatteryPercent;
	private TextView mTextViewTemperature;
	private TextView mTextViewPPM;
	private TextView mTextViewMethanalStatus;
	private Button mButtonGuiji;
	private Button mButtonGuijiShow;
	
	private final int CLICK_SHARE = 1;  //分享
	private final int CLICK_GUIJI = 2;  //轨迹记录
	private final int CLICK_GUIJI_SHOW = 3;  // 轨迹显示
	AppContext appContext;
	private boolean mGuiJiButtonStatus = false;//轨迹按钮状态
	audioLSTter audiosrlter=null;
	
	private EditText mEditTextAddressName;
	public static boolean mHintStatus = false;
	
    private RelativeLayout mLinearLayoutLed ;
    private ThreadPlaySound mThreadPlaySound;
	private HeadsetPlugReceiver headsetPlugReceiver;
	private AudioManager audioManager;
	private ComponentName mRemoteControlClientReceiverComponent;
	private volatile boolean IsExitThreadPlaySound = true; 
	
	public int recvCount = 0;
	
	private Handler stepTimeHandler;
	private Runnable mTicker;
	long startTime = 0;
	public static final int methanal_color[] = {Color.GREEN,Color.YELLOW,Color.RED};
	public static final String methanal_status[] = {"安全范围","超标范围","危险范围"};
		//ppm显示值与mg/m3换算公式											
	public static final double methanal_ratio[] = {	1.3393 ,//0
													1.3344 ,//1
													1.3296 ,//2
													1.3247 ,//3
													1.3200 ,//4
													1.3152 ,//5
													1.3105 ,//6
													1.3058 ,//7
													1.3012 ,//8
													1.2966 ,//9
													1.2920 ,//10
													1.2874 ,//11
													1.2829 ,//12
													1.2784 ,//13
													1.2740 ,//14
													1.2696 ,//15
													1.2652 ,//16
													1.2608 ,//17
													1.2565 ,//18
													1.2522 ,//19
													1.2479 ,//20
													1.2437 ,//21
													1.2395 ,//22
													1.2353 ,//23
													1.2311 ,//24
													1.2270 ,//25
													1.2229 ,//26
													1.2188 ,//27
													1.2148 ,//28
													1.2107 ,//29
													1.2067 ,//30
													1.2028 ,//31
													1.1988 ,//32
													1.1949 ,//33
													1.1910 ,//34
													1.1872 ,//35
													1.1833 ,//36
													1.1795 ,//37
													1.1757 ,//38
													1.1720 ,//39
													1.1682 ,//40
													1.1645 ,//41
													1.1608 ,//42
													1.1571 ,//43
													1.1535 ,//44
													1.1499 ,//45
													1.1463 ,//46
													1.1427 ,//47
													1.1391 ,//48
													1.1356  //49
													};
	
	private void registerHeadsetPlugReceiver() {
		headsetPlugReceiver = new HeadsetPlugReceiver();
		headsetPlugReceiver.setHeadhandler(handler);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.HEADSET_PLUG");
        getActivity().registerReceiver(headsetPlugReceiver, intentFilter);
	}
	
	public static String _getString(String str,String str1 ){  
		int index = str.indexOf(str1); 
		if(index == -1){
			Log.e("_getString", "index == -1");
		}
		//str1是想要开始截取的字符。str是被截取的字符。
		return str.substring(index+1,str.length());
	}
	public static int _getStringIndex(String str,String str1 ){  
		int index = str.indexOf(str1); 
		if(index == -1){
			Log.e("_getStringIndex", "index == -1");
		}
		//str1是想要开始截取的字符。str是被截取的字符。
		return index;
	}
	@SuppressLint("HandlerLeak") 
	public Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1000:{
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
						recvCount++;
						int h = buffer[1];
						byte aa[] = new byte[h];
						System.arraycopy(data, 0, aa, 0, h);
						String valueString = new String(aa);
						int index = _getStringIndex(valueString,";");
						//甲醛PPM值
						String xTextViewMethanal = valueString.substring(0, index);
						valueString = _getString(valueString,";");
						index = _getStringIndex(valueString,";");
						//当前温度
						String xTextViewTemperature = valueString.substring(0, index);
						//当前电量
						String xTextViewBatteryPercent = _getString(valueString,";");
						
						int arrayIndex = (Integer.parseInt(xTextViewTemperature)+5)/10;
						arrayIndex = (arrayIndex < 0) ? 0 : arrayIndex;
						arrayIndex = (arrayIndex > 49) ? 49 : arrayIndex;
						
						Double MethanalPPM = Double.parseDouble(xTextViewMethanal);
						Double MethanalValue =	Double.parseDouble(xTextViewMethanal)*methanal_ratio[arrayIndex];
						int methanal_rank = 0;
						if(MethanalValue >=0 && MethanalValue <= 0.1){
							methanal_rank = 0;
						}else if(MethanalValue > 0.1 && MethanalValue <= 0.3){
							methanal_rank = 1;
						}else if(MethanalValue > 0.3){
							methanal_rank = 2;
						}
						
						DecimalFormat df = new DecimalFormat("0.00");   
						GuiJiService.mMethanal = df.format(MethanalValue);
						Log.i("Handler", "收到一个数据 ："+GuiJiService.mMethanal);
						
						mTextViewMethanal.setText(GuiJiService.mMethanal);
						mTextViewPPM.setText(df.format(MethanalPPM));
						mTextViewMethanalStatus.setText(methanal_status[methanal_rank]);
						mTextViewMethanal.setTextColor(methanal_color[methanal_rank]);
						mTextViewPPM.setTextColor(methanal_color[methanal_rank]);
						mTextViewMethanalStatus.setTextColor(methanal_color[methanal_rank]);
						
						
						mTextViewBatteryPercent.setText(xTextViewBatteryPercent+"%");
						
						mTextViewTemperature.setText(xTextViewTemperature.substring(0, 2)+"."+xTextViewTemperature.substring(2, 3)+"℃");
					
					}
				}break;
				
				case HeadsetPlugReceiver.HEADSET_CONNECTED:{
					enAudioReceive();
					audioManager.registerMediaButtonEventReceiver(mRemoteControlClientReceiverComponent);
					enAudioSend();
					if(mThreadPlaySound == null){
						mThreadPlaySound = new ThreadPlaySound();
						try {
							IsExitThreadPlaySound = true;
							mThreadPlaySound.start(); 
						} catch (IllegalThreadStateException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}break;
				case HeadsetPlugReceiver.HEADSET_NOT_CONNECTED:{
					
					if(mThreadPlaySound != null){
						audiosrlter.audiotrackStop();
						IsExitThreadPlaySound = false;
						try {
							mThreadPlaySound.join();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block

						}
						while(mThreadPlaySound.isAlive()){
							try {
								Thread.sleep(20);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						mThreadPlaySound = null;
						audiosrlter.release();
						if(mGuiJiButtonStatus){//结束轨迹、结束计时
							mGuiJiButtonStatus = false;
							if (appContext.GuiJiServiceIntent != null) {
								appContext.stopService(appContext.GuiJiServiceIntent);
								appContext.GuiJiServiceIntent = null;
							}
							stepTimeHandler.removeCallbacks(mTicker);
							mButtonGuiji.setText("轨迹记录");
						}

						
					}
				}break;
				
				case 111:{
					if(msg.arg1 == 0){
						 mLinearLayoutLed.setBackgroundResource(R.drawable.methanal_edging_show_none);
					}else{
					      mLinearLayoutLed.setBackgroundResource(R.drawable.methanal_edging_show_working);
					}
				}break;
				default:break;
			}
        } 
	};
	public class ThreadPlaySound extends Thread{
		@Override
		public void run(){
			while(IsExitThreadPlaySound){
				if(audiosrlter!=null)
				{
					audiosrlter.write("huwei");
//					Log.d("ThreadPlaySound", "正在播放音频");
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		appContext = (AppContext) getActivity().getApplication();
		audiosrlter=new audioLSTter(44100);//采样率，最大数据长度（字节）
		//在onCreate()方法中调用setHasOptionsMenu()，否则系统不会调用Fragment的onCreateOptionsMenu()方法
		setHasOptionsMenu(true);
	}
	//onCreateView是创建该fragment对应的视图
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mCheckFragmentView = inflater.inflate(R.layout.check_fragment, container,false);
		mTextViewMethanal = (TextView) mCheckFragmentView.findViewById(R.id.TextViewMethanal);
		mTextViewBatteryPercent = (TextView) mCheckFragmentView.findViewById(R.id.TextViewBatteryPercent);
		mTextViewTemperature = (TextView) mCheckFragmentView.findViewById(R.id.TextViewTemperature);
		mTextViewPPM = (TextView) mCheckFragmentView.findViewById(R.id.TextViewPPM);
		mTextViewMethanalStatus = (TextView) mCheckFragmentView.findViewById(R.id.TextViewMethanalStatus);
		mLinearLayoutLed = (RelativeLayout)mCheckFragmentView.findViewById(R.id.LinearLayoutLed );
		
		mButtonCheck = (Button) mCheckFragmentView.findViewById(R.id.ButtonCheck);
		mButtonCheck.setOnClickListener(this);
		mButtonCheck.setTag(CLICK_SHARE);
		
		mButtonGuiji = (Button) mCheckFragmentView.findViewById(R.id.ButtonGuiji);
		mButtonGuiji.setOnClickListener(this);
		mButtonGuiji.setTag(CLICK_GUIJI);
		
		mButtonGuijiShow = (Button) mCheckFragmentView.findViewById(R.id.ButtonGuijiShow);
		mButtonGuijiShow.setOnClickListener(this);
		mButtonGuijiShow.setTag(CLICK_GUIJI_SHOW);
		
		audiosrlter.clientHandler=this.handler;
		return mCheckFragmentView;
	}
	
	//当activity的onCreate()方法返回时调用。
	@Override
	public void onActivityCreated (Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		//获取音频服务
		audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
		//注册接收的Receiver
		mRemoteControlClientReceiverComponent = new ComponentName(getActivity().getPackageName(), MediaButtonDisabler.class.getName());
		registerHeadsetPlugReceiver();
		
	}
	
	private void showNoticeDialog()
	{
		CustomDialog.Builder customBuilder = new CustomDialog.Builder(this.getActivity());
		customBuilder.setTitle(R.string.is_conserve);
		final LayoutInflater inflater = LayoutInflater.from(this.getActivity());
		View v = inflater.inflate(R.layout.conserve_dialog, null);
		mEditTextAddressName = (EditText) v.findViewById(R.id.EditTextAddressName);
		LayoutParams lp = mEditTextAddressName.getLayoutParams();
		mEditTextAddressName.setLayoutParams(lp);
		customBuilder.setContentView(v);
		customBuilder.setNegativeButton(R.string.conserve_cancel, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		});
		customBuilder.setPositiveButton(R.string.conserve_confirm, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				if(!mEditTextAddressName.getText().toString().equals("")){
					
					Intent intent = new Intent();
					intent.setClass(getActivity(), CheckShareActivity.class);
					String device_type = Device.DEVICE_TYPE_NORMAL;
					Double mMethanal = Double.parseDouble(GuiJiService.mMethanal);
					if(mMethanal >= 0 && mMethanal <= 0.1){
						device_type = Device.DEVICE_TYPE_NORMAL;
					}
					else if(mMethanal > 0.1 && mMethanal <= 0.3){
						device_type = Device.DEVICE_TYPE_OVERPROOF;
					}
					else if(mMethanal > 0.3){
						device_type = Device.DEVICE_TYPE_DANGEROUS;
					}
					
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd\tHH:mm:ss");//设置日期格式
					Device device = new Device(device_type, df.format(new Date()), mEditTextAddressName.getText().toString(), GuiJiService.mMethanal);
					intent.putExtra(Device.Device_Name, device.getmName());
					intent.putExtra(Device.Device_Time, device.getmTime());
					intent.putExtra(Device.Device_Type, device.getmType());
					intent.putExtra(Device.Device_Value, device.getmValue());
					startActivity(intent);
					dialog.dismiss();
				}else{
					UIHealper.DisplayToast(getActivity(), "没有输入名字");
				}
			}
		});

		Dialog noticeDialog = customBuilder.create();
		noticeDialog.show();
	}
	@Override
	public void onDestroy(){
		
		if(audiosrlter != null){
			audiosrlter.release();
		}
		if(mThreadPlaySound != null){
			IsExitThreadPlaySound = false;
			try {
				mThreadPlaySound.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mThreadPlaySound = null;
		}
		if (appContext.GuiJiServiceIntent != null) {
			Log.i("huwei", "stop service");
			appContext.stopService(appContext.GuiJiServiceIntent);
			appContext.GuiJiServiceIntent = null;
		}
		getActivity().unregisterReceiver(headsetPlugReceiver);
		super.onDestroy();
	}
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	@Override
	public void setUserVisibleHint (boolean isVisibleToUser){
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		mHintStatus = isVisibleToUser;
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		mHintStatus = false;
		super.onPause();
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		mHintStatus = true;
		super.onResume();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int tag = (Integer) v.getTag();
		switch (tag) {
		case CLICK_SHARE:{
			showNoticeDialog();
		}break;
		case CLICK_GUIJI:{
			if(mGuiJiButtonStatus){//结束轨迹、结束计时
				mGuiJiButtonStatus = false;
				if (appContext.GuiJiServiceIntent != null) {
					appContext.stopService(appContext.GuiJiServiceIntent);
					appContext.GuiJiServiceIntent = null;
				}
				stepTimeHandler.removeCallbacks(mTicker);
				mButtonGuiji.setText("轨迹记录");
			}else{//开始轨迹、显示计时
		        final String[] itemsString = getResources().getStringArray(R.array.refresh_sort_options);
		        final String[] itemInt = getResources().getStringArray(R.array.refresh_sort_options_values);
		        new AlertDialog.Builder(getActivity()).setTitle("选择间隔时间").setItems(itemsString, new DialogInterface.OnClickListener() { 
		            public void onClick(DialogInterface dialog, int item) { 
						mGuiJiButtonStatus = true;
						if (appContext.GuiJiServiceIntent == null) {
							appContext.GuiJiServiceIntent = new Intent(appContext,
									GuiJiService.class);
							appContext.GuiJiServiceIntent.putExtra(getString(R.string.guiji_interval), Integer.parseInt(itemInt[item]));
							appContext.startService(appContext.GuiJiServiceIntent);
						}
						 mButtonGuiji.setText("00:00:00");
						 stepTimeHandler = new Handler();
						 startTime = System.currentTimeMillis();
						 mTicker = new Runnable() {
							 public void run() {
								 String content = showTimeCount(System.currentTimeMillis() - startTime);
								 mButtonGuiji.setText(content);

								 long now = SystemClock.uptimeMillis();
								 long next = now + (1000 - now % 1000);
								 stepTimeHandler.postAtTime(mTicker, next);
							 }
						 };
						 //启动计时线程，定时更新
						 mTicker.run();
		            } 
		        }).show();//显示对话框 
				
			}

		}break;
		case CLICK_GUIJI_SHOW:{
			Intent intent = new Intent();
			intent.setClass(appContext, GuiJiShowActivity.class);
			startActivity(intent);
		}break;
		default:break;
		}
	}
	//使能音频检测
	private void enAudioReceive() {
		audiosrlter.inGtrig=2500;//中信N76:500,中信U880:100
		audiosrlter.enToReceive((int)1200, (byte)12);//TODO enable
		audiosrlter.inGmax=0.8f;//中信N76:0.9f,中信U880:0.75f
		audiosrlter.inGmin=0.2f;//中信N76:0.1f,中信U880:0.25f
		audiosrlter.inPolarity=true;
	}
	public String showTimeCount(long time) {
		if(time >= 360000000){
		  return "00:00:00";
		}
		String timeCount = "";
		long hourc = time/3600000;
		String hour = "0" + hourc;
		hour = hour.substring(hour.length()-2, hour.length());

		long minuec = (time-hourc*3600000)/(60000);
		String minue = "0" + minuec;
		minue = minue.substring(minue.length()-2, minue.length());

		long secc = (time-hourc*3600000-minuec*60000)/1000;
		String sec = "0" + secc;
		sec = sec.substring(sec.length()-2, sec.length());
		timeCount = hour + ":" + minue + ":" + sec;
		return timeCount;
	}
	private void enAudioSend() {
		audiosrlter.enToSend((int)9600, (byte)100);//TODO enable
		audiosrlter.outAmplitude=5000;
		audiosrlter.outPolarity=true;//
	}


}
