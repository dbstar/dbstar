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
				
				// 记录Initialize.xml中所有的特殊产品ID，用于判断智能卡中的那些ID号是特殊产品
				createtable_ret = createTable("SpecialProduct");
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
	sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"SELECT name FROM sqlite_master WHERE type='table' AND name='%q';", name);
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
				sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,\
					"CREATE TABLE %q(\
Name	NVARCHAR(64) PRIMARY KEY,\
Value	NVARCHAR(128) DEFAULT '',\
Param	NVARCHAR(1024) DEFAULT '');", name);
			}
			else if(!strcmp(name,"Initialize"))
			{
				sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,\
					"CREATE TABLE %q(\
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
				sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,\
					"CREATE TABLE %q(\
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
				sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,\
					"CREATE TABLE %q(\
ServiceID	NVARCHAR(64) PRIMARY KEY,\
RegionCode	NVARCHAR(64) DEFAULT '',\
OnlineTime	DATETIME DEFAULT '',\
OfflineTime	DATETIME DEFAULT '',\
Status	RCHAR(32) DEFAULT '0',\
TimeStamp NOT NULL DEFAULT (datetime('now','localtime')));", name);
			}
			else if(!strcmp(name,"ResStr"))
			{
				sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,\
					"CREATE TABLE %q(\
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
				sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,\
					"CREATE TABLE %q(\
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
				sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,\
					"CREATE TABLE %q(\
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
				sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,\
					"CREATE TABLE %q(\
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
				sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,\
					"CREATE TABLE %q(\
ServiceID	NVARCHAR(64) DEFAULT '0',\
ObjectName	NVARCHAR(256) DEFAULT '',\
EntityID	NVARCHAR(128) DEFAULT '',\
Name	NVARCHAR(64) DEFAULT '',\
Type	NVARCHAR(64) DEFAULT '',\
PRIMARY KEY (ServiceID,ObjectName,EntityID,Name));", name);
			}
			else if(!strcmp(name,"ResExtensionFile"))
			{
				sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,\
					"CREATE TABLE %q(\
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
				sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,\
					"CREATE TABLE %q(\
ServiceID	NVARCHAR(64) DEFAULT '0',\
ColumnID	NVARCHAR(64) DEFAULT '',\
ParentID	NVARCHAR(64) DEFAULT '',\
Path	NVARCHAR(256) DEFAULT '',\
ColumnType	NVARCHAR(256) DEFAULT '',\
ColumnIcon_losefocus	NVARCHAR(256) DEFAULT '',\
ColumnIcon_getfocus	NVARCHAR(256) DEFAULT '',\
ColumnIcon_onclick	NVARCHAR(256) DEFAULT '',\
ColumnIcon_spare	NVARCHAR(256) DEFAULT '',\
SequenceNum	INTEGER DEFAULT 100,\
URI	NVARCHAR(256) DEFAULT '',\
Visible	CHAR(32) DEFAULT '1',\
Favorite NVARCHAR(32) DEFAULT '0',\
Param	NVARCHAR(1024) DEFAULT '',\
TimeStamp NOT NULL DEFAULT (datetime('now','localtime')),\
PRIMARY KEY (ServiceID,ColumnID));", name);
			}
			else if(!strcmp(name,"ColumnEntity"))
			{
				sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,\
					"CREATE TABLE %q(\
ServiceID	NVARCHAR(64) DEFAULT '0',\
ColumnID	NVARCHAR(64) DEFAULT '',\
EntityID	NVARCHAR(64) DEFAULT '',\
EntityType	NVARCHAR(64) DEFAULT '',\
TimeStamp NOT NULL DEFAULT (datetime('now','localtime')));", name);
			}
			else if(!strcmp(name,"Product"))
			{
				sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,\
					"CREATE TABLE %q(\
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
				sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,\
					"CREATE TABLE %q(\
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
				sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,\
					"CREATE TABLE %q(\
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
				sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,\
					"CREATE TABLE %q(\
ServiceID	NVARCHAR(64) DEFAULT '0',\
PublicationID	NVARCHAR(64) DEFAULT '',\
ColumnID	NVARCHAR(64) DEFAULT '',\
PublicationType	NVARCHAR(64) DEFAULT '',\
ProductID	NVARCHAR(64) DEFAULT '',\
URI	NVARCHAR(256) DEFAULT '',\
DescURI	NVARCHAR(512) DEFAULT '',\
TotalSize	NVARCHAR(64) DEFAULT '',\
ProductDescID	NVARCHAR(64) DEFAULT '',\
ReceiveStatus	NVARCHAR(64) DEFAULT '0',\
PushStartTime	DATETIME DEFAULT '',\
PushEndTime	DATETIME DEFAULT '',\
IsReserved	CHAR(32) DEFAULT '0',\
Visible	CHAR(32) DEFAULT '1',\
DRMFile	NVARCHAR(512) DEFAULT '',\
SetID	NVARCHAR(64) DEFAULT '',\
SetName	NVARCHAR(512) DEFAULT '',\
SetDesc	NVARCHAR(1024) DEFAULT '',\
SetPosterID	NVARCHAR(64) DEFAULT '',\
SetPosterName	NVARCHAR(512) DEFAULT '',\
SetPosterURI	NVARCHAR(512) DEFAULT '',\
IndexInSet	NVARCHAR(32) DEFAULT '',\
Favorite	NVARCHAR(32) DEFAULT '0',\
Bookmark	NVARCHAR(32) DEFAULT '0',\
IsAuthorized	NVARCHAR(64) DEFAULT '',\
VODNum	NVARCHAR(64) DEFAULT '',\
VODPlatform	NVARCHAR(256) DEFAULT '',\
Deleted NVARCHAR(256) DEFAULT '0',\
FileID	NVARCHAR(64) DEFAULT '',\
FileSize	NVARCHAR(64) DEFAULT '',\
FileURI	NVARCHAR(512) DEFAULT '',\
FileType	NVARCHAR(64) DEFAULT '',\
FileFormat	NVARCHAR(32) DEFAULT '',\
Duration	NVARCHAR(32) DEFAULT '',\
Resolution	NVARCHAR(32) DEFAULT '',\
BitRate	NVARCHAR(32) DEFAULT '',\
CodeFormat	NVARCHAR(32) DEFAULT '',\
Preference	NVARCHAR(32) DEFAULT '',\
AccessTime	NOT NULL DEFAULT (datetime('now','localtime')),\
TimeStamp NOT NULL DEFAULT (datetime('now','localtime')),\
PRIMARY KEY (ServiceID,PublicationID,ColumnID));", name);
			}
			else if(!strcmp(name,"MultipleLanguageInfoVA"))
			{
				sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,\
					"CREATE TABLE %q(\
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
				sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,\
					"CREATE TABLE %q(\
ServiceID	NVARCHAR(64) DEFAULT '0',\
PublicationID	NVARCHAR(64) DEFAULT '',\
infolang	NVARCHAR(64) DEFAULT 'cho',\
PublishID	NVARCHAR(64) DEFAULT '',\
RMCategory	NVARCHAR(32) DEFAULT '',\
Author	NVARCHAR(512) DEFAULT '',\
Publisher	NVARCHAR(512) DEFAULT '',\
Issue	NVARCHAR(64) DEFAULT '',\
Keywords	NVARCHAR(512) DEFAULT '',\
Description	NVARCHAR(1024) DEFAULT '',\
PublishDate	NVARCHAR(64) DEFAULT '',\
PublishWeek	NVARCHAR(32) DEFAULT '',\
PublishPlace	NVARCHAR(256) DEFAULT '',\
CopyrightInfo	NVARCHAR(256) DEFAULT '',\
TotalEdition	NVARCHAR(64) DEFAULT '',\
Data	NVARCHAR(64) DEFAULT '',\
Format	NVARCHAR(64) DEFAULT '',\
TotalIssue	NVARCHAR(64) DEFAULT '',\
Recommendation	NVARCHAR(1024) DEFAULT '',\
PRIMARY KEY (ServiceID,PublicationID,infolang));", name);
			}
			else if(!strcmp(name,"MultipleLanguageInfoApp"))
			{
				sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,\
					"CREATE TABLE %q(\
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
				sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,\
					"CREATE TABLE %q(\
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
				sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,\
					"CREATE TABLE %q(\
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
				sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,\
					"CREATE TABLE %q(\
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
				sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,\
					"CREATE TABLE %q(\
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
				sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,\
					"CREATE TABLE %q(\
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
				sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,\
					"CREATE TABLE %q(\
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
				sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,\
					"CREATE TABLE %q(\
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
			
			// 目前实际使用的只有m_ID和ServiceID字段
			else if(!strcmp(name,"SpecialProduct"))
			{
				sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,\
					"CREATE TABLE %q(\
SmartCardID		NVARCHAR(64) DEFAULT '',\
m_OperatorID	NVARCHAR(64) DEFAULT '',\
m_ID	NVARCHAR(64) DEFAULT '',\
m_ProductStartTime	NVARCHAR(64) DEFAULT '',\
m_ProductEndTime	NVARCHAR(64) DEFAULT '',\
m_WatchStartTime	NVARCHAR(64) DEFAULT '',\
m_WatchEndTime	NVARCHAR(64) DEFAULT '',\
m_LimitTotaltValue	NVARCHAR(64) DEFAULT '',\
m_LimitUsedValue	NVARCHAR(64) DEFAULT '',\
ServiceID		NVARCHAR(64) DEFAULT '',\
TimeStamp NOT NULL DEFAULT (datetime('now','localtime')),\
PRIMARY KEY (m_ID));", name);
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
			global_info_init(0);
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
	DEBUG("waiting_cnt=%d, %s\n", waiting_cnt, exec_str);
	
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
//					DEBUG("sqlite select OK, %s\n", NULL==sqlite_callback?"no callback fun":"do callback fun");
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
	sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"DELETE FROM %q;", table_name);
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
//	PRINTF("%s\n", sqlite_cmd);
	
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
			DEBUG("sqlite3 [%s]\nERRMSG!!!: %s\n", sqlite_cmd,sqlite3_errmsg(g_db));
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
	
	sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"DELETE FROM %q;", table_name);
	
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
	
	PRINTF("sqlite_transaction_end %d %s\n", s_sql_status, 1==commit_flag?"commit <<>>":"<< rollback");
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
	PRINTF("sqlite_transaction_end %d\n", s_sql_status);
	
	
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
	sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"SELECT %q FROM %q WHERE %q='%q';", column_name, table_name, column_name, column_value);
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

