#ifndef __SERIAL_H__
#define __SERIAL_H__

int serial_int(void);
//BOOL_E sendSerial(int);
//int sendto_serial(unsigned char *buf, unsigned int len);
//int recvfrom_serial(unsigned char *buf, unsigned int len);
int serial_access(unsigned char *buf, unsigned int buf_len, unsigned int buf_size);

void serial_fd_close(void);

#endif
