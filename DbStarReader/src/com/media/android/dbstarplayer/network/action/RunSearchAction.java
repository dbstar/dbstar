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
import android.os.Bundle;

import com.media.dbstarplayer.tree.FBTree;
import com.media.dbstarplayer.network.NetworkLibrary;
import com.media.dbstarplayer.network.NetworkTree;
import com.media.dbstarplayer.network.tree.SearchCatalogTree;

import com.media.android.dbstarplayer.R;

import com.media.android.dbstarplayer.network.NetworkLibraryActivity;

public class RunSearchAction extends Action {
	public static SearchCatalogTree getSearchTree(FBTree tree) {
		for (; tree != null; tree = tree.Parent) {
			for (FBTree t : tree.subTrees()) {
				if (t instanceof SearchCatalogTree) {
					return (SearchCatalogTree)t;
				}
			}
		}
		return null;
	}

	private final boolean myFromContextMenu;

	public RunSearchAction(Activity activity, boolean fromContextMenu) {
		super(activity, ActionCode.SEARCH, "networkSearch", R.drawable.ic_menu_search);
		myFromContextMenu = fromContextMenu;
	}

	@Override
	public boolean isVisible(NetworkTree tree) {
		if (myFromContextMenu) {
			return tree instanceof SearchCatalogTree;
		} else {
			return getSearchTree(tree) != null;
		}
	}

	@Override
	public boolean isEnabled(NetworkTree tree) {
		return NetworkLibrary.Instance().getStoredLoader(getSearchTree(tree)) == null;
	}

	@Override
	public void run(NetworkTree tree) {
		final Bundle bundle = new Bundle();
		bundle.putSerializable(
			NetworkLibraryActivity.TREE_KEY_KEY,
			getSearchTree(tree).getUniqueKey()
		);
		final NetworkLibrary library = NetworkLibrary.Instance();
		myActivity.startSearch(library.NetworkSearchPatternOption.getValue(), true, bundle, false);
	}
}
