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
#include <sys/statfs.h>
#include <sys/vfs.h>
#include <dirent.h>
#include <sys/types.h>
#include <sys/stat.h>

#include "common.h"
#include "push.h"
#include "mid_push.h"
#include "porting.h"
#include "xmlparser.h"
#include "sqlite3.h"
#include "sqlite.h"
#include "dvbpush_api.h"
#include "multicast.h"
#include "motherdisc.h"
#include "drmport.h"
#include "drmapi.h"
#include "prodrm20.h"

#define MAX_PACK_LEN (1500)
#define MAX_PACK_BUF (60000)		//���建������С����λ����
//#define MEMSET_PUSHBUF_SAFE			// if MAX_PACK_BUF<200000 define

#define CLEAR_COLUMN_AFTER_PARSED

#define XML_NUM			16
static PUSH_XML_S		s_push_xml[XML_NUM];

static pthread_mutex_t mtx_xml = PTHREAD_MUTEX_INITIALIZER;
//static pthread_cond_t cond_xml = PTHREAD_COND_INITIALIZER;
static pthread_mutex_t mtx_push_monitor = PTHREAD_MUTEX_INITIALIZER;
//static pthread_cond_t cond_push_monitor = PTHREAD_COND_INITIALIZER;

static pthread_mutex_t mtx_maintenance = PTHREAD_MUTEX_INITIALIZER;
static pthread_cond_t cond_maintenance = PTHREAD_COND_INITIALIZER;

static int push_idle = 0;
static int s_disk_manage_flag = 0;

//���ݰ��ṹ
typedef struct tagDataBuffer
{
    short	m_len;
    unsigned char	m_buf[MAX_PACK_LEN];
}DataBuffer;

typedef struct tagPRG
{
	char			id[64];
	char			uri[512];
	char			descURI[512];
	char			caption[256];
	char			deadline[32];
	RECEIVETYPE_E	type;
	long long		cur;
	long long		total;
	int				parsed;
	char			publication_id[64];
	char			product_id[64];	
}PROG_S;

static int push_decoder_buf_uninit();
static int prog_name_fill();

#define PROGS_NUM 256
static PROG_S s_prgs[PROGS_NUM];
//static char s_push_data_dir[256];
/*************���ջ���������***********/
DataBuffer *g_recvBuffer = NULL;	//[MAX_PACK_BUF]
static int g_wIndex = 0;
//static int g_rIndex = 0;
/**************************************/

static pthread_t tidDecodeData;
static int s_xmlparse_running = 0;
static int s_decoder_running = 0;
static int s_push_monitor_active = 0;
static int s_dvbpush_getinfo_flag = 0;
static int s_dvbpush_info_refresh_flag = 0;

static int s_column_refresh = 0;
static int s_interface_refresh = 0;
static int s_preview_refresh = 0;
static int s_push_regist_inited = 0;
static int s_motherdisc_init_flag = 0;

static unsigned long long s_should_clean_M = 0LL;

/*
����push��д����ʱ���б�Ҫ�������ȣ�����ֱ��ʹ�����ݿ��м�¼�Ľ��ȼ��ɡ�
���ǵ����壬�������ݺ���ѯ������ֹͣ��ѯ�����push����ʱ���ô�ֵΪ3��
���ǵ�������ø�һ����ʾ�Ļ��ᣬ��ʼ��Ϊ1��
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
    int ret = 0;
	
	DEBUG("push decoder thread will goto main loop\n");
	s_decoder_running = 1;
rewake:	
	DEBUG("go to push main loop\n");
	while (1==s_decoder_running && NULL!=g_recvBuffer)
	{
		len = g_recvBuffer[rindex].m_len;
		
		// ���̵߳�����Ӱ�쵽��������ʹû��Ӳ��Ҳ�����ô��߳����С�
		// ��Ҫ����ĸ����Initialize������xml�����ص�xml����
		if(len && 1==s_motherdisc_init_flag && 1==pushdir_usable())
		{
			pBuf = g_recvBuffer[rindex].m_buf;
			/*
			* ����PUSH���ݽ����ӿڽ������ݣ��ú����������ģ�����Ӧ��ʹ��һ���ϴ�
			* �Ļ���������ʱ�洢ԴԴ���ϵ����ݡ�
			*/
			
			ret = push_parse((char *)pBuf, len);
			//PRINTF("push_parse[%d] %d, ret=%d\n", rindex,len,ret);
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
				PRINTF("read nothing, read index %d\n", rindex);
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

#if 0
//#define PARSE_XML_VIA_THREAD	// ͨ���̵߳���xml�������׵�������
static int push_prog_finish(char *id, RECEIVETYPE_E type,char *DescURI)
{
	if(NULL==DescURI || NULL==DescURI){
		DEBUG("invalid args\n");
		return -1;
	}
	
	DEBUG("should parse: %s\n", DescURI);
	if(RECEIVETYPE_PUBLICATION==type){
#ifdef PARSE_XML_VIA_THREAD
		mid_push_cb(DescURI,PUBLICATION_XML,id);
#else
/*
ֱ�ӵ���parse_xml��Ŀ���Ǳܿ��̣߳��������parse_xml��push_recv_manage_refreshͬʱʹ��sqlite�⣬�����쳣
���Ƿ��������
��������ֱ�ӵ���parse_xml�ᵼ�����е����񽻲�����sqlite����out of memory
զ���أ�����Ŀǰֻ����sqlite��������ô���һ�����Ᵽ����
*/
		parse_xml(DescURI,PUBLICATION_XML,id);
#endif
	}
	else if(RECEIVETYPE_COLUMN==type){
#ifdef PARSE_XML_VIA_THREAD
		mid_push_cb(DescURI,COLUMN_XML,NULL);
#else
		parse_xml(DescURI,COLUMN_XML,NULL);
#endif
	}
	else if(RECEIVETYPE_SPRODUCT==type){
#ifdef PARSE_XML_VIA_THREAD
		mid_push_cb(DescURI,SPRODUCT_XML,NULL);
#else
		parse_xml(DescURI,SPRODUCT_XML,NULL);
#endif
	}
	else
		DEBUG("this type can not be distinguish\n");
	
	return 0;
}
#endif

