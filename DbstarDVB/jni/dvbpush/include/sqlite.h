#ifndef __SQLITE_H__
#define __SQLITE_H__

int db_init();
int db_uninit();
int sqlite_read(char *sqlite_cmd, void *receiver, unsigned int receiver_size, int (*sqlite_read_callback)(char **result, int row, int column, void *receiver, unsigned int receiver_size));
int sqlite_execute(char *exec_str);
int sqlite_execute_db(char *db_uri, char *exec_str);
int sqlite_table_clear(char *table_name);

int str_read_cb(char **result, int row, int column, void *some_str, unsigned int receiver_size);
int sqlite_transaction_begin();
int sqlite_transaction_exec(char *sqlite_cmd);
int sqlite_transaction_table_clear(char *table_name);
int sqlite_transaction_read(char *sqlite_cmd, void *receiver, unsigned int receiver_size);
int sqlite_transaction_end(int commit_flag);

int str_sqlite_read(char *buf, unsigned int buf_size, char *sql_cmd);

int global_info_init(int force_reset);
int localcolumn_init();
int db_uri_set(char *db_uri);
int smarthome_setting_reset(char *sqlite_cmd);
int smartlife_sqlite_read(char *sqlite_cmd, void *receiver, unsigned int receiver_size, int (*sqlite_read_callback)(char **result, int row, int column, void *receiver, unsigned int receiver_size));
int sqlite_read_db(char *db_uri, char *sqlite_cmd, void *receiver, unsigned int receiver_size, int (*sqlite_read_callback)(char **result, int row, int column, void *receiver, unsigned int receiver_size));

#endif
