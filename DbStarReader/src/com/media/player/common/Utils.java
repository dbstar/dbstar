package com.media.player.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

public class Utils {
	private static boolean FLAG_LOG_ERROR = true;
	private static boolean FLAG_LOG_INFO = true;
	public static boolean IS_TEST=false;
	/**
	 *px转dip 
	 */
	public static int px2dip(Context context, float pxValue){ 
		float scale = context.getResources().getDisplayMetrics().density; 
		return (int)(pxValue / scale + 0.5f); 
	} 
	/**
	 *dip转px 
	 */
	public static int dip2px(Context context, float dipValue){
		float scale = context.getResources().getDisplayMetrics().density;
		return (int)(dipValue * scale + 0.5f);
	}
	
	public static void printLogError(String tag, String msg){
		if(FLAG_LOG_ERROR){
			Log.e(tag, msg);
		}
	}
	
	public static void printLogInfo(String tag, String msg){
		if(FLAG_LOG_INFO){
			Log.i(tag, msg);
		}
	}
	
	public static final String PATH_IMAGE_SAVED = Environment.getExternalStorageDirectory().getPath()+"/fbtest/";
	public static void saveBitmapByPath(String path, Bitmap mBitmap) {
		boolean isSuccess = false;
		File dir = new File(PATH_IMAGE_SAVED);
		if(!dir.exists()){
			dir.mkdirs();
		}
		File f = new File(path);
		try {
			f.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		try {
			fOut.flush();
			isSuccess = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
