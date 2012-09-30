package com.dbstar.guodian.model;

import android.net.Uri;
import android.provider.BaseColumns;

public class GDDVBDataContract {
	public static final String AUTHORITY = "com.dbstar.guodian.provider";
	public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
	
	public static final String COLUMNTABLE="Column";
	public static final String COLUMNENTITYTABLE="ColumnEntity";
	public static final String PRODUCTTABLE="Product";
	public static final String PUBLICATIONSETTABLE="PublicationsSet";
	public static final String PUBLICATIONTABLE="Publication";
	public static final String MULTIPLELANGUAGEINFOVATABLE="MultipleLanguageInfoVA";
	public static final String MULTIPLELANGUAGEINFORMTABLE="MultipleLanguageInfoRM";
	public static final String MULTIPLELANGUAGEINFOAPPTABLE="MultipleLanguageInfoApp";
	public static final String MFILETABLE="MFile";
	public static final String MESSAGETABLE="Message";
	public static final String GUIDELISTTABLE="GuideList";
	public static final String PRODUCTDESCTABLE="ProductDesc";
	public static final String PREVIEWTABLE="Preview";
	public static final String RESSTRTABLE="ResStr";
	public static final String RESPOSTERTABLE="ResPoster";
	public static final String RESTRAILERTABLE="ResTrailer";
	public static final String RESSUBTITLETABLE="ResSubTitle";
	
	//TOTO: spell is error!!!!!
	public static final String ObjectPublicationSet="PulicationsSet";
	

	public static final class Column implements BaseColumns {

		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, "Column");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.guodian.provider.Column";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.guodian.provider.Column";

		public static final String ID = "ColumnID";
		public static final String PARENT_ID = "ParentID";
		public static final String PATH = "Path";
		public static final String TYPE = "ColumnType";
		public static final String ICON_NORMAL = "ColumnIcon_losefocus";
		public static final String ICON_FOCUSED = "ColumnIcon_getfocus";
		public static final String ICON_CLICKED = "ColumnIcon_onclick";
	}

	public static final class ColumnEntity implements BaseColumns {

		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, "ColumnEntity");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.guodian.provider.ColumnEntity";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.guodian.provider.ColumnEntity";

		public static final String COLUMNID = "ColumnID";
		public static final String ENTITYID = "EntityID";
		public static final String ENTITYTYPE = "EntityType";
		
		public static final String ENTITYCOUNT = "count(*)";
	}

	public static final class Product implements BaseColumns {

		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, "Product");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.guodian.provider.Product";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.guodian.provider.Product";

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

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.guodian.provider.PublicationsSet";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.guodian.provider.PublicationsSet";

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
	}

	public static final class Publication implements BaseColumns {

		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, "Publication");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.guodian.provider.Publication";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.guodian.provider.Publication";

		public static final String PUBLICATIONID = "PublicationID";
		public static final String COLUMNID = "ColumnID";
		public static final String PRODUCTID = "ProductID";
		public static final String URI = "URI";
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
	}

	public static final class MultipleLanguageInfoVA implements BaseColumns {

		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, "MultipleLanguageInfoVA");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.guodian.provider.MultipleLanguageInfoVA";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.guodian.provider.MultipleLanguageInfoVA";

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

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.guodian.provider.MultipleLanguageInfoRM";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.guodian.provider.MultipleLanguageInfoRM";

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

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.guodian.provider.MultipleLanguageInfoApp";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.guodian.provider.MultipleLanguageInfoApp";

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

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.guodian.provider.MFile";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.guodian.provider.MFile";

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

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.guodian.provider.Message";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.guodian.provider.Message";

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

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.guodian.provider.GuideList";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.guodian.provider.GuideList";

		public static final String DATEVALUE = "DateValue";
		public static final String GUIDELISTID = "GuideListID";
		public static final String PUBLICATIONID = "PublicationID";
		public static final String URI = "URI";
		public static final String TOTALSIZE = "TotalSize";
		public static final String PRODUCTDESCID = "ProductDescID";
		public static final String RECEIVESTATUS = "ReceiveStatus";
		
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

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.guodian.provider.ProductDesc";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.guodian.provider.ProductDesc";

		public static final String RECEIVETYPE = "ReceiveType";
		public static final String PRODUCTDESCID = "ProductDescID";
		public static final String ID = "ID";
		public static final String TOTALSIZE = "TotalSize";
		public static final String URI = "URI";
		public static final String RECEIVESTATUS = "ReceiveStatus";
		public static final String PUSHTIME = "PushTime";
		//public static final String NAME = "__Name";
	}
	
	

	public static final class Preview implements BaseColumns {

		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, "MFile");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.guodian.provider.MFile";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.guodian.provider.MFile";

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
		//public static final String FILEFORMAT = "PreviewName";
	}
	
	public static final class ResStr implements BaseColumns {

		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, "ResStr");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.guodian.provider.ResStr";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.guodian.provider.ResStr";

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

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.guodian.provider.ResPoster";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.guodian.provider.ResPoster";

		public static final String OBJECTNAME = "ObjectName";
		public static final String ENTITYID = "EntityID";
		public static final String POSTERID = "PosterID";
		public static final String POSTERNAME = "PosterName";
		public static final String POSTERURI = "PosterURI";
	}

	public static final class ResTrailer implements BaseColumns {

		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, "ResTrailer");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.guodian.provider.ResTrailer";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.guodian.provider.ResTrailer";

		public static final String OBJECTNAME = "ObjectName";
		public static final String ENTITYID = "EntityID";
		public static final String TRAILERID = "TrailerID";
		public static final String TRAILERNAME = "TrailerName";
		public static final String TRAILERURI = "TrailerURI";
	}

	public static final class ResSubTitle implements BaseColumns {

		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, "ResSubTitle");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.guodian.provider.ResSubTitle";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.guodian.provider.ResSubTitle";

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

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.guodian.provider.content";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.guodian.provider.content";

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

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.dbstar.guodian.provider.brand";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.dbstar.guodian.provider.brand";

		public static final String ID = "id";
		public static final String REGIST_DIR = "regist_dir";
		public static final String DOWNLOAD = "download";
		public static final String TOTALSIZE = "totalsize";
		public static final String CNAME = "cname";
	}
}
