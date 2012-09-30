#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/socket.h>
#include <time.h>
#include <sys/mman.h>
#include <sys/ioctl.h>
#include <sys/time.h>
#include <unistd.h>
#include <errno.h>
#include <fcntl.h>
#include <semaphore.h>

#include "common.h"
#include "porting.h"
#include "socket.h"
#include "instruction.h"
#include "sqlite.h"
#include "serial.h"
#include "equipment.h"
#include "timing.h"

static SMART_POWER_TIME_S			g_smart_power_time;
static INSTRUCTION_S 				g_insert_insts[INSTRUCTION_INSERT_NUM];
static sem_t						s_sem_insert_insts;
static int fifo_fd = 0;

int instruction_init(void)
{
	DEBUG("instruction module init...\n");
	if( mkfifo(FIFO_2_INSTRUCTION, O_CREAT|O_EXCL)<0 && EEXIST!=errno ){
		ERROROUT("create FIFO_2_INSTRUCTION failed\n");
		return -1;
	}
	else
		DEBUG("mkfifo(FIFO_2_INSTRUCTION) success\n");

	int i = 0;
	if(-1==sem_init(&s_sem_insert_insts, 0, 1)){
		DEBUG("s_sem_insert_insts init failed\n");
		return -1;
	}
	sem_wait(&s_sem_insert_insts);
	for(i=0;i<INSTRUCTION_INSERT_NUM;i++)
		g_insert_insts[i].alterable_flag = -1;
	sem_post(&s_sem_insert_insts);

	g_smart_power_time.difference_time = 0;
	g_smart_power_time.server_time = time(NULL);
	return 0;
}

static int instruction_reset(INSTRUCTION_S* instruction)
{
	instruction->type_id = 0;
	memset(instruction->reserve, 0, sizeof(instruction->reserve));
	instruction->type = INSTRUCTION_UNDEFINED;
	instruction->arg1 = 0;
	instruction->arg2 = 0;
	instruction->alterable_flag = 0;
	memset(instruction->alterable_entity, 0, sizeof(instruction->alterable_entity));
	instruction->index_in_cmds = -1;

	g_smart_power_time.difference_time = 0;
	return 0;
}

/*
功能：	根据不同的指令操作结果（inst_result），填充指令返回串（err_str）
输入：	inst_result	——指令操作结果
		len			——err_str的长度
输出：	err_str		——指令返回串
返回：	0——成功；-1——失败
*/
int alterable_entity_result(INSTRUCTION_RESULT_E inst_result, char *err_str, unsigned int len)
{
	if(NULL==err_str || len<=0){
		DEBUG("arguments have some error\n");
		return -1;
	}
	switch(inst_result)		///if operate time-out
	{
		case RESULT_OK:
			strncpy(err_str, "&00", len);
			break;
		case ERR_TIMEOUT:		// caution
			strncpy(err_str, "#ffffffffff#ff#ffff#ff#", len);
			break;
		case ERR_FORMAT:		// caution
			strncpy(err_str, "&f1", len);
			break;
		case ERR_FORMATPRO:
			strncpy(err_str, "&f1", len);
			break;
		case ERR_SOCKET:
			strncpy(err_str, "&f2", len);
			break;
		case ERR_DATABASE:
			strncpy(err_str, "&f3", len);
			break;
		case ERR_MEMORY:
			strncpy(err_str, "&f4", len);
			break;
		case ERR_SERIAL:
			strncpy(err_str, "&f5", len);
			break;
		default:	// ERR_OTHER
			strncpy(err_str, "&ff", len);
			break;
	}
	
	return 0;
}

/*
功能：	指令解析，将解析的结果填充到结构体instruction中
输入：	str		——待解析的指令串
		str_len	——待解析指令串的长度
输出：	instruction——解析完毕存入的结构体，由调用者提供空间并初始化
返回：	0——成功；-1——失败
*/
static int instruction_parse(char *str, unsigned int str_len, INSTRUCTION_S *instruction)
{
	if(NULL==str || 0==str_len || strlen(str)<strlen("#0000000000#00#0000#00#")){
		DEBUG("some params are invalid\n");
		return -1;
	}
	
	// the first char of str is '#', invoke it
	instruction->type_id = appoint_str2int(str, str_len, 1, 6, 16);
	strncpy(instruction->reserve, str+1+6, 4);
	instruction->type = appoint_str2int(str, str_len, (1+6+4+1), 2, 16);
	instruction->arg1 = appoint_str2int(str, str_len, (1+6+4+1+2+1), 2, 16);
	instruction->arg2 = appoint_str2int(str, str_len, (1+6+4+1+2+1+2), 2, 16);	
	instruction->alterable_flag = appoint_str2int(str, str_len, (1+6+4+1+2+1+2+2+1), 2, 16);
	
	if(NULL!=(str+1+6+4+4+2+2+1+2+1))
		strcpy(instruction->alterable_entity, str+1+6+4+4+2+2+1+2+1);
	
	int i = 0;
	int entity_len = strlen(instruction->alterable_entity);
	for(i=0; i<entity_len; i++)
	{
//		printf("[%c]", instruction->alterable_entity[i]);
		if( '\n'==instruction->alterable_entity[entity_len-1-i] 
				|| '\r'==instruction->alterable_entity[entity_len-1-i] 
				|| '#'==instruction->alterable_entity[entity_len-1-i]){
			instruction->alterable_entity[entity_len-1-i] = '\0';
		}
		else
			break;
	}

	DEBUG("type_id=0x%06x=%d\n", instruction->type_id, instruction->type_id);
	DEBUG("type=0x%02x\n", instruction->type);
	DEBUG("arg1=0x%02x, arg2=0x%02x\n", instruction->arg1, instruction->arg2);
	DEBUG("alterable_flag=0x%02x\n", instruction->alterable_flag);
	DEBUG("alterable_entity=%s\n", instruction->alterable_entity);
	
	return 0;
}

/*
功能：	将指令结构体instruction拼接为字符串，此串用于cmd结构体中返回给服务器
输入：	instruction	——待拼接的指令结构体，其中的信息将返回给服务器
输出：	str			——指保存令内容拼接结果的空间，调用者提供空间并初始化
		str_len		——空间长度
返回：	0——成功；-1——失败
*/
static int instruction_splice(INSTRUCTION_S *instruction, char *str, unsigned int str_len )
{
	snprintf(str, str_len, "#%06x%s#%02x#%02x%02x#",
									instruction->type_id,
									instruction->reserve,
									instruction->type,
									instruction->arg1,
									instruction->arg2);
	if(-1!=instruction->alterable_flag)
		snprintf(str+strlen(str), str_len-strlen(str), "%02x#%s#",
									instruction->alterable_flag,
									instruction->alterable_entity);

	return 0;
}

/*
功能：	根据指令alterable_flag计算返回结果中的alterable_flag
输入：	flag——指令alterable_flag
返回：	返回给服务器的alterable_flag，-1表示失败
*/
static int alterable_flag_result(int flag)
{
	int ret = 0;
	switch(flag){
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
			ret = 0x80+flag;
			break;
		case 0x7f:
			ret = -1;
			break;
		default:
			ret = -1;
			break;
	}
	return ret;
}

/*
功能：	处理定时任务指令，先将定时任务存入数据库（time），然后刷新内存中的定时器
输入：	instruction——定时任务指令
返回：	0——成功；其他表示失败
*/
static INSTRUCTION_RESULT_E instruction_timing_task_add(INSTRUCTION_S *instruction)
{
	char sqlite_cmd_str[SQLITECMDLEN];

	int l_typeID=instruction->type_id;
	int l_cmdType=instruction->type;
	int l_controlValue=instruction->arg1 * 256 + instruction->arg2;

	int l_frequency = appoint_str2int(instruction->alterable_entity, strlen(instruction->alterable_entity), 0, 2, 16);
	int control_time = appoint_str2int(instruction->alterable_entity, strlen(instruction->alterable_entity),2, 10, 10);
	char *p_entity = instruction->alterable_entity;

//	instant_timing_task_regist(l_typeID, l_frequency, control_time);
	
	/*
	数据库记录的是定时任务的开始时间，（服务器时间，原样入库，不校正时区），
	本地使用时只需要做时区校正，而不需要做服务器时间校正。
	*/
	///	insert the value in the command into time
	INSTRUCTION_RESULT_E ret = ERR_OTHER;
	if(l_frequency>=0 && control_time>=0){
		snprintf(sqlite_cmd_str,sizeof(sqlite_cmd_str),"INSERT INTO time(typeID,cmdType,controlVal,controlTime,frequency,remark) VALUES(%d,%d,%d,%d,%d,'%s');",\
				l_typeID,l_cmdType,l_controlValue,control_time,l_frequency,p_entity+12);
		
		ret = sqlite_execute(sqlite_cmd_str);											///	quit
		if(RESULT_OK==ret){
			timing_task_refresh();
		}
	}
	else{
		DEBUG("invalid arguments: frequency=%d, control_time=%d\n", l_frequency, control_time);
		ret = ERR_FORMAT;
	}
	return ret;
}

