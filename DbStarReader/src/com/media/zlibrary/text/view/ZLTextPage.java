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

import com.media.dbstarplayer.book.Bookmark;
import com.media.reader.vo.ImageEntity;

public final class ZLTextPage {
	final ZLTextWordCursor StartCursor = new ZLTextWordCursor();
	final ZLTextWordCursor EndCursor = new ZLTextWordCursor();
	final ArrayList<ZLTextLineInfo> LineInfos = new ArrayList<ZLTextLineInfo>();
	final ArrayList<ImageEntity> ImageLists = new ArrayList<ImageEntity>();
	private int curImageIndex = 0; 
	int PaintState = PaintStateEnum.NOTHING_TO_PAINT;

	public final ZLTextElementAreaVector TextElementMap = new ZLTextElementAreaVector();

	private int myWidth;
	private int myHeight;

	/**
	 * book mark of this page
	 * */
	private Bookmark mBookMark =null;
	/**
	 * flag of release book mark page
	 * */
//	private boolean isReleaseBookMark = false;
	void setSize(int width, int height,boolean keepEndNotStart) {
		if (myWidth == width && myHeight == height) {
			return;
		}
		myWidth = width;
		myHeight = height;

		if (PaintState != PaintStateEnum.NOTHING_TO_PAINT) {
			LineInfos.clear();
			if (keepEndNotStart) {
				if (!EndCursor.isNull()) {
					StartCursor.reset();
					PaintState = PaintStateEnum.END_IS_KNOWN;
				} else if (!StartCursor.isNull()) {
					EndCursor.reset();
					PaintState = PaintStateEnum.START_IS_KNOWN;
				}
			} else {
				if (!StartCursor.isNull()) {
					EndCursor.reset();
					PaintState = PaintStateEnum.START_IS_KNOWN;
				} else if (!EndCursor.isNull()) {
					StartCursor.reset();
					PaintState = PaintStateEnum.END_IS_KNOWN;
				}
			}
		}
	}
	@Override
	public String toString() {
		return /*super.toString() +*/ "StartCursor:"+StartCursor+", EndCursor:"+EndCursor;
	}
	
	ImageEntity getCurrentImage(){
		if(ImageLists.size()>0&&curImageIndex>=0&&curImageIndex<ImageLists.size()){
			return ImageLists.get(curImageIndex);
		}else{
			return null;
		}
	}
	boolean hasImage(ImageEntity imageEntity){
		boolean flag = false;
		for(int i=0;i<ImageLists.size();i++){
			ImageEntity image = ImageLists.get(i);
			if(image!=null&&image.equals(imageEntity)){
				flag = true;
				break;
			}
		}
		return flag;
	}
	
	ImageEntity getPreviousImage(){
		if(ImageLists.size()>1&&curImageIndex>0&&curImageIndex<ImageLists.size()){
			return ImageLists.get(curImageIndex-1);
		}else{
			return null;
		}
	}
	
	void setCurImage(int index){
		if(index>=0)
			curImageIndex = index;
	}
	
	void resetImageTextHeight(ImageEntity imageEntity){
		for(int i=0;i<ImageLists.size();i++){
			ImageEntity image = ImageLists.get(i);
			if(image!=null&&image.equals(imageEntity)){
				image.resetTextAreaHeight();
				curImageIndex = i;
				break;
			}
		}
	}
	
	void resetAllImageTextHeight(){
		for(int i=0;i<ImageLists.size();i++){
			ImageEntity image = ImageLists.get(i);
			if(image!=null){
				image.resetTextAreaHeight();
			}
		}
		curImageIndex = 0;
	}
	
	void reset() {
		StartCursor.reset();
		EndCursor.reset();
		LineInfos.clear();
		ImageLists.clear();
		curImageIndex = 0;
		PaintState = PaintStateEnum.NOTHING_TO_PAINT;
	}

