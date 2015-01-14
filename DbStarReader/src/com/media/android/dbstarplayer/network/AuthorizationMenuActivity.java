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

import android.app.Activity;
import android.content.*;
import android.net.Uri;

import com.media.dbstarplayer.network.INetworkLink;
import com.media.dbstarplayer.network.NetworkLibrary;
import com.media.dbstarplayer.network.urlInfo.UrlInfo;
import com.media.dbstarplayer.network.authentication.NetworkAuthenticationManager;

import com.media.android.util.PackageUtil;

import com.media.android.dbstarplayer.api.PluginApi;

public class AuthorizationMenuActivity extends MenuActivity {
	public static void runMenu(Context context, INetworkLink link) {
		context.startActivity(
			Util.intentByLink(new Intent(context, AuthorizationMenuActivity.class), link)
		);
	}

	public static void runMenu(Activity activity, INetworkLink link, int code) {
		activity.startActivityForResult(
			Util.intentByLink(new Intent(activity, AuthorizationMenuActivity.class), link), code
		);
	}

	private INetworkLink myLink;

	@Override
	protected void init() {
		setTitle(NetworkLibrary.resource().getResource("authorizationMenuTitle").getValue());
		final String url = getIntent().getData().toString();
		myLink = NetworkLibrary.Instance().getLinkByUrl(url);

		if (myLink.getUrlInfo(UrlInfo.Type.SignIn) != null) {
			myInfos.add(new PluginApi.MenuActionInfo(
				Uri.parse(url + "/signIn"),
				NetworkLibrary.resource().getResource("signIn").getValue(),
				0
			));
		}
	}

	@Override
	protected String getAction() {
		return Util.AUTHORIZATION_ACTION;
	}

	@Override
	protected void runItem(final PluginApi.MenuActionInfo info) {
		try {
			final NetworkAuthenticationManager mgr = myLink.authenticationManager();
			if (info.getId().toString().endsWith("/signIn")) {
				Util.runAuthenticationDialog(AuthorizationMenuActivity.this, myLink, null);
			} else {
				final Intent intent = Util.authorizationIntent(myLink, info.getId());
				if (PackageUtil.canBeStarted(AuthorizationMenuActivity.this, intent, true)) {
					startActivity(intent);
				}
			}
		} catch (Exception e) {
			// do nothing
		}
	}
}
