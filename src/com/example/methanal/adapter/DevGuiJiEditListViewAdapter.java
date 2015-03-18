package com.example.methanal.adapter;

import com.example.methanal.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DevGuiJiEditListViewAdapter extends BaseAdapter {
	
	private Context 					context;//����������
	private String [] 					listItems;//���ݼ���
	private LayoutInflater 				listContainer;//��ͼ����
	private int 						itemViewResource;//�Զ�������ͼԴ 
	OnGuiJiItemClickClass onItemClickClass;
	static class ListItemView{				//�Զ���ؼ�����  
	    public TextView name;
	    public ImageView delete;
	}  
	
	public DevGuiJiEditListViewAdapter(Context context, String[] data,int resource,OnGuiJiItemClickClass onItemClickClass) {
		this.context = context;			
		this.listContainer = LayoutInflater.from(context);	//������ͼ����������������
		this.itemViewResource = resource;
		this.listItems = data;
		this.onItemClickClass=onItemClickClass;
	}
	
	public void setListItems(String[] data){
		listItems = data;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listItems.length;
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
			//��ȡlist_item�����ļ�����ͼ
			convertView = listContainer.inflate(this.itemViewResource, null);
			
			listItemView = new ListItemView();
			//��ȡ�ؼ�����
			listItemView.name= (TextView)convertView.findViewById(R.id.TextViewEditGuiJiName);
			listItemView.delete= (ImageView)convertView.findViewById(R.id.ImageViewEditGuiJiDelete);
			//���ÿؼ�����convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}	
		
		//�������ֺ�ͼƬ
		String name = listItems[position];
		listItemView.name.setText(name);
		listItemView.delete.setOnClickListener(new OnGuiJiDeleteClick(position));
		return convertView;
	}
	public interface OnGuiJiItemClickClass{
		public void OnItemClick(View v,int Position);
	}
	
	class OnGuiJiDeleteClick implements OnClickListener{
		int position;
		
		public OnGuiJiDeleteClick(int position) {
			this.position=position;
		}
		@Override
		public void onClick(View v) {
			if (onItemClickClass!=null ) {
				onItemClickClass.OnItemClick(v, position);
			}
		}
	}
}
