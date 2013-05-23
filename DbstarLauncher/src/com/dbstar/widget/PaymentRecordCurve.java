package com.dbstar.widget;

import java.util.ArrayList;

import com.dbstar.R;

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

/**********************************************************************************
Copyright (C), 2011-2012, 北京国电通网络技术有限公司. 
FileName:     com.smartlife.pad.view.SmartPowerDrawDoubleCurveContrast.java
Author:　     　　lei.peng
Version :     V4.0.1
Date:         2012-08-03
Description:　-》双曲线比较图(家庭能效=用电分析-用电趋势分析双折线图)

 ***********************************************************************************
 History:　　　update past records 
 <Author>　          <Date>　　 <Version>　  <Description> 
lei.peng       2012-08-03  V4.0.1       build
***********************************************************************************/ 

public class PaymentRecordCurve extends DrawBase{
	private float textXInterval;//x轴文字间距
	private float textYInterval;//y轴文字间距
	private int textXSize;//X轴数个数

	private int[] textX;  //X轴数字
	private String[] textY; //Y轴数字
	private String dateType;
	private float maxData;//最大的数据
	private float minData;
	private float f;//每单位尺寸所占的数据大小

	private Paint mLinePaint = null;//第一条折线画笔
	private Paint mPointPaint = null;
	private Paint mTexPaint = null;
	private Paint mPaint = null;
	private ArrayList<Float> mPointYData;//第一条折线数据
	private ArrayList<String> mPointTextData;//第一条折线数据
	
	private float origin_Y;//坐标轴原点Y坐标
	private float origin_X;//坐标轴原点X坐标
	private Paint paint = null;//白色画笔，用于画拐点圆圈

//	private float[] xCircle1;//折线1 拐点x坐标
//	private float[] yCircle1;//折线1 拐点y坐标
	int minIndex = 0;
    int maxIndex = 0;
    private ArrayList<Float> data;
    private int degreeCount;
    ArrayList<Point> mPoints;
	public PaymentRecordCurve(Context context) {
		super(context);
		init();
	}
	
	private void init(){
	    degreeCount = 5;
	    mPoints = new ArrayList<Point>();
	    mLinePaint = new Paint();
	    mLinePaint.setAntiAlias(true);
	    mLinePaint.setStyle(Paint.Style.STROKE);
	    mLinePaint.setStrokeCap(Paint.Cap.ROUND);
	    mLinePaint.setColor(Color.parseColor("#2EB7A5"));
	    
	    mPointPaint = new Paint();
	    mPointPaint.setAntiAlias(true);
	    mPointPaint.setStyle(Paint.Style.FILL_AND_STROKE);
	    mPointPaint.setStrokeCap(Paint.Cap.ROUND);
	    mPointPaint.setColor(Color.parseColor("#2EB7A5"));
        
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
	
	/**
	 *  根据传入的日期，决定是画月日还是小时图，X轴分段；根据传入的数据，高度分段
	 *  @return null
	 */
	public void setData(ArrayList<Float> yData,ArrayList<String> pointTextdata,ArrayList<Integer> xData)
	{
		this.mPointYData = yData;
		this.mPointTextData = pointTextdata;
		dateType = mContext.getString(R.string.mypower_text_brackets_month);
		textXSize = xData.size();
		
//		xCircle1 = new float[mDataCount];
//		yCircle1 = new float[mDataCount];

		//计算出x轴文字间距(+1:右边流出边距)
		textXInterval = (mWidth  - paddingLeft  - paddingRight )/(textXSize+1);
		//获取最大值（作为Y轴最高点）
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
			f = (mHeight  - paddingTop  - paddingBottom )/(maxData);//单位Y轴代表的实际数据
		}	
		//计算出y轴文字间距
		textYInterval = (mHeight  - paddingTop  - paddingBottom )/degreeCount;
		origin_Y = paddingTop+textYInterval*degreeCount;
		origin_X = paddingLeft;
		//y轴上的刻度值
		textY = new String[degreeCount+1];	

		for(int i = 0; i <degreeCount+1; i++)
		{

			textY[i] = CommondTools.round(maxData/degreeCount*i,0);

		}
		/*******   yang.xu        2012-11-29  V4.0.6       BUG-0000994   begin ******/
		for(int i = 0; i < textY.length; i++)
		{
			if(maxData/1000000 > 1){
				textY[i] = split(String.valueOf(Double.valueOf(textY[i].replace("-", "").replace(",", ""))/1000000))+"M";
			}else if(maxData < 1000000 && maxData > 1000){
				textY[i] = split(String.valueOf(Double.valueOf(textY[i].replace("-", "").replace(",", ""))/1000))+"K";
			}
		}
		/*******   yang.xu        2012-11-29  V4.0.6       BUG-0000994   end ******/
		//x轴上的刻度值
		textX = new int[textXSize];
		for(int i = 0; i < textXSize; i++)
		{
			textX[i] = xData.get(i);
		}	
	}

