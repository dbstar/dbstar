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

import com.media.android.dbstarplayer.R;
import com.media.reader.vo.ImageEntity.ImageAlign;
import com.media.zlibrary.core.application.ZLApplication;
import com.media.zlibrary.core.filesystem.ZLFile;
import com.media.zlibrary.core.library.ZLibrary;
import com.media.zlibrary.core.util.ZLColor;
import com.media.zlibrary.core.view.ZLPaintContext;
import com.media.zlibrary.core.view.ZLPaintContext.Size;
import com.media.zlibrary.core.view.ZLView;
import com.media.zlibrary.text.model.ZLTextMetrics;
import com.media.zlibrary.text.view.style.ZLTextBaseStyle;
import com.media.zlibrary.text.view.style.ZLTextExplicitlyDecoratedStyle;
import com.media.zlibrary.text.view.style.ZLTextStyleCollection;
import com.media.zlibrary.text.view.style.ZLTextStyleDecoration;
import com.media.zlibrary.ui.android.library.ZLAndroidLibrary;

abstract class ZLTextViewBase extends ZLView {
	/**
	 * title to content 
	 */
	public final static int MARGIN_TITLE_CONTENT_DIP=0;
	
	/**
	 * title height
	 */
	public final static int HEIGHT_TITLE_DIP=0;
	public static enum ImageFitting {
		none, covers, all
	}

	private ZLTextStyle myTextStyle;
	private int myWordHeight = -1;
	private ZLTextMetrics myMetrics;

	ZLTextViewBase(ZLApplication application) {
		super(application);
		resetTextStyle();
	}

	protected void resetMetrics() {
		myMetrics = null;
	}

	private ZLTextMetrics metrics() {
		if (myMetrics == null) {
			final ZLTextStyleCollection collection = ZLTextStyleCollection.Instance();
			final ZLTextBaseStyle base = collection.getBaseStyle();
			myMetrics = new ZLTextMetrics(
				ZLibrary.Instance().getDisplayDPI(),
				collection.getDefaultFontSize(),
				base.getFontSize(),
				// TODO: font X height
				base.getFontSize() * 15 / 10,
				// TODO: screen area width
				100,
				// TODO: screen area height
				100
			);
		}
		return myMetrics;
	}

	final int getWordHeight() {
		if (myWordHeight == -1) {
			final ZLTextStyle textStyle = myTextStyle;
			myWordHeight = getContext().getStringHeight() * textStyle.getLineSpacePercent() / 100 + textStyle.getVerticalShift();
		}
		return myWordHeight;
	}

	public abstract ImageFitting getImageFitting();

	public abstract int getLeftMargin();
	public abstract int getRightMargin();
	public abstract int getTopMargin();
	public abstract int getBottomMargin();
	public abstract int getSpaceBetweenColumns();


	public abstract ZLFile getWallpaperFile();
	public abstract ZLPaintContext.WallpaperMode getWallpaperMode();
	public abstract ZLColor getBackgroundColor();
	public abstract ZLColor getSelectionBackgroundColor();
	public abstract ZLColor getSelectionForegroundColor();
	public abstract ZLColor getHighlightingBackgroundColor();
	public abstract ZLColor getTextColor(ZLTextHyperlink hyperlink);

	ZLPaintContext.Size getTextAreaSize() {
		return new ZLPaintContext.Size(getTextAreaWidth(), getTextAreaHeight());
	}

	int getTextAreaHeight() {
		return getContextHeight() - getTopMargin() - getBottomMargin();
	}

	int getTextAreaWidth(){
		return getContextWidth() - getLeftMargin() - getRightMargin();
	}
	int getBottomLine() {
		return getContextHeight() - getBottomMargin() - 1;
	}

	int getRightLine() {
		return getContextWidth() - getRightMargin() - 1;
	}
	final ZLTextStyle getTextStyle() {
		return myTextStyle;
	}

