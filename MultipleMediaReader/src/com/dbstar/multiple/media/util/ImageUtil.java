package com.dbstar.multiple.media.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.http.util.EncodingUtils;

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
	
private static final String TAG = "DbstarSetting ImageUtil";
	
	public static Bitmap parserXmlAndLoadPic() {

		Bitmap bitmap = null;

		SatelliteSetting setting = SatelliteSetting.getInstance();
		String disk = setting.queryValue("PushDir");

		Log.d(TAG, " disk = " + disk);

		try {
			String content = "";

			InputStream in = new FileInputStream("/data/dbstar/SProduct.beacon");

			int length = in.available();
			byte[] bytes = new byte[length];
			in.read(bytes);
			content = EncodingUtils.getString(bytes, "UTF-8");

			in.close();

			Log.d(TAG, " content = " + content);

			if (content.length() <= 0) {
				return null;
			}

			String[] split = content.split("=");

			if (split != null && split.length > 1) {

				String xmlPath = split[1].replace("\n", "");

				if (xmlPath.contains("\t")) {
					xmlPath = split[1].replace("\t", "");
				}

				File picXmlFile = new File(xmlPath);

				Log.d(TAG, " xmlPath = " + xmlPath + " and this file is exists = " + picXmlFile.exists());
				
				if (!picXmlFile.exists()) {
					return null;
				}

				// 如果文件和xml文件存在，则转化为流
				InputStream inputStream = new FileInputStream(xmlPath);
				// 如果xml文件存在，则解析。
				HashMap<String, String> hashMap = XMLParserUtils.readXML(inputStream);

				if (hashMap != null && hashMap.size() > 0) {
					String appName = hashMap.get(XMLParserUtils.XML_PicApp_Bg_Name);
					String appUri = hashMap.get(XMLParserUtils.XML_PicApp_Bg_Uri);

					// 解析之后要判断图片文件在不在
					// 图片路径
					String picAppUri = disk + "/" + appUri + "/" + appName;
					Log.d("GDGeneralInfoActivity", "-------xml parser----picHomeUri = --" + picAppUri);

					File appFile = new File(picAppUri);
					Log.d(TAG, " homeFile.exists() = " + appFile.exists());
					// 如果图片存在则判断图片的大小，如果正常则填充在上面
					if (appFile.exists()) {
//						FileInputStream fileInputStream = new FileInputStream(picAppUri);
//						BufferedInputStream bis = new BufferedInputStream(fileInputStream);
//						bitmap = BitmapFactory.decodeStream(bis);
//
//						if (bitmap.getWidth() != 1280 || bitmap.getHeight() != 720) {
//							Log.d("GDGeneralInfoActivity", "-------bitmap.getWidth()------" + bitmap.getWidth());
//							Log.d("GDGeneralInfoActivity", "-------bitmap.getHeight()------" + bitmap.getHeight());
//							
//							return null;
//						}
						BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
						bitmapOptions.inJustDecodeBounds = true;
						
						Bitmap resizedBitmap = BitmapFactory.decodeFile(picAppUri, bitmapOptions);
						
						int width = bitmapOptions.outWidth;
						int height = bitmapOptions.outHeight;
						Log.d("ImageUtil HomeBG", "width = " + width);
						Log.d("ImageUtil HomeBG", "height = " + height);

						bitmapOptions.inJustDecodeBounds = false;
						// bitmapOptions.outWidth为获取到的原图的宽度
						bitmapOptions.inSampleSize = (int) ((bitmapOptions.outWidth) * 1.0 / 1280);

						bitmap = BitmapFactory.decodeFile(picAppUri, bitmapOptions);

					}
				}
			}

		} catch (Exception e) {
			Log.d("GDGeneralInfoActivity", "-------read file failed------" + e);
		}

		return bitmap;
	}
}
