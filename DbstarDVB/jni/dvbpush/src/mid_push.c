/*
* example.cpp
*
*  Created on: Aug 11, 2011
*      Author: YJQ
*/

#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/types.h>
#include <pthread.h>
#include <sys/ioctl.h>
#include <errno.h>
#include <sys/socket.h>
#include <net/if.h>
#include <string.h>
#include <arpa/inet.h>
#include <netpacket/packet.h>
#include <linux/if_ether.h>
#include <net/if_arp.h>
#include <stdlib.h>
#include <time.h>

#include "common.h"
#include "push.h"
#include "mid_push.h"
#include "porting.h"
#include "xmlparser.h"
#include "sqlite.h"
#include "dvbpush_api.h"
#include "multicast.h"

#define MAX_PACK_LEN (1500)
#define MAX_PACK_BUF (60000)		//定义缓冲区大小，单位：包	1500*200000=280M
//#define MEMSET_PUSHBUF_SAFE			// if MAX_PACK_BUF<200000 define

/*
 只有在进入“下载状态”页面才查询进度，不能用在当前push库中，因为需要通过监控下载进度获取节目下载完毕。
*/
//#define MONITOR_MIN

#define XML_NUM			8
static PUSH_XML_S		s_push_xml[XML_NUM];

static pthread_mutex_t mtx_xml = PTHREAD_MUTEX_INITIALIZER;
static pthread_cond_t cond_xml = PTHREAD_COND_INITIALIZER;
static pthread_mutex_t mtx_push_monitor = PTHREAD_MUTEX_INITIALIZER;
static pthread_cond_t cond_push_monitor = PTHREAD_COND_INITIALIZER;


static pthread_mutex_t mtx_push_rely_condition = PTHREAD_MUTEX_INITIALIZER;
static pthread_cond_t cond_push_rely_condition = PTHREAD_COND_INITIALIZER;
static int s_push_rely_condition = 0;
static int push_idle = 0;
static int s_disk_manage_flag = 0;

//数据包结构
typedef struct tagDataBuffer
{
    short	m_len;
    unsigned char	m_buf[MAX_PACK_LEN];
}DataBuffer;

typedef struct tagPRG
{
	char			id[32];
	char			uri[256];
	char			descURI[384];
	char			caption[256];
	char			deadline[32];
	RECEIVETYPE_E	type;
	long long		cur;
	long long		total;
	int				parsed;
}PROG_S;

static int mid_push_regist(PROG_S *prog);
static int mid_push_unregist(PROG_S *prog);
static int push_decoder_buf_uninit();
static int prog_name_fill();

#define PROGS_NUM 64
static PROG_S s_prgs[PROGS_NUM];
//static char s_push_data_dir[256];
/*************接收缓冲区定义***********/
DataBuffer *g_recvBuffer = NULL;	//[MAX_PACK_BUF]
static int g_wIndex = 0;
//static int g_rIndex = 0;
/**************************************/

static pthread_t tidDecodeData;
static int s_xmlparse_running = 0;
static int s_monitor_running = 0;
static int s_decoder_running = 0;
static int s_push_monitor_active = 0;
static int s_dvbpush_getinfo_flag = 0;

static int s_column_refresh = 0;
static int s_interface_refresh = 0;


/*
当向push中写数据时才有必要监听进度，否则直接使用数据库中记录的进度即可。
考虑到缓冲，在无数据后多查询几轮再停止查询，因此push数据时，置此值为3。
考虑到开机最好给一次显示的机会，初始化为1。
*/
static int s_push_has_data = 3;

int send_mpe_sec_to_push_fifo(uint8_t *pkt, int pkt_len)
{
	int res = -1;
	int snap = 0;
	int offset;
	unsigned char *eth;
	DataBuffer *revbuf;
	int revbufw;
	
	/*	static unsigned int rx_errors = 0;
	static unsigned int rx_length_errors = 0;
	static unsigned int rx_crc_errors = 0;
	static unsigned int rx_dropped = 0;
	static unsigned int rx_frame_errors = 0;
	static unsigned int rx_fifo_dropped = 0;
	*/
	
//	PRINTF("g_recvBuffer=%p\n", g_recvBuffer);
	//return 0;
	
	if (pkt_len < 16) {
		PRINTF("IP/MPE packet length = %d too small.\n", pkt_len);
		//		rx_errors++;
		//		rx_length_errors++;
		return res;
	}
	
	if (pkt[5] & 0x3e)
	{
		printf("lxy add for youhua,too many!!!!!!!!\n");
		if ((pkt[5] & 0x3c) != 0x00) {
			/* drop scrambled */
			//	rx_errors++;
			//	rx_crc_errors++;
			return res;
		}
		if (pkt[5] & 0x02) {
			if (pkt_len < 24 || memcmp(&pkt[12], "\xaa\xaa\x03\0\0\0", 6)) {
				//	rx_dropped++;
				return res;
			}
			snap = 8;
		}
	}
	/*	if (pkt[7]) {
	//		rx_errors++;
	//		rx_frame_errors++;
	return res;
	}*/
	offset = pkt_len - 16 - snap; 
	//if (pkt_len - 12 - 4 + 14 - snap <= 0) {
	if (offset + 14 <= 0) {
		printf("IP/MPE packet length = %d too small.\n", pkt_len);
		//rx_errors++;
		//rx_length_errors++;
		return res;
	}
	
//	if(NULL==g_recvBuffer){
//		DEBUG("g_recvBuffer is NULL\n");
//		return res;
//	}
	
	/*	if (g_wIndex == g_rIndex 
	&& g_recvBuffer[g_wIndex].m_len) {
	rx_fifo_dropped++;
	printf("Push FIFO is full. lost pkt %d\n", rx_fifo_dropped);
	return res;
	}*/
	revbufw = g_wIndex;
	revbuf = &g_recvBuffer[revbufw];
	//eth = g_recvBuffer[g_wIndex].m_buf;
	eth = revbuf->m_buf;
	
	memcpy(eth + 14, pkt + 12 + snap, offset);//pkt_len - 12 - 4 - snap);
	eth[0]=pkt[0x0b];
	eth[1]=pkt[0x0a];
	eth[2]=pkt[0x09];
	eth[3]=pkt[0x08];
	eth[4]=pkt[0x04];
	eth[5]=pkt[0x03];
	
	eth[6]=eth[7]=eth[8]=eth[9]=eth[10]=eth[11]=0;
	
	if (snap) {
		eth[12] = pkt[18];
		eth[13] = pkt[19];
	} else {
		if (pkt[12] >> 4 == 6) {
			eth[12] = 0x86;	
			eth[13] = 0xdd;
		} else {
			eth[12] = 0x08;	
			eth[13] = 0x00;
		}
	}
	
	
	//g_recvBuffer[g_wIndex].m_len = offset;//pkt_len - 12 - 4 - snap;
	revbuf->m_len = offset;
	revbufw++;
	if (revbufw < MAX_PACK_BUF)
		g_wIndex = revbufw;
	else
		g_wIndex = 0;
	
	//DEBUG("g_wIndex=%d\n", g_wIndex);
	//g_wIndex++;
	//g_wIndex %= MAX_PACK_BUF;
	
	return 0;
}

