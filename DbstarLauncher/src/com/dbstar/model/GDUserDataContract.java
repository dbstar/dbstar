package com.dbstar.model;

import android.net.Uri;
import android.provider.BaseColumns;

public class GDUserDataContract {
	public static final String AUTHORITY = "com.dbstar.provider";
	public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

	public static final String PUBLICATIONTABLE = "FavoritePublication";
	public static final String PUBLICATIONSETTABLE = "FavoritePublicationSet";

	public static final class FavoritePublication implements BaseColumns {

		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, "FavoritePublication");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.provider.FavoritePublication";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.provider.FavoritePublication";

		public static final String ID = "_id";
		public static final String COLUMNTYPE = "ColumnType";
		public static final String PUBLICATIONID = "PublicationID";
		public static final String URI = "URI";
		public static final String EPISODEINDEX = "EpisodeIndex";
	}

	public static final class FavoritePublicationSet implements BaseColumns {

		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, "FavoritePublicationSet");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.provider.FavoritePublicationSet";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.provider.FavoritePublicationSet";

		public static final String ID = "_id";
		public static final String COLUMNTYPE = "ColumnType";
		public static final String SETID = "SetID";
		public static final String NAME = "Name";
		public static final String DESCRIPTION = "Desc";
		public static final String POSTER = "Poster";
		public static final String TRAILER = "Trailer";
	}
}
