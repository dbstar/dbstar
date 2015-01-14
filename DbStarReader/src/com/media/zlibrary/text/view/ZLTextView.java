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

package com.media.zlibrary.text.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.media.android.dbstarplayer.AudioPlayAction;
import com.media.android.dbstarplayer.R;
import com.media.dbstarplayer.book.Bookmark;
import com.media.dbstarplayer.bookmodel.FBTextKind;
import com.media.dbstarplayer.dbstarplayer.ActionCode;
import com.media.dbstarplayer.dbstarplayer.BookmarkHighlighting;
import com.media.dbstarplayer.dbstarplayer.DbStarPlayerApp;
import com.media.player.common.Utils;
import com.media.reader.model.ImageManager;
import com.media.reader.view.ReadProgressChangeListener;
import com.media.reader.vo.ImageEntity;
import com.media.reader.vo.ImageEntity.ImageAlign;
import com.media.zlibrary.core.application.ZLApplication;
import com.media.zlibrary.core.util.ZLColor;
import com.media.zlibrary.core.view.ZLPaintContext;
import com.media.zlibrary.core.view.ZLPaintContext.Size;
import com.media.zlibrary.core.view.ZLView;
import com.media.zlibrary.text.hyphenation.ZLTextHyphenationInfo;
import com.media.zlibrary.text.hyphenation.ZLTextHyphenator;
import com.media.zlibrary.text.model.ZLTextAlignmentType;
import com.media.zlibrary.text.model.ZLTextMark;
import com.media.zlibrary.text.model.ZLTextModel;
import com.media.zlibrary.text.model.ZLTextParagraph;
import com.media.zlibrary.text.view.style.ZLTextStyleCollection;
import com.media.zlibrary.ui.android.library.ZLAndroidLibrary;
import com.media.zlibrary.ui.android.view.AnimationProvider;
import com.media.zlibrary.ui.android.view.BitmapManager.EdgePosition;
import com.media.zlibrary.ui.android.view.ZLAndroidPaintContext;
import com.media.zlibrary.ui.android.view.ZLAndroidWidget;

public abstract class ZLTextView extends ZLTextViewBase {
	private final String TAG = getClass().getSimpleName();
	public static final int MAX_SELECTION_DISTANCE = 10;

	public interface ScrollingMode {
		int NO_OVERLAPPING = 0;
		int KEEP_LINES = 1;
		int SCROLL_LINES = 2;
		int SCROLL_PERCENTAGE = 3;
	};

	private ZLTextModel myModel;

	private interface SizeUnit {
		int PIXEL_UNIT = 0;
		int LINE_UNIT = 1;
	};

	private int myScrollingMode;
	private int myOverlappingValue;

	/**
	 * double flip mode, this is always the flip page back side, as the third page
	 */
	private ZLTextPage myPreviousPage = new ZLTextPage();
	/**
	 * double flip mode, this is always the left and current page, as the first page
	 */
	public ZLTextPage myCurrentPage = new ZLTextPage();
	/**
	 * double flip mode, this is always the new second page, as the fourth page
	 */
	private ZLTextPage myNextPage = new ZLTextPage();
	/**
	 * double flip mode, the left page is the current page, this is the second page
	 */
	public ZLTextPage myCurrentRightPage = new ZLTextPage();

	private final HashMap<ZLTextLineInfo,ZLTextLineInfo> myLineInfoCache = new HashMap<ZLTextLineInfo,ZLTextLineInfo>();

	private ZLTextRegion.Soul mySelectedRegionSoul;
	private boolean myHighlightSelectedRegion = true;

	private final ZLTextSelection mySelection = new ZLTextSelection(this);
	private final Set<ZLTextHighlighting> myHighlightings =
		Collections.synchronizedSet(new TreeSet<ZLTextHighlighting>());

	/**
	 * book mark bitmap
	 */
	private Bitmap mBookMarkBitmap = null;
	
    /**
     * width of book mark image
     */
    private int mBookMarkImageWidth=-1;
    
    /**
     * height of book mark image
     */
    private int mBookMarkImageHeight=-1;
    
    /**
     * current drawing book mark height
     * */
//    private int mBookMarkDrawingHeight = -1;
    
    /**
     * the drawing step of the book mark bitmap
     * */
//    private final int mBookMarKDrawingHeightStep = 30;
    
    /**
     * transparent height, measure as dip
     * */
    
    private int mBookMarkTransparentHeight = 17;
    
    /**
     * flag of doing drawing book mark animation
     * */
//    private boolean isDrawBookmarkAnitmation = false;
    /**
     * flag of show volume status
     * */
    private boolean isShowVolumeStatus = false;
    
    private AudioManager mAudioManager = null;
    
    private Bitmap mAudioBackBitmap =null;
    
    private Bitmap mAudioLevelBitmap = null;
    
    private int mCurAudioLevel = -1;
    
    private int mCurAudioLevelImageResID = -1;
    
	private volatile HideVolumeStatusRunnable mHideVolumeStatusRunnable;

	private final int VOLUME_DISMISS_TIME = 2000;
	
	private ReadProgressChangeListener mReadProgressChangeListener = null;
	public ZLTextView(ZLApplication application) {
		super(application);
		getBookMarkBitmap();
		getAudioBackBitmap();
	}

	private void getBookMarkBitmap() {
		
		if(mBookMarkBitmap==null||mBookMarkBitmap.isRecycled()){
			final Context con = ((ZLAndroidLibrary)ZLAndroidLibrary.Instance()).getWidget().getContext();
			mBookMarkBitmap = ImageManager.getInstance().loadBitmapByResourceId(con, R.drawable.reader_read_bookmark_pink);
			mBookMarkImageWidth = mBookMarkBitmap.getWidth();
			mBookMarkImageHeight = mBookMarkBitmap.getHeight();
			mBookMarkTransparentHeight = Utils.dip2px(con, mBookMarkTransparentHeight);
		}
	}

	private Bitmap getAudioBackBitmap(){
		if(null==mAudioBackBitmap||mAudioBackBitmap.isRecycled()){
			mAudioBackBitmap = ImageManager.getInstance().loadBitmapByResourceId(((ZLAndroidLibrary)ZLAndroidLibrary.
					Instance()).getWidget().getContext(), R.drawable.reader_read_volume_background);
		}
		return mAudioBackBitmap;
	}
	
	public synchronized void setModel(ZLTextModel model) {
		ZLTextParagraphCursorCache.clear();

		mySelection.clear();
		myHighlightings.clear();

		myModel = model;
		myCurrentPage.reset();
		myPreviousPage.reset();
		myNextPage.reset();
		myCurrentRightPage.reset();
		
		if (myModel != null) {
			final int paragraphsNumber = myModel.getParagraphsNumber();
			if (paragraphsNumber > 0) {
				myCurrentPage.moveStartCursor(ZLTextParagraphCursor.cursor(myModel, 0));
			}
		}
		Application.getViewWidget().reset();
	}

	public ZLTextModel getModel() {
		return myModel;
	}

	public ZLTextWordCursor getStartCursor() {
		if (myCurrentPage.StartCursor.isNull()) {
			preparePaintInfo(myCurrentPage);
		}
		return myCurrentPage.StartCursor;
	}

	public ZLTextWordCursor getEndCursor() {
		if (myCurrentPage.EndCursor.isNull()) {
			preparePaintInfo(myCurrentPage);
		}
		return myCurrentPage.EndCursor;
	}

	public ZLTextWordCursor getDoublePageStartCursor(){
		if (myCurrentRightPage.StartCursor.isNull()) {
			preparePaintInfo(myCurrentRightPage);
		}
		return myCurrentRightPage.StartCursor;
	}
	
	public ZLTextWordCursor getDoublePageEndCursor() {
		if (myCurrentRightPage.EndCursor.isNull()) {
			preparePaintInfo(myCurrentRightPage);
		}
		return myCurrentRightPage.EndCursor;
	}
	
	private synchronized void gotoMark(ZLTextMark mark) {
		if (mark == null) {
			return;
		}

		myPreviousPage.reset();
		myNextPage.reset();
		myCurrentRightPage.reset();
		boolean doRepaint = false;
		if (myCurrentPage.StartCursor.isNull()) {
			doRepaint = true;
			preparePaintInfo(myCurrentPage);
		}
		if (myCurrentPage.StartCursor.isNull()) {
			return;
		}
		if (myCurrentPage.StartCursor.getParagraphIndex() != mark.ParagraphIndex ||
			myCurrentPage.StartCursor.getMark().compareTo(mark) > 0) {
			doRepaint = true;
			gotoPosition(mark.ParagraphIndex, 0, 0);
			preparePaintInfo(myCurrentPage);
		}
		if (myCurrentPage.EndCursor.isNull()) {
			preparePaintInfo(myCurrentPage);
		}
		while (mark.compareTo(myCurrentPage.EndCursor.getMark()) > 0) {
			doRepaint = true;
			scrollPage(true, ScrollingMode.NO_OVERLAPPING, 0);
			preparePaintInfo(myCurrentPage);
		}
		if (doRepaint) {
			if (myCurrentPage.StartCursor.isNull()) {
				preparePaintInfo(myCurrentPage);
			}
			refreshReadPage();
		}
		setAudioSpeekPosition();
	}

	public synchronized void gotoHighlighting(ZLTextHighlighting highlighting) {
		myPreviousPage.reset();
		myNextPage.reset();
		myCurrentRightPage.reset();
		boolean doRepaint = true;
		if (myCurrentPage.StartCursor.isNull()) {
//			doRepaint = true;
			preparePaintInfo(myCurrentPage);
		}
		if (myCurrentPage.StartCursor.isNull()) {
			return;
		}
		if (!highlighting.intersects(myCurrentPage)) {
			gotoPosition(highlighting.getStartPosition().getParagraphIndex(), highlighting.getStartPosition().getElementIndex(), highlighting.getStartPosition().getCharIndex());
			preparePaintInfo(myCurrentPage);
		}
		if (myCurrentPage.EndCursor.isNull()) {
			preparePaintInfo(myCurrentPage);
		}
		int loopcount = 0;
		ZLTextWordCursor curPageStartCopy = new ZLTextWordCursor(myCurrentPage.StartCursor);
		while (!highlighting.intersects(myCurrentPage)) {
			doRepaint = true;
			scrollPage(true, ScrollingMode.NO_OVERLAPPING, 0);
			preparePaintInfo(myCurrentPage);
			loopcount++;
			if(loopcount>1000){
				Utils.printLogError(TAG, "Error, can't find the book mark! break!");
				myCurrentPage.StartCursor.setCursor(curPageStartCopy);
				doRepaint = true;
				break;
			}
		}
		if(ZLApplication.Instance().getViewWidget().getCurAnimationType()==Animation.realdouble){
			if (myCurrentPage.StartCursor.isNull()) {
				preparePaintInfo(myCurrentPage);
			}
			myCurrentPage.EndCursor.reset();
			myCurrentPage.EndCursor.setCursor(myCurrentPage.StartCursor);
			myCurrentPage.PaintState= PaintStateEnum.END_IS_KNOWN; 
			preparePaintInfo(myCurrentPage);
		}
		if (doRepaint) {
			if (myCurrentPage.StartCursor.isNull()) {
				preparePaintInfo(myCurrentPage);
			}
			refreshReadPage();
		}
		setAudioSpeekPosition();
	}

	public synchronized int search(final String text, boolean ignoreCase, boolean wholeText, boolean backward, boolean thisSectionOnly) {
		if (TextUtils.isEmpty(text)) {
			return 0;
		}
		int startIndex = 0;
		int endIndex = myModel.getParagraphsNumber();
		if (thisSectionOnly) {
			// TODO: implement
		}
		int count = myModel.search(text, startIndex, endIndex, ignoreCase);
		myPreviousPage.reset();
		myNextPage.reset();
		if (!myCurrentPage.StartCursor.isNull()) {
			rebuildPaintInfo();
			if (count > 0) {
				ZLTextMark mark = myCurrentPage.StartCursor.getMark();
				gotoMark(wholeText ?
					(backward ? myModel.getLastMark() : myModel.getFirstMark()) :
					(backward ? myModel.getPreviousMark(mark) : myModel.getNextMark(mark)));
			}
			refreshReadPage();
		}
		return count;
	}

	public boolean canFindNext() {
		final ZLTextWordCursor end = myCurrentPage.EndCursor;
		return !end.isNull() && (myModel != null) && (myModel.getNextMark(end.getMark()) != null);
	}

	public synchronized void findNext() {
		final ZLTextWordCursor end = myCurrentPage.EndCursor;
		if (!end.isNull()) {
			gotoMark(myModel.getNextMark(end.getMark()));
		}
	}

	public boolean canFindPrevious() {
		final ZLTextWordCursor start = myCurrentPage.StartCursor;
		return !start.isNull() && (myModel != null) && (myModel.getPreviousMark(start.getMark()) != null);
	}

	public synchronized void findPrevious() {
		final ZLTextWordCursor start = myCurrentPage.StartCursor;
		if (!start.isNull()) {
			gotoMark(myModel.getPreviousMark(start.getMark()));
		}
	}

	public void clearFindResults() {
		if (!findResultsAreEmpty()) {
			myModel.removeAllMarks();
			rebuildPaintInfo();
			refreshReadPage();
		}
	}

	public boolean findResultsAreEmpty() {
		return (myModel == null) || myModel.getMarks().isEmpty();
	}

