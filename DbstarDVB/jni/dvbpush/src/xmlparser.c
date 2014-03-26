#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <errno.h>
#include <time.h>
#include <libxml/xmlmemory.h>
#include <libxml/parser.h>
#include <pthread.h>
#include <unistd.h>
#include <ctype.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>


#include "common.h"
#include "xmlparser.h"
#include "sqlite3.h"
#include "sqlite.h"
#include "mid_push.h"
#include "multicast.h"
#include "softdmx.h"
#include "porting.h"
#include "dvbpush_api.h"
#include "motherdisc.h"

static pthread_mutex_t mtx_parse_xml = PTHREAD_MUTEX_INITIALIZER;
static int s_column_SequenceNum = 0;
static int s_detect_valid_productID = 0;
static int s_preview_publication = 0;
static unsigned long long s_recv_totalsize_sum = 0LL;

/*
 初始化函数，读取Global表中的ServiceID，初始化push的根目录供UI使用。
*/
int xmlparser_init(void)
{	
	return 0;
}

int xmlparser_uninit(void)
{
	return 0;
}

static int xmluri_get(int pushflag, char *xmluri, unsigned int urisize)
{
	if(NULL==xmluri || 0==urisize){
		DEBUG("can not get xml uri with NULL buffer\n");
		return -1;
	}
	
	char sqlite_cmd[512];
	int (*sqlite_cb)(char **, int, int, void *, unsigned int) = str_read_cb;
	sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"SELECT URI FROM Initialize WHERE PushFlag='%d';", pushflag);

	int ret_sqlexec = sqlite_read(sqlite_cmd, xmluri, sizeof(xmluri), sqlite_cb);
	if(ret_sqlexec<=0){
		DEBUG("read no uri from db for %d\n", pushflag);
		return -1;
	}
	else
		return 0;
}

/*
 向全局信息表Global中插入或更新数据
 此函数调用处应当封装为事务。
*/
static int global_insert(DBSTAR_GLOBAL_S *p)
{
	if(NULL==p || 0==strlen(p->Name) || 0==strlen(p->Value)){
		DEBUG("invalid arguments\n");
		return -1;
	}
	
	char sqlite_cmd[2048];
	
	sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Global(Name,Value,Param) VALUES('%q','%q','%q');",
		p->Name, p->Value, p->Param);
	
	return sqlite_transaction_exec(sqlite_cmd);
}

/*
 向字符串资源表ResStr中插入数据。
 由于Res表都是依存于宿主表的寄生表，因此它们自身不能独立封装为事务，应为某事务的一部分。
*/
static int resstr_insert(DBSTAR_RESSTR_S *p)
{
	if(NULL==p){
		DEBUG("invalid arguments\n");
		return -1;
	}
	char sqlite_cmd[8192];
	
	if(0==strcmp("chi",p->StrLang)){
		DEBUG("shit, here should be 'cho', not 'chi'\n");
		snprintf(p->StrLang,sizeof(p->StrLang),"%s",CURLANGUAGE_DFT);
	}
	
#if 0
	char tmp_strvalue[8192];
	int i = 0;
	unsigned int check_pin = 0;	// 指示当前检查到哪个位置
	char *p_ESC = NULL;
	
// 不用while循环，防止意外引起死循环
	for(i=0;i<512;i++){
		p_ESC = strchr(p->StrValue+check_pin,'\'');
		if(p_ESC){
			snprintf(tmp_strvalue,sizeof(tmp_strvalue),"%s",p_ESC);
			check_pin = p_ESC-(p->StrValue);
//			PRINTF("p_ESC[%d]:%s,check_pin=%d,sizeof(p->StrValue)-check_pin=%d\n",i,p_ESC,check_pin,sizeof(p->StrValue)-check_pin);
			snprintf(p_ESC,sizeof(p->StrValue)-check_pin,"\'%s",tmp_strvalue);
			check_pin += 2;
			PRINTF("has ESC in StrValue, translate as (%s)\n",p->StrValue);
			
			if(check_pin>=strlen(p->StrValue))
				break;
		}
		else
			break;
	}
	
	if(512==i)
		PRINTF("fuck, there are too much ESC\n");
	
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ServiceID,ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s','%s');",
		p->ServiceID, p->ObjectName, p->EntityID, p->StrLang, p->StrName, p->StrValue, p->Extension);
#else
	// use sqlite3_snprintf instead of snprintf, Note that the order of the first two parameters is reversed from snprintf(). This is an historical accident that cannot be fixed without breaking backwards compatibility.
	
	sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ServiceID,ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q','%q');",
		p->ServiceID, p->ObjectName, p->EntityID, p->StrLang, p->StrName, p->StrValue, p->Extension);
#endif

	return sqlite_transaction_exec(sqlite_cmd);

}

/*
 向初始化表Initialize插入xml文件的信息，既有可能是解析Initialize.xml时插入，也有可能是解析每个xml时插入版本信息
*/
static int xmlinfo_insert(DBSTAR_XMLINFO_S *xmlinfo)
{
	if(NULL==xmlinfo)
		return -1;
	
//	if(PUBLICATION_XML==atoi(xmlinfo->PushFlag) || COLUMN_XML==atoi(xmlinfo->PushFlag) || SPRODUCT_XML==atoi(xmlinfo->PushFlag))
//	{
//		PRINTF("this xml [%s] is controled by column 'Parsed' in table ProductDesc, don't insert to table Initialize\n",xmlinfo->PushFlag);
//		return 0;
//	}
	
	DEBUG("%s,%s,%s,%s,%s,%s,%s\n", xmlinfo->PushFlag, xmlinfo->ServiceID, xmlinfo->XMLName, xmlinfo->Version, xmlinfo->StandardVersion, xmlinfo->URI, xmlinfo->ID);
	
	if(strlen(xmlinfo->Version)>0 || strlen(xmlinfo->StandardVersion)>0 || strlen(xmlinfo->URI)>0){
		char sqlite_cmd[2048];
		
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Initialize(PushFlag,ServiceID,ID) VALUES('%q','%q','%q');", xmlinfo->PushFlag, xmlinfo->ServiceID, xmlinfo->ID);
		sqlite_transaction_exec(sqlite_cmd);
		
		if(strlen(xmlinfo->XMLName)>0){
			if(PUBLICATION_XML==strtol(xmlinfo->PushFlag,NULL,0))
				sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"UPDATE Initialize SET XMLName='%q' WHERE PushFlag='%q' AND ID='%q';", xmlinfo->XMLName, xmlinfo->PushFlag, xmlinfo->ID);
			else
				sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"UPDATE Initialize SET XMLName='%q' WHERE PushFlag='%q';", xmlinfo->XMLName, xmlinfo->PushFlag);
			sqlite_transaction_exec(sqlite_cmd);
		}
		if(strlen(xmlinfo->Version)>0){
			if(PUBLICATION_XML==strtol(xmlinfo->PushFlag,NULL,0))
				sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"UPDATE Initialize SET Version='%q' WHERE PushFlag='%q' AND ID='%q';", xmlinfo->Version, xmlinfo->PushFlag, xmlinfo->ID);
			else
				sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"UPDATE Initialize SET Version='%q' WHERE PushFlag='%q';", xmlinfo->Version, xmlinfo->PushFlag);
			sqlite_transaction_exec(sqlite_cmd);
		}
		if(strlen(xmlinfo->StandardVersion)>0){
			if(PUBLICATION_XML==strtol(xmlinfo->PushFlag,NULL,0))
				sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"UPDATE Initialize SET StandardVersion='%q' WHERE PushFlag='%q' AND ID='%q';", xmlinfo->StandardVersion, xmlinfo->PushFlag, xmlinfo->ID);
			else
				sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"UPDATE Initialize SET StandardVersion='%q' WHERE PushFlag='%q';", xmlinfo->StandardVersion, xmlinfo->PushFlag);
			sqlite_transaction_exec(sqlite_cmd);
		}
		if(strlen(xmlinfo->URI)>0){
			if(PUBLICATION_XML==strtol(xmlinfo->PushFlag,NULL,0))
				sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"UPDATE Initialize SET URI='%q' WHERE PushFlag='%q' AND ID='%q';", xmlinfo->URI, xmlinfo->PushFlag, xmlinfo->ID);
			else
				sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"UPDATE Initialize SET URI='%q' WHERE PushFlag='%q';", xmlinfo->URI, xmlinfo->PushFlag);
			sqlite_transaction_exec(sqlite_cmd);
		}
	}
	
	return 0;
}

/*
 向业务表Service中插入业务信息，实际上本表只有一条记录
 如果需要支持多业务时，需要处理Service表中Status字段的值
*/
static int service_insert(DBSTAR_SERVICE_S *p)
{
	if(NULL==p || 0==strlen(p->ServiceID)){
		DEBUG("invalid arguments\n");
		return -1;
	}
	
	char sqlite_cmd[2048];
	
	SERVICE_STATUS_E service_status = SERVICE_STATUS_EFFECT;
#if 0
	if(0==strcmp(serviceID_get(),p->ServiceID)){
		DEBUG("service id %s is mine\n", p->ServiceID);
		service_status = SERVICE_STATUS_EFFECT;
	}
	else{
		DEBUG("service id %s is not mine(%s)\n", p->ServiceID, serviceID_get());
		service_status = SERVICE_STATUS_INVALID;
	}
#else
	sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"DELETE FROM Service;");
	sqlite_transaction_exec(sqlite_cmd);
#endif
	
	sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Service(ServiceID,RegionCode,OnlineTime,OfflineTime,Status) VALUES('%q','%q','%q','%q','%d');",
		p->ServiceID,p->RegionCode,p->OnlineTime,p->OfflineTime,service_status);
	
	return sqlite_transaction_exec(sqlite_cmd);
}

/*
 向产品表Product插入产品信息
*/
static int product_insert(DBSTAR_PRODUCT_S *p)
{
	if(NULL==p || 0==strlen(p->ProductID)){
		DEBUG("invalid arguments\n");
		return -1;
	}
	
	char sqlite_cmd[4096];

#if 0
// 新push只注册了属于自己的service，所以不用再判断serviceID	
	if(0==strcmp(serviceID_get(),p->ServiceID))

// 2013-3-18 16:07 不刷新ProductDesc表了。实际上Service.xml中的产品无用，判断接收依据的是智能卡信息。
	{
		DEBUG("product %s in service %s is mine, receive its publications\n", p->ProductID,p->ServiceID);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"UPDATE ProductDesc SET ReceiveStatus='%d',FreshFlag=1 where productID='%q' AND ReceiveStatus='%d';",RECEIVESTATUS_WAITING,p->ProductID,RECEIVESTATUS_REJECT);
		sqlite_transaction_exec(sqlite_cmd);
	}
#endif
	
	sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Product(ServiceID,ProductID,ProductType,Flag,OnlineDate,OfflineDate,IsReserved,Price,CurrencyType) VALUES('%q','%q','%q','%q','%q','%q','%q','%q','%q');",
		p->ServiceID,p->ProductID,p->ProductType,p->Flag,p->OnlineDate,p->OfflineDate,p->IsReserved,p->Price,p->CurrencyType);
	
	return sqlite_transaction_exec(sqlite_cmd);
}

/*
 Column.xml到来时，删除旧表中非预置栏目，所以此处不用考虑UPDATE
*/
static int column_insert(DBSTAR_COLUMN_S *ptr)
{
	if(NULL==ptr || 0==strlen(ptr->ColumnID)){
		DEBUG("invalid arguments\n");
		return -1;
	}
	
	char *p_slash = NULL;
	char from_file[256];
	char to_file[256];
	char cmd[4096];
	
	if(0==strlen(ptr->ParentID))
		snprintf(ptr->ParentID, sizeof(ptr->ParentID), "-1");
	
	s_column_SequenceNum++;
	
	p_slash = strrchr(ptr->ColumnIcon_losefocus,'/');
	if(p_slash)
		p_slash++;
	else
		p_slash = ptr->ColumnIcon_losefocus;
	
	snprintf(from_file,sizeof(from_file),"%s/%s", push_dir_get(),ptr->ColumnIcon_losefocus);
	snprintf(to_file,sizeof(to_file),"%s/%s",column_res_get(),p_slash);
	
	if(strlen(ptr->ColumnIcon_losefocus)>0 && 0==fcopy_c(from_file,to_file)){
		DEBUG("copy %s to %s success\n",from_file,to_file);
		sqlite3_snprintf(sizeof(cmd),cmd,"REPLACE INTO Column(ServiceID,ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,ColumnIcon_spare,SequenceNum) VALUES('%q','%q','%q','%q','%q','%q','%q','%q','%q',%d);",
			ptr->ServiceID,ptr->ColumnID,ptr->ParentID,ptr->Path,ptr->ColumnType,p_slash,ptr->ColumnIcon_getfocus,ptr->ColumnIcon_onclick,ptr->ColumnIcon_losefocus,s_column_SequenceNum);
	}
	else{
		DEBUG("copy %s to %s failed\n",from_file,to_file);
		//即便拷贝失败，也可以继续插入栏目项。UI展现时，如果没有栏目icon，有一个默认icon可以填充
		//return -1;
		sqlite3_snprintf(sizeof(cmd),cmd,"REPLACE INTO Column(ServiceID,ColumnID,ParentID,Path,ColumnType,SequenceNum) VALUES('%q','%q','%q','%q','%q',%d);",
			ptr->ServiceID,ptr->ColumnID,ptr->ParentID,ptr->Path,ptr->ColumnType,s_column_SequenceNum);
	}
	
	return sqlite_transaction_exec(cmd);
}

/*
在事务内部判断产品是否是小片产品
return:
		-1: failed
		0: sqlite select success, but not preview product
		>0: it is a preview product
*/
static int product_preview_check_in_trans(char *productid)
{
	char sqlite_cmd[512];
	
	sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"SELECT ProductID FROM Product WHERE ProductID='%q' AND Flag='%d';",productid,PRODUCTFLAG_PREVIEW);
	return sqlite_transaction_read(sqlite_cmd,NULL,0);
}


/*
在事务内部判断成品是否被用户（通过预告单）进行反选
return:
		1: unseleced by user (default)
		0: sqlite select success, but no record
		other: sqlite select failed
*/
static int publication_unselect_check_in_trans(char *publicationid)
{
	char sqlite_cmd[512];
	
	sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"SELECT PublicationID FROM GuideList WHERE PublicationID='%q' AND UserStatus='0';",publicationid);
	return sqlite_transaction_read(sqlite_cmd,NULL,0);
}

static int guidelist_insert(DBSTAR_GUIDELIST_S *ptr)
{
	if(NULL==ptr && strlen(ptr->DateValue)>0 && strlen(ptr->PublicationID)>0){
		DEBUG("invalid arguments\n");
		return -1;
	}
	
	/*
	 保留用户作出的“选择接收”
	*/
	char sqlite_cmd[2048];
	sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"SELECT PublicationID FROM GuideList WHERE PublicationID='%q';",ptr->PublicationID);
	if(0<sqlite_transaction_read(sqlite_cmd,NULL,0)){
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO GuideList(ServiceID,DateValue,GuideListID,productID,PublicationID,UserStatus) VALUES('%q',datetime('%q'),'%q','%q','%q',(select UserStatus from GuideList where PublicationID='%q'));",
			ptr->ServiceID,ptr->DateValue,ptr->GuideListID,ptr->productID,ptr->PublicationID,ptr->PublicationID);
		return sqlite_transaction_exec(sqlite_cmd);
	}
	else{
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO GuideList(ServiceID,DateValue,GuideListID,productID,PublicationID,UserStatus) VALUES('%q',datetime('%q'),'%q','%q','%q','1');",
			ptr->ServiceID,ptr->DateValue,ptr->GuideListID,ptr->productID,ptr->PublicationID);
		return sqlite_transaction_exec(sqlite_cmd);
	}
}

#if 0
int check_productid_from_db_in_trans(char *productid)
{
	char read_productid[64];
	memset(read_productid, 0, sizeof(read_productid));
	char sqlite_cmd[512];
	sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"select ProductID from Product where ProductID='%q';",productid);
	if(0<sqlite_transaction_read(sqlite_cmd,read_productid,sizeof(read_productid))){
		DEBUG("check ServiceID %s from datebase OK\n", productid);
		return 0;
	}
	else{
		DEBUG("check ServiceID %s from datebase failed\n", productid);
		return -1;
	}
}
#endif

unsigned long long recv_totalsize_sum_get()
{
	if(s_recv_totalsize_sum<DOWNLOAD_ONCE_MIN){
		DEBUG("check recv totalsize sum %llu Bytes is smaller than %llu, reset it as %llu\n",s_recv_totalsize_sum,DOWNLOAD_ONCE_MIN,DOWNLOAD_ONCE_MIN);
		s_recv_totalsize_sum = DOWNLOAD_ONCE_MIN;
	}
	else
		DEBUG("recv totalsize sum %llu Bytes\n",s_recv_totalsize_sum);
	
	return s_recv_totalsize_sum;
}

//datetime类型得到小时
//比如：2015-02-28 20:00:30得到20-1=19，而2015-02-27 00:00:30得到00-1=23
int datetime2onehourbefore(char *datetime_str)
{
	if(NULL==datetime_str)
		return -1;
	
	int my_year=0,my_mon=0,my_day=0,my_hour=0,my_min=0,my_sec=0;
	if(sscanf(datetime_str,"%d-%d-%d %d:%d:%d",&my_year,&my_mon,&my_day,&my_hour,&my_min,&my_sec)>=4){
		if(my_hour>0)
			return (my_hour-1);
		else
			return 23;
	}
	else
		return -1;
}

