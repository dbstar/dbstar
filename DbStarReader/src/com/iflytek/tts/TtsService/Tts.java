package com.iflytek.tts.TtsService;

import com.media.player.common.Utils;

import android.content.Context;
import android.os.Environment;


public final class Tts{
	private static boolean isInitialized= false;
	static {
		System.loadLibrary("Aisound");	
	}
	
	public static boolean isInitialized(){
		return isInitialized;
	}
	
	public static void initTtsEngine(){
		if(isInitialized){
			return;
		}else{
			isInitialized=!isInitialized;
		}
		if(!Utils.IS_TEST){
			Tts.JniCreate("/system/lib"+"/Resource.irf");
		}else{
			Tts.JniCreate(Environment.getExternalStorageDirectory()+"/Resource.irf");
		}
		/**
		 * set language 
		 * #define ivTTS_LANGUAGE_AUTO             0           /* Detect language automatically default
		 * #define ivTTS_LANGUAGE_CHINESE			1			/* Chinese (with English) 
         * #define ivTTS_LANGUAGE_ENGLISH			2			/* English 
		 * */
        Tts.JniSetParam(256, 0); 
        /**
         * set role
         *  #define ivTTS_ROLE_TIANCHANG			1			/* Tianchang (female, Chinese)
			#define ivTTS_ROLE_WENJING				2			/* Wenjing (female, Chinese) 
			#define ivTTS_ROLE_XIAOYAN				3			/* Xiaoyan (female, Chinese) 
			#define ivTTS_ROLE_YANPING				3			/* Xiaoyan (female, Chinese) 
			#define ivTTS_ROLE_XIAOFENG				4			/* Xiaofeng (male, Chinese) 
			#define ivTTS_ROLE_YUFENG				4			/* Xiaofeng (male, Chinese) 
			#define ivTTS_ROLE_SHERRI				5			/* Sherri (female, US English) 
			#define ivTTS_ROLE_XIAOJIN				6			/* Xiaojin (female, Chinese) 
			#define ivTTS_ROLE_NANNAN				7			/* Nannan (child, Chinese) 
			#define ivTTS_ROLE_JINGER				8			/* Jinger (female, Chinese) 
			#define ivTTS_ROLE_JIAJIA				9			/* Jiajia (girl, Chinese) 
			#define ivTTS_ROLE_YUER					10			/* Yuer (female, Chinese) 
			#define ivTTS_ROLE_XIAOQIAN				11			/* Xiaoqian (female, Chinese Northeast) 
			#define ivTTS_ROLE_LAOMA				12			/* Laoma (male, Chinese) 
			#define ivTTS_ROLE_BUSH					13			/* Bush (male, US English) 
			#define ivTTS_ROLE_XIAORONG				14			/* Xiaorong (female, Chinese Szechwan) 
			#define ivTTS_ROLE_XIAOMEI				15			/* Xiaomei (female, Cantonese) 
			#define ivTTS_ROLE_ANNI					16			/* Anni (female, Chinese) 
			#define ivTTS_ROLE_JOHN					17			/* John (male, US English) 
			#define ivTTS_ROLE_ANITA				18			/* Anita (female, British English) 
			#define ivTTS_ROLE_TERRY				19			/* Terry (female, US English) 
			#define ivTTS_ROLE_CATHERINE			20			/* Catherine (female, US English) 
			#define ivTTS_ROLE_TERRYW				21			/* Terry (female, US English Word) 
			#define ivTTS_ROLE_XIAOLIN				22			/* Xiaolin (female, Chinese) 
			#define ivTTS_ROLE_XIAOMENG				23			/* Xiaomeng (female, Chinese) 
			#define ivTTS_ROLE_XIAOQIANG			24			/* Xiaoqiang (male, Chinese) 
			#define ivTTS_ROLE_XIAOKUN				25			/* XiaoKun (male, Chinese) 
			#define ivTTS_ROLE_JIUXU				51			/* Jiu Xu (male, Chinese) 
			#define ivTTS_ROLE_DUOXU				52			/* Duo Xu (male, Chinese) 
			#define ivTTS_ROLE_XIAOPING				53			/* Xiaoping (female, Chinese) 
			#define ivTTS_ROLE_DONALDDUCK			54			/* Donald Duck (male, Chinese) 
			#define ivTTS_ROLE_BABYXU				55			/* Baby Xu (child, Chinese) 
			#define ivTTS_ROLE_DALONG				56			/* Dalong (male, Cantonese) 
			#define ivTTS_ROLE_TOM					57			/* Tom (male, US English) 
			#define ivTTS_ROLE_USER					99			/* user defined 
         */
        Tts.JniSetParam(1280, 3);
        /**
         * set speak style
         *  #define ivTTS_STYLE_PLAIN				0			/* plain speak style 
			#define ivTTS_STYLE_NORMAL				1			/* normal speak style (default) 
         * */
        Tts.JniSetParam(1281, 1);
        /**
         * set voice effect - predefined mode 
         *  #define ivTTS_VEMODE_NONE				0			/* none(default)
			#define ivTTS_VEMODE_WANDER				1			/* wander 
			#define ivTTS_VEMODE_ECHO				2			/* echo 
			#define ivTTS_VEMODE_ROBERT				3			/* robert 
			#define ivTTS_VEMODE_CHROUS				4			/* chorus 
			#define ivTTS_VEMODE_UNDERWATER			5			/* underwater 
			#define ivTTS_VEMODE_REVERB				6			/* reverb 
			#define ivTTS_VEMODE_ECCENTRIC			7			/* eccentric 
         * */
        Tts.JniSetParam(1536, 0);
	}
	
	public  synchronized static void startReadThread(Context ctx){
		class TtsRunThread implements Runnable{			
			@Override
			public void run() {   
				JniSpeak("nothing");	
			}			
		}		
		Thread ttsRun = (new Thread(new TtsRunThread()));
		ttsRun.setPriority(Thread.MAX_PRIORITY);
		ttsRun.start();
	}
	
	public static native int JniGetVersion();
	public static native int JniCreate(String resFilename);	
	public static native int JniDestory();	
	public static native int JniStop(); 
	public static native int JniSpeak(String text); 	
	public static native int JniSetParam(int paramId,int value);
	public static native int JniGetParam(int paramId);
	public static native int JniIsPlaying();
	public static native boolean JniIsCreated();	
}
