package com.guodian.checkdevicetool.testentry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;

import com.dbstar.DbstarDVB.common.Configs;
import com.guodian.checkdevicetool.BoardOrAllTestActivity;
import com.guodian.checkdevicetool.R;

public class USB2Test extends TestTask{
    
    private ArrayList<Disk> usbPaths;
    private String testFile1 = "/test1_USB2Test";
    private String testFile2 = "/test2_USB2Test";
    private String usb2Path;
    private String usb_name = "";
    public USB2Test(Context context, Handler handler, int viewId,
            boolean isAuto) {
        super(context, handler, viewId, isAuto);
    }

    public void start() {
     super.start();
     usbPaths = ((BoardOrAllTestActivity)context).getDisks();
     if(usbPaths.isEmpty()){
        mLog.i("usb2---usbPaths.isEmpty()");
         sendFailMsg(context.getResources().getString(R.string.test_disk_info_error));
     }
     else{
         if(usbPaths.size() >= 2){
            mLog.i("usb2---usbPaths.size() > 2");
             sendFailMsg(context.getResources().getString(R.string.test_disk_info_error));
         }else if(usbPaths.isEmpty()){
            mLog.i("usb2--usbPaths.isEmpty()");
             sendFailMsg(context.getResources().getString(R.string.test_disk_info_error));
         }else{
             usb2Path = usbPaths.get(0).filePath;
            mLog.i("usb2Path = " + usb2Path);
             testDisk();
         }
     }
    }
    public void testDisk(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(usbPaths.isEmpty()){
                   mLog.i("usb2---usbPaths.isEmpty()");
                    sendFailMsg(context.getResources().getString(R.string.test_disk_info_error));
                    return;
                }
                    File file = new File(usb2Path);
                    if(!file.exists()){
                       mLog.i("usb2---!file.exists())");
                        sendFailMsg(context.getResources().getString(R.string.test_disk_info_error));
                    }else{
                        File file2 = new File(usb2Path + testFile1);
                        if(file2.exists())
                            file2.delete();
                        file2 = new File(usb2Path + testFile2);
                        if(file2.exists())
                            file2.delete();
                        
                        File [] files = file.listFiles();
                        if(files != null){
                            String fileName;
                            for(File childFile : files){
                                fileName = childFile.getAbsolutePath().toLowerCase();
                                if(fileName.contains(Configs.USB_1)){
                                    usb_name = Configs.USB_1;
                                }else if(fileName.contains(Configs.USB_2)){
                                    usb_name = Configs.USB_2;
                                }else if(fileName.contains(Configs.USB_3)){
                                    usb_name = Configs.USB_3;
                                }
                            }
                        }
                        writAndReadFile();
                    }
            }
        }).start();
    }
    private void writAndReadFile(){
        if(usbPaths.isEmpty()){
           mLog.i("usb2---usbPaths.isEmpty()");
            sendFailMsg(context.getResources().getString(R.string.test_disk_info_error));
            return;
        }
        InputStream is = null;
        FileOutputStream out  = null;
        try {
            is = context.getAssets().open("abc");
            out = new FileOutputStream(usb2Path +testFile1);
            byte buffer [] = new byte [512];
            while((is.read(buffer)) != -1){
                out.write(buffer, 0, buffer.length);
            }
            is.close();
            out.flush();
            out.getFD().sync();
            out.close();
            
            
            is = new FileInputStream(usb2Path +testFile1);
            out = new FileOutputStream(usb2Path+ testFile2);
            buffer  = new byte [512];
            while((is.read(buffer)) != -1){
                out.write(buffer, 0, buffer.length);
            }
            is.close();
            out.flush();
            out.getFD().sync();
            out.close();
            
            
            File test1 = new File(usb2Path + testFile1);
            File test2 = new File(usb2Path + testFile2);
            boolean isSuccess;
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
            usbPaths.remove(0);
            if(isSuccess){
                sendSuccessMsg("(" + usb_name + ")");
             }else{
                mLog.i("usb2 !isSuccess");
                sendFailMsg(context.getResources().getString(R.string.test_disk_write_read_error)+ "(" + usb_name + ")");
             }
        } catch (Exception e) {
           mLog.i("usb2  Exception");
            sendFailMsg(context.getResources().getString(R.string.test_disk_info_error)+ "(" + usb_name + ")");
            e.printStackTrace();
        } finally{
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
