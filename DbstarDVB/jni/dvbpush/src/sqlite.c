#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <semaphore.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <sys/types.h>

#include "common.h"
#include "sqlite3.h"
#include "sqlite.h"
#include "porting.h"

/*
数据库必须及时打开、及时关闭，不能采用打开计数器的方式，因为上层应用也可能在使用
*/

///used in this file
static sqlite3* g_db = NULL;												///the pointer of database created or opened
static int s_sqlite_init_flag = 0;

static int createTable(char* name);
static void closeDatabase();
static int localcolumn_init();
static int global_info_init();

static int createDatabase(char *database_uri)
{
	char	*errmsgOpen=NULL;
	int		ret = 0;
	
	if(g_db!=NULL){
		DEBUG("the database has opened\n");
		ret = 0;
	}
	else
	{
		if(-1==dir_exist_ensure(database_uri)){
			return -1;
		}

		if(SQLITE_OK!=sqlite3_open(database_uri,&g_db)){
			ERROROUT("can't open database: %s\n", database_uri);
			ret = -1;
		}
		else{
			/// open foreign key support
			if(sqlite3_exec(g_db,"PRAGMA foreign_keys=ON;",NULL,NULL,&errmsgOpen)
				|| NULL!=errmsgOpen){
				ERROROUT("can't open foreign_keys\n");
				DEBUG("database errmsg: %s\n", errmsgOpen);
				ret = -1;
			}
			else{
				ret = 0;
				
				int createtable_ret = createTable("Global");
				if(-1==createtable_ret){
					ret = -1;
					goto CREATE_TABLE_END;
				}
				else{
					ret += createtable_ret;
				}
				
				createtable_ret = createTable("Initialize");
				if(-1==createtable_ret){
					ret = -1;
					goto CREATE_TABLE_END;
				}
				else{
					ret += createtable_ret;
				}
				
				createtable_ret = createTable("Channel");
				if(-1==createtable_ret){
					ret = -1;
					goto CREATE_TABLE_END;
				}
				else{
					ret += createtable_ret;
				}
				
				createtable_ret = createTable("Service");
				if(-1==createtable_ret){
					ret = -1;
					goto CREATE_TABLE_END;
				}
				else{
					ret += createtable_ret;
				}
				
				createtable_ret = createTable("ResStr");
				if(-1==createtable_ret){
					ret = -1;
					goto CREATE_TABLE_END;
				}
				else{
					ret += createtable_ret;
				}
				
				createtable_ret = createTable("ResPoster");
				if(-1==createtable_ret){
					ret = -1;
					goto CREATE_TABLE_END;
				}
				else{
					ret += createtable_ret;
				}
				
				createtable_ret = createTable("ResTrailer");
				if(-1==createtable_ret){
					ret = -1;
					goto CREATE_TABLE_END;
				}
				else{
					ret += createtable_ret;
				}
				
				createtable_ret = createTable("ResSubTitle");
				if(-1==createtable_ret){
					ret = -1;
					goto CREATE_TABLE_END;
				}
				else{
					ret += createtable_ret;
				}
				
				createtable_ret = createTable("ResExtension");
				if(-1==createtable_ret){
					ret = -1;
					goto CREATE_TABLE_END;
				}
				else{
					ret += createtable_ret;
				}
				
				createtable_ret = createTable("ResExtensionFile");
				if(-1==createtable_ret){
					ret = -1;
					goto CREATE_TABLE_END;
				}
				else{
					ret += createtable_ret;
				}
				
				createtable_ret = createTable("Column");
				if(-1==createtable_ret){
					ret = -1;
					goto CREATE_TABLE_END;
				}
				else{
					ret += createtable_ret;
				}
				
				createtable_ret = createTable("ColumnEntity");
				if(-1==createtable_ret){
					ret = -1;
					goto CREATE_TABLE_END;
				}
				else{
					ret += createtable_ret;
				}
				
				createtable_ret = createTable("Product");
				if(-1==createtable_ret){
					ret = -1;
					goto CREATE_TABLE_END;
				}
				else{
					ret += createtable_ret;
				}
				
				createtable_ret = createTable("PublicationsSet");
				if(-1==createtable_ret){
					ret = -1;
					goto CREATE_TABLE_END;
				}
				else{
					ret += createtable_ret;
				}
				
				createtable_ret = createTable("SetInfo");
				if(-1==createtable_ret){
					ret = -1;
					goto CREATE_TABLE_END;
				}
				else{
					ret += createtable_ret;
				}
				
				createtable_ret = createTable("Publication");
				if(-1==createtable_ret){
					ret = -1;
					goto CREATE_TABLE_END;
				}
				else{
					ret += createtable_ret;
				}
				
				createtable_ret = createTable("MultipleLanguageInfoVA");
				if(-1==createtable_ret){
					ret = -1;
					goto CREATE_TABLE_END;
				}
				else{
					ret += createtable_ret;
				}
				
				createtable_ret = createTable("MultipleLanguageInfoRM");
				if(-1==createtable_ret){
					ret = -1;
					goto CREATE_TABLE_END;
				}
				else{
					ret += createtable_ret;
				}
				
				createtable_ret = createTable("MultipleLanguageInfoApp");
				if(-1==createtable_ret){
					ret = -1;
					goto CREATE_TABLE_END;
				}
				else{
					ret += createtable_ret;
				}
				
				createtable_ret = createTable("Message");
				if(-1==createtable_ret){
					ret = -1;
					goto CREATE_TABLE_END;
				}
				else{
					ret += createtable_ret;
				}
				
				createtable_ret = createTable("GuideList");
				if(-1==createtable_ret){
					ret = -1;
					goto CREATE_TABLE_END;
				}
				else{
					ret += createtable_ret;
				}
				
				createtable_ret = createTable("ProductDesc");
				if(-1==createtable_ret){
					ret = -1;
					goto CREATE_TABLE_END;
				}
				else{
					ret += createtable_ret;
				}
				
				createtable_ret = createTable("Preview");
				if(-1==createtable_ret){
					ret = -1;
					goto CREATE_TABLE_END;
				}
				else{
					ret += createtable_ret;
				}
				
				createtable_ret = createTable("RejectRecv");
				if(-1==createtable_ret){
					ret = -1;
					goto CREATE_TABLE_END;
				}
				else{
					ret += createtable_ret;
				}
				
				createtable_ret = createTable("SProduct");
				if(-1==createtable_ret){
					ret = -1;
					goto CREATE_TABLE_END;
				}
				else{
					ret += createtable_ret;
				}
				
				// 智能卡中的授权产品ID
				createtable_ret = createTable("SCEntitleInfo");
				if(-1==createtable_ret){
					ret = -1;
					goto CREATE_TABLE_END;
				}
				else{
					ret += createtable_ret;
				}
				
CREATE_TABLE_END:
				DEBUG("shot tables finished, ret=%d\n", ret);
			}
			sqlite3_free(errmsgOpen);
			closeDatabase();
		}
	}
	
	return ret;
}

static int openDatabase()
{
	int ret = -1;
	
	if(g_db!=NULL){
		DEBUG("the database has opened\n");
		ret = 0;
	}
	else
	{
		if(SQLITE_OK!=sqlite3_open(dbstar_database_uri(),&g_db)){
			ERROROUT("can't open database\n");
			ret = -1;
		}
		else
			ret = 0;
	}
	
	return ret;
}

static void closeDatabase()
{
	if(g_db!=NULL)
	{
		sqlite3_close(g_db);
		g_db=NULL;
	}
	else{
		DEBUG("g_db is NULL, can not do database close action\n");
	}
	
	return;
}

