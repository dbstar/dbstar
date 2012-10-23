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
#include "dvbpush_api.h"
#include "mid_push.h"
#include "prodrm20.h"

static int 			s_settingInitFlag = 0;

static char			s_service_id[32];
static int			s_root_channel;
static char			s_root_push_file[128];
static unsigned int	s_root_push_file_size = 0;
static char			s_data_source[128];
static int			s_prog_data_pid = 0;

static char			s_database_uri[64];
static int			s_debug_level = 0;
static char			s_xml[128];
static char			s_initialize_xml[256];
static char			s_column_res[256];

static dvbpush_notify_t dvbpush_notify = NULL;

/* define some general interface function here */

static void settingDefault_set(void)
{
	memset(s_service_id, 0, sizeof(s_service_id));
	memset(s_root_push_file, 0, sizeof(s_root_push_file));
	memset(s_data_source, 0, sizeof(s_data_source));
	
	strncpy(s_service_id, SERVICE_ID, sizeof(s_service_id)-1);
	s_root_channel = ROOT_CHANNEL;
	strncpy(s_root_push_file, ROOT_PUSH_FILE, sizeof(s_root_push_file)-1);
	s_root_push_file_size = ROOT_PUSH_FILE_SIZE;
	strncpy(s_data_source, DATA_SOURCE, sizeof(s_data_source)-1);
	
	s_prog_data_pid = PROG_DATA_PID_DF;
	
	snprintf(s_database_uri, sizeof(s_database_uri), "%s", DATABASE);
	s_debug_level = 0;
	memset(s_xml, 0, sizeof(s_xml));
	snprintf(s_initialize_xml, sizeof(s_initialize_xml), "%d", INITIALIZE_XML);
	snprintf(s_column_res, sizeof(s_column_res), "%s", LOCALCOLUMN_RES);
	
	return;
}

/*
判断配置项（含有以separator（例如：“=”、“:”）分割的“item——value”组合的一行）是否合法，主要包括以下内容：
1、去除所有的不可显字符，只留下isgraph判断通过的；
2、如果是“#”打头则视为注释，不用解析；
3、有分隔符separator才算有效。
4、buf的数据可能会被改变，不可显字符都将去掉，且如果是有效的配置项，将会用'\0'替换“=”截断buf。
	所以如果需要原始字符串，则应在调用此函数前自行备份。

返回值：相当于配置项中separator后的位置，实际上是strchr(buf, '=')的返回值再加一（跳过“=”，指向value的地址）。
		因此，NULL表示此配置项无效，其他值则表示是value的指针
输出：	函数返回时，buf指向分隔符前的item
*/
char *setting_item_value(char *buf, unsigned int buf_len, char separator)
{
	if(NULL==buf || 0==buf_len || '#'==buf[0]){
		DEBUG("this line is ignored as explain\n");
		return NULL;
	}
//	DEBUG("read line: %s\n", buf);
	unsigned int i=0, j = 0;
	for(i=0; i<buf_len; i++){
#if 0
		if( (buf[i]>'0'&&buf[i]<'9')
			||(buf[i]>'A'&&buf[i]<'Z')
			||(buf[i]>'a'&&buf[i]<'z')
			||'_'==buf[i] || '-'==buf[i] || '@'==buf[i] )
#else
		if( isgraph(buf[i]) )		//or check it between 33('!') and 126('~')
#endif
		{
			if(j!=i)
				buf[j] = buf[i];
			j++;
		}
	}
	buf[j] = '\0';
//	DEBUG("buf: %s\n", buf);
	if('#'==buf[0]){
		DEBUG("ignore a line because of explain\n");
		return NULL;
	}
	char *p_value = strchr(buf, separator);
	if(p_value){
		*p_value = '\0';
		p_value ++;
		return p_value;
	}
	else
		return NULL;
}

int setting_init(void)
{
	if(1==s_settingInitFlag)
		return 0;
		
	FILE* fp;
	char tmp_buf[256];
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
		p_value = setting_item_value(tmp_buf, strlen(tmp_buf), ':');
		if(NULL!=p_value)
		{
			//DEBUG("setting item: %s, value: %s\n", tmp_buf, p_value);
			if(strlen(tmp_buf)>0 && strlen(p_value)>0){
				if(0==strcmp(tmp_buf, "server_id"))
					strncpy(s_service_id, p_value, sizeof(s_service_id)-1);
				else if(0==strcmp(tmp_buf, "root_channel"))
					s_root_channel = atoi(p_value);
				else if(0==strcmp(tmp_buf, "root_push_file"))
					strncpy(s_root_push_file, p_value, sizeof(s_root_push_file)-1);
				else if(0==strcmp(tmp_buf, "root_push_file_size"))
					s_root_push_file_size = atoi(p_value);
				else if(0==strcmp(tmp_buf, "data_source"))
					strncpy(s_data_source, p_value, sizeof(s_data_source)-1);
				else if(0==strcmp(tmp_buf, "prog_data_pid"))
					s_prog_data_pid = atoi(p_value);
				else if(0==strcmp(tmp_buf, "dbstar_database"))
					strncpy(s_database_uri, p_value, sizeof(s_database_uri)-1);
				else if(0==strcmp(tmp_buf, "dbstar_debug_level"))
					s_debug_level = atoi(p_value);
				else if(0==strcmp(tmp_buf, "parse_xml"))	/* this xml only for parse testing */
					strncpy(s_xml, p_value, sizeof(s_xml)-1);
				else if(0==strcmp(tmp_buf, "initialize_xml"))
					strncpy(s_initialize_xml, p_value, sizeof(s_initialize_xml)-1);
				else if(0==strcmp(tmp_buf, "localcolumn_res"))
					strncpy(s_column_res, p_value, sizeof(s_column_res)-1);
			}
		}
		memset(tmp_buf, 0, sizeof(tmp_buf));
	}
	fclose(fp);
	DEBUG("init settings OK\n");

	s_settingInitFlag = 1;
	return 0;
}

