package com.dbstar.guodian.app.familyefficency;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.Spinner;

import com.dbstar.R;
import com.dbstar.app.GDBaseActivity;
import com.dbstar.guodian.data.EPCConstitute;
import com.dbstar.guodian.data.ElectricityPrice;
import com.dbstar.guodian.data.JsonTag;
import com.dbstar.guodian.data.LoginData;
import com.dbstar.guodian.data.PPCConstitute;
import com.dbstar.guodian.data.SPCConstitute;
import com.dbstar.guodian.engine.GDConstract;
import com.dbstar.model.EventData;
import com.dbstar.util.DateUtil;

public class GDPowerConstitueActivity extends GDBaseActivity{
    
    private static final String EPCC = "electricalPowerConsuptionConstitute";
    private static final String SPCC = "stepPowerConsuptionConstitute";
    private static final String PPCC = "peroidPowerConsuptionConstitute";
    private static final String DATEMONTH = "month";
    private static final String DATEYEAR = "year";
    private static final String DATEDAY = "day";
    private Button mElecical;
    private Button mStep;
    private Button mTiming;
    private Button mQuery;
    private Spinner mSpinnerYear;
    private Spinner mSpinnerMonth;
    private Spinner mSpinnerDay;
    private ArrayList<String> mYearList;
    private ArrayList<String> mMonthList;
    private ArrayList<String> mDayList;
    private ArrayAdapter<String> mYearAdapter;
    private ArrayAdapter<String> mMonthAdapter;
    private ArrayAdapter<String> mDayAdapter;
    private String mCurrentYear;
    private String mCurrentMonth;
    private String mCurrentDateTtype;
    private String mCurrentPPC;
    private EPCConstitute mEPCConstitute;
    private SPCConstitute mSPCConstitute;
    private PPCConstitute mPPConstitute;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.family_power_efficency_power_constitute);
        Intent intent = getIntent();
        mMenuPath = intent.getStringExtra(INTENT_KEY_MENUPATH);
        initializeView();

        if (mMenuPath != null) {
            String[] menuArray = mMenuPath.split(MENU_STRING_DELIMITER);
            showMenuPath(menuArray);
        }
    }
    
    @Override
    protected void initializeView() {
        super.initializeView();
        
        mCurrentPPC = EPCC;
        
        mElecical = (Button) findViewById(R.id.power_constitute_electrical);
        mStep = (Button) findViewById(R.id.power_constitute_step);
        mTiming = (Button) findViewById(R.id.power_constitute_timing);
        mQuery = (Button) findViewById(R.id.power_constitue_query_button);
        mSpinnerYear = (Spinner) findViewById(R.id.year_spinner);
        mSpinnerMonth = (Spinner) findViewById(R.id.month_spinner);
        mSpinnerDay = (Spinner) findViewById(R.id.day_spinner);
        
        mElecical.setOnFocusChangeListener(mFocusChangeListener);
        mStep.setOnFocusChangeListener(mFocusChangeListener);
        mTiming.setOnFocusChangeListener(mFocusChangeListener);
        
        mQuery.setOnClickListener(mClickListener);
        
        mSpinnerMonth.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                if(position !=0)
                    updateDaySpiner(mMonthList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                
            }
        });
        
    }
    protected void updateDaySpiner(String string) {
        Calendar cal = Calendar.getInstance();
        int selectYear = Integer.parseInt(mYearList.get(mSpinnerYear.getSelectedItemPosition()));
        cal.set(Calendar.YEAR,selectYear);
        cal.set(Calendar.MONTH, Integer.parseInt(string) -1);//
        int dateOfMonth = cal.getActualMaximum(Calendar.DATE);
        String dayth = mDayList.get(0);
        mDayList.clear();
        mDayList.add(dayth);
        for(int i = 1;i <=dateOfMonth ;i++){
            mDayList.add(String.valueOf(i));
        }
        mDayAdapter.notifyDataSetChanged();
        mSpinnerDay.setSelection(0);
    }

    private void initializeData(String dateStr) {

//      Date date = DateUtil.getDateFromStr(dateStr, DateUtil.DateFormat1);
//      
        
        if (dateStr == null || dateStr.isEmpty()) {
            return;
        }

        String[] temp = dateStr.split(" ");
        String date = temp[0];
        String[] dateTime = date.split("-");

        mCurrentYear = dateTime[0];
        mCurrentMonth = dateTime[1];
        
        int year = Integer.valueOf(mCurrentYear);
        int month = Integer.valueOf(mCurrentMonth);

        mYearList = new ArrayList<String>();
        for (int i = year; i > year - 10; i--) {
            mYearList.add(String.valueOf(i));
        }

        mMonthList = new ArrayList<String>();
        String emptyDate = getResources().getString(R.string.str_month);
        mMonthList.add(emptyDate);
        for (int i = 1; i < 13; i++) {
            mMonthList.add(String.valueOf(i));
        }
        mDayList =new ArrayList<String>();
        String emptyDay = getResources().getString(R.string.str_day);
        mDayList.add(emptyDay);
        
        
        mYearAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item,
                mYearList);
        mSpinnerYear.setAdapter(mYearAdapter);
        mSpinnerYear.setSelection(0);

        mMonthAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item,
                mMonthList);

        mSpinnerMonth.setAdapter(mMonthAdapter);
        mSpinnerMonth.setSelection(month);
        
        mDayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item,mDayList);
        mSpinnerDay.setAdapter(mDayAdapter);
        mSpinnerDay.setSelection(0);
    }
    
    private void clearData(){
        if(mYearList != null && !mYearList.isEmpty()){
            mYearList.clear();
            mYearAdapter.notifyDataSetChanged();
            }
        if(mMonthList != null && !mMonthList.isEmpty()){
            mMonthList.clear();
            mMonthAdapter.notifyDataSetChanged();
        }
        if(mDayList != null && !mDayList.isEmpty()){
            mDayList.clear();
            mMonthAdapter.notifyDataSetChanged();
        }
        
    }
    @Override
    protected void onServiceStart() {
        super.onServiceStart();
        requestPCConstitute(DATEMONTH, "", "");
    }
    
    @Override
    public void notifyEvent(int type, Object event) {
        super.notifyEvent(type, event);
        if(EventData.EVENT_GUODIAN_DATA == type){
                EventData.GuodianEvent guodianEvent = (EventData.GuodianEvent) event;
                if(GDConstract.DATATYPE_ELECTRICAL_POWER_CONSUMPTION_CONSTITUTE == guodianEvent.Type ){
                    if(mEPCConstitute == null){
                        mEPCConstitute= (EPCConstitute) guodianEvent.Data;
                        initializeData(mEPCConstitute.serviceSysDate);
                    }else{
                        mEPCConstitute= (EPCConstitute) guodianEvent.Data;
                    }
                    
                    mCurrentPPC = EPCC;
                }else if(GDConstract.DATATYPE_STEP_POWER_CONSUMPTION_CONSTITUTE == guodianEvent.Type){
                    if(mSPCConstitute == null){
                        mSPCConstitute = (SPCConstitute) guodianEvent.Data;
                        initializeData(mSPCConstitute.serviceSysDate);
                    }else{
                        mSPCConstitute = (SPCConstitute) guodianEvent.Data;
                    }
                    mCurrentPPC = SPCC;
                }else if(GDConstract.DATATYPE_PERIOD_POWER_CONSUMPTION_CONSTITUTE == guodianEvent.Type){
                    if(mPPConstitute == null){
                        mPPConstitute = (PPCConstitute) guodianEvent.Data; 
                        initializeData(mPPConstitute.serviceSysDate);
                    }else{
                        mPPConstitute = (PPCConstitute) guodianEvent.Data;
                    }
                    mCurrentPPC = PPCC;
                }
                
            }
    }
    
    private void requestPCConstitute(String dateType,String startDate,String endDate){
        LoginData loginData =  mService.getLoginData();
        if(loginData == null)
            return ;
        String  userType =loginData.UserData.UserType;
        String ccguid = loginData.CtrlNo.CtrlNoGuid;
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(JsonTag.TAGNumCCGuid, ccguid);
        params.put(JsonTag.TAGDateStart, startDate);
        params.put(JsonTag.TAGDateEnd, endDate);
        params.put(JsonTag.TAGDateType, dateType);
        params.put(JsonTag.TAGUser_Type, userType);
        
        int dataType = GDConstract.DATATYPE_ELECTRICAL_POWER_CONSUMPTION_CONSTITUTE;
        if(mCurrentPPC.equals(SPCC)){
            dataType = GDConstract.DATATYPE_STEP_POWER_CONSUMPTION_CONSTITUTE;
        }else if(mCurrentPPC.equals(PPCC)){
            dataType = GDConstract.DATATYPE_PERIOD_POWER_CONSUMPTION_CONSTITUTE;
        }
        mService.requestPowerData(dataType, params);
    }
