package com.dbstar.app;

import java.io.InputStream;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageView;

import com.dbstar.model.PreviewData;
import com.dbstar.service.ClientObserver;
import com.dbstar.service.GDAudioController;
import com.dbstar.service.GDDataProviderService;
import com.dbstar.util.LogUtil;
import com.dbstar.widget.GDVideoView;

public class GDMediaScheduler implements ClientObserver, OnCompletionListener,
		OnErrorListener, OnPreparedListener, SurfaceHolder.Callback {

	private static final String TAG = "GDMediaScheduler";

	private static final int RNONE = 0;
	private static final int RVideo = 1;
	private static final int RImage = 2;

	public static final int PLAYMEDIA_INTERVAL = 1000; // 1s
	public static final int PLAYIMAGE_INTERVAL = 10000; // 10s
	public static final int PLAYMEDIA_INTERVAL_WITHERROR = 60000;

	public static final int PLAYER_STATE_NONE = -2;
	public static final int PLAYER_STATE_IDLE = 0;
	public static final int PLAYER_STATE_PREPARED = 1;
	public static final int PLAYER_STATE_COMPLETED = 2;
	public static final int PLAYER_STATE_ERROR = -1;

	Context mContext;
	GDDataProviderService mService = null;

	GDVideoView mVideoView;
	ImageView mPosterView;
	SurfaceHolder mHolder;
	Bitmap mImage = null;
	Bitmap mDefaultPoster;
	int mPlayerSate;
	boolean mResourcesReady;
	boolean mUIReady;

	PlayState mCurrentState = new PlayState();
	PlayState mStoreState = new PlayState();

	PreviewData[] mResources;
	int mResourceIndex = -1;

	Handler mHandler = new Handler();
	int mCount = 0;
	
	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
//			LogUtil.d(TAG, "-----------------------------isReady() = " + isReady());
			if (isReady()) {
//				LogUtil.d(TAG, "-----------------------------playMedia");
				if (mCount < mResources.length * 3) {
					playMedia();
					mCount++;					
				} else {
					mPosterView.setVisibility(View.VISIBLE);
					mPosterView.setImageBitmap(mDefaultPoster);
				}
			} else {
				mHandler.postDelayed(mUpdateTimeTask, 2000);
			}
		}
	};

	private Runnable mSimulateHomeKeyTask = new Runnable() {
		public void run() {
			startSimulateHomeKey();			
		}

	};

	private boolean mSimulateHomeKey = false;
	private boolean mHomeKeyIsSent = false;
	public boolean isHomeKeySent() {
		return mHomeKeyIsSent;
	}
	public void resetHomeKeyFlag() {
		mHomeKeyIsSent = false;
	}

	public void simulateHomeKey() {
		// Home key will be sent when player is prepared!
		// so just set a flag here.
		mSimulateHomeKey = true;
	}

	void startSimulateHomeKey() {
		if (mService != null) {
			mService.sendSimulateKey(KeyEvent.KEYCODE_HOME);
			mHomeKeyIsSent = true;
		}
	}

	public void cancelSimulateHomeKey() {
		mSimulateHomeKey = false;
		mHandler.removeCallbacks(mSimulateHomeKeyTask);
	}

	private boolean mIsMuteSilently = false;
	
	// Mute the audio before sending home key,
	// Unmute audio after the player is started for the second time.
	// does not show the mute indicator.
	public void setMuteWithSilence() {
		mIsMuteSilently = true;
		Intent intent = new Intent(GDAudioController.ActionMute);
        intent.putExtra("key_mute", true);
        mContext.sendBroadcast(intent);
	}

	public void unmuteWithSilence() {
		if(mIsMuteSilently){
			mIsMuteSilently = false;
			Intent intent = new Intent(GDAudioController.ActionMute);
        	intent.putExtra("key_mute", false);
        	mContext.sendBroadcast(intent);
		}
	}
	
	// when video start playback, we will start a timeout task, 
	// which will run after mDurationToPlay.
	// if a video not end after this time, we will stop it and play next.
	int mPlayingVideoIndex = -1;
	// this time is from start to end of a playback.
	// if a video is played from resume state, this time is: duration - start position.
	long mDurationToPlay = 0; 
	String mPlayingVideoUrl = null;

	Runnable mCheckVideoFinishedTask = new Runnable() {
		public void run() {
			if (mCurrentState.index == mPlayingVideoIndex
					&& mCurrentState.Url.equals(mPlayingVideoUrl)) {
				LogUtil.d(TAG, "stop this video and play next");
				stop();
				
				mPlayingVideoIndex = -1;
				mPlayingVideoUrl = null;
				mDurationToPlay = 0;
				
				//play next one
				mHandler.postDelayed(mUpdateTimeTask, 2000);
			}
		}
	};
	
	// After timeoutMills, check whether the video is completed.
	void startCheckVideoCompleteTask(long timeoutMills) {
		mHandler.postDelayed(mUpdateTimeTask, timeoutMills);
	}
	
	void stopCheckVideoCompleteTask() {
		mHandler.removeCallbacks(mCheckVideoFinishedTask);
		mPlayingVideoIndex = -1;
		mDurationToPlay = 0;
		mPlayingVideoUrl = null;
	}

	public GDMediaScheduler(Context context, GDVideoView videoView, ImageView posterView) {
		mVideoView = videoView;
		mPosterView = posterView;
		mContext = context;

		loadResources(context);
		mPosterView.setImageBitmap(mDefaultPoster);

		mHolder = mVideoView.getHolder();
		mHolder.addCallback(this);

		mVideoView.setOnCompletionListener(this);
		mVideoView.setOnPreparedListener(this);
		mVideoView.setOnErrorListener(this);

		mResourceIndex = -1;
		mResources = null;
		mResourcesReady = false;
		mUIReady = false;
	}

	public void start(GDDataProviderService service) {
		LogUtil.d(TAG, "start");

		mService = service;

		mResourcesReady = false;
		mResources = null;
		mResourceIndex = -1;

		mService.getPreviews(this);
	}

	public void resume() {
		LogUtil.d(TAG, "resume");

		mPosterView.setVisibility(View.VISIBLE);
		mPosterView.setImageBitmap(mDefaultPoster);
		
//		mHandler.postDelayed(mUpdateTimeTask, 2000);
		mHandler.postDelayed(mUpdateTimeTask, 5 * 1000);
		mCount = 0;
	}

	public void pause() {
		LogUtil.d(TAG, "pause");
		stopCheckVideoCompleteTask();

		mHandler.removeCallbacks(mUpdateTimeTask);
		mVideoView.setVideoURI(null);
		saveMediaState();
	}
	
	public void stop() {
		LogUtil.d(TAG, "stopMediaPlay");
		stopCheckVideoCompleteTask();

		mHandler.removeCallbacks(mUpdateTimeTask);

		if (mVideoView.isPlaying()) {
			mVideoView.stopPlayback();
		}
		
	}

	@Override
	public void notifyEvent(int type, Object event) {

	}

	@Override
	public void updateData(int type, int param1, int param2, Object data) {
	}

	@Override
	public void updateData(int type, Object key, Object data) {
		if (type == GDDataProviderService.REQUESTTYPE_GETPREVIEWS) {
			if (data != null) {
				mResources = (PreviewData[]) data;

				LogUtil.d(TAG, "updateData " + mResources + " " + mResources.length);

				mResourcesReady = true;
			}
		}
	}

	// Surface.Callback
	public void surfaceCreated(SurfaceHolder holder) {
		LogUtil.d(TAG, "surfaceCreated");
		LogUtil.d(TAG, "mStoreState.Type=" + mStoreState.Type);

		mUIReady = true;
	}

	public void surfaceChanged(SurfaceHolder surfaceholder, int format,
			int width, int height) {
		LogUtil.d(TAG, "surfaceChanged" + "(" + width + "," + height + ")");
	}

	public void surfaceDestroyed(SurfaceHolder surfaceholder) {
		LogUtil.d(TAG, "surfaceDestroyed");

		mUIReady = false;
		
		// we must clear this uri of VideoView, or else
		// when the surface is created again, it will play
		// this uri again automaticly, see
		// GDVideoView/surfaceCreated/openVideo()
		mVideoView.setVideoURI(null);
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		LogUtil.d(TAG, "onPrepared");

		mCurrentState.PlayerState = PLAYER_STATE_PREPARED;
		mCurrentState.Duration = mVideoView.getDuration();
		mVideoView.start();
		
		// start task here!
		mHandler.removeCallbacks(mCheckVideoFinishedTask);
		mPlayingVideoUrl = mCurrentState.Url;
		mPlayingVideoIndex = mCurrentState.index;
		if (mDurationToPlay == 0) {
			mDurationToPlay = mCurrentState.Duration;
		}
		// delay 3 seconds for computation tolerance.
		startCheckVideoCompleteTask((mDurationToPlay + 3)*1000);

		if(mSimulateHomeKey) {
			mSimulateHomeKey = false;
			mHandler.postDelayed(mSimulateHomeKeyTask, 1000);
		} else {
			mPosterView.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		LogUtil.d(TAG, "onError what=" + what + "extra=" + extra);

		mCurrentState.PlayerState = PLAYER_STATE_ERROR;

		return true;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		LogUtil.d(TAG, "onCompletion");

		if (mCurrentState.PlayerState == PLAYER_STATE_ERROR) {
			mHandler.postDelayed(mUpdateTimeTask, PLAYMEDIA_INTERVAL_WITHERROR);
		} else {
			mHandler.postDelayed(mUpdateTimeTask, PLAYMEDIA_INTERVAL);
		}
		mCurrentState.PlayerState = PLAYER_STATE_COMPLETED;
	}

	boolean isReady() {
		//LogUtil.d(TAG, "palyMedia mResourcesReady = " + mResourcesReady + " mUIReady = " + mUIReady);

		return mResourcesReady && mUIReady && mService.isDisplaySet();
	}
	
	void playMedia() {

		if (mResources == null || mResources.length == 0) {
			return;
		}

		boolean successed = false;
		while (true) {
			if (!fetchMediaResource() && mResourceIndex < mResources.length - 1) {
				continue;
			} else {
				successed = true;
				break;
			}
		}

		if (successed) {
			String resourcePath = "";
			int resourceType = RNONE;
			if (mResources != null && mResources.length > 0) {
				resourcePath = mResources[mResourceIndex].FileURI;
				resourceType = getResourceType(mResources[mResourceIndex].Type);
			}

			if (resourceType == RVideo) {
				playVideo(resourcePath);
			} else if (resourceType == RImage) {
				drawImage(resourcePath);
			} else {
				;
			}
		}
	}

	static int getResourceType(String type) {
		if (type.equals(PreviewData.TypeVideo)) {
			return RVideo;
		} else if (type.equals(PreviewData.TypeImage)) {
			return RImage;
		} else {
			return RNONE;
		}
	}

	private void playVideo(String url) {

		LogUtil.d(TAG, " playVideo " + url);

		//:mPosterView.setVisibility(View.GONE);

		if (!url.equals("")) {

			mCurrentState.Type = RVideo;
			mCurrentState.Url = url;
			mCurrentState.index = mResourceIndex;

			LogUtil.d(TAG, " mResourceIndex: " + mResourceIndex);
			
			mCurrentState.PlayerState = PLAYER_STATE_IDLE;
			mVideoView.setVideoPath(url);

			mDurationToPlay = 0;
	
			if (mStoreState.Url != null
					&& mStoreState.Url.equals(mCurrentState.Url)) {
				mVideoView.seekTo(mStoreState.Position);
				mDurationToPlay = mStoreState.Duration - mStoreState.Position;
				
				LogUtil.d(TAG, " mStoreState.Duration " + mStoreState.Duration);
				LogUtil.d(TAG, " mStoreState.Position " + mStoreState.Position);
				LogUtil.d(TAG, " mDurationToPlay " + mDurationToPlay);
		
				clearStoreState();
			}
		}
	}

	private void drawImage(String imagePath) {

		LogUtil.d(TAG, " drawImage " + imagePath);

		mCurrentState.Type = RImage;
		mCurrentState.Url = imagePath;
		mCurrentState.index = mResourceIndex;
		mCurrentState.Duration = PLAYIMAGE_INTERVAL;
		mCurrentState.StartTime = System.currentTimeMillis();

		if (mImage != null) {
			mImage.recycle();
		}

		mImage = BitmapFactory.decodeFile(imagePath);
		mPosterView.setVisibility(View.VISIBLE);
		mPosterView.setImageBitmap(mImage);

		int remainTime = mCurrentState.Duration;
		if (mStoreState.Url != null
				&& mStoreState.Url.equals(mCurrentState.Url)) {
			remainTime = mCurrentState.Duration - mStoreState.Position;
		}
		
		clearStoreState();

		mHandler.postDelayed(mUpdateTimeTask, remainTime);
	}

	private void getResourceIndex() {
		long currentTime = System.currentTimeMillis();
		long ecleapsedTime = currentTime - mStoreState.InterruptedTime;
		if ((mStoreState.Position + (int) ecleapsedTime) > mStoreState.Duration) {
			mResourceIndex = mStoreState.index + 1;
			clearStoreState();
			LogUtil.d(TAG, " mResourceIndex " + mResourceIndex);
		} else {
			mResourceIndex = mStoreState.index;
			mStoreState.Position = mStoreState.Position + (int) ecleapsedTime;
			
			
			LogUtil.d(TAG, " mResourceIndex " + mResourceIndex + " mStoreState.Position " + mStoreState.Position);
		}
	}

	private boolean fetchMediaResource() {

		LogUtil.d(TAG, "fetchMediaResource @@@@@@ mStoreState.Type = "
				+ mStoreState.Type);

		if (mStoreState.Type != RNONE) {

			LogUtil.d(TAG, "@@@@@@ mStoreState.PlayerState = " + mStoreState.PlayerState);
			LogUtil.d(TAG, "@@@@@@ mStoreState.Index = " + mStoreState.index);

			if (mStoreState.Type == RVideo) {
				LogUtil.d(TAG, "@@@@@@ mStoreState.Type == RVideo, set mStoreState.PlayerState as PLAYER_STATE_COMPLETED forced.");
				
				//if not set to PLAYER_STATE_COMPLETED forced, when booting, play micro-window a little seconds, play a normal film a little seconds, play micro-window, killed and rebooting...
				mStoreState.PlayerState = PLAYER_STATE_COMPLETED;
				
				if (mStoreState.PlayerState == PLAYER_STATE_PREPARED) {
					LogUtil.d(TAG, "@@@@@@ PLAYER_STATE_PREPARED");
					mResourceIndex = mStoreState.index;
				} else if (mStoreState.PlayerState == PLAYER_STATE_IDLE) {
					LogUtil.d(TAG, "@@@@@@ PLAYER_STATE_IDLE");
					mResourceIndex = mStoreState.index;
				} else if (mStoreState.PlayerState == PLAYER_STATE_COMPLETED
						|| mStoreState.PlayerState == PLAYER_STATE_ERROR) {
					LogUtil.d(TAG, "@@@@@@ PLAYER_STATE_COMPLETED or PLAYER_STATE_ERROR");
					mResourceIndex = mStoreState.index + 1;
					clearStoreState();
				} else {
					;
				}
			} else if (mStoreState.Type == RImage) {
				LogUtil.d(TAG, "@@@@@@ mStoreState.Type == RImage");
				getResourceIndex();
			} else {

			}

		} else {
			LogUtil.d(TAG, "@@@@@@ mStoreState.Type == RNONE");
			mResourceIndex = mResourceIndex + 1;
		}

		if (mResources != null && mResources.length > 0) {			
			mResourceIndex = mResourceIndex % mResources.length;
		}

		LogUtil.d(TAG, "fetch resource mResourceIndex = " + mResourceIndex);
		
		return true;
	}

	private void saveMediaState() {
		LogUtil.d(TAG, "storeMediaState");

		mStoreState.Type = mCurrentState.Type;
		mStoreState.Url = mCurrentState.Url;
		mStoreState.index = mCurrentState.index;
		mStoreState.Duration = mCurrentState.Duration;
		mStoreState.InterruptedTime = System.currentTimeMillis();
		mStoreState.PlayerState = mCurrentState.PlayerState;

		LogUtil.d(TAG, "mStoreState.Type = " + mStoreState.Type);
		LogUtil.d(TAG, "mStoreState.PlayerState = " + mStoreState.PlayerState);
		LogUtil.d(TAG, "mStoreState.index = " + mStoreState.index);
		LogUtil.d(TAG, "mStoreState.url = " + mStoreState.Url);
		LogUtil.d(TAG, "mStoreState.Duration = " + mStoreState.Duration);

		if (mCurrentState.Type == RVideo) {
			if (mVideoView.isPlaying()) {
				LogUtil.d(TAG, "get position");
				mStoreState.Position = mVideoView.getCurrentPosition();
				LogUtil.d(TAG, "mStoreState.Position = " + mStoreState.Position);

				mVideoView.stopPlayback();

				mVideoView.setVideoURI(null);
			} else {
				// the play has stopped.
				LogUtil.d(TAG, "play stopped");
			}
		} else {
			// Image
			mStoreState.Position = (int) (mStoreState.InterruptedTime - mCurrentState.StartTime);
			LogUtil.d(TAG, "mStoreState.Position = " + mStoreState.Position);
		}
	}

	private void clearStoreState() {
		mStoreState.Type = RNONE;
		mStoreState.Url = "";
		mStoreState.index = -1;
		mStoreState.Position = -1;
		mStoreState.PlayerState = PLAYER_STATE_NONE;
	}
	
	private void loadResources(Context context) {
		AssetManager am = context.getAssets();

		try {
			InputStream is = am.open("default/default_0.png");
			mDefaultPoster = BitmapFactory.decodeStream(is);
			is.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static class PlayState {
		int Type;
		String Url;
		int index;

		int Position;
		int Duration;
		long InterruptedTime;
		long StartTime;

		int PlayerState;

		PlayState() {
			Type = RNONE;
			Url = "";
			index = -1;
		}
	}

}
