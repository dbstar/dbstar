#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <termios.h>
#include <unistd.h>

#define UART_PORT "/dev/ttyS3"

#define UART_BUFF_LEN 1024
char buff[UART_BUFF_LEN];

int main(int argc, char **argv)
{
    int fd = -1;
    int len = 0;
    struct termios opt = {};

    /* open uart device */
    if ((fd = open(UART_PORT, O_RDWR | O_NOCTTY)) == -1) {
        perror("open uart port failed.\n");
        return -1;
    }
    printf("open uart dev %s success.\n", UART_PORT);

    /* set uart option */
    tcgetattr(fd, &opt);
    cfsetispeed(&opt, B115200);
    cfsetospeed(&opt, B115200);
    opt.c_cflag &= ~CSIZE;
    opt.c_cflag |= CS8;
    opt.c_cflag &= ~PARENB;
    opt.c_cflag &= ~CRTSCTS;
    tcsetattr(fd, TCSANOW, &opt);

    /* read/write uart */
    strcpy(buff, "just a test!");
    len = strlen(buff);
    if ((len = write(fd, buff, len)) < 0) {
        printf("write uart error!\n");
    } else {
        printf("write uart OK, len=%d\n", len);
    }

    /* close */
    close(fd);

    return 0;
}
