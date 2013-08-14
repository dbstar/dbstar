package com.dbstar.widget;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;


public class PowerTrackPolyLineView extends DrawBase{
	private float textXInterval;
	private float textYInterval;
	private int textXSize;

	private ArrayList<String> textX;  
	private String[] textY; 
	private String xDateType;
	private String yDateType;
	private float maxData;
	private float minData;
	private float f;

	private Paint mLinePaint = null;
	private Paint mPointPaint = null;
	private Paint mTexPaint = null;
	private Paint mPaint = null;
	private ArrayList<Float> mPointYData;
	private ArrayList<String> mPointTextData;
	
	private float origin_Y;
	private float origin_X;
	private Paint paint = null;

	int minIndex = 0;
    int maxIndex = 0;
    private ArrayList<Float> data;
    private int degreeCount;
    ArrayList<Point> mPoints;
	public PowerTrackPolyLineView(Context context) {
		super(context);
		init();
	}
	
	private void init(){
	    degreeCount = 5;
	    mPoints = new ArrayList<Point>();
	    mLinePaint = new Paint();
	    mLinePaint.setAntiAlias(true);
	    mLinePaint.setStyle(Paint.Style.STROKE);
	    mLinePaint.setStrokeCap(Paint.Cap.BUTT);
	    mLinePaint.setColor(Color.parseColor("#4e85c5"));
	    
	    mPointPaint = new Paint();
	    mPointPaint.setAntiAlias(true);
	    mPointPaint.setStyle(Paint.Style.FILL_AND_STROKE);
	    mPointPaint.setStrokeCap(Paint.Cap.ROUND);
	    mPointPaint.setColor(Color.parseColor("#4e85c5"));
        
	    mTexPaint = new Paint();
	    mTexPaint.setAntiAlias(true);
	    mTexPaint.setStyle(Paint.Style.STROKE);
	    mTexPaint.setStrokeCap(Paint.Cap.ROUND);
	    mTexPaint.setColor(Color.BLACK);
        
	    mPaint = new Paint();
	    mPaint.setAntiAlias(true);
	    mPaint.setStyle(Paint.Style.FILL);
	    //mPaint.setStrokeCap(Paint.Cap.ROUND);
	    
	    
	}
	
	public void setData(ArrayList<Float> yData,ArrayList<String> xTextdata,String xType,String yType)
	{
		this.mPointYData = yData;
		xDateType =xType;
		yDateType = yType;
		textXSize = xTextdata.size();
		

		textXInterval = (mWidth  - paddingLeft  - paddingRight )/(textXSize+1);
		  minData = Integer.MAX_VALUE;
		for(int i = 0; i < yData.size(); i++)
		{
		    if(yData.get(i) == null)
		        continue;
			if(maxData<=Math.abs(yData.get(i)))
			{
				maxData = Math.abs(yData.get(i));
				maxIndex = i;
			}
			if(minData >= Math.abs(yData.get(i)))
            {
                minData = Math.abs(yData.get(i));
                minIndex =  i;
            }
		}
		  int scale = 1;
		if(maxData == 0)
		{
			f=0;
			maxData = 100;
		}
		else
		{
		    maxData = maxData + (maxData / (degreeCount -1));
		    if(maxData >=1){
              scale = 1;
            }else if(maxData >=0.1  && maxData < 1 ){
                scale = 2;
            }else if(maxData >= 0.01  && maxData < 0.1){
                scale =3;
            }else if(maxData > 0 && maxData < 0.01){
                scale = 4;
            }
		    
			f = (mHeight  - paddingTop  - paddingBottom )/(maxData);//
		}	
		textYInterval = (mHeight  - paddingTop  - paddingBottom )/degreeCount;
		origin_Y = paddingTop+textYInterval*degreeCount;
		origin_X = paddingLeft;
		textY = new String[degreeCount+1];	

		for(int i = 0; i <degreeCount+1; i++)
		{

			textY[i] = CommondTools.round(maxData/degreeCount*i,scale);

		}
		/*******   yang.xu        2012-11-29  V4.0.6       BUG-0000994   begin ******/
//		for(int i = 0; i < textY.length; i++)
//		{
//			if(maxData/1000000 > 1){
//				textY[i] = split(String.valueOf(Double.valueOf(textY[i].replace("-", "").replace(",", ""))/1000000))+"M";
//			}else if(maxData < 1000000 && maxData > 1000){
//				textY[i] = split(String.valueOf(Double.valueOf(textY[i].replace("-", "").replace(",", ""))/1000))+"K";
//			}
//		}
		/*******   yang.xu        2012-11-29  V4.0.6       BUG-0000994   end ******/
		textX = xTextdata;
	}