/*
功能：	处理模式任务指令，将模式任务加入数据库（model）
输入：	instruciton——待处理的模式任务指令
返回：	0——成功，其他表示失败
*/
static INSTRUCTION_RESULT_E instruction_model_task_add(INSTRUCTION_S *instruction)
{
	int l_typeID;							/// save device's typeID temporarily
	int l_cmdType;							/// save command type  temporarily
	int l_controlValue;						///	save controlValue temporarily
	int l_frequency;						///	save frequency temporaily
	int l_num;								///	save the number of command
	int	l_controlTime;						///	save the controlTime

	char tmp_str[128];
	char sqlite_cmd_str[SQLITECMDLEN];
	char *p_entity = instruction->alterable_entity;
	int i=0;
//	int (*sqlite_callback)(char **,int,int,void *) = model_exist_check_callback;

	///command number
	l_num=appoint_str2int(instruction->alterable_entity,strlen(instruction->alterable_entity),0,2,10);
	
	/*
	 命令条数允许为0
	*/
	DEBUG("cmd number: %d\n", l_num);
	if(0==l_num){
//		DEBUG("no mode needs to process\n");
//		return ERR_FORMAT;
	}

	memset(sqlite_cmd_str, 0, sizeof(sqlite_cmd_str));
	sprintf(sqlite_cmd_str,"SELECT modeID FROM model WHERE modeID=%d;",instruction->arg2);
	DEBUG("sqlite cmd str: %s\n", sqlite_cmd_str);

	int ret_sqlexec = sqlite_read(sqlite_cmd_str, NULL, NULL);
	if(ret_sqlexec>RESULT_OK){
		DEBUG("modeID(%d) is exist in table\n", instruction->arg2);
		return RESULT_OK;
	}
	else if(ret_sqlexec<RESULT_OK){
		DEBUG("sqlite cmd exec failed\n");
		return ret_sqlexec;
	}

	DEBUG("will insert %d model tasks\n", l_num);
	p_entity += 2;

	///update 'model'
	memset(tmp_str, 0, sizeof(tmp_str));
	strncpy(tmp_str, (p_entity+l_num*24), sizeof(tmp_str)-1);
	if('#'==tmp_str[strlen(tmp_str)])
		tmp_str[strlen(tmp_str)] = '\0';
	///	insert modelID,modelName into tabel model
	sprintf(sqlite_cmd_str,"INSERT INTO model(modeID,name) VALUES(%d,'%s');",instruction->arg2,tmp_str);
	DEBUG("sqlite cmd str: %s\n", sqlite_cmd_str);

	INSTRUCTION_RESULT_E ret = sqlite_execute(sqlite_cmd_str);
	if(RESULT_OK!=ret)
		return ret;
	
	for(i=0;i<l_num;i++)									///	get the data of command  and insert into table  modtime
	{
		l_typeID=appoint_str2int(p_entity+i*24, strlen(p_entity+i*24), 0, 6, 16);
		l_cmdType=appoint_str2int(p_entity+i*24, strlen(p_entity+i*24), 6, 2, 16);
		l_controlValue=appoint_str2int(p_entity+i*24, strlen(p_entity+i*24), 8, 4, 16);
		l_frequency=appoint_str2int(p_entity+i*24, strlen(p_entity+i*24), 12, 2, 16);
		l_controlTime=appoint_str2int(p_entity+i*24, strlen(p_entity+i*24), 14, 10, 10);
		sprintf(sqlite_cmd_str,"INSERT INTO modtime(typeID,cmdType,controlVal,controlTime,frequency,remark,modeID) VALUES(%d,%d,%d,%d,%d,'0',%d);",\
				l_typeID,l_cmdType,l_controlValue,l_controlTime,l_frequency,instruction->arg2);

		DEBUG("insert model sqlite cmd str: %s\n", sqlite_cmd_str);
		ret = sqlite_execute(sqlite_cmd_str);
		if(RESULT_OK!=ret)
			return ret;
	}

	return ret;
}

/*
功能：	根据模式id进行sqlite查询的回调，将查询到的模式任务（可能是多个，最多TASK_NUM_IN_MODEL个）存入receiver
输入：	result、row、column定义参见sqlite API
输出：	receiver——在此应用中是模式数组的指针
返回：	0——成功；
*/
#define TASK_NUM_IN_MODEL	64
static int model_select_callback(char **result, int row, int column, void *receiver)
{
	DEBUG("row=%d, column=%d, receiver addr=%p\n", row, column, receiver);
	
	int i = 0;
	MODEL_S model;
	for(i=1;i<row+1 && i<=TASK_NUM_IN_MODEL;i++)
	{
		model.type_id = atoi(result[i*column]);
		model.cmd_type = atoi(result[i*column+1]);
		model.control_val = atoi(result[i*column+2]);
		model.control_time = atoi(result[i*column+3]);
		model.frequency = atoi(result[i*column+4]);
		strncpy(model.remark, result[i*column+5], MIN_LOCAL(sizeof(model.remark)-1, strlen(result[i*column+5])));
		DEBUG("model_array[%d].type_id=0x%06x, cmd_type=0x%02x, control_val=0x%04x, control_time=%d, frequency=0x%02x, remark=%s\n", 
				i-1, model.type_id, model.cmd_type, model.control_val, model.control_time, model.frequency, model.remark);
		memcpy(receiver+(i-1)*sizeof(MODEL_S), &model, sizeof(MODEL_S));
	}
	return 0;
}

/*
功能：	执行指定id的模式任务，先从数据库model表中检索出model的信息，然后重新调用instruction_dispath对模式进行再调度
输入：	model_id——模式任务id
返回：	0——成功；others——失败
*/
static INSTRUCTION_RESULT_E exec_model_with_id(int model_id, char *typeIDs, unsigned int typeIDs_size)
{
	MODEL_S models[TASK_NUM_IN_MODEL];
	int i = 0;
	char sqlite_cmd[SQLITECMDLEN];
	int (*sqlite_callback)(char **,int,int,void *) = model_select_callback;

	memset(models, 0, sizeof(models));
	for(i=0;i<TASK_NUM_IN_MODEL;i++)
		models[i].type_id = -1;
		
	DEBUG("sizeof(models[64])=%d, sizeof(MODEL_S)=%d\n", sizeof(models), sizeof(MODEL_S));

	memset(sqlite_cmd, 0, sizeof(sqlite_cmd));
	sprintf(sqlite_cmd,"SELECT typeID,cmdType,controlVal, controlTime, frequency, remark FROM modtime WHERE modeID=%d;",model_id);
//	DEBUG("before call sqlite_read, models addr=%p\n", models);
	INSTRUCTION_RESULT_E ret = sqlite_read(sqlite_cmd, models, sqlite_callback);
//	DEBUG("after  call sqlite_read, models addr=%p\n", models);
	if(ret<RESULT_OK){
		DEBUG("database select failed\n");
	}
	else if(ret==RESULT_OK){
		DEBUG("there is no such record, select 0 row\n");
		ret = ERR_OTHER;
	}
	else{	// ret>RESULT_OK
		for(i=0; i<TASK_NUM_IN_MODEL; i++){
			if(-1!=models[i].type_id){
				INSTRUCTION_S inst;
				inst.type_id = models[i].type_id;
				memset(inst.reserve, 0, sizeof(inst.reserve));
				inst.type = models[i].cmd_type;
				inst.arg1 = models[i].control_val >> 8 & 0xff;
				inst.arg2 = models[i].control_val & 0xff;
				/*
				如果待设定模式中的指令为即时指令，则时间戳用‘0000000000’填充，同理频度用‘00’填充。
				*/
				if(models[i].control_time<=0){
					inst.alterable_flag = 0x00;
					memset(inst.alterable_entity,0,sizeof(inst.alterable_entity));
				}
				else{
					inst.alterable_flag = 0x01;
					snprintf(inst.alterable_entity,sizeof(inst.alterable_entity),"%02x%10d", models[i].frequency, models[i].control_time);
				}
				
				DEBUG("instruction dispatch in model execute process\n");
				instruction_dispatch(&inst);
				snprintf(typeIDs+strlen(typeIDs), typeIDs_size-strlen(typeIDs), "%d|", inst.type_id);
			}
		}
		ret = RESULT_OK;
	}

	return ret;
}

/*
功能：	计算串口命令串校验位，截取16进制的后两位
输入：	serialcmd	——待计算的串口命令串
		num			——待计算命令串长度
返回：	0——失败；others——计算的校验位
*/
unsigned int serialcmd_checksum(unsigned char *serialcmd, int num)
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
功能：	解析串口命令串，只针对智能插座
注意：	目前存在这样的情况，串口返回的命令串重复了多遍，需要能兼容这种情况，形如下面的结果是读取有功功率时一次返回的：
		68 20 11 12 21 06 36 68 81 05 63 e9 33 33 33 db 16 68 20 11 12 21 06 36 68 c5 03 e9 01 63 85 16 68 20 11 06 00 41 56 68 81 06 43 c3 3c 33 33 33 00 16
*/
int smart_socket_serial_cmd_parse(unsigned char *serial_cmd, unsigned int cmd_len, SMART_SOCKET_ACTION_E socket_action, char *socket_id, double *result)
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

