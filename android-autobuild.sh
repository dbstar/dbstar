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
#MAKE_ARGS="-j5"
TIMESTAMP=`date +%Y%m%d`


#################################################################################
# building flags
#################################################################################
#  0x1: kernel 
#  0x2: recovery
#  0x4: rootfs
#  0x8: otapackage
# 0x10: dbstar
# 0x20: patch
AUTOBUILD_FLAG=0

#  0x0: donot clean
#  0x1: clean and make
REBUILD_FLAG=0


#################################################################################
# module auto build functions
#################################################################################
call()
{
    echo ">>> $@" 
	$@ 1>>$LOG_LOGGER 2>&1
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
	call repo init --repo-url=git://10.8.9.8/tools/repo.git -u $GIT_SREVER -b $GIT_BRANCH
	call repo sync $MAKE_ARGS
	call repo start $GIT_BRANCH-$TIMESTAMP --all
	call repo manifest -r -o $BUILD_OUT/$GIT_BRANCH-$TIMESTAMP.xml
	logger "FINISH Android repo checkout"
}

repo_sync()
{
	logger "START Android repo sync"
	LOG_LOGGER=$LOG_REPO
	call cd $ANDROID_SRC
	call repo sync $MAKE_ARGS
	call repo start $GIT_BRANCH --all
	call repo manifest -r -o $BUILD_OUT/$GIT_BRANCH-$TIMESTAMP.xml
	logger "FINISH Android repo checkout"
	logger "FINISH Android repo sync"
}

uboot_make()
{
	logger "START make uboot"
	LOG_LOGGER=$LOG_UBOOT
	call cd $UBOOT_SRC
	call rm -rf build
	call make $UBOOT_CONFIG
	call make $MAKE_ARGS
	call mkdir -p $BUILD_OUT
	call cp ./build/u-boot-aml-ucl.bin $BUILD_OUT
	logger "FINISH make uboot"
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
	call source ./build/envsetup.sh
	call lunch $ANDROID_LUNCH
	
	if [ $REBUILD_FLAG -eq 1 ]; then
		rootfs_clean
	fi
	call make $MAKE_ARGS
	if [ $? -eq 0 ]; then
		logger "FINISH make rootfs"
	else
		logger "ERROR make rootfs"
	fi
}

otapackage_make()
{
	logger "START make otapackage"

	LOG_LOGGER=$LOG_OTAPACKAGE
	call cp $BUILD_OUT/uImage $ROOTFS_OUT
	call cp $BUILD_OUT/uImage_recovery $ROOTFS_OUT

	call cd $ANDROID_SRC
	call source ./build/envsetup.sh
	call lunch $ANDROID_LUNCH
	call make otapackage $MAKE_ARGS
	if [ $? -eq 0 ]; then
		logger "FINISH make otapackage"
		call cp $ROOTFS_OUT/*.zip $BUILD_OUT
	else
		logger "ERROR make otapackage"
	fi
}

modules_make()
{
	logger "START make modules"
	LOG_LOGGER=$LOG_KERNEL
	call cd $KERNEL_SRC
	if [ $REBUILD_FLAG -eq 1 ]; then
		call make distclean
		call make $KERNEL_CONFIG
	fi
	call make uImage $MAKE_ARGS
	call make modules

	if [ $? -eq 0 ]; then
		call cp drivers/amlogic/mali/mali.ko $BUILD_OUT
		call cp drivers/amlogic/ump/ump.ko $BUILD_OUT
		call cp drivers/amlogic/wifi/rtl8xxx_CU/8192cu.ko $BUILD_OUT
		call cp drivers/scsi/scsi_wait_scan.ko $BUILD_OUT

		call cp drivers/amlogic/mali/mali.ko $ROOTFS_OUT/root/boot/
		call cp drivers/amlogic/ump/ump.ko $ROOTFS_OUT/root/boot/
		call cp drivers/amlogic/wifi/rtl8xxx_CU/8192cu.ko $ROOTFS_OUT/system/lib/
		call cp drivers/scsi/scsi_wait_scan.ko $ROOTFS_OUT/system/lib/

		logger "FINISH make modules"
	else
		logger "ERROR make modules"
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
	fi
}

recovery_make()
{
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
	fi
}

dbstar_patch()
{
	logger "START patch dbstar"
	call cd $ANDROID_SRC
	echo ">>>> patching kernel..."
	cp -rf $DBSTAR_SRC/kernel/* $ANDROID_SRC/kernel/
	echo ">>>> patching device..."
	cp -rf $DBSTAR_SRC/device/* $ANDROID_SRC/device/
	logger "FINISH patch dbstar"
}

dbstar_make()
{
	logger "START make dbstar"
	LOG_LOGGER=$LOG_ROOTFS
	call cd $ANDROID_SRC
	call source ./build/envsetup.sh
	call lunch $ANDROID_LUNCH
	mmm $DBSTAR_SRC/DbstarDVB
	mmm $DBSTAR_SRC/DbstarLauncher
	mmm $DBSTAR_SRC/DbstarSettings
	mmm $DBSTAR_SRC/rootfs
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

	if [ $AUTOBUILD_FLAG -eq 16 ]; then
		dbstar_patch
	fi
	if [ $AUTOBUILD_FLAG -eq 32 ]; then
		dbstar_make
	fi
	if [ $AUTOBUILD_FLAG -eq 4 ]; then
		rootfs_make
	fi
	if [ $AUTOBUILD_FLAG -eq 1 ]; then
		kernel_make
	fi
	if [ $AUTOBUILD_FLAG -eq 2 ]; then
		recovery_make
	fi
	if [ $AUTOBUILD_FLAG -eq 8 ]; then
		otapackage_make
	fi
	if [ $AUTOBUILD_FLAG -eq 15 ]; then
		rootfs_make
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
         $0 otapackage   # build system rootfs
         $0 dbstar       # build dbstar package
         $0 patch        # patch dbstar kernel and device


HELP
	exit 0
}

check_args()
{
	if [ $1 = "-h" ]; then
		help
	elif [ $1 = "kernel" ]; then
		AUTOBUILD_FLAG=1
	elif [ $1 = "recovery" ]; then
		AUTOBUILD_FLAG=2
	elif [ $1 = "rootfs" ]; then
		AUTOBUILD_FLAG=4
	elif [ $1 = "otapackage" ]; then
		AUTOBUILD_FLAG=8
	elif [ $1 = "patch" ]; then
		AUTOBUILD_FLAG=16
	elif [ $1 = "dbstar" ]; then
		AUTOBUILD_FLAG=32
	elif [ $1 = "all" ]; then
		AUTOBUILD_FLAG=15
	fi

	if [ "$2" = "-B" ]; then
		logger 
		REBUILD_FLAG=1
	fi
}

#################################################################################
# auto building android
#################################################################################
if [ $# -eq 1 ]; then
	check_args $1
elif [ $# -eq 2 ]; then
	check_args $1 $2
else
	help
fi

dumpinfo $1
autobuild
