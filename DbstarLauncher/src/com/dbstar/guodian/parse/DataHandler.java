package com.dbstar.guodian.parse;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.dbstar.guodian.data.JsonTag;
import com.dbstar.guodian.data.PowerData;
import com.dbstar.guodian.data.PowerTarget;
import com.dbstar.guodian.data.UserPriceStatus;

public class DataHandler {
	private static final String TAG = "DataHandler";

	public static PowerData parsePower(JSONObject object) throws JSONException {
		Log.d(TAG, "parsePower");

		PowerData data = new PowerData();
		data.Count = (String) object.getString(JsonTag.TAGNumCount);
		data.Fee = (String) object.getString(JsonTag.TAGNumFee);

		return data;
	}

	public static PowerTarget parsePowerTarget(JSONObject object) throws JSONException {
		Log.d(TAG, "parsePowerTarget");

		PowerTarget target = new PowerTarget();
		target.Guid = (String) object.getString(JsonTag.TAGNumGuid);
		target.CCGuid = (String) object.getString(JsonTag.TAGNumCCGuid);
		target.mPower = new PowerData();
		target.mPower.Count = (String) object.getString(JsonTag.TAGPowerNum);
		target.mPower.Fee = (String) object.getString(JsonTag.TAGPowerFee);
		target.Type = (String) object.getString(JsonTag.TAGNumOrFee);
		return target;
	}

	public static UserPriceStatus parseUserPriceStatus(JSONObject object)
			throws JSONException {

		Log.d(TAG, "parseUserPriceStatus");

		UserPriceStatus status = new UserPriceStatus();

		status.PriceType = (String) object.getString(JsonTag.TAGVC2PriceType);
		status.CycleType = (String) object.getString(JsonTag.TAGVC2CycleType);
		status.Step = (String) object.getString(JsonTag.TAGVC2CurrenStep);

		status.CurrentPeriodType = (String) object
				.getString(JsonTag.TAGVC2CurrentPeriodType);
		status.Period = (String) object.getString(JsonTag.TAGVC2PeriodDetail);
		status.Price = (String) object.getString(JsonTag.TAGNumElePrice);

		return status;
	}
}
