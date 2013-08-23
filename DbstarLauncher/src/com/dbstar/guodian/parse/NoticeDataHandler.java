package com.dbstar.guodian.parse;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import com.dbstar.guodian.data.JsonTag;
import com.dbstar.guodian.data.Notice;

public class NoticeDataHandler {
	private static final String TAG = "NoticeDataHandler";

	public static ArrayList<Notice> parse(String data) {
//		Log.d(TAG, "json data = " + data);

		ArrayList<Notice> notices = null;

		JSONTokener jsonParser = new JSONTokener(data);

		try {
			JSONArray array = (JSONArray) jsonParser.nextValue();

			JSONObject rootObject = (JSONObject) array.get(0);

			JSONArray noticesArray = (JSONArray) rootObject
					.getJSONArray(JsonTag.TAGNoticeList);
			if (noticesArray != null) {
				int length = noticesArray.length();
				notices = new ArrayList<Notice>();
				for (int i = 0; i < length; i++) {
					JSONObject object = noticesArray.getJSONObject(i);
					Notice notice = parseNotice(object);
					notices.add(notice);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return notices;
	}

	public static Notice parseNotice(JSONObject object) throws JSONException {
		Notice notice = new Notice();
		notice.Guid = (String) object.getString(JsonTag.TAGNumNoticeGuid);
		notice.Title = (String) object.getString(JsonTag.TAGVC2Title);
		notice.Content = (String) object.getString(JsonTag.TAGVC2Content);
		notice.Date = (String) object.getString(JsonTag.TAGDateTime);

		return notice;
	}
}
