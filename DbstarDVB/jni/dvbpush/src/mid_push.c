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

#include "common.h"
#include "push.h"
#include "mid_push.h"
#include "porting.h"
#include "xmlparser.h"
#include "sqlite.h"
#include "dvbpush_api.h"

#define MAX_PACK_LEN (1500)
#define MAX_PACK_BUF (200000)		//定义缓冲区大小，单位：包	1500*200000=280M

#define XML_NUM			8
static PUSH_XML_S s_push_xml[XML_NUM];

static pthread_mutex_t mtx_xml = PTHREAD_MUTEX_INITIALIZER;
static pthread_cond_t cond_xml = PTHREAD_COND_INITIALIZER;
static pthread_mutex_t mtx_push_monitor = PTHREAD_MUTEX_INITIALIZER;
static pthread_cond_t cond_push_monitor = PTHREAD_COND_INITIALIZER;

//数据包结构
typedef struct tagDataBuffer
{
    short	m_len;
    unsigned char	m_buf[MAX_PACK_LEN];
}DataBuffer;

typedef struct tagPRG
{
	char		id[32];
	char		uri[256];
	char		caption[128];
	long long	cur;
	long long	total;
}PROG_S;

static int mid_push_regist(char *id, char *content_uri, long long content_len);
static int push_monitor_regist(int regist_flag);

#define PROGS_NUM 64
static PROG_S s_prgs[PROGS_NUM];
//static char s_push_data_dir[256];
/*************接收缓冲区定义***********/
DataBuffer *g_recvBuffer;	//[MAX_PACK_BUF]
static int g_wIndex = 0;
static int g_rIndex = 0;
/**************************************/

static pthread_t tidDecodeData;
static int s_xmlparse_running = 0;
static int s_monitor_running = 0;
static int s_decoder_running = 0;
static char *s_dvbpush_info = NULL;
static time_t s_recv_status_refresh_pin = 0;
static int s_push_monitor_active = 0;

/*
当向push中写数据时才有必要监听进度，否则直接使用数据库中记录的进度即可。
考虑到缓冲，在无数据后多查询几轮再停止查询，因此push数据时，置此值为3。
考虑到开机最好给一次显示的机会，初始化为1。
*/
static int s_push_has_data = 0;

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
	
	if (pkt_len < 16) {
		printf("IP/MPE packet length = %d too small.\n", pkt_len);
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
	
	if(NULL==g_recvBuffer)
		return res;
	
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
	int read_nothing_count = 0;
    short len;
	
	s_decoder_running = 1;
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
			g_rIndex = rindex;
		}
		else
		{
			usleep(20000);
			read_nothing_count++;
			if(read_nothing_count>=500)
			{
				DEBUG("read nothing, read index %d\n", rindex);
				read_nothing_count = 0;
			}
		}
	}
	DEBUG("exit from push decoder thread\n");
	
	return NULL;
}

static void push_progs_finish(char *id)
{
	char sqlite_cmd[256+128];
	
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "UPDATE content SET ready=1 WHERE id='%s';", id);
	sqlite_execute(sqlite_cmd);
}

#if 0
static void push_progs_process_refresh(char *regist_dir, long long cur_size)
{
	char sqlite_cmd[256+128];
	
	memset(sqlite_cmd, 0, sizeof(sqlite_cmd));
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "UPDATE brand SET download=%lld WHERE regist_dir='%s';", cur_size, regist_dir);
	sqlite_execute(sqlite_cmd);
}
#endif

static int push_monitor_frequency_limit()
{
	time_t now_sec = time(NULL);
	if(now_sec-s_recv_status_refresh_pin>=5){
		DEBUG("time now=%ld, s_recv_status_refresh_pin=%ld\n", now_sec, s_recv_status_refresh_pin);
		s_recv_status_refresh_pin = now_sec;
		return 0;
	}
	else{
		DEBUG("time now=%ld, s_recv_status_refresh_pin=%ld, too frequent\n", now_sec, s_recv_status_refresh_pin);
		return -1;
	}
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
	DEBUG("start.........\n");
	s_push_monitor_active = push_monitor_regist(1);
	DEBUG("here start %d progs\n", s_push_monitor_active);
}

