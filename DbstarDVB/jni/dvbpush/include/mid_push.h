#ifndef __MID_PUSH_H__
#define __MID_PUSH_H__

typedef struct{
	char uri[512];
	int flag;
}PUSH_XML_S;

void push_root_dir_init(char *push_conf);
int send_mpe_sec_to_push_fifo(unsigned char *pkt, int pkt_len);
int mid_push_init(char *push_conf);
int mid_push_uninit();
int pushdata_rootdir_get(char *buf, unsigned int size);
int push_monitor_reset();

void dvbpush_getinfo_start();
void dvbpush_getinfo_stop();
int dvbpush_getinfo(char **p, unsigned int *len);

int push_decoder_pause();
int push_decoder_resume();
void push_rely_condition_set(int rely_cond);

int push_decoder_buf_init();

#endif
