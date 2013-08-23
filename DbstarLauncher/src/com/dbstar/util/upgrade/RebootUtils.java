package com.dbstar.util.upgrade;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.os.PowerManager;
import android.os.RecoverySystem;

import com.dbstar.util.LogUtil;

public class RebootUtils {
	private final static String TAG = "Upgrade.RebootUtils";

	public static void rebootInstallPackage(final Context context,
			final String packageFile) {
		if (packageFile == null || packageFile.isEmpty())
			return;

		File file = new File(packageFile);
		if (file != null && file.exists()) {
			rebootInstallPackage(context, file);
		}
	}

	public static void rebootInstallPackage(final Context context,
			final File packageFile) {
		LogUtil.w(TAG, "!!! REBOOT INSTALL PACKAGE !!!");

		LogUtil.d(TAG, "file path is " + packageFile.getPath());
		// The reboot call is blocking, so we need to do it on another thread.
		Thread thread = new Thread("Reboot") {
			@Override
			public void run() {
				try {
					RecoverySystem.installPackage(context, packageFile);
				} catch (IOException e) {
				    LogUtil.e(TAG, "Can't perform rebootInstallPackage", e);
				}
			}
		};
		thread.start();
	}

	public static void rebootRecovery(Context context) {
	    LogUtil.w(TAG, "!!! REBOOT RECOVERY !!!");
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		pm.reboot("recovery");
	}

	public static void rebootNormal(Context context) {
	    LogUtil.w(TAG, "!!! REBOOT NORMAL !!!");
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		pm.reboot("normal_reboot");
	}

	public static void rebootWipeUserData(final Context context) {
	    LogUtil.w(TAG, "!!! REBOOT WIPE USER DATA !!!");
		// The reboot call is blocking, so we need to do it on another thread.
		Thread thread = new Thread("Reboot") {
			@Override
			public void run() {
				try {
					RecoverySystem.rebootWipeUserData(context);
				} catch (IOException e) {
				    LogUtil.e(TAG, "Can't perform rebootInstallPackage", e);
				}
			}
		};
		thread.start();
	}
}
