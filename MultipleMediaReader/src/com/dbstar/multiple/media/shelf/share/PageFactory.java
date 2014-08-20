package com.dbstar.multiple.media.shelf.share;

import java.util.List;

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
        sb.append("<html>");
        sb.append("<meta http-equiv='content-type' content='text/html;charset=utf-8'>");
        sb.append("<meta name='viewport' content ='width=device-width, initial-scale=1.0,user-scalable=yes'/>");
        sb.append("<meta name='apple-mobile-web-app-capable' content='yes' />");
        sb.append("<meta name='apple-mobile-web-app-status-bar-style' content='black' />");
        sb.append("<meta name='apple-touch-fullscreen' content='yes'/>");
        sb.append("<meta content='telephone=no' name='format-detection' />");
        sb.append("<meta http-equiv='content-type' content='text/html;charset=utf-8'>");
        sb.append("<head><title></title>");
        sb.append("</head>");
        sb.append("<body>");
        sb.append("<table width = '100%' height = '100%' border = '0'>");
        sb.append("<tr>");
        sb.append("<td align='center'>");
        sb.append("<table border = '0'>");
        sb.append("<tr>");
        sb.append("<td align='center'><input type = 'button' value = '图书'  style='font-size:50px' onclick=\"location='http://" +ipAddress + ":" + 8080 +"/" + ServiceApi.ACTION_DOWNLOAD_BOOK_LIST + "'\"/></td>");
        sb.append("</tr>");
        sb.append("<tr>");
        sb.append("<td  align='center'><input type = 'button'  style='font-size:50px' value = '报纸'  onclick=\"location='http://" +ipAddress +":" + 8080+ "/" + ServiceApi.ACTION_DOWNLOAD_NEWSPAPER_LIST + "'\"/></td>");
        sb.append("</tr>");
        sb.append("</table>");
        sb.append("</td>");
        sb.append("</tr>");
        sb.append("</table>");
        sb.append("</body></html>");
        return sb.toString().getBytes();
    }
    
    public static byte[] getDownLoadListPage(List<LoadData> datas,String ipAddress){
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
        for(LoadData loadData: datas){
            sb.append("<tr>");
            sb.append("<td align='center' style='white-space: nowrap' >" + loadData.Name + "</td>");
            sb.append("<td align='center' style='white-space: nowrap'>" + loadData.Date + "</td>");
            sb.append("<td align='center' style='white-space: nowrap'>" + loadData.Size + "</td>");
            sb.append("<td align='center'style='white-space: nowrap'><input type = 'button' value = '下载' onclick=\"location='http://"+ipAddress+ ":" + 8080+"/"+ServiceApi.ACTION_LOAD_ITEM +"?="+ loadData.FilePath +"'\"/></td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        sb.append("</body></html>");
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
