package com.dbstar.guodian.app.smarthome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dbstar.R;
import com.dbstar.guodian.app.base.GDSmartActivity;
import com.dbstar.guodian.data.JsonTag;
import com.dbstar.guodian.data.RoomData;
import com.dbstar.guodian.data.RoomData.ElecRefreshResponse;
import com.dbstar.guodian.data.RoomData.ElecTurnResponse;
import com.dbstar.guodian.data.RoomData.RoomEletrical;
import com.dbstar.guodian.engine.GDConstract;
import com.dbstar.model.EventData;
import com.dbstar.util.ToastUtil;
import com.dbstar.widget.CircleFlowIndicator;

public class GDSmartHomeMyEleActivity extends GDSmartActivity {
    
    public static final String ELE_ON  = "01";
    public static final String ELE_OFF = "00";
    public static final String ELE_INVALID = "02";
    
    private GridView mRoomsListView;
    private ArrayList<RoomData> mListRoom;
    private ImageView mLeftJianTou;
    private ImageView mRightJianTou;
    private GridView mRoomEquListView;
    private CircleFlowIndicator indicator;
    private LinearLayout mRoomPage;
    private LinearLayout mNoRoomPage;
    private LinearLayout mElePage;
    private LinearLayout mNoElePage;
    private static final int RoomPageSize = 5;
    private static final int ElePageSize = 6;
    
    private ArrayList<RoomData[]> mRoomPagesData;
    private int mRoomPageCount, mRoomPageNumber,mElePageCount,mElePageNumber;
    private int mRoomCount,mEleCount;
    private RoomAdapter mRoomAdapter;
    private RoomEleAdapter mRoomEleAdapter;
    private String mCtrlSeridNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.smart_home_my_electrical);
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
        indicator = (CircleFlowIndicator) findViewById(R.id.indicator);
        mRoomPage = (LinearLayout) findViewById(R.id.room_page);
        mNoRoomPage = (LinearLayout) findViewById(R.id.no_room_page);
        mElePage = (LinearLayout) findViewById(R.id.ele_page);
        mNoElePage = (LinearLayout) findViewById(R.id.no_ele_page);

        mRoomPage.setVisibility(View.INVISIBLE);
        mNoRoomPage.setVisibility(View.INVISIBLE);
        mElePage.setVisibility(View.INVISIBLE);
        mNoElePage.setVisibility(View.INVISIBLE);
        
        mRightJianTou = (ImageView) findViewById(R.id.smart_home_right_jantou);
        mLeftJianTou = (ImageView) findViewById(R.id.smart_home_top_left_jantou);
        mRoomsListView = (GridView) findViewById(R.id.smart_home_top_rooms);

        mRoomEquListView = (GridView) findViewById(R.id.smart_home_bottom_equ);
        mRoomEleAdapter = new RoomEleAdapter();
        mRoomEquListView.setAdapter(mRoomEleAdapter);
        
        
        mRoomEquListView.setFocusable(false);
        mRoomEquListView.setFocusableInTouchMode(false);
        mRoomEquListView.clearFocus();
