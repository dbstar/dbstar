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

typedef enum{
	POL_TYPE_V = 0,		// 垂直极化
	POL_TYPE_H			// 水平极化
}POL_TYPE_E;

typedef enum{
	MOD_TYPE_QPSK = 0,
	MOD_TYPE_8PSK,
	MOD_TYPE_16PSK,
	MOD_TYPE_32PSK
}MOD_TYPE_E;

typedef struct{
	int			frequency;				//频率，单位MHz
	int			symbolRate;				//符号率，单位k/s
	int			local_oscillator;		//本振频率，单位MHz
	POL_TYPE_E	polarization_type;		//极化方式
	MOD_TYPE_E	modulation_type;		//调试方式
}TUNER_SETTINGS;

typedef void (*AM_DMX_DataCb) (int dev_no, int fhandle, const uint8_t *data, int len, void *user_data);

int data_stream_status_str_get(char *buf, unsigned int size);
int tuner_blindscan(struct  blindscan_result *scan_result);
void tuner_search_satelite(int *snr, int *strength);
int tuner_init();
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
int tuner_get_signalinfo(char *args, char *buf, unsigned int len);
int tuner_lock(char* args, char *buf, unsigned int len);

#endif

#endif
