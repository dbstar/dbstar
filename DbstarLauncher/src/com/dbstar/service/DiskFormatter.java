package com.dbstar.service;

import java.io.File;

import com.dbstar.app.GDApplication;
import com.dbstar.model.GDCommon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.storage.IMountService;
import android.os.storage.StorageEventListener;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.os.storage.IMountService;
import android.os.SystemProperties;
import android.util.Log;

public class DiskFormatter {
	static final String TAG = "DiskFormatter";

	private static final int ErrUnknown = 0x0;
	private static final int ErrFormatError = 0x1;
	private static final int ErrMountError = 0x2;
	private static final int ErrUnMountError = 0x3;
	private static final int ErrChecking = 0x4;
	private static final int ErrRemoved = 0x5;
	private static final int ErrBadRemoval = 0x6;
	private static final int ErrShared = 0x7;
	private static final int ErrTimeout = 0x8;

	private StorageVolume mStorageVolume;
	private IMountService mMountService = null;
	private StorageManager mStorageManager = null;
	private Handler mHandler = null;
	private String mMountPoint;
	
	private boolean mFormatStarted = false;

	public DiskFormatter() {
		mFormatStarted = false;
	}

	private BroadcastReceiver mExternalStorageReceiver;
	private boolean mExternalStorageAvailable = false;
	private boolean mExternalStorageWriteable = false;
	
	
	// method - 1
	public void startFormatDisk(String disk, Handler handler) {
		mHandler = handler;
		mMountPoint = disk;

		startWatchingExternalStorage();
		
		updateProgressState(disk, Environment.MEDIA_MOUNTED);
	}
	
	public void finishFormatDisk() {
		stopWatchingExternalStorage();
		mFormatStarted = false;
	}
	
	void updateProgressState(String path, String newState) {
		if (mMountPoint == null) {
			formatDisk();
			return;
		}
		
		if (!mMountPoint.equals(path)) {
			return;
		}
		
		Log.d(TAG, " volume : " + path + " status:" + newState);

		String status = newState;

		if (Environment.MEDIA_MOUNTED.equals(status)
			|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(status)) {

			if (!unmount(path)) {
				formatFailed(String.valueOf(ErrUnMountError));
			}

		} else if (Environment.MEDIA_NOFS.equals(status)
				|| Environment.MEDIA_UNMOUNTED.equals(status)
				|| Environment.MEDIA_UNMOUNTABLE.equals(status)
				|| Environment.MEDIA_REMOVED.equals(status)) {
			formatDisk();
		} else if (Environment.MEDIA_BAD_REMOVAL.equals(status)) {
			formatFailed(String.valueOf(ErrBadRemoval));
		} else if (Environment.MEDIA_CHECKING.equals(status)) {
			formatFailed(String.valueOf(ErrChecking));
		} else if (Environment.MEDIA_REMOVED.equals(status)) {
			formatFailed(String.valueOf(ErrRemoved));
		} else if (Environment.MEDIA_SHARED.equals(status)) {
			formatFailed(String.valueOf(ErrShared));
		} else {
			formatFailed(String.valueOf(ErrUnknown));
		}
	}
	
