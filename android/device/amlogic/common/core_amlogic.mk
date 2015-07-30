#
# Copyright (C) 2007 The Android Open Source Project
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

#PRODUCT_BRAND :=generic
#PRODUCT_DEVICE :=generic
PRODUCT_NAME := core_amlogic

USE_OPENGL_RENDERER := true

# set libplayer modules as defaults
WITH_LIBPLAYER_MODULE := true

# set soft stagefright extractor&decoder as defaults
WITH_SOFT_AM_EXTRACTOR_DECODER := true

# The OpenGL ES API level that is natively supported by this device.
# This is a 16.16 fixed point number
PRODUCT_PROPERTY_OVERRIDES += \
    ro.opengles.version=131072

PRODUCT_PROPERTY_OVERRIDES += \
    ro.config.notification_sound=OnTheHunt.ogg \
    ro.config.alarm_alert=Alarm_Classic.ogg

PRODUCT_PACKAGES := \
    ApplicationsProvider \
    BackupRestoreConfirmation \
    DefaultContainerService \
    HTMLViewer \
    KeyChain \
    MediaProvider \
    PackageInstaller \
    PicoTts \
    SettingsProvider \
    SharedStorageBackup \
    abcc \
    apache-xml \
    atrace \
    bouncycastle \
    bu \
    cacerts \
    com.android.location.provider \
    com.android.location.provider.xml \
    core \
    core-junit \
    dalvikvm \
    dexdeps \
    dexdump \
    dexlist \
    dexopt \
    dmtracedump \
    drmserver \
    dx \
    ext \
    framework2 \
    framework-res \
    hprof-conv \
    icu.dat \
    installd \
    ip \
    ip-up-vpn \
    ip6tables \
    iptables \
    e2fsck \
    keystore \
    keystore.default \
    libandroidfw \
    libOpenMAXAL \
    libOpenSLES \
    libaudiopreprocessing \
    libaudioutils \
    libbcc \
    libcrypto \
    libdownmix \
    libdvm \
    libdrmframework \
    libdrmframework_jni \
    libexpat \
    libfilterfw \
    libfilterpack_imageproc \
    libgabi++ \
    libicui18n \
    libicuuc \
    libjavacore \
    libkeystore \
    libmdnssd \
    libnativehelper \
    libnfc_ndef \
    libportable \
    libpowermanager \
    libspeexresampler \
    libsqlite_jni \
    libssl \
    libstagefright \
    libstagefright_chromium_http \
    libstagefright_soft_aacdec \
    libstagefright_soft_aacenc \
    libstagefright_soft_amrdec \
    libstagefright_soft_amrnbenc \
    libstagefright_soft_amrwbenc \
    libstagefright_soft_flacenc \
    libstagefright_soft_g711dec \
    libstagefright_soft_h264dec \
    libstagefright_soft_h264enc \
    libstagefright_soft_mp3dec \
    libstagefright_soft_mp2dec \
    libstagefright_soft_mpeg4dec \
    libstagefright_soft_mpeg4enc \
    libstagefright_soft_vorbisdec \
    libstagefright_soft_vpxdec \
    libstagefright_soft_rawdec \
    libstagefright_soft_adpcmdec \
    libstagefright_soft_adifdec \
    libstagefright_soft_latmdec \
    libstagefright_soft_adtsdec \
    libstagefright_soft_alacdec \
    libstagefright_platformenc \
    audio.r_submix.default \
    screen_source.amlogic \
    libvariablespeed \
    libwebrtc_audio_preprocessing \
    libstagefright_soft_wmaprodec \
    libstagefright_soft_wmadec    \
    libwilhelm \
    libz \
    make_ext4fs \
    mdnsd \
    requestsync \
    screencap \
    sensorservice \
    lint \
    uiautomator \
    telephony-common \
    mms-common \
    zoneinfo.dat \
    zoneinfo.idx \
    zoneinfo.version \
    rild \
    libsrec_jni \
    system_key_server \
    libcurl \
    curl \
    libamadec_omx_api  \
    libamadec_wfd_out  \
    libaac_helix   \
    libadpcm \
    libamr \
    libape \
    libcook \
    libdtsenc \
    libfaad \
    libflac \
    libmad  \
    libpcm  \
    libpcm_wfd \
    libraac 

