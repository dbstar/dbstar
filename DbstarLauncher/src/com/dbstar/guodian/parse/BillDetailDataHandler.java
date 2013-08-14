package com.dbstar.guodian.parse;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.dbstar.guodian.data.BillDetail;
import com.dbstar.guodian.data.BillDetailData;
import com.dbstar.guodian.data.BillItem;
import com.dbstar.guodian.data.JsonTag;

public class BillDetailDataHandler {
	private static final String TAG = "BillDetailDataHandler";

	public static BillDetailData parse(String data) {

//		Log.d(TAG, "json data = " + data);

		BillDetailData detailData = null;

		JSONTokener jsonParser = new JSONTokener(data);

		try {
			JSONArray array = (JSONArray) jsonParser.nextValue();

			JSONObject rootObject = (JSONObject) array.get(0);

			// begin to read tag
			detailData = new BillDetailData();
			JSONObject object = (JSONObject) rootObject
					.getJSONObject(JsonTag.TAGBillDetail);
			detailData.Detail = parseBillDetail(object);

			detailData.ServiceSysDate = (String) rootObject
					.getString(JsonTag.TAGServiceSysDate);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return detailData;
	}

	public static BillDetail parseBillDetail(JSONObject rootObject)
			throws JSONException {
		BillDetail billDetail = new BillDetail();
		billDetail.UserName = (String) rootObject
				.getString(JsonTag.TAGVC2UserName);
		billDetail.StartDate = (String) rootObject
				.getString(JsonTag.TAGDateStart);
		billDetail.EndDate = (String) rootObject.getString(JsonTag.TAGDateEnd);
		billDetail.TotalCost = (String) rootObject.getString(JsonTag.TAGNumSum);

		JSONArray array = (JSONArray) rootObject
				.getJSONArray(JsonTag.TAGBillItems); // TODO: which tag?

		if (array != null) {
			billDetail.BillList = parseBillList(array);
		}

		return billDetail;
	}

	private static ArrayList<BillItem> parseBillList(JSONArray array)
			throws JSONException {
		int length = array.length();
		if (length == 0)
			return null;

		ArrayList<BillItem> list = new ArrayList<BillItem>();
		for (int i = 0; i < length; i++) {
			BillItem item = new BillItem();
			JSONObject object = (JSONObject) array.getJSONObject(i);

			item.Type = (String) object.getString(JsonTag.TAGVC2BillItem);
			item.Count = (String) object.getString(JsonTag.TAGNumCount);
			item.Fee = (String) object.getString(JsonTag.TAGNumFee);

			list.add(item);
		}

		return list;
	}
}
