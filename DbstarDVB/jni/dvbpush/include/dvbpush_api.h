
#ifndef __DVBPUSH_API_H__
#define __DVBPUSH_API_H__

typedef enum {
	DBSTAR_COMMAND                  = 0x00000,
	DBSTAR_NOTIFY                   = 0xF0000,

	CMD_NETWORK_DISCONNECT	        = 0x00010,    // 网络断开
	CMD_NETWORK_CONNECT             = 0x00011,    // 网络恢复连接
	
	CMD_DISK_MOUNT                  = 0x00021,    // 硬盘插上可用
	CMD_DISK_UNMOUNT                = 0x00022,    // 硬盘拔掉
	CMD_DISK_FOREWARNING			= 0x00023,    // 硬盘到达预警空间
		
	CMD_DVBPUSH_GETINFO_START       = 0x00031,    // 开始获取push下载状态
	CMD_DVBPUSH_GETINFO				= 0x00032,    // 获取push下载状态
	CMD_DVBPUSH_GETINFO_STOP        = 0x00033,    // 停止获取push下载状态
	
	CMD_UPGRADE_CANCEL              = 0x00041,    // 用户取消升级
	CMD_UPGRADE_CONFIRM             = 0x00042,    // 用户确认升级
	CMD_UPGRADE_TIMEOUT             = 0x00043,    // 用户操作对话框超时
	
	CMD_PUSH_SELECT                 = 0x00051,    // 用户从“选择接收”页面退出，选择完毕。
	CMD_MAX                         = 0x0FFFF,

	MSG_MARQUEE                     = 0x10000,    // 跑马灯
	MSG_UPGRADE                     = 0x20000,    // 升级成功
	MSG_STATUS						= 0x30000,    // 信息状态提示
	MSG_ERROR                       = 0x40000,    // 错误提示

	STATUS_DVBPUSH_INIT_FAILED      = 0x30010,    // dvbpush初始化失败
	STATUS_DVBPUSH_INIT_SUCCESS     = 0x30011,    // dvbpush初始化成功
	STATUS_DATA_SIGNAL_ON			= 0x30012,    // 信号正常，即有ts流
	STATUS_DATA_SIGNAL_OFF			= 0x30013,    // 无信号，即无ts流
	
	UPGRADE_NEW_VER                 = 0x20001,     // 有新版本到来，用户选择升级
	UPGRADE_NEW_VER_FORCE           = 0x20002,     // 有新版本到来，强制升级
	UPGRADE_START                   = 0x20003,     // 开始升级
	UPGRADE_PERCENT                 = 0x20004,     // 升级进度百分比
	UPGRADE_SUCCESS                 = 0x21000,     // 升级成功
	UPGRADE_FAILED                  = 0x21001      // 升级失败
}DBSTAR_CMD_MSG_E;

typedef int (* dvbpush_notify_t)(int type, char *msg, int len);


/********************************************************
 功能：模块标准接口，初始化
 返回：0——成功；-1——失败
********************************************************/
int dvbpush_init();


/********************************************************
 功能：模块标准接口，反初始化
 返回：0——成功；-1——失败
********************************************************/
int dvbpush_uninit();



/********************************************************
 功能：UI向底层发送命令
 返回：0——成功；-1——失败
********************************************************/
int dvbpush_command(int cmd, char **buf, int *len);



/********************************************************
 功能：底层提供给UI使用，UI可以调用此函数设置回调（func），底层需要向上层发送消息时，可以通过调用回调（func）实现。
********************************************************/
int dvbpush_register_notify(void *func);

#endif
