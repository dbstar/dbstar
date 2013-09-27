#include <pthread.h>
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <fcntl.h>
#include <sys/ioctl.h>
#include <android/log.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>

#include "am/am_smc.h"
#include "linux/amsmc.h"
#include "prodrm20.h"
#include "softdmx.h"
#include "common.h"
#include "prodrm20.h"
#include "dvbpush_api.h"
#include "porting.h"

#define DRMVOD_LOG_TAG "DRMLIB"
#if 1
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, DRMVOD_LOG_TAG,__VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, DRMVOD_LOG_TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, DRMVOD_LOG_TAG,__VA_ARGS__)
#else
#define LOGI(...)
#define LOGD(...)
#define LOGE(...)
#endif

#define CDCA_MAXLEN_SN_PATH	  (CDCA_MAXLEN_SN + 128)

typedef struct {
    char sn[CDCA_MAXLEN_SN_PATH+1];
    int  fd;
}SCDCACardEntitleInfo;

typedef struct {
        CDCA_U8       byReqID;
        CDCA_U8       fid;
        CDCA_U16      wPID;
        CDCA_U32      timeouttime;
}SCDCAFilterInfo;


//#define CDCA_MAX_CARD_NUM 2
#define SMC_DEVICE  "/dev/smc0"
#define BLOCK01_FILE "/data/dbstar/drm/entitle/block01"
#define ENTITLE_FILE_PATH "/data/dbstar/drm/entitle"
//#define DMX_DEV_NO 0

CDCA_U8 checkTimeoutMark = 0;
int smc_fd = -1;
FILE *block01_fd = NULL;
extern int smc_set(struct am_smc_atr *abuf);
SCDCACardEntitleInfo card_sn = {"", -1}; //[CDCA_MAX_CARD_NUM];
SCDCAFilterInfo dmx_filter[MAX_CHAN_FILTER];
extern Channel_t chanFilter[];
extern int max_filter_num;


static int mkdirp(char *path)
{
	int ret = 0;
	char cmd[128] = {};

	if (path == NULL) {
		LOGD("--- path NULL\n");
		return -1;
	}

	if (access(path, 0) == 0) {
		LOGD("--- path[%s] already exist.\n", path);
		ret = 0;
	} else {
		sprintf(cmd, "mkdir -p %s", path);
		ret = system(cmd);
	}

	return ret;
}

/*-------- 线程管理 --------*/

void CDSTBCA_HDDec_CloseDecrypter(const void *pCtx)
{
	LOGD("CDSTBCA_HDDec_CloseDecrypter, pCtx = %p\n", pCtx);
}

/* 注册任务 */
#define MAX_DRM_THREADS 10

pthread_t tmpthread[MAX_DRM_THREADS];  //for max thread for drm module
CDCA_BOOL CDSTBCA_RegisterTask(const char* szName,
                               CDCA_U8     byPriority,
                               void*       pTaskFun,
                               void*       pParam,
                               CDCA_U16    wStackSize)
{
	static int i = 0;
//	int ret = 0;
	LOGD("DRM create thread[%d], thread name [%s]\n", i, szName);
	if (pthread_create(&tmpthread[i], NULL, pTaskFun, pParam) != 0) {
		LOGD("DRM Create task [%s] ERROR\n", szName);
		return CDCA_FALSE;
	}
	i++;
	if (i >= MAX_DRM_THREADS) {
		i = MAX_DRM_THREADS - 1;
	}
	return CDCA_TRUE;

}

/* 线程挂起 */
void CDSTBCA_Sleep(CDCA_U16 wMilliSeconds)
{
	usleep(1000 * wMilliSeconds);
}


/*-------- 信号量管理 --------*/

/* 初始化信号量 */
void CDSTBCA_SemaphoreInit(CDCA_Semaphore* pSemaphore, CDCA_BOOL bInitVal)
{
	if (bInitVal == CDCA_TRUE) {
		*pSemaphore = 1;
	} else {
		*pSemaphore = 0;
	}
	//LOGD("init a semaphore [%d]\n",*pSemaphore);
}

/* 信号量给予信号 */
void CDSTBCA_SemaphoreSignal(CDCA_Semaphore* pSemaphore)
{
	//LOGD("release semaphore...\n");
	*pSemaphore = 1;
}

/* 信号量获取信号 */
void CDSTBCA_SemaphoreWait(CDCA_Semaphore* pSemaphore)
{
	//LOGD("wait semaphore 1\n");
	while (*pSemaphore == 0) {
		usleep(5000);
	}
	*pSemaphore = 0;
	//LOGD("got a semaphore 2\n");
}


/*-------- 内存管理 --------*/

/* 分配内存 */
void* CDSTBCA_Malloc(CDCA_U32 byBufSize)
{
	void *ptr = NULL;
	ptr = malloc(byBufSize);
	//LOGD("&&& malloc(%lu), ptr=0x%p\n", byBufSize, ptr);
	return ptr;
}

