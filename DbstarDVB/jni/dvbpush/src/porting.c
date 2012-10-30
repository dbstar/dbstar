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
#include <time.h>

#include "common.h"
#include "dvbpush_api.h"
#include "mid_push.h"
#include "softdmx.h"
#include "bootloader.h"
#include "xmlparser.h"
#include "sqlite.h"
#include "prodrm20.h"
#include "multicast.h"

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

static int drm_date_convert(unsigned int drm_date, char *date_str, unsigned int date_str_size);

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

	if(0==strcmp(productid, "1"))
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
	DEBUG("type: %d, msg: %s, len: %d\n", type, msg, len);
	if (dvbpush_notify != NULL){
		return dvbpush_notify(type, msg, len);
	}
	else{
		DEBUG("there is no callback to send msg\n");
		return -1;
	}
}


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
		case CMD_NETWORK_CONNECT:
			push_rely_condition_set(RELY_CONDITION_NET);
			net_rely_condition_set(RELY_CONDITION_NET);
			break;
		case CMD_NETWORK_DISCONNECT:
			push_rely_condition_set(0-RELY_CONDITION_NET);
			net_rely_condition_set(0-RELY_CONDITION_NET);
			break;
		case CMD_DISK_MOUNT:
			push_rely_condition_set(RELY_CONDITION_HD);
			net_rely_condition_set(RELY_CONDITION_HD);
			break;
		case CMD_DISK_UNMOUNT:
			push_rely_condition_set(0-RELY_CONDITION_HD);
			net_rely_condition_set(0-RELY_CONDITION_HD);
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


static void upgrade_info_refresh(char *info_name, char *info_value)
{
	char sqlite_cmd[512];
	char stbinfo[128];
	
	int (*sqlite_cb)(char **, int, int, void *) = str_read_cb;
	snprintf(sqlite_cmd,sizeof(sqlite_cmd),"SELECT Value FROM Global WHERE Name='%s';", info_name);
	
	memset(stbinfo, 0, sizeof(stbinfo));
	int ret_sqlexec = sqlite_read(sqlite_cmd, stbinfo, sqlite_cb);
	if(ret_sqlexec<=0 || strcmp(stbinfo, info_value)){
		DEBUG("replace %s as %s to table 'Global'\n", info_name, info_value);
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Global(Name,Value,Param) VALUES('%s','%s','');",
			info_name,info_value);
		sqlite_execute(sqlite_cmd);
	}
	else
		DEBUG("same %s: %s\n", info_name, info_value);
}

void upgrade_info_init()
{
	unsigned char mark = 0;
	LoaderInfo_t out;
	
	memset(&out, 0, sizeof(out));
	if(0==get_loader_message(&mark, &out))
	{
		DEBUG("read loader msg: %d", mark);
		if(0!=mark){
			set_loader_reboot_mark(0);
		}
		
		char tmpinfo[128];
		
		snprintf(tmpinfo, sizeof(tmpinfo), "%u%u", out.stb_id_h, out.stb_id_l);
		upgrade_info_refresh(GLB_NAME_STBID, tmpinfo);
		
		snprintf(tmpinfo, sizeof(tmpinfo), "%03d.%03d.%03d.%03d", out.hardware_version[0],out.hardware_version[1],out.hardware_version[2],out.hardware_version[3]);
		upgrade_info_refresh(GLB_NAME_HARDWARE_VERSION, tmpinfo);
		
		snprintf(tmpinfo, sizeof(tmpinfo), "%03d.%03d.%03d.%03d", out.software_version[0],out.software_version[1],out.software_version[2],out.software_version[3]);
		upgrade_info_refresh(GLB_NAME_SOFTWARE_VERSION, tmpinfo);
	}
	else
		DEBUG("get loader message failed\n");
}

static void drm_errors(char *fun, CDCA_U16 ret)
{
	switch(ret){
		case CDCA_RC_CARD_INVALID:
			DEBUG("[%s] none or invalid smart card\n", fun);
			break;
		case CDCA_RC_POINTER_INVALID:
			DEBUG("[%s] null pointor\n", fun);
			break;
		case CDCA_RC_DATA_NOT_FIND:
			DEBUG("[%s] no such operator\n", fun);
			break;
		default:
			DEBUG("[%s] such return can not distinguished: %d\n", fun, ret);
			break;
	}
}