void *push_decoder_thread()
{
	unsigned char *pBuf = NULL;
	int rindex = 0;
    short len;
    int push_loop_idle_cnt = 0;
	
	DEBUG("push decoder thread will goto main loop\n");
	s_decoder_running = 1;
rewake:	
	DEBUG("go to push main loop\n");
	while (1==s_decoder_running && NULL!=g_recvBuffer)
	{
		len = g_recvBuffer[rindex].m_len;
		if (len)
		{
			pBuf = g_recvBuffer[rindex].m_buf;
			/*
			* 调用PUSH数据解析接口解析数据，该函数是阻塞的，所以应该使用一个较大
			* 的缓冲区来暂时存储源源不断的数据。
			*/
			push_parse((char *)pBuf, len);
			s_push_has_data = 3;
			
			g_recvBuffer[rindex].m_len = 0;
			rindex++;
			if(rindex >= MAX_PACK_BUF)
				rindex = 0;
			//g_rIndex = rindex;
		}
		else
		{
			usleep(20000);
			push_loop_idle_cnt++;
			if(push_loop_idle_cnt > 1024)
			{
				DEBUG("read nothing, read index %d\n", rindex);
				push_loop_idle_cnt = 0;
			}
		}
	}
	
	if (s_decoder_running == 2)
	{
		push_idle = 1;
		while (s_decoder_running == 2)
		{
			DEBUG("push thread in idle\n");
			sleep(15);
		}
#ifdef MEMSET_PUSHBUF_SAFE
		memset(g_recvBuffer,0 ,sizeof(DataBuffer)*MAX_PACK_BUF);
		DEBUG("g_recvBuffer=%p\n", g_recvBuffer);
#else
		g_recvBuffer[0].m_len = 0;
		g_recvBuffer[1].m_len = 0;
#endif
		g_wIndex = 0;
		rindex = 0;
		push_idle = 0;
		goto rewake;
	}
	DEBUG("exit from push decoder thread\n");
	
	return NULL;
}

void push_rely_condition_set(int cmd)
{
	pthread_mutex_lock(&mtx_push_rely_condition);
	int tmp_cond = s_push_rely_condition;
	if(CMD_NETWORK_DISCONNECT==cmd){
		s_push_rely_condition = s_push_rely_condition & (~RELY_CONDITION_NET);
	}
	else if(CMD_NETWORK_CONNECT==cmd){
		s_push_rely_condition = s_push_rely_condition | RELY_CONDITION_NET;
	}
	else if(CMD_DISK_UNMOUNT==cmd){
		s_push_rely_condition = s_push_rely_condition & (~RELY_CONDITION_HD);
	}
	else if(CMD_DISK_MOUNT==cmd){
		s_push_rely_condition = s_push_rely_condition | RELY_CONDITION_HD;
	}
	else{
		DEBUG("this cmd 0x%x is ignored\n", cmd);
		pthread_mutex_unlock(&mtx_push_rely_condition);
	}
	DEBUG("push origine %d, cmd 0x%x, so s_push_rely_condition is %d\n", tmp_cond, cmd, s_push_rely_condition);
	pthread_cond_signal(&cond_push_rely_condition);
	pthread_mutex_unlock(&mtx_push_rely_condition);
}

//#define PARSE_XML_VIA_THREAD
static int push_prog_finish(char *id, RECEIVETYPE_E type,char *DescURI)
{
	if(NULL==DescURI || NULL==DescURI){
		DEBUG("invalid args\n");
		return -1;
	}
	
	DEBUG("should parse: %s\n", DescURI);
	if(RECEIVETYPE_PUBLICATION==type){
#ifdef PARSE_XML_VIA_THREAD
		send_xml_to_parse(DescURI,PRODUCTION_XML,id);
#else
/*
直接调用parse_xml的目的是避开线程，否则出现parse_xml和push_recv_manage_refresh同时使用sqlite库，导致异常
但是否会死锁？
靠。但是直接调用parse_xml会导致其中的事务交叉引起sqlite错误：out of memory
咋办呢？？？目前只能在sqlite的事务调用处搞一个互斥保护。
*/
		parse_xml(DescURI,PRODUCTION_XML,id);
#endif
	}
	else if(RECEIVETYPE_COLUMN==type){
#ifdef PARSE_XML_VIA_THREAD
		send_xml_to_parse(DescURI,COLUMN_XML,NULL);
#else
		parse_xml(DescURI,COLUMN_XML,NULL);
#endif
	}
	else if(RECEIVETYPE_SPRODUCT==type){
#ifdef PARSE_XML_VIA_THREAD
		send_xml_to_parse(DescURI,SPRODUCT_XML,NULL);
#else
		parse_xml(DescURI,SPRODUCT_XML,NULL);
#endif
	}
	else
		DEBUG("this type can not be distinguish\n");
	
	return 0;
}

