package com.guodian.checkdevicetool.testentry;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.dbstar.DbstarDVB.IDbstarService;
import com.dbstar.DbstarDVB.common.Configs;
import com.guodian.checkdevicetool.BoardOrAllTestActivity;
import com.guodian.checkdevicetool.R;

public class GDModuleTest extends TestTask{
    private String mSocketNmuber;
    private String mTurnOffCmd;
    private String mTurnOnCmd;
    private Button mTurnOn;
    private Button mTurnOff;
    private boolean isFirst;
    private IDbstarService service;
    private int textGDModuleCount;
    public GDModuleTest(Context context, Handler handler, int viewId,
            boolean isAuto) {
        super(context, handler, viewId, isAuto);
    }
    
    @Override
    public void start() {
        super.start();
        service = ((BoardOrAllTestActivity)context).getDbService();
        
        if(service == null){
            isAutoToNext = true;
            isShowResult = true;
            sendFailMsg(null);
            return;
        }
        Configs config = ((BoardOrAllTestActivity)context).getConfig();
        if(config != null && config.mScoketNumber!= null && !config.mScoketNumber.isEmpty()){
            mSocketNmuber = config.mScoketNumber;
            mTurnOffCmd = mSocketNmuber +"\t6";
            mTurnOnCmd = mSocketNmuber +"\t7";
            
            mTurnOff = (Button) mLayout.findViewById(R.id.turn_off);
            mTurnOff.setVisibility(View.VISIBLE);
            mTurnOff.requestFocus();
            if(mTurnOff != null){
                isFirst = true;
                mTurnOff.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String result ;
                        if(isFirst){
                           sendCommnd(mTurnOffCmd);
                        }else{
                            sendCommnd(mTurnOnCmd);
                        }
                       
                    }
                });
                
            }else{
                isAutoToNext = true;
                isShowResult = true;
                sendFailMsg(context.getResources().getString(R.string.test_error));
            }
        }else{
            isAutoToNext = true;
            isShowResult = true;
            sendFailMsg(context.getResources().getString(R.string.test_read_configfile_fail));
        }
    }
    
    
    private void sendCommnd( String cmd){
        
        new AsyncTask<String, Integer, String>() {
            
            protected void onPreExecute() {
                mTurnOff.setEnabled(false);
                isShowResult = false;
                isAutoToNext = false;
                sendFailMsg(context.getResources().getString(R.string.test_trunning));
            };
            @Override
            protected String doInBackground(String... params) {
                String cmd = params[0];
                Intent intent;
                String result = null;
                try {
                    intent = service.sendCommand(0x00101, cmd, cmd.length());
                    byte[] bytes = intent.getByteArrayExtra("result");
                    
                    if (bytes != null) {
                       result = new String(bytes,"utf-8");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                return result;
            }
            
            protected void onPostExecute(String result) {
                if(isFirst){
                    if("1".equals(result)){
                        isShowResult = false;
                        isAutoToNext = false;
                        mTurnOff.setText(R.string.test_turn_on);
                        mTurnOff.setEnabled(true);
                        sendFailMsg(context.getResources().getString(R.string.test_trun_off_success));
                        isFirst = false;
                    }else{
                        isShowResult = false;
                        isAutoToNext = false;
                        mTurnOff.setText(R.string.test_turn_off);
                        mTurnOff.setEnabled(true);
                        sendFailMsg(context.getResources().getString(R.string.test_trun_off_fail));
                        textGDModuleCount ++;
                        if(textGDModuleCount < 3){
                            isFirst = true;
                        }else{
                            isShowResult = true;
                            isAutoToNext = true;
                            sendFailMsg(null);
                            mTurnOff.setVisibility(View.GONE);
                        }
                    }
                }else{
                    if("1".equals(result)){
                        isShowResult = true;
                        isAutoToNext = true;
                        mTurnOff.setVisibility(View.GONE);
                        sendSuccessMsg();
                    }else{
                        isShowResult = true;
                        isAutoToNext = true;
                        sendFailMsg(null);
                        mTurnOff.setVisibility(View.GONE);
                    }
                }
            };
        }.execute(cmd);
       
    }
}
