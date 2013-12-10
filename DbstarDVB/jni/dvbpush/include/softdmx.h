#ifndef _SOFTDMX_H
#define _SOFTDMX_H

#ifdef __cplusplus
extern "C"
{
#endif

#include "common.h"
#include "tunerdmx.h"

#ifdef TUNER_INPUT
	int TC_alloc_filter(unsigned short pid, Filter_param* param, AM_DMX_DataCb hdle, void* userdata, char pri);
	int TC_free_filter(int fid);
	
	void loader_des_section_handle(int dev_no, int fid, const unsigned char *data, int len, void *user_data);
	void ca_section_handle(int dev_no, int fid, const unsigned char *data, int len, void *user_data);
	void tdt_section_handle(int dev_no, int fid, const unsigned char *data, int len, void *user_data);
#else
	int TC_alloc_filter(unsigned short pid, Filter_param* param, dataCb hdle, void* userdata, char pri);
	void TC_free_filter(int fid);
	int parse_ts_packet(unsigned char *ptr, int write_ptr, int *read);
	void loader_des_section_handle(int fid, const unsigned char *data, int len, void *user_data);
	void ca_section_handle(int fid, const unsigned char *data, int len, void *user_data);
	void tdt_section_handle(int fid, const unsigned char *data, int len, void *user_data);
#endif

int alloc_filter(unsigned short pid, char pro);
int free_filter(unsigned short pid);
void chanFilterInit(void);
int upgradefile_clear();
unsigned int tc_crc32(const unsigned char *buf, int len);

#ifdef __cplusplus
}
#endif

#endif