// 从确定目录拷贝指定栏目图标到确定目录
static int columnicon_init(char *columnicon_name)
{
	char from_file[256];
	char to_file[256];
	
	snprintf(from_file,sizeof(from_file),"%s/%s", LOCAL_COLUMNICON_ORIGIN_DIR,columnicon_name);
	snprintf(to_file,sizeof(to_file),"%s/LocalColumnIcon/%s",column_res_get(),columnicon_name);
	
	if(NULL!=columnicon_name && 0==fcopy_c(from_file,to_file)){
		DEBUG("copy %s to %s success\n",from_file,to_file);
		return 0;
	}
	else{
		DEBUG("copy %s to %s failed\n",from_file,to_file);
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
	char localcolumn_iconname[128];
	
	if(-1==sqlite_transaction_begin())
		return -1;
	
	char sqlite_cmd[512];
	
	
	/*
	 一级菜单“CNTV”
	*/
	if(-1!=check_record_in_trans("Column","ColumnID","CNTV")){
		DEBUG("change ColumnID of CNTV from \'CNTV\' to \'L97\', so delete \'CNTV\' firstly\n");
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"DELETE FROM Column WHERE ColumnID='CNTV';");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	
	if(-1==check_record_in_trans("Column","ColumnID","L97")){
#ifdef CNTV_LC
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%q','%q','%q','%q','%q','%q','%q',10000);",
			"L97","-1","L97","L97","LocalColumnIcon/DefaultIcon_losefocus.png","LocalColumnIcon/DefaultIcon_losefocus.png","LocalColumnIcon/DefaultIcon_losefocus.png");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","L97",CURLANGUAGE_DFT,"DisplayName","CNTV","");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","L97","eng","DisplayName","CNTV","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
#endif
	}
	else{
#ifdef CNTV_LC
#else
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"DELETE FROM Column WHERE ColumnID='L97' or ColumnID='CNTV';");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
#endif
	}
	
	/*
	 一级菜单“个人中心”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","L98")){
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%q','%q','%q','%q','%q','%q','%q',10001);",
			"L98","-1","L98","L98","LocalColumnIcon/MyCenter_losefocus.png","LocalColumnIcon/MyCenter_losefocus.png","LocalColumnIcon/MyCenter_losefocus.png");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","L98",CURLANGUAGE_DFT,"DisplayName","个人中心","");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","L98","eng","DisplayName","My Center","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	
	/*
	 二级菜单“个人中心－基本信息”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","L9901")){
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%q','%q','%q','%q','%q','%q','%q',1);",
			"L9901","L98","L98/L9901","L99","LocalColumnIcon/BasicInfo_losefocus.png","LocalColumnIcon/BasicInfo_losefocus.png","LocalColumnIcon/BasicInfo_losefocus.png");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","L9901",CURLANGUAGE_DFT,"DisplayName","基本信息","");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","L9901","eng","DisplayName","Basic Info","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	else{
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"UPDATE Column SET ParentID='L98',Path='L98/L9901',ColumnType='L99',SequenceNum=1 WHERE ColumnID='L9901';");
		sqlite_transaction_exec(sqlite_cmd);
	}
	/*
	 二级菜单“个人中心－购买信息”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","L9907")){
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%q','%q','%q','%q','%q','%q','%q',2);",
			"L9907","L98","L98/L9907","L99","LocalColumnIcon/PurchaseInfo_losefocus.png","LocalColumnIcon/PurchaseInfo_losefocus.png","LocalColumnIcon/PurchaseInfo_losefocus.png");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","L9907",CURLANGUAGE_DFT,"DisplayName","购买信息","");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","L9907","eng","DisplayName","PurchaseInfo","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	else{
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"UPDATE Column SET ParentID='L98',Path='L98/L9907',ColumnType='L99',SequenceNum=2 WHERE ColumnID='L9907';");
		sqlite_transaction_exec(sqlite_cmd);
	}
	
	/*
	 二级菜单“选择接收”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","L9801")){
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%q','%q','%q','%q','%q','%q','%q',3);",
			"L9801","L98","L98/L9801","L98","LocalColumnIcon/Receiving_losefocus.png","LocalColumnIcon/Receiving_losefocus.png","LocalColumnIcon/Receiving_losefocus.png");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","L9801",CURLANGUAGE_DFT,"DisplayName","选择接收","");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","L9801","eng","DisplayName","Receiving","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	else{
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"UPDATE Column SET SequenceNum=3 WHERE ColumnID='L9801';");
		sqlite_transaction_exec(sqlite_cmd);
	}
	/*
	 二级菜单“下载状态”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","L9802")){
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%q','%q','%q','%q','%q','%q','%q',4);",
			"L9802","L98","L98/L9802","L98","LocalColumnIcon/Download_losefocus.png","LocalColumnIcon/Download_losefocus.png","LocalColumnIcon/Download_losefocus.png");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","L9802",CURLANGUAGE_DFT,"DisplayName","下载状态","");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","L9802","eng","DisplayName","Download","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	else{
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"UPDATE Column SET SequenceNum=4 WHERE ColumnID='L9802';");
		sqlite_transaction_exec(sqlite_cmd);
	}
	/*
	 二级菜单“富媒体分享”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","L9804")){
#ifdef MEDIASHARING_LC
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%q','%q','%q','%q','%q','%q','%q',10);",
			"L9804","L98","L98/L9804","L98","","","");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","L9804",CURLANGUAGE_DFT,"DisplayName","媒体分享","");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","L9804","eng","DisplayName","MediaSharing","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
#endif
	}
	else{
#ifdef MEDIASHARING_LC
#else
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"DELETE FROM Column WHERE ColumnID='L9804';");
		sqlite_transaction_exec(sqlite_cmd);
#endif
	}
	/*
	 二级菜单“文件浏览”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","L9805")){
#ifdef FILEBROWSER_LC
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%q','%q','%q','%q','%q','%q','%q',11);",
			"L9805","L98","L98/L9805","L98","LocalColumnIcon/DefaultIcon_losefocus.png","LocalColumnIcon/DefaultIcon_losefocus.png","LocalColumnIcon/DefaultIcon_losefocus.png");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","L9805",CURLANGUAGE_DFT,"DisplayName","文件浏览","");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","L9805","eng","DisplayName","FileBrowser","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
#endif
	}
	else{
#ifdef FILEBROWSER_LC
#else
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"DELETE FROM Column WHERE ColumnID='L9805';");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
#endif
	}
	/*
	 二级菜单“我的应用”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","L9806")){
#ifdef MYAPP_LC
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%q','%q','%q','%q','%q','%q','%q',12);",
			"L9806","L98","L98/L9806","L98","LocalColumnIcon/DefaultIcon_losefocus.png","LocalColumnIcon/DefaultIcon_losefocus.png","LocalColumnIcon/DefaultIcon_losefocus.png");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","L9806",CURLANGUAGE_DFT,"DisplayName","我的应用","");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","L9806","eng","DisplayName","MyApp","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
#endif
	}
	else{
#ifdef MYAPP_LC
#else
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"DELETE FROM Column WHERE ColumnID='L9806';");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
#endif
	}
	/*
	 二级菜单“浏览器”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","L9807")){
#ifdef WEBBROWSER_LC
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%q','%q','%q','%q','%q','%q','%q',13);",
			"L9807","L98","L98/L9807","L98","LocalColumnIcon/DefaultIcon_losefocus.png","LocalColumnIcon/DefaultIcon_losefocus.png","LocalColumnIcon/DefaultIcon_losefocus.png");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","L9807",CURLANGUAGE_DFT,"DisplayName","浏览器","");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","L9807","eng","DisplayName","WebBrowser","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
#endif
	}
	else{
#ifdef WEBBROWSER_LC
#else
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"DELETE FROM Column WHERE ColumnID='L9807';");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
#endif
	}
	/*
	 二级菜单“个人中心－帮助信息”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","L9808")){
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%q','%q','%q','%q','%q','%q','%q',20);",
			"L9908","L98","L98/L9908","L99","LocalColumnIcon/Help_losefocus.png","LocalColumnIcon/Help_losefocus.png","LocalColumnIcon/Help_losefocus.png");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","L9908",CURLANGUAGE_DFT,"DisplayName","帮助信息","");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","L9908","eng","DisplayName","Help","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	
	
	/*
	 一级菜单“设置”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","L99")){
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%q','%q','%q','%q','%q','%q','%q',10003);",
			"L99","-1","L99","L99","LocalColumnIcon/Setting_losefocus.png","LocalColumnIcon/Setting_losefocus.png","LocalColumnIcon/Setting_losefocus.png");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","L99",CURLANGUAGE_DFT,"DisplayName","设置","");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","L99","eng","DisplayName","Setting","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	/*
	 二级菜单“设置－媒体设置”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","L9902")){
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%q','%q','%q','%q','%q','%q','%q',2);",
			"L9902","L99","L99/L9902","L99","LocalColumnIcon/Media_losefocus.png","LocalColumnIcon/Media_losefocus.png","LocalColumnIcon/Media_losefocus.png");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","L9902",CURLANGUAGE_DFT,"DisplayName","媒体设置","");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","L9902","eng","DisplayName","Media","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	/*
	 二级菜单“设置－网络设置”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","L9903")){
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%q','%q','%q','%q','%q','%q','%q',3);",
			"L9903","L99","L99/L9903","L99","LocalColumnIcon/Network_losefocus.png","LocalColumnIcon/Network_losefocus.png","LocalColumnIcon/Network_losefocus.png");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","L9903",CURLANGUAGE_DFT,"DisplayName","网络设置","");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","L9903","eng","DisplayName","Network","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	/*
	 二级菜单“设置－高级设置”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","L9905")){
#if 0
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%q','%q','%q','%q','%q','%q','%q',5);",
			"L9905","L99","L99/L9905","L99","LocalColumnIcon/Setting_losefocus.png","LocalColumnIcon/Setting_losefocus.png","LocalColumnIcon/Setting_losefocus.png");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","L9905",CURLANGUAGE_DFT,"DisplayName","高级设置","");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","L9905","eng","DisplayName","Advance","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
#endif
	}
	else{
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"DELETE FROM Column WHERE ColumnID='L9905';");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	/*
	 二级菜单“设置－DRM” DRM_TEST
	*/
	if(-1==check_record_in_trans("Column","ColumnID","L9906")){
#ifdef DRM_TEST
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%q','%q','%q','%q','%q','%q','%q',6);",
			"L9906","L99","L99/L9906","L99","LocalColumnIcon/DefaultIcon_losefocus.png","LocalColumnIcon/DefaultIcon_losefocus.png","LocalColumnIcon/DefaultIcon_losefocus.png");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","L9906",CURLANGUAGE_DFT,"DisplayName","DRM","");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","L9906","eng","DisplayName","DRM","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
