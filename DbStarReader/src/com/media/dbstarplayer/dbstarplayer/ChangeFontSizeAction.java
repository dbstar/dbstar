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

import com.iflytek.tts.TtsService.Tts;
import com.media.android.dbstarplayer.AudioPlayAction;
import com.media.zlibrary.core.options.ZLIntegerRangeOption;
import com.media.zlibrary.text.view.style.ZLTextStyleCollection;

class ChangeFontSizeAction extends DbStarAction {
	private final int myDelta;

	ChangeFontSizeAction(DbStarPlayerApp dbstarplayer, int delta) {
		super(dbstarplayer);
		myDelta = delta;
	}

	@Override
	protected void run(Object ... params) {
		final ZLIntegerRangeOption option =
			ZLTextStyleCollection.Instance().getBaseStyle().FontSizeOption;
		option.setValue(option.getValue() + myDelta);
		if(AudioPlayAction.isAudioSpeek){
			Reader.runAction(ActionCode.AUDIO_CANCEL);
		}
		Reader.clearTextCaches();
		Reader.getViewWidget().repaint();
	}
}
