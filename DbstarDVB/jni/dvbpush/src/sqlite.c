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

/*数据库必须及时打开、及时关闭，不能采用打开计数器的方式，因为上层应用也可能在使用*/

///used in this file
static sqlite3* g_db = NULL;												///the pointer of database created or opened
static int s_sqlite_init_flag = 0;

static int createTable(char* name);
static void closeDatabase();

static int createDatabase()
{
	char	*errmsgOpen=NULL;
	int		ret = -1;
	
	if(g_db!=NULL){
		DEBUG("the database has opened\n");
		ret = 0;
	}
	else
	{
		char database_uri[64];
		memset(database_uri, 0, sizeof(database_uri));
		if(-1==database_uri_get(database_uri, sizeof(database_uri))){
			DEBUG("get database uri failed\n");
			return -1;
		}
		
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
				if(createTable("allpid")){
					ERROROUT("can not create table \"allpid\"\n");
					ret = -1;
				}
				else{
					DEBUG("create table \"allpid\" OK\n");
					ret = 0;
				}
				
				if(createTable("product")){
					ERROROUT("can not create table \"product\"\n");
					ret = -1;
				}
				else{
					DEBUG("create table \"product\" OK\n");
					ret = 0;
				}
				
				if(createTable("grouptag")){
					ERROROUT("can not create table \"grouptag\"\n");
					ret = -1;
				}
				else{
					DEBUG("create table \"grouptag\" OK\n");
					ret = 0;
				}
				
				if(createTable("column")){
					ERROROUT("can not create table \"column\"\n");
					ret = -1;
				}
				else{
					DEBUG("create table \"column\" OK\n");
					ret = 0;
				}
				
				if(createTable("content")){
					ERROROUT("can not create table \"content\"\n");
					ret = -1;
				}
				else{
					DEBUG("create table \"content\" OK\n");
					ret = 0;
				}
				if(createTable("brand")){
					ERROROUT("can not create table \"brand\"\n");
					ret = -1;
				}
				else{
					DEBUG("create table \"brand\" OK\n");
					ret = 0;
				}
				if(createTable("preproduct")){
					ERROROUT("can not create table \"preproduct\"\n");
					ret = -1;
				}
				else{
					DEBUG("create table \"preproduct\" OK\n");
					ret = 0;
				}
				if(createTable("communication")){
					ERROROUT("can not create table \"communication\"\n");
					ret = -1;
				}
				else{
					DEBUG("create table \"communication\" OK\n");
					ret = 0;
				}
			}
			sqlite3_free(errmsgOpen);
		}
		closeDatabase();
	}
	
	return ret;
}

