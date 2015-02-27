package com.dbstar.settings.network;

import java.io.FileOutputStream;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.style.EasyEditSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dbstar.DbstarDVB.IDbstarService;
import com.dbstar.settings.R;
import com.dbstar.settings.base.BaseFragment;
import com.dbstar.settings.utils.APPVersion;
import com.dbstar.settings.utils.SatelliteSetting;
import com.dbstar.settings.utils.SettingsCommon;
import com.dbstar.settings.utils.Utils;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.net.ethernet.EthernetManager;

public class ChannelSelectorPage extends BaseFragment {

	private static final String TAG = "ChannelSelectorPage";
	View mEthernetSwitchButton;
	TextView mEthSwitchTitle;
	CheckBox mEthernetSwitchIndicator;
	
	private IDbstarService mDbstarService ;
	private ServiceConnection mConnection;
	private Runnable mSatellitePatten;
    EditText mSearchRate;
    EditText mSymbolRate;
    EditText mLoaclFrequencyRate;
    Spinner mPolarization;
    Spinner mModulation;
    Button mBuutonSearch;
    Button mButtonReStore;
    Button mButtonStarPattern;
    
    ProgressBar mSingleQuality;
    ProgressBar mSingleStrength;
    
    TextView mTvSingleQualitye;
    TextView mTvSingleStrength;
	View mWifiSwitchButton;
	TextView mWifiSwitchTitle;
	CheckBox mWifiSwitchIndicator;

	Button mNextButton, mPrevButton;

	private WifiManager mWifiManager;
	private EthernetManager mEthManager;
	private SatelliteSetting mSatelliteSetting;
	ArrayAdapter<CharSequence>mPolarizationAdapter;
	ArrayAdapter<CharSequence>mModulationAdapter;
	public ChannelSelectorPage() {
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.network_channel_selectorview,
				container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.i("ChannelSelectorPage", "onActivityCreated");
		if(APPVersion.SATELLITE){
		    mSatelliteSetting = SatelliteSetting.getInstance();
		    initializeView();
		    mSatellitePatten = new Runnable() {
                
                @Override
                public void run() {
                    Log.i("ChannelSelectorPage", "runable");
                    satelliteStarPatten();
                }
            };
		    mConnection = new ServiceConnection() {
                
                @Override
                public void onServiceDisconnected(ComponentName name) {
                    mDbstarService = null;
                }
                
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    mDbstarService = IDbstarService.Stub.asInterface(service);
                    satelliteSearch();
                }
            };
            Intent mIntent = new Intent();
            mIntent.setComponent(new ComponentName("com.dbstar.DbstarDVB", "com.dbstar.DbstarDVB.DbstarService"));
            mActivity.bindService(mIntent, mConnection, Context.BIND_AUTO_CREATE);
		}else{
		    initializeView();
		}


