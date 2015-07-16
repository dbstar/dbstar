package com.dbstar.multiple.media.widget;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dbstar.multiple.media.data.NewsPaper;
import com.dbstar.multiple.media.shelf.R;
import com.dbstar.multiple.media.util.ImageUtil;

public class MagazineSubSubCategoryView extends LinearLayout{

	private List<NewsPaper> mData;
    private int mSelectedIndex = 3;
    private OnItemSelectedListener mOnListener;
    private Context mContext;
    
    public MagazineSubSubCategoryView(Context context) {
		super(context);
		this.mContext = context;
		this.setStaticTransformationsEnabled(true);
	}
    
    public MagazineSubSubCategoryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.setStaticTransformationsEnabled(true);
    }
    
    public MagazineSubSubCategoryView(Context context, AttributeSet attrs, int defStyle) {
    	super(context, attrs, defStyle);
    	this.mContext = context;
    	this.setStaticTransformationsEnabled(true);
    }
    
    public void setSelection(int index ){
        mSelectedIndex = index;
    }
    
	public void setData(List<NewsPaper> data){
        this.mData = data;
    }
    
    public List<NewsPaper> getData(){
        return mData;
    }
    
    public static final String ACTION_UP_PIC_CHANGE = "com.dbstar.multiple.media.action.up_pic_change";
	public static final String ACTION_DOWN_PIC_CHANGE = "com.dbstar.multiple.media.action.down_pic_change";
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
    	
    	if (mData == null || mData.size() <= 0) {
    		return true;
    	}
    	
        switch (keyCode) {
        case KeyEvent.KEYCODE_DPAD_DOWN:
            mSelectedIndex = ( ++mSelectedIndex + mData.size())% mData.size();
            notifyDataChanged();
            Intent intent = new Intent();
            intent.putExtra("index", mSelectedIndex);
            intent.setAction(ACTION_DOWN_PIC_CHANGE);
            mContext.sendBroadcast(intent);
            return true;
        case KeyEvent.KEYCODE_DPAD_UP:
            Log.i("MagazineSubSubCategoryView", "----" + mSelectedIndex);
            mSelectedIndex--;
            if(mSelectedIndex == -1){
                mSelectedIndex = mData.size() -1;
            }
            Log.i("MagazineSubSubCategoryView", "++++" + mSelectedIndex);
            notifyDataChanged();
            Intent intent1 = new Intent();
            intent1.putExtra("index", mSelectedIndex);
            intent1.setAction(ACTION_UP_PIC_CHANGE);
            mContext.sendBroadcast(intent1);            
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
//            	Log.i("MagazineSubSubCategoryView", "++++++++size == 1 and " + " i = " + i + ", mSelectedIndex = " + mSelectedIndex);
                if(i != 3)
                    v.setVisibility(View.INVISIBLE);
                else
                    v.setVisibility(View.VISIBLE);
            }else{
                v.setVisibility(View.VISIBLE);
            }
            int index = (Math.abs(size-3) + mSelectedIndex + i) % size;
            Log.i("MagazineSubSubCategoryView", "index = "+ index);
            if( i == 3){
//                v.setImageBitmap(BitmapFactory.decodeFile(mData.get(index).unFocusedIcon));
                v.setImageBitmap(ImageUtil.setDrawable(mData.get(index).PosterPath, 131).getBitmap());
                if(hasFocus()){
                    v.setBackgroundResource(R.drawable.magazine_list_left_focused_bk); 
                }
                else{
                    v.setBackgroundResource(R.drawable.magazine_list_left_unfoused);
                }
                if(mOnListener != null)
                    mOnListener.onSelected(v, mData.get(index));
            }else{
//            	Log.i("MagazineSubSubCategoryView", " ------------v.isShown() = "+ v.isShown());
                if(v.isShown()){
//                    v.setImageBitmap(BitmapFactory.decodeFile(mData.get(index).unFocusedIcon));
                    v.setImageBitmap(ImageUtil.setDrawable(mData.get(index).PosterPath, 131).getBitmap());
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
           
           void onSelected(View v,NewsPaper paper);
       } 
       
}
