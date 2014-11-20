package com.guodian.checkdevicetool;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import com.guodian.checkdevicetool.testentry.PlayerTest;

public class PlayerVideoActivity extends Activity {

	private VideoView videoView;

	private Handler handler = new Handler(){
		
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				PlayerVideoActivity.this.finish();
			}
		};
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 设置成全屏模式
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 强制为横屏
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		setContentView(R.layout.player_video);
		
		Intent intent = getIntent();
		String path = intent.getStringExtra(PlayerTest.Player_Video_Test_Path);

		try {
			videoView = (VideoView) findViewById(R.id.videoView);
			if (path != null) {			
				videoView.setVideoURI(Uri.parse(path));
				MediaController mediaController = new MediaController(this);
				videoView.setMediaController(mediaController);
				videoView.requestFocus();
				videoView.start();
			}
		} catch (Exception e) {			
			PlayerVideoActivity.this.finish();
			e.printStackTrace();
		}
		
		final Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				if (!videoView.isPlaying()) {
					handler.sendEmptyMessage(1);
					if (timer != null) {
						timer.cancel();
					}
				}
			}
		};
		timer.schedule(task, 10 * 1000, 1000);
	}
}