/* 释放内存 */
void  CDSTBCA_Free(void* pBuf)
{
	//LOGD("&&& free(0x%p)\n", pBuf);
	free(pBuf);
}

/* 内存赋值 */
void  CDSTBCA_Memset(void*    pDestBuf,
                     CDCA_U8  c,
                     CDCA_U32 wSize)
{
	memset(pDestBuf, c, wSize);
}

/* 内存复制 */
void  CDSTBCA_Memcpy(void*       pDestBuf,
                     const void* pSrcBuf,
                     CDCA_U32    wSize)
{
	memcpy(pDestBuf, pSrcBuf, wSize);
}


/*--------- 存储空间（Flash）管理 ---------*/

/* 读取存储空间 */
#if 1
void CDSTBCA_ReadBuffer(CDCA_U8 byBlockID, CDCA_U8*  pbyData, CDCA_U32* pdwLen)
{
	int len;
	int ret = 0;

	//LOGD("###############Read the flash 64k buffer [%d]\n", byBlockID);
	if (block01_fd == NULL) {
		//	CDCA_U8 tmp[128*1024];

		//	memset(tmp,0,128*1024);
		ret = mkdirp(ENTITLE_FILE_PATH);
		if (ret != 0) {
			LOGE("--- create the entitle path error. [%s]\n", strerror(errno));
			return;
		}
		block01_fd = fopen(BLOCK01_FILE, "r+");

		if (block01_fd == NULL) {
			LOGE("open the flash read file error!!!!\n");
			return;
		}
		//  fwrite(tmp,1,128*1024,block01_fd);
	}
	if (byBlockID == CDCA_FLASH_BLOCK_B) {
		fseek(block01_fd, 64 * 1024, SEEK_SET);
		len = *pdwLen;
		*pdwLen = fread(pbyData, 1, len, block01_fd);
		LOGD("read flash block 2[%lu]\n", *pdwLen);
	} else if (byBlockID == CDCA_FLASH_BLOCK_A) {
		fseek(block01_fd, 0, SEEK_SET);
		len = *pdwLen;
		*pdwLen = fread(pbyData, 1, len, block01_fd);
		LOGD("read flash block 1[%lu]\n", *pdwLen);
	}
}

/* 写入存储空间 */
void CDSTBCA_WriteBuffer(CDCA_U8 byBlockID, const CDCA_U8* pbyData, CDCA_U32 dwLen)
{
	int ret = 0;

	LOGD("############### Write the flash 64k buffer [%d]\n", byBlockID);
	if (block01_fd == NULL) {
		//	CDCA_U8 tmp[128*1024];

		//	memset(tmp,0,128*1024);
		ret = mkdirp(ENTITLE_FILE_PATH);
		if (ret != 0) {
			LOGE("--- create the entitle path error. [%s]\n", strerror(errno));
			return;
		}
		block01_fd = fopen(BLOCK01_FILE, "r+");
		if (block01_fd == NULL) {
			LOGE("open the flash file error!!!!!\n");
			return;
		}
		//  fwrite(tmp,1,128*1024,block01_fd);
	}
	if (byBlockID == CDCA_FLASH_BLOCK_B) {
		fseek(block01_fd, 64 * 1024, SEEK_SET);
		fwrite(pbyData, 1, dwLen, block01_fd);
		LOGD("write flash block 2 [%lu]\n", dwLen);
	} else if (byBlockID == CDCA_FLASH_BLOCK_A) {
		fseek(block01_fd, 0, SEEK_SET);
		fwrite(pbyData, 1, dwLen, block01_fd);
		LOGD("write flash block 1 [%lu]\n", dwLen);
	}
	fflush(block01_fd);
}
#endif

/*-------- TS流管理 --------*/

#if 1

void dmx_filter_init(void)
{
    int i;
    
    for (i=0; i< MAX_CHAN_FILTER; i++)
    {
    	dmx_filter[i].byReqID = 0xff;
	    dmx_filter[i].wPID = 0xffff;
	    dmx_filter[i].timeouttime = 0;
    }	
}


void filter_timeout_process()
{
	int i;
	time_t now_sec,theni;
	
	if (checkTimeoutMark>0)
	{
		now_sec = time(NULL);
//		LOGD("checkTimeoutMark: %d, now_sec: %lu\n",checkTimeoutMark,now_sec);
		
		for(i=0; i<max_filter_num; i++)
		{
			theni = dmx_filter[i].timeouttime;
//			LOGD("dmx_filter[%d].timeouttime=%lu\n",i,dmx_filter[i].timeouttime);
			if (theni > 0)
			{
				if (now_sec >= theni)
				{
					LOGD("[%d]theni: %lu (%lu)\n", i,theni,now_sec);
					if (checkTimeoutMark>0)
						checkTimeoutMark --;
					
					dmx_filter[i].timeouttime = 0;
					CDSTBCA_ReleasePrivateDataFilter(dmx_filter[i].byReqID, dmx_filter[i].wPID);
				}
			}
		}
	}
}


