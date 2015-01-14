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

package com.media.zlibrary.text.view.style;

import com.media.player.common.Utils;
import com.media.zlibrary.core.options.ZLBooleanOption;
import com.media.zlibrary.core.options.ZLIntegerRangeOption;
import com.media.zlibrary.core.options.ZLStringOption;
import com.media.zlibrary.text.model.ZLTextAlignmentType;
import com.media.zlibrary.text.model.ZLTextMetrics;
import com.media.zlibrary.text.view.ZLTextHyperlink;
import com.media.zlibrary.text.view.ZLTextStyle;
import com.media.zlibrary.ui.android.library.ZLAndroidLibrary;

public class ZLTextBaseStyle extends ZLTextStyle {
	private static final String GROUP = "Style";
	private static final String OPTIONS = "Options";
    
	/**
	 * Maximum content text size of read page
	 * */
	public static final int CONTENT_TEXT_MAXSIZE = 29;
	/**
	 * Minimum content text size of read page
	 * */
	public static final int CONTENT_TEXT_MINSIZE = 23;
	public final static int DEFAULT_SIZE_DIP=CONTENT_TEXT_MINSIZE;
    public final static int DEFAULT_SIZE = Utils.dip2px(((ZLAndroidLibrary)ZLAndroidLibrary.
    		Instance()).getWidget().getContext(),DEFAULT_SIZE_DIP);

	public final ZLBooleanOption AutoHyphenationOption =
		new ZLBooleanOption(OPTIONS, "AutoHyphenation", true);

	public final ZLBooleanOption BoldOption =
		new ZLBooleanOption(GROUP, "Base:bold", false);
	public final ZLBooleanOption ItalicOption =
		new ZLBooleanOption(GROUP, "Base:italic", false);
	public final ZLBooleanOption UnderlineOption =
		new ZLBooleanOption(GROUP, "Base:underline", false);
	public final ZLBooleanOption StrikeThroughOption =
		new ZLBooleanOption(GROUP, "Base:strikeThrough", false);
	public final ZLIntegerRangeOption AlignmentOption =
		new ZLIntegerRangeOption(GROUP, "Base:alignment", 1, 4, ZLTextAlignmentType.ALIGN_JUSTIFY);
	public final ZLIntegerRangeOption LineSpaceOption =
		new ZLIntegerRangeOption(GROUP, "Base:lineSpacing", 5, 20, 15);

	public final ZLStringOption FontFamilyOption;
	public final ZLIntegerRangeOption FontSizeOption;

	public ZLTextBaseStyle(String fontFamily, int fontSize) {
		super(null, ZLTextHyperlink.NO_LINK);
		FontFamilyOption = new ZLStringOption(GROUP, "Base:fontFamily", fontFamily);
	//	fontSize = fontSize * ZLibrary.Instance().getDisplayDPI() / 320 * 2;
		FontSizeOption = new ZLIntegerRangeOption(GROUP, "Base:fontSize", CONTENT_TEXT_MINSIZE, CONTENT_TEXT_MAXSIZE, DEFAULT_SIZE);
	}

	@Override
	public String getFontFamily() {
		return FontFamilyOption.getValue();
	}

	public int getFontSize() {
		return FontSizeOption.getValue();
	}

	@Override
	public int getFontSize(ZLTextMetrics metrics) {
		return getFontSize();
	}

	@Override
	public boolean isBold() {
		return BoldOption.getValue();
	}

	@Override
	public boolean isItalic() {
		return ItalicOption.getValue();
	}

	@Override
	public boolean isUnderline() {
		return UnderlineOption.getValue();
	}

	@Override
	public boolean isStrikeThrough() {
		return StrikeThroughOption.getValue();
	}

	@Override
	public int getLeftIndent() {
		return 0;
	}

	@Override
	public int getRightIndent() {
		return 0;
	}

	@Override
	public int getFirstLineIndentDelta() {
		return 0;
	}

	@Override
	public int getLineSpacePercent() {
		return LineSpaceOption.getValue() * 10;
	}

	@Override
	public int getVerticalShift() {
		return 0;
	}

	@Override
	public int getSpaceBefore() {
		return 0;
	}

	@Override
	public int getSpaceAfter() {
		return 0;
	}

	@Override
	public byte getAlignment() {
		return (byte)AlignmentOption.getValue();
	}

	@Override
	public boolean allowHyphenations() {
		return true;
	}
}
