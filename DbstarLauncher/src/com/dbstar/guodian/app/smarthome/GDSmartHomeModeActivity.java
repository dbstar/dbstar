package com.dbstar.guodian.app.smarthome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dbstar.R;
import com.dbstar.app.GDBaseActivity;
import com.dbstar.guodian.app.mypower.GDBillActivity;
import com.dbstar.guodian.data.ElectricalOperationMode;
import com.dbstar.guodian.data.ElectricalOperationMode.ModeElectrical;
import com.dbstar.guodian.data.ResultData;
import com.dbstar.guodian.data.RoomData.RoomEletrical;
import com.dbstar.guodian.data.JsonTag;
import com.dbstar.guodian.data.LoginData;
import com.dbstar.guodian.data.RoomData;
import com.dbstar.guodian.data.TimedTask;
import com.dbstar.guodian.engine.GDConstract;
import com.dbstar.model.EventData;
import com.dbstar.widget.CircleFlowIndicator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GDSmartHomeModeActivity extends GDBaseActivity{
    
    public static final String ELE_ON  = "01";
    public static final String ELE_OFF = "00";
    private ImageView mLeftJianTou;
    private ImageView mRightJianTou;
    private GridView mListViewMode;
    private GridView mListViewMoleEle;
    private LinearLayout mLayoutNoMode;
    private LinearLayout mLayoutMode;
    private CircleFlowIndicator indicator;
    private int mPageSizeMode = 5;
    private int ElePageSize = 6;
    private int mPageCountModel, mPageNumberModel,mElePageCount,mElePageNumber;
    private int mCountModel,mEleCount;
    private List<ElectricalOperationMode []> mPageModes;
    private List<RoomEletrical> mAllElectricals;
    private ElectricalOperationMode mCacheMode;
    private ModeAdapter mAdapterMode;
    private ModeElectricalAdapter mAdapterModeEle;
    private boolean mIsLoadBack = true;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.smart_home_model);
        Intent intent = getIntent();
        mMenuPath = intent.getStringExtra(INTENT_KEY_MENUPATH);
        initializeView();

        if (mMenuPath != null) {
            String[] menuArray = mMenuPath.split(MENU_STRING_DELIMITER);
            showMenuPath(menuArray);
        }
    }
    
    @Override
    public void initializeView() {
        super.initializeView();
        mRightJianTou = (ImageView) findViewById(R.id.smart_home_right_jantou);
        mLeftJianTou = (ImageView) findViewById(R.id.smart_home_top_left_jantou);
        mListViewMode = (GridView) findViewById(R.id.smart_home_top_model);
        mListViewMoleEle = (GridView) findViewById(R.id.smart_home_bottom_equ);
        mLayoutNoMode = (LinearLayout) findViewById(R.id.no_model_page);
        mLayoutMode = (LinearLayout) findViewById(R.id.model_page);
        indicator = (CircleFlowIndicator) findViewById(R.id.indicator);
        
        mAdapterMode = new ModeAdapter();
        mListViewMode.setAdapter(mAdapterMode);
        
        mAdapterModeEle = new ModeElectricalAdapter();
        
        mListViewMoleEle.setAdapter(mAdapterModeEle);
        
        mListViewMoleEle.setOnKeyListener(new OnKeyListener() {
            
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                boolean ret = false;
                int action = event.getAction();
                if (action == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {

                    case KeyEvent.KEYCODE_DPAD_LEFT: {
                        int selectedIndex = mListViewMoleEle
                                .getSelectedItemPosition();
                        if (selectedIndex == 0 && mElePageNumber > 0) {
                            loadElePrevPage();
                            ret = true;
                        }
                        break;
                    }
                    case KeyEvent.KEYCODE_DPAD_RIGHT: {
                        int selectedIndex = mListViewMoleEle
                                .getSelectedItemPosition();
                        if (selectedIndex == (ElePageSize - 1)
                                && mElePageNumber < mElePageCount - 1) {
                            loadEleNextPage();
                            ret = true;
                        }
                        break;
                    }
                    default:
                        break;
                    }

                }
                return ret;
            }
        });
        
        mListViewMode.setOnFocusChangeListener(new OnFocusChangeListener() {
            
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                int position = mListViewMode.getSelectedItemPosition();
                if(hasFocus){
                    for(int i = 0,size = mListViewMode.getChildCount(); i <size ;i ++){
                        if(i == position){
                            mListViewMode.getChildAt(i).setBackgroundResource(R.drawable.smart_home_room_focus_bg);
                        }else{
                            mListViewMode.getChildAt(i).setBackgroundResource(R.drawable.smarthome_myele_room_normal);
                        }
                    }
                }else{
                    for(int i = 0,size = mListViewMode.getChildCount(); i <size ;i ++){
                        if(i == position){
                            mListViewMode.getChildAt(i).setBackgroundResource(R.drawable.smarthome_myele_room_highlight);
                        }else{
                            mListViewMode.getChildAt(i).setBackgroundResource(R.drawable.smarthome_myele_room_normal);
                        }
                    }
                }
            }
        });
        mListViewMode.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                setModeItemBackground(position);
                ElectricalOperationMode mode = mAdapterMode.getData()[position];
                mAdapterModeEle.setData(null);
                mAdapterModeEle.notifyDataSetChanged();
                if(mode.ModelElectricalList == null || mode.ModelElectricalList.isEmpty()){
                    if(mIsLoadBack){
                        mCacheMode = mode;
                        requestModeElectricalList(mode.ModelGuid);
                    }
                }else{
                    initModeEleListView(mode);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                
            }
        });
        
        mListViewMode.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                boolean ret = false;
                int action = event.getAction();
                if (action == KeyEvent.ACTION_DOWN) {
                    
                    if(!mIsLoadBack){
                        Toast.makeText(GDSmartHomeModeActivity.this, getString(R.string.family_text_requestting), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    switch (keyCode) {
                    
                    case KeyEvent.KEYCODE_DPAD_LEFT: {
                        int selectedIndex = mListViewMode
                                .getSelectedItemPosition();
                        if (selectedIndex == 0 && mPageNumberModel > 0) {
                            loadPrevPage();
                            ret = true;
                        }
                        break;
                    }
                    case KeyEvent.KEYCODE_DPAD_RIGHT: {
                        int selectedIndex = mListViewMode
                                .getSelectedItemPosition();
                        if (selectedIndex == (mPageSizeMode - 1)
                                && mPageNumberModel < mPageCountModel - 1) {
                            loadNextPage();
                            ret = true;
                        }
                        break;
                    }
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                        int selectedIndex = mListViewMode.getSelectedItemPosition();
                        ElectricalOperationMode mode = mAdapterMode.getData()[selectedIndex];
                        executeMode(mode);
                        break;
                    }

                }
                return ret;
            }
        });
    }
    private void setModeItemBackground (int position){
        for(int i = 0,size = mListViewMode.getChildCount(); i <size ;i ++){
            if(i == position){
                mListViewMode.getChildAt(i).setBackgroundResource(R.drawable.smart_home_model_item_selecter);
            }else{
                mListViewMode.getChildAt(i).setBackgroundResource(R.drawable.smarthome_myele_room_normal);
            }
        }
        
    }
    private void displayJianTou() {
        if (mPageCountModel == 1) {
            mRightJianTou
                    .setBackgroundResource(R.drawable.smarthome_top_right_jantou_normal);
            mLeftJianTou
                    .setBackgroundResource(R.drawable.smarthome_top_left_jantou_normal);
        } else if (mPageCountModel > 1) {
            if (mPageNumberModel == 0) {
                mLeftJianTou
                        .setBackgroundResource(R.drawable.smarthome_top_left_jantou_normal);
                mRightJianTou
                        .setBackgroundResource(R.drawable.smarthome_top_right_jantou_highlight);
            } else if (mPageNumberModel > 0) {
                mLeftJianTou
                        .setBackgroundResource(R.drawable.smarthome_top_left_jantou_highlight);
                if (mPageNumberModel < mPageCountModel - 1) {
                    mRightJianTou
                            .setBackgroundResource(R.drawable.smarthome_top_right_jantou_highlight);
                } else {
                    mRightJianTou
                            .setBackgroundResource(R.drawable.smarthome_top_right_jantou_normal);
                }
            }
        }
    }
    
    
    private void initListViewMode(List<ElectricalOperationMode> lists) {
        if (lists == null || lists.isEmpty()) {
            mLayoutNoMode.setVisibility(View.VISIBLE);
            mLayoutMode.setVisibility(View.INVISIBLE);
            TextView note = (TextView) findViewById(R.id.no_mode_note);
            note.setText(Html.fromHtml(getString(R.string.family_text_no_mode)));
        } else {
            mLayoutNoMode.setVisibility(View.INVISIBLE);
            mLayoutMode.setVisibility(View.VISIBLE);
            constructModePages(lists);
            ElectricalOperationMode[] page = mPageModes.get(mPageNumberModel);
            mAdapterMode.setData(page);
            mAdapterMode.notifyDataSetChanged();
            
            displayJianTou();
        }
    }
    private void initModeEleListView(ElectricalOperationMode data){
        mAdapterModeEle.setData(null);
        mAdapterModeEle.notifyDataSetChanged();
        if(data.ModelElectricalList == null || data.ModelElectricalList.isEmpty()){
            return;
        }
        constructRoomElePages(data);
        displayRoomElePage(0, data);
    }
    private void displayRoomElePage(int pageNumber,ElectricalOperationMode data){
        mAdapterModeEle.setData(data.ModelElectricalPageList.get(pageNumber));
        mAdapterModeEle.notifyDataSetChanged();
        indicator.setPageCount(mElePageCount);
        indicator.setCurrentPage(mElePageNumber);
    }
    private void loadPrevPage() {

        mPageNumberModel--;

        ElectricalOperationMode[] page = mPageModes.get(mPageNumberModel);
        mAdapterMode.setData(page);
        mListViewMode.clearChoices();
        mListViewMode.setSelection(page.length);
        mAdapterMode.notifyDataSetChanged();
        displayJianTou();
    }
    private void loadNextPage() {

        mPageNumberModel++;

        ElectricalOperationMode[] page = mPageModes.get(mPageNumberModel);
        mAdapterMode.setData(page);
        mListViewMode.clearChoices();
        mListViewMode.setSelection(0);
        mAdapterMode.notifyDataSetChanged();
        displayJianTou();

    }
    private void loadElePrevPage() {

        mElePageNumber--;

        ElectricalOperationMode mode = mAdapterMode.getData()[mListViewMode.getSelectedItemPosition()];
        ModeElectrical [] eles =mode.ModelElectricalPageList.get(mElePageNumber);
        mAdapterModeEle.setData(eles);
        mListViewMoleEle.clearChoices();
        mListViewMoleEle.setSelection(eles.length);
        mAdapterModeEle.notifyDataSetChanged();
        indicator.setCurrentPage(mElePageNumber);
    }
    private void loadEleNextPage() {
        
        mElePageNumber ++;
        ElectricalOperationMode mode = mAdapterMode.getData()[mListViewMode.getSelectedItemPosition()];
        ModeElectrical [] eles =mode.ModelElectricalPageList.get(mElePageNumber);
        mAdapterModeEle.setData(eles);
        mListViewMoleEle.clearChoices();
        mListViewMoleEle.setSelection(0);
        mAdapterModeEle.notifyDataSetChanged();
        indicator.setCurrentPage(mElePageNumber);
    }
    
    private void constructRoomElePages(ElectricalOperationMode modeData) {
        int size = modeData.ModelElectricalList.size();
        mEleCount = size;
        mElePageNumber = 0;
        if (size == 0) {
            mElePageNumber = size;
            mElePageCount = size;
            return;
        }

        mElePageCount = size / ElePageSize;
        if (size % ElePageSize > 0) {
            mElePageCount++;
        }

        modeData.ModelElectricalPageList = new ArrayList<ModeElectrical[]>();

        int index = 0;
        for (int i = 0; i < mElePageCount; i++) {
            int pageSize = Math.min(ElePageSize, size - index);

            ModeElectrical[] page = new ModeElectrical[pageSize];
            for (int j = 0; j < pageSize; j++) {
                page[j] = modeData.ModelElectricalList.get(index);
                index++;
            }
            modeData.ModelElectricalPageList.add(page);
        }
    }
    private void constructModePages(List<ElectricalOperationMode> lists) {
        int size = lists.size();
        mCountModel = size;
        mPageNumberModel = 0;
        if (size == 0) {
            mPageNumberModel = size;
            mPageCountModel = size;
            return;
        }

        mPageCountModel = size / mPageSizeMode;
        if (size % mPageSizeMode > 0) {
            mPageCountModel++;
        }

        mPageModes = new ArrayList<ElectricalOperationMode[]>();

        int index = 0;
        for (int i = 0; i < mPageCountModel; i++) {
            int pageSize = Math.min(mPageSizeMode, size - index);

            ElectricalOperationMode[] page = new ElectricalOperationMode[pageSize];
            for (int j = 0; j < pageSize; j++) {
                page[j] = lists.get(index);
                index++;
            }

            mPageModes.add(page);
        }
    }
    
    class ModeAdapter extends BaseAdapter{
        
        private ElectricalOperationMode [] data;
        @Override
        public int getCount() {
            return data == null ? 0 : data.length;
        }
        public void setData(ElectricalOperationMode [] data){
            this.data = data;
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
            TextView modeTitle;
            if(convertView == null){
                convertView = LayoutInflater.from(GDSmartHomeModeActivity.this).inflate(R.layout.smart_home_model_itme,  null);
                modeTitle = (TextView) convertView.findViewById(R.id.model_name);
                convertView.setTag(modeTitle);
            }else {
                modeTitle = (TextView) convertView.getTag();
            }
            modeTitle.setText(data[position].ModelName);
            return convertView;
        }
        
        public ElectricalOperationMode [] getData (){
            return data;
        }
    }
    class ModeElectricalAdapter extends BaseAdapter{
        
        private ModeElectrical [] data;
        @Override
        public int getCount() {
            return data == null ? 0 : data.length;
        }
        public void setData(ModeElectrical [] data){
            this.data = data;
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
            ViewHolder v = null;
            if(convertView == null){
                convertView = LayoutInflater.from(GDSmartHomeModeActivity.this).inflate(R.layout.smart_home_model_equ_itme,  null);
                v = new ViewHolder();
                v.mEleTitle = (TextView) convertView.findViewById(R.id.ele_title);
                v.mElePic = (ImageView) convertView.findViewById(R.id.ele_pic);
                v.mStatu = (TextView) convertView.findViewById(R.id.ele_status);
                convertView.setTag(v);
            }else {
                v  = (ViewHolder) convertView.getTag();
            }
            ModeElectrical electrical = data [position];
            v.mEleTitle.setText(electrical.DeviceName);
            //v.mElePic
            if(electrical.Oper.equals(ELE_OFF)){
                v.mStatu.setText(getString(R.string.family_text_turn_off));
                
            }else if(electrical.Oper.equals(ELE_ON)){
                v.mStatu.setText(getString(R.string.family_text_turn_on));
            }
            v.mElePic.setImageResource(getPicId(electrical));
            return convertView;
        }
        
        public ModeElectrical [] getData (){
            return data;
        }
        
        class ViewHolder{
            TextView mEleTitle;
            ImageView mElePic;
            TextView mStatu;
        }
    }
    private int getPicId (ModeElectrical modeEle){
        
        int picId = R.drawable.common_icon_equ_defult;
        if(mAllElectricals == null)
            return picId;
        for(RoomEletrical eletrical : mAllElectricals){
            if(eletrical.DeviceGuid.equals(modeEle.DeviceGuid)){
                StringBuilder sb = new StringBuilder();
                sb.append("common_icon_equ_");
                
                String num = "defult";
                try {
                   num = String.valueOf(Integer.parseInt(eletrical.DevicePic, 16));
                   if(num.length() == 1){
                       num = "0"+ num;
                   }
                } catch (Exception e) {
                }
                sb.append(num);
                picId =  getResources().getIdentifier(sb.toString(), "drawable", getPackageName());
                if(picId == 0){
                    picId = R.drawable.common_icon_equ_defult;
                }
            }
        }
        return picId;
        
    }
    
    @Override
    protected void onServiceStart() {
        super.onServiceStart();
        requestAllElectrical();
        requestModelList();
    }
    
    @Override
    public void notifyEvent(int type, Object event) {
        super.notifyEvent(type, event);
        EventData.GuodianEvent guodianEvent = (EventData.GuodianEvent) event;
        if( EventData.EVENT_GUODIAN_DATA == type){
            if(GDConstract.DATATYPE_MODEL_LIST == guodianEvent.Type){
                List<ElectricalOperationMode> modeList = (List<ElectricalOperationMode>) guodianEvent.Data;
                initListViewMode(modeList);
                
            }else if(GDConstract.DATATYPE_MODEL_ELECTRICAL_LIST == guodianEvent.Type){
                mHandler.removeCallbacks(mTimeOutTask);
                List<ModeElectrical> eles = (List<ModeElectrical>) guodianEvent.Data;
                mCacheMode.ModelElectricalList = eles;
                initModeEleListView(mCacheMode);
                mIsLoadBack = true;
                
            }else if(GDConstract.DATATYPE_EXECUTE_MODE == guodianEvent.Type){
                mHandler.removeCallbacks(mTimeOutTask);
                mIsLoadBack = true;
                ResultData result = (ResultData) guodianEvent.Data;
                if(result != null){
                   if("true".equals(result.Result)){
                       Toast.makeText(this,getString(R.string.family_text_execute_success) , Toast.LENGTH_SHORT).show();
                       return ;
                   }
                }
                Toast.makeText(this,getString(R.string.family_text_execute_fail) , Toast.LENGTH_SHORT).show();
                
            }else if(GDConstract.DATATYPE_EQUMENTLIST == guodianEvent.Type){
                mAllElectricals = (List<RoomEletrical>) guodianEvent.Data;
                if(mAllElectricals != null && !mAllElectricals.isEmpty()){
                    mAdapterModeEle.notifyDataSetChanged();
                }
            } 
        }else if( EventData.EVENT_GUODIAN_DATA_ERROR == type){
            if(GDConstract.DATATYPE_MODEL_ELECTRICAL_LIST == guodianEvent.Type){
                mHandler.removeCallbacks(mTimeOutTask);
                mIsLoadBack = true;
                String error = (String) guodianEvent.Data;
                Toast.makeText(this,error , Toast.LENGTH_SHORT).show();
            }else if(GDConstract.DATATYPE_EXECUTE_MODE == guodianEvent.Type){
                mHandler.removeCallbacks(mTimeOutTask);
                mIsLoadBack = true;
                String error = (String) guodianEvent.Data;
                Toast.makeText(this,error , Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private void requestModelList(){
        LoginData loginData = mService.getLoginData();
        if (loginData == null)
            return;
        
        if(loginData.CtrlNo == null)
            return ;
        String ctrlSeridno = loginData.CtrlNo.CtrilSerialNo;
        Map<String, String> params = new HashMap<String, String>();
        params.put(JsonTag.TAGCTRL_SeridNo, ctrlSeridno);
        mService.requestPowerData(GDConstract.DATATYPE_MODEL_LIST, params);
    }
    
    private void requestModeElectricalList(String modeGuid){
        LoginData loginData = mService.getLoginData();
        if (loginData == null)
            return;
        
        if(loginData.CtrlNo == null)
            return ;
        
        String ctrlSeridno = loginData.CtrlNo.CtrilSerialNo;
        Map<String, String> params = new HashMap<String, String>();
        params.put(JsonTag.TAGCTRL_SeridNo, ctrlSeridno);
        params.put(JsonTag.TAGModeGuid, modeGuid);
        mIsLoadBack = false;
        mHandler.postDelayed(mTimeOutTask, 1000 * 30);
        mService.requestPowerData(GDConstract.DATATYPE_MODEL_ELECTRICAL_LIST, params);
    }
    
    private void executeMode(ElectricalOperationMode mode){
        LoginData loginData = mService.getLoginData();
        if (loginData == null)
            return;
        
        if(loginData.CtrlNo == null)
            return ;
        
        String ctrlSeridno = loginData.CtrlNo.CtrilSerialNo;
        Map<String, String> params = new HashMap<String, String>();
        params.put(JsonTag.TAGCTRL_SeridNo, ctrlSeridno);
        params.put(JsonTag.TAGModeGuid, mode.ModelGuid);
        params.put(JsonTag.TAGModeId, mode.ModelId);
        mIsLoadBack = false;
        mHandler.postDelayed(mTimeOutTask, 1000 * 30);
        mService.requestPowerData(GDConstract.DATATYPE_EXECUTE_MODE, params);
    }
    
    private void requestAllElectrical(){
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
    Runnable mTimeOutTask = new Runnable() {
        
        @Override
        public void run() {
            if(!mIsLoadBack)
                Toast.makeText(GDSmartHomeModeActivity.this, getString(R.string.family_text_request_timeout), Toast.LENGTH_SHORT).show();
            mIsLoadBack = true;
        }
    };
    protected void onStop() {
        super.onStop();
        mHandler.removeCallbacks(mTimeOutTask);
    };
    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}