/*
返回值：
0——指定的表存在，成功；
-1——创建表失败；
1——指定的表创建成功。
*/
static int createTable(char* name)
{
	char* errmsg=NULL;
	char ** l_result=NULL;									    	///result of tables in database
	int l_row=0;                                            	///the row of result
	int l_column=0;									        	///the column of result
	char sqlite_cmd[4096];
	int ret = -1;
	
	memset(sqlite_cmd, 0, sizeof(sqlite_cmd));
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "SELECT name FROM sqlite_master WHERE type='table' AND name='%s';", name);
	if(sqlite3_get_table(g_db,sqlite_cmd,&l_result,&l_row,&l_column,&errmsg))
	{
		ERROROUT("read tables from database failed.");
		ret = -1;
	}
	else{
		if(l_row>0){
			DEBUG("tabel \"%s\" is exist, OK\n", name);
			ret = 0;
		}
		else{
			sqlite3_free(errmsg);
			ret = 1;
			/*
			这里建立表的目的是查询，不是存储，所以不能用于查询的图片、结构体、描述等
			*/
			if(!strcmp(name,"Global"))
			{
				snprintf(sqlite_cmd, sizeof(sqlite_cmd),\
					"CREATE TABLE %s(\
Name	NVARCHAR(64) PRIMARY KEY,\
Value	NVARCHAR(128) DEFAULT '',\
Param	NVARCHAR(1024) DEFAULT '');", name);
			}
			else if(!strcmp(name,"Initialize"))
			{
				snprintf(sqlite_cmd, sizeof(sqlite_cmd),\
					"CREATE TABLE %s(\
PushFlag	NVARCHAR(64) DEFAULT '',\
ServiceID	NVARCHAR(64) DEFAULT '',\
XMLName	NVARCHAR(64) DEFAULT '',\
Version	NVARCHAR(64) DEFAULT '',\
StandardVersion	NVARCHAR(32) DEFAULT '',\
URI	NVARCHAR(256) DEFAULT '',\
ID	NVARCHAR(64) DEFAULT '',\
TimeStamp NOT NULL DEFAULT (datetime('now','localtime')),\
PRIMARY KEY (PushFlag,ServiceID,ID));", name);
			}
			else if(!strcmp(name,"Channel"))
			{
				snprintf(sqlite_cmd, sizeof(sqlite_cmd),\
					"CREATE TABLE %s(\
pid	NVARCHAR(64) DEFAULT '',\
ServiceID	NVARCHAR(64) DEFAULT '',\
pidtype	NVARCHAR(64) DEFAULT '',\
URI NVARCHAR(256) DEFAULT '',\
FreshFlag INTEGER DEFAULT 1,\
TimeStamp NOT NULL DEFAULT (datetime('now','localtime')),\
PRIMARY KEY (pid,ServiceID));", name);
			}
			else if(!strcmp(name,"Service"))
			{
				snprintf(sqlite_cmd, sizeof(sqlite_cmd),\
					"CREATE TABLE %s(\
ServiceID	NVARCHAR(64) PRIMARY KEY,\
RegionCode	NVARCHAR(64) DEFAULT '',\
OnlineTime	DATETIME DEFAULT '',\
OfflineTime	DATETIME DEFAULT '',\
Status	RCHAR(32) DEFAULT '0',\
TimeStamp NOT NULL DEFAULT (datetime('now','localtime')));", name);
			}
			else if(!strcmp(name,"ResStr"))
			{
				snprintf(sqlite_cmd, sizeof(sqlite_cmd),\
					"CREATE TABLE %s(\
ServiceID	NVARCHAR(64) DEFAULT '0',\
ObjectName	NVARCHAR(64) DEFAULT '',\
EntityID	NVARCHAR(128) DEFAULT '',\
StrLang		NVARCHAR(32) DEFAULT '',\
StrName		NVARCHAR(64) DEFAULT '',\
Extension	NVARCHAR(64) DEFAULT '',\
StrValue	NVARCHAR(1024) DEFAULT '',\
PRIMARY KEY (ServiceID,ObjectName,EntityID,StrLang,StrName,Extension));", name);
			}
			else if(!strcmp(name,"ResPoster"))
			{
				snprintf(sqlite_cmd, sizeof(sqlite_cmd),\
					"CREATE TABLE %s(\
ServiceID	NVARCHAR(64) DEFAULT '0',\
ObjectName	NVARCHAR(64) DEFAULT '',\
EntityID	NVARCHAR(128) DEFAULT '',\
PosterID	NVARCHAR(64) DEFAULT '',\
PosterName	NVARCHAR(64) DEFAULT '',\
PosterURI	NVARCHAR(256) DEFAULT '',\
PRIMARY KEY (ServiceID,ObjectName,EntityID,PosterID));", name);
			}
			else if(!strcmp(name,"ResTrailer"))
			{
				snprintf(sqlite_cmd, sizeof(sqlite_cmd),\
					"CREATE TABLE %s(\
ServiceID	NVARCHAR(64) DEFAULT '0',\
ObjectName	NVARCHAR(64) DEFAULT '',\
EntityID	NVARCHAR(128) DEFAULT '',\
TrailerID	NVARCHAR(64) DEFAULT '',\
TrailerName	NVARCHAR(64) DEFAULT '',\
TrailerURI	NVARCHAR(256) DEFAULT '',\
PRIMARY KEY (ServiceID,ObjectName,EntityID,TrailerID));", name);
			}
			else if(!strcmp(name,"ResSubTitle"))
			{
				snprintf(sqlite_cmd, sizeof(sqlite_cmd),\
					"CREATE TABLE %s(\
ServiceID	NVARCHAR(64) DEFAULT '0',\
ObjectName	NVARCHAR(64) DEFAULT '',\
EntityID	NVARCHAR(128) DEFAULT '',\
SubTitleID	NVARCHAR(64) DEFAULT '',\
SubTitleName	NVARCHAR(64) DEFAULT '',\
SubTitleLanguage	NVARCHAR(64) DEFAULT '',\
SubTitleURI	NVARCHAR(256) DEFAULT '',\
PRIMARY KEY (ServiceID,ObjectName,EntityID,SubTitleID));", name);
			}
			else if(!strcmp(name,"ResExtension"))
			{
				snprintf(sqlite_cmd, sizeof(sqlite_cmd),\
					"CREATE TABLE %s(\
ServiceID	NVARCHAR(64) DEFAULT '0',\
ObjectName	NVARCHAR(256) DEFAULT '',\
EntityID	NVARCHAR(128) DEFAULT '',\
Name	NVARCHAR(64) DEFAULT '',\
Type	NVARCHAR(64) DEFAULT '',\
PRIMARY KEY (ServiceID,ObjectName,EntityID,Name));", name);
			}
			else if(!strcmp(name,"ResExtensionFile"))
			{
				snprintf(sqlite_cmd, sizeof(sqlite_cmd),\
					"CREATE TABLE %s(\
ServiceID	NVARCHAR(64) DEFAULT '0',\
ObjectName	NVARCHAR(256) DEFAULT '',\
EntityID	NVARCHAR(128) DEFAULT '',\
FileID	NVARCHAR(64) DEFAULT '',\
FileName	NVARCHAR(64) DEFAULT '',\
FileURI	NVARCHAR(256) DEFAULT '',\
PRIMARY KEY (ServiceID,ObjectName,EntityID,FileID));", name);
			}
			else if(!strcmp(name,"Column"))
			{
				snprintf(sqlite_cmd, sizeof(sqlite_cmd),\
					"CREATE TABLE %s(\
ServiceID	NVARCHAR(64) DEFAULT '0',\
ColumnID	NVARCHAR(64) DEFAULT '',\
ParentID	NVARCHAR(64) DEFAULT '',\
Path	NVARCHAR(256) DEFAULT '',\
ColumnType	NVARCHAR(256) DEFAULT '',\
ColumnIcon_losefocus	NVARCHAR(256) DEFAULT '',\
ColumnIcon_getfocus	NVARCHAR(256) DEFAULT '',\
ColumnIcon_onclick	NVARCHAR(256) DEFAULT '',\
ColumnIcon_spare	NVARCHAR(256) DEFAULT '',\
SequenceNum	INTEGER,\
URI	NVARCHAR(256),\
TimeStamp NOT NULL DEFAULT (datetime('now','localtime')),\
PRIMARY KEY (ServiceID,ColumnID));", name);
			}
			else if(!strcmp(name,"ColumnEntity"))
			{
				snprintf(sqlite_cmd, sizeof(sqlite_cmd),\
					"CREATE TABLE %s(\
ServiceID	NVARCHAR(64) DEFAULT '0',\
ColumnID	NVARCHAR(64) DEFAULT '',\
EntityID	NVARCHAR(64) DEFAULT '',\
EntityType	NVARCHAR(64) DEFAULT '',\
TimeStamp NOT NULL DEFAULT (datetime('now','localtime')));", name);
			}
			else if(!strcmp(name,"Product"))
			{
				snprintf(sqlite_cmd, sizeof(sqlite_cmd),\
					"CREATE TABLE %s(\
ServiceID	NVARCHAR(64) DEFAULT '0',\
ProductID	NVARCHAR(64) DEFAULT '',\
ProductType	NVARCHAR(64) DEFAULT '',\
Flag	NVARCHAR(64) DEFAULT '',\
OnlineDate	DATETIME DEFAULT '',\
OfflineDate	DATETIME DEFAULT '',\
IsReserved	CHAR(32) DEFAULT '0',\
Price	NVARCHAR(32) DEFAULT '',\
CurrencyType	NVARCHAR(32) DEFAULT '',\
DRMFile	NVARCHAR(256) DEFAULT '',\
ColumnID	NVARCHAR(64) DEFAULT '',\
Authorization	NVARCHAR(64) DEFAULT '',\
Visible	CHAR(32) DEFAULT '1',\
Deleted	NVARCHAR(32) DEFAULT '',\
VODNum	NVARCHAR(64) DEFAULT '',\
VODPlatform	NVARCHAR(256),\
TimeStamp NOT NULL DEFAULT (datetime('now','localtime')),\
PRIMARY KEY (ServiceID,ProductID));", name);
			}
			else if(!strcmp(name,"PublicationsSet"))
			{
				snprintf(sqlite_cmd, sizeof(sqlite_cmd),\
					"CREATE TABLE %s(\
ServiceID	NVARCHAR(64) DEFAULT '0',\
SetID	NVARCHAR(64) DEFAULT '',\
ColumnID	NVARCHAR(64) DEFAULT '',\
ProductID	NVARCHAR(64) DEFAULT '',\
URI	NVARCHAR(256) DEFAULT '',\
TotalSize	NVARCHAR(64) DEFAULT '',\
ProductDescID	NVARCHAR(64) DEFAULT '',\
ReceiveStatus	NVARCHAR(64) DEFAULT '0',\
PushStartTime	DATETIME DEFAULT '',\
PushEndTime	DATETIME DEFAULT '',\
PublicationType	NVARCHAR(64) DEFAULT '',\
IsReserved	NVARCHAR(64) DEFAULT '0',\
Visible	NVARCHAR(64) DEFAULT '1',\
Favorite	NVARCHAR(64) DEFAULT '0',\
IsAuthorized	NVARCHAR(64) DEFAULT '',\
VODNum	NVARCHAR(64) DEFAULT '',\
VODPlatform	NVARCHAR(256) DEFAULT '',\
Deleted NVARCHAR(256) DEFAULT '0',\
TimeStamp NOT NULL DEFAULT (datetime('now','localtime')),\
PRIMARY KEY (ServiceID,SetID,ColumnID));", name);
			}
			else if(!strcmp(name,"SetInfo"))
			{
				snprintf(sqlite_cmd, sizeof(sqlite_cmd),\
					"CREATE TABLE %s(\
ServiceID	NVARCHAR(64) DEFAULT '0',\
SetID	NVARCHAR(64) DEFAULT '',\
ProductID	NVARCHAR(64) DEFAULT '',\
infolang	NVARCHAR(64) DEFAULT 'cho',\
Title	NVARCHAR(128) DEFAULT '',\
Starring	NVARCHAR(256) DEFAULT '',\
Scenario	NVARCHAR(512) DEFAULT '',\
Classification	NVARCHAR(64) DEFAULT '',\
Period	NVARCHAR(64) DEFAULT '',\
CollectionNumber	NVARCHAR(64) DEFAULT '',\
Review	NVARCHAR(64) DEFAULT '',\
TimeStamp NOT NULL DEFAULT (datetime('now','localtime')),\
PRIMARY KEY (ServiceID,SetID,infolang));", name);
			}
			else if(!strcmp(name,"Publication"))
			{
				snprintf(sqlite_cmd, sizeof(sqlite_cmd),\
					"CREATE TABLE %s(\
ServiceID	NVARCHAR(64) DEFAULT '0',\
PublicationID	NVARCHAR(64) DEFAULT '',\
ColumnID	NVARCHAR(64) DEFAULT '',\
ProductID	NVARCHAR(64) DEFAULT '',\
URI	NVARCHAR(256) DEFAULT '',\
DescURI	NVARCHAR(256) DEFAULT '',\
TotalSize	NVARCHAR(64) DEFAULT '',\
ProductDescID	NVARCHAR(64) DEFAULT '',\
ReceiveStatus	NVARCHAR(64) DEFAULT '0',\
PushStartTime	DATETIME DEFAULT '',\
PushEndTime	DATETIME DEFAULT '',\
PublicationType	NVARCHAR(64) DEFAULT '',\
IsReserved	CHAR(32) DEFAULT '0',\
Visible	CHAR(32) DEFAULT '1',\
DRMFile	NVARCHAR(256) DEFAULT '',\
SetID	NVARCHAR(64) DEFAULT '',\
IndexInSet	NVARCHAR(32) DEFAULT '',\
Favorite	NVARCHAR(32) DEFAULT '0',\
Bookmark	NVARCHAR(32) DEFAULT '0',\
IsAuthorized	NVARCHAR(64) DEFAULT '',\
VODNum	NVARCHAR(64) DEFAULT '',\
VODPlatform	NVARCHAR(256) DEFAULT '',\
Deleted NVARCHAR(256) DEFAULT '0',\
FileID	NVARCHAR(64) DEFAULT '',\
FileSize	NVARCHAR(64) DEFAULT '',\
FileURI	NVARCHAR(256) DEFAULT '',\
FileType	NVARCHAR(64) DEFAULT '',\
FileFormat	NVARCHAR(32) DEFAULT '',\
Duration	NVARCHAR(32) DEFAULT '',\
Resolution	NVARCHAR(32) DEFAULT '',\
BitRate	NVARCHAR(32) DEFAULT '',\
CodeFormat	NVARCHAR(32) DEFAULT '',\
TimeStamp NOT NULL DEFAULT (datetime('now','localtime')),\
PRIMARY KEY (ServiceID,PublicationID,ColumnID));", name);
			}
			else if(!strcmp(name,"MultipleLanguageInfoVA"))
			{
				snprintf(sqlite_cmd, sizeof(sqlite_cmd),\
					"CREATE TABLE %s(\
ServiceID	NVARCHAR(64) DEFAULT '0',\
PublicationID	NVARCHAR(64) DEFAULT '',\
infolang	NVARCHAR(64) DEFAULT '',\
PublicationDesc	NVARCHAR(1024) DEFAULT '',\
ImageDefinition	NVARCHAR(32) DEFAULT '',\
Keywords	NVARCHAR(256) DEFAULT '',\
Area	NVARCHAR(64) DEFAULT '',\
Language	NVARCHAR(64) DEFAULT '',\
Episode	NVARCHAR(32) DEFAULT '',\
AspectRatio	NVARCHAR(32) DEFAULT '',\
AudioChannel	NVARCHAR(32) DEFAULT '',\
Director	NVARCHAR(128) DEFAULT '',\
Actor	NVARCHAR(256) DEFAULT '',\
Audience	NVARCHAR(64) DEFAULT '',\
Model	NVARCHAR(32) DEFAULT '',\
PRIMARY KEY (ServiceID,PublicationID,infolang));", name);
			}
			else if(!strcmp(name,"MultipleLanguageInfoRM"))
			{
				snprintf(sqlite_cmd, sizeof(sqlite_cmd),\
					"CREATE TABLE %s(\
ServiceID	NVARCHAR(64) DEFAULT '0',\
PublicationID	NVARCHAR(64) DEFAULT '',\
infolang	NVARCHAR(64) DEFAULT '',\
PublicationDesc	NVARCHAR(1024) DEFAULT '',\
Keywords	NVARCHAR(256) DEFAULT '',\
Publisher	NVARCHAR(128) DEFAULT '',\
Area	NVARCHAR(64) DEFAULT '',\
Language	NVARCHAR(64) DEFAULT '',\
Episode	NVARCHAR(32) DEFAULT '',\
AspectRatio	NVARCHAR(32) DEFAULT '',\
VolNum	NVARCHAR(64) DEFAULT '',\
ISSN	NVARCHAR(64) DEFAULT '',\
PRIMARY KEY (ServiceID,PublicationID,infolang));", name);
			}
			else if(!strcmp(name,"MultipleLanguageInfoApp"))
			{
				snprintf(sqlite_cmd, sizeof(sqlite_cmd),\
					"CREATE TABLE %s(\
ServiceID	NVARCHAR(64) DEFAULT '0',\
PublicationID	NVARCHAR(64) DEFAULT '',\
infolang	NVARCHAR(64) DEFAULT '',\
PublicationDesc	NVARCHAR(1024) DEFAULT '',\
Keywords	NVARCHAR(256) DEFAULT '',\
Category	NVARCHAR(64) DEFAULT '',\
Released	DATETIME DEFAULT '',\
AppVersion	NVARCHAR(64) DEFAULT '',\
Language	NVARCHAR(64) DEFAULT '',\
Developer	NVARCHAR(64) DEFAULT '',\
Rated	NVARCHAR(64) DEFAULT '',\
Requirements	NVARCHAR(64) DEFAULT '',\
PRIMARY KEY (ServiceID,PublicationID,infolang));", name);
			}
			else if(!strcmp(name,"Message"))
			{
				snprintf(sqlite_cmd, sizeof(sqlite_cmd),\
					"CREATE TABLE %s(\
ServiceID	NVARCHAR(64) DEFAULT '0',\
MessageID	NVARCHAR(64) DEFAULT '',\
type	NVARCHAR(64) DEFAULT '',\
displayForm	NVARCHAR(64) DEFAULT '',\
StartTime	DATETIME DEFAULT '',\
EndTime		DATETIME DEFAULT '',\
Interval	CHAR(32) DEFAULT '',\
TimeStamp NOT NULL DEFAULT (datetime('now','localtime')),\
PRIMARY KEY (ServiceID,MessageID));", name);
			}
			else if(!strcmp(name,"GuideList"))
			{
				snprintf(sqlite_cmd, sizeof(sqlite_cmd),\
					"CREATE TABLE %s(\
ServiceID	NVARCHAR(64) DEFAULT '0',\
DateValue	DATETIME DEFAULT '',\
GuideListID	NVARCHAR(64) DEFAULT '',\
productID	NVARCHAR(64) DEFAULT '',\
PublicationID	NVARCHAR(64) DEFAULT '',\
URI	NVARCHAR(256) DEFAULT '',\
TotalSize	NVARCHAR(64) DEFAULT '',\
ProductDescID	NVARCHAR(64) DEFAULT '',\
ReceiveStatus	NVARCHAR(64) DEFAULT '0',\
PushStartTime	DATETIME DEFAULT '',\
PushEndTime	DATETIME DEFAULT '',\
UserStatus	NVARCHAR(64) DEFAULT '1',\
TimeStamp NOT NULL DEFAULT (datetime('now','localtime')),\
PRIMARY KEY (ServiceID,DateValue,PublicationID));", name);
			}
			else if(!strcmp(name,"ProductDesc"))
			{
				snprintf(sqlite_cmd, sizeof(sqlite_cmd),\
					"CREATE TABLE %s(\
ServiceID	NVARCHAR(64) DEFAULT '',\
ReceiveType	NVARCHAR(64) DEFAULT '',\
rootPath	NVARCHAR(256) DEFAULT '',\
ProductDescID	NVARCHAR(128) DEFAULT '',\
productID	NVARCHAR(64) DEFAULT '',\
ID	NVARCHAR(64) DEFAULT '',\
SetID	NVARCHAR(64) DEFAULT '',\
TotalSize	NVARCHAR(64) DEFAULT '',\
URI	NVARCHAR(256) DEFAULT '',\
DescURI	NVARCHAR(384) DEFAULT '',\
PushStartTime	DATETIME DEFAULT '',\
PushEndTime	DATETIME DEFAULT '',\
Columns	NVARCHAR(512) DEFAULT '',\
ReceiveStatus	NVARCHAR(64) DEFAULT '0',\
FreshFlag INTEGER DEFAULT 1,\
Parsed	NVARCHAR(32) DEFAULT '',\
TimeStamp NOT NULL DEFAULT (datetime('now','localtime')),\
PRIMARY KEY (ServiceID,ReceiveType,ID));", name);
			}
			else if(!strcmp(name,"Preview"))
			{
				snprintf(sqlite_cmd, sizeof(sqlite_cmd),\
					"CREATE TABLE %s(\
ServiceID	NVARCHAR(64) DEFAULT '0',\
PreviewID	NVARCHAR(64) DEFAULT '0',\
ProductID	NVARCHAR(64) DEFAULT '',\
PublicationID	NVARCHAR(64) DEFAULT '',\
IsReserved	CHAR(32) DEFAULT '0',\
Visible	CHAR(32) DEFAULT '1',\
PreviewType	NVARCHAR(64) DEFAULT '',\
PreviewSize	NVARCHAR(64) DEFAULT '',\
ShowTime	DATETIME DEFAULT '',\
PreviewURI	NVARCHAR(256) DEFAULT '',\
PreviewFormat	NVARCHAR(64) DEFAULT '',\
Duration	NVARCHAR(64) DEFAULT '',\
Resolution	NVARCHAR(64) DEFAULT '',\
BitRate	NVARCHAR(64) DEFAULT '',\
CodeFormat	NVARCHAR(64) DEFAULT '',\
URI	NVARCHAR(256) DEFAULT '',\
TotalSize	NVARCHAR(64) DEFAULT '',\
ProductDescID	NVARCHAR(64) DEFAULT '',\
ReceiveStatus	NVARCHAR(64) DEFAULT '0',\
PushStartTime	DATETIME DEFAULT '',\
PushEndTime	DATETIME DEFAULT '',\
StartTime	DATETIME DEFAULT '',\
EndTime	DATETIME DEFAULT '',\
PlayMode	NVARCHAR(64) DEFAULT '',\
TimeStamp NOT NULL DEFAULT (datetime('now','localtime')),\
PRIMARY KEY (ServiceID,PreviewID));", name);
			}
			else if(!strcmp(name,"RejectRecv"))
			{
				snprintf(sqlite_cmd, sizeof(sqlite_cmd),\
					"CREATE TABLE %s(\
ServiceID	NVARCHAR(64) DEFAULT '',\
ID	NVARCHAR(64) DEFAULT '',\
URI	NVARCHAR(512) DEFAULT '',\
Type	NVARCHAR(64) DEFAULT '',\
PushStartTime	DATETIME DEFAULT '',\
PushEndTime	DATETIME DEFAULT '',\
TimeStamp NOT NULL DEFAULT (datetime('now','localtime')),\
PRIMARY KEY (ServiceID,ID));", name);
			}
			else if(!strcmp(name,"SProduct"))
			{
				snprintf(sqlite_cmd, sizeof(sqlite_cmd),\
					"CREATE TABLE %s(\
ServiceID	NVARCHAR(64) DEFAULT '0',\
SType	NVARCHAR(64) DEFAULT '',\
Name	NVARCHAR(64) DEFAULT '',\
URI	NVARCHAR(256) DEFAULT '',\
URI_spare	NVARCHAR(256) DEFAULT '',\
TimeStamp NOT NULL DEFAULT (datetime('now','localtime')),\
PRIMARY KEY (ServiceID,SType));", name);
			}
			
			else if(!strcmp(name,"SCEntitleInfo"))
			{
				snprintf(sqlite_cmd, sizeof(sqlite_cmd),\
					"CREATE TABLE %s(\
SmartCardID		NVARCHAR(64) DEFAULT '',\
m_OperatorID	NVARCHAR(64) DEFAULT '',\
m_ID	NVARCHAR(64) DEFAULT '',\
m_ProductStartTime	NVARCHAR(64) DEFAULT '',\
m_ProductEndTime	NVARCHAR(64) DEFAULT '',\
m_WatchStartTime	NVARCHAR(64) DEFAULT '',\
m_WatchEndTime	NVARCHAR(64) DEFAULT '',\
m_LimitTotaltValue	NVARCHAR(64) DEFAULT '',\
m_LimitUsedValue	NVARCHAR(64) DEFAULT '',\
TimeStamp NOT NULL DEFAULT (datetime('now','localtime')),\
PRIMARY KEY (SmartCardID,m_OperatorID,m_ID));", name);
			}
			else{
				DEBUG("baby: table %s is not defined, so can not create it\n", name);
				memset(sqlite_cmd, 0, sizeof(sqlite_cmd));
				ret = -1;
			}
			
			if(strlen(sqlite_cmd)>0){
				if(0==sqlite3_exec(g_db,sqlite_cmd,NULL,NULL,&errmsg))
				{
					DEBUG("create table '%s' success\n", name);
					ret = 1;
				}
				else
				{
					ERROROUT("create '%s' failed: %s\n", name, sqlite_cmd);
					DEBUG("sqlite errmsg: %s\n", errmsg);
					ret = -1;
				}
			}
			else
				ret = -1;
		}
	}

	sqlite3_free_table(l_result);
	sqlite3_free(errmsg);
	return ret;
}


