package com.example.methanal.ui;

import java.util.ArrayList;
import java.util.List;

import com.example.methanal.R;
import com.example.methanal.adapter.DevLogListViewEditAdapter;
import com.example.methanal.adapter.DevLogListViewEditAdapter.OnItemClickClass;
import com.example.methanal.db.DBtableShareHistory;
import com.example.methanal.struct.ShareDevice;
import com.example.methanal.util.PrivateFileOperate;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class HistoryLogEditActivity extends Activity{
	public final static String SHAREDEVICE_SER_KEY = "com.example.methanal.ui.HistoryLogFragment.SHAREDEVICE";
	private ListView mListViewLogs;
	private TextView mTextViewHistoryLogClear;
	private TextView mTextViewHistoryLogFinish;
	private DevLogListViewEditAdapter mDevLogListViewAdapter;
	private DBtableShareHistory db;
	private Cursor myCursor;
	private List<ShareDevice> lvListViewDevice = new ArrayList<ShareDevice>();
	Boolean visiable = false;
	public static ShareDevice goShareDevice;
	protected static final int HANDLER_DOMBUILD_XML = 0;
	// 自定义的弹出框类
	private SelectClearLogWindows menuWindow;
	
	
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
		setContentView(R.layout.activity_edit_history);
		mListViewLogs = (ListView)findViewById(R.id.history_log_listview);
		mTextViewHistoryLogClear = (TextView)findViewById(R.id.TextViewHistoryLogClear);
		mTextViewHistoryLogClear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				menuWindow = new SelectClearLogWindows(HistoryLogEditActivity.this, itemsOnClick);
				// 显示窗口
				menuWindow.showAtLocation(HistoryLogEditActivity.this
						.findViewById(R.id.LinearLayoutHistoryEditActivity), Gravity.BOTTOM
						| Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
			}
		});
		mTextViewHistoryLogFinish = (TextView)findViewById(R.id.TextViewHistoryLogFinish);
		mTextViewHistoryLogFinish.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		mDevLogListViewAdapter = new DevLogListViewEditAdapter(this, new ArrayList<ShareDevice>(), R.layout.history_log_listview, onItemClickClass);
		mListViewLogs.setAdapter(mDevLogListViewAdapter);
		db=new DBtableShareHistory(this);
		db.createDBtable();
		update_list();
	}
	DevLogListViewEditAdapter.OnItemClickClass onItemClickClass=new OnItemClickClass() {
		@Override
		public void OnItemClick(View v, int Position) {
			PrivateFileOperate.delFile(getApplicationContext(), lvListViewDevice.get(Position).getmPicture());
			delete_database(lvListViewDevice.get(Position).getmName());
		}
	};
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
	/**
	 * 更新数据列表
	 */
	private void delete_database(String name) {
		db.delete(name);
		update_list();
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
	public void onDestroy(){
		super.onDestroy();
		if(db !=null){
			db.close();
		}
		if(myCursor != null){
			myCursor.close();
		}
	}
	
	// 为弹出窗口实现监听类
	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			menuWindow.dismiss();
			switch (v.getId()) {
			case R.id.btn_clear_all:
				for(int i = 0;i<lvListViewDevice.size();i++ ){
					PrivateFileOperate.delFile(getApplicationContext(), lvListViewDevice.get(i).getmPicture());
				}
				db.deleteAll();
				update_list();
				break;
			default:
				break;
			}
		}

	};
}