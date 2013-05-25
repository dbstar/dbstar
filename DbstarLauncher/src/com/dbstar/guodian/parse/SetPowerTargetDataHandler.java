package com.dbstar.guodian.parse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.dbstar.guodian.data.JsonTag;
import com.dbstar.guodian.data.PowerData;
import com.dbstar.guodian.data.PowerTarget;
import com.dbstar.guodian.data.ResultData;

public class SetPowerTargetDataHandler {
    
    public static PowerData parsePowerDefaultTarget(String data){
        JSONObject jb = null;
        try {
            jb = new JSONObject(data);
            return DataHandler.parsePower(jb);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static PowerTarget parsetPowerTarget(String data){
        JSONObject jb = null;
        try {
            jb = new JSONObject(data);
            return DataHandler.parsePowerTarget(jb);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
        
    }
    
    public static ResultData parseSetPowerTargetResult(String data){
        ResultData resultData = new ResultData();
        JSONTokener jsonParser = new JSONTokener(data);
        
        try {
            JSONArray array = (JSONArray) jsonParser.nextValue();
            JSONObject rootObject = (JSONObject) array.get(0);
            resultData.Result = rootObject.getString(JsonTag.TAGResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultData;
    }
}
