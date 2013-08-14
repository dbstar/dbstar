package com.dbstar.guodian.app.newsflash;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dbstar.R;
import com.dbstar.guodian.app.base.GDSmartActivity;
import com.dbstar.guodian.data.GDNews;
import com.dbstar.guodian.data.NewsPage;
import com.dbstar.util.DateUtil;
import com.dbstar.widget.CircleFlowIndicator;
import com.dbstar.widget.GDNewsViewGoup;
import com.dbstar.widget.GDNewsViewGoup.OnUpdatePageListener;


public class GDNewsFlashActivity extends GDSmartActivity {

    private Document mHtmlDoc;
    private StringBuilder mNewsContent;
    public final static String URL = "http://sgcc.smartlife.com.cn/video_list.php";
    public final static String JSOUP_TAG_NEWS_TITLE = "name";
    public final static String JSOUP_TAG_NEWS_CONTENT_INFO = "infotext";
    public final static String JSOUP_TAG_NEWS_CONTENT_PARAGRAPH = "p";
    public final static String JSOUP_TAG_NEWS_TITLE_LIST = "list";
    public final static String JSOUP_TAG_NEWS_A = "a";
    public final static String JSOUP_TAG_NEWS_HREF = "href";
    public final static String JSOUP_TAG_NEWS_PAGEBAR = "pagebar";
    public final static String JSOUP_TAG_NEWS_PAGE_INFO = "strong";
    public final static String JSOUP_TAG_NEWS_DATE = "date";
    public final static String JSOUP_TAG_NEWS_CONTENT_PIC = "pic";
    public final static String JSOUP_TAG_NEWS_CONTENT_IMG = "img";
    public final static String JSOUP_TAG_NEWS_CONTENT_IMG_SRC = "src";
    
    
    public final static int MESSAGE_WHAT_PARSE_TITLE_FINISH = 0;
    public final static int MESSAGE_WHAT_UPDATE_LIST = 1;
    public final static int MESSAGE_WHAT_PARSE_CONTENT_FINISH = 2;
    public final static int MESSAGE_WHAT_PARSE_ERROR = 3;
    public final static int MESSAGE_WHAT_PARSE_CONTENT_ERROR = 4;
    public final static int MESSAGE_WHAT_PARSE_NULL = 5;
    
    private NewsPage page;
    private List<NewsPage> mHtmlDataSource;
    private int mParseCount;
    private ListView mListView;
    private int mCurrentPage = 0;
    private int mPageCount;
    private int mItemCount;
    NewsAdapter mListAdapter = null;
    private TextView mTitle;
    private int mMaxCachePageCount = 4;
    private  int  mPageSize;
    
    
    private LinearLayout mDetailLayout;
    private RelativeLayout mListLayou;
    private TextView mItemCountView, mPageNumberView;
    private TextView mContentPageCount,mContentPgeNumber;
    private String mStrGong, mStrTiao, mStrDi, mStrYe;
    
    private GDNewsViewGoup mGdNewsViewGoup;
    private boolean mIsParsing;
    