	@Override
	public synchronized void onScrollingFinished(PageIndex pageIndex) {
		Utils.printLogError(TAG, "onScrollingFinished pageIndex:"+pageIndex);
		final Animation type = ZLApplication.Instance().getViewWidget().getCurAnimationType();
		if(type==Animation.realdouble){
			switch (pageIndex) {
				case current:
					break;
				case curright:
					break;
				case previous: {
					final ZLTextPage swap1 = myPreviousPage;
					final ZLTextPage swap2 = myNextPage;
					myPreviousPage = myCurrentPage;
					myNextPage = myCurrentRightPage;
					myCurrentPage = swap2;
					myCurrentRightPage = swap1;
					
					if (myCurrentRightPage.PaintState == PaintStateEnum.NOTHING_TO_PAINT) {
//						Utils.printLogError(TAG, "onScrollingFinished previous myCurrentRightPage Nothing to paint, prepare myPreviousPage:"+myPreviousPage);
						preparePaintInfo(myPreviousPage);
						myCurrentRightPage.EndCursor.setCursor(myPreviousPage.StartCursor);
						myCurrentRightPage.PaintState = PaintStateEnum.END_IS_KNOWN;
//						Utils.printLogError(TAG, "onScrollingFinished previous after prepare myCurrentRightPage:"+myCurrentRightPage);
					}
					if (myCurrentPage.PaintState == PaintStateEnum.NOTHING_TO_PAINT||myCurrentPage.EndCursor.getParagraphIndex()==0) {
//						Utils.printLogInfo(TAG, "onScrollingFinished myCurrentPage no content, myCurrentRightPage :"+myCurrentRightPage);
						preparePaintInfo(myCurrentRightPage);
						myCurrentPage.EndCursor.setCursor(myCurrentRightPage.StartCursor);
						myCurrentPage.PaintState = PaintStateEnum.END_IS_KNOWN;
//						Utils.printLogInfo(TAG, "onScrollingFinished set myCurrentPage.EndCursor :"+myCurrentPage.EndCursor);
					}
					myPreviousPage.reset();
					myNextPage.reset();
					break;
				}
				case next: {
					final ZLTextPage swap1 = myPreviousPage;
					final ZLTextPage swap2 = myNextPage;
					myNextPage = myCurrentPage;
					myPreviousPage = myCurrentRightPage;
					myCurrentPage = swap1;
					myCurrentRightPage = swap2;
					if (myCurrentPage.PaintState == PaintStateEnum.NOTHING_TO_PAINT) {
//						Utils.printLogError(TAG, "onScrollingFinished next myCurrentPage Nothing to paint, prepare myPreviousPage:"+myPreviousPage);
						preparePaintInfo(myPreviousPage);
						myCurrentPage.StartCursor.setCursor(myPreviousPage.EndCursor);
						myCurrentPage.PaintState = PaintStateEnum.START_IS_KNOWN;
//						Utils.printLogError(TAG, "onScrollingFinished next after prepare myCurrentPage:"+myCurrentPage);
					}
					if (myCurrentRightPage.PaintState == PaintStateEnum.NOTHING_TO_PAINT||
							myCurrentRightPage.StartCursor.getParagraphIndex()==0) {
//						Utils.printLogInfo(TAG, "onScrollingFinished myCurrentRightPage no content, myCurrentPage :"+myCurrentPage);
						preparePaintInfo(myCurrentPage);
						myCurrentRightPage.StartCursor.setCursor(myCurrentPage.EndCursor);
						myCurrentRightPage.PaintState = PaintStateEnum.START_IS_KNOWN;
//						Utils.printLogInfo(TAG, "onScrollingFinished set myCurrentRightPage.StartCursor :"+myCurrentRightPage.StartCursor);
					}
//					Utils.printLogInfo(TAG, "onScrollingFinished before reset myCurrentPage:"+myCurrentPage);
//					Utils.printLogError(TAG, "onScrollingFinished before reset myCurrentRightPage:"+myCurrentRightPage);
					myPreviousPage.reset();
					myNextPage.reset();
//					Utils.printLogInfo(TAG, "onScrollingFinished after reset myCurrentPage:"+myCurrentPage);
//					Utils.printLogError(TAG, "onScrollingFinished after reset myCurrentRightPage:"+myCurrentRightPage);
					break;
				}
			}
		}else{
			switch (pageIndex) {
				case current:
					break;
				case previous: {
					final ZLTextPage swap = myNextPage;
					myNextPage = myCurrentPage;
					myCurrentPage = myPreviousPage;
					myPreviousPage = swap;
					myPreviousPage.reset();
					if (myCurrentPage.PaintState == PaintStateEnum.NOTHING_TO_PAINT) {
						preparePaintInfo(myNextPage);
						myCurrentPage.EndCursor.setCursor(myNextPage.StartCursor);
						myCurrentPage.PaintState = PaintStateEnum.END_IS_KNOWN;
					} else if (!myCurrentPage.EndCursor.isNull() &&
							   !myNextPage.StartCursor.isNull() &&
							   !myCurrentPage.EndCursor.samePositionAs(myNextPage.StartCursor)) {
						myNextPage.reset();
						myNextPage.StartCursor.setCursor(myCurrentPage.EndCursor);
						myNextPage.PaintState = PaintStateEnum.START_IS_KNOWN;
						Application.getViewWidget().reset();
					}
					break;
				}
				case next: {
					final ZLTextPage swap = myPreviousPage;
					myPreviousPage = myCurrentPage;
					myCurrentPage = myNextPage;
					myNextPage = swap;
					myNextPage.reset();
					switch (myCurrentPage.PaintState) {
						case PaintStateEnum.NOTHING_TO_PAINT:
							preparePaintInfo(myPreviousPage);
							myCurrentPage.StartCursor.setCursor(myPreviousPage.EndCursor);
							myCurrentPage.PaintState = PaintStateEnum.START_IS_KNOWN;
							break;
						case PaintStateEnum.READY:
							myNextPage.StartCursor.setCursor(myCurrentPage.EndCursor);
							myNextPage.PaintState = PaintStateEnum.START_IS_KNOWN;
							break;
					}
					break;
				}
			}
		}
		setAudioSpeekPosition();
	}

	private void setAudioSpeekPosition(){
		if(!AudioPlayAction.isAudioSpeek){
			if(((DbStarPlayerApp)DbStarPlayerApp.Instance()).mCursorAudioSpeek==null){
				((DbStarPlayerApp)DbStarPlayerApp.Instance()).mCursorAudioSpeek =
						new ZLTextWordCursor(myCurrentPage.StartCursor);
			}else{
				((DbStarPlayerApp)DbStarPlayerApp.Instance()).mCursorAudioSpeek.
				setCursor(myCurrentPage.StartCursor);
			}
		}
	}

	public boolean removeHighlightings(Class<? extends ZLTextHighlighting> type) {
		boolean result = false;
		synchronized (myHighlightings) {
			for (Iterator<ZLTextHighlighting> it = myHighlightings.iterator(); it.hasNext(); ) {
				final ZLTextHighlighting h = it.next();
				if (type.isInstance(h)) {
					it.remove();
					result = true;
				}
			}
		}
		return result;
	}

	public void highlight(ZLTextPosition start, ZLTextPosition end) {
		removeHighlightings(ZLTextManualHighlighting.class);
		addHighlighting(new ZLTextManualHighlighting(this, start, end));
	}

	public final void addHighlighting(ZLTextHighlighting h) {
		myHighlightings.add(h);
		refreshReadPage();
	}

	public final void addHighlightings(Collection<ZLTextHighlighting> hilites) {
		myHighlightings.addAll(hilites);
		refreshReadPage();
	}

	public void clearHighlighting() {
		if (removeHighlightings(ZLTextManualHighlighting.class)) {
			refreshReadPage();
		}
	}

	protected void moveSelectionCursorTo(ZLTextSelectionCursor cursor, int x, int y) {
		y -= ZLTextSelectionCursor.getHeight() / 2 + ZLTextSelectionCursor.getAccent() / 2;
		mySelection.setCursorInMovement(cursor, x, y);
		mySelection.expandTo(myCurrentPage, x, y);
		refreshReadPage();
	}

	protected void releaseSelectionCursor() {
		mySelection.stop();
		refreshReadPage();
	}

	protected ZLTextSelectionCursor getSelectionCursorInMovement() {
		return mySelection.getCursorInMovement();
	}

	private ZLTextSelection.Point getSelectionCursorPoint(ZLTextPage page, ZLTextSelectionCursor cursor) {
		if (cursor == ZLTextSelectionCursor.None) {
			return null;
		}

		if (cursor == mySelection.getCursorInMovement()) {
			return mySelection.getCursorInMovementPoint();
		}

		if (cursor == ZLTextSelectionCursor.Left) {
			if (mySelection.hasPartBeforePage(page)) {
				return null;
			}
			final ZLTextElementArea selectionStartArea = mySelection.getStartArea(page);
			if (selectionStartArea != null) {
				return new ZLTextSelection.Point(selectionStartArea.XStart, selectionStartArea.YEnd);
			}
		} else {
			if (mySelection.hasPartAfterPage(page)) {
				return null;
			}
			final ZLTextElementArea selectionEndArea = mySelection.getEndArea(page);
			if (selectionEndArea != null) {
				return new ZLTextSelection.Point(selectionEndArea.XEnd, selectionEndArea.YEnd);
			}
		}
		return null;
	}

	private int distanceToCursor(int x, int y, ZLTextSelection.Point cursorPoint) {
		if (cursorPoint == null) {
			return Integer.MAX_VALUE;
		}

		final int dX, dY;

		final int w = ZLTextSelectionCursor.getWidth() / 2;
		if (x < cursorPoint.X - w) {
			dX = cursorPoint.X - w - x;
		} else if (x > cursorPoint.X + w) {
			dX = x - cursorPoint.X - w;
		} else {
			dX = 0;
		}

		final int h = ZLTextSelectionCursor.getHeight();
		if (y < cursorPoint.Y) {
			dY = cursorPoint.Y - y;
		} else if (y > cursorPoint.Y + h) {
			dY = y - cursorPoint.Y - h;
		} else {
			dY = 0;
		}

		return Math.max(dX, dY);
	}

	protected ZLTextSelectionCursor findSelectionCursor(int x, int y) {
		return findSelectionCursor(x, y, Integer.MAX_VALUE);
	}

	protected ZLTextSelectionCursor findSelectionCursor(int x, int y, int maxDistance) {
		if (mySelection.isEmpty()) {
			return ZLTextSelectionCursor.None;
		}

		final int leftDistance = distanceToCursor(
			x, y, getSelectionCursorPoint(myCurrentPage, ZLTextSelectionCursor.Left)
		);
		final int rightDistance = distanceToCursor(
			x, y, getSelectionCursorPoint(myCurrentPage, ZLTextSelectionCursor.Right)
		);

		if (rightDistance < leftDistance) {
			return rightDistance <= maxDistance ? ZLTextSelectionCursor.Right : ZLTextSelectionCursor.None;
		} else {
			return leftDistance <= maxDistance ? ZLTextSelectionCursor.Left : ZLTextSelectionCursor.None;
		}
	}

	private void drawSelectionCursor(ZLPaintContext context, ZLTextSelection.Point pt) {
		if (pt == null) {
			return;
		}

		final int w = ZLTextSelectionCursor.getWidth() / 2;
		final int h = ZLTextSelectionCursor.getHeight();
		final int a = ZLTextSelectionCursor.getAccent();
		final int[] xs = { pt.X, pt.X + w, pt.X + w, pt.X - w, pt.X - w };
		final int[] ys = { pt.Y - a, pt.Y, pt.Y + h, pt.Y + h, pt.Y };
		context.setFillColor(context.getBackgroundColor(), 192);
		context.fillPolygon(xs, ys);
		context.setLineColor(getTextColor(ZLTextHyperlink.NO_LINK));
		context.drawPolygonalLine(xs, ys);
	}

	@Override
	public synchronized void preparePage(ZLPaintContext context, PageIndex pageIndex) {
		setContext(context);
		preparePaintInfo(getPage(pageIndex));
	}

	@Override
	public synchronized void paint(ZLPaintContext context, PageIndex pageIndex, EdgePosition edgepos,ZLView.PageIndex progress) {
		setContext(context);
//		final ZLFile wallpaper = getWallpaperFile();
//		if (wallpaper != null) {
//			context.clear(wallpaper, getWallpaperMode());
//		} else {
//			context.clear(getBackgroundColor());
//		}
		//画图片
		drawBookBackground(context,edgepos);
		
		if (myModel == null || myModel.getParagraphsNumber() == 0) {
			return;
		}

		ZLTextPage page;
		final Animation type = ZLApplication.Instance().getViewWidget().getCurAnimationType();
		if(type==Animation.realdouble){
			final ZLAndroidWidget widget = ((ZLAndroidLibrary)ZLAndroidLibrary.Instance()).getWidget();
			final AnimationProvider animator = widget.getAnimationProvider();
			Log.d(TAG, "pageIndex = (" + pageIndex + ")");
			switch (pageIndex) {
			default:
			case current:
				page = myCurrentPage;
				Utils.printLogError(TAG, "paint called get myCurrentPage :"+myCurrentPage);
				if (myCurrentPage.PaintState == PaintStateEnum.NOTHING_TO_PAINT&&
						myCurrentRightPage.PaintState!=PaintStateEnum.NOTHING_TO_PAINT) {
					Utils.printLogInfo(TAG, "myCurrentPage no content, myCurrentRightPage :"+myCurrentRightPage);
					preparePaintInfo(myCurrentRightPage);
					if(myCurrentRightPage.PaintState!=PaintStateEnum.NOTHING_TO_PAINT){
						myCurrentPage.EndCursor.setCursor(myCurrentRightPage.StartCursor);
						myCurrentPage.PaintState = PaintStateEnum.END_IS_KNOWN;
						Utils.printLogInfo(TAG, "set myCurrentPage.EndCursor :"+myCurrentPage.EndCursor);
					}
				}
				break;
			case curright:
				page = myCurrentRightPage;
				Utils.printLogError(TAG, "paint called get myCurrentRightPage :"+myCurrentRightPage);
				if (myCurrentRightPage.PaintState == PaintStateEnum.NOTHING_TO_PAINT&&
						myCurrentPage.PaintState!=PaintStateEnum.NOTHING_TO_PAINT) {
					Utils.printLogInfo(TAG, "myCurrentRightPage no content, myCurrentPage :"+myCurrentPage);
					preparePaintInfo(myCurrentPage);
					if(myCurrentPage.PaintState!=PaintStateEnum.NOTHING_TO_PAINT){
						myCurrentRightPage.StartCursor.setCursor(myCurrentPage.EndCursor);
						myCurrentRightPage.PaintState = PaintStateEnum.START_IS_KNOWN;
						Utils.printLogInfo(TAG, "set myCurrentRightPage.StartCursor :"+myCurrentRightPage.StartCursor);
					}
				}
				break;
			case previous:
				page = myPreviousPage;
				Utils.printLogError(TAG, "paint called get myPreviousPage :"+myPreviousPage);
				if (myPreviousPage.PaintState == PaintStateEnum.NOTHING_TO_PAINT) {
					if(animator.getPageToScrollTo()==PageIndex.next){//后翻页
						Utils.printLogInfo(TAG, "no content,to next, myCurrentRightPage :"+myCurrentRightPage);
						preparePaintInfo(myCurrentRightPage);
						myPreviousPage.StartCursor.setCursor(myCurrentRightPage.EndCursor);
						myPreviousPage.PaintState = PaintStateEnum.START_IS_KNOWN;
						Utils.printLogInfo(TAG, "set myPreviousPage.StartCursor :"+myPreviousPage.StartCursor);
					}else if(animator.getPageToScrollTo()==PageIndex.previous){
						Utils.printLogInfo(TAG, "no content,to previous myCurrentPage :"+myCurrentPage);
						preparePaintInfo(myCurrentPage);
						myPreviousPage.EndCursor.setCursor(myCurrentPage.StartCursor);
						myPreviousPage.PaintState = PaintStateEnum.END_IS_KNOWN;
						Utils.printLogInfo(TAG, "set myPreviousPage.EndCursor :"+myPreviousPage.EndCursor);
					}
				}
				break;
			case next:
				page = myNextPage;
				Utils.printLogError(TAG, "paint called get myNextPage :"+myNextPage);
				if (myNextPage.PaintState == PaintStateEnum.NOTHING_TO_PAINT) {
					if(animator.getPageToScrollTo()==PageIndex.next){//后翻页
						Utils.printLogInfo(TAG, "no content,to next, myPreviousPage :"+myPreviousPage+", PaintStateEnum:"+myPreviousPage.PaintState);
						if(myPreviousPage.PaintState==PaintStateEnum.NOTHING_TO_PAINT&&
								myCurrentRightPage.PaintState!=PaintStateEnum.NOTHING_TO_PAINT){
							Utils.printLogError(TAG, "no content,to next, myPreviousPage has no content too, find it from myCurrentRightPage:"+myCurrentRightPage);
							preparePaintInfo(myCurrentRightPage);
							myPreviousPage.StartCursor.setCursor(myCurrentRightPage.EndCursor);
							myPreviousPage.PaintState=PaintStateEnum.START_IS_KNOWN;
						}
						preparePaintInfo(myPreviousPage);
						myNextPage.StartCursor.setCursor(myPreviousPage.EndCursor);
						myNextPage.PaintState = PaintStateEnum.START_IS_KNOWN;
						Utils.printLogInfo(TAG, "set myNextPage.StartCursor :"+myNextPage.StartCursor);
					}else if(animator.getPageToScrollTo()==PageIndex.previous){//回翻
						Utils.printLogInfo(TAG, "no content,to previous, myPreviousPage :"+myPreviousPage+", PaintStateEnum:"+myPreviousPage.PaintState);
						if(myPreviousPage.PaintState==PaintStateEnum.NOTHING_TO_PAINT&&
								myCurrentPage.PaintState!=PaintStateEnum.NOTHING_TO_PAINT){
							Utils.printLogError(TAG, "no content,to previous, myPreviousPage has no content too, find it from myCurrentPage:"+myCurrentPage);
							preparePaintInfo(myCurrentPage);
							myPreviousPage.EndCursor.setCursor(myCurrentPage.StartCursor);
							myPreviousPage.PaintState=PaintStateEnum.END_IS_KNOWN;
						}
						preparePaintInfo(myPreviousPage);
						myNextPage.EndCursor.setCursor(myPreviousPage.StartCursor);
						myNextPage.PaintState = PaintStateEnum.END_IS_KNOWN;
						Utils.printLogInfo(TAG, "set myNextPage.EndCursor :"+myNextPage.EndCursor);
					}
				}
				break;
			}
		}else{
			switch (pageIndex) {
				default:
				case current:
					page = myCurrentPage;
					break;
				case previous:
					page = myPreviousPage;
					if (myPreviousPage.PaintState == PaintStateEnum.NOTHING_TO_PAINT) {
						preparePaintInfo(myCurrentPage);
						myPreviousPage.EndCursor.setCursor(myCurrentPage.StartCursor);
						myPreviousPage.PaintState = PaintStateEnum.END_IS_KNOWN;
					}
					break;
				case next:
					page = myNextPage;
					if (myNextPage.PaintState == PaintStateEnum.NOTHING_TO_PAINT) {
						preparePaintInfo(myCurrentPage);
						myNextPage.StartCursor.setCursor(myCurrentPage.EndCursor);
						myNextPage.PaintState = PaintStateEnum.START_IS_KNOWN;
					}
			}
		}
		page.TextElementMap.clear();

		preparePaintInfo(page);

		if (page.StartCursor.isNull() || page.EndCursor.isNull()) {
			return;
		}

		final ArrayList<ZLTextLineInfo> lineInfos = page.LineInfos;
		final int[] labels = new int[lineInfos.size() + 1];
		int x = getLeftMargin();
		int y = getTopMargin();
		int index = 0;
		for(ImageEntity entity:page.ImageLists){
			entity.setOverHeight(true);
		}
		for (ZLTextLineInfo info : lineInfos) {
			prepareTextLine(page, info, x, y);
			y += info.Height + info.Descent + info.VSpaceAfter;
			final ImageEntity preImage = page.getPreviousImage();
			final ImageEntity curImage = page.getCurrentImage();
			if(curImage!=null&&curImage.lineInfo!=null&&curImage.lineInfo.ParagraphCursor!=null){
				if(curImage.lineInfo.RealStartElementIndex==info.RealStartElementIndex&&
						curImage.lineInfo.ParagraphCursor.Index==info.ParagraphCursor.Index){
					if(preImage!=null&&preImage.isReduceTextLineWidth()){
						preImage.setOverHeight(true);
						y+=(preImage.getLineHeight()+preImage.lineInfo.Descent-preImage.getCurTextHeight());
					}
				}else{
					if(curImage.isReduceTextLineWidth()&&info.isImageLine){
						curImage.setOverHeight(true);
						y+=(curImage.getLineHeight()+curImage.lineInfo.Descent-curImage.getCurTextHeight());
					}
				}
			}
			labels[++index] = page.TextElementMap.size();
		}

		x = getLeftMargin();
		y = getTopMargin();
		index = 0;
		final Bookmark bookmark = page.getBookMark();
		ZLTextFixedPosition pos = null;
		if(bookmark!=null){
			pos = new ZLTextFixedPosition(bookmark.ParagraphIndex, bookmark.ElementIndex, bookmark.CharIndex);
			if(bookmark.myEnd.compareTo(page.StartCursor)<0||pos.compareTo(page.EndCursor)>0){
				page.setBookMark(null);
			}
		}
		for (ZLTextLineInfo info : lineInfos) {
			drawHighlightings(page, edgepos, info, labels[index], labels[index + 1], x, y);
			y += info.Height + info.Descent + info.VSpaceAfter;
			++index;
		}

		if(edgepos==EdgePosition.EDGE_RIGHT){
			if(myRealBookmarkPage!=null&&myRealBookmarkPage.EndCursor.getParagraphIndex()==page.StartCursor.getParagraphIndex()&&
					myRealBookmarkPage.EndCursor.getElementIndex()==page.StartCursor.getElementIndex()){
				if(myRealBookmarkPage.getBookMark()!=null){
//					if(!myRealBookmarkPage.isReleaseBookMark()){
						drawBookMark(context,myRealBookmarkPage, true);
//					}
				}
//				else if(myRealBookmarkPage.isReleaseBookMark()){
//					drawBookMark(context, myRealBookmarkPage,false);
//				}
			}else{
				if(page.getBookMark()!=null){
//					if(!page.isReleaseBookMark()){
						drawBookMark(context,page, true);
//					}
				}
//				else if(page.isReleaseBookMark()){
//					drawBookMark(context, page,false);
//				}
			}
		}
		
		if(isShowVolumeStatus){
			if(type==Animation.realdouble){
				if(pageIndex==PageIndex.curright){
					drawVolume(context);
				}
			}else if(pageIndex==PageIndex.current){
				drawVolume(context);
			}
		}
		
		x = getLeftMargin();
		y = getTopMargin();
		index = 0;
		for(ImageEntity entity:page.ImageLists){
			entity.setOverHeight(true);
		}
		for (ZLTextLineInfo info : lineInfos) {
			drawTextLine(page, info, labels[index], labels[index + 1]);
//			y += info.Height + info.Descent + info.VSpaceAfter;
			++index;
		}

		final ZLTextRegion selectedElementRegion = getSelectedRegion(page);
		if (selectedElementRegion != null && myHighlightSelectedRegion) {
			selectedElementRegion.draw(context);
		}

		drawSelectionCursor(context, getSelectionCursorPoint(page, ZLTextSelectionCursor.Left));
		drawSelectionCursor(context, getSelectionCursorPoint(page, ZLTextSelectionCursor.Right));
		final int curPage = computeTextPageNumber(getCurrentCharNumber(progress, false));
		final int totalPage = computeTextPageNumber(sizeOfFullText());
		final int curProgress = curPage*100/totalPage;
		drawBookFooter(context,curPage,totalPage,edgepos);
//		if(mReadProgressChangeListener!=null){
//			mReadProgressChangeListener.onReadProgressChange(curProgress);
//		}
		Message msg = mHandler.obtainMessage(MSG_READ_PROGRESS_CHANGED);
		msg.arg1 = curProgress;
		mHandler.sendMessage(msg);
	}
	
