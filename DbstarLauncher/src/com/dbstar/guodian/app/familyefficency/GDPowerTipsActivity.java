package com.dbstar.guodian.app.familyefficency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dbstar.R;
import com.dbstar.guodian.app.base.GDSmartActivity;
import com.dbstar.guodian.data.PowerTips;
import com.dbstar.guodian.engine1.GDRequestType;
import com.dbstar.guodian.engine1.RequestParams;
import com.dbstar.model.EventData;
import com.dbstar.util.DateUtil;
import com.dbstar.widget.CircleFlowIndicator;
import com.dbstar.widget.GDNewsViewGoup;
import com.dbstar.widget.GDNewsViewGoup.OnUpdatePageListener;

public class GDPowerTipsActivity extends GDSmartActivity {

	private static final String TAG = "GDEnergyConservationNoticesActivity";

	private static final int MODE_LIST = 0;
	private static final int MODE_DETAIL = 1;

	private static final int PageSize = 8;
	private ArrayList<PowerTips[]> mPagesData;
	private int mPageCount, mPageNumber;
	private int mNoticesCount;

	private ListView mListView;
	private ListAdapter mNoticesAdapter;
	private int mViewMode;
	private ViewGroup mDetailContainer, mListContainer;
	private TextView mTitle;
    private GDNewsViewGoup mGdNewsViewGoup;
	private TextView mItemCountView, mPageNumberView;

	private String mStrGong, mStrTiao, mStrDi, mStrYe;
	