    private CircleFlowIndicator mIndicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gd_news_flash);
        initializeView();
        Intent intent = getIntent();
        mMenuPath = intent.getStringExtra(INTENT_KEY_MENUPATH);
        
        if (mMenuPath != null) {
            String[] menuArray = mMenuPath.split(MENU_STRING_DELIMITER);
            showMenuPath(menuArray);
        }
        mHtmlDataSource = new ArrayList<NewsPage>();
        loadNewsTitle(URL + "?type=2");
    }
    @Override
    public void notifyEvent(int type, Object event) {
        
    }
    @Override
    public void initializeView() {
        super.initializeView();
        mStrGong = getResources().getString(R.string.text_gong);
        mStrTiao = getResources().getString(R.string.text_tiao);
        mStrDi = getResources().getString(R.string.text_di);
        mStrYe = getResources().getString(R.string.text_ye);
        
        mListLayou = (RelativeLayout) findViewById(R.id.list_view);
        mDetailLayout = (LinearLayout) findViewById(R.id.detail);
        mGdNewsViewGoup = (GDNewsViewGoup) findViewById(R.id.gdContent);
        mIndicator = (CircleFlowIndicator) findViewById(R.id.indicator);
        
        mContentPageCount = (TextView) findViewById(R.id.content_page_count);
        mContentPgeNumber = (TextView) findViewById(R.id.content_page_number);
        
        mDetailLayout.setVisibility(View.GONE);
        mListLayou.setVisibility(View.VISIBLE);
        
        mListView = (ListView) findViewById(R.id.list);
        mTitle = (TextView) findViewById(R.id.title);
        
        mItemCountView = (TextView) findViewById(R.id.notices_count);
        mPageNumberView = (TextView) findViewById(R.id.notices_pages);
        
        mGdNewsViewGoup.setOnUpdatePageListener(new OnUpdatePageListener() {
            
            @Override
            public void onUpdate(int totalPage, int currentPage) {
                mIndicator.setPageCount(totalPage);
                mIndicator.setCurrentPage(currentPage -1);
                mContentPageCount.setText(String.valueOf(totalPage));
                mContentPgeNumber.setText(String.valueOf(currentPage));
            }
        });
        
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                
                if(mIsParsing)
                    return ;
                final GDNews gdNews = mListAdapter.getData().news
                        .get(position);
                mTitle.setText(gdNews.Title);
                mListLayou.setVisibility(View.INVISIBLE);
                showContent(position);
              
            }
        });
        
        mListView.setOnKeyListener(new OnKeyListener() {
            
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(mIsParsing)
                    return true;
                int action = event.getAction();
                if (action == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_UP:
                        if(mListView.getSelectedItemPosition() == 0 ){
                            loadPrePage();
                            return true;
                        }
                        break;

                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        if(mListView.getSelectedItemPosition() == mListAdapter.getData().news.size() -1){
                           loadNextPage();
                           return true;
                        }
                        break;
                       
                    }
                    
                }
                return false;
            }
        });
        
