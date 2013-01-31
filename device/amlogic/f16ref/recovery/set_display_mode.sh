#!/system/bin/sh

case `getprop sys.fb.bits` in
    32) osd_bits=32
    ;;

    *) osd_bits=16
    ;;
esac

case $1 in 
    480p)
        fbset -fb /dev/graphics/fb0 -g 720 480 720 960 $osd_bits
        echo $1 > /sys/class/display/mode
        echo 0 0 720 480 0 0 18 18 > /sys/class/display/axis
    ;;

    720p)
        fbset -fb /dev/graphics/fb0 -g 1280 720 1280 1440 $osd_bits
        echo $1 > /sys/class/display/mode
        echo 0 0 1280 720 0 0 18 18 > /sys/class/display/axis
    ;;

    1080p)
        fbset -fb /dev/graphics/fb0 -g 1920 1080 1920 2160 $osd_bits
        echo $1 > /sys/class/display/mode
        echo 0 0 1920 1080 0 0 18 18 > /sys/class/display/axis
    ;;

    *)
        echo "Error: Un-supported display mode $1"
        echo "       Default to 720p"
        fbset -fb /dev/graphics/fb0 -g 1280 720 1280 1440 $osd_bits
        echo $1 > /sys/class/display/mode
        echo 0 0 1280 720 0 0 18 18 > /sys/class/display/axis
esac

busybox echo null > /sys/class/display2/mode
busybox echo 576cvbs > /sys/class/display2/mode
busybox echo 1 > /sys/class/video2/screen_mode
busybox echo 0 > /sys/module/amvideo2/parameters/clone_frame_scale_width
