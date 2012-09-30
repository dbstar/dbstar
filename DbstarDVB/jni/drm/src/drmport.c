#include <pthread.h>
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include <am_smc.h>
#include <fcntl.h>
#include <sys/ioctl.h>

#include "linux/amsmc.h"
#include "prodrm20.h"

#if 0
typedef struct {
	char sn[CDCA_MAXLEN_SN + 1];
	FILE *fd;
} SCDCACardEntitleInfo;

typedef struct {
	CDCA_U8        byReqID;
	CDCA_U16      wPID;
	CDCA_U32      timeouttime;
} SCDCAFilterInfo;
#endif
//#define CDCA_MAX_CARD_NUM 2
#define SMC_DEVICE  "/dev/smc0"
#define BLOCK01_FILE "/mnt/sdb1/drm/entitle/block01"
#define ENTITLE_FILE_PATH "/mnt/sdb1/drm/entitle"
//#define DMX_DEV_NO 0

//CDCA_U8 checkTimeoutMark = 0;
int smc_fd = -1;


FILE *block01_fd = NULL;

SCDCACardEntitleInfo card_sn = {"", NULL}; //[CDCA_MAX_CARD_NUM];
//SCDCAFilterInfo dmx_filter[MAX_CHAN_FILTER];
//extern Channel_t chanFilter[];
//extern int max_filter_num;


/*-------- 线程管理 --------*/

