package com.dbstar.guodian.parse;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.dbstar.guodian.data.JsonTag;

public class BillMonthListHandler {
	private static final String TAG = "BillDetailDataHandler";

	//[{"billmonthlist":[]}]

	public static ArrayList<String> parse(String data) {

//		Log.d(TAG, "json data = " + data);
		// remove []
		String jsonData = data.substring(1, data.length() - 1);

		ArrayList<String> monthList = null;

		JSONTokener jsonParser = new JSONTokener(jsonData);

		try {
			JSONObject rootObject = (JSONObject) jsonParser.nextValue();

			JSONObject object = (JSONObject) rootObject
					.getJSONObject(JsonTag.TAGBillMonthList);
			// begin to read tag
			monthList = parseMonthList(object);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return monthList;
	}

	private static ArrayList<String> parseMonthList(JSONObject object) {
		ArrayList<String> monthlist = new ArrayList<String>();
		return monthlist;
	}
}
