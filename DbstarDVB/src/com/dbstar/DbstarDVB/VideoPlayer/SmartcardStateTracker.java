package com.dbstar.DbstarDVB.VideoPlayer;

import com.dbstar.DbstarDVB.DbstarServiceApi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

public class SmartcardStateTracker {
	protected static final String TAG = "SmartcardStateTracker";

	// Message to UI for smart card
	public static final int MSG_SMARTCARD_INSERTING = 0x40001;
	public static final int MSG_SMARTCARD_INSERTED = 0x40002;
	public static final int MSG_SMARTCARD_INVALID = 0x40003;
	public static final int MSG_SMARTCARD_REMOVED = 0x40004;

	// smart card state
	public static final int SMARTCARD_STATE_NONE = 0x1000;
	public static final int SMARTCARD_STATE_INSERTING = 0x1001;
	public static final int SMARTCARD_STATE_INSERTED = 0x1002;
	public static final int SMARTCARD_STATE_INVALID = 0x1003;
	public static final int SMARTCARD_STATE_REMOVING = 0x1004;
	public static final int SMARTCARD_STATE_REMOVED = 0x1005;

	private int mSmartcardState = SMARTCARD_STATE_NONE;

	Context mContext;
	Handler mHandler;

	public SmartcardStateTracker(Context context, Handler handler) {
		mContext = context;
		mHandler = handler;
		reqisterReceiver();
	}

	public void destroy() {
		mContext.unregisterReceiver(mReceiver);
	}

	public int getSmartcardState() {
		return mSmartcardState;
	}

	private void reqisterReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(DbstarServiceApi.ACTION_SMARTCARD_IN);
		filter.addAction(DbstarServiceApi.ACTION_SMARTCARD_OUT);
		filter.addAction(DbstarServiceApi.ACTION_NOTIFY);

		mContext.registerReceiver(mReceiver, filter);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			Log.d(TAG, "onReceive " + action);

			if (action.equals(DbstarServiceApi.ACTION_SMARTCARD_IN)) {
				mSmartcardState = SMARTCARD_STATE_INSERTING;
				mHandler.sendEmptyMessage(MSG_SMARTCARD_INSERTING);
			} else if (action.equals(DbstarServiceApi.ACTION_SMARTCARD_OUT)) {
				mSmartcardState = SMARTCARD_STATE_REMOVING;
				mHandler.sendEmptyMessage(MSG_SMARTCARD_REMOVED);
			} else if (action.equals(DbstarServiceApi.ACTION_NOTIFY)) {
				int type = intent.getIntExtra("type", 0);
				Log.d(TAG, " notifiy type =  " + type);

				switch (type) {
				case DbstarServiceApi.DRM_SC_INSERT_OK: {
					mSmartcardState = SMARTCARD_STATE_INSERTED;
					mHandler.sendEmptyMessage(MSG_SMARTCARD_INSERTED);
					break;
				}
				case DbstarServiceApi.DRM_SC_INSERT_FAILED: {
					mSmartcardState = SMARTCARD_STATE_INVALID;
					mHandler.sendEmptyMessage(MSG_SMARTCARD_INVALID);
					break;
				}
				case DbstarServiceApi.DRM_SC_REMOVE_OK: {
					mSmartcardState = SMARTCARD_STATE_REMOVED;
					break;
				}
				case DbstarServiceApi.DRM_SC_REMOVE_FAILED: {
					mSmartcardState = SMARTCARD_STATE_INVALID;
					break;
				}
				}
			}
		}
	};
}
