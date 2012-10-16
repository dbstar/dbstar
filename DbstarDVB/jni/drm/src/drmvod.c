#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <fcntl.h> 
#include <linux/unistd.h>
#include <android/log.h>

#include "player.h"

#include "prodrm20.h"

#define DRMVOD_LOG_TAG "DRMVOD"
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

#define DRM_MODULE_ENABLE 1
#define DRM_TEST_DRM_FILE "/mnt/sdb1/drm/1.drm"
#define DRM_FILENAME_LEN 256
#define DRM_TS_OFFSET 55
typedef struct {
	int inited;
	int ready;
	char filename_media[DRM_FILENAME_LEN];
	char filename_drm[DRM_FILENAME_LEN];
	FILE *fd_media;
	FILE *fd_drm;
	int64_t curpos;
	int64_t length;
} drmvod_t;
static drmvod_t s_drmvod;

static int drmvod_open(URLContext *h, const char *filename, int flags);
static int drmvod_read(URLContext *h, unsigned char *buf, int size);
static int drmvod_close(URLContext *h);
static int drmvod_write(URLContext *h, unsigned char *buf, int size);
static int64_t drmvod_seek(URLContext *h, int64_t pos, int whence);
static int drmvod_get_file_handle(URLContext *h);
int drm_init();
int drm_sc_insert();
int drm_sc_remove();
int drm_set_emmpid();
void drm_uninit();
static int drm_open(FILE *fd1, FILE *fd2);
static int drm_read(FILE *fd, unsigned char *buf, int size);
static int64_t drm_seek(FILE *fd, int pos, int whence);
static void drm_close(FILE *fd);

URLProtocol drmvod_protocol = {
	"drmvod",
	.url_open = drmvod_open,
	.url_read = drmvod_read,
	.url_write = drmvod_write,
	.url_seek = drmvod_seek,
	.url_close = drmvod_close,
	.url_get_file_handle = drmvod_get_file_handle,
};

static int drmvod_open(URLContext *h, const char *filename, int flags)
{
	int ret = 0;
	char *tmp = NULL;
	char *tmp2 = NULL;
	int len = 0;
	struct stat st;

	LOGD("########## %s(%s, %d)\n", __FUNCTION__, filename, flags);
	tmp = strstr(filename, "drmvod://");
	if (tmp == NULL) {
		LOGD("########## %s(%s, %d) failed!\n", __FUNCTION__, filename, flags);
		return -1;
	}

	s_drmvod.ready = 0;
	memset(&s_drmvod.filename_media, 0, DRM_FILENAME_LEN);
	memset(&s_drmvod.filename_drm, 0, DRM_FILENAME_LEN);
	s_drmvod.fd_media = NULL;
	s_drmvod.fd_drm = NULL;
	s_drmvod.curpos = 0;
	s_drmvod.length = 0;

	tmp += strlen("drmvod://");
	tmp2 = strstr(filename, "|");
	if (tmp2 == NULL) {
		LOGD("########## tmp11111\n");
		strcpy(s_drmvod.filename_media, tmp);
	} else {
		LOGD("########## tmp22222\n");
		len = tmp2 - tmp;
		strncpy(s_drmvod.filename_media, tmp, len);
		s_drmvod.filename_media[len] = '\0';
		tmp2 += 1;
		strcpy(s_drmvod.filename_drm, tmp2); 
	}
	s_drmvod.fd_media = fopen(s_drmvod.filename_media, "r");
	if (!s_drmvod.fd_media) {
		LOGE("########## open media(%s) ERROR!\n", s_drmvod.filename_media);
		return -1;
	}

	ret = stat(s_drmvod.filename_media, &st);
	if (ret == 0) {
		s_drmvod.length = st.st_size - DRM_TS_OFFSET;
		s_drmvod.curpos = 0;
	} else {
		LOGD("########## stat() ERROR. %s\n", strerror(errno));
	}
	LOGD("########## stat(%s) ret=%d, inited=%d, size=%lld\n", 
			s_drmvod.filename_media, ret, s_drmvod.inited, s_drmvod.length);
	h->priv_data = (void *)&s_drmvod;

	if (s_drmvod.filename_drm[0]) {
		if ((ret = drm_init()) == 0) {
			LOGD("########## drm_init() OK\n");
		} else {
			LOGD("########## drm_init() Failed.\n");
		}
	}

	if (s_drmvod.inited) {
		s_drmvod.fd_drm = fopen(s_drmvod.filename_drm, "r");
		if (!s_drmvod.fd_drm) {
			LOGE("########## open drm (%s) ERROR!\n", s_drmvod.filename_drm);
			s_drmvod.ready = 0;
		} else {
			ret = drm_open(s_drmvod.fd_media, s_drmvod.fd_drm);
			if (ret != 0) {
				LOGE("########## drm_open() ERROR!, ret=%d\n", ret);
				fclose(s_drmvod.fd_drm);
				s_drmvod.fd_drm = NULL;
				return -1;
			} else {
				s_drmvod.ready = 1;
			}
		}
	}

	LOGD("########## %s() OK\n", __FUNCTION__);
	return 0;
}

static int drmvod_read(URLContext *h, unsigned char *buf, int size)
{
	int ret = 0;
	int len = 0;
	drmvod_t *drmvod = (drmvod_t *)h->priv_data;

	len = MIN(size, (drmvod->length - drmvod->curpos));
	if (len <= 0) {
		return 0;
	}
	//LOGD("########## 1. %s(size=%d), curpos=%lld, len=%d\n", __FUNCTION__, size, drmvod->curpos, len);
	if (s_drmvod.inited && s_drmvod.ready) {
		ret = drm_read(drmvod->fd_media, buf, len);
	} else {
		ret = fread(buf, 1, len, drmvod->fd_media);
	}
	
	if (ret > 0) {
		drmvod->curpos += ret;
	}
	//LOGD("########## 2. %s(size=%d)=%d, curpos=%lld\n", __FUNCTION__, size, ret, drmvod->curpos);
	return ret;
}

