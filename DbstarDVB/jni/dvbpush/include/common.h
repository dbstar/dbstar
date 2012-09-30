//全局使用的头文件，不允许与其他文件存在依赖

#ifndef __COMMON_H__
#define __COMMON_H__
#include <errno.h>

extern int debug_level_get(void);
#define DEBUG(x...) do{printf("[%s:%s:%d] ", __FILE__, __FUNCTION__, __LINE__);printf(x);}while(0)

#define ERROROUT(ERRSTR...) \
			do{printf("[%s:%s:%d] ", __FILE__, __FUNCTION__, __LINE__);\
			if(errno!=0) printf("[err note: %s]",strerror(errno));\
			printf(ERRSTR);}while(0)

#define MIN_LOCAL(a,b) ((a)>(b)?(b):(a))

typedef enum{
	BOOL_FALSE = 0,
	BOOL_TRUE
}BOOL_E;

/*
程序自行使用的配置、fifo文件等
*/
#define	WORKING_DATA_DIR	"/data/dbstar"
#define	MSG_FIFO_ROOT_DIR	WORKING_DATA_DIR"/msg_fifo"
#define SETTING_BASE		WORKING_DATA_DIR"/dbstar.conf"
#define PUSH_CONF			WORKING_DATA_DIR"/push.conf"

/*
程序运行过程中产生的数据，包括：下载的片源、对应的数据库
*/
#define USR_DATA_ROOT_DIR	"/mnt/sda1/dbstar"
#define PUSH_DATA_DIR_DF	USR_DATA_ROOT_DIR		// 参考push.conf中DATA_DIR定义及时刷新，以备应急使用
#define DATABASE			USR_DATA_ROOT_DIR"/dbstar.db"

#define	SERVICE_ID			"0001"
#define PROG_DATA_PID_DF	(102)	// 0X66
#define ROOT_CHANNEL		(100)
#define ROOT_PUSH_FILE		"Initialize.xml"
#define ROOT_PUSH_FILE_SIZE	(1024)			/* Is this len right??? */
#define DATA_SOURCE			"igmp://224.0.0.1:5000"
#define MULTI_BUF_SIZE		(12*1024*1316)	/* larger than 16M */

#define FTOK_FNAME			"/proc/version"

#define RELAY_DIR			"videos1/pushvod"

/*
本地测试push时使用（针对hytd.ts播发流），正常情况下关闭此宏。
*/
//#define PUSH_LOCAL_TEST

typedef struct{
	char			id[32];
	char			name[128];
	char			parent_id[32];
}DBSTAR_COLUMN_2_S;

typedef struct{
	char			id[32];
	char			contentname[128];
	char			column_id[32+8];
	char			xmlpath[128];
}DBSTAR_PRODUCT_S;

typedef struct{
	char			preentry[32];
	char			id[32];
	char			prename[128];
	char			column_id[32+8];
	char			xmlpath[128];
}DBSTAR_PREPRODUCT_S;

typedef struct{
	char				pathid[32];
	char				cname[128];
	long long			totalsize;			//unsigned 
}DBSTAR_BRAND_S;


int appoint_str2int(char *str, unsigned int str_len, unsigned int start_position, unsigned int appoint_len, int base);
unsigned int randint();
int dir_exist_ensure(char *dir);

void print_timestamp(int show_s_ms, int show_str);
int dir_ensure(char *dir);
int phony_div(unsigned int div_father, unsigned int div_son);
void ms_sleep(unsigned int ms);
int filename_check(const char *pathname, char *filename);
int ipv4_simple_check(const char *ip_addr);

#endif

