package com.dbstar.multiple.media.data;

public class HistoryInfo {
    
    public String MainCategoryId;
    public String SubCategoryId;
    public String NewsPaperId;
    public boolean isNewsPaperIdIsNull(){
       return NewsPaperId == null || NewsPaperId.length() <= 0;
    }
}
