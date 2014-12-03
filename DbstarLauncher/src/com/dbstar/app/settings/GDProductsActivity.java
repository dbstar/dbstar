package com.dbstar.app.settings;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dbstar.R;
import com.dbstar.DbstarDVB.DbstarServiceApi;
import com.dbstar.app.GDBaseActivity;
import com.dbstar.model.EventData;
import com.dbstar.model.GDCommon;
import com.dbstar.util.ImageUtil;
import com.dbstar.util.LogUtil;

public class GDProductsActivity extends GDBaseActivity {
	private static final String TAG = "GDReceiveStatusActivity";

	TextView mSmartcardNumberView;
	ListView mProductsList;
	ListAdapter mAdapter;
	String mSmartcardSN;
	String mSNLabelStr = null;
	int mSmartcardState = GDCommon.SMARTCARD_STATE_NONE;
	ProductItem[] mProducts;
	private Bitmap mBitmap;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.products_view);
		initializeView();
		Intent intent = getIntent();
		mMenuPath = intent.getStringExtra(INTENT_KEY_MENUPATH);
		if (mMenuPath != null) {
			showMenuPath(mMenuPath.split(MENU_STRING_DELIMITER));
		}
	}

	public void onServiceStart() {
		super.onServiceStart();

		mSmartcardState = mService.getSmartcardState();

		if (mSmartcardState == GDCommon.SMARTCARD_STATE_INSERTED) {
			getSmartcardData();
		}
	}

	void getSmartcardData() {
		mService.getSmartcardInfo(this, DbstarServiceApi.CMD_DRM_SC_SN_READ);
		mService.getSmartcardInfo(this,
				DbstarServiceApi.CMD_DRM_PURCHASEINFO_READ);
	}

	public void updateData(int type, Object key, Object data) {
		if (data == null)
			return;

		int requestType = (Integer) key;
		if (requestType == DbstarServiceApi.CMD_DRM_SC_SN_READ) {
			mSmartcardSN = (String) data;

			updateSmartcardSN();
		} else if (requestType == DbstarServiceApi.CMD_DRM_PURCHASEINFO_READ) {
			updateProducts((String) data);
		}
	}

	public void notifyEvent(int type, Object event) {
		super.notifyEvent(type, event);

		if (type == EventData.EVENT_SMARTCARD_STATUS) {
			EventData.SmartcardStatus status = (EventData.SmartcardStatus) event;
			mSmartcardState = status.State;

			updateSmartcardState();
		}
	}

	void updateProducts(String data) {
		if (data.length() == 0)
			return;

		String[] items = data.split("\n");
		if (items.length == 0) {
		    LogUtil.d(TAG, " no product info !");
			return;
		}

		ArrayList<ProductItem> products = new ArrayList<ProductItem>();
		for (int i = 0; i < items.length; i++) {
			String[] item = items[i].split("\t");

			if (item.length == 0) {
				continue;
			}

			ProductItem product = new ProductItem();
			product.Name = item[0];
			product.Date = item[1];

			products.add(product);
		}

		if (products.size() > 0) {
			mProducts = products.toArray(new ProductItem[products.size()]);
		}

		mAdapter.setDataSet(mProducts);
		mAdapter.notifyDataSetChanged();
	}

	void updateSmartcardSN() {
		mSmartcardNumberView.setText(mSNLabelStr + mSmartcardSN);
	}

	void updateSmartcardState() {
		if (mSmartcardState == GDCommon.SMARTCARD_STATE_REMOVING) {
			clearSmartcardData();
		} else if (mSmartcardState == GDCommon.SMARTCARD_STATE_INVALID) {
			clearSmartcardData();
		} else if (mSmartcardState == GDCommon.SMARTCARD_STATE_INSERTED) {
			getSmartcardData();
		}

	}

	void clearSmartcardData() {
		mSmartcardNumberView.setText(mSNLabelStr);
		mAdapter.setDataSet(null);
		mAdapter.notifyDataSetChanged();
	}

	public void initializeView() {
		super.initializeView();

		RelativeLayout mContainer = (RelativeLayout) findViewById(R.id.products_view);
		mSmartcardNumberView = (TextView) findViewById(R.id.card_id);
		mProductsList = (ListView) findViewById(R.id.product_list);
		mAdapter = new ListAdapter(this);
		mProductsList.setAdapter(mAdapter);
		
		mSNLabelStr = getResources().getString(R.string.smartcard_number);
		mSmartcardNumberView.setText(mSNLabelStr);
		
		HashMap<String, Bitmap> bitmaps = ImageUtil.parserXmlAndLoadPic(false, false, true);
		
		if (bitmaps != null && bitmaps.containsKey(ImageUtil.App_Key)) {
			mBitmap = bitmaps.get(ImageUtil.App_Key);
			if (mBitmap != null)
				mContainer.setBackgroundDrawable(new BitmapDrawable(mBitmap));
			else
				mContainer.setBackgroundResource(R.drawable.view_background);				
		} else
			mContainer.setBackgroundResource(R.drawable.view_background);			
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mBitmap != null && !mBitmap.isRecycled()) {
			mBitmap.recycle();
			mBitmap = null;
		}
		System.gc();	
	}

	class ProductItem {
		public String Name;
		public String Date;
	}

	private class ListAdapter extends BaseAdapter {

		public class ViewHolder {
			TextView productName;
			TextView time;
		}

		private ProductItem[] mDataSet = null;

		public ListAdapter(Context context) {
		}

		public void setDataSet(ProductItem[] dataSet) {
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
				convertView = inflater.inflate(R.layout.product_listitem,
						parent, false);

				holder = new ViewHolder();
				holder.productName = (TextView) convertView
						.findViewById(R.id.name);
				holder.time = (TextView) convertView.findViewById(R.id.time);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.productName.setText(mDataSet[position].Name);
			holder.time.setText(mDataSet[position].Date);

			return convertView;
		}
	}
}
