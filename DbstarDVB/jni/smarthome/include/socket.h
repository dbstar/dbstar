#ifndef __SOCKET_H__
#define __SOCKET_H__

int socket_init();
void socket_mainloop(void);

unsigned int smart_power_cmds_open(CMD_ARRAY_OP_E cmd_op, BOOL_E insert_flag);
int smart_power_cmds_close(unsigned int index, int close_flag);
int smart_power_instruction_set(int index_p, char *str, unsigned int str_len);
int smart_power_instruction_get(int index_p, char *str, unsigned int str_len);
int getMacAddr(int,char *);
void setKeepAlive(int);
int getSerialNum(char *,char *,int *);
int cmd_insert(char *entity, CMD_HEADER_E report_type);
int smart_power_active_reported_clear(const CMD_HEADER_E report_type);

#endif /* SOCKET_H_ */
