package com.settings.model;

import android.net.Uri;
import android.provider.BaseColumns;

public class GDDVBDataContract {
	public static final String AUTHORITY = "com.dbstar.provider";
	public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

	public static final String GLOBALTABLE = "Global";
	public static final String COLUMNTABLE = "Column";
	public static final String COLUMNENTITYTABLE = "ColumnEntity";
	public static final String PRODUCTTABLE = "Product";
	public static final String PUBLICATIONSETTABLE = "PublicationsSet";
	public static final String PUBLICATIONTABLE = "Publication";
	public static final String SETINFOTABLE = "SetInfo";
	public static final String MULTIPLELANGUAGEINFOVATABLE = "MultipleLanguageInfoVA";
	public static final String MULTIPLELANGUAGEINFORMTABLE = "MultipleLanguageInfoRM";
	public static final String MULTIPLELANGUAGEINFOAPPTABLE = "MultipleLanguageInfoApp";
	public static final String MFILETABLE = "MFile";
	public static final String MESSAGETABLE = "Message";
	public static final String GUIDELISTTABLE = "GuideList";
	public static final String PRODUCTDESCTABLE = "ProductDesc";
	public static final String PREVIEWTABLE = "Preview";
	public static final String RESSTRTABLE = "ResStr";
	public static final String RESPOSTERTABLE = "ResPoster";
	public static final String RESTRAILERTABLE = "ResTrailer";
	public static final String RESSUBTITLETABLE = "ResSubTitle";

	public static final String ObjectPublication = "Publication";
	public static final String ObjectPublicationSet = "PublicationsSet";
	public static final String ObjectSetName = "SetName";
	public static final String ObjectSetDesc = "SetDesc";

	public static final String ValuePublicationName = "PublicationName";
	public static final String ValueColumnName = "ColumnName";

	// Global property name
	public static final String PropertyPreviewPath = "PreviewPath";
	public static final String PropertyColumnResPath = "ColumnRes";
	public static final String PropertyPushDir = "PushDir";
	public static final String PropertyLanguage = "CurLanguage";
	public static final String PropertyPushSource = "PushSource"; // igmp://239.0.0.1:5000
	public static final String PropertyDiskGuardSize = "HDForeWarning";

	public static final String PropertyDeviceSearialNumber = "ProductSN";
	public static final String PropertyHardwareType = "DeviceModel";
	public static final String PropertySoftwareVersion = "SoftwareVersion";
	public static final String PropertyLoaderVersion = "LoaderVersion";
	
	public static final String PropertyDefaultColumnIcon = "ColumnIconDft";

	public static final class Global implements BaseColumns {

		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, "Global");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.provider.Global";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.provider.Global";