/*
 回调：只用于读取单个字符串类型字段的单个值
*/
int str_read_cb(char **result, int row, int column, void *some_str, unsigned int receiver_size)
{
	//DEBUG("sqlite callback, row=%d, column=%d, filter_act addr: %p\n", row, column, some_str);
	if(row<1 || NULL==some_str){
		DEBUG("no record in table, or no buffer to read, return\n");
		return 0;
	}
	
	int i = 1;
//	for(i=1;i<row+1;i++)
	{
		//DEBUG("==%s:%s:%ld==\n", result[i*column], result[i*column+1], strtol(result[i*column+1], NULL, 0));
		if(result[i*column])
			snprintf((char *)some_str, receiver_size, "%s", result[i*column]);
		else
			DEBUG("NULL value\n");
	}
	
	return 0;
}


/***sqlite_init() brief init sqlite, include open database, create table, and so on.
 * param null
 *
 * retval int,0 if successful or -1 failed
 ***/
int sqlite_init()
{
	if(0==s_sqlite_init_flag){
		g_db = NULL;
		
		int ret = createDatabase(dbstar_database_uri());
		if(ret>=0){
			if(ret>0){
				DEBUG("create database success(some/all tables are created)\n");
				chmod(dbstar_database_uri(),0666);
			}
			DEBUG("open database success\n");
			localcolumn_init();
			global_info_init();
		}
		else{						///open database failed
			DEBUG("create/open database failed\n");
			return -1;
		}
			
		s_sqlite_init_flag = 1;
	}

	return 0;						/// quit
}

