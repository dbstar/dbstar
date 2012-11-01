package com.dbstar.app.alert;

import java.util.Timer;
import java.util.TimerTask;

import com.dbstar.R;
import com.dbstar.DbstarDVB.model.MediaData;
import com.dbstar.app.GDBaseActivity;
import com.dbstar.model.GDCommon;
import com.dbstar.model.Movie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GDPopupActivity extends GDBaseActivity {
	
	private static final String TAG = "GDPopupActivity";
	
	private static final int MSG_UPDATETIMEOUT = 0;
	private static final int TIMEOUT_IN_MILLIONSECONDS = 5000;
	private static final int TIMEOUT_IN_SECONDS = 5;
	private static final int UpdatePeriodInMills = 1000;
	private static final int UpdatePeriodInSeconds = 1;
	
	
	MediaData mMediaData;
	TextView mMovieTitle;
	TextView mMovieDescription;
	TextView mMovieDirector;
	TextView mMovieActors;
	TextView mMovieType;
	TextView mMovieRegion;
	
	TextView mTimeoutView;
	int mTimeoutInMills = TIMEOUT_IN_MILLIONSECONDS;
	int mTimeoutInSeconds = TIMEOUT_IN_SECONDS;
	
	Button mCloseButton, mReplayButton, mAddFavouriteButton,
	mDeleteButton;
	
	Handler mUIHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_UPDATETIMEOUT: {
					if (mTimeoutInMills == 0) {
						closePopupView();
						return;
					}
					
					mTimeoutInMills -= UpdatePeriodInMills;
					mTimeoutInSeconds -= UpdatePeriodInSeconds;
					updateTimeoutView();
					break;
				}
				default:
					break;
			}
		}
	};
	
	Timer mTimer = new Timer();
	UpdateTimeoutTask mTask = null;

	class UpdateTimeoutTask extends TimerTask {
		public void run() {
			timeout();
		}
	}

	void timeout() {
		mUIHandler.sendEmptyMessage(MSG_UPDATETIMEOUT);
	}
	
	void resetTimer() {
		if (mTask != null)
			mTask.cancel();
		
		mTask = new UpdateTimeoutTask();

		mTimeoutInMills = TIMEOUT_IN_MILLIONSECONDS;
		mTimeoutInSeconds = TIMEOUT_IN_SECONDS;
		
		updateTimeoutView();
		
		mTimer.schedule(mTask, UpdatePeriodInMills, UpdatePeriodInMills);
	}
	
	void stopTimer() {
		mTask.cancel();
		mTimer.cancel();
	}
	
	void updateTimeoutView() {
		String timeout = String.valueOf(mTimeoutInSeconds);
		mTimeoutView.setText(timeout);
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.movie_info_view);
		
		Intent intent = getIntent();
//		mMediaData = (MediaData) intent.getSerializableExtra(GDCommon.KeyMediaData);
		String publicationId = intent.getStringExtra("publication_id");
		String publicationSetId = intent.getStringExtra("publicationset_id");
		
		mMediaData = new MediaData();
		mMediaData.PublicationID = publicationId;
		mMediaData.SetID = publicationSetId;
		
		initializeView();
	}
	
	public void onStart() {
		super.onStart();
		
		resetTimer();
	}
	
	public void onDestroy() {
		super.onDestroy();
		
		stopTimer();
	}
	
	public void closePopupView() {
		finish();
	}
	
	public void initializeView() {
		mTimeoutView = (TextView) findViewById(R.id.timeout_view);
		
		mMovieTitle = (TextView) findViewById(R.id.title_view);
		mMovieDescription = (TextView) findViewById(R.id.description_view);
		mMovieDirector = (TextView) findViewById(R.id.director_view);
		mMovieActors = (TextView) findViewById(R.id.actor_view);
		mMovieType = (TextView) findViewById(R.id.type_view);

		mCloseButton = (Button) findViewById(R.id.close_button);
		mReplayButton = (Button) findViewById(R.id.replay_button);
		mAddFavouriteButton = (Button) findViewById(R.id.add_favourite_button);
		mDeleteButton = (Button) findViewById(R.id.delete_button);

		mCloseButton.setOnClickListener(mClickListener);
		mReplayButton.setOnClickListener(mClickListener);
		mAddFavouriteButton.setOnClickListener(mClickListener);
		mDeleteButton.setOnClickListener(mClickListener);
		
//		mCloseButton.setOnKeyListener(mButtonListenter);
//		mReplayButton.setOnKeyListener(mButtonListenter);
//		mAddFavouriteButton.setOnKeyListener(mButtonListenter);
//		mDeleteButton.setOnKeyListener(mButtonListenter);

//		updateMovieInfo(mMovie);
		Log.d(TAG, "++++++++++++++++initializeView++++++++++++++");
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		resetTimer();
		
		return super.onKeyDown(keyCode, event);
	}
	
	View.OnClickListener mClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Log.d(TAG, "button clicked");
			buttonClicked((Button)v);
		}
	};
	
//	View.OnKeyListener mButtonListenter = new View.OnKeyListener() {
//
//		@Override
//		public boolean onKey(View v, int keyCode, KeyEvent event) {
//			Log.d(TAG, "onKey " + keyCode);
//
//			int action = event.getAction();
//			if (action == KeyEvent.ACTION_DOWN) {
//				switch (keyCode) {
//				case 82:
//				case KeyEvent.KEYCODE_ENTER:
//				case KeyEvent.KEYCODE_DPAD_CENTER:
//					if (v instanceof Button) {
//						buttonClicked((Button) v);
//					}
//					return true;
//				}
//				return false;
//			}
//
//			return false;
//		}
//	};

	private void buttonClicked(Button button) {
		Log.d(TAG, "buttonClicked clicked" + button);
		
		if (button == mCloseButton) {
			closedButtonClicked();
		} else if (button == mReplayButton) {
			repalyButtonClicked();
		} else if (button == mAddFavouriteButton) {
			saveButtonClicked();
		} else if (button == mDeleteButton) {
			deleteButtonClicked();
		} else {
			
		}
	}
	
	void closedButtonClicked() {
		Log.d(TAG, "closedButtonClicked");
		closePopupView();
	}
	
	void repalyButtonClicked() {
		Log.d(TAG, "repalyButtonClicked");
	}
	
	void saveButtonClicked() {
		Log.d(TAG, "saveButtonClicked");
		
		if (mService != null) {
			mService.addMediaToFavorite(mMediaData);
		}
	}
	
	void deleteButtonClicked() {
		Log.d(TAG, "deleteButtonClicked");
		
	}
	
	void updateView(Movie movie) {
		if (movie != null) {
			if (movie.Content.Name != null) {
				mMovieTitle.setText(movie.Content.Name);
			}

			if (movie.Description != null) {
				mMovieDescription.setText(movie.Description);
			}

			String director = getResources().getString(R.string.property_director);
			if (movie.Content.Director != null) {
				director += movie.Content.Director;
			}
			mMovieDirector.setText(director);

			String actors = getResources().getString(R.string.property_actors);
			if (movie.Content.Actors != null) {
				actors += movie.Content.Actors;
			}
			mMovieActors.setText(actors);
		}
	}
}
