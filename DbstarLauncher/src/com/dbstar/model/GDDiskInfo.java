package com.dbstar.model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.StatFs;
import android.util.Log;

import com.dbstar.util.StringUtil;

public class GDDiskInfo {

	private static final String TAG = "GDDiskInfo";

	static public class DiskInfo {
		public long RawDiskSize;
		public long RawDiskSpace;
		public long RawDiskUsed;

		public String DiskSize;
		public String DiskUsed;
		public String DiskSpace;
	}

	private static long getDirSize(File file) {     
        // check file is exist
        if (file.exists()) {     
            // if directory
            if (file.isDirectory()) {     
                File[] childrenFile = file.listFiles();     
                long size = 0l;     
                for (File f : childrenFile)     
                    size += getDirSize(f);     
                return size;     
            } else {   
                long size = file.length();        
                return size;     
            }     
        } else {     
            Log.d("GDDiskInfo", "file or directory is not exist");
            return 0l;     
        }     
    }     
	
	static public DiskInfo getDiskInfo(String diskPath, boolean convert) {
		Log.d(TAG, diskPath);
		DiskInfo info = null;
		
		if (diskPath.equals("/data/dbstar/")) {
			diskPath += "pushroot";
			Log.d("GDDiskInfo", "disk = " + diskPath);
		}
		
		boolean isDisk = false;
		
		File disk = new File(diskPath);
		if (disk == null || !disk.exists()){
			if (diskPath.equals("/data/dbstar/pushroot")) {
				info = new DiskInfo();
				info.DiskSize = "1G";
				info.DiskUsed = "0k";
				info.DiskSpace = "1G";
				Log.d("GDDiskInfo", diskPath + " is not fount and info.DiskSize = " + info.DiskSize 
						+ " info.DiskUsed = " + info.DiskUsed + " info.DiskSpace = " + info.DiskSpace);
			}
			return info;
		}
		
		long diskSize = 0, diskSpace = 0, diskUsed = 0;
		
		if (diskPath.equals("/data/dbstar/pushroot")) {
			// step by 1000 to calculate G,M,K, not 1024
			diskSize = 1000000000;
			diskUsed = getDirSize(disk);
			diskSpace = diskSize - diskUsed;
			Log.d("GDDiskInfo", "-/data/dbstar/pushroot---diskSize = " + diskSize + " diskUsed = " + diskUsed + " diskSpace = " + diskSpace);
			if (diskSpace < 0 || diskSpace > diskSize) {
				diskSpace = 0;
			}
			isDisk = false;
		} else {			
			StatFs sf = new StatFs(diskPath);
			long blockSize = sf.getBlockSize();
			long blockCount = sf.getBlockCount();
			long availCount = sf.getAvailableBlocks();

			diskSize = blockSize * blockCount;
			diskSpace = blockSize * availCount;
			diskUsed = diskSize - diskSpace;
			isDisk = true;
			Log.d("GDDiskInfo", "diskSize = " + diskSize + " diskUsed = " + diskUsed + " diskSpace = " + diskSpace);
		}
		
		info = new DiskInfo();
		info.RawDiskSize = diskSize;
		info.RawDiskSpace = diskSpace;
		info.RawDiskUsed = diskUsed;
		
		if (convert) {
			Log.d("GDDiskInfo", "isDisk = " + isDisk);
			
			if (isDisk) {
				StringUtil.SizePair diskSizePair = StringUtil.formatSize(diskSize);
				info.DiskSize = StringUtil.formatFloatValue(diskSizePair.Value)
						+ StringUtil.getUnitString(diskSizePair.Unit);
				
				StringUtil.SizePair diskSpacePair = StringUtil.formatSize(diskSpace);
				info.DiskSpace = StringUtil.formatFloatValue(diskSpacePair.Value)
						+ StringUtil.getUnitString(diskSpacePair.Unit);
				
				StringUtil.SizePair diskUsedPair = StringUtil.formatSize(diskUsed);
				info.DiskUsed = StringUtil.formatFloatValue(diskUsedPair.Value)
						+ StringUtil.getUnitString(diskUsedPair.Unit);
			} else {
				StringUtil.SizePair fileSizePair = StringUtil.formatFileSize(diskSize);
				info.DiskSize = StringUtil.formatFloatValue(fileSizePair.Value)
						+ StringUtil.getUnitString(fileSizePair.Unit);
				
				StringUtil.SizePair fileSpacePair = StringUtil.formatFileSize(diskSpace);
				info.DiskSpace = StringUtil.formatFloatValue(fileSpacePair.Value)
						+ StringUtil.getUnitString(fileSpacePair.Unit);
				
				StringUtil.SizePair fileUsedPair = StringUtil.formatFileSize(diskUsed);
				info.DiskUsed = StringUtil.formatFloatValue(fileUsedPair.Value)
						+ StringUtil.getUnitString(fileUsedPair.Unit);
			}
		}

		return info;
	}
}
