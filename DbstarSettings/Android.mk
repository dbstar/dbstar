LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-subdir-java-files) \
	src/com/dbstar/DbstarDVB/IDbstarService.aidl \
#	./src/com/dbstar/DbstarDVB/PlayerService/IPlayerService.aidl

LOCAL_PACKAGE_NAME := DbstarDVB
LOCAL_CERTIFICATE := platform
LOCAL_STATIC_JAVA_LIBRARIES := amlogic.subtitle
LOCAL_REQUIRED_MODULES := libamplayerjni libsubjni

LOCAL_PROGUARD_ENABLED := disabled
include $(BUILD_PACKAGE)

# Use the folloing include to make our test apk.
include $(call all-makefiles-under,$(LOCAL_PATH))
