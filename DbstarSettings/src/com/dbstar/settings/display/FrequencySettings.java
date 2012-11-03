package com.dbstar.settings.display;

import com.dbstar.settings.R;
import com.dbstar.settings.utils.DisplaySettings;
import com.dbstar.settings.utils.SettingsCommon;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class FrequencySettings extends Fragment {

	ListView mFrequencyListView;
	private String[] mDefaultFrequencyEntries;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.frequency_settings, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mFrequencyListView = (ListView) getActivity().findViewById(
				R.id.frequency_list_view);

		mFrequencyListView.setOnItemClickListener(mOnModeSelectedListener);

		String valDefaultFrequency = DisplaySettings.getDefaultFrequency();
		mDefaultFrequencyEntries = getResources().getStringArray(
				R.array.default_frequency_entries);
		if (valDefaultFrequency.equals("")) {
			valDefaultFrequency = getResources().getString(
					R.string.tv_default_frequency_summary);
		}
		int index_DF = DisplaySettings.findIndexOfEntry(valDefaultFrequency,
				mDefaultFrequencyEntries);

		mFrequencyListView.setSelection(index_DF);
	}

	OnItemClickListener mOnModeSelectedListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			onModeChanged(position);
		}

	};

	private void onModeChanged(int modeIndex) {
		DisplaySettings
				.setDefaultFrequency(mDefaultFrequencyEntries[modeIndex]);
	}
}
