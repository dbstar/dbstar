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

package com.media.zlibrary.core.application;

import java.util.*;

import android.view.KeyEvent;
import com.media.dbstarplayer.dbstarplayer.ActionCode;
import com.media.dbstarplayer.Paths;

import com.media.player.common.Utils;
import com.media.zlibrary.core.options.ZLBooleanOption;
import com.media.zlibrary.core.options.ZLStringOption;
import com.media.zlibrary.core.options.ZLStringListOption;
import com.media.zlibrary.core.filesystem.ZLFile;
import com.media.zlibrary.core.xml.ZLStringMap;
import com.media.zlibrary.core.xml.ZLXMLReaderAdapter;

public final class ZLKeyBindings {
	private static final String ACTION = "Action";
	private static final String LONG_PRESS_ACTION = "LongPressAction";

	private final String myName;
	private final ZLStringListOption myKeysOption;
	private final TreeMap<Integer,ZLStringOption> myActionMap = new TreeMap<Integer,ZLStringOption>();
	private final TreeMap<Integer,ZLStringOption> myLongPressActionMap = new TreeMap<Integer,ZLStringOption>();

	public ZLKeyBindings(String name) {
		myName = name;
		final Set<String> keys = new TreeSet<String>();
		try{
			if(new Reader(keys).readQuietly(ZLFile.createFileByPath("default/keymap.xml"))){
				Utils.printLogInfo(getClass().getSimpleName(), "read Key map file successful by path="+"default/keymap.xml");
			}else if(new Reader(keys).readQuietly(ZLFile.createFileByPath(Paths.systemShareDirectory() + "/keymap.xml"))){
				Utils.printLogInfo(getClass().getSimpleName(), "read Key map file successful by path="+Paths.systemShareDirectory() + "/keymap.xml");
			}else if(new Reader(keys).readQuietly(ZLFile.createFileByPath(Paths.mainBookDirectory() + "/keymap.xml"))){
				Utils.printLogInfo(getClass().getSimpleName(), "read Key map file successful by path="+Paths.mainBookDirectory() + "/keymap.xml");
			}else{
				Utils.printLogError(getClass().getSimpleName(), "can't find key map file");
			}
		}catch (Exception e) {
			// handle exception
			Utils.printLogError(getClass().getSimpleName(), "catch read keymap file exception ="+e.getMessage());
			e.printStackTrace();
		}
 		myKeysOption = new ZLStringListOption(name, "KeyList", new ArrayList<String>(keys), ",");

		// this code is for migration from DbStarPlayer versions <= 1.1.2
		ZLStringOption oldBackKeyOption = new ZLStringOption(myName + ":" + ACTION, "<Back>", "");
		if (!"".equals(oldBackKeyOption.getValue())) {
			bindKey(KeyEvent.KEYCODE_BACK, false, oldBackKeyOption.getValue());
			oldBackKeyOption.setValue("");
		}
		oldBackKeyOption = new ZLStringOption(myName + ":" + LONG_PRESS_ACTION, "<Back>", "");
		if (!"".equals(oldBackKeyOption.getValue())) {
			bindKey(KeyEvent.KEYCODE_BACK, true, oldBackKeyOption.getValue());
			oldBackKeyOption.setValue("");
		}

		final ZLBooleanOption volumeKeysOption =
			new ZLBooleanOption("Scrolling", "VolumeKeys", true);
		final ZLBooleanOption invertVolumeKeysOption =
			new ZLBooleanOption("Scrolling", "InvertVolumeKeys", false);
		if (!volumeKeysOption.getValue()) {
			bindKey(KeyEvent.KEYCODE_VOLUME_UP, false, ZLApplication.NoAction);
			bindKey(KeyEvent.KEYCODE_VOLUME_DOWN, false, ZLApplication.NoAction);
		} else if (invertVolumeKeysOption.getValue()) {
			bindKey(KeyEvent.KEYCODE_VOLUME_UP, false, ActionCode.VOLUME_KEY_SCROLL_FORWARD);
			bindKey(KeyEvent.KEYCODE_VOLUME_DOWN, false, ActionCode.VOLUME_KEY_SCROLL_BACK);
		}
		volumeKeysOption.setValue(true);
		invertVolumeKeysOption.setValue(false);
		// end of migration code
	}

	private ZLStringOption createOption(int key, boolean longPress, String defaultValue) {
		final String group = myName + ":" + (longPress ? LONG_PRESS_ACTION : ACTION);
		return new ZLStringOption(group, String.valueOf(key), defaultValue);
	}

	public ZLStringOption getOption(int key, boolean longPress) {
		final TreeMap<Integer,ZLStringOption> map = longPress ? myLongPressActionMap : myActionMap;
		ZLStringOption option = map.get(key);
		if (option == null) {
			option = createOption(key, longPress, ZLApplication.NoAction);
			map.put(key, option);
		}
		return option;
	}

	public void bindKey(int key, boolean longPress, String actionId) {
		final String stringKey = String.valueOf(key);
		List<String> keys = myKeysOption.getValue();
		if (!keys.contains(stringKey)) {
			keys = new ArrayList<String>(keys);
			keys.add(stringKey);
			Collections.sort(keys);
			myKeysOption.setValue(keys);
		}
		getOption(key, longPress).setValue(actionId);
	}

	public String getBinding(int key, boolean longPress) {
		return getOption(key, longPress).getValue();
	}

	private class Reader extends ZLXMLReaderAdapter {
		private final Set<String> myKeySet;

		Reader(Set<String> keySet) {
			myKeySet = keySet;
		}

		@Override
		public boolean dontCacheAttributeValues() {
			return true;
		}

		@Override
		public boolean startElementHandler(String tag, ZLStringMap attributes) {
			if ("binding".equals(tag)) {
				final String stringKey = attributes.getValue("key");
				final String actionId = attributes.getValue("action");
				if (stringKey != null && actionId != null) {
					try {
						final int key = Integer.parseInt(stringKey);
						myKeySet.add(stringKey);
						myActionMap.put(key, createOption(key, false, actionId));
					} catch (NumberFormatException e) {
					}
				}
			}
			return false;
		}
	}
}
