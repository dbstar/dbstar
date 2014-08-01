package com.dbstar.model;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.dbstar.util.LogUtil;
import com.dbstar.model.APPVersion;

public class GDDBProvider {
	private static final String TAG = "GDDBProvider";

	protected static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);

	protected GDSystemConfigure mConfigure = null;
	protected SQLiteDatabase mDataBase = null;
	protected String mPreDbFile = null;	// if database is opened, but current db is not equal with previous, you should close previous db firstly
	protected String mDbFile = null;

	class FileInfo {
		String FileName;
		long LastModifiedTime;
	}

	List<FileInfo> mFiles = new LinkedList<FileInfo>();

	private FileInfo getFileInfo(String fileName) {
		FileInfo fileInfo = null;

		for (int i = 0; i < mFiles.size(); i++) {
			FileInfo info = mFiles.get(i);
			if (info.FileName.equalsIgnoreCase(fileName)) {
				fileInfo = info;
				break;
			}
		}

		return fileInfo;
	}

	private FileInfo addFileInfo(String fileName, long lastModifiedTime) {
		FileInfo fileInfo = null;
		for (int i = 0; i < mFiles.size(); i++) {
			FileInfo info = mFiles.get(i);
			if (info.FileName.equalsIgnoreCase(fileName)) {
				fileInfo = info;
				break;
			}
		}

		if (fileInfo != null) {
			fileInfo.LastModifiedTime = lastModifiedTime;
		} else {
			fileInfo = new FileInfo();
			fileInfo.FileName = fileName;
			fileInfo.LastModifiedTime = lastModifiedTime;
			mFiles.add(fileInfo);
		}

		return fileInfo;
	}

	private FileInfo addFileInfo(FileInfo fileInfo) {
		mFiles.add(fileInfo);

		return fileInfo;
	}

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

	// If the file is modified, we need to reopen it
	// Note: make sure this file exist before call this method
	protected boolean isNeedReopen(String fileName) {
		boolean modified = false;
		File file = new File(fileName);

		FileInfo fileInfo = getFileInfo(fileName);
		if (fileInfo == null) {
			// this file is first opened.
			fileInfo = new FileInfo();
			fileInfo.FileName = fileName;
			fileInfo.LastModifiedTime = file.lastModified();

			addFileInfo(fileInfo);

			modified = true;
		} else {
			if (fileInfo.LastModifiedTime != file.lastModified()) {
				modified = true;
			}
		}

		return modified;
	}

	protected synchronized void createDatabase(String dbFile) {

		LogUtil.d(TAG, "++++++++++++++++++createDatabase " + dbFile);

		if (!isFileExist(dbFile)) {
			SQLiteDatabase db = null;
			db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
			LogUtil.d(TAG, "dbFile=" + dbFile);
			LogUtil.d(TAG, "db=" + db);
			db.beginTransaction();
			try {
				onCreate(db);
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
				db.close();
			}
		}
	}

	protected synchronized SQLiteDatabase openDatabase(String dbFile,
			boolean isReadOnly) {

		LogUtil.d(TAG, "open dbFile = " + dbFile);

		if (!isFileExist(dbFile))
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
		mPreDbFile = dbFile;

		return db;
	}

	protected synchronized SQLiteDatabase reopenDatabase(String dbFile,
			boolean isReadOnly) {
		if (mDataBase != null) {
			if (mDataBase.isOpen()) {
				mDataBase.close();
			}
			mDataBase = null;
		}

		SQLiteDatabase db = openDatabase(dbFile, isReadOnly);

		return db;
	}

	protected synchronized SQLiteDatabase getReadableDatabase() {
		String dbFile = mDbFile;

		if (!isFileExist(dbFile)) {
			LogUtil.d(TAG, "getReadableDatabase: no such database["+dbFile+"]");
			return null;
		}

		LogUtil.d(TAG, "getReadableDatabase: "+dbFile);

		SQLiteDatabase db = null;

		if (mDataBase != null) {
			LogUtil.d(TAG, "mDataBase.isOpen() " + mDataBase.isOpen());
			if (mDataBase.isOpen()) {
				if(mPreDbFile==dbFile){
					db = mDataBase;
				}
				else{
					LogUtil.d(TAG, "getReadableDatabase: this db[" + dbFile + " is diffrent with pre db[" + mPreDbFile + "], close pre db and open new");
					mDataBase.close();
					mDataBase = null;
				}
			} else {
				mDataBase = null;
			}
		}

		if (db == null) {
			db = openDatabase(dbFile, true);
		}

		return db;
	}

	protected synchronized SQLiteDatabase getDeviceGlobalReadableDatabase() {
		
		LogUtil.d("getDeviceGlobalReadableDatabase", mConfigure + ">>>>>>>>>>>====");
		
		String dbFile = mConfigure.getDeviceGlobalDB();

		if (!isFileExist(dbFile)) {
			LogUtil.d(TAG, "getDeviceGlobalReadableDatabase: no such database["+dbFile+"]");
			return null;
		}

		LogUtil.d(TAG, "getDeviceGlobalReadableDatabase: "+dbFile);

		SQLiteDatabase db = null;

		if (mDataBase != null) {
			LogUtil.d(TAG, "getDeviceGlobalReadableDatabase: mDataBase.isOpen() " + mDataBase.isOpen());
			if (mDataBase.isOpen()) {
				if(mPreDbFile==dbFile){
					db = mDataBase;
				}
				else{
					LogUtil.d(TAG, "getDeviceGlobalReadableDatabase: this db[" + dbFile + "is diffrent with pre db[" + mPreDbFile + "], close pre db and open new");
					mDataBase.close();
					mDataBase = null;
				}
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

		if (!isFileExist(dbFile)) {
			LogUtil.d(TAG, "getWriteableDatabase: no such database["+dbFile+"]");
			return null;
		}

		LogUtil.d(TAG, "getWriteableDatabase: "+dbFile);

		SQLiteDatabase db = null;

		if (mDataBase != null) {
			LogUtil.d(TAG, "mDataBase.isOpen() " + mDataBase.isOpen() + " ");

			if (mDataBase.isOpen()) {
				LogUtil.d(TAG,
						"mDataBase.isReadOnly() " + mDataBase.isReadOnly());
				if (!mDataBase.isReadOnly()) {
					if(mPreDbFile==dbFile){
						db = mDataBase;
					}
					else{
						LogUtil.d(TAG, "getWriteableDatabase: this db[" + dbFile + "is diffrent with pre db[" + mPreDbFile + "], close pre db and open new");
						mDataBase.close();
						mDataBase = null;
					}
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

	protected synchronized SQLiteDatabase getDeviceGlobalWriteableDatabase() {
		String dbFile = mConfigure.getDeviceGlobalDB();

		if (!isFileExist(dbFile)) {
			LogUtil.d(TAG, "getDeviceGlobalWriteableDatabase: no such database["+dbFile+"]");
			return null;
		}

		LogUtil.d(TAG, "getDeviceGlobalWriteableDatabase: "+dbFile);

		SQLiteDatabase db = null;

		if (mDataBase != null) {
			LogUtil.d(TAG, "mDataBase.isOpen() " + mDataBase.isOpen() + " ");

			if (mDataBase.isOpen()) {
				LogUtil.d(TAG,
						"mDataBase.isReadOnly() " + mDataBase.isReadOnly());
				if (!mDataBase.isReadOnly()) {
					if(mPreDbFile==dbFile){
						db = mDataBase;
					}
					else{
						LogUtil.d(TAG, "getDeviceGlobalWriteableDatabase: this db[" + dbFile + "is diffrent with pre db[" + mPreDbFile + "], close pre db and open new");
						mDataBase.close();
						mDataBase = null;
					}
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

	protected synchronized void closeDatabase() {
		if (mDataBase != null && mDataBase.isOpen()) {
			mDataBase.close();
			mDataBase = null;
		}
	}

	protected boolean initialize(GDSystemConfigure configure) {
		mConfigure = configure;

		return true;
	}

	protected synchronized void deinitialize() {
		LogUtil.d(TAG, "deinitialize");
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
	public synchronized Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		SQLiteDatabase db = getReadableDatabase();
		if (db == null || !db.isOpen()) {
			return null;
		}

		Cursor curosr = null;
		String table = getTableName(sURIMatcher.match(uri));
		LogUtil.d(TAG, "table = " + table);

		if (table != null && !table.isEmpty()) {
			LogUtil.d(TAG, "do db.query(...)");

			curosr = db.query(table, projection, selection, selectionArgs,
					null, null, sortOrder);
		}

		return curosr;
	}
	
	public synchronized Cursor deviceGlobalQuery(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		SQLiteDatabase db = getDeviceGlobalReadableDatabase();
		if (db == null || !db.isOpen()) {
			return null;
		}

		Cursor curosr = null;
		String table = getTableName(sURIMatcher.match(uri));
		LogUtil.d(TAG, "deviceGlobalQuery: table = " + table);

		if (table != null && !table.isEmpty()) {
			LogUtil.d(TAG, "deviceGlobalQuery: db.query(...)");

			curosr = db.query(table, projection, selection, selectionArgs,
					null, null, sortOrder);
		}

		return curosr;
	}

	// @Override
	public synchronized int delete(Uri uri, String selection, String[] selectionArgs) {

		SQLiteDatabase db = getWriteableDatabase();
		if (db == null || !db.isOpen()) {
			return -1;
		}

		String table = getTableName(sURIMatcher.match(uri));
		int count = 0;
		if (table != null && !table.isEmpty()) {
			count = db.delete(table, selection, selectionArgs);

			LogUtil.d(TAG, " delete count " + count);

			// if (count > 0) {
			// getContext().getContentResolver().notifyChange(uri, null);
			// }
		}

		return count;
	}

	// @Override
	public synchronized Uri insert(Uri uri, ContentValues values) {

		SQLiteDatabase db = getWriteableDatabase();
		if (db == null || !db.isOpen()) {
			return null;
		}

		String table = getTableName(sURIMatcher.match(uri));

		long rowId = -1;
		Uri retUri;
		if (table != null && !table.isEmpty()) {
			LogUtil.d(TAG, " insert");

			rowId = db.insert(table, null, values);
			if (rowId > 0) {
				LogUtil.d(TAG, " insert at id=" + rowId);
				// retUri = ContentUris.withAppendedId(Global.CONTENT_URI,
				// rowId);
				retUri = ContentUris.withAppendedId(uri, rowId);
				// getContext().getContentResolver().notifyChange(retUri, null);
				return retUri;
			}
		}

		return null;
	}

	// @Override
	public synchronized Uri DeviceGlobalinsert(Uri uri, ContentValues values) {

		SQLiteDatabase db = getDeviceGlobalWriteableDatabase();
		if (db == null || !db.isOpen()) {
			return null;
		}

		String table = getTableName(sURIMatcher.match(uri));

		long rowId = -1;
		Uri retUri;
		if (table != null && !table.isEmpty()) {
			LogUtil.d(TAG, " insert");

			rowId = db.insert(table, null, values);
			if (rowId > 0) {
				LogUtil.d(TAG, " insert at id=" + rowId);
				// retUri = ContentUris.withAppendedId(Global.CONTENT_URI,
				// rowId);
				retUri = ContentUris.withAppendedId(uri, rowId);
				// getContext().getContentResolver().notifyChange(retUri, null);
				return retUri;
			}
		}

		return null;
	}

	// @Override
	public synchronized int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		SQLiteDatabase db = getWriteableDatabase();
		if (db == null || !db.isOpen()) {
			return -1;
		}

		String table = getTableName(sURIMatcher.match(uri));

		int count = 0;
		if (table != null && !table.isEmpty()) {
			count = db.update(table, values, selection, selectionArgs);
			LogUtil.d(TAG, " update count " + count);

			// if (count > 0) {
			// getContext().getContentResolver().notifyChange(uri, null);
			// }
		}
		return count;
	}

	// @Override
	public synchronized int DeviceGlobalupdate(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		SQLiteDatabase db = getDeviceGlobalWriteableDatabase();
		if (db == null || !db.isOpen()) {
			return -1;
		}

		String table = getTableName(sURIMatcher.match(uri));

		int count = 0;
		if (table != null && !table.isEmpty()) {
			count = db.update(table, values, selection, selectionArgs);
			LogUtil.d(TAG, " update count " + count);

			// if (count > 0) {
			// getContext().getContentResolver().notifyChange(uri, null);
			// }
		}
		return count;
	}

	public synchronized boolean execBatchSql(String sql, String[][] bindArgs) {
		boolean isSuccess = true;
		SQLiteDatabase db = getWriteableDatabase();

		try {
			db.beginTransaction();
			for (String[] args : bindArgs) {

				LogUtil.d(TAG, " execBatchSql " + sql + args);
				db.execSQL(sql, args);
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			isSuccess = false;
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
		return isSuccess;
	}

	public synchronized Cursor rawQuery(String sql, String[] selectionArgs) {
		SQLiteDatabase db = getReadableDatabase();
		if (db == null || !db.isOpen()) {
			return null;
		}

		return db.rawQuery(sql, selectionArgs);
	}

	protected String getTableName(int type) {
		return "";
	}
}
