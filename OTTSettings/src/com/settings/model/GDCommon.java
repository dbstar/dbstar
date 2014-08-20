package com.settings.model;

import android.content.Intent;

public class GDCommon {
	public static final int MSG_TASK_FINISHED = 0x10001;
	public static final int MSG_MEDIA_MOUNTED = 0x10002;
	public static final int MSG_MEDIA_REMOVED = 0x10003;
	public static final int MSG_NETWORK_CONNECT = 0x10004;
	public static final int MSG_NETWORK_DISCONNECT = 0x10005;
	public static final int MSG_DISK_SPACEWARNING = 0x10006;
	public static final int MSG_SYSTEM_UPGRADE = 0x10007;
	public static final int MSG_SYSTEM_FORCE_UPGRADE = 0x10008;
	public static final int MSG_USER_UPGRADE_CANCELLED = 0x10009;
	
	public static final int MSG_ADD_TO_FAVOURITE = 0x10010;
	public static final int MSG_DELETE = 0x10011;
	public static final int MSG_PLAY_COMPLETED = 0x10012;

	public static final int MSG_USER_CHANGE_GUIDELIST = 0x10012;

	public static final int MSG_GET_NETWORKINFO = 0x20013;
	public static final int MSG_SET_NETWORKINFO = 0x20014;
	public static final int MSG_GET_ETHERNETINFO = 0x20015;
	public static final int MSG_SET_ETHERNETINFO = 0x20016;

	public static final int MSG_DATA_SIGNAL_STATUS = 0x30015;
	public static final int STATUS_HASSIGNAL = 0;
	public static final int STATUS_NOSIGNAL = 1;

	public static final int SYNC_STATUS_TODBSERVER = 0x30016;

	public static final int MSG_SAVE_BOOKMARK = 0x30017;

	public static final int MSG_UPDATE_COLUMN = 0x30018;
	public static final int MSG_UPDATE_PREVIEW = 0x30019;
	public static final int MSG_UPDATE_UIRESOURCE = 0x30020;
	
	public static final int MSG_NEW_MAIL = 0x50001;

	public static final int MSG_DHCP_PRIVATEIP_READY = 0x60001;

	public static final int MSG_UPDATE_POWERCONSUMPTION = 0x70001;
	public static final int MSG_UPDATE_POWERTOTALCOST = 0x70002;
	public static final String KeyPowerConsumption = "number";
	public static final String KeyPowerTotalCost = "cost";

	public static final int MSG_DISP_NOTIFICATION = 0x80001;
	public static final int MSG_HIDE_NOTIFICATION = 0x80002;

	// Ethernet phy connected/disconnected
	public static final int MSG_ETHERNET_PHYCONECTED = 0x90001;
	public static final int MSG_ETHERNET_PHYDISCONECTED = 0x90002;
	public static final int MSG_ETHERNET_CONNECTED = 0x90003;

	public static final int MSG_MUTE_AUDIO = 0xA001;
	public static final int MUTE_TRUE = 1;
	public static final int MUTE_FALSE = 0;
	
	public static final int MSG_SYSTEM_RECOVERY = 0xB0001;
	public static final int MSG_DISK_FORMAT_FINISHED = 0xB0002;
	public static final int VALUE_SUCCESSED = 1;
	public static final int VAULE_FAILED = 0;
	public static final int MSG_DISK_INITIALIZE = 0xB0003;
	public static final int MSG_BOOT_COMPLETED = 0xB0004;
	public static final int MSG_HOMEKEY_PRESSED = 0xB0005;
	public static final int MSG_DEVICE_INIT_FINISHED = 0xB0006;
	public static final int MSG_SYSTEM_REBOOT = 0xB0007;
	
	public static final int OSDDISP_TIMEOUT = 3600000;
	public static final String KeyDisk = "disk";

	public static final String LangCN = "cho";
	public static final String LangEN = "eng";

	public static final String KeyNetworkType = "net_type";
	public static final int TypeEthernet = 1;
	public static final int TypeWifi = 2;
	
	public static final String ColumnTypeMovie = "1";
	public static final String ColumnTypeTV = "2";
	public static final String ColumnTypePreview = "3";
	public static final String ColumnTypeMyFavourites = "7";
	public static final String ColumnTypeRecord = "8";
	public static final String ColumnTypeEntertainment = "9";
	public static final String ColumnTypeSettings = "L99";
	public static final String ColumnTypeUserCenter = "L98";
	public static final String ColumnTypeSmartLife = "SmartLife";
	public static final String ColumnTypeIPTV = "OTT";
	public static final String ColumnTypeMULTIPLEMEDIABOOK = "3";
	public static final String ColumnTypeMULTIPLEMEDIAMAGAZINE = "4";
	public static final String ColumnTypeMULTIPLEMEDIANEWSPAPER = "5";
	public static final String ColumnTypeMULTIPLEMEDIAVOICEDBOOK = "L10000";

	public static final String ColumnIDReceiveChooser = "L9803";
	public static final String ColumnIDDownloadStatus = "L9804";
	public static final String ColumnIDMediaShare = "L9805";
	public static final String ColumnIDAPPLIST = "L9807";
	public static final String ColumnIDFileBrowser = "L9806";
	public static final String ColumnIDBrowser = "L9808";
	public static final String ColumnIDSystemManagement = "L9809";

	public static final String ColumnIDGeneralInfoSettings = "L9801";
	public static final String ColumnIDMultimediaSettings = "L9902";
	public static final String ColumnIDNetworkSettings = "L9903";
	
	public static final String ColumnIDAdvancedSettings = "L9905";
	public static final String ColumnIDSmartcardSettings = "L9906";
	public static final String ColumnIDHelp = "L9920";
	public static final String ColumnIDProducts = "L9802";
	public static final String ColumnIDPowerTarget = "L9909";
	
