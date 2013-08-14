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
import android.view.View;

import com.dbstar.R;


public class PowerTrendPolyLineView extends View{
    
    
    
    float mWidth=0;      
    float mHeight=0;     
    float paddingLeft=0;
    float paddingTop=0;
    float paddingRight=0;
    float paddingBottom=0;
    float contentHeight = 0;
    float contentWidth = 0;
    protected Context mContext;
     
	private float textXInterval;
	private float textYInterval;
	private int textXCount;

	private ArrayList<String> textX;  
	private String[] textY; 
	private String xDateType;
	private String yDateType;
	private float maxValue;
	private float minValue;
	private float f;

	private Paint mLinePaint = null;
	private Paint mPointPaint = null;
	private Paint mTexPaint = null;
	private Paint mAxisPaint = null;
	private ArrayList<Float> mPointData;
	private ArrayList<String> mPointTextData;
	private float origin_Y;
	private float origin_X;
	private Paint paint = null;

	int minIndex = 0;
    int maxIndex = 0;
    private ArrayList<Float> data;
    private int mTextYCount;
    ArrayList<Point> mPoints;
    private float mDensity;
    private boolean isMBias;
    private double maxStrLength;
    
	public PowerTrendPolyLineView(Context context) {
		super(context);
		mContext = context;
		mDensity = context.getResources().getDisplayMetrics().density;  
		this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		init();
	}
	
	public void setFrame(float mWidth, float mHeight, float paddingLeft,
	        float paddingTop, float paddingRight, float paddingBottom) {
	    this.mWidth = mWidth;
        this.mHeight = mHeight;     
        this.paddingLeft = dip2px(paddingLeft);
        this.paddingTop = dip2px(paddingTop);
        this.paddingRight = dip2px(paddingRight);
        this.paddingBottom = dip2px(paddingBottom);
        this.contentHeight = this.mHeight- this.paddingBottom - this.paddingTop;
        this.contentWidth = this.mWidth - this.paddingLeft - this.paddingRight;
        origin_X = this.paddingLeft;
        origin_Y =  this.paddingTop + this.contentHeight / 2;
	}
	private void init(){
	    mTextYCount = 10;
	    mPoints = new ArrayList<Point>();
	    mLinePaint = new Paint();
	    mLinePaint.setAntiAlias(true);
	    mLinePaint.setStyle(Paint.Style.STROKE);
	    mLinePaint.setStrokeCap(Paint.Cap.BUTT);
	    mLinePaint.setStrokeWidth(dip2px(3));
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
        
	    mAxisPaint = new Paint();
	    mAxisPaint.setAntiAlias(true);
	    mAxisPaint.setStyle(Paint.Style.FILL);
	    
	    
	}
	