static int productdesc_insert(DBSTAR_PRODUCTDESC_S *ptr)
{
	if(NULL==ptr){
		DEBUG("invalid args\n");
		return -1;
	}
	
	char direct_uri[1024];
	char sqlite_cmd[4096];
	unsigned long long this_total_size = 0LL;
	
	/*
	还需要检查用户在选择接收界面的反选
	*/
	
	RECEIVESTATUS_E receive_status = RECEIVESTATUS_WAITING;

#if 0
	// 判断是否接收放在解析出ProductID时进行，避免向ResStr等插入垃圾数据
	DEBUG("ptr->ReceiveType=%s, ptr->productID=%s\n", ptr->ReceiveType,ptr->productID);
	if(	RECEIVETYPE_SPRODUCT==strtol(ptr->ReceiveType,NULL,10)
		|| RECEIVETYPE_COLUMN==strtol(ptr->ReceiveType,NULL,10)
		|| (RECEIVETYPE_PUBLICATION==strtol(ptr->ReceiveType,NULL,10) && (0==ProductID_check(ptr->productID)))
		|| (RECEIVETYPE_PREVIEW==strtol(ptr->ReceiveType,NULL,10) && (0==ProductID_check(ptr->productID))) )
		receive_status = RECEIVESTATUS_WAITING;
	
	DEBUG("I will %s this program(%s), serviceID:%s, ProductID:%s\n", 0==receive_status?"receive":"reject",ptr->ID,ptr->ServiceID,ptr->productID);
#endif

	/*
	理论上，对于处在不同Service的Publication，如果需要拒绝接收，但其PublicationID已经存在于表中，则不需要再次入库；这意味着只有那些允许接收的Publication以及纯粹拒绝接收的Publication可以入库。
	但是考虑到ProductDesc.xml和Service.xml到来的顺序不一定，有可能Service.xml到来的比较晚，因此这里忠实的体现所有Service——publicaiton组合。
	对于那些相同PublicatonID既有允许接收，又有拒绝接收的冲突问题，放在注册时处理。
	
	2013-01-28
	更新新push后，不需要这么复杂的逻辑，不属于自己Product的不入库即可
	
	2013-03-05
	母盘初始化时，由于初始的智能卡中只包括普通产品，因此也要将暂时判断为拒绝接收的节目入库并做解析，
	预备过一段时间后特殊产品下发下来后可以正确的显示。
	由于母盘初始化是直接通过代码驱动解析，解析完ProductDesc.xml后接着就解析相应节目的描述文件。在解析节目描述文件时还要判断产品。
	*/
	if(1==motherdisc_processing()){
		struct stat filestat;
		char desc_direct_uri[1024];
		
		snprintf(desc_direct_uri,sizeof(desc_direct_uri),"%s/%s", push_dir_get(),ptr->DescURI);
		
		// check ContentDelivery.xml for mother disc
		int stat_ret = stat(desc_direct_uri, &filestat);
		if(0==stat_ret){
			DEBUG("in motherdisc processing, make receive_status as RECEIVESTATUS_WAITING, %s\n", desc_direct_uri);
			receive_status = RECEIVESTATUS_WAITING;
		}
		else{
			ERROROUT("can not stat(%s)\n", desc_direct_uri);
			DEBUG("this Publication(%s) is not exist\n", desc_direct_uri);
			receive_status = RECEIVESTATUS_REJECT;
		}
	}
	
	if(RECEIVESTATUS_WAITING==receive_status){
		// 准备接收前先删除旧有目录，防止push库判断异常。
		snprintf(direct_uri,sizeof(direct_uri),"%s/%s", push_dir_get(),ptr->URI);
		remove_force(direct_uri);
		
		this_total_size = 0LL;
		sscanf(ptr->TotalSize,"%llu", &this_total_size);
		s_recv_totalsize_sum += this_total_size;
		DEBUG("ptr->TotalSize: %s, this_total_size=%llu,s_recv_totalsize_sum=%llu\n", ptr->TotalSize,this_total_size,s_recv_totalsize_sum);
		
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ProductDesc(ServiceID,ReceiveType,ProductDescID,rootPath,productID,SetID,ID,TotalSize,URI,DescURI,PushStartTime,PushEndTime,Columns,ReceiveStatus,FreshFlag,Parsed) \
VALUES('%s',\
'%q',\
'%q',\
'%q',\
'%q',\
'%q',\
'%q',\
'%q',\
'%q',\
'%q',\
'%q',\
'%q',\
'%q',\
'%d',\
1,\
'0');",
ptr->ServiceID,
ptr->ReceiveType,
ptr->ProductDescID,
ptr->rootPath,
ptr->productID,
ptr->SetID,
ptr->ID,
ptr->TotalSize,
ptr->URI,
ptr->DescURI,
ptr->PushStartTime,
ptr->PushEndTime,
ptr->Columns,
receive_status);

		sqlite_transaction_exec(sqlite_cmd);
		
		onehour_before_pushend_set(datetime2onehourbefore(ptr->PushEndTime));
	
		if(RECEIVETYPE_PUBLICATION==strtol(ptr->ReceiveType,NULL,10)
			|| RECEIVETYPE_PREVIEW==strtol(ptr->ReceiveType,NULL,10)){
			char columns[512];
			snprintf(columns,sizeof(columns),"%s",ptr->Columns);
			char *p_column = columns;
			
			// 制表符\t
			char *p_HT = NULL;
			while(NULL!=p_column){
				p_HT = strchr(p_column,'\t');
				if(p_HT){
					*p_HT = '\0';
					p_HT ++;
				}
				DEBUG("p_column: %s, p_HT: %s\n", p_column, p_HT);

/*
 如果是剧集，将将Column信息拆分后存入PubliationsSet；如果是非剧集，则拆分后存入Publication
*/
				if(strlen(ptr->SetID)>0){
					sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO PublicationsSet(ServiceID,ColumnID,ProductID,PushStartTime,PushEndTime,ReceiveStatus,SetID) \
VALUES('%q',\
'%q',\
'%q',\
'%q',\
'%q',\
(SELECT ReceiveStatus FROM PublicationsSet WHERE ServiceID='%q' AND ColumnID='%q' AND SetID='%q'),\
'%q');",
ptr->ServiceID,
p_column,
ptr->productID,
ptr->PushStartTime,
ptr->PushEndTime,
ptr->ServiceID,
p_column,
ptr->SetID,
ptr->SetID);
		
					sqlite_transaction_exec(sqlite_cmd);
				}
				else{
					sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Publication(ServiceID,PublicationID,ColumnID,ProductID,URI,DescURI,TotalSize,ProductDescID,PushStartTime,PushEndTime,ReceiveStatus,SetID) \
VALUES('%q',\
'%q',\
'%q',\
'%q',\
'%q',\
'%q',\
'%q',\
'%q',\
'%q',\
'%q',\
'%d',\
'%q');",
ptr->ServiceID,
ptr->ID,
p_column,
ptr->productID,
ptr->URI,
ptr->DescURI,
ptr->TotalSize,
ptr->ProductDescID,
ptr->PushStartTime,
ptr->PushEndTime,
receive_status,
ptr->SetID);
		
					sqlite_transaction_exec(sqlite_cmd);
				}
				p_column = p_HT;
			}

/*
如果是剧集，除了上面存入PublicationsSet外，还要存入单集Publication，但是不拆分Column信息
*/		
			if(strlen(ptr->SetID)>0){
				sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Publication(ServiceID,PublicationID,ColumnID,ProductID,URI,DescURI,TotalSize,ProductDescID,PushStartTime,PushEndTime,ReceiveStatus,SetID) \
VALUES('%q',\
'%q',\
'%q',\
'%q',\
'%q',\
'%q',\
'%q',\
'%q',\
'%q',\
'%q',\
'%d',\
'%q');",
ptr->ServiceID,
ptr->ID,
ptr->Columns,
ptr->productID,
ptr->URI,
ptr->DescURI,
ptr->TotalSize,
ptr->ProductDescID,
ptr->PushStartTime,
ptr->PushEndTime,
receive_status,
ptr->SetID);
		
				sqlite_transaction_exec(sqlite_cmd);
			}
		}
	}
	else
		DEBUG("do not insert to Dbstar.db for reject prog: [%s]%s\n", ptr->ID,ptr->URI);
	
	return 0;
}

/*
 擦除接收单结构体中部分变量，clear_flag取值：
 0——整体清除；
 1——保留serviceID,Version,StandardVersion
 2——保留serviceID,Version,StandardVersion,ReceiveType,rootPath
 3——保留serviceID,Version,StandardVersion,ReceiveType,rootPath,ProductID
*/
static void productdesc_clear(DBSTAR_PRODUCTDESC_S *ptr, int clear_flag)
{
	if(NULL==ptr)
		return;
	
	if(0==clear_flag){
		memset(ptr, 0, sizeof(DBSTAR_PRODUCTDESC_S));
		return;
	}
	
	if(clear_flag<3){	// 1 or 2
		memset(ptr->productID, 0, sizeof(ptr->productID));
	}
	if(clear_flag<2){	// 1
		memset(ptr->ReceiveType, 0, sizeof(ptr->ReceiveType));
		memset(ptr->rootPath, 0, sizeof(ptr->rootPath));
	}
	
	memset(ptr->ProductDescID, 0, sizeof(ptr->ProductDescID));
	memset(ptr->SetID, 0, sizeof(ptr->SetID));
	memset(ptr->ID, 0, sizeof(ptr->ID));
	memset(ptr->TotalSize, 0, sizeof(ptr->TotalSize));
	memset(ptr->URI, 0, sizeof(ptr->URI));
	memset(ptr->DescURI, 0, sizeof(ptr->DescURI));
//	memset(ptr->PushStartTime, 0, sizeof(ptr->PushStartTime));
//	memset(ptr->PushEndTime, 0, sizeof(ptr->PushEndTime));
	memset(ptr->Columns, 0, sizeof(ptr->Columns));
	memset(ptr->ReceiveStatus, 0, sizeof(ptr->ReceiveStatus));
	memset(ptr->version, 0, sizeof(ptr->version));
}

/*
 channel新记录入库时，记FreshFlag为有效。
*/
static int channel_insert(DBSTAR_CHANNEL_S *p)
{
	if(NULL==p || 0>=strlen(p->pid))
		return -1;
	
	char sqlite_cmd[512];
	sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Channel(pid,ServiceID,pidtype,FreshFlag) VALUES('%q','%q','%q',1);",p->pid,serviceID_get(),p->pidtype);
	return sqlite_transaction_exec(sqlite_cmd);
}

/*
 channel新记录入库前将Channel原有pid记录置为无效。
*/
static int channel_ineffective_set()
{
	char sqlite_cmd[512];
	sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"UPDATE Channel SET FreshFlag=0,ServiceID='-1';");
	return sqlite_transaction_exec(sqlite_cmd);
}

/*
 清理channel中无效pid。直接执行，不进入数据库。
*/
static int channel_ineffective_clear()
{
	char sqlite_cmd[512];
	sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"DELETE FROM Channel WHERE FreshFlag=0;");
	return sqlite_execute(sqlite_cmd);
}

/*
 向成品表Publication插入成品信息，需要先判断是否存在，然后才能决定是Insert还是Update
*/
static int publication_insert(DBSTAR_PUBLICATION_S *p)
{
	if(NULL==p || 0==strlen(p->PublicationID)){
		DEBUG("invalid argument\n");
		return -1;
	}
	
	char sqlite_cmd[4096];
	int receive_status_tmp = RECEIVESTATUS_FINISH;
	
	if(1==motherdisc_processing()){
#if 0
// 不搞那么复杂，母盘解析时所有节目都正常展示
		receive_status_tmp = receive_status_get();
#endif
		struct stat filestat;
		char ts_direct_uri[1024];
		
		snprintf(ts_direct_uri,sizeof(ts_direct_uri),"%s/%s", push_dir_get(),p->FileURI);
		
		// check ContentDelivery.xml for mother disc
		int stat_ret = stat(ts_direct_uri, &filestat);
		if(0==stat_ret){
			PRINTF("in mother disc processing status, %s is exist\n",p->FileURI);
		}
		else{
			ERROROUT("can not stat(%s)\n", ts_direct_uri);
			DEBUG("in mother disc processing status, %s is NOT exist, return and do nothing\n",p->FileURI);
			return -1;
		}
	}
	
// 判断为“报纸”类型，则：
//（1）解压epub(zip)，并将解压后的目录uri作为FileURI入库；
//（2）DbstarLauncher实现有bug，目前发现如果SetID以1打头，则报纸的第一级栏目（实际上是SetID的父分类）无法显示名称。如果是5打头则无显示问题。
//		临时由dvbpush兼容，将所有的报纸SetID前均添加字母a，后续由DbstarLauncher修改
	if(PUBLICATIONTYPE_RM==atoi(p->PublicationType) && RMCATEGORY_NEWSPAPER==atoi(p->RMCategory)){
		char epub_file_uri[1024];
		char epub_dir_uri[1024];
		char *epub_suffix = NULL;
		char unzip_cmd[1024];
		
		if('/'==p->FileURI[0])
			snprintf(epub_file_uri,sizeof(epub_file_uri),"%s%s",push_dir_get(),p->FileURI);
		else
			snprintf(epub_file_uri,sizeof(epub_file_uri),"%s/%s",push_dir_get(),p->FileURI);
		PRINTF("newspaper publication, unzip %s\n",epub_file_uri);
		
		snprintf(epub_dir_uri,sizeof(epub_dir_uri),"%s",epub_file_uri);
		epub_suffix = strrchr(epub_dir_uri,'.');
		if(epub_suffix && strncasecmp(epub_dir_uri,".epub",5)){
			*epub_suffix = '/';
			epub_suffix++;
			*epub_suffix = '\0';
			
			remove_force(epub_dir_uri);
			dir_exist_ensure(epub_dir_uri);
			
			snprintf(unzip_cmd,sizeof(unzip_cmd),"unzip %s -d %s",epub_file_uri,epub_dir_uri);
			DEBUG("system(%s)\n",unzip_cmd);
			system(unzip_cmd);
			
			// 报纸epub解压后，存入数据库的FileURI为解压目录的路径
			epub_suffix = strrchr(p->FileURI,'.');
			*epub_suffix = '\0';
		}
		else{
			PRINTF("this newspaper content file is not epub!!!\n");
			return -1;
		}
		
// 下面是一个临时规避措施，实际上应当由Android来解决这个bug
		// if('1'==p->SetID[0])
		// 统一处理，将所有报纸的SetID前均添加a
		{
			char tmp_setid[128];
			
			snprintf(tmp_setid,sizeof(tmp_setid),"a%s",p->SetID);
			
			DEBUG("add char 'a' before SetID(%s), as result %s", p->SetID,tmp_setid);
			sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"UPDATE PublicationsSet SET SetID='%q' WHERE SetID='%q';",tmp_setid,p->SetID);
			sqlite_transaction_exec(sqlite_cmd);
			
			snprintf(p->SetID,sizeof(p->SetID),"%s",tmp_setid);
		}
	}
	
	sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"UPDATE Publication SET PublicationType='%q',IsReserved='%q',Visible='%q',DRMFile='%q',FileID='%q',FileSize='%q',FileURI='%q',FileType='%q',Duration='%q',Resolution='%q',BitRate='%q',FileFormat='%q',CodeFormat='%q',SetID='%q',SetName='%q',SetDesc='%q',SetPosterID='%q',SetPosterName='%q',SetPosterURI='%q',ReceiveStatus='%d',TimeStamp=datetime('now','localtime') WHERE PublicationID='%q';",
		p->PublicationType,p->IsReserved,p->Visible,p->DRMFile,p->FileID,p->FileSize,p->FileURI,p->FileType,p->Duration,p->Resolution,p->BitRate,p->FileFormat,p->CodeFormat,p->SetID,p->SetName,p->SetDesc,p->SetPosterID,p->SetPosterName,p->SetPosterURI,receive_status_tmp,p->PublicationID);
	
	return sqlite_transaction_exec(sqlite_cmd);
}

/*
 插入视音频类成品的信息到MultipleLanguageInfoVA中。
*/
static int publicationva_info_insert(DBSTAR_MULTIPLELANGUAGEINFOVA_S *p)
{
	if(NULL==p || 0==strlen(p->PublicationID) || 0==strlen(p->infolang)){
		DEBUG("invalid argument\n");
		return -1;
	}
	
	char sqlite_cmd[8192];
	sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO MultipleLanguageInfoVA(ServiceID,PublicationID,infolang,PublicationDesc,ImageDefinition,Keywords,Area,Language,Episode,AspectRatio,AudioChannel,Director,Actor,Audience,Model) VALUES('%q','%q','%q','%q','%q','%q','%q','%q','%q','%q','%q','%q','%q','%q','%q');",
		p->ServiceID,p->PublicationID,p->infolang,p->PublicationDesc,p->ImageDefinition,p->Keywords,p->Area,p->Language,p->Episode,p->AspectRatio,p->AudioChannel,p->Director,p->Actor,p->Audience,p->Model);
	
	return sqlite_transaction_exec(sqlite_cmd);
}

/*
 插入富媒体类成品的信息到MultipleLanguageInfoRM中。
*/
static int publicationrm_info_insert(DBSTAR_MULTIPLELANGUAGEINFORM_S *p)
{
	if(NULL==p || 0==strlen(p->PublicationID) || 0==strlen(p->infolang)){
		DEBUG("invalid argument\n");
		return -1;
	}
	
	char sqlite_cmd[8192];
	sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO MultipleLanguageInfoRM(PublicationID,language,PublishID,RMCategory,Author,Publisher,Issue,Keywords,Description,PublishDate,PublishWeek,PublishPlace,CopyrightInfo,TotalEdition,Data,Format,TotalIssue,Recommendation,Words,Title) VALUES('%q','%q','%q','%q','%q','%q','%q','%q','%q','%q','%q','%q','%q','%q','%q','%q','%q','%q','%q','%q');",
		p->PublicationID,p->infolang,p->PublishID,p->RMCategory,p->Author,p->Publisher,p->Issue,p->Keywords,p->Description,p->PublishDate,p->PublishWeek,p->PublishPlace,p->CopyrightInfo,p->TotalEdition,p->Data,p->Format,p->TotalIssue,p->Recommendation,p->Words,p->Title);
	
	return sqlite_transaction_exec(sqlite_cmd);
}

#if 0
/*
 插入应用类成品的信息到MultipleLanguageInfoApp中。
*/
static int publicationapp_info_insert(DBSTAR_MULTIPLELANGUAGEINFOAPP_S *p)
{
	if(NULL==p || 0==strlen(p->PublicationID) || 0==strlen(p->infolang)){
		DEBUG("invalid argument\n");
		return -1;
	}
	
	char sqlite_cmd[512];
	sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO MultipleLanguageInfoApp(PublicationID,infolang,Keywords,Category,Released,AppVersion,Language,Developer,Rated) VALUES('%q','%q','%q','%q','%q','%q','%q','%q','%q');",
		p->PublicationID,p->infolang,p->Keywords,p->Category,p->Released,p->AppVersion,p->Language,p->Developer,p->Rated);
	
	return sqlite_transaction_exec(sqlite_cmd);
}
#endif

static int publicationsset_insert(DBSTAR_PUBLICATIONSSET_S *p)
{
	if(NULL==p){
		DEBUG("invalid NULL arg\n");
		return -1;
	}
	
	char sqlite_cmd[8192];

#if 0
	char old_PublicationType[64];	memset(old_PublicationType,0,sizeof(old_PublicationType));
	char old_IsReserved[64];		memset(old_IsReserved,0,sizeof(old_IsReserved));
	char old_Visible[64];			memset(old_Visible,0,sizeof(old_Visible));
	
	sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"SELECT PublicationType FROM PublicationsSet WHERE SetID='%q';",xmlinfo->PushFlag,xmlinfo->ServiceID,xmlinfo->ID);
	if(0<sqlite_transaction_read(sqlite_cmd,old_xmlver,old_xmlver_size)){
		DEBUG("read xml old version: %s\n", old_xmlver);
		return 0;
	}
	else{
		DEBUG("read xml old version failed\n");
		return -1;
	}
#endif
	
	sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"UPDATE PublicationsSet SET PublicationType='%q',IsReserved='%q',Visible='%q',ReceiveStatus='1' WHERE SetID='%q';",
		p->PublicationType,p->IsReserved,p->Visible,p->SetID);
	sqlite_transaction_exec(sqlite_cmd);
	
	sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO SetInfo(SetID,Title,Starring,Scenario,Classification,Period,CollectionNumber,Review) VALUES('%q','%q','%q','%q','%q','%q','%q','%q');",
		p->SetID,p->Title,p->Starring,p->Scenario,p->Classification,p->Period,p->CollectionNumber,p->Review);
	
	return sqlite_transaction_exec(sqlite_cmd);
}

/*
 向字幕资源表ResSubTitle中插入字幕信息
 由于Res表都是依存于宿主表的寄生表，因此它们自身不能独立封装为事务，应为某事务的一部分。
*/
static int subtitle_insert(DBSTAR_RESSUBTITLE_S *p)
{
	if(NULL==p || 0==strlen(p->ObjectName) || 0==strlen(p->EntityID)){
		DEBUG("invalid argument\n");
		return -1;
	}
	
	char sqlite_cmd[2048];
	sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResSubTitle(ServiceID,ObjectName,EntityID,SubTitleID,SubTitleName,SubTitleLanguage,SubTitleURI) VALUES('%q','%q','%q','%q','%q','%q','%q');",
		p->ServiceID,p->ObjectName,p->EntityID,p->SubTitleID,p->SubTitleName,p->SubTitleLanguage,p->SubTitleURI);
	
	return sqlite_transaction_exec(sqlite_cmd);
}

/*
 向海报资源表ResPoster中插入海报信息
 由于Res表都是依存于宿主表的寄生表，因此它们自身不能独立封装为事务，应为某事务的一部分。
*/
static int poster_insert(DBSTAR_RESPOSTER_S *p)
{
	if(NULL==p || 0==strlen(p->ObjectName) || 0==strlen(p->EntityID) || 0==strlen(p->PosterID) || 0==strlen(p->PosterURI)){
		DEBUG("invalid arguments\n");
		return -1;
	}
	
	char sqlite_cmd[2048];
	sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResPoster(ServiceID,ObjectName,EntityID,PosterID,PosterName,PosterURI) VALUES('%q','%q','%q','%q','%q','%q');",
		p->ServiceID,p->ObjectName, p->EntityID, p->PosterID, p->PosterName, p->PosterURI);
	
	return sqlite_transaction_exec(sqlite_cmd);
}

/*
 向片花资源表ResTrailer中插入片花信息
 由于Res表都是依存于宿主表的寄生表，因此它们自身不能独立封装为事务，应为某事务的一部分。
*/
static int trailer_insert(DBSTAR_RESTRAILER_S *p)
{
	if(NULL==p || 0==strlen(p->ObjectName) || 0==strlen(p->EntityID) || 0==strlen(p->TrailerID) || 0==strlen(p->TrailerURI)){
		DEBUG("invalid arguments\n");
		return -1;
	}
	
	char sqlite_cmd[2048];
	sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResTrailer(ServiceID,ObjectName,EntityID,TrailerID,TrailerName,TrailerURI) VALUES('%q','%q','%q','%q','%q','%q');",
		p->ServiceID,p->ObjectName, p->EntityID, p->TrailerID, p->TrailerName, p->TrailerURI);
	
	return sqlite_transaction_exec(sqlite_cmd);
}


/*
 向消息表Message中插入消息
*/
static int message_insert(DBSTAR_MESSAGE_S *p)
{
	if(NULL==p || 0==strlen(p->MessageID)|| 0==strlen(p->type)|| 0==strlen(p->displayForm)){
		DEBUG("invalid arguments\n");
		return -1;
	}
	
	char sqlite_cmd[8192];
	
	sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Message(MessageID,type,displayForm,StartTime,EndTime,Interval) VALUES('%q','%q','%q','%q','%q','%q');",
		p->MessageID, p->type, p->displayForm, p->StartTime, p->EndTime, p->Interval);
	
	return sqlite_transaction_exec(sqlite_cmd);
}

