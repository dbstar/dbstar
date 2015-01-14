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

import com.media.zlibrary.core.money.Money;

import com.media.dbstarplayer.network.INetworkLink;
import com.media.dbstarplayer.network.NetworkTree;
import com.media.dbstarplayer.network.tree.NetworkCatalogRootTree;
import com.media.dbstarplayer.network.tree.TopUpTree;
import com.media.dbstarplayer.network.authentication.NetworkAuthenticationManager;

import com.media.android.dbstarplayer.network.TopupMenuActivity;

public class TopupAction extends Action {
	public TopupAction(Activity activity) {
		super(activity, ActionCode.TOPUP, "topup", -1);
	}

	@Override
	public boolean isVisible(NetworkTree tree) {
		if (tree instanceof TopUpTree) {
			return true;
		} else if (tree instanceof NetworkCatalogRootTree) {
			final INetworkLink link = tree.getLink();
			final NetworkAuthenticationManager mgr = link.authenticationManager();
			return
				mgr != null &&
				mgr.mayBeAuthorised(false) &&
				mgr.currentAccount() != null &&
				TopupMenuActivity.isTopupSupported(link);
		} else {
			return false;
		}
	}

	@Override
	public void run(NetworkTree tree) {
		final INetworkLink link = tree.getLink();
		if (link != null) {
			TopupMenuActivity.runMenu(myActivity, link, null);
		}
	}

	@Override
	public String getContextLabel(NetworkTree tree) {
		final INetworkLink link = tree.getLink();
		Money account = null;
		if (link != null) {
			final NetworkAuthenticationManager mgr = link.authenticationManager();
			if (mgr != null && mgr.mayBeAuthorised(false)) {
				account = mgr.currentAccount();
			}
		}
		return super.getContextLabel(tree).replace("%s", account != null ? account.toString() : "");
	}
}
