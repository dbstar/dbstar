#ifndef _DRMAPI_H_
#define _DRMAPI_H_

int drm_init();
int drm_sc_insert();
int drm_sc_remove();
int drm_set_emmpid();
void drm_uninit();
int drm_open(int fd1, int fd2);
int drm_read(int fd, unsigned char *buf, int size);
int64_t drm_seek(int fd, int64_t pos, int whence);
void drm_close(int fd);

#endif
