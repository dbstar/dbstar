
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <sys/types.h>
#include <sys/ioctl.h>
#include <unistd.h>
#include <fcntl.h>
#include <time.h>
#include "common.h"
#include "sha_verify.h"
#include "softdmx.h"
#include "prodrm20.h"
#include "dvbpush_api.h"
#include "bootloader.h"
#include "softdmx_print.h"
#include "porting.h"
#include "mid_push.h"
#include "drmapi.h"
#include "drmport.h"
#include "tunerdmx.h"

#ifdef TUNER_INPUT
#else
int loader_dsc_fid;
int tdt_dsc_fid = -1;
LoaderInfo_t g_loaderInfo;
static pthread_t loaderthread = 0;
static int loaderAction = 0;
static int s_print_cnt = 0;
static unsigned char tc_tid = 0xff;
static unsigned short tc_pid = 0xffff;

static char software_version[16];

int TC_loader_filter_handle(int aof);
#endif

Channel_t chanFilter[MAX_CHAN_FILTER+1];
int max_filter_num = 0;

const unsigned int tc_crc32_table[256] =
{
  0x00000000, 0x04c11db7, 0x09823b6e, 0x0d4326d9,
  0x130476dc, 0x17c56b6b, 0x1a864db2, 0x1e475005,
  0x2608edb8, 0x22c9f00f, 0x2f8ad6d6, 0x2b4bcb61,
  0x350c9b64, 0x31cd86d3, 0x3c8ea00a, 0x384fbdbd,
  0x4c11db70, 0x48d0c6c7, 0x4593e01e, 0x4152fda9,
  0x5f15adac, 0x5bd4b01b, 0x569796c2, 0x52568b75,
  0x6a1936c8, 0x6ed82b7f, 0x639b0da6, 0x675a1011,
  0x791d4014, 0x7ddc5da3, 0x709f7b7a, 0x745e66cd,
  0x9823b6e0, 0x9ce2ab57, 0x91a18d8e, 0x95609039,
  0x8b27c03c, 0x8fe6dd8b, 0x82a5fb52, 0x8664e6e5,
  0xbe2b5b58, 0xbaea46ef, 0xb7a96036, 0xb3687d81,
  0xad2f2d84, 0xa9ee3033, 0xa4ad16ea, 0xa06c0b5d,
  0xd4326d90, 0xd0f37027, 0xddb056fe, 0xd9714b49,
  0xc7361b4c, 0xc3f706fb, 0xceb42022, 0xca753d95,
  0xf23a8028, 0xf6fb9d9f, 0xfbb8bb46, 0xff79a6f1,
  0xe13ef6f4, 0xe5ffeb43, 0xe8bccd9a, 0xec7dd02d,
  0x34867077, 0x30476dc0, 0x3d044b19, 0x39c556ae,
  0x278206ab, 0x23431b1c, 0x2e003dc5, 0x2ac12072,
  0x128e9dcf, 0x164f8078, 0x1b0ca6a1, 0x1fcdbb16,
  0x018aeb13, 0x054bf6a4, 0x0808d07d, 0x0cc9cdca,
  0x7897ab07, 0x7c56b6b0, 0x71159069, 0x75d48dde,
  0x6b93dddb, 0x6f52c06c, 0x6211e6b5, 0x66d0fb02,
  0x5e9f46bf, 0x5a5e5b08, 0x571d7dd1, 0x53dc6066,
  0x4d9b3063, 0x495a2dd4, 0x44190b0d, 0x40d816ba,
  0xaca5c697, 0xa864db20, 0xa527fdf9, 0xa1e6e04e,
  0xbfa1b04b, 0xbb60adfc, 0xb6238b25, 0xb2e29692,
  0x8aad2b2f, 0x8e6c3698, 0x832f1041, 0x87ee0df6,
  0x99a95df3, 0x9d684044, 0x902b669d, 0x94ea7b2a,
  0xe0b41de7, 0xe4750050, 0xe9362689, 0xedf73b3e,
  0xf3b06b3b, 0xf771768c, 0xfa325055, 0xfef34de2,
  0xc6bcf05f, 0xc27dede8, 0xcf3ecb31, 0xcbffd686,
  0xd5b88683, 0xd1799b34, 0xdc3abded, 0xd8fba05a,
  0x690ce0ee, 0x6dcdfd59, 0x608edb80, 0x644fc637,
  0x7a089632, 0x7ec98b85, 0x738aad5c, 0x774bb0eb,
  0x4f040d56, 0x4bc510e1, 0x46863638, 0x42472b8f,
  0x5c007b8a, 0x58c1663d, 0x558240e4, 0x51435d53,
  0x251d3b9e, 0x21dc2629, 0x2c9f00f0, 0x285e1d47,
  0x36194d42, 0x32d850f5, 0x3f9b762c, 0x3b5a6b9b,
  0x0315d626, 0x07d4cb91, 0x0a97ed48, 0x0e56f0ff,
  0x1011a0fa, 0x14d0bd4d, 0x19939b94, 0x1d528623,
  0xf12f560e, 0xf5ee4bb9, 0xf8ad6d60, 0xfc6c70d7,
  0xe22b20d2, 0xe6ea3d65, 0xeba91bbc, 0xef68060b,
  0xd727bbb6, 0xd3e6a601, 0xdea580d8, 0xda649d6f,
  0xc423cd6a, 0xc0e2d0dd, 0xcda1f604, 0xc960ebb3,
  0xbd3e8d7e, 0xb9ff90c9, 0xb4bcb610, 0xb07daba7,
  0xae3afba2, 0xaafbe615, 0xa7b8c0cc, 0xa379dd7b,
  0x9b3660c6, 0x9ff77d71, 0x92b45ba8, 0x9675461f,
  0x8832161a, 0x8cf30bad, 0x81b02d74, 0x857130c3,
  0x5d8a9099, 0x594b8d2e, 0x5408abf7, 0x50c9b640,
  0x4e8ee645, 0x4a4ffbf2, 0x470cdd2b, 0x43cdc09c,
  0x7b827d21, 0x7f436096, 0x7200464f, 0x76c15bf8,
  0x68860bfd, 0x6c47164a, 0x61043093, 0x65c52d24,
  0x119b4be9, 0x155a565e, 0x18197087, 0x1cd86d30,
  0x029f3d35, 0x065e2082, 0x0b1d065b, 0x0fdc1bec,
  0x3793a651, 0x3352bbe6, 0x3e119d3f, 0x3ad08088,
  0x2497d08d, 0x2056cd3a, 0x2d15ebe3, 0x29d4f654,
  0xc5a92679, 0xc1683bce, 0xcc2b1d17, 0xc8ea00a0,
  0xd6ad50a5, 0xd26c4d12, 0xdf2f6bcb, 0xdbee767c,
  0xe3a1cbc1, 0xe760d676, 0xea23f0af, 0xeee2ed18,
  0xf0a5bd1d, 0xf464a0aa, 0xf9278673, 0xfde69bc4,
  0x89b8fd09, 0x8d79e0be, 0x803ac667, 0x84fbdbd0,
  0x9abc8bd5, 0x9e7d9662, 0x933eb0bb, 0x97ffad0c,
  0xafb010b1, 0xab710d06, 0xa6322bdf, 0xa2f33668,
  0xbcb4666d, 0xb8757bda, 0xb5365d03, 0xb1f740b4
};

unsigned int tc_crc32(const unsigned char *buf, int len)
{
	unsigned int i_crc = 0xffffffff;
	int i;
	
	for(i=0;i<len;i++)
	{
		i_crc = (i_crc << 8) ^ tc_crc32_table[(i_crc >> 24) ^ (*buf)];
		buf++;
	}
	
	return i_crc;
}