		mWifiManager = (WifiManager) mActivity
				.getSystemService(Context.WIFI_SERVICE);
		mEthManager = (EthernetManager) mActivity
				.getSystemService(Context.ETH_SERVICE);
		
	}

	void initializeView() {
	    
	    if(APPVersion.SATELLITE){
	        initSatelliteView();
	    } else{
	        mActivity.findViewById(R.id.satellie_content).setVisibility(View.GONE);
	    }
		mEthernetSwitchButton = (View) mActivity
				.findViewById(R.id.cable_check_button);
		mEthSwitchTitle = (TextView) mActivity
				.findViewById(R.id.cable_check_title);
		mEthernetSwitchIndicator = (CheckBox) mActivity
				.findViewById(R.id.cable_check_indicator);

		mWifiSwitchButton = (View) mActivity
				.findViewById(R.id.wireless_check_button);
		mWifiSwitchTitle = (TextView) mActivity
				.findViewById(R.id.wireless_check_title);
		mWifiSwitchIndicator = (CheckBox) mActivity
				.findViewById(R.id.wireless_check_indicator);

		mNextButton = (Button) mActivity.findViewById(R.id.nextbutton);
		mPrevButton = (Button) mActivity.findViewById(R.id.prevbutton);
		if(APPVersion.SATELLITE){
		    mPrevButton.setVisibility(View.GONE);
		}else{
		    mPrevButton.setVisibility(View.VISIBLE);
		}
		mEthernetSwitchButton.setOnClickListener(mOnClickListener);
		mWifiSwitchButton.setOnClickListener(mOnClickListener);

		mEthernetSwitchButton.setOnFocusChangeListener(mFocusChangeListener);
		mWifiSwitchButton.setOnFocusChangeListener(mFocusChangeListener);

		mNextButton.setOnClickListener(mOnClickListener);
		mPrevButton.setOnClickListener(mOnClickListener);

//		mEthernetSwitchButton.requestFocus();

		SharedPreferences settings = mActivity.getSharedPreferences(
				NetworkCommon.PREF_NAME_NETWORK, 0);
		String channel = settings.getString(NetworkCommon.KeyChannel,
				NetworkCommon.ChannelEthernet);
		if (channel.equals(NetworkCommon.ChannelEthernet)) {
			setChannelEthernet();
		} else {
			setChannelWireless();
		}
	}
	
	private void initSatelliteView(){
        mActivity.findViewById(R.id.satellie_content).setVisibility(View.VISIBLE);
        mSearchRate = (EditText) mActivity.findViewById(R.id.serarch_rate);
        mSymbolRate = (EditText) mActivity.findViewById(R.id.symbol_rate);
        mLoaclFrequencyRate = (EditText) mActivity.findViewById(R.id.loacl_frequency);
        
        mPolarization = (Spinner) mActivity.findViewById(R.id.polarization_mode);
        mModulation = (Spinner) mActivity.findViewById(R.id.modulation_mode);
        
        mBuutonSearch = (Button) mActivity.findViewById(R.id.btn_statellite_search);
        mButtonReStore = (Button) mActivity.findViewById(R.id.btn_satellite_restore_default);
        mButtonStarPattern = (Button) mActivity.findViewById(R.id.btn_satellite_star_patten);
        
        mSingleQuality = (ProgressBar) mActivity.findViewById(R.id.single_quality);
        mSingleStrength = (ProgressBar) mActivity.findViewById(R.id.single_strength);
        
        mTvSingleQualitye = (TextView) mActivity.findViewById(R.id.single_quality_value);
        mTvSingleStrength = (TextView) mActivity.findViewById(R.id.single_strength_value);
        
        String [] poloarizations = SatelliteSetting.POLARIZATION_ARR;
        String [] modulations = SatelliteSetting.MODULATION_ARR;
        
        mPolarizationAdapter = new ArrayAdapter<CharSequence>(mActivity, android.R.layout.simple_spinner_item, poloarizations);
        mModulationAdapter = new ArrayAdapter<CharSequence>(mActivity, android.R.layout.simple_spinner_item, modulations);
        
        mPolarizationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mModulationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        mPolarization.setAdapter(mPolarizationAdapter);
        mModulation.setAdapter(mModulationAdapter);
        
        String searchRate = null,symbolRate = null,localFrequency = null;
        
        int polarization =0, modulation = 0;
        
        String satellite = mSatelliteSetting.queryValue(SatelliteSetting.SATELLITE);
        if(satellite == null || satellite.trim().isEmpty()){
            satellite = mSatelliteSetting.queryValue(SatelliteSetting.SATELLITE_DEFAULT);
        }
        Log.d(TAG, " satellite = " + satellite);
        String [] satellites = null;
        if(satellite != null)
            satellites = satellite.split(SatelliteSetting.SEPARATOR);
        
        if(satellites != null && satellites.length == SatelliteSetting.TOTAL_PARAMETERS_COUNT){
            searchRate = satellites[SatelliteSetting.SERRCH_RATE];
            symbolRate = satellites[SatelliteSetting.SYMBOL_RATE];
            localFrequency = satellites[SatelliteSetting.LOCAL_FREQUENCY];
            polarization = Utils.getInt(satellites[SatelliteSetting.POLARIZATION_MODE]);
            modulation = Utils.getInt(satellites[SatelliteSetting.MODULATION_MODE]);
        }
        
        Log.d(TAG, " searchRate = " + searchRate);
        Log.d(TAG, " symbolRate = " + symbolRate);
        mSearchRate.setText(searchRate);
        mSymbolRate.setText(symbolRate);
        mLoaclFrequencyRate.setText(localFrequency);
        mPolarization.setSelection(polarization);
        mModulation.setSelection(modulation);
        mBuutonSearch.setOnClickListener(mOnClickListener);
        mButtonReStore.setOnClickListener(mOnClickListener);
        mButtonStarPattern.setOnClickListener(mOnClickListener);
        mBuutonSearch.requestFocus();
        
	}

	boolean mIsEthernetSelected = false;
	boolean mIsWirelessSelected = false;

	private void onWifiChecked() {
		mEthManager.setEthEnabled(true);

		int wifiApState = mWifiManager.getWifiApState();
		if ((wifiApState == WifiManager.WIFI_AP_STATE_ENABLING)
				|| (wifiApState == WifiManager.WIFI_AP_STATE_ENABLED)) {
			mWifiManager.setWifiApEnabled(null, false);
		}

		mWifiManager.setWifiEnabled(true);
	}

	private void onEthernetChecked() {
		mWifiManager.setWifiEnabled(false);
		mEthManager.setEthEnabled(true);
	}

	private void setChannel() {
		String channelValues = null;
		if (mIsEthernetSelected) {
			channelValues = NetworkCommon.ChannelEthernet;
		} else {
			channelValues = NetworkCommon.ChannelBoth;
		}

		SharedPreferences settings = mActivity.getSharedPreferences(
				NetworkCommon.PREF_NAME_NETWORK, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(NetworkCommon.KeyChannel, channelValues);
		editor.commit();
		
		Utils.setValue(NetworkCommon.ChannelFile, channelValues);
		
		Intent intent = new Intent(NetworkCommon.ActionChannelModeChange);
		intent.putExtra(NetworkCommon.KeyChannel, channelValues);
		mActivity.sendBroadcast(intent);
	}

	void setChannelEthernet() {
		mIsEthernetSelected = true;
		mIsWirelessSelected = false;
		mEthernetSwitchIndicator.setChecked(true);
		mWifiSwitchIndicator.setChecked(false);
	}

	void setChannelWireless() {
		mIsWirelessSelected = true;
		mIsEthernetSelected = false;
		mWifiSwitchIndicator.setChecked(true);
		mEthernetSwitchIndicator.setChecked(false);
	}

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
		    int id = v.getId();
			if (id == R.id.cable_check_button) {
				if (mEthernetSwitchIndicator.isChecked())
					return;
				
				mIsEthernetSelected = true;
				mIsWirelessSelected = false;
				mEthernetSwitchIndicator.setChecked(mIsEthernetSelected);
				mWifiSwitchIndicator.setChecked(mIsWirelessSelected);
			} else if (id == R.id.wireless_check_button) {
				if (mWifiSwitchIndicator.isChecked()) {
					return;
				}
				mIsWirelessSelected = true;
				mIsEthernetSelected = false;
				mWifiSwitchIndicator.setChecked(mIsWirelessSelected);
				mEthernetSwitchIndicator.setChecked(mIsEthernetSelected);
			} else if (id == R.id.nextbutton) {
				setChannel();
				if (mIsEthernetSelected) {
					onEthernetChecked();
					mManager.nextPage(SettingsCommon.PAGE_CHANNELSELECTOR,
							SettingsCommon.PAGE_ETHERNET);
				} else {
					onWifiChecked();
					mManager.nextPage(SettingsCommon.PAGE_CHANNELSELECTOR,
							SettingsCommon.PAGE_WIFI);
				}
			} else if (id == R.id.prevbutton) {
				mManager.prevPage(SettingsCommon.PAGE_CHANNELSELECTOR);
			}
			
			if(APPVersion.SATELLITE){
			    if(id == R.id.btn_statellite_search){
			        satelliteSearch();
			    }else if(id == R.id.btn_satellite_restore_default){
			        satelliteRestore();
			    }else if(id == R.id.btn_satellite_star_patten){
			        satelliteStarPatten();
			    }
			    
			}
		}
	};
	
	private String getSatellitePramas(){
	    String searchRate = null,symbolRate = null,localFrequency = null,polarization = null,modulation = null;
        searchRate = mSearchRate.getText().toString().trim();
        symbolRate = mSymbolRate.getText().toString().trim();
        localFrequency = mLoaclFrequencyRate.getText().toString().trim();
        polarization = mPolarization.getSelectedItemPosition()+"";
        modulation = mModulation.getSelectedItemPosition()+"";
        if(searchRate.isEmpty() || symbolRate.isEmpty() || localFrequency.isEmpty()){
            return null;
        }
        StringBuilder params = new StringBuilder();
        params.append(searchRate).append(SatelliteSetting.SEPARATOR)
              .append(symbolRate).append(SatelliteSetting.SEPARATOR)
              .append(localFrequency).append(SatelliteSetting.SEPARATOR)
              .append(polarization).append(SatelliteSetting.SEPARATOR)
              .append(modulation);
        return params.toString();
	}
	private void satelliteSearch(){
	 
	    String params = getSatellitePramas();
	    if(params == null){
	        Toast.makeText(mActivity, R.string.satellite_param_is_null, 1).show();
	        return;
	    }
	    String result = sentSatelliteCmd(0x00302, params.toString());
	    if(mSatelliteSetting != null){
	        mSatelliteSetting.insert(SatelliteSetting.SATELLITE,  params.toString());
	    }
	    updateSatelliteSingleView(result);
	}
	
	private void satelliteRestore(){
        String searchRate = null,symbolRate = null,localFrequency = null;
        
        int polarization =0, modulation = 0;
        
        String satellite = mSatelliteSetting.queryValue(SatelliteSetting.SATELLITE_DEFAULT);
        String [] satellites = null;
        if(satellite != null)
            satellites = satellite.split(SatelliteSetting.SEPARATOR);
        
        if(satellites.length == SatelliteSetting.TOTAL_PARAMETERS_COUNT){
            searchRate = satellites[SatelliteSetting.SERRCH_RATE];
            symbolRate = satellites[SatelliteSetting.SYMBOL_RATE];
            localFrequency = satellites[SatelliteSetting.LOCAL_FREQUENCY];
            polarization = Utils.getInt(satellites[SatelliteSetting.POLARIZATION_MODE]);
            modulation = Utils.getInt(satellites[SatelliteSetting.MODULATION_MODE]);
        }
        mSearchRate.setText(searchRate);
        mSymbolRate.setText(symbolRate);
        mLoaclFrequencyRate.setText(localFrequency);
        mPolarization.setSelection(polarization);
        mModulation.setSelection(modulation);
	}
	
	private void satelliteStarPatten(){
	    String params = getSatellitePramas();
        if(params == null){
            Toast.makeText(mActivity, R.string.satellite_param_is_null, 1).show();
            return;
        }
        String result = sentSatelliteCmd(0x00301, params.toString());
        
        updateSatelliteSingleView(result);
        
        mButtonStarPattern.postDelayed(mSatellitePatten, 1000);
	}
	private void updateSatelliteSingleView(String result){
        int quality = 0,strenth = 0;
        String [] results = null;
        if(result != null){
            results = result.replace("\n", "").split(SatelliteSetting.SEPARATOR);
        }
        if( results != null && results.length == 2){
            try {
                quality = Integer.parseInt(results[0].trim());
                strenth = Integer.parseInt(results[1].trim());
                Log.i("ChannelSelectorPage", "quality = " + quality + "strenth = " + strenth);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        mSingleQuality.setMax(100);
        mSingleQuality.setProgress(quality);
        mSingleStrength.setMax(100);
        mSingleStrength.setProgress(strenth);
        try {
//            mTvSingleQualitye.setText(quality+getResources().getString(R.string.network_satellite_signal_quality_unit));
//            mTvSingleStrength.setText(strenth +getResources().getString(R.string.network_satellite_signal_intensity_unit));
            mTvSingleQualitye.setText(quality + "%");
            mTvSingleStrength.setText(strenth + "%");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
	}
	private String sentSatelliteCmd(int cmd,String value){
	    Log.i(TAG, "sentSatelliteCmd = " + value);
	    Intent intent;
        String result = null;
        try {
            intent=  mDbstarService.sendCommand(cmd, value.toString(), value.length());
            byte[] bytes = intent.getByteArrayExtra("result");

            if (bytes != null) {
                result = new String(bytes, "utf-8");
                Log.i(TAG, "result = " + result); 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
	}
	View.OnFocusChangeListener mFocusChangeListener = new View.OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			Log.d("####", " ========== focus changed ==== " + v.getId() + " "
					+ hasFocus);
			if (hasFocus == true) {
				if (v.getId() == R.id.cable_check_button) {
					mEthSwitchTitle.setTextColor(0xFFFFCC00);
				} else if (v.getId() == R.id.wireless_check_button) {
					mWifiSwitchTitle.setTextColor(0xFFFFCC00);
				}

			} else {
				if (v.getId() == R.id.cable_check_button) {
					mEthSwitchTitle.setTextColor(0xFF000000);
				} else if (v.getId() == R.id.wireless_check_button) {
					mWifiSwitchTitle.setTextColor(0xFF000000);
				}
			}

		}
	};
	
	public void onDestroy() {
	    super.onDestroy();
	    Log.i("ChannelSelectorPage", "onDestroy");
        if(mDbstarService != null && mConnection != null){
            mActivity.unbindService(mConnection);
        }
        if(mButtonStarPattern != null && mSatellitePatten != null){
            mButtonStarPattern.removeCallbacks(mSatellitePatten);
            mSatellitePatten = null;
        }
        
	};
}
