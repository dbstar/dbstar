package com.dbstar.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class GDNewsContentView extends TextView{
    
    private Paint mPaint;
    private int mTextColor;
    private float mTextSize;
    
    private int mHeight , mWidth;
    private int mParentWidth,mParentHeight;
    private float mLineVSpace,mLineHSpace;
    private int mPicCount;
    private float drawedHeight;
    private char [] textArray;
    private Page mPage ;
    private List<Page> mPageList = new ArrayList<Page>();
    
    private String mContent;
    private int mCurrentPage;
    
    private int mStartIndex = 0;
    private int mEndIndex = 0;
    private float mTempWidth = 0;
    
    private OnPageChangeListener mOnPageChangeListener;
    
    public interface OnPageChangeListener{
        void onPageChange(int totale,int currentPage);
    }
    
    public void setOnPageChangeListener(OnPageChangeListener listener){
        this.mOnPageChangeListener = listener;
    }
    public GDNewsContentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public GDNewsContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GDNewsContentView(Context context) {
        super(context);
    }
    
    public void initParams(float hSpace,float vSpace,int textColor,float textSize,int parentWidth){
        mLineHSpace =  hSpace;
        mLineVSpace =  vSpace;
        mTextColor = textColor;
        mTextSize =  textSize;
        mParentWidth = parentWidth;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mTextSize);
    }
    @Override
    public void setTextColor(ColorStateList colors) {
        super.setTextColor(colors);
        if(mPaint != null)
            mPaint.setColor(mTextColor);
    }
    @Override
    public void setTextSize(float size) {
        super.setTextSize(size);
        if(mPaint != null)
            mPaint.setTextSize(mTextSize);
    }
    
    public void setText(String content){
        mCurrentPage = 0;
        mEndIndex = 0;
        mPageList.clear();
        this.mContent =content;
    }
    
    public void setPicCount(int picCount){
        this.mPicCount = picCount;
    }
   
    @Override
    protected void onDraw(Canvas canvas) {
        mEndIndex = 0;
        if(mContent == null || mContent.isEmpty())
            return;
        if(mPageList.isEmpty())
            constructPage(canvas);
        Page page = mPageList.get(mCurrentPage);
        if(page.lines == null || page.lines.isEmpty())
            return;
        for(Line line : page.lines){
            canvas.drawText(line.content, 0,line.stattY, mPaint);
            
        }
        
        if(this.mOnPageChangeListener != null)
            mOnPageChangeListener.onPageChange(mPageList.size(), mCurrentPage + 1);
    }
    
 
    private void constructPage(Canvas canvas) {
        mPage = new Page();
        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();
        textArray = mContent.toCharArray();
        String line = "";
        drawedHeight = mTextSize;
        while (!(line = getOneLineStr(mWidth)).isEmpty()) {
            int i = line.indexOf("\n");
            if(i != -1){
                mEndIndex = mEndIndex - (line.length() - i) + 1;
                line = line.substring(0,i);
            }
            if(drawedHeight + mTextSize <= mHeight){
                mPage.lines.add(new Line(line,0,drawedHeight));
                drawedHeight = drawedHeight +  mTextSize + mLineVSpace;
            }
            if(drawedHeight + mTextSize > mHeight){
                mPageList.add(mPage);
                if(mPageList.size() >= mPicCount){
                    mWidth = mParentWidth;
                }
                mPage = new Page();
                drawedHeight = mTextSize;
            }
        }
        if(!mPage.lines.isEmpty())
            mPageList.add(mPage);
        
        if(mPageList.size() < mPicCount){
            for(int i = 0 ,size = mPicCount - mPageList.size() ;i< size; i++){
                mPageList.add(new Page());
            }
        }
        
       
    }
    
    public int loadNextPage(){
        if(mCurrentPage >= mPageList.size() -1){
            return mCurrentPage;
        }
        mCurrentPage ++;
        invalidate();
        return mCurrentPage;
    }
    
    public int loadPrePage(){
        if(mCurrentPage == 0){
            return mCurrentPage;
        }
        mCurrentPage--;
        invalidate();
        return mCurrentPage;
    }
    
    class Page{
       public List<Line> lines = new ArrayList<GDNewsContentView.Line>();
    }
    class Line {
        public String content;
        public float startX = 0;
        public float stattY ;
        public Line(String content, float startX, float stattY) {
            super();
            this.content = content;
            this.startX = startX;
            this.stattY = stattY;
        }
        
    }
    
    private String getOneLineStr(float width){
        mStartIndex = mEndIndex;
        mEndIndex = mStartIndex + (int) (width/ mTextSize);
        if(mEndIndex > textArray.length)
            mEndIndex = textArray.length;
        mTempWidth = mPaint.measureText(textArray,mStartIndex,mEndIndex - mStartIndex);
        if(mTempWidth > 0 && mTempWidth < width){
            return String.valueOf(textArray, mStartIndex, mEndIndex - mStartIndex) + getOneLineStr(width - mTempWidth);
        }else if(mTempWidth == 0){
            return "";
        }else{
            return "";
        }
        
    }
    
    
}
