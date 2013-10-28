#include <stdio.h>      /*标准输入输出定义*/
#include <stdlib.h>     /*标准函数库定义*/
#include <string.h>
#include <unistd.h>     /*Unix 标准函数定义*/
#include <sys/types.h>  
#include <sys/stat.h>   
#include <fcntl.h>      /*文件控制定义*/
#include <termios.h>    /*PPSIX 终端控制定义*/
#include <errno.h>      /*错误号定义*/
#include <time.h>
#include <semaphore.h>

#include "common.h"
#include "smarthome_shadow/smarthome.h"
#include "smarthome_shadow/serial.h"
//#include "sqlite.h"
//#include "instruction.h"

static int g_serialfd;
static sem_t s_sem_serial;

static int s_serial_failed_count = 0;

static int UART0_Open(char* port);
static void UART0_Close(int fd);
static int UART0_Set(int fd,int speed,int flow_ctrl,int databits,int stopbits,int parity);
static int UART0_Recv(int fd, unsigned char *rcv_buf,int data_len);
static int UART0_Send(int fd, unsigned char *send_buf,int data_len);

static int serial_reset()
{
	sem_wait(&s_sem_serial);
	
	int ret = -1;
	
	if(g_serialfd>0)
		serial_fd_close();
	
	usleep(500000);
	
#if 0
	g_serialfd = UART0_Open("/dev/ttyUSB0"); //pc虚拟机linux测试，使用usb转串口，不经过开发板，直接发往串口设备
#else
	g_serialfd = UART0_Open("/dev/ttyS1"); //打开串口，返回文件描述符
#endif

	if(g_serialfd<0){
		DEBUG("open serial failed\n");
		ret = -1;
	}
	else{
		if(0 > UART0_Set(g_serialfd,115200,0,8,1,'N')){
			DEBUG("Set Port Exactly failed!\n");
			UART0_Close(g_serialfd);
			g_serialfd = -1;
			ret = -1;
		}
		else{
			s_serial_failed_count = 0;
			DEBUG("serial module reset success, serialfd=%d\n", g_serialfd);
			ret = 0;
		}
	}
	sem_post(&s_sem_serial);
	
	return ret;
}

int serial_int(void)
{
	if(-1==sem_init(&s_sem_serial, 0, 1)){
		DEBUG("s_sem_insert_insts init failed\n");
		return -1;
	}
	
	g_serialfd = -1;
	return serial_reset();
}

/*******************************************************************
* 名称：                  UART0_Open
* 功能：                打开串口并返回串口设备文件描述
* 入口参数：        fd    :文件描述符     port :串口号(ttyS0,ttyS1,ttyS2)
* 出口参数：        正确返回为1，错误返回为0
*******************************************************************/
static int UART0_Open(char* port)
{
	int fd = open( port, O_RDWR|O_NOCTTY|O_NDELAY);
	if (-1 == fd)
	{
		perror("Can't Open Serial Port");
		DEBUG("strerror(errno): [%d]%s\n", errno,strerror(errno));
		return(-1);
	}
	//恢复串口为阻塞状态                               
	if(fcntl(fd, F_SETFL, 0) < 0)
	{
		DEBUG("fcntl failed!\n");
		return(-1);
	}     
	else
	{
		DEBUG("fcntl=%d\n",fcntl(fd, F_SETFL,0));
	}
#if 0
	//测试是否为终端设备    
	if(0 == isatty(STDIN_FILENO))
	{
		DEBUG("standard input is not a terminal device\n");
		return(-1);
	}
	else
	{
		DEBUG("isatty success!\n");
	}
#endif

	DEBUG("serial port=%s, fd=%d\n", port, fd);
	return fd;
}
/*******************************************************************
* 名称：                UART0_Close
* 功能：                关闭串口并返回串口设备文件描述
* 入口参数：        fd    :文件描述符     port :串口号(ttyS0,ttyS1,ttyS2)
* 出口参数：        void
*******************************************************************/

