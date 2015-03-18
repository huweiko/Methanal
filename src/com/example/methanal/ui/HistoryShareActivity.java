package com.example.methanal.ui;

import java.io.File;
import java.io.FileOutputStream;

import cn.sharesdk.onekeyshare.OnekeyShare;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.methanal.R;
import com.example.methanal.struct.Device;
import com.example.methanal.struct.ShareDevice;
import com.example.methanal.util.PrivateFileOperate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HistoryShareActivity extends BaseActivity implements OnGetGeoCoderResultListener {
	private ImageView mImageViewType;
	private ImageView mImageViewShareLocationStatusPic;
	private TextView mImageViewName;
	private TextView mTextViewTime;
	private TextView mTextViewValue;
	private TextView mTextViewShareLocation;
	private EditText mEditTextShareTextContent;
	private Button mButtonShare;
	private Button mButtonActivityShareBack;
	private ImageView mImageViewSharePicture;
	private LinearLayout mLinearLayoutShareLocation;
	private int SELECT_PICTURE = 1000;
	GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	public static final String SHOW_PICTURE = "com.refeved.monitor.adapter.broadcast.SHOW_PICTURE";

	public static final int RESULT_PHOTO = 10000;
	private ShareDevice mShareDevice;
	@SuppressLint("HandlerLeak")
	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				mTextViewShareLocation.setText((CharSequence) msg.obj);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mShareDevice = HistoryLogFragment.goShareDevice;

		setContentView(R.layout.activity_history_share);
		mImageViewType = (ImageView) findViewById(R.id.ImageViewType);
		mImageViewName = (TextView) findViewById(R.id.TextViewName);
		mTextViewTime = (TextView) findViewById(R.id.TextViewTime);
		mTextViewValue = (TextView) findViewById(R.id.TextViewValue);
		mEditTextShareTextContent = (EditText) findViewById(R.id.EditTextShareTextContent);
		mImageViewSharePicture = (ImageView) findViewById(R.id.ImageViewSharePicture);
		mImageViewShareLocationStatusPic = (ImageView) findViewById(R.id.ImageViewShareLocationStatusPic);
		mTextViewShareLocation = (TextView) findViewById(R.id.TextViewShareLocation);
		mButtonShare = (Button) findViewById(R.id.ButtonShare);
		mLinearLayoutShareLocation = (LinearLayout) findViewById(R.id.LinearLayoutShareLocation);
		mButtonActivityShareBack = (Button) findViewById(R.id.ButtonActivityShareBack);
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
				// TODO Auto-generated method stub
				finish();
			}
		});
		mImageViewShareLocationStatusPic
				.setImageResource(R.drawable.sns_shoot_location_normal);
		mTextViewShareLocation.setText("显示所在位置");
		mTextViewShareLocation.setTextColor(getResources().getColor(
				R.color.theme_gray_text_color));
		if(!mShareDevice.getmLatitude().equals("") && !mShareDevice.getmLongitude().equals("")){
			LatLng ptCenter = new LatLng(Float.parseFloat(mShareDevice.getmLatitude()), Float.parseFloat(mShareDevice.getmLongitude()));
			// 初始化搜索模块，注册事件监听
			mSearch = GeoCoder.newInstance();
			mSearch.setOnGetGeoCodeResultListener(this);
			// 反Geo搜索
			mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ptCenter));	
		}else{
			mLinearLayoutShareLocation.setVisibility(View.GONE);
		}
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
		mEditTextShareTextContent.setText(mShareDevice.getmCommentText());
		if(mShareDevice.getmPicture() == null){
			mImageViewSharePicture.setVisibility(View.GONE);
		}else{
			mImageViewSharePicture.setImageBitmap(PrivateFileOperate.fileread(getApplicationContext(), mShareDevice.getmPicture()));
		}
	}

	private void showShare(boolean silent, String platform) {
		final OnekeyShare oks = new OnekeyShare();
		oks.setTitle(getString(R.string.shareTitle));
		oks.setTitleUrl("http://sharesdk.cn");
		oks.setSite(getString(R.string.app_name));
		oks.setNotification(R.drawable.ic_launcher,
				getResources().getString(R.string.app_name));
		oks.setText(mShareDevice.getmName() + "\t甲醛浓度为:" + mShareDevice.getmValue()
				+ "\t时间:" + mShareDevice.getmTime() + "\t\n"
				+ mEditTextShareTextContent.getText().toString());
		if(mShareDevice.getmPicture() != null){
			//得到外部存储卡的路径

			String path=Environment.getExternalStorageDirectory().toString();

			//ff.png是将要存储的图片的名称
			  File file=new File(path, "ff.png");

			//从资源文件中选择一张图片作为将要写入的源文件
			  try {
				  FileOutputStream out=new FileOutputStream(file);
				  Bitmap mPicture = PrivateFileOperate.fileread(getApplicationContext(), mShareDevice.getmPicture());
				  mPicture.compress(CompressFormat.PNG, 100, out);
				  out.flush();
				  out.close();
			  } catch (Exception e) {
			   // TODO Auto-generated catch block
				  e.printStackTrace();
			  }

			oks.setImagePath(path+"/ff.png");
		}
		if (!mShareDevice.getmLatitude().equals("") && !mShareDevice.getmLongitude().equals("")) {
			oks.setLatitude(Float.parseFloat(mShareDevice.getmLatitude()));
			oks.setLongitude(Float.parseFloat(mShareDevice.getmLongitude()));
		}
		oks.setSilent(silent);
		if (platform != null) {
			oks.setPlatform(platform);
		}
		oks.setShareContentCustomizeCallback(new ShareContentCustomizeDemo());
		oks.show(this.getApplicationContext());
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

		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		// TODO Auto-generated method stub
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(HistoryShareActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
					.show();
		}
		String strInfo = String.format("纬度:%f 经度:%f",
				result.getLocation().latitude, result.getLocation().longitude);
		Toast.makeText(HistoryShareActivity.this, strInfo, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		// TODO Auto-generated method stub
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(HistoryShareActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
					.show();
		}
		Message message = new Message();
		message.what = 1;
		message.obj = result.getAddress();
		mHandler.sendMessage(message);
		// Toast.makeText(ShareActivity.this, result.getAddress(),
		// Toast.LENGTH_LONG).show();
	}
}