/*
 向小片表Preview中插入小片
*/
static int preview_insert(DBSTAR_PREVIEW_S *p)
{
	if(NULL==p || 0==strlen(p->PreviewID)){
		DEBUG("invalid arguments\n");
		return -1;
	}
	
	char sqlite_cmd[4096];
	
	sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"UPDATE Publication SET ColumnID='-1' WHERE PublicationID='%q';",p->PublicationID);
	sqlite_transaction_exec(sqlite_cmd);
	
	sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Preview(ServiceID,PreviewID,PreviewType,PreviewSize,ShowTime,PreviewURI,PreviewFormat,Duration,Resolution,BitRate,CodeFormat,PublicationID,ReceiveStatus) \
VALUES('%q','%q','%q','%q','%q','%q','%q','%q','%q','%q','%q','%q','1');",
p->ServiceID,p->PreviewID,p->PreviewType,p->PreviewSize,p->ShowTime,p->PreviewURI,p->PreviewFormat,p->Duration,p->Resolution,p->BitRate,p->CodeFormat,p->PublicationID);
	
	s_preview_publication = 1;
	
	return sqlite_transaction_exec(sqlite_cmd);
}


static int sproduct_insert(DBSTAR_SPRODUCT_S *p)
{
	if(NULL==p || 0==strlen(p->SType)){
		DEBUG("invalid arguments\n");
		return -1;
	}
	
	char sqlite_cmd[4096];
	sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO SProduct(ServiceID,SType,Name,URI) \
	VALUES('%q',\
	'%q',\
	'%q',\
	'%q');",
	p->ServiceID,
	p->SType,
	p->Name,
	p->URI);
		
	return sqlite_transaction_exec(sqlite_cmd);
}


static int cmd_op_refresh(DBSTAR_CMD_OPERATION_S *p)
{
	if(NULL==p){
		DEBUG("invalid args\n");
		return -1;
	}
	
	char sqlite_cmd[1024];
	
	sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"UPDATE Publication SET");
	
	switch(p->type){
		case DBSTAR_CMD_OP_CANCELRESERVATION:
			sqlite3_snprintf(sizeof(sqlite_cmd)-strlen(sqlite_cmd),sqlite_cmd+strlen(sqlite_cmd)," IsReserved='0'");
			break;
		case DBSTAR_CMD_OP_RESERVE:
			sqlite3_snprintf(sizeof(sqlite_cmd)-strlen(sqlite_cmd),sqlite_cmd+strlen(sqlite_cmd)," IsReserved='1'");
			break;
		case DBSTAR_CMD_OP_FORCEDISPLAY:
			sqlite3_snprintf(sizeof(sqlite_cmd)-strlen(sqlite_cmd),sqlite_cmd+strlen(sqlite_cmd)," Visible='1'");
			break;
		case DBSTAR_CMD_OP_FORCEHIDE:
			sqlite3_snprintf(sizeof(sqlite_cmd)-strlen(sqlite_cmd),sqlite_cmd+strlen(sqlite_cmd)," Visible='0'");
			break;
		default:
			DEBUG("can not process such type: %d\n", p->type);
			break;
	}
	
	if(DBSTAR_CMD_OBJ_PUBLICATION==p->objectType)
		sqlite3_snprintf(sizeof(sqlite_cmd)-strlen(sqlite_cmd),sqlite_cmd+strlen(sqlite_cmd),"WHERE PublicationID='%q';",p->object.ID);
	else	//if(DBSTAR_CMD_OBJ_PRODUCT==p->objectType)
		sqlite3_snprintf(sizeof(sqlite_cmd)-strlen(sqlite_cmd),sqlite_cmd+strlen(sqlite_cmd),"WHERE ProductID='%q';",p->object.ID);
	
	sqlite_execute(sqlite_cmd);
	
// 小片处理比较特别，由于早先没有给Preview预留Visible字段，因此处理“显示/隐藏”时通过ReceiveStatus进行控制
	if(DBSTAR_CMD_OBJ_PREVIEW==p->objectType){
		if(DBSTAR_CMD_OP_FORCEDISPLAY==p->type){
			sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"UPDATE Preview SET ReceiveStatus='1' WHERE PublicationID='%q';", p->object.ID);
			sqlite_execute(sqlite_cmd);
			
			preview_refresh_flag_set(1);
		}
		else if(DBSTAR_CMD_OP_FORCEHIDE==p->type){
			sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"UPDATE Preview SET ReceiveStatus='0' WHERE PublicationID='%q';", p->object.ID);
			sqlite_execute(sqlite_cmd);
			
			preview_refresh_flag_set(1);
		}
	}
	
	return 0;
}


int command_operate(DBSTAR_CMD_OPERATION_S *p)
{
	if(NULL==p){
		DEBUG("invalid arg\n");
	}
	
	switch(p->type){
		case DBSTAR_CMD_OP_DELETE:
			switch(p->objectType){
				case DBSTAR_CMD_OBJ_PUBLICATION:
				case DBSTAR_CMD_OBJ_PREVIEW:
					delete_publication_from_monitor(p->object.ID,NULL);
					disk_manage(p->object.ID, NULL);
					break;
				case DBSTAR_CMD_OBJ_PRODUCT:
					delete_publication_from_monitor(NULL,p->object.ID);
					disk_manage(NULL, p->object.ID);
					break;
				default:
					DEBUG("can not process such objectType: %d\n", p->objectType);
					break;
			}
			break;
		case DBSTAR_CMD_OP_UPDATE:
			DEBUG("do nothing currently for DBSTAR_CMD_OP_UPDATE\n");
			break;
		case DBSTAR_CMD_OP_CANCELRESERVATION:
		case DBSTAR_CMD_OP_RESERVE:
		case DBSTAR_CMD_OP_FORCEDISPLAY:
		case DBSTAR_CMD_OP_FORCEHIDE:
			cmd_op_refresh(p);
			break;
		default:	// DBSTAR_CMD_OP_UNDEFINED or others
			DEBUG("can not process such type: %d\n", p->type);
			break;
	}
	return 0;
}


/*
功能：	解析xml结点中的属性
输入：	cur		——待解析的xml结点
		xmlroute——表明从xml的跟结点到当前结点之间的路由
		ptr		——预备用来保存解析出来的属性的结构体指针
*/
static void parseProperty(xmlNodePtr cur, const char *xmlroute, void *ptr)
{
	if(NULL==cur || NULL==xmlroute){
		DEBUG("some arguments are invalid\n");
		return;
	}
	
	//DEBUG("----------- property start -----------\n");
	xmlChar *szAttr = NULL;
	xmlAttrPtr attrPtr = cur->properties;
	while(NULL!=attrPtr){
		szAttr = xmlGetProp(cur, attrPtr->name);
		if(NULL!=szAttr)
		{
			//PRINTF("property of %s, %s: %s\n", xmlroute, attrPtr->name, szAttr);
// xml general property
			if(0==strcmp(xmlroute, XML_ROOT_ELEMENT)){
				DBSTAR_XMLINFO_S *p = (DBSTAR_XMLINFO_S *)ptr;
				if(	0==xmlStrncasecmp(BAD_CAST"Version", attrPtr->name, xmlStrlen(attrPtr->name))
					&& xmlStrlen(BAD_CAST"Version")==xmlStrlen(attrPtr->name)){
					strncpy(p->Version, (char *)szAttr, sizeof(p->Version)-1);
				}
				else if(0==xmlStrncasecmp(BAD_CAST"StandardVersion", attrPtr->name, xmlStrlen(attrPtr->name))
						&& xmlStrlen(BAD_CAST"StandardVersion")==xmlStrlen(attrPtr->name)){
					strncpy(p->StandardVersion, (char *)szAttr, sizeof(p->StandardVersion)-1);
				}
				else if(0==xmlStrncasecmp(BAD_CAST"ServiceID", attrPtr->name, xmlStrlen(attrPtr->name))
						&& xmlStrlen(BAD_CAST"ServiceID")==xmlStrlen(attrPtr->name)){
					strncpy(p->ServiceID, (char *)szAttr, sizeof(p->ServiceID)-1);
				}
				else
					DEBUG("can NOT process such property '%s' of xml route '%s'\n", attrPtr->name, xmlroute);
			}
			
// Initialize.xml
			else if(0==strcmp(xmlroute, "Initialize^ServiceInits^ServiceInit")){
				DBSTAR_PRODUCT_SERVICE_S *p = (DBSTAR_PRODUCT_SERVICE_S *)ptr;
				if(0==xmlStrcmp(BAD_CAST"productID", attrPtr->name)){
					strncpy(p->productID, (char *)szAttr, sizeof(p->productID)-1);
				}
				else if(0==xmlStrcmp(BAD_CAST"serviceID", attrPtr->name)){
					strncpy(p->serviceID, (char *)szAttr, sizeof(p->serviceID)-1);
				}
//				else if(0==xmlStrcmp(BAD_CAST"productName", attrPtr->name)){
//					strncpy(p->productName, (char *)szAttr, sizeof(p->productName)-1);
//				}
//				else
//					DEBUG("can NOT process such property '%s' of xml route '%s'\n", attrPtr->name, xmlroute);
			}
			else if(0==strcmp(xmlroute, "Initialize^ServiceInits^ServiceInit^Channels^Channel")){
				DBSTAR_CHANNEL_S *p  = (DBSTAR_CHANNEL_S *)ptr;
				if(0==xmlStrcmp(BAD_CAST"pid", attrPtr->name)){
					strncpy(p->pid, (char *)szAttr, sizeof(p->pid)-1);
				}
				else if(0==xmlStrcmp(BAD_CAST"pidtype", attrPtr->name)){
					strncpy(p->pidtype, (char *)szAttr, sizeof(p->pidtype)-1);
				}
//				else
//					DEBUG("can NOT process such property '%s' of xml route '%s'\n", attrPtr->name, xmlroute);
			}
			else if(0==strncmp(xmlroute, "Initialize^ServiceInits^ServiceInit^", strlen("Initialize^ServiceInits^ServiceInit^"))){
				DBSTAR_XMLINFO_S *p = (DBSTAR_XMLINFO_S *)ptr;
				if(0==xmlStrcmp(BAD_CAST"name", attrPtr->name)){
					strncpy(p->XMLName, (char *)szAttr, sizeof(p->XMLName)-1);
					DEBUG("name: %s\n", p->XMLName);
				}
				else if(0==xmlStrcmp(BAD_CAST"uri", attrPtr->name)){
					strncpy(p->URI, (char *)szAttr, sizeof(p->URI)-1);
					DEBUG("uri: %s\n", p->URI);
				}
				else
					DEBUG("can NOT process such property '%s' of xml route '%s'\n", attrPtr->name, xmlroute);
			}
			
// GuideList.xml
			else if(0==strcmp(xmlroute, "GuideList^Date^Product")){
				DBSTAR_GUIDELIST_S *p = (DBSTAR_GUIDELIST_S *)ptr;
				if(0==xmlStrcmp(BAD_CAST"productID", attrPtr->name)){
					strncpy(p->productID, (char *)szAttr, sizeof(p->productID)-1);
				}
//				else
//					DEBUG("can NOT process such property '%s' of xml route '%s'\n", attrPtr->name, xmlroute);
			}

// Column.xml Columns^Column^ColumnIcons^ColumnIcon
			else if(0==strcmp(xmlroute, "Columns^Column^ColumnIcons^ColumnIcon")){
				COLUMNICON_S *p = (COLUMNICON_S *)ptr;
				if(0==xmlStrcmp(BAD_CAST"type", attrPtr->name)){
					strncpy(p->type, (char *)szAttr, sizeof(p->type)-1);
				}
				else if(0==xmlStrcmp(BAD_CAST"uri", attrPtr->name)){
					strncpy(p->uri, (char *)szAttr, sizeof(p->uri)-1);
				}
				else
					DEBUG("can NOT process such property '%s' of xml route '%s'\n", attrPtr->name, xmlroute);
			}
			
// Publication.xml PublicationVA
			else if(0==strcmp(xmlroute, "Publication^PublicationVA^MultipleLanguageInfos^MultipleLanguageInfo")){
				DBSTAR_MULTIPLELANGUAGEINFOVA_S *p = (DBSTAR_MULTIPLELANGUAGEINFOVA_S *)ptr;
				if(0==xmlStrcmp(BAD_CAST"language", attrPtr->name)){
					strncpy(p->infolang, (char *)szAttr, sizeof(p->infolang)-1);
				}
//				else
//					DEBUG("can NOT process such property '%s' of xml route '%s'\n", attrPtr->name, xmlroute);
			}
// Publication.xml PublicationRM
			else if(0==strcmp(xmlroute, "Publication^PublicationRM^MultipleLanguageInfos^MultipleLanguageInfo")){
				DBSTAR_MULTIPLELANGUAGEINFORM_S *p = (DBSTAR_MULTIPLELANGUAGEINFORM_S *)ptr;
				if(0==xmlStrcmp(BAD_CAST"language", attrPtr->name)){
					strncpy(p->infolang, (char *)szAttr, sizeof(p->infolang)-1);
				}
//				else
//					DEBUG("can NOT process such property '%s' of xml route '%s'\n", attrPtr->name, xmlroute);
			}

// ProductDesc.xml 当前投递单
			else if(0==strcmp(xmlroute, "ProductDesc^PushDate")){
				DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
				if(0==xmlStrcmp(BAD_CAST"startTime", attrPtr->name)){
					snprintf(p->PushStartTime, sizeof(p->PushStartTime), "%s", (char *)szAttr);
				}
				else if(0==xmlStrcmp(BAD_CAST"endTime", attrPtr->name)){
					snprintf(p->PushEndTime, sizeof(p->PushEndTime), "%s", (char *)szAttr);
				}
				else
					DEBUG("can NOT process such property '%s' of xml route '%s'\n", attrPtr->name, xmlroute);
			}
			else if(0==strcmp(xmlroute, "ProductDesc^ReceivePublications")
					||	0==strcmp(xmlroute, "ProductDesc^ReceiveSProduct")
					||	0==strcmp(xmlroute, "ProductDesc^ReceiveColumn")){
				DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
				if(0==xmlStrcmp(BAD_CAST"rootPath", attrPtr->name)){
					snprintf(p->rootPath,sizeof(p->rootPath),"%s",(char *)szAttr);
					signed_char_clear(p->rootPath, strlen(p->rootPath), '/', 3);
				}
				else if(0==xmlStrncasecmp(BAD_CAST"version", attrPtr->name, xmlStrlen(attrPtr->name))
					&& xmlStrlen(BAD_CAST"version")==xmlStrlen(attrPtr->name)){
					snprintf(p->version,sizeof(p->version),"%s",(char *)szAttr);
				}
				else
					DEBUG("can NOT process such property '%s' of xml route '%s'\n", attrPtr->name, xmlroute);
			}
			else if(0==strcmp(xmlroute, "ProductDesc^ReceivePublications^Product")){
				DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
				if(0==xmlStrcmp(BAD_CAST"productID", attrPtr->name)){
					strncpy(p->productID, (char *)szAttr, sizeof(p->productID)-1);
				}
				else
					DEBUG("can NOT process such property '%s' of xml route '%s'\n", attrPtr->name, xmlroute);
			}
			else if(	0==strcmp(xmlroute, "ProductDesc^ReceivePublications^Product^Publication^PublicationURI")
					||	0==strcmp(xmlroute, "ProductDesc^ReceiveSProduct^SProductURI")
					||	0==strcmp(xmlroute, "ProductDesc^ReceiveColumn^ColumnURI")){
				DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
				if(0==xmlStrcmp(BAD_CAST"xmlURI", attrPtr->name)){
					char tmp_xmlURI[512];
					snprintf(tmp_xmlURI, sizeof(tmp_xmlURI), "%s", (char *)szAttr);
					
					snprintf(p->DescURI,sizeof(p->DescURI),"%s",p->rootPath);
					if('/'!=tmp_xmlURI[0])
						snprintf(p->DescURI+strlen(p->DescURI),sizeof(p->DescURI)-strlen(p->DescURI),"/");
					snprintf(p->DescURI+strlen(p->DescURI),sizeof(p->DescURI)-strlen(p->DescURI),"%s",tmp_xmlURI);
				}
				else
					DEBUG("can NOT process such property '%s' of xml route '%s'\n", attrPtr->name, xmlroute);
			}
			else if(0==strcmp(xmlroute, "ProductDesc^ReceivePublications^Product^Publication^Columns^Column")){
				DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
				if(0==xmlStrcmp(BAD_CAST"columnID", attrPtr->name)){
					if(0==strlen(p->Columns))
						snprintf(p->Columns, sizeof(p->Columns), "%s", (char *)szAttr);
					else
						snprintf((p->Columns) + strlen(p->Columns), sizeof(p->Columns) - strlen(p->Columns), "\t%s", (char *)szAttr);
				}
				else
					DEBUG("can NOT process such property '%s' of xml route '%s'\n", attrPtr->name, xmlroute);
			}
// Message.xml
			else if(0==strcmp(xmlroute, "Messages^Message")){
				DBSTAR_MESSAGE_S *p = (DBSTAR_MESSAGE_S *)ptr;
				if(0==xmlStrcmp(BAD_CAST"type", attrPtr->name)){
					strncpy(p->type, (char *)szAttr, sizeof(p->type)-1);
				}
				else if(0==xmlStrcmp(BAD_CAST"displayForm", attrPtr->name)){
					strncpy(p->displayForm, (char *)szAttr, sizeof(p->displayForm)-1);
				}
				else
					DEBUG("can NOT process such property '%s' of xml route '%s'\n", attrPtr->name, xmlroute);
			}
// SProduct.xml
			else if(0==strncmp(xmlroute, "SProduct^SProductInfo^", strlen("SProduct^SProductInfo^"))){
				DBSTAR_SPRODUCT_S *p = (DBSTAR_SPRODUCT_S *)ptr;
				if(0==xmlStrcmp(BAD_CAST"name", attrPtr->name)){
					strncpy(p->Name, (char *)szAttr, sizeof(p->Name)-1);
				}
				else if(0==xmlStrcmp(BAD_CAST"uri", attrPtr->name)){
					strncpy(p->URI, (char *)szAttr, sizeof(p->URI)-1);
				}
				else
					DEBUG("can NOT process such property '%s' of xml route '%s'\n", attrPtr->name, xmlroute);
			}
// Commands.xml
			else if(0==strcmp(xmlroute, "Commands^Operations^Operation")){
				DBSTAR_CMD_OPERATION_S *p = (DBSTAR_CMD_OPERATION_S *)ptr;
				if(0==xmlStrcmp(BAD_CAST"type", attrPtr->name)){
					p->type = strtol((char *)szAttr,NULL,0);
				}
				else
					DEBUG("can NOT process such property '%s' of xml route '%s'\n", attrPtr->name, xmlroute);
			}
			else if(0==strcmp(xmlroute, "Commands^Operations^Operation^Objects")){
				DBSTAR_CMD_OPERATION_S *p = (DBSTAR_CMD_OPERATION_S *)ptr;
				if(0==xmlStrcmp(BAD_CAST"objectType", attrPtr->name)){
					p->objectType = strtol( (char *)szAttr, NULL, 0);
				}
				else
					DEBUG("can NOT process such property '%s' of xml route '%s'\n", attrPtr->name, xmlroute);
			}
			else if(0==strcmp(xmlroute, "Commands^Operations^Operation^Objects^ObjectID")){
				DBSTAR_CMD_OPERATION_S *p = (DBSTAR_CMD_OPERATION_S *)ptr;
				if(0==xmlStrcmp(BAD_CAST"fileType", attrPtr->name)){
					p->object.fileType = strtol( (char *)szAttr, NULL, 0);
				}
				else
					DEBUG("can NOT process such property '%s' of xml route '%s'\n", attrPtr->name, xmlroute);
			}
			
// 集中处理字符串
			else if(	0==strcmp(xmlroute, "Publication^PublicationNames^PublicationName")
					||	0==strcmp(xmlroute, "Service^ServiceNames^ServiceName")
					||	0==strcmp(xmlroute, "Service^Products^Product^ProductNames^ProductName")
					||	0==strcmp(xmlroute, "Service^Products^Product^Descriptions^Description")
					||	0==strcmp(xmlroute, "Product^ProductNames^ProductName")
					||	0==strcmp(xmlroute, "Product^Descriptions^Description")
					||	0==strcmp(xmlroute, "Publication^PublicationNames^PublicationName")
					||	0==strcmp(xmlroute, "Publication^PublicationVA^MFile^FileNames^FileName")
					||	0==strcmp(xmlroute, "Publication^PublicationRM^MFile^FileNames^FileName")
					||	0==strcmp(xmlroute, "Messages^Message^Content^SubContent")
					||	0==strcmp(xmlroute, "Preview^PreviewNames^PreviewName")
					||	0==strcmp(xmlroute, "GuideList^Date^Product^Item^PublicationNames^PublicationName")
					||	0==strcmp(xmlroute, "GuideList^Date^Product^Item^ColumnNames^ColumnName")
					||	0==strcmp(xmlroute, "GuideList^Date^Product^Item^PublicationDescs^PublicationDesc")
					||	0==strcmp(xmlroute, "ProductDesc^ReceivePublications^Product^Publication^PublicationNames^PublicationName")
					||	0==strcmp(xmlroute, "ProductDesc^ReceiveSProduct^SProductNames^SProductName")
					||	0==strcmp(xmlroute, "ProductDesc^ReceiveColumn^ColumnNames^ColumnName")
					||	0==strcmp(xmlroute, "Columns^Column^DisplayNames^DisplayName")
					||	NULL!=strrstr_s(xmlroute, "MFile^FileNames^FileName", '^')
					||	NULL!=strrstr_s(xmlroute, "Extension^Captions^Caption", '^')
					|| 	NULL!=strrstr_s(xmlroute, "Extension^Values^Value", '^')){
				DBSTAR_RESSTR_S *p = (DBSTAR_RESSTR_S *)ptr;
				//DEBUG("attrPtr->name: %s\n", attrPtr->name);
				if(0==xmlStrcmp(BAD_CAST"language", attrPtr->name)){
					strncpy(p->StrLang, (char *)szAttr, sizeof(p->StrLang)-1);
					//DEBUG("langauge: %s:%s\n", (char *)szAttr, p->StrLang);
				}
				else if(0==xmlStrcmp(BAD_CAST"value", attrPtr->name)){
					//DEBUG("value: %s\n", (char *)szAttr);
					snprintf(p->StrValue, sizeof(p->StrValue), "%s", (char *)szAttr);
				}
				else
					DEBUG("can NOT process such property '%s' of xml route '%s'\n", attrPtr->name, xmlroute);
			}
			
			else
				DEBUG("can NOT process such xml route '%s'\n", xmlroute);
			
			xmlFree(szAttr);
		}
		attrPtr = attrPtr->next;
	}
	//DEBUG("----------- property end -----------\n\n");
	
	return;
}

