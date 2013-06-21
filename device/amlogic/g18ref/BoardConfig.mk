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

# Alsa
BOARD_USES_ALSA_AUDIO := true
BUILD_WITH_ALSA_UTILS := true
BOARD_USES_GENERIC_AUDIO := true

# Bluetooth
BOARD_HAVE_BLUETOOTH := true

# Check Logo Size
BOARD_MATCH_LOGO_SIZE := true

# Camera
USE_CAMERA_STUB := false
BOARD_HAVE_FRONT_CAM := false
BOARD_HAVE_BACK_CAM := false
BOARD_USE_USB_CAMERA := true
BOARD_HAVE_FLASHLIGHT := false
# GPS
#BOARD_GPS_LIBRARIES := libgpsstub

# Wifi
#rk 8188
WIFI_DRIVER := rtl8192cu
#WIFI_DRIVER := bcm40181

#bcm 40183
#WIFI_DRIVER := bcm40183
#WIFI_DRIVER_MODULE_PATH := /system/lib/dhd.ko
#WIFI_DRIVER_MODULE_NAME := dhd
#WIFI_DRIVER_MODULE_ARG  := "firmware_path=/etc/wifi/40183/sdio-sta.bin nvram_path=/etc/wifi/40183/nvram.txt"
#WIFI_DRIVER_FW_PATH_STA :=/etc/wifi/40183/sdio-sta.bin
#WIFI_DRIVER_FW_PATH_AP  :=/etc/wifi/40183/sdio-apsta.bin
#WIFI_DRIVER_FW_PATH_P2P :=/etc/wifi/40183/sdio-p2p.bin

#WIFI_DRIVER := rt3070
#WIFI_DRIVER := ath6kl
WPA_SUPPLICANT_VERSION := VER_0_8_X
BOARD_WPA_SUPPLICANT_PRIVATE_LIB := lib_driver_cmd_wext
BOARD_WPA_SUPPLICANT_DRIVER := WEXT
#BOARD_WPA_SUPPLICANT_PRIVATE_LIB := lib_driver_cmd_nl80211
#BOARD_WPA_SUPPLICANT_DRIVER := NL80211
BOARD_HOSTAPD_DRIVER_RTL :=true

ifeq ($(strip $(WIFI_DRIVER)),bcm40181)
WIFI_DRIVER_MODULE_PATH := /system/lib/dhd.ko
WIFI_DRIVER_MODULE_NAME := dhd
WIFI_DRIVER_MODULE_ARG  := "firmware_path=/etc/wifi/40181/fw_bcm40181a2.bin nvram_path=/etc/wifi/40181/nvram.txt"
WIFI_DRIVER_FW_PATH_STA :=/etc/wifi/40181/fw_bcm40181a2.bin
WIFI_DRIVER_FW_PATH_AP  :=/etc/wifi/40181/fw_bcm40181a2_apsta.bin
WIFI_DRIVER_FW_PATH_P2P :=/etc/wifi/40181/fw_bcm40181a2_p2p.bin
WPA_SUPPLICANT_VERSION := VER_0_8_X
BOARD_WPA_SUPPLICANT_PRIVATE_LIB := lib_driver_cmd_wext
BOARD_WPA_SUPPLICANT_DRIVER := WEXT
BOARD_HOSTAPD_DRIVER_RTL := false
endif

# Amlogic player
BUILD_WITH_AMLOGIC_PLAYER   := true

# VOut
BOARD_VOUT_USES_FREESCALE := false

TARGET_BOOTLOADER_BOARD_NAME := f16ref
TARGET_NO_BOOTLOADER := true
TARGET_NO_KERNEL := true
TARGET_NO_RADIOIMAGE := true
TARGET_SIMULATOR := false
TARGET_PROVIDES_INIT_RC := true
TARGET_PROVIDES_UEVENTD_RC := true
TARGET_CPU_ABI := armeabi-v7a
TARGET_CPU_ABI2 := armeabi
TARGET_ARCH_VARIANT := armv7-a-neon

# Recovery
TARGET_USE_AMLOGIC_MKYAFFS_TOOL := true
TARGET_AMLOGIC_MKYAFFSIMG_TOOL := mkyaffsimage4K.dat
TARGET_AMLOGIC_KERNEL := $(PRODUCT_OUT)/uImage
TARGET_AMLOGIC_RECOVERY_KERNEL := $(PRODUCT_OUT)/uImage_recovery
#TARGET_AMLOGIC_SPI := $(PRODUCT_OUT)/spi.bin

TARGET_AMLOGIC_AML_LOGO := device/amlogic/f16ref/aml_logo.bmp
TARGET_BUILD_WIPE_USERDATA := false

# Internal NAND flash /media partition

# Check Logo Size
BOARD_MATCH_LOGO_SIZE := true
BOARD_TVMODE_ALL_SCALE := true

#save ubootenv in nand partition
UBOOTENV_SAVE_IN_NAND := false
# Use default APK
BOARD_USE_DEFAULT_APPINSTALL := true



# Use default keylayout in device/amlogic/common/keylayout
# instead of sdk/emulator/keymaps
PRODUCT_PROVIDES_DEFAULT_KEYLAYOUT := true

include device/amlogic/$(TARGET_PRODUCT)/recovery/Recovery.mk
