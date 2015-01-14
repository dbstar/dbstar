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

package com.media.android.dbstarplayer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iflytek.tts.TtsService.Tts;
import com.media.android.dbstarplayer.api.ApiListener;
import com.media.android.dbstarplayer.api.ApiServerImplementation;
import com.media.android.dbstarplayer.api.PluginApi;
import com.media.android.dbstarplayer.library.BookInfoActivity;
import com.media.android.dbstarplayer.libraryService.BookCollectionShadow;
import com.media.android.dbstarplayer.tips.TipsActivity;
import com.media.android.util.ImageUtil;
import com.media.android.util.UIUtil;
import com.media.dbstarplayer.book.Book;
import com.media.dbstarplayer.book.Bookmark;
import com.media.dbstarplayer.book.SerializerUtil;
import com.media.dbstarplayer.bookmodel.BookModel;
import com.media.dbstarplayer.dbstarplayer.ActionCode;
import com.media.dbstarplayer.dbstarplayer.DbStarPlayerApp;
import com.media.dbstarplayer.dbstarplayer.PopViewAction;
import com.media.dbstarplayer.tips.TipsManager;
import com.media.player.common.Utils;
import com.media.reader.view.ReadProgressChangeListener;
import com.media.zlibrary.core.application.ZLApplication;
import com.media.zlibrary.core.filesystem.ZLFile;
import com.media.zlibrary.core.library.ZLibrary;
import com.media.zlibrary.core.resources.ZLResource;
import com.media.zlibrary.text.view.ZLTextView;
import com.media.zlibrary.ui.android.application.ZLAndroidApplicationWindow;
import com.media.zlibrary.ui.android.library.UncaughtExceptionHandler;
import com.media.zlibrary.ui.android.library.ZLAndroidApplication;
import com.media.zlibrary.ui.android.library.ZLAndroidLibrary;
import com.media.zlibrary.ui.android.view.AndroidFontUtil;
import com.media.zlibrary.ui.android.view.ZLAndroidWidget;

public final class DbStarPlayer extends Activity implements ReadProgressChangeListener{
	final String TAG= getClass().getSimpleName();
	public static final String ACTION_OPEN_BOOK = "android.dbstarplayer.action.VIEW";
	public static final String BOOK_KEY = "dbstarplayer.book";
	public static final String BOOKMARK_KEY = "dbstarplayer.bookmark";

	static final int ACTION_BAR_COLOR = Color.DKGRAY;

	public static final int REQUEST_PREFERENCES = 1;
	public static final int REQUEST_CANCEL_MENU = 2;

	public static final int RESULT_DO_NOTHING = RESULT_FIRST_USER;
	public static final int RESULT_REPAINT = RESULT_FIRST_USER + 1;

	public static void openBookActivity(Context context, Book book, Bookmark bookmark) {
		context.startActivity(
			new Intent(context, DbStarPlayer.class)
				.setAction(ACTION_OPEN_BOOK)
				.putExtra(BOOK_KEY, SerializerUtil.serialize(book))
				.putExtra(BOOKMARK_KEY, SerializerUtil.serialize(bookmark))
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
		);
	}

	private static ZLAndroidLibrary getZLibrary() {
		return (ZLAndroidLibrary)ZLAndroidLibrary.Instance();
	}

	private DbStarPlayerApp myDbStarPlayerApp;
	private volatile Book myBook;

	private RelativeLayout myRootView;
	private ZLAndroidWidget myMainView;

	private ProgressBar mReadProgressBar;
	private TextView mReadProgress;
	private int myFullScreenFlag;
	private String myMenuLanguage;

	private LinearLayout mAudioLayout;
	private LinearLayout mNoAudioLayout;
	private ImageView mAudioPlayStatusImage;
	private Bitmap mBgBitmap;
	
