#include <stdio.h>
#include <sys/socket.h>
#include <netinet/tcp.h>
#include <sys/mman.h>
#include <sys/ioctl.h>
#include <sys/time.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <net/if.h>
#include <unistd.h>
#include <string.h>
#include <errno.h>
#include <pthread.h>
#include <fcntl.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <semaphore.h>

#include "common.h"
#include "socket.h"
#include "porting.h"
#include "timing.h"
#include "sqlite.h"

static SMART_POWER_CMD_S 	g_smart_power_cmds[SMART_POWER_CMD_NUM];
static sem_t				s_sem_smart_power_cmds;
static unsigned int			g_smart_power_cmds_index_w = 0;
static unsigned int 		g_smart_power_cmds_index_process = 0;
static unsigned int			g_smart_power_cmds_index_r = 0;
static unsigned int			g_smart_power_cmds_id = 0;
static SMART_POWER_CMD_S	g_sync_cmds[CMD_SYNC_TIME-CMD_SYNC_DEVS+1];
static int					g_heartbeat_timer_id = -1;
static int					g_fifo_fd = -1;
static SOCKET_STATUS_E		g_socket_status = SOCKET_STATUS_CLOSED;

static int sendToServer(int l_socket_fd,char *l_send_buf, unsigned int buf_len);
static int recvFromServer(int l_socket_fd,char *l_recv_buf, unsigned int recv_buf_len);
static int connectRetry(int l_socket_fd,struct sockaddr_in server_addr);
static int smart_power_cmds_init(void);
static int continue_myself(int g_fifo_fd);

#ifdef WORK_NORMAL_NOT_TEST
#else
extern char *test_cmdstr_get(void);
static int test_for_one = 0;
#endif


int socket_init()
{
	if(-1==smart_power_cmds_init())
		return -1;
	
	g_socket_status = SOCKET_STATUS_CLOSED;
	if( mkfifo(FIFO_2_SOCKET, O_CREAT|O_EXCL)<0 && EEXIST!=errno ){
		ERROROUT("create FIFO_2_SOCKET:%s failed\n", FIFO_2_SOCKET);
		return -1;
	}
	
#if 0
	if( mkfifo(FIFO_SOCKET_SELF, O_CREAT|O_EXCL)<0 && EEXIST!=errno ){
		ERROROUT("create FIFO_SOCKET_SELF failed\n");
		return -1;
	}
	else
		DEBUG("mkfifo(FIFO_SOCKET_SELF) success\n");
#endif

	return 0;
}

static int smart_power_cmds_init(void)
{
	int i = 0;

	if(-1==sem_init(&s_sem_smart_power_cmds, 0, 1)){
		DEBUG("s_sem_timer init failed\n");
		return -1;
	}
	sem_wait(&s_sem_smart_power_cmds);
	for(i=0; i<SMART_POWER_CMD_NUM; i++)
	{
		g_smart_power_cmds[i].status = CMD_STATUS_NULL;
		g_smart_power_cmds[i].id = 0;
		g_smart_power_cmds[i].type = CMD_HEADER_UNDEFINED;
		g_smart_power_cmds[i].send_try = 0;
		memset(g_smart_power_cmds[i].serv_str, 0, sizeof(g_smart_power_cmds[i].serv_str));
		memset(g_smart_power_cmds[i].entity, 0, sizeof(g_smart_power_cmds[i].entity));
	}
	sem_post(&s_sem_smart_power_cmds);
	for(i=0; i<CMD_SYNC_TIME-CMD_SYNC_DEVS+1; i++){
		g_sync_cmds[i].status = CMD_STATUS_NULL;
		g_sync_cmds[i].id = 0;
		g_sync_cmds[i].type = CMD_HEADER_UNDEFINED;
		g_smart_power_cmds[i].send_try = 0;
		memset(g_sync_cmds[i].serv_str, 0, sizeof(g_sync_cmds[i].serv_str));
		memset(g_sync_cmds[i].entity, 0, sizeof(g_sync_cmds[i].entity));
	}

	g_smart_power_cmds_id = 0;
	g_smart_power_cmds_index_w = 0;
	g_smart_power_cmds_index_process = 0;
	g_smart_power_cmds_index_r = 0;
	return 0;
}

