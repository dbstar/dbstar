#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <string.h>
#include <arpa/inet.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <semaphore.h>
#include <unistd.h>
#include <asm/ioctls.h>
#include <sys/ioctl.h>
#include <ctype.h>
#include <fcntl.h>
#include <sys/param.h>
#include <net/if.h>
#include <netinet/in.h>
#include <net/if_arp.h>
#include <arpa/inet.h>
#include <pthread.h>
#include <errno.h>

#include "common.h"
#include "porting.h"
#include "dvb.h"
#include "multicast.h"
#include "sqlite.h"

#define MAXINTERFACES   (16)
#define MULTI_RECV_SIZE	(1316)
#define MULTI_BUF_SIZE	(12*1024*1316)	/* larger than 16M */
#define PUERTO			(5000)
#define GRUPO			"224.0.0.1"

/*
read可以追上write，此时buffer为空；但write必须举例read一个安全距离BUF_GAP_LEN
*/
#define BUF_GAP_LEN		(188)

#define MULTI_BUF_POINTER_MOVE(p,len) (p)=(((p)+(len))%MULTI_BUF_SIZE)

typedef struct{
	unsigned char buf[MULTI_BUF_SIZE];
	unsigned int p_read;
	unsigned int p_write;
	int full_flag;	// 当p_read等于p_write时，必须用这个flag指明buf目前是空闲还是充满。0表示有空闲（部分空闲或全部空闲）
}MULTI_BUF;

static sem_t s_sem_multi_buf;
static MULTI_BUF s_multi_buf;
static char s_tmp_recv_filename[64];

static int parse_ts_packet(unsigned char *ptr, int write_ptr, int *read);

static void multi_buf_reset()
{
	memset(s_multi_buf.buf, 0, sizeof(s_multi_buf.buf));
	s_multi_buf.p_read = 0;
	s_multi_buf.p_write = 0;
	s_multi_buf.full_flag = 0;
}

int multicast_init(char *tmp_file_name)
{
	if(-1==sem_init(&s_sem_multi_buf, 0, 1)){
		DEBUG("s_sem_equipment init failed\n");
		return -1;
	}
	multi_buf_reset();
	
	memset(s_tmp_recv_filename, 0, sizeof(s_tmp_recv_filename));
	if(NULL!=tmp_file_name){
		strncpy(s_tmp_recv_filename, tmp_file_name, (sizeof(s_tmp_recv_filename)-1));
	}
	
	return 0;
}

int igmp_init()
{
	char data_source[64];
	memset(data_source, 0, sizeof(data_source));
	if(-1==data_source_get(data_source, sizeof(data_source)-1)){
		DEBUG("has no data source to process, exit from %s()\n", __FUNCTION__);
		return -1;
	}
	multicast_init(NULL);
	if(-1==multicast_add(data_source)){
		DEBUG("igmp join failed\n");
		return -1;
	}
	return 0;
}

static int s_total_push = 0;
static int s_KBps_time_pin = 0;
static int s_printf_pin = 0;
#if 0
// 返回值为拷贝的字节数，因此即便是错误，也不返回负值，而是0
static int ring_buffer_write(unsigned char *s_buf, unsigned int s_len)
{
	if(NULL==s_buf || 0==s_len){
		DEBUG("some arguments are invalid, len=%d\n", s_len);
		return 0;
	}
	
	unsigned int ret = 0;
	int write_twice_flag = 0;
	if(0==s_multi_buf.full_flag){
		if(s_multi_buf.p_write==s_multi_buf.p_read){
			s_multi_buf.p_write = 0;
			s_multi_buf.p_read = 0;
			ret = MIN_LOCAL(MULTI_BUF_SIZE, s_len);
			//DEBUG("ret = %d\n", ret);
		}
		else if(s_multi_buf.p_write>s_multi_buf.p_read){
			ret = MIN_LOCAL((MULTI_BUF_SIZE-s_multi_buf.p_write), s_len);
			if(s_multi_buf.p_read>0)
				write_twice_flag = 1;
			//DEBUG("ret = %d\n", ret);
		}
		else{	// s_multi_buf.p_write<s_multi_buf.p_read
			ret = MIN_LOCAL((s_multi_buf.p_read-s_multi_buf.p_write), s_len);
			//DEBUG("ret = %d\n", ret);
		}
		
		memcpy(s_multi_buf.buf+s_multi_buf.p_write, s_buf, ret);
		MULTI_BUF_POINTER_MOVE(s_multi_buf.p_write, ret);
			
		if(s_multi_buf.p_write==s_multi_buf.p_read)
			s_multi_buf.full_flag = 1;
		
		if(write_twice_flag && ret<s_len){
			DEBUG("write %d/%dBs, left %dBs for twice\n", ret, s_len, s_len-ret);
			ret += ring_buffer_write(s_buf+ret, s_len-ret);
		}
	}
	else{
		ret = 0;
		DEBUG("multicast buf full, p_read=%d, p_write=%d, write %d/%d for this call\n", s_multi_buf.p_read, s_multi_buf.p_write, ret, s_len);
	}
	
	printf("++++ %d/%d, w=%d, r=%d, fflag=%d\n", ret, s_len, s_multi_buf.p_write, s_multi_buf.p_read, s_multi_buf.full_flag);
	
//	DEBUG("has wrote, p_read=%d, p_write=%d, write %d/%d for this call\n", s_multi_buf.p_read, s_multi_buf.p_write, ret, s_len);
	return ret;
}
#endif

