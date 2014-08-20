package com.dbstar.multiple.media.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastUitl {
    
    private static long mStartTime;
    private static long mLastTime;
    
    
    public static synchronized void showToast(Context context,int contentId){
        mStartTime = System.currentTimeMillis();
        if(mStartTime - mLastTime < 1500){
            return;
        }
        
        mLastTime = mStartTime;
        Toast toast = Toast.makeText(context, context.getString(contentId), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0, 0);
        toast.show();
    }
    public static synchronized  void showToast(Context context,String content){
        mStartTime = System.currentTimeMillis();
        if(mStartTime - mLastTime < 1500){
            return;
        }
        
        mLastTime = mStartTime;
        Toast toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0, 0);
        toast.show();
    }
    
}   
