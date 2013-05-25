/*
** Copyright 2007, The Android Open Source Project
**
** Licensed under the Apache License, Version 2.0 (the "License");
** you may not use this file except in compliance with the License.
** You may obtain a copy of the License at
**
**     http://www.apache.org/licenses/LICENSE-2.0
**
** Unless required by applicable law or agreed to in writing, software
** distributed under the License is distributed on an "AS IS" BASIS,
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
** See the License for the specific language governing permissions and
** limitations under the License.
*/

#define LOG_NDEBUG 0
#define LOG_TAG "AmlogicPlayer"
#include "utils/Log.h"
#include <stdio.h>
#include <assert.h>
#include <limits.h>
#include <unistd.h>
#include <fcntl.h>
#include <sched.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <utils/String8.h>

#include <surfaceflinger/Surface.h>
#include <gui/ISurfaceTexture.h>
#include <gui/SurfaceTextureClient.h>
#include <surfaceflinger/ISurfaceComposer.h>

#include <android/native_window.h>
#include "AmlogicPlayerRender.h"
#include <ui/Rect.h>

#include "AmlogicPlayerExtractorDemux.h"

//#include <ui/Overlay.h>
#define  TRACE()	LOGV("[%s::%d]\n",__FUNCTION__,__LINE__)
//#define  TRACE()

#include <cutils/properties.h>

extern int android_datasource_init(void);

#include "AmlogicPlayer.h"
#include "Amvideoutils.h"

#ifndef FBIOPUT_OSD_SRCCOLORKEY
#define  FBIOPUT_OSD_SRCCOLORKEY    0x46fb
#endif

#ifndef FBIOPUT_OSD_SRCKEY_ENABLE
#define  FBIOPUT_OSD_SRCKEY_ENABLE  0x46fa
#endif

#ifndef FBIOPUT_OSD_SET_GBL_ALPHA
#define  FBIOPUT_OSD_SET_GBL_ALPHA	0x4500
#endif


#ifdef HAVE_GETTID
static pid_t myTid() { return gettid(); }
#else
static pid_t myTid() { return getpid(); }
#endif

// ----------------------------------------------------------------------------

namespace android {
#ifndef MIN
#define MIN(x,y) ((x)<(y)?(x):(y))
#endif
// ----------------------------------------------------------------------------

// TODO: Determine appropriate return codes
static status_t ERROR_NOT_OPEN = -1;
static status_t ERROR_OPEN_FAILED = -2;
static status_t ERROR_ALLOCATE_FAILED = -4;
static status_t ERROR_NOT_SUPPORTED = -8;
static status_t ERROR_NOT_READY = -16;
static status_t STATE_INIT = 0;
static status_t STATE_ERROR = 1;
static status_t STATE_OPEN = 2;



static URLProtocol android_protocol;

#define MID_800_400_FREESC	(0x10001)

AmlogicPlayer::AmlogicPlayer() :
    mPlayTime(0),  mStreamTime(0),mDuration(0),
	mState(STATE_ERROR),
    mStreamType(-1), mLoop(false), 
    mExit(false), mPaused(false), mRunning(false),
    mPlayer_id(-1),
    mWidth(0), mHeight(0),
    mhasVideo(1),  mhasAudio(1),   
    mIgnoreMsg(false),
    mTypeReady(false),
    mAudioTrackNum(0),       
    mVideoTrackNum(0),
    mInnerSubNum(0),
    mHttpWV(false),
    mDecryptHandle(NULL),
    mDrmManagerClient(NULL),
    isHDCPFailed(false),
    isWidevineStreaming(false)
{
	Mutex::Autolock l(mMutex);
	streaminfo_valied=false;
	mStrCurrentVideoCodec = NULL;
	mStrCurrentAudioCodec = NULL;
	mAudioExtInfo = NULL; 
	mSubExtInfo = NULL;
	mVideoExtInfo = NULL;
	mChangedCpuFreq=false;
	mInbuffering=false; 
	PlayerStartTimeUS=ALooper::GetNowUs();
	mLastPlayTimeUpdateUS=ALooper::GetNowUs();
  	LOGV("AmlogicPlayer constructor\n");
  	memset(&mAmlogicFile, 0, sizeof mAmlogicFile);
	memset(&mPlay_ctl,0,sizeof mPlay_ctl);
	memset(mTypeStr,0,sizeof(mTypeStr));
	memset(&mStreamInfo,0,sizeof(mStreamInfo));
	curLayout=Rect(0,0,0,0);
	video_rotation_degree=0;
	fastNotifyMode=0;
	mEnded=false;
	mLowLevelBufMode=false;
	LatestPlayerState=PLAYER_INITING;
	mDelayUpdateTime=0;
	isTryDRM=false;
	mNeedResetOnResume=0;
	mStopFeedingBuf_ms=PropGetFloat("media.amplayer.stopbuftime")*1000;
	mStopFeedingBufLevel=PropGetFloat("media.amplayer.stopbuflevel");
	if(mStopFeedingBufLevel<0.0000001)
			mStopFeedingBufLevel=0.03;

	mHWaudiobufsize=384*1024;
	mHWvideobufsize=7*1024*1024;
	mStreamTimeExtAddS=PropGetFloat("media.amplayer.streamtimeadd");
	if(mStreamTimeExtAddS<=0)
		mStreamTimeExtAddS=10000;
	mLastStreamTimeUpdateUS=ALooper::GetNowUs();
		
}

int HistoryMgt(const char * path,int r0w1,int mTime)
{
  ///this a simple history mgt;only save the latest file,and playingtime,and must be http;
  static char static_path[1024]="";
  static Mutex HistoryMutex;
  static int lastplayingtime=-1;
  Mutex::Autolock l(HistoryMutex);
  LOGV("History mgt old[%s,%d,%d]\n",static_path,0,lastplayingtime);
  LOGV("History mgt    [%s,%d,%d]\n",path,r0w1,mTime);
  if(!r0w1)//read
  {
	if(strcmp(path,static_path)==0)
		return lastplayingtime;
		
  }else{//save
	if(strlen(path)>1024-1 || strlen(path)<10)
		return 0;
	if(memcmp(path,"http://",7)==0 || memcmp(path,"shttp://",8)==0 )//not http,we don't save it now;
	{
 		strcpy(static_path,path);
        	lastplayingtime=mTime;
		return 0;
	}
  }
  return 0;
}

status_t AmlogicPlayer::BasicInit()
{
	static int have_inited=0;
	if(!have_inited)
	{
		char dir[PROPERTY_VALUE_MAX];
		int cachesize, blocksize;
		player_init();
		URLProtocol *prot=&android_protocol;
		prot->name="android";
	    prot->url_open=(int (*)(URLContext *, const char *, int ))vp_open;
	    prot->url_read=(int (*)(URLContext *, unsigned char *, int))vp_read;
	    prot->url_write=(int (*)(URLContext *, unsigned char *, int))vp_write;
	    prot->url_seek=(int64_t (*)(URLContext *, int64_t , int))vp_seek;
	    prot->url_close=(int (*)(URLContext *))vp_close;
	    prot->url_get_file_handle = (int (*)(URLContext *))vp_get_file_handle;
		av_register_protocol(prot);
		AmlogicPlayerStreamSource::init();
		have_inited++;
		if(PropIsEnable("media.amplayer.cacheenable")){
                        if(property_get("media.amplayer.cachedir", dir, NULL)>0)
                                ;
                        else
                                dir[0]='\0';/*clear the dir path*/
                        cachesize=(int)PropGetFloat("media.amplayer.cachesize");
                        blocksize=(int)PropGetFloat("media.amplayer.cacheblocksize");
                        player_cache_system_init(1,dir,cachesize,blocksize);
                }
		AmlogicPlayerDataSouceProtocol::BasicInit();
		AmlogicPlayerExtractorDemux::BasicInit();
	}
	return 0;
}



 bool AmlogicPlayer::PropIsEnable(const char* str)
{ 
	char value[PROPERTY_VALUE_MAX];
	if(property_get(str, value, NULL)>0)
	{
		if ((!strcmp(value, "1") || !strcmp(value, "true") || !strcmp(value, "ok")))
		{
			LOGI("%s is enabled\n",str);
			return true;
		}
	}
	LOGI("%s is disabled\n",str);
	return false;
}


