package com.guodian.checkdevicetool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.dbstar.DbstarDVB.common.Configs;
import com.guodian.checkdevicetool.util.DeviceInfoProvider;
import com.guodian.checkdevicetool.widget.CustomAlertDialog;

public class SelectTestActivity extends Activity {
    
    CustomAlertDialog dialog;
    private TextView mProductSN;
    private ListView mResultList;
    private Button mButton1;
    private Map<String, String> mResult;
    private boolean isStarted;
    private TextView mReusltView;
    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            dialog();
        };
    };
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.selecte_view);
        mProductSN = (TextView) findViewById(R.id.product_sn);
        mResultList = (ListView) findViewById(R.id.testResultList);
        mResultList.setFocusable(false);
        mResultList.setEnabled(false);
        mResultList.setFocusableInTouchMode(false);
        mResultList.clearFocus();
        mReusltView = (TextView) findViewById(R.id.tv_result);
        
        mProductSN.setText(getString(R.string.test_product_sn) + DeviceInfoProvider.querypProductSN(Configs.PRODUCT_SN));
        
      
        
        mButton1 = (Button) findViewById(R.id.btn1);
        mButton1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                isStarted = true;
                Intent start = new Intent(SelectTestActivity.this, BoardOrAllTestActivity.class);
                start.putExtra(Configs.TEST_TYPE, Configs.TYPE_SELECTOR_TEST);
                startActivity(start);
            }
        });
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
                return true;
        }else if(keyCode == KeyEvent.KEYCODE_MENU){
            handler.sendMessageDelayed(handler.obtainMessage(1), 10 * 1000);
        }
        return super.onKeyDown(keyCode, event);
    }
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_MENU){
            handler.removeMessages(1);
        }
        return super.onKeyUp(keyCode, event);
    }
    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences preferences = getSharedPreferences(Configs.TEST_TYPE_ORDER_FILE_NAME, Context.MODE_PRIVATE);
        preferences = getSharedPreferences(Configs.TEST_RESULT_PATH, Context.MODE_PRIVATE);
        if(preferences != null)
            mResult = (Map<String, String>) preferences.getAll();
        ResultAdapter adapter = new ResultAdapter();
        mResultList.setAdapter(adapter);
        
        boolean isSuccussful = true;
        if(isStarted){
            mButton1.setVisibility(View.GONE);
            List<String> results = adapter.getResults();
            if(results != null){
                for(String result : results){
                    if(result.equals(getString(R.string.test_statu_fail))){
                        isSuccussful  = false;
                    }
                }
            }
            ComponentName cm = new ComponentName("com.guodian.checkdevicetool", "com.guodian.checkdevicetool.TestTypeReceiver");
            if(isSuccussful){
                mReusltView.setTextColor(Color.WHITE);
                mReusltView.setText(R.string.test_successful);
               // sendBroadcast(new Intent("com.dbstar.settings.action.CLEAR_SETTINGS"));
                writeFactoryStatFile();
                getPackageManager().setComponentEnabledSetting(cm, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            }else{
                mReusltView.setTextColor(Color.RED);
                mReusltView.setText(R.string.test_fail); 
                getPackageManager().setComponentEnabledSetting(cm, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            }
            isStarted = false;
            File file = new File(Configs.TARGET_VIDEO_FILE);
            if(file.exists())
                file.delete();
        }
    }
    private void writeFactoryStatFile(){
        File file = new File("/data/dbstar/product");
        if(!file.exists())
            file.mkdirs();
        FileOutputStream fos = null;
        try {
            String setflagValues = "1";
            byte[] setflag = setflagValues.getBytes();
            fos = new FileOutputStream(new File(file, "factory.stat"));
            fos.write(setflag);

        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    protected void dialog() {
        dialog = CustomAlertDialog.getInstance(this, new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.sure) {
                    ComponentName cm = new ComponentName("com.guodian.checkdevicetool", "com.guodian.checkdevicetool.TestTypeReceiver");
                    int id = dialog.getCheckedButtonId();
                    if (id == R.id.yes) {
                        getPackageManager().setComponentEnabledSetting(cm, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                    } else if (id == R.id.no) {
                        sendBroadcast(new Intent("com.dbstar.settings.action.CLEAR_SETTINGS"));
                        getPackageManager().setComponentEnabledSetting(cm, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                    }
                    File file = new File(Configs.TARGET_VIDEO_FILE);
                    if(file.exists())
                        file.delete();
                    dialog.dismiss();
                    finish();
                } else if (v.getId() == R.id.cancel) {
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    
    class ResultAdapter extends BaseAdapter{
        
        ViewHolder v;
        private List<String> titles;
        private List<String> results;
        
        public ResultAdapter() {
            if(mResult != null){
                mResult.remove(Configs.PLAY_TIME);
                Iterator<String> iterator = mResult.keySet().iterator();
                String key ,value;
                titles = new ArrayList<String>();
                results = new ArrayList<String>();
                while(iterator.hasNext()){
                    key = iterator.next();
                    value = mResult.get(key);
                    titles.add(key);
                    results.add(value);
                }
                
                
            }
        }
        @Override
        public int getCount() {
            if(mResult != null)
                return mResult.size();
            else
                return 0;
        }
        public List<String> getResults(){
            return results;
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
                convertView = LayoutInflater.from(SelectTestActivity.this).inflate(R.layout.result_item, null);
                v = new ViewHolder();
                v.mTitle  = (TextView) convertView.findViewById(R.id.title);
                v.mResult = (TextView) convertView.findViewById(R.id.result);
                convertView.setTag(v);
            }else{
                v = (ViewHolder) convertView.getTag();
            }
            v.mTitle .setText(titles.get(position));
            v.mResult .setText(results.get(position));
            if(results.get(position).equals(getString(R.string.test_statu_fail))){
                v.mResult.setTextColor(Color.RED);
            }else{
                v.mResult.setTextColor(Color.WHITE);
            }
            return convertView;
        }
        
        class ViewHolder{
            TextView mTitle;
            TextView mResult;
        }
    } 
}