#ifdef TUNER_INPUT
int MX_alloc_filter(unsigned short pid, Filter_param* param, dataCb hdle, void* userdata, char priority)
#else
int TC_alloc_filter(unsigned short pid, Filter_param* param, dataCb hdle, void* userdata, char priority)
#endif
{
	int i,j,start_filter;
	unsigned char m;

	if (priority)
		start_filter = 0;
	else
		start_filter = HIGH_PRIORITY_FILTER_NUM;

	for(i = start_filter; i < MAX_CHAN_FILTER; i++)
	{
		if (chanFilter[i].used == 0)
		{
			chanFilter[i].neq = 0;
			for(j=0; j<DMX_FILTER_SIZE; j++)
			{
				int pos = j?(j+2):j;
				unsigned char mask = param->mask[j];
				unsigned char mode = param->mode[j];
		
				chanFilter[i].value[pos] = param->filter[j];
		
				mode = ~mode;
				chanFilter[i].maskandmode[pos] = mask&mode;
				chanFilter[i].maskandnotmode[pos] = mask&~mode;
		
				if(chanFilter[i].maskandnotmode[pos])
					chanFilter[i].neq = 1;
			}
	
			chanFilter[i].maskandmode[1] = 0;
			chanFilter[i].maskandmode[2] = 0;
			chanFilter[i].maskandnotmode[1] = 0;
			chanFilter[i].maskandnotmode[2] = 0;
	
			if (i >= max_filter_num)
				max_filter_num = i+1;
	
			m = 0;
			for(j = 0; j < max_filter_num; j++)
			{
				if (chanFilter[j].used)
				{
					if(chanFilter[j].pid == pid)
						m++;
				}
			}
	
			if (m)
			{
				for(j = 0; j < max_filter_num; j++)
				{
					if ((chanFilter[j].used)&&(chanFilter[j].pid == pid))
					{
						chanFilter[j].samepidnum = m;
					}
				}
			}
			chanFilter[i].fid = i;
			chanFilter[i].hdle = hdle;
			chanFilter[i].userdata = userdata;
			chanFilter[i].samepidnum = m;
			chanFilter[i].used = 1;
			chanFilter[i].pid = pid;
            chanFilter[i].cc = -1;
			DEBUG("allcoate a filter fid(id)[%d],num[%d],pid[0x%x]\n",i,m,pid);
			return i;
		}
	}
	return -1;
}

#ifdef TUNER_INPUT
// buddy, look at tunerdmx.c for tuner box
#else
int upgradefile_clear()
{
	PRINTF("unlink %s\n", UPGRADEFILE_IMG);
	return unlink(UPGRADEFILE_IMG);
}

static void* loader_thread()
{
	unsigned char buf[1024];
	unsigned char sha0[64];
	FILE *fp = fopen(UPGRADEFILE_IMG,"r");
	int ret;
	unsigned int len = 0,wlen = 0,rlen = 0;
	DBSTAR_CMD_MSG_E upgrade_msg_id = UPGRADE_NEW_VER;
	char upgrade_msg[128];

reLoader:
	while (loaderAction == 0)
	{
		sleep(2);
		PRINTF("loaderAction == 0\n");
	}
	/*fp=fopen("localfile","rb");// localfile�ļ���
	fseek(fp,0,SEEK_SET);
	fseek(fp,0,SEEK_END);
	long longBytes=ftell(fp);// longBytes�����ļ��ĳ���
	*/
	//DEBUG("mtd_scan_partitions = [%d]\n",mtd_scan_partitions());
	DEBUG("in loader thread...\n");
	wlen = 0;
	ret = fread(buf,1,48,fp);
	len = 0;
	ret = fread(&len,1,1,fp);
	DEBUG("in loader thread, read file len = [%u]\n",len);
	if (len > 1024)
		rlen = 1024;
	else
		rlen = len;

	do
	{
		ret = fread(buf,1,rlen,fp);
		if (ret > 0)
			wlen += ret;
		else
		{
			break;
		}
		rlen = len - wlen;
		if (rlen > 1024)
			rlen = 1024;
		else
			DEBUG("rlen=%d\n", rlen);
		//        else if (rlen < 0)
		//            break;
	} while(wlen < len);
	if (wlen != len)
	{
		DEBUG("received upgrade file is err, re download the file!!!!\n");
	}

	wlen = 0;
	ret = fread(&len,4,1,fp);
	len = ((len&0xff)<<24)|((len&0xff00)<<8)|((len&0xff0000)>>8)|((len&0xff000000)>>24);
	fread(sha0,1,64,fp);

#if 1
	if (sha_verify(fp, sha0, g_loaderInfo.img_len-64) != 0)
	{
		DEBUG("sha verify err\n");
		
		loaderAction = 0;
		Filter_param param;
		memset(&param,0,sizeof(param));
		param.filter[0] = 0xf0;
		param.mask[0] = 0xff;

		loader_dsc_fid=TC_alloc_filter(0x1ff0, &param, loader_des_section_handle, NULL, 1);
		fclose(fp);
		
		goto reLoader;
		//return NULL;
	}
#endif
	fclose(fp);
#if 1
	//1 checking img, if not correct,return
	FILE *cfp = fopen(COMMAND_FILE,"w");

	if (!cfp)
		return NULL;
	//2 set upgrade mark
	DEBUG("g_loaderInfo.download_type: [%s], g_loaderInfo.file_type=[%s]\n", g_loaderInfo.download_type, g_loaderInfo.file_type);
	if (0==strcmp(g_loaderInfo.download_type,"1"))
	{
		//must upgrade,display upgrade info, wait 5 second, set uboot mark and then reboot
		
		if (0==strcmp(g_loaderInfo.file_type,"1"))
		{
			PRINTF("download_type is 1, file_type is 1, upgrade app\n");
//			fprintf(cfp,"--update_package=%s\n",UPGRADEFILE_ALL);
			//              fprintf(cfp,"--wipe_data\n");
			//              fprintf(cfp,"--wipe_cache\n");
			fprintf(cfp,"--orifile=%s\n",UPGRADEFILE_IMG);
			//snprintf(upgrade_msg, sizeof(upgrade_msg),"%d.%d.%d.%d",software_version[0],software_version[1],software_version[2],software_version[3]);
			fprintf(cfp,"%s\n",software_version);
			snprintf(upgrade_msg, sizeof(upgrade_msg),"%s",UPGRADEFILE_IMG);
		}
		else
		{
			PRINTF("download_type is 1, file_type is 0, upgrade uboot\n");
			snprintf(upgrade_msg, sizeof(upgrade_msg),"%s",UPGRADEFILE_IMG);
		}
		upgrade_msg_id = UPGRADE_NEW_VER_FORCE;
	}
	else
	{
		//display info and ask to upgrade right now?

		if (0==strcmp(g_loaderInfo.file_type,"1"))
		{
			PRINTF("download_type is 0, file_type is 1, upgrade app\n");
//			fprintf(cfp,"--update_package=%s\n",UPGRADEFILE_ALL);
			//                fprintf(cfp,"--wipe_data\n");
			//                fprintf(cfp,"--wipe_cache\n");
			fprintf(cfp,"--orifile=%s\n",UPGRADEFILE_IMG);
			//snprintf(upgrade_msg, sizeof(upgrade_msg),"%d.%d.%d.%d",software_version[0],software_version[1],software_version[2],software_version[3]);
			fprintf(cfp,"%s\n",software_version);

			snprintf(upgrade_msg, sizeof(upgrade_msg),"%s",UPGRADEFILE_IMG);
		}
		else
		{
			PRINTF("download_type is 0, file_type is 0, upgrade uboot\n");
			snprintf(upgrade_msg, sizeof(upgrade_msg),"%s",UPGRADEFILE_IMG);
		}
		upgrade_msg_id = UPGRADE_NEW_VER;
	}
	fclose(cfp);
	msg_send2_UI(upgrade_msg_id, upgrade_msg, strlen(upgrade_msg));
	
	//upgrade_sign_set();
	
	TC_loader_filter_handle(0);
#endif
	return NULL;
}

