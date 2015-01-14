/*
 * Copyright (C) 2007-2013 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package com.media.zlibrary.ui.android.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.iflytek.tts.TtsService.Tts;
import com.media.android.dbstarplayer.AudioPlayAction;
import com.media.android.dbstarplayer.DbStarPlayer;
import com.media.dbstarplayer.dbstarplayer.ActionCode;
import com.media.dbstarplayer.dbstarplayer.DbStarPlayerApp;
import com.media.dbstarplayer.dbstarplayer.DbStarView;
import com.media.player.common.Utils;
import com.media.zlibrary.core.application.ZLApplication;
import com.media.zlibrary.core.view.ZLView;
import com.media.zlibrary.core.view.ZLView.Animation;
import com.media.zlibrary.core.view.ZLView.PageIndex;
import com.media.zlibrary.core.view.ZLViewWidget;
import com.media.zlibrary.text.view.ZLTextView;
import com.media.zlibrary.ui.android.view.BitmapManager.EdgePosition;

public class ZLAndroidWidget extends View implements ZLViewWidget, View.OnLongClickListener, View.OnKeyListener{
	private final String TAG = getClass().getSimpleName();
	private final Paint myPaint = new Paint();
	private ZLAndroidPaintContext mPaintContext;
	private final BitmapManager myBitmapManager = new BitmapManager(this);
//	private Bitmap myFooterBitmap;
	
	public ZLAndroidWidget(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public ZLAndroidWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ZLAndroidWidget(Context context) {
		super(context);
		init();
	}

	private void init() {
		// next line prevent ignoring first onKeyDown DPad event
		// after any dialog was closed
		setFocusableInTouchMode(true);
		setDrawingCacheEnabled(false);
		setOnLongClickListener(this);
		setOnKeyListener(this);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		getAnimationProvider().terminate();
		if (myScreenIsTouched) {
			final ZLView view = ZLApplication.Instance().getCurrentView();
			myScreenIsTouched = false;
			view.onScrollingFinished(ZLView.PageIndex.current);
		}
	}

	@Override
	protected void onDraw(final Canvas canvas) {
		final Context context = getContext();
		if (context instanceof DbStarPlayer) {
			((DbStarPlayer)context).createWakeLock();
		} else {
			System.err.println("A surprise: view's context is not an DbStarPlayer");
		}
		super.onDraw(canvas);

//		final int w = getWidth();
//		final int h = getMainAreaHeight();

		if (getAnimationProvider().inProgress()) {
//			Log.d(TAG, "-------------------getAnimationProvider().inProgress() = " + getAnimationProvider().inProgress());
			onDrawInScrolling(canvas);
		} else {
//			Log.d(TAG, "-------------------getAnimationProvider().inProgress()-----");
			onDrawStatic(canvas);
			ZLApplication.Instance().onRepaintFinished();
		}
	}

	private AnimationProvider myAnimationProvider;
	private ZLView.Animation myAnimationType;
	
	public Animation getCurAnimationType(){
		if(null==myAnimationType){
			getAnimationProvider();
		}
		return myAnimationType;
	}
	
	public AnimationProvider getAnimationProvider() {
		if(getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE&&
				((DbStarPlayerApp) DbStarPlayerApp.Instance()).PageTurningOptions.ShowDoublePageOption.getValue()){
			if(myAnimationType != Animation.realdouble){
				myAnimationType = Animation.realdouble;
				myAnimationProvider = new DoublePageAnimationProvider(this, myBitmapManager);
			}
			if(myAnimationProvider==null){
				myAnimationProvider = new DoublePageAnimationProvider(this, myBitmapManager);
			}
			return myAnimationProvider;
		}
		final ZLView.Animation type = ZLApplication.Instance().getCurrentView().getAnimationType();
		if (myAnimationProvider == null || myAnimationType != type) {
			myAnimationType = type;
			switch (type) {
				case none:
					myAnimationProvider = new NoneAnimationProvider(myBitmapManager);
					break;
				case curl:
					myAnimationProvider = new CurlAnimationProvider(myBitmapManager);
					break;
				case slide:
					myAnimationProvider = new SlideAnimationProvider(myBitmapManager);
					break;
				case shift:
					myAnimationProvider = new ShiftAnimationProvider(myBitmapManager);
					break;
			}
		}
		return myAnimationProvider;
	}

	private void onDrawInScrolling(Canvas canvas) {
		final ZLView view = ZLApplication.Instance().getCurrentView();

//		final int w = getWidth();
//		final int h = getMainAreaHeight();

		final AnimationProvider animator = getAnimationProvider();
		final AnimationProvider.Mode oldMode = animator.getMode();
		animator.doStep();
		if (animator.inProgress()) {
//			view.finishDrawingBookmark();
			animator.draw(canvas);
			if (animator.getMode().Auto) {
				postInvalidate();
			}
//			drawFooter(canvas);
		} else {
			switch (oldMode) {
				case AnimatedScrollingForward:
				{
					final ZLView.PageIndex index = animator.getPageToScrollTo();
					myBitmapManager.shift(index == ZLView.PageIndex.next);
					view.onScrollingFinished(index);
					ZLApplication.Instance().onRepaintFinished();
					break;
				}
				case AnimatedScrollingBackward:
					view.onScrollingFinished(ZLView.PageIndex.current);
					break;
			}
			onDrawStatic(canvas);
		}
	}

	private void checkAudioSpeak(){
		if(AudioPlayAction.isAudioSpeek){
			Utils.printLogError(getClass().getSimpleName(), "checkAudioSpeak Stop Audio right Now");
			final ZLView view = ZLApplication.Instance().getCurrentView();
			AudioPlayAction.isAudioSpeek = false;
			if(Tts.isInitialized()){
				Tts.JniStop();
			}
//			Tts.JniDestory();
			if(view instanceof ZLTextView){
				((ZLTextView)view).clearHighlighting();
			}
			ZLApplication.Instance().runAction(ActionCode.AUDIO_CANCEL);
		}
	}
	/**
	 * shift the current page to previous or next
	 * */
	private void pageShiftCheck(){
		final ZLView view = ZLApplication.Instance().getCurrentView();
		final AnimationProvider animator = getAnimationProvider();
		if(animator.inProgress()){
			switch (animator.getMode()) {
			case AnimatedScrollingForward:
				final ZLView.PageIndex index = animator.getPageToScrollTo();
				Utils.printLogError(getClass().getSimpleName(), "pageShiftCheck called AnimatedScrollingForward index="+index);
				myBitmapManager.shift(index == ZLView.PageIndex.next);
				view.onScrollingFinished(index);
				ZLApplication.Instance().onRepaintFinished();
				break;
			case AnimatedScrollingBackward:
				Utils.printLogError(getClass().getSimpleName(), "pageShiftCheck called AnimatedScrollingBackward");
				view.onScrollingFinished(ZLView.PageIndex.current);
				break;
			}
			animator.terminate();
		}
	}
	
	/**
	 * reset the bitmap caches
	 * */
	public void reset() {
		myBitmapManager.reset();
	}

	public void repaint() {
		postInvalidate();
	}

	/**
	 * initial scrolling when turn page by move finger
	 * */
	public void startManualScrolling(int x, int y, ZLView.Direction direction) {
		final AnimationProvider animator = getAnimationProvider();
		int width = -1;
		if(myAnimationType==ZLView.Animation.realdouble){
			width=getWidth()/2;
		}else{
			width = getWidth();
		}
		animator.setup(direction, width, getMainAreaHeight());
		animator.startManualScrolling(x, y);
	}

	/**
	 * scroll the page by the point of finger
	 * */
	public void scrollManuallyTo(int x, int y) {
		final ZLView view = ZLApplication.Instance().getCurrentView();
		final AnimationProvider animator = getAnimationProvider();
		if (view.canScroll(animator.getPageToScrollTo(x, y))) {
			animator.scrollTo(x, y);
			postInvalidate();
		}
	}

	/**
	 * called when single tap to turn page
	 * */
	public void startAnimatedScrolling(ZLView.PageIndex pageIndex, int x, int y, ZLView.Direction direction, int speed) {
		final ZLView view = ZLApplication.Instance().getCurrentView();
		if (pageIndex == ZLView.PageIndex.current || !view.canScroll(pageIndex)) {
			return;
		}
		final AnimationProvider animator = getAnimationProvider();
		int width = -1;
		if(myAnimationType==ZLView.Animation.realdouble){
			width=getWidth()/2;
		}else{
			width = getWidth();
		}
		animator.setup(direction, width, getMainAreaHeight());
		Integer assumeX = -1;
		final Integer assumeY = (int) (getHeight()*2.0f/3);
		switch(pageIndex){
			case next:
				assumeX = (int) (getWidth()*9.0f/10);
				break;
			case previous:
				assumeX = (int) (getWidth()*1.0f/10);
				break;
			default:
				assumeX = (int) (getWidth()*1.0f/10);
				break;
		}
		animator.startAnimatedScrolling(pageIndex, assumeX, assumeY, speed);

		if(animator instanceof DoublePageAnimationProvider){
			((DoublePageAnimationProvider)animator).startAutoScrolling(assumeX, assumeY, false);
		}
		if (animator.getMode().Auto) {
			postInvalidate();
		}
	}

	/**
	 * for common calling request
	 * */
	public void startAnimatedScrolling(ZLView.PageIndex pageIndex, ZLView.Direction direction, int speed) {
		final ZLView view = ZLApplication.Instance().getCurrentView();
		if (pageIndex == ZLView.PageIndex.current || !view.canScroll(pageIndex)) {
			return;
		}
		final AnimationProvider animator = getAnimationProvider();
		int width = -1;
		if(myAnimationType==ZLView.Animation.realdouble){
			width=getWidth()/2;
		}else{
			width = getWidth();
		}
		animator.setup(direction, width, getMainAreaHeight());
		Integer assumeX = -1;
		final Integer assumeY = (int) (getHeight()*2.0f/3);
		switch(pageIndex){
			case next:
				assumeX = getWidth()*9/10;
				break;
			case previous:
				assumeX = getWidth()/10;
				break;
			default:
				assumeX = getWidth()/10;
				break;
		}
		
		animator.startAnimatedScrolling(pageIndex, assumeX, assumeY, speed);
		if(animator instanceof DoublePageAnimationProvider){
			((DoublePageAnimationProvider)animator).startAutoScrolling(assumeX, assumeY, false);

		}
		if (animator.getMode().Auto) {
			postInvalidate();
		}
	}

	/**
	 * called when turn page after finger move
	 * */
	public void startAnimatedScrolling(int x, int y, int speed) {
		final ZLView view = ZLApplication.Instance().getCurrentView();
		final AnimationProvider animator = getAnimationProvider();
		if (!view.canScroll(animator.getPageToScrollTo(x, y))) {
			animator.terminate();
			return;
		}
		animator.startAnimatedScrolling(x, y, speed);
		postInvalidate();
	}

	/**
	 * progress:read progress page index
	 */
	void drawOnBitmap(int w, int h, Bitmap bitmap, ZLView.PageIndex index, EdgePosition edgepos, ZLView.PageIndex progress) {
		final ZLView view = ZLApplication.Instance().getCurrentView();
		if (view == null) {
			return;
		}
		if(null==mPaintContext){
			mPaintContext = new ZLAndroidPaintContext();
		}
		mPaintContext.setCanvas(new Canvas(bitmap));
		mPaintContext.setSize(w,h,view.isScrollbarShown() ? getVerticalScrollbarWidth() : 0);
		view.paint(mPaintContext, index,edgepos,progress);
	}

