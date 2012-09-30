/***************************************************************************
 *  Copyright C 2009 by Amlogic, Inc. All Rights Reserved.
 */
/**\file
 * \brief 智能卡通讯模块
 *
 * \author Gong Ke <ke.gong@amlogic.com>
 * \date 2010-06-29: create the document
 ***************************************************************************/

#ifndef _AM_SMC_H
#define _AM_SMC_H

#include "am_types.h"
#include "am_evt.h"

#ifdef __cplusplus
extern "C"
{
#endif

/****************************************************************************
 * Macro definitions
 ***************************************************************************/

#define AM_SMC_MAX_ATR_LEN    (33)

/****************************************************************************
 * Error code definitions
 ****************************************************************************/

/**\brief 智能卡模块错误代码*/
enum AM_SMC_ErrorCode
{
	AM_SMC_ERROR_BASE=AM_ERROR_BASE(AM_MOD_SMC),
	AM_SMC_ERR_INVALID_DEV_NO,           /**< 无效的设备号*/
	AM_SMC_ERR_BUSY,                     /**< 设备已经打开*/
	AM_SMC_ERR_NOT_OPENNED,              /**< 设备还没有打开*/
	AM_SMC_ERR_CANNOT_OPEN_DEV,          /**< 打开设备失败*/
	AM_SMC_ERR_CANNOT_CREATE_THREAD,     /**< 创建设备失败*/
	AM_SMC_ERR_TIMEOUT,                  /**< 超时*/
	AM_SMC_ERR_NOT_SUPPORTED,            /**< 设备不支持此功能*/
	AM_SMC_ERR_IO,                       /**< 设备输入输出错误*/
	AM_SMC_ERR_BUF_TOO_SMALL,            /**< 缓冲区太小*/
	AM_SMC_ERR_NO_CARD,                  /**< 智能卡没有插入*/
	AM_SMC_ERR_END
};

/****************************************************************************
 * Event type definitions
 ****************************************************************************/

/**\brief 智能卡模块事件类型*/
enum AM_SMC_EventType
{
	AM_SMC_EVT_BASE=AM_EVT_TYPE_BASE(AM_MOD_SMC),
	AM_SMC_EVT_CARD_IN,                  /**< 智能卡插入*/
	AM_SMC_EVT_CARD_OUT,                 /**< 智能卡拔出*/
	AM_SMC_EVT_END
};


/****************************************************************************
 * Type definitions
 ***************************************************************************/

/**\brief 智能卡设备开启参数*/
typedef struct
{
	int  enable_thread;                  /**< 创建智能卡状态检测线程*/
} AM_SMC_OpenPara_t;

/**\brief 智能卡插入状态*/
typedef enum
{
	AM_SMC_CARD_OUT, /**< 智能卡没有插入*/
	AM_SMC_CARD_IN   /**< 智能卡已经插入*/
} AM_SMC_CardStatus_t;

/**\brief 智能卡状态回调*/
typedef void (*AM_SMC_StatusCb_t) (int dev_no, AM_SMC_CardStatus_t status, void *data);

/** brief 智能卡参数*/
typedef struct
{
	int     f;                 /**<时钟频率转换系数*/
	int     d;                 /**<波特率系数*/
	int     n;                 /**<*/
	int     bwi;               /**<*/
	int     cwi;               /**<*/
	int     bgt;               /**<*/
	int     freq;              /**<时钟频率*/
	int     recv_invert;       /**<*/
	int     recv_lsb_msb;      /**<*/
	int     recv_no_parity;    /**<*/
	int     xmit_invert;       /**<*/
	int     xmit_lsb_msb;      /**<*/
	int     xmit_retries;      /**<*/
	int     xmit_repeat_dis;   /**<*/
}AM_SMC_Param_t;

/****************************************************************************
 * Function prototypes  
 ***************************************************************************/

/**\brief 打开智能卡设备
 * \param dev_no 智能卡设备号
 * \param[in] para 智能卡设备开启参数
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_smc.h)
 */
extern AM_ErrorCode_t AM_SMC_Open(int dev_no, const AM_SMC_OpenPara_t *para);

/**\brief 关闭智能卡设备
 * \param dev_no 智能卡设备号
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_smc.h)
 */
extern AM_ErrorCode_t AM_SMC_Close(int dev_no);

/**\brief 得到当前的智能卡插入状态
 * \param dev_no 智能卡设备号
 * \param[out] status 返回智能卡插入状态
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_smc.h)
 */
extern AM_ErrorCode_t AM_SMC_GetCardStatus(int dev_no, AM_SMC_CardStatus_t *status);

/**\brief 复位智能卡
 * \param dev_no 智能卡设备号
 * \param[out] atr 返回智能卡的ATR数据
 * \param[in,out] len 输入ATR缓冲区大小，返回实际的ATR长度
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_smc.h)
 */
extern AM_ErrorCode_t AM_SMC_Reset(int dev_no, uint8_t *atr, int *len);

/**\brief 从智能卡读取数据
 *直接从智能卡读取数据，调用函数的线程会阻塞，直到读取到期望数目的数据，或到达超时时间。
 * \param dev_no 智能卡设备号
 * \param[out] data 数据缓冲区
 * \param[in] len 希望读取的数据长度
 * \param timeout 读取超时时间，以毫秒为单位，<0表示永久等待。
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_smc.h)
 */
extern AM_ErrorCode_t AM_SMC_Read(int dev_no, uint8_t *data, int len, int timeout);

/**\brief 向智能卡发送数据
 *直接向智能卡发送数据，调用函数的线程会阻塞，直到全部数据被写入，或到达超时时间。
 * \param dev_no 智能卡设备号
 * \param[in] data 数据缓冲区
 * \param[in] len 希望发送的数据长度
 * \param timeout 读取超时时间，以毫秒为单位，<0表示永久等待。
 * \return
 *  - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_smc.h)
 */
extern AM_ErrorCode_t AM_SMC_Write(int dev_no, const uint8_t *data, int len, int timeout);

/**\brief 从智能卡读取数据
 *直接从智能卡读取数据，调用函数的线程会阻塞，直到读取到期望数目的数据，或到达超时时间。
 * \param dev_no 智能卡设备号
 * \param[out] data 数据缓冲区
 * \param[in] len 希望读取的数据长度
 * \param timeout 读取超时时间，以毫秒为单位，<0表示永久等待。
 * \return
 *   - >=0 实际读取的数据长度
 *   - 其他值 错误代码(见am_smc.h)
 */
extern AM_ErrorCode_t AM_SMC_ReadEx(int dev_no, uint8_t *data, int len, int timeout);


/**\brief 向智能卡发送数据
 *直接向智能卡发送数据，调用函数的线程会阻塞，直到全部数据被写入，或到达超时时间。
 * \param dev_no 智能卡设备号
 * \param[in] data 数据缓冲区
 * \param[in] len 希望发送的数据长度
 * \param timeout 读取超时时间，以毫秒为单位，<0表示永久等待。
 * \return
 *   - >=0 实际写入的数据长度
 *   - 其他值 错误代码(见am_smc.h)
 */
extern AM_ErrorCode_t AM_SMC_WriteEx(int dev_no, const uint8_t *data, int len, int timeout);

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
extern AM_ErrorCode_t AM_SMC_TransferT0(int dev_no, const uint8_t *send, int slen, uint8_t *recv, int *rlen);

/**\brief 取得当前的智能卡状态回调函数
 * \param dev_no 智能卡设备号
 * \param[out] cb 返回回调函数指针
 * \param[out] data 返回用户数据
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_smc.h)
 */
extern AM_ErrorCode_t AM_SMC_GetCallback(int dev_no, AM_SMC_StatusCb_t *cb, void **data);

/**\brief 设定智能卡状态回调函数
 * \param dev_no 智能卡设备号
 * \param[in] cb 回调函数指针
 * \param[in] data 用户数据
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_smc.h)
 */
extern AM_ErrorCode_t AM_SMC_SetCallback(int dev_no, AM_SMC_StatusCb_t cb, void *data);

/**\brief 激活智能卡设备
 * \param dev_no 智能卡设备号
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_smc.h)
 */
extern AM_ErrorCode_t AM_SMC_Active(int dev_no);

/**\brief 取消激活智能卡设备
 * \param dev_no 智能卡设备号
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_smc.h)
 */
extern AM_ErrorCode_t AM_SMC_Deactive(int dev_no);

/**\brief 获取智能卡参数
 * \param dev_no 智能卡设备号
 * \param[out] para 返回智能卡参数
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_smc.h)
 */
extern AM_ErrorCode_t AM_SMC_GetParam(int dev_no, AM_SMC_Param_t *para);

/**\brief 设定智能卡参数
 * \param dev_no 智能卡设备号
 * \param[in] para 智能卡参数
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_smc.h)
 */
extern AM_ErrorCode_t AM_SMC_SetParam(int dev_no, const AM_SMC_Param_t *para);


#ifdef __cplusplus
}
#endif

#endif

