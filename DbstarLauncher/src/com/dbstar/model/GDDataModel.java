package com.dbstar.model;

import android.net.Uri;
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

import com.dbstar.app.settings.GDSettings;
import com.dbstar.model.GDDVBDataContract.*;
import com.dbstar.model.GDDVBDataProvider.ColumnQuery;
import com.dbstar.model.GDDVBDataProvider.ResStrQuery;
import com.dbstar.util.LogUtil;
import com.dbstar.model.APPVersion;

public class GDDataModel {
	private static final String TAG = "GDDataModel";

	private String mLocalization;
	public static String mColumnBookId;
	public static String mColumnNewsPaperId; 

	GDDVBDataProvider mDVBDataProvider;
	GDSmartHomeProvider mSmartHomeProvider;
	GDUserDataProvider mUserDataProvider;
	
	GDSystemConfigure mConfigure;

	public GDDataModel() {
		mDVBDataProvider = new GDDVBDataProvider();
		mSmartHomeProvider = new GDSmartHomeProvider();
		// mUserDataProvider = new GDUserDataProvider();
	}

	public void initialize(GDSystemConfigure configure) {
		mDVBDataProvider.initialize(configure);
		
		if(APPVersion.GUODIAN){
			LogUtil.d(TAG, "APPVersion.GUODIAN is true, go GDSmartHomeProvider init");
			mSmartHomeProvider.initialize(configure);
		}
		else{
			LogUtil.d(TAG, "APPVersion.GUODIAN is false, do not init GDSmartHomeProvider");
		}
		// mUserDataProvider.initialize(configure);

		String language = getLanguage();
		setLocalization(language);
		configure.setLocalization(language);

		LogUtil.d(TAG, "language = " + language);
		
		mConfigure = configure;
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

		LogUtil.d(TAG, "get columns: id=" + columnId);
		Cursor cursor = null;
		ColumnData[] Columns = null;

		String selection = Column.PARENT_ID + "=?";
		String[] selectionArgs = new String[] { columnId };
		String sortOrder = Column.INDEX + " ASC";

		cursor = mDVBDataProvider.query(Column.CONTENT_URI,
				ColumnQuery.COLUMNS, selection, selectionArgs, sortOrder);
		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				LogUtil.d(TAG, "columnId: sub-column number = " + cursor.getCount());
				Columns = new ColumnData[cursor.getCount()];
				int i = 0;
				do {
					Columns[i] = new ColumnData();
					Columns[i].Id = cursor.getString(ColumnQuery.ID);
					Columns[i].Type = cursor.getString(ColumnQuery.TYPE);
					Columns[i].IconNormalPath = cursor.getString(ColumnQuery.ICON_NORMAL);
					Columns[i].IconFocusedPath = cursor.getString(ColumnQuery.ICON_FOCUSED);
					
					if (columnId.equals("-1") && Columns[i].Type.equals(GDCommon.ColumnTypeMULTIPLEMEDIABOOK)) {
						mColumnBookId = Columns[i].Id;
						LogUtil.d(TAG, "----getColumns----mColumnBookId = " + mColumnBookId);
					}
					
					if (columnId.equals("-1") && Columns[i].Type.equals(GDCommon.ColumnTypeMULTIPLEMEDIANEWSPAPER)) {
						mColumnNewsPaperId = Columns[i].Id;
						LogUtil.d(TAG, "----getColumns----mColumnNewsPaperId = " + mColumnNewsPaperId);
					}

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
				LogUtil.d(TAG, "--------columnId=" + columnId + "----columnName = " + columnName);
			}
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return columnName;
	}
	
	public String queryRichMediaColumnId(String columnType) {
//    	String sql = "select ColumnID from Column where ColumnType= ? and ParentID='-1'";
    	Cursor cursor = mDVBDataProvider.query(Column.CONTENT_URI, new String[]{"ColumnID"}, "ColumnType = ? and ParentID = '-1'", new String[]{columnType}, null);
    	if (cursor != null && cursor.moveToNext()) {
    		LogUtil.d(TAG, "--------columnType = " + columnType + "------columnID = " + cursor.getString(0));
    		return cursor.getString(0);
    	}
    	if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
    	return null;
    	
    }
	