		public static final String NAME = "Name";
		public static final String VALUE = "Value";
		public static final String PARAM = "Param";
	}

	public static final class Column implements BaseColumns {

		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, "Column");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.provider.Column";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.provider.Column";

		public static final String ID = "ColumnID";
		public static final String PARENT_ID = "ParentID";
		public static final String PATH = "Path";
		public static final String TYPE = "ColumnType";
		public static final String INDEX = "SequenceNum";
		public static final String ICON_NORMAL = "ColumnIcon_losefocus";
		public static final String ICON_FOCUSED = "ColumnIcon_getfocus";
		public static final String ICON_CLICKED = "ColumnIcon_onclick";
	}

	public static final class ColumnEntity implements BaseColumns {

		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, "ColumnEntity");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.provider.ColumnEntity";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.provider.ColumnEntity";

		public static final String COLUMNID = "ColumnID";
		public static final String ENTITYID = "EntityID";
		public static final String ENTITYTYPE = "EntityType";

		public static final String ENTITYCOUNT = "count(*)";
	}

	public static final class Product implements BaseColumns {

		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, "Product");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.provider.Product";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.provider.Product";

		public static final String PRODUCTID = "ProductID";
		public static final String PRODUCTTYPE = "ProductType";
		public static final String FLAG = "Flag";
		public static final String ONLINEDATE = "OnlineDate";
		public static final String OFFLINEDATE = "OfflineDate";
		public static final String ISRESERVED = "IsReserved";
		public static final String PRICE = "Price";
		public static final String CURRENCYTYPE = "CurrencyType";
		public static final String DRMFILE = "DRMFile";
		public static final String COLUMNID = "ColumnID";
		public static final String VODNUM = "VODNum";
		public static final String VODPLATFORM = "VODPlatform";
	}

	public static final class PublicationsSet implements BaseColumns {

		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, "PublicationsSet");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.provider.PublicationsSet";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.provider.PublicationsSet";

		public static final String SETID = "SetID";
		public static final String COLUMNID = "ColumnID";
		public static final String PRODUCTID = "ProductID";
		public static final String URI = "URI";
		public static final String TOTALSIZE = "TotalSize";
		public static final String PRODUCTDESCID = "ProductDescID";
		public static final String RECEIVESTATUS = "ReceiveStatus";
		public static final String PUSHTIME = "PushTime";
		public static final String ISRESERVED = "IsReserved";
		public static final String VISIBLE = "Visible";
		public static final String FAVORITE = "Favorite";
		public static final String ISAUTHORIZED = "IsAuthorized";
		public static final String VODNUM = "VODNum";
		public static final String VODPLATFORM = "VODPlatform";
		public static final String DELETED = "Deleted";
	}
	
	public static final class SetInfo implements BaseColumns {

		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, "SetInfo");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.provider.SetInfo";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.provider.SetInfo";

		public static final String SETID = "SetID";
		public static final String TITLE = "Title";
		public static final String ACTORS = "Starring";
		public static final String DESCRIPTION = "Scenario";
		public static final String TYPE = "Classification";
		public static final String YEAR = "Period";
		public static final String EPISODECOUNT = "CollectionNumber";
	}

	public static final class Publication implements BaseColumns {

		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, "Publication");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.provider.Publication";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.provider.Publication";

		public static final String PUBLICATIONID = "PublicationID";
		public static final String COLUMNID = "ColumnID";
		public static final String PRODUCTID = "ProductID";
		public static final String URI = "URI";
		public static final String DESCURI = "DescURI";
		public static final String TOTALSIZE = "TotalSize";
		public static final String PRODUCTDESCID = "ProductDescID";
		public static final String RECEIVESTATUS = "ReceiveStatus";
		public static final String PUSHTIME = "PushTime";
		public static final String PUBLICATIONTYPE = "PublicationType";
		public static final String ISRESERVED = "IsReserved";
		public static final String VISIBLE = "Visible";
		public static final String DRMFILE = "DRMFile";
		public static final String SETID = "SetID";
		public static final String INDEXINSET = "IndexInSet";
		public static final String FAVORITE = "Favorite";
		public static final String BOOKMARK = "Bookmark";
		public static final String ISAUTHORIZED = "IsAuthorized";
		public static final String VODNUM = "VODNum";
		public static final String VODPLATFORM = "VODPlatform";
		public static final String DELETED = "Deleted";

		public static final String FILEID = "FileID";
		public static final String FILESIZE = "FileSize";
		public static final String FILEURI = "FileURI";
		public static final String FILETYPE = "FileType";
		public static final String FILEFORMAT = "FileFormat";
		public static final String DURATION = "Duration";
		public static final String RESOLUTION = "Resolution";
		public static final String BITRATE = "Bitrate";
		public static final String CODEFORMAT = "CodeFormat";
	}

	public static final class MultipleLanguageInfoVA implements BaseColumns {

		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, "MultipleLanguageInfoVA");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.provider.MultipleLanguageInfoVA";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.provider.MultipleLanguageInfoVA";

		public static final String PUBLICATIONID = "PublicationID";
		public static final String INFOLANG = "infolang";
		public static final String PUBLICATIONDESC = "PublicationDesc";
		public static final String KEYWORDS = "Keywords";
		public static final String AREA = "Area";
		public static final String LANGUAGE = "Language";

		public static final String IMAGEDEFINITION = "ImageDefinition";
		public static final String EPISODE = "Episode";
		public static final String ASPECTRATIO = "AspectRatio";
		public static final String AUDIOCHANNEL = "AudioChannel";

		public static final String DIRECTOR = "Director";
		public static final String ACTOR = "Actor";
		public static final String AUDIENCE = "Audience";
		public static final String MODEL = "Model";
	}

	public static final class MultipleLanguageInfoRM implements BaseColumns {

		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, "MultipleLanguageInfoRM");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.provider.MultipleLanguageInfoRM";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.provider.MultipleLanguageInfoRM";

		public static final String PUBLICATIONID = "PublicationID";
		public static final String INFOLANG = "infolang";
		public static final String PUBLICATIONDESC = "PublicationDesc";
		public static final String KEYWORDS = "Keywords";
		public static final String AREA = "Area";
		public static final String LANGUAGE = "Language";

		public static final String PUBLISHER = "Publisher";
		public static final String EPISODE = "Episode";
		public static final String ASPECTRATIO = "AspectRatio";
		public static final String VOLNUM = "VolNum";

		public static final String ISSN = "ISSN";

	}

	public static final class MultipleLanguageInfoApp implements BaseColumns {

		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, "MultipleLanguageInfoApp");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.provider.MultipleLanguageInfoApp";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.provider.MultipleLanguageInfoApp";

		public static final String PUBLICATIONID = "PublicationID";
		public static final String INFOLANG = "infolang";
		public static final String PUBLICATIONDESC = "PublicationDesc";
		public static final String KEYWORDS = "Keywords";
		public static final String AREA = "Area";
		public static final String LANGUAGE = "Language";

		public static final String CATEGORY = "Category";
		public static final String RELEASED = "Released";
		public static final String APPVERSION = "AppVersion";
		public static final String DEVELOPER = "Developer";

		public static final String RATED = "Rated";
		public static final String REQUIREMENTS = "Requirements";

	}

	public static final class MFile implements BaseColumns {

		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, "MFile");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.provider.MFile";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.provider.MFile";

		public static final String FILEID = "FileID";
		public static final String PUBLICATIONID = "PublicationID";
		public static final String FILESIZE = "FileSize";
		public static final String FILEURI = "FileURI";
		public static final String FILETYPE = "FileType";
		public static final String FILEFORMAT = "FileFormat";

		public static final String DURATION = "Duration";
		public static final String RESOLUTION = "Resolution";
		public static final String BITRATE = "BitRate";
		public static final String CODEFORMAT = "CodeFormat";
	}

	public static final class Message implements BaseColumns {

		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, "Message");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.provider.Message";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.provider.Message";

		public static final String MESSAGEID = "MessageID";
		public static final String TYPE = "type";
		public static final String DISPLAYFORM = "displayForm";
		public static final String STARTTIME = "StartTime";
		public static final String ENDTIME = "EndTime";
		public static final String INTERVAL = "Interval";
	}

	public static final class GuideList implements BaseColumns {

		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, "GuideList");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.provider.GuideList";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.provider.GuideList";

		public static final String DATEVALUE = "DateValue";
		public static final String GUIDELISTID = "GuideListID";
		public static final String PUBLICATIONID = "PublicationID";
		public static final String URI = "URI";
		public static final String TOTALSIZE = "TotalSize";
		public static final String PRODUCTDESCID = "ProductDescID";
		public static final String RECEIVESTATUS = "ReceiveStatus";

		public static final String USERSTATUS = "UserStatus";

		public static final String PUSHTIME = "PushTime";
		public static final String POSTERID = "PosterID";
		public static final String POSTERNAME = "PosterName";
		public static final String POSTERURI = "PosterURI";
		public static final String TRAILERID = "TrailerID";
		public static final String TRAILERNAME = "TrailerName";
		public static final String TRAILERURI = "TrailerURI";
	}

	public static final class ProductDesc implements BaseColumns {

		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, "ProductDesc");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.provider.ProductDesc";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.provider.ProductDesc";

		public static final String RECEIVETYPE = "ReceiveType";
		public static final String PRODUCTDESCID = "ProductDescID";
		public static final String ID = "ID";
		public static final String TOTALSIZE = "TotalSize";
		public static final String URI = "URI";
		public static final String RECEIVESTATUS = "ReceiveStatus";
		public static final String PUSHTIME = "PushTime";
		// public static final String NAME = "__Name";
	}

	public static final class Preview implements BaseColumns {

		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, "Preview");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.provider.Preview";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.provider.Preview";

		public static final String PREVIEWID = "PreviewID";
		public static final String PRODUCTID = "ProductID";
		public static final String PREVIEWTYPE = "PreviewType";
		public static final String PREVIEWSIZE = "PreviewSize";
		public static final String SHOWTIME = "ShowTime";
		public static final String PREVIEWURI = "PreviewURI";
		public static final String PREVIEWFORMAT = "PreviewFormat";
		public static final String DURATION = "Duration";
		public static final String RESOLUTION = "Resolution";
		public static final String BITRATE = "BitRate";
		public static final String CODEFORMAT = "CodeFormat";

		public static final String URI = "URI";
		public static final String TOTALSIZE = "TotalSize";
		public static final String PRODUCTDESCID = "ProductDescID";

		public static final String RECEIVESTATUS = "ReceiveStatus";
		public static final String PUSHTIME = "PushTime";
		public static final String STARTTIME = "StartTime";

		public static final String ENDTIME = "EndTime";
		public static final String PLAYMODE = "PlayMode";
	}

	public static final class ResStr implements BaseColumns {

		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, "ResStr");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.provider.ResStr";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.provider.ResStr";

		public static final String OBJECTNAME = "ObjectName";
		public static final String ENTITYID = "EntityID";
		public static final String STRLANG = "StrLang";
		public static final String STRNAME = "StrName";
		public static final String STRVALUE = "StrValue";
		public static final String EXTENSION = "Extension";
	}

	public static final class ResPoster implements BaseColumns {

		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, "ResPoster");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.provider.ResPoster";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.provider.ResPoster";

		public static final String OBJECTNAME = "ObjectName";
		public static final String ENTITYID = "EntityID";
		public static final String POSTERID = "PosterID";
		public static final String POSTERNAME = "PosterName";
		public static final String POSTERURI = "PosterURI";
	}

	public static final class ResTrailer implements BaseColumns {

		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, "ResTrailer");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.provider.ResTrailer";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.provider.ResTrailer";

		public static final String OBJECTNAME = "ObjectName";
		public static final String ENTITYID = "EntityID";
		public static final String TRAILERID = "TrailerID";
		public static final String TRAILERNAME = "TrailerName";
		public static final String TRAILERURI = "TrailerURI";
	}

	public static final class ResSubTitle implements BaseColumns {

		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, "ResSubTitle");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.provider.ResSubTitle";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.provider.ResSubTitle";

		public static final String OBJECTNAME = "ObjectName";
		public static final String ENTITYID = "EntityID";
		public static final String SUBTITLEID = "SubTitleID";
		public static final String SUBTITLENAME = "SubTitleName";
		public static final String SUBTITLELANGUAGE = "SubTitleLanguage";
		public static final String SUBTITLEURI = "SubTitleURI";
	}

	// not used
	public static final class Content implements BaseColumns {

		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, "content");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.provider.content";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.provider.content";

		public static final String ID = "id";
		public static final String READY = "ready";
		public static final String COLUMN_ID = "column_id";
		public static final String PATH = "path";
		public static final String SENDUSER = "senduser";
		public static final String SENDTIME = "sendtime";
		public static final String CONTENTNAME = "contentname";
		public static final String COREGTAG_ID = "coretag_id";
		public static final String CHINESENAME = "chineseName";
		public static final String ENGLISHNAME = "englishName";
		public static final String DIRECTOR = "director";
		public static final String ACTOR = "actor";
		public static final String FAVORITE = "favorite";
		public static final String BOOKMARK = "bookmark";

		public static final String QUERYCOUNT = "count(*)";
	}

	public static final class Brand implements BaseColumns {

		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, "brand");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.provider.brand";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.provider.brand";

		public static final String ID = "id";
		public static final String REGIST_DIR = "regist_dir";
		public static final String DOWNLOAD = "download";
		public static final String TOTALSIZE = "totalsize";
		public static final String CNAME = "cname";
	}
}
