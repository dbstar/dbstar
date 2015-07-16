package com.dbstar.model;

import java.io.File;

import com.dbstar.util.LogUtil;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.dbstar.util.LogUtil;

public class MultipleLanguageInfoProvider extends ContentProvider {

 private static final String URI_AUTHORITY = "com.dbstar.multipleLanguageInfo.provider";
    
    //--------------------------------------------
    private static final String ACTION_LOAD_NEWSPAPER_CATEGORIES = "LoadNewsPaperCategories";
    private static final String ACTION_LOAD_NEWSPAPERS = "LoadNewsPapers";
    private static final String ACTION_LOAD_MAGAZINES = "LoadMagazines";
    private static final String ACTION_COLLECT_NEWSPAPER = "CollectNewsPaper";
    private static final String ACTION_CANCEL_COLLECT_NEWSPAPER = "CancelCollectNewsPaper";
    private static final String ACTION_ADD_NEWSPAPER_TO_PERSONAL_PREFERENCE = "AddNewsPaperToPersonalPreference";
    private static final String ACTION_REMOCE_NEWSPAPER_FROM_PERSONAL_PREFERENCE = "RemoveNewsPaperFromPersonalPreference";
    private static final String ACTION_LOAD_ALL_NEWSPAPERS = "LoadAllNewsPapers";
    private static final String ACTION_LOAD_COLLECTED_NEWSPAPER_CATEGORIES = "LoadCollectedNewsPaperCategories";
    private static final String ACTION_LOAD_COLLECTED_NEWSPAPERS = "LoadCollectedNewsPapers";
    
    
    private static final String ACTION_LOAD_BOOK_CATEGORIES = "LoadBookCategories";
    private static final String ACTION_LOAD_BOOKS = "LoadBooks";
    private static final String ACTION_DELETE_BOOK = "DeleteBook";
    private static final String ACTION_COLLECT_BOOK = "CollectBook";
    private static final String ACTION_CANCEL_COLLECT_BOOK = "CancelCollectBook";
    private static final String ACTION_LOAD_ALL_COLLECTED_BOOKS = "LoadAllCollectedBooks";
    private static final String ACTION_LOAD_ALL_BOOKS = "LoadAllBooks";
    
    
    private static final String COLUMN_TABLE_NAME = "Column";
    private static final String PUBLICATION_TABLE_NAME = "Publication";
    private static final String MULTIPLELANGUAGEINFORM_TABLE_NAME = "MultipleLanguageInfoRM";
    private static final String RESSTR_TABLE_NAME = "ResStr";
    private static final String RESPOSTER_TABLE_NAME = "ResPoster";
    private static final String GLOBAL_TABLE_NAME = "Global";
    
    
    // private static final String NEWSPAPER_SUB_CATEGORY_TABLE =
    // "NewspaperSubCategorys";

    private static final int BOOK_COLLECTION = 0x01;
    private static final int BOOK_item = 0x02;

 
    
    private static final int LOAD_NEWSPAPER_CATEGORIES = 0X100000;
    private static final int LOAD_NEWSPAPERS = 0X100001;
    private static final int LOAD_BOOK_CATEGORIES = 0X100002;
    private static final int LOAD_BOOKS = 0X100003;
    private static final int CANCEL_NEWSPAER_COLLECTION = 0X100004;
    private static final int COLLECT_NEWSPAPER = 0X100005;
    private static final int DELETE_BOOK = 0X100006;
    private static final int CANCEL_BOOK_COLLECTION = 0X100007;
    private static final int COLLECT_BOOK = 0X100008;
    private static final int LOAD_ALL_FAVORITE_BOOK = 0X100009;
    private static final int LOAD_ALL_BOOKS = 0X100010;
    private static final int LOAD_ALL_NEWSPAPERS = 0X100011;
    private static final int ADD_NEWSPAPER_TO_PERSONAL_PREFERENCE = 0X100012;
    private static final int REMOVE_NEWSPAPER_FROM_PERSONAL_PREFERENCE = 0X100013;
    private static final int LOAD_COLLECTED_NEWSPAPER_CATEGORIES = 0X100014;
    private static final int LOAD_COLLECTED_NEWSPAPERS = 0X100015;
    private static final int LOAD_MAGAZINE = 0X100016;
    