//	int i = 0;
//	DEBUG("splice serial cmd(len=%d):", cmd_len);
//	for(i=0; i<cmd_len; i++)
//		printf(" %02x", serial_cmd[i]);
//	printf("\n");
	
	if(0x68!=serial_cmd[0] || 0x68!=serial_cmd[7])	/*  || 0x16!=serial_cmd[cmd_len-1] */
	{
		DEBUG("this cmd has invalid header and tailer signal\n");
		return -1;
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
		DEBUG("socket id can not match: id in cmd is %s, and mirror with %s\n", socket_addr, socket_id);
		return -1;
	}

	int ret = 0;
	switch(socket_action){
		case SMART_SOCKET_ACTIVE_POWER_CONSUMPTION_READ:	// 读当前有功总电量
			// 68 a0 a1 a2 a3 a4 a5 68 81 06 43 C3 xx xx xx xx cs 16
			if(serial_cmd[16]!=serialcmd_checksum(serial_cmd, 16)){
				DEBUG("check sum failed\n");
				ret = -1;
			}
			else{
				*result = bcdChange(serial_cmd[14]-0x33)*100+bcdChange(serial_cmd[13]-0x33)*1+bcdChange(serial_cmd[12]-0x33)*0.01;
			}
			break;
		case SMART_SOCKET_VOLTAGE_READ:				// 读电压
			// 68 a0 a1 a2 a3 a4 a5 68 81 04 44 E9 xx xx cs 16
			if(serial_cmd[14]!=serialcmd_checksum(serial_cmd, 14)){
				DEBUG("check sum failed\n");
				ret = -1;
			}
			else{
				*result = bcdChange(serial_cmd[13]-0x33)*100+bcdChange(serial_cmd[12]-0x33)*1;
			}
			break;
		case SMART_SOCKET_POWER_CURRENT_READ:		// 读电流
			// 68 a0 a1 a2 a3 a4 a5 68 81 04 54 E9 xx xx cs 16
			if(serial_cmd[14]!=serialcmd_checksum(serial_cmd, 14)){
				DEBUG("check sum failed\n");
				ret = -1;
			}
			else{
				*result = bcdChange(serial_cmd[13]-0x33)*1+bcdChange(serial_cmd[12]-0x33)*0.01;
			}
			break;
		case SMART_SOCKET_ACTIVE_POWER_READ:		// 有功功率
			// 68 a0 a1 a2 a3 a4 a5 68 81 05 63 E9 xx xx xx cs 16
			if(serial_cmd[15]!=serialcmd_checksum(serial_cmd, 15)){
				DEBUG("check sum failed\n");
				ret = -1;
			}
			else{
				*result = bcdChange(serial_cmd[14]-0x33)*1+bcdChange(serial_cmd[13]-0x33)*0.01+bcdChange(serial_cmd[12]-0x33)*0.0001;
			}
			break;
		case SMART_SOCKET_REACTIVE_POWER_READ:		// 无功功率
			// 68 a0 a1 a2 a3 a4 a5 68 81 04 73 E9 xx xx cs 16
			if(serial_cmd[14]!=serialcmd_checksum(serial_cmd, 14)){
				DEBUG("check sum failed\n");
				ret = -1;
			}
			else{
				*result = bcdChange(serial_cmd[13]-0x33)*1+bcdChange(serial_cmd[12]-0x33)*0.01;
			}
			break;
		case SMART_SOCKET_POWER_COEFFICIENT_READ:	// 功率因数
			// 68 a0 a1 a2 a3 a4 a5 68 81 04 83 E9 xx xx cs 16
			if(serial_cmd[14]!=serialcmd_checksum(serial_cmd, 14)){
				DEBUG("check sum failed\n");
				ret = -1;
			}
			else{
				*result = bcdChange(serial_cmd[13]-0x33)*0.01+bcdChange(serial_cmd[12]-0x33)*0.0001;
			}
			break;
		case SMART_SOCKET_RELAY_DISCONNECT:			// 继电器断开
			// 68 a0 a1 a2 a3 a4 a5 68 84 02 88 xx cs 16
			if(serial_cmd[12]!=serialcmd_checksum(serial_cmd, 12)){
				DEBUG("check sum failed\n");
				ret = -1;
			}
			else{
				ret = socket_relay_status_trans(serial_cmd[11], result);
			}
			break;
		case SMART_SOCKET_RELAY_CONNECT:			// 继电器闭合
			// 68 a0 a1 a2 a3 a4 a5 68 84 02 89 xx cs 16
			if(serial_cmd[12]!=serialcmd_checksum(serial_cmd, 12)){
				DEBUG("check sum failed\n");
				ret = -1;
			}
			else{
				ret = socket_relay_status_trans(serial_cmd[11], result);
			}
			break;
		case SMART_SOCKET_RELAY_STATUS_READ:		// 继电器状态
			// 68 a0 a1 a2 a3 a4 a5 68 81 03 93 E9 xx cs 16
			if(serial_cmd[13]!=serialcmd_checksum(serial_cmd, 13)){
				DEBUG("check sum failed\n");
				ret = -1;
			}
			else{
				ret = socket_relay_status_trans(serial_cmd[12], result);
			}
			break;
		case SMART_SOCKET_ADDR_CONFIRM:				// 地址确认
			// 68 a0 a1 a2 a3 a4 a5 68 87 06 33 33 33 33 33 33 cs 16
			if(serial_cmd[16]!=serialcmd_checksum(serial_cmd, 16)){
				DEBUG("check sum failed\n");
				ret = -1;
			}
			else{
				*result = 1;
			}
			break;
		case SMART_SOCKET_INSTRUCTION_INVALID:		// 指令错误
			// 68 a0 a1 a2 a3 a4 a5 68 C4 01 XX cs 16
			if(serial_cmd[10]!=serialcmd_checksum(serial_cmd, 10)){
				DEBUG("check sum failed\n");
				ret = -1;
			}
			else{
				*result = serial_cmd[11];
				DEBUG("serial instruction invalide, err code: 0x%02x\n", (int)(*result));
			}
		case SMART_SOCKET_COMMUNICATION_FAILD:		// 通讯失败
			// 68 a0 a1 a2 a3 a4 a5 68 C5 03 xx xx xx cs 16
			if(serial_cmd[13]!=serialcmd_checksum(serial_cmd, 13)){
				DEBUG("check sum failed\n");
				ret = -1;
			}
			else{
				*result = (serial_cmd[10]<<16) | (serial_cmd[11]<<8) | serial_cmd[12];
				DEBUG("serial control communication failed, err code: 0x%02x 0x%02x 0x%02x\n", serial_cmd[10], serial_cmd[11], serial_cmd[12]);
			}
		default:
			DEBUG("can not support this action of smart socket\n");
			ret = -1;
			break;
	}

	return ret;
}

/*
功能：	拼接串口命令串，只针对智能插座
*/
int smart_socket_serial_cmd_splice(unsigned char *serial_cmd, unsigned int cmd_size, SMART_SOCKET_ACTION_E socket_action, char *socket_id)
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

/*
功能：	处理即时任务，一般是读写智能插座的串口
*/
static INSTRUCTION_RESULT_E immediatly_task_run(INSTRUCTION_S *instruction)
{
	if(NULL==instruction){
		DEBUG("can not treat with NULL structure\n");
		return ERR_OTHER;
	}
	
	unsigned char serial_cmd[SERIAL_CMD_SIZE];
	EQUIPMENT_S myequipment;
	INSTRUCTION_RESULT_E ret = RESULT_OK;
	int serial_cmd_len = 0;

	memset(serial_cmd, 0, sizeof(serial_cmd));
	memset(&myequipment, 0, sizeof(EQUIPMENT_S));
	ret = equipment_get(instruction->type_id, &myequipment);
	if(RESULT_OK!=ret){
		DEBUG("equipment read failed, type_id=%d\n", instruction->type_id);
		return ret;
	}

	//make sure socket's address mustn't empty
	if(0x01==instruction->arg1)				///on-off
	{
		if(0x01==instruction->arg2)			///on
		{
			serial_cmd_len = smart_socket_serial_cmd_splice(serial_cmd,sizeof(serial_cmd),SMART_SOCKET_RELAY_CONNECT,myequipment.socket_id);
		}
		else if(0x00==instruction->arg2)		///off
		{
			serial_cmd_len = smart_socket_serial_cmd_splice(serial_cmd,sizeof(serial_cmd),SMART_SOCKET_RELAY_DISCONNECT,myequipment.socket_id);
		}
		else{
			DEBUG("can not support such arg2: 0x%d\n", instruction->arg2);
			return ERR_FORMAT;
		}
	}
	else
	{
		return ERR_FORMAT;
	}
	
#if 0
	ret = sendto_serial(serial_cmd, serial_cmd_len);
	if(RESULT_OK!=ret){
		DEBUG("send to serial failed\n");
		return ret;
	}
	//ms_sleep(500);
	memset(serial_cmd, 0, sizeof(serial_cmd));
	int recv_serial_len = recvfrom_serial(serial_cmd, sizeof(serial_cmd));
#else
	int recv_serial_len = serial_access(serial_cmd, serial_cmd_len, sizeof(serial_cmd));
#endif
	double result = 0.0;
	if(recv_serial_len>0){
		if(0x01==instruction->arg2)
			ret = smart_socket_serial_cmd_parse(serial_cmd, recv_serial_len, SMART_SOCKET_RELAY_CONNECT, myequipment.socket_id, &result);
		else if(0x00==instruction->arg2)
			ret = smart_socket_serial_cmd_parse(serial_cmd, recv_serial_len, SMART_SOCKET_RELAY_DISCONNECT, myequipment.socket_id, &result);

		if(RESULT_OK==ret /*&& 1==result*/){
			DEBUG("do immedatly task OK\n");
			return RESULT_OK;
		}
	}
	
	return ERR_OTHER;
}

static INSTRUCTION_RESULT_E inquire_electrical_status(INSTRUCTION_S *instruction, SMART_SOCKET_ACTION_E sock_action)
{
	unsigned char serial_cmd[SERIAL_CMD_SIZE];
	EQUIPMENT_S myequipment;
	INSTRUCTION_RESULT_E ret = RESULT_OK;

	memset(serial_cmd, 0, sizeof(serial_cmd));
	memset(&myequipment, 0, sizeof(EQUIPMENT_S));
	ret = equipment_get(instruction->type_id, &myequipment);
	if(RESULT_OK!=ret){
		return ret;
	}
	double power = 0.0;
	int serial_cmd_len = smart_socket_serial_cmd_splice(serial_cmd, sizeof(serial_cmd), sock_action, myequipment.socket_id);
	DEBUG("serial_cmd_len=%d\n", serial_cmd_len);
	if(serial_cmd_len<=0)
		return ERR_OTHER;
	
	int recv_serial_len = serial_access(serial_cmd, serial_cmd_len, sizeof(serial_cmd));
	if(recv_serial_len>0){
		if(SMART_SOCKET_RELAY_STATUS_READ==sock_action){
			// 68 a0 a1 a2 a3 a4 a5 68 81 03 93 E9 xx cs 16, 
			// XX代表继电器状态：0x55继电器断开, 0x56继电器闭合, 0x57继电器未知状态.
			memset(instruction->alterable_entity, 0, sizeof(instruction->alterable_entity));
			if(0x55==serial_cmd[12])	// off
				strncpy(instruction->alterable_entity,"011101;00;@", sizeof(instruction->alterable_entity));
			else if(0x56==serial_cmd[12])	// on
				strncpy(instruction->alterable_entity,"011101;01;@", sizeof(instruction->alterable_entity));
			else //if(0x57==serial_cmd[12])	// unknown
				strncpy(instruction->alterable_entity,"011101;02;@", sizeof(instruction->alterable_entity));
			
			return RESULT_ALTERABLE_ENTITY_FILL_OK;
		}
		else if(SMART_SOCKET_ACTIVE_POWER_READ==sock_action || SMART_SOCKET_REACTIVE_POWER_READ==sock_action){
			if(RESULT_OK==smart_socket_serial_cmd_parse(serial_cmd,recv_serial_len,sock_action,myequipment.socket_id,&power))
			{
				snprintf(instruction->alterable_entity, sizeof(instruction->alterable_entity),"%lf", power);
				DEBUG("power=%lf\n", power);
				return RESULT_ALTERABLE_ENTITY_FILL_OK;
			}
			else{
				DEBUG("parse serial cmd failed\n");
				return ERR_OTHER;
			}
		}
		else{
			DEBUG("can NOT process such action: %d\n", sock_action);
			return ERR_OTHER;
		}
	}

	return ERR_OTHER;
}

