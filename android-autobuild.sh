#!/bin/bash
#
# File:   aundroid-autobuild.sh
#     Do Android autobuild.
# Author: peifu.jiang@gmail.com
# 


#################################################################################
# basic configs
#################################################################################
BASEDIR="/android"
ANDROID_SRC=$BASEDIR/ics
KERNEL_SRC=$ANDROID_SRC/kernel
UBOOT_SRC=$ANDROID_SRC/uboot
DBSTAR_SRC=$ANDROID_SRC/packages/dbstar
BUILD_OUT=$BASEDIR/f16-autobuild-out
ROOTFS_OUT=$ANDROID_SRC/out/target/product/f16ref
LOG_REPO=$BUILD_OUT/repo.log
LOG_UBOOT=$BUILD_OUT/uboot.log
LOG_KERNEL=$BUILD_OUT/kernel.log
LOG_ROOTFS=$BUILD_OUT/rootfs.log
LOG_OTAPACKAGE=$BUILD_OUT/otapackage.log
LOG_LOGGER=$LOG_REPO

GIT_SREVER="git://git.myamlogic.com/platform/manifest.git"
GIT_BRANCH="ics-amlogic-0702"
ANDROID_LUNCH="18"
UBOOT_CONFIG="m3_mbox_config"
KERNEL_CONFIG="meson_reff16_defconfig"
#MAKE_ARGS="-j3"
TIMESTAMP=`date +%Y%m%d`


#################################################################################
# building flags
#################################################################################
BUILD_FLAG_PATCH=1
BUILD_FLAG_KERNEL=2
BUILD_FLAG_RECOVERY=3
BUILD_FLAG_ROOTFS=4
BUILD_FLAG_DBSTAR=5
BUILD_FLAG_OTAPACKAGE=6
BUILD_FLAG_ALL=998
BUILD_FLAG_RELEASE=999

AUTOBUILD_FLAG=0

#  0: donot clean
#  1: clean and make
REBUILD_FLAG=0

#  0: log into file
#  1: log stdout
VERBOSE_FLAG=0


#################################################################################
# module auto build functions
#################################################################################
call()
{
    echo ">>> $@" 
	if [ $VERBOSE_FLAG -eq 1 ]; then
		$@
	else
		$@ 1>>$LOG_LOGGER 2>&1
	fi
}

logger()
{
	echo "********** [`date`] $@"
}

checkout()
{
	logger "START Android repo checkout"
	LOG_LOGGER=$LOG_REPO
	call mkdir -p $ANDROID_SRC
	call cd $ANDROID_SRC
#call repo init --repo-url=$REPO_URL -u $GIT_SREVER -b $GIT_BRANCH
	call repo init -u $GIT_SREVER -b $GIT_BRANCH

	if [ $? -eq 0 ]; then
		call repo sync $MAKE_ARGS
		call repo start $GIT_BRANCH --all
		call repo manifest -r -o $BUILD_OUT/$GIT_BRANCH-$TIMESTAMP.xml
		logger "FINISH Android repo checkout"
	else
		logger "ERROR Android repo checkout"
		exit 1
	fi
}

repo_sync()
{
	logger "START Android repo sync"
	LOG_LOGGER=$LOG_REPO
	call cd $ANDROID_SRC
	call repo sync $MAKE_ARGS

	if [ $? -eq 0 ]; then
		call repo start $GIT_BRANCH --all
		call repo manifest -r -o $BUILD_OUT/$GIT_BRANCH-$TIMESTAMP.xml
		logger "FINISH Android repo sync"
	else
		logger "ERROR Android repo sync"
		exit 1
	fi
}

uboot_make()
{
	logger "START make uboot"
	LOG_LOGGER=$LOG_UBOOT
	call cd $UBOOT_SRC
	call rm -rf build
	call make $UBOOT_CONFIG
	call make $MAKE_ARGS
	if [ $? -eq 0 ]; then
		call mkdir -p $BUILD_OUT
		call cp ./build/u-boot-aml-ucl.bin $BUILD_OUT
		logger "FINISH make uboot"
	else
		logger "ERROR make uboot"
		exit 1
	fi
}

lunch_setup()
{
	call cd $ANDROID_SRC
	call source ./build/envsetup.sh
	call lunch $ANDROID_LUNCH
}

rootfs_clean()
{
	logger "START clean rootfs"
	LOG_LOGGER=$LOG_ROOTFS
	call cd $ANDROID_SRC
	call rm -rf ./out
	logger "FINISH clean rootfs"
}

rootfs_make()
{
	logger "START make rootfs"
	LOG_LOGGER=$LOG_ROOTFS

	call cd $ANDROID_SRC
	if [ $REBUILD_FLAG -eq 1 ]; then
		rootfs_clean
	fi
	call make $MAKE_ARGS
	if [ $? -eq 0 ]; then
		logger "FINISH make rootfs"
	else
		logger "ERROR make rootfs"
		exit 1
	fi
}

