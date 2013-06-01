package com.dbstar.guodian.app.mypower;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dbstar.R;
import com.dbstar.guodian.app.base.GDSmartActivity;
import com.dbstar.guodian.data.JsonTag;
import com.dbstar.guodian.data.PowerData;
import com.dbstar.guodian.data.PowerTarget;
import com.dbstar.guodian.data.ResultData;
import com.dbstar.guodian.engine.GDConstract;
import com.dbstar.model.EventData;
import com.dbstar.model.GDCalendarGB;
import com.dbstar.util.ToastUtil;

public class GDPowerTargetSettingActivity extends GDSmartActivity{
    
    
    private TextView mTVCurrentTarget;
    private EditText mEdNewTarget;
    private Button mButtonOk;
    private Button mButtonCancel;
    private boolean mIsRightTarget;
    private PowerTarget mPowerTarget;
    private PowerData mDefaultTarget;
    
    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypower_target_view);
        Intent intent = getIntent();
        mMenuPath = intent.getStringExtra(INTENT_KEY_MENUPATH);
        initializeView();
        
        if (mMenuPath != null) {
            String[] menuArray = mMenuPath.split(MENU_STRING_DELIMITER);
            showMenuPath(menuArray);
        }
        
    }
    
    /**
     * 
     */
    @Override
    protected void initializeView() {
        super.initializeView();
        
        mTVCurrentTarget = (TextView) findViewById(R.id.tv_power_target_count);
        mEdNewTarget = (EditText) findViewById(R.id.et_power_target_count);
        mButtonOk = (Button) findViewById(R.id.btn_ok);
        mButtonCancel = (Button) findViewById(R.id.btn_cancel);
        
        mEdNewTarget.setOnFocusChangeListener(new OnFocusChangeListener() {
            
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
//                if(!hasFocus){
//                    String value = mEdNewTarget.getText().toString();
//                    if(value == null || value.trim().isEmpty()){
//                        ToastUtil.showToast(GDPowerTargetSettingActivity.this, R.string.error_text_not_input_power_target);
//                        mIsRightTarget = false;
//                    }else{
//                        try {
//                            int  count = Integer.parseInt(value.trim());
//                            if(count <= 0){
//                                ToastUtil.showToast(GDPowerTargetSettingActivity.this, R.string.error_text_input_power_target_error);
//                                mIsRightTarget = false;
//                            }else{
//                                mIsRightTarget = true;
//                            }
//                        } catch (Exception e) {
//                            mIsRightTarget = false;
//                            ToastUtil.showToast(GDPowerTargetSettingActivity.this,  R.string.error_text_input_power_target_error);
//                            e.printStackTrace()
//                        }
//                    }
//                }
            }
        });
        
        mButtonOk.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                requestSetPowerTarget();
            }
        });
        
        mButtonCancel.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    
    @Override
    protected void onServiceStart() {
        super.onServiceStart();
        requestPowerTarget();
    }
    
    
    private void requestPowerTarget(){
        String CCGUID = getCCUID();
        if(CCGUID == null)
            return;
        
        Map<String, String> parmas = new HashMap<String, String>();
        parmas.put(JsonTag.TAGNumCCGuid, CCGUID);
        mService.requestPowerData(GDConstract.DATATYPE_POWER_TARGET, parmas);
    }
    private void requestDefaultPowerTarget(){
        String CCGUID = getCCUID();
        if(CCGUID == null)
            return;
        
        Map<String, String> parmas = new HashMap<String, String>();
        parmas.put(JsonTag.TAGNumCCGuid, CCGUID);
        mService.requestPowerData(GDConstract.DATATYPE_DEFAULT_POWER_TARGET, parmas);
    }
    
    private void requestSetPowerTarget(){
        String CCGUID = getCCUID();
        if(CCGUID == null)
            return;
        
//        if(!mIsRightTarget){
//            ToastUtil.showToast(this, R.string.error_text_input_power_target_error);
//            return;
//        }
        String powerCount = mEdNewTarget.getText().toString().trim();
        Map<String, String> parmas = new HashMap<String, String>();
        parmas.put(JsonTag.TAGNumCCGuid, CCGUID);
        parmas.put(JsonTag.TAGPowerNum, powerCount + ".00");
        parmas.put(JsonTag.TAGPowerFee, "0.00");
        parmas.put(JsonTag.TAGNumOrFee, "num");
        mService.requestPowerData(GDConstract.DATATYPE_SETTING_POWER_TARGET, parmas); 
    }
    @Override
    public void notifyEvent(int type, Object event) {
        super.notifyEvent(type, event);
        if (EventData.EVENT_GUODIAN_DATA == type) {
            EventData.GuodianEvent guodianEvent = (EventData.GuodianEvent) event;
            if(GDConstract.DATATYPE_POWER_TARGET == guodianEvent.Type){
               mPowerTarget = (PowerTarget) guodianEvent.Data;
               if(mPowerTarget != null && mPowerTarget.mPower != null){
                   mTVCurrentTarget.setText(mPowerTarget.mPower.Count);
               }
            }else if(GDConstract.DATATYPE_DEFAULT_POWER_TARGET == guodianEvent.Type){
                
            }else if(GDConstract.DATATYPE_SETTING_POWER_TARGET == guodianEvent.Type){
                ResultData result = (ResultData) guodianEvent.Data;
                if(result != null){
                    if("true".equals(result.Result)){
                        ToastUtil.showToast(this, R.string.text_set_power_target_success);
                        mTVCurrentTarget.setText(mEdNewTarget.getText().toString());
                    }else{
                        ToastUtil.showToast(this, R.string.text_set_power_target_fail);
                    }
                }else{
                    ToastUtil.showToast(this, R.string.text_set_power_target_fail);
                }
            }
            
        }else if(EventData.EVENT_GUODIAN_DATA_ERROR == type){
            EventData.GuodianEvent guodianEvent = (EventData.GuodianEvent) event;
            
            if(GDConstract.DATATYPE_POWER_TARGET == guodianEvent.Type){
                
            }else if(GDConstract.DATATYPE_DEFAULT_POWER_TARGET == guodianEvent.Type){
                
            }else if(GDConstract.DATATYPE_SETTING_POWER_TARGET == guodianEvent.Type){
                ToastUtil.showToast(this, R.string.text_set_power_target_fail);
            }
        }
    }
}
