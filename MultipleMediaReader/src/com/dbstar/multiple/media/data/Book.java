package com.dbstar.multiple.media.data;

public class Book {
    
    
    public String Id;
    public String Cover;
    public String Name;
    public String Synopsis;
    public String CategoryId;
    public String RMCategory;
    public String Path;
    public String Author;
    public long  lastUpdateTime ;
    public String Favorite;
    
    @Override
    public boolean equals(Object o) {
        if(o instanceof Book){
            Book book = (Book) o;
            if(Id != null)
                return Id.equals(book.Id);
        }
        return super.equals(o);
    }
    
}