	public void setData(ArrayList<Float> pointData,ArrayList<String> xTextdata,String xType,String yType)
	{
		this.mPointData = pointData;
		xDateType =xType;
		yDateType = yType;
		textXCount = xTextdata.size();
		textX = xTextdata;
		
		calculateMinMaxValue();
		
		initValue();
		
        maxStrLength = 0;
        double strLenth;
        for (int i = 0; i < textX.size(); i++) {
            strLenth = mTexPaint.measureText(textX.get(i));
            if (maxStrLength < strLenth) {
                maxStrLength = strLenth;
            }
        }
        if (maxStrLength > (textXInterval * 2 / 3)) {
            isMBias = true;
            // 判断倾斜后，画刻度值是否超出边界
            if (paddingBottom < (maxStrLength + dip2px(15))) {
                paddingBottom = (int) (maxStrLength + dip2px(15));
                setFrame(mWidth, mHeight, paddingLeft, paddingTop,
                        paddingRight, paddingBottom);
                
                // 重新计算大小
                calculateMinMaxValue();
                initValue();
            }
          
        }else{
            isMBias = false;
        }
		
	}
	
	
	/**
	 * 计算最大值和最小值
	 */
	private void calculateMinMaxValue(){
	    maxValue = 0;
	    minValue = Integer.MAX_VALUE;
	    if(mPointData == null)
	        return;
        for (int i = 0; i < mPointData.size(); i++) {
            if (mPointData.get(i) == null)
                continue;
            if (maxValue <= Math.abs(mPointData.get(i))) {
                maxValue = Math.abs(mPointData.get(i));
                maxIndex = i;
            }
            if (minValue >= Math.abs(mPointData.get(i))) {
                minValue = Math.abs(mPointData.get(i));
                minIndex = i;
            }
        }
	}
	/**
	 *初始化数据值 
	 */
    private void initValue() {

        // x轴每个单位的间隔距离
        textXInterval = (contentWidth) / (textXCount + 1);

       
        
        if (maxValue == 0) {
            f = 0;
            maxValue = 100;
        } else {
            f = (contentHeight) / (2 * maxValue);
        }
        // y轴每个单位的间隔距离
        textYInterval = (contentHeight) / mTextYCount;
        
        
        textY = new String[mTextYCount + 1];

        for (int i = -mTextYCount / 2; i < mTextYCount / 2 + 1; i++) {

            textY[i + mTextYCount / 2] = CommondTools.round(maxValue
                    / (mTextYCount / 2) * i, 0);

        }
        
        
        for (int i = 0; i < textY.length; i++) {
            if (maxValue / 1000000 > 1) {
                if (i < 6) {
                    textY[i] = "-"
                            + split(String
                                    .valueOf(Double.valueOf(textY[i].replace(
                                            "-", "").replace(",", "")) / 1000000))
                            + "M";

                } else {
                    textY[i] = split(String.valueOf(Double.valueOf(textY[i]
                            .replace("-", "").replace(",", "")) / 1000000))
                            + "M";
                }
            } else if (maxValue < 1000000 && maxValue > 1000) {
                if (i < 6) {
                    textY[i] = "-"
                            + split(String.valueOf(Double.valueOf(textY[i]
                                    .replace("-", "").replace(",", "")) / 1000))
                            + "K";
                } else {
                    textY[i] = split(String.valueOf(Double.valueOf(textY[i]
                            .replace("-", "").replace(",", "")) / 1000)) + "K";
                }
            }
        }
        
        
    }
    
    
    
	private void drawAxis(Canvas canvas){	
	    
	    mAxisPaint.setColor(Color.GRAY);
	    mAxisPaint.setStrokeWidth(dip2px(2));
	    
	    //画最下边的 线
		canvas.drawLine(origin_X-dip2px(3) ,paddingTop + contentHeight, mWidth -paddingRight ,paddingTop + contentHeight, mAxisPaint);
		// 换Y轴
		canvas.drawLine(origin_X , paddingTop , origin_X ,paddingTop + contentHeight + dip2px(3) , mAxisPaint);
		
		//画Y轴刻度线
		for (int i = 0; i < mTextYCount; i++) {
			canvas.drawLine(origin_X-dip2px(3) , paddingTop +textYInterval*i,mWidth -paddingRight, paddingTop +textYInterval*i, mAxisPaint);
		}
		
		//  画x轴刻度线
		for(int i= 0;i< textX.size();i++){
		    float startX = origin_X +(i+1)*textXInterval;
		    float stopX =  origin_X +(i+1)*textXInterval;
		    float startY = paddingTop+textYInterval*mTextYCount;
            float stopY = paddingTop+textYInterval*mTextYCount + dip2px(3);
		    canvas.drawLine(startX,startY,stopX,stopY, mAxisPaint);
		}
		
		// 画右边的标识信息
		
		Path path  = new Path();
		float x = mWidth - paddingRight + dip2px(30);
		float y = mHeight/2;
		path.moveTo(x, y);
		path.lineTo(x + dip2px(40), y);
		canvas.drawPath(path, mLinePaint);
		
		canvas.drawRect(x + dip2px(15), y -dip2px(3), x + dip2px(25), y+dip2px(3) , mLinePaint);
		
		canvas.drawText(mContext.getString(R.string.family_text_power_count), x + dip2px(40), y+dip2px(5), mTexPaint);
	} 
	
