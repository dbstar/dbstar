package com.dbstar.multiple.media.fragment;

import java.util.List;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dbstar.multiple.media.common.ImageManager;
import com.dbstar.multiple.media.common.ImageManager.ImageCallback;
import com.dbstar.multiple.media.common.ShelfController;
import com.dbstar.multiple.media.data.NewsPaperArticleContent;
import com.dbstar.multiple.media.data.NewsPaperArticleContent.Block;
import com.dbstar.multiple.media.data.NewsPaperArticleContent.Patch;
import com.dbstar.multiple.media.data.NewsPaperPage;
import com.dbstar.multiple.media.shelf.R;
import com.dbstar.multiple.media.util.EPUBParser;
import com.dbstar.multiple.media.util.ToastUitl;
import com.dbstar.multiple.media.widget.NewsPaperContentView;
import com.iflytek.tts.TtsService.Tts;

public class NewsPaperArticleContentFragment extends BaseFragment{
    
    private static int MESSAGE_PLAY_REFRESH_VIEW = 0x100;
    private static int MESSAGE_PLAY_NEXT_ARTICAL = 0x200;
    private static int MESSAGE_PLAY_STOP = 0x300;
    
    private NewsPaperPage mPage;
    private NewsPaperContentView mContentView;
    private TextView mTitleView;
    private ImageView mVoiceIcon;
    private ImageManager mImageManager;
    private boolean isNeedLoad;
    private RelativeLayout mArticlePicLoadingView;
    private RelativeLayout mParseContentLoadingView;
    private ImageView mArticleImageView;
    private ScrollView mScrollView;
    private TextView mFooterMainCategoryName;
    private TextView mFooterSubCategoryName;
    private TextView mFooterPageName;
    private NewsPaperArticleContent mData;
    private ShelfController mController;
    private Handler mHandler = new Handler(){
        
        public void handleMessage(android.os.Message msg) {
            
            int what = msg.what;
            
            if(what == MESSAGE_PLAY_REFRESH_VIEW){
                Patch patch = (Patch) msg.obj;
                mContentView.setPalyIndex(patch.startIndex, patch.endIndex);
            }else if(what == MESSAGE_PLAY_NEXT_ARTICAL){
                mActivity.showNextArticle();
            } else if (what == MESSAGE_PLAY_STOP) {
            	stopPlay();
            	hasStoped = true;
            }
            
        }
        
    };
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mController = ShelfController.getInstance(mActivity);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageManager = ImageManager.getInstance(getActivity());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         View v = inflater.inflate(R.layout.newspaper_article_content_fragment_view, null);
         initView(v);
         return v;
        
    }
    
    BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				Log.d("NewsPaperArticleContentFragment", " ==pressed home key!== ");
				if ((Tts.JniIsPlaying() == 0 || Tts.JniIsPlaying() == 1) && isPlay) {
					stopPlay();
					hasStoped = true;
				}
			}
			
		}
	};
    
    private void initView(View v){
        mScrollView = (ScrollView) v.findViewById(R.id.scroll_content_view);
        mContentView = (NewsPaperContentView) v.findViewById(R.id.contentView);
        mTitleView = (TextView) v.findViewById(R.id.article_title);
        mTitleView.setFocusable(false);
        mVoiceIcon = (ImageView) v.findViewById(R.id.voice_icon);
        mVoiceIcon.setVisibility(View.INVISIBLE);
        mArticlePicLoadingView = (RelativeLayout)v.findViewById(R.id.article_pic_loading_bar);
        mParseContentLoadingView = (RelativeLayout) v.findViewById(R.id.parse_content_bar);
        mArticleImageView = (ImageView) v.findViewById(R.id.article_pic);
        mFooterMainCategoryName = (TextView) v.findViewById(R.id.footer_main_category);
        mFooterSubCategoryName = (TextView) v.findViewById(R.id.footer_sub_category);
        mFooterPageName = (TextView) v.findViewById(R.id.footer_page);
        
        AudioManager audioManager = (AudioManager) getActivity().getSystemService(Service.AUDIO_SERVICE);
        audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
        
        mScrollView.setOnKeyListener(new OnKeyListener() {
            long lastOnKeyTime ;
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                
                if(event.getAction() == KeyEvent.ACTION_DOWN){
                    
                    if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
                        //mActivity.showNextArticle();
                        return true;
                    }else if(keyCode == KeyEvent.KEYCODE_ALT_LEFT){
						long currentTime = System.currentTimeMillis();
						if (currentTime - lastOnKeyTime > 2000) {
							Log.d("onkey in rm ", "------------------- isTtsInited = " + isTtsInited);
							if(!isTtsInited) {
								Log.d("onkey in rm ", " initTtsEngine(), Tts.JniIsPlaying() = " + Tts.JniIsPlaying());
								if (Tts.JniIsPlaying() != 0) {
									initTtsEngine();									
								} else 
									isTtsInited = true;
							}
							
							if ((Tts.JniIsPlaying() == 0 || Tts.JniIsPlaying() == 1) && isPlay) {
//								isPlay = false;
								stopPlay();
								hasStoped = true;
								Log.d("onkey in rm ", " stopPlay(), KEYCODE_ALT_LEFT, Tts.JniIsPlaying() = " + Tts.JniIsPlaying());
							} else if (Tts.JniIsPlaying() != 1 && hasStoped) {
//								isPlay = true;
								hasStoped = false;
								Log.d("onkey in rm ", " startPlay(), KEYCODE_ALT_LEFT, Tts.JniIsPlaying() = " + Tts.JniIsPlaying());
								startPlay();
							} else {
								Log.d("onkey in rm ", "ignore no status KEYCODE_ALT_LEFT, Tts.JniIsPlaying() = " + Tts.JniIsPlaying() + ", isPlay = " + isPlay + ", hasStoped = " + hasStoped);
							}
							lastOnKeyTime = currentTime;
						} else {
							Log.d("onkey in rm ", "ignore too frequency KEYCODE_ALT_LEFT");
						}
                       
                        return true;
                    }else if(keyCode == KeyEvent.KEYCODE_NOTIFICATION){
                        showTextFontSeeting();
                        return true;
                    }
                }
                return false;
            }
        });
    }
    @Override
    public void SetData(Object object) {
        if(object != null){
            if(object == mPage){
                isNeedLoad = false;
            }else
                isNeedLoad = true;
            mPage = (NewsPaperPage) object;
            
            if(isResumed() && !isHidden() && isNeedLoad){
                loadData();
                
            }
        }else{
            isNeedLoad = false;
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        mScrollView.requestFocus();
        loadData();
        IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        mActivity.registerReceiver(receiver, filter);
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	mActivity.unregisterReceiver(receiver);
    }
    
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.d("onHiddenChanged", "hiddlen = " + hidden);
        if(!hidden)
            loadData();
        else{
            if(isTtsInited){
            	Log.d("onHiddenChanged", "hiddlen = " + hidden + ", isTtsInited = " + isTtsInited);
            	hasStoped = true;
            	stopPlay();
            }
        }
    }
    
    private void loadData(){
        if(isNeedLoad){
            Log.i("NewsPaperArticleContentFragment", "isNeedLoad");
            mTitleView.setText(mPage.title);
            mArticlePicLoadingView.setVisibility(View.VISIBLE);
            mImageManager.getBitmapDrawable(mPage.PicPath, new ImageCallback() {
                @Override
                public void imageLoaded(BitmapDrawable imageDrawable, String viewKey) {
                    mArticlePicLoadingView.setVisibility(View.INVISIBLE);
                    mArticleImageView.setVisibility(View.VISIBLE);
                    mArticleImageView.setImageBitmap(imageDrawable.getBitmap());
                   
                   
                }
            }, null);
            new AsyncTask<String, String, NewsPaperArticleContent>() {
                
              
                protected void onPreExecute() {
                    mParseContentLoadingView.setVisibility(View.VISIBLE);
                    mContentView.setVisibility(View.INVISIBLE);
                    initFooterInfo();
                };
                @Override
                protected NewsPaperArticleContent doInBackground(String... params) {
                    long starttime = System.currentTimeMillis();
                    mData = EPUBParser.parseNewsPaperContent(mPage.path);
                    return mData;
                }
                @Override
                protected void onPostExecute(NewsPaperArticleContent result) {
                    
                    mParseContentLoadingView.setVisibility(View.INVISIBLE);
                    if(result == null)
                        ToastUitl.showToast(getActivity(), R.string.error_message_parse_xml_fail);
                    
                     mContentView.setData(result);
                     readRecord = 0;
                     mContentView.setPalyIndex(0, 0);
                     mContentView.setVisibility(View.VISIBLE);
                     mHandler.post(new Runnable() {
                        
                        @Override
                        public void run() {
                            mScrollView.requestFocus();
                        }
                    });
                     mScrollView.scrollTo(0, 0);
                     Log.d("NewsPaperArticleContentFragment", "in loadData(), and isPlay = " + isPlay + ", hasStoped = " + hasStoped);
//                     isPlay = true;
                     hasStoped = true;
                }
            }.execute("");
        }else{
            mHandler.post(new Runnable() {
                
                @Override
                public void run() {
                    mScrollView.requestFocus();
                }
            });
        }
    }
    
    private void initFooterInfo(){
        String footerInfo = mActivity.getFooterInfo();
        if(footerInfo != null){
            String [] arr = footerInfo.split("#");
            mFooterMainCategoryName.setText(arr[0]);
            mFooterSubCategoryName.setText(arr[1]);
            mFooterPageName.setText(arr[2]);
        }
    }
    boolean isPlay = false;
    boolean hasStoped = true;
    boolean isTtsInited = false;
    private int readRecord = 0;
    private void startPlay(){
        mLog.i("startPlay");
        class TtsRunThread implements Runnable{           
            @Override
            public void run() {
                STOP:
                while (true) {
                    if(!isPlay){
//                    	Log.d("startPlay thread", "isPlay is false, sleep...");
                    	continue;
                    }
                        
                    List<NewsPaperArticleContent.Patch> patchs = null;
                    if (mData != null) {
                    	patchs = mData.patchs;
                    } else {
                    	Log.d("startPlay thread", " mData is null!");
                    }
                    
                    if(patchs != null){
                        Patch patch;
                        String text = getString(R.string.aticle_titie) + mPage.title;
                        if(readRecord == 0){
                            Tts.JniSpeak(text);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        if(patchs.size() == 0){
//                            isPlay = false;
                            mHandler.sendEmptyMessage(MESSAGE_PLAY_NEXT_ARTICAL);
                            Tts.JniSpeak(getString(R.string.paly_next_acticle));
                        } else {                        	
                        	for(int i = readRecord, count = patchs.size(); i < count; i ++) {
                        		Log.d("NewsPaperArticleContentFragment", " i = " + i + ", count = " + count + ", readRecord = " + readRecord);
//                        		if(!isPlay)
//                        			break STOP;
                        		if(isPlay){
                        			patch = patchs.get(i);
                        			text = getText(patch);
                        			mLog.i(text);
                        			mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_PLAY_REFRESH_VIEW, patch));
                        			Tts.JniSpeak(text);
                        			readRecord = i;
                        			if(i == count -1){
                        				readRecord = count;
                        				mHandler.sendEmptyMessage(MESSAGE_PLAY_NEXT_ARTICAL);
                        				Tts.JniSpeak(getString(R.string.paly_next_acticle));
                        				Log.d("NewsPaperArticleContentFragment", "-------i == " + (count -1) + "------------patchs.size() != 0 and in for");
                        			} 
                        			
                        			if (readRecord == count) {
                        				mHandler.sendEmptyMessage(MESSAGE_PLAY_STOP);
                        				Log.d("NewsPaperArticleContentFragment", " readRecord == count = " + readRecord);
                        			}
//                        			Log.d("NewsPaperArticleContentFragment", "-------i = " + i + "------------patchs.size() != 0 and in for");
                        		} else {
//                        			Log.d("NewsPaperArticleContentFragment", "-----------STOP--------isPlay = " + isPlay);
                        			break STOP;
                        		}
                        	}
                        }
                    } else {
                    	Log.d("startPlay thread", "patchs is null!");
//                    	isPlay = false;
                        mHandler.sendEmptyMessage(MESSAGE_PLAY_NEXT_ARTICAL);
                        Tts.JniSpeak(getString(R.string.paly_next_acticle));
                    }
                    
                    if (!isPlay) {
                    	break;
                    }
                }
            	Log.d("NewsPaperArticleContentFragment", "break from while!");
            }           
        }       
        Thread ttsRun = (new Thread(new TtsRunThread()));
        ttsRun.setPriority(Thread.MAX_PRIORITY);
        ttsRun.start(); 
        mVoiceIcon.setVisibility(View.VISIBLE);
        isPlay = true;
    }
    
    private void stopPlay(){
        
        mLog.i("stopPlay");
        mVoiceIcon.setVisibility(View.INVISIBLE);
        Tts.JniStop();
//        Log.d("NewsPaperArticleContentFragment", "===================isPlay = " + isPlay);
        isPlay = false;
    }

    private void unInitTtsEngine() {
        
        mLog.i("uninit TtsEngine");
        isTtsInited = false;
        Tts.JniDestory();
    }
    
    private String getText(NewsPaperArticleContent.Patch patch){
        StringBuilder builder = new StringBuilder();
        List<Block> blocks = mData.blocks;
        Block block;
        for(int i = patch.startIndex;i<= patch.endIndex ;i++){
            block = blocks.get(i);
            if(block.type != 4)
                builder.append(block.value);
        }
        mScrollView.scrollTo(0,blocks.get(patch.endIndex).y - mScrollView.getHeight() + 5);
        return builder.toString();
    }
    
    private void showTextFontSeeting(){
        final RelativeLayout contentView = (RelativeLayout) mActivity.getLayoutInflater().inflate(R.layout.newspaper_text_font_setting_view, null);
        final View operationCotentView = contentView.findViewById(R.id.operation_content_view);
        final View operationNotifyView = contentView.findViewById(R.id.operation_notify_view);
        operationCotentView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        final PopupWindow operationView = new PopupWindow(contentView, operationCotentView.getMeasuredWidth(), operationCotentView.getMeasuredHeight());
        operationView.setAnimationStyle(R.style.AnimationPreview);
        operationView.setFocusable(true);
        operationView.setOutsideTouchable(true);
        operationView.setBackgroundDrawable(new
                ColorDrawable(Color.parseColor("#00000000")));
        TextView loation = (TextView) mActivity.findViewById(R.id.textfont_location_view);
        operationView.showAsDropDown(loation,-(operationCotentView.getMeasuredWidth()/2- loation.getWidth()/2),-15);
        final SeekBar seekBar =   (SeekBar) contentView.findViewById(R.id.operation_seekbar);
        final TextView Big = (TextView) contentView.findViewById(R.id.big);
        final TextView Middle = (TextView) contentView.findViewById(R.id.middle);
        final TextView Small = (TextView) contentView.findViewById(R.id.small);
        defaultSize = mContentView.getTextSize();
        
        final int bigSize = (int) Big.getTextSize();
        final int middleSize = (int) Middle.getTextSize();
        final int smallSize = (int) Small.getTextSize();
        final int maxProgress = bigSize - smallSize;
        final int stepSize = maxProgress /2;
        seekBar.setMax(maxProgress);
        
        seekBar.setProgress(defaultSize - smallSize);
        TextView pointView;
        if(defaultSize ==bigSize){
            pointView = Big;
        }else if(defaultSize ==middleSize){
            pointView = Middle;
        }else{
            pointView = Small;
        }
        
        pointView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        operationCotentView.setVisibility(View.VISIBLE);
        operationNotifyView.setVisibility(View.GONE);
        seekBar.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                int action = event.getAction();
                if (action == KeyEvent.ACTION_DOWN) {

                    int process = seekBar.getProgress();
                    switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_UP: {
                        if (process < maxProgress){
                            updateFont(process + stepSize);
                        }
                        break;
                    }

                    case KeyEvent.KEYCODE_DPAD_DOWN: {
                        if (process > 0)
                            updateFont(process - stepSize);
                        break;
                    }
                    case KeyEvent.KEYCODE_DPAD_CENTER: {
                        
//                        operationCotentView.setVisibility(View.GONE);
//                        operationNotifyView.setVisibility(View.VISIBLE);
//                        contentView.postDelayed(new Runnable() {
//                            
//                            @Override
//                            public void run() {
//                                operationView.dismiss();
//                            }
//                        }, 2000);
                    }
                    }
                }
                return false;
            }
            
            private void updateFont(int progress){
                int textSize = smallSize + progress;
                Big.setTypeface(textSize == bigSize ? Typeface.defaultFromStyle(Typeface.BOLD):Typeface.defaultFromStyle(Typeface.NORMAL));
                Middle.setTypeface(textSize == middleSize ? Typeface.defaultFromStyle(Typeface.BOLD):Typeface.defaultFromStyle(Typeface.NORMAL));
                Small.setTypeface(textSize == smallSize ? Typeface.defaultFromStyle(Typeface.BOLD):Typeface.defaultFromStyle(Typeface.NORMAL));
                mContentView.setTextSize(textSize);
                seekBar.setProgress(progress);
            }
        });
        
    }
    private int defaultSize;
