package com.guodian.checkdevicetool.testentry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.os.Handler;

import com.guodian.checkdevicetool.R;
import com.guodian.checkdevicetool.util.APPVersion;

public class SdcardTest extends TestTask{
    
    private String sdcardPath ;
    private String testFile1 = "/test1_SdcardTest";
    private String testFile2 = "/test2_SdcardTest";
    public SdcardTest(Context context, Handler handler, int viewId,
            boolean isAuto) {
        super(context, handler, viewId, isAuto);
        if(APPVersion.SINGLE){
            sdcardPath = "/mnt/sdcard/external_sdcard";
        }else{
            sdcardPath = "/storage/external_storage/sdcard1";
        }
    }

    public void start() {
         super.start();
         File sdcard = new File(sdcardPath);
         if(!sdcard.exists())
             sendFailMsg(context.getResources().getString(R.string.test_notfound_sdcard));
         else{
             testDisk();
         }
    }
    public void testDisk(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                writAndReadFile();
                File file2 = new File(sdcardPath + testFile1);
                if(file2.exists())
                    file2.delete();
                file2 = new File(sdcardPath + testFile2);
                if(file2.exists())
                    file2.delete();
            }
        }).start();
    }
    private void writAndReadFile(){
        InputStream is = null;
        FileOutputStream out  = null;
        try {
            is = context.getAssets().open("abc");
            out = new FileOutputStream(sdcardPath +  testFile1);
            byte buffer [] = new byte [512];
            while((is.read(buffer)) != -1){
                out.write(buffer, 0, buffer.length);
            }
            is.close();
            out.flush();
            out.getFD().sync();
            out.close();
            
            
            is = new FileInputStream(sdcardPath + testFile1);
            out = new FileOutputStream(sdcardPath + testFile2);
            buffer  = new byte [512];
            while((is.read(buffer)) != -1){
                out.write(buffer, 0, buffer.length);
            }
            is.close();
            out.flush();
            out.getFD().sync();
            out.close();
            
            
            boolean isSuccess;
            File test1 = new File(sdcardPath+ testFile1);
            File test2 = new File(sdcardPath + testFile2);
            if(test1.exists() && test2.exists()){
                if(test1.length()== test2.length()){
                    isSuccess = true;
                }else{
                    isSuccess = false;
                }
                
            }else{
                isSuccess = false;
            }
            if(test1.exists() && test2.exists()){
                test1.delete();
                test2.delete();
            }
            if(isSuccess){
               sendSuccessMsg();
            }else{
                sendFailMsg(context.getResources().getString(R.string.test_disk_write_read_error));
            }
        } catch (Exception e) {
            sendFailMsg(context.getResources().getString(R.string.test_disk_write_read_error));
            e.printStackTrace();
        }finally{
            try {
                if(is != null)
                    is.close();
                if(out != null)
                    out.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            
        }  
    }
    
  
}
