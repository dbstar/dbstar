#include<unistd.h>
#include <sys/stat.h>
#include <string.h>
#include <fcntl.h>
#include <stdio.h>
#include <asm/ioctl.h>

#include <stdlib.h>
#include <fcntl.h>
#include <errno.h>
#include <string.h>
#include <dirent.h>
#include <sys/socket.h>
#include <poll.h>
#include <sys/inotify.h>
#include <sys/select.h>
#include <sys/types.h>

#include "hardware_legacy/wifi.h"
#include "libwpa_client/wpa_ctrl.h"

#include "cutils/log.h"
#include "cutils/memory.h"
#include "cutils/properties.h"
#include "private/android_filesystem_config.h"
#ifdef HAVE_LIBC_SYSTEM_PROPERTIES
#define _REALLY_INCLUDE_SYS__SYSTEM_PROPERTIES_H_
#include <sys/_system_properties.h>
#endif

#include "usb.h"

static const char SYSFS_CLASS_NET[]     = "/sys/class/net";
static const char SYS_MOD_NAME_DIR[]    = "device/driver/module";
static const char DRIVER_PROP_NAME[]    = "wlan.driver.status";

struct wifi_vid_pid {
    unsigned short int vid;
    unsigned short int pid;
};

struct usb_detail_table {
    unsigned  short int usb_port;
    unsigned  short int vid;
    unsigned  short int pid;
};

int verbose1 = 0;
extern struct usb_bus *usb_busses;

extern int cu8192_load_driver();
extern int search_cu(unsigned  int vid,unsigned  int pid);
extern int cu8192_unload_driver();

extern int eu8188_load_driver();
extern int search_eu(unsigned  int vid,unsigned  int pid);
extern int eu8188_unload_driver();

extern int du8192_load_driver();
extern int search_du(unsigned  int vid,unsigned  int pid);
extern int du8192_unload_driver();

extern int su8712_load_driver();
extern int search_su(unsigned short int vid,unsigned short int pid);
extern int su8712_unload_driver();

extern int ath6kl_load_driver();
extern int search_ath6kl(unsigned  int vid,unsigned  int pid);
extern int ath6kl_unload_driver();

extern int ath9k_load_driver();
extern int search_ath9k(unsigned  int vid,unsigned  int pid);
extern int ath9k_unload_driver();

extern int ralink_load_driver();
extern int search_ralink(unsigned  int vid,unsigned  int pid);
extern int ralink_unload_driver();


char  WEXT_OR_NL80211[8];
static int cur_vid = 0;
static int cur_pid = 0;
typedef struct load_info{
	int(*load)();
	int (*unload)();
	int (*search)(unsigned int,unsigned int);
}dongle_info;

#define NUM_OF_DONGLE 7
static const dongle_info dongle_registerd[NUM_OF_DONGLE]={{cu8192_load_driver,cu8192_unload_driver,search_cu},\
                                {eu8188_load_driver,eu8188_unload_driver,search_eu},\
                                {du8192_load_driver,du8192_unload_driver,search_du},\
                                {su8712_load_driver,su8712_unload_driver,search_su},\
								{ath6kl_load_driver,ath6kl_unload_driver,search_ath6kl},\
								{ath9k_load_driver,ath9k_unload_driver,search_ath9k},\
								{ralink_load_driver,ralink_unload_driver,search_ralink}};


#define DRIVER_LINE_MAX 200
#define DRIVER_MEMBER_NUM 15
#define DRIVER_NAME_MAX_LEN 30


static int which_device_loaded = 0; //1 --> realtek 2--> ralink 3-->atheros 4-->atheros_2 5-->cu8192,su8172

static int has_device = 0;


static char driver_name[DRIVER_MEMBER_NUM][DRIVER_NAME_MAX_LEN];//15 30 
static int indent_usb_table = 0;

static struct usb_detail_table usb_table[10] = {};

#define POWER_UP    _IOW('m',1,unsigned long)
#define POWER_DOWN  _IOW('m',2,unsigned long)
#define WIFI_POWER  0
#define WIFI_BT     1
#define WIFI_3G     2

int set_power_on(int which)
{
    int fd;

    fd = open("/dev/power_ctrl", O_RDWR);

    if (fd !=  - 1) 
    {
        if(ioctl(fd,POWER_UP,which) < 0)
        {
            printf("Set Wi-Fi power on error!!!\n");
            return -1;
        }
    }
    else
        printf("Failed to open power control device! \n");

  
    close(fd);
  
    return 0;
}

