package com.dbstar.multiple.media.common;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.dbstar.multiple.media.data.VoiceBookPageInfo;
import com.dbstar.multiple.media.data.VoicedBook;
import com.dbstar.multiple.media.model.ModelVoicedBook;

public class GDBHelper extends SQLiteOpenHelper{

    private static final String BASE_NAME = "multiplyplayer_records";
    
    private static GDBHelper mDbHelper;
    
    private static final String CREATE_NEWSPAPER_CATEGORY_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + ModelVoicedBook.VOICEDBOOK_TABLE_NAME +
            " ( " + ModelVoicedBook.BOOK_ID + " TEXT NOT NULL," + ModelVoicedBook.PAGE_ID + " TEXT NOT NULL," + ModelVoicedBook.PAGE_IMAGES + 
            " TEXT," +  ModelVoicedBook.PAGE_AUDIOS + " TEXT," + ModelVoicedBook.PAGE_LABEL + " TEXT NOT NULL, " + ModelVoicedBook.PAGE_LEVEL + 
            " TEXT NOT NULL," + ModelVoicedBook.PAGE_TITLE + " TEXT," + ModelVoicedBook.PAGE_ORDER + " integer,"+ "PRIMARY KEY (" +ModelVoicedBook.BOOK_ID  + "," + ModelVoicedBook.PAGE_ID +") ) ";
    
    public GDBHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    
    
    private GDBHelper(Context context) {
        super(context, BASE_NAME, null, 1);
    }
    
    public static synchronized GDBHelper getInstance(Context context){
        if(mDbHelper == null)
            mDbHelper = new GDBHelper(context);
        return mDbHelper;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_NEWSPAPER_CATEGORY_TABLE_SQL);
    }
     
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        
    }
    
    public void insertVoicedBookInfo(VoicedBook book){
        String sql = "REPLACE INTO " + ModelVoicedBook.VOICEDBOOK_TABLE_NAME + " ( " + ModelVoicedBook.BOOK_ID + "," + ModelVoicedBook.PAGE_ID + ","
                    + ModelVoicedBook.PAGE_IMAGES + "," +  ModelVoicedBook.PAGE_AUDIOS + "," + ModelVoicedBook.PAGE_LABEL + "," 
                    + ModelVoicedBook.PAGE_LEVEL + "," +  ModelVoicedBook.PAGE_TITLE  + "," +  ModelVoicedBook.PAGE_ORDER + " ) "
                    + " VALUES (?,?,?,?,?,?,?,?)";
        SQLiteDatabase db = null;
        try {
            db = getReadableDatabase();
            SQLiteStatement mInsert = db.compileStatement(sql);
            db.beginTransaction();
            
            
            for(VoiceBookPageInfo info : book.mPages){
                
               bindString(mInsert, 1, book.BookId);
               bindString(mInsert, 2, info.PageId);
               bindString(mInsert, 3, info.Image);
               bindString(mInsert, 4, info.combineAudios());
               bindString(mInsert, 5, info.Label);
               bindString(mInsert, 6, info.Level);
               bindString(mInsert, 7, info.Title);
               bindString(mInsert, 8, info.Order);
               
               
               mInsert.executeInsert();
            }
            
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            db.endTransaction();
        }
        
        
    }
    
    public VoicedBook getVoicedBookInfo(String bookId){
//        String sql = "SELECT " + VOICED_BOOK_ID + "," + VOICED_BOOK_PAGE_ID + "," + VOICED_BOOK_PAGE_IMAGES +"," + VOICED_BOOK_PAGE_AUDIOS +","
//                    + VOICED_BOOK_PAGE_LABEL + "," + VOICED_BOOK_PAGE_LEVEL+ "," + VOICED_BOOK_PAGE_TITLE + "," + VOICED_BOOK_PAGE_ORDER + 
//                " FROM " + VOICED_BOOK_TALBE_NAME + " WHERE " + VOICED_BOOK_ID  + " = ? ORDER BY " + VOICED_BOOK_PAGE_ORDER ;
        
        SQLiteDatabase db = null;
        Cursor cursor = null;
        VoicedBook book = new VoicedBook();
        book.mPages = new ArrayList<VoiceBookPageInfo>();
        book.BookId = bookId;
        try {
            db = getReadableDatabase();
            
            VoiceBookPageInfo info;
             cursor = db.query(ModelVoicedBook.VOICEDBOOK_TABLE_NAME, ModelVoicedBook.Query.COLUMNS, ModelVoicedBook.Query.SELECTION, new String[]{bookId}, null, null, ModelVoicedBook.Query.ORDERBY);
            
            while (cursor.moveToNext()) {
                info = new VoiceBookPageInfo();
                
                info.PageId = cursor.getString(ModelVoicedBook.Query.PAGE_ID);
                info.Image = cursor.getString(ModelVoicedBook.Query.PAGE_IMAGES);
                info.Audio = cursor.getString(ModelVoicedBook.Query.PAGE_AUDIOS);
                info.Label = cursor.getString(ModelVoicedBook.Query.PAGE_LABLE);
                info.departAudios(info.Audio);
                info.Level = cursor.getString(ModelVoicedBook.Query.PAGE_LEVEL);
                info.Title = cursor.getString(ModelVoicedBook.Query.PAGE_TITLE);
                info.PageIndex = cursor.getPosition();
                book.mPages.add(info);
            }
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            
            if(cursor != null)
                cursor.close();
        }
        return book;
    }
    
    public int updateLabel(String label,String pageId,String bookId){
        SQLiteDatabase db = null;
        try {
            db = getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put(ModelVoicedBook.PAGE_LABEL, label);
            return db.update(ModelVoicedBook.VOICEDBOOK_TABLE_NAME, values, ModelVoicedBook.BOOK_ID + " = ? AND " + ModelVoicedBook.PAGE_ID+ " = ?", new String[]{bookId,pageId});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    private  void bindString(SQLiteStatement statement, int index, String value) {
        if (statement != null) {
            if (value != null) {
                statement.bindString(index, value);
            } else {
                statement.bindNull(index);
            }
        }
    }
}
