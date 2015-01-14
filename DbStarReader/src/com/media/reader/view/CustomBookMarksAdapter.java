package com.media.reader.view;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.media.android.dbstarplayer.DbStarPlayer;
import com.media.android.dbstarplayer.R;
import com.media.dbstarplayer.book.Bookmark;
import com.media.zlibrary.core.resources.ZLResource;

public final class CustomBookMarksAdapter extends BaseAdapter{
	
	public static final int OPEN_ITEM_ID = 0;
	public static final int DELETE_ITEM_ID = 1;
	private final List<Bookmark> myBookmarks =Collections.synchronizedList(new LinkedList<Bookmark>());
	private final ZLResource myResource = ZLResource.resource("bookmarksView");
	private final Comparator<Bookmark> myComparator = new Bookmark.ByTimeComparator();
	private DbStarPlayer mActivity;
	private ListView mList;
	public CustomBookMarksAdapter(DbStarPlayer activity, ListView listView) {
		mActivity = activity;
		mList = listView;
		listView.setAdapter(this);
	}

	public List<Bookmark> bookmarks() {
		return Collections.unmodifiableList(myBookmarks);
	}

	public void addAll(final List<Bookmark> bookmarks) {
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				synchronized (myBookmarks) {
					for (Bookmark b : bookmarks) {
						final int position = Collections.binarySearch(myBookmarks, b, myComparator);
						if (position < 0) {
							myBookmarks.add(- position - 1, b);
						}
					}
				}
				notifyDataSetChanged();
				mList.post(new Runnable() {
		            
		            @Override
		            public void run() {
		                mList.requestFocus();
		                if(getCount() >0)
		                    mList.setSelection(0);
		            }
		        });
			}
		});
	}

	public void add(final Bookmark b) {
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				synchronized (myBookmarks) {
					final int position = Collections.binarySearch(myBookmarks, b, myComparator);
					if (position < 0) {
						myBookmarks.add(- position - 1, b);
					}
				}
				notifyDataSetChanged();
			}
		});
	}

	public void remove(final Bookmark b) {
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				myBookmarks.remove(b);
				notifyDataSetChanged();
			}
		});
	}

	public void clear() {
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				myBookmarks.clear();
				notifyDataSetChanged();
			}
		});
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final View view = (convertView != null) ? convertView :
			LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_item, parent, false);
		final ImageView imageView = (ImageView)view.findViewById(R.id.bookmark_item_icon);
		final TextView textView = (TextView)view.findViewById(R.id.bookmark_item_text);
		final TextView bookTitleView = (TextView)view.findViewById(R.id.bookmark_item_booktitle);

		final Bookmark bookmark = getItem(position);
		if (bookmark == null) {
			imageView.setVisibility(View.VISIBLE);
			imageView.setImageResource(R.drawable.ic_list_plus);
			textView.setText(myResource.getResource("new").getValue());
			bookTitleView.setVisibility(View.GONE);
		} else {
			imageView.setVisibility(View.GONE);
			textView.setText((position+1)+". "+bookmark.getText());
			bookTitleView.setVisibility(View.VISIBLE);
			bookTitleView.setText(bookmark.getBookTitle());
		}
		return view;
	}

	@Override
	public final boolean areAllItemsEnabled() {
		return true;
	}

	@Override
	public final boolean isEnabled(int position) {
		return true;
	}

	@Override
	public final long getItemId(int position) {
		return position;
	}

	@Override
	public final Bookmark getItem(int position) {
		return (position >= 0) ? myBookmarks.get(position) : null;
	}

	@Override
	public final int getCount() {
		return myBookmarks.size();
	}
}