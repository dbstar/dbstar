package com.dbstar.guodian.parse;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dbstar.guodian.data.JsonTag;
import com.dbstar.guodian.data.PPCConstitute;
import com.dbstar.guodian.data.StepPowerConsumptionTrack;
import com.dbstar.guodian.data.StepPowerConsumptionTrack.DateStepPower;
import com.dbstar.guodian.data.StepPowerConsumptionTrack.StepPower;

public class StepPowerConsumptionTrackDataHandler {

	private static final String TAG = "StepPowerConsumptionTrackDataHandler";

	public static StepPowerConsumptionTrack parse(String data) {
	    String jsonData = data.substring(1, data.length() - 1);
	    StepPowerConsumptionTrack track = new StepPowerConsumptionTrack();
	    PPCConstitute constitute = new PPCConstitute();
	        JSONObject jsonObject;
	        try {
                jsonObject = new JSONObject(jsonData);
                track.serviceSysDate = jsonObject.getString(JsonTag.TAGServiceSysDate);
                track.totalConsumption = DataHandler.parsePower(jsonObject.getJSONObject(JsonTag.TAGPowerNumFeeTotal));
                track.dateStepPowerList = parseDateStepPower(jsonObject.getJSONArray(JsonTag.TAGDateStepPowerList));
            } catch (Exception e) {
                e.printStackTrace();
            }
	    return track;
		}

	 private static List<DateStepPower> parseDateStepPower(JSONArray array){
	    List<DateStepPower> list = new ArrayList<DateStepPower>();
	    DateStepPower item = null;
	    JSONObject jb ;
	     try {
	         for(int i = 0,size = array.length(); i<size; i++){
	             item =new DateStepPower();
	             jb = array.getJSONObject(i);
	             item.dateTime = jb.getString(JsonTag.TAGDate_Time);
	             item.stepPowerList = parseStepPower(item,jb.getJSONArray(JsonTag.TAGPowerList));
	             list.add(item);
	         }
        } catch (Exception e) {
            e.printStackTrace();
        }
	     
	     return list;
	 }
	 
	 private static List<StepPower> parseStepPower(DateStepPower stepPower ,JSONArray array){
	     List<StepPower> list =new ArrayList<StepPower>();
	     StepPower item = null;
	     float allCount = 0;
	        JSONObject jb ;
	         try {
	             for(int i = 0,size = array.length(); i<size; i++){
	                 item =new StepPower();
	                 jb = array.getJSONObject(i);
	                 item.dateTime = jb.getString(JsonTag.TAGDate_Time);
	                 item.stepName = jb.getString(JsonTag.TAGVC2STEPName);
	                 item.count = jb.getString(JsonTag.TAGNumPowerCount);
	                 item.fee = jb.getString(JsonTag.TAGNumPowerFee);
	                 allCount = allCount + Float.parseFloat(item.count.trim());
	                 list.add(item);
	             }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	     
	     stepPower.allCount = allCount;
	     return list;
	 }
}
