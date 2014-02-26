#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <pthread.h>

#include "common.h"
#include "sqlite3.h"
#include "sqlite.h"
#include "mid_push.h"
#include "porting.h"
#include "smarthome_shadow/smarthome.h"
#include "smarthome_shadow/serial.h"

static char s_smarthome_ctrl_result[64];
static char s_smarthome_gw_sn[32] = {0};

int smarthome_reset()
{
	char sqlite_cmd[512];
	
	sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO global VALUES('SmarthomeServerIP','%q');", SMARTPOWER_SERVER_IP);
	smarthome_setting_reset(sqlite_cmd);
	
	sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO global VALUES('SmarthomeServerPort','%d');", SMARTPOWER_SERVER_PORT);
	smarthome_setting_reset(sqlite_cmd);
	
	sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO global VALUES('SmartLifeIP','%q');", SMARTPOWER_SERVER_IP);
	smarthome_setting_reset(sqlite_cmd);
	
	sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO global VALUES('SmartLifePort','%d');",SMARTLIFE_SERVER_PORT);
	smarthome_setting_reset(sqlite_cmd);
	
	smarthome_gw_sn_save();
	
	return 0;
}


/*
功能：	计算串口命令串校验位，截取16进制的后两位
输入：	serialcmd	——待计算的串口命令串
		num			——待计算命令串长度
返回：	0——失败；others——计算的校验位
*/
static unsigned int serialcmd_checksum(unsigned char *serialcmd, int num)
{
	if(num <= 0){
		DEBUG("can not do checksum for %d element\n", num);
		return 0;
	}

	unsigned int l_cs=0;
	int i = 0;
	for(i=0;i<num;i++)
	{
		l_cs+=serialcmd[i];
	}
	return (l_cs & 0xff);
}



