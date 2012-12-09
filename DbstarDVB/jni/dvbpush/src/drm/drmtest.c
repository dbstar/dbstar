#include <stdio.h>
#include <stdlib.h>
#include <linux/unistd.h>
#include <linux/delay.h>
#include <fcntl.h> 

#include "prodrm20.h"

#define DRM_TEST_TS_FILE "/mnt/sdb1/drm/test.ts"
#define DRM_TEST_TS_OK_FILE "/mnt/sdb1/drm/test_ok.ts"
#define DRM_TEST_DRM_FILE "/mnt/sdb1/drm/1.drm"
//#define DRM_BUFF_LEN 128*1024
#define DRM_BUFF_LEN 150000
static char pbyBuffer[DRM_BUFF_LEN];

int main(int argc, char **argv)
{
	int fp1, fp2, fp3;
	int ret= 0, total_len = 0;
	int i = 0;
	int posk = 0;
	int pos = 0;
	int read_cnt = 10;
	int pdwBufferLen = DRM_BUFF_LEN;


	if (CDCASTB_Init(0)) { //初始化drm库
		printf("DRM Init successful!!!!!!\n");
	} else {
		printf("DRM Init failure!!!!!!!!!!\n");
	}

	if (CDCASTB_SCInsert()) { //初始化smart card
		printf("CARD inserted!!!!!!!!!\n");
	} else {
		printf("CARD out!!!!!!!!!!!!\n");
	}

	//CDCASTB_SetEmmPid(0x64);  //设置EMM滤波器

	sleep(2);

	if ((fp1 = open(DRM_TEST_TS_FILE, O_RDONLY)) == -1) { //打开加密的视频文件
		printf("open content1.txt error\n");
	}

	if ((fp2 = open(DRM_TEST_DRM_FILE, O_RDONLY)) == -1) { //打开授权文件
		printf("open product1.drm error\n");
	}

	if ((fp3 = open(DRM_TEST_TS_OK_FILE, O_WRONLY)) == NULL) {
		printf("open ts error\n");
	}

	ret = CDCASTB_DRM_OpenFile((const void*)fp1, (const void*)fp2); //DRM库打开文件，同时验证授权，如果返回0，表示有授权，否则，会有不同提示
	printf("@@@@@@@@@@@@@ CDCASTB_DRM_OpenFile() [%d]\n", ret);

#if 1
	while (pdwBufferLen > 0) {
		ret = CDCASTB_DRM_ReadFile((const void*)fp1, pbyBuffer, &pdwBufferLen); //读解密后的数据
		total_len += pdwBufferLen;
		printf("read file [%d][%d], total_len=[%d]\n", ret, pdwBufferLen, total_len);
		ret = write(fp3, pbyBuffer, pdwBufferLen);
		printf("write file [%d][%d], total_len=[%d]\n", ret, pdwBufferLen, total_len);
	}
#endif

#if 0
	posk = 0;
	pos = 500;
	printf("@@@@@@@@@@@@@ CDCASTB_DRM_SeekFilePos(%d, %d)=%d\n", posk, pos, ret);
	ret = CDCASTB_DRM_SeekFilePos((const void*)fp1, posk, pos);
	ret = CDCASTB_DRM_ReadFile((const void*)fp1, pbyBuffer, &pdwBufferLen);
	printf("CDCASTB_DRM_ReadFile()=%d, len=[%d]\n", ret, pdwBufferLen);

	//436547547
	posk = 426169;
	pos = 546;
	printf("@@@@@@@@@@@@@ CDCASTB_DRM_SeekFilePos(%d, %d)=%d\n", posk, pos, ret);
	ret = CDCASTB_DRM_SeekFilePos((const void*)fp1, posk, pos);
	ret = CDCASTB_DRM_ReadFile((const void*)fp1, pbyBuffer, &pdwBufferLen);
	printf("CDCASTB_DRM_ReadFile()=%d, len=[%d]\n", ret, pdwBufferLen);
#endif

	close(fp3);
	close(fp2);
	close(fp1);

	return ret;
}
