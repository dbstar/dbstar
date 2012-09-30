#ifndef __SQLITE_H__
#define __SQLITE_H__

int sqlite_init();
int sqlite_uninit();
int sqlite_read(char *sqlite_cmd, void *receiver, int (*sqlite_read_callback)(char **result, int row, int column, void *receiver));
int sqlite_execute(char *exec_str);
int sqlite_table_clear(char *table_name);

#endif
