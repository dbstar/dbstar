package com.guodian.checkdevicetool.testentry;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;

import com.guodian.checkdevicetool.R;

public class AudioTest extends TestTask{

	private Context mContext;
	private AudioManager mAudioManager;
    private MediaPlayer player;
	private int mMaxVolumeLevel;
    public AudioTest(Context context, Handler handler, int viewId, boolean isAuto) {
        super(context, handler, viewId, isAuto);
        mContext = context;
    }
    public void start() {
        super.start();
        playAudio();
    }
    public void playAudio(){
    	mAudioManager = (AudioManager) mContext.getSystemService(Service.AUDIO_SERVICE);
    	mMaxVolumeLevel = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    	Log.d("AudioTest", "------------mMaxVolumeLevel = " + mMaxVolumeLevel);
    	mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mMaxVolumeLevel / 2, 0);
       player = MediaPlayer.create(context,R.raw.audio);
       if(player != null ){
           player.start();
           isShowResult = false;
           isAutoToNext = false;
       }
      sendFailMsg(context.getResources().getString(R.string.test_audio_comfig));
    }
    public void stop(){
        if(player != null && player.isPlaying()){
            player.stop();
            player.release();
            player = null;
        }
    }
}
