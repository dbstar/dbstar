#include <stdio.h>
#include <sys/socket.h>
#include <netinet/tcp.h>
#include <sys/mman.h>
#include <sys/ioctl.h>
#include <sys/time.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <net/if.h>
#include <unistd.h>
#include <string.h>
#include <errno.h>
#include <pthread.h>
#include <fcntl.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <semaphore.h>
#include <sys/select.h>

#include "common.h"
#include "dvbpush_api.h"
#include "sqlite.h"
#include "porting.h"

#define MAXSLEEP					(128)
#define BUF_SIZE					(40960)
#define FIFO_STR_SIZE				(32)
#define FIFO_DIR					"/data/dbstar/fifo/"
#define FIFO_2_SOCKET				FIFO_DIR"fifo_2_socket"
#define MSGSTR_2_SOCKET				"msgstr_2_socket"
#define MSGSTR_SOCKET_SELF			"msgstr_socket_self"
#define SMARTPOWER_SERVER_IP		"211.160.203.86"		// the ip of baidu.com is "61.135.169.105"
#define	SMARTLIFE_SERVER_PORT		(9103)		// for smarthome apk

typedef enum{
	SOCKET_STATUS_EXCEPTION = -1,
	SOCKET_STATUS_CLOSED,
	SOCKET_STATUS_CREATE,
	SOCKET_STATUS_DISCONNECT,
	SOCKET_STATUS_CONNECTED,
	SOCKET_STATUS_REGISTING,
	SOCKET_STATUS_REGISTED,
	SOCKET_STATUS_UNREGIST
}SOCKET_STATUS_E;

typedef enum{
	SMARTLIFE_TCP_DISCONNECT = -1,
	SMARTLIFE_TCP_CONNECTING,
	SMARTLIFE_TCP_CONNECTED
}SMARTLIFE_CONNECT_STATUS_E;

static SOCKET_STATUS_E		g_socket_status = SOCKET_STATUS_CLOSED;
static int					g_fifo_fd = -1;
static char 				s_sendbuf[BUF_SIZE];				//buf of send
static int					s_sendbuf_len = 0;

static int sendToServer(int l_socket_fd,char *l_sendbuf, int buf_len);
static int recvFromServer(int l_socket_fd,char *l_recvbuf, int *recvbuf_size);
static int connectRetry(int l_socket_fd,struct sockaddr_in server_addr);
static void setKeepAlive(int l_socket_fd);

static int continue_myself()
{
	char fifo_str[FIFO_STR_SIZE];
	
	snprintf(fifo_str,sizeof(fifo_str),"%s",MSGSTR_SOCKET_SELF);
	if(-1==write(g_fifo_fd, fifo_str, strlen(fifo_str))){
		ERROROUT("write to fifo failed\n");
		return -1;
	}
	return 0;
}

int fifo_buf_clear(int fifo_fd, fd_set rdfds)
{
	if(fifo_fd<=0){
		DEBUG("this fd(%d) is invalid\n", fifo_fd);
		return -1;
	}

	char fifo_str[FIFO_STR_SIZE];
	if(FD_ISSET(fifo_fd, &rdfds)){
		memset(fifo_str, 0, FIFO_STR_SIZE);
		while(read(fifo_fd, fifo_str, sizeof(fifo_str))>0){
			DEBUG("clear from fifo_2_socket: %s\n", fifo_str);
		}
		return 0;
	}
	else{
		DEBUG("this fd(%d) can not be readed\n", fifo_fd);
		return -1;
	}
}

static int smartlife_tcp_close(int socket_fd)
{
	if(socket_fd>2){
		DEBUG("close smartlife tcp socket %d\n",socket_fd);
		
		close(socket_fd);
		socket_fd = -1;
	}
	else
		DEBUG("can not close such socket %d\n", socket_fd);
		
	return 0;
}

