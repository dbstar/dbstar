#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/param.h>
#include <sys/ioctl.h>
#include <sys/socket.h>
#include <net/if.h>
#include <netinet/in.h>
#include <net/if_arp.h>
#include <arpa/inet.h>

#include "common.h"
#include "sqlite.h"

static int	s_settingInitFlag = 0;
static char s_serial_num[33];
static char	s_server_ip[16];
static int	s_server_port;
static char s_version[32];

#ifdef WORK_NORMAL_NOT_TEST
#else
static char s_test_cmdstr[256];

char *test_cmdstr_get(void)
{
	return s_test_cmdstr;
}
#endif
/* define some general interface function here */

static void settingDefault_set(void)
{
	memset(s_serial_num, 0, sizeof(s_serial_num));
	memset(s_server_ip, 0, sizeof(s_server_ip));
	memset(s_version, 0, sizeof(s_version));
	
	strncpy(s_serial_num, SN_DEFAULT_TEST, strlen(SN_DEFAULT_TEST));
	strncpy(s_server_ip, SMARTPOWER_SERVER_IP, strlen(SMARTPOWER_SERVER_IP));
	s_server_port = SMARTPOWER_SERVER_PORT;
	strncpy(s_version, SW_VERSION, strlen(SW_VERSION));
	
	return;
}

int setting_init(void)
{
	if(1==s_settingInitFlag)
		return 0;
		
	FILE* fp;
	char tmp_buf[256];
	int i=0;
	int j=0;
	char *p_value;

	settingDefault_set();
	DEBUG("init settings with file %s\n", SETTING_BASE);
	fp = fopen(SETTING_BASE,"r");
	if (NULL == fp)
	{
		ERROROUT("open file %s faild! use default setting\n", SETTING_BASE);
		return 0;
	}
	DEBUG("open file %s success\n", SETTING_BASE);
	memset(tmp_buf, 0, sizeof(tmp_buf));
	
	while(NULL!=fgets(tmp_buf, sizeof(tmp_buf), fp)){
		if('#'==tmp_buf[0]){
			DEBUG("this line is ignored as explain\n");
			continue;
		}
		DEBUG("read line: %s\n", tmp_buf);
		j = 0;
		for(i=0; i<strlen(tmp_buf); i++){
#if 0
			if( (tmp_buf[i]>'0'&&tmp_buf[i]<'9')
				||(tmp_buf[i]>'A'&&tmp_buf[i]<'Z')
				||(tmp_buf[i]>'a'&&tmp_buf[i]<'z')
				||'_'==tmp_buf[i] || '-'==tmp_buf[i] || '@'==tmp_buf[i] )
#else
			if( isgraph(tmp_buf[i]) )		//or check it between 33('!') and 126('~')
#endif
			{
				if(j!=i)
					tmp_buf[j] = tmp_buf[i];
				j++;
			}
		}
		tmp_buf[j] = '\0';
		DEBUG("tmp_buf: %s\n", tmp_buf);
		if('#'==tmp_buf[0]){
			DEBUG("ignore a line because of explain\n");
			continue;
		}
		p_value = strchr(tmp_buf, '=');
		if(p_value){
			*p_value = '\0';
			p_value++;
			DEBUG("tmp_buf: %s, p_value=%s\n", tmp_buf, p_value);
			if(strlen(tmp_buf)>0 && strlen(p_value)>0){
				if(0==strcmp(tmp_buf, "server_ip"))
					strncpy(s_server_ip, p_value, sizeof(s_server_ip)-1);
				else if(0==strcmp(tmp_buf, "server_port"))
					s_server_port = atoi(p_value);
				else if(0==strcmp(tmp_buf, "serial_num"))
					strncpy(s_serial_num, p_value, sizeof(s_serial_num)-1);
				else if(0==strcmp(tmp_buf, "version"))
					strncpy(s_version, p_value, sizeof(s_version)-1);
#ifdef WORK_NORMAL_NOT_TEST
#else
				else if(0==strcmp(tmp_buf, "test_cmdstr")){
					memset(s_test_cmdstr, 0, sizeof(s_test_cmdstr));
					strncpy(s_test_cmdstr, p_value, sizeof(s_test_cmdstr)-1);
				}
#endif
			}
		}
		memset(tmp_buf, 0, sizeof(tmp_buf));
		j = 0;
	}
	fclose(fp);
	DEBUG("init settings OK\n");

	s_settingInitFlag = 1;
	return 0;
}

