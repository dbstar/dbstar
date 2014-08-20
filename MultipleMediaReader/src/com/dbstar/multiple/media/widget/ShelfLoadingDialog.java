package com.dbstar.multiple.media.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

public class ShelfLoadingDialog extends Dialog{
    
    private View mContentView;
    
    public ShelfLoadingDialog(Context context, int theme) {
        super(context, theme);
    }
    public ShelfLoadingDialog(Context context) {
        super(context);
    }
    
    
    /**
     * this class can help class to create a custom dialog
     */
    public static class Builder{
        
        private ShelfLoadingDialog dialog;
        private Context context;
        private int layoutId;
        
        public Builder(Context context ,int layoutId) {
            this.context = context;
            this.layoutId = layoutId;
        }
        
        public ShelfLoadingDialog create(){
            if(dialog == null){
               dialog = new ShelfLoadingDialog(context,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
            }
            
            if(dialog.mContentView == null)
                dialog.mContentView = dialog.getLayoutInflater().inflate(layoutId, null);
            
            dialog.setContentView(dialog.mContentView);
            return dialog;
        }
    }
    
    
    
}
