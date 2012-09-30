package com.dbstar.guodian;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dbstar.guodian.model.ClientObserver;
import com.dbstar.guodian.model.GDDataProviderService;
import com.dbstar.guodian.model.GDDiskInfo;
import com.dbstar.guodian.model.ReceiveEntry;
import com.dbstar.guodian.util.StringUtil;

public class GDReceiveStatusActivity extends GDBaseActivity {

	private static final String TAG = "GDReceiveStatusActivity";
	private static final int PageSize = 10;

	private static final int UpdatePeriodInMills = 5000;
	private static final int UpdatePeriodInSecs = 5;
	private boolean mReachPageEnd = false;
	private int mPageNumber = 0;
	private int mPageCount = 0;

	private List<ReceiveEntry[]> mPageDatas = null;
	private DownloadProgressAdapter mAdapter = null;

	private static final int MSG_UPDATEPROGRESS = 0;

	private ClientObserver mObserver = null;

	TextView mPageNumberView;
	ListView mListView;
	TextView mDownloadSpeedView;
	TextView mDiskInfoView;

	private Handler mUIUpdateHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_UPDATEPROGRESS: {
				if (mService != null && mBound) {
					mService.getDownloadStatus(mObserver, mPageNumber, PageSize);
				}
				break;
			}
			default:
				break;
			}
		}
	};

	Timer mTimer = new Timer();
	TimerTask mTask = new TimerTask() {
		public void run() {
			mUIUpdateHandler.sendEmptyMessage(MSG_UPDATEPROGRESS);
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mObserver = this;

		//mMenuPath = getResources().getString(R.string.systemsettings_settings);

		setContentView(R.layout.download_status_view);

		mPageDatas = new LinkedList<ReceiveEntry[]>();
		
		initializeView();
		
		Intent intent = getIntent();
		mMenuPath = intent.getStringExtra(INTENT_KEY_MENUPATH);
		showMenuPath(mMenuPath.split(MENU_STRING_DELIMITER));
	}

	public void onStart() {
		super.onStart();

		showMenuPath(mMenuPath.split(MENU_STRING_DELIMITER));
	}

	public void onStop() {
		super.onStop();

		mTimer.cancel();
		mTimer.purge();
	}

	public void onServiceStart() {
		super.onServiceStart();

		mTimer.schedule(mTask, 0, UpdatePeriodInMills);
	}

	public void onServiceStop() {
		super.onServiceStop();

		mTimer.cancel();
		mTimer.purge();
	}

	private void requestNewPage() {
		if (mService != null && mBound) {
			mService.getDownloadStatus(mObserver, mPageNumber + 1, PageSize);
		}
	}

	private void loadPrevPage() {
		if ((mPageNumber - 1) >= 0) {
			Log.d(TAG, "loadPrevPage");

			mPageNumber--;
			mPageNumberView.setText(formPageText(mPageNumber));

			ReceiveEntry[] entries = mPageDatas.get(mPageNumber);
			mAdapter.setDataSet(entries);
			mListView.clearChoices();
			mListView.setSelection(0);
			mAdapter.notifyDataSetChanged();
		}
	}

	private void loadNextPage() {
		Log.d(TAG, "loadNextPage");

		if (mPageCount == 0)
			return;

		if ((mPageNumber + 1) < mPageCount) {

			mPageNumber++;
			mPageNumberView.setText(formPageText(mPageNumber));

			ReceiveEntry[] entries = mPageDatas.get(mPageNumber);
			mAdapter.setDataSet(entries);
			mListView.clearChoices();
			mListView.setSelection(0);
			mAdapter.notifyDataSetChanged();

			// maybe can send request here

		} else {
			if (!mReachPageEnd) {
				requestNewPage();
			}
		}
	}

	public void updateData(int type, int param1, int param2, Object data) {
		if (type != GDDataProviderService.REQUESTTYPE_GETDOWNLOADSTATUS)
			return;

		int pageNumber = param1;
		int pageSize = param2;

		ReceiveEntry[] entries = (ReceiveEntry[]) data;
		ReceiveEntry[] preEntries = null;

		if (entries != null && entries.length > 0) {
			if (entries.length < pageSize) {
				mReachPageEnd = true;
			}

			if (pageNumber < mPageDatas.size()) {
				preEntries = mPageDatas.get(pageNumber);
				// update old page
				mPageDatas.set(pageNumber, entries);
			} else {
				// new page
				mPageDatas.add(pageNumber, entries);
				mPageCount++;
			}

			// update current page
			if (pageNumber == mPageNumber) {
				mPageNumberView.setText(formPageText(mPageNumber));
				mAdapter.setDataSet(entries);
				mAdapter.notifyDataSetChanged();

				if (preEntries != null) {
					long preSize = 0;
					long curSize = 0;
					for (int i = 0; i < preEntries.length; i++) {
						preSize += preEntries[i].RawProgress;
						curSize += entries[i].RawProgress;
					}

					float speed = (float) ((curSize - preSize) / 1024)
							/ (float) UpdatePeriodInSecs;
					String strSpeed = StringUtil.formatFloatValue(speed)
							+ "KB/s";
					mDownloadSpeedView.setText(strSpeed);
				}

				GDDiskInfo.DiskInfo diskInfo = null;
				if (mBound) {
					diskInfo = GDDiskInfo.getDiskInfo(mService.getStorageDisk(), true);
					if (diskInfo != null) {
						String diskSpaceStr = diskInfo.DiskSpace + "/"
								+ diskInfo.DiskSize;
						Log.d(TAG, " disk space = " + diskSpaceStr);
						mDiskInfoView.setText(diskSpaceStr);
					}
				}
			}

		} else {
			if (pageNumber > (mPageDatas.size() - 1)) {
				mReachPageEnd = true;
			}
		}
	}

	public void initializeView() {
		super.initializeView();

		mDownloadSpeedView = (TextView) findViewById(R.id.download_speed);
		mDiskInfoView = (TextView) findViewById(R.id.disk_info);
		mPageNumberView = (TextView) findViewById(R.id.pageNumberView);
		mAdapter = new DownloadProgressAdapter(this);
		mListView = (ListView) findViewById(R.id.download_view);
		mListView.setAdapter(mAdapter);

		mListView.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				boolean ret = false;
				int action = event.getAction();
				if (action == KeyEvent.ACTION_DOWN) {
					switch (keyCode) {

					case KeyEvent.KEYCODE_DPAD_UP: {
						loadPrevPage();
						ret = true;
						break;
					}
					case KeyEvent.KEYCODE_DPAD_DOWN: {
						loadNextPage();
						ret = true;
						break;
					}
					default:
						break;
					}

				}
				return ret;
			}
		});
	}

	public class DownloadProgressAdapter extends BaseAdapter {

		ReceiveEntry[] mDataSet = null;

		public void setDataSet(ReceiveEntry[] dataSet) {
			mDataSet = dataSet;
		}

		private class ItemHolder {
			TextView Name;
			ProgressBar ProgressView;
			TextView Progress;
			TextView PercentView;
		}

		public DownloadProgressAdapter(Context context) {
		}

		public int getCount() {
			int size = 0;

			if (mDataSet != null) {
				size = mDataSet.length;
			}

			Log.d(TAG, " DownloadProgressAdapter count = " + size);
			return size;
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ItemHolder holder = null;

			if (convertView == null) {
				holder = new ItemHolder();
				convertView = getLayoutInflater().inflate(
						R.layout.download_status_item, null);

				holder.Name = (TextView) convertView
						.findViewById(R.id.text_name);

				holder.ProgressView = (ProgressBar) convertView
						.findViewById(R.id.progress_bar);

				holder.Progress = (TextView) convertView
						.findViewById(R.id.text_progress);

				holder.PercentView = (TextView) convertView
						.findViewById(R.id.text_percent);

				convertView.setTag(holder);
			} else {
				holder = (ItemHolder) convertView.getTag();
			}

			holder.Name.setText(mDataSet[position].Name);

			String strProgress = mDataSet[position].Progress + "/"
					+ mDataSet[position].Total;
			holder.Progress.setText(strProgress);

			holder.ProgressView.setProgress(mDataSet[position].nProgress);

			holder.PercentView.setText(mDataSet[position].Percent);

			return convertView;
		}

	}

}