    private static String mPushDir = "/storage/external_storage/sda1";
    private static String mCurLanguage= "cho";
    // private static final int NEWSPAPER_SUB_CATEGORY_COLLECTION = 0x07;
    // private static final int NEWSPAPER_SUB_CATEGORY_SINGLE = 0x08;

    private static final UriMatcher mUriMathcer;
    
    private GDSystemConfigure mConfigure = null;
    

    static {
        mUriMathcer = new UriMatcher(UriMatcher.NO_MATCH);
//        mUriMathcer.addURI(URI_AUTHORITY, BOOOK_TABLE, BOOK_COLLECTION);
//        mUriMathcer.addURI(URI_AUTHORITY, BOOOK_TABLE + "/#", BOOK_item);
//        mUriMathcer.addURI(URI_AUTHORITY, BOOOK_CATEGORY_TABLE, BOOK_CATEGORY_COLLECTION);
//        mUriMathcer.addURI(URI_AUTHORITY, BOOOK_CATEGORY_TABLE + "/#", BOOK_CATEGORY_item);
//
//        mUriMathcer.addURI(URI_AUTHORITY, NEWSPAPER_CATEGORY_TABLE, NEWSPAPER_CATEGORY_COLLECTION);
//        mUriMathcer.addURI(URI_AUTHORITY, NEWSPAPER_CATEGORY_TABLE + "/#", NEWSPAPER_CATEGORY_item);
//        
//        mUriMathcer.addURI(URI_AUTHORITY, NEWSPAPER_TABLE, NEWSPAPER_COLLECTION);
//        mUriMathcer.addURI(URI_AUTHORITY, NEWSPAPER_TABLE + "/#", NEWSPAPER_item);
        
        //----------------------------------------------
        
        mUriMathcer.addURI(URI_AUTHORITY, ACTION_COLLECT_NEWSPAPER , COLLECT_NEWSPAPER);
        mUriMathcer.addURI(URI_AUTHORITY, ACTION_LOAD_NEWSPAPER_CATEGORIES , LOAD_NEWSPAPER_CATEGORIES);
        mUriMathcer.addURI(URI_AUTHORITY, ACTION_LOAD_NEWSPAPERS , LOAD_NEWSPAPERS);
        mUriMathcer.addURI(URI_AUTHORITY, ACTION_LOAD_MAGAZINES, LOAD_MAGAZINE);
        mUriMathcer.addURI(URI_AUTHORITY, ACTION_LOAD_BOOK_CATEGORIES , LOAD_BOOK_CATEGORIES);
        mUriMathcer.addURI(URI_AUTHORITY, ACTION_LOAD_BOOKS , LOAD_BOOKS);
        mUriMathcer.addURI(URI_AUTHORITY, ACTION_CANCEL_COLLECT_NEWSPAPER , CANCEL_NEWSPAER_COLLECTION);
        mUriMathcer.addURI(URI_AUTHORITY, ACTION_DELETE_BOOK , DELETE_BOOK);
        mUriMathcer.addURI(URI_AUTHORITY, ACTION_COLLECT_BOOK , COLLECT_BOOK);
        mUriMathcer.addURI(URI_AUTHORITY, ACTION_CANCEL_COLLECT_BOOK , CANCEL_BOOK_COLLECTION);
        mUriMathcer.addURI(URI_AUTHORITY, ACTION_LOAD_ALL_COLLECTED_BOOKS , LOAD_ALL_FAVORITE_BOOK);
        mUriMathcer.addURI(URI_AUTHORITY, ACTION_LOAD_ALL_BOOKS , LOAD_ALL_BOOKS);
        mUriMathcer.addURI(URI_AUTHORITY, ACTION_LOAD_ALL_NEWSPAPERS, LOAD_ALL_NEWSPAPERS);
        mUriMathcer.addURI(URI_AUTHORITY, ACTION_ADD_NEWSPAPER_TO_PERSONAL_PREFERENCE, ADD_NEWSPAPER_TO_PERSONAL_PREFERENCE);
        mUriMathcer.addURI(URI_AUTHORITY, ACTION_REMOCE_NEWSPAPER_FROM_PERSONAL_PREFERENCE, REMOVE_NEWSPAPER_FROM_PERSONAL_PREFERENCE);
        mUriMathcer.addURI(URI_AUTHORITY, ACTION_LOAD_COLLECTED_NEWSPAPER_CATEGORIES, LOAD_COLLECTED_NEWSPAPER_CATEGORIES);
        mUriMathcer.addURI(URI_AUTHORITY, ACTION_LOAD_COLLECTED_NEWSPAPERS, LOAD_COLLECTED_NEWSPAPERS);
    }

