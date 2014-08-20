package com.dbstar.multiple.media.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;

public class DisplayUtil {
    
    public static int dp2px(Context context,float dp){
         float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dp * scale + 0.5f);  
    }
    
    public static int px2dP(Context context,float px){
         float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (px / scale + 0.5f);  
    }
    
    public static void getScreenSize(Context context,Point point){
        ((Activity)context).getWindowManager().getDefaultDisplay().getSize(point);
    }
}
