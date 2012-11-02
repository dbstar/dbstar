#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <time.h>
#include <sys/time.h>
#include <unistd.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <fcntl.h>
#include <pthread.h>
#include <sys/ioctl.h>
#include <errno.h>
#include <sys/socket.h>
#include <net/if.h>
#include <arpa/inet.h>
#include <netpacket/packet.h>
#include <linux/if_ether.h>
#include <net/if_arp.h>
#include <semaphore.h>
#include <sys/types.h>
#include <dirent.h>

#include "common.h"

/* 
功能：将指定字符串的指定位置的字串，按照指定的进制进行转换，得到long型整数
输入：	str				——原始字符串，可以不以数字开头
		str_len			——原始字符串长度
		start_position	——指定转换的起始位置，原始字符串的开头位置定义为0
		appoint_len		——指定需要转换的长度
		base			——转换的进制，取值和strtol一致
输出：失败返回-1，正常返回得到的long int数字
*/
int appoint_str2int(char *str, unsigned int str_len, unsigned int start_position, unsigned int appoint_len, int base)
{
	if(NULL==str || str_len<(start_position+appoint_len) || appoint_len>64 || (base<0 && 36<base)){
		DEBUG("some arguments are invalid\n");
		return -1;
	}

	char tmp_str[65];
	int ret_int = 0;
	
	memset(tmp_str, 0, sizeof(tmp_str));
	strncpy(tmp_str, str+start_position, appoint_len);
	ret_int = strtol(tmp_str, NULL, base);//atoi(tmp_str);
	DEBUG("tmp_str=%s, will return with 0x%x==%d, origine str=%s, start at %d, aspect len %d\n", tmp_str,ret_int,ret_int, str, start_position, appoint_len);
	return ret_int;
}

void ms_sleep(unsigned int ms)
{
	if(ms<=0)
		return;
	struct timeval timeout;
	timeout.tv_sec=ms/1000;
	timeout.tv_usec=(ms%1000)*1000;			///ms
	select(0,NULL,NULL,NULL,&timeout);
}

unsigned int randint()
{
	struct timeval tv;
	gettimeofday(&tv, NULL);
	
	srand((unsigned int)(tv.tv_usec)%10000);
	return rand();
}


/*
目录初始化，避免直接创建文件失败。
如果需要确保的是目录，则需要在路径后加上斜杠，否则最后一段视为文件名。
如：filename为/home/test/aaa.txt，则确保的是目录/home/test
如：filename为/home/mydir/，则确保的是目录/home/mydir
*/
int dir_exist_ensure(char *filename)
{
	if(NULL==filename || strlen(filename)>128){
		DEBUG("file name is NULL or too long\n");
		return -1;
	}
		
	char tmp_dir[128];
	snprintf(tmp_dir, sizeof(tmp_dir), "%s", filename);
	char *last_slash = strrchr(tmp_dir, '/');
	if(NULL==last_slash)
		return 0;
	
	*last_slash = '\0';
	
	if(0!=access(tmp_dir, F_OK)){
		ERROROUT("dir %s is not exist\n", tmp_dir);
		if(0!=mkdir(tmp_dir, 0777)){
			ERROROUT("create dir %s failed\n", tmp_dir);
			return -1;
		}
		else{
			DEBUG("create dir %s success\n", tmp_dir);
			return 0;
		}
	}
	else{
		DEBUG("dir %s is exist\n", tmp_dir);
		return 0;
	}
}

