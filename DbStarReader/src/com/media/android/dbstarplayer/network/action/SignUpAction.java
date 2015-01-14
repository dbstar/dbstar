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

import com.media.dbstarplayer.network.INetworkLink;
import com.media.dbstarplayer.network.NetworkTree;
import com.media.dbstarplayer.network.tree.NetworkCatalogRootTree;
import com.media.dbstarplayer.network.authentication.NetworkAuthenticationManager;

import com.media.android.dbstarplayer.network.Util;

public class SignUpAction extends Action {
	public SignUpAction(Activity activity) {
		super(activity, ActionCode.SIGNUP, "signUp", -1);
	}

	@Override
	public boolean isVisible(NetworkTree tree) {
		if (!(tree instanceof NetworkCatalogRootTree)) {
			return false;
		}

		final INetworkLink link = tree.getLink();
		final NetworkAuthenticationManager mgr = link.authenticationManager();
		return
			mgr != null &&
			!mgr.mayBeAuthorised(false) &&
			Util.isRegistrationSupported(myActivity, link);
	}

	@Override
	public void run(NetworkTree tree) {
		Util.runRegistrationDialog(myActivity, tree.getLink());
	}
}