/*
这一段代码比较繁琐，目的是避免重复
*/
/*
功能：	查询有功功率、电量数据库的回调函数，将其结果拼接为适合返回服务器的字符串
*/
int power_inquire_callback(char **result, int row, int column, void *receiver)
{
	DEBUG("row=%d, column=%d, receiver add=%p\n", row, column, receiver);
	int i = 0, j = 0, k = 0;
	char *entity_str = (char *)receiver;
	EQUIPMENT_S tmp_equipment;
	int type_id = 0;
	char tmp_str[64];

	/*循环扫描的同时，需要根据typeID进行分类汇总*/
	for(i=1;i<row+1;i++)
	{
		type_id = atoi(result[i*column]);
		
		/*
		避免重复统计，需要反向查询当前type_id是否已经存在，若存在则已经被统计过，将忽略。
		*/
		for(k=1; k<i; k++){
			if(atoi(result[k*column])==type_id)
				break;
		}
		DEBUG("type_id=%d=0x%06x, k=%d, i=%d\n", type_id, type_id, k, i);
		
		/*
		处理此前循环没有处理过的typeID，k==i表明这是个新typeID。
		*/
		if(k==i && 0x000000!=type_id)
		{
			if(0!=equipment_get(type_id, &tmp_equipment)){
				DEBUG("search no equipment with type_id=0x%06x=%d\n", type_id, type_id);
				continue;
			}
			snprintf(	entity_str+strlen(entity_str), ALTERABLE_ENTITY_SIZE-strlen(entity_str), 
						"%06x;%s;", type_id, tmp_equipment.socket_id);
			
			for(j=i; j<row+1; j++){
				if(type_id==atoi(result[j*column])){
					float power_data = atof(result[j*column+2]);
					DEBUG("result[j*column+2]=%s, data=%f\n", result[j*column+2], power_data);
					snprintf(tmp_str, sizeof(tmp_str), "%s;%08f;", result[j*column+1], power_data);
					if(ALTERABLE_ENTITY_SIZE-strlen(entity_str) > strlen(tmp_str)){
						snprintf(	entity_str+strlen(entity_str), ALTERABLE_ENTITY_SIZE-strlen(entity_str), 
									"%s", tmp_str);
					}
					else{
						DEBUG("space of alterable entity is too small\n");
						return 0;
					}
				}
			}
			snprintf(	entity_str+strlen(entity_str), ALTERABLE_ENTITY_SIZE-strlen(entity_str), 
					"@");
		}
	}
	
	return 0;
}

#define REPORT_SOCKET_STATUS		"#0000000000#02#0101#80#"
/*
功能：定时任务、模式任务执行后需要主动上报开关状态，这里拼接指令，形如：#0000000000#02#0101#80#typeID;00/01/02;@typeID;00/01/02;@#
typeIDs: 	需要查询状态并主动上报的typeID的集合，以竖线分割，形如：62392|76231|45253|
			调用者需要确保这个字符串有正常的'\0'结尾
*/
int sockets_status_report(char *typeIDs)
{
	int i=0;
	int type_id = 0;
	char socket_id[32];
	unsigned char serial_cmd[128];
	double result = 0.0;
	
	if(NULL==typeIDs){
		DEBUG("can not proccess with invalid arguments\n");
		return -1;
	}
	else
		DEBUG("will report typeIDs: %s\n", typeIDs);
	
	EQUIPMENT_S tmp_equipments[EQUIPMENT_NUM];
	if(0!=equipments_get(&tmp_equipments)){
		DEBUG("can not read equipments\n");
		return -1;
	}
	
	char entity[ALTERABLE_ENTITY_SIZE];	// 4096
	snprintf(entity, sizeof(entity), "%s", REPORT_SOCKET_STATUS);
	
	char *p_typeID = typeIDs;
	while(p_typeID){
		if('|'==*p_typeID)
			p_typeID ++;
		
		type_id = atoi(p_typeID);
		if(type_id>0){
			for(i=0;i<EQUIPMENT_NUM;i++)
			{
				if(type_id==tmp_equipments[i].type_id)
				{
					memset(socket_id, 0, sizeof(socket_id));
					strcpy(socket_id, tmp_equipments[i].socket_id);
					snprintf(entity+strlen(entity), sizeof(entity)-strlen(entity), "%06x;", type_id);
					
					memset(serial_cmd, 0, sizeof(serial_cmd));
					
					int sock_status = SMART_SOCKET_RELAY_STATUS_UNKNOWN;
					int serial_cmd_len = smart_socket_serial_cmd_splice(serial_cmd,sizeof(serial_cmd), SMART_SOCKET_RELAY_STATUS_READ,socket_id);
					if(serial_cmd_len>0)
					{
						int recv_serial_len = serial_access(serial_cmd, serial_cmd_len, sizeof(serial_cmd));
						if(recv_serial_len>0)
						{
							if(RESULT_OK==smart_socket_serial_cmd_parse(serial_cmd,recv_serial_len,SMART_SOCKET_RELAY_STATUS_READ,socket_id,&result))
							{
								sock_status = (int)result;
								if(sock_status<SMART_SOCKET_RELAY_STATUS_OFF || sock_status>SMART_SOCKET_RELAY_STATUS_UNKNOWN)
									sock_status = SMART_SOCKET_RELAY_STATUS_UNKNOWN;
								DEBUG("socket %d(0x%06x) status: %d(%lf)\n", type_id, type_id, sock_status, result);
							}
						}
					}
					snprintf(entity+strlen(entity), sizeof(entity)-strlen(entity), "%02x;@", sock_status);
					break;
				}
			}
		}
		
		char *p_ver_line = strchr(p_typeID, '|');
		if(p_ver_line)
			p_typeID = p_ver_line + 1;
		else
			break;
	}
	
	if(strlen(entity)>strlen(REPORT_SOCKET_STATUS)){	
		snprintf(entity+strlen(entity), sizeof(entity)-strlen(entity), "#");
		cmd_insert(entity, CMD_ACTIVE_REPORTED_STATUS);
		return 0;
	}
	else
		return -1;
}

/*
功能：	查询有功功率数据库
输入：	instruction——有功功率查询指令
		single_equipment_flag——是否查询单个设备的有功功率，1表示查询单个设备有功功率，0表示查询所有设备的有功功率
*/
/*
static INSTRUCTION_RESULT_E inquire_active_power(INSTRUCTION_S *instruction, int single_equipment_flag)
{
	int start_time = 0;
	int end_time = 0;
	char sqlite_cmd[SQLITECMDLEN];
	INSTRUCTION_RESULT_E ret = ERR_OTHER;
	int (*sqlite_callback)(char **,int,int,void *) = power_inquire_callback;
	
	if(20==strlen(instruction->alterable_entity)){
		start_time = appoint_str2int(instruction->alterable_entity, strlen(instruction->alterable_entity), 0, 10, 10);
		end_time = appoint_str2int(instruction->alterable_entity, strlen(instruction->alterable_entity), 10, 10, 10);
		if(start_time<end_time){
			memset(sqlite_cmd, 0, sizeof(sqlite_cmd));
			if(0==single_equipment_flag)
				sprintf(sqlite_cmd,"SELECT typeID, hourTime, data FROM actpower WHERE hourTime BETWEEN %d AND %d;",start_time, end_time);
			else
				sprintf(sqlite_cmd,"SELECT typeID, hourTime, data FROM actpower WHERE typeID=%d AND (hourTime BETWEEN %d AND %d);", instruction->type_id, start_time, end_time);
			ret = sqlite_read(sqlite_cmd, instruction->alterable_entity, sqlite_callback);
			if(ret>=RESULT_OK){
				ret = RESULT_ALTERABLE_ENTITY_FILL_OK;
				memset(sqlite_cmd, 0, sizeof(sqlite_cmd));
			}
		}
		else{
			DEBUG("can not support such start_time(%d)>=end_time(%d)\n", start_time, end_time);
			ret = ERR_FORMAT;
		}
	}
	else{
		DEBUG("there is no valid start timestamp and end timestamp: %s\n", instruction->alterable_entity);
		ret = ERR_FORMAT;
	}
	
	return ret;
}
*/

/*
功能：	查询电量数据库
*/
static INSTRUCTION_RESULT_E inquire_power_consumption(INSTRUCTION_S *instruction)
{
	int start_time = 0;
	int end_time = 0;
	char sqlite_cmd[SQLITECMDLEN];
	INSTRUCTION_RESULT_E ret = ERR_OTHER;
	int (*sqlite_callback)(char **,int,int,void *) = power_inquire_callback;
	if(20==strlen(instruction->alterable_entity)){
		start_time = appoint_str2int(instruction->alterable_entity, strlen(instruction->alterable_entity), 0, 10, 10);
		end_time = appoint_str2int(instruction->alterable_entity, strlen(instruction->alterable_entity), 10, 10, 10);
		if(start_time<end_time){
			memset(sqlite_cmd, 0, sizeof(sqlite_cmd));
			if(0x000000==instruction->type_id)
				sprintf(sqlite_cmd,"SELECT typeID, hourTime, data FROM power WHERE hourTime BETWEEN %d AND %d;", start_time, end_time);
			else
				sprintf(sqlite_cmd,"SELECT typeID, hourTime, data FROM power WHERE typeID=%d AND (hourTime BETWEEN %d AND %d);", instruction->type_id, start_time, end_time);
		
			ret = sqlite_read(sqlite_cmd, instruction->alterable_entity, sqlite_callback);
			if(ret>=RESULT_OK){
				ret = RESULT_ALTERABLE_ENTITY_FILL_OK;
			}
		}
		else{
			DEBUG("can not support such start_time(%d)>=end_time(%d)\n", start_time, end_time);
			ret = ERR_FORMAT;
		}
	}
	else{
		DEBUG("there is no valid start timestamp and end timestamp: %s\n", instruction->alterable_entity);
		ret = ERR_FORMAT;
	}

	return ret;

}

