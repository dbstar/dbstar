#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <limits.h>
#include <sys/errno.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <poll.h>
#include <sys/ioctl.h>
#include <linux/amsmc.h>
#include <time.h>

#include "am/am_smc.h"
#include "am/am_smc_internal.h"

extern int smc_fd;

AM_ErrorCode_t AM_TIME_GetClock(int *clock)
{
	struct timespec ts;
	int ms;

	clock_gettime(CLOCK_REALTIME, &ts);
	ms = ts.tv_sec * 1000 + ts.tv_nsec / 1000000;
	*clock = ms;

	return AM_SUCCESS;
}

AM_ErrorCode_t readbytes(int fd, uint8_t *data, int *len, int timeout)
{
	struct pollfd pfd;
	int ret;

	pfd.fd = fd;
	pfd.events = POLLIN;

	ret = poll(&pfd, 1, timeout);
	if (ret != 1) {
		return AM_SMC_ERR_TIMEOUT;
	}

	ret = read(fd, data, *len);
	if (ret < 0) {
		printf("card read error\n");
		return AM_SMC_ERR_IO;
	}

	*len = ret;
	return AM_SUCCESS;
}

AM_ErrorCode_t writebytes(int fd, const uint8_t *data, int *len, int timeout)
{
	struct pollfd pfd;
	int ret;

	pfd.fd = fd;
	pfd.events = POLLOUT;

	ret = poll(&pfd, 1, timeout);
	if (ret != 1) {
		return AM_SMC_ERR_TIMEOUT;
	}

	ret = write(fd, data, *len);
	if (ret < 0) {
		printf("card write error\n");
		return AM_SMC_ERR_IO;
	}

	*len = ret;
	return AM_SUCCESS;
}

/**\brief 从智能卡读取数据*/
static AM_ErrorCode_t smc_read(int fd, uint8_t *buf, int len, int *act_len, int timeout)
{
	uint8_t *ptr = buf;
	int left = len;
	int now, end = 0, diff, cnt = 0;
	AM_ErrorCode_t ret = AM_SUCCESS;

	if (timeout >= 0) {
		AM_TIME_GetClock(&now);
		end = now + timeout;
	}

	while (left) {
		int tlen = left;
		int ms;

		if (timeout >= 0) {
			ms = end - now;
		} else {
			ms = -1;
		}

		ret = readbytes(fd, ptr, &tlen, ms);

		if (ret < 0) {
			break;
		}

		ptr  += tlen;
		left -= tlen;
		cnt  += tlen;

		AM_TIME_GetClock(&now);
		diff = now - end;
		if (diff >= 0) {
			printf("read %d bytes timeout", len);
			ret = AM_SMC_ERR_TIMEOUT;
			break;
		}
	}

	if (act_len) {
		*act_len = cnt;
	}

	return ret;
}


/**\brief 向智能卡发送数据*/
static AM_ErrorCode_t smc_write(int fd, const uint8_t *buf, int len, int *act_len, int timeout)
{
	const uint8_t *ptr = buf;
	int left = len;
	int now, end = 0, diff, cnt = 0;
	AM_ErrorCode_t ret = AM_SUCCESS;

	if (timeout >= 0) {
		AM_TIME_GetClock(&now);
		end = now + timeout;
	}

	while (left) {
		int tlen = left;
		int ms;

		if (timeout >= 0) {
			ms = end - now;
		} else {
			ms = -1;
		}

		ret = writebytes(fd, ptr, &tlen, ms);

		if (ret < 0) {
			break;
		}

		ptr  += tlen;
		left -= tlen;
		cnt  += tlen;

		AM_TIME_GetClock(&now);
		diff = now - end;
		if (diff >= 0) {
			printf("write %d bytes timeout", len);
			ret = AM_SMC_ERR_TIMEOUT;
			break;
		}
	}

	if (act_len) {
		*act_len = cnt;
	}

	return ret;
}

/**\brief 按T0协议传输数据
 * \param dev_no 智能卡设备号
 * \param[in] send 发送数据缓冲区
 * \param[in] slen 待发送的数据长度
 * \param[out] recv 接收数据缓冲区
 * \param[out] rlen 返回接收数据的长度
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_smc.h)
 */