#endif
	}
	else{
#ifdef DRM_TEST
#else
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"DELETE FROM Column WHERE ColumnID='L9906';");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
#endif
	}
	/*
	 二级菜单“设置－用电目标”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","L9909")){
#ifdef SMARTLIFE_LC
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%q','%q','%q','%q','%q','%q','%q',9);",
			"L9909","L99","L99/L9909","L99","LocalColumnIcon/SmartPowerSettings_losefocus.png","LocalColumnIcon/SmartPowerSettings_losefocus.png","LocalColumnIcon/SmartPowerSettings_losefocus.png");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","L9909",CURLANGUAGE_DFT,"DisplayName","用电目标","");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","L9909","eng","DisplayName","PowerLimit","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
#endif
	}
	else{
#ifdef SMARTLIFE_LC
#else
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"DELETE FROM Column WHERE ColumnID='L9909';");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
#endif
	}
	
	
	/*
	 一级菜单“智能用电”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","G1")){
#ifdef SMARTLIFE_LC
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%q','%q','%q','%q','%q','%q','%q',1);",
			"G1","-1","G1","SmartLife","LocalColumnIcon/SmartPower_losefocus.png","LocalColumnIcon/SmartPower_losefocus.png","LocalColumnIcon/SmartPower_losefocus.png");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd, "REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","G1",CURLANGUAGE_DFT,"DisplayName","智能用电","");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","G1","eng","DisplayName","SmartPower","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
#endif
	}
	else{
#ifdef SMARTLIFE_LC
#else
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"DELETE FROM Column WHERE ColumnID='G1';");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
#endif
	}
	
	/*
	 二级菜单“我的用电”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","G101")){
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%q','%q','%q','%q','%q','%q','%q',1);",
			"G101","G1","G101/G1","SmartLife","LocalColumnIcon/SmartPower_losefocus.png","LocalColumnIcon/SmartPower_losefocus.png","LocalColumnIcon/SmartPower_losefocus.png");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","G101",CURLANGUAGE_DFT,"DisplayName","我的用电","");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","G101","eng","DisplayName","MyPower","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}	
	/*
	 二级菜单“用电账单”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","G102")){
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%q','%q','%q','%q','%q','%q','%q',2);",
			"G102","G1","G102/G1","SmartLife","LocalColumnIcon/SmartPower_losefocus.png","LocalColumnIcon/SmartPower_losefocus.png","LocalColumnIcon/SmartPower_losefocus.png");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","G102",CURLANGUAGE_DFT,"DisplayName","用电账单","");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","G102","eng","DisplayName","MyBill","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	
	/*
	 二级菜单“缴费记录”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","G103")){
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%q','%q','%q','%q','%q','%q','%q',3);",
			"G103","G1","G103/G1","SmartLife","LocalColumnIcon/SmartPower_losefocus.png","LocalColumnIcon/SmartPower_losefocus.png","LocalColumnIcon/SmartPower_losefocus.png");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","G103",CURLANGUAGE_DFT,"DisplayName","缴费记录","");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","G103","eng","DisplayName","PaymentRecord","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	
	/*
	 二级菜单“电力公告”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","G104")){
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%q','%q','%q','%q','%q','%q','%q',4);",
			"G104","G1","G104/G1","SmartLife","LocalColumnIcon/SmartPower_losefocus.png","LocalColumnIcon/SmartPower_losefocus.png","LocalColumnIcon/SmartPower_losefocus.png");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","G104",CURLANGUAGE_DFT,"DisplayName","电力公告","");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","G104","eng","DisplayName","Announcement","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	/*
	 二级菜单“营业网点”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","G105")){
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%q','%q','%q','%q','%q','%q','%q',5);",
			"G105","G1","G105/G1","SmartLife","LocalColumnIcon/SmartPower_losefocus.png","LocalColumnIcon/SmartPower_losefocus.png","LocalColumnIcon/SmartPower_losefocus.png");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","G105",CURLANGUAGE_DFT,"DisplayName","营业网点","");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","G105","eng","DisplayName","BusinessOutlets","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	
		
	/*
	 一级菜单“家庭能效”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","G2")){
#ifdef SMARTLIFE_LC
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%q','%q','%q','%q','%q','%q','%q',2);",
			"G2","-1","G2","SmartLife","LocalColumnIcon/PowerEfficiency_losefocus.png","LocalColumnIcon/PowerEfficiency_losefocus.png","LocalColumnIcon/PowerEfficiency_losefocus.png");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","G2",CURLANGUAGE_DFT,"DisplayName","家庭能效","");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","G2","eng","DisplayName","PowerEfficiency","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
#endif
	}
	else{
#ifdef SMARTLIFE_LC
#else
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"DELETE FROM Column WHERE ColumnID='G2';");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
#endif
	}
	
	/*
	 二级菜单“用电构成”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","G201")){
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%q','%q','%q','%q','%q','%q','%q',1);",
			"G201","G2","G2/G201","SmartLife","LocalColumnIcon/PowerEfficiency_losefocus.png","LocalColumnIcon/PowerEfficiency_losefocus.png","LocalColumnIcon/PowerEfficiency_losefocus.png");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","G201",CURLANGUAGE_DFT,"DisplayName","用电构成","");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","G201","eng","DisplayName","PowerProportion","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	
	/*
	 二级菜单“用电跟踪”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","G202")){
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%q','%q','%q','%q','%q','%q','%q',2);",
			"G202","G2","G2/G202","SmartLife","LocalColumnIcon/PowerEfficiency_losefocus.png","LocalColumnIcon/PowerEfficiency_losefocus.png","LocalColumnIcon/PowerEfficiency_losefocus.png");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","G202",CURLANGUAGE_DFT,"DisplayName","用电跟踪","");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","G202","eng","DisplayName","PowerHistory","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	
	/*
	 二级菜单“用电趋势”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","G203")){
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%q','%q','%q','%q','%q','%q','%q',3);",
			"G203","G2","G2/G203","SmartLife","LocalColumnIcon/PowerEfficiency_losefocus.png","LocalColumnIcon/PowerEfficiency_losefocus.png","LocalColumnIcon/PowerEfficiency_losefocus.png");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","G203",CURLANGUAGE_DFT,"DisplayName","用电趋势","");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","G203","eng","DisplayName","PowerHistory","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	
	/*
	 二级菜单“节能贴士”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","G204")){
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%q','%q','%q','%q','%q','%q','%q',4);",
			"G204","G2","G2/G204","SmartLife","LocalColumnIcon/PowerEfficiency_losefocus.png","LocalColumnIcon/PowerEfficiency_losefocus.png","LocalColumnIcon/PowerEfficiency_losefocus.png");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","G204",CURLANGUAGE_DFT,"DisplayName","节能贴士","");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","G204","eng","DisplayName","PowerTips","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	
	
	/*
	 一级菜单“智能家居”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","G3")){
#ifdef SMARTLIFE_LC
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%q','%q','%q','%q','%q','%q','%q',3);",
			"G3","-1","G3","SmartLife","LocalColumnIcon/SmartHousehold_losefocus.png","LocalColumnIcon/SmartHousehold_losefocus.png","LocalColumnIcon/SmartHousehold_losefocus.png");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","G3",CURLANGUAGE_DFT,"DisplayName","智能家居","");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","G3","eng","DisplayName","SmartHousehold","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
#endif
	}
	else{
#ifdef SMARTLIFE_LC
#else
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"DELETE FROM Column WHERE ColumnID='G3';");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
#endif
	}
	
	/*
	 二级菜单“我的电器”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","G301")){
		snprintf(localcolumn_iconname,sizeof(localcolumn_iconname),"MyAppliances_losefocus.png");
		columnicon_init(localcolumn_iconname);
		
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%q','%q','%q','%q','%q','%q','%q',1);",
			"G301","G3","G3/G301","SmartLife","LocalColumnIcon/MyAppliances_losefocus.png","LocalColumnIcon/MyAppliances_losefocus.png","LocalColumnIcon/MyAppliances_losefocus.png");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","G301",CURLANGUAGE_DFT,"DisplayName","我的电器","");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","G301","eng","DisplayName","MyAppliances","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	
	/*
	 二级菜单“一键控制”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","G302")){
		snprintf(localcolumn_iconname,sizeof(localcolumn_iconname),"ShortcutCtrl_losefocus.png");
		columnicon_init(localcolumn_iconname);
		
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%q','%q','%q','%q','%q','%q','%q',2);",
			"G302","G3","G3/G302","SmartLife","LocalColumnIcon/ShortcutCtrl_losefocus.png","LocalColumnIcon/ShortcutCtrl_losefocus.png","LocalColumnIcon/ShortcutCtrl_losefocus.png");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","G302",CURLANGUAGE_DFT,"DisplayName","一键控制","");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","G302","eng","DisplayName","ShortcutCtrl","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	
	/*
	 二级菜单“定时任务”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","G303")){
		snprintf(localcolumn_iconname,sizeof(localcolumn_iconname),"TimingTask_losefocus.png");
		columnicon_init(localcolumn_iconname);
		
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%q','%q','%q','%q','%q','%q','%q',3);",
			"G303","G3","G3/G303","SmartLife","LocalColumnIcon/TimingTask_losefocus.png","LocalColumnIcon/TimingTask_losefocus.png","LocalColumnIcon/TimingTask_losefocus.png");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","G303",CURLANGUAGE_DFT,"DisplayName","定时任务","");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","G303","eng","DisplayName","TimingTask","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	
	
	/*
	 一级菜单“国网动态”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","G4")){
#if 0
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%q','%q','%q','%q','%q','%q','%q',4);",
			"G4","-1","G4","SmartLife","LocalColumnIcon/StateGridNews_losefocus.png","LocalColumnIcon/StateGridNews_losefocus.png","LocalColumnIcon/StateGridNews_losefocus.png");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","G4",CURLANGUAGE_DFT,"DisplayName","国网动态","");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","G4","eng","DisplayName","StateGridNews","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
#endif
	}
	else{
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"DELETE FROM Column WHERE ColumnID='G4';");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	
	
	/*
	 一级菜单“国网资讯”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","G5")){
#ifdef SMARTLIFE_LC
		snprintf(localcolumn_iconname,sizeof(localcolumn_iconname),"GridInfos_losefocus.png");
		columnicon_init(localcolumn_iconname);
		
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%q','%q','%q','%q','%q','%q','%q',5);",
			"G5","-1","G5","SmartLife","LocalColumnIcon/GridInfos_losefocus.png","LocalColumnIcon/GridInfos_losefocus.png","LocalColumnIcon/GridInfos_losefocus.png");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","G5",CURLANGUAGE_DFT,"DisplayName","国网资讯","");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","G5","eng","DisplayName","GridInfos","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
#endif
	}
	else{
#ifdef SMARTLIFE_LC
#else
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"DELETE FROM Column WHERE ColumnID='G5';");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
#endif
	}
	
	/*
	 二级菜单“国网快讯”
	*/
	if(-1==check_record_in_trans("Column","ColumnID","G501")){
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Column(ColumnID,ParentID,Path,ColumnType,ColumnIcon_losefocus,ColumnIcon_getfocus,ColumnIcon_onclick,SequenceNum) VALUES('%q','%q','%q','%q','%q','%q','%q',1);",
			"G501","G5","G5/G501","SmartLife","LocalColumnIcon/GridInfos_losefocus.png","LocalColumnIcon/GridInfos_losefocus.png","LocalColumnIcon/GridInfos_losefocus.png");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","G501",CURLANGUAGE_DFT,"DisplayName","国网快讯","");
		sqlite_transaction_exec(sqlite_cmd);
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO ResStr(ObjectName,EntityID,StrLang,StrName,StrValue,Extension) VALUES('%q','%q','%q','%q','%q','%q');",
			"Column","G501","eng","DisplayName","GridNews","");
		sqlite_transaction_exec(sqlite_cmd);
		insert_column_cnt ++;
	}
	
	
	if(insert_column_cnt>0)
		return sqlite_transaction_end(1);
	else
		return sqlite_transaction_end(0);
}

