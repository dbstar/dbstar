#ifndef __TIMING_H__
#define __TIMING_H__

#include <sys/time.h>
#include <unistd.h>
#include "common.h"


#define TIMER_NUM	256
typedef struct{
	unsigned int	id;
	TIMER_TYPE_E	type;				
	struct timeval 	tv_timer;
	int				arg1;
	int				arg2;
	int				(*callback)(struct timeval *tv_datum, int arg1, int arg2);
}TIMER_S;


int timing_init(void);
void timing_mainloop(void);
int timer_regist(struct timeval *tv, TIMER_TYPE_E type,int arg1, int arg2, int (*timer_callback)(struct timeval *tv_datum, int arg1, int arg2));
int instant_timing_task_regist(int type_id, int frequency, int control_time);
int timer_unregist(int *timer_id);
int timing_task_refresh(void);
int time_rectify_flag_reset(void);

#endif