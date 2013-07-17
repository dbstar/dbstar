package com.dbstar.guodian.app.smarthome;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dbstar.R;
import com.dbstar.guodian.app.base.GDSmartActivity;
import com.dbstar.guodian.data.AddTimedTaskResponse;
import com.dbstar.guodian.data.JsonTag;
import com.dbstar.guodian.data.ResultData;
import com.dbstar.guodian.data.RoomData.RoomEletrical;
import com.dbstar.guodian.data.TimedTask;
import com.dbstar.guodian.engine1.GDRequestType;
import com.dbstar.guodian.engine1.RequestParams;
import com.dbstar.model.EventData;
import com.dbstar.util.DateUtil;
import com.dbstar.util.ToastUtil;
import com.dbstar.widget.GDSpinner;

public class GDSmartHomeTimedTaskActivity extends GDSmartActivity {
    
    private static final String TASK_MARK_ALL = "1";
    private static final String TASK_MARK_OPENED = "2";
    private static final String TASK_MARK_CLOSED = "3";
    
    private static final String TASK_STATU_OPENED = "1";
    private static final String TASK_STATU_CLOASED = "0";
    
    public static final String SWITCH_OPER_ON = "01";
    public static final String SWITCH_OPER_OFF = "00";
    
    public static final String EXECUTE_SUCCESS = "true";
    public static final String PERIOD_1 = "1";
    public static final String PERIOD_2 = "2";
    public static final String PERIOD_3 = "3";
    public static final String PERIOD_4 = "4";
    public static final String PERIOD_5 = "5";
    public static final String PERIOD_6 = "6";
    public static final String PERIOD_7 = "7";
    
    private int mMaxHourCount = 23;
    private int mMinHourCount = 00;
    private int mMaxMinuteCount = 59;
    private int mMinMinuteCount = 00;
    
    private int mDefauteHour;
    private int mDefauteMinute;
    private int mCurrentHour;
    private int mCurrentMinute;
    
    private GridView mListViewTask;
    private ImageView mLeftJianTou;
    private ImageView mRightJianTou;
    
    private LinearLayout mNoTaskPage;
    private LinearLayout mTimedTaskDetailPage;
    private List<TimedTask []> mPageTimedTasks;
    private List<TimedTask> mListTimedTask;
    private List<RoomEletrical> mAllElectricalList;
    private List<String> mNoTaskElectricalList;
    private List<Integer> mPeriodList;
    private ArrayList<String> mEleSpinnerData;
    
    private GDSpinner mSpinnerEle;
    private Button mButtonSwitch;
    private Button mButtonIncreaseHour;
    private Button mButtonIncreaseMinute;
    private Button mButtonDecreaseHour;
    private Button mButtonDecreaseMinute;
    
    private EditText mEditTextHour;
    private EditText mEditTextMinute;
    private CheckBox mCheckBoxPeriod1;
    private CheckBox mCheckBoxPeriod2;
    private CheckBox mCheckBoxPeriod3;
    private CheckBox mCheckBoxPeriod4;
    private CheckBox mCheckBoxPeriod5;
    private CheckBox mCheckBoxPeriod6;
    private CheckBox mCheckBoxPeriod7;
    private Button mButtonSave;
    private TimedTaskAdapter mTimedTaskAdapter;
    