static int productdesc_parsed_set(char *xml_uri)
{
	char sqlite_cmd[512];
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "UPDATE ProductDesc SET Parsed='1' WHERE DescURI='%s';", xml_uri);
	return sqlite_execute(sqlite_cmd);
}


/*
 功能判断是否是合法节目
 返回值：	-1表示非法，1表示合法。
*/
static int prog_is_valid(PROG_S *prog)
{
	if(NULL==prog)
		return -1;
		
	if(strlen(prog->uri)>0 || (prog->total)>0LL){
		//DEBUG("valid prog\n");
		return 1;
	}
	else
		return -1;
}

void dvbpush_getinfo_start()
{
	s_dvbpush_getinfo_flag = 1;
	
	DEBUG("dvbpush getinfo start >>\n");
}

void dvbpush_getinfo_stop()
{
	DEBUG("dvbpush getinfo stop <<\n");
	s_dvbpush_getinfo_flag = 0;
}


/*
从目前测试的情况看，只有在监控线程调用prog_monitor时，接收完毕的节目才能直接调用xml解析。
如果是从JNI的dvbpush_getinfo接口调用过来，则不能解析xml，否则导致Launcher死掉。
*/
static int prog_monitor(PROG_S *prog, char *time_stamp)
{
	if(NULL==prog)
		return -1;
	
	if(NULL!=time_stamp && strcmp(time_stamp,prog->deadline)>0){
		DEBUG("this prog[%s:%s] is overdue, compare with %s\n", prog->id,prog->deadline,time_stamp);
		mid_push_unregist(prog);
		return 0;
	}
	
	if(s_push_has_data>0 && (prog->cur)<(prog->total)){
		/*
		* 获取指定节目的已接收字节大小，可算出百分比
		由于管理上的错误，有可能出现当前长度大于总长的情况，在数组中忠实记录rxb的值，只有在UI执行getinfo时再控制cur<=total
		*/
		long long rxb = push_dir_get_single(prog->uri);
		
		DEBUG("PROG_S[%s]:%s %s %lld/%lld %-3lld%%\n",
			prog->id,
			prog->uri,
			prog->descURI,
			rxb,
			prog->total,
			rxb*100/prog->total);
		
		prog->cur = rxb;
	}
	else
		DEBUG("s_push_has_data=%d, prog->cur=%lld, prog->total=%lld, no need to monitor\n", s_push_has_data,prog->cur,prog->total);

	return 0;
}


int dvbpush_getinfo(char *buf, unsigned int size)
{
	if(NULL==buf || 0==size){
		DEBUG("invalid args\n");
		return -1;
	}
	
	int i = 0;
	if(s_push_monitor_active>0){
		/*
		监测节目接收进度
		*/
		pthread_mutex_lock(&mtx_push_monitor);
		for(i=0; i<PROGS_NUM; i++)
		{
			if(-1==prog_is_valid(&s_prgs[i]))
				continue;
			
			prog_monitor(&s_prgs[i],NULL);
			
			if(RECEIVETYPE_PUBLICATION==s_prgs[i].type){
				if(0==i){
					snprintf(buf, size,
						"%s\t%s\t%lld\t%lld", s_prgs[i].id,s_prgs[i].caption,s_prgs[i].cur>s_prgs[i].total?s_prgs[i].total:s_prgs[i].cur,s_prgs[i].total);
				}
				else{
					snprintf(buf+strlen(buf), size-strlen(buf),
						"%s%s\t%s\t%lld\t%lld", "\n",s_prgs[i].id,s_prgs[i].caption,s_prgs[i].cur>s_prgs[i].total?s_prgs[i].total:s_prgs[i].cur,s_prgs[i].total);
				}
			}
		}
		s_push_has_data --;
		pthread_mutex_unlock(&mtx_push_monitor);
	
		DEBUG("%s\n", buf);
	}
	else
		DEBUG("no program in monitor\n");
	
	return 0;
}

#if 0
/*
 返回1表示过期，0表示时间相等，-1表示不过期，-2表示其他错误
*/
static int prog_overdue(char *my_time, char *deadline_time)
{
	if(NULL==my_time || NULL==deadline)
		return -2;
	
	struct tm my_tm;
	struct tm deadline_tm;	// short for deadline
	
	memset(my_tm, 0, sizeof(my_tm));
	memset(deadline_tm, 0, sizeof(deadline_tm));
	
	int ret = -2;
	if(		4!=sscanf(my_time, "%d-%d-%d %d:%d:%d", &my_tm.tm_year, &my_tm.tm_mon, &my_tm.tm_mday, &my_tm.tm_hour, &my_tm.tm_min, &my_tm.tm_sec)
		||	4!=sscanf(deadline_time, "%d-%d-%d %d:%d:%d", &deadline_tm.tm_year, &deadline_tm.tm_mon, &deadline_tm.tm_mday, &deadline_tm.tm_hour, &deadline_tm.tm_min, &deadline_tm.tm_sec))
	{
		DEBUG("sscanf for time str failed, my_time: %s, deadline_time: %s\n", my_time, deadline_time);
	}
	else{
		if(my_tm.tm_year>deadline_tm.tm_year)
			return 1;
		else if(my_tm.tm_year<deadline_tm.tm_year)
			return -1;
		else{
			if(my_tm.tm_mon>deadline.tm_mon)
				return 1;
			else if(my_tm.tm_mon<deadline.tm_mon)
				return -1;
			else{
				.....
			}
		}
	}
}
#endif

