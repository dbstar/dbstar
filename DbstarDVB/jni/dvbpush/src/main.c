#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <pthread.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#include "common.h"
#include "xmlparser.h"
#include "sqlite.h"
#include "mid_push.h"
#include "porting.h"
#include "multicast.h"
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
 考虑到升级是个非常重要但又较少依赖其他模块的功能，也和硬盘存储无关，因此即使大部分模块初始化失败、或者无硬盘，也一样要继续运行，只要组播功能正常即可。
*/
void *main_thread()
{
	DEBUG("main thread start...\n");
        
	_wLBM_zyzdmb(13578642);

#ifdef SMARTLIFE_LC
   	smarthome_gw_sn_init();
#endif
   	
	if(-1==setting_init()){
		DEBUG("setting init failed\n");
		//return NULL;	// continue for upgrade
	}
	
	if(-1==push_decoder_buf_init()){
		DEBUG("push decoder buf init failed\n");
		//return NULL;	// continue for upgrade
	}

#ifdef TUNER_INPUT
	DEBUG("this is a tuner box, build at %s %s\n", __DATE__,__TIME__);
#else
	DEBUG("this is a network box, build at %s %s\n", __DATE__,__TIME__);
	chanFilterInit();
#endif
    smc_init();
	
	// 只用来确保flash中主数据库的存在和完整，待pushroot确定后，再进行硬盘中数据库初始化
	if(-1==db_init(DB_MAIN_URI)){
		DEBUG("sqlite init failed\n");
		//return NULL;	// continue for upgrade
	}
	
	setting_init_with_database();

#if 0
线程maintenance_thread直接放在main中执行
	maintenance_thread_init();
#endif
	
	if(-1==xmlparser_init()){
		DEBUG("xmlparser init failed\n");
		//return NULL;	// continue for upgrade
	}

#ifdef TUNER_INPUT
	tuner_init();
#endif
	
//	return parse_xml("pushroot/pushinfo/1/ProductDesc.xml", PRODUCTDESC_XML, NULL);
	
	if(0!=drm_init()){
		DEBUG("drm init failed\n");
		//return NULL;	// continue for upgrade
	}
	
	upgrade_info_init();

#ifdef SMARTLIFE_LC	
// 根据首次开机标记"/data/data/com.dbstar/files/flag"决定是否要重置国电网关序列号
	smarthome_sn_init_when_network_init();
#endif
	
	if(-1==mid_push_init(PUSH_CONF_WORKING)){
		DEBUG("push model init with \"%s\" failed\n", PUSH_CONF_WORKING);
		//return NULL;	// continue for upgrade
	}

#ifdef TUNER_INPUT
#else
// 可以加入组播组
	if(-1==igmp_init()){
		DEBUG("igmp init failed\n");
		//return NULL;	// continue for upgrade
	}
#endif
	
	if(-1==softdvb_init()){
		DEBUG("dvb init with failed\n");
		//return NULL;	// continue for upgrade
	}
	
#ifdef SMARTLIFE_LC
	smartlife_connect_init();
#endif

	DEBUG("OK ========== dvbpush init finished ========== OK\n");
	msg_send2_UI(STATUS_DVBPUSH_INIT_SUCCESS, NULL, 0);
	
	maintenance_thread();
	DEBUG("exit from main thread\n");
	
	return NULL;
}

static int watchdog_close()
{
	char *watchdog_close_flag = "0";
	int watchdog_hd = open(WATCHDOG_CTRL, O_RDWR);
	if(-1==watchdog_hd){
		ERROROUT("open %s to close watch dog failed\n", WATCHDOG_CTRL);
		return -1;
	}
	else{
		if(write(watchdog_hd, watchdog_close_flag, strlen(watchdog_close_flag))<0){
			ERROROUT("write %s to %s failed\n", watchdog_close_flag, WATCHDOG_CTRL);
		}
		
		close(watchdog_hd);
		DEBUG("write %s to %s success\n", watchdog_close_flag, WATCHDOG_CTRL);
		
		return 0;
	}
}

int dvbpush_init()
{
	if(0==s_dvbpush_init_flag){
		s_dvbpush_init_flag = 1;
		DEBUG("dvbpush init...>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.......\n");
		watchdog_close();
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
		db_uninit();
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

