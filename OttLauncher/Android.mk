LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-subdir-java-files)


LOCAL_PACKAGE_NAME := OttLauncher
LOCAL_CERTIFICATE := platform
LOCAL_PROGUARD_ENABLED := disabled
include $(BUILD_PACKAGE)
# Use the folloing include to make our test apk.
include $(call all-makefiles-under,$(LOCAL_PATH))
