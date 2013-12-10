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

#include $(CLEAR_VARS)
#mylib := libpush.a
#LOCAL_MODULE := $(mylib)
#LOCAL_MODULE_TAGS := optional
#LOCAL_SRC_FILES := lib/libpush.a
#include $(PREBUILD_STATIC_LIBRARY)

#include $(CLEAR_VARS)
#mylib := libfileapi
#LOCAL_MODULE := $(mylib)
#LOCAL_MODULE_TAGS := optional
#LOCAL_MODULE_CLASS := SHARED_LIBRARIES
#LOCAL_MODULE_SUFFIX := .so
#LOCAL_MODULE_PATH := $(TARGET_OUT_SHARED_LIBRARIES)
#LOCAL_SRC_FILES := lib/$(mylib)$(LOCAL_MODULE_SUFFIX)
#OVERRIDE_BUILT_MODULE_PATH := $(TARGET_OUT_INTERMEDIATE_LIBRARIES)
#include $(BUILD_PREBUILT)

#//=======
include $(CLEAR_VARS)
LOCAL_MODULE := libfileapi
LOCAL_ARM_MODE := arm
LOCAL_MODULE_TAGS := optional
LOCAL_PRELINK_MODULE := false
LOCAL_SRC_FILES += \
        src/fileapi.cpp

LOCAL_CFLAGS += -W -Wall
#LOCAL_LDFLAGS += -L$(LOCAL_PATH)/lib
LOCAL_C_INCLUDES += $(LOCAL_PATH)/include
LOCAL_SHARED_LIBRARIES += libc libdl liblog libfileapic
include $(BUILD_SHARED_LIBRARY)
#//======


#include $(CLEAR_VARS)
#mylib := libfileapic
#LOCAL_MODULE := $(mylib)
#LOCAL_MODULE_TAGS := optional
#LOCAL_MODULE_CLASS := SHARED_LIBRARIES
#LOCAL_MODULE_SUFFIX := .so
#LOCAL_MODULE_PATH := $(TARGET_OUT_SHARED_LIBRARIES)
#LOCAL_SRC_FILES := lib/$(mylib)$(LOCAL_MODULE_SUFFIX)
#OVERRIDE_BUILT_MODULE_PATH := $(TARGET_OUT_INTERMEDIATE_LIBRARIES)
#include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
mylib := libpush
LOCAL_MODULE := $(mylib)
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := SHARED_LIBRARIES
LOCAL_MODULE_SUFFIX := .so
LOCAL_MODULE_PATH := $(TARGET_OUT_SHARED_LIBRARIES)
LOCAL_SRC_FILES := lib/$(mylib)$(LOCAL_MODULE_SUFFIX)
OVERRIDE_BUILT_MODULE_PATH := $(TARGET_OUT_INTERMEDIATE_LIBRARIES)
include $(BUILD_PREBUILT)

#include $(CLEAR_VARS)
#mylib := libam_adp
#LOCAL_MODULE := $(mylib)
#LOCAL_MODULE_TAGS := optional
#LOCAL_MODULE_CLASS := SHARED_LIBRARIES
#LOCAL_MODULE_SUFFIX := .so
#LOCAL_MODULE_PATH := $(TARGET_OUT_SHARED_LIBRARIES)
#LOCAL_SRC_FILES := lib/$(mylib)$(LOCAL_MODULE_SUFFIX)
#OVERRIDE_BUILT_MODULE_PATH := $(TARGET_OUT_INTERMEDIATE_LIBRARIES)
#include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
mylib := libxml2
LOCAL_MODULE := $(mylib)
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := SHARED_LIBRARIES
LOCAL_MODULE_SUFFIX := .so
LOCAL_MODULE_PATH := $(TARGET_OUT_SHARED_LIBRARIES)
LOCAL_SRC_FILES := lib/$(mylib)$(LOCAL_MODULE_SUFFIX)
OVERRIDE_BUILT_MODULE_PATH := $(TARGET_OUT_INTERMEDIATE_LIBRARIES)
include $(BUILD_PREBUILT)

