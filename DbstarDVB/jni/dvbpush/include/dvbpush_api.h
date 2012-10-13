
#ifndef __DVBPUSH_API_H__
#define __DVBPUSH_API_H__

/*
 小于0x1000的信息属于全局信息，不属于某个模块。
 属于模块的信息由四个16进制数字组成，前两位标识模块，后两位区分消息。
*/

typedef enum{
	DBSTAR_JNI_CMD_MIN					= -1,
	
	DBSTAR_JNI_CMD_NETWORK_DISCONNECT	= 0x10,		// 网络断开
	DBSTAR_JNI_CMD_NETWORK_CONNECT		= 0x11,		// 网络恢复连接
	
	DBSTAR_JNI_CMD_HD_UNMOUNT			= 0x20,		// 硬盘拔掉
	DBSTAR_JNI_CMD_HD_MOUNT				= 0x21,		// 硬盘插上可用
	
	DVBPUSH_GETINFO_STOP				= 0x1000,	// 停止获取push下载状态
	DVBPUSH_GETINFO_START				= 0x1001, 	// 开始获取push下载状态
	DVBPUSH_GETINFO						= 0x1002, 	// 获取push下载状态
	
	UPGRADE_CANCEL						= 0x1100,	// 用户取消升级
	UPGRADE_CONFIRM						= 0x1101,	// 用户确认升级
	UPGRADE_TIMEOUT						= 0x1102,	// 对用操作对话框超时
	
	PUSH_REJECT							= 0x1200,	 // 用户拒绝接收的成品Publication
	
	DBSTAR_JNI_CMD_MAX
}DBSTAR_JNI_CMD_E;


typedef enum{
	DBSTAR_JNI_NTF_MIN					= -1,
	
	DBSTAR_DVBPUSH_INIT_FAILED			= 0x10,		// dvbpush初始化失败
	DBSTAR_DVBPUSH_INIT_SUCCESS			= 0x11,		// dvbpush初始化成功
	
	DBSTAR_MARQUEE						= 0x100,	// 跑马灯
	DBSTAR_ERROR						= 0x101,	// 错误提示
	DBSTAR_STATUS						= 0x102,	// 信息状态提示
	
	UPGRADE_NEW_VER_FORCE				= 0x1100,	// 有新版本到来，强制升级
	UPGRADE_NEW_VER						= 0x1101,	// 有新版本到来，用户选择升级
	UPGRADE_START						= 0x1102,	// 开始升级
	UPGRADE_PERCENT						= 0x1103,	// 升级进度百分比
	UPGRADE_FAILED						= 0x1104,	// 升级失败
	UPGRADE_SUCCESS						= 0x1105,	// 升级成功
	
	DBSTAR_JNI_NTF_MAX
}DBSTAR_JNI_NTF_E;

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
