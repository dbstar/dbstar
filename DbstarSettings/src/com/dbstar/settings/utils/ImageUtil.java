package com.dbstar.settings.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.http.util.EncodingUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ImageUtil {
	
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
					String homeName = hashMap.get(XMLParserUtils.XML_PicHome_Bg_Name);
					String homeUri = hashMap.get(XMLParserUtils.XML_PicHmoe_Bg_Uri);

//					Log.d("GDGeneralInfoActivity","-------xml parser----homeName = --" + homeName);
//					Log.d("GDGeneralInfoActivity","-------xml parser----homeUri = --" + homeUri);

					// 解析之后要判断图片文件在不在
					// 图片路径
					String picHomeUri = disk + "/" + homeUri + "/" + homeName;
					Log.d("GDGeneralInfoActivity", "-------xml parser----picHomeUri = --" + picHomeUri);

					File homeFile = new File(picHomeUri);
					Log.d(TAG, " homeFile.exists() = " + homeFile.exists());
					// 如果图片存在则判断图片的大小，如果正常则填充在上面
					if (homeFile.exists()) {
						FileInputStream fileInputStream = new FileInputStream(picHomeUri);
						BufferedInputStream bis = new BufferedInputStream(fileInputStream);
						bitmap = BitmapFactory.decodeStream(bis);

						if (bitmap.getWidth() != 1280 || bitmap.getHeight() != 720) {
							Log.d("GDGeneralInfoActivity", "-------bitmap.getWidth()------" + bitmap.getWidth());
							Log.d("GDGeneralInfoActivity", "-------bitmap.getHeight()------" + bitmap.getHeight());
							
							return null;
						}
					}
				}
			}

		} catch (Exception e) {
			Log.d("GDGeneralInfoActivity", "-------read file failed------" + e);
		}

		return bitmap;
	}
}