int setting_uninit()
{
	s_settingInitFlag = 0;
	return 0;
}

/*
 检查指定的产品id是否在特殊产品之列。
*/
int special_productid_check(char *productid)
{
	if(NULL==productid)
		return -1;

	if(0==strcmp(productid, "special_product_001"))
		return 1;
	else
		return 0;
}

int root_channel_get(void)
{
	return s_root_channel;
}

int root_push_file_get(char *filename, unsigned int len)
{
	if(NULL==filename || 0==len)
		return -1;

	strncpy(filename, s_root_push_file, len);
	
	return 0;
}

int root_push_file_size_get(void)
{
	return s_root_push_file_size;
}

int data_source_get(char *data_source, unsigned int len)
{
	if(NULL==data_source || 0==len)
		return -1;
	
	strncpy(data_source, s_data_source, len);
	return 0;
}

int prog_data_pid_get(void)
{
	return s_prog_data_pid;
}

int database_uri_get(char *database_uri, unsigned int size)
{
	if(NULL==database_uri || 0==size)
		return -1;
	
	strncpy(database_uri, s_database_uri, size);
	return 0;
}

int debug_level_get(void)
{
	return s_debug_level;
}

int parse_xml_get(char *xml_uri, unsigned int size)
{
	if(NULL==xml_uri || 0==size)
		return -1;
	
	strncpy(xml_uri, s_xml, size);
	return 0;
}

int initialize_xml_get()
{
	return atoi(s_initialize_xml);
}

int column_res_get(char *column_res, unsigned int uri_size)
{
	if(NULL==column_res || 0==uri_size)
		return -1;
	
	strncpy(column_res, s_column_res, uri_size);
	return 0;
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

int special_product_id_get(char *id, unsigned int id_size)
{
	/*
	临时测试使用
	*/
	return snprintf(id, id_size, "1003");
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


/*
 通过jni提供给UI使用的函数，UI可以由此设置向上发送消息的回调函数。
 实际调用参见dvbpush_jni.c
*/
int dvbpush_register_notify(void *func)
{
	DEBUG("dvbpush_register_notify\n");
	if (func != NULL)
		dvbpush_notify = (dvbpush_notify_t)func;

	return 0;
}

/*
 底层通过此函数发送消息到上层。
 
*/
int msg_send2_UI(int type, char *msg, int len)
{
	if (dvbpush_notify != NULL){
		return dvbpush_notify(type, msg, len);
	}
	else{
		DEBUG("there is no callback to send msg\n");
		return -1;
	}
}

int smartcard_sn_get();
int dvbpush_command(int cmd, char **buf, int *len)
{
	int ret = 0;

	DEBUG("dvbpush_command(cmd=%d)\n", cmd);
	switch (cmd) {
		case CMD_DVBPUSH_GETINFO_START:
			dvbpush_getinfo_start();
			break;
		case CMD_DVBPUSH_GETINFO:
			dvbpush_getinfo(buf, (unsigned int *)len);
			break;
		case CMD_DVBPUSH_GETINFO_STOP:
			dvbpush_getinfo_stop();
			break;
		
		case CMD_UPGRADE_CANCEL:
			DEBUG("CMD_UPGRADE_CANCEL\n");
			break;
		case CMD_UPGRADE_CONFIRM:
			DEBUG("CMD_UPGRADE_CONFIRM\n");
			break;
		case CMD_UPGRADE_TIMEOUT:
			DEBUG("CMD_UPGRADE_TIMEOUT\n");
			break;
		
		default:
			break;
	}

	return ret;
}

static char s_smartcard_sn[CDCA_MAXLEN_SN+1];
int smartcard_sn_get()
{
	DEBUG("to read smartcard sn, space=%d\n", sizeof(s_smartcard_sn));
	
	memset(s_smartcard_sn, 0, sizeof(s_smartcard_sn));
	CDCA_U16 ret = CDCASTB_GetCardSN(s_smartcard_sn);
	if(CDCA_RC_OK==ret){
		DEBUG("read smartcard sn OK: %s\n", s_smartcard_sn);
		return 0;
	}
	else if(CDCA_RC_POINTER_INVALID==ret){
		DEBUG("pointer to read smartcard is invalid\n");
		return -1;
	}
	else if(CDCA_RC_CARD_INVALID==ret){
		DEBUG("there is none or invalid smartcard: %s\n", s_smartcard_sn);
		return -1;
	}
	
	return 0;
}
