package com.dbstar.settings.network;

public class NetworkCommon {
	// 1 - has been set
	public static final String FlagFile = "flag";
	
	//1 - ethernet 
	//2 - ethernet and wifi
	public static final String ChannelModeFile = "channel"; 
	public static final String ChannelEthernet = "1";
	public static final String ChannelBoth = "2";
	
	public static final String PREF_NAME_NETWORK = "dbstar.settings.network";
	public static final String KeyChannel = "channel";
	
	public static final String ActionChannelModeChange = "com.dbstar.DbstarSettings.Action.CHANNELMODE_CHANGE";
	public static final String ChannelFile = "/data/dbstar/channel_file";
	public static final String ActionGetEthernetInfo = "com.dbstar.DbstarLauncher.Action.GET_ETHERNETINFO";
	public static final String ActionSetEthernetInfo = "com.dbstar.DbstarLauncher.Action.SET_ETHERNETINFO";
	public static final String KeyEthernetInfo = "ethernet_info";

}
