package com.dbstar.guodian.app.familyefficency;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.dbstar.R;
import com.dbstar.app.GDBaseActivity;
import com.dbstar.guodian.data.EPCConstitute;
import com.dbstar.guodian.data.EPCConstitute.ElectricalItemDetail;
import com.dbstar.guodian.data.JsonTag;
import com.dbstar.guodian.data.LoginData;
import com.dbstar.guodian.data.PPCConstitute;
import com.dbstar.guodian.data.PPCConstitute.PeriodItemDetail;
import com.dbstar.guodian.data.PowerData;
import com.dbstar.guodian.data.SPCConstitute;
import com.dbstar.guodian.data.SPCConstitute.StepItemDetail;
import com.dbstar.guodian.engine.GDConstract;
import com.dbstar.model.EventData;
import com.dbstar.util.DateUtil;
import com.dbstar.widget.DrawPie;

public class GDPowerConstitueActivity extends GDBaseActivity{
    
    private static final String EPCC = "electricalPowerConsuptionConstitute";
    private static final String SPCC = "stepPowerConsuptionConstitute";
    private static final String PPCC = "peroidPowerConsuptionConstitute";
    private static final String DATEMONTH = "month";
    private static final String DATEYEAR = "year";
    private static final String DATEDAY = "day";
    private Button mButtonElecical;
    private Button mButtonStep;
    private Button mButtonTiming;
    private Button mButtonQuery;
    private TextView mTextViewTotalCount;
    private TextView mTextViewDataType;
    private Spinner mSpinnerYear;
    private Spinner mSpinnerMonth;
    private Spinner mSpinnerDay;
    private ArrayList<String> mYearList;
    private ArrayList<String> mMonthList;
    private ArrayList<String> mDayList;
    private ArrayAdapter<String> mYearAdapter;
    private ArrayAdapter<String> mMonthAdapter;
    private ArrayAdapter<String> mDayAdapter;
    private ListView mPowerInfoListView;
    private String mCurrentYear;
    private String mCurrentMonth;
    private String mCurrentDateTtype;
    private String mCurrentPCC;
    private EPCConstitute mEPCConstitute;
    private SPCConstitute mSPCConstitute;
    private PPCConstitute mPPCConstitute;
    private List<ListItemInfo> mEPCCListData;
    private List<ListItemInfo> mSPCCListData;
    private List<ListItemInfo> mPPCCListData;
    private PowerConstituteAdapter mAdapter;
    private LinearLayout mPieView;
    private String mTotalCount;
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
        
        mCurrentPCC = EPCC;
        mPowerInfoListView = (ListView) findViewById(R.id.power_constitue_list);
        mPieView = (LinearLayout) findViewById(R.id.piechart_view);
        mButtonElecical = (Button) findViewById(R.id.power_constitute_electrical);
        mButtonStep = (Button) findViewById(R.id.power_constitute_step);
        mButtonTiming = (Button) findViewById(R.id.power_constitute_timing);
        mButtonQuery = (Button) findViewById(R.id.power_constitue_query_button);
        mSpinnerYear = (Spinner) findViewById(R.id.year_spinner);
        mSpinnerMonth = (Spinner) findViewById(R.id.month_spinner);
        mSpinnerDay = (Spinner) findViewById(R.id.day_spinner);
        
        mTextViewDataType = (TextView) findViewById(R.id.power_constitute_data_type);
        mTextViewTotalCount = (TextView) findViewById(R.id.power_constitue_total_count);
        mButtonElecical.setOnFocusChangeListener(mFocusChangeListener);
        mButtonStep.setOnFocusChangeListener(mFocusChangeListener);
        mButtonTiming.setOnFocusChangeListener(mFocusChangeListener);
        
        mButtonQuery.setOnClickListener(mClickListener);
        
        mAdapter = new PowerConstituteAdapter(this);
        mPowerInfoListView.setAdapter(mAdapter);
        
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
    
