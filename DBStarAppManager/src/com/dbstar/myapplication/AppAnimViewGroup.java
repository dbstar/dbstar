package com.dbstar.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AppAnimViewGroup extends LinearLayout{
    
    private int l,t,r,b;
    private LinearLayout mChild;
    private ImageView mIcon;
    private TextView mTitle;
    private RelativeLayout mLayout;
    
    public AppAnimViewGroup(Context context) {
        super(context);
    }
    
    public void setLayout( int l, int t, int r, int b ){
        this.l = l;
        this.t =  t;
        this.r =  r;
        this.b =  b;
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        View v = getChildAt(0);
        if(v != null)
            v.layout(this.l, this.t, this.r, this.b);
    }
    
    public View clone(View v){
        if(this.mLayout == null){
            this.mLayout = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.app_anim_view, null);
            this.mChild = (LinearLayout) mLayout.findViewById(R.id.child);
            this.mIcon = (ImageView) mLayout.findViewById(R.id.icon);
            this.mTitle = (TextView) mLayout.findViewById(R.id.title);
            this.mTitle.setEllipsize(TruncateAt.MARQUEE);
            this.mTitle.setMarqueeRepeatLimit(-1);
            this.mTitle.setFocusable(true);
        }
            
            this.mLayout.clearAnimation();
            this.mChild.setBackgroundDrawable(v.findViewById(R.id.child).getBackground());
            View icon = v.findViewById(R.id.icon);
            Drawable drawable = icon.getBackground();
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            this.mIcon.setImageBitmap(bitmap);
            this.mTitle.setText(((TextView)v.findViewById(R.id.title)).getText());
            this.mTitle.requestFocus();
            return this.mLayout;
            
            
    }
    public void addView() {
        this.addView(mLayout);
    }
    
    
    public void startAnim(){
        if(this.mLayout != null){
            ScaleAnimation animation = new ScaleAnimation(0.83f, 1.0f, 0.83f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setDuration(200);
            this.mLayout.setVisibility(View.VISIBLE);
            this.mLayout.startAnimation(animation);
        }
    }
    public void stopAnim(){
        if(this.mLayout != null){
            this.mLayout.clearAnimation();
            this.mLayout.setVisibility(View.INVISIBLE);
        }
    }
}