void disk_manage_flag_set(int flag)
{
	s_disk_manage_flag = flag;
	DEBUG("s_disk_manage_flag=%d\n\n\n", s_disk_manage_flag);
}

/*
 除非push有数据，并且监控的节目个数大于0，否则不需要monitor监控。
 return 1——需要监控
 return 0——不需要监控
*/
static int need_push_monitor()
{
	if(s_push_has_data>0 && s_push_monitor_active>0)
		return 1;
	else{
		DEBUG("s_push_has_data: %d, s_push_monitor_active: %d\n", s_push_has_data, s_push_monitor_active);
		return 0;
	}
}

void column_refresh_flag_set(int flag)
{
	s_column_refresh = flag;
}

void interface_refresh_flag_set(int flag)
{
	s_interface_refresh = flag;
}

/*
为避免无意义的查询硬盘，应完成下面两个工作：
1、当节目接收完毕后不应再查询，数据库中记录的是100%
2、只有UI上进入查看进度的界面后，通知底层去查询，其他时间查询没有意义。
3、当push无数据后，再轮询若干遍（等待缓冲数据写入硬盘）后就不再轮询。
*/
void *push_monitor_thread()
{
	s_monitor_running = 1;
	
	struct timeval now;
	struct timespec outtime;
	int retcode = 0;
	char sqlite_cmd[256];
	char time_stamp[32];
	int monitor_interval = 61;
	unsigned int loop_cnt = 0;
	int i = 0;
	
	while (1==s_monitor_running)
	{
		pthread_mutex_lock(&mtx_push_monitor);
		PRINTF("2 s_push_has_data:%d,s_push_monitor_active:%d\n",s_push_has_data,s_push_monitor_active);
		
		gettimeofday(&now, NULL);
		outtime.tv_sec = now.tv_sec + monitor_interval;
		outtime.tv_nsec = now.tv_usec;
		retcode = pthread_cond_timedwait(&cond_push_monitor, &mtx_push_monitor, &outtime);
		
		if(need_push_monitor()>0 && 0==s_dvbpush_getinfo_flag){
			memset(time_stamp, 0, sizeof(time_stamp));
			snprintf(sqlite_cmd,sizeof(sqlite_cmd),"select datetime('now','localtime');");
			if(-1==str_sqlite_read(time_stamp,sizeof(time_stamp),sqlite_cmd)){
				DEBUG("can not generate DATETIME for prog monitor\n");
				memset(time_stamp, 0, sizeof(time_stamp));
			}
			
			for(i=0; i<PROGS_NUM; i++)
			{
				if(-1==prog_is_valid(&s_prgs[i]) || s_prgs[i].cur>=s_prgs[i].total)
					continue;
				
				prog_monitor(&s_prgs[i],time_stamp);
			}
			s_push_has_data--;
		}
		
		for(i=0; i<PROGS_NUM; i++)
		{
			if(-1==prog_is_valid(&s_prgs[i]))
				continue;
			
			if(0==s_prgs[i].parsed && (s_prgs[i].cur) >= (s_prgs[i].total)){
				DEBUG("%s download finished, parse %s\n", s_prgs[i].uri,s_prgs[i].descURI);
				push_prog_finish(s_prgs[i].id,s_prgs[i].type,s_prgs[i].descURI);
				productdesc_parsed_set(s_prgs[i].descURI);
				s_prgs[i].parsed = 1;
			}
		}
		
		pthread_mutex_unlock(&mtx_push_monitor);
		
		if(ETIMEDOUT!=retcode){
			DEBUG("push monitor thread is awaked by external signal\n");
		}
		else{
//			if(need_push_monitor()>0)
//				push_recv_manage_refresh(2,time_stamp);
		}
		
		// 每隔12个小时，打开tdt pid进行时间同步，这里只是借用了monitor这个低频循环。
		loop_cnt ++;
		if(loop_cnt>(720)){
			time_t timep;
			struct tm *p_tm;
			timep = time(NULL);
			p_tm = localtime(&timep); /*获取本地时区时间*/ 
			DEBUG("it's time to awake tdt time sync, %4d-%2d-%2d %2d:%2d:%2d\n", (p_tm->tm_year+1900), (p_tm->tm_mon+1), p_tm->tm_mday, p_tm->tm_hour, p_tm->tm_min, p_tm->tm_sec);
			tdt_time_sync_awake();
			loop_cnt = 0;
		}
		
		// 当栏目和界面产品发生改变时，不能直接在parse_xml()函数中通过JNI向UI发送notify，否则很容易导致Launcher死掉。
		if(s_column_refresh>0){
			s_column_refresh ++;
			if(s_column_refresh>2){
				msg_send2_UI(STATUS_COLUMN_REFRESH, NULL, 0);
				s_column_refresh = 0;
			}
		}
		if(s_interface_refresh>0){
			s_interface_refresh ++;
			if(s_interface_refresh>2){
				msg_send2_UI(STATUS_COLUMN_REFRESH, NULL, 0);
				s_interface_refresh = 0;
			}
		}
		
		if(1==s_disk_manage_flag){
			DEBUG("will clean disk\n");
			disk_manage();
			s_disk_manage_flag = 0;
		}
	}
	DEBUG("exit from push monitor thread\n");
	
	return NULL;
}

