package com.dbstar.guodian.parse;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.dbstar.guodian.data.AddTimedTaskResponse;
import com.dbstar.guodian.data.ElectricalOperationMode;
import com.dbstar.guodian.data.ElectricalOperationMode.ModeElectrical;
import com.dbstar.guodian.data.JsonTag;
import com.dbstar.guodian.data.ResultData;
import com.dbstar.guodian.data.RoomData;
import com.dbstar.guodian.data.RoomData.ElecRefreshResponse;
import com.dbstar.guodian.data.RoomData.ElecTurnResponse;
import com.dbstar.guodian.data.RoomData.RoomElectrical;
import com.dbstar.guodian.data.TimedTask;

public class SmartHomeDataHandler {
	private static final String TAG = "SmartHomeDataHandler";
	
	
	public static ArrayList<RoomData> parseRooms(String data){
	    ArrayList<RoomData> rooms = new ArrayList<RoomData>();
	    JSONTokener jsonParser = new JSONTokener(data);
	    try {
            JSONArray array = (JSONArray) jsonParser.nextValue();

            JSONObject rootObject = (JSONObject) array.get(0);
            JSONArray roomArray = rootObject.getJSONArray(JsonTag.TAGRoom_list);
            
            JSONObject jb  = null;
            RoomData roomData = null;
            for(int i = 0,size = roomArray.length();i<size ;i ++){
                jb = roomArray.getJSONObject(i);
                roomData = new RoomData();
                roomData.RoomGuid = jb.getString(JsonTag.TAGRoomGuid);
                roomData.RoomName = jb.getString(JsonTag.TAGRoomName);
                rooms.add(roomData);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
	    return rooms;
	    
	}
	
	public static ArrayList<RoomElectrical> parseRoomElectrical(String data){
	    ArrayList<RoomElectrical> eletricals = new ArrayList<RoomData.RoomElectrical>();
	    
	    JSONTokener jsonParser = new JSONTokener(data);
        try {
            JSONArray array = (JSONArray) jsonParser.nextValue();

            JSONObject rootObject = (JSONObject) array.get(0);
            JSONArray eleArray = rootObject.getJSONArray(JsonTag.TAGRoomEleList);
            
            JSONObject jb  = null;
            RoomElectrical ele = null;
            
            for(int i = 0 ,size = eleArray.length();i< size ;i ++){
                jb = eleArray.getJSONObject(i);
                ele = new RoomElectrical();
                ele.AdapterFlag = jb.getString(JsonTag.TAGAdapterFlag);
                ele.AdapterSeridNo  = jb.getString(JsonTag.TAGAdapterSeridNo);
                ele.CompanyName = jb.getString(JsonTag.TAGCompanyName);
                ele.ComTypeModelGuid = jb.getString(JsonTag.TAGComTypeModelGuid);
                ele.DeviceComCode = jb.getString(JsonTag.TAGDeviceComCode);
                ele.DeviceGuid = jb.getString(JsonTag.TAGDeviceGuid);
                ele.DeviceModelCode = jb.getString(JsonTag.TAGDeviceModelCode);
                ele.DeviceName = jb.getString(JsonTag.TAGDeviceName);
                ele.DevicePic = jb.getString(JsonTag.TAGDevicePic);
                ele.DeviceTypeCode = jb.getString(JsonTag.TAGDeviceTypeCode);
                ele.DeviceTypeName = jb.getString(JsonTag.TAGDeviceTypeName);
                ele.EleAmountOfDay = jb.getString(JsonTag.TAGEleAmountOfDay);
                ele.EleAmountOfMonth = jb.getString(JsonTag.TAGEleAmountOfMonth);
                ele.EleDeviceCode = jb.getString(JsonTag.TAGEleDeviceCode);
                ele.OpenStandByRemind = jb.getString(JsonTag.TAGOpenStandByRemind);
                ele.RealTimePower = jb.getString(JsonTag.TAGRealTimePower);
                ele.RoomGuid = jb.getString(JsonTag.TAGRoomGuid);
                ele.StandByPowerValue = jb.getString(JsonTag.TAGStandByPowerValue);
                
                eletricals.add(ele);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
	    
	    return eletricals;
	}
	
	public static ElecTurnResponse parseEleTurnResponse(String data){
	    ElecTurnResponse response = new ElecTurnResponse();
	    
	    try {
	        JSONObject jb = new JSONObject(data);
	        response.Exception = jb.getString(JsonTag.TAGException);
	        response.Reason = jb.getString(JsonTag.TAGReason);
	        response.Result = jb.getString(JsonTag.TAGResult);
	        response.RealTimePowerValue = jb.getString(JsonTag.TAGRealTimePowerValue);
        }catch (Exception e) {
            e.printStackTrace();
        }
	    return response;
	}
	
	public static ElecRefreshResponse parseEleRefreshResponse(String data){
	    
	    ElecRefreshResponse response = new ElecRefreshResponse();
        try {
            JSONObject jb = new JSONObject(data);
            response.EleAmountOfDay = jb.getString(JsonTag.TAGEleAmountOfDay);
            response.EleAmountOfDay = jb.getString(JsonTag.TAGEleAmountOfMonth);
            response.RealTimePowerValue = jb.getString(JsonTag.TAGRealTimePowerValue);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return response;
	}
	
	public static List<ElectricalOperationMode> parseEleOperModel(String data){
	    ArrayList<ElectricalOperationMode> models = new ArrayList<ElectricalOperationMode>();
        JSONTokener jsonParser = new JSONTokener(data);
        try {
            JSONArray array = (JSONArray) jsonParser.nextValue();

            JSONObject rootObject = (JSONObject) array.get(0);
            JSONArray modelArray = rootObject.getJSONArray(JsonTag.TAGModelist);
            
            JSONObject jb  = null;
            ElectricalOperationMode mode = null;
            for(int i = 0,size = modelArray.length();i<size ;i ++){
                jb = modelArray.getJSONObject(i);
                mode = new ElectricalOperationMode();
                mode.ModelGuid = jb.getString(JsonTag.TAGModeGuid);
                mode.ModelId = jb.getString(JsonTag.TAGModeId);
                mode.ModelName = jb.getString(JsonTag.TAGModeName);
                mode.ModelPicId = jb.getString(JsonTag.TAGModePicId);
                models.add(mode);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return models;
	}
	
	public static List<ModeElectrical> parseModeElectrical (String data){
	    ArrayList<ModeElectrical> electricals = new ArrayList<ModeElectrical>();
	    JSONTokener jsonParser = new JSONTokener(data);
	    
	    try {
            JSONArray array = (JSONArray) jsonParser.nextValue();
            JSONObject rootObject = (JSONObject) array.get(0);
            JSONArray eleArray = rootObject.getJSONArray(JsonTag.TAGModeEleList);
            
            ModeElectrical ele = null;
            JSONObject jb = null;
            for(int i  = 0,size = eleArray.length() ; i< size ; i++ ){
                jb = eleArray.getJSONObject(i);
                ele = new ModeElectrical();
                
                ele.DeviceGuid = jb.getString(JsonTag.TAGDeviceGuid);
                ele.DeviceName = jb.getString(JsonTag.TAGDeviceName);
                ele.typeId = jb.getString(JsonTag.TAGTypeID);
                ele.Oper = jb.getString(JsonTag.TAGOper);
                electricals.add(ele);
                
            }
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
	    
	    return electricals;
	    
	}
	
	   public static ResultData parseExecuteModeResult(String data){
	        ResultData result = new ResultData();
	        
	        try {
	            JSONObject jb = new JSONObject(data);
	            result.Exception = jb.getString(JsonTag.TAGException);
	            result.Reason = jb.getString(JsonTag.TAGReason);
	            result.Result = jb.getString(JsonTag.TAGResult);
	        }catch (Exception e) {
	            e.printStackTrace();
	        }
	        return result;
	    }
	   
	   public static List<TimedTask> parseTimedTaskList(String data){
	       ArrayList<TimedTask> tasks = new ArrayList<TimedTask>();
	        JSONTokener jsonParser = new JSONTokener(data);
	        try {
	            JSONArray array = (JSONArray) jsonParser.nextValue();

	            JSONObject rootObject = (JSONObject) array.get(0);
	            JSONArray roomArray = rootObject.getJSONArray(JsonTag.TAGTimetasklist);
	            
	            JSONObject jb  = null;
	            TimedTask task = null;
	            for(int i = 0,size = roomArray.length();i<size ;i ++){
	                jb = roomArray.getJSONObject(i);
	                task = new TimedTask();
	                task.DeviceGuid = jb.getString(JsonTag.TAGDeviceGuid);
	                task.DeviceName = jb.getString(JsonTag.TAGDeviceName);
	                task.Frequency = jb.getString(JsonTag.TAGFrequency);
	                task.Oper = jb.getString(JsonTag.TAGOper);
	                task.State = jb.getString(JsonTag.TAGState);
	                task.Time = jb.getString(JsonTag.TAGTime);
	                task.TimedTaskGuid = jb.getString(JsonTag.TAGTimeTaskGuid);
	                task.TypeId = jb.getString(JsonTag.TAGTypeID);
	                tasks.add(task);
	            }

	        } catch (JSONException e) {
	            e.printStackTrace();
	        }
	        return tasks;
	       
	   }
	   public static List<String> parseNoTimedTaskElectricalList(String data){
           ArrayList<String> tasks = new ArrayList<String>();
            JSONTokener jsonParser = new JSONTokener(data);
            try {
                JSONArray array = (JSONArray) jsonParser.nextValue();

                JSONObject rootObject = (JSONObject) array.get(0);
                JSONArray roomArray = rootObject.getJSONArray(JsonTag.TAGNoTaskEleList);
                
                JSONObject jb  = null;
                for(int i = 0,size = roomArray.length();i<size ;i ++){
                    jb = roomArray.getJSONObject(i);
                    tasks.add(jb.getString(JsonTag.TAGDeviceGuid));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return tasks;
           
       }
	   public static AddTimedTaskResponse parseAddTimedTaskResponse(String data){
	        AddTimedTaskResponse response = new AddTimedTaskResponse();
	        
	        try {
	            JSONObject jb = new JSONObject(data);
	            response.Exception = jb.getString(JsonTag.TAGException);
	            response.Reason = jb.getString(JsonTag.TAGReason);
	            response.Result = jb.getString(JsonTag.TAGResult);
	            response.TimedTaskGuid = jb.getString(JsonTag.TAGTimeTaskGuid);
	        }catch (Exception e) {
	            e.printStackTrace();
	        }
	        return response;
	    }
	   
	  public static ResultData parseModifyTimedTaskResponse(String data){
	      return parseExecuteModeResult(data);
	  }
	  public static ResultData parseDeleteTimedTaskResponse(String data){
          return parseExecuteModeResult(data);
      }
	  public static ResultData parseExecuteTimedTaskResponse(String data){
          return parseExecuteModeResult(data);
      }
}
