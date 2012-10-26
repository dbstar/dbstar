#!/system/bin/sh

system_init()
{
	setprop sys.mediascanner.enable false
	setprop persist.sys.strictmode.visual 0
	setprop persist.sys.strictmode.disable 1
}

dbstar_init()
{
	if [ -e "/data/dbstar/dbstar.conf" ]; then
		echo "dbstar.conf already exist!"
	else
		cp /system/etc/dbstar/dbstar.conf /data/dbstar/dbstar.conf
	fi
	if [ -e "/data/dbstar/push.conf" ]; then
		echo "push.conf already exist!"
	else
		cp /system/etc/dbstar/push.conf /data/dbstar/push.conf
	fi
	if [ -e "/data/dbstar/Dbstar.db" ]; then
		echo "Dbstar.db already exist!"
	else
		cp /system/etc/dbstar/Dbstar.db /data/dbstar/Dbstar.db
	fi
	if [ -e "/data/dbstar/ColumnRes" ]; then
		echo "Dbstar.db already exist!"
	else
		cp /system/etc/dbstar/Dbstar.db /data/dbstar/Dbstar.db
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
