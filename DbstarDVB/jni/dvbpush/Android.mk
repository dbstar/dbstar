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
mylib := libpush.a
LOCAL_MODULE := $(mylib)
LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := lib/libpush.a
include $(PREBUILD_STATIC_LIBRARY)

include $(CLEAR_VARS)
mylib := libxml2.so
LOCAL_MODULE := $(mylib)
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := SHARED_LIBRARIES
LOCAL_MODULE_PATH := $(TARGET_OUT_SHARED_LIBRARIES)
LOCAL_SRC_FILES := lib/$(mylib)
OVERRIDE_BUILT_MODULE_PATH := $(TARGET_OUT_INTERMEDIATE_LIBRARIES)
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
mylib := libiconv.so
LOCAL_MODULE := $(mylib)
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := SHARED_LIBRARIES
LOCAL_MODULE_PATH := $(TARGET_OUT_SHARED_LIBRARIES)
LOCAL_SRC_FILES := lib/$(mylib)
OVERRIDE_BUILT_MODULE_PATH := $(TARGET_OUT_INTERMEDIATE_LIBRARIES)
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE := libdvbpushjni
LOCAL_ARM_MODE := arm
LOCAL_MODULE_TAGS := optional
LOCAL_PRELINK_MODULE := false

LOCAL_SRC_FILES += \
	src/mid_push.c \
	src/common.c \
	src/multicast.c \
	src/main.c \
	src/xmlparser.c \
	src/sqlite.c \
	src/porting.c \
	src/softdmx.c \
	src/dvbpush_jni.c
#	src/drmport.c

LOCAL_CFLAGS += -W -Wall
LOCAL_LDFLAGS += -L$(LOCAL_PATH)/lib -lpush
LOCAL_C_INCLUDES += $(LOCAL_PATH)/include
LOCAL_SHARED_LIBRARIES += libc libdl liblog libsqlite libxml2 libiconv
include $(BUILD_SHARED_LIBRARY)