/*
功能：	拼接串口命令串，只针对智能插座
*/
static int smart_socket_serial_cmd_splice(unsigned char *serial_cmd, unsigned int cmd_size, SMART_SOCKET_ACTION_E socket_action, char *socket_id)
{
	if(NULL==serial_cmd || 0>=cmd_size){
		DEBUG("can not splice smart socket serial cmd because of null buf\n");
		return -1;
	}
	if(NULL==socket_id || 12!=strlen(socket_id)){
		DEBUG("socket id is invalid\n");
		return -1;
	}
	if(cmd_size<12){
		DEBUG("size of serial cmd buffer is too short: %d\n", cmd_size);
		return -1;
	}

	int ret = 0;
	int index = 0;
	serial_cmd[index++] = 0x68;
	serial_cmd[index++] = appoint_str2int(socket_id, 12, 0, 2, 16);
	serial_cmd[index++] = appoint_str2int(socket_id, 12, 2, 2, 16);
	serial_cmd[index++] = appoint_str2int(socket_id, 12, 4, 2, 16);
	serial_cmd[index++] = appoint_str2int(socket_id, 12, 6, 2, 16);
	serial_cmd[index++] = appoint_str2int(socket_id, 12, 8, 2, 16);
	serial_cmd[index++] = appoint_str2int(socket_id, 12, 10, 2, 16);
	serial_cmd[index++] = 0x68;
	switch(socket_action){
		case SMART_SOCKET_ACTIVE_POWER_CONSUMPTION_READ:	// 读当前有功总电量
			// 68 a0 a1 a2 a3 a4 a5 68 01 02 43 C3 cs 16
			if(cmd_size<14){
				DEBUG("length of serial cmd buffer is too short: %d\n", cmd_size);
				ret = -1;
			}
			else{
				serial_cmd[index++] = 0x01;
				serial_cmd[index++] = 0x02;
				serial_cmd[index++] = 0x43;
				serial_cmd[index++] = 0xC3;
			}
			break;
		case SMART_SOCKET_VOLTAGE_READ:				// 读电压
			// 68 a0 a1 a2 a3 a4 a5 68 01 02 44 e9 cs 16
			if(cmd_size<14){
				DEBUG("length of serial cmd buffer is too short: %d\n", cmd_size);
				ret = -1;
			}
			else{
				serial_cmd[index++] = 0x01;
				serial_cmd[index++] = 0x02;
				serial_cmd[index++] = 0x44;
				serial_cmd[index++] = 0xE9;
			}
			break;
		case SMART_SOCKET_POWER_CURRENT_READ:		// 读电流
			// 68 a0 a1 a2 a3 a4 a5 68 01 02 54 e9 cs 16
			if(cmd_size<14){
				DEBUG("length of serial cmd buffer is too short: %d\n", cmd_size);
				ret = -1;
			}
			serial_cmd[index++] = 0x01;
			serial_cmd[index++] = 0x02;
			serial_cmd[index++] = 0x54;
			serial_cmd[index++] = 0xE9;
			break;
		case SMART_SOCKET_ACTIVE_POWER_READ:		// 有功功率
			// 68 a0 a1 a2 a3 a4 a5 68 01 02 63 e9 cs 16
			if(cmd_size<14){
				DEBUG("length of serial cmd buffer is too short: %d\n", cmd_size);
				ret = -1;
			}
			else{
				serial_cmd[index++] = 0x01;
				serial_cmd[index++] = 0x02;
				serial_cmd[index++] = 0x63;
				serial_cmd[index++] = 0xE9;
			}
			break;
		case SMART_SOCKET_REACTIVE_POWER_READ:		// 无功功率
			// 68 a0 a1 a2 a3 a4 a5 68 01 02 73 e9 cs 16
			if(cmd_size<14){
				DEBUG("length of serial cmd buffer is too short: %d\n", cmd_size);
				ret = -1;
			}
			else{
				serial_cmd[index++] = 0x01;
				serial_cmd[index++] = 0x02;
				serial_cmd[index++] = 0x73;
				serial_cmd[index++] = 0xE9;
			}
			break;
		case SMART_SOCKET_POWER_COEFFICIENT_READ:	// 功率因数
			// 68 a0 a1 a2 a3 a4 a5 68 01 02 83 e9 cs 16
			if(cmd_size<14){
				DEBUG("length of serial cmd buffer is too short: %d\n", cmd_size);
				ret = -1;
			}
			else{
				serial_cmd[index++] = 0x01;
				serial_cmd[index++] = 0x02;
				serial_cmd[index++] = 0x83;
				serial_cmd[index++] = 0xE9;
			}
			break;
		case SMART_SOCKET_RELAY_DISCONNECT:			// 继电器断开
			// 68 a0 a1 a2 a3 a4 a5 68 04 09 55 16  33 33 33 33  33 33 33  cs 16
			if(cmd_size<21){
				DEBUG("length of serial cmd buffer is too short: %d\n", cmd_size);
				ret = -1;
			}
			else{
				serial_cmd[index++] = 0x04;
				serial_cmd[index++] = 0x09;
				serial_cmd[index++] = 0x55;
				serial_cmd[index++] = 0x16;
				serial_cmd[index++] = 0x33;
				serial_cmd[index++] = 0x33;
				serial_cmd[index++] = 0x33;
				serial_cmd[index++] = 0x33;
				serial_cmd[index++] = 0x33;
				serial_cmd[index++] = 0x33;
				serial_cmd[index++] = 0x33;
			}
			break;
		case SMART_SOCKET_RELAY_CONNECT:			// 继电器闭合
			// 68 a0 a1 a2 a3 a4 a5 68 04 09 56 16  33 33 33 33  44 44 44  cs 16
			if(cmd_size<21){
				DEBUG("length of serial cmd buffer is too short: %d\n", cmd_size);
				ret = -1;
			}
			else{
				serial_cmd[index++] = 0x04;
				serial_cmd[index++] = 0x09;
				serial_cmd[index++] = 0x56;
				serial_cmd[index++] = 0x16;
				serial_cmd[index++] = 0x33;
				serial_cmd[index++] = 0x33;
				serial_cmd[index++] = 0x33;
				serial_cmd[index++] = 0x33;
				serial_cmd[index++] = 0x44;
				serial_cmd[index++] = 0x44;
				serial_cmd[index++] = 0x44;
			}
			break;
		case SMART_SOCKET_RELAY_STATUS_READ:		// 继电器状态
			// 68 a0 a1 a2 a3 a4 a5 68 01 02 93 E9 cs 16
			if(cmd_size<14){
				DEBUG("length of serial cmd buffer is too short: %d\n", cmd_size);
				ret = -1;
			}
			else{
				serial_cmd[index++] = 0x01;
				serial_cmd[index++] = 0x02;
				serial_cmd[index++] = 0x93;
				serial_cmd[index++] = 0xE9;
			}
			break;
		case SMART_SOCKET_ADDR_CONFIRM:				// 地址确认
			// 68 a0 a1 a2 a3 a4 a5 68 07 00 cs 16
			if(cmd_size<12){
				DEBUG("length of serial cmd buffer is too short: %d\n", cmd_size);
				ret = -1;
			}
			else{
				serial_cmd[index++] = 0x07;
				serial_cmd[index++] = 0x00;
			}
			break;
		default:
			DEBUG("can not support this action of smart socket\n");
			ret = -1;
			break;
	}

	if(-1!=ret){
		serial_cmd[index] = serialcmd_checksum(serial_cmd, index);
		index++;
		serial_cmd[index++] = 0x16;

		int i = 0;
		DEBUG("splice serial cmd(len=%d):", index);
		for(i=0; i<index; i++)
			printf(" %02x", serial_cmd[i]);
		printf("\n");
		ret = index;
	}
	else
		DEBUG("serial cmd splice failed\n");

	return ret;
}


