package com.dbstar.guodian.parse;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.dbstar.guodian.data.EqumentData;
import com.dbstar.guodian.data.JsonTag;

public class EqumentDataHandler {
	private static final String TAG = "EqumentDataHandler";

	public static List<EqumentData> parse(String data) {

		Log.d(TAG, "data = " + data);
		List<EqumentData> list =new ArrayList<EqumentData>();
		EqumentData info = null;
		String jsonData = data.substring(1, data.length() - 1);
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(jsonData);
			JSONArray  array = jsonObject.getJSONArray(JsonTag.TAGEqumentList);
			for(int i = 0 ,size = array.length() ;i < size ; i ++){
			    info = parseEqument(array.getJSONObject(i));
			    list.add(info);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}
	
	public static EqumentData parseEqument (JSONObject object){
	    EqumentData data = new EqumentData();
	    try {
            data.EquGuid = object.getString(JsonTag.TAGEquGuid);
            data.EquName = object.getString(JsonTag.TAGVC2Equ_name);
            data.EquTypteId = object.getString(JsonTag.TAGVC2EquTypeId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
	    return data;
	    
	}
}