	public int getPublicationCount(String columnId) {
		String selection = Publication.COLUMNID + "=?  AND ("
				+ Publication.RECEIVESTATUS + "=? Or "
				+ Publication.RECEIVESTATUS + "=?) AND " + Publication.VISIBLE
				+ "=? AND (" + Publication.DELETED + "=? OR "
				+ Publication.DELETED + " is null OR " + Publication.DELETED
				+ "=?) ";
		String[] selectionArgs = new String[] { columnId, "1", "2", "1", "0",
				"" };

		return queryCount(selection, selectionArgs);
	}
	
	public int getPublicationSetCount(String columnId) {
		String selection = PublicationsSet.COLUMNID + "=?  AND ("
				+ PublicationsSet.RECEIVESTATUS + "=? OR "
				+ PublicationsSet.RECEIVESTATUS + "=?) AND ("
				+ PublicationsSet.DELETED + "=? OR " + PublicationsSet.DELETED
				+ " is null OR " + PublicationsSet.DELETED + " =?) ";

		String[] selectionArgs = new String[] { columnId, "1", "2", "0", "" };

		return queryCount(selection, selectionArgs);
	}

	private final static String[] ProjectionQueryPublications = {
			Publication.PUBLICATIONID, Publication.DESCURI,
			Publication.TOTALSIZE, Publication.DRMFILE, 
			Publication.FILEID, Publication.FILESIZE, Publication.FILEURI,
			Publication.BITRATE, Publication.RESOLUTION,
			Publication.CODEFORMAT, Publication.BOOKMARK };
	private final static int PublicationID = 0;
	private final static int PublicationDescURI = PublicationID + 1;
	private final static int PublicationTotalSize = PublicationDescURI + 1;
	private final static int PublicationDrmFile = PublicationTotalSize + 1;
	private final static int PublicationFileID = PublicationDrmFile + 1;
	private final static int PublicationFileSize = PublicationFileID + 1;
	private final static int PublicationFileURI = PublicationFileSize + 1;
	private final static int PublicationBitrate = PublicationFileURI + 1;
	private final static int PublicationResolution = PublicationBitrate + 1;
	private final static int PublicationCodeFormat = PublicationResolution + 1;
	private final static int PublicationBookmark = PublicationCodeFormat + 1;

	public ContentData[] getPublications(String columnId, String favorite) {

		ContentData[] contents = null;

		String selection = Publication.COLUMNID + "=?  AND ("
				+ Publication.RECEIVESTATUS + "=? Or "
				+ Publication.RECEIVESTATUS + "=?) AND " + Publication.VISIBLE
				+ "=? AND (" + Publication.DELETED + "=? OR "
				+ Publication.DELETED + " is null OR " + Publication.DELETED
				+ "=?) ";

		if (favorite != null && !favorite.isEmpty()) {
			selection += " AND " + Publication.FAVORITE + "=" + favorite;
		}

		String[] selectionArgs = new String[] { columnId, "1", "2", "1", "0",
				"" };

		contents = queryPublications(selection, selectionArgs);

		return contents;
	}

	private ContentData[] queryPublications(String selection,
			String[] selectionArgs) {

		ContentData[] contents = null;

		Cursor cursor = mDVBDataProvider.query(Publication.CONTENT_URI,
				ProjectionQueryPublications, selection, selectionArgs, null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				contents = new ContentData[cursor.getCount()];
				int i = 0;
				do {
					ContentData content = new ContentData();
					content.Id = cursor.getString(PublicationID);
					content.XMLFilePath = cursor.getString(PublicationDescURI);
					content.TotalSize = cursor.getInt(PublicationTotalSize);
					content.DRMFile = cursor.getString(PublicationDrmFile);

					content.BookMark = cursor.getInt(PublicationBookmark);

					// Main file
					content.MainFile = new ContentData.MFile();
					content.MainFile.FileURI = cursor
							.getString(PublicationFileURI);
					content.MainFile.FileSize = cursor
							.getString(PublicationFileSize);
					content.MainFile.BitRate = cursor
							.getString(PublicationBitrate);
					content.MainFile.Resolution = cursor
							.getString(PublicationResolution);
					content.MainFile.CodeFormat = cursor
							.getString(PublicationCodeFormat);

					// Posters
					String posterUri = getPublicationPoster(
							GDDVBDataContract.ObjectPublication, content.Id);
					ContentData.Poster item = new ContentData.Poster();
					item.URI = posterUri;
					content.Posters = new ArrayList<ContentData.Poster>();
					content.Posters.add(item);

					// Subtitle
					getPublicationSubtitle(content);

					// Name
					content.Name = getPublicationResStr(content.Id,
							GDDVBDataContract.ObjectPublication,
							GDDVBDataContract.ValuePublicationName);

					contents[i] = content;
					i++;

					LogUtil.d(TAG, "content id= " + content.Id);
				} while (cursor.moveToNext());
			}
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return contents;
	}

