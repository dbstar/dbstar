package com.dbstar.util;

import android.util.Log;

public class LogUtil {
    
    public static boolean mStatu = true;
    
    public static void i(String tag,String msg){
        if(mStatu){
            Log.i(tag, msg);
        }
    }
    
}   