int sqlite_uninit()
{
	return 0;
}


/*
 数据库事务使用规则：
 1、事务的开始和结束必须是成对出现的。事务不允许嵌套。
 2、对于整体替换的数据表，应将数据表清除、数据录入工作封装在一个事务中。
 3、所有Res开头的数据表，它们的数据都是依存于其他数据表才有意义，
 	因此这些数据表的插入、删除、更新等动作，均是某个宿主事务的一部分，它们自身不构成一个单独的事务。
*/
typedef enum{
	SQL_STATUS_IDLE = 0,
	SQL_STATUS_BUSY = 1,		// 普通的sqlite调用
	SQL_STATUS_TRANS = 2,		// sqlite事务中
}SQL_STATUS;
static SQL_STATUS s_sql_status = SQL_STATUS_IDLE;



/***getGlobalPara() brief get some global variables from sqlite, such as 'version'.
 * param name[in], the name of global param
 *
 * retval int,0 if successful or -1 failed
 ***/
int sqlite_execute(char *exec_str)
{
	char* errmsg=NULL;
	int ret = -1;
	int waiting_cnt = 0;
	
	while(SQL_STATUS_IDLE!=s_sql_status && waiting_cnt<=15){
		usleep(100000);
		waiting_cnt ++;
	}
	DEBUG("waiting_cnt=%d\n", waiting_cnt);
	
	if(SQL_STATUS_IDLE!=s_sql_status){
		DEBUG("s_sql_status=%d, failed\n", s_sql_status);
		ret = -1;
	}
	else{
		s_sql_status = SQL_STATUS_BUSY;
		
		//open database
		if(-1==openDatabase())
		{
			ERROROUT("Open database failed\n");
			ret = -1;
		}
		else{
			//DEBUG("%s\n", exec_str);
			if(sqlite3_exec(g_db,exec_str,NULL,NULL,&errmsg)){
				DEBUG("sqlite3 errmsg: %s\n", errmsg);
				ret = -1;
			}
			else{
				//DEBUG("sqlite3_exec success\n");
				ret = 0;
			}
			
			sqlite3_free(errmsg);								///	release the memery possessed by error message
			closeDatabase();									///	close database
		}
		
		s_sql_status = SQL_STATUS_IDLE;
	}
	
	return ret;	
}

