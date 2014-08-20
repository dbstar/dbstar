package com.dbstar.multiple.media.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

public class RepeatCharTextView extends TextView{
    
    Paint paint;
    String text;
    public RepeatCharTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        paint = getPaint();
        paint.setColor(getCurrentTextColor());
        text = getText().toString();
        
        if(text == null || text.length() == 0)
            return;
        
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        int charW = rect.width();
        int charH = rect.height();
        
        float drawWidth = 0f ;
        float measureText = paint.measureText(text);
        float startY = (float)(height - charH) /2 + charH;
        while((drawWidth+measureText) < width){
            canvas.drawText(text, drawWidth, startY, paint);
            drawWidth = drawWidth + measureText;
            
        }
    }
}   