ifdef DOLBY_DAP
    PRODUCT_PACKAGES += \
        Ds \
        framework_ext
    ifdef DOLBY_DAP_NOCONSUMERAPP
    else
        PRODUCT_PACKAGES += \
            DsUI
    endif
    ifdef DOLBY_DAP_OPENSLES
        PRODUCT_PACKAGES += \
            libdseffect
    else ifdef DOLBY_DAP_DSP
        PRODUCT_PACKAGES += \
            libds_jni \
            libds_native \
            libalsa-intf
    endif
endif #DOLBY_DAP
ifdef DOLBY_UDC
    PRODUCT_PACKAGES += \
        libstagefright_soft_ddpdec
endif #DOLBY_UDC    

ifeq ($(WITH_LIBPLAYER_MODULE),true)
PRODUCT_PACKAGES += \
    librtmp \
    libmms_mod \
    libcurl_mod \
    libvhls_mod \
    libdash_mod
endif 

ifeq ($(WITH_SOFT_AM_EXTRACTOR_DECODER),true)
PRODUCT_PACKAGES += \
    libamffmpegadapter \
    libamffmpeg \
    libstagefright_soft_amvp6adec \
    libstagefright_soft_amvp6dec \
    libstagefright_soft_amvp6fdec
endif 

PRODUCT_COPY_FILES += \
    system/core/rootdir/init.usb.rc:root/init.usb.rc \
    system/core/rootdir/init.trace.rc:root/init.trace.rc

fw_env_config := $(strip $(wildcard $(TARGET_DEVICE_DIR)/recovery/fw_env.config))

ifeq ($(fw_env_config),)
	fw_env_config := $(strip $(wildcard external/fw_env/fw_env.config))
endif

ifneq ($(fw_env_config),)
PRODUCT_COPY_FILES += \
    $(fw_env_config):$(TARGET_OUT)/system/etc/fw_env.config
endif

# host-only dependencies
ifeq ($(WITH_HOST_DALVIK),true)
    PRODUCT_PACKAGES += \
        apache-xml-hostdex \
        bouncycastle-hostdex \
        core-hostdex \
        libcrypto \
        libexpat \
        libicui18n \
        libicuuc \
        libjavacore \
        libssl \
        libz-host \
        dalvik \
        zoneinfo-host.dat \
        zoneinfo-host.idx \
        zoneinfo-host.version
endif

ADDITIONAL_BUILD_PROPERTIES += ds1.audio.multichannel.support=false

ifeq ($(HAVE_SELINUX),true)
    PRODUCT_PACKAGES += \
        sepolicy \
        file_contexts \
        seapp_contexts \
        property_contexts \
        mac_permissions.xml
endif

## set ds1 support properity##
ifeq ($(AMLOGIC_DS_EFFECT),true)
ADDITIONAL_BUILD_PROPERTIES += ds1.audio.effect.support=true
else
ADDITIONAL_BUILD_PROPERTIES += ds1.audio.effect.support=false
endif

#for usb_burning_v2
ifeq ($(TARGET_SUPPORT_USB_BURNING_V2),true)
PRODUCT_COPY_FILES += \
    $(TARGET_PRODUCT_DIR)/ddr_init.bin:$(PRODUCT_OUT)/ddr_init.bin \
    $(TARGET_PRODUCT_DIR)/u-boot-orig.bin:$(PRODUCT_OUT)/u-boot-orig.bin \
    $(TARGET_PRODUCT_DIR)/aml_sdc_burn.ini:$(PRODUCT_OUT)/aml_sdc_burn.ini

PRODUCT_PROPERTY_OVERRIDES += \
    ubootenv.var.firstboot=2

endif

ifeq ($(WITH_LMBENCH),true)
PRODUCT_PACKAGES += \
    bw_file_rd bw_mem bw_mmap_rd bw_pipe bw_tcp bw_udp  \
    bw_unix                                             \
    cache disk enough hello                             \
    lat_connect lat_ctx lat_fcntl lat_fifo lat_fs       \
    lat_mem_rd lat_mmap lat_ops lat_pagefault lat_pipe  \
    lat_proc lat_select lat_sig lat_syscall             \
    lat_tcp lat_udp lat_unix lat_unix_connect           \
    lat_usleep lat_pmake                                \
    line lmdd lmhttp par_mem par_ops loop_o memsize     \
    mhz msleep rhttp timing_o tlb stream                \
    lmbench-config lmbench-results lmbench
endif

#$(call inherit-product, $(SRC_TARGET_DIR)/product/base.mk)
$(call inherit-product, device/amlogic/common/base.mk)