static int socket_relay_status_trans(int origine_value, double *result)
{
	int ret = 0;
	
	DEBUG("origine status value=%d=0x%02x\n", origine_value, origine_value);
	if(0x55==(origine_value-0x33))
		*result = SMART_SOCKET_RELAY_STATUS_OFF;
	else if(0x56==(origine_value-0x33))
		*result = SMART_SOCKET_RELAY_STATUS_ON;
	else if(0x57==(origine_value-0x33))
		*result = SMART_SOCKET_RELAY_STATUS_UNKNOWN;
	else{
		DEBUG("can not distinguish this status: 0x%02x\n", origine_value);
		ret = -1;
	}
	
	return ret;
}


static int bcdChange(unsigned char input)
{
	return ((input>>4)*10+(input&0x0f));
}

/*
 返回值：
 -1：指令解析彻底失败，无法继续解析
 -2：指令解析的结果没有达到最佳预期（比如，期望解析得到电压值，但是解析的结果是通信失败），但应当继续解析
*/
static int smart_socket_serial_cmd_parse_son(unsigned char *serial_cmd, unsigned int cmd_len, SMART_SOCKET_ACTION_E socket_action, char *socket_id, double *result)
{
	if(NULL==serial_cmd || 0>=cmd_len){
		DEBUG("can not splice smart socket serial cmd because of null buf\n");
		return -1;
	}
	
	if(cmd_len<SERIAL_RESPONSE_LEN_MIN)	/*  || cmd_len>SERIAL_RESPONSE_LEN_MAX */
	{
		DEBUG("length of serial cmd is too short%d\n", cmd_len);
		return -1;
	}

	unsigned int i = 0;
	DEBUG("splice serial cmd(len=%d):", cmd_len);
	for(i=0; i<cmd_len; i++)
		printf(" %02x", serial_cmd[i]);
	printf("\n");
	
	if(0x68!=serial_cmd[0] || 0x68!=serial_cmd[7])	/*  || 0x16!=serial_cmd[cmd_len-1] */
	{
		DEBUG("this cmd has invalid header and tailer signal\n");
		return -2;
	}

	// 68 a0 a1 a2 a3 a4 a5 68 C4 01 XX cs 16		指令错误
	// 68 a0 a1 a2 a3 a4 a5 68 C5 03 xx xx xx cs 16	通讯失败
	if(0xC4==serial_cmd[8] && 0x01==serial_cmd[9]){
		socket_action = SMART_SOCKET_INSTRUCTION_INVALID;
		DEBUG("serial operation error, SMART_SOCKET_INSTRUCTION_INVALID\n");
	}
	else if(0xC5==serial_cmd[8] && 0x03==serial_cmd[9]){
		socket_action = SMART_SOCKET_COMMUNICATION_FAILD;
		DEBUG("serial operation error, SMART_SOCKET_COMMUNICATION_FAILD\n");
	}

	char socket_addr[32];
	memset(socket_addr, 0, sizeof(socket_addr));
	snprintf(socket_addr, sizeof(socket_addr), "%02x%02x%02x%02x%02x%02x", serial_cmd[1],serial_cmd[2],serial_cmd[3],serial_cmd[4],serial_cmd[5],serial_cmd[6]);
	if(strcmp(socket_addr, socket_id)){
		DEBUG("socket id can not match: id in cmd is %s, but I want %s\n", socket_addr, socket_id);
		return -2;
	}

	int ret = 0;
	switch(socket_action){
		case SMART_SOCKET_ACTIVE_POWER_CONSUMPTION_READ:	// 读当前有功总电量
			// 68 a0 a1 a2 a3 a4 a5 68 81 06 43 C3 xx xx xx xx cs 16
			if(serial_cmd[16]!=serialcmd_checksum(serial_cmd, 16)){
				DEBUG("check sum failed\n");
				ret = -2;
			}
			else{
				*result = bcdChange(serial_cmd[14]-0x33)*100+bcdChange(serial_cmd[13]-0x33)*1+bcdChange(serial_cmd[12]-0x33)*0.01;
			}
			break;
		case SMART_SOCKET_VOLTAGE_READ:				// 读电压
			// 68 a0 a1 a2 a3 a4 a5 68 81 04 44 E9 xx xx cs 16
			if(serial_cmd[14]!=serialcmd_checksum(serial_cmd, 14)){
				DEBUG("check sum failed\n");
				ret = -2;
			}
			else{
				*result = bcdChange(serial_cmd[13]-0x33)*100+bcdChange(serial_cmd[12]-0x33)*1;
			}
			break;
		case SMART_SOCKET_POWER_CURRENT_READ:		// 读电流
			// 68 a0 a1 a2 a3 a4 a5 68 81 04 54 E9 xx xx cs 16
			if(serial_cmd[14]!=serialcmd_checksum(serial_cmd, 14)){
				DEBUG("check sum failed\n");
				ret = -2;
			}
			else{
				*result = bcdChange(serial_cmd[13]-0x33)*1+bcdChange(serial_cmd[12]-0x33)*0.01;
			}
			break;
		case SMART_SOCKET_ACTIVE_POWER_READ:		// 有功功率
			// 68 a0 a1 a2 a3 a4 a5 68 81 05 63 E9 xx xx xx cs 16
			if(serial_cmd[15]!=serialcmd_checksum(serial_cmd, 15)){
				DEBUG("check sum failed\n");
				ret = -2;
			}
			else{
				*result = bcdChange(serial_cmd[14]-0x33)*1+bcdChange(serial_cmd[13]-0x33)*0.01+bcdChange(serial_cmd[12]-0x33)*0.0001;
			}
			break;
		case SMART_SOCKET_REACTIVE_POWER_READ:		// 无功功率
			// 68 a0 a1 a2 a3 a4 a5 68 81 04 73 E9 xx xx cs 16
			if(serial_cmd[14]!=serialcmd_checksum(serial_cmd, 14)){
				DEBUG("check sum failed\n");
				ret = -2;
			}
			else{
				*result = bcdChange(serial_cmd[13]-0x33)*1+bcdChange(serial_cmd[12]-0x33)*0.01;
			}
			break;
		case SMART_SOCKET_POWER_COEFFICIENT_READ:	// 功率因数
			// 68 a0 a1 a2 a3 a4 a5 68 81 04 83 E9 xx xx cs 16
			if(serial_cmd[14]!=serialcmd_checksum(serial_cmd, 14)){
				DEBUG("check sum failed\n");
				ret = -2;
			}
			else{
				*result = bcdChange(serial_cmd[13]-0x33)*0.01+bcdChange(serial_cmd[12]-0x33)*0.0001;
			}
			break;
		case SMART_SOCKET_RELAY_DISCONNECT:			// 继电器断开
			// 68 a0 a1 a2 a3 a4 a5 68 84 02 88 xx cs 16
			if(serial_cmd[12]!=serialcmd_checksum(serial_cmd, 12)){
				DEBUG("check sum failed\n");
				ret = -2;
			}
			else{
				ret = socket_relay_status_trans(serial_cmd[11], result);
			}
			break;
		case SMART_SOCKET_RELAY_CONNECT:			// 继电器闭合
			// 68 a0 a1 a2 a3 a4 a5 68 84 02 89 xx cs 16
			if(serial_cmd[12]!=serialcmd_checksum(serial_cmd, 12)){
				DEBUG("check sum failed\n");
				ret = -2;
			}
			else{
				ret = socket_relay_status_trans(serial_cmd[11], result);
			}
			break;
		case SMART_SOCKET_RELAY_STATUS_READ:		// 继电器状态
			// 68 a0 a1 a2 a3 a4 a5 68 81 03 93 E9 xx cs 16
			if(serial_cmd[13]!=serialcmd_checksum(serial_cmd, 13)){
				DEBUG("check sum failed\n");
				ret = -2;
			}
			else{
				ret = socket_relay_status_trans(serial_cmd[12], result);
			}
			break;
		case SMART_SOCKET_ADDR_CONFIRM:				// 地址确认
			// 68 a0 a1 a2 a3 a4 a5 68 87 06 33 33 33 33 33 33 cs 16
			if(serial_cmd[16]!=serialcmd_checksum(serial_cmd, 16)){
				DEBUG("check sum failed\n");
				ret = -2;
			}
			else{
				*result = 1;
			}
			break;
		case SMART_SOCKET_INSTRUCTION_INVALID:		// 指令错误
			// 68 a0 a1 a2 a3 a4 a5 68 C4 01 XX cs 16
			if(serial_cmd[10]!=serialcmd_checksum(serial_cmd, 10)){
				DEBUG("check sum failed\n");
				ret = -2;
			}
			else{
				*result = serial_cmd[11];
				DEBUG("serial instruction invalide, err code: 0x%02x\n", (int)(*result));
				ret = -2;
			}
			break;
		case SMART_SOCKET_COMMUNICATION_FAILD:		// 通讯失败
			// 68 a0 a1 a2 a3 a4 a5 68 C5 03 xx xx xx cs 16
			if(serial_cmd[13]!=serialcmd_checksum(serial_cmd, 13)){
				DEBUG("check sum failed\n");
				ret = -2;
			}
			else{
				*result = (serial_cmd[10]<<16) | (serial_cmd[11]<<8) | serial_cmd[12];
				DEBUG("serial control communication failed, err code: 0x%02x 0x%02x 0x%02x\n", serial_cmd[10], serial_cmd[11], serial_cmd[12]);
				ret = -2;
			}
			break;
		default:
			DEBUG("can not support this action of smart socket\n");
			ret = -2;
			break;
	}
	
	return ret;
}

