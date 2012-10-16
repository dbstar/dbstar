package com.dbstar.model;

import java.io.File;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class GDDBProvider {
	private static final String TAG = "GDDBProvider";
	
	protected static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	
	protected GDSystemConfigure mConfigure = null;
	protected SQLiteDatabase mDataBase = null;
	protected String mDbFile = null;

	protected synchronized boolean isFileExist(String filePath) {
		boolean exist = false;

		if (filePath == null || filePath.isEmpty())
			return false;

		File file = new File(filePath);
		if (file != null && file.exists()) {
			exist = true;
		}

		return exist;
	}

	protected synchronized void createDatabase(String dbFile) {
		
		Log.d(TAG, "++++++++++++++++++createDatabase " + dbFile);

		if (!mConfigure.isDiskAvailable())
			return;
		
		if (dbFile!=null && !dbFile.isEmpty() && !isFileExist(dbFile)) {
			SQLiteDatabase db = null;
			db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
			Log.d(TAG, "dbFile=" + dbFile);
			Log.d(TAG, "db=" + db);
			db.beginTransaction();
			try {
				onCreate(db);
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
			
			if (db != null) {
				db.close();
			}
		}
	}

	protected synchronized SQLiteDatabase openDatabase(String dbFile, boolean isReadOnly) {

		Log.d(TAG, "open dbFile = " + dbFile);
		
		if (!mConfigure.isDiskAvailable())
			return null;

		SQLiteDatabase db = null;
		try {

			int flags = (isReadOnly ? SQLiteDatabase.OPEN_READONLY
					: SQLiteDatabase.OPEN_READWRITE)
					| SQLiteDatabase.NO_LOCALIZED_COLLATORS;
			db = SQLiteDatabase.openDatabase(dbFile, null, flags);
		} catch (Exception e) {
			e.printStackTrace();
		}

		mDataBase = db;

		return db;
	}

	protected synchronized SQLiteDatabase getReadableDatabase() {
		String dbFile = mDbFile;
		if (!mConfigure.isDiskAvailable() || !isFileExist(dbFile)) {
			return null;
		}
		
		Log.d(TAG, "getReadableDatabase ");

		SQLiteDatabase db = null;

		if (mDataBase != null) {
			Log.d(TAG, "mDataBase.isOpen() " + mDataBase.isOpen());
			if (mDataBase.isOpen()) {
				db = mDataBase;
			} else {
				mDataBase = null;
			}
		}

		if (db == null) {
			db = openDatabase(dbFile, true);
		}

		return db;
	}

	protected synchronized SQLiteDatabase getWriteableDatabase() {
		String dbFile = mDbFile;
		if (!mConfigure.isDiskAvailable() || !isFileExist(dbFile)) {
			return null;
		}
		
		Log.d(TAG, "getWriteableDatabase ");

		SQLiteDatabase db = null;

		if (mDataBase != null) {
			Log.d(TAG, "mDataBase.isOpen() " + mDataBase.isOpen() + " ");
			
			if (mDataBase.isOpen()) {
				Log.d(TAG, "mDataBase.isReadOnly() " + mDataBase.isReadOnly());
				if (!mDataBase.isReadOnly()) {
					db = mDataBase;
				} else {
					mDataBase.close();
					mDataBase = null;
				}
			} else {
				mDataBase = null;
			}
		}

		if (db == null) {
			db = openDatabase(dbFile, false);
		}

		return db;
	}

	protected synchronized SQLiteDatabase reOpenDb(boolean isReadOnly) {
		SQLiteDatabase db = null;
		if (isReadOnly) {
			db = getReadableDatabase();
		} else {
			db = getWriteableDatabase();
		}
		if (db != null && db.isOpen()) {
			onOpen(db, isReadOnly);
		}
		return db;
	}

	protected synchronized void closeDatabase() {
		if (mDataBase != null && mDataBase.isOpen()) {
			mDataBase.close();
			mDataBase = null;
		}
	}

	protected boolean initialize(GDSystemConfigure configure) {
		Log.d(TAG, "initialize");

		mConfigure = configure;

		return true;
	}

	protected synchronized void deinitialize() {
		Log.d(TAG, "deinitialize");
		mDbFile = "";
		if (mDataBase != null && mDataBase.isOpen()) {
			mDataBase.close();
			mDataBase = null;
		}
	}

	protected void onCreate(SQLiteDatabase db) {

	}

	protected void onOpen(SQLiteDatabase db, boolean isReadOnly) {

	}

	// @Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		SQLiteDatabase db = getReadableDatabase();
		if (db == null || !db.isOpen()) {
			return null;
		}

		Cursor curosr = null;
		String table = getTableName(sURIMatcher.match(uri));
		Log.d(TAG, "table = " + table);

		if (table != null && !table.isEmpty()) {
			Log.d(TAG, " query");

			curosr = db.query(table, projection, selection, selectionArgs,
					null, null, sortOrder);
		}

		return curosr;
	}

	// @Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {

		SQLiteDatabase db = getWriteableDatabase();
		if (db == null || !db.isOpen()) {
			return -1;
		}

		String table = getTableName(sURIMatcher.match(uri));
		int count = 0;
		if (table != null && !table.isEmpty()) {
			count = db.delete(table, selection, selectionArgs);

			Log.d(TAG, " delete count " + count);

			// if (count > 0) {
			// getContext().getContentResolver().notifyChange(uri, null);
			// }
		}

		return count;
	}

	// @Override
	public Uri insert(Uri uri, ContentValues values) {

		SQLiteDatabase db = getWriteableDatabase();
		if (db == null || !db.isOpen()) {
			return null;
		}

		String table = getTableName(sURIMatcher.match(uri));

		long rowId = -1;
		Uri retUri;
		if (table != null && !table.isEmpty()) {
			Log.d(TAG, " insert");

			rowId = db.insert(table, null, values);
			if (rowId > 0) {
				Log.d(TAG, " insert at id=" + rowId);
//				retUri = ContentUris.withAppendedId(Global.CONTENT_URI, rowId);
				retUri = ContentUris.withAppendedId(uri, rowId);
				// getContext().getContentResolver().notifyChange(retUri, null);
				return retUri;
			}
		}

		return null;
	}

	// @Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		SQLiteDatabase db = getWriteableDatabase();
		if (db == null || !db.isOpen()) {
			return -1;
		}

		String table = getTableName(sURIMatcher.match(uri));

		int count = 0;
		if (table != null && !table.isEmpty()) {
			count = db.update(table, values, selection, selectionArgs);
			Log.d(TAG, " update count " + count);

			// if (count > 0) {
			// getContext().getContentResolver().notifyChange(uri, null);
			// }
		}
		return count;
	}
	
	
	public boolean execBatchSql(String sql, String[][] bindArgs){
		boolean isSuccess = true;
		SQLiteDatabase db = getWriteableDatabase();
		
		try{
			db.beginTransaction();// 添加事务
			for(String[] args:bindArgs){
				
				Log.d(TAG, " execBatchSql " + sql + args);
				db.execSQL(sql, args);
			}
			db.setTransactionSuccessful();// 设置事务标志为成功，当结束事务时就会提交事务
		}catch(Exception e){
			isSuccess = false;
			e.printStackTrace();
		}finally{
			db.endTransaction();// 提交事务
		}
		return isSuccess;
	}
	
	
	protected String getTableName(int type) {
		return "";
	}
}
