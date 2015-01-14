/*
 * Copyright (C) 2010-2013 Geometer Plus <contact@geometerplus.com>
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

package com.media.android.dbstarplayer.network;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;

import com.media.zlibrary.core.resources.ZLResource;

import com.media.dbstarplayer.network.NetworkLibrary;

import com.media.android.dbstarplayer.api.PluginApi;

public class AddCatalogMenuActivity extends MenuActivity {
	private final ZLResource myResource =
		NetworkLibrary.Instance().resource().getResource("addCatalog");

	private void addItem(String id, int weight) {
		myInfos.add(new PluginApi.MenuActionInfo(
			Uri.parse("http://" + id),
			myResource.getResource(id).getValue(),
			weight
		));
	}

	@Override
	protected void init() {
		setTitle(myResource.getResource("title").getValue());
		addItem("editUrl", 1);
		//addItem("scanLocalNetwork", 2);
	}

	@Override
	protected String getAction() {
		return Util.ADD_CATALOG_ACTION;
	}

	@Override
	protected void runItem(final PluginApi.MenuActionInfo info) {
		try {
			startActivity(new Intent(getAction(), info.getId()));
		} catch (ActivityNotFoundException e) {
		}
		finish();
	}
}