/*
功能：	查询定时任务数据库的回调函数
*/
static int timing_task_callback(char **result, int row, int column, void *receiver)
{
	DEBUG("row=%d, column=%d, receiver addr=%p\n", row, column, receiver);
	int i = 0;
	for(i=1;i<row+1;i++)
	{
		sprintf(receiver, 
				"%06x;%02x;%02x;%02x;%02x;%010x;%s;@", 
				atoi(result[i*column+5]),
				atoi(result[i*column]),
				atoi(result[i*column+1])>>8 & 0xff,
				atoi(result[i*column+1]) & 0xff,
				atoi(result[i*column+3]),
				atoi(result[i*column+2]),
				(result[i*column+4]));
	}
	return 0;
}

/*
功能：	查询定时任务
*/
static INSTRUCTION_RESULT_E inquire_timing_task(INSTRUCTION_S *instruction)
{
	INSTRUCTION_RESULT_E ret = ERR_OTHER;
	char sqlite_cmd[SQLITECMDLEN];
	int (*sqlite_callback)(char **,int,int,void *) = timing_task_callback;

	memset(sqlite_cmd, 0, sizeof(sqlite_cmd));
	if(0x000000==instruction->type_id)
		sprintf(sqlite_cmd,"SELECT cmdType,controlVal,controlTime,frequency,remark,typeID FROM time;");
	else
		sprintf(sqlite_cmd,"SELECT cmdType,controlVal,controlTime,frequency,remark FROM time WHERE typeID=%d;",instruction->type_id);

	memset(instruction->alterable_entity, 0, sizeof(instruction->alterable_entity));
	ret = sqlite_read(sqlite_cmd, instruction->alterable_entity, sqlite_callback);
	if(ret>=RESULT_OK){
		ret = RESULT_ALTERABLE_ENTITY_FILL_OK;
	}
	return ret;
}

/*
功能：	查询设备列表数据库的回调函数
*/
static int equipment_list_callback(char **result, int row, int column, void *receiver)
{
	DEBUG("row=%d, column=%d, receiver addr=%p\n", row, column, receiver);
	int i = 0;
	for(i=1;i<row+1;i++)
	{
		sprintf((char *)receiver, "%06x;%02x;%02x;%04x;%012x;%s;%s@", 
				atoi(result[i*column]),
				atoi(result[i*column+1]),
				atoi(result[i*column+2]),
				atoi(result[i*column+3]),
				atoi(result[i*column+4]),
				(result[i*column+5]),
				(result[i*column+6]));
	}
	return 0;
}

/*
功能：	查询设备列表数据库
*/
static INSTRUCTION_RESULT_E inquire_equipment_list(INSTRUCTION_S *instruction)
{
	INSTRUCTION_RESULT_E ret = ERR_OTHER;
	char sqlite_cmd[SQLITECMDLEN];
	int (*sqlite_callback)(char **,int,int,void *) = equipment_list_callback;

	memset(sqlite_cmd, 0, sizeof(sqlite_cmd));
	sprintf(sqlite_cmd,"SELECT typeID,locationID,iconID,operID,socketID,roomName,devName FROM devlist;");
	memset(instruction->alterable_entity, 0, sizeof(instruction->alterable_entity));
	ret = sqlite_read(sqlite_cmd, instruction->alterable_entity, sqlite_callback);
	if(ret>=RESULT_OK){
		ret = RESULT_ALTERABLE_ENTITY_FILL_OK;
	}
	return ret;
}

/*
功能：	查询模式任务数据库的回调函数
*/
static int model_callback(char **result, int row, int column, void *receiver)
{
	DEBUG("row=%d, column=%d, receiver addr=%p\n", row, column, receiver);
	int i = 0;
	for(i=1;i<row+1;i++)
	{
		sprintf((char *)receiver, "%06x;%02x;%02x;%02x;%02x;%010d@", 
				atoi(result[i*column]),
				atoi(result[i*column+1]),
				atoi(result[i*column+2])>>8 & 0xff,
				atoi(result[i*column+2]) & 0xff,
				atoi(result[i*column+4]),
				atoi(result[i*column+3]));
	}
	return 0;
}

/*
功能：	查询模式任务数据库
*/
static INSTRUCTION_RESULT_E inquire_model(INSTRUCTION_S *instruction)
{
	INSTRUCTION_RESULT_E ret = ERR_OTHER;
	int (*sqlite_callback)(char **,int,int,void *) = model_callback;
	char sqlite_cmd[SQLITECMDLEN];

	memset(sqlite_cmd, 0, sizeof(sqlite_cmd));
	sprintf(sqlite_cmd,"SELECT typeID,cmdType,controlVal,controlTime,frequency FROM modtime WHERE modeID=%d;", instruction->arg2);
	memset(instruction->alterable_entity, 0, sizeof(instruction->alterable_entity));
	ret = sqlite_read(sqlite_cmd, instruction->alterable_entity, sqlite_callback);
	if(ret>=RESULT_OK){
		ret = RESULT_ALTERABLE_ENTITY_FILL_OK;
	}
	return ret;
}

/*
功能：	插入一个设备到数据库devlist表中，插入完成后刷新内存中的设备数组
*/
static INSTRUCTION_RESULT_E electric_equipment_insert(INSTRUCTION_S *instruction)
{
	/* 统一处理房间号和iconID */
	int location_id = appoint_str2int(instruction->alterable_entity, strlen(instruction->alterable_entity), 0, 2, 16);
	int icon_id = appoint_str2int(instruction->alterable_entity, strlen(instruction->alterable_entity), 2, 2, 16);
//	int socket_id = 0;
	char socket_id[17];
	int oper_id = 0;
	char room_name[128];
	char dev_name[128];
	char *p_tmp = instruction->alterable_entity;
	char *p_star = NULL;
	char sqlite_cmd_str[SQLITECMDLEN];

	memset(sqlite_cmd_str, 0, sizeof(sqlite_cmd_str));
	memset(socket_id, 0, sizeof(socket_id));

	memset(room_name, 0, sizeof(room_name));
	memset(dev_name, 0, sizeof(dev_name));
	/* 前四位作为房间号和iconID已经处理完毕 */
	p_tmp += 4;
	
	if(0x01==instruction->arg2){
		// socket id, len=12
		strncpy(socket_id, p_tmp, 12);
		p_tmp += (12+4);	/* jump socketID and the filling '0000' */
		
		sprintf(sqlite_cmd_str,"SELECT typeID FROM devlist WHERE typeID=%d AND socketID='%s';",instruction->type_id, socket_id);
	}
	else if(0x02==instruction->arg2){
		// operID, len=4;
		oper_id = appoint_str2int(instruction->alterable_entity, strlen(instruction->alterable_entity), 4, 4, 16);
		p_tmp += (4);
		
		sprintf(sqlite_cmd_str,"SELECT typeID FROM devlist WHERE typeID=%d AND operID=%d;",instruction->type_id, oper_id);
	}
	else if(0x03==instruction->arg2){
		// operID, len=4
		oper_id = appoint_str2int(instruction->alterable_entity, strlen(instruction->alterable_entity), 4, 4, 16);
		strncpy(socket_id, p_tmp+4, 12);
		p_tmp += (4+12+4);	/* jump operID, socketID and the filling '0000' */
		
		sprintf(sqlite_cmd_str,"SELECT typeID FROM devlist WHERE typeID=%d AND socketID='%s' AND operID=%d;",instruction->type_id, socket_id, oper_id);
	}
	else{
		DEBUG("this arg2(0x%02x) is not supported\n", instruction->arg2);
		return ERR_FORMAT;
	}

	p_star = strstr(p_tmp, "**");
	if(NULL==p_star){
		DEBUG("warning: there is no delimiter to indicate devName\n");
		strncpy(room_name, p_tmp, MIN_LOCAL(strlen(p_tmp),(sizeof(room_name)-1)));
	}
	else{
		strncpy(room_name, p_tmp, MIN_LOCAL(abs(p_tmp-p_star),(sizeof(room_name)-1)));
		p_star += 2;
		strncpy(dev_name, p_star, MIN_LOCAL(strlen(p_star), (sizeof(dev_name)-1)));
	}

	DEBUG("sqlite cmd str: %s\n", sqlite_cmd_str);
//	int (*sqlite_callback)(char **,int,int,void *) = equipment_check_callback;
	int ret_sqlexec = sqlite_read(sqlite_cmd_str, NULL, NULL);
	if(ret_sqlexec>RESULT_OK){
		DEBUG("this equipment is exist in table\n");
		return RESULT_OK;
	}
	else if(ret_sqlexec<RESULT_OK){
		DEBUG("sqlite cmd exec failed\n");
		return ret_sqlexec;
	}
	else	// 0==ret_sqlexec
		;
		
	sprintf(sqlite_cmd_str,"INSERT INTO devlist(typeID,locationID,iconID,operID,socketID,roomName,devName) VALUES(%d,%d,%d,%d,'%s','%s','%s');",\
			instruction->type_id,location_id,icon_id,oper_id,socket_id,room_name,dev_name);

	DEBUG("insert model sqlite cmd str: %s\n", sqlite_cmd_str);
	INSTRUCTION_RESULT_E ret = sqlite_execute(sqlite_cmd_str);
	DEBUG("sqlite_execute ret = %d\n", ret);
	if(RESULT_OK==ret)
		return equipment_refresh();
	else
		return ret;
}

