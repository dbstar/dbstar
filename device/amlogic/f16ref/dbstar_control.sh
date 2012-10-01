#!/system/bin/sh

system_init()
{
	setprop sys.mediascanner.enable false
	setprop persist.sys.strictmode.visual 0
	setprop persist.sys.strictmode.disable 1
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
	start) 
		system_init
		sleep 60;
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