// 返回值为拷贝的字节数，因此即便是错误，也不返回负值，而是0
static int ring_buffer_read(unsigned char *d_buf, unsigned int d_len)
{
	if(NULL==d_buf || 0==d_len){
		DEBUG("some arguments are invalid, len=%d\n", d_len);
		return 0;
	}
	
	unsigned int ret = 0;
	int read_twice_flag = 0;
	if(s_multi_buf.p_write==s_multi_buf.p_read){
		if(0==s_multi_buf.full_flag){
			//DEBUG("multicast ring buffer is empty\n");
			ret = 0;
		}
		else{
			ret = MIN_LOCAL((MULTI_BUF_SIZE-s_multi_buf.p_read), d_len);
			if(s_multi_buf.p_write>0)
				read_twice_flag = 1;
		}
	}
	else if(s_multi_buf.p_write>s_multi_buf.p_read){
		ret = MIN_LOCAL((s_multi_buf.p_write-s_multi_buf.p_read), d_len);
	}
	else{	// s_multi_buf.p_write<s_multi_buf.p_read
		ret = MIN_LOCAL((MULTI_BUF_SIZE-s_multi_buf.p_read), d_len);
		if(s_multi_buf.p_write>0)
			read_twice_flag = 1;
	}
	
	if(ret>0){
		memcpy(d_buf, s_multi_buf.buf+s_multi_buf.p_read, ret);
		MULTI_BUF_POINTER_MOVE(s_multi_buf.p_read, ret);
			
		if(ret>0)
			s_multi_buf.full_flag = 0;
		
		if(read_twice_flag && (d_len>ret)){
			DEBUG("read %d/%dBs, %dBs for twice\n", ret, d_len, d_len-ret);
			ret += ring_buffer_read(d_buf+ret, d_len-ret);
		}
	}
//	else	//if(0==ret)
//		DEBUG("has readed, p_read=%d, p_write=%d, read %d/%d for this call\n", s_multi_buf.p_read, s_multi_buf.p_write, ret, d_len);
	
	return ret;
}

#ifdef WRITE2RINGBUF_DIRCTLY
static unsigned char *ring_buf_space(int *space_size)
{
	unsigned char *space_p = NULL;
	
	sem_wait(&s_sem_multi_buf);
	if(0==s_multi_buf.full_flag){
		if(s_multi_buf.p_write==s_multi_buf.p_read){
			s_multi_buf.p_write = 0;
			s_multi_buf.p_read = 0;
			*space_size = MULTI_BUF_SIZE;
			space_p = s_multi_buf.buf;
			//DEBUG("ret = %d\n", ret);
		}
		else if(s_multi_buf.p_write>s_multi_buf.p_read){
			*space_size = MULTI_BUF_SIZE-s_multi_buf.p_write;
			space_p = s_multi_buf.buf + s_multi_buf.p_write;
			//DEBUG("ret = %d\n", ret);
		}
		else{	// s_multi_buf.p_write<s_multi_buf.p_read
			*space_size = s_multi_buf.p_read-s_multi_buf.p_write;
			space_p = s_multi_buf.buf + s_multi_buf.p_write;
			//DEBUG("ret = %d\n", ret);
		}
	}
	else{
		*space_size = 0;
		space_p = NULL;
		DEBUG("multicast buf full, p_read=%d, p_write=%d, write %d/%d for this call\n", s_multi_buf.p_read, s_multi_buf.p_write, ret, s_len);
	}
	sem_post(&s_sem_multi_buf);
	
	return space_p;
}

