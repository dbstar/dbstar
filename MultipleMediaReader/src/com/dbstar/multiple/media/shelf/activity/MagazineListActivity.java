package com.dbstar.multiple.media.shelf.activity;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dbstar.multiple.media.common.ImageManager;
import com.dbstar.multiple.media.common.ImageManager.ImageCallback;
import com.dbstar.multiple.media.common.ShelfController;
import com.dbstar.multiple.media.data.NewsPaper;
import com.dbstar.multiple.media.data.NewsPaperPage;
import com.dbstar.multiple.media.shelf.R;
import com.dbstar.multiple.media.util.EPUBParser;
import com.dbstar.multiple.media.util.ImageUtil;
import com.dbstar.multiple.media.util.ToastUitl;
import com.dbstar.multiple.media.widget.MagazineSubSubCategoryView;

public class MagazineListActivity extends Activity {

	private static final String TAG = "MagazineListActivity";
	private Bitmap mBitmap;
	private LinearLayout mContainer;
//	private HistoryInfo mHistoryInfo;
	
	private MagazineSubSubCategoryView mSubCategoryView;
	private List<NewsPaper> mNewsPapers;
	private List<NewsPaperPage> mNewsPaperPages;
    private ImageManager mImageManager;
	private NewsPaper mCurrentNewsPaper;
	private ShelfController mController;
	private TextView mFooterMainCategoryName, mFooterSubCategoryName, mFooterPageName;
	private ImageView mPage1, mPage2;
	private FrameLayout mPageGroup;
	private RelativeLayout mPicLoadingView;
	private NewsPaperPage mCurrentPage;
	private String mMainCatogoryName, mSubCategoryName;
	private String mSetID;
	private int mPageNumber = 0;
    private int mPageCount;
//    private int mNewsPaperIndex;
    private String mAppUri;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.periodcial_subcategory_list_fragment_view);
		
		mImageManager = ImageManager.getInstance(this);
		mController = ShelfController.getInstance(this);
		mContainer = (LinearLayout) findViewById(R.id.magazine_list_container);
		mAppUri = getIntent().getStringExtra(ImageUtil.AppBG_Uri);
		mBitmap = ImageUtil.getBitmap(mAppUri);
		if (mBitmap == null) {
			mContainer.setBackgroundResource(R.drawable.magazine_list_bk);
		} else {
			Drawable drawable = new BitmapDrawable(mBitmap);
			mContainer.setBackgroundDrawable(drawable);
		}
		
		mMainCatogoryName = getIntent().getStringExtra("mainCategoryName");
		mSubCategoryName = getIntent().getStringExtra("subCategoryName");
		mSetID = getIntent().getStringExtra("setId");
		
		initView();
		loadMagazine(mSetID);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mBitmap != null && !mBitmap.isRecycled()) {
        	mBitmap.recycle();
        	mBitmap = null;
        }
        System.gc();
	}
	
	private void showUI() {
		if (mCurrentPage == null) 
			return;
		mPage2.bringToFront();
//		Log.d(TAG, "----------showUI()------mCurrentPage.PicPath = " + mCurrentPage.PicPath);
		mImageManager.getMagazineBitmapDrawable(mCurrentPage.PicPath, new ImageCallback() {
			
			@Override
			public void imageLoaded(BitmapDrawable imageDrawable, String viewKey) {
//				Log.d(TAG, "----------getNewsPaperPage(mPageNumber).path.equals(viewKey) = " + getNewsPaperPage(mPageNumber).path.equals(viewKey));
				if (getNewsPaperPage(mPageNumber).path.equals(viewKey)) {
					mPage2.setImageBitmap(imageDrawable.getBitmap());
					mPage2.bringToFront();
				}
			}
		}, mCurrentPage.path);
		
		if (mPageNumber + 1 < mPageCount) {
			mImageManager.getMagazineBitmapDrawable(mCurrentPage.PicPath, null, "");
		}
		
	}
	
	private NewsPaperPage getNewsPaperPage(int pageNumber) {
		if (pageNumber < mNewsPaperPages.size()) 
			return mNewsPaperPages.get(pageNumber);
		return null;
	}

	private void initView() {
		mPicLoadingView = (RelativeLayout) findViewById(R.id.periodcial_subCategory_periodcial_pic_loading_bar);
		mSubCategoryView = (MagazineSubSubCategoryView) findViewById(R.id.periodcial_subCategory_category);
		mFooterMainCategoryName = (TextView) findViewById(R.id.periodcial_subCategory_footer_main_category);
		mFooterSubCategoryName = (TextView) findViewById(R.id.periodcial_subCategory_footer_sub_category);
		mFooterPageName = (TextView) findViewById(R.id.periodcial_subCategory_footer_page);
		mPageGroup = (FrameLayout) findViewById(R.id.periodcial_subCategory_list_pageGroup);
		mPage1 = (ImageView) findViewById(R.id.periodcial_subCategory_list_view1);
		mPage2 = (ImageView) findViewById(R.id.periodcial_subCategory_list_view2);
		
		mSubCategoryView.setOnItemSelectedListener(new MagazineSubSubCategoryView.OnItemSelectedListener() {

			@Override
			public void onSelected(View v, NewsPaper paper) {
				mCurrentNewsPaper = paper;
//				List<NewsPaperPage> parseNewsPaperPage = EPUBParser.parseNewsPaperPage(paper.RootPath , paper.CataloguePath);
				initFooterInfo();
			}
		});
	}
	
	
	private void initParams() {
		if (mNewsPaperPages != null && mNewsPaperPages.size() > 0) {
			mPageCount = mNewsPaperPages.size();
			mPageNumber = 0;
//			mCurrentPage = mNewsPaperPages.get(0);
			if (mCurrentNewsPaper != null && mCurrentNewsPaper.Pages != null && mCurrentNewsPaper.Pages.size() > 0) {				
				mCurrentPage = mCurrentNewsPaper.Pages.get(0);
			}
		}
	}
	
	private void showNextPage() {
		if (mPageNumber >= mPageCount - 1) {
			ToastUitl.showToast(MagazineListActivity.this, "已经是最后一页了");
			return;
		}
		mPageNumber++;
//		mCurrentPage = mNewsPaperPages.get(mPageNumber);
		mCurrentPage = mCurrentNewsPaper.Pages.get(mPageNumber);
		final ImageView fontView = (ImageView) mPageGroup.getChildAt(1);
		TranslateAnimation animation = new TranslateAnimation(0, -900, 0, 0);
		animation.setDuration(500);
		
		animation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				ImageView view = (ImageView) mPageGroup.getChildAt(0);
//				Log.d(TAG, "----------------mCurrentPage.PicPath = " + mCurrentPage.PicPath);
				view.setImageBitmap(mImageManager.getBitmapDrawable(mCurrentPage.PicPath).getBitmap());
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				fontView.clearAnimation();
				ImageView backView = (ImageView) mPageGroup.getChildAt(0);
				backView.bringToFront();
				if (mPageNumber + 1 > mPageCount - 1) {
					Log.d(TAG, " mPageCount = " + mPageCount);
//					mHandler.sendEmptyMessage(MESSAGE_WAHT_ANIMATION_END);
				} else {
					mImageManager.getMagazineBitmapDrawable(mNewsPaperPages.get(mPageNumber + 1).PicPath, new ImageCallback() {
						
						@Override
						public void imageLoaded(BitmapDrawable imageDrawable, String viewKey) {
//							mHandler.sendEmptyMessage(MESSAGE_WAHT_ANIMATION_END);
						}
					}, "");
				}
				
			}
		});
		fontView.startAnimation(animation);
	}
	
	private void showPrePage() {
		if (mPageNumber <= 0) {
			ToastUitl.showToast(MagazineListActivity.this, "已经是第一页了");			
			return; 
		}
		mPageNumber--;
//		mCurrentPage = mNewsPaperPages.get(mPageNumber);
		mCurrentPage = mCurrentNewsPaper.Pages.get(mPageNumber);
		final ImageView fontView = (ImageView) mPageGroup.getChildAt(1);
		TranslateAnimation animation = new TranslateAnimation(0, 900, 0, 0);
		animation.setDuration(500);
		
		animation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				ImageView view = (ImageView) mPageGroup.getChildAt(0);
				view.setImageBitmap(mImageManager.getBitmapDrawable(mCurrentPage.PicPath).getBitmap());
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				fontView.clearAnimation();
				ImageView backView = (ImageView) mPageGroup.getChildAt(0);
				backView.bringToFront();
//				mHandler.sendEmptyMessage(MESSAGE_WAHT_ANIMATION_END);
			}
		});
		fontView.startAnimation(animation);
	}
	
	private void initFooterInfo() {
		mFooterMainCategoryName.setText(mMainCatogoryName);
		mFooterSubCategoryName.setText(mSubCategoryName);
		if (mCurrentPage != null) {			
			mFooterPageName.setText(mCurrentPage.title);
		}
	}

	private void loadMagazine(String setId) {
		new AsyncTask<String, Object, Object>() {
			
			@Override
			protected void onPreExecute() {
				mPicLoadingView.setVisibility(View.VISIBLE);
			};
			
			@Override
			protected Object doInBackground(String... params) {
				String id = params[0];

				mNewsPapers = mController.loadMagazines(id);

				if (mNewsPapers != null && mNewsPapers.size() > 0) {
					mCurrentNewsPaper = mNewsPapers.get(0);
					if(mCurrentNewsPaper.RootPath == null)
	                    return null;
	                String ncxPath = EPUBParser.getNCXPath(mCurrentNewsPaper.RootPath);
	                if(ncxPath == null)
	                    return null;
	                mCurrentNewsPaper.CataloguePath = ncxPath;
	                mCurrentNewsPaper.Pages = EPUBParser.parseNewsPaperPage(mCurrentNewsPaper.RootPath , mCurrentNewsPaper.CataloguePath);
	                mNewsPaperPages = EPUBParser.parseNewsPaperPagePicSrc(mCurrentNewsPaper.RootPath, mCurrentNewsPaper.Pages);
				}
				return null;
			}

			@Override
			protected void onPostExecute(Object result) {
				mPicLoadingView.setVisibility(View.GONE);
				showSubCategory(mNewsPapers);
				mCurrentPage = mCurrentNewsPaper.Pages.get(0);
				showUI();
				initParams();
				initFooterInfo();
			}
		}.execute(setId);
	}	

	  
	 private void showSubCategory(List<NewsPaper> data) {
		 mSubCategoryView.requestFocus();
	        if (data == null || data.isEmpty()) {
	            // TODO clear view and notify user there no any categroy
	        	Log.d(TAG, " in show subCategory, data is null!");
	            mSubCategoryView.setData(null);
	            mSubCategoryView.notifyDataChanged();
	            return;
	        }
	        mSubCategoryView.setSelection(0);
	        mSubCategoryView.setData(data);
	        mSubCategoryView.notifyDataChanged();

	    }

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			System.gc();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			Log.d(TAG, "------------KeyEvent.KEYCODE_DPAD_LEFT-----------");
			showPrePage();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			Log.d(TAG, "------------KeyEvent.KEYCODE_DPAD_RIGHT-----------");
			showNextPage();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
			Intent intent = new Intent();
			intent.setClass(MagazineListActivity.this, MagazineArticleContentActivity.class);
			
			if (mCurrentPage != null) {
				intent.putExtra(ImageUtil.AppBG_Uri, mAppUri);
				intent.putExtra("mainCategoryName", mMainCatogoryName);
				intent.putExtra("subCategoryName", mSubCategoryName);
				intent.putExtra("currentPageNumber", mPageNumber);
				intent.putExtra("currentPage", mCurrentPage);
				intent.putExtra("currentNewsPaper", mCurrentNewsPaper);
			} else {
				Log.d(TAG, "------------KeyEvent.KEYCODE_DPAD_CENTER -----------mCurrentPage = null!");				
			}
			startActivity(intent);
		}
		return super.onKeyUp(keyCode, event);
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MagazineSubSubCategoryView.ACTION_DOWN_PIC_CHANGE);
		filter.addAction(MagazineSubSubCategoryView.ACTION_UP_PIC_CHANGE);
		registerReceiver(mReceiver, filter);
	};
		
	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(mReceiver);
	};
	
	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