int set_power_off(int which)
{
    int fd;

    fd = open("/dev/power_ctrl", O_RDWR);

    if (fd !=  - 1) 
    {
        if(ioctl(fd,POWER_DOWN,which) < 0)
        {
            printf("Set Wi-Fi power off error!!!\n");
            return -1;
        }
    }
    else
        printf("Failed to open power control device! \n");
  
    close(fd);
  
    return 0;
}

static void print_endpoint(struct usb_endpoint_descriptor *endpoint)
{
  printf("      bEndpointAddress: %02xh\n", endpoint->bEndpointAddress);
  printf("      bmAttributes:     %02xh\n", endpoint->bmAttributes);
  printf("      wMaxPacketSize:   %d\n", endpoint->wMaxPacketSize);
  printf("      bInterval:        %d\n", endpoint->bInterval);
  printf("      bRefresh:         %d\n", endpoint->bRefresh);
  printf("      bSynchAddress:    %d\n", endpoint->bSynchAddress);
}

static void print_altsetting(struct usb_interface_descriptor *interface)
{
  int i;

  printf("    bInterfaceNumber:   %d\n", interface->bInterfaceNumber);
  printf("    bAlternateSetting:  %d\n", interface->bAlternateSetting);
  printf("    bNumEndpoints:      %d\n", interface->bNumEndpoints);
  printf("    bInterfaceClass:    %d\n", interface->bInterfaceClass);
  printf("    bInterfaceSubClass: %d\n", interface->bInterfaceSubClass);
  printf("    bInterfaceProtocol: %d\n", interface->bInterfaceProtocol);
  printf("    iInterface:         %d\n", interface->iInterface);

  for (i = 0; i < interface->bNumEndpoints; i++)
    print_endpoint(&interface->endpoint[i]);
}

static void print_interface(struct usb_interface *interface)
{
  int i;

  for (i = 0; i < interface->num_altsetting; i++)
    print_altsetting(&interface->altsetting[i]);
}

static void print_configuration(struct usb_config_descriptor *config)
{
  int i;

  printf("  wTotalLength:         %d\n", config->wTotalLength);
  printf("  bNumInterfaces:       %d\n", config->bNumInterfaces);
  printf("  bConfigurationValue:  %d\n", config->bConfigurationValue);
  printf("  iConfiguration:       %d\n", config->iConfiguration);
  printf("  bmAttributes:         %02xh\n", config->bmAttributes);
  printf("  MaxPower:             %d\n", config->MaxPower);

  for (i = 0; i < config->bNumInterfaces; i++)
    print_interface(&config->interface[i]);
}

static int print_device(struct usb_device *dev, int level)
{
  usb_dev_handle *udev;
  char description[256];
  char string[256];
  int ret, i;
//	indent_usb_table = 0;
  udev = usb_open(dev);
  if (udev) {
    if (dev->descriptor.iManufacturer) {
      ret = usb_get_string_simple(udev, dev->descriptor.iManufacturer, string, sizeof(string));
      if (ret > 0)
        snprintf(description, sizeof(description), "%s - ", string);
      else
        snprintf(description, sizeof(description), "%04X - ",
                 dev->descriptor.idVendor);
    } else
      snprintf(description, sizeof(description), "%04X - ",
               dev->descriptor.idVendor);
	LOGE("The VID is %04X \n",dev->descriptor.idVendor);
	LOGE("indent_usb_table is %d\n",indent_usb_table);
	usb_table[indent_usb_table].vid = dev->descriptor.idVendor;
    
    if (dev->descriptor.iProduct) {
      ret = usb_get_string_simple(udev, dev->descriptor.iProduct, string, sizeof(string));
      if (ret > 0)
        snprintf(description + strlen(description), sizeof(description) -
                 strlen(description), "%s", string);
      else
        snprintf(description + strlen(description), sizeof(description) -
                 strlen(description), "%04X", dev->descriptor.idProduct);
    } else
      snprintf(description + strlen(description), sizeof(description) -
               strlen(description), "%04X", dev->descriptor.idProduct);
    LOGE("The PID is %04X\n",dev->descriptor.idProduct);
	usb_table[indent_usb_table].pid = dev->descriptor.idProduct;
    indent_usb_table ++;
  } else{
  	LOGE("Open failed! \n");
  	snprintf(description, sizeof(description), "%04X - %04X",
             dev->descriptor.idVendor, dev->descriptor.idProduct);
    LOGE("The VID:PID is 0x%04X : 0x%04X\n",dev->descriptor.idVendor,dev->descriptor.idProduct);
}


  printf("%.*sDev #%d: %s\n", level * 2, "                    ", dev->devnum,
         description);

  if (udev && verbose1) {
    if (dev->descriptor.iSerialNumber) {
      ret = usb_get_string_simple(udev, dev->descriptor.iSerialNumber, string, sizeof(string));
      if (ret > 0)
        printf("%.*s  - Serial Number: %s\n", level * 2,
               "                    ", string);
    }
  }

  if (udev)
    usb_close(udev);

  if (verbose1) {
    if (!dev->config) {
      printf("  Couldn't retrieve descriptors\n");
      return 0;
    }

    for (i = 0; i < dev->descriptor.bNumConfigurations; i++)
      print_configuration(&dev->config[i]);
  } else {
    for (i = 0; i < dev->num_children; i++)
      print_device(dev->children[i], level + 1);
  }
  
  return 0;
}

