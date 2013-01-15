package com.dbstar.guodian.parse;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

import com.dbstar.guodian.data.JsonTag;
import com.dbstar.guodian.data.BusinessArea;

public class BusinessAreaHandler {
	private static final String TAG = "BusinessAreaHandler";

	public static ArrayList<BusinessArea> parse(String data) {
//		Log.d(TAG, "json data = " + data);

		ArrayList<BusinessArea> businessAreas = null;

		JSONTokener jsonParser = new JSONTokener(data);

		try {
			JSONArray array = (JSONArray) jsonParser.nextValue();

			JSONObject rootObject = (JSONObject) array.get(0);

			JSONArray businessArray = (JSONArray) rootObject
					.getJSONArray(JsonTag.TAGBusinessList);

			if (businessArray != null) {
				int length = businessArray.length();
				businessAreas = new ArrayList<BusinessArea>();
				for (int i = 0; i < length; i++) {
					JSONObject object = businessArray.getJSONObject(i);
					BusinessArea business = parseBusiness(object);
					businessAreas.add(business);
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return businessAreas;
	}

	public static BusinessArea parseBusiness(JSONObject object)
			throws JSONException {
		BusinessArea business = new BusinessArea();

		business.Name = (String) object.getString(JsonTag.TAGVC2Name);
		business.Address = (String) object.getString(JsonTag.TAGVC2Address);
		business.Telephone = (String) object.getString(JsonTag.TAGVC2Telephone);
		business.WorkTime = (String) object.getString(JsonTag.TAGVC2WorkTime);

		return business;
	}
}
