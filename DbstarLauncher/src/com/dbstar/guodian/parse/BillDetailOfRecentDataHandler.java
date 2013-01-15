package com.dbstar.guodian.parse;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

import com.dbstar.guodian.data.BillDetail;
import com.dbstar.guodian.data.BillDetailListData;
import com.dbstar.guodian.data.JsonTag;

public class BillDetailOfRecentDataHandler {
	private static final String TAG = "BillDetailDataHandler";

	public static BillDetailListData parse(String data) {

//		Log.d(TAG, "json data = " + data);
		// remove []
		String jsonData = data.substring(1, data.length() - 1);
		JSONTokener jsonParser = new JSONTokener(jsonData);

		BillDetailListData detailData = null;

		try {
			JSONObject rootObject = (JSONObject) jsonParser.nextValue();

			JSONArray array = (JSONArray) rootObject
					.getJSONArray(JsonTag.TAGBillDetailList);

			detailData = new BillDetailListData();

			if (array != null && array.length() > 0) {
				detailData.DetailList = parseBillDetails(array);
			}

			detailData.ServiceSysDate = (String) rootObject
					.getString(JsonTag.TAGServiceSysDate);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return detailData;
	}

	public static ArrayList<BillDetail> parseBillDetails(JSONArray arrary)
			throws JSONException {
		int length = arrary.length();
		ArrayList<BillDetail> detailList = new ArrayList<BillDetail>();
		for (int i = 0; i < length; i++) {
			JSONObject object = (JSONObject) arrary.getJSONObject(i);
			BillDetail detail = BillDetailDataHandler.parseBillDetail(object);

			detailList.add(detail);
		}

		return detailList;
	}
}