void dvbpush_getinfo_stop()
{
	push_monitor_regist(0);
	
	if(NULL!=s_dvbpush_info){
		DEBUG("FREE s_dvbpush_info\n");
		free(s_dvbpush_info);
		s_dvbpush_info = NULL;
	}
	DEBUG("here stop\n");
}

int dvbpush_getinfo(char **p, unsigned int *len)
{
	if(NULL!=s_dvbpush_info){
		DEBUG("FREE s_dvbpush_info\n");
		free(s_dvbpush_info);
		s_dvbpush_info = NULL;
	}
	
	if(-1==push_monitor_frequency_limit())
		return -1;
	
	int info_size;
	int i = 0;
	/*
	 形如：1001\taaaaaaname\t23932\t23523094823\n1002\tbbbbbbname\t234239\t12349320\n1003\tcccccname\t0\t213984902943
	 每条记录预留长度：64位id + strlen(caption) + 20位当前长度 + 20位总长 + 4位分隔符
	 其中：long long型转为10进制后最大长度为20
	*/
	if(1 && (s_push_monitor_active>0)){	// TRUE is only for test, here should be: s_push_has_data>0
		info_size = s_push_monitor_active*(256+64+20+20+4) + 1;
		s_dvbpush_info = malloc(info_size);
		
		if(s_dvbpush_info){
			DEBUG("malloc %d Bs for push info\n", info_size);
			s_dvbpush_info[0]='\0';
			/*
			监测节目接收进度
			*/
			for(i=0; i<PROGS_NUM; i++)
			{
				if(-1==prog_is_valid(&s_prgs[i]))
					break;
				/*
				* 获取指定节目的已接收字节大小，可算出百分比
				*/
				long long rxb = push_dir_get_single(s_prgs[i].uri);
				
				DEBUG("PROG_S:%s %s %lld/%lld %-3lld%%\n",
					s_prgs[i].id,
					s_prgs[i].uri,
					rxb,
					s_prgs[i].total,
					rxb*100/s_prgs[i].total);
					
				s_prgs[i].cur += 10*1024*1024;
				if(s_prgs[i].cur>s_prgs[i].total)
					s_prgs[i].cur = s_prgs[i].total;
					
				if(0==i){
					snprintf(s_dvbpush_info, info_size,
						"%s\t%s\t%lld\t%lld", s_prgs[i].id,s_prgs[i].caption,s_prgs[i].cur,s_prgs[i].total);
				}
				else{
					snprintf(s_dvbpush_info+strlen(s_dvbpush_info), info_size-strlen(s_dvbpush_info),
						"%s%s\t%s\t%lld\t%lld", "\n",s_prgs[i].id,s_prgs[i].caption,s_prgs[i].cur,s_prgs[i].total);
				}
				
				if(rxb>=s_prgs[i].total){
					DEBUG("%s download finished, wipe off from monitor, and set 'ready'\n", s_prgs[i].uri);
					push_progs_finish(s_prgs[i].id);
				}
			}
			*p = s_dvbpush_info;
			*len = strlen(s_dvbpush_info);
			DEBUG("%s\n", s_dvbpush_info);
			
			return 0;
		}
		else
			DEBUG("malloc %d Bs for push info failed\n", info_size);
		
		s_push_has_data --;
	}
	return -1;
}

/*
为避免无意义的查询硬盘，应完成下面两个工作：
1、当节目接收完毕后不应再查询，数据库中记录的是100%
2、只有UI上进入查看进度的界面后，通知底层去查询，其他时间查询没有意义。
3、当push无数据后，再轮询若干遍（等待缓冲数据写入硬盘）后就不再轮询。
*/
//void *push_monitor_thread()
//{
////	char *p = NULL;
////	unsigned int len = 0;
//	
//	s_monitor_running = 1;
//	//dvbpush_getinfo_start();
//	while (1==s_monitor_running)
//	{
//		sleep(5);
//		//dvbpush_getinfo(&p, &len);
//	}
//	//dvbpush_getinfo_stop();
//	DEBUG("exit from push monitor thread\n");
//	
//	return NULL;
//}

