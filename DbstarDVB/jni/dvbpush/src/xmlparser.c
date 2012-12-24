#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <errno.h>
#include <time.h>
#include <libxml/xmlmemory.h>
#include <libxml/parser.h>
#include <pthread.h>

#include "common.h"
#include "xmlparser.h"
#include "sqlite.h"
#include "mid_push.h"
#include "multicast.h"
#include "softdmx.h"
#include "porting.h"
#include "dvbpush_api.h"

static int global_insert(DBSTAR_GLOBAL_S *p);
static pthread_mutex_t mtx_parse_xml = PTHREAD_MUTEX_INITIALIZER;
static int s_column_SequenceNum = 0;


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
	snprintf(sqlite_cmd,sizeof(sqlite_cmd),"SELECT URI FROM Initialize WHERE PushFlag='%d';", pushflag);

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
	if(NULL==p){
		DEBUG("invalid arguments\n");
		return -1;
	}
	
	if(0==strcmp("chi",p->StrLang)){
		DEBUG("shit, here should be 'cho', not 'chi'\n");
		snprintf(p->StrLang,sizeof(p->StrLang),"%s",CURLANGUAGE_DFT);
	}
	
	char sqlite_cmd[2048];
	
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ServiceID,ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s','%s');",
		p->ServiceID, p->ObjectName, p->EntityID, p->StrLang, p->StrName, p->StrValue, p->Extension);
	return sqlite_transaction_exec(sqlite_cmd);
}

/*
 向初始化表Initialize插入xml文件的信息，既有可能是解析Initialize.xml时插入，也有可能是解析每个xml时插入版本信息
*/
static int xmlinfo_insert(DBSTAR_XMLINFO_S *xmlinfo)
{
	if(NULL==xmlinfo)
		return -1;
	
	DEBUG("%s,%s,%s,%s,%s,%s,%s\n", xmlinfo->PushFlag, xmlinfo->ServiceID, xmlinfo->XMLName, xmlinfo->Version, xmlinfo->StandardVersion, xmlinfo->URI, xmlinfo->ID);
	if(strlen(xmlinfo->Version)>0 || strlen(xmlinfo->StandardVersion)>0 || strlen(xmlinfo->URI)>0){
		char sqlite_cmd[512];
		
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Initialize(PushFlag,ServiceID,ID) VALUES('%s','%s','%s');", xmlinfo->PushFlag, xmlinfo->ServiceID, xmlinfo->ID);
		sqlite_transaction_exec(sqlite_cmd);
		
		if(strlen(xmlinfo->XMLName)>0){
			if(PRODUCTION_XML==strtol(xmlinfo->PushFlag,NULL,0))
				snprintf(sqlite_cmd, sizeof(sqlite_cmd), "UPDATE Initialize SET XMLName='%s' WHERE PushFlag='%s' AND ID='%s';", xmlinfo->XMLName, xmlinfo->PushFlag, xmlinfo->ID);
			else
				snprintf(sqlite_cmd, sizeof(sqlite_cmd), "UPDATE Initialize SET XMLName='%s' WHERE PushFlag='%s';", xmlinfo->XMLName, xmlinfo->PushFlag);
			sqlite_transaction_exec(sqlite_cmd);
		}
		if(strlen(xmlinfo->Version)>0){
			if(PRODUCTION_XML==strtol(xmlinfo->PushFlag,NULL,0))
				snprintf(sqlite_cmd, sizeof(sqlite_cmd), "UPDATE Initialize SET Version='%s' WHERE PushFlag='%s' AND ID='%s';", xmlinfo->Version, xmlinfo->PushFlag, xmlinfo->ID);
			else
				snprintf(sqlite_cmd, sizeof(sqlite_cmd), "UPDATE Initialize SET Version='%s' WHERE PushFlag='%s';", xmlinfo->Version, xmlinfo->PushFlag);
			sqlite_transaction_exec(sqlite_cmd);
		}
		if(strlen(xmlinfo->StandardVersion)>0){
			if(PRODUCTION_XML==strtol(xmlinfo->PushFlag,NULL,0))
				snprintf(sqlite_cmd, sizeof(sqlite_cmd), "UPDATE Initialize SET StandardVersion='%s' WHERE PushFlag='%s' AND ID='%s';", xmlinfo->StandardVersion, xmlinfo->PushFlag, xmlinfo->ID);
			else
				snprintf(sqlite_cmd, sizeof(sqlite_cmd), "UPDATE Initialize SET StandardVersion='%s' WHERE PushFlag='%s';", xmlinfo->StandardVersion, xmlinfo->PushFlag);
			sqlite_transaction_exec(sqlite_cmd);
		}
		if(strlen(xmlinfo->URI)>0){
			if(PRODUCTION_XML==strtol(xmlinfo->PushFlag,NULL,0))
				snprintf(sqlite_cmd, sizeof(sqlite_cmd), "UPDATE Initialize SET URI='%s' WHERE PushFlag='%s' AND ID='%s';", xmlinfo->URI, xmlinfo->PushFlag, xmlinfo->ID);
			else
				snprintf(sqlite_cmd, sizeof(sqlite_cmd), "UPDATE Initialize SET URI='%s' WHERE PushFlag='%s';", xmlinfo->URI, xmlinfo->PushFlag);
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
	
	char sqlite_cmd[512];
	
	SERVICE_STATUS_E service_status = SERVICE_STATUS_INVALID;
	if(0==strcmp(serviceID_get(),p->ServiceID)){
		DEBUG("service id %s is mine\n", p->ServiceID);
		service_status = SERVICE_STATUS_EFFECT;
	}
	else{
		DEBUG("service id %s is not mine(%s)\n", p->ServiceID, serviceID_get());
		service_status = SERVICE_STATUS_INVALID;
	}
	
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Service(ServiceID,RegionCode,OnlineTime,OfflineTime,Status) VALUES('%s','%s','%s','%s','%d');",
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
	
	char sqlite_cmd[512];
	
	if(0==strcmp(serviceID_get(),p->ServiceID)){
		DEBUG("product %s in service %s is mine, receive its publications\n", p->ProductID,p->ServiceID);
		snprintf(sqlite_cmd,sizeof(sqlite_cmd),"UPDATE ProductDesc SET ReceiveStatus='%d',FreshFlag=1 where productID='%s' AND ReceiveStatus='%d';",RECEIVESTATUS_WAITING,p->ProductID,RECEIVESTATUS_REJECT);
		sqlite_transaction_exec(sqlite_cmd);
	}
	
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Product(ServiceID,ProductID,ProductType,Flag,OnlineDate,OfflineDate,IsReserved,Price,CurrencyType) VALUES('%s','%s','%s','%s','%s','%s','%s','%s','%s');",
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
	
	if(0==strlen(ptr->ParentID))
		snprintf(ptr->ParentID, sizeof(ptr->ParentID), "-1");
	
	s_column_SequenceNum++;
	
	char *p_slash = strrchr(ptr->ColumnIcon_losefocus,'/');
	if(p_slash)
		p_slash++;
	else
		p_slash = ptr->ColumnIcon_losefocus;
	
	char from_file[256];
	char to_file[256];
	snprintf(from_file,sizeof(from_file),"%s/%s", push_dir_get(),ptr->ColumnIcon_losefocus);
	snprintf(to_file,sizeof(to_file),"%s/%s",column_res_get(),p_slash);
	if(0==fcopy_c(from_file,to_file)){
		char cmd[2048];
		snprintf(cmd, sizeof(cmd), "REPLACE INTO Column(ServiceID,ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,ColumnIcon_spare,SequenceNum) VALUES('%s','%s','%s','%s','%s','%s','%s','%s','%s',%d);",
			ptr->ServiceID,ptr->ColumnID,ptr->ParentID,ptr->Path,ptr->ColumnType,p_slash,ptr->ColumnIcon_getfocus,ptr->ColumnIcon_onclick,ptr->ColumnIcon_losefocus,s_column_SequenceNum);
		
		return sqlite_transaction_exec(cmd);
	}
	else{
		DEBUG("copy %s to %s failed\n",from_file,to_file);
		return -1;
	}
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
	char sqlite_cmd[512];
	snprintf(sqlite_cmd,sizeof(sqlite_cmd),"SELECT PublicationID FROM GuideList WHERE PublicationID='%s';",ptr->PublicationID);
	if(0<sqlite_transaction_read(sqlite_cmd,NULL,0)){
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO GuideList(ServiceID,DateValue,GuideListID,productID,PublicationID,UserStatus) VALUES('%s','%s','%s','%s','%s',(select UserStatus from GuideList where PublicationID='%s'));",
			ptr->ServiceID,ptr->DateValue,ptr->GuideListID,ptr->productID,ptr->PublicationID,ptr->PublicationID);
		return sqlite_transaction_exec(sqlite_cmd);
	}
	else{
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO GuideList(ServiceID,DateValue,GuideListID,productID,PublicationID,UserStatus) VALUES('%s','%s','%s','%s','%s','1');",
			ptr->ServiceID,ptr->DateValue,ptr->GuideListID,ptr->productID,ptr->PublicationID);
		return sqlite_transaction_exec(sqlite_cmd);
	}
}


