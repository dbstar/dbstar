package com.dbstar.multiple.media.widget;

import java.util.Arrays;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

@SuppressLint("NewApi")
public class BookSynopsisView extends TextView {
    
    private static final int MAX_LINE_COUNT = 11;
    private static final int MAX_LINE_WORD_COUNT = 15;
    private Paint mPaint;
    private int mStartIndex = 0;
    private int mEndIndex = 0;
    private char [] textArray;
    private float mTempWidth = 0;
    private float mTextSize;
    private float mWidth,mHeight;
    public BookSynopsisView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    
    @Override
    protected void onDraw(Canvas canvas) {
        mStartIndex = 0;
        mEndIndex = 0;
        mPaint = getPaint();
        String content  = getText().toString();
        textArray = content.toCharArray();
        mTextSize = getTextSize();
        mWidth = getWidth();
        mHeight = getHeight();
        int linespace = (int) getLineSpacingExtra();
        float drawHeight = mTextSize;
        String text;
        int lineCount =1;
        while(drawHeight <= mHeight){
            text = getOneLineStr(mWidth);
            if(text.isEmpty())
                break;
            if(lineCount == MAX_LINE_COUNT){
                canvas.drawText(text.subSequence(0, text.length() -2) + "……", 0, drawHeight, mPaint);
            }else{
                canvas.drawText(text, 0, drawHeight, mPaint);
            }
            drawHeight = drawHeight + mTextSize + linespace;
            lineCount ++;
        }
        
        
    }
    
    private String getOneLineStr(float width){
        mStartIndex = mEndIndex;
        mEndIndex = mStartIndex + (int) (width/ mTextSize);
        if(mEndIndex > textArray.length)
            mEndIndex = textArray.length;
        mTempWidth = mPaint.measureText(textArray,mStartIndex,mEndIndex - mStartIndex);
        if(mTempWidth > 0 && mTempWidth <= width){
            return String.valueOf(textArray, mStartIndex, mEndIndex - mStartIndex) + getOneLineStr(width - mTempWidth);
        }else if(mTempWidth == 0){
            return "";
        }else{
            return "";
        }
        
    }
}