static void filter_dump_bytes(int fid, const uint8_t *data, int len, void *user_data)
{
	CDCA_U8        byReqID;
	CDCA_U16       wPid;
	SCDCAFilterInfo *filterinfo;

	LOGD("Got EMM data len [%d]\n", len);
	/*{
	 int i;

	                for(i=0;i<len;i++)
	                {
	                        LOGD("%02x ", data[i]);
	                        if(((i+1)%32)==0) LOGD("\n");
	                }

	                if((i%32)!=0) LOGD("\n");

	}*/
	if (!user_data) {
		return;
	}
	filterinfo = (SCDCAFilterInfo *)user_data;
	filterinfo += fid;
	byReqID = filterinfo->byReqID;
	wPid = filterinfo->wPID;
	CDCASTB_PrivateDataGot(byReqID, CDCA_FALSE, wPid, data, len);
	if ((byReqID & 0x80) == 0x80) {
		if (checkTimeoutMark>0) {
			checkTimeoutMark --;
		}
		dmx_filter[fid].timeouttime = 0;
		LOGD("(byReqID & 0x80) == 0x80\n");
		CDSTBCA_ReleasePrivateDataFilter(byReqID, wPid);
	}
	else{
		filter_timeout_process();
	}
}
#endif

/* 设置私有数据过滤器 */
CDCA_BOOL CDSTBCA_SetPrivateDataFilter(CDCA_U8  byReqID,
                                       const CDCA_U8* pbyFilter,
                                       const CDCA_U8* pbyMask,
                                       CDCA_U8        byLen,
                                       CDCA_U16       wPid,
                                       CDCA_U8        byWaitSeconds)
{
	LOGD("CDSTBCA_SetPrivateDataFilter() called\n");
#if 1
	Filter_param param;
	//Channel_t *filter;
	int fid, i;
	time_t now_sec = 0;

	for (fid = 0; fid < MAX_CHAN_FILTER; fid++) {
		if ((dmx_filter[fid].byReqID == byReqID) && (chanFilter[fid].used)) {
			CDSTBCA_ReleasePrivateDataFilter(byReqID, dmx_filter[fid].wPID);
			break;
		}
	}

	//waitcplete
	memset(&param, 0, sizeof(param));
	for (i = 0; i < byLen; i++) {
		param.filter[i] = pbyFilter[i];
		param.mask[i] = pbyMask[i];
	}
	PRINTF("filter[8]: %x,%x,%x,%x,%x,%x,%x,%x\n",param.filter[0],param.filter[1],param.filter[2],param.filter[3],param.filter[4],param.filter[5],param.filter[6],param.filter[7]);
	PRINTF("mask  [8]: %x,%x,%x,%x,%x,%x,%x,%x\n\n",param.mask[0],param.mask[1],param.mask[2],param.mask[3],param.mask[4],param.mask[5],param.mask[6],param.mask[7]);

	fid = TC_alloc_filter(wPid, &param, (dataCb)filter_dump_bytes, (void *)&dmx_filter[0], 0);
	if ((fid >= MAX_CHAN_FILTER)||(fid < 0)) {
		return  CDCA_FALSE;
	}
	dmx_filter[fid].wPID = wPid;
	dmx_filter[fid].byReqID = byReqID;
	dmx_filter[fid].fid = fid;
	if (byWaitSeconds) {
		checkTimeoutMark ++;
		now_sec = time(NULL);
		dmx_filter[fid].timeouttime = now_sec + byWaitSeconds;
		LOGD("now_sec: %lu, byWaitSeconds: %u, dmx_filter[%d].timeouttime: %lu\n", now_sec,byWaitSeconds,fid,dmx_filter[fid].timeouttime);
	} else {
		dmx_filter[fid].timeouttime = 0;
	}

#endif
	return CDCA_TRUE;
}

/* 释放私有数据过滤器 */
void CDSTBCA_ReleasePrivateDataFilter(CDCA_U8  byReqID, CDCA_U16 wPid)
{
#if 1
	int fid;

	for (fid = 0; fid < max_filter_num; fid++) {
		if ((dmx_filter[fid].byReqID == byReqID) && (dmx_filter[fid].wPID == wPid) && (chanFilter[fid].used)) {
			break;
		}
	}
	LOGD("release fid[%d] filter\n", fid);
	if (fid >= max_filter_num) {
		return;
	}
	TC_free_filter(fid);
	dmx_filter[fid].byReqID = 0xff;
	dmx_filter[fid].wPID = 0xffff;
	dmx_filter[fid].timeouttime = 0;
#endif
}