//	private void drawFooter(Canvas canvas) {
//		final ZLView view = ZLApplication.Instance().getCurrentView();
//		final ZLView.FooterArea footer = view.getFooterArea();
//
//		if (footer == null) {
//			myFooterBitmap = null;
//			return;
//		}
//
//		if (myFooterBitmap != null &&
//			(myFooterBitmap.getWidth() != getWidth() ||
//			 myFooterBitmap.getHeight() != footer.getHeight())) {
//			myFooterBitmap = null;
//		}
//		if (myFooterBitmap == null) {
//			myFooterBitmap = Bitmap.createBitmap(getWidth(), footer.getHeight(), Bitmap.Config.RGB_565);
//		}
//		final ZLAndroidPaintContext context = new ZLAndroidPaintContext(
//			new Canvas(myFooterBitmap),
//			getWidth(),
//			footer.getHeight(),
//			view.isScrollbarShown() ? getVerticalScrollbarWidth() : 0
//		);
//		footer.paint(context);
//		canvas.drawBitmap(myFooterBitmap, 0, getHeight() - footer.getHeight(), myPaint);
//	}

	private void onDrawStatic(final Canvas canvas) {
		Utils.printLogInfo(TAG, "OnDrawStatic called getWidth():"+getWidth());
		if(myAnimationType==ZLView.Animation.realdouble){
			myBitmapManager.setSize(getWidth()/2, getMainAreaHeight());
			final AnimationProvider animator = getAnimationProvider();
			
			if(animator.getPageToScrollTo()==PageIndex.previous){
//				Log.d(TAG, "------if--------animator.getPageToScrollTo()==PageIndex.previous -------");
				canvas.drawBitmap(myBitmapManager.getBitmap(ZLView.PageIndex.curright,EdgePosition.EDGE_RIGHT,ZLView.PageIndex.curright), getWidth()/2, 0, myPaint);
				canvas.drawBitmap(myBitmapManager.getBitmap(ZLView.PageIndex.current,EdgePosition.EDGE_LEFT,ZLView.PageIndex.current), 0, 0, myPaint);
			}else{
//				Log.d(TAG, "--------else------animator.getPageToScrollTo()==PageIndex.previous -------");
				canvas.drawBitmap(myBitmapManager.getBitmap(ZLView.PageIndex.current,EdgePosition.EDGE_LEFT,ZLView.PageIndex.current), 0, 0, myPaint);
				canvas.drawBitmap(myBitmapManager.getBitmap(ZLView.PageIndex.curright,EdgePosition.EDGE_RIGHT,ZLView.PageIndex.curright), getWidth()/2, 0, myPaint);
			}
		}else{
		    myBitmapManager.setSize(getWidth(), getMainAreaHeight());
		    canvas.drawBitmap(myBitmapManager.getBitmap(ZLView.PageIndex.current,EdgePosition.EDGE_RIGHT,ZLView.PageIndex.current), 0, 0, myPaint);
		}
//		drawFooter(canvas);
		new Thread() {
			@Override
			public void run() {
				final ZLView view = ZLApplication.Instance().getCurrentView();
				int width = getWidth();
				if(myAnimationType==ZLView.Animation.realdouble){
					width /=2;
				}
				if(null==mPaintContext){
					mPaintContext = new ZLAndroidPaintContext();
				}
				mPaintContext.setCanvas(canvas);
				mPaintContext.setSize(width,getMainAreaHeight(),view.isScrollbarShown() ? getVerticalScrollbarWidth() : 0);
				view.preparePage(mPaintContext, ZLView.PageIndex.next);
			}
		}.start();
	}

	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			onKeyDown(KeyEvent.KEYCODE_DPAD_CENTER, null);
		} else {
			ZLApplication.Instance().getCurrentView().onTrackballRotated((int)(10 * event.getX()), (int)(10 * event.getY()));
		}
		return true;
	}


	private class LongClickRunnable implements Runnable {
		public void run() {
			if (performLongClick()) {
				myLongClickPerformed = true;
			}
		}
	}
	private volatile LongClickRunnable myPendingLongClickRunnable;
	private volatile boolean myLongClickPerformed;

	private void postLongClickRunnable() {
		myLongClickPerformed = false;
		myPendingPress = false;
		if (myPendingLongClickRunnable == null) {
			myPendingLongClickRunnable = new LongClickRunnable();
		}
		postDelayed(myPendingLongClickRunnable, 2 * ViewConfiguration.getLongPressTimeout());
	}

