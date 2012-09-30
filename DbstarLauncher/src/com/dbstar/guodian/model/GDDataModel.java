package com.dbstar.guodian.model;

import android.net.Uri;
import android.util.Log;
import android.util.Xml;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.dbstar.guodian.model.GDDVBDataContract.*;
import com.dbstar.guodian.model.GDDVBDataProvider.ColumnEntityQuery;
import com.dbstar.guodian.model.GDDVBDataProvider.ColumnQuery;
import com.dbstar.guodian.model.GDDVBDataProvider.ResStrQuery;
import com.dbstar.guodian.model.GDSmartHomeContract.Global;

public class GDDataModel {
	private static final String TAG = "GDDataModel";

	private Context mContext = null;

	public static final String DefaultDesFile = "/info/desc/Publication.xml";

	private String mLocalization;

	public void setLocalization(String localization) {
		mLocalization = localization;
	}

	private String getLocalization() {
		return mLocalization;
	}

	public GDDataModel(Context context) {
		mContext = context;
	}

	public void initialize() {

	}

	public void deInitialize() {

	}

	public ColumnData[] getColumns(String columnId) {
//		Log.d(TAG, "getColumn id=" + columnId);
		Cursor cursor = null;
		ColumnData[] Columns = null;

		String selection = Column.PARENT_ID + "=?";
		String[] selectionArgs = new String[] { columnId };

		cursor = mContext.getContentResolver().query(Column.CONTENT_URI,
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
				GDCommon.LangCN };

		Cursor cursor = mContext.getContentResolver().query(ResStr.CONTENT_URI,
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

	public ContentData[] getReadyPublications(String columnId) {

		EntityObject[] entities = getAllEntities(columnId);
		ArrayList<ContentData> contents = new ArrayList<ContentData>();

		for (int i = 0; i < entities.length; i++) {
			PublicationData data = getPublication(entities[i].Id);
			if (data != null) {
				ContentData content = new ContentData();
				content.XMLFilePath = data.URI + DefaultDesFile;
				content.Id = data.PublicationID;
//				Log.d(TAG, "id = " + content.Id + "xml path="
//						+ content.XMLFilePath);
				contents.add(content);
			}
		}

		// ContentData[] c = new ContentData[contents.size()];
		// for (int i = 0 ; i<contents.size(); i++) {
		// c[i]= contents.get(i);
		// }
		// return c;

		return (ContentData[]) contents
				.toArray(new ContentData[contents.size()]);
	}

	public EntityObject[] getAllEntities(String columnId) {
		Cursor cursor = null;
		EntityObject[] entities = null;

		String selection = ColumnEntity.COLUMNID + "=?";
		String[] selectionArgs = new String[] { columnId };

		cursor = mContext.getContentResolver().query(ColumnEntity.CONTENT_URI,
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

		cursor = mContext.getContentResolver().query(Publication.CONTENT_URI,
				ProjectionQueryPublicationsCount, selection, selectionArgs,
				null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
//				Log.d(TAG, "query cursor size = " + cursor.getCount());
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
		Log.d(TAG, "----- posterUri " + posterUri);
		ContentData.Poster item = new ContentData.Poster();
		item.URI = posterUri;
		tv.Content.Posters.add(item);
		getEpisodes(entityId, tv);

		return tv;
	}

	private static final String PublicationSetPropertyName = "SetName";
	private static final String PublicationSetPropertyDescription = "SetDesc";

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

		Cursor cursor = null;

		String selection = ResStr.OBJECTNAME + "=? AND " + ResStr.ENTITYID
				+ "=? AND " + ResStr.STRLANG + "=? AND " + ResStr.STRNAME
				+ "=?";
		//TODO PulicationsSet should be PublicationsSet
		String[] selectionArgs = new String[] { "PulicationsSet", setId, GDCommon.LangCN,
				propertyName };

		cursor = mContext.getContentResolver().query(ResStr.CONTENT_URI,
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

		Cursor cursor = null;

		String selection = ResPoster.OBJECTNAME + "=? AND " + ResPoster.ENTITYID
				+ "=?";
		String[] selectionArgs = new String[] { GDDVBDataContract.ObjectPublicationSet, setId };

//		Log.d(TAG, "getPublicationPoster setId " + setId);

		cursor = mContext.getContentResolver().query(ResPoster.CONTENT_URI,
				QueryPosterProjection, selection, selectionArgs, null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				uri = cursor.getString(0);
//				Log.d(TAG, "poster uri = " + uri);
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

		Cursor cursor = null;

		String selection = "(" + Publication.RECEIVESTATUS + "=? or "
				+ Publication.RECEIVESTATUS + "=?) And " + Publication.VISIBLE
				+ "=? AND " + Publication.SETID + "=?";
		String[] selectionArgs = new String[] { "1", "2", "true", setId };

		cursor = mContext.getContentResolver().query(Publication.CONTENT_URI,
				ProjectionQueryEpisodes, selection, selectionArgs, null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				tv.Episodes = new TV.EpisodeItem[cursor.getCount()];

				int i = 0;
				do {
					TV.EpisodeItem item = new TV.EpisodeItem();
					item.Url = cursor.getString(EpisodesURI);
					item.Number = cursor.getInt(EpisodesIndexInSet);
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
		Cursor cursor = null;

		String selection = Content.COLUMN_ID + "=?  AND " + Content.READY
				+ "=1";
		String[] selectionArgs = new String[] { columnId };

		cursor = mContext.getContentResolver().query(Content.CONTENT_URI,
				ProjectionQueryContentCount, selection, selectionArgs, null);
		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				count = cursor.getInt(0);
				Log.d(TAG, "count = " + count);
			}
		}
		return count;
	}

	private final static int QUERYBRAND_ID = 0;
	private final static int QUERYBRAND_DOWNLOAD = 1;
	private final static int QUERYBRAND_TOTALSIZE = 2;
	private final static int QUERYBRAND_CNAME = 3;

	public ContentData[] getContents(String columnId, int pageNumber,
			int pageSize) {

		Cursor cursor = null;
		ContentData[] Contents = null;

		String selection = Content.COLUMN_ID + "=?  AND " + Content.READY
				+ "=1" + " Limit ? Offset ?";
		String[] selectionArgs = new String[] { columnId,
				Integer.toString(pageSize),
				Integer.toString(pageNumber * pageSize) };

		cursor = mContext.getContentResolver().query(Content.CONTENT_URI,
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
		return Contents;
	}

	public ReceiveEntry[] getDownloadStatus(int pageNumber, int pageSize) {

		Log.d(TAG, "getDownloadStatus");

		Cursor cursor = null;
		ReceiveEntry[] Entries = null;

		String sortOrder = Brand.ID + " Limit " + Integer.toString(pageSize)
				+ " Offset " + Integer.toString(pageNumber * pageSize);

		cursor = mContext.getContentResolver().query(Brand.CONTENT_URI,
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
		return Entries;
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
		Cursor cursor = null;

		String selection = Global.NAME + "=?";
		String[] selectionArgs = new String[] { key };

		cursor = mContext.getContentResolver().query(Global.CONTENT_URI,
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
			// values.put(Global.ID, -1);
			values.put(Global.NAME, key);
			values.put(Global.VALUE, value);
			Uri retUri = mContext.getContentResolver().insert(
					Global.CONTENT_URI, values);
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
				int count = mContext.getContentResolver().update(
						Global.CONTENT_URI, values, selection, selectionArgs);
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

		cursor = mContext.getContentResolver().query(Global.CONTENT_URI,
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

	public String getTextContent(String file) {
		String text = "";
		File descriptionFile = new File(file);
		Log.d(TAG, "get Description path=" + descriptionFile.getAbsolutePath());

		if (descriptionFile.exists() && descriptionFile.length() > 0) {
			BufferedReader br = null;

			try {
				br = new BufferedReader(new FileReader(descriptionFile));

				text = "";
				String line;
				while ((line = br.readLine()) != null) {
					text += line;
				}

				Log.d(TAG, "text = " + text);
				br.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return text;
	}

	public void getDetailsData(String xmlFile, ContentData content) {

		Log.d(TAG, "getDetailsData xmlFile " + xmlFile);
		File file = new File(xmlFile);

		if (file.exists() && file.length() > 0) {
			InputStream in = null;

			try {
				in = new BufferedInputStream(new FileInputStream(file));

				parseDetailData(in, content);

				if (in != null) {
					in.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static final String ns = null;
	private static final String TAGPublication = "Publication";
	private static final String TAGVersion = "Version";
	private static final String TAGStandardVersion = "StandardVersion";
	private static final String TAGPublicationID = "PublicationID";
	private static final String TAGPublicationNames = "PublicationNames";
	private static final String TAGPublicationName = "PublicationName";
	private static final String TAGPublicationType = "PublicationType";
	private static final String TAGIsReserved = "IsReserved";
	private static final String TAGVisible = "Visible";
	private static final String TAGDRMFile = "DRMFile";
	private static final String TAGPublicationVA = "PublicationVA";
	private static final String TAGMultipleLanguageInfos = "MultipleLanguageInfos";
	private static final String TAGMultipleLanguageInfo = "MultipleLanguageInfo";
	private static final String TAGPublicationDesc = "PublicationDesc";
	private static final String TAGKeywords = "Keywords";
	private static final String TAGImageDefinition = "ImageDefinition";
	private static final String TAGDirector = "Director";
	private static final String TAGEpisode = "Episode";
	private static final String TAGActor = "Actor";
	private static final String TAGAudioChannel = "AudioChannel";
	private static final String TAGAspectRatio = "AspectRatio";
	private static final String TAGAudience = "Audience";
	private static final String TAGModel = "Model";
	private static final String TAGLanguage = "Language";
	private static final String TAGArea = "Area";
	private static final String TAGSubTitles = "SubTitles";
	private static final String TAGSubTitle = "SubTitle";
	private static final String TAGSubTitleID = "SubTitleID";
	private static final String TAGSubTitleName = "SubTitleName";
	private static final String TAGSubTitleLanguage = "SubTitleLanguage";
	private static final String TAGSubTitleURI = "SubTitleURI";
	private static final String TAGTrailers = "Trailers";
	private static final String TAGTrailer = "Trailer";
	private static final String TAGTrailerID = "TrailerID";
	private static final String TAGTrailerName = "TrailerName";
	private static final String TAGTrailerURI = "TrailerURI";
	private static final String TAGPosters = "Posters";
	private static final String TAGPoster = "Poster";
	private static final String TAGPosterID = "PosterID";
	private static final String TAGPosterName = "PosterName";
	private static final String TAGPosterURI = "PosterURI";
	private static final String TAGExtensions = "Extensions";
	private static final String TAGExtension = "Extension";
	private static final String TAGMFile = "MFile";
	private static final String TAGFileID = "FileID";
	private static final String TAGFileNames = "FileNames";
	private static final String TAGFileName = "FileName";
	private static final String TAGFileType = "FileType";
	private static final String TAGFileSize = "FileSize";
	private static final String TAGDuration = "Duration";
	private static final String TAGFileURI = "FileURI";
	private static final String TAGResolution = "Resolution";
	private static final String TAGBitRate = "BitRate";
	private static final String TAGFileFormat = "FileFormat";
	private static final String TAGCodeFormat = "CodeFormat";

	private static final String AttributeValue = "value";
	private static final String AttributeLanguage = "language";

	private void parseDetailData(InputStream in, ContentData content) {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			readData(parser, content);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	String readPublicationNames(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		String value = null;
		parser.require(XmlPullParser.START_TAG, ns, TAGPublicationNames);
		value = parseTag(parser, TAGPublicationName);
		parser.require(XmlPullParser.END_TAG, ns, TAGPublicationNames);
//		Log.d(TAG, "readPublicationNames " + value);
		return value;
	}

	String readDRMFile(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		String value = null;
		parser.require(XmlPullParser.START_TAG, ns, TAGDRMFile);
		value = parseTag(parser, TAGFileURI);
		parser.require(XmlPullParser.END_TAG, ns, TAGDRMFile);

//		Log.d(TAG, "readDRMFile " + value);

		return value;
	}

	void readMultipleLanguageInfos(XmlPullParser parser, ContentData content)
			throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, TAGMultipleLanguageInfos);
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
//			Log.d(TAG, "tag 1 " + name + " event = " + parser.getEventType());
			if (name.equals(TAGMultipleLanguageInfo)) {
				readMultipleLanguageInfo(parser, content);
			}
		}
		parser.require(XmlPullParser.END_TAG, ns, TAGMultipleLanguageInfos);
	}

	void readMultipleLanguageInfo(XmlPullParser parser, ContentData content)
			throws IOException, XmlPullParserException {

//		Log.d(TAG, "readMultipleLanguageInfo");
		parser.require(XmlPullParser.START_TAG, ns, TAGMultipleLanguageInfo);
//		Log.d(TAG, "readMultipleLanguageInfo 1");
		String language = parser.getAttributeValue(ns, AttributeLanguage);
//		Log.d(TAG, "language " + language);
		if (language.equals(getLocalization())) {
			while (parser.next() != XmlPullParser.END_TAG) {
				if (parser.getEventType() != XmlPullParser.START_TAG) {
					continue;
				}
				String name = parser.getName();
//				Log.d(TAG, "tag 2 " + name);
				if (name.equals(TAGPublicationDesc)) {
					content.Description = readTag(parser, TAGPublicationDesc);
				} else if (name.equals(TAGKeywords)) {
					content.Keywords = readTag(parser, TAGKeywords);
				} else if (name.equals(TAGImageDefinition)) {
					content.ImageDefinition = readTag(parser,
							TAGImageDefinition);
				} else if (name.equals(TAGDirector)) {
					content.Director = readTag(parser, TAGDirector);
				} else if (name.equals(TAGActor)) {
					content.Actors = readTag(parser, TAGActor);
				} else if (name.equals(TAGAudioChannel)) {
					content.AudioChannel = readTag(parser, TAGAudioChannel);
				} else if (name.equals(TAGAspectRatio)) {
					content.AspectRatio = readTag(parser, TAGAspectRatio);
				} else if (name.equals(TAGAudience)) {
					content.Audience = readTag(parser, TAGAudience);
				} else if (name.equals(TAGModel)) {
					content.Model = readTag(parser, TAGModel);
				} else if (name.equals(TAGLanguage)) {
					content.Language = readTag(parser, TAGLanguage);
				} else if (name.equals(TAGArea)) {
					content.Area = readTag(parser, TAGArea);
				} else {
					skip(parser);
				}
			}
		} else {
			skip(parser);
		}

		parser.require(XmlPullParser.END_TAG, ns, TAGMultipleLanguageInfo);
	}

	void readSubTitles(XmlPullParser parser, ContentData content)
			throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, TAGSubTitles);
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals(TAGSubTitle)) {
				readSubTitle(parser, content);
			}
		}
		parser.require(XmlPullParser.END_TAG, ns, TAGSubTitles);
	}

	void readSubTitle(XmlPullParser parser, ContentData content)
			throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, TAGSubTitle);

		ContentData.SubTitle item = new ContentData.SubTitle();
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals(TAGSubTitleID)) {
				item.Id = readTag(parser, TAGSubTitleID);
			} else if (name.equals(TAGSubTitleName)) {
				item.Name = readTag(parser, TAGSubTitleName);
			} else if (name.equals(TAGSubTitleLanguage)) {
				item.Language = readTag(parser, TAGSubTitleLanguage);
			} else if (name.equals(TAGSubTitleURI)) {
				item.URI = readTag(parser, TAGSubTitleURI);
			} else {
				skip(parser);
			}
		}
		if (content.SubTitles == null) {
			content.SubTitles = new ArrayList<ContentData.SubTitle>();
		}
		content.SubTitles.add(item);

		parser.require(XmlPullParser.END_TAG, ns, TAGSubTitle);
	}

	String readFileNames(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		String value = null;
		parser.require(XmlPullParser.START_TAG, ns, TAGFileNames);
		value = parseTag(parser, TAGFileName);
		parser.require(XmlPullParser.END_TAG, ns, TAGFileNames);
		return value;
	}

	void readTrailers(XmlPullParser parser, ContentData content)
			throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, TAGTrailers);
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals(TAGTrailer)) {
				readTrailer(parser, content);
			}
		}
		parser.require(XmlPullParser.END_TAG, ns, TAGTrailers);
	}

	void readTrailer(XmlPullParser parser, ContentData content)
			throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, TAGTrailer);

		ContentData.Trailer item = new ContentData.Trailer();
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals(TAG)) {
				item.Id = readTag(parser, TAGTrailerID);
			} else if (name.equals(TAGTrailerName)) {
				item.Name = readTag(parser, TAGTrailerName);
			} else if (name.equals(TAGTrailerURI)) {
				item.URI = readTag(parser, TAGTrailerURI);
			} else {
				skip(parser);
			}
		}
		parser.require(XmlPullParser.END_TAG, ns, TAGTrailer);

		if (content.Trailers == null) {
			content.Trailers = new ArrayList<ContentData.Trailer>();
		}
		content.Trailers.add(item);
	}

	void readPosters(XmlPullParser parser, ContentData content)
			throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, TAGPosters);
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals(TAGPoster)) {
				readPoster(parser, content);
			}
		}
		parser.require(XmlPullParser.END_TAG, ns, TAGPosters);
	}

	void readPoster(XmlPullParser parser, ContentData content)
			throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, TAGPoster);

		ContentData.Poster item = new ContentData.Poster();
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals(TAG)) {
				item.Id = readTag(parser, TAGPosterID);
			} else if (name.equals(TAGPosterName)) {
				item.Name = readTag(parser, TAGPosterName);
			} else if (name.equals(TAGPosterURI)) {
				item.URI = readTag(parser, TAGPosterURI);
			} else {
				skip(parser);
			}
		}
		parser.require(XmlPullParser.END_TAG, ns, TAGPoster);

		if (content.Posters == null) {
			content.Posters = new ArrayList<ContentData.Poster>();
		}
		content.Posters.add(item);
	}

	void readMFile(XmlPullParser parser, ContentData content)
			throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, TAGMFile);

		ContentData.MFile file = new ContentData.MFile();
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals(TAGFileID)) {
				file.FileID = readTag(parser, TAGFileID);
			} else if (name.equals(TAGFileNames)) {
				file.FileName = readFileNames(parser);
			} else if (name.equals(TAGFileType)) {
				file.FileType = readTag(parser, TAGFileType);
			} else if (name.equals(TAGFileSize)) {
				file.FileSize = readTag(parser, TAGFileSize);
			} else if (name.equals(TAGDuration)) {
				file.Duration = readTag(parser, TAGDuration);
			} else if (name.equals(TAGFileURI)) {
				file.FileURI = readTag(parser, TAGFileURI);
			} else if (name.equals(TAGResolution)) {
				file.Resolution = readTag(parser, TAGResolution);
			} else if (name.equals(TAGBitRate)) {
				file.BitRate = readTag(parser, TAGBitRate);
			} else if (name.equals(TAGFileFormat)) {
				file.FileFormat = readTag(parser, TAGFileFormat);
			} else if (name.equals(TAGCodeFormat)) {
				file.CodeFormat = readTag(parser, TAGCodeFormat);
			} else {
				skip(parser);
			}
		}
		parser.require(XmlPullParser.END_TAG, ns, TAGMFile);
		
		content.MainFile = file;
	}

	String parseTag(XmlPullParser parser, String tag) throws IOException,
			XmlPullParserException {

		String value = null;
		String parsedValue = null;

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals(tag)) {
				value = readTags(parser, tag);
				if (value != null) {
					parsedValue = value;
				}
			}
		}

		return parsedValue;
	}

	private String readTags(XmlPullParser parser, String tag)
			throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, tag);
		String value = null;
		if (tag.equals(TAGPublicationName)) {
			value = readPublicationName(parser);
		} else if (tag.equals(TAGFileURI)) {
			value = readTag(parser, TAGFileURI);
		} else if (tag.equals(TAGFileName)) {
			value = readPublicationName(parser);
		} else {
			skip(parser);
		}
		parser.require(XmlPullParser.END_TAG, ns, tag);