/*
功能：	执行SELECT语句
输入：	sqlite_cmd				——sql SELECT语句
		receiver				——用于处理SELECT结果的参数，如果sqlite_read_callback为NULL，则receiver也可以为NULL
		receiver_size			——receiver的大小，在receiver为数组时应根据此值做安全拷贝
		sqlite_read_callback	——用于处理SELECT结果的回调，如果只是想知道查询到几条记录，则此回调可以为NULL
返回：	-1——失败；其他值——查询到的记录数
*/
int sqlite_read(char *sqlite_cmd, void *receiver, unsigned int receiver_size, int (*sqlite_read_callback)(char **result, int row, int column, void *receiver, unsigned int receiver_size))
{
	char* errmsg=NULL;
	char** l_result = NULL;
	int l_row = 0;
	int l_column = 0;
	int ret = 0;
	int (*sqlite_callback)(char **,int,int,void *,unsigned int) = sqlite_read_callback;

	int waiting_cnt = 0;
	
	while(SQL_STATUS_IDLE!=s_sql_status && waiting_cnt<=15){
		usleep(100000);
		waiting_cnt ++;
	}
	DEBUG("waiting_cnt=%d\n", waiting_cnt);
	
	//DEBUG("sqlite read: %s\n", sqlite_cmd);
	
	if(SQL_STATUS_IDLE!=s_sql_status){
		DEBUG("s_sql_status=%d, failed\n", s_sql_status);
		ret = -1;
	}
	else{
		s_sql_status = SQL_STATUS_BUSY;
		
		///open database
		if(-1==openDatabase())
		{
			ERROROUT("Open database failed\n");
			ret = -1;
		}
		else{
			// open database ok
			if(sqlite3_get_table(g_db,sqlite_cmd,&l_result,&l_row,&l_column,&errmsg)
				|| NULL!=errmsg)
			{
				ERROROUT("sqlite cmd: %s\n", sqlite_cmd);
				DEBUG("errmsg: %s\n", errmsg);
				ret = -1;
			}
			else{ // inquire table ok
				if(0==l_row){
					DEBUG("no row, l_row=0, l_column=%d\n", l_column);
				}
				else{
					DEBUG("sqlite select OK, %s\n", NULL==sqlite_callback?"no callback fun":"do callback fun");
					if(sqlite_callback)	// && receiver
						sqlite_callback(l_result, l_row, l_column, receiver, receiver_size);
					else{
						DEBUG("no sqlite callback, l_row=%d, l_column=%d\n", l_row, l_column);
	//					int i = 0;
	//					for(i=0;i<(l_column+1);i++)
	//						printf("\t\t%s\n", l_result[i]);
					}
				}
				ret = l_row;
			}
			sqlite3_free_table(l_result);
			sqlite3_free(errmsg);
			closeDatabase();
		}
		
		s_sql_status = SQL_STATUS_IDLE;
	}
	
	return ret;
}

int sqlite_table_clear(char *table_name)
{
	DEBUG("CAUTION: will clear table '%s'\n", table_name);
	char sqlite_cmd[256];	
	
	memset(sqlite_cmd, 0, sizeof(sqlite_cmd));
	snprintf(sqlite_cmd,sizeof(sqlite_cmd),"DELETE FROM %s;", table_name);
	DEBUG("sqlite cmd str: %s\n", sqlite_cmd);

	int ret = sqlite_execute(sqlite_cmd);
	if(0==ret){
		DEBUG("table '%s' clear successfully\n", table_name);
		return 0;
	}
	else{
		DEBUG("table '%s' reset failed\n", table_name);
		return -1;
	}
}