	final void setTextStyle(ZLTextStyle style) {
		if (myTextStyle != style) {
			myTextStyle = style;
			myWordHeight = -1;
		}
		getContext().setFont(style.getFontFamily(), style.getFontSize(metrics()), style.isBold(), style.isItalic(), style.isUnderline(), style.isStrikeThrough());
	}

	final void resetTextStyle() {
		setTextStyle(ZLTextStyleCollection.Instance().getBaseStyle());
	}

	boolean isStyleChangeElement(ZLTextElement element) {
		return
			element == ZLTextElement.StyleClose ||
			element instanceof ZLTextStyleElement ||
			element instanceof ZLTextControlElement;
	}

	void applyStyleChangeElement(ZLTextElement element) {
		if (element == ZLTextElement.StyleClose) {
			applyStyleClose();
		} else if (element instanceof ZLTextStyleElement) {
			applyStyle((ZLTextStyleElement)element);
		} else if (element instanceof ZLTextControlElement) {
			applyControl((ZLTextControlElement)element);
		}
	}

	void applyStyleChanges(ZLTextParagraphCursor cursor, int index, int end) {
		for (; index != end; ++index) {
			applyStyleChangeElement(cursor.getElement(index));
		}
	}

	private void applyControl(ZLTextControlElement control) {
		if (control.IsStart) {
			final ZLTextStyleDecoration decoration =
				ZLTextStyleCollection.Instance().getDecoration(control.Kind);
			if (control instanceof ZLTextHyperlinkControlElement) {
				setTextStyle(decoration.createDecoratedStyle(myTextStyle, ((ZLTextHyperlinkControlElement)control).Hyperlink));
			} else {
				setTextStyle(decoration.createDecoratedStyle(myTextStyle));
			}
		} else {
			setTextStyle(myTextStyle.Base);
		}
	}

	private void applyStyle(ZLTextStyleElement element) {
		setTextStyle(new ZLTextExplicitlyDecoratedStyle(myTextStyle, element.Entry));
	}

	private void applyStyleClose() {
		setTextStyle(myTextStyle.Base);
	}

	protected final ZLPaintContext.ScalingType getScalingType(ZLTextImageElement imageElement) {
		switch (getImageFitting()) {
			default:
			case none:
				return ZLPaintContext.ScalingType.IntegerCoefficient;
			case covers:
				return imageElement.IsCover
					? ZLPaintContext.ScalingType.FitMaximum
					: ZLPaintContext.ScalingType.IntegerCoefficient;
			case all:
				return ZLPaintContext.ScalingType.FitMaximum;
		}
	}

	final int PICTURE_WIDTH_MAX_WIDTH_BOUNDARY = 700;
	protected int getPictureWidthMax(ImageAlign align){
		int width = getTextAreaWidth();
		int boundary = 2*getImageBoundary();
		if(align==ImageAlign.IMAGE_ALIGN_TEXT){
			boundary = 0;
		}
		if(width>PICTURE_WIDTH_MAX_WIDTH_BOUNDARY&&
				ZLApplication.Instance().getViewWidget().getCurAnimationType()==Animation.realdouble){
			width= PICTURE_WIDTH_MAX_WIDTH_BOUNDARY;
		}
		return width-boundary;
	}
	protected int getSingleCharacterWidth(){
		return getContext().getStringWidth(((ZLAndroidLibrary)ZLAndroidLibrary.Instance()).
				getWidget().getContext().getResources().getString(R.string.lable_font_big));
	}
	