char *process_over_str(XML_EXIT_E exit_flag)
{
	switch(exit_flag){
		case XML_EXIT_NORMALLY:
			return "XML_EXIT_NORMALLY";
			break;
		case XML_EXIT_MOVEUP:
			return "XML_EXIT_MOVEUP";
			break;
		case XML_EXIT_UNNECESSARY:
			return "XML_EXIT_UNNECESSARY";
			break;
		case XML_EXIT_ERROR:
			return "XML_EXIT_ERROR";
			break;
		default:
			return "XML_EXIT_UNDEFINED";
			break;
	}
}

static int boolean_translate(char *bool_str, unsigned int bool_str_len)
{
	if(0==strncasecmp("true", bool_str, bool_str_len) || 0==strncasecmp("1", bool_str, bool_str_len) || 0==strncasecmp("yes", bool_str, bool_str_len))
		return 1;
	else
		return 0;
}

#define PROCESS_OVER_CHECK(f) ( (XML_EXIT_NORMALLY==f || XML_EXIT_MOVEUP==f)?0:-1 )

static int read_xmlver_in_trans(DBSTAR_XMLINFO_S *xmlinfo,char *old_xmlver,unsigned int old_xmlver_size)
{
	if(NULL==xmlinfo)
		return -1;
		
	char sqlite_cmd[512];
	if(strlen(xmlinfo->ID)>0)
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"SELECT Version FROM Initialize WHERE PushFlag='%q' AND ID='%q';",xmlinfo->PushFlag,xmlinfo->ID);
	else
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"SELECT Version FROM Initialize WHERE PushFlag='%q';",xmlinfo->PushFlag);
		
	if(0<sqlite_transaction_read(sqlite_cmd,old_xmlver,old_xmlver_size)){
		PRINTF("read xml old version: %s\n", old_xmlver);
		return 0;
	}
	else{
		PRINTF("read xml old version failed: %s\n", sqlite_cmd);
		return -1;
	}
}

