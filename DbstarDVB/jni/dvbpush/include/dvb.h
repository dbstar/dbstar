#ifndef __DVB_H__
#define __DVB_H__

int dvb_init(void);
int TC_SetFilter(int pid);
void TC_ReleaseFilter( int fid );

#endif