void *push_xml_parse_thread()
{
	s_xmlparse_running = 1;
	while (1==s_xmlparse_running)
	{
		pthread_mutex_lock(&mtx_xml);
		pthread_cond_wait(&cond_xml,&mtx_xml); //wait
		if(1==s_xmlparse_running){
			int i = 0;
			for(i=0; i<XML_NUM; i++){
				if(strlen(s_push_xml[i].uri)>0){
					parse_xml(s_push_xml[i].uri, s_push_xml[i].flag);
					memset(s_push_xml[i].uri, 0, sizeof(s_push_xml[i].uri));
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

//int pushdata_rootdir_get(char *buf, unsigned int size)
//{
//	if(NULL==buf || 0==size){
//		DEBUG("some arguments are invalid\n");
//		return -1;
//	}
//	
//	strncpy(buf, s_push_data_dir, size);
//	return 0;
//}

void callback(const char *path, long long size, int flag)
{
	DEBUG("path:%s, size:%lld, flag:%d\n", path, size, flag);
	
	/* 由于涉及到解析和数据库操作，这里不直接调用parseDoc，避免耽误push任务的运行效率 */
	// settings/allpid/allpid.xml
	if(	PUSH_XML_FLAG_MINLINE<flag && flag<PUSH_XML_FLAG_MAXLINE){
		if(0==check_tail(path, ".xml", 0)){
			pthread_mutex_lock(&mtx_xml);
			
			int i = 0;
			for(i=0; i<XML_NUM; i++){
				if(0==strlen(s_push_xml[i].uri)){
					snprintf(s_push_xml[i].uri, sizeof(s_push_xml[i].uri),"%s", path);
					s_push_xml[i].flag = flag;
					break;
				}
			}
			if(XML_NUM<=i)
				DEBUG("xml name space is full\n");
			else
				pthread_cond_signal(&cond_xml); //send sianal
				
			pthread_mutex_unlock(&mtx_xml);
		}
		else
			DEBUG("this is not a xml\n");
	}
	else
		DEBUG("this file is ignore\n");
}

/*
 如果传入参数为空，则寻找“/etc/push.conf”文件。详细约束参考push_init()说明
 此函数需要及早调用，xml解析模块也需要此值。目前在main()中调用。
*/
//void push_root_dir_init(char *push_conf)
//{
//	FILE* fp = NULL;
//	char tmp_buf[256];
//	char *p_value;
//	
//	if(NULL==push_conf)
//		fp = fopen(PUSH_CONF_DF, "r");
//	else
//		fp = fopen(push_conf, "r");
//	
//	memset(s_push_data_dir, 0, sizeof(s_push_data_dir));
//	if(fp){
//		memset(tmp_buf, 0, sizeof(tmp_buf));
//		while(NULL!=fgets(tmp_buf, sizeof(tmp_buf), fp)){
//			p_value = setting_item_value(tmp_buf, strlen(tmp_buf), '=');
//			if(NULL!=p_value)
//			{
//				if(strlen(tmp_buf)>0 && strlen(p_value)>0){
//					if(0==strcmp(tmp_buf, "DATA_DIR")){
//						strncpy(s_push_data_dir, p_value, sizeof(s_push_data_dir)-1);
//						break;
//					}
//				}
//			}
//			memset(tmp_buf, 0, sizeof(tmp_buf));
//		}
//		fclose(fp);
//	}
//	
//	if(0==strlen(s_push_data_dir)){
//		DEBUG("waring: open %s to get push data dir failed, use %s instead\n",s_push_data_dir, PUSH_DATA_DIR_DF);
//		strncpy(s_push_data_dir, PUSH_DATA_DIR_DF, sizeof(s_push_data_dir)-1);
//	}
//	else{
//		if('/'==s_push_data_dir[strlen(s_push_data_dir)-1])
//			s_push_data_dir[strlen(s_push_data_dir)-1] = '\0';
//	}
//}

/*
根据s_prgs数组对push接收进度查询进行注册和反注册。
regist_flag: 1表示注册；0表示反注册。
注意：必须降低push接收进度查询，因为在大量数据接收时，硬盘本身有很大的写数据压力。
同时，如果注册了接收进度查询，但是没有主动查询时，push模块自身会15秒左右查询一次，这应当避免。
因此，每次查询前注册，查询后立即反注册。

返回值：返回注册或反注册的节目个数，异常时返回-1
*/
static int push_monitor_regist(int regist_flag)
{
	int i = 0;
	int ret = 0;
	
	if(1==regist_flag || 0==regist_flag){
		for(i=0; i<PROGS_NUM; i++){
			//DEBUG("s_prgs[%d]=%s\n", i, s_prgs[i].uri);
			if(1==prog_is_valid(&s_prgs[i])){
				ret ++;
				if(1==regist_flag)
					push_dir_register(s_prgs[i].uri, s_prgs[i].total, 0);
				else if(0==regist_flag)
					push_dir_unregister(s_prgs[i].uri);
			}
		}
	}
	else{
		DEBUG("invalid regist flag: %d\n", regist_flag);
		ret = -1;
	}
	
	return ret;
}

static int pushlist_sqlite_cb(char **result, int row, int column, void *receiver)
{
	DEBUG("sqlite callback, row=%d, column=%d, receiver addr: %p\n", row, column, receiver);
	if(row<1){
		DEBUG("no record in table, return\n");
		return 0;
	}
	
	int i = 0;
	long long totalsize = 0LL;
	for(i=1;i<row+1;i++)
	{
		//DEBUG("==%s:%s:%ld==\n", result[i*column], result[i*column+1], strtol(result[i*column+1], NULL, 0));
		sscanf(result[i*column+2],"%lld", &totalsize);
		mid_push_regist(result[i*column], result[i*column+1], totalsize);
	}
	
	return 0;
}

/*
初始化push monitor数组，一般情况是从数据库brand表中读取需要监控的节目。
如果下发了新的brand.xml，解析xml并入库后，需要调用此函数刷新数组。
需要确保刷新数组前就已经将数组中非空条目从push模块中反注册，避免监控垃圾信息。
*/
int push_monitor_reset()
{
	int ret = -1;
	char sqlite_cmd[256+128];
	int (*sqlite_callback)(char **, int, int, void *) = pushlist_sqlite_cb;

	pthread_mutex_lock(&mtx_push_monitor);
#if 0	// only for test	
	mid_push_regist("1","prog/video/1", 206237980LL);
	mid_push_regist("2","prog/file/2", 18816360LL);
	mid_push_regist("3","prog/audio/3", 38729433LL);
	mid_push_regist("4","prog/video/4", 2206237980LL);
	mid_push_regist("5","prog/file/5", 11118816360LL);
	mid_push_regist("6","prog/audio/6", 30338729433LL);
	mid_push_regist("7","prog/video/7", 21206237980LL);
	mid_push_regist("8","prog/file/这是中文测试文件名", 1882316360LL);
	mid_push_regist("9","prog/audio/9", 3872439433LL);
	mid_push_regist("10","prog/video/10", 20625337980LL);
	mid_push_regist("11","prog/file/11", 18816323460LL);
	mid_push_regist("12","prog/audio/12", 38729423433LL);
	mid_push_regist("13","prog/video/13", 206237942380LL);
	mid_push_regist("14","prog/file/14", 1881636043LL);
	mid_push_regist("15","中文长文件名测试，中文English混排，长文件名", 3872943433LL);
	ret = 0;
#else
	/*
	虽然投递单中还有成品集PublicationsSet、预告单GuideList、小片Preview，但与用户紧密相关的只有成品Publication
	*/
#if 0
	snprintf(sqlite_cmd,sizeof(sqlite_cmd),"SELECT ProductDescID, URI, TotalSize FROM ProductDesc;");
#else
	snprintf(sqlite_cmd,sizeof(sqlite_cmd),"SELECT ProductDescID, URI, TotalSize FROM Publication WHERE ReceiveStatus='0' OR ReceiveStatus='1';");
#endif
	ret = sqlite_read(sqlite_cmd, NULL, sqlite_callback);
#endif
	pthread_mutex_unlock(&mtx_push_monitor);

	return ret;
}

int mid_push_init(char *push_conf)
{
	int i = 0;
	for(i=0;i<XML_NUM;i++){
		memset(s_push_xml[i].uri, 0, sizeof(s_push_xml[i].uri));
		s_push_xml[i].flag = -1;
	}
	
	for(i=0; i<PROGS_NUM; i++){
		memset(s_prgs[i].id, 0, sizeof(s_prgs[i].id));
		memset(s_prgs[i].uri, 0, sizeof(s_prgs[i].uri));
		memset(s_prgs[i].caption, 0, sizeof(s_prgs[i].caption));
		s_prgs[i].cur = 0LL;
		s_prgs[i].total = 0LL;
	}
	push_monitor_reset();
	
	g_recvBuffer = (DataBuffer *)malloc(sizeof(DataBuffer)*MAX_PACK_BUF);
	if(NULL==g_recvBuffer){
		ERROROUT("can not malloc %d*%d\n", sizeof(DataBuffer), MAX_PACK_BUF);
		return -1;
	}
	for(i=0;i<MAX_PACK_BUF;i++)
		g_recvBuffer[i].m_len = 0;
	
	/*
	* 初始化PUSH库
	 */
	if (push_init(push_conf) != 0)
	{
		DEBUG("Init push lib failed!\n");
		return -1;
	}
	
	/*
	确保开机后至少有一次扫描机会，获得准确的下载进度。
	*/
	s_push_has_data = 1;
	
	push_set_notice_callback(callback);
	
	//创建数据解码线程
	pthread_create(&tidDecodeData, NULL, push_decoder_thread, NULL);
	//pthread_detach(tidDecodeData);
	
//	//创建监视线程
//	pthread_t tidMonitor;
//	pthread_create(&tidMonitor, NULL, push_monitor_thread, NULL);
//	pthread_detach(tidMonitor);
	
	//创建xml解析线程
	pthread_t tidxmlphase;
	pthread_create(&tidxmlphase, NULL, push_xml_parse_thread, NULL);
	pthread_detach(tidxmlphase);
	
	return 0;
}

int mid_push_uninit()
{
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
	
	free(g_recvBuffer);
	g_recvBuffer = NULL;
	
	return 0;
}

static char *language_get()
{
	return "chi";
}

//注册节目
static int mid_push_regist(char *id, char *content_uri, long long content_len)
{
	if(NULL==id || NULL==content_uri || 0==strlen(content_uri) || content_len<=0LL){
		DEBUG("some arguments are invalid\n");
		return -1;
	}
	
	/*
	* Notice:节目路径是一个相对路径，不要以'/'开头；
	* 若节目单中给出的路径是"/vedios/pushvod/1944"，则去掉最开始的'/'，
	* 用"vedios/pushvod/1944"进行注册。
	*
	* 此处PRG这个结构体是出于示例方便定义的，不一定适用于您的程序中
	*/
	int i;
	for(i=0; i<PROGS_NUM; i++)
	{
		if(0==strlen(s_prgs[i].uri)){
			snprintf(s_prgs[i].id, sizeof(s_prgs[i].id), "%s", id);
			snprintf(s_prgs[i].uri, sizeof(s_prgs[i].uri), "%s", content_uri);
			s_prgs[i].cur = 0;
			s_prgs[i].total = content_len;
			
			char sqlite_cmd[256];
			memset(s_prgs[i].caption, 0, sizeof(s_prgs[i].caption));
			int (*sqlite_cb)(char **, int, int, void *) = str_read_cb;
			snprintf(sqlite_cmd,sizeof(sqlite_cmd),"SELECT StrValue FROM ResStr WHERE ObjectName='ProductDesc' AND EntityID='%s' AND StrLang='%s' AND StrName='Name' AND Extension='';", 
				s_prgs[i].id, language_get());
		
			int ret_sqlexec = sqlite_read(sqlite_cmd, s_prgs[i].caption, sqlite_cb);
			if(ret_sqlexec<=0){
				DEBUG("read no Name from db, filled with id\n");
				strncpy(s_prgs[i].caption, s_prgs[i].id, sizeof(s_prgs[i].caption)-1);
			}
			
			/*
			只在需要查询时才注册，并在查询后反注册。避免push模块自身无意义的查询
			push_dir_register(s_prgs[i].uri, s_prgs[i].total, 0);
			*/
			DEBUG("regist to push %d: %s %lld\n", i, s_prgs[i].uri, s_prgs[i].total);
			break;
		}
	}
	if(i>=PROGS_NUM){
		DEBUG("progs monitor array is overflow\n");
		return -1;
	}
	else
		return 0;
}