void *push_xml_parse_thread()
{
	int i = 0;
	
	s_xmlparse_running = 1;
	while (1==s_xmlparse_running)
	{
		pthread_mutex_lock(&mtx_xml);
		pthread_cond_wait(&cond_xml,&mtx_xml); //wait
		DEBUG("awaked and parse xml\n");
		sleep(1);
		
		if(1==s_xmlparse_running){
			for(i=0; i<XML_NUM; i++){
				DEBUG("xml queue[%d]: %s\n", i, s_push_xml[i].uri);
				if(strlen(s_push_xml[i].uri)>0){
					DEBUG("will parse[%d] %s\n", i, s_push_xml[i].uri);
					parse_xml(s_push_xml[i].uri, s_push_xml[i].flag, s_push_xml[i].id);
					
					memset(s_push_xml[i].uri, 0, sizeof(s_push_xml[i].uri));
					s_push_xml[i].flag = PUSH_XML_FLAG_UNDEFINED;
					memset(s_push_xml[i].id, 0, sizeof(s_push_xml[i].id));
				}
			}
		}
		pthread_mutex_unlock(&mtx_xml);
	}
	DEBUG("exit from xml parse thread\n");
	
	return NULL;
}

void usage()
{
	printf("-i	interface name, default value is eth0.\n");
	printf("-h	print out this message.\n");
	
	printf("\n");
	exit(0);
}

int send_xml_to_parse(const char *path, int flag, char *id)
{
	int ret = 0;
	
	if(	PUSH_XML_FLAG_MINLINE<flag && flag<PUSH_XML_FLAG_MAXLINE){
		if(0==check_tail(path, ".xml", 0))
		{
			DEBUG("path: %s\n", path);
			pthread_mutex_lock(&mtx_xml);
			
			int i = 0;
			for(i=0; i<XML_NUM; i++){
				if(0==strlen(s_push_xml[i].uri)){
					DEBUG("add to index %d: [%s]path: %s\n", i,id,path);
					snprintf(s_push_xml[i].uri, sizeof(s_push_xml[i].uri),"%s", path);
					s_push_xml[i].flag = flag;
					if(id)
						snprintf(s_push_xml[i].id, sizeof(s_push_xml[i].id),"%s", id);
					break;
				}
			}
			if(XML_NUM<=i){
				DEBUG("xml name space is full\n");
				ret = -1;
			}
			else{
				pthread_cond_signal(&cond_xml); //send sianal
				ret = 0;
			}
				
			pthread_mutex_unlock(&mtx_xml);
			DEBUG("path in queue: %s ok\n", path);
		}
		else{
			DEBUG("this is not a xml\n");
			ret = -1;
		}
	}
	else{
		DEBUG("this file(%d) is ignore\n", flag);
		ret = -1;
	}
	
	return ret;
}

void callback(const char *path, long long size, int flag)
{
	DEBUG("\n\n\n===========================path:%s, size:%lld, flag:%d=============\n\n\n", path, size, flag);
	
	/* 由于涉及到解析和数据库操作，这里不直接调用parseDoc，避免耽误push任务的运行效率 */
	// settings/allpid/allpid.xml
	send_xml_to_parse(path, flag, NULL);
}


int push_decoder_buf_init()
{
	g_recvBuffer = (DataBuffer *)malloc(sizeof(DataBuffer)*MAX_PACK_BUF);
	if(NULL==g_recvBuffer){
		ERROROUT("can not malloc %d*%d\n", sizeof(DataBuffer), MAX_PACK_BUF);
		return -1;
	}
	else
		DEBUG("malloc for push decoder buffer %d*%d success\n", sizeof(DataBuffer), MAX_PACK_BUF);

#ifdef MEMSET_PUSHBUF_SAFE	
	memset(g_recvBuffer,0,sizeof(DataBuffer)*MAX_PACK_BUF);	
	DEBUG("g_recvBuffer=%p\n", g_recvBuffer);
#else	
	g_recvBuffer[0].m_len = 0;
	g_recvBuffer[1].m_len = 0;
#endif
	
	return 0;
}

static int push_decoder_buf_uninit()
{
	if(g_recvBuffer){
		DEBUG("free push decoder buf\n");
		DataBuffer *tmp_recvbuf = g_recvBuffer;
		g_recvBuffer = NULL;
		usleep(300);
		free(tmp_recvbuf);
		DEBUG("free push decoder buf: %p\n", tmp_recvbuf);
		tmp_recvbuf = NULL;
	}
	return 0;
}

int mid_push_init(char *push_conf)
{
	int i = 0;
	for(i=0;i<XML_NUM;i++){
		memset(s_push_xml[i].uri, 0, sizeof(s_push_xml[i].uri));
		s_push_xml[i].flag = PUSH_XML_FLAG_UNDEFINED;
		memset(s_push_xml[i].id, 0, sizeof(s_push_xml[i].id));
	}
	
	for(i=0; i<PROGS_NUM; i++){
		memset(&s_prgs[i], 0, sizeof(PROG_S));
	}
	
	/*
	* 初始化PUSH库
	 */
	if (push_init(push_conf) != 0)
	{
		DEBUG("Init push lib failed with %s!\n", push_conf);
		return -1;
	}
	else
		DEBUG("Init push lib success with %s!\n", push_conf);
	
	
	/*
	初始化拒绝接收和接收监控，必须在push解码线程之前。
	*/
	push_recv_manage_refresh(1,NULL);
	
	/*
	确保开机后至少有一次扫描机会，获得准确的下载进度。
	*/
	s_push_has_data = 3;
	
	push_set_notice_callback(callback);
	
	//创建数据解码线程
	pthread_create(&tidDecodeData, NULL, push_decoder_thread, NULL);
	//pthread_detach(tidDecodeData);
	
	//创建监视线程
	pthread_t tidMonitor;
	pthread_create(&tidMonitor, NULL, push_monitor_thread, NULL);
	pthread_detach(tidMonitor);
	
	//创建xml解析线程
	pthread_t tidxmlparse;
	pthread_create(&tidxmlparse, NULL, push_xml_parse_thread, NULL);
	pthread_detach(tidxmlparse);
	
	return 0;
}

