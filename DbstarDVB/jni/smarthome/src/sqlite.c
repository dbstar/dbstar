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

///used in this file
static sqlite3* g_db = NULL;												///the pointer of database created or opened
static sem_t g_sem_db;
static int g_db_open_count=0;
//static char cmdStr[SQLITECMDLEN];									///cmd string to be operate by "sqlite3_exec"\"sqlite3_get_table"

static int createDatabase();
static int openDatabase();
static void closeDatabase();
static int createTable(char* name);


static int createDatabase()
{
	return openDatabase();
}
static int openDatabase()
{
	char	*errmsgOpen=NULL;
	int		ret = -1;
	///open database
	if(g_db!=NULL){
		DEBUG("the database has opened\n");
		ret = 0;
	}
	else
	{	
		if(-1==dir_exist_ensure(DATABASE_DIR)){
			ret = -1;
		}
		else{
			if(SQLITE_OK!=sqlite3_open(DATABASE,&g_db)){
				ERROROUT("can't open database %s\n", DATABASE);
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
					if(createTable("global")){
						ERROROUT("can not create table \"global\"\n");
						ret = -1;
					}
					else{
						DEBUG("create table \"global\" OK\n");
						ret = 0;
					}
					if(createTable("devlist")){
						ERROROUT("can not create table \"devlist\"\n");
						ret = -1;
					}
					else{
						DEBUG("create \"devlist\" OK\n");
						ret = 0;
					}
					if(createTable("power")){
						ERROROUT("can not create table \"power\"\n");
						ret = -1;
					}
					else{
						DEBUG("create \"power\" OK\n");
						ret = 0;
					}
					if(createTable("actpower")){
						ERROROUT("can not create table \"actpower\"\n");
						ret = -1;
					}
					else{
						DEBUG("create \"actpower\" OK\n");
						ret = 0;
					}
					if(createTable("time")){
						ERROROUT("can not create table \"time\"\n");
						ret = -1;
					}
					else{
						DEBUG("create \"time\" OK\n");
						ret = 0;
					}
					if(createTable("model")){
						ERROROUT("can not create table \"model\"\n");
						ret = -1;
					}
					else{
						DEBUG("create \"model\" OK\n");
						ret = 0;
					}
					if(createTable("modtime")){
						ERROROUT("can not create table \"modtime\"\n");
						ret = -1;
					}
					else{
						DEBUG("create \"modtime\" OK\n");
						ret = 0;
					}
					if(createTable("writelog")){
						ERROROUT("can not create table \"writelog\"\n");
						ret = -1;
					}
					else{
						DEBUG("create \"writelog\" OK\n");
						ret = 0;
					}
					if(createTable("readlog")){
						ERROROUT("can not create table \"readlog\"\n");
						ret = -1;
					}
					else{
						DEBUG("create \"readlog\" OK\n");
						ret = 0;
					}
				}
				sqlite3_free(errmsgOpen);
			}
		}
	}
	if(0==ret){
		sem_wait(&g_sem_db);
		g_db_open_count++;
		DEBUG("open database for %d times\n", g_db_open_count);
		sem_post(&g_sem_db);
	}
	
	return ret;
}
static void closeDatabase()
{
	sem_wait(&g_sem_db);
	if(--g_db_open_count>0)
		DEBUG("close database, leave %d times\n", g_db_open_count);
	else
	{
		if(g_db!=NULL)
		{
			DEBUG("g_db_open_count is 0, and do sqlite3_close action\n");
			sqlite3_close(g_db);
			g_db_open_count=0;
			g_db=NULL;
		}
		else{
			DEBUG("g_db is NULL, can not do database close action\n");
		}
	}
	sem_post(&g_sem_db);
}

