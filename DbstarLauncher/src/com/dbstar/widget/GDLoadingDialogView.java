package com.dbstar.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class GDLoadingDialogView extends Dialog {

    private View contentView;
    private boolean isShown = false;
    public GDLoadingDialogView(Context context, int theme) {
        super(context, theme);
       // this.getWindow().setWindowAnimations(R.style.ContentOverlay);
    }

    public GDLoadingDialogView(Context context) {
        super(context);

    }

    /**
     * Helper class for creating a custom dialog
     */
    public static class Builder {

        private final Context context;

        private int layoutResId;

        private GDLoadingDialogView dialog;

        public Builder(Context context, int layoutResId) {
            this.context = context;
            this.layoutResId = layoutResId;
        }

        public Builder(Context context, RelativeLayout view) {
            this.context = context;
        }

        /**
         * Create the custom dialog
         */
        public GDLoadingDialogView create() {
            if (dialog == null)
                dialog = new GDLoadingDialogView(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
            if (dialog.contentView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                dialog.contentView = inflater.inflate(layoutResId, null);
                dialog.contentView.setBackgroundColor(Color.parseColor("#00000000"));
                dialog.setContentView(dialog.contentView);
            } else {
                dialog.setContentView(dialog.contentView);
            }
            dialog.setCancelable(false);
            return dialog;
        }

    }

    public void ShowDialog() {
        show();
        isShown = true;
    }
    
    public void hideDialog() {
        hide();
        isShown= false;
    }
    
    public boolean  isShowing(){
        return isShown;
    }
}
