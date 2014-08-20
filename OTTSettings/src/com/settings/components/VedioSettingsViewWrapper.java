package com.settings.components;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.settings.display.OutputSetConfirm;
import com.settings.ottsettings.R;
import com.settings.utils.DisplaySettings;
import com.settings.utils.SettingsCommon;
import com.settings.utils.Utils;

public class VedioSettingsViewWrapper {
	private static final String TAG = "VedioSettingsViewWrapper";
	private Context context;
//	private RadioGroup radioGroup;
	ArrayList<OutputMode> mVideoModes = new ArrayList<OutputMode>();
	
	private ListView mVideoOutputView;
	private ListAdapter mVideoOutputModeAdapter;
	private String[] mVideoModeEntries;

	private String[] mCVBSValues;
	private int mCVBSIndex;
	boolean mHasCVBSOutput;

	private static final int DefaultFrequecy50Hz = 0;
	private static final int DefaultFrequecy60Hz = 1;
	boolean mHasDefaultFrequency;
	String mDefaultFrequency;
	private CharSequence[] mDefaultFrequencyEntries;

	OutputMode mDefaultVideoMode;

	int mVideoSelectedMode = -1;
	int mVideoLastSelectedMode = -1;

	int mLastSelectedItem = -1;
	int mSelectedItem = -1;
	View mLastSelectedView = null, mSelectedView = null;

	boolean mIsSettingOutputMode = false;
	Handler mHandler = null;

	public VedioSettingsViewWrapper(Context context) {
		this.context = context;
	}
	
	public void initView(View view) {
//		radioGroup = (RadioGroup) view.findViewById(R.id.vedio_settings_HDMI_rg_container);

		mHandler = new Handler();
		mVideoOutputView = (ListView) view.findViewById(R.id.video_outputmode_list);
		initializeView();
		// Video output mode
				mHasCVBSOutput = Utils.hasCVBSMode();
				if (mHasCVBSOutput) {
					String valCVBSmode = DisplaySettings.getCVBSOutpuMode();
					mCVBSValues = context.getResources().getStringArray(R.array.cvbsmode_entries);
					if (!valCVBSmode.equals("null")) {
						mCVBSIndex = DisplaySettings.findIndexOfEntry(valCVBSmode, mCVBSValues);
					}
				}

				// get default frequency
				mHasDefaultFrequency = Utils.platformHasDefaultTVFreq();
				Log.d(TAG, "has default frequency = " + mHasDefaultFrequency);
				if (mHasDefaultFrequency) {
					mDefaultFrequency = DisplaySettings.getDefaultFrequency();
				}
				
				if (mDefaultFrequency == null || mDefaultFrequency.isEmpty()) {
					mDefaultFrequency = mDefaultFrequencyEntries[DefaultFrequecy60Hz].toString();
				}

				Log.d(TAG, "default fq " + mDefaultFrequency);

				// get default output mode
				String videoMode = DisplaySettings.getOutpuMode();

				Log.d(TAG, "default mode " + videoMode);

				mVideoSelectedMode = -1;

				if (videoMode.isEmpty()) {
					OutputMode mode = mVideoModes.get(mVideoModes.size() - 1);
					mode.isSelected = true; // default is 720p
					mVideoSelectedMode = mVideoModes.size() - 1;
					Log.d(TAG, "default output mode = " + mode.modeStr + " value " + mode.modeValue);
				} else {
					for (int i = 1; i < mVideoModes.size(); i++) {
						OutputMode mode = mVideoModes.get(i);
						boolean selected = false;
						if (mode.modeValue.equals(videoMode)) {
							if (mode.frequecy.equalsIgnoreCase(mDefaultFrequency)) {
								selected = true;
							}
						}

						mode.isSelected = selected;
						if (selected) {
							mVideoSelectedMode = i;
						}
					}

					if (mVideoSelectedMode < 0) {
						mVideoSelectedMode = 0;
						OutputMode mode = mVideoModes.get(0);
						mode.isSelected = true;
					}
				}

				mVideoOutputModeAdapter.notifyDataSetChanged();
				
				mVideoOutputView.requestFocus();
	}
	
