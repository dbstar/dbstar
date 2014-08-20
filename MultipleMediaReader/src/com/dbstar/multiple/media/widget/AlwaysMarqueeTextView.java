package com.dbstar.multiple.media.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

public class AlwaysMarqueeTextView extends TextView{
    
    
    public AlwaysMarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    @Override
    public boolean isFocused() {
        return true;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
    
    
    @Override
    public void setFocusable(boolean focusable) {
        super.setFocusable(focusable);
//        if(focusable){
//            setMarqueeRepeatLimit(-1);
//            setEllipsize(TruncateAt.MARQUEE);
//        }else{
//            setMarqueeRepeatLimit(0);
//            setEllipsize(TruncateAt.END);
//        }
//       
//        invalidate();
//        
    }
    
}