int productdesc_parsed_set(char *xml_uri, PUSH_XML_FLAG_E push_flag, char *arg_ext)
{
	char sqlite_cmd[512];
	sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"UPDATE ProductDesc SET Parsed='1' WHERE DescURI='%q';", xml_uri);
	int ret = sqlite_execute(sqlite_cmd);
	
	if(arg_ext && push_flag>0){
	}
#if 0
ĸ�̽���ʱ��arg_extΪ�գ�������ͼɾ��/mnt/sda1
	if(COLUMN_XML==push_flag){
		char reject_uri[512];
		snprintf(reject_uri,sizeof(reject_uri),"%s/%s",push_dir_get(),arg_ext);
		remove_force(reject_uri);
		DEBUG("remove(%s) finished\n", reject_uri);
	}
#endif
	
	return ret;
}


/*
 �����ж��Ƿ��ǺϷ���Ŀ����Ŀ���Ƴ��ȴ���0�������ܴ�С����0
 ����ֵ��	-1��ʾ�Ƿ���1��ʾ�Ϸ���
*/
static int prog_is_valid(PROG_S *prog)
{
	if(NULL==prog)
		return -1;
		
	if(strlen(prog->uri)>0 && (prog->total)>0LL){
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
��Ŀǰ���Ե��������ֻ���ڼ���̵߳���prog_monitorʱ��������ϵĽ�Ŀ����ֱ�ӵ���xml������
����Ǵ�JNI��dvbpush_getinfo�ӿڵ��ù��������ܽ���xml��������Launcher������
*/
static int prog_monitor(PROG_S *prog)	//, char *time_stamp
{
	if(NULL==prog)
		return -1;

#if 0
// 2013-01-23��ֻҪ���²�������ɾ���ɵģ�������PushStartTime��PushEndTime������
	if(NULL!=time_stamp && strcmp(time_stamp,prog->deadline)>0){
		DEBUG("this prog[%s:%s] is overdue, compare with %s\n", prog->id,prog->deadline,time_stamp);
		mid_push_unregist(prog);
		return 0;
	}
#endif
	
	if(s_push_has_data>0 && (prog->cur)<(prog->total)){
		/*
		* ��ȡָ����Ŀ���ѽ����ֽڴ�С��������ٷֱ�
		���ڹ����ϵĴ����п��ܳ��ֵ�ǰ���ȴ����ܳ������������������ʵ��¼rxb��ֵ��ֻ����UIִ��getinfoʱ�ٿ���cur<=total
		*/
		long long rxb = push_dir_get_single(prog->uri);
		
//		PRINTF("PROG_S[%s]:%s %s %lld/%lld %-3lld%%\n",
//			prog->id,
//			prog->uri,
//			prog->descURI,
//			rxb,
//			prog->total,
//			rxb*100/prog->total);
		
		prog->cur = rxb;
	}
//	else
//		PRINTF("s_push_has_data=%d, prog->cur=%lld, prog->total=%lld, no need to monitor\n", s_push_has_data,prog->cur,prog->total);

	return 0;
}


int dvbpush_getinfo(char *buf, unsigned int size)
{
	if(NULL==buf || 0==size){
		DEBUG("invalid args\n");
		return -1;
	}
	
#ifdef TUNER_INPUT
	data_stream_status_str_get(buf,size);
	snprintf(buf+strlen(buf),size-strlen(buf),"\n%d",s_dvbpush_info_refresh_flag);
#else
	snprintf(buf,size,"%d",s_dvbpush_info_refresh_flag);
#endif

	s_dvbpush_info_refresh_flag = 0;
	
	int i = 0;
	if(s_push_monitor_active>0){
		/*
		����Ŀ���ս���
		*/
		pthread_mutex_lock(&mtx_push_monitor);
		for(i=0; i<PROGS_NUM; i++)
		{
			if(-1==prog_is_valid(&s_prgs[i]))
				continue;
			
			prog_monitor(&s_prgs[i]);
			
			if(RECEIVETYPE_PUBLICATION==s_prgs[i].type)
			{
//				if(0==i){
//					snprintf(buf+strlen(buf), size-strlen(buf),
//						"%s\t%s\t%lld\t%lld", s_prgs[i].id,s_prgs[i].caption,s_prgs[i].cur>s_prgs[i].total?s_prgs[i].total:s_prgs[i].cur,s_prgs[i].total);
//				}
//				else
				{
					snprintf(buf+strlen(buf), size-strlen(buf),
						"%s%s\t%s\t%lld\t%lld", "\n",s_prgs[i].id,s_prgs[i].caption,s_prgs[i].cur>s_prgs[i].total?s_prgs[i].total:s_prgs[i].cur,s_prgs[i].total);
				}
			}
			else
				DEBUG("%s\t%s\t%lld\t%lld", s_prgs[i].id,s_prgs[i].caption,s_prgs[i].cur,s_prgs[i].total);
		}
		s_push_has_data --;
		pthread_mutex_unlock(&mtx_push_monitor);
	
		DEBUG("%s\n", buf);
	}
	else
		DEBUG("no program in monitor\n");
	
	return 0;
}

/*
 ����Ƿ��������
 return 0 ���� �������أ�1 ���� �������
*/
int dvbpush_download_finish()
{
	int i = 0;
	int finish_flag = 1;
	
	if(s_push_monitor_active>0){
		for(i=0; i<PROGS_NUM; i++)
		{
			if(-1==prog_is_valid(&s_prgs[i]))
				continue;
			
			// �ܶ�ʱ�������ƷSProduct��û���·������ڲ������д��ڣ��������ֻ�ж���ͨ��Ʒ����Ŀ��СƬ
			if(RECEIVETYPE_PUBLICATION==s_prgs[i].type || RECEIVETYPE_COLUMN==s_prgs[i].type || RECEIVETYPE_PREVIEW==s_prgs[i].type){
				if(s_prgs[i].cur!=s_prgs[i].total){
					DEBUG("at least, this prog is downloading: %s\t%s\t%lld\t%lld", s_prgs[i].id,s_prgs[i].caption,s_prgs[i].cur,s_prgs[i].total);
					finish_flag = 0;
					break;
				}
			}
		}
	}
	else{
		DEBUG("no program in monitor, s_push_monitor_active=%d\n", s_push_monitor_active);
	}
	
	return finish_flag;
}

#if 0
/*
 ����1��ʾ���ڣ�0��ʾʱ����ȣ�-1��ʾ�����ڣ�-2��ʾ��������
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

#if 0
/*
 ����push�����ݣ����Ҽ�صĽ�Ŀ��������0��������Ҫmonitor��ء�
 return 1������Ҫ���
 return 0��������Ҫ���
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
#endif

void column_refresh_flag_set(int flag)
{
	s_column_refresh = flag;
}

void interface_refresh_flag_set(int flag)
{
	s_interface_refresh = flag;
}

void preview_refresh_flag_set(int flag)
{
	s_preview_refresh = flag;
}

static int push_regist_init()
{
//	push_file_register("pushroot/initialize/Initialize.xml");
	
	int push_flags[16];
	unsigned int push_flags_cnt = 0;
	push_flags[push_flags_cnt] = GUIDELIST_XML;
	push_flags_cnt++;
	push_flags[push_flags_cnt] = COMMANDS_XML;
	push_flags_cnt++;
	push_flags[push_flags_cnt] = MESSAGE_XML;
	push_flags_cnt++;
	push_flags[push_flags_cnt] = PRODUCTDESC_XML;
	push_flags_cnt++;
	push_flags[push_flags_cnt] = SERVICE_XML;
	push_flags_cnt++;
	info_xml_refresh(1,push_flags,push_flags_cnt);
	
	push_recv_manage_refresh();
	
	return 0;
}

void maintenance_thread_awake()
{
	pthread_mutex_lock(&mtx_maintenance);
	pthread_cond_signal(&cond_maintenance);
	pthread_mutex_unlock(&mtx_maintenance);
}

void *maintenance_thread()
{
	struct timeval now;
	struct timespec outtime;
	int retcode = 0;
	//char sqlite_cmd[256];
	int monitor_interval = 1;
	unsigned int loop_cnt = 0;
	int smart_card_insert_flag = -1;
	
	time_t s_pin_sec;	// ��¼����ʱ�䣨�룩
	time_t now_sec;
	struct tm now_tm;
	char sqlite_cmd[256];
	int ret = 0;
	char SmartCardSn[128];
	
	time(&s_pin_sec);
	
	while (1)
	{
		pthread_mutex_lock(&mtx_maintenance);
		
		gettimeofday(&now, NULL);
		outtime.tv_sec = now.tv_sec + monitor_interval;
		outtime.tv_nsec = now.tv_usec;
		retcode = pthread_cond_timedwait(&cond_maintenance, &mtx_maintenance, &outtime);
		
		pthread_mutex_unlock(&mtx_maintenance);
		
#if 1
		if(ETIMEDOUT!=retcode){
			DEBUG("maintenance thread is awaked by external signal\n");
		}
#endif

#ifdef DRM_TEST
#else		
		// ÿ��12��Сʱ����tdt pid����ʱ��ͬ��������ֻ�ǽ�����monitor�����Ƶѭ����
		loop_cnt ++;
		if(loop_cnt>(43200)){
			time_t timep;
			struct tm *p_tm;
			timep = time(NULL);
			p_tm = localtime(&timep); /*��ȡ����ʱ��ʱ��*/ 
			DEBUG("it's time to awake tdt time sync, %4d-%2d-%2d %2d:%2d:%2d\n", (p_tm->tm_year+1900), (p_tm->tm_mon+1), p_tm->tm_mday, p_tm->tm_hour, p_tm->tm_min, p_tm->tm_sec);
			tdt_time_sync_awake();
			loop_cnt = 0;
		}
#endif
		
		// ����Ŀ�ͽ����Ʒ�����ı�ʱ������ֱ����parse_xml()������ͨ��JNI��UI����notify����������׵���Launcher������
		if(s_column_refresh>0){
			DEBUG("column refresh\n");
			s_column_refresh ++;
			if(s_column_refresh>2){
				msg_send2_UI(STATUS_COLUMN_REFRESH, NULL, 0);
				s_column_refresh = 0;
			}
		}
		if(s_interface_refresh>0){
			DEBUG("interface refresh\n");
			s_interface_refresh ++;
			if(s_interface_refresh>2){
				msg_send2_UI(STATUS_INTERFACE_REFRESH, NULL, 0);
				s_interface_refresh = 0;
			}
		}
		if(s_preview_refresh>0){
			DEBUG("preview refresh\n");
			s_preview_refresh ++;
			if(s_preview_refresh>2){
				msg_send2_UI(STATUS_PREVIEW_REFRESH, NULL, 0);
				s_preview_refresh = 0;
			}
		}
		
		filter_timeout_process();
		
		if(smart_card_insert_flag != smart_card_insert_flag_get()){
			smart_card_insert_flag = smart_card_insert_flag_get();
			if(smart_card_insert_flag>0){
				// ���Է��ֲ��1s��Ӳ�̴�ʱ��δ׼�����
				DEBUG("smart card insert, wait 1s for disc ready\n");
				sleep(1);
				
				memset(SmartCardSn,0,sizeof(SmartCardSn));
				ret = CDCASTB_GetCardSN(SmartCardSn);
				if(CDCA_RC_OK==ret)
				{
					DEBUG("read smartcard sn OK: %s\n", SmartCardSn);
					
					if(1==smartcard_entitleinfo_refresh())
						pushinfo_reset();
					smart_card_insert_flag_set(0);
					
					DEBUG("CA_LIB Ver: 0X%lX\n", CDCASTB_GetVer());
					
					send_sc_notify(1,DRM_SC_INSERT_OK, NULL, 0);
				}
				else{
					if(smart_card_insert_flag<=3){
						DEBUG("read card sn failed, will retry %dth times\n",smart_card_insert_flag+1);
						drm_sc_insert();
					}
					else{
						DEBUG("reset smartcard finally failed after try %d times\n",smart_card_insert_flag);
						smart_card_insert_flag_set(0);
						
						send_sc_notify(1,DRM_SC_INSERT_FAILED, NULL, 0);
					}
				}
			}
		}
		
		// Ӳ�̹��غ���ܿ�ʼ����Ƿ���Ҫĸ�̳�ʼ��
		if(0==s_motherdisc_init_flag && 1==pushdir_usable() && 1==network_init_status()){
			DEBUG("do mother disc process\n");
			motherdisc_process();
			
			s_motherdisc_init_flag = 1;
		}
		
		// ĸ�̳�ʼ������һ������push��ʼ��֮ǰ��ȷ����ĸ�̳�ʼ���������ݿ�����в�����
		if(0==s_push_regist_inited && 1==pushdir_usable()){
			s_push_regist_inited = 1;
			
#if 0
2013-06-29
push_decoder_thread������������˳��ִ��ota�������̣����mid_push_init��Ҫ�����ʼ����ֻ��push_regist_initҪ�ȵ�Ӳ�̼������
			if(-1==mid_push_init(PUSH_CONF)){
				DEBUG("push model init with \"%s\" failed\n", PUSH_CONF);
			}
#endif

			push_regist_init();
		}
		
#ifdef DRM_TEST
#else
		if(1==user_idle_status_get()){
			time(&now_sec);
			
//			DEBUG("in user idle status,now_sec: %ld, s_pin_sec=%ld, reboot_timestamp_get()=%d\n", now_sec, s_pin_sec, reboot_timestamp_get());
			
			// 1����������6��Сʱ(21600)��2�����ϴ�������ʱ������7200������Сʱ������Ч
			if((now_sec-s_pin_sec>21600) && (now_sec-reboot_timestamp_get())>7200){
				localtime_r(&now_sec, &now_tm);
				
				// ����������Ҫ��45�ֺ�����֮�䱣�ֿ���״̬��Ԥ��15��������ʱ�䣬����ʱ��ÿСʱΪ0�ֵ�30��
				if(onehour_before_pushend_get()==now_tm.tm_hour && now_tm.tm_min>=0 && now_tm.tm_min<=30){
					DEBUG("in system reboot window(0<=tm_min<=30) at %d %02d %02d - %02d:%02d:%02d\n", 
						(1900+now_tm.tm_year),(1+now_tm.tm_mon),now_tm.tm_mday,now_tm.tm_hour,now_tm.tm_min,now_tm.tm_sec);
					
					now_sec += 1;
					reboot_timestamp_set(now_sec);
					
					sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Global(Name,Value,Param) VALUES('%q','%ld','');",
						GLB_NAME_REBOOT_TIMESTAMP,now_sec);
					if(0==sqlite_execute(sqlite_cmd)){
						s_decoder_running = 0;
						DEBUG("set s_decoder_running=%d to stop push write\n", s_decoder_running);
						sleep(1);
						
						push_destroy();
						
						msg_send2_UI(SYSTEM_REBOOT, NULL, 0);
					}
					else
						DEBUG("replace reboot timestamp failed, don't reboot\n");
				}
			}
		}
