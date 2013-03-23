#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <fcntl.h>
#include <android/log.h>
//#include <jni.h>
//#include <pthread.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/ioctl.h>
#include <netinet/in.h>
#include <net/if.h>

#include "dvbpush_api.h"

#define JNI_LOG_ENABLE 1
#if JNI_LOG_ENABLE
#define LOG_TAG "DbstarNetwork"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#else
#define  LOGI(...)
#define  LOGD(...)
#define  LOGE(...)
#endif

int network_getinfo(char *buf, unsigned int len)
{
	int ret = 0;
	int fd = -1;
	char ip[32] = {};
	char mask[32] = {};
	char gw[32] = {};
	char mac[32] = {};
	FILE *fp = NULL;
	char tmpbuf[128];
	char iface[16];
	unsigned long dest_addr = INADDR_NONE;
	unsigned long gate_addr = INADDR_NONE;
	struct ifreq ifr;

	if (buf == NULL || len <= 0) {
		LOGE("buf null!\n");
		return -1;
	}

	fd = socket(AF_INET, SOCK_DGRAM, 0);
	if (fd < 0) {
		LOGE("socket() failed!\n");
		return -1;
	}
	fp = fopen("/proc/net/route", "r");
	if (fp == NULL) {
		LOGE("open(route) failed!\n");
		return -1;
	}

	/* get ip address */
	memset(&ifr, 0, sizeof(ifr));
	ifr.ifr_addr.sa_family = AF_INET;
	strncpy(ifr.ifr_name, "eth0", IFNAMSIZ - 1);
	ret = ioctl(fd, SIOCGIFADDR, &ifr);
	if (ret != 0) {
		LOGE("ioctl() failed!\n");
	} else {
		sprintf(ip, "%s", inet_ntoa(((struct sockaddr_in *)&ifr.ifr_addr)->sin_addr));
	}

	/* get sub mask */
	memset(&ifr, 0, sizeof(ifr));
	ifr.ifr_addr.sa_family = AF_INET;
	strncpy(ifr.ifr_name, "eth0", IFNAMSIZ - 1);
	ret = ioctl(fd, SIOCGIFNETMASK, &ifr);
	if (ret != 0) {
		LOGE("ioctl() failed!\n");
	} else {
		sprintf(mask, "%s", inet_ntoa(((struct sockaddr_in *)&ifr.ifr_addr)->sin_addr));
	}

	/* get gate way */
	fgets(tmpbuf, sizeof(buf), fp);
	while (fgets(tmpbuf, sizeof(tmpbuf), fp)) {
		if (sscanf(tmpbuf, "%s\t%lX\t%lX", iface, &dest_addr, &gate_addr) != 3 || 
				dest_addr != 0) {
			continue;
		}
		if (strcmp("eth0", iface) == 0) {
			sprintf(gw, "%s", inet_ntoa(gate_addr));
			break;
		}
	}

	/* get mac address */
	memset(&ifr, 0, sizeof(ifr));
	ifr.ifr_addr.sa_family = AF_INET;
	strncpy(ifr.ifr_name, "eth0", IFNAMSIZ - 1);
	ret = ioctl(fd, SIOCGIFHWADDR, &ifr);
	if (ret != 0) {
		LOGE("ioctl() failed!\n");
	} else {
		sprintf(mac, "%02x:%02x:%02x:%02x:%02x:%02x",
		        (unsigned char)ifr.ifr_hwaddr.sa_data[0],
		        (unsigned char)ifr.ifr_hwaddr.sa_data[1],
		        (unsigned char)ifr.ifr_hwaddr.sa_data[2],
		        (unsigned char)ifr.ifr_hwaddr.sa_data[3],
		        (unsigned char)ifr.ifr_hwaddr.sa_data[4],
		        (unsigned char)ifr.ifr_hwaddr.sa_data[5]);
	}

	sprintf(buf, "ip=%s, mask=%s, gw=%s, mac=%s", ip, mask, gw, mac);
	LOGD("networkinfo[%s], len=%d\n", buf, strlen(buf));

	close(fd);
	fclose(fp);

	return ret;
}
