package com.dbstar.widget;

import com.dbstar.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

public class GDScrollBar extends View {

	private static final String TAG = "GDScrollBar";
	
	Drawable mBackground;
	Drawable mTracker;
	
	private int mRange;
	private int mCurrentPosition;
	private int mOldPosition;
	private int mTrackerTop;
	
	private int mTackerHeight;
	boolean mRecomputeTrackerHeight;
	boolean mRecomputeTrackerTop;
	
	public GDScrollBar(Context context) {
		this(context, null);
	}
	
	public GDScrollBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		initScrollBar();
		
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.GDScrollBar);
		
		mBackground = a.getDrawable(R.styleable.GDScrollBar_background);
		mTracker  = a.getDrawable(R.styleable.GDScrollBar_tracker);
		
		a.recycle();
	}

	private void initScrollBar() {
		mRange = 0;
		mCurrentPosition = 0;
		setFocusable(false);
	}
	
	public void setBackground(Drawable background) {
		mBackground = background;
	}
	
	public void setTracker(Drawable tracker) {
		mTracker = tracker;
	}
	
	
	public void setRange(int range) {
		mRange = range;
		mRecomputeTrackerHeight = true;
	}
	
	public void setPosition(int position) {
		mOldPosition = mCurrentPosition;
		mCurrentPosition = position;
		mRecomputeTrackerTop = true;
		invalidate();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	@Override
	protected void onLayout (boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		final Drawable background = mBackground;
		final Drawable tracker = mTracker;
		
		int left = getPaddingLeft();
		int top = getPaddingTop();
		int right = getWidth() - getPaddingRight();
		int bottom= getHeight() - getPaddingBottom();
		
//		Log.d(TAG, "background bounds=" + left + " " + right +" " +  top +" " +  bottom);
//		Log.d(TAG, "onDraw");
		background.setBounds(left, top, right, bottom);
		
		
		mBackground.draw(canvas);
		

		int trackerTop = 0;
		final int padding = 2;
		final int height = bottom - top - padding - padding;
		
		if (mRecomputeTrackerHeight) {
			mRecomputeTrackerHeight = false;
			
			if (mRange > 1) {
				mTackerHeight = (int)((float)height / mRange);
			} else {
				mTackerHeight = height;
			}
			
			mTrackerTop = top;
			
//			Log.d(TAG, "mTackerHeight =" + mTackerHeight);
		}
		
		if (mRecomputeTrackerTop) {
			mRecomputeTrackerTop = false;
			int delta = mCurrentPosition - mOldPosition;
			if (delta > 0) {
				trackerTop = mTrackerTop + mTackerHeight;
			}
			else if (delta < 0) {
				trackerTop = mTrackerTop - mTackerHeight;
			} else {
				trackerTop = mTrackerTop;
			}

			mTrackerTop = trackerTop;
//			Log.d(TAG, "trackerTop =" + trackerTop);
		} else {
			trackerTop = mTrackerTop;
		}
		
//		int trackerHeight = tracker.getIntrinsicHeight();
		final int trackerHeight = mTackerHeight;
		final int trackerWidth = tracker.getIntrinsicWidth();
	
//		Log.d(TAG, "tacker bounds=" + trackerWidth + " " + trackerHeight);
		
		final int deltaX = (right - left - trackerWidth)/2;
		final int deltaY = trackerTop + padding;
		left += deltaX;
		right -= deltaX;
		top += deltaY;
		bottom = top + trackerHeight;
		
		tracker.setBounds(left, top, right, bottom);
		tracker.draw(canvas);		
	}
}