    private void showPieView(List<ListItemInfo> data){
        if(data == null || data.isEmpty()){
            mPieView.removeAllViews(); 
            return;
        }
        if(mPie == null){
            mPie = new DrawPie(this);
            mPie.setOriginPoint(0, 0);//位置
            mPie.setPicSize(mPieView.getMeasuredWidth(), mPieView.getMeasuredHeight() - 25);//大小
            mPie.setChartDepth(25);//厚度
        }
        percents = new ArrayList<Float>();
        if(data != null){
            ArrayList<Integer> colors = mPie.initColor(data.size());
            ListItemInfo info ;
            for(int i = 0;i< data.size();i++){
                info = data.get(i);
                if(i== data.size() -1){
                    info.color = colors.get(colors.size() -1);
                }
                else
                    info.color = colors.get(i);
                percents.add(Float.parseFloat(info.powerPercent.substring(0,info.powerPercent.length() -1)));
            }
        }
      
        mPie.setData(percents);//数据
        mPieView.removeAllViews();
        mPieView.addView(mPie);
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
        if(mYearList != null)
            return;
        
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
        
        mTextViewTotalCount.setText(getString(R.string.family_text_total_used_power_count));
        if(mAdapter != null){
            mAdapter.setData(null);
            mAdapter.notifyDataSetChanged();
        }
            
        if(mPie != null){
            mPie.clearData();
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
                    mEPCConstitute= (EPCConstitute) guodianEvent.Data;
                    initializeData(mEPCConstitute.serviceSysDate);
                    if(mCurrentPCC.equals(EPCC)){
                        refreshContectView();
                    }
                    
                }else if(GDConstract.DATATYPE_STEP_POWER_CONSUMPTION_CONSTITUTE == guodianEvent.Type){
                    mSPCConstitute = (SPCConstitute) guodianEvent.Data;
                    initializeData(mSPCConstitute.serviceSysDate);
                    if(mCurrentPCC.equals(SPCC)){
                        refreshContectView();
                    }
                }else if(GDConstract.DATATYPE_PERIOD_POWER_CONSUMPTION_CONSTITUTE == guodianEvent.Type){
                    mPPCConstitute = (PPCConstitute) guodianEvent.Data; 
                    initializeData(mPPCConstitute.serviceSysDate);
                    if(mCurrentPCC.equals(PPCC)){
                        refreshContectView();
                    }
                }
                
            }
    }

    private void refreshContectView() {
        List<ListItemInfo> data = inintListData();
        showPieView(data);
        mAdapter.setData(data);
        mAdapter.notifyDataSetChanged();
        mTextViewTotalCount.setText(getString(R.string.family_text_total_used_power_count )+ getTotalCount());
    }
    
    private String getTotalCount() {
        PowerData powerData = null;
        if(mCurrentPCC.equals(EPCC)){
            if(mEPCConstitute != null)
                powerData = mEPCConstitute.totalPower;
        }else if(mCurrentPCC.equals(SPCC)){
            if(mSPCConstitute != null)
                powerData = mSPCConstitute.totalPower;
        }else if(mCurrentPCC.equals(PPCC)){
            if(mPPCConstitute != null)
                powerData = mPPCConstitute.totalPower;
        }
        if(powerData != null)
            return powerData.Count +  getString(R.string.str_degree);
        return "0.0" + getString(R.string.str_degree);
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
        if(mCurrentPCC.equals(SPCC)){
            dataType = GDConstract.DATATYPE_STEP_POWER_CONSUMPTION_CONSTITUTE;
        }else if(mCurrentPCC.equals(PPCC)){
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
            setButtonBackground(id);
            switch (id) {
            case R.id.power_constitute_electrical:
                mCurrentPCC = EPCC;
                if (mEPCConstitute == null) {
                    requestPCConstitute(DATEMONTH, "", "");
                    clearData();
                } else {
                    refreshContectView();
                }
                break;

            case R.id.power_constitute_step:
                mCurrentPCC = SPCC;
                if (mSPCConstitute == null) {
                    requestPCConstitute(DATEMONTH, "", "");
                    clearData();
                } else {
                    refreshContectView();
                }
                break;
            case R.id.power_constitute_timing:
                mCurrentPCC = PPCC;
                if (mPPCConstitute == null) {
                    requestPCConstitute(DATEMONTH, "", "");
                    clearData();
                } else {
                    refreshContectView();
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
                clearData();
                if(date != null){
                    if(mCurrentPCC.equals(EPCC)){
                        if(mEPCCListData != null){
                            mEPCCListData.clear();
                            mEPCCListData = null;
                        }
                    }else if(mCurrentPCC.equals(SPCC)){
                        if(mSPCCListData != null){
                            mSPCCListData.clear();
                            mSPCCListData = null;
                        }
                    }else if(mCurrentPCC.equals(PPCC)){
                        if(mPPCCListData != null){
                            mPPCCListData.clear();
                            mPPCCListData = null;
                        }
                    }
                    requestPCConstitute(mCurrentDateTtype, date, date);
                }
                break;
                
            }
        }
    };
    private ArrayList<Float> percents;
    private DrawPie mPie;
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

    class PowerConstituteAdapter extends BaseAdapter{
        
        public List<ListItemInfo> mData;
        private ViewHolder v;
        private LayoutInflater inflater;
        
        public PowerConstituteAdapter(Context context){
            inflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return mData == null ? 0
                    : mData.size();
        }
        
        public void setData(List<ListItemInfo> data){
            this.mData = data;
        }
        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = inflater.inflate(R.layout.power_constitute_list_itme_view, null);
                v = new ViewHolder();
                v.mColor = (ImageView) convertView.findViewById(R.id.color);
                v.mDataType = (TextView) convertView.findViewById(R.id.data_type) ;  
                v.mCount = (TextView) convertView.findViewById(R.id.count);
                v.mPercent = (TextView) convertView.findViewById(R.id.percent);
                convertView.setTag(v);
               }else{
                   v = (ViewHolder) convertView.getTag();
               }
            
               ListItemInfo itemInfo = mData.get(position);
               v.mColor.setBackgroundColor(itemInfo.color);
               v.mDataType.setText(itemInfo.dataType);
               v.mCount.setText(itemInfo.powerCount);
               v.mPercent.setText(itemInfo.powerPercent);
            return convertView;
        }
        
        class ViewHolder {
            ImageView mColor;
            TextView  mDataType;
            TextView  mCount;
            TextView mPercent;
        }
    }
    public List<ListItemInfo> inintListData(){
        List<ListItemInfo> itemInfoList = null;;
        if(mCurrentPCC.equals(EPCC)){
            mTextViewDataType.setText(getString(R.string.power_constitue_power_data_type_ele));
            if(mEPCCListData != null){
                return mEPCCListData;
            }
            if(mEPCConstitute != null){
                List<ElectricalItemDetail> listData = mEPCConstitute.electricalItemDetails;
                if(listData != null){
                    itemInfoList = new ArrayList<GDPowerConstitueActivity.ListItemInfo>();
                    ListItemInfo info;
                    ElectricalItemDetail detail;
                    for(int i = 0;i < listData.size();i++){
                        detail = listData.get(i);
                        info = new ListItemInfo();
                        info.color =  0 ;
                        info.dataType = detail.ElecName;
                        info.powerCount = detail.Count;
                        info.powerPercent   = detail.CountPercent;
                        itemInfoList.add(info);
                    }
                    mEPCCListData = itemInfoList;
                }
            }
        }else if(mCurrentPCC.equals(SPCC)){
            mTextViewDataType.setText(getString(R.string.power_constitue_power_data_type_step));
            if(mSPCCListData != null)
                return mSPCCListData;
            if(mSPCConstitute != null){
                List<StepItemDetail> listData = mSPCConstitute.stepItemDetails;
                if(listData != null){
                    itemInfoList = new ArrayList<GDPowerConstitueActivity.ListItemInfo>();
                    ListItemInfo info; 
                    StepItemDetail detail;
                    for(int i = 0;i < listData.size();i++){
                        detail = listData.get(i);
                        info = new ListItemInfo();
                        info.color =  0 ;
                        if("1".equals(detail.stepName)){
                            info.dataType = getString(R.string.step_1);
                        }else if("2".equals(detail.stepName)){
                            info.dataType = getString(R.string.step_2);
                        }else if("3".equals(detail.stepName)){
                            info.dataType = getString(R.string.step_3);
                        } 
                        info.powerCount = detail.Count;
                        info.powerPercent   = detail.CountPercent;
                        itemInfoList.add(info);
                    }
                    mSPCCListData = itemInfoList;
                }
            }
        }else if(mCurrentPCC.equals(PPCC)){
            mTextViewDataType.setText(getString(R.string.power_constitue_power_data_type_period));
            if(mPPCCListData != null)
                return mPPCCListData;
            if(mPPCConstitute != null){
                List<PeriodItemDetail> listData = mPPCConstitute.periodItemDetails;
                if(listData != null){
                    itemInfoList = new ArrayList<GDPowerConstitueActivity.ListItemInfo>();
                    ListItemInfo info; 
                    PeriodItemDetail detail;
                    for(int i = 0;i < listData.size();i++){
                        detail = listData.get(i);
                        info = new ListItemInfo();
                        info.color =  0 ;
                        if("1".equals(detail.periodName)){
                            info.dataType = getString(R.string.ch_period_1);
                        }else if("2".equals(detail.periodName)){
                            info.dataType = getString(R.string.ch_period_2);
                        }else if("3".equals(detail.periodName)){
                            info.dataType = getString(R.string.ch_period_3);
                        } 
                        info.powerCount = detail.Count;
                        info.powerPercent   = detail.CountPercent;
                        itemInfoList.add(info);
                    }
                    mPPCCListData = itemInfoList;
                }
            }
        }
        return  itemInfoList;
    }
    class  ListItemInfo{
        int color;
        String dataType;
        String powerCount;
        String powerPercent;
        
    }
    
    protected void setButtonBackground(int id) {
        switch (id) {
        case R.id.power_constitute_electrical:
            mButtonElecical.setBackgroundResource(R.drawable.mypower_payment_record_button_highlight);
            mButtonElecical.setTextColor(Color.WHITE);
            mButtonStep.setBackgroundResource(R.drawable.mypower_payment_record_button_normal);
            mButtonStep.setTextColor(Color.BLACK);
            mButtonTiming.setBackgroundResource(R.drawable.mypower_payment_record_button_normal);
            mButtonTiming.setTextColor(Color.BLACK);
            break;

        case R.id.power_constitute_step:
            mButtonElecical.setBackgroundResource(R.drawable.mypower_payment_record_button_normal);
            mButtonElecical.setTextColor(Color.BLACK);
            mButtonStep.setBackgroundResource(R.drawable.mypower_payment_record_button_highlight);
            mButtonStep.setTextColor(Color.WHITE);
            mButtonTiming.setBackgroundResource(R.drawable.mypower_payment_record_button_normal);
            mButtonTiming.setTextColor(Color.BLACK);
            break;
        case R.id.power_constitute_timing:
            mButtonElecical.setBackgroundResource(R.drawable.mypower_payment_record_button_normal);
            mButtonElecical.setTextColor(Color.BLACK);
            mButtonStep.setBackgroundResource(R.drawable.mypower_payment_record_button_normal);
            mButtonStep.setTextColor(Color.BLACK);
            mButtonTiming.setBackgroundResource(R.drawable.mypower_payment_record_button_highlight);
            mButtonTiming.setTextColor(Color.WHITE);
            break;
        }
    }
}
