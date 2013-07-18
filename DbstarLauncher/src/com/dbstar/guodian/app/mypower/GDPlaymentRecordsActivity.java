package com.dbstar.guodian.app.mypower;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dbstar.R;
import com.dbstar.guodian.app.base.GDSmartActivity;
import com.dbstar.guodian.data.JsonTag;
import com.dbstar.guodian.data.PaymentRecord;
import com.dbstar.guodian.data.PaymentRecord.Record;
import com.dbstar.guodian.engine1.GDRequestType;
import com.dbstar.guodian.engine1.RequestParams;
import com.dbstar.model.EventData;
import com.dbstar.widget.PaymentRecordCurve;

public class GDPlaymentRecordsActivity extends GDSmartActivity{
    
    private Button mButtonYearLevel1;
    private Button mButtonYearLevel2;
    private Button mButtonYearLevel3;
    
    private TextView mCurYearFee;
    private TextView mRecentFirstDate;
    private TextView mRecentFirstFee;
    private TextView mRecentSecondDate;
    private TextView mRecentSecondFee;
    
    private LinearLayout mPolyLineView;
    private View mContent;
    private String CCGUID;
    private String mNum;
    private String mNumYear;
    private PaymentRecord mPaymentRecord;
    private String mCurrentYear;
    private PaymentRecordCurve curve;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypower_payment_records_view);
        Intent intent = getIntent();
        mMenuPath = intent.getStringExtra(INTENT_KEY_MENUPATH);
        mNum = "2";
        mNumYear = "3";
        initializeView();

        if (mMenuPath != null) {
            String[] menuArray = mMenuPath.split(MENU_STRING_DELIMITER);
            showMenuPath(menuArray);
        }
    }
    
    private String getCurrentYear(long timeMillis) {
        Calendar calendar =  Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        mNumYear = String.valueOf(calendar.get(Calendar.YEAR));
        return null;
    }

    @Override
    protected void initializeView() {
        super.initializeView();
        
        mContent = findViewById(R.id.content);
        mContent.setVisibility(View.INVISIBLE);
        mButtonYearLevel1 = (Button) findViewById(R.id.p_r_year_level1);
        mButtonYearLevel2 = (Button) findViewById(R.id.p_r_year_level2);
        mButtonYearLevel3 = (Button) findViewById(R.id.p_r_year_level3);
        
        mButtonYearLevel1.setOnClickListener(mClickListener);
        mButtonYearLevel2.setOnClickListener(mClickListener);
        mButtonYearLevel3.setOnClickListener(mClickListener);
        
        mButtonYearLevel1.setVisibility(View.INVISIBLE);
        mButtonYearLevel2.setVisibility(View.INVISIBLE);
        mButtonYearLevel3.setVisibility(View.INVISIBLE);
        
        mCurYearFee = (TextView) findViewById(R.id.p_r_current_year_payment);
        mRecentFirstDate = (TextView) findViewById(R.id.p_r_recent_first_date);
        mRecentFirstFee = (TextView) findViewById(R.id.p_r_recent_first_fee);
        mRecentSecondDate = (TextView) findViewById(R.id.p_r_recent_second_date);
        mRecentSecondFee = (TextView) findViewById(R.id.p_r_recent_second_fee);
        
        mPolyLineView = (LinearLayout) findViewById(R.id.polylinraView);
        
    }
    
    protected void onServiceStart() {
        super.onServiceStart();
        if(getCtrlNo() != null){
            CCGUID = getCtrlNo().CtrlNoGuid;
        }
        requstDataFromService();
    };
    
   
    @Override
    public void notifyEvent(int type, Object event) {
        super.notifyEvent(type, event);
        if(type == EventData.EVENT_GUODIAN_DATA){
            EventData.GuodianEvent guodianEvent = (EventData.GuodianEvent) event;
            if(GDRequestType.DATATYPE_PAYMENT_RECORDS == guodianEvent.Type){
                mPaymentRecord = (PaymentRecord) guodianEvent.Data;
                handPaymentRecordData(mPaymentRecord);
            }else if(GDRequestType.DATATYPE_YREAR_FEE_DETAIL == guodianEvent.Type){
                showPolyLineView(mPaymentRecord);
                Map<String, Record> yearDetail = (Map<String, Record>) guodianEvent.Data;
                if(yearDetail != null && !yearDetail.isEmpty()){
                   Iterator<Record> iterator = yearDetail.values().iterator();
                   if(iterator.hasNext()){
                       Record  r = iterator.next();
                       mPaymentRecord.paymentListYear.put(r.date.substring(0,4), yearDetail);
                       if(r.date.substring(0,4).equals(mCurrentYear)){
                           showPolyLineView(mPaymentRecord);
                       }
                   }
                   
                }
            }
        }else if(EventData.EVENT_GUODIAN_DATA_ERROR == type){
           showErrorMsg(R.string.loading_error);
           return;
        }
    }
    
    private void handPaymentRecordData(PaymentRecord paymentRecord){
        if(paymentRecord == null){
            return;
        }
        
        mCurrentYear = paymentRecord.serviceSysDate.substring(0,4);
        List<Record> yearPaymentList = paymentRecord.yearPaymentList;
        if(yearPaymentList == null || yearPaymentList.isEmpty())
            return;
        mContent.setVisibility(View.VISIBLE);
        mButtonYearLevel1.setVisibility(View.VISIBLE);
        mButtonYearLevel2.setVisibility(View.VISIBLE);
        mButtonYearLevel3.setVisibility(View.VISIBLE);
        mButtonYearLevel1.setText(yearPaymentList.get(0).date);
        mButtonYearLevel2.setText(yearPaymentList.get(1).date);
        mButtonYearLevel3.setText(yearPaymentList.get(2).date);
        
        mCurYearFee.setText(getCurrentPayment());
        
        List<Record> recentPaymentList = paymentRecord.recentPaymentList;
        if(recentPaymentList != null && !recentPaymentList.isEmpty()){
            mRecentFirstDate.setText(recentPaymentList.get(0).date);
            mRecentFirstFee.setText(recentPaymentList.get(0).fee + getString(R.string.string_yuan));
            
            mRecentSecondDate.setText(recentPaymentList.get(1).date);
            mRecentSecondFee.setText(recentPaymentList.get(1).fee  + getString(R.string.string_yuan));
        }
        
        showPolyLineView(paymentRecord);
    }
    
    private void showPolyLineView(PaymentRecord paymentRecord){
        Map<String, Map<String,Record>> allYears = paymentRecord.paymentListYear;
        Map<String, Record> currentYears  = allYears.get(mCurrentYear);
        ArrayList<Float> ydata = new ArrayList<Float>();
        ArrayList<Integer>xData = new ArrayList<Integer>();
        for(int i = 1;i<=12 ;i++){
            String key = String.valueOf(i);
            if(currentYears.containsKey(key)){
                ydata.add(currentYears.get(key).fee);
            }else{
                ydata.add(null);
            }
            
            xData.add(i);
        }
        curve = new PaymentRecordCurve(this);
        curve.setFrame(mPolyLineView.getMeasuredWidth(),mPolyLineView.getMeasuredHeight(), 50, 50, 100, 50);
        curve.setData(ydata, null, xData);
        mPolyLineView.removeAllViews();
        mPolyLineView.addView(curve);
        
        
    }
    private String getCurrentPayment(){
        List<Record> list = mPaymentRecord.yearPaymentList;
        StringBuilder sb = new StringBuilder();
        for(Record record : list){
            if(record.date.equals(mCurrentYear)){
              sb.append(record.date)
              .append(getString(R.string.string_year_payment_all))
              .append(record.fee)
              .append(getString(R.string.string_yuan));
            }
        }
        return sb.toString();
    }
    private void requstDataFromService(){
        if(CCGUID == null){
            showErrorMsg(R.string.no_login);
            return;
        }
        mSystemFlag = "elc";
        mRequestMethodId = "m008f003";
        RequestParams params = new RequestParams(GDRequestType.DATATYPE_PAYMENT_RECORDS);
        params.put(RequestParams.KEY_SYSTEM_FLAG, mSystemFlag);
        params.put(RequestParams.KEY_METHODID, mRequestMethodId);
        params.put(JsonTag.TAGNumCCGuid, CCGUID);
        params.put(JsonTag.TAGNum, mNum);
        params.put(JsonTag.TAGNum_Years, mNumYear);
        requestData(params);
    }
    
    private void requestYearPlayment(String date){
        if(CCGUID == null){
            showErrorMsg(R.string.no_login);
            return;
        }
        mSystemFlag = "elc";
        mRequestMethodId = "m005f003";
        RequestParams params = new RequestParams(GDRequestType.DATATYPE_YREAR_FEE_DETAIL);
        params.put(RequestParams.KEY_SYSTEM_FLAG, mSystemFlag);
        params.put(RequestParams.KEY_METHODID, mRequestMethodId);
        params.put(JsonTag.TAGNumCCGuid, CCGUID);
        params.put(JsonTag.TAGDate, date+"-01-01 00:00:00");
        requestData(params);
    }
    OnClickListener mClickListener = new OnClickListener() {
        
        @Override
        public void onClick(View v) {
            if(mPaymentRecord == null)
                return;
            int id = v.getId();
            String strYear = null;
            switch (id) {
            case R.id.p_r_year_level1:
                strYear = mPaymentRecord.yearPaymentList.get(0).date;
                break;
              
            case R.id.p_r_year_level2:
                strYear = mPaymentRecord.yearPaymentList.get(1).date;
                break;
                
            case R.id.p_r_year_level3:
                strYear = mPaymentRecord.yearPaymentList.get(2).date;
                break;
                
      
            }
            if(strYear == null)
                return;
            mCurrentYear = strYear;
            Map<String, Record> yearPayment = mPaymentRecord.paymentListYear.get(strYear);
            if(yearPayment== null || yearPayment.isEmpty()){
                if(curve != null)
                    curve.clearData();
                requestYearPlayment(strYear);
            }else{
                showPolyLineView(mPaymentRecord);
            }
            mCurYearFee.setText(getCurrentPayment());
        }
    };
    
    
    
}