void *smartlife_connect_thread()
{									//send number
	int l_return = -1;											//return of function
	int l_socket_fd = -1;										//socket descriptor
	char l_recvbuf[BUF_SIZE];									//buf of recv
	int l_recvbuf_size = 0;
	char smartlife_connect_status[32];
	struct sockaddr_in server_addr;
	
	char sqlite_cmd[256];
	char server_ip[16];
	char server_port_str[32];
	int server_port = 8080;
	
	memset(server_ip, 0, sizeof(server_ip));
	memset(server_port_str,0,sizeof(server_port_str));
	
	int (*sqlite_cb)(char **, int, int, void *,unsigned int) = str_read_cb;
	snprintf(sqlite_cmd,sizeof(sqlite_cmd),"select value from global where name='SmarthomeServerIP';");
	int ret_sqlexec = sqlite_read(sqlite_cmd, server_ip, sizeof(server_ip), sqlite_cb);
	if(ret_sqlexec<=0){
		DEBUG("read no server_ip from db for smartlife\n");
	}
	else
		DEBUG("read server_ip: %s\n", server_ip);
	
	if(0==strlen(server_ip) || strlen(server_ip)>sizeof(server_ip)){
		snprintf(server_ip,sizeof(server_ip),"%s", SMARTPOWER_SERVER_IP);
		DEBUG("get invalid server_ip, use default value: %s\n",server_ip);
	}
	
	snprintf(sqlite_cmd,sizeof(sqlite_cmd),"select value from global where name='SmartLifePort';");
	ret_sqlexec = sqlite_read(sqlite_cmd, server_port_str, sizeof(server_port_str), sqlite_cb);
	if(ret_sqlexec<=0){
		DEBUG("read no server_port_str from db for smartlife\n");
	}
	else
		DEBUG("read server_port_str: %s\n", server_port_str);
		
	if(0==strlen(server_port_str) || strlen(server_port_str)>sizeof(server_port_str)){
		snprintf(server_port_str,sizeof(server_port_str),"%d", SMARTLIFE_SERVER_PORT);
		DEBUG("get invalid server_port_str, use default value: %s\n",server_port_str);
	}
	server_port = atoi(server_port_str);
	
	DEBUG("get smartlife server ip: %s, server port: %d\n", server_ip, server_port);
	
	//argument of function select
	fd_set rdfds;									//write/read descriptor

	g_socket_status = SOCKET_STATUS_CLOSED;
	
	if( mkfifo(FIFO_2_SOCKET, 0777)<0 && EEXIST!=errno ){
		ERROROUT("create FIFO_2_SOCKET:%s failed\n", FIFO_2_SOCKET);
		return NULL;
	}
	g_fifo_fd = open(FIFO_2_SOCKET, O_RDWR|O_NONBLOCK, 0);
	if(g_fifo_fd<0){
		ERROROUT("open %s failed\n",FIFO_2_SOCKET);
		return NULL;
	}
	else
		DEBUG("open FIFO_2_SOCKET with fd %d\n", g_fifo_fd);
	
	struct timeval tv_select = {7, 500000};
	int ret_select = -1;
	int max_fd = g_fifo_fd;

	continue_myself();
	
	sleep(9);
	
	while(1)
	{
		FD_ZERO(&rdfds);
		FD_SET(g_fifo_fd, &rdfds);
		if(l_socket_fd>2){
			FD_SET(l_socket_fd, &rdfds);
			if(l_socket_fd>max_fd)
				max_fd = l_socket_fd;
		}
		tv_select.tv_sec = 37;
		tv_select.tv_usec = 500000;
		ret_select = select(max_fd+1, &rdfds, NULL, NULL, &tv_select);
		if(ret_select<0){
			ERROROUT("select failed\n");
			return NULL;
		}
		else if(0==ret_select){
			DEBUG("timeout\n");
			continue;
		}
		else{
			;
		}
		
		switch(g_socket_status){
			case SOCKET_STATUS_CLOSED:
				DEBUG("SOCKET_STATUS_CLOSED\n");
				fifo_buf_clear(g_fifo_fd, rdfds);
				if (-1 == (l_socket_fd=socket(AF_INET,SOCK_STREAM,0)))
				{
					ERROROUT("create socket failed, sleep 17s and try again\n");
					sleep(17);
				}
				else{
					//monitor tcp link
					setKeepAlive(l_socket_fd);
					DEBUG("create socket(%d) success\n", l_socket_fd);
					g_socket_status = SOCKET_STATUS_CREATE;
				}
				continue_myself();
				break;
			case SOCKET_STATUS_CREATE:
				DEBUG("SOCKET_STATUS_CREATE\n");
			case SOCKET_STATUS_DISCONNECT:
				DEBUG("SOCKET_STATUS_DISCONNECT\n");				
				fifo_buf_clear(g_fifo_fd, rdfds);
				//set argumnet of sockaddr_in
				server_addr.sin_family=AF_INET;							//address family IPV4
				server_addr.sin_port=htons(server_port);				//port number
				//use inet_addr
				server_addr.sin_addr.s_addr=inet_addr(server_ip);		//IPv4 address
			//	//use inet_aton
			//	if (0 == inet_aton(g_serverip,&server_addr.sin_addr))	//IPv4 address
			//		ERROROUT("inet_aton error");
			//	//use inet_pton
			//	if (-1 != inet_pton(AF_INET,g_serverip,&server_addr.sin_addr))	//IPv4 address
			//		ERROROUT("inet_pton error");
				bzero(&(server_addr.sin_zero),8);						//filler, all 0

				if(-1==connectRetry(l_socket_fd,server_addr)){
					DEBUG("connect %s:%d failed, sleep 17s and try again\n", server_ip, server_port);
					g_socket_status = SOCKET_STATUS_DISCONNECT;
					sleep(17);
				}
				else{
					DEBUG("connect %s:%d success\n", server_ip, server_port);
					g_socket_status = SOCKET_STATUS_CONNECTED;
					
					snprintf(smartlife_connect_status,sizeof(smartlife_connect_status),"%d",SMARTLIFE_TCP_CONNECTED);
					msg_send2_UI(SMARTLIFE_CONNECT_STATUS,smartlife_connect_status,strlen(smartlife_connect_status));
				}
				continue_myself();
				break;
			case SOCKET_STATUS_CONNECTED:
				DEBUG("SOCKET_STATUS_CONNECTED\n");
				if(FD_ISSET(l_socket_fd, &rdfds)){
					memset(l_recvbuf, 0, sizeof(l_recvbuf));
					l_recvbuf_size = sizeof(l_recvbuf);
					l_return=recvFromServer(l_socket_fd,l_recvbuf,&l_recvbuf_size);

					if(0==l_return){
						DEBUG("recv from server[%d]: [%s], notify to UI\n", l_recvbuf_size,l_recvbuf);
						msg_send2_UI(SMARTLIFE_RECV,l_recvbuf,l_recvbuf_size);
					}
					else if(-1==l_return){
						// 如果socket提示可读，但是recv的结果为-1，则说明是对方关闭了socket，己方应当close掉socket重新开始
						DEBUG("socket can read, but read return -1. this tcp connect is closed by server\n");
						
						fifo_buf_clear(g_fifo_fd, rdfds);
						smartlife_tcp_close(l_socket_fd);
						g_socket_status = SOCKET_STATUS_DISCONNECT;
					}
				}
				else if(FD_ISSET(g_fifo_fd, &rdfds)){
					fifo_buf_clear(g_fifo_fd, rdfds);
					
					if(s_sendbuf_len>0){
						DEBUG("sending [%d][%s]\n",s_sendbuf_len,s_sendbuf);
						if ( 0==sendToServer(l_socket_fd,s_sendbuf,s_sendbuf_len)){
							DEBUG("send to server success\n");
						}
						else{
							DEBUG("send to server failed, reset the connect and try again\n");
							smartlife_tcp_close(l_socket_fd);
							g_socket_status = SOCKET_STATUS_DISCONNECT;
						}
						
						memset(s_sendbuf,0,sizeof(s_sendbuf));
						s_sendbuf_len = 0;
					}
				}
					
				break;
			
			case SOCKET_STATUS_EXCEPTION:
				DEBUG("SOCKET_STATUS_EXCEPTION\n");
				smartlife_tcp_close(l_socket_fd);
				g_socket_status = SOCKET_STATUS_DISCONNECT;
				break;
			
			default:
				DEBUG("default\n");
				fifo_buf_clear(g_fifo_fd, rdfds);
				DEBUG("this status(%d) of socket can not be dealed with\n", g_socket_status);
				continue_myself();
				sleep(60*3);
				break;
		}
	}
	
	close(l_socket_fd);
	l_socket_fd = -1;

	return NULL;
}

