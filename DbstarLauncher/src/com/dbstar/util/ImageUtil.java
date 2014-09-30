package com.dbstar.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.http.util.EncodingUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.dbstar.model.GDDataModel;
import com.dbstar.model.GDSystemConfigure;

public class ImageUtil {
	
	public static final String Home_Key = "home"; 
	public static final String Service_Key = "service"; 
	
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
						FileInputStream fileInputStream = new FileInputStream(picHomeUri);
						BufferedInputStream bis = new BufferedInputStream(fileInputStream);
						Bitmap bitmap = BitmapFactory.decodeStream(bis);

						if (bitmap.getWidth() * bitmap.getHeight() <= 1280 * 720) {
							bitmaps.put(Home_Key, bitmap);
						}
					}

					String serviceName = hashMap.get(XMLParserUtils.XML_PicService_Bg_Name);
					String serviceUri = hashMap.get(XMLParserUtils.XML_PicService_Bg_Uri);

					String picServiceUri = disk + "/" + serviceUri + "/" + serviceName;
					LogUtil.d(TAG, " picServiceUri = --" + picServiceUri);
					File serviceFile = new File(picServiceUri);
					LogUtil.d(TAG, " serviceFile.exists() = " + serviceFile.exists());

					if (serviceFile.exists()) {
						FileInputStream stream = new FileInputStream(picServiceUri);
						BufferedInputStream bis = new BufferedInputStream(stream);

						Bitmap bitmap = BitmapFactory.decodeStream(bis);

						if (bitmap.getWidth() * bitmap.getHeight() <= 560 * 400) {
							bitmaps.put("service", bitmap);
						}
					}
				}
			}
			
		} catch (Exception e) {
			LogUtil.d(TAG, "-------read file failed------" + e);
		}

		return bitmaps;
	}
	
}
