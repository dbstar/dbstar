#ifndef __MID_PUSH_H__
#define __MID_PUSH_H__

typedef struct{
	char	uri[512];
	int		flag;
	char	arg_ext[512];
}PUSH_XML_S;

void push_root_dir_init(char *push_conf);
int send_mpe_sec_to_push_fifo(unsigned char *pkt, int pkt_len);
int mid_push_init(char *push_conf);
int mid_push_uninit();
int pushdata_rootdir_get(char *buf, unsigned int size);
int push_monitor_reset();

void dvbpush_getinfo_start();
void dvbpush_getinfo_stop();
int dvbpush_getinfo(char *buf, unsigned int size);

int push_decoder_pause();
int push_decoder_resume();
void push_rely_condition_set(int rely_cond);

int push_decoder_buf_init();
int prog_monitor_reset(void);
int push_recv_manage_refresh();
int send_xml_to_parse(const char *path, int flag, char *id);
int productdesc_parsed_set(char *xml_uri, PUSH_XML_FLAG_E push_flag, char *arg_ext);

void disk_manage_flag_set(int flag);
void column_refresh_flag_set(int flag);
void interface_refresh_flag_set(int flag);
void preview_refresh_flag_set(int flag);
void service_xml_waiting_set(int flag);
int info_xml_refresh(int regist_flag, int push_flags[], unsigned int push_flags_cnt);
int info_xml_regist();
int maintenance_thread_init();
void maintenance_thread_awake();
int delete_publication_from_monitor(char *PublicationID, char *ProductID);
int dvbpush_download_finish();

#endif
