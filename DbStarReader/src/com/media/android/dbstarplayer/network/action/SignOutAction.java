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

import com.media.dbstarplayer.network.NetworkLibrary;
import com.media.dbstarplayer.network.NetworkTree;
import com.media.dbstarplayer.network.tree.NetworkCatalogRootTree;
import com.media.dbstarplayer.network.authentication.NetworkAuthenticationManager;

import com.media.android.util.UIUtil;

public class SignOutAction extends Action {
	public SignOutAction(Activity activity) {
		super(activity, ActionCode.SIGNOUT, "signOut", -1);
	}

	@Override
	public boolean isVisible(NetworkTree tree) {
		if (!(tree instanceof NetworkCatalogRootTree)) {
			return false;
		}

		final NetworkAuthenticationManager mgr = tree.getLink().authenticationManager();
		return mgr != null && mgr.mayBeAuthorised(false);
	}

	@Override
	public void run(NetworkTree tree) {
		final NetworkAuthenticationManager mgr = tree.getLink().authenticationManager();
		final Runnable runnable = new Runnable() {
			public void run() {
				if (mgr.mayBeAuthorised(false)) {
					mgr.logOut();
					myActivity.runOnUiThread(new Runnable() {
						public void run() {
							final NetworkLibrary library = NetworkLibrary.Instance();
							library.invalidateVisibility();
							library.synchronize();
						}
					});
				}
			}
		};
		UIUtil.wait("signOut", runnable, myActivity);
	}

	@Override
	public String getOptionsLabel(NetworkTree tree) {
		final NetworkAuthenticationManager mgr = tree.getLink().authenticationManager();
		final String userName =
			mgr != null && mgr.mayBeAuthorised(false) ? mgr.getVisibleUserName() : "";
		return super.getOptionsLabel(tree).replace("%s", userName);
	}

	@Override
	public String getContextLabel(NetworkTree tree) {
		final NetworkAuthenticationManager mgr = tree.getLink().authenticationManager();
		final String userName =
			mgr != null && mgr.mayBeAuthorised(false) ? mgr.getVisibleUserName() : "";
		return super.getContextLabel(tree).replace("%s", userName);
	}
}
