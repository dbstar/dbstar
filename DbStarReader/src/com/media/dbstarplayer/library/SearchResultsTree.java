/*
 * Copyright (C) 2009-2013 Geometer Plus <contact@geometerplus.com>
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

package com.media.dbstarplayer.library;

import com.media.zlibrary.core.resources.ZLResource;

import com.media.dbstarplayer.book.*;

public class SearchResultsTree extends FilteredTree {
	public final String Pattern;
	private final String myId;
	private final ZLResource myResource;

	SearchResultsTree(RootTree root, String id, String pattern) {
		super(root, new Filter.ByPattern(pattern), 0);
		myId = id;
		myResource = resource().getResource(myId);
		Pattern = pattern != null ? pattern : "";
	}

	@Override
	public String getName() {
		return myResource.getValue();
	}

	@Override
	public String getTreeTitle() {
		return getSummary();
	}

	@Override
	protected String getStringId() {
		return myId;
	}

	@Override
	public boolean isSelectable() {
		return false;
	}

	@Override
	public String getSummary() {
		return myResource.getResource("summary").getValue().replace("%s", Pattern);
	}

	@Override
	protected boolean createSubTree(Book book) {
		return createBookWithAuthorsSubTree(book);
	}
}
