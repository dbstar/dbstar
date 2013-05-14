package com.dbstar.guodian.egine;

import java.util.Map;

import com.dbstar.guodian.data.ElectriDimension;
import com.dbstar.guodian.data.ElectricityPrice;
import com.dbstar.guodian.data.LoginData;
import com.dbstar.guodian.egine.GDClient.Task;
import com.dbstar.model.EventData;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class GDEngine {
	private static final String TAG = "GDEngine";
	
	public static final int MSG_CONNECTED = 0x1001;
	public static final int MSG_DISCONNECTED= 0x1002;
	public static final int MSG_CONNECT_ALREADY = 0x1003;
	public static final int MSG_CONNECT_FAILED = 0x1004;
	public static final int MSG_REQUEST_FINISHED = 0x1005;
	public static final int MSG_REQUEST_ERROR = 0x1006;
	public static final int MSG_SOCKET_ERROR = 0x1007;
	
	private static final int STATE_NONE = 0x01 ;
	private static final int STATE_CONNECTING = 0x02 ;
	private static final int STATE_CONNECTED = 0x03 ;
	private static final int STATE_DISCONNECTING = 0x04 ;
	private static final int STATE_DISCONNECTED = 0x5 ;
	
	private static final int LOGIN_ISLOGINGIN = 0x1;
	private static final int LOGIN_ISLOGIN = 0x2;
	private static final int LOGIN_NOTLOGIN = 0x3;

	private static final String UserId = "daotang0104";

	private GDClient mClient = null;
	private Handler mHander = null;
	private Context mContext = null;

	private int mState = STATE_NONE;
	private int mLoginState = LOGIN_NOTLOGIN;
	private GDClientObserver mObserver;
	
	private LoginData mLoginData;
	private ElectriDimension mEleDimension;
	private String mCtrlNoGuid;
	private String mUserType;
	private String mUserId;
	private long mReconnectTime;
	
	private static final int REPEATLOGIN_COUNT = 20;
	private int mRepeatLoginCount = 0;

	public GDEngine(Context context) {
		mContext = context;
		mHander = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MSG_CONNECTED: {
					handleConnected();
					break;
				}
				case MSG_CONNECT_ALREADY: {
					handleConnectedAlready();
					break;
				}
				case MSG_REQUEST_FINISHED: {
					handleFinishedRequest((Task) msg.obj);
					break;
				}
				case MSG_REQUEST_ERROR: {
					handleRequestError(msg.arg1);
					break;
				}
				
				case MSG_CONNECT_FAILED: {
					handleConnectFailed();
					break;
				}
				case MSG_SOCKET_ERROR: {
					handleSocketError();
					break;
				}
				}
			}
		};

		mClient = new GDClient(context, mHander);
	}

	// this maybe called many times, event the socket is
	// connected already.
	// when called with socket connected already, 
	// connectSuccessed() will not be invoked, so there is no
	// chance to set mIsConnected to true again.
	public void start(String ip, int port, GDClientObserver observer) {
		mObserver = observer;
		
		if (mState != STATE_CONNECTED && mState != STATE_CONNECTING) {
			// not connected
			mState = STATE_CONNECTING;
			mClient.setHostAddress(ip, port);
			mClient.connectToServer();
		}
	}
	
	public void setReconnectTime (long time) {
		mReconnectTime = time;
	}
	
	public void restart() {
		Log.d(TAG, " == restart == ");

		mState = STATE_NONE;
		mLoginState = LOGIN_NOTLOGIN;
		
		mClient.connectToServerDelayed(mReconnectTime);
	}

	public void stop() {
		Log.d(TAG, " ===== stop guodian engine ======= ");
		mState = STATE_DISCONNECTED;
		mClient.stop();
	}
	
	public void destroy() {
		mObserver = null;
		mClient.destroy();
	}

	public void requestData(int type, Object args) {
		switch(type) {
		case GDConstract.DATATYPE_POWERPANELDATA: {
			getPowerPanelData();
			break;
		}
		
		case GDConstract.DATATYPE_BILLDETAILOFMONTH: {
			String date = (String) args;
			getBillDetailOfMonth(date);
			break;
		}
		
		case GDConstract.DATATYPE_BILLDETAILOFRECENT: {
			String dateNum = (String) args;
			getBillDetailOfRecent(dateNum);
			break;
		}
		case GDConstract.DATATYPE_BILLMONTHLIST: {
			String yearNum = (String) args;
			getBillMonthList(yearNum);
			break;
		}
		
		case GDConstract.DATATYPE_NOTICES: {
			getNotices();
			break;
		}
		
		case GDConstract.DATATYPE_USERAREAINFO: {
			String areaIdPath = (String) args;
			getUserAreaInfo(areaIdPath);
			break;
		}
		
		case GDConstract.DATATYPE_BUSINESSAREA: {
			String areaId = (String) args;
			getBusinessAreas(areaId);
			break;
		}
		case GDConstract.DATATYPE_CITYES:
		    String pid = (String) args;
		    getCitys(pid);
		    break;
		case GDConstract.DATATYPE_ZONES:
		    String cid = (String) args;
            getZones(cid);
		    break;
		case GDConstract.DATATYPE_ELECTRICAL_DIMENSIONALTIY:
		    Map<String, String> params = (Map<String, String>) args;
		    getElecDimension(params);
		}
	}


    public ElectricityPrice getElecPrice() {
		if (mLoginData != null) {
			return mLoginData.ElecPrice;
		}
		
		return null;
	}
    public LoginData getLoginData() {
        if (mLoginData != null) {
            return mLoginData;
        }
        
        return null;
    }
    public ElectriDimension getElectriDimension() {
        if (mEleDimension != null) {
            return mEleDimension;
        }
        
        return null;
    }
	private void getPowerPanelData() {
		Log.d(TAG, " ======== is connected ==== " + (mState == STATE_CONNECTED));
		if (mState == STATE_CONNECTED) {
			mClient.getPowerPanelData(mUserId, mCtrlNoGuid, mUserType);
		}
	}
	
	private void getBillDetailOfMonth(String date) {
		if (mState == STATE_CONNECTED) {
			mClient.getBillDetailOfMonth(mUserId, mCtrlNoGuid, date);
		}
	}
	
	private void getBillDetailOfRecent(String dateNum) {
		if (mState == STATE_CONNECTED) {
			mClient.getBillDetailOfRecent(mUserId, mCtrlNoGuid, dateNum);
		}
	}
	
	private void getBillMonthList(String yearNum) {
		if (mState == STATE_CONNECTED) {
			mClient.getBillMonthList(mUserId, mCtrlNoGuid, yearNum);
		}
	}
	
	private void getNotices() {
		if (mState == STATE_CONNECTED) {
			mClient.getNotices(mUserId, mCtrlNoGuid);
		}
	}
	
	private void getUserAreaInfo(String areaIdPath) {
		if (mState == STATE_CONNECTED) {
			mClient.getUserAreaInfo(mUserId, areaIdPath);
		}
	}
	
	private void getBusinessAreas(String areaId) {
		if (mState == STATE_CONNECTED) {
			mClient.getBusinessArea(mUserId, areaId);
		}
	}

	private void getCitys(String pid){
	    if (mState == STATE_CONNECTED) {
            mClient.getCitysArea(mUserId, pid);
        }
	}
	private void getZones(String pid){
        if (mState == STATE_CONNECTED) {
            mClient.getZonesArea(mUserId, pid);
        }
    }
	private void getElecDimension(Map<String, String> params) {
	    if (mState == STATE_CONNECTED) {
            mClient.getElecDimension(mUserId,params);
        }
    }
	private void handleFinishedRequest(Task task) {
		int requestType = task.TaskType;
		switch (requestType) {
		case GDClient.REQUEST_LOGIN: {
			loginFinished((LoginData) task.ParsedData);
			break;
		}
		
		case GDClient.REQUEST_POWERPANELDATA: {
			requestFinished(GDConstract.DATATYPE_POWERPANELDATA, task.ParsedData);
			break;
		}
		
		case GDClient.REQUEST_BILLMONTHLIST: {
			requestFinished(GDConstract.DATATYPE_BILLMONTHLIST, task.ParsedData);
			break;
		}
		
		case GDClient.REQUEST_BILLDETAILOFMONTH: {
			requestFinished(GDConstract.DATATYPE_BILLDETAILOFMONTH, task.ParsedData);
			break;
		}
		case GDClient.REQUEST_BILLDETAILOFRECENT: {
			requestFinished(GDConstract.DATATYPE_BILLDETAILOFRECENT, task.ParsedData);
			break;
		}
		case GDClient.REQUEST_NOTICE: {
			requestFinished(GDConstract.DATATYPE_NOTICES, task.ParsedData);
			break;
		}
		case GDClient.REQUEST_USERAREAINFO: {
			requestFinished(GDConstract.DATATYPE_USERAREAINFO, task.ParsedData);
			break;
		}
		case GDClient.REQUEST_BUSINESSAREA: {
			requestFinished(GDConstract.DATATYPE_BUSINESSAREA, task.ParsedData);
			break;
		}
		case GDClient.REQUEST_CITYS:
		    requestFinished(GDConstract.DATATYPE_CITYES, task.ParsedData);
		    break;
		case GDClient.REQUEST_ZONES:
		    requestFinished(GDConstract.DATATYPE_ZONES, task.ParsedData);
		    break;
		case GDClient.REQUEST_ELECTRICAL_DIMENSIONALITY:
		    requestFinished(GDConstract.DATATYPE_ELECTRICAL_DIMENSIONALTIY, task.ParsedData);
		    if(task.ParsedData != null)
		        mEleDimension = (ElectriDimension) task.ParsedData;
		    break;
		}
	}
	
	private void handleRequestError(int error) {
		if (error == GDConstract.ErrorCodeRepeatLogin) {
			mRepeatLoginCount++;
			
			Log.d(TAG, " == handleRequestError == " + error);
			
			if (mRepeatLoginCount == REPEATLOGIN_COUNT) {
				mRepeatLoginCount = 0;
				return;
			}
			
			mClient.login();
		}
	}

	private void handleSocketError() {
		Log.d(TAG, " == handleSocketError == ");
		
		restart();
	}

	private void handleConnectFailed() {
		Log.d(TAG, " == handleConnectFailed == ");
		
		restart();
	}

	private void handleConnected() {
		Log.d(TAG, "======= connectSuccessed=========");
		mState = STATE_CONNECTED;
		
		if (mLoginState != LOGIN_ISLOGIN) {
			mLoginState = LOGIN_ISLOGINGIN;
			mClient.login();
		} else {
			if (mLoginData != null) {
				EventData.GuodianEvent event = new EventData.GuodianEvent();
				event.Type = GDConstract.DATATYPE_LOGIN;
				event.Data = mLoginData;

				notifyEvent(EventData.EVENT_LOGIN_SUCCESSED, event);
			}
		}
	}
	
	private void handleConnectedAlready() {
		
		Log.d(TAG, " == handleConnectedAlready == state =" + mState + " login=" + mLoginState);
		
		if (mState != STATE_CONNECTED) {
			mState = STATE_CONNECTED;
		}
		
		if (mLoginState != LOGIN_ISLOGIN && mLoginState != LOGIN_ISLOGINGIN) {
			mLoginState = LOGIN_ISLOGINGIN;
			mClient.login();
		}
	}

	private void loginFinished(LoginData data) {
		Log.d(TAG, "======= loginFinished=========");
		mLoginState = LOGIN_ISLOGIN;
		
		mLoginData = data;
		
		if (data != null) {
			if (data.CtrlNo != null) {
				mCtrlNoGuid = data.CtrlNo.CtrlNoGuid;
			}
			
			if (data.UserData != null && data.UserData.UserInfo != null) {
				mUserType = data.UserData.UserInfo.UserType;
				mUserId = data.UserData.UserInfo.Account;
			}
		}
		
		EventData.GuodianEvent event = new EventData.GuodianEvent();
		event.Type = GDConstract.DATATYPE_LOGIN;
		event.Data = data;

		notifyEvent(EventData.EVENT_LOGIN_SUCCESSED, event);
	}
	
	private void requestFinished(int type, Object data) {
		EventData.GuodianEvent event = new EventData.GuodianEvent();
		event.Type = type;
		event.Data = data;
		notifyEvent(EventData.EVENT_GUODIAN_DATA, event);
	}

	private void notifyEvent(int type, Object event) {
		if (mObserver != null) {
			mObserver.notifyEvent(type, event);
		}
	}
}
