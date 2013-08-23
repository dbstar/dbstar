package com.dbstar.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.View;



public class DrawPillar extends View{

	protected int textXSize;
	protected int textYSize=6;
	private ArrayList<RectF> rectF = new ArrayList<RectF>();
	private ArrayList<Float> data;
	private Paint paint0 = null;
    protected float textXInterval;
    protected float textYInterval;
    protected float unit;
    protected String[] textX; 
    protected String[] textY ; 
    protected String unit_x;
    protected String unit_y;
    protected float maxData;
    
    int mWidth=0;      
    int mHeight=0;     
    int paddingLeft=0;
    int paddingTop=0;
    int paddingRight=0;
    int paddingBottom=0;
    int zX;
    int zY;
    private float mDensity;
    protected Context mContext;
    Paint textPaint = null;
    int originX=0;    
    int originY=0; 
	public DrawPillar(Context context) {
	    super(context);
	    mDensity = context.getResources().getDisplayMetrics().density;  
	    this.mContext = context;
	    textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTypeface(Typeface.SANS_SERIF);

	}

    public void setFrame(int mWidth,int mHeight,int paddingLeft,int paddingTop,int paddingRight,int paddingBottom){
        this.mWidth = mWidth;
        this.mHeight = mHeight;     
        this.paddingLeft = dip2px(paddingLeft);
        this.paddingTop = dip2px(paddingTop);
        this.paddingRight = dip2px(paddingRight);
        this.paddingBottom = dip2px(paddingBottom);
        this.originX = this.paddingLeft;
        this.originY = this.mHeight - this.paddingBottom;
    }
    

	public void setData(ArrayList<Float> data,ArrayList<String> Xtexts,String unit_x,String unit_y)
	{

		this.data = data;

		textXSize = data.size();	

		textX = new String[textXSize];
		textY = new String[textYSize];
		for(int i = 0; i < textXSize; i++)
		{
			textX[i] = Xtexts.get(i);
		}

		int datasize=data.size();
		for(int i = 0; i < datasize; i++)
		{
			if(maxData<data.get(i))
			{
				maxData = data.get(i);
			}
		}
		int scale = 1;
		if(maxData == 0){
		    maxData = 100;
		}else{
		    maxData = maxData + (maxData / (textYSize -1));
		    float f = textYSize -1;
		    if(maxData >=1){
		        scale = 1;
		    }else if(maxData >=0.1  && maxData < 1 ){
	            scale = 2;
	        }else if(maxData >= 0.01  && maxData < 0.1){
	            scale =3;
	        }else if(maxData > 0 && maxData < 0.01){
	            scale = 4;
	        }
		}
		for (int i = 0; i < textYSize; i++) {
			textY[i]=CommondTools.round(maxData/(textYSize-1)*i,scale);
		}
		this.unit_x = unit_x;
        this.unit_y =unit_y;

        textXInterval = (mWidth  - paddingLeft  - paddingRight )/(textXSize+1);
        textYInterval=(mHeight-paddingTop-paddingBottom)/(textYSize -1);
    
        unit =(textYInterval*(textYSize-1))/maxData;
        
        zX = dip2px(20);
        zY = dip2px(10);
		float width = (textXInterval/5)*3;
		if(width > dip2px(30))
		    width = dip2px(30);
		float left= 0;
		for(int i = 0; i < datasize; i++)
		{ 
		    if(data.get(i) == null)
		        rectF.add(null);
		    else{
		        left = originX + (i* textXInterval);
		        left =left + (textXInterval  - width ) / 2;
		        rectF.add(new RectF(left ,originY -unit*data.get(i),
		                left + width,originY ));
		    }
		    
		}	

	}


