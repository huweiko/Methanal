package com.example.methanal.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.example.methanal.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class DevSharePictureGridViewAdapter extends BaseAdapter{
	private Context 					context;//����������
	private List<String> 					listItems;//���ݼ���
	private LayoutInflater 				listContainer;//��ͼ����
	private int 						itemViewResource;//�Զ�������ͼԴ 
	public DevSharePictureGridViewAdapter(Context context, List<String> data,int resource) {
		this.context = context;			
		this.listContainer = LayoutInflater.from(context);	//������ͼ����������������
		this.itemViewResource = resource;
		this.listItems = data;
	}
	public void setListItems(List<String> data) {
		this.listItems = data;
	}
	class ListItemView{				//�Զ���ؼ�����  
		public ImageView sharepicture;
	} 
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
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
			listItemView.sharepicture = (ImageView)convertView.findViewById(R.id.ImageViewSharePictureGridView);
	
			//���ÿؼ�����convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}
		String SharePicture = listItems.get(position);
		if(SharePicture.equals("add")){
			listItemView.sharepicture.setImageResource(R.drawable.app_panel_add_icon_normal);
		}else{
			Bitmap bitmap = getLoacalBitmap(SharePicture); //�ӱ���ȡͼƬ
			listItemView.sharepicture.setImageBitmap(bitmap);
		}

		return convertView;
	}
	/**

	* ���ر���ͼƬ

	* @param url

	* @return

	*/
/*
	public Bitmap getLoacalBitmap(String url) {

	     try {

	          FileInputStream fis = new FileInputStream(url);

	          return BitmapFactory.decodeStream(fis);

	     } catch (FileNotFoundException e) {

	          e.printStackTrace();

	          return null;

	     }

	}*/
	private Bitmap getLoacalBitmap(String path)   {
		Bitmap result = null;
	    Log.i("showImage","loading:"+path);
	    BitmapFactory.Options bfOptions=new BitmapFactory.Options();
	    bfOptions.inDither=false;                     
	    bfOptions.inPurgeable=true;                 
	    bfOptions.inInputShareable=true;             
	    bfOptions.inTempStorage=new byte[32 * 1024]; 


	    File file=new File(path);
	    FileInputStream fs=null;
	    try {
	        fs = new FileInputStream(file);
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }

	    try {
	        if(fs!=null){
	        	result = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, bfOptions);	
	        } 
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally{ 
	        if(fs!=null) {
	            try {
	                fs.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	    return result;
	}
}