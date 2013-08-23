package com.dbstar.model;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.dbstar.model.GDSmartHomeContract.Global;

public class GDSmartHomeProvider extends GDDBProvider {

	private static final String TAG = "GDSmartHomeProvider";

	private static final int GLOBAL = 1001;

	// Create Table Statement
//	private static final String CREATE_GLOBAL_TABLE_STATEMENT = "CREATE TABLE IF NOT EXISTS global("
//			+ Global.ID
//			+ " integer primary key AutoIncrement, "
//			+ Global.NAME
//			+ " NVARCHAR(20), " + Global.VALUE + " NVARCHAR(20));";
	
	private static final String CREATE_GLOBAL_TABLE_STATEMENT = "CREATE TABLE IF NOT EXISTS global("
			+ Global.NAME
			+ " NVARCHAR(32) PRIMARY KEY, " + Global.VALUE + " NVARCHAR(64));";

	static {
		sURIMatcher.addURI(GDSmartHomeContract.AUTHORITY, "global", GLOBAL);
	}

	interface Tables {
		String GLOBAL = "global";
	}

	public interface GlobalQuery {
		String TABLE = Tables.GLOBAL;

		String[] COLUMNS = new String[] { Global.NAME, Global.VALUE };

		int NAME = 0;
		int VALUE = 1;
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