//	private class ShortClickRunnable implements Runnable {
//		public void run() {
//			final ZLView view = ZLApplication.Instance().getCurrentView();
//			view.onFingerSingleTap(myPressedX, myPressedY);
//			myPendingPress = false;
//			myPendingShortClickRunnable = null;
//		}
//	}
//	
//	private volatile ShortClickRunnable myPendingShortClickRunnable;

	private volatile boolean myPendingPress;
//	private volatile boolean myPendingDoubleTap;
	private int myPressedX, myPressedY;
	private boolean myScreenIsTouched;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int)event.getX();
		int y = (int)event.getY();

		final ZLView view = ZLApplication.Instance().getCurrentView();
		switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
//				if (myPendingDoubleTap) {
//					view.onFingerDoubleTap(x, y);
//				} 
				if (myLongClickPerformed) {
					view.onFingerReleaseAfterLongPress(x, y);
				} else {
					if (myPendingLongClickRunnable != null) {
						removeCallbacks(myPendingLongClickRunnable);
						myPendingLongClickRunnable = null;
					}
					if (myPendingPress) {
//						if (view.isDoubleTapSupported()) {
//							if (myPendingShortClickRunnable == null) {
//								myPendingShortClickRunnable = new ShortClickRunnable();
//							}
//							postDelayed(myPendingShortClickRunnable, ViewConfiguration.getDoubleTapTimeout());
//						} else {
							view.onFingerSingleTap(x, y);
//						}
					} else {
						view.onFingerRelease(x, y);
					}
				}