int initial_serial_num_get(char *sn, unsigned int len)
{
	if(NULL==sn || 0==len)
		return -1;

	strncpy(sn, s_serial_num, len);
	return 0;
}

int initial_server_ip_get(char *server_ip, unsigned int len)
{
	if(NULL==server_ip || 0==len)
		return -1;

	strncpy(server_ip, s_server_ip, len);
	
	return 0;
}

int initial_server_port_get(void)
{
	return s_server_port;
}

int initial_software_version_get(char *version, unsigned int len)
{
	if(NULL==version || 0==len)
		return -1;

	strncpy(version, s_version, len);
	
	return 0;
}


// use 'receiver' as char*
static int setting_sqlite_callback(char **result, int row, int column, void *receiver)
{
	DEBUG("sqlite callback, row=%d, column=%d, receiver addr: %p\n", row, column, receiver);
	if(row<1){
		DEBUG("no record in table, return\n");
		return -1;
	}

// only one recored
	int i = 1;
//	for(i=1;i<row+1;i++)
	{
		strcpy((char *)receiver, result[i*column]);
	}
	return 0;
}

int serialNum_get(char *sn, unsigned int len)
{
	if(NULL==sn || 0==len)
		return -1;
	
//	if(strlen(s_serial_num)>10){
//		strncpy(sn, s_serial_num, len);
//		return 0;
//	}
		
	char sqlite_cmd[SQLITECMDLEN];	
	int (*sqlite_callback)(char **, int, int, void *) = setting_sqlite_callback;

	memset(sqlite_cmd, 0, sizeof(sqlite_cmd));
	snprintf(sqlite_cmd,sizeof(sqlite_cmd),"SELECT value FROM global WHERE name='serialNUM';");
	INSTRUCTION_RESULT_E ret = sqlite_read(sqlite_cmd, sn, sqlite_callback);
	if(ret<=RESULT_OK){
		DEBUG("read 'sn' from table 'global' failed, read from ini\n");
		strncpy(sn, s_serial_num, len);
	}
	
	return 0;
}

int smartpower_server_ip_get(char *server_ip, unsigned int len)
{
	if(NULL==server_ip || 0==len)
		return -1;

	char sqlite_cmd[SQLITECMDLEN];	
	int (*sqlite_callback)(char **, int, int, void *) = setting_sqlite_callback;

	memset(sqlite_cmd, 0, sizeof(sqlite_cmd));
	snprintf(sqlite_cmd,sizeof(sqlite_cmd),"SELECT value FROM global WHERE name='serverIP';");
	INSTRUCTION_RESULT_E ret = sqlite_read(sqlite_cmd, server_ip, sqlite_callback);
	if(ret<=RESULT_OK){
		DEBUG("read 'serverIP' from table 'global' failed, read from ini\n");
		strncpy(server_ip, s_server_ip, len);
	}
	
	return 0;
}

int smartpower_server_port_get(void)
{
	char server_port[16];
	char sqlite_cmd[SQLITECMDLEN];	
	int (*sqlite_callback)(char **, int, int, void *) = setting_sqlite_callback;

	memset(server_port, 0, sizeof(server_port));
	memset(sqlite_cmd, 0, sizeof(sqlite_cmd));
	snprintf(sqlite_cmd,sizeof(sqlite_cmd),"SELECT value FROM global WHERE name='port';");
	INSTRUCTION_RESULT_E ret = sqlite_read(sqlite_cmd, server_port, sqlite_callback);
	if(ret<=RESULT_OK || 0>=atoi(server_port)){
		DEBUG("read 'port' from table 'global' failed, read from ini\n");
		return s_server_port;
	}
	else{
		return atoi(server_port);
	}
}

int softwareVersion_get(char *version, unsigned int len)
{
	if(NULL==version || 0==len)
		return -1;

	char sqlite_cmd[SQLITECMDLEN];	
	int (*sqlite_callback)(char **, int, int, void *) = setting_sqlite_callback;

	memset(sqlite_cmd, 0, sizeof(sqlite_cmd));
	snprintf(sqlite_cmd,sizeof(sqlite_cmd),"SELECT value FROM global WHERE name='version';");
	INSTRUCTION_RESULT_E ret = sqlite_read(sqlite_cmd, version, sqlite_callback);
	if(ret<=RESULT_OK){
		DEBUG("read 'version' from table 'global' failed, read from ini\n");
		strncpy(version, s_version, len);
	}
	
	return 0;
}