	public static final String ColumnIDGuodianSmartPower = "G1";
	public static final String ColumnIDGuodianHomeEfficiency = "G2";
	public static final String ColumnIDGuodianSmartHome = "G3";
	public static final String ColumnIDGuodianNews = "G5";
	public static final String ColumnIDCNTV = "L97";

	public static final String ColumnIDGuodianMyPower = "G101";
	public static final String ColumnIDGuodianPowerBill = "G102";
	public static final String ColumnIDGuodianFeeRecord = "G103";
	public static final String ColumnIDGuodianPowerNews = "G104";
	public static final String ColumnIDGuodianBusinessNet = "G105";
	
	public static final String ColumnIDGuodianMyElectrical = "G301";
	public static final String ColumnIDGuodianModel = "G302";
	public static final String ColumnIDGuodianTimedTask = "G303";
	
	public static final String ColumnIDGuodianPowerConstitue = "G201";
	public static final String ColumnIDGuodianPowerConsumptionTrack = "G202";
	public static final String ColumnIDGuodianPowerConsumptionTrend = "G203";
	public static final String ColumnIDGuodianPowerTips = "G204";
	
	public static final String ColumnIDGuodianNewsFlash = "G501";
	
	public static final String ColumnIDMULTIPLEMEDIABOOK = "3";
	public static final String ColumnIDMULTIPLEMEDIANEWSPAPER = "5";
	public static final String ColumnIDMULTIPLEMEDIAVOICEDBOOK = "L10000";
	
	public static final String KeyMediaData = "media_data";
	public static final String KeyPackgeFile = "packge_file";

	public static final String KeyPublicationID = "publication_id";
	public static final String KeyPublicationSetID = "publicationset_id";
	public static final String KeyBookmark = "bookmark";

	public static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
	public static final String ActionAddFavourite = "com.dbstar.DbstarLauncher.Action.ADD_TO_FAVOURITE";
	public static final String ActionDelete = "com.dbstar.DbstarLauncher.Action.DELETE";
	public static final String ActionUpgradeCancelled = "com.dbstar.DbstarLauncher.Action.UPGRADE_CANCELLED";
	public static final String ActionBookmark = "com.dbstar.DbstarLauncher.Action.BOOKMARK";
	public static final String ActionPlayCompleted = "com.dbstar.DbstarDVB.Action.PLAY_COMPLETED";
	public static final String ActionPlayNext = "com.dbstar.DbstarDVB.Action.PLAY_NEXT";
	public static final String ActionNoNext = "com.dbstar.DbstarDVB.Action.NO_NEXT";

	public static final String ActionClearSettings = "com.dbstar.settings.action.CLEAR_SETTINGS";
	public static final String ActionSystemRecovery = "com.dbstar.DbstarLauncher.SystemRecovery";
	public static final String KeyRecoveryType = "recovery_type";
	public static final int RecoveryTypeClearPush = 0x1001;
	public static final int RecoveryTypeClearDrmInfo = 0x1002;
	public static final int RecoveryTypeFormatDisk = 0x1003;

	public static final String ActionGetNetworkInfo = "com.dbstar.DbstarLauncher.Action.GET_NETWORKINFO";
	public static final String ActionUpateNetworkInfo = "com.dbstar.DbstarLauncher.Action.UPDATE_NETWORKINFO";
	public static final String ActionSetNetworkInfo = "com.dbstar.DbstarLauncher.Action.SET_NETWORKINFO";
	public static final String ActionGetEthernetInfo = "com.dbstar.DbstarLauncher.Action.GET_ETHERNETINFO";
	public static final String ActionSetEthernetInfo = "com.dbstar.DbstarLauncher.Action.SET_ETHERNETINFO";
	public static final String KeyEthernetInfo = "ethernet_info";

	public static final String ActionScreenOn = Intent.ACTION_SCREEN_ON;
	public static final String ActionScreenOff = Intent.ACTION_SCREEN_OFF;

	public static final String ActionSDStateChange = "com.dbstar.DbstarLauncher.SDSTATE_CHANGE";
	public static final String KeySDState = "state";

	// Message to UI for smart card
	public static final int MSG_SMARTCARD_IN = 0x40001;
	public static final int MSG_SMARTCARD_OUT = 0x40002;
	public static final int MSG_SMARTCARD_INSERT_OK = 0x40003;
	public static final int MSG_SMARTCARD_INSERT_FAILED = 0x40004;
	public static final int MSG_SMARTCARD_REMOVE_OK = 0x40005;
	public static final int MSG_SMARTCARD_REMOVE_FAILED = 0x40006;

	// smart card state
	public static final int SMARTCARD_STATE_INSERTING = 0x1001;
	public static final int SMARTCARD_STATE_INSERTED = 0x1002;
	public static final int SMARTCARD_STATE_INVALID = 0x1003;
	public static final int SMARTCARD_STATE_REMOVING = 0x1004;
	public static final int SMARTCARD_STATE_REMOVED = 0x1005;
	public static final int SMARTCARD_STATE_NONE = 0x1000;

	// playback event
	public static final int PLAYBACK_COMPLETED = 0x01;

	public static final String FlagPlayVideo = "flag_playvideo";
	
	// network settigns
	public static final String ChannelEthernet = "1";
	public static final String ChannelBoth = "2";
	public static final int EthernetMode = 1;
	public static final int WirelessMode = 2;
	public static final String KeyChannel = "channel";
	public static final String ActionChannelModeChange = "com.dbstar.DbstarSettings.Action.CHANNELMODE_CHANGE";
	public static final String ChannelFile = "/data/dbstar/channel_file";
}