/*
功能：	打开命令数组，将根据不同的操作类型和插入标记（insert_flag）决定数组条目的状态转换
输入：	cmd_op——操作类型：
							CMD_ARRAY_OP_W		——打开数组，准备插入新一条命令
							CMD_ARRAY_OP_PROCESS——打开数组，准备对插入的命令进行处理
							CMD_ARRAY_OP_R		——打开数组，准备对处理完毕的命令进行上报，如果上报成功，将删除这条命令
		insert_flag——插入标记
							通常情况下write、process、read之间有严格的前后次序，但如果insert_flag为真的话，则可以在空白记录上直接进行process，即跳过write动作。
							此选项用来插入来自系统内部的指令，一般来说是由定时器产生的。这些指令不是来自服务器，所以没有正常的write动作。
返回：	
说明：	为了避免处在数组后部的命令由于新命令频繁而得不到处理，对于write、process、read操作都记录了开始查找的位置，避免饥饿。
*/
unsigned int smart_power_cmds_open(CMD_ARRAY_OP_E cmd_op, BOOL_E insert_flag)
{
	int i_start = 0;
	char op_note[32];

	sem_wait(&s_sem_smart_power_cmds);
	memset(op_note, 0, sizeof(op_note));
	switch(cmd_op){
		case CMD_ARRAY_OP_W:
			i_start = g_smart_power_cmds_index_w;
			strncpy(op_note, "CMD_ARRAY_OP_W", 32);
			break;
		case CMD_ARRAY_OP_PROCESS:
			i_start = g_smart_power_cmds_index_process;
			strncpy(op_note, "CMD_ARRAY_OP_PROCESS", 32);
			break;
		case CMD_ARRAY_OP_R:
			i_start = g_smart_power_cmds_index_r;
			strncpy(op_note, "CMD_ARRAY_OP_R", 32);
			break;
		default:
			DEBUG("this op(%d) can not be processed now\n", cmd_op);
			sem_post(&s_sem_smart_power_cmds);
			return -1;
	}
	int i = i_start;
	//DEBUG("poll smart power cmds array start from index: %d\n", i);
	
	for(; i<i_start+SMART_POWER_CMD_NUM; i++){
		if(CMD_ARRAY_OP_W==cmd_op && CMD_STATUS_NULL==g_smart_power_cmds[i%SMART_POWER_CMD_NUM].status){
			g_smart_power_cmds[i%SMART_POWER_CMD_NUM].status = CMD_STATUS_WRITING;
			g_smart_power_cmds[i%SMART_POWER_CMD_NUM].id = g_smart_power_cmds_id;

			memset(g_smart_power_cmds[i%SMART_POWER_CMD_NUM].serv_str, 0, sizeof(g_smart_power_cmds[i%SMART_POWER_CMD_NUM].serv_str));
			memset(g_smart_power_cmds[i%SMART_POWER_CMD_NUM].entity, 0, sizeof(g_smart_power_cmds[i%SMART_POWER_CMD_NUM].entity));
			
			g_smart_power_cmds_id++;
			break;
		}
		else if(CMD_ARRAY_OP_PROCESS==cmd_op && (	CMD_STATUS_WRITED==g_smart_power_cmds[i%SMART_POWER_CMD_NUM].status
													||	(CMD_STATUS_NULL==g_smart_power_cmds[i%SMART_POWER_CMD_NUM].status
														&&	BOOL_TRUE==insert_flag))){
			g_smart_power_cmds[i%SMART_POWER_CMD_NUM].status = CMD_STATUS_PROCESSING;
			break;
		}
		else if(CMD_ARRAY_OP_R==cmd_op && CMD_STATUS_PROCESSED==g_smart_power_cmds[i%SMART_POWER_CMD_NUM].status){
			g_smart_power_cmds[i%SMART_POWER_CMD_NUM].status = CMD_STATUS_READING;
			g_smart_power_cmds[i%SMART_POWER_CMD_NUM].send_try = 0;
			break;
		}
	}

	if(i>=(i_start+SMART_POWER_CMD_NUM)){	// (SMART_POWER_CMD_NUM-i_start)
		DEBUG("can not open an valid cmd site, i=%d, NUM=%d, i_start=%d\n", i, SMART_POWER_CMD_NUM, i_start);
		i = -1;
	}
	sem_post(&s_sem_smart_power_cmds);
	
	if(i>SMART_POWER_CMD_NUM)
		i -= SMART_POWER_CMD_NUM;
	DEBUG("cmds array open with operation \"%s\", return with index %d\n", op_note, i);
	return i;
}

// close flag: 1 -- normal close, and transform the status
// close flag: 0 -- close, and DO NOT transform the status
// close flag: -1 -- close, and abandon the cmd record
int smart_power_cmds_close(unsigned int index, int close_flag)
{
	if(index<0 || index>=SMART_POWER_CMD_NUM){
		DEBUG("this index(%d) is invalid\n", index);
		return -1;
	}

	sem_wait(&s_sem_smart_power_cmds);
	if(-1==close_flag){
		g_smart_power_cmds[index].status= CMD_STATUS_NULL;
		DEBUG("g_smart_power_cmds[%d] is abandon\n", index);
	}
	else
	{
		if(g_smart_power_cmds[index].status>=CMD_STATUS_WRITING 
			&& g_smart_power_cmds[index].status<CMD_STATUS_WRITED){
			if(1==close_flag){
				g_smart_power_cmds[index].status = CMD_STATUS_WRITED;
				g_smart_power_cmds_index_w = (g_smart_power_cmds_index_w+1)%SMART_POWER_CMD_NUM;

				// only for debug
				DEBUG("g_smart_power_cmds[%d].status=%d\n", index,g_smart_power_cmds[index].status);
				DEBUG("g_smart_power_cmds[%d].id=%d\n", index,g_smart_power_cmds[index].id);
				DEBUG("g_smart_power_cmds[%d].type=%d\n", index,g_smart_power_cmds[index].type);
				DEBUG("g_smart_power_cmds[%d].serv_str=%s\n", index,g_smart_power_cmds[index].serv_str);
				DEBUG("g_smart_power_cmds[%d].status=%s\n", index,g_smart_power_cmds[index].entity);
			}
			else if(0==close_flag)
				g_smart_power_cmds[index].status = CMD_STATUS_NULL;
		}
		else if(g_smart_power_cmds[index].status>=CMD_STATUS_PROCESSING
			&& g_smart_power_cmds[index].status<CMD_STATUS_PROCESSED){
			if(1==close_flag){
				g_smart_power_cmds[index].status = CMD_STATUS_PROCESSED;
				g_smart_power_cmds_index_process = (g_smart_power_cmds_index_process+1)%SMART_POWER_CMD_NUM;
			}
			else if(0==close_flag)
				g_smart_power_cmds[index].status = CMD_STATUS_WRITED;
		}
		else if(g_smart_power_cmds[index].status>=CMD_STATUS_READING
			&& g_smart_power_cmds[index].status<CMD_STATUS_READED){
			if(1==close_flag){
				g_smart_power_cmds[index].status = CMD_STATUS_NULL;	//CMD_STATUS_READED;
				g_smart_power_cmds_index_r = (g_smart_power_cmds_index_r+1)%SMART_POWER_CMD_NUM;
			}
			else if(0==close_flag)
				g_smart_power_cmds[index].status = CMD_STATUS_PROCESSED;
		}
	}
	sem_post(&s_sem_smart_power_cmds);

	return 0;
}

