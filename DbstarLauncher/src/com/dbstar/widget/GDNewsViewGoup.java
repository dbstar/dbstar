package com.dbstar.widget;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.dbstar.R;
import com.dbstar.widget.GDNewsContentView.OnPageChangeListener;

public class GDNewsViewGoup extends LinearLayout{
    
    private GDNewsContentView mChildContent;
    private ImageView mChildPic;
    private List<String> mUrls;
    private float mSpaceBetweenPicAndCotent;
    private int mPicViewWidth,mPicViweHeight;
    private float mContentWidth,mContentHeight;
    private int mHeight , mWidth;
    private float mLineVSpace,mLineHSpace;
    private int mTextColor;
    private float mTextSize;
    private String mContent;
    private int mCurrentPageNumber = 1;
    private int mPicCount;
    private LinearLayout mLoadingPicLayout;
    
    
    private Map<String, SoftReference<Bitmap>> mCacheBitmaps;
    private float mDensity;
    
    private OnUpdatePageListener mUpdatePageListener;
    
    public interface OnUpdatePageListener {
        void onUpdate(int totalPage,int currentPage);
    }
      
    public void setOnUpdatePageListener(OnUpdatePageListener listener){
        this.mUpdatePageListener = listener;
    }
    public GDNewsViewGoup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public GDNewsViewGoup(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.GDNewsContent);
        mLineHSpace =  a.getDimension(R.styleable.GDNewsContent_hSpace, 0);
        mLineVSpace =  a.getDimension(R.styleable.GDNewsContent_vSpace, dip2px(15));
        mTextColor = a.getColor(R.styleable.GDNewsContent_textColor, Color.WHITE);
        mTextSize =  a.getDimension(R.styleable.GDNewsContent_textSize, 20);
        mSpaceBetweenPicAndCotent = a.getDimension(R.styleable.GDNewsContent_mSpaceBetweenPicAndCotent, dip2px(15));;
        mPicViewWidth = (int) a.getDimension(R.styleable.GDNewsContent_imageViewWidth, dip2px(445));
        mPicViweHeight = (int) a.getDimension(R.styleable.GDNewsContent_imageViewHeight, dip2px(355));
       
