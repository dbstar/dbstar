
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
#include "softdmx.h"
#include "prodrm20.h"
#include "dvbpush_api.h"
#include "bootloader.h"
#include "softdmx_print.h"
#include "porting.h"
#include "mid_push.h"

#define HIGH_PRIORITY_FILTER_NUM 2

typedef enum {
        CHAN_STAGE_START,
        CHAN_STAGE_HEADER,
        CHAN_STAGE_PTS,
        CHAN_STAGE_PTR,
        CHAN_STAGE_DATA_SEC,
        CHAN_STAGE_DATA_PES,
        CHAN_STAGE_END
} ChannelStage_t;

typedef void (*dataCb) (int fid, const unsigned char *data, int len, void *user_data);

typedef struct Channel Channel_t;
struct Channel {
        int              bytes;
        int              fid;
        int              offset;
        int              sec_len;
        void             *userdata;
        dataCb           hdle;
        unsigned short   pid;
        unsigned char    buf[FILTER_BUF_SIZE];
        unsigned char    used;
        unsigned char    value[DMX_FILTER_SIZE+2];
        unsigned char    maskandmode[DMX_FILTER_SIZE+2];
        unsigned char    maskandnotmode[DMX_FILTER_SIZE+2];
        unsigned char    neq;
        ChannelStage_t   stage;
        unsigned char    samepidnum;
};
//typedef struct Filterp Filter_param;
/*struct Filterp {
        unsigned char filter[DMX_FILTER_SIZE];
        unsigned char mask[DMX_FILTER_SIZE];
        unsigned char mode[DMX_FILTER_SIZE];
};
typedef struct Filterp Filter_param;
*/
Channel_t chanFilter[MAX_CHAN_FILTER+1];
int max_filter_num = 0;

#if 1
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
#endif

static void dump_bytes(int fid, const unsigned char *data, int len, void *user_data)
{
}

int MX_alloc_filter(unsigned short pid, Filter_param* param, dataCb hdle, void* userdata, char priority)
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
			//DEBUG("****************************allcoate a filter id[%d],num[%d],pid[0x%x]\n",i,m,pid);
			return i;
		}
	}
	return -1;
}

int alloc_filter(unsigned short pid, char pro)
{
	Filter_param param;
	
	DEBUG("pid=%d|0x%x, pro=%d\n", pid, pid, pro);
	
	memset(&param,0,sizeof(param));
	param.filter[0] = 0x3e;
	param.mask[0] = 0xff;
	
	return MX_alloc_filter(pid, &param, dump_bytes, NULL, pro);
}