/***sendToServer() biref send information to server
 * 2011.11.4, liyang
 * param l_socket_fd[in], socket descriptor
 * param l_wrfds[in], write descriptor
 * param l_sendbuf[in][out], buf of send
 *
 * retval, 0 if successful or -1 failed
 * Version 1.0
 ***/
static int sendToServer(int l_socket_fd,char *l_sendbuf, int buf_len)
{
	struct timeval s_time={0,0};
	int ret_select = -1;
	int ret = -1;
	fd_set l_wrfds;
	FD_ZERO(&l_wrfds);

	if(l_socket_fd<3 || NULL==l_sendbuf || 0==buf_len){
		DEBUG("can not send to server, socket: %d\n", l_socket_fd);
		return ret;
	}
	
	FD_CLR(l_socket_fd,&l_wrfds);
	FD_ZERO(&l_wrfds);
	FD_SET(l_socket_fd,&l_wrfds);
	ret_select = select(l_socket_fd+1,NULL,&l_wrfds,NULL,&s_time);
	if ( ret_select<0)
	{
		DEBUG("select error\n");
		ret = -1;
	}
	else if( 0==ret_select )
	{
		DEBUG("select timeout\n");
		ret = -1;
	}
	else
	{
		if (FD_ISSET(l_socket_fd,&l_wrfds))
		{
			if ( -1 == (write(l_socket_fd,l_sendbuf,strlen(l_sendbuf))) )
			{
				DEBUG("write to socket failed\n");
				ret = -1;
			}
			else
			{
				DEBUG("write to socket success\n");
				ret = 0;
			}
		}
	}
	
	FD_CLR(l_socket_fd,&l_wrfds);
	return ret;
}

