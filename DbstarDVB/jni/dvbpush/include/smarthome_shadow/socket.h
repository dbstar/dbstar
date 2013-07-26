#ifndef __SOCKET_H__
#define __SOCKET_H__

int smartlife_send(char *buf, int buf_len);
int smartlife_connect(char *buf, int buf_len);
int smartlife_connect_init();
int smartlife_connect_status_get(char *buf, int buf_size);

#endif
