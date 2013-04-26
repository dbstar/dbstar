package com.dbstar.guodian.parse;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.media.JetPlayer;
import android.util.Log;

import com.dbstar.guodian.data.ElectriDimension;
import com.dbstar.guodian.data.JsonTag;
import com.dbstar.guodian.data.PowerPanelData;

public class ElecDimensionDataHandler {

	private static final String TAG = "ElecDimensionDataHandler";

	public static ElectriDimension parse(String data) {
	    String jsonData = data.substring(1, data.length() - 1);
	    ElectriDimension dimension = new ElectriDimension();
	        JSONObject jsonObject;
	        try {
                jsonObject = new JSONObject(jsonData);
                dimension.totalPower = DataHandler.parsePower(jsonObject.getJSONObject(JsonTag.TAGTotalPowerNumAndFee));
                dimension.AllCountFeePercent = parseList(jsonObject.getJSONArray(JsonTag.TAGEquCountAndFeeList)); 
            } catch (Exception e) {
                e.printStackTrace();
            }
	    return dimension;
		}

	 private static List<ElectriDimension.CountAndFeePercent> parseList(JSONArray array){
	     List<ElectriDimension.CountAndFeePercent> list = new ArrayList<ElectriDimension.CountAndFeePercent>();
	     ElectriDimension.CountAndFeePercent cfp = null;
	     try {
    	     for(int i = 0;i < array.length();i++){
    	         cfp = new ElectriDimension.CountAndFeePercent();
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
