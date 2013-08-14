package com.dbstar.guodian.engine1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONStringer;

import com.dbstar.util.LogUtil;
import com.smartlife.mobile.service.FormatCMD;

public class CmdHelper {
    
    private static final String TAG = "CmdHelper";
    private static final String CmdDelimiterTag = "#";
    private static final String CmdStartTag = "#!";
    private static final String CmdEndTag = "!#";
    private static final String DeviceVersion = "v3.3.5";
    private static final String DeviceId = "epg_htcm";
    
    
    
    public static String constructCmd(RequestParams params,String userId,String taskId){
        StringBuilder builder = new StringBuilder();
        
        builder.append(taskId)
            .append(CmdDelimiterTag)
            .append(params.getSystemFlag())
            .append(CmdDelimiterTag)
            .append(params.getMethodId())
            .append(CmdDelimiterTag)
            .append(userId)
            .append(CmdDelimiterTag)
            .append(DeviceVersion)
            .append(CmdDelimiterTag)
            .append(DeviceId)
            .append(CmdDelimiterTag);
        
            if(params.getSpecail() != null && !params.getSpecail().isEmpty()){
                builder.append(params.getSpecail());
            }else{
                builder.append(toJson(params.getData()));
            }
            
        
        
        LogUtil.i(TAG, builder.toString());
        String encryptStr = FormatCMD.encryptCMD(builder.toString());
        builder.delete(0, builder.length());
        builder.append(CmdStartTag).append(encryptStr).append( CmdEndTag).append("\n");
        
        
        return builder.toString();
        }
    public static String[] processResponse(String response) {
        String data = response;
        
        
        String cmd = isValideCommand(data);
        if(cmd == null)
            return null;
        String decryptedStr = FormatCMD.decryptCMD(cmd);
        
        LogUtil.i(TAG, "decrypt data = " + decryptedStr);
        
        
        try {
            int i1 = decryptedStr.indexOf("[");
            int i2 = decryptedStr.indexOf("{");
            
            int jsonStartIndex = 0;
            
            if(i1 != -1 && i1 < i2){
                jsonStartIndex = i1;
            }else {
                jsonStartIndex = i2;
            }
                
            if(jsonStartIndex == -1){
               return decryptedStr.split(CmdDelimiterTag);
            }
            
            int jsonEndIndex = decryptedStr.length();

            String jsonStr = decryptedStr.substring(jsonStartIndex,
                    jsonEndIndex);
            if (jsonStr.contains(CmdDelimiterTag)) {
                decryptedStr = decryptedStr.substring(0, jsonStartIndex);
                List<String> list = Arrays.asList(decryptedStr
                        .split(CmdDelimiterTag));
                ArrayList<String> arrayList = new ArrayList<String>(list);
                arrayList.add(jsonStr);
                return arrayList.toArray(new String[arrayList.size()]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return decryptedStr.split(CmdDelimiterTag);
    }
    private static String isValideCommand(String cmd) {
        int startTagLen = cmd.indexOf(CmdStartTag);
        int endTagLen = cmd.lastIndexOf(CmdEndTag);
        if(startTagLen != -1 && endTagLen != -1){
            return cmd.substring(CmdEndTag.length(), (endTagLen +1));
        }
        LogUtil.i(TAG, "respose cmd is invalid ");
        return null;
    }
    private static  String toJson(Map<String, String> data) {
        String jsonStr = null;
        try {
            JSONStringer jsonStringer = new JSONStringer();
            jsonStringer.object();
            if(data != null){
                Iterator<java.util.Map.Entry<String, String>>  iterator = data.entrySet().iterator();
                Map.Entry<String,String> entry;
                while(iterator.hasNext()){
                    entry = iterator.next();
                    jsonStringer.key(entry.getKey()).value(entry.getValue());
                }
            }
            jsonStringer.endObject();
            jsonStr = jsonStringer.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonStr;
    }
}
