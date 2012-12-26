package com.dbstar.settings.network;

import java.io.FileOutputStream;

import android.content.Context;
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
	
	TextView mStateView;
	Button mOkButton, mPrevButton;
	
	private Handler mHander;

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
		
		mHander = new Handler();
	}
	
	public void onStart() {
		super.onStart();
		
		mHander.postDelayed(new Runnable() {

			@Override
			public void run() {
				checkConfigResult();
			}
			
		}, 2000);
	}

	void initializeView() {
		mStateView = (TextView) mActivity.findViewById(R.id.state_view);

		mOkButton = (Button) mActivity.findViewById(R.id.okbutton);
		mPrevButton = (Button) mActivity.findViewById(R.id.prevbutton);

		mOkButton.setOnClickListener(mOnClickListener);
		mPrevButton.setOnClickListener(mOnClickListener);

		mPrevButton.requestFocus();
	}

	void checkConfigResult() {
		
	}
	
	void finishNetsettings() {
		try {
			String setflagValues = "1";
			byte[] setflag = setflagValues.getBytes();
			FileOutputStream fos = mActivity.openFileOutput(NetworkCommon.FlagFile,
					Context.MODE_WORLD_READABLE);
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
