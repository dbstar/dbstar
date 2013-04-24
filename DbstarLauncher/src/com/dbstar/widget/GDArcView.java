package com.dbstar.widget;

import com.dbstar.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class GDArcView extends View {

	private float mRadius;
	private float mStartAngle, mSweepAngle;
	private int mColor;

	public GDArcView(Context context) {
		this(context, null);
	}

	public GDArcView(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.GDArcView);

		mRadius = a.getFloat(R.styleable.GDArcView_circleRadius, 0);
		mStartAngle = a.getFloat(R.styleable.GDArcView_startAngle, 0);
		mSweepAngle = a.getFloat(R.styleable.GDArcView_sweepAngle, 0);
		mColor = a.getColor(R.styleable.GDArcView_color, 0);
		a.recycle();

		setFocusable(false);
	}
	
	public void setSweepAngle(float angle) {
		mSweepAngle = angle;
		invalidate();
	}
	
	public void setSweepAngle(float start,float angle){
	    mStartAngle = start;
	    mSweepAngle = angle;
	    invalidate();
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		Paint paint = new Paint();
		paint.setColor(mColor);
		RectF oval = new RectF(0, 0, mRadius, mRadius);
		canvas.drawArc(oval, mStartAngle, mSweepAngle, true, paint);
	}
}