	private String[] ProjectionQueryPublicationVAInfo = {
			MultipleLanguageInfoVA.PUBLICATIONDESC,
			MultipleLanguageInfoVA.IMAGEDEFINITION,
			MultipleLanguageInfoVA.AREA, MultipleLanguageInfoVA.DIRECTOR,
			MultipleLanguageInfoVA.ACTOR,
			MultipleLanguageInfoVA.EPISODE};

	private final static int VAInfoPublicationDesc = 0;
	private final static int VAInfoImageDefinition = 1;
	private final static int VAInfoArea = 2;
	private final static int VAInfoDirector = 3;
	private final static int VAInfoActor = 4;
	private final static int VAEpisode = 5;

	public void getPublicationVAInfo(ContentData content) {

		String selection = MultipleLanguageInfoVA.PUBLICATIONID + "=? AND "
				+ MultipleLanguageInfoVA.INFOLANG + "=?";

		String[] selectionArgs = new String[] { content.Id, getLocalization() };

		Cursor cursor = mDVBDataProvider.query(
				MultipleLanguageInfoVA.CONTENT_URI,
				ProjectionQueryPublicationVAInfo, selection, selectionArgs,
				null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				content.Description = cursor.getString(VAInfoPublicationDesc);
				content.ImageDefinition = cursor
						.getString(VAInfoImageDefinition);
				content.Area = cursor.getString(VAInfoArea);
				content.Director = cursor.getString(VAInfoDirector);
				content.Actors = cursor.getString(VAInfoActor);
				content.IndexInSet = cursor.getInt(VAEpisode);
			}
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return;
	}

	private final static String[] ProjectionQueryPublicationsSet = {
			PublicationsSet.SETID, PublicationsSet.URI };
	private final static int PublicationsSetID = 0;
	private final static int PublicationsSetURI = 1;

	public ContentData[] getPublicationSets(String columnId, String favorite) {

		ContentData[] contents = null;

		String selection = PublicationsSet.COLUMNID + "=?  AND ("
				+ PublicationsSet.RECEIVESTATUS + "=? OR "
				+ PublicationsSet.RECEIVESTATUS + "=?) AND ("
				+ PublicationsSet.DELETED + "=? OR " + PublicationsSet.DELETED
				+ " is null OR " + PublicationsSet.DELETED + " =?) ";

		if (favorite != null && !favorite.isEmpty()) {
			selection += " AND " + PublicationsSet.FAVORITE + "=" + favorite;
		}

		String[] selectionArgs = new String[] { columnId, "1", "2", "0", "" };

		Cursor cursor = mDVBDataProvider.query(PublicationsSet.CONTENT_URI,
				ProjectionQueryPublicationsSet, selection, selectionArgs, null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
//				contents = new ContentData[cursor.getCount()];
				ArrayList<ContentData> contentList = new ArrayList<ContentData>();

				do {
					ContentData content = new ContentData();
					content.Id = cursor.getString(PublicationsSetID);
					content.XMLFilePath = cursor.getString(PublicationsSetURI);

					int itemsCount = getPublicationSetItemCount(content.Id, null);
					if (itemsCount > 0) {
						getPublicationSetInfo(content);
						
						contentList.add(content);
					}
					
				} while (cursor.moveToNext());
				
				int size = contentList.size();
				if (size > 0) {
					contents =  contentList.toArray(new ContentData[size]);
				}
			}
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return contents;
	}
	
	private final static String[] ProjectionQuerySetInfo = {
		SetInfo.TITLE,  SetInfo.ACTORS, SetInfo.DESCRIPTION,
		SetInfo.TYPE, SetInfo.YEAR, SetInfo.EPISODECOUNT};
	private final static int SetTitle = 0;
	private final static int SetActors = 1;
	private final static int SetDescription = 2;
	private final static int SetType = 3;
	private final static int SetYear = 4;
	private final static int SetEpisodeCount = 5;

