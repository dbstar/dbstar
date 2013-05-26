package com.dbstar.settings.network;

import java.io.FileOutputStream;
import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dbstar.settings.R;
import com.dbstar.settings.base.BaseFragment;
import com.dbstar.settings.utils.SettingsCommon;

public class FinishSettingsPage extends BaseFragment {

	private static final String TAG = "FinishSettingsPage";

	TextView mEthernetStateView;
	TextView mWifiStateView;
	Button mOkButton, mPrevButton;

	boolean mIsChecked = false;
	boolean mFirstDisconnectInfo = true;

	private Handler mHandler;
	ConnectivityManager mConnectManager;
	private IntentFilter mConnectIntentFilter;

	private Timer mTimer = null;
	private TimerTask mTask = null;
	private  String mKeyChannel;
	class TimeoutTask implements Runnable {

		@Override
		public void run() {
			Log.d(TAG, "=== timeout === ");
			configureTimeout();
		}

	}

	void configureTimeout() {
	   mHandler.post(handleEthernetTask);
	   mHandler.post(handleWifiTask);
		stopTimer();
	}

	void stopTimer() {
	    Log.d(TAG, "============== stop Timer" );
		if (mTask != null) {
			mTask.cancel();
			mTask = null;
		}

		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}

   
    void handleNetworkConnectStatus(TextView view,boolean connected){
        int stringId = R.string.network_channel_cable;
        if(view.getId() == R.id.wifi_state_view){
            stringId =R.string.network_channel_wireless;
        }
       if(connected){
           view.setText(getStringFromResource(stringId,R.string.network_setup_success));
       }else{
           view.setText(getStringFromResource(stringId,R.string.network_setup_failed));
       }
   }
   private  String getStringFromResource(int ...ids){
       StringBuffer sb = new StringBuffer();
        if(ids != null){
            for(int id :ids){
                sb.append(getResources().getString(id));
            }
        }
        return sb.toString();
    }
   
   
   Runnable handleEthernetTask = new Runnable() {
    
    @Override
    public void run() {
        if(getEthernetState() == State.CONNECTED){
            handleNetworkConnectStatus(mEthernetStateView, true);
        }else if(getEthernetState() == State.DISCONNECTED){
            handleNetworkConnectStatus(mEthernetStateView, false);
        }
    }
   };
   Runnable handleWifiTask = new Runnable() {
       
       @Override
       public void run() {
           if(getWifiState() == State.CONNECTED){
               handleNetworkConnectStatus(mWifiStateView, true);
           }else if(getWifiState() == State.DISCONNECTED){
               handleNetworkConnectStatus(mWifiStateView, false);
           }
       }
      };
	private BroadcastReceiver mNetworkReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
		    if(mKeyChannel.equals(NetworkCommon.ChannelEthernet)){
		        NetworkInfo netInfo = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
	            mHandler.removeCallbacks(handleEthernetTask);
	            NetworkInfo eh = mConnectManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
	            if(eh.getState() == State.CONNECTED){
	                mHandler.post(handleEthernetTask);
	            }else{
	               mHandler.postDelayed(handleEthernetTask, 1000 * 5);
	            }
		    }else if(mKeyChannel.equals(NetworkCommon.ChannelBoth)){
		        NetworkInfo wifi = mConnectManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		        if(wifi.getState() == State.CONNECTED){
                    mHandler.postDelayed(handleWifiTask, 1000 * 2);
                }else{
                    mHandler.postDelayed(handleWifiTask, 1000 * 20);
                }
		        NetworkInfo eh = mConnectManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
                if(eh.getState() == State.CONNECTED){
                    mHandler.postDelayed(handleEthernetTask, 1000 * 2);
                }else{
                   mHandler.postDelayed(handleEthernetTask, 1000 * 5);
                }
		        NetworkInfo networkInfo = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
		        if(networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET){
		             mHandler.removeCallbacks(handleEthernetTask);
	                    if(eh.getState() == State.CONNECTED){
	                        mHandler.post(handleEthernetTask);
	                    }else{
	                       mHandler.postDelayed(handleEthernetTask, 1000 * 5);
	                    }
		        }else if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI){
		            mHandler.removeCallbacks(handleWifiTask);
		            if(wifi.getState() == State.CONNECTED){
		                mHandler.post(handleWifiTask);
		            }else{
		                mHandler.postDelayed(handleWifiTask, 1000 * 20);
		            }
		        }
		        
		        
		    }
		    
		}

	};
	private State getWifiState(){
	    NetworkInfo wifiInfo =  mConnectManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	    return wifiInfo.getState();
	}
	
	private State getEthernetState(){
	    NetworkInfo ethernetInfo =  mConnectManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
	    return ethernetInfo.getState();
	}
	public boolean isNetworkConnected() {
		NetworkInfo networkInfo = mConnectManager.getActiveNetworkInfo();
		return networkInfo != null
				&& (networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET || networkInfo
						.getType() == ConnectivityManager.TYPE_WIFI)
				&& networkInfo.isConnected();
	}

	void handleNetConnected() {

		mHandler.post(new Runnable() {
			public void run() {
				handleNetworkConnectStatus();
			}
		});
	}

	public FinishSettingsPage() {
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.network_setup_endview, container,
				false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		initializeView();

		mHandler = new Handler();

		mConnectIntentFilter = new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION);

		mConnectManager = (ConnectivityManager) mActivity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	public void onStart() {
		super.onStart();
		SharedPreferences settings = mActivity.getSharedPreferences(
                NetworkCommon.PREF_NAME_NETWORK, 0);
        mKeyChannel = settings.getString(NetworkCommon.KeyChannel,
                NetworkCommon.ChannelEthernet);
        if(mKeyChannel.equals(NetworkCommon.ChannelEthernet)){
            mWifiStateView.setVisibility(View.INVISIBLE);
        }else if(mKeyChannel.equals(NetworkCommon.ChannelBoth)){
            mWifiStateView.setVisibility(View.VISIBLE);
        }
		reqisterConnectReceiver();
	    scheduleTimeoutTask();
//
//		mHandler.postDelayed(new Runnable() {
//
//			@Override
//			public void run() {
//				checkConfigResult();
//			}
//
//		}, 2000);
	}

	public void onStop() {
		super.onStop();
		stopTimer();
		mHandler.removeCallbacks(handleEthernetTask);
		mHandler.removeCallbacks(handleWifiTask);
		unregisterConnectReceiver();
	}

	void initializeView() {
		mEthernetStateView = (TextView) mActivity.findViewById(R.id.ethernet_state_view);
		mWifiStateView = (TextView) mActivity.findViewById(R.id.wifi_state_view);
		mWifiStateView.setVisibility(View.INVISIBLE);
		mOkButton = (Button) mActivity.findViewById(R.id.okbutton);
		mPrevButton = (Button) mActivity.findViewById(R.id.prevbutton);

		mOkButton.setOnClickListener(mOnClickListener);
		mPrevButton.setOnClickListener(mOnClickListener);

		mPrevButton.requestFocus();
	}

	private void reqisterConnectReceiver() {
		mActivity.registerReceiver(mNetworkReceiver, mConnectIntentFilter);
	}

	private void unregisterConnectReceiver() {
		mActivity.unregisterReceiver(mNetworkReceiver);
	}

	void checkConfigResult() {

		mIsChecked = true;

		NetworkInfo netInfo = mConnectManager.getActiveNetworkInfo();
		Log.d(TAG, "============== checkConfigResult " + netInfo);
		Log.d(TAG,
				"============== checkConfigResult "
						+ mConnectManager
								.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET));

		if (netInfo != null) {

			Log.d(TAG, "============== checkConfigResult " + netInfo.getState()
					+ " " + netInfo.getDetailedState());

			if (netInfo.getState() == NetworkInfo.State.CONNECTED) {
				mEthernetStateView.setText(R.string.network_setup_success);
			} else {
				if (netInfo.getState() == NetworkInfo.State.CONNECTING
						|| netInfo.getState() == NetworkInfo.State.DISCONNECTING) {
				    scheduleTimeoutTask();
					return;
				}

				if (netInfo.getState() == NetworkInfo.State.DISCONNECTED
						|| netInfo.getState() == NetworkInfo.State.SUSPENDED
						|| netInfo.getState() == NetworkInfo.State.UNKNOWN) {
					mEthernetStateView.setText(R.string.network_setup_failed);
					return;
				}
			}
		} else {
			// there is no connect now, so just wait the message, and handle it
			// there.
			scheduleTimeoutTask();
		}
	}

	void scheduleTimeoutTask() {
		stopTimer();
		mTimer = new Timer();
		mTask = new TimerTask() {
			public void run() {
				mHandler.post(new TimeoutTask());
			}
		};

		mTimer.schedule(mTask, 120000);
		Log.d(TAG, "============== schedule TimeOut ");
	}

	void handleNetworkConnectStatus() {
		stopTimer();

		boolean connected = isNetworkConnected();
		if (connected) {
			mEthernetStateView.setText(R.string.network_setup_success);
		} else {
			mEthernetStateView.setText(R.string.network_setup_failed);
		}
	}

	void finishNetsettings() {
		try {
			String setflagValues = "1";
			byte[] setflag = setflagValues.getBytes();
			FileOutputStream fos = mActivity.openFileOutput(
					NetworkCommon.FlagFile, Context.MODE_WORLD_READABLE);
			fos.write(setflag);

			fos.close();
		} catch (Exception e) {
			Log.e(TAG,
					"Exception Occured: Trying to add set setflag : "
							+ e.toString());
			Log.e(TAG, "Finishing the Application");
		}
	}

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.okbutton) {
				finishNetsettings();
				mActivity.finish();
			} else if (v.getId() == R.id.prevbutton) {
				mManager.prevPage(SettingsCommon.PAGE_FINISH);
			}
		}
	};
}
