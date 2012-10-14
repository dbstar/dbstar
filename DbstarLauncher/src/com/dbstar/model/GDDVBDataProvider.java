package com.dbstar.model;

import com.dbstar.model.GDDVBDataContract.Column;
import com.dbstar.model.GDDVBDataContract.ColumnEntity;
import com.dbstar.model.GDDVBDataContract.GuideList;
import com.dbstar.model.GDDVBDataContract.MFile;
import com.dbstar.model.GDDVBDataContract.Message;
import com.dbstar.model.GDDVBDataContract.Preview;
import com.dbstar.model.GDDVBDataContract.Product;
import com.dbstar.model.GDDVBDataContract.ProductDesc;
import com.dbstar.model.GDDVBDataContract.Publication;
import com.dbstar.model.GDDVBDataContract.PublicationsSet;
import com.dbstar.model.GDDVBDataContract.ResPoster;
import com.dbstar.model.GDDVBDataContract.ResStr;
import com.dbstar.model.GDDVBDataContract.ResSubTitle;
import com.dbstar.model.GDDVBDataContract.ResTrailer;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class GDDVBDataProvider  extends GDDBProvider {

	private static final String TAG = "DVBDataProvider";

	private static final int COLUMNTABLE = 1001;
	private static final int PUBLICATIONSETTABLE = 1002;
	private static final int PUBLICATIONTABLE = 1003;
	private static final int PRODUCTTABLE = 1004;
	private static final int PRODUCTDESCTABLE = 1005;
	private static final int PREVIEWTABLE = 1006;
	private static final int GUIDELISTTABLE = 1007;
	private static final int MESSAGETABLE = 1008;
	private static final int MFILETABLE = 1009;
	private static final int MULTIPLELANGUAGEINFOAPPTABLE = 1010;
	private static final int MULTIPLELANGUAGEINFORMTABLE = 1011;

	private static final int MULTIPLELANGUAGEINFOVATABLE = 1012;
	private static final int RESPOSTERTABLE = 1013;
	private static final int RESSTRTABLE = 1014;
	private static final int RESSUBTITLETABLE = 1015;
	private static final int RESTRAILERTABLE = 1016;

	private static final int COLUMNENTITYTABLE = 1017;

	static {
		sURIMatcher.addURI(GDDVBDataContract.AUTHORITY,
				GDDVBDataContract.COLUMNTABLE, COLUMNTABLE);
		sURIMatcher.addURI(GDDVBDataContract.AUTHORITY,
				GDDVBDataContract.COLUMNENTITYTABLE, COLUMNENTITYTABLE);
		sURIMatcher.addURI(GDDVBDataContract.AUTHORITY,
				GDDVBDataContract.PUBLICATIONSETTABLE, PUBLICATIONSETTABLE);
		sURIMatcher.addURI(GDDVBDataContract.AUTHORITY,
				GDDVBDataContract.PUBLICATIONTABLE, PUBLICATIONTABLE);
		sURIMatcher.addURI(GDDVBDataContract.AUTHORITY,
				GDDVBDataContract.PRODUCTTABLE, PRODUCTTABLE);
		sURIMatcher.addURI(GDDVBDataContract.AUTHORITY,
				GDDVBDataContract.PRODUCTDESCTABLE, PRODUCTDESCTABLE);
		sURIMatcher.addURI(GDDVBDataContract.AUTHORITY,
				GDDVBDataContract.PREVIEWTABLE, PREVIEWTABLE);
		sURIMatcher.addURI(GDDVBDataContract.AUTHORITY,
				GDDVBDataContract.GUIDELISTTABLE, GUIDELISTTABLE);
		sURIMatcher.addURI(GDDVBDataContract.AUTHORITY,
				GDDVBDataContract.MESSAGETABLE, MESSAGETABLE);
		sURIMatcher.addURI(GDDVBDataContract.AUTHORITY,
				GDDVBDataContract.MFILETABLE, MFILETABLE);
		sURIMatcher.addURI(GDDVBDataContract.AUTHORITY,
				GDDVBDataContract.MULTIPLELANGUAGEINFOAPPTABLE,
				MULTIPLELANGUAGEINFOAPPTABLE);
		sURIMatcher.addURI(GDDVBDataContract.AUTHORITY,
				GDDVBDataContract.MULTIPLELANGUAGEINFORMTABLE,
				MULTIPLELANGUAGEINFORMTABLE);
		sURIMatcher.addURI(GDDVBDataContract.AUTHORITY,
				GDDVBDataContract.MULTIPLELANGUAGEINFOVATABLE,
				MULTIPLELANGUAGEINFOVATABLE);
		sURIMatcher.addURI(GDDVBDataContract.AUTHORITY,
				GDDVBDataContract.RESPOSTERTABLE, RESPOSTERTABLE);
		sURIMatcher.addURI(GDDVBDataContract.AUTHORITY,
				GDDVBDataContract.RESSTRTABLE, RESSTRTABLE);
		sURIMatcher.addURI(GDDVBDataContract.AUTHORITY,
				GDDVBDataContract.RESSUBTITLETABLE, RESSUBTITLETABLE);
		sURIMatcher.addURI(GDDVBDataContract.AUTHORITY,
				GDDVBDataContract.RESTRAILERTABLE, RESTRAILERTABLE);
	}

	interface Tables {
		String COLUMN = GDDVBDataContract.COLUMNTABLE;
		String COLUMNENTITY = GDDVBDataContract.COLUMNENTITYTABLE;
		String GUIDELIST = GDDVBDataContract.GUIDELISTTABLE;
		String MESSAGE = GDDVBDataContract.MESSAGETABLE;
		String MFILE = GDDVBDataContract.MFILETABLE;
		String MULTIPLELANGUAGEINFOAPP = GDDVBDataContract.MULTIPLELANGUAGEINFOAPPTABLE;
		String MULTIPLELANGUAGEINFORM = GDDVBDataContract.MULTIPLELANGUAGEINFORMTABLE;
		String MULTIPLELANGUAGEINFOVA = GDDVBDataContract.MULTIPLELANGUAGEINFOVATABLE;
		String PREVIEW = GDDVBDataContract.PREVIEWTABLE;
		String PRODUCTDESC = GDDVBDataContract.PRODUCTDESCTABLE;
		String PRODUCT = GDDVBDataContract.PRODUCTTABLE;
		String PUBLICATIONSET = GDDVBDataContract.PUBLICATIONSETTABLE;
		String PUBLICATION = GDDVBDataContract.PUBLICATIONTABLE;
		String RESPOSTER = GDDVBDataContract.RESPOSTERTABLE;
		String RESSTR = GDDVBDataContract.RESSTRTABLE;
		String RESSUBTITLE = GDDVBDataContract.RESSUBTITLETABLE;
		String RESTRAILER = GDDVBDataContract.RESTRAILERTABLE;
	}

	public interface ColumnQuery {
		String TABLE = Tables.COLUMN;

		String[] COLUMNS = new String[] { Column.ID, Column.PATH, Column.TYPE,
				Column.ICON_NORMAL, Column.ICON_FOCUSED, Column.ICON_CLICKED };

		int ID = 0;
		int PATH = 1;
		int TYPE = 2;
		int ICON_NORMAL = 3;
		int ICON_FOCUSED = 4;
		int ICON_CLICKED = 5;
	}

	public interface ColumnEntityQuery {
		String TABLE = Tables.COLUMNENTITY;

		String[] COLUMNS = new String[] { ColumnEntity.ENTITYID,
				ColumnEntity.ENTITYTYPE };

		int ENTITYID = 0;
		int ENTITYTYPE = 1;
	}

	public interface GuideListQuery {
		String TABLE = Tables.GUIDELIST;

		String[] COLUMNS = new String[] { GuideList.DATEVALUE,
				GuideList.GUIDELISTID, GuideList.PUBLICATIONID, GuideList.URI,
				GuideList.TOTALSIZE, GuideList.PRODUCTDESCID,
				GuideList.RECEIVESTATUS, GuideList.USERSTATUS, GuideList.PUSHTIME,
				GuideList.POSTERID, GuideList.POSTERNAME, GuideList.POSTERURI,
				GuideList.TRAILERID, GuideList.TRAILERNAME,
				GuideList.TRAILERURI };

		int DATEVALUE = 0;
		int GUIDELISTID = 1;
		int PUBLICATIONID = 2;
		int URI = 3;
		int TOTALSIZE = 4;
		int PRODUCTDESCID = 5;
		int RECEIVESTATUS = 6;
		int PUSHTIME = 7;
		int POSTERID = 8;
		int POSTERNAME = 9;
		int POSTERURI = 10;
		int TRAILERID = 11;
		int TRAILERNAME = 12;
		int TRAILERURI = 13;
	}

	public interface ProductQuery {
		String TABLE = Tables.PRODUCT;

		String[] COLUMNS = new String[] { Product.PRODUCTID,
				Product.PRODUCTTYPE, Product.FLAG, Product.ONLINEDATE,
				Product.OFFLINEDATE, Product.ISRESERVED, Product.PRICE,
				Product.CURRENCYTYPE, Product.DRMFILE, Product.COLUMNID,
				Product.VODNUM, Product.VODPLATFORM };

		int PRODUCTID = 0;
		int PRODUCTTYPE = 1;
		int FLAG = 2;
		int ONLINEDATE = 3;
		int OFFLINEDATE = 4;
		int ISRESERVED = 5;
		int PRICE = 6;
		int CURRENCYTYPE = 7;
		int DRMFILE = 8;
		int COLUMNID = 9;
		int VODNUM = 10;
		int VODPLATFORM = 11;
	}

	public interface PublicationsSetQuery {
		String TABLE = Tables.PUBLICATIONSET;

		String[] COLUMNS = new String[] { PublicationsSet.SETID,
				PublicationsSet.COLUMNID, PublicationsSet.PRODUCTID,
				PublicationsSet.URI, PublicationsSet.TOTALSIZE,
				PublicationsSet.PRODUCTDESCID, PublicationsSet.RECEIVESTATUS,
				PublicationsSet.PUSHTIME, PublicationsSet.ISRESERVED,
				PublicationsSet.VISIBLE, PublicationsSet.FAVORITE,
				PublicationsSet.ISAUTHORIZED, PublicationsSet.VODNUM,
				PublicationsSet.VODPLATFORM };

		int SETID = 0;
		int COLUMNID = 1;
		int PRODUCTID = 2;
		int URI = 3;
		int TOTALSIZE = 4;
		int PRODUCTDESCID = 5;
		int RECEIVESTATUS = 6;
		int PUSHTIME = 7;
		int ISRESERVED = 8;
		int VISIBLE = 9;
		int FAVORITE = 10;
		int ISAUTHORIZED = 11;
		int VODNUM = 12;
		int VODPLATFORM = 13;
	}

	public interface PublicationQuery {
		String TABLE = Tables.PUBLICATION;

		String[] COLUMNS = new String[] { Publication.COLUMNID,
				Publication.PRODUCTID, Publication.URI, Publication.TOTALSIZE,
				Publication.PRODUCTDESCID, Publication.RECEIVESTATUS,
				Publication.PUSHTIME, Publication.PUBLICATIONTYPE,
				Publication.ISRESERVED, Publication.VISIBLE,
				Publication.DRMFILE, Publication.SETID, Publication.INDEXINSET,
				Publication.FAVORITE, Publication.BOOKMARK,
				Publication.ISAUTHORIZED, Publication.VODNUM,
				Publication.VODPLATFORM };

		int COLUMNID = 0;
		int PRODUCTID = 1;
		int URI = 2;
		int TOTALSIZE = 3;
		int PRODUCTDESCID = 4;
		int RECEIVESTATUS = 5;
		int PUSHTIME = 6;
		int PUBLICATIONTYPE = 7;
		int ISRESERVED = 8;
		int VISIBLE = 9;
		int DRMFILE = 10;
		int SETID = 11;
		int INDEXINSET = 12;
		int FAVORITE = 13;
		int BOOKMARK = 14;
		int ISAUTHORIZED = 15;
		int VODNUM = 16;
		int VODPLATFORM = 17;
	}

	public interface MFileQuery {
		String TABLE = Tables.MFILE;

		String[] COLUMNS = new String[] { MFile.FILEID, MFile.PUBLICATIONID,
				MFile.FILESIZE, MFile.FILEURI, MFile.FILETYPE,
				MFile.FILEFORMAT, MFile.DURATION, MFile.RESOLUTION,
				MFile.BITRATE, MFile.CODEFORMAT };

		int FILEID = 0;
		int PUBLICATIONID = 1;
		int FILESIZE = 2;
		int FILEURI = 3;
		int FILETYPE = 4;
		int FILEFORMAT = 5;
		int DURATION = 6;
		int RESOLUTION = 7;
		int CODEFORMAT = 8;
	}

	public interface MessageQuery {
		String TABLE = Tables.MESSAGE;

		String[] COLUMNS = new String[] { Message.MESSAGEID, Message.TYPE,
				Message.DISPLAYFORM, Message.STARTTIME, Message.ENDTIME,
				Message.INTERVAL };

		int MESSAGEID = 0;
		int TYPE = 1;
		int DISPLAYFORM = 2;
		int STARTTIME = 3;
		int ENDTIME = 4;
		int INTERVAL = 5;
	}

	public interface ProductDescQuery {
		String TABLE = Tables.PRODUCTDESC;

		String[] COLUMNS = new String[] { ProductDesc.RECEIVETYPE,
				ProductDesc.PRODUCTDESCID, ProductDesc.ID,
				ProductDesc.TOTALSIZE, ProductDesc.URI,
				ProductDesc.RECEIVESTATUS, ProductDesc.PUSHTIME, };

		int RECEIVETYPE = 0;
		int PRODUCTDESCID = 1;
		int ID = 2;
		int TOTALSIZE = 3;
		int URI = 4;
		int RECEIVESTATUS = 5;
		int PUSHTIME = 6;
	}

	public interface PreviewQuery {
		String TABLE = Tables.PREVIEW;

		String[] COLUMNS = new String[] { Preview.PREVIEWID, Preview.PRODUCTID,
				Preview.PREVIEWTYPE, Preview.PREVIEWSIZE, Preview.SHOWTIME,
				Preview.PREVIEWURI, Preview.PREVIEWFORMAT, Preview.DURATION,
				Preview.RESOLUTION, Preview.BITRATE, Preview.CODEFORMAT,

				Preview.URI, Preview.TOTALSIZE, Preview.PRODUCTDESCID,
				Preview.RECEIVESTATUS, Preview.PUSHTIME, Preview.STARTTIME,
				Preview.ENDTIME, Preview.PLAYMODE };

		int PREVIEWID = 0;
		int PRODUCTID = 1;
		int PREVIEWTYPE = 2;
		int PREVIEWSIZE = 3;
		int SHOWTIME = 4;
		int PREVIEWURI = 5;
		int PREVIEWFORMAT = 6;
		int DURATION = 7;
		int RESOLUTION = 8;
		int BITRATE = 9;
		int CODEFORMAT = 10;
		int URI = 11;
		int TOTALSIZE = 12;
		int PRODUCTDESCID = 13;
		int RECEIVESTATUS = 14;
		int PUSHTIME = 15;
		int STARTTIME = 16;
		int ENDTIME = 17;
		int PLAYMODE = 18;
	}

	public interface ResStrQuery {
		String TABLE = Tables.RESSTR;

		String[] COLUMNS = new String[] { ResStr.STRVALUE };

		int STRVALUE = 0;
	}

	public interface ResPosterQuery {
		String TABLE = Tables.RESPOSTER;

		String[] COLUMNS = new String[] { ResPoster.POSTERURI };

		int POSTERURI = 0;
	}

	public interface ResTrailerQuery {
		String TABLE = Tables.RESTRAILER;

		String[] COLUMNS = new String[] { ResTrailer.TRAILERURI };

		int TRAILERURI = 0;
	}

	public interface ResSubTitleQuery {
		String TABLE = Tables.RESSUBTITLE;

		String[] COLUMNS = new String[] { ResSubTitle.SUBTITLEURI };

		int SUBTITLEURI = 0;
	}

	
	// @Override
	public boolean initialize(GDSystemConfigure configure) {
		super.initialize(configure);
		
		String dbFile = mConfigure.getDVBDatabaseFile();
		if (!isFileExist(dbFile)) {
			return false;
		}
		
		mDbFile = dbFile;
		
		return true;
	}
	
	public void deinitialize() {
		super.deinitialize();
	}

	// @Override
	public String getType(Uri uri) {
		int match = sURIMatcher.match(uri);
		String typeStr;
		switch (match) {
		case COLUMNTABLE:
			typeStr = GDDVBDataContract.Column.CONTENT_TYPE;
			break;
		case COLUMNENTITYTABLE:
			typeStr = GDDVBDataContract.ColumnEntity.CONTENT_TYPE;
			break;
		case PUBLICATIONSETTABLE:
			typeStr = GDDVBDataContract.PublicationsSet.CONTENT_TYPE;
			break;
		case PUBLICATIONTABLE:
			typeStr = GDDVBDataContract.Publication.CONTENT_TYPE;
			break;

		case PRODUCTTABLE:
			typeStr = GDDVBDataContract.Product.CONTENT_TYPE;
			break;
		case PRODUCTDESCTABLE:
			typeStr = GDDVBDataContract.ProductDesc.CONTENT_TYPE;
			break;
		case PREVIEWTABLE:
			typeStr = GDDVBDataContract.Preview.CONTENT_TYPE;
			break;
		case GUIDELISTTABLE:
			typeStr = GDDVBDataContract.GuideList.CONTENT_TYPE;
			break;
		case MESSAGETABLE:
			typeStr = GDDVBDataContract.Message.CONTENT_TYPE;
			break;
		case MFILETABLE:
			typeStr = GDDVBDataContract.MFile.CONTENT_TYPE;
			break;
		case MULTIPLELANGUAGEINFOAPPTABLE:
			typeStr = GDDVBDataContract.MultipleLanguageInfoApp.CONTENT_TYPE;
			break;
		case MULTIPLELANGUAGEINFORMTABLE:
			typeStr = GDDVBDataContract.MultipleLanguageInfoRM.CONTENT_TYPE;
			break;

		case MULTIPLELANGUAGEINFOVATABLE:
			typeStr = GDDVBDataContract.MultipleLanguageInfoVA.CONTENT_TYPE;
			break;
		case RESPOSTERTABLE:
			typeStr = GDDVBDataContract.ResPoster.CONTENT_TYPE;
			break;
		case RESSTRTABLE:
			typeStr = GDDVBDataContract.ResStr.CONTENT_TYPE;
			break;
		case RESSUBTITLETABLE:
			typeStr = GDDVBDataContract.ResSubTitle.CONTENT_TYPE;
			break;
		case RESTRAILERTABLE:
			typeStr = GDDVBDataContract.ResTrailer.CONTENT_TYPE;
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

		case COLUMNTABLE:
			table = Tables.COLUMN;
			break;
		case COLUMNENTITYTABLE:
			table = Tables.COLUMNENTITY;
			break;
		case PUBLICATIONSETTABLE:
			table = Tables.PUBLICATIONSET;
			break;
		case PUBLICATIONTABLE:
			table = Tables.PUBLICATION;
			break;
		case PRODUCTTABLE:
			table = Tables.PRODUCT;
			break;
		case PRODUCTDESCTABLE:
			table = Tables.PRODUCTDESC;
			break;
		case PREVIEWTABLE:
			table = Tables.PREVIEW;
			break;
		case GUIDELISTTABLE:
			table = Tables.GUIDELIST;
			break;
		case MESSAGETABLE:
			table = Tables.MESSAGE;
			break;
		case MFILETABLE:
			table = Tables.MFILE;
			break;
		case MULTIPLELANGUAGEINFOAPPTABLE:
			table = Tables.MULTIPLELANGUAGEINFOAPP;
			break;
		case MULTIPLELANGUAGEINFORMTABLE:
			table = Tables.MULTIPLELANGUAGEINFORM;
			break;

		case MULTIPLELANGUAGEINFOVATABLE:
			table = Tables.MULTIPLELANGUAGEINFOVA;
			break;
		case RESPOSTERTABLE:
			table = Tables.RESPOSTER;
			break;
		case RESSTRTABLE:
			table = Tables.RESSTR;
			break;
		case RESSUBTITLETABLE:
			table = Tables.RESSUBTITLE;
			break;
		case RESTRAILERTABLE:
			table = Tables.RESTRAILER;
			break;
		default:
			break;
		}

		return table;
	}
}