static int issue_cmd_basic_check(char *cmd_str, unsigned int str_len)
{
	int i = 0;
	int n = 0;

	// do str*** action with a NULL string will get segmentation error
	if(NULL==cmd_str || str_len<strlen("#tt##1#1#"))
		return -1;

	for(i=0; i<str_len; i++){
		if('\0'==cmd_str[i])
			break;
		else if('#'==cmd_str[i])
			n++;
	}
	if(n>5)
		return 1;
	else
		return -1;
}
static CMD_HEADER_E smart_power_cmd_parse(char *cmd_str, unsigned int str_len)
{
	CMD_HEADER_E cmd_header= CMD_HEADER_UNDEFINED;
	char *p_str = NULL;
	char *p_mark = NULL;
	char *p_tmp_entity = NULL;
	unsigned int index_w = 0;
	char tmp_serv_str[128];
	
	if(NULL==cmd_str || 0==str_len){
		cmd_header = CMD_HEADER_INVALID;
	}
	else if(0==strncmp(cmd_str, "#rs#", 4)){
		cmd_header = CMD_HEADER_REGIST;
	}
	else if(0==strncmp(cmd_str, "#ff#", 4)){
		cmd_header = CMD_HEADER_INVALID;
	}
	else if(0==strncmp(cmd_str, "#tt#", 4)){
		cmd_header = CMD_HEADER_INVALID;
		if(1==issue_cmd_basic_check(cmd_str, str_len)){
			cmd_header = CMD_HEADER_ISSUE;
			
			p_str = cmd_str+strlen("#tt#");
			p_mark = strchr(p_str, '#');
			if(p_mark){
				// should has a sem lock here;
				
				// store the server str
				memset(tmp_serv_str, 0, sizeof(tmp_serv_str));
				strncpy(tmp_serv_str, p_str, abs(p_mark-p_str));

				// overleap the serial num
				p_str = p_mark+1;
				p_mark = strchr(p_str, '#');
				p_tmp_entity = p_mark;			// only for normal issue commond

				// check if has "sync"
				p_str = p_mark+1;
				p_mark = strchr(p_str, '#');
				if(p_mark && 0==strncmp(p_str, "#sync", abs(p_mark-p_str))){
					cmd_header = CMD_HEADER_SYNC;
					p_str = p_mark+1;
					p_mark = strchr(p_str, '#');
					if(p_mark){
						int sync_i = 0;
						if(0==strncmp(p_str, "#devs", abs(p_mark-p_str))){
							DEBUG("has command to \"sync devs\"\n");
							memset(&g_sync_cmds, 0, sizeof(g_sync_cmds));
							sync_i = CMD_SYNC_DEVS-CMD_SYNC_DEVS;
							g_sync_cmds[sync_i].type = CMD_SYNC_DEVS;
							strcpy(g_sync_cmds[sync_i].entity, p_mark);
						}
						else if(0==strncmp(p_str, "#modl", abs(p_mark-p_str))){
							DEBUG("has command to \"sync modl\"\n");
							sync_i = CMD_SYNC_MODL-CMD_SYNC_DEVS;
							g_sync_cmds[sync_i].type = CMD_SYNC_MODL;
							strcpy(g_sync_cmds[sync_i].entity, p_mark);
						}
						else if(0==strncmp(p_str, "#time", abs(p_mark-p_str))){
							DEBUG("has command to \"sync time\"\n");
							sync_i = CMD_SYNC_TIME-CMD_SYNC_DEVS;
							g_sync_cmds[sync_i].type = CMD_SYNC_TIME;
							strcpy(g_sync_cmds[sync_i].entity, p_mark);
							
							index_w = smart_power_cmds_open(CMD_ARRAY_OP_W, BOOL_FALSE);
							if(-1==index_w){
								DEBUG("the command array are full, this command will be discard\n");
								return -1;
							}
							g_smart_power_cmds[index_w].type = CMD_HEADER_SYNC;
							strcpy(g_smart_power_cmds[index_w].entity, "ff");
							smart_power_cmds_close(index_w, 1);
						}
						else{
							DEBUG("sync such (\"%s\") can not be dealed with\n", p_str);
							cmd_header = CMD_HEADER_INVALID;
						}
					}
					else{
						DEBUG("illeagal sync command string\n");
						cmd_header = CMD_HEADER_INVALID;
					}
				}
				else{
					DEBUG("normal issue command but not sync\n");
					cmd_header = CMD_HEADER_ISSUE;
					index_w = smart_power_cmds_open(CMD_ARRAY_OP_W, BOOL_FALSE);
					if(-1==index_w){
						DEBUG("the command array are full, this command will be discard\n");
						return -1;
					}
					g_smart_power_cmds[index_w].type = CMD_HEADER_ISSUE;
					strcpy(g_smart_power_cmds[index_w].serv_str, tmp_serv_str);
					strcpy(g_smart_power_cmds[index_w].entity, p_tmp_entity);

					smart_power_cmds_close(index_w, 1);
				}
				// should has a sem unlock here;
			}
		}
	}
	else if(0==strncmp(cmd_str, "#cc#", 4) 
			|| 0==strncmp(cmd_str, "#re#", 4)){
		cmd_header = CMD_HEADER_REPORTED;
	}
	else if(0==strncmp(cmd_str, "#am#", 4)){
		cmd_header = CMD_HEADER_ALARM;
	}
	else if(0==strncmp(cmd_str, "#reboot#", 4)){
		cmd_header = CMD_HEADER_REBOOT;
	}

	return cmd_header;
}

