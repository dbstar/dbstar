#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <time.h>
#include <sys/time.h>
#include <unistd.h>
#include <sys/stat.h>
#include <sys/types.h>

#include "common.h"
#include "instruction.h"

/* 
功能：将指定字符串的指定位置的字串，按照指定的进制进行转换，得到long型整数
输入：	str				——原始字符串，可以不以数字开头
		str_len			——原始字符串长度
		start_position	——指定转换的起始位置，原始字符串的开头位置定义为0
		appoint_len		——指定需要转换的长度
		base			——转换的进制，取值和strtol一致
返回：失败返回-1，正常返回得到的long int数字
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
//	DEBUG("tmp_str=%s, will return with 0x%x==%d, origine str=%s, start at %d, aspect len %d\n", tmp_str,ret_int,ret_int, str, start_position, appoint_len);
	return ret_int;
}

/*
功能：	以毫秒为单位进行休眠
输入：	休眠的毫秒数
返回：	（无）
*/
void ms_sleep(unsigned int ms)
{
	if(ms<=0)
		return;
	struct timeval timeout;
	timeout.tv_sec=ms/1000;
	timeout.tv_usec=(ms%1000)*1000;			///ms
	select(0,NULL,NULL,NULL,&timeout);
}

/*
功能：	获得随机值，封装了srand()和rand()组合，采用时间usec作为种子，比直接采用sec更加“随机”，敏感度高一些
输入：	rand_top表示封顶的随机数（以浮点型表示的整型值），结果将大于等于0但小于此值。
返回：	指定范围内的无符号整型值，类似rand()的返回值
举例：	调用randint(5.0)得到的是0、1、2、3、4五者之一的随机数。效果类似于rand()%x
*/
/*
用"int x = rand() % 100;"来生成 0 到 100 之间的随机数这种方法是不或取的，
比较好的做法是： j=(int)(ｎ*rand()/(RAND_MAX+1.0))产生一个0到ｎ之间的随机数。
注意要使用浮点数，否则除法使用后结果会有不妥。
*/
unsigned int randint(float rand_top)
{
	struct timeval tv;
	gettimeofday(&tv, NULL);
	
	srand((unsigned int)tv.tv_usec);
		
	return (unsigned int)(rand_top*rand()/(RAND_MAX+1.0));
}

/* 
	在localtime()的基础上对时区进行校正。由于系统可能未设时区，导致localtime的返回值还是GMT时间，等效于gmtime()。
	为了规避此问题，手工对时间进行再校正。
	
	特别要注意：只是针对系统未设置时区信息的情况进行补救。！！！！！！！！
*/
int timezone_repair(void)
{
	return 0;
	
	struct timeval tv;
	struct timezone tz;
	gettimeofday(&tv, &tz);
	
	int timezone = (TIMEZONE_EMENDATION*60 - tz.tz_minuteswest)/60;
	if(timezone<(-23))
		timezone = -23;
	else if(timezone>23)
		timezone = 23;
	
//	DEBUG("minuteswest=%d, dsttime=%d, repair with timezone=%d\n", tz.tz_minuteswest, tz.tz_dsttime, timezone);
	return timezone;
}

/*
这是在time_t time(time_t *timer)基础上封装出来的函数，目的是将时区校正和服务器时间校正统一处理。
即：得到以服务器为准的本地时间。
一、关于time_t类型，在time.h中定义：typedef long     time_t;    时间值time_t 为长整型的别名。
	既然time_t实际上是长整型，到未来的某一天，从一个时间点（一般是1970年1月1日0时0分0秒）
	到那时的秒数（即日历时间），超出了长整形所能表示的数的范围怎么办？
	对time_t数据类型的值来说，它所表示的时间不能晚于2038年1月18日19时14分07秒。
	为了能够表示更久远的时间，一些编译器厂商引入了64位甚至更长的整形数来保存日历时间。
	比如微软在Visual C++中采用了__time64_t数据类型来保存日历时间，
	并通过_time64()函数来获得日历时间（而不是通过使用32位字的time()函数），
	这样就可以通过该数据类型保存3001年1月1日0时0分0秒（不包括该时间点）之前的时间。
二、此函数的基础是time()函数，在此基础上，加上服务器时间校正和时区校正。
三、整体用法和time()类似。
*/
time_t time_get(time_t *timer)
{
	time_t timep;
	time(&timep);
	timep += smart_power_difftime_get();
	timep += 60*60*timezone_repair();
	
	if(NULL!=timer)
		*timer = timep;
	
	return timep;
}

/*
目录初始化，避免由于缺乏目录创建权限导致文件创建失败
*/
int dir_exist_ensure(char *dir)
{
	if(0!=access(dir, F_OK)){
		ERROROUT("dir %s is not exist\n", dir);
		if(0!=mkdir(dir, 0777)){
			ERROROUT("create dir %s failed\n", dir);
			return -1;
		}
		else{
			DEBUG("create dir %s success\n", dir);
			return 0;
		}
	}
	else{
		DEBUG("dir %s is exist\n", dir);
		return 0;
	}
}

/*
获得给定时间零时的秒数（从1970年1月1日0时0分0秒开始计算）；如果参数为0，则获得今天零时的秒数。
比如：传入1342524240表示UTC时间Tue Jul 17 11:24:00 2012，那么将返回Tue Jul 17 00:00:00 2012对应的秒数
采用本地时间
*/
int zero_sec_get(time_t appoint_secs)
{
	time_t appoint_sec;
	time_t day_sec_0;	// 今天零时的秒数
	struct tm appoint_tm;
	
	if(0==appoint_secs)
		time_get(&appoint_sec);
	else
		appoint_sec = appoint_secs;
	
	localtime_r(&appoint_sec, &appoint_tm);
	
	appoint_tm.tm_hour = 0;
	appoint_tm.tm_min = 0;
	appoint_tm.tm_sec = 0;
	day_sec_0 = mktime(&appoint_tm);
	
	return day_sec_0;
}
