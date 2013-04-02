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
	public static final int CMD_NETWORK_GETINFO             = 0x00012;
	
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
	
	public static final int DRM_SC_INSERT_OK				= 0x20100;    // DRM smartcard Insert OK
	public static final int DRM_SC_INSERT_FAILED			= 0x20101;    // DRM smartcard Insert failed
	public static final int DRM_SC_REMOVE_OK				= 0x20102;    // DRM smartcard Remove OK
	public static final int DRM_SC_REMOVE_FAILED			= 0x20103;    // DRM smartcard Remove failed
	public static final int DRM_EMAIL_NEW                   = 0x20104;
	public static final int	DRM_OSD_SHOW                    = 0x20107;
	public static final int	DRM_OSD_HIDE                    = 0x20108;

	public static final int TDT_TIME_SYNC                   = 0x30017;    // Time format: 2012-12-22 18:03:14

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
	
	public static final int DIALOG_NOTICE                   = 0x20000; // Update times info
	public static final int	UPGRADE_NEW_VER                 = 0x20001;
	public static final int	UPGRADE_NEW_VER_FORCE           = 0x20002;
	public static final int	UPGRADE_START                   = 0x20003;
	public static final int	UPGRADE_PERCENT                 = 0x20004;
	public static final int	UPGRADE_SUCCESS                 = 0x21000;
	public static final int	UPGRADE_FAILED                  = 0x21001;
	
	public static final int	CMD_DRM_SC_SN_READ              = 0x00064;
	public static final int	CMD_DRMLIB_VER_READ             = 0x00065;
	public static final int	CMD_DRM_SC_EIGENVALUE_READ      = 0x00066;
	public static final int	CMD_DRM_ENTITLEINFO_READ        = 0x00067;
	public static final int	CMD_DRM_ENTITLEINFO_OUTPUT      = 0x00068;
	public static final int	CMD_DRM_ENTITLEINFO_INPUT       = 0x00069;
	public static final int	CMD_DRM_EMAILHEADS_READ         = 0x0006a;
	public static final int	CMD_DRM_EMAILCONTENT_READ       = 0x0006b;
	public static final int	CMD_DRM_PURCHASEINFO_READ       = 0x0006d;
	
	public static final String CA_NO_ENTITLE = "NO_ENTITLE";
	public static final String CA_NO_DEVICE = "NO_DEVICE";
	public static final String CA_NOT_ENOUGH_SPACE = "NOT_ENOUGH_SPACE";
	public static final String CA_ENTITLE_OUTPUT_FINISH = "ENTITLE_OUTPUT_FINISH";
	public static final String CA_ENTITLE_INPUT_INTERRUPT = "ENTITLE_INPUT_INTERRUPT";
	public static final String CA_ENTITLE_INPUT_FINISH = "ENTITLE_INPUT_FINISH";
}
