LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-subdir-java-files) \
	src/com/dbstar/DbstarDVB/IDbstarService.aidl 

LOCAL_STATIC_JAVA_LIBRARIES := achartengine ksoap2 FormatCMD

LOCAL_PACKAGE_NAME := DbstarLauncher
LOCAL_CERTIFICATE := platform
LOCAL_REQUIRED_MODULES := libnativeutils
include $(BUILD_PACKAGE)

########################################################
include $(CLEAR_VARS)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := achartengine:libs/achartengine-1.0.0.jar \
										ksoap2:libs/ksoap2-android-assembly-2.6.5-jar-with-dependencies.jar \
										FormatCMD:libs/FormatCMD.jar

include $(BUILD_MULTI_PREBUILT)

########################################################
# Build native code
include $(call all-makefiles-under,$(LOCAL_PATH))