    @Override
    public boolean onCreate() {
    	GDDataModel dataModel = new GDDataModel();
    	mConfigure = new GDSystemConfigure();
    	dataModel.initialize(mConfigure);
    	
        String Value;
        if((Value = dataModel.queryDeviceGlobalProperty("CurLanguage") )!= null){
            mCurLanguage = Value;
        }
//        if((Value =queryGlobalProperty("ColumnDir"))!= null){
//            mColumnDir = Value;
//        }
        
        
        if((Value = dataModel.queryDeviceGlobalProperty("PushDir")) != null){
            mPushDir = Value;
        }
        
        LogUtil.d("MultipleLanguageInfoProvider", "-------mConfigure.getStorageDir() = " + mConfigure.getStorageDir());
        return true;
    }
    
    private String queryGlobalProperty(String property){
       String sql = "select Value from " + GLOBAL_TABLE_NAME + " where Name = ?";
       //Cursor cursor = getReadableDatabase().rawQuery(sql, new String []{property});
       Cursor cursor =  getReadableDatabase().query(GLOBAL_TABLE_NAME, new String[]{"Value"}, "Name = ?", new String[]{property}, null, null, null);
       if(cursor != null && cursor.moveToNext()){
           return cursor.getString(0);
       }
       return null;
    }
    
    public SQLiteDatabase getWritableDatabase() {
    	SQLiteDatabase database = getReadableDatabase();
        return database;
    }

    public SQLiteDatabase getReadableDatabase() {
//    	String dir = mConfigure.getStorageDir();
//    	LogUtil.d("MultipleLanguageInfoProvider", "dir = " + dir);
    	
//    	// TODO:测试，看看有没有这个文件
//    	File file = new File(mPushDir + "/Dbstar.db");
//    	LogUtil.d("MultipleLanguageInfoProvider", "----------mPushDir = " + mPushDir);
//    	LogUtil.d("MultipleLanguageInfoProvider", "----------file.exists() = " + file.exists());
    	
    	SQLiteDatabase sqLiteDatabase = null;
    	try {
    		LogUtil.d("MultipleLanguageInfoProvider", "-------mPushDir = " + mPushDir);
//			sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(mPushDir + "/Dbstar.db", null);
    		if (mPushDir.startsWith("/data/dbstar")) {    			
    			sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase("/data/dbstar/Dbstar.db", null);
    		} else {    			
    			sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase("/data/dbstar/hd/Dbstar.db", null);
    		}
		} catch (Exception e) {
			LogUtil.d("MultipleLanguageInfoProvider", "dir = " + mPushDir + "////e = " + e);
		}
        return sqLiteDatabase;
    }
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int option = mUriMathcer.match(uri);
        Cursor cursor = null;
        
        if (selectionArgs != null && selectionArgs.length > 0) {        	
        	LogUtil.d("MultipleLanguageInfoProvider", "selectionArgs[0] = " + selectionArgs[0]);
        }
        
