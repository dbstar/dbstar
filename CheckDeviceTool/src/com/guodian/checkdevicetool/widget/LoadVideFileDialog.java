package com.guodian.checkdevicetool.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.guodian.checkdevicetool.R;

public class LoadVideFileDialog extends Dialog{

    
    private View view;
    private LoadVideFileDialog(Context context) {
        super(context);
    }
    
    private LoadVideFileDialog(Context context, int theme) {
        super(context, theme);
    }
    
    private LoadVideFileDialog(Context context, int theme,android.view.View.OnClickListener listener) {
        super(context, theme);
        view = getLayoutInflater().inflate(R.layout.loading_device_dialog, null);
    }
    public static LoadVideFileDialog getInstance(Context context,android.view.View.OnClickListener listener){
            return new LoadVideFileDialog(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen, listener);
     
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(view);
    }
    
}
