/*
 * Copyright (C) 2011-2013 Geometer Plus <contact@geometerplus.com>
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

package com.media.dbstarplayer.formats.oeb;

import java.util.Collections;
import java.util.List;

import com.media.zlibrary.core.encodings.EncodingCollection;
import com.media.zlibrary.core.encodings.AutoEncodingCollection;

import com.media.dbstarplayer.book.Book;
import com.media.dbstarplayer.bookmodel.BookModel;
import com.media.dbstarplayer.bookmodel.BookReadingException;
import com.media.dbstarplayer.formats.NativeFormatPlugin;

public class OEBNativePlugin extends NativeFormatPlugin {
	public OEBNativePlugin() {
		super("ePub");
	}

	@Override
	public void readModel(BookModel model) throws BookReadingException {
		super.readModel(model);
		model.setLabelResolver(new BookModel.LabelResolver() {
			public List<String> getCandidates(String id) {
				final int index = id.indexOf("#");
				return index > 0
					? Collections.<String>singletonList(id.substring(0, index))
					: Collections.<String>emptyList();
			}
		});
	}

	@Override
	public EncodingCollection supportedEncodings() {
		return new AutoEncodingCollection();
	}

	@Override
	public void detectLanguageAndEncoding(Book book) {
		book.setEncoding("auto");
	}
}
