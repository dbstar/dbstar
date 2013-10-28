#ifndef __SMARTHOME_H__
#define __SMARTHOME_H__

#define SMARTPOWER_SERVER_IP		"211.160.203.86"		// the ip of baidu.com is "61.135.169.105"
#define	SMARTPOWER_SERVER_PORT		(9999)
#define	SMARTLIFE_SERVER_PORT		(9103)		// for smarthome apk

typedef enum{
	SMART_SOCKET_ACTION_UNDEFINED = -1,
	SMART_SOCKET_ACTIVE_POWER_CONSUMPTION_READ = 0,	// 有功总电量
	SMART_SOCKET_VOLTAGE_READ,											// 电压
	SMART_SOCKET_POWER_CURRENT_READ,								// 电流
	SMART_SOCKET_ACTIVE_POWER_READ,									// 有功功率
	SMART_SOCKET_REACTIVE_POWER_READ,								// 无功功率
	SMART_SOCKET_POWER_COEFFICIENT_READ,						// 功率因数
	SMART_SOCKET_RELAY_DISCONNECT,									// 继电器断开
	SMART_SOCKET_RELAY_CONNECT,											// 继电器闭合
	SMART_SOCKET_RELAY_STATUS_READ,									// 继电器状态
	SMART_SOCKET_ADDR_CONFIRM,											// 地址确认
	SMART_SOCKET_INSTRUCTION_INVALID,								// 指令错误
	SMART_SOCKET_COMMUNICATION_FAILD								// 通讯失败
}SMART_SOCKET_ACTION_E;

typedef enum{
	SMART_SOCKET_RELAY_STATUS_OFF = 0,
	SMART_SOCKET_RELAY_STATUS_ON,
	SMART_SOCKET_RELAY_STATUS_UNKNOWN
}SMART_SOCKET_RALAY_STATUS_E;


#define SERIAL_RECV_RETRY		(16)	// 从串口接收命令尝试的次数，这个值和select的超时时间共同决定了串口接收时的反应速度
#define SERIAL_RESPONSE_LEN_MIN	(12)

// 68 a0 a1 a2 a3 a4 a5 68 01 02 43 C3 cs 16
// 发往串口的命令，长度至少包括：一个开始符68，6个标识socket id，socket id结束符68，校验符cs，指令结束符16
#define SERIAL_CMD_SEND_LEN_MIN	(10)
#define SERIAL_CMD_SEND_LEN_MAX	(64)


int smarthome_reset();
int smarthome_ctrl(char **buf, int *len);
int smarthome_gw_sn_set(char *sm_gw_sn);
void smarthome_gw_sn_init();
int smarthome_gw_sn_save();
void smarthome_sn_init_when_network_init();

#endif
