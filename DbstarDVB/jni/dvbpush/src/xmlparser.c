#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <errno.h>
#include <time.h>
#include <libxml/xmlmemory.h>
#include <libxml/parser.h>

#include "common.h"
#include "xmlparser.h"
#include "sqlite.h"
#include "mid_push.h"
#include "multicast.h"
#include "softdmx.h"
#include "porting.h"

static char s_serviceID[64];
static char s_push_root_path[512];
static int global_insert(DBSTAR_GLOBAL_S *p);

/*
 只用于读取单个字符串类型字段的单个值
*/
int str_read_cb(char **result, int row, int column, void *some_str)
{
	DEBUG("sqlite callback, row=%d, column=%d, filter_act addr: %p\n", row, column, some_str);
	if(row<1 || NULL==some_str){
		DEBUG("no record in table, return\n");
		return 0;
	}
	
	int i = 1;
//	for(i=1;i<row+1;i++)
	{
		//DEBUG("==%s:%s:%ld==\n", result[i*column], result[i*column+1], strtol(result[i*column+1], NULL, 0));
		strcpy((char *)some_str, result[i*column]);
	}
	
	return 0;
}

/*
 从数据表Global中读取push的根路径，此路径由上层写入数据库。
 此路径应当更新到push.conf中供push模块初始化使用。
 之所以这么更新，是因为无法确保硬盘一定是挂在/mnt/sda1下。
*/
char *push_dir_get()
{
	return s_push_root_path;
}

static int push_dir_init()
{
	char sqlite_cmd[512];
	
	memset(s_push_root_path, 0, sizeof(s_push_root_path));
	
	int (*sqlite_cb)(char **, int, int, void *) = str_read_cb;
	snprintf(sqlite_cmd,sizeof(sqlite_cmd),"SELECT Value FROM Global WHERE Name='%s';", GLB_NAME_PUSHDIR);

	int ret_sqlexec = sqlite_read(sqlite_cmd, s_push_root_path, sqlite_cb);
	if(ret_sqlexec<=0){
		DEBUG("read no PushDir from db, filled with %s\n", PUSH_DATA_DIR_DF);
		snprintf(s_push_root_path, sizeof(s_push_root_path), "%s", PUSH_DATA_DIR_DF);
	}
	else
		DEBUG("read PushDir: %s\n", s_push_root_path);
		
	return 0;
}


/*
 初始化函数，读取Global表中的ServiceID，初始化push的根目录供UI使用。
*/
int xmlparser_init(void)
{
	char sqlite_cmd[512];
	memset(s_serviceID, 0, sizeof(s_serviceID));
	int (*sqlite_cb)(char **, int, int, void *) = str_read_cb;
	snprintf(sqlite_cmd,sizeof(sqlite_cmd),"SELECT Value FROM Global WHERE Name='%s';", GLB_NAME_SERVICEID);

	int ret_sqlexec = sqlite_read(sqlite_cmd, s_serviceID, sqlite_cb);
	if(ret_sqlexec<=0){
		DEBUG("read no serviceID from db, filled with %s\n", SERVICE_ID);
		//strncpy(s_serviceID, SERVICE_ID, sizeof(s_serviceID)-1);
	}
	else
		DEBUG("read serviceID: %s\n", s_serviceID);
	
	DBSTAR_GLOBAL_S global_s;
	sqlite_transaction_begin();
		
	memset(&global_s, 0, sizeof(global_s));
	strncpy(global_s.Name, GLB_NAME_PREVIEWPATH, sizeof(global_s.Name));
	strncpy(global_s.Value, DBSTAR_PREVIEWPATH, sizeof(global_s.Value));
	global_insert(&global_s);
	
	memset(&global_s, 0, sizeof(global_s));
	strncpy(global_s.Name, GLB_NAME_CURLANGUAGE, sizeof(global_s.Name));
	strncpy(global_s.Value, "chi", sizeof(global_s.Value));	// this should be changed at future
	global_insert(&global_s);
	
//	memset(&global_s, 0, sizeof(global_s));
//	strncpy(global_s.Name, GLB_NAME_COLUMNRES, sizeof(global_s.Name));
//	strncpy(global_s.Value, LOCALCOLUMN_RES, sizeof(global_s.Value));
//	global_insert(&global_s);
	
	sqlite_transaction_end(1);
		
	char init_xml_path[512];
	char init_xml_uri[512];
	snprintf(init_xml_path, sizeof(init_xml_path), "%s/%s", s_push_root_path,INITIALIZE_PATH);
	memset(init_xml_uri, 0, sizeof(init_xml_uri));
	
	if(0==distill_file(init_xml_path, init_xml_uri, sizeof(init_xml_uri), "xml", "Initialize.xml")){
		parse_xml(init_xml_uri,INITIALIZE_XML);
	}
	
	push_dir_init();
	
	//localcolumn_init();
	
	return 0;
}

int xmlparser_uninit(void)
{
	return 0;
}

char *serviceID_get()
{
	return s_serviceID;
}

static int xmlver_get(int pushflag, char *xmlver, unsigned int versize)
{
	if(NULL==xmlver || 0==versize){
		DEBUG("can not get xml version with NULL buffer\n");
		return -1;
	}
	
	char sqlite_cmd[512];
	int (*sqlite_cb)(char **, int, int, void *) = str_read_cb;
	snprintf(sqlite_cmd,sizeof(sqlite_cmd),"SELECT Version FROM Initialize WHERE PushFlag='%d';", pushflag);

	int ret_sqlexec = sqlite_read(sqlite_cmd, xmlver, sqlite_cb);
	if(ret_sqlexec<=0){
		DEBUG("read no version from db for %d\n", pushflag);
		return -1;
	}
	else
		return 0;
}

static int xmluri_get(int pushflag, char *xmluri, unsigned int urisize)
{
	if(NULL==xmluri || 0==urisize){
		DEBUG("can not get xml uri with NULL buffer\n");
		return -1;
	}
	
	char sqlite_cmd[512];
	int (*sqlite_cb)(char **, int, int, void *) = str_read_cb;
	snprintf(sqlite_cmd,sizeof(sqlite_cmd),"SELECT URI FROM Initialize WHERE PushFlag='%d';", pushflag);

	int ret_sqlexec = sqlite_read(sqlite_cmd, xmluri, sqlite_cb);
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
	
	char sqlite_cmd[512];
	
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Global(Name,Value,Param) VALUES('%s','%s','%s');",
		p->Name, p->Value, p->Param);
	
	return sqlite_transaction_exec(sqlite_cmd);
}

/*
 向字符串资源表ResStr中插入数据。
 由于Res表都是依存于宿主表的寄生表，因此它们自身不能独立封装为事务，应为某事务的一部分。
*/
static int resstr_insert(DBSTAR_RESSTR_S *p)
{
	if(NULL==p || 0==strlen(p->ObjectName) || 0==strlen(p->EntityID) || 0==strlen(p->StrLang) || 0==strlen(p->StrName)){
		DEBUG("invalid arguments\n");
		return -1;
	}
	
	int cmd_size = strlen(p->StrValue)+256;
	char *sqlite_cmd = malloc(cmd_size);
	
	if(sqlite_cmd){
		snprintf(sqlite_cmd, cmd_size, "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s');",
			p->ObjectName, p->EntityID, p->StrLang, p->StrName, p->StrValue, p->Extension);
		sqlite_transaction_exec(sqlite_cmd);
		free(sqlite_cmd);
			
		return 0;
	}
	else{
		DEBUG("malloc %d failed\n", cmd_size);
		return -1;
	}
}

/*
 向初始化表Initialize插入xml文件的信息，既有可能是解析Initialize.xml时插入，也有可能是解析每个xml时插入版本信息
 效率比较低，应转换为事务处理。
*/
static int xmlinfo_insert(DBSTAR_XMLINFO_S *xmlinfo)
{
	if(NULL==xmlinfo && 0==strlen(xmlinfo->PushFlag))
		return -1;
	
	if(strlen(xmlinfo->Version)>0 || strlen(xmlinfo->StandardVersion)>0 || strlen(xmlinfo->URI)>0){
		char sqlite_cmd[512];
		
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Initialize(PushFlag) VALUES('%s');", xmlinfo->PushFlag);
		sqlite_transaction_exec(sqlite_cmd);
		
		if(strlen(xmlinfo->Version)>0){
			snprintf(sqlite_cmd, sizeof(sqlite_cmd), "UPDATE Initialize SET XMLName='%s'WHERE PushFlag='%s';", xmlinfo->XMLName, xmlinfo->PushFlag);
			sqlite_transaction_exec(sqlite_cmd);
		}
		if(strlen(xmlinfo->Version)>0){
			snprintf(sqlite_cmd, sizeof(sqlite_cmd), "UPDATE Initialize SET Version='%s'WHERE PushFlag='%s';", xmlinfo->Version, xmlinfo->PushFlag);
			sqlite_transaction_exec(sqlite_cmd);
		}
		if(strlen(xmlinfo->StandardVersion)>0){
			snprintf(sqlite_cmd, sizeof(sqlite_cmd), "UPDATE Initialize SET StandardVersion='%s'WHERE PushFlag='%s';", xmlinfo->StandardVersion, xmlinfo->PushFlag);
			sqlite_transaction_exec(sqlite_cmd);
		}
		if(strlen(xmlinfo->URI)>0){
			snprintf(sqlite_cmd, sizeof(sqlite_cmd), "UPDATE Initialize SET URI='%s'WHERE PushFlag='%s';", xmlinfo->URI, xmlinfo->PushFlag);
			sqlite_transaction_exec(sqlite_cmd);
		}
	}
	
	return 0;
}

/*
 向业务表Service中插入业务信息，实际上本表只有一条记录
*/
static int service_insert(DBSTAR_SERVICE_S *p)
{
	if(NULL==p || 0==strlen(p->ServiceID)){
		DEBUG("invalid arguments\n");
		return -1;
	}
	
	char sqlite_cmd[512];
	
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Service(ServiceID,RegionCode,OnlineTime,OfflineTime) VALUES('%s','%s','%s','%s');",
		p->ServiceID,p->RegionCode,p->OnlineTime,p->OfflineTime);
	
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
	
	char sqlite_cmd[512];
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Product(ProductID,ProductType,Flag,OnlineDate,OfflineDate,IsReserved,Price,CurrencyType) VALUES('%s','%s','%s','%s','%s','%s','%s','%s');",
		p->ProductID,p->ProductType,p->Flag,p->OnlineDate,p->OfflineDate,p->IsReserved,p->Price,p->CurrencyType);
	
	return sqlite_transaction_exec(sqlite_cmd);
}

static int publicationsset_insert(DBSTAR_PUBLICATIONSSET_S *p)
{
	if(NULL==p || 0==strlen(p->SetID)){
		DEBUG("invalid arguments\n");
		return -1;
	}
	
	char sqlite_cmd[1024*3];
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO PublicationsSet(SetID,ColumnID,ProductID,URI,TotalSize,ProductDescID,ReceiveStatus,PushTime,IsReserved,Visible,Favorite,IsAuthorized,VODNum,VODPlatform) \
	VALUES('%s',\
	(select ColumnID from PublicationsSet where SetID='%s'),\
	'%s',\
	(select URI from PublicationsSet where SetID='%s'),\
	(select TotalSize from PublicationsSet where SetID='%s'),\
	(select ProductDescID from PublicationsSet where SetID='%s'),\
	(select ReceiveStatus from PublicationsSet where SetID='%s'),\
	(select PushTime from PublicationsSet where SetID='%s'),\
	(select IsReserved from PublicationsSet where SetID='%s'),\
	(select Visible from PublicationsSet where SetID='%s'),\
	(select Favorite from PublicationsSet where SetID='%s'),\
	(select IsAuthorized from PublicationsSet where SetID='%s'),\
	(select VODNum from PublicationsSet where SetID='%s'),\
	(select VODPlatform from PublicationsSet where SetID='%s'));",
		p->SetID,
		p->SetID,
		p->ProductID,
		p->SetID,
		p->SetID,
		p->SetID,
		p->SetID,
		p->SetID,
		p->SetID,
		p->SetID,
		p->SetID,
		p->SetID,
		p->SetID,
		p->SetID);
	
	return sqlite_transaction_exec(sqlite_cmd);
}

static int publication_insert_setinfo(DBSTAR_PUBLICATIONSSET_S *p)
{
	if(NULL==p || 0==strlen(p->PublicationID) || 0==strlen(p->SetID) || 0==strlen(p->IndexInSet)){
		DEBUG("invalid arguments\n");
		return -1;
	}
	
	char sqlite_cmd[1024*4];
	
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Publication(PublicationID,ColumnID,ProductID,URI,DescURI,TotalSize,ProductDescID,ReceiveStatus,PushTime,PublicationType,IsReserved,Visible,DRMFile,SetID,IndexInSet,Favorite,Bookmark,IsAuthorized,VODNum,VODPlatform) \
	VALUES('%s',\
	(select ColumnID from Publication where PublicationID='%s'),\
	(select ProductID from Publication where PublicationID='%s'),\
	(select URI from Publication where PublicationID='%s'),\
	(select DescURI from Publication where PublicationID='%s'),\
	(select TotalSize from Publication where PublicationID='%s'),\
	(select ProductDescID from Publication where PublicationID='%s'),\
	(select ReceiveStatus from Publication where PublicationID='%s'),\
	(select PushTime from Publication where PublicationID='%s'),\
	(select PublicationType from Publication where PublicationID='%s'),\
	(select IsReserved from Publication where PublicationID='%s'),\
	(select Visible from Publication where PublicationID='%s'),\
	(select DRMFile from Publication where PublicationID='%s'),\
	'%s',\
	'%s',\
	(select Favorite from Publication where PublicationID='%s'),\
	(select Bookmark from Publication where PublicationID='%s'),\
	(select IsAuthorized from Publication where PublicationID='%s'),\
	(select VODNum from Publication where PublicationID='%s'),\
	(select VODPlatform from Publication where PublicationID='%s'));",
		p->PublicationID,
		p->PublicationID,
		p->PublicationID,
		p->PublicationID,
		p->PublicationID,
		p->PublicationID,
		p->PublicationID,
		p->PublicationID,
		p->PublicationID,
		p->PublicationID,
		p->PublicationID,
		p->PublicationID,
		p->PublicationID,
		p->SetID,
		p->IndexInSet,
		p->PublicationID,
		p->PublicationID,
		p->PublicationID,
		p->PublicationID,
		p->PublicationID);
	return sqlite_transaction_exec(sqlite_cmd);
}