static int createTable(char* name)
{
	char* errmsg=NULL;
	char ** l_result=NULL;									    	///result of tables in database
	int l_row=0;                                            	///the row of result
	int l_column=0;									        	///the column of result
	char sqlite_cmd[SQLITECMDLEN];
	char tmp_str[128];
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
			if(!strcmp(name,"devlist"))
			{
				snprintf(sqlite_cmd, sizeof(sqlite_cmd),"CREATE TABLE devlist(typeID INTEGER PRIMARY KEY,locationID INTEGER,iconID INTEGER,operID INTEGER,socketID NVARCHAR(16),roomName NVARCHAR(40),devName NVARCHAR(40),elecData DOUBLE);");
				if(sqlite3_exec(g_db,sqlite_cmd,NULL,NULL,&errmsg))
				{
					ERROROUT("create 'devlist' failed.");
					ret = -1;
				}
		// 20120604
		//		sqlite3_free(errmsg);
		//		snprintf(sqlite_cmd,sizeof(sqlite_cmd),"INSERT INTO devlist VALUES(0,0,0,0,0,0,0,0);");
		//		if(sqlite3_exec(g_db,sqlite_cmd,NULL,NULL,&errmsg))
		//		{
		//			ERROROUT("insert '0' failed.");
		//			goto FAILED;
		//		}
			}
			else if (!strcmp(name,"power"))
			{
				/*status是为了避免定时主动查询上报时重复而额外增加的字段，0——新添加、未查询，1——已查询、未上报，2——上报成功*/
				snprintf(sqlite_cmd,sizeof(sqlite_cmd),"CREATE TABLE power(typeID INTEGER,hourTime INTEGER,data DOUBLE,status INTEGER,FOREIGN KEY(typeID) REFERENCES devlist(typeID) ON UPDATE CASCADE ON DELETE CASCADE);");
				if(sqlite3_exec(g_db,sqlite_cmd,NULL,NULL,&errmsg))
				{
					ERROROUT("create 'power' failed.");
					ret = -1;
				}
			}
			else if (!strcmp(name,"actpower"))
			{
				snprintf(sqlite_cmd,sizeof(sqlite_cmd),"CREATE TABLE actpower(typeID INTEGER,hourTime INTEGER,data DOUBLE,status INTEGER,FOREIGN KEY(typeID) REFERENCES devlist(typeID) ON UPDATE CASCADE ON DELETE CASCADE);");
				if(sqlite3_exec(g_db,sqlite_cmd,NULL,NULL,&errmsg))
				{
					ERROROUT("create 'actpower' failed.");
					ret = -1;
				}	
			}
			else if (!strcmp(name,"tmppower"))
			{
				snprintf(sqlite_cmd,sizeof(sqlite_cmd),"CREATE TABLE tmppower(typeID INTEGER,hourTime INTEGER,data DOUBLE,FOREIGN KEY(typeID) REFERENCES devlist(typeID) ON UPDATE CASCADE ON DELETE CASCADE);");
				if(sqlite3_exec(g_db,sqlite_cmd,NULL,NULL,&errmsg))
				{
					ERROROUT("create 'tmppower' failed.");
					ret = -1;
				}
			}
			else if(!strcmp(name,"time"))
			{
				snprintf(sqlite_cmd,sizeof(sqlite_cmd),"CREATE TABLE time(typeID INTEGER,cmdType INTEGER,controlVal INTEGER,controlTime INTEGER,frequency INTEGER,remark NVARCHAR(40),FOREIGN KEY(typeID) REFERENCES devlist(typeID) ON UPDATE CASCADE ON DELETE CASCADE);");
				if(sqlite3_exec(g_db,sqlite_cmd,NULL,NULL,&errmsg))
				{
					ERROROUT("create 'time' failed.");
					ret = -1;
				}
			}
			else if(!strcmp(name,"model"))
			{
				snprintf(sqlite_cmd,sizeof(sqlite_cmd),"CREATE TABLE model(modeID INTEGER,name NVARCHAR(40));");
				if(sqlite3_exec(g_db,sqlite_cmd,NULL,NULL,&errmsg))
				{
					ERROROUT("create 'model' failed.");
					ret = -1;
				}
			}
			else if(!strcmp(name,"modtime"))
			{
				snprintf(sqlite_cmd,sizeof(sqlite_cmd),"CREATE TABLE modtime(typeID INTEGER,cmdType INTEGER,controlVal INTEGER,controlTime INTEGER,frequency INTEGER,remark NVARCHAR(40),modeID INTEGER,FOREIGN KEY(typeID) REFERENCES devlist(typeID) ON UPDATE CASCADE ON DELETE CASCADE);");
				if(sqlite3_exec(g_db,sqlite_cmd,NULL,NULL,&errmsg))
				{
					ERROROUT("create 'day' failed.");
					ret = -1;
				}
			}
			else if(!strcmp(name,"global"))
			{
				snprintf(sqlite_cmd,sizeof(sqlite_cmd),"CREATE TABLE global(ID INTEGER PRIMARY KEY AUTOINCREMENT,name NVARCHAR(20),value NVARCHAR(20));");
				if(sqlite3_exec(g_db,sqlite_cmd,NULL,NULL,&errmsg))
				{
					ERROROUT("create 'global' failed.");
					ret = -1;
				}
				sqlite3_free(errmsg);
				///version
				memset(tmp_str, 0, sizeof(tmp_str));
				initial_software_version_get(tmp_str, sizeof(tmp_str)-1);
				snprintf(sqlite_cmd,sizeof(sqlite_cmd),"INSERT INTO global VALUES(NULL,'version','%s');",tmp_str);
				if(sqlite3_exec(g_db,sqlite_cmd,NULL,NULL,&errmsg))
				{
					ERROROUT("insert vertion to 'global' failed.");
					ret = -1;
					goto END;
				}
				sqlite3_free(errmsg);
				///username
				snprintf(sqlite_cmd,sizeof(sqlite_cmd),"INSERT INTO global VALUES(NULL,'username','test_user');");
				if(sqlite3_exec(g_db,sqlite_cmd,NULL,NULL,&errmsg))
				{
					ERROROUT("insert 'global' failed.");
					ret = -1;
					goto END;
				}
				sqlite3_free(errmsg);
				///passwd
				snprintf(sqlite_cmd,sizeof(sqlite_cmd),"INSERT INTO global VALUES(NULL,'passwd','123456');");
				if(sqlite3_exec(g_db,sqlite_cmd,NULL,NULL,&errmsg))
				{
					ERROROUT("insert 'global' failed.");
					ret = -1;
					goto END;
				}

				sqlite3_free(errmsg);
				///serialNUM
				memset(tmp_str, 0, sizeof(tmp_str));
				initial_serial_num_get(tmp_str, sizeof(tmp_str)-1);
				snprintf(sqlite_cmd,sizeof(sqlite_cmd),"INSERT INTO global VALUES(NULL,'serialNUM','%s');", tmp_str);
				if(sqlite3_exec(g_db,sqlite_cmd,NULL,NULL,&errmsg))
				{
					ERROROUT("insert 'global' failed.");
					ret = -1;
					goto END;
				}
				sqlite3_free(errmsg);
				///serverIP
				memset(tmp_str, 0, sizeof(tmp_str));
				initial_server_ip_get(tmp_str, sizeof(tmp_str)-1);
				snprintf(sqlite_cmd,sizeof(sqlite_cmd),"INSERT INTO global VALUES(NULL,'serverIP','%s');", tmp_str);
				if(sqlite3_exec(g_db,sqlite_cmd,NULL,NULL,&errmsg))
				{
					ERROROUT("insert 'global' failed.");
					ret = -1;
					goto END;
				}
				sqlite3_free(errmsg);
				///port
				snprintf(sqlite_cmd,sizeof(sqlite_cmd),"INSERT INTO global VALUES(NULL,'port','%d');", initial_server_port_get());
				if(sqlite3_exec(g_db,sqlite_cmd,NULL,NULL,&errmsg))
				{
					ERROROUT("insert 'global' failed.");
					ret = -1;
				}
			}
			else if(!strcmp(name,"writelog"))
			{
				snprintf(sqlite_cmd,sizeof(sqlite_cmd),"CREATE TABLE writelog(ID INTEGER PRIMARY KEY AUTOINCREMENT,cmd  NVARCHAR(40),time INTEGER);");
				if(sqlite3_exec(g_db,sqlite_cmd,NULL,NULL,&errmsg))
				{
					ERROROUT("create 'writelog' failed.");
					ret = -1;
				}
			}
			else if(!strcmp(name,"readlog"))
			{
				snprintf(sqlite_cmd,sizeof(sqlite_cmd),"CREATE TABLE readlog(ID INTEGER PRIMARY KEY AUTOINCREMENT,cmd  NVARCHAR(40),time INTEGER);");
				if(sqlite3_exec(g_db,sqlite_cmd,NULL,NULL,&errmsg))
				{
					ERROROUT("create 'readlog' failed.");
					ret = -1;
				}
			}

		}
	}