static void UART0_Close(int fd)
{
	if(fd>0){
		close(fd);
		DEBUG("close serial fd %d\n", fd);
	}
	else
		DEBUG("can NOT close fd %d\n", fd);
}

/*******************************************************************
* 名称：                UART0_Set
* 功能：                设置串口数据位，停止位和效验位
* 入口参数：        fd        串口文件描述符
*                              speed     串口速度
*                              flow_ctrl   数据流控制
*                           databits   数据位   取值为 7 或者8
*                           stopbits   停止位   取值为 1 或者2
*                           parity     效验类型 取值为N,E,O,,S
*出口参数：          正确返回为1，错误返回为0
*******************************************************************/
static int UART0_Set(int fd,int speed,int flow_ctrl,int databits,int stopbits,int parity)
{
	unsigned int   i;
	int   speed_arr[] = { B115200, B19200, B9600, B4800, B2400, B1200, B300};
	int   name_arr[] = {115200,  19200,  9600,  4800,  2400,  1200,  300};
	
	struct termios options;
	
	/*tcgetattr(fd,&options)得到与fd指向对象的相关参数，并将它们保存于options,该函数还可以测试配置是否正确，该串口是否可用等。若调用成功，函数返回值为0，若调用失败，函数返回值为1.
	*/
	if  ( tcgetattr( fd,&options)  !=  0)
	{
		perror("SetupSerial 1");    
		return(-1); 
	}
	
	//设置串口输入波特率和输出波特率
	for ( i= 0;  i < sizeof(speed_arr) / sizeof(int);  i++)
	{
		if  (speed == name_arr[i])
		{             
			cfsetispeed(&options, speed_arr[i]); 
			cfsetospeed(&options, speed_arr[i]);  
		}
	}     
	
	//修改控制模式，保证程序不会占用串口
	options.c_cflag |= CLOCAL;
	//修改控制模式，使得能够从串口中读取输入数据
	options.c_cflag |= CREAD;
	
	//设置数据流控制
	switch(flow_ctrl)
	{
	case 0 ://不使用流控制
		options.c_cflag &= ~CRTSCTS;
		break; 
	case 1 ://使用硬件流控制
		options.c_cflag |= CRTSCTS;
		break;
	case 2 ://使用软件流控制
		options.c_cflag |= IXON | IXOFF | IXANY;
		break;
	}
	//设置数据位
	//屏蔽其他标志位
	options.c_cflag &= ~CSIZE;
	switch (databits)
	{  
	case 5    :
		options.c_cflag |= CS5;
		break;
	case 6    :
		options.c_cflag |= CS6;
		break;
	case 7    :    
		options.c_cflag |= CS7;
		break;
	case 8:    
		options.c_cflag |= CS8;
		break;  
	default:   
		fprintf(stderr,"Unsupported data size\n");
		return (-1); 
	}
	//设置校验位
	switch (parity)
	{  
	case 'n':
	case 'N': //无奇偶校验位。
		options.c_cflag &= ~PARENB; 
		options.c_iflag &= ~INPCK;    
		break; 
	case 'o':  
	case 'O'://设置为奇校验    
		options.c_cflag |= (PARODD | PARENB); 
		options.c_iflag |= INPCK;             
		break; 
	case 'e': 
	case 'E'://设置为偶校验  
		options.c_cflag |= PARENB;       
		options.c_cflag &= ~PARODD;       
		options.c_iflag |= INPCK;      
		break;
	case 's':
	case 'S': //设置为空格 
		options.c_cflag &= ~PARENB;
		options.c_cflag &= ~CSTOPB;
		break; 
	default:  
		fprintf(stderr,"Unsupported parity\n");    
		return (-1); 
	} 
	// 设置停止位 
	switch (stopbits)
	{  
	case 1:   
		options.c_cflag &= ~CSTOPB; break; 
	case 2:   
		options.c_cflag |= CSTOPB; break;
	default:   
		fprintf(stderr,"Unsupported stop bits\n"); 
		return (-1);
	}
	
	//修改输出模式，原始数据输出
	options.c_oflag &= ~OPOST;
	
	options.c_lflag &= ~(ICANON | ECHO | ECHOE | ISIG);//我加的
	//options.c_lflag &= ~(ISIG | ICANON);
	
	cfmakeraw(&options);
	
	//设置等待时间和最小接收字符
	options.c_cc[VTIME] = 1; 
	options.c_cc[VMIN] = 4;
	
	//如果发生数据溢出，接收数据，但是不再读取 刷新收到的数据但是不读
	tcflush(fd,TCIFLUSH);
	
	//激活配置 (将修改后的termios数据设置到串口中）
	if (tcsetattr(fd,TCSANOW,&options) != 0)  
	{
		perror("com set error!\n");  
		return (-1); 
	}
	return (0); 
}

