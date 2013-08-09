#ifndef _SOFTDMX_H
#define _SOFTDMX_H

#ifdef __cplusplus
extern "C"
{
#endif

#define FILTER_BUF_SIZE (4096+184)
#define MAX_CHAN_FILTER 32
#define DMX_FILTER_SIZE 16
#define MULTI_BUF_SIZE (512*1024)

typedef struct LoaderInfo LoaderInfo_t;
struct LoaderInfo {
    unsigned int stb_id_h;  //64bit
    unsigned int stb_id_l;
    unsigned char software_version[4]; //32bit
    unsigned char hardware_version[4]; //32bit
    unsigned int img_len;          //32bit
    int fid;                       //32bit
    unsigned short oui;            //16bit
    unsigned short model_type;     //16bit
    unsigned short user_group_id;  //16bit
    unsigned char  download_type;  //8bit
    unsigned char  file_type;      //8bit
	char guodian_serialnum[24];
};

#define UPGRADE_PARA_STRUCT "/cache/recovery/last_install"
#define TC_OUI 3
#define TC_MODEL_TYPE 1
#define TC_HARDWARE_VERSION0 0
#define TC_HARDWARE_VERSION1 0
#define TC_HARDWARE_VERSION2 3
#define TC_HARDWARE_VERSION3 1
 
struct Filterp {
        unsigned char filter[DMX_FILTER_SIZE];
        unsigned char mask[DMX_FILTER_SIZE];
        unsigned char mode[DMX_FILTER_SIZE];
};
typedef struct Filterp Filter_param;
typedef void (*AM_DMX_DataCb) (int dev_no, int fhandle, const uint8_t *data, int len, void *user_data);
int TC_alloc_filter(unsigned short pid, Filter_param* param, AM_DMX_DataCb hdle, void* userdata, char pri);
int alloc_filter(unsigned short pid, char pro);
int free_filter(unsigned short pid);
int TC_free_filter(int fid);

void chanFilterInit(void);
void loader_des_section_handle(int dev_no, int fid, const unsigned char *data, int len, void *user_data);
void ca_section_handle(int dev_no, int fid, const unsigned char *data, int len, void *user_data);
void tdt_section_handle(int dev_no, int fid, const unsigned char *data, int len, void *user_data);
int upgradefile_clear();

#ifdef __cplusplus
}
#endif

#endif
