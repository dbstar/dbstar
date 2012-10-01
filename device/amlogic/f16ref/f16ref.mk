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

#
# This file is the build configuration for a full Android
# build for M1 reference board. This cleanly combines a set of
# device-specific aspects (drivers) with a device-agnostic
# product configuration (apps).
#

# Inherit from those products. Most specific first.
$(call inherit-product, device/amlogic/common/mbx_amlogic.mk)

# Discard inherited values and use our own instead.
PRODUCT_NAME := f16ref
PRODUCT_MANUFACTURER := MBX
PRODUCT_DEVICE := f16ref
PRODUCT_MODEL := MBX reference board (f16ref)
PRODUCT_LOCALES := en_US \
	zh_CN \
	zh_TW \
	en_GB \
	en_CA \
	en_AU \
	en_NZ \
	en_SG \
	ja_JP \
	mdpi hdpi

# Change this to match target country
# 11 North America; 14 Japan; 13 rest of world
PRODUCT_DEFAULT_WIFI_CHANNELS := 14

PRODUCT_DEFAULT_PROPERTY_OVERRIDES += \
	persist.sys.usb.config=mass_storage

PRODUCT_PACKAGES += \
	FileBrowser \
	AppInstaller \
	VideoPlayer \
	Samba \
	libsmbbase \
	libsmbmnt \
	smbd \
	Update \
	RemoteIME \
	OOBE \
	remotecfg \
	PPPoE \
	libpppoejni \
	pppoe_wrapper \
	pppoe \
	amlogic.pppoe \
	amlogic.pppoe.xml \
	pcli \
	audio_firmware \
	remote_control \
	amlpictureKit \
	PicturePlayer \
	MusicPlayer	\
	Bluetooth

PRODUCT_COPY_FILES += \
	device/amlogic/f16ref/ump.ko:root/boot/ump.ko \
	device/amlogic/f16ref/mali.ko:root/boot/mali.ko \
	device/amlogic/f16ref/8192cu.ko:system/lib/8192cu.ko \
	device/amlogic/f16ref/cfg80211.ko:system/lib/cfg80211.ko \
	device/amlogic/f16ref/ath6kl_usb.ko:system/lib/ath6kl_usb.ko \
	device/amlogic/f16ref/cfg80211_ath6kl.ko:system/lib/cfg80211_ath6kl.ko \
	device/amlogic/f16ref/compat.ko:system/lib/compat.ko \
	device/amlogic/f16ref/rt3070sta.ko:system/lib/rt3070sta.ko \
	device/amlogic/f16ref/RT2870STA.dat:system/etc/Wireless/RT2870STA/RT2870STA.dat

	
PRODUCT_PACKAGES += wpa_supplicant.conf
PRODUCT_PACKAGES += hostapd_wps


# 40181 Wifi driver
PRODUCT_PACKAGES += wl
PRODUCT_PACKAGES += dhd
PRODUCT_PACKAGES += 40181/nvram.txt
PRODUCT_PACKAGES += 40181/fw_bcm40181a0.bin
PRODUCT_PACKAGES += 40181/fw_bcm40181a0_apsta.bin
PRODUCT_PACKAGES += 40181/fw_bcm40181a2.bin
PRODUCT_PACKAGES += 40181/fw_bcm40181a2_apsta.bin
PRODUCT_PACKAGES += 40181/fw_bcm40181a2_p2p.bin
PRODUCT_COPY_FILES += device/amlogic/f16ref/dhd.ko:system/lib/dhd.ko

# 4018X Wifi driver
#PRODUCT_PACKAGES += wpa_cli
#PRODUCT_PACKAGES += 40183/sdio-sta.bin
#PRODUCT_PACKAGES += 40183/sdio-apsta.bin
#PRODUCT_PACKAGES += 40183/sdio-p2p.bin
#PRODUCT_PACKAGES += 40183/fw_bcm40183b2.bin
#PRODUCT_PACKAGES += 40183/fw_bcm40183b2_apsta.bin
#PRODUCT_PACKAGES += 40183/fw_bcm40183b2_p2p.bin
#PRODUCT_PACKAGES += 40183/fw_bcm4330b2.bin
#PRODUCT_PACKAGES += 40183/nvram.txt
#PRODUCT_COPY_FILES += device/amlogic/f16ref/dhd.ko:system/lib/dhd.ko
#PRODUCT_COPY_FILES += hardware/amlogic/wifi/bcm_4018x/config/40183/fw_bcm40183b2.bin:system/etc/wifi/40183/fw_bcm40183b2.bin
#PRODUCT_COPY_FILES += hardware/amlogic/wifi/bcm_4018x/config/40183/fw_bcm40183b2_apsta.bin:system/etc/wifi/40183/fw_bcm40183b2_apsta.bin
#PRODUCT_COPY_FILES += hardware/amlogic/wifi/bcm_4018x/config/40183/fw_bcm40183b2_p2p.bin:system/etc/wifi/40183/fw_bcm40183b2_p2p.bin
#PRODUCT_COPY_FILES += hardware/amlogic/wifi/bcm_4018x/config/40183/fw_bcm4330b2.bin:system/etc/wifi/40183/fw_bcm4330b2.bin

