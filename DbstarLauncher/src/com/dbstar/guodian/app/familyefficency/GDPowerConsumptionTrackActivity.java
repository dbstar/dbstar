package com.dbstar.guodian.app.familyefficency;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.dbstar.R;
import com.dbstar.app.GDBaseActivity;
import com.dbstar.guodian.data.JsonTag;
import com.dbstar.guodian.data.LoginData;
import com.dbstar.guodian.data.RoomData;
import com.dbstar.guodian.data.RoomData.RoomEletrical;
import com.dbstar.guodian.data.StepPowerConsumptionTrack;
import com.dbstar.guodian.data.StepPowerConsumptionTrack.DateStepPower;
import com.dbstar.guodian.engine.GDConstract;
import com.dbstar.model.EventData;
import com.dbstar.util.DateUtil;
import com.dbstar.widget.DrawPillar;
import com.dbstar.widget.PowerTrackPolyLineView;

public class GDPowerConsumptionTrackActivity extends GDBaseActivity{
    
    private static final String DATEMONTH = "month";
    private static final String DATEYEAR = "year";
    private static final String DATEDAY = "day";
    private static final String EQUTYPEID_ALL_EQU = "all";
    private static final String EQUTYPEID_DELETED_EQU = "000000";
    private static final String EQUTYPEID_ALL_COUNT = "ffffff";
            
    private Spinner mSpinnerYear;
    private Spinner mSpinnerMonth;
    private Spinner mSpinnerDay;
    private Spinner mSpinnerEqu;
    private Button  mButtonRequey;
    private Button  mButtonViewType;
    private TextView mTextViewTitle;
    private TextView mTextViewCount;
    