	final int getElementWidth(ZLTextElement element, int charIndex) {
		if (element instanceof ZLTextWord) {
			return getWordWidth((ZLTextWord)element, charIndex);
		} else if (element instanceof ZLTextImageElement) {
			final ZLTextImageElement imageElement = (ZLTextImageElement)element;
			int boundary = 2*getImageBoundary();
			if(imageElement.align==ImageAlign.IMAGE_ALIGN_TEXT){
				boundary = 0;
			}
			int widthMax = getPictureWidthMax(imageElement.align);
			if(imageElement.width>0){
				if(imageElement.width+boundary<getTextAreaWidth()){
					if(imageElement.width>widthMax&&
							imageElement.align!=ImageAlign.IMAGE_ALIGN_CENTER){
						return widthMax+boundary;
					}else{
						if(imageElement.align==ImageAlign.IMAGE_ALIGN_TEXT){
							return imageElement.width;
						}
						return (imageElement.width+boundary);
					}
				}else{
					if(imageElement.align!=ImageAlign.IMAGE_ALIGN_CENTER){
						return widthMax+boundary;
					}else{
						return getTextAreaWidth();
					}
				}
			}
			Size maxSize;
			if(!imageElement.IsCover){
				maxSize = new Size(widthMax, getTextAreaHeight());
			}else{
				maxSize = getTextAreaSize();
			}
			final Size size = getContext().imageSize(
				imageElement.ImageData,
				maxSize,
				imageElement.IsCover
					? ZLPaintContext.ScalingType.FitMaximum
					: ZLPaintContext.ScalingType.IntegerCoefficient
			);
			if(imageElement.IsCover){
				return size != null ? size.Width: 0;
			}
			if(size!=null){
				if(imageElement.align==ImageAlign.IMAGE_ALIGN_TEXT){
					return size.Width;
				}else{
					if(size.Width>widthMax){
						return widthMax+boundary;
					}else{
						return size.Width+boundary;
					}
				}
			}else{
				return 0;
			}
			
//			return size != null ? (size.Width +2*getImageBoundary()): 0;
//			return size != null ? (int)(size.Width*1.15) : 0;//edit by jiangxubo for make sure display the image center hornize
		} else if (element == ZLTextElement.Indent) {
			return myTextStyle.getFirstLineIndentDelta();
		} else if (element instanceof ZLTextFixedHSpaceElement) {
			return getContext().getSpaceWidth() * ((ZLTextFixedHSpaceElement)element).Length;
		}
		return 0;
	}

	int getImageBoundary(){
		return getContext().getDescent();
	}
	final int getElementHeight(ZLTextElement element) {
		if (element instanceof ZLTextWord) {
			return getWordHeight();
		} else if (element instanceof ZLTextImageElement) {
			final ZLTextImageElement imageElement = (ZLTextImageElement)element;
			int boundary = 2*getImageBoundary();
			if(imageElement.align==ImageAlign.IMAGE_ALIGN_TEXT){
				boundary = 0;
			}
			int widthMax = getPictureWidthMax(imageElement.align);
			if(imageElement.height>0){
				if(imageElement.height+boundary<getTextAreaHeight()){
					if(imageElement.align==ImageAlign.IMAGE_ALIGN_TEXT){
						return imageElement.height;
					}
					return (imageElement.height+boundary);
				}else{
					return getTextAreaHeight();
				}
			}
			Size maxSize;
			if(!imageElement.IsCover){
				maxSize = new Size(widthMax, getTextAreaHeight());
			}else{
				maxSize = getTextAreaSize();
			}
			final ZLPaintContext.Size size = getContext().imageSize(
				imageElement.ImageData,
				maxSize,
				imageElement.IsCover
					? ZLPaintContext.ScalingType.FitMaximum
					: ZLPaintContext.ScalingType.IntegerCoefficient
			);
			if(imageElement.IsCover){
				return size != null ? size.Height: 0;
			}
			if(size!=null){
				if(imageElement.align==ImageAlign.IMAGE_ALIGN_TEXT){
					return size.Height;
				}else{
					if(size.Height+boundary>getTextAreaHeight()){
						return getTextAreaHeight();
					}else{
						return size.Height+boundary;
					}
				}
			}else{
				return 0;
			}
//			return size != null ? (size.Height+2*getImageBoundary()+myContext.getDescent()+1): 0;
//			return (size != null ? size.Height : 0) +
//				Math.max(myContext.getStringHeight() * (myTextStyle.getLineSpacePercent() - 100) / 100, 3);
		}
		return 0;
	}

	final int getElementDescent(ZLTextElement element) {
		return element instanceof ZLTextWord ? getContext().getDescent() : 0;
	}

