package com.dbstar.widget;



import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;


public class DrawXY extends DrawBase{
	private final String TAG="DrawBase"; 
	protected float textXInterval;
	protected float textYInterval;
	protected float unit;
	protected int textXSize;
	protected int textYSize;
	protected String[] textX; 
	protected String[] textY ; 
	private String unit_x;
	private String unit_y;

	public DrawXY(Context context) {
		super(context);

	}



	@Override
	public void setFrame(int mWidth, int mHeight, int paddingLeft,
			int paddingTop, int paddingRight, int paddingBottom) {
		// TODO Auto-generated method stub
		super.setFrame(mWidth, mHeight, paddingLeft, paddingTop, paddingRight,
				paddingBottom);
		setOriginPoint(paddingLeft, mHeight-paddingBottom);

	}

	/**
	 * set Picture Size
	 * @param iWidth:Picture Width
	 *        iHeight:Picture Height
	 * @return 
	 */

	@Override
	public void setPicSize(int mWidth, int mHeight) {
		// TODO Auto-generated method stub
		super.setPicSize(mWidth, mHeight);
		setOriginPoint(paddingLeft, mHeight-paddingBottom);

	}

	public void setData(String[] data_x,float[] data_y,String unit_x,String unit_y)
	{

		this.unit_x = unit_x;
		this.unit_y =unit_y;
		this.textX=data_x;

		textY=new String[data_y.length];
		float maxData=0;
		for(int i=0;i<data_y.length;i++){
			textY[i]=CommondTools.round(data_y[i],1);
			if(maxData<data_y[i]){
				maxData=data_y[i];
			}
		}


		textXSize =data_x.length;
		textYSize =data_y.length;

		textXInterval = (mWidth  - paddingLeft  - paddingRight )/(textXSize+1);
		textYInterval=(mHeight-paddingTop-paddingBottom)/(textYSize);
	
		unit =(textYInterval*(textY.length-1))/maxData;
		

	}


	public void darwAxis(Canvas canvas){
		
	    int w = 20;
	    int h = 10;
	    Path path ;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GRAY);
        path = new Path();
        path.moveTo(originX-5, originY);
        path.lineTo(originX, originY);
        path.lineTo(originX + w, originY - h);
        path.lineTo(originX + mWidth - paddingLeft - paddingRight, originY -h);
        path.lineTo(originX + mWidth - paddingLeft - paddingRight - w, originY);
        path.lineTo(originX, originY);
        
        canvas.drawPath(path, paint);
        
		canvas.drawLine(originX , originY , originX , paddingTop +textYInterval , paint);

		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		for (int i = 0; i < textYSize; i++) {
		    float y = originY-textYInterval*(i);
		    path = new Path();
		    path.moveTo(originX-5, y);
		    path.lineTo(originX, y);
		    path.lineTo(originX + w, y - h);
		    path.lineTo(originX + mWidth - paddingLeft - paddingRight, y - h);
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
			canvas.drawText(String.valueOf(textY[i]), originX-5, originY-textYInterval*(i)+5, textPaint);
		}
		canvas.drawText(unit_y, originX -20, originY-textYInterval*5-10, textPaint);

		
		 textPaint.setTextAlign(Paint.Align.CENTER);
			for (int i = 0; i < textXSize; i++) {

				canvas.drawText(String.valueOf(textX[i]),originX+textXInterval*(i+1)- textXInterval/2, originY+20, textPaint);
			}
			
			 textPaint.setColor(Color.GRAY);
             for(int i= 0;i< textX.length;i++){
                    float startX = originX +(i+1)*textXInterval;
                    float stopX =  originX +(i+1)*textXInterval;
                    float startY = originY;
                    float stopY =  originY  +3;
                    canvas.drawLine(startX,startY,stopX,stopY, textPaint);
                }
		canvas.drawText(unit_x, mWidth - paddingRight +5,originY+15, textPaint);

	}


	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		Log.i(TAG, ">>>>>>>>>>>>>>>"+"mWidth="+mWidth+"---mHeight="+mHeight+"---paddingLeft="+paddingLeft+"---paddingTop="+paddingTop+"---paddingRight="+paddingRight+"---paddingBottom="+paddingBottom);

		super.onDraw(canvas);	
	
		
		darwAxis(canvas);
		drawDataXY(canvas);	

	}
}
