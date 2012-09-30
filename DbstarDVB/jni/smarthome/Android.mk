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

LOCAL_MODULE := smarthome
LOCAL_MODULE_TAGS := optional
LOCAL_PRELINK_MODULE := false

LOCAL_SRC_FILES := src/main.c
LOCAL_SRC_FILES += src/common.c
LOCAL_SRC_FILES += src/equipment.c
LOCAL_SRC_FILES += src/global.c
LOCAL_SRC_FILES += src/instruction.c
LOCAL_SRC_FILES += src/porting.c
LOCAL_SRC_FILES += src/serial.c
LOCAL_SRC_FILES += src/socket.c
LOCAL_SRC_FILES += src/sqlite.c
LOCAL_SRC_FILES += src/timing.c

LOCAL_CFLAGS := -Wall

LOCAL_C_INCLUDES := $(LOCAL_PATH)/include/
LOCAL_STATIC_LIBRARIES +=
LOCAL_SHARED_LIBRARIES += libc liblog libsqlite
#LOCAL_LDLIBS := $(LOCAL_PATH)/libsqlite3.so


include $(BUILD_EXECUTABLE)