int TC_loader_filter_handle(int aof) //1 allocate loader filter, 0 free loader filter
{
    static Channel_t chanFilter0;
    static int maxnum=0;
	
	DEBUG("aof: %d\n", aof);
    if(aof)
    {
        TC_loader_to_push_order(0);  //let push idle;
        memset(&chanFilter0,0,sizeof(chanFilter0));
        memcpy(&chanFilter0,&chanFilter[0],sizeof(chanFilter0));
        maxnum = max_filter_num;
        max_filter_num = 1;
        TC_free_filter(0);
        
    }
    else
    {
        memcpy(&chanFilter[0],&chanFilter0,sizeof(chanFilter0));
        chanFilter[0].bytes = 0;
        chanFilter[0].stage  = CHAN_STAGE_START;

        if (max_filter_num != 1)
        {
            if (max_filter_num >= maxnum)
                return 0;
        }
        max_filter_num = maxnum;
        memset(&chanFilter0,0,sizeof(chanFilter0));
        maxnum = 0;
    }
    return 0;
}

static void loader_section_handle(int fid, const unsigned char *data, int len, void *user_data)
{
	static unsigned char startWrite = 0, getMaxSeq = 0, total_subone = 0, total_cycle = 0;
	static unsigned char *recv_buf=NULL, *recv_mark=NULL;
	unsigned char *datap = NULL;
	static int total_loader=0, lastSeq=-1, totalLen=0;
	static int maxSeq = -1;
	int seq=0;
	FILE *upgradefile=NULL;
	
	static int s_first_package_flag = -1;
	int tmp_i = 0;
	
	static int print_loader_section_handle_only_once = 0;
	if(0==print_loader_section_handle_only_once){
		DEBUG("in loader_section_handle, fid = %d, len = %d\n", fid, len);
		print_loader_section_handle_only_once = 1;
	}
	
	//DEBUG("call loader_section_handle\n");
	if ((len < 12)||(len > 4096))
	{
		DEBUG("loader data too small!!!!!!!!!!\n");
		return;
	}
	len -= 12;
	
	datap = (unsigned char *)data+4;
	if (getMaxSeq==0)
	{
        if (tc_crc32(data,len+12) )
        {
			PRINTF("seq = [%u] crc error !!!!!!!!!!!!!!!!!!!!\n",seq);
			return;
        }

		if (datap[2]==datap[3])  //last section num = current section num
		{
			if(datap[0]==datap[1])//last part num = current part num seq >= 3*0x100+0xad)
			{
				maxSeq = datap[1]*0x100+datap[3]+1;
				getMaxSeq = 1;
				DEBUG("datap[1]=%d, datap[3]=%d, maxSeq=%d\n", datap[1], datap[3], maxSeq);
			}
		}
	}
	
	seq = datap[0]*0x100 + datap[2];
	
	if(s_first_package_flag>0 && (s_first_package_flag==seq)){
		DEBUG("has recv %d/%d for one loop, lost such packages:\n", total_loader, maxSeq);
		int count_need = 0;
		for(tmp_i=0;tmp_i<maxSeq;tmp_i++){
			if(0==recv_mark[tmp_i]){
				count_need ++;
				PRINTF("lost %d: %d\n", count_need, tmp_i);
			}
		}
		s_first_package_flag = -2;
	}
	
	if (startWrite)
	{
		if(seq < maxSeq)
		{
			if(recv_mark[seq]==0)
			{
    	    	                if (tc_crc32(data,len+12) )
    	    	                {
    	    		            PRINTF("seq = [%u] crc error !!!!!!!!!!!!!!!!!!!!\n",seq);
    	    		            return;
    	    	                }
				recv_mark[seq]=1;
				total_loader++;
				
				if(-2==s_first_package_flag){
					PRINTF("supply seq: %d, len=%d, total_loader=%d\n", seq, len, total_loader);
				}
//				if(LOADER_PACKAGE_SIZE!=len){
//					DEBUG("monitor this package: seq=%u, len=%u, maxSeq=%u, total_loader=%d, totalLen=%d\n", seq, len, maxSeq, total_loader, totalLen);
//				}
				
				if(((lastSeq+1) != seq && lastSeq!=0) || g_loaderInfo.fid>32 || g_loaderInfo.fid<0)
					PRINTF("total_loader=%d/%d,lastSeq=%d,seq=[%u],fid=%d\n", total_loader,maxSeq,lastSeq,seq,g_loaderInfo.fid);
				
				totalLen += len;
patch0:
				memcpy(recv_buf+seq*LOADER_PACKAGE_SIZE,datap+4,len);
				if (seq == 1)
				{
					if (lastSeq == 0)
					{        
                                                if (recv_mark[0] == 1)
                                                {
                                                    if (total_subone == 1)
                                                    {
                                                        total_loader++;
                                                        total_subone = 0;
                                                    }
						    recv_mark[0] = 2;
                                                }
					}
				}
                                else if (seq == 0)
                                {
                                        if ((lastSeq+1) == maxSeq)
                                        {
                                                recv_mark[0] = 2;
                                        }
                                }
				lastSeq = seq;
				
				//DEBUG("total_loader: %d, maxSeq: %d\n", total_loader, maxSeq);
				if (total_loader >= maxSeq)
				{
					if (recv_mark[0] < 2)
					{
						total_loader -= 1;
                                                total_subone = 1;
						DEBUG("---subone and total_loader=%u, maxseq=%u\n", total_loader,maxSeq);
						return;
					}
					
					PRINTF("total_loader: %u, maxSeq: %u, g_loaderInfo.fid: %d\n", total_loader, maxSeq,g_loaderInfo.fid);
					TC_free_filter(g_loaderInfo.fid);
					TC_loader_to_push_order(1);                                        
					upgradefile = fopen(UPGRADEFILE_IMG,"w");
					if (!upgradefile)
					{
						PRINTF("open %s failed\n", UPGRADEFILE_IMG);
						startWrite = 0;
						getMaxSeq = 0;
                                                total_subone = 0;
                                                lastSeq = -1;
                                                maxSeq = -1;
                                                total_cycle = 0;
						if (recv_buf!= TC_loader_get_push_buf_pointer())
							free(recv_buf);
						free(recv_mark);
						return;
					}
					else
						PRINTF("open %s OK\n", UPGRADEFILE_IMG);
					
					fwrite(recv_buf,1,totalLen,upgradefile);
					fclose(upgradefile);
					if (recv_buf!= TC_loader_get_push_buf_pointer())
						free(recv_buf);
					free(recv_mark);
					startWrite = 0;
					getMaxSeq = 0;
                                        total_subone = 0;
                                        lastSeq = -1;
                                        maxSeq = -1;
                                        total_cycle = 0;
					if (loaderthread == 0)
					{
						pthread_create(&loaderthread, NULL, loader_thread, NULL);
						loaderAction = 1;
					}
					return;
				}
			}
			else
			{
				if (seq == 0)
				{
					if(recv_mark[0] == 1)
					{
						if ((lastSeq+1) == maxSeq)
						{
							recv_mark[0] = 2;
                                                        if (total_subone)
                                                        {
                                                            total_subone = 0;
                                                            total_loader++;
                                                        }
DEBUG("monitor this package: seq=%u, len=%u, maxSeq=%u, total_loader=%d, totalLen=%d\n", seq, len, maxSeq, total_loader, totalLen);
						}
						goto patch0;
					}
DEBUG("monit0 this package:seq=%u, len=%u, maxSeq=%u, total_loader=%d, totalLen=%d, recvmark0=%d\n", seq, len, maxSeq, total_loader, totalLen,recv_mark[0]);
				}
				else
				{
					lastSeq = seq;
				}
                                if (seq == 1)
                                {
                                        total_cycle++;
                                        if(total_cycle > 5)
                                        {
                                                startWrite = 0;
                                                getMaxSeq = 0;
                                                total_subone = 0;
                                                lastSeq = -1;
                                                maxSeq = -1;
                                                if (recv_buf!= TC_loader_get_push_buf_pointer())
                                                        free(recv_buf);
                                                free(recv_mark);
                                                total_cycle = 0;
                                                TC_free_filter(g_loaderInfo.fid);
                                                TC_loader_to_push_order(1);
                                                {
                                                Filter_param param;
                                                memset(&param,0,sizeof(param));
                                                param.filter[0] = 0xf0;
                                                param.mask[0] = 0xff;

                                                loader_dsc_fid=TC_alloc_filter(0x1ff0, &param, loader_des_section_handle, NULL, 1);
                                                }
                                        } 
                                }

			}
		}
		else
			PRINTF("seq=[%u] part_num[%x] last_part_num[%x],sec_num[%x] last_sec_num[%x]\n",seq,datap[0],datap[1],datap[2],datap[3]);
	}
	else
	{
// add new
                if (tc_crc32(data,len+12) )
                {
                        PRINTF("seq = [%u] crc error !!!!!!!!!!!!!!!!!!!!\n",seq);
                        return;
                }
//add end
		if ((datap[0]==datap[1])&&(datap[2]==datap[3]))  //donot delete, for this section if not the full section,section size is not correct
			return;
		if (maxSeq == -1)
			maxSeq = (datap[1]+1)*0x100+1;
		
		while(TC_loader_get_push_state()==0)
		{
			usleep(100000);
		}
		
		if (TC_loader_get_push_buf_size() >= maxSeq*LOADER_PACKAGE_SIZE)
		{
			recv_buf = TC_loader_get_push_buf_pointer();
			DEBUG("use push buf for upgrade: %p\n", recv_buf);
		}
		else 
		{
			DEBUG("push buffer is too small for upgrade file!!!!\n");
			recv_buf = (unsigned char *)malloc(maxSeq*LOADER_PACKAGE_SIZE);
			DEBUG("malloc %d x %d = %d for upgrade file\n", maxSeq,LOADER_PACKAGE_SIZE,maxSeq*LOADER_PACKAGE_SIZE);
		}
		
		if (recv_buf == NULL)
		{
			DEBUG("can not allcate mem for upgrade file!!!!\n");
			return;
		}
		
		recv_mark = (unsigned char *)malloc(maxSeq);
		if (recv_mark == NULL)
		{
			DEBUG("can not allcocate mem for recv_mark!!!\n");
			return;
		}
		memset(recv_mark,0,maxSeq);
		
		if (seq < maxSeq)
		{
			recv_mark[seq] = 1;
			lastSeq = seq;
			memcpy(recv_buf+seq*len,datap+4,len);
			startWrite = 1;
			totalLen = len;
			total_loader = 1;
			s_first_package_flag = seq;
		}
		DEBUG("maxSeq=%d, lastSeq=%d, seq=%d, startWrite=%d, totalLen=%d, total_loader=%d\n", maxSeq, lastSeq, seq, startWrite, totalLen, total_loader);
	}
}