/* 设置CW给解扰器 */
void CDSTBCA_ScrSetCW(CDCA_U16       wEcmPID,
                      const CDCA_U8* pbyOddKey,
                      const CDCA_U8* pbyEvenKey,
                      CDCA_U8        byKeyLen,
                      CDCA_BOOL      bTapingEnabled)
{
	LOGD("####################CDSTBCA_ScrSetCW function not implementted\n");
}

// -1表示拔卡，1表示插卡，0表示处理完插卡动作。这里表示的纯物理动作，不含软件层面reset的过程。
static int s_smartcard_action = 0;

int smartcard_action_set(int smartcard_action)
{
	s_smartcard_action = smartcard_action;
	LOGD("s_smartcard_action=%d\n", s_smartcard_action);
	
	return s_smartcard_action;
}

int send_sc_notify(int can_send_nofity, DBSTAR_CMD_MSG_E sc_notify, char *msg, int len)
{
	int ret = -1;
	
	if(1==can_send_nofity && -1!=s_smartcard_action){
		ret = msg_send2_UI(sc_notify, msg, len);
	}
	else{
		LOGD("can_send_nofity=%d, s_smartcard_action=%d, no need to send 0x%x\n", can_send_nofity,s_smartcard_action,sc_notify);
		ret = -1;
	}
	
	return ret;
}

/*--------- 智能卡管理 ---------*/

/* 智能卡复位 */
int smc_set(struct am_smc_atr *abuf);
CDCA_BOOL CDSTBCA_SCReset(CDCA_U8* pbyATR, CDCA_U8* pbyLen)
{
	struct am_smc_atr abuf;
	int ds, i;
	AM_SMC_CardStatus_t status;
	int can_send_nofity = 0;
	
	LOGD("CDSTBCA_SCReset s_smartcard_action=%d, smc_fd=%d\n", s_smartcard_action,smc_fd);
	if(1==s_smartcard_action)
		can_send_nofity = 1;
	
	s_smartcard_action = 0;
	
	if (smc_fd == -1) {
		smc_fd = open(SMC_DEVICE, O_RDWR);
		if (smc_fd == -1) {
			LOGD("cannot open device smc0\n");
			send_sc_notify(can_send_nofity,DRM_SC_INSERT_FAILED, NULL, 0);
			return CDCA_FALSE;
		} else {
			LOGD("open the smc device succeful [%d]\n", smc_fd);
		}
	}

#if 0
	//=========================
	LOGD("please insert a card\n");
	i = 0;
	do {
		//AM_TRY(AM_SMC_GetCardStatus(SMC_DEV_NO, &status));
		if (ioctl(smc_fd, AMSMC_IOC_GET_STATUS, &ds)) {
			LOGD("get card status failed\n");
			send_sc_notify(can_send_nofity,DRM_SC_INSERT_FAILED, NULL, 0);
			return CDCA_FALSE;
		}

		status = ds ? AM_SMC_CARD_IN : AM_SMC_CARD_OUT;
		usleep(100000);
		i++;
		if (i > 50) {
			LOGD("########### there is no smard card in \n");
			send_sc_notify(can_send_nofity,DRM_SC_INSERT_FAILED, NULL, 0);
			return CDCA_FALSE;
		}
	} while (status == AM_SMC_CARD_OUT);

	LOGD("card in\n");
	//=============================
#endif

	LOGD("reset the card = [%d]\n", smc_fd);
	if (ioctl(smc_fd, AMSMC_IOC_RESET, &abuf)) {
		LOGD("&&&&&&&&&&&&&&&&&&&&&&&&&& reset the card failed\n");
		send_sc_notify(can_send_nofity,DRM_SC_INSERT_FAILED, NULL, 0);
		return  CDCA_FALSE;
	}

	memcpy(pbyATR, abuf.atr, abuf.atr_len);
	*pbyLen = abuf.atr_len;
	LOGD("reset the smc succeful!!!\n ART: length [%d][%d]\n", *pbyLen, abuf.atr_len);
	for (i = 0; i < *pbyLen; i++) {
		LOGD("0x%x,", abuf.atr[i]);
	}
    smc_set(&abuf);
//	send_sc_notify(can_send_nofity,DRM_SC_INSERT_OK, NULL, 0);
	smart_card_insert_flag_set(1);
	
	return CDCA_TRUE;
}