int mid_push_uninit()
{
//	push_rely_condition_set(RELY_CONDITION_EXIT);
	
	pthread_mutex_lock(&mtx_xml);
	s_xmlparse_running = 0;
	pthread_cond_signal(&cond_xml);
	pthread_mutex_unlock(&mtx_xml);
	
	pthread_mutex_lock(&mtx_push_monitor);
	s_monitor_running = 0;
	pthread_cond_signal(&cond_push_monitor);
	pthread_mutex_unlock(&mtx_push_monitor);
	
	s_push_has_data = 0;
	
	push_destroy();
	
	s_decoder_running = 0;
	pthread_join(tidDecodeData, NULL);
	push_decoder_buf_uninit();
	
	return 0;
}

/*
 将push暂停，目前在清理硬盘前调用。
*/
int push_pause()
{
#if 0
	push_destroy();
#endif

	return 0;
}

/*
 将push恢复，目前在清理硬盘后调用。
*/
int push_resume()
{
#if 0
	if (push_init(PUSH_CONF) != 0)
	{
		DEBUG("Init push lib failed with %s!\n", push_conf);
		return -1;
	}
	else
		DEBUG("Init push lib success with %s!\n", push_conf);
#endif
	return 0;
}


int TC_loader_to_push_order(int ord)
{
	DEBUG("ord: %d\n", ord);
    if (ord)
    {
        s_decoder_running = 1;
    }
    else
    {
        s_decoder_running = 2;
       // g_wIndex = 0;
    }
    return 0;
}

int TC_loader_get_push_state(void)
{
    return push_idle;
}

int TC_loader_get_push_buf_size(void)
{
    return sizeof(DataBuffer)*MAX_PACK_BUF;
}

unsigned char * TC_loader_get_push_buf_pointer(void)
{
    return (unsigned char *)g_recvBuffer;
}

/*
注册节目
*/
static int mid_push_regist(PROG_S *prog)
{
	if(NULL==prog){
		DEBUG("arg is invalid\n");
		return -1;
	}
	if(0==prog_is_valid(prog)){
		DEBUG("invalid prog to regist monitor\n");
		return -1;
	}
	
	/*
	* Notice:节目路径是一个相对路径，不要以'/'开头；
	* 若节目单中给出的路径是"/vedios/pushvod/1944"，则去掉最开始的'/'，
	* 用"vedios/pushvod/1944"进行注册。
	*
	* 此处PRG这个结构体是出于示例方便定义的，不一定适用于您的程序中
	*/
	int i = 0, ret = -1;
	
/*
 先判断此uri是否已经在monitor内，防止重复插入相同的目录监控导致监控数组爆满
*/
	for(i=0; i<PROGS_NUM; i++)
	{
		if(1==prog_is_valid(&s_prgs[i]) && 0==strcmp(s_prgs[i].uri,prog->uri)){
			DEBUG("Warning: this prog[id=%s] is already regist, cover old record\n",s_prgs[i].id);
			
			snprintf(s_prgs[i].id, sizeof(s_prgs[i].id), "%s", prog->id);
			snprintf(s_prgs[i].uri, sizeof(s_prgs[i].uri), "%s", prog->uri);
			snprintf(s_prgs[i].descURI, sizeof(s_prgs[i].descURI), "%s", prog->descURI);
			snprintf(s_prgs[i].deadline, sizeof(s_prgs[i].deadline), "%s", prog->deadline);
			s_prgs[i].type = prog->type;
			s_prgs[i].cur = prog->cur;
			s_prgs[i].total = prog->total;
			s_prgs[i].parsed = prog->parsed;
			
			DEBUG("regist to push[%d]:%s %s %s %s %lld\n",
					i,
					s_prgs[i].id,
					s_prgs[i].caption,
					s_prgs[i].uri,
					s_prgs[i].descURI,
					s_prgs[i].total);
			
			return 0;
		}
	}
	
	for(i=0; i<PROGS_NUM; i++)
	{
		if(-1==prog_is_valid(&s_prgs[i])){
			snprintf(s_prgs[i].id, sizeof(s_prgs[i].id), "%s", prog->id);
			snprintf(s_prgs[i].uri, sizeof(s_prgs[i].uri), "%s", prog->uri);
			snprintf(s_prgs[i].descURI, sizeof(s_prgs[i].descURI), "%s", prog->descURI);
			snprintf(s_prgs[i].deadline, sizeof(s_prgs[i].deadline), "%s", prog->deadline);
			s_prgs[i].type = prog->type;
			s_prgs[i].cur = prog->cur;
			s_prgs[i].total = prog->total;
			s_prgs[i].parsed = prog->parsed;
			
			push_dir_register(s_prgs[i].uri, s_prgs[i].total, 0);
			s_push_monitor_active++;
			
			DEBUG("regist to push[%d]:%s %s %s %lld\n",
					i,
					s_prgs[i].id,
					s_prgs[i].uri,
					s_prgs[i].descURI,
					s_prgs[i].total);
			break;
		}
	}
	
	if(i>=PROGS_NUM){
		DEBUG("progs monitor array is overflow\n");
		ret = -1;
	}
	else
		ret = 0;
	
	return ret;
}


