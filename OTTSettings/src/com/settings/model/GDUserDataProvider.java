package com.settings.model;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.settings.model.GDUserDataContract.FavoritePublication;
import com.settings.model.GDUserDataContract.FavoritePublicationSet;
import com.settings.utils.LogUtil;

public class GDUserDataProvider extends GDDBProvider {

	private static final String TAG = "GDUserDataProvider";

	private static final int PUBLICATIONTABLE = 1001;
	private static final int PUBLICATIONSETTABLE = 1002;

	static {
		sURIMatcher.addURI(GDDVBDataContract.AUTHORITY,
				GDUserDataContract.PUBLICATIONTABLE, PUBLICATIONTABLE);
		sURIMatcher.addURI(GDDVBDataContract.AUTHORITY,
				GDUserDataContract.PUBLICATIONSETTABLE, PUBLICATIONSETTABLE);
	};

	interface Tables {
		String PUBLICATION = GDUserDataContract.PUBLICATIONTABLE;
		String PUBLICATIONSET = GDUserDataContract.PUBLICATIONSETTABLE;
	};

	public interface PublicationQuery {
		String TABLE = Tables.PUBLICATION;

		String[] COLUMNS = new String[] { FavoritePublication.ID, FavoritePublication.PUBLICATIONID,
				FavoritePublication.URI };

		int ID = 0;
		int PUBLICATIONID = 1;
		int URI = 2;
	}

	public interface PublicationExQuery {
		String TABLE = Tables.PUBLICATION;

		String[] COLUMNS = new String[] { FavoritePublication.ID,
				FavoritePublication.PUBLICATIONID,
				FavoritePublication.URI, FavoritePublication.EPISODEINDEX };

		int ID = 0;
		int PUBLICATIONID = 1;
		int URI = 2;
		int EPISODEINDEX = 3;
	}

	public interface PublicationSetQuery {
		String TABLE = Tables.PUBLICATIONSET;

		String[] COLUMNS = new String[] { FavoritePublicationSet.ID,
				FavoritePublicationSet.SETID,
				FavoritePublicationSet.NAME,
				FavoritePublicationSet.DESCRIPTION,
				FavoritePublicationSet.POSTER, FavoritePublicationSet.TRAILER };

		int ID = 0;
		int SETID = 1;
		int NAME = 2;
		int DESCRIPTION = 3;
		int POSTER = 4;
		int TRAILER = 5;
	}

	public static final int FavoriteMovie = 0;
	public static final int FavoriteTV = 1;
	public static final int FavoriteRecord = 2;
	public static final int FavoriteEntertainment = 3;

	private static final String CREATE_PUBLICATION_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ Tables.PUBLICATION
			+ " ("
			+ FavoritePublication.ID
			+ " integer not NULL primary key AutoIncrement,"
			+ FavoritePublication.COLUMNTYPE
			+ " varchar(64) not NULL,"
			+ FavoritePublication.PUBLICATIONID
			+ " varchar(64) not NULL,"
			+ FavoritePublication.URI
			+ " varchar(256),"
			+ FavoritePublication.EPISODEINDEX + " varchar(32)" + ");";

	private static final String CREATE_PUBLICATIONSET_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ Tables.PUBLICATIONSET
			+ " ("
			+ FavoritePublicationSet.ID
			+ " integer not NULL primary key AutoIncrement,"
			+ FavoritePublicationSet.COLUMNTYPE
			+ " varchar(64) not NULL,"
			+ FavoritePublicationSet.SETID
			+ " varchar(64) not NULL,"
			+ FavoritePublicationSet.NAME
			+ " varchar(1024),"
			+ FavoritePublicationSet.DESCRIPTION
			+ " varchar(1024),"
			+ FavoritePublicationSet.POSTER
			+ " varchar(256),"
			+ FavoritePublicationSet.TRAILER + " varchar(256)" + ");";

	public GDUserDataProvider() {

	}

	public boolean initialize(GDSystemConfigure configure) {
		super.initialize(configure);

		mDbFile = configure.getUserDatabaseFile();

		createDatabase(mDbFile);

		return true;
	}

	public void deinitialize() {
		super.deinitialize();
	}

	public void onCreate(SQLiteDatabase db) {
		// create all tables
	    LogUtil.d(TAG, "onCreate");

	    LogUtil.d(TAG, "CREATE_PUBLICATIONSET_TABLE");
	    LogUtil.d(TAG, "CREATE_PUBLICATION_TABLE");

		db.execSQL(CREATE_PUBLICATIONSET_TABLE);
		db.execSQL(CREATE_PUBLICATION_TABLE);
	}

	public String getType(Uri uri) {
		int match = sURIMatcher.match(uri);
		String typeStr;
		switch (match) {
		case PUBLICATIONSETTABLE:
			typeStr = GDUserDataContract.FavoritePublicationSet.CONTENT_TYPE;
			break;
		case PUBLICATIONTABLE:
			typeStr = GDUserDataContract.FavoritePublication.CONTENT_TYPE;
			break;
		default:
			typeStr = null;
			break;
		}

		return typeStr;
	}

	public String getTableName(int uri) {
		String table = "";
		switch (uri) {

		case PUBLICATIONSETTABLE:
			table = Tables.PUBLICATIONSET;
			break;
		case PUBLICATIONTABLE:
			table = Tables.PUBLICATION;
			break;
		default:
			break;
		}

		return table;
	}

}
