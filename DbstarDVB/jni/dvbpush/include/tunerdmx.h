#ifndef __TUNERDMX_H__
#define __TUNERDMX_H__

#ifdef TUNER_INPUT

#include "common.h"

#include "libinclude/am_dmx.h"
#include "libinclude/am_fend.h"
#include "libinclude/am_util.h"
#include "am_dvr.h"

struct blindscan_result{
	int count;
	unsigned int freq[128];
	unsigned int sr[128];
};

typedef void (*AM_DMX_DataCb) (int dev_no, int fhandle, const uint8_t *data, int len, void *user_data);

int data_stream_status_str_get(char *buf, unsigned int size);
int tuner_blindscan(struct  blindscan_result *scan_result);
void tuner_search_satelite(int *snr, int *strength);
int tuner_init(int freq, int symbolrate, int voltage);
int tuner_uninit();
int TC_free_filter(int fid);
int TC_alloc_filter(unsigned short pid, Filter_param* sparam, AM_DMX_DataCb hdle, void* userdata, char priority);
int start_feedpush(AM_DVR_StartRecPara_t *spara);
int stop_feedpush();
int upgradefile_clear();
void root_section_handle(int dev_no, int fid, const unsigned char *data, int len, void *user_data);
void tdt_section_handle(int dev_no, int fid, const unsigned char *data, int len, void *user_data);
void ca_section_handle(int dev_no, int fid, const unsigned char *data, int len, void *user_data);
void loader_des_section_handle(int dev_no, int fid, const unsigned char *data, int len, void *user_data);
int tuner_get_signalinfo(char *freq, char *buf, unsigned int len);
int tuner_scan(char *buf, unsigned int len);

#endif

#endif
