package com.dbstar.multiple.media.shelf.share;

import java.util.List;

import android.util.Log;

public class PageFactory {
    
    private static PageFactory mFactory;
    private PageFactory(){};
    
    public synchronized static PageFactory getInstance(){
        if(mFactory == null)
            mFactory = new PageFactory();
        return mFactory;
    }

    
    public static byte[] getIndexPage(String ipAddress){
    	StringBuffer sb = new StringBuffer();
    	sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    	sb.append("<feed xmlns=\"http://www.w3.org/2005/Atom\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:opensearch=\"http://a9.com/-/spec/opensearch/1.1/\" xmlns:app=\"http://www.w3.org/2007/app\" xmlns:thr=\"http://purl.org/syndication/thread/1.0\" xmlns:opds=\"http://opds-spec.org/2010/catalog\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xml:lang=\"en\">\n");
    	sb.append("<id>http://www.feedbooks.com/catalog.atom</id>\n");
    	sb.append("<title>卫星书屋</title>\n");
    	sb.append("<icon>/e/books.png</icon>\n");
    	sb.append("<entry>\n");
    	sb.append("<title>图书</title>\n");
    	sb.append("<link type=\"image/png\" href=\"/e/books.png\" rel=\"\"/>\n");
    	sb.append("<link type=\"application/atom+xml;profile=opds-catalog;kind=acquisition\" href=\"e/ts.html\" rel=\"\"/>\n");
    	sb.append("<id11>http://www.feedbooks.com/books/top.atom?range=week</id11>\n");
    	sb.append("<content type=\"text\">在这里，可以看到你想要的图书</content>\n");
    	sb.append("</entry>\n");
    	sb.append("<entry>\n");
    	sb.append("<title>报纸</title>\n");
    	sb.append("<link type=\"application/atom+xml;profile=opds-catalog;kind=navigation\" href=\"e/bz.html\" rel=\"\"/>\n");
    	sb.append("<id11>http://www.feedbooks.com/store/selection.atom</id11>\n");
    	sb.append("<content type=\"text\">一报在手，知晓天下事</content>\n");
    	sb.append("</entry>\n");
    	sb.append("<entry>\n");
    	sb.append("<title>期刊</title>\n");
    	sb.append("<link type=\"application/atom+xml;profile=opds-catalog;kind=navigation\" href=\"e/qk.html\" rel=\"\"/>\n");
    	sb.append("<id11>http://www.feedbooks.com/store/selection.atom</id11>\n");
    	sb.append("<content type=\"text\">在这里，可以看到你喜欢的期刊杂志</content>\n");
    	sb.append("</entry>\n");
    	sb.append("</feed>\n");
    	
    	Log.d("PageFactory", "-----index.html = " + sb.toString());    	
    	return sb.toString().getBytes();
    }
    
    public static byte[] getDownLoadListPage(List<LoadData> datas,String ipAddress){
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<feed xmlns:opensearch=\"http://a9.com/-/spec/opensearch/1.1/\" xml:lang=\"en\" xmlns:opds=\"http://opds-spec.org/2010/catalog\" xmlns:app=\"http://www.w3.org/2007/app\" xmlns=\"http://www.w3.org/2005/Atom\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:thr=\"http://purl.org/syndication/thread/1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");
		sb.append("<id>http://www.feedbooks.com/store/top.atom</id>\n");
		sb.append("<title>Best Selling</title>\n");
		sb.append("<icon>http://assets1.feedbooks.net/images/favicon.ico?t=1401282854</icon>\n");
		
		for (LoadData loadData : datas) {
			sb.append("<entry>\n");
			sb.append("<title>" + loadData.Name + "</title>\n");
			sb.append("<id>" + loadData.Id + "</id>\n");
			sb.append("<author>\n" + "<name>" + loadData.Author + "</name>\n<uri></uri>\n" + "</author>\n");
			sb.append("<link type=\"image/jpeg\" href=\"" + loadData.Cover + "\" rel=\"\"/>\n");
			sb.append("<link type=\"text/html\" href=\"https://www.feedbooks.com/item/205763/buy\" rel=\"http://opds-spec.org/acquisition/buy\">\n");
			sb.append("<opds:indirectAcquisition type=\"application/vnd.adobe.adept+xml\">\n");
			sb.append("<opds:indirectAcquisition type=\"application/epub+zip\"/>\n");
			sb.append("</opds:indirectAcquisition>\n");
			sb.append("</link>\n");
			sb.append("<link type=\"application/epub+zip\" href=\"" + loadData.FilePath + "\" rel=\"\"/>\n");
			sb.append("</entry>\n");
		}
		
		sb.append("</feed>");
		return sb.toString().getBytes();
    }
    public static byte[] getDownLoadListPage(List<LoadData> books,List<LoadData> papers,String ipAddress,int port){
        StringBuffer sb = new StringBuffer();
        sb.append("<html> ");
        sb.append("<meta name='viewport' content ='width=device-width, initial-scale=1.0,user-scalable=yes'/>");
        sb.append("<meta name='apple-mobile-web-app-capable' content='yes' />");
        sb.append("<meta name='apple-mobile-web-app-status-bar-style' content='black' />");
        sb.append("<meta name='apple-touch-fullscreen' content='yes'/>");
        sb.append("<meta content='telephone=no' name='format-detection' />");
        sb.append("<meta http-equiv='content-type' content='text/html;charset=utf-8'>");
        sb.append(" <head><title></title>");
        sb.append("</head>");
        sb.append("<body>");
        sb.append("<table width = '100%' border = '0'>");
        sb.append("<tr> <th>名称</th> <th>日期</th> <th>大小</th> <th>操作</th> </tr>");
        sb.append("<tr align='center'>");
        sb.append("<td align='center' width = '100%' colspan = '4' style='white-space: nowrap' ><b>图书</b></td>");
        sb.append("</tr>");
        for(LoadData loadData: books){
            sb.append("<tr>");
            sb.append("<td align='center' style='white-space: nowrap' >" + loadData.Name + "</td>");
            sb.append("<td align='center' style='white-space: nowrap'>" + loadData.Date + "</td>");
            sb.append("<td align='center' style='white-space: nowrap'>" + loadData.Size + "</td>");
            sb.append("<td align='center' style='white-space: nowrap'><input type = 'button' value = '下载' onclick=\"location='http://"+ipAddress+ ":" + port+"/"+ServiceApi.ACTION_LOAD_ITEM +"?="+ loadData.FilePath +"'\"/></td>");
            sb.append("</tr>");
        }
        sb.append("<tr align='center'>");
        sb.append("<td align='center'  width = '100%' colspan = '4' style='white-space: nowrap' ><b>报纸</b></td>");
        sb.append("</tr>");
        for(LoadData loadData: papers){
            sb.append("<tr>");
            sb.append("<td align='center' style='white-space: nowrap' >" + loadData.Name + "</td>");
            sb.append("<td align='center' style='white-space: nowrap'>" + loadData.Date + "</td>");
            sb.append("<td align='center' style='white-space: nowrap'>" + loadData.Size + "</td>");
            sb.append("<td align='center'style='white-space: nowrap'><input type = 'button' value = '下载' onclick=\"location='http://"+ipAddress+ ":" + port+"/"+ServiceApi.ACTION_LOAD_ITEM +"?="+ loadData.FilePath +"'\"/></td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        sb.append("</body></html>");
        return sb.toString().getBytes();
    }
}
