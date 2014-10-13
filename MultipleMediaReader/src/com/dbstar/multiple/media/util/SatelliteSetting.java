package com.dbstar.multiple.media.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class SatelliteSetting {
    
    
    private static final String DATA_BASE_FILE_PATH = "/data/dbstar/Dbstar.db";
    public static final String TABLE_NAME = "Global";
    public static final String COLUMN_NMAE = "Name";
    public static final String COLUMN_VALUE = "Value";
    
    public static final int TOTAL_PARAMETERS_COUNT = 5;
    public static final String CMD_POLARIZATION_V = "vertical";
    public static final String CMD_POLARIZATION_H = "horizontal";
    
    public static final String POLARIZATION_V = "<V-垂直>";
    public static final String POLARIZATION_H= "<V-水平>"; 
    
    
    public static final String [] POLARIZATION_ARR = {POLARIZATION_V,POLARIZATION_H};
    
    
    public static final String MODULATION_Q = "QPSK";
    public static final String MODULATION_8= "8PSK";
    public static final String MODULATION_16 = "16PSK";
    public static final String MODULATION_32 = "32PSK";
    
    
    public static String [] MODULATION_ARR = {MODULATION_Q,MODULATION_8,MODULATION_16,MODULATION_32};
    
    
    public static final String SATELLITE= "TunerArgs";
    public static final String SATELLITE_DEFAULT = "TunerArgsDefault";
    public static final String SEPARATOR = "\t";
    
    
    public static final int SERRCH_RATE = 0;
    public static final int SYMBOL_RATE = 1;
    public static final int LOCAL_FREQUENCY = 2;
    public static final int POLARIZATION_MODE = 3;
    public static final int MODULATION_MODE = 4;
    
    
    
    
    private SQLiteDatabase mDatabase;
    
    private static SatelliteSetting mSatelliteSetting;
    
    private SatelliteSetting(){
        
    }
    
    
    public static SatelliteSetting getInstance(){
        if(mSatelliteSetting == null)
            mSatelliteSetting = new SatelliteSetting();
        return mSatelliteSetting;
    }
    
    public SQLiteDatabase getWritableDatabase() {
        return getReadableDatabase();
    }

    public SQLiteDatabase getReadableDatabase() {
        return SQLiteDatabase.openOrCreateDatabase(DATA_BASE_FILE_PATH, null);
    }
    
    
    
    public String queryValue(String columnValue){
        String value = null;
        if(mDatabase == null || !mDatabase.isOpen()){
            mDatabase = getReadableDatabase();
        }
        Cursor cursor = null;
        try {
          cursor =  mDatabase.query(TABLE_NAME, new String[]{COLUMN_VALUE}, COLUMN_NMAE + " = ? ", new String []{columnValue}, null, null, null);
          if(cursor != null){
              cursor.moveToNext();
              value = cursor.getString(0);
          }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(cursor != null)
                cursor.close();
        }
        
        return value;
        
    }
    
    public long insert(String name,String value){
        long rowId = -1;
        if(mDatabase == null || !mDatabase.isOpen()){
            mDatabase = getReadableDatabase();
        }
        String sql =  "REPLACE INTO " + TABLE_NAME + "(" + COLUMN_NMAE + "," + COLUMN_VALUE + " ) VALUES (?,?)" ;
        SQLiteStatement statement;
        try {
            statement = mDatabase.compileStatement(sql);
            statement.bindString(1, name);
            statement.bindString(2, value);
           rowId =  statement.executeInsert();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return rowId;
        
    }
    
    public void onDestroy(){
        if(mDatabase != null){
            mDatabase.close();
            mDatabase = null;
        }
        
        mSatelliteSetting = null;
    }
}
