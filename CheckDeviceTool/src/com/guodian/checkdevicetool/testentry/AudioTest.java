package com.guodian.checkdevicetool.testentry;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;

import com.guodian.checkdevicetool.R;

public class AudioTest extends TestTask{

  
    private MediaPlayer player;
    public AudioTest(Context context, Handler handler, int viewId, boolean isAuto) {
        super(context, handler, viewId, isAuto);
    }
    public void start() {
        super.start();
        playAudio();
    }
    public void playAudio(){
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