static char s_time_sync_2_ui[128];
void tdt_section_handle(int fid, const unsigned char *data, int len, void *user_data)
{
    int year,month,day;
    int mjd;
    char tdt[64];

    if (len < 8)
    {
        return;
    }
    
    if ((data[0]!= 0x70)&&(data[0]!= 0x73))
        return;
    mjd = (int)((data[3]<<8)|data[4]);
    year = (int)((mjd -15078.2)/365.25);
    month = (int)((mjd - 14956.1 - (int)(year*365.25))/30.6001);
    day   = mjd - 14956 - (int)(year*365.25) - (int)(month*30.6001);
    if ((month == 14)||(month == 15))
    {
        year += 1;
        month -= 13; 
    }
    else
    {
        month -= 1;
    }
    
    snprintf(tdt,sizeof(tdt),"%4d-%2d-%2d %2x:%2x:%2x",year+1900,month,day,data[5],data[6],data[7]);
    TC_free_filter(tdt_dsc_fid);
    DEBUG("catch tdt time(%s) and free tdt_dsc_fid(%d)\n",tdt,tdt_dsc_fid);
    
    struct tm tm_tdt;
    memset(&tm_tdt,0,sizeof(tm_tdt));
	sscanf(tdt,"%d-%d-%d %d:%d:%d",&(tm_tdt.tm_year),&(tm_tdt.tm_mon),&(tm_tdt.tm_mday),&(tm_tdt.tm_hour),&(tm_tdt.tm_min),&(tm_tdt.tm_sec));
    
	tm_tdt.tm_year-=1900;	/*���ֵ��ȥ1900���õ�tm�ṹ�б�����������*/
	tm_tdt.tm_mon-=1;		/*�·�ֵ��ȥ1���õ�tm�ṹ�б�����·�����*/
	tm_tdt.tm_isdst = 0;
	
	time_t local_time_s = mktime(&tm_tdt);
	
// ��ǰ��tdt��1.3��2.0���ã����õ��Ǳ�׼ʱ�䣬�ʱ������8СʱתΪ����ʱ�䡣

#if 0  //TOT table parse
    if (data[0]==0x73)
    {
        if(data[10] == 0x58)
        {
            unsigned char *dsc_data = data+12;
            unsigned char add_sub = 0;
            time_t dif_time=0;
            //int len = 0, i=0;

            if (data[11] >= 13)
            {
                //do
                {
                    add_sub = dsc_data[3]&0x1;
                    dif_time = ((dsc_data[4]>>4)*10 + dsc_data[4]&0xf)*3600 + ((dsc_data[5]>>4)*10+dsc_data[5]&0xf)*60;
                  //  len += 13;
                }  // while (len < data[11]);
            }
        }
    }

#endif
	local_time_s += (8*60*60);
	DEBUG("rectify time for 8 hours\n");
	
    snprintf(s_time_sync_2_ui,sizeof(s_time_sync_2_ui),"%ld",local_time_s);
	
#if 0
// only for test
	char sqlite_cmd[512];
	snprintf(sqlite_cmd,sizeof(sqlite_cmd),"replace into RejectRecv(ID,URI) values('%s','%s');",s_time_sync_2_ui,tdt);
	sqlite_execute(sqlite_cmd);
#endif
	
	msg_send2_UI(TDT_TIME_SYNC, s_time_sync_2_ui, strlen(s_time_sync_2_ui));
    
    DEBUG("distill %4d-%2d-%2d %2d:%2d:%2d, send to UI %s\n",(tm_tdt.tm_year),(tm_tdt.tm_mon),(tm_tdt.tm_mday),(tm_tdt.tm_hour),(tm_tdt.tm_min),(tm_tdt.tm_sec),s_time_sync_2_ui);
	tdt_dsc_fid = -1;
	
	// following code only for test, check the seconds if correctly.
	time_t timep = strtol(s_time_sync_2_ui,NULL,0);
	struct tm *check_tdt = localtime(&timep);
	DEBUG("check tdt time which is send to UI, seconds(str type): %s, seconds(long type): %ld, localtime: %04d-%02d-%02d %02d:%02d:%02d\n", s_time_sync_2_ui,timep,
		check_tdt->tm_year+1900,check_tdt->tm_mon+1,check_tdt->tm_mday,check_tdt->tm_hour,check_tdt->tm_min,check_tdt->tm_sec);
}

void ca_section_handle(int fid, const unsigned char *data, int len, void *user_data)
{
	static unsigned short emmpid=0xffff;
	unsigned short pid = 0;
	static unsigned char version=0xff;
	unsigned char tmp;
	
	if (len < 18)
	{
		DEBUG("ca section too small!!!! %d\n",len);
		return;
	}
	
	tmp = data[5]&0x3e;
	if (version != tmp)
	{
		if (tc_crc32(data,len))
		{
			PRINTF("CA table  error !!!!!!!!!!!!!!!!!!!!\n");
			return;
		}

		version = tmp;
		pid = ((data[12]&0x1f)<<8)|data[13];
		if (pid != emmpid)
		{
			DEBUG("set emm pid =[%x] start...\n",pid);
			if(0==drm_init()){
				CDCASTB_SetEmmPid(pid);
				DEBUG("set emm pid =[%x] finished\n",pid);
			}
			else
				DEBUG("drm init failed\n");
		}
	}
}

typedef enum{
	CHECK_UPGRADE_FAILED = -1,
	NO_NEED_UPGRADE = 0,
	NEED_UPGRADE = 1
}CHECK_UPGRADE;

