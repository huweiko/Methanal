package com.example.methanal.ui;

import java.util.ArrayList;
import java.util.List;

import com.example.methanal.R;
import com.example.methanal.adapter.DevLogListViewAdapter;
import com.example.methanal.db.DBtableShareHistory;
import com.example.methanal.struct.ShareDevice;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class HistoryLogFragment extends Fragment implements OnItemClickListener{
	public final static String SHAREDEVICE_SER_KEY = "com.example.methanal.ui.HistoryLogFragment.SHAREDEVICE";
	private ListView mListViewLogs;
	private DevLogListViewAdapter mDevLogListViewAdapter;
	private DBtableShareHistory db;
	private Cursor myCursor;
	private List<ShareDevice> lvListViewDevice = new ArrayList<ShareDevice>();
	Boolean visiable = false;
	public static ShareDevice goShareDevice;
	protected static final int HANDLER_DOMBUILD_XML = 0;
		@SuppressLint("HandlerLeak") 
		public Handler mHandler = new Handler(){
	    	@Override
			public void handleMessage(Message msg)  
			{  
				switch(msg.what)  
				{  
					case HANDLER_DOMBUILD_XML:
					break;
					default:  
					break;            
				}  
				super.handleMessage(msg);  
			}  
		}; 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		db=new DBtableShareHistory(getActivity());
		db.createDBtable();
		//在onCreate()方法中调用setHasOptionsMenu()，否则系统不会调用Fragment的onCreateOptionsMenu()方法
		setHasOptionsMenu(true);
	}
	//onCreateView是创建该fragment对应的视图
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mTemLogFragmentView = inflater.inflate(R.layout.history_log_fragment, container,false);
		mListViewLogs = (ListView)mTemLogFragmentView.findViewById(R.id.history_log_listview);
		mDevLogListViewAdapter = new DevLogListViewAdapter(this.getActivity(), new ArrayList<ShareDevice>(), R.layout.history_log_listview);
		mListViewLogs.setAdapter(mDevLogListViewAdapter);
		mListViewLogs.setOnItemClickListener(this);
		return mTemLogFragmentView;
	}
	
	//当activity的onCreate()方法返回时调用。
	@Override
	public void onActivityCreated (Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		update_list();
		
	}
	
	Bitmap getIconFromCursor(Cursor c, int iconIndex) {  
	    byte[] data = c.getBlob(iconIndex);  
	    try {  
	        return BitmapFactory.decodeByteArray(data, 0, data.length);  
	    } catch (Exception e) {  
	        return null;  
	    }  
	}

	/**
	 * 更新数据列表
	 */
	private void update_list() {
		myCursor=db.select();
		lvListViewDevice.clear();
//		把从数据库中获取的数据放入数组列表
		for(int i = 0;i < myCursor.getCount();i++){
			myCursor.moveToPosition(i);
			ShareDevice lvDevice = new ShareDevice(myCursor.getString(0), myCursor.getString(1), myCursor.getString(2), myCursor.getString(3),myCursor.getString(4),myCursor.getString(5),myCursor.getString(6),myCursor.getString(7));
			lvListViewDevice.add(lvDevice);
		}
		mDevLogListViewAdapter.setListItems(lvListViewDevice);
		mDevLogListViewAdapter.notifyDataSetChanged();
	}
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		update_list();
	}
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
		if(db !=null){
			db.close();
		}
		if(myCursor != null){
			myCursor.close();
		}
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		goShareDevice = lvListViewDevice.get(position);
		Intent intent = new Intent();
		intent.setClass(getActivity(), HistoryShareActivity.class);
		startActivity(intent);
	}
	@Override
	public void setUserVisibleHint (boolean isVisibleToUser){
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		visiable = isVisibleToUser;
		if (isVisibleToUser == true) {
			update_list();
		}
		else
		{
		}
	}
}