package com.dbstar.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;



public class DrawPillar extends DrawXY{

	protected int textXSize;
	protected int textYSize=5;
	protected String[] textX; 
	private float[] textY ; 

	private int dateType=2;
	protected float maxData;
	private ArrayList<RectF> rectF = new ArrayList<RectF>();
	private ArrayList<Float> data;
	private Paint paint0 = null;

	public DrawPillar(Context context) {
		super(context);

	}



	public void setData(ArrayList<Float> data,ArrayList<String> Xtexts,String unit_x,String unit_y)
	{

		this.data = data;

		textXSize = data.size();	

		textX = new String[textXSize];
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
		if(maxData == 0){
		    maxData = 100;
		}
		float [] text_Y = new float[6];

		for (int i = 0; i < text_Y.length; i++) {
			text_Y[i]=maxData/(text_Y.length-1)*i;
		}


		super.setData(textX, text_Y, unit_x, unit_y);	
		float width = (textXInterval/5)*3;
		if(width > 30)
		    width = 30;
		for(int i = 0; i < datasize; i++)
		{
		    float left = originX + (i* textXInterval);
		    left =left + (textXInterval  - width ) / 2;
		    
			rectF.add(new RectF(left ,originY -unit*data.get(i) ,
					left + width,originY-5 ));
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
		    float top = originY-8;
		    float left = rectF.get(i).left;
		    float right = rectF.get(i).right;
		    float bottom = top + 8;
		    List<RectF> rectFs  = new ArrayList<RectF>();
		    float size = rectF.get(i).bottom - rectF.get(i).top;
		    if(rectF.get(i).top == originY){
		        size = 2;
		    }else {
		        size = size + 10;
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

	public void setBarPaint()
	{
		paint0=new Paint();
		paint0.setAntiAlias(true);
		paint0.setStyle(Paint.Style.FILL);
		paint0.setColor(DrawBase.color[1]);
	}



	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
	    canvas.drawColor(Color.WHITE);
		super.onDraw(canvas);
		if((data!=null)&&(data.size()>0)){
			drawPillar(canvas);
		}

	}
	
	public void clearData(){
	    data = null;
	    invalidate();
	}
}