//    private void showTextFontSettingView(){
//        defaultSize = mContentView.getTextSize();
//        int id = R.id.small;
//        if(defaultSize == 36){
//            id = R.id.big;
//        }else if(defaultSize == 28){
//            id = R.id.middle;
//        }
//        mController.showTextFrontSettingDialog(new OnClickListener() {
//            
//            @Override
//            public void onClick(View v) {
//                mController.hideTextFrontSettingDialog();
//                defaultSize = mContentView.getTextSize();
//            }
//        },new OnFocusChangeListener() {
//            
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(hasFocus){
//                    if(v.getId() == R.id.big){
//                        mContentView.setTextSize(36);
//                    }else if(v.getId() == R.id.middle){
//                        mContentView.setTextSize(28);
//                    }else if(v.getId() == R.id.small){
//                        mContentView.setTextSize(22);
//                    }
//                }
//            }
//        },new OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//                if(defaultSize != mContentView.getTextSize()){
//                    mContentView.setTextSize(defaultSize);
//                }
//            }
//        },id);
//    }
    private void initTtsEngine(){
        Tts.JniCreate("/system/lib"+"/Resource.irf");
//      Tts.JniCreate(Environment.getExternalStorageDirectory()+"/Resource.irf");
        /**
         * set language 
         * #define ivTTS_LANGUAGE_AUTO             0           /* Detect language automatically default
         * #define ivTTS_LANGUAGE_CHINESE           1           /* Chinese (with English) 
         * #define ivTTS_LANGUAGE_ENGLISH           2           /* English 
         * */
        Tts.JniSetParam(256, 1); 
        /**
         * set role
         *  #define ivTTS_ROLE_TIANCHANG            1           /* Tianchang (female, Chinese)
            #define ivTTS_ROLE_WENJING              2           /* Wenjing (female, Chinese) 
            #define ivTTS_ROLE_XIAOYAN              3           /* Xiaoyan (female, Chinese) 
            #define ivTTS_ROLE_YANPING              3           /* Xiaoyan (female, Chinese) 
            #define ivTTS_ROLE_XIAOFENG             4           /* Xiaofeng (male, Chinese) 
            #define ivTTS_ROLE_YUFENG               4           /* Xiaofeng (male, Chinese) 
            #define ivTTS_ROLE_SHERRI               5           /* Sherri (female, US English) 
            #define ivTTS_ROLE_XIAOJIN              6           /* Xiaojin (female, Chinese) 
            #define ivTTS_ROLE_NANNAN               7           /* Nannan (child, Chinese) 
            #define ivTTS_ROLE_JINGER               8           /* Jinger (female, Chinese) 
            #define ivTTS_ROLE_JIAJIA               9           /* Jiajia (girl, Chinese) 
            #define ivTTS_ROLE_YUER                 10          /* Yuer (female, Chinese) 
            #define ivTTS_ROLE_XIAOQIAN             11          /* Xiaoqian (female, Chinese Northeast) 
            #define ivTTS_ROLE_LAOMA                12          /* Laoma (male, Chinese) 
            #define ivTTS_ROLE_BUSH                 13          /* Bush (male, US English) 
            #define ivTTS_ROLE_XIAORONG             14          /* Xiaorong (female, Chinese Szechwan) 
            #define ivTTS_ROLE_XIAOMEI              15          /* Xiaomei (female, Cantonese) 
            #define ivTTS_ROLE_ANNI                 16          /* Anni (female, Chinese) 
            #define ivTTS_ROLE_JOHN                 17          /* John (male, US English) 
            #define ivTTS_ROLE_ANITA                18          /* Anita (female, British English) 
            #define ivTTS_ROLE_TERRY                19          /* Terry (female, US English) 
            #define ivTTS_ROLE_CATHERINE            20          /* Catherine (female, US English) 
            #define ivTTS_ROLE_TERRYW               21          /* Terry (female, US English Word) 
            #define ivTTS_ROLE_XIAOLIN              22          /* Xiaolin (female, Chinese) 
            #define ivTTS_ROLE_XIAOMENG             23          /* Xiaomeng (female, Chinese) 
            #define ivTTS_ROLE_XIAOQIANG            24          /* Xiaoqiang (male, Chinese) 
            #define ivTTS_ROLE_XIAOKUN              25          /* XiaoKun (male, Chinese) 
            #define ivTTS_ROLE_JIUXU                51          /* Jiu Xu (male, Chinese) 
            #define ivTTS_ROLE_DUOXU                52          /* Duo Xu (male, Chinese) 
            #define ivTTS_ROLE_XIAOPING             53          /* Xiaoping (female, Chinese) 
            #define ivTTS_ROLE_DONALDDUCK           54          /* Donald Duck (male, Chinese) 
            #define ivTTS_ROLE_BABYXU               55          /* Baby Xu (child, Chinese) 
            #define ivTTS_ROLE_DALONG               56          /* Dalong (male, Cantonese) 
            #define ivTTS_ROLE_TOM                  57          /* Tom (male, US English) 
            #define ivTTS_ROLE_USER                 99          /* user defined 
         */
        Tts.JniSetParam(1280, 3);
        /**
         * set speak style
         *  #define ivTTS_STYLE_PLAIN               0           /* plain speak style 
            #define ivTTS_STYLE_NORMAL              1           /* normal speak style (default) 
         * */
        Tts.JniSetParam(1281, 1);
        /**
         * set voice effect - predefined mode 
         *  #define ivTTS_VEMODE_NONE               0           /* none(default)
            #define ivTTS_VEMODE_WANDER             1           /* wander 
            #define ivTTS_VEMODE_ECHO               2           /* echo 
            #define ivTTS_VEMODE_ROBERT             3           /* robert 
            #define ivTTS_VEMODE_CHROUS             4           /* chorus 
            #define ivTTS_VEMODE_UNDERWATER         5           /* underwater 
            #define ivTTS_VEMODE_REVERB             6           /* reverb 
            #define ivTTS_VEMODE_ECCENTRIC          7           /* eccentric 
         * */
        Tts.JniSetParam(1536, 0);
        isTtsInited = true;
    } 
}
