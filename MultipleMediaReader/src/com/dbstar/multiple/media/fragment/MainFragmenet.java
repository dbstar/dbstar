package com.dbstar.multiple.media.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dbstar.multiple.media.adapter.NewsPaperPageAdapter;
import com.dbstar.multiple.media.common.ImageManager;
import com.dbstar.multiple.media.common.ShelfController;
import com.dbstar.multiple.media.data.HistoryInfo;
import com.dbstar.multiple.media.data.NewsPaper;
import com.dbstar.multiple.media.data.NewsPaperCategory;
import com.dbstar.multiple.media.data.NewsPaperPage;
import com.dbstar.multiple.media.shelf.R;
import com.dbstar.multiple.media.shelf.activity.NewsPaperActivity;
import com.dbstar.multiple.media.util.DateUtil;
import com.dbstar.multiple.media.util.EPUBParser;
import com.dbstar.multiple.media.widget.CustomListView;
import com.dbstar.multiple.media.widget.NewsPaperMainCategoryView;
import com.dbstar.multiple.media.widget.NewsPaperSubCategoryView;
import com.dbstar.multiple.media.widget.NewspaperDateView;
import com.dbstar.multiple.media.widget.NewspaperDateView.OnDateViewSelectedListener;

public class MainFragmenet extends BaseFragment {
    
    private static final String TAG = "MainFragmenet";
    
    private static final int LOAD_CATETORY_FININSH = 0x10001;
    private static final int LOAD_NEWSPAPER_FININSH = 0x10002;
    private static final int LOAD_EDITION_CATALOGU_FININSH = 0x10003;
    private static final int PREPARE_LOAD_NEWSPAPER_PAGE_PIC = 0x10004;
    private static final String OPERATION_TYPE_PREFERENCE = "Preference"; 
    private static final String OPERATION_TYPE_COLLETION = "Collection"; 
    private NewsPaperMainCategoryView mMainCategoryView;
    private NewsPaperSubCategoryView mSubCategoryView;
    private CustomListView mPageListView;
    private ImageView mPagePicView;
    private TextView mCurrentPaperTitle, mCurrentPaperPageCount, mCurrentPaperPublishDate;
    private RelativeLayout mPagePicLoadingView,mCategoryLoadingView;
    private TextView mFooterMainCategoryName;
    private TextView mFooterSubCategoryName;
    private TextView mOperationView;
    private NewspaperDateView mDateViewGroup;
    private List<NewsPaperCategory> mMainCategoryData;
    private List<NewsPaper> mNewsPapers;
    private ShelfController mController;
    private HistoryInfo mHistoryInfo;

