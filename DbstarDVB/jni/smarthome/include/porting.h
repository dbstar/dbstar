#ifndef __PORTING_H__
#define __PORTING_H__

int serialNum_get(char *sn, unsigned int len);
int setting_init(void);
int initial_serial_num_get(char *sn, unsigned int len);
int initial_server_ip_get(char *server_ip, unsigned int len);
int initial_server_port_get(void);
int initial_software_version_get(char *version, unsigned int len);

int smartpower_server_ip_get(char *server_ip, unsigned int len);
int smartpower_server_port_get(void);
int softwareVersion_get(char *version, unsigned int len);
int reboot(void);
int poweroff(void);

int ifconfig_get(char *interface_name, char *ip, char *status, char *mac);

#endif