/*
 反注册监控节目。
*/
static int mid_push_unregist(PROG_S *prog)
{
	/*
	* Notice:节目路径是一个相对路径，不要以'/'开头；
	* 若节目单中给出的路径是"/vedios/pushvod/1944"，则去掉最开始的'/'，
	* 用"vedios/pushvod/1944"进行注册。
	*
	* 此处PRG这个结构体是出于示例方便定义的，不一定适用于您的程序中
	*/
	int ret = -1;
	if(prog){
		DEBUG("unregist from push monitor: %s %lld\n", prog->uri, prog->total);
		
		push_dir_unregister(prog->uri);
		
		memset(prog, 0, sizeof(PROG_S));
		
		s_push_monitor_active--;
		ret = 0;
	}
	else{
		DEBUG("invalid arg\n");
		ret = -1;
	}
	
	return ret;
}

/*
填充节目的名称
*/
static int prog_name_fill()
{
	/*
	* Notice:节目路径是一个相对路径，不要以'/'开头；
	* 若节目单中给出的路径是"/vedios/pushvod/1944"，则去掉最开始的'/'，
	* 用"vedios/pushvod/1944"进行注册。
	*
	* 此处PRG这个结构体是出于示例方便定义的，不一定适用于您的程序中
	*/
	int i;
	char sqlite_cmd[256];
	for(i=0; i<PROGS_NUM; i++)
	{
		if(1==prog_is_valid(&s_prgs[i]) && 0==strlen(s_prgs[i].caption)){
			memset(s_prgs[i].caption, 0, sizeof(s_prgs[i].caption));
			snprintf(sqlite_cmd,sizeof(sqlite_cmd),"SELECT StrValue FROM ResStr WHERE ServiceID='%s' AND ObjectName='ProductDesc' AND EntityID='%s' AND StrLang='%s' AND (StrName='ProductDescName' OR StrName='SProductName' OR StrName='ColumnName');", 
				serviceID_get(),s_prgs[i].id,language_get());
			
			if(0==str_sqlite_read(s_prgs[i].caption,sizeof(s_prgs[i].caption),sqlite_cmd)){
				DEBUG("read prog caption success: %s\n", s_prgs[i].caption);
			}
			else{
				DEBUG("read prog caption failed, filled with prog id: %s\n",s_prgs[i].id);
				snprintf(s_prgs[i].caption, sizeof(s_prgs[i].caption), "%s", s_prgs[i].id);
			}
		}
	}
	
	return 0;
}

int mid_push_reject(const char *prog_uri)
{
	if(NULL==prog_uri){
		DEBUG("invalid prog_uri\n");
		return -1;
	}
	
	int ret = 0;
	ret = push_dir_forbid(prog_uri);
	if(0==ret)
		DEBUG("push forbid: %s\n", prog_uri);
	else if(-1==ret)
		DEBUG("push forbid failed: %s, no such uri\n", prog_uri);
	else
		DEBUG("push forbid failed: %s, some other err(%d)\n", prog_uri, ret);
	
	ret = push_dir_remove(prog_uri);
	if(0==ret)
		DEBUG("push remove: %s\n", prog_uri);
	else if(-1==ret)
		DEBUG("push remove failed: %s, no such uri\n", prog_uri);
	else
		DEBUG("push remove failed: %s, some other err(%d)\n", prog_uri, ret);
	
	char reject_uri[128];
	snprintf(reject_uri,sizeof(reject_uri),"%s/%s",push_dir_get(),prog_uri);
	unlink(reject_uri);
	DEBUG("unlink(%s)\n", reject_uri);
	
	return ret;
}

