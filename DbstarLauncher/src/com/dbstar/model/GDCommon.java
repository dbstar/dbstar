package com.dbstar.model;

public class GDCommon {
	public static final int MSG_TASK_FINISHED = 1;
	public static final int MSG_MEDIA_MOUNTED = 2;
	public static final int MSG_MEDIA_REMOVED = 3;
	public static final int MSG_NETWORK_CONNECT = 4;
	public static final int MSG_NETWORK_DISCONNECT = 5;
	public static final int MSG_DISK_SPACEWARNING = 6;
	
		
	public static final String KeyDisk = "disk";
	
	public static final String LangCN = "chi";
	public static final String LangEN = "eng";
	
	public static final int LocalizationEN = 0;
	public static final int LocalizationCN = 1;
	
	public static int getLocalizationType(String localization) {
		if (localization.equals(LangEN)) {
			return LocalizationEN;
		} else if (localization.equals(LangCN)) {
			return LocalizationCN;
		} else {
			return -1;
		}
	}
	
}
