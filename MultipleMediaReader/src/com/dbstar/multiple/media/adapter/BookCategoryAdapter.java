package com.dbstar.multiple.media.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dbstar.multiple.media.data.BookCategory;
import com.dbstar.multiple.media.shelf.R;

public class BookCategoryAdapter extends BaseAdapter{
    
    public List<BookCategory> data;
    private LayoutInflater mInflater;
    private int layoutId;
    public BookCategoryAdapter(Context context,int layoutId) {
        mInflater = LayoutInflater.from(context);
        this.layoutId = layoutId;
    }
    @Override
    public int getCount() {
        if(data == null || data.size() == 0)
            return 0;
            return Integer.MAX_VALUE;
    }
    
    public void setData(List<BookCategory> data){
        this.data = data;
    }
    
    public List<BookCategory> getData(){
        return data;
    }
    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    
    public String getSelectedCategoryId(int position){
        if(data != null ){
            int index = (position % data.size());
            return data.get(index).Id;
        }
        return null;
    }
    public BookCategory getSelectedCategory(int position){
        if(data != null ){
            int index = (position % data.size());
            return data.get(index);
        }
        return null;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        ViewHoler viewHolder = null;  
        if(convertView == null){  
            viewHolder = new ViewHoler();  
            convertView = mInflater.inflate(layoutId, null);  
            viewHolder.tvText = (TextView) convertView.findViewById(R.id.category_name);  
            convertView.setTag(viewHolder);  
        }else{  
            viewHolder = (ViewHoler) convertView.getTag();  
        }  
        viewHolder.tvText.setText(data.get(position % data.size()).Name);//取余展示数据  
        return convertView;  
    }
    static class ViewHoler{  
        TextView tvText;  
    } 
}
