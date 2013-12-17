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

#ifdef SMARTLIFE_LC

#define MAXSLEEP					(128)
#define BUF_SIZE_DFT				(40960)
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

static SOCKET_STATUS_E		g_socket_status = SOCKET_STATUS_CLOSED;
static int					g_fifo_fd = -1;
static char 				s_sendbuf[BUF_SIZE_DFT];				//buf to send
static char *				s_recvbuf = NULL;				//buf for send, malloc or realloc
static unsigned int			s_recvbuf_size = 0;
static int					s_sendbuf_len = 0;


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

static int smartlife_tcp_close(int *socket_fd)
{
	if(*socket_fd>2){
		DEBUG("close smartlife tcp socket %d\n",*socket_fd);
		
		close(*socket_fd);
		*socket_fd = -1;
	}
	else
		DEBUG("can not close such socket %d\n", *socket_fd);
		
	return 0;
}

void setnonblocking(int sock)
{
	int  opts;
	
	opts = fcntl(sock,F_GETFL);
	
	if (opts < 0 )
	{
		perror( " fcntl(sock,GETFL) " );
		return;
	}
	
	opts  =  opts | O_NONBLOCK;
	if (fcntl(sock,F_SETFL,opts) < 0 )
	{
		perror( " fcntl(sock,SETFL,opts) " );
		return;
	}
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


static int recvFromServer(int l_socket_fd)
{
	int ret_select = -1;					//select return
	int ret = -1;
	int ret_recv = -1;
	int total_recv_len = 0;
	int recvbuf_free = 0;
	char *recvbuf_tmp = NULL;
	int recv_timeout_try = 0;
	struct timeval s_time={0,500000};			/* perhaps this 500ms is too short */
	
	fd_set l_rdfds;
	FD_ZERO(&l_rdfds);
	
	if(l_socket_fd<3){
		DEBUG("can not recv from server, socket: %d\n", l_socket_fd);
		return ret;
	}
	
	if(NULL==s_recvbuf){
		s_recvbuf_size = BUF_SIZE_DFT;
		s_recvbuf = malloc(s_recvbuf_size);
		DEBUG("s_recvbuf is free, malloc it at %p\n",s_recvbuf);
	}
	
	if(s_recvbuf){
		while(1){
			FD_CLR(l_socket_fd,&l_rdfds);
			FD_ZERO(&l_rdfds);
			FD_SET(l_socket_fd,&l_rdfds);
			s_time.tv_sec = 0;
			s_time.tv_usec = 500000;
			ret_select=select(l_socket_fd+1,&l_rdfds,NULL,NULL,&s_time);
			if ( ret_select<0)
			{
				ERROROUT("select error\n");
				ret = -1;
				break;
			}
			else if( 0==ret_select )
			{
				recv_timeout_try ++;
				
				ret = total_recv_len;
				if(recv_timeout_try>=7){
					DEBUG("recv select timeout finish, total_recv_len=%d\n",total_recv_len);
					break;
				}
				else
					DEBUG("select timeout for %d times\n",recv_timeout_try);
			}
			else
			{
				if (FD_ISSET(l_socket_fd,&l_rdfds))
				{
//					DEBUG("socket(%d) can be readed\n", l_socket_fd);
					
					recvbuf_free = s_recvbuf_size-1-total_recv_len;
					if(recvbuf_free<1500){
						DEBUG("s_recvbuf(%p) need realloc: size %d, total_recv_len %d, recvbuf_free %d",s_recvbuf,s_recvbuf_size,total_recv_len,recvbuf_free);
						
						s_recvbuf_size += BUF_SIZE_DFT;
						recvbuf_tmp = realloc(s_recvbuf,s_recvbuf_size);
						if(recvbuf_tmp){
							DEBUG("realloc s_recvbuf(%p) to %p, resize as %d\n", s_recvbuf,recvbuf_tmp,s_recvbuf_size);
							s_recvbuf = recvbuf_tmp;
						}
						else{
							DEBUG("can not realloc s_recvbuf!!!\n");
							ret = -1;
							break;
						}
					}
					
					recvbuf_free = s_recvbuf_size-1-total_recv_len;
					ret_recv=recv(l_socket_fd,s_recvbuf+total_recv_len,recvbuf_free,0);
					//monitor tcp link,-1 out line . next time select will return 1 and recv return 0
					if (-1 == ret_recv)
					{
						ERROROUT("out line -1!!!\n");
						ret = -1;
						
	//					if(EAGAIN==errno || EWOULDBLOCK==errno){
	//						DEBUG("recv finish\n");
	//						ret = 0;
	//					}
						
						break;
					}
					//server is out
					else if (0 == ret_recv)
					{
						DEBUG("server is out line 0!!!\n");
						ret = -1;
						break;
					}
					else{
						recvbuf_tmp = s_recvbuf+total_recv_len;
						*(recvbuf_tmp+ret_recv) = '\0';
						
						total_recv_len += ret_recv;
						
						if(strstr(recvbuf_tmp,"!#")){
							DEBUG("recv %d[%d] in %d successfully, receive finished\n", ret_recv,total_recv_len,s_recvbuf_size);
							ret = total_recv_len;
							break;
						}
						else{
							//DEBUG("recv %d[%d] in %d successfully\n", ret_recv,total_recv_len,s_recvbuf_size);
						}
					}
				}
				else
				{
					DEBUG("another socket but not %d can be readed\n", l_socket_fd);
					ret = -1;
				}
			}
		}
		
		FD_CLR(l_socket_fd,&l_rdfds);
	}
	else{
		DEBUG("malloc s_recvbuf failed!!!\n");
		ret = -1;
	}
	
	return ret;
}

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


void *smartlife_connect_thread()
{									//send number
	int l_return = -1;											//return of function
	int l_socket_fd = -1;										//socket descriptor
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
	int ret_sqlexec = smartlife_sqlite_read(sqlite_cmd, server_ip, sizeof(server_ip), sqlite_cb);
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
	ret_sqlexec = smartlife_sqlite_read(sqlite_cmd, server_port_str, sizeof(server_port_str), sqlite_cb);
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
	
	sleep(7);
	
	while(1)
	{
		FD_ZERO(&rdfds);
		FD_SET(g_fifo_fd, &rdfds);
		max_fd = g_fifo_fd;
		
		if(l_socket_fd>2){
			FD_SET(l_socket_fd, &rdfds);
			if(l_socket_fd>max_fd)
				max_fd = l_socket_fd;
		}
		tv_select.tv_sec = 37;
		tv_select.tv_usec = 500000;
		ret_select = select(max_fd+1, &rdfds, NULL, NULL, &tv_select);
		if(ret_select<0){
			ERROROUT("select failed, g_fifo_fd=%d, l_socket_fd=%d, max_fd=%d\n",g_fifo_fd,l_socket_fd,max_fd);
			return NULL;
		}
		else if(0==ret_select){
//			DEBUG("%ld timeout, g_fifo_fd=%d, l_socket_fd=%d, max_fd=%d\n",tv_select.tv_sec,g_fifo_fd,l_socket_fd,max_fd);
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
					//setnonblocking(l_socket_fd);
					
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
					DEBUG("connect %s:%d failed, sleep 3s and try again\n", server_ip, server_port);
					g_socket_status = SOCKET_STATUS_DISCONNECT;
					sleep(3);
				}
				else{
					DEBUG("connect %s:%d success\n", server_ip, server_port);
					g_socket_status = SOCKET_STATUS_CONNECTED;
					
					memset(s_sendbuf,0,sizeof(s_sendbuf));
					s_sendbuf_len = 0;
					snprintf(smartlife_connect_status,sizeof(smartlife_connect_status),"%d",SOCKET_STATUS_CONNECTED);
					msg_send2_UI(SMARTLIFE_CONNECT_STATUS,smartlife_connect_status,strlen(smartlife_connect_status));
				}
//				continue_myself();
				break;
			case SOCKET_STATUS_CONNECTED:
				DEBUG("SOCKET_STATUS_CONNECTED\n");
				if(FD_ISSET(l_socket_fd, &rdfds)){
					l_return=recvFromServer(l_socket_fd);

					if(l_return>0){
						DEBUG("recv from server[%d], notify to UI\n", l_return);
						msg_send2_UI(SMARTLIFE_RECV,s_recvbuf,l_return);
					}
					else{	// if(-1==l_return)
						// 如果socket提示可读，但是recv的结果为-1，则说明是对方关闭了socket，己方应当close掉socket重新开始
						DEBUG("socket can read, but read return %d. this tcp connect is closed by server\n",l_return);
						
						fifo_buf_clear(g_fifo_fd, rdfds);
						smartlife_tcp_close(&l_socket_fd);
						DEBUG("after socket close, l_socket_fd=%d\n",l_socket_fd);
						l_socket_fd = -1;
						g_socket_status = SOCKET_STATUS_CLOSED;
					}
				}
				else if(FD_ISSET(g_fifo_fd, &rdfds)){
					fifo_buf_clear(g_fifo_fd, rdfds);
					
					if(s_sendbuf_len>0){
						DEBUG("sending %d\n",s_sendbuf_len);
						if ( 0==sendToServer(l_socket_fd,s_sendbuf,s_sendbuf_len)){
							DEBUG("send to server success\n");
						}
						else{
							DEBUG("send to server failed, reset the connect and try again\n");
							smartlife_tcp_close(&l_socket_fd);
							g_socket_status = SOCKET_STATUS_DISCONNECT;
						}
						
						memset(s_sendbuf,0,sizeof(s_sendbuf));
						s_sendbuf_len = 0;
					}
				}
					
				break;
			
			case SOCKET_STATUS_EXCEPTION:
				DEBUG("SOCKET_STATUS_EXCEPTION\n");
				smartlife_tcp_close(&l_socket_fd);
				g_socket_status = SOCKET_STATUS_DISCONNECT;
				break;
			
			default:
				DEBUG("default\n");
				fifo_buf_clear(g_fifo_fd, rdfds);
				DEBUG("this status(%d) of socket can not be dealed with\n", g_socket_status);
				//continue_myself();
				sleep(3);
				break;
		}
	}
	
	close(l_socket_fd);
	l_socket_fd = -1;

	return NULL;
}


int smartlife_send(char *buf, int buf_len)
{
	int valid_len = buf_len>sizeof(s_sendbuf)?sizeof(s_sendbuf):buf_len;
	
	DEBUG("len=%d -> %d\n", buf_len,valid_len);
	
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

int smartlife_connect_status_get(char *buf, int buf_size)
{
	snprintf(buf,buf_size,"%d",g_socket_status);
	
	return 0;
}

#endif
