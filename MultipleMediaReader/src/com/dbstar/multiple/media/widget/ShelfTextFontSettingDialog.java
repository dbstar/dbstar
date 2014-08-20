package com.dbstar.multiple.media.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnFocusChangeListener;

import com.dbstar.multiple.media.shelf.R;

public class ShelfTextFontSettingDialog extends Dialog{
    
    private View view;
    private ShelfTextFontSettingDialog mInstance;
    private ShelfTextFontSettingDialog(Context context) {
        super(context);
    }
    
    private ShelfTextFontSettingDialog(Context context, int theme) {
        super(context, theme);
    }
    
    private ShelfTextFontSettingDialog(Context context, int theme,android.view.View.OnClickListener listener,OnFocusChangeListener focusChangeListener,OnCancelListener cancelListener,final int focus) {
        super(context, theme);
        view = getLayoutInflater().inflate(R.layout.text_font_setting_view, null);
        view.findViewById(R.id.big).setOnClickListener(listener);
        view.findViewById(R.id.middle).setOnClickListener(listener);
        view.findViewById(R.id.small).setOnClickListener(listener);
        
        view.findViewById(R.id.big).setOnFocusChangeListener(focusChangeListener);
        view.findViewById(R.id.middle).setOnFocusChangeListener(focusChangeListener);
        view.findViewById(R.id.small).setOnFocusChangeListener(focusChangeListener);
        view.post(new Runnable() {
            @Override
            public void run() {
                view.findViewById(focus).requestFocus();
                
            }
        });
        setOnCancelListener(cancelListener);
    }
    public static ShelfTextFontSettingDialog getInstance(Context context,android.view.View.OnClickListener listener,OnFocusChangeListener focusChangeListener,OnCancelListener cancelListener,int focus){
            return new ShelfTextFontSettingDialog(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen, listener,focusChangeListener,cancelListener,focus);
     
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(view);
    }
    
}