/*******************************************************************
* 名称：                  UART0_Recv
* 功能：                接收串口数据
* 入口参数：        fd                  :文件描述符    
*                              rcv_buf     :接收串口中数据存入rcv_buf缓冲区中
*                              data_len    :一帧数据的长度
* 出口参数：        正确返回为1，错误返回为0
*******************************************************************/
static int UART0_Recv(int fd, unsigned char *rcv_buf,int data_len)
{
	int len,fs_sel;
	fd_set fs_read;
	
	struct timeval time;
	
	FD_ZERO(&fs_read);
	FD_SET(fd,&fs_read);
	
	time.tv_sec = 0;
	time.tv_usec = 150000;
	
	//使用select实现串口的多路通信
	fs_sel = select(fd+1,&fs_read,NULL,NULL,&time);
	if(fs_sel>0)
	{
		if(FD_ISSET(fd, &fs_read)){
			len = read(fd,rcv_buf,data_len);
			return len;
		}
		else{
			DEBUG("other fd is can be read, but not serial\n");
			return 0;
		}
	}
	else if(0==fs_sel)
	{
		DEBUG("serial read waiting timeout\n");
		return (0);
	}
	else{	//fs_sel < 0
		DEBUG("serial read failed\n");
		return (-1);
	}
}
/********************************************************************
* 名称：                  UART0_Send
* 功能：                发送数据
* 入口参数：        fd                  :文件描述符    
*                              send_buf    :存放串口发送数据
*                              data_len    :一帧数据的个数
* 出口参数：        正确返回为1，错误返回为0
*******************************************************************/
static int UART0_Send(int fd, unsigned char *send_buf,int data_len)
{
	int len = 0;
	
	len = write(fd,send_buf,data_len);
	if (len == data_len )
	{
		return len;
	}     
	else   
	{
		tcflush(fd,TCOFLUSH);
		return -1;
	}
}

static int sendto_serial(unsigned char *buf, unsigned int len)
{
	if(NULL==buf || len<=0){
		DEBUG("params some error\n");
		return -1;
	}
	
	unsigned int i = 0;
	printf("---------------------------------------- send %d chars\n", len);
	for(i=0;i<len;i++)
		printf(" %02x", buf[i]);
	printf("\n----------------------------------------\n");
	
	int ret = UART0_Send(g_serialfd, buf, len);
	
	if((unsigned int)ret==len){
		DEBUG("send to serial OK\n");
		return 0;
	}
	else
		return -1;
}

/*
模仿strstr，在一个命令串中查找另一个串，由于这种串不是字符，有可能存在'\0'，所以不能直接用strstr
目前采用简单的匹配，没有优化。如果要查询的串比较长，则此方法不可取。
返回值为匹配串son_buf的开头在dad_buf中的index，从0开始。
*/
/*
static int ascinasc(unsigned char *dad_buf, unsigned int dad_len, unsigned char *son_buf, unsigned int son_len)
{
	if(0==dad_len || 0==son_len)
		return -1;
	
	unsigned int i = 0, j = 0;
	for(i=0;i<(dad_len-son_len);i++){
		j = 0;
		for(j=0;j<son_len;j++){
			if(dad_buf[i+j]!=son_buf[j]){
				//DEBUG("dad_buf[%d]=0x%02x, son_buf[%d]=0x%02x	break\n", i+j, dad_buf[i+j], j, son_buf[j]);
				break;
			}
			//else
				//;//DEBUG("dad_buf[%d]=0x%02x, son_buf[%d]=0x%02x	==\n", i+j, dad_buf[i+j], j, son_buf[j]);
				
		}
		if(j==son_len)
			return i;
	}
	
	return -1;
}
*/