 float AmlogicPlayer::PropGetFloat(const char* str)
{ 
	char value[PROPERTY_VALUE_MAX];
	float ret=0.0;
	if(property_get(str, value, NULL)>0)
	{
		if ((sscanf(value,"%f",&ret))>0)
		{
			LOGI("%s is set to %f\n",str,ret);
			return ret;
		}
	}
	LOGI("%s is not set\n",str);
	return ret;
}

status_t AmlogicPlayer::exitAllThreads()
{
	AmlogicPlayer::BasicInit();
	pid_info_t playerinfo;

	player_list_allpid(&playerinfo);
	LOGI("found %d not exit player threads,try exit it now\n",playerinfo.num);
	if(playerinfo.num>0)
	{
		int i;
		for(i=0;i<playerinfo.num;i++)
			{
			//player_exit(playerinfo.pid[i]);
			}
	}
	return NO_ERROR;
}

void AmlogicPlayer::onFirstRef()
{
	Mutex::Autolock l(mMutex);
    LOGV("onFirstRef");
	AmlogicPlayer::BasicInit();
	AmlogicPlayer::exitAllThreads();
	av_log_set_level(50);
    // create playback thread
    mState = STATE_INIT;
}

status_t AmlogicPlayer::initCheck()
{
	Mutex::Autolock l(mMutex);
	LOGV("initCheck");
    if (mState != STATE_ERROR) 
			return NO_ERROR;
    return ERROR_NOT_READY;
}

int get_sysfs_int(const char *path)
{
    int fd;
    int val = 0;
    char  bcmd[16];
    fd = open(path, O_RDONLY);
    if (fd >= 0) {
        read(fd, bcmd, sizeof(bcmd));
        val = strtol(bcmd, NULL, 10);
        close(fd);
    }
    return val;
}

int set_sys_int(const char *path,int val)
{
    	int fd;
        char  bcmd[16];
        fd=open(path, O_CREAT|O_RDWR | O_TRUNC, 0644);
        if(fd>=0)
        {
        	sprintf(bcmd,"%d",val);
        	write(fd,bcmd,strlen(bcmd));
        	close(fd);
        	return 0;
        }
	LOGV("set fs%s=%d failed\n",path,val);
        return -1;
}
#define DISABLE_VIDEO "/sys/class/video/disable_video"
void 
AmlogicPlayer::VideoViewOn(void)
{
	int ret=0;
	//disable_freescale(MID_800_400_FREESC);
	//GL_2X_scale(1);
	//disable_freescale_MBX();
    	ret=player_video_overlay_en(1);
	LOGV("VideoViewOn=%d\n",ret);
	//OsdBlank("/sys/class/graphics/fb0/blank",1);
	if(!PropIsEnable("media.amplayer.displast_frame")){
		set_sys_int(DISABLE_VIDEO,2);
	}
}
void 
AmlogicPlayer::VideoViewClose(void)
{
	int ret=0;
    	ret=player_video_overlay_en(0);
	if(!PropIsEnable("media.amplayer.displast_frame")){
		set_sys_int(DISABLE_VIDEO,2);
	}
	enable_freescale(MID_800_400_FREESC);
	//GL_2X_scale(0);
	//enable_freescale_MBX();
	LOGV("VideoViewClose=%d\n",ret);
	//OsdBlank("/sys/class/graphics/fb0/blank",0);

}

void 
AmlogicPlayer::SetCpuScalingOnAudio(float mul_audio){
	const char InputFile[] = "/sys/class/audiodsp/codec_mips";
	const char OutputFile[] = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq";
	int val;
	val=get_sysfs_int(InputFile);
	if(val>0 && mul_audio >0){
		val=mul_audio*val;
		set_sys_int(OutputFile,val);
		LOGV("set_cpu_freq_scaling_based_auido %d\n",val);
	}else{
		LOGV("set_cpu_freq_scaling_based_auido failed\n");
	}
}

AmlogicPlayer::~AmlogicPlayer() {
    LOGV("AmlogicPlayer destructor\n");
	Mutex::Autolock l(mMutex);
    release();
	if(mStrCurrentAudioCodec){
		free(mStrCurrentAudioCodec);
		mStrCurrentAudioCodec = NULL;
	}	
	if(mStrCurrentVideoCodec){
		free(mStrCurrentVideoCodec);
		mStrCurrentVideoCodec = NULL;
	}
	if(mAudioExtInfo){
		free(mAudioExtInfo);
		mAudioExtInfo = NULL;
	}
	if(mSubExtInfo){
		free(mSubExtInfo);
		mSubExtInfo = NULL;
	}	
	if(mVideoExtInfo){
		free(mVideoExtInfo);
		mVideoExtInfo = NULL;
	}

	
}

status_t AmlogicPlayer::setDataSource(
        const char *uri, const KeyedVector<String8, String8> *headers) 
{
    LOGV("setDataSource");
    if(PropIsEnable("media.amplayer.useandroidhttp") &&  !strncmp(uri,"http://",strlen("http://"))){	
    	mSouceProtocol=AmlogicPlayerDataSouceProtocol::CreateFromUrl(uri,headers);
		return setdatasource(mSouceProtocol->GetPathString(), -1, 0, 0x7ffffffffffffffLL, NULL);	
    }else {
        if(PropIsEnable("media.amplayer.widevineenable") && 
            !strncmp(uri,"widevine://",strlen("widevine://")))
         {
    		mSouceProtocol=AmlogicPlayerDataSouceProtocol::CreateFromUrl(uri,headers);
            if(mSouceProtocol.get()!=NULL){
		  mPlay_ctl.auto_buffing_enable=1;
		  isTryDRM=true;
                return setdatasource(mSouceProtocol->GetPathString(), -1, 0, 0x7ffffffffffffffLL, NULL);	
            }
    	}
    }
    return setdatasource(uri, -1, 0, 0x7ffffffffffffffLL, headers); // intentionally less than LONG_MAX
}


status_t AmlogicPlayer::setDataSource(int fd, int64_t offset, int64_t length)
{	
	LOGV("setDataSource,fd=%d,offset=%lld,len=%lld,not finished\n",fd,offset,length);
	return setdatasource(NULL,fd,offset,length, NULL);
}
int AmlogicPlayer:: setDataSource(const sp<IStreamSource> &source) 
{
	mSource=source;
	mStreamSource=new AmlogicPlayerStreamSource(source);
	fastNotifyMode=1;
	mLowLevelBufMode=true;
	mPlay_ctl.auto_buffing_enable=1;/*istream mode.maybe network,used auto buffering.*/
	return setdatasource(mStreamSource->GetPathString(),-1, 0, 0x7ffffffffffffffLL,NULL);
}
int AmlogicPlayer::vp_open(URLContext *h, const char *filename, int flags)
{	
	/*
	sprintf(file,"android:AmlogicPlayer=[%x:%x],AmlogicPlayer_fd=[%x:%x]",
	*/
	LOGV("vp_open=%s\n",filename);
	if(strncmp(filename,"android",strlen("android"))==0)
	{	
		unsigned int fd=0,fd1=0;
		char *str=strstr(filename,"AmlogicPlayer_fd");
		if(str==NULL)
			return -1;
		sscanf(str,"AmlogicPlayer_fd=[%x:%x]\n",(unsigned int*)&fd,(unsigned int*)&fd1);
		if(fd!=0 && ((unsigned int)fd1==~(unsigned int)fd))
		{
			AmlogicPlayer_File* af= (AmlogicPlayer_File*)fd;
			h->priv_data=(void*) fd;
			if(af!=NULL && af->fd_valid)
			{
				
				lseek(af->fd, af->mOffset, SEEK_SET);
				af->mCurPos=af->mOffset;
				LOGV("android_open %s OK,h->priv_data=%p\n",filename,h->priv_data);
				return 0;
			}
			else
			{
				LOGV("android_open %s Faild\n",filename);
				return -1;
			}
		}
	}
	return -1;
}

int AmlogicPlayer::vp_read(URLContext *h, unsigned char *buf, int size)
{	
	AmlogicPlayer_File* af= (AmlogicPlayer_File*)h->priv_data;
	int ret;
	int len=MIN(size,(af->mOffset+af->mLength-af->mCurPos));
	if(len<=0)
		return 0;/*read end*/
	//LOGV("start%s,pos=%lld,size=%d,ret=%d\n",__FUNCTION__,(int64_t)lseek(af->fd, 0, SEEK_CUR),size,ret);
	ret=read(af->fd,buf,len);
	//LOGV("end %s,size=%d,ret=%d\n",__FUNCTION__,size,ret);
	if(ret>0)
		af->mCurPos+=ret;
	return ret;
}

int AmlogicPlayer::vp_write(URLContext *h, unsigned char *buf, int size)
{	
	AmlogicPlayer_File* af= (AmlogicPlayer_File*)h->priv_data;
	LOGV("%s\n",__FUNCTION__);
	return -1;
}
int64_t AmlogicPlayer::vp_seek(URLContext *h, int64_t pos, int whence)
{
	AmlogicPlayer_File* af= (AmlogicPlayer_File*)h->priv_data;
	int64_t ret;
	int64_t newsetpos;	
	//LOGV("%sret=%lld,pos=%lld,whence=%d,tell=%lld\n",__FUNCTION__,(int64_t)0,pos,whence,(int64_t)lseek(af->fd,0,SEEK_CUR));
	if (whence == AVSEEK_SIZE)
	{
		return af->mLength;
	}
	switch(whence){
		case SEEK_CUR:
			newsetpos=af->mCurPos+pos;
			break;
		case SEEK_END:	
			newsetpos=af->mOffset+af->mLength+pos;
			break;
		case SEEK_SET:
			newsetpos=af->mOffset+pos;
			break;
		default:
			return -1;/*unsupport other case;*/
	}
	if(newsetpos>(af->mOffset+af->mLength) || newsetpos<af->mOffset){
		return -1;/*out stream range*/
	}
	ret=lseek(af->fd,newsetpos, SEEK_SET);
	if(ret>=0){
		af->mCurPos=ret;
		return ret-af->mOffset;
	}else{
		return ret;
	}
	return -1;
}


int AmlogicPlayer::vp_close(URLContext *h)
{
	FILE* fp= (FILE*)h->priv_data;
	LOGV("%s\n",__FUNCTION__);
	return 0; /*don't close file here*/
	//return fclose(fp);
}

int AmlogicPlayer::vp_get_file_handle(URLContext *h)
{
	LOGV("%s\n",__FUNCTION__);
    return (intptr_t) h->priv_data;
}

status_t AmlogicPlayer::UpdateBufLevel(hwbufstats_t *pbufinfo)
{
	if( !pbufinfo || !mLowLevelBufMode || (LatestPlayerState<PLAYER_INITOK) || (LatestPlayerState>PLAYER_ERROR))
		return 0;
	mHWaudiobufsize=pbufinfo->abufsize;
	mHWvideobufsize=pbufinfo->vbufsize;
	if((!pbufinfo->abufused || pbufinfo->adatasize>mStopFeedingBufLevel*pbufinfo->abufsize) && 
		(!pbufinfo->vbufused || pbufinfo->vdatasize>mStopFeedingBufLevel*pbufinfo->vbufsize) )
	{	/*used buf data reached feeding level*/
		mStreamSource->feedMoreData(0);/*stop feeding*/
	}else{
		int needdata=0;
		if(pbufinfo->vbufused && pbufinfo->vdatasize<mStopFeedingBufLevel*pbufinfo->vbufsize )
			needdata+=pbufinfo->vbufsize*mStopFeedingBufLevel-pbufinfo->vdatasize;
		if(pbufinfo->vbufused && pbufinfo->adatasize<mStopFeedingBufLevel*pbufinfo->abufsize)
			needdata+=pbufinfo->abufsize *mStopFeedingBufLevel-pbufinfo->adatasize;
		if(needdata<=0)//maybe unkonw reaon,still filled..
		{/*if one buffer have more data and another no data,keep feeding.*/
			needdata+=188*2;
		}
		mStreamSource->feedMoreData(needdata);/*need more data*/
	}
	return 0;
}
int AmlogicPlayer::notifyhandle(int pid,int msg,unsigned long ext1,unsigned long ext2)
{
	AmlogicPlayer *player =(AmlogicPlayer *)player_get_extern_priv(pid);
	if(player!=NULL)
		return player->NotifyHandle(pid,msg,ext1,ext2);
	else
		return -1;
}
int AmlogicPlayer::NotifyHandle(int pid,int msg,unsigned long ext1,unsigned long ext2)
{ 
	player_file_type_t *type;
	int ret;
	switch(msg)
		{
			case PLAYER_EVENTS_PLAYER_INFO:
				return UpdateProcess(pid,(player_info_t *)ext1);
				break;
			case PLAYER_EVENTS_STATE_CHANGED:
			case PLAYER_EVENTS_ERROR:
			case PLAYER_EVENTS_BUFFERING:
				break;
			case PLAYER_EVENTS_FILE_TYPE:
				{
				type=(player_file_type_t *)ext1;
				mhasAudio=type->audio_tracks;
				mhasVideo=type->video_tracks;
				strncpy(mTypeStr,type->fmt_string,64);
				mTypeStr[63]='\0';
				LOGV("Type=%s,videos=%d,audios=%d\n",type->fmt_string,mhasVideo,mhasAudio);
				if(!strcmp(type->fmt_string, "DRMdemux")) {
					isWidevineStreaming = true;
					LOGV("It is WidevineStreaming!\n");
				}
				if(!PropIsEnable("media.amplayer.hdmicloseauthen") && isWidevineStreaming){
					ret = amvideo_utils_get_hdmi_authenticate();
					LOGV("hdcp authenticate : %d\n",ret);
					if (ret == HDMI_HDCP_FAILED) {
						isHDCPFailed = true;
						LOGV("hdcp authenticate failed, it will close video!\n");
					}
				}
				mTypeReady=true;
				sendEvent(0x11000);
					if(strstr(mTypeStr,"mpeg")!=NULL){/*mpeg,ts,ps\,may can't detect types here.*/
						mhasVideo=1;
						mhasAudio=1;
					}
					else if ((mDecryptHandle==NULL) && 
							 (strstr(mTypeStr,"DRMdemux")!=NULL)) {
						if(mSouceProtocol.get()!=NULL) {
							mSouceProtocol->getDrmInfo(mDecryptHandle, &mDrmManagerClient);

							if (mDecryptHandle == NULL) {
								LOGE("after getDrmInfo, mDecryptHandle = NULL");
							}
							if (mDrmManagerClient == NULL) {
								LOGE("after getDrmInfo, mDrmManagerClient = NULL");
							}		

							if (mDecryptHandle != NULL &&mDrmManagerClient !=NULL) {
								LOGV("L%d:getmDecryptHandle", __LINE__);
								if (RightsStatus::RIGHTS_VALID != mDecryptHandle->status) {
						    		//notifyListener_l(MEDIA_ERROR, MEDIA_ERROR_UNKNOWN, ERROR_DRM_NO_LICENSE);
						    		LOGE("L%d:getDrmInfo error", __LINE__);
								}else {
									mDrmManagerClient->setPlaybackStatus(mDecryptHandle,
										Playback::START, (int64_t)(mPlay_ctl.t_pos * 1000));
								}
							}
						}
					}
				break;
				}
			case PLAYER_EVENTS_HTTP_WV:
			{
				mHttpWV = true;
				sendEvent(0x12000);
				LOGV("Get http wvm, goto WVM Extractor");	
			}
				break;
			case PLAYER_EVENTS_HWBUF_DATA_SIZE_CHANGED:
				{
				hwbufstats_t *pbufinfo=(hwbufstats_t *)ext1;
				UpdateBufLevel(pbufinfo);
				}
				break;
			default:
				break;
		}
	return 0;
}

int AmlogicPlayer::UpdateProcess(int pid,player_info_t *info)
{

	LOGV("update_process pid=%d, current=%d,status=[%s]\n", pid, info->current_time,player_status2str(info->status));
	if(mIgnoreMsg && info->status!=PLAYER_ERROR)	
		return 0;

	LatestPlayerState=info->status;
	if(info->status!=PLAYER_ERROR&&info->error_no!=0){
		if(info->error_no ==PLAYER_NO_VIDEO){
			sendEvent(MEDIA_INFO,MEDIA_INFO_AMLOGIC_NO_VIDEO);
			LOGW("player no video\n");
		}
		else if(info->error_no ==PLAYER_NO_AUDIO){
			sendEvent(MEDIA_INFO,MEDIA_INFO_AMLOGIC_NO_AUDIO);
			LOGW("player no audio\n");
		}
		else if(info->error_no == PLAYER_UNSUPPORT_VCODEC||info->error_no ==PLAYER_UNSUPPORT_VIDEO){
			LOGW("player video not supported\n");
			sendEvent(MEDIA_INFO,MEDIA_INFO_AMLOGIC_VIDEO_NOT_SUPPORT);
		}
		else if(info->error_no == PLAYER_UNSUPPORT_ACODEC||info->error_no == PLAYER_UNSUPPORT_AUDIO){
			LOGW("player audio not supported\n");
			sendEvent(MEDIA_INFO,MEDIA_INFO_AMLOGIC_AUDIO_NOT_SUPPORT);
		}		
	}
	else if(	info->status==PLAYER_BUFFERING)
	{
		if(mDuration>0)
			sendEvent(MEDIA_BUFFERING_UPDATE,mPlayTime*100/mDuration);
		if(!mInbuffering){
			mInbuffering=true;
			if(!mLowLevelBufMode)
				sendEvent(MEDIA_INFO,MEDIA_INFO_BUFFERING_START);
		}
	}
	else if(info->status==PLAYER_INITOK)
	{	
		updateMediaInfo();
                if(info->full_time_ms!=-1)
                        mDuration=info->full_time_ms;
		else if(info->full_time!=-1)
			mDuration=info->full_time*1000;
		if(video_rotation_degree==1 || video_rotation_degree==3)
			sendEvent(MEDIA_SET_VIDEO_SIZE,mHeight,mWidth);// 90du,or 270du
		else
			sendEvent(MEDIA_SET_VIDEO_SIZE,mWidth,mHeight);
		if(!fastNotifyMode)///fast mode,will send before,do't send again
			sendEvent(MEDIA_PREPARED);
	}
	else if(info->status==PLAYER_STOPED || info->status==PLAYER_PLAYEND)
	{
		LOGV("Player status:%s, playback complete",player_status2str(info->status));
		if(mHttpWV == false) {
		if(!mEnded)
			sendEvent(MEDIA_PLAYBACK_COMPLETE);
		}
		mEnded=true;
	}
	else if(info->status==PLAYER_EXIT )
	{
		LOGV("Player status:%s, playback exit",player_status2str(info->status));
		mRunning=false;
		if(mHttpWV == false) {
		if (!mLoop && (mState != STATE_ERROR) && (!mEnded))//no errors & no loop^M
			sendEvent(MEDIA_PLAYBACK_COMPLETE);
		}
		mEnded=true;
		if(isHDCPFailed == true){
			set_sys_int(DISABLE_VIDEO,2);
			isHDCPFailed = false;
			LOGV("[L%d]:Enable Video", __LINE__);
		}
	}
	else if(info->status==PLAYER_ERROR)
	{
		if(mHttpWV == false) {
			sendEvent(MEDIA_ERROR,MEDIA_ERROR_UNKNOWN,info->error_no);
			//sendEvent(MEDIA_ERROR,MEDIA_ERROR_UNKNOWN,info->error_no);
			LOGV("Player status:%s, error occur",player_status2str(info->status));
			//sendEvent(MEDIA_ERROR);
			sendEvent(MEDIA_ERROR,MEDIA_ERROR_UNKNOWN,-1);
			mState = STATE_ERROR;
			if(isHDCPFailed == true){
				set_sys_int(DISABLE_VIDEO,2);
				isHDCPFailed = false;
				LOGV("[L%d]:Enable Video", __LINE__);
			}
		}
	}
	else
	{
		if(	info->status==PLAYER_SEARCHING)
		{
			if(mDuration>0)
				sendEvent(MEDIA_BUFFERING_UPDATE,mPlayTime*100/mDuration);
			if(!mInbuffering){
				mInbuffering=true;
				sendEvent(MEDIA_INFO,MEDIA_INFO_BUFFERING_START);
			}
		}
		int percent=0;
		if(mInbuffering && info->status!=PLAYER_SEARCHING){
			mInbuffering=false;
			sendEvent(MEDIA_INFO,MEDIA_INFO_BUFFERING_END);
		}
		if(mDuration>0)
			percent=(mPlayTime)*100/(mDuration);
		else
			percent=0;

		if(info->status==PLAYER_SEARCHOK)
		{ 
			///sendEvent(MEDIA_SEEK_COMPLETE);
		}
                if(info->full_time_ms!=-1)
                        mDuration=info->full_time_ms;
		else if(info->full_time!=-1)
			mDuration=info->full_time*1000;
		if(info->current_ms>=100 && mDelayUpdateTime--<=0){
		 	mPlayTime=info->current_ms;	
			mLastPlayTimeUpdateUS=ALooper::GetNowUs();
		}if(info->current_pts!=0 && info->current_pts!=0xffffffff){
			mStreamTime=info->current_pts/90;/*pts(90000hz)->ms*/
			mLastStreamTimeUpdateUS=ALooper::GetNowUs();
		}

		
		LOGV("Playing percent =%d,mPlayTime:%d,mStreamTime:%d\n",percent,mPlayTime,mStreamTime);
	 	if(streaminfo_valied && mDuration>0&& info->bufed_time>0)
		{
			percent=(info->bufed_time*100/(mDuration/1000));
			LOGV("Playing percent on percent=%d,bufed time=%dS,Duration=%dS\n",percent,info->bufed_time,mDuration/1000);
		}else if(streaminfo_valied && mDuration>0&& info->bufed_pos>0 && mStreamInfo.stream_info.file_size>0)
		{
		
			percent=(info->bufed_pos*100/(mStreamInfo.stream_info.file_size));
			LOGV("Playing percent on percent=%d,bufed pos=%lld,Duration=%lld\n",percent,info->bufed_pos,(mStreamInfo.stream_info.file_size));
		}
		else if(mDuration >0 && streaminfo_valied && mStreamInfo.stream_info.file_size>0)
		{
			percent+=((long long)4*1024*1024*100*info->audio_bufferlevel/mStreamInfo.stream_info.file_size);
			percent+=((long long)6*1024*1024*100*info->video_bufferlevel/mStreamInfo.stream_info.file_size);
			/*we think the lowlevel buffer size is alsways 10M */
			LOGV("Playing buffer percent =%d\n",percent);
		}else
		{
			//percent+=info->audio_bufferlevel*4;
			//percent+=info->video_bufferlevel*6;
		}
		if(percent>100) percent=100;
		else if(percent<0) percent=0;
		if(mDuration>0 && !mLowLevelBufMode)
			sendEvent(MEDIA_BUFFERING_UPDATE,percent);		

	}
	 return 0;
}
status_t AmlogicPlayer::GetFileType(char **typestr,int *videos,int *audios)
{
	if(!mTypeReady)
		return ERROR_NOT_OPEN;
	
	LOGV("GetFileType---Type=%s,videos=%d,audios=%d\n",mTypeStr,mhasVideo,mhasAudio);
	*typestr=mTypeStr;
	
	*videos=mhasVideo;
	
	*audios=mhasAudio;
	if(mSouceProtocol.get()!=NULL){
		mSouceProtocol->getDrmInfo(mDecryptHandle, &mDrmManagerClient);
	    if (mDecryptHandle != NULL &&mDrmManagerClient !=NULL) {	      
		    LOGV("L%d:getmDecryptHandle", __LINE__);
	        if (RightsStatus::RIGHTS_VALID != mDecryptHandle->status) {
	            //notifyListener_l(MEDIA_ERROR, MEDIA_ERROR_UNKNOWN, ERROR_DRM_NO_LICENSE);
	            LOGE("L%d:getDrmInfo error", __LINE__);
	        }else {
				mDrmManagerClient->setPlaybackStatus(mDecryptHandle,
             	Playback::START, (int64_t)(mPlay_ctl.t_pos * 1000));
			}	
        }		    
	}

	return NO_ERROR;
}

status_t AmlogicPlayer::setdatasource(const char *path, int fd, int64_t offset, int64_t length, const KeyedVector<String8, String8> *headers)
{
	int num;
  	char * file=NULL;

	if(path==NULL)
	{
		if(fd<0 || offset<0)
			return -1;
		file=(char *)malloc(128);
		if(file==NULL)
			return NO_MEMORY;
		mAmlogicFile.oldfd=fd;
		mAmlogicFile.fd = dup(fd);
		mAmlogicFile.fd_valid=1;
		mAmlogicFile.mOffset=offset;
		mAmlogicFile.mLength=length;
		mPlay_ctl.t_pos=-1;/*don't seek to 0*/
		//mPlay_ctl.t_pos=0;/*don't seek to 0*/
		sprintf(file,"android:AmlogicPlayer=[%x:%x],AmlogicPlayer_fd=[%x:%x]",
					(unsigned int)this,(~(unsigned int)this),
					(unsigned int)&mAmlogicFile,(~(unsigned int)&mAmlogicFile));
	}
	else
	{
		int time;
		file=(char *)malloc(strlen(path)+10);
		if(file==NULL)
			return NO_MEMORY;
		if(strncmp(path,"http",strlen("http"))==0)
		{	/*http-->shttp*/
			size_t len = strlen(path);
            		if (len >= 5 && !strcasecmp(".m3u8", &path[len - 5])) {
            			num=sprintf(file,"list:s%s",path);
			}else
				num=sprintf(file,"s%s",path);
			file[num]='\0';
		}
		else
		{
			num=sprintf(file,"%s",path);
			file[num]='\0';
		}
		time=HistoryMgt(file,0,0);
		if(time>0)
			mPlay_ctl.t_pos=time;
		else	
			mPlay_ctl.t_pos=-1;
        	if (mPlay_ctl.headers) {
            		free(mPlay_ctl.headers);
            		mPlay_ctl.headers = NULL;
        	}
        if (headers) {
            //one huge string of the HTTP headers to add
            int len = 0;
            for (size_t i = 0; i < headers->size(); ++i) {
                len += strlen(headers->keyAt(i));
                len += strlen(": ");
                len += strlen(headers->valueAt(i));
                len += strlen("\r\n");
            }
            len += 1;
            mPlay_ctl.headers = (char *)malloc(len);
            if (mPlay_ctl.headers) {
                mPlay_ctl.headers[0] = 0;
                for (size_t i = 0; i < headers->size(); ++i) {
                    strcat(mPlay_ctl.headers, headers->keyAt(i));
                    strcat(mPlay_ctl.headers, ": ");
                    strcat(mPlay_ctl.headers, headers->valueAt(i));
                    strcat(mPlay_ctl.headers, "\r\n");
                }
                mPlay_ctl.headers[len - 1] = '\0';
            }
        }
		if(strncmp(path,"http",strlen("http"))==0 || 
		    strncmp(path,"shttp",strlen("shttp"))==0 || 	
		    strncmp(path,"https",strlen("https"))==0 || 	
		    strncmp(path,"rtsp",strlen("rtsp"))==0 || 	
		    strncmp(path,"mms",strlen("mms"))==0 || 	
		    strncmp(path,"ftp",strlen("ftp"))==0 ||
		    strncmp(path,"widevine",strlen("widevine"))==0 )/*if net work mode ,enable buffering*/
			mPlay_ctl.auto_buffing_enable=1;
			LOGV("setDataSource enable buffering\n");
	}
	mPlay_ctl.need_start=1;
	mAmlogicFile.datasource=file;
	mPlay_ctl.file_name=(char*)mAmlogicFile.datasource;
	LOGV("setDataSource url=%s, len=%d\n", mPlay_ctl.file_name, strlen(mPlay_ctl.file_name));
	mState = STATE_OPEN;
	return NO_ERROR;

}




status_t AmlogicPlayer::prepare()
{
		LOGV("prepare\n");
		if(prepareAsync()!=NO_ERROR)
			return UNKNOWN_ERROR;
		while(player_get_state(mPlayer_id)!=PLAYER_INITOK)
			{
			if((player_get_state(mPlayer_id))==PLAYER_ERROR ||
				player_get_state(mPlayer_id)==PLAYER_STOPED ||
				player_get_state(mPlayer_id)==PLAYER_PLAYEND|| 
				player_get_state(mPlayer_id)==PLAYER_EXIT
				)
				{
				return UNKNOWN_ERROR;
				}
				usleep(1000*10);
			}
    	return NO_ERROR;
}


status_t AmlogicPlayer::prepareAsync() {
	int check_is_playlist = -1;
	float level=PropGetFloat("media.amplayer.lpbufferlevel");
	float buftime=PropGetFloat("media.amplayer.buffertime");
	float delaybuffering=(int)PropGetFloat("media.amplayer.delaybuffering");
	LOGV("prepareAsync\n");
	mPlay_ctl.callback_fn.notify_fn=notifyhandle;
	mPlay_ctl.callback_fn.update_interval=1000;
	mPlay_ctl.audio_index=-1;
	mPlay_ctl.video_index=-1;
	mPlay_ctl.hassub = 1;  //enable subtitle
	mPlay_ctl.is_type_parser=1;
	mPlay_ctl.buffing_min=(level<0.001 && level >0.0)?level/10:0.001;
	mPlay_ctl.buffing_middle=level>0?level:0.02;
	mPlay_ctl.buffing_max=level<0.8?0.8:level;
	if(mLowLevelBufMode){
		if(mStopFeedingBufLevel>0){
			mPlay_ctl.buffing_min=mStopFeedingBufLevel/10;
			mPlay_ctl.buffing_middle=mStopFeedingBufLevel/2;
			mPlay_ctl.buffing_max=0.8;
			mPlay_ctl.callback_fn.update_interval=1000;
		}else{
			mPlay_ctl.buffing_min=0.001;
			mPlay_ctl.buffing_middle=0.01;
			mPlay_ctl.buffing_max=0.8;
			mPlay_ctl.callback_fn.update_interval=1000;
		}
	}
	mPlay_ctl.buffing_starttime_s=buftime;
	if(delaybuffering>0)
		mPlay_ctl.buffing_force_delay_s=delaybuffering;

	if(mLowLevelBufMode){
		mPlay_ctl.enable_rw_on_pause=0;/**/
		mPlay_ctl.lowbuffermode_flag=1;
	}else	
		mPlay_ctl.enable_rw_on_pause=1;
	mPlay_ctl.read_max_cnt=10000;/*retry num*/
	mPlay_ctl.nosound=PropIsEnable("media.amplayer.noaudio")?1:0;
	mPlay_ctl.novideo=PropIsEnable("media.amplayer.novideo")?1:0;
	mPlay_ctl.displast_frame = PropIsEnable("media.amplayer.displast_frame")?1:0;
	
	streaminfo_valied=false;
	LOGV("buffer level setting is:%f-%f-%f\n",
		mPlay_ctl.buffing_min,
		mPlay_ctl.buffing_middle,
		mPlay_ctl.buffing_max
		);
	LOGV("prepareAsync,file_name=%s\n",mPlay_ctl.file_name);
	mPlayer_id=player_start(&mPlay_ctl,(unsigned long)this);
	if(mPlayer_id>=0)
		{
			LOGV("Start player,pid=%d\n",mPlayer_id);
			if(fastNotifyMode){
					sendEvent(MEDIA_PREPARED);
			}		
			return NO_ERROR;
		}
	return UNKNOWN_ERROR;
}

status_t AmlogicPlayer::start()
{
    LOGV("start\n");
    if (mState != STATE_OPEN) {
        return ERROR_NOT_OPEN;
    }

	if (mRunning && !mPaused) {
		return NO_ERROR;
	}	
	player_start_play(mPlayer_id);

	if(mPaused)
		{
			if (mDecryptHandle != NULL) {      
		        mDrmManagerClient->setPlaybackStatus(mDecryptHandle,
		        	Playback::RESUME, 0);
		    }
		if(mNeedResetOnResume)
			player_timesearch(mPlayer_id,-1);
		player_resume(mPlayer_id);
		mNeedResetOnResume=false;
		}
	if(mhasVideo && !mRunning){
		initVideoSurface();
		VideoViewOn();
		if(isHDCPFailed == true){
			set_sys_int(DISABLE_VIDEO,1);
			LOGV("HDCP authenticate failed, Disable Video");
		}
	}
	if(mhasAudio){
		SetCpuScalingOnAudio(2);
		mChangedCpuFreq=true;
	}
   	mPaused = false;
    	mRunning = true;
	mEnded=false;
	mLastPlayTimeUpdateUS=ALooper::GetNowUs();
	mDelayUpdateTime=1;
	//sendEvent(MEDIA_PLAYER_STARTED);
    // wake up render thread
    return NO_ERROR;
}

status_t AmlogicPlayer::stop()
{
    LOGV("stop\n");
    if (mState != STATE_OPEN) {
        return ERROR_NOT_OPEN;
    }
	if (mDecryptHandle != NULL) {      
        	mDrmManagerClient->setPlaybackStatus(mDecryptHandle,
        		Playback::STOP, 0);
    }

    mPaused = true;
    mRunning = false;
	player_stop(mPlayer_id);
	///sendEvent(MEDIA_PLAYBACK_COMPLETE);
    return NO_ERROR;
}

//-----------------------------------------
static int adif_filetype_check(char *path)
{
    FILE *fp=fopen(path,"rb");
	char adif_head[4];
	int ret=0;
	if(!fp){
		LOGI("open %s failed!\n",path);
	}else{
		LOGI("open %s success!\n",path);
		fread(adif_head,1,4,fp);
		if(adif_head[0]=='A'&&adif_head[1]=='D'||adif_head[2]=='I'||adif_head[3]=='F'){
			LOGI("the file is adiftype\n");
			ret=1;
		}
		fclose(fp);
	}
	return ret; 
}
//-----------------------------------------
status_t AmlogicPlayer::seekTo(int position)
{
	if(position<0)
	{/*cancel seek*/
		return NO_ERROR;
	}
#if 0
	if(position<mPlayTime+1000 && position>=mPlayTime-1000)
	{
		sendEvent(MEDIA_SEEK_COMPLETE);
		return NO_ERROR;/**/
	}
	int time=position/1000;
	LOGV("seekTo:%d\n",position);
	player_timesearch(mPlayer_id,time);	
	if(!mRunning)/*have  not start,we tell it seek end*/
		sendEvent(MEDIA_SEEK_COMPLETE);
#endif
    if(mhasVideo == 0&&(strstr(mTypeStr,"aac")!=NULL))
    { 
        if(adif_filetype_check(mPlay_ctl.file_name))
        { 
		//sendEvent( MEDIA_NOP);
		    sendEvent(MEDIA_SEEK_COMPLETE);
		    return NO_ERROR;
        }
    }
	LOGI("seekTo:%d,player_get_state=%x,running=%d,Player time=%dms\n",position,player_get_state(mPlayer_id),mRunning,(int) (ALooper::GetNowUs()-PlayerStartTimeUS)/1000);
	if (!mRunning || player_get_state(mPlayer_id) >= PLAYER_ERROR || player_get_state(mPlayer_id)== PLAYER_NOT_VALID_PID) {
		if (player_get_state(mPlayer_id) >= PLAYER_ERROR || player_get_state(mPlayer_id)== PLAYER_NOT_VALID_PID) {
			int watingrunning=10;
			mIgnoreMsg = true;
			if (mDecryptHandle != NULL) {
	            		mDrmManagerClient->setPlaybackStatus(mDecryptHandle,
	                    		Playback::STOP, 0);     
			}
			player_exit(mPlayer_id);
			mPlayer_id = -1;
			mPlay_ctl.t_pos = (float)position/1000;
			mPlay_ctl.is_type_parser=0;
			prepare();
			mIgnoreMsg = false;
			LatestPlayerState=PLAYER_INITOK;
			mEnded=false;	
			if (mDecryptHandle != NULL) {
	            		mDrmManagerClient->setPlaybackStatus(mDecryptHandle,
	                    		Playback::START, position);
			}
			player_start_play(mPlayer_id);
			while(LatestPlayerState<=PLAYER_INITOK && watingrunning-->0)
				usleep(100000);/*wait player running*/	
			mDelayUpdateTime=2;
			LatestPlayerState=PLAYER_RUNNING;/*make sure we are running,*/
			LOGI("seek watingrunning:%d,player_get_state=%x,running=%d,Player time=%dms\n",watingrunning,player_get_state(mPlayer_id),mRunning,(int) (ALooper::GetNowUs()-PlayerStartTimeUS)/1000);			
		} 
		else {
			player_timesearch(mPlayer_id,(float)position/1000);
			if (mDecryptHandle != NULL) {
            mDrmManagerClient->setPlaybackStatus(mDecryptHandle,
                    Playback::PAUSE, 0);
            mDrmManagerClient->setPlaybackStatus(mDecryptHandle,
                    Playback::START, position);
			}
        	mDelayUpdateTime=2;
		}
		sendEvent(MEDIA_SEEK_COMPLETE);
	} else {
		if (position < (mPlayTime+100) && position >= (mPlayTime-100)) {
			sendEvent(MEDIA_SEEK_COMPLETE);
		}
		else{
			player_timesearch(mPlayer_id,(float)position/1000);
			mDelayUpdateTime=2;
			sendEvent(MEDIA_SEEK_COMPLETE);
		}
	}

    mPlayTime=position;
    mLastPlayTimeUpdateUS=ALooper::GetNowUs();
    return NO_ERROR;
}

status_t AmlogicPlayer::pause()
{
    LOGV("pause\n");
    if (mState != STATE_OPEN) {
        return ERROR_NOT_OPEN;
    }

	if(adif_filetype_check(mPlay_ctl.file_name)||mhasVideo){/*video mode,and no video,no audio*/		
		if (mDecryptHandle != NULL) {      
	        mDrmManagerClient->setPlaybackStatus(mDecryptHandle,
	                Playback::PAUSE, 0);
	    }	
		player_pause(mPlayer_id);
	}
	else
	{
		mIgnoreMsg=true;
		mPlay_ctl.t_pos=mPlayTime/1000;
		if (mDecryptHandle != NULL) {      
	        	mDrmManagerClient->setPlaybackStatus(mDecryptHandle,
	                	Playback::PAUSE, 0);
	    	}
		player_stop(mPlayer_id);
		player_exit(mPlayer_id);
		mPlayer_id=-1;
		mPlay_ctl.need_start=1;
		prepare();
		mIgnoreMsg=false;
	}
    mPaused = true;
	if(mhasAudio && mChangedCpuFreq){
		SetCpuScalingOnAudio(1);
		mChangedCpuFreq=false;
	}
	LatestPlayerState=PLAYER_PAUSE;
    return NO_ERROR;
}

bool AmlogicPlayer::isPlaying()
{
    	///LOGV("isPlaying?----%d\n",mRender);
	if(!mPaused)
		return mRunning;
	else
		return false;
}
const char* AmlogicPlayer::getStrAudioCodec(int type){
	const char* tmp = "unkown";
	switch(type){
		case AFORMAT_MPEG:
			tmp = "MPEG";
			break;
		case AFORMAT_PCM_S16LE:
			tmp = "PCMS16LE";
			break;
		case AFORMAT_AAC:
			tmp = "AAC";
			break;
		case AFORMAT_AC3:
			tmp = "AC3";
			break;
		case AFORMAT_ALAW:
			tmp = "ALAW";
			break;
		case AFORMAT_MULAW:
			tmp = "MULAW";
			break;
		case AFORMAT_DTS:
			tmp = "DTS";
			break;
		case AFORMAT_PCM_S16BE:
			tmp = "PCMS16BE";
			break;
		case AFORMAT_FLAC:
			tmp = "FLAC";
			break;
		case AFORMAT_COOK:
			tmp = "COOK";
			break;
		case AFORMAT_PCM_U8:
			tmp = "PCMU8";
			break;
		case AFORMAT_ADPCM:
			tmp = "ADPCM";
			break;
		case AFORMAT_AMR:
			tmp = "AMR";
			break;
		case AFORMAT_RAAC:
			tmp = "RAAC";
			break;
		case AFORMAT_WMA:
			tmp = "WMA";
			break;
		case AFORMAT_WMAPRO:
			tmp = "WMAPRO";
			break;
		case AFORMAT_PCM_BLURAY:
			tmp = "BLURAY";
			break;
		case AFORMAT_ALAC:
			tmp = "ALAC";
			break;
		case AFORMAT_VORBIS:
			tmp = "VORBIS";
	    		break;
	}
	return tmp;
}

const char* AmlogicPlayer::getStrVideoCodec(int vtype){
	const char* tmp = "unkown";
	switch(vtype){
		case VFORMAT_MPEG12:
			tmp = "MPEG12";
			break;
		case VFORMAT_MPEG4:
			tmp = "MPEG4";
			break;
		case VFORMAT_H264:
			tmp = "H264";
			break;
		case VFORMAT_MJPEG:
			tmp = "MJPEG";
			break;
		case VFORMAT_REAL:
			tmp = "REAL";
			break;
		case VFORMAT_JPEG:
			tmp = "JPEG";
			break;
		case VFORMAT_VC1:
			tmp = "VC1";
			break;
		case VFORMAT_AVS:
			tmp = "AVS";
			break;
		case VFORMAT_SW:
			tmp = "SW";
			break;
		case VFORMAT_H264MVC:
			tmp = "H264MVC";
			break;
    	
	}
	return tmp;
}

 
status_t AmlogicPlayer::updateMediaInfo(void)
{
	int ret;
	int i;
	if(mPlayer_id<0)
		return OK;
	mInnerSubNum = 0;
	mAudioTrackNum = 0;
	
	
	ret=player_get_media_info(mPlayer_id,&mStreamInfo);
	if(ret!=0)
	{
		LOGV("player_get_media_info failed\n");
		return NO_INIT;
	}
	streaminfo_valied=true;
	const int buflen = 1024;
	char tmp[buflen];
	int boffset = 0;
	
	mhasVideo=mStreamInfo.stream_info.total_video_num>0?mStreamInfo.stream_info.total_video_num:0;
	if(  mStreamInfo.stream_info.total_video_num>0 && 
		 mStreamInfo.stream_info.cur_video_index>=0)
	{
		memset(tmp,0,buflen);
		snprintf(tmp,buflen,"({");
		boffset = 2;

		for (i=0; i < mStreamInfo.stream_info.total_video_num; i ++) {
		  if(mStreamInfo.video_info[i]) {
			if(mStreamInfo.video_info[i]->index == mStreamInfo.stream_info.cur_video_index) 
			{
				mWidth=mStreamInfo.video_info[i]->width;
				mHeight=mStreamInfo.video_info[i]->height;	
				LOGI("################ old info:w:%d,h:%d\n",mWidth,mHeight);
				if (mWidth < 1280)
					mWidth = mWidth*4/3;
				video_rotation_degree=mStreamInfo.video_info[i]->video_rotation_degree;
				LOGI("player current video info:w:%d,h:%d\n",mWidth,mHeight);
				if(mStrCurrentVideoCodec){
					free(mStrCurrentVideoCodec);
					mStrCurrentVideoCodec = NULL;
				}
				mStrCurrentVideoCodec = strdup(getStrVideoCodec(mStreamInfo.video_info[i]->format));
				LOGI("player current video info:codec:%s\n",mStrCurrentVideoCodec);
				
			} 

			snprintf(tmp+boffset,buflen,"vid:%d,vcodec:%s,bitrate:%d",mStreamInfo.video_info[i]->id,
				getStrVideoCodec(mStreamInfo.video_info[i]->format),
				mStreamInfo.video_info[i]->bit_rate>0?mStreamInfo.video_info[i]->bit_rate:mStreamInfo.stream_info.bitrate);
			boffset = strlen(tmp);
			if( i < mStreamInfo.stream_info.total_video_num){	
				
				snprintf(tmp+boffset,buflen,";");
				boffset +=1;
			}			
			
		  }
		} 
		
		snprintf(tmp+boffset,buflen,"})");
		if(mVideoExtInfo){
			free(mVideoExtInfo);
			mVideoExtInfo = NULL;
		}
		mVideoTrackNum = mStreamInfo.stream_info.total_video_num;
		if(strlen(tmp)>0){
			mVideoExtInfo =strndup(tmp,buflen);
		}
			   
	} 
	
	boffset = 0;

	mhasAudio=mStreamInfo.stream_info.total_audio_num>0?mStreamInfo.stream_info.total_audio_num:0;
	if(  mStreamInfo.stream_info.total_audio_num>0)
	{
		memset(tmp,0,buflen);
		snprintf(tmp,buflen,"({");
		boffset = 2;		
		for (i=0; i < mStreamInfo.stream_info.total_audio_num; i ++) {
		  if(mStreamInfo.audio_info[i]) {
		  	if(mStreamInfo.stream_info.cur_audio_index>=0&&mStreamInfo.audio_info[i]->id == mStreamInfo.stream_info.cur_audio_index) {
				if(mStrCurrentAudioCodec){
					free(mStrCurrentAudioCodec);
					mStrCurrentAudioCodec = NULL;
				}
				mStrCurrentAudioCodec =strdup(getStrAudioCodec(mStreamInfo.audio_info[i]->aformat));
		  	}			
	  		
			snprintf(tmp+boffset,buflen,"aid:%d,acodec:%s,bitrate:%d,samplerate:%d",mStreamInfo.audio_info[i]->id,getStrAudioCodec(mStreamInfo.audio_info[i]->aformat),mStreamInfo.audio_info[i]->bit_rate,mStreamInfo.audio_info[i]->sample_rate);
			boffset = strlen(tmp);
			if( i < mStreamInfo.stream_info.total_audio_num){	
				
				snprintf(tmp+boffset,buflen,";");
				boffset +=1;
			}				
				
		  }
		}
		
		snprintf(tmp+boffset,buflen,"})");
		if(mAudioExtInfo){
			free(mAudioExtInfo);
			mAudioExtInfo = NULL;
		}
		if(strlen(tmp)>0){
			mAudioExtInfo =strndup(tmp,buflen);
		}
		
		mAudioTrackNum = mStreamInfo.stream_info.total_audio_num;
		
	} 
	
	boffset = 0;
	
	if(  mStreamInfo.stream_info.total_sub_num>0)
	{
		memset(tmp,0,buflen);
		snprintf(tmp,buflen,"({");
		boffset = 2;		
		for (i=0; i < mStreamInfo.stream_info.total_sub_num; i ++) {
		  if(mStreamInfo.sub_info[i]&& mStreamInfo.sub_info[i]->internal_external ==0) {		  	
		  	
				snprintf(tmp+boffset,buflen,"sid:%d,lang:%s",mStreamInfo.sub_info[i]->id,mStreamInfo.sub_info[i]->sub_language?mStreamInfo.sub_info[i]->sub_language:"unkown");
				boffset = strlen(tmp);
				if(i < mStreamInfo.stream_info.total_sub_num){	
					
					snprintf(tmp+boffset,buflen,";");
					boffset +=1;
				}	
				mInnerSubNum++;
		  }
		} 
		
		snprintf(tmp+boffset,buflen,"})");
		if(mSubExtInfo){
			free(mSubExtInfo);
			mSubExtInfo = NULL;
		}
		
		if(strlen(tmp)>0){
			mSubExtInfo = strndup(tmp,buflen);
		}
		LOGI("inner subtitle info:%s\n",mSubExtInfo);
	} 	
	if(mStreamInfo.stream_info.bitrate>0 && mLowLevelBufMode)
	{
		int bitrate=mStreamInfo.stream_info.bitrate;
		if(mStopFeedingBuf_ms>=1){
			//default the total buf size as 7M;
			int bufsize=(mHWvideobufsize+mHWaudiobufsize);
			if(bufsize<0) bufsize=7*1024*1024;
			if(bitrate<200*1024) bitrate=200*1024;//if bitrate is low ran 200bps,we think maybe have bug on get bitrate.so make sure 200bps.
			mStopFeedingBufLevel=(float)(mStopFeedingBuf_ms*(bitrate/8)/1000)/(bufsize);
			LOGI("recalculate the mStopFeedingBuf_ms=%dms,mStopFeedingBufLevel=%f,bufsize=%d\n",mStopFeedingBuf_ms,mStopFeedingBufLevel,bufsize);
			if(mStopFeedingBufLevel<=mPlay_ctl.buffing_middle)
			{
				mPlay_ctl.buffing_min=mStopFeedingBufLevel/10;
				mPlay_ctl.buffing_middle=mStopFeedingBufLevel/2;
				mPlay_ctl.buffing_max=0.8;
				player_set_autobuffer_level(mPlayer_id,mStopFeedingBufLevel/20,mStopFeedingBufLevel/3,0.8);
			}
		}
	}
	return OK;
}


status_t AmlogicPlayer::getMetadata(
	const media::Metadata::Filter& ids, Parcel *records) {
	using media::Metadata;
		LOGV("getMetadata\n");
	Metadata metadata(records);

	metadata.appendBool(
	        Metadata::kPauseAvailable,true);
	metadata.appendBool(
	        Metadata::kSeekBackwardAvailable,true);

	metadata.appendBool(
	        Metadata::kSeekForwardAvailable,true);
	updateMediaInfo();

	if(mhasVideo||mhasAudio){
		if(strlen(mTypeStr)>0){
			metadata.appendCString(Metadata::kStreamType,mTypeStr);	
		}
	}

	if(mhasVideo){
		metadata.appendInt32(Metadata::kVideoWidth,mWidth);
		metadata.appendInt32(Metadata::kVideoHeight,mHeight);		
		metadata.appendCString(Metadata::kVideoCodec,mStrCurrentVideoCodec!=NULL?mStrCurrentVideoCodec:"unkown");
		metadata.appendCString(Metadata::kVideoCodecAllInfo,mVideoExtInfo!=NULL?mVideoExtInfo:"unkown");
		metadata.appendInt32(Metadata::kVideoTrackNum,mVideoTrackNum);
		LOGV("set meta video info:%s\n",mVideoExtInfo);
	}else{
		metadata.appendInt32(Metadata::kVideoTrackNum,0);	
	}

	if(mhasAudio){	
		metadata.appendInt32(Metadata::kAudioTrackNum,mAudioTrackNum);	
		metadata.appendCString(Metadata::kAudioCodec,mStrCurrentAudioCodec!=NULL?mStrCurrentAudioCodec:"unkown");
		metadata.appendCString(Metadata::kAudioCodecAllInfo,mAudioExtInfo!=NULL?mAudioExtInfo:"unkown");	
		LOGV("set meta audio info:%s\n",mAudioExtInfo);
	}else{
		metadata.appendInt32(Metadata::kAudioTrackNum,0);	
	}

	if(mInnerSubNum>0){
		metadata.appendInt32(Metadata::kInnerSubtitleNum,mInnerSubNum);	
		metadata.appendCString(Metadata::kInnerSubtitleAllInfo,mSubExtInfo!=NULL?mSubExtInfo:"unkown");	
		LOGV("set meta sub info:%s\n",mSubExtInfo);
	}else{
		metadata.appendInt32(Metadata::kInnerSubtitleNum,0);	
	}

	metadata.appendInt32(Metadata::kPlayerType,AMLOGIC_PLAYER);


	LOGV("get meta data over");
		   
    	return OK;
}

status_t AmlogicPlayer::initVideoSurface(void)
{
	if(mNativeWindow.get()!=NULL){
		mPlayerRender=new AmlogicPlayerRender(mNativeWindow);
		mPlayerRender->Start();
		if(Rect(0,0,0,0)!=curLayout)/*default it 0000,have set layout*/
			mPlayerRender->onSizeChanged(curLayout,Rect(0,0));
	}
	return OK; 
}


status_t AmlogicPlayer::setVideoSurfaceTexture(const sp<ISurfaceTexture>& surfaceTexture)
{
	LOGV("Set setVideoSurfaceTexture\n");
	Mutex::Autolock autoLock(mMutex);
	int oldhavestarted=0;
	mPlayTime=mPlayTime+(int)(ALooper::GetNowUs()-mLastPlayTimeUpdateUS)/1000;/*save the time before*/
	if(mDelayUpdateTime>0)
		mDelayUpdateTime++;/*ignore ++,don't clear the value before*/
	else
		mDelayUpdateTime=1;
	mLastPlayTimeUpdateUS=ALooper::GetNowUs();
	if(mNativeWindow.get()!=NULL){
		if(mPlayerRender.get()!=NULL){
			mPlayerRender->Stop();
			mPlayerRender.clear();
			oldhavestarted=1;
		}
	}
	mSurface.clear();
	mSurfaceTexture = surfaceTexture;
	if(mSurfaceTexture.get()!=NULL){/*set new*/
		mNativeWindow=new SurfaceTextureClient(surfaceTexture);
		if(oldhavestarted || (mRunning && mhasVideo)){/*player has running*/
			initVideoSurface();
		}
	}
	return OK; 
}
int AmlogicPlayer::getintfromString8(String8 &s,const char*pre)
{
	int off;
	int val=0;
	if((off=s.find(pre,0))>=0){
		sscanf(s.string()+off+strlen(pre),"%d",&val);
	}
	return val;
}

status_t    AmlogicPlayer::setParameter(int key, const Parcel &request)
{	
	Mutex::Autolock autoLock(mMutex);
	LOGI("setParameter %d\n",key);
	switch(key)
	{
		case KEY_PARAMETER_AML_VIDEO_POSITION_INFO:
			{
			int left,right,top,bottom;
			Rect newRect,oldRect;
			int off;
			const String16 uri16 = request.readString16();
	   		String8 keyStr = String8(uri16);
		    	LOGI("setParameter %d=[%s]\n",key,keyStr.string());
			left=getintfromString8(keyStr,".left=");
			top=getintfromString8(keyStr,".top=");
			right=getintfromString8(keyStr,".right=");
			bottom=getintfromString8(keyStr,".bottom=");
			newRect=Rect(left,top,right,bottom);
			LOGI("setParameter info to newrect=[%d,%d,%d,%d]\n",
				left,top,right,bottom);

			left=getintfromString8(keyStr,".oldLeft=");
			top=getintfromString8(keyStr,".oldTop=");
			right=getintfromString8(keyStr,".oldRight=");
			bottom=getintfromString8(keyStr,".oldBotton=");
			oldRect=Rect(left,top,right,bottom);
			LOGI("setParameter info oldrect=[%d,%d,%d,%d]\n",
				left,top,right,bottom);
			if(mPlayerRender!=NULL && curLayout!=newRect)
				mPlayerRender->onSizeChanged(newRect,oldRect);
			curLayout=newRect;
			break;
			}
		case KEY_PARAMETER_TIMED_TEXT_TRACK_INDEX:	
			break;
		case KEY_PARAMETER_AML_PLAYER_SWITCH_AUDIO_TRACK:
			//audio TRACK?
			if(mPlayer_id>=0)
			{
				int aid =-1;
				const String16 uri16 = request.readString16();
				String8 keyStr = String8(uri16);
				LOGI("setParameter %d=[%s]\n",key,keyStr.string());
				aid=getintfromString8(keyStr,"aid:");
				if(aid>0){
					LOGI("switch audio track,id:%d\n",aid);
					player_aid(mPlayer_id,aid);
				}
			}				
			break;
		case KEY_PARAMETER_TIMED_TEXT_ADD_OUT_OF_BAND_SOURCE:
			//_ADD_OUT_OF_BAND_SOURCE?
			break;
		case KEY_PARAMETER_CACHE_STAT_COLLECT_FREQ_MS:
			//FREQ_MS?
			break;
		case KEY_PARAMETER_AML_PLAYER_SWITCH_SOUND_TRACK:
			//sound track
			if(mPlayer_id>=0)
			{
				const String16 uri16 = request.readString16();
				String8 keyStr = String8(uri16);
				LOGI("setParameter %d=[%s]\n",key,keyStr.string());
				if(!keyStr.compare(String8("lmono"))){
					audio_left_mono(mPlayer_id);
				}
				else if(!keyStr.compare(String8("rmono"))){
					audio_right_mono(mPlayer_id);				
				}
				else if(!keyStr.compare(String8("stereo"))){
					audio_stereo(mPlayer_id);
				}
			}
			break;
		case KEY_PARAMETER_AML_PLAYER_TRICKPLAY_FORWARD:
			if(mPlayer_id>=0){
				int speed = 0;
				const String16 uri16 = request.readString16();
				String8 keyStr = String8(uri16);				
				speed=getintfromString8(keyStr,"forward:");
				if(speed>=0)
					player_forward(mPlayer_id,speed);				
						
			}
			break;
		case KEY_PARAMETER_AML_PLAYER_TRICKPLAY_BACKWARD:
			if(mPlayer_id>=0){
				int speed = 0;
				const String16 uri16 = request.readString16();
				String8 keyStr = String8(uri16);
				LOGI("setParameter %d=[%s]\n",key,keyStr.string());
				
				speed=getintfromString8(keyStr,"backward:");
				if(speed>=0)
					player_backward(mPlayer_id,speed);				
				
						
			}
			break;			
		case KEY_PARAMETER_AML_PLAYER_SCREEN_MODE:
			if(mPlayer_id>=0){
				int mode = request.readInt32();
				LOGI("setParameter screen mode = %d", mode);
				amvideo_utils_set_screen_mode(mode);
			}
			break;
		case KEY_PARAMETER_AML_PLAYER_RESET_BUFFER:
			if(mPlayer_id>=0){
				LOGI("Do player buffer reset now.\n",0);
				mNeedResetOnResume=true;
			}
			break;	
		default:
			LOGI("unsupport setParameter value!=%d\n",key);
	}
	return OK; 	
}

status_t    AmlogicPlayer::getParameter(int key, Parcel *reply)
{
	Mutex::Autolock autoLock(mMutex);
	TRACE();
	if(key==KEY_PARAMETER_AML_PLAYER_VIDEO_OUT_TYPE){
		reply->writeInt32(VIDEO_OUT_HARDWARE);
		return 0;
	}
	return OK; 
}	

status_t AmlogicPlayer::getCurrentPosition(int* position)
{
	//LOGV("getCurrentPosition\n");
	LOGI(" getCurrentPosition Player time=%dms\n",(int) (ALooper::GetNowUs()-PlayerStartTimeUS)/1000);		
	Mutex::Autolock autoLock(mMutex);
	if(fastNotifyMode){
		if(mStreamTime<=0)/*mStreamTime is have not set,just set a big value for netflix may pause bug.*/
			mStreamTime+=mStreamTimeExtAddS*1000;
		*position=(int)(mStreamTime+(ALooper::GetNowUs()-mLastStreamTimeUpdateUS)/1000);/*jast let uplevel know,we are playing,they don't care it.(netflix's bug/)*/
		///*position+=mStreamTimeExtAddS*1000;
	}else{
		if(!mPaused && LatestPlayerState==PLAYER_RUNNING){ 
			int realposition;
			realposition=mPlayTime+(int)(ALooper::GetNowUs()-mLastPlayTimeUpdateUS)/1000;
			//LOGI(" getCurrentPosition mPlayTime=%d,mLastPlayTimeUpdateUS=%lld*1000,GetNowUs()=%lld*1000,realposition=%d\n",
			//		mPlayTime,mLastPlayTimeUpdateUS/1000,ALooper::GetNowUs()/1000,realposition);
			//*position=((realposition+500)/1000)*1000;/*del small  changes,<500ms*/
			*position=((realposition+100)/200)*200;/*del small  changes,<200ms*/
		}else{
			//*position=((mPlayTime+500)/1000)*1000;
			*position=((mPlayTime+100)/200)*200;
		}
	}
	  LOGV("CurrentPosition=%dmS,mStreamTime=%d\n",*position,mStreamTime);
    	return NO_ERROR;
}

status_t AmlogicPlayer::getDuration(int* duration)
{
	Mutex::Autolock autoLock(mMutex);
    LOGV("getDuration\n");
	if(mDuration<=0)
		*duration=-1;
	else
		*duration=mDuration;
    return NO_ERROR;
}

status_t AmlogicPlayer::release()
{
	int exittime;
    LOGV("release\n");
	if(mPlayer_id>=0){
		if (mDecryptHandle != NULL) {      
	        mDrmManagerClient->setPlaybackStatus(mDecryptHandle,
	        	Playback::STOP, 0);
		 	mDecryptHandle = NULL;
         	mDrmManagerClient = NULL;
	    }	

		player_stop(mPlayer_id);
		player_exit(mPlayer_id);
		if(mhasVideo)
			VideoViewClose();
		if(mPlayerRender.get()!=NULL){
			mPlayerRender->Stop();
			mPlayerRender.clear();
		}
	}
	TRACE();
	mPlayer_id=-1;
	if(mDuration>1000 && mPlayTime>1000 && mPlayTime<mDuration-5000)//if 5 seconds left,I think end plaing
		exittime=mPlayTime/1000;
	else
		exittime=0;
	if(mAmlogicFile.datasource)
		HistoryMgt(mAmlogicFile.datasource,1,exittime);
	
	if(mAmlogicFile.datasource!=NULL)
		free(mAmlogicFile.datasource);
	mAmlogicFile.datasource=NULL;
	if(mAmlogicFile.fd_valid)
		close(mAmlogicFile.fd);
	mAmlogicFile.fd_valid=0;
	if (mPlay_ctl.headers) {
		free(mPlay_ctl.headers);
		mPlay_ctl.headers = NULL;
	}
	if(mStreamSource.get()!=NULL){
		mStreamSource.clear();
	}
	if(mSouceProtocol.get()!=NULL){
		mSouceProtocol.clear();
	}
	if(mhasAudio && mChangedCpuFreq){
		SetCpuScalingOnAudio(1);
		mChangedCpuFreq=false;
	}
	///sendEvent(MEDIA_PLAYBACK_COMPLETE);
    return NO_ERROR;
}

status_t AmlogicPlayer::reset()
{
	//Mutex::Autolock autoLock(mMutex);
	 LOGV("reset\n");
	 player_exit(mPlayer_id);
	 mPlayTime=0;
	//pause();
	//mPaused = true;
    mRunning = false;
    return NO_ERROR;
}

// always call with lock held
status_t AmlogicPlayer::reset_nosync()
{
	Mutex::Autolock autoLock(mMutex);
	 LOGV("reset_nosync\n");
    // close file
    //player_stop_async(mPlayer_id);
    return NO_ERROR;
}

status_t AmlogicPlayer::setLooping(int loop)
{
	Mutex::Autolock autoLock(mMutex);
    	LOGV("setLooping\n");
	bool isLoop = (loop != 0);
	if(isLoop == mLoop){
		LOGV("drop same message,is loop:%s",isLoop?"YES":"NO");
		return NO_ERROR;	
	}else{
		mLoop = isLoop;
	}
	if(mLoop)
		player_loop(mPlayer_id);
	else
		player_noloop(mPlayer_id);
    return NO_ERROR;
}

status_t  AmlogicPlayer::setVolume(float leftVolume, float rightVolume){
	Mutex::Autolock autoLock(mMutex);
   	LOGV("setVolume\n");
	audio_set_lrvolume(mPlayer_id,leftVolume,rightVolume);
       return NO_ERROR;	
}






} // end namespace android

