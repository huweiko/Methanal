package com.example.methanal.view;

import java.util.Random;

import com.example.methanal.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class DialView extends SurfaceView implements Callback,Runnable{
	private SurfaceHolder holder;
	private Thread thread;
	private Paint paint;
	private Canvas canvas;
	private int screenW,screenH;
	private Bitmap bigDialBmp,bigPointerBmp,bgBmp,fuSheBmp;
	public static boolean flag;
	private int bigDialX,bigDialY,bigPointerX,bigPointerY,textBgX,textBgY;
	private Rect bgRect;
	private Bitmap textBg;
	public static Integer bigDialDegrees = new Integer(0);
	private String percentageText="";
	public DialView(Context context, AttributeSet attrs) {
		super(context, attrs);
		holder=getHolder();
		holder.addCallback(this);
		setZOrderOnTop(true);//改变SurfaceView在Viewpager中滑动有黑块的问题
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.BLACK);
		paint.setColor(Color.argb(255, 207, 60, 11));
		paint.setTextSize(22);
		setFocusable(true);
		setFocusableInTouchMode(true);
	}
	
	public void myDraw(){
		try {
			canvas = null;
			canvas=holder.lockCanvas(bgRect);
			if(canvas == null){
				return;
			}
			canvas.drawColor(Color.WHITE);
			drawBigDial();
		} catch (Exception e) {
			e.printStackTrace();			
		}finally{
			if(canvas != null){
				holder.unlockCanvasAndPost(canvas);
			}
		}
	}
    public void drawBigDial(){
		canvas.drawBitmap(bigDialBmp, bigDialX, bigDialY, paint);
		canvas.save();
		synchronized(bigDialDegrees)
        {
			canvas.rotate(bigDialDegrees,bigPointerX+bigPointerBmp.getWidth()/2, bigPointerY+bigPointerBmp.getHeight()/2);
        }
		
		canvas.drawBitmap(bigPointerBmp,bigPointerX,bigPointerY,paint);
		canvas.restore();
	}
	public void logic(){
		Random random1 = new Random(); 
		bigDialDegrees = random1.nextInt(); 
//		bigDialDegrees++;
	}
	public void run() {
		while(flag){
			long start = System.currentTimeMillis();
	        myDraw();
	        logic();
	        long end = System.currentTimeMillis();
	        try {
	            if (end - start < 20)
	           Thread.sleep(20 - (end - start));
	        } catch (Exception e) {
	           e.printStackTrace();
	        }
	        
		}
	}
	
	public void surfaceCreated(SurfaceHolder holder) {
		fuSheBmp = BitmapFactory.decodeResource(getResources(), R.drawable.signsec_dj_iv);
		textBg = BitmapFactory.decodeResource(getResources(), R.drawable.black_box);
		bigDialBmp = BitmapFactory.decodeResource(getResources(), R.drawable.signsec_dashboard_01);
		bigPointerBmp = BitmapFactory.decodeResource(getResources(), R.drawable.signsec_pointer_01);
		bgBmp = BitmapFactory.decodeResource(getResources(), R.drawable.signsec_dj_ll_blue);
		screenH=getHeight();
		screenW=getWidth();
		bgRect=new Rect(0, 0,screenW , bgBmp.getHeight());
		bigDialX =screenW/2-bigDialBmp.getWidth()/2;
		bigDialY =50;
		bigPointerX = bigDialX+bigDialBmp.getWidth()/2-bigPointerBmp.getWidth()/2;
		bigPointerY = 50;
		
		textBgX = bigDialX+bigDialBmp.getWidth()/2-textBg.getWidth()/2;
		textBgY = bigDialY+bigDialBmp.getHeight()/2-textBg.getHeight()/2-50;
		
		flag=true;
		thread= new Thread(this);
		thread.start();
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		flag=false;
	}
	public int getBigDialDegrees() {
		return bigDialDegrees;
	}
	public void setBigDialDegrees(int l_bigDialDegrees) {
		bigDialDegrees = l_bigDialDegrees;
	}
	public String getPercentageText() {
		return percentageText;
	}
	public void setPercentageText(String percentageText) {
		this.percentageText = percentageText;
	}
}
