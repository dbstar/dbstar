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
import android.util.Log;

import com.dbstar.R;


public class PowerTrendPolyLineView extends DrawBase{
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
	public PowerTrendPolyLineView(Context context) {
		super(context);
		init();
	}
	
	@Override
	public void setFrame(int mWidth, int mHeight, int paddingLeft,
	        int paddingTop, int paddingRight, int paddingBottom) {
	    // TODO Auto-generated method stub
	    super.setFrame(mWidth, mHeight, paddingLeft, paddingTop, paddingRight,
	            paddingBottom);
	    setOriginPoint(paddingLeft, mHeight-paddingBottom);
	}
	private void init(){
	    degreeCount = 10;
	    mPoints = new ArrayList<Point>();
	    mLinePaint = new Paint();
	    mLinePaint.setAntiAlias(true);
	    mLinePaint.setStyle(Paint.Style.STROKE);
	    mLinePaint.setStrokeCap(Paint.Cap.BUTT);
	    mLinePaint.setStrokeWidth(3);
	    mLinePaint.setColor(Color.parseColor("#c14e51"));
	    
	    mPointPaint = new Paint();
	    mPointPaint.setAntiAlias(true);
	    mPointPaint.setStyle(Paint.Style.FILL_AND_STROKE);
	    mPointPaint.setStrokeCap(Paint.Cap.ROUND);
	    mPointPaint.setColor(Color.parseColor("#c14e51"));
        
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
	      if(maxData == 0)
	        {
	            f=0;
	            maxData = 100;
	        }
	        else
	        {
	            f = (mHeight  - paddingTop  - paddingBottom )/(2*maxData);
	        }   
	        textYInterval = (mHeight  - paddingTop  - paddingBottom )/degreeCount;
	         origin_Y = paddingTop+textYInterval*5;
	        origin_X = paddingLeft;
	        textY = new String[degreeCount+1];  

	        for(int i = - degreeCount/2; i <degreeCount/2+1; i++)
	        {

	            textY[i + degreeCount /2 ] = CommondTools.round(maxData/(degreeCount/2)*i,0);

	        }
		/*******   yang.xu        2012-11-29  V4.0.6       BUG-0000994   begin ******/
	        for(int i = 0; i < textY.length; i++)
	        {
	            if(maxData/1000000 > 1){
	                if(i<6){
	                    textY[i] = "-"+split(String.valueOf(Double.valueOf(textY[i].replace("-", "").replace(",", ""))/1000000))+"M";

	                }else{
	                    textY[i] = split(String.valueOf(Double.valueOf(textY[i].replace("-", "").replace(",", ""))/1000000))+"M";
	                }
	            }else if(maxData < 1000000 && maxData > 1000){
	                if(i<6){
	                    textY[i] = "-"+split(String.valueOf(Double.valueOf(textY[i].replace("-", "").replace(",", ""))/1000))+"K";
	                }else{
	                    textY[i] = split(String.valueOf(Double.valueOf(textY[i].replace("-", "").replace(",", ""))/1000))+"K";
	                }
	            }
	        }
		/*******   yang.xu        2012-11-29  V4.0.6       BUG-0000994   end ******/
		textX = xTextdata;
	}

	private void darwAxis(Canvas canvas){	
	    mPaint.setColor(Color.GRAY);
	    mPaint.setStrokeWidth(2);
		canvas.drawLine(origin_X-3 ,paddingTop+textYInterval*degreeCount, mWidth -paddingRight ,paddingTop +textYInterval*degreeCount, mPaint);
		canvas.drawLine(origin_X , paddingTop , paddingLeft ,paddingTop+textYInterval*degreeCount+3 , mPaint);
		 mPaint.setStrokeWidth(1);
		for (int i = 0; i < degreeCount; i++) {
			canvas.drawLine(origin_X-3 , paddingTop +textYInterval*i,mWidth -paddingRight, paddingTop +textYInterval*i, mPaint);
		}
		
		
		for(int i= 0;i< textX.size();i++){
		    float startX = origin_X +(i+1)*textXInterval;
		    float stopX =  origin_X +(i+1)*textXInterval;
		    float startY = paddingTop+textYInterval*degreeCount;
            float stopY = paddingTop+textYInterval*degreeCount + 3;
		    canvas.drawLine(startX,startY,stopX,stopY, mPaint);
		}
		
		
		Path path  = new Path();
		int x = mWidth - paddingRight + 30;
		int y = mHeight/2;
		path.moveTo(x, y);
		path.lineTo(x + 40, y);
		canvas.drawPath(path, mLinePaint);
		
		canvas.drawRect(x + 15, y -3, x + 25, y+3 , mLinePaint);
		
		canvas.drawText(mContext.getString(R.string.family_text_power_count), x + 40, y+5, mTexPaint);
	} 

	public void drawData(Canvas canvas)
	{
	    
	  
	    mTexPaint.setTextAlign(Paint.Align.CENTER);
	    int  y = (int) (paddingTop+textYInterval*degreeCount + 15);
		for(int i = 0; i < textX.size(); i++)
		{
			canvas.drawText(textX.get(i), originX+textXInterval*(i+1)- textXInterval/2, y , mTexPaint);
		}
		if(xDateType != null)
		    canvas.drawText(xDateType, mWidth  - paddingRight +5, origin_Y, mTexPaint);

		mTexPaint.setTextAlign(Paint.Align.RIGHT);
		Rect rect = new Rect();
		String text;
		for(int i = 0; i < degreeCount +1; i++)
		{   text = String.valueOf(textY[i]);
		    mTexPaint.getTextBounds(text, 0, 1, rect);
		    float h = rect.height() /2;
			canvas.drawText(String.valueOf(textY[i]) + yDateType, origin_X -5, (mHeight -paddingBottom -i*textYInterval)+h, mTexPaint);
		}
//		if(yDateType != null){
//		    mTexPaint.getTextBounds(yDateType, 0, 3, rect);
//		    canvas.drawText(yDateType, paddingLeft -5, paddingTop + textYInterval / 2 + (rect.height() /2), mTexPaint);
//		}

	}

	private void initPointXY(Canvas canvas)
	{
	
		 
		Log.e("drawCurve", "drawCurve");
//		for(int i = 0; i < textXSize; i++)
//		{	
//			Log.e("data.get(i)", ""+data1.get(i));
//		}

		Log.e("paddingLeft  2 ", paddingLeft +"");

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
	    int w = 5;
	    int h = 3;
        for(Point point :mPoints)
        {   if(point == null)
                continue;
            Path path = new Path();
            path.moveTo(point.x -w, point.y-h);
            path.lineTo(point.x +w, point.y - h);
            path.lineTo(point.x +w, point.y+ h);
            path.lineTo(point.x -w, point.y + h);
            path.lineTo(point.x-w, point.y -h);
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
		if(mPointYData==null || mPointYData.size()==0)
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
	
	public void clearData(){
	    mPointYData = null;
	    invalidate();
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
}