void CDSTBCA_HDDec_CloseDecrypter(const void *pCtx)
{
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
	int ret = 0;
	printf("DRM create thread[%d], thread name [%s]\n", i, szName);
	if (pthread_create(&tmpthread[i], NULL, pTaskFun, pParam) != 0) {
		printf("DRM Create task [%s] ERROR\n", szName);
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
	//printf("init a semaphore [%d]\n",*pSemaphore);
}

/* 信号量给予信号 */
void CDSTBCA_SemaphoreSignal(CDCA_Semaphore* pSemaphore)
{
	//printf("release semaphore...\n");
	*pSemaphore = 1;
}

/* 信号量获取信号 */
void CDSTBCA_SemaphoreWait(CDCA_Semaphore* pSemaphore)
{
	//printf("wait semaphore 1\n");
	while (*pSemaphore == 0) {
		usleep(5000);
	}
	*pSemaphore = 0;
	//printf("got a semaphore 2\n");
}


/*-------- 内存管理 --------*/

/* 分配内存 */
void* CDSTBCA_Malloc(CDCA_U32 byBufSize)
{
	return malloc(byBufSize);
}

/* 释放内存 */
void  CDSTBCA_Free(void* pBuf)
{
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

	printf("###############Read the flash 64k buffer [%d]\n", byBlockID);
	if (block01_fd == NULL) {
		//	CDCA_U8 tmp[128*1024];

		//	memset(tmp,0,128*1024);
		block01_fd = fopen(BLOCK01_FILE, "r+");

		if (block01_fd == NULL) {
			printf("open the flash read file error!!!!\n");
			return;
		}
		//  fwrite(tmp,1,128*1024,block01_fd);
	}
	if (byBlockID == CDCA_FLASH_BLOCK_B) {
		fseek(block01_fd, 64 * 1024, SEEK_SET);
		len = *pdwLen;
		*pdwLen = fread(pbyData, 1, len, block01_fd);
		printf("read flash block 2[%d]\n", *pdwLen);
	} else if (byBlockID == CDCA_FLASH_BLOCK_A) {
		fseek(block01_fd, 0, SEEK_SET);
		len = *pdwLen;
		*pdwLen = fread(pbyData, 1, len, block01_fd);
		printf("read flash block 1[%d]\n", *pdwLen);
	}
}

/* 写入存储空间 */
void CDSTBCA_WriteBuffer(CDCA_U8 byBlockID, const CDCA_U8* pbyData, CDCA_U32 dwLen)
{
	printf("###############Write the flash 64k buffer [%d]\n", byBlockID);
	if (block01_fd == NULL) {
		//	CDCA_U8 tmp[128*1024];

		//	memset(tmp,0,128*1024);
		block01_fd = fopen(BLOCK01_FILE, "r+");
		if (block01_fd == NULL) {
			printf("open the flash file error!!!!!\n");
			return;
		}
		//  fwrite(tmp,1,128*1024,block01_fd);
	}
	if (byBlockID == CDCA_FLASH_BLOCK_B) {
		fseek(block01_fd, 64 * 1024, SEEK_SET);
		fwrite(pbyData, 1, dwLen, block01_fd);
		printf("write flash block 2 [%d]\n", dwLen);
	} else if (byBlockID == CDCA_FLASH_BLOCK_A) {
		fseek(block01_fd, 0, SEEK_SET);
		fwrite(pbyData, 1, dwLen, block01_fd);
		printf("write flash block 1 [%d]\n", dwLen);
	}
	fflush(block01_fd);
}
#endif

/*-------- TS流管理 --------*/

#if 0
void filter_timeout_handler(int fid)
{
	if (fid >= max_filter_num) {
		return;
	}
	if (checkTimeoutMark) {
		checkTimeoutMark --;
	}
	TC_free_filter(fid);
	dmx_filter[fid].byReqID = 0xff;
	dmx_filter[fid].wPID = 0xffff;
	dmx_filter[fid].timeouttime = 0;
}


static void filter_dump_bytes(int fid, const uint8_t *data, int len, void *user_data)
{
	CDCA_U8        byReqID;
	CDCA_U16       wPid;
	SCDCAFilterInfo *filterinfo;

	printf("Got EMM data len [%d]\n", len);
	/*{
	 int i;

	                for(i=0;i<len;i++)
	                {
	                        printf("%02x ", data[i]);
	                        if(((i+1)%32)==0) printf("\n");
	                }

	                if((i%32)!=0) printf("\n");

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
		if (checkTimeoutMark) {
			checkTimeoutMark --;
		}
		dmx_filter[fid].timeouttime = 0;
		CDSTBCA_ReleasePrivateDataFilter(byReqID, wPid);
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
	printf("CDSTBCA_SetPrivateDataFilter() called\n");
#if 0
	Filter_param param;
	//Channel_t *filter;
	int fid, i;


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

	fid = TC_alloc_filter(wPid, &param, (dataCb)filter_dump_bytes, (void *)&dmx_filter[0], 0);
	if (fid >= MAX_CHAN_FILTER) {
		return  CDCA_FALSE;
	}
	dmx_filter[fid].wPID = wPid;
	dmx_filter[fid].byReqID = byReqID;
	dmx_filter[fid].fid = fid;
	if (byWaitSeconds) {
		int now;
		checkTimeoutMark ++;
		AM_TIME_GetClock(&now);
		dmx_filter[fid].timeouttime = now + byWaitSeconds * 1000;
	} else {
		dmx_filter[fid].timeouttime = 0;
	}

#endif
	return CDCA_TRUE;
}

/* 释放私有数据过滤器 */
void CDSTBCA_ReleasePrivateDataFilter(CDCA_U8  byReqID, CDCA_U16 wPid)
{
#if 0
	int fid;

	for (fid = 0; fid < max_filter_num; fid++) {
		if ((dmx_filter[fid].byReqID == byReqID) && (dmx_filter[fid].wPID == wPid) && (chanFilter[fid].used)) {
			break;
		}
	}
	printf("@@@@@@@@@@@release [%d] filter\n", fid);
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
	printf("####################CDSTBCA_ScrSetCW function not implementted\n");
}


/*--------- 智能卡管理 ---------*/

/* 智能卡复位 */
CDCA_BOOL CDSTBCA_SCReset(CDCA_U8* pbyATR, CDCA_U8* pbyLen)
{
	struct am_smc_atr abuf;
	int ds, i;
	AM_SMC_CardStatus_t status;


	if (smc_fd == -1) {
		smc_fd = open(SMC_DEVICE, O_RDWR);
		if (smc_fd == -1) {
			printf("cannot open device smc0\n");
			return CDCA_FALSE;
		} else {
			printf("open the smc device succeful [%d]\n", smc_fd);
		}
	}

	//=========================
	printf("please insert a card\n");
	i = 0;
	do {
		//AM_TRY(AM_SMC_GetCardStatus(SMC_DEV_NO, &status));
		if (ioctl(smc_fd, AMSMC_IOC_GET_STATUS, &ds)) {
			printf("get card status failed\n");
			return -1;
		}

		status = ds ? AM_SMC_CARD_IN : AM_SMC_CARD_OUT;
		usleep(100000);
		i++;
		if (i > 50) {
			printf("########### there is no smard card in \n");
			return CDCA_FALSE;
		}
	} while (status == AM_SMC_CARD_OUT);

	printf("card in\n");


	//=============================

	printf("&&&&&&&&&&&&&&&&&&&&&&&&&& reset the card = [%d]\n", smc_fd);
	if (ioctl(smc_fd, AMSMC_IOC_RESET, &abuf)) {
		printf("reset the card failed");
		return  CDCA_FALSE;
	}

	memcpy(pbyATR, abuf.atr, abuf.atr_len);
	*pbyLen = abuf.atr_len;
	printf("reset the smc succeful!!!\n ART: length [%d][%d]\n", *pbyLen, abuf.atr_len);
	for (i = 0; i < *pbyLen; i++) {
		printf("0x%x,", abuf.atr[i]);
	}
	return CDCA_TRUE;
}

/* 智能卡通讯 */
extern AM_ErrorCode_t AM_SMC_readwrite(const uint8_t *send, int slen, uint8_t *recv, int *rlen);
CDCA_BOOL CDSTBCA_SCPBRun(const CDCA_U8* pbyCommand,
                          CDCA_U16       wCommandLen,
                          CDCA_U8*       pbyReply,
                          CDCA_U16*      pwReplyLen)
{
	int i;
	//printf("ooooooooooooori send len [%d][%d]--[%d][%d]\n",wCommandLen,*pwReplyLen,pbyCommand[0],pbyCommand[1]);
	for (i = 0; i < 3; i++) {
		if (AM_SMC_readwrite(pbyCommand, (int)wCommandLen,  pbyReply, (int *) pwReplyLen) == AM_SUCCESS) {
			/*{int j;
			printf("smart card command:\n");
			for (j=0;j<wCommandLen;j++)
			    printf("0x%x,",pbyCommand[j]);
			printf("\n");
			printf("smart card reply:\n");

			for (j=0;j<*pwReplyLen;j++)
			    printf("0x%x,",pbyReply[j]);
			printf("\n");

			}*/
			//printf("sssssssssssssssssssssssend read successful\n");
			return CDCA_TRUE;
		}
	}
	printf("sssssssssssssssssssssssssssend fail \n");
	return CDCA_FALSE;
}

/*-------- 授权信息管理 -------*/

/* 通知授权变化 */
void CDSTBCA_EntitleChanged(CDCA_U16 wTvsID)
{
	printf("###############CDSTBCA_EntitleChanged function not implemented\n");
}


/* 反授权确认码通知 */
void CDSTBCA_DetitleReceived(CDCA_U8 bstatus)
{
	printf("##############CDSTBCA_DetitleReceived function not implemented [%d]\n", bstatus);
}

/*-------- 安全控制 --------*/

/* 读取机顶盒唯一编号 */
void CDSTBCA_GetSTBID(CDCA_U16* pwPlatformID,
                      CDCA_U32* pdwUniqueID)
{
	*pwPlatformID = 0;//0x1122;
	*pdwUniqueID = 0x00000000;
	printf("######################get STBID pwPlatformID=0x%x, pdwUniqueIDpdwUniqueID=0x%x \n", *pwPlatformID, *pdwUniqueID);
}

/* 安全芯片接口 */
CDCA_U16 CDSTBCA_SCFunction(CDCA_U8* pData)
{
	printf("#################CDSTBCA_SCFunction unsupport, return 0x9100\n");
	return 0x9100;//0x9400;//0x9100;
}

/*-------- IPPV应用 -------*/

/* IPPV节目通知 */
void CDSTBCA_StartIppvBuyDlg(CDCA_U8                 byMessageType,
                             CDCA_U16                wEcmPid,
                             const SCDCAIppvBuyInfo* pIppvProgram)
{
	printf("##################### CDSTBCA_StartIppvBuyDlg not implemented\n");
}

/* 隐藏IPPV对话框 */
void CDSTBCA_HideIPPVDlg(CDCA_U16 wEcmPid)
{
	printf("##################### CDSTBCA_HideIPPVDlg not implemented\n");
}

/*------- 邮件/OSD显示管理 -------*/

/* 邮件通知 */
void CDSTBCA_EmailNotifyIcon(CDCA_U8 byShow, CDCA_U32 dwEmailID)
{
	printf("##################### CDSTBCA_EmailNotifyIcon not implemented\n");
}

/* 显示OSD信息 */
void CDSTBCA_ShowOSDMessage(CDCA_U8     byStyle, const char* szMessage)
{
	printf("##################### CDSTBCA_ShowOSDMessage not implemented\n");
}

/* 隐藏OSD信息*/
void CDSTBCA_HideOSDMessage(CDCA_U8 byStyle)
{
	printf("##################### CDSTBCA_HideOSDMessage not implemented\n");
}



/*-------- 子母卡应用 --------*/

/* 请求提示读取喂养数据结果 */
void  CDSTBCA_RequestFeeding(CDCA_BOOL bReadStatus)
{
	if (bReadStatus == CDCA_TRUE) {
		printf("Please insert child card!!!!!!!!!!!!!\n");
	} else {
		printf("Read mother card failure!!!!!!!!!!!\n");
	}
}

/*-------- 强制切换频道 --------*/

/* 频道锁定 */
void CDSTBCA_LockService(const SCDCALockService* pLockService)
{
	printf("##################### CDSTBCA_LockService not implemented\n");
}

/* 解除频道锁定 */
void CDSTBCA_UNLockService(void)
{
	printf("##################### CDSTBCA_UNLockService not implemented\n");
}

/*-------- 显示界面管理 --------*/

/* 不能正常收看节目的提示 */
/*wEcmPID==0表示与wEcmPID无关的消息，且不能被其他消息覆盖*/
void CDSTBCA_ShowBuyMessage(CDCA_U16 wEcmPID, CDCA_U8  byMessageType)
{
	printf("$$$$$$$$$$$$$$$$$ no right to see this program in 	CDSTBCA_ShowBuyMessage\n");
}

/* 指纹显示 */
void CDSTBCA_ShowFingerMessage(CDCA_U16 wEcmPID, CDCA_U32 dwCardID)
{
	printf(" need display Ecm PID = %d  -------------Card ID =%d\n", (int)wEcmPID, (int)dwCardID);
}


/* 安全窗帘显示*/


/* 进度显示 */
void CDSTBCA_ShowProgressStrip(CDCA_U8 byProgress,  CDCA_U8 byMark)
{
	printf(" need display progress strip progress = %d  -------------byMark =%d\n", byProgress, byMark);
}

/*--------- 机顶盒通知 --------*/

/* 机顶盒通知 */
void  CDSTBCA_ActionRequest(CDCA_U16 wTVSID, CDCA_U8  byActionType)
{
	printf("######################### CDSTBCA_ActionRequest do not impletement\n");
}


/*--------- 双向模块接口 --------*/

/* 回传数据通知*/
void CDSTBCA_NotifyCallBack(void)
{
	printf("######################### CDSTBCA_NotifyCallBack do not impletement\n");
}

/*-------- 其它 --------*/

/* 获取字符串长度 */
CDCA_U16 CDSTBCA_Strlen(const char* pString)
{
	printf("########## return string length = %d\n", strlen(pString));
	return (CDCA_U16)strlen(pString);
}

/* 调试信息输出 */
void CDSTBCA_Printf(CDCA_U8 byLevel, const char* szMesssage)
{
	printf("[DRM](%d) %s\n", byLevel, szMesssage);
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

	printf("open the entitle file [%s]\n", CardSN);
	for (i = 0; i < CDCA_MAX_CARD_NUM; i++) {
		printf("candsn [%s][%s][%d][%d]\n", card_sn[i].sn, CardSN, i, strcmp(card_sn[i].sn, CardSN));
		if (!strcmp(card_sn[i].sn, CardSN)) {
			if (card_sn[i].fd) {
				*pFileHandle = card_sn[i].fd;
			} else {
				card_sn[i].fd = fopen(card_sn[i].sn, "r+"); //a+ 以附加方式打开可读写的文件。若不存在，建立，存在，加到文件尾后
				if (card_sn[i].fd) {
					printf("open the entitle [%d] successful\n", i);
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
			printf("open the entitle 0 successful\n");
		}
		*pFileHandle = card_sn[0].fd;
	}

	if (*pFileHandle) {
		return CDCA_TRUE;
	}
	printf("open the entitle file failed!!!!!\n");
	return CDCA_FALSE;
}
#endif

CDCA_BOOL CDSTBCA_DRM_OpenEntitleFile(char   CardSN[CDCA_MAXLEN_SN + 1],  void** pFileHandle)
{
	//int i;
	char fullentitle[CDCA_MAXLEN_SN_PATH];

	sprintf(fullentitle, "%s/%s", ENTITLE_FILE_PATH, CardSN);
	printf("open the entitle file [%s]\n", fullentitle);
	if (access(fullentitle, 0)) { //not exsit
		if (card_sn.fd) {
			fclose(card_sn.fd);
		}
		strncpy(card_sn.sn, fullentitle, CDCA_MAXLEN_SN_PATH);
		card_sn.fd = fopen(fullentitle, "w+"); //a+ 以附加方式打开可读写的文件。若不存在，建立，存在，加到文件尾后
		if (card_sn.fd) {
			printf("open the entitle 0 successful\n");
		}
		*pFileHandle = card_sn.fd;
	} else {
		if (card_sn.fd) {
			if (!strcmp(card_sn.sn, fullentitle)) {
				*pFileHandle = card_sn.fd;
			} else {
				fclose(card_sn.fd);
				strncpy(card_sn.sn, fullentitle, CDCA_MAXLEN_SN_PATH);
				card_sn.fd = fopen(card_sn.sn, "r+"); //a+ 以附加方式打开可读写的文件。若不存在，建立，存在，加到文件尾后
				if (card_sn.fd) {
					printf("open the entitle 0 successful\n");
				}
				*pFileHandle = card_sn.fd;
			}
		} else {
			strncpy(card_sn.sn, fullentitle, CDCA_MAXLEN_SN_PATH);
			card_sn.fd = fopen(fullentitle, "r+"); //a+ 以附加方式打开可读写的文件。若不存在，建立，存在，加到文件尾后
			if (card_sn.fd) {
				printf("open the entitle [0] successful\n");
			}
			*pFileHandle = card_sn.fd;
		}
	}

	if (*pFileHandle) {
		return CDCA_TRUE;
	}
	printf("open the entitle file failed!!!!!\n");
	return CDCA_FALSE;
}

/* 关闭授权文件 */
void CDSTBCA_DRM_CloseEntitleFile(void*  pFileHandle)
{
	printf("close the entitle file!!!!\n");
	fclose(pFileHandle);
}

/* 移动文件指针*/
CDCA_BOOL CDSTBCA_SeekPos(const void* pFileHandle,
                          CDCA_U8     byOrigin,
                          CDCA_U32    dwOffsetKByte,
                          CDCA_U32    dwOffsetByte)
{
	printf("seek the file ori=[%d] posk=[%d] pos=[%d] \n", byOrigin, dwOffsetKByte, dwOffsetByte);

	if (!pFileHandle) {
		return CDCA_FALSE;
	}
	if (byOrigin == CDCA_SEEK_SET) {
		if (fseek((FILE *)pFileHandle, 1024 * dwOffsetKByte + dwOffsetByte, SEEK_SET)) {
			printf("!!!!!!!!!!!!!!!!!!!!!!!!!!fseek error\n");
			return CDCA_FALSE;
		}
	} else if (byOrigin == CDCA_SEEK_CUR_BACKWARD) {
		if (fseek((FILE *)pFileHandle, 1024 * dwOffsetKByte + dwOffsetByte, SEEK_CUR)) {
			printf("!!!!!!!!!!!!!!!!!!!!!!!!!!fseek error\n");
			return CDCA_FALSE;
		}
	} else if (byOrigin == CDCA_SEEK_CUR_FORWARD) {
		if (fseek((FILE *)pFileHandle, -(1024 * dwOffsetKByte + dwOffsetByte), SEEK_CUR)) {
			printf("!!!!!!!!!!!!!!!!!!!!!!!!!!fseek error\n");
			return CDCA_FALSE;
		}
	} else if (byOrigin == CDCA_SEEK_END) {
		if (fseek((FILE *)pFileHandle, -(1024 * dwOffsetKByte + dwOffsetByte), SEEK_END)) {
			printf("!!!!!!!!!!!!!!!!!!!!!!!!!!fseek error\n");
			return CDCA_FALSE;
		}
	}
	printf("seek the file pos successful\n");
	return CDCA_TRUE;
}

/* 读文件 */
CDCA_U32 CDSTBCA_ReadFile(const void* pFileHandle, CDCA_U8* pBuf, CDCA_U32 dwLen)
{
	int ret;
	printf("read file len [%d]\n", dwLen);
	if (!pFileHandle) {
		return -1;
	}

	ret = fread(pBuf, 1, dwLen, (FILE *)pFileHandle);
	if (ret > 0) {
		printf("read file successful[%d][%d]!!!!\n", ret, dwLen);
	} else {
		printf("read file failed!!!!!!\n");
	}
	return ret;
}

/* 写文件 */
CDCA_U32 CDSTBCA_WriteFile(const void* pFileHandle, CDCA_U8* pBuf, CDCA_U32 dwLen)
{
	int ret;
	printf("write file len [%d]\n", dwLen);
	if (!pFileHandle) {
		return -1;
	}
	for (ret = 0 ; ret < dwLen; ret++) {
		printf("0x%x,", pBuf[ret]);
	}
	/*return*/ret =  fwrite(pBuf, 1, dwLen, (FILE *)pFileHandle);
	if (ret > 0) {
		printf("write file successful[%d][%d]!!!!\n", ret, dwLen);
	} else {
		printf("write file failed!!!!!!\n");
	}
	fflush((FILE *)pFileHandle);
	return ret;
}
