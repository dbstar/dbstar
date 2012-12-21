package com.dbstar.model;

import android.content.Intent;

public class GDCommon {
	public static final int MSG_TASK_FINISHED = 1;
	public static final int MSG_MEDIA_MOUNTED = 2;
	public static final int MSG_MEDIA_REMOVED = 3;
	public static final int MSG_NETWORK_CONNECT = 4;
	public static final int MSG_NETWORK_DISCONNECT = 5;
	public static final int MSG_DISK_SPACEWARNING = 6;
	public static final int MSG_SYSTEM_UPGRADE = 7;
	public static final int MSG_SYSTEM_FORCE_UPGRADE = 8;
	public static final int MSG_USER_UPGRADE_CANCELLED = 9;

	public static final int MSG_ADD_TO_FAVOURITE = 10;
	public static final int MSG_DELETE = 11;

	public static final int MSG_USER_CHANGE_GUIDELIST = 12;

	public static final int MSG_GET_NETWORKINFO = 13;
	public static final int MSG_SET_NETWORKINFO = 14;

	public static final int MSG_DATA_SIGNAL_STATUS = 15;
	public static final int STATUS_HASSIGNAL = 0;
	public static final int STATUS_NOSIGNAL = 1;
	
	public static final int SYNC_STATUS_TODBSERVER = 16;

	public static final int MSG_SAVE_BOOKMARK = 17;
	
	public static final int MSG_UPDATE_COLUMN = 18;
	public static final int MSG_UPDATE_PREVIEW = 19;
	public static final int MSG_UPDATE_UIRESOURCE = 20;
	
	public static final int MSG_SMARTCARD_IN = 0x40001;
	public static final int MSG_SMARTCARD_OUT = 0x40002;
	
	public static final int MSG_NEW_MAIL = 0x50001;
	
	public static final String KeyDisk = "disk";

	public static final String LangCN = "cho";
	public static final String LangEN = "eng";

	public static final String ColumnTypeMovie = "1";
	public static final String ColumnTypeTV = "2";
	public static final String ColumnTypePreview = "3";
	public static final String ColumnTypeMyFavourites = "7";
	public static final String ColumnTypeRecord = "8";
	public static final String ColumnTypeEntertainment = "9";
	public static final String ColumnTypeSettings = "L99";
	public static final String ColumnTypeUserCenter = "L98";
	public static final String ColumnTypeGuodian = "100";

	public static final String ColumnIDReceiveChooser = "L9801";
	public static final String ColumnIDDownloadStatus = "L9802";

	public static final String ColumnIDGeneralInfoSettings = "L9901";
	public static final String ColumnIDMultimediaSettings = "L9902";
	public static final String ColumnIDNetworkSettings = "L9903";
	public static final String ColumnIDFileBrowser = "L9904";
	public static final String ColumnIDAdvancedSettings = "L9905";
	public static final String ColumnIDSmartcardSettings = "L9906";
	
	public static final String KeyMediaData = "media_data";
	public static final String KeyPackgeFile = "packge_file";

	public static final String KeyPublicationID = "publication_id";
	public static final String KeyPublicationSetID = "publicationset_id";
	public static final String KeyBookmark = "bookmark";

	public static final String ActionAddFavourite = "com.dbstar.DbstarLauncher.Action.ADD_TO_FAVOURITE";
	public static final String ActionDelete = "com.dbstar.DbstarLauncher.Action.DELETE";
	public static final String ActionUpgradeCancelled = "com.dbstar.DbstarLauncher.Action.UPGRADE_CANCELLED";
	public static final String ActionBookmark = "com.dbstar.DbstarLauncher.Action.BOOKMARK";

	public static final String ActionGetNetworkInfo = "com.dbstar.DbstarLauncher.Action.GET_NETWORKINFO";
	public static final String ActionUpateNetworkInfo = "com.dbstar.DbstarLauncher.Action.UPDATE_NETWORKINFO";
	public static final String ActionSetNetworkInfo = "com.dbstar.DbstarLauncher.Action.SET_NETWORKINFO";

	public static final String ActionScreenOn = Intent.ACTION_SCREEN_ON;
	public static final String ActionScreenOff = Intent.ACTION_SCREEN_OFF;
}