END:
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
	g_db=NULL;
	///initialize g_sem_db
	if(-1==sem_init(&(g_sem_db),0,1))
	{
		ERROROUT("g_sem_db init failed!");
		return -1;
	}
	if(-1==createDatabase())						///open database
		return -1;
	///see need update
//	if (-1==updateDatabase())					/// update database
//		return -1;
	return 0;						/// quit
}
/***getGlobalPara() brief get some global variables from sqlite, such as 'version'.
 * param name[in], the name of global param
 *
 * retval int,0 if successful or -1 failed
 ***/
int getGlobalPara(char* name)
{
	char* errmsg=NULL;
	char** l_result = NULL;
	int l_row = 0;
	int l_column = 0;
	char sqlite_cmd[SQLITECMDLEN];
	///open database
	DEBUG("getGlobalPara starting...\n");
	if(-1==openDatabase())
	{
		ERROROUT("Open database failed.");
		return -1;
	}
	if(!strcmp(name,"version"))
		snprintf(sqlite_cmd,sizeof(sqlite_cmd),"SELECT value FROM global WHERE name='version';");
	if(sqlite3_get_table(g_db,sqlite_cmd,&l_result,&l_row,&l_column,&errmsg))
	{
		ERROROUT("query failed.");
		goto FAILED;
	}
	///close database
	sqlite3_free(errmsg);
	sqlite3_free_table(l_result);
	closeDatabase();
	return atoi(l_result[1]);

	FAILED:
	sqlite3_free(errmsg);
	sqlite3_free_table(l_result);
	closeDatabase();
	return -1;
}