void print_timestamp(int show_s_ms, int show_str)
{
	struct timeval tv_now;
	time_t t;
	struct tm area;
	tzset(); /* tzset()*/
	
	if(show_s_ms){
		if(-1==gettimeofday(&tv_now, NULL)){
			ERROROUT("gettimeofday failed\n");
		}
		else
			DEBUG("|s: %ld\t|ms: %ld\t|us:%ld\t", tv_now.tv_sec, (tv_now.tv_usec)/1000, (tv_now.tv_usec));
	}
	if(show_str){
		t = time(NULL);
		localtime_r(&t, &area);
		DEBUG("|%s", asctime(&area));
	}
	
	if(0==show_str)
		DEBUG("\n");
	
	return;
}
/*
靠！除法运算在板子上执行时提示Floating point exception，原因：
高版本的gcc在链接时采用了新的哈希技术来提高动态链接的速度，这在低版本中是不支持的。因此会发生这个错误。
解决方案：
在链接的时候添加选项-Wl,--hash-style=sysv
例如 gcc -Wl,--hash-type=sysv -o test test.c
http://fhqdddddd.blog.163.com/blog/static/18699154201002683914623/
--------------------------------------------------
简单方法:静态编译
编译参数加 -static
g++ ....... -static

但是上述方法实际使用时无效，只好自己搞一个很简单的，仅支持正整数相除的函数。

*/
int phony_div(unsigned int div_father, unsigned int div_son)
{
	if(0==div_son)
		return -1;
	
	if(0==div_father)
		return 0;
	
	int ret = 0;
	while(div_father>=div_son){
		ret ++;
		div_father -= div_son;
	}
	
	return ret;
}

/* 
检查字符串str_dad的尾部是否有str_son，并且str_son要么紧跟在指定的separater_sign字符后面，要么str_dad完全等于str_son，
场景：	检查全路径settings/allpid/allpid.xml中是否含有文件名allpid.xml，指定分隔符为“/”。
			
		strrstr_s("settings/allpid/allpid.xml", STR_SON, '/');
		
		STR_SON规则：
			1、必须是全字匹配，llpid.xml不存在，aallpid.xml也不存在；
			2、如果str_dad完全等于str_son，则判断为存在；即：settings/allpid/allpid.xml存在
			3、子串必须出现在父串末尾，allpid不存在，因为其在中间；	
			4、子串允许有分隔符，allpid/allpid.xml存在，
		
返回值：参考strrstr
 */
char *strrstr_s(const char *str_dad, char *str_son, char separater_sign)
{
	if(NULL==str_dad || NULL==str_son || strlen(str_dad)<strlen(str_son)){
		//DEBUG("can not compared between invalid string\n");
		return NULL;
	}
	
	if(0==strcmp(str_dad, str_son))
		return (char *)str_dad;
	
	
	int i = 0;
	char *p_dad = (char *)str_dad;
	char *p = NULL;
	for(i=0; i<256; i++){
		p = strchr(p_dad, separater_sign);
		//DEBUG("p_dad: %s, p: %s\n", p_dad, p);
		if(p && strlen(p)>=(strlen(str_son)+1)){
			p++;
			if(0==strcmp(p, str_son))
				return p;
			else
				p_dad = p;
		}
		else
			return NULL;
	}
	
	if(256==i)
		DEBUG("what a fucking string you check for, it has 256 separater sign at least.\n");
	
	return NULL;
}

/*
 是否以指定字符串结尾，case_cmp表示是否敏感匹配，0表示不敏感，其他表示敏感
*/
int check_tail(const char *str_dad, char *str_tail, int case_cmp)
{
	if(NULL==str_dad || NULL==str_tail || 0==strlen(str_tail) || strlen(str_dad)<strlen(str_tail)){
		DEBUG("invalid args\n");
		return -1;
	}
	
	int space_size = strlen(str_tail)+1;
	char *p_dad = malloc(space_size);
	if(p_dad){
		snprintf(p_dad, space_size, "%s", str_dad+(strlen(str_dad)-strlen(str_tail)));
	}
	else{
		DEBUG("can not malloc for %d Bs\n", space_size);
		return -1;
	}
	
	
	char *p_tail = malloc(space_size);
	if(p_tail){
		snprintf(p_tail, space_size, "%s", str_tail);
	}
	else{
		DEBUG("can not malloc for %d Bs\n", space_size);
		free(p_dad);
		return -1;
	}
	
	//DEBUG("str_dad: %s, p_dad:%s, p_tail:%s\n", str_dad, p_dad, p_tail);
	int ret = 1;
	if(0==case_cmp)
		ret = strncasecmp(p_dad, p_tail, space_size-1);
	else
		ret = strcmp(p_dad, p_tail);
	
	if(0==ret)
		return 0;
	else
		return 1;
}

