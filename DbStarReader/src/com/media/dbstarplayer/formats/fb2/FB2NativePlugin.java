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

package com.media.dbstarplayer.formats.fb2;

import com.media.zlibrary.core.encodings.EncodingCollection;
import com.media.zlibrary.core.encodings.AutoEncodingCollection;

import com.media.zlibrary.core.filesystem.ZLFile;

import com.media.dbstarplayer.book.Book;
import com.media.dbstarplayer.bookmodel.BookReadingException;
import com.media.dbstarplayer.formats.NativeFormatPlugin;

public class FB2NativePlugin extends NativeFormatPlugin {
	public FB2NativePlugin() {
		super("fb2");
	}

	@Override
	public ZLFile realBookFile(ZLFile file) throws BookReadingException {
		final ZLFile realFile = FB2Util.getRealFB2File(file);
		if (realFile == null) {
			throw new BookReadingException("incorrectFb2ZipFile", file);
		}
		return realFile;
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