static int ring_buf_has_wrote(unsigned int len)
{
	if(0==len)
		return -1;
	
	int ret = 0;
	
	sem_wait(&s_sem_multi_buf);
	MULTI_BUF_POINTER_MOVE(s_multi_buf.p_write, len);
		
	if(s_multi_buf.p_write==s_multi_buf.p_read)
		s_multi_buf.full_flag = 1;
	
	ret = 0;
	sem_post(&s_sem_multi_buf);
	
	return ret;
}
#endif

static int multi_buf_write(unsigned char *buf, unsigned int len)
{
	if(NULL==buf || 0==len){
		DEBUG("some arguments are invalid, len=%d\n", len);
		return -1;
	}
	
	int ret = 0;
#if 0
	sem_wait(&s_sem_multi_buf);
	ret = ring_buffer_write(buf, len);
	sem_post(&s_sem_multi_buf);
#else
	
//	printf("igmp w:%d, r:%d\n", s_multi_buf.p_write, s_multi_buf.p_read);
	unsigned int space_len = 0;
	if(s_multi_buf.p_write >= s_multi_buf.p_read){
		space_len = MULTI_BUF_SIZE-s_multi_buf.p_write + s_multi_buf.p_read - BUF_GAP_LEN;
		
		if(space_len>0){
			ret = space_len>len?len:space_len;
			
			int tail_len = MULTI_BUF_SIZE-s_multi_buf.p_write;
			//DEBUG("space_len=%d, len=%d, ret=%d, tail_len=%d\n", space_len, len, ret, tail_len);
			if(ret>tail_len){
				memcpy(s_multi_buf.buf + s_multi_buf.p_write, buf, tail_len);
				memcpy(s_multi_buf.buf, buf+tail_len, ret-tail_len);
				
				s_multi_buf.p_write = ret-tail_len;
			}
			else{
				memcpy(s_multi_buf.buf + s_multi_buf.p_write, buf, ret);
				s_multi_buf.p_write = (s_multi_buf.p_write+ret)%MULTI_BUF_SIZE;
			}
		}
		else{
			ret = 0;
		}
	}
	else{
		space_len = s_multi_buf.p_read - s_multi_buf.p_write - BUF_GAP_LEN;
		
		if(space_len>0){
			ret = space_len>len?len:space_len;
			
			memcpy(s_multi_buf.buf + s_multi_buf.p_write, buf, ret);
			s_multi_buf.p_write += ret;
		}
		else{
			ret = 0;
		}
	}
	
//	printf("+%d/%d, w=%d, r=%d %s\n", ret, len, s_multi_buf.p_write, s_multi_buf.p_read, space_len<=0?"FULL":"OK");
	
	s_total_push += ret;
	if(0==s_KBps_time_pin)
		s_KBps_time_pin = time(NULL);
	int footprint = 25;
	if((s_total_push>>footprint)>s_printf_pin)
	{
		s_printf_pin = (s_total_push>>footprint);
		int timp_span = (time(NULL)-s_KBps_time_pin);
		DEBUG("%dMBs\t|%dKBs\t|%ds\n", s_total_push>>20, s_total_push>>10, timp_span);
	}
	
#endif	
	return ret;
}

int multi_buf_read(unsigned char *buf, unsigned int len)
{
	if(NULL==buf || 0==len){
		DEBUG("some arguments are invalid, len=%d\n", len);
		return -1;
	}
	
	int ret = 0;
	sem_wait(&s_sem_multi_buf);
	ret = ring_buffer_read(buf, len);
	sem_post(&s_sem_multi_buf);
	
	return ret;
}


