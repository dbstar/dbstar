/*
 * Copyright (C) 2007-2012 Geometer Plus <contact@geometerplus.com>
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

import com.media.android.dbstarplayer.DbStarPlayer;
import com.media.reader.dialog.CustomDialog;
import com.media.reader.dialog.DialogBaseLayout;

public class PopViewAction extends DbStarAction {
	private DialogBaseLayout dialogBase = null;
	private DbStarPlayer activity;
	private DbStarPlayerApp mReader;
	public PopViewAction(DbStarPlayer activity,DbStarPlayerApp dbstarplayer, boolean forward) {
		super(dbstarplayer);
		this.activity = activity;
		mReader = dbstarplayer;
	}

	@Override
	public void run(Object ... params) {
		if(dialogBase==null){
			dialogBase = new CustomDialog(activity,mReader);
		}
		((CustomDialog) dialogBase).show();
	}
}