/*
功能：	解析串口命令串，只针对智能插座
注意：	目前存在这样的情况，串口返回的命令串重复了多遍，需要能兼容这种情况，形如下面的结果是读取有功功率时一次返回的：
		68 20 11 12 21 06 36 68 81 05 63 e9 33 33 33 db 16 68 20 11 12 21 06 36 68 c5 03 e9 01 63 85 16 68 20 11 06 00 41 56 68 81 06 43 c3 3c 33 33 33 00 16
*/
static int smart_socket_serial_cmd_parse(unsigned char *serial_cmd, unsigned int cmd_len, SMART_SOCKET_ACTION_E socket_action, char *socket_id, double *result)
{
	unsigned char *serial_cmd_son = serial_cmd;
	unsigned int cmd_len_son = cmd_len;
	
	unsigned int i = 0;
	int ret = 0;
	
	for(i=0;i<(cmd_len-SERIAL_RESPONSE_LEN_MIN);i++){
		if(0x68==*(serial_cmd_son+i) && 0x68==*(serial_cmd_son+7+i)){
			DEBUG("catch valid cmd head,i=%d\n",i);
			ret = smart_socket_serial_cmd_parse_son(serial_cmd_son+i, cmd_len_son-i, socket_action, socket_id, result);
			if(-1==ret){
				DEBUG("cmd parse failed thoroughly\n");
				break;
			}
			else if(0==ret){
				DEBUG("cmd parse successfully\n");
				break;
			}
			else if(-2==ret){
				DEBUG("this parse failed, but will try again\n");
			}
			else
				DEBUG("look this return value: %d, what a fucking meaning\n", ret);
		}
	}
	DEBUG("parse action finished\n");
	
//	if(0!=ret)
//		ret = -1;
		
	return ret;
}


