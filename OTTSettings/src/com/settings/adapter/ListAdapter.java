package com.settings.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.settings.bean.OutputMode;
import com.settings.ottsettings.R;

public class ListAdapter  extends BaseAdapter {


	public class ViewHolder {
		public TextView modeViewHighlight;
		public TextView modeView;
		CheckBox checkBox;
	}

	private ArrayList<OutputMode> mDataSet = null;
	private Context mContext;
	public ListAdapter(Context context) {
		this.mContext = context;
	}

	public void setDataSet(ArrayList<OutputMode> dataSet) {
		mDataSet = dataSet;
	}

	@Override
	public int getCount() {
		int count = 0;
		if (mDataSet != null) {
			count = mDataSet.size();
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
			LayoutInflater inflater =  LayoutInflater.from(mContext);
			convertView = inflater.inflate(R.layout.checked_listitem, parent, false);

			holder = new ViewHolder();
			holder.modeView = (TextView) convertView.findViewById(R.id.title);
			holder.modeViewHighlight = (TextView) convertView.findViewById(R.id.highlight_title);
			holder.checkBox = (CheckBox) convertView.findViewById(R.id.checked_indicator);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.modeView.setText(mDataSet.get(position).modeStr);
		holder.modeViewHighlight.setText(mDataSet.get(position).modeStr);
		holder.checkBox.setChecked(mDataSet.get(position).isSelected);

		return convertView;
	}

}