int global_info_init(int force_reset)
{
	DEBUG("init table 'Global', set default records\n");
	
	if(-1==sqlite_transaction_begin())
		return -1;
	
	int insert_record_cnt = 0;
	char key_value[128];
	char sqlite_cmd[1024];
	
	snprintf(key_value,sizeof(key_value),"%s",GLB_NAME_PREVIEWPATH);
	if(1==force_reset || -1==check_record_in_trans("Global","Name",key_value)){
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Global(Name,Value,Param) VALUES('%q','%q','');",
			GLB_NAME_PREVIEWPATH,DBSTAR_PREVIEWPATH);
		sqlite_transaction_exec(sqlite_cmd);
		insert_record_cnt ++;
	}
	
	snprintf(key_value,sizeof(key_value),"%s",GLB_NAME_CURLANGUAGE);
	if(1==force_reset || -1==check_record_in_trans("Global","Name",key_value)){
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Global(Name,Value,Param) VALUES('%q','%q','');",
			GLB_NAME_CURLANGUAGE,CURLANGUAGE_DFT);
		sqlite_transaction_exec(sqlite_cmd);
		insert_record_cnt ++;
	}
	
	snprintf(key_value,sizeof(key_value),"%s",GLB_NAME_DEVICEMODEL);
	if(1==force_reset || -1==check_record_in_trans("Global","Name",key_value)){
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Global(Name,Value,Param) VALUES('%q','%q','');",
			GLB_NAME_DEVICEMODEL,DEVICEMODEL_DFT);
		sqlite_transaction_exec(sqlite_cmd);
		insert_record_cnt ++;
	}
	
	snprintf(key_value,sizeof(key_value),"%s",GLB_NAME_DBDATASERVERIP);
	if(1==force_reset || -1==check_record_in_trans("Global","Name",key_value)){
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Global(Name,Value,Param) VALUES('%q','%q','');",
			GLB_NAME_DBDATASERVERIP,DBDATASERVERIP_DFT);
		sqlite_transaction_exec(sqlite_cmd);
		insert_record_cnt ++;
	}
	
	snprintf(key_value,sizeof(key_value),"%s",GLB_NAME_DBDATASERVERPORT);
	if(1==force_reset || -1==check_record_in_trans("Global","Name",key_value)){
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Global(Name,Value,Param) VALUES('%q','%q','');",
			GLB_NAME_DBDATASERVERPORT,DBDATASERVERPORT_DFT);
		sqlite_transaction_exec(sqlite_cmd);
		insert_record_cnt ++;
	}
	
	snprintf(key_value,sizeof(key_value),"%s",GLB_NAME_HDFOREWARNING);
	if(1==force_reset || -1==check_record_in_trans("Global","Name",key_value)){
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Global(Name,Value,Param) VALUES('%q','%llu','');",
			GLB_NAME_HDFOREWARNING,HDFOREWARNING_M_DFT);
		sqlite_transaction_exec(sqlite_cmd);
		insert_record_cnt ++;
	}
	
	if(1==force_reset || -1==check_record_in_trans("Global","Name","ColumnIconDft")){
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Global(Name,Value,Param) VALUES('ColumnIconDft','LocalColumnIcon/DefaultIcon_losefocus.png','');");
		sqlite_transaction_exec(sqlite_cmd);
		insert_record_cnt ++;
	}
	
	
	snprintf(key_value,sizeof(key_value),"%s",GLB_NAME_TUNERARGS);
	if(1==force_reset || -1==check_record_in_trans("Global","Name",key_value)){
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Global(Name,Value,Param) VALUES('%s','%s','');",
			GLB_NAME_TUNERARGS,TUNERARGS_DFT);
		sqlite_transaction_exec(sqlite_cmd);
		insert_record_cnt ++;
	}
	
	snprintf(key_value,sizeof(key_value),"%s",GLB_NAME_TUNERARGS_DFT);
	if(1==force_reset || -1==check_record_in_trans("Global","Name",key_value)){
		sqlite3_snprintf(sizeof(sqlite_cmd),sqlite_cmd,"REPLACE INTO Global(Name,Value,Param) VALUES('%s','%s','');",
			GLB_NAME_TUNERARGS_DFT,TUNERARGS_DFT);
		sqlite_transaction_exec(sqlite_cmd);
		insert_record_cnt ++;
	}
	
	if(insert_record_cnt>0){
		return sqlite_transaction_end(1);
	}
	else{
		DEBUG("no global default setting need save");
		return sqlite_transaction_end(0);
	}
}


