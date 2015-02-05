/***************************************************************************
 *  Copyright C 2009 by Amlogic, Inc. All Rights Reserved.
 */
/**\file
 * \brief DVB前端设备驱动
 *
 * \author Gong Ke <ke.gong@amlogic.com>
 * \date 2010-06-07: create the document
 ***************************************************************************/

#ifndef _AM_FEND_H
#define _AM_FEND_H

#include "am_types.h"
#include "am_evt.h"
#include "am_dmx.h"
#include <linux/dvb/frontend.h>

#ifdef __cplusplus
extern "C"
{
#endif

/****************************************************************************
 * Macro definitions
 ***************************************************************************/

/****************************************************************************
 * Error code definitions
 ****************************************************************************/

/**\brief DVB前端模块错误代码*/
enum AM_FEND_ErrorCode
{
	AM_FEND_ERROR_BASE=AM_ERROR_BASE(AM_MOD_FEND),
	AM_FEND_ERR_NO_MEM,                   /**< 内存不足*/
	AM_FEND_ERR_BUSY,                     /**< 设备已经打开*/
	AM_FEND_ERR_INVALID_DEV_NO,           /**< 无效的设备号*/
	AM_FEND_ERR_NOT_OPENNED,              /**< 设备还没有打开*/
	AM_FEND_ERR_CANNOT_CREATE_THREAD,     /**< 无法创建线程*/
	AM_FEND_ERR_NOT_SUPPORTED,            /**< 设备不支持此功能*/
	AM_FEND_ERR_CANNOT_OPEN,              /**< 无法打开设备*/
	AM_FEND_ERR_TIMEOUT,                  /**< 操作超时*/
	AM_FEND_ERR_INVOKE_IN_CB,             /**< 操作不能在回调函数中调用*/
	AM_FEND_ERR_IO,                       /**< 输入输出错误*/ 
	AM_FEND_ERR_BLINDSCAN, 								/**< 盲扫错误*/
	AM_FEND_ERR_BLINDSCAN_INRUNNING, 			/**< 盲扫运行中*/
	AM_FEND_ERR_END
};

/****************************************************************************
 * Event type definitions
 ****************************************************************************/

/**\brief DVB前端模块事件类型*/
enum AM_FEND_EventType
{
	AM_FEND_EVT_BASE=AM_EVT_TYPE_BASE(AM_MOD_FEND),
	AM_FEND_EVT_STATUS_CHANGED,    /**< 前端状态发生改变，参数为struct dvb_frontend_event*/
	AM_FEND_EVT_ROTOR_MOVING,    /**< Rotor移动*/
	AM_FEND_EVT_ROTOR_STOP,    /**< Rotor停止*/
	AM_FEND_EVT_SHORT_CIRCUIT, /**< Frontend短路*/ 
	AM_FEND_EVT_SHORT_CIRCUIT_REPAIR, /**< Frontend短路修复*/	
	AM_FEND_EVT_END
};

/****************************************************************************
 * Type definitions
 ***************************************************************************/

/**\brief 前端设备开启参数*/
typedef struct
{
	int mode; /**< 解调模式*/
} AM_FEND_OpenPara_t;

/**\brief DVB前端监控回调函数*/
typedef void (*AM_FEND_Callback_t) (int dev_no, struct dvb_frontend_event *evt, void *user_data);

/**\brief 卫星盲扫状态*/
typedef enum
{
	AM_FEND_BLIND_START,			/**< 卫星盲扫开始*/
	AM_FEND_BLIND_UPDATEPROCESS,	/**< 卫星盲扫更新进度*/
	AM_FEND_BLIND_UPDATETP			/**< 卫星盲扫更新频点信息*/
} AM_FEND_BlindStatus_t;

/**\brief 卫星盲扫事件*/
typedef struct
{
	AM_FEND_BlindStatus_t    status; /**< 卫星盲扫状态*/
	union
	{
		unsigned int freq;
		unsigned int process;
	};
} AM_FEND_BlindEvent_t;

/**\brief 卫星盲扫监控回调函数*/
typedef void (*AM_FEND_BlindCallback_t) (int dev_no, AM_FEND_BlindEvent_t *evt, void *user_data);


/****************************************************************************
 * Function prototypes  
 ***************************************************************************/

/**\brief 打开一个DVB前端设备
 * \param dev_no 前端设备号
 * \param[in] para 设备开启参数
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_fend.h)
 */
extern AM_ErrorCode_t AM_FEND_Open(int dev_no, const AM_FEND_OpenPara_t *para);

/**\brief 关闭一个DVB前端设备
 * \param dev_no 前端设备号
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_fend.h)
 */
extern AM_ErrorCode_t AM_FEND_Close(int dev_no);

/**\brief 设定前端解调模式
 * \param dev_no 前端设备号
 * \param mode 解调模式
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_fend.h)
 */
extern AM_ErrorCode_t AM_FEND_SetMode(int dev_no, int mode);

/**\brief 取得一个DVB前端设备的相关信息
 * \param dev_no 前端设备号
 * \param[out] info 返回前端信息数据
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_fend.h)
 */
extern AM_ErrorCode_t AM_FEND_GetInfo(int dev_no, struct dvb_frontend_info *info);

/**\brief 取得一个DVB前端设备连接的TS输入源
 * \param dev_no 前端设备号
 * \param[out] src 返回设备对应的TS输入源
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_fend.h)
 */
extern AM_ErrorCode_t AM_FEND_GetTSSource(int dev_no, AM_DMX_Source_t *src);

/**\brief 设定前端参数
 * \param dev_no 前端设备号
 * \param[in] para 前端设置参数
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_fend.h)
 */
extern AM_ErrorCode_t AM_FEND_SetPara(int dev_no, const struct dvb_frontend_parameters *para);
extern AM_ErrorCode_t AM_FEND_SetProp(int dev_no, const struct dtv_properties *prop);

/**\brief 取得当前端设备设定的参数
 * \param dev_no 前端设备号
 * \param[out] para 前端设置参数
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_fend.h)
 */
extern AM_ErrorCode_t AM_FEND_GetPara(int dev_no, struct dvb_frontend_parameters *para);
extern AM_ErrorCode_t AM_FEND_GetProp(int dev_no, struct dtv_properties *prop);

/**\brief 取得前端设备当前的锁定状态
 * \param dev_no 前端设备号
 * \param[out] status 返回前端设备的锁定状态
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_fend.h)
 */
extern AM_ErrorCode_t AM_FEND_GetStatus(int dev_no, fe_status_t *status);

/**\brief 取得前端设备当前的SNR值
 * \param dev_no 前端设备号
 * \param[out] snr 返回SNR值
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_fend.h)
 */
extern AM_ErrorCode_t AM_FEND_GetSNR(int dev_no, int *snr);

/**\brief 取得前端设备当前的BER值
 * \param dev_no 前端设备号
 * \param[out] ber 返回BER值
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_fend.h)
 */
extern AM_ErrorCode_t AM_FEND_GetBER(int dev_no, int *ber);

/**\brief 取得前端设备当前的信号强度值
 * \param dev_no 前端设备号
 * \param[out] strength 返回信号强度值
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_fend.h)
 */
extern AM_ErrorCode_t AM_FEND_GetStrength(int dev_no, int *strength);

/**\brief 取得当前注册的前端状态监控回调函数
 * \param dev_no 前端设备号
 * \param[out] cb 返回注册的状态回调函数
 * \param[out] user_data 返回状态回调函数的参数
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_fend.h)
 */
extern AM_ErrorCode_t AM_FEND_GetCallback(int dev_no, AM_FEND_Callback_t *cb, void **user_data);

/**\brief 注册前端设备状态监控回调函数
 * \param dev_no 前端设备号
 * \param[in] cb 状态回调函数
 * \param[in] user_data 状态回调函数的参数
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_fend.h)
 */
extern AM_ErrorCode_t AM_FEND_SetCallback(int dev_no, AM_FEND_Callback_t cb, void *user_data);

/**\brief 设置前端设备状态监控回调函数活动状态
 * \param dev_no 前端设备号
 * \param[in] enable_cb 允许或者禁止状态回调函数
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_fend.h)
 */
extern AM_ErrorCode_t AM_FEND_SetActionCallback(int dev_no, AM_Bool_t enable_cb);

/**\brief 设定前端设备参数，并等待参数设定完成
 * \param dev_no 前端设备号
 * \param[in] para 前端设置参数
 * \param[out] status 返回前端设备状态
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_fend.h)
 */
extern AM_ErrorCode_t AM_FEND_Lock(int dev_no, const struct dvb_frontend_parameters *para, fe_status_t *status);

/**\brief 设定前端管理线程的检测间隔
 * \param dev_no 前端设备号
 * \param delay 间隔时间(单位为毫秒)，0表示没有间隔，<0表示前端管理线程暂停工作
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_fend.h)
 */
extern AM_ErrorCode_t AM_FEND_SetThreadDelay(int dev_no, int delay);

/**\brief 重置数字卫星设备控制
 * \param dev_no 前端设备号
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_fend.h)
 */
extern AM_ErrorCode_t AM_FEND_DiseqcResetOverload(int dev_no); 

/**\brief 发送数字卫星设备控制命令
 * \param dev_no 前端设备号 
 * \param[in] cmd 数字卫星设备控制命令
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_fend.h)
 */
extern AM_ErrorCode_t AM_FEND_DiseqcSendMasterCmd(int dev_no, struct dvb_diseqc_master_cmd* cmd); 

/**\brief 接收数字卫星设备控制2.0命令回应
 * \param dev_no 前端设备号 
 * \param[out] reply 数字卫星设备控制回应
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_fend.h)
 */
extern AM_ErrorCode_t AM_FEND_DiseqcRecvSlaveReply(int dev_no, struct dvb_diseqc_slave_reply* reply); 

/**\brief 发送数字卫星设备控制tone burst
 * \param dev_no 前端设备号 
 * \param tone burst控制方式
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_fend.h)
 */
extern AM_ErrorCode_t AM_FEND_DiseqcSendBurst(int dev_no, fe_sec_mini_cmd_t minicmd); 

/**\brief 设置卫星设备tone模式
 * \param dev_no 前端设备号 
 * \param tone 卫星设备tone模式
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_fend.h)
 */
extern AM_ErrorCode_t AM_FEND_SetTone(int dev_no, fe_sec_tone_mode_t tone); 

/**\brief 设置卫星设备控制电压
 * \param dev_no 前端设备号 
 * \param voltage 卫星设备控制电压 
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_fend.h)
 */
extern AM_ErrorCode_t AM_FEND_SetVoltage(int dev_no, fe_sec_voltage_t voltage); 

/**\brief 控制卫星设备LNB高电压
 * \param dev_no 前端设备号 
 * \param arg 0表示禁止，!=0表示允许
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_fend.h)
 */
extern AM_ErrorCode_t AM_FEND_EnableHighLnbVoltage(int dev_no, long arg);                          
                                                                           
/**\brief 转化信号强度dBm值为百分比(NorDig)
 * \param rf_power_dbm dBm值
 * \param constellation 调制模式
 * \param code_rate 码率
 * \return 百分比值
 */
extern int AM_FEND_CalcTerrPowerPercentNorDig(short rf_power_dbm, fe_modulation_t constellation, fe_code_rate_t code_rate);

/**\brief 转化C/N值为百分比(NorDig)
 * \param cn C/N值
 * \param constellation 调制模式
 * \param code_rate 码率
 * \param hierarchy 等级调制参数
 * \param isLP 低优先级模式
 * \return 百分比值
 */
extern int AM_FEND_CalcTerrCNPercentNorDig(float cn, int ber, fe_modulation_t constellation, fe_code_rate_t code_rate, fe_hierarchy_t hierarchy, int isLP);

/**\brief 卫星盲扫开始  
 * \param dev_no 前端设备号
 * \param[in] cb 盲扫回调函数
 * \param[in] user_data 状态回调函数的参数
 * \param start_freq 开始频点 unit HZ
 * \param stop_freq 结束频点 unit HZ
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_fend.h)
 */
extern AM_ErrorCode_t AM_FEND_BlindScan(int dev_no, AM_FEND_BlindCallback_t cb, void *user_data, unsigned int start_freq, unsigned int stop_freq);

/**\brief 卫星盲扫结束
 * \param dev_no 前端设备号
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_fend.h)
 */
extern AM_ErrorCode_t AM_FEND_BlindExit(int dev_no); 

/**\brief 卫星盲扫进度 
 * \param dev_no 前端设备号
 * \param[out] process 盲扫进度0-100
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_fend.h)
 */
extern AM_ErrorCode_t AM_FEND_BlindGetProcess(int dev_no, unsigned int *process); 

/**\brief 卫星盲扫信息 
 * \param dev_no 前端设备号
 * \param[out] para 盲扫频点信息缓存区
 * \param[in out] para in 盲扫频点信息缓存区大小，out 盲扫频点个数
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_fend.h)
 */
extern AM_ErrorCode_t AM_FEND_BlindGetTPInfo(int dev_no, struct dvb_frontend_parameters *para, unsigned int *count);  

/**\brief 模拟微调
 *\param dev_no 前端设备号
 *\param freq 频率，单位为Hz
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_fend.h)
 */
extern AM_ErrorCode_t AM_FEND_FineTune(int dev_no, unsigned int freq);

/**\brief 模拟CVBS AMP OUT
 *\param dev_no 前端设备号
 *\param amp ，单位为int
 * \return
 *   - AM_SUCCESS 成功
 *   - 其他值 错误代码(见am_fend.h)
 */
extern AM_ErrorCode_t AM_FEND_SetCvbsAmpOut(int dev_no, unsigned int amp);

#ifdef __cplusplus
}
#endif

#endif