/*
功能：	更新设备
*/
static INSTRUCTION_RESULT_E electric_equipment_update(INSTRUCTION_S *instruction)
{
	char sqlite_cmd_str[SQLITECMDLEN];
	memset(sqlite_cmd_str, 0, sizeof(sqlite_cmd_str));

	int location_id = 0;
	int icon_id = 0;
	int oper_id = 0;
	// invoke 0000 0000 0000
	int socket_id = 0;
	// invoke 0000
	char room_name[128];
	char dev_name[128];
	char *p_tmp = instruction->alterable_entity;
	char *p_star = NULL;
	
	switch(instruction->arg2){
		case 0x01:
			sprintf(sqlite_cmd_str,"UPDATE devlist SET operID=%d WHERE typeID=%d;",atoi(instruction->alterable_entity), instruction->type_id);
			break;
		case 0x02:
			sprintf(sqlite_cmd_str,"UPDATE devlist SET socketID=%d WHERE typeID=%d;",atoi(instruction->alterable_entity), instruction->type_id);
			break;
		case 0x03:
			sprintf(sqlite_cmd_str,"UPDATE devlist SET devName=%s WHERE typeID=%d;",instruction->alterable_entity, instruction->type_id);
			break;
		case 0x04:
			sprintf(sqlite_cmd_str,"UPDATE devlist SET locationID=%d WHERE typeID=%d;",atoi(instruction->alterable_entity), instruction->type_id);
			break;
		case 0x05:
			sprintf(sqlite_cmd_str,"UPDATE devlist SET roomName=%s WHERE typeID=%d;",instruction->alterable_entity, instruction->type_id);
			break;
		case 0x06:
			sprintf(sqlite_cmd_str,"UPDATE devlist SET iconID=%d WHERE typeID=%d;",atoi(instruction->alterable_entity), instruction->type_id);
			break;
		case 0x07:
			location_id = appoint_str2int(instruction->alterable_entity, strlen(instruction->alterable_entity), 0, 2, 16);
			icon_id = appoint_str2int(instruction->alterable_entity, strlen(instruction->alterable_entity), 2, 2, 16);
			oper_id = appoint_str2int(instruction->alterable_entity, strlen(instruction->alterable_entity), 4, 4, 16);
			// invoke 0000 0000 0000
			socket_id = appoint_str2int(instruction->alterable_entity, strlen(instruction->alterable_entity), 20, 12, 16);
			// invoke 0000
			memset(room_name, 0, sizeof(room_name));
			memset(dev_name, 0, sizeof(dev_name));
			p_star = strstr(p_tmp, "**");
			if(NULL==p_star){
				DEBUG("warning: there is no delimiter to indicate devName\n");
				strncpy(room_name, p_tmp, MIN_LOCAL(strlen(p_tmp),(sizeof(room_name)-1)));
			}
			else{
				strncpy(room_name, p_tmp, MIN_LOCAL(abs(p_tmp-p_star),(sizeof(room_name)-1)));
				p_star += 2;
				strncpy(dev_name, p_star, MIN_LOCAL(strlen(p_star), (sizeof(dev_name)-1)));
			}
			sprintf(sqlite_cmd_str,"UPDATE devlist SET locationID=%d, iconID=%d, operID=%d, socketID=%d, roomName='%s', devName='%s' WHERE typeID=%d;",
									location_id, icon_id, oper_id, socket_id, room_name, dev_name, instruction->type_id);
			break;
		default:
			DEBUG("can not support this arg2: 0x%02x\n", instruction->arg2);
			return ERR_FORMAT;
			break;
	}
	
	DEBUG("sqlite cmd str: %s\n", sqlite_cmd_str);
	INSTRUCTION_RESULT_E ret = sqlite_execute(sqlite_cmd_str);
	if(RESULT_OK==ret)
		return equipment_refresh();
	else
		return ret;
}

static INSTRUCTION_RESULT_E electric_equipment_delete(INSTRUCTION_S *instruction)
{
	char sqlite_cmd_str[SQLITECMDLEN];
	memset(sqlite_cmd_str, 0, sizeof(sqlite_cmd_str));
	
	sprintf(sqlite_cmd_str,"DELETE FROM devlist WHERE typeID=%d;",instruction->type_id);
	DEBUG("sqlite cmd str: %s\n", sqlite_cmd_str);
	INSTRUCTION_RESULT_E ret = sqlite_execute(sqlite_cmd_str);
	if(RESULT_OK==ret)
		return equipment_refresh();
	else
		return ret;
}

static INSTRUCTION_RESULT_E model_update(INSTRUCTION_S *instruction)
{
	char sqlite_cmd_str[SQLITECMDLEN];
	memset(sqlite_cmd_str, 0, sizeof(sqlite_cmd_str));
	
	sprintf(sqlite_cmd_str,"UPDATE model SET name='%s' WHERE modeID=%d;",instruction->alterable_entity, instruction->type_id);
	DEBUG("sqlite cmd str: %s\n", sqlite_cmd_str);
	return sqlite_execute(sqlite_cmd_str);
}

static INSTRUCTION_RESULT_E model_delete(INSTRUCTION_S *instruction)
{
	char sqlite_cmd_str[SQLITECMDLEN];
	memset(sqlite_cmd_str, 0, sizeof(sqlite_cmd_str));

	///	delete the value in the time according controlValue
	// sprintf(cmdStr,"DELETE FROM time WHERE controlVal=%d;",l_controlValue);
	
	sprintf(sqlite_cmd_str,"DELETE FROM model WHERE modeID=%d;",instruction->arg2);
	DEBUG("sqlite cmd str: %s\n", sqlite_cmd_str);
	INSTRUCTION_RESULT_E ret = sqlite_execute(sqlite_cmd_str);
	if(RESULT_OK!=ret){
		DEBUG("delete model from \"model\" table failed\n");
		return ret;
	}

	sprintf(sqlite_cmd_str,"DELETE FROM modtime WHERE modeID=%d;",instruction->arg2);
	DEBUG("sqlite cmd str: %s\n", sqlite_cmd_str);
	return sqlite_execute(sqlite_cmd_str);
}

static INSTRUCTION_RESULT_E timing_task_delete(INSTRUCTION_S *instruction)
{
	char sqlite_cmd_str[SQLITECMDLEN];

	/*
	在实测时，当在UI上更新一个定时任务时，服务器先发送一个删除旧任务的指令，然后再发送添加新任务的指令。
	但是删除旧任务的“参数1+参数2+频度+时间戳”是新任务的参数，无法识别。
	*/
#if 0
	int control_val = appoint_str2int(instruction->alterable_entity, strlen(instruction->alterable_entity), 0, 4, 16);
	int frequency = appoint_str2int(instruction->alterable_entity, strlen(instruction->alterable_entity), 4, 2, 16);
	int control_time = appoint_str2int(instruction->alterable_entity, strlen(instruction->alterable_entity), 6, 10, 10);
	
	snprintf(sqlite_cmd_str,sizeof(sqlite_cmd_str),"DELETE FROM time WHERE (cmdType=1 AND controlVal=%d AND frequency=%d AND controlTime=%d);",
																control_val, frequency, control_time);
#else
	snprintf(sqlite_cmd_str,sizeof(sqlite_cmd_str),"DELETE FROM time WHERE (typeID=%d);",
																instruction->type_id);
#endif
	//DEBUG("sqlite cmd str: %s\n", sqlite_cmd_str);
	return sqlite_execute(sqlite_cmd_str);
}

static INSTRUCTION_RESULT_E timing_task_update(INSTRUCTION_S *instruction)
{
	char sqlite_cmd_str[SQLITECMDLEN];
	memset(sqlite_cmd_str, 0, sizeof(sqlite_cmd_str));

	int old_control_val = appoint_str2int(instruction->alterable_entity, strlen(instruction->alterable_entity), 0, 4, 16);
	int old_frequency = appoint_str2int(instruction->alterable_entity, strlen(instruction->alterable_entity), 4, 2, 16);
	int old_control_time = appoint_str2int(instruction->alterable_entity, strlen(instruction->alterable_entity), 6, 10, 10);

	int control_val = appoint_str2int(instruction->alterable_entity, strlen(instruction->alterable_entity), 16, 4, 16);
	int frequency = appoint_str2int(instruction->alterable_entity, strlen(instruction->alterable_entity), 20, 2, 16);
	int control_time = appoint_str2int(instruction->alterable_entity, strlen(instruction->alterable_entity), 22, 10, 10);
	char *p_remark = instruction->alterable_entity + 32;
	
	sprintf(sqlite_cmd_str,"UPDATE time SET controlVal=%d,frequency=%d,controlTime=%d,remark='%s' WHERE (controlVal=%d AND frequency=%d AND controlTime=%d);",
											control_val, frequency, control_time, p_remark, old_control_val, old_frequency, old_control_time);
	DEBUG("sqlite cmd str: %s\n", sqlite_cmd_str);
	return sqlite_execute(sqlite_cmd_str);
}

static INSTRUCTION_RESULT_E verify_address(INSTRUCTION_S *instruction)
{
	if(NULL==instruction){
		DEBUG("can not treat with NULL structure\n");
		return ERR_OTHER;
	}
	
	INSTRUCTION_RESULT_E ret = RESULT_OK;
	SMART_SOCKET_ACTION_E smart_socket_action = SMART_SOCKET_ADDR_CONFIRM;
	int serial_cmd_len = 0;
	unsigned char serial_cmd[SERIAL_CMD_SIZE];
	memset(serial_cmd, 0, sizeof(serial_cmd));
	
	if(0x02==instruction->arg2 && 0x07==instruction->alterable_flag){
		DEBUG("verify socket address\n");
		serial_cmd_len = smart_socket_serial_cmd_splice(serial_cmd,sizeof(serial_cmd),smart_socket_action,instruction->alterable_entity);
		if(serial_cmd_len>0)
		{
			int recv_serial_len = serial_access(serial_cmd, serial_cmd_len, sizeof(serial_cmd));
			if(recv_serial_len>0)
			{
				double result = 0.0;
				smart_socket_serial_cmd_parse(serial_cmd, recv_serial_len, smart_socket_action, instruction->alterable_entity, &result);
				DEBUG("result=%lf\n", result);
				if(1==(int)(result))
					ret = RESULT_OK;
				else
					ret = ERR_OTHER;
			}
			else{
				DEBUG("receive from serial failed\n");
				ret = ERR_SERIAL;
			}
		}
		else{
			DEBUG("splice serial cmd failed\n");
			ret = ERR_FORMAT;
		}
	}
	else{
		DEBUG("other device address can not support immediatly\n");
		ret = ERR_FORMAT;
	}
	
	return ret;
}

