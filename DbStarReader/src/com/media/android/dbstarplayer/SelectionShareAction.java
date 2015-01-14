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

package com.media.android.dbstarplayer;

import android.content.Intent;

import com.media.zlibrary.core.resources.ZLResource;

import com.media.dbstarplayer.dbstarplayer.DbStarPlayerApp;

public class SelectionShareAction extends DbStarAndroidAction {
	SelectionShareAction(DbStarPlayer baseActivity, DbStarPlayerApp dbstarplayerapp) {
		super(baseActivity, dbstarplayerapp);
	}

	@Override
	protected void run(Object ... params) {
		final String text = Reader.getTextView().getSelectedText();
		final String title = Reader.Model.Book.getTitle();
		Reader.getTextView().clearSelection();

		final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(android.content.Intent.EXTRA_SUBJECT,
			ZLResource.resource("selection").getResource("quoteFrom").getValue().replace("%s", title)
		);
		intent.putExtra(android.content.Intent.EXTRA_TEXT, text);
		BaseActivity.startActivity(Intent.createChooser(intent, null));
	}
}
