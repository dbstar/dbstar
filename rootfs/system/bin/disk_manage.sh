#!/system/bin/sh
#
# File: disk_manager.sh
#     Android Disk Manager
# 

#################################################################################
# Disk Manager Command
#################################################################################
DISK_CMD_ADD=1
DISK_CMD_DEL=2
DISK_CMD_FORMAT=3
DISK_MANAGER_CMD=0

#DISK_DEVS="/dev/sda /dev/sdb /dev/sdc"
DISK_DEVS="/dev/block/sda /dev/block/sda1 /dev/block/sdb /dev/block/sdc"
DISK_MANAGER_DEV=""
DISK_MANAGER_MNT=""


help()
{
	cat << HELP
Usage:
  $0 <cmd> <disk>
  $0 [-h]

Android Disk Manage Tool.
  -a       Add NTFS disk partition and format.
  -d       Delete disk partition.
  -h       U are reading it right now.

Example: 
  $0 -a /dev/sdc

HELP
	exit 0
}

stmt_create()
{
	if [ -d /sqlite_stmt_journals ]; then
		return;
	else
		mkdir -r /sqlite_stmt_journals;
	fi
}

stmt_unlink()
{
	if [ -d /sqlite_stmt_journals ]; then
		rm -rf /sqlite_stmt_journals;
	fi
}

partition_add()
{
stmt_create
echo "*** Disk Add: $1 ***"
fdisk $1 << EOF
n
p
1


t
7
w
EOF
#stmt_unlink
}

partition_del()
{
stmt_create
echo "*** Disk Delete: $1 ***"
fdisk $1 << EOF
d
w
EOF
#stmt_create
}

unmount()
{
	echo "*** Disk Unmount $1 ***"
	umount $1
}

ntfs_format()
{
	echo "*** Disk Format $1 to NTFS ***"
	mkntfs -f $1
}

check_dev()
{
	for var in $DISK_DEVS; do
		if [ $1 = $var ]; then
			DISK_MANAGER_DEV=$var
			break;
		fi
	done
	if [ -z $DISK_MANAGER_DEV ]; then
		echo "*** Wrong disk dev path. Exit."
		exit 1;
	fi
}

check_args()
{
	if [ $1 = "-h" ]; then
		help
	elif [ $1 = "-a" ]; then
		DISK_MANAGER_CMD=$DISK_CMD_ADD
	elif [ $1 = "-d" ]; then
		DISK_MANAGER_CMD=$DISK_CMD_DEL
	elif [ $1 = "-f" ]; then
		DISK_MANAGER_CMD=$DISK_CMD_FORMAT
	else
		help
	fi

	check_dev $2
}

get_disk_manage_param()
{
	DMCMD=$(getprop service.disk_manage.cmd)
	DMDEV=$(getprop service.disk_manage.dev)

	if [ $DMCMD = "add" ]; then
		DISK_MANAGER_CMD=$DISK_CMD_ADD
	elif [ $DMCMD = "del" ]; then
		DISK_MANAGER_CMD=$DISK_CMD_DEL
	elif [ $DMCMD = "format" ]; then
		DISK_MANAGER_CMD=$DISK_CMD_FORMAT
	else
		echo "*** Wrong disk manage cmd. Exit."
	fi
	check_dev $DMDEV
}

stop_disk_manage()
{
	setprop service.disk_manage.state stopped
}

#if [ $# -eq 2 ]; then
#	check_args $1 $2
#else
#	help
#fi

get_disk_manage_param

if [ $DISK_MANAGER_CMD -eq $DISK_CMD_FORMAT ]; then
#	DISK_MANAGER_DEV=$DISK_MANAGER_DEV"1"
	ntfs_format $DISK_MANAGER_DEV
elif [ $DISK_MANAGER_CMD -eq $DISK_CMD_ADD ]; then
	partition_add $DISK_MANAGER_DEV
	DISK_MANAGER_DEV=$DISK_MANAGER_DEV"1"
	ntfs_format $DISK_MANAGER_DEV
elif [ $DISK_MANAGER_CMD -eq $DISK_CMD_DEL ]; then
	DISK_MANAGER_MNT="/mnt/"$(basename "$DISK_MANAGER_DEV")"1"
	echo "$DISK_MANAGER_MNT"
	unmount $DISK_MANAGER_MNT
	partition_del $DISK_MANAGER_DEV
fi

stop_disk_manage

echo "*** Disk Manage Done ***"
