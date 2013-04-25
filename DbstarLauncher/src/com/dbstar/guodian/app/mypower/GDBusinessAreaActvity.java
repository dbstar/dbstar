package com.dbstar.guodian.app.mypower;

import java.lang.reflect.Array;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

import com.dbstar.R;
import com.dbstar.app.GDBaseActivity;
import com.dbstar.guodian.data.AreaInfo;
import com.dbstar.guodian.data.AreaInfo.Area;
import com.dbstar.guodian.data.BusinessArea;
import com.dbstar.guodian.data.Notice;
import com.dbstar.guodian.egine.GDConstract;
import com.dbstar.model.EventData;
import com.dbstar.model.ContentData.Poster;

public class GDBusinessAreaActvity extends GDBaseActivity {
	private static final String TAG = "GDBusinessAreaActvity";

	private String mAreaId = null;
	private static final int PageSize = 6;
	private ArrayList<BusinessArea[]> mPagesData;
	private int mPageCount, mPageNumber;
	private String mUserProvinceId, mUserCityId, mUserZoneId;
	private ListView mListView;
	private ListAdapter mBusinessAdapter;
	private Button mQueryButton;
	private Spinner mProvinceSpinner, mCitySpinner, mZoneSpinner;
	private ArrayAdapter<String> mProvinceAdapter, mCityAdapter, mZoneAdapter;
	private ArrayList<String> mProvinceList, mCityList, mZoneList;
	private int mCurProvinceIndex = -1, mCurCityIndex = -1, mCurZoneIndex = -1;
	private AreaInfo mAreaData;
	private String mStrGong, mStrTiao, mStrDi, mStrYe;
	private TextView mItemCountView, mPageNumberView;
	private int mItemsCount;
	boolean isFirstLoad = true;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.mypower_businessareaview);

		Intent intent = getIntent();
		mMenuPath = intent.getStringExtra(INTENT_KEY_MENUPATH);
		mAreaId = intent.getStringExtra(GDConstract.KeyUserAreaId);
		if (mAreaId != null && !mAreaId.isEmpty()) {
			String[] ids = mAreaId.split("-");
			if (ids.length > 1) {
				mUserProvinceId = ids[1];
			}

			if (ids.length > 2) {
				mUserCityId = ids[2];
			}

			if (ids.length > 3) {
				mUserZoneId = ids[3];
			}
		}

		initializeView();

		if (mMenuPath != null) {
			String[] menuArray = mMenuPath.split(MENU_STRING_DELIMITER);
			showMenuPath(menuArray);
		}
	}

	public void initializeView() {
		super.initializeView();

		mStrGong = getResources().getString(R.string.text_gong);
		mStrTiao = getResources().getString(R.string.text_tiao);
		mStrDi = getResources().getString(R.string.text_di);
		mStrYe = getResources().getString(R.string.text_ye);

		mItemCountView = (TextView) findViewById(R.id.count);
		mPageNumberView = (TextView) findViewById(R.id.pages);

		mProvinceSpinner = (Spinner) findViewById(R.id.province_spinner);
		mCitySpinner = (Spinner) findViewById(R.id.city_spinner);
		mZoneSpinner = (Spinner) findViewById(R.id.district_spinner);

		mProvinceList = new ArrayList<String>();
		mCityList = new ArrayList<String>();
		mZoneList = new ArrayList<String>();

		mProvinceAdapter = new ArrayAdapter<String>(this,
				R.layout.spinner_item, mProvinceList);

		mProvinceSpinner.setAdapter(mProvinceAdapter);

		mCityAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item,
				mCityList);
		mCitySpinner.setAdapter(mCityAdapter);
		mZoneAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item,
				mZoneList);
		mZoneSpinner.setAdapter(mZoneAdapter);

		mProvinceSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent, View v,
							int position, long id) {
						onProvinceChanged(position);
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}

				});
		mCitySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int position, long id) {
				onCityChanged(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}

		});

		mQueryButton = (Button) findViewById(R.id.mypower_query_button);

		mQueryButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				quearyBusinessInfo(null);
			}
		});

		mListView = (ListView) findViewById(R.id.listview);
		mBusinessAdapter = new ListAdapter();
		mListView.setAdapter(mBusinessAdapter);

		mListView.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				boolean ret = false;
				int action = event.getAction();
				if (action == KeyEvent.ACTION_DOWN) {
					switch (keyCode) {

					case KeyEvent.KEYCODE_DPAD_UP: {
						int selectedIndex = mListView.getSelectedItemPosition();
						if (selectedIndex == 0 && mPageNumber > 0) {
							loadPrevPage();
							ret = true;
						}
						break;
					}
					case KeyEvent.KEYCODE_DPAD_DOWN: {
						int selectedIndex = mListView.getSelectedItemPosition();
						if (selectedIndex == (PageSize - 1)
								&& mPageNumber < mPageCount - 1) {
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
	}

	protected void onServiceStart() {
		super.onServiceStart();
		Log.d(TAG, "onServiceStart");

		mService.requestPowerData(GDConstract.DATATYPE_USERAREAINFO, mAreaId);
	}

	private void quearyBusinessInfo(String areaId) {
	    if(mPagesData != null){
	       mBusinessAdapter.setDataSet(null);
	       mBusinessAdapter.notifyDataSetChanged();
	       mPagesData.clear();
	       mItemsCount = 0;
	       mPageNumber = 0;
	       mPageCount = 1;
	       displayPage(mPageNumber);
	    }
		if (areaId == null) {

			AreaInfo.Area province = null;
			if (mProvinceList.size() > 0) {
				int index = mProvinceSpinner.getSelectedItemPosition();
				Log.d(TAG, " province index = " + index);

				if (index >= 0) {
					province = mAreaData.Provinces.get(index);
				}
			}

			AreaInfo.Area city = null;
			if (mCityList.size() > 0) {
				int index = mCitySpinner.getSelectedItemPosition();
				Log.d(TAG, " city index = " + index);
				if (index >= 0 && province.SubArea != null && province.SubArea.size() > index) {
					city = province.SubArea.get(index);
				}
			}

			AreaInfo.Area zone = null;

			if (mZoneList.size() > 0) {
				int index = mZoneSpinner.getSelectedItemPosition();
				Log.d(TAG, " zone index = " + index);
				if (index >= 0 && city != null && city.SubArea != null && city.SubArea.size() > index) {
					zone = city.SubArea.get(index);
				}
			}

			if (zone != null) {
				areaId = zone.Guid;
			}
		}

		if (areaId != null) {
			mService.requestPowerData(GDConstract.DATATYPE_BUSINESSAREA, areaId);
		}
	}

	public void notifyEvent(int type, Object event) {
		super.notifyEvent(type, event);

		if (type == EventData.EVENT_GUODIAN_DATA) {
			EventData.GuodianEvent guodianEvent = (EventData.GuodianEvent) event;
			handlePowerData(guodianEvent.Type, guodianEvent.Data);
		}
	}

	private void handlePowerData(int type, Object data) {
		if (data == null) {
			Log.d(TAG, "ERROR: data is null");
			return;
		}

		if (type == GDConstract.DATATYPE_USERAREAINFO) {
			mAreaData = (AreaInfo) data;
			isFirstLoad = true;
			initializeAreaData(mAreaData);

			if (mCurProvinceIndex >= 0) {
				mProvinceSpinner.setSelection(mCurProvinceIndex);
			}
			if(mAreaData.CityName != null && !mAreaData.CityName.equals("")){
			    mCityList.add(mAreaData.CityName);
			    mCityAdapter.notifyDataSetChanged();
			}
			if(mAreaData.ZoneName != null && !mAreaData.ZoneName.equals("")){
			    mZoneList.add(mAreaData.ZoneName);
			    mZoneAdapter.notifyDataSetChanged();
			}
			
//			if (mCurCityIndex >= 0) {
//				mCitySpinner.setSelection(mCurCityIndex);
//			}

			if (mAreaData.BusinessList != null) {
				constructPages(mAreaData.BusinessList);
				displayPage(mPageNumber);
			}

		} else if (type == GDConstract.DATATYPE_BUSINESSAREA) {
			ArrayList<BusinessArea> business = (ArrayList<BusinessArea>) data;
			constructPages(business);
			displayPage(mPageNumber);
		}else if(type == GDConstract.DATATYPE_CITYES){
            ArrayList<AreaInfo.Area> citys = (ArrayList<AreaInfo.Area>) data;
            initializeCitysData(citys);
		}else if(type == GDConstract.DATATYPE_ZONES){
		     ArrayList<AreaInfo.Area> zones = (ArrayList<AreaInfo.Area>) data;
		     initializeZonesData(zones);
		}

	}

	private void initializeCitysData(ArrayList<Area> citys) {
	    mCurCityIndex = 0;
        mCityList.clear();
        AreaInfo.Area province = mAreaData.Provinces.get(mCurProvinceIndex);
        if(citys != null && !citys.isEmpty()){
            province.SubArea = citys;
            for(int i = 0 ; i< citys.size() ; i++){
                AreaInfo.Area city = citys.get(i);
                mCityList.add(city.Name);
                if (isFirstLoad && mUserCityId != null && !mUserCityId.isEmpty()
                        && mUserCityId.equals(city.Guid)) {
                    mCurCityIndex = i;
                    
                }
            }
        }else{
            isFirstLoad = false;
        }
        mCityAdapter.notifyDataSetChanged();
        mCitySpinner.setSelection(mCurCityIndex);
    }
	private void initializeZonesData(ArrayList<Area> zones) {
	    mZoneList.clear();
	    mCurZoneIndex = 0;
        AreaInfo.Area province = mAreaData.Provinces.get(mCurProvinceIndex);
        if(province.SubArea == null )
            return;
        AreaInfo.Area citye = province.SubArea.get(mCurCityIndex);
        if(zones != null && !zones.isEmpty()){
            citye.SubArea = zones;
            for(int i = 0 ; i< zones.size() ; i++){
                AreaInfo.Area zone = zones.get(i);
                mZoneList.add(zone.Name);
                if (isFirstLoad && mUserZoneId != null && !mUserZoneId.isEmpty()
                        && mUserZoneId.equals(zone.Guid)) {
                    mCurZoneIndex = i;
                    isFirstLoad = false;
                    
                }
            }
        }else{
            isFirstLoad = false;
        }
        mZoneAdapter.notifyDataSetChanged();
        mZoneSpinner.setSelection(mCurZoneIndex);
    }
    private void reqeustCitysByPId(String pid){
	   mService.requestPowerData(GDConstract.DATATYPE_CITYES, pid);
	   Log.i("Futao", "request city " + pid);
	}
    private void reqeustZonesByCId(String cid){
        Log.i("Futao", "request zone " + cid);
        mService.requestPowerData(GDConstract.DATATYPE_ZONES, cid);
     }
	private void initializeAreaData(AreaInfo areaInfo) {
		ArrayList<AreaInfo.Area> provinces = areaInfo.Provinces;
		if (provinces == null || provinces.size() == 0)
			return;

		AreaInfo.Area userProvince = null;
		mProvinceList.clear();
		int size = provinces.size();
		for (int i = 0; i < size; i++) {
			AreaInfo.Area p = provinces.get(i);
			mProvinceList.add(p.Name);
			Log.d(TAG, " province name = " + p.Name);

			if (mUserProvinceId != null && !mUserProvinceId.isEmpty()
					&& mUserProvinceId.equals(p.Guid)) {
				userProvince = p;
				mCurProvinceIndex = i;
			}
		}

		mProvinceAdapter.notifyDataSetChanged();

		if (mCurProvinceIndex < 0) {
			mCurProvinceIndex = 0;
			userProvince = provinces.get(mCurProvinceIndex);
		}
		Log.d(TAG, " mCurProvinceIndex = " + mCurProvinceIndex);
		
		
		AreaInfo.Area userCity = null;
		//AreaInfo.Area defaultCity = null;
		if (userProvince != null && userProvince.SubArea != null) {
			ArrayList<AreaInfo.Area> cities = userProvince.SubArea;
			size = cities.size();

			mCityList.clear();
			for (int i = 0; i < size; i++) {
				AreaInfo.Area c = cities.get(i);
				mCityList.add(c.Name);

				Log.d(TAG, " city name = " + c.Name);

				if (mUserCityId != null && !mUserCityId.isEmpty()
						&& mUserCityId.equals(c.Guid)) {
					userCity = c;
					mCurCityIndex = i;
				}
			}

			if (mCurCityIndex < 0) {
				mCurCityIndex = 0;

				userCity = cities.get(mCurCityIndex);
			}

			mCityAdapter.notifyDataSetChanged();
		}

		Log.d(TAG, " mCurCityIndex = " + mCurProvinceIndex);

		AreaInfo.Area userZone = null;
		if (userCity != null && userCity.SubArea != null) {
			ArrayList<AreaInfo.Area> zones = userCity.SubArea;
			size = zones.size();

			mZoneList.clear();
			for (int i = 0; i < size; i++) {
				AreaInfo.Area z = zones.get(i);
				mZoneList.add(z.Name);
				Log.d(TAG, " zone name = " + z.Name);

				if (mUserZoneId != null && !mUserZoneId.isEmpty()
						&& mUserZoneId.equals(z.Guid)) {
					userZone = z;
					mCurZoneIndex = i;
				}
			}

			if (mCurZoneIndex < 0) {
				mCurZoneIndex = 0;

				userZone = zones.get(mCurZoneIndex);
			}

			mZoneAdapter.notifyDataSetChanged();
		}

		Log.d(TAG, " mCurZoneIndex = " + mCurZoneIndex);
	}

	void onProvinceChanged(int index) {
		Log.d(TAG, " onProvinceChanged " + index);
		mCurProvinceIndex = index;
		final AreaInfo.Area province = mAreaData.Provinces.get(index);
		if(!isFirstLoad){
    		mCityList.clear();
    		mZoneList.clear();
    		mCityAdapter.notifyDataSetChanged();
    		mZoneAdapter.notifyDataSetChanged();
		}
	    if(province.SubArea == null || province.SubArea.isEmpty()){
		    reqeustCitysByPId(province.Guid);
		}else{
           
            mCitySpinner.post(new Runnable() {
                @Override
                public void run() {
                    initializeCitysData(province.SubArea);
                }
            });
		}
	}

	void onCityChanged(int index) {
		Log.d(TAG, " onCityChanged " + index);
		
		mCurCityIndex = index;
		AreaInfo.Area province = mAreaData.Provinces.get(mCurProvinceIndex);
		AreaInfo.Area city = null;
		if(province.SubArea != null && !province.SubArea.isEmpty())
		   city = province.SubArea.get(mCurCityIndex);
		if(city == null){
		    reqeustZonesByCId(mUserCityId);
		    return;
		}
		mZoneList.clear();
		if(city.SubArea == null || city.SubArea.isEmpty()){
		    reqeustZonesByCId(city.Guid);
		}else{
		    initializeZonesData(city.SubArea);
		}
	}

	private void displayPage(int pageNumber) {
	    BusinessArea[] page = null;
	    if(mPagesData != null && mPagesData.size() > pageNumber){
    		 page = mPagesData.get(pageNumber);
	    }
	    mBusinessAdapter.setDataSet(page);
	    mBusinessAdapter.notifyDataSetChanged();
		
		displayPageNumber(pageNumber);
	}
	
	private void displayPageNumber(int pageNumber) {
		mItemCountView.setText(mStrGong + mItemsCount + mStrTiao);
		mPageNumberView.setText(mStrDi + (pageNumber + 1) + mStrYe + "/"
				+ mStrGong + mPageCount + mStrYe);
	}

	private void constructPages(ArrayList<BusinessArea> items) {
		int size = items.size();
		mItemsCount = size;
		if (size == 0) {
			mPageNumber = size;
			mPageCount = size;
			Toast.makeText(this, R.string.load_data_is_null, Toast.LENGTH_SHORT).show();
			return;
		}

		mPageCount = size / PageSize;
		if (size % PageSize > 0) {
			mPageCount++;
		}

		mPagesData = new ArrayList<BusinessArea[]>();

		int index = 0;
		for (int i = 0; i < mPageCount; i++) {
			int pageSize = Math.min(PageSize, size - index);
			BusinessArea[] page = new BusinessArea[pageSize];
			for (int j = 0; j < pageSize; j++) {
				page[j] = items.get(index);
				index++;
			}

			mPagesData.add(page);
		}
	}

	private void loadPrevPage() {
		Log.d(TAG, "loadPrevPage count=" + mPageCount + " number= "
				+ mPageNumber);

		Log.d(TAG, "loadPrevPage");
		mPageNumber--;

		BusinessArea[] items = mPagesData.get(mPageNumber);
		mBusinessAdapter.setDataSet(items);
		mListView.clearChoices();
		mListView.setSelection(items.length);
		mBusinessAdapter.notifyDataSetChanged();
		
		displayPageNumber(mPageNumber);
	}

	private void loadNextPage() {
		Log.d(TAG, "loadNextPage count=" + mPageCount + " number= "
				+ mPageNumber);

		mPageNumber++;

		BusinessArea[] items = mPagesData.get(mPageNumber);
		mBusinessAdapter.setDataSet(items);
		mListView.clearChoices();
		mListView.setSelection(0);
		mBusinessAdapter.notifyDataSetChanged();
		
		displayPageNumber(mPageNumber);
	}

	private class ListAdapter extends BaseAdapter {

		public class ViewHolder {
			TextView index;
			TextView name;
			TextView phone;
			TextView address;
			TextView time;
		}

		private BusinessArea[] mDataSet = null;

		public ListAdapter() {
		}

		public void setDataSet(BusinessArea[] dataSet) {
			mDataSet = dataSet;
		}

		@Override
		public int getCount() {
			int count = 0;
			if (mDataSet != null) {
				count = mDataSet.length;
			}
			return count;
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
			ViewHolder holder = null;
			if (null == convertView) {
				LayoutInflater inflater = getLayoutInflater();
				convertView = inflater.inflate(
						R.layout.mypower_business_listitem, parent, false);

				holder = new ViewHolder();
				holder.index = (TextView) convertView.findViewById(R.id.index);
				holder.name = (TextView) convertView.findViewById(R.id.name);

				holder.phone = (TextView) convertView.findViewById(R.id.phone);
				holder.address = (TextView) convertView
						.findViewById(R.id.address);
				holder.time = (TextView) convertView.findViewById(R.id.time);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			int index = mPageNumber * PageSize + position + 1;
			holder.index.setText(String.valueOf(index));
			holder.name.setText(mDataSet[position].Name);
			holder.phone.setText(mDataSet[position].Telephone);
			holder.address.setText(mDataSet[position].Address);
			holder.time.setText(mDataSet[position].WorkTime);

			return convertView;
		}
	}
}
