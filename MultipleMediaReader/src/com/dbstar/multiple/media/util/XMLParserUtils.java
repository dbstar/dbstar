package com.dbstar.multiple.media.util;

import java.io.InputStream;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;

import android.util.Log;
import android.util.Xml;

public class XMLParserUtils {
	
	public static final String XML_PicApp_Bg_Name = "PicHomeBgNmae";
	public static final String XML_PicApp_Bg_Uri = "PicHomeBg";
	
	public static HashMap<String, String> readXML(InputStream inputStream) {
		
		if (inputStream == null) {
			return null;
		}
		
		HashMap<String, String> hashMap = new HashMap<String, String>();
		
		XmlPullParser pullParser = Xml.newPullParser();
		
		try {
			pullParser.setInput(inputStream, "UTF-8");
			int eventType = pullParser.getEventType();
			
			while(eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				// 文档开始事件，可以进行数据初始化处理
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					String tagName = pullParser.getName();
					if (tagName.equalsIgnoreCase("AppBG")) {
						
						String name = pullParser.getAttributeValue(null, "name");
						String uri = pullParser.getAttributeValue(null, "uri");
						
						hashMap.put(XML_PicApp_Bg_Name, name);
						hashMap.put(XML_PicApp_Bg_Uri, uri);
					}
					break;
				}
				eventType = pullParser.next();
			}
			
			inputStream.close();
			
		} catch (Exception e) {
			Log.d("XMLParserUtils", "-------error in parser xml--------" + e);
			e.printStackTrace();
		}
		
		return hashMap;
	}

}
