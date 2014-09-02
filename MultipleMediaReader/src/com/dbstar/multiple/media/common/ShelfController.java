package com.dbstar.multiple.media.common;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;

import com.dbstar.multiple.media.data.Book;
import com.dbstar.multiple.media.data.BookCategory;
import com.dbstar.multiple.media.data.HistoryInfo;
import com.dbstar.multiple.media.data.NewsPaper;
import com.dbstar.multiple.media.data.NewsPaperCategory;
import com.dbstar.multiple.media.shelf.R;
import com.dbstar.multiple.media.widget.ShelfDeleteOrCollectDialog;
import com.dbstar.multiple.media.widget.ShelfLoadingDialog;
import com.dbstar.multiple.media.widget.ShelfLoadingDialog.Builder;
import com.dbstar.multiple.media.widget.ShelfTextFontSettingDialog;

public class ShelfController {
    // --------------------------------------------
    private static final String URI_AUTHORITY = "com.dbstar.multipleLanguageInfo.provider";
    private static final String ACTION_LOAD_NEWSPAPER_CATEGORIES = "LoadNewsPaperCategories";
    private static final String ACTION_LOAD_NEWSPAPERS = "LoadNewsPapers";
    private static final String ACTION_COLLECT_NEWSPAPER = "CollectNewsPaper";
    private static final String ACTION_CANCEL_COLLECT_NEWSPAPER = "CancelCollectNewsPaper";
    private static final String ACTION_ADD_NEWSPAPER_TO_PERSONAL_PREFERENCE = "AddNewsPaperToPersonalPreference";
    private static final String ACTION_REMOCE_NEWSPAPER_FROM_PERSONAL_PREFERENCE = "RemoveNewsPaperFromPersonalPreference";
    private static final String ACTION_LOAD_ALL_NEWSPAPERS = "LoadAllNewsPapers";
    private static final String ACTION_LOAD_COLLECTED_NEWSPAPER_CATEGORIES = "LoadCollectedNewsPaperCategories";
    private static final String ACTION_LOAD_COLLECTED_NEWSPAPERS = "LoadCollectedNewsPapers";

    private static final String ACTION_LOAD_BOOK_CATEGORIES = "LoadBookCategories";
    private static final String ACTION_LOAD_BOOKS = "LoadBooks";
    private static final String ACTION_DELETE_BOOK = "DeleteBook";
    private static final String ACTION_COLLECT_BOOK = "CollectBook";
    private static final String ACTION_CANCEL_COLLECT_BOOK = "CancelCollectBook";
    private static final String ACTION_LOAD_ALL_COLLECTED_BOOKS = "LoadAllCollectedBooks";
    private static final String ACTION_LOAD_ALL_BOOKS = "LoadAllBooks";

    private static final int BOOK_LIMIT_SIZE = 10;
    public static final int BOOK_PAGE_SIZE = 5;


    Context mContext;
    ContentResolver mResolver;
    private static ShelfController mController;
    private ShelfLoadingDialog mLoadingDialog;
    private ShelfDeleteOrCollectDialog mOperationdialog;
    private ShelfTextFontSettingDialog mTextFontSettingDialog;

    private ShelfController(Context context) {
        this.mContext = context;
        mResolver = context.getContentResolver();
    }

    public static synchronized ShelfController getInstance(Context context) {
        if (mController == null)
            mController = new ShelfController(context);

        return mController;

    }

    public void showLoadingDialog() {
        if (mLoadingDialog == null) {
            ShelfLoadingDialog.Builder builder = new Builder(mContext, R.layout.shelf_loading_dialog_view);
            mLoadingDialog = builder.create();
        }
        mLoadingDialog.show();

    }

    public void showBookCollectionOrDeleteDialog(OnClickListener listener, boolean isFavorite) {
        if (mOperationdialog != null && mOperationdialog.isShowing())
            return;
        mOperationdialog = ShelfDeleteOrCollectDialog.getBookInstance(mContext, listener, isFavorite);
        mOperationdialog.show();
    }