// e.g.: *buf is 201112210636\t6, it means cut off 201112210636
int smarthome_ctrl(char **buf, int *len)
{	
	if(*buf==NULL || *len<3){
		DEBUG("invalid smarthome ctrl command\n");
		return -1;
	}
	
	char *tab_char = strchr(*buf,'\t');
	if(NULL==tab_char){
		DEBUG("invalid command format: %s\n", *buf);
		return -1;
	}
	
	*tab_char = '\0';
	tab_char++;
	
	int ret = -1;
	char socket_id[32];
	SMART_SOCKET_ACTION_E smart_socket_action = SMART_SOCKET_ACTION_UNDEFINED;
	unsigned char serial_cmd[128];
	
	snprintf(socket_id,sizeof(socket_id),"%s",*buf);
	smart_socket_action = atoi(tab_char);
	DEBUG("serial cmd to RF433, socket_id=%s,smart_socket_action=%d\n",socket_id,smart_socket_action);
	
	memset(serial_cmd, 0, sizeof(serial_cmd));
	
	int serial_cmd_len = smart_socket_serial_cmd_splice(serial_cmd,sizeof(serial_cmd), smart_socket_action,socket_id);
	if(serial_cmd_len>0)
	{
		if(-1==serial_int()){
			DEBUG("serial module init failed\n");
			ret = -1;
		}
		else{
			int recv_serial_len = serial_access(serial_cmd, serial_cmd_len, sizeof(serial_cmd));
			if(recv_serial_len>0)
			{
				double result = 0.0;
				ret = smart_socket_serial_cmd_parse(serial_cmd, recv_serial_len, smart_socket_action, socket_id, &result);
				DEBUG("serial cmd parse result: %d\n", ret);
				if(SMART_SOCKET_RELAY_STATUS_OFF==ret || SMART_SOCKET_RELAY_STATUS_ON==ret){
					ret = 0;
				}
				else
					ret = -1;
			}
			else{
				DEBUG("serial access failed, recv_serial_len=%d\n", recv_serial_len);
				ret = -1;
			}
			/*
			测试代码，应用启动一次只执行一组串口write-read操作，到此完毕；
			为了及时释放串口，便于再次执行，此处应关闭
			*/
			serial_fd_close( );
		}
	}
	else{
		DEBUG("serial cmd splice failed, serial_cmd_len=%d\n", serial_cmd_len);
		ret = -1;
	}
	
	if(0==ret)
		snprintf(s_smarthome_ctrl_result,sizeof(s_smarthome_ctrl_result),"1");
	else
		snprintf(s_smarthome_ctrl_result,sizeof(s_smarthome_ctrl_result),"0");
	
	*buf = s_smarthome_ctrl_result;
	*len = strlen(s_smarthome_ctrl_result);
	
	return ret;
}