int setting_sn_set(char *sn, unsigned int len)
{
	if(NULL==sn || 0>=len){
		DEBUG("sn to set is invalid\n");
		return -1;
	}

	char sqlite_cmd[SQLITECMDLEN];	
	
	memset(sqlite_cmd, 0, sizeof(sqlite_cmd));
	snprintf(sqlite_cmd,sizeof(sqlite_cmd),"UPDATE global SET value='%s' WHERE name='serialNUM';",sn);
	DEBUG("sqlite cmd str: %s\n", sqlite_cmd);

	INSTRUCTION_RESULT_E ret = sqlite_execute(sqlite_cmd);
	if(RESULT_OK==ret)
		DEBUG("set sn as %s success\n", sn);
		
	return ret;
}

int settings_reset(void)
{
	DEBUG("CAUTION: begin to reset setting\n");
	char sqlite_cmd[SQLITECMDLEN];	
	
	memset(sqlite_cmd, 0, sizeof(sqlite_cmd));
	snprintf(sqlite_cmd,sizeof(sqlite_cmd),"DELETE FROM global;");
	DEBUG("sqlite cmd str: %s\n", sqlite_cmd);

	INSTRUCTION_RESULT_E ret = sqlite_execute(sqlite_cmd);
	if(RESULT_OK==ret){
		DEBUG("settings table 'global' clear success\n");
		unlink(SETTING_BASE);
		return 0;
	}
	else{
		DEBUG("settings table 'global' reset failed\n");
		return -1;
	}
}

int factory_renew(void)
{
	DEBUG("CAUTION: begin to renew factory status\n");

	unlink(DATABASE);
	unlink(SETTING_BASE);
	sync();
	sleep(1);
	
	return 0;
}

int reboot(void)
{
	DEBUG("need reboot the stb, this is a phony action\n");
	return 0;
}

int poweroff(void)
{
	DEBUG("need power off the stb, this is a phony action\n");
	return 0;
}

int alarm_ring(void)
{
	return -1;
}



#ifdef SOLARIS
#include <sys/sockio.h>
#endif

// only for IP v4
#define MAXINTERFACES   16