	void moveStartCursor(ZLTextParagraphCursor cursor) {
		StartCursor.setCursor(cursor);
		EndCursor.reset();
		LineInfos.clear();
		ImageLists.clear();
		curImageIndex = 0;
		PaintState = PaintStateEnum.START_IS_KNOWN;
	}

	void moveStartCursor(int paragraphIndex, int wordIndex, int charIndex) {
		if (StartCursor.isNull()) {
			StartCursor.setCursor(EndCursor);
		}
		StartCursor.moveToParagraph(paragraphIndex);
		StartCursor.moveTo(wordIndex, charIndex);
		EndCursor.reset();
		LineInfos.clear();
		ImageLists.clear();
		curImageIndex = 0;
		PaintState = PaintStateEnum.START_IS_KNOWN;
	}

	void moveEndCursor(int paragraphIndex, int wordIndex, int charIndex) {
		if (EndCursor.isNull()) {
			EndCursor.setCursor(StartCursor);
		}
		EndCursor.moveToParagraph(paragraphIndex);
		if ((paragraphIndex > 0) && (wordIndex == 0) && (charIndex == 0)) {
			EndCursor.previousParagraph();
			EndCursor.moveToParagraphEnd();
		} else {
			EndCursor.moveTo(wordIndex, charIndex);
		}
		StartCursor.reset();
		LineInfos.clear();
		ImageLists.clear();
		PaintState = PaintStateEnum.END_IS_KNOWN;
	}

	int getTextWidth() {
		return myWidth;
	}

	int getTextHeight() {
		return myHeight;
	}

	boolean isEmptyPage() {
		for (ZLTextLineInfo info : LineInfos) {
			if (info.IsVisible) {
				return false;
			}
		}
		return true;
	}

	void findLineFromStart(ZLTextWordCursor cursor, int overlappingValue) {
		if (LineInfos.isEmpty() || (overlappingValue == 0)) {
			cursor.reset();
			return;
		}
		ZLTextLineInfo info = null;
		for (ZLTextLineInfo i : LineInfos) {
			info = i;
			if (info.IsVisible) {
				--overlappingValue;
				if (overlappingValue == 0) {
					break;
				}
			}
		}
		cursor.setCursor(info.ParagraphCursor);
		cursor.moveTo(info.EndElementIndex, info.EndCharIndex);
	}

	void findLineFromEnd(ZLTextWordCursor cursor, int overlappingValue) {
		if (LineInfos.isEmpty() || (overlappingValue == 0)) {
			cursor.reset();
			return;
		}
		final ArrayList<ZLTextLineInfo> infos = LineInfos;
		final int size = infos.size();
		ZLTextLineInfo info = null;
		for (int i = size - 1; i >= 0; --i) {
			info = infos.get(i);
			if (info.IsVisible) {
				--overlappingValue;
				if (overlappingValue == 0) {
					break;
				}
			}
		}
		cursor.setCursor(info.ParagraphCursor);
		cursor.moveTo(info.StartElementIndex, info.StartCharIndex);
	}

	void findPercentFromStart(ZLTextWordCursor cursor, int percent) {
		if (LineInfos.isEmpty()) {
			cursor.reset();
			return;
		}
		int height = myHeight * percent / 100;
		boolean visibleLineOccured = false;
		ZLTextLineInfo info = null;
		for (ZLTextLineInfo i : LineInfos) {
			info = i;
			if (info.IsVisible) {
				visibleLineOccured = true;
			}
			height -= info.Height + info.Descent + info.VSpaceAfter;
			if (visibleLineOccured && (height <= 0)) {
				break;
			}
		}
		cursor.setCursor(info.ParagraphCursor);
		cursor.moveTo(info.EndElementIndex, info.EndCharIndex);
	}
	
	public Bookmark getBookMark() {
		return mBookMark;
	}
	
	public void setBookMark(Bookmark bookmark) {
		mBookMark = bookmark;
	}
	
//	public boolean isReleaseBookMark() {
//		return isReleaseBookMark;
//	}
//	public void setReleaseBookMark(boolean flag) {
//		isReleaseBookMark = flag;
//	}
}
