#ifndef __MULTICAST_H__
#define __MULTICAST_H__

int multicast_add(const char *multi_addr);
int multi_buf_read(unsigned char *buf, unsigned int len);
int softdvb_init();
int softdvb_uninit();
int igmp_init();
int igmp_uninit();
int pid_init(int act_flag);
void net_rely_condition_set(int rely_cond);

#endif