static int smart_power_cmd_splice(int index, char *buf, unsigned int buf_len)
{
	if(index<0 || index>=SMART_POWER_CMD_NUM || NULL==buf || 0==buf_len)
		return -1;

	char serial_num[33];	
	memset(serial_num, 0, sizeof(serial_num));
	serialNum_get(serial_num, sizeof(serial_num));

	DEBUG("g_smart_power_cmds[%d] need to upload, cmd type is %d\n", index, g_smart_power_cmds[index].type);
	switch(g_smart_power_cmds[index].type){	// notice: the entity string has two '#' mark around itself
		case CMD_HEADER_ISSUE:
			snprintf(buf, buf_len, "#cc#%s#%s%s", g_smart_power_cmds[index].serv_str, serial_num, g_smart_power_cmds[index].entity);
			break;
		case CMD_HEADER_REPORTED:
		case CMD_ACTIVE_REPORTED_ACTPOWER:
		case CMD_ACTIVE_REPORTED_POWER:
		case CMD_ACTIVE_REPORTED_STATUS:
#if 0		// 主动上报的命令串没有serv_str部分
			snprintf(buf, buf_len, "#re#%s#%s%s", g_smart_power_cmds[index].serv_str, serial_num, g_smart_power_cmds[index].entity);
#else
			snprintf(buf, buf_len, "#re#%s%s", serial_num, g_smart_power_cmds[index].entity);
#endif
			break;
		case CMD_HEADER_ALARM:
			snprintf(buf, buf_len, "#am#%s#%s%s", g_smart_power_cmds[index].serv_str, serial_num, g_smart_power_cmds[index].entity);	// perhaps need "#arm#" here?
			break;
		case CMD_HEADER_SYNC:
			snprintf(buf, buf_len, "#cc#%s#%s#sync%s", g_smart_power_cmds[index].serv_str, serial_num, g_smart_power_cmds[index].entity);
			break;
		default:
			snprintf(buf, buf_len, "#ff#%s#%s#", g_smart_power_cmds[index].serv_str, serial_num);
			break;
	}
	return 0;
}

int smart_power_instruction_get(int index_p, char *str, unsigned int str_len)
{
	if(index_p<0 || index_p>SMART_POWER_CMD_NUM || NULL==str || 0==str_len){
		DEBUG("argument input has some error\n");
		return -1;
	}

	strncpy(str, g_smart_power_cmds[index_p].entity, str_len);
	return 0;
}

int smart_power_instruction_set(int index_p, char *str, unsigned int str_len)
{
	if(index_p<0 || index_p>SMART_POWER_CMD_NUM || NULL==str || 0==str_len){
		DEBUG("argument input has some error\n");
		return -1;
	}

	strncpy(g_smart_power_cmds[index_p].entity, str, str_len);
	DEBUG("g_smart_power_cmds[%d].entity: %s\n", index_p, g_smart_power_cmds[index_p].entity);
	return 0;
}

int smart_power_active_reported_clear(const CMD_HEADER_E report_type)
{
	int i = 0;
	
	sem_wait(&s_sem_smart_power_cmds);
	for(i=0; i<SMART_POWER_CMD_NUM; i++){
		if( report_type==g_smart_power_cmds[i].type && CMD_STATUS_NULL!=g_smart_power_cmds[i].status){
			DEBUG("cancel this active reported, cmds[%d].type=%d, status=%d\n", i, report_type, g_smart_power_cmds[i].status);
			g_smart_power_cmds[i].status = CMD_STATUS_NULL;
		}
	}
	sem_post(&s_sem_smart_power_cmds);
	
	return 0;
}

/*
功能：	插入主动上报命令，来源为定时上报任务（有功功率、电量）和开关状态
注：主动上报开关状态是在非交互操作插座开关后触发的，包括：定时和模式任务执行后的状态。
*/
int cmd_insert(char *entity, CMD_HEADER_E report_type)
{
	DEBUG("insert a cmd mannually: %s\n", entity);
	int index_p = smart_power_cmds_open(CMD_ARRAY_OP_PROCESS, BOOL_TRUE);
	if(-1!=index_p){
		g_smart_power_cmds[index_p].id = 0;
		g_smart_power_cmds[index_p].type = report_type;
		snprintf(g_smart_power_cmds[index_p].entity, sizeof(g_smart_power_cmds[index_p].entity), "%s", entity);
		g_smart_power_cmds[index_p].status = CMD_STATUS_PROCESSED;
		smart_power_cmds_close(index_p, 1);
		DEBUG("has insert a cmd, will send to server entity=%s\n", entity);
		continue_myself(g_fifo_fd);
	}
	return index_p;
}

int smart_power_heartbeat_cmd_insert(void)
{
	char stb_ip[16];
//	struct sockaddr_in stb_addr;
	
	DEBUG("insert a heartbeat cmd to smart power cmds\n");
	int index_p = smart_power_cmds_open(CMD_ARRAY_OP_PROCESS, BOOL_TRUE);
	if(-1!=index_p){
		memset(stb_ip, 0, sizeof(stb_ip));
		//stb_addr.sin_addr.s_addr = INADDR_ANY;
		//strncpy(stb_ip, inet_ntoa(stb_addr.sin_addr), sizeof(stb_ip));
		if(-1==ifconfig_get("eth0", stb_ip, NULL, NULL)){
			DEBUG("get stb ip failed, use default instead\n");
			strcpy(stb_ip, "192.168.10.109");
		}
		DEBUG("get stb ip: %s\n", stb_ip);
		
		g_smart_power_cmds[index_p].id = 0;
		g_smart_power_cmds[index_p].type = CMD_HEADER_REPORTED;
		memset(g_smart_power_cmds[index_p].serv_str, 0, sizeof(g_smart_power_cmds[index_p].serv_str));
		memset(g_smart_power_cmds[index_p].entity, 0, sizeof(g_smart_power_cmds[index_p].entity));
		strncpy(g_smart_power_cmds[index_p].serv_str, "reported", sizeof(g_smart_power_cmds[index_p].serv_str));
		snprintf(g_smart_power_cmds[index_p].entity, sizeof(g_smart_power_cmds[index_p].entity), "#0000000000#04#0300#07#%s#", stb_ip);
		g_smart_power_cmds[index_p].status = CMD_STATUS_PROCESSED;
		smart_power_cmds_close(index_p, 1);
	}
	return index_p;
}

// caution: I am not make a integer timer for heartbeat.
int heartbeat_timer_callback(struct timeval *tv_datum, int arg1, int arg2)
{
	DEBUG("tv_sec=%ld, tv_usec=%ld\n", tv_datum->tv_sec, tv_datum->tv_usec);
	tv_datum->tv_sec += 90;
	tv_datum->tv_usec += 77000;

	smart_power_heartbeat_cmd_insert();
	continue_myself(g_fifo_fd);
	
	return 0;
}

