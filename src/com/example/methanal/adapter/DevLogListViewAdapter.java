package com.example.methanal.adapter;

import java.util.List;

import com.example.methanal.R;
import com.example.methanal.struct.Device;
import com.example.methanal.struct.ShareDevice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DevLogListViewAdapter extends BaseAdapter {
	
	private Context 					context;//运行上下文
	private List<ShareDevice> 					listItems;//数据集合
	private LayoutInflater 				listContainer;//视图容器
	private int 						itemViewResource;//自定义项视图源 

	static class ListItemView{				//自定义控件集合  
		public ImageView type;
	    public TextView name;
	    public TextView time;
	    public TextView value;
	}  
	
	public DevLogListViewAdapter(Context context, List<ShareDevice> data,int resource) {
		this.context = context;			
		this.listContainer = LayoutInflater.from(context);	//创建视图容器并设置上下文
		this.itemViewResource = resource;
		this.listItems = data;
	}
	
	public void setListItems(List<ShareDevice> data){
		listItems = data;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listItems.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ListItemView  listItemView = null;
		
		if (convertView == null) {
			//获取list_item布局文件的视图
			convertView = listContainer.inflate(this.itemViewResource, null);
			
			listItemView = new ListItemView();
			//获取控件对象
			listItemView.type = (ImageView)convertView.findViewById(R.id.ImageViewType);
			listItemView.name= (TextView)convertView.findViewById(R.id.TextViewName);
			listItemView.time = (TextView)convertView.findViewById(R.id.TextViewTime);
			listItemView.value = (TextView)convertView.findViewById(R.id.TextViewValue);
			
			//设置控件集到convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}	
		
		//设置文字和图片
		ShareDevice deviceLog = listItems.get(position);
		if(deviceLog.getmType().equals(Device.DEVICE_TYPE_NORMAL)){
			listItemView.type.setImageResource(R.drawable.device_normal);
		}
		else if(deviceLog.getmType().equals(Device.DEVICE_TYPE_OVERPROOF)){
			listItemView.type.setImageResource(R.drawable.device_overproof);
		}
		else if(deviceLog.getmType().equals(Device.DEVICE_TYPE_DANGEROUS)){
			listItemView.type.setImageResource(R.drawable.device_dangerous);
		}else{
			listItemView.type.setImageResource(R.drawable.device_normal);
		}
		listItemView.name.setText(deviceLog.getmName());
		listItemView.time.setText(deviceLog.getmTime());
		listItemView.value.setText(deviceLog.getmValue());
	
		return convertView;
	}

}