/*
以秒数加毫秒数生成唯一代码。当没有同步时间时，有可能得到相同的值，但是在毫秒级别上概率微乎其微。
*/
static char s_time_serial[32];
char *time_serial()
{
	struct timeval tv;
	gettimeofday(&tv, NULL);
	snprintf(s_time_serial, sizeof(s_time_serial),"%ld%ld", tv.tv_sec, tv.tv_usec);
	//DEBUG("tv.tv_sec=%ld, tv.tv_usec=%ld, s_time_serial=%s\n", tv.tv_sec, tv.tv_usec, s_time_serial);
	return s_time_serial;
}

/*
检查给定的点分十进制IPv4地址是否为合法的IP地址，这个检查比较弱。
-1：非法，
0：合法
*/
int ipv4_simple_check(const char *ip_addr)
{
	if(NULL==ip_addr || 0==strlen(ip_addr))
		return -1;
	
	int ret = -1;
	int ip[4];
	if(4==sscanf(ip_addr, "%d.%d.%d.%d",&ip[0],&ip[1],&ip[2],&ip[3])){
		DEBUG("will check ip %d-%d-%d-%d\n", ip[0], ip[1], ip[2], ip[3]);
	}
	else{
		DEBUG("can NOT check %s, perhaps it has invalid format\n", ip_addr);
		return -1;
	}
	
	if((ip[0]>=0&&ip[0]<224)&&(ip[1]>=0&&ip[1]<256)&&(ip[2]>=0&&ip[2]<256)&&(ip[3]>=0&&ip[3]<256))
	{
		/*
		把地址为全零的IP地址除去
		*/
		if(ip[0]==0&&ip[1]==0&&ip[2]==0&&ip[3]==0)
			ret = -1;
		/*
		将127.0.0.0去除
		*/
		else if(ip[0]==127)
			ret = -1;
		/*
		将主机号为全1的去除
		*/
		else if(ip[1]==255&&ip[2]==255&&ip[3]==255)
			ret = -1;
		/*
		将主机号为全0的去除
		*/
		else if(ip[1]==0&&ip[2]==0&&ip[3]==0)
			ret = -1;
		else
			ret = 0;
	}
	else
		ret = -1;
	
	return ret;
}

/*
 组播报文的目的地址使用D类IP地址， 范围是从224.0.0.0到239.255.255.255
 这里只做简单的头部、粗略IP、端口号检查，不做过于细致的区别。实际上可用的组播IP范围要小一些
*/
int igmp_simple_check(const char *igmp_addr, char *igmp_ip, int *igmp_port)
{
	if(NULL==igmp_addr || NULL==igmp_ip || NULL==igmp_port){
		DEBUG("invalid args\n");
		return -1;
	}
	
	if(0!=strncasecmp(igmp_addr, "igmp://", strlen("igmp://"))){
		DEBUG("there is no valid protocol head for igmp addr: %s\n", igmp_addr);
		return -1;
	}
	
	char *p_multi_addr = igmp_addr+strlen("igmp://");
	char *p_colon = strchr(p_multi_addr, ':');
	char multi_ip[16];
	if(p_colon || abs(p_colon-p_multi_addr)>15){
		memset(multi_ip, 0, sizeof(multi_ip));
		strncpy(multi_ip, p_multi_addr, abs(p_colon-p_multi_addr));

		int ip[4];
		if(4==sscanf(multi_ip, "%d.%d.%d.%d",&ip[0],&ip[1],&ip[2],&ip[3])){
			DEBUG("will check multi ip %d-%d-%d-%d\n", ip[0], ip[1], ip[2], ip[3]);
		}
		else{
			DEBUG("can NOT check multi ip %s, perhaps it has invalid format\n", multi_ip);
			return -1;
		}
		
		if((ip[0]>=224&&ip[0]<=239)&&(ip[1]>=0&&ip[1]<=255)&&(ip[2]>=0&&ip[2]<=255)&&(ip[3]>=0&&ip[3]<=255))
		{
			strcpy(igmp_ip, multi_ip);
			p_colon ++;
			*igmp_port = strtol(p_colon, NULL, 0);
			
			DEBUG("valid multi addr: %s\n", igmp_addr);
			return 0;
		}
		else{
			DEBUG("invalid multi ip: %s\n", multi_ip);
			return -1;
		}
	}
	else{
		DEBUG("invalid igmp addr: %s\n", igmp_addr);
		return -1;
	}
	
	return 0;
}