#if 0
/*
 将一系列sqlite语句封装为一个事务，各个语句间用'\n'结尾。
*/
int sqlite_transaction(char *sqlite_cmd)
{
	if(NULL==sqlite_cmd || 0==strlen(sqlite_cmd)){
		DEBUG("invalid argument\n");
		return -1;
	}
	DEBUG("%s\n", sqlite_cmd);
	int ret = -1;
	
	if(-1==openDatabase())
	{
		ERROROUT("Open database failed\n");
		ret = -1;
	}
	else{
		ret = sqlite3_exec(g_db, "begin transaction", NULL, NULL, NULL);
		if(SQLITE_OK == ret){
			char *p_cmd = sqlite_cmd;
			char *p = NULL;
			unsigned int killed_len = 0;
			do{
				killed_len = 0;
				p = strchr(p_cmd, '\n');
				if(p){
					*p = '\0';
					killed_len += 1;
				}
				killed_len += strlen(p_cmd);
				
				DEBUG("sqlite cmd: %s, p=%s, killed_len=%d\n", p_cmd, p, killed_len);
				if(strlen(p_cmd)>0){
					ret = sqlite3_exec(g_db, p_cmd, NULL, NULL, NULL);
					if(SQLITE_OK == ret){
						;
					}
					else{
						DEBUG("sqlite3 errmsg: %s\n", sqlite3_errmsg(g_db));
						ret = -1;
						break;
					}
				}
				p_cmd += killed_len;
			}while(p_cmd && strlen(p_cmd)>0);
			
			if(SQLITE_OK == ret){
				ret = sqlite3_exec(g_db, "commit transaction", NULL, NULL, NULL);
			}
			else{
				DEBUG("rollback transaction\n");
				ret = sqlite3_exec(g_db, "rollback transaction", NULL, NULL, NULL);
			}
			
			if(SQLITE_OK == ret){
				ret = 0;
			}
			else{
				DEBUG("%s\n", sqlite3_errmsg(g_db));
				ret = -1;
			}
		}
		else{
			DEBUG("sqlite3 errmsg: %s\n", sqlite3_errmsg(g_db));
			ret = -1;
		}
		
		closeDatabase();									///	close database
	}
	
	return ret;
}
#endif


/*
 函数1：事务开始
*/
int sqlite_transaction_begin()
{
	int ret = -1;
	
	PRINTF("sqlite_transaction_begin >>\n");
	if(SQL_STATUS_IDLE!=s_sql_status){
		DEBUG("s_sql_status=%d, transaction begin failed\n", s_sql_status);
		ret = -1;
	}
	else{
		s_sql_status = SQL_STATUS_TRANS;
		if(-1==openDatabase())
		{
			ERROROUT("Open database failed\n");
			s_sql_status = SQL_STATUS_IDLE;
			ret = -1;
		}
		else{
			ret = sqlite3_exec(g_db, "begin transaction", NULL, NULL, NULL);
			if(SQLITE_OK == ret){
				ret = 0;
			}
			else{
				DEBUG("sqlite3 errmsg: %s\n", sqlite3_errmsg(g_db));
				closeDatabase();
				s_sql_status = SQL_STATUS_IDLE;
				ret = -1;
			}
		}
	}
	
	return ret;
}
/*
 函数2：事务中的sqlite语句。
*/
int sqlite_transaction_exec(char *sqlite_cmd)
{
	if(NULL==sqlite_cmd || 0==strlen(sqlite_cmd)){
		DEBUG("invalid argument\n");
		return -1;
	}
	PRINTF("%s\n", sqlite_cmd);
	
	int ret = -1;
	
	if(SQL_STATUS_TRANS!=s_sql_status){
		DEBUG("s_sql_status=%d, failed\n", s_sql_status);
		ret = -1;
	}
	else{
		ret = sqlite3_exec(g_db, sqlite_cmd, NULL, NULL, NULL);
		if(SQLITE_OK == ret){
			ret = 0;
		}
		else{
			DEBUG("sqlite3 errmsg: %s\n", sqlite3_errmsg(g_db));
			ret = -1;
		}
	}
	
	return ret;
}
/*
 函数3：清理数据表，在事务中执行。
*/
int sqlite_transaction_table_clear(char *table_name)
{
	DEBUG("CAUTION: will clear table '%s'\n", table_name);
	char sqlite_cmd[256];	
	
	snprintf(sqlite_cmd,sizeof(sqlite_cmd),"DELETE FROM %s;", table_name);
	
	return sqlite_transaction_exec(sqlite_cmd);
}

/*
功能：	事务中执行SELECT语句。
注意：	事务中的读取仅能对当前数据库状态负责，因为事务提交后，此值有可能被改变。
		仅能读取单个字段字符串字段值。
输入：	sqlite_cmd				——sql SELECT语句
		receiver				——用于处理SELECT结果的参数，如果sqlite_read_callback为NULL，则receiver也可以为NULL
		receiver_size			——receiver的大小，在receiver为数组时应根据此值做安全拷贝
		sqlite_read_callback	——用于处理SELECT结果的回调，如果只是想知道查询到几条记录，则此回调可以为NULL
返回：	-1——失败；其他值——查询到的记录数
*/
int sqlite_transaction_read(char *sqlite_cmd, void *receiver, unsigned int receiver_size/*, int (*sqlite_read_callback)(char **result, int row, int column, void *receiver, unsigned int receiver_size)*/)
{
	char* errmsg=NULL;
	char** l_result = NULL;
	int l_row = 0;
	int l_column = 0;
	int ret = 0;
	int (*sqlite_callback)(char **,int,int,void *,unsigned int) = str_read_cb;	/*sqlite_read_callback;*/

	if(NULL==sqlite_cmd || 0==strlen(sqlite_cmd)){
		DEBUG("invalid argument\n");
		return -1;
	}
	//DEBUG("%s\n", sqlite_cmd);
	
	if(SQL_STATUS_TRANS!=s_sql_status){
		DEBUG("s_sql_status=%d, failed\n", s_sql_status);
		ret = -1;
	}
	else{
		if(sqlite3_get_table(g_db,sqlite_cmd,&l_result,&l_row,&l_column,&errmsg)
			|| NULL!=errmsg)
		{
			ERROROUT("sqlite cmd: %s\n", sqlite_cmd);
			DEBUG("errmsg: %s\n", errmsg);
			ret = -1;
		}
		else{ // inquire table ok
			if(0==l_row){
				DEBUG("no row, l_row=0, l_column=%d\n", l_column);
			}
			else{
				//DEBUG("sqlite select OK, %s\n", NULL==sqlite_callback?"no callback fun":"do callback fun");
				if(sqlite_callback && receiver)
					sqlite_callback(l_result, l_row, l_column, receiver, receiver_size);
				else{
					DEBUG("l_row=%d, l_column=%d\n", l_row, l_column);
				}
			}
			ret = l_row;
		}
		sqlite3_free_table(l_result);
		sqlite3_free(errmsg);
	}
	
	return ret;
}

/*
 函数4：事务结束。
 参数：commin_flag——0：提交事务；-1：回滚事务。
*/
int sqlite_transaction_end(int commit_flag)
{
	int ret = -1;
	
	PRINTF("sqlite_transaction_end %s\n", 1==commit_flag?"commit <<>>":"<< rollback");
	if(SQL_STATUS_TRANS!=s_sql_status){
		DEBUG("s_sql_status=%d, failed\n", s_sql_status);
		ret = -1;
	}
	else{
		if(1==commit_flag){
			PRINTF("commit transaction\n");
			ret = sqlite3_exec(g_db, "commit transaction", NULL, NULL, NULL);
		}
		else{
			PRINTF("rollback transaction\n");
			ret = sqlite3_exec(g_db, "rollback transaction", NULL, NULL, NULL);
		}
		
		if(SQLITE_OK == ret){
			ret = 0;
		}
		else{
			DEBUG("%s\n", sqlite3_errmsg(g_db));
			ret = -1;
		}
		closeDatabase();
		
		s_sql_status = SQL_STATUS_IDLE;
	}
	
	return ret;
}

