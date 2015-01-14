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

package com.media.dbstarplayer.dbstarplayer;

import java.util.*;

import com.media.zlibrary.core.application.*;
import com.media.zlibrary.core.library.ZLibrary;
import com.media.zlibrary.core.options.*;
import com.media.zlibrary.core.resources.ZLResource;
import com.media.zlibrary.core.util.MiscUtil;
import com.media.zlibrary.core.util.ZLColor;
import com.media.zlibrary.core.view.ZLView.Animation;

import com.media.zlibrary.text.hyphenation.ZLTextHyphenator;
import com.media.zlibrary.text.model.ZLTextModel;
import com.media.zlibrary.text.view.*;
import com.media.zlibrary.text.view.style.ZLTextBaseStyle;
import com.media.zlibrary.text.view.style.ZLTextStyleCollection;
import com.media.zlibrary.ui.android.library.ZLAndroidLibrary;

import com.media.player.common.Utils;
import com.media.reader.model.ImageManager;
import com.media.reader.vo.TocReference;
import com.media.android.dbstarplayer.R;
import com.media.dbstarplayer.book.*;
import com.media.dbstarplayer.bookmodel.*;
import com.media.dbstarplayer.dbstarplayer.options.*;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public final class DbStarPlayerApp extends ZLApplication {
	
	private final String TAG="DbStarPlayerApp";
//	private boolean  audioPlay=false;
//	public static List<Bookmark> bookMarks;
	public ZLTextWordCursor mCursorAudioSpeek = null;
	/**
	 * margins for read page as pix
	 * 
	 * */
	private final int WIDTH_MARGIN_LEFT_DEFAULT = 75;
	private final int WIDTH_MARGIN_RIGHT_DEFAULT = 75;
	private final int HEIGHT_MARGIN_TOP_DEFAULT = 58;
	private final int HEIGHT_MARGIN_BOTTOM_DEFAULT = 65;
	
	/**
	 * width of edge left or right
	 * measured as dp
	 * */
	private int mEdgeWidth = 21;
	
	/**
	 * height of edge bottom
	 * measured as dp
	 * */
	private int mEdgeBottom = 10;
	
	private final static int INCREMENT = 3;
	private final static int DEINCREMENT = -3;
	
	private final int MSG_OPEN_BOOK_FAILED = 0;
	ArrayList<TocReference> mChapterList = new ArrayList<TocReference>();
	
	public Context mContext = null;
	private volatile Book curBook;
	public final ZLBooleanOption AllowScreenBrightnessAdjustmentOption =
		new ZLBooleanOption("LookNFeel", "AllowScreenBrightnessAdjustment", true);
	public final ZLStringOption TextSearchPatternOption =
		new ZLStringOption("TextSearch", "Pattern", "");

	public final ZLBooleanOption UseSeparateBindingsOption =
		new ZLBooleanOption("KeysOptions", "UseSeparateBindings", false);

	public final ZLBooleanOption EnableDoubleTapOption =
		new ZLBooleanOption("Options", "EnableDoubleTap", false);
	public final ZLBooleanOption NavigateAllWordsOption =
		new ZLBooleanOption("Options", "NavigateAllWords", false);

	public static enum WordTappingAction {
		doNothing, selectSingleWord, startSelecting, openDictionary
	}
	public final ZLEnumOption<WordTappingAction> WordTappingActionOption =
		new ZLEnumOption<WordTappingAction>("Options", "WordTappingAction", WordTappingAction.startSelecting);

	public final ZLColorOption ImageViewBackgroundOption =
		new ZLColorOption("Colors", "ImageViewBackground", new ZLColor(255, 255, 255));
	public final ZLEnumOption<DbStarView.ImageFitting> FitImagesToScreenOption =
		new ZLEnumOption<DbStarView.ImageFitting>("Options", "FitImagesToScreen", DbStarView.ImageFitting.covers);
	public static enum ImageTappingAction {
		doNothing, selectImage, openImageView
	}
	public final ZLEnumOption<ImageTappingAction> ImageTappingActionOption =
		new ZLEnumOption<ImageTappingAction>("Options", "ImageTappingAction", ImageTappingAction.openImageView);

//	public final ZLBooleanOption TwoColumnViewOption;
	public final ZLIntegerRangeOption LeftMarginOption;
	public final ZLIntegerRangeOption RightMarginOption;
	public final ZLIntegerRangeOption TopMarginOption;
	public final ZLIntegerRangeOption BottomMarginOption;
	public final ZLIntegerRangeOption SpaceBetweenColumnsOption;
	public final ZLIntegerRangeOption FooterHeightOption;
	{
		final int dpi = ZLibrary.Instance().getDisplayDPI();
		final int x = ZLibrary.Instance().getPixelWidth();
		final int y = ZLibrary.Instance().getPixelHeight();
		final int horMargin = Math.min(dpi / 5, Math.min(x, y) / 30);

//		TwoColumnViewOption = new ZLBooleanOption("Options", "TwoColumnView", x * x + y * y >= 42 * dpi * dpi);
		LeftMarginOption = new ZLIntegerRangeOption("Options", "LeftMargin", 0, 100, WIDTH_MARGIN_LEFT_DEFAULT);
		RightMarginOption = new ZLIntegerRangeOption("Options", "RightMargin", 0, 100, WIDTH_MARGIN_RIGHT_DEFAULT);
		TopMarginOption = new ZLIntegerRangeOption("Options", "TopMargin", 0, 100, HEIGHT_MARGIN_TOP_DEFAULT);
		BottomMarginOption = new ZLIntegerRangeOption("Options", "BottomMargin", 0, 100, HEIGHT_MARGIN_BOTTOM_DEFAULT);
		SpaceBetweenColumnsOption = new ZLIntegerRangeOption("Options", "SpaceBetweenColumns", 0, 300, 3 * horMargin);
		FooterHeightOption = new ZLIntegerRangeOption("Options", "FooterHeight", 8, dpi / 8, dpi / 20);
	}

	public final ZLIntegerRangeOption ScrollbarTypeOption =
		new ZLIntegerRangeOption("Options", "ScrollbarType", 0, 3, DbStarView.SCROLLBAR_SHOW_AS_FOOTER);

	final ZLStringOption ColorProfileOption =
		new ZLStringOption("Options", "ColorProfile", ColorProfile.DAY);

	public final PageTurningOptions PageTurningOptions = new PageTurningOptions();
	public final FooterOptions FooterOptions = new FooterOptions();
	public final CancelMenuOptions CancelMenuOptions = new CancelMenuOptions();

	private final ZLKeyBindings myBindings = new ZLKeyBindings("Keys");

	public final DbStarView BookTextView;
	public final DbStarView FootnoteView;
	private String myFootnoteModelId;

	public volatile BookModel Model;

	private ZLTextPosition myJumpEndPosition;
	private Date myJumpTimeStamp;

	public final IBookCollection Collection;

	public DbStarPlayerApp(Context con,IBookCollection collection) {
		mContext = con;
		Collection = collection;

		collection.addListener(new IBookCollection.Listener() {
			public void onBookEvent(BookEvent event, Book book) {
				switch (event) {
					case BookmarkStyleChanged:
					case BookmarksUpdated:
						if (Model != null && (book == null || book.equals(Model.Book))) {
							if (BookTextView.getModel() != null) { 
								setBookmarkHighlightings(BookTextView, null);
							}
							if (FootnoteView.getModel() != null && myFootnoteModelId != null) { 
								setBookmarkHighlightings(FootnoteView, myFootnoteModelId);
							}
						}
						break;
					case Updated:
						onBookUpdated(book);
						break;
				}
			}

			public void onBuildEvent(IBookCollection.Status status) {
			}
		});

		addAction(ActionCode.INCREASE_FONT, new ChangeFontSizeAction(this, INCREMENT));
		addAction(ActionCode.DECREASE_FONT, new ChangeFontSizeAction(this, DEINCREMENT));

		addAction(ActionCode.FIND_NEXT, new FindNextAction(this));
		addAction(ActionCode.FIND_PREVIOUS, new FindPreviousAction(this));
		addAction(ActionCode.CLEAR_FIND_RESULTS, new ClearFindResultsAction(this));

		addAction(ActionCode.SELECTION_CLEAR, new SelectionClearAction(this));

		addAction(ActionCode.TURN_PAGE_FORWARD, new TurnPageAction(this, true));
		addAction(ActionCode.TURN_PAGE_BACK, new TurnPageAction(this, false));
		
		addAction(ActionCode.CHAPTER_FORWARD, new ChapterNavigateAction(this, true));
		addAction(ActionCode.CHAPTER_BACK, new ChapterNavigateAction(this, false));

		addAction(ActionCode.MOVE_CURSOR_UP, new MoveCursorAction(this, DbStarView.Direction.up));
		addAction(ActionCode.MOVE_CURSOR_DOWN, new MoveCursorAction(this, DbStarView.Direction.down));
		addAction(ActionCode.MOVE_CURSOR_LEFT, new MoveCursorAction(this, DbStarView.Direction.rightToLeft));
		addAction(ActionCode.MOVE_CURSOR_RIGHT, new MoveCursorAction(this, DbStarView.Direction.leftToRight));

		addAction(ActionCode.VOLUME_KEY_SCROLL_FORWARD, new VolumeKeyTurnPageAction(this, true));
		addAction(ActionCode.VOLUME_KEY_SCROLL_BACK, new VolumeKeyTurnPageAction(this, false));

		addAction(ActionCode.SWITCH_TO_DAY_PROFILE, new SwitchProfileAction(this, ColorProfile.DAY));
		addAction(ActionCode.SWITCH_TO_NIGHT_PROFILE, new SwitchProfileAction(this, ColorProfile.NIGHT));

		addAction(ActionCode.EXIT, new ExitAction(this));

		BookTextView = new DbStarView(this);
		FootnoteView = new DbStarView(this);

		setView(BookTextView);
	}

	public void openBook(final Book book, final Bookmark bookmark, final Runnable postAction) {
		if(book==null){
			Log.d(TAG, "--------book is null!");
			mHandler.sendEmptyMessage(MSG_OPEN_BOOK_FAILED);
			return;
		}
		if (book != null || Model == null) {
			runWithMessage("loadingBook", new Runnable() {
				public void run() {
					openBookInternal(book, bookmark, false);
					if (book != null) {
						book.addLabel(Book.READ_LABEL);
						Collection.saveBook(book, false);
						curBook = book;
					}
				}
			}, postAction);
		}
	}

	public void reloadBook() {
		if (Model != null && Model.Book != null) {
			runWithMessage("loadingBook", new Runnable() {
				public void run() {
					openBookInternal(Model.Book, null, true);
				}
			}, null);
		}
	}

	private ColorProfile myColorProfile;

	public ColorProfile getColorProfile() {
		if (myColorProfile == null) {
			myColorProfile = ColorProfile.get(getColorProfileName());
		}
		return myColorProfile;
	}

	public String getColorProfileName() {
		return ColorProfileOption.getValue();
	}

	public void setColorProfileName(String name) {
		ColorProfileOption.setValue(name);
		myColorProfile = null;
	}

	public ZLKeyBindings keyBindings() {
		return myBindings;
	}

	public DbStarView getTextView() {
		return (DbStarView)getCurrentView();
	}

	public void tryOpenFootnote(String id) {
		if (Model != null) {
			myJumpEndPosition = null;
			myJumpTimeStamp = null;
			BookModel.Label label = Model.getLabel(id);
			if (label != null) {
				if (label.ModelId == null) {
					if (getTextView() == BookTextView) {
//						addInvisibleBookmark();
						myJumpEndPosition = new ZLTextFixedPosition(label.ParagraphIndex, 0, 0);
						myJumpTimeStamp = new Date();
					}
					BookTextView.gotoPosition(label.ParagraphIndex, 0, 0);
					setView(BookTextView);
				} else {
					setFootnoteModel(label.ModelId);
					setView(FootnoteView);
					FootnoteView.gotoPosition(label.ParagraphIndex, 0, 0);
				}
				getViewWidget().repaint();
			}
		}
	}

	public void clearTextCaches() {
		BookTextView.clearCaches();
		FootnoteView.clearCaches();
	}

	public Bookmark addSelectionBookmark() {
		final DbStarView fbView = getTextView();
		final String text = fbView.getSelectedText();

		final Bookmark bookmark = new Bookmark(
			Model.Book,
			fbView.getModel().getId(),
			fbView.getSelectionStartPosition(),
			fbView.getSelectionEndPosition(),
			text,
			true
		);
		Collection.saveBookmark(bookmark);
		fbView.clearSelection();

		return bookmark;
	}

	private void setBookmarkHighlightings(ZLTextView view, String modelId) {
		view.removeHighlightings(BookmarkHighlighting.class);
		for (BookmarkQuery query = new BookmarkQuery(Model.Book, 20); ; query = query.next()) {
			final List<Bookmark> bookmarks = Collection.bookmarks(query);
			if (bookmarks.isEmpty()) {
				break;
			}
			for (Bookmark b : bookmarks) {
				if (b.getEnd() == null) {
					b.findEnd(view);
				}
				if (MiscUtil.equals(modelId, b.ModelId)) {
					view.addHighlighting(new BookmarkHighlighting(view, Collection, b));
				}
			}
		}
	}

	private void setFootnoteModel(String modelId) {
		final ZLTextModel model = Model.getFootnoteModel(modelId);
		FootnoteView.setModel(model);
		if (model != null) {
			myFootnoteModelId = modelId;
			setBookmarkHighlightings(FootnoteView, modelId);
		}
	}

	synchronized void openBookInternal(Book book, Bookmark bookmark, boolean force) {
		if (book == null) {
//			book = Collection.getRecentBook(0);
//			if (book == null || !book.File.exists()) {
//				book = Collection.getBookByFile(BookUtil.getHelpFile());
//			}
//			if (book == null) {
				Utils.printLogError(getClass().getSimpleName(), "Can't get book to open!!!");
				mHandler.sendEmptyMessage(MSG_OPEN_BOOK_FAILED);
				return;
//			}
//			book.addLabel(Book.READ_LABEL);
//			Collection.saveBook(book, false);
		}else{
			curBook = book;
		}
		
		if (!force && Model != null && book.equals(Model.Book)) {
			if (bookmark != null) {
				gotoBookmark(bookmark);
			}
			return;
		}

		onViewChanged();

//		storePosition();
		BookTextView.setModel(null);
		FootnoteView.setModel(null);
		clearTextCaches();

		Model = null;
		System.gc();
		System.gc();
		try {
			Model = BookModel.createModel(book);
			Collection.saveBook(book, false);
			ZLTextHyphenator.Instance().load(book.getLanguage());
			BookTextView.setModel(Model.getTextModel());
			BookTextView.gotoPosition(Collection.getStoredPosition(book.getId()));
			setBookmarkHighlightings(BookTextView, null);
			if (bookmark == null) {
				setView(BookTextView);
			} else {
				gotoBookmark(bookmark);
			}
			Collection.addBookToRecentList(book);
			final StringBuilder title = new StringBuilder(book.getTitle());
			if (!book.authors().isEmpty()) {
				boolean first = true;
				for (Author a : book.authors()) {
					title.append(first ? " (" : ", ");
					title.append(a.DisplayName);
					first = false;
				}
				title.append(")");
			}
			setTitle(title.toString());
			initChapterList(Model.TOCTree);
		} catch (BookReadingException e) {
			processException(e);
		}

		getViewWidget().reset();
		getViewWidget().repaint();
	}

	private List<Bookmark> invisibleBookmarks() {
		final List<Bookmark> bookmarks = Collection.bookmarks(
			new BookmarkQuery(Model.Book, false, 10)
		);
		Collections.sort(bookmarks, new Bookmark.ByTimeComparator());
		return bookmarks;
	}

	public boolean jumpBack() {
		try {
			if (getTextView() != BookTextView) {
				showBookTextView();
				return true;
			}

			if (myJumpEndPosition == null || myJumpTimeStamp == null) {
				return false;
			}
			// more than 2 minutes ago
			if (myJumpTimeStamp.getTime() + 2 * 60 * 1000 < new Date().getTime()) {
				return false;
			}
			if (!myJumpEndPosition.equals(BookTextView.getStartCursor())) {
				return false;
			}

			final List<Bookmark> bookmarks = invisibleBookmarks();
			if (bookmarks.isEmpty()) {
				return false;
			}
			final Bookmark b = bookmarks.get(0);
			Collection.deleteBookmark(b);
			gotoBookmark(b);
			return true;
		} finally {
			myJumpEndPosition = null;
			myJumpTimeStamp = null;
		}
	}

	public void gotoBookmark(Bookmark bookmark) {
		final String modelId = bookmark.ModelId;
		if (modelId == null) {
//			addInvisibleBookmark();
			BookTextView.gotoHighlighting(
				new BookmarkHighlighting(BookTextView, Collection, bookmark)
			);
			setView(BookTextView);
		} else {
			setFootnoteModel(modelId);
			FootnoteView.gotoHighlighting(
				new BookmarkHighlighting(FootnoteView, Collection, bookmark)
			);
			setView(FootnoteView);
		}
		getViewWidget().repaint();
	}

	public void showBookTextView() {
		setView(BookTextView);
	}

	public void onWindowClosing() {
		storePosition();
	}

	public void storePosition() {
		if (Model != null && Model.Book != null && BookTextView != null) {
			Collection.storePosition(Model.Book.getId(), BookTextView.getStartCursor());
		}
	}

	static enum CancelActionType {
		library,
		networkLibrary,
		previousBook,
		returnTo,
		close
	}

	public static class CancelActionDescription {
		final CancelActionType Type;
		public final String Title;
		public final String Summary;

		CancelActionDescription(CancelActionType type, String summary) {
			final ZLResource resource = ZLResource.resource("cancelMenu");
			Type = type;
			Title = resource.getResource(type.toString()).getValue();
			Summary = summary;
		}
	}

	private static class BookmarkDescription extends CancelActionDescription {
		final Bookmark Bookmark;

		BookmarkDescription(Bookmark b) {
			super(CancelActionType.returnTo, b.getText());
			Bookmark = b;
		}
	}

	private final ArrayList<CancelActionDescription> myCancelActionsList =
		new ArrayList<CancelActionDescription>();

	public List<CancelActionDescription> getCancelActionsList() {
		myCancelActionsList.clear();
//		if (CancelMenuOptions.ShowLibraryItem.getValue()) {
//			myCancelActionsList.add(new CancelActionDescription(
//				CancelActionType.library, null
//			));
//		}
//		if (CancelMenuOptions.ShowNetworkLibraryItem.getValue()) {
//			myCancelActionsList.add(new CancelActionDescription(
//				CancelActionType.networkLibrary, null
//			));
//		}
//		if (CancelMenuOptions.ShowPreviousBookItem.getValue()) {
//			final Book previousBook = Collection.getRecentBook(1);
//			if (previousBook != null) {
//				myCancelActionsList.add(new CancelActionDescription(
//					CancelActionType.previousBook, previousBook.getTitle()
//				));
//			}
//		}
//		if (CancelMenuOptions.ShowPositionItems.getValue()) {
//			if (Model != null && Model.Book != null) {
//				for (Bookmark bookmark : invisibleBookmarks()) {
//					myCancelActionsList.add(new BookmarkDescription(bookmark));
//				}
//			}
//		}
		myCancelActionsList.add(new CancelActionDescription(
			CancelActionType.close, null
		));
		return myCancelActionsList;
	}

	public void runCancelAction(int index) {
		if (index < 0 || index >= myCancelActionsList.size()) {
			return;
		}

		final CancelActionDescription description = myCancelActionsList.get(index);
		switch (description.Type) {
			case library:
				runAction(ActionCode.SHOW_LIBRARY);
				break;
			case networkLibrary:
				runAction(ActionCode.SHOW_NETWORK_LIBRARY);
				break;
			case previousBook:
				openBook(Collection.getRecentBook(1), null, null);
				break;
			case returnTo:
			{
				final Bookmark b = ((BookmarkDescription)description).Bookmark;
				Collection.deleteBookmark(b);
				gotoBookmark(b);
				break;
			}
			case close:
				closeWindow();
				break;
		}
	}

//	private synchronized void updateInvisibleBookmarksList(Bookmark b) {
//		if (Model != null && Model.Book != null && b != null) {
//			for (Bookmark bm : invisibleBookmarks()) {
//				if (b.equals(bm)) {
//					Collection.deleteBookmark(bm);
//				}
//			}
//			Collection.saveBookmark(b);
//			final List<Bookmark> bookmarks = invisibleBookmarks();
//			for (int i = 3; i < bookmarks.size(); ++i) {
//				Collection.deleteBookmark(bookmarks.get(i));
//			}
//		}
//	}
//
//	public void addInvisibleBookmark(ZLTextWordCursor cursor) {
//		if (cursor != null && Model != null && Model.Book != null && getTextView() == BookTextView) {
//			updateInvisibleBookmarksList(Bookmark.createBookmark(
//				Model.Book,
//				getTextView().getModel().getId(),
//				cursor,
//				6,
//				false
//			));
//		}
//	}
//
//	public void addInvisibleBookmark() {
//		if (Model.Book != null && getTextView() == BookTextView) {
//			updateInvisibleBookmarksList(createBookmark(6, false));
//		}
//	}

	public Bookmark createBookmark(int maxLength, boolean visible) {
		Utils.printLogInfo(TAG, "createBookmark called");
		final DbStarView view = getTextView();
		ZLTextWordCursor cursor = null;
		if(ZLApplication.Instance().getViewWidget().getCurAnimationType()==Animation.realdouble){
			cursor = view.getDoublePageStartCursor();
		}else{
			cursor = view.getStartCursor();
		}

		if (cursor.isNull()) {
			return null;
		}

		return Bookmark.createBookmark(
			Model.Book,
			view.getModel().getId(),
			cursor,
			maxLength,
			visible
		);
	}

	public TOCTree getCurrentTOCElement() {
		final ZLTextWordCursor cursor = BookTextView.getStartCursor();
		if (Model == null || cursor == null) {
			return null;
		}

		int index = cursor.getParagraphIndex();
		if (cursor.isEndOfParagraph()) {
			++index;
		}
		TOCTree treeToSelect = null;
		for (TOCTree tree : Model.TOCTree) {
			final TOCTree.Reference reference = tree.getReference();
			if (reference == null) {
				continue;
			}
			if (reference.ParagraphIndex > index) {
				break;
			}
			treeToSelect = tree;
		}
		return treeToSelect;
	}

	public void onBookUpdated(Book book) {
		if (Model == null || Model.Book == null || !Model.Book.equals(book)) {
			return;
		}

		final String newEncoding = book.getEncodingNoDetection();
		final String oldEncoding = Model.Book.getEncodingNoDetection();

		Model.Book.updateFrom(book);

		if (newEncoding != null && !newEncoding.equals(oldEncoding)) {
			reloadBook();
		} else {
			ZLTextHyphenator.Instance().load(Model.Book.getLanguage());
			clearTextCaches();
			getViewWidget().repaint();
		}
	}
	
	public int getEdgeWidth() {
		return mEdgeWidth;
	}
	
	public void setEdgeWidth(int value) {
		mEdgeWidth = value;
	}
	
	public int getEdgeBottom() {
		return Utils.dip2px(mContext, mEdgeBottom);
	}
	
//	public void setEdgeBottom(int value) {
//		mEdgeBottom = value;
//	}
	
	public Book getCurBook(){
		return curBook;
	}
	
	public void updateFont(String type) {
		ZLIntegerRangeOption option = ZLTextStyleCollection.Instance()
				.getBaseStyle().FontSizeOption;
		if (ActionCode.INCREASE_FONT.equals(type)){
			if(option.getValue() < ZLTextBaseStyle.CONTENT_TEXT_MAXSIZE) {
				((DbStarPlayerApp) DbStarPlayerApp.Instance()).runAction(type);
			}else{
				Toast.makeText(mContext, R.string.tip_max_text_size, Toast.LENGTH_SHORT).show();
			}
		} else if (ActionCode.DECREASE_FONT.equals(type)){
			if(option.getValue() > ZLTextBaseStyle.CONTENT_TEXT_MINSIZE) {
				((DbStarPlayerApp) DbStarPlayerApp.Instance()).runAction(type);
			}else{
				Toast.makeText(mContext, R.string.tip_min_text_size, Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private void initChapterList(TOCTree root){
		if(root!=null){
			for(TOCTree tree:root.subTrees()){
				mChapterList.add(new TocReference(tree.getText(),tree.getReference()));
			}
		}
	}
	
	public List<TocReference> getChapterList(){
		if(mChapterList!=null&&mChapterList.size()<=0){
			if(Model!=null&&Model.TOCTree!=null){
				initChapterList(Model.TOCTree);
			}
		}
		Collections.sort(mChapterList,new Comparator<TocReference>() {

			@Override
			public int compare(TocReference lhs, TocReference rhs) {
				// TODO Auto-generated method stub
				return lhs.mRef.ParagraphIndex-rhs.mRef.ParagraphIndex;
			}
		});
		return mChapterList; 
	}
	
	private void showOpenBookFailedDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(R.string.label_toast);
		builder.setMessage(R.string.tip_open_book_failed);
		builder.setPositiveButton(R.string.label_confirm, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				onWindowClosing();
				ImageManager.getInstance().clearBitmapCache();
				((ZLAndroidLibrary)ZLAndroidLibrary.Instance()).finish();
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		});
		builder.setCancelable(false);
		builder.create().show();
	}
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch(msg.what){
				case MSG_OPEN_BOOK_FAILED:
					showOpenBookFailedDialog();
					break;
			}
		}
	};
	
//	public boolean isAudioPlay() {
//		return audioPlay;
//	}
//
//	public void setAudioPlay(boolean audioPlay) {
//		this.audioPlay = audioPlay;
//	}
}