/*
功能：获取指定网卡的ip、状态、mac地址
输入：interface_name ——网卡名称，如："eth0"、"lo"
输出：	ip		——点分十进制的IP v4地址，如："192.168.100.100"
		status	——状态，如："UP"、"DOWN"
		mac		——MAC地址，以分号隔开的16进制表示，如："00:0c:29:50:fc:f8"
返回：0——成功；其他——失败
*/
int ifconfig_get(char *interface_name, char *ip, char *status, char *mac)
{
	if(NULL==interface_name || (NULL==ip && NULL==status && NULL==mac)){
		DEBUG("some params are invalid\n");
		return -1;
	}
	
#ifdef SOLARIS
	struct arpreq arp;
#endif
	register int fd=0, interface_num=0, ret = 0;
	struct ifreq buf[MAXINTERFACES];
	struct ifconf ifc;
	
	if ((fd = socket(AF_INET, SOCK_DGRAM, 0)) >= 0)
	{
		DEBUG("create socket(%d) to read ifconfig infor\n", fd);
		ifc.ifc_len = sizeof(buf);
		ifc.ifc_buf = (caddr_t) buf;
		if (!ioctl (fd, SIOCGIFCONF, (char *) &ifc))
		{
			//获取接口信息
			interface_num = ifc.ifc_len / sizeof (struct ifreq);
			DEBUG("interface num is %d\n",interface_num);
			//根据接口信息循环获取设备IP和MAC地址
			while (interface_num-- > 0)
			{
				//获取设备名称
				DEBUG ("net device %d: %s\n", interface_num, buf[interface_num].ifr_name);
				if(strcmp(interface_name, buf[interface_num].ifr_name)){
					continue;
				}
				
				ret = 0;
				//判断网卡类型
				if (!(ioctl (fd, SIOCGIFFLAGS, (char *) &buf[interface_num])))
				{
					if (buf[interface_num].ifr_flags & IFF_PROMISC)
					{
						DEBUG("the interface_num is PROMISC\n");
					}
				}
				else
				{
					char str[256];
					sprintf (str, "cpm: ioctl device %s", buf[interface_num].ifr_name);
					perror (str);
					ret = -1;
					break;
				}
				
				//判断网卡状态
				if(NULL!=status)
				{
					if (buf[interface_num].ifr_flags & IFF_UP)
					{
						DEBUG("the interface_num status is UP\n");
						strcpy(status, "UP");
					}
					else
					{
						DEBUG("the interface_num status is DOWN\n");
						strcpy(status, "DOWN");
					}
				}
				
				//获取当前网卡的IP地址
				if(NULL!=ip)
				{
					if (!(ioctl (fd, SIOCGIFADDR, (char *) &buf[interface_num])))
					{
						strcpy(ip, (char *)inet_ntoa(((struct sockaddr_in*)(&buf[interface_num].ifr_addr))->sin_addr));
						DEBUG ("IP address is: %s\n", ip);
					}
					else
					{
						char str[256];
						sprintf (str, "cpm: ioctl device %s", buf[interface_num].ifr_name);
						perror (str);
						ret = -1;
						break;
					}
				}
				if(NULL!=mac)
				{
		
#ifdef SOLARIS		/* this section can't get Hardware Address,I don't know whether the reason is module driver*/
					//获取MAC地址
					arp.arp_pa.sa_family = AF_INET;
					arp.arp_ha.sa_family = AF_INET;
					((struct sockaddr_in*)&arp.arp_pa)->sin_addr.s_addr=((struct sockaddr_in*)(&buf[interface_num].ifr_addr))->sin_addr.s_addr;
					if (!(ioctl (fd, SIOCGARP, (char *) &arp)))
					{
						puts ("HW address is:");     //以十六进制显示MAC地址
						printf("%02x:%02x:%02x:%02x:%02x:%02x\n",
							(unsigned char)arp.arp_ha.sa_data[0],
							(unsigned char)arp.arp_ha.sa_data[1],
							(unsigned char)arp.arp_ha.sa_data[2],
							(unsigned char)arp.arp_ha.sa_data[3],
							(unsigned char)arp.arp_ha.sa_data[4],
							(unsigned char)arp.arp_ha.sa_data[5]);
						puts("");
						puts("");
					}
#else
	#if 0
					/*Get HW ADDRESS of the net card */
					if (!(ioctl (fd, SIOCGENADDR, (char *) &buf[interface_num])))
					{
						puts ("HW address is:");
						printf("%02x:%02x:%02x:%02x:%02x:%02x\n",
							(unsigned char)buf[interface_num].ifr_enaddr[0],
							(unsigned char)buf[interface_num].ifr_enaddr[1],
							(unsigned char)buf[interface_num].ifr_enaddr[2],
							(unsigned char)buf[interface_num].ifr_enaddr[3],
							(unsigned char)buf[interface_num].ifr_enaddr[4],
							(unsigned char)buf[interface_num].ifr_enaddr[5]);
						puts("");
						puts("");
					}
	#endif

					if (!(ioctl (fd, SIOCGIFHWADDR, (char *) &buf[interface_num])))
					{
						sprintf(mac, "%02x:%02x:%02x:%02x:%02x:%02x",
							(unsigned char)buf[interface_num].ifr_hwaddr.sa_data[0],
							(unsigned char)buf[interface_num].ifr_hwaddr.sa_data[1],
							(unsigned char)buf[interface_num].ifr_hwaddr.sa_data[2],
							(unsigned char)buf[interface_num].ifr_hwaddr.sa_data[3],
							(unsigned char)buf[interface_num].ifr_hwaddr.sa_data[4],
							(unsigned char)buf[interface_num].ifr_hwaddr.sa_data[5]);
						DEBUG("hardware address: %s\n", mac);
					}
#endif
					else
					{
						char str[256];
						sprintf (str, "cpm: ioctl device %s", buf[interface_num].ifr_name);
						perror (str);
						ret = -1;
						break;
					}
				}
				break;					// 只完成一个有效循环即可。
			} //while
		}
		else{
			ERROROUT ("cpm: ioctl");
			ret = -1;
		}
		
		close (fd);
		DEBUG("close fd(%d)\n", fd);
	}
	else{
		ERROROUT ("cpm: socket");
		ret = -1;
	}
	
	return ret;
}


