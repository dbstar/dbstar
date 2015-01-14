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

package com.media.zlibrary.core.view;

import com.media.dbstarplayer.book.Bookmark;
import com.media.reader.view.ReadProgressChangeListener;
import com.media.zlibrary.core.application.ZLApplication;
import com.media.zlibrary.ui.android.view.BitmapManager.EdgePosition;

abstract public class ZLView {
	public final ZLApplication Application;
	private ZLPaintContext myViewContext = new DummyPaintContext();

	protected ZLView(ZLApplication application) {
		Application = application;
	}

	protected final void setContext(ZLPaintContext context) {
		myViewContext = context;
	}

	public final ZLPaintContext getContext() {
		return myViewContext;
	}

	public final int getContextWidth() {
		return myViewContext.getWidth();
	}

	public final int getContextHeight() {
		return myViewContext.getHeight();
	}

//	abstract public interface FooterArea {
//		int getHeight();
//		void paint(ZLPaintContext context);
//	}

//	abstract public FooterArea getFooterArea();

	public static enum PageIndex {
		previous, current, next,curright;

		/**
		 * if in double flip mode 
		 * 
		 * previous is always the flip page back side page, as the third page
		 * 
		 * next is always the new second page, as the forth page
		 * 
		 * current page is always the left page,as the first page
		 * 
		 * current page is always the current right page, as the second page
		 * */
		public PageIndex getNext() {
			final Animation type = ZLApplication.Instance().getViewWidget().getCurAnimationType();
			if(type==Animation.realdouble){
				switch (this) {
					case next:
						return current;
					case previous:
						return curright;
					case current:
						return previous;
					case curright:
						return next;
					default:
						return null;
				}
			}else{
				switch (this) {
					case previous:
						return current;
					case current:
						return next;
					default:
						return null;
				}
			}
		}

		public PageIndex getPrevious() {
			final Animation type = ZLApplication.Instance().getViewWidget().getCurAnimationType();
			if(type==Animation.realdouble){
				switch (this) {
					case next:
						return curright;
					case previous:
						return current;
					case curright:
						return previous;
					case current:
						return next;
					default:
						return null;
				}
			}else{
				switch (this) {
					case next:
						return current;
					case current:
						return previous;
					default:
						return null;
				}
			}
		}
	};
	public static enum Direction {
		leftToRight(true), rightToLeft(true), up(false), down(false);

		public final boolean IsHorizontal;

		Direction(boolean isHorizontal) {
			IsHorizontal = isHorizontal;
		}
	};
	public static enum Animation {
		none, curl, slide, shift,realsingle,realdouble
	}

	public abstract Animation getAnimationType();

	abstract public void preparePage(ZLPaintContext context, PageIndex pageIndex);
	abstract public void paint(ZLPaintContext context, PageIndex pageIndex, EdgePosition edgepos,ZLView.PageIndex progress);
	abstract public void onScrollingFinished(PageIndex pageIndex);

	public boolean onFingerPress(int x, int y) {
		return false;
	}

	public boolean onFingerRelease(int x, int y) {
		return false;
	}

	public boolean onFingerMove(int x, int y) {
		return false;
	}

	public boolean onFingerLongPress(int x, int y) {
		return false;
	}

	public boolean onFingerReleaseAfterLongPress(int x, int y) {
		return false;
	}

	public boolean onFingerMoveAfterLongPress(int x, int y) {
		return false;
	}

	public boolean onFingerSingleTap(int x, int y) {
		return false;
	}

	public boolean onFingerDoubleTap(int x, int y) {
		return false;
	}

	public boolean isDoubleTapSupported() {
		return false;
	}

	public boolean onTrackballRotated(int diffX, int diffY) {
		return false;
	}

	public abstract boolean isScrollbarShown();
	public abstract int getScrollbarFullSize();
	public abstract int getScrollbarThumbPosition(PageIndex pageIndex);
	public abstract int getScrollbarThumbLength(PageIndex pageIndex);

	public abstract boolean canScroll(PageIndex index);
	
	public abstract Bookmark getCurBookMark(); 
	
	public abstract void setReadProgressChangeListener(ReadProgressChangeListener listener);
//	public abstract void setDrawBookmarkAnimation();
//	
//	public abstract void finishDrawingBookmark();
}
