package com.settings.components;

import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;

import com.settings.adapter.ListAdapter;
import com.settings.bean.OutputMode;
import com.settings.ottsettings.R;
import com.settings.utils.SoundSettings;

public class AudioSettingsViewWrapper {
	private static final String TAG = "AudioSettingsViewWrapper";
	
	private Context context;
	private ListView mAudioOutputView;
	private ListAdapter mAudioOutputModeAdapter;
	private String[] mAudioModeEntries, mAudioModeEntriesStr, mAudioModeValues;
	ArrayList<OutputMode> mAudioModes = new ArrayList<OutputMode>();
	
	boolean mHasCVBSOutput;
	
	boolean mHasDefaultFrequency;
	String mDefaultFrequency;
	private CharSequence[] mDefaultFrequencyEntries;
	
	OutputMode mDefaultVideoMode;
	
	int mLastSelectedItem = -1;
	int mSelectedItem = -1;
	View mLastSelectedView = null, mSelectedView = null;
	
	int mAudioSelectedMode = -1;
	
	int mAudioLastSelectedItem = -1;
	int mAudioSelectedItem = -1;
	View mAudioLastSelectedView = null, mAudioSelectedView = null;
	
	boolean mIsSettingOutputMode = false;
	Handler mHandler = null;
	
	public AudioSettingsViewWrapper(Context context) {
		this.context = context;
	}
	
	public void initView(View view) {
		initializeView(view);
		
		String audioMode = SoundSettings.getAudioOutputMode();
		if (!audioMode.isEmpty()) {

			for (int i = 0; i < mAudioModes.size(); i++) {
				OutputMode aMode = mAudioModes.get(i);
				if (aMode.modeValue.equalsIgnoreCase(audioMode)) {
					aMode.isSelected = true;
					mAudioSelectedMode = i;
				} else {
					aMode.isSelected = false;
				}
			}
		} else {
			mAudioSelectedMode = 0;
			setAudioModeSelected(0, true);
		}

		mAudioOutputModeAdapter.notifyDataSetChanged();

		mAudioOutputView.requestFocus();
		
		onStart();
	}

	private void onStart() {

		View v = mAudioOutputView.getSelectedView();
		Log.d(TAG, "1 ================ v " + v);
		showSelectedItem(v, false);

	}

	public void initializeView(View view) {

		mAudioModeEntriesStr = context.getResources().getStringArray(R.array.digit_audio_output_entries_str);

		mAudioModeEntries = context.getResources().getStringArray(R.array.digit_audio_output_entries);

		mAudioModeValues = context.getResources().getStringArray(R.array.digit_audio_output_entries_values);

		mDefaultFrequencyEntries = context.getResources().getStringArray(R.array.default_frequency_entries);

		for (int i = 0; i < mAudioModeEntries.length; i++) {
			OutputMode aMode = new OutputMode();
			aMode.modeStr = mAudioModeEntriesStr[i];
			aMode.modeValue = mAudioModeEntries[i];
			mAudioModes.add(aMode);

			Log.d(TAG, "audio mode " + aMode.modeStr + " " + aMode.modeValue);
		}

		mAudioOutputView = (ListView) view.findViewById(R.id.audio_outputmode_list);

		mAudioOutputModeAdapter = new ListAdapter(context);

		mAudioOutputModeAdapter.setDataSet(mAudioModes);

		mAudioOutputView.setAdapter(mAudioOutputModeAdapter);

		mAudioOutputView.setOnItemClickListener(mOnAudioModeSelectedListener);

		mAudioOutputView.setOnItemSelectedListener(mAudioItemSelectedListener);

		mAudioOutputView.setOnFocusChangeListener(mFocusChangeListener);

		mAudioOutputView.setOnKeyListener(mAudioKeyListener);
	}

	OnItemClickListener mOnAudioModeSelectedListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			if (mAudioSelectedMode >= 0) {
				setAudioModeSelected(mAudioSelectedMode, false);
			}

			mAudioSelectedMode = position;
			setAudioModeSelected(mAudioSelectedMode, true);

			mAudioOutputModeAdapter.notifyDataSetChanged();
		}

	};

	OnItemSelectedListener mAudioItemSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

			if (!mAudioOutputView.hasFocus())
				return;

			mAudioLastSelectedView = mAudioSelectedView;
			mAudioSelectedView = view;

			showSelectedItem(mAudioLastSelectedView, false);
			showSelectedItem(mAudioSelectedView, true);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}

	};


	View.OnFocusChangeListener mFocusChangeListener = new View.OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {

//			if (v.getId() == R.id.video_outputmode_list) {
//				showSelectedItem(mSelectedView, hasFocus);
//			} else
				if (v.getId() == R.id.audio_outputmode_list) {

				if (mAudioSelectedView == null) {
					mAudioSelectedView = mAudioOutputView.getChildAt(0);
				}
				showSelectedItem(mAudioSelectedView, hasFocus);

			}
		}
	};

	void showSelectedItem(View v, boolean show) {

		if (v != null) {
			ListAdapter.ViewHolder holder = (ListAdapter.ViewHolder) v.getTag();
			holder.modeViewHighlight.setVisibility(show ? View.VISIBLE : View.GONE);
			holder.modeView.setVisibility(show ? View.GONE : View.VISIBLE);

			Log.d(TAG, "view " + holder.modeView.getText() + " show=" + show);
		}
	}


	private void setAudioModeSelected(int position, boolean selected) {
		OutputMode mode = mAudioModes.get(position);
		mode.isSelected = selected;

		if (selected) {
			String value = mAudioModeValues[position];
			SoundSettings.setAudioOutputMode(mode.modeValue, value);
		}
	}

	private View.OnKeyListener mAudioKeyListener = new View.OnKeyListener() {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
					return handleUpKey(mAudioOutputView);
				} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
					return handleDownKey(mAudioOutputView);
				}
			}

			return false;
		}
	};

	boolean handleUpKey(ListView listView) {
		if (listView.getSelectedItemPosition() == 0) {
			listView.setSelection(listView.getCount() - 1);
			return true;
		}

		return false;
	}

	boolean handleDownKey(ListView listView) {
		if (listView.getSelectedItemPosition() == listView.getCount() - 1) {
			listView.setSelection(0);
			return true;
		}

		return false;
	}
}
