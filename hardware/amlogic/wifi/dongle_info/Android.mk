LOCAL_PATH := $(call my-dir)

ifneq ($(BOARD_WPA_SUPPLICANT_DRIVER),)
  CONFIG_DRIVER_$(BOARD_WPA_SUPPLICANT_DRIVER) := y
endif

WPA_SUPPL_DIR = external/wpa_supplicant_8

include $(WPA_SUPPL_DIR)/wpa_supplicant/.config

WPA_SUPPL_DIR_INCLUDE = $(WPA_SUPPL_DIR)/src \
	$(WPA_SUPPL_DIR)/src/common \
	$(WPA_SUPPL_DIR)/src/drivers \
	$(WPA_SUPPL_DIR)/src/l2_packet \
	$(WPA_SUPPL_DIR)/src/utils \
	$(WPA_SUPPL_DIR)/src/wps \
	$(WPA_SUPPL_DIR)/wpa_supplicant

# To force sizeof(enum) = 4
L_CFLAGS += -mabi=aapcs-linux

ifdef CONFIG_ANDROID_LOG
L_CFLAGS += -DCONFIG_ANDROID_LOG
endif

LOCAL_C_INCLUDES := $(KERNEL_HEADERS) \
                    external/libusb/libusb-0.1.12
L_CFLAGS += -DUSE_USB_WIFI_DONGLE -DSYSFS_PATH_MAX=256 -DWIFI_DRIVER_MODULE_DIR=\"/system/lib\"


include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE := lib_driver_load
LOCAL_SHARED_LIBRARIES := libc libcutils libusb
LOCAL_CFLAGS := $(L_CFLAGS)
LOCAL_SRC_FILES := driver_load_ath6kl.c \
					driver_load_ralink.c \
					driver_load_rtl8192cu.c \
					driver_load_rtl8712su.c \
					driver_load_ath9k.c \
					driver_load_rtl8192du.c\
					driver_load_rtl8188eu.c
					
LOCAL_C_INCLUDES := $(WPA_SUPPL_DIR_INCLUDE)
include $(BUILD_SHARED_LIBRARY)
