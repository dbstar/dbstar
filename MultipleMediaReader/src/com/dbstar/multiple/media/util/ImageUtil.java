package com.dbstar.multiple.media.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

public class ImageUtil {
	
	public static BitmapDrawable setDrawable(String uri, int targetWidth) {
		BitmapDrawable drawable;
		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(uri, bitmapOptions);

		int width = bitmapOptions.outWidth;
		int height = bitmapOptions.outHeight;
		Log.d("ImageManager", "width = " + width);
		Log.d("ImageManager", "height = " + height);

		bitmapOptions.inJustDecodeBounds = false;
		// bitmapOptions.outWidth为获取到的原图的宽度
		bitmapOptions.inSampleSize = (int) ((bitmapOptions.outWidth) * 1.0 / targetWidth);

		Bitmap resizedBitmap = BitmapFactory.decodeFile(uri, bitmapOptions);

		Log.d("ImageManager", "uri = " + uri);
		drawable = new BitmapDrawable(resizedBitmap);
		return drawable;
	}
	
}