#define USE_SELECT_IN_MULTI
//#define SAVE_LOCAL_FILE
void *igmp_join_thread(void *multi_addr)
{
	char *p_multi_addr = (char *)multi_addr;
	DEBUG("multicast addr: %s\n", p_multi_addr);
    char if_ip[16] = {0};
	int sock, opt;
	struct sockaddr_in sin;
	int sizeof_sin = -1;
    int recv_len = -1;
    int ret = -1;
    unsigned char buf[MULTI_RECV_SIZE];
    struct ip_mreq ipmreq;

#ifdef USE_SELECT_IN_MULTI
	struct timeval tv_select = {71, 717000};
	fd_set rdfds;
	int ret_select = -1;
#endif
#ifdef SAVE_LOCAL_FILE
	FILE* fp = NULL;
#endif

	char multi_ip[16];
	int multi_port = 3000;

	p_multi_addr += strlen("igmp://");
	char *p_colon = strchr(p_multi_addr, ':');
	memset(multi_ip, 0, sizeof(multi_ip));
	if(p_colon){
		strncpy(multi_ip, p_multi_addr, abs(p_colon-p_multi_addr)>(sizeof(multi_ip)-1)?(sizeof(multi_ip)-1):abs(p_colon-p_multi_addr));
		p_colon ++;
		multi_port = atoi(p_colon);
	}
	else{
		strncpy(multi_ip, p_multi_addr, sizeof(multi_ip)-1);
	}
	DEBUG("multicast ip: %s, port: %d\n", multi_ip, multi_port);
	
	sleep(1);
	memset(if_ip, 0, sizeof(if_ip));
#if 0
	strcpy(if_ip, "192.168.1.252");
#else
	if(-1==ifconfig_get("eth0", if_ip, NULL, NULL)){
		DEBUG("get eth0 ip failed\n");
		return NULL;
	}
	else
		DEBUG("get eth0 ip: %s\n", if_ip);
#endif
	
	bzero((char *)&sin, sizeof(sin));
	sin.sin_family = AF_INET;
	sin.sin_addr.s_addr = inet_addr( multi_ip );
	sin.sin_port = htons(multi_port);

	if ((sock = socket( AF_INET, SOCK_DGRAM, 0)) == -1) {
		DEBUG("Error opening socket");
		return NULL;
	}
	DEBUG("create socket %d\n", sock);

	opt = 1;
	if( ioctl( sock,  FIONBIO, (int)&opt ) < 0 ){
		DEBUG("set nonblock mode failed!\n");
		ret = -1;
		goto END;
	}
	if (setsockopt(sock, SOL_SOCKET, SO_REUSEADDR, (void*)&opt, sizeof(opt)) < 0) {
		DEBUG("setsockopt(allow multiple socket use) failed\n");
		ret = -1;
		goto END;
	}

	if (bind(sock, (struct sockaddr *)&sin, sizeof(sin)) < 0) {
		DEBUG("call to bind failed\n");
		ret = -1;
		goto END;
	}
	DEBUG("set FIONBIO, setsockopt(SO_REUSEADDR), bind, ok\n");

	opt = 1024 * 128;
	if (setsockopt(sock, SOL_SOCKET, SO_RCVBUF, (void*)&opt, sizeof(opt)) < 0){
		DEBUG("Can't change system network size (wanted size = %d)\n", opt);
	}
	/*
	opt = 1316 * 8;
	if (osex_setsockopt(sock, SOL_SOCKET, SO_RCVLOWAT, (void*)&opt, sizeof(opt)) < 0){
		TDY_PERROR("Can't change system csv  lowat(wanted size = %d)", opt);
	}
	*/
	/*
	if(1 == yx_pppoe_status()){
		if( osex_ipaddr_get("pppoe0", if_ip)==0 ){
			ipmreq.imr_multiaddr.s_addr = inet_addr(multiaddr);
			ipmreq.imr_interface.s_addr = inet_addr(if_ip);
			TDY_DEBUG(("PPPOE IPTV ENTER %s\n", inet_ntoa((struct in_addr)(ipmreq.imr_interface))));
			if (osex_setsockopt(sock, IPPROTO_IP, IP_ADD_MEMBERSHIP, (char *)&ipmreq, sizeof(ipmreq)) < 0){
				TDY_PERROR("Error in setsocket(add membership)");
				osex_close( sock );
				return -1;
			}
			return sock;
		}		
	}
	*/

	ipmreq.imr_multiaddr.s_addr = inet_addr(multi_ip);
	ipmreq.imr_interface.s_addr = inet_addr(if_ip);
	if (setsockopt(sock, IPPROTO_IP, IP_ADD_MEMBERSHIP, (char *)&ipmreq, sizeof(ipmreq)) < 0){
		DEBUG("Error in setsocket(add membership)\n");
		ret = -1;
		goto END;
	}
	DEBUG("add membership OK\n");

	sizeof_sin = sizeof(sin);

#ifdef SAVE_LOCAL_FILE	
	if(strlen(s_tmp_recv_filename)>0){
		fp = fopen(s_tmp_recv_filename, "w");
		if(NULL==fp)
			ERROROUT("tmp recv file open failed\n");
	}
#endif
	
	while(1){
#ifdef USE_SELECT_IN_MULTI
		FD_ZERO(&rdfds);
		FD_SET(sock, &rdfds);
		tv_select.tv_sec = 3;
		tv_select.tv_usec = 717000;
		ret_select = select(sock+1, &rdfds, NULL, NULL, &tv_select);
		if(ret_select<0){
			ERROROUT("select faild\n");
			ret = -1;
			break;
		}
		else if(0==ret_select){
			DEBUG("timeout for multicast select\n");
			continue;
		}
		if (FD_ISSET(sock,&rdfds))
		{
			;
		}
		else{
			DEBUG("other id is calling, but not multicast\n");
			continue;
		}
#endif
		recv_len = recvfrom(sock, buf, MULTI_RECV_SIZE, 0, (struct sockaddr *)&sin, (socklen_t*)&sizeof_sin);
		if( recv_len > 0 ){
			//DEBUG("recv_len=%d\n", recv_len);
#ifdef SAVE_LOCAL_FILE
			if(NULL!=fp){
				if(1!=fwrite(buf, recv_len, 1, fp))
					ERROROUT("write to tmp recv file failed\n");
			}
#endif
			multi_buf_write(buf, recv_len);
			
		}
		else{
			
#ifdef USE_SELECT_IN_MULTI
			DEBUG("recv_len=%d\n", recv_len);
			perror("recvfrom");
#endif
#ifdef SAVE_LOCAL_FILE
			fflush(fp);
#endif

		}
	}
END:
	if(sock>=0)
		close(sock);

#ifdef SAVE_LOCAL_FILE
	if(fp!=NULL){
		fclose(fp);
		fp = NULL;
	}
#endif

	DEBUG("igmp join thread exit\n");
		
	return NULL;
}

