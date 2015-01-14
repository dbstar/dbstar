package com.media.reader.vo;

import com.media.dbstarplayer.bookmodel.TOCTree.Reference;

/**
 * toc list item 
 * */
public final class TocReference{
	public final String tocName;
//	public final int parIndex;
//	public final ZLTextModel model;
	public final Reference mRef;
	public TocReference(String value,Reference ref) {
		tocName = value;
		mRef = ref;
//		model = m;
//		parIndex = index;
	}
}