static int continue_myself(int g_fifo_fd)
{
	char fifo_str[FIFO_STR_SIZE];
	memset(fifo_str, 0, FIFO_STR_SIZE);
	strncpy(fifo_str, MSGSTR_SOCKET_SELF, FIFO_STR_SIZE-1);
	if(-1==write(g_fifo_fd, fifo_str, strlen(fifo_str))){
		ERROROUT("write to fifo failed\n");
		return -1;
	}
	return 0;
}

int fifo_buf_clear(int fifo_fd, fd_set rdfds)
{
	if(fifo_fd<=0){
		DEBUG("this fd(%d) is invalid\n", fifo_fd);
		return -1;
	}

	char fifo_str[FIFO_STR_SIZE];
	if(FD_ISSET(g_fifo_fd, &rdfds)){
		memset(fifo_str, 0, FIFO_STR_SIZE);
		while(read(g_fifo_fd, fifo_str, sizeof(fifo_str))>0){
			DEBUG("read from fifo_2_socket: %s\n", fifo_str);
		}
		return 0;
	}
	else{
		DEBUG("this fd(%d) can not be readed\n", fifo_fd);
		return -1;
	}
}

void socket_mainloop()
{									//send number
	int l_return = -1;											//return of function
	int l_socket_fd = -1;										//socket descriptor
	char l_recv_buf[MAXLEN],l_send_buf[MAXLEN];				//buf of recv/send
	struct sockaddr_in server_addr;
	int delay_sec = 0;
	int cmd_header = CMD_HEADER_UNDEFINED;

	char serial_num[33];											//serial num, actual len is 32B
	char software_version[33];
	char server_ip[16];
	int server_port = 8080;
	int index_r = -1;
#ifdef HEARTBEAT_SUPPORT
	struct timeval tv_heartbeat = {7, 177000};
	// shit!!! Must use variable of callback function. If use callback function directly, it lost its pointer.
	int (*heartbeat_callbackfun)(struct timeval *, int arg1, int arg2) = heartbeat_timer_callback;
#endif

	memset(serial_num, 0, sizeof(serial_num));
	memset(software_version, 0, sizeof(software_version));
	memset(server_ip, 0, sizeof(server_ip));
	serialNum_get(serial_num, sizeof(serial_num));
	softwareVersion_get(software_version, sizeof(software_version));
	smartpower_server_ip_get(server_ip, sizeof(server_ip));
	server_port = smartpower_server_port_get();
	DEBUG("get serial num: %s, software_version: %s, server ip: %s, server port: %d\n", serial_num, software_version, server_ip, server_port);
	
	//argument of function select
	fd_set rdfds;									//write/read descriptor

#ifdef WORK_NORMAL_NOT_TEST
	g_socket_status = SOCKET_STATUS_CLOSED;
#else
	g_socket_status = SOCKET_STATUS_REGISTED;
#endif
	char fifo_str[FIFO_STR_SIZE];
	g_fifo_fd = open(FIFO_2_SOCKET, O_RDWR|O_NONBLOCK, 0);
	if(g_fifo_fd<0){
		ERROROUT("open fifo_2_socket failed\n");
		return;
	}
	else
		DEBUG("open FIFO_2_SOCKET with fd %d\n", g_fifo_fd);
	int fifoout_fd = open(FIFO_2_INSTRUCTION, O_RDWR|O_NONBLOCK, 0);
	if(fifoout_fd<0){
		ERROROUT("open fifo_2_instruction failed\n");
		return;
	}
	else
		DEBUG("open FIFO_2_INSTRUCTION with fd %d\n", fifoout_fd);
	
	struct timeval tv_select = {4, 500000};
	int ret_select = -1;
	int max_fd = g_fifo_fd;

	continue_myself(g_fifo_fd);
	
	while(1)
	{
		FD_ZERO(&rdfds);
		FD_SET(g_fifo_fd, &rdfds);
		if(l_socket_fd>0){
			FD_SET(l_socket_fd, &rdfds);
			if(l_socket_fd>max_fd)
				max_fd = l_socket_fd;
		}
		tv_select.tv_sec = 47;
		tv_select.tv_usec = 500000;
		ret_select = select(max_fd+1, &rdfds, NULL, NULL, &tv_select);
		if(ret_select<0){
			ERROROUT("select failed\n");
			return;
		}
		else if(0==ret_select){
			DEBUG("timeout\n");
			continue;
		}
		else{
			;
		}
		
		switch(g_socket_status){
			case SOCKET_STATUS_EXCEPTION:
				fifo_buf_clear(g_fifo_fd, rdfds);
				timer_unregist(&g_heartbeat_timer_id);
				g_heartbeat_timer_id = -1;
				if(l_socket_fd>2){
					close(l_socket_fd);
					l_socket_fd = -1;
				}
				g_socket_status = SOCKET_STATUS_CLOSED;
				DEBUG("has exception, socket is closed, retry connecting after 5mins\n");
				continue_myself(g_fifo_fd);
				sleep(60*5);
				break;
			case SOCKET_STATUS_CLOSED:				
				fifo_buf_clear(g_fifo_fd, rdfds);
				timer_unregist(&g_heartbeat_timer_id);
				g_heartbeat_timer_id = -1;
				if (-1 == (l_socket_fd=socket(AF_INET,SOCK_STREAM,0)))
				{
					ERROROUT("create socket failed, exit\n");
					exit(-1);
				}
				//monitor tcp link
				setKeepAlive(l_socket_fd);
				DEBUG("create socket(%d) success\n", l_socket_fd);
				g_socket_status = SOCKET_STATUS_CREATE;
				continue_myself(g_fifo_fd);
				break;
			case SOCKET_STATUS_CREATE:
			case SOCKET_STATUS_DISCONNECT:				
				fifo_buf_clear(g_fifo_fd, rdfds);
				//set argumnet of sockaddr_in
				server_addr.sin_family=AF_INET;							//address family IPV4
				server_addr.sin_port=htons(server_port);				//port number
				//use inet_addr
				server_addr.sin_addr.s_addr=inet_addr(server_ip);		//IPv4 address
			//	//use inet_aton
			//	if (0 == inet_aton(g_serverip,&server_addr.sin_addr))	//IPv4 address
			//		ERROROUT("inet_aton error");
			//	//use inet_pton
			//	if (-1 != inet_pton(AF_INET,g_serverip,&server_addr.sin_addr))	//IPv4 address
			//		ERROROUT("inet_pton error");
				bzero(&(server_addr.sin_zero),8);						//filler, all 0

				if(-1==connectRetry(l_socket_fd,server_addr)){
					DEBUG("connect %s:%d failed\n", server_ip, server_port);
					g_socket_status = SOCKET_STATUS_DISCONNECT;
					return;
				}
				else{
					DEBUG("connect %s:%d success\n", server_ip, server_port);
					g_socket_status = SOCKET_STATUS_CONNECTED;;
				}
				continue_myself(g_fifo_fd);
				break;
			case SOCKET_STATUS_CONNECTED:
				fifo_buf_clear(g_fifo_fd, rdfds);
				//send registration information
				memset(l_send_buf, 0, sizeof(l_send_buf));
#if 1
				snprintf(l_send_buf, sizeof(l_send_buf), "%s%s#%s#", PREFIX_REGIST, serial_num, software_version);
#else
				strncpy(l_send_buf, "GET /index.html HTTP/1.1\r\nHost: 61.135.169.105\r\nConnection: keep-alive\r\n\
User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.835.186 Safari/535.1\r\n\
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\r\n\
Accept-Encoding: gzip,deflate,sdch\r\n\
Accept-Language: zh-CN,zh;q=0.8\r\n\
Accept-Charset: GBK,utf-8;q=0.7,*;q=0.3\r\n\r\n", sizeof(l_send_buf));
#endif
				
				DEBUG("sending regist string: %s\n",l_send_buf);
				if ( 0 == sendToServer(l_socket_fd,l_send_buf,strlen(l_send_buf))){
					DEBUG("send regist string success\n");
					g_socket_status = SOCKET_STATUS_REGISTING;
					// wait for socket recv in selecting, so no need cycle_myself
				}
				else{
					ERROROUT("send registration to server failed!\n");
					delay_sec = 30 + randint(60.0);
					DEBUG("will regist again after %d seconds...\n", delay_sec);
					continue_myself(g_fifo_fd);
					sleep(delay_sec);
				}
				break;
			case SOCKET_STATUS_REGISTING:
				if(FD_ISSET(l_socket_fd, &rdfds)){
					memset(l_recv_buf, 0, sizeof(l_recv_buf));
					l_return=recvFromServer(l_socket_fd,l_recv_buf, sizeof(l_recv_buf));
					if(0==l_return){
						DEBUG("recv from server for regist: %s\n", l_recv_buf);
						cmd_header = smart_power_cmd_parse(l_recv_buf, strlen(l_recv_buf));
						if(CMD_HEADER_REGIST==cmd_header){
							DEBUG("regist to server success\n");
							g_socket_status = SOCKET_STATUS_REGISTED;
#ifdef HEARTBEAT_SUPPORT
							g_heartbeat_timer_id = timer_regist(&tv_heartbeat, TIMER_TYPE_MANUAL,0, 0,heartbeat_callbackfun);
#endif
						}
						else if(CMD_HEADER_REBOOT==cmd_header){
							DEBUG("smart power center will reboot after 30s\n");
							g_socket_status = SOCKET_STATUS_EXCEPTION;
							continue_myself(g_fifo_fd);
							sleep(30);
						}
						else{
							DEBUG("regist faild, will exit\n");
							g_socket_status = SOCKET_STATUS_UNREGIST;
							exit(0);
						}
					}
					else if(1==l_return){
						DEBUG("recv from server timeout, sleep 3s and try again\n");
						// wait for socket recv in select; so no need cycle_myself
						sleep(3);
					}
					else if(-1==l_return){
						ERROROUT("error happened when recving, will reboot after 10s\n");
						g_socket_status = SOCKET_STATUS_EXCEPTION;
						continue_myself(g_fifo_fd);
						sleep(10);
					}
				}
				//else{
				//	DEBUG("not the socket fd can be readed\n");
				//	fifo_buf_clear(g_fifo_fd, rdfds);
				//}
				break;
			case SOCKET_STATUS_REGISTED:
#ifdef WORK_NORMAL_NOT_TEST
				if(FD_ISSET(l_socket_fd, &rdfds)){
					memset(l_recv_buf, 0, sizeof(l_recv_buf));
					l_return=recvFromServer(l_socket_fd,l_recv_buf, sizeof(l_recv_buf));
#else
				if(test_for_one==0){
					//strcpy(l_recv_buf, "#tt#test#89277089728430810813#0103010000#01#0101#00#");
					strcpy(l_recv_buf, test_cmdstr_get());
					
					test_for_one = 1;
					l_return = 0;
#ifdef HEARTBEAT_SUPPORT
					DEBUG("regist to timer tv_sec=%ld, callbackfun=%p\n", tv_heartbeat.tv_sec, heartbeat_callbackfun);
					// this heartbeat registing is only for testing
					g_heartbeat_timer_id = timer_regist(&tv_heartbeat, TIMER_TYPE_MANUAL,0,0,heartbeat_callbackfun);
					DEBUG("g_heartbeat_timer_id=%d\n", g_heartbeat_timer_id);
#endif
#endif
					if(0==l_return){
						DEBUG("recv from server: %s\n", l_recv_buf);
						cmd_header = smart_power_cmd_parse(l_recv_buf, strlen(l_recv_buf));
						if(CMD_HEADER_UNDEFINED!=cmd_header){
							memset(fifo_str, 0, FIFO_STR_SIZE);
							strncpy(fifo_str, MSGSTR_2_INSTRUCTION, FIFO_STR_SIZE-1);
							if(-1==write(fifoout_fd, fifo_str, strlen(fifo_str))){
								ERROROUT("write to fifoout failed\n");
							}
							else
								DEBUG("send fifo str to instruction module success\n");
						}
					}
					else if(-1==l_return){
						// 如果socket提示可读，但是recv的结果为-1，则说明是对方关闭了socket，己方应当close掉socket重新开始
						DEBUG("socket can read, but read return -1. perhaps server is close\n");
						
						fifo_buf_clear(g_fifo_fd, rdfds);
						timer_unregist(&g_heartbeat_timer_id);
						g_heartbeat_timer_id = -1;
						if(l_socket_fd>2){
							close(l_socket_fd);
							l_socket_fd = -1;
						}
						g_socket_status = SOCKET_STATUS_CLOSED;
						sleep(1);
						continue_myself(g_fifo_fd);
					}
				}
				else if(FD_ISSET(g_fifo_fd, &rdfds)){
					memset(fifo_str, 0, FIFO_STR_SIZE);
					if(read(g_fifo_fd, fifo_str, sizeof(fifo_str))<0){
						ERROROUT("read from fifo_2_socket failed\n");
					}
					else
						DEBUG("read from fifo_2_socket: %s\n", fifo_str);
					
					index_r = smart_power_cmds_open(CMD_ARRAY_OP_R, BOOL_FALSE);
					if(-1!=index_r){
						memset(l_send_buf, 0, sizeof(l_send_buf));
						l_return = smart_power_cmd_splice(index_r, l_send_buf, sizeof(l_send_buf));
						if(0==l_return){
							DEBUG("will send to server: %s\n", l_send_buf);
							char sqlite_cmd[128];
							memset(sqlite_cmd, 0, sizeof(sqlite_cmd));
							if(0==sendToServer(l_socket_fd, l_send_buf, strlen(l_send_buf))){
								smart_power_cmds_close(index_r, 1);
								/*如果主动上报成功，则将actpower或power表相应记录删除，避免下次重复上报*/
								if(CMD_ACTIVE_REPORTED_ACTPOWER==g_smart_power_cmds[index_r].type){
									snprintf(sqlite_cmd, sizeof(sqlite_cmd), "DELETE FROM actpower WHERE status=1;");
									sqlite_execute(sqlite_cmd);
								}
								else if(CMD_ACTIVE_REPORTED_POWER==g_smart_power_cmds[index_r].type){
									snprintf(sqlite_cmd, sizeof(sqlite_cmd), "DELETE FROM power WHERE status=1;");
									sqlite_execute(sqlite_cmd);
								}
							}
							else{
#ifdef WORK_NORMAL_NOT_TEST
								if(g_smart_power_cmds[index_r].send_try<3){
									smart_power_cmds_close(index_r, 0);
									DEBUG("will retry to sendto server again, send_try=%d\n", g_smart_power_cmds[index_r].send_try);
									g_smart_power_cmds[index_r].send_try ++;
									continue_myself(g_fifo_fd);	// try to send again
								}
								else{
									smart_power_cmds_close(index_r, -1);
									/*如果主动上报失败，则对actpower或power表相应记录重新打上标记“0”，允许再次上报*/
									if(CMD_ACTIVE_REPORTED_ACTPOWER==g_smart_power_cmds[index_r].type){
										snprintf(sqlite_cmd, sizeof(sqlite_cmd), "UPDATE actpower SET status=0 WHERE status=1;");
										sqlite_execute(sqlite_cmd);
									}
									else if(CMD_ACTIVE_REPORTED_POWER==g_smart_power_cmds[index_r].type){
										snprintf(sqlite_cmd, sizeof(sqlite_cmd), "UPDATE power SET status=0 WHERE status=1;");
										sqlite_execute(sqlite_cmd);
									}
								}
#else
								smart_power_cmds_close(index_r, -1);
#endif
								DEBUG("send to server failed\n");
							}
						}
					}
					else
						DEBUG("smart_power_cmds_open(CMD_ARRAY_OP_R, BOOL_FALSE) return with -1\n");
				}
				break;
			case SOCKET_STATUS_UNREGIST:
				fifo_buf_clear(g_fifo_fd, rdfds);
				DEBUG("at the swith of SOCKET_STATUS_UNREGIST\n");
				g_socket_status = SOCKET_STATUS_CONNECTED;
				continue_myself(g_fifo_fd);
				break;
			default:
				fifo_buf_clear(g_fifo_fd, rdfds);
				DEBUG("this status(%d) of socket can not be dealed with\n", g_socket_status);
				continue_myself(g_fifo_fd);
				sleep(60*3);
				break;
		}
	}
	
	close(l_socket_fd);
	l_socket_fd = -1;

	return;
}

/***sendToServer() biref send information to server
 * 2011.11.4, liyang
 * param l_socket_fd[in], socket descriptor
 * param l_wrfds[in], write descriptor
 * param l_send_buf[in][out], buf of send
 *
 * retval, 0 if successful or -1 failed
 * Version 1.0
 ***/
static int sendToServer(int l_socket_fd,char *l_send_buf, unsigned int buf_len)
{
	struct timeval s_time={0,0};
	int ret_select = -1;
	int ret = -1;
	fd_set l_wrfds = {};

	if(l_socket_fd<3 || NULL==l_send_buf || 0==buf_len){
		DEBUG("can not send to server, socket: %d\n", l_socket_fd);
		return ret;
	}
	
	FD_CLR(l_socket_fd,&l_wrfds);
	FD_ZERO(&l_wrfds);
	FD_SET(l_socket_fd,&l_wrfds);
	ret_select = select(l_socket_fd+1,NULL,&l_wrfds,NULL,&s_time);
	if ( ret_select<0)
	{
		DEBUG("select error\n");
		ret = -1;
	}
	else if( 0==ret_select )
	{
		DEBUG("select timeout\n");
		ret = -1;
	}
	else
	{
		if (FD_ISSET(l_socket_fd,&l_wrfds))
		{
			if ( -1 == (write(l_socket_fd,l_send_buf,strlen(l_send_buf))) )
			{
				DEBUG("write to socket failed\n");
				ret = -1;
			}
			else
			{
				DEBUG("write to socket success\n");
				ret = 0;
			}
		}
	}
	
	FD_CLR(l_socket_fd,&l_wrfds);
	return ret;
}

/***recvFromServer() biref recv information from server
 * 2011.11.4, liyang
 * param l_socket_fd[in], socket descriptor
 * param l_wrfds[in], write descriptor
 * param l_recv_buf[in][out], buf of recv
 *
 * retval, 0 if successful. -1 failed or 1 reconnect
 * Version 1.0
 ***/
static int recvFromServer(int l_socket_fd,char *l_recv_buf, unsigned int recv_buf_len)
{
	int ret_select = -1;					//select return
	int ret = -1;
	int ret_recv = -1;
	fd_set l_rdfds = {};
	
	if(l_socket_fd<3 || NULL==l_recv_buf || 0==recv_buf_len){
		DEBUG("can not send to server, socket: %d\n", l_socket_fd);
		return ret;
	}
	
	struct timeval s_time={0,500000};			/* perhaps this 500ms is too short */
	FD_CLR(l_socket_fd,&l_rdfds);
	FD_ZERO(&l_rdfds);
	FD_SET(l_socket_fd,&l_rdfds);
	ret_select=select(l_socket_fd+1,&l_rdfds,NULL,NULL,&s_time);
	if ( ret_select<0)
	{
		DEBUG("select error\n");
		ret = -1;
	}
	else if( 0==ret_select )
	{
		DEBUG("select timeout\n");
		ret = 1;
	}
	else
	{
		if (FD_ISSET(l_socket_fd,&l_rdfds))
		{
			DEBUG("socket(%d) can be readed\n", l_socket_fd);
			
			ret_recv=recv(l_socket_fd,l_recv_buf,recv_buf_len,0);
			//monitor tcp link,-1 out line . next time select will return 1 and recv return 0
			if (-1 == ret_recv)
			{
				DEBUG("out line!!!\n");
				ret = -1;
			}
			//server is out
			else if (0 == ret_recv)
			{
				DEBUG("server is out line!!!\n");
				ret = -1;
			}
			else{
				DEBUG("recv successfully: %s\n", l_recv_buf);
				ret = 0;
			}
		}
		else
		{
			DEBUG("another socket but not %d can be readed\n", l_socket_fd);
			ret = -1;
		}
	}
	
	FD_CLR(l_socket_fd,&l_rdfds);
	return ret;
}

/***getMacAddr() biref get local device's Mac address
 * 2011.11.4, liyang
 * param l_socket_fd[in], socket descriptor
 * param l_l_mac_addr[in][out], buf of MacAddr
 *
 * retval, 0 if successful or -1 failed
 * Version 1.0
 ***/
int getMacAddr(int l_socket_fd,char* l_mac_addr)
{
	struct ifreq ifr_mac;
	memset(&ifr_mac,0,sizeof(ifr_mac));
	strncpy(ifr_mac.ifr_name, "eth0", sizeof(ifr_mac.ifr_name)-1);
	if( (ioctl( l_socket_fd, SIOCGIFHWADDR, &ifr_mac)) < 0)
	{
		printf("mac ioctl error\n");
		return -1;
	}
	sprintf(l_mac_addr,"%02x%02x%02x%02x%02x%02x",
			(unsigned char)ifr_mac.ifr_hwaddr.sa_data[0],
			(unsigned char)ifr_mac.ifr_hwaddr.sa_data[1],
			(unsigned char)ifr_mac.ifr_hwaddr.sa_data[2],
			(unsigned char)ifr_mac.ifr_hwaddr.sa_data[3],
			(unsigned char)ifr_mac.ifr_hwaddr.sa_data[4],
			(unsigned char)ifr_mac.ifr_hwaddr.sa_data[5]);

	return 0;
}

/***connectRetry() biref reconnect
 * 2011.11.5, liyang
 * param l_socket_fd[in], socket descriptor
 * param server_addr[in], argumnet of socketaddr_in
 *
 * retval void
 * Version 1.0
 ***/
static int connectRetry(int l_socket_fd,struct sockaddr_in server_addr)
{
	int l_sec=1;				//sleep time

	if(l_socket_fd<3)
		return -1;
	
#if 0
	//close socket
	close(l_socket_fd);

	//reopen socket
	if (-1 == (l_socket_fd=socket(AF_INET,SOCK_STREAM,0)))
	{
		ERROROUT("socket error");
	}
	setKeepAlive(l_socket_fd);
#endif

	DEBUG("connecting to server...\n");
	while(1)
	{
		//connect
		if ( -1 == connect(l_socket_fd,(struct sockaddr*)&server_addr,sizeof(struct sockaddr)) )
		{
			if (l_sec < MAXSLEEP)
			{
				DEBUG("connect error,retry after %d sec......\n", l_sec);
				sleep(l_sec);
				l_sec<<=1;
			}
			else	//if l_sec<MAXSLEEP,set l_sec=1
			{
#if 0
				printf("connect error,retry after %dsec......\n",l_sec);
				sleep(l_sec);
				l_sec=1;
#else
				return -1;
#endif
			}
		}
		else
			break;
	}
	return 0;
}

/***setKeepAlive() biref monitor tcp link
 * 2011.11.7, liyang
 * param l_socket_fd[in], socket descriptor
 *
 * retval void
 * Version 1.0
 ***/
void setKeepAlive(int l_socket_fd)
{
	if(l_socket_fd<3)
		return;
	
	socklen_t l_keepalive=1;		//set keepalive
	socklen_t l_keepidle=5;			//the beginning of the first keepalive detection before the TCP air closing time
	socklen_t l_keepinterval=5;		//two keepalive detection interval
	socklen_t l_keepcount=3;		//determination of disconnected before the keepalive detection times

	setsockopt( l_socket_fd , SOL_SOCKET , SO_KEEPALIVE , (const char*)&l_keepalive , sizeof(l_keepalive) );
	setsockopt( l_socket_fd , SOL_TCP , TCP_KEEPIDLE , (const char*)&l_keepidle , sizeof(l_keepidle) );
	setsockopt( l_socket_fd , SOL_TCP , TCP_KEEPINTVL , (const char*)&l_keepinterval , sizeof(l_keepinterval) );
	setsockopt( l_socket_fd , SOL_TCP , TCP_KEEPCNT , (const char*)&l_keepcount , sizeof(l_keepcount) );
}


