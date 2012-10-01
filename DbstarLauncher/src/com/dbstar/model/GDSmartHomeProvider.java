package com.dbstar.model;

import java.io.File;

import com.dbstar.model.GDDVBDataProvider.Tables;
import com.dbstar.model.GDSmartHomeContract.Global;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class GDSmartHomeProvider extends ContentProvider {

	private static final String TAG = "GDSmartHomeProvider";

	private static final int GLOBAL = 1001;

	// Create Table Statement
	private static final String CREATE_GLOBAL_TABLE_STATEMENT = "CREATE TABLE IF NOT EXISTS global("
			+ Global.ID + " integer primary key AutoIncrement, "
			+ Global.NAME + " NVARCHAR(20), "
			+ Global.VALUE + " NVARCHAR(20));";

	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(GDSmartHomeContract.AUTHORITY, "global", GLOBAL);
	}

	private SQLiteDatabase mDataBase = null;
	GDDataAccessor mDataAccessor = new GDDataAccessor();

	interface Tables {
		String GLOBAL = "global";
	}

	public interface GlobalQuery {
		String TABLE = Tables.GLOBAL;

		String[] COLUMNS = new String[] { Global.ID, Global.NAME, Global.VALUE };

		int ID = 0;
		int NAME = 1;
		int VALUE = 2;
	}

SQLiteDatabase openDatabase (String dbFile, boolean isReadOnly) {
		
		Log.d(TAG, "open dbFile = " + dbFile);
		
		SQLiteDatabase db = null;
		try {
			
			int flags = (isReadOnly ? SQLiteDatabase.OPEN_READONLY : SQLiteDatabase.OPEN_READWRITE) | SQLiteDatabase.NO_LOCALIZED_COLLATORS;
			db = SQLiteDatabase.openDatabase(dbFile, null, flags);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return db;
	} 
	
	boolean isFileExist(String filePath) {
		boolean exist = false;
		
		if (filePath == null || filePath.isEmpty())
			return false;
		
		File file = new File(filePath);
		if (file != null && file.exists()) {
			exist = true;
		}
		
		return exist;
	}
	
	SQLiteDatabase getReadableDatabase() {
		String dbFile = mDataAccessor.getSmartHomeDBFile();
		if (dbFile == null || dbFile.isEmpty()) {
			// configure again here
			if (mDataAccessor.configure()) {
				dbFile = mDataAccessor.getDatabaseFile();
				if (!isFileExist(dbFile))
					return null;
			}
		}
		
		SQLiteDatabase db = openDatabase(dbFile, true);
		
		return db;
	}
	
	SQLiteDatabase getWriteableDatabase() {
		String dbFile = mDataAccessor.getSmartHomeDBFile();
		if (dbFile == null || dbFile.isEmpty()) {
			// configure again here
			if (mDataAccessor.configure()) {
				dbFile = mDataAccessor.getDatabaseFile();
				if (!isFileExist(dbFile))
					return null;
			}
		}
		
		SQLiteDatabase db = openDatabase(dbFile, false);
		
		return db;
	}
	
	private SQLiteDatabase reOpenDb() {
		SQLiteDatabase db = getWriteableDatabase();
		if (db != null && db.isOpen()) {
			db.execSQL(CREATE_GLOBAL_TABLE_STATEMENT);
		}
		return db;
	}
	
	String getTableName(int uri) {
		String table = "";
		switch (uri) {

		case GLOBAL:
			table = Tables.GLOBAL;
			break;
		}
		
		return table;
	}

	@Override
	public boolean onCreate() {
		Log.d(TAG, "onCreate");

		if (!mDataAccessor.configure()) {
			// if configure failed, we return, but the content provider 
			// will be created, and it will configure again when client 
			// try to query data.
			return true;
		}
		
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		SQLiteDatabase db = getReadableDatabase();
		if (db == null || !db.isOpen()) {
			return null;
		}
			
		Cursor curosr = null;
		String table = getTableName(sURIMatcher.match(uri));
		
		if (table != null && !table.isEmpty()) {
			Log.d(TAG, " query");

			curosr = db.query(table, projection, selection, selectionArgs,
					null, null, sortOrder);
		}

		return curosr;
	}

	@Override
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
	
			if (count > 0) {
				getContext().getContentResolver().notifyChange(uri, null);
			}
		}

		return count;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		SQLiteDatabase db = reOpenDb();
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
				retUri = ContentUris.withAppendedId(Global.CONTENT_URI, rowId);
				getContext().getContentResolver().notifyChange(retUri, null);
				return retUri;
			}
		}

		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		SQLiteDatabase db = reOpenDb();
		if (db == null || !db.isOpen()) {
			return -1;
		}
			
		String table = getTableName(sURIMatcher.match(uri));

		int count = 0;
		if (table != null && !table.isEmpty()) {
			count = db.update(table, values, selection, selectionArgs);
			Log.d(TAG, " update count " + count);
			
			if (count > 0) {
				getContext().getContentResolver().notifyChange(uri, null);
			}
		}
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		int match = sURIMatcher.match(uri);
		String typeStr;
		switch (match) {
		case GLOBAL:
			typeStr = GDSmartHomeContract.Global.CONTENT_TYPE;
			break;
		default:
			typeStr = null;
			break;
		}

		return typeStr;
	}

}