# Camera
PRODUCT_PACKAGES += camera.amlogic
	
BUILD_DVB_PACKAGES := false

ifeq ($(BUILD_DVB_PACKAGES), true)
PRODUCT_PACKAGES += \
	libam_adp \
	libam_mw \
	libjnidvbplayer \
	libjnidvr \
	libjnifrontend \
	libjnidmx \
	libjnidsc \
	libjnifilter \
	libjniosd \
	libjniamsmc \
	libjnidvbdatabase \
	libjnidvbscanner \
	libjnidvbepgscanner \
	libjnidvbrecorder \
	libjnidvbclientsubtitle \
	bookplay_package \
	dvbepg \
	DVBPlayer \
	dvbsearch \
	DVBService \
	progmanager
endif
PRODUCT_PACKAGES += Phone
PRODUCT_PACKAGES += dbstar
# USE_OPENGL_RENDERER := false
PRODUCT_COPY_FILES += \
       $(LOCAL_PATH)/init.factorytest.rc:init.factorytest.rc \
       $(LOCAL_PATH)/initlogo-robot-1280x720.rle:root/initlogo.720p.rle \
       $(LOCAL_PATH)/initlogo-robot-1920x1080.rle:root/initlogo.1080p.rle \
       $(LOCAL_PATH)/initlogo-robot-720x480.rle:root/initlogo.480p.rle \
       $(LOCAL_PATH)/initlogo-robot-720x576.rle:root/initlogo.576p.rle \
       $(LOCAL_PATH)/set_display_mode.sh:system/bin/set_display_mode.sh \
       $(LOCAL_PATH)/reset_display_mode.sh:system/bin/reset_display_mode.sh \
       $(LOCAL_PATH)/dbstar_control.sh:system/bin/dbstar_control.sh \
       $(LOCAL_PATH)/dbstar.conf:data/dbstar/dbstar.conf \
       $(LOCAL_PATH)/push.conf:data/dbstar/push.conf \
       $(LOCAL_PATH)/audiodsp_codec_ac3.bin:system/etc/firmware/audiodsp_codec_ac3.bin\
       $(LOCAL_PATH)/audiodsp_codec_ddp_dcv.bin:system/etc/firmware/audiodsp_codec_ddp_dcv.bin\
       $(LOCAL_PATH)/bootanimation.zip:system/media/bootanimation.zip \
       $(LOCAL_PATH)/media_profiles.xml:system/etc/media_profiles.xml \
       $(LOCAL_PATH)/asound.conf:system/etc/asound.conf \
       $(LOCAL_PATH)/asound.state:system/etc/asound.state \
       $(LOCAL_PATH)/audio_effects.conf:system/etc/audio_effects.conf \
       $(LOCAL_PATH)/remote.conf:system/etc/remote.conf \
	     $(LOCAL_PATH)/Vendor_0001_Product_0001.kl:/system/usr/keylayout/Vendor_0001_Product_0001.kl

# Overlay for device specific settings
DEVICE_PACKAGE_OVERLAYS := device/amlogic/f16ref/overlay

PRODUCT_COPY_FILES += \
  frameworks/base/data/etc/android.hardware.camera.front.xml:system/etc/permissions/android.hardware.camera.front.xml \
	frameworks/base/data/etc/android.hardware.camera.autofocus.xml:system/etc/permissions/android.hardware.camera.autofocus.xml \
	frameworks/base/data/etc/android.hardware.wifi.xml:system/etc/permissions/android.hardware.wifi.xml \
	frameworks/base/data/etc/android.hardware.sensor.accelerometer.xml:system/etc/permissions/android.hardware.sensor.accelerometer.xml \
	frameworks/base/data/etc/android.hardware.sensor.gyroscope.xml:system/etc/permissions/android.hardware.sensor.gyroscope.xml \
	frameworks/base/data/etc/android.hardware.sensor.compass.xml:system/etc/permissions/android.hardware.sensor.compass.xml \
	frameworks/base/data/etc/android.hardware.touchscreen.xml:system/etc/permissions/android.hardware.touchscreen.xml \
	frameworks/base/data/etc/android.hardware.location.xml:system/etc/permissions/android.hardware.location.xml \
	packages/wallpapers/LivePicker/android.software.live_wallpaper.xml:system/etc/permissions/android.software.live_wallpaper.xml

# multitouch
PRODUCT_COPY_FILES += \
  device/amlogic/f16ref/tablet_core_hardware.xml:system/etc/permissions/tablet_core_hardware.xml \
	frameworks/base/data/etc/android.hardware.touchscreen.multitouch.xml:system/etc/permissions/android.hardware.touchscreen.multitouch.xml \
	frameworks/base/data/etc/android.hardware.touchscreen.multitouch.distinct.xml:system/etc/permissions/android.hardware.touchscreen.multitouch.distinct.xml