otapackage_make()
{
	logger "START make otapackage"

	LOG_LOGGER=$LOG_OTAPACKAGE
	call cp $BUILD_OUT/uImage $ROOTFS_OUT
	call cp $BUILD_OUT/uImage_recovery $ROOTFS_OUT

	call cd $ANDROID_SRC
	call make otapackage $MAKE_ARGS
	if [ $? -eq 0 ]; then
		logger "FINISH make otapackage"
		call cp $ROOTFS_OUT/*.zip $BUILD_OUT
	else
		logger "ERROR make otapackage"
		exit 1
	fi
}

modules_make()
{
	logger "START make modules"
	LOG_LOGGER=$LOG_KERNEL
	call cd $KERNEL_SRC
	if [ $REBUILD_FLAG -eq 1 ]; then
		call make distclean
	fi
	call make $KERNEL_CONFIG
	call make uImage $MAKE_ARGS
	call make modules

	if [ $? -eq 0 ]; then
		call cp drivers/amlogic/mali/mali.ko $BUILD_OUT
		call cp drivers/amlogic/ump/ump.ko $BUILD_OUT
		call cp drivers/amlogic/wifi/rtl8xxx_CU/8192cu.ko $BUILD_OUT
		call cp net/wireless/cfg80211.ko $BUILD_OUT
		call cp drivers/scsi/scsi_wait_scan.ko $BUILD_OUT

		call cp drivers/amlogic/mali/mali.ko $ROOTFS_OUT/root/boot/
		call cp drivers/amlogic/ump/ump.ko $ROOTFS_OUT/root/boot/
		call cp drivers/amlogic/wifi/rtl8xxx_CU/8192cu.ko $ROOTFS_OUT/system/lib/
		call cp net/wireless/cfg80211.ko $ROOTFS_OUT/system/lib/
		call cp drivers/scsi/scsi_wait_scan.ko $ROOTFS_OUT/system/lib/

		logger "FINISH make modules"
	else
		logger "ERROR make modules"
		exit 1
	fi
}

kernel_make()
{
	logger "START make kernel"
	LOG_LOGGER=$LOG_KERNEL
	call cd $KERNEL_SRC
	modules_make
	call make uImage $MAKE_ARGS
	if [ $? -eq 0 ]; then
		call cp ./arch/arm/boot/uImage $BUILD_OUT
		logger "FINISH make kernel"
	else
		logger "ERROR make kernel"
		exit 1
	fi
}

bootable_make()
{
	logger "START make bootable recovery"
	LOG_LOGGER=$LOG_ROOTFS
	call cd $ANDROID_SRC
	if [ $REBUILD_FLAG -eq 1 ]; then
		mmm $ANDROID_SRC/bootable/recovery -B
	else
		mmm $ANDROID_SRC/bootable/recovery
	fi
	if [ $? -eq 0 ]; then
		call cp -f $ROOTFS_OUT/system/bin/recovery $ROOTFS_OUT/recovery/root/sbin/
		logger "FINISH make bootable recovery"
	else
		logger "ERROR make bootable recovery"
	fi
}

recovery_make()
{
	bootable_make
	logger "START make recovery"
	LOG_LOGGER=$LOG_KERNEL
	call cd $KERNEL_SRC

	if [ $REBUILD_FLAG -eq 1 ]; then
		call make distclean
	fi
	call make $KERNEL_CONFIG

    match=`sed -n "s|^CONFIG_BLK_DEV_INITRD=y$|&|gp" .config`
    if [ -n "$match" ]; then
        echo ">>> initramfs selected in kernel config"
        rootfsconfig1="CONFIG_INITRAMFS_SOURCE=\"$ROOTFS_OUT/recovery/root\""
        sed -e "s|^.*CONFIG_INITRAMFS_SOURCE=\".*\".*$|$rootfsconfig1|g" .config > tmpconfig
        call cp tmpconfig .config
    fi

	call make uImage $MAKE_ARGS
	if [ $? -eq 0 ]; then
		call cp ./arch/arm/boot/uImage $BUILD_OUT/uImage_recovery
		logger "FINISH make recovery"
	else
		logger "ERROR make recovery"
		exit 1
	fi
}

dbstar_patch()
{
	logger "START patch dbstar"
	call cd $ANDROID_SRC
	echo ">>>> patching bionic ..."
	cp -rf $DBSTAR_SRC/bionic/* $ANDROID_SRC/bionic/
	echo ">>>> patching frameworks ..."
	cp -rf $DBSTAR_SRC/frameworks/* $ANDROID_SRC/frameworks/
	echo ">>>> patching kernel ..."
	cp -rf $DBSTAR_SRC/kernel/* $ANDROID_SRC/kernel/
	echo ">>>> patching device ..."
	cp -rf $DBSTAR_SRC/device/* $ANDROID_SRC/device/
	echo ">>>> patching build ..."
	cp -rf $DBSTAR_SRC/build/* $ANDROID_SRC/build/
	logger "FINISH patch dbstar"
}

dbstar_make()
{
	logger "START make dbstar"
	LOG_LOGGER=$LOG_ROOTFS
	call cd $ANDROID_SRC
	call mmm $DBSTAR_SRC/rootfs
	if [ $REBUILD_FLAG -eq 1 ]; then
		call mmm $DBSTAR_SRC/DbstarDVB -B
		call mmm $DBSTAR_SRC/DbstarLauncher -B
		call mmm $DBSTAR_SRC/DbstarSettings -B
	else
		call mmm $DBSTAR_SRC/DbstarDVB
		call mmm $DBSTAR_SRC/DbstarLauncher
		call mmm $DBSTAR_SRC/DbstarSettings
	fi
	if [ $? -eq 0 ]; then
		logger "FINISH make dbstar"
	else
		logger "ERROR make dbstar"
	fi
}

autobuild()
{
	logger "******************** start..."
	mkdir -p $BUILD_OUT
	lunch_setup

	if [ $AUTOBUILD_FLAG -eq $BUILD_FLAG_PATCH ]; then
		dbstar_patch
	fi
	if [ $AUTOBUILD_FLAG -eq $BUILD_FLAG_ROOTFS ]; then
		rootfs_make
	fi
	if [ $AUTOBUILD_FLAG -eq $BUILD_FLAG_DBSTAR ]; then
		dbstar_make
	fi
	if [ $AUTOBUILD_FLAG -eq $BUILD_FLAG_KERNEL ]; then
		kernel_make
	fi
	if [ $AUTOBUILD_FLAG -eq $BUILD_FLAG_RECOVERY ]; then
		recovery_make
	fi
	if [ $AUTOBUILD_FLAG -eq $BUILD_FLAG_OTAPACKAGE ]; then
		otapackage_make
	fi
	if [ $AUTOBUILD_FLAG -eq $BUILD_FLAG_ALL ]; then
		dbstar_patch
		dbstar_make
		kernel_make
		recovery_make
		otapackage_make
	fi
	if [ $AUTOBUILD_FLAG -eq $BUILD_FLAG_RELEASE ]; then
		rootfs_clean
		dbstar_patch
		rootfs_make
		dbstar_make
		kernel_make
		recovery_make
		otapackage_make
	fi

	logger "******************** done."
}

dumpinfo()
{
	echo "=================== Building info ================"
	echo "    build time:    $TIMESTAMP"
	echo "    build module:  $1"
	echo "    source path:   $ANDROID_SRC"
	echo "    output path:   $BUILD_OUT"
	echo "    lunch config:  $ANDROID_LUNCH"
	echo "    kernel config: $KERNEL_CONFIG"
	echo "=================================================="
}

help()
{
	cat << HELP
Usage:   $0 MODULE [-B] [-h]

Auto build android system.
  MODULE                     module name, e.g. [kernel|recovery|rootfs|otapackage]
  -h                         help info
  -B                         rebuild, make clean and make
Example: $0 kernel       # build kernel
         $0 recovery     # build recovery kernel
         $0 rootfs       # build system rootfs
         $0 otapackage   # build otapackage
         $0 dbstar       # build dbstar package
         $0 patch        # patch dbstar kernel and device
         $0 all          # patch and build dbstar/kernel/otapackage
         $0 release      # patch, clean, and rebuild all


HELP
	exit 0
}

do_select()
{
	echo "Autobuild android system:"
	echo "Please select:"
	select var in "patch" "dbstar" "kernel" "recovery" "rootfs" "otapackage" "all" "release"; do
		break
	done
	echo "You have selected $var"
	check_args  $var
}

check_args()
{
	if [ $1 = "-h" ]; then
		help
	elif [ $1 = "kernel" ]; then
		AUTOBUILD_FLAG=$BUILD_FLAG_KERNEL
	elif [ $1 = "recovery" ]; then
		AUTOBUILD_FLAG=$BUILD_FLAG_RECOVERY
	elif [ $1 = "rootfs" ]; then
		AUTOBUILD_FLAG=$BUILD_FLAG_ROOTFS
	elif [ $1 = "patch" ]; then
		AUTOBUILD_FLAG=$BUILD_FLAG_PATCH
	elif [ $1 = "dbstar" ]; then
		AUTOBUILD_FLAG=$BUILD_FLAG_DBSTAR
	elif [ $1 = "otapackage" ]; then
		AUTOBUILD_FLAG=$BUILD_FLAG_OTAPACKAGE
	elif [ $1 = "all" ]; then
		AUTOBUILD_FLAG=$BUILD_FLAG_ALL
	elif [ $1 = "release" ]; then
		AUTOBUILD_FLAG=$BUILD_FLAG_RELEASE
	else
		help
	fi

	if [ "$2" = "-B" ]; then
		REBUILD_FLAG=1
	elif [ "$2" = "-v" ]; then
		VERBOSE_FLAG=1
	fi

	if [ "$3" = "-v" ]; then
		VERBOSE_FLAG=1
	fi
}

#################################################################################
# auto building android
#################################################################################
if [ $# -eq 0 ]; then
	do_select
elif [ $# -eq 1 ]; then
	check_args $1
elif [ $# -eq 2 ]; then
	check_args $1 $2
elif [ $# -eq 3 ]; then
	check_args $1 $2 $3
else
	help
fi

dumpinfo $1
autobuild