#define SERIAL_RESPONSE_CHECK_MIN	(10)	// e.g.: 68 a0 a1 a2 a3 a4 a5 68 c5 03

// 检查是否存在合法的串口返回串
// 返回值:
//			-1: failed;		0: 获取到合法并有效的指令
// *check_start_pos: 传出时表示下次可以开始的位置
static int check_valid_serial_response(unsigned char *buf, unsigned int *check_start_pos, unsigned int buf_len, unsigned char *distinguish_cmd)
{
	// 期望合法指令识别：			68 a0 a1 a2 a3 a4 a5 68
	// 指令合法，但通信失败：		68 a0 a1 a2 a3 a4 a5 68 c5 03
	// 指令不合法，433模块的广播:	68 99 99 99 99 99 99 68
	if(NULL==buf || buf_len<SERIAL_RESPONSE_CHECK_MIN || NULL==distinguish_cmd){
		DEBUG("invalid args, buf_len=%u\n", buf_len);
	}
	
	unsigned int i = 0, j = 0;;
	int ret = -1;
	
	printf("will check %d bytes cmd :::::::::::::::::::::\n",buf_len);
	for(i=0;i<buf_len;i++)
		printf(" %02x", buf[i]);
	printf("\ncompared with =================\n");
	for(i=0;i<8;i++)
		printf(" %02x", distinguish_cmd[i]);
	printf("\n?????????????????????????????\n");
	
	for(i=0;i<(buf_len-SERIAL_RESPONSE_CHECK_MIN);i++){
		if(0x68==buf[i]){
			if(	distinguish_cmd[0]==buf[i+0] 
				&& distinguish_cmd[1]==buf[i+1] 
				&& distinguish_cmd[2]==buf[i+2] 
				&& distinguish_cmd[3]==buf[i+3] 
				&& distinguish_cmd[4]==buf[i+4] 
				&& distinguish_cmd[5]==buf[i+5] 
				&& distinguish_cmd[6]==buf[i+6] 
				&& distinguish_cmd[7]==buf[i+7]){
					
				if(0xc5==buf[i+8] && 0x03==buf[i+9]){
					DEBUG("catch valid cmd but communication failed ==xxxx== i=%d\n",i);
					for(j=i;j<(buf_len);j++)
						printf(" %02x", buf[j]);
					printf("\n=======xxxxxxxxxxxxx==============================\n");
				}
				else{
					DEBUG("catch valid and success serial response cmd ==== i=%d\n",i);
					for(j=i;j<(buf_len);j++)
						printf(" %02x", buf[j]);
					printf("\n==================================================\n");
					
					ret = 0;
					break;
				}
			}
		}
	}
	
	*check_start_pos += i;
	
	return ret;
}

