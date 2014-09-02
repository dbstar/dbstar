package com.dbstar.multiple.media.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import com.dbstar.multiple.media.data.Book;

public class BookShelfGroup extends RelativeLayout {
    private final static String TAG = "BookShelfGroup";

    private boolean mIsInited;
    private boolean mIsAnimationStop = true;
    private BookView mLastView;
    private BookView mFirstView;
    private List<BookView> mCacheView;
    private int maxBookCount = 5;
    
    private Handler mHandler;
    public BookShelfGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public BookShelfGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BookShelfGroup(Context context) {
        super(context);
    }
    
    public void setHandler(Handler handler){
        this.mHandler = handler;
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (!mIsInited)
            super.onLayout(changed, l, t, r, b);
        else {
            BookView v;
            for (int i = 0, count = getChildCount(); i < count; i++) {
                v = (BookView) getChildAt(i);
                v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                Log.i(TAG,
                        "onLayout left = " + v.getLeft() + " top = " + v.getTop() + " right = " + v.getRight() + " bottom = " + v.getBottom() + " pt = " + v.getPaddingTop() + " width = "
                                + v.getMeasuredWidth());
            }
        }

    }


    int bookCount = 0;
    public void initChildView(Book [] books) {
        if(mIsInited){
            if(books == null || books.length == 0){
                removeViews(0, getChildCount());
                mIsAnimationStop = true;
            }
            else{
                Next5Book(books);
            }
            return;
        }
            
        mCacheView = new ArrayList<BookView>();
        if(books == null)
             bookCount = 0;
        else
            bookCount = books.length;
        BookView v;

        for (int i = 0, count = getChildCount(); i < count; i++) {
            mCacheView.add((BookView) getChildAt(i));
        }

        removeViews(0, getChildCount() - bookCount);
        mIsInited = true;
        for (int i = 0, count = getChildCount(); i < count; i++) {
            v = (BookView) getChildAt(i);
            v.onInit(books[i]);
        }
        mHandler.sendEmptyMessage(0);
    }

    public void reSetView() {
        BookView v;
        for (int i = 0, count = getChildCount(); i < count; i++) {
            v = (BookView) getChildAt(i);
            v.reset();
        }
        if(getChildCount() == mCacheView.size()){
            mCacheView.clear();
            for(int i = 0, count = getChildCount(); i < count; i++){
                mCacheView.add((BookView) getChildAt(i));
            }
        }else{
            for(int i = 0, count = getChildCount(); i < count; i++){
                mCacheView.remove(getChildAt(i));
            }
            for(int i = 0, count = getChildCount(); i < count; i++){
                mCacheView.add((BookView) getChildAt(i));
            }
        }
        mIsAnimationStop = true;
    }