// check if need upgrade, return 0 means no need upgrade; return -1 means failed; return 1 means need upgrade
static CHECK_UPGRADE check_upgrade(char *cur_version, char *new_version)
{
	int curver[4];
	int newver[4];
	int i = 0;
	
	if(NULL==cur_version){
		INTERMITTENT_PRINT("current version is null, upgrade directly\n");
		return NEED_UPGRADE;
	}
	
	if(4!=sscanf(cur_version, "%d.%d.%d.%d", &curver[0], &curver[1], &curver[2], &curver[3])){
		INTERMITTENT_PRINT("current version (%s) format is invalid, upgrade directly\n", cur_version);
		return NEED_UPGRADE;
	}
	
	if(NULL==new_version){
		INTERMITTENT_PRINT("new version is null, no need upgrade\n");
		return NO_NEED_UPGRADE;
	}
	
	if(4!=sscanf(new_version, "%d.%d.%d.%d", &newver[0], &newver[1], &newver[2], &newver[3])){
		INTERMITTENT_PRINT("new version (%s) format is invalid, no need upgrade\n", new_version);
		return NO_NEED_UPGRADE;
	}
	
	for(i=0; i<4; i++){
		if(newver[i]>curver[i]){
			INTERMITTENT_PRINT("newver[%d]=%d is larger than curver[%d]=%d, need upgrade\n", i, newver[i], i, curver[i]);
			return NEED_UPGRADE;
		}
		else if(newver[i]==curver[i]){
			continue;
		}
		else{	// if(newver[i]<curver[i])
			INTERMITTENT_PRINT("newver[%d]=%d is less than curver[%d]=%d, no need upgrade\n", i, newver[i], i, curver[i]);
			return NO_NEED_UPGRADE;
		}
	}
	
	INTERMITTENT_PRINT("newver[%d][%d][%d][%d] is equal as curver[%d][%d][%d][%d], no need upgrade\n", 
			newver[0], newver[1], newver[2], newver[3], 
			curver[0], curver[1], curver[2], curver[3]);
	
	return NO_NEED_UPGRADE;
}

void loader_des_section_handle(int fid, const unsigned char *data, int len, void *user_data)
{
	unsigned char *datap=NULL, ctmp;
//	unsigned char mark = 0;
	char tmp[10],cmp[128],tmp_id[20];
        int stb_id_l;
        int stb_seral;
	unsigned short tmp16=0;
	//unsigned int stb_id_l=0,stb_id_h=0;
	
	if(s_print_cnt>2048)
		s_print_cnt = 0;
	else
		s_print_cnt ++;
	
//	INTERMITTENT_PRINT("Got loader des section len [%d]\n",len);
	/*{
	int i;
	
	for(i=0;i<len;i++)
	{
	DEBUG("%02x ", data[i]);
	if(((i+1)%32)==0) DEBUG("\n");
	}
	
	if((i%32)!=0) DEBUG("\n");
	
	}*/
	if (len < 55)
	{
		INTERMITTENT_PRINT("loader info too small!!!!!!!!!![%d]\n",len);
		//        return;
	}
	
	int i = 0;
	static int print_only_once = 0;
	if(0==print_only_once || 100==print_only_once || 200==print_only_once || 300==print_only_once || 400==print_only_once || 500==print_only_once){
		DEBUG("loader ori data start len=%d(", len);
		for(i=0; i<len; i++){
			PRINTF("[%x]", data[i]);
		}
		DEBUG(")\n loader ori data end\n");
		print_only_once ++;
	}
	
#if 0
	if (tc_crc32(data,len))
	{
		INTERMITTENT_PRINT("loader des error !!!!!!!!!!!!!!!!!!!!\n");
		return;
	}
#endif
	datap = (unsigned char *)data+4;
	//if ((datap[0] != datap[1])||(datap[2] != datap[3]))
	//    DEBUG("!!!!!!!!!!!!!!!!error section number,need modify code!\n");
	
	datap += 4;
#if 0	
	memset(&g_loaderInfo, 0, sizeof(g_loaderInfo));
	get_loader_message(&mark,&g_loaderInfo);
#endif
	
	//oui
	tmp16 = *datap;
	datap++;
	tmp16 = (tmp16<<8)|(*datap);
        snprintf(cmp,3,"%.2d",tmp16);

//	INTERMITTENT_PRINT("loader info oui = [%x]\n",tmp16);
	if (strncmp(cmp, g_loaderInfo.oui,2)){
		INTERMITTENT_PRINT("loader oui check failed [0x%x], compare with my oui [%s]\n",tmp16,g_loaderInfo.oui);
		return;
	}
	
	//model_type
	datap++;
	tmp16 = *datap;
	datap++;
	tmp16 = (tmp16<<8)|(*datap);
        snprintf(cmp,3,"%.2d",tmp16);
        if (strncmp(cmp, g_loaderInfo.model_type,2)){
	//INTERMITTENT_PRINT("loader info model type = [%x]\n",tmp16);
	//if (tmp16 != g_loaderInfo.model_type){
		INTERMITTENT_PRINT("model type check failed [%x]\n",tmp16);
		return;
	}
	
	datap ++;  //usergroup id
	
	//hardware_version
	datap += 2;
	//tmp32 = ((datap[0]<<24)|(datap[1]<<16)|(datap[2]<<8)|(datap[3]));
	INTERMITTENT_PRINT("loader harder version [%u][%u][%u][%u]\n",datap[0],datap[1],datap[2],datap[3]);
//	if ((datap[0] != g_loaderInfo.hardware_version[0])||(datap[1] != g_loaderInfo.hardware_version[1])
//	||(datap[2] != g_loaderInfo.hardware_version[2])||(datap[3] != g_loaderInfo.hardware_version[3]))
	sprintf(cmp,"%d.%d.%d.%d",datap[0],datap[1],datap[2],datap[3]);
	if(strcmp(cmp, g_loaderInfo.hardware_version))
	{
		INTERMITTENT_PRINT("hardware version check failed!!!!!\n");
		return;
	}
	//software_version
	datap += 4;
	INTERMITTENT_PRINT("new software ver: [%u][%u][%u][%u]\n",datap[0],datap[1],datap[2],datap[3]);
	if(255==datap[0] && 255==datap[1] && 255==datap[2] && 255==datap[3]){
		INTERMITTENT_PRINT("software version is 255.255.255.255, do upgrade directly\n");
	}
	else{
		sprintf(cmp,"%d.%d.%d.%d",datap[0],datap[1],datap[2],datap[3]);
		
		INTERMITTENT_PRINT("coming version[%s], g_loaderInfo.software_version[%s]\n", cmp,g_loaderInfo.software_version);
        if(NEED_UPGRADE!=check_upgrade(g_loaderInfo.software_version, cmp)){
        	INTERMITTENT_PRINT("no need upgrade\n");
        	return;
        }
	}
	
	strncpy(software_version,cmp,sizeof(software_version));
	//stb_id
	datap += 4;
        snprintf(cmp,7,"%6s",g_loaderInfo.stbid+10);
INTERMITTENT_PRINT("stb serail = [%s]\n",cmp);
        stb_seral = atol(cmp);
INTERMITTENT_PRINT("serial = [%d]\n",stb_seral);
        snprintf(cmp,11,"%.2x%.2x%.2x%.2x%.2x",datap[0],datap[1],datap[2],datap[3],datap[4]);
INTERMITTENT_PRINT("first 10 = [%s] == [%s]\n",cmp,g_loaderInfo.stbid);

        if (0) //strncmp(stbid,cmp,10)) //loaderinfo.stb_id_h < stb_id_h)
        {
            INTERMITTENT_PRINT("stb id is not in this update sequence \n");
            return;
        }
        else // if (loaderinfo.stb_id_h == stb_id_h)
        {
            sprintf(tmp_id,"%.2x%.2x%.2x",datap[5],datap[6],datap[7]);
            stb_id_l = atol(tmp_id);
			INTERMITTENT_PRINT("start id l=[%u], me=[%u]\n",stb_id_l,stb_seral);
            if (stb_seral  < stb_id_l)
            {
                INTERMITTENT_PRINT("stb id is not in this update sequence \n");
                return ;
            }
        }
        
        datap += 8;
        snprintf(cmp,11,"%.2x%.2x%.2x%.2x%.2x",datap[0],datap[1],datap[2],datap[3],datap[4]);
        if (0) //strncmp(stbid,cmp,10)) //loaderinfo.stb_id_h > stb_id_h)
        {
            INTERMITTENT_PRINT("stb id is not in this update sequence \n");
            return ;
        }
        else // if (loaderinfo.stb_id_h == stb_id_h)
        {
            snprintf(tmp_id,7,"%.2x%.2x%.2x",datap[5],datap[6],datap[7]);
            stb_id_l = atol(tmp_id);
            if (stb_seral > stb_id_l)
            {
                INTERMITTENT_PRINT("stb id is not in this update sequence\n");
                return ;
            }
        }

/*	snprintf(tmp,sizeof(tmp),"%.2x%.2x%.2x%.2x",datap[0],datap[1],datap[2],datap[3]);
	stb_id_h = atol(tmp);
	INTERMITTENT_PRINT("start stb id h = [%u] me h[%u]\n",stb_id_h,g_loaderInfo.stb_id_h);
	if (g_loaderInfo.stb_id_h < stb_id_h)
	{
		datap += 4;
		INTERMITTENT_PRINT("stb id is not in this update sequence \n");
		return;
	}
	else if (g_loaderInfo.stb_id_h == stb_id_h)
	{
		datap += 4;
		snprintf(tmp,sizeof(tmp),"%.2x%.2x%.2x%.2x",datap[0],datap[1],datap[2],datap[3]);
		stb_id_l = atol(tmp);
		INTERMITTENT_PRINT("start id l=[%u], l=[%u]\n",stb_id_h, stb_id_l);
		if (g_loaderInfo.stb_id_l < stb_id_l)
		{
			INTERMITTENT_PRINT("stb id is not in this update sequence \n");
			return;
		}
	}
	else
		datap += 4;
	datap += 4;
	snprintf(tmp,sizeof(tmp),"%.2x%.2x%.2x%.2x",datap[0],datap[1],datap[2],datap[3]);
	stb_id_h = atol(tmp);
	INTERMITTENT_PRINT("end stb id h [%u] me [%u]\n",stb_id_h,g_loaderInfo.stb_id_h);
	if (g_loaderInfo.stb_id_h > stb_id_h)
	{
		datap += 4;
		INTERMITTENT_PRINT("stb id is not in this update sequence \n");
		return;
	}
	else if (g_loaderInfo.stb_id_h == stb_id_h)
	{
		datap += 4;
		snprintf(tmp,sizeof(tmp),"%.2x%.2x%.2x%.2x",datap[0],datap[1],datap[2],datap[3]);
		stb_id_l = atol(tmp);
		INTERMITTENT_PRINT("end start id h=[%u], l=[%u]\n",stb_id_h, stb_id_l);
		if (g_loaderInfo.stb_id_l > stb_id_l)
		{
			INTERMITTENT_PRINT("stb id is not in this update sequence \n");
			return;
		}
	}
	else
		datap += 4;
*/
        datap += 8;
        if (tc_crc32(data,len))  //verify the desc section data
        {
                INTERMITTENT_PRINT("loader des error !!!!!!!!!!!!!!!!!!!!\n");
                return;
        }

	
	INTERMITTENT_PRINT("loader_dsc_fid: %d=%x\n", loader_dsc_fid,loader_dsc_fid);
	s_print_cnt = 0;
	TC_free_filter(loader_dsc_fid);
	//datap += 4;
	{
		//unsigned short pid;
		//unsigned char tid;
		Filter_param param;
		
		tc_pid = *datap;
		datap++;
		tc_pid = ((tc_pid<<8)|(*datap));//&0x1fff;
		datap++;
		tc_tid = *datap++;
		DEBUG(">>>> pid = [%x]  tid=[%x] loader_section_handle=%p\n",tc_pid,tc_tid,loader_section_handle);
		TC_loader_filter_handle(1);
		memset(&param,0,sizeof(param));
		param.filter[0] = tc_tid;
		param.mask[0] = 0xff;
		g_loaderInfo.fid = TC_alloc_filter(tc_pid, &param, loader_section_handle, NULL, 1);
		//DEBUG("pid: %d|0x%x, fid: %d\n", tc_pid,tc_pid, g_loaderInfo.fid);
	}

        ctmp = *datap++;	
	snprintf(g_loaderInfo.file_type,3,"%d",ctmp);
	g_loaderInfo.img_len = ((datap[0]<<24)|(datap[1]<<16)|(datap[2]<<8)|(datap[3]));
        ctmp = datap[4];
	snprintf(g_loaderInfo.download_type,3,"%d",ctmp);
	DEBUG("g_loaderInfo.file_type=%s, g_loaderInfo.img_len=%d, g_loaderInfo.fid: %d\n", g_loaderInfo.file_type, g_loaderInfo.img_len, g_loaderInfo.fid);
	//DEBUG(">>>>>> filetype =[%d], img_len[%d], downloadtype=[%d]\n",g_loaderInfo.file_type,g_loaderInfo.img_len,g_loaderInfo.download_type);
}
#endif