#define DRMENTITLE	"DRMProduct"
int drm_info_init()
{
	if(0==drm_init()){
		char		smartcard_sn[CDCA_MAXLEN_SN+1];
		CDCA_U16	wArrTvsID[CDCA_MAXNUM_OPERATOR];
		CDCA_U8		index = 0;
		CDCA_U16	j = 0;
		SCDCAEntitles Entitle;
		CDCA_U16	ret = 0;
		CDCA_U16	OperatorCount = 0;
		SCDCAOperatorInfo OperatorInfo;
		char		date_str[32];
		char		sqlite_cmd[256];
		
		memset(smartcard_sn, 0, sizeof(smartcard_sn));
		
		ret = CDCASTB_GetCardSN(smartcard_sn);
		if(CDCA_RC_OK==ret){
			DEBUG("read smartcard sn OK: %s\n", smartcard_sn);
		}
		else{
			drm_errors("CDCASTB_GetCardSN", ret);
			return -1;
		}
		
		memset(wArrTvsID, 0, sizeof(wArrTvsID));
		ret = CDCASTB_GetOperatorIds(wArrTvsID);
		if(CDCA_RC_OK==ret){
			for(index=0;index<CDCA_MAXNUM_OPERATOR;index++){
				if(0==wArrTvsID[index]){
					DEBUG("OperatorID list end\n");
					break;
				}
				else
					DEBUG("OperatorID: %d\n", wArrTvsID[index]);
			}
		}
		else{
			drm_errors("CDCASTB_GetOperatorIds", ret);
			return -1;
		}
		
		OperatorCount = index;
		for(j=0;j<OperatorCount;j++){
			ret = CDCASTB_GetOperatorInfo(wArrTvsID[j], &OperatorInfo);
			if(CDCA_RC_OK==ret){
				DEBUG("[%d]: %s\n", wArrTvsID[j], OperatorInfo.m_szTVSPriInfo);
			}
			else
				drm_errors("CDCASTB_GetServiceEntitles", ret);
			
			memset(&Entitle, 0, sizeof(Entitle));
			ret = CDCASTB_GetServiceEntitles(wArrTvsID[j],&Entitle);
			if(CDCA_RC_OK==ret){
				DEBUG("Operator %d has %d entitles\n", wArrTvsID[j],Entitle.m_wProductCount);
				sqlite_transaction_begin();
				for(index=0;index<Entitle.m_wProductCount;index++){
					DEBUG("\n[%d]Product id: %lu\n", wArrTvsID[j],Entitle.m_Entitles[index].m_dwProductID);
					DEBUG("[%d]Product Expire: %d-%d\n\n", wArrTvsID[j],Entitle.m_Entitles[index].m_tBeginDate, Entitle.m_Entitles[index].m_tExpireDate);
					memset(date_str, 0, sizeof(date_str));
					if(0==drm_date_convert(Entitle.m_Entitles[index].m_tExpireDate, date_str, sizeof(date_str))){
						snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Global(Name,Value,Param) VALUES('%lu','%s','%s');", Entitle.m_Entitles[index].m_dwProductID, date_str, DRMENTITLE);
						sqlite_transaction_exec(sqlite_cmd);
					}
				}
				if(index>0)
					sqlite_transaction_end(1);
				else
					sqlite_transaction_end(0);
			}
			else{
				drm_errors("CDCASTB_GetServiceEntitles", ret);
				return -1;
			}
		}
	}
	else
		DEBUG("drm init failed\n");
	
	return -1;
}

/*
struct tm{
int tm_sec;
int tm_min;
int tm_hour;
int tm_mday;
int tm_mon;//代表目前月份，从一月算起，范围从0-11
int tm_year;
int tm_wday;
int tm_yday;
int tm_isdst;
};
*/
/*
void drm_date_time_test()
{
//	time_t timep;
//	struct tm *p;
//	time(&timep);
//	printf(“time() : %d n”,timep);
//	p=localtime(&timep);
//	timep = mktime(p);
//	printf(“time()->localtime()->mktime():%dn”,timep);
	
	time_t sec_2000;
	struct tm *p;
	struct tm tm_2000;
	memset(&tm_2000, 0, sizeof(struct tm));
	tm_2000.tm_mday = 1;
	tm_2000.tm_mon = 0;
	tm_2000.tm_year = (2000-1900);
	sec_2000 = mktime(&tm_2000);
	p = localtime(&sec_2000);
	DEBUG("%dYear %dMon %dDay: %dHour %dMin %dSec\n", 1900+p->tm_year, 1+p->tm_mon, p->tm_mday, p->tm_hour, p->tm_min, p->tm_sec);
	sec_2000 += (13879*24*60*60);
	p = localtime(&sec_2000);
	DEBUG("%dYear %dMon %dDay: %dHour %dMin %dSec\n", 1900+p->tm_year, 1+p->tm_mon, p->tm_mday, p->tm_hour, p->tm_min, p->tm_sec);
	
	time_t sec_today;
	struct tm tm_today;
	memset(&tm_today, 0, sizeof(struct tm));
	tm_today.tm_sec = 27;
	tm_today.tm_min = 59;
	tm_today.tm_hour = 14;
	tm_today.tm_mday = 28;
	tm_today.tm_mon = 9;
	tm_today.tm_year = (2012-1900);
	sec_today = mktime(&tm_today);
	p = localtime(&sec_today);
	DEBUG("%dYear %dMon %dDay: %dHour %dMin %dSec\n", 1900+p->tm_year, 1+p->tm_mon, p->tm_mday, p->tm_hour, p->tm_min, p->tm_sec);
	
	sec_today += (7*24*60*60);
	p = localtime(&sec_today);
	DEBUG("%dYear %dMon %dDay: %dHour %dMin %dSec\n", 1900+p->tm_year, 1+p->tm_mon, p->tm_mday, p->tm_hour, p->tm_min, p->tm_sec);
}
*/

/*
 参考drm_date_time_test实现，将drm中的date转换为年月日。date为自2000年1月1日开始的天数，详见drm移植文档。
*/
static int drm_date_convert(unsigned int drm_date, char *date_str, unsigned int date_str_size)
{
	if(NULL==date_str || 0==date_str_size){
		DEBUG("invalid args\n");
		return -1;
	}
	
	time_t sec_appointed;
	struct tm tm_appointed;
	struct tm *p;
	
	memset(&tm_appointed, 0, sizeof(struct tm));
	tm_appointed.tm_mday = 1;
	tm_appointed.tm_mon = 0;
	tm_appointed.tm_year = (2000-1900);
	sec_appointed = mktime(&tm_appointed);
	sec_appointed += (drm_date*24*60*60);
	p = localtime(&sec_appointed);
	snprintf(date_str, date_str_size, "%d/%d/%d", 1900+p->tm_year, 1+p->tm_mon, p->tm_mday);
	
	return 0;
}
