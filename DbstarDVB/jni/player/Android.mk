LOCAL_PATH := $(call my-dir)
LIBPLAYER_PATH := $(LOCAL_PATH)/../../../../amlogic/LibPlayer

include $(CLEAR_VARS)

ifneq ($(BOARD_VOUT_USES_FREESCALE),false)
LOCAL_CFLAGS += -DENABLE_FREE_SCALE
endif

LOCAL_MODULE := libplayerjni
LOCAL_ARM_MODE := arm
LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := player_jni.c \
				   sys_conf.c

LOCAL_C_INCLUDES := $(LIBPLAYER_PATH)/amplayer/player/include \
    $(LIBPLAYER_PATH)/amplayer/control/include \
    $(LIBPLAYER_PATH)/amcodec/include \
    $(LIBPLAYER_PATH)/amffmpeg \
    $(JNI_H_INCLUDE) 

LOCAL_STATIC_LIBRARIES := libamplayer libamcodec libavformat librtmp libavcodec libavutil libamadec  
LOCAL_SHARED_LIBRARIES += libutils libmedia libz libbinder libdl libcutils libc libamavutils libssl libcrypto libnativehelper
LOCAL_SHARED_LIBRARIES += libdrmvod

LOCAL_PROGUARD_ENABLED := disabled
LOCAL_PRELINK_MODULE := false

include $(BUILD_SHARED_LIBRARY)
