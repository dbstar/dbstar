package com.dbstar.guodian.engine1;

import java.util.HashMap;
import java.util.Map;

public class RequestParams {
    
    public static final String KEY_SYSTEM_FLAG = "SystemFlag";
    public static final String KEY_METHODID = "MethodId";
    public static final String KEY_REQUEST_TYPE = "RequestType";
    public static final String KEY_TASK_ID = "TaskId";
    public static final String KEY_TASK_USER_ID = "UserId";
    public static final String KEY_TASK_SPECAIL = "special";
    
    private Map<String, String> data ;
    
    private String SystemFlag;
    
    private String MethodId;
    
    private int RequestType;
    
    private String TaskId;
    
    private String UserId;
    
    private String mSpecial;
    
    public RequestParams(int type) {
        data = new HashMap<String, String>();
        this.RequestType = type;
    }
    
    public RequestParams(){
       this(0); 
    }
    
    public RequestParams put(String key ,String Value){
        
            if(KEY_SYSTEM_FLAG.equals(key)){
                SystemFlag = Value;
            }else if(KEY_METHODID.equals(key)){
                MethodId = Value;
            }else if(KEY_TASK_USER_ID.equals(key)){
                UserId = Value;
            }else if(KEY_TASK_ID.equals(key)){
                TaskId = Value;
            }else if(KEY_TASK_SPECAIL.equals(key)){
                mSpecial = Value;
            }else{
                if(key != null)
                    data.put(key, Value);
            }
        return this;
    }
    public void setRequestType(int type){
        this.RequestType = type;
    }
    public void setParams(Map<String, String> value){
        if(value != null && !value.isEmpty()){
            data.putAll(value);
        }
    }
    public String getSystemFlag(){
        return  SystemFlag;
    }
    
    public String getMethodId(){
        return MethodId;
    }
    
    public int getRequestType(){
        return RequestType;
    }
    
    public String getUserId(){
        return UserId;
    }
    public String getTaskId(){
        return TaskId;
    }
    public Map<String, String> getData(){
        return data;
    }
    
    public String getSpecail(){
        return mSpecial;
    }
}
