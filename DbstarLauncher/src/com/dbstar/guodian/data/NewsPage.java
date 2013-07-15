package com.dbstar.guodian.data;

import java.util.ArrayList;
import java.util.List;

public class NewsPage {
    public List<GDNews> news = new ArrayList<GDNews>();
    public int TotalPageItems;
    public int TotalPages;
    public int CurrentPageNum;
    public String NextPageURL;
    public String PrePageURL;
}
