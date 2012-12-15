package com.dbstar.DbstarDVB.VideoPlayer;

import com.dbstar.DbstarDVB.DbstarServiceApi;
import com.dbstar.DbstarDVB.R;
import com.dbstar.DbstarDVB.PlayerService.DivxInfo;
import com.dbstar.DbstarDVB.PlayerService.Errorno;
import com.dbstar.DbstarDVB.PlayerService.IPlayerService;
import com.dbstar.DbstarDVB.PlayerService.MediaInfo;
import com.dbstar.DbstarDVB.PlayerService.ScreenMode;
import com.dbstar.DbstarDVB.PlayerService.SettingsVP;
import com.dbstar.DbstarDVB.PlayerService.VideoInfo;
import com.dbstar.DbstarDVB.VideoPlayer.alert.DbVideoInfoDlg;
import com.dbstar.DbstarDVB.VideoPlayer.alert.GDAlertDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Toast;

import android.os.SystemProperties;

public class PlayerActivity extends Activity {

	private static final String TAG = "PlayerActivity";

	protected IPlayerService mAmplayer = null;
	protected int mPlayerStatus = VideoInfo.PLAYER_UNKNOWN;
	protected boolean INITOK = false;

	protected MediaInfo mMediaInfo = null;
	// audio track
	protected int mTotalAudioStreamNumber = 0;
	protected int mCurrentAudioStream = 0;

	protected int mTotalTime = 0;
	protected int mCurrentTime = 0;

	// used for resume. last played position when player exit.
	protected int mPlayPosition = 0;

	private static int VOLUME_LEVEL[] = { 0, 2, 4, 6, 8, 10, 11, 12, 13, 14, 15 };
	private static int VOLUME_ADJUST_STEP[] = { 2, 2, 2, 2, 2, 1, 1, 1, 1, 1 };
	protected static final int DefaultVolumeLevel = 10;
	protected AudioManager mAudioManager;
	protected boolean mIsMute = false;
	protected int mMaxVolumeLevel = -1; // default is 15 on Android.
	protected int mVolumeLevel = -1;
	protected int mVolumeStep = -1;
	protected int mVolumeLevelIndex = -1;

	protected int[] angle_table = { 0, 1, 2, 3 };