void *soft_dvb_thread()
{
	int left = 0;
	
	while(1)
	{
		if(s_multi_buf.p_write>s_multi_buf.p_read)
			left = s_multi_buf.p_write - s_multi_buf.p_read;
		else if(s_multi_buf.p_write == s_multi_buf.p_read)
			left = 0;
		else	// if(s_multi_buf.p_write < s_multi_buf.p_read)
			left = MULTI_BUF_SIZE - s_multi_buf.p_read + s_multi_buf.p_write;
		
//		printf("left: %d\n", left);
		if(left<188){
			usleep(10000);
			continue;
		}
		
		parse_ts_packet(s_multi_buf.buf,s_multi_buf.p_write,&(s_multi_buf.p_read));
//		printf("-188 w=%d, r=%d %s\n", s_multi_buf.p_write, s_multi_buf.p_read, s_multi_buf.p_read==s_multi_buf.p_write?"EMPTY":"OK");
	}
}

/*
此函数目前智能调用一次。
*/
int multicast_add(const char *multi_addr)
{
	if(NULL==multi_addr || 0!=strncasecmp(multi_addr, "igmp://", strlen("igmp://"))){
		DEBUG("this multicast addr is invalid: %s\n", multi_addr);
		return -1;
	}
	
	int ret = -1;
	// 创建接收线程
	pthread_t pthread_igmp_join_id;
	
	if(0==pthread_create(&pthread_igmp_join_id, NULL, igmp_join_thread, (void *)multi_addr)){
		pthread_detach(pthread_igmp_join_id);
		DEBUG("create multicast receive thread success\n");
		ret = 0;
	}
	else{
		ERROROUT("create multicast receive thread failed\n");
		ret = -1;
	}
	
	return ret;
}