static void dump_bytes(int fid, const unsigned char *data, int len, void *user_data)
{
}

int alloc_filter(unsigned short pid, char pro)
{
	Filter_param param;
	
	DEBUG("pid=%d|0x%x, pro=%d\n", pid, pid, pro);
	
	memset(&param,0,sizeof(param));
	param.filter[0] = 0x3e;
	param.mask[0] = 0xff;
	
#ifdef TUNER_INPUT
	return MX_alloc_filter(pid, &param, dump_bytes, NULL, pro);
#else
	return TC_alloc_filter(pid, &param, dump_bytes, NULL, pro);
#endif
}

#ifdef TUNER_INPUT
void MX_free_filter(int fid)
#else
void TC_free_filter(int fid)
#endif
{
	DEBUG("fid=%d\n",fid);
	if ((fid < MAX_CHAN_FILTER)&&(fid>=0))
	{
		DEBUG("free fid=%d, pid=0x%x\n", fid, chanFilter[fid].pid);
		chanFilter[fid].used = 0;
		if(chanFilter[fid].samepidnum)
		{
			unsigned short pid,j;
			
			pid = chanFilter[fid].pid;
			for(j = 0; j < max_filter_num; j++)
			{
				if ((chanFilter[j].used)&&(chanFilter[j].pid == pid))
				{
					if(chanFilter[j].samepidnum)
						chanFilter[j].samepidnum--;
				}
			}
		}
		chanFilter[fid].pid = 0xffff;
		chanFilter[fid].bytes = 0;
		chanFilter[fid].fid = -1;
		chanFilter[fid].stage  = CHAN_STAGE_START;
	    chanFilter[fid].cc = -1;	
		//chanFilter[fid].pid = 0xffff;
		if ((fid+1) == max_filter_num)
		{
			int k;
			int maxt = 0;
			for (k = 0; k < max_filter_num; k++)
			{
				if (chanFilter[k].used)
				{
					maxt = k+1;
				}   
			}
			max_filter_num = maxt;
		}
	}
	else
		DEBUG("invalid fid: %d\n", fid);
}

int free_filter(unsigned short pid)
{
	//	if(-1==pid)
	//		return -1;
	DEBUG("pid=%d\n", pid);
	
	int i = 0;
	for(i=0; i < MAX_CHAN_FILTER; i++)
	{
		if(pid==chanFilter[i].pid)
		{
#ifdef TUNER_INPUT
			MX_free_filter(chanFilter[i].fid);
#else
			TC_free_filter(chanFilter[i].fid);
#endif
			return 0;
		}
	}
	
	return -1;
}
	
static int get_filter(unsigned short pid)
{
	int i=0;
	
	for(i = 0; i < max_filter_num; i++)
	{
		if (chanFilter[i].pid == pid)
		{
			if(chanFilter[i].samepidnum)
			{
				chanFilter[MAX_CHAN_FILTER].pid = pid;
				return MAX_CHAN_FILTER;
			}
			else
				return i;
		}
	}
	return -1;
}