/* 智能卡通讯 */
extern AM_ErrorCode_t AM_SMC_readwrite(const uint8_t *send, int slen, uint8_t *recv, int *rlen);
CDCA_BOOL CDSTBCA_SCPBRun(const CDCA_U8* pbyCommand,
                          CDCA_U16       wCommandLen,
                          CDCA_U8*       pbyReply,
                          CDCA_U16*      pwReplyLen)
{
	//int i;
#if 0
	int j;
	#define TMP_STR_SIZE	4096
	char tmp_str[TMP_STR_SIZE];
	
	memset(tmp_str,0,TMP_STR_SIZE);
	for(j=0;j<wCommandLen;j++)
		snprintf(tmp_str+strlen(tmp_str),TMP_STR_SIZE-strlen(tmp_str),"0x%x\t",pbyCommand[j]);
	LOGD("smart card command[%d]: %s\n",wCommandLen,tmp_str);
#endif

	//LOGD("ooooooooooooori send len [%d][%d]--[%d][%d]\n",wCommandLen,*pwReplyLen,pbyCommand[0],pbyCommand[1]);
	//for (i = 0; i < 3; i++) {
		if (AM_SMC_readwrite(pbyCommand, (int)wCommandLen,  pbyReply, (int *) pwReplyLen) == AM_SUCCESS) {
#if 0
			{
				memset(tmp_str,0,TMP_STR_SIZE);
				for (j=0;j<*pwReplyLen;j++)
				    snprintf(tmp_str+strlen(tmp_str),TMP_STR_SIZE-strlen(tmp_str),"0x%x\t",pbyReply[j]);
				LOGD("smart card reply[%d]: %s\n",*pwReplyLen,tmp_str);
			}
			//LOGD("AM_SMC_readwrite successful yyyyyyyyyyyyyyyyyy\n\n\n");
#endif
			return CDCA_TRUE;
		}
	//}
	LOGD("AM_SMC_readwrite fail xxxxxxxxxxxxxxx\n\n\n");
	return CDCA_FALSE;
}

/*-------- 授权信息管理 -------*/

/* 通知授权变化 */
void CDSTBCA_EntitleChanged(CDCA_U16 wTvsID)
{
	LOGD("###############CDSTBCA_EntitleChanged function not implemented, wTvsID=%u\n",wTvsID);
}


/* 反授权确认码通知 */
void CDSTBCA_DetitleReceived(CDCA_U8 bstatus)
{
	LOGD("##############CDSTBCA_DetitleReceived function not implemented [%d]\n", bstatus);
}

/*-------- 安全控制 --------*/

/* 读取机顶盒唯一编号 */
void CDSTBCA_GetSTBID(CDCA_U16* pwPlatformID,
                      CDCA_U32* pdwUniqueID)
{
	*pwPlatformID = 0;//0x1122;
	*pdwUniqueID = 0x00000000;
	LOGD("######################get STBID pwPlatformID=0x%x, pdwUniqueIDpdwUniqueID=0x%lx \n", *pwPlatformID, *pdwUniqueID);
}

/* 安全芯片接口 */
CDCA_U16 CDSTBCA_SCFunction(CDCA_U8* pData)
{
	LOGD("#################CDSTBCA_SCFunction unsupport, return 0x9100\n");
	return 0x9100;//0x9400;//0x9100;
}

/*-------- IPPV应用 -------*/

/* IPPV节目通知 */
void CDSTBCA_StartIppvBuyDlg(CDCA_U8                 byMessageType,
                             CDCA_U16                wEcmPid,
                             const SCDCAIppvBuyInfo* pIppvProgram)
{
	LOGD("##################### CDSTBCA_StartIppvBuyDlg not implemented\n");
}

/* 隐藏IPPV对话框 */
void CDSTBCA_HideIPPVDlg(CDCA_U16 wEcmPid)
{
	LOGD("##################### CDSTBCA_HideIPPVDlg not implemented\n");
}

/*------- 邮件/OSD显示管理 -------*/

/* 邮件通知 */
void CDSTBCA_EmailNotifyIcon(CDCA_U8 byShow, CDCA_U32 dwEmailID)
{
	LOGD("\n\n\n\n\n##################### CDSTBCA_EmailNotifyIcon byShow=%d, dwEmailID=%lu\n\n\n\n\n\n", byShow,dwEmailID);

#if 0
	if(CDCA_Email_New==byShow)
		msg_send2_UI(DRM_EMAIL_NEW, NULL, 0);
	else if(CDCA_Email_IconHide==byShow)
		msg_send2_UI(DRM_EMAIL_ICONHIDE, NULL, 0);
	else if(CDCA_Email_SpaceExhaust==byShow)
		msg_send2_UI(DRM_EMAIL_SPACEEXHAUST, NULL, 0);
	else
		LOGD("do nothing for email with this byShow=%d\n", byShow);
#endif
}

static char s_DRM_OSD_msg[CDCA_MAXLEN_OSD+32];
/* 显示OSD信息 */
void CDSTBCA_ShowOSDMessage(CDCA_U8	byStyle, const char* szMessage)
{
	LOGD("\n\n\n\n\n##################### CDSTBCA_ShowOSDMessage byStyle=%d,szMessage=%s\n\n\n\n\n\n",byStyle,szMessage);
	
	snprintf(s_DRM_OSD_msg,sizeof(s_DRM_OSD_msg),"%d\t%s",byStyle,szMessage);
	msg_send2_UI(DRM_OSD_SHOW, s_DRM_OSD_msg, strlen(s_DRM_OSD_msg));
}

