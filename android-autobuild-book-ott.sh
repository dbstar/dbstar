#!/bin/bash
#
# File:   aundroid-autobuild.sh
#     Android autobuild script.
# Author: jiangpeifu@126.com
# 


#################################################################################
# basic configs
#################################################################################
TIMESTAMP=`date +%Y%m%d`
BASEDIR="`pwd`/../../.."
ANDROID_SRC="`pwd`/../.."
UBOOT_SRC=$BASEDIR/uboot
DBSTAR_SRC=$ANDROID_SRC/packages/dbstar
BUILD_OUT=$BASEDIR/buildout/$TIMESTAMP
ROOTFS_OUT=$ANDROID_SRC/out/target/product/g18ref
LOG_BUILD=$BUILD_OUT/build.log
LOG_REPO=$BUILD_OUT/repo.log
LOG_DBSTAR=$BUILD_OUT/dbstar.log
LOG_UBOOT=$BUILD_OUT/uboot.log
LOG_KERNEL=$BUILD_OUT/kernel.log
LOG_ROOTFS=$BUILD_OUT/rootfs.log
LOG_OTAPACKAGE=$BUILD_OUT/otapackage.log
LOG_LOGGER=$LOG_BUILD

GIT_SREVER="git://git.myamlogic.com/platform/manifest.git"
GIT_BRANCH="jb-mr1-amlogic"
REPO_URL="git://10.8.9.5/tools/repo.git"
ANDROID_LUNCH="15"
UBOOT_CONFIG="m6_mbox_v1_config"
KERNEL_CONFIG="meson6_g18_jbmr1_defconfig"
MAKE_ARGS=""


#################################################################################
# building flags
#################################################################################
BUILD_FLAG_CHECKOUT=1
BUILD_FLAG_PATCH=2
BUILD_FLAG_KERNEL=3
BUILD_FLAG_RECOVERY=4
BUILD_FLAG_ROOTFS=5
BUILD_FLAG_DBSTAR=6
BUILD_FLAG_OTAPACKAGE=7
BUILD_FLAG_ALL=998
BUILD_FLAG_RELEASE=999

AUTOBUILD_FLAG=0
AUTOBUILD_TYPE=""

#  0: donot clean
#  1: clean and make
REBUILD_FLAG=0

#  0: no log
#  1: log stdout
#  2: log into file
VERBOSE_FLAG=2


#################################################################################
# module auto build functions
#################################################################################
call()
{
	echo ">>> $@" 
	if [ $VERBOSE_FLAG -eq 1 ]; then
		$@
		return $?
	elif [ $VERBOSE_FLAG -eq 2 ]; then
		$@ 1>>$LOG_LOGGER 2>&1
		return $?
	else
		$@ > /dev/null 2>&1
		return $?
	fi
}

logger()
{
	echo "********** [`date`] $@"
}

checkout()
{
	logger "START Android repo checkout"
	LOG_LOGGER=$LOG_REPO.$TIMESTAMP
	call mkdir -p $ANDROID_SRC
	call cd $ANDROID_SRC
	call repo init --repo-url=$REPO_URL -u $GIT_SREVER -b $GIT_BRANCH

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
	LOG_LOGGER=$LOG_REPO.$TIMESTAMP
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
	LOG_LOGGER=$LOG_UBOOT.$TIMESTAMP
	call cd $UBOOT_SRC
	call rm -rf build
	call make $UBOOT_CONFIG
	call make $MAKE_ARGS
	if [ $? -eq 0 ]; then
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
	LOG_LOGGER=$LOG_ROOTFS.$TIMESTAMP
	call cd $ANDROID_SRC
	call rm -rf ./out
	logger "FINISH clean rootfs"
}