/***recvFromServer() biref recv information from server
 * 2011.11.4, liyang
 * param l_socket_fd[in], socket descriptor
 * param l_wrfds[in], write descriptor
 * param l_recvbuf[in][out], buf of recv
 *
 * retval, 0 if successful. -1 failed or 1 reconnect
 * Version 1.0
 ***/
#if 0
static int recvFromServer(int l_socket_fd,char *l_recvbuf, int *recv_buf_size)
{
	int ret = -1;
	int ret_recv = -1;
	fd_set l_rdfds;
	FD_ZERO(&l_rdfds);
	
	if(l_socket_fd<3 || NULL==l_recvbuf || 0==*recv_buf_size){
		DEBUG("can not send to server, socket: %d\n", l_socket_fd);
		return ret;
	}
	
	DEBUG("socket(%d) can be readed\n", l_socket_fd);
	
	ret_recv=recv(l_socket_fd,l_recvbuf,*recv_buf_size,0);
	//monitor tcp link,-1 out line . next time select will return 1 and recv return 0
	if (-1 == ret_recv)
	{
		DEBUG("out line!!!\n");
		ret = -1;
	}
	//server is out
	else if (0 == ret_recv)
	{
		DEBUG("server is out line!!!\n");
		ret = -1;
	}
	else{
		DEBUG("recv %d successfully\n", ret_recv);
		*recv_buf_size = ret_recv;
		ret = 0;
	}
	
	return ret;
}
#else
static int recvFromServer(int l_socket_fd,char *l_recv_buf, int *recv_buf_size)
{
	int ret_select = -1;					//select return
	int ret = -1;
	int ret_recv = -1;
	fd_set l_rdfds;
	FD_ZERO(&l_rdfds);
	
	if(l_socket_fd<3 || NULL==l_recv_buf || 0==*recv_buf_size){
		DEBUG("can not recv from server, socket: %d\n", l_socket_fd);
		return ret;
	}
	
	struct timeval s_time={0,500000};			/* perhaps this 500ms is too short */
	FD_CLR(l_socket_fd,&l_rdfds);
	FD_ZERO(&l_rdfds);
	FD_SET(l_socket_fd,&l_rdfds);
	ret_select=select(l_socket_fd+1,&l_rdfds,NULL,NULL,&s_time);
	if ( ret_select<0)
	{
		DEBUG("select error\n");
		ret = -1;
	}
	else if( 0==ret_select )
	{
		DEBUG("select timeout\n");
		ret = 1;
	}
	else
	{
		if (FD_ISSET(l_socket_fd,&l_rdfds))
		{
			DEBUG("socket(%d) can be readed\n", l_socket_fd);
			
			ret_recv=recv(l_socket_fd,( l_recv_buf+(*recv_buf_size) ),*recv_buf_size-1,0);
			//monitor tcp link,-1 out line . next time select will return 1 and recv return 0
			if (-1 == ret_recv)
			{
				DEBUG("out line!!!\n");
				ret = -1;
			}
			//server is out
			else if (0 == ret_recv)
			{
				DEBUG("server is out line!!!\n");
				ret = -1;
			}
			else{
				*recv_buf_size += ret_recv;
				DEBUG("recv [%d]%d successfully\n", *recv_buf_size,ret_recv);
				ret = 0;
			}
		}
		else
		{
			DEBUG("another socket but not %d can be readed\n", l_socket_fd);
			ret = -1;
		}
	}
	
	FD_CLR(l_socket_fd,&l_rdfds);
	return ret;
}
#endif

/***getMacAddr() biref get local device's Mac address
 * 2011.11.4, liyang
 * param l_socket_fd[in], socket descriptor
 * param l_l_mac_addr[in][out], buf of MacAddr
 *
 * retval, 0 if successful or -1 failed
 * Version 1.0
 ***/
