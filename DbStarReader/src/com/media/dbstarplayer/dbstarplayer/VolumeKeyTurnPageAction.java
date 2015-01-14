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

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.iflytek.tts.TtsService.Tts;
import com.media.android.dbstarplayer.AudioPlayAction;
import com.media.zlibrary.core.view.ZLView;
import com.media.zlibrary.text.view.ZLTextView;
import com.media.zlibrary.ui.android.library.ZLAndroidLibrary;

class VolumeKeyTurnPageAction extends DbStarAction {
	private final boolean myForward;

	VolumeKeyTurnPageAction(DbStarPlayerApp dbstarplayer, boolean forward) {
		super(dbstarplayer);
		myForward = forward;
	}

	@Override
	protected void run(Object ... params) {
//		final PageTurningOptions preferences = Reader.PageTurningOptions;
//		Reader.getViewWidget().startAnimatedScrolling(
//			myForward ? DbStarView.PageIndex.next : DbStarView.PageIndex.previous,
//			preferences.Horizontal.getValue()
//				? DbStarView.Direction.rightToLeft : DbStarView.Direction.up,
//			preferences.AnimationSpeed.getValue()
//		);
		if(!AudioPlayAction.isAudioSpeek){
			Log.d("VolumeKeyTurnPageAction", "Audio is not playing, do not show volume status,Return");
			return;
		}
		final Context con = ((ZLAndroidLibrary)ZLAndroidLibrary.Instance()).getWidget().getContext();
		if(myForward){
			((AudioManager)con.getSystemService(Service.AUDIO_SERVICE)).
			adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
		}else{
			((AudioManager)con.getSystemService(Service.AUDIO_SERVICE)).
			adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
		}
		final ZLView view= Reader.getCurrentView();
		if(view instanceof ZLTextView){
			((ZLTextView)view).setShowVolumeStatus(true);
		}
		Reader.getViewWidget().reset();
		Reader.getViewWidget().repaint();
	}
}
