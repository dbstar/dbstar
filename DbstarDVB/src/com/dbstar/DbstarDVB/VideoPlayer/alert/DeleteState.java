package com.dbstar.DbstarDVB.VideoPlayer.alert;

import android.app.Dialog;
import android.view.View;
import android.widget.Button;

import com.dbstar.DbstarDVB.R;

public class DeleteState extends ViewState {
	Button mDeleteButton, mNotDeleteButton;
	MediaData mMediaData;

	public DeleteState(Dialog dlg, ViewStateManager manager) {
		super(dlg, manager);
	}

	public void enter(Object args) {
		mMediaData = (MediaData) args;

		mDialog.setContentView(R.layout.delete_confirm_view);
		initializeView(mDialog);

		mActionHandler = new ActionHandler(mDialog.getContext(), mMediaData);
	}

	private void initializeView(Dialog dlg) {
		mDeleteButton = (Button) dlg.findViewById(R.id.delete_button);
		mNotDeleteButton = (Button) dlg.findViewById(R.id.donot_delete_button);

		mDeleteButton.setOnClickListener(mClickListener);
		mNotDeleteButton.setOnClickListener(mClickListener);

		mNotDeleteButton.requestFocus();
	}

	View.OnClickListener mClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == mDeleteButton) {
				deleteButtonClicked();
			} else if (v == mNotDeleteButton) {
				notDeleteButtonClicked();
			}
		}
	};

	void deleteButtonClicked() {
		closePopupView();

		mActionHandler.sendCommnd(ActionHandler.COMMAND_EXIT_PLAYER);
		mActionHandler.sendCommnd(ActionHandler.COMMAND_DELETE);
	}

	void notDeleteButtonClicked() {
		closePopupView();
	}

	void closePopupView() {
		mDialog.dismiss();
	}
}
