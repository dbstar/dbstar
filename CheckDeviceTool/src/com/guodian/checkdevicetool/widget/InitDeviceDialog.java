package com.guodian.checkdevicetool.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.guodian.checkdevicetool.R;

public class InitDeviceDialog extends Dialog{

    
    private View view;
    private InitDeviceDialog(Context context) {
        super(context);
    }
    
    private InitDeviceDialog(Context context, int theme) {
        super(context, theme);
    }
    
    private InitDeviceDialog(Context context, int theme,android.view.View.OnClickListener listener) {
        super(context, theme);
        view = getLayoutInflater().inflate(R.layout.init_device_dialog, null);
    }
    public static InitDeviceDialog getInstance(Context context,android.view.View.OnClickListener listener){
            return new InitDeviceDialog(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen, listener);
     
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(view);
    }
    
}
