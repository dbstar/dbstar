#ifndef __SQLITE_H__
#define __SQLITE_H__

int sqlite_init();
INSTRUCTION_RESULT_E sqlite_read(char *sqlite_cmd, void *receiver, int (*sqlite_read_callback)(char **result, int row, int column, void *receiver));
int getGlobalPara(char* name);
int sqlite_execute(char *exec_str);
INSTRUCTION_RESULT_E read_model_with_id(int model_id, MODEL_S *model_array, unsigned int *model_num);
INSTRUCTION_RESULT_E read_equipment_with_type_id(int type_id, EQUIPMENT_S *equipment);

///debug functions
#ifdef SMARTHOME_DEBUG
void saveComInDatabase(unsigned char*,int,bool);
#endif

#endif
