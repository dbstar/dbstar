package com.dbstar.app;

import android.app.Application;
import android.content.Context;

public class GDApplication extends Application {
	private static Context sContext;

	public void onCreate() {
		super.onCreate();
		GDApplication.sContext = getApplicationContext();
	}

	public static Context getAppContext() {
		return GDApplication.sContext;
	}
}