/* 隐藏OSD信息*/
void CDSTBCA_HideOSDMessage(CDCA_U8 byStyle)
{
	LOGD("##################### CDSTBCA_HideOSDMessage byStyle=%d\n", byStyle);
	
	snprintf(s_DRM_OSD_msg,sizeof(s_DRM_OSD_msg),"%d",byStyle);
	msg_send2_UI(DRM_OSD_HIDE, s_DRM_OSD_msg, strlen(s_DRM_OSD_msg));
}



/*-------- 子母卡应用 --------*/

/* 请求提示读取喂养数据结果 */
void  CDSTBCA_RequestFeeding(CDCA_BOOL bReadStatus)
{
	if (bReadStatus == CDCA_TRUE) {
		LOGD("Please insert child card!!!!!!!!!!!!!\n");
	} else {
		LOGD("Read mother card failure!!!!!!!!!!!\n");
	}
}

/*-------- 强制切换频道 --------*/

/* 频道锁定 */
void CDSTBCA_LockService(const SCDCALockService* pLockService)
{
	LOGD("##################### CDSTBCA_LockService not implemented\n");
}

/* 解除频道锁定 */
void CDSTBCA_UNLockService(void)
{
	LOGD("##################### CDSTBCA_UNLockService not implemented\n");
}

/*-------- 显示界面管理 --------*/

/* 不能正常收看节目的提示 */
/*wEcmPID==0表示与wEcmPID无关的消息，且不能被其他消息覆盖*/
void CDSTBCA_ShowBuyMessage(CDCA_U16 wEcmPID, CDCA_U8  byMessageType)
{
	LOGD("$$$$$$$$$$$$$$$$$ no right to see this program in 	CDSTBCA_ShowBuyMessage\n");
}

/* 指纹显示 */
void CDSTBCA_ShowFingerMessage(CDCA_U16 wEcmPID, CDCA_U32 dwCardID)
{
	LOGD(" need display Ecm PID = %d  -------------Card ID =%d\n", (int)wEcmPID, (int)dwCardID);
}


/* 安全窗帘显示*/


/* 进度显示 */
void CDSTBCA_ShowProgressStrip(CDCA_U8 byProgress,  CDCA_U8 byMark)
{
	LOGD(" need display progress strip progress = %d  -------------byMark =%d\n", byProgress, byMark);
}

/*--------- 机顶盒通知 --------*/

/* 机顶盒通知 */
void  CDSTBCA_ActionRequest(CDCA_U16 wTVSID, CDCA_U8  byActionType)
{
	LOGD("######################### CDSTBCA_ActionRequest do not impletement\n");
}


/*--------- 双向模块接口 --------*/

/* 回传数据通知*/
void CDSTBCA_NotifyCallBack(void)
{
	LOGD("######################### CDSTBCA_NotifyCallBack do not impletement\n");
}

/*-------- 其它 --------*/

/* 获取字符串长度 */
CDCA_U16 CDSTBCA_Strlen(const char* pString)
{
	LOGD("########## return string length = %d\n", strlen(pString));
	return (CDCA_U16)strlen(pString);
}

/* 调试信息输出 */
void CDSTBCA_Printf(CDCA_U8 byLevel, const char* szMesssage)
{
	LOGD("[DRM](%d) %s\n", byLevel, szMesssage);
}

/*-------- PVODDRM模块接口 -------*/

#if 0
void Card_Entitle_init()
{
	sprintf(card_sn[0].sn, "8000302100000333");
	card_sn[0].fd = NULL;
}

/* 打开授权文件 */
CDCA_BOOL CDSTBCA_DRM_OpenEntitleFile(char   CardSN[CDCA_MAXLEN_SN + 1],  void** pFileHandle)
{
	int i;

	LOGD("open the entitle file [%s]\n", CardSN);
	for (i = 0; i < CDCA_MAX_CARD_NUM; i++) {
		LOGD("candsn [%s][%s][%d][%d]\n", card_sn[i].sn, CardSN, i, strcmp(card_sn[i].sn, CardSN));
		if (!strcmp(card_sn[i].sn, CardSN)) {
			if (card_sn[i].fd) {
				*pFileHandle = card_sn[i].fd;
			} else {
				card_sn[i].fd = fopen(card_sn[i].sn, "r+"); //a+ 以附加方式打开可读写的文件。若不存在，建立，存在，加到文件尾后
				if (card_sn[i].fd) {
					LOGD("open the entitle [%d] successful\n", i);
				}
				*pFileHandle = card_sn[i].fd;
				//break;
			}
			break;
		}
	}

	if (i >= CDCA_MAX_CARD_NUM) {
		strcpy(card_sn[0].sn, CardSN);
		card_sn[0].fd = fopen(card_sn[0].sn, "w+"); //a+ 以附加方式打开可读写的文件。若不存在，建立，存在，加到文件尾后
		if (card_sn[0].fd) {
			LOGD("open the entitle 0 successful\n");
		}
		*pFileHandle = card_sn[0].fd;
	}

	if (*pFileHandle) {
		return CDCA_TRUE;
	}
	LOGD("open the entitle file failed!!!!!\n");
	return CDCA_FALSE;
}
#endif

