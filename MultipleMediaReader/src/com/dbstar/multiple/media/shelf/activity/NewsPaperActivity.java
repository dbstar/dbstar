package com.dbstar.multiple.media.shelf.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.widget.RelativeLayout;

import com.dbstar.multiple.media.common.ShelfController;
import com.dbstar.multiple.media.data.NewsPaperPage;
import com.dbstar.multiple.media.fragment.BaseFragment;
import com.dbstar.multiple.media.fragment.MainFragmenet;
import com.dbstar.multiple.media.fragment.NewsPaperArticleContentFragment;
import com.dbstar.multiple.media.fragment.NewsPaperArticleListFragment;
import com.dbstar.multiple.media.shelf.R;
import com.dbstar.multiple.media.shelf.R.animator;

public class NewsPaperActivity extends Activity{
    
    private static final String TAG = "NewsPaperActivity";
    public static final int FRAGMENT_MAIN = 0x1000;
    public static final int FRAGMENT_ARTICLE_LIST = 0x2000;
    public static final int FRAGMENT_ARTICLE_CONTENT = 0x3000;
    public static final String SHOWTYPE_NORMAL = "Noraml";
    public static final String SHOWTYPE_COLLECTION = "Collection";
    public String mShowType = SHOWTYPE_NORMAL;
    private String mRootId;
    public int mSoundVolume;
    public boolean isMute;
    private SparseArray <BaseFragment> mFragments;
    RelativeLayout mGroupView; 
    
    public NewsPaperPage mCurrentPage,mLastEdition;
    private static int mCurrentFragement; 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newspaper_shelf);
        mGroupView = (RelativeLayout) findViewById(R.id.group_fragment);
        mRootId = getIntent().getStringExtra("Id");
        String showType = getIntent().getStringExtra("showType");
        showType = SHOWTYPE_NORMAL;
        if(showType != null)
            mShowType = showType;
        preperFragment();
        switchFragment(null,FRAGMENT_MAIN);
        
    }
    
    
    private void preperFragment(){
        mFragments = new SparseArray<BaseFragment>(3);
        mFragments.put(FRAGMENT_MAIN ,new MainFragmenet());
        mFragments.put(FRAGMENT_ARTICLE_LIST,new NewsPaperArticleListFragment());
        mFragments.put(FRAGMENT_ARTICLE_CONTENT,new NewsPaperArticleContentFragment());
        
    }
    
    public void switchFragment(NewsPaperPage page,int index){
        mCurrentFragement = index;
        mCurrentPage = page;
        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
        BaseFragment fragment= mFragments.get(index);
        if(fragment.isAdded()){
            transaction.show(fragment);
        }else{
            transaction.add(R.id.group_fragment, fragment);
            transaction.show(fragment);
        }
        if(FRAGMENT_MAIN == index){
            fragment = mFragments.get(FRAGMENT_ARTICLE_LIST);
            if(fragment.isAdded()){
                transaction.hide(fragment);
               
            }
        }else if(FRAGMENT_ARTICLE_LIST == index){
            fragment.SetData(page);
            fragment = mFragments.get(FRAGMENT_ARTICLE_CONTENT);
            if(fragment.isAdded()){
                transaction.hide(fragment);
            }
            fragment = mFragments.get(FRAGMENT_MAIN);
            transaction.hide(fragment);
            
        }else if(FRAGMENT_ARTICLE_CONTENT == index){
            fragment.SetData(page);
            fragment = mFragments.get(FRAGMENT_ARTICLE_LIST);
            if(fragment.isAdded()){
                transaction.hide(fragment);
            }
        }
      
        transaction.commit();
    }
    
    public void changeArticleTitleListFragmentData(NewsPaperPage edition){
      NewsPaperArticleListFragment  fragment = (NewsPaperArticleListFragment) mFragments.get(FRAGMENT_ARTICLE_LIST);
      fragment.changeListData(edition);
    }
    public void showNextArticle(){
        NewsPaperArticleListFragment fragment = (NewsPaperArticleListFragment) mFragments.get(FRAGMENT_ARTICLE_LIST);
        fragment.showNextArticle();
    }
    public void showNextPage(){
        MainFragmenet fragmenet = (MainFragmenet) mFragments.get(FRAGMENT_MAIN);
        fragmenet.showNextPage();
    }
    public String getFooterInfo(){
        MainFragmenet fragmenet = (MainFragmenet) mFragments.get(FRAGMENT_MAIN);
        return fragmenet.getFooterInfo();
    }
    
    public String getRootId(){
        return mRootId;
    }
    public boolean isCollectionType(){
        return mShowType.equals(SHOWTYPE_COLLECTION);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(FRAGMENT_ARTICLE_LIST == mCurrentFragement){
                switchFragment(null,FRAGMENT_MAIN);
                return true;
            }else if(FRAGMENT_ARTICLE_CONTENT == mCurrentFragement){
                switchFragment(null,FRAGMENT_ARTICLE_LIST);
                return true;
            }else if(FRAGMENT_MAIN == mCurrentFragement){
                finish();
                overridePendingTransition(0, 0);
            }
        }
        for(int i = 0 ;i < mFragments.size();i++){
            BaseFragment baseFragment = mFragments.valueAt(i);
            if(baseFragment !=null && baseFragment.isVisible())
                baseFragment.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
