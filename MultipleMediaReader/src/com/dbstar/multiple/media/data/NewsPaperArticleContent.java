package com.dbstar.multiple.media.data;
import java.util.ArrayList;


public class NewsPaperArticleContent {
    
    public ArrayList<Block> blocks;
    
    public ArrayList<Patch> patchs;
    
    public ArrayList<Title> titles;
    
    
    public static class Title{
        public int level;
        public String text;
    }
    
    public static class Block {
        
        public int type;
        public String value;
        public int x;
        public int y;
    }
    public static class Patch {
        public int startIndex,endIndex;
    }
}