	private final int MSG_READ_PROGRESS_CHANGED = 0;
	Handler mHandler = new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
				case MSG_READ_PROGRESS_CHANGED:
					if(mReadProgressChangeListener!=null){
						mReadProgressChangeListener.onReadProgressChange(msg.arg1);
					}
					break;
			}
		}
	};
	/**
	 *  绘制图片
	 * @param isDrawEdge
	 */
	public void drawBookBackground(ZLPaintContext context, EdgePosition edgepos){
		if(null!=context&&context instanceof ZLAndroidPaintContext)
			((ZLAndroidPaintContext)context).drawBackground(edgepos);
	}
	
	/**
	 * animation of  the book mark added or released
	 * */
	private void drawBookMark(ZLPaintContext context, ZLTextPage page, boolean isAdd){
		getBookMarkBitmap();
		final Rect src = new Rect(0,mBookMarkTransparentHeight,
				mBookMarkImageWidth,mBookMarkImageHeight);
		final Rect dst = new Rect(0,0,mBookMarkImageWidth,mBookMarkImageHeight-mBookMarkTransparentHeight);
		context.drawImage(src, dst, mBookMarkBitmap);
//		if(isAdd){
//			if(mBookMarkDrawingHeight+mBookMarKDrawingHeightStep+mBookMarkTransparentHeight<mBookMarkImageHeight){
//				mBookMarkDrawingHeight+=mBookMarKDrawingHeightStep;
//			}else{
//				mBookMarkDrawingHeight = mBookMarkImageHeight-mBookMarkTransparentHeight;
//				isDrawBookmarkAnitmation =false;
//				if(myRealBookmarkPage!=null){
//					myRealBookmarkPage = null;
//				}
//			}
//		}else{
//			if(mBookMarkDrawingHeight-mBookMarKDrawingHeightStep>0){
//				mBookMarkDrawingHeight-=mBookMarKDrawingHeightStep;
//			}else{
//				mBookMarkDrawingHeight = 0;
//				isDrawBookmarkAnitmation = false;
//				page.setReleaseBookMark(false);
//				page.setBookMark(null);
//				if(myRealBookmarkPage!=null){
//					myRealBookmarkPage = null;
//				}
//			}
//		}
//		final Rect src = new Rect(0,mBookMarkImageHeight-mBookMarkDrawingHeight,
//				mBookMarkImageWidth,mBookMarkImageHeight);
//		final Rect dst = new Rect(0,0,mBookMarkImageWidth,mBookMarkDrawingHeight);
//		context.drawImage(src, dst, mBookMarkBitmap);
//		if((isAdd&&mBookMarkDrawingHeight!=mBookMarkImageHeight-mBookMarkTransparentHeight)||
//				(!isAdd&&mBookMarkDrawingHeight!=0)){
//			if(isDrawBookmarkAnitmation){
//				refreshReadPage();
//			}
//		}
	}
	
	private AudioManager getAudioManager(){
		if(null==mAudioManager){
			mAudioManager = (AudioManager) ((ZLAndroidLibrary)ZLAndroidLibrary.
					Instance()).getWidget().getContext().getSystemService(Service.AUDIO_SERVICE);
		}
		return mAudioManager;
	}
	
	private void drawVolume(ZLPaintContext context){
		getAudioManager();
		if(mHideVolumeStatusRunnable!=null){
			((ZLAndroidLibrary)ZLAndroidLibrary.
			Instance()).getWidget().removeCallbacks(mHideVolumeStatusRunnable);
			mHideVolumeStatusRunnable = null;
		}
		int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		mCurAudioLevel = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		
//		max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
//		cur = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
//		
//		max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
//		cur = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
//		
//		max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
//		cur = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
		
		if(0==mCurAudioLevel){
			//mute
			checkCurAudioLevelImageByResID(R.drawable.reader_read_volume_mute);
			context.drawImage(context.getWidth()-getRightMargin(), getTopMargin(), mAudioLevelBitmap);
		}else{
			switch(mCurAudioLevel*10/max){
				case 0:
					checkCurAudioLevelImageByResID(R.drawable.reader_read_volume_1);
					break;
				case 1:
					checkCurAudioLevelImageByResID(R.drawable.reader_read_volume_2);
					break;
				case 2:
					checkCurAudioLevelImageByResID(R.drawable.reader_read_volume_3);
					break;
				case 3:
					checkCurAudioLevelImageByResID(R.drawable.reader_read_volume_4);
					break;
				case 4:
					checkCurAudioLevelImageByResID(R.drawable.reader_read_volume_5);
					break;
				case 5:
					checkCurAudioLevelImageByResID(R.drawable.reader_read_volume_6);
					break;
				case 6:
					checkCurAudioLevelImageByResID(R.drawable.reader_read_volume_7);
					break;
				case 7:
					checkCurAudioLevelImageByResID(R.drawable.reader_read_volume_8);
					break;
				case 8:
					checkCurAudioLevelImageByResID(R.drawable.reader_read_volume_9);
					break;
				case 9:
					checkCurAudioLevelImageByResID(R.drawable.reader_read_volume_10);
					break;
				default:
					break;
			}
			context.drawImage(context.getWidth()-getRightMargin(), getTopMargin(), getAudioBackBitmap());
			context.drawImage(context.getWidth()-getRightMargin(), getTopMargin(), mAudioLevelBitmap);
		}
		postHideVolumeStatusRunnable();
	}

	private void checkCurAudioLevelImageByResID(int desireID){
		if(mCurAudioLevelImageResID!=desireID){
			recycleBitmapByResID(mCurAudioLevelImageResID);
			mCurAudioLevelImageResID = desireID;
			mAudioLevelBitmap = ImageManager.getInstance().loadBitmapByResourceId(
					((ZLAndroidLibrary)ZLAndroidLibrary.Instance()).getWidget().getContext(), desireID);
		}
	}
	
	private class HideVolumeStatusRunnable implements Runnable {
		public void run() {
			if (isShowVolumeStatus) {
				if(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)==mCurAudioLevel){
					isShowVolumeStatus = false;
					refreshReadPage();
				}else{
					//audio level changed again, so not reset it
				}
			}
		}
	}
	
	
	private void postHideVolumeStatusRunnable() {
		if (mHideVolumeStatusRunnable == null) {
			mHideVolumeStatusRunnable = new HideVolumeStatusRunnable();
		}
		((ZLAndroidLibrary)ZLAndroidLibrary.Instance()).getWidget().
			postDelayed(mHideVolumeStatusRunnable, VOLUME_DISMISS_TIME);
	}
	/**
	 *   draw book footer
	 * @param curPage
	 */
	public void drawBookFooter(ZLPaintContext context, int curPage, int totalPage, EdgePosition edgepos){
			
		if(null!=context&&context instanceof ZLAndroidPaintContext)
				((ZLAndroidPaintContext)context).drawBookFooter(curPage,totalPage,edgepos);
	}
	
	private ZLTextPage getPage(PageIndex pageIndex) {
		switch (pageIndex) {
			default:
			case current:
				return myCurrentPage;
			case previous:
				return myPreviousPage;
			case next:
				return myNextPage;
			case curright:
				return myCurrentRightPage;
		}
	}

	public static final int SCROLLBAR_HIDE = 0;
	public static final int SCROLLBAR_SHOW = 1;
	public static final int SCROLLBAR_SHOW_AS_PROGRESS = 2;

	public abstract int scrollbarType();

	@Override
	public final boolean isScrollbarShown() {
		return scrollbarType() == SCROLLBAR_SHOW || scrollbarType() == SCROLLBAR_SHOW_AS_PROGRESS;
	}

	protected final synchronized int sizeOfTextBeforeParagraph(int paragraphIndex) {
		return myModel != null ? myModel.getTextLength(paragraphIndex - 1) : 0;
	}

	protected final synchronized int sizeOfFullText() {
		if (myModel == null || myModel.getParagraphsNumber() == 0) {
			return 1;
		}
		return myModel.getTextLength(myModel.getParagraphsNumber() - 1);
	}

	private final synchronized int getCurrentCharNumber(PageIndex pageIndex, boolean startNotEndOfPage) {
		if (myModel == null || myModel.getParagraphsNumber() == 0) {
			return 0;
		}
		final ZLTextPage page = getPage(pageIndex);
		preparePaintInfo(page);
		if (startNotEndOfPage) {
			return Math.max(0, sizeOfTextBeforeCursor(page.StartCursor));
		} else {
			int end = sizeOfTextBeforeCursor(page.EndCursor);
			if (end == -1) {
				end = myModel.getTextLength(myModel.getParagraphsNumber() - 1) - 1;
			}
			return Math.max(1, end);
		}
	}

	@Override
	public final synchronized int getScrollbarFullSize() {
		return sizeOfFullText();
	}

	@Override
	public final synchronized int getScrollbarThumbPosition(PageIndex pageIndex) {
		return scrollbarType() == SCROLLBAR_SHOW_AS_PROGRESS ? 0 : getCurrentCharNumber(pageIndex, true);
	}

	@Override
	public final synchronized int getScrollbarThumbLength(PageIndex pageIndex) {
		int start = scrollbarType() == SCROLLBAR_SHOW_AS_PROGRESS
			? 0 : getCurrentCharNumber(pageIndex, true);
		int end = getCurrentCharNumber(pageIndex, false);
		return Math.max(1, end - start);
	}

	private int sizeOfTextBeforeCursor(ZLTextWordCursor wordCursor) {
		final ZLTextParagraphCursor paragraphCursor = wordCursor.getParagraphCursor();
		if (paragraphCursor == null) {
			return -1;
		}
		final int paragraphIndex = paragraphCursor.Index;
		int sizeOfText = myModel.getTextLength(paragraphIndex - 1);
		final int paragraphLength = paragraphCursor.getParagraphLength();
		if (paragraphLength > 0) {
			sizeOfText +=
				(myModel.getTextLength(paragraphIndex) - sizeOfText)
				* wordCursor.getElementIndex()
				/ paragraphLength;
		}
		return sizeOfText;
	}

	// Can be called only when (myModel.getParagraphsNumber() != 0)
	private synchronized float computeCharsPerPage() {
		setTextStyle(ZLTextStyleCollection.Instance().getBaseStyle());

		final int textWidth = getTextAreaWidth();//getTextColumnWidth();
		final int textHeight = getTextAreaHeight();

		final int num = myModel.getParagraphsNumber();
		final int totalTextSize = myModel.getTextLength(num - 1);
		final float charsPerParagraph = ((float)totalTextSize) / num;

		final float charWidth = computeCharWidth();

		final int indentWidth = getElementWidth(ZLTextElement.Indent, 0);
		final float effectiveWidth = textWidth - (indentWidth + 0.5f * textWidth) / charsPerParagraph;
		float charsPerLine = Math.min(effectiveWidth / charWidth,
				charsPerParagraph * 1.2f);

		final int strHeight = getWordHeight() + getContext().getDescent();
		final int effectiveHeight = (int) (textHeight - (getTextStyle().getSpaceBefore()
				+ getTextStyle().getSpaceAfter()) / charsPerParagraph);
		final int linesPerPage = effectiveHeight / strHeight;

		return charsPerLine * linesPerPage;
	}

	private synchronized int computeTextPageNumber(int textSize) {
		if (myModel == null || myModel.getParagraphsNumber() == 0) {
			return 1;
		}

		final float factor = 1.0f / computeCharsPerPage();
		final float pages = textSize * factor;
		return Math.max((int)(pages + 1.0f - 0.5f * factor), 1);
	}

	private static final char[] ourDefaultLetters = "System developers have used modeling languages for decades to specify, visualize, construct, and document systems. The Unified Modeling Language (UML) is one of those languages. UML makes it possible for team members to collaborate by providing a common language that applies to a multitude of different systems. Essentially, it enables you to communicate solutions in a consistent, tool-supported language.".toCharArray();

	private final char[] myLettersBuffer = new char[512];
	private int myLettersBufferLength = 0;
	private ZLTextModel myLettersModel = null;
	private float myCharWidth = -1f;

	private final float computeCharWidth() {
		if (myLettersModel != myModel) {
			myLettersModel = myModel;
			myLettersBufferLength = 0;
			myCharWidth = -1f;

			int paragraph = 0;
			final int textSize = myModel.getTextLength(myModel.getParagraphsNumber() - 1);
			if (textSize > myLettersBuffer.length) {
				paragraph = myModel.findParagraphByTextLength((textSize - myLettersBuffer.length) / 2);
			}
			while (paragraph < myModel.getParagraphsNumber()
					&& myLettersBufferLength < myLettersBuffer.length) {
				ZLTextParagraph.EntryIterator it = myModel.getParagraph(paragraph++).iterator();
				while (it.hasNext()
						&& myLettersBufferLength < myLettersBuffer.length) {
					it.next();
					if (it.getType() == ZLTextParagraph.Entry.TEXT) {
						final int len = Math.min(it.getTextLength(),
								myLettersBuffer.length - myLettersBufferLength);
						System.arraycopy(it.getTextData(), it.getTextOffset(),
								myLettersBuffer, myLettersBufferLength, len);
						myLettersBufferLength += len;
					}
				}
			}

			if (myLettersBufferLength == 0) {
				myLettersBufferLength = Math.min(myLettersBuffer.length, ourDefaultLetters.length);
				System.arraycopy(ourDefaultLetters, 0, myLettersBuffer, 0, myLettersBufferLength);
			}
		}

		if (myCharWidth < 0f) {
			myCharWidth = computeCharWidth(myLettersBuffer, myLettersBufferLength);
		}
		return myCharWidth;
	}

	private final float computeCharWidth(char[] pattern, int length) {
		return getContext().getStringWidth(pattern, 0, length) / ((float)length);
	}

	public static class PagePosition {
		public final int Current;
		public final int Total;

		PagePosition(int current, int total) {
			Current = current;
			Total = total;
		}
	}

	public final synchronized PagePosition pagePosition() {
		int current = computeTextPageNumber(getCurrentCharNumber(PageIndex.current, false));
		int total = computeTextPageNumber(sizeOfFullText());

		if (total > 3) {
			return new PagePosition(current, total);
		}

		preparePaintInfo(myCurrentPage);
		ZLTextWordCursor cursor = myCurrentPage.StartCursor;
		if (cursor == null || cursor.isNull()) {
			return new PagePosition(current, total);
		}

		if (cursor.isStartOfText()) {
			current = 1;
		} else {
			ZLTextWordCursor prevCursor = myPreviousPage.StartCursor;
			if (prevCursor == null || prevCursor.isNull()) {
				preparePaintInfo(myPreviousPage);
				prevCursor = myPreviousPage.StartCursor;
			}
			if (prevCursor != null && !prevCursor.isNull()) {
				current = prevCursor.isStartOfText() ? 2 : 3;
			}
		}

		total = current;
		cursor = myCurrentPage.EndCursor;
		if (cursor == null || cursor.isNull()) {
			return new PagePosition(current, total);
		}
		if (!cursor.isEndOfText()) {
			ZLTextWordCursor nextCursor = myNextPage.EndCursor;
			if (nextCursor == null || nextCursor.isNull()) {
				preparePaintInfo(myNextPage);
				nextCursor = myNextPage.EndCursor;
			}
			if (nextCursor != null) {
				total += nextCursor.isEndOfText() ? 1 : 2;
			}
		}

		return new PagePosition(current, total);
	}

	public final synchronized void gotoPage(int page) {
		if (myModel == null || myModel.getParagraphsNumber() == 0) {
			return;
		}

		final float factor = computeCharsPerPage();
		final float textSize = page * factor;

		int intTextSize = (int) textSize;
		int paragraphIndex = myModel.findParagraphByTextLength(intTextSize);

		if (paragraphIndex > 0 && myModel.getTextLength(paragraphIndex) > intTextSize) {
			--paragraphIndex;
		}
		intTextSize = myModel.getTextLength(paragraphIndex);

		int sizeOfTextBefore = myModel.getTextLength(paragraphIndex - 1);
		while (paragraphIndex > 0 && intTextSize == sizeOfTextBefore) {
			--paragraphIndex;
			intTextSize = sizeOfTextBefore;
			sizeOfTextBefore = myModel.getTextLength(paragraphIndex - 1);
		}

		final int paragraphLength = intTextSize - sizeOfTextBefore;

		final int wordIndex;
		if (paragraphLength == 0) {
			wordIndex = 0;
		} else {
			preparePaintInfo(myCurrentPage);
			final ZLTextWordCursor cursor = new ZLTextWordCursor(myCurrentPage.EndCursor);
			cursor.moveToParagraph(paragraphIndex);
			if(!cursor.isNull())
				wordIndex = cursor.getParagraphCursor().getParagraphLength();
			else
				wordIndex = 0;
		}

		gotoPositionByEnd(paragraphIndex, wordIndex, 0);
	}

	public void gotoHome() {
		final ZLTextWordCursor cursor = getStartCursor();
		if (!cursor.isNull() && cursor.isStartOfParagraph() && cursor.getParagraphIndex() == 0) {
			return;
		}
		gotoPosition(0, 0, 0);
		preparePaintInfo();
		setAudioSpeekPosition();
	}

	private ZLTextPage myRealBookmarkPage = null;
	private void drawHighlightings(ZLTextPage page, EdgePosition edgePos, ZLTextLineInfo info, int from, int to, int x, int y) {
		if (from == to) {
			return;
		}

		final LinkedList<ZLTextHighlighting> hilites = new LinkedList<ZLTextHighlighting>();
		if (mySelection.intersects(page)) {
			hilites.add(mySelection);
		}
		synchronized (myHighlightings) {
			boolean isAddBookBark = false;
			for (ZLTextHighlighting h : myHighlightings) {
				if (h.intersects(page)) {
					if(h instanceof BookmarkHighlighting){
						if(h.getStartPosition().compareTo(page.StartCursor)>=0){
							if(page.getBookMark()==null){
								page.setBookMark(((BookmarkHighlighting)h).getHightlightingBookmark());
								if(edgePos==EdgePosition.EDGE_LEFT){
									myRealBookmarkPage = page;
								}
//								if(isDrawBookmarkAnitmation){
//									mBookMarkDrawingHeight=0;
//								}else{
//									mBookMarkDrawingHeight = mBookMarkImageHeight-mBookMarkTransparentHeight;
//								}
							}
							isAddBookBark = true;
						}
					}else{
						hilites.add(h);
					}
				}
			}
			
			if(page.getBookMark()!=null&&!isAddBookBark/*&&!page.isReleaseBookMark()*/){
				page.setBookMark(null);
//				page.setReleaseBookMark(true);
				if(edgePos==EdgePosition.EDGE_LEFT){
					myRealBookmarkPage = page;
				}
//				if(isDrawBookmarkAnitmation){
//					mBookMarkDrawingHeight = mBookMarkImageHeight-mBookMarkTransparentHeight;
//				}else{
//					mBookMarkDrawingHeight = 0;
//				}
			}
		}
		if (hilites.isEmpty()) {
			return;
		}
	
		final ZLTextElementArea fromArea = page.TextElementMap.get(from);
		final ZLTextElementArea toArea = page.TextElementMap.get(to - 1);
		for (ZLTextHighlighting h : hilites) {
			final ZLColor bgColor = h.getBackgroundColor();
			if (bgColor == null) {
				continue;
			}
			final ZLTextElementArea selectionStartArea = h.getStartArea(page);
			if (selectionStartArea == null || selectionStartArea.compareTo(toArea) > 0) {
				continue;
			}
			final ZLTextElementArea selectionEndArea = h.getEndArea(page);
			if (selectionEndArea == null || selectionEndArea.compareTo(fromArea) < 0) {
				continue;
			}

			final int top = y + 1;
			int left, right, bottom = y + info.Height + info.Descent;
			if (selectionStartArea.compareTo(fromArea) < 0) {
				left = x;
			} else {
				left = selectionStartArea.XStart;
			}
			if (selectionEndArea.compareTo(toArea) > 0) {
				right = x + page.getTextWidth() - 1;
				bottom += info.VSpaceAfter;
			} else {
				right = selectionEndArea.XEnd;
			}
			getContext().setFillColor(bgColor);
			getContext().fillRectangle(left, top, right, bottom);
		}
	}

	private static final char[] SPACE = new char[] { ' ' };
	private void drawTextLine(ZLTextPage page, ZLTextLineInfo info, int from, int to) {
		final ZLPaintContext context = getContext();
		final ZLTextParagraphCursor paragraph = info.ParagraphCursor;
		int index = from;
		final int endElementIndex = info.EndElementIndex;
		int charIndex = info.RealStartCharIndex;
		for (int wordIndex = info.RealStartElementIndex; wordIndex != endElementIndex && index < to; ++wordIndex, charIndex = 0) {
			final ZLTextElement element = paragraph.getElement(wordIndex);
			final ZLTextElementArea area = page.TextElementMap.get(index);
			if (element == area.Element) {
				++index;
				if (area.ChangeStyle) {
					setTextStyle(area.Style);
				}
				final int areaX = area.XStart;
				final int areaY = area.YEnd - getElementDescent(element) - getTextStyle().getVerticalShift();
				if (element instanceof ZLTextWord) {
					drawWord(
						areaX, areaY, (ZLTextWord)element, charIndex, -1, false,
						mySelection.isAreaSelected(area)
							? getSelectionForegroundColor() : getTextColor(getTextStyle().Hyperlink)
					);
				} else if (element instanceof ZLTextImageElement) {
					final ZLTextImageElement imageElement = (ZLTextImageElement)element;
					Size maxSize;
					if(!imageElement.IsCover){
						maxSize = new Size(getPictureWidthMax(imageElement.align), getTextAreaHeight());
					}else{
						maxSize = getTextAreaSize();
					}
					context.drawImage(
						areaX, areaY,
						imageElement.ImageData,
						maxSize,
						imageElement.IsCover
						? ZLPaintContext.ScalingType.FitMaximum
						: ZLPaintContext.ScalingType.IntegerCoefficient
					);
				} else if (element == ZLTextElement.HSpace) {
					final int cw = context.getSpaceWidth();
					/*
					context.setFillColor(getHighlightingColor());
					context.fillRectangle(
						area.XStart, areaY - context.getStringHeight(),
						area.XEnd - 1, areaY + context.getDescent()
					);
					*/
					for (int len = 0; len < area.XEnd - area.XStart; len += cw) {
						context.drawString(areaX + len, areaY, SPACE, 0, 1);
					}
				}
			}
		}
		if (index != to) {
			ZLTextElementArea area = page.TextElementMap.get(index++);
			if (area.ChangeStyle) {
				setTextStyle(area.Style);
			}
			final int start = info.StartElementIndex == info.EndElementIndex
				? info.StartCharIndex : 0;
			final int len = info.EndCharIndex - start;
			final ZLTextWord word = (ZLTextWord)paragraph.getElement(info.EndElementIndex);
			drawWord(
				area.XStart, area.YEnd - context.getDescent() - getTextStyle().getVerticalShift(),
				word, start, len, area.AddHyphenationSign,
				mySelection.isAreaSelected(area)
					? getSelectionForegroundColor() : getTextColor(getTextStyle().Hyperlink)
			);
		}
	}
	
	private int findImageFromLine(ZLTextLineInfo info){
		int ret=-1;
		for(int i=info.StartElementIndex;i<info.EndElementIndex;i++){
			if(info.ParagraphCursor.getElement(i) instanceof ZLTextImageElement){
				return i;
			}
		}
		return ret;
	}
	
	private int checkLostContent(ZLTextPage page,
			ZLTextLineInfo info,
			ZLTextWordCursor start,
			ZLTextWordCursor resultCopy,
			ZLTextWordCursor result,int inTextAreaHeight){
		int textAreaHeight = inTextAreaHeight;
		ZLTextLineInfo inf = null;
		if(resultCopy.compareTo(new ZLTextFixedPosition(
				info.ParagraphCursor.Index, info.EndElementIndex, info.EndCharIndex))>0){
			int removeCount = 0;
			Utils.printLogError(getClass().getSimpleName(), "Error, content may lost, start:("+
			start.getParagraphIndex()+","+start.getElementIndex()+","+start.getCharIndex()+"), resultCopy:("+
			resultCopy.getParagraphIndex()+","+resultCopy.getElementIndex()+","+resultCopy.getCharIndex()+")," +
			"result:("+result.getParagraphIndex()+","+result.getElementIndex()+","+result.getCharIndex()+"),inf:("
			+info.ParagraphCursor.Index+","+info.EndElementIndex+","+info.EndCharIndex+")");
			textAreaHeight -= info.VSpaceAfter;
			removeCount=0;
			for(int j=0;j<page.LineInfos.size();j++){
				inf = page.LineInfos.get(j);
				textAreaHeight+=inf.Height+inf.Descent;
				removeCount++;
				if(textAreaHeight>=0){
					Utils.printLogInfo(getClass().getSimpleName(), "I might correct the error");
					break;
				}
			}
			if(textAreaHeight>=0&&removeCount<page.LineInfos.size()){
				while(removeCount>0){
					page.LineInfos.remove(0);
					removeCount--;
				}
				inf = page.LineInfos.get(0);
				Utils.printLogInfo(getClass().getSimpleName(), "correcting the error move start to pos:(" +
						""+inf.ParagraphCursor.Index+","+inf.StartElementIndex+","+inf.StartCharIndex+")");
				start.moveToParagraph(inf.ParagraphCursor.Index);
				start.moveTo(inf.StartElementIndex, inf.StartCharIndex);
			}else{
				Utils.printLogError(getClass().getSimpleName(), "failed to correct the error content lost!!!");
//				start.moveToParagraph(info.ParagraphCursor.Index);
//				start.moveTo(info.StartElementIndex, info.StartCharIndex);
			}
		}
		Utils.printLogInfo(getClass().getSimpleName(), "return textAreaHeight:"+textAreaHeight);
		return textAreaHeight;
	}
	
	private void buildInfos(ZLTextPage page, ZLTextWordCursor start, ZLTextWordCursor result, boolean useCache) {
		ZLTextWordCursor resultCopy= new ZLTextWordCursor(result);
		result.setCursor(start);
		int textAreaHeight = page.getTextHeight();
		if(textAreaHeight<=0){
			return;
		}
		page.LineInfos.clear();
		page.ImageLists.clear();
		int counter = 0;
		/**
		 * is the word element occured before
		 * */
		boolean isUseCache = useCache;
		boolean isReversed = false;
		ZLTextLineInfo myReversedImageLineInfo = null;
//		ImageEntity preImage = null;
		ImageEntity curImage = null;
		do {
			resetTextStyle();
			ZLTextParagraphCursor paragraphCursor = result.getParagraphCursor();
			
			if(paragraphCursor==null){
				Utils.printLogError(TAG, "Error, buildInfos paragraphCursor is null");
				break;
			}else{
				if(myReversedImageLineInfo!=null&&myReversedImageLineInfo.ParagraphCursor!=null&&
						myReversedImageLineInfo.ParagraphCursor.Index==paragraphCursor.Index){
					result.moveTo(myReversedImageLineInfo.EndElementIndex,myReversedImageLineInfo.EndCharIndex);
					isReversed = false;
				}
			}
			int wordIndex = result.getElementIndex();
			applyStyleChanges(paragraphCursor, 0, wordIndex);
			ZLTextLineInfo info = new ZLTextLineInfo(paragraphCursor, wordIndex, result.getCharIndex(), getTextStyle());
			int endIndex = info.ParagraphCursorLength;
			while (info.EndElementIndex != endIndex) {
				info = processTextLine(page, paragraphCursor, info.EndElementIndex, info.EndCharIndex, endIndex,isUseCache);
				textAreaHeight -= info.Height + info.Descent;
				curImage = page.getCurrentImage();
//				preImage = page.getPreviousImage();
				if(curImage!=null&&curImage.lineInfo!=null&&curImage.lineInfo.ParagraphCursor!=null){
					if(curImage.lineInfo.RealStartElementIndex==info.RealStartElementIndex&&
						curImage.lineInfo.ParagraphCursor.Index==info.ParagraphCursor.Index){
//					if(preImage!=null&&preImage.isReduceTextLineWidth()){
//						preImage.setOverHeight(true);
//						textAreaHeight-=(preImage.getLineHeight()+preImage.lineInfo.Descent-preImage.getCurTextHeight());
//						if(textAreaHeight<=0){
//							/**
//							 * can't show another image,remove it
//							 * */
//							if(page.ImageLists.contains(curImage)){
//								page.ImageLists.remove(curImage);
//								page.setCurImage(page.ImageLists.size()-1);
//								curImage = page.getCurrentImage();
//							}
//						}
//					}
					if(curImage.isReduceTextLineWidth()){
						if(textAreaHeight>=0){
							textAreaHeight+=info.Height + info.Descent;
							info.Height = 0;
						}else{
							/**
							 * doesn't got enough room for the image, we should go back to get some room 
							 * */
							boolean isContinue = false;
//							if(preImage!=null){
//								if(page.ImageLists.contains(curImage)){
//									page.ImageLists.remove(curImage);
//									page.setCurImage(page.ImageLists.size()-1);
//									curImage = page.getCurrentImage();
//								}
//							}else{
								 if(!isReversed){
									 isReversed = !isReversed;
								 }else{
									 /**
									  * already reversed, break now
									  * */
									 Utils.printLogError(getClass().getSimpleName(), "It had been Reversed, break right now!");
//									 result.setCursor(curImage.lineInfo.ParagraphCursor);
//									 result.moveTo(curImage.lineInfo.EndElementIndex, curImage.lineInfo.EndCharIndex);
									 break;
								 }
								int removeCount = 0;
								ZLTextLineInfo inf=null;
								int dummyHeight = textAreaHeight;
								if(dummyHeight*curImage.lineInfo.Width+
										(curImage.getLineHeight()+textAreaHeight)*
										(getTextAreaWidth()-curImage.lineInfo.Width)<0){
									/**
									 * can't reverse situation
									 * */
//									if(page.LineInfos.size()<2&&!isUseCache){
//										//only one line text, so show this image here
//										page.StartCursor.setCursor(curImage.lineInfo.ParagraphCursor);
//										page.StartCursor.moveToParagraphStart();
//										page.LineInfos.clear();
//										page.LineInfos.add(curImage.lineInfo);
//										result.setCursor(page.StartCursor);
//										result.moveTo(curImage.lineInfo.EndElementIndex, curImage.lineInfo.EndCharIndex);
//									}else{
										/**
										 * encounter another image,or previous image side text line
										 */
										if(page.PaintState==PaintStateEnum.END_IS_KNOWN){
											/**
											 * avoid lost contents
											 * */
											Utils.printLogInfo(getClass().getSimpleName(), "call checkLostContent 0");
											textAreaHeight = checkLostContent(page, info, start, resultCopy, result,textAreaHeight);
											if(textAreaHeight>=0){
												textAreaHeight+=info.Height + info.Descent;
												info.Height = 0;
												result.moveTo(info.EndElementIndex, info.EndCharIndex);
												if(!page.LineInfos.contains(info)){
													page.LineInfos.add(info);
												}
												removeCount=0;
												isContinue = true;
												break;
											}
										}else if(page.ImageLists.contains(curImage)){
											page.ImageLists.remove(curImage);
											page.setCurImage(page.ImageLists.size()-1);
											curImage = page.getCurrentImage();
										}
//									}
									break;
								}
								for(int i=page.LineInfos.size()-1;i>=0;i--){
									inf=page.LineInfos.get(i);
									if(dummyHeight<0&&(inf.isImageLine||inf.isSideTextLine)){
										if(curImage.lineInfo.ParagraphCursor.Index==inf.ParagraphCursor.Index&&
												curImage.lineInfo.RealStartElementIndex==inf.RealStartElementIndex){
											/**
											 * encounter line of current image , just remove it, but it impossible occur here
											 * */
											page.LineInfos.remove(inf);
											continue;
										}else{
//											if(findImageFromLine(inf)>=0||inf.isSideTextLine){
												/**
												 * encounter another image,
												 */
												if(page.PaintState==PaintStateEnum.END_IS_KNOWN){
													/**
													 * avoid lost contents
													 * */
													Utils.printLogInfo(getClass().getSimpleName(), "call checkLostContent 1");
													textAreaHeight = checkLostContent(page, info, start, resultCopy, result,textAreaHeight);
													if(textAreaHeight>=0){
														textAreaHeight+=info.Height + info.Descent;
														info.Height = 0;
														result.moveTo(info.EndElementIndex, info.EndCharIndex);
														if(!page.LineInfos.contains(info)){
															page.LineInfos.add(info);
														}
														removeCount=0;
														isContinue = true;
														break;
													}
												}else if(page.ImageLists.contains(curImage)){
													page.ImageLists.remove(curImage);
													page.setCurImage(page.ImageLists.size()-1);
													curImage = page.getCurrentImage();
												}
												removeCount=0;
												break;
											}
//										}
									}
									dummyHeight+=inf.Height+inf.Descent;
									removeCount++;
									if(dummyHeight>=0){
//										i--;
//										if(i>=0){
//											inf=page.LineInfos.get(i);
//											removeCount++;
//											textAreaHeight+=inf.Height+inf.Descent;
//										}
										break;
									}
								}
								if(removeCount>0){
									if(page.LineInfos.size()>=removeCount&&dummyHeight>=0){
										for(int j=0;j<removeCount;j++){
											/**
											 * if can successfully reposition this image, we can remove the line infos
											 * */
											page.LineInfos.remove(page.LineInfos.size()-1);
										}
										myReversedImageLineInfo = curImage.lineInfo;
										textAreaHeight=dummyHeight;
										textAreaHeight+=info.Height+info.Descent;
										info.Height = 0;
										page.LineInfos.add(info);
										resetTextStyle();
										result.setCursor(inf.ParagraphCursor);
										result.moveTo(inf.StartElementIndex, inf.StartCharIndex);
										paragraphCursor = inf.ParagraphCursor;
										wordIndex = inf.StartElementIndex;
										applyStyleChanges(paragraphCursor, 0, wordIndex);
										info = new ZLTextLineInfo(paragraphCursor, wordIndex, inf.StartCharIndex, getTextStyle());
										endIndex = info.ParagraphCursorLength;
										isUseCache = false;
										isContinue = true;
									}else{
										/**
										 * can't show this image at this page, show it to next page
										 * */
										if(page.ImageLists.contains(curImage)){
											page.ImageLists.remove(curImage);
											page.setCurImage(page.ImageLists.size()-1);
											curImage = page.getCurrentImage();
										}
										inf = page.LineInfos.get(page.LineInfos.size()-1);
										if(inf!=null){
											result.setCursor(inf.ParagraphCursor);
											result.moveTo(inf.EndElementIndex,inf.EndCharIndex);
										}
										removeCount=0;
										break;
									}
								}
//							}
							if(isContinue)
								continue;
						}
					}else{
						break;
					}
				}else{
						if(curImage.isReduceTextLineWidth()&&info.isImageLine){
							curImage.setOverHeight(true);
							textAreaHeight-=(curImage.getLineHeight()+curImage.lineInfo.Descent-curImage.getCurTextHeight());
							if(textAreaHeight<0&&isReversed){
								ZLTextLineInfo inf=null;
								/**
								 * find last visible paragraph
								 * */
								for(int i=page.LineInfos.size()-1;i>=0;i--){
									inf = page.LineInfos.get(i);
									if(inf!=null&&inf.Height>0){
										break;
									}
								}
								if(inf!=null&&inf.ParagraphCursor.Index!=curImage.lineInfo.ParagraphCursor.Index){
									/**
									 * current image not at the last line, should be reversed situation
									 * */
									if(curImage.lineInfo.ParagraphCursor.Index>=inf.ParagraphCursor.Index){
										result.setCursor(curImage.lineInfo.ParagraphCursor);
										result.moveTo(curImage.lineInfo.EndElementIndex, curImage.lineInfo.EndCharIndex);
										break;
									}
								}
							}
						}
					}
				}
				if (textAreaHeight < 0 && counter > 0) {
					if(page.PaintState==PaintStateEnum.END_IS_KNOWN){
						Utils.printLogInfo(getClass().getSimpleName(), "call checkLostContent 3");
						textAreaHeight = checkLostContent(page, info, start, resultCopy, result, textAreaHeight);
						if(textAreaHeight>=0){
							textAreaHeight-=info.Height + info.Descent;
//							info.Height = 0;
							result.moveTo(info.EndElementIndex, info.EndCharIndex);
							if(!page.LineInfos.contains(info)){
								page.LineInfos.add(info);
							}
							continue;
						}
					}
					break;
				}
				textAreaHeight -= info.VSpaceAfter;
				result.moveTo(info.EndElementIndex, info.EndCharIndex);
				if(!page.LineInfos.contains(info)){
					page.LineInfos.add(info);
				}
				
				if(page.PaintState==PaintStateEnum.END_IS_KNOWN&&
						resultCopy.compareTo(result)<=0){
//					Utils.printLogError(TAG, "May get same content, break!!!++++++++++++++++++++++++++++++");
//					Utils.printLogInfo(TAG, "result:"+result);
//					Utils.printLogError(TAG, "resultCopy:"+resultCopy);
					textAreaHeight=0;
				}
				
				if (textAreaHeight < 0) {
					break;
				}
				counter++;
			}
		} while (result.isEndOfParagraph() && result.nextParagraph() && !result.getParagraphCursor().isEndOfSection() && (textAreaHeight >= 0));
		resetTextStyle();
	}

	private boolean isHyphenationPossible() {
		return ZLTextStyleCollection.Instance().getBaseStyle().AutoHyphenationOption.getValue()
			&& getTextStyle().allowHyphenations();
	}

	/**
	 * @param paragraphCursor
	 * @param startIndex start element index of this line
	 * @param startCharIndex start char index of this start element
	 * @param endIndex the last element index of this paragraph
	 * @return
	 */
	private ZLTextLineInfo processTextLine(
		ZLTextPage page,
		ZLTextParagraphCursor paragraphCursor,
		final int startIndex,
		final int startCharIndex,
		final int endIndex,
		boolean useCache
	) {
		boolean isCover=false;
		boolean isImageLine = false;
		boolean isAddToCache = true;
		final ZLPaintContext context = getContext();
		final ZLTextLineInfo info = new ZLTextLineInfo(paragraphCursor, startIndex, startCharIndex, getTextStyle());
		/**
		 * abandon cache
		 * */
		final ZLTextLineInfo cachedInfo = myLineInfoCache.get(info);
		if (cachedInfo != null&&useCache) {
			applyStyleChanges(paragraphCursor, startIndex, cachedInfo.EndElementIndex);
			return cachedInfo;
		}

		int currentElementIndex = startIndex;
		int currentCharIndex = startCharIndex;
		final boolean isFirstLine = startIndex == 0 && startCharIndex == 0;
        boolean isTitle=false;
        boolean isStart=false;
        boolean isEnd=false;
        boolean isH1=false;
		if (isFirstLine) {
			ZLTextElement element = paragraphCursor.getElement(currentElementIndex);
			while (isStyleChangeElement(element)) {
				if (element instanceof ZLTextControlElement
						&& (((ZLTextControlElement) element).Kind == FBTextKind.TITLE
						|| ((ZLTextControlElement) element).Kind == FBTextKind.H1 || ((ZLTextControlElement) element).Kind == FBTextKind.H2)) {
					isTitle = true;
					
					if(((ZLTextControlElement) element).IsStart){
						isStart=true;
					}else{
						isEnd=true;
					}
					//------------
					if( ((ZLTextControlElement) element).Kind == FBTextKind.H1 ){
						isH1=true;
					}
				}
				applyStyleChangeElement(element);
				++currentElementIndex;
				currentCharIndex = 0;
				if (currentElementIndex == endIndex) {
					break;
				}
				element = paragraphCursor.getElement(currentElementIndex);
			}
			info.StartStyle = getTextStyle();
			info.RealStartElementIndex = currentElementIndex;
			info.RealStartCharIndex = currentCharIndex;
		}

		ZLTextStyle storedStyle = getTextStyle();

		info.LeftIndent = getTextStyle().getLeftIndent();
		if (isFirstLine && !isTitle) {
			info.LeftIndent += getTextStyle().getFirstLineIndentDelta();
		}

		info.Width = info.LeftIndent;

		if (info.RealStartElementIndex == endIndex) {
			info.EndElementIndex = info.RealStartElementIndex;
			info.EndCharIndex = info.RealStartCharIndex;
			return info;
		}

		int newWidth = info.Width;
		int newHeight = info.Height;
		int newDescent = info.Descent;
		int rightIndent = getTextStyle().getRightIndent();
		int maxWidth = getTextAreaWidth() - rightIndent;
		int maxHeight = getTextAreaHeight();
		final ImageEntity curImage= page.getCurrentImage();
		if(curImage!=null&&curImage.isReduceTextLineWidth()){
			maxWidth-=curImage.width;
			if(maxWidth<getSingleCharacterWidth()){//no enough space to show a single character
				info.EndElementIndex = currentElementIndex;
				info.EndCharIndex = currentCharIndex;
				info.Height = curImage.getLineHeight()+curImage.lineInfo.Descent-curImage.getCurTextHeight();
				storedStyle = getTextStyle();
				curImage.setOverHeight(true);
				return info;
			}
		}
		
		boolean wordOccurred = false;
		boolean isVisible = false;
		int lastSpaceWidth = 0;
		int internalSpaceCounter = 0;
		boolean removeLastSpace = false;
		ImageEntity image=null;
		int elementWidth = 0;
		int elementHeight = 0;
		int offsetHeight=0;
		do {
			ZLTextElement element = paragraphCursor.getElement(currentElementIndex);
			elementWidth = getElementWidth(element, currentCharIndex);
			newWidth +=elementWidth; 
			elementHeight = getElementHeight(element);
			if(newHeight>0){
				offsetHeight = elementHeight-newHeight;
			}
			newHeight = Math.max(newHeight, elementHeight);
			newDescent = Math.max(newDescent, getElementDescent(element));
			if (element == ZLTextElement.HSpace) {
				if (wordOccurred) {
					wordOccurred = false;
					internalSpaceCounter++;
					lastSpaceWidth = context.getSpaceWidth();
					newWidth += lastSpaceWidth;
				}
			} else if (element instanceof ZLTextWord) {
				wordOccurred = true;
				isVisible = true;
			}else if (isStyleChangeElement(element)) {
				applyStyleChangeElement(element);
				if (element instanceof ZLTextControlElement
						&& (((ZLTextControlElement) element).Kind == FBTextKind.TITLE
						|| ((ZLTextControlElement) element).Kind == FBTextKind.H1 || ((ZLTextControlElement) element).Kind == FBTextKind.H2)) {
					isTitle = true;
					if(((ZLTextControlElement) element).IsStart){
						isStart=true;
					}else{
						isEnd=true;
					}
				}
			} else if (element instanceof ZLTextImageElement) {

				wordOccurred = true;
				isVisible = true;
				if(((ZLTextImageElement) element).IsCover){
					isCover=true;
				}else {
					final int imageWidth = elementWidth;
					final int imageHeight = elementHeight;
					boolean isBreak = false;
					boolean isEmbededImage= false;
					if(newWidth<=maxWidth&&imageWidth>0&&imageHeight>0){
						if(((ZLTextImageElement) element).align==ImageAlign.IMAGE_ALIGN_LEFT||
								((ZLTextImageElement) element).align==ImageAlign.IMAGE_ALIGN_RIGHT){
							if(imageWidth<getTextAreaWidth()){
								if(currentElementIndex>startIndex&&info.Width>0&&
										info.Width>info.LeftIndent&&info.Height>0){
									//got something ahead, should turn to next line 
									isBreak = true;
								}else{
									isEmbededImage = true;
								}
							}else if(imageWidth>=getTextAreaWidth()){
								//only equal may occur
								((ZLTextImageElement) element).align = ImageAlign.IMAGE_ALIGN_CENTER;
							}
						}
					}else{
						if(newWidth>maxWidth){
							if(info.LeftIndent>0&&info.Width==info.LeftIndent&&
									info.Height==0&&info.Width+elementWidth>maxWidth&&
									(curImage==null||!curImage.isReduceTextLineWidth())){
								/**
								 * picture too big to shows at the first line, just show it whatever  
								 * */
								isBreak = false;
								if(((ZLTextImageElement) element).align==ImageAlign.IMAGE_ALIGN_LEFT||
										((ZLTextImageElement) element).align==ImageAlign.IMAGE_ALIGN_RIGHT){
									isEmbededImage = true;
								}
								newWidth -=info.LeftIndent;
								info.LeftIndent = 0;
							}else{
								isBreak = true;
								info.isImageLine = true;
							}
						}
					}
					if(isEmbededImage){
						if(curImage!=null&&curImage.isReduceTextLineWidth()){
							if(!TextUtils.isEmpty(((ZLTextImageElement) element).Id)&&
									((ZLTextImageElement) element).Id.equals(curImage.getPath())){
								isBreak = false;
							}else{
								isBreak = true;
								if(info.EndElementIndex==info.StartElementIndex){
									info.isImageLine = true; 
									isAddToCache = false;
								}
							}
//							info.Height = newHeight-elementHeight+
//									curImage.getLineHeight()+curImage.lineInfo.VSpaceAfter-curImage.getCurTextHeight();
//							curImage.setOverHeight(true);
						}else{
							image= new ImageEntity(info,
									((ZLTextImageElement) element).Id,
									imageWidth, 
									imageHeight,
									((ZLTextImageElement) element).align);
							if(!page.hasImage(image)){
								page.ImageLists.add(image);
								page.setCurImage(page.ImageLists.size()-1);
							}else{
								page.resetImageTextHeight(image);
							}
						}
					}
					if(((ZLTextImageElement) element).align!=ImageAlign.IMAGE_ALIGN_TEXT&&!isBreak){
						isImageLine = true;
						info.isImageLine = true;
					}
					if(image!=null||
							((ZLTextImageElement) element).align==ImageAlign.IMAGE_ALIGN_CENTER||
							newWidth>maxWidth||isBreak){
						info.IsVisible = isVisible;
						if(!isBreak){
							info.Width = newWidth;
							if (info.Height < newHeight) {
								info.Height = newHeight;
							}
							if(offsetHeight>0){
								info.Height=newHeight-offsetHeight;
							}
							if (info.Descent < newDescent) {
								info.Descent = newDescent;
							}
							info.EndElementIndex = currentElementIndex+1;
						}else{
							info.EndElementIndex = currentElementIndex;
//							info.isImageLine = true;
						}
						info.EndCharIndex = currentCharIndex;
						info.SpaceCounter = internalSpaceCounter;
						storedStyle = getTextStyle();
						removeLastSpace = !wordOccurred && (internalSpaceCounter > 0);
						break;
					}
				}
			}
			if (newWidth > maxWidth) {
				if (info.EndElementIndex != startIndex || element instanceof ZLTextWord) {
					info.IsVisible = isVisible;
					info.Width = newWidth-elementWidth;
					if (info.Height < newHeight) {
						info.Height = newHeight;
					}
					if(offsetHeight>0){
						info.Height=newHeight-offsetHeight;
					}
					if (info.Descent < newDescent) {
						info.Descent = newDescent;
					}
					info.EndElementIndex = currentElementIndex;
					info.EndCharIndex = currentCharIndex;
					info.SpaceCounter = internalSpaceCounter;
					storedStyle = getTextStyle();
					removeLastSpace = !wordOccurred && (internalSpaceCounter > 0);
					break;
				}
			}
			ZLTextElement previousElement = element;
			++currentElementIndex;
			currentCharIndex = 0;
			boolean allowBreak = currentElementIndex == endIndex;
			if (!allowBreak) {
				element = paragraphCursor.getElement(currentElementIndex);
				allowBreak = ((!(element instanceof ZLTextWord) || previousElement instanceof ZLTextWord) &&
						!(element instanceof ZLTextImageElement) &&
						!(element instanceof ZLTextControlElement));
			}
			if (allowBreak) {
				info.IsVisible = isVisible;
				info.Width = newWidth;
				if (info.Height < newHeight) {
					info.Height = newHeight;
				}
				if (info.Descent < newDescent) {
					info.Descent = newDescent;
				}
				info.EndElementIndex = currentElementIndex;
				info.EndCharIndex = currentCharIndex;
				info.SpaceCounter = internalSpaceCounter;
				storedStyle = getTextStyle();
				removeLastSpace = !wordOccurred && (internalSpaceCounter > 0);
			}
		} while (currentElementIndex != endIndex);

		if (currentElementIndex != endIndex &&
			(isHyphenationPossible() || info.EndElementIndex == startIndex)) {
			ZLTextElement element = paragraphCursor.getElement(currentElementIndex);
			if (element instanceof ZLTextWord) {
				final ZLTextWord word = (ZLTextWord)element;
				newWidth -= getWordWidth(word, currentCharIndex);
				info.Width = newWidth;
				int spaceLeft = maxWidth - newWidth;
				if ((word.Length > 3 && spaceLeft > 2 * context.getSpaceWidth())
					|| info.EndElementIndex == startIndex) {
					ZLTextHyphenationInfo hyphenationInfo = ZLTextHyphenator.Instance().getInfo(word);
					int hyphenationPosition = word.Length - 1;
					int subwordWidth = 0;
					for(; hyphenationPosition > currentCharIndex; hyphenationPosition--) {
						if (hyphenationInfo.isHyphenationPossible(hyphenationPosition)) {
							subwordWidth = getWordWidth(
								word,
								currentCharIndex,
								hyphenationPosition - currentCharIndex,
								word.Data[word.Offset + hyphenationPosition - 1] != '-'
							);
							if (subwordWidth <= spaceLeft) {
								break;
							}
						}
					}
					if (hyphenationPosition == currentCharIndex && info.EndElementIndex == startIndex) {
						hyphenationPosition = word.Length == currentCharIndex + 1 ? word.Length : word.Length - 1;
						subwordWidth = getWordWidth(word, currentCharIndex, word.Length - currentCharIndex, false);
						for(; hyphenationPosition > currentCharIndex + 1; hyphenationPosition--) {
							subwordWidth = getWordWidth(
								word,
								currentCharIndex,
								hyphenationPosition - currentCharIndex,
								word.Data[word.Offset + hyphenationPosition - 1] != '-'
							);
							if (subwordWidth <= spaceLeft) {
								break;
							}
						}
					}
					if (hyphenationPosition > currentCharIndex) {
						info.IsVisible = true;
						info.Width = newWidth + subwordWidth;
						if (info.Height < newHeight) {
							info.Height = newHeight;
						}
						if (info.Descent < newDescent) {
							info.Descent = newDescent;
						}
						info.EndElementIndex = currentElementIndex;
						info.EndCharIndex = hyphenationPosition;
						info.SpaceCounter = internalSpaceCounter;
						storedStyle = getTextStyle();
						removeLastSpace = false;
					}
				}
			}
		}

		if (removeLastSpace) {
			info.Width -= lastSpaceWidth;
			info.SpaceCounter--;
		}

		setTextStyle(storedStyle);
		if (isFirstLine&&!isCover) {
			info.Height += info.StartStyle.getSpaceBefore();
			//info.Height+=myContext.getDedentHegiht();
			if(info.isImageLine&&info.Height>maxHeight){
				info.Height = maxHeight;
				if((info.Height+info.Descent)>maxHeight){
					info.Descent=0;
				}
			}
		}
		if (info.isEndOfParagraph()) {
			info.VSpaceAfter = getTextStyle().getSpaceAfter();
		}
		if(isTitle&&isStart){
			info.Height+=Utils.dip2px(((ZLAndroidLibrary)ZLAndroidLibrary.Instance()).getWidget().getContext(),20);
		}
		if(isTitle&&isEnd){
			info.VSpaceAfter=Utils.dip2px(((ZLAndroidLibrary)ZLAndroidLibrary.Instance()).getWidget().getContext(),20);
		}
		if(isH1){
			info.VSpaceAfter+=100;
		}
		if(isImageLine&&image!=null){
			if(info.Height<maxHeight){
				image.setLineHeight(info.Height);
			}else{
				image.setLineHeight(image.height);
				info.Height = image.height;
			}
			
		}
		if(curImage!=null&&curImage.isReduceTextLineWidth()){
			if(isImageLine){
				//got another image while no enough text to fill the empty space
//				curImage.setOverHeight(true);
			}else{
				curImage.addTextLineHeight(info.Height+info.Descent);
				info.isSideTextLine = true;
			}
		}
		if ((info.EndElementIndex != endIndex || endIndex == info.ParagraphCursorLength)&&useCache&&isAddToCache) {
			myLineInfoCache.put(info, info);
		}

		return info;
	}

	private void prepareTextLine(ZLTextPage page, ZLTextLineInfo info, int x, int y) {
		boolean isImageLine = false;
		y = Math.min(y + info.Height, getTopMargin() + page.getTextHeight() - 1);

		final ZLPaintContext context = getContext();
		final ZLTextParagraphCursor paragraphCursor = info.ParagraphCursor;

		setTextStyle(info.StartStyle);
		int spaceCounter = info.SpaceCounter;
		int fullCorrection = 0;
		boolean endOfParagraph = info.isEndOfParagraph();
		boolean wordOccurred = false;
		boolean changeStyle = true;
		x += info.LeftIndent;
		int rightIndent = getTextStyle().getRightIndent();
		int maxWidth = getTextAreaWidth();
		final ImageEntity curImage = page.getCurrentImage();
		if(curImage!=null&&
				curImage.isReduceTextLineWidth()){
			maxWidth-=curImage.width;
		}
		
		if(info.isImageLine){
			endOfParagraph = true;
		}
		if(maxWidth<=0){
			maxWidth +=curImage.width;
			curImage.setOverHeight(true);
		}
		switch (getTextStyle().getAlignment()) {
			case ZLTextAlignmentType.ALIGN_RIGHT:
				x += maxWidth - rightIndent - info.Width;
				break;
			case ZLTextAlignmentType.ALIGN_CENTER:
				x += (maxWidth - rightIndent - info.Width) / 2;
				break;
			case ZLTextAlignmentType.ALIGN_JUSTIFY:
				if (!endOfParagraph && (paragraphCursor.getElement(info.EndElementIndex) != ZLTextElement.AfterParagraph)) {
					fullCorrection = maxWidth - getTextStyle().getRightIndent() - info.Width;
				}
				break;
			case ZLTextAlignmentType.ALIGN_LEFT:
			case ZLTextAlignmentType.ALIGN_UNDEFINED:
				break;
		}

		final ZLTextParagraphCursor paragraph = info.ParagraphCursor;
		final int paragraphIndex = paragraph.Index;
		final int endElementIndex = info.EndElementIndex;
		int charIndex = info.RealStartCharIndex;
		ZLTextElementArea spaceElement = null;
		int xMax = getLeftMargin()+maxWidth;
		int xMin = x;
		if(curImage!=null&&curImage.isReduceTextLineWidth()){
			if(curImage.align==ImageAlign.IMAGE_ALIGN_LEFT&&maxWidth!=getTextAreaWidth()){
				//image left
				xMax += curImage.width;
				xMin += curImage.width;
				x=xMin;
			}
		}
		info.xMax = xMax;
		info.xMin = xMin;
		for (int wordIndex = info.RealStartElementIndex; wordIndex < endElementIndex; ++wordIndex, charIndex = 0) {
			final ZLTextElement element = paragraph.getElement(wordIndex);
			final int width = getElementWidth(element, charIndex);
			if (element == ZLTextElement.HSpace) {
				if (wordOccurred && (spaceCounter > 0)) {
					final int correction = fullCorrection / spaceCounter;
					final int spaceLength = context.getSpaceWidth() + correction;
					if (getTextStyle().isUnderline()) {
						spaceElement = new ZLTextElementArea(
							paragraphIndex, wordIndex, 0,
							0, // length
							true, // is last in element
							false, // add hyphenation sign
							false, // changed style
							getTextStyle(), element, x, x + spaceLength, y, y
						);
					} else {
						spaceElement = null;
					}
					x += spaceLength;
					fullCorrection -= correction;
					wordOccurred = false;
					--spaceCounter;
				}
			} else if (element instanceof ZLTextWord ) {
				final int height = getElementHeight(element);
				final int descent = getElementDescent(element);
				final int length = element instanceof ZLTextWord ? ((ZLTextWord)element).Length : 0;
				if (spaceElement != null) {
					page.TextElementMap.add(spaceElement);
					spaceElement = null;
				}
				page.TextElementMap.add(new ZLTextElementArea(
					paragraphIndex, wordIndex, charIndex,
					length - charIndex,
					true, // is last in element
					false, // add hyphenation sign
					changeStyle, getTextStyle(), element,
					x, x + width - 1, y - height + 1, y + descent
				));
				changeStyle = false;
				wordOccurred = true;
			}else if(element instanceof ZLTextImageElement){
				final int height = getElementHeight(element);
				final int descent = getElementDescent(element);
				final int length =  0;
				if(!((ZLTextImageElement) element).IsCover){
					if(width<=0||height<=0){
						
					}else{
						ImageEntity image = new ImageEntity(info,((ZLTextImageElement) element).Id, 
								width, height, ((ZLTextImageElement) element).align);
						if(image.isEmbedImage()){
							if(!page.hasImage(image)){
								page.ImageLists.add(image);
								page.setCurImage(page.ImageLists.size()-1);
							}else{
								page.resetImageTextHeight(image);
							}
							if(image.lineInfo.Height==0){
								y+=height;
							}
						}
						if(curImage!=null&&curImage.isReduceTextLineWidth()&&!curImage.equals(image)){
							y+=(curImage.getLineHeight()+curImage.lineInfo.Descent-curImage.getCurTextHeight());
							if(image.align==ImageAlign.IMAGE_ALIGN_RIGHT){
								xMax+= curImage.width;
							}
						}
						if(image.align!=ImageAlign.IMAGE_ALIGN_TEXT){
							isImageLine = true;
						}
						switch(image.align){
						case IMAGE_ALIGN_CENTER:
							//align middle
							x=(getContext().getWidth()-width)/2;
							break;
						case IMAGE_ALIGN_LEFT:
							//align left
							x=getLeftMargin()+getImageBoundary();
							break;
						case IMAGE_ALIGN_RIGHT:
							//align right
							x=xMax-width+getImageBoundary();
							break;
						case IMAGE_ALIGN_TEXT:
							break;
						case IMAGE_ALIGN_MID:
							break;
						default:
							break;
						}
					}
				}
				
				/**************************************************************/
				if (spaceElement != null) {
					page.TextElementMap.add(spaceElement);
					spaceElement = null;
				}
				
				page.TextElementMap.add(new ZLTextElementArea(
					paragraphIndex, wordIndex, charIndex,
					length - charIndex,
					true, // is last in element
					false, // add hyphenation sign
					changeStyle, getTextStyle(), element,
					x, x + width - 1, y - height + getImageBoundary(), y + descent
				));
				changeStyle = false;
				wordOccurred = true;
			}else if (isStyleChangeElement(element)) {
				applyStyleChangeElement(element);
				changeStyle = true;
			}
			x += width;
		}
		if (!endOfParagraph) {
			final int len = info.EndCharIndex;
			if (len > 0) {
				final int wordIndex = info.EndElementIndex;
				final ZLTextWord word = (ZLTextWord)paragraph.getElement(wordIndex);
				final boolean addHyphenationSign = word.Data[word.Offset + len - 1] != '-';
				final int width = getWordWidth(word, 0, len, addHyphenationSign);
				final int height = getElementHeight(word);
				final int descent = context.getDescent();
				page.TextElementMap.add(
					new ZLTextElementArea(
						paragraphIndex, wordIndex, 0,
						len,
						false, // is last in element
						addHyphenationSign,
						changeStyle, getTextStyle(), word,
						x, x + width - 1, y - height + 1, y + descent
					)
				);
			}
		}
		if(curImage!=null&&curImage.isReduceTextLineWidth()){
			if(isImageLine){
				//got another image while no enough text to fill the empty space
//				curImage.setOverHeight(true);
			}else{
				curImage.addTextLineHeight(info.Height+info.Descent);
			}
		}
	}

	public synchronized final void scrollPage(boolean forward, int scrollingMode, int value) {
		preparePaintInfo(myCurrentPage);
		myPreviousPage.reset();
		myNextPage.reset();
		myCurrentRightPage.reset();
		if (myCurrentPage.PaintState == PaintStateEnum.READY) {
			myCurrentPage.PaintState = forward ? PaintStateEnum.TO_SCROLL_FORWARD : PaintStateEnum.TO_SCROLL_BACKWARD;
			myScrollingMode = scrollingMode;
			myOverlappingValue = value;
		}
	}

	public final synchronized void gotoPosition(ZLTextPosition position) {
		if (position != null) {
			gotoPosition(position.getParagraphIndex(), position.getElementIndex(), position.getCharIndex());
		}
	}

	public final synchronized void gotoPosition(int paragraphIndex, int wordIndex, int charIndex) {
		if (myModel != null && myModel.getParagraphsNumber() > 0) {
			if(AudioPlayAction.isAudioSpeek){
				DbStarPlayerApp.Instance().runAction(ActionCode.AUDIO_CANCEL);
			}
			Application.getViewWidget().reset();
			myCurrentPage.moveStartCursor(paragraphIndex, wordIndex, charIndex);
			myPreviousPage.reset();
			myNextPage.reset();
			myCurrentRightPage.reset();
			preparePaintInfo(myCurrentPage);
			if (myCurrentPage.isEmptyPage()) {
				scrollPage(true, ScrollingMode.NO_OVERLAPPING, 0);
			}
			setAudioSpeekPosition();
		}
	}

	private final synchronized void gotoPositionByEnd(int paragraphIndex, int wordIndex, int charIndex) {
		if (myModel != null && myModel.getParagraphsNumber() > 0) {
			if(AudioPlayAction.isAudioSpeek){
				DbStarPlayerApp.Instance().runAction(ActionCode.AUDIO_CANCEL);
			}
			myCurrentPage.moveEndCursor(paragraphIndex, wordIndex, charIndex);
			myPreviousPage.reset();
			myNextPage.reset();
			myCurrentRightPage.reset();
			preparePaintInfo(myCurrentPage);
			if (myCurrentPage.isEmptyPage()) {
				scrollPage(false, ScrollingMode.NO_OVERLAPPING, 0);
			}
			setAudioSpeekPosition();
		}
	}

	protected synchronized void preparePaintInfo() {
		myPreviousPage.reset();
		myNextPage.reset();
		myCurrentRightPage.reset();
		preparePaintInfo(myCurrentPage);
	}

	private synchronized void preparePaintInfo(ZLTextPage page) {
		page.setSize(getTextAreaWidth(), getTextAreaHeight(), page == myPreviousPage);

		if (page.PaintState == PaintStateEnum.NOTHING_TO_PAINT || page.PaintState == PaintStateEnum.READY) {
			return;
		}
		final int oldState = page.PaintState;

		final HashMap<ZLTextLineInfo,ZLTextLineInfo> cache = myLineInfoCache;
		for (ZLTextLineInfo info : page.LineInfos) {
			cache.put(info, info);
		}

		switch (page.PaintState) {
			default:
				break;
			case PaintStateEnum.TO_SCROLL_FORWARD:
				if (!page.EndCursor.isEndOfText()) {
					final ZLTextWordCursor startCursor = new ZLTextWordCursor();
					switch (myScrollingMode) {
						case ScrollingMode.NO_OVERLAPPING:
							break;
						case ScrollingMode.KEEP_LINES:
							page.findLineFromEnd(startCursor, myOverlappingValue);
							break;
						case ScrollingMode.SCROLL_LINES:
							page.findLineFromStart(startCursor, myOverlappingValue);
							if (startCursor.isEndOfParagraph()) {
								startCursor.nextParagraph();
							}
							break;
						case ScrollingMode.SCROLL_PERCENTAGE:
							page.findPercentFromStart(startCursor, myOverlappingValue);
							break;
					}

					if (!startCursor.isNull() && startCursor.samePositionAs(page.StartCursor)) {
						page.findLineFromStart(startCursor, 1);
					}

					if (!startCursor.isNull()) {
						final ZLTextWordCursor endCursor = new ZLTextWordCursor();
						buildInfos(page, startCursor, endCursor,true);
						if (!page.isEmptyPage() && (myScrollingMode != ScrollingMode.KEEP_LINES || !endCursor.samePositionAs(page.EndCursor))) {
							page.StartCursor.setCursor(startCursor);
							page.EndCursor.setCursor(endCursor);
							break;
						}
					}

					page.StartCursor.setCursor(page.EndCursor);
					buildInfos(page, page.StartCursor, page.EndCursor,true);
				}
				break;
			case PaintStateEnum.TO_SCROLL_BACKWARD:
				if (!page.StartCursor.isStartOfText()) {
					switch (myScrollingMode) {
						case ScrollingMode.NO_OVERLAPPING:
							page.StartCursor.setCursor(findStartOfPrevousPage(page, page.StartCursor));
							break;
						case ScrollingMode.KEEP_LINES:
						{
							ZLTextWordCursor endCursor = new ZLTextWordCursor();
							page.findLineFromStart(endCursor, myOverlappingValue);
							if (!endCursor.isNull() && endCursor.samePositionAs(page.EndCursor)) {
								page.findLineFromEnd(endCursor, 1);
							}
							if (!endCursor.isNull()) {
								ZLTextWordCursor startCursor = findStartOfPrevousPage(page, endCursor);
								if (startCursor.samePositionAs(page.StartCursor)) {
									page.StartCursor.setCursor(findStartOfPrevousPage(page, page.StartCursor));
								} else {
									page.StartCursor.setCursor(startCursor);
								}
							} else {
								page.StartCursor.setCursor(findStartOfPrevousPage(page, page.StartCursor));
							}
							break;
						}
						case ScrollingMode.SCROLL_LINES:
							page.StartCursor.setCursor(findStart(page, page.StartCursor, SizeUnit.LINE_UNIT, myOverlappingValue));
							break;
						case ScrollingMode.SCROLL_PERCENTAGE:
							page.StartCursor.setCursor(findStart(page, page.StartCursor, SizeUnit.PIXEL_UNIT, page.getTextHeight() * myOverlappingValue / 100));
							break;
					}
					buildInfos(page, page.StartCursor, page.EndCursor,false);
					if (page.isEmptyPage()) {
						page.StartCursor.setCursor(findStart(page, page.StartCursor, SizeUnit.LINE_UNIT, 1));
						buildInfos(page, page.StartCursor, page.EndCursor,false);
					}
				}
				break;
			case PaintStateEnum.START_IS_KNOWN:
				buildInfos(page, page.StartCursor, page.EndCursor,true);
				break;
			case PaintStateEnum.END_IS_KNOWN:
				page.StartCursor.setCursor(findStartOfPrevousPage(page, page.EndCursor));
				buildInfos(page, page.StartCursor, page.EndCursor,false);
				break;
		}
		page.PaintState = PaintStateEnum.READY;
		// TODO: cache?
		myLineInfoCache.clear();

		if (page == myCurrentPage) {
			final Animation type = ZLApplication.Instance().getViewWidget().getCurAnimationType();
			if(type==Animation.realdouble){
				if (oldState != PaintStateEnum.START_IS_KNOWN) {
					myPreviousPage.reset();
				}
				if (oldState != PaintStateEnum.END_IS_KNOWN) {
					myCurrentRightPage.reset();
				}
			}else{
				if (oldState != PaintStateEnum.START_IS_KNOWN) {
					myPreviousPage.reset();
				}
				if (oldState != PaintStateEnum.END_IS_KNOWN) {
					myNextPage.reset();
				}
			}
		}
	}

	public void clearCaches() {
		resetMetrics();
		rebuildPaintInfo();
		refreshReadPage();
		myCharWidth = -1;
	}

	protected void rebuildPaintInfo() {
		myPreviousPage.reset();
		myNextPage.reset();
		myCurrentRightPage.reset();
		ZLTextParagraphCursorCache.clear();

		if (myCurrentPage.PaintState != PaintStateEnum.NOTHING_TO_PAINT) {
			myCurrentPage.LineInfos.clear();
			if (!myCurrentPage.StartCursor.isNull()) {
				myCurrentPage.StartCursor.rebuild();
				myCurrentPage.EndCursor.reset();
				myCurrentPage.PaintState = PaintStateEnum.START_IS_KNOWN;
			} else if (!myCurrentPage.EndCursor.isNull()) {
				myCurrentPage.EndCursor.rebuild();
				myCurrentPage.StartCursor.reset();
				myCurrentPage.PaintState = PaintStateEnum.END_IS_KNOWN;
			}
		}

		myLineInfoCache.clear();
	}

	private int infoSize(ZLTextLineInfo info, int unit) {
		return (unit == SizeUnit.PIXEL_UNIT) ? (info.Height + info.Descent + info.VSpaceAfter) : (info.IsVisible ? 1 : 0);
	}

	private int paragraphSize(ZLTextPage page, ZLTextWordCursor cursor, int startIndex, boolean beforeCurrentPosition, int unit) {
		final ZLTextParagraphCursor paragraphCursor = cursor.getParagraphCursor();
		if (paragraphCursor == null) {
			return 0;
		}
		final int endElementIndex =
			beforeCurrentPosition ? cursor.getElementIndex() : paragraphCursor.getParagraphLength();

		resetTextStyle();

		int size = 0;

		int wordIndex = startIndex;
		int charIndex = 0;
		while (wordIndex != endElementIndex) {
			ZLTextLineInfo info = processTextLine(page, paragraphCursor, wordIndex, charIndex, endElementIndex,false);
			wordIndex = info.EndElementIndex;
			charIndex = info.EndCharIndex;
			final ImageEntity curImage = page.getCurrentImage();
			final ImageEntity preImage = page.getPreviousImage();
			if(curImage!=null&&curImage.lineInfo!=null&&curImage.lineInfo.ParagraphCursor!=null){
				if(curImage.lineInfo.RealStartElementIndex==info.RealStartElementIndex&&
						curImage.lineInfo.ParagraphCursor.Index==info.ParagraphCursor.Index){
					if(preImage!=null&&preImage.isReduceTextLineWidth()){
						preImage.setOverHeight(true);
						size+=preImage.getLineHeight() + preImage.lineInfo.Descent-preImage.getCurTextHeight();
					}
					info.Height = 0;
				}else{
					if(curImage.isReduceTextLineWidth()&&info.isImageLine){
						curImage.setOverHeight(true);
						size+=curImage.getLineHeight()+ curImage.lineInfo.Descent-curImage.getCurTextHeight();
					}
					size += infoSize(info, unit);
				}
			}else{
				size += infoSize(info, unit);
			}
			if(!page.LineInfos.contains(info)){
				page.LineInfos.add(info);
			}
		}

		return size;
	}

	private int skip(ZLTextPage page,ZLTextWordCursor cursor, int unit, int size) {
		final ZLTextParagraphCursor paragraphCursor = cursor.getParagraphCursor();
		if (paragraphCursor == null) {
			return 0;
		}
		final int endElementIndex = paragraphCursor.getParagraphLength();
		resetTextStyle();
		applyStyleChanges(paragraphCursor, 0, cursor.getElementIndex());
//		for(ImageEntity entity:page.ImageLists){
//			if(entity.lineInfo!=null&&
//					entity.lineInfo.ParagraphCursor!=null&&
//					entity.lineInfo.ParagraphCursor.Index==cursor.getParagraphIndex()){
//				entity.resetTextAreaHeight();
//			}
//		}
		page.ImageLists.clear();
		while (!cursor.isEndOfParagraph() && (size > 0)) {
			ZLTextLineInfo info = processTextLine(page, paragraphCursor, cursor.getElementIndex(), cursor.getCharIndex(), endElementIndex,false);
			cursor.moveTo(info.EndElementIndex, info.EndCharIndex);
			final ImageEntity curImage = page.getCurrentImage();
			final ImageEntity preImage = page.getPreviousImage();
			if(curImage!=null&&curImage.lineInfo!=null&&curImage.lineInfo.ParagraphCursor!=null){
				if(curImage.lineInfo.RealStartElementIndex==info.RealStartElementIndex&&
						curImage.lineInfo.ParagraphCursor.Index==info.ParagraphCursor.Index){
					if(preImage!=null&&preImage.isReduceTextLineWidth()){
						preImage.setOverHeight(true);
						size-=preImage.getLineHeight() + preImage.lineInfo.Descent-preImage.getCurTextHeight();
					}
					info.Height = 0;
				}else{
					if(curImage.isReduceTextLineWidth()&&info.isImageLine){
						curImage.setOverHeight(true);
						size-=curImage.getLineHeight()+ curImage.lineInfo.Descent-curImage.getCurTextHeight();
					}
					size -= infoSize(info, unit);
				}
			}else{
				size -= infoSize(info, unit);
			}
		}
		return size;

	}

	private ZLTextWordCursor findStartOfPrevousPage(ZLTextPage page, ZLTextWordCursor end) {
//		if (twoColumnView()) {
//			end = findStart(page, end, SizeUnit.PIXEL_UNIT, page.getTextHeight());
//		}
		end = findStart(page, end, SizeUnit.PIXEL_UNIT, page.getTextHeight());
		return end;
	}

	private ZLTextWordCursor findStart(ZLTextPage page, ZLTextWordCursor end, int unit, int size) {
		page.LineInfos.clear();
		page.ImageLists.clear();
		ZLTextWordCursor start = new ZLTextWordCursor(end);
		size -= paragraphSize(page,start,0, true, unit);
		boolean positionChanged = !start.isStartOfParagraph();
		start.moveToParagraphStart();
		ImageEntity curImage = page.getCurrentImage();
		ImageEntity preImage = page.getPreviousImage();
		if(curImage!=null&&curImage.isReduceTextLineWidth()&&
				size>=0&&size<=curImage.getLineHeight()-curImage.getCurTextHeight()){
			size-=curImage.getLineHeight()-curImage.getCurTextHeight();
			curImage.setOverHeight(true);
		}
		boolean isReversed =false;
//		boolean isMovePrevious = true;
		ZLTextLineInfo myReversedImageLineInfo = null;
		while (size > 0) {
			if (positionChanged && start.getParagraphCursor().isEndOfSection()) {
				break;
			}
//			if(isMovePrevious){
				if (!start.previousParagraph()) {
					break;
				}
//			}else{
//				isMovePrevious=!isMovePrevious;
//			}
			if (!start.getParagraphCursor().isEndOfSection()) {
				positionChanged = true;
			}
			if(myReversedImageLineInfo!=null&&myReversedImageLineInfo.ParagraphCursor!=null&&
					myReversedImageLineInfo.ParagraphCursor.Index==start.getParagraphCursor().Index){
				start.moveToParagraphEnd();
				size-=paragraphSize(page, start, myReversedImageLineInfo.EndElementIndex, true, unit);
				isReversed = false;
			}else{
				size -= paragraphSize(page,start,0, false, unit);
			}
			curImage = page.getCurrentImage();
//			preImage = page.getPreviousImage();
			if(curImage!=null){
				final int imageHeight = curImage.getLineHeight();
//				if(preImage!=null&&preImage.isReduceTextLineWidth()){
//					if(size<=0){
//						start.nextParagraph();
//						size = 0;
//					}else{
//						preImage.setOverHeight(true);
//						size-=(preImage.getLineHeight()+preImage.lineInfo.Descent-preImage.getCurTextHeight());
//						if(size<0){
//							/**
//							 * just can't show another image, remove it
//							 * */
	//						if(page.ImageLists.contains(curImage)){
	//							page.ImageLists.remove(curImage);
	//							page.setCurImage(page.ImageLists.size()-1);
	//							curImage = page.getCurrentImage();
	//						}
//							start.nextParagraph();
//							size = 0;
//						}
//					}
//				}
				if(size>=0&&(size<=(imageHeight-curImage.getCurTextHeight()))&&curImage.isReduceTextLineWidth()&&
						end.getParagraphCursor()!=null&&curImage.lineInfo.ParagraphCursor.Index<=end.getParagraphCursor().Index){
					/**
					 * if no enough room for the image, we should skip forward to get some room,
					 * here we use the principle of same acreage
					 * */

					if(!isReversed){
						isReversed = !isReversed;
					}else{
						start.setCursor(curImage.lineInfo.ParagraphCursor);
						start.moveTo(curImage.lineInfo.EndElementIndex,curImage.lineInfo.EndCharIndex);
						size = 0;
						break;
					}
					int removeCount = 0;
					int deltaHeight = size-(imageHeight-curImage.getCurTextHeight());
					ZLTextLineInfo inf = null;
					int reversedHeight = 0; 
					if((deltaHeight*curImage.lineInfo.Width+
							size*(getTextAreaWidth()-curImage.lineInfo.Width)<0)){
						/**
						 * can't reverse situation
						 * */
						for(int i=page.LineInfos.size()-1;i>=0;i--){
							inf=page.LineInfos.get(i);
							if(curImage.lineInfo.ParagraphCursor.Index==inf.ParagraphCursor.Index){
								page.LineInfos.remove(inf);
							}
						}
						if(page.LineInfos.size()>0){
							inf = page.LineInfos.get(page.LineInfos.size()-1);
							if(inf!=null){
								start.setCursor(inf.ParagraphCursor);
								if(inf.ParagraphCursor.Index==end.getParagraphIndex()){
									/**
									 * same paragraph as the end, we should get the first one 
									 * */
									if(page.LineInfos.size()>1){
										start.moveTo(page.LineInfos.get(0).EndElementIndex, 
												page.LineInfos.get(0).EndCharIndex);
									}else{
										start.moveTo(inf.StartElementIndex, inf.StartCharIndex);
									}
								}else{
									start.moveTo(inf.EndElementIndex, inf.EndCharIndex);
								}
							}
						}else{
							start.setCursor(curImage.lineInfo.ParagraphCursor);
							start.moveTo(curImage.lineInfo.StartElementIndex, 
									curImage.lineInfo.StartCharIndex);
						}
						page.LineInfos.clear();
						page.ImageLists.clear();
						return start;

					}
					for(int i=page.LineInfos.size()-1;i>=0;i--){
						inf=page.LineInfos.get(i);
						if(deltaHeight<0&&(inf.isImageLine||inf.isSideTextLine)){
							if(curImage.lineInfo.ParagraphCursor.Index==inf.ParagraphCursor.Index&&
									curImage.lineInfo.RealStartElementIndex==inf.RealStartElementIndex){
								/**
								 * encounter line of current image, just remove it
								 * */
								page.LineInfos.remove(inf);
								continue;
							}else{
//								int pos = findImageFromLine(inf);
//								if(pos>0){
									/**
									 * should be previous image or previous image side text line
									 * */
									if(inf.ParagraphCursor.Index>curImage.lineInfo.ParagraphCursor.Index){
										start.setCursor(inf.ParagraphCursor);
										start.moveTo(inf.EndElementIndex,inf.EndCharIndex);
										size = 0;
										removeCount=0;
										break;
									}else{
										/**
										 * previous image is the same paragraph as the current image, not show these image at this page
										 * */
										start.setCursor(curImage.lineInfo.ParagraphCursor);
										start.moveTo(curImage.lineInfo.EndElementIndex,curImage.lineInfo.EndCharIndex);
										size = 0;
										removeCount=0;
										break;
									}
//								}
							}
						}
						deltaHeight+=inf.Height+inf.Descent;
						reversedHeight+=inf.Height+inf.Descent;
						removeCount++;
						if(deltaHeight>=0){
//							i--;
//							if(i>=0){
//								inf=page.LineInfos.get(i);
//								removeCount++;
//								deltaHeight+=inf.Height+inf.Descent;
//								size+=inf.Height+inf.Descent;
//							}
							break;
						}
					}
					if(removeCount>0){
						
						if(page.LineInfos.size()>=removeCount&&deltaHeight>=0){
							start.setCursor(inf.ParagraphCursor);
//							start.moveTo(inf.EndElementIndex, inf.EndCharIndex);
							start.moveToParagraphEnd();
							size+=reversedHeight;
							myReversedImageLineInfo = curImage.lineInfo;
							for(int j=0;j<removeCount;j++){
								/**
								 * if can successfully reposition this image, we can remove the line infos
								 * */
								page.LineInfos.remove(page.LineInfos.size()-1);
							}
							size -= paragraphSize(page, start, inf.StartElementIndex, true,unit);
							continue;
//							isMovePrevious = false;
						}else{
							inf = page.LineInfos.get(page.LineInfos.size()-1);
							if(inf!=null){
								start.setCursor(inf.ParagraphCursor);
								start.moveTo(inf.EndElementIndex, inf.EndCharIndex);
							}
							page.LineInfos.clear();
							page.ImageLists.clear();
							return start;
						}
					}
				}
				/**
				 * if got picture at last paragraph
				 * */
//				if(curImage!=null&&curImage.isReduceTextLineWidth()&&curImage.lineInfo!=null&&
//						curImage.lineInfo.ParagraphCursor!=null&&
//						curImage.lineInfo.ParagraphCursor.Index==end.getParagraphIndex()&&
//						start.getParagraphIndex()==end.getParagraphIndex()){
//					curImage.setOverHeight(true);
//					size-=(curImage.getLineHeight()+curImage.lineInfo.Descent-curImage.getCurTextHeight());
//				}
			}
		}
		if(curImage!=null&&curImage.isReduceTextLineWidth()){
			size -= (curImage.getLineHeight()+curImage.lineInfo.Descent-curImage.getCurTextHeight());
			curImage.setOverHeight(true);
		}
		skip(page,start, unit, -size);
//		int skipSize = -size;
//		if(skipSize>0){
//			ZLTextLineInfo skipInf = null;
//			for(int i=page.LineInfos.size()-1;i>=0;i--){
//				skipInf = page.LineInfos.get(i);
//				skipSize-= skipInf.Height+skipInf.Descent;
//				if(skipSize<0){
//					start.setCursor(skipInf.ParagraphCursor);
//					start.moveTo(skipInf.EndElementIndex, skipInf.EndCharIndex);
//					break;
//				}
//			}
//		}

		if (unit == SizeUnit.PIXEL_UNIT) {
			boolean sameStart = start.samePositionAs(end);
			if (!sameStart && start.isEndOfParagraph() && end.isStartOfParagraph()) {
				ZLTextWordCursor startCopy = new ZLTextWordCursor(start);
				startCopy.nextParagraph();
				sameStart = startCopy.samePositionAs(end);
				start.setCursor(startCopy);
			}
			if (sameStart) {
				start.setCursor(findStart(page, end, SizeUnit.LINE_UNIT, 1));
			}
		}
		if(start.compareTo(end)>=0){
			if(curImage!=null&&end.getParagraphIndex()>curImage.lineInfo.ParagraphCursor.Index){
				start.setCursor(curImage.lineInfo.ParagraphCursor);
				start.moveTo(curImage.lineInfo.StartElementIndex, curImage.lineInfo.StartCharIndex);
			}
		}
		page.LineInfos.clear();
		page.ImageLists.clear();
		return start;
	}

	protected ZLTextElementArea getElementByCoordinates(int x, int y) {
		return myCurrentPage.TextElementMap.binarySearch(x, y);
	}

	public void hideSelectedRegionBorder() {
		myHighlightSelectedRegion = false;
		Application.getViewWidget().reset();
	}

	private ZLTextRegion getSelectedRegion(ZLTextPage page) {
		return page.TextElementMap.getRegion(mySelectedRegionSoul);
	}

	public ZLTextRegion getSelectedRegion() {
		return getSelectedRegion(myCurrentPage);
	}

	protected ZLTextHighlighting findHighlighting(int x, int y, int maxDistance) {
		final ZLTextRegion region = findRegion(x, y, maxDistance, ZLTextRegion.AnyRegionFilter);
		if (region == null) {
			return null;
		}
		synchronized (myHighlightings) {
			for (ZLTextHighlighting h : myHighlightings) {
				if (h.getBackgroundColor() != null && h.intersects(region)) {
					return h;
				}
			}
		}
		return null;
	}

	protected ZLTextRegion findRegion(int x, int y, ZLTextRegion.Filter filter) {
		return findRegion(x, y, Integer.MAX_VALUE - 1, filter);
	}

	protected ZLTextRegion findRegion(int x, int y, int maxDistance, ZLTextRegion.Filter filter) {
		return myCurrentPage.TextElementMap.findRegion(x, y, maxDistance, filter);
	}

	public void selectRegion(ZLTextRegion region) {
		final ZLTextRegion.Soul soul = region != null ? region.getSoul() : null;
		if (soul == null || !soul.equals(mySelectedRegionSoul)) {
			myHighlightSelectedRegion = true;
		}
		mySelectedRegionSoul = soul;
	}

	protected boolean initSelection(int x, int y) {
		y -= ZLTextSelectionCursor.getHeight() / 2 + ZLTextSelectionCursor.getAccent() / 2;
		if (!mySelection.start(x, y)) {
			return false;
		}
		refreshReadPage();
		return true;
	}

	public void clearSelection() {
		if (mySelection.clear()) {
			refreshReadPage();
		}
	}

	public int getSelectionStartY() {
		if (mySelection.isEmpty()) {
			return 0;
		}
		final ZLTextElementArea selectionStartArea = mySelection.getStartArea(myCurrentPage);
		if (selectionStartArea != null) {
			return selectionStartArea.YStart;
		}
		if (mySelection.hasPartBeforePage(myCurrentPage)) {
			final ZLTextElementArea firstArea = myCurrentPage.TextElementMap.getFirstArea();
			return firstArea != null ? firstArea.YStart : 0;
		} else {
			final ZLTextElementArea lastArea = myCurrentPage.TextElementMap.getLastArea();
			return lastArea != null ? lastArea.YEnd : 0;
		}
	}

	public int getSelectionEndY() {
		if (mySelection.isEmpty()) {
			return 0;
		}
		final ZLTextElementArea selectionEndArea = mySelection.getEndArea(myCurrentPage);
		if (selectionEndArea != null) {
			return selectionEndArea.YEnd;
		}
		if (mySelection.hasPartAfterPage(myCurrentPage)) {
			final ZLTextElementArea lastArea = myCurrentPage.TextElementMap.getLastArea();
			return lastArea != null ? lastArea.YEnd : 0;
		} else {
			final ZLTextElementArea firstArea = myCurrentPage.TextElementMap.getFirstArea();
			return firstArea != null ? firstArea.YStart : 0;
		}
	}

	public ZLTextPosition getSelectionStartPosition() {
		return mySelection.getStartPosition();
	}

	public ZLTextPosition getSelectionEndPosition() {
		return mySelection.getEndPosition();
	}

	public boolean isSelectionEmpty() {
		return mySelection.isEmpty();
	}

	public void resetRegionPointer() {
		mySelectedRegionSoul = null;
		myHighlightSelectedRegion = true;
	}

	public ZLTextRegion nextRegion(Direction direction, ZLTextRegion.Filter filter) {
		return myCurrentPage.TextElementMap.nextRegion(getSelectedRegion(), direction, filter);
	}

	@Override
	public boolean canScroll(PageIndex index) {
		switch (index) {
			default:
				return true;
			case next:
			{
				final ZLTextWordCursor cursor;
				if(ZLApplication.Instance().getViewWidget().getCurAnimationType()==Animation.realdouble){
					cursor = getDoublePageEndCursor();
				}else{
					cursor = getEndCursor();
				}
				return cursor != null && !cursor.isNull() && !cursor.isEndOfText();
			}
			case previous:
			{
				final ZLTextWordCursor cursor = getStartCursor();
				return cursor != null && !cursor.isNull() && !cursor.isStartOfText();
			}
		}
	}

	/**
	 * Destroy Bitmaps
	 */
	public void destroyBitmaps(){
		
		recycleBitmapByResID(R.drawable.reader_read_bookmark_pink);
		recycleBitmapByResID(R.drawable.reader_read_volume_background);
	}
	
	private void refreshReadPage(){
		Application.getViewWidget().reset();
		Application.getViewWidget().repaint();
	}

	public boolean isShowVolumeStatus() {
		return isShowVolumeStatus;
	}

	public void setShowVolumeStatus(boolean flag) {
		isShowVolumeStatus = flag;
	}
	
	private void recycleBitmapByResID(int resid){
		if(resid!=-1){
			ImageManager.getInstance().recycleById(resid);
		}
	}
	
	public Bookmark getCurBookMark(){
		Bookmark bookmark = null;
		checkHightlings(myCurrentPage);
		bookmark = myCurrentPage.getBookMark();
		if(null==bookmark&&ZLApplication.Instance().getViewWidget().getCurAnimationType()==Animation.realdouble){
			checkHightlings(myCurrentRightPage);
			return myCurrentRightPage.getBookMark();
		}
		return bookmark;
	}
	
//	public void setDrawBookmarkAnimation(){
//		isDrawBookmarkAnitmation = true;
//	}
//	
//	public void finishDrawingBookmark(){
//		if(isDrawBookmarkAnitmation){
//			Utils.printLogError(TAG, "Book mark is drawing, stop it now");
//			isDrawBookmarkAnitmation = !isDrawBookmarkAnitmation;
//			
//		}
//	}
	
	private void checkHightlings(ZLTextPage page){
		synchronized (myHighlightings) {
			for (ZLTextHighlighting h : myHighlightings) {
				if (h.intersects(page)) {
					if(h instanceof BookmarkHighlighting){
						if(h.getStartPosition().compareTo(page.StartCursor)>=0){
							page.setBookMark(((BookmarkHighlighting)h).getHightlightingBookmark());	
						}
					}
				}
			}
		}
	}
	
	@Override
	public void setReadProgressChangeListener(
			ReadProgressChangeListener listener) {
		mReadProgressChangeListener = listener;
	}
}