/*
 从路径path中获取第一个文件格式为filefmt的文件，优先匹配preferential_file，
 将获得的文件uri存放在file中
*/
int distill_file(char *path, char *file, unsigned int file_size, char *filefmt, char *preferential_file)
{
	DIR * pdir;
	struct dirent *ptr;
	char newpath[512];
	struct stat filestat;
	int file_count = 0;
	int file_count_max = 64;
	int ret = -1;
	
	if(NULL==path || NULL==file || 0==file_size){
		DEBUG("some arguments are invalid\n");
		return -1;
	}
	
	if(stat(path, &filestat) != 0){
		DEBUG("The file or path(%s) can not be get stat!\n", path);
		return -1;
	}
	if((filestat.st_mode & S_IFDIR) != S_IFDIR){
		DEBUG("(%s) is not be a path!\n", path);
		return -1;
	}
	pdir =opendir(path);
	while((ptr = readdir(pdir))!=NULL)
	{
		if((file_count+1) > file_count_max){
			DEBUG("The count of the files is too much(%d > %d)!\n", file_count + 1, file_count_max);
			break;
		}
		
		if(0==strcmp(ptr->d_name, ".") || 0==strcmp(ptr->d_name, ".."))
			continue;
		
		snprintf(newpath,sizeof(newpath),"%s/%s", path,ptr->d_name);
		if(stat(newpath, &filestat) != 0){
			DEBUG("The file or path(%s) can not be get stat!\n", newpath);
			continue;
		}
		/* Check if it is file. */
		if((filestat.st_mode & S_IFREG) == S_IFREG){
			snprintf(file,file_size,"%s/%s", path,ptr->d_name);
			if(NULL!=preferential_file && 0!=strlen(preferential_file)){
				if(0==strcmp(ptr->d_name, preferential_file)){
					DEBUG("match %s in %s\n", preferential_file, path);
					ret = 0;
					break;
				}
			}
			else if(NULL!=filefmt && 0!=strlen(filefmt)){
				char *p = strrchr(ptr->d_name,'.');
				if(p){
					p++;
					if(p && strlen(filefmt)==strlen(p) && 0==strncasecmp(p, filefmt, strlen(filefmt))){
						DEBUG("match file format %s file %s\n", filefmt, file);
						ret = 0;
						break;
					}
				}
			}
			else{
				ret = 0;
				DEBUG("get %s in %s\n", ptr->d_name, path);
				break;
			}
			
//			if(filefmt[0] != '\0'){
//				char* p;
//				if((p = strrchr(ptr->d_name,'.')) == 0) continue;
//				
//				char fileformat[64];
//				char* token;
//				strcpy(fileformat, filefmt);        
//				if((token = strtok( fileformat,";")) == NULL){
//					strcpy(file[file_count], newpath);
//					file_count++;
//					continue;
//				}else{
//					if(strcasecmp(token,p) == 0){
//						strcpy(file[file_count], newpath);
//						file_count++;
//						continue;
//					}
//				}
//				while((token = strtok( NULL,";")) != NULL){
//					if(strcasecmp(token,p) == 0){
//						strcpy(file[file_count], newpath);
//						file_count++;
//						continue;
//					}
//				}
//			}
//			else{
//				strcpy(file[file_count], newpath);
//				file_count++;
//			}
		}
//		else if((filestat.st_mode & S_IFDIR) == S_IFDIR){
//			if(ReadPath(newpath, file, filefmt) != 0){
//				DEBUG("Path(%s) reading is fail!\n", newpath);
//				continue;
//			}
//		} 
	}
	closedir(pdir);
	return ret;   
}
