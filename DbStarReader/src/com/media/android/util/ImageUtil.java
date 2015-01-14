package com.media.android.util;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ImageUtil {
	public static final String AppBG_Uri = "AppBG";
	
	public static Bitmap getBitmap(String appBg) {
		Bitmap bitmap = null;
		if (appBg != null && !appBg.equals("")) {
			File file = new File(appBg);
			if (file.exists()) {
				try {
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inJustDecodeBounds = true;
					Bitmap reBitmap = BitmapFactory.decodeFile(appBg, options);
					int width = options.outWidth;
//					int height = options.outHeight;
					options.inSampleSize = (int) (width * 1.0 / 1280);
					options.inJustDecodeBounds = false;
					
					bitmap = BitmapFactory.decodeFile(appBg, options);
				} catch (OutOfMemoryError e) {
					Log.d("DbstarPlayer ImageUtil", "appBg out of memory error = " + e);
					e.printStackTrace();
				}
			}
		}
		return bitmap;
	}
}
