package com.dbstar.settings;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.dbstar.settings.R;
import com.dbstar.settings.base.PageManager;
import com.dbstar.settings.utils.APPVersion;
import com.dbstar.settings.utils.ImageUtil;
import com.dbstar.settings.utils.SettingsCommon;

public class GDNetworkSettingsActivity extends GDBaseActivity implements
		PageManager {

	private static final String TAG = "GDNetworkSettingsActivity";
	private static final String BACK_STACK_PREFS = ":android:prefs";

	private static final String PACKAGENAME = "com.dbstar.settings.network";

	Page[] mPages;

	private Bitmap mBitmap;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		constructPages();

		setContentView(R.layout.network_settings);
		
		initializeView();
		
		RelativeLayout mContainer = (RelativeLayout) findViewById(R.id.network_settings_container);
		
		String appUri = getIntent().getStringExtra(SettingsCommon.AppBG_Uri);
		
		mBitmap = ImageUtil.loadPic(appUri);
		
		if (mBitmap == null) {
			mContainer.setBackgroundResource(R.drawable.view_background);
		} else {
			Drawable drawable = new BitmapDrawable(mBitmap);
			mContainer.setBackgroundDrawable(drawable);			
		}
		
		
		Intent intent = getIntent();
		mMenuPath = intent.getStringExtra(INTENT_KEY_MENUPATH);
		if (mMenuPath != null) {
			showMenuPath(mMenuPath.split(MENU_STRING_DELIMITER));
		}
		
		if(APPVersion.SATELLITE)
		    switchToPage(SettingsCommon.PAGE_CHANNELSELECTOR);
		else
		    switchToPage(SettingsCommon.PAGE_GATEWAY);
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mBitmap != null && !mBitmap.isRecycled()) {
			mBitmap.recycle();
			mBitmap = null;
		}
		System.gc();
	}

	protected void switchToPageInternal(String fragmentName, Bundle args) {
		getFragmentManager().popBackStack(BACK_STACK_PREFS,
				FragmentManager.POP_BACK_STACK_INCLUSIVE);
		Fragment f = Fragment.instantiate(this, fragmentName, args);
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		transaction.replace(R.id.frame, f);
		transaction.commitAllowingStateLoss();
	}

	int getPageIndexById(int id) {
		int size = mPages.length;
		for (int i = 0; i < size; i++) {

			if (mPages[i].Id == id)
				return i;
		}

		return -1;
	}

	int getNextPage(int currentPageId) {

		return -1;
	}

	int getPrevPage(int currentPageId) {
		return -1;
	}
	
	private void switchToPage(int pageId) {
		int toPageIndex = getPageIndexById(pageId);
		if (toPageIndex < 0)
			return;
		
		Page toPage = mPages[toPageIndex];
		switchToPageInternal(toPage.ComponentName, toPage.Args);
	}

	@Override
	public void nextPage(int currentPageId, int nextPageId) {
		int toPageIndex = getPageIndexById(nextPageId);
		if (toPageIndex < 0)
			return;

		int currentPageIndex = getPageIndexById(currentPageId);
		Page currentPage = mPages[currentPageIndex];
		Page toPage = mPages[toPageIndex];

		currentPage.NextPageId = nextPageId;
		toPage.PrevPageId = currentPageId;

		switchToPageInternal(toPage.ComponentName, toPage.Args);
	}

	@Override
	public void prevPage(int currentPageId) {
		int currentPageIndex = getPageIndexById(currentPageId);
		Page currentPage = mPages[currentPageIndex];

		int toPageId = currentPage.PrevPageId;
		if (toPageId < 0)
			return;

		int prevPageIndex = getPageIndexById(toPageId);
		Page toPage = mPages[prevPageIndex];

		currentPage.PrevPageId = -1;
		toPage.NextPageId = -1;

		switchToPageInternal(toPage.ComponentName, toPage.Args);
	}

	void constructPages() {
		mPages = new Page[6];
		Page page = new Page();
		page.Id = SettingsCommon.PAGE_GATEWAY;
		page.ComponentName = PACKAGENAME + ".GatewaySettingsPage";
		mPages[SettingsCommon.PAGE_GATEWAY] = page;

		page = new Page();
		page.Id = SettingsCommon.PAGE_CHANNELSELECTOR;
		page.ComponentName = PACKAGENAME + ".ChannelSelectorPage";
		mPages[SettingsCommon.PAGE_CHANNELSELECTOR] = page;

		page = new Page();
		page.Id = SettingsCommon.PAGE_ETHERNET;
		page.ComponentName = PACKAGENAME + ".EthernetSettingsPage";
		mPages[SettingsCommon.PAGE_ETHERNET] = page;

		page = new Page();
		page.Id = SettingsCommon.PAGE_WIFI;
		page.ComponentName = PACKAGENAME + ".WifiSettingsPage";
		mPages[SettingsCommon.PAGE_WIFI] = page;

		page = new Page();
		page.Id = SettingsCommon.PAGE_ETHERNET2;
		page.ComponentName = PACKAGENAME + ".EthernetSettings2Page";
		mPages[SettingsCommon.PAGE_ETHERNET2] = page;

		page = new Page();
		page.Id = SettingsCommon.PAGE_FINISH;
		page.ComponentName = PACKAGENAME + ".FinishSettingsPage";
		mPages[SettingsCommon.PAGE_FINISH] = page;
	}

}
