package com.dbstar.guodian.parse;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dbstar.guodian.data.JsonTag;
import com.dbstar.guodian.data.PowerConsumptionTrend;
import com.dbstar.guodian.data.PowerConsumptionTrend.ConsumptionPercent;

public class PowerConsumptionTrendDataHandler {

	private static final String TAG = "PowerConsumptionTrendDataHandler";

	public static PowerConsumptionTrend parse(String data) {
	    String jsonData = data.substring(1, data.length() - 1);
	    PowerConsumptionTrend trend = new PowerConsumptionTrend();
	        JSONObject jsonObject;
	        try {
                jsonObject = new JSONObject(jsonData);
                trend.HuanBiList = parseList(jsonObject.getJSONArray(JsonTag.TAGPeriodOfLastYearList));
                trend.TongBiList = parseList(jsonObject.getJSONArray(JsonTag.TAGComOfSamePeriodList));
            } catch (Exception e) {
                e.printStackTrace();
            }
	    return trend;
		}

	 private static List<ConsumptionPercent> parseList(JSONArray array){
	     List<ConsumptionPercent> list = new ArrayList<ConsumptionPercent>();
	     ConsumptionPercent item = null;
	     try {
    	     for(int i = 0;i < array.length();i++){
    	         item = new ConsumptionPercent();
    	         JSONObject jb = array.getJSONObject(i);
    	         item.DateTime  = jb.getString(JsonTag.TAGDate_Time);
    	         item.CountPercent = jb.getString(JsonTag.TAGNumCountVal);
    	         item.FeePercent = jb.getString(JsonTag.TAGNumFeeVal);
    	         list.add(item);
    	     }
	     } catch (Exception e) {
	         e.printStackTrace();
	     }
	     return list;
	 }
}
