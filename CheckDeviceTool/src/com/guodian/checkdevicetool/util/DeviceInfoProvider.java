package com.guodian.checkdevicetool.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Config;
import android.util.Log;

import com.dbstar.DbstarDVB.common.Configs;
import com.guodian.checkdevicetool.testentry.Disk;

public class DeviceInfoProvider {
    
    public static GLog mLog = GLog.getLogger("FactoryTest");
    public static final long UNITSIZE_100G = 107374182400l;
    
    public static  ArrayList<Disk> loadDiskInfo(){
        Map<String, String> map = new HashMap<String, String>();
        ArrayList<Disk> disks = new ArrayList<Disk>();
        
        File mnt = new File("/storage/external_storage");
        mLog.i("mnt.exists() = " + mnt.exists());
        if(APPVersion.SINGLE){
            mnt = new File("/mnt");
        }
        if (mnt == null || !mnt.exists()) {
            mLog.i("No / folder!");
            return null;
        }

        File[] mnts = mnt.listFiles();
//        mLog.i("mnts = " + mnts);
        if (mnts == null)
            return null;
        if(APPVersion.SINGLE){
            String fileStr = "/mnt/sd";
            for (File file : mnts) {
                if (!("/mnt/sata".equals(file.toString())
                        || "/mnt/obb".equals(file.toString())
                        || "/mnt/asec".equals(file.toString()) || "/mnt/secure"
                            .equals(file.toString())||"/mnt/sdcard".equals(file.toString())))
                {   
                    String filePath = file.toString();
                    if(filePath.startsWith(fileStr)){
                        String key = filePath.substring(0, fileStr.length() + 1);
                        map.put(key, filePath);
                    }
                }
            }
        }else{
            String fileStr = "/storage/external_storage/sd";
            for (File file : mnts) {
                if (!"/storage/external_storage/sdcard1".equals(file.toString()))
                {   
                    String filePath = file.toString();
                    if(filePath.startsWith(fileStr)){
                    	Log.d("DeviceInfoProvider", "----------------filePath = " + filePath);
                        String key = filePath.substring(0, fileStr.length() + 1);
                        map.put(key, filePath);
                    }
                }
            }
        }
        File file;
        Disk disk = null;
        for(String fp : map.values()){
        	Log.d("DeviceInfoProvider", "----------------fp = " + fp);
            file = new File(fp);
            if(file.exists() && file.getTotalSpace() < UNITSIZE_100G){
            	Log.d("DeviceInfoProvider", "--------<--------fp = " + fp);
                disk = new Disk();
                disk.filePath = fp;
                disks.add(disk);
            }else if(file.exists() && file.getTotalSpace() > UNITSIZE_100G){
            	Log.d("DeviceInfoProvider", "-------->--------fp = " + fp);
                Configs.DEFALUT_DISK = fp;
            }
        }
        
//        Disk disk = null;
//        for(String fp : map.values()){
//            disk = new Disk();
//            disk.filePath = fp;
//            disks.add(disk);
//        }
        if(disks != null){
            for(Disk string :disks){
                mLog.i("mounted usb path = " + string.filePath);
            }
         }else{
             mLog.i("usb umount"); 
         }
        return disks;
    }
    
    /**
     * query prodect serial number from database
     * @param property 
     * @return
     */
    public static String querypProductSN(String property) {
        String value = null;
       String selection = "Name = ?";
       String u =  "content://com.dbstar.provider";
       Uri uri = Uri.withAppendedPath(
               Uri.parse(u), "Global");
        String[] selectionArgs = { property };
        Cursor cursor = query(
                uri,
                new String[] { "Value", "Param" }, selection,
                selectionArgs, null);

        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                value = cursor.getString(0);
            }
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return value;
    }

    public static Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor curosr = null;
        try {

            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase("/data/dbstar/Dbstar.db", null);
            if (db == null || !db.isOpen()) {
                return null;
            }
            String table = "Global";

            if (table != null && !table.isEmpty()) {
                curosr = db.query(table, projection, selection, selectionArgs, null, null, sortOrder);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return curosr;
    }
    
}