	public void initializeView() {

		mVideoModeEntries = context.getResources().getStringArray(R.array.outputmode_entries);

		mDefaultFrequencyEntries = context.getResources().getStringArray(R.array.default_frequency_entries);

		OutputMode mode = new OutputMode();
		mode.modeStr = mVideoModeEntries[0];
		mode.modeValue = "auto";
		mVideoModes.add(mode);

		mode = new OutputMode();
		mode.modeStr = mVideoModeEntries[1];
		mode.modeValue = "1080p";
		mode.frequecy = mDefaultFrequencyEntries[DefaultFrequecy50Hz].toString();
		mVideoModes.add(mode);

		mode = new OutputMode();
		mode.modeStr = mVideoModeEntries[2];
		mode.modeValue = "1080p";
		mode.frequecy = mDefaultFrequencyEntries[DefaultFrequecy60Hz].toString();
		mVideoModes.add(mode);

		mode = new OutputMode();
		mode.modeStr = mVideoModeEntries[3];
		mode.modeValue = "1080i";
		mode.frequecy = mDefaultFrequencyEntries[DefaultFrequecy50Hz].toString();
		mVideoModes.add(mode);

		mode = new OutputMode();
		mode.modeStr = mVideoModeEntries[4];
		mode.modeValue = "720p";
		mode.frequecy = mDefaultFrequencyEntries[DefaultFrequecy60Hz].toString();
		mVideoModes.add(mode);

		for (int i = 0; i < mVideoModes.size(); i++) {
			OutputMode vMode = mVideoModes.get(i);
			Log.d(TAG, "video mode " + vMode.modeStr + " " + vMode.modeValue + " f " + vMode.frequecy);
		}

		mVideoOutputModeAdapter = new ListAdapter(context);

		mVideoOutputModeAdapter.setDataSet(mVideoModes);

		mVideoOutputView.setAdapter(mVideoOutputModeAdapter);

		mVideoOutputView.setOnItemClickListener(mOnVideoModeClickedListener);

		mVideoOutputView.setOnItemSelectedListener(mVideoItemSelectedListener);

		mVideoOutputView.setOnFocusChangeListener(mFocusChangeListener);

		mVideoOutputView.setOnKeyListener(mVideoKeyListener);
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (mVideoModes == null || mVideoModes.isEmpty()) {
			return;
		}
		
		switch (requestCode) {
		case (SettingsCommon.GET_USER_OPERATION):
			if (resultCode == Activity.RESULT_OK) {
				OutputMode mode = mVideoModes.get(mVideoSelectedMode);
				Intent saveIntent = new Intent(SettingsCommon.ACTION_OUTPUTMODE_SAVE);
				saveIntent.putExtra(SettingsCommon.OUTPUT_MODE, mode.modeValue);
				context.sendBroadcast(saveIntent);

			} else if (resultCode == Activity.RESULT_CANCELED) {
				if (mVideoLastSelectedMode >= 0) {
					setVideoModeSelected(mVideoLastSelectedMode, true, true);
				}

				setVideoModeSelected(mVideoSelectedMode, false, false);

				// set the selected mode
				mVideoSelectedMode = mVideoLastSelectedMode;

				mVideoOutputModeAdapter.notifyDataSetChanged();
			}
		
			// block user operation for 3 seconds.
			mIsSettingOutputMode = true;
			mHandler.postDelayed(new Runnable() {
				public void run() {
					mIsSettingOutputMode = false;
					Log.d(TAG, "change mode finished");
				}
			}, 2000);

			break;
		}
	}
	
	OnItemSelectedListener mVideoItemSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

			mLastSelectedView = mSelectedView;
			mSelectedView = view;

			showSelectedItem(mLastSelectedView, false);
			showSelectedItem(mSelectedView, true);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}

	};

	View.OnFocusChangeListener mFocusChangeListener = new View.OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {

			if (v.getId() == R.id.video_outputmode_list) {
				showSelectedItem(mSelectedView, hasFocus);
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
	
	OnItemClickListener mOnVideoModeClickedListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			onModeChanged(position);
		}

	};

	private void onModeChanged(int modeIndex) {
		if (mVideoSelectedMode == modeIndex || mIsSettingOutputMode)
			return;

		mVideoLastSelectedMode = mVideoSelectedMode;
		mVideoSelectedMode = modeIndex;

		if (mVideoLastSelectedMode >= 0) {
			setVideoModeSelected(mVideoLastSelectedMode, false, false);
		}

		if (modeIndex > 0) {
			setVideoModeSelected(modeIndex, true, true);

			OutputMode mode = mVideoModes.get(modeIndex);

			Intent intent = new Intent(context, OutputSetConfirm.class);
			intent.putExtra(SettingsCommon.KeySetMode, mode.modeValue);
			if (mHasCVBSOutput) {
				intent.putExtra(SettingsCommon.KeyCVBSMode, mCVBSIndex);
			}
			((Activity)context).startActivityForResult(intent, SettingsCommon.GET_USER_OPERATION);
//			context.startActivityForResult(intent, SettingsCommon.GET_USER_OPERATION);
		} else {
			// "auto" mode
			setVideoModeSelected(modeIndex, true, false);
			
			OutputMode mode = mVideoModes.get(mVideoModes.size() - 1);
			Intent intent = new Intent(context, OutputSetConfirm.class);
			intent.putExtra(SettingsCommon.KeySetMode, mode.modeValue);
			if (mHasCVBSOutput) {
				intent.putExtra(SettingsCommon.KeyCVBSMode, mCVBSIndex);
			}
			context.startActivity(intent);
			((Activity)context).startActivityForResult(intent, SettingsCommon.GET_USER_OPERATION);
//			startActivityForResult(intent, SettingsCommon.GET_USER_OPERATION);
		}

		mVideoOutputModeAdapter.notifyDataSetChanged();
	}
	
	private View.OnKeyListener mVideoKeyListener = new View.OnKeyListener() {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {

			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
					return handleUpKey(mVideoOutputView);
				} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
					return handleDownKey(mVideoOutputView);
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
	
	private void setVideoModeSelected(int position, boolean selected, boolean setFrequency) {

		Log.d(TAG, "set " + position + " " + selected + " " + setFrequency);

		OutputMode mode = mVideoModes.get(position);
		mode.isSelected = selected;

		if (setFrequency) {
			DisplaySettings.setDefaultFrequency(mode.frequecy);
		}
	}
}
