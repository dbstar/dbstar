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

#define DVB_TEST_ENABLE 0
static int s_dvbpush_init_flag = 0;
static pthread_t tid_main;
static pthread_mutex_t mtx_main = PTHREAD_MUTEX_INITIALIZER;
static pthread_cond_t cond_main = PTHREAD_COND_INITIALIZER;

void *main_thread()
{	
	DEBUG("main thread start...\n");
	compile_timeprint();
	
	if(-1==setting_init()){
		DEBUG("setting init failed\n");
		return NULL;
	}
	
//	push_root_dir_init(PUSH_CONF);
	
	if(-1==sqlite_init()){
		DEBUG("sqlite init failed\n");
		return NULL;
	}
	
	if(-1==xmlparser_init()){
		DEBUG("xmlparser init failed\n");
		return NULL;
	}
	
//	char xml_uri[128];
//	memset(xml_uri, 0, sizeof(xml_uri));
//	parse_xml_get(xml_uri, sizeof(xml_uri));
//	DEBUG("parse_xml: %s\n", xml_uri);
//	if(strlen(xml_uri)>0)
//	{
//		// 可以开始解析指定的xml文件
//		//return parseDoc("/mnt/sda1/dbstar/pushinfo/initialize/Initialize.xml");
//		//return parseDoc("/mnt/sda1/dbstar/pushinfo/channel/Channel.xml");
//		//return parseDoc("/mnt/sda1/dbstar/pushinfo/servicegroup/01/101/desc/Product_preview.xml");
//		parse_xml(xml_uri, 0);
//		return NULL;
//	}
	
	if(-1==mid_push_init(PUSH_CONF)){
		DEBUG("push model init with \"%s\" failed\n", PUSH_CONF);
		return NULL;
	}
	
	if(-1==igmp_init()){
		DEBUG("igmp init failed\n");
		return NULL;
	}
	
	if(-1==softdvb_init()){
		DEBUG("dvb init with failed\n");
		return NULL;
	}
	
	msg_send2_UI(STATUS_DVBPUSH_INIT_SUCCESS, NULL, 0);
	
	upgrade_info_init();
	
	DEBUG("read drm info\n");
	drm_info_init();
	
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
		DEBUG("dvbpush init...\n");
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
