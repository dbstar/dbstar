package com.dbstar.guodian;

import com.dbstar.guodian.GDClient.Task;
import com.dbstar.guodian.data.ElectricityPrice;
import com.dbstar.guodian.data.LoginData;
import com.dbstar.guodian.data.PowerPanelData;
import com.dbstar.model.EventData;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class GDEngine {
	private static final String TAG = "GDEngine";
	
	public static final int MSG_CONNECT_SUCCESSED = 0x1001;
	public static final int MSG_REQUEST_FINISHED = 0x1002;

	GDClient mClient = null;
	Handler mHander = null;
	Context mContext = null;

	boolean mIsConnected = false;
	GDClientObserver mObserver;
	
	LoginData mLoginData;

	public GDEngine(Context context) {
		mContext = context;
		mHander = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MSG_CONNECT_SUCCESSED: {
					connectSuccessed();
					break;
				}
				case MSG_REQUEST_FINISHED: {
					handleFinishedRequest((Task) msg.obj);
					break;
				}
				}
			}
		};

		mClient = new GDClient(context, mHander);
	}

	public void start(String ip, int port, GDClientObserver observer) {
		mObserver = observer;
		mIsConnected = false;
		mClient.setHostAddress(ip, port);
		mClient.connectToServer();
	}

	public void stop() {
		mClient.stop();
	}
	
	public void destroy() {
		mObserver = null;
		mClient.destroy();
	}
	
	public PowerPanelData getPowerPanelData() {
		if (mLoginData != null)
			return mLoginData.PanelData;
		
		return null;
	}
	
	public ElectricityPrice getPowerPriceData() {
		return mLoginData.ElecPrice;
	}

	void handleFinishedRequest(Task task) {
		int requestType = task.TaskType;
		switch (requestType) {
		case GDClient.REQUEST_LOGIN: {
			loginFinished((LoginData) task.ParsedData);
			break;
		}
		}
	}

	void connectSuccessed() {
		Log.d(TAG, "======= connectSuccessed=========");
		mIsConnected = true;
		mClient.login();
		notifyEvent(EventData.EVENT_CONNECTED, null);
	}

	void loginFinished(LoginData data) {
		Log.d(TAG, "======= loginFinished=========");
		
		mLoginData = data;
		notifyEvent(EventData.EVENT_LOGIN_SUCCESSED, data);
	}

	void notifyEvent(int type, Object event) {
		if (mObserver != null) {
			mObserver.notifyEvent(type, event);
		}
	}
}