    private int mPageSizeMode = 5;
    private int mPageCountModel, mPageNumberModel;
    private int mCountModel;
    private TimedTask addTask;
    boolean mIsLoadBackAllEle = false;
    boolean mIsLoadAllEleSuccess = false;
    private boolean mIsOpenedSwitch = true;
    private ArrayAdapter<String> mSpinerAdapter;
    private String mCtrlSeridNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smart_home_timed_task);
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
        mRightJianTou = (ImageView) findViewById(R.id.smart_home_right_jantou);
        mLeftJianTou = (ImageView) findViewById(R.id.smart_home_top_left_jantou);
        mListViewTask = (GridView) findViewById(R.id.smart_home_top_tasks);
        
        mNoTaskPage = (LinearLayout) findViewById(R.id.no_timed_task_page);
        mNoTaskPage.setVisibility(View.INVISIBLE);
        
        mTimedTaskDetailPage = (LinearLayout) findViewById(R.id.timed_task__detail_page);
        mTimedTaskDetailPage.setVisibility(View.INVISIBLE);
        
        mSpinnerEle = (GDSpinner) findViewById(R.id.ele_spinner);
        mButtonSwitch = (Button) findViewById(R.id.smart_home_switch);
        mEditTextHour = (EditText) findViewById(R.id.et_hour);
        mEditTextMinute = (EditText) findViewById(R.id.et_minute);
        mCheckBoxPeriod1 = (CheckBox) findViewById(R.id.cb_period_1);
        mCheckBoxPeriod2 = (CheckBox) findViewById(R.id.cb_period_2);
        mCheckBoxPeriod3 = (CheckBox) findViewById(R.id.cb_period_3);
        mCheckBoxPeriod4 = (CheckBox) findViewById(R.id.cb_period_4);
        mCheckBoxPeriod5= (CheckBox) findViewById(R.id.cb_period_5);
        mCheckBoxPeriod6 = (CheckBox) findViewById(R.id.cb_period_6);
        mCheckBoxPeriod7 = (CheckBox) findViewById(R.id.cb_period_7);
        mButtonSave = (Button) findViewById(R.id.save);
        
        mButtonIncreaseHour = (Button) findViewById(R.id.increase_hour);
        mButtonIncreaseMinute = (Button) findViewById(R.id.increase_minute);
        mButtonDecreaseHour = (Button) findViewById(R.id.decrease_hour);
        mButtonDecreaseMinute = (Button) findViewById(R.id.derease_minute);
        
        mButtonIncreaseHour.setVisibility(View.INVISIBLE);
        mButtonIncreaseMinute.setVisibility(View.INVISIBLE);
        mButtonDecreaseHour.setVisibility(View.INVISIBLE);
        mButtonDecreaseMinute.setVisibility(View.INVISIBLE);
        
        mCheckBoxPeriod1.setOnCheckedChangeListener(mOnCheckedChangeListener);
        mCheckBoxPeriod2.setOnCheckedChangeListener(mOnCheckedChangeListener);
        mCheckBoxPeriod3.setOnCheckedChangeListener(mOnCheckedChangeListener);
        mCheckBoxPeriod4.setOnCheckedChangeListener(mOnCheckedChangeListener);
        mCheckBoxPeriod5.setOnCheckedChangeListener(mOnCheckedChangeListener);
        mCheckBoxPeriod6.setOnCheckedChangeListener(mOnCheckedChangeListener);
        mCheckBoxPeriod7.setOnCheckedChangeListener(mOnCheckedChangeListener);
        
        Calendar calendar = Calendar.getInstance();
        mDefauteHour = calendar.get(Calendar.HOUR_OF_DAY);
        mDefauteMinute = calendar.get(Calendar.MINUTE);
        mCurrentHour = mDefauteHour;
        mCurrentMinute = mDefauteMinute;
        mEditTextHour.setText(getTimeString(mDefauteHour));
        mEditTextMinute.setText(getTimeString(mDefauteMinute));
        
        mEditTextMinute.setOnKeyListener(new OnKeyListener() {
            
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                int action = event.getAction();
                if (action == KeyEvent.ACTION_DOWN) {
                    if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
                        mEditTextMinute.setInputType(android.text.InputType.TYPE_NULL);
                        if(mButtonIncreaseMinute.isShown() && mButtonDecreaseMinute.isShown()){
                            mButtonIncreaseMinute.setVisibility(View.INVISIBLE);
                            mButtonDecreaseMinute.setVisibility(View.INVISIBLE);
                            mDefauteMinute = mCurrentMinute;
                        }else{
                            mButtonIncreaseMinute.setVisibility(View.VISIBLE);
                            mButtonDecreaseMinute.setVisibility(View.VISIBLE);
                            mCurrentMinute = mDefauteMinute;
                        }
                    }else if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
                        if(mButtonIncreaseMinute.isShown() && mButtonDecreaseMinute.isShown()){
                            if(mCurrentMinute == mMinMinuteCount){
                                mCurrentMinute = mMaxMinuteCount;
                            }else {
                                mCurrentMinute --;
                            }
                            mEditTextMinute.setText(getTimeString(mCurrentMinute));
                            return true;
                        }
                    }else if(keyCode == KeyEvent.KEYCODE_DPAD_UP){
                        if(mButtonIncreaseMinute.isShown() && mButtonDecreaseMinute.isShown()){
                            if(mCurrentMinute == mMaxMinuteCount){
                                mCurrentMinute = mMinMinuteCount;
                            }else{
                                mCurrentMinute ++;
                            }
                            mEditTextMinute.setText(getTimeString(mCurrentMinute));
                            return true;
                        }
                    }else if(keyCode == KeyEvent.KEYCODE_BACK){
                        if(mButtonIncreaseMinute.isShown() && mButtonDecreaseMinute.isShown()){
                            mEditTextMinute.setText(getTimeString(mDefauteMinute));
                            mCurrentMinute = mDefauteMinute;
                            return true;
                        }
                  }
                    
                    
                }
                return false;
            }
        });
        mEditTextHour.setOnKeyListener(new OnKeyListener() {
        
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            
            int action = event.getAction();
            if (action == KeyEvent.ACTION_DOWN) {
                if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
                    mEditTextHour.setInputType(android.text.InputType.TYPE_NULL);
                    if(mButtonIncreaseHour.isShown() && mButtonDecreaseHour.isShown()){
                        mButtonIncreaseHour.setVisibility(View.INVISIBLE);
                        mButtonDecreaseHour.setVisibility(View.INVISIBLE);
                        mDefauteHour = mCurrentHour;
                    }else{
                        mButtonIncreaseHour.setVisibility(View.VISIBLE);
                        mButtonDecreaseHour.setVisibility(View.VISIBLE);
                        mCurrentHour = mDefauteHour;
                    }
                }else if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
                    if(mButtonIncreaseHour.isShown()){
                        if(mCurrentHour == mMinHourCount){
                            mCurrentHour = mMaxHourCount;
                        }else {
                            mCurrentHour --;
                        }
                        mEditTextHour.setText(getTimeString(mCurrentHour));
                        return true;
                    }
                }else if(keyCode == KeyEvent.KEYCODE_DPAD_UP){
                    if(mButtonDecreaseHour.isShown()){
                        if(mCurrentHour == mMaxHourCount){
                            mCurrentHour = mMinHourCount;
                        }else{
                            mCurrentHour ++;
                        }
                        mEditTextHour.setText(getTimeString(mCurrentHour));
                        return true;
                    }
                }else if(keyCode == KeyEvent.KEYCODE_BACK){
                    if(mButtonDecreaseHour.isShown() && mButtonIncreaseHour.isShown()){
                        mEditTextHour.setText(getTimeString(mDefauteHour));
                        mCurrentHour = mDefauteHour;
                        return true;
                    }
              }
                
                
            }
            return false;
        }
    }) ;
       mEditTextHour.setOnFocusChangeListener(mFocusChangeListener);
       mEditTextMinute.setOnFocusChangeListener(mFocusChangeListener);
        
        mButtonSave.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                requestAddTask();
            }
        });
        
        mSpinnerEle.setOnFocusChangeListener(new OnFocusChangeListener() {
            
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mButtonSave.setVisibility(View.VISIBLE);
            }
        });
        mEleSpinnerData = new ArrayList<String>();
        mEleSpinnerData.add(getString(R.string.family_text_please_select_ele));
        mSpinerAdapter = new ArrayAdapter<String>(this, R.layout.gd_spinner_drop_list_item, mEleSpinnerData);
        mSpinnerEle.setAdapter(mSpinerAdapter);
        
        mSpinnerEle.setOnKeyListener(new OnKeyListener() {
            
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                int action = event.getAction();
                if (action == KeyEvent.ACTION_DOWN) {
                    if(KeyEvent.KEYCODE_DPAD_CENTER == keyCode){
                        if(mEleSpinnerData.size() == 1){
                            if(!mIsLoadBackAllEle){
                                Toast.makeText(GDSmartHomeTimedTaskActivity.this, getString(R.string.family_text_loading_electrical), Toast.LENGTH_SHORT).show();
                                return true;
                            }
                        } 
                    }
                    
                }
                return false;
            }
        });
        
        mButtonSwitch.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if(mIsOpenedSwitch){
                    v.setBackgroundResource(R.drawable.smart_home_timed_task_switch_off_selecter);
                    mIsOpenedSwitch = false;
                }else{
                    v.setBackgroundResource(R.drawable.smart_home_timed_task_switch_on_selecter);
                    mIsOpenedSwitch = true;
                }
            }
        });
        TimedTask datas[] = new TimedTask[1];
        addTask = new TimedTask();
        mListTimedTask = new ArrayList<TimedTask>();
        mListTimedTask.add(addTask);
        mTimedTaskAdapter = new TimedTaskAdapter();
        mListViewTask.setAdapter(mTimedTaskAdapter);
        mListViewTask.setOnFocusChangeListener(new OnFocusChangeListener() {
            
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                int position = mListViewTask.getSelectedItemPosition();
                if(hasFocus){
                    for(int i = 0,size = mListViewTask.getChildCount(); i <size ;i ++){
                        View chiid =  mListViewTask.getChildAt(i).findViewById(R.id.task_content);
                        if(i == position){
                            chiid.setBackgroundResource(R.drawable.smart_home_model_item_selecter);
                        }else{
                            chiid.setBackgroundResource(R.drawable.smarthome_myele_room_normal);
                        }
                    }
                }else{
                    for(int i = 0,size = mListViewTask.getChildCount(); i <size ;i ++){
                        View chiid =  mListViewTask.getChildAt(i).findViewById(R.id.task_content);
                        if(i == position){
                            chiid.setBackgroundResource(R.drawable.smarthome_myele_room_highlight);
                        }else{
                            chiid.setBackgroundResource(R.drawable.smarthome_myele_room_normal);
                        }
                    }
                } 
            }
        });
        
        mListViewTask.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                if(position == 0 && mPageNumberModel == 0){
                    addTimedTask();
                }else{
                    requestOpenOrCloseTimedTask();
                }
            }
        });
        mListViewTask.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                setTaskItemBackground(position);
                TimedTask task = mTimedTaskAdapter.getData()[position];
                if(task.TimedTaskGuid == null || task.TimedTaskGuid.isEmpty()){
                    mNoTaskPage.setVisibility(View.VISIBLE);
                    mTimedTaskDetailPage.setVisibility(View.INVISIBLE);
                    TextView note = (TextView) mNoTaskPage.findViewById(R.id.no_timed_task_note);
                    note.setText(Html.fromHtml(getString(R.string.family_text_no_timed_task)));
                    return;
                }else{
                    mNoTaskPage.setVisibility(View.INVISIBLE);
                    mTimedTaskDetailPage.setVisibility(View.VISIBLE);
                    String devieName = task.DeviceName;
                    int index = mEleSpinnerData.indexOf(devieName);
                    mSpinnerEle.setSelection(index == -1? 0 : index);
                    String oper = task.Oper;
                    if(oper.equals(SWITCH_OPER_ON)){
                        mButtonSwitch.setBackgroundResource(R.drawable.smart_home_timed_task_switch_on_selecter);
                        mIsOpenedSwitch = true;
                    }else{
                        mButtonSwitch.setBackgroundResource(R.drawable.smart_home_timed_task_switch_off_selecter);
                        mIsOpenedSwitch = false;
                    }
                    Date date =  DateUtil.getDateFromStr(task.Time, DateUtil.DateFormat6);
                    if(date != null){
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        mDefauteHour = calendar.get(Calendar.HOUR_OF_DAY);
                        mDefauteMinute = calendar.get(Calendar.MINUTE);
                        mEditTextHour.setText(getTimeString(mDefauteHour));
                        mEditTextMinute.setText(getTimeString(mDefauteMinute));
                    }
                    
                    mButtonSave.setVisibility(View.INVISIBLE);
                    String frequecy = task.Frequency;
                    int [] list = new int [7];
                    if("o".equals(frequecy)){
                        for(int i = 0;i<list.length;i ++){
                            list[i] = 0;
                        }
                    }else if("e".equals(frequecy)){
                        for(int i = 0;i<list.length;i ++){
                            list[i] = i+1;
                        }
                    }else{
                        String [] fre = frequecy.split("[$]");
                        for(int i = 1 ; i< fre.length;i++){
                            if(i < list.length){
                                if((Integer.parseInt(fre[i])-1) < list.length)
                                    list[Integer.parseInt(fre[i])-1] = Integer.parseInt(fre[i]);
                            }
                        }
                    }
                    
                    for(int i =  0;i<list.length ; i++){
                      int period = list[i];
                      boolean checked = period == 0 ? false:true;
                      if(i ==0){
                         mCheckBoxPeriod1.setChecked(checked);
                      }else if(i == 1){
                          mCheckBoxPeriod2.setChecked(checked);
                      }else if(i == 2){
                          mCheckBoxPeriod3.setChecked(checked);
                      }else if( i == 3){
                          mCheckBoxPeriod4.setChecked(checked);
                      }else if(i == 4){
                          mCheckBoxPeriod5.setChecked(checked);
                      }else if(i == 5){
                          mCheckBoxPeriod6.setChecked(checked);
                      }else if(i == 6){
                          mCheckBoxPeriod7.setChecked(checked);
                      }
                  }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                
            }
        });
        mListViewTask.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                    int position, long id) {
                if(position != 0)
                        dialog();
                return true;
            }
        });
        mListViewTask.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                boolean ret = false;
                int action = event.getAction();
                if (action == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                    
                    case KeyEvent.KEYCODE_DPAD_LEFT: {
                        int selectedIndex = mListViewTask
                                .getSelectedItemPosition();
                        if (selectedIndex == 0 && mPageNumberModel > 0) {
                            loadPrevPage();
                            ret = true;
                        }
                        break;
                    }
                    case KeyEvent.KEYCODE_DPAD_RIGHT: {
                        int selectedIndex = mListViewTask
                                .getSelectedItemPosition();
                        if (selectedIndex == (mPageSizeMode - 1)
                                && mPageNumberModel < mPageCountModel - 1) {
                            loadNextPage();
                            ret = true;
                        }
                        break;
                    }
                    }

                }
                return ret;
            }
        });
       initTimedTaskList(mListTimedTask);
        
    }
    
    protected void addTimedTask() {
        mNoTaskPage.setVisibility(View.INVISIBLE);
        mTimedTaskDetailPage.setVisibility(View.VISIBLE);
        mSpinnerEle.setSelection(0);
        mButtonSwitch.setBackgroundResource(R.drawable.smart_home_timed_task_switch_on_selecter);
        mDefauteHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        mDefauteMinute = Calendar.getInstance().get(Calendar.MINUTE);
        mEditTextMinute.setText(getTimeString(mDefauteMinute));
        mEditTextHour.setText(getTimeString(mDefauteHour));
        mCheckBoxPeriod1.setChecked(false);
        mCheckBoxPeriod2.setChecked(false);
        mCheckBoxPeriod3.setChecked(false);
        mCheckBoxPeriod4.setChecked(false);
        mCheckBoxPeriod5.setChecked(false);
        mCheckBoxPeriod6.setChecked(false);
        mCheckBoxPeriod7.setChecked(false);
        mHandler.post(new Runnable() {
            
            @Override
            public void run() {
                mSpinnerEle.requestFocus();
            }
        });
        }

    @Override
    protected void onServiceStart() {
        super.onServiceStart();
        if(getCtrlNo() != null)
            mCtrlSeridNo = getCtrlNo().CtrilSerialNo;
        requestTaskList();
        //requestNoTimedTaskElectricalList();
       
    }
    
    @Override
    public void notifyEvent(int type, Object event) {
        super.notifyEvent(type, event);
        EventData.GuodianEvent guodianEvent = (EventData.GuodianEvent) event;
        if(EventData.EVENT_GUODIAN_DATA == type){
            if(GDRequestType.DATATYPE_TIMED_TASK_LIST == guodianEvent.Type){
               requestAllEleList();
               List<TimedTask> list = (List<TimedTask>) guodianEvent.Data;
               if(list != null){
                   mListTimedTask.addAll(list);
                   initTimedTaskList(mListTimedTask);
               }
            }else if(GDRequestType.DATATYPE_EQUMENTLIST == guodianEvent.Type){
                mIsLoadBackAllEle = true;
                mIsLoadAllEleSuccess = true;
                mAllElectricalList = (List<RoomEletrical>) guodianEvent.Data;
                if(mListTimedTask.size() > 1 && mAllElectricalList != null && mAllElectricalList.size() > 0){
                    mTimedTaskAdapter.notifyDataSetChanged();
                }
                if(mEleSpinnerData.size() == 1){
                    constructeSpinnerData();
                }
            }else if(GDRequestType.DATATYPE_NO_TASK_ELCTRICAL_LIST == guodianEvent.Type){
//                mNoTaskElectricalList = (List<String>) guodianEvent.Data;
//                if(mEleSpinnerData.size() == 1 && mAllElectricalList != null){
//                    constructeSpinnerData();
//                }
            }else if(GDRequestType.DATATYPE_ADD_TIMED_TASK == guodianEvent.Type){
                AddTimedTaskResponse response = (AddTimedTaskResponse) guodianEvent.Data;
                if(response != null){
                    if(EXECUTE_SUCCESS.equals(response.Result)){
                        showNotifyMessage(R.string.family_text_add_timed_success);
                        //ToastUtil.showToast(this, R.string.family_text_add_timed_success);
                        mButtonSave.setVisibility(View.INVISIBLE);
                        mCacheTask.TimedTaskGuid = response.TimedTaskGuid;
                        mListTimedTask.add(mCacheTask);
                        constructModePages(mListTimedTask);
                        mPageNumberModel = mPageCountModel  -1;
                        TimedTask [] tasks = mPageTimedTasks.get(mPageNumberModel);
                        mTimedTaskAdapter.setData(tasks);
                        mTimedTaskAdapter.notifyDataSetChanged();
                        mListViewTask.setSelection(tasks.length -1);
                    }else{
                        showErrorMsg(R.string.family_text_add_timed_fail);
                        //ToastUtil.showToast(this,  getString( R.string.family_text_add_timed_fail) + response.Exception + " ," + response.Reason );
                        mButtonSave.setVisibility(View.VISIBLE);
                    }
                }else{
                    showErrorMsg(R.string.family_text_add_timed_fail);
                    //ToastUtil.showToast(this,  getString( R.string.family_text_add_timed_fail)  );
                    mButtonSave.setVisibility(View.VISIBLE);
                }
            }else if(GDRequestType.DATATYPE_MODIFY_TIMED_TASK == guodianEvent.Type){
                ResultData resultData = (ResultData) guodianEvent.Data;
                if(resultData != null){
                    if(EXECUTE_SUCCESS.equals(resultData.Result)){
                        showNotifyMessage(R.string.family_text_modify_timed_success);
                        //ToastUtil.showToast(this, R.string.family_text_modify_timed_success);
                        mButtonSave.setVisibility(View.INVISIBLE);
                        int index = mListViewTask.getSelectedItemPosition();
                        TimedTask selectedTimedTask =  mTimedTaskAdapter.getData()[index];
                        selectedTimedTask.DeviceGuid = mCacheTask.DeviceGuid;
                        selectedTimedTask.DeviceName = mCacheTask.DeviceName;
                        selectedTimedTask.Frequency  = mCacheTask.Frequency;
                        selectedTimedTask.Oper = mCacheTask.Oper;
                        selectedTimedTask.State = mCacheTask.State;
                        selectedTimedTask.Time = mCacheTask.Time;
                        selectedTimedTask.TypeId = mCacheTask.TypeId;
                    }else{
                        showErrorMsg(R.string.family_text_modify_timed_fail);
                        //ToastUtil.showToast(this,  getString( R.string.family_text_modify_timed_fail) + resultData.Exception + " ," + resultData.Reason );
                        mButtonSave.setVisibility(View.VISIBLE);
                    }
                }else{
                    showErrorMsg(R.string.family_text_modify_timed_fail);
                    //ToastUtil.showToast(this,  getString( R.string.family_text_modify_timed_fail));
                    mButtonSave.setVisibility(View.VISIBLE);
                }
            }else if(GDRequestType.DATATYPE_DELETE_TIMED_TASK == guodianEvent.Type ){
                ResultData resultData = (ResultData) guodianEvent.Data;
                if(resultData != null){
                    if(EXECUTE_SUCCESS.equals(resultData.Result)){
                        showNotifyMessage(R.string.family_text_delete_timed_task_success);
                        //ToastUtil.showToast(this, R.string.family_text_delete_timed_task_success);
                        int index = mListViewTask.getSelectedItemPosition();
                        TimedTask task = mTimedTaskAdapter.getData()[index];
                        mListTimedTask.remove(task);
                        int pageNum = mPageNumberModel;
                        int pageCount = mPageCountModel;
                        constructModePages(mListTimedTask);
                        int selection = 0;
                        if( mTimedTaskAdapter.getData().length == 1){
                                mPageNumberModel = mPageCountModel-1;
                                selection = mPageSizeMode -1;
                        }else{
                            if(pageCount == 1){
                                selection = 0;
                            }else if(pageCount > 0){
                                selection = index;   
                            }
                           mPageNumberModel = pageNum;
                        }
                       
                        TimedTask [] tasks = mPageTimedTasks.get(mPageNumberModel);
                        mTimedTaskAdapter.setData(tasks);
                        mTimedTaskAdapter.notifyDataSetChanged();
                        mListViewTask.setSelection(selection);
                    }else{
                        showErrorMsg(R.string.family_text_delete_timed_task_fail);
                        //ToastUtil.showToast(this,  getString( R.string.family_text_delete_timed_task_fail) + resultData.Exception + " ," + resultData.Reason );
                    }
                }else{
                    showErrorMsg(R.string.family_text_delete_timed_task_fail);
                    //ToastUtil.showToast(this,  getString( R.string.family_text_delete_timed_task_fail));
                }
            }else if(GDRequestType.DATATYPE_EXECUTE_TIMED_TASK == guodianEvent.Type){
                ResultData resultData = (ResultData) guodianEvent.Data;
                if(resultData != null){
                    if(EXECUTE_SUCCESS.equals(resultData.Result)){
                        if(TASK_STATU_CLOASED.equals(mCacheTask.State)){
                            mCacheTask.State = TASK_STATU_OPENED;
                        }else if(TASK_STATU_OPENED.equals(mCacheTask.State)){
                            mCacheTask.State = TASK_STATU_CLOASED;
                        }
                        mTimedTaskAdapter.notifyDataSetChanged();
                    }else{
                        if(TASK_STATU_CLOASED.equals(mCacheTask.State)){
                            showErrorMsg(R.string.family_text_open_timed_task_fail);
                            //ToastUtil.showToast(this,  getString( R.string.family_text_open_timed_task_fail));
                        }else if(TASK_STATU_OPENED.equals(mCacheTask.State)){
                            showErrorMsg(R.string.family_text_open_timed_task_fail);
                           // ToastUtil.showToast(this,  getString( R.string.family_text_close_timed_task_fail));
                        }
                    }
                }else{
                    if(TASK_STATU_CLOASED.equals(mCacheTask.State)){
                        showErrorMsg(R.string.family_text_close_timed_task_fail);
                        //ToastUtil.showToast(this,  getString( R.string.family_text_open_timed_task_fail));
                    }else if(TASK_STATU_OPENED.equals(mCacheTask.State)){
                        showErrorMsg(R.string.family_text_close_timed_task_fail);
                        //ToastUtil.showToast(this,  getString( R.string.family_text_close_timed_task_fail));
                    }
                }
            }
            
        }else if(EventData.EVENT_GUODIAN_DATA_ERROR == type){
            if(GDRequestType.DATATYPE_TIMED_TASK_LIST == guodianEvent.Type){
                requestAllEleList();
                mPageContent.setVisibility(View.VISIBLE);
                showErrorMsg(R.string.loading_error);
            }else if(GDRequestType.DATATYPE_EQUMENTLIST == guodianEvent.Type){
                mIsLoadBackAllEle = true;
                mIsLoadAllEleSuccess = false;
                showErrorMsg(R.string.loading_electrical_list_fail);
            }else if(GDRequestType.DATATYPE_ADD_TIMED_TASK == guodianEvent.Type){
                showErrorMsg(R.string.family_text_add_timed_fail);
            }else if(GDRequestType.DATATYPE_MODIFY_TIMED_TASK == guodianEvent.Type){
                showErrorMsg(R.string.family_text_modify_timed_fail);
            }else if(GDRequestType.DATATYPE_DELETE_TIMED_TASK == guodianEvent.Type ){
                showErrorMsg(R.string.family_text_delete_timed_task_fail);
            }else if(GDRequestType.DATATYPE_EXECUTE_TIMED_TASK == guodianEvent.Type){
                if(TASK_STATU_CLOASED.equals(mCacheTask.State)){
                    showErrorMsg(R.string.family_text_open_timed_task_fail);
                }else if(TASK_STATU_OPENED.equals(mCacheTask.State)){
                    showErrorMsg(R.string.family_text_close_timed_task_fail);
                }
            }
            return;
        }
        
    }
    
    private void constructeSpinnerData() {
        if(mAllElectricalList !=null){
            RoomEletrical ele; 
            for(RoomEletrical eletrical: mAllElectricalList){
                mEleSpinnerData.add(eletrical.DeviceName);
            }
            mSpinerAdapter = new ArrayAdapter<String>(this, R.layout.gd_spinner_drop_list_item, mEleSpinnerData);
            mSpinnerEle.setAdapter(mSpinerAdapter);
            mSpinerAdapter.notifyDataSetChanged();
        }
    }

    private void setTaskItemBackground (int position){
        for(int i = 0,size = mListViewTask.getChildCount(); i <size ;i ++){
            View v =  mListViewTask.getChildAt(i).findViewById(R.id.task_content);
            if(i == position){
                v.setBackgroundResource(R.drawable.smart_home_model_item_selecter);
            }else{
                v.setBackgroundResource(R.drawable.smarthome_myele_room_normal);
            }
        }
        
    }
    private void initTimedTaskList(List<TimedTask> list){
        if(list == null || list.isEmpty()){
           
            return;
        }else{
            constructModePages(list);
            TimedTask[] page = mPageTimedTasks.get(mPageNumberModel);
            mTimedTaskAdapter.setData(page);
            mTimedTaskAdapter.notifyDataSetChanged();
            displayJianTou();
        }
        
    }
    
    private void loadPrevPage() {

        mPageNumberModel--;

        TimedTask[] page = mPageTimedTasks.get(mPageNumberModel);
        mTimedTaskAdapter.setData(page);
        mListViewTask.clearChoices();
        mListViewTask.setSelection(page.length);
        mTimedTaskAdapter.notifyDataSetChanged();
        displayJianTou();
    }
    
    
    private void loadNextPage() {

        mPageNumberModel++;

        TimedTask[] page = mPageTimedTasks.get(mPageNumberModel);
        mTimedTaskAdapter.setData(page);
        mListViewTask.clearChoices();
        mListViewTask.setSelection(0);
        mTimedTaskAdapter.notifyDataSetChanged();
        displayJianTou();
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
    
    private void constructModePages(List<TimedTask> lists) {
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

        mPageTimedTasks = new ArrayList<TimedTask[]>();

        int index = 0;
        for (int i = 0; i < mPageCountModel; i++) {
            int pageSize = Math.min(mPageSizeMode, size - index);

            TimedTask[] page = new TimedTask[pageSize];
            for (int j = 0; j < pageSize; j++) {
                page[j] = lists.get(index);
                index++;
            }

            mPageTimedTasks.add(page);
        }
    }
    class TimedTaskAdapter extends BaseAdapter{
        
        private TimedTask [] data;
        ViewHolder v = null;
        @Override
        public int getCount() {
            return data == null ? 0 : data.length;
        }
        
        public void setData(TimedTask [] data){
            this.data = data;
        }
        
        public TimedTask [] getData(){
            return data;
            
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
                convertView = LayoutInflater.from(GDSmartHomeTimedTaskActivity.this).inflate(R.layout.smart_home_timed_task_itme, null);
                v = new ViewHolder();
                v.mStatuIcon = (ImageView) convertView.findViewById(R.id.iv_task_statu);
                v.mElePic = (ImageView) convertView.findViewById(R.id.task_pic);
                v.mStatu   = (TextView) convertView.findViewById(R.id.tv_task_statu);
                
                convertView.setTag(v);
            }else{
                v = (ViewHolder) convertView.getTag();
            }
            TimedTask task = data[position];
            
            if(task.DeviceGuid != null && !task.DeviceGuid.isEmpty()){
                v.mStatuIcon.setVisibility(View.VISIBLE);
                v.mElePic.setImageResource(getPicId(task));
            }else{
                v.mElePic.setImageResource(R.drawable.smarthome_timed_task_addtask_icon);
                v.mStatuIcon.setVisibility(View.INVISIBLE);
                v.mStatu.setText(getString(R.string.family_text_add_timed_task));
            }
            if(TASK_STATU_OPENED.equals(task.State)){
                v.mStatuIcon.setBackgroundResource(R.drawable.smarthome_timed_task_on_icon);
                v.mStatu.setText(getString(R.string.family_text_close_task));
            }else if(TASK_STATU_CLOASED.equals(task.State)){
                v.mStatuIcon.setBackgroundResource(R.drawable.smarthome_timed_task_off_icon);
                v.mStatu.setText(getString(R.string.family_text_open_task));
            }
           
            return convertView;
        }
       
        class ViewHolder {
            ImageView mStatuIcon;
            ImageView mElePic;
            TextView mStatu;
        }
        
    }
    
    OnFocusChangeListener mFocusChangeListener = new OnFocusChangeListener() {
        
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus)
                return;
            int id = v.getId();
            
           switch (id) {
            case R.id.et_hour:
                      if(mButtonIncreaseHour.isShown()){
                          mButtonIncreaseHour.setVisibility(View.INVISIBLE);
                          mButtonDecreaseHour.setVisibility(View.INVISIBLE);
                      }
//                    String hour = mEditTextHour.getText().toString();
//                    if(hour.isEmpty())
//                        return;
//                    int h = Integer.parseInt(hour);
//                    if( h < 0 || h > 23){
//                        ToastUtil.showToast(getApplicationContext(), R.string.family_text_input_time_hour_error);
//                    }else{
//                        if(hour.length() == 1){
//                            mEditTextHour.setText("0" + hour);
//                        }
//                    }
                break;
    
            case R.id.et_minute:
                if(mButtonIncreaseMinute.isShown()){
                    mButtonIncreaseMinute.setVisibility(View.INVISIBLE);
                    mButtonDecreaseMinute.setVisibility(View.INVISIBLE);
                }
//                String minute = mEditTextMinute.getText().toString();
//                if(minute.isEmpty())
//                    return;
//                int m = Integer.parseInt(minute);
//                if(m < 0 || m > 59){
//                    ToastUtil.showToast(getApplicationContext(), R.string.family_text_input_time_minute_error);
//                }else{
//                    if(minute.length() == 1){
//                        mEditTextMinute.setText("0" + minute); 
//                    }
//                }
            break;
        }
        }
    };
    
    OnCheckedChangeListener mOnCheckedChangeListener = new OnCheckedChangeListener() {
        
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(mPeriodList == null){
                mPeriodList = new ArrayList<Integer>();
            }
           int id =  buttonView.getId();
           int period = 0;
           switch (id) {
        case R.id.cb_period_1:
            period = 1;
            break;
        case R.id.cb_period_2:
            period = 2;
            break;
        case R.id.cb_period_3:
            period = 3;
            break;
        case R.id.cb_period_4:
            period = 4;
            break;
        case R.id.cb_period_5:
            period = 5;
            break;
        case R.id.cb_period_6:
            period = 6;
            break;
        case R.id.cb_period_7:
            period = 7;
            break;
        }
           if(isChecked && period != 0){
               mPeriodList.add(period);
           }else if(!isChecked && period != 0){
               mPeriodList.remove(new Integer(period));
           }
        }
    };
    private TimedTask mCacheTask;
    private void requestTaskList(){
        if(mCtrlSeridNo == null){
            showErrorMsg(R.string.no_login);
            return;
        }
        mSystemFlag = "sml";
        mRequestMethodId = "m011f001";
        RequestParams params = new RequestParams(GDRequestType.DATATYPE_TIMED_TASK_LIST);
        params.put(RequestParams.KEY_SYSTEM_FLAG, mSystemFlag);
        params.put(RequestParams.KEY_METHODID, mRequestMethodId);
        params.put(JsonTag.TAGCTRL_SeridNo, mCtrlSeridNo);
        params.put(JsonTag.TAGTaskMark, TASK_MARK_ALL);
        requestData(params);
    }
    private void requestAllEleList(){
        if(mCtrlSeridNo == null){
            showErrorMsg(R.string.loading_electrical_list_fail);
            return;
        }
        mSystemFlag = "sml";
        mRequestMethodId = "m001f010";
        RequestParams params = new RequestParams(GDRequestType.DATATYPE_EQUMENTLIST);
        params.put(RequestParams.KEY_SYSTEM_FLAG, mSystemFlag);
        params.put(RequestParams.KEY_METHODID, mRequestMethodId);
        params.put(JsonTag.TAGCTRL_SeridNo, mCtrlSeridNo);
        requestDataNotShowDialog(params);
    }
    private void requestNoTimedTaskElectricalList(){
        if(mCtrlSeridNo == null){
            ToastUtil.showToast(this, R.string.no_login);
            return;
        }
        mSystemFlag = "sml";
        mRequestMethodId = "m011f002";
        RequestParams params = new RequestParams(GDRequestType.DATATYPE_NO_TASK_ELCTRICAL_LIST);
        params.put(RequestParams.KEY_SYSTEM_FLAG, mSystemFlag);
        params.put(RequestParams.KEY_METHODID, mRequestMethodId);
        params.put(JsonTag.TAGCTRL_SeridNo, mCtrlSeridNo);
        requestData(params);
    }
    
    
    private int getPicId (TimedTask task){
            
        int picId = R.drawable.common_icon_equ_defult;
        if(mAllElectricalList == null)
            return picId;
        for(RoomEletrical eletrical : mAllElectricalList){
            if(eletrical.DeviceGuid.equals(task.DeviceGuid)){
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
    /**
     * 
     */
    private void requestAddTask(){
        if(mCtrlSeridNo == null){
            showErrorMsg(R.string.no_login);
            return;
        }
        RequestParams params = new RequestParams();
        int spinnerSelectedPosition = mSpinnerEle.getSelectedItemPosition();
        if(spinnerSelectedPosition < 1){
            if(!mIsLoadBackAllEle){
                //handleErrorResponse(R.string.family_text_loading_electrical);
                ToastUtil.showToast(this, R.string.family_text_loading_electrical);
                return;
            }else{
                if(mAllElectricalList == null || mAllElectricalList.isEmpty()){
                    if(mIsLoadAllEleSuccess){
                        //handleErrorResponse(R.string.family_text_add_task_error1);
                        ToastUtil.showToast(this, R.string.family_text_add_task_error1);
                    }else{
                       // handleErrorResponse(R.string.family_text_load_ele_list_fail);
                        ToastUtil.showToast(this, R.string.family_text_load_ele_list_fail);
                    }
                    return;
                }else{
                    //handleErrorResponse(R.string.family_text_please_select_ele);
                    ToastUtil.showToast(this, R.string.family_text_please_select_ele); 
                    return;
                }
            }
        }
        
        RoomEletrical selectedEle = mAllElectricalList.get(spinnerSelectedPosition -1);
        String deviceGuid = selectedEle.DeviceGuid;
        String oper = SWITCH_OPER_ON;
        String state = TASK_STATU_OPENED;
        String typeid = selectedEle.EleDeviceCode;
        if(!mIsOpenedSwitch){
            oper = SWITCH_OPER_OFF;
        }
        String hour = mEditTextHour.getText().toString();
        String  minute = mEditTextMinute.getText().toString();
        
        if(hour.isEmpty() || minute .isEmpty()){
            ToastUtil.showToast(this, R.string.family_text_no_input_time);
            return ;
            }
        String time = null;
        String frequency = null;
        
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour.trim()));
        calendar.set(Calendar.MINUTE, Integer.parseInt(minute));
        if(mPeriodList == null || mPeriodList.isEmpty()){
            frequency = "o";
            if(System.currentTimeMillis() >= calendar.getTimeInMillis())
                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) +1);
            
        }else if(mPeriodList != null && mPeriodList.size() < 7){
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) -1;
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            Collections.sort(mPeriodList);
            StringBuffer sb = new StringBuffer();
            sb.append("$");
            for(int i = 0;i< mPeriodList.size();i++){
                sb.append(mPeriodList.get(i));
                sb.append("$");
            }
            frequency = sb.toString();
            if(mPeriodList.contains(new Integer(dayOfWeek))){
                if(System.currentTimeMillis() >= calendar.getTimeInMillis()){
                   int index = mPeriodList.indexOf(new Integer(dayOfWeek));
                   int nextIndex = index + 1;
                   if(nextIndex >= mPeriodList.size()){
                       int nextDayInNextWeek = mPeriodList.get(0);
                       calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth + 7 - dayOfWeek + nextDayInNextWeek);
                   }else{
                       int nextDayInNextWeek = mPeriodList.get(nextIndex);
                       calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth +nextDayInNextWeek - dayOfWeek);
                   }
                }
            }else{
                mPeriodList.add(new Integer(dayOfWeek));
                Collections.sort(mPeriodList);
                int index = mPeriodList.indexOf(new Integer(dayOfWeek));
                int nextIndex = index + 1;
                if(nextIndex >= mPeriodList.size()){
                    int nextDayInNextWeek = mPeriodList.get(0);
                    calendar.set(Calendar.DAY_OF_MONTH,  dayOfMonth + 7 - dayOfWeek + nextDayInNextWeek);
                }else{
                    int nextDayInNextWeek = mPeriodList.get(nextIndex);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth +nextDayInNextWeek - dayOfWeek);
                }
            }
            
        }else if(mPeriodList != null && mPeriodList.size() == 7){
            frequency = "e";
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            if(System.currentTimeMillis() >=calendar.getTimeInMillis()){
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth +1);
            }
        }
        calendar.set(Calendar.MINUTE, Integer.parseInt(minute));
        time =  DateUtil.getStringFromDate(calendar.getTime(), DateUtil.DateFormat6);
        
        params.put(JsonTag.TAGCTRL_SeridNo, mCtrlSeridNo);
        params.put(JsonTag.TAGDeviceGuid, deviceGuid);
        params.put(JsonTag.TAGOper, oper);
        params.put(JsonTag.TAGTime, time);
        params.put(JsonTag.TAGFrequency, frequency);
        params.put(JsonTag.TAGTypeId, typeid);
        
        int dataType = GDRequestType.DATATYPE_ADD_TIMED_TASK;
        mSystemFlag = "sml";
        mRequestMethodId = "m011f004";
        mCacheTask = new TimedTask();
        
        if(mListViewTask.getSelectedItemPosition() == 0 && mPageNumberModel == 0){
            
        }else{
            TimedTask task = mTimedTaskAdapter.getData()[mListViewTask.getSelectedItemPosition()];
            mCacheTask.TimedTaskGuid = task.TimedTaskGuid;
            params.put(JsonTag.TAGOldOper, task.Oper);
            params.put(JsonTag.TAGOldTime, task.Time);
            params.put(JsonTag.TAGOldCycle,task.Frequency);
            params.put(JsonTag.TAGTimeTaskGuid,task.TimedTaskGuid);
            
            mSystemFlag = "sml";
            mRequestMethodId = "m011f007";
            dataType = GDRequestType.DATATYPE_MODIFY_TIMED_TASK;
            state = task.State;
        }
        
        params.setRequestType(dataType);
        params.put(RequestParams.KEY_SYSTEM_FLAG, mSystemFlag);
        params.put(RequestParams.KEY_METHODID, mRequestMethodId);
        
        params.put(JsonTag.TAGState, state);
        mCacheTask.DeviceGuid = deviceGuid;
        mCacheTask.DeviceName = selectedEle.DeviceName;
        mCacheTask.Frequency = frequency;
        mCacheTask.Oper = oper;
        mCacheTask.State = state;
        mCacheTask.Time = time;
        mCacheTask.TypeId = typeid;
        
        requestData( params);
    }
    
    private void requestDeleteTimedTask(){
        if(mCtrlSeridNo == null){
            showErrorMsg(R.string.no_login);
            return;
        }
        
        TimedTask selectedTimedTask = mTimedTaskAdapter.getData()[mListViewTask.getSelectedItemPosition()];
        
        mSystemFlag = "sml";
        mRequestMethodId = "m011f008";
        
        RequestParams params = new RequestParams(GDRequestType.DATATYPE_DELETE_TIMED_TASK);
        params.put(RequestParams.KEY_SYSTEM_FLAG, mSystemFlag);
        params.put(RequestParams.KEY_METHODID, mRequestMethodId);
        params.put(JsonTag.TAGCTRL_SeridNo, mCtrlSeridNo);
        params.put(JsonTag.TAGTimeTaskGuid, selectedTimedTask.TimedTaskGuid);
        params.put(JsonTag.TAGOper, selectedTimedTask.Oper);
        params.put(JsonTag.TAGTime, selectedTimedTask.Time);
        params.put(JsonTag.TAGFrequency, selectedTimedTask.Frequency);
        params.put(JsonTag.TAGTypeId, selectedTimedTask.TypeId);
        requestData(params);
    }
    
    private void requestOpenOrCloseTimedTask(){
        if(mCtrlSeridNo == null){
            showErrorMsg(R.string.no_login);
            return;
        }
        mCacheTask = mTimedTaskAdapter.getData()[mListViewTask.getSelectedItemPosition()];
        
        mSystemFlag = "sml";
        mRequestMethodId = "m011f009";
        
        RequestParams params = new RequestParams(GDRequestType.DATATYPE_EXECUTE_TIMED_TASK);
        params.put(RequestParams.KEY_SYSTEM_FLAG, mSystemFlag);
        params.put(RequestParams.KEY_METHODID, mRequestMethodId);
        
        params.put(JsonTag.TAGCTRL_SeridNo, mCtrlSeridNo);
        params.put(JsonTag.TAGTimeTaskGuid, mCacheTask.TimedTaskGuid);
        params.put(JsonTag.TAGOper, mCacheTask.Oper);
        params.put(JsonTag.TAGTime, mCacheTask.Time);
        params.put(JsonTag.TAGFrequency, mCacheTask.Frequency);
        params.put(JsonTag.TAGTypeId, mCacheTask.TypeId);
        params.put(JsonTag.TAGDeviceGuid, mCacheTask.DeviceGuid);
        if(TASK_STATU_CLOASED.equals(mCacheTask.State)){
            params.put(JsonTag.TAGState, TASK_STATU_OPENED);
        }else if(TASK_STATU_OPENED.equals(mCacheTask.State)){
            params.put(JsonTag.TAGState, TASK_STATU_CLOASED);
        }
        
        requestData(params);
    }
    
    private String getTimeString(int time){
        if(time < 0)
            return "00";
        String str = String.valueOf(time);
        if(str.length() == 1){
            return "0" + str;
        }
        return str;
    }
    protected void dialog() {
        AlertDialog.Builder builder = new Builder(this);
        builder.setMessage(getString(R.string.family_text_delete_timed_task_alert));

        builder.setTitle(getString(R.string.family_text_delete_timed_task));

        builder.setPositiveButton(R.string.button_text_ok, new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestDeleteTimedTask();
            }
        });
        builder.setNegativeButton(R.string.button_text_cancel, new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }) ; 
        builder.create().show();
       }
}
