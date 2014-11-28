package com.dbstar.settings.utils;

public class SettingsCommon {

	// Settings actions
	public final static String ACTION_CVBSMODE_CHANGE = "android.intent.action.CVBSMODE_CHANGE";

	public final static String ACTION_OUTPUTPOSITION_CHANGE = "dbstar.intent.action.OUTPUTPOSITION_CHANGE";
	public final static String ACTION_OUTPUTPOSITION_CANCEL = "dbstar.intent.action.OUTPUTPOSITION_CANCEL";
	public final static String ACTION_OUTPUTPOSITION_SAVE = "dbstar.intent.action.OUTPUTPOSITION_SAVE";
	public final static String ACTION_OUTPUTPOSITION_DEFAULT_SAVE = "dbstar.intent.action.OUTPUTPOSITION_DEFAULT_SAVE";
	
	public final static String ACTION_OUTPUTMODE_SAVE = "dbstar.intent.action.OUTPUTMODE_SAVE";
	public final static String ACTION_OUTPUTMODE_CHANGE = "dbstar.intent.action.OUTPUTMODE_CHANGE";
	public final static String ACTION_OUTPUTMODE_CANCEL = "dbstar.intent.action.OUTPUTMODE_CANCEL";
	public final static String ACTION_DISP_CHANGE = "dbstar.intent.action.DISP_CHANGE";
	public final static String ACTION_REALVIDEO_ON = "dbstar.intent.action.REALVIDEO_ON";
	public final static String ACTION_REALVIDEO_OFF = "dbstar.intent.action.REALVIDEO_OFF";
	public final static String ACTION_VIDEOPOSITION_CHANGE = "dbstar.intent.action.VIDEOPOSITION_CHANGE";
	
	public static final String STR_OUTPUT_VAR = "ubootenv.var.outputmode";
	public static final String STR_CVBS_VAR = "ubootenv.var.cvbsmode";
	public static final String STR_OUTPUT_MODE = "ubootenv.var.outputmode";
	public static final String STR_DEFAULT_FREQUENCY_VAR = "ubootenv.var.defaulttvfrequency";
	public static final String STR_1080SCALE="ro.platform.has.1080scale";
	public static final String KEY_SELECTED_ITEM = "SelectedItemPosition";
	public static final String OUTPUT_MODE = "output_mode";
	
	public static final String KeySetMode = "set_mode";
	public static final String KeyFrequency = "frequency";
	public static final String KeyCVBSMode= "cvbs_mode";
	
	public static final int GET_USER_OPERATION = 1;
	
	
	public static final int PAGE_GATEWAY = 0;
	public static final int PAGE_CHANNELSELECTOR = 1;
	public static final int PAGE_ETHERNET = 2;
	public static final int PAGE_WIFI = 3;
	public static final int PAGE_ETHERNET2 = 4;
	public static final int PAGE_FINISH = 5;
	
	public static final String AppBG_Uri = "AppBG";
}
