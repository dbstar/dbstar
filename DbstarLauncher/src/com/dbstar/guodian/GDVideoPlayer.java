package com.dbstar.guodian;

import com.dbstar.guodian.widget.GDVideoView;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

public class GDVideoPlayer extends Activity implements OnCompletionListener,
		OnErrorListener, OnPreparedListener, SurfaceHolder.Callback {

	private static final String TAG = "GDVideoPlayer";
	private String mUri;
	private GDVideoView mVideoView;
	private SurfaceHolder mHolder;
	MediaController mMediaController;
	
	Activity activity = this;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.videoplayer);

		mVideoView = (GDVideoView) findViewById(R.id.player_view);

		Intent intent = getIntent();
		mUri = intent.getStringExtra("Uri");

		mHolder = mVideoView.getHolder();
		mHolder.addCallback(this);

		mVideoView.setOnCompletionListener(this);
		mVideoView.setOnPreparedListener(this);
		mVideoView.setOnErrorListener(this);

		mMediaController = new MediaController(this);
		
		mMediaController.setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				Log.d(TAG, "onKey " + keyCode);
				boolean ret = false;
				int action = event.getAction();
				if (action == KeyEvent.ACTION_DOWN) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_ESCAPE:
					case KeyEvent.KEYCODE_BACK: {
						ret = true;
						activity.onBackPressed();
						break;
					}
					default:
						break;
					}
				}
				return ret;
			}
		});
		
		mVideoView.setMediaController(mMediaController);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!mUri.equals("")) {
			mVideoView.setVideoPath(mUri);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub
		mVideoView.start();
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub

	}
}
