package com.dbstar.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class SqliteUtils {

	private static final String DATA_BASE_FILE_PATH = "/data/dbstar/Dbstar.db";
	public static final String TABLE_NAME = "Global";
	public static final String COLUMN_NMAE = "Name";
	public static final String COLUMN_VALUE = "Value";

	private static SqliteUtils mSatelliteSetting;
	private SQLiteDatabase mDatabase;

	private SqliteUtils() {

	}

	public static SqliteUtils getInstance() {
		if (mSatelliteSetting == null)
			mSatelliteSetting = new SqliteUtils();
		return mSatelliteSetting;
	}

	public SQLiteDatabase getWritableDatabase() {
		return SQLiteDatabase.openDatabase(DATA_BASE_FILE_PATH, null, SQLiteDatabase.OPEN_READWRITE);
	}

	public SQLiteDatabase getReadableDatabase() {
		return SQLiteDatabase.openDatabase(DATA_BASE_FILE_PATH, null, SQLiteDatabase.OPEN_READONLY);
	}

	public String queryValue(String columnValue) {
		String value = null;
		
		if (mDatabase == null || !mDatabase.isOpen()) {
			mDatabase = getInstance().getReadableDatabase();
		}
		
		Cursor cursor = null;
		try {
			cursor = mDatabase.query(TABLE_NAME, new String[] { COLUMN_VALUE }, COLUMN_NMAE + " = ? ", new String[] { columnValue }, null, null, null);
			if (cursor != null) {
				cursor.moveToNext();
				value = cursor.getString(cursor.getColumnIndex(COLUMN_VALUE));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
		}

		return value;

	}

	public long insert(String name, String value) {
		long rowId = -1;
		if (mDatabase == null || !mDatabase.isOpen()) {
			mDatabase = getReadableDatabase();
		}
		String sql = "REPLACE INTO " + TABLE_NAME + "(" + COLUMN_NMAE + "," + COLUMN_VALUE + " ) VALUES (?,?)";
		SQLiteStatement statement;
		try {
			statement = mDatabase.compileStatement(sql);
			statement.bindString(1, name);
			statement.bindString(2, value);
			rowId = statement.executeInsert();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rowId;
	}

	public void onDestroy() {
		if (mDatabase != null) {
			mDatabase.close();
			mDatabase = null;
		}

		mSatelliteSetting = null;
	}
}