//				myPendingDoubleTap = false;
				myPendingPress = false;
				myScreenIsTouched = false;
				break;
			case MotionEvent.ACTION_DOWN:
//				if (myPendingShortClickRunnable != null) {
//					removeCallbacks(myPendingShortClickRunnable);
//					myPendingShortClickRunnable = null;
//					myPendingDoubleTap = true;
//				} else {
				    pageShiftCheck();
					postLongClickRunnable();
					myPendingPress = true;
//				}
				myScreenIsTouched = true;
				myPressedX = x;
				myPressedY = y;
				break;
			case MotionEvent.ACTION_MOVE:
			{
				final int slop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
				final boolean isAMove =
					Math.abs(myPressedX - x) > slop || Math.abs(myPressedY - y) > slop;
//				if (isAMove) {
//					myPendingDoubleTap = false;
//				}
				if (myLongClickPerformed) {
					view.onFingerMoveAfterLongPress(x, y);
				} else {
					if (myPendingPress) {
						if (isAMove) {
//							if (myPendingShortClickRunnable != null) {
//								removeCallbacks(myPendingShortClickRunnable);
//								myPendingShortClickRunnable = null;
//							}
							if (myPendingLongClickRunnable != null) {
								removeCallbacks(myPendingLongClickRunnable);
							}
							view.onFingerPress(myPressedX, myPressedY);
							myPendingPress = false;
						}
					}
					if (!myPendingPress) {
						view.onFingerMove(x, y);
					}
				}
				break;
			}
		}

		return true;
	}

	public boolean onLongClick(View v) {
		final ZLView view = ZLApplication.Instance().getCurrentView();
		return view.onFingerLongPress(myPressedX, myPressedY);
	}

	private int myKeyUnderTracking = -1;
	private long myTrackingStartTime;

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		final ZLApplication application = ZLApplication.Instance();

		if (application.hasActionForKey(keyCode, true) ||
			application.hasActionForKey(keyCode, false)) {
			if (myKeyUnderTracking != -1) {
				if (myKeyUnderTracking == keyCode) {
					return true;
				} else {
					myKeyUnderTracking = -1;
				}
			}
			if (application.hasActionForKey(keyCode, true)) {
				myKeyUnderTracking = keyCode;
				myTrackingStartTime = System.currentTimeMillis();
				return true;
			} else {
				return application.runActionByKey(keyCode, false);
			}
		}else if(keyCode == KeyEvent.KEYCODE_MENU||
				keyCode == KeyEvent.KEYCODE_SEARCH) {//MENU键
	        //监控/拦截菜单键
	         return true;
	    }   else {
			return false;
		}
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (myKeyUnderTracking != -1) {
			if (myKeyUnderTracking == keyCode) {
				final boolean longPress = System.currentTimeMillis() >
					myTrackingStartTime + ViewConfiguration.getLongPressTimeout();
				ZLApplication.Instance().runActionByKey(keyCode, longPress);
			}
			myKeyUnderTracking = -1;
			return true;
		} else {
			final ZLApplication application = ZLApplication.Instance();
			return
				application.hasActionForKey(keyCode, false) ||
				application.hasActionForKey(keyCode, true);
		}
	}

	protected int computeVerticalScrollExtent() {
		final ZLView view = ZLApplication.Instance().getCurrentView();
		if (!view.isScrollbarShown()) {
			return 0;
		}
		final AnimationProvider animator = getAnimationProvider();
		if (animator.inProgress()) {
			final int from = view.getScrollbarThumbLength(ZLView.PageIndex.current);
			final int to = view.getScrollbarThumbLength(animator.getPageToScrollTo());
			final int percent = animator.getScrolledPercent();
			return (from * (100 - percent) + to * percent) / 100;
		} else {
			return view.getScrollbarThumbLength(ZLView.PageIndex.current);
		}
	}

	protected int computeVerticalScrollOffset() {
		final ZLView view = ZLApplication.Instance().getCurrentView();
		if (!view.isScrollbarShown()) {
			return 0;
		}
		final AnimationProvider animator = getAnimationProvider();
		if (animator.inProgress()) {
			final int from = view.getScrollbarThumbPosition(ZLView.PageIndex.current);
			final int to = view.getScrollbarThumbPosition(animator.getPageToScrollTo());
			final int percent = animator.getScrolledPercent();
			return (from * (100 - percent) + to * percent) / 100;
		} else {
			return view.getScrollbarThumbPosition(ZLView.PageIndex.current);
		}
	}

	protected int computeVerticalScrollRange() {
		final ZLView view = ZLApplication.Instance().getCurrentView();
		if (!view.isScrollbarShown()) {
			return 0;
		}
		return view.getScrollbarFullSize();
	}

	private int getMainAreaHeight() {
//		final ZLView.FooterArea footer = ZLApplication.Instance().getCurrentView().getFooterArea();
//		return footer != null ? getHeight() - footer.getHeight() : getHeight();
		return getHeight();
	}
	/**
	 * compute the scroll
	 * */
	@Override
	public void computeScroll() {
		super.computeScroll();
		/**
		 * compute the bezier animation 
		 * */
		final AnimationProvider animator = getAnimationProvider();
		if(animator instanceof DoublePageAnimationProvider){
			((DoublePageAnimationProvider)animator).computeScroll();
		}
		/******************************/
	}
	
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		int action = event.getAction();
		if(action==KeyEvent.ACTION_DOWN){
			switch(event.getKeyCode()){
			    case KeyEvent.KEYCODE_VOLUME_DOWN:
			    	if(!Utils.IS_TEST){
			    		break;
			    	}
				case KeyEvent.KEYCODE_DPAD_LEFT:
					checkAudioSpeak();
					pageShiftCheck();
					startAnimatedScrolling(DbStarView.PageIndex.previous,
							((DbStarPlayerApp) DbStarPlayerApp.Instance()).PageTurningOptions.Horizontal.getValue()
								? DbStarView.Direction.rightToLeft : DbStarView.Direction.up,
										((DbStarPlayerApp) DbStarPlayerApp.Instance()).PageTurningOptions.AnimationSpeed.getValue());
					if(Utils.IS_TEST){
						return true;
					}else{
						break;
					}
				case KeyEvent.KEYCODE_VOLUME_UP:
					if(!Utils.IS_TEST){
			    		break;
			    	}
				case KeyEvent.KEYCODE_DPAD_RIGHT:
					checkAudioSpeak();
					pageShiftCheck();
					startAnimatedScrolling(DbStarView.PageIndex.next,
							((DbStarPlayerApp) DbStarPlayerApp.Instance()).PageTurningOptions.Horizontal.getValue()
								? DbStarView.Direction.rightToLeft : DbStarView.Direction.up,
										((DbStarPlayerApp) DbStarPlayerApp.Instance()).PageTurningOptions.AnimationSpeed.getValue());
					if(Utils.IS_TEST){
						return true;
					}else{
						break;
					}
			}
		}
		return false;
	}

	@Override
	public boolean isTurningPage() {
		// TODO Auto-generated method stub
		return getAnimationProvider().inProgress();
	}
}
