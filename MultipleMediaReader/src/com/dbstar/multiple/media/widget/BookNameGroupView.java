package com.dbstar.multiple.media.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dbstar.multiple.media.shelf.R;

public class BookNameGroupView extends LinearLayout{
    
    private static final String TAG = "BookNameGroupView";
    private TextView mBookNameView;
    private TextView mBookDateView;
    private TextView mBookNameStartTagView,mBookNameEndTagView;
    
    public int mWidth,mHeiht;
    
    public int mMarginLeft,mMarginTop;
    
    public int mPaddingLeft,mPaddingRight;
    
    public float mTextSize;
    
    public float mBookNameMaxWidth;
    
    private float mTemp;
    
    public int w,h;
    public BookNameGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ShelfBookTitleMaxHeight);
        mBookNameMaxWidth = a.getDimension(R.styleable.ShelfBookTitleMaxHeight_contentMaxHeight, 0);
        a.recycle();
    }
    
    
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
//        
//        mBookNameView = (TextView) findViewById(R.id.bookName);
//        mBookNameStartTagView = (TextView) findViewById(R.id.bookNameStartTag);
//        mBookNameEndTagView = (TextView) findViewById(R.id.bookNameEndTag);
//        mBookDateView = (TextView) findViewById(R.id.bookDate);
        mBookNameStartTagView = (TextView) getChildAt(0);
        mBookNameView = (TextView) getChildAt(1);
        mBookNameEndTagView = (TextView) getChildAt(2);
        mBookDateView = (TextView) getChildAt(4);
    }
    
    public void initParams(){
        mWidth = getWidth();
        mHeiht = getHeight();
        
        RelativeLayout.LayoutParams params =  (android.widget.RelativeLayout.LayoutParams) getLayoutParams();
        mMarginLeft = params.leftMargin;
        mMarginTop = params.topMargin;
        
        mPaddingLeft = getPaddingLeft();
        mPaddingRight = getPaddingRight();
        
        mTextSize = mBookNameView.getTextSize();
        
        w = mBookDateView.getWidth();
        h = mBookDateView.getHeight();
        
        if(mTemp != 0){
            mBookNameMaxWidth = mTemp;
            mTemp = 0;
        }
        
    }
    public void setBookInfo(String bookName){
        mBookNameView.setText(bookName);
    }
    
    public void setBookDate(String date){
        mBookDateView.setText(date);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
       
    }
    public void setTextSize(float size){
        Log.i(TAG, "old size = " + mBookNameView.getTextSize() + ", new size = " + size);
        mBookNameEndTagView.setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
        mBookNameStartTagView.setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
        mBookNameView.setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
        mBookDateView.setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
        
    }
    
    public void swap(BookNameGroupView target){
        RelativeLayout.LayoutParams params =  (android.widget.RelativeLayout.LayoutParams) getLayoutParams();
        params.width = target.mWidth;
        params.height =android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT;
        params.setMargins(target.mMarginLeft, target.mMarginTop, 0, 0);
        setPadding(target.mPaddingLeft, 0, target.mPaddingRight, 0);
        setTextSize(target.mTextSize);
        mBookNameView.setMaxWidth((int)target.mBookNameMaxWidth);
        mTemp = target.mBookNameMaxWidth;
        
        //Log.i(TAG, "swap = " + target.toString());
        
        
    }
    
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        Log.v(TAG, "onLayout = " + l + "," + t + "," + r + "," + b);
    }
    
    
    @Override
    public String toString() {
        return "BookNameGroupView [mWidth=" + mWidth + ", mHeiht=" + mHeiht +  ", left = " + getLeft() + ", mMarginLeft=" + mMarginLeft + ", mMarginTop=" + mMarginTop + ", mPaddingLeft=" + mPaddingLeft + ", mPaddingRight="
                + mPaddingRight + ", mTextSize=" + mTextSize + ", mBookNameMaxWidth=" + mBookNameMaxWidth + "]";
    }
        
    
    public void requestBookNameFocus() {
        mBookNameView.setFocusable(true);
    }
    public void requestBookNameNotFocus(){
        mBookNameView.setFocusable(false);
    }
}
