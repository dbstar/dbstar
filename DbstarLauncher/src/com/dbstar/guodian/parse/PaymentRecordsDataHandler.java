package com.dbstar.guodian.parse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.media.JetPlayer;
import android.util.Log;

import com.dbstar.guodian.data.EPCConstitute;
import com.dbstar.guodian.data.JsonTag;
import com.dbstar.guodian.data.Notice;
import com.dbstar.guodian.data.PaymentRecord;
import com.dbstar.guodian.data.PaymentRecord.Record;
import com.dbstar.guodian.data.PowerPanelData;
import com.dbstar.util.DateUtil;

public class PaymentRecordsDataHandler {

	private static final String TAG = "PaymentRecordsDataHandler";

    public static PaymentRecord parase(String data){
        PaymentRecord  paymentRecord = new PaymentRecord();
        String jsonData = data.substring(1, data.length() - 1);
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonData);
            paymentRecord.serviceSysDate = jsonObject.getString(JsonTag.TAGServiceSysDate);
            paymentRecord.recentPaymentList = paraseList(jsonObject.getJSONArray(JsonTag.TAGRecentPaymentList));
            for(int i = 0;i<paymentRecord.recentPaymentList.size();i++){
                String date = paymentRecord.recentPaymentList.get(i).date;
                date = DateUtil.getStringFromDateString(date, DateUtil.DateFormat4);
                paymentRecord.recentPaymentList.get(i).date = date;
            }
            paymentRecord.yearPaymentList = paraseList(jsonObject.getJSONArray(JsonTag.TAGYearPaymentList));
            sortList(paymentRecord.yearPaymentList);
            paymentRecord.paymentListYear = new HashMap<String, Map<String,Record>>();
            for(int i = 0; i< paymentRecord.yearPaymentList.size() ; i ++){
                String key = paymentRecord.yearPaymentList.get(i).date.trim();
                paymentRecord.paymentListYear.put(key, new HashMap<String, PaymentRecord.Record>());
            }

            Map<String, Record> yearPaymentDetail = paraseMap(jsonObject.getJSONArray(JsonTag.TAGPaymentListOfYear));
            paymentRecord.paymentListYear.put(paymentRecord.serviceSysDate.substring(0,4), yearPaymentDetail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        return paymentRecord;
    }
    
    public static Map<String, Record> paraseYearDetail(String data){
        String jsonData = data.substring(1, data.length() - 1);
        Map<String, Record> map =new HashMap<String, PaymentRecord.Record>();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonData);
            map = paraseMap(jsonObject.getJSONArray(JsonTag.TAGPaymentListOfYear));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
        }
    public static List<Record> paraseList(JSONArray array){
        List<Record> list = new ArrayList<Record>();
        Record record ;
        JSONObject jsonObject;
        try {
            for(int i = 0;i < array.length(); i++){
             record = new Record();
             jsonObject = array.getJSONObject(i);
             record.date = jsonObject.getString(JsonTag.TAGDate);
             record.fee = Float.valueOf(jsonObject.getString(JsonTag.TAGNumPay).trim());
             list.add(record);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return list;
    }
    
    public static Map<String,Record> paraseMap(JSONArray array){
        Map<String, Record> map = new HashMap<String, PaymentRecord.Record>();
        List<Record> list = paraseList(array);
        String key;
        Record record;
        for(int i = 0; i< list.size();i++){
            record = list.get(i);
            key = DateUtil.getMothFromString(record.date);
           if(!map.containsKey(key)){
               map.put(key, record);
           }else{
             map.get(key).fee  +=record.fee;
           }
        }
        return map;
    }
    private static void sortList(List<Record> list){
        Collections.sort(list, new Comparator<Record>() {

            @Override
            public int compare(Record lhs, Record rhs) {
              return rhs.date.compareTo(lhs.date);
            }
        });
    }
}
