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

package com.media.dbstarplayer.network.opds;

import java.util.List;

import com.media.zlibrary.core.constants.XMLNamespaces;
import com.media.zlibrary.core.util.MimeType;
import com.media.zlibrary.core.util.ZLNetworkUtil;
import com.media.zlibrary.core.xml.ZLStringMap;
import com.media.zlibrary.core.xml.ZLXMLReaderAdapter;

class OpenSearchXMLReader extends ZLXMLReaderAdapter {
	private final List<OpenSearchDescription> myDescriptions;

	private final String myBaseURL;

	public OpenSearchXMLReader(String baseUrl, List<OpenSearchDescription> descriptions) {
		myDescriptions = descriptions;
		myBaseURL = baseUrl;
	}

	@Override
	public boolean processNamespaces() {
		return true;
	}

	private int parseInt(String value) {
		if (value == null || value.length() == 0) {
			return -1;
		}
		try {
			return Integer.valueOf(value);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	private static final int START = 0;
	private static final int DESCRIPTION = 1;

	private static final String TAG_DESCRIPTION = "opensearchdescription";
	private static final String TAG_URL = "url";

	private int myState = START;

	@Override
	public boolean startElementHandler(String tag, ZLStringMap attributes) {
		tag = tag.toLowerCase();

		switch (myState) {
			case START:
				if (testTag(XMLNamespaces.OpenSearch, TAG_DESCRIPTION, tag)) {
					myState = DESCRIPTION;
				}
				break;
			case DESCRIPTION:
				if (testTag(XMLNamespaces.OpenSearch, TAG_URL, tag)) {
					final MimeType mime = MimeType.get(attributes.getValue("type"));
					final String rel = attributes.getValue("rel");
					if ((MimeType.APP_ATOM_XML.weakEquals(mime) || MimeType.TEXT_HTML.weakEquals(mime)) &&
						(rel == null || rel == "results")) {
						final String tmpl = ZLNetworkUtil.url(myBaseURL, attributes.getValue("template"));
						final int indexOffset = parseInt(attributes.getValue("indexOffset"));
						final int pageOffset = parseInt(attributes.getValue("pageOffset"));
						final OpenSearchDescription descr =
							new OpenSearchDescription(tmpl, indexOffset, pageOffset, mime);
						if (descr.isValid()) {
							myDescriptions.add(0, descr);
						}
					}
				}
				break;
		}

		return false;
	}

	@Override
	public boolean endElementHandler(String tag) {
		tag = tag.toLowerCase();
		switch (myState) {
			case DESCRIPTION:
				if (testTag(XMLNamespaces.OpenSearch, TAG_DESCRIPTION, tag)) {
					myState = START;
				}
				break;
		}
		return false;
	}
}
