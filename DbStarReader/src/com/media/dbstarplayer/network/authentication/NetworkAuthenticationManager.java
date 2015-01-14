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

package com.media.dbstarplayer.network.authentication;

import java.util.*;

import com.media.zlibrary.core.options.ZLStringOption;
import com.media.zlibrary.core.network.ZLNetworkException;
import com.media.zlibrary.core.money.Money;

import com.media.dbstarplayer.network.*;
import com.media.dbstarplayer.network.opds.OPDSNetworkLink;
import com.media.dbstarplayer.network.authentication.litres.LitResAuthenticationManager;
import com.media.dbstarplayer.network.urlInfo.*;

public abstract class NetworkAuthenticationManager {
	private static final HashMap<String, NetworkAuthenticationManager> ourManagers = new HashMap<String, NetworkAuthenticationManager>();

	public static NetworkAuthenticationManager createManager(INetworkLink link, Class<? extends NetworkAuthenticationManager> managerClass) {
		NetworkAuthenticationManager mgr = ourManagers.get(link.getSiteName());
		if (mgr == null) {
			if (managerClass == LitResAuthenticationManager.class) {
				mgr = new LitResAuthenticationManager((OPDSNetworkLink)link);
			}
			if (mgr != null) {
				ourManagers.put(link.getSiteName(), mgr);
			}
		}
		return mgr;
	}


	public final INetworkLink Link;
	protected final ZLStringOption UserNameOption;

	protected NetworkAuthenticationManager(INetworkLink link) {
		Link = link;
		UserNameOption = new ZLStringOption(link.getSiteName(), "userName", "");
	}

	public String getUserName() {
		return UserNameOption.getValue();
	}

	public String getVisibleUserName() {
		final String username = getUserName();
		return username.startsWith("dbstarplayer-auto-") ? "auto" : username;
	}

	/*
	 * Common manager methods
	 */
	public abstract boolean isAuthorised(boolean useNetwork /* = true */) throws ZLNetworkException;
	public abstract void authorise(String username, String password) throws ZLNetworkException;
	public abstract void logOut();
	public abstract BookUrlInfo downloadReference(NetworkBookItem book);
	public abstract void refreshAccountInformation() throws ZLNetworkException;

	public final boolean mayBeAuthorised(boolean useNetwork) {
		try {
			return isAuthorised(useNetwork);
		} catch (ZLNetworkException e) {
		}
		return true;
	}

	public boolean needsInitialization() {
		return false;
	}

	public void initialize() throws ZLNetworkException {
		throw new ZLNetworkException(NetworkException.ERROR_UNSUPPORTED_OPERATION);
	}

	// returns true if link must be purchased before downloading
	public boolean needPurchase(NetworkBookItem book) {
		return true;
	}

	public void purchaseBook(NetworkBookItem book) throws ZLNetworkException {
		throw new ZLNetworkException(NetworkException.ERROR_UNSUPPORTED_OPERATION);
	}

	public List<NetworkBookItem> purchasedBooks() {
		return Collections.emptyList();
	}

	public Money currentAccount() {
		return null;
	}

	/*
	 * topup account
	 */

	public String topupLink(Money sum) {
		return null;
	}
	public Map<String,String> getTopupData() {
		return Collections.emptyMap();
	}

	/*
	 * Password Recovery
	 */
	public boolean passwordRecoverySupported() {
		return false;
	}

	public void recoverPassword(String email) throws ZLNetworkException {
		throw new ZLNetworkException(NetworkException.ERROR_UNSUPPORTED_OPERATION);
	}
}
