package com.dbstar.multiple.media.shelf.activity;

import java.util.List;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dbstar.multiple.media.adapter.MagazinePageAdapter;
import com.dbstar.multiple.media.common.ImageManager;
import com.dbstar.multiple.media.data.NewsPaper;
import com.dbstar.multiple.media.data.NewsPaperArticleContent;
import com.dbstar.multiple.media.data.NewsPaperArticleContent.Block;
import com.dbstar.multiple.media.data.NewsPaperArticleContent.Patch;
import com.dbstar.multiple.media.data.NewsPaperPage;
import com.dbstar.multiple.media.shelf.R;
import com.dbstar.multiple.media.util.EPUBParser;
import com.dbstar.multiple.media.util.ImageUtil;
import com.dbstar.multiple.media.util.ToastUitl;
import com.dbstar.multiple.media.widget.CustomListView;
import com.dbstar.multiple.media.widget.NewsPaperContentView;
import com.iflytek.tts.TtsService.Tts;

public class MagazineArticleContentActivity extends Activity {
	
	private static final String TAG = "MagazineArticleContentActivity";
	private static int MESSAGE_MAGAZINE_PLAY_REFRESH_VIEW = 0x400;
	private static int MESSAGE_MAGAZINE_PLAY_NEXT_ARTICAL = 0x500;
	private static int MESSAGE_MAGAZINE_PLAY_STOP = 0x600;
	    
	private NewsPaper mCurrentNewsPaper;
	private NewsPaperPage mCurrentPage;
	private NewsPaperContentView mContentView;
//	private TextView mTitleView;
	private ImageView mVoiceIcon;
	private ImageManager mImageManager;
	private boolean isNeedLoad = true;
	private LinearLayout mContainer;
	private RelativeLayout mArticlePicLoadingView;
	private RelativeLayout mParseContentLoadingView;
	private ScrollView mScrollView;
	private TextView mFooterMainCategoryName, mFooterSubCategoryName, mFooterPageName;
	private TextView mCurrentPaperTitle, mCurrentPaperPageCount, mCurrentPaperPublishDate, mMagazineArticleTitle;
	private NewsPaperArticleContent mData;
	private ListView mPageListView;
	private MagazinePageAdapter mEditionadapter;
	
	private String mMainCatogoryName, mSubCategoryName;
	private int mCurrentPageNum, mArticlePosition = 0;
	private int mListViewPosition = 0;
	
