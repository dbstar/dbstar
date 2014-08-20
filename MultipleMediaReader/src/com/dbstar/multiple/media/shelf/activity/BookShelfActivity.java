package com.dbstar.multiple.media.shelf.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dbstar.multiple.media.common.ShelfController;
import com.dbstar.multiple.media.common.VoicedBookService;
import com.dbstar.multiple.media.data.Book;
import com.dbstar.multiple.media.data.BookCategory;
import com.dbstar.multiple.media.shelf.R;
import com.dbstar.multiple.media.util.ToastUitl;
import com.dbstar.multiple.media.widget.BookCategoryView;
import com.dbstar.multiple.media.widget.BookShelfGroup;

public class BookShelfActivity extends Activity {
    
    private static final String FAVORITE_CATEGORY_ID = "favorite_bookcategory_id";
    private static final int SHELF_ACTION_INIT_BOOK = 0;
    private static final int SHELF_ACTION_NEXT_5_BOOK = 1;
    private static final int SHELF_ACTION_PRE_5_BOOK = 2;
    private static final int SHELF_ACTION_SHOW_NEXT_BOOK = 3;
    private static final int SHELF_ACTION_SHOW_PRE_BOOK = 4;
    
    private BookCategoryView mBookCategoryView;
    BookShelfGroup mBookShefl;
    private List<BookCategory> mCategoryInfo;
    private ShelfController mController;
    private ImageView mCategoryIndicator;
    private View mNoticeView;
    private RelativeLayout mBookSynopsisLayout;
    private TextView mAuthor;
    private TextView mBookTitle;
    private TextView mSynopsis;
    private BookCategory mCurrentBookCategory;
    private String mRootId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_shelf);
        mRootId = getIntent().getStringExtra("Id");
        mController = ShelfController.getInstance(this);
        inintView();
      
    }
    
    protected void onResume() {
        super.onResume();
    }
  
    private void inintView() {
        mBookCategoryView = (BookCategoryView) findViewById(R.id.book_category);
        mBookShefl = (BookShelfGroup) findViewById(R.id.shelf_group);
        
        mBookShefl.setHandler(mHandler);
        mCategoryIndicator = (ImageView) findViewById(R.id.category_indicator);
        mNoticeView = findViewById(R.id.shwoNoticeView);
        mAuthor = (TextView) findViewById(R.id.author);
        mBookTitle = (TextView) findViewById(R.id.bookName_title);
        mSynopsis = (TextView) findViewById(R.id.synopsis);
        mBookSynopsisLayout = (RelativeLayout) findViewById(R.id.bookContent_synopsis);
        mBookSynopsisLayout.setVisibility(View.INVISIBLE);
        mNoticeView.setVisibility(View.GONE);
        
        
        mCategoryInfo = mController.loadBookCategoryInfo(mRootId);
        if(mCategoryInfo == null || mCategoryInfo.isEmpty())
            mNoticeView.setVisibility(View.VISIBLE);
        String historyId = mController.getLastSaveRecord();
        mBookCategoryView.setSelection(getWillSelectedIndex(historyId, mCategoryInfo));
        mBookCategoryView.setData(mCategoryInfo);
        mBookCategoryView.notifyDataChanged(); 
        
        mBookCategoryView.setOnItemSelectedListener(new BookCategoryView.OnItemSelectedListener() {
            
            @Override
            public void onSelected(View v, BookCategory category) {
                if(category == mCurrentBookCategory)
                    return;
                mCurrentBookCategory = category;
                loadBookInfoByCategoryId(category);
            }
        });
        loadFavoriteBookInfo();
    }
    
    private void loadFavoriteBookInfo(){
        new AsyncTask<String, Integer, List<Book>>(){

            @Override
            protected List<Book> doInBackground(String... params) {
                return mController.loadAllFavoriteBooks(mRootId);
            }
            protected void onPostExecute(java.util.List<Book> result) {
                if(result != null && !result.isEmpty()){
                    BookCategory favorite = new BookCategory();
                    favorite.Id = FAVORITE_CATEGORY_ID;
                    favorite.Name = getString(R.string.text_collection);
                    favorite.BookList = new ArrayList<Book>();
                    favorite.BookList.addAll(result);
                    constructBookPage(favorite, result);
                    mCategoryInfo.add(favorite);
                    mBookCategoryView.notifyDataChanged();
                }
            };
            
        }.execute("");
    }
    private void loadBookInfoByCategoryId(BookCategory category){
       
        if(category.PageBooks == null || category.PageBooks.isEmpty()){
            new AsyncTask<BookCategory, Integer, List<Book>>(){
                BookCategory category;
                @Override
                protected void onPreExecute() {
                    mNoticeView.setVisibility(View.INVISIBLE);
                    mController.showLoadingDialog();
                }
                @Override
                protected List<Book> doInBackground(BookCategory... params) {
                    category = params[0];
                    return mController.loadBooksByCategroyId(category.LastIndex, category.Id);
                }
                @Override
                protected void onPostExecute(List<Book> books) {
                    Log.i("Futao", category.Name + ""+  books.size());
                    if(books == null || books.isEmpty()){
                        updateBookShelf(SHELF_ACTION_INIT_BOOK,null);
                        return;
                    }
                    if(category.BookList == null)
                        category.BookList = new ArrayList<Book>();
                    category.BookList.addAll(books);
                    constructBookPage(category, books);
                    updateBookShelf(SHELF_ACTION_INIT_BOOK,category);
                    }
            }.execute(category);
           
        }else{
            updateBookShelf(SHELF_ACTION_INIT_BOOK,category);
        }
    }
    private void loadMoreBookInfoByCategoryId(BookCategory category){
            new AsyncTask<BookCategory, Integer, List<Book>>(){
                BookCategory category;
                @Override
                protected void onPreExecute() {
                    mNoticeView.setVisibility(View.INVISIBLE);
                    mController.showLoadingDialog();
                }
                @Override
                protected List<Book> doInBackground(BookCategory... params) {
                    category = params[0];
                    return mController.loadBooksByCategroyId(category.LastIndex, category.Id);
                }
                @Override
                protected void onPostExecute(List<Book> books) {
                    if(books == null || books.isEmpty()){
                        if(mBookShefl.getChildCount() == 0){
                            mNoticeView.setVisibility(View.VISIBLE);
                        }else{
                            mBookSynopsisLayout.setVisibility(View.VISIBLE); 
                            ToastUitl.showToast(getApplicationContext(), "没有更多图书");
                        }
                        return;
                    }
                    if(category.BookList == null)
                        category.BookList = new ArrayList<Book>();
                    category.BookList.addAll(books);
                    constructBookPage(category, books);
                    Log.i("Futao", "pageNumer = " + category.PageNumber + "pageCount  = " + category.PageCount + "last index = " + category.LastIndex + "/" +category.PageBooks.size() );
                    updateBookShelf(SHELF_ACTION_NEXT_5_BOOK,category);
                    }
            }.execute(category);
           
    }
    
    private void constructBookPage(BookCategory category,List<Book> books){
        if(books == null || books.isEmpty())
            return;
        if(category.PageBooks == null){
              category.PageBooks = new ArrayList<Book[]>();
        }
        int lastPageSize = 0;
        if(!category.PageBooks.isEmpty()){
            lastPageSize = category.PageBooks.get(category.PageBooks.size() -1).length;
        }
        if(lastPageSize > 0 && lastPageSize < ShelfController.BOOK_PAGE_SIZE){
            Book [] newPage = null;
            if(books.size() > (ShelfController.BOOK_PAGE_SIZE - lastPageSize)){
                newPage = new Book[ShelfController.BOOK_PAGE_SIZE - lastPageSize];
            }else{
                newPage = new Book[lastPageSize + books.size()];
            }
            Book [] lastPage = category.PageBooks.get(category.PageBooks.size() -1);
            int count =0;
            for(int i = 0;i< lastPage.length;i++){
                newPage[i] = lastPage[i];
                count ++;
            }
            for(int i = 0;i< newPage.length - lastPage.length; i++){
                newPage[count ++] = books.remove(0);
            }
            
            category.PageBooks.remove(category.PageBooks.size() -1);
            category.PageBooks.add(newPage);
        }
        int size = books.size();
        int count =  (books.size() + ShelfController.BOOK_PAGE_SIZE -1) / ShelfController.BOOK_PAGE_SIZE;
        category.PageCount +=count;
        int index = 0;
        for(int i = 0;i< count ;i++){
            int pageSize = Math.min(ShelfController.BOOK_PAGE_SIZE, size - index);
            Book[] page = new Book[pageSize];
            for(int j = 0;j<pageSize ;j ++){
                page[pageSize -1 -j] = books.get(index);
                index ++;
            }
            category.PageBooks.add(page);
        }
        category.LastIndex += size; 
        
    }
    private void updateBookShelf(int action , BookCategory category){
        mBookSynopsisLayout.setVisibility(View.INVISIBLE); 
        switch (action) {
        case SHELF_ACTION_INIT_BOOK:
            Book [] books = getPage(category);
            if(books == null || books.length == 0){
                mBookShefl.initChildView(null);
                mNoticeView.setVisibility(View.VISIBLE);
               
            }else{
                mBookShefl.initChildView(books);
                mNoticeView.setVisibility(View.INVISIBLE);
            }
            break;

        case SHELF_ACTION_NEXT_5_BOOK:
            if(category.PageNumber < category.PageCount -1){
                category.PageNumber ++;
                books = getPage(category);
                if(books == null || books.length == 0){
                    ToastUitl.showToast(getApplicationContext(), "没有更多图书");
                }else{
                    mBookShefl.Next5Book(books);
                }
            }else{
                loadMoreBookInfoByCategoryId(category);
            }
            break;
        case SHELF_ACTION_PRE_5_BOOK:
            if(category.PageNumber > 0){
                category.PageNumber --;
                books = getPage(category);
                if(books == null || books.length == 0){
                    ToastUitl.showToast(getApplicationContext(), "没有更多图书");
                }else{
                    mBookShefl.Pre5Book(books);
                }
            }else{
                ToastUitl.showToast(getApplicationContext(), "已经是最前了");
                if(mBookShefl.getChildCount() > 0)
                    mBookSynopsisLayout.setVisibility(View.VISIBLE); 
            }
            break;
            
        case SHELF_ACTION_SHOW_PRE_BOOK:
            if(mBookShefl.getChildCount() > 1){
                mBookShefl.scrollToPreBook();
            }else{
                mBookSynopsisLayout.setVisibility(View.VISIBLE); 
            }
            break;
            
        case SHELF_ACTION_SHOW_NEXT_BOOK:
            if(mBookShefl.getChildCount() > 1){
                mBookShefl.scrollToNextBook();
            }else{
                mBookSynopsisLayout.setVisibility(View.VISIBLE); 
            }
            break;
        }
        mController.hideLoadingDialog();
    }
    private void updateBookSynoppsisLayout(final Book book){
        if(book == null){
            mBookSynopsisLayout.setVisibility(View.INVISIBLE); 
            mAuthor.setText(null);
            mBookTitle.setText(null);
            mSynopsis.setText(null);
        }else{
            mAuthor.setText(book.Author);
            mBookTitle.setText(book.Name);
            mSynopsis.setText(book.Synopsis);
            mBookSynopsisLayout.setVisibility(View.VISIBLE); 
        }
    }
    private Book [] getPage(BookCategory category){
        if(category != null && category.PageBooks != null && category.PageBooks.size() > category.PageNumber)
            return category.PageBooks.get(category.PageNumber);
        return null;
    }
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(!mBookShefl.isAnimationStop())
            return true;
        if(event.getAction() ==KeyEvent.ACTION_DOWN){
            switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                updateBookShelf(SHELF_ACTION_SHOW_PRE_BOOK, null);
                break;
    
            case KeyEvent.KEYCODE_DPAD_RIGHT:
              updateBookShelf(SHELF_ACTION_SHOW_NEXT_BOOK, null);
                break;
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:{
                updateBookShelf(SHELF_ACTION_NEXT_5_BOOK, mCurrentBookCategory);
                break;
            }
            case KeyEvent.KEYCODE_MEDIA_REWIND: {
                updateBookShelf(SHELF_ACTION_PRE_5_BOOK, mCurrentBookCategory);
                break;
            }
            case KeyEvent.KEYCODE_DPAD_CENTER:
                Book book = mBookShefl.getBookInfoByIndex(mBookShefl.getChildCount() -1);
                if(book == null)
                    return true;
                startReadActivity(book);
               // mController.updateBookLastUpdateTime(book.Id);
                break;
            case KeyEvent.KEYCODE_NOTIFICATION:
                showBookOperationView();
                //showBookOprationDialog();
                break;
           case KeyEvent.KEYCODE_BACK:
                finish();
                overridePendingTransition(0, 0);
               break;
            }
            return true;
        }else if(event.getAction() == KeyEvent.ACTION_UP){
            switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if(mCategoryInfo != null && !mCategoryInfo.isEmpty()){
                    mBookShefl.setAnimationStatu(false);
                    return super.dispatchKeyEvent(event);
                }

            }
        }
        return true;
    };
    
    private void showBookOperationView(){
        final Book book = mBookShefl.getBookInfoByIndex(mBookShefl.getChildCount() -1);
        if(book == null)
            return;
        final RelativeLayout contentView = (RelativeLayout) getLayoutInflater().inflate(R.layout.book_operation_view, null);
        final View operationCotentView = contentView.findViewById(R.id.operation_content_view);
        final View operationNotifyView = contentView.findViewById(R.id.operation_notify_view);
        operationCotentView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        final PopupWindow operationView = new PopupWindow(contentView, operationCotentView.getMeasuredWidth(), operationCotentView.getMeasuredHeight());
        operationView.setAnimationStyle(R.style.AnimationPreview);
        operationView.setFocusable(true);
        operationView.setOutsideTouchable(true);
        operationView.setBackgroundDrawable(new
                ColorDrawable(Color.parseColor("#00000000")));
        TextView loation = (TextView) findViewById(R.id.operation_location_view);
        operationView.showAsDropDown(loation,-(operationCotentView.getMeasuredWidth()/2- loation.getWidth()/2),-15);
        final SeekBar seekBar =   (SeekBar) contentView.findViewById(R.id.operation_seekbar);
        final TextView collect = (TextView) contentView.findViewById(R.id.operation_collect);
        final TextView delete = (TextView) contentView.findViewById(R.id.operation_delete);
        final TextView message = (TextView) contentView.findViewById(R.id.message); 
        if("1".equals(book.Favorite)){
            collect.setText(R.string.cancel_collection);
        }else{
            collect.setText(R.string.text_collect_book);
        }
        collect.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        operationCotentView.setVisibility(View.VISIBLE);
        operationNotifyView.setVisibility(View.GONE);
        seekBar.setMax(100);
        seekBar.setProgress(80);
        seekBar.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                int action = event.getAction();
                if (action == KeyEvent.ACTION_DOWN) {

                    int process = seekBar.getProgress();
                    switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_UP: {
                        if (process < 50){
                            seekBar.setProgress(80);
                            collect.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                            delete.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                        }
                        break;
                    }

                    case KeyEvent.KEYCODE_DPAD_DOWN: {
                        if (process > 50){
                            seekBar.setProgress(20);
                            delete.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                            collect.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                        }
                        break;
                    }
                    case KeyEvent.KEYCODE_DPAD_CENTER: {
                        if(process < 50){
                            if((mController.deleteBook(book.Id)) != -1){
                                for(BookCategory category : getCategoryByBookId(book.Id)){
                                    deleteBookFromBookPages(category, book);
                                }
                                message.setText(R.string.text_do_successful);
                            }else{
                                message.setText(R.string.text_do_fail);
                            }
                        }else{
                            if("1".equals(book.Favorite)){
                                if((mController.cancelCollectBook(book.Id)) != -1){
                                    book.Favorite = "0";
                                    refreshBookCategoryView(book);
                                    message.setText(R.string.text_do_successful);
                                }else{
                                    message.setText(R.string.text_do_fail);
                                }
                            }else{
                                if((mController.collectBook(book.Id)!= -1)){
                                    book.Favorite = "1";
                                    refreshBookCategoryView(book);
                                    message.setText(R.string.text_do_successful);
                                }else{
                                    message.setText(R.string.text_do_fail);
                                }
                            }
                        }
                        operationCotentView.setVisibility(View.GONE);
                        operationNotifyView.setVisibility(View.VISIBLE);
                        contentView.postDelayed(new Runnable() {
                            
                            @Override
                            public void run() {
                                operationView.dismiss();
                            }
                        }, 2000);
                    }
                    }
                }
                return false;
            }
        });
    }
    
    private void refreshBookCategoryView(Book book){
        BookCategory favorite =null;
        for(BookCategory category :mCategoryInfo){
            if(FAVORITE_CATEGORY_ID.equals(category.Id)){
                favorite = category;
            }
        }
        if("1".equals(book.Favorite)){
            if(favorite == null){
                favorite = new BookCategory();
                favorite.Id = FAVORITE_CATEGORY_ID;
                favorite.Name = getString(R.string.text_collection);
                favorite.BookList = new ArrayList<Book>();
                mCategoryInfo.add(favorite);
                mBookCategoryView.notifyDataChanged();
            }
            if(favorite.BookList == null)
                favorite.BookList = new ArrayList<Book>();
            favorite.BookList.add(book);
            List<Book> books = new ArrayList<Book>();
            books.add(book);
            constructBookPage(favorite, books);
        }else{
            if(favorite != null && favorite.PageBooks != null){
                deleteBookFromBookPages(favorite, book);
                if(favorite == mCurrentBookCategory)
                    updateBookFavorite(book.Id, book.Favorite);
            }
        }
    }
    private List<BookCategory> getCategoryByBookId(String bookId){
        List<BookCategory>  list= new ArrayList<BookCategory>();
        for(BookCategory category :mCategoryInfo){
            if(category.BookList == null || category.BookList.isEmpty())
                continue;
              for(Book book : category.BookList){
                  if(book.Id.equals(bookId)){
                      list.add(category);
                  }
              }
          }
        return list;
    }
    private void updateBookFavorite(String bookId,String favorite){
      for(BookCategory category :mCategoryInfo){
          if(category.BookList == null || category.BookList.isEmpty())
              continue;
            for(Book book : category.BookList){
                if(book.Id.equals(bookId) && !category.Id.equals(FAVORITE_CATEGORY_ID)){
                    book.Favorite = favorite;
                    return;
                }
            }
        }
    }
    private void deleteBookFromBookPages(BookCategory category,Book book){
        category.BookList.remove(book);
        if(category.BookList.isEmpty()){
            category.recyle();
            if(category == mCurrentBookCategory){
                mBookShefl.startDeleteAnimation(null);
                mNoticeView.setVisibility(View.VISIBLE);
                updateBookSynoppsisLayout(null);
            }
        }else{
            category.PageBooks = null;
            category.PageCount = 0;
            category.LastIndex = 0;
            constructBookPage(category, category.BookList);
            if(category == mCurrentBookCategory){
                if(category.PageNumber  > (category.PageCount -1)){
                        updateBookShelf(SHELF_ACTION_PRE_5_BOOK, category);
                  }else{
                      Book [] page = getPage(category);
                      mBookShefl.startDeleteAnimation(page);
                  }
            }
        }
    }
    
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            Book book = mBookShefl.getBookInfoByIndex(mBookShefl.getChildCount() -1);
            mBookShefl.requestChidBookNameFocus();
             updateBookSynoppsisLayout(book);
        };
    };
    private int getWillSelectedIndex(String id, List<BookCategory> list) {
        if (list == null || id == null)
            return 0;
        BookCategory category;
        for (int i = 0, count = list.size(); i < count; i++) {
            category = list.get(i);
            if (id.equals(category.Id)) {
                return i;
            }
        }
        return 0;
    }
    protected void startReadActivity(Book book){
        Intent intent = new Intent();
        intent.setClassName("com.media.android.dbstarplayer", "com.media.android.dbstarplayer.DbStarPlayer");
        intent.setData(Uri.parse("file:///" + book.Path));
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
    @Override
    protected void onDestroy() {
       mController.saveCurrentRecord(mCurrentBookCategory.Id);
       mController.destroy();
       super.onDestroy();
        
    }
    
}
