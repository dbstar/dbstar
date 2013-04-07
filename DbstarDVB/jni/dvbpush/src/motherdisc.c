#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>

#include "common.h"
#include "mid_push.h"
#include "xmlparser.h"
#include "sqlite.h"
#include "porting.h"
#include "push.h"
#include "dvbpush_api.h"

static int s_motherdisc_processing_status = 0;
static int s_receive_status = 0;

int receive_status_get()
{
	return s_receive_status;
}

static int parse_progs_cb(char **result, int row, int column, void *receiver, unsigned int receiver_size)
{
	DEBUG("sqlite callback, row=%d, column=%d, receiver addr=%p, receive_size=%u\n", row, column, receiver,receiver_size);
	if(row<1){
		DEBUG("no record in table, return\n");
		return 0;
	}
// ReceiveType,DescURI
	int i = 0;
	PUSH_XML_FLAG_E push_flag = PUSH_XML_FLAG_UNDEFINED;
	RECEIVETYPE_E recv_type = -1;
	int product_id = -1;
	
	for(i=1;i<row+1;i++)
	{
		s_receive_status = RECEIVESTATUS_FINISH;
		recv_type = atoi(result[i*column]);
		
		if(RECEIVETYPE_PUBLICATION==recv_type || RECEIVETYPE_PREVIEW==recv_type){
			push_flag = PUBLICATION_XML;
		}
		else if(RECEIVETYPE_COLUMN==recv_type){
			push_flag = COLUMN_XML;
		}
		else if(RECEIVETYPE_SPRODUCT==recv_type){
			push_flag = SPRODUCT_XML;
		}
		else{
			DEBUG("%d(%s) can not be distinguish\n", recv_type, result[i*column+1]);
			push_flag = PUSH_XML_FLAG_UNDEFINED;
		}

		if(PUSH_XML_FLAG_UNDEFINED!=push_flag){
			product_id = atoi(result[i*column+2]);
			s_receive_status = RECEIVESTATUS_FINISH;

			// 确保在解析Publications.xml之前，明确得到s_receive_status，因为在解析入库时需要判断
			if(0==parse_xml(result[i*column+1], SERVICE_XML, NULL)){
				DEBUG("parse %s success\n", result[i*column+1]);
			}
			else{
				DEBUG("parse %s failed\n", result[i*column+1]);
			}
		}
	}
	
	return 0;
}

/*
 系统初始化和Initialize.xml更新时需要刷新注册，但Initialize.xml由push系统自己注册。
 regist_flag
 	0 means unregist
 	1 means resgist
*/
static int parse_progs()
{
	int ret = -1;
	char sqlite_cmd[1024];
	int (*sqlite_callback)(char **, int, int, void *, unsigned int) = parse_progs_cb;
	int resgist_action = 0;
	
	snprintf(sqlite_cmd,sizeof(sqlite_cmd),"SELECT ReceiveType,DescURI,productID FROM ProductDesc;");
	ret = sqlite_read(sqlite_cmd, (void *)(&resgist_action), sizeof(resgist_action), sqlite_callback);

	return ret;
}

int motherdisc_processing()
{
	return s_motherdisc_processing_status;
}

/*
 整个母盘初始化过程应当是独占的，避免在母盘初始化过程中由于插拔智能卡或插拔网线等导致逻辑混乱
 return :
 0: 	nomarl
 -1:	failed
 1:		this is mother disc, perhaps reboot the system.
*/
int motherdisc_init()
{
	struct stat filestat;
	char direct_uri[1024];
	int ret = 0;
	
	snprintf(direct_uri,sizeof(direct_uri),"%s/%s", push_dir_get(),MOTHERDISC_XML_URI);
	
	// check ContentDelivery.xml for mother disc
	int stat_ret = stat(direct_uri, &filestat);
	if(0==stat_ret){
		s_motherdisc_processing_status = 1;
		
		DEBUG("%s is exist, initialize disc starting...\n", MOTHERDISC_XML_URI);
		msg_send2_UI(MOTHER_DISC_INITIALIZE_START, NULL, 0);
		ret = 0;
	}
	else{
		ERROROUT("can not stat(%s)\n", direct_uri);
		DEBUG("this is not a mother disc\n");
	
		s_motherdisc_processing_status = 0;
		ret = -1;
	}
	
	return ret;
}