/*
 Column.xml到来时，删除旧表，所以此处不用考虑UPDATE
*/
static int column_insert(DBSTAR_COLUMN_S *ptr)
{
	if(NULL==ptr || 0==strlen(ptr->ColumnID) || 0==strlen(ptr->ParentID) || 0==strlen(ptr->ServiceID)){
		DEBUG("invalid arguments\n");
		return -1;
	}
	
	if(0==strlen(ptr->ParentID))
		snprintf(ptr->ParentID, sizeof(ptr->ParentID), "-1");
	
	char sqlite_cmd[2048];
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick) VALUES('%s','%s','%s','%s','%s','%s','%s');",
		ptr->ColumnID,ptr->ParentID,ptr->Path,ptr->ColumnType,ptr->ColumnIcon_losefocus,ptr->ColumnIcon_getfocus,ptr->ColumnIcon_onclick);
	
	return sqlite_transaction_exec(sqlite_cmd);
}

/*
 GuideList.xml到来时，删除旧表，所以此处不用考虑UPDATE
*/
static int guidelist_insert(DBSTAR_GUIDELIST_S *ptr)
{
	if(NULL==ptr){
		DEBUG("invalid arguments\n");
		return -1;
	}
	
//	char sqlite_cmd[512];
//	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO GuideList(DateValue,GuideListID,productID,PublicationID,URI,TotalSize,ProductDescID,ReceiveStatus,PushTime,UserStatus) VALUES('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s');",
//		ptr->DateValue,ptr->GuideListID,ptr->productID,ptr->PublicationID,ptr->URI,ptr->TotalSize,ptr->ProductDescID,ptr->ReceiveStatus,ptr->PushTime,ptr->UserStatus);
//	sqlite_transaction_exec(sqlite_cmd);
	
	return 0;
}

/*
 当新的播发单ProductDesc.xml到来时，将旧播发单对应在数据库中的正在下载的记录的ReceiveStatus由'0'标记为'-1'，表明下载失败
*/
#if 0
static int productdesc_history_clear(DBSTAR_PRODUCTDESC_S *ptr)
{
	if(NULL==ptr){
		DEBUG("invalid args\n");
		return -1;
	}
	
	char sqlite_cmd[512];
	if(0==strcmp(ptr->ReceiveType, "ReceivePublications")){
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "UPDATE Publication SET ReceiveStatus='-1' WHERE ReceiveStatus='0';");
	}
	else if(0==strcmp(ptr->ReceiveType, "ReceiveSets")){
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "UPDATE PublicationsSet SET ReceiveStatus='-1' WHERE ReceiveStatus='0';");
	}
	else if(	0==strcmp(ptr->ReceiveType, "ReceiveGuideList")
			||	0==strcmp(ptr->ReceiveType, "ReceivePreview")){
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "UPDATE ProductDesc SET ReceiveStatus='-1' WHERE ReceiveStatus='0';");
	}
	else{
		DEBUG("can not distinguish such ReceiveType: %s", ptr->ReceiveType);
		memset(sqlite_cmd, 0, sizeof(sqlite_cmd));
	}
	
	return sqlite_transaction_exec(sqlite_cmd);
}
#endif

static int productdesc_clear()
{
	char sqlite_cmd[512];
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "UPDATE Publication SET ReceiveStatus='-1' WHERE ReceiveStatus='0';");
	sqlite_transaction_exec(sqlite_cmd);
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "UPDATE Publication SET ReceiveStatus='2' WHERE ReceiveStatus='1';");
	sqlite_transaction_exec(sqlite_cmd);
	
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "UPDATE PublicationsSet SET ReceiveStatus='-1' WHERE ReceiveStatus='0';");
	sqlite_transaction_exec(sqlite_cmd);
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "UPDATE PublicationsSet SET ReceiveStatus='2' WHERE ReceiveStatus='1';");
	sqlite_transaction_exec(sqlite_cmd);
	
	sqlite_transaction_table_clear("ProductDesc");
	sqlite_transaction_exec(sqlite_cmd);
	
	return sqlite_transaction_exec(sqlite_cmd);
}

/*
 播发单ProductDesc.xml，成品Publication和成品集PublicationsSet直接存放到对应表中，而小片Preview和预告单GuideList则存放在播发单表ProductDesc中
*/
static int productdesc_insert(DBSTAR_PRODUCTDESC_S *ptr)
{
	if(NULL==ptr){
		DEBUG("invalid args\n");
		return -1;
	}
	
	tzset(); /* tzset()*/ 
	time_t timep;
	time (&timep);
	
	char sqlite_cmd[1024*4];
	char total_uri[512];
	char time_str[64];
	snprintf(total_uri, sizeof(total_uri), "%s/%s", ptr->rootPath,ptr->URI);
	snprintf(time_str, sizeof(time_str), "%s", asctime(localtime(&timep)));
	if('\n'==time_str[strlen(time_str)-1])
		time_str[strlen(time_str)-1] = '\0';
	snprintf(time_str+strlen(time_str), sizeof(time_str)-strlen(time_str), "#%ld", timep);
	
	char desc_uri[512];
	memset(desc_uri, 0, sizeof(desc_uri));
	char desc_path[512];
	snprintf(desc_path, sizeof(desc_path), "%s/%s", s_push_root_path, total_uri);
	distill_file(desc_path, desc_uri, sizeof(desc_uri), "xml", "Publication.xml");
	
	if(0==strcmp(ptr->ReceiveType, "ReceivePublications")){
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Publication(PublicationID,ColumnID,ProductID,URI,DescURI,TotalSize,ProductDescID,ReceiveStatus,PushTime,PublicationType,IsReserved,Visible,DRMFile,SetID,IndexInSet,Favorite,Bookmark,IsAuthorized,VODNum,VODPlatform) \
	VALUES('%s',\
	(select ColumnID from Publication where PublicationID='%s'),\
	'%s',\
	'%s',\
	'%s',\
	'%s',\
	'%s',\
	(select ReceiveStatus from Publication where PublicationID='%s'),\
	'%s',\
	(select PublicationType from Publication where PublicationID='%s'),\
	(select IsReserved from Publication where PublicationID='%s'),\
	(select Visible from Publication where PublicationID='%s'),\
	(select DRMFile from Publication where PublicationID='%s'),\
	(select SetID from Publication where PublicationID='%s'),\
	(select IndexInSet from Publication where PublicationID='%s'),\
	(select Favorite from Publication where PublicationID='%s'),\
	(select Bookmark from Publication where PublicationID='%s'),\
	(select IsAuthorized from Publication where PublicationID='%s'),\
	(select VODNum from Publication where PublicationID='%s'),\
	(select VODPlatform from Publication where PublicationID='%s'));",
		ptr->ID,
		ptr->ID,
		ptr->productID,
		total_uri,
		desc_uri,
		ptr->TotalSize,
		ptr->ProductDescID,
		ptr->ID,
		time_str,
		ptr->ID,
		ptr->ID,
		ptr->ID,
		ptr->ID,
		ptr->ID,
		ptr->ID,
		ptr->ID,
		ptr->ID,
		ptr->ID,
		ptr->ID,
		ptr->ID);
		
		return sqlite_transaction_exec(sqlite_cmd);
	}
	else if(0==strcmp(ptr->ReceiveType, "ReceiveSets")){
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO PublicationsSet(SetID,ColumnID,ProductID,URI,TotalSize,ProductDescID,ReceiveStatus,PushTime,IsReserved,Visible,Favorite,IsAuthorized,VODNum,VODPlatform) \
	VALUES('%s',\
	(select ColumnID from PublicationsSet where SetID='%s'),\
	'%s',\
	'%s',\
	'%s',\
	'%s',\
	(select ReceiveStatus from PublicationsSet where SetID='%s'),\
	'%s',\
	(select IsReserved from PublicationsSet where SetID='%s'),\
	(select Visible from PublicationsSet where SetID='%s'),\
	(select Favorite from PublicationsSet where SetID='%s'),\
	(select IsAuthorized from PublicationsSet where SetID='%s'),\
	(select VODNum from PublicationsSet where SetID='%s'),\
	(select VODPlatform from PublicationsSet where SetID='%s'));",
		ptr->ID,
		ptr->ID,
		ptr->productID,
		total_uri,
		ptr->TotalSize,
		ptr->ProductDescID,
		ptr->ID,
		time_str,
		ptr->ID,
		ptr->ID,
		ptr->ID,
		ptr->ID,
		ptr->ID,
		ptr->ID);
		
		return sqlite_transaction_exec(sqlite_cmd);
	}
	else if(	0==strcmp(ptr->ReceiveType, "ReceiveGuideList")
			||	0==strcmp(ptr->ReceiveType, "ReceivePreview")){
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ProductDesc(ReceiveType,ProductDescID,URI,TotalSize,ReceiveStatus,PushTime) VALUES('%s','%s','%s','%s','0','%s');",
			ptr->ReceiveType,ptr->ProductDescID,total_uri,ptr->TotalSize,time_str);
		
		return sqlite_transaction_exec(sqlite_cmd);
	}
	else{
		DEBUG("can not distinguish such ReceiveType: %s", ptr->ReceiveType);
	}
	
	return 0;
}

/*
 擦除接收单结构体中除rootPath和ReceiveType之外的变量
*/
static void productdesc_clear_partial(DBSTAR_PRODUCTDESC_S *ptr, int clear_productid)
{
	if(NULL==ptr)
		return;
	
	if(1==clear_productid)
		memset(ptr->productID, 0, sizeof(ptr->productID));
	memset(ptr->ProductDescID, 0, sizeof(ptr->ProductDescID));
	memset(ptr->SetID, 0, sizeof(ptr->SetID));
	memset(ptr->ID, 0, sizeof(ptr->ID));
	memset(ptr->TotalSize, 0, sizeof(ptr->TotalSize));
	memset(ptr->URI, 0, sizeof(ptr->URI));
	memset(ptr->Columns, 0, sizeof(ptr->Columns));
}

/*
 插入栏目和成品、成品集、产品之间的关系。但目前无法支持更新功能。
*/
static int column_entity_insert(DBSTAR_COLUMNENTITY_S *p, char *entity_type)
{
	if(NULL==p || 0==strlen(p->columnID) || 0==strlen(p->EntityID) || NULL==entity_type || 0==strlen(entity_type)){
		DEBUG("invalid arguments\n");
		return -1;
	}
	
	char sqlite_cmd[512];
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ColumnEntity(ColumnID,EntityID,EntityType) VALUES('%s','%s','%s');",
		p->columnID,p->EntityID, entity_type);
	
	return sqlite_transaction_exec(sqlite_cmd);
}


/*
 只在Publication表中插入PublicationID和ProductID之间的对应关系
*/
static int publication_insert_productid(char *PublicationID, char *ProductID)
{
	if(NULL==PublicationID || NULL==ProductID || 0==strlen(PublicationID) || 0==strlen(ProductID)){
		DEBUG("invalid args\n");
		return -1;
	}
	
	char sqlite_cmd[1024*4];
	
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Publication(PublicationID,ColumnID,ProductID,URI,DescURI,TotalSize,ProductDescID,ReceiveStatus,PushTime,PublicationType,IsReserved,Visible,DRMFile,SetID,IndexInSet,Favorite,Bookmark,IsAuthorized,VODNum,VODPlatform) \
	VALUES('%s',\
	(select ColumnID from Publication where PublicationID='%s'),\
	'%s',\
	(select URI from Publication where PublicationID='%s'),\
	(select DescURI from Publication where PublicationID='%s'),\
	(select TotalSize from Publication where PublicationID='%s'),\
	(select ProductDescID from Publication where PublicationID='%s'),\
	(select ReceiveStatus from Publication where PublicationID='%s'),\
	(select PushTime from Publication where PublicationID='%s'),\
	(select PublicationType from Publication where PublicationID='%s'),\
	(select IsReserved from Publication where PublicationID='%s'),\
	(select Visible from Publication where PublicationID='%s'),\
	(select DRMFile from Publication where PublicationID='%s'),\
	(select SetID from Publication where PublicationID='%s'),\
	(select IndexInSet from Publication where PublicationID='%s'),\
	(select Favorite from Publication where PublicationID='%s'),\
	(select Bookmark from Publication where PublicationID='%s'),\
	(select IsAuthorized from Publication where PublicationID='%s'),\
	(select VODNum from Publication where PublicationID='%s'),\
	(select VODPlatform from Publication where PublicationID='%s'));",
		PublicationID,
		PublicationID,
		ProductID,
		PublicationID,
		PublicationID,
		PublicationID,
		PublicationID,
		PublicationID,
		PublicationID,
		PublicationID,
		PublicationID,
		PublicationID,
		PublicationID,
		PublicationID,
		PublicationID,
		PublicationID,
		PublicationID,
		PublicationID,
		PublicationID,
		PublicationID);
	return sqlite_transaction_exec(sqlite_cmd);
}

