package com.dbstar.multiple.media.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.dbstar.multiple.media.data.NewsPaperArticleContent;
import com.dbstar.multiple.media.data.NewsPaperArticleContent.Block;

public class NewsPaperContentView extends View{
    
    private  NewsPaperArticleContent data;
    private Paint mPaint;
    private int mWidth;
    private int mHeight;
    private int startIndex,endIndex;
    private int mTextSize = 23;
    private int mVSpace;
    private int mRowHeight;
    public NewsPaperContentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public NewsPaperContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NewsPaperContentView(Context context) {
        super(context);
        init();
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.makeMeasureSpec(getLayoutParams().width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY));
    }
    private void init(){
     mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
     mPaint.setTextSize(mTextSize);
     mPaint.setColor(Color.BLACK);
     mVSpace = mTextSize/2;
    }
    public void setData(NewsPaperArticleContent newsPaperData){
        data = newsPaperData;
        if(data == null)
            return;
        mHeight = measureHeight();
        startIndex = 0;
        endIndex  = 0;
        requestLayout();
        invalidate();
    }
    public void setTextSize(int size){
        if(size == mTextSize)
            return;
        mTextSize = size;
        mPaint.setTextSize(mTextSize);
        mVSpace = mTextSize/2;
        mHeight = measureHeight();
        requestLayout();
        invalidate();
    }
    public int getTextSize (){
        return mTextSize;
    }
    private int drawWidth,drawHeiht;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        start = System.currentTimeMillis();
        Block block;
        String text;
        int type;
        if (data != null && data.blocks != null && data.blocks.size() > 0) {        	
        	for(int i =0,count = data.blocks.size();i< count;i++){
        		block = data.blocks.get(i);
        		text = block.value;
        		type = block.type;
        		if(startIndex != endIndex && i >= startIndex &&  i<= endIndex){
        			mPaint.setColor(Color.BLUE);
        		}else{
        			mPaint.setColor(Color.BLACK);
        		}
        		if(type == 0){
        			canvas.drawText(text, block.x, block.y , mPaint);
        		}else if(type == 2){
        			if(!block.value.equals("&#xd;"))
        				canvas.drawText("    ", block.x, block.y  , mPaint);
        		}else if(type == 4){
        			Bitmap bitmap = createBitmap(block.value);
        			if(bitmap != null)
        				canvas.drawBitmap(bitmap,block.x, block.y , mPaint);
        		} 
        	}
        } else {
        	Log.d("NewsPaperArticleContent", "no newspaper need draw!");
        }
        
        end = System.currentTimeMillis();
        Log.i("NewsPaperContentView", " ondraw  take time = " + (end - start));
        
    }
    public void setPalyIndex(int start,int end){
        startIndex = start;
        endIndex = end;
        invalidate();
    }
    public void autoScroll(boolean auto){
        
    }
    private long start,end;
    private int measureHeight(){
        start = System.currentTimeMillis();
        drawHeiht = 0;
        drawWidth = 0;
        mWidth = getLayoutParams().width;
        mRowHeight = mTextSize + mVSpace;
        
        drawHeiht = mRowHeight;
        Block block;
        String text;
        int type;
        for(int i =0,count = data.blocks.size();i< count;i++){
            block = data.blocks.get(i);
            text = block.value;
            type = block.type;
            
            if(type == 0){
                if("\r".equals(text)){
                    continue;
                }else if("\n".equals(text)){
                    drawWidth = 0;
                    drawHeiht = drawHeiht + mRowHeight;
                    block.x = drawWidth;
                    block.y = drawHeiht;
                    continue;
                }
                
                if(drawWidth + mPaint.measureText(text) <= mWidth){
                   
                }else{
                    drawWidth = 0;
                    drawHeiht = drawHeiht + mRowHeight;
                }
                block.x = drawWidth;
                block.y = drawHeiht;
                drawWidth = (int) (drawWidth + mPaint.measureText(text)) ;
            }else if(type == 2){
                if(block.value.equals("&#xd;")){
                    drawWidth = 0;
                    drawHeiht = drawHeiht + mRowHeight;
                }else{
                    if(drawWidth + mPaint.measureText(text) <= mWidth){
                        
                    }else{
                        drawWidth = 0;
                        drawHeiht = drawHeiht + mRowHeight;
                    }
                    block.x = drawWidth;
                    block.y = drawHeiht;
                    drawWidth = (int) (drawWidth + mPaint.measureText(text)) ;  
                }
                
            }else if(type == 4){
                drawHeiht = drawHeiht + mRowHeight;
                Bitmap bitmap = createBitmap(block.value);
                if(bitmap != null){
                    drawWidth = (mWidth - bitmap.getWidth())/2;
                    block.x = drawWidth;
                    block.y = drawHeiht;
                    drawHeiht = drawHeiht + bitmap.getHeight() +mRowHeight;
                }
                drawWidth = 0;
            } else if(type == 5){
                drawWidth = 0;
                drawHeiht = drawHeiht + mRowHeight;
            }
    }
        end = System.currentTimeMillis();
        Log.i("NewsPaperContentView", " measure hight take time = " + (end - start));
        return drawHeiht + 100;
}
    
    private Bitmap createBitmap(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        Bitmap bitmap = null;
        try {
             bitmap = BitmapFactory.decodeFile(path,options);
             
             if(bitmap != null && bitmap.getWidth() > mWidth){
                 Matrix matrix = new Matrix();
                 float sx = (float)mWidth / bitmap.getWidth();
                 matrix.postScale(sx, sx);
                 bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
             }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
    
    
}