/*
int main(int argc, char **argv)
{
	DEBUG("test multicast recv...\n");
	
	// 初始化组播，参数为接收数据临时文件的绝对路径，如果不需要临时文件，则传入NULL
	multicast_init("/home/tmp/multicast_file");
	
	// 加入组播组，并将数据放入循环buffer，如果初始化时有临时文件，则同时存入临时文件。
	// 参数1为组播ip，参数2为组播端口
	multicast_add(GRUPO, PUERTO);
	
	// 在另外一个线程里读取循环buffer数据
	// buf——调用者自行准备的空间，读取的数据将放在这里
	// len——buf的空间大小
	// 返回实际读取的数据长度Bytes
	// multi_buf_read(unsigned char *buf, unsigned int len)
	
	return 0;
}
*/

int multicast_drop(int mcfd)
{
	if( mcfd < 0 )
		return -1;
	
/*
	if(yx_pppoe_status()==1)//pppoe state
	{
		TDY_DEBUG(("PPPOE IPTV EXITED %s\n", inet_ntoa((struct in_addr)(s_ipmreq.imr_interface))));
		if(osex_ipaddr_get("pppoe0", if_ip)){
			s_ipmreq.imr_interface.s_addr = inet_addr(if_ip);
			if (osex_setsockopt(mcfd, IPPROTO_IP, IP_DROP_MEMBERSHIP, (char *)&s_ipmreq, sizeof(s_ipmreq)) < 0) {
				perror("Error in setsocket(add membership)");
			}
			TDY_DEBUG(  "@@@@@@@@: sock=%d close\n", mcfd );
			osex_close(mcfd);
			return 0;
		}		
	}
*/
//	if(mcfd>=0){
//		if (setsockopt(mcfd, IPPROTO_IP, IP_DROP_MEMBERSHIP, (char *)&s_ipmreq, sizeof(s_ipmreq)) < 0) {
//			perror("Error in setsocket(add membership)");
//		}
//		
//		close(mcfd);
//	}
	return 0;
}




//#define MULTI_BUF_SIZE		(1024*1024)
#define FILTER_BUF_SIZE     (4096+4)
#define MAX_CHAN_FILTER		(10)

typedef enum {
	CHAN_STAGE_START,
	CHAN_STAGE_HEADER,
	CHAN_STAGE_PTS,
	CHAN_STAGE_PTR,
	CHAN_STAGE_DATA,
	CHAN_STAGE_END
} ChannelStage_t;

/**\brief PID channel*/
typedef struct Channel Channel_t;
struct Channel {
	unsigned short   pid;
	unsigned char    buf[FILTER_BUF_SIZE];
	unsigned char    used;
	int              bytes;
	ChannelStage_t   stage;
};

Channel_t chanFilter[MAX_CHAN_FILTER];
int tt=0;

int alloc_filter(unsigned short pid)
{
	int i;
	for(i = 0; i < MAX_CHAN_FILTER; i++)
	{
		if (chanFilter[i].used == 0)
		{
			chanFilter[i].pid = pid;
			chanFilter[i].used = 1;
			return i;
		}
	}
	return -1;	
}

void free_filter(int fid)
{
	if ((fid < MAX_CHAN_FILTER)&&(fid>=0))
	{
		chanFilter[fid].used = 0;
		chanFilter[fid].pid = -1;
		chanFilter[fid].bytes = 0;
		chanFilter[fid].stage  = CHAN_STAGE_START;
	}
}

static int get_filter(unsigned short pid)
{
	int i;
	for(i = 0; i < MAX_CHAN_FILTER; i++)
	{
		if (chanFilter[i].pid == pid)
		return i;
	}
	return -1;	
}

