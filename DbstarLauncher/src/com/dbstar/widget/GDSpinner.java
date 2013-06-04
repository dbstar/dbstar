package com.dbstar.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dbstar.R;


import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.widget.AbsListView.LayoutParams;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;

public class GDSpinner extends Button{
    
    private PopupWindow mDropDownPopu;
    private int mWidth;
    private ListView mDropDownListView;
    private BaseAdapter mAdapter;
    private int mSelectionPosition = 0;
    private int mDropDownPopuWidth = 158;
    private int mDropDownPopuHeight = 175;
    private OnItemSelectedListener onItemSelectedListener;
    private boolean mEnable = true;;
    
    
    public GDSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public GDSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnKeyListener(onKeyListener);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.GDSpinner);
        mDropDownPopuWidth = (int) a.getDimension(R.styleable.GDSpinner_dropDownListWidth, mDropDownPopuWidth);
        mDropDownPopuHeight = (int) a.getDimension(R.styleable.GDSpinner_dropDownListHeight, mDropDownPopuHeight);
    }

    public GDSpinner(Context context) {
        super(context);
    }
    
    public void setArrayData(List<String> arrayData){
        
    }
    
    public int  getSelectedItemPosition(){
       return mSelectionPosition;
    }
    @Override
    public void setOnClickListener(OnClickListener l) {
    }
    
    public void setAdapter(BaseAdapter adapter){
        this.mAdapter = adapter;
        this.mAdapter.registerDataSetObserver(new DataSetObserver() {
            
            @Override
            public void onChanged() {
                super.onChanged();
                if(mAdapter != null){
                    if(mAdapter.getCount() == 0){
                        setFocusableInTouchMode(false);
                        setFocusable(false);
                        setEnabled(false);
                        
                    }else{
                        setFocusableInTouchMode(true);
                        setFocusable(true);
                        mSelectionPosition = 0;
                        setEnabled(true);
                        
                        if(mDropDownPopu != null && mDropDownPopu.isShowing()){
                            mDropDownPopu.dismiss();
                            mDropDownPopu = null;
                            mDropDownListView = null;
                            createListView();
                            showDropList();
                            
                        }
                    }
                }else{
                    setFocusableInTouchMode(false);
                    setFocusable(false);
                    setEnabled(false);
                }
                
            }
        });
        mWidth = getLayoutParams().width;
        createListView();
        GDSpinner.this.setText(mAdapter.getItem(mSelectionPosition).toString());
        mDropDownListView.setSelection(mSelectionPosition);
    }
    
    public void setSelection(int selection){
        mSelectionPosition = selection;
        if(mAdapter != null){
            GDSpinner.this.setText(mAdapter.getItem(mSelectionPosition).toString());
        }
        if(mDropDownListView != null){
            if(onItemSelectedListener != null){
               onItemSelectedListener.onItemSelected(mDropDownListView, null, mSelectionPosition, 0);
            }
        }
            
    }
    public void clearData(){
        this.setText("");
    }
    
    @Override
    public void setOnKeyListener(OnKeyListener l) {
        super.setOnKeyListener(onKeyListener);
    }
    public void setOnItemSelectedListener(OnItemSelectedListener l){
        this.onItemSelectedListener = l;
    }
    private void showDropList(){
        if(mAdapter != null){
            if(mAdapter.getCount() == 0){
                return;
            }
        }else{
            return;
        }
        
        if(mDropDownListView == null)
            createListView();
        
        if(mDropDownPopu == null)
            mDropDownPopu = new PopupWindow(mDropDownListView, mDropDownPopuWidth, mDropDownPopuHeight);

        mDropDownPopu.setFocusable(true);
        mDropDownPopu.setBackgroundDrawable(new
        ColorDrawable(Color.parseColor("#00000000")));
        mDropDownPopu.showAsDropDown(this, (mWidth - mDropDownPopuWidth)/2, 0);
        
    }
    private void createListView() {
        mDropDownListView = new ListView(getContext());
        LayoutParams listLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        mDropDownListView.setCacheColorHint(Color.TRANSPARENT);
        mDropDownListView.setSelector(new
                ColorDrawable(Color.parseColor("#a0000000")));
        mDropDownListView.setBackgroundResource(R.drawable.gd_spinner_drop_bg_pic);
        mDropDownListView.setAdapter(mAdapter);
//        mDropDownListView.setOnItemSelectedListener(new OnItemSelectedListener() {
//
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view,
//                    int position, long id) {
//                
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                if(onItemSelectedListener != null)
//                    onItemSelectedListener.onNothingSelected(parent);
//            }
//        });
        
        mDropDownListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                GDSpinner.this.setText(mAdapter.getItem(position).toString());
                mDropDownPopu.dismiss();
                mSelectionPosition = position;
                if(onItemSelectedListener != null)
                    onItemSelectedListener.onItemSelected(parent, view, position, id);
                
            }
        });
        
        mDropDownListView.setSelection(mSelectionPosition);
    }
    
    OnKeyListener onKeyListener = new OnKeyListener() {
        
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            int action = event.getAction();
            if (action == KeyEvent.ACTION_DOWN){
                if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
                    GDSpinner.this.setInputType(InputType.TYPE_NULL);
                    showDropList();
                    return true;
                }
            } 
            return false;
        }
    };
    
}