	private static final String PLUGIN_ACTION_PREFIX = "___";
	private final List<PluginApi.ActionInfo> myPluginActions =
		new LinkedList<PluginApi.ActionInfo>();
	private final BroadcastReceiver myPluginInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final ArrayList<PluginApi.ActionInfo> actions = getResultExtras(true).<PluginApi.ActionInfo>getParcelableArrayList(PluginApi.PluginInfo.KEY);
			if (actions != null) {
				synchronized (myPluginActions) {
					int index = 0;
					while (index < myPluginActions.size()) {
						myDbStarPlayerApp.removeAction(PLUGIN_ACTION_PREFIX + index++);
					}
					myPluginActions.addAll(actions);
					index = 0;
					for (PluginApi.ActionInfo info : myPluginActions) {
						myDbStarPlayerApp.addAction(
							PLUGIN_ACTION_PREFIX + index++,
							new RunPluginAction(DbStarPlayer.this, myDbStarPlayerApp, info.getId())
						);
					}
				}
			}
		}
	};

	private synchronized void openBook(Intent intent, Runnable action, boolean force) {
		if (!force && myBook != null) {
			return;
		}

		myBook = SerializerUtil.deserializeBook(intent.getStringExtra(BOOK_KEY));
		final Bookmark bookmark =
			SerializerUtil.deserializeBookmark(intent.getStringExtra(BOOKMARK_KEY));
		if (myBook == null) {
			final Uri data = intent.getData();
			if (data != null) {
				myBook = createBookForFile(ZLFile.createFileByPath(data.getPath()));
			}else{
				myBook = createBookForFile(ZLFile.createFileByPath("/mnt/sdcard/myBooks/xiyouji.epub"));
			}
		}
		myDbStarPlayerApp.openBook(myBook, bookmark, action);
	}

	private Book createBookForFile(ZLFile file) {
		if (file == null) {
			return null;
		}
		Book book = myDbStarPlayerApp.Collection.getBookByFile(file);
		if (book != null) {
			return book;
		}
		if (file.isArchive()) {
			for (ZLFile child : file.children()) {
				book = myDbStarPlayerApp.Collection.getBookByFile(child);
				if (book != null) {
					return book;
				}
			}
		}
		return null;
	}

	private Runnable getPostponedInitAction() {
		return new Runnable() {
			public void run() {
				runOnUiThread(new Runnable() {
					public void run() {
//						new TipRunner().start();
						DictionaryUtil.init(DbStarPlayer.this);
					}
				});
			}
		};
	}

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		long startTime = System.currentTimeMillis();
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler(this));
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		myRootView = (RelativeLayout)findViewById(R.id.root_view);
		myMainView = (ZLAndroidWidget)findViewById(R.id.main_view);
		
		mAudioLayout = (LinearLayout) findViewById(R.id.reader_audio_play_layout);
		mNoAudioLayout = (LinearLayout) findViewById(R.id.reader_no_audio_play_layout);
		mAudioPlayStatusImage = (ImageView) findViewById(R.id.reader_audio_play_status_image);
		Utils.printLogError(getClass().getSimpleName(), "Escape time 1:"+(System.currentTimeMillis()-startTime));
		
		Bundle bundle = getIntent().getBundleExtra("BookBackground");
		String appBgUri = bundle.getString("AppBG");
		Log.d(TAG, "appBgUri = " + appBgUri);
		mBgBitmap = ImageUtil.getBitmap(appBgUri);
		if (mBgBitmap == null) {
			myRootView.setBackgroundResource(R.drawable.reader_view_background);
		} else {
    		myRootView.setBackgroundDrawable(new BitmapDrawable(mBgBitmap));
		}
		
		if(Utils.IS_TEST){
			//audio control
			mAudioLayout.setVisibility(View.VISIBLE);
			mNoAudioLayout.setVisibility(View.GONE);
			mAudioPlayStatusImage.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(!AudioPlayAction.isAudioSpeek){
						myDbStarPlayerApp.runAction(ActionCode.AUDIO_PLAY);
					}
					else{
						myDbStarPlayerApp.runAction(ActionCode.AUDIO_CANCEL);
					}
				}
			});
			findViewById(R.id.img_reader_ok).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					myDbStarPlayerApp.runAction(ActionCode.POPUP_VIEW_ACTION);
				}
			});
			
			//chapter navigation
			TextView chapterNext = (TextView) findViewById(R.id.btn_chapter_next);
			chapterNext.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					myDbStarPlayerApp.runAction(ActionCode.CHAPTER_FORWARD);
				}
			});
			
			TextView chapterPrev = (TextView) findViewById(R.id.btn_chapter_prev);
			chapterPrev.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					myDbStarPlayerApp.runAction(ActionCode.CHAPTER_BACK);
				}
			});
			
			TextView fontIncrease = (TextView) findViewById(R.id.btn_font_increase);
			fontIncrease.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					myDbStarPlayerApp.updateFont(ActionCode.INCREASE_FONT);
				}
			});
			TextView fontDecrease = (TextView) findViewById(R.id.btn_font_decrease);
			fontDecrease.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					myDbStarPlayerApp.updateFont(ActionCode.DECREASE_FONT);
				}
			});
			TextView pagePre = (TextView) findViewById(R.id.btn_page_prev);
			pagePre.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					myDbStarPlayerApp.runAction(ActionCode.TURN_PAGE_BACK);
				}
			});
			TextView pageNext = (TextView) findViewById(R.id.btn_page_next);
			pageNext.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					myDbStarPlayerApp.runAction(ActionCode.TURN_PAGE_FORWARD);
				}
			});
			TextView volumeIncrease = (TextView) findViewById(R.id.btn_volume_increase);
			volumeIncrease.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					myDbStarPlayerApp.runAction(ActionCode.VOLUME_KEY_SCROLL_FORWARD);
				}
			});
			TextView volumeDecrease = (TextView) findViewById(R.id.btn_volume_decrease);
			volumeDecrease.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					myDbStarPlayerApp.runAction(ActionCode.VOLUME_KEY_SCROLL_BACK);
				}
			});
		}
		mReadProgressBar = (ProgressBar)findViewById(R.id.bar_read_progress);
		mReadProgress = (TextView)findViewById(R.id.txt_read_progress);
		setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);

		getZLibrary().setActivity(this);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		myDbStarPlayerApp = (DbStarPlayerApp)DbStarPlayerApp.Instance();
		if (myDbStarPlayerApp == null) {
			myDbStarPlayerApp = new DbStarPlayerApp(this,new BookCollectionShadow());
		}
		getCollection().bindToService(this, null);
		myBook = null;

		final ZLAndroidApplication androidApplication = (ZLAndroidApplication)getApplication();
		if (androidApplication.myMainWindow == null) {
			androidApplication.myMainWindow = new ZLAndroidApplicationWindow(myDbStarPlayerApp);
			myDbStarPlayerApp.initWindow();
		}
		Utils.printLogError(getClass().getSimpleName(), "Escape time 2:"+(System.currentTimeMillis()-startTime));
		final ZLAndroidLibrary zlibrary = (ZLAndroidLibrary)ZLibrary.Instance();
		myFullScreenFlag =
			zlibrary.ShowStatusBarOption.getValue() ? 0 : WindowManager.LayoutParams.FLAG_FULLSCREEN;
		getWindow().setFlags(
			WindowManager.LayoutParams.FLAG_FULLSCREEN, myFullScreenFlag
		);

		if (myDbStarPlayerApp.getPopupById(TextSearchPopup.ID) == null) {
			new TextSearchPopup(myDbStarPlayerApp);
		}
		if (myDbStarPlayerApp.getPopupById(NavigationPopup.ID) == null) {
			new NavigationPopup(myDbStarPlayerApp);
		}
		if (myDbStarPlayerApp.getPopupById(SelectionPopup.ID) == null) {
			new SelectionPopup(myDbStarPlayerApp);
		}

		myDbStarPlayerApp.addAction(ActionCode.SHOW_LIBRARY, new ShowLibraryAction(this, myDbStarPlayerApp));
		myDbStarPlayerApp.addAction(ActionCode.SHOW_PREFERENCES, new ShowPreferencesAction(this, myDbStarPlayerApp));
		myDbStarPlayerApp.addAction(ActionCode.SHOW_BOOK_INFO, new ShowBookInfoAction(this, myDbStarPlayerApp));
		myDbStarPlayerApp.addAction(ActionCode.SHOW_TOC, new ShowTOCAction(this, myDbStarPlayerApp));
		myDbStarPlayerApp.addAction(ActionCode.SHOW_BOOKMARKS, new ShowBookmarksAction(this, myDbStarPlayerApp));
		myDbStarPlayerApp.addAction(ActionCode.SHOW_NETWORK_LIBRARY, new ShowNetworkLibraryAction(this, myDbStarPlayerApp));

		myDbStarPlayerApp.addAction(ActionCode.POPUP_VIEW_ACTION, new PopViewAction(this,myDbStarPlayerApp, true));
		myDbStarPlayerApp.addAction(ActionCode.SHOW_MENU, new ShowMenuAction(this, myDbStarPlayerApp));
		myDbStarPlayerApp.addAction(ActionCode.SHOW_NAVIGATION, new ShowNavigationAction(this, myDbStarPlayerApp));
		myDbStarPlayerApp.addAction(ActionCode.SEARCH, new SearchAction(this, myDbStarPlayerApp));
		myDbStarPlayerApp.addAction(ActionCode.SHARE_BOOK, new ShareBookAction(this, myDbStarPlayerApp));

		myDbStarPlayerApp.addAction(ActionCode.SELECTION_SHOW_PANEL, new SelectionShowPanelAction(this, myDbStarPlayerApp));
		myDbStarPlayerApp.addAction(ActionCode.SELECTION_HIDE_PANEL, new SelectionHidePanelAction(this, myDbStarPlayerApp));
		myDbStarPlayerApp.addAction(ActionCode.SELECTION_COPY_TO_CLIPBOARD, new SelectionCopyAction(this, myDbStarPlayerApp));
		myDbStarPlayerApp.addAction(ActionCode.SELECTION_SHARE, new SelectionShareAction(this, myDbStarPlayerApp));
		myDbStarPlayerApp.addAction(ActionCode.SELECTION_TRANSLATE, new SelectionTranslateAction(this, myDbStarPlayerApp));
		myDbStarPlayerApp.addAction(ActionCode.SELECTION_BOOKMARK, new SelectionBookmarkAction(this, myDbStarPlayerApp));

		myDbStarPlayerApp.addAction(ActionCode.PROCESS_HYPERLINK, new ProcessHyperlinkAction(this, myDbStarPlayerApp));

		myDbStarPlayerApp.addAction(ActionCode.SHOW_CANCEL_MENU, new ShowCancelMenuAction(this, myDbStarPlayerApp));

		myDbStarPlayerApp.addAction(ActionCode.SET_SCREEN_ORIENTATION_SYSTEM, new SetScreenOrientationAction(this, myDbStarPlayerApp, ZLibrary.SCREEN_ORIENTATION_SYSTEM));
		myDbStarPlayerApp.addAction(ActionCode.SET_SCREEN_ORIENTATION_SENSOR, new SetScreenOrientationAction(this, myDbStarPlayerApp, ZLibrary.SCREEN_ORIENTATION_SENSOR));
		myDbStarPlayerApp.addAction(ActionCode.SET_SCREEN_ORIENTATION_PORTRAIT, new SetScreenOrientationAction(this, myDbStarPlayerApp, ZLibrary.SCREEN_ORIENTATION_PORTRAIT));
		myDbStarPlayerApp.addAction(ActionCode.SET_SCREEN_ORIENTATION_LANDSCAPE, new SetScreenOrientationAction(this, myDbStarPlayerApp, ZLibrary.SCREEN_ORIENTATION_LANDSCAPE));
		myDbStarPlayerApp.addAction(ActionCode.AUDIO_CANCEL, new AudioPlayAction(this, myDbStarPlayerApp,AudioPlayAction.CANCEL));
		myDbStarPlayerApp.addAction(ActionCode.AUDIO_PLAY, new AudioPlayAction(this, myDbStarPlayerApp,AudioPlayAction.PLAY));
		myDbStarPlayerApp.addAction(ActionCode.AUDIO_PAUSE, new AudioPlayAction(this, myDbStarPlayerApp,AudioPlayAction.PAUSE));
		myDbStarPlayerApp.addAction(ActionCode.AUDIO_RESUME, new AudioPlayAction(this, myDbStarPlayerApp,AudioPlayAction.RESUME));
		if (ZLibrary.Instance().supportsAllOrientations()) {
			myDbStarPlayerApp.addAction(ActionCode.SET_SCREEN_ORIENTATION_REVERSE_PORTRAIT, new SetScreenOrientationAction(this, myDbStarPlayerApp, ZLibrary.SCREEN_ORIENTATION_REVERSE_PORTRAIT));
			myDbStarPlayerApp.addAction(ActionCode.SET_SCREEN_ORIENTATION_REVERSE_LANDSCAPE, new SetScreenOrientationAction(this, myDbStarPlayerApp, ZLibrary.SCREEN_ORIENTATION_REVERSE_LANDSCAPE));
		}
		Utils.printLogError(getClass().getSimpleName(), "Escape time 3:"+(System.currentTimeMillis()-startTime));
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		final ZLAndroidLibrary zlibrary = (ZLAndroidLibrary)ZLibrary.Instance();
		if (!zlibrary.isKindleFire() && !zlibrary.ShowStatusBarOption.getValue()) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		}

		setupMenu(menu);

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		super.onOptionsMenuClosed(menu);
		final ZLAndroidLibrary zlibrary = (ZLAndroidLibrary)ZLibrary.Instance();
		if (!zlibrary.isKindleFire() && !zlibrary.ShowStatusBarOption.getValue()) {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final ZLAndroidLibrary zlibrary = (ZLAndroidLibrary)ZLibrary.Instance();
		if (!zlibrary.isKindleFire() && !zlibrary.ShowStatusBarOption.getValue()) {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		}
		return super.onOptionsItemSelected(item);
	}

	public ZLAndroidWidget getMainView() {
		return myMainView;
	}

	@Override
	protected void onNewIntent(final Intent intent) {
		final String action = intent.getAction();
		final Uri data = intent.getData();

		if ((intent.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0) {
			super.onNewIntent(intent);
		} else if (Intent.ACTION_VIEW.equals(action)
				   && data != null && "dbstarplayer-action".equals(data.getScheme())) {
			myDbStarPlayerApp.runAction(data.getEncodedSchemeSpecificPart(), data.getFragment());
		} else if (Intent.ACTION_VIEW.equals(action) || ACTION_OPEN_BOOK.equals(action)) {
			getCollection().bindToService(this, new Runnable() {
				public void run() {
					openBook(intent, null, true);
				}
			});
		} else if (Intent.ACTION_SEARCH.equals(action)) {
			final String pattern = intent.getStringExtra(SearchManager.QUERY);
			final Runnable runnable = new Runnable() {
				public void run() {
					final TextSearchPopup popup = (TextSearchPopup)myDbStarPlayerApp.getPopupById(TextSearchPopup.ID);
					popup.initPosition();
					myDbStarPlayerApp.TextSearchPatternOption.setValue(pattern);
					if (myDbStarPlayerApp.getTextView().search(pattern, true, false, false, false) != 0) {
						runOnUiThread(new Runnable() {
							public void run() {
								myDbStarPlayerApp.showPopup(popup.getId());
							}
						});
					} else {
						runOnUiThread(new Runnable() {
							public void run() {
								UIUtil.showErrorMessage(DbStarPlayer.this, "textNotFound");
								popup.StartPosition = null;
							}
						});
					}
				}
			};
			UIUtil.wait("search", runnable, this);
		} else {
			super.onNewIntent(intent);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		getCollection().bindToService(this, new Runnable() {
			public void run() {
				new Thread() {
					public void run() {
						openBook(getIntent(), getPostponedInitAction(), false);
						myDbStarPlayerApp.getViewWidget().repaint();
					}
				}.start();

				myDbStarPlayerApp.getViewWidget().repaint();
			}
		});

		initPluginActions();

		final ZLAndroidLibrary zlibrary = (ZLAndroidLibrary)ZLibrary.Instance();

		final int fullScreenFlag =
			zlibrary.ShowStatusBarOption.getValue() ? 0 : WindowManager.LayoutParams.FLAG_FULLSCREEN;
		if (fullScreenFlag != myFullScreenFlag) {
			finish();
			startActivity(new Intent(this, getClass()));
		}

		SetScreenOrientationAction.setOrientation(this, zlibrary.getOrientationOption().getValue());

		((PopupPanel)myDbStarPlayerApp.getPopupById(TextSearchPopup.ID)).setPanelInfo(this, myRootView);
		((PopupPanel)myDbStarPlayerApp.getPopupById(NavigationPopup.ID)).setPanelInfo(this, myRootView);
		((PopupPanel)myDbStarPlayerApp.getPopupById(SelectionPopup.ID)).setPanelInfo(this, myRootView);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		switchWakeLock(hasFocus &&
			getZLibrary().BatteryLevelToTurnScreenOffOption.getValue() <
			myDbStarPlayerApp.getBatteryLevel()
		);
	}

	private void initPluginActions() {
		synchronized (myPluginActions) {
			int index = 0;
			while (index < myPluginActions.size()) {
				myDbStarPlayerApp.removeAction(PLUGIN_ACTION_PREFIX + index++);
			}
			myPluginActions.clear();
		}

		sendOrderedBroadcast(
			new Intent(PluginApi.ACTION_REGISTER),
			null,
			myPluginInfoReceiver,
			null,
			RESULT_OK,
			null,
			null
		);
	}

	private class TipRunner extends Thread {
		TipRunner() {
			setPriority(MIN_PRIORITY);
		}

		public void run() {
			final TipsManager manager = TipsManager.Instance();
			switch (manager.requiredAction()) {
				case Initialize:
					startActivity(new Intent(
						TipsActivity.INITIALIZE_ACTION, null, DbStarPlayer.this, TipsActivity.class
					));
					break;
				case Show:
					startActivity(new Intent(
						TipsActivity.SHOW_TIP_ACTION, null, DbStarPlayer.this, TipsActivity.class
					));
					break;
				case Download:
					manager.startDownloading();
					break;
				case None:
					break;
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		ZLApplication.Instance().getCurrentView().setReadProgressChangeListener(this);
		myStartTimer = true;
		final int brightnessLevel =
			getZLibrary().ScreenBrightnessLevelOption.getValue();
		if (brightnessLevel != 0) {
			setScreenBrightness(brightnessLevel);
		} else {
			setScreenBrightnessAuto();
		}
		if (getZLibrary().DisableButtonLightsOption.getValue()) {
			setButtonLight(false);
		}

		registerReceiver(myBatteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

		PopupPanel.restoreVisibilities(myDbStarPlayerApp);
		ApiServerImplementation.sendEvent(this, ApiListener.EVENT_READ_MODE_OPENED);
		myDbStarPlayerApp.getViewWidget().repaint();
		getCollection().bindToService(this, new Runnable() {
			public void run() {
				final BookModel model = myDbStarPlayerApp.Model;
				if (model == null || model.Book == null) {
					return;
				}
				onPreferencesUpdate(myDbStarPlayerApp.Collection.getBookById(model.Book.getId()));
			}
		});
	}

	@Override
	protected void onPause() {
		try {
			unregisterReceiver(myBatteryInfoReceiver);
		} catch (IllegalArgumentException e) {
			// do nothing, this exception means myBatteryInfoReceiver was not registered
		}
		myDbStarPlayerApp.stopTimer();
		if (getZLibrary().DisableButtonLightsOption.getValue()) {
			setButtonLight(true);
		}
		myDbStarPlayerApp.onWindowClosing();
		super.onPause();
	}

	@Override
	protected void onStop() {
		ApiServerImplementation.sendEvent(this, ApiListener.EVENT_READ_MODE_CLOSED);
		PopupPanel.removeAllWindows(myDbStarPlayerApp, this);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		getCollection().unbind();
		if(Tts.isInitialized()){
			Tts.JniDestory();
		}
		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		myDbStarPlayerApp.onWindowClosing();
		super.onLowMemory();
	}

	@Override
	public boolean onSearchRequested() {
		final DbStarPlayerApp.PopupPanel popup = myDbStarPlayerApp.getActivePopup();
		myDbStarPlayerApp.hideActivePopup();
		final SearchManager manager = (SearchManager)getSystemService(SEARCH_SERVICE);
		manager.setOnCancelListener(new SearchManager.OnCancelListener() {
			public void onCancel() {
				if (popup != null) {
					myDbStarPlayerApp.showPopup(popup.getId());
				}
				manager.setOnCancelListener(null);
			}
		});
		startSearch(myDbStarPlayerApp.TextSearchPatternOption.getValue(), true, null, false);
		return true;
	}

	public void showSelectionPanel() {
		final ZLTextView view = myDbStarPlayerApp.getTextView();
		((SelectionPopup)myDbStarPlayerApp.getPopupById(SelectionPopup.ID))
			.move(view.getSelectionStartY(), view.getSelectionEndY());
		myDbStarPlayerApp.showPopup(SelectionPopup.ID);
	}

	public void hideSelectionPanel() {
		final DbStarPlayerApp.PopupPanel popup = myDbStarPlayerApp.getActivePopup();
		if (popup != null && popup.getId() == SelectionPopup.ID) {
			myDbStarPlayerApp.hideActivePopup();
		}
	}

	private void onPreferencesUpdate(Book book) {
		AndroidFontUtil.clearFontCache();
		myDbStarPlayerApp.onBookUpdated(book);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_PREFERENCES:
				if (resultCode != RESULT_DO_NOTHING) {
					final Book book = BookInfoActivity.bookByIntent(data);
					if (book != null) {
						getCollection().bindToService(this, new Runnable() {
							public void run() {
								onPreferencesUpdate(book);
							}
						});
					}
				}
				break;
			case REQUEST_CANCEL_MENU:
				myDbStarPlayerApp.runCancelAction(resultCode - 1);
				break;
		}
	}

	public void navigate() {
		((NavigationPopup)myDbStarPlayerApp.getPopupById(NavigationPopup.ID)).runNavigation();
	}

	private Menu addSubMenu(Menu menu, String id) {
		final ZLAndroidApplication application = (ZLAndroidApplication)getApplication();
		return application.myMainWindow.addSubMenu(menu, id);
	}

	private void addMenuItem(Menu menu, String actionId, String name) {
		final ZLAndroidApplication application = (ZLAndroidApplication)getApplication();
		application.myMainWindow.addMenuItem(menu, actionId, null, name);
	}

	private void addMenuItem(Menu menu, String actionId, int iconId) {
		final ZLAndroidApplication application = (ZLAndroidApplication)getApplication();
		application.myMainWindow.addMenuItem(menu, actionId, iconId, null);
	}

	private void addMenuItem(Menu menu, String actionId) {
		final ZLAndroidApplication application = (ZLAndroidApplication)getApplication();
		application.myMainWindow.addMenuItem(menu, actionId, null, null);
	}

	private void setupMenu(Menu menu) {
		final String menuLanguage = ZLResource.getLanguageOption().getValue();
		if (menuLanguage.equals(myMenuLanguage)) {
			return;
		}
		myMenuLanguage = menuLanguage;

		menu.clear();
		addMenuItem(menu, ActionCode.SHOW_LIBRARY, R.drawable.ic_menu_library);
		addMenuItem(menu, ActionCode.SHOW_NETWORK_LIBRARY, R.drawable.ic_menu_networklibrary);
		addMenuItem(menu, ActionCode.SHOW_TOC, R.drawable.ic_menu_toc);
		addMenuItem(menu, ActionCode.SHOW_BOOKMARKS, R.drawable.ic_menu_bookmarks);
		addMenuItem(menu, ActionCode.SWITCH_TO_NIGHT_PROFILE, R.drawable.ic_menu_night);
		addMenuItem(menu, ActionCode.SWITCH_TO_DAY_PROFILE, R.drawable.ic_menu_day);
		addMenuItem(menu, ActionCode.SEARCH, R.drawable.ic_menu_search);
		addMenuItem(menu, ActionCode.SHARE_BOOK, R.drawable.ic_menu_search);
		addMenuItem(menu, ActionCode.SHOW_PREFERENCES);
		addMenuItem(menu, ActionCode.SHOW_BOOK_INFO);
		final Menu subMenu = addSubMenu(menu, "screenOrientation");
		addMenuItem(subMenu, ActionCode.SET_SCREEN_ORIENTATION_SYSTEM);
		addMenuItem(subMenu, ActionCode.SET_SCREEN_ORIENTATION_SENSOR);
		addMenuItem(subMenu, ActionCode.SET_SCREEN_ORIENTATION_PORTRAIT);
		addMenuItem(subMenu, ActionCode.SET_SCREEN_ORIENTATION_LANDSCAPE);
		if (ZLibrary.Instance().supportsAllOrientations()) {
			addMenuItem(subMenu, ActionCode.SET_SCREEN_ORIENTATION_REVERSE_PORTRAIT);
			addMenuItem(subMenu, ActionCode.SET_SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
		}
		addMenuItem(menu, ActionCode.INCREASE_FONT);
		addMenuItem(menu, ActionCode.DECREASE_FONT);
		addMenuItem(menu, ActionCode.SHOW_NAVIGATION);
		synchronized (myPluginActions) {
			int index = 0;
			for (PluginApi.ActionInfo info : myPluginActions) {
				if (info instanceof PluginApi.MenuActionInfo) {
					addMenuItem(
						menu,
						PLUGIN_ACTION_PREFIX + index++,
						((PluginApi.MenuActionInfo)info).MenuItemName
					);
				}
			}
		}

		final ZLAndroidApplication application = (ZLAndroidApplication)getApplication();
		application.myMainWindow.refresh();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		setupMenu(menu);

		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return (myMainView != null && myMainView.onKeyDown(keyCode, event)) || super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return (myMainView != null && myMainView.onKeyUp(keyCode, event)) || super.onKeyUp(keyCode, event);
	}

	private void setButtonLight(boolean enabled) {
		try {
			final WindowManager.LayoutParams attrs = getWindow().getAttributes();
			final Class<?> cls = attrs.getClass();
			final Field fld = cls.getField("buttonBrightness");
			if (fld != null && "float".equals(fld.getType().toString())) {
				fld.setFloat(attrs, enabled ? -1.0f : 0.0f);
				getWindow().setAttributes(attrs);
			}
		} catch (NoSuchFieldException e) {
		} catch (IllegalAccessException e) {
		}
	}

	private PowerManager.WakeLock myWakeLock;
	private boolean myWakeLockToCreate;
	private boolean myStartTimer;

	public final void createWakeLock() {
		if (myWakeLockToCreate) {
			synchronized (this) {
				if (myWakeLockToCreate) {
					myWakeLockToCreate = false;
					myWakeLock =
						((PowerManager)getSystemService(POWER_SERVICE))
							.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "DbStarPlayer");
					myWakeLock.acquire();
				}
			}
		}
		if (myStartTimer) {
			myDbStarPlayerApp.startTimer();
			myStartTimer = false;
		}
	}

	private final void switchWakeLock(boolean on) {
		if (on) {
			if (myWakeLock == null) {
				myWakeLockToCreate = true;
			}
		} else {
			if (myWakeLock != null) {
				synchronized (this) {
					if (myWakeLock != null) {
						myWakeLock.release();
						myWakeLock = null;
					}
				}
			}
		}
	}

	private BroadcastReceiver myBatteryInfoReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			final int level = intent.getIntExtra("level", 100);
			final ZLAndroidApplication application = (ZLAndroidApplication)getApplication();
			application.myMainWindow.setBatteryLevel(level);
			switchWakeLock(
				hasWindowFocus() &&
				getZLibrary().BatteryLevelToTurnScreenOffOption.getValue() < level
			);
		}
	};

	private void setScreenBrightnessAuto() {
		final WindowManager.LayoutParams attrs = getWindow().getAttributes();
		attrs.screenBrightness = -1.0f;
		getWindow().setAttributes(attrs);
	}

	public void setScreenBrightness(int percent) {
		if (percent < 1) {
			percent = 1;
		} else if (percent > 100) {
			percent = 100;
		}
		final WindowManager.LayoutParams attrs = getWindow().getAttributes();
		attrs.screenBrightness = percent / 100.0f;
		getWindow().setAttributes(attrs);
		getZLibrary().ScreenBrightnessLevelOption.setValue(percent);
	}

	public int getScreenBrightness() {
		final int level = (int)(100 * getWindow().getAttributes().screenBrightness);
		return (level >= 0) ? level : 50;
	}

	private BookCollectionShadow getCollection() {
		return (BookCollectionShadow)myDbStarPlayerApp.Collection;
	}
	
	public void addCurBookMark(){
		getCollection().saveBookmark(myDbStarPlayerApp.createBookmark(Bookmark.MAX_LENGTH, true));
//		myDbStarPlayerApp.getCurrentView().setDrawBookmarkAnimation();
	}
	
	public void deleteBookMark(Bookmark bookmark){
		getCollection().deleteBookmark(bookmark);
//		myDbStarPlayerApp.getCurrentView().setDrawBookmarkAnimation();
	}
	
	public void setReadProgress(int progress){
		mReadProgressBar.setProgress(progress);
		mReadProgress.setText(progress+"%");
	}

	@Override
	public void onReadProgressChange(int progress) {
		setReadProgress(progress);
	}
	
	public void setAudioPlay(){
//		mAudioLayout.setVisibility(View.VISIBLE);
//		mNoAudioLayout.setVisibility(View.GONE);
		mAudioPlayStatusImage.setImageResource(R.drawable.reader_audio_playing);
	}
	
	public void setAudioPause(){
		mAudioPlayStatusImage.setImageResource(R.drawable.reader_audio_paused);
	}

	public void cancelAudioPlay(){
		setAudioPause();
		if(!Utils.IS_TEST){
//			mAudioLayout.setVisibility(View.GONE);
//			mNoAudioLayout.setVisibility(View.VISIBLE);
		}
	}
}