int check_productid_from_db_in_trans(char *productid)
{
	char read_productid[64];
	memset(read_productid, 0, sizeof(read_productid));
	char sqlite_cmd[512];
	snprintf(sqlite_cmd,sizeof(sqlite_cmd),"select ProductID from Product where ProductID='%s';",productid);
	if(0<sqlite_transaction_read(sqlite_cmd,read_productid,sizeof(read_productid))){
		DEBUG("check ServiceID %s OK\n", productid);
		return 0;
	}
	else{
		DEBUG("check ServiceID %s failed\n", productid);
		return -1;
	}
}

/*
 播发单ProductDesc.xml，成品Publication和成品集PublicationsSet直接存放到对应表中，而小片Preview和预告单GuideList则存放在播发单表ProductDesc中
 如果先解析ProductDesc后解析Service，则有可能本应接收的Product被拒绝。
*/
static int productdesc_insert(DBSTAR_PRODUCTDESC_S *ptr)
{
	if(NULL==ptr){
		DEBUG("invalid args\n");
		return -1;
	}
	
	char sqlite_cmd[2048];

#if 0
	if(strlen(ptr->PushStartTime)>0){
		snprintf(sqlite_cmd,sizeof(sqlite_cmd),"UPDATE ProductDesc SET PushStartTime='%s' WHERE ServiceID='%s' AND PushStartTime='';", \
ptr->PushStartTime,
ptr->ServiceID);
		sqlite_transaction_exec(sqlite_cmd);
	}
	
	if(strlen(ptr->PushEndTime)>0){
		snprintf(sqlite_cmd,sizeof(sqlite_cmd),"UPDATE ProductDesc SET PushEndTime='%s' WHERE ServiceID='%s' AND PushEndTime='';", \
ptr->PushEndTime,
ptr->ServiceID);
		sqlite_transaction_exec(sqlite_cmd);
	}
#endif
	
	/*
	还需要检查用户在选择接收界面的反选
	*/
	
	RECEIVESTATUS_E receive_status = RECEIVESTATUS_REJECT;	// make sure the default value is -2, it means reject
	if(0==strcmp(ptr->ServiceID,serviceID_get())){
		if(	RECEIVETYPE_SPRODUCT==strtol(ptr->ReceiveType,NULL,10)
			|| RECEIVETYPE_COLUMN==strtol(ptr->ReceiveType,NULL,10)
			|| (RECEIVETYPE_PUBLICATION==strtol(ptr->ReceiveType,NULL,10) && (0==check_productid_from_smartcard(ptr->productID) || 0==check_productid_from_db_in_trans(ptr->productID))) )
			receive_status = RECEIVESTATUS_WAITING;
	}
	
	DEBUG("I will %s this program(%s), serviceID:%s, ProductID:%s\n", 0==receive_status?"receive":"reject",ptr->ID,ptr->ServiceID,ptr->productID);
	
	/*
	理论上，对于处在不同Service的Publication，如果需要拒绝接收，但其PublicationID已经存在于表中，则不需要再次入库；这意味着只有那些允许接收的Publication以及纯粹拒绝接收的Publication可以入库。
	但是考虑到ProductDesc.xml和Service.xml到来的顺序不一定，有可能Service.xml到来的比较晚，因此这里忠实的体现所有Service——publicaiton组合。
	对于那些相同PublicatonID既有允许接收，又有拒绝接收的冲突问题，放在注册时处理。
	*/
	
	snprintf(sqlite_cmd,sizeof(sqlite_cmd),"REPLACE INTO ProductDesc(ServiceID,ReceiveType,ProductDescID,rootPath,productID,SetID,ID,TotalSize,URI,DescURI,PushStartTime,PushEndTime,Columns,ReceiveStatus,FreshFlag) \
VALUES('%s',\
'%s',\
'%s',\
'%s',\
'%s',\
'%s',\
'%s',\
'%s',\
'%s',\
'%s',\
'%s',\
'%s',\
'%s',\
'%d',\
1);",
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
	
	if(RECEIVETYPE_PUBLICATION==strtol(ptr->ReceiveType,NULL,10) && RECEIVESTATUS_WAITING==receive_status){
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
				snprintf(sqlite_cmd,sizeof(sqlite_cmd),"REPLACE INTO PublicationsSet(ServiceID,ColumnID,ProductID,PushStartTime,PushEndTime,ReceiveStatus,SetID) \
VALUES('%s',\
'%s',\
'%s',\
'%s',\
'%s',\
'%d',\
'%s');",
ptr->ServiceID,
p_column,
ptr->productID,
ptr->PushStartTime,
ptr->PushEndTime,
receive_status,
ptr->SetID);
		
				sqlite_transaction_exec(sqlite_cmd);
			}
			else{
				snprintf(sqlite_cmd,sizeof(sqlite_cmd),"REPLACE INTO Publication(ServiceID,PublicationID,ColumnID,ProductID,URI,DescURI,TotalSize,ProductDescID,PushStartTime,PushEndTime,ReceiveStatus,SetID) \
VALUES('%s',\
'%s',\
'%s',\
'%s',\
'%s',\
'%s',\
'%s',\
'%s',\
'%s',\
'%s',\
'%d',\
'%s');",
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
			snprintf(sqlite_cmd,sizeof(sqlite_cmd),"REPLACE INTO Publication(ServiceID,PublicationID,ColumnID,ProductID,URI,DescURI,TotalSize,ProductDescID,PushStartTime,PushEndTime,ReceiveStatus,SetID) \
VALUES('%s',\
'%s',\
'%s',\
'%s',\
'%s',\
'%s',\
'%s',\
'%s',\
'%s',\
'%s',\
'%d',\
'%s');",
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
	
//	char	Version[64];
//	char	StandardVersion[64];
//	char	ServiceID[64];
//	char	ReceiveType[64];
//	char	rootPath[256];
//	char	productID[64];

//	char	ProductDescID[128];
//	char	SetID[64];
//	char	ID[64];
//	char	TotalSize[64];
//	char	URI[256];
//	char	DescURI[384];
//	char	PushStartTime[64];
//	char	PushEndTime[64];
//	char	Columns[512];
//	char	ReceiveStatus[32];
	
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
 只在Previews表中插入PreviewsID和ProductID之间的对应关系
*/
static int preview_insert_productid(char *PreviewID, char *ProductID)
{
	if(NULL==PreviewID || NULL==ProductID || 0==strlen(PreviewID) || 0==strlen(ProductID)){
		DEBUG("invalid args\n");
		return -1;
	}
	
	char sqlite_cmd[1024*4];
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Preview(PreviewID,ProductID,PreviewType,PreviewSize,ShowTime,PreviewURI,PreviewFormat,Duration,Resolution,BitRate,CodeFormat,URI,TotalSize,ProductDescID,ReceiveStatus,PushStartTime,PushEndTime,StartTime,EndTime,PlayMode) \
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
(select PushStartTime from Preview where PreviewID='%s'),\
(select PushEndTime from Preview where PreviewID='%s'),\
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
		PreviewID,
		PreviewID);
	return sqlite_transaction_exec(sqlite_cmd);
}


/*
 channel新记录入库时，记FreshFlag为有效。
*/
static int channel_insert(DBSTAR_CHANNEL_S *p)
{
	if(NULL==p || 0>=strlen(p->pid))
		return -1;
	
	char sqlite_cmd[512];
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Channel(pid,ServiceID,pidtype,FreshFlag) VALUES('%s','%s','%s',1);",p->pid,serviceID_get(),p->pidtype);
	return sqlite_transaction_exec(sqlite_cmd);
}

/*
 channel新记录入库前将Channel原有pid记录置为无效。
*/
static int channel_ineffective_set()
{
	char sqlite_cmd[512];
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "UPDATE Channel SET FreshFlag=0,ServiceID='-1';");
	return sqlite_transaction_exec(sqlite_cmd);
}

/*
 清理channel中无效pid。直接执行，不进入数据库。
*/
static int channel_ineffective_clear()
{
	char sqlite_cmd[512];
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "DELETE FROM Channel WHERE FreshFlag=0;");
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
	
	char sqlite_cmd[1024];
	
	snprintf(sqlite_cmd,sizeof(sqlite_cmd),"UPDATE Publication SET PublicationType='%s',IsReserved='%s',Visible='%s',DRMFile='%s',FileID='%s',FileSize='%s',FileURI='%s',FileType='%s',Duration='%s',Resolution='%s',BitRate='%s',FileFormat='%s',CodeFormat='%s',ReceiveStatus='1',TimeStamp=datetime('now','localtime') WHERE PublicationID='%s';",
		p->PublicationType,p->IsReserved,p->Visible,p->DRMFile,p->FileID,p->FileSize,p->FileURI,p->FileType,p->Duration,p->Resolution,p->BitRate,p->FileFormat,p->CodeFormat,p->PublicationID);
	
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
	
	char sqlite_cmd[1024*4];
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO MultipleLanguageInfoVA(ServiceID,PublicationID,infolang,PublicationDesc,ImageDefinition,Keywords,Area,Language,Episode,AspectRatio,AudioChannel,Director,Actor,Audience,Model) VALUES('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s');",
		p->ServiceID,p->PublicationID,p->infolang,p->PublicationDesc,p->ImageDefinition,p->Keywords,p->Area,p->Language,p->Episode,p->AspectRatio,p->AudioChannel,p->Director,p->Actor,p->Audience,p->Model);
	
	return sqlite_transaction_exec(sqlite_cmd);
}

#if 0
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
#endif

static int publicationsset_insert(DBSTAR_PUBLICATIONSSET_S *p)
{
	if(NULL==p){
		DEBUG("invalid NULL arg\n");
		return -1;
	}
	
	char sqlite_cmd[512];

#if 0
	char old_PublicationType[64];	memset(old_PublicationType,0,sizeof(old_PublicationType));
	char old_IsReserved[64];		memset(old_IsReserved,0,sizeof(old_IsReserved));
	char old_Visible[64];			memset(old_Visible,0,sizeof(old_Visible));
	
	snprintf(sqlite_cmd,sizeof(sqlite_cmd),"SELECT PublicationType FROM PublicationsSet WHERE SetID='%s';",xmlinfo->PushFlag,xmlinfo->ServiceID,xmlinfo->ID);
	if(0<sqlite_transaction_read(sqlite_cmd,old_xmlver,old_xmlver_size)){
		DEBUG("read xml old version: %s\n", old_xmlver);
		return 0;
	}
	else{
		DEBUG("read xml old version failed\n");
		return -1;
	}
#endif
	
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "UPDATE PublicationsSet SET PublicationType='%s',IsReserved='%s',Visible='%s',ReceiveStatus='1' WHERE SetID='%s';",
		p->PublicationType,p->IsReserved,p->Visible,p->SetID);
	sqlite_transaction_exec(sqlite_cmd);
	
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO SetInfo(SetID,Title,Starring,Scenario,Classification,Period,CollectionNumber,Review) VALUES('%s','%s','%s','%s','%s','%s','%s','%s');",
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
	
	char sqlite_cmd[512];
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResSubTitle(ServiceID,ObjectName,EntityID,SubTitleID,SubTitleName,SubTitleLanguage,SubTitleURI) VALUES('%s','%s','%s','%s','%s','%s','%s');",
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
	
	char sqlite_cmd[512];
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResPoster(ServiceID,ObjectName,EntityID,PosterID,PosterName,PosterURI) VALUES('%s','%s','%s','%s','%s','%s');",
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
	
	char sqlite_cmd[512];
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResTrailer(ServiceID,ObjectName,EntityID,TrailerID,TrailerName,TrailerURI) VALUES('%s','%s','%s','%s','%s','%s');",
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
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Preview(PreviewID,ProductID,PreviewType,PreviewSize,ShowTime,PreviewURI,PreviewFormat,Duration,Resolution,BitRate,CodeFormat,URI,TotalSize,ProductDescID,ReceiveStatus,PushStartTime,PushEndTime,StartTime,EndTime,PlayMode) \
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
	(select PushStartTime from Preview where PreviewID='%s'),\
	(select PushEndTime from Preview where PreviewID='%s'),\
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
			DEBUG("property of %s, %s: %s\n", xmlroute, attrPtr->name, szAttr);
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
					//snprintf(p->DescURI, sizeof(p->DescURI), "%s/%s", p->rootPath,(char *)szAttr);
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
			
// 集中处理字符串
			else if(	0==strcmp(xmlroute, "Publication^PublicationNames^PublicationName")
					||	0==strcmp(xmlroute, "Service^ServiceNames^ServiceName")
					||	0==strcmp(xmlroute, "Service^Products^Product^ProductNames^ProductName")
					||	0==strcmp(xmlroute, "Service^Products^Product^Descriptions^Description")
					||	0==strcmp(xmlroute, "Product^ProductNames^ProductName")
					||	0==strcmp(xmlroute, "Product^Descriptions^Description")
					||	0==strcmp(xmlroute, "Publication^PublicationNames^PublicationName")
					||	0==strcmp(xmlroute, "Publication^PublicationVA^MFile^FileNames^FileName")
					||	0==strcmp(xmlroute, "Messages^Message^Content^SubContent")
					||	0==strcmp(xmlroute, "Preview^PreviewNames^PreviewName")
					||	0==strcmp(xmlroute, "GuideList^Date^Product^Item^PublicationNames^PublicationName")
					||	0==strcmp(xmlroute, "GuideList^Date^Product^Item^ColumNames^ColumName")
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

#define PROCESS_OVER_CHECK(f) ( (XML_EXIT_NORMALLY==f || XML_EXIT_MOVEUP==f)?0:-1 )

static int read_xmlver_in_trans(DBSTAR_XMLINFO_S *xmlinfo,char *old_xmlver,unsigned int old_xmlver_size)
{
	if(NULL==xmlinfo)
		return -1;
		
	char sqlite_cmd[512];
	snprintf(sqlite_cmd,sizeof(sqlite_cmd),"SELECT Version FROM Initialize WHERE PushFlag='%s' AND ServiceID='%s' AND ID='%s';",xmlinfo->PushFlag,xmlinfo->ServiceID,xmlinfo->ID);
	if(0<sqlite_transaction_read(sqlite_cmd,old_xmlver,old_xmlver_size)){
		DEBUG("read xml old version: %s\n", old_xmlver);
		return 0;
	}
	else{
		DEBUG("read xml old version failed\n");
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
static int parseNode (xmlDocPtr doc, xmlNodePtr cur, char *xmlroute, void *ptr, DBSTAR_XMLINFO_S *xmlinfo, char *rootelement, int *p_child_tree_is_valid, char *old_xmlver)
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
			DEBUG("XML route: %s\n", new_xmlroute);
			
// Initialize.xml
			if(0==strncmp(new_xmlroute, "Initialize^", strlen("Initialize^"))){
				if(0==strcmp(new_xmlroute, "Initialize^ServiceInits")){
					process_over = parseNode(doc, cur, new_xmlroute, NULL, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "Initialize^ServiceInits^ServiceInit")){
					DBSTAR_PRODUCT_SERVICE_S product_service_s;
					memset(&product_service_s, 0, sizeof(product_service_s));
					parseProperty(cur, new_xmlroute, (void *)&product_service_s);
					if(0==special_productid_check(product_service_s.productID)){
						DEBUG("detect valid productID: %s\n", product_service_s.productID);
						DBSTAR_GLOBAL_S global_s;
						memset(&global_s, 0, sizeof(global_s));
						strncpy(global_s.Name, GLB_NAME_SERVICEID, sizeof(global_s.Name)-1);
						strncpy(global_s.Value, product_service_s.serviceID, sizeof(global_s.Value)-1);
						global_insert(&global_s);
						serviceID_set(product_service_s.serviceID);
						
						sqlite_transaction_table_clear("Initialize");
						parseNode(doc, cur, new_xmlroute, NULL, NULL, NULL, NULL, NULL);
						process_over = XML_EXIT_MOVEUP;
						DEBUG("process over moveup on valid serviceID %s\n", serviceID_get());
					}
					else{
						DEBUG("productID %s is invalid\n", product_service_s.productID);
					}
				}
				else if(0==strcmp(new_xmlroute, "Initialize^ServiceInits^ServiceInit^Channels")){
					channel_ineffective_set();
					parseNode(doc, cur, new_xmlroute, NULL, NULL, NULL, NULL, NULL);
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
						if('/'==xmlinfo.URI[strlen(xmlinfo.URI)-1] || 1==check_tail(xmlinfo.URI, ".xml", 0))
							snprintf(xmlinfo.URI+strlen(xmlinfo.URI), sizeof(xmlinfo.URI)-strlen(xmlinfo.URI), "/%s", xmlinfo.XMLName);
						xmlinfo_insert(&xmlinfo);
					}
				}
				else
					DEBUG("can not distinguish such xml route: %s\n", new_xmlroute);
			}
			
// Service.xml
			else if(0==strncmp(new_xmlroute, "Service^", strlen("Service^"))){
				if(0==strcmp(new_xmlroute, "Service^ServiceNames")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
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
					
					parseNode(doc, cur, new_xmlroute, &product_s, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "Service^Products^Product")){
					DBSTAR_PRODUCT_S *p_product = (DBSTAR_PRODUCT_S *)ptr;
					char service_id[64];
					snprintf(service_id,sizeof(service_id),"%s",p_product->ServiceID);
					memset(ptr, 0, sizeof(DBSTAR_PRODUCT_S));
					snprintf(p_product->ServiceID,sizeof(p_product->ServiceID),"%s",service_id);
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
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
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
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
					strncpy(p_product->IsReserved, (char *)szKey, sizeof(p_product->IsReserved)-1);
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
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
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
					if(0==strncasecmp("true", (char *)szKey, strlen("true")))
						snprintf(p->Visible,sizeof(p->Visible),"1");
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
					snprintf(info_va_s.ServiceID, sizeof(info_va_s.ServiceID), "%s", p->ServiceID);
					snprintf(info_va_s.PublicationID, sizeof(info_va_s.PublicationID), "%s", p->PublicationID);
					
					parseProperty(cur, new_xmlroute, (void *)&info_va_s);
					parseNode(doc, cur, new_xmlroute, (void *)&info_va_s, NULL, NULL, NULL, NULL);
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
					
					parseNode(doc, cur, new_xmlroute, &sset_s, NULL, NULL, NULL, NULL);
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
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MFile^FileID")){
					DBSTAR_PUBLICATION_S *p = (DBSTAR_PUBLICATION_S *)ptr;
					szKey = xmlNodeGetContent(cur);
					strncpy(p->FileID, (char *)szKey, sizeof(p->FileID)-1);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MFile^FileNames")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "Publication^PublicationVA^MFile^FileNames^FileName")){
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
			}
			
// Column.xml
			else if(0==strncmp(new_xmlroute, "Columns^", strlen("Columns^"))){
				if(0==strcmp(new_xmlroute, "Columns^Column")){
					DBSTAR_COLUMN_S *p_column = (DBSTAR_COLUMN_S *)ptr;
					char tmp_serviceid[64];
					snprintf(tmp_serviceid,sizeof(tmp_serviceid),"%s",p_column->ServiceID);
					memset(ptr, 0, sizeof(DBSTAR_COLUMN_S));
					snprintf(p_column->ServiceID,sizeof(p_column->ServiceID),"%s",tmp_serviceid);
					
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
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
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
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
				if(0==strcmp(new_xmlroute, "GuideList^Date")){
					DBSTAR_GUIDELIST_S *p_guidelist = (DBSTAR_GUIDELIST_S *)ptr;
					
					memset(p_guidelist->DateValue,0,sizeof(p_guidelist->DateValue));
					memset(p_guidelist->GuideListID,0,sizeof(p_guidelist->GuideListID));
					memset(p_guidelist->productID,0,sizeof(p_guidelist->productID));
					memset(p_guidelist->PublicationID,0,sizeof(p_guidelist->PublicationID));
					
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
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
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "GuideList^Date^Product^Item")){
					/*
					针对每个Item，清理除DataValue之外的其他变量
					*/
					DBSTAR_GUIDELIST_S *p_guidelist = (DBSTAR_GUIDELIST_S *)ptr;
					
					memset(p_guidelist->GuideListID,0,sizeof(p_guidelist->GuideListID));
					memset(p_guidelist->PublicationID,0,sizeof(p_guidelist->PublicationID));
					
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
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
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
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
				else if(0==strcmp(new_xmlroute, "GuideList^Date^Product^Item^ColumNames")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "GuideList^Date^Product^Item^ColumNames^ColumName")){
					DBSTAR_GUIDELIST_S *p_guidelist = (DBSTAR_GUIDELIST_S *)ptr;
					
					DBSTAR_RESSTR_S resstr_s;
					memset(&resstr_s, 0, sizeof(resstr_s));
					
					snprintf(resstr_s.ServiceID,sizeof(resstr_s.ServiceID),"%s",p_guidelist->ServiceID);
					strncpy(resstr_s.ObjectName, OBJ_GUIDELIST, sizeof(resstr_s.ObjectName)-1);
					if(strlen(p_guidelist->GuideListID)>0)
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s", p_guidelist->GuideListID);
					else
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s%s", OBJID_PAUSE, OBJ_GUIDELIST);
					strncpy(resstr_s.StrName, "ColumName", sizeof(resstr_s.StrName)-1);
					
					parseProperty(cur, new_xmlroute, (void *)(&resstr_s));
					
					resstr_insert(&resstr_s);
				}
				else if(0==strcmp(new_xmlroute, "GuideList^Date^Product^Item^PublicationDescs")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "GuideList^Date^Product^Item^PublicationDescs^PublicationDesc")){
					DBSTAR_GUIDELIST_S *p_guidelist = (DBSTAR_GUIDELIST_S *)ptr;
					
					DBSTAR_RESSTR_S resstr_s;
					memset(&resstr_s, 0, sizeof(resstr_s));
					
					snprintf(resstr_s.ServiceID,sizeof(resstr_s.ServiceID),"%s",p_guidelist->ServiceID);
					strncpy(resstr_s.ObjectName, OBJ_GUIDELIST, sizeof(resstr_s.ObjectName)-1);
					if(strlen(p_guidelist->GuideListID)>0)
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s", p_guidelist->GuideListID);
					else
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s%s", OBJID_PAUSE, OBJ_GUIDELIST);
					strncpy(resstr_s.StrName, "PublicationDesc", sizeof(resstr_s.StrName)-1);
					
					parseProperty(cur, new_xmlroute, (void *)(&resstr_s));
					
					resstr_insert(&resstr_s);
				}
 			}


// ProductDesc.xml 当前投递单
			else if(0==strncmp(new_xmlroute, "ProductDesc^", strlen("ProductDesc^"))){
				if(0==strcmp(new_xmlroute, "ProductDesc^ReceivePublications")){
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					productdesc_clear((DBSTAR_PRODUCTDESC_S *)ptr, 1);
					parseProperty(cur, new_xmlroute, ptr);
					snprintf(p->ReceiveType, sizeof(p->ReceiveType), "%d",RECEIVETYPE_PUBLICATION);
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceivePublications^Product")){
					productdesc_clear((DBSTAR_PRODUCTDESC_S *)ptr, 2);
					parseProperty(cur, new_xmlroute, ptr);
					DEBUG("productID: %s\n", ((DBSTAR_PRODUCTDESC_S *)ptr)->productID);
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceivePublications^Product^Publication")){
					productdesc_clear((DBSTAR_PRODUCTDESC_S *)ptr, 3);
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
					productdesc_insert((DBSTAR_PRODUCTDESC_S *)ptr);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceivePublications^Product^Publication^PublicationID")){
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					
					szKey = xmlNodeGetContent(cur);
					strncpy(p->ID, (char *)szKey, sizeof(p->ID)-1);
					snprintf(p->ProductDescID, sizeof(p->ProductDescID),"%s_%s_%s", p->ServiceID,p->ReceiveType,p->ID);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceivePublications^Product^Publication^PublicationNames")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
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
					//signed_char_clear(tmp_URI,sizeof(tmp_URI),'/',3);
					snprintf(p->URI,sizeof(p->URI),"%s",p->rootPath);
					if('/'!=tmp_URI[0])
						snprintf(p->URI+strlen(p->URI),sizeof(p->URI)-strlen(p->URI),"/");
					snprintf(p->URI+strlen(p->URI),sizeof(p->URI)-strlen(p->URI),"%s",tmp_URI);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceivePublications^Product^Publication^Columns")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
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
					snprintf(p->ReceiveType, sizeof(p->ReceiveType), "%d",RECEIVETYPE_SPRODUCT);
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
					productdesc_insert((DBSTAR_PRODUCTDESC_S *)ptr);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceiveSProduct^SProductID")){
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					
					szKey = xmlNodeGetContent(cur);
					strncpy(p->ID, (char *)szKey, sizeof(p->ID)-1);
					snprintf(p->ProductDescID, sizeof(p->ProductDescID),"%s_%s_%s", p->ServiceID,p->ReceiveType,p->ID);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceiveSProduct^SProductNames")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
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
					//signed_char_clear(tmp_URI,sizeof(tmp_URI),'/',3);
					snprintf(p->URI,sizeof(p->URI),"%s",p->rootPath);
					if('/'!=tmp_URI[0])
						snprintf(p->URI+strlen(p->URI),sizeof(p->URI)-strlen(p->URI),"/");
					snprintf(p->URI+strlen(p->URI),sizeof(p->URI)-strlen(p->URI),"%s",tmp_URI);
				}
				
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceiveColumn")){
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					productdesc_clear((DBSTAR_PRODUCTDESC_S *)ptr, 1);
					parseProperty(cur, new_xmlroute, ptr);
					snprintf(p->ReceiveType, sizeof(p->ReceiveType), "%d", RECEIVETYPE_COLUMN);
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
					productdesc_insert((DBSTAR_PRODUCTDESC_S *)ptr);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceiveColumn^ColumnID")){
					DBSTAR_PRODUCTDESC_S *p = (DBSTAR_PRODUCTDESC_S *)ptr;
					
					szKey = xmlNodeGetContent(cur);
					strncpy(p->ID, (char *)szKey, sizeof(p->ID)-1);
					snprintf(p->ProductDescID, sizeof(p->ProductDescID),"%s_%s_%s", p->ServiceID,p->ReceiveType,p->ID);
					xmlFree(szKey);
				}
				else if(0==strcmp(new_xmlroute, "ProductDesc^ReceiveColumn^ColumnNames")){
					parseNode(doc, cur, new_xmlroute, ptr, NULL, NULL, NULL, NULL);
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
					//signed_char_clear(tmp_URI,sizeof(tmp_URI),'/',3);
					snprintf(p->URI,sizeof(p->URI),"%s",p->rootPath);
					if('/'!=tmp_URI[0])
						snprintf(p->URI+strlen(p->URI),sizeof(p->URI)-strlen(p->URI),"/");
					snprintf(p->URI+strlen(p->URI),sizeof(p->URI)-strlen(p->URI),"%s",tmp_URI);
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
					
					snprintf(resstr_s.ServiceID,sizeof(resstr_s.ServiceID),"%s",p->ServiceID);
					strncpy(resstr_s.ObjectName, OBJ_PREVIEW, sizeof(resstr_s.ObjectName)-1);
					if(strlen(p->PreviewID)>0)
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s", p->PreviewID);
					else
						snprintf(resstr_s.EntityID, sizeof(resstr_s.EntityID), "%s%s", OBJID_PAUSE, OBJ_PREVIEW);
					strncpy(resstr_s.StrName, "SubContent", sizeof(resstr_s.StrName)-1);
					
					parseProperty(cur, new_xmlroute, (void *)(&resstr_s));
					
					resstr_insert(&resstr_s);
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
		
		if(XML_EXIT_NORMALLY==process_over || XML_EXIT_UNNECESSARY==process_over)
			cur = cur->next;
		else{	// if(XML_EXIT_MOVEUP==process_over || XML_EXIT_ERROR==process_over)
			DEBUG("process over advance !!!\n");
			break;
		}
	}
	DEBUG("return from %s with %s\n", xmlroute, process_over_str(process_over));
	
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

static int parseDoc(char *docname, PUSH_XML_FLAG_E xml_flag, char *id)
{
	xmlDocPtr doc;
	xmlNodePtr cur;
	int ret = 0;
	char xml_uri[512];
	
	DEBUG("xml_flag: %d\n", xml_flag);
	pthread_mutex_lock(&mtx_parse_xml);
//	if(NULL==docname){
//		DEBUG("CAUTION: name of xml file is NULL\n");
//		ret = -1;
//		goto PARSE_XML_END;
//	}
	
	memset(xml_uri, 0, sizeof(xml_uri));
	if(NULL==docname){
		char tmp_uri[512];
		if(-1==xmluri_get(xml_flag, tmp_uri, sizeof(tmp_uri))){
			DEBUG("can not get valid xml uri to parse\n");
			ret = -1;
			goto PARSE_XML_END;
		}
		else
			snprintf(xml_uri, sizeof(xml_uri), "%s/%s", push_dir_get(), tmp_uri);
	}
	else
		snprintf(xml_uri, sizeof(xml_uri), "%s/%s", push_dir_get(), docname);
	
	DEBUG("parse xml file[%d]: %s\n", xml_flag, xml_uri);
	
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
		snprintf(xmlinfo.PushFlag, sizeof(xmlinfo.PushFlag), "%d", xml_flag);
		
		char sqlite_cmd[256];
		char old_xmlver[64];
		memset(old_xmlver, 0, sizeof(old_xmlver));
		
		if(GUIDELIST_XML==xml_flag){
#if 0	//  不能直接删除所有的记录，应保留用户选择“拒绝接收”的记录。只能删除昨天及以前的记录。
			parseProperty(cur, XML_ROOT_ELEMENT, (void *)&xmlinfo);
#else
			snprintf(sqlite_cmd, sizeof(sqlite_cmd), "DELETE FROM GuideList WHERE DateValue<datetime('now','localtime','start of day');");
			sqlite_execute(sqlite_cmd);
#endif
		}
		
		sqlite_transaction_begin();
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
				ret = parseNode(doc, cur, "Initialize", NULL, &xmlinfo, "Initialize", NULL, old_xmlver);
				
				ret = PROCESS_OVER_CHECK(ret);
				if(0==ret){
					snprintf(xmlinfo.XMLName, sizeof(xmlinfo.XMLName), "Initialize.xml");
					/*
					 Initialize.xml是所有service共用的，不存在单独的serviceID属性，这里只是起到填充作用
					*/
					snprintf(xmlinfo.ServiceID, sizeof(xmlinfo.XMLName), "%s", SERVICEID_FILL);
				}
			}
		}
// Service.xml
		else if(0==xmlStrcmp(cur->name, BAD_CAST"Service")){
			parseProperty(cur, XML_ROOT_ELEMENT, (void *)&xmlinfo);
			read_xmlver_in_trans(&xmlinfo,old_xmlver,sizeof(old_xmlver));
			/*
			 由于需要反注册，所以需要解析所有serviceID的投递内容做反向注册
			 || 0!=strcmp(serviceID_get(), xmlinfo.ServiceID)
			*/
			if((strlen(old_xmlver)>0 && 0==strcmp(old_xmlver, xmlinfo.Version))){
				DEBUG("old ver: %s, new ver: %s, my ServiceID: %s, xml ServiceID: %s, no need to parse\n",\
						old_xmlver, xmlinfo.Version, serviceID_get(), xmlinfo.ServiceID);
				ret = -1;
			}
			else
			{
				/*
				在父节点上定义子节点的结构体，并清空
				*/
				DBSTAR_SERVICE_S service_s;
				memset(&service_s, 0, sizeof(service_s));
				snprintf(service_s.ServiceID, sizeof(service_s.ServiceID), "%s", xmlinfo.ServiceID);
				ret = parseNode(doc, cur, "Service", &service_s, &xmlinfo, "Service", NULL, old_xmlver);
				service_insert(&service_s);
				
				ret = PROCESS_OVER_CHECK(ret);
			}
		}

// Publication.xml
		else if(0==xmlStrcmp(cur->name, BAD_CAST"Publication")){
			parseProperty(cur, XML_ROOT_ELEMENT, (void *)&xmlinfo);
			
			/*
			 针对Publication.xml，在Initialize.xml表中记录的ServiceID复用为其自身的PublicationID
			*/
			snprintf(xmlinfo.ID,sizeof(xmlinfo.ID),"%s",id);
			read_xmlver_in_trans(&xmlinfo,old_xmlver,sizeof(old_xmlver));
			
			/*
			 Publication已经通过接收单进行了业务级别的过滤，这里不需要比对
			 || 0!=strcmp(serviceID_get(), xmlinfo.ServiceID)
			*/
			if((strlen(old_xmlver)>0 && 0==strcmp(old_xmlver, xmlinfo.Version))){
				DEBUG("old ver: %s, new ver: %s, my ServiceID: %s, xml ServiceID: %s, no need to parse\n",\
						old_xmlver, xmlinfo.Version, serviceID_get(), xmlinfo.ServiceID);
				ret = -1;
			}
			else{
				DBSTAR_PUBLICATION_S publication_s;
				memset(&publication_s, 0, sizeof(publication_s));
				snprintf(publication_s.ServiceID,sizeof(publication_s.ServiceID),"%s", xmlinfo.ServiceID);
				ret = parseNode(doc, cur, "Publication", (void *)&publication_s, &xmlinfo, "Publication", NULL, old_xmlver);
				publication_insert(&publication_s);
				
				ret = PROCESS_OVER_CHECK(ret);
			}
		}
// Column.xml
		else if(0==xmlStrcmp(cur->name, BAD_CAST"Columns")){
			parseProperty(cur, XML_ROOT_ELEMENT, (void *)&xmlinfo);
			read_xmlver_in_trans(&xmlinfo,old_xmlver,sizeof(old_xmlver));
			if((strlen(old_xmlver)>0 && 0==strcmp(old_xmlver, xmlinfo.Version)) || 0!=strcmp(serviceID_get(), xmlinfo.ServiceID)){
				DEBUG("old ver: %s, new ver: %s, my ServiceID: %s, xml ServiceID: %s, no need to parse\n",\
						old_xmlver, xmlinfo.Version, serviceID_get(), xmlinfo.ServiceID);
				ret = -1;
			}
			else{
				/*
				 不能一股脑的清理掉Column的所有数据，保留本地菜单
				*/
				snprintf(sqlite_cmd, sizeof(sqlite_cmd), "DELETE FROM Column WHERE ColumnType!='L98' AND ColumnType!='L99' AND ColumnType!='SmartLife';");
				sqlite_transaction_exec(sqlite_cmd);
				s_column_SequenceNum = 10;	// 允许一些内置的栏目排在下发栏目之前，故SequenceNum从10计起
						
				DBSTAR_COLUMN_S column_s;
				memset(&column_s, 0, sizeof(column_s));
				snprintf(column_s.ServiceID,sizeof(column_s.ServiceID),"%s", xmlinfo.ServiceID);
				ret = parseNode(doc, cur, "Columns", &column_s, &xmlinfo, "Columns", NULL, old_xmlver);
				
				ret = PROCESS_OVER_CHECK(ret);
			}
		}
// GuideList.xml
		else if(0==xmlStrcmp(cur->name, BAD_CAST"GuideList")){
			parseProperty(cur, XML_ROOT_ELEMENT, (void *)&xmlinfo);
			read_xmlver_in_trans(&xmlinfo,old_xmlver,sizeof(old_xmlver));
			if((strlen(old_xmlver)>0 && 0==strcmp(old_xmlver, xmlinfo.Version)) || 0!=strcmp(serviceID_get(), xmlinfo.ServiceID)){
				DEBUG("old ver: %s, new ver: %s, my ServiceID: %s, xml ServiceID: %s, no need to parse\n",\
						old_xmlver, xmlinfo.Version, serviceID_get(), xmlinfo.ServiceID);
				ret = -1;
			}
			else{
				DBSTAR_GUIDELIST_S guidelist_s;
				memset(&guidelist_s, 0, sizeof(guidelist_s));
				snprintf(guidelist_s.ServiceID,sizeof(guidelist_s.ServiceID),"%s", xmlinfo.ServiceID);
				ret = parseNode(doc, cur, "GuideList", &guidelist_s, &xmlinfo, "GuideList", NULL, old_xmlver);
				
				ret = PROCESS_OVER_CHECK(ret);
			}
		}
// ProductDesc.xml 当前投递单
		else if(0==xmlStrcmp(cur->name, BAD_CAST"ProductDesc")){
			parseProperty(cur, XML_ROOT_ELEMENT, (void *)&xmlinfo);
			read_xmlver_in_trans(&xmlinfo,old_xmlver,sizeof(old_xmlver));
			/*
			 由于需要反注册，所以需要解析所有serviceID的投递内容做反向注册
			 || 0!=strcmp(serviceID_get(), xmlinfo.ServiceID)
			*/
			if((strlen(old_xmlver)>0 && 0==strcmp(old_xmlver, xmlinfo.Version))){
				DEBUG("old ver: %s, new ver: %s, my ServiceID: %s, xml ServiceID: %s, no need to parse\n",\
						old_xmlver, xmlinfo.Version, serviceID_get(), xmlinfo.ServiceID);
				ret = -1;
			}
			else{
				DEBUG("old ver: %s, new ver: %s\n",old_xmlver, xmlinfo.Version);
				DBSTAR_PRODUCTDESC_S productdesc_s;
				memset(&productdesc_s, 0, sizeof(productdesc_s));
				snprintf(productdesc_s.ServiceID,sizeof(productdesc_s.ServiceID),"%s", xmlinfo.ServiceID);
				ret = parseNode(doc, cur, "ProductDesc", &productdesc_s, &xmlinfo, "ProductDesc", NULL, old_xmlver);
				
				ret = PROCESS_OVER_CHECK(ret);
			}
		}
		
// Message.xml
		else if(0==xmlStrcmp(cur->name, BAD_CAST"Messages")){
			parseProperty(cur, XML_ROOT_ELEMENT, (void *)&xmlinfo);
			read_xmlver_in_trans(&xmlinfo,old_xmlver,sizeof(old_xmlver));
			if((strlen(old_xmlver)>0 && 0==strcmp(old_xmlver, xmlinfo.Version)) || 0!=strcmp(serviceID_get(), xmlinfo.ServiceID)){
				DEBUG("old ver: %s, new ver: %s, my ServiceID: %s, xml ServiceID: %s, no need to parse\n",\
						old_xmlver, xmlinfo.Version, serviceID_get(), xmlinfo.ServiceID);
				ret = -1;
			}
			else{
				ret = parseNode(doc, cur, "Messages", NULL, &xmlinfo, "Messages", NULL, old_xmlver);
				
				ret = PROCESS_OVER_CHECK(ret);
			}
		}
#if 0
// Preview.xml
		else if(0==xmlStrcmp(cur->name, BAD_CAST"Preview")){
			parseProperty(cur, XML_ROOT_ELEMENT, (void *)&xmlinfo);
			read_xmlver_in_trans(&xmlinfo,old_xmlver,sizeof(old_xmlver));
			if((strlen(old_xmlver)>0 && 0==strcmp(old_xmlver, xmlinfo.Version)) || 0!=strcmp(serviceID_get(), xmlinfo.ServiceID)){
				DEBUG("old ver: %s, new ver: %s, my ServiceID: %s, xml ServiceID: %s, no need to parse\n",\
						old_xmlver, xmlinfo.Version, serviceID_get(), xmlinfo.ServiceID);
				ret = -1;
			}
			else{
				DBSTAR_PREVIEW_S preview_s;
				memset(&preview_s, 0, sizeof(preview_s));
				ret = parseNode(doc, cur, "Preview", &preview_s, &xmlinfo, "Preview", NULL, old_xmlver);
				preview_insert(&preview_s);
				
				ret = PROCESS_OVER_CHECK(ret);
			}
		}
#endif		
		else{
			ERROROUT("xml file has wrong root node with '%s'\n", cur->name);
			ret = -1;
		}
		
		if(-1==ret)
			sqlite_transaction_end(0);
		else if(0==ret){
			if(docname)
				snprintf(xmlinfo.URI, sizeof(xmlinfo.URI), "%s", docname);
			xmlinfo_insert(&xmlinfo);
			
			sqlite_transaction_end(1);
			
			if(INITIALIZE_XML==xml_flag){
				pid_init(1);
				channel_ineffective_clear();
			}
			else if(PRODUCTDESC_XML==xml_flag || SERVICE_XML==xml_flag){
				DEBUG("refresh push monitor because of xml %d\n", xml_flag);
				
				push_recv_manage_refresh(0,NULL);
			}
			else if(COLUMN_XML==xml_flag)
				msg_send2_UI(STATUS_COLUMN_REFRESH, NULL, 0);
			else if(SPRODUCT_XML==xml_flag)
				msg_send2_UI(STATUS_INTERFACE_REFRESH, NULL, 0);
		}
	}
	
	xmlFreeDoc(doc);

PARSE_XML_END:
	DEBUG("parse xml end\n");
	pthread_mutex_unlock(&mtx_parse_xml);
	
	return ret;
}

