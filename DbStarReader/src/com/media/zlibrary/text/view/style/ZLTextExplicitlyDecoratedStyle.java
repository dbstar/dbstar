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

import com.media.zlibrary.core.util.ZLBoolean3;

import com.media.zlibrary.text.model.*;
import com.media.zlibrary.text.view.ZLTextStyle;

public class ZLTextExplicitlyDecoratedStyle extends ZLTextDecoratedStyle implements ZLTextStyleEntry.Feature, ZLTextStyleEntry.FontModifier {
	private final ZLTextStyleEntry myEntry;

	public ZLTextExplicitlyDecoratedStyle(ZLTextStyle base, ZLTextStyleEntry entry) {
		super(base, base.Hyperlink);
		myEntry = entry;
	}

	@Override
	protected String getFontFamilyInternal() {
		if (myEntry.isFeatureSupported(FONT_FAMILY)) {
			// TODO: implement
		}
		return Base.getFontFamily();
	}
	@Override
	protected int getFontSizeInternal(ZLTextMetrics metrics) {
		if (myEntry instanceof ZLTextCSSStyleEntry &&
			!ZLTextStyleCollection.Instance().UseCSSFontSizeOption.getValue()) {
			return Base.getFontSize(metrics);
		}
		if (myEntry.isFeatureSupported(FONT_STYLE_MODIFIER)) {
			if (myEntry.getFontModifier(FONT_MODIFIER_INHERIT) == ZLBoolean3.B3_TRUE) {
				return Base.Base.getFontSize(metrics);
			}
			if (myEntry.getFontModifier(FONT_MODIFIER_LARGER) == ZLBoolean3.B3_TRUE) {
				return Base.Base.getFontSize(metrics) * 120 / 100;
			}
			if (myEntry.getFontModifier(FONT_MODIFIER_SMALLER) == ZLBoolean3.B3_TRUE) {
				return Base.Base.getFontSize(metrics) * 100 / 120;
			}
		}
		if (myEntry.isFeatureSupported(LENGTH_FONT_SIZE)) {
			return myEntry.getLength(LENGTH_FONT_SIZE, metrics);
		}
		return Base.getFontSize(metrics);
	}

	@Override
	protected boolean isBoldInternal() {
		switch (myEntry.getFontModifier(FONT_MODIFIER_BOLD)) {
			case B3_TRUE:
				return true;
			case B3_FALSE:
				return false;
			default:
				return Base.isBold();
		}
	}
	@Override
	protected boolean isItalicInternal() {
		switch (myEntry.getFontModifier(FONT_MODIFIER_ITALIC)) {
			case B3_TRUE:
				return true;
			case B3_FALSE:
				return false;
			default:
				return Base.isItalic();
		}
	}
	@Override
	protected boolean isUnderlineInternal() {
		switch (myEntry.getFontModifier(FONT_MODIFIER_UNDERLINED)) {
			case B3_TRUE:
				return true;
			case B3_FALSE:
				return false;
			default:
				return Base.isUnderline();
		}
	}
	@Override
	protected boolean isStrikeThroughInternal() {
		switch (myEntry.getFontModifier(FONT_MODIFIER_STRIKEDTHROUGH)) {
			case B3_TRUE:
				return true;
			case B3_FALSE:
				return false;
			default:
				return Base.isStrikeThrough();
		}
	}

	public int getLeftIndent() {
		// TODO: implement
		return Base.getLeftIndent();
	}
	public int getRightIndent() {
		// TODO: implement
		return Base.getRightIndent();
	}
	public int getFirstLineIndentDelta() {
		// TODO: implement
		return Base.getFirstLineIndentDelta();
	}
	public int getLineSpacePercent() {
		// TODO: implement
		return Base.getLineSpacePercent();
	}
	@Override
	protected int getVerticalShiftInternal() {
		// TODO: implement
		return Base.getVerticalShift();
	}
	public int getSpaceBefore() {
		// TODO: implement
		return Base.getSpaceBefore();
	}
	public int getSpaceAfter() {
		// TODO: implement
		return Base.getSpaceAfter();
	}
	public byte getAlignment() {
		if (myEntry instanceof ZLTextCSSStyleEntry &&
			!ZLTextStyleCollection.Instance().UseCSSTextAlignmentOption.getValue()) {
			return Base.getAlignment();
		}
		return
			myEntry.isFeatureSupported(ALIGNMENT_TYPE)
				? myEntry.getAlignmentType()
				: Base.getAlignment();
	}

	public boolean allowHyphenations() {
		// TODO: implement
		return Base.allowHyphenations();
	}
}
