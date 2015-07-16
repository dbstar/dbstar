package com.dbstar.multiple.media.shelf.activity;

import java.util.ArrayList;
import java.util.Iterator;
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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dbstar.multiple.media.common.ImageManager;
import com.dbstar.multiple.media.common.ShelfController;
import com.dbstar.multiple.media.data.HistoryInfo;
import com.dbstar.multiple.media.data.NewsPaperCategory;
import com.dbstar.multiple.media.gallery.FancyCoverFlow;
import com.dbstar.multiple.media.gallery.ImageAdapter;
import com.dbstar.multiple.media.model.FilmInfo;
import com.dbstar.multiple.media.shelf.R;
import com.dbstar.multiple.media.util.ImageUtil;
import com.dbstar.multiple.media.widget.NewsPaperMainCategoryView;
import com.iflytek.tts.TtsService.Tts;

public class MagazineActivity extends Activity {
	private static final String TAG = "MagazineActivity";
	private String mRootId;
	public int mSoundVolume;
	public boolean isMute;
	private Bitmap mBitmap;

	private static final int PERIODCIAL_LOAD_CATETORY_FININSH = 0x20001;
	private NewsPaperMainCategoryView mMainCategoryView;
    private FancyCoverFlow mGalleryView;
    private TextView mFooterMainCategoryName, mFooterSubCategoryName;
    
    private ImageManager mImageManager;
    private List<NewsPaperCategory> mMainCategoryData;
    private ShelfController mController;
    private HistoryInfo mHistoryInfo;
    private ImageAdapter mImageAdapter;
    private View mNoticeView;
    
