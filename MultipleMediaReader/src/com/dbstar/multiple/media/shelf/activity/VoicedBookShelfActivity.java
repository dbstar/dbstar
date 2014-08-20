package com.dbstar.multiple.media.shelf.activity;

import android.content.Intent;

import com.dbstar.multiple.media.data.Book;

public class VoicedBookShelfActivity extends BookShelfActivity {
    
    @Override
    protected void startReadActivity(Book book) {
        Intent intent = new Intent(this, VoicedBookReadActivity.class);
        intent.putExtra("FilePath", book.Path);
        intent.putExtra("BookId", book.Id);
        startActivity(intent);
    }
}