	private CircleFlowIndicator mIndicator;
    private TextView mContentPageCount,mContentPgeNumber;
    
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.family_power_efficency_power_tips);
		Intent intent = getIntent();
		mMenuPath = intent.getStringExtra(INTENT_KEY_MENUPATH);

		initializeView();

		if (mMenuPath != null) {
			String[] menuArray = mMenuPath.split(MENU_STRING_DELIMITER);
			showMenuPath(menuArray);
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK: {
			if (mViewMode == MODE_DETAIL) {
				showDetail(false);
				return true;
			}
			break;
		}
		}

		return super.onKeyDown(keyCode, event);
	}

	public void initializeView() {
		super.initializeView();

		mStrGong = getResources().getString(R.string.text_gong);
		mStrTiao = getResources().getString(R.string.text_tiao);
		mStrDi = getResources().getString(R.string.text_di);
		mStrYe = getResources().getString(R.string.text_ye);

		mViewMode = MODE_LIST;

		mItemCountView = (TextView) findViewById(R.id.notices_count);
		mPageNumberView = (TextView) findViewById(R.id.notices_pages);
		
		mContentPageCount = (TextView) findViewById(R.id.content_page_count);
        mContentPgeNumber = (TextView) findViewById(R.id.content_page_number);
        mIndicator = (CircleFlowIndicator) findViewById(R.id.indicator);
		
		mListContainer = (ViewGroup) findViewById(R.id.list_view);
		mListView = (ListView) findViewById(R.id.listview);
		mNoticesAdapter = new ListAdapter();
		mListView.setAdapter(mNoticesAdapter);
		
		mDetailContainer = (ViewGroup) findViewById(R.id.detail);
		mDetailContainer.setVisibility(View.GONE);
		
		mTitle = (TextView) findViewById(R.id.title);
		mGdNewsViewGoup = (GDNewsViewGoup) findViewById(R.id.content);
		//mContent = (TextView) findViewById(R.id.content);
		//mContent.setMovementMethod(new ScrollingMovementMethod(true));
		 mGdNewsViewGoup.setOnUpdatePageListener(new OnUpdatePageListener() {
             
             @Override
             public void onUpdate(int totalPage, int currentPage) {
                 mIndicator.setPageCount(totalPage);
                 mIndicator.setCurrentPage(currentPage -1);
                 mContentPageCount.setText(String.valueOf(totalPage));
                 mContentPgeNumber.setText(String.valueOf(currentPage));
             }
         });
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				showDetail(true);
			}

		});

		mListView.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				Log.d(TAG, " ===== onKey === " + keyCode);
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

	private void loadPrevPage() {
		Log.d(TAG, "loadPrevPage count=" + mPageCount + " number= "
				+ mPageNumber);

		Log.d(TAG, "loadPrevPage");
		mPageNumber--;

		PowerTips [] notices = mPagesData.get(mPageNumber);
		mNoticesAdapter.setDataSet(notices);
		mListView.clearChoices();
		mListView.setSelection(notices.length);
		mNoticesAdapter.notifyDataSetChanged();
		
		displayPageNumber(mPageNumber);
	}

	private void loadNextPage() {
		Log.d(TAG, "loadNextPage count=" + mPageCount + " number= "
				+ mPageNumber);

		mPageNumber++;

		PowerTips [] notices = mPagesData.get(mPageNumber);
		mNoticesAdapter.setDataSet(notices);
		mListView.clearChoices();
		mListView.setSelection(0);
		mNoticesAdapter.notifyDataSetChanged();
		
		displayPageNumber(mPageNumber);
	}

	protected void onServiceStart() {
		super.onServiceStart();
		Log.d(TAG, "onServiceStart");
		mSystemFlag = "sml";
        mRequestMethodId = "m005f001";
        RequestParams params =  new RequestParams(GDRequestType.DATATYPE_POWER_TIPS);
        params.put(RequestParams.KEY_SYSTEM_FLAG, mSystemFlag);
        params.put(RequestParams.KEY_METHODID, mRequestMethodId);
		requestData(params);
	}

	public void notifyEvent(int type, Object event) {
	    super.notifyEvent(type, event);
		if (type == EventData.EVENT_GUODIAN_DATA) {
			EventData.GuodianEvent guodianEvent = (EventData.GuodianEvent) event;
			handlePowerData(guodianEvent.Type, guodianEvent.Data);
		}else if(EventData.EVENT_GUODIAN_DATA_ERROR == type){
		    showErrorMsg( R.string.loading_error);
		    return;
		}
	}

	private void handlePowerData(int type, Object data) {
		if (data == null) {
			Log.d(TAG, "ERROR: data is null");
			return;
		}

		if (type == GDRequestType.DATATYPE_POWER_TIPS) {
			ArrayList<PowerTips> notices = (ArrayList<PowerTips>) data;
			sortList(notices);
			cutOffDateString(notices);
			constructPages(notices);
			displayPage(mPageNumber);
		}
	}
	private void cutOffDateString(ArrayList<PowerTips> notices ){
	    for(PowerTips tip : notices){
	        tip.TipsLastUpdateTime = tip.TipsLastUpdateTime.substring(0,10);
	    }
	}
	private void constructPages(ArrayList<PowerTips> notices) {
		int size = notices.size();
		mNoticesCount = size;
		if (size == 0) {
			mPageNumber = size;
			mPageCount = size;
			return;
		}

		mPageCount = size / PageSize;
		if (size % PageSize > 0) {
			mPageCount++;
		}

		mPagesData = new ArrayList<PowerTips[]>();

		int index = 0;
		for (int i = 0; i < mPageCount; i++) {
			int pageSize = Math.min(PageSize, size - index);

			PowerTips[] page = new PowerTips[pageSize];
			for (int j = 0; j < pageSize; j++) {
				page[j] = notices.get(index);
				index++;
			}

			mPagesData.add(page);
		}
	}

	private void displayPage(int pageNumber) {

	    PowerTips[] page = mPagesData.get(pageNumber);
		mNoticesAdapter.setDataSet(page);
		mNoticesAdapter.notifyDataSetChanged();
		displayPageNumber(pageNumber);
		mListView.setFocusableInTouchMode(true);
	    mListView.setFocusable(true);
	    mListView.requestFocus();
	}
	
	private void displayPageNumber(int pageNumber) {
		mItemCountView.setText(mStrGong + mNoticesCount + mStrTiao);
		mPageNumberView.setText(mStrDi + (pageNumber + 1) + mStrYe + "/"
				+ mStrGong + mPageCount + mStrYe);
	}

	private void showDetail(boolean show) {
		if (show) {

			int index = mListView.getSelectedItemPosition();
			PowerTips [] page = mPagesData.get(mPageNumber);
			PowerTips notice = page[index];

			mTitle.setText(notice.TipsTitle);
			mGdNewsViewGoup.setData(null, notice.TipsContent);
            mListView.setFocusableInTouchMode(false);
            mListView.setFocusable(false);
            mListView.clearFocus();
			mListContainer.setVisibility(View.GONE);
			mDetailContainer.setVisibility(View.VISIBLE);
			mViewMode = MODE_DETAIL;

			mDetailContainer.requestLayout();
		} else {
			mListContainer.setVisibility(View.VISIBLE);
			mDetailContainer.setVisibility(View.GONE);
            mListView.setFocusableInTouchMode(true);
            mListView.setFocusable(true);
            mListView.requestFocus();
			mViewMode = MODE_LIST;
		}
	}

	private class ListAdapter extends BaseAdapter {

		public class ViewHolder {
			TextView index;
			TextView title;
			TextView date;
		}

		private PowerTips [] mDataSet = null;

		public ListAdapter() {
		}

		public void setDataSet(PowerTips[] dataSet) {
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
				convertView = inflater.inflate(R.layout.gd_news_flash_item,
						parent, false);

				holder = new ViewHolder();
				holder.index = (TextView) convertView.findViewById(R.id.index);
				holder.title = (TextView) convertView.findViewById(R.id.title);

				holder.date = (TextView) convertView.findViewById(R.id.date);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			int index = mPageNumber * PageSize + position + 1;
			holder.index.setText(String.valueOf(index)+ "、");
			holder.title.setText(mDataSet[position].TipsTitle);
			holder.date.setText(mDataSet[position].TipsLastUpdateTime);

			return convertView;
		}
	}
	
	private void sortList(List<PowerTips> list){
        Collections.sort(list, new Comparator<PowerTips>() {

            @Override
            public int compare(PowerTips lhs, PowerTips rhs) {
            Date ld =  DateUtil.getDateFromStr(lhs.TipsLastUpdateTime, DateUtil.DateFormat1);
            Date rd = DateUtil.getDateFromStr(rhs.TipsLastUpdateTime, DateUtil.DateFormat1);
            Long lhsTime = ld.getTime();
            Long rhsTime = rd.getTime();
              return rhsTime.compareTo(lhsTime);
            }
        });
    }
}