    private NewsPaperCategory mCurrentMainCategory, mCurrentSubCategory;
	private String mAppUri;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.periodcial_main_fragment_view);
		
		RelativeLayout mContainer = (RelativeLayout) findViewById(R.id.periodcial_main_fragment_container);

		mAppUri = getIntent().getStringExtra(ImageUtil.AppBG_Uri);
		mBitmap = ImageUtil.getBitmap(mAppUri);
		if (mBitmap == null) {
			mContainer.setBackgroundResource(R.drawable.reader_view_background);
		} else {
			Drawable drawable = new BitmapDrawable(mBitmap);
			mContainer.setBackgroundDrawable(drawable);
		}

		mRootId = getIntent().getStringExtra("Id");
		Log.d(TAG, "-----mRootId = " + mRootId);
		
		initViews();
		loadMainCategoryData();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter();
		filter.addAction(FancyCoverFlow.ACTION_GALLERY_PIC_LEFT_CHANGE);
		filter.addAction(FancyCoverFlow.ACTION_GALLERY_PIC_RIGHT_CHANGE);
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
			int position = intent.getIntExtra("position", 0);
			if (action.equals(FancyCoverFlow.ACTION_GALLERY_PIC_LEFT_CHANGE) || action.equals(FancyCoverFlow.ACTION_GALLERY_PIC_RIGHT_CHANGE)) {				
//				Log.d(TAG, "===============itemId = " + position);
				if (mCurrentMainCategory != null && mCurrentMainCategory.SubCategroys != null
						&& mCurrentMainCategory.SubCategroys.size() > position) {
					mCurrentSubCategory = mCurrentMainCategory.SubCategroys.get(position);
				}
				initFooterInfo();
			}
		}
	};
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			int position = mGalleryView.getSelectedItemPosition();
//			Log.d(TAG, "===============itemId = " + position);
			if (mCurrentMainCategory != null && mCurrentMainCategory.SubCategroys != null
					&& mCurrentMainCategory.SubCategroys.size() > position) {
				mCurrentSubCategory = mCurrentMainCategory.SubCategroys.get(position);
			}
			initFooterInfo();
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			mMainCategoryView.onKeyDown(keyCode, event);
			mGalleryView.requestFocus();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			mMainCategoryView.onKeyUp(keyCode, event);
			initFooterInfo();
			mGalleryView.requestFocus();
		}
		return super.onKeyUp(keyCode, event);
	}

	private void initViews() {
		mController = ShelfController.getInstance(this);
		mImageManager = ImageManager.getInstance(this);
    	mHistoryInfo = mController.getNewsPaperHistoryInfo();
		
    	mMainCategoryView = (NewsPaperMainCategoryView) findViewById(R.id.periodcial_main_fragment_category);
		mFooterMainCategoryName = (TextView) findViewById(R.id.periodcial_footer_main_category);
		mFooterSubCategoryName = (TextView) findViewById(R.id.periodcial_footer_sub_category);
		mGalleryView = (FancyCoverFlow) findViewById(R.id.periodcial_main_fragment_view_group);
		mNoticeView = findViewById(R.id.periodcial_main_fragment_shwoNoticeView);

		mGalleryView.setSpacing(-72); 
		mGalleryView.requestFocus();
		mGalleryView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO：点击进入到相应的subcategory页面
				Intent intent = new Intent();
				intent.putExtra(ImageUtil.AppBG_Uri, mAppUri);
				intent.putExtra("mainCategoryName", mCurrentMainCategory.Name);
				intent.putExtra("subCategoryName", mCurrentSubCategory.Name);				
				intent.putExtra("setId", mCurrentSubCategory.Id);
				intent.setClass(MagazineActivity.this, MagazineListActivity.class);
				startActivity(intent);
			}
			
		});
		
		mMainCategoryView.setOnItemSelectedListener(new NewsPaperMainCategoryView.OnItemSelectedListener() {
			
			@Override
			public void onSelected(View v, NewsPaperCategory category) {
				mCurrentMainCategory = category;
				if (mHistoryInfo != null && mCurrentMainCategory != null) {
					mCurrentSubCategory = mCurrentMainCategory.SubCategroys.get(getWillSelectedIndex(mHistoryInfo.SubCategoryId, mCurrentMainCategory.SubCategroys));					
				} else {
					if (mCurrentMainCategory != null && mCurrentMainCategory.SubCategroys != null && mCurrentMainCategory.SubCategroys.size() > 0) {
						mCurrentSubCategory = mCurrentMainCategory.SubCategroys.get(0);
					}					
				}
				showSubCategorysImage(category);
				initFooterInfo();				
			}

		});
	}
	
	private void showSubCategorysImage(NewsPaperCategory category) {
		List<FilmInfo> filmList = getImageUriPath(category);
		if (mImageAdapter != null) {
			mImageAdapter.refresh(filmList);
			mGalleryView.invalidate();			
		} else {
			mImageAdapter = new ImageAdapter(this, filmList);
			mGalleryView.setAdapter(mImageAdapter);
		}
//		Log.d(TAG, "-==================mHistoryInfo.SubCategoryId = " + mHistoryInfo.SubCategoryId);
//		Log.d(TAG, "-==================mCurrentMainCategory.SubCategroys = " + mCurrentMainCategory.SubCategroys);
//		Log.d(TAG, "-==================getWillSelectedIndex(mHistoryInfo.SubCategoryId, mCurrentMainCategory.SubCategroys) = "
//				+ getWillSelectedIndex(mHistoryInfo.SubCategoryId, mCurrentMainCategory.SubCategroys));
		if (mHistoryInfo != null && mCurrentMainCategory != null) {			
			mGalleryView.setSelection(getWillSelectedIndex(mHistoryInfo.SubCategoryId, mCurrentMainCategory.SubCategroys));
		} else {
			mGalleryView.setSelection(0);			
		}
		if (filmList != null && filmList.size() > 0) {
    		mNoticeView.setVisibility(View.GONE);
    	} else {
    		mNoticeView.setVisibility(View.VISIBLE);    		
    	}
	}
	
	private List<FilmInfo> getImageUriPath(NewsPaperCategory category) {
		List<FilmInfo> imagePath = new ArrayList<FilmInfo>();
		if (category != null) {
			List<NewsPaperCategory> subCategroys = category.SubCategroys;
			if (subCategroys != null && subCategroys.size() > 0) {
				for (NewsPaperCategory subCategory : subCategroys) {
//					Log.d(TAG, "-==================subCategory.Name = " + subCategory.Name);
//					Log.d(TAG, "-==================subCategory.FocusedIcon = " + subCategory.unFocusedIcon);
					FilmInfo filmInfo = new FilmInfo(subCategory.Name, subCategory.unFocusedIcon, null);
					imagePath.add(filmInfo);
				}
			}
		}
		return imagePath;
	}

    private void showMainCategory() {
        if (mHistoryInfo != null) {
            mMainCategoryView.setSelection(getWillSelectedIndex(mHistoryInfo.MainCategoryId, mMainCategoryData));
        }
        mMainCategoryView.setData(mMainCategoryData);
        mMainCategoryView.notifyDataChanged();
    }

    private int getWillSelectedIndex(String id, List<NewsPaperCategory> list) {
    	if (id == null || list == null)
            return 0;
        NewsPaperCategory category;
        for (int i = 0, count = list.size(); i < count; i++) {
            category = list.get(i);
            if (id.equals(category.Id)) {
                return i;
            }
        }
        return 0;
	}
    
    private void initFooterInfo(){    
        mFooterMainCategoryName.setText(mCurrentMainCategory.Name);
//        Log.d(TAG, "-==================mCurrentSubCategory.Name = " + mCurrentSubCategory.Name);
        mFooterSubCategoryName.setText(mCurrentSubCategory.Name);
    }
    
    @Override
    public void onDestroy() {
    	if (Tts.isInitialized()) {
			Tts.JniDestory();
		}
    	mHistoryInfo =new HistoryInfo();
    	if(mCurrentMainCategory != null){
    		mHistoryInfo.MainCategoryId = mCurrentMainCategory.Id;
    		mHistoryInfo.SubCategoryId = mCurrentSubCategory.Id;
//            if (mCurrentSubCategory != null && mCurrentSubCategory.CurrentNewsPaper != null) {            	
//            	mHistoryInfo.NewsPaperId = mCurrentSubCategory.CurrentNewsPaper.Id;
//            }
    		mController.saveNewsPaperHistoryInfo(mHistoryInfo);
    	}
    	mController.destroy();
        super.onDestroy();
        mImageManager.destroy();
        
        if (mBitmap != null && !mBitmap.isRecycled()) {
        	mBitmap.recycle();
        	mBitmap = null;
        }
        System.gc();
    }

	private void loadMainCategoryData() {
        new AsyncTask<Object, Integer, String>() {
            
            @Override
            protected String doInBackground(Object... params) {
                String rootId = mRootId;
                if(rootId == null)
                    return null;
     
                   mMainCategoryData = mController.loadAllNewsPaperCategorys(rootId);
                if (mMainCategoryData != null) {
                    Iterator<NewsPaperCategory> iterator = mMainCategoryData.iterator();
                    NewsPaperCategory category;
                    while (iterator.hasNext()) {
                        category = iterator.next();
                        if (category.SubCategroys == null || category.SubCategroys.isEmpty())
                            iterator.remove();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
            	if (mHistoryInfo != null) {            		
            		mCurrentMainCategory = mMainCategoryData.get(getWillSelectedIndex(mHistoryInfo.MainCategoryId, mMainCategoryData));
            	} else {
            		mCurrentMainCategory = mMainCategoryData.get(0);            		
            	}
            	mHandler.sendEmptyMessage(PERIODCIAL_LOAD_CATETORY_FININSH);
            }
        }.execute("");
    }
	
	private Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {

            int what = msg.what;
            if (PERIODCIAL_LOAD_CATETORY_FININSH == what) {
                showMainCategory();
                showSubCategorysImage(mCurrentMainCategory);
            } 
//            else if (LOAD_NEWSPAPER_FININSH == what) {
//                showNewsPaperInfo();
//            } else if (LOAD_EDITION_CATALOGU_FININSH == what) {
//                mLog.d("onPostExecute" + (mCurrentSubCategory.CurrentNewsPaper == null));
//                showEditionList();
//            }else if(PREPARE_LOAD_NEWSPAPER_PAGE_PIC == what){
//               // loadEditionPic((String)msg.obj);
//            }
        };
    };
	
}