int motherdisc_process()
{
	char direct_uri[1024];
	char xml_uri[512];
	char sqlite_cmd[1024];
	char new_uri_motherdisc_xml[1024];
	int ret = 0;
	
	if(1==s_motherdisc_processing_status){
		DEBUG("%s, process motherdisc...\n", MOTHERDISC_XML_URI);
		
		snprintf(sqlite_cmd,sizeof(sqlite_cmd), "DELETE FROM Initialize;");
		sqlite_execute(sqlite_cmd);
		
		// parse Initialize.xml
		if(0==parse_xml(initialize_uri_get(), INITIALIZE_XML, NULL)){
			
			// parse Service.xml
			snprintf(sqlite_cmd,sizeof(sqlite_cmd),"SELECT URI FROM Initialize WHERE PushFlag='%d';", SERVICE_XML);
			memset(xml_uri,0,sizeof(xml_uri));
			if(0==str_sqlite_read(xml_uri,sizeof(xml_uri),sqlite_cmd) && 0==parse_xml(xml_uri, SERVICE_XML, NULL)){
				
				// parse GuideList.xml	
				snprintf(sqlite_cmd,sizeof(sqlite_cmd),"SELECT URI FROM Initialize WHERE PushFlag='%d';", GUIDELIST_XML);
				memset(xml_uri,0,sizeof(xml_uri));
				if(0==str_sqlite_read(xml_uri,sizeof(xml_uri),sqlite_cmd) && 0==parse_xml(xml_uri, GUIDELIST_XML, NULL)){
					
					// parse ProductDesc.xml
					// 解析ProductDesc.xml时判断拒绝的节目也入库
					
					snprintf(sqlite_cmd,sizeof(sqlite_cmd), "DELETE FROM ProductDesc;");
					sqlite_execute(sqlite_cmd);
					
					snprintf(sqlite_cmd,sizeof(sqlite_cmd),"SELECT URI FROM Initialize WHERE PushFlag='%d';", PRODUCTDESC_XML);
					memset(xml_uri,0,sizeof(xml_uri));
					if(0==str_sqlite_read(xml_uri,sizeof(xml_uri),sqlite_cmd) && 0==parse_xml(xml_uri, PRODUCTDESC_XML, NULL)){
						DEBUG("parse xmls for mother disc initialize finish, waiting for programs parsing...\n");
						
						parse_progs();
						msg_send2_UI(MOTHER_DISC_INITIALIZE_SUCCESS, NULL, 0);
						DEBUG("parse publications for mother disc initialize finished\n");
						
						ret = 0;
					}
					else{
						DEBUG("read URI or parse for %d failed\n",PRODUCTDESC_XML);
						msg_send2_UI(MOTHER_DISC_INITIALIZE_FAILED, NULL, 0);
						ret = -1;
					}
				}
				else{
					DEBUG("read URI or parse for %d failed\n",GUIDELIST_XML);
					msg_send2_UI(MOTHER_DISC_INITIALIZE_FAILED, NULL, 0);
					ret = -1;
				}
			}
			else{
				DEBUG("read URI or parse for %d failed\n",SERVICE_XML);
				msg_send2_UI(MOTHER_DISC_INITIALIZE_FAILED, NULL, 0);
				ret = -1;
			}
		}
		else{
			DEBUG("parse %d for motherdisc init failed\n", INITIALIZE_XML);
			msg_send2_UI(MOTHER_DISC_INITIALIZE_FAILED, NULL, 0);
			ret = -1;
		}
		
		
	}
	else{
		DEBUG("this is not a mother disc\n");
		ret = -1;
	}
	
	snprintf(direct_uri,sizeof(direct_uri),"%s/%s", push_dir_get(),MOTHERDISC_XML_URI);
	snprintf(new_uri_motherdisc_xml,sizeof(new_uri_motherdisc_xml),"__%s_PROCESSED__",direct_uri);
	
	if(0!=rename(direct_uri,new_uri_motherdisc_xml)){
		ERROROUT("rename %s to %s failed\n", direct_uri, new_uri_motherdisc_xml);
		remove(new_uri_motherdisc_xml);
	}
	
	s_motherdisc_processing_status = 0;
	
	return ret;
}
