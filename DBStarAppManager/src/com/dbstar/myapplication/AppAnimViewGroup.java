package com.dbstar.myapplication;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

public class AppAnimViewGroup extends LinearLayout{
    
    private int l,t,r,b;
    public AppAnimViewGroup(Context context) {
        super(context);
    }
    
    public void setLayout( int l, int t, int r, int b ){
        this.l = l;
        this.t = t;
        this.r = r;
        this.b = b;
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        
        View v = getChildAt(0);
        if(v != null)
            v.layout(this.l, this.t, this.r, this.b);
    }
}
