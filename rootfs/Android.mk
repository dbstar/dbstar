LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
$(shell cd $(LOCAL_PATH) && mkdir -p $(ANDROID_PRODUCT_OUT)/system && cp -rf system/* $(ANDROID_PRODUCT_OUT)/system)
$(shell cd $(LOCAL_PATH) && mkdir -p $(ANDROID_PRODUCT_OUT)/root && cp -rf root/* $(ANDROID_PRODUCT_OUT)/root)
