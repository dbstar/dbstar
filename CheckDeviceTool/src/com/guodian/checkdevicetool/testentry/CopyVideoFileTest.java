package com.guodian.checkdevicetool.testentry;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.dbstar.DbstarDVB.common.Configs;
import com.guodian.checkdevicetool.BoardOrAllTestActivity;
import com.guodian.checkdevicetool.R;
import com.guodian.checkdevicetool.util.GLog;

public class CopyVideoFileTest extends TestTask{
    
    private static final int START_COPY = 1;
    private static final int RUN_COPY = 2;
    private static final int END_COPY = 3;
    private static final int FAIL_COPY = 4;
    private GLog mLog;
    private Configs mConfig;
    private int mCopyState;
    private BoardOrAllTestActivity mActivity;
    
    public CopyVideoFileTest(Context context, Handler handler, int viewId,
            boolean isAuto) {
        super(context, handler, viewId, isAuto);
        mActivity = (BoardOrAllTestActivity) context;
        mLog = GLog.getLogger("FactoryTest");
    }
   
    public void start() {
     super.start();
     mConfig = mActivity.getConfig();
     if(mConfig == null || mConfig.mVideoPath == null || mConfig.mVideoPath.isEmpty()){
         sendFailMsg(mActivity.getString(R.string.test_read_configfile_fail));
     }else{
         startCopyFile(getVideoPath(), Configs.TARGET_VIDEO_FILE);
     }
    }
    
    private void startCopyFile(final String sourceFile,final String targetFile){
//        File file = new File("/mnt/sda1");
//        if(!file.exists()){
//            mLog.i("sda1 umounted");
//            sendFailMsg(mActivity.getString(R.string.test_read_configfile_fail));
//            return;
//        }
        mCopyState = START_COPY;
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                String[] cmdtest = {
                        "/system/bin/sh",
                        "-c",
                        "cp " + sourceFile + " " + targetFile
                        };
                do_exec(cmdtest);
                mLog.i("startCopyFile finish");
                File sdcartFile = new File(sourceFile);
                File targetFile = new File(Configs.TARGET_VIDEO_FILE);
                
//                Log.d("CopyVideoFileTest", "-------------sdcartFile.length() = " + sdcartFile.length());
//                Log.d("CopyVideoFileTest", "-------------targetFile.length() = " + targetFile.length());
//                Log.d("CopyVideoFileTest", "-------------mCopyState = " + mCopyState);
//                Log.d("CopyVideoFileTest", "-------------END_COPY = " + END_COPY);
//                Log.d("CopyVideoFileTest", "-------------mConfig.mVideoPath = " + mConfig.mVideoPath);
//                Log.d("CopyVideoFileTest", "-------------getVideoPath() = " + getVideoPath());
//                Log.d("CopyVideoFileTest", "-------------targetFile.exists() = " + targetFile.exists());
                if(mCopyState == END_COPY && targetFile.exists() && sdcartFile.length() == targetFile.length()){
                    sendSuccessMsg();
                }else{
                   sendFailMsg(null);
                }
            }
        }).start();
    }
    private String do_exec(String[] cmd) {
        
        String s = "\n";
        try {
            mCopyState = RUN_COPY;
            java.lang.Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                s += line + "\n";
            }
            
            Runtime.getRuntime().exec("sync");
            Runtime.getRuntime().exec("sync");
            mCopyState = END_COPY;
        } catch (IOException e) {
            mCopyState = FAIL_COPY;
            e.printStackTrace();
        }
        return cmd.toString();
    }

	private String getVideoPath() {
		String mVideoPath = mConfig.mVideoPath;
		String videoPath = null;
		if (mConfig.mVideoPath.contains("/")) {
			String[] split = mConfig.mVideoPath.split("/");
			if (split != null && split.length > 0) {
				videoPath = split[split.length - 1];
			}
		} else {
			videoPath = mConfig.mVideoPath;
		} 
		
		mVideoPath = Configs.TEST_CONFIG_PAHT_SDB1 + videoPath;			
		String[] paths = { Configs.TEST_CONFIG_PAHT_SDA1, Configs.TEST_CONFIG_PAHT_SDB1, Configs.TEST_CONFIG_PAHT_SDC1 };
		for (int i = 0; i < paths.length; i++) {
			String path = paths[i] + videoPath;
			File file = new File(path);
			if (file.exists()) {
				mVideoPath = path;
			}
		}
		return mVideoPath;
	}
}