int getMacAddr(int l_socket_fd,char* l_mac_addr)
{
	struct ifreq ifr_mac;
	memset(&ifr_mac,0,sizeof(ifr_mac));
	strncpy(ifr_mac.ifr_name, "eth0", sizeof(ifr_mac.ifr_name)-1);
	if( (ioctl( l_socket_fd, SIOCGIFHWADDR, &ifr_mac)) < 0)
	{
		printf("mac ioctl error\n");
		return -1;
	}
	sprintf(l_mac_addr,"%02x%02x%02x%02x%02x%02x",
			(unsigned char)ifr_mac.ifr_hwaddr.sa_data[0],
			(unsigned char)ifr_mac.ifr_hwaddr.sa_data[1],
			(unsigned char)ifr_mac.ifr_hwaddr.sa_data[2],
			(unsigned char)ifr_mac.ifr_hwaddr.sa_data[3],
			(unsigned char)ifr_mac.ifr_hwaddr.sa_data[4],
			(unsigned char)ifr_mac.ifr_hwaddr.sa_data[5]);

	return 0;
}

/***connectRetry() biref reconnect
 * 2011.11.5, liyang
 * param l_socket_fd[in], socket descriptor
 * param server_addr[in], argumnet of socketaddr_in
 *
 * retval void
 * Version 1.0
 ***/
static int connectRetry(int l_socket_fd,struct sockaddr_in server_addr)
{
	int l_sec=1;				//sleep time

	if(l_socket_fd<3){
		DEBUG("this fucking socket %d is invalid\n", l_socket_fd);
		return -1;
	}

	DEBUG("connecting to server...\n");
	while(1)
	{
		//connect
		if ( -1 == connect(l_socket_fd,(struct sockaddr*)&server_addr,sizeof(struct sockaddr)) )
		{
			if (l_sec < MAXSLEEP)
			{
				DEBUG("connect error,retry after %d sec......\n", l_sec);
				sleep(l_sec);
				l_sec<<=1;
			}
			else	//if l_sec<MAXSLEEP,set l_sec=1
			{
				DEBUG("connect error,retry after %d sec......\n",l_sec);
				sleep(l_sec);
			}
		}
		else{
			DEBUG("connect success\n");
			break;
		}
	}
	return 0;
}

/***setKeepAlive() biref monitor tcp link
 * 2011.11.7, liyang
 * param l_socket_fd[in], socket descriptor
 *
 * retval void
 * Version 1.0
 ***/
static void setKeepAlive(int l_socket_fd)
{
	if(l_socket_fd<3)
		return;
	
	socklen_t l_keepalive=1;		//set keepalive
	socklen_t l_keepidle=5;			//the beginning of the first keepalive detection before the TCP air closing time
	socklen_t l_keepinterval=5;		//two keepalive detection interval
	socklen_t l_keepcount=3;		//determination of disconnected before the keepalive detection times

	setsockopt( l_socket_fd , SOL_SOCKET , SO_KEEPALIVE , (const char*)&l_keepalive , sizeof(l_keepalive) );
	setsockopt( l_socket_fd , SOL_TCP , TCP_KEEPIDLE , (const char*)&l_keepidle , sizeof(l_keepidle) );
	setsockopt( l_socket_fd , SOL_TCP , TCP_KEEPINTVL , (const char*)&l_keepinterval , sizeof(l_keepinterval) );
	setsockopt( l_socket_fd , SOL_TCP , TCP_KEEPCNT , (const char*)&l_keepcount , sizeof(l_keepcount) );
}

int smartlife_send(char *buf, int buf_len)
{
	int valid_len = buf_len>sizeof(s_sendbuf)?sizeof(s_sendbuf):buf_len;
	
	DEBUG("[len=%d -> %d][%s]\n", buf_len,valid_len,buf);
	
	memcpy(s_sendbuf,buf,valid_len);
	s_sendbuf_len = valid_len;
	
	continue_myself();
	
	return 0;
}

int smartlife_connect(char *buf, int buf_len)
{
	if(NULL==buf || 0>=buf_len){
		DEBUG("invalid args\n");
	}
	
	continue_myself();
	
	return 0;
}

int smartlife_connect_init()
{
	if(-1==dir_exist_ensure(FIFO_DIR)){
		DEBUG("dir_exist_ensure(%s) failed\n", FIFO_DIR);
		return -1;
	}
	
	pthread_t tidsocket_mainloop;
	pthread_create(&tidsocket_mainloop, NULL, smartlife_connect_thread, NULL);
	DEBUG("create smartlife mainthread finish\n");
	
	return 0;
}

