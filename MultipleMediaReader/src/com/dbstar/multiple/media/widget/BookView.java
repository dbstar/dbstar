package com.dbstar.multiple.media.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.dbstar.multiple.media.common.ImageManager;
import com.dbstar.multiple.media.common.ImageManager.ImageCallback;
import com.dbstar.multiple.media.data.Book;
import com.dbstar.multiple.media.shelf.R;

public class BookView extends ImageView{
    private final static String TAG = "BookView";
    public  int mLeft,mTop,mRight,mBottom;
    public int width, height;
    private Book mBook;
    public boolean mIsWillRemove;
    
    public BookView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
    }   
    
    
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }


    public void onInit(Book book) {
        mBook = book;
        String viewKey = book.Id;
        setTag(viewKey);
        ImageManager.getInstance(getContext()).getBitmapDrawable(book.Cover, new ImageCallback( ) {
            
            @Override
            public void imageLoaded(BitmapDrawable imageDrawable, String viewKey) {
               if(((String)getTag()).equals(viewKey) && imageDrawable.getBitmap() != null){
                   setImageBitmap(imageDrawable.getBitmap());
               }else{
                   setImageResource(R.drawable.book_bg_01);
               }
            }
        }, viewKey);
        reset();
    }


    public void reset() {
        mLeft = getLeft();
        mTop = getTop();
        mRight = getRight();
        mBottom = getBottom();
        width = getWidth();
        height = getHeight();
    }


    public void clear() {
       setImageResource(R.drawable.book_bg_01);
    }


    public Book getBookInfo() {
        // TODO Auto-generated method stub
        return mBook;
    }
}