#endif

	}
	DEBUG("exit from push monitor thread\n");
	
	return NULL;
}

void *push_xml_parse_thread()
{
	int i = 0;
	
	s_xmlparse_running = 1;
	int has_parse_xml = 0;
	while (1==s_xmlparse_running)
	{
		pthread_mutex_lock(&mtx_xml);
		//pthread_cond_wait(&cond_xml,&mtx_xml); //wait
		//DEBUG("awaked and parse xml\n");
		
		has_parse_xml = 0;
		if(1==s_xmlparse_running){
			for(i=0; i<XML_NUM; i++){
				//DEBUG("xml queue[%d]: %s\n", i, s_push_xml[i].uri);
				if(strlen(s_push_xml[i].uri)>0){
					DEBUG("will parse[%d] %s\n", i, s_push_xml[i].uri);
					parse_xml(s_push_xml[i].uri, s_push_xml[i].flag, s_push_xml[i].arg_ext);
					
					memset(s_push_xml[i].uri, 0, sizeof(s_push_xml[i].uri));
					s_push_xml[i].flag = PUSH_XML_FLAG_UNDEFINED;
					memset(s_push_xml[i].arg_ext, 0, sizeof(s_push_xml[i].arg_ext));
					DEBUG("finish parse[%d] %s\n", i, s_push_xml[i].uri);
					has_parse_xml = 1;
				}
			}
		}
		pthread_mutex_unlock(&mtx_xml);
		
		if(1==has_parse_xml)
			sleep(2);
		else
			sleep(3);
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

int mid_push_cb(const char *path, int flag)
{
	int ret = 0;
	char xml_uri[512];
	int i = 0;
	
	snprintf(xml_uri,sizeof(xml_uri),"%s",path);
	
	if(PRODUCTDESC_XML==flag){
		DEBUG("have receive %s, need check smartcard entitleinfo\n", xml_uri);
		if(1==smartcard_entitleinfo_refresh()){
			DEBUG("check smartcard entitleinfo refresh when receiving ProductDesc.xml\n");
			pushinfo_reset();
		}
	}
	
	if(PUBLICATION_DIR==flag){
		pthread_mutex_lock(&mtx_push_monitor);
		for(i=0; i<PROGS_NUM; i++)
		{
			if(-1==prog_is_valid(&s_prgs[i]))
				continue;
			
			if(0==strcmp(s_prgs[i].uri, path)){
				if(0==s_prgs[i].parsed){
					snprintf(xml_uri,sizeof(xml_uri),"%s",s_prgs[i].descURI);
					
					s_prgs[i].cur = s_prgs[i].total;
					s_prgs[i].parsed = 1;
					break;
				}
				else
					DEBUG("fuck, this prog has parsed=%d\n", s_prgs[i].parsed);
			}
		}
		pthread_mutex_unlock(&mtx_push_monitor);
		
		if(PROGS_NUM==i){
			DEBUG("select no desc uri for %s, filled with default uri\n", path);
			snprintf(xml_uri,sizeof(xml_uri),"%s/info/desc/Publications.xml",path);
		}
		
		DEBUG("desc uri(%s) for prog(%s)\n", xml_uri, path);
	}
	
	if(	PUBLICATION_DIR<=flag && flag<PUSH_XML_FLAG_MAXLINE){
		if(0==strtailcmp(xml_uri, ".xml", 0))
		{
			DEBUG("xml_uri: %s\n", xml_uri);
			pthread_mutex_lock(&mtx_xml);
			
			int i = 0;
			for(i=0; i<XML_NUM; i++){
				if(0==strlen(s_push_xml[i].uri)){
					DEBUG("add to index %d: xml_uri: %s\n", i,xml_uri);
					snprintf(s_push_xml[i].uri, sizeof(s_push_xml[i].uri),"%s", xml_uri);
					s_push_xml[i].flag = flag;
					snprintf(s_push_xml[i].arg_ext, sizeof(s_push_xml[i].arg_ext),"%s", path);
					break;
				}
			}
			if(XML_NUM<=i){
				DEBUG("xml name space is full\n");
				ret = -1;
			}
			else{
				//pthread_cond_signal(&cond_xml); //send sianal
				ret = 0;
			}
				
			pthread_mutex_unlock(&mtx_xml);
			DEBUG("xml_uri in queue: %s ok\n", xml_uri);
		}
		else{
			DEBUG("%s is not a xml\n", xml_uri);
			ret = -1;
		}
	}
	else{
		DEBUG("file(%d) is ignore\n", flag);
		ret = -1;
	}
	
	return ret;
}

// ������Ѿ�������ϵĽ�Ŀ�ٴ�ע�ᣬ�������callback
void callback(const char *path, long long size, int flag)
{
#ifdef DRM_TEST
	DEBUG("\n\n\n===== not parse xml because of drm test  ======path:%s, size:%lld, flag:%d=============\n\n\n", path, size, flag);
#else
	DEBUG("\n\n\n===========path:%s, size:%lld, flag:%d=============\n\n\n", path, size, flag);
	
	/* �����漰�����������ݿ���������ﲻֱ�ӵ���parse_xml�����ⵢ��push���������Ч�� */
	// settings/allpid/allpid.xml
	mid_push_cb(path, flag);
#endif
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

int maintenance_thread_init()
{
	pthread_t tidMaintenance;
	pthread_create(&tidMaintenance, NULL, maintenance_thread, NULL);
	pthread_detach(tidMaintenance);
	
	return 0;
}

int mid_push_init(char *push_conf)
{
	int i = 0;
	for(i=0;i<XML_NUM;i++){
		memset(s_push_xml[i].uri, 0, sizeof(s_push_xml[i].uri));
		s_push_xml[i].flag = PUSH_XML_FLAG_UNDEFINED;
		memset(s_push_xml[i].arg_ext, 0, sizeof(s_push_xml[i].arg_ext));
	}
	
	for(i=0; i<PROGS_NUM; i++){
		memset(&s_prgs[i], 0, sizeof(PROG_S));
	}
	
	/*
	* ��ʼ��PUSH��
	 */
	if (push_init(push_conf) != 0)
	{
		DEBUG("Init push lib failed with %s!\n", push_conf);
		return -1;
	}
	else
		DEBUG("Init push lib success with %s!\n", push_conf);
	
	/*
	ȷ��������������һ��ɨ����ᣬ���׼ȷ�����ؽ��ȡ�
	*/
	s_push_has_data = 7;
	
	push_set_notice_callback(callback);
	
	//�������ݽ����߳�
	pthread_create(&tidDecodeData, NULL, push_decoder_thread, NULL);
	//pthread_detach(tidDecodeData);
	
	//����xml�����߳�
	pthread_t tidxmlparse;
	pthread_create(&tidxmlparse, NULL, push_xml_parse_thread, NULL);
	pthread_detach(tidxmlparse);
	
	return 0;
}

int mid_push_uninit()
{
	pthread_mutex_lock(&mtx_xml);
	s_xmlparse_running = 0;
	//pthread_cond_signal(&cond_xml);
	pthread_mutex_unlock(&mtx_xml);
	
	pthread_mutex_lock(&mtx_maintenance);
	pthread_cond_signal(&cond_maintenance);
	pthread_mutex_unlock(&mtx_maintenance);
	
	s_push_has_data = 0;
	
	push_destroy();
	
	s_decoder_running = 0;
	pthread_join(tidDecodeData, NULL);
	push_decoder_buf_uninit();
	
	return 0;
}

/*
 ��push��ͣ��Ŀǰ������Ӳ��ǰ���á�
*/
int push_pause()
{
#if 0
	push_destroy();
#endif

	return 0;
}

/*
 ��push�ָ���Ŀǰ������Ӳ�̺���á�
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
ע���Ŀ
*/
static int mid_push_regist(PROG_S *prog)
{
	if(NULL==prog){
		DEBUG("arg is invalid\n");
		return -1;
	}
	if(-1==prog_is_valid(prog)){
		DEBUG("INVALID PROGRAM to regist monitor, uri: %s, total: %lld\n", prog->uri, prog->total);
		return -1;
	}
	
	/*
	* Notice:��Ŀ·����һ�����·������Ҫ��'/'��ͷ��
	* ����Ŀ���и�����·����"/vedios/pushvod/1944"����ȥ���ʼ��'/'��
	* ��"vedios/pushvod/1944"����ע�ᡣ
	*
	* �˴�PRG����ṹ���ǳ���ʾ�����㶨��ģ���һ�����������ĳ�����
	*/
	int i = 0, ret = -1;
	
/*
 ���жϴ�uri�Ƿ��Ѿ���monitor�ڣ���ֹ�ظ�������ͬ��Ŀ¼��ص��¼�����鱬��
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
			snprintf(s_prgs[i].publication_id, sizeof(s_prgs[i].publication_id), "%s", prog->publication_id);
			snprintf(s_prgs[i].product_id, sizeof(s_prgs[i].product_id), "%s", prog->product_id);
			
			DEBUG("regist to push[%d]:%s(%s) in %s %s %s %s %lld\n",
					i,
					s_prgs[i].id,
					s_prgs[i].publication_id,
					s_prgs[i].product_id,
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
			snprintf(s_prgs[i].publication_id, sizeof(s_prgs[i].publication_id), "%s", prog->publication_id);
			snprintf(s_prgs[i].product_id, sizeof(s_prgs[i].product_id), "%s", prog->product_id);
			
/*
 �Ѿ��������Ľ�Ŀ������ע�ᵽpush���У�ֻ��ҪUI����ʾ���ɡ�
*/
			if((s_prgs[i].cur)<(s_prgs[i].total)){				
				push_dir_register(s_prgs[i].uri, s_prgs[i].total, 0);
			}
			else{
				PRINTF("prog [%s]%s is download finish, no need to monitor\n", s_prgs[i].id,s_prgs[i].uri);
			}
			s_push_monitor_active++;
			
			PRINTF("regist to push[%d]:%s(%s) in %s %s %s %lld\n",
					i,
					s_prgs[i].id,
					s_prgs[i].publication_id,
					s_prgs[i].product_id,
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

#if 0
/*
 ��ע���ؽ�Ŀ��
*/
static int mid_push_unregist(PROG_S *prog)
{
	/*
	* Notice:��Ŀ·����һ�����·������Ҫ��'/'��ͷ��
	* ����Ŀ���и�����·����"/vedios/pushvod/1944"����ȥ���ʼ��'/'��
	* ��"vedios/pushvod/1944"����ע�ᡣ
	*
	* �˴�PRG����ṹ���ǳ���ʾ�����㶨��ģ���һ�����������ĳ�����
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
#endif

/*
����Ŀ������
*/
static int prog_name_fill()
{
	/*
	* Notice:��Ŀ·����һ�����·������Ҫ��'/'��ͷ��
	* ����Ŀ���и�����·����"/vedios/pushvod/1944"����ȥ���ʼ��'/'��
	* ��"vedios/pushvod/1944"����ע�ᡣ
	*
	* �˴�PRG����ṹ���ǳ���ʾ�����㶨��ģ���һ�����������ĳ�����
	*/
	int i;
	char sqlite_cmd[256];
	for(i=0; i<PROGS_NUM; i++)
	{
		if(1==prog_is_valid(&s_prgs[i]) && 0==strlen(s_prgs[i].caption)){
			memset(s_prgs[i].caption, 0, sizeof(s_prgs[i].caption));
#if 0
			sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"SELECT StrValue FROM ResStr WHERE ServiceID='%q' AND ObjectName='ProductDesc' AND EntityID='%q' AND StrLang='%q' AND (StrName='ProductDescName' OR StrName='SProductName' OR StrName='ColumnName');", 
				serviceID_get(),s_prgs[i].id,language_get());
#else
			sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"SELECT StrValue FROM ResStr WHERE ObjectName='ProductDesc' AND EntityID='%q' AND StrLang='%q' AND (StrName='ProductDescName' OR StrName='SProductName' OR StrName='ColumnName');", 
				s_prgs[i].id,language_get());
#endif
			
			if(0==str_sqlite_read(s_prgs[i].caption,sizeof(s_prgs[i].caption),sqlite_cmd)){
				if(0==strlen(s_prgs[i].caption)){
					DEBUG("length of caption is 0, filled with prog id: %s\n",s_prgs[i].id);
					snprintf(s_prgs[i].caption, sizeof(s_prgs[i].caption), "%s", s_prgs[i].id);
				}
//				else
//					DEBUG("read prog caption success: %s\n", s_prgs[i].caption);
			}
			else{
				DEBUG("read prog caption failed, filled with prog id: %s\n",s_prgs[i].id);
				snprintf(s_prgs[i].caption, sizeof(s_prgs[i].caption), "%s", s_prgs[i].id);
			}
		}
	}
	
	return 0;
}

int delete_publication_from_monitor(char *PublicationID, char *ProductID)
{
	int i = 0;
	int ret = 0;
	
	char sqlite_cmd[1024];
	
	if(NULL!=PublicationID)
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"DELETE FROM ProductDesc WHERE ID='%q';",PublicationID);
	else
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"DELETE FROM ProductDesc WHERE productID='%q';",ProductID);
		
	sqlite_execute(sqlite_cmd);
	
	for(i=0; i<PROGS_NUM; i++)
	{
		if(1==prog_is_valid(&s_prgs[i])){
			if(		(NULL!=PublicationID && 0==strcmp(PublicationID,s_prgs[i].publication_id))
				||	(NULL!=ProductID && 0==strcmp(ProductID,s_prgs[i].product_id))	){
/*
 ������ϵĽ�Ŀpushϵͳ���Զ���ע�ᣬ������������ʱ������Щ���ղ���ϵĽ�Ŀ���з�ע�Ტɾ������
 ����ע��ʱ�ļ����رգ����Խ���ɾ��
*/
			
				PRINTF("[%s]%s %s should be deleted, unregist and clean it\n", s_prgs[i].id,s_prgs[i].publication_id,s_prgs[i].uri);
				ret = push_dir_unregister(s_prgs[i].uri);
				if(0==ret){
					ret = push_dir_remove(s_prgs[i].uri);
					if(0==ret){
						usleep(100000);
					}
					else if(-1==ret)
						PRINTF("push remove failed: %s, no such uri\n", s_prgs[i].uri);
					else
						PRINTF("push remove failed: %s, some other err(%d)\n", s_prgs[i].uri, ret);
				}
				else if(-1==ret)
					PRINTF("push unregist failed: %s, no registed uri\n", s_prgs[i].uri);
				else
					PRINTF("push unregist failed: %s, some other err(%d)\n", s_prgs[i].uri, ret);
				
				memset(s_prgs[i].id, 0, sizeof(s_prgs[i].id));
				memset(s_prgs[i].uri, 0, sizeof(s_prgs[i].uri));
				memset(s_prgs[i].descURI, 0, sizeof(s_prgs[i].descURI));
				memset(s_prgs[i].caption, 0, sizeof(s_prgs[i].caption));
				memset(s_prgs[i].deadline, 0, sizeof(s_prgs[i].deadline));
				s_prgs[i].type = 0;
				s_prgs[i].cur = 0LL;
				s_prgs[i].total = 0LL;
				s_prgs[i].parsed = 0;
				memset(s_prgs[i].publication_id, 0, sizeof(s_prgs[i].publication_id));
				memset(s_prgs[i].product_id, 0, sizeof(s_prgs[i].product_id));
				
				DEBUG("delete Publication %s from monitor and ProductDesc table finish\n", s_prgs[i].publication_id);
				s_dvbpush_info_refresh_flag = 1;
			}
			else
				DEBUG("%s is not the publication(%s) which you want delete\n", s_prgs[i].publication_id,NULL==PublicationID?ProductID:PublicationID);
		}
	}
	
	return 0;
}


int prog_monitor_reset(void)
{
	int i = 0;
	int ret = 0;
	
	for(i=0; i<PROGS_NUM; i++)
	{
		if(1==prog_is_valid(&s_prgs[i])){
/*
 ������ϵĽ�Ŀpushϵͳ���Զ���ע�ᣬ������������ʱ������Щ���ղ���ϵĽ�Ŀ���з�ע�Ტɾ������
 ����ע��ʱ�ļ����رգ����Խ���ɾ��
*/
			if(0==s_prgs[i].parsed){
				PRINTF("[%s] %s is download stop but not complete, unregist and clean it\n", s_prgs[i].id,s_prgs[i].uri);
				ret = push_dir_unregister(s_prgs[i].uri);
				if(0==ret){
					ret = push_dir_remove(s_prgs[i].uri);
					if(0==ret){
						usleep(100000);
					}
					else if(-1==ret)
						PRINTF("push remove failed: %s, no such uri\n", s_prgs[i].uri);
					else
						PRINTF("push remove failed: %s, some other err(%d)\n", s_prgs[i].uri, ret);
				}
				else if(-1==ret)
					PRINTF("push unregist failed: %s, no registed uri\n", s_prgs[i].uri);
				else
					PRINTF("push unregist failed: %s, some other err(%d)\n", s_prgs[i].uri, ret);
				
				
				int wanting_percent = (100*(s_prgs[i].total-s_prgs[i].cur))/s_prgs[i].total;
				
				// ����1G�ĳ�Ʒ�Ѿ�����95%������
				if(RECEIVETYPE_PUBLICATION==s_prgs[i].type && wanting_percent<=5 && s_prgs[i].total>1073741824LL)
				{
					DEBUG("cur=%lld, total=%lld, wanting %d%%\n", s_prgs[i].cur, s_prgs[i].total, wanting_percent);
				}
			}
			
			DEBUG("unregist from push[%d]:%s(%s) in %s %s %s %lld\n",
					i,
					s_prgs[i].id,
					s_prgs[i].publication_id,
					s_prgs[i].product_id,
					s_prgs[i].uri,
					s_prgs[i].descURI,
					s_prgs[i].total);
			
			memset(s_prgs[i].id, 0, sizeof(s_prgs[i].id));
			memset(s_prgs[i].uri, 0, sizeof(s_prgs[i].uri));
			memset(s_prgs[i].descURI, 0, sizeof(s_prgs[i].descURI));
			memset(s_prgs[i].caption, 0, sizeof(s_prgs[i].caption));
			memset(s_prgs[i].deadline, 0, sizeof(s_prgs[i].deadline));
			s_prgs[i].type = 0;
			s_prgs[i].cur = 0LL;
			s_prgs[i].total = 0LL;
			s_prgs[i].parsed = 0;
			memset(s_prgs[i].publication_id, 0, sizeof(s_prgs[i].publication_id));
			memset(s_prgs[i].product_id, 0, sizeof(s_prgs[i].product_id));
		}
	}
	
	s_push_monitor_active = 0;
	
	return 0;
}

/*
�ص�����ʱ��receiverЯ���Ƿ��н���ע��ı�ǣ�1��ʾ��ע�ᣬ0��ʾ��ע�ᡣ
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
	RECEIVESTATUS_E recv_stat = RECEIVESTATUS_REJECT;
	long long totalsize = 0LL;
	
	*((int *)receiver) = 0;
	
	for(i=1;i<row+1;i++)
	{
#if 0
		/*
		���ڳ�Ʒ������û�ѡ�񲻽��գ���һ�������գ�����Ҫ������ϸ���ж�
		���򣬸���ҵ������������ж�
		*/
		if(RECEIVETYPE_PUBLICATION==atoi(result[i*column+2]) && 0==guidelist_select_status((const char *)(result[i*column+1]))){
			recv_stat = RECEIVESTATUS_REJECT;
			DEBUG("this prog(%s) is reject by user in guidelist\n", result[i*column+3]);
		}
		else

			// 2013-4-7 11:14
			// ���û���Ԥ�浥�е�ѡ����ˣ����ڽ���ProductDescʱ����
#endif

			recv_stat = RECEIVESTATUS_WAITING;
		
		sscanf(result[i*column+5],"%lld", &totalsize);
		
		if(RECEIVESTATUS_REJECT==recv_stat){
			// �����û��ܾ����յĽ�Ŀ����ע�ἴ�ɣ�������ʽ�ܾ�			
			//mid_push_reject(result[i*column+3],totalsize);
		}
		else{
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
			snprintf(cur_prog.publication_id,sizeof(cur_prog.publication_id),"%s",result[i*column+1]);
			snprintf(cur_prog.product_id,sizeof(cur_prog.product_id),"%s",result[i*column+11]);

/*
 �Ѿ��������Ľ�Ŀ������ע�ᵽpush���У�ֻ��ҪUI����ʽ���ɡ�
*/
			if(1==cur_prog.parsed){
				PRINTF("[%s]%s is parsed already, make cur as total directly\n", cur_prog.id,cur_prog.uri);
				cur_prog.cur = cur_prog.total;
			}
			else{
				// δ�������Ľ�Ŀ������Ҫ���½��գ�Ϊ��ȷ����Щ����IO����Ľ�ĿĿ¼Ҳ�����¿�ʼ����Ҫ����Ŀ¼�Ƿ�������
				// �������Ľ�ĿĿ¼��IO����������
				char prog_total_uri[1024];
				snprintf(prog_total_uri,sizeof(prog_total_uri),"%s/%s",push_dir_get(),cur_prog.uri);
				dir_stat_ensure(prog_total_uri);
			}
			
			mid_push_regist(&cur_prog);
			
			/*
			�ص�����ʱ��receiverЯ���Ƿ��н���ע��ı�ǣ�1��ʾ��ע�ᣬ0��ʾ��ע�ᡣ
			*/
			*((int *)receiver) = 1;
		}
	}
	
	return 0;
}


