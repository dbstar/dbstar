#include <stdio.h>
#include <pthread.h>
#include <string.h>
#include <sys/types.h>
#include <unistd.h>

#include "common.h"
#include "global.h"
#include "socket.h"
#include "instruction.h"
#include "timing.h"
#include "porting.h"
#include "sqlite.h"
#include "serial.h"
#include "equipment.h"

/*
功能：	程序开始时初始化各个模块，启动相关的线程，instruction线程用于处理具体指令，timing线程管理定时器
返回：	0——成功；-1——失败
*/
int global_init()
{
	int ret = -1;

	if(-1==dir_exist_ensure(WORKSPACE_SURFIX))
		return -1;
	
	setting_init();
	
	if(-1==dir_exist_ensure(FIFO_DIR))
		return -1;
	if(-1==socket_init()){
		DEBUG("socket module init failed\n");
		return -1;
	}
	if(-1==sqlite_init()){
		DEBUG("sqlite module init failed\n");
		return -1;
	}
	if(-1==timing_init()){
		DEBUG("timer module init failed\n");
		return -1;
	}
	if(-1==equipment_init()){
		DEBUG("equipment array init failed\n");
		return -1;
	}
	if(-1==instruction_init()){
		DEBUG("instruction module init failed\n");
		return -1;
	}
	if(-1==serial_int()){
		DEBUG("serial module init failed\n");
		return -1;
	}

// only for database testing
	DEBUG("getGlobalPara()=%d\n", getGlobalPara("version"));
	
	//~~~~~~~~~~create  thread~~~~~~~~~~//
	typedef void*(*format)(void *);													///define function pointer of "void*(name)(void*)"
	pthread_t l_socketlinkthread;													/// the file description of thread
#if 0
	pthread_attr_t l_attrthread;													///the attribute of thread
	size_t l_stacksize=1048576*1;													///set thread stack size
	///start thread.
	pthread_attr_init(&l_attrthread);												///initialize attribute
	pthread_attr_setscope(&l_attrthread,PTHREAD_SCOPE_SYSTEM);						///set attribute binding
	pthread_attr_setdetachstate(&l_attrthread,PTHREAD_CREATE_DETACHED);				///set attribute detached
	pthread_attr_setstacksize(&l_attrthread,l_stacksize);
	ret=pthread_create(&l_socketlinkthread,&l_attrthread,(format)socketHandler,NULL);
#else	/* use default attr */
	ret = pthread_create(&l_socketlinkthread,NULL,(format)instruction_mainloop,NULL);
#endif
	if(0!=ret)
	{
		ERROROUT("thread instruction_mainloop create failed!");
		return -1;
	}

	pthread_t timing_thread_id;
	ret = pthread_create(&timing_thread_id, NULL, (void *)timing_mainloop, NULL);
	if(0!=ret){
		ERROROUT("thread timing_mainloop create failed\n");
	}
	
	return 0;
}

