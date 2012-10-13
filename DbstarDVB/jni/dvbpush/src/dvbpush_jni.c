#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <fcntl.h>
#include <android/log.h>
#include <jni.h>
#include <pthread.h>

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

#define NOTIFY_METHOD "postNotifyMessage"
#define NOTIFY_METHOD_DEC "(I[B)V"
static JavaVM* g_java_vm = NULL;
static jclass g_server_clz = NULL;
static jmethodID g_notify_mid = NULL;


/* test code. */
#define NOTIFY_TYPE_UPGRADE 1
#define NOTIFY_MSG_UPGRADE "Upgrade Right Now!"

static pthread_t s_task_tid;
static int s_task_running = 0;
//typedef int (* dvbpush_notify_t)(int type, char *msg, int len);
//static dvbpush_notify_t dvbpush_notify;

//static void *task_run()
//{
//	int delay = 1000;
//	int looper = 1000;
//	static int cnt = 0;
//	int type = NOTIFY_TYPE_UPGRADE;
//	char *msg = NOTIFY_MSG_UPGRADE;
//
//	LOGD("task_run ...\n");
//	while(s_task_running) {
//		usleep(delay);
//		cnt++;
//		if (cnt >= looper) {
//			cnt = 0;
//			if (dvbpush_notify != NULL) {
//				dvbpush_notify(type, msg, strlen(msg));
//			}
//		}
//	}
//	LOGD("task_run exit!\n");
//
//	return NULL;
//}

//int dvbpush_task_run()
//{
//	LOGD("dvbpush_task_run()\n");
//	s_task_running = 1;
//	pthread_create(&s_task_tid, NULL, task_run, NULL);
//
//	return 0;
//}

int dvbpush_task_stop()
{
	LOGD("dvbpush_task_stop()\n");
	s_task_running = 0;

	return 0;
}

/**
 * cmd: 1 start taskinfo
 * cmd: 2 stop taskinfo
 * cmd: 3 get taskinfo
 */
static char *task_info = "1001\ttask_1\t19889\t200000\n" \
	"1002\ttask_2\t299999\t4000000\n" \
	"1003\ttask_3\t299999\t5000000\n";
/**
 * need implement this func in dvbpush module.
 */
int dvbpush_command(int cmd, char **buf, int *len)
{
	int ret = 0;

	LOGI("dvbpush_command()\n");
//	switch (cmd) {
//	case 1:
//		LOGD("cmd:1, start taskinfo.\n");
//		dvbpush_getinfo_start();
//		break;
//	case 2:
//		LOGD("cmd:2, stop taskinfo.\n");
//		dvbpush_getinfo_stop();
//		break;
//	case 3:
//		LOGD("cmd:3, get taskinfo.\n");
//		dvbpush_getinfo(buf, len);
//		LOGD("addr of buf: %p, len=%d\n", *buf, *len);
//		break;
//	default:
//		break;
//	}

	return ret;
}

/**
 * need implement this func in dvbpush module.
 */
//int dvbpush_register_notify(void *func)
//{
//	LOGD("dvbpush_register_notify\n");
//	if (func != NULL)
//		dvbpush_notify = (dvbpush_notify_t)func;
//
//	return 0;
//}

int dvbpush_notify_cb(int type, char *msg, int len)
{
	int ret = 0;
	int is_attached = -1;
	JNIEnv *env = NULL;
	int length = 0;
	char *buffer = NULL;
	jbyteArray bytes = NULL;

	LOGI("dvbpush_notify_cb()\n");
	ret = (*g_java_vm)->GetEnv(g_java_vm, (void**)&env, JNI_VERSION_1_6);
	if (ret < 0) {
		ret = (*g_java_vm)->AttachCurrentThread(g_java_vm, &env, NULL);
		if (ret < 0) {
			LOGE("callback handler:failed to attach current thread\n");
			return -2;
		}
		is_attached = 1;
	}

	if (g_server_clz != NULL && g_notify_mid != NULL) {
		if ((msg != NULL) && (len > 0)) {
			buffer = msg;
			length = len;
			bytes = (*env)->NewByteArray(env, length);
			(*env)->SetByteArrayRegion(env, bytes, 0, length, (jbyte *)buffer);
		}
		(*env)->CallStaticVoidMethod(env, g_server_clz, g_notify_mid, type, bytes);
		//LOGD("%p[len=%d]", buffer, length);
	}

	if (is_attached > 0) {
		(*g_java_vm)->DetachCurrentThread(g_java_vm);
	}

	return ret;
}

/*
 * Class:     com_dbstar_DbstarDVB_DbstarService
 * Method:    command
 * Signature: (ILjava/lang/String;I)[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_dbstar_DbstarDVB_DbstarService_command
  (JNIEnv *env, jobject obj, jint cmd, jstring buf, jint len)
{
	int ret = 0;
	int length = len;
	char *buffer = NULL;
	jbyteArray bytes = NULL;

	LOGD("command(cmd=%d, buf=%p, len=%d)\n", cmd, buf, len);
	if (NULL != buf)
		buffer = (*env)->GetStringUTFChars(env, buf, NULL);

	ret = dvbpush_command(cmd, &buffer, &length);
	if ((ret == 0) && (length > 0)) {
		bytes = (*env)->NewByteArray(env, length);
		(*env)->SetByteArrayRegion(env, bytes, 0, length, (jbyte *)buffer);
	} else {
		bytes = NULL;
	}

	if (NULL != buf)
		(*env)->ReleaseStringUTFChars(env, buf, buffer);

	return bytes;
}

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

	jclass clz = (*env)->GetObjectClass(env, obj);
	g_server_clz = (*env)->NewGlobalRef(env, clz);
	if (NULL == g_server_clz) {
		LOGI("get server class Failed");
		return -1;
	}
	g_notify_mid = (*env)->GetStaticMethodID(env, g_server_clz, NOTIFY_METHOD, NOTIFY_METHOD_DEC);
	if (NULL == g_notify_mid) {
		LOGI("get notify class Failed");
		return -2;
	}

	ret = dvbpush_init();
	LOGI("set notify callback function\n");
	ret = dvbpush_register_notify((void *)&dvbpush_notify_cb);
	//ret = dvbpush_task_run();

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
	ret = dvbpush_task_stop();
	ret = dvbpush_uninit();

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

void JNI_OnUnLoad(JavaVM* vm, void* reserved)
{
	JNIEnv* env = NULL;

	if ((*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_6) != JNI_OK) {
		LOGE("OnUnLoad(), get env filed\n");
		return;
	}
	LOGI("OnUnLoad() OK\n");

	if (g_server_clz != NULL) {
		(*env)->DeleteLocalRef(env, g_server_clz);
	}
	return;
}