	/**
	 * 画xy坐标轴
	 * @param canvas
	 */
	private void darwAxis(Canvas canvas){	
		/**
		 * 此处于校准上下左右间距 ，若感觉坐标轴距离上下左右距离不合适可调整这些值（以后再改进）
		 */
	    mPaint.setColor(Color.GRAY);
	    mPaint.setStrokeWidth(2);
		//画x轴
		canvas.drawLine(origin_X ,paddingTop+textYInterval*degreeCount, mWidth -paddingRight ,paddingTop +textYInterval*degreeCount, mPaint);
		//画y轴
		canvas.drawLine(origin_X , paddingTop , paddingLeft ,paddingTop+textYInterval*degreeCount , mPaint);
		 mPaint.setStrokeWidth(1);
		//画y轴刻度
		for (int i = 0; i < degreeCount; i++) {
			canvas.drawLine(origin_X , paddingTop +textYInterval*i,mWidth -paddingRight, paddingTop +textYInterval*i, mPaint);
		}
		
		
		for(int i= 0;i< textX.length;i++){
		    float startX = origin_X +(i+1)*textXInterval;
		    float stopX =  origin_X +(i+1)*textXInterval;
		    float startY = origin_Y;
		    float stopY = origin_Y - 3;
		    canvas.drawLine(startX,startY,stopX,stopY, mPaint);
		}


	} 

	/**
	 *  画坐标轴数字
	 *  @return null
	 */
	public void drawData(Canvas canvas)
	{
	    
	  
		//画x轴
	    mTexPaint.setTextAlign(Paint.Align.CENTER);
		for(int i = 0; i < textX.length; i++)
		{
			//(-5,使文字与拐点对齐).
			canvas.drawText(String.valueOf(textX[i]), paddingLeft +(i+1)*textXInterval,origin_Y+15, mTexPaint);
		}
		//x轴单位
		canvas.drawText(dateType, mWidth  - paddingRight +5, origin_Y, mTexPaint);

		//画Y轴
		mTexPaint.setTextAlign(Paint.Align.RIGHT);
		Rect rect = new Rect();
		String text;
		for(int i = 0; i < degreeCount +1; i++)
		{   text = String.valueOf(textY[i]);
		    mTexPaint.getTextBounds(text, 0, text.length(), rect);
		    float h = rect.height() /2;
			canvas.drawText(String.valueOf(textY[i]), origin_X -5, (mHeight -paddingBottom -i*textYInterval)+h, mTexPaint);
		}
		//y轴单位
		 String str = mContext.getString(R.string.mypower_text_brackets_yuan);
		 mTexPaint.getTextBounds(str, 0, str.length(), rect);
		 
		canvas.drawText(str, paddingLeft -5, paddingTop + textYInterval / 2 + (rect.height() /2), mTexPaint);

	}

	/**
	 *  设置画笔（1支时）
	 *  @return null
	 */
	
	/**
	 *  画折线图
	 *  @return null
	 */
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
        //在每个拐点画圆
        for(Point point :mPoints)
        {   if(point == null)
                continue;
            canvas.drawCircle(point.x, point.y, 5, mPaint);
            canvas.drawCircle(point.x, point.y, 4, mPointPaint);
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
            
            String st =  mPointYData.get(i).toString()+ mContext.getString(R.string.mypower_text_brackets_yuan);
            if(mPointTextData != null){
                st = mPointTextData.get(i) + mContext.getString(R.string.mypower_text_brackets_yuan);
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
		darwAxis(canvas);
		drawData(canvas);//画数据
		if( mPointYData==null || mPointYData.size()==0)
		{
			return;
		}	
		
		initPointXY(canvas);
		drawShaderColor(canvas);
		drawPointText(canvas);
		
		drawCurve(canvas);//画折线
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
