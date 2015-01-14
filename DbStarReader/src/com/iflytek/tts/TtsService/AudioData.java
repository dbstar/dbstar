package com.iflytek.tts.TtsService;


import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class AudioData {
	private static AudioTrack mAudio = null;
	private static final String TAG = "TtsService(audio)";
	private static int mStreamType = AudioManager.STREAM_MUSIC;
	private static int mSampleRate = 16000;
	private static int mBuffSize = 8000; 
	
		
	static {
		mAudio = new AudioTrack(mStreamType
				,mSampleRate,AudioFormat.CHANNEL_CONFIGURATION_MONO 
				,AudioFormat.ENCODING_PCM_16BIT
				,mBuffSize,AudioTrack.MODE_STREAM );		
		Log.d(TAG," AudioTrack create ok");
	}
	
	/**
	 * For C call 
	 */
	public static  void onJniOutData(int len,byte [] data){	
		
			if (null == mAudio){
				Log.e(TAG," mAudio null");
				return;
			}
			if (mAudio.getState() != AudioTrack.STATE_INITIALIZED ){
				Log.e(TAG," mAudio STATE_INITIALIZED");
				return;
			}
			
			try{
				mAudio.write(data, 0, len);	
				mAudio.play();
			}catch (Exception e){
				Log.e(TAG,e.toString());
			}
	}
	
	/**
	 * For C Watch Call back
	 * @param nProcBegin
	 */
	public static void onJniWatchCB(int nProcBegin){
		Log.d(TAG,"onJniWatchCB  process begin = " + nProcBegin);

	}
	
	
}