AM_ErrorCode_t AM_SMC_readwrite(const uint8_t *send, int slen, uint8_t *recv, int *rlen)
{
	int fd;
	AM_ErrorCode_t ret = AM_SUCCESS;
	uint8_t byte;
	uint8_t *dst;
	int left, act_len = 0;
	AM_Bool_t sent = AM_FALSE;

	//assert(send && recv && rlen && (slen>=5));

	fd = smc_fd;
	if (fd < 0) {
		return AM_SMC_ERR_IO;
	}

	//printf("ssssssssssssssend length [%d] --[%d][%d]\n",slen,send[0],send[1]);
	dst  = recv;
	left = 4096;//*rlen;

	if (smc_write(fd, send, 5, NULL, 1000) != AM_SUCCESS) {
		return AM_SMC_ERR_TIMEOUT;
	}
	while (1) {
		if (smc_read(fd, &byte, 1, NULL, 1000) != AM_SUCCESS) {
			ret = AM_SMC_ERR_TIMEOUT;
			goto final;
		}
		if (byte == 0x60) {
			continue;
		} else if (((byte & 0xF0) == 0x60) || ((byte & 0xF0) == 0x90)) {
			if (left < 2) {
				printf("1 receive buffer must >= 2\n");
				ret = AM_SMC_ERR_BUF_TOO_SMALL;
				goto final;
			}
			dst[0] = byte;
			if (smc_read(fd, &dst[1], 1, NULL, 1000) != AM_SUCCESS) {
				ret = AM_SMC_ERR_TIMEOUT;
				goto final;

			}
			dst += 2;
			left -= 2;
			break;
		} else if (byte == send[1]) {
			if (!sent) {
				int cnt = slen - 5;

				if (cnt) {
					if (smc_write(fd, send + 5, cnt, NULL, 5000) != AM_SUCCESS) {
						ret = AM_SMC_ERR_TIMEOUT;
						goto final;
					}
				} else {
					cnt = send[4];
					if (!cnt) {
						cnt = 256;
					}

					if (left < cnt + 2) {
						printf("2 receive buffer must >= %d", cnt + 2);
						ret = AM_SMC_ERR_BUF_TOO_SMALL;
						goto final;
					}

					if (smc_read(fd, dst, cnt, &act_len/*NULL*/, 5000) != AM_SUCCESS) {
						ret = AM_SMC_ERR_TIMEOUT;
						goto final;
					}
					dst  += act_len;//cnt;
					left -= act_len;//cnt;
				}

				sent = AM_TRUE;
			} else {
				ret = AM_SMC_ERR_IO;
				break;
			}
		} else {
			ret = AM_SMC_ERR_IO;
			break;
		}
	}
final:

	*rlen = dst - recv;

	return ret;
}
#if 0
unsigned char data1[] = {0x82, 0x70, 0x65, 0x90, 0x00, 0x98, 0x96, 0xa1, 0x00, 0x00, 0x0e, 0x02, 0x03, 0x2a, 0x00, 0x00,
                         0x00, 0x01, 0x00, 0x03, 0x10, 0x9e, 0x8b, 0x61, 0x10, 0x9d, 0x8b, 0x61, 0xff, 0x00, 0x00, 0x00,
                         0x00, 0x00, 0x00, 0x00, 0x1c, 0x89, 0x00, 0x00, 0x0f, 0xe9, 0x00, 0x00, 0x12, 0x0b, 0x90, 0x00,
                         0xbb, 0x0f, 0xb4, 0x46, 0x9c, 0xc7, 0x74, 0xbf, 0x03, 0x2a, 0x00, 0x00, 0x00, 0x01, 0x00, 0x03,
                         0x10, 0x9e, 0x8b, 0x61, 0x10, 0x9d, 0x8b, 0x61, 0xff, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00,
                         0x1c, 0x89, 0x00, 0x00, 0x0f, 0xe9, 0x00, 0x00, 0x12, 0x0b, 0x90, 0x00, 0x29, 0x4a, 0x30, 0xce,
                         0xbb, 0x46, 0xd6, 0xd0, 0x25, 0x62, 0xb9, 0x60
                        };

