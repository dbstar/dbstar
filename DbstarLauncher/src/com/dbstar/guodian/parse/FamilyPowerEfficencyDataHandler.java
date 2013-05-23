package com.dbstar.guodian.parse;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.media.JetPlayer;
import android.util.Log;

import com.dbstar.guodian.data.EPCConstitute;
import com.dbstar.guodian.data.JsonTag;
import com.dbstar.guodian.data.PowerPanelData;

public class FamilyPowerEfficencyDataHandler {

	private static final String TAG = "FamilyPowerEfficencyDataHandler";

	public static EPCConstitute parse(String data) {
	    String jsonData = data.substring(1, data.length() - 1);
	    EPCConstitute dimension = new EPCConstitute();
	        JSONObject jsonObject;
	        try {
                jsonObject = new JSONObject(jsonData);
                dimension.totalPower = DataHandler.parsePower(jsonObject.getJSONObject(JsonTag.TAGMonthTotalPowerNumAndFee));
                dimension.electricalItemDetails = parseList(jsonObject.getJSONArray(JsonTag.TAGEquCountAndFeeList)); 
                dimension.serviceSysDate = jsonObject.getString(JsonTag.TAGServiceSysDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
	    return dimension;
		}

	 private static List<EPCConstitute.ElectricalItemDetail> parseList(JSONArray array){
	     List<EPCConstitute.ElectricalItemDetail> list = new ArrayList<EPCConstitute.ElectricalItemDetail>();
	     EPCConstitute.ElectricalItemDetail cfp = null;
	     try {
    	     for(int i = 0;i < array.length();i++){
    	         cfp = new EPCConstitute.ElectricalItemDetail();
    	         JSONObject jb = array.getJSONObject(i);
    	         cfp.Count = jb.getString(JsonTag.TAGNumCount);
    	         cfp.Fee = jb.getString(JsonTag.TAGNumFee);
    	         cfp.CountPercent  = jb.getString(JsonTag.TAGVC2CountPercent);
    	         cfp.ElecGuid = jb.getString(JsonTag.TAGEquGuid);
    	         cfp.ElecName  = jb.getString(JsonTag.TAGVC2Equ_name);
    	         cfp.ElecTypeId = jb.getString(JsonTag.TAGVC2EquTypeId);
    	         cfp.order = jb.getString(JsonTag.TAGNumOrder);
    	         cfp.FeePercent = jb.getString(JsonTag.TAGVC2FeePercent);
    	         
    	         list.add(cfp);
    	     }
	     } catch (Exception e) {
	         e.printStackTrace();
	     }
	     return list;
	 }
}
