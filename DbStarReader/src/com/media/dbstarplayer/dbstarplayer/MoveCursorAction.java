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

package com.media.dbstarplayer.dbstarplayer;

import com.media.zlibrary.text.view.ZLTextRegion;
import com.media.zlibrary.text.view.ZLTextWordRegionSoul;

class MoveCursorAction extends DbStarAction {
	private final DbStarView.Direction myDirection;

	MoveCursorAction(DbStarPlayerApp dbstarplayer, DbStarView.Direction direction) {
		super(dbstarplayer);
		myDirection = direction;
	}

	@Override
	protected void run(Object ... params) {
		final DbStarView fbView = Reader.getTextView();
		ZLTextRegion region = fbView.getSelectedRegion();
		final ZLTextRegion.Filter filter =
			(region != null && region.getSoul() instanceof ZLTextWordRegionSoul)
				|| Reader.NavigateAllWordsOption.getValue()
					? ZLTextRegion.AnyRegionFilter : ZLTextRegion.ImageOrHyperlinkFilter;
		region = fbView.nextRegion(myDirection, filter);
		if (region != null) {
			fbView.selectRegion(region);
		} else {
			switch (myDirection) {
				case down:
					fbView.scrollPage(true, DbStarView.ScrollingMode.SCROLL_LINES, 1);
					break;
				case up:
					fbView.scrollPage(false, DbStarView.ScrollingMode.SCROLL_LINES, 1);
					break;
			}
		}

		Reader.getViewWidget().reset();
		Reader.getViewWidget().repaint();
	}
}