static int parse_payload(int fid, int p, int len, int start, unsigned char *ptr)
{
	int part;
	unsigned char *optr;
	Channel_t *chan = &chanFilter[fid];
//	static int total = 0;
	
	optr = ptr;
	if(start)
	{
		chan->bytes = 0;
		chan->stage = CHAN_STAGE_HEADER;
	}
	else if(chan->stage==CHAN_STAGE_START)
	{
		return 0;
	}
	
	if ((MULTI_BUF_SIZE - p) >= len)
	{
		memcpy(chan->buf+chan->bytes, &optr[p], len);
		chan->bytes += len;
		p += len;
	}
	else
	{
		part = 	MULTI_BUF_SIZE - p;
		memcpy(chan->buf+chan->bytes, &optr[p], part);
		chan->bytes += part;
		memcpy(chan->buf+chan->bytes, &optr[0], len - part);
		chan->bytes += len - part;
		p = len - part;
	}
	
	if(chan->bytes<3)
		return 0;
	
	if(chan->stage==CHAN_STAGE_HEADER)
	{
		/*if((chan->buf[0]==0x00) && (chan->buf[1]==0x00) && (chan->buf[2]==0x01))
		{
		chan->type  = CHAN_TYPE_PES;
		chan->stage = CHAN_STAGE_PTS;
		}
		else*/
		{
		//chan->type  = CHAN_TYPE_SEC;
			chan->stage = CHAN_STAGE_PTR;
		}
	}
	
	if(chan->stage==CHAN_STAGE_PTR)
	{
		int len = chan->buf[0]+1;
		int left = chan->bytes-len;
		
		if(chan->bytes<len)
			return 0;
		
		if(left)
			memmove(chan->buf, chan->buf+len, left);
		chan->bytes = left;
		chan->stage = CHAN_STAGE_DATA;
	}
	
	if(!chan->bytes)
		return 0;
	
	retry:
	if(chan->stage==CHAN_STAGE_DATA)
	{
		if(chan->bytes<1)
		return 0;
				
		if(chan->buf[0]==0xFF)
		{
			chan->stage = CHAN_STAGE_END;
		}
		else
		{
			int sec_len, left;
			
			if(chan->bytes<3)
				return 0;
			
			sec_len = ((chan->buf[1]<<8)|chan->buf[2])&0xFFF;
			sec_len += 3;
			
			if(chan->bytes<sec_len)
				return 0;
			
			if(chan->buf[0] == 0x3e) 
			{
				//printf("got a section len = [%d]\n",sec_len);
				send_mpe_sec_to_push_fifo(chan->buf, sec_len);
				// tt += sec_len;
				
				//printf("payload [%d]\n",total);
			}
			left = chan->bytes-sec_len;
			if(left)
			{
				memmove(chan->buf, chan->buf+sec_len, left);
			}
			chan->bytes = left;
			if(left)
				goto retry;
		}
	}
	
	if(chan->stage==CHAN_STAGE_END)
	chan->bytes = 0;
	
	return 0;
}

static int parse_ts_packet(unsigned char *ptr, int write_ptr, int *read)
{
	static int p = 0;
	int left,p1,tmp,size,chan;
	unsigned char *optr;
	unsigned short pid;
	unsigned char  tei, cc, af_avail, p_avail, ts_start, sc;
	
	optr = ptr;
	/*Scan the sync byte*/
	if (optr[p]!=0x47)
	{
		resync:
		while(optr[p]!=0x47)
		{
			//printf("eeeeeeeeeeeerror\n");
			p++;
			if( p == write_ptr)
			{
				*read = p;
				return 0;
			}
			else if(p>=MULTI_BUF_SIZE)
				p = 0;
		}
		
		if ((p+188) < MULTI_BUF_SIZE)
		{
			if (optr[p+188] != 0x47)
			{
				p++;
				goto resync;
			}
		}
		else
		{
			if (optr[p+188 - MULTI_BUF_SIZE] != 0x47)
			{
				p++;
				if (p >= MULTI_BUF_SIZE)
					p = 0;
				goto resync;
			}
		}
	}
	
	if (write_ptr > p) 
		size = write_ptr - p;
	else
		size = (MULTI_BUF_SIZE - p) + write_ptr;
	
	if (size < 188)
	{
		*read = p;
		return 0;
	}
	left = 188;
	p1 = p;
	p += 188;
	if ( p >= MULTI_BUF_SIZE)
		p = p - MULTI_BUF_SIZE;
	*read = p;
	//ptr = &optr[p];//p;
	//t
	p1++;
	if (p1 >= MULTI_BUF_SIZE)
		p1 = 0;
	tmp =  optr[p1];  
	tei  = tmp&0x80;
	//tei = ptr[1]&0x80;
	ts_start = tmp&0x40;
	//ts_start = ptr[1]&0x40;
	tmp = optr[p1];
	p1++;
	if (p1 >= MULTI_BUF_SIZE)
		p1 = 0;
	pid = ((tmp<<8)|optr[p1])&0x1FFF;
	
	p1++;
	if (p1 >= MULTI_BUF_SIZE)
	p1 = 0;
	tmp = optr[p1];
	sc = tmp&0xC0;
	//sc  = ptr[3]&0xC0;
	cc  = tmp&0x0F;
	//cc  = ptr[3]&0x0F;
	af_avail = tmp&0x20;
	//af_avail = ptr[3]&0x20;
	p_avail  = tmp&0x10;
	//p_avail  = ptr[3]&0x10;
	
	if(pid==0x1FFF)
		goto end;
	
	if(tei)
	{
		//AM_DEBUG(3, "ts error");
		goto end;
	}
	
	chan = get_filter(pid);
	if(chan == -1)
		goto end;
	/*chan = parser_get_chan(parser, pid);
	if(!chan)
	goto end;
	*/
	/*if(chan->cc!=0xFF)
	{
	if(chan->cc!=cc)
	AM_DEBUG(3, "discontinuous");
	}*/
	
	/*if(p_avail)
	cc = (cc+1)&0x0f;
	chan->cc = cc;*/
	
	p1++;
	if (p1 >= MULTI_BUF_SIZE)
		p1 = 0;
	//ptr += 4;
	left-= 4;
	
	if(af_avail)
	{
		/*left-= ptr[0]+1;
		ptr += ptr[0]+1;*/
		left -= optr[p1]+1;
		p1 += optr[p1]+1;
		if(p1 >= MULTI_BUF_SIZE)
		p1 = p1 - MULTI_BUF_SIZE;
	}
	
	if(p_avail && (left>0) && !sc)
	{
		parse_payload(chan, p1, left, ts_start, ptr);
	}
	
end:
	return 1;
}

