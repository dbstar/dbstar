#ifndef __SOFTDMX_PRINT_H__
#define __SOFTDMX_PRINT_H__

extern int debug_level_get(void);

#include <android/log.h>
#define LOG_TAG "dvbpush"
#define INTERMITTENT_PRINT(x...)  do { \
	if(1==s_print_cnt){ \
		__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, x); \
	} \
} while(0)

#endif