/*
 xmlroute仍存在重复的可能性
*/
/*
 只有从parseDoc过来的调用，其rootelement方不为空，其他地方的调用rootelement均为空
*/
/*
 p_child_tree_is_valid
 判断标记，若正在处理的节点有效，则本父节点有效，且叔伯节点无效。
 使用场景：通过子节点的值判断本节点是否有效。注意和局部变量process_over之间的关系。
 			如果本父节点有效（自然本节点也有效），则回到父节点时，可置process_over提前退出解析。
*/
/*
 返回值：
 0——成功
 -1——失败
*/
static int parseNode (xmlDocPtr doc, xmlNodePtr cur, char *xmlroute, void *ptr, DBSTAR_XMLINFO_S *xmlinfo, char *rootelement, char *old_xmlver)
{
	if(NULL==doc || NULL==cur || NULL==xmlroute){
		DEBUG("some arguments are invalide\n");
		return -1;
	}
	//DEBUG("%s:%d\n", xmlroute, p_child_tree_is_valid);
	/*
	一旦正式进入本函数，只能在函数末尾return，不允许中途退出，确保每个XML“树”都有一个正确的结束。
	*/
	
	xmlChar *szKey = NULL;
	char new_xmlroute[256];
	char old_xmlver_in_recv[64];
	DBSTAR_XMLINFO_S xmlinfo_in_recv;
	
	/*
	只有公共的Version和StandardVersion字段统一处理
	*/
	int uniform_parse = 0;
	
	/*
	若兄弟节点中，只需要处理本节点内部信息，完毕后不需要继续扫描其余兄弟节点，则置process_over为1。
	场景：在属性中存在判断条件，合法时递归parseNode进入内部，内部处理完毕后整个节点结束。
	1：已经找到合法的分支，剩下的分支无需解析，提前退出
	2：在业务逻辑上判断不需要解析，比如，ServiceID不匹配
	3：解析错误导致提前退出
	*/
	int process_over = XML_EXIT_NORMALLY;
	
	cur = cur->xmlChildrenNode;
	while (cur != NULL) {
//		DEBUG("%s cur->name:%s\n", XML_TEXT_NODE==cur->type?"XML_TEXT_NODE":"not XML_TEXT_NODE", cur->name);
		if(XML_TEXT_NODE==cur->type || 0==xmlStrcmp(BAD_CAST"comment", cur->name)){
			cur = cur->next;
			continue;
		}
/*
 统一处理Version和StandardVersion 
*/
		if(NULL!=rootelement && NULL!=xmlinfo && 0==strcmp(xmlroute, rootelement)){
			szKey = xmlNodeGetContent(cur);
			if(0==xmlStrcmp(cur->name, BAD_CAST"Version")){
				strncpy(xmlinfo->Version, (char *)szKey, sizeof(xmlinfo->Version)-1);
				if(NULL==old_xmlver || 0==strcmp(old_xmlver, xmlinfo->Version)){
					DEBUG("same xml version: %s\n", old_xmlver);
					process_over = XML_EXIT_UNNECESSARY;
				}
				uniform_parse = 1;
			}
			else if(0==xmlStrcmp(cur->name, BAD_CAST"StandardVersion")){
				strncpy(xmlinfo->StandardVersion, (char *)szKey, sizeof(xmlinfo->StandardVersion)-1);
				uniform_parse = 1;
			}
			else
				uniform_parse = 0;
			xmlFree(szKey);
		}
		
		if(0==uniform_parse){
			snprintf(new_xmlroute, sizeof(new_xmlroute), "%s^%s", xmlroute, cur->name);
			PRINTF("XML route: %s\n", new_xmlroute);
			
// Initialize.xml
			if(0==strncmp(new_xmlroute, "Initialize^", strlen("Initialize^"))){
				if(0==strcmp(new_xmlroute, "Initialize^ServiceInits")){
					process_over = parseNode(doc, cur, new_xmlroute, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "Initialize^ServiceInits^ServiceInit")){
					DBSTAR_PRODUCT_SERVICE_S product_service_s;
					memset(&product_service_s, 0, sizeof(product_service_s));
					parseProperty(cur, new_xmlroute, (void *)&product_service_s);
					
					if(1==motherdisc_processing() || 0==ProductID_check(product_service_s.productID)){
						DEBUG("detect valid productID: %s\n", product_service_s.productID);
						s_detect_valid_productID = 1;
						DBSTAR_GLOBAL_S global_s;
						memset(&global_s, 0, sizeof(global_s));
						strncpy(global_s.Name, GLB_NAME_SERVICEID, sizeof(global_s.Name)-1);
						strncpy(global_s.Value, product_service_s.serviceID, sizeof(global_s.Value)-1);
						global_insert(&global_s);
						serviceID_set(product_service_s.serviceID);
						
						sqlite_transaction_table_clear("Initialize");
						parseNode(doc, cur, new_xmlroute, NULL, NULL, NULL, NULL);
						process_over = XML_EXIT_MOVEUP;
						DEBUG("process over moveup on valid serviceID %s\n", serviceID_get());
					}
					else{
						DEBUG("productID %s is invalid\n", product_service_s.productID);
					}
				}
				else if(0==strcmp(new_xmlroute, "Initialize^ServiceInits^ServiceInit^Channels")){
					channel_ineffective_set();
					parseNode(doc, cur, new_xmlroute, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "Initialize^ServiceInits^ServiceInit^Channels^Channel")){
					DBSTAR_CHANNEL_S channel_s;
					memset(&channel_s, 0, sizeof(channel_s));
					parseProperty(cur, new_xmlroute, (void *)(&channel_s));
					channel_insert(&channel_s);
				}
				else if(0==strncmp(new_xmlroute, "Initialize^ServiceInits^ServiceInit^", strlen("Initialize^ServiceInits^ServiceInit^"))){
					DBSTAR_XMLINFO_S xmlinfo;
					memset(&xmlinfo, 0, sizeof(xmlinfo));
					if(0==xmlStrcmp(cur->name, BAD_CAST"GuideList"))
						snprintf(xmlinfo.PushFlag, sizeof(xmlinfo.PushFlag), "%d", GUIDELIST_XML);
					else if(0==xmlStrcmp(cur->name, BAD_CAST"Column"))
						snprintf(xmlinfo.PushFlag, sizeof(xmlinfo.PushFlag), "%d", COLUMN_XML);
					else if(0==xmlStrcmp(cur->name, BAD_CAST"ProductDesc"))
						snprintf(xmlinfo.PushFlag, sizeof(xmlinfo.PushFlag), "%d", PRODUCTDESC_XML);
					else if(0==xmlStrcmp(cur->name, BAD_CAST"SProduct"))
						snprintf(xmlinfo.PushFlag, sizeof(xmlinfo.PushFlag), "%d", SPRODUCT_XML);
					else if(0==xmlStrcmp(cur->name, BAD_CAST"Service"))
						snprintf(xmlinfo.PushFlag, sizeof(xmlinfo.PushFlag), "%d", SERVICE_XML);
					else if(0==xmlStrcmp(cur->name, BAD_CAST"Command"))
						snprintf(xmlinfo.PushFlag, sizeof(xmlinfo.PushFlag), "%d", COMMANDS_XML);
					else if(0==xmlStrcmp(cur->name, BAD_CAST"Message"))
						snprintf(xmlinfo.PushFlag, sizeof(xmlinfo.PushFlag), "%d", MESSAGE_XML);
					else{
						DEBUG("such xml node can not be processed: %s\n", new_xmlroute);
						snprintf(xmlinfo.PushFlag, sizeof(xmlinfo.PushFlag), "InvalidPushFlag");
					}
					
					if(strcmp("InvalidPushFlag", xmlinfo.PushFlag)){
						parseProperty(cur, new_xmlroute, (void *)(&xmlinfo));
						snprintf(xmlinfo.ServiceID, sizeof(xmlinfo.ServiceID), "%s", serviceID_get());
						if('/'==xmlinfo.URI[strlen(xmlinfo.URI)-1] || 0!=strtailcmp(xmlinfo.URI, ".xml", 0))
							snprintf(xmlinfo.URI+strlen(xmlinfo.URI), sizeof(xmlinfo.URI)-strlen(xmlinfo.URI), "/%s", xmlinfo.XMLName);
						signed_char_clear(xmlinfo.URI, strlen(xmlinfo.URI), '/', 1);
						xmlinfo_insert(&xmlinfo);
					}
				}
				else
					DEBUG("can not distinguish such xml route: %s\n", new_xmlroute);
			}
			
// Service.xml
			else if(0==strncmp(new_xmlroute, "Service^", strlen("Service^"))){
				if(0==strcmp(new_xmlroute, "Service^ServiceNames")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "Service^ServiceNames^ServiceName")){
					DBSTAR_SERVICE_S *p_service = (DBSTAR_SERVICE_S *)ptr;
					
					DBSTAR_RESSTR_S resstr_s;
					memset(&resstr_s, 0, sizeof(resstr_s));
					
					snprintf(resstr_s.ServiceID,sizeof(resstr_s.ServiceID),"%s",p_service->ServiceID);
					strncpy(resstr_s.ObjectName, OBJ_SERVICE, sizeof(resstr_s.ObjectName)-1);
					if(strlen(p_service->ServiceID)>0)
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s", p_service->ServiceID);
					else
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s%s", OBJID_PAUSE, OBJ_SERVICE);
					strncpy(resstr_s.StrName, "ServiceName", sizeof(resstr_s.StrName)-1);
					
					parseProperty(cur, new_xmlroute, (void *)(&resstr_s));
					
					resstr_insert(&resstr_s);
				}
				else if(0==strcmp(new_xmlroute, "Service^RegionCode")){
					DBSTAR_SERVICE_S *p_service = (DBSTAR_SERVICE_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p_service->RegionCode, (char *)szKey, sizeof(p_service->RegionCode)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Service^OnlineTime")){
					DBSTAR_SERVICE_S *p_service = (DBSTAR_SERVICE_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p_service->OnlineTime, (char *)szKey, sizeof(p_service->OnlineTime)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Service^OfflineTime")){
					DBSTAR_SERVICE_S *p_service = (DBSTAR_SERVICE_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p_service->OfflineTime, (char *)szKey, sizeof(p_service->OfflineTime)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Service^Products")){
					DBSTAR_SERVICE_S *p_service = (DBSTAR_SERVICE_S *)ptr;
					
					DBSTAR_PRODUCT_S product_s;
					memset(&product_s, 0, sizeof(product_s));
					snprintf(product_s.ServiceID,sizeof(product_s.ServiceID),"%s",p_service->ServiceID);
					
					parseNode(doc, cur, new_xmlroute, &product_s, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "Service^Products^Product")){
					DBSTAR_PRODUCT_S *p_product = (DBSTAR_PRODUCT_S *)ptr;
					char service_id[64];
					snprintf(service_id,sizeof(service_id),"%s",p_product->ServiceID);
					memset(ptr, 0, sizeof(DBSTAR_PRODUCT_S));
					snprintf(p_product->ServiceID,sizeof(p_product->ServiceID),"%s",service_id);
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
					product_insert((DBSTAR_PRODUCT_S *)ptr);
				}
				else if(0==strcmp(new_xmlroute, "Service^Products^Product^ProductID")){
					DBSTAR_PRODUCT_S *p_product = (DBSTAR_PRODUCT_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p_product->ProductID, (char *)szKey, sizeof(p_product->ProductID)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Service^Products^Product^ProductType")){
					DBSTAR_PRODUCT_S *p_product = (DBSTAR_PRODUCT_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p_product->ProductType, (char *)szKey, sizeof(p_product->ProductType)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Service^Products^Product^Flag")){
					DBSTAR_PRODUCT_S *p_product = (DBSTAR_PRODUCT_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p_product->Flag, (char *)szKey, sizeof(p_product->Flag)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Service^Products^Product^ProductNames")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "Service^Products^Product^ProductNames^ProductName")){
					DBSTAR_PRODUCT_S *p_product = (DBSTAR_PRODUCT_S *)ptr;
					
					DBSTAR_RESSTR_S resstr_s;
					memset(&resstr_s, 0, sizeof(resstr_s));
					
					snprintf(resstr_s.ServiceID,sizeof(resstr_s.ServiceID),"%s",p_product->ServiceID);
					strncpy(resstr_s.ObjectName, OBJ_PRODUCT, sizeof(resstr_s.ObjectName)-1);
					//DEBUG("ProductID: %s\n", p_product->ProductID);
					if(strlen(p_product->ProductID)>0)
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s", p_product->ProductID);
					else
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s%s", OBJID_PAUSE, OBJ_PRODUCT);
					strncpy(resstr_s.StrName, "ProductName", sizeof(resstr_s.StrName)-1);
					
					parseProperty(cur, new_xmlroute, (void *)(&resstr_s));
					
					resstr_insert(&resstr_s);
				}
				else if(0==strcmp(new_xmlroute, "Service^Products^Product^OnlineDate")){
					DBSTAR_PRODUCT_S *p_product = (DBSTAR_PRODUCT_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p_product->OnlineDate, (char *)szKey, sizeof(p_product->OnlineDate)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Service^Products^Product^OfflineDate")){
					DBSTAR_PRODUCT_S *p_product = (DBSTAR_PRODUCT_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p_product->OfflineDate, (char *)szKey, sizeof(p_product->OfflineDate)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Service^Products^Product^IsReserved")){
					DBSTAR_PRODUCT_S *p_product = (DBSTAR_PRODUCT_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					snprintf(p_product->IsReserved,sizeof(p_product->IsReserved),"%d",boolean_translate((char *)szKey,strlen((char *)szKey)));
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Service^Products^Product^Price")){
					DBSTAR_PRODUCT_S *p_product = (DBSTAR_PRODUCT_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p_product->Price, (char *)szKey, sizeof(p_product->Price)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Service^Products^Product^CurrencyType")){
					DBSTAR_PRODUCT_S *p_product = (DBSTAR_PRODUCT_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p_product->CurrencyType, (char *)szKey, sizeof(p_product->CurrencyType)-1);
					xmlFree(szKey);
				}
			}
			
// Publication.xml
			else if(0==strncmp(new_xmlroute, "Publication^", strlen("Publication^"))){
				if(0==strcmp(new_xmlroute, "Publication^PublicationID")){
					DBSTAR_PUBLICATION_S *p = (DBSTAR_PUBLICATION_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->PublicationID, (char *)szKey, sizeof(p->PublicationID)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationNames")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationNames^PublicationName")){
					DBSTAR_PUBLICATION_S *p = (DBSTAR_PUBLICATION_S *)ptr;
					
					DBSTAR_RESSTR_S resstr_s;
					memset(&resstr_s, 0, sizeof(resstr_s));
					
					snprintf(resstr_s.ServiceID,sizeof(resstr_s.ServiceID),"%s",p->ServiceID);
					strncpy(resstr_s.ObjectName, OBJ_PUBLICATION, sizeof(resstr_s.ObjectName)-1);
					if(strlen(p->PublicationID)>0)
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s", p->PublicationID);
					else
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s%s", OBJID_PAUSE, OBJ_PUBLICATION);
					
					strncpy(resstr_s.StrName, "PublicationName", sizeof(resstr_s.StrName)-1);
					
					parseProperty(cur, new_xmlroute, (void *)(&resstr_s));
						
					resstr_insert(&resstr_s);
					
#ifdef GET_TITLE_FROM_PUBLICATIONNAME
					if(0==strcmp(resstr_s.StrLang,language_get())){
						snprintf(p->PublicationName,sizeof(p->PublicationName),"%s",resstr_s.StrValue);
						DEBUG("will transit PublicationName: %s,%s\n",language_get(),p->PublicationName);
					}
#endif
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationType")){
					DBSTAR_PUBLICATION_S *p = (DBSTAR_PUBLICATION_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->PublicationType, (char *)szKey, sizeof(p->PublicationType)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^IsReserved")){
					DBSTAR_PUBLICATION_S *p = (DBSTAR_PUBLICATION_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					snprintf(p->IsReserved,sizeof(p->IsReserved),"%d",boolean_translate((char *)szKey,strlen((char *)szKey)));
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^Visible")){
					DBSTAR_PUBLICATION_S *p = (DBSTAR_PUBLICATION_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					snprintf(p->Visible,sizeof(p->Visible),"%d",boolean_translate((char *)szKey,strlen((char *)szKey)));
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^DRMFile")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "Publication^DRMFile^FileURI")){
					DBSTAR_PUBLICATION_S *p = (DBSTAR_PUBLICATION_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->DRMFile, (char *)szKey, sizeof(p->DRMFile)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MultipleLanguageInfos")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MultipleLanguageInfos^MultipleLanguageInfo")){
					DBSTAR_PUBLICATION_S *p = (DBSTAR_PUBLICATION_S *)ptr;
					
					DBSTAR_MULTIPLELANGUAGEINFOVA_S info_va_s;
					memset(&info_va_s, 0, sizeof(info_va_s));
					snprintf(info_va_s.ServiceID, sizeof(info_va_s.ServiceID), "%s", p->ServiceID);
					snprintf(info_va_s.PublicationID, sizeof(info_va_s.PublicationID), "%s", p->PublicationID);
					
					parseProperty(cur, new_xmlroute, (void *)&info_va_s);
					parseNode(doc, cur, new_xmlroute, (void *)&info_va_s, NULL, NULL, NULL);
					publicationva_info_insert(&info_va_s);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MultipleLanguageInfos^MultipleLanguageInfo^PublicationDesc")){
					DBSTAR_MULTIPLELANGUAGEINFOVA_S *p = (DBSTAR_MULTIPLELANGUAGEINFOVA_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					snprintf(p->PublicationDesc, sizeof(p->PublicationDesc), "%s", (char *)szKey);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MultipleLanguageInfos^MultipleLanguageInfo^Keywords")){
					DBSTAR_MULTIPLELANGUAGEINFOVA_S *p = (DBSTAR_MULTIPLELANGUAGEINFOVA_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->Keywords, (char *)szKey, sizeof(p->Keywords)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MultipleLanguageInfos^MultipleLanguageInfo^ImageDefinition")){
					DBSTAR_MULTIPLELANGUAGEINFOVA_S *p = (DBSTAR_MULTIPLELANGUAGEINFOVA_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->ImageDefinition, (char *)szKey, sizeof(p->ImageDefinition)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MultipleLanguageInfos^MultipleLanguageInfo^Director")){
					DBSTAR_MULTIPLELANGUAGEINFOVA_S *p = (DBSTAR_MULTIPLELANGUAGEINFOVA_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->Director, (char *)szKey, sizeof(p->Director)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MultipleLanguageInfos^MultipleLanguageInfo^Episode")){
					DBSTAR_MULTIPLELANGUAGEINFOVA_S *p = (DBSTAR_MULTIPLELANGUAGEINFOVA_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->Episode, (char *)szKey, sizeof(p->Episode)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MultipleLanguageInfos^MultipleLanguageInfo^Actor")){
					DBSTAR_MULTIPLELANGUAGEINFOVA_S *p = (DBSTAR_MULTIPLELANGUAGEINFOVA_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->Actor, (char *)szKey, sizeof(p->Actor)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MultipleLanguageInfos^MultipleLanguageInfo^AudioChannel")){
					DBSTAR_MULTIPLELANGUAGEINFOVA_S *p = (DBSTAR_MULTIPLELANGUAGEINFOVA_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->AudioChannel, (char *)szKey, sizeof(p->AudioChannel)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MultipleLanguageInfos^MultipleLanguageInfo^AspectRatio")){
					DBSTAR_MULTIPLELANGUAGEINFOVA_S *p = (DBSTAR_MULTIPLELANGUAGEINFOVA_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->AspectRatio, (char *)szKey, sizeof(p->AspectRatio)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MultipleLanguageInfos^MultipleLanguageInfo^Audience")){
					DBSTAR_MULTIPLELANGUAGEINFOVA_S *p = (DBSTAR_MULTIPLELANGUAGEINFOVA_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->Audience, (char *)szKey, sizeof(p->Audience)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MultipleLanguageInfos^MultipleLanguageInfo^Model")){
					DBSTAR_MULTIPLELANGUAGEINFOVA_S *p = (DBSTAR_MULTIPLELANGUAGEINFOVA_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->Model, (char *)szKey, sizeof(p->Model)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MultipleLanguageInfos^MultipleLanguageInfo^Language")){
					DBSTAR_MULTIPLELANGUAGEINFOVA_S *p = (DBSTAR_MULTIPLELANGUAGEINFOVA_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->Language, (char *)szKey, sizeof(p->Language)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MultipleLanguageInfos^MultipleLanguageInfo^Area")){
					DBSTAR_MULTIPLELANGUAGEINFOVA_S *p = (DBSTAR_MULTIPLELANGUAGEINFOVA_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->Area, (char *)szKey, sizeof(p->Area)-1);
					xmlFree(szKey);
				}
				
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^SetInfo")){
					DBSTAR_PUBLICATION_S *p = (DBSTAR_PUBLICATION_S *)ptr;
					
					DBSTAR_PUBLICATIONSSET_S sset_s;
					memset(&sset_s,0,sizeof(sset_s));
					snprintf(sset_s.ServiceID,sizeof(sset_s.ServiceID),"%s",p->ServiceID);
					snprintf(sset_s.PublicationType, sizeof(sset_s.PublicationType), "%s", p->PublicationType);
					snprintf(sset_s.IsReserved, sizeof(sset_s.IsReserved), "%s", p->IsReserved);
					snprintf(sset_s.Visible, sizeof(sset_s.Visible), "%s", p->Visible);
					
					parseNode(doc, cur, new_xmlroute, &sset_s, NULL, NULL, NULL);
					publicationsset_insert(&sset_s);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^SetInfo^Title")){
					DBSTAR_PUBLICATIONSSET_S *p = (DBSTAR_PUBLICATIONSSET_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					snprintf(p->Title, sizeof(p->Title), "%s", (char *)szKey);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^SetInfo^Starring")){
					DBSTAR_PUBLICATIONSSET_S *p = (DBSTAR_PUBLICATIONSSET_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					snprintf(p->Starring, sizeof(p->Starring), "%s", (char *)szKey);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^SetInfo^Scenario")){
					DBSTAR_PUBLICATIONSSET_S *p = (DBSTAR_PUBLICATIONSSET_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					snprintf(p->Scenario, sizeof(p->Scenario), "%s", (char *)szKey);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^SetInfo^Classification")){
					DBSTAR_PUBLICATIONSSET_S *p = (DBSTAR_PUBLICATIONSSET_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					snprintf(p->Classification, sizeof(p->Classification), "%s", (char *)szKey);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^SetInfo^Period")){
					DBSTAR_PUBLICATIONSSET_S *p = (DBSTAR_PUBLICATIONSSET_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					snprintf(p->Period, sizeof(p->Period), "%s", (char *)szKey);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^SetInfo^SetID")){
					DBSTAR_PUBLICATIONSSET_S *p = (DBSTAR_PUBLICATIONSSET_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					snprintf(p->SetID, sizeof(p->SetID), "%s", (char *)szKey);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^SetInfo^CollectionNumber")){
					DBSTAR_PUBLICATIONSSET_S *p = (DBSTAR_PUBLICATIONSSET_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					snprintf(p->CollectionNumber, sizeof(p->CollectionNumber), "%s", (char *)szKey);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^SetInfo^Review")){
					DBSTAR_PUBLICATIONSSET_S *p = (DBSTAR_PUBLICATIONSSET_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					snprintf(p->Review, sizeof(p->Review), "%s", (char *)szKey);
					xmlFree(szKey);
				}
				
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^SubTitles")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^SubTitles^SubTitle")){
					DBSTAR_MULTIPLELANGUAGEINFOVA_S *p = (DBSTAR_MULTIPLELANGUAGEINFOVA_S *)ptr;
					
					DBSTAR_RESSUBTITLE_S subtitle_s;
					memset(&subtitle_s, 0, sizeof(subtitle_s));
					strncpy(subtitle_s.ObjectName, OBJ_PUBLICATION, sizeof(subtitle_s.ObjectName)-1);
					strncpy(subtitle_s.EntityID, p->PublicationID, sizeof(subtitle_s.EntityID)-1);
					parseNode(doc, cur, new_xmlroute, &subtitle_s, NULL, NULL, NULL);
					subtitle_insert(&subtitle_s);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^SubTitles^SubTitle^SubTitleID")){
					DBSTAR_RESSUBTITLE_S *p = (DBSTAR_RESSUBTITLE_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->SubTitleID, (char *)szKey, sizeof(p->SubTitleID)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^SubTitles^SubTitle^SubTitleName")){
					DBSTAR_RESSUBTITLE_S *p = (DBSTAR_RESSUBTITLE_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->SubTitleName, (char *)szKey, sizeof(p->SubTitleName)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^SubTitles^SubTitle^SubTitleLanguage")){
					DBSTAR_RESSUBTITLE_S *p = (DBSTAR_RESSUBTITLE_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->SubTitleLanguage, (char *)szKey, sizeof(p->SubTitleLanguage)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^SubTitles^SubTitle^SubTitleURI")){
					DBSTAR_RESSUBTITLE_S *p = (DBSTAR_RESSUBTITLE_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->SubTitleURI, (char *)szKey, sizeof(p->SubTitleURI)-1);
					xmlFree(szKey);
				}
				
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^Trailers")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^Trailers^Trailer")){
					DBSTAR_MULTIPLELANGUAGEINFOVA_S *p = (DBSTAR_MULTIPLELANGUAGEINFOVA_S *)ptr;
					
					DBSTAR_RESTRAILER_S trailer_s;
					memset(&trailer_s, 0, sizeof(trailer_s));
					strncpy(trailer_s.ObjectName, OBJ_PUBLICATION, sizeof(trailer_s.ObjectName)-1);
					strncpy(trailer_s.EntityID, p->PublicationID, sizeof(trailer_s.EntityID)-1);
					parseNode(doc, cur, new_xmlroute, &trailer_s, NULL, NULL, NULL);
					trailer_insert(&trailer_s);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^Trailers^Trailer^TrailerID")){
					DBSTAR_RESTRAILER_S *p = (DBSTAR_RESTRAILER_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->TrailerID, (char *)szKey, sizeof(p->TrailerID)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^Trailers^Trailer^TrailerName")){
					DBSTAR_RESTRAILER_S *p = (DBSTAR_RESTRAILER_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->TrailerName, (char *)szKey, sizeof(p->TrailerName)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^Trailers^Trailer^TrailerURI")){
					DBSTAR_RESTRAILER_S *p = (DBSTAR_RESTRAILER_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->TrailerURI, (char *)szKey, sizeof(p->TrailerURI)-1);
					xmlFree(szKey);
				}
				
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^Posters")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^Posters^Poster")){
					DBSTAR_MULTIPLELANGUAGEINFOVA_S *p = (DBSTAR_MULTIPLELANGUAGEINFOVA_S *)ptr;
					
					DBSTAR_RESPOSTER_S poster_s;
					memset(&poster_s, 0, sizeof(poster_s));
					strncpy(poster_s.ObjectName, OBJ_PUBLICATION, sizeof(poster_s.ObjectName)-1);
					strncpy(poster_s.EntityID, p->PublicationID, sizeof(poster_s.EntityID)-1);
					parseNode(doc, cur, new_xmlroute, &poster_s, NULL, NULL, NULL);
					poster_insert(&poster_s);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^Posters^Poster^PosterID")){
					DBSTAR_RESPOSTER_S *p = (DBSTAR_RESPOSTER_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->PosterID, (char *)szKey, sizeof(p->PosterID)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^Posters^Poster^PosterName")){
					DBSTAR_RESPOSTER_S *p = (DBSTAR_RESPOSTER_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->PosterName, (char *)szKey, sizeof(p->PosterName)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^Posters^Poster^PosterURI")){
					DBSTAR_RESPOSTER_S *p = (DBSTAR_RESPOSTER_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->PosterURI, (char *)szKey, sizeof(p->PosterURI)-1);
					xmlFree(szKey);
				}
				
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MFile")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MFile^FileID")){
					DBSTAR_PUBLICATION_S *p = (DBSTAR_PUBLICATION_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->FileID, (char *)szKey, sizeof(p->FileID)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MFile^FileNames")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MFile^FileNames^FileName")
						|| 0==strcmp(new_xmlroute, "Publication^PublicationRM^MFile^FileNames^FileName")){
					DBSTAR_PUBLICATION_S *p = (DBSTAR_PUBLICATION_S *)ptr;
					
					DBSTAR_RESSTR_S resstr_s;
					memset(&resstr_s, 0, sizeof(resstr_s));
					
					snprintf(resstr_s.ServiceID,sizeof(resstr_s.ServiceID),"%s",p->ServiceID);
					strncpy(resstr_s.ObjectName, OBJ_MFILE, sizeof(resstr_s.ObjectName)-1);
					//DEBUG("ProductID: %s\n", p_product->ProductID);
					if(strlen(p->PublicationID)>0)
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s", p->PublicationID);
					else
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s%s", OBJID_PAUSE, OBJ_MFILE);
					strncpy(resstr_s.StrName, "FileName", sizeof(resstr_s.StrName)-1);
					
					parseProperty(cur, new_xmlroute, (void *)(&resstr_s));
					
					resstr_insert(&resstr_s);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MFile^FileType")){
					DBSTAR_PUBLICATION_S *p = (DBSTAR_PUBLICATION_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->FileType, (char *)szKey, sizeof(p->FileType)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MFile^FileSize")){
					DBSTAR_PUBLICATION_S *p = (DBSTAR_PUBLICATION_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->FileSize, (char *)szKey, sizeof(p->FileSize)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MFile^Duration")){
					DBSTAR_PUBLICATION_S *p = (DBSTAR_PUBLICATION_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->Duration, (char *)szKey, sizeof(p->Duration)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MFile^FileURI")){
					DBSTAR_PUBLICATION_S *p = (DBSTAR_PUBLICATION_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->FileURI, (char *)szKey, sizeof(p->FileURI)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MFile^Resolution")){
					DBSTAR_PUBLICATION_S *p = (DBSTAR_PUBLICATION_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->Resolution, (char *)szKey, sizeof(p->Resolution)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MFile^BitRate")){
					DBSTAR_PUBLICATION_S *p = (DBSTAR_PUBLICATION_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->BitRate, (char *)szKey, sizeof(p->BitRate)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MFile^FileFormat")){
					DBSTAR_PUBLICATION_S *p = (DBSTAR_PUBLICATION_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->FileFormat, (char *)szKey, sizeof(p->FileFormat)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MFile^FileFormat")){
					DBSTAR_PUBLICATION_S *p = (DBSTAR_PUBLICATION_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->FileFormat, (char *)szKey, sizeof(p->FileFormat)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MFile^CodeFormat")){
					DBSTAR_PUBLICATION_S *p = (DBSTAR_PUBLICATION_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->CodeFormat, (char *)szKey, sizeof(p->CodeFormat)-1);
					xmlFree(szKey);
				}
				
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^Preview")){
					DBSTAR_PUBLICATION_S *p = (DBSTAR_PUBLICATION_S *)ptr;
					
					DBSTAR_PREVIEW_S preview_s;
					memset(&preview_s,0,sizeof(preview_s));
					snprintf(preview_s.ServiceID,sizeof(preview_s.ServiceID),"%s",p->ServiceID);
					snprintf(preview_s.PublicationID,sizeof(preview_s.PublicationID),"%s",p->PublicationID);
					
					parseNode(doc, cur, new_xmlroute, &preview_s, NULL, NULL, NULL);
					
					preview_insert(&preview_s);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^Preview^PreviewID")){
					DBSTAR_PREVIEW_S *p = (DBSTAR_PREVIEW_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->PreviewID, (char *)szKey, sizeof(p->PreviewID)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^Preview^PreviewNames")){
//					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^Preview^PreviewNames^PreviewName")){
//					DBSTAR_PUBLICATION_S *p = (DBSTAR_PUBLICATION_S *)ptr;
//					
//					DBSTAR_RESSTR_S resstr_s;
//					memset(&resstr_s, 0, sizeof(resstr_s));
//					
//					snprintf(resstr_s.ServiceID,sizeof(resstr_s.ServiceID),"%s",p->ServiceID);
//					strncpy(resstr_s.ObjectName, OBJ_MFILE, sizeof(resstr_s.ObjectName)-1);
//					//DEBUG("ProductID: %s\n", p_product->ProductID);
//					if(strlen(p->PublicationID)>0)
//						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s", p->PublicationID);
//					else
//						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s%s", OBJID_PAUSE, OBJ_MFILE);
//					strncpy(resstr_s.StrName, "FileName", sizeof(resstr_s.StrName)-1);
//					
//					parseProperty(cur, new_xmlroute, (void *)(&resstr_s));
//					
//					resstr_insert(&resstr_s);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^Preview^PreviewType")){
					DBSTAR_PREVIEW_S *p = (DBSTAR_PREVIEW_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->PreviewType, (char *)szKey, sizeof(p->PreviewType)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^Preview^PreviewSize")){
					DBSTAR_PREVIEW_S *p = (DBSTAR_PREVIEW_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->PreviewSize, (char *)szKey, sizeof(p->PreviewSize)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^Preview^Duration")){
					DBSTAR_PREVIEW_S *p = (DBSTAR_PREVIEW_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->Duration, (char *)szKey, sizeof(p->Duration)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^Preview^ShowTime")){
					DBSTAR_PREVIEW_S *p = (DBSTAR_PREVIEW_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->ShowTime, (char *)szKey, sizeof(p->ShowTime)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^Preview^PreviewURI")){
					DBSTAR_PREVIEW_S *p = (DBSTAR_PREVIEW_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->PreviewURI, (char *)szKey, sizeof(p->PreviewURI)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^Preview^Resolution")){
					DBSTAR_PREVIEW_S *p = (DBSTAR_PREVIEW_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->Resolution, (char *)szKey, sizeof(p->Resolution)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^Preview^BitRate")){
					DBSTAR_PREVIEW_S *p = (DBSTAR_PREVIEW_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->BitRate, (char *)szKey, sizeof(p->BitRate)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^Preview^PreviewFormat")){
					DBSTAR_PREVIEW_S *p = (DBSTAR_PREVIEW_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->PreviewFormat, (char *)szKey, sizeof(p->PreviewFormat)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^Preview^CodeFormat")){
					DBSTAR_PREVIEW_S *p = (DBSTAR_PREVIEW_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->CodeFormat, (char *)szKey, sizeof(p->CodeFormat)-1);
					xmlFree(szKey);
				}

	// PublicationRM in Publication
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MultipleLanguageInfos")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MultipleLanguageInfos^MultipleLanguageInfo")){
					DBSTAR_PUBLICATION_S *p = (DBSTAR_PUBLICATION_S *)ptr;
					
					DBSTAR_MULTIPLELANGUAGEINFORM_S info_rm_s;
					memset(&info_rm_s, 0, sizeof(info_rm_s));
					snprintf(info_rm_s.ServiceID, sizeof(info_rm_s.ServiceID), "%s", p->ServiceID);
					snprintf(info_rm_s.PublicationID, sizeof(info_rm_s.PublicationID), "%s", p->PublicationID);
					
#ifdef GET_TITLE_FROM_PUBLICATIONNAME
					DEBUG("Compatible for early DbstarLauncher, add 'Title' from PublicationName values (%s)\n",p->PublicationName);
					snprintf(info_rm_s.Title, sizeof(info_rm_s.Title), "%s", p->PublicationName);
#endif
					
					parseProperty(cur, new_xmlroute, (void *)&info_rm_s);
					parseNode(doc, cur, new_xmlroute, (void *)&info_rm_s, NULL, NULL, NULL);
					publicationrm_info_insert(&info_rm_s);
					
					snprintf(p->SetID,sizeof(p->SetID),"%s",info_rm_s.SetID);
					snprintf(p->SetName,sizeof(p->SetName),"%s",info_rm_s.SetName);
					snprintf(p->SetDesc,sizeof(p->SetDesc),"%s",info_rm_s.SetDesc);
					snprintf(p->SetPosterID,sizeof(p->SetPosterID),"%s",info_rm_s.SetPosterID);
					snprintf(p->SetPosterName,sizeof(p->SetPosterName),"%s",info_rm_s.SetPosterName);
					snprintf(p->SetPosterURI,sizeof(p->SetPosterURI),"%s",info_rm_s.SetPosterURI);
					
					snprintf(p->RMCategory,sizeof(p->RMCategory),"%s",info_rm_s.RMCategory);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MultipleLanguageInfos^MultipleLanguageInfo^PublishID")){
					DBSTAR_MULTIPLELANGUAGEINFORM_S *p = (DBSTAR_MULTIPLELANGUAGEINFORM_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					snprintf(p->PublishID, sizeof(p->PublishID), "%s", (char *)szKey);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MultipleLanguageInfos^MultipleLanguageInfo^RMCategory")){
					DBSTAR_MULTIPLELANGUAGEINFORM_S *p = (DBSTAR_MULTIPLELANGUAGEINFORM_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					snprintf(p->RMCategory, sizeof(p->RMCategory), "%s", (char *)szKey);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MultipleLanguageInfos^MultipleLanguageInfo^Author")){
					DBSTAR_MULTIPLELANGUAGEINFORM_S *p = (DBSTAR_MULTIPLELANGUAGEINFORM_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					snprintf(p->Author, sizeof(p->Author), "%s", (char *)szKey);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MultipleLanguageInfos^MultipleLanguageInfo^Publisher")){
					DBSTAR_MULTIPLELANGUAGEINFORM_S *p = (DBSTAR_MULTIPLELANGUAGEINFORM_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					snprintf(p->Publisher, sizeof(p->Publisher), "%s", (char *)szKey);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MultipleLanguageInfos^MultipleLanguageInfo^Issue")){
					DBSTAR_MULTIPLELANGUAGEINFORM_S *p = (DBSTAR_MULTIPLELANGUAGEINFORM_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					snprintf(p->Issue, sizeof(p->Issue), "%s", (char *)szKey);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MultipleLanguageInfos^MultipleLanguageInfo^Keywords")){
					DBSTAR_MULTIPLELANGUAGEINFORM_S *p = (DBSTAR_MULTIPLELANGUAGEINFORM_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					snprintf(p->Keywords, sizeof(p->Keywords), "%s", (char *)szKey);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MultipleLanguageInfos^MultipleLanguageInfo^Description")){
					DBSTAR_MULTIPLELANGUAGEINFORM_S *p = (DBSTAR_MULTIPLELANGUAGEINFORM_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					snprintf(p->Description, sizeof(p->Description), "%s", (char *)szKey);
					xmlFree(szKey);
				}
				
				// SetInfo in PublicationRM's MultipleLanguageInfo
				// SetInfo节点临时存储在DBSTAR_MULTIPLELANGUAGEINFORM_S，等处理完毕后要先拷贝为DBSTAR_PUBLICATION_S，然后通过DBSTAR_PUBLICATION_S存储到数据库之Publication表中
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MultipleLanguageInfos^MultipleLanguageInfo^SetInfo")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MultipleLanguageInfos^MultipleLanguageInfo^SetInfo^SetID")){
					DBSTAR_MULTIPLELANGUAGEINFORM_S *p = (DBSTAR_MULTIPLELANGUAGEINFORM_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					snprintf(p->SetID, sizeof(p->SetID), "%s", (char *)szKey);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MultipleLanguageInfos^MultipleLanguageInfo^SetInfo^SetName")){
					DBSTAR_MULTIPLELANGUAGEINFORM_S *p = (DBSTAR_MULTIPLELANGUAGEINFORM_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					snprintf(p->SetName, sizeof(p->SetName), "%s", (char *)szKey);
					xmlFree(szKey);
					
					DEBUG("Compatible for early DbstarLauncher, add 'Title' from SetName values (%s)\n",p->SetName);
					snprintf(p->Title, sizeof(p->Title), "%s", p->SetName);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MultipleLanguageInfos^MultipleLanguageInfo^SetInfo^SetDesc")){
					DBSTAR_MULTIPLELANGUAGEINFORM_S *p = (DBSTAR_MULTIPLELANGUAGEINFORM_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					snprintf(p->SetDesc, sizeof(p->SetDesc), "%s", (char *)szKey);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MultipleLanguageInfos^MultipleLanguageInfo^SetInfo^Poster")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MultipleLanguageInfos^MultipleLanguageInfo^SetInfo^Poster^PosterID")){
					DBSTAR_MULTIPLELANGUAGEINFORM_S *p = (DBSTAR_MULTIPLELANGUAGEINFORM_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					snprintf(p->SetPosterID, sizeof(p->SetPosterID), "%s", (char *)szKey);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MultipleLanguageInfos^MultipleLanguageInfo^SetInfo^Poster^PosterName")){
					DBSTAR_MULTIPLELANGUAGEINFORM_S *p = (DBSTAR_MULTIPLELANGUAGEINFORM_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					snprintf(p->SetPosterName, sizeof(p->SetPosterName), "%s", (char *)szKey);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MultipleLanguageInfos^MultipleLanguageInfo^SetInfo^Poster^PosterURI")){
					DBSTAR_MULTIPLELANGUAGEINFORM_S *p = (DBSTAR_MULTIPLELANGUAGEINFORM_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					snprintf(p->SetPosterURI, sizeof(p->SetPosterURI), "%s", (char *)szKey);
					xmlFree(szKey);
				}
				
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MultipleLanguageInfos^MultipleLanguageInfo^Posters")){
					DBSTAR_MULTIPLELANGUAGEINFORM_S *p = (DBSTAR_MULTIPLELANGUAGEINFORM_S *)ptr;
					
					DBSTAR_RESPOSTER_S poster_s;
					memset(&poster_s, 0, sizeof(poster_s));
					strncpy(poster_s.ObjectName, OBJ_PUBLICATION, sizeof(poster_s.ObjectName)-1);
					strncpy(poster_s.EntityID, p->PublicationID, sizeof(poster_s.EntityID)-1);
					parseNode(doc, cur, new_xmlroute, &poster_s, NULL, NULL, NULL);
					
					if(0==strlen(poster_s.PosterURI)){
						PRINTF("PosterURI of Publication %s is NULL, copy from SetPosterURI(%s)\n",p->PublicationID,p->SetPosterURI);
						snprintf(poster_s.PosterID,sizeof(poster_s.PosterID),"%s",p->SetPosterID);
						snprintf(poster_s.PosterName,sizeof(poster_s.PosterName),"%s",p->SetPosterName);
						snprintf(poster_s.PosterURI,sizeof(poster_s.PosterURI),"%s",p->SetPosterURI);
					}
					
					poster_insert(&poster_s);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MultipleLanguageInfos^MultipleLanguageInfo^Posters^PosterID")){
					DBSTAR_RESPOSTER_S *p = (DBSTAR_RESPOSTER_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					snprintf(p->PosterID, sizeof(p->PosterID), "%s", (char *)szKey);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MultipleLanguageInfos^MultipleLanguageInfo^Posters^PosterName")){
					DBSTAR_RESPOSTER_S *p = (DBSTAR_RESPOSTER_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					snprintf(p->PosterName, sizeof(p->PosterName), "%s", (char *)szKey);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MultipleLanguageInfos^MultipleLanguageInfo^Posters^PosterURI")){
					DBSTAR_RESPOSTER_S *p = (DBSTAR_RESPOSTER_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					snprintf(p->PosterURI, sizeof(p->PosterURI), "%s", (char *)szKey);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MultipleLanguageInfos^MultipleLanguageInfo^PublishDate")){
					DBSTAR_MULTIPLELANGUAGEINFORM_S *p = (DBSTAR_MULTIPLELANGUAGEINFORM_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					snprintf(p->PublishDate, sizeof(p->PublishDate), "%s", (char *)szKey);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MultipleLanguageInfos^MultipleLanguageInfo^PublishWeek")){
					DBSTAR_MULTIPLELANGUAGEINFORM_S *p = (DBSTAR_MULTIPLELANGUAGEINFORM_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					snprintf(p->PublishWeek, sizeof(p->PublishWeek), "%s", (char *)szKey);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MultipleLanguageInfos^MultipleLanguageInfo^PublishPlace")){
					DBSTAR_MULTIPLELANGUAGEINFORM_S *p = (DBSTAR_MULTIPLELANGUAGEINFORM_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					snprintf(p->PublishPlace, sizeof(p->PublishPlace), "%s", (char *)szKey);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MultipleLanguageInfos^MultipleLanguageInfo^CopyrightInfo")){
					DBSTAR_MULTIPLELANGUAGEINFORM_S *p = (DBSTAR_MULTIPLELANGUAGEINFORM_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					snprintf(p->CopyrightInfo, sizeof(p->CopyrightInfo), "%s", (char *)szKey);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MultipleLanguageInfos^MultipleLanguageInfo^TotalEdition")){
					DBSTAR_MULTIPLELANGUAGEINFORM_S *p = (DBSTAR_MULTIPLELANGUAGEINFORM_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					snprintf(p->TotalEdition, sizeof(p->TotalEdition), "%s", (char *)szKey);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MultipleLanguageInfos^MultipleLanguageInfo^Data")){
					DBSTAR_MULTIPLELANGUAGEINFORM_S *p = (DBSTAR_MULTIPLELANGUAGEINFORM_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					snprintf(p->Data, sizeof(p->Data), "%s", (char *)szKey);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MultipleLanguageInfos^MultipleLanguageInfo^Format")){
					DBSTAR_MULTIPLELANGUAGEINFORM_S *p = (DBSTAR_MULTIPLELANGUAGEINFORM_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					snprintf(p->Format, sizeof(p->Format), "%s", (char *)szKey);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MultipleLanguageInfos^MultipleLanguageInfo^TotalIssue")){
					DBSTAR_MULTIPLELANGUAGEINFORM_S *p = (DBSTAR_MULTIPLELANGUAGEINFORM_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					snprintf(p->TotalIssue, sizeof(p->TotalIssue), "%s", (char *)szKey);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MultipleLanguageInfos^MultipleLanguageInfo^Recommendation")){
					DBSTAR_MULTIPLELANGUAGEINFORM_S *p = (DBSTAR_MULTIPLELANGUAGEINFORM_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					snprintf(p->Recommendation, sizeof(p->Recommendation), "%s", (char *)szKey);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MultipleLanguageInfos^MultipleLanguageInfo^Words")){
					DBSTAR_MULTIPLELANGUAGEINFORM_S *p = (DBSTAR_MULTIPLELANGUAGEINFORM_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					snprintf(p->Words, sizeof(p->Words), "%s", (char *)szKey);
					xmlFree(szKey);
				}
				
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MFile")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MFile^FileID")){
					DBSTAR_PUBLICATION_S *p = (DBSTAR_PUBLICATION_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->FileID, (char *)szKey, sizeof(p->FileID)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MFile^FileNames")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MFile^FileNames^FileName")){
					DBSTAR_PUBLICATION_S *p = (DBSTAR_PUBLICATION_S *)ptr;
					
					DBSTAR_RESSTR_S resstr_s;
					memset(&resstr_s, 0, sizeof(resstr_s));
					
					snprintf(resstr_s.ServiceID,sizeof(resstr_s.ServiceID),"%s",p->ServiceID);
					strncpy(resstr_s.ObjectName, OBJ_MFILE, sizeof(resstr_s.ObjectName)-1);
					//DEBUG("ProductID: %s\n", p_product->ProductID);
					if(strlen(p->PublicationID)>0)
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s", p->PublicationID);
					else
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s%s", OBJID_PAUSE, OBJ_MFILE);
					strncpy(resstr_s.StrName, "FileName", sizeof(resstr_s.StrName)-1);
					
					parseProperty(cur, new_xmlroute, (void *)(&resstr_s));
					
					resstr_insert(&resstr_s);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MFile^FileType")){
					DBSTAR_PUBLICATION_S *p = (DBSTAR_PUBLICATION_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->FileType, (char *)szKey, sizeof(p->FileType)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MFile^FileSize")){
					DBSTAR_PUBLICATION_S *p = (DBSTAR_PUBLICATION_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->FileSize, (char *)szKey, sizeof(p->FileSize)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MFile^FileURI")){
					DBSTAR_PUBLICATION_S *p = (DBSTAR_PUBLICATION_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->FileURI, (char *)szKey, sizeof(p->FileURI)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MFile^FileFormat")){
					DBSTAR_PUBLICATION_S *p = (DBSTAR_PUBLICATION_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->FileFormat, (char *)szKey, sizeof(p->FileFormat)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationRM^MFile^CodeFormat")){
					DBSTAR_PUBLICATION_S *p = (DBSTAR_PUBLICATION_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->CodeFormat, (char *)szKey, sizeof(p->CodeFormat)-1);
					xmlFree(szKey);
				}
				else
					PRINTF("CAN NOT proccess such %s\n",new_xmlroute);
			}
			
// Column.xml
			else if(0==strncmp(new_xmlroute, "Columns^", strlen("Columns^"))){
				if(0==strcmp(new_xmlroute, "Columns^Column")){
					DBSTAR_COLUMN_S *p_column = (DBSTAR_COLUMN_S *)ptr;
					char tmp_serviceid[64];
					snprintf(tmp_serviceid,sizeof(tmp_serviceid),"%s",p_column->ServiceID);
					memset(ptr, 0, sizeof(DBSTAR_COLUMN_S));
					snprintf(p_column->ServiceID,sizeof(p_column->ServiceID),"%s",tmp_serviceid);
					
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
					if(0!=column_insert((DBSTAR_COLUMN_S *)ptr)){
						DEBUG("insert a column record to db failed\n");
						process_over = XML_EXIT_ERROR;
					}
				}
				else if(0==strcmp(new_xmlroute, "Columns^Column^ColumnID")){
					DBSTAR_COLUMN_S *p_column = (DBSTAR_COLUMN_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p_column->ColumnID, (char *)szKey, sizeof(p_column->ColumnID)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Columns^Column^ParentID")){
					DBSTAR_COLUMN_S *p_column = (DBSTAR_COLUMN_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					if(strlen((char *)szKey)>0)
						strncpy(p_column->ParentID, (char *)szKey, sizeof(p_column->ParentID)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Columns^Column^DisplayNames"))
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
				else if(0==strcmp(new_xmlroute, "Columns^Column^DisplayNames^DisplayName")){
					DBSTAR_COLUMN_S *p_column = (DBSTAR_COLUMN_S *)ptr;
					
					DBSTAR_RESSTR_S resstr_s;
					memset(&resstr_s, 0, sizeof(resstr_s));
					
					snprintf(resstr_s.ServiceID,sizeof(resstr_s.ServiceID),"%s",p_column->ServiceID);
					strncpy(resstr_s.ObjectName, OBJ_COLUMN, sizeof(resstr_s.ObjectName)-1);
					if(strlen(p_column->ColumnID)>0)
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s", p_column->ColumnID);
					else
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s%s", OBJID_PAUSE, OBJ_COLUMN);
					strncpy(resstr_s.StrName, "DisplayName", sizeof(resstr_s.StrName)-1);
					
					parseProperty(cur, new_xmlroute, (void *)(&resstr_s));
					
					resstr_insert(&resstr_s);
				}
				else if(0==strcmp(new_xmlroute, "Columns^Column^Path")){
					DBSTAR_COLUMN_S *p_column = (DBSTAR_COLUMN_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p_column->Path, (char *)szKey, sizeof(p_column->Path)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Columns^Column^ColumnType")){
					DBSTAR_COLUMN_S *p_column = (DBSTAR_COLUMN_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p_column->ColumnType, (char *)szKey, sizeof(p_column->ColumnType)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Columns^Column^ColumnIcons"))
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
				else if(0==strcmp(new_xmlroute, "Columns^Column^ColumnIcons^ColumnIcon")){
					DBSTAR_COLUMN_S *p_column = (DBSTAR_COLUMN_S *)ptr;
					
					COLUMNICON_S columnicon_s;
					memset(&columnicon_s, 0, sizeof(columnicon_s));
					parseProperty(cur, new_xmlroute, &columnicon_s);
					if(0==strcmp(columnicon_s.type, "losefocus"))
						strncpy(p_column->ColumnIcon_losefocus, columnicon_s.uri, sizeof(p_column->ColumnIcon_losefocus)-1);
					else if(0==strcmp(columnicon_s.type, "getfocus"))
						strncpy(p_column->ColumnIcon_getfocus, columnicon_s.uri, sizeof(p_column->ColumnIcon_getfocus)-1);
					else if(0==strcmp(columnicon_s.type, "onclick"))
						strncpy(p_column->ColumnIcon_onclick, columnicon_s.uri, sizeof(p_column->ColumnIcon_onclick)-1);
				}
			}
			
// GuideList.xml
			else if(0==strncmp(new_xmlroute, "GuideList^", strlen("GuideList^"))){
				if(0==strcmp(new_xmlroute, "GuideList^Date")){
					DBSTAR_GUIDELIST_S *p_guidelist = (DBSTAR_GUIDELIST_S *)ptr;
					
					memset(p_guidelist->DateValue,0,sizeof(p_guidelist->DateValue));
					memset(p_guidelist->GuideListID,0,sizeof(p_guidelist->GuideListID));
					memset(p_guidelist->productID,0,sizeof(p_guidelist->productID));
					memset(p_guidelist->PublicationID,0,sizeof(p_guidelist->PublicationID));
					
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "GuideList^Date^DateValue")){
					DBSTAR_GUIDELIST_S *p_guidelist = (DBSTAR_GUIDELIST_S *)ptr;
					
					szKey = xmlNodeGetContent(cur);
					strncpy(p_guidelist->DateValue, (char *)szKey, sizeof(p_guidelist->DateValue)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "GuideList^Date^Product")){
					DBSTAR_GUIDELIST_S *p_guidelist = (DBSTAR_GUIDELIST_S *)ptr;
					
					memset(p_guidelist->GuideListID,0,sizeof(p_guidelist->GuideListID));
					memset(p_guidelist->productID,0,sizeof(p_guidelist->productID));
					memset(p_guidelist->PublicationID,0,sizeof(p_guidelist->PublicationID));
					
					parseProperty(cur, new_xmlroute, ptr);
					
					if(0==ProductID_check(p_guidelist->productID)){
						if(product_preview_check_in_trans(p_guidelist->productID)>0){
							DEBUG("Product(%s) in GuideList is a Preview, do NOT insert into db\n", p_guidelist->productID);
						}
						else{
							DEBUG("Product(%s) in GuideList is a normal product, insert into db and showing\n", p_guidelist->productID);
							parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
						}
					}
					else{
						DEBUG("Product(%s) in GuideList is NOT a valid product, do NOT insert into db\n", p_guidelist->productID);
					}
				}
				else if(0==strcmp(new_xmlroute, "GuideList^Date^Product^Item")){
					/*
					针对每个Item，清理除DataValue之外的其他变量
					*/
					DBSTAR_GUIDELIST_S *p_guidelist = (DBSTAR_GUIDELIST_S *)ptr;
					
					memset(p_guidelist->GuideListID,0,sizeof(p_guidelist->GuideListID));
					memset(p_guidelist->PublicationID,0,sizeof(p_guidelist->PublicationID));
					
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
					guidelist_insert((DBSTAR_GUIDELIST_S *)ptr);
				}
				else if(0==strcmp(new_xmlroute, "GuideList^Date^Product^Item^PublicationID")){
					DBSTAR_GUIDELIST_S *p_guidelist = (DBSTAR_GUIDELIST_S *)ptr;
					
					szKey = xmlNodeGetContent(cur);
					snprintf(p_guidelist->PublicationID,sizeof(p_guidelist->PublicationID),"%s", (char *)szKey);
					xmlFree(szKey);
					snprintf(p_guidelist->GuideListID, sizeof(p_guidelist->GuideListID), "%s_%s", p_guidelist->productID, p_guidelist->PublicationID);
				}
				else if(0==strcmp(new_xmlroute, "GuideList^Date^Product^Item^PublicationNames")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "GuideList^Date^Product^Item^PublicationNames^PublicationName")){
					DBSTAR_GUIDELIST_S *p_guidelist = (DBSTAR_GUIDELIST_S *)ptr;
					
					DBSTAR_RESSTR_S resstr_s;
					memset(&resstr_s, 0, sizeof(resstr_s));
					
					snprintf(resstr_s.ServiceID,sizeof(resstr_s.ServiceID),"%s",p_guidelist->ServiceID);
					strncpy(resstr_s.ObjectName, OBJ_GUIDELIST, sizeof(resstr_s.ObjectName)-1);
					if(strlen(p_guidelist->GuideListID)>0)
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s", p_guidelist->GuideListID);
					else
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s%s", OBJID_PAUSE, OBJ_GUIDELIST);
					strncpy(resstr_s.StrName, "PublicationName", sizeof(resstr_s.StrName)-1);
					
					parseProperty(cur, new_xmlroute, (void *)(&resstr_s));
					
					resstr_insert(&resstr_s);
				}
				else if(0==strcmp(new_xmlroute, "GuideList^Date^Product^Item^ColumnNames")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "GuideList^Date^Product^Item^ColumnNames^ColumnName")){
					DBSTAR_GUIDELIST_S *p_guidelist = (DBSTAR_GUIDELIST_S *)ptr;
					
					DBSTAR_RESSTR_S resstr_s;
					memset(&resstr_s, 0, sizeof(resstr_s));
					
					snprintf(resstr_s.ServiceID,sizeof(resstr_s.ServiceID),"%s",p_guidelist->ServiceID);
					strncpy(resstr_s.ObjectName, OBJ_GUIDELIST, sizeof(resstr_s.ObjectName)-1);
					if(strlen(p_guidelist->GuideListID)>0)
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s", p_guidelist->GuideListID);
					else
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s%s", OBJID_PAUSE, OBJ_GUIDELIST);
					strncpy(resstr_s.StrName, "ColumnName", sizeof(resstr_s.StrName)-1);
					
					parseProperty(cur, new_xmlroute, (void *)(&resstr_s));
					
					resstr_insert(&resstr_s);
				}
				else if(0==strcmp(new_xmlroute, "GuideList^Date^Product^Item^PublicationDescs")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "GuideList^Date^Product^Item^PublicationDescs^PublicationDesc")){
					DBSTAR_GUIDELIST_S *p_guidelist = (DBSTAR_GUIDELIST_S *)ptr;
					
					DBSTAR_RESSTR_S resstr_s;
					memset(&resstr_s, 0, sizeof(resstr_s));
					
					snprintf(resstr_s.ServiceID,sizeof(resstr_s.ServiceID),"%s",p_guidelist->ServiceID);
					strncpy(resstr_s.ObjectName, OBJ_GUIDELIST, sizeof(resstr_s.ObjectName)-1);
					
					if(strlen(p_guidelist->GuideListID) > 0){
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s", p_guidelist->GuideListID);
					}
					else
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s%s", OBJID_PAUSE, OBJ_GUIDELIST);
					strncpy(resstr_s.StrName, "PublicationDesc", sizeof(resstr_s.StrName)-1);
					
					parseProperty(cur, new_xmlroute, (void *)(&resstr_s));
					
					resstr_insert(&resstr_s);
				}
				else
					DEBUG("can not parse such xml route:[%s]\n",new_xmlroute);
 			}

// ProductDesc.xml 当前投递单
			else if(0==strncmp(new_xmlroute, "ProductDesc^", strlen("ProductDesc^"))){
				if(0==strcmp(new_xmlroute, "ProductDesc^ReceivePublications")){
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					productdesc_clear((DBSTAR_PRODUCTDESC_S *)ptr, 1);
					parseProperty(cur, new_xmlroute, ptr);
					snprintf(p->ReceiveType, sizeof(p->ReceiveType), "%d",RECEIVETYPE_PUBLICATION);
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceivePublications^Product")){
					productdesc_clear((DBSTAR_PRODUCTDESC_S *)ptr, 2);
					parseProperty(cur, new_xmlroute, ptr);
					
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					
					if(1==motherdisc_processing() || 0==ProductID_check(p->productID)){
						DEBUG("I'll receive productID: %s\n", p->productID);
						if(product_preview_check_in_trans(p->productID)>0){
							DEBUG("Product(%s) in ProductDesc is a Preview, refresh ReceiveType as %d\n", p->productID,RECEIVETYPE_PREVIEW);
							snprintf(p->ReceiveType, sizeof(p->ReceiveType), "%d", RECEIVETYPE_PREVIEW);
						}
						else{
							DEBUG("this product is not a preview, normal publication\n");
							snprintf(p->ReceiveType, sizeof(p->ReceiveType), "%d",RECEIVETYPE_PUBLICATION);
						}
						
						parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
					}
					else
						DEBUG("I'll REJECT productID: %s\n", p->productID);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceivePublications^Product^Publication")){
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					
					productdesc_clear(p, 3);
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
					
					if(publication_unselect_check_in_trans(p->ID)>0){
						DEBUG("Publication %s is unselect by user, do not insert into table ProductDesc\n", p->ID);
					}
					else{
						productdesc_insert(p);
					}
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceivePublications^Product^Publication^PublicationID")){
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					
					szKey = xmlNodeGetContent(cur);
					strncpy(p->ID, (char *)szKey, sizeof(p->ID)-1);
					snprintf(p->ProductDescID, sizeof(p->ProductDescID),"%s_%s_%s", p->ServiceID,p->ReceiveType,p->ID);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceivePublications^Product^Publication^PublicationNames")){
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					
					if(publication_unselect_check_in_trans(p->ID)>0){
						DEBUG("Publication %s is unselect by user, do not insert PublicationNames\n", p->ID);
					}
					else{
						parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
					}
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceivePublications^Product^Publication^PublicationNames^PublicationName")){
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					
					DBSTAR_RESSTR_S resstr_s;
					memset(&resstr_s, 0, sizeof(resstr_s));
					
					snprintf(resstr_s.ServiceID,sizeof(resstr_s.ServiceID),"%s",p->ServiceID);
					strncpy(resstr_s.ObjectName, OBJ_PRODUCTDESC, sizeof(resstr_s.ObjectName)-1);
					if(strlen(p->ProductDescID)>0)
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s", p->ProductDescID);
					else
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s%s", OBJID_PAUSE, OBJ_PRODUCTDESC);
					strncpy(resstr_s.StrName, "ProductDescName", sizeof(resstr_s.StrName)-1);
					
					parseProperty(cur, new_xmlroute, (void *)(&resstr_s));
					
					resstr_insert(&resstr_s);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceivePublications^Product^Publication^TotalSize")){
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					
					szKey = xmlNodeGetContent(cur);
					strncpy(p->TotalSize, (char *)szKey, sizeof(p->TotalSize)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceivePublications^Product^Publication^SetID")){
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					
					szKey = xmlNodeGetContent(cur);
					strncpy(p->SetID, (char *)szKey, sizeof(p->SetID)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceivePublications^Product^Publication^PublicationURI")){
					parseProperty(cur, new_xmlroute, ptr);
					
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					char tmp_URI[512];
					szKey = xmlNodeGetContent(cur);
					snprintf(tmp_URI,sizeof(tmp_URI),"%s",(char *)szKey);
					xmlFree(szKey);
					snprintf(p->URI,sizeof(p->URI),"%s",p->rootPath);
					if('/'!=tmp_URI[0])
						snprintf(p->URI+strlen(p->URI),sizeof(p->URI)-strlen(p->URI),"/");
					snprintf(p->URI+strlen(p->URI),sizeof(p->URI)-strlen(p->URI),"%s",tmp_URI);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceivePublications^Product^Publication^Columns")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceivePublications^Product^Publication^Columns^Column")){
					parseProperty(cur, new_xmlroute, ptr);
				}
				
				else if(0==strcmp(new_xmlroute, "ProductDesc^PushDate")){
					parseProperty(cur, new_xmlroute, ptr);
				}
				
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceiveSProduct")){
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					productdesc_clear((DBSTAR_PRODUCTDESC_S *)ptr, 1);
					parseProperty(cur, new_xmlroute, ptr);
					
					memset(&xmlinfo_in_recv, 0, sizeof(xmlinfo_in_recv));
					snprintf(xmlinfo_in_recv.PushFlag,sizeof(xmlinfo_in_recv.PushFlag),"%d",SPRODUCT_XML);
					read_xmlver_in_trans(&xmlinfo_in_recv,old_xmlver_in_recv,sizeof(old_xmlver_in_recv));
					if((strlen(old_xmlver_in_recv)>0 && 0==strcmp(old_xmlver_in_recv, p->version))){
						DEBUG("old ver: %s, new ver: %s, no need to regist and parse\n",old_xmlver_in_recv, p->version);
					}
					else{
						snprintf(p->ReceiveType, sizeof(p->ReceiveType), "%d",RECEIVETYPE_SPRODUCT);
						parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
						
						snprintf(xmlinfo_in_recv.Version,sizeof(xmlinfo_in_recv.Version),"%s",p->version);
						snprintf(xmlinfo_in_recv.URI,sizeof(xmlinfo_in_recv.URI),"%s",p->DescURI);
						
						productdesc_insert((DBSTAR_PRODUCTDESC_S *)ptr);
						
						xmlinfo_insert(&xmlinfo_in_recv);
					}
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceiveSProduct^SProductID")){
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					
					szKey = xmlNodeGetContent(cur);
					strncpy(p->ID, (char *)szKey, sizeof(p->ID)-1);
					snprintf(p->ProductDescID, sizeof(p->ProductDescID),"%s_%s_%s", p->ServiceID,p->ReceiveType,p->ID);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceiveSProduct^SProductNames")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceiveSProduct^SProductNames^SProductName")){
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					
					DBSTAR_RESSTR_S resstr_s;
					memset(&resstr_s, 0, sizeof(resstr_s));
					
					snprintf(resstr_s.ServiceID,sizeof(resstr_s.ServiceID),"%s",p->ServiceID);
					strncpy(resstr_s.ObjectName, OBJ_PRODUCTDESC, sizeof(resstr_s.ObjectName)-1);
					if(strlen(p->ProductDescID)>0)
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s", p->ProductDescID);
					else
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s%s", OBJID_PAUSE, OBJ_PRODUCTDESC);
					strncpy(resstr_s.StrName, "SProductName", sizeof(resstr_s.StrName)-1);
					
					parseProperty(cur, new_xmlroute, (void *)(&resstr_s));
					
					resstr_insert(&resstr_s);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceiveSProduct^TotalSize")){
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					
					szKey = xmlNodeGetContent(cur);
					snprintf(p->TotalSize,sizeof(p->TotalSize),"%s",(char *)szKey);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceiveSProduct^SProductURI")){
					parseProperty(cur, new_xmlroute, ptr);
					
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					char tmp_URI[512];
					szKey = xmlNodeGetContent(cur);
					snprintf(tmp_URI,sizeof(tmp_URI),"%s",(char *)szKey);
					xmlFree(szKey);
					
					snprintf(p->URI,sizeof(p->URI),"%s",p->rootPath);
					if('/'!=tmp_URI[0])
						snprintf(p->URI+strlen(p->URI),sizeof(p->URI)-strlen(p->URI),"/");
					snprintf(p->URI+strlen(p->URI),sizeof(p->URI)-strlen(p->URI),"%s",tmp_URI);
					
					/*
					比较操蛋，有时tmp_URI长度为0，p->URI指向pushroot/pushinfo目录，导致直接删除了所有信息文件。
					只有在非母盘状态下才需要删除旧目录
					*/
					if(strlen(tmp_URI)>0 && 0==motherdisc_processing()){
						// 显式清理本目录，避免本次判断接收进度异常
						char absolute_sproduct_uri[512];
						snprintf(absolute_sproduct_uri,sizeof(absolute_sproduct_uri),"%s/%s", push_dir_get(),p->URI);
						DEBUG("clear %s for this receive task\n", absolute_sproduct_uri);
						remove_force(absolute_sproduct_uri);
					}
					else{
						DEBUG("shit tmp_URI=%s, p->URI=%s, motherdisc_processing()=%d, can not remove\n", tmp_URI, p->URI, motherdisc_processing());
					}
				}
				
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceiveColumn")){
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					productdesc_clear((DBSTAR_PRODUCTDESC_S *)ptr, 1);
					parseProperty(cur, new_xmlroute, ptr);
					
					memset(&xmlinfo_in_recv, 0, sizeof(xmlinfo_in_recv));
					snprintf(xmlinfo_in_recv.PushFlag,sizeof(xmlinfo_in_recv.PushFlag),"%d",COLUMN_XML);
					read_xmlver_in_trans(&xmlinfo_in_recv,old_xmlver_in_recv,sizeof(old_xmlver_in_recv));
					if((strlen(old_xmlver_in_recv)>0 && 0==strcmp(old_xmlver_in_recv, p->version))){
						DEBUG("old ver: %s, new ver: %s, no need to regist and parse\n",old_xmlver_in_recv, p->version);
					}
					else{
						snprintf(p->ReceiveType, sizeof(p->ReceiveType), "%d", RECEIVETYPE_COLUMN);
						parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
						
						snprintf(xmlinfo_in_recv.Version,sizeof(xmlinfo_in_recv.Version),"%s",p->version);
						snprintf(xmlinfo_in_recv.URI,sizeof(xmlinfo_in_recv.URI),"%s",p->DescURI);
						
						productdesc_insert((DBSTAR_PRODUCTDESC_S *)ptr);
						
						xmlinfo_insert(&xmlinfo_in_recv);
					}
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceiveColumn^ColumnID")){
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					
					szKey = xmlNodeGetContent(cur);
					strncpy(p->ID, (char *)szKey, sizeof(p->ID)-1);
					snprintf(p->ProductDescID, sizeof(p->ProductDescID),"%s_%s_%s", p->ServiceID,p->ReceiveType,p->ID);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceiveColumn^ColumnNames")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceiveColumn^ColumnNames^ColumnName")){
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					
					DBSTAR_RESSTR_S resstr_s;
					memset(&resstr_s, 0, sizeof(resstr_s));
					
					snprintf(resstr_s.ServiceID,sizeof(resstr_s.ServiceID),"%s",p->ServiceID);
					strncpy(resstr_s.ObjectName, OBJ_PRODUCTDESC, sizeof(resstr_s.ObjectName)-1);
					if(strlen(p->ProductDescID)>0)
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s", p->ProductDescID);
					else
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s%s", OBJID_PAUSE, OBJ_PRODUCTDESC);
					strncpy(resstr_s.StrName, "ColumnName", sizeof(resstr_s.StrName)-1);
					
					parseProperty(cur, new_xmlroute, (void *)(&resstr_s));
					
					resstr_insert(&resstr_s);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceiveColumn^TotalSize")){
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					
					szKey = xmlNodeGetContent(cur);
					strncpy(p->TotalSize, (char *)szKey, sizeof(p->TotalSize)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceiveColumn^ColumnURI")){
					parseProperty(cur, new_xmlroute, ptr);
					
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					char tmp_URI[512];
					szKey = xmlNodeGetContent(cur);
					snprintf(tmp_URI,sizeof(tmp_URI),"%s",(char *)szKey);
					xmlFree(szKey);
					snprintf(p->URI,sizeof(p->URI),"%s",p->rootPath);
					if('/'!=tmp_URI[0])
						snprintf(p->URI+strlen(p->URI),sizeof(p->URI)-strlen(p->URI),"/");
					snprintf(p->URI+strlen(p->URI),sizeof(p->URI)-strlen(p->URI),"%s",tmp_URI);
					
					if(strlen(tmp_URI)>0 && 0==motherdisc_processing()){
						// 显式清理本目录。如果前一次Column解析失败，会留下残留文件，引起本次判断接收进度异常
						char absolute_column_uri[512];
						snprintf(absolute_column_uri,sizeof(absolute_column_uri),"%s/%s", push_dir_get(),p->URI);
						DEBUG("clear %s for this receive task\n", absolute_column_uri);
						remove_force(absolute_column_uri);
					}
					else{
						DEBUG("shit tmp_URI=%s, p->URI=%s, motherdisc_processing()=%d, can not remove\n", tmp_URI, p->URI, motherdisc_processing());
					}
				}
			}
			
// Message.xml
			else if(0==strncmp(new_xmlroute, "Messages^", strlen("Messages^"))){
				if(0==strcmp(new_xmlroute, "Messages^Message")){
					DBSTAR_MESSAGE_S message_s;
					memset(&message_s, 0, sizeof(message_s));
					
					snprintf(message_s.MessageID, sizeof(message_s.MessageID), "Msg_%s", time_serial());
					parseProperty(cur, new_xmlroute, (void *)(&message_s));
					parseNode(doc, cur, new_xmlroute, (void *)(&message_s), NULL, NULL, NULL);
					message_insert(&message_s);
				}
				else if(0==strcmp(new_xmlroute, "Messages^Message^Content"))
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
				else if(0==strcmp(new_xmlroute, "Messages^Message^Content^SubContent")){
					DBSTAR_MESSAGE_S *p = (DBSTAR_MESSAGE_S *)ptr;
					
					DBSTAR_RESSTR_S resstr_s;
					memset(&resstr_s, 0, sizeof(resstr_s));
					
					snprintf(resstr_s.ServiceID,sizeof(resstr_s.ServiceID),"%s",p->ServiceID);
					strncpy(resstr_s.ObjectName, OBJ_MESSAGE, sizeof(resstr_s.ObjectName)-1);
					if(strlen(p->MessageID)>0)
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s", p->MessageID);
					else
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s%s", OBJID_PAUSE, OBJ_MESSAGE);
					strncpy(resstr_s.StrName, "SubContent", sizeof(resstr_s.StrName)-1);
					
					parseProperty(cur, new_xmlroute, (void *)(&resstr_s));
					
					resstr_insert(&resstr_s);
				}
				else if(0==strcmp(new_xmlroute, "Messages^Message^StartTime")){
					DBSTAR_MESSAGE_S *p = (DBSTAR_MESSAGE_S *)ptr;
					
					szKey = xmlNodeGetContent(cur);
					strncpy(p->StartTime, (char *)szKey, sizeof(p->StartTime)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Messages^Message^EndTime")){
					DBSTAR_MESSAGE_S *p = (DBSTAR_MESSAGE_S *)ptr;
					
					szKey = xmlNodeGetContent(cur);
					strncpy(p->EndTime, (char *)szKey, sizeof(p->EndTime)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Messages^Message^Interval")){
					DBSTAR_MESSAGE_S *p = (DBSTAR_MESSAGE_S *)ptr;
					
					szKey = xmlNodeGetContent(cur);
					strncpy(p->Interval, (char *)szKey, sizeof(p->Interval)-1);
					xmlFree(szKey);
				}
			}
			
// SProduct.xml
			else if(0==strncmp(new_xmlroute, "SProduct^", strlen("SProduct^"))){
				if(0==strcmp(new_xmlroute, "SProduct^SProductInfo")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "SProduct^SProductInfo^BGPicture")){
					DBSTAR_SPRODUCT_S sproduct_s;
					memset(&sproduct_s,0,sizeof(sproduct_s));
					snprintf(sproduct_s.ServiceID,sizeof(sproduct_s.ServiceID),"%s",(char *)ptr);
					
					snprintf(sproduct_s.SType,sizeof(sproduct_s.SType),"BGPicture");
					parseProperty(cur, new_xmlroute, &sproduct_s);
					sproduct_insert(&sproduct_s);
				}
				else if(0==strcmp(new_xmlroute, "SProduct^SProductInfo^WelcomeAV")){
					DBSTAR_SPRODUCT_S sproduct_s;
					memset(&sproduct_s,0,sizeof(sproduct_s));
					snprintf(sproduct_s.ServiceID,sizeof(sproduct_s.ServiceID),"%s",(char *)ptr);
					
					snprintf(sproduct_s.SType,sizeof(sproduct_s.SType),"WelcomeAV");
					parseProperty(cur, new_xmlroute, &sproduct_s);
					sproduct_insert(&sproduct_s);
				}
				else if(0==strcmp(new_xmlroute, "SProduct^SProductInfo^AppLogo")){
					DBSTAR_SPRODUCT_S sproduct_s;
					memset(&sproduct_s,0,sizeof(sproduct_s));
					snprintf(sproduct_s.ServiceID,sizeof(sproduct_s.ServiceID),"%s",(char *)ptr);
					
					snprintf(sproduct_s.SType,sizeof(sproduct_s.SType),"AppLogo");
					parseProperty(cur, new_xmlroute, &sproduct_s);
					sproduct_insert(&sproduct_s);
				}
				else if(0==strcmp(new_xmlroute, "SProduct^SProductInfo^AppBG")){
					DBSTAR_SPRODUCT_S sproduct_s;
					memset(&sproduct_s,0,sizeof(sproduct_s));
					snprintf(sproduct_s.ServiceID,sizeof(sproduct_s.ServiceID),"%s",(char *)ptr);
					
					snprintf(sproduct_s.SType,sizeof(sproduct_s.SType),"AppBG");
					parseProperty(cur, new_xmlroute, &sproduct_s);
					sproduct_insert(&sproduct_s);
				}
				else if(0==strcmp(new_xmlroute, "SProduct^SProductInfo^ServicePic")){
					DBSTAR_SPRODUCT_S sproduct_s;
					memset(&sproduct_s,0,sizeof(sproduct_s));
					snprintf(sproduct_s.ServiceID,sizeof(sproduct_s.ServiceID),"%s",(char *)ptr);
					
					snprintf(sproduct_s.SType,sizeof(sproduct_s.SType),"ServicePic");
					parseProperty(cur, new_xmlroute, &sproduct_s);
					sproduct_insert(&sproduct_s);
				}
			}
			
// Commands.xml
			else if(0==strncmp(new_xmlroute, "Commands^", strlen("Commands^"))){
				if(0==strcmp(new_xmlroute, "Commands^Operations")){
					DBSTAR_CMD_OPERATION_S cmd_op_s;
					
					parseNode(doc, cur, new_xmlroute, (void *)(&cmd_op_s), NULL, NULL, NULL);
				}if(0==strcmp(new_xmlroute, "Commands^Operations^Operation")){
					memset(ptr, 0, sizeof(DBSTAR_CMD_OPERATION_S));
					
					parseProperty(cur, new_xmlroute, ptr);
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "Commands^Operations^Operation^Objects")){
					DBSTAR_CMD_OPERATION_S *p = (DBSTAR_CMD_OPERATION_S *)ptr;
					
					p->objectType = DBSTAR_CMD_OBJ_UNDEFINED;
					parseProperty(cur, new_xmlroute, ptr);
					
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "Commands^Operations^Operation^Objects^ObjectID")){
					DBSTAR_CMD_OPERATION_S *p = (DBSTAR_CMD_OPERATION_S *)ptr;
					
					p->object.fileType = DBSTAR_CMD_OBJ_FILE_UNDEFINED;
					memset(p->object.ID,0,sizeof(p->object.ID));
					parseProperty(cur, new_xmlroute, ptr);
					
					szKey = xmlNodeGetContent(cur);
					strncpy(p->object.ID, (char *)szKey, sizeof(p->object.ID)-1);
					xmlFree(szKey);
					
					PRINTF("cmd operation: type=%d, objectType=%d, object.fileType=%d, object.ID=%s", p->type,p->objectType,p->object.fileType,p->object.ID);
					command_operate(p);
				}
			}
			
			else
				DEBUG("can NOT process such element '%s' in xml route '%s'\n", cur->name, xmlroute);
			
		}
		
		if(XML_EXIT_NORMALLY==process_over || XML_EXIT_UNNECESSARY==process_over){
			cur = cur->next;
		}
		else{	// if(XML_EXIT_MOVEUP==process_over || XML_EXIT_ERROR==process_over)
			DEBUG("process over advance !!!\n");
			break;
		}
	}
	//DEBUG("return from %s with %s\n", xmlroute, process_over_str(process_over));
	
#if 0
	if(XML_EXIT_NORMALLY==process_over || XML_EXIT_MOVEUP==process_over){
		DEBUG("parse xml ok, process_over=%d\n", process_over);
		return 0;
	}
	else{
		DEBUG("parse xml failed, process_over=%d\n", process_over);
		return -1;
	}
#else
	return process_over;
#endif
}

static int parseDoc(char *xml_relative_uri, PUSH_XML_FLAG_E xml_flag, char *arg_ext)
{
	xmlDocPtr doc;
	xmlNodePtr cur;
	int ret = 0;
//	int push_flags[16];
	char xml_uri[512];
	PUSH_XML_FLAG_E actual_xml_flag = xml_flag;
	
	s_preview_publication = 0;
	
	DEBUG("actual_xml_flag: %d, arg_ext: %s\n", actual_xml_flag, arg_ext);
	pthread_mutex_lock(&mtx_parse_xml);
	
	memset(xml_uri, 0, sizeof(xml_uri));
	if(NULL==xml_relative_uri){
		char tmp_uri[512];
		if(-1==xmluri_get(actual_xml_flag, tmp_uri, sizeof(tmp_uri))){
			DEBUG("can not get valid xml uri to parse\n");
			ret = -1;
			goto PARSE_XML_END;
		}
		else
			snprintf(xml_uri, sizeof(xml_uri), "%s/%s", push_dir_get(), tmp_uri);
	}
	else
		snprintf(xml_uri, sizeof(xml_uri), "%s/%s", push_dir_get(), xml_relative_uri);
	
	DEBUG("parse xml file[%d]: %s\n", actual_xml_flag, xml_uri);
	
	doc = xmlParseFile(xml_uri);
	if (doc == NULL ) {
		ERROROUT("parse failed: %s\n", xml_uri);
		ret = -1;
		goto PARSE_XML_END;
	}

	cur = xmlDocGetRootElement(doc);
	if (cur == NULL) {
		ERROROUT("empty document\n");
		ret = -1;
	}
	else{
		DBSTAR_XMLINFO_S xmlinfo;
		memset(&xmlinfo, 0, sizeof(xmlinfo));
		snprintf(xmlinfo.PushFlag, sizeof(xmlinfo.PushFlag), "%d", actual_xml_flag);
		
		char sqlite_cmd[1024];
		char old_xmlver[64];
		memset(old_xmlver, 0, sizeof(old_xmlver));

// Commands.xml比较特殊，无需入库但需要即时执行一些指令，因此不能放在“事务”中解析。
// Commands.xml
		if(COMMANDS_XML==actual_xml_flag && 0==xmlStrcmp(cur->name, BAD_CAST"Commands")){
			DEBUG("CAUTION: this is a command xml\n");
#if 0
			parseProperty(cur, XML_ROOT_ELEMENT, (void *)&xmlinfo);
			read_xmlver_in_trans(&xmlinfo,old_xmlver,sizeof(old_xmlver));
			
			// || 0!=strcmp(serviceID_get(), xmlinfo.ServiceID)
			if((strlen(old_xmlver)>0 && 0==strcmp(old_xmlver, xmlinfo.Version)) ){
				DEBUG("old ver: %s, new ver: %s, my ServiceID: %s, xml ServiceID: %s, no need to parse\n",\
						old_xmlver, xmlinfo.Version, serviceID_get(), xmlinfo.ServiceID);
				ret = -1;
			}
			else
#endif
			{
				ret = parseNode(doc, cur, "Commands", NULL, &xmlinfo, "Commands", old_xmlver);
				
				ret = PROCESS_OVER_CHECK(ret);
			}
		}
		else{
			if(-1==sqlite_transaction_begin()){
				ret = -1;
			}
			else{
			
// Initialize.xml
				if(0==xmlStrcmp(cur->name, BAD_CAST"Initialize")){
					parseProperty(cur, XML_ROOT_ELEMENT, (void *)&xmlinfo);
					/*
					 Initalize.xml本身不区分ServiceID，只是为了统一，将其ServiceID固定为‘0’
					*/
					snprintf(xmlinfo.ServiceID,sizeof(xmlinfo.ServiceID),"%s",SERVICEID_FILL);
					read_xmlver_in_trans(&xmlinfo,old_xmlver,sizeof(old_xmlver));
					if(strlen(old_xmlver)>0 && 0==strcmp(old_xmlver, xmlinfo.Version)){
						DEBUG("[%s]same Version: %s, no need to parse\n", xmlinfo.PushFlag, old_xmlver);
						ret = -1;
					}
					else{
						s_detect_valid_productID = 0;
						
						ret = parseNode(doc, cur, "Initialize", NULL, &xmlinfo, "Initialize", old_xmlver);
						
						ret = PROCESS_OVER_CHECK(ret);
						if(0==ret){
							if(s_detect_valid_productID){
								snprintf(xmlinfo.XMLName, sizeof(xmlinfo.XMLName), "Initialize.xml");
								/*
								 Initialize.xml是所有service共用的，不存在单独的serviceID属性，这里只是起到填充作用
								*/
								snprintf(xmlinfo.ServiceID, sizeof(xmlinfo.XMLName), "%s", SERVICEID_FILL);
							}
							else{
								DEBUG("detect no valid special productID, make return as -1\n");
								ret = -1;
							}
						}
					}
				}
				
// Service.xml
				else if(0==xmlStrcmp(cur->name, BAD_CAST"Service")){
					parseProperty(cur, XML_ROOT_ELEMENT, (void *)&xmlinfo);
					read_xmlver_in_trans(&xmlinfo,old_xmlver,sizeof(old_xmlver));
					/*
					 新push只接收注册的文件，所以不用处理非当前业务的文件夹进行反注册
					*/
					if((strlen(old_xmlver)>0 && 0==strcmp(old_xmlver, xmlinfo.Version)) || 0!=strcmp(serviceID_get(), xmlinfo.ServiceID)){
						DEBUG("old ver: %s, new ver: %s, my ServiceID: %s, xml ServiceID: %s, no need to parse\n",\
								old_xmlver, xmlinfo.Version, serviceID_get(), xmlinfo.ServiceID);
#if 0
						ret = -1;
#else
						/*
						 Service.xml起到承上启下的作用，因此即便版本号相同，也返回0，以便ProductDesc.xml、GuideList.xml等得以注册
						*/
						ret = 0;
#endif
					}
					else
					{
						sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"DELETE FROM Product;");
						sqlite_transaction_exec(sqlite_cmd);

						/*
						在父节点上定义子节点的结构体，并清空
						*/
						DBSTAR_SERVICE_S service_s;
						memset(&service_s, 0, sizeof(service_s));
						snprintf(service_s.ServiceID, sizeof(service_s.ServiceID), "%s", xmlinfo.ServiceID);
						ret = parseNode(doc, cur, "Service", &service_s, &xmlinfo, "Service", old_xmlver);
						service_insert(&service_s);
						
						ret = PROCESS_OVER_CHECK(ret);
					}
				}
				
// Publication.xml
				else if(0==xmlStrcmp(cur->name, BAD_CAST"Publication")){
					// 成品、栏目和特殊产品均通过文件通道下发，原始PushFlag都是0，故此处进行修正
					actual_xml_flag = PUBLICATION_XML;
					snprintf(xmlinfo.PushFlag, sizeof(xmlinfo.PushFlag), "%d", actual_xml_flag);
					
					parseProperty(cur, XML_ROOT_ELEMENT, (void *)&xmlinfo);
	
#if 0			
// 成品、栏目和特殊产品均通过文件通道下发，是否已解析通过ProductDesc的Parsed字段控制，这里不再判断
					read_xmlver_in_trans(&xmlinfo,old_xmlver,sizeof(old_xmlver));
					
					if((strlen(old_xmlver)>0 && 0==strcmp(old_xmlver, xmlinfo.Version))  || 0!=strcmp(serviceID_get(), xmlinfo.ServiceID)){
						DEBUG("old ver: %s, new ver: %s, my ServiceID: %s, xml ServiceID: %s, no need to parse\n",\
								old_xmlver, xmlinfo.Version, serviceID_get(), xmlinfo.ServiceID);
						ret = -1;
					}
					else
#endif
					{
						DBSTAR_PUBLICATION_S publication_s;
						memset(&publication_s, 0, sizeof(publication_s));
						snprintf(publication_s.ServiceID,sizeof(publication_s.ServiceID),"%s", xmlinfo.ServiceID);
						ret = parseNode(doc, cur, "Publication", (void *)&publication_s, &xmlinfo, "Publication", old_xmlver);
						publication_insert(&publication_s);
						snprintf(xmlinfo.ID,sizeof(xmlinfo.ID),"%s",publication_s.PublicationID);
						
						ret = PROCESS_OVER_CHECK(ret);
					}
				}
// Column.xml
				else if(0==xmlStrcmp(cur->name, BAD_CAST"Columns")){
					// 成品、栏目和特殊产品均通过文件通道下发，原始PushFlag都是0，故此处进行修正
					actual_xml_flag = COLUMN_XML;
					snprintf(xmlinfo.PushFlag, sizeof(xmlinfo.PushFlag), "%d", actual_xml_flag);
					
					parseProperty(cur, XML_ROOT_ELEMENT, (void *)&xmlinfo);
#if 0			
// 成品、栏目和特殊产品均通过文件通道下发，是否已解析通过ProductDesc的Parsed字段控制，这里不再判断
					read_xmlver_in_trans(&xmlinfo,old_xmlver,sizeof(old_xmlver));
					if((strlen(old_xmlver)>0 && 0==strcmp(old_xmlver, xmlinfo.Version)) || 0!=strcmp(serviceID_get(), xmlinfo.ServiceID)){
						DEBUG("old ver: %s, new ver: %s, my ServiceID: %s, xml ServiceID: %s, no need to parse\n",\
								old_xmlver, xmlinfo.Version, serviceID_get(), xmlinfo.ServiceID);
						ret = -1;
					}
					else
#endif
					{
						/*
						 不能一股脑的清理掉Column的所有数据，保留本地菜单
						*/
						sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"DELETE FROM Column WHERE ColumnType='1' OR ColumnType='2' OR ColumnType='3' OR ColumnType='4' OR ColumnType='5' OR ColumnType='6' OR ColumnType='7' OR ColumnType='8' OR ColumnType='9' OR ColumnType='10' OR ColumnType='11' OR ColumnType='12' OR ColumnType='13' OR ColumnType='14';");
						sqlite_transaction_exec(sqlite_cmd);

						s_column_SequenceNum = 10;	// 允许一些内置的栏目（如国电业务）排在下发栏目之前，故SequenceNum从10计起
						
						DBSTAR_COLUMN_S column_s;
						memset(&column_s, 0, sizeof(column_s));
						snprintf(column_s.ServiceID,sizeof(column_s.ServiceID),"%s", xmlinfo.ServiceID);
						ret = parseNode(doc, cur, "Columns", &column_s, &xmlinfo, "Columns", old_xmlver);
						
						ret = PROCESS_OVER_CHECK(ret);
					}
				}
				
// GuideList.xml
				else if(0==xmlStrcmp(cur->name, BAD_CAST"GuideList")){
					parseProperty(cur, XML_ROOT_ELEMENT, (void *)&xmlinfo);
					read_xmlver_in_trans(&xmlinfo,old_xmlver,sizeof(old_xmlver));
					
					//	|| 0!=strcmp(serviceID_get(), xmlinfo.ServiceID)
					if((strlen(old_xmlver)>0 && 0==strcmp(old_xmlver, xmlinfo.Version)) ){
						DEBUG("old ver: %s, new ver: %s, my ServiceID: %s, xml ServiceID: %s, no need to parse\n",\
								old_xmlver, xmlinfo.Version, serviceID_get(), xmlinfo.ServiceID);
						ret = -1;
					}
					else{
						
#if 0	//  不能直接删除所有的记录，应保留用户选择“拒绝接收”的记录。只能删除昨天及以前的记录。
						parseProperty(cur, XML_ROOT_ELEMENT, (void *)&xmlinfo);
#else
						sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"DELETE FROM GuideList WHERE DateValue<datetime('now','localtime','-2 days');");
						sqlite_transaction_exec(sqlite_cmd);
#endif
						
						DBSTAR_GUIDELIST_S guidelist_s;
						memset(&guidelist_s, 0, sizeof(guidelist_s));
						snprintf(guidelist_s.ServiceID,sizeof(guidelist_s.ServiceID),"%s", xmlinfo.ServiceID);
						ret = parseNode(doc, cur, "GuideList", &guidelist_s, &xmlinfo, "GuideList", old_xmlver);
						
						ret = PROCESS_OVER_CHECK(ret);
					}
				}
				
// ProductDesc.xml 当前投递单
				else if(0==xmlStrcmp(cur->name, BAD_CAST"ProductDesc")){
					s_recv_totalsize_sum = 0LL;
					DEBUG("reset s_recv_totalsize_sum as %lld\n", s_recv_totalsize_sum);
					
					parseProperty(cur, XML_ROOT_ELEMENT, (void *)&xmlinfo);
					read_xmlver_in_trans(&xmlinfo,old_xmlver,sizeof(old_xmlver));
					/*
					 新push只接收注册的文件，所以不用处理非当前业务的节目进行反注册
					  || 0!=strcmp(serviceID_get(), xmlinfo.ServiceID)
					*/
					if((strlen(old_xmlver)>0 && 0==strcmp(old_xmlver, xmlinfo.Version)) ){
						DEBUG("old ver: %s, new ver: %s, my ServiceID: %s, xml ServiceID: %s, no need to parse\n",\
								old_xmlver, xmlinfo.Version, serviceID_get(), xmlinfo.ServiceID);
						ret = -1;
					}
					else{
						/*
						 不考虑PushStartTime和PushEndTime的限制，只要有新的播发单就删除旧单，简化逻辑
						*/
						prog_monitor_reset();
						
						sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"DELETE FROM ProductDesc;");
						sqlite_transaction_exec(sqlite_cmd);

						DEBUG("old ver: %s, new ver: %s\n",old_xmlver, xmlinfo.Version);
						DBSTAR_PRODUCTDESC_S productdesc_s;
						memset(&productdesc_s, 0, sizeof(productdesc_s));
						snprintf(productdesc_s.ServiceID,sizeof(productdesc_s.ServiceID),"%s", xmlinfo.ServiceID);
						ret = parseNode(doc, cur, "ProductDesc", &productdesc_s, &xmlinfo, "ProductDesc", old_xmlver);
						
						ret = PROCESS_OVER_CHECK(ret);
					}
				}
				
// Message.xml
				else if(0==xmlStrcmp(cur->name, BAD_CAST"Messages")){
					parseProperty(cur, XML_ROOT_ELEMENT, (void *)&xmlinfo);
					read_xmlver_in_trans(&xmlinfo,old_xmlver,sizeof(old_xmlver));
					
					// || 0!=strcmp(serviceID_get(), xmlinfo.ServiceID)
					if((strlen(old_xmlver)>0 && 0==strcmp(old_xmlver, xmlinfo.Version)) ){
						DEBUG("old ver: %s, new ver: %s, my ServiceID: %s, xml ServiceID: %s, no need to parse\n",\
								old_xmlver, xmlinfo.Version, serviceID_get(), xmlinfo.ServiceID);
						ret = -1;
					}
					else{
						ret = parseNode(doc, cur, "Messages", NULL, &xmlinfo, "Messages", old_xmlver);
						
						ret = PROCESS_OVER_CHECK(ret);
					}
				}
	
// SProduct.xml
				else if(0==xmlStrcmp(cur->name, BAD_CAST"SProduct")){
					// 成品、栏目和特殊产品均通过文件通道下发，原始PushFlag都是0，故此处进行修正
					actual_xml_flag = SPRODUCT_XML;
					snprintf(xmlinfo.PushFlag, sizeof(xmlinfo.PushFlag), "%d", actual_xml_flag);
					
					parseProperty(cur, XML_ROOT_ELEMENT, (void *)&xmlinfo);
#if 0			
// 成品、栏目和特殊产品均通过文件通道下发，是否已解析通过ProductDesc的Parsed字段控制，这里不再判断
					read_xmlver_in_trans(&xmlinfo,old_xmlver,sizeof(old_xmlver));
					if((strlen(old_xmlver)>0 && 0==strcmp(old_xmlver, xmlinfo.Version)) || 0!=strcmp(serviceID_get(), xmlinfo.ServiceID)){
						DEBUG("old ver: %s, new ver: %s, my ServiceID: %s, xml ServiceID: %s, no need to parse\n",\
								old_xmlver, xmlinfo.Version, serviceID_get(), xmlinfo.ServiceID);
						ret = -1;
					}
					else
#endif
					{
						char SProduct_ServiceID[64];
						snprintf(SProduct_ServiceID,sizeof(SProduct_ServiceID),"%s", xmlinfo.ServiceID);
						ret = parseNode(doc, cur, "SProduct", SProduct_ServiceID, &xmlinfo, "SProduct", old_xmlver);
						
						ret = PROCESS_OVER_CHECK(ret);
					}
				}
			
				else{
					ERROROUT("xml file has wrong root node with '%s'\n", cur->name);
					ret = -1;
				}
			
				if(-1==ret)
					sqlite_transaction_end(0);
				else if(0==ret){
					if(xml_relative_uri)
						snprintf(xmlinfo.URI, sizeof(xmlinfo.URI), "%s", xml_relative_uri);
					
					if(PUBLICATION_XML==atoi(xmlinfo.PushFlag) || COLUMN_XML==atoi(xmlinfo.PushFlag) || SPRODUCT_XML==atoi(xmlinfo.PushFlag))
					{
						PRINTF("this xml [%s] is controled by column 'Parsed' in table ProductDesc, don't insert to table Initialize\n",xmlinfo.PushFlag);
					}
					else{
						xmlinfo_insert(&xmlinfo);
					}
					
					sqlite_transaction_end(1);
				}
			}
		}
	}
	
	xmlFreeDoc(doc);

PARSE_XML_END:
	DEBUG("parse xml end, actual_xml_flag=%d, ret=%d\n", actual_xml_flag, ret);
	pthread_mutex_unlock(&mtx_parse_xml);

// 不要放在事务或线程锁内部发送这些信号	
	if(0==ret){
		if(PUBLICATION_XML==actual_xml_flag){
			if(1==s_preview_publication){
				DEBUG("this Publication is a preview\n");
				preview_refresh_flag_set(1);
			}
			
			productdesc_parsed_set(xml_relative_uri, actual_xml_flag, arg_ext);
		}
		else if(COLUMN_XML==actual_xml_flag){
			column_refresh_flag_set(1);
			productdesc_parsed_set(xml_relative_uri, actual_xml_flag, arg_ext);
		}
		else if(SPRODUCT_XML==actual_xml_flag){
			interface_refresh_flag_set(1);
			productdesc_parsed_set(xml_relative_uri, actual_xml_flag, arg_ext);
		}
		else if(PRODUCTDESC_XML==actual_xml_flag){	//  || SERVICE_XML==actual_xml_flag 只接收本service的播发单数据，无需根据Service.xml进行刷新
			if(1==motherdisc_processing()){
				DEBUG("in mother disc processing, do nothing after PRODUCTDESC_XML parsed\n");
			}
			else{
				DEBUG("refresh push monitor because of xml %d\n", actual_xml_flag);
				
				/*
				 如果是进行了磁盘清理，需要再重试一次进行硬盘空间检查。因为磁盘清理时，清理空间计算依据的是数据库记录，但是有的节目没有下载完整，导致数据库标识的节目体积有虚高的风险，
				 也因此导致实际清理掉的空间没有达到期望值。
				*/
				while(0==disk_space_check()){
					DEBUG("do disk_space_check() finish with 0, sleep(3) and check again\n");
					sleep(3);
				}
				
				push_recv_manage_refresh();
			}
		}
		else if(INITIALIZE_XML==actual_xml_flag){
			pid_init(1);
			channel_ineffective_clear();
			
			if(1==motherdisc_processing()){
				DEBUG("in mother disc processing, do nothing after INITIALIZE_XML parsed\n");
			}
			else{
				info_xml_regist();
			}
		}
		else if(SERVICE_XML==actual_xml_flag){
		}
	}

	return ret;
}


/*
 允许xml_name是空，但是必须有xml_flag。如果xml_uri不为空，则直接使用此uri
 此函数需要独占调用，因为如果当前解析的是Initialize.xml的话，解析完毕后还要自动扫描解析那些依赖于serviceID的xml。
 但同时，push系统的回调也有可能刚好得到这些xml而引起解析。
*/
int parse_xml(char *relative, PUSH_XML_FLAG_E xml_flag, char *arg_ext)
{
	int ret = parseDoc(relative, xml_flag, arg_ext);
	
	return ret;
}

