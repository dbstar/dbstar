
#include <sys/types.h>
#include <stdio.h>
#include "file_api.h"

#define FILEAPI_DEBUG_ANDROID 1
#if FILEAPI_DEBUG_ANDROID
#include <android/log.h>
#define LOG_TAG "fileapi"
#define SIMPLE_DEBUG(x...) do { \
	__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, x); \
} while(0)
#if 0
#define DEBUG(x...) do { \
	__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "[%s:%s:%d] ", __FILE__, __FUNCTION__, __LINE__); \
	__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, x); \
} while(0)
#endif
#define DEBUG(x...) do { \
        __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "[%s:%d] ", __FUNCTION__, __LINE__); \
        __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, x); \
} while(0)

#else
#define DEBUG(x...)
#endif

FILE64 * tc_fopen(const char * path,const char * mode)
{   
//DEBUG("@@@@@@@@@@1@@@@@@@@using tc_open to open file [%s][%s]\n",path,mode);
#if 0
    return tc_fopen1(path,mode);
#else
    FILE64 *p = tc_fopen1(path,mode);

    DEBUG("$$$$1$$$$$ open para [path=%s, mode=%s, return handler=%x]\n",path,mode,(int)p);
    return p;
#endif
}

int tc_fclose(FILE64 *stream)
{
#if 0
    return tc_fclose1(stream);
#else
    int ret = -1;
    ret = tc_fclose1(stream);
DEBUG("$$$$$$$$ fclose handler [%x], ret=[%d]\n", (int)stream,ret);
    return ret;
#endif
}

size_t tc_fwrite(const void *buffer, size_t size, size_t count, FILE64 *stream)
{
	//printf("@@@@@@@@@@@@@@@@@@@tc_write file pointer [%x]\n",stream);
	//DEBUG("@@@@@@@@@1@@@@@@@@@@tc_write file pointer [%x]\n",stream);
	size_t ret = tc_fwrite1(buffer,size,count,stream);
	
	if(ret != count)
	{
		DEBUG("@@@@@@@@@1@@@@@@@@@@@@@tc_write file pointer [%d]\n",ret);
	}
    return ret;
}

int tc_fseeko(FILE64 *stream, off_t64 offset, int whence)
{
#if 0
    return tc_fseeko1(stream,offset,whence);
#else

    int ret = -1;
    off_t64 offset_ori = offset;
    ret = tc_fseeko1(stream,offset,whence);
	if (ret < 0)
	    DEBUG("fseeko para[handler=%x, offset_ori=%lld, offset=%lld, whence=%d, ret=%d]\n",(int)stream,offset_ori,offset,whence,ret);
    return ret;
#endif
}

off_t64 tc_ftello(FILE64 *stream)
{
    return tc_ftello1(stream);
}

int tc_fgetpos(FILE64 *stream, off_t64 *pos)
{
    return tc_fgetpos1(stream, pos);
}

int tc_fsetpos(FILE64 *stream, const off_t64 *pos)
{
    return tc_fsetpos1(stream,pos);
}

size_t tc_fread(void *buf, size_t size, size_t count, FILE64 *fp)
{
    return tc_fread1(buf,size,count,fp);
}

int push_log(const char *log_str)
{
     DEBUG("%s\n",log_str);
     return 0;
}
