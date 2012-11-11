package com.dbstar.model;

import com.dbstar.model.GDSmartHomeContract.Global;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class GDSmartHomeProvider extends GDDBProvider {

	private static final String TAG = "GDSmartHomeProvider";

	private static final int GLOBAL = 1001;

	// Create Table Statement
	private static final String CREATE_GLOBAL_TABLE_STATEMENT = "CREATE TABLE IF NOT EXISTS global("
			+ Global.ID
			+ " integer primary key AutoIncrement, "
			+ Global.NAME
			+ " NVARCHAR(20), " + Global.VALUE + " NVARCHAR(20));";

	static {
		sURIMatcher.addURI(GDSmartHomeContract.AUTHORITY, "global", GLOBAL);
	}

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

	public String getTableName(int uri) {
		String table = "";
		switch (uri) {

		case GLOBAL:
			table = Tables.GLOBAL;
			break;
		}

		return table;
	}

	// @Override
	public boolean initialize(GDSystemConfigure configure) {
		super.initialize(configure);

		String dbFile = mConfigure.getSmartHomeDBFile();
		if (!isFileExist(dbFile)) {
			return false;
		}

		mDbFile = dbFile;

		return true;
	}

	public void deinitialize() {
		super.deinitialize();
	}

	protected void onOpen(SQLiteDatabase db) {
		db.execSQL(CREATE_GLOBAL_TABLE_STATEMENT);
	}

	// @Override
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
