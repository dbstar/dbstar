package com.dbstar.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastUtils {
    
    public static void showToast(Context context, int contentId) {
        Toast toast = Toast.makeText(context, context.getString(contentId), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showToast(Context context, String content) {
        Toast toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
