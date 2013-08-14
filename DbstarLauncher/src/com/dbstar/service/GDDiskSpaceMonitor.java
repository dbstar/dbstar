package com.dbstar.service;

import java.util.ArrayList;
import java.util.List;

import com.dbstar.model.GDCommon;
import com.dbstar.model.GDDiskInfo;
import com.dbstar.util.LogUtil;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;

public class GDDiskSpaceMonitor {

	private static final String TAG = "GDDiskSpaceMonitor";

	private static final int CheckDiskInterval = 60000;
	private static final int DefaultGuardSize = 1090519040; // 10G 
	private static final int DefaultValidDiskSize = 109051904; // 1G

	private Handler mAppHandler;
	private HandlerThread mBackgroundThread;
	private Handler mBackgroundHandler;

	private List<String> mDisks = null;
	private Object mLock = new Object();
	private long mDiskGuardSize = 0;
	private int mCheckDiskInterval = 0;

	public GDDiskSpaceMonitor(Handler handler) {

		setCheckInterval(CheckDiskInterval);
		setGuardSize(DefaultGuardSize);

		mAppHandler = handler;

		mBackgroundThread = new HandlerThread(TAG,
				Process.THREAD_PRIORITY_BACKGROUND);

		mBackgroundThread.start();
		mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
	}

	public synchronized void setGuardSize(long guardSize) {
		mDiskGuardSize = guardSize;
	}

	public synchronized void setCheckInterval(int interval) {
		mCheckDiskInterval = interval;
	}

	public void startMonitor() {
		mBackgroundHandler.postDelayed(mCheckDiskTask, mCheckDiskInterval);
	}

	public void stopMonitor() {
		mBackgroundHandler.removeCallbacks(mCheckDiskTask);
	}

	public void addDiskToMonitor(String disk) {
		synchronized (mLock) {
			if (mDisks == null) {
				mDisks = new ArrayList<String>();
			}

			boolean needStart = false;

			if (!isContain(disk)) {
				mDisks.add(disk);

				// there is no disk before, so need to start monitor.
				needStart = mDisks.size() == 1 ? true : false;
			}

			if (needStart) {
				startMonitor();
			}
		}
	}

	public void removeDiskFromMonitor(String disk) {
		synchronized (mLock) {
			if (mDisks == null)
				return;

			mDisks.remove(disk);
		}
	}

	private boolean isContain(String disk) {
		boolean contain = false;

		for (int i = 0; i < mDisks.size(); i++) {
			if (disk.equals(mDisks.get(i))) {
				contain = true;
				break;
			}
		}

		return contain;
	}

	public void removeAllDiskFromMonitor() {

		synchronized (mLock) {
			if (mDisks == null)
				return;

			mDisks.clear();
		}
	}

	private synchronized long getGuardSize() {
		return mDiskGuardSize;
	}

	private Runnable mCheckDiskTask = new Runnable() {

		@Override
		public void run() {
		    LogUtil.d(TAG, "check Disk space!");

			int diskCount = 0;
			synchronized (mLock) {
				if (mDisks != null) {
					diskCount = mDisks.size();
				}

				if (diskCount == 0)
					return;
			}

			int index = 0;
			while (index < diskCount) {
				String disk = null;
				synchronized (mLock) {
					disk = mDisks.get(index);
				}

				GDDiskInfo.DiskInfo diskInfo = GDDiskInfo.getDiskInfo(disk,
						false);
				if (diskInfo != null) {
					if (diskInfo.RawDiskSize > DefaultValidDiskSize && diskInfo.RawDiskSpace < getGuardSize()) {
						Message msg = mAppHandler
								.obtainMessage(GDCommon.MSG_DISK_SPACEWARNING);
						Bundle data = new Bundle();
						data.putString(GDCommon.KeyDisk, disk);
						msg.setData(data);
						mAppHandler.sendMessage(msg);
					}
					
					index++;
				} else {
					// this disk not exist, maybe removed!
					removeDiskFromMonitor(disk);
					diskCount--;
				}
			}

			// do this in period
			if (diskCount > 0) {
				mBackgroundHandler.postDelayed(mCheckDiskTask, mCheckDiskInterval);
			}
		}

	};
}
