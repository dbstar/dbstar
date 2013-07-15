package com.dbstar.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dbstar.R;
import com.dbstar.guodian.app.base.GDSmartActivity;

public class GDLoadingDialogView extends Dialog {

    private View contentView;
    private boolean isShown = false;
    private View mErroPageView;
    private View mNoNetWorKView;
    private View mLoadingView;
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
                dialog.mErroPageView = dialog.contentView.findViewById(R.id.load_error_page);
                dialog.mLoadingView = dialog.contentView.findViewById(R.id.loading_page);
                dialog.mNoNetWorKView = dialog.contentView.findViewById(R.id.network_error_page);
                dialog.setContentView(dialog.contentView);
            } else {
                dialog.setContentView(dialog.contentView);
            }
            dialog.setCancelable(false);
            dialog.setOnCancelListener(new OnCancelListener() {
                
                @Override
                public void onCancel(DialogInterface dialog) {
                    GDSmartActivity activity = (GDSmartActivity) context;
                    dialog.dismiss();
                    activity.finish();
                }
            });
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
    
    public void showLoadErrorInfo(int resID){
        if(mLoadingView != null)
            mLoadingView.setVisibility(View.INVISIBLE);
        if(mNoNetWorKView != null)
            mNoNetWorKView.setVisibility(View.INVISIBLE);
        if(mErroPageView != null){
            mErroPageView.setVisibility(View.VISIBLE);
            TextView textView = (TextView) mErroPageView.findViewById(R.id.tv_error_info);
            textView.setText(resID);
        }
        
    }
    public void showLoadErrorInfo(String info){
        if(mLoadingView != null)
            mLoadingView.setVisibility(View.INVISIBLE);
        if(mNoNetWorKView != null)
            mNoNetWorKView.setVisibility(View.INVISIBLE);
        if(mErroPageView != null){
            mErroPageView.setVisibility(View.VISIBLE);
            TextView textView = (TextView) mErroPageView.findViewById(R.id.tv_error_info);
            textView.setText(info);
        }
        
    }
    public void showNetWorkErrorInfo(){
        if(mLoadingView != null)
            mLoadingView.setVisibility(View.INVISIBLE);
        if(mNoNetWorKView != null)
            mNoNetWorKView.setVisibility(View.VISIBLE);
        if(mErroPageView != null){
            mErroPageView.setVisibility(View.INVISIBLE);
        }
    }
    
    public void showLoadingInfo(){
        if(mLoadingView != null)
            mLoadingView.setVisibility(View.VISIBLE);
        if(mNoNetWorKView != null)
            mNoNetWorKView.setVisibility(View.INVISIBLE);
        if(mErroPageView != null){
            mErroPageView.setVisibility(View.INVISIBLE);
        }
    }
}