CDCA_BOOL CDSTBCA_DRM_OpenEntitleFile(char   CardSN[CDCA_MAXLEN_SN + 1],  void** pFileHandle)
{
	int ret = 0;
	char fullentitle[CDCA_MAXLEN_SN_PATH];
	
	*(int *)pFileHandle = -1;
	sprintf(fullentitle, "%s/%s", ENTITLE_FILE_PATH, CardSN);
//	LOGD("will open entitle file [%s]\n", fullentitle);
	if (access(fullentitle, 0)) { //not exsit
		if (card_sn.fd != -1) {
			close(card_sn.fd);
		}
		ret = mkdirp(ENTITLE_FILE_PATH);
		if (ret != 0) {
			LOGD("--- create the entitle path error. [%s]\n", strerror(errno));
			return CDCA_FALSE;
		}
		strncpy(card_sn.sn, fullentitle, CDCA_MAXLEN_SN_PATH);
		card_sn.fd = open(fullentitle, O_CREAT|O_RDWR,S_IRUSR|S_IWUSR|S_IRGRP|S_IWGRP|S_IROTH|S_IWOTH); //"w+"); //a+ 以附加方式打开可读写的文件。若不存在，建立，存在，加到文件尾后
		if (card_sn.fd >= 0) {
			LOGD("open origine entitle %s(%d) successful\n",fullentitle,card_sn.fd);
		}
		*pFileHandle = &card_sn.fd;
	} else {
		if (card_sn.fd >= 0) {
			if (!strcmp(card_sn.sn, fullentitle)) {
				*pFileHandle = &card_sn.fd;
			} else {
				close(card_sn.fd);
				strncpy(card_sn.sn, fullentitle, CDCA_MAXLEN_SN_PATH);
				card_sn.fd = open(card_sn.sn, O_RDWR);//"r+"); //a+ 以附加方式打开可读写的文件。若不存在，建立，存在，加到文件尾后
				if (card_sn.fd >= 0) {
					LOGD("open another entitle %s(%d) successful\n",fullentitle,card_sn.fd);
				}
				*pFileHandle = &card_sn.fd;
			}
		} else {
			strncpy(card_sn.sn, fullentitle, CDCA_MAXLEN_SN_PATH);
			card_sn.fd = open(fullentitle, O_RDWR);//"r+"); //a+ 以附加方式打开可读写的文件。若不存在，建立，存在，加到文件尾后
			if (card_sn.fd >= 0) {
				LOGD("open entitle %s(%d) successful\n",fullentitle,card_sn.fd);
			}
			*pFileHandle = &card_sn.fd;
		}
	}

	if ((int)(*pFileHandle) >= 0) {
		return CDCA_TRUE;
	}
	LOGD("open the entitle file failed!!!!!\n");
	return CDCA_FALSE;
}

/* 关闭授权文件 */
void CDSTBCA_DRM_CloseEntitleFile(const void*  pFileHandle)
{
	LOGD("close the entitle file!!!!\n");
	close(*(int *)pFileHandle);
	memset(&card_sn, 0, sizeof(SCDCACardEntitleInfo));
        card_sn.fd = -1;
}

