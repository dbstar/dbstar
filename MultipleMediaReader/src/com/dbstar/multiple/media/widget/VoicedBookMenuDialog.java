package com.dbstar.multiple.media.widget;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.dbstar.multiple.media.data.VoiceBookPageInfo;
import com.dbstar.multiple.media.data.VoicedBook;
import com.dbstar.multiple.media.model.ModelVoicedBook.Label;
import com.dbstar.multiple.media.shelf.R;
import com.dbstar.multiple.media.util.DisplayUtil;

public class VoicedBookMenuDialog extends Dialog{
    
    private static final int TYPE_BOOKMARK = 0x1000;
    private static final int TYPE_CATALOGUE = 0x2000;
    private VoicedBook mBook;
    private List<VoiceBookPageInfo> mBookMarks;
    private Button mButtonAddBookMark,mButtonDeleteBookMark;
    private Button mButtonFindCatelogue;
    private Button mButtonFindMark;
    private Button mButtonLanguage;
    private ListView mListBookMarksView;
    private VoiceBookPageInfo mCurrentPageInfo;
    
    private OnBookMarkChangeListener mBookMarkChangeListener;
    private OnLanguageChangeListener mLanguageChangeListener;
    private OnBookMarkListItemClickListener mBookMarkListItemClickListener;
    private Activity mActivity;
    public VoicedBookMenuDialog(Context context, int theme) {
        super(context, theme);
        mActivity = (Activity) context;
    }
    public VoicedBookMenuDialog(Context context) {
        super(context);
    }
    
