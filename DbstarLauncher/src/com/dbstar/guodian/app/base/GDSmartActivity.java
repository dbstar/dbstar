package com.dbstar.guodian.app.base;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.dbstar.R;
import com.dbstar.app.GDBaseActivity;
import com.dbstar.model.EventData;
import com.dbstar.widget.GDLoadingDialogView;

public class GDSmartActivity extends GDBaseActivity {

    private static final String TAG = "GDSmartActivity";
    private static final long DefaultTimeout = 8000;
    private static final long NoNotifyTimeout = 2000;
    private static final long NoResponseTimeout = 40000;
    private static final int MaxReconnectCount = 3;
    private long mTimeout = DefaultTimeout;
    private int mReconnectCount = 0;
    private boolean mStartReconnect = false;
    private GDLoadingDialogView mLoadDialogView;
    public Map<String, Object> mCacheRequest;
    private static final String REQUEST_TYPE = "request_type";
    private static final String REQUEST_DATA = "request_data";
    private boolean isFirstRequest = true;
    private boolean isOnReStart = false;
    protected View mPageContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCacheRequest = new HashMap<String, Object>();
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        if(!isOnReStart){
            isOnReStart = true;
            mPageContent = findViewById(R.id.page_content);
            if(mPageContent != null){
                mPageContent.setVisibility(View.INVISIBLE);
            }
        }
    }
    private Runnable mTimeoutTask = new Runnable() {

        public void run() {
            handleRequestTimeout();
        }

    };
    private Runnable mNoNotifyTask = new Runnable() {
        @Override
        public void run() {
            handCanNotConnectToServer();
        }
    };
    
    private Runnable mNoResponseTask =  new Runnable() {
        
        @Override
        public void run() {
            if (mLoadDialogView != null && mLoadDialogView.isShowing())
                handCanNotConnectToServer();
        }
    };
    public void notifyEvent(int type, Object event) {
        super.notifyEvent(type, event);
        
        if(type !=  EventData.EVENT_GUODIAN_CONNECTED){
            mHandler.removeCallbacks(mNoNotifyTask);
            mHandler.removeCallbacks(mNoResponseTask);
        }
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
        } else if (type == EventData.EVENT_GUODIAN_DATA_ERROR) {
            hideloadingPage();
        }
    }

    public void handleErrorResponse(final int errorStrId) {
        mHandler.removeCallbacks(mTimeoutTask);
        if(mLoadDialogView == null)
                showLoadingPage();
        if(!mLoadDialogView.isShowing())
            mLoadDialogView.ShowDialog();
        mLoadDialogView.showLoadErrorInfo(errorStrId);
        if(mPageContent != null && mPageContent.getVisibility() == View.VISIBLE){
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideloadingPage();
                }
            }, 1000);
        }else{
            mLoadDialogView.setCancelable(true);
        }
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

        if (type != null && args != null) {
            requestDataNotShowDialog(type, args);
        }
    }

    protected void handleReconnecting() {
        Log.d(TAG, "handleReconnecting");
    }

    protected void handleRequestTimeout() {
        Log.d(TAG, "handleRequestTimeout");
        mStartReconnect = true;
        mReconnectCount = 0;
        mService.disconnect();
        mHandler.postDelayed(mNoNotifyTask, NoNotifyTimeout);
    }

    protected void handleRequestFinished() {
        mHandler.removeCallbacks(mTimeoutTask);
        hideloadingPage();
        if(mPageContent != null){
            mPageContent.setVisibility(View.VISIBLE);
        }
        
    }

    private void connectFailed() {
        // mHandler.removeCallbacks(mNoResponseTask);
        Log.d(TAG, "connectFailed");
        handCanNotConnectToServer();

    }

    public void requestData(int type, Object args) {
        Log.d(TAG, "requestData");
        mHandler.removeCallbacks(mTimeoutTask);
        mService.requestPowerData(type, args);
        mHandler.postDelayed(mTimeoutTask, mTimeout);
        mCacheRequest.put(REQUEST_TYPE, type);
        mCacheRequest.put(REQUEST_DATA, args);
        showLoadingPage();
        mHandler.postDelayed(mNoResponseTask, NoResponseTimeout);

    }

    public void requestDataNotShowDialog(int type, Object args) {
        Log.d(TAG, "requestDataNotShowDialog");
        mHandler.removeCallbacks(mTimeoutTask);
        mService.requestPowerData(type, args);
        mHandler.postDelayed(mTimeoutTask, mTimeout);
        mCacheRequest.put(REQUEST_TYPE, type);
        mCacheRequest.put(REQUEST_DATA, args);
    }

    protected void showLoadingPage() {
        if (mLoadDialogView == null) {
            GDLoadingDialogView.Builder builder = new GDLoadingDialogView.Builder(
                    this, R.layout.no_network_error_view);
            mLoadDialogView = builder.create();
            mLoadDialogView.ShowDialog();
        } else {
            if (!mLoadDialogView.isShowing())
                mLoadDialogView.ShowDialog();
        }
        mLoadDialogView.showLoadingInfo();
        mLoadDialogView.setCancelable(false);
    }
/*      protected void showLoadErrorPage(int resId){
          mHandler.removeCallbacks(mNoNotifyTask);
          mHandler.removeCallbacks(mNoResponseTask);
          showLoadingPage();
          mLoadDialogView.showLoadErrorInfo(resId);
          mLoadDialogView.setCancelable(true);
      }*/
    protected void showNoNetWorkPage() {
        showLoadingPage();
        handCanNotConnectToServer();
    }

    protected void hideloadingPage() {
        mHandler.removeCallbacks(mNoResponseTask);
        if (mLoadDialogView != null && mLoadDialogView.isShowing()) {
            mLoadDialogView.hideDialog();
        }
    }

    protected void onStop() {
        super.onStop();
        if(mLoadDialogView != null)
            mLoadDialogView.dismiss();
    };

    private void handCanNotConnectToServer() {
        mHandler.removeCallbacks(mNoNotifyTask);
        mHandler.removeCallbacks(mNoResponseTask);
        mLoadDialogView.showNetWorkErrorInfo();
        mLoadDialogView.setCancelable(true);
        if(mPageContent != null && mPageContent.getVisibility() == View.VISIBLE){
            mHandler.postDelayed(new Runnable() {
                
                @Override
                public void run() {
                    hideloadingPage();
                }
            }, 1000);
        }
    }
}
