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
        mLog = GLog.getLogger("Futao");
    }
   
    public void start() {
     super.start();
     mConfig = mActivity.getConfig();
     if(mConfig == null || mConfig.mVideoPath == null || mConfig.mVideoPath.isEmpty()){
         sendFailMsg(mActivity.getString(R.string.test_read_configfile_fail));
     }else{
         startCopyFile(mConfig.mVideoPath, Configs.TARGET_VIDEO_FILE);
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
                
                File targetFile = new File(Configs.TARGET_VIDEO_FILE);
                File sdcartFile = new File(mConfig.mVideoPath);
                Log.d("CopyVideoFileTest", "-------------sdcartFile.length() = " + sdcartFile.length());
                Log.d("CopyVideoFileTest", "-------------targetFile.length() = " + targetFile.length());
                Log.d("CopyVideoFileTest", "-------------mCopyState = " + mCopyState);
                Log.d("CopyVideoFileTest", "-------------END_COPY = " + END_COPY);
                Log.d("CopyVideoFileTest", "-------------mConfig.mVideoPath = " + mConfig.mVideoPath);
                Log.d("CopyVideoFileTest", "-------------targetFile.exists() = " + targetFile.exists());
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
}