INSTRUCTION_RESULT_E sqlite_execute(char *exec_str)
{
	if(NULL==exec_str || 0==strlen(exec_str)){
		DEBUG("can not execute NULL sqlite command\n");
		return -1;
	}
	
	char* errmsg=NULL;
	int ret = -1;
	
	//open database
	if(-1==openDatabase())
	{
		ERROROUT("Open database failed\n");
		ret = ERR_DATABASE;
	}
	else{
		DEBUG("sqlite cmd: %s\n", exec_str);
		if(sqlite3_exec(g_db,exec_str,NULL,NULL,&errmsg)){
			ERROROUT("sqlite3_exec failed\n");
			DEBUG("sqlite3 errmsg: %s\n", errmsg);
			ret = ERR_DATABASE;
		}
		else{
			DEBUG("sqlite3_exec success\n");
			ret = RESULT_OK;
		}
		
		sqlite3_free(errmsg);								///	release the memery possessed by error message
		closeDatabase();									///	close database
	}
	
	return ret;	
}

// return: <0 means error, others means row number
INSTRUCTION_RESULT_E sqlite_read(char *sqlite_cmd, void *receiver, int (*sqlite_read_callback)(char **result, int row, int column, void *receiver))
{
	if(NULL==sqlite_cmd || 0==strlen(sqlite_cmd)){
		DEBUG("can not read sqlite with NULL command\n");
		return -1;
	}
	
	char* errmsg=NULL;
	char** l_result = NULL;
	int l_row = 0;
	int l_column = 0;
	INSTRUCTION_RESULT_E ret = RESULT_OK;
	int (*sqlite_callback)(char **,int,int,void *) = sqlite_read_callback;

	DEBUG("sqlite cmd str: %s\n", sqlite_cmd);
	
	///open database
	if(-1==openDatabase())
	{
		ERROROUT("Open database failed\n");
		ret = ERR_DATABASE;
	}
	else{	// open database ok
		
		if(sqlite3_get_table(g_db,sqlite_cmd,&l_result,&l_row,&l_column,&errmsg)
			|| NULL!=errmsg)
		{
			ERROROUT("sqlite cmd: %s\n", sqlite_cmd);
			DEBUG("errmsg: %s\n", errmsg);
			ret = ERR_DATABASE;
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

INSTRUCTION_RESULT_E table_clear(char *table_name)
{
	if(NULL==table_name || 0==strlen(table_name)){
		DEBUG("can not clear table with NULL name\n");
		return -1;
	}
	
	DEBUG("CAUTION: begin to clear table '%s'\n", table_name);
	char sqlite_cmd[SQLITECMDLEN];	
	
	memset(sqlite_cmd, 0, sizeof(sqlite_cmd));
	snprintf(sqlite_cmd,sizeof(sqlite_cmd),"DELETE FROM %s;", table_name);
	DEBUG("sqlite cmd str: %s\n", sqlite_cmd);

	INSTRUCTION_RESULT_E ret = sqlite_execute(sqlite_cmd);
	if(RESULT_OK==ret){
		DEBUG("table '%s' clear success\n", table_name);
		return 0;
	}
	else{
		DEBUG("table '%s' reset failed\n", table_name);
		return -1;
	}
}

