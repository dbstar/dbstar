#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <time.h>
#include "common.h"
#include "global.h"
#include "socket.h"

int main(int argc, char *argv[])
{
	DEBUG("smart power gateway start...\n");
	
#if 1
	time_t t;
	struct tm gmt, area;
	tzset(); /* tzset()*/
	
	int tz_repair = timezone_repair();
	DEBUG("timezone force repair %d\n", tz_repair);
	
	t = time(NULL);
	localtime_r(&t, &area);
	DEBUG("Local time is: %s", asctime(&area));
	gmtime_r(&t, &gmt);
	DEBUG("GMT time   is: %s", asctime(&gmt));
	
	t += 60*60*tz_repair;
	localtime_r(&t, &area);
	DEBUG("forcible repair Local time is: %s", asctime(&area));
#endif
	
#ifdef TEST_SERIAL_CMD_ONLY_ONCE
	
	if(-1==serial_int()){
		DEBUG("serial module init failed\n");
		return -1;
	}
	
	if(argc<2 || atoi(argv[1])<=SMART_SOCKET_ACTION_UNDEFINED || atoi(argv[1])>SMART_SOCKET_COMMUNICATION_FAILD){
		DEBUG("arguments need 1 at least, and value with [%d, %d)\n", SMART_SOCKET_ACTION_UNDEFINED, SMART_SOCKET_COMMUNICATION_FAILD);
		serial_fd_close( );
		return -1;
	}
	
	char socket_id[32];
	unsigned char serial_cmd[128];
	SMART_SOCKET_ACTION_E smart_socket_action = SMART_SOCKET_ACTION_UNDEFINED;
	
	memset(socket_id, 0, sizeof(socket_id));
	memset(serial_cmd, 0, sizeof(serial_cmd));
	smart_socket_action = atoi(argv[1]);
	if(argc>=3 && strlen(argv[2])==strlen("201112210636"))
		strcpy(socket_id, argv[2]);
	else{
	//	strcpy(socket_id, "201112210636");
		strcpy(socket_id, "201112210951");
	//	strcpy(socket_id, "201109206603");
	}
	
	int serial_cmd_len = smart_socket_serial_cmd_splice(serial_cmd,sizeof(serial_cmd), smart_socket_action,socket_id);
	if(serial_cmd_len>0)
	{
		int recv_serial_len = serial_access(serial_cmd, serial_cmd_len, sizeof(serial_cmd));
		if(recv_serial_len>0)
		{
			double result = 0.0;
			smart_socket_serial_cmd_parse(serial_cmd, recv_serial_len, smart_socket_action, socket_id, &result);
			DEBUG("result=%lf\n", result);
		}
	}
	
	/* 测试代码，应用启动一次只执行一组串口write-read操作，到此完毕；
	为了及时释放串口，便于再次执行，此处应关闭。 */
	serial_fd_close( );
	
	return 0;
#endif

	//start init
	if (-1 == global_init())
	{
		DEBUG("init error!!!\n");
		return -1;
	}

	socket_mainloop();
	
	return 0;
}