        mDensity = context.getResources().getDisplayMetrics().density;  
        a.recycle();
        
    }

    public GDNewsViewGoup(Context context) {
        super(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        View child;
        for(int i = 0;i< getChildCount();i++){
            child = getChildAt(i);
            child.layout(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
        }
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mWidth, mHeight);
        View child;
        for(int i = 0;i< getChildCount();i++){
            child = getChildAt(i);
            child.measure(MeasureSpec.makeMeasureSpec(child.getLayoutParams().width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(child.getLayoutParams().height, MeasureSpec.EXACTLY));
        }
    }
    
    public void setData(ArrayList<String> urls,String content){
        mWidth = getLayoutParams().width;
        mHeight = getLayoutParams().height;
        this.mUrls = urls;
        mContent = content;
        mCurrentPageNumber = 1;
        
        if(mUrls != null && !mUrls.isEmpty()){
            mPicCount = mUrls.size();
            if(mCacheBitmaps == null)
                mCacheBitmaps = new HashMap<String, SoftReference<Bitmap>>();
            else{
                mCacheBitmaps.clear();
            }
            if(mChildPic == null){
                createImageView();
            }else{
                mChildPic.setImageBitmap(null);
                mChildPic.setVisibility(View.VISIBLE);
            }
            setChildPicBitmap();
        }else{
            if(mChildPic != null)
                mChildPic.setVisibility(View.INVISIBLE);
        }
        if(mChildContent == null){
            createContentView();
            mChildContent.setPicCount(mPicCount);
            mChildContent.setText(mContent);
        }else{
            updateChildContentView();
            mChildContent.setPicCount(mPicCount);
            mChildContent.setText(mContent);
        }
        
    } 
    private void setChildPicBitmap(){
        
        new AsyncTask<String, Integer, Bitmap>(){
            
            protected void onPreExecute() {
                if(mLoadingPicLayout == null)
                    createLoadingPicView();
                
                addView(mLoadingPicLayout);
            };
            @Override
            protected Bitmap doInBackground(String... params) {
                try {
                    String key = params[0];
                    SoftReference<Bitmap> sofB = mCacheBitmaps.get(key);
                    Bitmap bitmap = null;
                    if(sofB != null  && (bitmap = sofB.get()) != null){
                        return bitmap;
                    }else{
                        
                        bitmap = BitmapFactory.decodeStream(new URL(key).openStream());
                        mCacheBitmaps.put(key, new SoftReference<Bitmap>(bitmap));
                        return bitmap;
                    }
                    
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
                return null;
            }
            
            @Override
            protected void onPostExecute(Bitmap result) {
                mChildPic.setImageBitmap(result);
                removeView(mLoadingPicLayout);
            }
        }.execute(mUrls.get(mCurrentPageNumber -1));
    }
    private void createImageView(){
        mChildPic = new ImageView(getContext());
        mChildPic.setLayoutParams(new LayoutParams(mPicViewWidth, mPicViweHeight));
        mChildPic.setScaleType(ScaleType.FIT_XY);
        mChildPic.setLeft(getPaddingLeft());
        mChildPic.setTop(getPaddingTop());
        mChildPic.setRight(mChildPic.getLeft() + mPicViewWidth);
        mChildPic.setBottom(mChildPic.getTop() + mPicViweHeight);
        addView(mChildPic);
    }
    
    private void createLoadingPicView(){
       mLoadingPicLayout = new LinearLayout(getContext());
       mLoadingPicLayout.setLayoutParams(new LayoutParams(mPicViewWidth, mPicViweHeight));
       mLoadingPicLayout.setGravity(Gravity.CENTER);
       ProgressBar bar = new ProgressBar(getContext());
       mLoadingPicLayout.addView(bar);
       mLoadingPicLayout.setLeft(getPaddingLeft());
       mLoadingPicLayout.setTop(getPaddingTop());
       mLoadingPicLayout.setRight(mLoadingPicLayout.getLeft() + mPicViewWidth);
       mLoadingPicLayout.setBottom(mLoadingPicLayout.getTop() + mPicViweHeight);
      
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        if (action == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                mCurrentPageNumber = mChildContent.loadNextPage() + 1;
                
                if(mChildPic != null){
                    if((mCurrentPageNumber) > mPicCount){
                        removeView(mLoadingPicLayout);
                        mChildPic.setVisibility(View.INVISIBLE);
                        updateChildContentView();
                    }
                }
                return true;

            case KeyEvent.KEYCODE_DPAD_LEFT:
                if(mCurrentPageNumber == 1){
                    mCurrentPageNumber = mChildContent.loadPrePage() + 1;
                    return true;
                }
                mCurrentPageNumber = mChildContent.loadPrePage() + 1;
                if((mCurrentPageNumber) <= mPicCount){
                    mChildPic.setVisibility(View.VISIBLE);
                    setChildPicBitmap();
                    updateChildContentView();
                }
                return true;

            }

        }
        return super.dispatchKeyEvent(event);
    }
    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if(View.VISIBLE == visibility){
            setFocusableInTouchMode(true);
            setFocusable(true);
            requestFocus();
        }else{
            setFocusableInTouchMode(false);
            setFocusable(false);
            clearFocus();
            
            if(mCacheBitmaps != null){
                for(SoftReference<Bitmap> sb : mCacheBitmaps.values()){
                   Bitmap bitmap = sb.get();
                   if(bitmap != null && !bitmap.isRecycled()){
                       bitmap.recycle();
                       sb.clear();
                   }
                }
                mCacheBitmaps.clear();
            }
        }
    }
    private void createContentView(){
        mChildContent = new GDNewsContentView(getContext());
        mChildContent.initParams(mLineHSpace, mLineVSpace, mTextColor, mTextSize,mWidth);
        
        mChildContent.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageChange(int totale, int currentPage) {
                if(mUpdatePageListener != null)
                    mUpdatePageListener.onUpdate(totale, currentPage);
            }
        });
        if(mChildPic != null && mChildPic.isShown()){
            mContentWidth = mWidth - getPaddingLeft() - getPaddingRight() - 
                    mSpaceBetweenPicAndCotent - mChildPic.getLayoutParams().width;
            mContentHeight = mHeight - getPaddingTop() - getPaddingBottom() ;
            mChildContent.setLeft((int) (getPaddingLeft() + mChildPic.getLayoutParams().width + mSpaceBetweenPicAndCotent));
            mChildContent.setTop(getPaddingTop());
            mChildContent.setRight((int) (mChildContent.getLeft() + mContentWidth));
            mChildContent.setBottom(getPaddingTop() + mHeight);
        }else{
            mContentWidth = mWidth -  getPaddingLeft() - getPaddingRight();
            mContentHeight = mHeight  - getPaddingTop() - getPaddingBottom();
            mChildContent.setLeft((int) (getPaddingLeft()));
            mChildContent.setTop(getPaddingTop());
            mChildContent.setRight((int) (mChildContent.getLeft() + mContentWidth));
            mChildContent.setBottom(getPaddingTop() + mHeight);
        }
        mChildContent.setLayoutParams(new LayoutParams((int)mContentWidth, (int)mContentHeight));
        addView(mChildContent);
    }
    
    
    
    private void updateChildContentView(){
        if(mChildPic != null && mChildPic.isShown()){
            mContentWidth = mWidth - getPaddingLeft() - getPaddingRight() - 
                    mSpaceBetweenPicAndCotent - mChildPic.getLayoutParams().width;
            mContentHeight = mHeight - getPaddingTop() - getPaddingBottom() ;
            mChildContent.setLeft((int) (getPaddingLeft() + mChildPic.getLayoutParams().width + mSpaceBetweenPicAndCotent));
            mChildContent.setTop(getPaddingTop());
            mChildContent.setRight((int) (mChildContent.getLeft() + mContentWidth));
            mChildContent.setBottom(getPaddingTop() + mHeight);
        }else{
            mContentWidth = mWidth -  getPaddingLeft() - getPaddingRight();
            mContentHeight = mHeight  - getPaddingTop() - getPaddingBottom();
            mChildContent.setLeft((int) (getPaddingLeft()));
            mChildContent.setTop(getPaddingTop());
            mChildContent.setRight((int) (mChildContent.getLeft() + mContentWidth));
            mChildContent.setBottom(getPaddingTop() + mHeight);
        }
        mChildContent.setLayoutParams(new LayoutParams((int)mContentWidth, (int)mContentHeight));
    }
    public int dip2px(float dpValue) {
        return (int) (dpValue * mDensity + 0.5f);
    }
}
