package com.media.zlibrary.ui.android.view;

import com.media.player.common.Utils;
import com.media.dbstarplayer.dbstarplayer.DbStarPlayerApp;
import com.media.zlibrary.core.application.ZLApplication;
import com.media.zlibrary.core.view.ZLView;
import com.media.zlibrary.core.view.ZLView.PageIndex;
import com.media.zlibrary.ui.android.view.BitmapManager.EdgePosition;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.util.Log;
import android.view.View;
import android.widget.Scroller;

public class DoublePageAnimationProvider extends AnimationProvider {

	/**
	 * 日志TAG
	 */
	private final String TAG = "DoublePageAnimationProvider";
	
	private boolean FLAG_LOG_ERROR = false;
	
	private boolean FLAG_LOG_INFO = false;
	
	private int mReadMode = MODE_TWO_PAGE;
	
	/**
	 * one page mode
	 */
	public static final int MODE_ONE_PAGE = 1;
	
	/**
	 * two page mode
	 */
	public static final int MODE_TWO_PAGE = 2;
	
	/**
	 * 起始位置
	 */
	private Rect mPositionRectDst;
	
	/**
	 * 图片subset矩形
	 */
	private Rect mBitmapRectDst;
	
	/**
	 * 页边边框，比如构造书的厚度等信息
	 * */
	private int mPageBorder = -1;
	
	/**
	 * book edge bottom
	 * */
	private int mPageBottomBorder = -1;

	/**
	 * 单页向左翻页
	 */
	public static final int ONEFLIPLEFT = -2;
	
	/**
	 * 单页向右翻页
	 */
	public static final int ONEFLIPRIGHT = -1;
	
	/**
	 * 双页向左翻页
	 */
	public static final int TWOFLIPLEFT = 0;
	
	/**
	 * 双页向右翻页
	 */
	public static final int TWOFLIPRIGHT = 1;
	
	
	/**
	 * 翻页模式：单页向左翻页、单页向右翻页、双页向左翻页、双页向右翻页、单页向上翻页、单页向下翻页
	 */
	private int mFlipMode = TWOFLIPLEFT;

	/**
	 * 普通状态
	 */
	public static final int PAGENORMAL = -1;
	
	/**
	 * 拖动状态
	 */
	public static final int PAGEDRAGGING = 0;
	
	/**
	 * 翻页动画状态
	 */
	public static final int PAGEFLIPPEDANIMATION = 1;
	
	/**
	 * 特效状态：普通状态、拖动状态、动画状态
	 */
	public int mEffectState = PAGENORMAL;

	/**
	 *   屏幕宽度
	 */
	private int mPageWidth = 0;
	/**
	 *   屏幕高度
	 */
	private int mPageHeight = 0;
	/**
	 *   拖拽点页脚
	 */
	private float mCornerX = 0; 
	private float mCornerY = 0;
	/**
	 *    绘制路径
	 */
	private Path mPath0 = new Path();
	private Path mPath1 = new Path();
	/**
	 * 位于前页图片前面一页的图片(不参与翻页动作，单是要显示)
	 */
	private Bitmap mBeforePreviousPageBitmap = null;
	
	/**
	 * 前页图片
	 */
	private Bitmap mPreviousPageBitmap = null;
	
	/**
	 * 前页背面图片
	 */
	private Bitmap mPreviousBackPageBitmap = null;
	private ShapeDrawable mPreviousBackPageShapeDrawable = null;
	private Shader mPreviousBackPageShader = null;
	/**
	 * 后页图片
	 */
	private Bitmap mNextPageBitmap = null;
	private ShapeDrawable mNextPageShapeDrawable = null;
	private Shader mNextPagePageShader = null;
	/**
	 *    拖拽点
	 */
	private PointF mTouch = new PointF(); 
	/**
	 *    上一次拖拽点
	 */
//	private PointF mTouchPrevious = new PointF(); // 上一次拖拽点
	
	/**
	 *    Bezier曲线起始点
	 */
	private PointF mBezierStart1 = new PointF();
	private PointF mBezierStart2 = new PointF();
	/**
	 *    Bezier曲线控制点
	 */
	private PointF mBezierControl1 = new PointF();
	private PointF mBezierControl2 = new PointF();
	/**
	 *    Bezier曲线顶点
	 */
	private PointF mBeziervertex1 = new PointF();
	private PointF mBeziervertex2 = new PointF(); 
	/**
	 *    Bezier曲线结束点
	 */
	private PointF mBezierEnd1 = new PointF();
	private PointF mBezierEnd2 = new PointF();
	/**
	 *    拖拽点与页脚连线中点
	 */
	private float mMiddleX;
	private float mMiddleY;
	/**
	 *    阴影顶点与拖拽点连线角度
	 */
	private float mDegrees = 0;
	/**
	 *    拖拽点与页脚连线距离
	 */
	private float mTouchToCornerDis = 0;
	/**
	 *    定义矩阵
	 */
	private Matrix mMatrix = new Matrix();
	private float[] mMatrixArray = { 0, 0, 0, 0, 0, 0, 0, 0, 1.0f };
	/**
	 *    拖拽起始位置是否属于右上左下
	 */
	private boolean mIsRTandLB = false; 
	/**
	 *    页面对角线长度
	 */
	private float mMaxLength = 0;
	/**
	 *    后页阴影颜色
	 */
	private int[] mBackShadowColors = null;
	/**
	 *    前页阴影颜色
	 */
	private int[] mFrontShadowColors = null;
	/**
	 *    从左到右翻页后页阴影
	 */
	private GradientDrawable mBackShadowDrawableLR = null;
	/**
	 *    从右到左翻页后页阴影
	 */
	private GradientDrawable mBackShadowDrawableRL = null;
	/**
	 *    从左到右翻起页阴影
	 */
	private GradientDrawable mFolderShadowDrawableLR = null;
	/**
	 *     从右到左翻起页阴影
	 */
	private GradientDrawable mFolderShadowDrawableRL = null;
	/**
	 *     从下到上当前页阴影
	 */
	private GradientDrawable mFrontShadowDrawableHBT = null;
	/**
	 *     从上到下当前页阴影
	 */
	private GradientDrawable mFrontShadowDrawableHTB = null;
	/**
	 *     从左到右当前页阴影
	 */
	private GradientDrawable mFrontShadowDrawableVLR = null;
	/**
	 *     从右到左当前页阴影
	 */
	private GradientDrawable mFrontShadowDrawableVRL = null;
	/**
	 *    定义画笔
	 */
	private Paint mPaint = new Paint();;
	/**
	 *    定义拖拽动作
	 */
	private Scroller mScroller = null;

	/**
	 * 翻书特效作用于的View
	 */
	private View mTargetView = null;
	/**
	 *     翻页持续时间
	 */
	private int mDuration = 15000;
	/**
	 *     是否手动翻页
	 */
	private boolean isDraging = false;		
	/**
	 *     定义最小数值
	 */
	private int minValue = -1;
	/**
	 *     是否自动翻页
	 * */
	private boolean isAutoFlip = false;
	private PaintFlagsDrawFilter paintFilter;
	private boolean mFlatFlip = false;
	/**
	 * constructor
	 * @param scroller a Scroller
	 * @param targetView the view of drawing
	 */
	public DoublePageAnimationProvider(View targetView, BitmapManager bitmapManager) {
		super(bitmapManager);
		printLogInfo(TAG,"DoublePageAnimationProvider constructor");
		mScroller = new Scroller(targetView.getContext());
		mTargetView = targetView;
		initBezierEffect();
		// 起始位置
		mPositionRectDst = new Rect(0, 0, 0, 0);
		// 图片subset矩形
		mBitmapRectDst = new Rect(0, 0, 0, 0);
		setPosition(targetView.getWidth()/2, 0);
		setPageSize(targetView.getWidth()/2, targetView.getHeight());
	}

