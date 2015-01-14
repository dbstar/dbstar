/*
 * Copyright (C) 2009-2013 Geometer Plus <contact@geometerplus.com>
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

package com.media.android.dbstarplayer;

import android.widget.RelativeLayout;

import com.media.android.dbstarplayer.R;

import com.media.dbstarplayer.dbstarplayer.ActionCode;
import com.media.dbstarplayer.dbstarplayer.DbStarPlayerApp;

final class TextSearchPopup extends ButtonsPopupPanel {
	final static String ID = "TextSearchPopup";

	TextSearchPopup(DbStarPlayerApp dbstarplayer) {
		super(dbstarplayer);
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	protected void hide_() {
		getReader().getTextView().clearFindResults();
		super.hide_();
	}

	@Override
	public void createControlPanel(DbStarPlayer activity, RelativeLayout root) {
		if (myWindow != null && activity == myWindow.getActivity()) {
			return;
		}

		myWindow = new PopupWindow(activity, root, PopupWindow.Location.Bottom);

		addButton(ActionCode.FIND_PREVIOUS, false, R.drawable.text_search_previous);
		addButton(ActionCode.CLEAR_FIND_RESULTS, true, R.drawable.text_search_close);
		addButton(ActionCode.FIND_NEXT, false, R.drawable.text_search_next);
	}
}
