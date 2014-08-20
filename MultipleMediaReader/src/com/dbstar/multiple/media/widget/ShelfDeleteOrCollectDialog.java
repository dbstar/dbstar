package com.dbstar.multiple.media.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dbstar.multiple.media.shelf.R;

public class ShelfDeleteOrCollectDialog extends Dialog{
    
    private View view;
    private ShelfDeleteOrCollectDialog(Context context) {
        super(context);
    }
    
    private ShelfDeleteOrCollectDialog(Context context, int theme) {
        super(context, theme);
    }
    
    private ShelfDeleteOrCollectDialog(Context context, int theme,android.view.View.OnClickListener listener) {
        super(context, theme);
        view = getLayoutInflater().inflate(R.layout.shelf_dialog, null);
        view.findViewById(R.id.collect).setOnClickListener(listener);
        view.findViewById(R.id.delete).setOnClickListener(listener);
    }
    private ShelfDeleteOrCollectDialog(Context context, int theme,android.view.View.OnClickListener listener,boolean isFavorite,boolean isShowDelete) {
        super(context, theme);
        view = getLayoutInflater().inflate(R.layout.shelf_dialog, null);
        Button delete = (Button) view.findViewById(R.id.delete);
        Button collect = (Button) view.findViewById(R.id.collect);
        if(isShowDelete){
            delete.setVisibility(View.VISIBLE);
            delete.setOnClickListener(listener);
        }else{
            delete.setVisibility(View.GONE);
        }
        
        if(!isFavorite){
            collect.setText(R.string.text_collect_book);
        }
        else{
            collect.setText(R.string.cancel_collection);
        }
        
        collect.setOnClickListener(listener);
        
    }
    public static ShelfDeleteOrCollectDialog getBookInstance(Context context,android.view.View.OnClickListener listener,boolean isFavorite){
            return new ShelfDeleteOrCollectDialog(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen, listener,isFavorite,true);
     
    }
    
    public static ShelfDeleteOrCollectDialog getNewsPaperInstance(Context context,android.view.View.OnClickListener listener,boolean isFavorite){
        return new ShelfDeleteOrCollectDialog(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen, listener,isFavorite,false);
 
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(view);
    }
    
}
