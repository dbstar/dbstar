package com.dbstar.model;

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

	public static final String KeyDisk = "disk";

	public static final String LangCN = "chi";
	public static final String LangEN = "eng";

	public static final String ColumnTypeTV = "2";
	public static final String ColumnTypeMovie = "1";
	public static final String ColumnTypePreview = "3";
	public static final String ColumnTypeGuodian = "100";
	public static final String ColumnTypeSettings = "99";
	public static final String ColumnTypeUserCenter = "98";
	public static final String ColumnTypeMyFavourites = "7";
	public static final String ColumnTypeRecord = "8";
	public static final String ColumnTypeEntertainment = "9";

	public static final String ColumnIDReceiveChooser = "9801";
	public static final String ColumnIDDownloadStatus = "9802";
	
	public static final String ColumnIDNetworkSettings = "9901";
	public static final String ColumnIDVideoSettings = "9902";
	public static final String ColumnIDAudioSettings = "9903";
	public static final String ColumnIDUserInfoSettings = "9904";
	public static final String ColumnIDDeviceInfoSettings = "9905";
	public static final String ColumnIDHelpSettings = "9906";
	
	public static final String KeyMediaData = "media_data";
	public static final String KeyPackgeFile = "packge_file";

	public static final String KeyPublicationID = "publication_id";
	public static final String KeyPublicationSetID = "publicationset_id";

	public static final String ActionAddFavourite = "com.dbstar.DbstarLauncher.Action.ADD_TO_FAVOURITE";
	public static final String ActionDelete = "com.dbstar.DbstarLauncher.Action.DELETE";
	public static final String ActionUpgradeCancelled = "com.dbstar.DbstarLauncher.Action.UPGRADE_CANCELLED";
}
