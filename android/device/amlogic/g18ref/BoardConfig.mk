# Copyright (C) 2010 Amlogic Inc
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

# config.mk
#
# Product-specific compile-time definitions.
#


# Check Logo Size

BOARD_TVMODE_ALL_SCALE := true
TARGET_CPU_ABI2 := armeabi
# Camera
USE_CAMERA_STUB := false
BOARD_HAVE_FRONT_CAM := false
BOARD_HAVE_BACK_CAM := false
BOARD_USE_USB_CAMERA := true
BOARD_HAVE_FLASHLIGHT := false

BOARD_VOUT_USES_FREESCALE := false
##################################################### CPU
TARGET_ARCH := arm
TARGET_ARCH_VARIANT := armv7-a-neon
TARGET_CPU_ABI := armeabi-v7a
TARGET_CPU_ABI2 := armeabi
TARGET_CPU_SMP := true

##################################################### release package
TARGET_BOOTLOADER_BOARD_NAME := g18ref
TARGET_BOARD_PLATFORM := meson6
TARGET_NO_BOOTLOADER := true
TARGET_NO_RADIOIMAGE := true
TARGET_SIMULATOR := false

TARGET_NO_KERNEL := false
#TARGET_AMLOGIC_BOOTLOADER := $(PRODUCT_OUT)/u-boot.bin
TARGET_AMLOGIC_LOGO := $(PRODUCT_OUT)/res-package.img
TARGET_AMLOGIC_RES_PACKAGE := device/amlogic/g18ref/res_pack
#TARGET_AMLOGIC_AML_LOGO := device/amlogic/g18ref/aml_logo.bmp
# Check Logo Size
BOARD_MATCH_LOGO_SIZE := true
#save ubootenv in nand partition
UBOOTENV_SAVE_IN_NAND := false
BOARD_UBOOTENV_BIG_SIZE := true
TARGET_BUILD_WIPE_USERDATA := false
TARGET_USERIMAGES_USE_EXT4 := true
BOARD_SYSTEMIMAGE_PARTITION_SIZE := 1073741824
BOARD_FLASH_BLOCK_SIZE := 2048
TARGET_ENABLE_SCALE_FUNCTION := true
TARGET_HAS_HDMIONLY_FUNCTION := true

USE_OPENGL_RENDERER := true

ifeq ($(BUILD_WITH_PLAYREADY_DRM), true)
#playready license process in smoothstreaming(default)
BOARD_PLAYREADY_LP_IN_SS := true
#set BOARD_PLAYREADY_TVP and TARGET_USE_SECUREOS to enable PlayReady TVP
#BOARD_PLAYREADY_TVP:= true
#TARGET_USE_SECUREOS := true  #for secureos kernel compiling
endif

#PRODUCT_EXTRA_RECOVERY_KEYS := ../common/releasekey.x509.pem

ifeq ($(BOARD_WIDEVINE_SUPPORTLEVEL),1)
    BOARD_WIDEVINE_OEMCRYPTO_LEVEL := 1
    TARGET_USE_SECURE_STORAGE := true
    TARGET_USE_SECUREOS := true
endif

#system md5
ifeq ($(strip $(TARGET_USE_SECURITY_MODE)),true)
TARGET_AMLOGIC_SYSTEMMD5 := $(PRODUCT_OUT)/md5_result
endif
include device/amlogic/$(TARGET_PRODUCT)/Kernel.mk
