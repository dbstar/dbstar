#include <stdio.h>
#include <stdlib.h>
#include <linux/unistd.h>
#include <linux/delay.h>
#include <fcntl.h> 

#include "prodrm20.h"
#include "common.h"

#define debug printf
//#define debug PRINTF

#define BASE_PATH "/mnt/sdb1/"
#define TS_FILE "/mnt/sda1/dbstar/pushroot_bk/pushfile/105/content/D0018-0825-E1.ts"
#define DRMTS_FILE "/mnt/sda1/dbstar/pushroot_bk/pushfile/105/content/D0018-0825-E1.ts"
#define DRM_FILE "/mnt/sda1/dbstar/pushroot_bk/drm_10G_test/5.drm"


#define DRM_BUFF_LEN 150000
static char buff[DRM_BUFF_LEN];
static char filename[256];

int main(int argc, char **argv)
{
	int fd1, fd2, fd3, fdw;
	int ret= 0, total_len = 0;
	int i = 0;
	int posk = 0;
	int pos = 0;
	int read_cnt = 10;
	int len = 0;
	long long seek = 0;
	long long offset = 0;

	if (argc != 3) {
		debug("drmtest [pos] [len] \n");
		return -1;
	}
	seek = atoll(argv[1]);
	len = atoi(argv[2]);
	posk = seek/1024;
	pos = seek%1024;
	debug("** posk=%d, pos=%d, seek=%lld, len=%d\n", posk, pos, seek, len);

	if (CDCASTB_Init(0)) { //初始化drm库
		debug("DRM Init successful!!!!!!\n");
	} else {
		debug("DRM Init failure!!!!!!!!!!\n");
	}

	if (CDCASTB_SCInsert()) { //初始化smart card
		debug("CARD inserted!!!!!!!!!\n");
	} else {
		debug("CARD out!!!!!!!!!!!!\n");
	}

	sleep(6);
	debug(">>>>>>>>>>>>>>>>>\n");

	if ((fd1 = open(DRMTS_FILE, O_RDONLY)) == -1) { //打开加密的视频文件
		debug("open encrypted ts file(%s) failed\n", DRMTS_FILE);
		return -1;
	}
	else
		debug("open encrypted ts file: %s\n", DRMTS_FILE);

	if ((fd2 = open(DRM_FILE, O_RDONLY)) == -1) { //打开授权文件
		debug("open drm file(%s) failed\n", DRM_FILE);
		return -1;
	}
	else
		debug("open drm file: %s\n", DRM_FILE);

	if ((fd3 = open(TS_FILE, O_RDONLY)) == -1) {
		debug("open output file(%s) failed\n", TS_FILE);
		return -1;
	}
	else
		debug("open output file: %s\n", TS_FILE);

	ret = CDCASTB_DRM_OpenFile((const void*)&fd1, (const void*)&fd2); //DRM库打开文件，同时验证授权，如果返回0，表示有授权，否则，会有不同提示
	debug("@@@@@@@@@@@@@ CDCASTB_DRM_OpenFile() [%d]\n", ret);

#if 0
	while (len > 0) {
		ret = CDCASTB_DRM_ReadFile((const void*)&fd1, buff, &len); //读解密后的数据
		total_len += len;
		debug("read file [%d][%d], total_len=[%d]\n", ret, len, total_len);
		ret = write(fd3, buff, len);
		debug("write file [%d][%d], total_len=[%d]\n", ret, len, total_len);
	}
#endif

#if 1
	debug("@@@@@@@@@@@@@ CDCASTB_DRM_SeekFilePos(%d, %d)=%d\n", posk, pos, ret);
	ret = CDCASTB_DRM_SeekFilePos((const void*)&fd1, posk, pos);
	ret = CDCASTB_DRM_ReadFile((const void*)&fd1, buff, &len);
	sprintf(filename, "%s/pos-%lld_len%d_drmts.ts", BASE_PATH, seek, len);
	debug("CDCASTB_DRM_ReadFile()=%d, len=[%d]\n", ret, len);
	if ((fdw = open(filename, O_WRONLY | O_CREAT)) > 0) {
		ret = write(fdw, buff, len);
		debug("write(%d) to %s OK\n", ret, filename);
		close(fdw);
	}

	offset = lseek64(fd3, seek,  SEEK_SET);
	debug("@@@@@@@@@@@@@ lseek64(%lld)=%lld\n", seek, offset);
	len = read(fd3, buff, len);
	debug("read(%d)=%d\n", len, ret);
	sprintf(filename, "%s/pos-%lld_len%d_ts.ts", BASE_PATH, seek, len);
	if ((fdw = open(filename, O_WRONLY | O_CREAT)) > 0) {
		ret = write(fdw, buff, len);
		debug("write(%d) to %s OK\n", ret, filename);
		close(fdw);
	}
#endif

	close(fd3);
	close(fd2);
	close(fd1);
	
	debug("drm test finish\n");

	return ret;
}