//			Log.d(TAG, "------------mReceiver-----------");
			int position = intent.getIntExtra("index", MSG_PIC_CHANGE);
			if (action.equals(MagazineSubSubCategoryView.ACTION_DOWN_PIC_CHANGE) || action.equals(MagazineSubSubCategoryView.ACTION_UP_PIC_CHANGE)) {
				Message msg = mHandler.obtainMessage(MSG_PIC_CHANGE);
				msg.obj = position;
				mHandler.sendMessage(msg);
			}
		}
	};
	
	private static final int MSG_PIC_CHANGE = 0;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			if (what == MSG_PIC_CHANGE) {
				int position = (Integer) msg.obj;
				mCurrentNewsPaper = mNewsPapers.get(position);
				if(mCurrentNewsPaper.RootPath != null) {
					String ncxPath = EPUBParser.getNCXPath(mCurrentNewsPaper.RootPath);
					if(ncxPath != null) {
						mCurrentNewsPaper.CataloguePath = ncxPath;
						mCurrentNewsPaper.Pages = EPUBParser.parseNewsPaperPage(mCurrentNewsPaper.RootPath , mCurrentNewsPaper.CataloguePath);
						mNewsPaperPages = EPUBParser.parseNewsPaperPagePicSrc(mCurrentNewsPaper.RootPath, mCurrentNewsPaper.Pages);						
					}
				}
				
				initParams();
				Log.d(TAG, "------------KeyEvent.KEYCODE_DPAD_DOWN or KeyEvent.KEYCODE_DPAD_UP-----------");
				initFooterInfo();
				
				mPage2.setImageBitmap(null);
				mPage1.setImageBitmap(null);
				mPage2.bringToFront();
//				Log.d(TAG, "----------mHandler------mCurrentPage.PicPath = " + mCurrentPage.PicPath);
				mImageManager.getMagazineBitmapDrawable(mCurrentPage.PicPath, new ImageCallback() {
					
					@Override
					public void imageLoaded(BitmapDrawable imageDrawable, String viewKey) {
//						Log.d(TAG, "----mHandler------getNewsPaperPage(mPageNumber).path.equals(viewKey) = " + getNewsPaperPage(mPageNumber).path.equals(viewKey));
						if (getNewsPaperPage(mPageNumber).path.equals(viewKey)) {
							mPage2.setImageBitmap(imageDrawable.getBitmap());
							mPage2.bringToFront();
						}
					}
				}, mCurrentPage.path);
			}
		};
	};
}
