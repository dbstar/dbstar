package com.dbstar.guodian.app.base;

import java.util.HashMap;
import java.util.Map;

import android.app.DownloadManager.Request;
import android.content.pm.PackageParser.NewPermissionInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup.MarginLayoutParams;

import com.dbstar.R;
import com.dbstar.app.GDBaseActivity;
import com.dbstar.model.EventData;
import com.dbstar.util.ToastUtil;
import com.dbstar.widget.GDLoadingDialogView;

public class GDSmartActivity extends GDBaseActivity {
	
	private static final String TAG = "GDSmartActivity";
	private static final long DefaultTimeout = 5000;
	private static final long NoResponseTimeout = 30000;
	private static final int MaxReconnectCount = 3;
	private long mTimeout = DefaultTimeout;
	private int mReconnectCount = 0;
	private boolean mStartReconnect = false;
	private GDLoadingDialogView mLoadDialogView;
	public Map<String, Object> mCacheRequest;
    private static final String REQUEST_TYPE = "request_type";
    private static final String REQUEST_DATA = "request_data";
    private static final String REQUEST_SHOW_DIALOG = "request_show_dialog";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCacheRequest = new HashMap<String, Object>();
    }
	private Runnable mTimeoutTask = new Runnable() {

		public void run() {
		    if(mService.isSocketConected())
		        return;
			handleRequestTimeout();
		}
		
	};
	private Runnable mNoResponseTask = new Runnable() {
        @Override
        public void run() {
            if(mLoadDialogView != null && mLoadDialogView.isShowing()){
                ToastUtil.showToast(GDSmartActivity.this, R.string.family_text_request_timeout);
                hideLoadingDialog();
            }
        }
    };
	public void notifyEvent(int type, Object event) {
		super.notifyEvent(type, event);
		if (type == EventData.EVENT_GUODIAN_DATA) {
			handleRequestFinished();
		} else if (type == EventData.EVENT_GUODIAN_DISCONNECTED) {
			handleDisconnected();
		} else if (type == EventData.EVENT_GUODIAN_CONNECT_FAILED) {
			handleConnectFailed();
		} else if (type == EventData.EVENT_GUODIAN_CONNECTED) {
			handleConnected();
		} else if (type == EventData.EVENT_LOGIN_SUCCESSED) {
			handleLoginSuccessed();
		} else if (type == EventData.EVENT_GUODIAN_RECONNECTTING) {
			handleReconnecting();
		} else if(type == EventData.EVENT_GUODIAN_DATA_ERROR){
			handleErrorResponse();
		}
	}
	
	private void handleErrorResponse() {
	    mHandler.removeCallbacks(mNoResponseTask);
	    hideLoadingDialog();
	    mCacheRequest.clear();
    }

    protected void handleDisconnected() {
		Log.d(TAG, "handleDisconnected");
		
		mHandler.removeCallbacks(mTimeoutTask);
		
		if (!mStartReconnect) {
			mStartReconnect = true;
			mReconnectCount = 0;
		}

		mReconnectCount++;
		if (mReconnectCount > MaxReconnectCount) {
			Log.d(TAG, "reach max reconnect count");
			
			mStartReconnect = false;
			connectFailed();
			return;
		}

		mService.reconnect();
	}
	
    protected void handleConnectFailed() {
        Log.d(TAG, "handleConnectFailed");
        
        mHandler.removeCallbacks(mTimeoutTask);
        
        if (!mStartReconnect) {
            mStartReconnect = true;
            mReconnectCount = 0;
        }
        
        mReconnectCount++;
        if (mReconnectCount > MaxReconnectCount) {
            Log.d(TAG, "reach max reconnect count");
            
            mStartReconnect = false;
            connectFailed();
            return;
        }

        mService.reconnect();
    }
	
	protected void handleConnected() {
		Log.d(TAG, "handleConnected");
	}
	
	protected void handleLoginSuccessed() {
		Log.d(TAG, "handleLoginSuccessed");
		
		if (mStartReconnect) {
			mStartReconnect = false;
			mReconnectCount = 0;
		}
		Integer type = (Integer) mCacheRequest.get(REQUEST_TYPE);
		Object args = mCacheRequest.get(REQUEST_DATA);
		
		if(type != null && args != null){
		    requestDataNotShowDialog(type, args);
		}
	}
	
	protected void handleReconnecting() {
		Log.d(TAG, "handleReconnecting");
	}
	
	protected void handleRequestTimeout() {
		Log.d(TAG, "handleReconnecting");
		mHandler.removeCallbacks(mNoResponseTask);
		mStartReconnect = true;
		mReconnectCount = 0;
		mService.disconnect();
		hideLoadingDialog();
		mCacheRequest.clear();
		ToastUtil.showToast(this, R.string.network_connect_error);
	}
	
	protected void handleRequestFinished() {
	    mHandler.removeCallbacks(mNoResponseTask);
		mHandler.removeCallbacks(mTimeoutTask);
		hideLoadingDialog();
		mCacheRequest.clear();
	}
	
	private void connectFailed() {
	    mHandler.removeCallbacks(mNoResponseTask);
	    Log.d(TAG, "connectFailed");
	    hideLoadingDialog();
	    if(!mCacheRequest.isEmpty()){
	        mCacheRequest.clear();
	        ToastUtil.showToast(this, R.string.network_connect_error);
	    }
	}
	
	public void requestData(int type, Object args) {
		mService.requestPowerData(type,	args);
		mHandler.postDelayed(mTimeoutTask, mTimeout);
		mCacheRequest.put(REQUEST_TYPE, type);
        mCacheRequest.put(REQUEST_DATA, args);
		showLoadingDialog();
        mHandler.postDelayed(mNoResponseTask, NoResponseTimeout);
		
	}
	
	public void requestDataNotShowDialog(int type,Object args){
	    mService.requestPowerData(type, args);
        mHandler.postDelayed(mTimeoutTask, mTimeout);
        mCacheRequest.put(REQUEST_TYPE, type);
        mCacheRequest.put(REQUEST_DATA, args);
	}
	    
	protected void showLoadingDialog(){
	    if(mLoadDialogView == null){
	        GDLoadingDialogView.Builder builder = new GDLoadingDialogView.Builder(this, R.layout.gd_wait_dialog_view);
    	    mLoadDialogView = builder.create();
    	    mLoadDialogView.ShowDialog();
	    }else {
	        if(!mLoadDialogView.isShowing())
	            mLoadDialogView.ShowDialog();
	    }
	    
	   
	    
	}
	
	protected void hideLoadingDialog(){
	    if(mLoadDialogView != null && mLoadDialogView.isShowing()){
	      /* mHandler.postDelayed(hideDialogTask, 500);*/
	        mLoadDialogView.hideDialog();
	    }
	}
	
	Runnable hideDialogTask = new Runnable() {
        
        @Override
        public void run() {
            mLoadDialogView.hideDialog();
        }
    };
    
    protected void onStop() {
        super.onStop();
        if(mLoadDialogView != null && mLoadDialogView.isShowing())
            mLoadDialogView.dismiss();
    };
}
