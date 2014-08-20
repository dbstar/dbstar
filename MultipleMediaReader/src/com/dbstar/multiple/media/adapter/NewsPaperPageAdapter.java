package com.dbstar.multiple.media.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dbstar.multiple.media.data.NewsPaperPage;
import com.dbstar.multiple.media.shelf.R;

public class NewsPaperPageAdapter extends BaseAdapter {

    public List<NewsPaperPage> data;
    private LayoutInflater mInflater;
    private int layoutId;
    private int markPosition = -1;
    private String color;
    public NewsPaperPageAdapter(Context context,String color, int layoutId) {
        mInflater = LayoutInflater.from(context);
        this.layoutId = layoutId;
        this.color = color;
    }

    @Override
    public int getCount() {
        if (data == null || data.size() == 0)
            return 0;

        return data.size();
    }
    
    public void setMarkPosition(int markPosition){
        this.markPosition = markPosition;
    }
    public void setData(List<NewsPaperPage> data) {
        this.data = data;
    }

    public List<NewsPaperPage> getData() {
        return data;
    }

    public NewsPaperPage getEdition(int index){
        if(data != null){
            if(data.size() > index){
                return data.get(index);
            }
        }
        
        return null;
    }
    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    // public String getSelectedCategoryId(int position){
    // if(data != null ){
    // int index = (position % data.size());
    // return data.get(index).Id;
    // }
    // return null;
    // }
    // public BookCategory getSelectedCategory(int position){
    // if(data != null ){
    // int index = (position % data.size());
    // return data.get(index);
    // }
    // return null;
    // }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHoler viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHoler();
            convertView = mInflater.inflate(layoutId, null);
            viewHolder.tvText = (TextView) convertView.findViewById(R.id.category_name);
            viewHolder.mark = (ImageView) convertView.findViewById(R.id.mark);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHoler) convertView.getTag();
        }
        viewHolder.tvText.setText(data.get(position).title);// 取余展示数据
        if( data.get(position).IsOpen == 1){
            viewHolder.tvText.setTextColor(Color.RED);
            viewHolder.tvText.setTag("#FFFF0000");
        }else {
            viewHolder.tvText.setTextColor(Color.parseColor(color));
            viewHolder.tvText.setTag(color);
        }
        if(markPosition == position)
            viewHolder.mark.setVisibility(View.VISIBLE);
        else
            viewHolder.mark.setVisibility(View.INVISIBLE);
            
        return convertView;
    }

    static class ViewHoler {
        TextView tvText;
        ImageView mark;
    }
}