    public void showNewsPaperCollectionDialog(OnClickListener listener, boolean isFavorite) {
        if (mOperationdialog != null && mOperationdialog.isShowing())
            return;
        mOperationdialog = ShelfDeleteOrCollectDialog.getNewsPaperInstance(mContext, listener, isFavorite);
        mOperationdialog.show();
    }

    public void hideDeleteOrCollectDialog() {
        if (mOperationdialog != null && mOperationdialog.isShowing()) {
            mOperationdialog.dismiss();
            mOperationdialog = null;
        }
    }

    public void hideLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.hide();
        }
    }

    public void showTextFrontSettingDialog(OnClickListener listener, OnFocusChangeListener focusChangeListener, OnCancelListener cancelListener, int focus) {
        if (mTextFontSettingDialog == null) {
            mTextFontSettingDialog = ShelfTextFontSettingDialog.getInstance(mContext, listener, focusChangeListener, cancelListener, focus);
        }
        mTextFontSettingDialog.show();
    }

    public void hideTextFrontSettingDialog() {
        if (mTextFontSettingDialog != null && mTextFontSettingDialog.isShowing()) {
            mTextFontSettingDialog.dismiss();
            mTextFontSettingDialog = null;
        }
    }

    private Uri getUri(String action) {
        Uri uri = Uri.parse("content://" + URI_AUTHORITY + "/" + action);
        return uri;
    }

    // -------------------for books ------------------------------
    public List<BookCategory> loadBookCategoryInfo(String mBookColumnId) {
        List<BookCategory> categories = null;
        Cursor cursor = null;
        try {
            Uri uri = getUri(ACTION_LOAD_BOOK_CATEGORIES);

            cursor = mResolver.query(uri, null, null, new String[] { mBookColumnId }, null);
            if (cursor != null) {
                categories = new ArrayList<BookCategory>();
                BookCategory category;
                while (cursor.moveToNext()) {
                    category = new BookCategory();
                    category.Id = cursor.getString(0);
                    category.Name = cursor.getString(1);
                    categories.add(category);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return categories;
    }

    public List<Book> loadAllFavoriteBooks(String mBookColumnId) {
        return loadBooks(ACTION_LOAD_ALL_COLLECTED_BOOKS, null, null, new String[] { mBookColumnId }, null);
    }

    public List<Book> loadAllBooks(String mBookColumnId) {
        return loadBooks(ACTION_LOAD_ALL_BOOKS, null, null, new String[] { mBookColumnId }, null);
    }

    public List<Book> loadBooksByCategroyId(int startIndex, String categroyId) {
        String selection = "limit " + BOOK_PAGE_SIZE + " Offset " + startIndex;
        return loadBooks(ACTION_LOAD_BOOKS, null, selection, new String[] { categroyId }, null);
    }

    public List<Book> loadBooks(String uriStr, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        List<Book> books = null;
        Cursor cursor = null;
        try {
            Uri uri = getUri(uriStr);
            cursor = mResolver.query(uri, projection, selection, selectionArgs, sortOrder);
            if (cursor != null) {
                books = new ArrayList<Book>();
                Book book;
                while (cursor.moveToNext()) {
                    book = new Book();
                    book.Id = cursor.getString(0);
                    book.CategoryId = cursor.getString(1);
                    book.Path = cursor.getString(2);
                    book.Name = cursor.getString(3);
                    book.Cover = cursor.getString(4);
                    book.Synopsis = cursor.getString(5);
                    book.Author = cursor.getString(6);
                    book.Favorite = cursor.getString(7);
                    books.add(book);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return books;
    }

    public int deleteBook(String bookId) {
        Uri uri = getUri(ACTION_DELETE_BOOK);
        try {
            return mResolver.update(uri, new ContentValues(), null, new String[] { bookId });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int collectBook(String bookId) {
        Uri uri = getUri(ACTION_COLLECT_BOOK);
        try {
            return mResolver.update(uri, new ContentValues(), null, new String[] { bookId });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int cancelCollectBook(String bookId) {
        Uri uri = getUri(ACTION_CANCEL_COLLECT_BOOK);
        try {
            return mResolver.update(uri, new ContentValues(), null, new String[] { bookId });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    // ---------------for newspaper --------------------------

    public int collectNewsPaper(String newspaperId) {
        Uri uri = getUri(ACTION_COLLECT_NEWSPAPER);
        try {
            return mResolver.update(uri, new ContentValues(), null, new String[] { newspaperId });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int cancelCollectNewsPaper(String newspaperId) {
        Uri uri = getUri(ACTION_CANCEL_COLLECT_NEWSPAPER);
        try {
            return mResolver.update(uri, new ContentValues(), null, new String[] { newspaperId });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int addNPPPreferenc(String categoryId) {
        Uri uri = getUri(ACTION_ADD_NEWSPAPER_TO_PERSONAL_PREFERENCE);
        try {
            return mResolver.update(uri, new ContentValues(), null, new String[] { categoryId });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int RemoveNPPPreference(String categoryId) {
        Uri uri = getUri(ACTION_REMOCE_NEWSPAPER_FROM_PERSONAL_PREFERENCE);
        try {
            return mResolver.update(uri, new ContentValues(), null, new String[] { categoryId });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<NewsPaperCategory> loadAllNewsPaperCategorys(String mNewsPaperColumnId) {
        return loadNewsPaperCategorys(ACTION_LOAD_NEWSPAPER_CATEGORIES, mNewsPaperColumnId);
    }

    public List<NewsPaperCategory> loadCollectedNewsPaperCategorys(String mNewsPaperColumnId) {
        return loadNewsPaperCategorys(ACTION_LOAD_COLLECTED_NEWSPAPER_CATEGORIES, mNewsPaperColumnId);
    }

    public List<NewsPaper> loadAllNewsPapers(String mNewsPaperColumnId) {
        return loadNewsPapers(ACTION_LOAD_ALL_NEWSPAPERS, mNewsPaperColumnId);
    }

    public List<NewsPaper> loadNewsPapers(String categoryId) {
        return loadNewsPapers(ACTION_LOAD_NEWSPAPERS, categoryId);
    }

    public List<NewsPaper> loadCollectedNewsPapers(String categoryId) {
        return loadNewsPapers(ACTION_LOAD_COLLECTED_NEWSPAPERS, categoryId);
    }

    public List<NewsPaperCategory> loadNewsPaperCategorys(String action, String parentID) {

        List<NewsPaperCategory> categories = null;
        Cursor cursor = null;
        try {
            Uri uri = getUri(action);
            cursor = mResolver.query(uri, null, null, new String[]{parentID}, null);
            if (cursor != null) {
                categories = new ArrayList<NewsPaperCategory>();
                NewsPaperCategory category = null;
                NewsPaperCategory subCategory;
                String pid;
                NewsPaperCategory preference = new NewsPaperCategory();
                preference.SubCategroys = new ArrayList<NewsPaperCategory>();
                preference.Id = null;
                preference.Name = mContext.getResources().getString(R.string.personal_preference);
                categories.add(preference);
                while (cursor.moveToNext()) {
                    category = null;
                    pid = cursor.getString(1);
                    if (parentID.equals(pid) || pid == null || pid.isEmpty()) {
                        category = new NewsPaperCategory();
                        categories.add(category);
                        category.Id = cursor.getString(0);
                        category.Name = cursor.getString(2);
                    } else {
                        category = null;
                        for (NewsPaperCategory c : categories) {
                            if (pid.equals(c.Id)) {
                                category = c;
                            }
                        }
                        if (category == null) {
                            category = new NewsPaperCategory();
                            categories.add(category);
                            category.Id = pid;
                        }

                        subCategory = new NewsPaperCategory();
                        subCategory.Id = cursor.getString(0);
                        subCategory.Name = cursor.getString(2);
                        subCategory.unFocusedIcon = cursor.getString(3);
                        subCategory.Preference = cursor.getString(4);
                        subCategory.Pid = pid;

                        if (category.SubCategroys == null || category.SubCategroys.isEmpty()) {
                            category.SubCategroys = new ArrayList<NewsPaperCategory>();
                        }
                        category.SubCategroys.add(subCategory);
                        if ("1".equals(subCategory.Preference)) {
                            preference.SubCategroys.add(subCategory);
                        }
                    }

                }
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return categories;
    }

    public List<NewsPaper> loadNewsPapers(String action, String selectionArg) {
        List<NewsPaper> news = null;
        Cursor cursor = null;
        try {
            Uri uri = getUri(action);
            cursor = mResolver.query(uri, null, null, new String[] { selectionArg }, null);
            if (cursor != null) {
                news = new ArrayList<NewsPaper>();
                NewsPaper paper;
                while (cursor.moveToNext()) {
                    paper = new NewsPaper();
                    paper.Id = cursor.getString(0);
                    paper.CategoryId = cursor.getString(1);
                    paper.RootPath = cursor.getString(2);
                    paper.Name = cursor.getString(3);
                    paper.PublishTime = cursor.getString(4);
                    paper.Favarite = cursor.getString(5);
                    news.add(paper);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return news;
    }

    public HistoryInfo getNewsPaperHistoryInfo() {
        HistoryInfo info = new HistoryInfo();
        SharedPreferences preferences = mContext.getSharedPreferences("NewspaperRecord", Context.MODE_PRIVATE);
        info.MainCategoryId = preferences.getString("mainCategoryId", null);
        info.SubCategoryId = preferences.getString("subCategoryId", null);
        info.NewsPaperId = preferences.getString("newsPaperId", null);
        return info;
    }

    public void saveNewsPaperHistoryInfo(HistoryInfo info) {
        if (info == null)
            return;
        SharedPreferences preferences = mContext.getSharedPreferences("NewspaperRecord", Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString("mainCategoryId", info.MainCategoryId);
        editor.putString("subCategoryId", info.SubCategoryId);
        editor.putString("newsPaperId", info.NewsPaperId);
        editor.commit();
    }


    public String getLastSaveRecord() {
        SharedPreferences preferences = mContext.getSharedPreferences("BookRecord", Context.MODE_PRIVATE);
        String categoryId = preferences.getString("categoryId", null);
        return categoryId;
    }

    public void saveCurrentRecord(String categoryId) {
        SharedPreferences preferences = mContext.getSharedPreferences("BookRecord", Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString("categoryId", categoryId);
        editor.commit();
    }

    public Bundle getVoicedBookRecord() {
        SharedPreferences preferences = mContext.getSharedPreferences(GDataConstant.VOICEDBOOK_SHARE_PREFERENCES, Context.MODE_PRIVATE);
        Bundle bundle = new Bundle();
        bundle.putString(GDataConstant.VOICEDBOOK_ID, preferences.getString(GDataConstant.VOICEDBOOK_ID, null));
        bundle.putInt(GDataConstant.VOICEDBOOK_PAGE_ORDER, preferences.getInt(GDataConstant.VOICEDBOOK_PAGE_ORDER, 0));
        return bundle;
    }

    public void saveVoicedBookRecord(String bookId, int pageIndex) {
        SharedPreferences preferences = mContext.getSharedPreferences(GDataConstant.VOICEDBOOK_SHARE_PREFERENCES, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString(GDataConstant.VOICEDBOOK_ID, bookId);
        editor.putInt(GDataConstant.VOICEDBOOK_PAGE_ORDER, pageIndex);
        editor.commit();
    }

    public void destroy() {
        mController = null;
        if (mOperationdialog != null)
            mOperationdialog.dismiss();
        if (mLoadingDialog != null)
            mLoadingDialog.dismiss();
        if (mTextFontSettingDialog != null)
            mTextFontSettingDialog.dismiss();
        mOperationdialog = null;
        mLoadingDialog = null;
        mTextFontSettingDialog = null;
    }
}