/*
 从指定的表中读取单个字段字符串
 buf由调用者提供，并保障初始化正确。
 读取到一条记录返回0，否则返回-1。
*/
int str_sqlite_read(char *buf, unsigned int buf_size, char *sql_cmd)
{
	if(NULL==buf || NULL==sql_cmd || 0==strlen(sql_cmd) || 0==buf_size){
		DEBUG("some args are invalid\n");
		return -1;
	}
	
	int (*sqlite_cb)(char **, int, int, void *, unsigned int) = str_read_cb;

	int ret_sqlexec = sqlite_read(sql_cmd, buf, buf_size, sqlite_cb);
	if(ret_sqlexec<=0){
		DEBUG("read nothing for %s\n", sql_cmd);
		return -1;
	}
	else{
		//DEBUG("read %s for %s\n", buf,sql_cmd);
		return 0;
	}
}

static int check_record_in_trans(char *table_name, char *column_name, char *column_value)
{
	if(NULL==table_name || 0==strlen(table_name) || NULL==column_name || 0==strlen(column_name))
		return -1;
	
	char read_column_value[64];
	char sqlite_cmd[512];
	snprintf(sqlite_cmd,sizeof(sqlite_cmd),"SELECT %s FROM %s WHERE %s='%s';", column_name, table_name, column_name, column_value);
	memset(read_column_value,0,sizeof(read_column_value));
	if(0<sqlite_transaction_read(sqlite_cmd,read_column_value,sizeof(read_column_value))){
		//DEBUG("%s has %s=%s already\n", table_name, column_name, column_value);
		return 0;
	}
	else{
		DEBUG("%s has NO %s=%s\n", table_name, column_name, column_value);
		return -1;
	}
}

