package com.settings.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class DataUtils {
	// 系统保存偏好设置的引用
	private static SharedPreferences marsorPreferences = null;
	/** 系统偏好设置的名称 */
	public static final String SharedPreferences_Name = "CommonPreferences";
	/** 当系统打印Log的时候，统一使用这个作为TagName */
	public static final String Log_TagName = "MarsorAndroidCommon";
	
	//**-------------------------保存和获取系统偏好设置-------------------------------**//
	private static SharedPreferences getPreferences(Context context) {
		if (marsorPreferences == null) {
			if (context == null) {
				return null;
			}
			marsorPreferences = context.getSharedPreferences(SharedPreferences_Name, Context.MODE_PRIVATE);
		}
		return marsorPreferences;
	}
	public static void savePreference(Context context, String key, Boolean value) {
		try {
			getPreferences(context).edit().putBoolean(key, value).commit();
		} catch (Exception e) {
			Log.e(Log_TagName, "save preference failed!", e);
		}
	}

	public static boolean getPreference(Context context, String key, boolean defValue) {
		try {
			return getPreferences(context).getBoolean(key, defValue);
		} catch (Exception e) {
			Log.e(Log_TagName, "save preference failed!", e);
		}
		return defValue;
	}

	public static void savePreference(Context context, String key, float value) {
		try {
			getPreferences(context).edit().putFloat(key, value).commit();
		} catch (Exception e) {
			Log.e(Log_TagName, "save preference failed!", e);
		}
	}

	public static float getPreference(Context context, String key, float defValue) {
		try {
			return getPreferences(context).getFloat(key, defValue);
		} catch (Exception e) {
			Log.e(Log_TagName, "save preference failed!", e);
		}
		return defValue;
	}

	public static void savePreference(Context context, String key, int value) {
		try {
			getPreferences(context).edit().putInt(key, value).commit();
		} catch (Exception e) {
			Log.e(Log_TagName, "save preference failed!", e);
		}
	}

	public static int getPreference(Context context, String key, int defValue) {
		try {
			return getPreferences(context).getInt(key, defValue);
		} catch (Exception e) {
			Log.e(Log_TagName, "save preference failed!", e);
		}
		return defValue;
	}

	public static void savePreference(Context context, String key, long value) {
		try {
			getPreferences(context).edit().putLong(key, value).commit();
		} catch (Exception e) {
			Log.e(Log_TagName, "save preference failed!", e);
		}
	}

	public static long getPreference(Context context, String key, long defValue) {
		try {
			return getPreferences(context).getLong(key, defValue);
		} catch (Exception e) {
			Log.e(Log_TagName, "save preference failed!", e);
		}
		return defValue;
	}

	public static void savePreference(Context context, String key, String value) {
		try {
			getPreferences(context).edit().putString(key, value).commit();
		} catch (Exception e) {
			Log.e(Log_TagName, "save preference failed!", e);
		}
	}

	public static String getPreference(Context context, String key, String defValue) {
		try {
			return getPreferences(context).getString(key, defValue);
		} catch (Exception e) {
			Log.e(Log_TagName, "save preference failed!", e);
		}
		return defValue;
	}
	
}
