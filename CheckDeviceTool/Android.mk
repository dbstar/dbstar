LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

#LOCAL_JAVA_LIBRARIES := bouncycastle
#LOCAL_STATIC_JAVA_LIBRARIES := guava

LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := $(call all-subdir-java-files) \
	src/com/dbstar/DbstarDVB/IDbstarService.aidl 

LOCAL_PACKAGE_NAME := CheckDeviceTool
LOCAL_CERTIFICATE := platform

include $(BUILD_PACKAGE)

# Use the folloing include to make our test apk.
include $(call all-makefiles-under,$(LOCAL_PATH))