    public static VoicedBookMenuDialog create(Context context,VoicedBook data){
        VoicedBookMenuDialog dialog = new VoicedBookMenuDialog(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        View contetView = dialog.getLayoutInflater().inflate(R.layout.voicedbook_menus_dialog, null);
        dialog.setContentView(contetView);
        dialog.mBook = data;
        dialog.initBookMarksData();
        dialog.initView(contetView);
        return dialog;
        
    } 
    private void initBookMarksData(){
        mBookMarks = new ArrayList<VoiceBookPageInfo>();
        for(VoiceBookPageInfo info : mBook.mPages){
            if(info.Label.equals(Label.MARKED.value)){
                mBookMarks.add(info);
            }
        }
        
    }
    
    public void show(VoiceBookPageInfo mInfo){
        mCurrentPageInfo = mInfo;
        updateButtonState();
        show();
    }
    
    private void updateButtonState() {
       final  Button button;
        if(mCurrentPageInfo.Label.equals(Label.MARKED.value)){
            mButtonDeleteBookMark.setVisibility(View.VISIBLE);
            mButtonAddBookMark.setVisibility(View.INVISIBLE);
            button = mButtonAddBookMark;
        }else{
            mButtonDeleteBookMark.setVisibility(View.INVISIBLE);
            mButtonAddBookMark.setVisibility(View.VISIBLE);
            button = mButtonDeleteBookMark;
            
        }
        mButtonFindCatelogue.setFocusable(false);
        mButtonFindMark.setFocusable(false);
        mButtonLanguage.setFocusable(false);
        button.post(new Runnable() {
            
            @Override
            public void run() {
                button.setFocusable(true);
                button.requestFocus();
                mButtonFindCatelogue.setFocusable(true);
                mButtonFindMark.setFocusable(true);
                mButtonLanguage.setFocusable(true);
            }
        });
    }
    
    private void showBookMarkList(){
        ListAdapter adapter = (ListAdapter) mListBookMarksView.getAdapter();
        if(mLastFoucsId== R.id.reader_language_transformation ){
            mListBookMarksView.setVisibility(View.VISIBLE);
        }else if(mLastFoucsId == R.id.reader_find_toc){
            adapter.setData(mBookMarks);
            adapter.setTyep(TYPE_BOOKMARK);
            adapter.notifyDataSetChanged();
            RelativeLayout.LayoutParams params = (LayoutParams) mListBookMarksView.getLayoutParams();
            params.setMargins(DisplayUtil.dp2px(getContext(), 352),DisplayUtil.dp2px(getContext(), 60), 0, 0);
            
        }
        if(mBookMarks != null && mBookMarks.size() > 0){
            mListBookMarksView.post(new Runnable() {
                
                @Override
                public void run() {
                    mListBookMarksView.requestFocus();
                    mListBookMarksView.setSelection(0);
                }
            });
        }
    }
    private void hideListView(){
        mListBookMarksView.setVisibility(View.INVISIBLE);
    }
    private void showBookCatalogueList(){
        ListAdapter adapter = (ListAdapter) mListBookMarksView.getAdapter();
      if(mLastFoucsId== R.id.reader_add_bookmark || mLastFoucsId == R.id.reader_delete_bookmark){
          if(adapter == null || adapter.getType() != TYPE_CATALOGUE){
               adapter = new ListAdapter(mBook.mPages,TYPE_CATALOGUE);
              mListBookMarksView.setAdapter(adapter);
              }
          mListBookMarksView.setVisibility(View.VISIBLE);
      }else if(mLastFoucsId == R.id.reader_find_bookmark){
          adapter.setData(mBook.mPages);
          adapter.setTyep(TYPE_CATALOGUE);
          adapter.notifyDataSetChanged();
      }
      RelativeLayout.LayoutParams params = (LayoutParams) mListBookMarksView.getLayoutParams();
      params.setMargins(DisplayUtil.dp2px(getContext(), 117),DisplayUtil.dp2px(getContext(), 60), 0, 0);
      if(mBook.mPages != null &&mBook.mPages.size() > 0){
          mListBookMarksView.post(new Runnable() {
              
              @Override
              public void run() {
                  mListBookMarksView.requestFocus();
                  mListBookMarksView.setSelection(0);
              }
          });
      }
      }
    
    private void initView(View v){
        mButtonAddBookMark = (Button) v.findViewById(R.id.reader_add_bookmark);
        mButtonDeleteBookMark = (Button) v.findViewById(R.id.reader_delete_bookmark);
        mButtonFindMark = (Button) v.findViewById(R.id.reader_find_bookmark);
        mButtonFindCatelogue = (Button) v.findViewById(R.id.reader_find_toc);
        mListBookMarksView = (ListView) findViewById(R.id.list_bookmark);
        mButtonLanguage = (Button) findViewById(R.id.reader_language_transformation);
        
        mButtonLanguage.setOnFocusChangeListener(buttonFocusChangeListener);
        mButtonFindMark.setOnFocusChangeListener(buttonFocusChangeListener);
        mButtonAddBookMark.setOnFocusChangeListener(buttonFocusChangeListener);
        mButtonDeleteBookMark.setOnFocusChangeListener(buttonFocusChangeListener);
        mButtonFindCatelogue.setOnFocusChangeListener(buttonFocusChangeListener);
        
        mButtonAddBookMark.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                mCurrentPageInfo.Label = Label.MARKED.value;
                mBookMarks.add(mCurrentPageInfo);
                updateButtonState();
                if(mBookMarkChangeListener != null)
                    mBookMarkChangeListener.onChange();
            }
        });
        
        mButtonDeleteBookMark.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                mCurrentPageInfo.Label = Label.UNMARKED.value;
                mBookMarks.remove(mCurrentPageInfo);
                updateButtonState();
                if(mBookMarkChangeListener != null)
                    mBookMarkChangeListener.onChange();
            }
        });
        mListBookMarksView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mBookMarkListItemClickListener != null){
                    ListAdapter adapter = (ListAdapter) mListBookMarksView.getAdapter();
                    mBookMarkListItemClickListener.itemClick(adapter.getItemData(position).PageIndex);
                }
                
            }
        });
        
        mListBookMarksView.setOnKeyListener(new View.OnKeyListener() {
            
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(mListBookMarksView.getSelectedItemPosition() == mBook.mPages.size() -1 && keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
                    return true;
                }
                return false;
            }
        });
        
        mButtonLanguage.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if(mLanguageChangeListener != null)
                    mLanguageChangeListener.onChange();
            }
        });
    }
    public void setOnBookMarkChangeListener(OnBookMarkChangeListener changeListener){
        
        this.mBookMarkChangeListener = changeListener;
    }
    public void setOnLanguageChangeListener(OnLanguageChangeListener changeListener){
        this.mLanguageChangeListener = changeListener;
    }
    public void setOnBookMarkListItemClickListener(OnBookMarkListItemClickListener listener){
        this.mBookMarkListItemClickListener = listener;
        
    }
    public interface OnLanguageChangeListener{
        
        void onChange();
    }
    public interface OnBookMarkChangeListener{
        void onChange();
    }
    public interface OnBookMarkListItemClickListener{
        
        void itemClick(int pageIndex);
    }
    public void onDestroy(){
       
       
   }
    
    private int mLastFoucsId;
    OnFocusChangeListener buttonFocusChangeListener = new OnFocusChangeListener() {
        
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            int id = v.getId();
            
            switch (id) {
            case R.id.reader_find_bookmark:
                if(hasFocus){
                    v.setBackgroundResource(R.drawable.reader_read_image_find_bookmark_focus);
                    mButtonFindCatelogue.setBackgroundResource(R.drawable.reader_read_image_find_toc_no_focus);
                    showBookMarkList();
                }
                break;

            case R.id.reader_find_toc:
                if(hasFocus){
                    //hideBookMarkList();
                    mButtonFindMark.setBackgroundResource(R.drawable.reader_read_image_find_bookmark_no_focus);
                    v.setBackgroundResource(R.drawable.reader_read_image_find_toc_focus);
                    showBookCatalogueList();
                }
                    
                break;
            case R.id.reader_add_bookmark:
            case R.id.reader_delete_bookmark:
            case R.id.reader_language_transformation:
                if(hasFocus){
                   hideListView();
                    mButtonFindCatelogue.setBackgroundResource(R.drawable.reader_read_image_find_toc_no_focus);
                    mButtonFindMark.setBackgroundResource(R.drawable.reader_read_image_find_bookmark_no_focus);
                }
                break;
            }
            if(hasFocus)
                mLastFoucsId = id;
        }
    };
   class ListAdapter extends BaseAdapter{
       
    private List<VoiceBookPageInfo> data;
    private ViewHolder vh;
    private int type;
    public ListAdapter(List<VoiceBookPageInfo> data,int type) {
        this.type = type;
        this.data = data;
    }
    
    public void setData(List<VoiceBookPageInfo> data) {
        this.data = data;
    }
    public void setTyep(int type){
        this.type = type;
        
    }
    public int getType(){
        return this.type;
    }
    @Override
    public int getCount() {
        if(data == null || data.size() == 0)
            return 0;
        else
            return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    public VoiceBookPageInfo getItemData(int position){
        if(position < data.size())
            return data.get(position);
        return null;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = getLayoutInflater().inflate(R.layout.voicedbook_bookmarks_list_item, null);
            vh = new ViewHolder();
            vh.ItemValue = (TextView) convertView.findViewById(R.id.bookmark_item);
            vh.ItemPageIndex = (TextView) convertView.findViewById(R.id.bookmark_pageindex);
            convertView.setTag(vh);
        }
        
        vh = (ViewHolder) convertView.getTag();
        
        vh.ItemValue.setText(data.get(position).Title);
        vh.ItemPageIndex.setText(data.get(position).PageIndex+"");
        return  convertView;
    }
       
   }
   
   @Override
protected void onStop() {
    super.onStop();
    hideListView();
}
   class ViewHolder{
       TextView ItemValue;
       TextView ItemPageIndex;
   }
   @Override
public void cancel() {
    super.cancel();
    hideListView();
}
}
