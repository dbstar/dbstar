package com.dbstar.guodian.parse;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

import com.dbstar.guodian.data.CtrlNo;
import com.dbstar.guodian.data.ElectricityPrice;
import com.dbstar.guodian.data.JsonTag;
import com.dbstar.guodian.data.LoginData;
import com.dbstar.guodian.data.PowerPanelData;
import com.dbstar.guodian.data.UserData;

public class LoginDataHandler {

	private static final String TAG = "LoginDataHandler";

	public static LoginData parse(String data) {

//		Log.d(TAG, "json data = " + data);

		// remove []
		String jsonData = data.substring(1, data.length() - 1);

		LoginData loginData = new LoginData();

		JSONTokener jsonParser = new JSONTokener(jsonData);

		try {
			JSONObject rootObject = (JSONObject) jsonParser.nextValue();

			// begin to read tag
			JSONArray array = (JSONArray) rootObject
					.getJSONArray(JsonTag.TAGRoomList);
			array = (JSONArray) rootObject.getJSONArray(JsonTag.TAGEleDevList);
			array = (JSONArray) rootObject.getJSONArray(JsonTag.TAGModeList);
			array = (JSONArray) rootObject
					.getJSONArray(JsonTag.TAGTimeTaskList);

			loginData.PanelData = new PowerPanelData();

			JSONObject object = (JSONObject) rootObject
					.getJSONObject(JsonTag.TAGYearPower);

			loginData.PanelData.YearPower = DataHandler.parsePower(object);

			object = (JSONObject) rootObject
					.getJSONObject(JsonTag.TAGLastPower);
			loginData.PanelData.RemainPower = DataHandler.parsePower(object);

			object = (JSONObject) rootObject
					.getJSONObject(JsonTag.TAGMonthPower);
			loginData.PanelData.MonthPower = DataHandler.parsePower(object);

			object = (JSONObject) rootObject
					.getJSONObject(JsonTag.TAGDefaultTarget);
			loginData.PanelData.DefaultTarget = DataHandler.parsePower(object);

			object = (JSONObject) rootObject
					.getJSONObject(JsonTag.TAGPowerTarget);
			loginData.PanelData.Target = DataHandler.parsePowerTarget(object);

			object = (JSONObject) rootObject
					.getJSONObject(JsonTag.TAGUserPrice);
			loginData.PanelData.PriceStatus = DataHandler
					.parseUserPriceStatus(object);

			object = (JSONObject) rootObject
					.getJSONObject(JsonTag.TAGElecPrice);
			loginData.ElecPrice = parseElecPrice(object);

			loginData.PanelData.DailyFee = (String) rootObject
					.getString(JsonTag.TAGDailyFee);

			object = (JSONObject) rootObject.getJSONObject(JsonTag.TAGCtrlNo);
			loginData.CtrlNo = parseCtrlNo(object);

			loginData.UserData = new UserData();

			loginData.UserData.AreaName = (String) rootObject
					.getString(JsonTag.TAGAreaName);
			loginData.UserData.UserType = (String) rootObject
					.getString(JsonTag.TAGUserType);
			object = (JSONObject) rootObject.getJSONObject(JsonTag.TAGUserInfo);
			loginData.UserData.UserInfo = parseUserInfo(object);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return loginData;
	}

	static ElectricityPrice parseElecPrice(JSONObject object)
			throws JSONException {
		Log.d(TAG, "parseElecPrice");

		ElectricityPrice priceData = new ElectricityPrice();

		priceData.Type = (String) object.getString(JsonTag.TAGEleType);
		if (priceData.Type.equals(ElectricityPrice.PRICETYPE_SINGLE)) {
			priceData.SinglePrice = (String) object
					.getString(JsonTag.TAGEleSinglePrice);
		} else if (priceData.Type.equals(ElectricityPrice.PRICETYPE_STEP)) {
			JSONArray stepPrice = (JSONArray) object
					.getJSONArray(JsonTag.TAGStepPriceList);
			priceData.StepPriceList = parseStepPriceList(stepPrice, false);
		} else if (priceData.Type
				.equals(ElectricityPrice.PRICETYPE_STEPPLUSTIMING)) {
			JSONArray stepPrice = (JSONArray) object
					.getJSONArray(JsonTag.TAGStepPriceList);
			priceData.StepPriceList = parseStepPriceList(stepPrice, true);
		} else if (priceData.Type.equals(ElectricityPrice.PRICETYPE_TIMING)) {
			JSONArray periodPrice = (JSONArray) object
					.getJSONArray(JsonTag.TAGPeriodPriceList);
			priceData.PeriodPriceList = parsePeriodPriceList(periodPrice);
		}

		return priceData;
	}

	static List<ElectricityPrice.StepPrice> parseStepPriceList(JSONArray array,
			boolean timingStepPower) throws JSONException {
		Log.d(TAG, "parseStepPriceList");

		List<ElectricityPrice.StepPrice> stepPriceList = new ArrayList<ElectricityPrice.StepPrice>();

		Log.d(TAG, " step size = " + array.length());
		
		for (int i = 0; i < array.length(); i++) {
			JSONObject object = (JSONObject) array.getJSONObject(i);

			ElectricityPrice.StepPrice stepPrice = new ElectricityPrice.StepPrice();
			stepPrice.GroupName = (String) object
					.getString(JsonTag.TAGVC2StepGroupName);
			stepPrice.CycleType = (String) object
					.getString(JsonTag.TAGVC2CycleType);

			stepPrice.Step = (String) object.getString(JsonTag.TAGNumStep);
			stepPrice.StepStartValue = (String) object
					.getString(JsonTag.TAGNumStart);
			stepPrice.StepEndValue = (String) object
					.getString(JsonTag.TAGNumEnd);

			stepPrice.StepEndValue = (String) object
					.getString(JsonTag.TAGNumEnd);
			stepPrice.StepPrice = (String) object
					.getString(JsonTag.TAGNumStepPrice);

			if (timingStepPower) {
				stepPrice.PeriodPriceList = parsePeriodPriceList(object
						.getJSONArray(JsonTag.TAGPeriodPriceList));
			}
			
			stepPriceList.add(stepPrice);
		}

		return stepPriceList;
	}

	static List<ElectricityPrice.PeriodPrice> parsePeriodPriceList(
			JSONArray array) throws JSONException {

		Log.d(TAG, "parsePeriodPriceList");

		List<ElectricityPrice.PeriodPrice> priceList = new ArrayList<ElectricityPrice.PeriodPrice>();
		for (int i = 0; i < array.length(); i++) {
			JSONObject object = (JSONObject) array.getJSONObject(i);

			ElectricityPrice.PeriodPrice price = new ElectricityPrice.PeriodPrice();
			price.GroupName = (String) object
					.getString(JsonTag.TAGVC2PeriodGroupName);
			price.PeriodType = (String) object
					.getString(JsonTag.TAGVC2PeriodType);
			price.TimePeriod = (String) object.getString(JsonTag.TAGTimeList);
			price.Price = (String) object.getString(JsonTag.TAGNumPrice);
			priceList.add(price);
		}

		return priceList;
	}

	static CtrlNo parseCtrlNo(JSONObject object) throws JSONException {
		Log.d(TAG, "parseCtrlNo");

		CtrlNo ctrlNo = new CtrlNo();
		ctrlNo.CtrlNoGuid = (String) object.getString(JsonTag.TAGCtrlNoGuid);
		ctrlNo.CtrilSerialNo = (String) object
				.getString(JsonTag.TAGCtrlSerialNo);
		return ctrlNo;
	}

	static UserData.UserInfo parseUserInfo(JSONObject object)
			throws JSONException {

		Log.d(TAG, "parseUserInfo");

		UserData.UserInfo userInfo = new UserData.UserInfo();

		userInfo.Account = (String) object.getString(JsonTag.TAGVC2UserAccount);
		userInfo.Guid = (String) object.getString(JsonTag.TAGVC2UserGuid);
		userInfo.AreaGuid = (String) object.getString(JsonTag.TAGNumAreaGuid);
		userInfo.Name = (String) object.getString(JsonTag.TAGVC2UserName);
		userInfo.Sexual = (String) object.getString(JsonTag.TAGVC2UserSexual);
		userInfo.Mobile = (String) object.getString(JsonTag.TAGVC2UserMobile);
		userInfo.Phone = (String) object.getString(JsonTag.TAGVC2UserPhone);
		userInfo.Address = (String) object.getString(JsonTag.TAGVC2UserAddress);		
		userInfo.Email = (String) object.getString(JsonTag.TAGVC2UserEmail);
		userInfo.PriceType = (String) object.getString(JsonTag.TAGNumPriceType);
		userInfo.PriceGroupName = (String) object
				.getString(JsonTag.TAGVC2PriceGroupName);
		userInfo.ElecCard = (String) object
				.getString(JsonTag.TAGVC2UserElecCard);
		userInfo.UserType = (String) object.getString(JsonTag.TAGVC2UserType);
		userInfo.UserAction = (String) object
				.getString(JsonTag.TAGNumUserAction);
		userInfo.AreaIdPath = (String) object.getString(JsonTag.TAGAreaIdPath);

		return userInfo;
	}
}