int smarthome_gw_sn_set(char *sm_gw_sn)
{
	if(NULL==sm_gw_sn){
		DEBUG("can not set NULL as smarthome gateway sn\n");
		memset(s_smarthome_gw_sn,0,sizeof(s_smarthome_gw_sn));
		return -1;
	}
	else{
		snprintf(s_smarthome_gw_sn,sizeof(s_smarthome_gw_sn),"%s",sm_gw_sn);
		DEBUG("set %s as smarthome gateway sn\n", s_smarthome_gw_sn);
		return 0;
	}
}

int smarthome_gw_sn_save()
{
	char sqlite_cmd[512];
	
	if(strlen(s_smarthome_gw_sn)>12){
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO global VALUES('SmarthomeSN','%q');", s_smarthome_gw_sn);
		smarthome_setting_reset(sqlite_cmd);
	}
	return 0;
}

void smarthome_gw_sn_init()
{
	memset(s_smarthome_gw_sn,0,sizeof(s_smarthome_gw_sn));
	
	return;
}

void smarthome_sn_init_when_network_init()
{
	// 如果是首次开机，/data/data/com.dbstar/files/flag文件还不存在，此时覆盖国电网关序列号
	// 如果生产完毕的终端，由于特别的原因需要修改设备中recovery记录的国电网关序列号，此时覆盖国电网关序列号
	if(0==network_init_status() || 1==device_num_changed()){
		DEBUG("reset smarthome sn from recovery to db\n");
		smarthome_gw_sn_save();
		
		remove_force(DEVICE_NUM_CHANGED_FLAG);
		DEBUG("clear %s\n",DEVICE_NUM_CHANGED_FLAG);
	}
}

