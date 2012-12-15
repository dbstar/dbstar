package com.dbstar.DbstarDVB;

public class DbstarServiceApi {
	
	public static final String ACTION_NOTIFY = "com.dbstar.DbstarDVB.NOTIFY";
	public static final String ACTION_HDMI_IN = "com.dbstar.DbstarDVB.HDMI_IN";
	public static final String ACTION_HDMI_OUT = "com.dbstar.DbstarDVB.HDMI_OUT";
	public static final String ACTION_SMARTCARD_IN = "com.dbstar.DbstarDVB.SMARTCARD_IN";
	public static final String ACTION_SMARTCARD_OUT = "com.dbstar.DbstarDVB.SMARTCARD_OUT";
	
	
	/* send command from UI to server */
	public static final int DBSTAR_COMMAND                  = 0x00000;
	/* receive notification from server to UI */
	public static final int DBSTAR_NOTIFY                   = 0xF0000;

	/* COMMAND */
	public static final int CMD_NETWORK_DISCONNECT	        = 0x00010;
	public static final int CMD_NETWORK_CONNECT             = 0x00011;
	
	public static final int CMD_DISK_MOUNT                  = 0x00021;
	public static final int CMD_DISK_UNMOUNT                = 0x00022;
	public static final int CMD_DISK_FOREWARNING            = 0x00023;

	public static final int CMD_DVBPUSH_GETINFO_START       = 0x00031;
	public static final int CMD_DVBPUSH_GETINFO				= 0x00032;
	public static final int CMD_DVBPUSH_GETINFO_STOP        = 0x00033;
	public static final int CMD_DVBPUSH_GETTS_STATUS        = 0x00034;
	
	public static final int CMD_UPGRADE_CANCEL              = 0x00041;
	public static final int CMD_UPGRADE_CONFIRM             = 0x00042;
	public static final int CMD_UPGRADE_TIMEOUT             = 0x00043;
	
	public static final int CMD_PUSH_SELECT                 = 0x00051;

	public static final int CMD_DRM_SC_INSERT               = 0x00061;
	public static final int CMD_DRM_SC_REMOVE               = 0x00062;

	public static final int CMD_MAX                         = 0x0FFFF;

	/* NOTIFY STATUS or ERROR */
	public static final int	MSG_MARQUEE                     = 0x10000;
	public static final int	MSG_UPGRADE                     = 0x20000;
	public static final int	MSG_STATUS						= 0x30000;
	public static final int	MSG_ERROR                       = 0x40000;

	public static final int	STATUS_DVBPUSH_INIT_FAILED      = 0x30010;
	public static final int	STATUS_DVBPUSH_INIT_SUCCESS     = 0x30011;
	public static final int	STATUS_DATA_SIGNAL_ON           = 0x30012;
	public static final int	STATUS_DATA_SIGNAL_OFF          = 0x30013;
	public static final int	STATUS_COLUMN_REFRESH           = 0x30014; // Column has new update
	public static final int	STATUS_PREVIEW_REFRESH          = 0x30015; // Preview has new update
	public static final int	STATUS_INTERFACE_REFRESH        = 0x30016; // UI resource has new update
	
	public static final int	UPGRADE_NEW_VER                 = 0x20001;
	public static final int	UPGRADE_NEW_VER_FORCE           = 0x20002;
	public static final int	UPGRADE_START                   = 0x20003;
	public static final int	UPGRADE_PERCENT                 = 0x20004;
	public static final int	UPGRADE_SUCCESS                 = 0x21000;
	public static final int	UPGRADE_FAILED                  = 0x21001;
}
