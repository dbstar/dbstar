package com.dbstar.multiple.media.shelf.activity;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dbstar.multiple.media.common.GDBHelper;
import com.dbstar.multiple.media.common.GDataConstant;
import com.dbstar.multiple.media.common.ImageManager;
import com.dbstar.multiple.media.common.ImageManager.ImageCallback;
import com.dbstar.multiple.media.common.VoicedBookService;
import com.dbstar.multiple.media.common.VoicedBookService.PlayServiceBinder;
import com.dbstar.multiple.media.data.VoiceBookPageInfo;
import com.dbstar.multiple.media.data.VoicedBook;
import com.dbstar.multiple.media.model.ModelVoicedBook.Label;
import com.dbstar.multiple.media.shelf.R;
import com.dbstar.multiple.media.util.EPUBParser;
import com.dbstar.multiple.media.util.ImageUtil;
import com.dbstar.multiple.media.widget.VoicedBookMenuDialog;
import com.dbstar.multiple.media.widget.VoicedBookMenuDialog.OnBookMarkChangeListener;
import com.dbstar.multiple.media.widget.VoicedBookMenuDialog.OnBookMarkListItemClickListener;
import com.dbstar.multiple.media.widget.VoicedBookMenuDialog.OnLanguageChangeListener;

public class VoicedBookReadActivity extends Activity {
    private static final int MESSAGE_WAHT_ANIMATION_END = 0x1000;
    private static final int MESSAGE_WAHT_HIDE_VOLUME_VIEW = 0x2000;
    
    private final static String NULL_STRING = "";
    private VoicedBook mBook;
    private GDBHelper mGdbHelper;
    private ImageView mPage1, mPage2;
    private ImageView mBookMarkView;
    private FrameLayout mPageGroup;
    private ImageManager mImageManager;
    private ImageView mVolumeView;
    private ProgressBar mReadProgressBar;
    private TextView mReadProgressValue;

    private VoicedBookMenuDialog mMenuDialog;
    private VoiceBookPageInfo mCurrentPageInfo;
    private VoicedBookService mService;
    private AudioManager mAudioManager;
    
    private String RootPath;
    private String BookId;
    private int [] mVolumeBitmap;
    private int mPageNumber;
    private int mPageCount;
    
    private Bitmap mBitmap;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int what = msg.what;
            if(MESSAGE_WAHT_ANIMATION_END == what){
            if (mService != null) {
                if (isAutoPlay) {
                    if (mPageNumber < mPageCount)
                        mService.play(mPageNumber);
                    isAutoPlay = false;
                } else {
                    mService.stop();
                }
            }
            refreshReadProgressView();
            refreshBookMarkView();
            }else if(MESSAGE_WAHT_HIDE_VOLUME_VIEW == what){
                mVolumeView.setVisibility(View.GONE);
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_voiced_book_read);
        RootPath = getIntent().getStringExtra("FilePath");
        BookId = getIntent().getStringExtra("BookId");
        
        String appUri = getIntent().getStringExtra("app_uri");
        initView(appUri);
        mGdbHelper = GDBHelper.getInstance(this);
        mImageManager = ImageManager.getInstance(this);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        loadData();
        
