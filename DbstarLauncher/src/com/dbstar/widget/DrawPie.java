package com.dbstar.widget;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class DrawPie extends DrawBase{

	private ArrayList<RectF> rectFs = new ArrayList<RectF>();
	private RectF         rectF; 
	private int           length;
	private float[]       sweep; 
	private int           m_iDepth;   
	private boolean       useCenter        = true;
	private Paint mPaint;
	private ArrayList<Integer> mColors = new ArrayList<Integer>();
	public DrawPie(Context context) {
		super(context);
		initPaint();
	}
	
	private void initPaint(){
	    mPaint = new Paint();
	    mPaint.setAntiAlias(true);
	    mPaint.setStrokeWidth(2);
	    mPaint.setStyle(Paint.Style.FILL);
	  
	}
	
    public ArrayList<Integer> initColor(int count){
        if(mColors.size() > count){
            return mColors;
        }
        mColors.clear();
        mColors.add(Color.RED);
        mColors.add(Color.BLUE);
        mColors.add(Color.CYAN);
        mColors.add(Color.DKGRAY);
        mColors.add(Color.GRAY);
        mColors.add(Color.LTGRAY);
        mColors.add(Color.MAGENTA);
        mColors.add(Color.YELLOW);
        mColors.add(Color.BLACK);
        mColors.add(Color.WHITE);
        mColors.add(Color.GREEN);
       
        int j = 0;
        while (count >= mColors.size()) {
                int i = j +1;
                
                int R1 = Color.red(mColors.get(i));
                int G1 = Color.green(mColors.get(i));
                int B1 = Color.blue(mColors.get(i));

                int R2 = Color.red(mColors.get(j));
                int G2 = Color.green(mColors.get(j));
                int B2 = Color.blue(mColors.get(j));

                int R = (R1 + R2) / (2 );
                int G = (G1 + G2) / (2 );
                int B = (B1 + B1) / (2);
                mColors.add(Color.argb( 255, R, G, B));
            j++;
            
        }
        mColors.remove(10);
        mColors.add(Color.GREEN);
        return mColors;
        
    }
	public void setData(ArrayList<Float> percent)//
	{
	    
		setPos();
		float    angle       = 0;
		float    total       = 0;
		length = percent.size();
		sweep                = new float[length];
		for(int i=0;i<length;i++)
		{
			total            += percent.get(i);
		}
		if(total==0){
		    angle            = 0;
		    rectFs.clear();
		   return;
		}
		else{
		    angle=360/total;
		}
		
//		length = length - 1;
		total=0;
			for(int i=0;i<length-1;i++)
			{
				sweep[i]           = angle* percent.get(i);
				total               += sweep[i];
			}
			sweep[length-1] = 360-total;
		
			insertSort(sweep);
//		length = length+1;

		for(int i=0;i<m_iDepth;i++)
		{
			rectFs.add(new RectF(rectF.left,rectF.top+i,rectF.right,rectF.bottom+i));  	
		}	 
	}

    public void insertSort(float[] args) {
        for (int i = 1; i < args.length; i++) {
            for (int j = i; j > 0; j--) {
                if (args[j] > args[j - 1]) {
                    float temp = args[j - 1];
                    args[j - 1] = args[j];
                    args[j] = temp;
                } else
                    break;
            }
        }
    }
	public void setChartDepth(int iDepth)
	{
		m_iDepth = dip2px(iDepth);
	}

	public void setPos()
	{
		rectF         = new RectF();
		rectF.left    = 0;
		rectF.top     = 0;
		rectF.right   = rectF.left+mWidth ;
		rectF.bottom  = rectF.top+mHeight ;
	}

	public void drawPie(Canvas canvas)
	{
		//setPaint();
		float start  =  0;	
		for(int i=0;i<length;i++)
		{ 
		    if(i == length -1){
		       mPaint.setColor(mColors.get(mColors.size() -1));
		    }else{
		        mPaint.setColor(mColors.get(i));
		    }
			canvas.drawArc(rectF, start, sweep[i], useCenter,mPaint);
			start=start+sweep[i];
		}
	}

	public void drawArcPie(Canvas canvas)
	{
		setPaint();  
		float start       = 0;
		int j=0;
		float[] start2    = new float[length];
		while(start<=90)
		{
			start         = start+sweep[j];
			if(start<=90.1)
			{
				start2[j] = sweep[j];
			}

			j++;
		}
		start=0;
		for(int s=0;start<=90;s++)
		{     
		    int c = mColors.get(s);
            int R = Color.red(c);
            int G = Color.green(c);
            int B = Color.blue(c);
            
            int max = Math.max(R, G);
            max = Math.max(max, B);
            if(R == max){
                R = R/2;
            }else if(G == max){
                G = G/2;
            }else{
                B = B/2;
            }
            mPaint.setColor(Color.rgb(R, G, B));
			for(int i=0;i<rectFs.size();i++)
			{
				canvas.drawArc(rectFs.get(i), start, start2[s], useCenter, mPaint);
			}
			start=start+sweep[s];
		}
		float     main   = 0;
		int       count  = 0;
		float[]   edn2   = new float[length];
		while(main<=180)
		{
			main=main+sweep[count];
			if(main>180)
				edn2[count]=sweep[count]-(main-180);
			else
				edn2[count]=sweep[count];
			count++;
		}

		float end       = 180;
		for(int m=count-1;end>90;m--)
		{   
		    int c = mColors.get(m);
            int R = Color.red(c);
            int G = Color.green(c);
            int B = Color.blue(c);
            
            int max = Math.max(R, G);
            max = Math.max(max, B);
            if(R == max){
                R = R/2;
            }else if(G == max){
                G = G/2;
            }else{
                B = B/2;
            }
            mPaint.setColor(Color.rgb(R, G, B));
			end       = end-edn2[m];
			for(int i=0;i<rectFs.size();i++)
			{
				canvas.drawArc(rectFs.get(i), end, edn2[m], useCenter, mPaint);
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if(rectFs == null || rectFs.isEmpty())
		    return;
		canvas.translate(originX, originY);
		drawArcPie(canvas);
		drawPie(canvas);	
	}
	
	public void clearData(){
	    rectFs.clear();
	    invalidate();
	}
}