/* 移动文件指针*/
CDCA_BOOL CDSTBCA_SeekPos(const void* pFileHandle,
                          CDCA_U8     byOrigin,
                          CDCA_U32    dwOffsetKByte,
                          CDCA_U32    dwOffsetByte)
{
	long long posk = (long long)dwOffsetKByte;
	long long posb = (long long)dwOffsetByte;
	long long offset = 1024 * posk + posb;
	long long file_pos = 0LL;
	
//	LOGD("++++ seek file(%d) byori=[%d] posk=[%lu] pos=[%lu] offset=[%llu]\n", *(int *)pFileHandle,byOrigin, dwOffsetKByte, dwOffsetByte, offset);
//	LOGD(">--- seek fd(%d) from file pos: %lld\n", *(int *)pFileHandle,lseek64(*(int *)pFileHandle, 0, SEEK_CUR));
	
	if (*(int *)pFileHandle < 0) {
		return CDCA_FALSE;
	}
	
	//LOGD("%s *(int *)pFileHandle=%d\n", __FUNCTION__,*(int *)pFileHandle);
	if (byOrigin == CDCA_SEEK_SET) {
		if ((file_pos=lseek64(*(int *)pFileHandle, offset, SEEK_SET)) < 0) {
			LOGE("!!!!!!!!!!!!!!!!!!!!!!CDCA_SEEK_SET!!fseek error\n");
			return CDCA_FALSE;
		}
//		LOGD(">>>> lseek64(%d,%lld,%d) at [%lld]\n", *(int *)pFileHandle,offset,byOrigin,file_pos);
	} else if (byOrigin == CDCA_SEEK_CUR_BACKWARD) {
		if ((file_pos=lseek64(*(int *)pFileHandle, -offset, SEEK_CUR)) < 0) {
			LOGE("!!!!!!!!!!!!!!!!!!!!CDCA_SEEK_CUR_BACKWARD!!!!fseek error\n");
			return CDCA_FALSE;
		}
		
//		LOGD(">>>> lseek64(%d,%lld,%d) at [%lld]\n", *(int *)pFileHandle,offset,byOrigin,file_pos);
	} else if (byOrigin == CDCA_SEEK_CUR_FORWARD) {
		if ((file_pos=lseek64(*(int *)pFileHandle, offset, SEEK_CUR)) < 0) {
			LOGE("!!!!!!!!!!!!!!!!!!!!CDCA_SEEK_CUR_FORWARD!!!!fseek error\n");
			return CDCA_FALSE;
		}
//		LOGD(">>>> lseek64(%d,%lld,%d) at [%lld]\n", *(int *)pFileHandle,offset,byOrigin,file_pos);
	} else if (byOrigin == CDCA_SEEK_END) {
		if ((file_pos=lseek64(*(int *)pFileHandle, -offset, SEEK_END)) < 0) {
			LOGE("!!!!!!!!!!!!!!!!!!!!CDCA_SEEK_END!!!!fseek error\n");
			return CDCA_FALSE;
		}
//		LOGD(">>>> lseek64(%d,%lld,%d) at [%lld]\n", *(int *)pFileHandle,offset,byOrigin,file_pos);
	}
	else
		LOGD(">>>> faile to lseek64(%d,%d,%lld)\n", *(int *)pFileHandle,byOrigin,offset);
	//LOGD("seek the file pos successful\n");
	
	return CDCA_TRUE;
}

/* 读文件 */
CDCA_U32 CDSTBCA_ReadFile(const void* pFileHandle, CDCA_U8* pBuf, CDCA_U32 dwLen)
{
	int ret;
	//LOGD("read file len [%lu]\n", dwLen);
	if ((*(int *)pFileHandle) < 0) {
		return -1;
	}
	
//	LOGD("read fd(%d) at file pos: %lld\n", *(int *)pFileHandle,lseek64(*(int *)pFileHandle, 0, SEEK_CUR));
	ret = read((*(int *)pFileHandle), pBuf, dwLen);
	if (ret > 0) {
//		LOGD("read [%lu] from fd(%d), file pos arrive at %lld\n", dwLen,(*(int *)pFileHandle),lseek64(*(int *)pFileHandle, 0, SEEK_CUR));
	} else {
		LOGD("want read [%lu] from fd(%d) but failed[%d]\n", dwLen, (*(int *)pFileHandle), ret);
	}
	return ret;
}

/* 写文件 */
CDCA_U32 CDSTBCA_WriteFile(const void* pFileHandle, CDCA_U8* pBuf, CDCA_U32 dwLen)
{
	CDCA_U32 ret;

	LOGD("write file len [%lu]\n", dwLen);
	if ((*(int *)pFileHandle) < 0) {
		return -1;
	}
	/*for (ret = 0 ; ret < dwLen; ret++) {
		LOGD("0x%x,", pBuf[ret]);
	}*/
	/*return*/ret = write(*(int *)pFileHandle, pBuf, dwLen);
	if (ret > 0) {
		LOGD("write file successful[%lu][%lu]!!!!\n", ret, dwLen);
	} else {
		LOGD("write file failed!!!!!!\n");
	}
	//fflush((FILE *)pFileHandle);
	return ret;
}

/* 类似于 fgets(char *buf, int bufsize, FILE *stream); */
CDCA_U8 *CDSTBCA_ReadLine(const void *pFileHandle,CDCA_U8 *pbyBuf,CDCA_U32 pdwLen)
{
	if ((*(int *)pFileHandle) < 0 || 0==pdwLen) {
		LOGD("CDSTBCA_ReadLine failed: %d, %lu\n", (*(int *)pFileHandle),pdwLen);
		return NULL;
	}

	int i = 0;
	int ret = 0;
	for(i=0;i<(pdwLen-1);i++){
		ret = read((*(int *)pFileHandle), pbyBuf+i, 1);
		if(1==ret){
			if('\n'==pbyBuf[i]){
				pbyBuf[i] = '\0';
				break;
			}
		}
		else if(0==ret){
			LOGD("CDSTBCA_ReadLine want len %lu but 0, success already\n", pdwLen);
			pbyBuf[i] = '\0';
		}
		else{
			ERROROUT("CDSTBCA_ReadLine failed: %d\n", ret);
			return NULL;
		}
	}
	
	pbyBuf[pdwLen-1] = '\0';
	LOGD("CDSTBCA_ReadLine %lu: [%s]\n", pdwLen,pbyBuf);
	
	return pbyBuf;
}

