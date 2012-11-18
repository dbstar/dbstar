#ifndef _GPIO_H
#define _GPIO_H

#include <linux/sysfs.h>

#define GPIO_DEVCIE_NAME        "gpio"
#define GPIO_CMD_OP             0x10001


typedef struct {
    char cmd;
    char bank;
    u32  bit;
    u32  val;
} cmd_t;

typedef struct {
    char name[10];
    struct class *cla;
    struct device *dev;
    int major;
} gpio_t;

static  DEFINE_MUTEX(gpio_mutex);

extern ssize_t gpio_cmd_restore(struct class *cla, struct class_attribute *attr, const char *buf, size_t count);

static ssize_t gpio_powerkey_led_show(struct class * cla, struct class_attribute *attr, char *buf);
static ssize_t gpio_powerkey_led_store(struct class *cla, struct class_attribute *attr, const char *buf, size_t count);

static struct class_attribute gpio_class_attrs[] = {
    __ATTR(cmd,
    S_IRUGO | S_IWUSR,
    NULL,
    gpio_cmd_restore),
    __ATTR(powerkey_led,
    S_IRUGO | S_IWUSR,
    gpio_powerkey_led_show,
    gpio_powerkey_led_store),
    __ATTR_NULL,
};

static struct class gpio_class = {
        .name = GPIO_DEVCIE_NAME,
        .class_attrs = gpio_class_attrs,
    };

static  spinlock_t  gpio_lock = SPIN_LOCK_UNLOCKED;

static  gpio_t  am_gpio = {
    .cla = NULL,
    .dev = NULL,
    .major = -1,
};
static struct uio_info gpio_uio_info = {
    .name = "gpio_uio",
    .version = "0.1",
    .irq = UIO_IRQ_NONE,

    .mem = {
        [0] = {
            .memtype = UIO_MEM_PHYS,
            .addr = (IO_CBUS_PHY_BASE + CBUS_REG_OFFSET(PREG_PAD_GPIO0)),
            .size = (PREG_PAD_GPIO5 - PREG_PAD_GPIO0 + 1) * 4,
        },
    },
};

#endif //_GPIO_H