	public void drawPillar(Canvas canvas)
	{
		
		paint0=new Paint();
		int rectfsize=rectF.size();
		paint0.setAntiAlias(true);
		paint0.setColor(Color.parseColor("#578ACB"));
		paint0.setStyle(Paint.Style.FILL);
		for(int i = 0; i < rectfsize; i ++)
		{     
		    if(rectF.get(i) == null)
		        continue;
		    float top = originY - zY;
		    float left = rectF.get(i).left;
		    float right = rectF.get(i).right;
		    float bottom = originY;
		    List<RectF> rectFs  = new ArrayList<RectF>();
		    float size = rectF.get(i).bottom - rectF.get(i).top;
		    if(rectF.get(i).top == originY){
		        size = 2;
		    }else {
		        size = size;
		    }
		    for(int j = 1 ;j< size;j++){
		        rectFs.add(new RectF(left, top-j,  right, bottom-j));
		    }
		    for(int a = 0; a < rectFs.size();a++){
		        
		        canvas.drawOval(rectFs.get(a), paint0);
		    }
			//textPaint.setTextSize(12);
			//canvas.drawText(data.get(i).toString(),originX+(i+1)*textXInterval , originY -unit*data.get(i)-10 , textPaint);
		}
	}

//	public void setBarPaint()
//	{
//		paint0=new Paint();
//		paint0.setAntiAlias(true);
//		paint0.setStyle(Paint.Style.FILL);
//		paint0.setColor(DrawBase.color[1]);
//	}


    public void darwAxis(Canvas canvas){
        
    
        Path path ;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GRAY);
        path = new Path();
        path.moveTo(originX-dip2px(5), originY);
        path.lineTo(originX, originY);
        path.lineTo(originX + zX, originY - zY);
        path.lineTo(originX + mWidth - paddingLeft - paddingRight, originY -zY);
        path.lineTo(originX + mWidth - paddingLeft - paddingRight - zX, originY);
        path.lineTo(originX, originY);
        
        canvas.drawPath(path, paint);
        
        canvas.drawLine(originX , originY , originX , originY - textYInterval * (textYSize -1) , paint);

        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        for (int i = 0; i < textYSize; i++) {
            float y = originY-textYInterval*(i);
            path = new Path();
            path.moveTo(originX-dip2px(5), y);
            path.lineTo(originX, y);
            path.lineTo(originX + zX, y - zY);
            path.lineTo(originX + mWidth - paddingLeft - paddingRight, y - zY);
            canvas.drawPath(path, paint);
            //canvas.drawLine(originX , originY-textYInterval*(i+1), originX+3, originY-textYInterval*(i+1) , textPaint);
        }
        
          
    }

    public void drawDataXY(Canvas canvas)
    {
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.RIGHT);
        int ysize=textY.length;
        for(int i = 0; i < ysize; i++)
        {
            canvas.drawText(String.valueOf(textY[i]), originX-5, originY-textYInterval*(i)+dip2px(5), textPaint);
        }
        canvas.drawText(unit_y, originX -dip2px(20), originY-textYInterval*5-dip2px(10), textPaint);

        
         textPaint.setTextAlign(Paint.Align.CENTER);
            for (int i = 0; i < textXSize; i++) {

                canvas.drawText(String.valueOf(textX[i]),originX+textXInterval*(i+1)- textXInterval/2, originY+dip2px(20), textPaint);
            }
            
             textPaint.setColor(Color.GRAY);
             for(int i= 0;i< textX.length;i++){
                    float startX = originX +(i+1)*textXInterval;
                    float stopX =  originX +(i+1)*textXInterval;
                    float startY = originY;
                    float stopY =  originY  +dip2px(3);
                    canvas.drawLine(startX,startY,stopX,stopY, textPaint);
                }
        canvas.drawText(unit_x, mWidth - paddingRight +dip2px(5),originY+dip2px(15), textPaint);

    }
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
	    super.onDraw(canvas);
		canvas.drawColor(Color.WHITE);
		darwAxis(canvas);
        drawDataXY(canvas); 
		if((data!=null)&&(data.size()>0)){
			drawPillar(canvas);
		}

	}
	
	public void clearData(){
	    data = null;
	    invalidate();
	}
	   
    private int px2dip(float pxValue) {
        return (int) (pxValue / mDensity + 0.5f);
    }
    private int dip2px(float dpValue) {  
        return (int) (dpValue * mDensity + 0.5f);  
    }  
}
