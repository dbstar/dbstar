package com.dbstar.multiple.media.data;

import android.R.color;

public class NewsPaperMap {
    int capacity;
    String [] dateValue;
    NewsPaper [] newsPaper;
    int cursor;
    public NewsPaperMap(int capacity) {
        this.capacity = capacity;
        dateValue = new String[capacity];
        newsPaper = new NewsPaper[capacity];
        cursor = 0;
    }
    
    public void put(String key,NewsPaper value){
        if(cursor < capacity){
            int index = cursor ++;
            dateValue[index] = key;
            newsPaper[index] = value;
        }
    }
    public String getDateValue(int index){
        if(index < cursor)
            return dateValue[index];
        return null;
    }
    
    public NewsPaper getNewsPaper(int index){
        if(index < cursor)
            return newsPaper[index];
        return null;
    }
    
    public  String [] getDateValues (){
        return dateValue;
    }
    
    public int getSize(){
        return capacity;
                
    }
}