	private void getPublicationSetInfo(ContentData content) {
		String selection = SetInfo.SETID + "=?";
		String[] selectionArgs = new String[] { content.Id };

		Cursor cursor = mDVBDataProvider.query(SetInfo.CONTENT_URI,
				ProjectionQuerySetInfo, selection, selectionArgs, null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				content.Name = cursor.getString(SetTitle);
				content.Actors = cursor.getString(SetActors);
				content.Description = cursor.getString(SetDescription);
				content.Type = cursor.getString(SetType);
				content.Year = cursor.getString(SetYear);
				content.EpisodesCount = cursor.getInt(SetEpisodeCount);
			}
		}

		if (cursor != null) {
			cursor.close();
		}
	}

	public int getPublicationSetItemCount(String setId, String favorite) {

		int count = 0;
		String selection = Publication.SETID + "=?  AND ("
				+ Publication.RECEIVESTATUS + "=? Or "
				+ Publication.RECEIVESTATUS + "=?) AND " + Publication.VISIBLE
				+ "=? AND (" + Publication.DELETED + "=? OR "
				+ Publication.DELETED + " is null OR " + Publication.DELETED
				+ "=?)";
		String[] selectionArgs = null;

		if (favorite != null && !favorite.isEmpty()) {
			selection += " AND " + PublicationsSet.FAVORITE + "=?";
			selectionArgs = new String[] { setId, "1", "2", "1", "0", "",
					favorite };
		} else {
			selectionArgs = new String[] { setId, "1", "2", "1", "0", "" };
		}

		Cursor cursor = mDVBDataProvider.query(Publication.CONTENT_URI,
				ProjectionQueryContentCount, selection, selectionArgs, null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				count = cursor.getInt(0);
			}
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return count;
	}

	public ContentData[] getPublicationsBySetId(String setId, String favorite) {
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

		String[] selectionArgs = new String[] { setId, "1", "2", "1", "0", "" };

		contents = queryPublications(selection, selectionArgs);

		return contents;
	}

	public String getPublicationResStr(String publicationId, String objectName,
			String property) {
		String resStr = "";
		String selection = ResStr.OBJECTNAME + "=? AND " + ResStr.ENTITYID
				+ "=? AND " + ResStr.STRLANG + "=? AND " + ResStr.STRNAME
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

	public String getPublicationPoster(String objectName, String id) {
		String uri = "";

		String selection = ResPoster.OBJECTNAME + "=? AND "
				+ ResPoster.ENTITYID + "=?";
		String[] selectionArgs = new String[] { objectName, id };

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

	private static final String QuerySubtitleProjection[] = {
			ResSubTitle.SUBTITLEID, ResSubTitle.SUBTITLENAME,
			ResSubTitle.SUBTITLELANGUAGE, ResSubTitle.SUBTITLEURI };
	private static final int SubtitleId = 0;
	private static final int SubtitleName = 1;
	private static final int SubtitleLanguage = 2;
	private static final int SubtitleURI = 3;

	public void getPublicationSubtitle(ContentData content) {
		String selection = ResSubTitle.OBJECTNAME + "=? AND "
				+ ResSubTitle.ENTITYID + "=?";
		String[] selectionArgs = new String[] {
				GDDVBDataContract.ObjectPublication, content.Id };

		Cursor cursor = mDVBDataProvider.query(ResSubTitle.CONTENT_URI,
				QuerySubtitleProjection, selection, selectionArgs, null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				content.SubTitles = new ArrayList<ContentData.SubTitle>();
				do {
					ContentData.SubTitle subTitle = new ContentData.SubTitle();
					subTitle.Id = cursor.getString(SubtitleId);
					subTitle.Name = cursor.getString(SubtitleName);
					subTitle.Language = cursor.getString(SubtitleLanguage);

					String subtitleUri = cursor.getString(SubtitleURI);

					if (subtitleUri != null && !subtitleUri.isEmpty()) {
						String storageDir = mConfigure.getStorageDir();
						subtitleUri = storageDir + "/" + subtitleUri;
						LogUtil.d(TAG, "publication id =" + content.Id +" subtitle uri =  " + subtitleUri);
						File subtitleFile = new File(subtitleUri);
						if (subtitleFile.exists()) {
							subTitle.URI = subtitleUri;
							content.SubTitles.add(subTitle);
						}
					}

				} while (cursor.moveToNext());
			}
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
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

		LogUtil.d(TAG, "getGuideList");

		ArrayList<GuideListItem> items = null;

		Cursor cursor = mDVBDataProvider.query(GuideList.CONTENT_URI,
				ProjectionQueryGuideList, selection, selectionArgs, null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				LogUtil.d(TAG, "query cursor size = " + cursor.getCount());
				items = new ArrayList<GuideListItem>();
				do {

					String date = cursor.getString(QUERYGUIDELIST_DATEVALUE);
					if (date == null || date.isEmpty()) {
						// invalid item
						continue;
					}

					GuideListItem item = new GuideListItem();

					item.Date = date;
					if (date.length() == "yyyy-mm-dd".length()) {
						item.NormalizedDate = date + " 00:00:00";
					} else if (date.length() > "yyyy-mm-dd".length()) {
						item.NormalizedDate = date.substring(0,
								"yyyy-mm-dd".length())
								+ " 00:00:00";
					}
					item.GuideListID = cursor
							.getString(QUERYGUIDELIST_GUIDELISTID);
					item.PublicationID = cursor
							.getString(QUERYGUIDELIST_PUBLICATIONID);
					int status = cursor.getInt(QUERYGUIDELIST_USERSTATUS);
					item.isSelected = item.originalSelected = status == 0 ? false
							: true;
					items.add(item);
				} while (cursor.moveToNext());
			}
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		if (items != null && items.size() > 0) {

			for (int i = 0; i < items.size(); i++) {
				items.get(i).Name = getPublicationResStr(
						items.get(i).GuideListID,
						GDDVBDataContract.GUIDELISTTABLE,
						GDDVBDataContract.ValuePublicationName);
				items.get(i).ColumnType = getPublicationResStr(
						items.get(i).GuideListID,
						GDDVBDataContract.GUIDELISTTABLE,
						GDDVBDataContract.ValueColumnName);
			}

			return items.toArray(new GuideListItem[items.size()]);
		}

		return null;
	}

	public GuideListItem[] getGuideList(String Date) {
		String selection = GuideList.DATEVALUE + "=?";
		String[] selectionArgs = { Date };

		return getGuideList(selection, selectionArgs);
	}

	public GuideListItem[] getGuideList() {
		return getGuideList(null, null);
	}

	public GuideListItem[] getLatestGuideList() {
		String selection = GuideList.DATEVALUE
				+ ">= datetime('now','localtime')";

		return getGuideList(selection, null);
	}

	public boolean updateGuideList(GuideListItem[] items) {
		boolean result = true;

		String sql = "UPDATE " + GDDVBDataContract.GUIDELISTTABLE + " SET "
				+ GuideList.USERSTATUS + "=? WHERE " + GuideList.DATEVALUE
				+ "=? AND " + GuideList.PUBLICATIONID + "=?";

		String[][] bindArgs = new String[items.length][];
		for (int i = 0; i < items.length; i++) {
			String[] args = new String[3];
			args[0] = items[i].isSelected ? "1" : "0";
			args[1] = items[i].Date;
			args[2] = items[i].PublicationID;
			bindArgs[i] = args;
		}

		result = mDVBDataProvider.execBatchSql(sql, bindArgs);

		return result;
	}

	// Global property query
	public String getPreviewPath() {
		return queryDeviceGlobalProperty(GDDVBDataContract.PropertyPreviewPath);
	}

	public String getPushDir() {
		return queryDeviceGlobalProperty(GDDVBDataContract.PropertyPushDir);
	}

	public String getColumnResDir() {
		return queryGlobalProperty(GDDVBDataContract.PropertyColumnResPath);
	}

	public String getLanguage() {
		return queryDeviceGlobalProperty(GDDVBDataContract.PropertyLanguage);
	}

	public String getPushSource() {
		return queryDeviceGlobalProperty(GDDVBDataContract.PropertyPushSource);
	}

	public boolean setPushDir(String pushDir) {
		return updateDeviceGlobalProperty(GDDVBDataContract.PropertyPushDir, pushDir);
	}

	public boolean setPushSource(String source) {
		return updateDeviceGlobalProperty(GDDVBDataContract.PropertyPushSource,
				source);
	}

	public String getDeviceSearialNumber() {
		return queryDeviceGlobalProperty(GDDVBDataContract.PropertyDeviceSearialNumber);
	}

	public String getHardwareType() {
		return queryDeviceGlobalProperty(GDDVBDataContract.PropertyHardwareType);
	}

	public String getSoftwareVersion() {
		return queryDeviceGlobalProperty(GDDVBDataContract.PropertySoftwareVersion);
	}

	public String getLoaderVersion() {
		return queryDeviceGlobalProperty(GDDVBDataContract.PropertyLoaderVersion);
	}
	
	public String getRichMediaColumnID(String columnType) {
		return queryRichMediaColumnId(columnType);
	}

	public String queryGlobalProperty(String property) {
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
	
	// this property is related to Device but not storage(flash/harddisk), such as PushDir, ProductSN, DeviceModel...
	public String queryDeviceGlobalProperty(String property) {
		String value = null;
		String selection = GDDVBDataContract.Global.NAME + "=?";
		String[] selectionArgs = { property };
		Cursor cursor = mDVBDataProvider.deviceGlobalQuery(
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
		
		LogUtil.d(TAG, "queryDeviceGlobal: query property["+property+"] has value["+value+"]");

		return value;
	}
	
	public boolean updateGlobalProperty(String property, String value) {
		String selection = GDDVBDataContract.Global.NAME + "=?";
		String[] selectionArgs = { property };
		//LogUtil.d(TAG, "updateGlobalProperty: selection["+selection+"]");
		Cursor cursor = mDVBDataProvider.query(
				GDDVBDataContract.Global.CONTENT_URI,
				GDDVBDataProvider.GlobalQuery.COLUMNS, selection,
				selectionArgs, null);

		boolean ret = true;
		int Id = -1;
		String oldValue = "";
		
		LogUtil.d(TAG, "updateGlobalProperty: want set property["+property+"] value["+value+"]");
		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				Id = 0;
				oldValue = cursor
						.getString(GDDVBDataProvider.GlobalQuery.VALUE);
				LogUtil.d(TAG, "Global["+ property + "], old value = " + oldValue);
			}
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		if (Id < 0) {
			LogUtil.d(TAG, "insert to Global["+property+"] value=" + value);
			// insert
			ContentValues values = new ContentValues();
			values.put(GDDVBDataContract.Global.NAME, property);
			values.put(GDDVBDataContract.Global.VALUE, value);
			Uri retUri = mDVBDataProvider.insert(
					GDDVBDataContract.Global.CONTENT_URI, values);
			/*
			 * long rowId = Long.valueOf(retUri.getLastPathSegment()); if (rowId
			 * > 0)
			 */
			if (retUri != null)
				ret = true;
		} else {
			LogUtil.d(TAG, "update " + oldValue + " to " + value);

			if (oldValue != null && !oldValue.equals(value)) {
				// update
				ContentValues values = new ContentValues();
				values.put(GDDVBDataContract.Global.VALUE, value);
				int count = mDVBDataProvider.update(
						GDDVBDataContract.Global.CONTENT_URI, values,
						selection, selectionArgs);
				if (count == 1)
					ret = true;
			}
		}

		return ret;
	}
	
	// this property is related to Device but not storage(flash/harddisk), such as PushDir, ProductSN, DeviceModel...
	public boolean updateDeviceGlobalProperty(String property, String value) {
		String selection = GDDVBDataContract.Global.NAME + "=?";
		String[] selectionArgs = { property };
		Cursor cursor = mDVBDataProvider.deviceGlobalQuery(
				GDDVBDataContract.Global.CONTENT_URI,
				GDDVBDataProvider.GlobalQuery.COLUMNS, selection,
				selectionArgs, null);

		boolean ret = true;
		int Id = -1;
		String oldValue = "";
		
		LogUtil.d(TAG, "updateDeviceGlobal: want set property["+property+"] value["+value+"]");
		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				Id = 0;
				oldValue = cursor
						.getString(GDDVBDataProvider.GlobalQuery.VALUE);
				LogUtil.d(TAG, "updateDeviceGlobal["+ property + "], old value = " + oldValue);
			}
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		if (Id < 0) {
			LogUtil.d(TAG, "updateDeviceGlobal, insert to Global["+property+"] value=" + value);
			// insert
			ContentValues values = new ContentValues();
			values.put(GDDVBDataContract.Global.NAME, property);
			values.put(GDDVBDataContract.Global.VALUE, value);
			Uri retUri = mDVBDataProvider.DeviceGlobalinsert(
					GDDVBDataContract.Global.CONTENT_URI, values);
			/*
			 * long rowId = Long.valueOf(retUri.getLastPathSegment()); if (rowId
			 * > 0)
			 */
			if (retUri != null)
				ret = true;
		} else {
			LogUtil.d(TAG, "updateDeviceGlobal, update " + oldValue + " to " + value);

			if (oldValue != null && !oldValue.equals(value)) {
				// update
				ContentValues values = new ContentValues();
				values.put(GDDVBDataContract.Global.VALUE, value);
				int count = mDVBDataProvider.DeviceGlobalupdate(
						GDDVBDataContract.Global.CONTENT_URI, values,
						selection, selectionArgs);
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

		String selection = Preview.RECEIVESTATUS + "=? OR "
				+ Preview.RECEIVESTATUS + "=?";

		String[] selectionArgs = new String[] { "1", "2" };

		Cursor cursor = mDVBDataProvider.query(Preview.CONTENT_URI,
				ProjectionQueryPreview, selection, selectionArgs, null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				items = new PreviewData[cursor.getCount()];
				int i = 0;
				do {
					PreviewData item = new PreviewData();
					item.Type = cursor.getString(QUERYPREVIEW_PREVIEWTYPE);
					item.URI = cursor.getString(QUERYPREVIEW_PREVIEWURI);

					// item.ShowTime = Integer.valueOf(cursor
					// .getString(QUERYPREVIEW_SHOWTIME));
					// item.Duration = Integer.valueOf(cursor
					// .getString(QUERYPREVIEW_DURATION));
					// item.PlayMode = Integer.valueOf(cursor
					// .getString(QUERYPREVIEW_PLAYMODE));
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

		Cursor cursor = mSmartHomeProvider.query(
				GDSmartHomeContract.Global.CONTENT_URI,
				GDSmartHomeProvider.GlobalQuery.COLUMNS, selection,
				selectionArgs, null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				Id = 0;
				oldValue = cursor
						.getString(GDSmartHomeProvider.GlobalQuery.VALUE);
			}
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		if (Id < 0) {
			// insert
			ContentValues values = new ContentValues();
			values.put(GDSmartHomeContract.Global.NAME, key);
			values.put(GDSmartHomeContract.Global.VALUE, value);
			Uri retUri = mSmartHomeProvider.insert(
					GDSmartHomeContract.Global.CONTENT_URI, values);
			/*
			 * long rowId = Long.valueOf(retUri.getLastPathSegment()); if (rowId
			 * > 0)
			 */
			if (retUri != null)
				ret = true;
		} else {
			if (!oldValue.equals(value)) {
				// update
				selection = GDSmartHomeContract.Global.NAME + "=?";
				selectionArgs = new String[] { key };

				ContentValues values = new ContentValues();
				values.put(GDSmartHomeContract.Global.VALUE, value);
				int count = mSmartHomeProvider.update(
						GDSmartHomeContract.Global.CONTENT_URI, values,
						selection, selectionArgs);
				if (count == 1)
					ret = true;
			}
		}

		return ret;
	}

	public String getSettingValue(String key) {
		Cursor cursor = null;
		String value = "";
		String selection = GDSmartHomeContract.Global.NAME + "=?";
		String[] selectionArgs = new String[] { key };

		cursor = mSmartHomeProvider.query(
				GDSmartHomeContract.Global.CONTENT_URI,
				GDSmartHomeProvider.GlobalQuery.COLUMNS, selection,
				selectionArgs, null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				LogUtil.d(TAG, "query cursor size = " + cursor.getCount());

				value = cursor.getString(GDSmartHomeProvider.GlobalQuery.VALUE);
			}
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return value;
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

	public Bitmap getImage(String file) {
		LogUtil.d(TAG, "image =" + file);

		if (!isFileExist(file))
			return null;

		Bitmap image = BitmapFactory.decodeFile(file);

		return image;
	}

	public void getDetailsData(String xmlFile, ContentData content) {

		LogUtil.d(TAG, "getDetailsData xmlFile " + xmlFile);

		if (xmlFile == null || xmlFile.isEmpty())
			return;

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

	public boolean savePublicationBookmark(String publicationId, int bookmark) {
		boolean result = false;
		if (publicationId != null && !publicationId.isEmpty()) {
			result = setPublicationProperty(publicationId,
					Publication.BOOKMARK, String.valueOf(bookmark));
		}

		return result;
	}

	public boolean deletePublication(String setId, String publicationId) {
		boolean result = false;
		if (publicationId != null && !publicationId.isEmpty()) {
			result = setPublicationProperty(publicationId, Publication.DELETED,
					"1");
		}

		return result;
	}

	public boolean deletePublicationSet(String publicationSetId) {
		boolean result = false;
		if (publicationSetId != null && !publicationSetId.isEmpty()) {
			result = setPublicationSetProperty(publicationSetId,
					PublicationsSet.DELETED, "1");
		}
		return result;
	}

	// user favorite
	public boolean addPublicationToFavourite(String publicationSetId,
			String publicationId) {

		boolean result = true;
		if (publicationSetId != null && !publicationSetId.isEmpty()) {
			result = addPublicationSetToFavourite(publicationSetId);
		}

		if (result && publicationId != null && !publicationId.isEmpty()) {
			result = setPublicationFavouriteProperty(publicationId, "1");
		}

		return result;
	}

	public boolean addPublicationSetToFavourite(String publicationSetId) {
		return setPublicationSetFavouriteProperty(publicationSetId, "1");
	}

	public boolean removePublicationFromFavourite(String publicationSetId,
			String publicationId) {
		boolean result = true;
		if (publicationId != null && !publicationId.isEmpty()) {
			result = setPublicationFavouriteProperty(publicationId, "0");
		}

		if (result && publicationSetId != null && !publicationSetId.isEmpty()) {
			int count = getPublicationSetItemCount(publicationSetId, "1");
			if (count == 0) {
				setPublicationSetFavouriteProperty(publicationSetId, "0");
			}
		}

		return result;
	}

	private boolean setPublicationFavouriteProperty(String publicationId,
			String value) {
		return setPublicationProperty(publicationId, Publication.FAVORITE,
				value);
	}

	private boolean setPublicationSetFavouriteProperty(String publicationSetId,
			String value) {
		return setPublicationSetProperty(publicationSetId,
				PublicationsSet.FAVORITE, value);
	}

	private boolean setPublicationProperty(String publicationId,
			String propery, String value) {
		String selection = Publication.PUBLICATIONID + "=?";
		String[] selectionArgs = new String[] { publicationId };

		ContentValues values = new ContentValues();
		values.put(propery, value);

		int count = mDVBDataProvider.update(Publication.CONTENT_URI, values,
				selection, selectionArgs);

		if (count < 1)
			return false;

		return true;
	}

	private boolean setPublicationSetProperty(String publicationSetId,
			String propery, String value) {
		String selection = PublicationsSet.SETID + "=?";
		String[] selectionArgs = new String[] { publicationSetId };

		ContentValues values = new ContentValues();
		values.put(propery, value);

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
				LogUtil.d(TAG, "query cursor size = " + cursor.getCount());
				Contents = new ContentData[cursor.getCount()];
				int i = 0;
				do {
					ContentData content = new ContentData();
					content.Id = cursor.getString(QUERYCONTENT_ID);
					content.XMLFilePath = cursor.getString(QUERYCONTENT_PATH);

					LogUtil.d(TAG, "coloumn " + columnId + " item " + i + " name="
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
				LogUtil.d(TAG, "query cursor size = " + cursor.getCount());
				Entries = new ReceiveEntry[cursor.getCount()];
				int i = 0;
				do {
					ReceiveEntry entry = new ReceiveEntry();
					entry.Id = cursor.getString(QUERYBRAND_ID);
					entry.Name = cursor.getString(QUERYBRAND_CNAME);
					entry.RawProgress = cursor.getLong(QUERYBRAND_DOWNLOAD);
					entry.RawTotal = cursor.getLong(QUERYBRAND_TOTALSIZE);
					entry.ConvertSize();

					LogUtil.d(TAG, "Name " + entry.Name + " item " + i
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

	private final static String[] ProjectionQueryContentCount = { "count(*)" };

	private final static String[] ProjectionQueryContent = { Content.ID,
			Content.PATH };

	private final static int QUERYCONTENT_ID = 0;
	private final static int QUERYCONTENT_PATH = 1;

	private final static String[] ProjectionQueryBrand = { Brand.ID,
			Brand.DOWNLOAD, Brand.TOTALSIZE, Brand.CNAME };

	private int queryCount(String selection, String[] selectionArgs) {
		int count = 0;
		
		Cursor cursor = mDVBDataProvider.query(Publication.CONTENT_URI,
				ProjectionQueryContentCount, selection, selectionArgs, null);
		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				count = cursor.getInt(0);
				LogUtil.d(TAG, "count = " + count);
			}
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return count;
	}

}