rootfs_make()
{
	logger "START make rootfs"
	LOG_LOGGER=$LOG_ROOTFS.$TIMESTAMP

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

	LOG_LOGGER=$LOG_OTAPACKAGE.$TIMESTAMP
        cp /home/mx/app/CNTV_DBSTAR_Android4.03_06_07.apk $ROOTFS_OUT/system/app/.
        cp /home/mx/app/AppStore_DBSTAR_3.3.3.apk $ROOTFS_OUT/system/app/.
        cp /home/mx/m6-book/TWReader.apk $ROOTFS_OUT/system/app/.
        cp /home/mx/m6-book/libAisound.so $ROOTFS_OUT/system/lib/.
        cp /home/mx/m6-book/Resource.irf $ROOTFS_OUT/system/lib/.

	call cd $ANDROID_SRC
	call make otapackage $MAKE_ARGS
	if [ $? -eq 0 ]; then
		logger "FINISH make otapackage"
		call cp $ROOTFS_OUT/*.zip $BUILD_OUT
		call cp $ROOTFS_OUT/boot.img $BUILD_OUT
		call cp $ROOTFS_OUT/recovery.img $BUILD_OUT
		call cp -rf $ROOTFS_OUT/root $BUILD_OUT
		call cp $ROOTFS_OUT/u-boot.bin $BUILD_OUT
	else
		logger "ERROR make otapackage"
		exit 1
	fi
}

kernel_make()
{
	logger "START make kernel"
	LOG_LOGGER=$LOG_KERNEL.$TIMESTAMP
	call cd $ANDROID_SRC
	call make bootimage $MAKE_ARGS
	if [ $? -eq 0 ]; then
		call cp $ROOTFS_OUT/boot.img $BUILD_OUT
		logger "FINISH make kernel"
	else
		logger "ERROR make kernel"
		exit 1
	fi
}

bootable_make()
{
	logger "START make bootable recovery"
	LOG_LOGGER=$LOG_ROOTFS.$TIMESTAMP
	call cd $ANDROID_SRC
	if [ $REBUILD_FLAG -eq 1 ]; then
		call mmm $ANDROID_SRC/bootable/recovery -B
	else
		call mmm $ANDROID_SRC/bootable/recovery
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
	LOG_LOGGER=$LOG_KERNEL.$TIMESTAMP
	call cd $ANDROID_SRC
	call make recoveryimage $MAKE_ARGS

	if [ $? -eq 0 ]; then
		call cp $ROOTFS_OUT/recovery.img $BUILD_OUT
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
	echo ">>>> patching android ..."
	cp -rf $DBSTAR_SRC/android/* $ANDROID_SRC/
	logger "FINISH patch dbstar"
}

dbstar_make()
{
	logger "START make dbstar"
	LOG_LOGGER=$LOG_DBSTAR.$TIMESTAMP
	call cd $ANDROID_SRC
	call mmm $DBSTAR_SRC/rootfs
	call mmm $ANDROID_SRC/external/dvb
	if [ $? -ne 0 ]; then
		logger "ERROR make rootfs"
	fi
	if [ $REBUILD_FLAG -eq 1 ]; then
		call mmm $DBSTAR_SRC/DbstarDVB -B
		call mmm $DBSTAR_SRC/DbstarLauncher -B
		call mmm $DBSTAR_SRC/DbstarSettings -B
		call mmm $DBSTAR_SRC/DBStarAppManager -B
		call mmm $DBSTAR_SRC/DbstarFileBrowser -B
		call mmm $DBSTAR_SRC/MultipleMediaReader -B
		call mmm $DBSTAR_SRC/OTTSettings -B
	else
		call mmm $DBSTAR_SRC/DbstarDVB
		if [ $? -ne 0 ]; then
			logger "ERROR make DbstarDVB"
		fi
		call mmm $DBSTAR_SRC/DbstarLauncher
		if [ $? -ne 0 ]; then
			logger "ERROR make DbstarLauncher"
		fi
		call mmm $DBSTAR_SRC/DbstarSettings
		if [ $? -ne 0 ]; then
			logger "ERROR make DbstarSettings"
		fi
		call mmm $DBSTAR_SRC/DBStarAppManager
		if [ $? -ne 0 ]; then
			logger "ERROR make DBStarAppManager"
		fi
		call mmm $DBSTAR_SRC/DbstarFileBrowser
		if [ $? -ne 0 ]; then
			logger "ERROR make DbstarFileBrowser"
		fi
		call mmm $DBSTAR_SRC/MultipleMediaReader
		if [ $? -ne 0 ]; then
			logger "ERROR make MultipleMediaReader"
		fi
		call mmm $DBSTAR_SRC/OTTSettings
		if [ $? -ne 0 ]; then
			logger "ERROR make OTTSettings"
		fi
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

	if [ $AUTOBUILD_FLAG -eq $BUILD_FLAG_CHECKOUT ]; then
		checkout
	else
		lunch_setup
	fi

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
#		dbstar_patch
		rootfs_make
		dbstar_make
		otapackage_make
	fi
	if [ $AUTOBUILD_FLAG -eq $BUILD_FLAG_RELEASE ]; then
		REBUILD_FLAG=1
		dbstar_patch
		rootfs_make
		dbstar_make
		otapackage_make
	fi

	logger "******************** done."
}

dumpinfo()
{
	echo "=================== Building info ================"
	echo "    build time:    $TIMESTAMP"
	echo "    build tpye:    $AUTOBUILD_TYPE"
	echo "    build args:    $MAKE_ARGS"
	echo "    build flag:    $VERBOSE_FLAG"
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
	select var in "checkout" "patch" "dbstar" "kernel" "recovery" "rootfs" "otapackage" "all" "release"; do
		break
	done
	echo "You have selected $var"
	check_args  $var
}

check_args()
{
	arg1=$1;
	arg2=$2;
	arg3=$3;
	if [ $arg1 = "-h" ]; then
		help
	elif [ $arg1 = "-s" ]; then
		VERBOSE_FLAG=0
	elif [ $arg1 = "-v" ]; then
		VERBOSE_FLAG=1
	elif [ "$arg1" = "-B" ]; then
		REBUILD_FLAG=1
	elif [ ${arg1:0:2} = "-j" ]; then
		MAKE_ARGS=$1
	elif [ $arg1 = "checkout" ]; then
		AUTOBUILD_FLAG=$BUILD_FLAG_CHECKOUT
		AUTOBUILD_TYPE=$arg1
	elif [ $arg1 = "kernel" ]; then
		AUTOBUILD_FLAG=$BUILD_FLAG_KERNEL
		AUTOBUILD_TYPE=$arg1
	elif [ $arg1 = "recovery" ]; then
		AUTOBUILD_FLAG=$BUILD_FLAG_RECOVERY
		AUTOBUILD_TYPE=$arg1
	elif [ $arg1 = "rootfs" ]; then
		AUTOBUILD_FLAG=$BUILD_FLAG_ROOTFS
		AUTOBUILD_TYPE=$arg1
	elif [ $arg1 = "patch" ]; then
		AUTOBUILD_FLAG=$BUILD_FLAG_PATCH
		AUTOBUILD_TYPE=$arg1
	elif [ $arg1 = "dbstar" ]; then
		AUTOBUILD_FLAG=$BUILD_FLAG_DBSTAR
		AUTOBUILD_TYPE=$arg1
	elif [ $arg1 = "otapackage" ]; then
		AUTOBUILD_FLAG=$BUILD_FLAG_OTAPACKAGE
		AUTOBUILD_TYPE=$arg1
	elif [ $arg1 = "all" ]; then
		AUTOBUILD_FLAG=$BUILD_FLAG_ALL
		AUTOBUILD_TYPE=$arg1
	elif [ $arg1 = "release" ]; then
		AUTOBUILD_FLAG=$BUILD_FLAG_RELEASE
		AUTOBUILD_TYPE=$arg1
	else
		help
	fi

	if [ "$arg2" = "-B" ]; then
		REBUILD_FLAG=1
	elif [ "$arg2" = "-s" ]; then
		VERBOSE_FLAG=0
	elif [ "$arg2" = "-v" ]; then
		VERBOSE_FLAG=1
	elif [ "${arg2:0:2}" = "-j" ]; then
		MAKE_ARGS=$arg2
	fi

	if [ "$arg3" = "-B" ]; then
		REBUILD_FLAG=1
	elif [ "$arg3" = "-s" ]; then
		VERBOSE_FLAG=0
	elif [ "$arg3" = "-v" ]; then
		VERBOSE_FLAG=1
	elif [ "${arg3:0:2}" = "-j" ]; then
		MAKE_ARGS=$arg3
	fi

	if [ $AUTOBUILD_FLAG -eq 0 ]; then
		do_select
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

dumpinfo
autobuild