	// Surface.ROTATION_0, ROTATION_90, ROTATION_180, ROTATION_270

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		reqisterSystemMessageReceiver();
	}
	
	public void onDestroy() {
		super.onDestroy();
		
		unregisterReceiver(mSystemMessageReceiver);
	}
	
	private void reqisterSystemMessageReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(DbstarServiceApi.ACTION_HDMI_IN);
		filter.addAction(DbstarServiceApi.ACTION_HDMI_OUT);

		filter.addAction(DbstarServiceApi.ACTION_SMARTCARD_IN);
		filter.addAction(DbstarServiceApi.ACTION_SMARTCARD_OUT);

		registerReceiver(mSystemMessageReceiver, filter);
	}
	
	private static final int MSG_SMARTCARD_IN = 0x1000;
	private static final int MSG_SMARTCARD_OUT = 0x1001;
	
	protected boolean mIsSmartcardIn = false;

	private BroadcastReceiver mSystemMessageReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			Log.d(TAG, "onReceive System msg " + action);
		
			if (action.equals(DbstarServiceApi.ACTION_HDMI_IN)) {
			} else if (action.equals(DbstarServiceApi.ACTION_HDMI_OUT)) {
			} else if (action.equals(DbstarServiceApi.ACTION_SMARTCARD_IN)) {
				Log.d(TAG, "######: " + action);
				mHandler.sendEmptyMessage(MSG_SMARTCARD_IN);
			} else if (action.equals(DbstarServiceApi.ACTION_SMARTCARD_OUT)) {
				Log.d(TAG, "######: " + action);
				mHandler.sendEmptyMessage(MSG_SMARTCARD_OUT);
			}
		}
	};
	
	protected static final int DLG_MEDIAINFO_POPUP = 0;
	protected static final int DLG_SMARTCARD_POPUP = 1;
	
	protected static final int MSG_DIALOG_POPUP = 1;
	protected static final int MSG_DIALOG_TIMEOUT = 500;

	protected DbVideoInfoDlg mVideoInfoDlg = null;
	protected Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_DIALOG_POPUP:
				showDialog(DLG_MEDIAINFO_POPUP);
				break;
			case MSG_SMARTCARD_IN: {
				mIsSmartcardIn = true;
				smartcardPlugin(mIsSmartcardIn);
				showDialog(DLG_SMARTCARD_POPUP);
				break;
			}
			case MSG_SMARTCARD_OUT: {
				mIsSmartcardIn = false;
				smartcardPlugin(mIsSmartcardIn);
				showDialog(DLG_SMARTCARD_POPUP);
				break;
			}
			}
		}
	};
	
	GDAlertDialog mSmartcardDialog = null;
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch (id) {
		case DLG_MEDIAINFO_POPUP: {
			mVideoInfoDlg = new DbVideoInfoDlg(this, getIntent());
			dialog = mVideoInfoDlg;
			break;
		}
		case DLG_SMARTCARD_POPUP: {
			mSmartcardDialog = new GDAlertDialog(this, DLG_SMARTCARD_POPUP);
			mSmartcardDialog.setOnCreatedListener(mOnCreatedListener);
			dialog = mSmartcardDialog;
		}
		default:
			dialog = null;
			break;
		}

		return dialog;
	}
	
	GDAlertDialog.OnCreatedListener mOnCreatedListener = new GDAlertDialog.OnCreatedListener() {

		@Override
		public void onCreated(GDAlertDialog dialog) {
			if (dialog.getId() == DLG_SMARTCARD_POPUP) {
				dialog.setTitle(R.string.smartcard_status_title);
				if (mIsSmartcardIn) {
					dialog.setMessage(R.string.smartcard_status_in);
				} else {
					dialog.setMessage(R.string.smartcard_status_out);
				}
				dialog.showSingleButton();
				dialog.setPositiveBtnClickListener(mDRMPositiveBtnClickListener);
			}
		}

	};
	
	View.OnClickListener mDRMPositiveBtnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (mSmartcardDialog != null) {
				mSmartcardDialog.dismiss();
				exitPlayer();
			}
		}
	};
	
	protected void smartcardPlugin(boolean plugIn) {
		
	}
	
	public void initAngleTable() {
		String hwrotation = SystemProperties.get("ro.sf.hwrotation");
		if (hwrotation == null) {
			angle_table[0] = 0;
			angle_table[1] = 1;
			angle_table[2] = 2;
			angle_table[3] = 3;
			Log.e(TAG, "initAngleTable, Can not get hw rotation!");
			return;
		}

		if (hwrotation.equals("90")) {
			angle_table[0] = 1;
			angle_table[1] = 2;
			angle_table[2] = 3;
			angle_table[3] = 0;
		} else if (hwrotation.equals("180")) {
			angle_table[0] = 2;
			angle_table[1] = 3;
			angle_table[2] = 0;
			angle_table[3] = 1;
		} else if (hwrotation.equals("270")) {
			angle_table[0] = 3;
			angle_table[1] = 0;
			angle_table[2] = 1;
			angle_table[3] = 2;
		} else {
			angle_table[0] = 0;
			angle_table[1] = 1;
			angle_table[2] = 2;
			angle_table[3] = 3;
		}
	}

	protected void updateSoundVolumeView() {

	}

	int getVolumeLevelIndex(int volume) {
		int i = 0;

		for (i = 0; i < VOLUME_LEVEL.length; i++) {
			if (volume < VOLUME_LEVEL[i]) {
				break;
			}
		}

		if (i > 0) {
			i--;
		}

		return i;
	}

	void increaseVolume() {

		if (mIsMute || mVolumeLevel == mMaxVolumeLevel)
			return;

		if (mVolumeLevel > VOLUME_LEVEL[mVolumeLevelIndex]) {
			mVolumeLevel += 1;
		}
		mVolumeLevel += VOLUME_ADJUST_STEP[mVolumeLevelIndex];
		mVolumeLevelIndex++;

		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mVolumeLevel,
				0);
		updateSoundVolumeView();
	}

	void decreaseVolume() {
		if (mIsMute || mVolumeLevel == 0)
			return;

		if (mVolumeLevel > VOLUME_LEVEL[mVolumeLevelIndex]) {
			mVolumeLevel -= 1;
		}

		mVolumeLevel -= VOLUME_ADJUST_STEP[mVolumeLevelIndex - 1];
		mVolumeLevelIndex--;
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mVolumeLevel,
				0);
		updateSoundVolumeView();
	}

	protected void displayInit() {
		int mode = SettingsVP.getParaInt(SettingsVP.DISPLAY_MODE);
		switch (mode) {
		case ScreenMode.NORMAL:
			ScreenMode.setScreenMode("0");
			break;
		case ScreenMode.FULLSTRETCH:
			ScreenMode.setScreenMode("1");
			break;
		case ScreenMode.RATIO4_3:
			ScreenMode.setScreenMode("2");
			break;
		case ScreenMode.RATIO16_9:
			ScreenMode.setScreenMode("3");
			break;

		default:
			Log.e(TAG, "load display mode para error!");
			break;
		}
	}

	protected int getOSDRotation() {
		Display display = getWindowManager().getDefaultDisplay();
		int rotation = display.getRotation();
		int hw_rotation = SystemProperties.getInt("ro.sf.hwrotation", 0);
		return (rotation * 90 + hw_rotation) % 360;
	}

	protected static SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
		public void surfaceChanged(SurfaceHolder holder, int format, int w,
				int h) {
			Log.d(TAG, "surfaceChanged");
		}

		public void surfaceCreated(SurfaceHolder holder) {
			Log.d(TAG, "surfaceCreated");
			initSurface(holder);
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			Log.d(TAG, "surfaceDestroyed");
		}

		private void initSurface(SurfaceHolder h) {
			Canvas c = null;
			try {
				Log.d(TAG, "initSurface");
				c = h.lockCanvas();
			} finally {
				if (c != null)
					h.unlockCanvasAndPost(c);
			}
		}
	};

	public void startPlayerService() {
		Intent intent = new Intent();
		ComponentName component = new ComponentName("com.dbstar.DbstarDVB",
				"com.dbstar.DbstarDVB.PlayerService.AmPlayer");
		intent.setComponent(component);
		startService(intent);
		bindService(intent, mPlayerConnection, BIND_AUTO_CREATE);
	}

	public void stopPlayerService() {
		unbindService(mPlayerConnection);
		Intent intent = new Intent();
		ComponentName component = new ComponentName("com.dbstar.DbstarDVB",
				"com.dbstar.DbstarDVB.PlayerService.AmPlayer");
		intent.setComponent(component);
		stopService(intent);
		mAmplayer = null;
	}

	ServiceConnection mPlayerConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName name, IBinder service) {
			mAmplayer = IPlayerService.Stub.asInterface(service);

			try {
				mAmplayer.Init();
			} catch (RemoteException e) {
				e.printStackTrace();
				Log.d(TAG, "init fail!");
			}

			try {
				mAmplayer.RegisterClientMessager(mPlayerMsg.getBinder());
			} catch (RemoteException e) {
				e.printStackTrace();
				Log.e(TAG, "register to player server fail!");
			}

			// auto play
			try {
				final short color = ((0x8 >> 3) << 11) | ((0x30 >> 2) << 5)
						| ((0x8 >> 3) << 0);
				mAmplayer.SetColorKey(color);
				Log.d(TAG, "set colorkey() color=" + color);
			} catch (RemoteException e) {
				e.printStackTrace();
			}

			Amplayer_play(mPlayPosition);
		}

		public void onServiceDisconnected(ComponentName name) {
			try {
				mAmplayer.Stop();
			} catch (RemoteException e) {
				e.printStackTrace();
			}

			try {
				mAmplayer.Close();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			mAmplayer = null;
		}
	};

	protected void switchAudioStreamToNext() {
		if (mMediaInfo != null && mMediaInfo.getAudioTrackCount() > 1) {
			int nextAudioStream = (mCurrentAudioStream + 1)
					% mTotalAudioStreamNumber;
			try {
				mAmplayer.SwitchAID(AudioTrackOperation.AudioStreamInfo
						.get(nextAudioStream).audio_id);
				Log.d(TAG, " ============ change audio stream to: "
						+ nextAudioStream);
				mCurrentAudioStream = nextAudioStream;
			} catch (RemoteException e) {
				e.printStackTrace();
			}

			try {
				mAmplayer.GetMediaInfo();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	protected void updatePlaybackTimeInfo(int currentTime, int totalTime) {

	}

	protected void updatePlaybackSubtitle(int currentTime) {

	}

	protected void Amplayer_play(int startPosition) {

		// stop music player
		Intent intent = new Intent();
		intent.setAction("com.android.music.musicservicecommand.pause");
		intent.putExtra("command", "stop");
		sendBroadcast(intent);
	}

	protected void playbackInited() {
		INITOK = true;

		try {
			mMediaInfo = mAmplayer.GetMediaInfo();

			// Init audio track info
			AudioTrackOperation.setAudioStream(mMediaInfo);

		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	protected void playbackStart() {

	}

	protected void playbackPause() {

	}

	protected void playbackStopped() {

	}

	protected void playbackComplete() {

	}

	public void playbackError(int error) {

	}

	public void playbackExit() {

	}

	public void exitPlayer() {

	}
	
	public void searchOk() {
		
	}

	// =========================================================
	private Messenger mPlayerMsg = new Messenger(new Handler() {

		public void handleMessage(Message msg) {
			Log.d(TAG, " =========== Player msg = " + msg.what);

			switch (msg.what) {
			case VideoInfo.TIME_INFO_MSG:

				mCurrentTime = msg.arg1 / 1000;
				mTotalTime = msg.arg2;

				updatePlaybackTimeInfo(mCurrentTime, mTotalTime);

				// for subtitle tick;
				if (mPlayerStatus == VideoInfo.PLAYER_RUNNING) {
					updatePlaybackSubtitle(msg.arg1);
				}

				break;

			case VideoInfo.STATUS_CHANGED_INFO_MSG:
				Log.d(TAG, " ==================== Player status = " + msg.arg1);
				mPlayerStatus = msg.arg1;

				switch (mPlayerStatus) {
				case VideoInfo.PLAYER_INITOK:
					playbackInited();
					break;
				case VideoInfo.PLAYER_RUNNING:
					playbackStart();
					break;
				case VideoInfo.PLAYER_PAUSE:
				case VideoInfo.PLAYER_SEARCHING:
					playbackPause();
					break;

				case VideoInfo.PLAYER_PLAYEND:
					playbackComplete();
					break;

				case VideoInfo.PLAYER_STOPED:
					playbackStopped();
					break;

				case VideoInfo.PLAYER_EXIT:
					Log.d(TAG, "VideoInfo.PLAYER_EXIT");
					playbackExit();
					break;

				case VideoInfo.PLAYER_ERROR:
					playbackError(msg.arg2);
					break;
				case VideoInfo.PLAYER_SEARCHOK:
					break;

				case VideoInfo.DIVX_AUTHOR_ERR: {
					Log.d(TAG, "Authorize Error");
					DivxInfo divxInfo = null;
					try {
						divxInfo = mAmplayer.GetDivxInfo();
					} catch (RemoteException e) {
						e.printStackTrace();
					}

					if (divxInfo != null) {
						alertDivxAuthorError(divxInfo, msg.arg2);
					}
					break;
				}
				case VideoInfo.DIVX_EXPIRED: {
					Log.d(TAG, "Authorize Expired");
					DivxInfo divxInfo = null;
					try {
						divxInfo = mAmplayer.GetDivxInfo();
					} catch (RemoteException e) {
						e.printStackTrace();
					}

					if (divxInfo != null) {
						alertDivxExpired(divxInfo, msg.arg2);
					}
					break;
				}
				case VideoInfo.DIVX_RENTAL: {
					Log.d(TAG, "Authorize rental");
					DivxInfo divxInfo = null;
					try {
						divxInfo = mAmplayer.GetDivxInfo();
					} catch (RemoteException e) {
						e.printStackTrace();
					}

					if (divxInfo != null) {
						alertDivxRental(divxInfo, msg.arg2);
					}

					break;
				}
				default:
					break;
				}
				break;

			case VideoInfo.AUDIO_CHANGED_INFO_MSG:
				mTotalAudioStreamNumber = msg.arg1;
				mCurrentAudioStream = msg.arg2;
				break;
			case VideoInfo.HAS_ERROR_MSG:
				String errStr = Errorno.getErrorInfo(msg.arg2);
				Toast tp = Toast.makeText(PlayerActivity.this, errStr,
						Toast.LENGTH_SHORT);
				tp.show();
				break;
			default:
				super.handleMessage(msg);
				break;
			}
		}
	});

	// --------------------- Divx alert handler ----------------------------
	void alertDivxExpired(DivxInfo divxInfo, int args) {
		String s = "This rental has " + args
				+ " views left\nDo you want to use one of your " + args
				+ " views now";
		new AlertDialog.Builder(PlayerActivity.this)
				.setTitle("View DivX(R) VOD Rental").setMessage(s)
				.setPositiveButton(R.string.str_ok, mAlertButtonClickListener)
				.show();
	}

	void alertDivxAuthorError(DivxInfo divxInfo, int args) {
		new AlertDialog.Builder(this)
				.setTitle("Authorization Error")
				.setMessage(
						"This player is not authorized to play this DivX protected video")
				.setPositiveButton(R.string.str_ok, mAlertButtonClickListener)
				.show();
	}

	void alertDivxRental(DivxInfo divxInfo, int args) {
		String s = "This rental has " + args
				+ " views left\nDo you want to use one of your " + args
				+ " views now?";
		new AlertDialog.Builder(PlayerActivity.this)
				.setTitle("View DivX(R) VOD Rental")
				.setMessage(s)
				.setPositiveButton(R.string.str_ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// finish();
								try {
									mAmplayer.Play();
								} catch (RemoteException e) {
									e.printStackTrace();
								}
							}
						})
				.setNegativeButton(R.string.str_cancel,
						mAlertButtonClickListener).show();
	}

	DialogInterface.OnClickListener mAlertButtonClickListener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			exitPlayer();
		}
	};
}
