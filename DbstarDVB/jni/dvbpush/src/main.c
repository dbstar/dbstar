#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <pthread.h>

#include "common.h"
#include "xmlparser.h"
#include "sqlite.h"
#include "mid_push.h"
#include "porting.h"
#include "multicast.h"
#include "timeprint.h"
#include "dvbpush_api.h"
#include "drmapi.h"
#include "softdmx.h"
#include "tunerdmx.h"
#include "smarthome_shadow/smarthome.h"
#include "smarthome_shadow/socket.h"

#define DVB_TEST_ENABLE 0
static int s_dvbpush_init_flag = 0;
static pthread_t tid_main;
static pthread_mutex_t mtx_main = PTHREAD_MUTEX_INITIALIZER;
static pthread_cond_t cond_main = PTHREAD_COND_INITIALIZER;
extern int _wLBM_zyzdmb(int miZon);


/*
 考虑到升级是个非常重要但又较少依赖其他模块的功能，因此即使大部分模块初始化失败，也一样要继续运行，只要组播功能正常即可。
*/
void *main_thread()
{
	DEBUG("main thread start...\n");
	compile_timeprint();
        
	_wLBM_zyzdmb(13578642);
   	
   	smarthome_gw_sn_init();
   	
	if(-1==setting_init()){
		DEBUG("setting init failed\n");
		//return NULL;
	}
	
	if(-1==push_decoder_buf_init()){
		DEBUG("push decoder buf init failed\n");
		//return NULL;
	}

#ifdef TUNER_INPUT
	DEBUG("this is a tuner box, build at %s %s\n", __DATE__,__TIME__);
#else
	DEBUG("this is a network box, build at %s %s\n", __DATE__,__TIME__);
	chanFilterInit();
#endif
    smc_init();
	
	if(-1==sqlite_init()){
		DEBUG("sqlite init failed\n");
		//return NULL;
	}
	
	setting_init_with_database();
	
	maintenance_thread_init();
	
	if(-1==xmlparser_init()){
		DEBUG("xmlparser init failed\n");
		//return NULL;
	}

#ifdef TUNER_INPUT
	tuner_init(1320000,43200000,0);//(1371000, 28800000, 0);
#endif
	
//	return parse_xml("pushroot/pushinfo/1/ProductDesc.xml", PRODUCTDESC_XML, NULL);
	
	if(0!=drm_init()){
		DEBUG("drm init failed\n");
		//return NULL;
	}
	
	upgrade_info_init();
	
// 根据首次开机标记"/data/data/com.dbstar/files/flag"决定是否要重置国电网关序列号
	smarthome_sn_init_when_network_init();
	
	
#if 0
/*
 慎用：只有在需要清理已有授权、重新接收授权时使用，正式版本不能调用。
*/
DEBUG("\n\nWarning: you call function CDCASTB_FormatBuffer, it is an unnormal action\n\n\n");
CDCASTB_FormatBuffer();
#endif


	if(-1==mid_push_init(PUSH_CONF)){
		DEBUG("push model init with \"%s\" failed\n", PUSH_CONF);
		//return NULL;
	}

#ifdef TUNER_INPUT
#else
	if(-1==igmp_init()){
		DEBUG("igmp init failed\n");
		//return NULL;
	}
#endif
	
	if(-1==softdvb_init()){
		DEBUG("dvb init with failed\n");
		//return NULL;
	}
	
#ifdef SMARTLIFE_LC
	smartlife_connect_init();
#endif

	DEBUG("OK ================================ OK\n");
	msg_send2_UI(STATUS_DVBPUSH_INIT_SUCCESS, NULL, 0);
	
	int main_running = 1;
	while(1==main_running)
	{
		pthread_mutex_lock(&mtx_main);
		/*
		需要本线程先运行到这里，再在其他非父线程中执行pthread_cond_signal(&cond_push_monitor)才能生效。
		*/
		pthread_cond_wait(&cond_main,&mtx_main);
		DEBUG("main thread is closed by external call\n");
		main_running = 0;
		pthread_mutex_unlock(&mtx_main);
	}
	DEBUG("exit from main thread\n");
	
	return NULL;
}

int dvbpush_init()
{
	if(0==s_dvbpush_init_flag){
		s_dvbpush_init_flag = 1;
		DEBUG("dvbpush init...>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.......\n");
		pthread_create(&tid_main, NULL, main_thread, NULL);
		//pthread_detach(tid_main);
	}
	else{
		DEBUG("can NOT do dvbpush init for more than once: %d\n", s_dvbpush_init_flag);
	}
	return 0;
}

int dvbpush_uninit()
{
	if(1==s_dvbpush_init_flag){
		DEBUG("dvbpush uninit...\n");
		
		/*
		必须先调用softdvb_uninit()，因为softdvb_thread()中使用了malloc出来的两个资源：
		1、p_buf——在igmp_thread()中malloc和free，关联igmp_uninit()。
		2、g_recvBuffer——在mid_push_init()中malloc，在mid_push_uninit()中free
		*/
		softdvb_uninit();
#ifdef TUNER_INPUT
#else
		igmp_uninit();
#endif
		mid_push_uninit();
		xmlparser_uninit();
		sqlite_uninit();
		setting_uninit();
		
		pthread_mutex_lock(&mtx_main);
		pthread_cond_signal(&cond_main);
		pthread_mutex_unlock(&mtx_main);
		
		pthread_join(tid_main, NULL);
		
		DEBUG("dvbpush over\n");
		s_dvbpush_init_flag = -1;
	}
	else{
		DEBUG("can not do dvbpush uninit with %d\n", s_dvbpush_init_flag);
	}
	return 0;
}

#if DVB_TEST_ENABLE
void *main_thread_test(int argc, char **argv)
{
	main_thread(NULL);
	return NULL;
}

int main(int argc, char **argv)
{
	main_thread_test(argc, argv);
	return 0;
}
#endif

