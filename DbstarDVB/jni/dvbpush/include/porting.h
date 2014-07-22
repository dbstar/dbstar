#ifndef __PORTING_H__
#define __PORTING_H__

char *setting_item_value(char *buf, unsigned int buf_len, char separator);
int setting_init(void);
int setting_uninit(void);
int ProductID_check(char *productid);
int root_channel_get(void);
int root_push_file_get(char *filename, unsigned int len);
int root_push_file_size_get(void);
char *multi_addr_get(void);
char *dbstar_database_uri();
char *smartlife_database_uri_get();
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
char *serviceID_get();
int serviceID_set(char *serv_id);
void upgrade_sign_set();
char *push_dir_get();
char *initialize_uri_get();
int guidelist_select_status(const char *publication_id);
int guidelist_select_refresh();
int disk_manage(char *PublicationID, char *ProductID);
int smart_card_insert_flag_set(int insert_flag);
int smart_card_insert_flag_get();
int smart_card_remove_flag_set(int remove_flag);
int smart_card_remove_flag_get();
int intialize_xml_reset(void);
int setting_init_with_database();
int pushinfo_reset(void);
int smartcard_entitleinfo_refresh();
int storage_hd_enable();
int user_idle_status_get();
time_t reboot_timestamp_get();
int reboot_timestamp_set(time_t time_stamp_s);
int onehour_before_pushend_get();
int onehour_before_pushend_set(int onehour_before_pushend);
int network_init_status();
int device_num_changed();
int smartcard_action_set(int smartcard_action);
int smartcard_action_get();

int motherdisc_check();

int storage_hd_enable();
int storage_flash_check();
int hd_is_ready_by_launcher();

#include "dvbpush_api.h"
int send_sc_notify(int can_send_nofity, DBSTAR_CMD_MSG_E sc_notify, char *msg, int len);

#endif