INSTRUCTION_RESULT_E instruction_dispatch(INSTRUCTION_S *instruction)
{
	int ret = 0;
	time_t stb_time;
	int vendor_id;
	
	switch(instruction->type){
		case INSTRUCTION_CTRL:
			if(0x00==instruction->alterable_flag){		// 即时任务
				vendor_id = (instruction->type_id >> 16) & 0xff;
				DEBUG("immediatly task, vendor id: 0x%02x\n", vendor_id);
				if(0xfe==instruction->arg1){			// 立即执行arg2指定的模式
					DEBUG("do model task by model_id, %d\n", instruction->arg2);
					
					char typeIDs[4096];
					memset(typeIDs, 0, sizeof(typeIDs));
					INSTRUCTION_RESULT_E ret = exec_model_with_id(instruction->arg2, typeIDs, sizeof(typeIDs));
					if(RESULT_OK==ret){
						/*
						在此处上报插座状态有点儿早。可能导致主动上报状态比模式执行结果更早的返回给服务器
						*/
						sockets_status_report(typeIDs);
					}
					return ret;
				}
				else{									// 普通的即时任务。来源有两处：1、服务器直接下发；2、先通过模式设定即时任务，然后通过模式执行即时任务。
					if(0x80 > vendor_id)				// 为啥有这个判断？？
					{
						DEBUG("normal task in control type, arg1: 0x%02x\n", instruction->arg1);
						return immediatly_task_run(instruction);
					}
					else
					{
						DEBUG("this instruction can not support\n");
						return ERR_FORMAT;
					}
				}
			}
			else if(0x01==instruction->alterable_flag){	// 定时任务
				DEBUG("add timing task\n");
				return instruction_timing_task_add(instruction);
			}
			else if(0x02==instruction->alterable_flag){	// 模式任务
				DEBUG(" add model task\n");
				return instruction_model_task_add(instruction);
			}
			break;
		case INSTRUCTION_INQUIRE:
			DEBUG("inquiry kind command, arg1=0x%02x, arg2=0x%02x\n", instruction->arg1, instruction->arg2);
			switch (instruction->arg1){
				case 0x01:
					return inquire_electrical_status(instruction, SMART_SOCKET_RELAY_STATUS_READ);
					break;
				case 0x02:
					if(0x01==instruction->arg2 && 0x00==instruction->alterable_flag\
						&& 0x000000!=instruction->type_id) ///active power inquiry
					{
						DEBUG("single device active power inquiry.\n");
#if 0
						return inquire_active_power(instruction, 1);
#else
						return inquire_electrical_status(instruction, SMART_SOCKET_ACTIVE_POWER_READ);
#endif
					}
					if(0x02==instruction->arg2  && 0x03==instruction->alterable_flag\
						&& 0x000000==instruction->type_id){
#if 0
						DEBUG("inquiry all device's active-power between begin-time to end-time\n");
						return inquire_active_power(instruction, 0);
#else
						DEBUG("single device recative power inquiry.\n");
						return inquire_electrical_status(instruction, SMART_SOCKET_REACTIVE_POWER_READ);
#endif
					}
					else
						return ERR_FORMAT;
					break;
				case 0x03:
					if(0x01==instruction->arg2 && 0x04==instruction->alterable_flag){
						DEBUG("inquiry all device's total elec-powr.\n");
						return inquire_power_consumption(instruction);
					}
					else{
						DEBUG("arg2=0x%02x, alterable_flag=0x%02x\n", instruction->arg2, instruction->alterable_flag);
						return ERR_FORMAT;
					}
					break;
				case 0x04:
					//0x01==instruction->arg2 && 
					if(0x00==instruction->alterable_flag){
						DEBUG("inquire timing task\n");
						return inquire_timing_task(instruction);
					}
					else{
						DEBUG("err: arg2=0x%02x, alterable_flag=0x%02x\n", instruction->arg2, instruction->alterable_flag);
						return ERR_FORMAT;
					}
					break;
				case 0x05:
					if(0x00==instruction->alterable_flag && 0x000000==instruction->type_id){
						DEBUG("inquire equipment list\n");
						return inquire_equipment_list(instruction);
					}
					else{
						DEBUG("err: arg2=0x%02x, alterable_flag=0x%02x\n", instruction->arg2, instruction->alterable_flag);
						return ERR_FORMAT;
					}
					break;
				case 0x06:
					DEBUG("inquire smart electrical\n");
					// has some error here
					return ERR_OTHER;//inquire_smart_electrical(instruction);
					break;
				case 0x07:
					DEBUG("verify address\n");
					return verify_address(instruction);
					break;
				case 0x08:
					DEBUG("can not support this inquire 0x08\n");
					return ERR_FORMAT;
					break;
				case 0xfe:
					if(0x00==instruction->alterable_flag){
						DEBUG("inquire model by id(0x%d)\n", instruction->arg2);
						return inquire_model(instruction);
					}
					else{
						DEBUG("instruction error, alterable_flag=%d\n", instruction->alterable_flag);
						return ERR_FORMAT;
					}
					break;
				default:
					break;
			}
			break;
		case INSTRUCTION_OP:
			DEBUG("operation task, arg1=0x%02x, alterable_flag=0x%02x\n", instruction->arg1, instruction->alterable_flag);
			switch(instruction->arg1){
				case 0x01:
					if(0x05==instruction->alterable_flag){
						DEBUG("insert a electric equipment\n");
						int tmp_ret = electric_equipment_insert(instruction);
						DEBUG("insert a electric equipment, return with %d\n", tmp_ret);
						return tmp_ret;
					}
					else{
						DEBUG("the alterable_flag is invalid\n");
						return ERR_FORMAT;
					}
					break;
				case 0x02:
					if(0x05==instruction->alterable_flag)
						return electric_equipment_update(instruction);
					else{
						DEBUG("the alterable_flag is invalid\n");
						return ERR_FORMAT;
					}
					break;
				case 0x03:
					DEBUG("delete equipment, typeID=0x%06x\n", instruction->type_id);
					return electric_equipment_delete(instruction);
					break;
				case 0x04:
					DEBUG("update model, model_id=0x%02x\n", instruction->arg2);
					return model_update(instruction);
					break;
				case 0x05:
					DEBUG("delete model, model_id=0x%02x\n", instruction->arg2);
					return model_delete(instruction);
					break;
				case 0x06:
					DEBUG("delete timing task, model_id=0x%02x\n", instruction->arg2);
					return timing_task_delete(instruction);
					break;
				case 0x07:
					DEBUG("update timing task\n");
					return timing_task_update(instruction);
					break;
				default:
					return ERR_FORMAT;
					break;
			}
			break;
		case INSTRUCTION_OTHER:
			if(0x01==instruction->arg1){
				if(0x01==instruction->arg2 && 0x06==instruction->alterable_flag){
					g_smart_power_time.server_time = atoi(instruction->alterable_entity);
					stb_time = time((time_t *)NULL);
					g_smart_power_time.difference_time = difftime(g_smart_power_time.server_time,stb_time);
					
					DEBUG("set server time: %ld. And stb time is %ld, difference time is %f\n", g_smart_power_time.server_time, stb_time, g_smart_power_time.difference_time);
					
					time_t t;
					struct tm gmt_tm, local_tm;
					tzset(); /* tzset()*/
					t = g_smart_power_time.server_time;
					localtime_r(&t, &local_tm);
					DEBUG("Server Local time is: %s", asctime(&local_tm));
					gmtime_r(&t, &gmt_tm);
					DEBUG("Server GMT time   is: %s", asctime(&gmt_tm));

					time_rectify_flag_reset();
					return RESULT_OK;
				}
				else if(0x02==instruction->arg2 && 0x00==instruction->alterable_flag){
					stb_time = time((time_t *)NULL)+g_smart_power_time.difference_time;
					memset(instruction->alterable_entity, 0, sizeof(instruction->alterable_entity));
					snprintf(instruction->alterable_entity, sizeof(instruction->alterable_entity), "%ld\n", stb_time);
					return RESULT_ALTERABLE_ENTITY_FILL_OK;
				}
			}
			else if(0x04==instruction->arg1){
				if(0x01==instruction->arg2 && 0x00==instruction->alterable_flag){
					char smart_power_version[33];
					memset(smart_power_version, 0, sizeof(smart_power_version));
					softwareVersion_get(smart_power_version, sizeof(smart_power_version));
					smart_power_version[32] = '\0';

					memset(instruction->alterable_entity, 0, sizeof(instruction->alterable_entity));
					strcpy(instruction->alterable_entity, smart_power_version);
					return RESULT_ALTERABLE_ENTITY_FILL_OK;
				}
			}
			
			break;
		default:
			DEBUG("this type(%d) of instruction can not be handled\n", instruction->type);
			return ERR_OTHER;
			break;
	}
	return ret;
}

int instruction_difftime_get(void)
{
	int diff_time = (int)g_smart_power_time.difference_time;
	return diff_time;
}

/*
功能：	通过定时任务插入的指令，单独处理。目前是定时查询有功功率和电量
*/
int instruction_insert(INSTRUCTION_S *inst)
{
	int i = 0;
	char fifo_str[32];
	int ret = -1;

	sem_wait(&s_sem_insert_insts);
	for(i=0; i<INSTRUCTION_INSERT_NUM; i++){
		if(-1==g_insert_insts[i].alterable_flag){
			memcpy(&g_insert_insts[i], inst, sizeof(INSTRUCTION_S));
			break;
		}
	}
	if(INSTRUCTION_INSERT_NUM==i)
		ret = -1;
	else{
		memset(fifo_str, 0, FIFO_STR_SIZE);
		strncpy(fifo_str, MSGSTR_INSTRUCTION_SELF, FIFO_STR_SIZE-1);
		if(-1==write(fifo_fd, fifo_str, strlen(fifo_str))){
			ERROROUT("write to fifo_2_instruction failed\n");
			ret = -1;
		}
		else{
			DEBUG("write to fifo_2_instruction: %s\n", fifo_str);
			ret = 0;
		}
	}
	sem_post(&s_sem_insert_insts);
	
	return ret;
}

