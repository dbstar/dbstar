#ifndef __PORTING_H__
#define __PORTING_H__

char *setting_item_value(char *buf, unsigned int buf_len, char separator);
int setting_init(void);
int setting_uninit(void);
int special_productid_check(char *productid);
int root_channel_get(void);
int root_push_file_get(char *filename, unsigned int len);
int root_push_file_size_get(void);
char *multi_addr_get(void);
int database_uri_get(char *database_uri, unsigned int size);
int parse_xml_get(char *xml_uri, unsigned int size);
int prog_data_pid_get(void);
int ifconfig_get(char *interface_name, char *ip, char *status, char *mac);
int msg_send2_UI(int type, char *msg, int len);
int initialize_xml_get();
int localcolumn_res_get(char *localcolumn_res, unsigned int uri_size);
void upgrade_info_init();
int drm_info_refresh();
char *language_get();
int software_check(void);
int check_productid_from_smartcard(char *productid);
char *serviceID_get();
int serviceID_set(char *serv_id);
void upgrade_sign_set();
char *column_res_get();
char *push_dir_get();

#endif