/*
 只在Previews表中插入PreviewsID和ProductID之间的对应关系
*/
static int preview_insert_productid(char *PreviewID, char *ProductID)
{
	if(NULL==PreviewID || NULL==ProductID || 0==strlen(PreviewID) || 0==strlen(ProductID)){
		DEBUG("invalid args\n");
		return -1;
	}
	
	char sqlite_cmd[1024*4];
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Preview(PreviewID,ProductID,PreviewType,PreviewSize,ShowTime,PreviewURI,PreviewFormat,Duration,Resolution,BitRate,CodeFormat,URI,TotalSize,ProductDescID,ReceiveStatus,PushTime,StartTime,EndTime,PlayMode) \
	VALUES('%s',\
	'%s',\
	(select PreviewType from Preview where PreviewID='%s'),\
	(select PreviewSize from Preview where PreviewID='%s'),\
	(select ShowTime from Preview where PreviewID='%s'),\
	(select PreviewURI from Preview where PreviewID='%s'),\
	(select PreviewFormat from Preview where PreviewID='%s'),\
	(select Duration from Preview where PreviewID='%s'),\
	(select Resolution from Preview where PreviewID='%s'),\
	(select BitRate from Preview where PreviewID='%s'),\
	(select CodeFormat from Preview where PreviewID='%s'),\
	(select URI from Preview where PreviewID='%s'),\
	(select TotalSize from Preview where PreviewID='%s'),\
	(select ProductDescID from Preview where PreviewID='%s'),\
	(select ReceiveStatus from Preview where PreviewID='%s'),\
	(select PushTime from Preview where PreviewID='%s'),\
	(select StartTime from Preview where PreviewID='%s'),\
	(select EndTime from Preview where PreviewID='%s'),\
	(select PlayMode from Preview where PreviewID='%s'));",
		PreviewID,
		ProductID,
		PreviewID,
		PreviewID,
		PreviewID,
		PreviewID,
		PreviewID,
		PreviewID,
		PreviewID,
		PreviewID,
		PreviewID,
		PreviewID,
		PreviewID,
		PreviewID,
		PreviewID,
		PreviewID,
		PreviewID,
		PreviewID,
		PreviewID);
	return sqlite_transaction_exec(sqlite_cmd);
}


/*
 channel记录入库前会将Channel数据表清空，故此处不必考虑UPDATE的情况。
*/
static int channel_insert(DBSTAR_CHANNEL_S *p)
{
	if(NULL==p || 0>=strlen(p->pid))
		return -1;
	
	char sqlite_cmd[512];
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Channel(pid,pidtype,multiURI) VALUES('%s','%s','%s');",p->pid,p->pidtype,p->multiURI);
	return sqlite_transaction_exec(sqlite_cmd);
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
	
	char sqlite_cmd[1024*4];
	
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Publication(PublicationID,ColumnID,ProductID,URI,DescURI,TotalSize,ProductDescID,ReceiveStatus,PushTime,PublicationType,IsReserved,Visible,DRMFile,SetID,IndexInSet,Favorite,Bookmark,IsAuthorized,VODNum,VODPlatform) \
	VALUES('%s',\
	(select ColumnID from Publication where PublicationID='%s'),\
	(select ProductID from Publication where PublicationID='%s'),\
	(select URI from Publication where PublicationID='%s'),\
	(select DescURI from Publication where PublicationID='%s'),\
	(select TotalSize from Publication where PublicationID='%s'),\
	(select ProductDescID from Publication where PublicationID='%s'),\
	(select ReceiveStatus from Publication where PublicationID='%s'),\
	(select PushTime from Publication where PublicationID='%s'),\
	'%s',\
	'%s',\
	'%s',\
	'%s',\
	(select SetID from Publication where PublicationID='%s'),\
	(select IndexInSet from Publication where PublicationID='%s'),\
	(select Favorite from Publication where PublicationID='%s'),\
	(select Bookmark from Publication where PublicationID='%s'),\
	(select IsAuthorized from Publication where PublicationID='%s'),\
	(select VODNum from Publication where PublicationID='%s'),\
	(select VODPlatform from Publication where PublicationID='%s'));",
		p->PublicationID,
		p->PublicationID,
		p->PublicationID,
		p->PublicationID,
		p->PublicationID,
		p->PublicationID,
		p->PublicationID,
		p->PublicationID,
		p->PublicationID,
		p->PublicationType,
		p->IsReserved,
		p->Visible,
		p->DRMFile,
		p->PublicationID,
		p->PublicationID,
		p->PublicationID,
		p->PublicationID,
		p->PublicationID,
		p->PublicationID,
		p->PublicationID);
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
	
	char sqlite_cmd[512];
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO MultipleLanguageInfoVA(PublicationID,infolang,Keywords,ImageDefinition,Director,Episode,Actor,AudioChannel,AspectRatio,Audience,Model,Language,Area) VALUES('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s');",
		p->PublicationID,p->infolang,p->Keywords,p->ImageDefinition,p->Director,p->Episode,p->Actor,p->AudioChannel,p->AspectRatio,p->Audience,p->Model,p->Language,p->Area);
	
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
	
	char sqlite_cmd[512];
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO MultipleLanguageInfoRM(PublicationID,infolang,Keywords,Publisher,Area,Language,Episode,AspectRatio,VolNum,ISSN) VALUES('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s');",
		p->PublicationID,p->infolang,p->Keywords,p->Publisher,p->Area,p->Language,p->Episode,p->AspectRatio,p->VolNum,p->ISSN);
	
	return sqlite_transaction_exec(sqlite_cmd);
}

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
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO MultipleLanguageInfoApp(PublicationID,infolang,Keywords,Category,Released,AppVersion,Language,Developer,Rated) VALUES('%s','%s','%s','%s','%s','%s','%s','%s','%s');",
		p->PublicationID,p->infolang,p->Keywords,p->Category,p->Released,p->AppVersion,p->Language,p->Developer,p->Rated);
	
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
	
	char sqlite_cmd[512];
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResSubTitle(ObjectName,EntityID,SubTitleID,SubTitleName,SubTitleLanguage,SubTitleURI) VALUES('%s','%s','%s','%s','%s','%s');",
		p->ObjectName,p->EntityID,p->SubTitleID,p->SubTitleName,p->SubTitleLanguage,p->SubTitleURI);
	
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
	
	char sqlite_cmd[512];
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResPoster(ObjectName,EntityID,PosterID,PosterName,PosterURI) VALUES('%s','%s','%s','%s','%s');",
		p->ObjectName, p->EntityID, p->PosterID, p->PosterName, p->PosterURI);
	
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
	
	char sqlite_cmd[512];
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResTrailer(ObjectName,EntityID,TrailerID,TrailerName,TrailerURI) VALUES('%s','%s','%s','%s','%s');",
		p->ObjectName, p->EntityID, p->TrailerID, p->TrailerName, p->TrailerURI);
	
	return sqlite_transaction_exec(sqlite_cmd);
}