int smc_test(void)
{
	AM_SMC_OpenPara_t para;
	uint8_t atr[AM_SMC_MAX_ATR_LEN];
	int i, len, ds;
	AM_SMC_CardStatus_t status;
	uint8_t sbuf[5] = {0x80, 0x44, 0x00, 0x00, 0x08};
	uint8_t rbuf[256];
	int rlen = sizeof(rbuf);
	char name[PATH_MAX];
	char data[1024];
	int fd, ret;
	FILE *fp1, *fp2, *fp3;
	char pbyBuffer[2048];
	int pdwBufferLen;

#if 0
	char tm[128 * 1024];

	memset(tm, 0, 128 * 1024);
	fp3 = fopen("./block01", "w");
	fwrite(tm, 1, 128 * 1024, fp3);
	fclose(fp3);
#endif

	if (CDCASTB_Init(0)) {
		printf("DRM Init successful!!!!!!\n");
	} else {
		printf("DRM Init failure!!!!!!!!!!\n");
	}
	sleep(5);
	//printf("2222222222222222222\n");
	if (CDCASTB_SCInsert()) {
		printf("CARD inserted!!!!!!!!!\n");
	} else {
		printf("CARD out!!!!!!!!!!!!\n");
	}
	CDCASTB_SetEmmPid(0x64);
	sleep(2);
	return 0;
	printf("11111111111111111111\n");
	if ((fp1 = fopen("test.ts", "r")) == NULL)
		//if ((fp1 = fopen("content1.txt","r")) == NULL)
	{
		printf("open content1.txt error\n");
	}
	if ((fp2 = fopen("1.drm", "r")) == NULL)
		//if ((fp2 = fopen("product1.drm","r")) == NULL)
	{
		printf("open product1.drm error\n");
	}
	if ((fp3 = fopen("result.txt", "wt")) == NULL) {
		printf("open result.txt error\n");
	}
	//ret = CDCASTB_DRM_OpenFile("content1.txt","product1.drm");
	//AM_SMC_readwrite(sbuf,5,rbuf,&rlen);
	//printf("$$$$$$$$$$$$$$$$$$$$$[%d\n",rlen);

	//CDCASTB_PrivateDataGot(0x81,0, 0x10, data1, 104);
	sleep(5);
	printf("opening file.....\n");
	ret = CDCASTB_DRM_OpenFile((const void*)&fp1, (const void*)fp2);
	printf("open the two file [%d]\n", ret);
	pdwBufferLen = 2048;
	ret = CDCASTB_DRM_ReadFile((const void*)&fp1, pbyBuffer, &pdwBufferLen);
	printf("read file [%d][%d]\n", ret, pdwBufferLen);
	fwrite(pbyBuffer, 1, pdwBufferLen, fp3);
	fclose(fp3);
	fclose(fp2);
	fclose(fp1);


	while (1);

	memset(&para, 0, sizeof(para));
	//para.enable_thread = !sync;
	//AM_TRY(AM_SMC_Open(SMC_DEV_NO, &para));

	snprintf(name, sizeof(name), "/dev/smc0");
	fd = open(name, O_RDWR);
	if (fd < 0) {
		printf("ooooooooopen smc0 error\n");
	}

	printf("please insert a card\n");
	do {
		//AM_TRY(AM_SMC_GetCardStatus(SMC_DEV_NO, &status));
		if (ioctl(fd, AMSMC_IOC_GET_STATUS, &ds)) {
			printf("get card status failed\n");
			return -1;
		}

		status = ds ? AM_SMC_CARD_IN : AM_SMC_CARD_OUT;
		usleep(100000);
	} while (status == AM_SMC_CARD_OUT);

	printf("card in\n");

	len = sizeof(atr);
	//AM_TRY(AM_SMC_Reset(SMC_DEV_NO, atr, &len));
	{
		struct am_smc_atr abuf;

		if (ioctl(fd, AMSMC_IOC_RESET, &abuf)) {
			printf("reset the card failed");
			return -1;
		}

		memcpy(atr, abuf.atr, abuf.atr_len);
		len = abuf.atr_len;

		printf("ATR: ");
		for (i = 0; i < len; i++) {
			printf("%02x ", atr[i]);
		}
		printf("\n");
	}
	//AM_TRY(AM_SMC_TransferT0(SMC_DEV_NO, sbuf, sizeof(sbuf), rbuf, &rlen));
	{
		struct pollfd pfd;

		pfd.fd = fd;
		pfd.events = POLLOUT;

		ret = poll(&pfd, 1, 1000);
		if (ret != 1) {
			printf("wwwwwwwrite timeout\n");
			return -1;
		}
		printf("beggggin wrrite  \n");
		ret = write(fd, sbuf, 5);
		if (ret < 0) {
			printf("card write error");
			return -1;
		}
		printf("write data == [%d]\n", ret);
		//	while(1)
		{
			pfd.fd = fd;
			pfd.events = POLLIN;

			ret = poll(&pfd, 1, 10000);
			if (ret != 1) {
				printf("read timeout !!!!!!!!\n");
				return -1;
			}
			printf("begin read  ....\n");
			ret = read(fd, data, 1);
			if (ret < 0) {
				printf("card read error");
				return -1;
			}

		}
	}
	printf("send: ");
	for (i = 0; i < sizeof(sbuf); i++) {
		printf("%02x ", sbuf[i]);
	}
	printf("\n");

	printf("recv: ");
	//	for(i=0; i<rlen; i++)
	{
		printf("%02x ", data[0]);
	}
	printf("\n");



	close(fd);

	return 0;
}

#endif