static int parse_payload(int fid, int p, int dlen, int start, unsigned char *ptr)
{
	int part=0;
	unsigned char *optr=NULL;
	Channel_t *chan = &chanFilter[fid];
	
	optr = ptr;
	if(start)
	{
		if(0!=chan->bytes){
			//if(0x19B==chanFilter[fid].pid || 0x19C==chanFilter[fid].pid || 0x19D==chanFilter[fid].pid || 0x190==chanFilter[fid].pid)
			if(0x190==(0x190&(chanFilter[fid].pid))){
				PRINTF("[%s] chanFilter[%d].pid=%d, chan->bytes=%d\n", hms_stamp(), fid, chanFilter[fid].pid, chan->bytes);
			}
			chan->bytes = 0;
		}
		chan->stage = CHAN_STAGE_HEADER;
	}
	else if(chan->stage==CHAN_STAGE_START)
	{
		PRINTF("chan->stage==CHAN_STAGE_START\n");
		return 0;
	}
	
// should make SURE sizeof(chan->buf)==FILTER_BUF_SIZE
	if((chan->bytes + dlen)>FILTER_BUF_SIZE){
		PRINTF("chanFilter[%d].pid=0x%x, chanFilter[%d].bytes=%d, overflow\n",fid,chanFilter[fid].pid,fid,chanFilter[fid].bytes);
		return 0;
	}
	
	if ((MULTI_BUF_SIZE - p) >= dlen)
	{
		memcpy(chan->buf+chan->bytes, optr+p, dlen);
		chan->bytes += dlen;
		p += dlen;
	}
	else
	{
		part = MULTI_BUF_SIZE - p;
		memcpy(chan->buf+chan->bytes, optr+p, part);
		chan->bytes += part;
		memcpy(chan->buf+chan->bytes, optr, dlen - part);
		chan->bytes += dlen - part;
		p = dlen - part;
	}
	//DEBUG("chan_bytes = [%d]\n",chan->bytes);
	if(chan->bytes<3){
		PRINTF("chan_bytes = [%d]\n",chan->bytes);
		return 0;
	}
	
	if(chan->stage==CHAN_STAGE_HEADER)
	{
		/*if((chan->buf[0]==0x00) && (chan->buf[1]==0x00) && (chan->buf[2]==0x01))
			{
		chan->type  = CHAN_TYPE_PES;
		chan->stage = CHAN_STAGE_PTS;
			}
		else*/
		if (chan->buf[0])
		{
			//chan->type  = CHAN_TYPE_SEC;
			chan->stage = CHAN_STAGE_PTR;
		}
		else if(chan->buf[1])
		{
			chan->stage = CHAN_STAGE_PTR;
		}
		else if(chan->buf[2]==0x01)
		{
			//chan->type  = CHAN_TYPE_PES;
			chan->stage = CHAN_STAGE_PTS;
		}
		else
		{
			//chan->type  = CHAN_TYPE_SEC;
			chan->stage = CHAN_STAGE_PTR;
		}
	}
retry:
	if(chan->stage==CHAN_STAGE_PTR)
	{
		int sec_len;
		int len = chan->buf[0]+1;
		int left = chan->bytes-len;
		//DEBUG("len = [%d] left[%d]\n",len,chan->bytes);
		/*if(chan->bytes<len)
		return 0;
		
		if(left)
		memmove(chan->buf, chan->buf+len, left);
		chan->bytes = left;*/
		if (left < 3)
			return 0;
		if(chan->buf[len]==0xFF)
		{
			chan->stage = CHAN_STAGE_END;
			if(0!=chan->bytes){
			//	PRINTF("222222222222222 chanFilter[%d].pid=%d, chan->bytes = %d\n", fid, chanFilter[fid].pid, chan->bytes);
				chan->bytes = 0;
			}
			return 0;
		}
		else
		{
			sec_len = ((chan->buf[len+1]<<8)|chan->buf[len+2])&0xFFF;
			sec_len += 3;
			
			chan->offset = len;
			chan->sec_len = sec_len;
			chan->stage = CHAN_STAGE_DATA_SEC;
			if(left<sec_len)
				return 0;
		}
	}
	else if(chan->stage==CHAN_STAGE_PTS)
	{
		//uint64_t pts;
		
		if(chan->bytes<14)
			return 0;
		
		/*if(parse_pts(chan->buf, &pts)>=0)
			{
		int offset = ftell(parser->fp)-parser->bytes+parser->parsed;
		if(chan->pts)
			{
		parser->rate = ((uint64_t)(offset-chan->offset))*1000/((pts-chan->pts)/90);
		//AM_DEBUG(2, "ts bits rate %d", parser->rate*8);
			}
		
		if(!chan->pts)
			{
		chan->pts    = pts;
		chan->offset = offset;
			}
		}*/
		
		chan->stage = CHAN_STAGE_DATA_PES;
	}
	//if(!chan->bytes)
	//	return 0;
	
//retry:
	if(chan->stage==CHAN_STAGE_DATA_SEC)
	{
		/*
		if(chan->bytes<1)
			return 0;
		
		if(chan->buf[0]==0xFF)
		{
			//DEBUG("eeeeeeeeeeeeeeeeeeeend \n");
			chan->stage = CHAN_STAGE_END;
		}
		else
		{
			int sec_len, left;
			
			if(chan->bytes<3)
				return 0;
			
			sec_len = ((chan->buf[1]<<8)|chan->buf[2])&0xFFF;
			//DEBUG("section len = [%d]\n",sec_len);
			sec_len += 3;
		*/
		
		if((chan->bytes - chan->offset) < chan->sec_len)
			return 0;
		{
			unsigned char *chanbuf=NULL;
			int sec_len=0,left=0;
					
			chanbuf = chan->buf + chan->offset;
			
			sec_len = chan->sec_len;
			if(*chanbuf == 0x3e)
			{
				if(0==tc_crc32(chanbuf,sec_len))
					send_mpe_sec_to_push_fifo(chanbuf, sec_len);
                else
                {
                	PRINTF("section crc error, sec_len=%d\n", sec_len);
                }
							
				//DEBUG("payload [%d]\n",total);
			}
#if 0
			else if(*chanbuf == tc_tid) 
            {	
            	if(chan->pid == tc_pid)
                {
					loader_section_handle(0, chanbuf, sec_len, NULL);
                }
                else
                	goto chandle;
			}
#endif

#ifdef TUNER_INPUT
#else
			else if (chan->pid == 0x1)
			{
				if(*chanbuf == 0x1)
				{
					ca_section_handle(0, chanbuf, sec_len, NULL);
				}
			}
#endif
			else
			{
				int j;
				for(j = 0; j < max_filter_num; j++)
				{
					unsigned char match = 1;
					unsigned char neq = 0;
					Channel_t *f = &chanFilter[j];
					int i = 0;
								
					if(!f->used||(f->pid != chan->pid)){
						continue;
					}
								
					for(i=0; i<DMX_FILTER_SIZE+2; i++)
					{
						unsigned char xor = chanbuf[i]^f->value[i];
										
						if(xor&f->maskandmode[i])
						{
							match = 0;
							break;
						}
										
						if(xor&f->maskandnotmode[i])
							neq = 1;
					}
									
					if(match && f->neq && !neq)
						match = 0;
					if(!match)
						continue;
					else
					{
						if (f->hdle)
						{
							//PRINTF("call fid=%d, chan->pid=0x%x\n", f->fid,chan->pid);
							f->hdle(f->fid, chanbuf, sec_len, f->userdata);
						}
						break;
					}
				}
                          }
				left = chan->bytes - sec_len - chan->offset;
				if(left>0)
				{
					//DEBUG("llllllllllleft some data!!!!!!!!!!!!!sec_len=%d [%d]\n",sec_len,left);
					if(chanbuf[sec_len]==0xFF)
					{
						chan->stage = CHAN_STAGE_END;
						if(0!=chan->bytes){
						//	PRINTF("3333333333333 chanFilter[%d].pid=%d, chan->bytes=%d left=%d, offset=%d\n", fid, chanFilter[fid].pid, chan->bytes,left, chan->offset);
							chan->bytes = 0;
						}
						
						return 0;
					}
					memmove(chan->buf, chanbuf+sec_len, left);
				}
				else
					left = 0;
				chan->bytes = left;
				if(left)
                {
                	// DEBUG("aaaaaaaaaaaaaa chanFilter[%d].pid=%d, left=%d,  sec_len=%d\n", fid, chanFilter[fid].pid, left, sec_len);
                	chan->stage = CHAN_STAGE_PTR;   //liukevin add
					goto retry;
                }

		}
	}
	else if (chan->stage==CHAN_STAGE_DATA_PES)
	{
		/*for(f=parser->filters; f; f=f->next)
			{
		if(f->enable && (f->params.pid==chan->pid) && (f->type==chan->type))
		break;
			}
		if(f)*/
		/*{
		int left = f->buf_size-f->bytes;
		int len = AM_MIN(left, chan->bytes);
		int pos = (f->begin+f->bytes)%f->buf_size;
		int cnt = f->buf_size-pos;
		
		if(cnt>=len)
			{
		memcpy(f->buf+pos, chan->buf, len);
			}
		else
			{
		int cnt2 = len-cnt;
		memcpy(f->buf+pos, chan->buf, cnt);
		memcpy(f->buf, chan->buf+cnt, cnt2);
			}
		
		pthread_cond_broadcast(&parser->cond);
		}*/
		//             handle_pes_packet(fid, chan->buf, chan->bytes, userdata);
		//DEBUG("get a pes packet\n");
		chan->bytes = 0;
		return 0;
	}
	else{
//		DEBUG("other packet, chan->stage=%d,fid=%d,chanFilter[%d].pid=%d\n",chan->stage,fid,fid,chanFilter[fid].pid);
	}
	
	if(chan->stage==CHAN_STAGE_END){
		if(0!=chan->bytes){
			//PRINTF("444444444444 chanFilter[%d].pid=%d, chan->bytes=%d\n", fid, chanFilter[fid].pid, chan->bytes);
			chan->bytes = 0;
		}
	}
	
	return 0;
}

