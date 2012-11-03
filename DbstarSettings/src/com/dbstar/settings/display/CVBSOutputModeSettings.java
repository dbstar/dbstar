package com.dbstar.settings.display;

import com.dbstar.settings.R;
import com.dbstar.settings.utils.DisplaySettings;
import com.dbstar.settings.utils.SettingsCommon;
import com.dbstar.settings.utils.Utils;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class CVBSOutputModeSettings extends Fragment {
	
	ListView mModeListView;
	
	private String[] mCVBSValues;
	private int mCVBSIndex;
	boolean mHasCVBSOutput;
	int mSelectedModeIndex;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.cvbs_output_settings, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mModeListView = (ListView) getActivity().findViewById(
				R.id.output_mode_view);

		mModeListView.setOnItemClickListener(mOnModeSelectedListener);
		
		mHasCVBSOutput = Utils.hasCVBSMode();
		if (mHasCVBSOutput) {
			String valCVBSmode = DisplaySettings.getCVBSOutpuMode();
			mCVBSValues = getResources().getStringArray(
					R.array.cvbsmode_entries);
			if (!valCVBSmode.equals("null")) {
				mCVBSIndex = DisplaySettings.findIndexOfEntry(valCVBSmode, mCVBSValues);
			}
		}
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
		Intent cvbsChangeIntent = new Intent(SettingsCommon.ACTION_CVBSMODE_CHANGE);
		cvbsChangeIntent.putExtra("cvbs_mode", mCVBSIndex);
		getActivity().sendBroadcast(cvbsChangeIntent);
	}
}