	/**
	 * 画XY的刻度值
	 * @param canvas
	 */
    public void drawData(Canvas canvas) {

        Path path = new Path();
        mTexPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        
        
        // 判读x轴的刻度值是否需要倾斜
        double x = textXInterval ;
        double y = 0;
        if(isMBias){
            // 判断倾斜后，画刻度值是否超出边界
            x = textXInterval / 2;
            y = Math.sqrt((maxStrLength * maxStrLength) - (x * x));

        }else{
            mTexPaint.setTextAlign(Paint.Align.CENTER);
        }
        
        // 画x轴刻度值
        for (int i = 0; i < textX.size(); i++) {
            path.reset();
            path.moveTo(origin_X + textXInterval * i, (float) (paddingTop + contentHeight + y));
            path.lineTo((float) (origin_X + textXInterval * i + x), paddingTop + contentHeight);
            canvas.drawTextOnPath(textX.get(i), path, 0, dip2px(15), mTexPaint);
        }
        
        
        if (xDateType != null)
            canvas.drawText(xDateType, mWidth - paddingRight + 5, origin_Y,
                    mTexPaint);

        
        //  画 y轴刻度
        mTexPaint.setTextAlign(Paint.Align.RIGHT);
        Rect rect = new Rect();
        String text;
        for (int i = 0; i < mTextYCount + 1; i++) {
            text = String.valueOf(textY[i]);
            mTexPaint.getTextBounds(text, 0, 1, rect);
            float h = rect.height() / 2;
            canvas.drawText(String.valueOf(textY[i]) + yDateType, origin_X - 5,
                    (paddingTop + contentHeight - (i * textYInterval)) + h,
                    mTexPaint);
        }

        mTexPaint.setStyle(Paint.Style.STROKE);
    }

	private void initPointXY(Canvas canvas)
	{
	
		Point point = null;
		for(int i = 0; i < this.mPointData.size(); i++)
		{
		    if(mPointData.get(i) == null){
		        mPoints.add(null);
		        continue;
		    } 
		    point  = new Point();
		    point.x = (int) (origin_X + (i+1)*textXInterval);
		    point.y = (int) (origin_Y - f*mPointData.get(i));
			
		    mPoints.add(point);
		}	
		

	}
	
	private void drawPoint(Canvas canvas){
	    mAxisPaint.setColor(Color.WHITE);
	    float w = dip2px(5);
	    float h = dip2px(3);
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
	
	private void drawLine(Canvas canvas){
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
            
            String st =  mPointData.get(i).toString()+"Ԫ";
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
            mAxisPaint.setStyle(Paint.Style.FILL);
            mAxisPaint.setColor(Color.parseColor("#F0F000"));
            canvas.drawPath(path,mAxisPaint );
            canvas.drawText(st, x +5+ w - 5, y + rect.height()/2, mTexPaint);

        }
	}	
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.WHITE);
		
		mTexPaint.setStyle(Paint.Style.STROKE);
		canvas.drawRect(dip2px(10), dip2px(10), mWidth -dip2px(10), mHeight -dip2px(10), mTexPaint);
		
        if (mPointData == null || mPointData.size() == 0) {
            return;
        }
		drawAxis(canvas);
		drawData(canvas);
		initPointXY(canvas);
		drawLine(canvas);
		drawPoint(canvas);
		
		//drawShaderColor(canvas);
       //drawPointText(canvas);
        

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
	    mPointData = null;
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
	
    private int px2dip(float pxValue) {
        return (int) (pxValue / mDensity + 0.5f);
    }
    private int dip2px(float dpValue) {  
        return (int) (dpValue * mDensity + 0.5f);  
    }  
}
