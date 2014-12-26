package com.guodian.checkdevicetool;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.util.EncodingUtils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
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
import com.guodian.checkdevicetool.widget.CustomAlertDialog;

public class SelectTestActivity extends Activity {
    
    CustomAlertDialog dialog;
    private TextView mProductSN;
    private ListView mResultList;
    private Button mButton1;
    private Map<String, String> mResult;
    private boolean isStarted;
    private TextView mReusltView;
    private TextView mTestSNFile;
    private TextView mMACView;
    
    private List<String> snFilePathList;
    
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
        
//        mProductSN.setText(getString(R.string.test_product_sn) + getTerminalNum());
        mProductSN.setVisibility(View.GONE);
        
        mTestSNFile = (TextView) findViewById(R.id.selecte_sn);
        mMACView = (TextView) findViewById(R.id.selecte_mac);
                
        mButton1 = (Button) findViewById(R.id.btn1);
        mButton1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
            	String[] snPaths = {Configs.TEST_SN_FILE_CSV_SDA1, Configs.TEST_SN_FILE_CSV_SDB1, Configs.TEST_SN_FILE_CSV_SDC1, 
            			Configs.TEST_SN_FILE_CSV_SDCARD1};
            	snFilePathList = new ArrayList<String>();
            	for (int i = 0; i < snPaths.length; i++) {
            		File file = new File(snPaths[i]);
            		if (file.exists()) {
            			snFilePathList.add(snPaths[i]);
            		}
            	}
            	
            	if (snFilePathList != null && snFilePathList.size() == 1) {            		
            		Log.d("SelectTestActivity", "-------------------snFilePathList.get(0) = " + snFilePathList.get(0));
            		mTestSNFile.setVisibility(View.GONE);
            		isStarted = true;
            		Intent start = new Intent(SelectTestActivity.this, BoardOrAllTestActivity.class);
            		start.putExtra(Configs.TEST_TYPE, Configs.TYPE_SELECTOR_TEST);
            		startActivity(start);
            	} else {
            		mTestSNFile.setVisibility(View.VISIBLE);
            		Log.d("SelectTestActivity", "-------------------snFilePathList.size() = " + snFilePathList.size());
            		mTestSNFile.setText(getResources().getString(R.string.test_sn_file_isOrNot_exits));
            		mButton1.setEnabled(false);
            	}
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
                mMACView.setText("MAC地址：" + getLocalMacAddress(true));
                
                try {
					if (snFilePathList != null && snFilePathList.size() == 1) {
						String snFilePath = snFilePathList.get(0);
						File file = new File(snFilePath);
						byte[] buf = new byte[8];
						if (file.exists()) {
							FileInputStream stream = new FileInputStream(file);
							BufferedInputStream bufInStream = new BufferedInputStream(stream);
							bufInStream.read(buf, 0, buf.length);
							
							String values = new String(buf, 0, buf.length);
							String substring = values.substring(0, 8);
							Log.d("SelectTestActivity", "----------------------substring = (" + substring.charAt(0) + ")");
							substring.trim();
							substring = substring.replaceAll(String.valueOf(substring.charAt(0)), "0");
							Log.d("SelectTestActivity", "-------0---------------substring = " + substring);
							int valueOf = Integer.parseInt(substring, 10);
							
							// 将第一行取出来之后，写当前串号。
							File snFile = new File("/cache/recovery/last_log");
							if (snFile.exists()) {
								String snFormat = "2000317130000000";
								// 改写/cache/recovery/last_log的第一行，就16位							
								
								String reSnStr = snFormat.subSequence(0, (16 - String.valueOf(valueOf).length())) + String.valueOf(valueOf);
								Log.d("SelectTestActivity", "----------------------reSnStr = " + reSnStr);
								RandomAccessFile snRaf = new RandomAccessFile(snFile, "rw");
								snRaf.seek(0);
								snRaf.write(reSnStr.getBytes("utf-8"), 0, reSnStr.getBytes("utf-8").length);
								
								snRaf.close();
								
								mProductSN.setVisibility(View.VISIBLE);
								mProductSN.setText(getString(R.string.test_product_sn) + getTerminalNum());
								mProductSN.setTextColor(Color.WHITE);
								
								valueOf  = valueOf + 1;
								Log.d("SelectTestActivity", "----------------------valueOf = " + valueOf);
								
								String format = "        ";
//								zhengcuiString reValues = String.format("%8d", valueOf);
								String reValues = format.subSequence(0, (8 - String.valueOf(valueOf).length())) + String.valueOf(valueOf);
								
								Log.d("SelectTestActivity", "----------------------reValues = " + reValues);
								
								RandomAccessFile raf = new RandomAccessFile(file, "rw");
								raf.seek(0);
								raf.write(reValues.getBytes("utf-8"), 0, reValues.getBytes("utf-8").length);								
								
								raf.close();
								bufInStream.close();
								stream.close();
							}
						}
						
					}
					
					writeFactoryStatFile();
					getPackageManager().setComponentEnabledSetting(cm, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
				} catch (Exception e) {
					Log.d("SelectTestActivity", "----------------------e = " + e);					
					e.printStackTrace();	
					
					mReusltView.setTextColor(Color.RED);
					mReusltView.setText(R.string.test_fail); 
					mProductSN.setVisibility(View.VISIBLE);
					mProductSN.setText(getString(R.string.test_product_sn) + "维护失败");
					mProductSN.setTextColor(Color.RED);
					mMACView.setText("MAC地址：" + getLocalMacAddress(true));
					getPackageManager().setComponentEnabledSetting(cm, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
				}
            }else{
                mReusltView.setTextColor(Color.RED);
                mReusltView.setText(R.string.test_fail); 
                mProductSN.setVisibility(View.VISIBLE);
				mProductSN.setText(getString(R.string.test_product_sn) + "维护失败");
				mProductSN.setTextColor(Color.RED);
                mMACView.setText("MAC地址：" + getLocalMacAddress(true));
                getPackageManager().setComponentEnabledSetting(cm, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            }
            isStarted = false;
            File file = new File(Configs.TARGET_VIDEO_FILE);
            if(file.exists())
                file.delete();
        }
    }
    
