package com.dbstar.app.help;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dbstar.R;
import com.dbstar.app.GDBaseActivity;
import com.dbstar.app.settings.GDSystemMgrActivity;
import com.dbstar.util.ImageUtil;
import com.dbstar.util.LogUtil;

public class GDHelpActivity extends GDBaseActivity {
	private static final String TAG = "GDHelpActivity";

	private RelativeLayout mContainer;
	private ListView mHeaderView;
	private ListAdapter mAdapter;
	private WebView mContentView;
	ItemHeader[] mItems;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.help_view);

		initializeView();

		Intent intent = getIntent();
		mMenuPath = intent.getStringExtra(INTENT_KEY_MENUPATH);
		LogUtil.d(TAG, "menu path = " + mMenuPath);

		if (mMenuPath != null) {
			String[] menuArray = mMenuPath.split(MENU_STRING_DELIMITER);
			showMenuPath(menuArray);
		}
	}

	public void initializeView() {
		super.initializeView();

		mContainer = (RelativeLayout) findViewById(R.id.help_view_container);
		mHeaderView = (ListView) findViewById(R.id.list);
		mContentView = (WebView) findViewById(R.id.web_view);

		WebViewClient wvClient = new WebViewClient();
		WebChromeClient chromeClient = new WebChromeClient();
		mContentView.setWebViewClient(wvClient);
		mContentView.setWebChromeClient(chromeClient);
		mContentView.setBackgroundColor(Color.TRANSPARENT);
		mContentView.getSettings().setDefaultTextEncodingName("utf-8");

		ItemHeader[] items = new ItemHeader[4];
		ItemHeader item = new ItemHeader();
		item.Title = getResources().getString(
				R.string.help_title_remote_control);
		item.PageUrl = "help/remotecontrol_helppage.html";
		items[0] = item;

		item = new ItemHeader();
		item.Title = getResources().getString(R.string.help_title_settings);
		item.PageUrl = "help/settings_helppage.html";
		items[1] = item;

		item = new ItemHeader();
		item.Title = getResources().getString(R.string.help_title_failure);
		item.PageUrl = "help/failure_helppage.html";
		items[2] = item;
		
		item = new ItemHeader();
		item.Title = getResources().getString(R.string.help_title_qualifications);
		item.PageUrl = "help/qualification_helppage.html";
		items[3] = item;

		mItems = items;

		HeaderAdapter adapter = new HeaderAdapter(this);
		adapter.setDataset(items);
		mHeaderView.setAdapter(adapter);

		mHeaderView.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				ItemHeader item = mItems[position];
				String pageUrl = item.PageUrl;
				loadPage(pageUrl);
			}

			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		
		HashMap<String, Bitmap> bitmaps = ImageUtil.parserXmlAndLoadPic();
		
		if (bitmaps == null || bitmaps.size() <= 0) {
			mContainer.setBackgroundResource(R.drawable.view_background);
			return;
		}
		
		if (bitmaps.containsKey(ImageUtil.Home_Key)) {
			Drawable drawable = new BitmapDrawable(bitmaps.get(ImageUtil.Home_Key));
			mContainer.setBackgroundDrawable(drawable);		
		}
	}

	public void onStart() {
		super.onStart();

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
// because of the inexplicable auto menu key, use KEYCODE_NOTIFICATION in GDGeneralInfoActivity
//		switch (keyCode) {
//		case KeyEvent.KEYCODE_MENU: {
//			mIsMenuKeyPressed = true;
//			mHandler.postDelayed(mCheckLongPressTask, 10000);
//			return true;
//		}
//		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	public boolean onKeyUp(int keyCode, KeyEvent event) {
// because of the inexplicable auto menu key, use KEYCODE_NOTIFICATION in GDGeneralInfoActivity
//		switch (keyCode) {
//		case KeyEvent.KEYCODE_MENU: {
//			mIsMenuKeyPressed = false;
//			mHandler.removeCallbacks(mCheckLongPressTask);
//			return true;
//		}
//		}
		return super.onKeyUp(keyCode, event);
	}
	
	// when long press menu key more than 5 seconds, 
	// show the system management page.
	private boolean mIsMenuKeyPressed = false;

	Runnable mCheckLongPressTask = new Runnable() {

		public void run() {
		
			if (mIsMenuKeyPressed) {
				Intent intent = new Intent();
				intent.setClass(GDHelpActivity.this, GDSystemMgrActivity.class);

				intent.putExtra(INTENT_KEY_MENUPATH, mMenuPath);
				startActivity(intent);
			}
		}
		
	};
	
	

	void loadPage(String url) {
		mContentView.loadUrl("file:///android_asset/" + url);
	}

	class ItemHeader {
		public String Title;
		public String PageUrl;
	}

	private static class HeaderAdapter extends BaseAdapter {
		private static class HeaderViewHolder {
			TextView title;
		}

		private LayoutInflater mInflater;

		private ItemHeader[] mDataset;

		public HeaderAdapter(Context context) {
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public void setDataset(ItemHeader[] data) {
			mDataset = data;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			HeaderViewHolder holder;
			View view;

			if (convertView == null) {
				view = mInflater.inflate(R.layout.help_header_item, parent,
						false);
				holder = new HeaderViewHolder();
				holder.title = (TextView) view.findViewById(R.id.title);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (HeaderViewHolder) view.getTag();
			}

			holder.title.setText(mDataset[position].Title);

			return view;
		}

		@Override
		public int getCount() {
			if (mDataset != null) {
				return mDataset.length;
			}
			return 0;
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}
	}

}
