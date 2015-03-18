package com.example.methanal.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.sharesdk.onekeyshare.OnekeyShare;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.methanal.R;
import com.example.methanal.adapter.DevSharePictureGridViewAdapter;
import com.example.methanal.custom.CustomDialog;
import com.example.methanal.db.DBtableShareHistory;
import com.example.methanal.logic.ImgFileListActivity;
import com.example.methanal.struct.Device;
import com.example.methanal.struct.ShareDevice;
import com.example.methanal.util.LocationUtil;
import com.example.methanal.util.MyLocation;
import com.example.methanal.util.PrivateFileOperate;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CheckShareActivity extends BaseActivity implements OnItemClickListener,
		OnGetGeoCoderResultListener {
	private ShareDevice mShareDevice;
	private ImageView mImageViewType;
	private ImageView mImageViewShareLocationStatusPic;
	private TextView mImageViewName;
	private TextView mTextViewTime;
	private TextView mTextViewValue;
	private TextView mTextViewShareLocation;
	private EditText mEditTextShareTextContent;
	private Button mButtonShare;
	private Button mButtonActivityShareBack;
	private Button mButtonActivityFinish;
	private GridView mGridViewSharePicture;
	private LinearLayout mLinearLayoutShareLocation;
	private DevSharePictureGridViewAdapter mDevSharePictureGridViewAdapter;
	private ArrayList<String> PictureList = new ArrayList<String>();
	private int SELECT_PICTURE = 1000;
	private GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	private boolean mShareLocationStatus = false;
	public static final String SHOW_PICTURE = "com.refeved.monitor.adapter.broadcast.SHOW_PICTURE";
	// 自定义的弹出框类
	private SelectPicPopupWindow menuWindow;

	public static final int RESULT_PHOTO = 10000;
	
	private DBtableShareHistory db;
	
	private LocationUtil locationUtil;
	private MyLocation location = null;
	@SuppressLint("HandlerLeak")
	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if(msg.obj != null){
					if(msg.obj.equals("")){
						mTextViewShareLocation.setText("未能获取位置信息");	
					}else{
						mTextViewShareLocation.setText((CharSequence) msg.obj);	
					}
				}else{
					mTextViewShareLocation.setText("未能获取位置信息");
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	public BroadcastReceiver receiver = new BroadcastReceiver() {
		@SuppressLint("SimpleDateFormat")
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals(SHOW_PICTURE)) {
				Bundle bundle = intent.getExtras();
				if (bundle.getStringArrayList("files") != null) {
					PictureList = bundle.getStringArrayList("files");
					if (!PictureList.get(0).equals("")&&!PictureList.get(0).equals("add")) {
						String filename = createFileName();
						PrivateFileOperate.filewrite(getApplicationContext(), filename, PictureList.get(0));
						mShareDevice.setmPicture(filename);
					}
					mDevSharePictureGridViewAdapter.setListItems(PictureList);
					mDevSharePictureGridViewAdapter.notifyDataSetChanged();
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		db=new DBtableShareHistory(this);
		db.createDBtable();
		Bundle bundle = getIntent().getExtras();

		if (bundle != null) {
			String name = null;
			String time = null;
			String type = null;
			String value = null;
			if (bundle.getString(Device.Device_Name) != null) {
				name = bundle.getString(Device.Device_Name);
			}
			if (bundle.getString(Device.Device_Time) != null) {
				time = bundle.getString(Device.Device_Time);
			}
			if (bundle.getString(Device.Device_Type) != null) {
				type = bundle.getString(Device.Device_Type);
			}
			if (bundle.getString(Device.Device_Value) != null) {
				value = bundle.getString(Device.Device_Value);
			}
			mShareDevice = new ShareDevice(type, time, name, value, "", "", "",null);
		}

		setContentView(R.layout.activity_share);
		mImageViewType = (ImageView) findViewById(R.id.ImageViewType);
		mImageViewName = (TextView) findViewById(R.id.TextViewName);
		mTextViewTime = (TextView) findViewById(R.id.TextViewTime);
		mTextViewValue = (TextView) findViewById(R.id.TextViewValue);
		mEditTextShareTextContent = (EditText) findViewById(R.id.EditTextShareTextContent);
		mGridViewSharePicture = (GridView) findViewById(R.id.GridViewSharePicture);
		mLinearLayoutShareLocation = (LinearLayout) findViewById(R.id.LinearLayoutShareLocation);
		mImageViewShareLocationStatusPic = (ImageView) findViewById(R.id.ImageViewShareLocationStatusPic);
		mTextViewShareLocation = (TextView) findViewById(R.id.TextViewShareLocation);
		mButtonShare = (Button) findViewById(R.id.ButtonShare);
		mButtonActivityShareBack = (Button) findViewById(R.id.ButtonActivityShareBack);
		mButtonActivityFinish = (Button) findViewById(R.id.ButtonActivityFinish);
		mButtonShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showShare(true, null);
			}
		});
		mButtonActivityShareBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		mButtonActivityFinish.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mShareDevice.setmCommentText(mEditTextShareTextContent.getText().toString());
				db.insert(mShareDevice);
				finish();
			}
		});
		
		mImageViewShareLocationStatusPic
				.setImageResource(R.drawable.sns_shoot_location_normal);
		mTextViewShareLocation.setText("显示所在位置");
		mTextViewShareLocation.setTextColor(getResources().getColor(
				R.color.theme_gray_text_color));

		mLinearLayoutShareLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mShareLocationStatus) {
					mShareDevice.setmLatitude("");
					mShareDevice.setmLongitude("");
					mShareLocationStatus = false;
					mImageViewShareLocationStatusPic
							.setImageResource(R.drawable.sns_shoot_location_normal);
					mTextViewShareLocation.setText("显示所在位置");
					mTextViewShareLocation.setTextColor(getResources()
							.getColor(R.color.theme_gray_text_color));
				} else {

					mShareLocationStatus = true;
					mImageViewShareLocationStatusPic
							.setImageResource(R.drawable.sns_shoot_location_pressed);
					mTextViewShareLocation.setText("正在获取位置...");
					mTextViewShareLocation.setTextColor(getResources()
							.getColor(R.color.black));
					if(locationUtil.isEnbled() == true) {
						locationUtil.exec();
						location = locationUtil.getMyLocation();
						if(location.getLatitude() == 0.0 && location.getLongitude() == 0.0){
							mTextViewShareLocation.setText("未能获取位置信息");
						}else{
							mShareDevice.setmLatitude(String.valueOf(location.getLatitude()));
							mShareDevice.setmLongitude(String.valueOf(location.getLongitude()));
							LatLng ptCenter = new LatLng(location.getLatitude(), location.getLongitude());
							// 反Geo搜索
							mSearch.reverseGeoCode(new ReverseGeoCodeOption()
									.location(ptCenter));	
						}
					}else{
						mTextViewShareLocation.setText("未能获取位置信息");
					}


				}
			}
		});
		if(mShareDevice.getmType().equals(Device.DEVICE_TYPE_NORMAL)){
			mImageViewType.setImageResource(R.drawable.device_normal);
		}
		else if(mShareDevice.getmType().equals(Device.DEVICE_TYPE_OVERPROOF)){
			mImageViewType.setImageResource(R.drawable.device_overproof);
		}
		else if(mShareDevice.getmType().equals(Device.DEVICE_TYPE_DANGEROUS)){
			mImageViewType.setImageResource(R.drawable.device_dangerous);
		}else{
			mImageViewType.setImageResource(R.drawable.device_normal);
		}
		mImageViewName.setText(mShareDevice.getmName());
		mTextViewTime.setText(mShareDevice.getmTime());
		mTextViewValue.setText(mShareDevice.getmValue());
		mGridViewSharePicture.setOnItemClickListener(this);
		PictureList.add("add");
		mDevSharePictureGridViewAdapter = new DevSharePictureGridViewAdapter(
				this, PictureList, R.layout.share_picture_item);
		mGridViewSharePicture.setAdapter(mDevSharePictureGridViewAdapter);
		locationUtil = new LocationUtil(this);
		// 初始化搜索模块，注册事件监听
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);

		IntentFilter filter = new IntentFilter();
		filter.addAction(CheckShareActivity.SHOW_PICTURE);
		registerReceiver(receiver, filter);
	}
	//显示保存对话框
	private void showDialog() {
		CustomDialog.Builder customBuilder = new CustomDialog.Builder(CheckShareActivity.this);
        customBuilder.setTitle(R.string.is_conserve);
		customBuilder.setPositiveButton(R.string.conserve_confirm, 
	            new DialogInterface.OnClickListener() {
	       
	    	public void onClick(DialogInterface dialog, int which) {
				mShareDevice.setmCommentText(mEditTextShareTextContent.getText().toString());
				db.insert(mShareDevice);
				dialog.dismiss();
				finish();
	        }
	    	
	  	});	
        customBuilder.setNegativeButton(R.string.conserve_cancel, new DialogInterface.OnClickListener() {
           
        	public void onClick(DialogInterface dialog, int which) {
        		dialog.dismiss();
        		finish();
            }
                	
        });

        Dialog dialog = customBuilder.create();
        dialog.show();
	}
	private void showShare(boolean silent, String platform) {
		final OnekeyShare oks = new OnekeyShare();
		oks.setTitle(getString(R.string.shareTitle));
		oks.setTitleUrl("http");
		oks.setSite(getString(R.string.app_name));
		oks.setNotification(R.drawable.ic_launcher,
				getResources().getString(R.string.app_name));

		oks.setText(mShareDevice.getmName() + "\t甲醛浓度为:" + mShareDevice.getmValue()
				+ "\t时间:" + mShareDevice.getmTime() + "\t\n"
				+ mEditTextShareTextContent.getText().toString());

		if (!PictureList.get(0).equals("")) {
			oks.setImagePath(PictureList.get(0));
		}
		if (mShareLocationStatus) {
			if (location != null) {
				oks.setLatitude((float) location.getLatitude());
				oks.setLongitude((float) location.getLongitude());	
			}
		}

		oks.setSilent(silent);
		if (platform != null) {
			oks.setPlatform(platform);
		}
		oks.setShareContentCustomizeCallback(new ShareContentCustomizeDemo());

		oks.show(this.getApplicationContext());
		mShareDevice.setmCommentText(mEditTextShareTextContent.getText().toString());
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SELECT_PICTURE) {
			if (resultCode == RESULT_OK) {
				if (data != null) {
				}
			}

		} else if (requestCode == RESULT_PHOTO) {
			String pathString = Environment.getExternalStorageDirectory().toString() + "/camera.jpg";
			PictureList.clear();
			PictureList.add(pathString);
			if (!PictureList.get(0).equals("")&&!PictureList.get(0).equals("add")) {
				String filename = createFileName();
				PrivateFileOperate.filewrite(getApplicationContext(), filename, PictureList.get(0));
				mShareDevice.setmPicture(filename);
			}
			mDevSharePictureGridViewAdapter.setListItems(PictureList);
			mDevSharePictureGridViewAdapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		// 实例化SelectPicPopupWindow
		menuWindow = new SelectPicPopupWindow(CheckShareActivity.this, itemsOnClick);
		// 显示窗口
		menuWindow.showAtLocation(CheckShareActivity.this
				.findViewById(R.id.RelativeLayoutShareActivity), Gravity.BOTTOM
				| Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置

	}

	private void showPictureContent() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, ImgFileListActivity.class);
		startActivity(intent);
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		// TODO Auto-generated method stub
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(CheckShareActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
					.show();
		}
		String strInfo = String.format("纬度:%f 经度:%f",
				result.getLocation().latitude, result.getLocation().longitude);
		Toast.makeText(CheckShareActivity.this, strInfo, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		// TODO Auto-generated method stub
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(CheckShareActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
					.show();
		}
		Message message = new Message();
		message.what = 1;
		message.obj = result.getAddress();
		mHandler.sendMessage(message);
	}

	// 为弹出窗口实现监听类
	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			menuWindow.dismiss();
			switch (v.getId()) {
			case R.id.btn_take_photo:
				try {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File out = new File(Environment.getExternalStorageDirectory(),"camera.jpg");

					Uri uri = Uri.fromFile(out);

					intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

					startActivityForResult(intent, RESULT_PHOTO);

				} catch (Exception e) {
					e.printStackTrace();
				}

				break;
			case R.id.btn_pick_photo:
				showPictureContent();
				break;
			default:
				break;
			}
		}

	};
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			showDialog();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}

	}
	@SuppressLint("SimpleDateFormat") 
	public String createFileName(){
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");//设置日期格式
		return "HW_"+df.format(new Date())+".jpg";
	}
}