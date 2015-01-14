/*
 * Copyright (C) 2007-2013 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package com.media.zlibrary.ui.android.library;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.media.player.common.Utils;
import com.media.reader.model.ImageManager;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Process;

public class UncaughtExceptionHandler implements java.lang.Thread.UncaughtExceptionHandler {
	private final Context myContext;

	public UncaughtExceptionHandler(Context context) {
		myContext = context;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable exception) {
		final StringWriter stackTrace = new StringWriter();
		exception.printStackTrace(new PrintWriter(stackTrace));
		System.err.println(stackTrace);

		Utils.printLogError("UncaughtExceptionHandler", exception.getMessage());
//		Intent intent = new Intent(
//			"android.dbstarplayer.action.CRASH",
//			new Uri.Builder().scheme(exception.getClass().getSimpleName()).build()
//		);
//		try {
//			myContext.startActivity(intent);
//		} catch (ActivityNotFoundException e) {
//			intent = new Intent(myContext, BugReportActivity.class);
//			intent.putExtra(BugReportActivity.STACKTRACE, stackTrace.toString());
//			myContext.startActivity(intent);
//		}
		try {
			NotificationManager nm = (NotificationManager) myContext.getSystemService(Context.NOTIFICATION_SERVICE);
			nm.cancel(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (myContext instanceof Activity) {
			((Activity)myContext).finish();
		}

		ImageManager.getInstance().clearBitmapCache();
		Process.killProcess(Process.myPid());
		System.exit(10);
	}
}
