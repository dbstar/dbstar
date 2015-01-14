package com.media.reader.dialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.media.android.dbstarplayer.DbStarPlayer;
import com.media.android.dbstarplayer.R;
import com.media.android.util.UIUtil;
import com.media.dbstarplayer.book.Book;
import com.media.dbstarplayer.book.Bookmark;
import com.media.dbstarplayer.book.BookmarkQuery;
import com.media.dbstarplayer.bookmodel.TOCTree;
import com.media.dbstarplayer.dbstarplayer.ActionCode;
import com.media.dbstarplayer.dbstarplayer.DbStarPlayerApp;
import com.media.reader.view.CatalogAdapter;
import com.media.reader.view.CustomBookMarksAdapter;
import com.media.reader.view.VerticalSeekBar;
import com.media.reader.vo.TocReference;
import com.media.zlibrary.core.application.ZLApplication;
import com.media.zlibrary.text.view.style.ZLTextBaseStyle;
import com.media.zlibrary.text.view.style.ZLTextStyleCollection;

public class CustomDialog extends DialogBaseLayout implements
	OnClickListener, OnFocusChangeListener{

	private final String TAG = getClass().getSimpleName();
	private RelativeLayout mDlgRoot = null;
	private DbStarPlayer mActivity = null;
	private DbStarPlayerApp mReader = null;
	private LinearLayout mToolBarLayout = null;
	private Button mAddBookmark = null;
	private Button mDeleteBookmark = null;
	private Button mFindToc = null;
	private Button mFindBookmark = null;
	private Button mFontAdjust = null;
	private int mScreenWidth;
	private ListView mListToc = null;
//	private CustomTocAdapter mTocAdapter = null;
	
	private CatalogAdapter mTocAdapter = null;
	ArrayList<TocReference> mTocList = new ArrayList<TocReference>();
	private ListView mListBookmark = null;
	private CustomBookMarksAdapter mBookMarksAdapter = null;
	
	private LinearLayout mFontAdjustLayout = null;
	private VerticalSeekBar mFontAdjustSeekBar = null;
	private int mSeekBarProgress = -1;
	/**
	 * Sensitivity control of font adjust seek bar
	 * */
	private final int SEEK_SENSITIVITY = 2;
	
	private final int SEEKBAR_PROGRESS_STEP = 3; 
	public CustomDialog(DbStarPlayer activity, DbStarPlayerApp reader) {
		super(activity);
		mActivity = activity;
		mReader = reader;
		mDlgRoot = (RelativeLayout) mInflater.inflate(
				R.layout.reader_custom_dialog, this, false);
		addView(mDlgRoot);
		findViews();
		initViews();
	}

	private void findViews(){
		mToolBarLayout = (LinearLayout) findViewById(R.id.reader_toolbar_layout);
		mAddBookmark = (Button) findViewById(R.id.reader_add_bookmark);
		mDeleteBookmark = (Button) findViewById(R.id.reader_delete_bookmark);
		mFindToc = (Button) findViewById(R.id.reader_find_toc);
		mFindBookmark = (Button) findViewById(R.id.reader_find_bookmark);
		mFontAdjust = (Button) findViewById(R.id.reader_font_adjust);
		mListToc = (ListView) findViewById(R.id.list_toc);
		mListBookmark = (ListView) findViewById(R.id.list_bookmark);
		
		mFontAdjustLayout = (LinearLayout) findViewById(R.id.font_adjust_layout);
		mFontAdjustSeekBar = (VerticalSeekBar) findViewById(R.id.seekbar_font_adjust);
		
	
	}
	
	private void initViews(){
		initBookMarkOpertion();
		mFindToc.setOnClickListener(this);
		mFindBookmark.setOnClickListener(this);
		mFontAdjust.setOnClickListener(this);
		//mAddBookmark.setOnFocusChangeListener(this);
		mFindToc.setOnFocusChangeListener(this);
		mFindBookmark.setOnFocusChangeListener(this);
		mFontAdjust.setOnFocusChangeListener(this);
		
		mListToc.setOnKeyListener(new OnKeyListener() {
            
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN){
                    
                    switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        if(mListToc.getSelectedItemPosition() <mTocAdapter.getCount()-1){
                            return false;
                        }else{
                            return true;
                        }
                    case KeyEvent.KEYCODE_DPAD_UP:
                        if(mListToc.getSelectedItemPosition()  > 0){
                            return false;
                        }else{
                            return true;
                        }
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        return false;
                    }
                }
                return false;
            }
        });

        mListBookmark.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        if (mListBookmark.getSelectedItemPosition() < mBookMarksAdapter.getCount() - 1) {
                            return false;
                        } else {
                            return true;
                        }
                    case KeyEvent.KEYCODE_DPAD_UP:
                        if (mListBookmark.getSelectedItemPosition() > 0) {
                            return false;
                        } else {
                            return true;
                        }
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        return false;
                    }
                }
                return false;
            }
        });
        mListBookmark.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				showBookmarkDeleteConfirmDialog(position);
				return false;
			}
		});
        
    	mFontAdjustSeekBar.setOnKeyListener(new OnKeyListener() {
                
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if(event.getAction() == KeyEvent.ACTION_DOWN){
                        switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_DOWN:
                            changeProgress(false);
                            return true;
                        case KeyEvent.KEYCODE_DPAD_UP:
                            changeProgress(true);   
                            return true;    
                        }
                    }
                    return false;
                }
            });
	}
	
	private void showBookmarkDeleteConfirmDialog(final int pos){
		new AlertDialog.Builder(mActivity)
		.setTitle(R.string.label_toast)
		.setMessage(R.string.tip_bookmark_delete)
		.setPositiveButton(R.string.label_confirm, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				final Bookmark bookmark = mBookMarksAdapter.getItem(pos);
				mReader.Collection.deleteBookmark(bookmark);
				mBookMarksAdapter.remove(bookmark);
				mBookMarksAdapter.notifyDataSetChanged();
				dialog.dismiss();
			}
		})
		.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		}).create().show();
		
	}
	
	private void initTocList(TOCTree root){
		if(root!=null){
			for(TOCTree tree:root.subTrees()){
				mTocList.add(new TocReference(tree.getText(),tree.getReference()));
				initTocList(tree);
			}
		}
	}
	
	private void showTocList(View v){
	    setViewLoacation(v, mListToc);
		mListToc.setVisibility(View.VISIBLE);
		final DbStarPlayerApp dbstarplayer = (DbStarPlayerApp)ZLApplication.Instance();
		if(dbstarplayer.Model!=null&&dbstarplayer.Model.TOCTree!=null){
			if(mTocList!=null&&mTocList.size()<=0){
				initTocList(dbstarplayer.Model.TOCTree);
				Collections.sort(mTocList,new Comparator<TocReference>() {
					@Override
					public int compare(TocReference lhs, TocReference rhs) {
						// TODO Auto-generated method stub
						return lhs.mRef.ParagraphIndex-rhs.mRef.ParagraphIndex;
					}
				});
			}
			
			if(null==mTocAdapter){
				mTocAdapter = new CatalogAdapter(mListToc,mTocList);
			}
			mListToc.setAdapter(mTocAdapter);
			mListToc.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					// TODO Auto-generated method stub
					int tmpIndex = mTocList.get(position).mRef.ParagraphIndex;
					final DbStarPlayerApp fbreader = (DbStarPlayerApp) ZLApplication.Instance();
					if(tmpIndex>0){
						fbreader.BookTextView.gotoPosition(tmpIndex, 0, 0);
						fbreader.showBookTextView();
						mListToc.setVisibility(View.GONE);
					}else{
						/**
						 * find the last chapter with content
						 * */
						for(int i=position-1;i>=0;i--){
							tmpIndex = mTocList.get(i).mRef.ParagraphIndex;
							if(tmpIndex>0){
								break;
							}
						}
						if(tmpIndex>0){
							fbreader.BookTextView.gotoPosition(tmpIndex, 0, 0);
							fbreader.showBookTextView();
							mListToc.setVisibility(View.GONE);
						}
					}
					close();
				}
			});			
		}
		
		mListToc.post(new Runnable() {
            
            @Override
            public void run() {
                mListToc.requestFocus();
                if(mTocAdapter.getCount() > 0)
                    mListToc.setSelection(0);
            }
        });
		
	}
	
	private void setViewLoacation(View v,View v2){
	    int[] location = new int[2];
        v.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = v.getMeasuredWidth() + left;
        int middle = left + (right - left)/2;
        int halfDistance = v2.getLayoutParams().width;
        int mrginLeft = middle - halfDistance/2;
        android.widget.RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) v2.getLayoutParams();
        params.setMargins(mrginLeft, params.topMargin, mScreenWidth - mrginLeft - v2.getLayoutParams().width, params.bottomMargin);
        v2.setLayoutParams(params);
        
	}
	private void hideTocList(){
		mListToc.setVisibility(View.GONE);
	}
	
	private void showBookMarkList(View v){
	    setViewLoacation(v, mListBookmark);
		mListBookmark.setVisibility(View.VISIBLE);
		if(mBookMarksAdapter==null){
			mBookMarksAdapter = new CustomBookMarksAdapter(mActivity,mListBookmark);
		}
		mListBookmark.setAdapter(mBookMarksAdapter);
		mListBookmark.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				final Bookmark bookmark = mBookMarksAdapter.getItem(position);
				if (bookmark != null) {
					gotoBookmark(bookmark);
					mListBookmark.setVisibility(View.GONE);
					close();
				}
			}
		});
		new Thread(new Initializer()).start();
	}
	
	private void gotoBookmark(Bookmark bookmark) {
		bookmark.markAsAccessed();
		((DbStarPlayerApp)DbStarPlayerApp.Instance()).Collection.saveBookmark(bookmark);
		final Book book = ((DbStarPlayerApp)DbStarPlayerApp.Instance()).Collection.
				getBookById(bookmark.getBookId());
		if (book != null) {
//			DbStarPlayer.openBookActivity(mActivity, book, bookmark);
			((DbStarPlayerApp)DbStarPlayerApp.Instance()).gotoBookmark(bookmark);
		} else {
			UIUtil.showErrorMessage(mActivity, "cannotOpenBook");
		}
	}
	
	private void hideBookMarkList(){
		mListBookmark.setVisibility(View.GONE);
	}
	
	private void initFontSeekProgress(){
		mFontAdjustSeekBar.setMax(ZLTextBaseStyle.CONTENT_TEXT_MAXSIZE-
				ZLTextBaseStyle.CONTENT_TEXT_MINSIZE);
		mFontAdjustSeekBar.setProgress(ZLTextStyleCollection.Instance().
				getBaseStyle().FontSizeOption.getValue()-ZLTextBaseStyle.CONTENT_TEXT_MINSIZE);
		mSeekBarProgress = mFontAdjustSeekBar.getProgress();
	}
	
	private void showFontAdjustView(View v){
	    setViewLoacation(v, mFontAdjustLayout);
		mFontAdjustLayout.setVisibility(View.VISIBLE);
		initFontSeekProgress();
		mFontAdjustLayout.post(new Runnable() {
            
            @Override
            public void run() {
                mFontAdjustSeekBar.setFocusableInTouchMode(true);
                mFontAdjustSeekBar.setFocusable(true);
                mFontAdjustSeekBar.requestFocus();
            }
        });
		mFontAdjustSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser) {
                //doProgressChange(seekBar, progress,fromUser);
            }
        });
	}
	
	/**
	 * @param seekBar
	 * @param progress
	 * @param fromUser
	 */
	private void doProgressChange(SeekBar seekBar, int progress,
			boolean fromUser){
		progress=Math.max(0, progress);
		progress=Math.min(seekBar.getMax(), progress);
		if (mSeekBarProgress == -1||
				Math.abs(mSeekBarProgress - progress) >= SEEK_SENSITIVITY
				) {
			if(progress>mSeekBarProgress){
				mReader.updateFont(ActionCode.INCREASE_FONT);
			}else{
				mReader.updateFont(ActionCode.DECREASE_FONT);
			}
			mSeekBarProgress = progress;
			seekBar.setProgress(mSeekBarProgress);
		}
	}
	
	/**
	 * @param isIncrease是否为增
	 */
	protected void changeProgress(boolean isIncrease) {
		if(mFontAdjustSeekBar!=null)
		{    
			int progress;
			if(isIncrease){
				progress=mSeekBarProgress+SEEKBAR_PROGRESS_STEP;
			}else{
				progress=mSeekBarProgress-SEEKBAR_PROGRESS_STEP;
			}
			doProgressChange(mFontAdjustSeekBar, progress, true);
		}
		
	}
	
	private void hideFontAdjustView(){
		mFontAdjustLayout.setVisibility(View.GONE);
	}
	
	private class Initializer implements Runnable {
		public void run() {
			if (mReader.getCurBook() != null) {
				for (BookmarkQuery query = new BookmarkQuery(mReader.getCurBook(), 20); ; query = query.next()) {
					final List<Bookmark> thisBookBookmarks = mReader.Collection.bookmarks(query);
					if (thisBookBookmarks.isEmpty()) {
						break;
					}
					mBookMarksAdapter.clear();
					mBookMarksAdapter.addAll(thisBookBookmarks);
				}
			}
			mActivity.runOnUiThread(new Runnable() {
				public void run() {
					mActivity.setProgressBarIndeterminateVisibility(false);
				}
			});
		}
	}
	
	private void initBookMarkOpertion(){
		if(ZLApplication.Instance().getCurrentView().getCurBookMark()!=null){
			initDeleteBookMark();
		}else{
			initAddBookMark();
		}
	}
	
	private void initAddBookMark(){
		mAddBookmark.setVisibility(View.VISIBLE);
		mDeleteBookmark.setVisibility(View.GONE);
		mAddBookmark.setOnClickListener(this);
		mAddBookmark.setOnFocusChangeListener(this);
	}
	
	private void initDeleteBookMark(){
		mAddBookmark.setVisibility(View.GONE);
		mDeleteBookmark.setVisibility(View.VISIBLE);
		mDeleteBookmark.setOnClickListener(this);
		mDeleteBookmark.setOnFocusChangeListener(this);
	}
	
	@Override
	public void doShow() {
		initBookMarkOpertion();
		final Rect frame = new Rect();
		mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		// 根据不同的设备设置适合尺寸，防止被状态栏覆盖
		mScreenWidth = frame.right - frame.left;
		WindowManager.LayoutParams wlp = new WindowManager.LayoutParams(
				frame.right-frame.left, frame.bottom - frame.top,
				WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG,
				WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
				PixelFormat.TRANSLUCENT);
		wlp.gravity = Gravity.TOP;
		wlp.y = frame.top;
		wlp.dimAmount = 0.5f;
		try{
			mWindowManager.addView(this, wlp);
		}catch(Exception e){
			
		}
		
		post(new Runnable() {
            
            @Override
            public void run() {
                View view = null;
                  if(mAddBookmark.isShown()){
                      view = mAddBookmark;
                  }else if(mDeleteBookmark.isShown()){
                      view = mDeleteBookmark;
                  }
                  if(view != null){
                      view.setFocusableInTouchMode(true);
                      view.setFocusable(true);
                      view.requestFocus();
                  }
            }
        });
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
				close();
			}
		}
		return super.dispatchKeyEvent(event);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		switch(event.getAction()&MotionEvent.ACTION_MASK){
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			close();
			break;
		}
		return super.onTouchEvent(event);
	}

	private void refreshReader(){
		mReader.getViewWidget().reset();
		mReader.getViewWidget().repaint();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.reader_add_bookmark:{
					mActivity.addCurBookMark();
					initDeleteBookMark();
					refreshReader();
					close();
					// 添加书签后，让弹出框也消失，是为了与“删除书签”的操作保持一致。
					super.close();
				}
				break;
			case R.id.reader_delete_bookmark:{
					Bookmark bookmark = ZLApplication.Instance().getCurrentView().getCurBookMark();
					mActivity.deleteBookMark(bookmark);
					if(mBookMarksAdapter!=null){
						mBookMarksAdapter.remove(bookmark);
						mBookMarksAdapter.notifyDataSetChanged();
					}
					initAddBookMark();
					refreshReader();
					close();
				}
				break;
			case R.id.reader_find_toc:{
					hideBookMarkList();
					hideFontAdjustView();
					if(mListToc.isShown()){
						hideTocList();
					}else{
						showTocList(v);
					}
				}
				break;
			case R.id.reader_find_bookmark:	{
					hideTocList();
					hideFontAdjustView();
					if(mListBookmark.isShown()){
						hideBookMarkList();
					}else{
						showBookMarkList(v);
					}
				}
				break;
			case R.id.reader_font_adjust:{
					hideBookMarkList();
					hideTocList();
					if(mFontAdjustLayout.isShown()){
						hideFontAdjustView();
					}else{
						showFontAdjustView(v);
					}
				}
				break;
		}
	}
	
	@Override
	public void close() {
		if (!this.isShown()) {
			return;
		}
		Log.d(TAG, "----------mListToc.isShown() = " + mListToc.isShown() + ", mListBookmark.isShown() = " + mListBookmark.isShown());
		Log.d(TAG, "----------mFontAdjustLayout.isShown() = " + mFontAdjustLayout.isShown() + ", mToolBarLayout.isShown() = " +  mToolBarLayout.isShown());
		if(mListToc.isShown()){
			hideTocList();
		}else if(mListBookmark.isShown()){
			hideBookMarkList();
		}else if(mFontAdjustLayout.isShown()){
			hideFontAdjustView();
		}else if(mToolBarLayout.isShown()){
			super.close();
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.reader_add_bookmark:{
			    hideTocList();
				}
				break;
			case R.id.reader_delete_bookmark:{
			    hideTocList();
			}
			    break;
			case R.id.reader_find_toc:{
			    if(hasFocus){
    			    hideBookMarkList();
                    hideFontAdjustView();
                    if(mListToc.isShown()){
                        hideTocList();
                    }else{
                        showTocList(v);
                    }
			    }
				}
				break;
			case R.id.reader_find_bookmark:	{
			    if(hasFocus){
    			    hideTocList();
                    hideFontAdjustView();
                    if(mListBookmark.isShown()){
                        hideBookMarkList();
                    }else{
                        showBookMarkList(v);
                    }
			    }
				}
				break;
			case R.id.reader_font_adjust:{
			    if(hasFocus){
    			    hideBookMarkList();
                    hideTocList();
                    if(mFontAdjustLayout.isShown()){
                        hideFontAdjustView();
                    }else{
                        showFontAdjustView(v);
                    }
			    }
				}
				break;
		}
	}

}
