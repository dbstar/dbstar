package com.dbstar.multiple.media.data;

import java.util.List;

public class NewsPaper {
    
    public String Id;
    public String Name;
    public String RootPath;
    public String CataloguePath;
    public String PublishTime;
    public String CategoryId;
    public String Favarite;
    
    public int SelectedIndex = -1;
    public int PaddinTop;
    public List<NewsPaperPage> Pages;
}