static int get_driver_info() 
{
    DIR  *netdir;
    struct dirent *de;
    char path[SYSFS_PATH_MAX];
    char link[SYSFS_PATH_MAX];
    int ret = 0; 
    if ((netdir = opendir(SYSFS_CLASS_NET)) != NULL) {
        while ((de = readdir(netdir))!=NULL) {
            struct dirent **namelist = NULL;
            int cnt;
            if ((!strcmp(de->d_name,".")) || (!strcmp(de->d_name,"..")))
                continue;
            snprintf(path, SYSFS_PATH_MAX, "%s/%s/wireless", SYSFS_CLASS_NET, de->d_name);
            if (access(path, F_OK)) {
                snprintf(path, SYSFS_PATH_MAX, "%s/%s/phy80211", SYSFS_CLASS_NET, de->d_name);
                if (access(path, F_OK))
                    continue;
            } else
                ret = 1;

        }
    }
    closedir(netdir); 
    return ret;
}

int is_driver_loaded()
{
    if (!get_driver_info()) {
        property_set(DRIVER_PROP_NAME,"unloaded");
        //property_set(WLAN_DRIVER,"");
        return 0;
    } else {
        property_set(DRIVER_PROP_NAME,"ok");
        return 1;
    }
}

int usb_wifi_load_driver()
{
	int i,j;
	int usb_vidpid_count=0;
	int count = 100;
	struct usb_bus *bus;
	
	set_power_on(WIFI_POWER);
    usleep(2000000);
	
	LOGE("wifi_load_driver start!!\n");
	
	usb_init();
	usb_find_busses();
	usb_find_devices();
	if(usb_busses==NULL){
		LOGE("usb_busses is NULL\n");
	}
	else{
		LOGE("usb_busses is not NULL\n");
	}
	
	for (bus = usb_busses; bus; bus = bus->next) {
    	if (bus->root_dev && !verbose1){
			print_device(bus->root_dev, 0);
		}
    	else {
			struct usb_device *dev;
			for (dev = bus->devices; dev; dev = dev->next)
				print_device(dev, 0);
    	}
  	}  	
  	
  	if (is_driver_loaded()) {
		LOGD("wifi_driver has loaded!");
		return 0;
    }

	usb_vidpid_count = indent_usb_table;
	indent_usb_table = 0;
	
	
	LOGD("Start to search\n");
	LOGE("THE usb_vidpid_count is %d\n",usb_vidpid_count);	
    for (i = 0;i < usb_vidpid_count; i ++){
       // LOGD("The  serach vid:pid is 0x%x : 0x%x\n",usb_table[i].vid,usb_table[i].pid);    	
    	for(j = 0;j < NUM_OF_DONGLE;j ++){    		
    		//LOGD("The dongle no. is %d\n",j);
			if(dongle_registerd[j].search(usb_table[i].vid,usb_table[i].pid)==1){
				LOGD("search ok!");
				cur_vid = usb_table[i].vid;
				cur_pid = usb_table[i].pid;
				LOGD("The matched dongle no. is %d\n",j);
				//which_dongle_loaded = j;//record the dongle in use
				if((j == 3)||(j == 4)){
                	strcpy(WEXT_OR_NL80211,"nl80211");
            	}else {
                	strcpy(WEXT_OR_NL80211,"wext");
                }
				if(dongle_registerd[j].load() != 0){
					LOGD("Load Wi-Fi driver error!\n");
					return -1;
				}
			}
		}
	}
	
	return 0;
}

int usb_wifi_unload_driver()
{
	LOGE("wifi_unload_driver start!!\n");
	int count = 20;
	int j;
	usleep(200000);
	for(j = 0;j < NUM_OF_DONGLE;j ++){
		if(dongle_registerd[j].search(cur_vid,cur_pid)==1){
			LOGE("The dongle no. %d would be unloaded!!\n",j);
			if(dongle_registerd[j].unload() != 0){
				LOGE("Failed to  unload!\n");
				set_power_off(WIFI_POWER);
				return -1;
			}			
		}
    }
    
    set_power_off(WIFI_POWER);
		
    return 0;
}
