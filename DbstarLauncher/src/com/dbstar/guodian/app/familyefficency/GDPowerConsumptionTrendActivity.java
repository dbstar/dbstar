package com.dbstar.guodian.app.familyefficency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Element.DataType;
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
import com.dbstar.guodian.app.base.GDSmartActivity;
import com.dbstar.guodian.data.EqumentData;
import com.dbstar.guodian.data.JsonTag;
import com.dbstar.guodian.data.LoginData;
import com.dbstar.guodian.data.PowerConsumptionTrend;
import com.dbstar.guodian.data.RoomData;
import com.dbstar.guodian.data.PowerConsumptionTrend.ConsumptionPercent;
import com.dbstar.guodian.data.RoomData.RoomEletrical;
import com.dbstar.guodian.engine.GDConstract;
import com.dbstar.model.EventData;
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
    private Spinner mSpinnerDate;
    private Spinner mSpinnerEqu;
    private Button mButtonQuery;
    private Button mButtonType;
    private TextView mTitle;
    private LinearLayout mPolyLineView;
    private int mCurrentShowType; // tong bi tu or huan bi tu
    private ArrayAdapter<String> mDateAdapter;
    private ArrayAdapter<String> mEquAdapter;
    private PowerConsumptionTrend mTrend;
    private ArrayList<RoomEletrical> mEquList;
    private List<String> mDateList;
    private PowerTrendPolyLineView mPolyLine;
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
        
        mSpinnerDate = (Spinner) findViewById(R.id.date_spinner);
        mSpinnerEqu = (Spinner) findViewById(R.id.electrical_changer_spinner);
        mButtonQuery =  (Button) findViewById(R.id.power_trend_query_button);
        mButtonType = (Button) findViewById(R.id.power_trend_type);
        mTitle = (TextView) findViewById(R.id.power_trend_title);
        mPolyLineView = (LinearLayout) findViewById(R.id.polylineView);
        mDateList = new ArrayList<String>();
        mDateList.add(getString(R.string.family_text_half_year));
        mDateList.add(getString(R.string.family_text_one_year));
        mDateList.add(getString(R.string.family_text_two_year));
        
        mDateAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, mDateList);
        mSpinnerDate.setAdapter(mDateAdapter);
        
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
        requestAllEleList();
        requestPowerConsumptionTrend();
    }
    
    
    @Override
    public void notifyEvent(int type, Object event) {
        super.notifyEvent(type, event);
        if(EventData.EVENT_GUODIAN_DATA == type){
            EventData.GuodianEvent guodianEvent = (EventData.GuodianEvent) event;
            if(GDConstract.DATATYPE_POWER_CONSUMPTION_TREND == guodianEvent.Type){
                mTrend = (PowerConsumptionTrend) guodianEvent.Data;
                showPolyLineView(mTrend);
                updateTitle();
            }else if(GDConstract.DATATYPE_EQUMENTLIST == guodianEvent.Type){
                List<RoomEletrical> list = (ArrayList<RoomEletrical>) guodianEvent.Data;
                if(list != null && !list.isEmpty()){
                    mEquList.addAll(list);
                    initEqumentSpinner();
                }
                
            }
            
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
        String CCGUID = getCCUID();
        if(CCGUID == null)
            return ;
        Map<String, String>params = new HashMap<String, String>();
        params.put(JsonTag.TAGNumCCGuid, CCGUID);
        params.put(JsonTag.TAGVC2EquTypeId, getEquTypeId());
        params.put(JsonTag.TAGDatePeriod, getDatePeriod());
        mService.requestPowerData(GDConstract.DATATYPE_POWER_CONSUMPTION_TREND,params);
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
    private void initEqumentSpinner(){
        ArrayList<String> equNames = new ArrayList<String>();
        for (RoomEletrical equ : mEquList) {
            equNames.add(equ.DeviceName);
        }
        mEquAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, equNames);
        mSpinnerEqu.setAdapter(mEquAdapter);
        
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
        mPolyLine.setFrame(mPolyLineView.getMeasuredWidth(), mPolyLineView.getMeasuredHeight(),  100, 50, 150, 50);
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
