package com.dbstar.app;

import com.dbstar.R;
import com.dbstar.model.GDDVBDataContract.Content;
import com.dbstar.model.Movie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GDPopupActivity extends Activity {
	TextView mMovieTitle;
	TextView mMovieDescription;
	TextView mMovieDirector;
	TextView mMovieActors;
	TextView mMovieType;
	TextView mMovieRegion;
	
	Button mCloseButton, mReplayButton, mAddFavouriteButton,
	mDeleteButton;
	
	Movie mMovie;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.movie_info_view);
		
		Intent intent = getIntent();
		
		initializeView();
	}
	
	void initializeView() {
		mMovieTitle = (TextView) findViewById(R.id.title_view);
		mMovieDescription = (TextView) findViewById(R.id.description_view);
		mMovieDirector = (TextView) findViewById(R.id.director_view);
		mMovieActors = (TextView) findViewById(R.id.actor_view);
		mMovieType = (TextView) findViewById(R.id.type_view);

		mCloseButton = (Button) findViewById(R.id.close_button);
		mReplayButton = (Button) findViewById(R.id.replay_button);
		mAddFavouriteButton = (Button) findViewById(R.id.add_favourite_button);
		mDeleteButton = (Button) findViewById(R.id.delete_button);

		mCloseButton.setOnKeyListener(mButtonListenter);
		mReplayButton.setOnKeyListener(mButtonListenter);
		mAddFavouriteButton.setOnKeyListener(mButtonListenter);
		mDeleteButton.setOnKeyListener(mButtonListenter);

		updateMovieInfo(mMovie);
	}
	
	View.OnKeyListener mButtonListenter = new View.OnKeyListener() {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			int action = event.getAction();
			if (action == KeyEvent.ACTION_DOWN) {
				switch (keyCode) {
				case KeyEvent.KEYCODE_ENTER:
				case KeyEvent.KEYCODE_DPAD_CENTER:
					if (v instanceof Button) {
						buttonClicked((Button) v);
					}
					return true;
				}
				return false;
			}

			return false;
		}
	};

	private void buttonClicked(Button button) {
		if (button == mCloseButton) {
			;
		}
	}
	
	void updateMovieInfo(Movie movie) {
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
