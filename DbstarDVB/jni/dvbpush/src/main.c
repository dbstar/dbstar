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
   	
	if(-1==setting_init()){
		DEBUG("setting init failed\n");
		//return NULL;
	}
	
	if(-1==push_decoder_buf_init()){
		DEBUG("push decoder buf init failed\n");
		return NULL;
	}
	
	if(-1==sqlite_init()){
		DEBUG("sqlite init failed\n");
		//return NULL;
	}
	
	if(-1==xmlparser_init()){
		DEBUG("xmlparser init failed\n");
		//return NULL;
	}
	
	if(0==drm_init()){
		DEBUG("drm init failed\n");
		//return NULL;
	}
	
	// only for xml parse testing
#if 0
	char xml_uri[128];
	char sqlite_cmd[256];
	memset(xml_uri, 0, sizeof(xml_uri));
	int (*sqlite_cb)(char **, int, int, void *, unsigned int) = str_read_cb;
	snprintf(sqlite_cmd,sizeof(sqlite_cmd),"SELECT Value FROM Global WHERE Name='XMLURI';");
	int ret_sqlexec = sqlite_read(sqlite_cmd, xml_uri, sizeof(xml_uri), sqlite_cb);
	if(ret_sqlexec<=0){
		DEBUG("read no xml uri for parse testing\n");
	}
	else{
		DEBUG("parse xml uri: %s\n", xml_uri);
	}
	
	char xml_flag[128];
	memset(xml_flag, 0, sizeof(xml_flag));
	snprintf(sqlite_cmd,sizeof(sqlite_cmd),"SELECT Value FROM Global WHERE Name='XMLFlag';");
	ret_sqlexec = sqlite_read(sqlite_cmd, xml_flag, sizeof(xml_flag), sqlite_cb);
	if(ret_sqlexec<=0){
		DEBUG("read no xml flag for parse testing\n");
	}
	else{
		DEBUG("parse xml flag: %s\n", xml_flag);
	}
	
	if(strlen(xml_uri)>0)
	{
		parse_xml(xml_uri, atoi(xml_flag), NULL);
		//return NULL;
	}
#endif

	if(-1==mid_push_init(PUSH_CONF)){
		DEBUG("push model init with \"%s\" failed\n", PUSH_CONF);
		//return NULL;
	}
	
	if(-1==igmp_init()){
		DEBUG("igmp init failed\n");
		return NULL;
	}
	
	if(-1==softdvb_init()){
		DEBUG("dvb init with failed\n");
		//return NULL;
	}
	
	upgrade_info_init();
	
	
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
		igmp_uninit();
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
