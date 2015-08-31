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
#include <dirent.h>
#include <sys/statfs.h>
#include <sys/vfs.h>

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
 输出文件操作相关的错误信息
*/
static int printf_fserrno(const char *uri, int fserrno)
{
	switch(fserrno){
		case EROFS:		// 欲写入的文件存在于只读文件系统内。
			DEBUG("%s EROFS\n",uri);
			break;
		case EFAULT:	//参数pathname 指针超出可存取内存空间。
			DEBUG("%s EFAULT\n",uri);
			break;
		case ENAMETOOLONG:	//参数pathname 太长。
			DEBUG("%s ENAMETOOLONG\n",uri);
			break;
		case ENOMEM:	//核心内存不足。
			DEBUG("%s ENOMEM\n",uri);
			break;
		case ELOOP:	//参数pathname 有过多符号连接问题。
			DEBUG("%s ELOOP\n",uri);
			break;
		case EIO:	//I/O 存取错误。
			DEBUG("%s EIO\n",uri);
			break;
		case ENOENT:
			DEBUG("%s ENOENT\n",uri);
			break;
		case ENOTDIR:
			DEBUG("%s ENOTDIR\n",uri);
			break;
		default:
			DEBUG("%s other errno: %d\n", uri,fserrno);
			break;
	}
	
	return 0;
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
 类似于strcmp或者strncasecmp
 reutrn:
 	0 means: yes, it has the tail
 	others: no, it has no such tail
*/
int strtailcmp(const char *str_dad, char *str_tail, int case_cmp)
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
	
	int ret = 1;
	if(0==case_cmp)
		ret = strncasecmp(p_dad, str_tail, space_size-1);
	else
		ret = strcmp(p_dad, str_tail);
	
	if(p_dad){
		free(p_dad);
		p_dad = NULL;
	}
	
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
	
	const char *p_multi_addr = igmp_addr+strlen("igmp://");
	char *p_colon = strchr(p_multi_addr, ':');
	char multi_ip[16];
	if(p_colon){
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
			if(*igmp_port>0){
				DEBUG("valid multi addr: %s\n", igmp_addr);
				return 0;
			}
			else{
				DEBUG("invalid multi port: %d\n", *igmp_port);
				return -1;
			}
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

#if 0
/*
 从路径path中获取第一个文件格式为filefmt的文件，优先匹配preferential_file，
 将获得的文件uri存放在file中
*/
int distill_file(char *path, char *file, unsigned int file_size, char *filefmt, char *preferential_file)
{
	DIR * pdir;
	struct dirent *ptr;
	char newpath[1024];
	struct stat filestat;
	int file_count = 0;
	int file_count_max = 64;
	int ret = -1;
	
	if(NULL==path || NULL==file || 0==file_size){
		DEBUG("some arguments are invalid\n");
		return -1;
	}
	
	if(stat(path, &filestat) != 0){
		ERROROUT("The file or path(%s) can not be get stat!\n", path);
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
#endif

/*
清理字符串中的指定字符，比如：将字符串头尾的斜线去掉，原串为"/mnt/sda1/dbstar///"，指定清理头尾字符'/'，结果为"mnt/sda1/dbstar"
flag——1表示清理开头，2表示清理末尾，3表示清理开头和结尾，0及其他表示全部清理
返回值——0表示成功，-1表示失败
考虑到效率，字符串长度不能大于4096。这个长度也能避免那些意外的字符串
*/
int signed_char_clear(char *str_dad, unsigned int str_dad_len, char sign_c, int flag)
{
	if(NULL==str_dad || 0==strlen(str_dad) || 0==str_dad_len || str_dad_len>4096){
		DEBUG("invalid args, len=%d\n", str_dad_len);
		return -1;
	}
	DEBUG("will clear '%c' in %s with %d\n", sign_c, str_dad, flag);
	
	unsigned int i = 0;
	if(2==flag || 3==flag){
		for(i=0; i<str_dad_len; i++){
			if(sign_c!=str_dad[str_dad_len-1-i])
				break;
			else
				str_dad[str_dad_len-1-i] = '\0';
		}
	}
	
	unsigned int len_now = str_dad_len>strlen(str_dad)?strlen(str_dad):str_dad_len;
	if(1==flag || 3==flag){
		for(i=0; i<len_now; i++){
			if(sign_c!=str_dad[i])
				break;
		}
		
		if(i>0){
			int j = 0;
			for(;i<len_now;i++){
				str_dad[j] = str_dad[i];
				j++;
			}
			str_dad[j] = '\0';
		}
	}
	
	DEBUG("clear return: %s\n", str_dad);
	
	return 0;
}


#define BUFFER_SIZE 10240
int fcopy_c(char *from_file, char *to_file)
{
#if 0
	int from_fd = 0, to_fd = 0;
	int bytes_read = 0, bytes_write = 0;
	char buffer[BUFFER_SIZE];
	char *ptr = NULL;
	int ret = 0;
	
	if(NULL==from_file || NULL==to_file)
	{
		DEBUG("some args are failed\n");
		return -1;
	}
	else
		DEBUG("copy %s to %s\n", from_file,to_file);
	
	/* 打开源文件 */
	if((from_fd=open(from_file,O_RDONLY))==-1)  /*open file readonly,返回-1表示出错，否则返回文件描述符*/
	{
		ERROROUT("open %s to read failed\n", from_file);
		return -1;
	}
	
//	mode_t new_umask, old_umask;
//	new_umask=0111;  
//	old_umask=umask(new_umask);
//	DEBUG("old umask: %o, change to new umask: %o\n", old_umask,new_umask);
	
	/* 创建目的文件 */
	/* 使用了O_CREAT选项-创建文件,open()函数需要第3个参数,
	mode=S_IRUSR|S_IWUSR表示S_IRUSR 用户可以读 S_IWUSR 用户可以写*/
	if((to_fd=open(to_file,O_WRONLY|O_CREAT,S_IRUSR|S_IWUSR|S_IRGRP|S_IWGRP|S_IROTH|S_IWOTH))==-1) 
	{
		ERROROUT("open %s to write failed\n", to_file);
		close(from_fd);
		return -1;
	}
	
	/* 以下代码是一个经典的拷贝文件的代码 */
	while(0!=(bytes_read=read(from_fd,buffer,BUFFER_SIZE)))
	{
		if(bytes_read>0)
		{
			ptr=buffer;
			while(0!=(bytes_write=write(to_fd,ptr,bytes_read)))
			{
				/* 写完了所有读的字节 */
				if(bytes_write==bytes_read) 
					break;
				/* 只写了一部分,继续写 */
				else if(bytes_write>0)
				{
					ptr+=bytes_write;
					bytes_read-=bytes_write;
				}
				/* 一个致命错误发生了 */
				else if((bytes_write==-1)&&(errno!=EINTR)){
					ERROROUT("write1 failed\n");
					ret = -1;
					break;
				}
			}
			/* 写的时候发生的致命错误 */
			if(bytes_write==-1){
				ERROROUT("write2 failed\n");
				ret = -1;
				break;
			}
		}
		/* 一个致命的错误发生了 */
		else if((bytes_read==-1)&&(errno!=EINTR)){
			ERROROUT("read failed\n");
			ret = -1;
			break;
		}
	}
	close(from_fd);
	close(to_fd);
	
	return ret;
#else
	char sys_cmd[1024];
	snprintf(sys_cmd, sizeof(sys_cmd), "cp -f %s %s", from_file, to_file);
	system(sys_cmd);
	sync();
	
	return 0;
#endif
}

// 将一个目录下的所有文件拷贝到另一个指定目录下。懒省事，只拷贝文件，不管其中的目录
int files_copy(char *from_dir, char *to_dir)
{
#if 0
	DIR * pdir = NULL;
	struct dirent *ptr = NULL;
	char fileson[1024];
	char fileson_to[1024];
	struct stat filestat;
	struct stat fileson_stat;
	long long cur_size = 0LL;
	
	if(NULL==from_dir || 0==strlen(from_dir) || NULL==to_dir || 0==strlen(to_dir)){
		DEBUG("can not copy will null uri\n");
		return -1;
	}
	
	int stat_ret = stat(from_dir, &filestat);
	if(0==stat_ret){
		if(S_IFDIR==(filestat.st_mode & S_IFDIR)){
			pdir = opendir(from_dir);
			if(pdir){
				while((ptr = readdir(pdir))!=NULL)
				{
					if(0==strcmp(ptr->d_name, ".") || 0==strcmp(ptr->d_name, ".."))
						continue;
					
					snprintf(fileson,sizeof(fileson),"%s/%s", from_dir,ptr->d_name);
					snprintf(fileson_to,sizeof(fileson_to),"%s/%s", to_dir,ptr->d_name);
					if(0==stat(fileson, &fileson_stat)){
						if((fileson_stat.st_mode & S_IFREG) == S_IFREG)
							fcopy_c(fileson, fileson_to);
					}
				}
				closedir(pdir);
			}
			else{
				ERROROUT("opendir(%s) failed\n", from_dir);
				cur_size = -1;
			}
		}
	}
	else{
		ERROROUT("can not stat(%s)\n", from_dir);
		cur_size = -1;
	}
	
	return cur_size;
#else
	char sys_cmd[1024];
	snprintf(sys_cmd, sizeof(sys_cmd), "cp -rf %s/* %s/*", from_dir, to_dir);
	system(sys_cmd);
	sync();
	
	return 0;
#endif
}

/*
 扫描指定的目录，如果其下的文件/文件夹stat失败，则将其重命名。
 主要解决在下载节目过程中由于意外掉电导致此目录异常，下次打开文件时出现IO错误。
 此时必须重命名此目录然后才能重新下载，否则一直无法恢复。
 注意：只有目录才能递归进去，不要递归到软链接里面，有可能导致死循环
*/
#define EIO_RENAME_POSTFIX	"__EIO_RENAME__"
#define EIO_RETURN_FLAG		(-2)
static int dir_stat_check(const char *uri)
{
	DIR * pdir = NULL;
	struct dirent *ptr = NULL;
	char newpath[1024];
	struct stat filestat;
	int ret = 0;
	
	if(NULL==uri || 0==strlen(uri)){
		DEBUG("can not rm such uri, it is NULL, or length is 0\n");
		return -1;
	}
	
	int stat_ret = stat(uri, &filestat);
	if(0==stat_ret){
		if(S_IFDIR==(filestat.st_mode & S_IFDIR)){
			pdir = opendir(uri);
			if(pdir){
				while((ptr = readdir(pdir))!=NULL)
				{
					if(0==strcmp(ptr->d_name, ".") || 0==strcmp(ptr->d_name, ".."))
						continue;
					
					snprintf(newpath,sizeof(newpath),"%s/%s", uri,ptr->d_name);
					ret = dir_stat_check((const char *)newpath);
					if(EIO_RETURN_FLAG==ret)
						break;
				}
				closedir(pdir);
			}
			else{
				ERROROUT("opendir(%s) failed\n", uri);
				ret = -1;
			}
		}
	}
	else{
		ret = -1;
		
		int my_errno = errno;
		printf_fserrno(uri,my_errno);
		if(EIO==my_errno){
			DEBUG("stat(%s) return as EIO\n", uri);
			ret = EIO_RETURN_FLAG;
		}
	}
	
	return ret;
}

int dir_stat_ensure(const char *uri)
{
	if(0==strstr(uri,EIO_RENAME_POSTFIX) && EIO_RETURN_FLAG==dir_stat_check(uri)){
		char new_uri_name[1024];
		snprintf(new_uri_name,sizeof(new_uri_name),"%s%s",uri,EIO_RENAME_POSTFIX);
		if(0!=rename(uri,new_uri_name))
			ERROROUT("rename %s to %s failed\n", uri, new_uri_name);
		
		DEBUG("WARNING: rename %s to %s\n", uri, new_uri_name);
	}
	else
		DEBUG("%s is normal\n", uri);
	
	return 0;
}


/*
 计算指定uri的大小
 注意：只有目录才能递归进去，不要递归到软链接里面，有可能导致死循环
*/
long long dir_size(const char *uri)
{
	DIR * pdir = NULL;
	struct dirent *ptr = NULL;
	char newpath[1024];
	struct stat filestat;
	long long cur_size = 0LL;
	
	if(NULL==uri || 0==strlen(uri)){
		DEBUG("can not rm such uri, it is NULL, or length is 0\n");
		return -1;
	}
	
	int stat_ret = stat(uri, &filestat);
	if(0==stat_ret){
		cur_size += filestat.st_size;
		PRINTF("%s size %lld\n", uri, cur_size);
		
		if(S_IFDIR==(filestat.st_mode & S_IFDIR)){
			pdir = opendir(uri);
			if(pdir){
				while((ptr = readdir(pdir))!=NULL)
				{
					if(0==strcmp(ptr->d_name, ".") || 0==strcmp(ptr->d_name, ".."))
						continue;
					
					snprintf(newpath,sizeof(newpath),"%s/%s", uri,ptr->d_name);
					long long subdir_size = dir_size((const char *)newpath);
					if(subdir_size>0LL)
						cur_size += subdir_size;
				}
				closedir(pdir);
			}
			else{
				ERROROUT("opendir(%s) failed\n", uri);
				cur_size = -1;
			}
			
			PRINTF("%s size2 %lld\n", uri, cur_size);
		}
	}
	else{
		ERROROUT("can not stat(%s)\n", uri);
		cur_size = -1;
	}
	
	return cur_size;   
}

/*
 将uri下的文件夹和文件递归删除，rmdir不能删除非空文件夹
 注意：只有目录才能递归进去，不要递归到软链接里面，有可能导致死循环
*/
static int remove_force_t(const char *uri)
{
	char newpath[1024];
	int ret = -1;
	
#if 0
	DIR * pdir = NULL;
	struct dirent *ptr = NULL;
	struct stat filestat;
	
	if(NULL==uri || 0==strlen(uri)){
		DEBUG("can not rm such uri, it is NULL, or length is 0\n");
		return -1;
	}
	
	if(0==stat(uri, &filestat)){
		if(S_IFDIR==(filestat.st_mode & S_IFDIR)){
			pdir = opendir(uri);
			if(pdir){
				while((ptr = readdir(pdir))!=NULL)
				{
					if(0==strcmp(ptr->d_name, ".") || 0==strcmp(ptr->d_name, ".."))
						continue;
					
					snprintf(newpath,sizeof(newpath),"%s/%s", uri,ptr->d_name);
					remove_force_t((const char *)newpath);
				}
				
				closedir(pdir);
			}
			else{
				ERROROUT("opendir(%s) failed\n", uri);
				ret = -1;
			}
		}
		
		ret = remove(uri);
		if(0==ret){
			PRINTF("remove(%s)\n", uri);
//			sync();
		}
		else{
			if(ENOENT==errno)
				ret = 0;
			else
				ret = -1;
			
			printf_fserrno(uri,errno);
		}
	}
	else{
		if(ENOENT==errno)
			ret = 0;
		else
			ret = -1;
			
		printf_fserrno(uri,errno);
	}
	
#else
	
	char sys_cmd[1024];
	snprintf(sys_cmd,sizeof(sys_cmd),"rm -rf %s", uri);
	system(sys_cmd);
	DEBUG("do system(%s)\n", sys_cmd);
	ret = 0;
	
#endif

	return ret;   
}

int remove_force(const char *from_fun, const char *uri)
{
	int ret = -1;
	
	if(0==remove_force_t(uri)){
		PRINTF("[%s] remove_force(%s) success\n", from_fun, uri);
		sync();
		
		ret = 0;
	}
	else{
		DEBUG("[%s] remove_force(%s) failed\n", from_fun, uri);
		ret = -1;
	}
	
	return ret;
}

//// hour,minite,second, e.g.: 16:20:44
static char s_hms_stamp[16];
char *hms_stamp(void)
{
	time_t now_sec = 0;
	if(-1==time(&now_sec)){
		PRINTF("get now_sec failed\n");
		memset(s_hms_stamp, 0, sizeof(s_hms_stamp));
		return NULL;
	}
	
	struct tm now_tm;
	localtime_r(&now_sec, &now_tm);
	snprintf(s_hms_stamp,sizeof(s_hms_stamp),"%02d:%02d:%02d", now_tm.tm_hour,now_tm.tm_min,now_tm.tm_sec);
	
	return s_hms_stamp;
}

// e.g.: 2013-01-26 09:23:05
int localtime_rf(char *time_str, unsigned int time_str_size)
{
	if(NULL==time_str){
		DEBUG("invalid arg\n");
		return -1;
	}
	
	time_t now_sec = 0;
	if(-1==time(&now_sec)){
		DEBUG("get time failed\n");
		return -1;
	}
	
	struct tm now_tm;
	localtime_r(&now_sec, &now_tm);
	snprintf(time_str,time_str_size,"%04d-%02d-%02d %02d:%02d:%02d", now_tm.tm_year+1900,now_tm.tm_mon+1,now_tm.tm_mday,now_tm.tm_hour,now_tm.tm_min,now_tm.tm_sec);
	DEBUG("seconds(long type): %ld, localtime: %s\n", now_sec,time_str);
	
	return 0;
}

/*
检查磁盘的可用状态，不仅是statfs执行成功，还要检查其总大小是合法值。
测试发现，无论硬盘是否存在，开机后UI都能下发一个mount信号和一个umount信号，磁盘存在时，在umount信号后检查磁盘statfs成功，但是总大小为0；如果硬盘存在，则再次下发一个mount信号。
在最后一个mount信号前，磁盘的statfs大小都是非法值，对其中的文件也不可操作。

注意，由于此调用涉及到磁盘信息的查看，不要在高频率动作中调用此函数。

return:
	1: success, has usable free size
	0: success, has no free size
	-1:failed, disable
*/
int disk_usable_check(char *disk_dir, unsigned long long *tt_size, unsigned long long *free_size)
{
	int ret = -1;
	struct statfs diskInfo;
	
	ret = statfs(disk_dir,&diskInfo);
	if(0==ret){
		unsigned long long tmp_total_size	= diskInfo.f_bsize * diskInfo.f_blocks;
		unsigned long long tmp_free_size	= diskInfo.f_bsize * diskInfo.f_bfree;
	    if(tmp_free_size>0LL)	// 有剩余空间，磁盘是正常的
	    	ret = 1;
	    else if(0LL==tmp_total_size)	// 即便statfs正确执行，但如果总大小为0，则磁盘也处在不可用状态
	    	ret = -1;
	    else							// statfs正确执行，总大小不为0，但空闲为0，则可进行删除、查询等操作
	    	ret = 0;
	    
	    if(tt_size)
	    	*tt_size = tmp_total_size;
	    if(free_size)
	    	*free_size = tmp_free_size;
	    
	    DEBUG("%s(%d): TOTAL_SIZE(%llu B) FREE_SIZE(%llu B)\n",disk_dir,ret,tmp_total_size,tmp_free_size);
	}
	else{
		ERROROUT("%s is NOK\n", disk_dir);
		ret = -1;
	}
	
	return ret;
}