/*
 向主文件表MFile中插入主文件信息
 主文件表是依存于宿主表的寄生表，因此它们自身不能独立封装为事务，应为某事务的一部分。
*/
static int mfile_insert(DBSTAR_MFILE_S *p)
{
	if(NULL==p || 0==strlen(p->PublicationID) || 0==strlen(p->FileID)){
		DEBUG("invalid argument\n");
		return -1;
	}
	
	char sqlite_cmd[512];
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Publication(FileID,PublicationID,FileType,FileSize,Duration,FileURI,Resolution,BitRate,FileFormat,CodeFormat) VALUES('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s');",
		p->FileID,p->PublicationID,p->FileType,p->FileSize,p->Duration,p->FileURI,p->Resolution,p->BitRate,p->FileFormat,p->CodeFormat);
	
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
	
	char sqlite_cmd[512];
	
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Message(MessageID,type,displayForm,StartTime,EndTime,Interval) VALUES('%s','%s','%s','%s','%s','%s');",
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
	
	char sqlite_cmd[1024*4];
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Preview(PreviewID,ProductID,PreviewType,PreviewSize,ShowTime,PreviewURI,PreviewFormat,Duration,Resolution,BitRate,CodeFormat,URI,TotalSize,ProductDescID,ReceiveStatus,PushTime,StartTime,EndTime,PlayMode) \
	VALUES('%s',\
	(select ProductID from Preview where PreviewID='%s'),\
	'%s',\
	'%s',\
	'%s',\
	'%s',\
	'%s',\
	'%s',\
	'%s',\
	'%s',\
	'%s',\
	(select URI from Preview where PreviewID='%s'),\
	(select TotalSize from Preview where PreviewID='%s'),\
	(select ProductDescID from Preview where PreviewID='%s'),\
	(select ReceiveStatus from Preview where PreviewID='%s'),\
	(select PushTime from Preview where PreviewID='%s'),\
	(select StartTime from Preview where PreviewID='%s'),\
	(select EndTime from Preview where PreviewID='%s'),\
	(select PlayMode from Preview where PreviewID='%s'));",
		p->PreviewID,
		p->PreviewID,
		p->PreviewType,
		p->PreviewSize,
		p->ShowTime,
		p->PreviewURI,
		p->PreviewFormat,
		p->Duration,
		p->Resolution,
		p->BitRate,
		p->CodeFormat,
		p->PreviewID,
		p->PreviewID,
		p->PreviewID,
		p->PreviewID,
		p->PreviewID,
		p->PreviewID,
		p->PreviewID,
		p->PreviewID);
	return sqlite_transaction_exec(sqlite_cmd);
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
		DEBUG("some arguments are invalide\n");
		return;
	}
	
	//DEBUG("----------- property start -----------\n");
	xmlChar *szAttr = NULL;
	xmlAttrPtr attrPtr = cur->properties;
	while(NULL!=attrPtr){
		szAttr = xmlGetProp(cur, attrPtr->name);
		if(NULL!=szAttr)
		{
			//DEBUG("property of %s, %s: %s\n", xmlroute, attrPtr->name, szAttr);

// Initialize.xml
			if(0==strcmp(xmlroute, "Initialize^ServiceInits^ServiceInit")){
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
			
// GuideList.xml
			else if(0==strcmp(xmlroute, "GuideList^Date^Product")){
				DBSTAR_GUIDELIST_S *p = (DBSTAR_GUIDELIST_S *)ptr;
				if(0==xmlStrcmp(BAD_CAST"productID", attrPtr->name)){
					strncpy(p->productID, (char *)szAttr, sizeof(p->productID)-1);
				}
//				else
//					DEBUG("can NOT process such property '%s' of xml route '%s'\n", attrPtr->name, xmlroute);
			}

#if 0
// Channel.xml
			else if(0==strcmp(xmlroute, "Channels^Service")){
				if(0==xmlStrcmp(BAD_CAST"serviceID", attrPtr->name)){
					strcpy((char *)ptr, (char *)szAttr);
				}
//				else
//					DEBUG("can NOT process such property '%s' of xml route '%s'\n", attrPtr->name, xmlroute);
			}
			else if(0==strcmp(xmlroute, "Channels^Service^Channel")){
				DBSTAR_CHANNEL_S *p  = (DBSTAR_CHANNEL_S *)ptr;
				if(0==xmlStrcmp(BAD_CAST"pid", attrPtr->name)){
					strncpy(p->pid, (char *)szAttr, sizeof(p->pid)-1);
				}
				else if(0==xmlStrcmp(BAD_CAST"pidType", attrPtr->name)){
					strncpy(p->pidType, (char *)szAttr, sizeof(p->pidType)-1);
				}
				else if(0==xmlStrcmp(BAD_CAST"multParamSet", attrPtr->name)){
					strncpy(p->multParamSet, (char *)szAttr, sizeof(p->multParamSet)-1);
				}
//				else
//					DEBUG("can NOT process such property '%s' of xml route '%s'\n", attrPtr->name, xmlroute);
			}
#endif

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
			
// Publication.xml
			else if(0==strcmp(xmlroute, "Publication^PublicationVA^MultipleLanguageInfos^MultipleLanguageInfo")){
				DBSTAR_MULTIPLELANGUAGEINFOVA_S *p = (DBSTAR_MULTIPLELANGUAGEINFOVA_S *)ptr;
				if(0==xmlStrcmp(BAD_CAST"language", attrPtr->name)){
					strncpy(p->infolang, (char *)szAttr, sizeof(p->infolang)-1);
				}
//				else
//					DEBUG("can NOT process such property '%s' of xml route '%s'\n", attrPtr->name, xmlroute);
			}

// ProductDesc.xml 当前投递单
			else if(0==strcmp(xmlroute, "ProductDesc^ReceivePublications")
					||	0==strcmp(xmlroute, "ProductDesc^ReceiveSets")
					||	0==strcmp(xmlroute, "ProductDesc^ReceiveGuideList")
					||	0==strcmp(xmlroute, "ProductDesc^ReceivePreview")){
				DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
				if(0==xmlStrcmp(BAD_CAST"rootPath", attrPtr->name)){
					strncpy(p->rootPath, (char *)szAttr, sizeof(p->rootPath)-1);
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
			else if(0==strcmp(xmlroute, "ProductDesc^ReceivePublications^Product^Publication^Columns^Column")){
				DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
				if(0==xmlStrcmp(BAD_CAST"columnID", attrPtr->name)){
					if(0==strlen(p->Columns))
						snprintf(p->Columns, sizeof(p->Columns), "%s", (char *)szAttr);
					else
						snprintf((p->Columns) + strlen(p->Columns), sizeof(p->Columns) - strlen(p->Columns), "%s", (char *)szAttr);
				}
				else
					DEBUG("can NOT process such property '%s' of xml route '%s'\n", attrPtr->name, xmlroute);
			}
// PublicationsColumn.xml
			else if(0==strcmp(xmlroute, "PublicationsColumn^Navigation")){
				DBSTAR_NAVIGATION_S *p = (DBSTAR_NAVIGATION_S *)ptr;
				if(0==xmlStrcmp(BAD_CAST"serviceID", attrPtr->name)){
					strncpy(p->serviceID, (char *)szAttr, sizeof(p->serviceID)-1);
				}
				else if(0==xmlStrcmp(BAD_CAST"navigationType", attrPtr->name)){
					strncpy(p->navigationType, (char *)szAttr, sizeof(p->navigationType)-1);
				}
				else
					DEBUG("can NOT process such property '%s' of xml route '%s'\n", attrPtr->name, xmlroute);
			}
			else if(0==strcmp(xmlroute, "PublicationsColumn^Navigation^Publications^PublicationColumn")){
				DBSTAR_COLUMNENTITY_S *p = (DBSTAR_COLUMNENTITY_S *)ptr;
				if(0==xmlStrcmp(BAD_CAST"publicationID", attrPtr->name)){
					strncpy(p->EntityID, (char *)szAttr, sizeof(p->EntityID)-1);
				}
				else
					DEBUG("can NOT process such property '%s' of xml route '%s'\n", attrPtr->name, xmlroute);
			}
			else if(0==strcmp(xmlroute, "PublicationsColumn^Navigation^Publications^SetColumn")){
				DBSTAR_COLUMNENTITY_S *p = (DBSTAR_COLUMNENTITY_S *)ptr;
				if(0==xmlStrcmp(BAD_CAST"setID", attrPtr->name)){
					strncpy(p->EntityID, (char *)szAttr, sizeof(p->EntityID)-1);
				}
				else
					DEBUG("can NOT process such property '%s' of xml route '%s'\n", attrPtr->name, xmlroute);
			}
			else if(0==strcmp(xmlroute, "PublicationsColumn^Navigation^Publications^ProductColumn")){
				DBSTAR_COLUMNENTITY_S *p = (DBSTAR_COLUMNENTITY_S *)ptr;
				if(0==xmlStrcmp(BAD_CAST"productID", attrPtr->name)){
					strncpy(p->EntityID, (char *)szAttr, sizeof(p->EntityID)-1);
				}
				else
					DEBUG("can NOT process such property '%s' of xml route '%s'\n", attrPtr->name, xmlroute);
			}
			else if(	0==strcmp(xmlroute, "PublicationsColumn^Navigation^Publications^PublicationColumn^Column")
					||	0==strcmp(xmlroute, "PublicationsColumn^Navigation^Publications^SetColumn^Column")
					||	0==strcmp(xmlroute, "PublicationsColumn^Navigation^Publications^ProductColumn^Column")){
				DBSTAR_COLUMNENTITY_S *p = (DBSTAR_COLUMNENTITY_S *)ptr;
				if(0==xmlStrcmp(BAD_CAST"columnID", attrPtr->name)){
					strncpy(p->columnID, (char *)szAttr, sizeof(p->columnID)-1);
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
			
// 集中处理字符串
			else if(	0==strcmp(xmlroute, "Publication^PublicationNames^PublicationName")
					||	0==strcmp(xmlroute, "ServiceGroup^Services^Service^ServiceNames^ServiceName")
					||	0==strcmp(xmlroute, "ServiceGroup^Services^Service^Products^Product^ProductNames^ProductName")
					||	0==strcmp(xmlroute, "Product^ProductNames^ProductName")
					||	0==strcmp(xmlroute, "Product^Descriptions^Description")
					||	0==strcmp(xmlroute, "PublicationsSets^product^PublicationsSet^SetNames^SetName")
					||	0==strcmp(xmlroute, "PublicationsSets^product^PublicationsSet^SetDescs^SetDesc")
					||	0==strcmp(xmlroute, "Publication^PublicationNames^PublicationName")
					||	0==strcmp(xmlroute, "Publication^PublicationVA^MFile^FileNames^FileName")
					||	0==strcmp(xmlroute, "Messages^Message^Content^SubContent")
					||	0==strcmp(xmlroute, "Preview^PreviewNames^PreviewName")
					||	0==strncmp(xmlroute, "GuideList^Date^Item^", strlen("GuideList^Date^Item^"))
					||	0==strcmp(xmlroute, "ProductDesc^ReceivePublications^Product^Publication^PublicationNames^PublicationName")
					||	0==strcmp(xmlroute, "ProductDesc^ReceiveSets^Set^SetNames^SetName")
					||	0==strcmp(xmlroute, "ProductDesc^ReceiveGuideList^GuideListNames^GuideListName")
					||	0==strcmp(xmlroute, "ProductDesc^ReceivePreview^PreviewNames^PreviewName")
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
					int value_size = strlen((char *)szAttr)+1;
					p->StrValue = malloc(value_size);
					if(p->StrValue)
						snprintf(p->StrValue, value_size, "%s", (char *)szAttr);
					else
						ERROROUT("malloc %d failed\n", value_size);
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
*/
/*
 返回值：
 0——成功
 -1——失败
*/
static int parseNode (xmlDocPtr doc, xmlNodePtr cur, char *xmlroute, void *ptr, DBSTAR_XMLINFO_S *xmlinfo, char *rootelement, int *p_child_tree_is_valid, char *xml_ver)
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
		//DEBUG("%s cur->name:%s\n", XML_TEXT_NODE==cur->type?"XML_TEXT_NODE":"not XML_TEXT_NODE", cur->name);
		if(XML_TEXT_NODE==cur->type){
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
				if(NULL==xml_ver || 0==strcmp(xml_ver, xmlinfo->Version)){
					DEBUG("same xml version: %s\n", xml_ver);
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
			DEBUG("XML route: %s\n", new_xmlroute);
			
// Initialize.xml
			if(0==strncmp(new_xmlroute, "Initialize^", strlen("Initialize^"))){
				if(0==strcmp(new_xmlroute, "Initialize^ServiceInits")){
					parseNode(doc, cur, new_xmlroute, NULL, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "Initialize^ServiceInits^ServiceInit")){
					DBSTAR_PRODUCT_SERVICE_S product_service_s;
					memset(&product_service_s, 0, sizeof(product_service_s));
					parseProperty(cur, new_xmlroute, (void *)&product_service_s);
					if(1==special_productid_check(product_service_s.productID)){
						DEBUG("detect valid productID: %s\n", product_service_s.productID);
						DBSTAR_GLOBAL_S global_s;
						memset(&global_s, 0, sizeof(global_s));
						strncpy(global_s.Name, GLB_NAME_SERVICEID, sizeof(global_s.Name)-1);
						strncpy(global_s.Value, product_service_s.serviceID, sizeof(global_s.Value)-1);
						global_insert(&global_s);
						
						sqlite_transaction_table_clear("Initialize");
						parseNode(doc, cur, new_xmlroute, NULL, NULL, NULL, NULL, NULL);
						process_over = XML_EXIT_MOVEUP;
					}
					else
						DEBUG("productID %s is invalid\n", product_service_s.productID);
				}
				else if(0==strcmp(new_xmlroute, "Initialize^ServiceInits^ServiceInit^Channels")){
					pid_init(0);
					sqlite_transaction_table_clear("Channel");
					parseNode(doc, cur, new_xmlroute, NULL, NULL, NULL, NULL, NULL);
					pid_init(1);
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
					if(0==xmlStrcmp(cur->name, BAD_CAST"Guidelist"))
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
					else
						DEBUG("such xml node can not be processed: %s\n", new_xmlroute);
					
					parseProperty(cur, new_xmlroute, (void *)(&xmlinfo));
					xmlinfo_insert(&xmlinfo);
				}
				else
					DEBUG("can not distinguish such xml route: %s\n", new_xmlroute);
			}
#if 0
// Channels.xml
			else if(0==strncmp(new_xmlroute, "Channels^", strlen("Channels^"))){
				if(0==strcmp(new_xmlroute, "Channels^Service")){
					char serviceID[64];
					memset(serviceID, 0, sizeof(serviceID));
					parseProperty(cur, new_xmlroute, (void *)serviceID);
					if(0==strcmp(serviceID, serviceID_get())){
						DEBUG("detect valid productID: %s\n", serviceID);
						pid_init(0);
						sqlite_transaction_table_clear("Channel");
						parseNode(doc, cur, new_xmlroute, NULL, NULL, NULL, NULL, NULL);
						process_over = XML_EXIT_MOVEUP;
					}
					else
						DEBUG("this productID is invalid: %s\n", serviceID);
				}
				else if(0==strcmp(new_xmlroute, "Channels^Service^Channel")){
					DBSTAR_CHANNEL_S channel_s;
					memset(&channel_s, 0, sizeof(channel_s));
					parseProperty(cur, new_xmlroute, (void *)(&channel_s));
					channel_insert(&channel_s);
				}
			}
#endif
// Service.xml
			else if(0==strncmp(new_xmlroute, "ServiceGroup^", strlen("ServiceGroup^"))){
				if(0==strcmp(new_xmlroute, "ServiceGroup^Services")){
					/*
					在父节点上定义子节点的结构体，并清空
					*/
					DBSTAR_SERVICE_S service_s;
					memset(&service_s, 0, sizeof(service_s));
					int child_tree_is_valid = 0;
					//DEBUG("child_tree_is_valid's addr: %d\n", &child_tree_is_valid);
					parseNode(doc, cur, new_xmlroute, &service_s, NULL, NULL, &child_tree_is_valid, NULL);
					process_over = child_tree_is_valid;
				}
				else if(0==strcmp(new_xmlroute, "ServiceGroup^Services^Service")){
					/*
					清空结构体，预备本次使用。
					*/
					memset(ptr, 0, sizeof(DBSTAR_SERVICE_S));
					/*
					清空结构体后，进入节点内部，预备填充数据。
					*/
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, p_child_tree_is_valid, NULL);
					process_over = *p_child_tree_is_valid;
					if(XML_EXIT_MOVEUP==process_over)
						service_insert((DBSTAR_SERVICE_S *)ptr);
				}
				else if(0==strcmp(new_xmlroute, "ServiceGroup^Services^Service^ServiceID")){
					DBSTAR_SERVICE_S *p_service = (DBSTAR_SERVICE_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p_service->ServiceID, (char *)szKey, sizeof(p_service->ServiceID)-1);
					xmlFree(szKey);
					//DEBUG("child_tree_is_valid's addr: %d\n", p_child_tree_is_valid);
					if(0==strcmp(p_service->ServiceID,serviceID_get())){
						DEBUG("catch valid ServiceID %s\n", p_service->ServiceID);
						// 这个地方的判断比较麻烦，需要停止其叔伯节点，而不是兄弟节点。process_over标记不能在这里使用
						*p_child_tree_is_valid = 1;
					}
					else{
						DEBUG("ServiceID %s is invalid\n", p_service->ServiceID);
						*p_child_tree_is_valid = 0;
						process_over = XML_EXIT_MOVEUP;	// 无效节点，接下来不需要无意义的扫描。
					}
				}
				else if(0==strcmp(new_xmlroute, "ServiceGroup^Services^Service^ServiceNames")){
					//DEBUG("ServiceID: %s\n", ((DBSTAR_SERVICE_S *)ptr)->ServiceID);
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "ServiceGroup^Services^Service^ServiceNames^ServiceName")){
					DBSTAR_SERVICE_S *p_service = (DBSTAR_SERVICE_S *)ptr;
					
					DBSTAR_RESSTR_S resstr_s;
					memset(&resstr_s, 0, sizeof(resstr_s));
					
					strncpy(resstr_s.ObjectName, OBJ_SERVICE, sizeof(resstr_s.ObjectName)-1);
					//DEBUG("ServiceID: %s\n", p_service->ServiceID);
					if(strlen(p_service->ServiceID)>0)
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s", p_service->ServiceID);
					else
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s%s", OBJID_PAUSE, OBJ_SERVICE);
					strncpy(resstr_s.StrName, "ServiceName", sizeof(resstr_s.StrName)-1);
					
					parseProperty(cur, new_xmlroute, (void *)(&resstr_s));
					
					resstr_insert(&resstr_s);
					if(resstr_s.StrValue)
						free(resstr_s.StrValue);
				}
				else if(0==strcmp(new_xmlroute, "ServiceGroup^Services^Service^RegionCode")){
					DBSTAR_SERVICE_S *p_service = (DBSTAR_SERVICE_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p_service->RegionCode, (char *)szKey, sizeof(p_service->RegionCode)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "ServiceGroup^Services^Service^OnlineTime")){
					DBSTAR_SERVICE_S *p_service = (DBSTAR_SERVICE_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p_service->OnlineTime, (char *)szKey, sizeof(p_service->OnlineTime)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "ServiceGroup^Services^Service^OfflineTime")){
					DBSTAR_SERVICE_S *p_service = (DBSTAR_SERVICE_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p_service->OfflineTime, (char *)szKey, sizeof(p_service->OfflineTime)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "ServiceGroup^Services^Service^Products")){
					DBSTAR_PRODUCT_S product_s;
					memset(&product_s, 0, sizeof(product_s));
					
					parseNode(doc, cur, new_xmlroute, &product_s, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "ServiceGroup^Services^Service^Products^Product")){
					memset(ptr, 0, sizeof(DBSTAR_PRODUCT_S));
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
					product_insert((DBSTAR_PRODUCT_S *)ptr);
				}
				else if(0==strcmp(new_xmlroute, "ServiceGroup^Services^Service^Products^Product^ProductID")){
					DBSTAR_PRODUCT_S *p_product = (DBSTAR_PRODUCT_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p_product->ProductID, (char *)szKey, sizeof(p_product->ProductID)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "ServiceGroup^Services^Service^Products^Product^ProductType")){
					DBSTAR_PRODUCT_S *p_product = (DBSTAR_PRODUCT_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p_product->ProductType, (char *)szKey, sizeof(p_product->ProductType)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "ServiceGroup^Services^Service^Products^Product^Flag")){
					DBSTAR_PRODUCT_S *p_product = (DBSTAR_PRODUCT_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p_product->Flag, (char *)szKey, sizeof(p_product->Flag)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "ServiceGroup^Services^Service^Products^Product^ProductNames")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "ServiceGroup^Services^Service^Products^Product^ProductNames^ProductName")){
					DBSTAR_PRODUCT_S *p_product = (DBSTAR_PRODUCT_S *)ptr;
					
					DBSTAR_RESSTR_S resstr_s;
					memset(&resstr_s, 0, sizeof(resstr_s));
					
					strncpy(resstr_s.ObjectName, OBJ_PRODUCT, sizeof(resstr_s.ObjectName)-1);
					//DEBUG("ProductID: %s\n", p_product->ProductID);
					if(strlen(p_product->ProductID)>0)
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s", p_product->ProductID);
					else
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s%s", OBJID_PAUSE, OBJ_PRODUCT);
					strncpy(resstr_s.StrName, "ProductName", sizeof(resstr_s.StrName)-1);
					
					parseProperty(cur, new_xmlroute, (void *)(&resstr_s));
					
					resstr_insert(&resstr_s);
					if(resstr_s.StrValue)
						free(resstr_s.StrValue);
				}
				else if(0==strcmp(new_xmlroute, "ServiceGroup^Services^Service^Products^Product^OnlineDate")){
					DBSTAR_PRODUCT_S *p_product = (DBSTAR_PRODUCT_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p_product->OnlineDate, (char *)szKey, sizeof(p_product->OnlineDate)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "ServiceGroup^Services^Service^Products^Product^OfflineDate")){
					DBSTAR_PRODUCT_S *p_product = (DBSTAR_PRODUCT_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p_product->OfflineDate, (char *)szKey, sizeof(p_product->OfflineDate)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "ServiceGroup^Services^Service^Products^Product^IsReserved")){
					DBSTAR_PRODUCT_S *p_product = (DBSTAR_PRODUCT_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p_product->IsReserved, (char *)szKey, sizeof(p_product->IsReserved)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "ServiceGroup^Services^Service^Products^Product^Price")){
					DBSTAR_PRODUCT_S *p_product = (DBSTAR_PRODUCT_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p_product->Price, (char *)szKey, sizeof(p_product->Price)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "ServiceGroup^Services^Service^Products^Product^CurrencyType")){
					DBSTAR_PRODUCT_S *p_product = (DBSTAR_PRODUCT_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p_product->CurrencyType, (char *)szKey, sizeof(p_product->CurrencyType)-1);
					xmlFree(szKey);
				}
				else if(	0==strcmp(new_xmlroute, "ServiceGroup^Services^Service^Products^Product^ProductItem")
						||	0==strcmp(new_xmlroute, "ServiceGroup^Services^Service^Products^Product^ProductItem^Publications")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "ServiceGroup^Services^Service^Products^Product^ProductItem^Publications^PublicationID")){
					DBSTAR_PRODUCT_S *p_product = (DBSTAR_PRODUCT_S *)ptr;
					char publication_id[64];
					szKey = xmlNodeGetContent(cur);
					snprintf(publication_id, sizeof(publication_id), "%s", (char *)szKey);
					xmlFree(szKey);
					publication_insert_productid(publication_id, p_product->ProductID);
				}
			}
			
// xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
// Product.xml 这一部分和上面Service中的Product信息是重复的。。。。。。。。。
			else if(0==strncmp(new_xmlroute, "Product^", strlen("Product^"))){
				if(0==strcmp(new_xmlroute, "Product^ProductID")){
					DBSTAR_PRODUCT_S *p_product = (DBSTAR_PRODUCT_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p_product->ProductID, (char *)szKey, sizeof(p_product->ProductID)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Product^ProductType")){
					DBSTAR_PRODUCT_S *p_product = (DBSTAR_PRODUCT_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p_product->ProductType, (char *)szKey, sizeof(p_product->ProductType)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Product^Flag")){
					DBSTAR_PRODUCT_S *p_product = (DBSTAR_PRODUCT_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p_product->Flag, (char *)szKey, sizeof(p_product->Flag)-1);
					xmlFree(szKey);
				}
				else if(	0==strcmp(new_xmlroute, "Product^ProductNames")
						||	0==strcmp(new_xmlroute, "Product^Descriptions")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
				}
				else if(	0==strcmp(new_xmlroute, "Product^ProductNames^ProductName")
						||	0==strcmp(new_xmlroute, "Product^Descriptions^Description")){
					DBSTAR_PRODUCT_S *p_product = (DBSTAR_PRODUCT_S *)ptr;
					
					DBSTAR_RESSTR_S resstr_s;
					memset(&resstr_s, 0, sizeof(resstr_s));
					
					strncpy(resstr_s.ObjectName, OBJ_PRODUCT, sizeof(resstr_s.ObjectName)-1);
					//DEBUG("ProductID: %s\n", p_product->ProductID);
					if(strlen(p_product->ProductID)>0)
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s", p_product->ProductID);
					else
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s%s", OBJID_PAUSE, OBJ_PRODUCT);
					
					int valid_str = 1;
					if(0==strcmp(new_xmlroute, "Product^ProductNames^ProductName"))
						strncpy(resstr_s.StrName, "ProductName", sizeof(resstr_s.StrName)-1);
					else if(0==strcmp(new_xmlroute, "Product^Descriptions^Description"))
						strncpy(resstr_s.StrName, "Description", sizeof(resstr_s.StrName)-1);
					else{
						DEBUG("shit! what a fucking xml route: %s\n", new_xmlroute);
						valid_str = 0;
					}
					
					if(1==valid_str){
						parseProperty(cur, new_xmlroute, (void *)(&resstr_s));
						
						resstr_insert(&resstr_s);
						if(resstr_s.StrValue)
							free(resstr_s.StrValue);
					}
				}
				else if(0==strcmp(new_xmlroute, "Product^OnlineDate")){
					DBSTAR_PRODUCT_S *p_product = (DBSTAR_PRODUCT_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p_product->OnlineDate, (char *)szKey, sizeof(p_product->OnlineDate)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Product^OfflineDate")){
					DBSTAR_PRODUCT_S *p_product = (DBSTAR_PRODUCT_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p_product->OfflineDate, (char *)szKey, sizeof(p_product->OfflineDate)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Product^IsReserved")){
					DBSTAR_PRODUCT_S *p_product = (DBSTAR_PRODUCT_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p_product->IsReserved, (char *)szKey, sizeof(p_product->IsReserved)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Product^Price")){
					DBSTAR_PRODUCT_S *p_product = (DBSTAR_PRODUCT_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p_product->Price, (char *)szKey, sizeof(p_product->Price)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Product^CurrencyType")){
					DBSTAR_PRODUCT_S *p_product = (DBSTAR_PRODUCT_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p_product->CurrencyType, (char *)szKey, sizeof(p_product->CurrencyType)-1);
					xmlFree(szKey);
				}
				else if(	0==strcmp(new_xmlroute, "Product^ProductItem")
						||	0==strcmp(new_xmlroute, "Product^ProductItem^Publications")
						||	0==strcmp(new_xmlroute, "Product^ProductItem^Previews")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "Product^ProductItem^Publications^PublicationID")){
					DBSTAR_PRODUCT_S *p_product = (DBSTAR_PRODUCT_S *)ptr;
					char publication_id[64];
					szKey = xmlNodeGetContent(cur);
					snprintf(publication_id, sizeof(publication_id), "%s", (char *)szKey);
					xmlFree(szKey);
					publication_insert_productid(publication_id, p_product->ProductID);
				}
				else if(0==strcmp(new_xmlroute, "Product^ProductItem^Previews^PreviewsID")){
					DBSTAR_PRODUCT_S *p_product = (DBSTAR_PRODUCT_S *)ptr;
					char preview_id[64];
					szKey = xmlNodeGetContent(cur);
					snprintf(preview_id, sizeof(preview_id), "%s", (char *)szKey);
					xmlFree(szKey);
					preview_insert_productid(preview_id, p_product->ProductID);
				}
			}
			
// PublicationsSets.xml
			else if(0==strncmp(new_xmlroute, "PublicationsSets^", strlen("PublicationsSets^"))){
				if(0==strcmp(new_xmlroute, "PublicationsSets^product")){
					DBSTAR_PUBLICATIONSSET_S publicationset_s;
					memset(&publicationset_s, 0, sizeof(publicationset_s));
					
					parseNode(doc, cur, new_xmlroute, &publicationset_s, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "PublicationsSets^product^ProductID")){
					DBSTAR_PUBLICATIONSSET_S *p = (DBSTAR_PUBLICATIONSSET_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->ProductID, (char *)szKey, sizeof(p->ProductID)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "PublicationsSets^product^PublicationsSet")){
					DBSTAR_PUBLICATIONSSET_S *p = (DBSTAR_PUBLICATIONSSET_S *)ptr;
					memset(p->SetID, 0, sizeof(p->SetID));
					memset(p->PublicationID, 0, sizeof(p->PublicationID));
					memset(p->IndexInSet, 0, sizeof(p->IndexInSet));
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
					publicationsset_insert(p);
				}
				else if(0==strcmp(new_xmlroute, "PublicationsSets^product^PublicationsSet^SetID")){
					DBSTAR_PUBLICATIONSSET_S *p = (DBSTAR_PUBLICATIONSSET_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->SetID, (char *)szKey, sizeof(p->SetID)-1);
					xmlFree(szKey);
				}
				else if(	0==strcmp(new_xmlroute, "PublicationsSets^product^PublicationsSet^SetNames")
						||	0==strcmp(new_xmlroute, "PublicationsSets^product^PublicationsSet^SetDescs")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
				}
				else if(	0==strcmp(new_xmlroute, "PublicationsSets^product^PublicationsSet^SetNames^SetName")
						||	0==strcmp(new_xmlroute, "PublicationsSets^product^PublicationsSet^SetDescs^SetDesc")){
					DBSTAR_PUBLICATIONSSET_S *p = (DBSTAR_PUBLICATIONSSET_S *)ptr;
					
					DBSTAR_RESSTR_S resstr_s;
					memset(&resstr_s, 0, sizeof(resstr_s));
					
					strncpy(resstr_s.ObjectName, OBJ_PUBLICATIONSSET, sizeof(resstr_s.ObjectName)-1);
					if(strlen(p->SetID)>0)
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s", p->SetID);
					else
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s%s", OBJID_PAUSE, OBJ_PUBLICATIONSSET);
					
					int valid_str = 1;
					if(0==strcmp(new_xmlroute, "PublicationsSets^product^PublicationsSet^SetNames^SetName"))
						strncpy(resstr_s.StrName, "SetName", sizeof(resstr_s.StrName)-1);
					else if(0==strcmp(new_xmlroute, "PublicationsSets^product^PublicationsSet^SetDescs^SetDesc"))
						strncpy(resstr_s.StrName, "SetDesc", sizeof(resstr_s.StrName)-1);
					else{
						DEBUG("shit! what a fucking xml route: %s\n", new_xmlroute);
						valid_str = 0;
					}
					
					if(1==valid_str){
						parseProperty(cur, new_xmlroute, (void *)(&resstr_s));
						
						resstr_insert(&resstr_s);
						if(resstr_s.StrValue)
							free(resstr_s.StrValue);
					}
				}
				else if(0==strcmp(new_xmlroute, "PublicationsSets^product^PublicationsSet^Set")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "PublicationsSets^product^PublicationsSet^Set^Publications")){
					DBSTAR_PUBLICATIONSSET_S *p = (DBSTAR_PUBLICATIONSSET_S *)ptr;
					memset(p->PublicationID, 0, sizeof(p->PublicationID));
					memset(p->IndexInSet, 0, sizeof(p->IndexInSet));
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
					publication_insert_setinfo(p);
				}
				else if(0==strcmp(new_xmlroute, "PublicationsSets^product^PublicationsSet^Set^Publications^PublicationID")){
					DBSTAR_PUBLICATIONSSET_S *p = (DBSTAR_PUBLICATIONSSET_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->PublicationID, (char *)szKey, sizeof(p->PublicationID)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "PublicationsSets^product^PublicationsSet^Set^Publications^IndexInSet")){
					DBSTAR_PUBLICATIONSSET_S *p = (DBSTAR_PUBLICATIONSSET_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->IndexInSet, (char *)szKey, sizeof(p->IndexInSet)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "PublicationsSets^product^PublicationsSet^Posters")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "PublicationsSets^product^PublicationsSet^Posters^Poster")){
					DBSTAR_PUBLICATIONSSET_S *p = (DBSTAR_PUBLICATIONSSET_S *)ptr;
					
					DBSTAR_RESPOSTER_S poster_s;
					memset(&poster_s, 0, sizeof(poster_s));
					strncpy(poster_s.ObjectName, "PublicationsSet", sizeof(poster_s.ObjectName)-1);
					strncpy(poster_s.EntityID, p->SetID, sizeof(poster_s.EntityID)-1);
					
					parseNode(doc, cur, new_xmlroute, &poster_s, NULL, NULL, NULL, NULL);
					poster_insert(&poster_s);
				}
				else if(0==strcmp(new_xmlroute, "PublicationsSets^product^PublicationsSet^Posters^Poster^PosterID")){
					DBSTAR_RESPOSTER_S *p = (DBSTAR_RESPOSTER_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->PosterID, (char *)szKey, sizeof(p->PosterID)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "PublicationsSets^product^PublicationsSet^Posters^Poster^PosterName")){
					DBSTAR_RESPOSTER_S *p = (DBSTAR_RESPOSTER_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->PosterName, (char *)szKey, sizeof(p->PosterName)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "PublicationsSets^product^PublicationsSet^Posters^Poster^PosterURI")){
					DBSTAR_RESPOSTER_S *p = (DBSTAR_RESPOSTER_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->PosterURI, (char *)szKey, sizeof(p->PosterURI)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "PublicationsSets^product^PublicationsSet^Trailers")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "PublicationsSets^product^PublicationsSet^Trailers^Trailer")){
					DBSTAR_PUBLICATIONSSET_S *p = (DBSTAR_PUBLICATIONSSET_S *)ptr;
					
					DBSTAR_RESTRAILER_S trailer_s;
					memset(&trailer_s, 0, sizeof(trailer_s));
					strncpy(trailer_s.ObjectName, "PublicationsSet", sizeof(trailer_s.ObjectName)-1);
					strncpy(trailer_s.EntityID, p->SetID, sizeof(trailer_s.EntityID)-1);
					
					parseNode(doc, cur, new_xmlroute, &trailer_s, NULL, NULL, NULL, NULL);
					trailer_insert(&trailer_s);
				}
				else if(0==strcmp(new_xmlroute, "PublicationsSets^product^PublicationsSet^Trailers^Trailer^TrailerID")){
					DBSTAR_RESTRAILER_S *p = (DBSTAR_RESTRAILER_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->TrailerID, (char *)szKey, sizeof(p->TrailerID)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "PublicationsSets^product^PublicationsSet^Trailers^Trailer^TrailerName")){
					DBSTAR_RESTRAILER_S *p = (DBSTAR_RESTRAILER_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->TrailerName, (char *)szKey, sizeof(p->TrailerName)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "PublicationsSets^product^PublicationsSet^Trailers^Trailer^TrailerURI")){
					DBSTAR_RESTRAILER_S *p = (DBSTAR_RESTRAILER_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->TrailerURI, (char *)szKey, sizeof(p->TrailerURI)-1);
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
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
				}
				else if(	0==strcmp(new_xmlroute, "Publication^PublicationNames^PublicationName")){
					DBSTAR_PUBLICATION_S *p = (DBSTAR_PUBLICATION_S *)ptr;
					
					DBSTAR_RESSTR_S resstr_s;
					memset(&resstr_s, 0, sizeof(resstr_s));
					
					strncpy(resstr_s.ObjectName, OBJ_PUBLICATION, sizeof(resstr_s.ObjectName)-1);
					if(strlen(p->PublicationID)>0)
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s", p->PublicationID);
					else
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s%s", OBJID_PAUSE, OBJ_PUBLICATION);
					
					strncpy(resstr_s.StrName, "PublicationName", sizeof(resstr_s.StrName)-1);
					
					parseProperty(cur, new_xmlroute, (void *)(&resstr_s));
						
					resstr_insert(&resstr_s);
					if(resstr_s.StrValue)
						free(resstr_s.StrValue);
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
					strncpy(p->IsReserved, (char *)szKey, sizeof(p->IsReserved)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^Visible")){
					DBSTAR_PUBLICATION_S *p = (DBSTAR_PUBLICATION_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->Visible, (char *)szKey, sizeof(p->Visible)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^DRMFile")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "Publication^DRMFile^FileURI")){
					DBSTAR_PUBLICATION_S *p = (DBSTAR_PUBLICATION_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->DRMFile, (char *)szKey, sizeof(p->DRMFile)-1);
					xmlFree(szKey);
				}

/*
 PublicationInfo部分和MFile部分，可以不入库
*/
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MultipleLanguageInfos")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MultipleLanguageInfos^MultipleLanguageInfo")){
					DBSTAR_PUBLICATION_S *p = (DBSTAR_PUBLICATION_S *)ptr;
					
					DBSTAR_MULTIPLELANGUAGEINFOVA_S info_va_s;
					memset(&info_va_s, 0, sizeof(info_va_s));
					strncpy(info_va_s.PublicationID, p->PublicationID, sizeof(info_va_s.PublicationID)-1);
					
					parseProperty(cur, new_xmlroute, (void *)&info_va_s);
					parseNode(doc, cur, new_xmlroute, (void *)&info_va_s, NULL, NULL, NULL, NULL);
					publicationva_info_insert(&info_va_s);
					if(info_va_s.PublicationDesc){
						free(info_va_s.PublicationDesc);
						info_va_s.PublicationDesc = NULL;
					}
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MultipleLanguageInfos^MultipleLanguageInfo^PublicationDesc")){
					DBSTAR_MULTIPLELANGUAGEINFOVA_S *p = (DBSTAR_MULTIPLELANGUAGEINFOVA_S *)ptr;
					int desc_size = 0;
					szKey = xmlNodeGetContent(cur);
					desc_size = strlen((char *)szKey)+1;
					p->PublicationDesc = malloc(desc_size);
					if(p->PublicationDesc){
						snprintf(p->PublicationDesc, desc_size, "%s", (char *)szKey);
					}
					else
						DEBUG("malloc %d for PublicationDesc faild\n", desc_size);
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
				
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^SubTitles")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^SubTitles^SubTitle")){
					DBSTAR_MULTIPLELANGUAGEINFOVA_S *p = (DBSTAR_MULTIPLELANGUAGEINFOVA_S *)ptr;
					
					DBSTAR_RESSUBTITLE_S subtitle_s;
					memset(&subtitle_s, 0, sizeof(subtitle_s));
					strncpy(subtitle_s.ObjectName, OBJ_PUBLICATION, sizeof(subtitle_s.ObjectName)-1);
					strncpy(subtitle_s.EntityID, p->PublicationID, sizeof(subtitle_s.EntityID)-1);
					parseNode(doc, cur, new_xmlroute, &subtitle_s, NULL, NULL, NULL, NULL);
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
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^Trailers^Trailer")){
					DBSTAR_MULTIPLELANGUAGEINFOVA_S *p = (DBSTAR_MULTIPLELANGUAGEINFOVA_S *)ptr;
					
					DBSTAR_RESTRAILER_S trailer_s;
					memset(&trailer_s, 0, sizeof(trailer_s));
					strncpy(trailer_s.ObjectName, OBJ_PUBLICATION, sizeof(trailer_s.ObjectName)-1);
					strncpy(trailer_s.EntityID, p->PublicationID, sizeof(trailer_s.EntityID)-1);
					parseNode(doc, cur, new_xmlroute, &trailer_s, NULL, NULL, NULL, NULL);
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
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^Posters^Poster")){
					DBSTAR_MULTIPLELANGUAGEINFOVA_S *p = (DBSTAR_MULTIPLELANGUAGEINFOVA_S *)ptr;
					
					DBSTAR_RESPOSTER_S poster_s;
					memset(&poster_s, 0, sizeof(poster_s));
					strncpy(poster_s.ObjectName, OBJ_PUBLICATION, sizeof(poster_s.ObjectName)-1);
					strncpy(poster_s.EntityID, p->PublicationID, sizeof(poster_s.EntityID)-1);
					parseNode(doc, cur, new_xmlroute, &poster_s, NULL, NULL, NULL, NULL);
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
					DBSTAR_MULTIPLELANGUAGEINFOVA_S *p = (DBSTAR_MULTIPLELANGUAGEINFOVA_S *)ptr;
					
					DBSTAR_MFILE_S mfile_s;
					memset(&mfile_s, 0, sizeof(mfile_s));
					strncpy(mfile_s.PublicationID, p->PublicationID,sizeof(mfile_s.PublicationID));
					parseNode(doc, cur, new_xmlroute, &mfile_s, NULL, NULL, NULL, NULL);
					mfile_insert(&mfile_s);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MFile^FileID")){
					DBSTAR_MFILE_S *p = (DBSTAR_MFILE_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->FileID, (char *)szKey, sizeof(p->FileID)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MFile^FileNames")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MFile^FileNames^FileName")){
					DBSTAR_MFILE_S *p_mfile = (DBSTAR_MFILE_S *)ptr;
					
					DBSTAR_RESSTR_S resstr_s;
					memset(&resstr_s, 0, sizeof(resstr_s));
					
					strncpy(resstr_s.ObjectName, OBJ_MFILE, sizeof(resstr_s.ObjectName)-1);
					//DEBUG("ProductID: %s\n", p_product->ProductID);
					if(strlen(p_mfile->PublicationID)>0)
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s", p_mfile->PublicationID);
					else
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s%s", OBJID_PAUSE, OBJ_MFILE);
					strncpy(resstr_s.StrName, "FileName", sizeof(resstr_s.StrName)-1);
					
					parseProperty(cur, new_xmlroute, (void *)(&resstr_s));
					
					resstr_insert(&resstr_s);
					if(resstr_s.StrValue)
						free(resstr_s.StrValue);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MFile^FileType")){
					DBSTAR_MFILE_S *p = (DBSTAR_MFILE_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->FileType, (char *)szKey, sizeof(p->FileType)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MFile^FileSize")){
					DBSTAR_MFILE_S *p = (DBSTAR_MFILE_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->FileSize, (char *)szKey, sizeof(p->FileSize)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MFile^Duration")){
					DBSTAR_MFILE_S *p = (DBSTAR_MFILE_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->Duration, (char *)szKey, sizeof(p->Duration)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MFile^FileURI")){
					DBSTAR_MFILE_S *p = (DBSTAR_MFILE_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->FileURI, (char *)szKey, sizeof(p->FileURI)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MFile^Resolution")){
					DBSTAR_MFILE_S *p = (DBSTAR_MFILE_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->Resolution, (char *)szKey, sizeof(p->Resolution)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MFile^BitRate")){
					DBSTAR_MFILE_S *p = (DBSTAR_MFILE_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->BitRate, (char *)szKey, sizeof(p->BitRate)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MFile^FileFormat")){
					DBSTAR_MFILE_S *p = (DBSTAR_MFILE_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->FileFormat, (char *)szKey, sizeof(p->FileFormat)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MFile^CodeFormat")){
					DBSTAR_MFILE_S *p = (DBSTAR_MFILE_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->CodeFormat, (char *)szKey, sizeof(p->CodeFormat)-1);
					xmlFree(szKey);
				}
			}
			
// Column.xml
			else if(0==strncmp(new_xmlroute, "Columns^", strlen("Columns^"))){
				if(0==strcmp(new_xmlroute, "Columns^ServiceID")){
					if(0!=strcmp((char *)szKey, serviceID_get())){
						DEBUG("invalid ServiceID %s for Column.xml, contrast with %s\n", (char *)szKey,serviceID_get());
						process_over = XML_EXIT_UNNECESSARY;
					}
					else{
						DEBUG("valid ServiceID %s for Column.xml\n", (char *)szKey);
						/*
						 不能一股脑的清理掉Column的所有数据，保留本地菜单
						*/
						char sqlite_cmd[256];
						snprintf(sqlite_cmd, sizeof(sqlite_cmd), "DELETE FROM Column WHERE ColumnType!='%d';", COLUMN_LOCAL);
						sqlite_transaction_exec(sqlite_cmd);
					}
				}
				else if(0==strcmp(new_xmlroute, "Columns^Column")){
					memset(ptr, 0, sizeof(DBSTAR_COLUMN_S));
					
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
					column_insert((DBSTAR_COLUMN_S *)ptr);
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
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
				else if(0==strcmp(new_xmlroute, "Columns^Column^DisplayNames^DisplayName")){
					DBSTAR_COLUMN_S *p_column = (DBSTAR_COLUMN_S *)ptr;
					
					DBSTAR_RESSTR_S resstr_s;
					memset(&resstr_s, 0, sizeof(resstr_s));
					
					strncpy(resstr_s.ObjectName, OBJ_COLUMN, sizeof(resstr_s.ObjectName)-1);
					if(strlen(p_column->ColumnID)>0)
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s", p_column->ColumnID);
					else
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s%s", OBJID_PAUSE, OBJ_COLUMN);
					strncpy(resstr_s.StrName, "DisplayName", sizeof(resstr_s.StrName)-1);
					
					parseProperty(cur, new_xmlroute, (void *)(&resstr_s));
					
					resstr_insert(&resstr_s);
					if(resstr_s.StrValue)
						free(resstr_s.StrValue);
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
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
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
				if(0==strcmp(new_xmlroute, "GuideList^ServiceID")){
					if(0!=strcmp((char *)szKey, serviceID_get())){
						DEBUG("invalid ServiceID %s for GuideList.xml, contrast with %s\n", (char *)szKey,serviceID_get());
						process_over = XML_EXIT_UNNECESSARY;
					}
					else{
						DEBUG("valid ServiceID %s for GuideList.xml\n", (char *)szKey);
						sqlite_transaction_table_clear("GuideList");
					}
				}
				else if(0==strcmp(new_xmlroute, "GuideList^Date")){
					memset(ptr, 0, sizeof(DBSTAR_GUIDELIST_S));
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "GuideList^Date^DateValue")){
					DBSTAR_GUIDELIST_S *p_guidelist = (DBSTAR_GUIDELIST_S *)ptr;
					
					szKey = xmlNodeGetContent(cur);
					strncpy(p_guidelist->DateValue, (char *)szKey, sizeof(p_guidelist->DateValue)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "GuideList^Date^Product")){
					parseProperty(cur, new_xmlroute, ptr);
				}
				else if(0==strcmp(new_xmlroute, "GuideList^Date^Item")){
					/*
					针对每个Item，清理除DataValue之外的其他变量
					*/
					DBSTAR_GUIDELIST_S *p_guidelist = (DBSTAR_GUIDELIST_S *)ptr;
					memset(p_guidelist->GuideListID, 0, sizeof(p_guidelist->GuideListID));
					memset(p_guidelist->PublicationID, 0, sizeof(p_guidelist->PublicationID));
					
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
					guidelist_insert((DBSTAR_GUIDELIST_S *)ptr);
				}
				else if(0==strcmp(new_xmlroute, "GuideList^Date^Item^PublicationID")){
					DBSTAR_GUIDELIST_S *p_guidelist = (DBSTAR_GUIDELIST_S *)ptr;
					
					szKey = xmlNodeGetContent(cur);
					strncpy(p_guidelist->PublicationID, (char *)szKey, sizeof(p_guidelist->PublicationID)-1);
					xmlFree(szKey);
					snprintf(p_guidelist->GuideListID, sizeof(p_guidelist->GuideListID), "%s_%s", p_guidelist->DateValue, p_guidelist->PublicationID);
				}
				else if(0==strcmp(new_xmlroute, "GuideList^Date^Item^PublicationNames")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "GuideList^Date^Item^PublicationNames^PublicationName")){
					DBSTAR_GUIDELIST_S *p_guidelist = (DBSTAR_GUIDELIST_S *)ptr;
					
					DBSTAR_RESSTR_S resstr_s;
					memset(&resstr_s, 0, sizeof(resstr_s));
					
					strncpy(resstr_s.ObjectName, OBJ_GUIDELIST, sizeof(resstr_s.ObjectName)-1);
					if(strlen(p_guidelist->GuideListID)>0)
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s", p_guidelist->GuideListID);
					else
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s%s", OBJID_PAUSE, OBJ_GUIDELIST);
					strncpy(resstr_s.StrName, "PublicationName", sizeof(resstr_s.StrName)-1);
					
					parseProperty(cur, new_xmlroute, (void *)(&resstr_s));
					
					resstr_insert(&resstr_s);
					if(resstr_s.StrValue)
						free(resstr_s.StrValue);
				}
				else if(0==strcmp(new_xmlroute, "GuideList^Date^Item^ColumNames")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "GuideList^Date^Item^ColumNames^ColumName")){
					DBSTAR_GUIDELIST_S *p_guidelist = (DBSTAR_GUIDELIST_S *)ptr;
					
					DBSTAR_RESSTR_S resstr_s;
					memset(&resstr_s, 0, sizeof(resstr_s));
					
					strncpy(resstr_s.ObjectName, OBJ_GUIDELIST, sizeof(resstr_s.ObjectName)-1);
					if(strlen(p_guidelist->GuideListID)>0)
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s", p_guidelist->GuideListID);
					else
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s%s", OBJID_PAUSE, OBJ_GUIDELIST);
					strncpy(resstr_s.StrName, "ColumName", sizeof(resstr_s.StrName)-1);
					
					parseProperty(cur, new_xmlroute, (void *)(&resstr_s));
					
					resstr_insert(&resstr_s);
					if(resstr_s.StrValue)
						free(resstr_s.StrValue);
				}
				else if(0==strcmp(new_xmlroute, "GuideList^Date^Item^PublicationDescs")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "GuideList^Date^Item^PublicationDescs^PublicationDesc")){
					DBSTAR_GUIDELIST_S *p_guidelist = (DBSTAR_GUIDELIST_S *)ptr;
					
					DBSTAR_RESSTR_S resstr_s;
					memset(&resstr_s, 0, sizeof(resstr_s));
					
					strncpy(resstr_s.ObjectName, OBJ_GUIDELIST, sizeof(resstr_s.ObjectName)-1);
					if(strlen(p_guidelist->GuideListID)>0)
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s", p_guidelist->GuideListID);
					else
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s%s", OBJID_PAUSE, OBJ_GUIDELIST);
					strncpy(resstr_s.StrName, "PublicationDesc", sizeof(resstr_s.StrName)-1);
					
					parseProperty(cur, new_xmlroute, (void *)(&resstr_s));
					
					resstr_insert(&resstr_s);
					if(resstr_s.StrValue)
						free(resstr_s.StrValue);
				}
 			}


// ProductDesc.xml 当前投递单
			else if(0==strncmp(new_xmlroute, "ProductDesc^", strlen("ProductDesc^"))){
				if(0==strcmp(new_xmlroute, "ProductDesc^ServiceID")){
					if(0!=strcmp((char *)szKey, serviceID_get())){
						DEBUG("invalid ServiceID %s for ProductDesc.xml, contrast with %s\n", (char *)szKey,serviceID_get());
						process_over = XML_EXIT_UNNECESSARY;
					}
					else{
						DEBUG("valid ServiceID %s for ProductDesc.xml\n", (char *)szKey);
						productdesc_clear();
					}
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceivePublications")){
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					memset(ptr, 0, sizeof(DBSTAR_PRODUCTDESC_S));
					parseProperty(cur, new_xmlroute, ptr);
					
					strncpy(p->ReceiveType, "ReceivePublications", sizeof(p->ReceiveType)-1);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceivePublications^Product")){
					productdesc_clear_partial((DBSTAR_PRODUCTDESC_S *)ptr, 1);
					parseProperty(cur, new_xmlroute, ptr);
					DEBUG("productID: %s\n", ((DBSTAR_PRODUCTDESC_S *)ptr)->productID);
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceivePublications^Product^Publication")){
					productdesc_clear_partial((DBSTAR_PRODUCTDESC_S *)ptr, 0);
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
					productdesc_insert((DBSTAR_PRODUCTDESC_S *)ptr);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceivePublications^Product^Publication^PublicationID")){
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					
					szKey = xmlNodeGetContent(cur);
					strncpy(p->ID, (char *)szKey, sizeof(p->ID)-1);
					snprintf(p->ProductDescID, sizeof(p->ProductDescID),"Publication_%s", p->ID);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceivePublications^Product^Publication^PublicationNames")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceivePublications^Product^Publication^PublicationNames^PublicationName")){
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					
					DBSTAR_RESSTR_S resstr_s;
					memset(&resstr_s, 0, sizeof(resstr_s));
					
					strncpy(resstr_s.ObjectName, OBJ_PRODUCTDESC, sizeof(resstr_s.ObjectName)-1);
					if(strlen(p->ProductDescID)>0)
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s", p->ProductDescID);
					else
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s%s", OBJID_PAUSE, OBJ_PRODUCTDESC);
					strncpy(resstr_s.StrName, "PublicationName", sizeof(resstr_s.StrName)-1);
					
					parseProperty(cur, new_xmlroute, (void *)(&resstr_s));
					
					resstr_insert(&resstr_s);
					if(resstr_s.StrValue)
						free(resstr_s.StrValue);
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
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					
					szKey = xmlNodeGetContent(cur);
					strncpy(p->URI, (char *)szKey, sizeof(p->URI)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceivePublications^Product^Publication^Columns")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceivePublications^Product^Publication^Columns^Column")){
					parseProperty(cur, new_xmlroute, ptr);
				}
				
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceiveSets")){
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					memset(ptr, 0, sizeof(DBSTAR_PRODUCTDESC_S));
					parseProperty(cur, new_xmlroute, ptr);
					
					if(0==strcmp(p->serviceID, serviceID_get())){
						strncpy(p->ReceiveType, "ReceiveSets", sizeof(p->ReceiveType)-1);
						//productdesc_history_clear(p);
						parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
					}
					else
						DEBUG("ignore invalid serviceID: %s\n", p->serviceID);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceiveSets^Set")){
					productdesc_clear_partial((DBSTAR_PRODUCTDESC_S *)ptr, 1);
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
					productdesc_insert((DBSTAR_PRODUCTDESC_S *)ptr);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceiveSets^Set^SetID")){
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					
					szKey = xmlNodeGetContent(cur);
					strncpy(p->ID, (char *)szKey, sizeof(p->ID)-1);
					snprintf(p->ProductDescID, sizeof(p->ProductDescID),"SetID_%s", p->ID);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceiveSets^Set^SetNames")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceiveSets^Set^SetNames^SetName")){
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					
					DBSTAR_RESSTR_S resstr_s;
					memset(&resstr_s, 0, sizeof(resstr_s));
					
					strncpy(resstr_s.ObjectName, OBJ_PRODUCTDESC, sizeof(resstr_s.ObjectName)-1);
					if(strlen(p->ProductDescID)>0)
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s", p->ProductDescID);
					else
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s%s", OBJID_PAUSE, OBJ_PRODUCTDESC);
					strncpy(resstr_s.StrName, "SetName", sizeof(resstr_s.StrName)-1);
					
					parseProperty(cur, new_xmlroute, (void *)(&resstr_s));
					
					resstr_insert(&resstr_s);
					if(resstr_s.StrValue)
						free(resstr_s.StrValue);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceiveSets^Set^TotalSize")){
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					
					szKey = xmlNodeGetContent(cur);
					strncpy(p->TotalSize, (char *)szKey, sizeof(p->TotalSize)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceiveSets^Set^SetURI")){
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					
					szKey = xmlNodeGetContent(cur);
					strncpy(p->URI, (char *)szKey, sizeof(p->URI)-1);
					xmlFree(szKey);
				}
				
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceiveGuideList")){
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					memset(ptr, 0, sizeof(DBSTAR_PRODUCTDESC_S));
					parseProperty(cur, new_xmlroute, ptr);
					
					if(0==strcmp(p->serviceID, serviceID_get())){
						strncpy(p->ReceiveType, "ReceiveGuideList", sizeof(p->ReceiveType)-1);
						snprintf(p->ProductDescID, sizeof(p->ProductDescID), "GuideListID_%s", time_serial());
						//productdesc_history_clear(p);
						parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
						productdesc_insert((DBSTAR_PRODUCTDESC_S *)ptr);
					}
					else
						DEBUG("ignore invalid serviceID: %s\n", p->serviceID);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceiveGuideList^GuideListNames")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceiveGuideList^GuideListNames^GuideListName")){
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					
					DBSTAR_RESSTR_S resstr_s;
					memset(&resstr_s, 0, sizeof(resstr_s));
					
					strncpy(resstr_s.ObjectName, OBJ_PRODUCTDESC, sizeof(resstr_s.ObjectName)-1);
					if(strlen(p->ProductDescID)>0)
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s", p->ProductDescID);
					else
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s%s", OBJID_PAUSE, OBJ_PRODUCTDESC);
					strncpy(resstr_s.StrName, "GuideListName", sizeof(resstr_s.StrName)-1);
					
					parseProperty(cur, new_xmlroute, (void *)(&resstr_s));
					
					resstr_insert(&resstr_s);
					if(resstr_s.StrValue)
						free(resstr_s.StrValue);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceiveGuideList^TotalSize")){
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					
					szKey = xmlNodeGetContent(cur);
					strncpy(p->TotalSize, (char *)szKey, sizeof(p->TotalSize)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceiveGuideList^GuideListURI")){
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					
					szKey = xmlNodeGetContent(cur);
					strncpy(p->URI, (char *)szKey, sizeof(p->URI)-1);
					xmlFree(szKey);
				}
				
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceivePreview")){
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					memset(ptr, 0, sizeof(DBSTAR_PRODUCTDESC_S));
					parseProperty(cur, new_xmlroute, ptr);
					
					if(0==strcmp(p->serviceID, serviceID_get())){
						strncpy(p->ReceiveType, "ReceivePreview", sizeof(p->ReceiveType)-1);
						snprintf(p->ProductDescID, sizeof(p->ProductDescID), "GuidePreview_%s", time_serial());
						//productdesc_history_clear(p);
						parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
						productdesc_insert((DBSTAR_PRODUCTDESC_S *)ptr);
					}
					else
						DEBUG("ignore invalid serviceID: %s\n", p->serviceID);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceivePreview^PreviewNames")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceivePreview^PreviewNames^PreviewName")){
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					
					DBSTAR_RESSTR_S resstr_s;
					memset(&resstr_s, 0, sizeof(resstr_s));
					
					strncpy(resstr_s.ObjectName, OBJ_PRODUCTDESC, sizeof(resstr_s.ObjectName)-1);
					if(strlen(p->ProductDescID)>0)
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s", p->ProductDescID);
					else
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s%s", OBJID_PAUSE, OBJ_PRODUCTDESC);
					strncpy(resstr_s.StrName, "PreviewName", sizeof(resstr_s.StrName)-1);
					
					parseProperty(cur, new_xmlroute, (void *)(&resstr_s));
					
					resstr_insert(&resstr_s);
					if(resstr_s.StrValue)
						free(resstr_s.StrValue);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceivePreview^TotalSize")){
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					
					szKey = xmlNodeGetContent(cur);
					strncpy(p->TotalSize, (char *)szKey, sizeof(p->TotalSize)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceivePreview^PreviewURI")){
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					
					szKey = xmlNodeGetContent(cur);
					strncpy(p->URI, (char *)szKey, sizeof(p->URI)-1);
					xmlFree(szKey);
				}
			}
// PublicationsColumn.xml
			else if(0==strncmp(new_xmlroute, "PublicationsColumn^", strlen("PublicationsColumn^"))){
				if(0==strcmp(new_xmlroute, "PublicationsColumn^Navigation")){
					DBSTAR_NAVIGATION_S navigation_s;
					memset(&navigation_s, 0, sizeof(navigation_s));
					parseProperty(cur, new_xmlroute, (void *)(&navigation_s));
					if(0==strcmp(navigation_s.serviceID, serviceID_get()) && NAVIGATIONTYPE_COLUMN==atoi(navigation_s.navigationType))
						parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "PublicationsColumn^Navigation^Publications")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "PublicationsColumn^Navigation^Publications^PublicationColumn")){
					DBSTAR_COLUMNENTITY_S column_entity_s;
					memset(&column_entity_s, 0, sizeof(column_entity_s)); 
					parseProperty(cur, new_xmlroute, (void *)(&column_entity_s));
					parseNode(doc, cur, new_xmlroute, (void *)(&column_entity_s), NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "PublicationsColumn^Navigation^Publications^PublicationColumn^Column")){
					DBSTAR_COLUMNENTITY_S *p = (DBSTAR_COLUMNENTITY_S *)ptr;
					memset(p->columnID, 0, sizeof(p->columnID));
					parseProperty(cur, new_xmlroute, ptr);
					column_entity_insert(p, "Publication");
				}
				else if(0==strcmp(new_xmlroute, "PublicationsColumn^Navigation^Publications^SetColumn")){
					DBSTAR_COLUMNENTITY_S column_entity_s;
					memset(&column_entity_s, 0, sizeof(column_entity_s)); 
					parseProperty(cur, new_xmlroute, (void *)(&column_entity_s));
					parseNode(doc, cur, new_xmlroute, (void *)(&column_entity_s), NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "PublicationsColumn^Navigation^Publications^SetColumn^Column")){
					DBSTAR_COLUMNENTITY_S *p = (DBSTAR_COLUMNENTITY_S *)ptr;
					
					memset(p->columnID, 0, sizeof(p->columnID));
					parseProperty(cur, new_xmlroute, ptr);
					column_entity_insert(p, "Set");
				}
				else if(0==strcmp(new_xmlroute, "PublicationsColumn^Navigation^Publications^ProductColumn")){
					DBSTAR_COLUMNENTITY_S column_entity_s;
					memset(&column_entity_s, 0, sizeof(column_entity_s)); 
					parseProperty(cur, new_xmlroute, (void *)(&column_entity_s));
					parseNode(doc, cur, new_xmlroute, (void *)(&column_entity_s), NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "PublicationsColumn^Navigation^Publications^ProductColumn^Column")){
					DBSTAR_COLUMNENTITY_S *p = (DBSTAR_COLUMNENTITY_S *)ptr;
					
					memset(p->columnID, 0, sizeof(p->columnID));
					parseProperty(cur, new_xmlroute, ptr);
					column_entity_insert(p, "Product");
				}
			}
			
// Message.xml
			else if(0==strncmp(new_xmlroute, "Messages^", strlen("Messages^"))){
				if(0==strcmp(new_xmlroute, "Messages^Message")){
					DBSTAR_MESSAGE_S message_s;
					memset(&message_s, 0, sizeof(message_s));
					
					snprintf(message_s.MessageID, sizeof(message_s.MessageID), "Msg_%s", time_serial());
					parseProperty(cur, new_xmlroute, (void *)(&message_s));
					parseNode(doc, cur, new_xmlroute, (void *)(&message_s), NULL, NULL, NULL, NULL);
					message_insert(&message_s);
				}
				else if(0==strcmp(new_xmlroute, "Messages^Message^Content"))
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
				else if(0==strcmp(new_xmlroute, "Messages^Message^Content^SubContent")){
					DBSTAR_MESSAGE_S *p = (DBSTAR_MESSAGE_S *)ptr;
					
					DBSTAR_RESSTR_S resstr_s;
					memset(&resstr_s, 0, sizeof(resstr_s));
					
					strncpy(resstr_s.ObjectName, OBJ_MESSAGE, sizeof(resstr_s.ObjectName)-1);
					if(strlen(p->MessageID)>0)
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s", p->MessageID);
					else
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s%s", OBJID_PAUSE, OBJ_MESSAGE);
					strncpy(resstr_s.StrName, "SubContent", sizeof(resstr_s.StrName)-1);
					
					parseProperty(cur, new_xmlroute, (void *)(&resstr_s));
					
					resstr_insert(&resstr_s);
					if(resstr_s.StrValue)
						free(resstr_s.StrValue);
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
			
// Preview.xml
			else if(0==strncmp(new_xmlroute, "Preview^", strlen("Preview^"))){
				if(0==strcmp(new_xmlroute, "Preview^PreviewID")){
					DBSTAR_PREVIEW_S *p = (DBSTAR_PREVIEW_S *)ptr;
					
					szKey = xmlNodeGetContent(cur);
					strncpy(p->PreviewID, (char *)szKey, sizeof(p->PreviewID)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Preview^PreviewNames"))
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
				else if(0==strcmp(new_xmlroute, "Preview^PreviewNames^PreviewName")){
					DBSTAR_PREVIEW_S *p = (DBSTAR_PREVIEW_S *)ptr;
					
					DBSTAR_RESSTR_S resstr_s;
					memset(&resstr_s, 0, sizeof(resstr_s));
					
					strncpy(resstr_s.ObjectName, OBJ_PREVIEW, sizeof(resstr_s.ObjectName)-1);
					if(strlen(p->PreviewID)>0)
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s", p->PreviewID);
					else
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s%s", OBJID_PAUSE, OBJ_PREVIEW);
					strncpy(resstr_s.StrName, "SubContent", sizeof(resstr_s.StrName)-1);
					
					parseProperty(cur, new_xmlroute, (void *)(&resstr_s));
					
					resstr_insert(&resstr_s);
					if(resstr_s.StrValue)
						free(resstr_s.StrValue);
				}
				else if(0==strcmp(new_xmlroute, "Preview^PreviewType")){
					DBSTAR_PREVIEW_S *p = (DBSTAR_PREVIEW_S *)ptr;
					
					szKey = xmlNodeGetContent(cur);
					strncpy(p->PreviewType, (char *)szKey, sizeof(p->PreviewType)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Preview^PreviewSize")){
					DBSTAR_PREVIEW_S *p = (DBSTAR_PREVIEW_S *)ptr;
					
					szKey = xmlNodeGetContent(cur);
					strncpy(p->PreviewSize, (char *)szKey, sizeof(p->PreviewSize)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Preview^ShowTime")){
					DBSTAR_PREVIEW_S *p = (DBSTAR_PREVIEW_S *)ptr;
					
					szKey = xmlNodeGetContent(cur);
					strncpy(p->ShowTime, (char *)szKey, sizeof(p->ShowTime)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Preview^Duration")){
					DBSTAR_PREVIEW_S *p = (DBSTAR_PREVIEW_S *)ptr;
					
					szKey = xmlNodeGetContent(cur);
					strncpy(p->Duration, (char *)szKey, sizeof(p->Duration)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Preview^PreviewURI")){
					DBSTAR_PREVIEW_S *p = (DBSTAR_PREVIEW_S *)ptr;
					
					szKey = xmlNodeGetContent(cur);
					strncpy(p->PreviewURI, (char *)szKey, sizeof(p->PreviewURI)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Preview^Resolution")){
					DBSTAR_PREVIEW_S *p = (DBSTAR_PREVIEW_S *)ptr;
					
					szKey = xmlNodeGetContent(cur);
					strncpy(p->Resolution, (char *)szKey, sizeof(p->Resolution)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Preview^BitRate")){
					DBSTAR_PREVIEW_S *p = (DBSTAR_PREVIEW_S *)ptr;
					
					szKey = xmlNodeGetContent(cur);
					strncpy(p->BitRate, (char *)szKey, sizeof(p->BitRate)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Preview^PreviewFormat")){
					DBSTAR_PREVIEW_S *p = (DBSTAR_PREVIEW_S *)ptr;
					
					szKey = xmlNodeGetContent(cur);
					strncpy(p->PreviewFormat, (char *)szKey, sizeof(p->PreviewFormat)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Preview^CodeFormat")){
					DBSTAR_PREVIEW_S *p = (DBSTAR_PREVIEW_S *)ptr;
					
					szKey = xmlNodeGetContent(cur);
					strncpy(p->CodeFormat, (char *)szKey, sizeof(p->CodeFormat)-1);
					xmlFree(szKey);
				}
			}
			
			
			
	//		else
	//			DEBUG("can NOT process such element '%s' in xml route '%s'\n", cur->name, xmlroute);
		}
		
		if(XML_EXIT_NORMALLY==process_over)
			cur = cur->next;
		else{
			DEBUG("process over advance, because have the valid child-tree already\n");
			break;
		}
	}
	//DEBUG("return from %s\n", xmlroute);
	
	if(XML_EXIT_NORMALLY==process_over || XML_EXIT_MOVEUP==process_over)
		return 0;
	else
		return -1;
}

static int parseDoc(char *docname, PUSH_XML_FLAG_E xml_flag)
{
	xmlDocPtr doc;
	xmlNodePtr cur;

//	if(NULL==docname){
//		DEBUG("CAUTION: name of xml file is NULL\n");
//		return -1;
//	}
	
	char xml_uri[512];
	memset(xml_uri, 0, sizeof(xml_uri));
	if(NULL==docname){
		if(-1==xmluri_get(xml_flag, xml_uri, sizeof(xml_uri))){
			DEBUG("can not get valid xml uri to parse\n");
			return -1;
		}
	}
	else
		strncpy(xml_uri, docname, sizeof(xml_uri)-1);
	
	DEBUG("parse xml file[%d]: %s\n", xml_flag, xml_uri);
	
	doc = xmlParseFile(xml_uri);
	if (doc == NULL ) {
		ERROROUT("parse failed: %s\n", xml_uri);
		return -1;
	}
	
	int ret = 0;

	cur = xmlDocGetRootElement(doc);
	if (cur == NULL) {
		ERROROUT("empty document\n");
		ret = -1;
	}
	else{
		DBSTAR_XMLINFO_S xmlinfo;
		memset(&xmlinfo, 0, sizeof(xmlinfo));
		
		char xml_ver[64];
		memset(xml_ver, 0, sizeof(xml_ver));
		xmlver_get(xml_flag, xml_ver, sizeof(xml_ver));
		
		char *p_slash = strrchr(xml_uri, '/');
		if(p_slash)
			snprintf(xmlinfo.XMLName, sizeof(xmlinfo.XMLName), "%s", p_slash+1);
		else
			snprintf(xmlinfo.XMLName, sizeof(xmlinfo.XMLName), "%s", xml_uri);
		
		sqlite_transaction_begin();
// Initialize.xml
		if(0==xmlStrcmp(cur->name, BAD_CAST"Initialize")){
			ret = parseNode(doc, cur, "Initialize", NULL, &xmlinfo, "Initialize", NULL, xml_ver);
		}
// Channels.xml
		else if(0==xmlStrcmp(cur->name, BAD_CAST"Channels")){
			ret = parseNode(doc, cur, "Channels", NULL, &xmlinfo, "Channels", NULL, xml_ver);
		}
// Service.xml
		else if(0==xmlStrcmp(cur->name, BAD_CAST"ServiceGroup")){
			ret = parseNode(doc, cur, "ServiceGroup", NULL, &xmlinfo, "ServiceGroup", NULL, xml_ver);
		}
// Product.xml
		else if(0==xmlStrcmp(cur->name, BAD_CAST"Product")){
			DBSTAR_PRODUCT_S product_s;
			memset(&product_s, 0, sizeof(product_s));
					
			ret = parseNode(doc, cur, "Product", &product_s, &xmlinfo, "Product", NULL, xml_ver);
			product_insert(&product_s);
		}
// PublicationsSets.xml
		else if(0==xmlStrcmp(cur->name, BAD_CAST"PublicationsSets")){
			ret = parseNode(doc, cur, "PublicationsSets", NULL, &xmlinfo, "PublicationsSets", NULL, xml_ver);
		}
// Publication.xml
		else if(0==xmlStrcmp(cur->name, BAD_CAST"Publication")){
			DBSTAR_PUBLICATION_S publication_s;
			memset(&publication_s, 0, sizeof(publication_s));
			ret = parseNode(doc, cur, "Publication", (void *)&publication_s, &xmlinfo, "Publication", NULL, xml_ver);
			publication_insert(&publication_s);
		}
// Column.xml
		else if(0==xmlStrcmp(cur->name, BAD_CAST"Columns")){
			DBSTAR_COLUMN_S column_s;
			ret = parseNode(doc, cur, "Columns", &column_s, &xmlinfo, "Columns", NULL, xml_ver);
		}
// GuideList.xml
		else if(0==xmlStrcmp(cur->name, BAD_CAST"GuideList")){
			DBSTAR_GUIDELIST_S guidelist_s;
			ret = parseNode(doc, cur, "GuideList", &guidelist_s, &xmlinfo, "GuideList", NULL, xml_ver);
		}
// ProductDesc.xml 当前投递单
		else if(0==xmlStrcmp(cur->name, BAD_CAST"ProductDesc")){
			DBSTAR_PRODUCTDESC_S productdesc_s;
			ret = parseNode(doc, cur, "ProductDesc", &productdesc_s, &xmlinfo, "ProductDesc", NULL, xml_ver);
		}
// PublicationsColumn.xml
		else if(0==xmlStrcmp(cur->name, BAD_CAST"PublicationsColumn")){
			ret = parseNode(doc, cur, "PublicationsColumn", NULL, &xmlinfo, "PublicationsColumn", NULL, xml_ver);
		}
		
		
// Message.xml
		else if(0==xmlStrcmp(cur->name, BAD_CAST"Messages")){
			ret = parseNode(doc, cur, "Messages", NULL, &xmlinfo, "Messages", NULL, xml_ver);
		}
// Preview.xml
		else if(0==xmlStrcmp(cur->name, BAD_CAST"Preview")){
			DBSTAR_PREVIEW_S preview_s;
			memset(&preview_s, 0, sizeof(preview_s));
			ret = parseNode(doc, cur, "Preview", &preview_s, &xmlinfo, "Preview", NULL, xml_ver);
			preview_insert(&preview_s);
		}
		
		else{
			ERROROUT("xml file has wrong root node with '%s'\n", cur->name);
			ret = -1;
		}
		
		if(0==ret && strcmp(xml_ver, xmlinfo.Version)){
			if(INITIALIZE_XML==xml_flag){
				snprintf(xmlinfo.PushFlag, sizeof(xmlinfo.PushFlag), "%d", xml_flag);
				snprintf(xmlinfo.XMLName, sizeof(xmlinfo.XMLName), "Initialize");
				snprintf(xmlinfo.URI, sizeof(xmlinfo.URI), "%s", xml_uri);
			}
			
			xmlinfo_insert(&xmlinfo);
		}
		
		if(-1==ret)
			sqlite_transaction_end(0);
		else if(0==ret)
			sqlite_transaction_end(1);
	}
	
	xmlFreeDoc(doc);
	return ret;
}

/*
 如果xml依赖于serviceID才能解析，则返回1，否则返回0
*/
static int depent_on_serviceID(PUSH_XML_FLAG_E xml_flag)
{
	if(	CHANNEL_XML==xml_flag
		|| COLUMN_XML==xml_flag
		|| SERVICE_XML==xml_flag
		|| PRODUCTDESC_XML==xml_flag
		|| PUBLICATIONSCOLUMN_XML==xml_flag )
		return 1;
	else
		return 0;
}

/*
 允许xml_name是空，但是必须有xml_flag。如果xml_uri不为空，则直接使用此uri
 此函数需要独占调用，因为如果当前解析的是Initialize.xml的话，解析完毕后还要自动扫描解析那些依赖于serviceID的xml。
 但同时，push系统的回调也有可能刚好得到这些xml而引起解析。
*/
int parse_xml(char *xml_uri, PUSH_XML_FLAG_E xml_flag)
{
	/*
	 如果还未获得serviceID，则那些依赖于serviceID进行判断的xml不能解析
	*/
	if(0==strlen(serviceID_get()) && depent_on_serviceID(xml_flag)){
		DEBUG("has no serviceID already, waiting please. xml: %s\n", xml_uri);
		return -1;
	}
	
	int ret = parseDoc(xml_uri, xml_flag);
	
	/*
	如果是解析了初始化文件Initialize.xml，则陆续解析配套的Channel.xml等文件。这些配套的文件不一定存在。
	实际使用中，这里的判断应当是根据PUSH回调出来的Initialize.xml对应的Flag，此Flag才是唯一的标识。
	*/
	if(0==ret && INITIALIZE_XML==xml_flag){
		parseDoc(NULL, CHANNEL_XML);
		parseDoc(NULL, COLUMN_XML);
		parseDoc(NULL, PRODUCTDESC_XML);
		parseDoc(NULL, PUBLICATIONSCOLUMN_XML);
	}
	
	return ret;
}