//        mRoomEquListView.setOnKeyListener(new OnKeyListener() {
//            
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                boolean ret = false;
//                int action = event.getAction();
//                if (action == KeyEvent.ACTION_DOWN) {
//                    switch (keyCode) {
//
//                    case KeyEvent.KEYCODE_DPAD_LEFT: {
//                        int selectedIndex = mRoomEquListView
//                                .getSelectedItemPosition();
//                        if (selectedIndex == 0 && mElePageNumber > 0) {
//                            loadElePrevPage();
//                            ret = true;
//                        }
//                        break;
//                    }
//                    case KeyEvent.KEYCODE_DPAD_RIGHT: {
//                        int selectedIndex = mRoomEquListView
//                                .getSelectedItemPosition();
//                        if (selectedIndex == (ElePageSize - 1)
//                                && mElePageNumber < mElePageCount - 1) {
//                            loadEleNextPage();
//                            ret = true;
//                        }
//                        break;
//                    }
//                    default:
//                        break;
//                    }
//
//                }
//                return ret;
//            }
//        });
        
        mRoomEquListView.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                Log.i("Futao", "onItemSelected = " + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                
            }
        });
        mRoomsListView.setOnFocusChangeListener(new OnFocusChangeListener() {
            
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                int position = mRoomsListView.getSelectedItemPosition();
                if(hasFocus){
                    for(int i = 0,size = mRoomsListView.getChildCount(); i <size ;i ++){
                        if(i == position){
                            mRoomsListView.getChildAt(i).setBackgroundResource(R.drawable.smart_home_room_focus_bg);
                        }else{
                            mRoomsListView.getChildAt(i).setBackgroundResource(R.drawable.smarthome_myele_room_normal);
                        }
                    }
                }else{
                    for(int i = 0,size = mRoomsListView.getChildCount(); i <size ;i ++){
                        if(i == position){
                            mRoomsListView.getChildAt(i).setBackgroundResource(R.drawable.smarthome_myele_room_highlight);
                        }else{
                            mRoomsListView.getChildAt(i).setBackgroundResource(R.drawable.smarthome_myele_room_normal);
                        }
                    }
                }
            }
        });
        mRoomsListView.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                setRoomItemBackground(position);
                RoomData roomData [] = mRoomPagesData.get(mRoomPageNumber);
                RoomData data = roomData [position];
                mRoomEleAdapter.setData(null);
                mRoomEleAdapter.notifyDataSetChanged();
                indicator.setPageCount(0);
                mNoElePage.setVisibility(View.INVISIBLE);
                if(data.EletricalList == null || data.EletricalList.isEmpty()){
                    requestRoomEleList(data.RoomGuid);
                }else{
                    
                    initRoomEleListView();
                }
                
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                
            }
        });
        
        mRoomsListView.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                boolean ret = false;
                int action = event.getAction();
                if (action == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {

                    case KeyEvent.KEYCODE_DPAD_LEFT: {
                        int selectedIndex = mRoomsListView
                                .getSelectedItemPosition();
                        if (selectedIndex == 0 && mRoomPageNumber > 0) {
                            loadPrevPage();
                            ret = true;
                        }
                        break;
                    }
                    case KeyEvent.KEYCODE_DPAD_RIGHT: {
                        int selectedIndex = mRoomsListView
                                .getSelectedItemPosition();
                        if (selectedIndex == (RoomPageSize - 1)
                                && mRoomPageNumber < mRoomPageCount - 1) {
                            loadNextPage();
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
        mRoomAdapter = new RoomAdapter();
        mRoomsListView.setAdapter(mRoomAdapter);
    }

    private void setRoomItemBackground (int position){
        for(int i = 0,size = mRoomsListView.getChildCount(); i <size ;i ++){
            if(i == position){
                mRoomsListView.getChildAt(i).setBackgroundResource(R.drawable.smart_home_room_focus_bg);
            }else{
                mRoomsListView.getChildAt(i).setBackgroundResource(R.drawable.smarthome_myele_room_normal);
            }
        }
        
    }
    @Override
    protected void onServiceStart() {
        super.onServiceStart();
        if(getCtrlNo() != null)
            mCtrlSeridNo = getCtrlNo().CtrilSerialNo;
       requestRoomList();
    }

    private void requestRoomList(){
        if(mCtrlSeridNo == null){
            ToastUtil.showToast(this, R.string.no_login);
            return;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put(JsonTag.TAGCTRL_SeridNo, mCtrlSeridNo);
        requestData(GDConstract.DATATYPE_ROOM_LIST, params);
    }
    
    private void requestRoomEleList(String roomGuid){
        if(mCtrlSeridNo == null){
            ToastUtil.showToast(this, R.string.no_login);
            return;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put(JsonTag.TAGCTRL_SeridNo, mCtrlSeridNo);
        params.put(JsonTag.TAGRoomGuid, roomGuid);
        requestData(GDConstract.DATATYPE_ROOM_ELECTRICAL_LIST, params);
    }
    private void initRoomListView() {
        if (mListRoom == null || mListRoom.isEmpty()) {
            mNoRoomPage.setVisibility(View.VISIBLE);
            mRoomPage.setVisibility(View.INVISIBLE);
            TextView note = (TextView) mNoRoomPage.findViewById(R.id.no_room_note);
            note.setText(Html.fromHtml(getString(R.string.family_text_no_room)));
        } else {
            mNoRoomPage.setVisibility(View.INVISIBLE);
            mRoomPage.setVisibility(View.VISIBLE);
            constructRoomPages(mListRoom);
            displayRoomPage(0);
        }
    }
    
    private void initRoomEleListView(){
        RoomData data = mRoomAdapter.getCurrentRoomPages()[mRoomsListView.getSelectedItemPosition()];
        if(data == null || data.EletricalList == null || data.EletricalList.isEmpty()){
            mElePage.setVisibility(View.INVISIBLE);
            mNoElePage.setVisibility(View.VISIBLE);
            TextView note = (TextView) findViewById(R.id.no_ele_note);
            note.setText(Html.fromHtml(getString(R.string.family_text_no_electrical)));
            return;
        }else{
            mElePage.setVisibility(View.VISIBLE);
            mNoElePage.setVisibility(View.INVISIBLE);
        }
        constructRoomElePages(data);
        displayRoomElePage(0, data);
        
        
    }
        
    @Override
    public void notifyEvent(int type, Object event) {
        EventData.GuodianEvent guodianEvent = (EventData.GuodianEvent) event;
        if (type == EventData.EVENT_GUODIAN_DATA) {
            if(GDConstract.DATATYPE_ROOM_LIST == guodianEvent.Type){
                mListRoom = (ArrayList<RoomData>) guodianEvent.Data;
                initRoomListView();
            }else if(GDConstract.DATATYPE_ROOM_ELECTRICAL_LIST == guodianEvent.Type){
              List<RoomEletrical> eles = (List<RoomEletrical>) guodianEvent.Data;
              RoomData room = null;
              if(eles != null && !eles.isEmpty()){
                String guid =  eles.get(0).RoomGuid;
                for(RoomData data : mListRoom){
                    if(data.RoomGuid.equals(guid)){
                        data.EletricalList = eles;
                       room = data;
                    }
                }
              }
              initRoomEleListView();
            }else if(GDConstract.DATATYPE_TUNN_ON_OFF_ELECTRICAL == guodianEvent.Type){
                ElecTurnResponse elecTurnResponse = (ElecTurnResponse) guodianEvent.Data;
                updateEleSwitch(elecTurnResponse);
            }else if(GDConstract.DATATYPE_REFRESH_ELECTRICAL == guodianEvent.Type){
                ElecRefreshResponse elecRefreshResponse = (ElecRefreshResponse) guodianEvent.Data;
                updateEleInfo(elecRefreshResponse);
            }
        }else if(type == EventData.EVENT_GUODIAN_DATA_ERROR){
            
            if(GDConstract.DATATYPE_TUNN_ON_OFF_ELECTRICAL == guodianEvent.Type){
                ToastUtil.showToast(this, R.string.server_error);
            }else if(GDConstract.DATATYPE_REFRESH_ELECTRICAL == guodianEvent.Type){
                ToastUtil.showToast(this, R.string.server_error);
            }else{
                ToastUtil.showToast(this, R.string.loading_error);
            }
            
        }
        super.notifyEvent(type, event);
    }
    
    private void updateEleInfo( ElecRefreshResponse elecRefreshResponse){
        if(elecRefreshResponse != null){
            RoomEletrical eletrical = mRoomEleAdapter.getEletricals()[mEleListselectedIndex];
            if(elecRefreshResponse.EleAmountOfDay != null){
                eletrical.EleAmountOfDay = elecRefreshResponse.EleAmountOfDay;
            }
            if(elecRefreshResponse.EleAmountOfMonth != null){
                eletrical.EleAmountOfMonth = elecRefreshResponse.EleAmountOfMonth;
            }
            
            if(elecRefreshResponse.RealTimePowerValue != null){
                eletrical.RealTimePower = elecRefreshResponse.RealTimePowerValue;
            }
            
            mRoomEleAdapter.notifyDataSetChanged();
        }
        
    }
    private void updateEleSwitch(ElecTurnResponse elecTurnResponse){
        if(elecTurnResponse != null){
            RoomEletrical eletrical = mRoomEleAdapter.getEletricals()[mEleListselectedIndex];
            if("true".equals(elecTurnResponse.Result)){
                if(eletrical.AdapterFlag.equals(ELE_ON)){
                    eletrical.AdapterFlag = ELE_OFF;
                }else if(eletrical.AdapterFlag.equals(ELE_OFF)){
                    eletrical.AdapterFlag = ELE_ON;
                }
                mRoomEleAdapter.notifyDataSetChanged();
            }else{
                Toast.makeText(this, elecTurnResponse.Reason, Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void loadPrevPage() {

        mRoomPageNumber--;

        RoomData[] rooms = mRoomPagesData.get(mRoomPageNumber);
        mRoomAdapter.setData(rooms);
        mRoomsListView.clearChoices();
        mRoomsListView.setSelection(rooms.length);
        mRoomAdapter.notifyDataSetChanged();
        displayJianTou();
    }
    private void loadElePrevPage() {

        mElePageNumber--;

        RoomData[] rooms =  mRoomAdapter.getCurrentRoomPages();
        RoomData room = rooms[mRoomsListView.getSelectedItemPosition()];
        RoomEletrical [] eles = room.ElePageList.get(mElePageNumber);
        mRoomEleAdapter.setData(eles);
        mRoomEquListView.clearChoices();
        mRoomEquListView.setSelection(rooms.length);
        mRoomEleAdapter.notifyDataSetChanged();
        mRoomEquListView.post(new Runnable() {
            
            @Override
            public void run() {
                  mRoomEquListView.getChildAt(5).findViewById(R.id.smart_home_on_off).requestFocus();
            }
        });
        indicator.setCurrentPage(mElePageNumber);
    }
    private void loadNextPage() {

        mRoomPageNumber++;

        RoomData[] rooms = mRoomPagesData.get(mRoomPageNumber);
        mRoomAdapter.setData(rooms);
        mRoomsListView.clearChoices();
        mRoomsListView.setSelection(0);
        mRoomAdapter.notifyDataSetChanged();
        displayJianTou();

    }
    private void loadEleNextPage() {
        
        mElePageNumber ++;
        RoomData[] rooms =  mRoomAdapter.getCurrentRoomPages();
        RoomData room = rooms[mRoomsListView.getSelectedItemPosition()];
        RoomEletrical [] eles = room.ElePageList.get(mElePageNumber);
        mRoomEleAdapter.setData(eles);
        mRoomEquListView.clearChoices();
        mRoomEquListView.setSelection(0);
        mRoomEleAdapter.notifyDataSetChanged();
        mRoomEquListView.post(new Runnable() {
            
            @Override
            public void run() {
                  mRoomEquListView.getChildAt(0).findViewById(R.id.smart_home_on_off).requestFocus();
            }
        });
        indicator.setCurrentPage(mElePageNumber);
    }
    private void constructRoomElePages(RoomData roomData) {
        int size = roomData.EletricalList.size();
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

        roomData.ElePageList = new ArrayList<RoomEletrical[]>();

        int index = 0;
        for (int i = 0; i < mElePageCount; i++) {
            int pageSize = Math.min(ElePageSize, size - index);

            RoomEletrical[] page = new RoomEletrical[pageSize];
            for (int j = 0; j < pageSize; j++) {
                page[j] = roomData.EletricalList.get(index);
                index++;
            }
            roomData.ElePageList.add(page);
        }
    }
    
    private void constructRoomPages(ArrayList<RoomData> rooms) {
        int size = rooms.size();
        mRoomCount = size;
        mRoomPageNumber = 0;
        if (size == 0) {
            mRoomPageNumber = size;
            mRoomPageCount = size;
            return;
        }

        mRoomPageCount = size / RoomPageSize;
        if (size % RoomPageSize > 0) {
            mRoomPageCount++;
        }

        mRoomPagesData = new ArrayList<RoomData[]>();

        int index = 0;
        for (int i = 0; i < mRoomPageCount; i++) {
            int pageSize = Math.min(RoomPageSize, size - index);

            RoomData[] page = new RoomData[pageSize];
            for (int j = 0; j < pageSize; j++) {
                page[j] = rooms.get(index);
                index++;
            }

            mRoomPagesData.add(page);
        }
    }

    private void displayRoomPage(int pageNumber) {

        RoomData[] page = mRoomPagesData.get(pageNumber);
        mRoomAdapter.setData(page);
        mRoomAdapter.notifyDataSetChanged();
        displayJianTou();
    }
  
    private void displayRoomElePage(int pageNumber,RoomData roomData){
        mRoomEleAdapter.setData(roomData.ElePageList.get(pageNumber));
        mRoomEleAdapter.notifyDataSetChanged();
        indicator.setPageCount(mElePageCount);
        indicator.setCurrentPage(mElePageNumber);
    }
    private void displayJianTou() {
        if (mRoomPageCount == 1) {
            mRightJianTou
                    .setBackgroundResource(R.drawable.smarthome_top_right_jantou_normal);
            mLeftJianTou
                    .setBackgroundResource(R.drawable.smarthome_top_left_jantou_normal);
        } else if (mRoomPageCount > 1) {
            if (mRoomPageNumber == 0) {
                mLeftJianTou
                        .setBackgroundResource(R.drawable.smarthome_top_left_jantou_normal);
                mRightJianTou
                        .setBackgroundResource(R.drawable.smarthome_top_right_jantou_highlight);
            } else if (mRoomPageNumber > 0) {
                mLeftJianTou
                        .setBackgroundResource(R.drawable.smarthome_top_left_jantou_highlight);
                if (mRoomPageNumber < mRoomPageCount - 1) {
                    mRightJianTou
                            .setBackgroundResource(R.drawable.smarthome_top_right_jantou_highlight);
                } else {
                    mRightJianTou
                            .setBackgroundResource(R.drawable.smarthome_top_right_jantou_normal);
                }
            }
        }
    }
    private int mEleListselectedIndex;
    class RoomEleAdapter extends BaseAdapter {

        EquViewHolder v;
        public RoomEletrical data [];
        
        public void setData(RoomEletrical [] data){
            this.data = data;
        }
        @Override
        public int getCount() {
            return data == null ? 0 : data.length;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View myView;
            final int index = position;
            if (convertView == null) {
                convertView = LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.smart_home_my_elec_equ_itme, null);
                myView = convertView;
                v = new EquViewHolder();
                v.mEleTitle = (TextView) myView.findViewById(R.id.ele_title);
                v.mTurn = (Button) myView.findViewById(R.id.smart_home_on_off);
                v.mRefresh = (Button) myView
                        .findViewById(R.id.smart_home_refresh);
                
                v.mDayAmount = (TextView) convertView.findViewById(R.id.smarthome_ele_day_amount);
                v.mMonthAmount = (TextView)convertView. findViewById(R.id.smarthome_ele_month_amount);
                v.mRealTimePower = (TextView)convertView. findViewById(R.id.smarthome_ele_reatime_power);
                v.mDevicePic = (ImageView) convertView.findViewById(R.id.smarthome_bottom_device_pic);
                v.mTurn.setOnKeyListener(new OnKeyListener() {
                    
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        return handOnEleListOnKeyLinstener(v, keyCode, event);
                    }
                });
                v.mRefresh.setOnKeyListener(new OnKeyListener() {
                    
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        return handOnEleListOnKeyLinstener(v, keyCode, event);
                    }
                });
                    convertView.setTag(v);
            } else {
                myView = convertView;
                v = (EquViewHolder) convertView.getTag();
            }
            
            v.mTurn.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    mEleListselectedIndex = index;
                     handOnEleListFocusLinstener(hasFocus, myView);
                }
            });
            v.mRefresh
                    .setOnFocusChangeListener(new OnFocusChangeListener() {

                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            mEleListselectedIndex = index;
                            handOnEleListFocusLinstener(hasFocus, myView);
                        }
                    });
            RoomEletrical eletrical = data [position];
            if(eletrical.AdapterFlag.equals(ELE_ON)){
                v.mTurn.setBackgroundResource(R.drawable.smart_home_on_off_on_selecter);
            }else if(eletrical.AdapterFlag.equals(ELE_OFF)){
                v.mTurn.setBackgroundResource(R.drawable.smart_home_on_off_offselecter);
            }else if(eletrical.AdapterFlag.equals(ELE_INVALID)){
                v.mTurn.setBackgroundDrawable( null);
                v.mTurn.setText("");
            }
            v.mEleTitle.setText(eletrical.DeviceName);
            v.mDayAmount.setText(eletrical.EleAmountOfDay);
            v.mMonthAmount.setText(eletrical.EleAmountOfMonth);
            v.mRealTimePower.setText(eletrical.RealTimePower);
            
            
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
           
            int picId =  getResources().getIdentifier(sb.toString(), "drawable", getPackageName());
            if(picId == 0){
                v.mDevicePic.setImageResource(R.drawable.common_icon_equ_defult);
            }else{
                v.mDevicePic.setImageResource(picId);
            }
            //v.mDayAmount.setText((mElePageNumber * ElePageSize) + position +"");
            return convertView;
        }
        
        public RoomEletrical [] getEletricals(){
            return data;
        }
        class EquViewHolder {
            TextView mEleTitle;
            Button mTurn;
            Button mRefresh;
            TextView mDayAmount;
            TextView mMonthAmount;
            TextView mRealTimePower;
            ImageView mDevicePic;
        }
    }

    private void handOnEleListFocusLinstener(boolean hasFocus,View v){
        if (hasFocus) {
            v.setBackgroundResource(R.drawable.smarthome_myele_bottom_equ_highlight);
        } else
            v.setBackgroundResource(R.drawable.smarthome_myele_bottom_equ_normal);
    }
    
    private boolean handOnEleListOnKeyLinstener(View v, int keyCode,
            KeyEvent event) {
        int action = event.getAction();
        if (action == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {

            case KeyEvent.KEYCODE_DPAD_LEFT: {
                if (mEleListselectedIndex == 0 && mElePageNumber > 0) {
                    loadElePrevPage();
                }
                break;
            }
            case KeyEvent.KEYCODE_DPAD_RIGHT: {
                if (mEleListselectedIndex == (ElePageSize - 1)
                        && mElePageNumber < mElePageCount - 1) {
                    loadEleNextPage();
                }
                break;

            }

            case KeyEvent.KEYCODE_DPAD_CENTER: {
                    if (v.getId() == R.id.smart_home_on_off) {
                        requestTurnOnOrOff();
                    } else if (v.getId() == R.id.smart_home_refresh) {
                        requestRefreshElectrical();
                    }

                break;
            }
            }

        }

        return false;

    }
    
    private void requestTurnOnOrOff(){
       RoomEletrical eletrical = mRoomEleAdapter.getEletricals()[mEleListselectedIndex];
       if(mCtrlSeridNo == null){
           ToastUtil.showToast(this, R.string.no_login);
           return;
       }
       String adapter_seridno = eletrical.AdapterSeridNo;
       String device_guid = eletrical.DeviceGuid;
       String typeid = eletrical.EleDeviceCode;
       
       
       String oper = ELE_INVALID;
       if(eletrical.AdapterFlag.equals(ELE_ON)){
           oper = ELE_OFF;
       }else if(eletrical.AdapterFlag.equals(ELE_OFF)){
           oper = ELE_ON;
       }
       
       Map<String, String> params = new HashMap<String, String>();
       params.put(JsonTag.TAGCTRL_SeridNo, mCtrlSeridNo);
       params.put(JsonTag.TAGAdapterSeridNo, adapter_seridno);
       params.put(JsonTag.TAGDeviceGuid, device_guid);
       params.put(JsonTag.TAGTypeId, typeid);
       params.put(JsonTag.TAGOper, oper);
       requestData(GDConstract.DATATYPE_TUNN_ON_OFF_ELECTRICAL, params);
    }
    
    private void requestRefreshElectrical(){
        RoomEletrical eletrical = mRoomEleAdapter.getEletricals()[mEleListselectedIndex];
        if(mCtrlSeridNo == null){
            ToastUtil.showToast(this, R.string.no_login);
            return;
        }
        String adapter_seridno = eletrical.AdapterSeridNo;
        String device_guid = eletrical.DeviceGuid;
        String typeid = eletrical.EleDeviceCode;
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(JsonTag.TAGCTRL_SeridNo, mCtrlSeridNo);
        params.put(JsonTag.TAGAdapterSeridNo, adapter_seridno);
        params.put(JsonTag.TAGDeviceGuid, device_guid);
        params.put(JsonTag.TAGTypeId, typeid);
        requestData(GDConstract.DATATYPE_REFRESH_ELECTRICAL, params);
    }
    class RoomAdapter extends BaseAdapter {

        public RoomData[] rooms = null;
        
        ViewHolder v;

        @Override
        public int getCount() {
            return rooms == null ? 0 : rooms.length;
        }

        public void setData(RoomData[] rooms) {
            this.rooms = rooms;
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
            if (convertView == null) {
                convertView = LayoutInflater.from(getApplication()).inflate(
                        R.layout.smart_home_my_ele_rooms_itme, null);
                v = new ViewHolder();
                v.rootTileView = (TextView) convertView
                        .findViewById(R.id.room_title);
                convertView.setTag(v);

            } else {
                v = (ViewHolder) convertView.getTag();
            }
            v.rootTileView.setText(rooms[position].RoomName);
            return convertView;
        }
        public RoomData [] getCurrentRoomPages(){
            return rooms;
        }
        class ViewHolder {
            TextView rootTileView;
        }

    }

    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    
}
