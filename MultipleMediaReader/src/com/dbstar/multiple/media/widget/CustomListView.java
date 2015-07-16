package com.dbstar.multiple.media.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.dbstar.multiple.media.shelf.R;

public class CustomListView extends ListView{
    
    public int mLastSelectedIndex;
    private int mFromTop;
    private OnItemSelectedListener mItemSelectedListener;
    public CustomListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
    }

     
    public void setSelectLastIndex(int index,int y){
        mLastSelectedIndex = index;
        mFromTop = y;
    }
    @Override
    public void setOnItemSelectedListener(android.widget.AdapterView.OnItemSelectedListener listener) {
        mItemSelectedListener = listener;
        super.setOnItemSelectedListener(listener);
    }
    @Override
    public void setSelectionFromTop(int position, int y) {
        super.setSelectionFromTop(mLastSelectedIndex, mFromTop);
        if(mItemSelectedListener != null)
            mItemSelectedListener.onItemSelected(null, null, mLastSelectedIndex, mLastSelectedIndex);
        if(isFocused()){
            post(new Runnable() {
                
                @Override
                public void run() {
                    View v;
                    if(( v = getSelectedView() ) != null){
                        TextView textView =  (TextView)getSelectedView().findViewById(R.id.category_name);
                        textView.setTextColor(Color.WHITE);
                        textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    }
                }
            });
           
        }
    }
    
    public void requestSelectItem(){
        setSelectionFromTop(0,0);
    }
}