    public void Pre5Book(final Book[] data) {
        bookCount = data.length;
        BookView v;
        mFirstView = (BookView) getChildAt(0);
        for (int i = 0, count = getChildCount(); i < count; i++) {
            v = (BookView) getChildAt(i);
            ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f);
            scaleAnimation.setDuration(500);
            v.startAnimation(scaleAnimation);
            if (i == count - 1) {
                scaleAnimation.setAnimationListener(new AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if(getChildCount() < bookCount){
                            int count = bookCount - getChildCount();
                            int startIndex = maxBookCount - getChildCount() -1;
                            int endIndex = startIndex - count;
                            for(int i = startIndex;i > endIndex ;i --){
                                addView(mCacheView.get(i), 0);
                            }
                        }else if(getChildCount() > bookCount){
                            removeViews(0, getChildCount() - bookCount);
                        }
                        BookView v;
                        for (int i = 0, count = getChildCount(); i < count; i++) {
                            v = (BookView) getChildAt(i);
                            v.clearAnimation();
                            v.clear();
                            ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1.0f, 0f, 1.0f, Animation.RELATIVE_TO_SELF, 1F, Animation.RELATIVE_TO_SELF, 0f);
                            scaleAnimation.setDuration(500);
                            v.startAnimation(scaleAnimation);
                            if (i == count - 1) {
                                scaleAnimation.setAnimationListener(new AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                    }
                                    @Override
                                    public void onAnimationRepeat(Animation animation) {}
                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        BookView v;
                                        for (int i = 0, count = getChildCount(); i < count; i++) {
                                            v = (BookView) getChildAt(i);
                                            v.clearAnimation();
                                            v.onInit(data[i]);
                                        }
                                        
                                        mIsAnimationStop = true;
                                        mHandler.sendEmptyMessage(0);
                                    }
                                });
                            }

                        }
                    }
                });
            }
        }
    }
    
    public void Next5Book(final Book [] data) {
        bookCount = data.length;
        if(getChildCount() == 0){
            if(getChildCount() < bookCount){
                int count = bookCount - getChildCount();
                int startIndex = maxBookCount - getChildCount() -1;
                int endIndex = startIndex - count;
                for(int i = startIndex;i > endIndex ;i --){
                    addView(mCacheView.get(i), 0);
                }
            }else if(getChildCount() > bookCount){
                removeViews(0, getChildCount() - bookCount);
            }
            BookView v;
            for (int i = 0, count = getChildCount(); i < count; i++) {
                v = (BookView) getChildAt(i);
                v.clearAnimation();
                ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1.0f, 0f, 1.0f, Animation.RELATIVE_TO_SELF, 0F, Animation.RELATIVE_TO_SELF, 1f);
                scaleAnimation.setDuration(500);
                v.startAnimation(scaleAnimation);
                if (i == count - 1) {
                    scaleAnimation.setAnimationListener(new AnimationListener() {

                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            BookView v;
                            for (int i = 0, count = getChildCount(); i < count; i++) {
                                v = (BookView) getChildAt(i);
                                v.clearAnimation();
                                v.onInit(data[i]);
                            }
                            mIsAnimationStop = true;
                            mHandler.sendEmptyMessage(0);
                        }
                    });
                }
            }
            return;
        }
        BookView v;
        mLastView = (BookView) getChildAt(getChildCount() -1);
        mIsAnimationStop = false;
        for (int i = 0, count = getChildCount(); i < count; i++) {
            v = (BookView) getChildAt(i);
            ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0f);
            scaleAnimation.setDuration(500);
            v.startAnimation(scaleAnimation);
            if (i == count - 1) {
                scaleAnimation.setAnimationListener(new AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if(getChildCount() < bookCount){
                            int count = bookCount - getChildCount();
                            int startIndex = maxBookCount - getChildCount() -1;
                            int endIndex = startIndex - count;
                            for(int i = startIndex;i > endIndex ;i --){
                                addView(mCacheView.get(i), 0);
                            }
                        }else if(getChildCount() > bookCount){
                            removeViews(0, getChildCount() - bookCount);
                        }
                        BookView v;
                        for (int i = 0, count = getChildCount(); i < count; i++) {
                            v = (BookView) getChildAt(i);
                            v.clearAnimation();
                            v.clear();
                            ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1.0f, 0f, 1.0f, Animation.RELATIVE_TO_SELF, 0F, Animation.RELATIVE_TO_SELF, 1f);
                            scaleAnimation.setDuration(500);
                            v.startAnimation(scaleAnimation);
                            if (i == count - 1) {
                                scaleAnimation.setAnimationListener(new AnimationListener() {

                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        BookView v;
                                        for (int i = 0, count = getChildCount(); i < count; i++) {
                                            v = (BookView) getChildAt(i);
                                            v.clearAnimation();
                                            v.onInit(data[i]);
                                        }
                                        mIsAnimationStop = true;
                                        mHandler.sendEmptyMessage(0);
                                    }
                                });
                            }
                        }
                    }
                });
            }

        }
    }
    
    public void scrollToNextBook() {
        if (!mIsAnimationStop)
            return;
        if(bookCount == 0)
            return;
        if (getChildCount() == 1)
            return;
        count = getChildCount() - 1;
        AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation trans = new TranslateAnimation(0, 1000, 0, 0);
        animationSet.setInterpolator(new DecelerateInterpolator());
        animationSet.setDuration(500);
        trans.setFillAfter(true);
        trans.setAnimationListener(mAlphaRemoveListener);

        animationSet.addAnimation(trans);

        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0f, 1.0f, 0f, Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0f);
        animationSet.addAnimation(scaleAnimation);

        getChiView(count).startAnimation(animationSet);
        mIsAnimationStop = false;
    }

    public void scrollToPreBook() {
        if (!mIsAnimationStop)
            return;
        
        if(bookCount == 0)
            return;
        if (getChildCount() == 1)
            return;
        count = 0;
        BookView child = getChiView(count);
        
        removeViewAt(count);
        int left = getChildAt(getChildCount() - 1).getLeft() + 500;
        int right = getChildAt(getChildCount() - 1).getRight() + 500;
        int top = getChildAt(getChildCount() - 1).getTop();
        int bottom = getChildAt(getChildCount() - 1).getBottom();
        swapParam(child, (BookView)getChildAt(getChildCount() - 1));
        child.layout(left, top, right, bottom);
        addView(child);
        child.setVisibility(View.INVISIBLE);
        startPreAnimoation(count, child);
        mIsAnimationStop = false;
    }
    int count1 = 1;
    private void startPreAnimoation(final int preViewIndex, BookView mv) {
        final BookView currentView = getChiView(preViewIndex);
        final BookView preView = mv;
        
        if(preViewIndex == getChildCount() - 1){
            final AnimationSet animationSet = new AnimationSet(true);
            TranslateAnimation animation2 = new TranslateAnimation(0, preView.mLeft - currentView.getLeft(), 0, 0);
            ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1.0f, 0f, 1.0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
            animationSet.addAnimation(animation2);
            animationSet.addAnimation(scaleAnimation);
            animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
            scaleAnimation.setDuration(500);
            currentView.startAnimation(animationSet);
            animationSet.setAnimationListener(new AnimationListener() {
             
                @Override
                public void onAnimationStart(Animation animation) {
                    currentView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    currentView.post(new Runnable() {
                        
                        @Override
                        public void run() {
                            currentView.clearAnimation();
                            swapParam(currentView,preView);
                            reSetView();
                            mHandler.sendEmptyMessage(0);
                        }
                    });
                       
                }
            });
            return;
        }
        
        final AnimationSet animationSet = new AnimationSet(false);
        TranslateAnimation trans = new TranslateAnimation(0, preView.mLeft - currentView.mLeft, 0, 0);
        
        float x = (float) preView.width / (float) currentView.width;
        float y = (float) preView.height / (float) currentView.height;
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, x, 1.0f, y, Animation.RELATIVE_TO_SELF, -0.2f, Animation.RELATIVE_TO_SELF, 0.35f);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(trans);
        animationSet.setDuration(100);
        animationSet.setAnimationListener(new AnimationListener() {
          
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
              currentView.post(new Runnable() {
                
                @Override
                public void run() {
                    currentView.clearAnimation();
                    swapParam(currentView,preView);
                    startPreAnimoation(++count, currentView);
                }
            });
              
                
            }
        });
        currentView.startAnimation(animationSet);

    }
    
    private void swapParam(BookView cur,BookView pre){
        cur.getLayoutParams().width = pre.width;
        cur.getLayoutParams().height = pre.height;
        cur.layout(pre.mLeft, pre.mTop, pre.mRight,pre.mBottom);  
        
    }
    private BookView getChiView(int index) {
        return (BookView) this.getChildAt(index);
    }

    private int count = 0;
    private BookView mRemoveView = null;
    AnimationListener mAlphaRemoveListener = new AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mRemoveView = getChiView(count);
            mRemoveView.clearAnimation();
            swapParam(mRemoveView, (BookView)getChildAt(0));
            removeViewAt(count);
            addView(mRemoveView, 0);
            mRemoveView.setVisibility(View.INVISIBLE);
            startNextAnimoation(count, mRemoveView);
            
        }
    };

    private void startNextAnimoation(final int nextViewIndex, BookView mv) {
        Log.i(TAG, "startNextAnimoation");
        final BookView currentView = getChiView(nextViewIndex);
        final BookView preView = mv;
        
        if(nextViewIndex == 0){
            if(currentView.mIsWillRemove){
                reSetView();
                currentView.mIsWillRemove = false;
                removeViewAt(nextViewIndex);
                currentView.setVisibility(View.VISIBLE);
                mHandler.sendEmptyMessage(0);
                return;
            }
            TranslateAnimation trans = new TranslateAnimation(100, 0, 0, 0);
            trans.setFillAfter(true);
            trans.setDuration(50);
            currentView.startAnimation(trans);
            trans.setAnimationListener(new AnimationListener() {
                
                @Override
                public void onAnimationStart(Animation animation) {
                    
                }
                
                @Override
                public void onAnimationRepeat(Animation animation) {
                    
                }
                
                @Override
                public void onAnimationEnd(Animation animation) {
                    currentView.clearAnimation();
                    reSetView();
                    currentView.setVisibility(View.VISIBLE);
                    mHandler.sendEmptyMessage(0);
                }
            });
            return;
        }
        final AnimationSet animationSet = new AnimationSet(false);
        TranslateAnimation trans = new TranslateAnimation(0, preView.mLeft - currentView.mLeft, 0, preView.mTop - currentView.getTop());
        animationSet.addAnimation(trans);
        float x = (float) preView.width / (float) currentView.width;
        float y = (float) preView.height / (float) currentView.height;
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, x, 1.0f, y, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
        animationSet.setFillAfter(true);
        animationSet.addAnimation(scaleAnimation);
        animationSet.setDuration(100);
        trans.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                currentView.post(new Runnable() {
                    
                    @Override
                    public void run() {
                        currentView.clearAnimation();
                        swapParam(currentView,preView);
                        startNextAnimoation(-- count , currentView);
                    }
                });
                
            }

        });
        currentView.startAnimation(animationSet);
    }
    
    public void startDeleteAnimation(Book [] page){
        if(page  == null || page.length == 0){
            removeAllViews();
            return ;
        }
        
        BookView v = (BookView) getChildAt(getChildCount() -1);
        if(page.length <getChildCount()){
            v.mIsWillRemove = true;
            scrollToNextBook();
        }else{
            v.onInit(page [0]);
           scrollToNextBook();
        }
    }
    public Book getBookInfoByIndex(int index){
        if(index < 0)
            return null;
        Book book = null;
        if(getChildCount()> index){
            BookView v = (BookView) getChildAt(index);
            book = v.getBookInfo();
        }
        return book;
    }
    
    public void setBookInfoByIndex(Book book ,int index){
        if(index < 0)
            return ;
        if(getChildCount()> index){
            BookView v = (BookView) getChildAt(index);
            v.onInit(book);
        }
    }
    public boolean isAnimationStop(){
        return mIsAnimationStop;
    }
    
    public void setAnimationStatu(boolean statu){
        mIsAnimationStop = statu;
    }
    
    
    public void requestChidBookNameFocus(){
//        BookView v ;
//        for(int i = 0 ,count = getChildCount();i < count;i++){
//            v = (BookView) getChildAt(i);
//            if(i == count -1){
//                v.requestChidBookNameFocus();
//            }else{
//                v.requestChidBookNameNotFocus();
//            }
//        }
//        requestLayout();
//        invalidate();
    }
}
