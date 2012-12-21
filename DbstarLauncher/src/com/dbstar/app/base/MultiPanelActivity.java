package com.dbstar.app.base;

import java.util.ArrayList;
import java.util.List;

import com.dbstar.R;
import com.dbstar.app.GDBaseActivity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MultiPanelActivity extends GDBaseActivity implements EngineInterface {

	private static final String TAG = "MultiPanelActivity";
	private static final String BACK_STACK_PREFS = ":android:prefs";

	private final ArrayList<Header> mHeaders = new ArrayList<Header>();
	protected ListView mHeaderView;
	protected ListAdapter mAdapter;
	private Header mCurHeader = null;
	private ViewGroup mContentView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.multipanel_view);

		initializeView();

		if (mMenuPath != null) {
			String[] menuArray = mMenuPath.split(MENU_STRING_DELIMITER);
			showMenuPath(menuArray);
		}

		mHeaderView = (ListView) findViewById(R.id.list);
		mContentView = (ViewGroup) findViewById(R.id.frame);

		getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

		onBuildHeaders(mHeaders);

		if (mHeaders.size() > 0) {

			Log.d(TAG, "header size = " + mHeaders.size());

			mHeaderView.setOnItemClickListener(mOnClickListener);
			setListAdapter(new HeaderAdapter(this, mHeaders));
			mContentView.setVisibility(View.VISIBLE);

			Header h = mHeaders.get(0);
//			switchToHeader(h);
			onHeaderClick(h, 0);
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean ret = false;
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				if (mObserver != null) {
					ret = mObserver.onBackkeyPress();
				}
			}
		}
		
		if (ret) {
			return ret;
		}
		
		return super.onKeyDown(keyCode, event);
	}

	public void switchToHeader(Header header) {
		if (mCurHeader == header) {
			// This is the header we are currently displaying. Just make sure
			// to pop the stack up to its root state.
			getFragmentManager().popBackStack(BACK_STACK_PREFS,
					FragmentManager.POP_BACK_STACK_INCLUSIVE);
		} else {
			int direction = mHeaders.indexOf(header)
					- mHeaders.indexOf(mCurHeader);
			switchToHeaderInner(header.fragment, header.fragmentArguments,
					direction);
			setSelectedHeader(header);
		}
	}

	private void switchToHeaderInner(String fragmentName, Bundle args,
			int direction) {
		getFragmentManager().popBackStack(BACK_STACK_PREFS,
				FragmentManager.POP_BACK_STACK_INCLUSIVE);
		Fragment f = Fragment.instantiate(this, fragmentName, args);
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		transaction.replace(R.id.frame, f);
		transaction.commitAllowingStateLoss();
	}

	void setSelectedHeader(Header header) {
		mCurHeader = header;
		int index = mHeaders.indexOf(header);
		if (index >= 0) {
			getListView().setItemChecked(index, true);
			getListView().smoothScrollToPosition(index);
		} else {
			getListView().clearChoices();
		}
	}

	public ListView getListView() {
		return mHeaderView;
	}

	public void setListAdapter(ListAdapter adapter) {
		synchronized (this) {
			mAdapter = adapter;
			mHeaderView.setAdapter(adapter);
		}
	}

	public void onHeaderClick(Header header, int position) {
		if (header.fragment != null) {
			switchToHeader(header);
		} else if (header.intent != null) {
			startActivity(header.intent);
		}
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		if (mAdapter != null) {
			Object item = mAdapter.getItem(position);
			if (item instanceof Header)
				onHeaderClick((Header) item, position);
		}
	}

	private AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			onListItemClick((ListView) parent, v, position, id);
		}
	};

	public void onBuildHeaders(List<Header> target) {
		// Should be overloaded by subclasses
	}

	private static class HeaderAdapter extends ArrayAdapter<Header> {
		private static class HeaderViewHolder {
			ImageView icon;
			TextView title;
			TextView summary;
		}

		private LayoutInflater mInflater;

		public HeaderAdapter(Context context, List<Header> objects) {
			super(context, 0, objects);
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			HeaderViewHolder holder;
			View view;

			if (convertView == null) {
				view = mInflater.inflate(R.layout.header_item, parent, false);
				holder = new HeaderViewHolder();
				holder.icon = (ImageView) view.findViewById(R.id.icon);
				holder.title = (TextView) view.findViewById(R.id.title);
				holder.summary = (TextView) view.findViewById(R.id.summary);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (HeaderViewHolder) view.getTag();
			}

			// All view fields must be updated every time, because the view may
			// be recycled
			Header header = getItem(position);
			holder.icon.setImageResource(header.iconRes);
			holder.title.setText(header.getTitle(getContext().getResources()));
			CharSequence summary = header.getSummary(getContext()
					.getResources());
			if (!TextUtils.isEmpty(summary)) {
				holder.summary.setVisibility(View.VISIBLE);
				holder.summary.setText(summary);
			} else {
				holder.summary.setVisibility(View.GONE);
			}

			return view;
		}
	}

	public static final long HEADER_ID_UNDEFINED = -1;

	/**
	 * Description of a single Header item that the user can select.
	 */
	public static final class Header implements Parcelable {
		/**
		 * Identifier for this header, to correlate with a new list when it is
		 * updated. The default value is
		 * {@link PreferenceActivity#HEADER_ID_UNDEFINED}, meaning no id.
		 * 
		 * @attr ref android.R.styleable#PreferenceHeader_id
		 */
		public long id = HEADER_ID_UNDEFINED;
		public int titleRes;
		public CharSequence title;
		public int summaryRes;
		public CharSequence summary;
		public int iconRes;
		public String fragment;
		public Bundle fragmentArguments;
		public Intent intent;
		public Bundle extras;

		public Header() {
		}

		public CharSequence getTitle(Resources res) {
			if (titleRes != 0) {
				return res.getText(titleRes);
			}
			return title;
		}

		public CharSequence getSummary(Resources res) {
			if (summaryRes != 0) {
				return res.getText(summaryRes);
			}
			return summary;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeLong(id);
			dest.writeInt(titleRes);
			TextUtils.writeToParcel(title, dest, flags);
			dest.writeInt(summaryRes);
			TextUtils.writeToParcel(summary, dest, flags);
			dest.writeInt(iconRes);
			dest.writeString(fragment);
			dest.writeBundle(fragmentArguments);
			if (intent != null) {
				dest.writeInt(1);
				intent.writeToParcel(dest, flags);
			} else {
				dest.writeInt(0);
			}
			dest.writeBundle(extras);
		}

		public void readFromParcel(Parcel in) {
			id = in.readLong();
			titleRes = in.readInt();
			title = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
			summaryRes = in.readInt();
			summary = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
			iconRes = in.readInt();
			fragment = in.readString();
			fragmentArguments = in.readBundle();
			if (in.readInt() != 0) {
				intent = Intent.CREATOR.createFromParcel(in);
			}
			extras = in.readBundle();
		}

		Header(Parcel in) {
			readFromParcel(in);
		}

		public static final Creator<Header> CREATOR = new Creator<Header>() {
			public Header createFromParcel(Parcel source) {
				return new Header(source);
			}

			public Header[] newArray(int size) {
				return new Header[size];
			}
		};

		@Override
		public int describeContents() {
			return 0;
		}
	}

	FragmentObserver mObserver = null;
	
	public void updateData(int type, Object key, Object data) {
		super.updateData(type, key, data);
		
		if (mObserver != null) {
			mObserver.updateData(mObserver, type, key, data);
		}
	}

	public void notifyEvent(int type, Object event) {
		super.notifyEvent(type, event);
		
		if (mObserver != null) {
			mObserver.notifyEvent(mObserver, type, event);
		}
	}

	@Override
	public Service getService() {
		return mService;
	}
	
	public void registerObserver(FragmentObserver observer) {
		mObserver = observer;
	}
	
	public void unregisterObserver(FragmentObserver observer) {
		if (mObserver == observer) {
			mObserver = null;
		}
	}
	
	public void getSmartcardInfo(FragmentObserver observer, int type) {
		if (mService != null) {
			mService.getSmartcardInfo(this, type);
		}
	}
	
	public void manageCA(FragmentObserver observer, int type) {
		if (mService != null) {
			mService.manageCA(this, type);
		}
	}
	
	public void getMailContent(FragmentObserver observer, String id) {
		if (mService != null) {
			mService.getMailContent(this, id);
		}
	}
}