static int recvfrom_serial(unsigned char *buf, unsigned int *start_pos,unsigned int buf_size, unsigned char *distinguish_cmd)
{
	if(NULL==buf || buf_size<=0){
		DEBUG("params some error\n");
		return -1;
	}
	
	unsigned int has_read_len = 0;
	unsigned char *p_readbuf = buf;
	int len = 0;
	unsigned int check_start_pos = 0;
	int has_catched_response = 0;
	unsigned int serial_read_faild_cnt = 0;
	
	while(1){
		len = UART0_Recv(g_serialfd, p_readbuf+has_read_len, buf_size-1-has_read_len);
		
		if(has_catched_response>0)
			has_catched_response++;
		
		if(len > 0)
		{
			DEBUG("read %d bytes for this time,(",len);
			int j = 0;
			for(j=0;j<len;j++)
				printf(" %02x", *(p_readbuf+has_read_len+j));
			has_read_len += len;
			
			printf("), and total read %d bytes\n", has_read_len);
			
			DEBUG("[%d] has_read_len(%d) - check_start_pos(%d) = %d\n", has_catched_response, has_read_len,check_start_pos,has_read_len-check_start_pos);
			if(0==has_catched_response && (has_read_len-check_start_pos)>=SERIAL_RESPONSE_CHECK_MIN){
				if(0==check_valid_serial_response(p_readbuf+check_start_pos,&check_start_pos,has_read_len-check_start_pos,distinguish_cmd)){
					DEBUG("has catched valid serial response\n");
					has_catched_response = 1;
				}
				
				DEBUG("[%d]check_start_pos: %d\n", has_catched_response,check_start_pos);
			}
			
			serial_read_faild_cnt = 0;
		}
		else if(0==len)
		{
			DEBUG("cannot receive data for %d try\n", serial_read_faild_cnt);
			serial_read_faild_cnt ++;
			
			if(serial_read_faild_cnt>4){
				DEBUG("read nothing nolonger, failed at %d\n",serial_read_faild_cnt);
				break;
			}
		}
		else{
			serial_read_faild_cnt ++;
			
			if(serial_read_faild_cnt>4){
				DEBUG("read nothing nolonger, failed at %d\n",serial_read_faild_cnt);
				break;
			}
		}
		
		if(has_catched_response>2){
			DEBUG("finish to do another %d times trying\n",has_catched_response);
			break;
		}
	}
		
	tcflush(g_serialfd, TCIOFLUSH);
	
	*start_pos = check_start_pos;
	
	DEBUG("has_read_len=%d, check_start_pos=%d\n", has_read_len,check_start_pos);
	return (has_read_len-check_start_pos);
}

