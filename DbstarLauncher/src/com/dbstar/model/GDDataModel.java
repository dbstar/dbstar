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

import com.dbstar.model.GDDVBDataContract.*;
import com.dbstar.model.GDDVBDataProvider.ColumnEntityQuery;
import com.dbstar.model.GDDVBDataProvider.ColumnQuery;
import com.dbstar.model.GDDVBDataProvider.ResStrQuery;

public class GDDataModel {
	private static final String TAG = "GDDataModel";

	private String mLocalization;

	GDDVBDataProvider mDVBDataProvider;
	GDSmartHomeProvider mSmartHomeProvider;
	GDUserDataProvider mUserDataProvider;

	public GDDataModel() {
		mDVBDataProvider = new GDDVBDataProvider();
		mSmartHomeProvider = new GDSmartHomeProvider();
		// mUserDataProvider = new GDUserDataProvider();
	}

	public void initialize(GDSystemConfigure configure) {
		setLocalization(configure.getLocalization());
		mDVBDataProvider.initialize(configure);
		mSmartHomeProvider.initialize(configure);
		// mUserDataProvider.initialize(configure);
	}

	public void deInitialize() {
		mDVBDataProvider.deinitialize();
		mSmartHomeProvider.deinitialize();
		// mUserDataProvider.deinitialize();
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

	public ContentData[] getReadyPublications(String columnId, String favorite) {

		EntityObject[] entities = getAllEntities(columnId);
		if (entities == null || entities.length == 0)
			return null;

		ArrayList<ContentData> contents = new ArrayList<ContentData>();

		for (int i = 0; i < entities.length; i++) {
			PublicationData data = getPublication(entities[i].Id, favorite);
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

	public ContentData[] getReadyPublicationSet(String columnId, String favorite) {
		EntityObject[] entities = getAllEntities(columnId);
		if (entities == null || entities.length == 0)
			return null;

		ArrayList<ContentData> contents = new ArrayList<ContentData>();

		for (int i = 0; i < entities.length; i++) {
			String entityId = entities[i].Id;
			PublicationData data = getPublicationsSet(entityId, favorite);

			if (data != null) {
				ContentData content = new ContentData();
				content.XMLFilePath = data.URI;
				content.Id = data.PublicationID;

				content.Name = getPublicationSetName(entityId);
				content.Description = getPublicationSetDescription(entityId);
				String posterUri = getPublicationPoster(entityId);
				ContentData.Poster item = new ContentData.Poster();
				item.URI = posterUri;
				content.Posters = new ArrayList<ContentData.Poster>();
				content.Posters.add(item);

				contents.add(content);
			}
		}

		return (ContentData[]) contents
				.toArray(new ContentData[contents.size()]);
	}

	private final static String[] ProjectionQueryPublications = { Publication.URI };
	private final static int PublicationURI = 0;

	public PublicationData getPublication(String entityId, String favorite) {

		PublicationData publication = null;

		String selection = Publication.PUBLICATIONID + "=?  AND ("
				+ Publication.RECEIVESTATUS + "=? Or "
				+ Publication.RECEIVESTATUS + "=?) AND " + Publication.VISIBLE
				+ "=? AND (" + Publication.DELETED + "=? OR "
				+ Publication.DELETED + " is null OR " + Publication.DELETED
				+ "=?)";

		if (favorite != null && !favorite.isEmpty()) {
			selection += " AND " + PublicationsSet.FAVORITE + "=" + favorite;
		}

		String[] selectionArgs = new String[] { entityId, "1", "2", "true",
				"0", "" };

		Cursor cursor = mDVBDataProvider.query(Publication.CONTENT_URI,
				ProjectionQueryPublications, selection, selectionArgs, null);

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

	private final static String[] ProjectionQueryEpisodes = {
			Publication.PUBLICATIONID, Publication.URI, Publication.INDEXINSET };
	private final static int EpisodesID = 0;
	private final static int EpisodesURI = 1;
	private final static int EpisodesIndexInSet = 2;

	public ContentData[] getPublicationsEx(String setId, String favorite) {
		ContentData[] contents = null;

		String selection = Publication.SETID + "=? AND ("
				+ Publication.RECEIVESTATUS + "=? or "
				+ Publication.RECEIVESTATUS + "=?) And " + Publication.VISIBLE
				+ "=? AND (" + Publication.DELETED + "=? OR "
				+ Publication.DELETED + " is null OR " + Publication.DELETED
				+ "=?)";

		if (favorite != null && !favorite.isEmpty()) {
			selection += " AND " + PublicationsSet.FAVORITE + "=" + favorite;
		}

		String[] selectionArgs = new String[] { setId, "1", "2", "true", "0",
				"" };

		Cursor cursor = mDVBDataProvider.query(Publication.CONTENT_URI,
				ProjectionQueryEpisodes, selection, selectionArgs, null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				contents = new ContentData[cursor.getCount()];
				int i = 0;
				do {
					ContentData content = new ContentData();
					content.Id = cursor.getString(EpisodesID);
					content.XMLFilePath = cursor.getString(EpisodesURI);
					content.IndexInSet = Integer.valueOf(cursor
							.getString(EpisodesIndexInSet));
					contents[i] = content;
					i++;
				} while (cursor.moveToNext());
			}
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return contents;
	}

	private final static String[] ProjectionQueryPublicationsSet = { PublicationsSet.URI };
	private final static int PublicationsSetURI = 0;

	public PublicationData getPublicationsSet(String entityId, String favorite) {

		PublicationData publication = null;

		String selection = PublicationsSet.SETID + "=?  AND ("
				+ PublicationsSet.RECEIVESTATUS + "=? Or "
				+ PublicationsSet.RECEIVESTATUS + "=?)";
		// + PublicationsSet.RECEIVESTATUS + "=?) AND "
		// + PublicationsSet.VISIBLE + "=? AND (" + Publication.DELETED +
		// "=? OR " + Publication.DELETED + " is null OR " + Publication.DELETED
		// + "=?)";
		if (favorite != null && !favorite.isEmpty()) {
			selection += " AND " + PublicationsSet.FAVORITE + "=" + favorite;
		}

		String[] selectionArgs = new String[] { entityId, "1", "2" };

		// String[] selectionArgs = new String[] { entityId, "1", "2", "true",
		// "0", "" };

		Cursor cursor = mDVBDataProvider.query(PublicationsSet.CONTENT_URI,
				ProjectionQueryPublicationsSet, selection, selectionArgs, null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				publication = new PublicationData();
				publication.PublicationID = entityId;
				publication.URI = cursor.getString(PublicationsSetURI);
			}
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return publication;
	}

	public String getPublicationResStr(String publicationId, String objectName,
			String property) {
		String resStr = "";
		String selection = ResStr.OBJECTNAME + "=? and " + ResStr.ENTITYID
				+ "=? and " + ResStr.STRLANG + "=? AND " + ResStr.STRNAME
				+ "=?";
		String[] selectionArgs = { objectName, publicationId,
				getLocalization(), property };

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

		String[] selectionArgs = new String[] {
				GDDVBDataContract.ObjectPublicationSet, setId, GDCommon.LangCN,
				propertyName };

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
	private static final String[] ProjectionQueryGuideList = {
			GuideList.DATEVALUE, GuideList.GUIDELISTID,
			GuideList.PUBLICATIONID, GuideList.USERSTATUS };
	private static final int QUERYGUIDELIST_DATEVALUE = 0;
	private static final int QUERYGUIDELIST_GUIDELISTID = 1;
	private static final int QUERYGUIDELIST_PUBLICATIONID = 2;
	private static final int QUERYGUIDELIST_USERSTATUS = 3;

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
					item.Date = cursor.getString(QUERYGUIDELIST_DATEVALUE);
					item.GuideListID = cursor
							.getString(QUERYGUIDELIST_GUIDELISTID);
					item.PublicationID = cursor
							.getString(QUERYGUIDELIST_PUBLICATIONID);
					int status = cursor.getInt(QUERYGUIDELIST_USERSTATUS);
					item.isSelected = item.originalSelected = status == 0 ? false
							: true;
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
				items[i].Name = getPublicationResStr(items[i].GuideListID,
						GDDVBDataContract.GUIDELISTTABLE,
						GDDVBDataContract.ValuePublicationName);
				items[i].ColumnType = getPublicationResStr(
						items[i].GuideListID, GDDVBDataContract.GUIDELISTTABLE,
						GDDVBDataContract.ValueColumnName);
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

	public boolean updateGuideList(GuideListItem[] items) {
		boolean result = true;

		String sql = "UPDATE " + GDDVBDataContract.GUIDELISTTABLE + " SET "
				+ GuideList.USERSTATUS + "=? WHERE " + GuideList.DATEVALUE
				+ "=? AND " + GuideList.GUIDELISTID + "=? AND "
				+ GuideList.PUBLICATIONID + "=?";

		String[][] bindArgs = new String[items.length][];
		for (int i = 0; i < items.length; i++) {
			String[] args = new String[4];
			args[0] = items[i].isSelected ? "1" : "0";
			args[1] = items[i].Date;
			args[2] = items[i].GuideListID;
			args[3] = items[i].PublicationID;
			bindArgs[i] = args;
		}

		result = mDVBDataProvider.execBatchSql(sql, bindArgs);

		return result;
	}

	// Global property query
	public String getPreviewPath() {
		return queryGlobalProperty(GDDVBDataContract.PropertyPreviewPath);
	}
	
	public String getPushDir() {
		return queryGlobalProperty(GDDVBDataContract.PropertyPushDir);
	}
	
	public String getColumnResDir() {
		return queryGlobalProperty(GDDVBDataContract.PropertyColumnResPath);
	}
	
	private String queryGlobalProperty(String property) {
		String value = null;
		String selection = GDDVBDataContract.Global.NAME + "=?";
		String[] selectionArgs = { property };
		Cursor cursor = mDVBDataProvider.query(
				GDDVBDataContract.Global.CONTENT_URI,
				GDDVBDataProvider.GlobalQuery.COLUMNS, selection,
				selectionArgs, null);
		
		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				value = cursor.getString(GDDVBDataProvider.GlobalQuery.VALUE);
			}
		}
		
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return value;
	}
	
	public boolean setPushDir (String pushDir) {
		return updateGlobalProperty(GDDVBDataContract.PropertyPushDir, pushDir);
	}
	
	private boolean updateGlobalProperty(String property, String value) {
		String selection = GDDVBDataContract.Global.NAME + "=?";
		String[] selectionArgs = { property };
		Cursor cursor = mDVBDataProvider.query(
				GDDVBDataContract.Global.CONTENT_URI,
				GDDVBDataProvider.GlobalQuery.COLUMNS, selection,
				selectionArgs, null);
		
		boolean ret = true;
		int Id = -1;
		String oldValue = "";
		
		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				Id = 0;
				oldValue = cursor
						.getString(GDDVBDataProvider.GlobalQuery.VALUE);
			}
		}
		
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		if (Id < 0) {
			// insert
			ContentValues values = new ContentValues();
			values.put(GDDVBDataContract.Global.NAME, property);
			values.put(GDDVBDataContract.Global.VALUE, value);
			Uri retUri = mDVBDataProvider.insert(GDDVBDataContract.Global.CONTENT_URI, values);
			/*
			 * long rowId = Long.valueOf(retUri.getLastPathSegment()); if (rowId
			 * > 0)
			 */
			if (retUri != null)
				ret = true;
		} else {
			if (!oldValue.equals(value)) {
				// update
				ContentValues values = new ContentValues();
				values.put(GDDVBDataContract.Global.VALUE, value);
				int count = mDVBDataProvider.update(GDDVBDataContract.Global.CONTENT_URI,
						values, selection, selectionArgs);
				if (count == 1)
					ret = true;
			}
		}

		return ret;
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

					item.ShowTime = Integer.valueOf(cursor
							.getString(QUERYPREVIEW_SHOWTIME));
					item.Duration = Integer.valueOf(cursor
							.getString(QUERYPREVIEW_DURATION));
					item.PlayMode = Integer.valueOf(cursor
							.getString(QUERYPREVIEW_PLAYMODE));
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

	// private final static String[] ProjectionQueryGlobal = { Global.ID,
	// Global.NAME, Global.VALUE };
	//
	// private final static int QUERYGLOBAL_ID = 0;
	// private final static int QUERYGLOBAL_NAME = 1;
	// private final static int QUERYGLOBAL_VALUE = 2;

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
		String selection = GDSmartHomeContract.Global.NAME + "=?";
		String[] selectionArgs = new String[] { key };

		Cursor cursor = mSmartHomeProvider.query(Global.CONTENT_URI,
				GDSmartHomeProvider.GlobalQuery.COLUMNS, selection,
				selectionArgs, null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				// Log.d(TAG, "query cursor size = " + cursor.getCount());

				do {
					Id = cursor.getInt(GDSmartHomeProvider.GlobalQuery.ID);
					oldValue = cursor
							.getString(GDSmartHomeProvider.GlobalQuery.VALUE);
				} while (cursor.moveToNext());
			}
		}
		
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
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
				selection = GDSmartHomeContract.Global.ID + "=?";
				selectionArgs = new String[] { String.valueOf(Id) };

				ContentValues values = new ContentValues();
				values.put(GDSmartHomeContract.Global.ID, Id);
				values.put(GDSmartHomeContract.Global.NAME, key);
				values.put(GDSmartHomeContract.Global.VALUE, value);
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
				GDSmartHomeProvider.GlobalQuery.COLUMNS, selection,
				selectionArgs, null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				Log.d(TAG, "query cursor size = " + cursor.getCount());

				do {
					value = cursor
							.getString(GDSmartHomeProvider.GlobalQuery.VALUE);
				} while (cursor.moveToNext());
			}
		}
		
		if (cursor != null && !cursor.isClosed()) {
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
	public boolean addPublicationToFavourite(String publicationSetId,
			String publicationId) {

		boolean result = true;
		if (publicationSetId != null && !publicationSetId.isEmpty()) {
			result = addPublicationSetToFavourite(publicationSetId);
		}

		if (result && publicationId != null && !publicationId.isEmpty()) {

			String selection = Publication.PUBLICATIONID + "=?";
			String[] selectionArgs = new String[] { publicationId };

			ContentValues values = new ContentValues();
			values.put(Publication.FAVORITE, "1");

			int count = mDVBDataProvider.update(Publication.CONTENT_URI,
					values, selection, selectionArgs);

			if (count < 1)
				result = false;
		}

		return result;
	}

	public boolean addPublicationSetToFavourite(String publicationSetId) {
		String selection = PublicationsSet.SETID + "=?";
		String[] selectionArgs = new String[] { publicationSetId };

		ContentValues values = new ContentValues();
		values.put(PublicationsSet.FAVORITE, "1");

		int count = mDVBDataProvider.update(PublicationsSet.CONTENT_URI,
				values, selection, selectionArgs);

		if (count > 0)
			return true;

		return false;
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