/*
回调结束时，receiver携带是否有进度注册的标记，1表示有注册，0表示无注册。
*/
static int push_recv_manage_cb(char **result, int row, int column, void *receiver, unsigned int receiver_size)
{
	DEBUG("sqlite callback, row=%d, column=%d, receiver addr=%p, receive_size=%u\n", row, column, receiver,receiver_size);
	if(row<1){
		DEBUG("no record in table, return\n");
		return 0;
	}
//ProductDescID,ID,ReceiveType,URI,DescURI,TotalSize,PushStartTime,PushEndTime,ReceiveStatus,FreshFlag,Parsed
	int i = 0;
	int j = 0;
	int recv_flag = 1;
	int tmp_init_flag = *((int *)receiver);
	RECEIVESTATUS_E receive_status = RECEIVESTATUS_REJECT;
	long long totalsize = 0LL;
	
	DEBUG("*receiver(init flag)=%d\n", tmp_init_flag);
	
	*((int *)receiver) = 0;
	
	for(i=1;i<row+1;i++)
	{
		/*
		如果是开机时注册；或者不是开机注册、但FreshFlag为1，则注册拒绝接收或进度监控
		否则，不加处理。
		*/
		if(1==tmp_init_flag || (1!=tmp_init_flag && 1==atoi(result[i*column+9]))){
			/*
			对于成品，如果用户选择不接收，则一定不接收，不需要更加详细的判断
			否则，根据业务等条件进行判断
			*/
			if(RECEIVETYPE_PUBLICATION==atoi(result[i*column+2]) && 0==guidelist_select_status((const char *)(result[i*column+1]))){
				recv_flag = 0;
				DEBUG("this prog(%s) is reject by user in guidelist\n", result[i*column+3]);
			}
			else
			{
				receive_status = atoi(result[i*column+8]);
				if(RECEIVESTATUS_REJECT==receive_status){
					/*
					拒绝接收时一定要小心，相同的Publication有可能既属于当前service，又属于其他service；尤其是，在其他Service中需要接收。
					*/
					recv_flag = 0;
					for(j=1;j<row+1;j++){
						if(0==strcmp(result[j*column+3],result[i*column+3]) && (RECEIVESTATUS_WAITING==atoi(result[j*column+8])|| RECEIVESTATUS_FINISH==atoi(result[j*column+8]))){
							DEBUG("this prog(%s) is need recv in other service, do not reject it\n",result[i*column+3]);
							recv_flag = 1;
							break;
						}
					}
				}
				else if (RECEIVESTATUS_WAITING==receive_status || RECEIVESTATUS_FINISH==receive_status){
					recv_flag = 1;
				}
				else{ // RECEIVESTATUS_FAILED==receive_status || RECEIVESTATUS_HISTORY==receive_status
					DEBUG("[%d:%s] %s is ignored by push monitor\n", i,result[i*column],result[i*column+3]);
					recv_flag = 0;
				}
			}
			
			if(0==recv_flag){
				mid_push_reject(result[i*column+3]);
			}
			else{
				sscanf(result[i*column+5],"%lld", &totalsize);
				
				PROG_S cur_prog;
				memset(&cur_prog,0,sizeof(cur_prog));
				snprintf(cur_prog.id,sizeof(cur_prog.id),"%s",result[i*column]);
				snprintf(cur_prog.uri,sizeof(cur_prog.uri),"%s",result[i*column+3]);
				snprintf(cur_prog.descURI,sizeof(cur_prog.descURI),"%s",result[i*column+4]);
				memset(cur_prog.caption,0,sizeof(cur_prog.caption));
				snprintf(cur_prog.deadline,sizeof(cur_prog.deadline),"%s",result[i*column+7]);
				cur_prog.type = atoi(result[i*column+2]);
				cur_prog.cur = 0LL;
				cur_prog.total = totalsize;
				cur_prog.parsed = atoi(result[i*column+10]);
				mid_push_regist(&cur_prog);
				
				/*
				回调结束时，receiver携带是否有进度注册的标记，1表示有注册，0表示无注册。
				*/
				*((int *)receiver) = 1;
			}
		}
	}
	
	return 0;
}

/*
 当下发新的ProductDesc.xml或Service.xml时刷新push拒绝接收注册和进度监控注册
 init_flag——1：初始化，表示需要处理ProductDesc所有的节目
 init_flag——0：非初始化，表示是动态处理，接收到新的Service.xml和ProductDesc.xml，只处理FreshFlag为1的节目
 init_flag——2：从monitor中调用的实时监控，目的是清理掉过期的节目不再进行进度监控，避免终端几天不关机后监控累积
*/
int push_recv_manage_refresh(int init_flag, char *time_stamp_pointed)
{
	DEBUG("init_flag: %d, time_stamp_pointed: %s\n", init_flag, time_stamp_pointed);
	
	int ret = -1;
	char sqlite_cmd[256+128];
	int (*sqlite_callback)(char **, int, int, void *, unsigned int) = push_recv_manage_cb;
	
	char time_stamp[32];
	memset(time_stamp, 0, sizeof(time_stamp));
	if(NULL==time_stamp_pointed || 0==strlen(time_stamp_pointed)){
		snprintf(sqlite_cmd,sizeof(sqlite_cmd),"select datetime('now','localtime');");
		if(-1==str_sqlite_read(time_stamp,sizeof(time_stamp),sqlite_cmd)){
			DEBUG("can not process push regist\n");
			return -1;
		}
	}
	else
		snprintf(time_stamp,sizeof(time_stamp),"%s",time_stamp_pointed);
	
	pthread_mutex_lock(&mtx_push_monitor);
	
	if(1==init_flag){
/*
 开机初始化时，先删掉所有过期的节目
*/
		snprintf(sqlite_cmd,sizeof(sqlite_cmd),"DELETE FROM ProductDesc WHERE PushEndTime<'%s';", time_stamp);
		sqlite_execute(sqlite_cmd);
	}
	
	int flag_carrier = init_flag;
	
/*
 由于一个Publication可能存在于多个service中，因此需要全部取出，在回调中遍历那些需要拒绝的publication是否恰好也在需要接收之列。
 所以对FreshFlag的判断移动到回调中进行，避免其条件在FreshFlag之外
*/
	snprintf(sqlite_cmd,sizeof(sqlite_cmd),"SELECT ProductDescID,ID,ReceiveType,URI,DescURI,TotalSize,PushStartTime,PushEndTime,ReceiveStatus,FreshFlag,Parsed FROM ProductDesc WHERE PushStartTime<='%s' AND PushEndTime>'%s';", time_stamp,time_stamp);
	
	ret = sqlite_read(sqlite_cmd, (void *)(&flag_carrier), sizeof(flag_carrier), sqlite_callback);
/*
回调结束时，flag_carrier携带是否有进度注册的标记，1表示有注册，0表示无注册。
*/	
	PRINTF("ret: %d, flag_carrier: %d\n", ret,flag_carrier);
	if(ret>0 && flag_carrier>0){
		prog_name_fill();
		
		snprintf(sqlite_cmd,sizeof(sqlite_cmd),"UPDATE ProductDesc SET FreshFlag=0 WHERE PushStartTime<='%s' AND PushEndTime>'%s' AND FreshFlag=1;", time_stamp,time_stamp);
		sqlite_execute(sqlite_cmd);

#if 0
		if(0==init_flag){
			pthread_cond_signal(&cond_push_monitor);
			DEBUG("refresh monitor arrary immediatly\n");
		}
#endif

	}
	
	pthread_mutex_unlock(&mtx_push_monitor);

	return ret;
}

