package com.example.methanal.ui;

import java.util.ArrayList;
import java.util.HashMap;

import cn.sharesdk.framework.CustomPlatform;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.ShareCore;
import com.example.methanal.R;
import com.example.methanal.util.UIHealper;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ShareSettingFragment extends Fragment{
	private AuthAdapter adapter;
	protected static final int HANDLER_DOMBUILD_XML = 0;
		@SuppressLint("HandlerLeak") 
		public Handler mHandler = new Handler(){
	    	@Override
			public void handleMessage(Message msg)  
			{
	    		Platform plat = (Platform) msg.obj;
	    		String text = null;
	    		switch (msg.arg1) {
	    			case 1: {
	    				// 成功
	    				text = plat.getName() + " completed at " + text;
	    				UIHealper.DisplayToast(ShareSettingFragment.this.getActivity(), text);
	    			}
	    			adapter.notifyDataSetChanged();
	    			break;
	    			case 2: {
	    				// 失败
	    				text = plat.getName() + " caught error at " + text;
	    				UIHealper.DisplayToast(ShareSettingFragment.this.getActivity(), text);
	    			}
	    			break;
	    			case 3: {
	    				// 取消
	    				text = plat.getName() + " canceled at " + text;
	    				UIHealper.DisplayToast(ShareSettingFragment.this.getActivity(), text);
	    			}
	    			break;
	    			default:break;
	    		}
	    	}  
		}; 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//在onCreate()方法中调用setHasOptionsMenu()，否则系统不会调用Fragment的onCreateOptionsMenu()方法
		setHasOptionsMenu(true);
	}
	//onCreateView是创建该fragment对应的视图
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mTemLogFragmentView = inflater.inflate(R.layout.share_setting_fragment, container,false);
		ListView lvPlats = (ListView) mTemLogFragmentView.findViewById(R.id.lvPlats);
		lvPlats.setSelector(new ColorDrawable());
		ShareSDK.initSDK(this.getActivity());
		ShareSDK.registerPlatform(Laiwang.class);
		adapter = new AuthAdapter(this.getActivity());
		lvPlats.setAdapter(adapter);
		lvPlats.setOnItemClickListener(adapter);
		return mTemLogFragmentView;
	}
	
	//当activity的onCreate()方法返回时调用。
	@Override
	public void onActivityCreated (Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);

	}
	@Override
	public void onDestroy(){
		ShareSDK.stopSDK(this.getActivity());
		super.onDestroy();
		
	}

	private class AuthAdapter extends BaseAdapter implements OnItemClickListener, PlatformActionListener {
		private Context mContext;
		private ArrayList<Platform> platforms;

		public AuthAdapter(Context Context) {
			this.mContext = Context;

			// 获取平台列表
			Platform[] tmp = ShareSDK.getPlatformList(mContext);
			platforms = new ArrayList<Platform>();
			if (tmp == null) {
				return;
			}

			for (Platform p : tmp) {
				String name = p.getName();
				if ((p instanceof CustomPlatform)
						|| !ShareCore.canAuthorize(p.getContext(), name)) {
					continue;
				}
				platforms.add(p);
			}
		}

		public int getCount() {
			return platforms == null ? 0 : platforms.size();
		}

		public Platform getItem(int position) {
			return platforms.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(mContext, R.layout.auth_page_item, null);
			}

			int count = getCount();
			View llItem = convertView.findViewById(R.id.llItem);
			int dp_10 = cn.sharesdk.framework.utils.R.dipToPx(parent.getContext(), 10);
			if (count == 1) {
				llItem.setBackgroundResource(R.drawable.list_item_single_normal);
				llItem.setPadding(0, 0, 0, 0);
				convertView.setPadding(dp_10, dp_10, dp_10, dp_10);
			}
			else if (position == 0) {
				llItem.setBackgroundResource(R.drawable.list_item_first_normal);
				llItem.setPadding(0, 0, 0, 0);
				convertView.setPadding(dp_10, dp_10, dp_10, 0);
			}
			else if (position == count - 1) {
				llItem.setBackgroundResource(R.drawable.list_item_last_normal);
				llItem.setPadding(0, 0, 0, 0);
				convertView.setPadding(dp_10, 0, dp_10, dp_10);
			}
			else {
				llItem.setBackgroundResource(R.drawable.list_item_middle_normal);
				llItem.setPadding(0, 0, 0, 0);
				convertView.setPadding(dp_10, 0, dp_10, 0);
			}

			Platform plat = getItem(position);
			ImageView ivLogo = (ImageView) convertView.findViewById(R.id.ivLogo);
			Bitmap logo = getIcon(plat);
			if (logo != null && !logo.isRecycled()) {
				ivLogo.setImageBitmap(logo);
			}
			CheckedTextView ctvName = (CheckedTextView) convertView.findViewById(R.id.ctvName);
			ctvName.setChecked(plat.isValid());
			if (plat.isValid()) {
				String userName = plat.getDb().get("nickname");
				if (userName == null || userName.length() <= 0
						|| "null".equals(userName)) {
					// 如果平台已经授权却没有拿到帐号名称，则自动获取用户资料，以获取名称
					userName = getName(plat);
					plat.setPlatformActionListener(this);
					plat.showUser(null);
				}
				ctvName.setText(userName);
			}
			else {
				ctvName.setText(R.string.not_yet_authorized);
			}
			return convertView;
		}

		private Bitmap getIcon(Platform plat) {
			if (plat == null) {
				return null;
			}

			String name = plat.getName();
			if (name == null) {
				return null;
			}

			String resName = "logo_" + plat.getName();
			int resId = cn.sharesdk.framework.utils.R.getResId(R.drawable.class, resName);
			return BitmapFactory.decodeResource(mContext.getResources(), resId);
		}

		private String getName(Platform plat) {
			if (plat == null) {
				return "";
			}

			String name = plat.getName();
			if (name == null) {
				return "";
			}

			int resId = cn.sharesdk.framework.utils.R.getStringRes(mContext, plat.getName());
			return mContext.getString(resId);
		}

		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Platform plat = getItem(position);
			CheckedTextView ctvName = (CheckedTextView) view.findViewById(R.id.ctvName);
			if (plat == null) {
				ctvName.setChecked(false);
				ctvName.setText(R.string.not_yet_authorized);
				return;
			}

			if (plat.isValid()) {
				plat.removeAccount();
				ctvName.setChecked(false);
				ctvName.setText(R.string.not_yet_authorized);
				return;
			}

			plat.setPlatformActionListener(this);
			plat.showUser(null);
		}

		@Override
		public void onCancel(Platform plat, int action) {
			// TODO Auto-generated method stub
			Message msg = new Message();
			msg.arg1 = 3;
			msg.arg2 = action;
			msg.obj = plat;
			mHandler.sendMessage(msg);
		}

		@Override
		public void onComplete(Platform plat, int action,
				HashMap<String, Object> res) {
			// TODO Auto-generated method stub
			Message msg = new Message();
			msg.arg1 = 1;
			msg.arg2 = action;
			msg.obj = plat;
			mHandler.sendMessage(msg);
		}

		@Override
		public void onError(Platform plat, int action, Throwable arg2) {
			// TODO Auto-generated method stub
			Message msg = new Message();
			msg.arg1 = 2;
			msg.arg2 = action;
			msg.obj = plat;
			mHandler.sendMessage(msg);
		}

	}

}