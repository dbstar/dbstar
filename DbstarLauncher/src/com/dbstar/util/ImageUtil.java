package com.dbstar.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.http.util.EncodingUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.dbstar.model.GDDataModel;
import com.dbstar.model.GDSystemConfigure;

public class ImageUtil {
	
	public static final String Home_Key = "home"; 
	public static final String Service_Key = "service"; 
	public static final String App_Key = "app"; 
	
	private static final String TAG = "ImageUtil";
	
	public static HashMap<String, Bitmap> parserXmlAndLoadPic() {
		HashMap<String, Bitmap> bitmaps = new HashMap<String, Bitmap>();
		
		GDDataModel dataModel = new GDDataModel();
		GDSystemConfigure mConfigure = new GDSystemConfigure();
		dataModel.initialize(mConfigure);
		String disk = dataModel.getPushDir();
		
		LogUtil.d(TAG, " disk = " + disk);
		
		try {
			String content = "";
			
			InputStream in = new FileInputStream("/data/dbstar/SProduct.beacon");
			
			int length = in.available();
			byte[] bytes = new byte[length];
			in.read(bytes);
			content = EncodingUtils.getString(bytes, "UTF-8");
			
			in.close();
			
			LogUtil.d(TAG, " content = " + content);
			
			if (content.length() <= 0) {
				return null;
			}
			
			String[] split = content.split("=");
			
			if (split!= null && split.length > 1) {
				
				split[1].replace('\t', '\0');
				String xmlPath = split[1].replace("\n", "");
				
				if (xmlPath.contains("\t")) {
					xmlPath = split[1].replace("\t", "");
				}
				
				File picXmlFile = new File(xmlPath);
				
				LogUtil.d(TAG, " xmlPath = " + xmlPath + " and this file is exists = " + picXmlFile.exists());
				
				if (!picXmlFile.exists()) {
					return null;
				}
				
				// 如果文件和xml文件存在，则转化为流
				InputStream inputStream = new FileInputStream(xmlPath);
				// 如果xml文件存在，则解析。
				HashMap<String, String> hashMap = XMLParserUtils.readXML(inputStream);
				
				if (hashMap != null && hashMap.size() > 0) {
					String homeName = hashMap.get(XMLParserUtils.XML_PicHome_Bg_Name);
					String homeUri = hashMap.get(XMLParserUtils.XML_PicHmoe_Bg_Uri);

					// 解析之后要判断图片文件在不在
					// 图片路径
					String picHomeUri = disk + "/" + homeUri + "/" + homeName;
					LogUtil.d(TAG, " picHomeUri = " + picHomeUri);
					
					File homeFile = new File(picHomeUri);
					LogUtil.d(TAG, " homeFile.exists() = " + homeFile.exists());
					// 如果图片存在则判断图片的大小，如果正常则填充在上面
					if (homeFile.exists()) {
//						FileInputStream fileInputStream = new FileInputStream(picHomeUri);
//						BufferedInputStream bis = new BufferedInputStream(fileInputStream);
//						Bitmap bitmap = BitmapFactory.decodeStream(bis);
						
						
						BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
						bitmapOptions.inJustDecodeBounds = true;
						
						Bitmap bitmap = BitmapFactory.decodeFile(picHomeUri, bitmapOptions);
						
						int width = bitmapOptions.outWidth;
						int height = bitmapOptions.outHeight;
						Log.d("ImageUtil HomeBG", "width = " + width);
						Log.d("ImageUtil HomeBG", "height = " + height);

						bitmapOptions.inJustDecodeBounds = false;
						// bitmapOptions.outWidth为获取到的原图的宽度
						bitmapOptions.inSampleSize = (int) ((bitmapOptions.outWidth) * 1.0 / 1280);

						Bitmap resizedBitmap = BitmapFactory.decodeFile(picHomeUri, bitmapOptions);

//						if (bitmap.getWidth() * bitmap.getHeight() <= 1280 * 720) {
							bitmaps.put(Home_Key, resizedBitmap);
//						}
					}

					String serviceName = hashMap.get(XMLParserUtils.XML_PicService_Bg_Name);
					String serviceUri = hashMap.get(XMLParserUtils.XML_PicService_Bg_Uri);

					String picServiceUri = disk + "/" + serviceUri + "/" + serviceName;
					LogUtil.d(TAG, " picServiceUri = --" + picServiceUri);
					File serviceFile = new File(picServiceUri);
					LogUtil.d(TAG, " serviceFile.exists() = " + serviceFile.exists());

					if (serviceFile.exists()) {
//						FileInputStream stream = new FileInputStream(picServiceUri);
//						BufferedInputStream bis = new BufferedInputStream(stream);
//
//						Bitmap bitmap = BitmapFactory.decodeStream(bis);
//
//						if (bitmap.getWidth() * bitmap.getHeight() <= 560 * 400) {
//							bitmaps.put("service", bitmap);
//						}
						
						BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
						bitmapOptions.inJustDecodeBounds = true;
						
						Bitmap bitmap = BitmapFactory.decodeFile(picServiceUri, bitmapOptions);
						
						int width = bitmapOptions.outWidth;
						int height = bitmapOptions.outHeight;
						Log.d("ImageUtil HomeBG", "width = " + width);
						Log.d("ImageUtil HomeBG", "height = " + height);

						bitmapOptions.inJustDecodeBounds = false;
						// bitmapOptions.outWidth为获取到的原图的宽度
						bitmapOptions.inSampleSize = (int) ((bitmapOptions.outWidth) * 1.0 / 560);

						Bitmap resizedBitmap = BitmapFactory.decodeFile(picServiceUri, bitmapOptions);

						bitmaps.put(Service_Key, resizedBitmap);
					}
					
					String appName = hashMap.get(XMLParserUtils.XML_PicApp_Bg_Name);
					String appUri = hashMap.get(XMLParserUtils.XML_PicApp_Bg_Uri);
					String picAppUri = disk + "/" + appUri + "/" + appName;
					LogUtil.d(TAG, " picAppUri = --" + picAppUri);
					File appFile = new File(picAppUri);
					LogUtil.d(TAG, " appFile.exists() = " + appFile.exists());
					if (appFile.exists()) {
//						FileInputStream stream = new FileInputStream(picAppUri);
//						BufferedInputStream bis = new BufferedInputStream(stream);
//						
//						Bitmap bitmap = BitmapFactory.decodeStream(stream);
//						if (bitmap.getWidth() <= 1280 && bitmap.getHeight() <= 720) {
//							bitmaps.put("app", bitmap);
//						}
						
						BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
						bitmapOptions.inJustDecodeBounds = true;
						
						Bitmap bitmap = BitmapFactory.decodeFile(picAppUri, bitmapOptions);
						
						int width = bitmapOptions.outWidth;
						int height = bitmapOptions.outHeight;
						Log.d("ImageUtil HomeBG", "width = " + width);
						Log.d("ImageUtil HomeBG", "height = " + height);

						bitmapOptions.inJustDecodeBounds = false;
						// bitmapOptions.outWidth为获取到的原图的宽度
						bitmapOptions.inSampleSize = (int) ((bitmapOptions.outWidth) * 1.0 / 1280);

						Bitmap resizedBitmap = BitmapFactory.decodeFile(picAppUri, bitmapOptions);

						bitmaps.put(App_Key, resizedBitmap);
					}
					
				}
			}
			
		} catch (Exception e) {
			LogUtil.d(TAG, "-------read file failed------" + e);
		}

		return bitmaps;
	}
	
}
