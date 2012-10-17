package com.dbstar.app.media;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class GDPlayerUtil {

	private static final String TAG = "GDPlayerUtil";
	
	public static void playVideo(Context context, String mainFile, String drmFile) {
		Log.d(TAG, "file = " + mainFile);
		Log.d(TAG, "drm file = " + drmFile);
		if (!mainFile.equals("")) {
			Intent intent = new Intent();

			final String schema = "file://";
			String path = schema + mainFile;
			if (drmFile != null && !drmFile.isEmpty()) {
				path += "|" + drmFile;
			}
			
			Uri uri = Uri.parse(path);

			Log.d(TAG, "play = " + uri.toString());

			intent.setData(uri);
//			intent.putExtra("publication_id", movie.Content.Id);
//			intent.putExtra("column_type", GDCommon.ColumnTypeMovie);
			
//			intent.setComponent(new ComponentName("com.farcore.videoplayer",
//					"com.farcore.videoplayer.playermenu"));
//			intent.setAction("android.intent.action.View");
			
			intent.setComponent(new ComponentName("com.dbstar.DbstarDVB",
					"com.dbstar.DbstarDVB.VideoPlayer.PlayerMenu"));
			intent.setAction("android.intent.action.View");
			
			context.startActivity(intent);
		}
	}
}
