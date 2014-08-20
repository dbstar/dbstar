package com.settings.model;

import android.net.Uri;
import android.provider.BaseColumns;

public class GDSmartHomeContract {
	public static final String AUTHORITY = "com.dbstar.smarthome.provider";
	public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
	
	public static final class Global implements BaseColumns {

		public static final Uri CONTENT_URI = 
				Uri.withAppendedPath(AUTHORITY_URI, "global");

		public static final String CONTENT_TYPE =
	            "vnd.android.cursor.dir/com.dbstar.smarthome.provider.global";
		
		public static final String CONTENT_ITEM_TYPE =
	            "vnd.android.cursor.item/com.dbstar.smarthome.provider.global";
		
//		public static final String ID = "ID";
		public static final String NAME = "name";
		public static final String VALUE = "value";
	}
}
