package com.settings.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class GDXMLDataAccessor {
	private static final String TAG = "";
	
	private static final String ns = null;
	private static final String TAGPublication = "Publication";
	private static final String TAGVersion = "Version";
	private static final String TAGStandardVersion = "StandardVersion";
	private static final String TAGPublicationID = "PublicationID";
	private static final String TAGPublicationNames = "PublicationNames";
	private static final String TAGPublicationName = "PublicationName";
	private static final String TAGPublicationType = "PublicationType";
	private static final String TAGIsReserved = "IsReserved";
	private static final String TAGVisible = "Visible";
	private static final String TAGDRMFile = "DRMFile";
	private static final String TAGPublicationVA = "PublicationVA";
	private static final String TAGMultipleLanguageInfos = "MultipleLanguageInfos";
	private static final String TAGMultipleLanguageInfo = "MultipleLanguageInfo";
	private static final String TAGPublicationDesc = "PublicationDesc";
	private static final String TAGKeywords = "Keywords";
	private static final String TAGImageDefinition = "ImageDefinition";
	private static final String TAGDirector = "Director";
	private static final String TAGEpisode = "Episode";
	private static final String TAGActor = "Actor";
	private static final String TAGAudioChannel = "AudioChannel";
	private static final String TAGAspectRatio = "AspectRatio";
	private static final String TAGAudience = "Audience";
	private static final String TAGModel = "Model";
	private static final String TAGLanguage = "Language";
	private static final String TAGArea = "Area";
	private static final String TAGSubTitles = "SubTitles";
	private static final String TAGSubTitle = "SubTitle";
	private static final String TAGSubTitleID = "SubTitleID";
	private static final String TAGSubTitleName = "SubTitleName";
	private static final String TAGSubTitleLanguage = "SubTitleLanguage";
	private static final String TAGSubTitleURI = "SubTitleURI";
	private static final String TAGTrailers = "Trailers";
	private static final String TAGTrailer = "Trailer";
	private static final String TAGTrailerID = "TrailerID";
	private static final String TAGTrailerName = "TrailerName";
	private static final String TAGTrailerURI = "TrailerURI";
	private static final String TAGPosters = "Posters";
	private static final String TAGPoster = "Poster";
	private static final String TAGPosterID = "PosterID";
	private static final String TAGPosterName = "PosterName";
	private static final String TAGPosterURI = "PosterURI";
	private static final String TAGExtensions = "Extensions";
	private static final String TAGExtension = "Extension";
	private static final String TAGMFile = "MFile";
	private static final String TAGFileID = "FileID";
	private static final String TAGFileNames = "FileNames";
	private static final String TAGFileName = "FileName";
	private static final String TAGFileType = "FileType";
	private static final String TAGFileSize = "FileSize";
	private static final String TAGDuration = "Duration";
	private static final String TAGFileURI = "FileURI";
	private static final String TAGResolution = "Resolution";
	private static final String TAGBitRate = "BitRate";
	private static final String TAGFileFormat = "FileFormat";
	private static final String TAGCodeFormat = "CodeFormat";

	private static final String AttributeValue = "value";
	private static final String AttributeLanguage = "language";

	String mLocalization;

	public GDXMLDataAccessor(String localization) {
		setLocalization(localization);
	}
	
	
	public void setLocalization(String localization) {
		mLocalization = localization;
	} 
	
	String getLocalization() {
		return mLocalization;
	}
	
	public void parseDetailData(InputStream in, ContentData content) {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			readData(parser, content);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	String readPublicationNames(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		String value = null;
		parser.require(XmlPullParser.START_TAG, ns, TAGPublicationNames);
		value = parseTag(parser, TAGPublicationName);
		parser.require(XmlPullParser.END_TAG, ns, TAGPublicationNames);
//		Log.d(TAG, "readPublicationNames " + value);
		return value;
	}

	String readDRMFile(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		String value = null;
		parser.require(XmlPullParser.START_TAG, ns, TAGDRMFile);
		value = parseTag(parser, TAGFileURI);
		parser.require(XmlPullParser.END_TAG, ns, TAGDRMFile);

//		Log.d(TAG, "readDRMFile " + value);

		return value;
	}

	void readMultipleLanguageInfos(XmlPullParser parser, ContentData content)
			throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, TAGMultipleLanguageInfos);
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
//			Log.d(TAG, "tag 1 " + name + " event = " + parser.getEventType());
			if (name.equals(TAGMultipleLanguageInfo)) {
				readMultipleLanguageInfo(parser, content);
			}
		}
		parser.require(XmlPullParser.END_TAG, ns, TAGMultipleLanguageInfos);
	}

	void readMultipleLanguageInfo(XmlPullParser parser, ContentData content)
			throws IOException, XmlPullParserException {

//		Log.d(TAG, "readMultipleLanguageInfo");
		parser.require(XmlPullParser.START_TAG, ns, TAGMultipleLanguageInfo);
//		Log.d(TAG, "readMultipleLanguageInfo 1");
		String language = parser.getAttributeValue(ns, AttributeLanguage);
//		Log.d(TAG, "language " + language);
		if (language.equals(getLocalization())) {
			while (parser.next() != XmlPullParser.END_TAG) {
				if (parser.getEventType() != XmlPullParser.START_TAG) {
					continue;
				}
				String name = parser.getName();
//				Log.d(TAG, "tag 2 " + name);
				if (name.equals(TAGPublicationDesc)) {
					content.Description = readTag(parser, TAGPublicationDesc);
				} else if (name.equals(TAGKeywords)) {
					content.Keywords = readTag(parser, TAGKeywords);
				} else if (name.equals(TAGImageDefinition)) {
					content.ImageDefinition = readTag(parser,
							TAGImageDefinition);
				} else if (name.equals(TAGDirector)) {
					content.Director = readTag(parser, TAGDirector);
				} else if (name.equals(TAGActor)) {
					content.Actors = readTag(parser, TAGActor);
				} else if (name.equals(TAGAudioChannel)) {
					content.AudioChannel = readTag(parser, TAGAudioChannel);
				} else if (name.equals(TAGAspectRatio)) {
					content.AspectRatio = readTag(parser, TAGAspectRatio);
				} else if (name.equals(TAGAudience)) {
					content.Audience = readTag(parser, TAGAudience);
				} else if (name.equals(TAGModel)) {
					content.Model = readTag(parser, TAGModel);
				} else if (name.equals(TAGLanguage)) {
					content.Language = readTag(parser, TAGLanguage);
				} else if (name.equals(TAGArea)) {
					content.Area = readTag(parser, TAGArea);
				} else {
					skip(parser);
				}
			}
		} else {
			skip(parser);
		}

		parser.require(XmlPullParser.END_TAG, ns, TAGMultipleLanguageInfo);
	}

	void readSubTitles(XmlPullParser parser, ContentData content)
			throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, TAGSubTitles);
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals(TAGSubTitle)) {
				readSubTitle(parser, content);
			}
		}
		parser.require(XmlPullParser.END_TAG, ns, TAGSubTitles);
	}

	void readSubTitle(XmlPullParser parser, ContentData content)
			throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, TAGSubTitle);

		ContentData.SubTitle item = new ContentData.SubTitle();
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals(TAGSubTitleID)) {
				item.Id = readTag(parser, TAGSubTitleID);
			} else if (name.equals(TAGSubTitleName)) {
				item.Name = readTag(parser, TAGSubTitleName);
			} else if (name.equals(TAGSubTitleLanguage)) {
				item.Language = readTag(parser, TAGSubTitleLanguage);
			} else if (name.equals(TAGSubTitleURI)) {
				item.URI = readTag(parser, TAGSubTitleURI);
			} else {
				skip(parser);
			}
		}
		if (content.SubTitles == null) {
			content.SubTitles = new ArrayList<ContentData.SubTitle>();
		}
		content.SubTitles.add(item);

		parser.require(XmlPullParser.END_TAG, ns, TAGSubTitle);
	}

	String readFileNames(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		String value = null;
		parser.require(XmlPullParser.START_TAG, ns, TAGFileNames);
		value = parseTag(parser, TAGFileName);
		parser.require(XmlPullParser.END_TAG, ns, TAGFileNames);
		return value;
	}

	void readTrailers(XmlPullParser parser, ContentData content)
			throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, TAGTrailers);
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals(TAGTrailer)) {
				readTrailer(parser, content);
			}
		}
		parser.require(XmlPullParser.END_TAG, ns, TAGTrailers);
	}

	void readTrailer(XmlPullParser parser, ContentData content)
			throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, TAGTrailer);

		ContentData.Trailer item = new ContentData.Trailer();
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals(TAG)) {
				item.Id = readTag(parser, TAGTrailerID);
			} else if (name.equals(TAGTrailerName)) {
				item.Name = readTag(parser, TAGTrailerName);
			} else if (name.equals(TAGTrailerURI)) {
				item.URI = readTag(parser, TAGTrailerURI);
			} else {
				skip(parser);
			}
		}
		parser.require(XmlPullParser.END_TAG, ns, TAGTrailer);

		if (content.Trailers == null) {
			content.Trailers = new ArrayList<ContentData.Trailer>();
		}
		content.Trailers.add(item);
	}

	void readPosters(XmlPullParser parser, ContentData content)
			throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, TAGPosters);
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals(TAGPoster)) {
				readPoster(parser, content);
			}
		}
		parser.require(XmlPullParser.END_TAG, ns, TAGPosters);
	}

	void readPoster(XmlPullParser parser, ContentData content)
			throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, TAGPoster);

		ContentData.Poster item = new ContentData.Poster();
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals(TAG)) {
				item.Id = readTag(parser, TAGPosterID);
			} else if (name.equals(TAGPosterName)) {
				item.Name = readTag(parser, TAGPosterName);
			} else if (name.equals(TAGPosterURI)) {
				item.URI = readTag(parser, TAGPosterURI);
			} else {
				skip(parser);
			}
		}
		parser.require(XmlPullParser.END_TAG, ns, TAGPoster);

		if (content.Posters == null) {
			content.Posters = new ArrayList<ContentData.Poster>();
		}
		content.Posters.add(item);
	}

	void readMFile(XmlPullParser parser, ContentData content)
			throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, TAGMFile);

		ContentData.MFile file = new ContentData.MFile();
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals(TAGFileID)) {
				file.FileID = readTag(parser, TAGFileID);
			} else if (name.equals(TAGFileNames)) {
				file.FileName = readFileNames(parser);
			} else if (name.equals(TAGFileType)) {
				file.FileType = readTag(parser, TAGFileType);
			} else if (name.equals(TAGFileSize)) {
				file.FileSize = readTag(parser, TAGFileSize);
			} else if (name.equals(TAGDuration)) {
				file.Duration = readTag(parser, TAGDuration);
			} else if (name.equals(TAGFileURI)) {
				file.FileURI = readTag(parser, TAGFileURI);
			} else if (name.equals(TAGResolution)) {
				file.Resolution = readTag(parser, TAGResolution);
			} else if (name.equals(TAGBitRate)) {
				file.BitRate = readTag(parser, TAGBitRate);
			} else if (name.equals(TAGFileFormat)) {
				file.FileFormat = readTag(parser, TAGFileFormat);
			} else if (name.equals(TAGCodeFormat)) {
				file.CodeFormat = readTag(parser, TAGCodeFormat);
			} else {
				skip(parser);
			}
		}
		parser.require(XmlPullParser.END_TAG, ns, TAGMFile);
		
		content.MainFile = file;
	}

	String parseTag(XmlPullParser parser, String tag) throws IOException,
			XmlPullParserException {

		String value = null;
		String parsedValue = null;

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals(tag)) {
				value = readTags(parser, tag);
				if (value != null) {
					parsedValue = value;
				}
			}
		}

		return parsedValue;
	}

	private String readTags(XmlPullParser parser, String tag)
			throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, tag);
		String value = null;
		if (tag.equals(TAGPublicationName)) {
			value = readPublicationName(parser);
		} else if (tag.equals(TAGFileURI)) {
			value = readTag(parser, TAGFileURI);
		} else if (tag.equals(TAGFileName)) {
			value = readPublicationName(parser);
		} else {
			skip(parser);
		}
		parser.require(XmlPullParser.END_TAG, ns, tag);

