package com.dbstar.guodian.app.familyefficency;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dbstar.R;
import com.dbstar.guodian.app.base.GDSmartActivity;
import com.dbstar.guodian.data.JsonTag;
import com.dbstar.guodian.data.PowerConsumptionTrend;
import com.dbstar.guodian.data.PowerConsumptionTrend.ConsumptionPercent;
import com.dbstar.guodian.data.RoomData;
import com.dbstar.guodian.data.RoomData.RoomElectrical;
import com.dbstar.guodian.engine1.GDRequestType;
import com.dbstar.guodian.engine1.RequestParams;
import com.dbstar.model.EventData;
import com.dbstar.widget.GDSpinner;
import com.dbstar.widget.PowerTrendPolyLineView;

public class GDPowerConsumptionTrendActivity extends GDSmartActivity {
    
    private static final String DATE_PERIOD_HALF_YEAR = "halfayear";
    private static final String DATE_PERIOD_ONE_YEAR = "ayear";
    private static final String DATE_PERIOD_TWO_YEAR = "twoyears";
    
    private static final String EQUTYPEID_ALL_EQU = "all";
    private static final String EQUTYPEID_DELETED_EQU = "000000";
    private static final String EQUTYPEID_ALL_COUNT = "ffffff";
    private static final int TONGBI = 1;
    private static final int HUANBI = 0;
    private GDSpinner mSpinnerDate;
    private GDSpinner mSpinnerEqu;
    private Button mButtonQuery;
    private Button mButtonType;
    private TextView mTitle;
    private LinearLayout mPolyLineView;
    private int mCurrentShowType; // tong bi tu or huan bi tu
    private ArrayAdapter<String> mDateAdapter;
    private ArrayAdapter<String> mEquAdapter;
    private PowerConsumptionTrend mTrend;
    private ArrayList<RoomElectrical> mEquList;
    private List<String> mDateList;
    private PowerTrendPolyLineView mPolyLine;
    private boolean isRequestedElectrical;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.family_power_efficency_power_trend);
        Intent intent = getIntent();
        mMenuPath = intent.getStringExtra(INTENT_KEY_MENUPATH);
        initializeView();

        if (mMenuPath != null) {
            String[] menuArray = mMenuPath.split(MENU_STRING_DELIMITER);
            showMenuPath(menuArray);
        }
        mCurrentShowType = HUANBI;
    }

    @Override
    protected void initializeView() {
        super.initializeView();
        
        mSpinnerDate = (GDSpinner) findViewById(R.id.date_spinner);
        mSpinnerEqu = (GDSpinner) findViewById(R.id.electrical_changer_spinner);
        mButtonQuery =  (Button) findViewById(R.id.power_trend_query_button);
        mButtonType = (Button) findViewById(R.id.power_trend_type);
        mTitle = (TextView) findViewById(R.id.power_trend_title);
        mPolyLineView = (LinearLayout) findViewById(R.id.polylineView);
        mDateList = new ArrayList<String>();
        mDateList.add(getString(R.string.family_text_half_year));
        mDateList.add(getString(R.string.family_text_one_year));
        mDateList.add(getString(R.string.family_text_two_year));
        
        mDateAdapter = new ArrayAdapter<String>(this, R.layout.gd_spinner_drop_list_item, mDateList);
        mSpinnerDate.setAdapter(mDateAdapter);
        
        mEquList = new ArrayList<RoomData.RoomElectrical>();
        RoomElectrical all =  new RoomElectrical();
        all.EleDeviceCode = EQUTYPEID_ALL_EQU;
        all.DeviceName = getString(R.string.family_text_all_electrical);
        mEquList.add(all);
        RoomElectrical deleted =  new RoomElectrical();
        deleted.EleDeviceCode = EQUTYPEID_DELETED_EQU;
        deleted .DeviceName = getString(R.string.family_text_deleted_electrical);
        mEquList.add(deleted);
        
        RoomElectrical allCount =  new RoomElectrical();
        allCount.EleDeviceCode = EQUTYPEID_ALL_COUNT;
        allCount .DeviceName = getString(R.string.family_text_all_count);
        mEquList.add(allCount);
        
        ArrayList<String> equNames = new ArrayList<String>();
        for (RoomElectrical equ : mEquList) {
            equNames.add(equ.DeviceName);
        }
        mEquAdapter = new ArrayAdapter<String>(this, R.layout.gd_spinner_drop_list_item, equNames);
        mSpinnerEqu.setAdapter(mEquAdapter);
        
        mButtonQuery.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTitle();
                if(mPolyLine != null)
                    mPolyLine.clearData();
               requestPowerConsumptionTrend();
            }
        });
        
        mButtonType.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if(mCurrentShowType == HUANBI){
                    mButtonType.setBackgroundResource(R.drawable.huanbi_btn_bg_selector);
                    mCurrentShowType = TONGBI;
                }else if(mCurrentShowType == TONGBI){
                    mButtonType.setBackgroundResource(R.drawable.tongbi_btn_bg_selector);
                    mCurrentShowType = HUANBI;
                }
                updateTitle();
                showPolyLineView(mTrend);
                // update 
            }
        });
    }
    
    @Override
    protected void onServiceStart() {
        super.onServiceStart();
        requestPowerConsumptionTrend();
    }
    
    
    @Override
    public void notifyEvent(int type, Object event) {
        super.notifyEvent(type, event);
        if(EventData.EVENT_GUODIAN_DATA == type){
            EventData.GuodianEvent guodianEvent = (EventData.GuodianEvent) event;
            if(GDRequestType.DATATYPE_POWER_CONSUMPTION_TREND == guodianEvent.Type){
                if(!isRequestedElectrical)
                    requestAllEleList();
                mTrend = (PowerConsumptionTrend) guodianEvent.Data;
                showPolyLineView(mTrend);
                updateTitle();
            }else if(GDRequestType.DATATYPE_EQUMENTLIST == guodianEvent.Type){
                List<RoomElectrical> list = (ArrayList<RoomElectrical>) guodianEvent.Data;
                if(list != null && !list.isEmpty()){
                    mEquList.addAll(list);
                    initEqumentSpinner();
                }
                
            }
            
        }else if(EventData.EVENT_GUODIAN_DATA_ERROR == type){
            EventData.GuodianEvent guodianEvent = (EventData.GuodianEvent) event;
            if(GDRequestType.DATATYPE_EQUMENTLIST == guodianEvent.Type){
                showErrorMsg(R.string.loading_electrical_list_fail);
             }else{
                 showErrorMsg(R.string.loading_error);
             }
            return;
        }
    }
    
    private void updateTitle() {
        StringBuilder sb =new StringBuilder();
        sb.append(mDateList.get(mSpinnerDate.getSelectedItemPosition()));
        if(mEquList == null || mEquList.isEmpty()){
            sb.append(getString(R.string.family_text_all_electrical));
        }else{
            sb.append(mEquList.get(mSpinnerEqu.getSelectedItemPosition()).DeviceName);
        }
        sb.append(getString(R.string.family_text_power_trend_titile));
        if(mCurrentShowType == TONGBI){
            sb.append(getString(R.string.family_text_tongbitu));
        }else if(mCurrentShowType == HUANBI){
            sb.append(getString(R.string.family_text_huanbitu));
        }
       mTitle.setText(sb.toString());
        
    }

    private void requestPowerConsumptionTrend(){
        String CCGUID = null;
        if(getCtrlNo() != null){
            CCGUID = getCtrlNo().CtrlNoGuid;
        }
        if(CCGUID == null){
            showErrorMsg(R.string.no_login);
            return;
        }
        mSystemFlag = "elc";
        mRequestMethodId = "m008f012";
        RequestParams params =  new RequestParams(GDRequestType.DATATYPE_POWER_CONSUMPTION_TREND);
        params.put(RequestParams.KEY_SYSTEM_FLAG, mSystemFlag);
        params.put(RequestParams.KEY_METHODID, mRequestMethodId);
        params.put(JsonTag.TAGNumCCGuid, CCGUID);
        params.put(JsonTag.TAGVC2EquTypeId, getEquTypeId());
        params.put(JsonTag.TAGDatePeriod, getDatePeriod());
        requestData(params);
    }
    
    private void requestAllEleList(){
        String ctrlSeridno = null;
        if(getCtrlNo() != null){
            ctrlSeridno = getCtrlNo().CtrilSerialNo;
        }
        
        if(ctrlSeridno == null){
            showErrorMsg(R.string.loading_electrical_list_fail);
            return;
        }
        mSystemFlag = "sml";
        mRequestMethodId = "m001f010";
        RequestParams params =  new RequestParams(GDRequestType.DATATYPE_EQUMENTLIST);
        params.put(RequestParams.KEY_SYSTEM_FLAG, mSystemFlag);
        params.put(RequestParams.KEY_METHODID, mRequestMethodId);
        params.put(JsonTag.TAGCTRL_SeridNo, ctrlSeridno);
        requestDataNotShowDialog(params);
        isRequestedElectrical = true;
    }
    private void initEqumentSpinner(){
        ArrayList<String> equNames = new ArrayList<String>();
        for (RoomElectrical equ : mEquList) {
            equNames.add(equ.DeviceName);
        }
        mEquAdapter = new ArrayAdapter<String>(this, R.layout.gd_spinner_drop_list_item, equNames);
        mSpinnerEqu.setAdapter(mEquAdapter);
        mEquAdapter.notifyDataSetChanged();
        
    }
    private String getDatePeriod(){
        int seletion = mSpinnerDate.getSelectedItemPosition();
        String dateType = DATE_PERIOD_HALF_YEAR;
        if(seletion == 1){
            dateType = DATE_PERIOD_ONE_YEAR;
        }else if(seletion == 2){
            dateType = DATE_PERIOD_TWO_YEAR;
        }
        
        return dateType;
    }
   
    private void showPolyLineView(PowerConsumptionTrend trend){
        
        if(trend == null )
            return;
        List<ConsumptionPercent> trendData = null;
        if(mCurrentShowType == HUANBI){
            trendData = trend.HuanBiList;
        }else if(mCurrentShowType == TONGBI){
            trendData = trend.TongBiList;
        }
        if(trendData == null)
            return;
        
        ArrayList<Float> data1 = new ArrayList<Float>();
        ArrayList<String> xTextData = new ArrayList<String>();
        ConsumptionPercent percent;
        for(int i = 0;i< trendData.size() ;i ++){
            percent = trendData.get(i);
            if(percent.CountPercent != null && !percent.CountPercent.isEmpty())
            {
                
               data1.add(Float.valueOf(percent.CountPercent.substring(0,percent.CountPercent.length() -1)));
                //data1.add(new Float(new Random().nextInt(200)));
            }
            else{
                data1.add(0f);
            }
            if(percent.DateTime != null)
                xTextData.add(percent.DateTime);
            else{
                xTextData.add(" ");
            }
        }
        mPolyLineView.removeAllViews();
        mPolyLine = new PowerTrendPolyLineView(this);
        mPolyLine.setFrame(mPolyLineView.getMeasuredWidth(), mPolyLineView.getMeasuredHeight(),  100, 20, 150, 40);
        mPolyLine.setData(data1, xTextData, null, "%");
        mPolyLineView.addView(mPolyLine);
        
    }
    private String getEquTypeId(){
        int selection = mSpinnerEqu.getSelectedItemPosition();
        if(selection == -1){
            return EQUTYPEID_ALL_EQU;
        }
        return mEquList.get(selection).EleDeviceCode;
        
    }
}
