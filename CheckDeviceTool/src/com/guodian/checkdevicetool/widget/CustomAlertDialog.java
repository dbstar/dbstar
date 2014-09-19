package com.guodian.checkdevicetool.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.guodian.checkdevicetool.R;

public class CustomAlertDialog extends Dialog{

    
    private View view;
    private int checkedId;
    private CustomAlertDialog(Context context) {
        super(context);
    }
    
    private CustomAlertDialog(Context context, int theme) {
        super(context, theme);
    }
    
    private CustomAlertDialog(Context context, int theme,android.view.View.OnClickListener listener) {
        super(context, theme);
        view = getLayoutInflater().inflate(R.layout.alert_back_dialog, null);
        view.findViewById(R.id.sure).setOnClickListener(listener);
        view.findViewById(R.id.cancel).setOnClickListener(listener);
        checkedId = R.id.no;
        RadioGroup radioGroup =  (RadioGroup)view.findViewById(R.id.rg_result);
        
        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                CustomAlertDialog.this.checkedId = checkedId;
            }
        });
    }
    public static CustomAlertDialog getInstance(Context context,android.view.View.OnClickListener listener){
            return new CustomAlertDialog(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen, listener);
     
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(view);
    }
    
    public int getCheckedButtonId(){
        return checkedId;
    }
    
}
