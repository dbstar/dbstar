#!/system/bin/sh

system_init()
{
	setprop sys.mediascanner.enable false
	setprop persist.sys.strictmode.visual 0
	setprop persist.sys.strictmode.disable 1
}

dbstar_init()
{
	mv /system/app/SystemUI.apk /system/app/SystemUI.apk.bk
	mv /system/app/Launcher2.apk /system/app/Launcher2.apk.bk
	if [ -e "/data/dbstar/Dbstar.db" ]; then
		echo "dbstar already inited!"
	else
		cp -rf /system/etc/dbstar/Dbstar.db /data/dbstar/
		chmod 666 /data/dbstar/Dbstar.db
		setprop dbstar.inited 1
	fi
	if [ -e "/data/dbstar/Smarthome.db" ]; then
		echo "Smarthome.db already inited!"
	else
		cp -rf /system/etc/dbstar/Smarthome.db /data/dbstar/
		chmod 666 /data/dbstar/Smarthome.db
		setprop dbstar.inited 1
	fi
}


dbstar_start()
{
#	setprop dbstar.dvbpush.started 1
#	setprop dbstar.smarthome.started 1
}

dbstar_stop()
{
	setprop dbstar.dvbpush.started 0
	setprop dbstar.smarthome.started 0
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
