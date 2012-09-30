package com.dbstar.guodian.model;

import java.io.File;

import android.os.StatFs;
import android.util.Log;

import com.dbstar.guodian.util.StringUtil;

public class GDDiskInfo {

	private static final String TAG = "GDDiskInfo";

	static public class DiskInfo {
		public long RawDiskSize;
		public long RawDiskSpace;

		public String DiskSize;
		public String DiskSpace;
	}

	static public DiskInfo getDiskInfo(String diskPath, boolean convert) {
		Log.d(TAG, diskPath);
		DiskInfo info = null;
		
		File disk = new File(diskPath);
		if (disk == null || !disk.exists())
			return info;
		
		StatFs sf = new StatFs(diskPath);
		long blockSize = sf.getBlockSize();
		long blockCount = sf.getBlockCount();
		long availCount = sf.getAvailableBlocks();

		info = new DiskInfo();

		long diskSize = blockSize * blockCount;
		info.RawDiskSize = diskSize;

		long diskSpace = blockSize * availCount;
		info.RawDiskSpace = diskSpace;

		if (convert) {

			StringUtil.SizePair diskSizePair = StringUtil.formatSize(diskSize);
			info.DiskSize = StringUtil.formatFloatValue(diskSizePair.Value)
					+ StringUtil.getUnitString(diskSizePair.Unit);

			StringUtil.SizePair diskSpacePair = StringUtil
					.formatSize(diskSpace);
			info.DiskSpace = StringUtil.formatFloatValue(diskSpacePair.Value)
					+ StringUtil.getUnitString(diskSpacePair.Unit);
		}

		return info;
	}
}
