﻿#ifndef __MULTICAST_H__
#define __MULTICAST_H__

int multicast_add();
int multi_buf_read(unsigned char *buf, unsigned int len);
int softdvb_init();
int softdvb_uninit();
int tdt_time_sync_awake();
int igmp_init();
int igmp_uninit();
int pid_init(int act_flag);
void net_rely_condition_set(int rely_cond);
int data_stream_status_get();
int data_stream_status_str_get(char *buf, unsigned int size);

#endif
