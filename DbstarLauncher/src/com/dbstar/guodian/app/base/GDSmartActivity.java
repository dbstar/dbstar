package com.dbstar.guodian.app.base;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.view.View;

import com.dbstar.R;
import com.dbstar.app.GDBaseActivity;
import com.dbstar.guodian.engine1.RequestParams;
import com.dbstar.model.EventData;
import com.dbstar.util.LogUtil;
import com.dbstar.widget.GDLoadingDialogView;

public class GDSmartActivity extends GDBaseActivity {

    private static final String TAG = "GDSmartActivity";
    private static final long DefaultTimeout = 8000;
    //private static final long NoNotifyTimeout = 2000;
    private static final long NoResponseTimeout = 12000;
    private static final int MaxReconnectCount = 3;
    private long mTimeout = DefaultTimeout;
    private int mReconnectCount = 0;
    private boolean mStartReconnect = false;
    private GDLoadingDialogView mLoadDialogView;
    public Map<String, Object> mCacheRequest;
    protected  String mSystemFlag;
    protected  String mRequestMethodId;
//    private static final String REQUEST_TYPE = "request_type";
//    private static final String REQUEST_DATA = "request_data";
    private static final String REQUEST_PARAMS = "request_params";
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
//    private Runnable mNoNotifyTask = new Runnable() {
//        @Override
//        public void run() {
//            handCanNotConnectToServer();
//        }
//    };
    
    private Runnable mNoResponseTask =  new Runnable() {
        
        @Override
        public void run() {
            if (mLoadDialogView != null && mLoadDialogView.isShowing())
                handCanNotConnectToServer();
        }
    };
    public void notifyEvent(int type, Object event) {
        super.notifyEvent(type, event);
        
        if (type == EventData.EVENT_GUODIAN_DATA) {
            handleRequestFinished();
        }  else if (type == EventData.EVENT_LOGIN_SUCCESSED) {
            handleLoginSuccessed();
        }else if (type == EventData.EVENT_GUODIAN_DATA_ERROR) {
            hideloadingPage();
        }else if(type == EventData.EVENT_GUODIAN_CONNECT_FAILED){
            showNoNetWorkPage();
        }
    }

    public void showErrorMsg(final int errorStrId) {
        String error = getResources().getString(errorStrId);
        showErrorMsg(error);
    }
    
    public void showErrorMsg(String error) {
        mHandler.removeCallbacks(mTimeoutTask);
        mHandler.removeCallbacks(mNoResponseTask);
        if(mLoadDialogView == null)
                showLoadingPage();
        if(!mLoadDialogView.isShowing())
            mLoadDialogView.ShowDialog();
        mLoadDialogView.showLoadErrorInfo(error);
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
    
    public void showNotifyMessage(int rId){
        String msg = getResources().getString(rId);
        showNotifyMessage(msg);
    }
    
    public void showNotifyMessage(String msg){
        mHandler.removeCallbacks(mTimeoutTask);
        mHandler.removeCallbacks(mNoResponseTask);
        if(mLoadDialogView == null)
                showLoadingPage();
        if(!mLoadDialogView.isShowing())
            mLoadDialogView.ShowDialog();
        mLoadDialogView.showNotifyMsg(msg);
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
    protected void handleLoginSuccessed() {
        LogUtil.d(TAG, "handleLoginSuccessed");

        if (mStartReconnect) {
            mStartReconnect = false;
            mReconnectCount = 0;
        }
        RequestParams params = (RequestParams) mCacheRequest.get(REQUEST_PARAMS);
        if(params != null)
            requestDataNotShowDialog(params);
    }


    protected void handleRequestTimeout() {
        LogUtil.d(TAG, "handleRequestTimeout");
        if(!mService.isNetworkConnected()){
            handCanNotConnectToServer();
        }
    }

    protected void handleRequestFinished() {
        mHandler.removeCallbacks(mTimeoutTask);
        mHandler.removeCallbacks(mNoResponseTask);
        hideloadingPage();
        if(mPageContent != null){
            mPageContent.setVisibility(View.VISIBLE);
        }
        
    }

    public void requestData(RequestParams params) {
        LogUtil.d(TAG, "requestData");
        if(!mService.isNetworkConnected()){
           showNoNetWorkPage();
            return;
        }
        mHandler.removeCallbacks(mTimeoutTask);
        mService.requestData(params);
        mHandler.postDelayed(mTimeoutTask, mTimeout);
        mCacheRequest.put(REQUEST_PARAMS, params);
        showLoadingPage();
        mHandler.postDelayed(mNoResponseTask, NoResponseTimeout);

    }
    public void requestDataNotShowDialog(RequestParams params) {
        LogUtil.d(TAG, "requestDataNotShowDialog");
        mService.requestData(params);
        mCacheRequest.put(REQUEST_PARAMS, params);
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
        mHandler.removeCallbacks(mTimeoutTask);
        mHandler.removeCallbacks(mNoResponseTask);
        if(mLoadDialogView != null){
            mLoadDialogView.showNetWorkErrorInfo();
            mLoadDialogView.setCancelable(true);
        }
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