unsigned long long should_clean_M_get()
{
	return s_should_clean_M;
}

/*
 return 0: disc cleaning finish
 		-1: can not clean, some err
 		1: no need to clean
*/
int disk_space_check()
{
	unsigned long long tt_size = 0LL;
	unsigned long long free_size = 0LL;
	int ret = -1;
	
	if(-1==disk_usable_check(push_dir_get(),&tt_size,&free_size)){
		DEBUG("HardDisc %s disable\n",push_dir_get());
		ret = -1;
	}
	else{
		DEBUG("HardDisc %s enable, total_size: %llu, free_size: %llu\n",push_dir_get(),tt_size,free_size);
		unsigned long long free_size_M = (free_size >> 20);
		
		DEBUG("has free size %llu M, compared with level %llu M\n", free_size_M,(HDFOREWARNING_M_DFT+recv_totalsize_sum_M_get()));
		
		if(free_size_M<=(HDFOREWARNING_M_DFT+recv_totalsize_sum_M_get())){
			s_should_clean_M = HDFOREWARNING_M_DFT + recv_totalsize_sum_M_get() - free_size_M;
			DEBUG("should cleaning hd %llu MiB...\n",s_should_clean_M);
			
			//clear_wild_prog();
			
			disk_manage(NULL,NULL);
			ret = 0;
		}
		else{
			DEBUG("no need to clean hd\n");
			ret = 1;
		}
	}
	
	return ret;
}