	private void darwAxis(Canvas canvas){	
	    mPaint.setColor(Color.GRAY);
	    mPaint.setStrokeWidth(2);
		canvas.drawLine(origin_X ,paddingTop+textYInterval*degreeCount, mWidth -paddingRight ,paddingTop +textYInterval*degreeCount, mPaint);
		canvas.drawLine(origin_X , paddingTop , paddingLeft ,paddingTop+textYInterval*degreeCount , mPaint);
		 mPaint.setStrokeWidth(1);
//		for (int i = 0; i < degreeCount; i++) {
//			canvas.drawLine(origin_X , paddingTop +textYInterval*i,mWidth -paddingRight, paddingTop +textYInterval*i, mPaint);
//		}
		
		
//		for(int i= 0;i< textX.length;i++){
//		    float startX = origin_X +(i+1)*textXInterval;
//		    float stopX =  origin_X +(i+1)*textXInterval;
//		    float startY = origin_Y;
//		    float stopY = origin_Y - 3;
//		    canvas.drawLine(startX,startY,stopX,stopY, mPaint);
//		}


	} 

	public void drawData(Canvas canvas)
	{
	    
	  
	    mTexPaint.setTextAlign(Paint.Align.CENTER);
		for(int i = 0; i < textX.size(); i++)
		{
			canvas.drawText(textX.get(i), paddingLeft +(i+1)*textXInterval,origin_Y+15, mTexPaint);
		}
		if(xDateType != null)
		    canvas.drawText(xDateType, mWidth  - paddingRight +5, origin_Y, mTexPaint);

		mTexPaint.setTextAlign(Paint.Align.RIGHT);
		Rect rect = new Rect();
		String text;
		for(int i = 0; i < degreeCount +1; i++)
		{   text = String.valueOf(textY[i]);
		    mTexPaint.getTextBounds(text, 0, text.length(), rect);
		    float h = rect.height() /2;
			canvas.drawText(String.valueOf(textY[i]), origin_X -5, (mHeight -paddingBottom -i*textYInterval)+h, mTexPaint);
		}
		if(yDateType != null){
		    mTexPaint.getTextBounds(yDateType, 0, yDateType.length(), rect);
		    canvas.drawText(yDateType, paddingLeft -5, paddingTop + textYInterval / 2 + (rect.height() /2), mTexPaint);
		}

	}

	private void initPointXY(Canvas canvas)
	{
	
		Point point = null;
		for(int i = 0; i < this.mPointYData.size(); i++)
		{
		    if(mPointYData.get(i) == null){
		        mPoints.add(null);
		        continue;
		    } 
		    point  = new Point();
		    point.x = (int) (origin_X + (i+1)*textXInterval);
		    point.y = (int) (origin_Y - f*mPointYData.get(i));
			
		    mPoints.add(point);
		}	
		

	}
	
	private void drawPoint(Canvas canvas){
	    mPaint.setColor(Color.WHITE);
	    int w = 7;
	    int h = 5;
        for(Point point :mPoints)
        {   if(point == null)
                continue;
            Path path = new Path();
            path.moveTo(point.x -w, point.y);
            path.lineTo(point.x, point.y - h);
            path.lineTo(point.x +w, point.y);
            path.lineTo(point.x, point.y + h);
            path.lineTo(point.x-w, point.y);
            canvas.drawPath(path, mPointPaint);
        }
       
        
	}
	
	private void drawCurve(Canvas canvas){
	    Path path =new Path();
        path.moveTo(getFirstVaildPoint().x, getFirstVaildPoint().y);
        for(Point point :mPoints){
            if(point != null)
                 path.lineTo(point.x, point.y);
        }
        mLinePaint.setStrokeWidth(3);
        canvas.drawPath(path, mLinePaint);
	    
	}
	private void drawPointText(Canvas canvas){
	    Point point;
	    for(int i = 0;i< mPoints.size();i++)
        {    
	        point = mPoints.get(i);
	        if(point == null)
	            continue;
            float x = point.x + 5;
            float y =  point.y;
            
            String st =  mPointYData.get(i).toString()+"Ԫ";
            if(mPointTextData != null){
                st = mPointTextData.get(i) + "Ԫ";
                        }
            Rect rect = new Rect();
            mTexPaint.getTextBounds(st, 0, st.length(), rect);
            int w = rect.width() + 10;
            int h = rect.height()+2;
            
            Path path = new Path();
            path.moveTo(x, y);
            path.lineTo(x, y);
            path.lineTo(x+5, y+(h/3));
            path.lineTo(x+5, y+(h*2/3));
            path.lineTo(x+5+w, y+(h*2/3));
            path.lineTo(x+5+w, y -(h*2/3));
            path.lineTo(x+5, y-(h*2/3));
            path.lineTo(x+5, y-(h/3));
            path.lineTo(x, y);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(Color.parseColor("#F0F000"));
            canvas.drawPath(path,mPaint );
            canvas.drawText(st, x +5+ w - 5, y + rect.height()/2, mTexPaint);

        }
	}	
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.drawColor(Color.WHITE);
		
