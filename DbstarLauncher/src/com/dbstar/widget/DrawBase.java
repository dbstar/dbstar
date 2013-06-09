package com.dbstar.widget;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;


public class DrawBase extends View{

	private final String TAG="SmartPowerDrawBase"; 
	int mWidth=0;      
	int mHeight=0;     
	int paddingLeft=0;
	int paddingTop=0;
	int paddingRight=0;
	int paddingBottom=0;
	 
	protected Context mContext;
	 
	int originX=0;    
	int originY=0;   


	public static final int[]   color            = new int[]{

		0XFFFF4F00,
		0XFF00ECFF,
		0XFF72FF00,
		0XFF0165FF,0XFF6602FE,
		0XFF009244,0XFF99CE01,0XFF2F31CF,0XFFFF3433,0XFFFDAB00,
		0XFFFF0260,0XFF0034CB,0XFFCD0194,0XFFFF7E00,0XFF00FFFF,

		0XFFFF9569,0XFF82F6FE,0XFFBEFF86,0XFF62A0FF,0XFF6634CC,
		0XFF04FC7D,0XFFCBFF65,0XFF6568FF,0XFFFE6765,0XFFFED656,
		0XFFFF327F,0XFF0067FD,0XFFFE65CC,0XFFFFCD31,0XFF66FFFF

	};

	Paint[]         mPaints          = new Paint[30];
	Paint           textPaint        = null;
    private float mDensity;  
	
	public DrawBase(Context context) {
		super(context);
		this.mContext = context;
		mDensity = context.getResources().getDisplayMetrics().density;  
		setPaint();
	}

	/**
	 * set the paint
	 * @return 
	 */
	public void setPaint()
	{
		mPaints[0] = new Paint();
		mPaints[0].setAntiAlias(true);
		mPaints[0].setStrokeWidth(2);
		mPaints[0].setStyle(Paint.Style.FILL);
		int length = mPaints.length;
		for(int i = 0; i < length; i++)
		{
			mPaints[i] = new Paint(mPaints[0]);
			mPaints[i].setColor(color[i]);
		}
		textPaint = new Paint();
		textPaint.setTextSize(15);
		textPaint.setColor(0xFFFFFFFF);
		textPaint.setAntiAlias(true);
		textPaint.setTypeface(Typeface.SANS_SERIF);
		Log.i(TAG, "setPaint");
	}

	/**
	 * set Picture Size
	 * @param iWidth:Picture Width; 
	 *        iHeight:Picture Height
	 * @return 
	 */
	public void setPicSize(int mWidth,int mHeight)
	{
		this.mWidth = mWidth;
		this.mHeight = mHeight;
		Log.i(TAG, "mWidth="+mWidth+"---mHeight="+mHeight);

	}


	public void setFrame(int mWidth,int mHeight,int paddingLeft,int paddingTop,int paddingRight,int paddingBottom){
		this.mWidth = mWidth;
		this.mHeight = mHeight;		
		this.paddingLeft = dip2px(paddingLeft);
        this.paddingTop = dip2px(paddingTop);
        this.paddingRight = dip2px(paddingRight);
        this.paddingBottom = dip2px(paddingBottom);
		Log.i(TAG, "mWidth="+mWidth+"---mHeight="+mHeight+"---paddingLeft="+paddingLeft+"---paddingTop="+paddingTop+"---paddingRight="+paddingRight+"---paddingBottom="+paddingBottom);
	}
	
	public void setOriginPoint(int originX, int originY)
	{
		this.originX = originX;
		this.originY = originY; 
		Log.i(TAG, "originX="+originX+"---originY="+originY);
	}
	
    public int px2dip(float pxValue) {
        return (int) (pxValue / mDensity + 0.5f);
    }

    public int dip2px(float dpValue) {
        return (int) (dpValue * mDensity + 0.5f);
    }
}