static int drmvod_write(URLContext *h, unsigned char *buf, int size)
{
	int ret = 0;

	LOGD("########## %s\n", __FUNCTION__);
	return ret;
}

static int64_t drmvod_seek(URLContext *h, int64_t pos, int whence)
{
	struct stat st;
	int64_t seekpos = 0;
	drmvod_t *drmvod = (drmvod_t *)h->priv_data;

	switch (whence) {
	case AVSEEK_SIZE: //65536
		LOGD("########## %s(pos=%lld,whence=%d), ret=%lld\n", __FUNCTION__, pos, whence, drmvod->length);
		return drmvod->length;
	case SEEK_CUR:
		seekpos = drmvod->curpos + pos;
		break;
	case SEEK_END:
		seekpos = drmvod->length + pos;
		break;
	case SEEK_SET:
		seekpos = pos;
		break;
	default:
		seekpos = 0;
	}
	if ((seekpos > drmvod->length)) {
		LOGD("########## seekpos=%lld file-length=%lld, ERROR!)\n", seekpos, drmvod->length);
		return -1;
	}

	if (s_drmvod.inited && s_drmvod.ready) {
		seekpos = drm_seek(drmvod->fd_media, seekpos, SEEK_SET);
	} else {
		seekpos = fseek(drmvod->fd_media, seekpos, SEEK_SET);
	}
	if (seekpos >= 0) {
		drmvod->curpos = seekpos;
	}

	LOGD("########## %s(pos=%lld,whence=%d), ret=%lld\n", __FUNCTION__, pos, whence, seekpos);
	return seekpos;
}

static int drmvod_close(URLContext *h)
{
	drmvod_t *drmvod = (drmvod_t *)h->priv_data;

	LOGD("########## %s()\n", __FUNCTION__);

	if (s_drmvod.inited && s_drmvod.ready) {
		drm_close(s_drmvod.fd_media);
		if (drmvod->fd_drm) {
			fclose(drmvod->fd_drm);
			drmvod->fd_drm = NULL;
		}
	}

	if (drmvod->fd_media) {
		fclose(drmvod->fd_media);
		drmvod->fd_media = NULL;
	}
	memset(&s_drmvod, 0, sizeof(drmvod_t));

	return 0;
}

static int drmvod_get_file_handle(URLContext *h)
{
	LOGD("########## %s\n", __FUNCTION__);
	return 0;
}

static int drm_open(FILE *fd1, FILE *fd2)
{
	int ret = 0;

	ret = CDCASTB_DRM_OpenFile((const void*)fd1, (const void*)fd2);
	LOGD("DRM_OPEN()=%d\n", ret);

	return ret;
}

static int drm_read(FILE *fd, unsigned char *buf, int size)
{
	int ret = 0;
	int rdsize = size;
	ret = CDCASTB_DRM_ReadFile((const void*)fd, buf, &rdsize);
	//LOGD("DRM_READ(size=%d)=%d, rdsize=%d\n", size, ret, rdsize);

	return rdsize;
}

static int64_t drm_seek(FILE *fd, int pos, int whence)
{
	int success = 0;
	int64_t ret = 0;
	int posb;
	int posk;

	posk = pos >> 10;
	posb = pos % 1024;
	success = CDCASTB_DRM_SeekFilePos((const void*)fd, posk, posb);
	LOGD("DRM_SEEK(pos=%d, posk=%d, posb=%d)\n", pos, posk, posb);

	if (success) {
		ret = pos;
	} else {
		ret = -1;
	}
	return ret;
}

static void drm_close(FILE *fd)
{
	LOGD("DRM_CloseFile()\n");
	CDCASTB_DRM_CloseFile((const void*)fd);
}

int drm_init()
{
	int ret = 0;

#if (!DRM_MODULE_ENABLE)
	return -1;
#endif

	if (s_drmvod.inited == 1) {
		LOGD("DRM already inited!\n");
		return 0;
	}

	ret = CDCASTB_Init(0);
	LOGD("DRM INIT() ret=%d\n", ret);
	if (ret == 0) {
		LOGD("DRM Init() FAILED!\n");
		s_drmvod.inited = 0;
		return -1;
	} else {
		s_drmvod.inited = 1;
	}

	drm_sc_insert();
	sleep(2);

	return 0;
}

int drm_sc_insert()
{
	int ret = 0;

	LOGD("DRM SCInsert()\n");

	if (s_drmvod.inited == 0) {
		LOGD("DRM not inited!\n");
		return -1;
	}

	ret = CDCASTB_SCInsert();
	LOGD("DRM SCInsert() ret=%d\n", ret);
	if (ret == 0) {
		LOGD("DRM Init() FAILED!\n");
		return -1;
	}

	return ret;
}

int drm_sc_remove()
{
	int ret = 0;

	LOGD("DRM SCRemove()\n");

	if (s_drmvod.inited == 0) {
		LOGD("DRM not inited!\n");
		return -1;
	}

	CDCASTB_SCRemove();

	return ret;
}

int drm_set_emmpid()
{
	int ret = 0;
	unsigned char emmpid = 0x64;

	LOGD("CDCASTB_SetEmmPid()\n");

	if (s_drmvod.inited == 0) {
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

	if (s_drmvod.inited == 0) {
		LOGD("DRM not inited!\n");
		return;
	}

	drm_sc_remove();
	CDCASTB_Close();
}
