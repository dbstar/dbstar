package com.dbstar.model;

import android.net.Uri;
import android.util.Log;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.ContentValues;
import android.database.Cursor;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;

import com.dbstar.DbstarDVB.model.MediaData;
import com.dbstar.model.GDDVBDataContract.*;
import com.dbstar.model.GDDVBDataProvider.ColumnEntityQuery;
import com.dbstar.model.GDDVBDataProvider.ColumnQuery;
import com.dbstar.model.GDDVBDataProvider.ResStrQuery;
import com.dbstar.model.GDSmartHomeContract.Global;
import com.dbstar.model.GDUserDataContract.FavoritePublication;
import com.dbstar.model.GDUserDataContract.FavoritePublicationSet;

public class GDDataModel {
	private static final String TAG = "GDDataModel";

	private String mLocalization;

	GDDVBDataProvider mDVBDataProvider;
	GDSmartHomeProvider mSmartHomeProvider;
	GDUserDataProvider mUserDataProvider;

	public GDDataModel() {
		mDVBDataProvider = new GDDVBDataProvider();
		mSmartHomeProvider = new GDSmartHomeProvider();
		mUserDataProvider = new GDUserDataProvider();
	}

	public void initialize(GDSystemConfigure configure) {
		setLocalization(configure.getLocalization());
		mDVBDataProvider.initialize(configure);
		mSmartHomeProvider.initialize(configure);
		mUserDataProvider.initialize(configure);
	}

	public void deInitialize() {
		mDVBDataProvider.deinitialize();
		mSmartHomeProvider.deinitialize();
		mUserDataProvider.deinitialize();
	}

	public void setLocalization(String localization) {
		mLocalization = localization;
	}

	private String getLocalization() {
		return mLocalization;
	}

	public ColumnData[] getColumns(String columnId) {
		// Log.d(TAG, "getColumn id=" + columnId);
		Cursor cursor = null;
		ColumnData[] Columns = null;

		String selection = Column.PARENT_ID + "=?";
		String[] selectionArgs = new String[] { columnId };

		// cursor = mContext.getContentResolver().query(Column.CONTENT_URI,
		cursor = mDVBDataProvider.query(Column.CONTENT_URI,
				ColumnQuery.COLUMNS, selection, selectionArgs, null);
		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				Log.d(TAG, "query cursor size = " + cursor.getCount());
				Columns = new ColumnData[cursor.getCount()];
				int i = 0;
				do {
					Columns[i] = new ColumnData();
					Columns[i].Id = cursor.getString(ColumnQuery.ID);
					Columns[i].Type = cursor.getString(ColumnQuery.TYPE);
					Columns[i].IconNormalPath = cursor
							.getString(ColumnQuery.ICON_NORMAL);
					Columns[i].IconFocusedPath = cursor
							.getString(ColumnQuery.ICON_FOCUSED);

					i++;
				} while (cursor.moveToNext());
			}
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		for (int i = 0; Columns != null && i < Columns.length; i++) {
			Columns[i].Name = getColumnName(Columns[i].Id);
		}