void chanFilterInit(void)
{
	int i;
	
	for(i = 0; i < MAX_CHAN_FILTER; i++)
	{
		chanFilter[i].pid = -1;
		chanFilter[i].used = 0;
		chanFilter[i].bytes = 0;
		chanFilter[i].stage  = CHAN_STAGE_START;
	}
}

static int allpid_sqlite_callback(char **result, int row, int column, void *receiver)
{
	DEBUG("sqlite callback, row=%d, column=%d, receiver addr: %p\n", row, column, receiver);
	if(row<1){
		DEBUG("no record in table, return\n");
		return 0;
	}
	int i = 0;
	int fid = -1;
	for(i=1;i<row+1;i++)
	{
		fid = alloc_filter((unsigned short)(atoi(result[i*column])));
		DEBUG("set dvb filter, pid=%d, fid=%d\n", atoi(result[i*column]), fid);
	}
	
	return 0;
}

int softdvb_init()
{
	int ret = 0;
	
	chanFilterInit();
	
	// prog/file
	unsigned short root_pid = root_channel_get();
	int filter1 = alloc_filter(root_pid);
	DEBUG("set dvb filter1, pid=%d, fid=%d\n", root_pid, filter1);
	
	// alloc filter for the pid which already in database
	char sqlite_cmd[256+128];
	int (*sqlite_callback)(char **, int, int, void *) = allpid_sqlite_callback;

	snprintf(sqlite_cmd,sizeof(sqlite_cmd),"SELECT id FROM allpid;");
	sqlite_read(sqlite_cmd, NULL, sqlite_callback);
	
//	// prog/video
//	unsigned short video_pid = 123;	//prog_data_pid_get();
//	int filter2 = alloc_filter(123);
//	DEBUG("set dvb filter2, pid=%d, fid=%d\n", video_pid, filter2);
	
//	// prog/audio
//	unsigned short audio_pid = 8123;
//	int filter3 = alloc_filter(audio_pid);
//	DEBUG("set dvb filter3, pid=%d, fid=%d\n", audio_pid, filter3);
	
	pthread_t pthread_dvb_id;
	
	if(0==pthread_create(&pthread_dvb_id, NULL, soft_dvb_thread, NULL)){
		pthread_detach(pthread_dvb_id);
		DEBUG("create soft dvb thread success\n");
		ret = 0;
	}
	else{
		ERROROUT("create multicast receive thread failed\n");
		ret = -1;
	}
	
	
	return ret;
}