int smarthome_setting_reset(char *sqlite_cmd)
{
	char* errmsg=NULL;
	int ret = -1;
	sqlite3* smarthome_db = NULL;
	
	if(SQLITE_OK!=sqlite3_open(SMARTHOME_DATABASE,&smarthome_db)){
		ERROROUT("can't open database\n");
		ret = -1;
	}
	else{
		ret = 0;

		if(sqlite3_exec(smarthome_db,sqlite_cmd,NULL,NULL,&errmsg)){
			DEBUG("sqlite3 errmsg: %s\n", errmsg);
			ret = -1;
		}
		else{
			DEBUG("sqlite3 %s success\n", sqlite_cmd);
			ret = 0;
		}
		
		sqlite3_free(errmsg);								///	release the memery possessed by error message
		sqlite3_close(smarthome_db);
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
int smartlife_sqlite_read(char *sqlite_cmd, void *receiver, unsigned int receiver_size, int (*sqlite_read_callback)(char **result, int row, int column, void *receiver, unsigned int receiver_size))
{
	char* errmsg=NULL;
	char** l_result = NULL;
	int l_row = 0;
	int l_column = 0;
	int ret = 0;
	int (*sqlite_callback)(char **,int,int,void *,unsigned int) = sqlite_read_callback;

	DEBUG("smartlife sqlite read: %s\n", sqlite_cmd);
	
	sqlite3* smarthome_db = NULL;
	
	if(SQLITE_OK!=sqlite3_open(SMARTHOME_DATABASE,&smarthome_db)){
		ERROROUT("can't open database\n");
		ret = -1;
	}
	else{
		ret = 0;
		
		// open database ok
		if(sqlite3_get_table(smarthome_db,sqlite_cmd,&l_result,&l_row,&l_column,&errmsg)
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
		sqlite3_free(errmsg);								///	release the memery possessed by error message
		sqlite3_close(smarthome_db);
	}
	
	return ret;
}
