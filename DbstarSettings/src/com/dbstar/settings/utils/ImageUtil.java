package com.dbstar.settings.utils;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ImageUtil {
	
	private static final String TAG = "DbstarSetting ImageUtil";
	
	public static Bitmap loadPic(String appUri) {

		Bitmap bitmap = null;

		File appFile = new File(appUri);
		if (appFile.exists()) {			
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			Bitmap resizedBitmap = BitmapFactory.decodeFile(appUri, options);
			int width = options.outWidth;
			int height = options.outHeight;
			Log.d(TAG, "width = " + width);
			Log.d(TAG, "height = " + height);
			
			// bitmapOptions.outWidth为获取到的原图的宽度
			options.inSampleSize = (int) ((options.outWidth) * 1.0 / 1280);
			options.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeFile(appUri, options);
		}
		return bitmap;
	}
}