	/**
	 *  获取本机MAC地址方法
	 * @param isEthernet
	 * @return
	 */
	 
	private String getLocalMacAddress(boolean isEthernet) {
		
		String macAddress = "";
		if (isEthernet) {
			String addressFileName = "/sys/class/net/eth0/address";
			File addressFile = new File(addressFileName);
			if (addressFile.exists()) {
				macAddress = readString(addressFile);
				Log.d("DbstarUtil", "getLocalMacAddress" + macAddress);
			}
		} else {
			String addressFileName = "/sys/class/net/wlan0/address";
			File addressFile = new File(addressFileName);
			if (addressFile.exists()) {
				macAddress = readString(addressFile);
				Log.d("DbstarUtil", "getLocalMacAddress" + macAddress);				
			}
		}
		
		if (macAddress != null && !macAddress.isEmpty()) {
			macAddress = macAddress.toUpperCase();
		}
		
		return macAddress;
	}
	
	private String readString(File file) {
		String value = "";
		int BUFFER_SIZE = 8892;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"), BUFFER_SIZE);
			value = reader.readLine();
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return value;
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
    
	private String getTerminalNum() {
		String terminalNum = null;
		try {
			InputStream in = new FileInputStream("/cache/recovery/last_log");
			int length = in.available();
			byte[] bytes = new byte[length];
			in.read(bytes);
			String content = EncodingUtils.getString(bytes, "UTF-8");
			in.close();
			Log.d("SelectTestActivity", "/cache/recovery/last_log  content = " + content);
			
			if (content != null && content.length() > 0) {
				String[] split = content.split("\n");
				if (split != null && split.length >= 0) {
					terminalNum = split[0];
				}
			}
		} catch (Exception e) {
			Log.d("SelectTestActivity", "-------read file failed------" + e);
			e.printStackTrace();
		}
		return terminalNum;
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
                if (mResult.containsKey(getResources().getString(R.string.test_sleep_key))) {
                	mResult.remove(getResources().getString(R.string.test_sleep_key));
                }
                if (mResult.containsKey(getResources().getString(R.string.test_power_light))) {
                	mResult.remove(getResources().getString(R.string.test_power_light));
                }
                Iterator<String> iterator = mResult.keySet().iterator();
                String key ,value;
                titles = new ArrayList<String>();
                results = new ArrayList<String>();
                while(iterator.hasNext()){
					key = iterator.next();
					value = mResult.get(key);
					titles.add(key);
					results.add(value);
					Log.d("SelectTestActivity", "mResult key = " + key + " value = " + value);                    	
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
