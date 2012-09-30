# Copyright (C) 2009 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
mylib := libdbstardrm.a
LOCAL_MODULE := $(mylib)
LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := lib/$(mylib)
include $(PREBUILD_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE := drmtest
LOCAL_MODULE_TAGS := optional
LOCAL_PRELINK_MODULE := false

LOCAL_SRC_FILES := \
	src/drmport.c \
	src/smcdrv.c \
	src/drmtest.c 

LOCAL_CFLAGS := -Wall
LOCAL_LDFLAGS += -L$(LOCAL_PATH)/lib -ldbstardrm

LOCAL_C_INCLUDES := $(LOCAL_PATH)/include/
LOCAL_SHARED_LIBRARIES += libc liblog
#LOCAL_STATIC_LIBRARIES += Y31-daotang-Amlogic8726-20120904D

include $(BUILD_EXECUTABLE)

include $(CLEAR_VARS)
LOCAL_MODULE := libdrmvod
LOCAL_MODULE_TAGS := optional
LOCAL_PRELINK_MODULE := false

LOCAL_SRC_FILES := \
	src/drmport.c \
	src/smcdrv.c \
	src/drmvod.c 

LOCAL_CFLAGS := -Wall
LOCAL_LDFLAGS += -L$(LOCAL_PATH)/lib -ldbstardrm

LIBPLAYER_PATH := $(LOCAL_PATH)/../../../../amlogic/LibPlayer
LOCAL_C_INCLUDES := $(LOCAL_PATH)/include \
	$(LIBPLAYER_PATH)/amplayer/player/include \
    $(LIBPLAYER_PATH)/amplayer/control/include \
    $(LIBPLAYER_PATH)/amcodec/include \
    $(LIBPLAYER_PATH)/amffmpeg \
    $(JNI_H_INCLUDE) 

LOCAL_STATIC_LIBRARIES := libamplayer libamcodec libavformat librtmp libavcodec libavutil libamadec  
LOCAL_SHARED_LIBRARIES += libutils libmedia libz libbinder libdl libcutils libc libamavutils libssl libcrypto libnativehelper

LOCAL_PROGUARD_ENABLED := disabled
LOCAL_PRELINK_MODULE := false



include $(BUILD_SHARED_LIBRARY)
