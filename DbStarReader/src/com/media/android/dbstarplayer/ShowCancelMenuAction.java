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

import java.util.List;

import android.content.Intent;

import com.media.dbstarplayer.dbstarplayer.DbStarPlayerApp;

class ShowCancelMenuAction extends DbStarAndroidAction {
	ShowCancelMenuAction(DbStarPlayer baseActivity, DbStarPlayerApp dbstarplayerapp) {
		super(baseActivity, dbstarplayerapp);
	}

	@Override
	protected void run(Object ... params) {
		if (!Reader.jumpBack()) {
			final List<DbStarPlayerApp.CancelActionDescription> descriptionList =
				Reader.getCancelActionsList();
			if (descriptionList.size() == 1) {
				Reader.closeWindow();
			} else {
				final Intent intent = new Intent();
				intent.setClass(BaseActivity, CancelActivity.class);
				intent.putExtra(CancelActivity.LIST_SIZE, descriptionList.size());
				int index = 0;
				for (DbStarPlayerApp.CancelActionDescription description : descriptionList) {
					intent.putExtra(CancelActivity.ITEM_TITLE + index, description.Title);
					intent.putExtra(CancelActivity.ITEM_SUMMARY + index, description.Summary);
					++index;
				}
				BaseActivity.startActivityForResult(intent, DbStarPlayer.REQUEST_CANCEL_MENU);
			}
		}
	}
}
