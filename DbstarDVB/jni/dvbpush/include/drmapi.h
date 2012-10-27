#ifndef _DRMAPI_H_
#define _DRMAPI_H_

int drm_init();
int drm_sc_insert();
int drm_sc_remove();
int drm_set_emmpid();
void drm_uninit();
int drm_open(FILE *fd1, FILE *fd2);
int drm_read(FILE *fd, unsigned char *buf, int size);
int64_t drm_seek(FILE *fd, int pos, int whence);
void drm_close(FILE *fd);

#endif