void MX_free_filter(int fid)
{
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
			MX_free_filter(chanFilter[i].fid);
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
	//DEBUG("startmak = [%d],chanbytes[%d]\n",start,chan->bytes);
	optr = ptr;
	if(start)
	{
		chan->bytes = 0;
		chan->stage = CHAN_STAGE_HEADER;
	}
	else if(chan->stage==CHAN_STAGE_START)
	{
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
	if(chan->bytes<3)
		return 0;
	
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
			chan->bytes = 0;
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
	
retry:
	if(chan->stage==CHAN_STAGE_DATA_SEC)
	{
		/*				if(chan->bytes<1)
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
		sec_len += 3;*/
		if((chan->bytes - chan->offset) < chan->sec_len)
			return 0;
		{
			unsigned char *chanbuf=NULL;
			int sec_len=0,left=0;
			static int pnum = 0;
			
			chanbuf = chan->buf + chan->offset;
			sec_len = chan->sec_len;
			if(*chanbuf == 0x3e)
			{
				if(0==tc_crc32(chanbuf,sec_len))
				{
					send_mpe_sec_to_push_fifo(chanbuf, sec_len);
				    pnum ++;
				    if(pnum == 1000)
				    {
				        pnum = 0;	
				        DEBUG("get 1000 good mpe packet  [%d]\n",sec_len);
				    }	
				}
				else
				{
					DEBUG("mpe crc error !!!!!!!!!! [%d]\n",sec_len);
				}
			}
#if 0
			else if(*chanbuf == tc_tid) 
                        {//0xf1){
				//DEBUG("chan->pid: 0x%x\n", chan->pid);
                                if(chan->pid == tc_pid)
                                {
				    loader_section_handle(0, chanbuf, sec_len, NULL);
                                }
                                else
                                    goto chandle;
			}

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
				left = chan->bytes - sec_len - chan->offset;
				if(left>0)
				{
					//DEBUG("llllllllllleft some data!!!!!!!!!!!!! [%d]\n",left);
					if(chanbuf[sec_len]==0xFF)
					{
						chan->stage = CHAN_STAGE_END;
						chan->bytes = 0;
						return 0;
					}
					memmove(chan->buf, chanbuf+sec_len, left);
				}
				else
					left = 0;
					chan->bytes = left;
					if(left)
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
	
	if(chan->stage==CHAN_STAGE_END)
		chan->bytes = 0;
	
	return 0;
}

//static unsigned short last_pid=0;
int parse_ts_packet(unsigned char *ptr, int write_ptr, int *read)
{
	static int p = 0;
	int left=0,p1=0,tmp=0,size=0,chan=0;
	unsigned char *optr=NULL;
	unsigned short pid=0;
	unsigned char  tei=0, cc=0, af_avail=0, p_avail=0, ts_start=0, sc=0;
	
	//DEBUG("aaaaaaaa\n");
	optr = ptr;
	/*Scan the sync byte*/
	if (optr[p]!=0x47)
	{
resync:
		while(optr[p]!=0x47)
		{
			DEBUG("eeeeeeeeeeeerror\n");
			p++;
			if( p == write_ptr)
			{
				*read = p;
				return 0;
			}
			else
				if(p>=MULTI_BUF_SIZE)
			p = 0;
		}
		
		if ((p+188) < MULTI_BUF_SIZE)
		{
			if (optr[p+188] != 0x47)
			{
				p++;
				goto resync;
			}
		}
		else
		{
			if (optr[p+188 - MULTI_BUF_SIZE] != 0x47)
			{
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
		return 0;
	}
	left = 188;
	p1 = p;
	p += 188;
	if ( p >= MULTI_BUF_SIZE)
		p = p - MULTI_BUF_SIZE;
	*read = p;
	//ptr = &optr[p];//p;
	//t
	//return 0;
	
	p1++;
	if (p1 >= MULTI_BUF_SIZE)
		p1 = 0;
	tmp =  optr[p1];
	tei  = tmp&0x80;
	//tei = ptr[1]&0x80;
	ts_start = tmp&0x40;
	//ts_start = ptr[1]&0x40;
	tmp = optr[p1];
	p1++;
	if (p1 >= MULTI_BUF_SIZE)
		p1 = 0;
	pid = ((tmp<<8)|optr[p1])&0x1FFF;
	
	p1++;
	if (p1 >= MULTI_BUF_SIZE)
		p1 = 0;
	tmp = optr[p1];
	sc = tmp&0xC0;
	//sc  = ptr[3]&0xC0;
	cc  = tmp&0x0F;
	//cc  = ptr[3]&0x0F;
	af_avail = tmp&0x20;
	//af_avail = ptr[3]&0x20;
	p_avail  = tmp&0x10;
	//p_avail  = ptr[3]&0x10;
	
	if(pid==0x1FFF)
		goto end;
	
	if(tei)
	{
		//AM_DEBUG(3, "ts error");
		goto end;
	}
	
	//chan = get_filter(pid);
	/*if(chan == -1)
	goto end;*/
	/*chan = parser_get_chan(parser, pid);
	if(!chan)
	goto end;
	*/
	/*if(chan->cc!=0xFF)
	{
	if(chan->cc!=cc)
	AM_DEBUG(3, "discontinuous");
	}*/
	
	/*if(p_avail)
	cc = (cc+1)&0x0f;
	chan->cc = cc;*/
	
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
		if(chan != -1)
			parse_payload(chan, p1, left, ts_start, ptr);
	}
	
end:
	return 1;
}

extern void dmx_filter_init(void);
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
	}
	dmx_filter_init();
}