#include $(CLEAR_VARS)
#mylib := libiconv
#LOCAL_MODULE := $(mylib)
#LOCAL_MODULE_TAGS := optional
#LOCAL_MODULE_CLASS := SHARED_LIBRARIES
#LOCAL_MODULE_SUFFIX := .so
#LOCAL_MODULE_PATH := $(TARGET_OUT_SHARED_LIBRARIES)
#LOCAL_SRC_FILES := lib/$(mylib)$(LOCAL_MODULE_SUFFIX)
#OVERRIDE_BUILT_MODULE_PATH := $(TARGET_OUT_INTERMEDIATE_LIBRARIES)
#include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE := libdvbpushjni
LOCAL_ARM_MODE := arm
LOCAL_MODULE_TAGS := optional
LOCAL_PRELINK_MODULE := false
LOCAL_SRC_FILES += \
	src/drm/drmport.c \
	src/drm/smcdrv.c \
	src/drm/drmapi.c \
	src/mid_push.c \
	src/common.c \
	src/multicast.c \
	src/main.c \
	src/xmlparser.c \
	src/sqlite.c \
	src/porting.c \
	src/tunerdmx.c \
        src/softdmx.c \
	src/dvbpush_jni.c \
	src/network.c \
	src/sha_verify.c \
	src/mtdutils.c \
	src/motherdisc.c \
	src/smarthome_shadow/smarthome.c \
	src/smarthome_shadow/serial.c \
        src/smarthome_shadow/socket.c 
LOCAL_CFLAGS += -W -Wall
LOCAL_LDFLAGS += -L$(LOCAL_PATH)/lib -ldbstardrm
LIBDVB_PATH := $(LOCAL_PATH)/../../../../../external/dvb/include
LOCAL_C_INCLUDES += $(LOCAL_PATH)/include \
        $(LIBDVB_PATH)/am_adp
LOCAL_SHARED_LIBRARIES += libc libcutils libdl liblog libsqlite
LOCAL_SHARED_LIBRARIES += libfileapi libfileapic libpush libam_adp libxml2 libiconv
include $(BUILD_SHARED_LIBRARY)

#include $(CLEAR_VARS)
#LOCAL_MODULE := libdrmvod
#LOCAL_MODULE_TAGS := optional
#LOCAL_PRELINK_MODULE := false
#LOCAL_SRC_FILES := \
	src/drm/drmvod.c 
#LIBPLAYER_PATH := $(LOCAL_PATH)/../../../../amlogic/LibPlayer
#LOCAL_C_INCLUDES := $(LOCAL_PATH)/include \
	$(LIBPLAYER_PATH)/amplayer/player/include \
    $(LIBPLAYER_PATH)/amplayer/control/include \
    $(LIBPLAYER_PATH)/amcodec/include \
    $(LIBPLAYER_PATH)/amffmpeg \
    $(JNI_H_INCLUDE) 
#LOCAL_CFLAGS := -Wall
#LOCAL_SHARED_LIBRARIES += liblog libdvbpushjni libplayerjni
#LOCAL_PROGUARD_ENABLED := disabled
#LOCAL_PRELINK_MODULE := false
#include $(BUILD_SHARED_LIBRARY)


#include $(CLEAR_VARS)
#LOCAL_MODULE := drmtest
#LOCAL_MODULE_TAGS := optional
#LOCAL_PRELINK_MODULE := false
#LOCAL_SRC_FILES := \
#	src/drm/drmport.c \
#	src/softdmx.c \
#	src/porting.c \
#	src/drm/smcdrv.c \
#	src/drm/drmtest.c 
#LOCAL_CFLAGS := -Wall
#LOCAL_LDFLAGS += -L$(LOCAL_PATH)/lib -ldbstardrm
#LOCAL_C_INCLUDES := $(LOCAL_PATH)/include/
#LOCAL_SHARED_LIBRARIES += libc liblog
#include $(BUILD_EXECUTABLE)

