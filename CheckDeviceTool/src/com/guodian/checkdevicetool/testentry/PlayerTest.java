package com.guodian.checkdevicetool.testentry;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.guodian.checkdevicetool.PlayerVideoActivity;
import com.guodian.checkdevicetool.R;

public class PlayerTest extends TestTask{

	protected int mCount = 0;
	private String TAG = "playerTest";
	
	public static String Player_Video_Test_Path = "player_video_test";

	public PlayerTest(Context context, Handler handler, int viewId, boolean isAuto) {
		super(context, handler, viewId, isAuto);
	}
	
	@Override
	public void start() {
		super.start();
		// TODO:
		String path = "/storage/external_storage/sda1/pushroot/preview/11.ts";
		testVideo(path);
	}

	private void testVideo(String path) {
		File file = new File(path);
		
		if (file.exists()) {
			try {
				Intent intent = new Intent();
				intent.putExtra(Player_Video_Test_Path, path);
				intent.setClass(context, PlayerVideoActivity.class);
				context.startActivity(intent);
//				Log.d(TAG, "-------------------------start player video activity !!!");
				
				mHandler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						sendSuccessMsg(context.getResources().getString(R.string.test_video_success));					
					}
				}, 10 * 1000);
				
			} catch (Exception e) {
				sendFailMsg(context.getResources().getString(R.string.test_video_failed));				
				e.printStackTrace();
			}
		} else {
//			Log.d(TAG, "-------------------------disk preview path is not exists!!!");
			sendFailMsg(context.getResources().getString(R.string.test_video_file_not_found));
		}
	}
}
