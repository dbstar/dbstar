package com.dbstar.DbstarDVB.VideoPlayer.alert;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.dbstar.DbstarDVB.R;

public class DbVideoInfoDlg extends Dialog implements ViewStateManager {
	Intent mIntent = null;
	ViewState mState;
	MediaData mMediaData;

	public DbVideoInfoDlg(Context context, Intent intent) {
		super(context, R.style.DbDialog);

		mMediaData = getMediaData(intent);
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mState = new NormalState(this, this);
		mState.enter(mMediaData);
	}

	public void onStart() {
		super.onStart();

		mState.start();
	}

	public void onStop() {
		super.onStop();

		mState.stop();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {

		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			Log.d("DbVideoInfoDlg",
					"==== onKeyDown keyCode ====" + event.getKeyCode());

			mState.keyEvent(event.getKeyCode(), event);
		}

		return super.dispatchKeyEvent(event);
	}

	@Override
	public void changeToState(ViewState state, Object args) {
		mState.stop();
		mState.exit();

		state.enter(args);
		state.start();
		mState = state;
	}

	MediaData getMediaData(Intent intent) {
		MediaData mediaData = new MediaData();

		mediaData.PublicationSetID = intent.getStringExtra("publicationset_id");
		mediaData.PublicationId = intent.getStringExtra("publication_id");
		mediaData.Title = intent.getStringExtra("title");
		mediaData.Description = intent.getStringExtra("description");
		mediaData.Director = intent.getStringExtra("director");
		mediaData.Actors = intent.getStringExtra("actors");
		mediaData.Type = intent.getStringExtra("type");

		Log.d("DbVideoInfoDlg", "PublicationId = " + mediaData.PublicationId);
		Log.d("DbVideoInfoDlg", "Description = " + mediaData.Description);
		return mediaData;
	}
}
