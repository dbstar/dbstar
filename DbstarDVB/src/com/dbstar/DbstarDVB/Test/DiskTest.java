package com.dbstar.DbstarDVB.Test;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.Environment;
import android.os.ServiceManager;
import android.os.storage.IMountService;
import android.os.SystemProperties;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import java.util.List;
import java.util.ArrayList;

import com.dbstar.DbstarDVB.*;


public class DiskTest extends Activity implements OnClickListener {
	private static final String TAG = "DiskTest";

	private static final String DiskManageCmdPrepertyName = "service.disk_manage.cmd";
	private static final String DiskManageDevPrepertyName = "service.disk_manage.dev";
	private static final String DiskManageStatePrepertyName = "service.disk_manage.state";

	private BroadcastReceiver mExternalStorageReceiver;
    private IMountService mMountService = null;
	private boolean mExternalStorageAvailable = false;
	private boolean mExternalStorageWriteable = false;
    private String mMountPoint = "/mnt/sdb1";
    private String mDiskManageDev = "/dev/block/sdb";
    private String mDiskManageDev1 = "/dev/block/sdb1";

	private Toast mToast = null;
	private View Button01, Button02, Button03, Button04;
	private TextView info = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.disk_test);

		Button01 = this.findViewById(R.id.DiskButton01);
		Button01.setOnClickListener(this);
		Button02 = this.findViewById(R.id.DiskButton02);
		Button02.setOnClickListener(this);
		Button03 = this.findViewById(R.id.DiskButton03);
		Button03.setOnClickListener(this);
		Button04 = this.findViewById(R.id.DiskButton04);
		Button04.setOnClickListener(this);


		info = (TextView)findViewById(R.id.Info);

		startWatchingExternalStorage();
	}

	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
	}

	public void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.DiskButton01:
			showToast("get disk info");
			updateDiskDeviceMountInfo();
			break;
		case R.id.DiskButton02:
			showToast("create disk partition");
			createDiskPartition();
			break;
		case R.id.DiskButton03:
			showToast("delete disk partition");
			deleteDiskPartition();
			break;
		case R.id.DiskButton04:
			showToast("create file in sdcard");
			createFile();
			break;


		default:
			break;
		}
	}

	public void onDestroy() {
		super.onDestroy();
		stopWatchingExternalStorage();
	}

	private void updateDiskDeviceMountInfo() {
		String minfo = "DEV:\n";
		String[] devs = getDiskDevs();
		String[] mnts = getMountedDisks();
		if (devs != null) {
			for (String dev: devs) {
				Log.d(TAG, "getDiskDev: " + dev);
				minfo= minfo.concat(dev);
				minfo= minfo.concat("\n");
			}
		}
		minfo = minfo.concat("\nMNT:\n");
		if (mnts != null) {
			for (String mnt: mnts) {
				Log.d(TAG, "getMountedDisk: " + mnt);
				minfo = minfo.concat(mnt);
				minfo= minfo.concat("\n");
			}
		}
		info.setText(minfo);
	}

	private String[] getDiskDevs() {
		String[] valid_devs = { 
			"/dev/block/sda", 
			"/dev/block/sda1", 
			"/dev/block/sda2", 
			"/dev/block/sda3", 
			"/dev/block/sdb", 
			"/dev/block/sdb1", 
			"/dev/block/sdb2", 
			"/dev/block/sdb3", 
			"/dev/block/sdc", 
			"/dev/block/sdc1", 
			"/dev/block/sdc2", 
			"/dev/block/sdc3", 
		};

		String[] paths = null;
		ArrayList<String> list = new ArrayList<String>();

		File dev = new File("/dev/block/");
		if (dev == null || !dev.exists()) {
			Log.d(TAG, "No /dev/block folder!");
			return null;
		}

		File[] devs = dev.listFiles();
		if (devs == null)
			return null;

		for (File file : devs) {
			for (String vdev : valid_devs) {
				if (vdev.equals(file.toString())) {
					list.add(file.toString());
				}
			}
		}

		paths = (String[]) list.toArray(new String[list.size()]);

		return paths;
	}

	private String[] getMountedDisks() {
		String[] invalid_mnts = {
			"/mnt/sata",
			"/mnt/obb",
			"/mnt/asec",
			"/mnt/secure",
			"/mnt/sdcard",
		};
		String[] paths = null;
		ArrayList<String> list = new ArrayList<String>();

		File mnt = new File("/storage/external_storage");
		if (mnt == null || !mnt.exists()) {
			Log.d(TAG, "No /storage folder!");
			return null;
		}

		File[] mnts = mnt.listFiles();
		if (mnts == null)
			return null;

		for (File file : mnts) {
			boolean bingo = false;
			for (String ivpath : invalid_mnts) {
				if (ivpath.equals(file.toString())) {
					bingo = true;
				}
			}
			if (!bingo)
				list.add(file.toString());
		}

		paths = (String[]) list.toArray(new String[list.size()]);

		return paths;
	}


	private void showToast(String text) {
		if (mToast == null)
			mToast = Toast.makeText(this, null, Toast.LENGTH_SHORT);
		mToast.setText(text);
		mToast.show();
	}

	private void updateExternalStorageState() {
		String state = Environment.getExternalStorageState();
		File file = Environment.getExternalStorageDirectory();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		Log.d(TAG, "mExternalStorageAvailable: " + mExternalStorageAvailable);
		Log.d(TAG, "mExternalStorageWriteable: " + mExternalStorageWriteable);
		Log.d(TAG, "Environment.getExternalStorageDirectory: " + file.toString());
	}

	private void startWatchingExternalStorage() {
		mExternalStorageReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.i(TAG, "Storage: " + intent.getData());
				showToast(intent.getAction());
				updateExternalStorageState();
				updateDiskDeviceMountInfo();
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		filter.addAction(Intent.ACTION_MEDIA_REMOVED);
		filter.addDataScheme("file");
		registerReceiver(mExternalStorageReceiver, filter);
		updateExternalStorageState();
	}

	private void stopWatchingExternalStorage() {
		unregisterReceiver(mExternalStorageReceiver);
	}

    private void unmount() {
		showToast("unmount " + mMountPoint);
        IMountService mountService = getMountService();
        try {
            if (mountService != null) {
				mountService.unmountVolume(mMountPoint, true, false);
            } else {
                Log.e(TAG, "Mount service is null, can't mount");
            }
        } catch (RemoteException e) {
			Log.e(TAG, "unmount failed!");
		}
    }

    private void mount() {
		showToast("mount " + mMountPoint);
        IMountService mountService = getMountService();
        try {
            if (mountService != null) {
                mountService.mountVolume(mMountPoint);
            } else {
                Log.e(TAG, "Mount service is null, can't mount");
            }
        } catch (RemoteException ex) {
			Log.e(TAG, "mount failed!");
        }
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

	private void createDiskPartition() {
		SystemProperties.set(DiskManageCmdPrepertyName, "add");
		SystemProperties.set(DiskManageDevPrepertyName, mDiskManageDev);
		SystemProperties.set(DiskManageStatePrepertyName, "running");
	}

	private void deleteDiskPartition() {
		SystemProperties.set(DiskManageCmdPrepertyName, "del");
		SystemProperties.set(DiskManageDevPrepertyName, mDiskManageDev);
		SystemProperties.set(DiskManageStatePrepertyName, "running");
	}

	private void formatDiskPartition() {
		SystemProperties.set(DiskManageCmdPrepertyName, "format");
		SystemProperties.set(DiskManageDevPrepertyName, mDiskManageDev1);
		SystemProperties.set(DiskManageStatePrepertyName, "running");
	}

	private void createFile() {
		String path = "/storage/external_storage/sdcard1/tttest.txt";
		String value = "How are U";
		byte[] bytes = value.getBytes();

		Log.d(TAG, "save " + value + " to " + path);
		File file = new File(path);
		try {
			if (!file.exists()) {
				if (!file.createNewFile()) {
				Log.d(TAG, "create " + path + "error.");
				return;
				}
			}

			FileOutputStream fos = new FileOutputStream(file);
			fos.write(bytes);
			fos.close();

			Log.d(TAG, "== success == ");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