    private LinearLayout mHistogramView;
    private ArrayList<String> mYearList;
    private ArrayList<String> mMonthList;
    private ArrayList<String> mDayList;
    private ArrayList<RoomEletrical> mEquList;
    private ArrayAdapter<String> mYearAdapter;
    private ArrayAdapter<String> mMonthAdapter;
    private ArrayAdapter<String> mDayAdapter;
    private ArrayAdapter<String> mEquAdapter;
    private String mCurrentYear;
    private String mCurrentMonth;
    private String mCurrentDateTtype;
    private DrawPillar mPillar;
    private PowerTrackPolyLineView mPolyLine;
    private boolean mIsPillar = true;
    Random random = new Random();
    private StepPowerConsumptionTrack track;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.family_power_efficency_power_track);
        Intent intent = getIntent();
        mMenuPath = intent.getStringExtra(INTENT_KEY_MENUPATH);
        initializeView();

        if (mMenuPath != null) {
            String[] menuArray = mMenuPath.split(MENU_STRING_DELIMITER);
            showMenuPath(menuArray);
        }
        mCurrentDateTtype = DATEMONTH;
    }
    
    @Override
    protected void initializeView() {
        super.initializeView();
        mSpinnerYear = (Spinner) findViewById(R.id.year_spinner);
        mSpinnerMonth = (Spinner) findViewById(R.id.month_spinner);
        mSpinnerDay = (Spinner) findViewById(R.id.day_spinner);
        mHistogramView = (LinearLayout) findViewById(R.id.histogramView);
        mButtonRequey  = (Button) findViewById(R.id.power_track_query_button);
        mTextViewCount = (TextView) findViewById(R.id.power_track_count);
        mTextViewTitle = (TextView) findViewById(R.id.power_track_title);
        mButtonViewType = (Button) findViewById(R.id.power_track_view_type);
        mSpinnerEqu = (Spinner) findViewById(R.id.electrical_changer_spinner);
        mEquList = new ArrayList<RoomData.RoomEletrical>();
        RoomEletrical all =  new RoomEletrical();
        all.EleDeviceCode = EQUTYPEID_ALL_EQU;
        all.DeviceName = getString(R.string.family_text_all_electrical);
        mEquList.add(all);
        RoomEletrical deleted =  new RoomEletrical();
        deleted.EleDeviceCode = EQUTYPEID_DELETED_EQU;
        deleted .DeviceName = getString(R.string.family_text_deleted_electrical);
        mEquList.add(deleted);
        
        RoomEletrical allCount =  new RoomEletrical();
        allCount.EleDeviceCode = EQUTYPEID_ALL_COUNT;
        allCount .DeviceName = getString(R.string.family_text_all_count);
        mEquList.add(allCount);
        
        ArrayList<String> equNames = new ArrayList<String>();
        for (RoomEletrical equ : mEquList) {
            equNames.add(equ.DeviceName);
        }
        mEquAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, equNames);
        mSpinnerEqu.setAdapter(mEquAdapter);
        
        mButtonViewType.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if(mIsPillar){
                    mButtonViewType.setBackgroundResource(R.drawable.histogram_btn_bg_selector);
                    mIsPillar = false;
                }else{
                    mButtonViewType.setBackgroundResource(R.drawable.polyline_btn_bg_selector);
                    mIsPillar = true;
                }
                initTextView(track);
                showHistogramView(track);
            }
        });
        mButtonRequey.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                String date = getSelectedDate();
                String equType  = EQUTYPEID_ALL_EQU;
                int equTypeIndex = mSpinnerEqu.getSelectedItemPosition();
                if(equTypeIndex != -1 && mEquList.size() > equTypeIndex){
                    equType = mEquList.get(equTypeIndex).EleDeviceCode;
                }
                if(date != null){
                    if(mIsPillar){
                        if(mPillar != null)
                            mPillar.clearData();
                    }else{
                        if(mPolyLine != null)
                            mPolyLine.clearData();
                    }
                    requestPowerConsumptionTrack(equType, date);
                }
            }
        });
        
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
    private void initDateSpinner(String dateStr) {

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
    
    
    private void initEqumentSpinner(){
        ArrayList<String> equNames = new ArrayList<String>();
        for (RoomEletrical equ : mEquList) {
            equNames.add(equ.DeviceName);
        }
        mEquAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, equNames);
        mSpinnerEqu.setAdapter(mEquAdapter);
        
    }
    
    @Override
    protected void onServiceStart() {
        super.onServiceStart();
        requestPowerConsumptionTrack(EQUTYPEID_ALL_EQU, "");
        requestAllEleList();
    }
    

    private void requestPowerConsumptionTrack(String equTypeId,String dateTime){
        String ccguid = getCCUID();
        
        if(ccguid == null)
            return ;
        Map<String, String> params = new HashMap<String, String>();
        params.put(JsonTag.TAGNumCCGuid, ccguid);
        params.put(JsonTag.TAGDateType, mCurrentDateTtype);
        params.put(JsonTag.TAGDate_Time, dateTime);
        params.put(JsonTag.TAGVC2EquTypeId, equTypeId);
        
        mService.requestPowerData(GDConstract.DATATYPE_STEP_POWER_CONSUMPTION_TRACK, params);
        
    }
    private void requestAllEleList(){
        LoginData loginData = mService.getLoginData();
        if (loginData == null)
            return;
        
        if(loginData.CtrlNo == null)
            return ;
        String ctrlSeridno = loginData.CtrlNo.CtrilSerialNo;
        Map<String, String> params = new HashMap<String, String>();
        params.put(JsonTag.TAGCTRL_SeridNo, ctrlSeridno);
        mService.requestPowerData(GDConstract.DATATYPE_EQUMENTLIST, params);
    }
    @Override
    public void notifyEvent(int type, Object event) {
        super.notifyEvent(type, event);
        if(EventData.EVENT_GUODIAN_DATA == type){
            EventData.GuodianEvent guodianEvent = (EventData.GuodianEvent) event;
            if(GDConstract.DATATYPE_STEP_POWER_CONSUMPTION_TRACK == guodianEvent.Type){
                track = (StepPowerConsumptionTrack) guodianEvent.Data;
                if(mYearList == null || mYearList.isEmpty())
                    initDateSpinner(track.serviceSysDate);
                initTextView(track);
                showHistogramView(track);
            }else if(GDConstract.DATATYPE_EQUMENTLIST == guodianEvent.Type){
                List<RoomEletrical> list = (ArrayList<RoomEletrical>) guodianEvent.Data;
                if(list != null && !list.isEmpty()){
                    mEquList.addAll(list);
                    initEqumentSpinner();
                }
            }
        }
        
    }
    
    private void initTextView(StepPowerConsumptionTrack track) {
        if(track == null)
            return;
        String date = getSelectedDate();
        if(date == null)
            return;
        String equType =  getString(R.string.family_text_all_electrical);
        int equTypeIndex = mSpinnerEqu.getSelectedItemPosition();
        if(equTypeIndex != -1 && mEquList.size() > equTypeIndex){
            equType = mEquList.get(equTypeIndex).DeviceName;
        }
        StringBuilder sb = new StringBuilder();
        if(mCurrentDateTtype.equals(DATEMONTH)){
            
            sb.append(mYearList.get(mSpinnerYear.getSelectedItemPosition()));
            sb.append(getString(R.string.ch_year));
            sb.append(mMonthList.get(mSpinnerMonth.getSelectedItemPosition()));
            sb.append(getString(R.string.ch_month));
        }else if(mCurrentDateTtype.equals(DATEYEAR)){
            sb.append(mYearList.get(mSpinnerYear.getSelectedItemPosition()));
            sb.append(getString(R.string.ch_year));
        }else if(mCurrentDateTtype .equals(DATEDAY)){
            sb.append(mYearList.get(mSpinnerYear.getSelectedItemPosition()));
            sb.append(getString(R.string.ch_year));
            sb.append(mMonthList.get(mSpinnerMonth.getSelectedItemPosition()));
            sb.append(getString(R.string.ch_month));
            sb.append(mDayList.get(mSpinnerDay.getSelectedItemPosition()));
            sb.append(getString(R.string.ch_day));
        }
        sb.append(equType);
        sb.append(getString(R.string.family_text_used_power_count));
        if(mIsPillar){
            sb.append(getString(R.string.family_text_histogram));
        }else{
            sb.append(getString(R.string.family_text_polyline_chart));
        }
        mTextViewTitle.setText(sb.toString());
        if(track.totalConsumption == null)
            return;
        mTextViewCount.setText(track.totalConsumption.Count + getString(R.string.string_yuan));
    }

    private void showHistogramView(StepPowerConsumptionTrack track) {
        if(track == null || track.dateStepPowerList == null)
            return;
            
        List<DateStepPower> data = track.dateStepPowerList;
        ArrayList<Float>  data1 = new ArrayList<Float>();
        ArrayList<String> xText = new ArrayList<String>();
        DateStepPower stepPower;
        String xType = getString(R.string.ch_month);
        String yType = getString(R.string.str_degree);
        int size =0;
        if(mCurrentDateTtype.equals(DATEYEAR)){
            size = 12;
            xType = getString(R.string.ch_month);
        }else if(mCurrentDateTtype.equals(DATEMONTH)){
            size = mDayList.size()-1;
            xType = getString(R.string.ch_day);
            
        }else if(mCurrentDateTtype.equals(DATEDAY)){
            size = 24;
            xType = getString(R.string.ch_hour);
        }else{
            return;
        }
        for(int i = 0 , m = data.size();i< size ;i++){
            if(i < m ){
                stepPower =  data.get(i);
                data1.add(stepPower.allCount);
            }else{
                data1.add(null);
            }
            xText .add(String.valueOf(i+1));
        }
//        
//        for(int i  = 0;i< data.size();i++){
//            stepPower =  data.get(i);
//            data1.add(stepPower.allCount);
//            //data1.add(new Float(random.nextInt(500)));
//            xText .add(String.valueOf(i+1));
//            if(i == 11){
//                xType = getString(R.string.ch_year);
//            }
//            
//            if(i == 23){
//                xType = getString(R.string.ch_month);
//            }
//            
//            if(i == 27){
//                xType = getString(R.string.ch_day);
//            }
//        }
        mHistogramView.removeAllViews();
        if(mIsPillar){
            mPillar = new DrawPillar(this);
            mPillar.setFrame(mHistogramView.getMeasuredWidth(), mHistogramView.getMeasuredHeight(), 70, 20, 30, 50);
            mPillar.setData(data1, xText, xType, yType);
            mHistogramView.addView(mPillar);
        }else{
            mPolyLine = new PowerTrackPolyLineView(this);
            mPolyLine.setFrame(mHistogramView.getMeasuredWidth(), mHistogramView.getMeasuredHeight(), 100, 50, 100, 50);
            mPolyLine.setData(data1, xText, xType, yType);
            mHistogramView.addView(mPolyLine);
        }
        
    }
    
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