/*
2013-01-23
	�򻯲������²������·�������ɵĲ����������򲻸��²�������������PushStartTime��PushEndTime
*/
int push_recv_manage_refresh()
{
	int ret = -1;
	char sqlite_cmd[256+128];
	int (*sqlite_callback)(char **, int, int, void *, unsigned int) = push_recv_manage_cb;
	
	pthread_mutex_lock(&mtx_push_monitor);
	
	s_dvbpush_info_refresh_flag = 1;
	
	int flag_carrier = 0;
	
/*
 ����һ��Publication���ܴ����ڶ��service�У������Ҫȫ��ȡ�����ڻص��б�����Щ��Ҫ�ܾ���publication�Ƿ�ǡ��Ҳ����Ҫ����֮�С�
 ���Զ�FreshFlag���ж��ƶ����ص��н��У�������������FreshFlag֮��
*/
	sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"SELECT ProductDescID,ID,ReceiveType,URI,DescURI,TotalSize,PushStartTime,PushEndTime,ReceiveStatus,FreshFlag,Parsed,productID FROM ProductDesc;");
	
	ret = sqlite_read(sqlite_cmd, (void *)(&flag_carrier), sizeof(flag_carrier), sqlite_callback);
/*
�ص�����ʱ��flag_carrierЯ���Ƿ��н���ע��ı�ǣ�1��ʾ��ע�ᣬ0��ʾ��ע�ᡣ
*/	
	PRINTF("ret: %d, flag_carrier: %d\n", ret,flag_carrier);
	if(ret>0 && flag_carrier>0){
		prog_name_fill();
	}
	
	pthread_mutex_unlock(&mtx_push_monitor);

	return ret;
}

