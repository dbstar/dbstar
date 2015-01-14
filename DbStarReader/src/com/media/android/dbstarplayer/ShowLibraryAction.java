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

import android.content.ActivityNotFoundException;
import android.content.Intent;

import com.media.dbstarplayer.book.SerializerUtil;
import com.media.dbstarplayer.dbstarplayer.DbStarPlayerApp;

import com.media.android.dbstarplayer.library.LibraryActivity;
import com.media.android.util.PackageUtil;

class ShowLibraryAction extends DbStarAndroidAction {
	ShowLibraryAction(DbStarPlayer baseActivity, DbStarPlayerApp dbstarplayerapp) {
		super(baseActivity, dbstarplayerapp);
	}

	@Override
	protected void run(Object ... params) {
		final Intent externalIntent =
			new Intent("android.dbstarplayer.action.EXTERNAL_LIBRARY");
		final Intent internalIntent =
			new Intent(BaseActivity.getApplicationContext(), LibraryActivity.class);
		if (PackageUtil.canBeStarted(BaseActivity, externalIntent, true)) {
			try {
				startLibraryActivity(externalIntent);
			} catch (ActivityNotFoundException e) {
				startLibraryActivity(internalIntent);
			}
		} else {
			startLibraryActivity(internalIntent);
		}
	}

	private void startLibraryActivity(Intent intent) {
		if (Reader.Model != null) {
			intent.putExtra(DbStarPlayer.BOOK_KEY, SerializerUtil.serialize(Reader.Model.Book));
		}
		OrientationUtil.startActivity(BaseActivity, intent);
	}
}
