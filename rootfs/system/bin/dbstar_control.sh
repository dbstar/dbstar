#!/system/bin/sh

system_init()
{
	setprop sys.mediascanner.enable false
	setprop persist.sys.strictmode.visual 0
	setprop persist.sys.strictmode.disable 1
	setprop dbstar.deepsleep 0
}

dbstar_init()
{
	if [ -e "/system/app/Launcher2.apk" ]; then
		mv /system/app/Launcher2.apk /system/app/Launcher2.apk.bk
	fi

	if [ -s "/data/dbstar/Dbstar.db" ]; then
		echo "dbstar already inited!"
	else
		cp -rf /system/etc/dbstar/Dbstar.db /data/dbstar/
		sync
		chown system /data/dbstar/Dbstar.db
		chmod 666 /data/dbstar/Dbstar.db
		setprop dbstar.inited 1
	fi

	if [ -s "/data/dbstar/Smarthome.db" ]; then
		echo "Smarthome.db already inited!"
	else
		cp -rf /system/etc/dbstar/Smarthome.db /data/dbstar/
		sync
		chown system /data/dbstar/Smarthome.db
		chmod 666 /data/dbstar/Smarthome.db
		setprop dbstar.inited 1
	fi

	if [ -e "/data/dbstar/ColumnRes" ]; then
		echo "ColumnRes already inited!"
	else
		cp -rf /system/etc/dbstar/ColumnRes /data/dbstar/
		sync
		chown system /data/dbstar/ColumnRes
		chmod 777 /data
		chmod 777 /data/dbstar
		chmod 777 /data/dbstar/ColumnRes
		chmod 777 /data/dbstar/ColumnRes/LocalColumnIcon
		chmod 777 /data/dbstar/ColumnRes/LocalColumnIcon/*
		setprop dbstar.inited 1
	fi

	if [ -e "/data/dbstar/drm" ]; then
		echo "drm entitle already inited!"
	else
		cp -rf /system/etc/dbstar/drm /data/dbstar/
		sync
		chown system /data/dbstar/drm
		chmod 777 /data
		chmod 777 /data/dbstar
		chmod 777 /data/dbstar/drm
		chmod 777 /data/dbstar/drm/entitle
		chmod 777 /data/dbstar/drm/entitle/block01
		setprop dbstar.inited 1
	fi


	if [ -e "/data/misc/dhcp/dhcpcd-eth0.lease" ]; then
		rm /data/misc/dhcp/dhcpcd-eth0.lease
	fi
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