//		Log.d(TAG, "tag " + tag + " value = " + value);
		return value;
	}

	String readPublicationName(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		String name = null;
		String language = parser.getAttributeValue(ns, AttributeLanguage);
//		Log.d(TAG, "readPublicationName " + language);
		if (language.equals(getLocalization())) {
			name = parser.getAttributeValue(ns, AttributeValue);
//			Log.d(TAG, "readPublicationName name " + name);
		}
		parser.nextTag();

//		Log.d(TAG, "readPublicationName next tag " + parser.getName());
		return name;
	}

	void readAVInfo(XmlPullParser parser, ContentData content)
			throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, TAGPublicationVA);
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();

//			Log.d(TAG, "tag name = " + name + " event=" + parser.getEventType());

			if (name.equals(TAGMultipleLanguageInfos)) {
				readMultipleLanguageInfos(parser, content);
			} else if (name.equals(TAGSubTitles)) {
				readSubTitles(parser, content);
			} else if (name.equals(TAGTrailers)) {
				readTrailers(parser, content);
			} else if (name.equals(TAGPosters)) {
				readPosters(parser, content);
			} else if (name.equals(TAGMFile)) {
				readMFile(parser, content);
			} else {
				skip(parser);
			}
		}
		parser.require(XmlPullParser.END_TAG, ns, TAGPublicationVA);
	}

	private void readData(XmlPullParser parser, ContentData content)
			throws XmlPullParserException, IOException {
		parser.nextTag();
		parser.require(XmlPullParser.START_TAG, ns, TAGPublication);

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
//			Log.d(TAG, "tag = " + name);

			if (name.equals(TAGPublicationNames)) {
				content.Name = readPublicationNames(parser);
			} else if (name.equals(TAGDRMFile)) {
				content.DRMFile = readDRMFile(parser);
			} else if (name.equals(TAGPublicationVA)) {
				readAVInfo(parser, content);
			} else {
				skip(parser);
			}
		}

	}

	private String readTag(XmlPullParser parser, String tag)
			throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, tag);
		String value = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, tag);
		return value;
	}

	private String readText(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
//		Log.d(TAG, "read text " + result);
		return result;
	}

	private List<String> readList(XmlPullParser parser, String rootTag,
			String tag) throws XmlPullParserException, IOException {
		List<String> items = new LinkedList<String>();
		parser.require(XmlPullParser.START_TAG, ns, rootTag);

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			String name = parser.getName();
			if (name.equals(tag)) {
				String value = readTag(parser, tag);
				items.add(value);
			} else {
				skip(parser);
			}
		}

		return items;
	}

	private void skip(XmlPullParser parser) throws XmlPullParserException,
			IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}
	}

}
