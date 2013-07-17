package com.dbstar.widget;

import com.dbstar.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CircleFlowIndicator extends View{
    
    private float radius = 4;
    private  Paint mPaint ;
    private int mFocus;
    private int  mNormal;
    private int count = 0;
    private float leftpadding;
    private int mNumPage = 0;
    public CircleFlowIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CircleFlowIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.GDIndicator);
        radius = a.getDimension(R.styleable.GDIndicator_indicatorRadius, 4);
        mFocus = a.getColor(R.styleable.GDIndicator_selectColor, Color.parseColor("#0199EF"));
        mNormal = a.getColor(R.styleable.GDIndicator_normalColor, Color.parseColor("#182E39"));
        init();
    }

    public CircleFlowIndicator(Context context) {
        super(context);
    }
    
    private void init(){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //mPaint.setColor(mNormal);
       
    }
    public void setPageCount (int count){
        this.count = count;
        invalidate();
    } 
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(count <=1)
            return;
        float width =(3* count -1)* radius;
        leftpadding = (this.getLayoutParams().width - width) /2;
        float circleSeparation = 2*radius+radius;
        float centeringOffset = 0;
        
        int leftPadding = (int) leftpadding;
        
        for (int iLoop = 0; iLoop < count; iLoop++) {
            if(mNumPage == iLoop){
                mPaint.setColor(mFocus);
            }else{
                mPaint.setColor(mNormal);
            }
            canvas.drawCircle(leftPadding + radius
                    + (iLoop * circleSeparation) + centeringOffset,
                    getPaddingTop() + radius, radius, mPaint);
        }
        
    }
    
    public void setCurrentPage(int numPage){
        mNumPage = numPage;
        invalidate();
    }
    
}
