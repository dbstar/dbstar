#ifndef __PORTING_H__
#define __PORTING_H__

char *setting_item_value(char *buf, unsigned int buf_len, char separator);
int setting_init(void);
int setting_uninit(void);
int special_productid_check(char *productid);
int root_channel_get(void);
int root_push_file_get(char *filename, unsigned int len);
int root_push_file_size_get(void);
int data_source_get(char *data_source, unsigned int len);
int database_uri_get(char *database_uri, unsigned int size);
int parse_xml_get(char *xml_uri, unsigned int size);
int prog_data_pid_get(void);
int ifconfig_get(char *interface_name, char *ip, char *status, char *mac);
int msg_send2_UI(int type, char *msg, int len);
int initialize_xml_get();
int localcolumn_res_get(char *localcolumn_res, unsigned int uri_size);
void upgrade_info_init();
int drm_info_init();
char *language_get();

#endif
