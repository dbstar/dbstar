package com.dbstar.guodian.app.smarthome;

import java.util.ArrayList;
import java.util.List;

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

import com.dbstar.R;
import com.dbstar.guodian.app.base.GDSmartActivity;
import com.dbstar.guodian.data.JsonTag;
import com.dbstar.guodian.data.ResultData;
import com.dbstar.guodian.data.RoomData;
import com.dbstar.guodian.data.RoomData.ElecRefreshResponse;
import com.dbstar.guodian.data.RoomData.ElecTurnResponse;
import com.dbstar.guodian.data.RoomData.RoomEletrical;
import com.dbstar.guodian.engine1.GDRequestType;
import com.dbstar.guodian.engine1.RequestParams;
import com.dbstar.model.EventData;
import com.dbstar.widget.CircleFlowIndicator;

public class GDSmartHomeMyEleActivity extends GDSmartActivity {
    
    public static final String SOCKET_ELE_ON  = "01";
    public static final String SOCKET_ELE_OFF = "00";
    public static final String SOCKET_ELE_INVALID = "02";
    
    public static final String SMART_ELE_ON  = "03";
    public static final String SMART_ELE_OFF = "04";
    public static final String SMART_ELE_STOP = "02";
    
    public static final String DEVICE_TYPE_CURTAIN = "06";
    public static final String DEVICE_TYPE_DEFAULT= "defualt";
    public static final String DEVICE_TYPE_LIGHTING = "03";
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
            showErrorMsg( R.string.no_login);
            return;
        }
        mSystemFlag = "sml";
        mRequestMethodId = "m007f002";
        RequestParams params =  new RequestParams(GDRequestType.DATATYPE_ROOM_LIST);
        params.put(RequestParams.KEY_SYSTEM_FLAG, mSystemFlag);
        params.put(RequestParams.KEY_METHODID, mRequestMethodId);
        params.put(JsonTag.TAGCTRL_SeridNo, mCtrlSeridNo);
        requestData( params);
    }
    
    private void requestRoomEleList(String roomGuid){
        if(mCtrlSeridNo == null){
            showErrorMsg( R.string.loading_room_ele_list_fail);
            return;
        }
        mSystemFlag = "sml";
        mRequestMethodId = "m007f001";
        RequestParams params =  new RequestParams(GDRequestType.DATATYPE_ROOM_ELECTRICAL_LIST);
        params.put(RequestParams.KEY_SYSTEM_FLAG, mSystemFlag);
        params.put(RequestParams.KEY_METHODID, mRequestMethodId);
        params.put(JsonTag.TAGCTRL_SeridNo, mCtrlSeridNo);
        params.put(JsonTag.TAGRoomGuid, roomGuid);
        requestDataNotShowDialog(params);
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
            if(GDRequestType.DATATYPE_ROOM_LIST == guodianEvent.Type){
                mListRoom = (ArrayList<RoomData>) guodianEvent.Data;
                initRoomListView();
            }else if(GDRequestType.DATATYPE_ROOM_ELECTRICAL_LIST == guodianEvent.Type){
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
            }else if(GDRequestType.DATATYPE_TUNN_ON_OFF_ELECTRICAL == guodianEvent.Type){
                ElecTurnResponse elecTurnResponse = (ElecTurnResponse) guodianEvent.Data;
                updateEleSwitch(elecTurnResponse);
            }else if(GDRequestType.DATATYPE_TUNN_ON_OFF_SMART_ELECTRICAL== guodianEvent.Type){
                ResultData resultData = (ResultData) guodianEvent.Data;
                    if(!"true".equals(resultData.Result)){
                        showErrorMsg(resultData.Reason);
                        return;
                    }
            }else if(GDRequestType.DATATYPE_REFRESH_ELECTRICAL == guodianEvent.Type){
                ElecRefreshResponse elecRefreshResponse = (ElecRefreshResponse) guodianEvent.Data;
                updateEleInfo(elecRefreshResponse);
            }
        }else if(type == EventData.EVENT_GUODIAN_DATA_ERROR){
            if(GDRequestType.DATATYPE_TUNN_ON_OFF_ELECTRICAL == guodianEvent.Type){
                showErrorMsg(R.string.server_error);
            }else if(GDRequestType.DATATYPE_REFRESH_ELECTRICAL == guodianEvent.Type){
                showErrorMsg(R.string.server_error);
            }else if(GDRequestType.DATATYPE_ROOM_LIST == guodianEvent.Type){
                showErrorMsg(R.string.loading_error);
            }else if(GDRequestType.DATATYPE_ROOM_ELECTRICAL_LIST == guodianEvent.Type){
                showErrorMsg(R.string.loading_room_ele_list_fail);
            }else if(GDRequestType.DATATYPE_TUNN_ON_OFF_SMART_ELECTRICAL == guodianEvent.Type){
                showErrorMsg(R.string.server_error);
            }else{
                showErrorMsg(R.string.loading_error);
            }
            return;
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
                if(eletrical.AdapterFlag.equals(SOCKET_ELE_ON)){
                    eletrical.AdapterFlag = SOCKET_ELE_OFF;
                }else if(eletrical.AdapterFlag.equals(SOCKET_ELE_OFF)){
                    eletrical.AdapterFlag = SOCKET_ELE_ON;
                }
                if(elecTurnResponse.RealTimePowerValue != null){
                    eletrical.RealTimePower = elecTurnResponse.RealTimePowerValue;
                }
                mRoomEleAdapter.notifyDataSetChanged();
            }else{
                showErrorMsg(elecTurnResponse.Reason);
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
            
            RoomEletrical eletrical = data [position];
            if(eletrical.DevicePic.equals(DEVICE_TYPE_CURTAIN)){
                CurtainHolder vh = null;
                
                if(convertView != null && convertView.findViewById(R.id.on) != null){
                    myView = convertView;
                    v= (CurtainHolder) convertView.getTag();
                    
                }else{
                    convertView = LayoutInflater
                            .from(getApplicationContext())
                            .inflate(R.layout.smart_home_my_elec_equ_curtain_itme, null);
                    myView = convertView;
                    v = new CurtainHolder();
                    vh = (CurtainHolder) v;
                    vh.mEleTitle = (TextView)  convertView
                            .findViewById(R.id.ele_title);
                    
                    v.mRefresh = (Button) myView
                            .findViewById(R.id.smart_home_refresh);

                    vh.mDayAmount = (TextView) convertView
                            .findViewById(R.id.smarthome_ele_day_amount);
                    vh.mMonthAmount = (TextView) convertView
                            .findViewById(R.id.smarthome_ele_month_amount);
                    vh.mDevicePic = (ImageView) convertView
                            .findViewById(R.id.smarthome_bottom_device_pic);
                    vh.mOn = (Button) convertView.findViewById(R.id.on);
                    vh.mStop = (Button) convertView.findViewById(R.id.stop);
                    vh.mOff = (Button) convertView.findViewById(R.id.off);
                    
                    vh.mOn.setOnKeyListener(new OnKeyListener() {
                        
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            
                            return handOnEleListOnKeyLinstener(v, keyCode,
                                    event);
                        }
                    });
                    vh.mStop.setOnKeyListener(new OnKeyListener() {
                        
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            return handOnEleListOnKeyLinstener(v, keyCode,
                                    event);
                        }
                    });
                    vh.mOff.setOnKeyListener(new OnKeyListener() {
                        
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            return handOnEleListOnKeyLinstener(v, keyCode,
                                    event);
                        }
                    });
                    convertView.setTag(vh);
                }
                
                vh = (CurtainHolder) v;
                
                vh.mRefresh.setOnFocusChangeListener(new OnFocusChangeListener() {
                    
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                      myView.findViewById(R.id.on).requestFocus();
                    }
                });
                
                vh.mOn.setOnFocusChangeListener(new OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        mEleListselectedIndex = index;
                        handOnEleListFocusLinstener(hasFocus, myView);
                    }
                });
           

                vh.mOff.setOnFocusChangeListener(new OnFocusChangeListener() {
    
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        mEleListselectedIndex = index;
                        handOnEleListFocusLinstener(hasFocus, myView);
                    }
                });
                vh.mStop.setOnFocusChangeListener(new OnFocusChangeListener() {
                    
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        mEleListselectedIndex = index;
                        handOnEleListFocusLinstener(hasFocus, myView);
                    }
                });
                
            }else{
                if(convertView != null  && convertView.findViewById(R.id.on) == null){
                    myView = convertView;
                    v = (EquViewHolder) convertView.getTag();
                }else{
                    convertView = LayoutInflater
                            .from(getApplicationContext())
                            .inflate(R.layout.smart_home_my_elec_equ_itme, null);
                    myView = convertView;
                    v = new EquViewHolder();
                    v.mEleTitle = (TextView) myView
                            .findViewById(R.id.ele_title);
                    v.mTurn = (Button) myView
                            .findViewById(R.id.smart_home_on_off);
                    v.mRefresh = (Button) myView
                            .findViewById(R.id.smart_home_refresh);

                    v.mDayAmount = (TextView) convertView
                            .findViewById(R.id.smarthome_ele_day_amount);
                    v.mMonthAmount = (TextView) convertView
                            .findViewById(R.id.smarthome_ele_month_amount);
                    v.mRealTimePower = (TextView) convertView
                            .findViewById(R.id.smarthome_ele_reatime_power);
                    v.mDevicePic = (ImageView) convertView
                            .findViewById(R.id.smarthome_bottom_device_pic);
                    v.mTurn.setOnKeyListener(new OnKeyListener() {

                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            return handOnEleListOnKeyLinstener(v, keyCode,
                                    event);
                        }
                    });
                    v.mRefresh.setOnKeyListener(new OnKeyListener() {

                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            return handOnEleListOnKeyLinstener(v, keyCode,
                                    event);
                        }
                    });
                    convertView.setTag(v);
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

                if (eletrical.AdapterFlag.equals(SOCKET_ELE_ON)) {
                    v.mTurn.setBackgroundResource(R.drawable.smart_home_on_off_on_selecter);
                } else if (eletrical.AdapterFlag.equals(SOCKET_ELE_OFF)) {
                    v.mTurn.setBackgroundResource(R.drawable.smart_home_on_off_offselecter);
                } else if (eletrical.AdapterFlag.equals(SOCKET_ELE_INVALID)) {
                    v.mTurn.setBackgroundDrawable(null);
                    v.mTurn.setText("");
                }
                
                if (eletrical.RealTimePower != null
                        && !eletrical.RealTimePower.isEmpty()) {
                    v.mRealTimePower
                            .setText(String.valueOf(Math.round(Float
                                    .parseFloat(eletrical.RealTimePower.trim()) * 1000)));
                } else {
                    v.mRealTimePower.setText("0.0");
                }
            }
                v.mEleTitle.setText(eletrical.DeviceName);
                v.mDayAmount.setText(eletrical.EleAmountOfDay);
                v.mMonthAmount.setText(eletrical.EleAmountOfMonth);
              

                StringBuilder sb = new StringBuilder();
                sb.append("common_icon_equ_");

                String num = "defult";
                try {
                    num = String.valueOf(Integer.parseInt(eletrical.DevicePic,
                            16));
                    if (num.length() == 1) {
                        num = "0" + num;
                    }
                } catch (Exception e) {
                }
                sb.append(num);

                int picId = getResources().getIdentifier(sb.toString(),
                        "drawable", getPackageName());
                if (picId == 0) {
                    v.mDevicePic
                            .setImageResource(R.drawable.common_icon_equ_defult);
                } else {
                    v.mDevicePic.setImageResource(picId);
                }
           
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
        
        class CurtainHolder extends EquViewHolder{
            Button mOn;
            Button mStop;
            Button mOff;
        }
    }

    private void handOnEleListFocusLinstener(boolean hasFocus,View v){
        if (hasFocus) {
            if(v.findViewById(R.id.on) != null){
                v.setBackgroundResource(R.drawable.smart_home_mode_equ_focus_bg);
            }else{
                v.setBackgroundResource(R.drawable.smart_home_room_ele_focus_bg);
            }
        } else
            if(v.findViewById(R.id.on) != null){
                v.setBackgroundResource(R.drawable.smarthome_model_ele_nomarl);
            }else{
                v.setBackgroundResource(R.drawable.smarthome_myele_bottom_equ_normal);
            }
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
                        requestTurnOnOrOff(DEVICE_TYPE_DEFAULT,"");
                    } else if (v.getId() == R.id.smart_home_refresh) {
                        requestRefreshElectrical();
                    }else if(v.getId() == R.id.on){
                        requestTurnOnOrOff(DEVICE_TYPE_CURTAIN,SMART_ELE_ON);
                    }else if(v.getId() == R.id.stop){
                        requestTurnOnOrOff(DEVICE_TYPE_CURTAIN,SMART_ELE_STOP);
                    }else if(v.getId() == R.id.off){
                        requestTurnOnOrOff(DEVICE_TYPE_CURTAIN,SMART_ELE_OFF);
                    }

                break;
            }
            }

        }

        return false;

    }
    
    private void requestTurnOnOrOff(String deviceType,String oper){
       if(mCtrlSeridNo == null){
           showErrorMsg(R.string.no_login);
           return;
       }
       
       
       RoomEletrical eletrical = mRoomEleAdapter.getEletricals()[mEleListselectedIndex];
       
       String adapter_seridno = eletrical.AdapterSeridNo;
       String device_guid = eletrical.DeviceGuid;
       String typeid = eletrical.EleDeviceCode;
       RequestParams params = null;
       mSystemFlag = "sml";
       mRequestMethodId = "m001f014";
       int requestType = GDRequestType.DATATYPE_TUNN_ON_OFF_ELECTRICAL;
       if(DEVICE_TYPE_DEFAULT.equals(deviceType)){
           params = new RequestParams(requestType);
           oper = SOCKET_ELE_INVALID;
           if(eletrical.AdapterFlag.equals(SOCKET_ELE_ON)){
               oper = SOCKET_ELE_OFF;
           }else if(eletrical.AdapterFlag.equals(SOCKET_ELE_OFF)){
               oper = SOCKET_ELE_ON;
           }
       }else if(DEVICE_TYPE_CURTAIN.equals(deviceType)){
           requestType = GDRequestType.DATATYPE_TUNN_ON_OFF_SMART_ELECTRICAL;
           mRequestMethodId = "m001f001";
           params = new RequestParams(requestType);
           params.put("device_iconid", deviceType);
       }
       params.put(RequestParams.KEY_SYSTEM_FLAG, mSystemFlag);
       params.put(RequestParams.KEY_METHODID, mRequestMethodId);
       params.put(JsonTag.TAGCTRL_SeridNo, mCtrlSeridNo);
       params.put(JsonTag.TAGAdapterSeridNo, adapter_seridno);
       params.put(JsonTag.TAGDeviceGuid, device_guid);
       params.put(JsonTag.TAGTypeId, typeid);
       params.put(JsonTag.TAGOper, oper);
       requestData(params);
    }
    
    private void requestRefreshElectrical(){
        RoomEletrical eletrical = mRoomEleAdapter.getEletricals()[mEleListselectedIndex];
        if(mCtrlSeridNo == null){
            showErrorMsg(R.string.no_login);
            return;
        }
        String adapter_seridno = eletrical.AdapterSeridNo;
        String device_guid = eletrical.DeviceGuid;
        String typeid = eletrical.EleDeviceCode;
        
        mSystemFlag = "sml";
        mRequestMethodId = "m001f011";
        RequestParams params = new RequestParams(GDRequestType.DATATYPE_REFRESH_ELECTRICAL);
        params.put(RequestParams.KEY_SYSTEM_FLAG, mSystemFlag);
        params.put(RequestParams.KEY_METHODID, mRequestMethodId);
        params.put(JsonTag.TAGCTRL_SeridNo, mCtrlSeridNo);
        params.put(JsonTag.TAGAdapterSeridNo, adapter_seridno);
        params.put(JsonTag.TAGDeviceGuid, device_guid);
        params.put(JsonTag.TAGTypeId, typeid);
        requestData(params);
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
