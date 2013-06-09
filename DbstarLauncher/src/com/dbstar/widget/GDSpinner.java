package com.dbstar.widget;

import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.dbstar.R;

public class GDSpinner extends Button{
    
    private PopupWindow mDropDownPopu;
    private int mWidth;
    private ListView mDropDownListView;
    private BaseAdapter mAdapter;
    private int mSelectionPosition = 0;
    private int mDropDownPopuWidth = 0;
    private int mDropDownPopuHeight = 0;
    private OnItemSelectedListener onItemSelectedListener;
    private int mPaddingLeft;
    private int mItemHeight;
    private int mItemCount = 5;
    private int mItemDefaultCount = 5;
    private int mItemPaddingLeft;
    int itemLyoautId = R.layout.gd_spinner_drop_list_item;
    private float mDensity;
    private OnKeyListener mOnKeyListener;
    public GDSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public GDSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDensity = context.getResources().getDisplayMetrics().density;  
        setOnKeyListener(onKeyListener);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.GDSpinner);
        itemLyoautId = a.getResourceId(R.styleable.GDSpinner_itemlayout, itemLyoautId);
        mItemCount = a.getInteger(R.styleable.GDSpinner_itemCount, mItemCount);
        
        
        View v = LayoutInflater.from(context).inflate(itemLyoautId, null);
        measureChild(v);
        mItemHeight = v.getMeasuredHeight();
        mItemPaddingLeft = v.getPaddingLeft();
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
        if(l != onKeyListener)
            this.mOnKeyListener = l;
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
        mWidth = getLayoutParams().width;
        mPaddingLeft = this.getPaddingLeft();
        mDropDownPopuWidth = mWidth - (mPaddingLeft - mItemPaddingLeft) * 2;
        if(mAdapter.getCount() < mItemCount){
            mItemCount = mAdapter.getCount();
        }else{
            if(mAdapter.getCount() < mItemDefaultCount)
               mItemCount = mAdapter.getCount();
            else
                mItemCount = mItemDefaultCount;
        }
        mDropDownPopuHeight = mItemCount * mItemHeight + dip2px(15);
        
        mDropDownListView = new ListView(getContext());
        LayoutParams listLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        mDropDownListView.setCacheColorHint(Color.TRANSPARENT);
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
            boolean result = false;
            if(mOnKeyListener != null)
                result = mOnKeyListener.onKey(v, keyCode, event);
            
            if(!result){
                int action = event.getAction();
                if (action == KeyEvent.ACTION_DOWN){
                    if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
                        GDSpinner.this.setInputType(InputType.TYPE_NULL);
                        showDropList();
                        return true;
                    }
                } 
                return false;
            }else{
                return result;
            }
        }
    };
    public void measureChild(View v) {
        ViewGroup.LayoutParams params = v.getLayoutParams();
        if (params == null)
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        int height = params.height;
        if (height > 0) {
            height = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        } else {
            height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        v.measure(MeasureSpec.makeMeasureSpec(params.width, MeasureSpec.UNSPECIFIED), height);
    }
    private int px2dip(float pxValue) {
        return (int) (pxValue / mDensity + 0.5f);
    }
    private int dip2px(float dpValue) {  
        return (int) (dpValue * mDensity + 0.5f);  
    }   
}
