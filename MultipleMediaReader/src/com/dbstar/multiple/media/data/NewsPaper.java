package com.dbstar.multiple.media.data;

import java.io.Serializable;
import java.util.List;

public class NewsPaper implements Serializable{
    
    public String Id;
    public String Name;
    public String RootPath;
    public String CataloguePath;
    public String PublishTime;
    public String CategoryId;
    public String Favarite;
    public String PosterPath;
    
    public int SelectedIndex = -1;
    public int PaddinTop;
    public List<NewsPaperPage> Pages;
}
