#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <fcntl.h>
#include <android/log.h>
#include <jni.h>

#include "dvbpush_api.h"

#define JNI_LOG_ENABLE 1
#if JNI_LOG_ENABLE
#define LOG_TAG "Dbstar_JNI"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#else
#define  LOGI(...)
#define  LOGD(...)
#define  LOGE(...)
#endif

static JavaVM* g_java_vm = NULL;

/*
 * Class:     com_dbstar_DbstarDVB_DbstarService
 * Method:    dvbpushStart
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_dbstar_DbstarDVB_DbstarService_dvbpushStart
  (JNIEnv *env, jobject obj)
{
	int ret = 0;

	LOGI("dvbpushStart()\n");
	ret = dvbpush_start();

	return ret;
}

/*
 * Class:     com_dbstar_DbstarDVB_DbstarService
 * Method:    dvbpushStop
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_dbstar_DbstarDVB_DbstarService_dvbpushStop
  (JNIEnv *env, jobject obj)
{
	int ret = 0;

	LOGI("dvbpushStop()\n");
	ret = dvbpush_stop();

	return ret;
}

/*
 * Class:     com_dbstar_DbstarDVB_DbstarService
 * Method:    taskinfoStart
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_dbstar_DbstarDVB_DbstarService_taskinfoStart
  (JNIEnv *env, jobject obj)
{
	int ret = 0;

	LOGI("taskinfoStart()\n");
	dvbpush_getinfo_start();


	return ret;
}

/*
 * Class:     com_dbstar_DbstarDVB_DbstarService
 * Method:    taskinfoStop
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_dbstar_DbstarDVB_DbstarService_taskinfoStop
  (JNIEnv *env, jobject obj)
{
	int ret = 0;

	LOGI("taskinfoStop()\n");
	dvbpush_getinfo_stop();

	return ret;
}

/*
 * Class:     com_dbstar_DbstarDVB_DbstarService
 * Method:    taskinfoGet
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_dbstar_DbstarDVB_DbstarService_taskinfoGet
  (JNIEnv *env, jobject obj)
{
	int ret = 0;
	char *buf = NULL;
	unsigned int len = 0;

	LOGI("taskinfoGet()\n");
	ret = dvbpush_getinfo(&buf, &len);
	LOGD("buf[%d][%s]\n", len, buf);
	if (len > 0) {
		jbyteArray bytes = (*env)->NewByteArray(env, len);
		(*env)->SetByteArrayRegion(env, bytes, 0, len, (jbyte *)buf);
		return bytes; 
	} else {
		return NULL;
	}
}

JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
	jint ret = -1;
	JNIEnv* env = NULL;

	if ((*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_6) != JNI_OK) {
		LOGE("OnLoad(), get env filed\n");
		return -1;
	}
	LOGI("OnLoad() OK\n");
	g_java_vm = vm;
	ret = JNI_VERSION_1_6;

	return ret;
}
