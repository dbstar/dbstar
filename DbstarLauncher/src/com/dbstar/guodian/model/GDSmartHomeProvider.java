package com.dbstar.guodian.model;

import java.io.File;

import com.dbstar.guodian.model.GDSmartHomeContract.Global;

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

	@Override
	public boolean onCreate() {
		boolean ret = true;
		mDataAccessor.configure();

		String dbFile = mDataAccessor.getSmartHomeDBFile();
		File file = new File(dbFile);
		if (!file.exists()) {
			return true;
		}
		
		Log.d(TAG, "mDBFile = " + dbFile);
		try {
			mDataBase = SQLiteDatabase.openDatabase(dbFile, null,
					SQLiteDatabase.CREATE_IF_NECESSARY
					| SQLiteDatabase.OPEN_READWRITE
					| SQLiteDatabase.NO_LOCALIZED_COLLATORS);
			// mActiveDb.set(mDataBase);
			mDataBase.execSQL(CREATE_GLOBAL_TABLE_STATEMENT);

		} catch (Exception e) {
			ret = false;
			e.printStackTrace();
		}

		return ret;
	}

	private boolean reOpenDb() {
		if (mDataBase != null) {
			return true;
		}
		
		boolean ret = false;
		mDataAccessor.configure();

		String dbFile = mDataAccessor.getSmartHomeDBFile();
		File file = new File(dbFile);
		if (!file.exists()) {
			return ret;
		}
		
		Log.d(TAG, "mDBFile = " + dbFile);
		try {
			mDataBase = SQLiteDatabase.openDatabase(dbFile, null,
					SQLiteDatabase.CREATE_IF_NECESSARY
					| SQLiteDatabase.OPEN_READWRITE
					| SQLiteDatabase.NO_LOCALIZED_COLLATORS);
			// mActiveDb.set(mDataBase);
			mDataBase.execSQL(CREATE_GLOBAL_TABLE_STATEMENT);
			
			ret = true;

		} catch (Exception e) {
			e.printStackTrace();
			return ret;
		}
		
		return ret;
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

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		Cursor curosr = null;
		String table = null;
		
		if (!reOpenDb()) {
			return curosr;
		}

		int match = sURIMatcher.match(uri);
		switch (match) {
		case GLOBAL:
			table = Tables.GLOBAL;
			break;
		default:
			break;
		}

		SQLiteDatabase db = mDataBase;

		if (table != null && db != null && db.isOpen()) {
			Log.d(TAG, " query");

			curosr = db.query(table, projection, selection, selectionArgs,
					null, null, sortOrder);
		}

		return curosr;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		String table = null;

		if (!reOpenDb()) {
			return -1;
		}
		
		int match = sURIMatcher.match(uri);
		switch (match) {
		case GLOBAL:
			table = Tables.GLOBAL;
			break;
		default:
			break;
		}

		int count = 0;
		SQLiteDatabase db = mDataBase;
		if (table != null && db != null && db.isOpen()) {
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

		if (!reOpenDb()) {
			return null;
		}
		
		String table = null;

		int match = sURIMatcher.match(uri);
		switch (match) {
		case GLOBAL:
			table = Tables.GLOBAL;
			break;
		default:
			break;
		}

		long rowId = -1;
		SQLiteDatabase db = mDataBase;
		Uri retUri;
		if (table != null && db != null && db.isOpen()) {
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

		if (!reOpenDb()) {
			return -1;
		}
		
		String table = null;

		int match = sURIMatcher.match(uri);
		switch (match) {
		case GLOBAL:
			table = Tables.GLOBAL;
			break;
		default:
			break;
		}

		int count = 0;
		SQLiteDatabase db = mDataBase;
		if (table != null && db != null && db.isOpen()) {
			count = db.update(table, values, selection, selectionArgs);
			Log.d(TAG, " update count " + count);
			
			if (count > 0) {
				getContext().getContentResolver().notifyChange(uri, null);
			}
		}
		return 0;
	}

}