/*
 此函数本意：如果xml依赖于serviceID才能解析，则返回1，否则返回0
 但是目前的系统将Initialize.xml单独占用初始化通道，其他xml在另外一个文件通道，此文件通道是由初始化通道开启的，所以Initialize.xml一定是第一个到来的
*/
static int depent_on_serviceID(PUSH_XML_FLAG_E xml_flag)
{
/*
	ProductDesc.xml和Service.xml涉及到push拒绝注册，因此不论Initialize.xml的serviceID是否解析到，都要解析入库
		|| PRODUCTDESC_XML == xml_flag
		|| SERVICE_XML == xml_flag
*/
	if(	COLUMN_XML == xml_flag
		|| GUIDELIST_XML == xml_flag
		|| COMMANDS_XML == xml_flag
		|| MESSAGE_XML == xml_flag
		|| SPRODUCT_XML == xml_flag )
		return 1;
	else
		return 0;
}

/*
 允许xml_name是空，但是必须有xml_flag。如果xml_uri不为空，则直接使用此uri
 此函数需要独占调用，因为如果当前解析的是Initialize.xml的话，解析完毕后还要自动扫描解析那些依赖于serviceID的xml。
 但同时，push系统的回调也有可能刚好得到这些xml而引起解析。
*/
int parse_xml(char *xml_uri, PUSH_XML_FLAG_E xml_flag, char *id)
{
	/*
	 如果还未获得serviceID，则那些依赖于serviceID进行判断的xml不能解析
	if(0==strlen(serviceID_get()) && depent_on_serviceID(xml_flag)){
		DEBUG("has no serviceID already, waiting please. xml: %s\n", xml_uri);
		return -1;
	}
	*/
	
	int ret = parseDoc(xml_uri, xml_flag, id);
	
	return ret;
}