	private void initBezierEffect() {
		printLogInfo(TAG,"initBezierEffect called");
		mPath0 = new Path();
		mPath1 = new Path();
		minValue = Utils.dip2px(mTargetView.getContext(),1);
		createDrawable();

		mPaint = new Paint();
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setAntiAlias(true);
		mPaint.setFilterBitmap(true);
		mMatrix = new Matrix();
		paintFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|
				Paint.FILTER_BITMAP_FLAG|
				Paint.DITHER_FLAG);
		mTouch.x = 0.01f; // 不让x,y为0,否则在点计算时会有问题
		mTouch.y = 0.01f;
	}
	
	private void printLogError(String tag, String msg){
		if(FLAG_LOG_ERROR){
			Log.e(tag, msg);
		}
	}
	
	private void printLogInfo(String tag, String msg){
		if(FLAG_LOG_INFO){
			Log.i(tag, msg);
		}
	}
	
	/**
	 * 设置图片显示位置
	 * 
	 * @param positionX
	 *            左上角X坐标
	 * @param positionY
	 *            左上角Y坐标
	 */
	public void setPosition(int positionX, int positionY) {
		printLogInfo(TAG,"setPosition positionX="+positionX+", positionY="+positionY);
		mPositionRectDst.left = positionX;
		mPositionRectDst.top = positionY;
	}

	/**
	 * @return the current read mode 
	 */
	public int getReadMode() {
		return mReadMode;
	}

	/**
	 * set the current read mode
	 * @param mReadMode the mode
	 */
	public void setReadMode(int mReadMode) {
		this.mReadMode = mReadMode;
	}

	/**
	 * 设置翻页模式：单页向左翻页、单页向右翻页、单页向上翻页、单页向下翻页、双页向左翻页、双页向右翻页
	 * 
	 * @param mode mode of flip
	 * 
	 */
	public void setFlipMode(int mode) {
		mFlipMode = mode;
	}

	/**
	 * 获取翻页模式
	 * @return the current flip mode
	 */
	public int getFlipMode() {
		return mFlipMode;
	}
	
	/**
	 * 更新drawNormal方式下的单页图片
	 * @param previousPageBitmap 前页图片(当前页)
	 */
//	public void refreshNormalOnePageBitmap(Bitmap previousPageBitmap)
//	{
//		mPreviousPageBitmap = previousPageBitmap;
//	}

	/**
	 * 更新需要翻页的页面图片
	 * 
	 * @param previousPageBitmap
	 *            前页图片
	 * @param nextPageBitmap
	 *            后页图片
	 */
