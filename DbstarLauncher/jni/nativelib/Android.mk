LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := libnativeutils
LOCAL_ARM_MODE := arm
LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := utils_jni.c
LOCAL_C_INCLUDES := $(JNI_H_INCLUDE) 

LOCAL_SHARED_LIBRARIES += liblog libcutils

LOCAL_PROGUARD_ENABLED := disabled
LOCAL_PRELINK_MODULE := false

include $(BUILD_SHARED_LIBRARY)