    private int mLastFocusedDateId;
    private boolean isCollectionType;
    private NewsPaperCategory mCurrentMainCategroy, mCurrentSubCategory;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mController = ShelfController.getInstance(mActivity);
        mHistoryInfo = mController.getNewsPaperHistoryInfo();
        isCollectionType = mActivity.isCollectionType();
        mLog.i("onCreate");
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mLog.i("onCreateView");
        View view = inflater.inflate(R.layout.newspaper_main_fragment_view, null);
        initView(view);
        loadMainCategoryData();
        return view;
    }
    
    private void initView(View view) {
        mMainCategoryView = (NewsPaperMainCategoryView) view.findViewById(R.id.category1);
        mSubCategoryView = (NewsPaperSubCategoryView) view.findViewById(R.id.category2);
        mPageListView = (CustomListView) view.findViewById(R.id.pagelist);
        mPagePicView = (ImageView) view.findViewById(R.id.newspaper_pic);

        mCurrentPaperTitle = (TextView) view.findViewById(R.id.current_newspaper_title);
        mCurrentPaperPageCount = (TextView) view.findViewById(R.id.total_page);
        mCurrentPaperPublishDate = (TextView) view.findViewById(R.id.release_date);

        mPagePicLoadingView = (RelativeLayout) view.findViewById(R.id.newspaper_pic_loading_bar);
        
        mFooterMainCategoryName = (TextView) view.findViewById(R.id.footer_main_category);
        mFooterSubCategoryName = (TextView) view.findViewById(R.id.footer_sub_category);
        mCategoryLoadingView = (RelativeLayout) view.findViewById(R.id.newspaper_category_loading_view);
        mOperationView = (TextView) view.findViewById(R.id.operationview);
        mDateViewGroup = (NewspaperDateView) view.findViewById(R.id.newspaper_date_group);
        

        mPagePicView.setVisibility(View.INVISIBLE);
        mPagePicLoadingView.setVisibility(View.INVISIBLE);


        mCurrentPaperTitle.setText("");
        mCurrentPaperPageCount.setText("");
        mCurrentPaperPublishDate.setText("");

        
        mSubCategoryView.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                int action = event.getAction();

                if (action == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        if (mLastFocusedDateId != 0)
                            mSubCategoryView.setNextFocusRightId(mLastFocusedDateId);
                    }
                }
                return false;
            }
        });

        mPageListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCurrentSubCategory.CurrentNewsPaper.SelectedIndex = position;
                if (view != null) {
                    mCurrentSubCategory.CurrentNewsPaper.PaddinTop = view.getTop();
                }
                NewsPaperPageAdapter adapter = (NewsPaperPageAdapter) mPageListView.getAdapter();
                NewsPaperPage page = adapter.getEdition(position);
                adapter.setMarkPosition(position);
                page.IsOpen = 1;
                if (view != null) {
                    TextView textView = (TextView) view.findViewById(R.id.category_name);
                    textView.setTextColor(Color.RED);
                    textView.setTag("#FFFF0000");
                }
                mActivity.switchFragment(page,NewsPaperActivity.FRAGMENT_ARTICLE_LIST);
                View v;
                for(int i = 0,count = mPageListView.getChildCount();i  < count ; i ++){
                    v = mPageListView.getChildAt(i);
                    if(v == view){
                        v.findViewById(R.id.mark).setVisibility(View.VISIBLE);
                    }else{
                        v.findViewById(R.id.mark).setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
        mPageListView.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                
            
                NewsPaperPageAdapter adapter = (NewsPaperPageAdapter) mPageListView.getAdapter();
                NewsPaperPage page = adapter.getEdition(position);
                if (page != null){
                    loadEditionPic(page.PicPath);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mPageListView.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v1, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    View view = null;
                    switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_UP: {
                        if (mPageListView.getSelectedItemPosition() == 0) {
                            return true;
                        }
                        view = mPageListView.getSelectedView();
                        view = mPageListView.getChildAt(mPageListView.indexOfChild(view) - 1);
                        break;
                    }
                    case KeyEvent.KEYCODE_DPAD_DOWN: {
                        view = mPageListView.getSelectedView();
                        view = mPageListView.getChildAt(mPageListView.indexOfChild(view) + 1);
                        break;
                    }
                    case KeyEvent.KEYCODE_DPAD_LEFT: {
                         TextView textView =  (TextView) mPageListView.getSelectedView().findViewById(R.id.category_name);
                         textView.setTextColor(Color.parseColor((String) textView.getTag()));
                        break;
                    }

                    }

                    if (view != null) {
                        TextView textView = (TextView) view.findViewById(R.id.category_name);
                        View v;
                        for (int i = 0, count = mPageListView.getChildCount(); i < count; i++) {
                            v = mPageListView.getChildAt(i);
                            textView = (TextView) v.findViewById(R.id.category_name);
                            if (v == view) {
                                textView.setTextColor(Color.WHITE);
                            } else {
                                textView.setTextColor(Color.parseColor((String) textView.getTag()));
                            }
                        }
                    }
                }
                return false;
            }
        });

        mMainCategoryView.setOnItemSelectedListener(new NewsPaperMainCategoryView.OnItemSelectedListener() {

            @Override
            public void onSelected(View v, NewsPaperCategory category) {
                mCurrentMainCategroy = category;
                showSubCategory(category.SubCategroys, category.CurrentSubCategoryId);
            }
        });

        mSubCategoryView.setOnItemSelectedListener(new NewsPaperSubCategoryView.OnItemSelectedListener() {

            @Override
            public void onSelected(View v, NewsPaperCategory category) {
                mCurrentSubCategory = category;
                mCurrentMainCategroy.CurrentSubCategoryId = category.Id;
                initFooterInfo(false);
                if (category.NewsPapers == null)
                    loadNewsPaper(category);
                else {
                    mNewsPapers = category.NewsPapers;
                    showNewsPaperInfo();
                }
            }
        });
        
        mDateViewGroup.setOnSelectedListener(new OnDateViewSelectedListener() {
            
            @Override
            public void onSelected(NewsPaper paper) {
                    showPagePictureInfo(paper);
                
                if(mDateViewGroup.hasFocus())
                    initFooterInfo(true);
            }
        });
    }

    private void showMainCategory() {
        if (mHistoryInfo != null) {
            mMainCategoryView.setSelection(getWillSelectedIndex(mHistoryInfo.MainCategoryId, mMainCategoryData));
        }
        mMainCategoryView.setData(mMainCategoryData);
        mMainCategoryView.notifyDataChanged();
    }

    private void showSubCategory(List<NewsPaperCategory> data, String slectedSubId) {
        if (data == null || data.isEmpty()) {
            // TODO clear view and notify user there no any categroy
            mSubCategoryView.setData(null);
            mSubCategoryView.notifyDataChanged();
            return;
        }

        if (mHistoryInfo != null) {
            mSubCategoryView.setSelection(getWillSelectedIndex(mHistoryInfo.SubCategoryId, data));
        } else {
            mSubCategoryView.setSelection(getWillSelectedIndex(slectedSubId, data));
        }

        mSubCategoryView.setData(data);
        mSubCategoryView.notifyDataChanged();

    }

    private void loadNewsPaper(NewsPaperCategory categroy) {
        new AsyncTask<NewsPaperCategory, Object, Object>() {
            @Override
            protected Object doInBackground(NewsPaperCategory... params) {
                NewsPaperCategory category = params[0];
                if(isCollectionType){
                    mNewsPapers = mController.loadCollectedNewsPapers(category.Id);
                }else{
                    mNewsPapers = mController.loadNewsPapers(category.Id);
                }
                category.NewsPapers = mNewsPapers;

                return null;
            }

            @Override
            protected void onPostExecute(Object result) {
                sendEmptyMessage(LOAD_NEWSPAPER_FININSH);
            }
        }.execute(categroy);
    }

    private void loadMainCategoryData() {
        new AsyncTask<Object, Integer, String>() {
            
            protected void onPreExecute() {
                mCategoryLoadingView.setVisibility(View.VISIBLE);
            };
            @Override
            protected String doInBackground(Object... params) {
                String rootId = mActivity.getRootId();
                if(rootId == null)
                    return null;
                if(isCollectionType){
                    mMainCategoryData = mController.loadCollectedNewsPaperCategorys(rootId);
                }else{
                    mMainCategoryData = mController.loadAllNewsPaperCategorys(rootId);
                }
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
                mCategoryLoadingView.setVisibility(View.INVISIBLE);
                sendEmptyMessage(LOAD_CATETORY_FININSH);

            }
        }.execute("");
    }

    private Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {

            int what = msg.what;
            if (LOAD_CATETORY_FININSH == what) {
                showMainCategory();
            } else if (LOAD_NEWSPAPER_FININSH == what) {
                showNewsPaperInfo();
            } else if (LOAD_EDITION_CATALOGU_FININSH == what) {
                mLog.d("onPostExecute" + (mCurrentSubCategory.CurrentNewsPaper == null));
                showEditionList();
            }else if(PREPARE_LOAD_NEWSPAPER_PAGE_PIC == what){
               // loadEditionPic((String)msg.obj);
            }
        };
    };

    private void sendEmptyMessage(int what) {
        mHandler.sendEmptyMessage(what);
    }

    protected void showNewsPaperInfo() {
        showNewsPaperDateInfo();

    }

    private void showPagePictureInfo(NewsPaper paper) {
        mLog.d("showPagePictureInfo11" + (mCurrentSubCategory.CurrentNewsPaper == null));
        mCurrentSubCategory.CurrentNewsPaper = paper;
        mLog.d("showPagePictureInfo1" + (mCurrentSubCategory.CurrentNewsPaper == null));
        if (mHistoryInfo != null)
            mHistoryInfo = null;
        if (paper == null) {
            mPagePicView.setVisibility(View.INVISIBLE);
            clearEdtionlist();
            showCurrentPaperInfor(null);
            // TODO prompt user no newspaper
        } else {
            if (paper.Pages == null || paper.Pages.isEmpty()) {
                paserPagesCatalogue(paper);
            } else {
                mLog.d("showPagePictureInfo2" + (mCurrentSubCategory == null));
                showEditionList();
            }
        }
    }
    private void showCurrentPaperInfor(NewsPaper newsPaper){
        if(newsPaper == null){
            mCurrentPaperTitle.setText("");
            mCurrentPaperPageCount.setText("");
            mCurrentPaperPublishDate.setText("");
        }else{
            mCurrentPaperTitle.setText(newsPaper.Name);
            if(newsPaper.Pages != null)
                mCurrentPaperPageCount.setText(getString(R.string.gong) + newsPaper.Pages.size()+getString(R.string.ban));
            mCurrentPaperPublishDate.setText(DateUtil.getStringFromDate(DateUtil.getDate(newsPaper.PublishTime, DateUtil.FORMART3), DateUtil.FORMART2));
            
        }
        
    }
    private void paserPagesCatalogue(NewsPaper paper) {

        new AsyncTask<NewsPaper, String, String>() {

            @Override
            protected String doInBackground(NewsPaper... params) {
                NewsPaper paper = params[0];
                mLog.d("doInBackground" + (mCurrentSubCategory.CurrentNewsPaper == null));
                if(paper.RootPath == null)
                    return null;
                String ncxPath = EPUBParser.getNCXPath(paper.RootPath);
                
                if(ncxPath == null)
                    return null;
                paper.CataloguePath = ncxPath;
                paper.Pages = EPUBParser.parseNewsPaperPage(paper.RootPath , paper.CataloguePath);
                EPUBParser.parseNewsPaperPagePicSrc(paper.RootPath, paper.Pages);
                return null;
            }

            protected void onPostExecute(String result) {
                mLog.d("onPostExecute" + (mCurrentSubCategory.CurrentNewsPaper == null));
                sendEmptyMessage(LOAD_EDITION_CATALOGU_FININSH);
            };
        }.execute(paper);

    }

    private void showEditionList() {
        
        // mLog.i("showEditionList = " + (mCurrentSubCategory == null));
        // mLog.i("showEditionList = " + (mCurrentSubCategory.CurrentNewsPaper == null)) ;
        // mLog.i("showEditionList = " + (mCurrentSubCategory.CurrentNewsPaper.Pages == null)); 
        showCurrentPaperInfor(mCurrentSubCategory.CurrentNewsPaper);
        if (mPageadapter == null) {
            mPageadapter = new NewsPaperPageAdapter(mActivity, "#032783",R.layout.newspaper_page_list_item);
            if(mCurrentSubCategory.CurrentNewsPaper == null ||mCurrentSubCategory.CurrentNewsPaper.Pages == null){
                clearEdtionlist();
                return;
            }
            else
                mPageadapter.setData(mCurrentSubCategory.CurrentNewsPaper.Pages);
            mPageListView.setAdapter(mPageadapter);
        } else {
            if(mCurrentSubCategory.CurrentNewsPaper  == null ||mCurrentSubCategory.CurrentNewsPaper.Pages == null){
                clearEdtionlist();
                return;                
            }
            else
                mPageadapter.setData(mCurrentSubCategory.CurrentNewsPaper.Pages);
            mPageadapter.notifyDataSetChanged();
        }
        int index = mCurrentSubCategory.CurrentNewsPaper.SelectedIndex;
        mPageadapter.setMarkPosition(index);
        mPageListView.setSelectLastIndex(index == -1? 0 : index, mCurrentSubCategory.CurrentNewsPaper.PaddinTop);
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                mPageListView.requestSelectItem();
            }
        });
    }

    private void clearEdtionlist() {
        if (mPageadapter != null){
            mPageadapter.setData(null);
            mPageadapter.notifyDataSetChanged();
            mPagePicLoadingView.setVisibility(View.INVISIBLE);
            mPagePicView.setVisibility(View.INVISIBLE);
        }
    }

    private void loadEditionPic(String filePath) {
        mPagePicLoadingView.setVisibility(View.VISIBLE);
        mLog.i("loadEditionPic"+filePath);
        ImageManager.getInstance(mActivity).getBitmapDrawable(filePath, new ImageManager.ImageCallback() {
            @Override
            public void imageLoaded(BitmapDrawable imageDrawable, String viewKey) {
                mPagePicLoadingView.setVisibility(View.INVISIBLE);
                mPagePicView.setVisibility(View.VISIBLE);
                mPagePicView.setImageBitmap(imageDrawable.getBitmap());
            }
        }, null);

    }

    private void showNewsPaperDateInfo() {
        mNewsPapers = mCurrentSubCategory.NewsPapers;
        mDateViewGroup.setData(mCurrentSubCategory);

    }

    
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            mPageListView.requestFocus();
            showEditionList();
        }
    };
    private NewsPaperPageAdapter mPageadapter;

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
    public void showNextPage(){
        mCurrentSubCategory.CurrentNewsPaper.SelectedIndex ++;
        mLog.i("showNextPage =" +  mCurrentSubCategory.CurrentNewsPaper.SelectedIndex);
        NewsPaperPageAdapter adapter = (NewsPaperPageAdapter) mPageListView.getAdapter();
        if( mCurrentSubCategory.CurrentNewsPaper.SelectedIndex < adapter.getCount()){
            NewsPaperPage page = adapter.getEdition(mCurrentSubCategory.CurrentNewsPaper.SelectedIndex);
            adapter.setMarkPosition(  mCurrentSubCategory.CurrentNewsPaper.SelectedIndex);
            page.IsOpen = 1;
            mActivity.changeArticleTitleListFragmentData(page);
        }
    }
    public String getFooterInfo(){
        NewsPaperPage page = getEdition(  mCurrentSubCategory.CurrentNewsPaper.SelectedIndex );
        if(page != null)
         return  mCurrentMainCategroy.Name + "#" + mCurrentSubCategory.Name + "#" + page.title;
        return null;
    }   
    
    public NewsPaperPage getEdition(int index){
        NewsPaperPageAdapter adapter = (NewsPaperPageAdapter) mPageListView.getAdapter();
        if(adapter != null && index < adapter.getCount()){
            return adapter.getEdition(index);
        }
        return null;
    }
    
    private void initFooterInfo(boolean isCollection){
        
        mFooterMainCategoryName.setText(mCurrentMainCategroy.Name);
        mFooterSubCategoryName.setText(mCurrentSubCategory.Name);
        if(isCollection){
            if("1".equals(mCurrentSubCategory.CurrentNewsPaper.Favarite)){
                mOperationView.setText(R.string.cancel_collection);
                
            }else{
                mOperationView.setText(R.string.collect_newspaper);
            }
            mOperationView.setTag(OPERATION_TYPE_COLLETION);
        }else{
            if("1".equals(mCurrentSubCategory.Preference))
                mOperationView.setText(R.string.remove_newspaper_from_personal_preference);
            else{
                mOperationView.setText(R.string.add_newspaper_to_personal_preference);
            }
            mOperationView.setTag(OPERATION_TYPE_PREFERENCE);
        }
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(isVisible()){
            if(keyCode == KeyEvent.KEYCODE_NOTIFICATION){
                
                String type = (String) mOperationView.getTag();
                if(type == null || type.isEmpty())
                    return true;
                
                if(type.equals(OPERATION_TYPE_PREFERENCE)){
                    if("1".equals(mCurrentSubCategory.Preference)){
                        if(mController.RemoveNPPPreference(mCurrentSubCategory.Id) != -1){
                            mCurrentSubCategory.Preference = "0"; 
                            refreshCategoryView();
                            showPopupNotification(true);
                            initFooterInfo(false);
                        }else{
                            showPopupNotification(false);
                        }
                    }else{
                        if(mController.addNPPPreferenc(mCurrentSubCategory.Id) != -1){
                            mCurrentSubCategory.Preference = "1";
                            refreshCategoryView();
                            initFooterInfo(false);
                            showPopupNotification(true);
                        }else{
                            showPopupNotification(false);
                        }
                    }
                }else if(type.equals(OPERATION_TYPE_COLLETION)){
                    if("1".equals(mCurrentSubCategory.CurrentNewsPaper.Favarite)){
                        if(mController.cancelCollectNewsPaper(mCurrentSubCategory.CurrentNewsPaper.Id) != -1){
                            mCurrentSubCategory.CurrentNewsPaper.Favarite = "0";
                            initFooterInfo(true);
                            showPopupNotification(true);
                        }else{
                            showPopupNotification(false);
                        }
                        
                    }else{
                        if(mController.collectNewsPaper(mCurrentSubCategory.CurrentNewsPaper.Id)!= -1){
                            mCurrentSubCategory.CurrentNewsPaper.Favarite = "1";
                            initFooterInfo(true);
                            showPopupNotification(true);
                        }else{
                            showPopupNotification(false);
                        }
                    }
                }
            }
        }
        return super.onKeyDown(keyCode, event);
            
    }
    
    private void showPopupNotification(boolean succeess){
        View contentView = mActivity.getLayoutInflater().inflate(R.layout.newspaper_popup_notification_view, null);
        View measureView = contentView.findViewById(R.id.operation_notify_view);
        measureView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        TextView message = (TextView) contentView.findViewById(R.id.message);
        if(succeess){
            message.setText(R.string.text_do_successful);
        }else{
            message.setText(R.string.text_do_fail);
        }
       final PopupWindow popupWindow = new PopupWindow(contentView, measureView.getMeasuredWidth(), measureView.getMeasuredHeight());
        
        popupWindow.setAnimationStyle(R.style.AnimationPreview);
        
        popupWindow.showAsDropDown(mOperationView,-(measureView.getMeasuredWidth()/2- mOperationView.getWidth()/2),-15);
        contentView.postDelayed(new Runnable() {
            
            @Override
            public void run() {
                popupWindow.dismiss();
            }
        }, 2000);
    }
    private NewsPaperCategory refreshCategoryView(){
        NewsPaperCategory favorite = null;
        for(NewsPaperCategory category : mMainCategoryData){
            if(category.Id == null || category.Id.isEmpty())
                favorite =  category;
        }
        if( mCurrentSubCategory.Preference.equals("1")){
            if(favorite == null){
                favorite = new NewsPaperCategory();
                favorite.SubCategroys = new ArrayList<NewsPaperCategory>();
                favorite.Name = getString(R.string.personal_preference);
                favorite.SubCategroys.add(mCurrentSubCategory);
                mMainCategoryData.add(favorite);
                mMainCategoryView.setData(mMainCategoryData);
                mMainCategoryView.notifyDataChanged();
            }else{
                favorite.SubCategroys.add(mCurrentSubCategory);
            }
        }else{
            if(favorite != null && favorite.SubCategroys != null){
                favorite.SubCategroys.remove(mCurrentSubCategory);
                if(favorite.SubCategroys == null || favorite.SubCategroys.isEmpty()){
                    mMainCategoryData.remove(favorite);
                    mMainCategoryView.notifyDataChanged();
                }
            }
        }
        
        if(mCurrentMainCategroy.Id == null || mCurrentMainCategroy.Id.isEmpty()){
            mSubCategoryView.notifyDataChanged();
        }
       
       return favorite;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mHistoryInfo =new HistoryInfo();
        if(mCurrentMainCategroy != null){
            mHistoryInfo.MainCategoryId = mCurrentMainCategroy.Id;
            mHistoryInfo.SubCategoryId = mCurrentMainCategroy.CurrentSubCategoryId;
            mHistoryInfo.NewsPaperId = mCurrentSubCategory.CurrentNewsPaper.Id;
            mController.saveNewsPaperHistoryInfo(mHistoryInfo);
        }
        mController.destroy();
        ImageManager.getInstance(mActivity).destroy();
    }
    
}
