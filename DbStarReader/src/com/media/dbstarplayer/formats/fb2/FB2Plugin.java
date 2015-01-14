/*
 * Copyright (C) 2007-2013 Geometer Plus <contact@geometerplus.com>
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

import com.media.zlibrary.core.filesystem.ZLFile;
import com.media.zlibrary.core.encodings.AutoEncodingCollection;
import com.media.zlibrary.core.image.ZLImage;

import com.media.dbstarplayer.book.Book;
import com.media.dbstarplayer.bookmodel.BookModel;
import com.media.dbstarplayer.bookmodel.BookReadingException;
import com.media.dbstarplayer.formats.JavaFormatPlugin;

public class FB2Plugin extends JavaFormatPlugin {
	public FB2Plugin() {
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
	public void readMetaInfo(Book book) throws BookReadingException {
		new FB2MetaInfoReader(book).readMetaInfo();
	}

	@Override
	public void readUids(Book book) throws BookReadingException {
		// this method does nothing, we expect it will be never called
	}

	@Override
	public void readModel(BookModel model) throws BookReadingException {
		new FB2Reader(model).readBook();
	}

	@Override
	public ZLImage readCover(ZLFile file) {
		return new FB2CoverReader().readCover(file);
	}

	@Override
	public String readAnnotation(ZLFile file) {
		return new FB2AnnotationReader().readAnnotation(file);
	}

	@Override
	public AutoEncodingCollection supportedEncodings() {
		return new AutoEncodingCollection();
	}

	@Override
	public void detectLanguageAndEncoding(Book book) {
		book.setEncoding("auto");
	}
}
