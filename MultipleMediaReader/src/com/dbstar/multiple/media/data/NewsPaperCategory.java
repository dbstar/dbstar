package com.dbstar.multiple.media.data;

import java.io.Serializable;
import java.util.List;

public class NewsPaperCategory implements Serializable{
    
    public String Id;
    public String Name;
    public String Pid;
    public String FocusedIcon;
    public String unFocusedIcon;
    public String Preference;
    
    public List<NewsPaperCategory> SubCategroys;
    public List<NewsPaper> NewsPapers;
    public NewsPaper CurrentNewsPaper;
    public String CurrentSubCategoryId;
    
    public List<NewsPaperMap> mPages;
    public int mCurrentPageIndex;
    public int mPostion;
    
    public NewsPaper getCurrentNewsPaper(){
        if(mPages == null || mPages.isEmpty()){
            return null;
        }
        return mPages.get(mCurrentPageIndex).getNewsPaper(mPostion);
    }
    
    public NewsPaperMap getCurrentPageMap(){
        if(mPages == null || mPages.isEmpty()){
            return null;
        }
       return mPages.get(mCurrentPageIndex);
    }
}
