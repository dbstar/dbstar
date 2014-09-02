package com.dbstar.multiple.media.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

import com.dbstar.multiple.media.shelf.R;

public class BookNameView extends TextView{
    
    private int width;
    private float startX;
    private float drawHeight;
    private Paint paint;
    private float fontSize;
    private float maxContentHeight;
    private String startTag = "《";
    private String endTag = "》";
    private float vSpace;
    boolean tag = false;
    public BookNameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ShelfBookTitleMaxHeight);
        maxContentHeight = a.getDimension(R.styleable.ShelfBookTitleMaxHeight_contentMaxHeight, 0);
        fontSize = a.getDimension(R.styleable.ShelfBookTitleMaxHeight_textSize, 18);
        vSpace = fontSize / 5.0f;
        a.recycle();
    }
    
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        if(true)
            return;
        width = getWidth();
        startX = (float)width/2;
        if(paint == null)
            paint = new Paint();
        paint.reset();
        paint.setAntiAlias(true);
        paint.setColor(getCurrentTextColor());
        paint.setTextAlign(Align.CENTER);
        paint.setTextSize(fontSize);
       
        canvas.save();
        
        String content = calculateContent(paint, getText().toString());
        if(content == null || content.length() == 0)
            return;
        char [] arr = content.toCharArray();
        drawHeight = getPaddingTop() ;
        
        canvas.rotate(90,startX,drawHeight);
        
        Rect bounds = new Rect();
        paint.getTextBounds(startTag, 0, 1, bounds);
        canvas.drawText(startTag, startX,  drawHeight + (float)(bounds.height() /2) -2, paint);
        
        drawHeight = drawHeight + vSpace;
        canvas.restore();
        for(int i = 0; i< arr.length;i ++){
            paint.getTextBounds(arr, i, 1, bounds);
            drawHeight = drawHeight + bounds.height() +vSpace;
            
            canvas.drawText(arr, i, 1, startX, drawHeight, paint);
        }
        canvas.save();
        bounds = new Rect();
        paint.setTextSize((int)(fontSize+0.5));
        paint.getTextBounds(endTag, 0, 1, bounds);
        drawHeight = drawHeight +  bounds.width() + vSpace;
        canvas.rotate(90,startX,drawHeight);
        canvas.drawText(endTag, startX,  drawHeight + (float)(bounds.height() /2) -2,paint );
    }
    
    @Override
    public void setTextSize(float size) {
        fontSize = size;
    }
    
    @Override
    public float getTextSize() {
        return fontSize;
    }
    
    public String calculateContent(Paint paint,String content){
        if(maxContentHeight == 0)
            return content;
        char [] arr = content.toCharArray();
        drawHeight = getPaddingTop() ;
        
        Rect bounds = new Rect();
        paint.getTextBounds(startTag, 0, 1, bounds);
        drawHeight = drawHeight + vSpace;
        for(int i = 0; i< arr.length;i ++){
            paint.getTextBounds(arr, i, 1, bounds);
            drawHeight = drawHeight + bounds.height() + vSpace;
        }
        paint.getTextBounds(endTag, 0, 1, bounds);
        drawHeight = drawHeight + vSpace + bounds.width();
        StringBuffer sb = new StringBuffer();
        if(drawHeight > maxContentHeight){
            float l = drawHeight - maxContentHeight;
            int count =  (int) (l / fontSize) + 1;
            int startIndex = (content.length() - 2) / 2;
            String str = content.substring(startIndex, startIndex+ count);
           return content.replaceFirst(str, "...");
        }
        return content;
    }

    public float getMaxContentHeight() {
        return maxContentHeight;
    }

    public void setMaxContentHeight(float maxContentHeight) {
        this.maxContentHeight = maxContentHeight;
    }
    
    
}
