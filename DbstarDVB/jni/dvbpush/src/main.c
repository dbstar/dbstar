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
	
	if(-1==sqlite_init()){
		DEBUG("sqlite init failed\n");
		return NULL;
	}
	
	if(-1==xmlparser_init()){
		DEBUG("xmlparser init failed\n");
		return NULL;
	}

	// 可以开始解析指定的xml文件
	//parseDoc(xxxxx.xml);
	
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

int dvbpush_start()
{
	DEBUG("dvbpush start...\n");
	pthread_create(&tid_main, NULL, main_thread, NULL);
	//pthread_detach(tid_main);
	
	return 0;
}

int dvbpush_stop()
{
	DEBUG("dvbpush stop...\n");
	
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
	return 0;
}