static int openDatabase()
{
	int		ret = -1;
	
	if(g_db!=NULL){
		DEBUG("the database has opened\n");
		ret = 0;
	}
	else
	{
		char database_uri[64];
		memset(database_uri, 0, sizeof(database_uri));
		if(-1==database_uri_get(database_uri, sizeof(database_uri))){
			DEBUG("get database uri failed\n");
			return -1;
		}
		if(SQLITE_OK!=sqlite3_open(database_uri,&g_db)){
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

static int createTable(char* name)
{
	char* errmsg=NULL;
	char ** l_result=NULL;									    	///result of tables in database
	int l_row=0;                                            	///the row of result
	int l_column=0;									        	///the column of result
	char sqlite_cmd[512];
	int ret = -1;
	
	DEBUG("creating table: %s\n", name);
	memset(sqlite_cmd, 0, sizeof(sqlite_cmd));
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "SELECT name FROM sqlite_master WHERE type='table' AND name='%s';", name);
	if(sqlite3_get_table(g_db,sqlite_cmd,&l_result,&l_row,&l_column,&errmsg))
	{
		ERROROUT("read tables from database failed.");
		ret = -1;
	}
	else{
		if(l_row>0){
			DEBUG("tabel \"%s\" is exist\n", name);
			ret = 0;
		}
		else{
			sqlite3_free(errmsg);
			ret = 0;
			/*这里建立表的目的是查询，不是存储，所以不能用于查询的图片、结构体、描述等，不存入数据库*/
			if(!strcmp(name,"product"))
			{
				snprintf(sqlite_cmd, sizeof(sqlite_cmd),\
					"CREATE TABLE product(\
id		NVARCHAR(32) PRIMARY KEY,\
version	NVARCHAR(32),\
name	NVARCHAR(128),\
path	NVARCHAR(128));");
				if(sqlite3_exec(g_db,sqlite_cmd,NULL,NULL,&errmsg))
				{
					ERROROUT("create 'product' failed\n");
					DEBUG("sqlite errmsg: %s\n", errmsg);
					ret = -1;
				}
			}
			else if(!strcmp(name,"grouptag"))
			{
				snprintf(sqlite_cmd, sizeof(sqlite_cmd),\
					"CREATE TABLE grouptag(\
version		NVARCHAR(32) PRIMARY KEY,\
senduser	NVARCHAR(32),\
sendtime	NVARCHAR(32),\
id			NVARCHAR(32),\
mode		NVARCHAR(32),\
groupname	NVARCHAR(32),\
grouptype	NVARCHAR(32));");
				if(sqlite3_exec(g_db,sqlite_cmd,NULL,NULL,&errmsg))
				{
					ERROROUT("create 'grouptag' failed\n");
					DEBUG("sqlite errmsg: %s\n", errmsg);
					ret = -1;
				}
			}
			else if(!strcmp(name,"column"))
			{
				snprintf(sqlite_cmd, sizeof(sqlite_cmd),\
					"CREATE TABLE column(\
id			NVARCHAR(32) PRIMARY KEY,\
name		NVARCHAR(128),\
type		NVARCHAR(16), \
parent_id	NVARCHAR(32));");
				if(sqlite3_exec(g_db,sqlite_cmd,NULL,NULL,&errmsg))
				{
					ERROROUT("create 'column' failed\n");
					DEBUG("sqlite errmsg: %s\n", errmsg);
					ret = -1;
				}
			}
			else if(!strcmp(name,"brand"))
			{
				snprintf(sqlite_cmd, sizeof(sqlite_cmd),\
					"CREATE TABLE brand(\
id			NVARCHAR(32) PRIMARY KEY,\
regist_dir	NVARCHAR(256),\
download	BIGINT,\
totalsize	BIGINT,\
cname		NVARCHAR(128));");
				if(sqlite3_exec(g_db,sqlite_cmd,NULL,NULL,&errmsg))
				{
					ERROROUT("create 'column' failed\n");
					DEBUG("sqlite errmsg: %s\n", errmsg);
					ret = -1;
				}
			}
			else if (!strcmp(name,"content"))
			{
				snprintf(sqlite_cmd,sizeof(sqlite_cmd),\
					"CREATE TABLE content(\
id			NVARCHAR(32),\
ready		INTEGER,\
senduser	NVARCHAR(32),\
sendtime	NVARCHAR(32),\
contentname	NVARCHAR(128),\
path		NVARCHAR(256),\
column_id	NVARCHAR(32),\
coretag_id	NVARCHAR(32),\
chineseName	NVARCHAR(128),\
englishName	NVARCHAR(128),\
director	NVARCHAR(64),\
actor		NVARCHAR(64),\
favorite	NVARCHAR(16),\
bookmark	NVARCHAR(16));");
				if(sqlite3_exec(g_db,sqlite_cmd,NULL,NULL,&errmsg))
				{
					ERROROUT("create 'content' failed\n");
					DEBUG("sqlite errmsg: %s\n", errmsg);
					ret = -1;
				}	
			}
			else if (!strcmp(name,"preproduct"))
			{
				snprintf(sqlite_cmd,sizeof(sqlite_cmd),\
					"CREATE TABLE preproduct(\
id			NVARCHAR(32) PRIMARY KEY,\
download	BIGINT,\
preentry	NVARCHAR(32),\
prename		NVARCHAR(128),\
xmlpath		NVARCHAR(256),\
column_id	NVARCHAR(32));");
				if(sqlite3_exec(g_db,sqlite_cmd,NULL,NULL,&errmsg))
				{
					ERROROUT("create 'content' failed\n");
					DEBUG("sqlite errmsg: %s\n", errmsg);
					ret = -1;
				}	
			}
			else if(!strcmp(name,"allpid"))
			{
				snprintf(sqlite_cmd, sizeof(sqlite_cmd),\
					"CREATE TABLE allpid(\
id		NVARCHAR(32) PRIMARY KEY);");
				if(sqlite3_exec(g_db,sqlite_cmd,NULL,NULL,&errmsg))
				{
					ERROROUT("create 'product' failed\n");
					DEBUG("sqlite errmsg: %s\n", errmsg);
					ret = -1;
				}
			}
			else if(!strcmp(name,"communication"))
			{
				snprintf(sqlite_cmd, sizeof(sqlite_cmd),\
					"CREATE TABLE communication(\
name		NVARCHAR(128),\
value		NVARCHAR(128),\
arg			NVARCHAR(256));");
				if(sqlite3_exec(g_db,sqlite_cmd,NULL,NULL,&errmsg))
				{
					ERROROUT("create 'communication' failed\n");
					DEBUG("sqlite errmsg: %s\n", errmsg);
					ret = -1;
				}
			}
		}
	}

	sqlite3_free_table(l_result);
	sqlite3_free(errmsg);
	return ret;
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
		if(-1==createDatabase()){						///open database
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

/***getGlobalPara() brief get some global variables from sqlite, such as 'version'.
 * param name[in], the name of global param
 *
 * retval int,0 if successful or -1 failed
 ***/
int sqlite_execute(char *exec_str)
{
	char* errmsg=NULL;
	int ret = -1;
	
	//open database
	if(-1==openDatabase())
	{
		ERROROUT("Open database failed\n");
		ret = -1;
	}
	else{
		//DEBUG("sqlite cmd: %s\n", exec_str);
		if(sqlite3_exec(g_db,exec_str,NULL,NULL,&errmsg)){
			ERROROUT("sqlite3_exec failed\n");
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
	
	return ret;	
}

/*
功能：	执行SELECT语句
输入：	sqlite_cmd				——sql SELECT语句
		receiver				——用于处理SELECT结果的参数，如果sqlite_read_callback为NULL，则receiver也可以为NULL
		sqlite_read_callback	——用于处理SELECT结果的回调，如果只是想知道查询到几条记录，则此回调可以为NULL
返回：	-1——失败；其他值——查询到的记录数
*/
int sqlite_read(char *sqlite_cmd, void *receiver, int (*sqlite_read_callback)(char **result, int row, int column, void *receiver))
{
	char* errmsg=NULL;
	char** l_result = NULL;
	int l_row = 0;
	int l_column = 0;
	int ret = 0;
	int (*sqlite_callback)(char **,int,int,void *) = sqlite_read_callback;

	//DEBUG("sqlite cmd str: %s\n", sqlite_cmd);
	
	///open database
	if(-1==openDatabase())
	{
		ERROROUT("Open database failed\n");
		ret = -1;
	}
	else{	// open database ok
		
		if(sqlite3_get_table(g_db,sqlite_cmd,&l_result,&l_row,&l_column,&errmsg)
			|| NULL!=errmsg)
		{
			ERROROUT("sqlite cmd: %s\n", sqlite_cmd);
			DEBUG("errmsg: %s\n", errmsg);
			ret = -1;
		}
		else{ // inquire table ok
			if(0==l_row){
				DEBUG("no row, l_row=0, l_column=%d", l_column);
				int i = 0;
				for(i=0;i<l_column;i++)
					printf("\t\t%s", l_result[i]);
				printf("\n");
			}
			else{
				DEBUG("sqlite select OK. %s\n", NULL==sqlite_callback?"there is no callback fun":"do callback fun");
				if(sqlite_callback)
					sqlite_callback(l_result, l_row, l_column, receiver);
				else{
					DEBUG("l_row=%d, l_column=%d\n", l_row, l_column);
					int i = 0;
					for(i=0;i<l_column;i++)
						printf("\t\t%s\n", l_result[i]);
				}
			}
			ret = l_row;
		}
		sqlite3_free_table(l_result);
		sqlite3_free(errmsg);
		closeDatabase();
	}
	
	///return
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
		DEBUG("table '%s' clear success\n", table_name);
		return 0;
	}
	else{
		DEBUG("table '%s' reset failed\n", table_name);
		return -1;
	}
}

