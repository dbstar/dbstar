#ifndef __INSTRUCTION_H__
#define __INSTRUCTION_H__

int instruction_init(void);
void instruction_mainloop();
int instruction_insert(INSTRUCTION_S *inst);
int smart_power_difftime_get(void);
int power_inquire_callback(char **result, int row, int column, void *receiver);
INSTRUCTION_RESULT_E instruction_dispatch(INSTRUCTION_S *instruction);
int sockets_status_report(char *typeIDs);

#endif