        switch (option) {
        case LOAD_BOOKS:{
            String sql = "select p.PublicationID,ColumnID,'"+ mPushDir +"/' || FileURI,Title,'"+ mPushDir +"/' || r.PosterURI,m.Description,m.Author,p.Favorite,m.RMCategory from " +
            		"Publication p ,MultipleLanguageInfoRM m ,ResPoster r" +
            		" where r.EntityID = p.PublicationID and p.Deleted='0' and p.ReceiveStatus='1' and p.FileType!='1' and m.language = '"+ mCurLanguage +"' and p.PublicationID = m.PublicationID and p.ColumnID = ? " + selection ;
            LogUtil.d("MultipleLanguageInfoProvider", "LOAD_BOOKS-----sql = " + sql);
            cursor = getReadableDatabase().rawQuery(sql, selectionArgs);
            break;
        }
        case LOAD_ALL_FAVORITE_BOOK:{
            String sql = "select p.PublicationID,ColumnID,'"+ mPushDir +"/' || FileURI,Title,'"+ mPushDir +"/' || r.PosterURI,m.Description,m.Author,p.Favorite,m.RMCategory from " +
                    "Publication p ,MultipleLanguageInfoRM m ,ResPoster r" +
                    " where r.EntityID = p.PublicationID and p.Deleted='0' and p.FileType!='1' and m.language = '"+ mCurLanguage +"' and p.PublicationID = m.PublicationID and p.Favorite = '1' and p.ColumnID in "+
                    " (select ColumnID From Column where ParentID = ? )";
            
            LogUtil.d("MultipleLanguageInfoProvider", "LOAD_ALL_FAVORITE_BOOK-----sql = " + sql);
            cursor = getReadableDatabase().rawQuery(sql, selectionArgs);
            break;
        }
        case LOAD_BOOK_CATEGORIES:{
            String sql = "select ColumnID,StrValue" +
                    " From Column c,ResStr r " +
                    "WHERE c.ParentID = ? and c.ColumnID = r.EntityID and r.StrLang = '"+ mCurLanguage+ "' and ObjectName= 'Column' and StrName = 'DisplayName'" +
                    " and ColumnID in (select ColumnID from Publication where ReceiveStatus='1' and PublicationType='1')";
            LogUtil.d("MultipleLanguageInfoProvider", "LOAD_BOOK_CATEGORIES-----sql = " + sql);
            cursor = getReadableDatabase().rawQuery(sql, selectionArgs);     
            break;
        }
        case LOAD_ALL_BOOKS:{
            String sql = "select p.PublicationID,ColumnID,'"+ mPushDir +"/' || FileURI,Title,'"+ mPushDir +"/' || r.PosterURI,m.Description,m.Author,p.Favorite,m.RMCategory from " +
                    "Publication p ,MultipleLanguageInfoRM m ,ResPoster r" +
                    " where r.EntityID = p.PublicationID and p.Deleted='0' and p.ReceiveStatus='1' and p.FileType!='1' and m.language = '"+ mCurLanguage +"' and p.PublicationID = m.PublicationID and p.ColumnID in"+
                    " (select ColumnID From Column where ParentID = ? )";
            LogUtil.d("MultipleLanguageInfoProvider", "LOAD_ALL_BOOKS-----sql = " + sql);
            cursor = getReadableDatabase().rawQuery(sql,selectionArgs);
            break;
        }
        case LOAD_NEWSPAPER_CATEGORIES:{
            String sql = "select ColumnID as CategorySon,ParentID as CategoryDad ,StrValue as Name,ColumnIcon_onclick as icon,c.Favorite as preference from Column c, ResStr r where c.ColumnID = r.EntityID and ParentID= ? and r.StrLang = '"+mCurLanguage+"' and ObjectName= 'Column' and StrName = 'DisplayName' " +
            		" union select SetID as CategorySon,ColumnID as CategoryDad ,Title as Name ,'" + mPushDir  + "/' || PosterURI as icon,p.Preference as preference from Publication p,ResPoster rp , MultipleLanguageInfoRM m " +
            		" where rp.EntityID = p.PublicationID and p.PublicationID = m.PublicationID and ColumnID in (select ColumnID from Column where ParentID= ? ) group by SetID ";
            String ParentID = selectionArgs[0];
            LogUtil.d("MultipleLanguageInfoProvider", "LOAD_NEWSPAPER_CATEGORIES-----sql = " + sql + "  (ParentID = " + ParentID + ")");
            cursor = getReadableDatabase().rawQuery(sql, new String []{ParentID,ParentID});           
            break;
        }
        case LOAD_COLLECTED_NEWSPAPER_CATEGORIES:{
            String sql = "select SetID as CategorySon,ColumnID as CategoryDad ,Title as Name ,'" + mPushDir  + "/' || PosterURI as icon,p.Preference as preference" +
                    " from Publication p,ResPoster rp ,MultipleLanguageInfoRM m " +
                    " where p.Favorite = '1' and rp.EntityID = p.PublicationID and p.PublicationID = m.PublicationID and ColumnID in (select ColumnID from Column where ParentID= ?) group by SetID "+
                    " union "+    
                    " select ColumnID as CategorySon,ParentID as CategoryDad ,StrValue as Name,ColumnIcon_onclick as icon,c.Favorite as preference " +
                    " from Column c, ResStr r " +
                    " where c.ColumnID = r.EntityID and ParentID= ? and r.StrLang = '"+mCurLanguage+"' and ObjectName= 'Column' and StrName = 'DisplayName' and ColumnID in (select ColumnID from Publication p  where p.Favorite = '1' and ColumnID in (select ColumnID from Column where ParentID= ?))";
            String ParentID = selectionArgs[0];
            LogUtil.d("MultipleLanguageInfoProvider", "LOAD_COLLECTED_NEWSPAPER_CATEGORIES-----sql = " + sql + "  (ParentID = " + ParentID + ")");
            cursor = getReadableDatabase().rawQuery(sql, new String []{ParentID,ParentID,ParentID});   
            break;
        }
        case LOAD_NEWSPAPERS:{
            String  sql = "select p.PublicationID,ColumnID,'"+ mPushDir +"/' || FileURI,Title,PublishDate,Favorite from Publication p ,MultipleLanguageInfoRM m " +
                    "where  p.Deleted='0' and p.ReceiveStatus='1' and p.FileType!='1' and m.language = '"+ mCurLanguage +"'  and p.PublicationID = m.PublicationID and p.SetID = ? order by PublishDate desc";
            LogUtil.d("MultipleLanguageInfoProvider", "LOADNEWSPAPERS-----sql = " + sql);
            cursor = getReadableDatabase().rawQuery(sql, selectionArgs);
            break;
        }
        case LOAD_MAGAZINE: {
        	String sql = "select PublicationID,'"+ mPushDir +"/' || PosterURI,'"+ mPushDir +"/' || FileURI from Publication,ResPoster where Publication.PublicationID=ResPoster.EntityID and SetID = ?";
        	LogUtil.d("MultipleLanguageInfoProvider", "LOAD_COLLECTED_NEWSPAPER_CATEGORIES-----sql = " + sql + "  (SetID = " + selectionArgs[0] + ")");
        	cursor = getReadableDatabase().rawQuery(sql, selectionArgs);
        	break;
        }
        case LOAD_ALL_NEWSPAPERS:{
            
            String sql = "select p.PublicationID,ColumnID,'"+ mPushDir +"/' || FileURI,Title,PublishDate ,Favorite from Publication p ,MultipleLanguageInfoRM m " +
                    "where  p.Deleted='0' and p.ReceiveStatus='1' and p.FileType!='1' and m.language = '"+ mCurLanguage +"'  and p.PublicationID = m.PublicationID and p.[ColumnID] in (select ColumnID from Column c where c.ParentID = ?) order by PublishDate desc";
            
            LogUtil.d("MultipleLanguageInfoProvider", "LOAD_ALL_NEWSPAPERS-----sql = " + sql);
            cursor = getReadableDatabase().rawQuery(sql, selectionArgs);
            break;
        }
        case LOAD_COLLECTED_NEWSPAPERS:{
            String  sql = "select p.PublicationID,ColumnID,'"+ mPushDir +"/' || FileURI,Title,PublishDate,Favorite from Publication p ,MultipleLanguageInfoRM m " +
                    "where p.Favorite = '1' and p.Deleted='0' and p.FileType!='1' and m.language = '"+ mCurLanguage +"'  and p.PublicationID = m.PublicationID and p.SetID = ? order by PublishDate desc";
            LogUtil.d("MultipleLanguageInfoProvider", "LOAD_COLLECTED_NEWSPAPERS-----sql = " + sql);
            cursor = getReadableDatabase().rawQuery(sql, selectionArgs);
            break;
        }
        default:
            throw new IllegalArgumentException("unknow uri " + uri);
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int option = mUriMathcer.match(uri);
        long id = -1;
        switch (option) {
        case BOOK_COLLECTION:
            if (values != null && values.size() > 0) {

                //id = getReadableDatabase().insert(PlayerDBHepler.BOOK_TABLE_NAME, null, values);
            } else
                throw new NullPointerException("contentvalues is null or empty");
            break;
        default:
            throw new IllegalArgumentException("unknow uri " + uri);
        }
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int option = mUriMathcer.match(uri);
        int id = -1;
        switch (option) {
        case BOOK_item:
           // id = getReadableDatabase().delete(PlayerDBHepler.BOOK_TABLE_NAME, selection, selectionArgs);
            break;

        default:
            throw new IllegalArgumentException("unknow uri " + uri);
        }
        return id;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int option = mUriMathcer.match(uri);
        int id =-1;
        switch (option) {
        case COLLECT_NEWSPAPER:{
            values = new ContentValues();
            values.put("Favorite", "1");
            id = getReadableDatabase().update(PUBLICATION_TABLE_NAME, values, "PublicationID = ?", selectionArgs);
            break;
        }
        case CANCEL_NEWSPAER_COLLECTION:{
            values = new ContentValues();
            values.put("Favorite", "0");
            id = getReadableDatabase().update(PUBLICATION_TABLE_NAME, values, "PublicationID = ?", selectionArgs);
            break;
        }
        case ADD_NEWSPAPER_TO_PERSONAL_PREFERENCE:{
            values = new ContentValues();
            values.put("Preference", "1");
            id = getReadableDatabase().update(PUBLICATION_TABLE_NAME, values, "SetID = ?", selectionArgs);
            break;
        }
        case REMOVE_NEWSPAPER_FROM_PERSONAL_PREFERENCE:{
            values = new ContentValues();
            values.put("Preference", "0");
            id = getReadableDatabase().update(PUBLICATION_TABLE_NAME, values, "SetID = ?", selectionArgs);
            break;
        }
        case DELETE_BOOK:{
            values = new ContentValues();
            values.put("Deleted", "1");
            id = getReadableDatabase().update(PUBLICATION_TABLE_NAME, values, "PublicationID = ?", selectionArgs);
            break;
        }
        case COLLECT_BOOK:{
            values = new ContentValues();
            values.put("Favorite", "1");
            id = getReadableDatabase().update(PUBLICATION_TABLE_NAME, values, "PublicationID = ?", selectionArgs);
            break;
        }
        case CANCEL_BOOK_COLLECTION:{
            values = new ContentValues();
            values.put("Favorite", "0");
            id = getReadableDatabase().update(PUBLICATION_TABLE_NAME, values, "PublicationID = ?", selectionArgs);
            break;
        }
        default:
            throw new IllegalArgumentException("unknow uri " + uri);
        }
        return id;
    }

}