//        mContent.setOnKeyListener(new OnKeyListener() {
//            
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                
//                int action = event.getAction();
//                if (action == KeyEvent.ACTION_DOWN) {
//                    switch (keyCode) {
//                    case KeyEvent.KEYCODE_DPAD_RIGHT:
//                        
//                      mContent.loadNextPage();
//                        break;
//
//                    case KeyEvent.KEYCODE_DPAD_LEFT:
//                        mContent.loadPrePage();
//                        break;
//                       
//                    }
//                    
//                } 
//                
//                return false;
//            }
//        });
//        
    }
    
    private void loadNewsTitle(String url){
        new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected void onPreExecute() {
                showLoadingPage();
            }
            @Override
            protected Boolean doInBackground(String... params) {
                mIsParsing = true;
                return parseTitleListFromUrl(params[0]);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if(result){
                    if(mHtmlDataSource.isEmpty()){
                        handler.sendEmptyMessage(MESSAGE_WHAT_PARSE_NULL);
                        return;
                    }
                    handler.sendEmptyMessage(MESSAGE_WHAT_PARSE_TITLE_FINISH);
                }else{
                    handler.sendEmptyMessage(MESSAGE_WHAT_PARSE_ERROR);
                }
                
            }
        }.execute(url);
    }
    private void showContent( int positon){
        
        new AsyncTask<Integer, Integer,Integer>() {
            boolean flag = true;
            @Override
            protected void onPreExecute() {
                showLoadingPage();
            }
            @Override
            protected Integer doInBackground(Integer... params) {
                mIsParsing = true;
                flag = parseContentFromUrl(params[0]);
                return params[0];
            }
            @Override
            protected void onPostExecute(Integer result) {
                if(flag){
                    Message message = handler.obtainMessage(MESSAGE_WHAT_PARSE_CONTENT_FINISH);
                    message.arg1 = result;
                    handler.sendMessage(message);
                }else{
                    handler.sendEmptyMessage(MESSAGE_WHAT_PARSE_CONTENT_ERROR);
                }
            }
        }.execute(positon);
        
        
    }
    private void loadNextPage() {
        if (mCurrentPage == mHtmlDataSource.get(0).TotalPages -1){
            return;
             
        }
        mCurrentPage++;
        if (mCurrentPage >= mHtmlDataSource.size()) {
            new AsyncTask<String, Integer, Boolean>() {
                @Override
                protected void onPreExecute() {
                    showLoadingPage();
                }
                @Override
                protected Boolean doInBackground(String... params) {
                   
                    return parseTitleListFromUrl(mHtmlDataSource.get(mHtmlDataSource
                            .size() - 1).NextPageURL);
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    if(result){
                        handler.sendEmptyMessage(MESSAGE_WHAT_UPDATE_LIST);
                    }else{
                        mCurrentPage --;
                        handler.sendEmptyMessage(MESSAGE_WHAT_PARSE_ERROR);
                    }
                }
            }.execute("");
        } else {
            handler.sendEmptyMessage(MESSAGE_WHAT_UPDATE_LIST);
        }

    }
    
    
    private void loadPrePage() {
        if (mCurrentPage == 0){
            return;
            
        }

        mCurrentPage--;

        handler.sendEmptyMessage(MESSAGE_WHAT_UPDATE_LIST);

    }

    public boolean parseContentFromUrl(int position) {
        GDNews gdNews = mListAdapter.getData().news.get(position);
        mIsParsing = true;
        try {
            mHtmlDoc = Jsoup.parse(new URL(gdNews.ContentURL), 8000);
            
            Elements els = mHtmlDoc
                    .getElementsByClass(JSOUP_TAG_NEWS_CONTENT_INFO);
            Elements contents = els.first().getElementsByTag(
                    JSOUP_TAG_NEWS_CONTENT_PARAGRAPH);
            
            
            mNewsContent = new StringBuilder();

            for (Element e : contents) {
                mNewsContent.append("        ");
                mNewsContent.append(e.text());
                mNewsContent.append("\n");
            }
            
            Elements pics = els.first().getElementsByClass(JSOUP_TAG_NEWS_CONTENT_PIC);
            gdNews.urls = new ArrayList<String>();
            String picUr = null;
            for(Element pic : pics){
                picUr = pic.getElementsByTag(JSOUP_TAG_NEWS_CONTENT_IMG).first().attr(JSOUP_TAG_NEWS_CONTENT_IMG_SRC);
                String fileName = picUr.substring(picUr.lastIndexOf("/"),
                        picUr.length());
                fileName = URLEncoder.encode(fileName, "UTF-8");
                picUr = picUr.substring(0, picUr.lastIndexOf("/") + 1) + fileName;
                gdNews.urls.add(picUr);
            }
            
            return true;
        } catch (Exception e1) {
            mIsParsing = false;
            e1.printStackTrace();
        }
        return false;
    }

    public boolean parseTitleListFromUrl(String url) {
        mIsParsing = true;
        mParseCount++;
        try {
            mHtmlDoc = Jsoup.parse(new URL(url), 8000);
            
        page = new NewsPage();
        Element list = mHtmlDoc.getElementsByClass(JSOUP_TAG_NEWS_TITLE_LIST)
                .first();
        Elements els = list.getElementsByClass(JSOUP_TAG_NEWS_TITLE);
        Elements dates = list.getElementsByClass(JSOUP_TAG_NEWS_DATE);
        GDNews news;
        for (int i = 0; i < els.size(); i++) {
            news = new GDNews();
            Element e = els.get(i);
            news.Title = e.getElementsByTag(JSOUP_TAG_NEWS_A).text();
            news.ContentURL = e.getElementsByTag(JSOUP_TAG_NEWS_A).attr(
                    JSOUP_TAG_NEWS_HREF);
            e = dates.get(i);
            news.date = e.text();

            page.news.add(news);
        }

        Elements pageBars = mHtmlDoc.getElementsByClass(JSOUP_TAG_NEWS_PAGEBAR);

        Element pageBar = pageBars.first();

        Elements elements = pageBar.getElementsByTag(JSOUP_TAG_NEWS_PAGE_INFO);
        for (int i = 0; i < elements.size(); i++) {
            Element e = elements.get(i);
            if (i == 0)
                page.TotalPageItems = Integer.parseInt(e.text().trim());
            else if (i == 1)
                page.TotalPages = Integer.parseInt(e.text().trim());
            else if (i == 2)
                page.CurrentPageNum = Integer.parseInt(e.text().trim());

        }

        Elements elements2 = pageBar.getElementsByTag(JSOUP_TAG_NEWS_A);
        for (int i = 0; i < elements2.size(); i++) {
            Element e = elements2.get(i);
            if (i == 1)
                page.PrePageURL = URL + e.attr(JSOUP_TAG_NEWS_HREF);
            else if (elements2.size() == 4 && i == 2)
                page.NextPageURL = URL + e.attr(JSOUP_TAG_NEWS_HREF);

        }

        mHtmlDataSource.add(page);
        if (mParseCount < mMaxCachePageCount) {
            if (page.CurrentPageNum < page.TotalPages)
                parseTitleListFromUrl(page.NextPageURL);
            return true;
        } else {
            mParseCount = 0;
            return true;
        }
        } catch (Exception e) {
            mIsParsing = false;
            e.printStackTrace();
        }
        
        return false;
    }

    class NewsAdapter extends BaseAdapter {
        ViewHolder v;
        NewsPage data;

        public void setData(NewsPage newsPage) {
            this.data = newsPage;
        }
        public NewsPage getData(){
            return data;
        }
        
        @Override
        public int getCount() {
            return data.news.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.gd_news_flash_item, null);
                //convertView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT,45));
                v = new ViewHolder();
                v.title = (TextView) convertView.findViewById(R.id.title);
                v.date = (TextView) convertView.findViewById(R.id.date);
                v.index = (TextView) convertView.findViewById(R.id.index);
                convertView.setTag(v);
            } else {
                v = (ViewHolder) convertView.getTag();
            }
            GDNews news = data.news.get(position);
            v.title.setText(news.Title);
            v.date.setText(DateUtil.getStringFromDateString(news.date, DateUtil.DateFormat4));
            v.index .setText(String.valueOf(((mCurrentPage * mPageSize) + position + 1 ) + "、"));
            return convertView;
        }

        class ViewHolder {
            TextView index;
            TextView title;
            TextView date;
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == MESSAGE_WHAT_PARSE_TITLE_FINISH) {
                handleRequestFinished();
                if(mCurrentPage < mHtmlDataSource.size()){
                    mListAdapter = new NewsAdapter();
                    mListAdapter.setData(mHtmlDataSource.get(mCurrentPage));
                    mListView.setAdapter(mListAdapter);
                    
                    mPageSize = mHtmlDataSource.get(mCurrentPage).news.size();
                    mPageCount = mHtmlDataSource.get(mCurrentPage).TotalPages;
                    mItemCount = mHtmlDataSource.get(mCurrentPage).TotalPageItems;
                    
                    mItemCountView.setText(mStrGong + mItemCount + mStrTiao);
                    displayPageNumber();
                    mListView.setFocusableInTouchMode(true);
                    mListView.setFocusable(true);
                    mListView.requestFocus();
                }

            } else if (msg.what == MESSAGE_WHAT_UPDATE_LIST) {
                    handleRequestFinished();
                    if(mCurrentPage < mHtmlDataSource.size()){
                    mListAdapter.setData(mHtmlDataSource.get(mCurrentPage));
                    mListAdapter.notifyDataSetChanged();
                    mListView.setSelection(0);
                    displayPageNumber();
                    mListView.setFocusableInTouchMode(true);
                    mListView.setFocusable(true);
                    mListView.requestFocus();
                }
            } else if(msg.what == MESSAGE_WHAT_PARSE_CONTENT_FINISH){
                    handleRequestFinished();
                    mListView.setFocusableInTouchMode(false);
                    mListView.setFocusable(false);
                    mListView.clearFocus();
                    mDetailLayout.setVisibility(View.VISIBLE);
                    GDNews gdNews = mListAdapter.getData().news.get(msg.arg1);
                   if((mNewsContent == null || mNewsContent.length() == 0) && (gdNews.urls == null || gdNews.urls.isEmpty())){
                       showErrorMsg(R.string.loading_null);
                   }else{
                       mGdNewsViewGoup.setData(gdNews.urls, mNewsContent.toString());
                   }
                    mNewsContent = null;
            }else if(msg.what == MESSAGE_WHAT_PARSE_ERROR){
                    showErrorMsg(R.string.loading_error);
            }else if(msg.what == MESSAGE_WHAT_PARSE_CONTENT_ERROR){
                    showErrorMsg(R.string.loading_error);
                    mDetailLayout.setVisibility(View.GONE);
                    mListLayou.setVisibility(View.VISIBLE);
                    mListView.setFocusableInTouchMode(true);
                    mListView.setFocusable(true);
                    mListView.requestFocus();
            }else if(msg.what == MESSAGE_WHAT_PARSE_NULL){
                    showErrorMsg(R.string.loading_null);
            }
            
            mIsParsing = false;
        };
    };

    private void displayPageNumber() {
        mPageNumberView.setText(mStrDi + (mCurrentPage + 1) + mStrYe + "/"
                + mStrGong + mPageCount + mStrYe);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_BACK: {
            if (mDetailLayout.isShown()) {
               mDetailLayout.setVisibility(View.GONE);
               mListLayou.setVisibility(View.VISIBLE);
               mListView.setFocusableInTouchMode(true);
               mListView.setFocusable(true);
               mListView.requestFocus();
                return true;
            }
            break;
        }
        }

        return super.onKeyDown(keyCode, event);
    }
    
   
}


