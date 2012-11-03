package com.dbstar.settings.display;

import com.dbstar.settings.R;
import com.dbstar.settings.utils.DisplaySettings;
import com.dbstar.settings.utils.SettingsCommon;
import com.dbstar.settings.utils.Utils;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class TVOutputModeSettings extends Fragment {

	private static final String TAG = "TVOutputModeSettings";
	
	ListView mModeListView;
	private String[] mEntryValues;
	private int mSelectedModeIndex;
	private int mEntryIndex;

	private String[] mCVBSValues;
	private int mCVBSIndex;
	boolean mHasCVBSOutput;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.tv_output_settings, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mHasCVBSOutput = Utils.hasCVBSMode();
		if (mHasCVBSOutput) {
			String valCVBSmode = DisplaySettings.getCVBSOutpuMode();
			mCVBSValues = getResources().getStringArray(
					R.array.cvbsmode_entries);
			if (!valCVBSmode.equals("null")) {
				mCVBSIndex = DisplaySettings.findIndexOfEntry(valCVBSmode,
						mCVBSValues);
			}
		}

		mModeListView = (ListView) getActivity().findViewById(
				R.id.output_mode_view);

		mModeListView.setOnItemClickListener(mOnModeSelectedListener);

		String valOutputmode = DisplaySettings.getOutpuMode();
		
		Log.d(TAG, "valOutputmode =" + valOutputmode);
		
		mEntryValues = getResources()
				.getStringArray(R.array.outputmode_entries);
		mEntryIndex = DisplaySettings.findIndexOfEntry(valOutputmode,
				mEntryValues);

		mModeListView.setSelection(mEntryIndex);
	}

	OnItemClickListener mOnModeSelectedListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			onModeChanged(position);
		}

	};

	private void onModeChanged(int modeIndex) {
		mSelectedModeIndex = modeIndex;
		Intent intent = new Intent(getActivity(), OutputSetConfirm.class);
		intent.putExtra("set_mode", mEntryValues[mSelectedModeIndex]);
		if (mHasCVBSOutput) {
			intent.putExtra("cvbs_mode", mCVBSIndex);
		}
		startActivityForResult(intent, SettingsCommon.GET_USER_OPERATION);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case (SettingsCommon.GET_USER_OPERATION):
			if (resultCode == Activity.RESULT_OK) {
				String tvOutputMode = mEntryValues[mSelectedModeIndex];
				Intent saveIntent = new Intent(
						SettingsCommon.ACTION_OUTPUTMODE_SAVE);
				saveIntent.putExtra(SettingsCommon.OUTPUT_MODE, tvOutputMode);
				getActivity().sendBroadcast(saveIntent);

				mEntryIndex = mSelectedModeIndex;
			} else if (resultCode == Activity.RESULT_CANCELED) {
				mModeListView.setSelection(mEntryIndex);
			}

		}
	}
}