/*
 本地栏目的初始化
 注意和下发的Column.xml字段识别保持一致
*/
int localcolumn_init()
{
	DEBUG("init local column, such as 'Settings' or 'My Center\n");
	int insert_column_cnt = 0;
	
	if(-1==sqlite_transaction_begin())
		return -1;
	
	char sqlite_cmd[512];
	
	/*
	 一级菜单“个人中心”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","L98")){
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%s','%s','%s','%s','%s','%s','%s',98);",
			"L98","-1","L98","L98","LocalColumnIcon/MyCenter_losefocus.png","LocalColumnIcon/MyCenter_losefocus.png","98");
		sqlite_transaction_exec(sqlite_cmd);
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s');",
			"Column","L98",CURLANGUAGE_DFT,"DisplayName","个人中心","");
		sqlite_transaction_exec(sqlite_cmd);
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s');",
			"Column","L98","eng","DisplayName","My Center","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	/*
	 二级菜单“选择接收”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","L9801")){
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%s','%s','%s','%s','%s','%s','%s',1);",
			"L9801","L98","L98/L9801","L98","LocalColumnIcon/Receiving_losefocus.png","LocalColumnIcon/Receiving_losefocus.png","1");
		sqlite_transaction_exec(sqlite_cmd);
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s');",
			"Column","L9801",CURLANGUAGE_DFT,"DisplayName","选择接收","");
		sqlite_transaction_exec(sqlite_cmd);
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s');",
			"Column","L9801","eng","DisplayName","Receiving","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	/*
	 二级菜单“下载状态”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","L9802")){
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%s','%s','%s','%s','%s','%s','%s',2);",
			"L9802","L98","L98/L9802","L98","LocalColumnIcon/Download_losefocus.png","LocalColumnIcon/Download_losefocus.png","2");
		sqlite_transaction_exec(sqlite_cmd);
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s');",
			"Column","L9802",CURLANGUAGE_DFT,"DisplayName","下载状态","");
		sqlite_transaction_exec(sqlite_cmd);
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s');",
			"Column","L9802","eng","DisplayName","Download","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	
	
	/*
	 一级菜单“设置”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","L99")){
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%s','%s','%s','%s','%s','%s','%s',99);",
			"L99","-1","L99","L99","LocalColumnIcon/Setting_losefocus.png","LocalColumnIcon/Setting_losefocus.png","99");
		sqlite_transaction_exec(sqlite_cmd);
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s');",
			"Column","L99",CURLANGUAGE_DFT,"DisplayName","设置","");
		sqlite_transaction_exec(sqlite_cmd);
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s');",
			"Column","L99","eng","DisplayName","Setting","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	/*
	 二级菜单“设置－基本信息”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","L9901")){
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%s','%s','%s','%s','%s','%s','%s',1);",
			"L9901","L99","L99/L9901","L99","LocalColumnIcon/BasicInfo_losefocus.png","LocalColumnIcon/BasicInfo_losefocus.png","1");
		sqlite_transaction_exec(sqlite_cmd);
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s');",
			"Column","L9901",CURLANGUAGE_DFT,"DisplayName","基本信息","");
		sqlite_transaction_exec(sqlite_cmd);
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s');",
			"Column","L9901","eng","DisplayName","Basic Info","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	/*
	 二级菜单“设置－媒体设置”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","L9902")){
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%s','%s','%s','%s','%s','%s','%s',2);",
			"L9902","L99","L99/L9902","L99","LocalColumnIcon/Media_losefocus.png","LocalColumnIcon/Media_losefocus.png","2");
		sqlite_transaction_exec(sqlite_cmd);
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s');",
			"Column","L9902",CURLANGUAGE_DFT,"DisplayName","媒体设置","");
		sqlite_transaction_exec(sqlite_cmd);
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s');",
			"Column","L9902","eng","DisplayName","Media","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	/*
	 二级菜单“设置－网络设置”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","L9903")){
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%s','%s','%s','%s','%s','%s','%s',3);",
			"L9903","L99","L99/L9903","L99","LocalColumnIcon/Network_losefocus.png","LocalColumnIcon/Network_losefocus.png","3");
		sqlite_transaction_exec(sqlite_cmd);
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s');",
			"Column","L9903",CURLANGUAGE_DFT,"DisplayName","网络设置","");
		sqlite_transaction_exec(sqlite_cmd);
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s');",
			"Column","L9903","eng","DisplayName","Network","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	/*
	 二级菜单“设置－文件浏览”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","L9904")){
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%s','%s','%s','%s','%s','%s','%s',4);",
			"L9904","L99","L99/L9904","L99","LocalColumnIcon/FileBrowser_losefocus.png","LocalColumnIcon/FileBrowser_losefocus.png","4");
		sqlite_transaction_exec(sqlite_cmd);
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s');",
			"Column","L9904",CURLANGUAGE_DFT,"DisplayName","文件浏览","");
		sqlite_transaction_exec(sqlite_cmd);
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s');",
			"Column","L9904","eng","DisplayName","FileBrowser","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	/*
	 二级菜单“设置－高级设置”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","L9905")){
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%s','%s','%s','%s','%s','%s','%s',5);",
			"L9905","L99","L99/L9905","L99","LocalColumnIcon/Setting_losefocus.png","LocalColumnIcon/Setting_losefocus.png","5");
		sqlite_transaction_exec(sqlite_cmd);
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s');",
			"Column","L9905",CURLANGUAGE_DFT,"DisplayName","高级设置","");
		sqlite_transaction_exec(sqlite_cmd);
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s');",
			"Column","L9905","eng","DisplayName","Advance","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	/*
	 二级菜单“设置－DRM”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","L9906")){
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%s','%s','%s','%s','%s','%s','%s',6);",
			"L9906","L99","L99/L9906","L99","LocalColumnIcon/DefaultIcon_losefocus.png","LocalColumnIcon/DefaultIcon_losefocus.png","6");
		sqlite_transaction_exec(sqlite_cmd);
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s');",
			"Column","L9906",CURLANGUAGE_DFT,"DisplayName","DRM","");
		sqlite_transaction_exec(sqlite_cmd);
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s');",
			"Column","L9906","eng","DisplayName","DRM","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	
	
	/*
	 一级菜单“智能用电”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","G1")){
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%s','%s','%s','%s','%s','%s','%s',1);",
			"G1","-1","G1","SmartLife","LocalColumnIcon/SmartPower_losefocus.png","LocalColumnIcon/SmartPower_losefocus.png","1");
		sqlite_transaction_exec(sqlite_cmd);
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s');",
			"Column","G1",CURLANGUAGE_DFT,"DisplayName","智能用电","");
		sqlite_transaction_exec(sqlite_cmd);
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s');",
			"Column","G1","eng","DisplayName","SmartPower","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	/*
	 二级菜单“我的用电”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","G101")){
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%s','%s','%s','%s','%s','%s','%s',1);",
			"G101","G1","G101/G1","SmartLife","LocalColumnIcon/SmartPower_losefocus.png","LocalColumnIcon/SmartPower_losefocus.png","1");
		sqlite_transaction_exec(sqlite_cmd);
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s');",
			"Column","G101",CURLANGUAGE_DFT,"DisplayName","我的用电","");
		sqlite_transaction_exec(sqlite_cmd);
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s');",
			"Column","G101","eng","DisplayName","MyPower","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}	
	/*
	 二级菜单“用电账单”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","G102")){
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%s','%s','%s','%s','%s','%s','%s',1);",
			"G102","G1","G102/G1","SmartLife","LocalColumnIcon/SmartPower_losefocus.png","LocalColumnIcon/SmartPower_losefocus.png","2");
		sqlite_transaction_exec(sqlite_cmd);
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s');",
			"Column","G102",CURLANGUAGE_DFT,"DisplayName","用电账单","");
		sqlite_transaction_exec(sqlite_cmd);
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s');",
			"Column","G102","eng","DisplayName","MyBill","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
#if 0
	/*
	 二级菜单“缴费记录”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","G103")){
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%s','%s','%s','%s','%s','%s','%s',1);",
			"G103","G1","G103/G1","SmartLife","LocalColumnIcon/SmartPower_losefocus.png","LocalColumnIcon/SmartPower_losefocus.png","3");
		sqlite_transaction_exec(sqlite_cmd);
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s');",
			"Column","G103",CURLANGUAGE_DFT,"DisplayName","缴费记录","");
		sqlite_transaction_exec(sqlite_cmd);
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s');",
			"Column","G103","eng","DisplayName","PaymentRecord","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
#endif
	/*
	 二级菜单“电力公告”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","G104")){
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%s','%s','%s','%s','%s','%s','%s',1);",
			"G104","G1","G104/G1","SmartLife","LocalColumnIcon/SmartPower_losefocus.png","LocalColumnIcon/SmartPower_losefocus.png","4");
		sqlite_transaction_exec(sqlite_cmd);
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s');",
			"Column","G104",CURLANGUAGE_DFT,"DisplayName","电力公告","");
		sqlite_transaction_exec(sqlite_cmd);
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s');",
			"Column","G104","eng","DisplayName","Announcement","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	/*
	 二级菜单“营业网点”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","G105")){
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%s','%s','%s','%s','%s','%s','%s',1);",
			"G105","G1","G105/G1","SmartLife","LocalColumnIcon/SmartPower_losefocus.png","LocalColumnIcon/SmartPower_losefocus.png","5");
		sqlite_transaction_exec(sqlite_cmd);
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s');",
			"Column","G105",CURLANGUAGE_DFT,"DisplayName","营业网点","");
		sqlite_transaction_exec(sqlite_cmd);
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s');",
			"Column","G105","eng","DisplayName","BusinessOutlets","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	
		
	/*
	 一级菜单“家庭能效”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","G2")){
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%s','%s','%s','%s','%s','%s','%s',2);",
			"G2","-1","G2","SmartLife","LocalColumnIcon/PowerEfficiency_losefocus.png","LocalColumnIcon/PowerEfficiency_losefocus.png","2");
		sqlite_transaction_exec(sqlite_cmd);
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s');",
			"Column","G2",CURLANGUAGE_DFT,"DisplayName","家庭能效","");
		sqlite_transaction_exec(sqlite_cmd);
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s');",
			"Column","G2","eng","DisplayName","PowerEfficiency","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	
	
	/*
	 一级菜单“智能家居”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","G3")){
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%s','%s','%s','%s','%s','%s','%s',3);",
			"G3","-1","G3","SmartLife","LocalColumnIcon/SmartHousehold_losefocus.png","LocalColumnIcon/SmartHousehold_losefocus.png","3");
		sqlite_transaction_exec(sqlite_cmd);
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s');",
			"Column","G3",CURLANGUAGE_DFT,"DisplayName","智能家居","");
		sqlite_transaction_exec(sqlite_cmd);
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s');",
			"Column","G3","eng","DisplayName","SmartHousehold","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	
	
	/*
	 一级菜单“国网动态”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","G4")){
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%s','%s','%s','%s','%s','%s','%s',4);",
			"G4","-1","G4","SmartLife","LocalColumnIcon/StateGridNews_losefocus.png","LocalColumnIcon/StateGridNews_losefocus.png","4");
		sqlite_transaction_exec(sqlite_cmd);
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s');",
			"Column","G4",CURLANGUAGE_DFT,"DisplayName","国网动态","");
		sqlite_transaction_exec(sqlite_cmd);
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%s','%s','%s','%s','%s','%s');",
			"Column","G4","eng","DisplayName","StateGridNews","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	
	if(insert_column_cnt>0)
		return sqlite_transaction_end(1);
	else
		return sqlite_transaction_end(0);
}

static int global_info_init()
{
	DEBUG("init table 'Global', set default records\n");
	
	if(-1==sqlite_transaction_begin())
		return -1;
	
	int insert_record_cnt = 0;
	char key_value[128];
	char sqlite_cmd[1024];
	
	snprintf(key_value,sizeof(key_value),"%s",GLB_NAME_PREVIEWPATH);
	if(-1==check_record_in_trans("Global","Name",key_value)){
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Global(Name,Value,Param) VALUES('%s','%s','');",
			GLB_NAME_PREVIEWPATH,DBSTAR_PREVIEWPATH);
		sqlite_transaction_exec(sqlite_cmd);
		insert_record_cnt ++;
	}
	
	snprintf(key_value,sizeof(key_value),"%s",GLB_NAME_CURLANGUAGE);
	if(-1==check_record_in_trans("Global","Name",key_value)){
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Global(Name,Value,Param) VALUES('%s','%s','');",
			GLB_NAME_CURLANGUAGE,CURLANGUAGE_DFT);
		sqlite_transaction_exec(sqlite_cmd);
		insert_record_cnt ++;
	}
	
	snprintf(key_value,sizeof(key_value),"%s",GLB_NAME_DEVICEMODEL);
	if(-1==check_record_in_trans("Global","Name",key_value)){
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Global(Name,Value,Param) VALUES('%s','%s','');",
			GLB_NAME_DEVICEMODEL,DEVICEMODEL_DFT);
		sqlite_transaction_exec(sqlite_cmd);
		insert_record_cnt ++;
	}
	
	snprintf(key_value,sizeof(key_value),"%s",GLB_NAME_DBDATASERVERIP);
	if(-1==check_record_in_trans("Global","Name",key_value)){
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Global(Name,Value,Param) VALUES('%s','%s','');",
			GLB_NAME_DBDATASERVERIP,DBDATASERVERIP_DFT);
		sqlite_transaction_exec(sqlite_cmd);
		insert_record_cnt ++;
	}
	
	snprintf(key_value,sizeof(key_value),"%s",GLB_NAME_DBDATASERVERPORT);
	if(-1==check_record_in_trans("Global","Name",key_value)){
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Global(Name,Value,Param) VALUES('%s','%s','');",
			GLB_NAME_DBDATASERVERPORT,DBDATASERVERPORT_DFT);
		sqlite_transaction_exec(sqlite_cmd);
		insert_record_cnt ++;
	}
	
	snprintf(key_value,sizeof(key_value),"%s",GLB_NAME_HDFOREWARNING);
	if(-1==check_record_in_trans("Global","Name",key_value)){
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Global(Name,Value,Param) VALUES('%s','%s','');",
			GLB_NAME_HDFOREWARNING,HDFOREWARNING_DFT);
		sqlite_transaction_exec(sqlite_cmd);
		insert_record_cnt ++;
	}
	
	if(-1==check_record_in_trans("Global","Name","ColumnIconDft")){
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "REPLACE INTO Global(Name,Value,Param) VALUES('ColumnIconDft','LocalColumnIcon/DefaultIcon_losefocus.png','');");
		sqlite_transaction_exec(sqlite_cmd);
		insert_record_cnt ++;
	}
	
	if(insert_record_cnt>0)
		return sqlite_transaction_end(1);
	else
		return sqlite_transaction_end(0);
}
