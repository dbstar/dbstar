#!/system/bin/sh

system_init()
{
	setprop sys.mediascanner.enable false
	setprop persist.sys.strictmode.visual 0
	setprop persist.sys.strictmode.disable 1
	setprop dbstar.deepsleep 0
	setprop libplayer.ts.softdemux true
	setprop media.p2pplay.enable false
}

dbstar_init()
{
	if [ -e "/system/app/Launcher2.apk" ]; then
		mount -o remount,rw /system
		mv /system/app/Launcher2.apk /system/app/Launcher2.apk.bk
		mv /system/app/dongle_launcher.apk /system/app/dongle_launcher.apk.bk
	fi

	if [ -s "/data/dbstar/Dbstar.db" ]; then
		echo "dbstar already inited!"
	else
		cp -rf /system/etc/dbstar/Dbstar.db /data/dbstar/
		sync
		chown system:system /data/dbstar/Dbstar.db
		chmod 666 /data/dbstar/Dbstar.db
		setprop dbstar.inited 1
	fi

#	if [ -s "/data/dbstar/Smarthome.db" ]; then
#		echo "Smarthome.db already inited!"
#	else
#		cp -rf /system/etc/dbstar/Smarthome.db /data/dbstar/
#		sync
#		chown system:system /data/dbstar/Smarthome.db
#		chmod 666 /data/dbstar/Smarthome.db
#		setprop dbstar.inited 1
#	fi

	if [ -e "/data/dbstar/ColumnRes" ]; then
		echo "ColumnRes already inited!"
	else
		cp -rf /system/etc/dbstar/ColumnRes /data/dbstar/
		sync
		chown system:system /data/dbstar/ColumnRes
		chmod 777 /data
		chmod 777 /data/dbstar
		chmod 777 /data/dbstar/ColumnRes
		chmod 777 /data/dbstar/ColumnRes/LocalColumnIcon
		chmod 644 /data/dbstar/ColumnRes/LocalColumnIcon/*
		setprop dbstar.inited 1
	fi

        if [ -e "/data/dbstar/ColumnRes/Books_losefocus.png" ]; then
                echo "ColumnRes/Books_losefocus.png already inited!"
        else
                cp -rf /system/etc/dbstar/ColumnRes/*.png /data/dbstar/ColumnRes
                sync
                chown system /data/dbstar/ColumnRes
                chmod 777 /data/dbstar/ColumnRes/*.png
                setprop dbstar.inited 1
        fi
 
	if [ -e "/data/dbstar/drm" ]; then
		echo "drm entitle already inited!"
	else
		cp -rf /system/etc/dbstar/drm /data/dbstar/
		sync
		chown system:system /data/dbstar/drm
		chmod 777 /data
		chmod 777 /data/dbstar
		chmod 777 /data/dbstar/drm
		chmod 777 /data/dbstar/drm/entitle
		chmod 777 /data/dbstar/drm/entitle/block01
		setprop dbstar.inited 1
	fi

	if [ -e "/data/dbstar/dbstar.conf" ]; then
		echo "dbstar.conf already exist!"
	else
		cp -rf /system/etc/dbstar/dbstar.conf /data/dbstar/
		sync
		chown system:system /data/dbstar/dbstar.conf
		chmod 644 /data/dbstar/dbstar.conf
	fi

	if [ -e "/data/dbstar/push.conf" ]; then
		echo "push.conf already exist!"
	else
		cp -rf /system/etc/dbstar/push.conf /data/dbstar/
		sync
		chown system:system /data/dbstar/push.conf
		chmod 644 /data/dbstar/push.conf
	fi


	if [ -e "/data/misc/dhcp/dhcpcd-eth0.lease" ]; then
		rm /data/misc/dhcp/dhcpcd-eth0.lease
	fi

        setprop service.adb.tcp.port 5555
        stop adbd
        start adbd
}


dbstar_start()
{
	setprop service.smarthome.started 1
}

dbstar_stop()
{
	setprop service.smarthome.started 0
}

case $1 in
	init) 
		system_init;
		dbstar_init;
		;;
	start) 
#sleep 3;
		dbstar_start;
		;;
	stop) 
		dbstar_stop;
		;;
	restart) 
		dbstar_stop;
		dbstar_start;
		;;
	*)
		echo "Error param."
		;;
esac
