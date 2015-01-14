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

package com.media.android.dbstarplayer.network.action;

import android.app.Activity;
import android.content.Intent;

import com.media.dbstarplayer.network.NetworkTree;
import com.media.dbstarplayer.network.ICustomNetworkLink;
import com.media.dbstarplayer.network.tree.NetworkCatalogRootTree;

import com.media.android.dbstarplayer.network.Util;
import com.media.android.dbstarplayer.network.AddCustomCatalogActivity;

public class EditCustomCatalogAction extends CatalogAction {
	public EditCustomCatalogAction(Activity activity) {
		super(activity, ActionCode.CUSTOM_CATALOG_EDIT, "editCustomCatalog");
	}

	@Override
	public boolean isVisible(NetworkTree tree) {
		return
			tree instanceof NetworkCatalogRootTree &&
			tree.getLink() instanceof ICustomNetworkLink;
	}

	@Override
	public void run(NetworkTree tree) {
		final Intent intent = new Intent(myActivity, AddCustomCatalogActivity.class);
		Util.intentByLink(intent, tree.getLink());
		intent.setAction(Util.EDIT_CATALOG_ACTION);
		myActivity.startActivity(intent);
	}
}
