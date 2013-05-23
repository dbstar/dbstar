package com.dbstar.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    
    public static void showToast(Context context,int contentId){
        Toast.makeText(context, context.getString(contentId), Toast.LENGTH_SHORT).show();
    }
    public static void showToast(Context context,String content){
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }
}
