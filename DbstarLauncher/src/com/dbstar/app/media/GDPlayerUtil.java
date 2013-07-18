package com.dbstar.app.media;

import java.util.ArrayList;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.dbstar.app.GDBaseActivity;
import com.dbstar.model.ContentData;
import com.dbstar.model.GDCommon;
import com.dbstar.util.FileOperation;
import com.dbstar.util.LogUtil;

public class GDPlayerUtil {

	private static final String TAG = "GDPlayerUtil";
	private static final String Fb0Blank = "/sys/class/graphics/fb0/blank";

	public static void playVideo(Context context, String publicationSetID,
			ContentData content, String mainFile, String drmFile, boolean playNext) {
	    LogUtil.d(TAG, "file = " + mainFile);
	    LogUtil.d(TAG, "drm file = " + drmFile);
		if (mainFile != null && !mainFile.equals("")) {
			Intent intent = new Intent();

			final String schema = "file://";
			String path = schema + mainFile;
			if (drmFile != null && !drmFile.isEmpty()) {
				path += "|" + drmFile;
			}

			Uri uri = Uri.parse(path);

			LogUtil.d(TAG, "play = " + uri.toString());

			intent.setData(uri);
			if (publicationSetID != null && !publicationSetID.isEmpty()) {
				intent.putExtra("publicationset_id", publicationSetID);
			}

			addMetaData(intent, content, playNext);

			intent.setComponent(new ComponentName("com.dbstar.DbstarDVB",
					"com.dbstar.DbstarDVB.VideoPlayer.PlayerMenu"));
			intent.setAction("android.intent.action.View");

			FileOperation.write(Fb0Blank, "1");
			//FileOperation.saveValueToFile(context, GDCommon.FlagPlayVideo, "1");
			
			GDBaseActivity activity = (GDBaseActivity) context;
			activity.startActivity(intent, false);
		}
	}
	
	public static void playNextVideo(Context context, String publicationSetID,
			ContentData content, String mainFile, String drmFile, boolean playNext) {
	    LogUtil.d(TAG, "play next video");
		LogUtil.d(TAG, "file = " + mainFile);
		LogUtil.d(TAG, "drm file = " + drmFile);
		
		if (mainFile != null && !mainFile.equals("")) {
			Intent intent = new Intent(GDCommon.ActionPlayNext);

			final String schema = "file://";
			String path = schema + mainFile;
			if (drmFile != null && !drmFile.isEmpty()) {
				path += "|" + drmFile;
			}

			Uri uri = Uri.parse(path);

			LogUtil.d(TAG, "play = " + uri.toString());

			intent.setData(uri);
			if (publicationSetID != null && !publicationSetID.isEmpty()) {
				intent.putExtra("publicationset_id", publicationSetID);
			}

			addMetaData(intent, content, playNext);
			
			context.sendBroadcast(intent);
		}
	}
	
	public static void addMetaData(Intent intent, ContentData content, boolean playNext) {
		intent.putExtra("publication_id", content.Id);
		intent.putExtra("title", content.Name);
		intent.putExtra("description", content.Description);
		intent.putExtra("director", content.Director);	
		intent.putExtra("scenarist", content.Scenarist);
		intent.putExtra("actors", content.Actors);
		intent.putExtra("type", content.Type);
		intent.putExtra("area", content.Area);
		intent.putExtra("resolution", content.MainFile.Resolution);
		intent.putExtra("bitrate", content.MainFile.BitRate);
		intent.putExtra("codeformat", content.MainFile.CodeFormat); 
		intent.putExtra("play_next", playNext);

		intent.putExtra("bookmark", content.BookMark);

		if (content.SubTitles != null && content.SubTitles.size() > 0) {
			ArrayList<String> subtitleUris = new ArrayList<String>();
			for (int i = 0; i < content.SubTitles.size(); i++) {
				ContentData.SubTitle subtitle = content.SubTitles.get(i);
				LogUtil.d(TAG, "add subtitle=" + subtitle.URI);
				subtitleUris.add(subtitle.URI);
			}
			intent.putStringArrayListExtra("subtitle_uri", subtitleUris);
		}

	}
}