static int info_xml_refresh_cb(char **result, int row, int column, void *receiver, unsigned int receiver_size)
{
	DEBUG("sqlite callback, row=%d, column=%d, receiver addr=%p, receive_size=%u\n", row, column, receiver,receiver_size);
	if(row<1){
		DEBUG("no record in table, return\n");
		return 0;
	}
//ProductDescID,ID,ReceiveType,URI,DescURI,TotalSize,PushStartTime,PushEndTime,ReceiveStatus,FreshFlag,Parsed
	int i = 0;
	int regist_flag = *((int *)receiver);
	int ret = 0;
	char direct_uri[512];
	
	for(i=1;i<row+1;i++)
	{
		if(0==regist_flag){
			ret = push_file_unregister(result[i*column+1]);
			
			snprintf(direct_uri,sizeof(direct_uri),"%s/%s", push_dir_get(),result[i*column+1]);
			remove_force(direct_uri);
		}
		else{
			ret = push_file_register(result[i*column+1]);
		}
		
		PRINTF("%s(%s) return with %d\n", 0==regist_flag?"unregist":"regist",result[i*column+1],ret);
	}
	
	return 0;
}

/*
 ϵͳ��ʼ����Initialize.xml����ʱ��Ҫˢ��ע�ᣬ��Initialize.xml��pushϵͳ�Լ�ע�ᡣ
 regist_flag
 	0 means unregist
 	1 means resgist
*/
int info_xml_refresh(int regist_flag, int push_flags[], unsigned int push_flags_cnt)
{
	if(0==push_flags_cnt){
		DEBUG("invalid push_flags_cnt\n");
		return -1;
	}
	
	unsigned int i = 0;
	int ret = -1;
	char sqlite_cmd[1024];
	int (*sqlite_callback)(char **, int, int, void *, unsigned int) = info_xml_refresh_cb;
	
	int resgist_action = regist_flag;
	
	DEBUG("regist action: %d\n", regist_flag);
	sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"SELECT PushFlag,URI FROM Initialize WHERE");
	for(i=0;i<push_flags_cnt;i++){
		if(i>0)
			sqlite3_snprintf(sizeof(sqlite_cmd)-strlen(sqlite_cmd),sqlite_cmd+strlen(sqlite_cmd)," OR");
		sqlite3_snprintf(sizeof(sqlite_cmd)-strlen(sqlite_cmd),sqlite_cmd+strlen(sqlite_cmd)," PushFlag='%d'", push_flags[i]);
	}
	sqlite3_snprintf(sizeof(sqlite_cmd)-strlen(sqlite_cmd),sqlite_cmd+strlen(sqlite_cmd),";");
	
	ret = sqlite_read(sqlite_cmd, (void *)(&resgist_action), sizeof(resgist_action), sqlite_callback);

	return ret;
}

/*
 ����Service.xml�а���ServiceID��Ϣ�������ѭ������˳��Initialize.xml --> Service.xml --> ������info�ļ�
 2013-3-18 15:55 Service.xml���ã����еĲ�Ʒ��Ȩ���Ǵ����ܿ��в�ѯ��
*/
int info_xml_regist()
{
	int push_flags[16];
	unsigned int push_flags_cnt = 0;
	
	push_flags_cnt = 0;
	push_flags[push_flags_cnt] = SERVICE_XML;
	push_flags_cnt ++;
	push_flags[push_flags_cnt] = GUIDELIST_XML;
	push_flags_cnt ++;
	push_flags[push_flags_cnt] = PRODUCTDESC_XML;
	push_flags_cnt ++;
	push_flags[push_flags_cnt] = COMMANDS_XML;
	push_flags_cnt ++;
	push_flags[push_flags_cnt] = MESSAGE_XML;
	push_flags_cnt ++;
	
	return info_xml_refresh(1,push_flags,push_flags_cnt);
}

