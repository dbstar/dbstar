package com.dbstar.guodian.parse;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dbstar.guodian.data.JsonTag;
import com.dbstar.guodian.data.SPCConstitute;

public class SPCConstituteDataHandler {

	private static final String TAG = "SPCConstituteDataHandler";

	public static SPCConstitute parse(String data) {
	    String jsonData = data.substring(1, data.length() - 1);
	    SPCConstitute constitute = new SPCConstitute();
	        JSONObject jsonObject;
	        try {
                jsonObject = new JSONObject(jsonData);
                constitute.serviceSysDate = jsonObject.getString(JsonTag.TAGServiceSysDate);
                constitute.totalPower = DataHandler.parsePower(jsonObject.getJSONObject(JsonTag.TAGTotalPowerNumAndFee));
                constitute.stepItemDetails = parseList(jsonObject.getJSONArray(JsonTag.TAGStepItemCountAndFeeList));
            } catch (Exception e) {
                e.printStackTrace();
            }
	    return constitute;
		}

	 private static List<SPCConstitute.StepItemDetail> parseList(JSONArray array){
	     List<SPCConstitute.StepItemDetail> list = new ArrayList<SPCConstitute.StepItemDetail>();
	     SPCConstitute.StepItemDetail item = null;
	     try {
    	     for(int i = 0;i < array.length();i++){
    	         item = new SPCConstitute.StepItemDetail();
    	         JSONObject jb = array.getJSONObject(i);
    	         item.stepName  = jb.getString(JsonTag.TAGVC2STEPName);
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