		return Columns;
	}

	public String getColumnName(String columnId) {
		String columnName = "";
		String selection = ResStr.OBJECTNAME + "=? and " + ResStr.ENTITYID
				+ "=? and " + ResStr.STRLANG + "=?";
		String[] selectionArgs = { GDDVBDataContract.COLUMNTABLE, columnId,
				getLocalization() };

		Cursor cursor = mDVBDataProvider.query(ResStr.CONTENT_URI,
				ResStrQuery.COLUMNS, selection, selectionArgs, null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				columnName = cursor.getString(ResStrQuery.STRVALUE);
			}
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return columnName;
	}

	public String getPublicationResStr(String publicationId, String objectName) {
		String resStr = "";
		String selection = ResStr.OBJECTNAME + "=? and " + ResStr.ENTITYID
				+ "=? and " + ResStr.STRLANG + "=?";
		String[] selectionArgs = { objectName, publicationId, getLocalization() };

		Cursor cursor = mDVBDataProvider.query(ResStr.CONTENT_URI,
				ResStrQuery.COLUMNS, selection, selectionArgs, null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				resStr = cursor.getString(ResStrQuery.STRVALUE);
			}
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return resStr;
	}

	public ContentData[] getReadyPublications(String columnId) {

		EntityObject[] entities = getAllEntities(columnId);
		if (entities == null || entities.length == 0)
			return null;

		ArrayList<ContentData> contents = new ArrayList<ContentData>();

		for (int i = 0; i < entities.length; i++) {
			PublicationData data = getPublication(entities[i].Id);
			if (data != null) {
				ContentData content = new ContentData();
				content.XMLFilePath = data.URI;
				content.Id = data.PublicationID;

				contents.add(content);
			}
		}

		return (ContentData[]) contents
				.toArray(new ContentData[contents.size()]);
	}

	public EntityObject[] getAllEntities(String columnId) {
		EntityObject[] entities = null;

		String selection = ColumnEntity.COLUMNID + "=?";
		String[] selectionArgs = new String[] { columnId };

		Cursor cursor = mDVBDataProvider.query(ColumnEntity.CONTENT_URI,
				ColumnEntityQuery.COLUMNS, selection, selectionArgs, null);
		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				entities = new EntityObject[cursor.getCount()];
				int i = 0;
				do {
					EntityObject entity = new EntityObject();
					entity.Id = cursor.getString(ColumnEntityQuery.ENTITYID);
					entity.Type = cursor
							.getString(ColumnEntityQuery.ENTITYTYPE);
					entities[i] = entity;
					i++;
				} while (cursor.moveToNext());
			}
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return entities;
	}

	private final static String[] ProjectionQueryPublicationsCount = { Publication.URI };
	private final static int PublicationURI = 0;

	public PublicationData getPublication(String entityId) {

		PublicationData publication = null;
		Cursor cursor = null;

		String selection = Publication.PUBLICATIONID + "=?  AND ("
				+ Publication.RECEIVESTATUS + "=? Or "
				+ Publication.RECEIVESTATUS + "=?) AND " + Publication.VISIBLE
				+ "=?";
		String[] selectionArgs = new String[] { entityId, "1", "2", "true" };

		// cursor = mContext.getContentResolver().query(Publication.CONTENT_URI,
		cursor = mDVBDataProvider.query(Publication.CONTENT_URI,
				ProjectionQueryPublicationsCount, selection, selectionArgs,
				null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				// Log.d(TAG, "query cursor size = " + cursor.getCount());
				publication = new PublicationData();
				publication.PublicationID = entityId;
				publication.URI = cursor.getString(PublicationURI);
			}
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return publication;
	}

	public TV getTVData(String entityId) {
		TV tv = new TV();

		tv.Content = new ContentData();
		tv.Content.Id = entityId;
		tv.Content.Name = getPublicationSetName(entityId);
		tv.Content.Description = getPublicationSetDescription(entityId);
		tv.Content.Posters = new ArrayList<ContentData.Poster>();
		String posterUri = getPublicationPoster(entityId);
		ContentData.Poster item = new ContentData.Poster();
		item.URI = posterUri;
		tv.Content.Posters.add(item);
		getEpisodes(entityId, tv);

		return tv;
	}

	private static final String PublicationSetPropertyName = GDDVBDataContract.ObjectSetName;
	private static final String PublicationSetPropertyDescription = GDDVBDataContract.ObjectSetDesc;

	private static final String QuerySetPropertyProjection[] = { ResStr.STRVALUE };

	public String getPublicationSetName(String setId) {
		return getPublicationSetProperty(setId, PublicationSetPropertyName);
	}

	public String getPublicationSetDescription(String setId) {
		return getPublicationSetProperty(setId,
				PublicationSetPropertyDescription);
	}

	public String getPublicationSetProperty(String setId, String propertyName) {
		String propertyValue = "";

		String selection = ResStr.OBJECTNAME + "=? AND " + ResStr.ENTITYID
				+ "=? AND " + ResStr.STRLANG + "=? AND " + ResStr.STRNAME
				+ "=?";

		String[] selectionArgs = new String[] { GDDVBDataContract.ObjectPublicationSet, setId,
				GDCommon.LangCN, propertyName };

		Cursor cursor = mDVBDataProvider.query(ResStr.CONTENT_URI,
				QuerySetPropertyProjection, selection, selectionArgs, null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				propertyValue = cursor.getString(0);
			}
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return propertyValue;
	}

	private static final String QueryPosterProjection[] = { ResPoster.POSTERURI };

	public String getPublicationPoster(String setId) {
		String uri = "";

		String selection = ResPoster.OBJECTNAME + "=? AND "
				+ ResPoster.ENTITYID + "=?";
		String[] selectionArgs = new String[] {
				GDDVBDataContract.ObjectPublicationSet, setId };

		Cursor cursor = mDVBDataProvider.query(ResPoster.CONTENT_URI,
				QueryPosterProjection, selection, selectionArgs, null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				uri = cursor.getString(0);
			}
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return uri;
	}

	private final static String[] ProjectionQueryEpisodes = { Publication.URI,
			Publication.INDEXINSET };
	private final static int EpisodesURI = 0;
	private final static int EpisodesIndexInSet = 1;

	public void getEpisodes(String setId, TV tv) {
		String selection = "(" + Publication.RECEIVESTATUS + "=? or "
				+ Publication.RECEIVESTATUS + "=?) And " + Publication.VISIBLE
				+ "=? AND " + Publication.SETID + "=?";
		String[] selectionArgs = new String[] { "1", "2", "true", setId };

		Cursor cursor = mDVBDataProvider.query(Publication.CONTENT_URI,
				ProjectionQueryEpisodes, selection, selectionArgs, null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				tv.Episodes = new TV.EpisodeItem[cursor.getCount()];

				int i = 0;
				do {
					TV.EpisodeItem item = new TV.EpisodeItem();
					item.Url = cursor.getString(EpisodesURI);
					item.Number = Integer.valueOf(cursor
							.getString(EpisodesIndexInSet));
					tv.Episodes[i] = item;
					i++;
				} while (cursor.moveToNext());
			}
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

	}

	private final static String[] ProjectionQueryContentCount = { "count(*)" };

	private final static String[] ProjectionQueryContent = { Content.ID,
			Content.PATH };

	private final static int QUERYCONTENT_ID = 0;
	private final static int QUERYCONTENT_PATH = 1;

	private final static String[] ProjectionQueryBrand = { Brand.ID,
			Brand.DOWNLOAD, Brand.TOTALSIZE, Brand.CNAME };

	public int getContentsCount(String columnId) {
		int count = 0;
		String selection = Content.COLUMN_ID + "=?  AND " + Content.READY
				+ "=1";
		String[] selectionArgs = new String[] { columnId };

		Cursor cursor = mDVBDataProvider.query(Content.CONTENT_URI,
				ProjectionQueryContentCount, selection, selectionArgs, null);
		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				count = cursor.getInt(0);
				Log.d(TAG, "count = " + count);
			}
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return count;
	}

	// Query Guide list
	private static final String[] ProjectionQueryGuideList = { GuideList.PUBLICATIONID };
	private static final int QUERYGUIDELIST_PUBLICATIONID = 0;

	private GuideListItem[] getGuideList(String selection,
			String[] selectionArgs) {

		Log.d(TAG, "getGuideList");

		GuideListItem[] items = null;

		Cursor cursor = mDVBDataProvider.query(GuideList.CONTENT_URI,
				ProjectionQueryGuideList, selection, selectionArgs, null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				Log.d(TAG, "query cursor size = " + cursor.getCount());
				items = new GuideListItem[cursor.getCount()];
				int i = 0;
				do {
					GuideListItem item = new GuideListItem();
					item.PublicationID = cursor
							.getString(QUERYGUIDELIST_PUBLICATIONID);
					items[i] = item;
					i++;
				} while (cursor.moveToNext());
			}
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		if (items != null && items.length > 0) {

			for (int i = 0; i < items.length; i++) {
				items[i].Name = getPublicationResStr(items[i].PublicationID,
						GDDVBDataContract.PUBLICATIONTABLE);
			}
		}

		return items;
	}

	public GuideListItem[] getGuideList(String Date) {
		String selection = GuideList.DATEVALUE + "=?";
		String[] selectionArgs = { Date };

		return getGuideList(selection, selectionArgs);
	}

	public GuideListItem[] getGuideList() {
		return getGuideList(null, null);
	}
	
	public boolean updateGuideList(GuideListItem[] item) {
		boolean result = true;
		
		// update
		String selection = "";
		String[] selectionArgs = new String[] {  };

		ContentValues values = new ContentValues();
//		values.put(GuideList.);

		int count = mDVBDataProvider.update(
				GuideList.CONTENT_URI, values, selection,
				selectionArgs);
		if (count == item.length)
			result = true;
		
		return result;
	}

	private static final String[] ProjectionQueryPreview = {
			Preview.PREVIEWTYPE, Preview.SHOWTIME, Preview.PREVIEWURI,
			Preview.DURATION, Preview.PLAYMODE };

	private static final int QUERYPREVIEW_PREVIEWTYPE = 0;
	private static final int QUERYPREVIEW_SHOWTIME = 1;
	private static final int QUERYPREVIEW_PREVIEWURI = 2;
	private static final int QUERYPREVIEW_DURATION = 3;
	private static final int QUERYPREVIEW_PLAYMODE = 4;

	public PreviewData[] getPreviews() {
		PreviewData[] items = null;

		Cursor cursor = mDVBDataProvider.query(Preview.CONTENT_URI,
				ProjectionQueryPreview, null, null, null);
		
		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				items = new PreviewData[cursor.getCount()];
				int i = 0;
				do {
					PreviewData item = new PreviewData();
					item.Type = cursor.getString(QUERYPREVIEW_PREVIEWTYPE);
					item.URI = cursor.getString(QUERYPREVIEW_PREVIEWURI);
					
					item.ShowTime = Integer.valueOf(cursor.getString(QUERYPREVIEW_SHOWTIME));
					item.Duration = Integer.valueOf(cursor.getString(QUERYPREVIEW_DURATION));
					item.PlayMode = Integer.valueOf(cursor.getString(QUERYPREVIEW_PLAYMODE));
					items[i] = item;
					i++;
				} while (cursor.moveToNext());
			}
		}
		
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		
		return items;
	}

	private final static String[] ProjectionQueryGlobal = { Global.ID,
			Global.NAME, Global.VALUE };

	private final static int QUERYGLOBAL_ID = 0;
	private final static int QUERYGLOBAL_NAME = 1;
	private final static int QUERYGLOBAL_VALUE = 2;

	public boolean setGuodianServerIP(String ip) {
		return setSettingValue(GDSettings.SettingServerIP, ip);
	}

	public boolean setGuodianServerPort(String port) {
		return setSettingValue(GDSettings.SettingServerPort, port);
	}

	public boolean setGuodianSerialNumber(String serialNumber) {
		return setSettingValue(GDSettings.SettingSerialNumber, serialNumber);
	}

	public boolean setGuodianUserName(String userName) {
		return setSettingValue(GDSettings.SettingUserName, userName);
	}

	public boolean setGuodianPassword(String passwd) {
		return setSettingValue(GDSettings.SettingPasswrod, passwd);
	}

	public boolean setGuodianVersion(String version) {
		return setSettingValue(GDSettings.SettingVersion, version);
	}

	public boolean setSettingValue(String key, String value) {

		boolean ret = true;
		int Id = -1;
		String oldValue = "";
		String selection = Global.NAME + "=?";
		String[] selectionArgs = new String[] { key };

		Cursor cursor = mSmartHomeProvider.query(Global.CONTENT_URI,
				ProjectionQueryGlobal, selection, selectionArgs, null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				Log.d(TAG, "query cursor size = " + cursor.getCount());

				do {
					Id = cursor.getInt(QUERYGLOBAL_ID);
					oldValue = cursor.getString(QUERYGLOBAL_VALUE);
				} while (cursor.moveToNext());
			}
		}

		if (Id < 0) {
			// insert
			ContentValues values = new ContentValues();
			values.put(Global.NAME, key);
			values.put(Global.VALUE, value);
			Uri retUri = mSmartHomeProvider.insert(Global.CONTENT_URI, values);
			/*
			 * long rowId = Long.valueOf(retUri.getLastPathSegment()); if (rowId
			 * > 0)
			 */
			if (retUri != null)
				ret = true;
		} else {
			if (!oldValue.equals(value)) {
				// update
				selection = Global.ID + "=?";
				selectionArgs = new String[] { String.valueOf(Id) };

				ContentValues values = new ContentValues();
				values.put(Global.ID, Id);
				values.put(Global.NAME, key);
				values.put(Global.VALUE, value);
				int count = mSmartHomeProvider.update(Global.CONTENT_URI,
						values, selection, selectionArgs);
				if (count == 1)
					ret = true;
			}
		}

		return ret;
	}

	public String getSettingValue(String key) {
		Cursor cursor = null;
		String value = "";
		String selection = Global.NAME + "=?";
		String[] selectionArgs = new String[] { key };

		cursor = mSmartHomeProvider.query(Global.CONTENT_URI,
				ProjectionQueryGlobal, selection, selectionArgs, null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				Log.d(TAG, "query cursor size = " + cursor.getCount());

				do {
					value = cursor.getString(QUERYGLOBAL_VALUE);
				} while (cursor.moveToNext());
			}

			cursor.close();
		}

		return value;
	}

	public Bitmap getImage(String file) {
		Log.d(TAG, "image =" + file);

		if (file == null || file.isEmpty())
			return null;

		Bitmap image = BitmapFactory.decodeFile(file);

		return image;
	}

	public void getDetailsData(String xmlFile, ContentData content) {

		Log.d(TAG, "getDetailsData xmlFile " + xmlFile);
		File file = new File(xmlFile);

		if (file.exists() && file.length() > 0) {
			InputStream in = null;

			try {
				in = new BufferedInputStream(new FileInputStream(file));

				GDXMLDataAccessor xmlAccessor = new GDXMLDataAccessor(
						getLocalization());
				xmlAccessor.parseDetailData(in, content);

				if (in != null) {
					in.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// user favorite
	public ContentData[] getFavoriteMovie() {
		ContentData[] contents = null;

		FavoritePublicationData[] publications = getPublicationsByType(GDCommon.ColumnTypeMovie);

		if (publications != null && publications.length > 0) {
			contents = new ContentData[publications.length];

			for (int i = 0; i < publications.length; i++) {
				FavoritePublicationData data = publications[i];
				ContentData content = new ContentData();
				content.XMLFilePath = data.URI;
				content.Id = data.PublicationID;
				contents[i] = content;
			}

		}

		return contents;
	}

	public TV[] getFavoriteTV() {
		TV[] tvs = null;
		FavoritePublicationSetData[] sets = getPublicationsSetByType(GDCommon.ColumnTypeTV);

		if (sets != null && sets.length > 0) {
			tvs = new TV[sets.length];

			for (int i = 0; i < sets.length; i++) {
				ContentData data = new ContentData();

				data.Id = sets[i].SetID;
				data.Name = sets[i].Name;
				data.Description = sets[i].Description;
				data.Posters = new LinkedList<ContentData.Poster>();
				ContentData.Poster poster = new ContentData.Poster();
				poster.URI = sets[i].PosterFile;
				data.Posters.add(poster);

				TV tv = new TV();
				tv.Content = data;
				tv.Episodes = getFavoriteEpisodes(sets[i].SetID);
			}
		}

		return tvs;
	}

	public TV.EpisodeItem[] getFavoriteEpisodes(String setId) {
		TV.EpisodeItem[] items = null;

		FavoritePublicationData[] publications = getPublicationsExByType(setId);
		if (publications != null && publications.length > 0) {
			items = new TV.EpisodeItem[publications.length];
			for (int i = 0; i < items.length; i++) {
				TV.EpisodeItem item = new TV.EpisodeItem();
				item.Url = publications[i].URI;
				item.Number = publications[i].EpisodeIndex;
			}
		}

		return items;
	}

	public ContentData[] getFavoriteRecord() {
		ContentData[] contents = null;

		FavoritePublicationSetData[] sets = getPublicationsSetByType(GDCommon.ColumnTypeRecord);

		if (sets != null && sets.length > 0) {
			contents = new ContentData[sets.length];

			for (int i = 0; i < sets.length; i++) {
				ContentData data = new ContentData();

				data.Id = sets[i].SetID;
				data.Name = sets[i].Name;
				data.Description = sets[i].Description;
				data.Posters = new LinkedList<ContentData.Poster>();
				ContentData.Poster poster = new ContentData.Poster();
				poster.URI = sets[i].PosterFile;
				data.Posters.add(poster);
			}
		}
		return contents;
	}

	public ContentData[] getFavoriteEntertainment() {
		ContentData[] contents = null;
		FavoritePublicationSetData[] sets = getPublicationsSetByType(GDCommon.ColumnTypeEntertainment);

		if (sets != null && sets.length > 0) {
			contents = new ContentData[sets.length];

			for (int i = 0; i < sets.length; i++) {
				ContentData data = new ContentData();

				data.Id = sets[i].SetID;
				data.Name = sets[i].Name;
				data.Description = sets[i].Description;
				data.Posters = new LinkedList<ContentData.Poster>();
				ContentData.Poster poster = new ContentData.Poster();
				poster.URI = sets[i].PosterFile;
				data.Posters.add(poster);
			}
		}
		return contents;
	}

	private FavoritePublicationData[] getPublicationsByType(String columnType) {
		FavoritePublicationData[] publications = null;
		Cursor cursor = null;

		String selection = FavoritePublication.COLUMNTYPE + "=?";
		String[] selectionArgs = new String[] { columnType };

		// cursor = mContext.getContentResolver().query(Publication.CONTENT_URI,
		cursor = mUserDataProvider.query(FavoritePublication.CONTENT_URI,
				GDUserDataProvider.PublicationQuery.COLUMNS, selection,
				selectionArgs, null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				publications = new FavoritePublicationData[cursor.getCount()];
				int i = 0;
				do {
					FavoritePublicationData publication = new FavoritePublicationData();
					publication.PublicationID = cursor
							.getString(GDUserDataProvider.PublicationQuery.PUBLICATIONID);
					publication.URI = cursor
							.getString(GDUserDataProvider.PublicationQuery.URI);

					publications[i] = publication;
					i++;
				} while (cursor.moveToNext());
			}
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return publications;
	}

	private FavoritePublicationData[] getPublicationsExByType(String columnType) {
		FavoritePublicationData[] publications = null;
		Cursor cursor = null;

		String selection = FavoritePublication.COLUMNTYPE + "=?";
		String[] selectionArgs = new String[] { columnType };

		// cursor = mContext.getContentResolver().query(Publication.CONTENT_URI,
		cursor = mUserDataProvider.query(FavoritePublication.CONTENT_URI,
				GDUserDataProvider.PublicationQuery.COLUMNS, selection,
				selectionArgs, null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				publications = new FavoritePublicationData[cursor.getCount()];
				int i = 0;
				do {
					FavoritePublicationData publication = new FavoritePublicationData();
					publication.PublicationID = cursor
							.getString(GDUserDataProvider.PublicationExQuery.PUBLICATIONID);
					publication.URI = cursor
							.getString(GDUserDataProvider.PublicationExQuery.URI);

					publication.EpisodeIndex = Integer
							.valueOf(cursor
									.getString(GDUserDataProvider.PublicationExQuery.EPISODEINDEX));

					publications[i] = publication;
					i++;
				} while (cursor.moveToNext());
			}
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return publications;
	}

	class FavoritePublicationData {
		public String PublicationID;
		public String URI;
		public int EpisodeIndex;
	}

	class FavoritePublicationSetData {
		public String SetID;
		public String Name;
		public String Description;
		public String PosterFile;
		public String TrailerFile;
	}

	private FavoritePublicationSetData[] getPublicationsSetByType(
			String columnType) {
		FavoritePublicationSetData[] publicationSets = null;
		Cursor cursor = null;

		String selection = FavoritePublicationSet.COLUMNTYPE + "=?";
		String[] selectionArgs = new String[] { columnType };

		// cursor = mContext.getContentResolver().query(Publication.CONTENT_URI,
		cursor = mUserDataProvider.query(FavoritePublicationSet.CONTENT_URI,
				GDUserDataProvider.PublicationSetQuery.COLUMNS, selection,
				selectionArgs, null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				publicationSets = new FavoritePublicationSetData[cursor
						.getCount()];
				int i = 0;
				do {
					FavoritePublicationSetData set = new FavoritePublicationSetData();
					set.SetID = cursor
							.getString(GDUserDataProvider.PublicationSetQuery.SETID);
					set.Name = cursor
							.getString(GDUserDataProvider.PublicationSetQuery.NAME);
					set.Description = cursor
							.getString(GDUserDataProvider.PublicationSetQuery.DESCRIPTION);
					set.PosterFile = cursor
							.getString(GDUserDataProvider.PublicationSetQuery.POSTER);
					set.TrailerFile = cursor
							.getString(GDUserDataProvider.PublicationSetQuery.TRAILER);

					publicationSets[i] = set;
					i++;
				} while (cursor.moveToNext());
			}
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return publicationSets;
	}

	// save to favorite
	public void addMeidaToFavorite(MediaData mediaData) {
		if (mediaData == null)
			return;

		if (mediaData.ColumnType.equals(GDCommon.ColumnTypeMovie)) {
			addToFavoritePublication(mediaData);
		} else if (mediaData.ColumnType.equals(GDCommon.ColumnTypeTV)) {
			addToFavoritePublicationSet(mediaData);
			addToFavoritePublicationEx(mediaData);
		}
	}

	public boolean addToFavoritePublication(MediaData mediaData) {
		boolean result = false;

		String columnType = mediaData.ColumnType;
		String publicationId = mediaData.PublicationID;
		String uri = mediaData.URI;

		String[] projection = GDUserDataProvider.PublicationQuery.COLUMNS;
		String selection = FavoritePublication.COLUMNTYPE + "=? AND "
				+ FavoritePublication.PUBLICATIONID + "=?";
		String[] selectionArgs = { columnType, publicationId };
		Cursor cursor = mUserDataProvider.query(
				FavoritePublication.CONTENT_URI, projection, selection,
				selectionArgs, null);

		String oldURI = "";
		int id = -1;
		if (cursor != null && cursor.getCount() > 0) {
			id = cursor.getInt(GDUserDataProvider.PublicationQuery.ID);
			oldURI = cursor.getString(GDUserDataProvider.PublicationQuery.URI);
		}

		if (id < 0) {
			// insert
			ContentValues values = new ContentValues();
			values.put(FavoritePublication.COLUMNTYPE, columnType);
			values.put(FavoritePublication.PUBLICATIONID, publicationId);
			values.put(FavoritePublication.URI, uri);
			Uri retUri = mUserDataProvider.insert(
					FavoritePublication.CONTENT_URI, values);

			if (retUri != null)
				result = true;
		} else {
			if (oldURI == null || !oldURI.equals(uri)) {
				// update
				selection = FavoritePublication.ID + "=?";
				selectionArgs = new String[] { String.valueOf(id) };

				ContentValues values = new ContentValues();
				values.put(FavoritePublication.COLUMNTYPE, columnType);
				values.put(FavoritePublication.PUBLICATIONID, publicationId);
				values.put(FavoritePublication.URI, uri);
				int count = mUserDataProvider.update(
						FavoritePublication.CONTENT_URI, values, selection,
						selectionArgs);
				if (count == 1)
					result = true;
			} else {
				result = true;
			}
		}

		return result;
	}

	public boolean addToFavoritePublicationEx(MediaData mediaData) {
		boolean result = false;

		String columnType = mediaData.ColumnType;
		String publicationId = mediaData.PublicationID;
		String uri = mediaData.URI;
		int episodeIndex = mediaData.EpisodeIndex;

		String[] projection = GDUserDataProvider.PublicationExQuery.COLUMNS;
		String selection = FavoritePublication.COLUMNTYPE + "=? AND "
				+ FavoritePublication.PUBLICATIONID + "=?";
		String[] selectionArgs = { columnType, publicationId };
		Cursor cursor = mUserDataProvider.query(
				FavoritePublication.CONTENT_URI, projection, selection,
				selectionArgs, null);

		String oldURI = "";
		int oldIndex = -1;
		int id = -1;
		if (cursor != null && cursor.getCount() > 0) {
			id = cursor.getInt(GDUserDataProvider.PublicationExQuery.ID);
			oldURI = cursor
					.getString(GDUserDataProvider.PublicationExQuery.URI);
			oldIndex = cursor
					.getInt(GDUserDataProvider.PublicationExQuery.EPISODEINDEX);
		}

		if (id < 0) {
			// insert
			ContentValues values = new ContentValues();
			values.put(FavoritePublication.COLUMNTYPE, columnType);
			values.put(FavoritePublication.PUBLICATIONID, publicationId);
			values.put(FavoritePublication.URI, uri);
			values.put(FavoritePublication.EPISODEINDEX, episodeIndex);
			Uri retUri = mUserDataProvider.insert(
					FavoritePublication.CONTENT_URI, values);

			if (retUri != null)
				result = true;
		} else {
			if (oldURI == null || !oldURI.equals(uri)
					|| oldIndex != episodeIndex) {
				// update
				selection = FavoritePublication.ID + "=?";
				selectionArgs = new String[] { String.valueOf(id) };

				ContentValues values = new ContentValues();
				values.put(FavoritePublication.COLUMNTYPE, columnType);
				values.put(FavoritePublication.PUBLICATIONID, publicationId);
				values.put(FavoritePublication.URI, uri);
				values.put(FavoritePublication.EPISODEINDEX, episodeIndex);
				int count = mUserDataProvider.update(
						FavoritePublication.CONTENT_URI, values, selection,
						selectionArgs);
				if (count == 1)
					result = true;
			} else {
				result = true;
			}
		}

		return result;
	}

	public boolean addToFavoritePublicationSet(MediaData mediaData) {
		boolean result = false;

		String setId = mediaData.SetID;
		String name = mediaData.Name;
		String description = mediaData.Description;
		String poster = mediaData.Poster;
		String trailer = mediaData.Trailer;

		String[] projection = GDUserDataProvider.PublicationSetQuery.COLUMNS;
		String selection = FavoritePublicationSet.SETID + "=?";
		String[] selectionArgs = { setId };
		Cursor cursor = mUserDataProvider.query(
				FavoritePublicationSet.CONTENT_URI, projection, selection,
				selectionArgs, null);

		int id = -1;
		String oldName = "";
		if (cursor != null && cursor.getCount() > 0) {
			id = cursor.getInt(GDUserDataProvider.PublicationSetQuery.ID);
			oldName = cursor
					.getString(GDUserDataProvider.PublicationSetQuery.NAME);
		}

		if (id < 0) {
			// insert
			ContentValues values = new ContentValues();
			values.put(FavoritePublicationSet.SETID, setId);
			values.put(FavoritePublicationSet.NAME, name);
			values.put(FavoritePublicationSet.DESCRIPTION, description);
			values.put(FavoritePublicationSet.POSTER, poster);
			values.put(FavoritePublicationSet.TRAILER, trailer);
			Uri retUri = mUserDataProvider.insert(
					FavoritePublicationSet.CONTENT_URI, values);

			if (retUri != null)
				result = true;
		} else {
			if (oldName == null || !oldName.equals(name)) {
				// update
				selection = FavoritePublicationSet.ID + "=?";
				selectionArgs = new String[] { String.valueOf(id) };

				ContentValues values = new ContentValues();
				values.put(FavoritePublicationSet.SETID, setId);
				values.put(FavoritePublicationSet.NAME, name);
				values.put(FavoritePublicationSet.DESCRIPTION, description);
				values.put(FavoritePublicationSet.POSTER, poster);
				values.put(FavoritePublicationSet.TRAILER, trailer);
				int count = mUserDataProvider.update(
						FavoritePublication.CONTENT_URI, values, selection,
						selectionArgs);
				if (count == 1)
					result = true;
			} else {
				result = true;
			}
		}

		return result;
	}

	private final static int QUERYBRAND_ID = 0;
	private final static int QUERYBRAND_DOWNLOAD = 1;
	private final static int QUERYBRAND_TOTALSIZE = 2;
	private final static int QUERYBRAND_CNAME = 3;

	public ContentData[] getContents(String columnId, int pageNumber,
			int pageSize) {

		ContentData[] Contents = null;

		String selection = Content.COLUMN_ID + "=?  AND " + Content.READY
				+ "=1" + " Limit ? Offset ?";
		String[] selectionArgs = new String[] { columnId,
				Integer.toString(pageSize),
				Integer.toString(pageNumber * pageSize) };

		Cursor cursor = mDVBDataProvider.query(Content.CONTENT_URI,
				ProjectionQueryContent, selection, selectionArgs, null);
		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				Log.d(TAG, "query cursor size = " + cursor.getCount());
				Contents = new ContentData[cursor.getCount()];
				int i = 0;
				do {
					ContentData content = new ContentData();
					content.Id = cursor.getString(QUERYCONTENT_ID);
					content.XMLFilePath = cursor.getString(QUERYCONTENT_PATH);

					Log.d(TAG, "coloumn " + columnId + " item " + i + " name="
							+ content.XMLFilePath);

					Contents[i] = content;
					i++;
				} while (cursor.moveToNext());
			}
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return Contents;
	}

	public ReceiveEntry[] getDownloadStatus(int pageNumber, int pageSize) {

		ReceiveEntry[] Entries = null;

		String sortOrder = Brand.ID + " Limit " + Integer.toString(pageSize)
				+ " Offset " + Integer.toString(pageNumber * pageSize);

		Cursor cursor = mSmartHomeProvider.query(Brand.CONTENT_URI,
				ProjectionQueryBrand, null, null, sortOrder);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				Log.d(TAG, "query cursor size = " + cursor.getCount());
				Entries = new ReceiveEntry[cursor.getCount()];
				int i = 0;
				do {
					ReceiveEntry entry = new ReceiveEntry();
					entry.Id = cursor.getString(QUERYBRAND_ID);
					entry.Name = cursor.getString(QUERYBRAND_CNAME);
					entry.RawProgress = cursor.getLong(QUERYBRAND_DOWNLOAD);
					entry.RawTotal = cursor.getLong(QUERYBRAND_TOTALSIZE);
					entry.ConverSize();

					Log.d(TAG, "Name " + entry.Name + " item " + i
							+ " progress=" + entry.RawProgress + " total="
							+ entry.RawTotal);

					Entries[i] = entry;
					i++;
				} while (cursor.moveToNext());
			}
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return Entries;
	}

}