/*
兼容这样的情况，返回处的开头一段是非法值：
例1、以继电器闭合操作为例
发送：68 20 11 12 21 06 36 68 04 09 56 16 33 33 33 33 44 44 44 81 16
返回：56 7b 16 68 20 11 06 02 41 56 68 81 06 43 c3 33 33 33 33 f9 16 68 20 11 12 21 06 36 68 c5 03 e9 04 56 7b 16 68 20 11 12 21 06 36 68 c5 03 e9 04 56 7b 16 68 20 11 12 21 06 36 68 c5 03 e9 04 56 7b 16 68 20 11 12 21 06 36 68 c5 03 e9 04 56 7b 16 68 20 11 06 00 41 56 68 81 06 43 c3 33 3b 33 33 ff 16 68 20 11 12 21 06 36 68 c5 03 e9 04 56 7b 16 68 20 11 06 00 41 56 68 81 06 43 c3 33 33 33 33 f7 16 68 20 11 12 21 06 36 68 c5 03 e9 04 56 7b 16 68 20 11 12 21 06 36 68 c5 03 e9 04 56 7b 16 68 20 11 12 21 06 36 68 c5 03 e9 04 56 7b 16 68 20 11 12 21 06 36 68 c5 03 e9 04 56 7b 16 68 20 11 12 21 06 36 68 c5 03 e9 04 56 7b 16 68 20 11 12 21 06 36 68 c5 03 e9 04 56 7b 16 68 20 11 12 21 06 36 68 c5 03 e9 04 56 7b 16 68 20 11 12 21 06 36 68 c5 03 e9 04 56 7b 16 68 20 11
实际上有用的是：68 20 11 12 21 06 36 68 c5 03 e9 04 56 7b 16，表示通信错误。

例2、验证插座是否存在
发送：68 20 11 12 21 06 36 68 07 00 77 16
返回：68 99 99 99 99 99 99 68 c5 03 e9 99 99 49 16 68 20 11 12 21 06 36 68 87 06 53 44 45 54 39 69 cf 16
实际上有用的是：68 20 11 12 21 06 36 68 87 06 53 44 45 54 39 69 cf 16
完全正确的是：	68 20 11 12 21 06 36 68 87 06 33 33 33 33 33 33 cf 16

例3、继电器闭合
发送：68 20 11 12 21 09 51 68 04 09 56 16 33 33 33 33 44 44 44 9f 16
返回：68 20 11 12 21 09 51 68 c5 03 e9 04 56 99 16 68 20 11 12 21 09 51 68 84 02 89 89
第一段说明通信失败，第二段是执行成功。这有点儿操蛋，怎样才能获取到正确的命令返回？

还需要解决一个问题：如果插座连续通信失败怎么办？应该关闭串口，重启smarthome应用。
*/
static int serial_access_son(unsigned char *buf, unsigned int buf_len, unsigned int buf_size)
{
	int has_read_len = -1;
	int ret = -1;
	
	if(NULL==buf || buf_len<SERIAL_CMD_SEND_LEN_MIN || buf_len>SERIAL_CMD_SEND_LEN_MAX){
		DEBUG("invalid serial cmd, buf_len=%u\n", buf_len);
		ret = -1;
	}
	else{
		sem_wait(&s_sem_serial);
	
		usleep(20000);
		
		// 期望合法指令识别：68 a0 a1 a2 a3 a4 a5 68
		unsigned char distinguish_cmd[32];
		memset(distinguish_cmd, 0, sizeof(distinguish_cmd));
		memcpy(distinguish_cmd, buf, 8);	//识别段: 68 20 11 12 21 06 36 68
		
		if(0!=sendto_serial(buf, buf_len)){
			DEBUG("send to serial failed\n");
			ret = -1;
		}
		else{
			memset(buf, 0, buf_size);
			usleep(250000);
			
			unsigned char serial_response_buf[128000];
			unsigned int start_pos = 0;
			
			memset(serial_response_buf,0,sizeof(serial_response_buf));
			has_read_len = recvfrom_serial(serial_response_buf, &start_pos, sizeof(serial_response_buf), distinguish_cmd);
			if(has_read_len>10){
				memset(buf,0,buf_size);
				memcpy(buf,serial_response_buf+start_pos,buf_size);
				ret = has_read_len;
			}
			else{
				DEBUG("has read len: %d, perhaps failed\n", has_read_len);
				ret = -1;
			}
		}
	
		sem_post(&s_sem_serial);
		
		if(s_serial_failed_count>2)
			serial_reset();
	}
	
	return ret;
}

int serial_access(unsigned char *buf, unsigned int buf_len, unsigned int buf_size)
{
	int i = 0;
	int ret = 0;
	unsigned char remem_buf[128];
	unsigned int remem_buf_len = buf_len;
	
	memset(remem_buf,0,sizeof(remem_buf));
	memcpy(remem_buf,buf,buf_len);
	
	for(i=0;i<3;i++){
		if(i>0){
			buf_len = remem_buf_len;
			memcpy(buf,remem_buf,buf_len);
		}
		
		ret = serial_access_son(buf,buf_len,buf_size);
		if(-1==ret){
			DEBUG("serial access son failed at %d times\n", i+1);
		}
		else{
			DEBUG("serial access son success %d times\n", i+1);
			DEBUG("read %d bytes for this time,(",ret);
			int j = 0;
			for(j=0;j<ret;j++)
				printf(" %02x", *(buf+j));
			printf(")\n");
			
			break;
		}
	}
	
	return ret;
}

void serial_fd_close(void)
{
	UART0_Close(g_serialfd);
	g_serialfd = -1;
}


/***sendSerial() brief send data to serial port
 * param length[in], file descriptor of serial port to be send data
 *
 * retval int, return value--true if true,false failed.
 ***/
 /*
BOOL_E sendSerial(int length)
{
	int l_fd=-1;								///temporary file descriptor

	if(write(l_fd,g_dataTrans.st_sendSerial,length)!=length)
	{
		ERROROUT("write() error!");
		tcflush(l_fd,TCOFLUSH);						///???bug(maybe cause some problem)???flushes data written but not transmitted.
		return false;
	}


	return true;
}*/