//    private void requestEPCConstitute(String dataType,String startDate,String endDate){
//        LoginData loginData =  mService.getLoginData();
//        if(loginData == null)
//            return ;
//        String  userType =loginData.UserData.UserType;
//        String ccguid = loginData.CtrlNo.CtrlNoGuid;
//        
//        Map<String, String> params = new HashMap<String, String>();
//        params.put(JsonTag.TAGNumCCGuid, ccguid);
//        params.put(JsonTag.TAGDateStart, startDate);
//        params.put(JsonTag.TAGDateEnd, endDate);
//        params.put(JsonTag.TAGDateType, dataType);
//        params.put(JsonTag.TAGUser_Type, userType);
//        mService.requestPowerData(GDConstract.DATATYPE_ELECTRICAL_POWER_CONSUMPTION_CONSTITUTE, params);
//    }
//    
//    private void requestSPCConstitute(String dataType,String startDate,String endDate){
//        LoginData loginData =  mService.getLoginData();
//        if(loginData == null)
//            return ;
//        String  userType =loginData.UserData.UserType;
//        String ccguid = loginData.CtrlNo.CtrlNoGuid;
//        
//        Map<String, String> params = new HashMap<String, String>();
//        params.put(JsonTag.TAGNumCCGuid, ccguid);
//        params.put(JsonTag.TAGDateStart, startDate);
//        params.put(JsonTag.TAGDateEnd, endDate);
//        params.put(JsonTag.TAGDateType, dataType);
//        params.put(JsonTag.TAGUser_Type, userType);
//        mService.requestPowerData(GDConstract.DATATYPE_STEP_POWER_CONSUMPTION_CONSTITUTE, params);
//    }
//    
//    private void requestPPCConstitute(String dataType,String startDate,String endDate){
//        LoginData loginData =  mService.getLoginData();
//        if(loginData == null)
//            return ;
//        String  userType =loginData.UserData.UserType;
//        String ccguid = loginData.CtrlNo.CtrlNoGuid;
//        
//        Map<String, String> params = new HashMap<String, String>();
//        params.put(JsonTag.TAGNumCCGuid, ccguid);
//        params.put(JsonTag.TAGDateStart, startDate);
//        params.put(JsonTag.TAGDateEnd, endDate);
//        params.put(JsonTag.TAGDateType, dataType);
//        params.put(JsonTag.TAGUser_Type, userType);
//        mService.requestPowerData(GDConstract.DATATYPE_PERIOD_POWER_CONSUMPTION_CONSTITUTE, params);
//    }

    OnFocusChangeListener mFocusChangeListener = new OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(!hasFocus){
                return;
            }
            int id = v.getId();
            switch (id) {
            case R.id.power_constitute_electrical:
                mCurrentPPC = EPCC;
                if (mEPCConstitute == null) {
                    requestPCConstitute(DATEMONTH, "", "");
                    clearData();
                } else {
                    initializeData(mEPCConstitute.serviceSysDate);
                }
                break;

            case R.id.power_constitute_step:
                mCurrentPPC = SPCC;
                if (mSPCConstitute == null) {
                    requestPCConstitute(DATEMONTH, "", "");
                    clearData();
                } else {
                    initializeData(mSPCConstitute.serviceSysDate);
                }
                break;
            case R.id.power_constitute_timing:
                mCurrentPPC = PPCC;
                if (mPPConstitute == null) {
                    requestPCConstitute(DATEMONTH, "", "");
                    clearData();
                } else {
                    initializeData(mPPConstitute.serviceSysDate);
                }
                break;
            }
        }
    };
    OnClickListener mClickListener = new OnClickListener() {
        
        @Override
        public void onClick(View v) {
            int id = v.getId();
            
            switch (id) {
//            case R.id.power_constitute_electrical:
//                mCurrentPPC = EPCC;
//                if(mEPCConstitute == null){
//                    requestPCConstitute(DATEMONTH, "", "");
//                    clearData();
//                }else{
//                    initializeData(mEPCConstitute.serviceSysDate);
//                }
//                break;
//
//            case R.id.power_constitute_step:
//                mCurrentPPC = SPCC;
//                if(mSPCConstitute == null){
//                    requestPCConstitute(DATEMONTH, "", "");
//                    clearData();
//                }else{
//                    initializeData(mSPCConstitute.serviceSysDate);
//                }
//                break;
//            case R.id.power_constitute_timing:
//                mCurrentPPC = PPCC;
//                if(mPPConstitute == null){
//                    requestPCConstitute(DATEMONTH, "", "");
//                    clearData();
//                }else{
//                    initializeData(mPPConstitute.serviceSysDate);
//                }
//                break;
                
            case R.id.power_constitue_query_button:
                String date = getSelectedDate();
                if(date != null){
                    
                    requestPCConstitute(mCurrentDateTtype, date, date);
                }
                break;
                
            }
        }
    };
    protected String getSelectedDate() {
        if(mYearList == null)
            return null;
        
        String year = mYearList.get(mSpinnerYear.getSelectedItemPosition());
        
        String month = null;
        
        if(mMonthList != null && !mMonthList.isEmpty()){
            int position = mSpinnerMonth.getSelectedItemPosition();
            if(position != 0){
                month = mMonthList.get(position);
                month = String.valueOf(Integer.parseInt(month) -1);
            }
        }
       String day = null;
       if(mDayList != null && !mDayList.isEmpty()){
           int position = mSpinnerDay.getSelectedItemPosition();
           if(position != 0){
               day = mDayList.get(position);
           }
       }
       if(year != null && month != null && day != null){
           mCurrentDateTtype = DATEDAY;
           return DateUtil.constructDateStr(year, month, day, DateUtil.DateFormat1);
       }else if(year != null && month != null && day == null){
           mCurrentDateTtype = DATEMONTH;
           return DateUtil.constructDateStr(year, month, "01", DateUtil.DateFormat1);
       }else if(year != null && month == null && day == null){
           mCurrentDateTtype = DATEYEAR;
           return DateUtil.constructDateStr(year, "0", "01", DateUtil.DateFormat1);
       } 
        return null;
    }
}