	private void formatDisk() {
		if (mFormatStarted)
			return;
		
		mFormatStarted = true;
		
		formatDiskPartition("/dev/block/sda");
		
		new Thread() {
			@Override
			public void run() {
				int formatTime = 0;
				int err = 0;
				boolean successed = false;
				while (true) {
					try {
						formatTime += 4;
						if (formatTime > FormatTimeout) {
							err = ErrTimeout;
							break;
						}

						Thread.sleep(4000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					String propertyValue = SystemProperties.get(DiskManageStatePrepertyName);
					if (propertyValue.equals("stopped")) {
						Log.d(TAG, "format finished!");
						successed = true;
						break;
					}
				}
				
				if (successed) {
					formatSuccessed();
				} else {
					formatFailed(String.valueOf(err));
				}
			}
		}.start();
	}
	
	static final int MSG_DISK_REMOVED = 0xD001;
	static final int MSG_DISK_MOUNTED = 0xD002;

	Handler mDiskHandler = new Handler() {
		public void handleMessage(Message msg) {
			int msgId = msg.what;
			switch (msgId) {
			case MSG_DISK_REMOVED: {
				updateProgressState((String)msg.obj, Environment.MEDIA_REMOVED);
				break;
			}
			}
		}
	};

	private void startWatchingExternalStorage() {
		Context context = GDApplication.getAppContext();
		
		mExternalStorageReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.i(TAG, "Storage: " + intent.getData());
				
				String action = intent.getAction();
				Uri uri = intent.getData();
				
				Log.d(TAG, "---- action " + action);
				Log.d(TAG, "---- disk:" + uri.toString());

				if (action.equals(Intent.ACTION_MEDIA_REMOVED)
						|| action.equals(Intent.ACTION_MEDIA_BAD_REMOVAL)
						|| action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {

					Message message = mDiskHandler
							.obtainMessage(MSG_DISK_REMOVED);
					message.obj = uri.getPath();
					mDiskHandler.sendMessage(message);
				}
			}
		};

		IntentFilter filter = new IntentFilter();
		//filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		filter.addAction(Intent.ACTION_MEDIA_UNMOUNTABLE);
		filter.addAction(Intent.ACTION_MEDIA_REMOVED);
		filter.addDataScheme("file");
		context.registerReceiver(mExternalStorageReceiver, filter);
	}

	private void stopWatchingExternalStorage() {
		Context context = GDApplication.getAppContext();
		context.unregisterReceiver(mExternalStorageReceiver);
	}
	

    private boolean unmount(String disk) {
    	boolean ret = false;
        IMountService mountService = getMountService();
        try {
            if (mountService != null) {
				mountService.unmountVolume(disk, true, false);
				ret = true;
            } else {
                Log.e(TAG, "Mount service is null, can't mount");
            }
        } catch (RemoteException e) {
			Log.e(TAG, "unmount failed!");
		}
        
        return ret;
    }

    private void mount(String disk) {
        IMountService mountService = getMountService();
        try {
            if (mountService != null) {
                mountService.mountVolume(disk);
            } else {
                Log.e(TAG, "Mount service is null, can't mount");
            }
        } catch (RemoteException ex) {
			Log.e(TAG, "mount failed!");
        }
    }

	private static final String DiskManageCmdPrepertyName = "service.disk_manage.cmd";
	private static final String DiskManageDevPrepertyName = "service.disk_manage.dev";
	private static final String DiskManageStatePrepertyName = "service.disk_manage.state";
	private static final int FormatTimeout = 3600;
	
	private void formatDiskPartition(String disk) {
        SystemProperties.set(DiskManageCmdPrepertyName, "add");
        SystemProperties.set(DiskManageDevPrepertyName, disk);
        SystemProperties.set(DiskManageStatePrepertyName, "running");
    }
	
	private synchronized IMountService getMountService() {
		if (mMountService == null) {
			IBinder service = ServiceManager.getService("mount");
			if (service != null) {
				mMountService = IMountService.Stub.asInterface(service);
			} else {
				Log.e(TAG, "Can't get mount service");
			}
		}
		return mMountService;
	}

	private void formatSuccessed() {
		Message msg = mHandler.obtainMessage(GDCommon.MSG_DISK_FORMAT_FINISHED);
		msg.arg1 = GDCommon.VALUE_SUCCESSED;
		msg.sendToTarget();
	}

	private void formatFailed(String errorStr) {
		Message msg = mHandler.obtainMessage(GDCommon.MSG_DISK_FORMAT_FINISHED);
		msg.arg1 = GDCommon.VAULE_FAILED;
		msg.obj = errorStr;
		msg.sendToTarget();
	}
	
	// method - 2
	
//	StorageEventListener mStorageListener = new StorageEventListener() {
//		@Override
//		public void onStorageStateChanged(String path, String oldState,
//				String newState) {
//			Log.i(TAG, "Storage state changed :" + path + ":" + oldState
//					+ " to " + newState);
//			updateProgressState(path, newState);
//		}
//	};

	
	// this is not tested, maybe not work.
//	public void startFormat(String disk, Handler handler) {
//		Log.d(TAG, "format disk:" + disk);
//		
//		mHandler = handler;
//		
//		if (disk == null) {
//			formatFailed(String.valueOf(ErrRemoved));
//			return;
//		}
//
//		if (mStorageManager == null) {
//			Context context = GDApplication.getAppContext();
//			mStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
//			mStorageManager.registerListener(mStorageListener);
//		}
//
//		mStorageVolume = null;
//
//		StorageVolume[] storageVolumes = mStorageManager.getVolumeList();
//		for (StorageVolume volume : storageVolumes) {
//			String path = volume.getPath();
//			
//			Log.d(TAG, " volume list: volume=" + path);
//			
//			if (path != null && path.equals(disk)
//					&& path.length() == disk.length()) {
//				mStorageVolume = volume;
//				break;
//			}
//		}
//		
//		if (mStorageVolume != null) {
//			updateProgressState(mStorageVolume.getPath(), null);
//		} else {
//			formatFailed(String.valueOf(ErrRemoved));
//		}
//	}
//
//	public void finishFormat() {
//		if (mStorageManager != null) {
//			mStorageManager.unregisterListener(mStorageListener);
//		}
//	}
//
//	void updateProgressState(String path, String newState) {
//		if (!mStorageVolume.getPath().equals(path)) {
//			return;
//		}
//
//		String status = mStorageManager
//				.getVolumeState(mStorageVolume.getPath());
//		
//		Log.d(TAG, " volume : " + mStorageVolume.getPath() + " status:" + status);	
//
//		if (Environment.MEDIA_MOUNTED.equals(status)
//				|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(status)) {
//			IMountService mountService = getMountService();
//			final String extStoragePath = mStorageVolume.getPath();
//
//			try {
//				mountService.unmountVolume(extStoragePath, true, false);
//			} catch (RemoteException e) {
//				Log.w(TAG, "Failed talking with mount service", e);
//				
//				formatFailed(String.valueOf(ErrUnMountError));
//			}
//		} else if (Environment.MEDIA_NOFS.equals(status)
//				|| Environment.MEDIA_UNMOUNTED.equals(status)
//				|| Environment.MEDIA_UNMOUNTABLE.equals(status)) {
//
//			final IMountService mountService = getMountService();
//
//			final String extStoragePath = mStorageVolume.getPath();
//
//			if (mountService != null) {
//				new Thread() {
//					@Override
//					public void run() {
//						boolean success = false;
//						
//						try {
//							mountService.formatVolume(extStoragePath, "ntfs");
//							success = true;
//						} catch (Exception e) {
//							// format error!
//						}
//
//						if (success) {
//							// foramt success!
//							try {
//								mountService.mountVolume(extStoragePath);
//							} catch (RemoteException e) {
//								Log.w(TAG, "Failed talking with mount service",
//										e);
//								
//								formatFailed(String.valueOf(ErrMountError));
//								
//								return;
//							}
//
//							formatSuccessed();
//						} else {
//							formatFailed(String.valueOf(ErrFormatError));
//						}
//
//						return;
//					}
//				}.start();
//			} else {
//				Log.w(TAG, "Unable to locate IMountService");
//			}
//		} else if (Environment.MEDIA_BAD_REMOVAL.equals(status)) {
//			formatFailed(String.valueOf(ErrBadRemoval));
//		} else if (Environment.MEDIA_CHECKING.equals(status)) {
//			formatFailed(String.valueOf(ErrChecking));
//		} else if (Environment.MEDIA_REMOVED.equals(status)) {
//			formatFailed(String.valueOf(ErrRemoved));
//		} else if (Environment.MEDIA_SHARED.equals(status)) {
//			formatFailed(String.valueOf(ErrShared));
//		} else {
//			formatFailed(String.valueOf(ErrUnknown));
//		}
//	}

}
