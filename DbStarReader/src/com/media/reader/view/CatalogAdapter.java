package com.media.reader.view;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.media.android.dbstarplayer.R;
import com.media.dbstarplayer.dbstarplayer.DbStarPlayerApp;
import com.media.reader.vo.TocReference;
import com.media.zlibrary.text.model.ZLTextModel;
import com.media.zlibrary.text.view.ZLTextView;

public class CatalogAdapter extends BaseAdapter{

	private List<TocReference> mData = null;
	
	private ListView mList = null;
	
	public CatalogAdapter(ListView listview, List<TocReference> bookToc){
		mData = bookToc;
		mList = listview;
	}
	
	@Override
	public int getCount() {
		return mData==null?0:mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView  == null){
		    convertView = LayoutInflater.from(mList.getContext()).inflate(R.layout.reader_toc_customer_tree_item, null);
		}
		final TextView tocTextView = (TextView)convertView.findViewById(R.id.toc_customer_tree_item_text);
		tocTextView.setText(mData.get(position).tocName);
		final TextView tocTextProgress = (TextView)convertView.findViewById(R.id.toc_customer_tree_item_progress);
		final ZLTextView view = (ZLTextView)DbStarPlayerApp.Instance().getCurrentView();
		final ZLTextModel textModel = view.getModel();
		tocTextProgress.setText(mData.get(position).mRef.ParagraphIndex*100/textModel.getParagraphsNumber()+"%");
		return convertView;
	}
	
}
