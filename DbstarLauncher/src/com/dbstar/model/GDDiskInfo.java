package com.dbstar.model;

import java.io.File;

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

	static public DiskInfo getDiskInfo(String diskPath, boolean convert) {
		Log.d(TAG, diskPath);
		DiskInfo info = null;
		
		File disk = new File(diskPath);
		if (disk == null || !disk.exists())
			return info;
		

		info = new DiskInfo();

		long diskSize = disk.getTotalSpace();
        info.RawDiskSize = diskSize;

        long diskSpace = disk.getFreeSpace();
        info.RawDiskSpace = diskSpace;
        
        long diskUsed = diskSize - diskSpace;
        info.RawDiskUsed = diskUsed;

		if (convert) {

			StringUtil.SizePair diskSizePair = StringUtil.formatSize(diskSize);
			info.DiskSize = StringUtil.formatFloatValue(diskSizePair.Value)
					+ StringUtil.getUnitString(diskSizePair.Unit);

			StringUtil.SizePair diskSpacePair = StringUtil
					.formatSize(diskSpace);
			info.DiskSpace = StringUtil.formatFloatValue(diskSpacePair.Value)
					+ StringUtil.getUnitString(diskSpacePair.Unit);
			
			StringUtil.SizePair diskUsedPair = StringUtil
					.formatSize(diskUsed);
			
			info.DiskUsed = StringUtil.formatFloatValue(diskUsedPair.Value)
					+ StringUtil.getUnitString(diskUsedPair.Unit);
		}

		return info;
	}
}
