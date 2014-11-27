package com.dbstar.multiple.media.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dbstar.multiple.media.adapter.NewsPaperPageAdapter;
import com.dbstar.multiple.media.common.ImageManager;
import com.dbstar.multiple.media.data.NewsPaperPage;
import com.dbstar.multiple.media.shelf.R;
import com.dbstar.multiple.media.shelf.activity.NewsPaperActivity;
import com.dbstar.multiple.media.widget.CustomListView;

public class NewsPaperArticleListFragment extends BaseFragment{
    
    private RelativeLayout mPagePicLoadingView;
    private CustomListView mArticleTitleListView;
    private TextView mPageTitleView;
    private ImageView mPageImageView;
    private TextView mFooterMainCategoryName;
    private TextView mFooterSubCategoryName;
    private TextView mFooterPageName;
    private NewsPaperPage mPage;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.newspaper_article_list_fragment_view, null);
        initView(view);
        
        
        return view;
    }
    
    private void initView(View view){
        
       mPagePicLoadingView = (RelativeLayout) view.findViewById(R.id.page_pic_loading_bar);
       mPageTitleView = (TextView) view.findViewById(R.id.page_title);
       mArticleTitleListView = (CustomListView) view.findViewById(R.id.articletitlelist);
       mPageImageView = (ImageView) view.findViewById(R.id.page_pic);
       mFooterMainCategoryName = (TextView) view.findViewById(R.id.footer_main_category);
       mFooterSubCategoryName = (TextView) view.findViewById(R.id.footer_sub_category);
       mFooterPageName = (TextView) view.findViewById(R.id.footer_page);
       mArticleTitleListView.setOnItemClickListener(new OnItemClickListener() {

           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              mPage.SelectedIndex = position;
               if (view != null) {
                   mPage.PaddinTop = view.getTop();
               }
               NewsPaperPageAdapter adapter = (NewsPaperPageAdapter) mArticleTitleListView.getAdapter();
               NewsPaperPage article = adapter.getEdition(position);
               adapter.setMarkPosition(position);
               article.IsOpen = 1;
               if (view != null) {
                   TextView textView = (TextView) view.findViewById(R.id.category_name);
                   textView.setTextColor(Color.RED);
                   textView.setTag("#FFFF0000");
               }
               View v;
               for(int i = 0,count = mArticleTitleListView.getChildCount();i  < count ; i ++){
                   v = mArticleTitleListView.getChildAt(i);
                   if(v == view){
                       v.findViewById(R.id.mark).setVisibility(View.VISIBLE);
                   }else{
                       v.findViewById(R.id.mark).setVisibility(View.INVISIBLE);
                   }
               }
               article.PicPath = mPage.PicPath;
               mActivity.switchFragment(article, NewsPaperActivity.FRAGMENT_ARTICLE_CONTENT);
           }
       });

       mArticleTitleListView.setOnKeyListener(new OnKeyListener() {

           @Override
           public boolean onKey(View v1, int keyCode, KeyEvent event) {
               if (event.getAction() == KeyEvent.ACTION_DOWN) {
                   View view = null;
                   switch (keyCode) {
                   case KeyEvent.KEYCODE_DPAD_UP: {
                       if (mArticleTitleListView.getSelectedItemPosition() == 0) {
                           return true;
                       }
                       view = mArticleTitleListView.getSelectedView();
                       view = mArticleTitleListView.getChildAt(mArticleTitleListView.indexOfChild(view) - 1);
                       break;
                   }
                   case KeyEvent.KEYCODE_DPAD_DOWN: {
                       view = mArticleTitleListView.getSelectedView();
                       view = mArticleTitleListView.getChildAt(mArticleTitleListView.indexOfChild(view) + 1);
                       break;
                   }
                   }

                   if (view != null) {
                       TextView textView = (TextView) view.findViewById(R.id.category_name);
                       textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                       textView.setTextColor(Color.WHITE);
                       View v ;
                       for (int i = 0, count = mArticleTitleListView.getChildCount(); i < count; i++) {
                           v = mArticleTitleListView.getChildAt(i);
                           textView = (TextView) v.findViewById(R.id.category_name);
                           if (v != view) {
                               textView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                               textView.setTextColor(Color.parseColor((String) textView.getTag()));
                           } 
                       }
                   }
               }
               return false;
           }
       });
    }
    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }
    
    public void showNextArticle(){
        mPage.SelectedIndex ++;
        mLog.i("showNextArticle" +  mPage.SelectedIndex);
        NewsPaperPageAdapter adapter = (NewsPaperPageAdapter) mArticleTitleListView.getAdapter();
//        Log.d("NewsPaperArticleListFragment", "<<<<<<<<<adapter.getCount() = " + adapter.getCount());
        if(mPage.SelectedIndex < adapter.getCount()){
            NewsPaperPage article = adapter.getEdition(mPage.SelectedIndex);
            adapter.setMarkPosition(mPage.SelectedIndex);
            article.IsOpen = 1;
            article.PicPath = mPage.PicPath;
            mActivity.switchFragment(article, NewsPaperActivity.FRAGMENT_ARTICLE_CONTENT);
        }else{
            //TODO  showNextEdition
//        	Log.d("NewsPaperArticleListFragment", "<<<<<<<<<?<<<<<<<<");
            mActivity.showNextPage();
        }
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            loadData();
        }
    }
    private NewsPaperPageAdapter mEditionadapter;
    private boolean isNeedLoad = true;
    @Override
    public void SetData(Object object) {
        if(object != null){
            if(object == mPage){
                isNeedLoad = false;
            }else
                isNeedLoad = true;
            mPage = (NewsPaperPage) object;
            
            if(isResumed() && !isHidden()&& isNeedLoad){
                loadData();
            }
        }else{
            isNeedLoad = false;
        }
    }
    
    public void changeListData(NewsPaperPage page){
        if(page != null){
           SetData(page);
            loadData();
            showNextArticle();
        }
    }
    
    private void loadData(){
        if(isNeedLoad){
            mPageTitleView.setText(mPage.title);
            if (mEditionadapter == null) {
                mEditionadapter = new NewsPaperPageAdapter(getActivity(), "#0A3782",R.layout.newspaper_article_list_item);
                mEditionadapter.setData(mPage.mArticles);
                mArticleTitleListView.setAdapter(mEditionadapter);
            } else {
                mEditionadapter.setData(mPage.mArticles);
                mEditionadapter.notifyDataSetChanged();
            }
            
            initFooterInfo();
            loadEditionPic(mPage.PicPath);
            mArticleTitleListView.requestFocus();
            
            int index = mPage.SelectedIndex;
            mEditionadapter.setMarkPosition(index);
            mArticleTitleListView.setSelectLastIndex(index == -1? 0 : index,mPage.PaddinTop);
            mArticleTitleListView.post(new Runnable() {
    
                @Override
                public void run() {
                    mArticleTitleListView.requestSelectItem();
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
    private void loadEditionPic(String filePath) {
        mPagePicLoadingView.setVisibility(View.VISIBLE);
        ImageManager.getInstance(getActivity()).getBitmapDrawable(filePath, new ImageManager.ImageCallback() {
            @Override
            public void imageLoaded(BitmapDrawable imageDrawable, String viewKey) {
                mPagePicLoadingView.setVisibility(View.INVISIBLE);
                mPageImageView.setVisibility(View.VISIBLE);
                mPageImageView.setImageBitmap(imageDrawable.getBitmap());
            }
        }, null);

    }
    
}
