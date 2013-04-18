package com.dbstar.app;

import com.dbstar.R;
import com.dbstar.model.ProductItem;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class GDDrmInfoDialog extends Dialog {

	private ListView mDrmInfoList = null;
	private ListAdapter mAdapter = null;
	private ProductItem[] mDrmInfo = null;
	
	public GDDrmInfoDialog(Context context) {
		super(context, R.style.GDAlertDialog);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.publication_drminfo_view);

		mDrmInfoList = (ListView) findViewById(R.id.athorization_info);
		
		mAdapter = new ListAdapter();
		mDrmInfoList.setAdapter(mAdapter);
		
		if (mDrmInfo != null) {
			updateView();
		}
	}
	
	public void setData(ProductItem[] data) {
		mDrmInfo = data;
		if (mAdapter != null) {
			updateView();
		}
	}

	public void updateView() {
		mAdapter.setDataSet(mDrmInfo);
		mAdapter.notifyDataSetChanged();
	}
	
	private class ListAdapter extends BaseAdapter {

		public class ViewHolder {
			TextView contetnId;
			TextView operatorId;
			TextView productId;
			TextView startTime;
			TextView endTime;
		}

		private ProductItem[] mDataSet = null;

		public ListAdapter() {
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
				convertView = inflater.inflate(R.layout.publication_drminfo_item, parent,
						false);

				holder = new ViewHolder();
				holder.contetnId = (TextView) convertView
						.findViewById(R.id.content_id);
				holder.operatorId = (TextView) convertView
						.findViewById(R.id.operator_id);
				holder.productId = (TextView) convertView
						.findViewById(R.id.product_id);

				holder.startTime = (TextView) convertView
						.findViewById(R.id.start_time);

				holder.endTime = (TextView) convertView
						.findViewById(R.id.end_time);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.contetnId.setText(mDataSet[position].ContentID);
			holder.operatorId.setText(mDataSet[position].OperatorID);
			holder.productId.setText(mDataSet[position].ProductID);
			holder.startTime.setText(mDataSet[position].StartTime);
			holder.endTime.setText(mDataSet[position].EndTime);

			return convertView;
		}
	}
}
