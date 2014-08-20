package com.dbstar.multiple.media.model;

import com.dbstar.multiple.media.common.GDataConstant;

public class ModelVoicedBook {
    
    public static final String VOICEDBOOK_TABLE_NAME = GDataConstant.TABLE_VOICEDBOOK;
    
    public static final String BOOK_ID = GDataConstant.VOICEDBOOK_ID;
    public static final String PAGE_ID = GDataConstant.VOICEDBOOK_PAGE_ID;
    public static final String PAGE_IMAGES = GDataConstant.VOICEDBOOK_PAGE_IMAGES;
    public static final String PAGE_AUDIOS = GDataConstant.VOICEDBOOK_PAGE_AUDIOS;
    public static final String PAGE_LABEL = GDataConstant.VOICEDBOOK_PAGE_LABEL;
    public static final String PAGE_LEVEL = GDataConstant.VOICEDBOOK_PAGE_LEVEL;
    public static final String PAGE_TITLE = GDataConstant.VOICEDBOOK_PAGE_TITLE;
    public static final String PAGE_ORDER = GDataConstant.VOICEDBOOK_PAGE_ORDER;
    
    public static final String LANGUAGE_SEPARATOR = GDataConstant.POUND_SIGN;
    
    public static interface Query{
        String [] COLUMNS = new String[]{ ModelVoicedBook.BOOK_ID,ModelVoicedBook.PAGE_ID,ModelVoicedBook.PAGE_IMAGES,ModelVoicedBook.PAGE_AUDIOS,ModelVoicedBook.PAGE_LABEL,ModelVoicedBook.PAGE_LEVEL,ModelVoicedBook.PAGE_TITLE,ModelVoicedBook.PAGE_ORDER};
        String SELECTION = ModelVoicedBook.BOOK_ID + " = ? ";
        String ORDERBY = ModelVoicedBook.PAGE_ORDER;
        
        int BOOK_ID = 0;
        int PAGE_ID = 1;
        int PAGE_IMAGES = 2;
        int PAGE_AUDIOS = 3;
        int PAGE_LABLE = 4;
        int PAGE_LEVEL = 5;
        int PAGE_TITLE = 6;
        int PAGE_ORDER = 7;
    }
    
    public enum Label{
        
        MARKED("1"),UNMARKED("0"),LAST_READ_UNMARKED("2"),LAST_READ_MARKED("3");
        
        public String value;
        Label(String value){
            this.value = value;    
        }
            
    }
    
    public enum Language{
        CHINESE('c'),ENGLISH('e');
        
        public int value;
        Language(int value){
            this.value = value;
        }
    }
}
