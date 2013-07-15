package com.dbstar.guodian.parse;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.dbstar.guodian.data.JsonTag;
import com.dbstar.guodian.data.PowerPanelData;

public class PanelDataHandler {

	private static final String TAG = "PanelDataHandler";

	public static PowerPanelData parse(String data) {
//		Log.d(TAG, "json data = " + data);

		// remove []
		String jsonData = data.substring(1, data.length() - 1);

		PowerPanelData panelData = null;

		JSONTokener jsonParser = new JSONTokener(jsonData);

		try {
			JSONObject rootObject = (JSONObject) jsonParser.nextValue();

			// begin to read tag
			if (rootObject != null) {
				panelData = parsePanelData(rootObject);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return panelData;
	}

	private static PowerPanelData parsePanelData(JSONObject rootObject)
			throws JSONException {
		PowerPanelData panelData = null;

		panelData = new PowerPanelData();

		JSONObject object = (JSONObject) rootObject
				.getJSONObject(JsonTag.TAGYearPower);

		panelData.YearPower = DataHandler.parsePower(object);

		object = (JSONObject) rootObject.getJSONObject(JsonTag.TAGMonthPower);

		panelData.MonthPower = DataHandler.parsePower(object);

		object = (JSONObject) rootObject.getJSONObject(JsonTag.TAGPowerNumFee);
		panelData.RemainPower = DataHandler.parsePower(object);

		panelData.DailyFee = (String) rootObject
				.getString(JsonTag.TAGDailyAverFee);

		object = (JSONObject) rootObject.getJSONObject(JsonTag.TAGPriceStatus);

		panelData.PriceStatus = DataHandler.parseUserPriceStatus(object);

		object = (JSONObject) rootObject
				.getJSONObject(JsonTag.TAGDefaulttarget);

		panelData.DefaultTarget = DataHandler.parsePower(object);

		object = (JSONObject) rootObject.getJSONObject(JsonTag.TAGPowertarget);

		panelData.Target = DataHandler.parsePowerTarget(object);

		return panelData;
	}

}
