#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <fcntl.h> 
#include <linux/unistd.h>
#include <android/log.h>

#include "prodrm20.h"
#include "drmapi.h"

#define DRMVOD_LOG_TAG "DRMAPI"
#define MIN(x,y) ((x)<(y)?(x):(y))

#if 1
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, DRMVOD_LOG_TAG,__VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, DRMVOD_LOG_TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, DRMVOD_LOG_TAG,__VA_ARGS__)
#else
#define LOGI(...)
#define LOGD(...)
#define LOGE(...)
#endif

static int s_drm_inited = 0;
static int s_drm_sc_in = 0;

int drm_init()
{
	int ret = 0;

	if (s_drm_inited == 1) {
		LOGD("DRM already inited!\n");
		return 0;
	}

	ret = CDCASTB_Init(0);
	LOGD("DRM INIT() ret=%d\n", ret);
	if (ret == 0) {
		LOGD("DRM Init() FAILED!\n");
		s_drm_inited = 0;
		return -1;
	} else {
		s_drm_inited = 1;
	}

	LOGD("drm_init(),s_drm_inited=%d\n",s_drm_inited);

	return 0;
}

int drm_sc_check()
{
	int ret = 0;

	if (s_drm_sc_in == 1) {
		LOGD("drm_sc_check() OK\n");
		ret = 0;
	} else {
		LOGD("drm_sc_check() FAILED.\n");
		ret = -1;
	}

	return ret;
}

int drm_sc_insert()
{
	int ret = 0;

	LOGD("drm_sc_insert()\n");
	if (s_drm_inited == 0) {
		LOGD("DRM not inited!\n");
		return -1;
	}

	ret = CDCASTB_SCInsert();
	LOGD("DRM SCInsert() ret=%d\n", ret);
	if (ret == 0) {
		LOGD("DRM SCInsert() FAILED!\n");
		return -1;
	} else {
		s_drm_sc_in = 1;
	}

	return ret;
}

int drm_sc_remove()
{
	int ret = 0;

	LOGD("DRM SCRemove()\n");

	if (s_drm_inited == 0) {
		LOGD("DRM not inited!\n");
		return -1;
	}

	s_drm_sc_in = 0;
	CDCASTB_SCRemove();

	return ret;
}

int drm_open(int *fd1, int *fd2)
{
	int ret = 0;

	ret = CDCASTB_DRM_OpenFile((const void*)fd1, (const void*)fd2);
	LOGD("DRM_OPEN()=%d\n", ret);

	return ret;
}

int drm_read(int *fd, unsigned char *buf, int size)
{
	int ret = 0;
	unsigned int rdsize = (unsigned int)size;
	ret = CDCASTB_DRM_ReadFile((const void*)fd, buf, &rdsize);
	if (ret != 0) {
		LOGD("@@@@@@@@@@@ DRM_READ ERROR [%d](size=%d)=0x%x, rdsize=%d\n", fd,size, ret, rdsize);
		if ((ret == 0x42) || (ret == 0x1)) { // CA card plug out
			rdsize = 0;
		} else { // CA error
			LOGD("@@@@@@@@@@@ CA ERROR =0x%x\n", ret);
			return -ret;
		}
	}
	//LOGD("DRM_READ[%d](size=%d)=%d, rdsize=%d\n", fd,size, ret, rdsize);

	return (int)rdsize;
}

int64_t drm_seek(int *fd, int64_t pos, int whence)
{
	int success = 0;
	int64_t ret = 0;
	unsigned int posb;
	unsigned int posk;

	posk = (unsigned int)(pos >> 10);
	posb = (unsigned int)(pos % 1024);
	success = CDCASTB_DRM_SeekFilePos((const void*)fd, posk, posb);
	//LOGD("DRM_SEEK(pos=%lld, posk=%d, posb=%d)\n", pos, posk, posb);

	if (success) {
		ret = pos;
	} else {
		ret = -1;
	}
	return ret;
}

void drm_close(int *fd)
{
	LOGD("DRM_CloseFile()\n");
	CDCASTB_DRM_CloseFile((const void*)fd);
}

int drm_set_emmpid()
{
	int ret = 0;
	unsigned char emmpid = 0x64;

	LOGD("CDCASTB_SetEmmPid()\n");

	if (s_drm_inited == 0) {
		LOGD("DRM not inited!\n");
		return -1;
	}

	//setup EMM filter
	CDCASTB_SetEmmPid(emmpid); 

	return 0;
}

void drm_uninit()
{
	LOGD("DRM_UNINIT()\n");

	if (s_drm_inited == 0) {
		LOGD("DRM not inited!\n");
		return;
	}

	drm_sc_remove();
	CDCASTB_Close();
}