        showUI();
    }

    private void initView(String appUri) {
        mPage1 = (ImageView) findViewById(R.id.view1);
        mPage2 = (ImageView) findViewById(R.id.view2);
        mPageGroup = (FrameLayout) findViewById(R.id.pageGroup);
        mBookMarkView = (ImageView) findViewById(R.id.bookmark);
        mVolumeView = (ImageView) findViewById(R.id.volume_view);
        mVolumeView.setVisibility(View.INVISIBLE);
        mReadProgressBar = (ProgressBar) findViewById(R.id.read_progress_bar);
        mReadProgressValue = (TextView) findViewById(R.id.txt_read_progress);
        
        mVolumeBitmap = new int[]{R.drawable.volume_1,
                R.drawable.volume_2,R.drawable.volume_3,R.drawable.volume_4,
                R.drawable.volume_5,R.drawable.volume_6,R.drawable.volume_7,
                R.drawable.volume_8,R.drawable.volume_9,R.drawable.volume_10};
        
        RelativeLayout mContainer = (RelativeLayout) findViewById(R.id.activity_voiced_book_read_container);
        mBitmap = ImageUtil.getBitmap(appUri);
        if (mBitmap == null) {
        	mContainer.setBackgroundResource(R.drawable.reader_view_background);
        } else {
        	Drawable drawable = new BitmapDrawable(mBitmap);
    		mContainer.setBackgroundDrawable(drawable);
        }
    }

    private void loadData() {
        mBook = mGdbHelper.getVoicedBookInfo(BookId);
        if (mBook.mPages == null || mBook.mPages.isEmpty()) {
            String ncxPath = EPUBParser.getNCXPath(RootPath);
            mBook.mPages = EPUBParser.parseVoiceBookPageInfo(RootPath, ncxPath);
            if (mBook.mPages != null) {
                VoiceBookPageInfo info;
                for (int i = 0, count = mBook.mPages.size(); i < count; i++) {
                    info = mBook.mPages.get(i);
                    info.PageIndex = i;
                    EPUBParser.parseVoicedBookContent(info);
                }
            }
            mGdbHelper.insertVoicedBookInfo(mBook);
        }
        initParams();
       

    }

    private void initParams(){
        if (getPageCount() > 0) {
            mPageCount = getPageCount();
            mPageNumber = 0;
            for(VoiceBookPageInfo info : mBook.mPages){
                if(info.Label.equals(Label.LAST_READ_MARKED.value) || info.Label.equals(Label.LAST_READ_UNMARKED.value)){
                    mPageNumber = info.PageIndex;
                    if(info.Label.equals(Label.LAST_READ_MARKED.value)){
                        info.Label = Label.MARKED.value;
                    }else {
                        info.Label = Label.UNMARKED.value;
                    }
                    mGdbHelper.updateLabel(info.Label, info.PageId, mBook.BookId);
                }
            }
            mCurrentPageInfo = mBook.mPages.get(mPageNumber);
            mReadProgressBar.setMax(mPageCount);
            
        } 
        
    }
    private void showUI() {
        if (mCurrentPageInfo == null)
            return;
        mPage2.bringToFront();
        mImageManager.getBitmapDrawable(mCurrentPageInfo.Image, new ImageCallback() {

            @Override
            public void imageLoaded(BitmapDrawable imageDrawable, String viewKey) {
                if (getBookPageInfo(mPageNumber).PageId.equals(viewKey)) {
                    mPage2.setImageBitmap(imageDrawable.getBitmap());
                    refreshBookMarkView();
                }
            }
        }, (mCurrentPageInfo.PageId));
        if (mPageNumber + 1 < mPageCount)
            mImageManager.getBitmapDrawable(getBookPageInfo(mPageNumber + 1).Image, null, NULL_STRING);

        refreshReadProgressView();
        bindService();
    }

    private void bindService() {

        Intent intent = new Intent(this, VoicedBookService.class);
        intent.putExtra(GDataConstant.VOICEDBOOK_ID, mBook.BookId);
        startService(intent);

        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private void unBindService() {
        unbindService(connection);
    }

    ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            VoicedBookService.PlayServiceBinder binder = (PlayServiceBinder) service;
            mService = binder.getService();
            mService.registerObserver(VoicedBookReadActivity.this);
            mService.play(mPageNumber);
        }
    };

    
    private VoiceBookPageInfo getBookPageInfo(int pageNumber) {
        if (pageNumber < mBook.mPages.size())
            return mBook.mPages.get(pageNumber);
        return null;
    }

    private int getPageCount() {
        if (mBook != null && mBook.mPages != null)
            return mBook.mPages.size();
        return 0;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
        case KeyEvent.KEYCODE_DPAD_RIGHT:
            showNextPage(true);
            break;
        case KeyEvent.KEYCODE_DPAD_LEFT:
            showPrePage();
            break;
        case KeyEvent.KEYCODE_DPAD_CENTER:
            showMenuDialog();
            break;
        case KeyEvent.KEYCODE_DPAD_UP:
        case KeyEvent.KEYCODE_DPAD_DOWN:
            adjustStreamVolume(keyCode);
            break;
        case KeyEvent.KEYCODE_ALT_LEFT:
            if (mService == null)
                bindService();
            else {
                if (mService.isPlaying()) {
                    mService.stop();
                    isAutoPlay = false;
                } else {
                    mService.play(mPageNumber);
                }
            }
            break;
        default:
            break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private long duration = 500;
    private long startTime, endTime;
    private boolean isAnimationEnd = true;;
    private boolean isAutoPlay = false;

    public void playNextNotifycation() {
        isAutoPlay = true;
        showNextPage(true);
    }

    private void showNextPage(boolean isShowAnimation) {
        // AlphaAnimation animation = new AlphaAnimation(1.0f, 0f);
        if (!isAnimationEnd) {
            return;
        }
        startTime = System.currentTimeMillis();
        if (mPageNumber >= mPageCount - 1) {
            return;
        }
        long interval = startTime - endTime;
        if (interval < 500) {
            if (interval < 10) {
                return;
            } else
                duration = (startTime - endTime) * 3 / 4;
        } else {
            duration = 500;
        }

        isAnimationEnd = false;
        mPageNumber++;
        // Log.e("VoicedBookReadActivity", "duration = " + duration);

        if (!isShowAnimation)
            duration = 10;
        mCurrentPageInfo = mBook.mPages.get(mPageNumber);
        final ImageView fontView = getFrontView();
        TranslateAnimation animation = new TranslateAnimation(0, -1140, 0, 0);
        animation.setDuration(duration);

        animation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                getBackView().setImageBitmap(mImageManager.getBitmapDrawable(mCurrentPageInfo.Image).getBitmap());
                mBookMarkView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fontView.clearAnimation();
                ImageView backView = getBackView();
                backView.bringToFront();
                if (mPageNumber + 1 > mPageCount - 1) {
                    endTime = System.currentTimeMillis();
                    isAnimationEnd = true;
                    mHandler.sendEmptyMessage(MESSAGE_WAHT_ANIMATION_END);
                } else {
                    mImageManager.getBitmapDrawable(mBook.mPages.get(mPageNumber + 1).Image, new ImageCallback() {

                        @Override
                        public void imageLoaded(BitmapDrawable imageDrawable, String viewKey) {
                            endTime = System.currentTimeMillis();
                            isAnimationEnd = true;
                            mHandler.sendEmptyMessage(MESSAGE_WAHT_ANIMATION_END);
                        }
                    }, NULL_STRING);
                }
            }
        });
        fontView.startAnimation(animation);
    }

    private void showPrePage() {
        if (!isAnimationEnd)
            return;
        startTime = System.currentTimeMillis();

        if (mPageNumber <= 0)
            return;
        long interval = startTime - endTime;
        if (interval < 500) {
            if (interval < 10)
                return;
            else
                duration = (startTime - endTime) * 3 / 4;
        } else {
            duration = 500;
        }

        isAnimationEnd = false;
        mPageNumber--;
        mCurrentPageInfo = mBook.mPages.get(mPageNumber);
        final ImageView fontView = getFrontView();
        TranslateAnimation animation = new TranslateAnimation(0, 1140, 0, 0);
        animation.setDuration(500);

        animation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                getBackView().setImageBitmap(mImageManager.getBitmapDrawable(mCurrentPageInfo.Image).getBitmap());
                mBookMarkView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fontView.clearAnimation();
                ImageView backView = getBackView();
                backView.bringToFront();
                endTime = System.currentTimeMillis();
                isAnimationEnd = true;
                mHandler.sendEmptyMessage(MESSAGE_WAHT_ANIMATION_END);
            }
        });
        fontView.startAnimation(animation);
    }

    /**
     * the group have 2 child view,and get the child view that the position at 1
     * 
     * @return
     */
    private ImageView getFrontView() {
        return (ImageView) mPageGroup.getChildAt(1);
    }

    /**
     * the group have 2 child view,and get the child view that the position at 0
     * 
     * @return
     */
    private ImageView getBackView() {
        return (ImageView) mPageGroup.getChildAt(0);
    }

    /**
     * show menu view then user can add or remove bookmark, find catalogue ,find
     * bookmark,and change playback language
     */

    private void showMenuDialog() {

        if (mPageCount <= 0)
            return;
        if (mMenuDialog == null) {
            mMenuDialog = VoicedBookMenuDialog.create(this, mBook);
            mMenuDialog.setOnBookMarkChangeListener(new OnBookMarkChangeListener() {

                @Override
                public void onChange() {
                    if (updateLabel() != -1) {
                        refreshBookMarkView();
                    }

                    mMenuDialog.dismiss();
                }
            });
            mMenuDialog.setOnLanguageChangeListener(new OnLanguageChangeListener() {
                
                @Override
                public void onChange() {
                    if(mService != null)
                        mService.changeLanguage();
                }
            });
            mMenuDialog.setOnBookMarkListItemClickListener(new OnBookMarkListItemClickListener() {

                @Override
                public void itemClick(int pageIndex) {
                    if (pageIndex != mPageNumber) {
                        mPageNumber = pageIndex - 1;
                        showNextPage(false);
                    }
                    mMenuDialog.dismiss();

                }
            });
        }

        mMenuDialog.show(mCurrentPageInfo);
    }

    /**
     * show bookmark view if the value of label is 1 or hide,
     */
    private void refreshBookMarkView() {
        if (mCurrentPageInfo.Label.equals(Label.MARKED.value)) {
            mBookMarkView.setVisibility(View.VISIBLE);
        } else {
            mBookMarkView.setVisibility(View.INVISIBLE);
        }
    }

    private void refreshReadProgressView(){
       mReadProgressBar.setProgress(mPageNumber+1);
       NumberFormat format = NumberFormat.getPercentInstance();
       format.setMaximumFractionDigits(0);//设置两位小数位
       float  progress = (float)(mPageNumber + 1)/(float)(mPageCount);//男比例
       mReadProgressValue.setText(format.format(progress));
       
    }
    /**
     * update database for bookmark
     * 
     * @return
     */
    private int updateLabel() {
        return mGdbHelper.updateLabel(mCurrentPageInfo.Label, mCurrentPageInfo.PageId, mBook.BookId);
    }
    
    
    /**
     * Sorts the specified list in ascending numerical order.
     */
    private void sortList(List<VoiceBookPageInfo> list) {
        Collections.sort(list, new Comparator<VoiceBookPageInfo>() {
            @Override
            public int compare(VoiceBookPageInfo lhs, VoiceBookPageInfo rhs) {
                return lhs.Order.compareTo(rhs.Order);
            }
        });
    }

   
   private void adjustStreamVolume(int keyCode){
       mHandler.removeMessages(MESSAGE_WAHT_HIDE_VOLUME_VIEW);
       mVolumeView.setVisibility(View.VISIBLE);
       if(keyCode == KeyEvent.KEYCODE_DPAD_UP){
           mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
       }else if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
           mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND); 
       }
       int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
       if(keyCode == KeyEvent.KEYCODE_DPAD_UP && volume == 0){
           Intent intent = new Intent("dbstar.intent.action.MUTE");
           intent.putExtra("key_mute", false);
           sendBroadcast(intent);
       }
       int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
       
       int index = mVolumeBitmap.length * volume / max -1;
       if(index < 0)
           index = 0;
       if(index == 0){
           mVolumeView.setImageBitmap(null);
       }else{
           mVolumeView.setImageResource(mVolumeBitmap[index]);
       }
       
       mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_WAHT_HIDE_VOLUME_VIEW), 5*1000);
   }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mCurrentPageInfo.Label.equals(Label.MARKED.value)){
            mCurrentPageInfo.Label = Label.LAST_READ_MARKED.value;
        }else if(mCurrentPageInfo.Label.equals(Label.UNMARKED.value)) {
            mCurrentPageInfo.Label = Label.LAST_READ_UNMARKED.value;
        }
        mGdbHelper.updateLabel(mCurrentPageInfo.Label, mCurrentPageInfo.PageId, mBook.BookId);
        
        if(mService != null)
            unBindService();
        stopService(new Intent(this, VoicedBookService.class));
        
        if (mBitmap != null && !mBitmap.isRecycled()) {
     	   mBitmap.recycle();
        }
    }
}
