package com.dbstar.guodian.parse;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dbstar.guodian.data.JsonTag;
import com.dbstar.guodian.data.PPCConstitute;

public class PPCConstituteDataHandler {

	private static final String TAG = "PPCConstituteDataHandler";

	public static PPCConstitute parse(String data) {
	    String jsonData = data.substring(1, data.length() - 1);
	    PPCConstitute constitute = new PPCConstitute();
	        JSONObject jsonObject;
	        try {
                jsonObject = new JSONObject(jsonData);
                constitute.serviceSysDate = jsonObject.getString(JsonTag.TAGServiceSysDate);
                constitute.totalPower = DataHandler.parsePower(jsonObject.getJSONObject(JsonTag.TAGTotalPowerNumAndFee));
                constitute.periodItemDetails = parseList(jsonObject.getJSONArray(JsonTag.TAGPeriodItemCountAndFeeList));
            } catch (Exception e) {
                e.printStackTrace();
            }
	    return constitute;
		}

	 private static List<PPCConstitute.PeriodItemDetail> parseList(JSONArray array){
	     List<PPCConstitute.PeriodItemDetail> list = new ArrayList<PPCConstitute.PeriodItemDetail>();
	     PPCConstitute.PeriodItemDetail item = null;
	     try {
    	     for(int i = 0;i < array.length();i++){
    	         item = new PPCConstitute.PeriodItemDetail();
    	         JSONObject jb = array.getJSONObject(i);
    	         item.periodName  = jb.getString(JsonTag.TAGVC2PeriodName);
    	         item.Count = jb.getString(JsonTag.TAGNumCount);
    	         item.Fee = jb.getString(JsonTag.TAGNumFee);
    	         item.CountPercent  = jb.getString(JsonTag.TAGVC2CountPercent);
    	         item.FeePercent = jb.getString(JsonTag.TAGVC2FeePercent);
    	         
    	         list.add(item);
    	     }
	     } catch (Exception e) {
	         e.printStackTrace();
	     }
	     return list;
	 }
}