	private Bitmap mBitmap;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			if (what == MESSAGE_MAGAZINE_PLAY_REFRESH_VIEW) {
				Patch patch = (Patch) msg.obj;
				mContentView.setPalyIndex(patch.startIndex, patch.endIndex);
			} else if (what ==  MESSAGE_MAGAZINE_PLAY_NEXT_ARTICAL) {
				showNextArticle();
			} else if (what == MESSAGE_MAGAZINE_PLAY_STOP) {
				stopPlay();
				hasStoped = true;
			}
		};
	};
	
    private void showNextArticle(){
//    	Log.d(TAG, "<<<<<<<<<mCurrentPage.SelectedIndex = " + mCurrentPage.SelectedIndex);
    	mCurrentPage.SelectedIndex ++;
    	MagazinePageAdapter adapter = (MagazinePageAdapter) mPageListView.getAdapter();
        Log.d(TAG, " adapter.getCount() = " + adapter.getCount() + ", mCurrentPage.SelectedIndex = " + mCurrentPage.SelectedIndex);
        if(mCurrentPage.SelectedIndex < adapter.getCount()){
            NewsPaperPage article = adapter.getEdition(mCurrentPage.SelectedIndex);
//            adapter.setMarkPosition(mCurrentPage.SelectedIndex);
            article.IsOpen = 1;
            article.PicPath = mCurrentPage.PicPath;
			mArticlePosition++;
			if (mCurrentPage.mArticles != null && mCurrentPage.mArticles.size() > mArticlePosition) {
				mMagazineArticleTitle.setText(mCurrentPage.mArticles.get(mArticlePosition).title);
			} else if (mCurrentPage.mArticles != null && mCurrentPage.mArticles.size() > 0) {
				mMagazineArticleTitle.setText(mCurrentPage.mArticles.get(0).title);
			}
			loadData();
			loadArticleListData(mCurrentPage);
			showCurrentPaperInfor(mCurrentPage);
			initFooterInfo();
        }
        else {
            //TODO  showNextEdition
//        	Log.d(TAG, "<<<<<<<<<?<<<<<<<<");
            showNextPage();
        }
    }
    
    private void showNextPage(){
        mCurrentNewsPaper.SelectedIndex ++;
        mCurrentPageNum++;
        Log.i(TAG, "showNextPage, mCurrentPageNum =" +  mCurrentPageNum + ", mCurrentNewsPaper.Pages.size() = " + mCurrentNewsPaper.Pages.size());
        if (mCurrentNewsPaper != null && mCurrentNewsPaper.Pages != null && mCurrentNewsPaper.Pages.size() > mCurrentPageNum) {
        	NewsPaperPage page = mCurrentNewsPaper.Pages.get(mCurrentPageNum);
        	page.IsOpen = 1;
        	page.SelectedIndex = -1;
        	mCurrentPage = page;
        	changeListData(page);        	
        }
    }
    
    private void changeListData(NewsPaperPage page){
        if(page != null){
        	mArticlePosition = -1;
            showNextArticle();
        }
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.periodcial_article_content_view);
		Intent intent = getIntent();
		
		mCurrentNewsPaper = (NewsPaper) intent.getSerializableExtra("currentNewsPaper");
		mCurrentPage = (NewsPaperPage) intent.getSerializableExtra("currentPage");
		mMainCatogoryName = intent.getStringExtra("mainCategoryName");
		mSubCategoryName = intent.getStringExtra("subCategoryName");
		mCurrentPageNum = intent.getIntExtra("currentPageNumber", 0);
		
		if (mCurrentPage != null) {
			mCurrentPage.SelectedIndex = 0;
		}
		
		initViews();
		
		String appUri = intent.getStringExtra(ImageUtil.AppBG_Uri);
		mBitmap = ImageUtil.getBitmap(appUri);
		if (mBitmap == null) {
			mContainer.setBackgroundResource(R.drawable.reader_view_background);
		} else {
			Drawable drawable = new BitmapDrawable(mBitmap);
			mContainer.setBackgroundDrawable(drawable);
		}
		
		loadData();
		loadArticleListData(mCurrentPage);
		showCurrentPaperInfor(mCurrentPage);
		
		 mArticlePicLoadingView.setVisibility(View.INVISIBLE);
		 if (mCurrentPage.mArticles != null && mCurrentPage.mArticles.size() > mArticlePosition) {				 
			 mMagazineArticleTitle.setText(mCurrentPage.mArticles.get(mArticlePosition).title);
		 } else if (mCurrentPage.mArticles != null && mCurrentPage.mArticles.size() > 0) {
	 		mMagazineArticleTitle.setText(mCurrentPage.mArticles.get(0).title);
		 }
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(receiver, filter);
	};
	
	@Override
	protected void onDestroy() {
		if (Tts.isInitialized()) {
			hasStoped = true;
			if (isPlay) {
				stopPlay();
			}
//			unInitTtsEngine();
		}
		unregisterReceiver(receiver);
		if (mBitmap != null && !mBitmap.isRecycled()) {
			mBitmap.recycle();
			mBitmap = null;
		}
		
		System.gc();
		super.onDestroy();
	};
	
    BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				Log.d(TAG, " ==pressed home key!== ");
				if (Tts.isInitialized() && isPlay) {
					stopPlay();
					hasStoped = true;
				}
			}
			
		}
	};

	private void initViews() {
		mContainer = (LinearLayout) findViewById(R.id.magazine_article_content_container);
		mCurrentPaperTitle = (TextView) findViewById(R.id.current_magazine_title);
		mCurrentPaperPageCount = (TextView) findViewById(R.id.magazine_total_period);
		mCurrentPaperPublishDate = (TextView) findViewById(R.id.magazine_content_list);

		mPageListView = (ListView) findViewById(R.id.magazine_article_title_list);
		mArticlePicLoadingView = (RelativeLayout) findViewById(R.id.magazine_article_pic_loading_bar);
		mMagazineArticleTitle = (TextView) findViewById(R.id.magazine_article_title);
		mVoiceIcon = (ImageView) findViewById(R.id.magazine_voice_icon);
		mParseContentLoadingView = (RelativeLayout) findViewById(R.id.magazine_parse_content_bar);
		mScrollView = (ScrollView) findViewById(R.id.magazine_scroll_content_view);
		mContentView = (NewsPaperContentView) findViewById(R.id.magazine_contentView);
		mFooterMainCategoryName = (TextView) findViewById(R.id.magazine_footer_main_category);
		mFooterSubCategoryName = (TextView) findViewById(R.id.magazine_footer_sub_category);
		mFooterPageName = (TextView) findViewById(R.id.magazine_footer_page);
		
		mVoiceIcon.setVisibility(View.GONE);
		mCurrentPaperTitle.setText("");
        mCurrentPaperPageCount.setText("");
        
        mPageListView.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mListViewPosition = position;
//				Log.d(TAG, "=----------------------mListViewPosition = " + mListViewPosition);
				mCurrentPage.SelectedIndex = position;
				if (view != null) {
					mCurrentPage.PaddinTop = view.getTop();
				}
				MagazinePageAdapter adapter = (MagazinePageAdapter) mPageListView.getAdapter();
				NewsPaperPage article = adapter.getEdition(position);
//				adapter.setMarkPosition(position);
				article.IsOpen = 1;
				if (view != null) {
					TextView textView = (TextView) view.findViewById(R.id.category_name);
//					textView.setTextColor(Color.RED);
					textView.setTextColor(Color.WHITE);
					textView.setTag("#FFFF0000");
				}
				article.PicPath = mCurrentPage.PicPath;
//				mCurrentPage = article;
				mArticlePosition = position;
				
				if (mCurrentPage.mArticles != null && mCurrentPage.mArticles.size() > position) {				 
					mMagazineArticleTitle.setText(mCurrentPage.mArticles.get(position).title);
				} else if (mCurrentPage.mArticles != null && mCurrentPage.mArticles.size() > 0) {
					mMagazineArticleTitle.setText(mCurrentPage.mArticles.get(0).title);
				}
				loadData();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
        
        mPageListView.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					View view = null;
					switch(keyCode) {
					case KeyEvent.KEYCODE_DPAD_RIGHT:
//						TextView textView =  (TextView) mPageListView.getSelectedView().findViewById(R.id.category_name);
//                        textView.setTextColor(Color.parseColor((String) textView.getTag()));
						mHandler.post(new Runnable() {

							@Override
							public void run() {
								mScrollView.requestFocus();
							}
						});
                       break;
					}
	               }
				return false;
			}
		});
	
        AudioManager audioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
        audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
        
        mScrollView.setOnKeyListener(new OnKeyListener() {
        	long lastOnKeyTime;
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
						return true;
					} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
						mPageListView.requestFocus();
						Log.d(TAG, "mListViewPosition = " + mListViewPosition);
						mPageListView.setSelection(mListViewPosition);
						if (mEditionadapter != null) {							
							mEditionadapter.notifyDataSetChanged();
						}
						return true;
					} else if (keyCode == KeyEvent.KEYCODE_ALT_LEFT || keyCode == KeyEvent.KEYCODE_VOLUME_MUTE) {
						long currentTime = System.currentTimeMillis();
						if (currentTime - lastOnKeyTime > 2000) {
							Log.d(TAG, " onKey in rm, isTtsInited = " + Tts.isInitialized());
							if (!Tts.isInitialized()) {
								Log.d(TAG, " onKey in rm, initTtsEngine(),Tts.JniIsPlaying()" + Tts.JniIsPlaying());
								if (Tts.JniIsPlaying() != 0) {
									Tts.initTtsEngine();
								}
//								else 
//									isTtsInited = true;
							}
							
							if (Tts.isInitialized() && isPlay) {
								stopPlay();
								hasStoped = true;
								Log.d(TAG, " onKey in rm, stopPlay(), KEYCODE_ALT_LEFT, Tts.JniIsPlaying() = " + Tts.JniIsPlaying());
							} else if (Tts.JniIsPlaying() != 1 && hasStoped) {
								hasStoped = false;
								Log.d(TAG, " onKey in rm, startPlay(), KEYCODE_ALT_LEFT, Tts.JniIsPlaying() = " + Tts.JniIsPlaying());
								startPlay();
							} else {
								Log.d(TAG, "onkey in rm, ignore no status KEYCODE_ALT_LEFT, Tts.JniIsPlaying() = " + Tts.JniIsPlaying() + ", isPlay = " + isPlay + ", hasStoped = " + hasStoped);
							}
							lastOnKeyTime = currentTime;
						} else {
							Log.d(TAG, "onkey in rm, ignore too frequency KEYCODE_ALT_LEFT");
						}
						return true;
					} 