		canvas.drawRect(10, 10, mWidth -10, mHeight -10, mTexPaint);
		darwAxis(canvas);
		drawData(canvas);
		if(mPointYData==null ||mPointYData.size()==0)
		{
			return;
		}	
		
		initPointXY(canvas);
		//drawShaderColor(canvas);
		//drawPointText(canvas);
		
		drawCurve(canvas);
		drawPoint(canvas);

	}

	private String split(String params) {

		if (params != null && params.indexOf(".") != -1) {
			params = params.substring(0, params.indexOf(".") + 2);

		}

		return params;
	}
	private void drawShaderColor(Canvas canvas){
      Path path = new Path();
      Point firstVaildPoint = getFirstVaildPoint();
      int firstVaildPointIndex = mPoints.indexOf(firstVaildPoint);
      if(minIndex <maxIndex){
          if(minIndex > firstVaildPointIndex){
              path.moveTo(firstVaildPoint.x, firstVaildPoint.y);
              for(int i = 0;i<= maxIndex ; i++){
                  if(mPoints.get(i)!= null)
                      path.lineTo(mPoints.get(i).x, mPoints.get(i).y);
              } 
              path.lineTo(mPoints.get(maxIndex).x,origin_Y);
              path.lineTo(firstVaildPoint.x, origin_Y);
              path.lineTo(firstVaildPoint.x, firstVaildPoint.y);
          }else{
              for(int i = minIndex;i<= maxIndex ; i++){
                  if(mPoints.get(i)!= null)
                      path.lineTo(mPoints.get(i).x, mPoints.get(i).y);
              } 
              path.lineTo(mPoints.get(maxIndex).x, origin_Y);
              path.lineTo(mPoints.get(minIndex).x, origin_Y);
              path.lineTo(mPoints.get(minIndex).x, mPoints.get(minIndex).y);
          }
          
      }else if(minIndex > maxIndex){
          
          path.moveTo(firstVaildPoint.x, firstVaildPoint.y);
          for(int i = 0;i<= minIndex ; i++){
              if(mPoints.get(i)!= null)
                  path.lineTo(mPoints.get(i).x, mPoints.get(i).y);
          } 
          path.lineTo(mPoints.get(minIndex).x,origin_Y);
          path.lineTo(firstVaildPoint.x, origin_Y);
          path.lineTo(firstVaildPoint.x,firstVaildPoint.y);
          
      }else{
          Point lastVailPoint = getLastVaildPoint();
          path.lineTo(firstVaildPoint.x,firstVaildPoint.y);
          path.lineTo(lastVailPoint.x, firstVaildPoint.y);
          path.lineTo(lastVailPoint.x, origin_Y);
          path.lineTo(firstVaildPoint.x, origin_Y);
          path.lineTo(firstVaildPoint.x, firstVaildPoint.y);
      }
      
      Paint paint = new Paint();
      paint.setAntiAlias(true);
      paint.setStyle(Paint.Style.FILL);
      int[] colors = new int[] { Color.parseColor("#2EB7A5") ,Color.TRANSPARENT };
      int left = (mPoints.get(maxIndex).x- mPoints.get(minIndex).x) /2 +   mPoints.get(minIndex).x;
      int top =  mPoints.get(maxIndex).y;
      Shader colorsShader = new LinearGradient(left, top, left, origin_Y, colors, null, TileMode.MIRROR);
      paint.setShader(colorsShader);
      canvas.drawPath(path,paint );
    }
	
	private Point getFirstVaildPoint(){
	    for(int i = 0;i< mPoints.size();i++){
	        if(mPoints.get(i) != null){
	            return mPoints.get(i);
	        }
	    }
	    return null;
	}
	
	private Point getLastVaildPoint(){
	    for(int i = mPoints.size()-1;i >=0;i--){
            if(mPoints.get(i) != null){
                return mPoints.get(i);
            }
        }
	    return null;
	}
	public void clearData(){
        mPointYData = null;
        invalidate();
    }
}