int sqlite_read_basic_power_callback(char **result, int row, int column, void *receiver)
{
	DEBUG("row=%d, column=%d, receiver add=%p\n", row, column, receiver);
	if(row>0){
		*((double *)receiver) = atof(result[1*column]);
		DEBUG("basic power data: %08f\n", *((double *)receiver));
	}
	
	return 0;
}

/*
功能：	单独处理通过定时任务插入的指令。目前是定时查询有功功率和电量
*/
void instruction_insert_poll(void)
{
	int i=0;
	int j=0;
	int type_id = 0;
	char socket_id[32];
	unsigned char serial_cmd[128];
	char sqlite_cmd[128];
	double power = 0.0;
	SMART_SOCKET_ACTION_E smart_socket_action = SMART_SOCKET_ACTION_UNDEFINED;

	sem_wait(&s_sem_insert_insts);
	EQUIPMENT_S tmp_equipments[EQUIPMENT_NUM];
	if(0!=equipments_get(&tmp_equipments)){
		DEBUG("can not read equipments\n");
	}
	else{
		for(i=0; i<INSTRUCTION_INSERT_NUM; i++){
			if(-1!=g_insert_insts[i].alterable_flag){
				for(j=0;j<EQUIPMENT_NUM;j++)
				{
					if(-1!=tmp_equipments[j].type_id)
					{
						type_id = tmp_equipments[j].type_id;
						memset(socket_id, 0, sizeof(socket_id));
						strcpy(socket_id, tmp_equipments[j].socket_id);
						
						memset(serial_cmd, 0, sizeof(serial_cmd));
						if(0x02==g_insert_insts[i].arg1)
							smart_socket_action = SMART_SOCKET_ACTIVE_POWER_READ;
						else if(0x03==g_insert_insts[i].arg1)
							smart_socket_action = SMART_SOCKET_ACTIVE_POWER_CONSUMPTION_READ;
						else{
							DEBUG("can not process such arg1: %d\n", g_insert_insts[i].arg1);
							break;
						}
							
						int serial_cmd_len = smart_socket_serial_cmd_splice(serial_cmd,sizeof(serial_cmd), smart_socket_action,socket_id);
						if(serial_cmd_len>0)
						{
							int recv_serial_len = serial_access(serial_cmd, serial_cmd_len, sizeof(serial_cmd));
							if(recv_serial_len>0)
							{
								if(RESULT_OK==smart_socket_serial_cmd_parse(serial_cmd,recv_serial_len,smart_socket_action,socket_id,&power))
								{
									if(power<0){
										DEBUG("perhaps some error, catch power little 0. translate it as 0\n");
										power = 0;
									}
									
									memset(sqlite_cmd, 0, sizeof(sqlite_cmd));
									if(SMART_SOCKET_ACTIVE_POWER_READ==smart_socket_action){
										snprintf(sqlite_cmd,sizeof(sqlite_cmd),"INSERT INTO actpower(typeID,hourTime,data,status) VALUES(%d,%d,%lf,0);",\
												type_id,(int)time(NULL)+smart_power_difftime_get(),power);
									}
									else if(SMART_SOCKET_ACTIVE_POWER_CONSUMPTION_READ==smart_socket_action){
										
										/*
										status==2表明此记录是上次采集电量时的数据，将本次采集的电量power和上次记录的电量之间的差值录入数据库。
										第一次建立表格时，此typeID没有对应的status==2的记录，上报的电量等于采集的电量。
										*/
										double basic_power_data = 0.0;
										int (*p_sqlite_read_basic_power_callback)(char **,int,int,void *) = sqlite_read_basic_power_callback;
										
										snprintf(sqlite_cmd, sizeof(sqlite_cmd), "SELECT data FROM power WHERE typeID=%d AND status=2;", type_id);
										int ret = sqlite_read(sqlite_cmd, (void *)(&basic_power_data), p_sqlite_read_basic_power_callback);
										if(ret>RESULT_OK){
											snprintf(sqlite_cmd, sizeof(sqlite_cmd), "UPDATE power SET data=%lf WHERE typeID=%d AND status=2;", power, type_id);
										}
										else{
											DEBUG("no basic power data, have to initial it\n");
											snprintf(sqlite_cmd,sizeof(sqlite_cmd),"INSERT INTO power(typeID,hourTime,data,status) VALUES(%d,%d,%lf,2);",\
													type_id,(int)time(NULL)+smart_power_difftime_get(),power);
										}
										sqlite_execute(sqlite_cmd);
										
										power -= basic_power_data;
										if(power<0){
											DEBUG("shit! calculate power little than 0(%lf-%lf), translate is as 0", power, basic_power_data);
											power=0;
										}
										snprintf(sqlite_cmd,sizeof(sqlite_cmd),"INSERT INTO power(typeID,hourTime,data,status) VALUES(%d,%d,%lf,0);",\
												type_id,(int)time(NULL)+smart_power_difftime_get(),power);
									}
	
									DEBUG("insert power sqlite cmd str: %s\n", sqlite_cmd);
									sqlite_execute(sqlite_cmd);
								}
							}
						}
					}
				}
				
				g_insert_insts[i].alterable_flag = -1;
			}
		}
	}
	sem_post(&s_sem_insert_insts);
	
	return;
}

void instruction_mainloop()
{
	INSTRUCTION_S				instruction;
	int index_p = -1;
	char instruction_str[4096];
	struct timeval tv_select = {7, 717000};
	fd_set rdfds;
	int ret_select = -1;
	fifo_fd = open(FIFO_2_INSTRUCTION, O_RDWR|O_NONBLOCK, 0);
	char fifo_str[FIFO_STR_SIZE];
	INSTRUCTION_RESULT_E inst_result = RESULT_OK;
	
	if(fifo_fd<0){
		ERROROUT("open fifo_2_instruction failed\n");
		return;
	}
	else
		DEBUG("open FIFO_2_INSTRUCTION with fd %d\n", fifo_fd);
	
	int fifoout_fd = open(FIFO_2_SOCKET, O_RDWR|O_NONBLOCK, 0);
	if(fifoout_fd<0){
		ERROROUT("open fifo_2_socket failed\n");
		return;
	}
	else
		DEBUG("open FIFO_2_SOCKET with fd %d\n", fifoout_fd);
	
	while(1)
	{
		FD_ZERO(&rdfds);
		FD_SET(fifo_fd, &rdfds);
		tv_select.tv_sec = 1137;
		tv_select.tv_usec = 717000;
		ret_select = select(fifo_fd+1, &rdfds, NULL, NULL, &tv_select);
		if(ret_select<0){
			ERROROUT("select faild\n");
			return;
		}
		else if(0==ret_select){
			DEBUG("timeout\n");
			continue;
		}
		else{	// ret_select>0
			if(FD_ISSET(fifo_fd, &rdfds)){
				memset(fifo_str, 0, FIFO_STR_SIZE);
				if(read(fifo_fd, fifo_str, FIFO_STR_SIZE)<0){
					ERROROUT("read from fifo_2_instruction failed\n");
				}
				else{
					DEBUG("read from fifo_fd: %s\n", fifo_str);
					if(0==strcmp(fifo_str, MSGSTR_2_INSTRUCTION)){
						index_p = smart_power_cmds_open(CMD_ARRAY_OP_PROCESS, BOOL_FALSE);
						if(-1!=index_p){
							DEBUG("processing index: %d\n", index_p);
							memset(instruction_str, 0, sizeof(instruction_str));
							instruction_reset(&instruction);
							if(0==smart_power_instruction_get(index_p, instruction_str, sizeof(instruction_str))){
								DEBUG("get instruction: %s\n", instruction_str);
								inst_result = instruction_parse(instruction_str, sizeof(instruction_str), &instruction);
								instruction.index_in_cmds = index_p;
								if(-1==inst_result ){
									DEBUG("instruction phase failed\n");
								}
								else{
									inst_result = instruction_dispatch(&instruction);
								}

								instruction.alterable_flag = alterable_flag_result(instruction.alterable_flag);
								if(RESULT_ALTERABLE_ENTITY_FILL_OK!=inst_result){
									DEBUG("the return alterable entity str will be filled manually, inst_result=%d\n", inst_result);
									memset(instruction.alterable_entity, 0, sizeof(instruction.alterable_entity));
									alterable_entity_result(inst_result, instruction.alterable_entity, sizeof(instruction.alterable_entity));
								}
								else
									DEBUG("the return alterable entity str is filled automaticly\n");
									
								memset(instruction_str, 0, sizeof(instruction_str));
								instruction_splice(&instruction, instruction_str, sizeof(instruction_str));
								smart_power_instruction_set(index_p, instruction_str, sizeof(instruction_str));
							}
							smart_power_cmds_close(index_p, 1);
							
							memset(fifo_str, 0, FIFO_STR_SIZE);
							strncpy(fifo_str, MSGSTR_2_SOCKET, FIFO_STR_SIZE-1);
							if(-1==write(fifoout_fd, fifo_str, strlen(fifo_str))){
								ERROROUT("write to fifo_2_socket failed\n");
							}
							else
								DEBUG("write to fifo_2_socket: %s\n", fifo_str);
						}
					}
					else if(0==strcmp(fifo_str, MSGSTR_INSTRUCTION_SELF)){
						instruction_insert_poll();
					}
					else
						DEBUG("it is a wild msg, ignore it\n");
				}
			}
			else
				DEBUG("some fd can read, but not fifo_fd\n");
		}
	}
}

int smart_power_difftime_get(void)
{
	return (int)(g_smart_power_time.difference_time);
}