//					else if (keyCode == KeyEvent.KEYCODE_NOTIFICATION) {
//						showTextFontSeeting();
//						return true;
//					}
				}
				return false;
			}
		});
	}

	boolean isPlay = false;
	boolean hasStoped = true;
	private int readRecord = 0;
	private void startPlay() {
		Log.d(TAG, "startPlay");
		class TtsRunThread implements Runnable {

			@Override
			public void run() {
				STOP:
				while (true) {
					if (!isPlay) {
						Log.d(TAG, "isPlay is false......");
						continue;
					}
					
					List<NewsPaperArticleContent.Patch> patchs = null;
					if (mData != null) {
						patchs = mData.patchs;
					} else {
						Log.d(TAG, " mData is null! ");
					}
					
					if (patchs != null) {
						Patch patch;
						Log.d(TAG, " mCurrentPage.title = " + mMagazineArticleTitle.getText().toString());
						String text = getString(R.string.aticle_titie) + mMagazineArticleTitle.getText().toString();
						if (readRecord == 0) {
							Tts.JniSpeak(text);
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						
						if (patchs.size() == 0) {
							mHandler.sendEmptyMessage(MESSAGE_MAGAZINE_PLAY_NEXT_ARTICAL);
							Tts.JniSpeak(getString(R.string.paly_next_acticle));
						} else {
							for (int i = readRecord, count = patchs.size(); i < count; i++) {
								Log.d(TAG, " i = " + i + ", count = " + count + ", readRecord = " + readRecord);
								if (isPlay) {
									patch = patchs.get(i);
									text = getText(patch);
									Log.d(TAG, "text = " + text);
									mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_MAGAZINE_PLAY_REFRESH_VIEW, patch));
									Tts.JniSpeak(text);
									readRecord = i;
									if (i == count - 1) {
										readRecord = count;
										mHandler.sendEmptyMessage(MESSAGE_MAGAZINE_PLAY_NEXT_ARTICAL);
										Tts.JniSpeak(getString(R.string.paly_next_acticle));
										Log.d(TAG, "-- i = " + (count -1) + ", patchs.size() != 0 and in for ");
									}
									
									if (readRecord == count) {
										mHandler.sendEmptyMessage(MESSAGE_MAGAZINE_PLAY_STOP);
										Log.d(TAG, " readRecord == count = " + readRecord);
									}
								} else {
									break STOP;
								}
							}
						}
					} else {
						Log.d(TAG, " patchs is null !");
						mHandler.sendEmptyMessage(MESSAGE_MAGAZINE_PLAY_NEXT_ARTICAL);
						Tts.JniSpeak(getString(R.string.paly_next_acticle));
					}
					
					if (!isPlay) {
						break;
					}
				}
			Log.d(TAG, " break from while! ");
			}
		}
		Thread ttsRun = new Thread(new TtsRunThread());
		ttsRun.setPriority(Thread.MAX_PRIORITY);
		ttsRun.start();
		mVoiceIcon.setVisibility(View.VISIBLE);
		isPlay = true;
	}
	
	private void stopPlay() {
		Log.d(TAG, " stopPlay ");
		mVoiceIcon.setVisibility(View.GONE);
		Tts.JniStop();
		isPlay = false;
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
    
	 private void showCurrentPaperInfor(NewsPaperPage page){
	        if (page == null) {
	            mCurrentPaperTitle.setText("");
	            mCurrentPaperPageCount.setText("");
	        }else{
	            mCurrentPaperTitle.setText(mSubCategoryName);
//	            mCurrentPaperPageCount.setText(getString(R.string.di) + mCurrentPageNum + getString(R.string.qi));
	            mCurrentPaperPageCount.setText(mCurrentPage.title);
	        }
	    }
	 
	 private void loadArticleListData(NewsPaperPage currentPage) {
		 if (isNeedLoad) {
			 if (currentPage == null) {				 
				 Log.i(TAG, "currentPage = " + currentPage);
				 return;
			 }
			 
			 if (mEditionadapter == null) {
//				 mEditionadapter = new NewsPaperPageAdapter(MagazineArticleContentActivity.this, "#0A3782",R.layout.newspaper_page_list_item);
				 mEditionadapter = new MagazinePageAdapter(MagazineArticleContentActivity.this, "#FFFFFF",R.layout.magazine_page_list_item);
				 mEditionadapter.setData(currentPage.mArticles);
				 mPageListView.setAdapter(mEditionadapter);
			 } else {
				 mEditionadapter.setData(currentPage.mArticles);
				 mEditionadapter.notifyDataSetChanged();
			 }
			 
			initFooterInfo();
			mPageListView.requestFocus();
			Log.i(TAG, "showNextArticle, mCurrentPage.SelectedIndex = " +  mCurrentPage.SelectedIndex);
			int index = currentPage.SelectedIndex;
//			mEditionadapter.setMarkPosition(index);
//			mPageListView.setSelectLastIndex(index == -1 ? 0 : index, currentPage.PaddinTop);
			mPageListView.post(new Runnable() {

				@Override
				public void run() {
					mPageListView.setSelection(mListViewPosition);
//					mPageListView.requestSelectItem(mListViewPosition);
					if (mEditionadapter != null) {							
						mEditionadapter.notifyDataSetChanged();
					}
				}
			});
		 }
	 }
	 
	 private void loadData() {
		 if (isNeedLoad) {
			 Log.i(TAG, "isNeedLoad");
			 if (mCurrentPage == null) {				 
				 Log.i(TAG, "mCurrentPage = " + mCurrentPage);
				 return;
			 }
			 
			 new AsyncTask<String, String, NewsPaperArticleContent> (){

				 @Override
				protected void onPreExecute() {
					super.onPreExecute();
					mParseContentLoadingView.setVisibility(View.VISIBLE);
				}
				 
				@Override
				protected NewsPaperArticleContent doInBackground(String... params) {
					if (mCurrentPage.mArticles != null && mCurrentPage.mArticles.size() > mArticlePosition) {
						String path = mCurrentPage.mArticles.get(mArticlePosition).path;
						Log.i(TAG, " path = " + path);
						mData = EPUBParser.parseNewsPaperContent(path);
						
					}
					return mData;
				}
				
				@Override
				protected void onPostExecute(NewsPaperArticleContent result) {
					super.onPostExecute(result);
					
					 
                    mParseContentLoadingView.setVisibility(View.INVISIBLE);
                    if(result == null)
                        ToastUitl.showToast(MagazineArticleContentActivity.this, R.string.error_message_parse_xml_fail);
					mContentView.setData(mData);
					
	                readRecord = 0;
	                mContentView.setPalyIndex(0, 0);
	                mContentView.setVisibility(View.VISIBLE);
//	                mHandler.post(new Runnable() {
//	                   
//	                   @Override
//	                   public void run() {
//	                       mScrollView.requestFocus();
//	                   }
//	               });
	                mScrollView.scrollTo(0, 0);
	                Log.d("TAG", "in loadData(), and isPlay = " + isPlay + ", hasStoped = " + hasStoped);
	                hasStoped = true;
				}
			 }.execute(""); 
		 } 
//		 else {
//			 mHandler.post(new Runnable() {
//	                
//	                @Override
//	                public void run() {
//	                    mScrollView.requestFocus();
//	                }
//	         });
//		 }
	 }
	 
	 private void initFooterInfo() {
		 mFooterMainCategoryName.setText(mMainCatogoryName);
		 mFooterSubCategoryName.setText(mSubCategoryName);
		 if (mCurrentPage != null) {			 
			 // TODO：
			 mFooterPageName.setText(mCurrentPage.title);
		 }
	 }
}
