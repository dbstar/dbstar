package com.guodian.checkdevicetool.testentry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.os.Handler;
import android.os.StatFs;

import com.dbstar.DbstarDVB.common.Configs;
import com.guodian.checkdevicetool.R;
import com.guodian.checkdevicetool.util.StringUtil;

public class DefaultDiskTest extends TestTask{
    
    private String mDiskPath;
    private String mTestFile1;
    private String mTestFile2;
    private String size ;
    public DefaultDiskTest(Context context, Handler handler, int viewId,
            boolean isAuto) {
        super(context, handler, viewId, isAuto);
        this.mDiskPath = Configs.DEFALUT_DISK;
        //this.mDiskPath ="/storage/external_storage/sda1";
        mTestFile1 = mDiskPath + "/test1_DefaultDiskTest";
        mTestFile2 = mDiskPath + "/test2_DefaultDiskTest";
    }
   
    public void start() {
     super.start();
     testDisk();
    }
    public void testDisk(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(mDiskPath);
                if(!file.exists()){
                    mLog.i("sda1 hava not mounted");
                    sendFailMsg(context.getResources().getString(R.string.test_notfound_disk));
                }else{
                    File file2 = new File(mTestFile1);
                    if(file2.exists())
                        file2.delete();
                    file2 = new File(mTestFile2);
                    if(file2.exists())
                        file2.delete();
                    getDiskInfo(mDiskPath, true);
                    writAndReadFile();
                }
            }
        }).start();
    }
    private void writAndReadFile(){
        InputStream is = null;
        FileOutputStream out  = null;
        try {
            is  = context.getAssets().open("abc");
            out = new FileOutputStream(mTestFile1);
            byte buffer [] = new byte [512];
            while((is.read(buffer)) != -1){
                out.write(buffer, 0, buffer.length);
            }
            is.close();
            out.flush();
            out.getFD().sync();
            out.close();
            is = new FileInputStream(mTestFile1);
            out = new FileOutputStream(mTestFile2);
            buffer  = new byte [512];
            while((is.read(buffer)) != -1){
                out.write(buffer, 0, buffer.length);
            }
            is.close();
            out.flush();
            out.getFD().sync();
            out.close();
            File test1 = new File(mTestFile1);
            File test2 = new File(mTestFile2);
            if(test1.exists() && test1.exists()){
                if(test1.length() == test2.length()){
                    if(size != null && size.length() > 0){
                        sendSuccessMsg(context.getString(R.string.test_disk_size) + size);
                    }else{
                        sendSuccessMsg();
                    }
                }else{
                    sendFailMsg(context.getResources().getString(R.string.test_disk_write_read_error));
                }
            }else{
                sendFailMsg(context.getResources().getString(R.string.test_disk_write_read_error));
            }
            if(test1.exists() && test2.exists()){
                test1.delete();
                test2.delete();
            }
            
        } catch (Exception e) {
            sendFailMsg(context.getResources().getString(R.string.test_disk_write_read_error));
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
    
    
   
     public void getDiskInfo(String diskPath, boolean convert) {
        
        File disk = new File(diskPath);
        if (disk == null || !disk.exists())
            return;
        
        StatFs sf = new StatFs(diskPath);
        long blockSize = sf.getBlockSize();
        long blockCount = sf.getBlockCount();
        //long availCount = sf.getAvailableBlocks();


        long diskSize = blockSize * blockCount;

//        long diskSpace = blockSize * availCount;
//        
//        long diskUsed = diskSize - diskSpace;

        if (convert) {
            StringUtil.SizePair diskSizePair = StringUtil.formatSize(diskSize);
            size = StringUtil.formatFloatValue(diskSizePair.Value)
                    + StringUtil.getUnitString(diskSizePair.Unit);

//            StringUtil.SizePair diskSpacePair = StringUtil
//                    .formatSize(diskSpace);
//            info.DiskSpace = StringUtil.formatFloatValue(diskSpacePair.Value)
//                    + StringUtil.getUnitString(diskSpacePair.Unit);
//            
//            StringUtil.SizePair diskUsedPair = StringUtil
//                    .formatSize(diskUsed);
//            
//            info.DiskUsed = StringUtil.formatFloatValue(diskUsedPair.Value)
//                    + StringUtil.getUnitString(diskUsedPair.Unit);
        }

    }
}
