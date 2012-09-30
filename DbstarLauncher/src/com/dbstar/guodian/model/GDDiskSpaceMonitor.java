package com.dbstar.guodian.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.util.Log;

public class GDDiskSpaceMonitor {

	private static final String TAG = "GDDiskSpaceMonitor";
	
	private static final int CheckDiskInterval = 60000;//60s
	private Handler mAppHandler;
	private HandlerThread mBackgroundThread;
	private Handler mBackgroundHandler;
	
	private List<String> mDisks = null;
	private Object mLock = new Object();
	
	private boolean isDiskPathValid() {
		synchronized(mLock) {
			if (mDisks!=null && mDisks.size() >0)
				return true;
			else
				return false;
		}
	}
	
	public void addDiskToMonitor(String disk) {
		synchronized(mLock) {
			if (mDisks==null) {
				mDisks = new ArrayList<String>();
			}
			
			mDisks.add(disk);
		}
	}
	
	public void removeDiskFromMonitor(String disk) {
		synchronized(mLock) {
			if (mDisks==null)
				return;
			
			mDisks.remove(disk);
		}
	}

	private Runnable mCheckDiskTask = new Runnable() {

		@Override
		public void run() {
			Log.d(TAG, "check Disk space!");
			if (!isDiskPathValid())
				return;

			int diskCount = 0;
			synchronized(mLock) {
				diskCount = mDisks.size();
			}
	
			int index = 0;
			while(index < diskCount) {
				String disk = null;
				synchronized(mLock) {
					disk = mDisks.get(index);
				}
				index++;
				
				GDDiskInfo.DiskInfo diskInfo = GDDiskInfo.getDiskInfo(disk, false);
				if (diskInfo.RawDiskSpace < diskInfo.RawDiskSize/100) {
					Message msg = mAppHandler.obtainMessage(GDCommon.MSG_DISK_SPACEWARNING);
					Bundle data = new Bundle();
					data.putString(GDCommon.KeyDisk, disk);
					msg.setData(data);
					mAppHandler.sendMessage(msg);
				}
			}
			
			// do this in period
			mBackgroundHandler.postDelayed(mCheckDiskTask, CheckDiskInterval);
		}
		
	};
	
	public GDDiskSpaceMonitor(Handler handler) {
		
		mAppHandler = handler;
		
		mBackgroundThread = new HandlerThread(TAG, Process.THREAD_PRIORITY_BACKGROUND);
		
		mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
	}
	
	public void startMonitor() {
		mBackgroundHandler.postDelayed(mCheckDiskTask, CheckDiskInterval);
	}
	
	public void stopMonitor() {
		mBackgroundHandler.removeCallbacks(mCheckDiskTask);
	}
}
