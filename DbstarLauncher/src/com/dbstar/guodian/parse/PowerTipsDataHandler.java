package com.dbstar.guodian.parse;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.dbstar.guodian.data.JsonTag;
import com.dbstar.guodian.data.PowerTips;

public class PowerTipsDataHandler {
	private static final String TAG = "PowerTipsDataHandler";

	public static ArrayList<PowerTips> parse(String data) {
//		Log.d(TAG, "json data = " + data);

		ArrayList<PowerTips> tips = null;

		JSONTokener jsonParser = new JSONTokener(data);

		try {
			JSONArray array = (JSONArray) jsonParser.nextValue();

			JSONObject rootObject = (JSONObject) array.get(0);

			JSONArray tipsArray = (JSONArray) rootObject
					.getJSONArray(JsonTag.TAGPowerTipsList);
			if (tipsArray != null) {
				int length = tipsArray.length();
				tips = new ArrayList<PowerTips>();
				for (int i = 0; i < length; i++) {
					JSONObject object = tipsArray.getJSONObject(i);
					PowerTips tip = parseTips(object);
					tips.add(tip);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return tips;
	}

	public static PowerTips parseTips(JSONObject object) throws JSONException {
	    PowerTips tip = new PowerTips();
		tip.TipsContent = (String) object.getString(JsonTag.TAGPowerTipsContent);
		tip.TipsTitle = (String) object.getString(JsonTag.TAGPowerTipsTitle);
		tip.TipsGuid = (String) object.getString(JsonTag.TAGPowerTipsGuid);
		tip.TipsLastUpdateTime = (String) object.getString(JsonTag.TAGDateLastUpdate);

		return tip;
	}
}
