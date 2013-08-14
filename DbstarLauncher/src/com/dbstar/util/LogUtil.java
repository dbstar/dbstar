package com.dbstar.util;

import android.util.Log;

public class LogUtil {
    
    public static boolean mStatu = false;
    
    public static void i(String tag,String msg){
        if(mStatu){
            Log.i(tag, msg);
        }
    }
    public static void d(String tag,String msg){
        if(mStatu){
            Log.d(tag, msg);
        }
    }
    public static void w(String tag,String msg){
        if(mStatu){
            Log.w(tag, msg);
        }
    }
    public static void w(String tag,String msg,Throwable e){
        if(mStatu){
            Log.w(tag, msg,e);
        }
    }
    public static void e(String tag,String msg,Throwable e){
        if(mStatu){
            Log.e(tag, msg, e);
        }
    }
    public static void e(String tag,String msg){
        if(mStatu){
            Log.e(tag, msg);
        }
    }
}   
