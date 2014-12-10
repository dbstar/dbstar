package com.dbstar.multiple.media.widget;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dbstar.multiple.media.data.NewsPaper;
import com.dbstar.multiple.media.data.NewsPaperCategory;
import com.dbstar.multiple.media.data.NewsPaperMap;
import com.dbstar.multiple.media.shelf.R;
import com.dbstar.multiple.media.util.DateUtil;
import com.dbstar.multiple.media.util.DisplayUtil;

public class NewspaperDateView extends RelativeLayout {

    private TextView mSelectedView;
    private TextView mTopDateView, mMiddleDateView, mBottomDateView;

    private static final int TOP_VIEW = 0;
    private static final int MIDDLE_VIEW = 1;
    private static final int BOTTOM_VIEW = 2;
    private static final int PAGE_SIZE_MAX = 3;
    
    private OnDateViewSelectedListener mSelectedListener;
    private NewsPaperCategory mData;
    
    private int mCurrentPageSize;
    private int mPageCount;
    private NewsPaper mLastNewsPaper;
    public NewspaperDateView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mTopDateView = (TextView) getChildAt(0);
        mMiddleDateView = (TextView) getChildAt(1);
        mBottomDateView = (TextView) getChildAt(2);
        
    }

    
    public void setData(NewsPaperCategory category){
        this.mData = category;
        if(category.mPages == null){
            constructPage();
        }
        if(mData.mPages != null)
            mPageCount = mData.mPages.size();
        if(mData.getCurrentPageMap() != null)
            mCurrentPageSize = mData.getCurrentPageMap().getSize();
        updateDateValue();
        updateChildViewBg();
    }
    public void setOnSelectedListener(OnDateViewSelectedListener listener){
        this.mSelectedListener = listener;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

    	if (mData == null) {
    		return true;
    	}
    	
        switch (keyCode) {
        case KeyEvent.KEYCODE_DPAD_DOWN:
//        	Log.d("NewspaperDateView", "---------mData.mPostion = " + mData.mPostion + ", mCurrentPageSize = " + mCurrentPageSize);
//        	Log.d("NewspaperDateView", "---------mData.mCurrentPageIndex = " + mData.mCurrentPageIndex + ", mPageCount = " + mPageCount);
            if (mData.mPostion < mCurrentPageSize - 1) {
                mData.mPostion++;
                updateChildViewBg();
            } else if (mData.mPostion == mCurrentPageSize-1) {
                if(mData.mCurrentPageIndex < mPageCount-1){
                    mData.mCurrentPageIndex ++;
                    mData.mPostion = TOP_VIEW;
                    updateDateValue();
                    updateChildViewBg();
                }
            }
            break;
        case KeyEvent.KEYCODE_DPAD_UP:
            if (mData.mPostion > TOP_VIEW) {
                mData.mPostion--;
                updateChildViewBg();
            } else if (mData.mPostion == TOP_VIEW) {
                if(mData.mCurrentPageIndex > 0){
                    mData.mCurrentPageIndex --;
                    mData.mPostion = BOTTOM_VIEW;
                    updateDateValue();
                    updateChildViewBg();
                }
            }
            break;

        default:
            break;
        }
      
        return super.onKeyDown(keyCode, event);
    }

    private void updateChildViewBg() {
    	if (mData == null)
    		return;
    	NewsPaper newsPaper = mData.getCurrentNewsPaper();
    	if(newsPaper != null && newsPaper != mLastNewsPaper){
    		if(mSelectedListener != null){
                mSelectedListener.onSelected(newsPaper);
            }
    		mLastNewsPaper = newsPaper;
        }
    	
    	if (newsPaper != null) {    		
    		if(!isShown())
    			return;
    		TextView v;
    		for (int i = 0; i < getChildCount(); i++) {
    			v = (TextView) getChildAt(i);
    			if(!v.isShown())
    				continue;
    			if (i == mData.mPostion && hasFocus()) {
    				if (i == TOP_VIEW) {
    					v.setBackgroundResource(R.drawable.newspaper_date_btn_focused_1);
    				} else {
    					v.setBackgroundResource(R.drawable.newspaper_date_btn_focused_2);
    				}
    				v.setPadding(0, 0, getPx(7), 0);
    			} else if (i == mData.mPostion && !hasFocus()) {
    				if (i == TOP_VIEW) {
    					v.setBackgroundResource(R.drawable.newspaper_date_btn_selected_1);
    				} else {
    					v.setBackgroundResource(R.drawable.newspaper_date_btn_selected_2);
    				}
    				v.setPadding(0, 0, getPx(7), 0);
    			} else {
    				if (i == TOP_VIEW) {
    					v.setBackgroundResource(R.drawable.newspaper_date_btn_normal_1);
    				} else {
    					v.setBackgroundResource(R.drawable.newspaper_date_btn_normal_2);
    				}
    				v.setPadding(0, 0, getPx(2), 0);
    			}
    			
    		}
    	}
        
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        updateChildViewBg();
    }

    
    private void constructPage(){
        List<NewsPaper> papers = this.mData.NewsPapers;
        int size;
        if(papers == null){
            mPageCount = 0;
            mCurrentPageSize = 0;
            return;
        }
        else 
            size = papers.size();
        
        mData.mPages = new ArrayList<NewsPaperMap>();
        mData.mCurrentPageIndex = 0;
        
        mPageCount = (size + PAGE_SIZE_MAX -1) / PAGE_SIZE_MAX;
        NewsPaperMap page;
        int pageSize;
        int index = 0;
        NewsPaper paper;
        String key = null;
        for(int  i = 0;i< mPageCount ;i++){
//            Log.d("NewspaperDateView", "----------size = " + size + ", i = " + i  + ", size - i = " + (size - i));
            pageSize = Math.min(PAGE_SIZE_MAX, size - i*PAGE_SIZE_MAX);
//            Log.d("NewspaperDateView", "----------mPageCount = " + mPageCount + ", pageSize = " + pageSize);
            page = new NewsPaperMap(pageSize);
            for(int j = 0 ;j < pageSize; j++){
//            	Log.d("NewspaperDateView", "----------index = " + index + ", and papers.size() = " + papers.size());
            	if (index < papers.size()) {
//            		Log.d("NewspaperDateView", "----------index = " + index);
            		paper = papers.get(index);
            		Date date = DateUtil.getDate(paper.PublishTime, DateUtil.FORMART3);
            		if(date != null){
            			key = DateUtil.getStringFromDate(date, DateUtil.FORMART1);
            		}
            		
            		if(key == null)
            			key = "0\n月\n0\n日\n";
            		page.put(key, paper);
            		index ++;
            	}
            }
            mData.mPages.add(page);
        }
    }
    private void updateDateValue(){
        if(mData.mPages == null || mData.mPages.isEmpty()){
            mTopDateView.setText(null);
            mMiddleDateView.setText(null);
            mBottomDateView.setText(null);
            if(isShown())
                setVisibility(View.INVISIBLE);
        }else{
            if(!isShown()){
                setVisibility(View.VISIBLE);
            }
        }
        String [] dateValues =  mData.mPages.get(mData.mCurrentPageIndex).getDateValues();
        
        TextView v;
        for(int i = 0 ,count = dateValues.length ;i < PAGE_SIZE_MAX ;i++){
            v = (TextView) getChildAt(i);
            if(i < count){
                v.setText(dateValues[i]);
                if(!v.isShown())
                    v.setVisibility(View.VISIBLE);
            }else{
                v.setText(null);
                v.setVisibility(View.INVISIBLE);
            }
                
        }
    }
    
    
    
    public interface OnDateViewSelectedListener{
        
        void onSelected(NewsPaper paper);
    }
    private int getPx(int value) {
        return DisplayUtil.dp2px(getContext(), value);
    }
}
