package com.dbstar.multiple.media.widget;

import java.util.List;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dbstar.multiple.media.data.NewsPaperCategory;
import com.dbstar.multiple.media.shelf.R;
import com.dbstar.multiple.media.util.ImageUtil;

public class NewsPaperSubCategoryView extends LinearLayout{
    
    private List<NewsPaperCategory> mData;
    private int mSelectedIndex = 3;
    private OnItemSelectedListener mOnListener;
    public NewsPaperSubCategoryView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public void setSelection(int index ){
        mSelectedIndex = index;
    }
    
    public void setData(List<NewsPaperCategory> data){
        this.mData = data;
    }
    
    public List<NewsPaperCategory> getData(){
        return mData;
    }
    
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
    	
    	if (mData == null || mData.size() <= 0) {
    		return true;
    	}
    	
        switch (keyCode) {
        case KeyEvent.KEYCODE_DPAD_DOWN:
            mSelectedIndex = ( ++mSelectedIndex + mData.size())% mData.size();
            notifyDataChanged();
            return true;

        case KeyEvent.KEYCODE_DPAD_UP:
            Log.i("NewsPaperSubCategoryView", "----" + mSelectedIndex);
            mSelectedIndex--;
            if(mSelectedIndex == -1){
                mSelectedIndex = mData.size() -1;
            }
            Log.i("NewsPaperSubCategoryView", "++++" + mSelectedIndex);
            notifyDataChanged();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_DPAD_DOWN:
            return true;

        case KeyEvent.KEYCODE_DPAD_UP:
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    public void notifyDataChanged(){
        ImageView v ;
        if(mData == null || mData.isEmpty()){
            for(int i = 0,count = getChildCount();i< count ;i ++){
                v = (ImageView) getChildAt(i);
                v.setBackgroundDrawable(null);
                v.setImageBitmap(null);
        }
            return;
        }
        
        int size = mData.size();
        for(int i = 0,count = getChildCount();i< count ;i ++){
            v = (ImageView) getChildAt(i);
            if(size == 1){
                if(i != 3)
                    v.setVisibility(View.INVISIBLE);
                else
                    v.setVisibility(View.VISIBLE);
            }else{
                v.setVisibility(View.VISIBLE);
            }
            int index = (Math.abs(size-3) + mSelectedIndex + i) % size;
            Log.i("NewsPaperSubCategoryView", "index = "+ index) ;
            if( i == 3){
//                v.setImageBitmap(BitmapFactory.decodeFile(mData.get(index).unFocusedIcon));
                v.setImageBitmap(ImageUtil.setDrawable(mData.get(index).unFocusedIcon, 195).getBitmap());
                if(hasFocus()){
                    v.setBackgroundResource(R.drawable.newspaper_header_focus_bg); 
                }
                else{
                    v.setBackgroundResource(R.drawable.newspaper_header_bg);
                }
                if(mOnListener != null)
                    mOnListener.onSelected(v, mData.get(index));
            }else{
                if(v.isShown()){
//                    v.setImageBitmap(BitmapFactory.decodeFile(mData.get(index).unFocusedIcon));
                    v.setImageBitmap(ImageUtil.setDrawable(mData.get(index).unFocusedIcon, 195).getBitmap());
                    v.setBackgroundDrawable(null);
                }
            }
//            if(i < mSelectedIndex){
//            }else if(i == mSelectedIndex){
//                v.setBackgroundResource(mData.get(mSelectedIndex).id);
//            }else {
//                v.setBackgroundResource(mData.get((mSelectedIndex - 3) + i).id);
//            }
        }
        
        
    }
    
       @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        
    /*    int size = mData.size();
        if(size == 0)
            return;
        int index = (Math.abs(size-3) + mSelectedIndex + 3) % size;
        ImageView v = (ImageView) getChildAt(3);
        if(gainFocus){
            v.setBackgroundResource(R.drawable.newspaper_header_focus_bg);
            v.setImageBitmap(BitmapFactory.decodeFile(mData.get(index).FocusedIcon));
            
        }else{
            v.setBackgroundResource(R.drawable.newspaper_header_bg);
            v.setImageBitmap(BitmapFactory.decodeFile(mData.get(index).unFocusedIcon));
        }*/
        notifyDataChanged();
    }
       public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
           this.mOnListener = onItemSelectedListener;
       }
       
       public interface OnItemSelectedListener{
           
           void onSelected(View v,NewsPaperCategory category);
       } 
       
}
