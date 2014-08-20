package com.dbstar.multiple.media.data;

import java.util.List;

public class BookCategory {
    
   public String Id;
   public  String Name;
   public List<Book []> PageBooks;
   public List<Book> BookList;
   public int LastIndex;
   public int PageCount;
   public int PageNumber;
   
   
   public void recyle(){
       if(PageBooks != null){
           PageBooks.clear();
       }
       PageBooks = null;
       if(BookList != null)
           BookList.clear();
       BookList = null;
       LastIndex = 0;
       PageCount = 0;
       PageNumber = 0;
       
   }
}