	final int getWordWidth(ZLTextWord word, int start) {
		return
			start == 0 ?
				word.getWidth(getContext()) :
				getContext().getStringWidth(word.Data, word.Offset + start, word.Length - start);
	}

	final int getWordWidth(ZLTextWord word, int start, int length) {
		return getContext().getStringWidth(word.Data, word.Offset + start, length);
	}

	private char[] myWordPartArray = new char[20];

	final int getWordWidth(ZLTextWord word, int start, int length, boolean addHyphenationSign) {
		if (length == -1) {
			if (start == 0) {
				return word.getWidth(getContext());
			}
			length = word.Length - start;
		}
		if (!addHyphenationSign) {
			return getContext().getStringWidth(word.Data, word.Offset + start, length);
		}
		char[] part = myWordPartArray;
		if (length + 1 > part.length) {
			part = new char[length + 1];
			myWordPartArray = part;
		}
		System.arraycopy(word.Data, word.Offset + start, part, 0, length);
		part[length] = '-';
		return getContext().getStringWidth(part, 0, length + 1);
	}

	int getAreaLength(ZLTextParagraphCursor paragraph, ZLTextElementArea area, int toCharIndex) {
		setTextStyle(area.Style);
		final ZLTextWord word = (ZLTextWord)paragraph.getElement(area.ElementIndex);
		int length = toCharIndex - area.CharIndex;
		boolean selectHyphenationSign = false;
		if (length >= area.Length) {
			selectHyphenationSign = area.AddHyphenationSign;
			length = area.Length;
		}
		if (length > 0) {
			return getWordWidth(word, area.CharIndex, length, selectHyphenationSign);
		}
		return 0;
	}

	final void drawWord(int x, int y, ZLTextWord word, int start, int length, boolean addHyphenationSign, ZLColor color) {
		final ZLPaintContext context = getContext();
		context.setTextColor(color);
		if (start == 0 && length == -1) {
			drawString(x, y, word.Data, word.Offset, word.Length, word.getMark(), 0);
		} else {
			if (length == -1) {
				length = word.Length - start;
			}
			if (!addHyphenationSign) {
				drawString(x, y, word.Data, word.Offset + start, length, word.getMark(), start);
			} else {
				char[] part = myWordPartArray;
				if (length + 1 > part.length) {
					part = new char[length + 1];
					myWordPartArray = part;
				}
				System.arraycopy(word.Data, word.Offset + start, part, 0, length);
				part[length] = '-';
				drawString(x, y, part, 0, length + 1, word.getMark(), start);
			}
		}
	}

	private final void drawString(int x, int y, char[] str, int offset, int length, ZLTextWord.Mark mark, int shift) {
		final ZLPaintContext context = getContext();
		if (mark == null) {
			context.drawString(x, y, str, offset, length);
		} else {
			int pos = 0;
			for (; (mark != null) && (pos < length); mark = mark.getNext()) {
				int markStart = mark.Start - shift;
				int markLen = mark.Length;

				if (markStart < pos) {
					markLen += markStart - pos;
					markStart = pos;
				}

				if (markLen <= 0) {
					continue;
				}

				if (markStart > pos) {
					int endPos = Math.min(markStart, length);
					context.drawString(x, y, str, offset + pos, endPos - pos);
					x += context.getStringWidth(str, offset + pos, endPos - pos);
				}

				if (markStart < length) {
					context.setFillColor(getHighlightingBackgroundColor());
					int endPos = Math.min(markStart + markLen, length);
					final int endX = x + context.getStringWidth(str, offset + markStart, endPos - markStart);
					context.fillRectangle(x, y - context.getStringHeight(), endX - 1, y + context.getDescent());
					context.drawString(x, y, str, offset + markStart, endPos - markStart);
					x = endX;
				}
				pos = markStart + markLen;
			}

			if (pos < length) {
				context.drawString(x, y, str, offset + pos, length - pos);
			}
		}
	}
}