//	public void refreshOnePageBitmap(Bitmap previousPageBitmap,
//			Bitmap nextPageBitmap) {
//		refreshTwoPageBitmap(previousPageBitmap, previousPageBitmap,
//				nextPageBitmap);
//	}
//	
//	/**
//	 * 更新drawNormal方式下的双页图片
//	 * @param beforePreviousPageBitmap the page of left 
//	 * @param previousPageBitmap 前页图片(当前页)
//	 */
//	public void refreshNormalTwoPageBitmap(Bitmap beforePreviousPageBitmap,
//			Bitmap previousPageBitmap)
//	{
//		mBeforePreviousPageBitmap = beforePreviousPageBitmap;
//		mPreviousPageBitmap = previousPageBitmap;
//	}
//
//	/**
//	 * 更新需要翻页的页面图片
//	 * 
//	 * @param previousPageBitmap
//	 *            前页图片
//	 * @param previousBackPageBitmap
//	 *            前页背面图片
//	 * @param nextPageBitmap
//	 *            后页图片
//	 */
//	public void refreshTwoPageBitmap(Bitmap previousPageBitmap,
//			Bitmap previousBackPageBitmap, Bitmap nextPageBitmap) {
//		mPositionRectDst.right = mPositionRectDst.left + previousPageBitmap.getWidth();
//		mPositionRectDst.bottom = mPositionRectDst.top + previousPageBitmap.getHeight();
//		mPageWidth = previousPageBitmap.getWidth()-getPageBorder();
//		mPageHeight = previousPageBitmap.getHeight();
//		mMaxLength = (float) Math.hypot(mPageWidth, mPageHeight);
//		mBitmapRectDst.right = previousPageBitmap.getWidth();
//		mBitmapRectDst.bottom = mPageHeight;
//		mPreviousPageBitmap = previousPageBitmap;
//		mPreviousBackPageBitmap = previousBackPageBitmap;
//		mNextPageBitmap = nextPageBitmap;
//	}

	/**
	 * 更新需要翻页的页面图片
	 * 
	 * @param beforePreviousPageBitmap
	 *            不参与本次动画效果的图片：向右翻书时，是书右侧图片；向左翻书时，是书左侧图片
	 * @param previousPageBitmap
	 *            前页图片
	 * @param previousBackPageBitmap
	 *            前页背面图片
	 * @param nextPageBitmap
	 *            后页图片
	 */
	public void refreshFourPageBitmap(Bitmap beforePreviousPageBitmap,
			Bitmap previousPageBitmap, Bitmap previousBackPageBitmap,
			Bitmap nextPageBitmap) {
		printLogError(TAG, "refreshFourPageBitmap beforePreviousPageBitmap:"+
			beforePreviousPageBitmap+", previousPageBitmap:"+previousPageBitmap+", previousBackPageBitmap:"+
				previousBackPageBitmap+", nextPageBitmap:"+nextPageBitmap);
		mPositionRectDst.right = mPositionRectDst.left + previousPageBitmap.getWidth();
		mPositionRectDst.bottom = mPositionRectDst.top + previousPageBitmap.getHeight();
		mBitmapRectDst.right = previousPageBitmap.getWidth();
		mBitmapRectDst.bottom = previousPageBitmap.getHeight();
		if(mPageWidth<=0){
			setPageSize(previousPageBitmap.getWidth(), previousPageBitmap.getHeight());
		}
		mMaxLength = (float) Math.hypot(mPageWidth, mPageHeight);
		mBeforePreviousPageBitmap = beforePreviousPageBitmap;
		mPreviousPageBitmap = previousPageBitmap;
		destroyBitmap(mPreviousBackPageBitmap);
		mPreviousBackPageBitmap = reverseBitmapLeftToRight(previousBackPageBitmap,false);
		mNextPageBitmap = nextPageBitmap;
	    // 创建前页背面
//		mPreviousBackPageShader = new BitmapShader(mPreviousBackPageBitmap,
//				Shader.TileMode.REPEAT, Shader.TileMode.MIRROR);
//		mPreviousBackPageShapeDrawable.getPaint().setShader(
//				mPreviousBackPageShader);
//		// 设置显示区域
//		mPreviousBackPageShapeDrawable.setBounds(mPositionRectDst);
//
//		// 创建后页
//		mNextPagePageShader = new BitmapShader(mNextPageBitmap,
//				Shader.TileMode.REPEAT, Shader.TileMode.MIRROR);
//		// 设置要绘制的多边形为ShapeDrawable图片
//		mNextPageShapeDrawable.getPaint().setShader(mNextPagePageShader);
//		// 设置显示区域
//		mNextPageShapeDrawable.setBounds(mPositionRectDst);	
	}

	
	/**
	 * 翻页拖动
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public void prepareDragging(float x,float y) {
		printLogError(TAG,"prepareDragging x="+x+", y="+y);
		if (PAGENORMAL == mEffectState) {
			mEffectState = PAGEDRAGGING;
			calcCornerXY(x, y);
		}
	}

	/**
	 * 绘制页面图片
	 * 
	 * @param canvas the canvas
	 */
	public void drawInternal(Canvas canvas) {
//		printLogInfo(TAG, "drawInternal called mEffectState:"+mEffectState);
		canvas.setDrawFilter(paintFilter);
		switch (mEffectState) {
		case PAGENORMAL:
			drawPageNormal(canvas);
			break;
		case PAGEDRAGGING:
			drawPageDragging(canvas);
			break;
		case PAGEFLIPPEDANIMATION:
			drawFlippedPage(canvas);
			break;
		default:
			break;
		}
	}

	/**
	 * 绘制普通页面图片
	 * 
	 * @param canvas the canvas
	 */
	private void drawPageNormal(Canvas canvas) {
		printLogInfo(TAG,"drawPageNormal mFlipMode="+mFlipMode);
		switch (mFlipMode) {
		case ONEFLIPRIGHT:
		case ONEFLIPLEFT:
			// 绘制当前页图片
			canvas.drawBitmap(mPreviousPageBitmap, mBitmapRectDst,
					mPositionRectDst, null);
			
			break;
		case TWOFLIPLEFT:
		case TWOFLIPRIGHT:
			//绘制左页图片
//			canvas.drawBitmap(mBeforePreviousPageBitmap, mPositionRectDst.left
//					- mBeforePreviousPageBitmap.getWidth(), mPositionRectDst.top, null);
			//绘制右页图片
//			canvas.drawBitmap(mPreviousPageBitmap, mBitmapRectDst, mPositionRectDst, null);
			Utils.printLogError(TAG, "drawPageNormal mPositionRectDst:"+mPositionRectDst+", mPageWidth:"+mPageWidth+", pageBorder:"
					+getPageBorder()+" final xPos:"+(mPositionRectDst.left-mPageWidth-getPageBorder()));
			canvas.drawBitmap(getBitmapLeft(false), mPositionRectDst.left-mPageWidth-getPageBorder(), mPositionRectDst.top, null);
			Utils.printLogError(TAG, "drawPageNormal mBitmapRectDst:"+mBitmapRectDst);
			canvas.drawBitmap(getBitmapRight(false), mBitmapRectDst, mPositionRectDst, null);
			break;
		default:
			break;
		}

	}

	/**
	 * 绘制具有翻页拖拉效果的页面图片
	 * 
	 * @param canvas the canvas
	 */
	private void drawPageDragging(Canvas canvas) {
		printLogInfo(TAG,"drawPageDragging mFlipMode="+mFlipMode);
		drawFlippedPage(canvas);
	}

	/**
	 * 绘制具有翻页效果的页面图片
	 * @param canvas the canvas
	 */
	private void drawFlippedPage(Canvas canvas) {
//		printLogInfo(TAG,"drawFlippedPage mFlipMode="+mFlipMode);
		switch (mFlipMode) {
		case TWOFLIPRIGHT:
			// 绘制前页图片
			canvas.drawBitmap(mNextPageBitmap, mPositionRectDst.left
					- mNextPageBitmap.getWidth(), mPositionRectDst.top, null);
			// 设置裁减区域
//			canvas.clipRect(mPositionRectDst.left - mNextPageBitmap.getWidth(),
//					mPositionRectDst.top, mPositionRectDst.right,
//					mPositionRectDst.bottom);
			calcBezierPoints(mPositionRectDst.left-mPageWidth, mPositionRectDst.left, 0, 0);
			drawCurrentPageArea(canvas, mPreviousPageBitmap, mPath0);
			drawNextPageAreaAndShadow(canvas, mBeforePreviousPageBitmap);
//			drawCurrentPageShadow(canvas);
			canvas.drawBitmap(mBeforePreviousPageBitmap, mPositionRectDst.left
					, mPositionRectDst.top, null);
			drawCurrentBackArea(canvas, mPreviousBackPageBitmap);
			break;
		case TWOFLIPLEFT:
			// 绘制前页图片的前一页
			canvas.drawBitmap(mBeforePreviousPageBitmap, mPositionRectDst.left
					- mBeforePreviousPageBitmap.getWidth(), mPositionRectDst.top, null);
//			// 绘制前页图片
//			canvas.drawBitmap(mPreviousPageBitmap, mBitmapRectDst,
//					mPositionRectDst, null);
////			// 设置裁减区域
//			canvas.clipRect(mPositionRectDst.left - mPageWidth,
//					mPositionRectDst.top, mPositionRectDst.right,
//					mPositionRectDst.bottom);
			calcBezierPoints(mPositionRectDst.left, mPositionRectDst.left+mBeforePreviousPageBitmap.getWidth(), 0, 0);
			drawCurrentPageArea(canvas, mPreviousPageBitmap, mPath0);
			drawNextPageAreaAndShadow(canvas, mNextPageBitmap);
			drawCurrentPageShadow(canvas);
			drawCurrentBackArea(canvas, mPreviousBackPageBitmap);
			break;

		case ONEFLIPLEFT:
		case ONEFLIPRIGHT:
			calcBezierPoints(0, mPageWidth, 0, 0);
			drawCurrentPageArea(canvas, mPreviousPageBitmap, mPath0);
			drawNextPageAreaAndShadow(canvas, mNextPageBitmap);
			drawCurrentPageShadow(canvas);
			drawCurrentBackArea(canvas, mPreviousPageBitmap);
			break;
		default:
			break;
		}
	}

	/**
	 *       自动翻页
	  */
	public void startAutoScrolling(Integer x, Integer y, boolean is_after_manual) {
		final int screenWidth = mTargetView.getWidth();
		setTouchPoint(new PointF(x,y));
		if(!is_after_manual){
			if(x<screenWidth/2){
				setFlipMode(DoublePageAnimationProvider.TWOFLIPRIGHT);
				refreshFourPageBitmap(myBitmapManager.getBitmap(PageIndex.curright, EdgePosition.EDGE_RIGHT, PageIndex.curright), 
						myBitmapManager.getBitmap(PageIndex.current, EdgePosition.EDGE_LEFT, PageIndex.current), 
						myBitmapManager.getBitmap(PageIndex.previous, EdgePosition.EDGE_RIGHT, PageIndex.previous),
						myBitmapManager.getBitmap(PageIndex.next, EdgePosition.EDGE_LEFT, PageIndex.next));
			}else{
				setFlipMode(DoublePageAnimationProvider.TWOFLIPLEFT);
				refreshFourPageBitmap(myBitmapManager.getBitmap(PageIndex.current, EdgePosition.EDGE_LEFT, PageIndex.current), 
						myBitmapManager.getBitmap(PageIndex.curright, EdgePosition.EDGE_RIGHT, PageIndex.curright), 
						myBitmapManager.getBitmap(PageIndex.previous, EdgePosition.EDGE_LEFT, PageIndex.previous),
						myBitmapManager.getBitmap(PageIndex.next, EdgePosition.EDGE_RIGHT, PageIndex.next));
			}
			calcCornerXY(x, y);
//			double deltaX = x - danimatior.myStartX;
//			boolean needDoneFlipPage = Math.abs((int) deltaX) > getWidth() / 8;
//			if (needDoneFlipPage) 
//			{
//				danimatior.startAnimation();
//			}
//			else 
//			{
//				danimatior.startPageTurningAbortAnimation();
//			}
		}
		startAnimation();
		isAutoFlip = true;
		isDraging = false;
	}
	
	/**
	 *     手动翻页
	 */
	public void scrollManually(Integer x, Integer y) {
		if (mEffectState == DoublePageAnimationProvider.PAGENORMAL) {
			prepareDragging(x,y);
		}
		setTouchPoint(new PointF(x, y));
		isAutoFlip = true;
		isDraging = true;
	}
	
	/**
	 * 获取翻页状态：普通状态、拖动状态、动画状态
	 * @return the current page state
	 * 
	 */
	public int getEffectState() {
		return mEffectState;
	}
	
	/**
	 * 计算拖拽点对应的拖拽脚
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public void calcCornerXY(float x, float y) {
		printLogInfo(TAG,"calcCornerXY start x="+x+", y="+y);
		float leftX = 0;
		float rightX = 0;
		float topY = 0;
		float bottomY =0;
		if(mReadMode==MODE_ONE_PAGE){
			leftX = mPositionRectDst.left;
			rightX = mPageWidth+leftX;
			topY = mPositionRectDst.top;
			bottomY = mPageHeight+topY;
			mCornerX = rightX;
			if(y<topY+(bottomY-topY)*2/5.0f&&
					mFlipMode==ONEFLIPLEFT){
				mCornerY = topY;
				mFlatFlip = false;
				mIsRTandLB = true;
			}else if(y>topY+(bottomY-topY)*3/5.0f&&
					mFlipMode==ONEFLIPLEFT){
				mCornerY = bottomY;
				mFlatFlip = false;
				mIsRTandLB = false;
			}else{
				mCornerY = topY;
				mFlatFlip = true;
				mIsRTandLB = true;
			}
		}else if(mReadMode==MODE_TWO_PAGE){
			leftX = mPositionRectDst.left-mPageWidth;
			rightX = mPageWidth+mPositionRectDst.left;
			topY = mPositionRectDst.top;
			bottomY = mPageHeight+topY;
			if (x <mPositionRectDst.left&&x>mPositionRectDst.left-mPageWidth)
				mCornerX = leftX;
			else
				mCornerX = rightX;
			if(y<topY+(bottomY-topY)*2/5.0f){
				mCornerY = topY;
				mFlatFlip = false;
			}else if(y>topY+(bottomY-topY)*3/5.0f){
				mCornerY = bottomY;
				mFlatFlip = false;
			}else{
				mCornerY = topY;
				mFlatFlip = true;
				mIsRTandLB = true;
			}
			if ((mCornerX == leftX && mCornerY == bottomY)
					|| (mCornerX == rightX && mCornerY == topY))
				mIsRTandLB = true;
			else
				mIsRTandLB = false;
		}
		printLogInfo(TAG,"calcCornerXY end mCornerX="+mCornerX+", mCornerY="+mCornerY);
	}

	/**
	 * 求解直线P1P2和直线P3P4的交点坐标
	 * @param P1 the point one
	 * @param P2 the point two
	 * @param P3 the point three
	 * @param P4 the point four
	 * @return the cross point
	 */
	public PointF getCross(PointF P1, PointF P2, PointF P3, PointF P4) {
		PointF CrossP = new PointF();
		// 二元函数通式： y=ax+b
		float a1 = (float) ((P2.y - P1.y) / getDivider(P2.x, P1.x));
		float b1 = (float) (((P1.x * P2.y) - (P2.x * P1.y)) / getDivider(P1.x, P2.x));
		float a2 = (float) ((P4.y - P3.y) / getDivider(P4.x, P3.x));
		float b2 = (float) (((P3.x * P4.y) - (P4.x * P3.y)) /getDivider(P3.x, P4.x));
		CrossP.x = (float) ((b2 - b1) / getDivider(a1,a2));
		CrossP.y = a1 * CrossP.x + b1;
		return CrossP;
	}
	
	/**
	 * calculate bezier line points
	 * 
	 * @param leftbound
	 *            left boundary of the line
	 * @param rightbound
	 *            right boundary of the line
	 * @param offsetx
	 *            x axis offset of the points need to add after calculate
	 * @param offsety
	 *            y axis offset of the points need to add after calculate
	 */
	private void calcBezierPoints(float leftbound,float rightbound, 
			float offsetx, float offsety){
//		printLogInfo(TAG,"calcBezierPoints start leftbound="+leftbound+", rightbound="+rightbound);
		mMiddleX = (mTouch.x + mCornerX) / 2;
		mMiddleY = (mTouch.y + mCornerY) / 2; 
		mBezierControl1.x = (float) (mMiddleX - (mCornerY - mMiddleY)
				* (mCornerY - mMiddleY) / getDivider(mCornerX,mMiddleX));
		mBezierControl1.y = mCornerY;
		mBezierControl2.x = mCornerX;
		mBezierControl2.y = (float) (mMiddleY - (mCornerX - mMiddleX)
				* (mCornerX - mMiddleX) / getDivider(mCornerY, mMiddleY));
		mBezierStart1.x = mBezierControl1.x - (mCornerX - mBezierControl1.x)/ 2;
		mBezierStart1.y = mCornerY;
		if(mReadMode==MODE_TWO_PAGE){
			if(mBezierStart1.x<leftbound||mBezierStart1.x>rightbound){
				if (mBezierStart1.x < leftbound){
					mBezierStart1.x = leftbound;
				}else if(mBezierStart1.x>rightbound){
					mBezierStart1.x =rightbound;
				}
			}
		}
		// 当mBezierStart1.x < 0或者mBezierStart1.x > 480时
		// 如果继续翻页，会出现BUG故在此限制
		if (mTouch.x > leftbound && mTouch.x < rightbound) {
			if (mBezierStart1.x < leftbound || mBezierStart1.x > rightbound) {
				if (mBezierStart1.x < leftbound)
					mBezierStart1.x = rightbound - mBezierStart1.x;
				float f1 = Math.abs(mCornerX - mTouch.x);
				float f2 = rightbound * f1 / mBezierStart1.x;
				mTouch.x = Math.abs(mCornerX - f2);

				float f3 = Math.abs(mCornerX - mTouch.x)
						* Math.abs(mCornerY - mTouch.y) / f1;
				mTouch.y = Math.abs(mCornerY - f3);

				mMiddleX = (mTouch.x + mCornerX) / 2;
				mMiddleY = (mTouch.y + mCornerY) / 2;

				mBezierControl1.x = (float) (mMiddleX - (mCornerY - mMiddleY)
						* (mCornerY - mMiddleY) / getDivider(mCornerX, mMiddleX));
				mBezierControl1.y = mCornerY;

				mBezierControl2.x = mCornerX;
				mBezierControl2.y = (float) (mMiddleY - (mCornerX - mMiddleX)
						* (mCornerX - mMiddleX) / getDivider(mCornerY, mMiddleY));
				mBezierStart1.x = mBezierControl1.x	- (mCornerX - mBezierControl1.x) / 2;
			}
		}
		mBezierStart2.x = mCornerX;
		mBezierStart2.y = mBezierControl2.y - (mCornerY - mBezierControl2.y)/ 2;
		mTouchToCornerDis = (float) Math.hypot((mTouch.x - mCornerX),
				(mTouch.y - mCornerY));
		mBezierEnd1 = getCross(mTouch, mBezierControl1, mBezierStart1,mBezierStart2);
		mBezierEnd2 = getCross(mTouch, mBezierControl2, mBezierStart1,mBezierStart2);
		mBeziervertex1.x = (mBezierStart1.x + 2 * mBezierControl1.x + mBezierEnd1.x) / 4;
		mBeziervertex1.y = (2 * mBezierControl1.y + mBezierStart1.y + mBezierEnd1.y) / 4;
		mBeziervertex2.x = (mBezierStart2.x + 2 * mBezierControl2.x + mBezierEnd2.x) / 4;
		mBeziervertex2.y = (2 * mBezierControl2.y + mBezierStart2.y + mBezierEnd2.y) / 4;
		if(offsetx>0){
			mBezierStart1.x+=offsetx;
			mBezierControl1.x+=offsetx;
			mBezierEnd1.x+=offsetx;
			mBeziervertex1.x+=offsetx;
			mBezierStart2.x+=offsetx;
			mBezierControl2.x+=offsetx;
			mBezierEnd2.x+=offsetx;
			mBeziervertex2.x+=offsetx;
			mTouch.x+=offsetx;
			mCornerX+=offsetx;
		}
		if(offsety>0){
			mBezierStart1.y+=offsety;
			mBezierControl1.y+=offsety;
			mBezierEnd1.y+=offsety;
			mBeziervertex1.y+=offsety;
			mBezierStart2.y+=offsety;
			mBezierControl2.y+=offsety;
			mBezierEnd2.y+=offsety;
			mBeziervertex2.y+=offsety;
			mTouch.y+=offsety;
			mCornerY+=offsety;
		}
//		mTouchPrevious.x = mTouch.x;
//		mTouchPrevious.y = mTouch.y;
//		printLogError(TAG, "final mMiddleX=" + mMiddleX + "  mMiddleY=" + mMiddleY);
//		printLogInfo(TAG, "final mPageWidth=" + mPageWidth + "  mPageHeight=" + mPageHeight);
//		printLogInfo(TAG, "final mPositionRectDst=" + mPositionRectDst);
//		printLogInfo(TAG, "final mTouchX=" + mTouch.x + "  mTouchY=" + mTouch.y);
//		printLogInfo(TAG, "final mCornerX=" + mCornerX + "  mCornerY=" + mCornerY);
//		printLogInfo(TAG, "final mBezierStart1.x=" + mBezierStart1.x + "  mBezierStart1.y=" + mBezierStart1.y);
//		printLogInfo(TAG, "final mBezierStart2.x=" + mBezierStart2.x + "  mBezierStart2.y=" + mBezierStart2.y);
//		printLogInfo(TAG, "final mBezierControl1.x=" + mBezierControl1.x+ "  mBezierControl1.y=" + mBezierControl1.y);
//		printLogInfo(TAG, "final mBezierControl2.x=" + mBezierControl2.x+ "  mBezierControl2.y=" + mBezierControl2.y);
//		printLogInfo(TAG, "final mBezierEnd1.x=" + mBezierEnd1.x + "  mBezierEnd1.y="+ mBezierEnd1.y);
//		printLogInfo(TAG, "final mBezierEnd2.x=" + mBezierEnd2.x + "  mBezierEnd2.y="+ mBezierEnd2.y);
//		printLogInfo(TAG, "final mBeziervertex1.x=" + mBeziervertex1.x + "  mBeziervertex1.y=" + mBeziervertex1.y);
//		printLogError(TAG, "final mBeziervertex2.x=" + mBeziervertex2.x + "  mBeziervertex2.y=" + mBeziervertex2.y);
	}

	private double getDivider(float a, float b){
		final double exp = 10E-10;
		if(Float.compare(a, b)==0){
			return exp;
		}else{
			return a - b;
		}
	}
	
	/**
     *      绘制当前页
	 */
	private void drawCurrentPageArea(Canvas canvas, Bitmap bitmap, Path path) {
//		printLogInfo(TAG,"drawCurrentPageArea bitmap="+bitmap);
		mPath0.reset();
		mPath0.moveTo(mBezierStart1.x, mBezierStart1.y);
		mPath0.quadTo(mBezierControl1.x, mBezierControl1.y, mBezierEnd1.x,
				mBezierEnd1.y);
		mPath0.lineTo(mTouch.x, mTouch.y);
		mPath0.lineTo(mBezierEnd2.x, mBezierEnd2.y);
		mPath0.quadTo(mBezierControl2.x, mBezierControl2.y, mBezierStart2.x,
				mBezierStart2.y);
		mPath0.lineTo(mCornerX, mCornerY);
		mPath0.close();

		canvas.save();
		canvas.clipPath(mPath0, Region.Op.XOR);
		if(mFlipMode==TWOFLIPRIGHT){
			canvas.drawBitmap(bitmap, mPositionRectDst.left-bitmap.getWidth(), mPositionRectDst.top, mPaint);
		}else{
			canvas.drawBitmap(bitmap, mPositionRectDst.left, mPositionRectDst.top, mPaint);
		}
		canvas.restore();
	}

	/**
	 *     下页页面及阴影
	 */
	private void drawNextPageAreaAndShadow(Canvas canvas, Bitmap bitmap) {
//		printLogInfo(TAG,"drawNextPageAreaAndShadow bitmap="+bitmap);
		mPath1.reset();
		mPath1.moveTo(mBezierStart1.x, mBezierStart1.y);
		mPath1.lineTo(mBeziervertex1.x, mBeziervertex1.y);
		mPath1.lineTo(mBeziervertex2.x, mBeziervertex2.y);
		mPath1.lineTo(mBezierStart2.x, mBezierStart2.y);
		mPath1.lineTo(mCornerX, mCornerY);
		mPath1.close();

		mDegrees = (float) Math.toDegrees(Math.atan2(mBezierControl1.x
				- mCornerX, mBezierControl2.y - mCornerY));
		int leftx = 0;
		int rightx = 0;//take control of width of the shadow
		GradientDrawable mBackShadowDrawable;
		if (mIsRTandLB) {
			if(mReadMode==MODE_TWO_PAGE){
				if(mFlipMode==TWOFLIPLEFT&&mTouch.x<mPositionRectDst.left){
					leftx = (int) mBezierStart1.x;
					rightx = (int) (mBezierStart1.x - (mPositionRectDst.left-mPageWidth-mTouch.x)/3);
				}else if(mFlipMode==TWOFLIPRIGHT&&mTouch.x>mPositionRectDst.left){
					leftx = (int) mBezierStart1.x;
					rightx = (int) (mBezierStart1.x - (mTouch.x-mPositionRectDst.right)/3);
				}else{
					leftx = (int) (mBezierStart1.x);
					rightx = (int) (mBezierStart1.x + mTouchToCornerDis / 4);
				}
			}else{
				leftx = (int) (mBezierStart1.x);
				rightx = (int) (mBezierStart1.x + mTouchToCornerDis / 4);
			}
			mBackShadowDrawable = mBackShadowDrawableLR;
		} else {
			if(mReadMode==MODE_TWO_PAGE){
				if(mFlipMode==TWOFLIPLEFT&&mTouch.x<mPositionRectDst.left){
					leftx = (int) (mBezierStart1.x + (mPositionRectDst.left-mPageWidth-mTouch.x)/3);
					rightx = (int) mBezierStart1.x;
				}else if(mFlipMode==TWOFLIPRIGHT&&mTouch.x>mPositionRectDst.left){
					leftx = (int) (mBezierStart1.x + (mTouch.x-mPositionRectDst.right)/3);
					rightx = (int) mBezierStart1.x;
				}else{
					leftx = (int) (mBezierStart1.x - mTouchToCornerDis / 4);
					rightx = (int) mBezierStart1.x;
				}
			}else{
				leftx = (int) (mBezierStart1.x - mTouchToCornerDis / 4);
				rightx = (int) mBezierStart1.x;
			}
			mBackShadowDrawable = mBackShadowDrawableRL;
		}
		canvas.save();
		canvas.clipPath(mPath0);
		canvas.clipPath(mPath1, Region.Op.INTERSECT);
		if(mFlipMode!=TWOFLIPRIGHT){
			canvas.drawBitmap(bitmap,mPositionRectDst.left, mPositionRectDst.top, mPaint);
		}
		canvas.rotate(mDegrees, mBezierStart1.x, mBezierStart1.y);
		mBackShadowDrawable.setBounds(leftx, (int) mBezierStart1.y, rightx,
				(int) (mMaxLength + mBezierStart1.y));
		mBackShadowDrawable.draw(canvas);
		canvas.restore();
	}

	/**
	 * 创建阴影的GradientDrawable
	 */
	private void createDrawable() {
		int[] color = { 0x333333, 0x80333333 };
		mFolderShadowDrawableRL = new GradientDrawable(
				GradientDrawable.Orientation.RIGHT_LEFT, color);
		mFolderShadowDrawableRL
				.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		mFolderShadowDrawableLR = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, color);
		mFolderShadowDrawableLR
				.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		mBackShadowColors = new int[] { 0x80111111, 0x111111 };
		mBackShadowDrawableRL = new GradientDrawable(
				GradientDrawable.Orientation.RIGHT_LEFT, mBackShadowColors);
		mBackShadowDrawableRL.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		mBackShadowDrawableLR = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, mBackShadowColors);
		mBackShadowDrawableLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		mFrontShadowColors = new int[] { 0x40111111, 0x111111 };
		mFrontShadowDrawableVLR = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, mFrontShadowColors);
		mFrontShadowDrawableVLR
				.setGradientType(GradientDrawable.LINEAR_GRADIENT);
		mFrontShadowDrawableVRL = new GradientDrawable(
				GradientDrawable.Orientation.RIGHT_LEFT, mFrontShadowColors);
		mFrontShadowDrawableVRL
				.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		mFrontShadowDrawableHTB = new GradientDrawable(
				GradientDrawable.Orientation.TOP_BOTTOM, mFrontShadowColors);
		mFrontShadowDrawableHTB
				.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		mFrontShadowDrawableHBT = new GradientDrawable(
				GradientDrawable.Orientation.BOTTOM_TOP, mFrontShadowColors);
		mFrontShadowDrawableHBT
				.setGradientType(GradientDrawable.LINEAR_GRADIENT);
	}

	/**
	 * 绘制翻起页的阴影
	 * @param canvas the canvas
	 */
	public void drawCurrentPageShadow(Canvas canvas) {
//		printLogInfo(TAG,"drawCurrentPageShadow called");
		double degree;
		if (mIsRTandLB) {
			degree = Math.PI/4 - Math.atan2(mBezierControl1.y - mTouch.y, 
					mTouch.x - mBezierControl1.x);
		} else {
			degree = Math.PI/4 - Math.atan2(mTouch.y - mBezierControl1.y, 
					mTouch.x - mBezierControl1.x);
		}
		// 翻起页阴影顶点与touch点的距离
		double d1 = (float) 25 * 1.414 * Math.cos(degree);
		double d2 = (float) 25 * 1.414 * Math.sin(degree);
		float x = (float) (mTouch.x + d1);
		float y;
		if (mIsRTandLB) {
			y = (float) (mTouch.y + d2);
		} else {
			y = (float) (mTouch.y - d2);
		}
		mPath1.reset();
		mPath1.moveTo(x, y);
		mPath1.lineTo(mTouch.x, mTouch.y);
		mPath1.lineTo(mBezierControl1.x, mBezierControl1.y);
		mPath1.lineTo(mBezierStart1.x, mBezierStart1.y);
		mPath1.close();
		float rotateDegrees;
		canvas.save();

		canvas.clipPath(mPath0, Region.Op.XOR);
		canvas.clipPath(mPath1, Region.Op.INTERSECT);
		int leftx = 0;
		int rightx = 0;
		GradientDrawable mCurrentPageShadow;
		if (mIsRTandLB) {
			leftx = (int) (mBezierControl1.x);
			rightx = (int) mBezierControl1.x + 25;
			mCurrentPageShadow = mFrontShadowDrawableVLR;
		} else {
			leftx = (int) (mBezierControl1.x - 25);
			rightx = (int) mBezierControl1.x + 1;
			mCurrentPageShadow = mFrontShadowDrawableVRL;
		}

		rotateDegrees = (float) Math.toDegrees(Math.atan2(mTouch.x
				- mBezierControl1.x, mBezierControl1.y - mTouch.y));
		canvas.rotate(rotateDegrees, mBezierControl1.x, mBezierControl1.y);
		mCurrentPageShadow.setBounds(leftx,
				(int) (mBezierControl1.y - mMaxLength), rightx,
				(int) (mBezierControl1.y));
		mCurrentPageShadow.draw(canvas);
		canvas.restore();

		int upY = 0;
		int downY = 0;
		mPath1.reset();
		mPath1.moveTo(x, y);
		mPath1.lineTo(mTouch.x, mTouch.y);
		mPath1.lineTo(mBezierControl2.x, mBezierControl2.y);
		mPath1.lineTo(mBezierStart2.x, mBezierStart2.y);
		mPath1.close();
		canvas.save();
		canvas.clipPath(mPath0, Region.Op.XOR);
		canvas.clipPath(mPath1, Region.Op.INTERSECT);
		if (mIsRTandLB) {
			upY = (int) (mBezierControl2.y);
			downY = (int) (mBezierControl2.y + 25);
			mCurrentPageShadow = mFrontShadowDrawableHTB;
		} else {
			upY = (int) (mBezierControl2.y - 25);
			downY = (int) (mBezierControl2.y + 1);
			mCurrentPageShadow = mFrontShadowDrawableHBT;
		}
		rotateDegrees = (float) Math.toDegrees(
				Math.atan2(mBezierControl2.y - mTouch.y, mBezierControl2.x - mTouch.x));
		canvas.rotate(rotateDegrees, mBezierControl2.x, mBezierControl2.y);
		float temp;
		if (mBezierControl2.y < 0)
			temp = mBezierControl2.y - mPageHeight;
		else
			temp = mBezierControl2.y;
		int hmg = (int) Math.hypot(mBezierControl2.x, temp);
		if (hmg > mMaxLength){
			mCurrentPageShadow.setBounds((int) (mBezierControl2.x - 25) - hmg,
					upY,(int) (mBezierControl2.x + mMaxLength) - hmg,downY);
		}
		else{
			mCurrentPageShadow.setBounds(
					(int) (mBezierControl2.x - mMaxLength), upY,
					(int) (mBezierControl2.x), downY);
		}
		mCurrentPageShadow.draw(canvas);
		canvas.restore();
	}
	
	/**
	 * 绘制翻起页背面
	 * @param canvas the canvas
	 * @param bitmap the bitmap
	 */
	private void drawCurrentBackArea(Canvas canvas, Bitmap bitmap) {
//		printLogInfo(TAG,"drawCurrentBackArea bitmap="+bitmap);
		int MidXOfStAndCtrl = (int) (mBezierStart1.x + mBezierControl1.x) / 2;
		float DistXBetweenMidAndCtrl = Math.abs(MidXOfStAndCtrl - mBezierControl1.x);
		int MidYOfStartAndControl = (int) (mBezierStart2.y + mBezierControl2.y) / 2;
		float DistYBetweenMidAndCtrl = Math.abs(MidYOfStartAndControl - mBezierControl2.y);
		float MinDistance = Math.min(DistXBetweenMidAndCtrl, DistYBetweenMidAndCtrl);
		mPath1.reset();
		mPath1.moveTo(mBeziervertex2.x, mBeziervertex2.y);
		mPath1.lineTo(mBeziervertex1.x, mBeziervertex1.y);
		mPath1.lineTo(mBezierEnd1.x, mBezierEnd1.y);
		mPath1.lineTo(mTouch.x, mTouch.y);
		mPath1.lineTo(mBezierEnd2.x, mBezierEnd2.y);
		mPath1.close();
		GradientDrawable mFolderShadowDrawable;
		int left;
		int right;
		if (mIsRTandLB) {
			left = (int) (mBezierStart1.x - 1);
			right = (int) (mBezierStart1.x + MinDistance + 1);
			mFolderShadowDrawable = mFolderShadowDrawableLR;
		} else {
			left = (int) (mBezierStart1.x - MinDistance - 1);
			right = (int) (mBezierStart1.x + 1);
			mFolderShadowDrawable = mFolderShadowDrawableRL;
		}
		canvas.save();
		canvas.clipPath(mPath0);
		canvas.clipPath(mPath1, Region.Op.INTERSECT);
//		//画边角缺失颜色
//		mPaint.setAlpha(0xff);
//		mPaint.setColor(computeColor(bitmap));
//		canvas.drawPath(mPath1, mPaint);
//		canvas.save();
		float DistanceBetweenTwoCtrls = (float) Math.hypot(mCornerX - mBezierControl1.x,
				mBezierControl2.y - mCornerY);
		//if A is the angle of two line segments,one line segment is terminated by two control points,
		//and the other one  is terminated by corner and the control point one
		float cosA = (mCornerX - mBezierControl1.x) / DistanceBetweenTwoCtrls;
		float sinA = (mBezierControl2.y - mCornerY) / DistanceBetweenTwoCtrls;//sinA
		mMatrixArray[0] = 1 - 2 * sinA * sinA;//cos2A
		mMatrixArray[1] = 2 * cosA * sinA;//sin2A
		mMatrixArray[3] = mMatrixArray[1];//sin2A
		mMatrixArray[4] = 1 - 2 * cosA * cosA;//-cos2A
		mMatrix.reset();
		mMatrix.setValues(mMatrixArray);
		if(mFlipMode==TWOFLIPRIGHT){
			mMatrix.preTranslate(mPositionRectDst.left-bitmap.getWidth(), mPositionRectDst.top);
		}else{
			mMatrix.preTranslate(mPositionRectDst.left, mPositionRectDst.top);
		}
		mMatrix.preTranslate(-mBezierControl1.x, -mBezierControl1.y);
		mMatrix.postTranslate(mBezierControl1.x, mBezierControl1.y);
		if(mReadMode==MODE_ONE_PAGE){
			mPaint.setAlpha(0x40);
		}else{
			mPaint.setAlpha(0xff);
		}
		canvas.drawBitmap(bitmap, mMatrix, mPaint);
		mPaint.setAlpha(0xff);
		canvas.rotate(mDegrees, mBezierStart1.x, mBezierStart1.y);
		mFolderShadowDrawable.setBounds(left, (int) mBezierStart1.y, right,
				(int) (mBezierStart1.y + mMaxLength));
		mFolderShadowDrawable.draw(canvas); 
		canvas.restore();
	}

	/**
	 * 边角缺失颜色
	 */
	public int computeColor(Bitmap bitmap){
		long r = 0, g = 0, b = 0;
		int wEnd = 5;
		int hEnd = 5;
		for (int i = 0; i < wEnd; ++i) {
			for (int j = 0; j < hEnd; ++j) {
				int color = bitmap.getPixel(i, j);
				r += color & 0xFF0000;
				g += color & 0xFF00;
				b += color & 0xFF;
			}
		}
		r /= wEnd * hEnd;
		g /= wEnd * hEnd;
		b /= wEnd * hEnd;
		r >>= 16;
		g >>= 8;
		return Color.rgb((int)(r & 0xFF), (int)(g & 0xFF), (int)(b & 0xFF));
	}
	
	/**
	 * compute the scroll factor of page
	 * */
	public void computeScroll() {
		if (isAutoFlip) {
			if(mScroller.computeScrollOffset()){
				float x = mScroller.getCurrX();
				float y = mScroller.getCurrY();
				mTouch.x = x;
				if(mCornerY>0&&y>mPageHeight-minValue)
					mTouch.y = mPageHeight-minValue;
				else if(mCornerY==0&&y<minValue)
					mTouch.y = minValue;
				else
					mTouch.y = y;
				mTargetView.postInvalidate();
			}else{
				if(!isDraging){
					isAutoFlip = false;
				}
			}
		}
	}
	
	/**
	 * start page turning animation automatically
	 */
	public void startAnimation() {
		int dx = 0;
		int dy = 0;
		int top = -1;
		/**
		 * Calculate duration by distance
		 * */
		int tmpDuration = 0;
		if(mReadMode==MODE_ONE_PAGE){
			int left = -1;
			left = mPositionRectDst.left;
			top = mPositionRectDst.top;
			// dx 水平方向滑动的距离，负值会使滚动向左滚动
			// dy 垂直方向滑动的距离，负值会使滚动向上滚动
			if (mCornerX > left&&mFlipMode==ONEFLIPLEFT) {
				dx = -(int) (mPageWidth + mTouch.x-left);
				tmpDuration = (-dx*mDuration)/(2*mPageWidth);
			} else {
				dx = (int) (mPageWidth+left - mTouch.x/* + mPageWidth*/);
				tmpDuration = (dx*mDuration)/(mPageWidth);
			}
			if (mCornerY > top) {
				dy = (int) (mPageHeight - mTouch.y+mPositionRectDst.top);
			} else {
				dy = (int) (1f - mTouch.y+mPositionRectDst.top); // 防止mTouch.y最终变为0
			}
		}else{
			int mid = mPositionRectDst.left;
			top = mPositionRectDst.top;
			if(mCornerX>mid){
				dx = -(int) (mPageWidth + mTouch.x-mid);
				tmpDuration = (-dx*mDuration)/(2*mPageWidth);
			}else {
				dx = (int) (mPageWidth + mid - mTouch.x);
				tmpDuration = (dx*mDuration)/(2*mPageWidth);
			}
			if (mCornerY > top) {
				dy = (int) (mPageHeight - mTouch.y+mPositionRectDst.top);
			} else {
				dy = (int) (1f - mTouch.y+mPositionRectDst.top); // 防止mTouch.y最终变为0
			}
		}
		mEffectState = PAGEFLIPPEDANIMATION;
		printLogInfo(TAG,"startAnimation mTouch.x="+mTouch.x+", dx="+dx+", dy="+dy);
		mScroller.startScroll((int) mTouch.x, (int) mTouch.y, dx, dy,tmpDuration);
		mTargetView.postInvalidate();
	}

	/**
	 * start page return animation automatically
	 */
	public void startPageTurningAbortAnimation() {
		int dx = 0;
		int dy = 0;
		int top = -1;
		/**
		 * Calculate duration by distance
		 * */
		int tmpDuration = 0;
		if(mReadMode==MODE_ONE_PAGE){
			int left = -1;
			left = mPositionRectDst.left;
			top = mPositionRectDst.top;
			// dx 水平方向滑动的距离，负值会使滚动向左滚动
			// dy 垂直方向滑动的距离，负值会使滚动向上滚动
			if (mCornerX > left&&mFlipMode==ONEFLIPLEFT) {
				dx = (int) (mCornerX -left- mTouch.x);
				tmpDuration = (int) (dx*mDuration/mPageWidth);
			} else {
				dx = (int) (left - mTouch.x-mPageWidth);
				tmpDuration = (int) (-dx*mDuration)/(2*mPageWidth);
			}
			if (mCornerY > top) {
				dy = (int) (mPageHeight - mTouch.y+mPositionRectDst.top);
			} else {
				dy = (int) (1f - mTouch.y+mPositionRectDst.top); // 防止mTouch.y最终变为0
			}
		}else{
			int mid = mPositionRectDst.left;
			top = mPositionRectDst.top;
			if(mCornerX>mid){
				dx = (int) (mCornerX -(mid-mPageWidth)- mTouch.x);
				tmpDuration = (int) (dx*mDuration/(mPageWidth*2));
			}else {
				dx = (int) ( mTouch.x-(mid+mPageWidth));
				tmpDuration = (int) (-dx*mDuration/(mPageWidth*2));
			}
			if (mCornerY > top) {
				dy = (int) (mPageHeight - mTouch.y+mPositionRectDst.top);
			} else {
				dy = (int) (1f - mTouch.y+mPositionRectDst.top); // 防止mTouch.y最终变为0
			}
		}
		mEffectState = PAGEFLIPPEDANIMATION;
		mScroller.startScroll((int) mTouch.x, (int) mTouch.y, dx, dy,tmpDuration);
		mTargetView.postInvalidate();
	}
	
	/**
	 * abort current animation
	 */
	public void abortAnimation() {
		Log.e(getClass().getSimpleName(),"abortAnimation called");
		if (!mScroller.isFinished()) {
			mScroller.abortAnimation();
		}
		mTouch.x = mCornerX - 0.09f;
		mTouch.y = mCornerY - 0.09f;
		mFlatFlip = false;
		mEffectState = PAGENORMAL;
		isAutoFlip = false;
	}
	
	void terminate(){
		Log.e(getClass().getSimpleName(),"terminate animation");
		abortAnimation();
		super.terminate();
	}
	
	public boolean canDragOver() {
		if (mTouchToCornerDis > mPageWidth / 10)
			return true;
		return false;
	}
	
	/**
	 * 是否从左边翻向右边
	 */
	public boolean DragToRight() {
		if (mCornerX > 0)
			return false;
		return true;
	}
	
	@Override
	protected void startAnimatedScrollingInternal(int speedfactor) {
		// TODO Auto-generated method stub
		setDuration(speedfactor*400);
		
	}

	@Override
	protected void setupAnimatedScrollingStart(Integer x, Integer y) {
		// TODO Auto-generated method stub
		printLogInfo(TAG,"setupAnimatedScrollingStart start myStartX="+myStartX+
				", myStartY="+myStartY+", x="+x+", y="+y);
		myStartX = x;
		if(mPageWidth<=0){
			setPageSize(mTargetView.getWidth()/2,mTargetView.getHeight());
		}
		if(x<mPageWidth){
			myEndX=myStartX+10;
		}else{
			myEndX=myStartX-10;
		}
		myStartY = y;
		printLogInfo(TAG,"setupAnimatedScrollingStart end myStartX="+myStartX+", myStartY="+myStartY);
	}

	@Override
	PageIndex getPageToScrollTo(int x, int y) {
		if (myDirection == null) {
			return ZLView.PageIndex.next;
		}
		switch (myDirection) {
			case rightToLeft:
				return myStartX < x ? ZLView.PageIndex.previous : ZLView.PageIndex.next;
			case leftToRight:
				return myStartX < x ? ZLView.PageIndex.next : ZLView.PageIndex.previous;
			case up:
				return myStartY < y ? ZLView.PageIndex.previous : ZLView.PageIndex.next;
			case down:
				return myStartY < y ? ZLView.PageIndex.next : ZLView.PageIndex.previous;
		}
		return ZLView.PageIndex.current;
	}
	
	/**
	  * 中断自动翻页状态
	  */     
	@Override
	void doStep() {
		if(!isAutoFlip){
			printLogError(TAG,"doStep called super.terminate()");
			terminate();
		}
	}
		
	/**
	 *  获得翻页持续时间
	 */
	public int getDuration() {
		return mDuration;
	}

	/**
	 *      设定翻页持续时间
	 */
	public void setDuration(int mDuration) {
		this.mDuration = mDuration;
	}
	
	/**
	 * set touch point 
	 * 
	 * @param p
	 *            point on the screen
	 */
	public void setTouchPoint(PointF p){
		mTouch = p;
		if(	mEffectState==PAGEDRAGGING&&
				!mFlatFlip){
			//distance to pivot 
			float touchToPiovt = (float) Math.hypot((mTouch.x - mPositionRectDst.left),
					(mTouch.y - mCornerY));
			/**
			 * x,y is the point of an ellipse
			 * x=x1+r*cosA
			 * y=y1+(Math.sqrt(2)/2r)*sinA
			 * */
			if((Math.pow(mTouch.x-mPositionRectDst.left, 2))/(mPageWidth*mPageWidth)+
					(Math.pow(mTouch.y-mCornerY,2))/(Math.pow(0.8f*mPageWidth,2))>1){
				mTouch.y = (float) (mCornerY+
						(mPageWidth*Math.sqrt(2)/2)*((mTouch.y-mCornerY)/touchToPiovt));
				mTouch.x = (float) (mPositionRectDst.left+
						(mPageWidth)*((mTouch.x-mPositionRectDst.left)/touchToPiovt));
			}
		}
		if((mTouch.x>=(mPositionRectDst.left+mPageWidth-2))||
				(mTouch.y>=(mPageHeight+mPositionRectDst.top-10))||
				(mTouch.y<=mPositionRectDst.top+10&&!mFlatFlip)){
			printLogInfo(TAG,"setTouchPoint call abortAnimation");
			abortAnimation();
			return;
		}
		if(mFlatFlip){
			mTouch.y=mCornerY+0.0001f;
		}
		printLogInfo(TAG,"setTouchPoint mTouch.x="+mTouch.x+", mTouch.y="+mTouch.y);
	}
	
	/**
	 * destroy bitmap and drawables
	 * */
	public void destroy(){
		destroyBitmap(mBeforePreviousPageBitmap);
		destroyBitmap(mPreviousPageBitmap);
		destroyBitmap(mPreviousBackPageBitmap);
		destroyBitmap(mNextPageBitmap);
		destroyDrawable(mBackShadowDrawableLR);
		destroyDrawable(mBackShadowDrawableRL);
		destroyDrawable(mFolderShadowDrawableLR);
		destroyDrawable(mFolderShadowDrawableRL);
		destroyDrawable(mFrontShadowDrawableHBT);
		destroyDrawable(mFrontShadowDrawableHTB);
		destroyDrawable(mFrontShadowDrawableVLR);
		destroyDrawable(mFrontShadowDrawableVRL);
	}

	/**
	 * set mode of flat flip
	 * 
	 * @param mMode
	 *            true or false
	 */
	public void setFlatFlipMode(boolean mMode) {
		this.mFlatFlip = mMode;
	}
	
	/**
	 * create a new bitmap base on a given bitmap
	 * return with the content left-side-right
	 * @param bmp the original bitmap
	 * @param isNeedRecycle is need to recycle the original bitmap
	 * @return the reversed bitmap
	 * */
	private Bitmap reverseBitmapLeftToRight(Bitmap bmp, boolean isNeedRecycle){
		//float array for transforming bitmap content to left-side-right
		float matrix_values[] = {-1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f};
		final Matrix m = new Matrix();
		m.setValues(matrix_values);
		//transform the back page bitmap content to left-right reversed
		final Bitmap tmpBitmap = Bitmap.createBitmap(bmp,0,0,
				bmp.getWidth(),bmp.getHeight(),
				m,true);
		//recycle the former bitmap
		if(isNeedRecycle){
			bmp.recycle();
			bmp = null;
		}
		//return the new create bitmap
		return tmpBitmap;
	}
	
	/**
	 * tell the system to recycle the bitmap
	 * @param bitmap the bitmap object
	 * */
	private void destroyBitmap(Bitmap bitmap){
		if (bitmap != null && !bitmap.isRecycled())
		{
			bitmap.recycle();
			bitmap = null;
		}
	}
	
	/**
	 * destroy drawable and remove its callback
	 * @param drawable the drawable to destroy
	 * */
	private void destroyDrawable(Drawable drawable){
		if(drawable!=null){
			drawable.setCallback(null);
			drawable = null;
		}
	}

	/**
	 *     设定屏幕高度宽度
	 */
	public void setPageSize(int w, int h) {
		mPageWidth = w;//-getPageBorder();
		mPageHeight = h;//-getPageBottomBorder();
	}
	
	private int getPageBorder(){
		if(mPageBorder<0){
			mPageBorder = ((DbStarPlayerApp)ZLApplication.Instance()).getEdgeWidth();
		}
		return mPageBorder; 
	}
	
	private int getPageBottomBorder(){
		if(mPageBottomBorder<0){
			mPageBottomBorder = ((DbStarPlayerApp)ZLApplication.Instance()).getEdgeBottom();
		}
		return mPageBottomBorder;
	}
}