//		Log.d(TAG, "tag " + tag + " value = " + value);
		return value;
	}

	String readPublicationName(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		String name = null;
		String language = parser.getAttributeValue(ns, AttributeLanguage);
//		Log.d(TAG, "readPublicationName " + language);
		if (language.equals(getLocalization())) {
			name = parser.getAttributeValue(ns, AttributeValue);
//			Log.d(TAG, "readPublicationName name " + name);
		}
		parser.nextTag();

//		Log.d(TAG, "readPublicationName next tag " + parser.getName());
		return name;
	}

	void readAVInfo(XmlPullParser parser, ContentData content)
			throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, TAGPublicationVA);
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();

//			Log.d(TAG, "tag name = " + name + " event=" + parser.getEventType());

			if (name.equals(TAGMultipleLanguageInfos)) {
				readMultipleLanguageInfos(parser, content);
			} else if (name.equals(TAGSubTitles)) {
				readSubTitles(parser, content);
			} else if (name.equals(TAGTrailers)) {
				readTrailers(parser, content);
			} else if (name.equals(TAGPosters)) {
				readPosters(parser, content);
			} else if (name.equals(TAGMFile)) {
				readMFile(parser, content);
			} else {
				skip(parser);
			}
		}
		parser.require(XmlPullParser.END_TAG, ns, TAGPublicationVA);
	}

	private void readData(XmlPullParser parser, ContentData content)
			throws XmlPullParserException, IOException {
		parser.nextTag();
		parser.require(XmlPullParser.START_TAG, ns, TAGPublication);

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
//			Log.d(TAG, "tag = " + name);

			if (name.equals(TAGPublicationNames)) {
				content.Name = readPublicationNames(parser);
			} else if (name.equals(TAGDRMFile)) {
				content.DRMFile = readDRMFile(parser);
			} else if (name.equals(TAGPublicationVA)) {
				readAVInfo(parser, content);
			} else {
				skip(parser);
			}
		}

	}

	private String readTag(XmlPullParser parser, String tag)
			throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, tag);
		String value = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, tag);
		return value;
	}

	private String readText(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
//		Log.d(TAG, "read text " + result);
		return result;
	}

	private List<String> readList(XmlPullParser parser, String rootTag,
			String tag) throws XmlPullParserException, IOException {
		List<String> items = new LinkedList<String>();
		parser.require(XmlPullParser.START_TAG, ns, rootTag);

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			String name = parser.getName();
			if (name.equals(tag)) {
				String value = readTag(parser, tag);
				items.add(value);
			} else {
				skip(parser);
			}
		}

		return items;
	}

	private void skip(XmlPullParser parser) throws XmlPullParserException,
			IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}
	}
}
