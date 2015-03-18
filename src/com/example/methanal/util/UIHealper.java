package com.example.methanal.util;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.DateFormat;
import android.widget.Toast;


@SuppressLint("SimpleDateFormat") public class UIHealper {

	public static void DisplayToast(Context context, CharSequence charSequence)
	{
		Toast.makeText(context, charSequence, Toast.LENGTH_SHORT).show();
	}

	/**
     * 判断是否为合法IP
     * 网上摘的，自己验证下，怎么用，我就不用说了吧？
     * @return true or false
     */
    public static boolean isIpv4(String ipAddress) {
        String ip = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
        Pattern pattern = Pattern.compile(ip);
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.matches();
    }
      //通过文件路径获取到bitmap

    public static Bitmap getBitmapByPath(String path) {
      if (path == null || "".equals(path))
       return null;
      else {
       Bitmap temp = null;
       File file = new File(path);
       if (file != null && file.isFile()) {
        temp = BitmapFactory.decodeFile(path);
        }
       return temp;
       }
       
     }
     public static Bitmap convertToBitmap(String path, int w, int h) {
    	 BitmapFactory.Options opts = new BitmapFactory.Options();
    	 // 设置为ture只获取图片大小
    	 opts.inJustDecodeBounds = true;
    	 opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
    	 // 返回为空
    	 BitmapFactory.decodeFile(path, opts);
    	 int width = opts.outWidth;
    	 int height = opts.outHeight;
	 	 float scaleWidth = 0.f, scaleHeight = 0.f;
	 	 if (width > w || height > h) {
	 		 // 缩放
	 		 scaleWidth = ((float) width) / w;
	         scaleHeight = ((float) height) / h;
	      }
	      opts.inJustDecodeBounds = false;
	      float scale = Math.max(scaleWidth, scaleHeight);
	      opts.inSampleSize = (int)scale;
	      WeakReference<Bitmap> weak = new WeakReference<Bitmap>(BitmapFactory.decodeFile(path, opts));
	      return Bitmap.createScaledBitmap(weak.get(), w, h, true);
	 }
     private static SimpleDateFormat format1= new SimpleDateFormat("yyyyMMddHHmmss");
     private static SimpleDateFormat format2= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
     public static Date StringToDate(String datestr){ 
    	 Date date = null; 
    	 try { 
    		 	date = format1.parse(datestr);  
    		 } catch (Exception e) {  
    			 e.printStackTrace();
    		 }
    	 return date;
     }
     public static String DateToString(Date date){
    	 try { 
    		 	return format2.format(date);  
    	 } catch (Exception e) {
    			 // TODO Auto-generated catch block  
    			 e.printStackTrace();
    	 } 
    	 return "";
     }
}
