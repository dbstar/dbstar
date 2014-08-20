package com.dbstar.multiple.media.common;

import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.dbstar.multiple.media.data.VoiceBookPageInfo;
import com.dbstar.multiple.media.model.ModelVoicedBook.Language;
import com.dbstar.multiple.media.shelf.activity.VoicedBookReadActivity;

public class VoicedBookService extends Service{
    
    private PlayServiceBinder mBinder = new PlayServiceBinder();
    
    private VoicedBookReadActivity mObserver;
    private MediaPlayer mPlayer;
    public List<VoiceBookPageInfo> mData;
    private Handler mHandler = new Handler();
    public String mBookId;
    private int mCurrentPageIndex;
    private boolean isPlaying;
    private boolean isReDelivery;
    private int mCurLanguage = Language.CHINESE.value;
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){
            mBookId = intent.getStringExtra(GDataConstant.VOICEDBOOK_ID);
        }
        
        if(mBookId != null){
            mData = GDBHelper.getInstance(this).getVoicedBookInfo(mBookId).mPages;
        }
        if(flags == START_FLAG_REDELIVERY){
            Bundle record = ShelfController.getInstance(this).getVoicedBookRecord();
            String bookId = record.getString(GDataConstant.VOICEDBOOK_ID);
            if(mBookId.equals(bookId)){
                mCurrentPageIndex = record.getInt(GDataConstant.VOICEDBOOK_PAGE_ORDER);
            }
            isReDelivery = true;
        }
        return START_FLAG_REDELIVERY;
    }
    
    public class PlayServiceBinder extends Binder{
        
        public VoicedBookService getService(){
            return VoicedBookService.this;
        }
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(completionListener);
        mPlayer.setOnPreparedListener(preparedListener); 
        
    }
    
    
    public void play(int index){
        if(index >= mData.size())
            return;
            
        String uri = null;
        if(mData != null){
            uri = mData.get(index).getSpecificAudios(mCurLanguage);
            mCurrentPageIndex = index;
        }
        if(uri == null || uri.isEmpty()){
            mHandler.postDelayed(new Runnable() {
                
                @Override
                public void run() {
                    completionListener.onCompletion(mPlayer);
                }
            }, 2000);
            return;
        }
        try {
            
            mPlayer.stop();
            mPlayer.reset();
            mPlayer.setDataSource(uri);
            mPlayer.prepare();
            
        } catch (Exception e) {
            e.printStackTrace();
            playNext();
        }
      
        
    }
    
    private void playNext(){
        mCurrentPageIndex ++;
        play(mCurrentPageIndex);
    }
    public void stop(){
        if(mPlayer != null && mPlayer.isPlaying()){
            mPlayer.stop();
            isPlaying = false;
        }
    }
    public boolean isPlaying(){
        if(mPlayer != null && mPlayer.isPlaying())
            return isPlaying;
        return false;
    }
    
    public void changeLanguage(){
        if(mCurLanguage == Language.CHINESE.value){
            mCurLanguage = Language.ENGLISH.value;
        }else{
            mCurLanguage = Language.CHINESE.value;
        }
        
        play(mCurrentPageIndex);
    }
    public void registerObserver(VoicedBookReadActivity oberver){
        mObserver = oberver;
    }
    public void unregisterObserver(){
        mObserver = null;
    }
    
    OnPreparedListener preparedListener = new OnPreparedListener() {
        
        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.start();
            isPlaying = true;
        }
    };
    
    OnCompletionListener completionListener = new OnCompletionListener() {
        
        @Override
        public void onCompletion(MediaPlayer mp) {
            if(mObserver != null){
                if(mCurrentPageIndex < mData.size()-1){
                    mObserver.playNextNotifycation();
                }else{
                    stop();
                }
                
            }
        }
    };
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        ShelfController.getInstance(this).saveVoicedBookRecord(mBookId, mCurrentPageIndex);
        if(mPlayer != null){
            stop();
            mPlayer.release();
        }
    }
}
