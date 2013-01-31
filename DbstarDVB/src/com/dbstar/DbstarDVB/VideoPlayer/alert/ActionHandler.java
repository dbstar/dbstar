package com.dbstar.DbstarDVB.VideoPlayer.alert;

import com.dbstar.DbstarDVB.VideoPlayer.Common;

import android.content.Context;
import android.content.Intent;

public class ActionHandler {
	public static final int COMMAND_REPLAY = 0;
	public static final int COMMAND_EXIT_PLAYER = 1;
	public static final int COMMAND_ADDTOFAVOURITE = 2;
	public static final int COMMAND_DELETE = 3;
	public static final int COMMAND_PLAY_NEXT = 4;

	MediaData mActionData;
	Context mContext;

	public ActionHandler(Context context, MediaData actionData) {
		mActionData = actionData;
		mContext = context;
	}

	public void sendCommnd(int cmd) {
		Intent intent = null;
		switch (cmd) {
		case COMMAND_REPLAY: {
			intent = new Intent();
			intent.setAction(Common.ActionReplay);
			break;
		}
		
		case COMMAND_EXIT_PLAYER: {
			intent = new Intent();
			intent.setAction(Common.ActionExit);
			break;
		}
		
		case COMMAND_ADDTOFAVOURITE: {

			String publicationId = mActionData.PublicationId;
			String publicationSetId = mActionData.PublicationSetID;

			intent = new Intent();
			intent.setAction(Common.ActionAddToFavourite);
			intent.putExtra("publication_id", publicationId);
			if (publicationSetId != null) {
				intent.putExtra("publicationset_id", publicationSetId);
			}
			break;
		}
		case COMMAND_DELETE: {
			String publicationId = mActionData.PublicationId;
			String publicationSetId = mActionData.PublicationSetID;

			intent = new Intent();
			intent.setAction(Common.ActionDelete);
			intent.putExtra("publication_id", publicationId);
			if (publicationSetId != null) {
				intent.putExtra("publicationset_id", publicationSetId);
			}
			break;
		}
		}

		if (intent != null) {
			mContext.sendBroadcast(intent);
		}
	}
}