// only for debug, print periodicity
static char s_ts_loss_log[1024];
void ts_loss_printf_periodicity()
{
	if(strlen(s_ts_loss_log)>0){
		PRINTF("%s\n", s_ts_loss_log);
		memset(s_ts_loss_log, 0, sizeof(s_ts_loss_log));
	}
}

void ts_loss_log_init()
{
	memset(s_ts_loss_log, 0, sizeof(s_ts_loss_log));
}

//static unsigned short last_pid=0;
int parse_ts_packet(unsigned char *ptr, int write_ptr, int *read)
{
	int p = *read;
	int left=0,p1=0,tmp=0,size=0,chan=0;
	unsigned char *optr=NULL;
	unsigned short pid=0;
	unsigned char  tei=0, cc=0, af_avail=0, p_avail=0, ts_start=0, sc=0;
	
	//STATic unsigned char precc = 0;
	
	//DEBUG("aaaaaaaa\n");
	optr = ptr;
	/*Scan the sync byte*/
	if (optr[p]!=0x47)
	{
resync:
		while(optr[p]!=0x47)
		{
			PRINTF("not 0x47\n");
			p++;
			if( p == write_ptr)
			{
				*read = p;
				return 0;
			}
			else{
				if(p>=MULTI_BUF_SIZE)
					p = 0;
			}
		}
		
		if ((p+188) < MULTI_BUF_SIZE)
		{
			if (optr[p+188] != 0x47)
			{
				PRINTF("optr[p+188]!=0x47\n");
				p++;
				goto resync;
			}
		}
		else
		{
			if (optr[p+188 - MULTI_BUF_SIZE] != 0x47)
			{
				PRINTF("optr[p+188-MULTI_BUF_SIZE]!=0x47\n");
				p++;
				if (p >= MULTI_BUF_SIZE)
					p = 0;
				goto resync;
			}
		}
	}
	
	if (write_ptr > p)
		size = write_ptr - p;
	else
		size = (MULTI_BUF_SIZE - p) + write_ptr;
	
	if (size < 188)
	{
		*read = p;
		PRINTF("size<188\n");
		return 0;
	}
	left = 188;
	p1 = p;
	p += 188;
	if ( p >= MULTI_BUF_SIZE)
		p = p - MULTI_BUF_SIZE;
	
	p1++;
	if (p1 >= MULTI_BUF_SIZE)
		p1 = 0;
	tmp =  optr[p1];
	
	//��������ʶ��
	tei  = tmp&0x80;
	//tei = ptr[1]&0x80;
	
	if(tei)
	{
		PRINTF("transport_error_indicator\n");
		goto end;
	}
	
	//��Ч���صĿ�ʼ���
	ts_start = tmp&0x40;
	//ts_start = ptr[1]&0x40;
	
	p1++;
	if (p1 >= MULTI_BUF_SIZE)
		p1 = 0;
	
	//��Ч���ص���������pid
	pid = ((tmp<<8)|optr[p1])&0x1FFF;
	
	//����ǿհ���ֱ���˳�
	if(pid==0x1FFF)
		goto end;
	
	p1++;
	if (p1 >= MULTI_BUF_SIZE)
		p1 = 0;
	tmp = optr[p1];
	
	//���ܱ�־λ,00��ʾδ����
	sc = tmp&0xC0;
	//sc  = ptr[3]&0xC0;
	
	//һ��4bit�ļ���������Χ0-15
	cc  = tmp&0x0F;
	//cc  = ptr[3]&0x0F;
	
	//�����ֶο���,��01������Ч���أ�10���������ֶΣ�11���е����ֶκ���Ч���ء�Ϊ00�Ļ������������д���
	af_avail = tmp&0x20;
	//af_avail = ptr[3]&0x20;
	p_avail  = tmp&0x10;
	//p_avail  = ptr[3]&0x10;
	
	p1++;
	if (p1 >= MULTI_BUF_SIZE)
		p1 = 0;
	//ptr += 4;
	left-= 4;
	
	if(af_avail)
	{
		/*left-= ptr[0]+1;
		ptr += ptr[0]+1;*/
		left -= optr[p1]+1;
		p1 += optr[p1]+1;
		if(p1 >= MULTI_BUF_SIZE)
			p1 = p1 - MULTI_BUF_SIZE;
	}
	
	if(p_avail && (left>0) && !sc)
	{
		chan = get_filter(pid);
//		if(last_pid!=pid)
//			DEBUG("get channel [%d][%x]\n",chan,pid);
//		last_pid = pid;
		if(chan != -1){
			if(cc !=  chanFilter[chan].cc) {
				
				if(((chanFilter[chan].cc + 1)&0x0f)!=cc)
				{
					snprintf(s_ts_loss_log+strlen(s_ts_loss_log), sizeof(s_ts_loss_log)-strlen(s_ts_loss_log),
						"[%s] ts loss (%02d,%02d)\n",hms_stamp(),chanFilter[chan].cc,cc);
				}
				
				chanFilter[chan].cc = cc;
				parse_payload(chan, p1, left, ts_start, ptr);
			}
			else
			{
				//PRINTF("sssssssssssssssssssssame 184\n");
			}
		}
	}
//	else{
//		DEBUG("ignor ts package: p_avail(%d), left(%d), sc(%d)\n", p_avail, left, sc);
//	}
	
end:
	*read = p;
	
	return 1;
}

void chanFilterInit(void)
{
	int i=0;
	
	for(i = 0; i < MAX_CHAN_FILTER; i++)
	{
		chanFilter[i].pid = -1;
		chanFilter[i].used = 0;
		chanFilter[i].bytes = 0;
		chanFilter[i].stage  = CHAN_STAGE_START;
		chanFilter[i].samepidnum = 0;
		chanFilter[i].fid = -1;
        chanFilter[i].cc = -1;
	}
        dmx_filter_init();
}

