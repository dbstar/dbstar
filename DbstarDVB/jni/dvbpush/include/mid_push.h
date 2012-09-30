#ifndef __MID_PUSH_H__
#define __MID_PUSH_H__

int send_mpe_sec_to_push_fifo(unsigned char *pkt, int pkt_len);
int mid_push_init(char *push_conf);
int mid_push_uninit();
int pushdata_rootdir_get(char *buf, unsigned int size);
int push_monitor_reset();
#endif
