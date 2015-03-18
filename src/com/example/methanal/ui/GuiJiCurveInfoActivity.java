package com.example.methanal.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import com.example.methanal.AppContext;
import com.example.methanal.R;
import com.example.methanal.db.DBtableGuiJiItem;
import com.example.methanal.struct.DeviceGuiJi;
import com.example.methanal.util.UIHealper;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GuiJiCurveInfoActivity extends BaseActivity{

	private DBtableGuiJiItem mDBtableGuiJiItem;
	private AppContext appContext;
	private Cursor myCursor;
	private TextView mTextViewGuiJiTitle; 
	private TextView mTextViewCurveStartTime; 
	private TextView mTextViewCurveStopTime; 
	private Button mButtonGuiJiCurveBack; 
	private List<DeviceGuiJi> lvDeviceGuiJi = new ArrayList<DeviceGuiJi>();
	private LinearLayout layout;
	private GraphicalView chart;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guiji_curve);
		appContext = (AppContext) getApplicationContext();
		mTextViewGuiJiTitle = (TextView) findViewById(R.id.TextViewGuiJiTitle);
		mTextViewCurveStartTime = (TextView) findViewById(R.id.TextViewCurveStartTime);
		mTextViewCurveStopTime = (TextView) findViewById(R.id.TextViewCurveStopTime);
		mButtonGuiJiCurveBack = (Button) findViewById(R.id.ButtonGuiJiCurveBack);
		mButtonGuiJiCurveBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		layout = (LinearLayout) findViewById(R.id.LinearLayoutCurveLine);
		//获取intent附加值
		Bundle extras = getIntent().getExtras();
		String guiji_title = extras.getString(GuiJiShowActivity.GUIJI_TITLE);
		if(extras != null){
			mTextViewGuiJiTitle.setText(guiji_title);//在标题栏显示地址
		}
		mDBtableGuiJiItem = new DBtableGuiJiItem(appContext);
		mDBtableGuiJiItem.createDBtable(guiji_title);
		myCursor=mDBtableGuiJiItem.select(guiji_title);
		
//		把从数据库中获取的数据放入数组列表
		for(int i = 0;i < myCursor.getCount();i++){
			myCursor.moveToPosition(i);
			DeviceGuiJi l_DeviceGuiJi = new DeviceGuiJi(myCursor.getString(0), myCursor.getString(1));
			lvDeviceGuiJi.add(l_DeviceGuiJi);
		}
		if(lvDeviceGuiJi.size()>0){
			String starttime = UIHealper.DateToString(UIHealper.StringToDate(lvDeviceGuiJi.get(0).getmTime()));
			String stoptime = UIHealper.DateToString(UIHealper.StringToDate(lvDeviceGuiJi.get(myCursor.getCount()-1).getmTime()));
			mTextViewCurveStartTime.setText(starttime);
			mTextViewCurveStopTime.setText(stoptime);
			layout.removeView(chart);
			 double double_numbers[]=new double[lvDeviceGuiJi.size()];
			 String string_time[]=new String[lvDeviceGuiJi.size()];
			 for (int i = 0; i < double_numbers.length; i++) {
				 double_numbers[i] = Double.parseDouble(lvDeviceGuiJi.get(i).getmValue());
				 string_time[i] = lvDeviceGuiJi.get(i).getmTime();
			 }
			 chart = execute(this,double_numbers,string_time);
			layout.addView(chart);	
		}

	}
	//显示保存对话框
	public GraphicalView execute(Context context, double[] double_numbers, String[] string_time) {
		String[] titles = new String[] { "value"};
		List<Date[]> dates = new ArrayList<Date[]>();
		List<double[]> values = new ArrayList<double[]>();
		double [] date = new double[2];
		values.add(double_numbers);
		getMaxMinValue(double_numbers, date);
		dates.add(new Date[string_time.length]);
		for (int i = 0; i < string_time.length; i++) {
			dates.get(0)[i] = UIHealper.StringToDate(string_time[i]);
		}
		int [] colors = new int[] { Color.BLUE};
		PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE};
		AbstractDemoChart LAbstractDemoChart = new AbstractDemoChart();
		XYMultipleSeriesRenderer renderer = LAbstractDemoChart.buildRenderer(colors, styles);
		LAbstractDemoChart.setChartSettings(renderer, "轨迹曲线", "时间", "甲醛浓度值", dates.get(0)[0].getTime()-10000, dates.get(0)[string_time.length-1].getTime()+10000, date[0]-0.1, date[1]+0.1,
		    Color.GRAY, Color.LTGRAY);
		renderer.setXLabels(20);
		renderer.setYLabels(10);
		  
		renderer.setGridColor(getResources().getColor(R.color.theme_blue_option_color)); //设置方格颜色
		renderer.setAxesColor(getResources().getColor(R.color.theme_blue_color));
		renderer.setLabelsColor(getResources().getColor(R.color.theme_blue_color));
		renderer.setLegendTextSize(15);    //曲线说明
		renderer.setXLabelsColor(getResources().getColor(R.color.theme_blue_color));
		renderer.setYLabelsColor(0,getResources().getColor(R.color.theme_blue_color));
		renderer.setShowLegend(false);
		renderer.setMargins(new int[] {20, 30, 100, 0});
		renderer.setMarginsColor(getResources().getColor(R.color.white));
		renderer.setShowGrid(true);
		renderer.setInScroll(true);  //调整大小
		renderer.setZoomButtonsVisible(true);
		return ChartFactory.getTimeChartView(context,  LAbstractDemoChart.buildDateDataset(titles, dates, values), renderer, "dd HH:mm:ss");
	}
	//求给定队列的最大值和最小值
	private int getMaxMinValue(double [] logdate1, double[] date){
		int result = 0;
		int m = logdate1.length;
		double [] logdate = new double[m];
		for(int i=0;i<m;i++){
			logdate[i] = logdate1[i];
		}
		for(int i = 0; i < m; i++){
			for(int j = i;j < m -1; j++){
				if(logdate[i] < logdate[j+1]){
					Double temp = logdate[i];
					logdate[i] = logdate[j+1];
					logdate[j+1] = temp;
				}
			}
		}
		
		if(m-1 > 0){
			date[0] = logdate[m-1];
			date[1] = logdate[0];
		}else{
			result = -1;
		}
		return result;
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(mDBtableGuiJiItem != null){
			mDBtableGuiJiItem.close();
		}
		super.onDestroy();
	}

}