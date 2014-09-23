package com.dbstar.multiple.media.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.text.Html;
import android.util.Log;
import android.util.Xml;

import com.dbstar.multiple.media.data.NewsPaperArticleContent;
import com.dbstar.multiple.media.data.NewsPaperArticleContent.Block;
import com.dbstar.multiple.media.data.NewsPaperArticleContent.Patch;
import com.dbstar.multiple.media.data.NewsPaperArticleContent.Title;
import com.dbstar.multiple.media.data.NewsPaperPage;
import com.dbstar.multiple.media.data.VoiceBookPageInfo;

public class EPUBParser {
    
    private static final String TAG_TITLE = "docTitle";
    private static final String TAG_NAVMAP = "navMap";
    private static final String TAG_NAVPOINT = "navPoint";
    private static final String TAG_TEXT = "text";
    private static final String TAG_CONTENT = "content";
    private static final String TAG_SRC = "src";
    private static final String TAG_ID = "id";
    private static final String TAG_ORDER = "playOrder";
    private static final String TAG_PAGE_IMG = "img";
    
    public static boolean isLeftSymbol;
    public static boolean hasLeftSymbol;
    
    public static List<NewsPaperPage> parseNewsPaperPagePicSrc(String RootPath,List<NewsPaperPage> data){
        try {
            NewsPaperPage edition;
            for(int i = 0,count = data.size();i< count ;i++){
                edition = data.get(i);
                InputStream in = new FileInputStream(edition.path);
                XmlPullParser parser = Xml.newPullParser();  
                parser.setInput(in, "utf-8");  
                int event = parser.getEventType();//
                while(event!=XmlPullParser.END_DOCUMENT){
                    switch (event) {

                    case XmlPullParser.START_TAG:
                        String tagName = parser.getName();
                        if(TAG_PAGE_IMG.equals(tagName)){
                            edition.PicPath = parser.getAttributeValue(null, TAG_SRC);
                            edition.PicPath = parsePath(edition.path,edition.PicPath);
                        }
                        break;
                        
                    }
                    
                    event = parser.next();
                }
                
            }
            
        } catch (Exception e) {
            
        }
        return data;
    }
    public static String getNCXPath(String rootPath){
        String metainfoxml = rootPath + "/META-INF/container.xml";
        String path = null;
        try {
            InputStream in = new FileInputStream(metainfoxml);
            XmlPullParser parser = Xml.newPullParser();  
            parser.setInput(in, "utf-8");  
            int event = parser.getEventType();//
            while(event!=XmlPullParser.END_DOCUMENT){
                switch (event) {
                case XmlPullParser.START_TAG:
                    String tagName = parser.getName();
                    if(tagName.equals("rootfile"));
                        path = parser.getAttributeValue(null, "full-path");
                    break;
                }
                event = parser.next();
            }
            
          if(path != null){
              String opfPath = rootPath + "/" + path;
              InputStream inputStream = new FileInputStream(opfPath);
              XmlPullParser parser2 = Xml.newPullParser();  
              parser2.setInput(inputStream, "utf-8");  
              path = null;
              int event2 = parser2.getEventType();//
              while(event2!=XmlPullParser.END_DOCUMENT){
                  switch (event2) {
                  case XmlPullParser.START_TAG:
                      String tagName = parser2.getName();
                      if(tagName.equals("item"));
                          String id = parser2.getAttributeValue(null, "id");
                          if(id != null && id.equals("ncx")){
                              path = parser2.getAttributeValue(null, "href");
                          }
                      break;
                  }
                  event2 = parser2.next();
              }
              
              if(path != null){
                  path = getParentPath(opfPath, 1)+ path;
              }
          }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }
    
    public static List<VoiceBookPageInfo> parseVoiceBookPageInfo(String RootPath,String path){
        List<VoiceBookPageInfo> list = null;
        try {
            int enterlevel = 0;
            InputStream in = new FileInputStream(path);
            XmlPullParser parser = Xml.newPullParser();  
            parser.setInput(in, "utf-8");  
            VoiceBookPageInfo page = null;
            int event = parser.getEventType();//
            while(event!=XmlPullParser.END_DOCUMENT){
                switch (event) {
                case XmlPullParser.START_DOCUMENT:{
                    list = new ArrayList<VoiceBookPageInfo>();
                    break;
                }
                case XmlPullParser.START_TAG:{
                    String tagName = parser.getName();
                    if(TAG_NAVPOINT.equals(tagName)){
                       enterlevel++;
                       page = new VoiceBookPageInfo();
                       page.Level = enterlevel +"";
                       page.PageId = parser.getAttributeValue(null, TAG_ID);
                       page.Order = parser.getAttributeValue(null, TAG_ORDER);
                       list.add(page);
                    }else if(TAG_TEXT.equals(tagName)){
                        if(page != null)
                            page.Title = parser.nextText();
                        
                    }else if(TAG_CONTENT.equals(tagName)){
                        if (page != null) {
                            page.PagePath =  getParentPath(path, 1) + parser.getAttributeValue(null, TAG_SRC);
                            if (page.PagePath.contains(".html#ncx")) {
                                int endIndex = page.PagePath.indexOf("#ncx");
                                page.PagePath = page.PagePath.substring(0, endIndex);

                            }
                        }
                    }
                    break;
                }   
                case XmlPullParser.END_TAG: {
                    String tagName = parser.getName();
                    if (TAG_NAVPOINT.equals(tagName)) {
                        enterlevel--;
                    }
                    break;
                }
                }
                
                event = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  list;
    }
    
    public static void parseVoicedBookContent(VoiceBookPageInfo info){
          try {
              InputStream in = new FileInputStream(info.PagePath);
              XmlPullParser parser = Xml.newPullParser();  
              parser.setInput(in, "utf-8");  
              
              int event = parser.getEventType();//
              while(event!=XmlPullParser.END_DOCUMENT){
                  switch (event) {
                  case XmlPullParser.START_TAG:
                      String tagName = parser.getName();
                       if(tagName.equals("img")){
                          info.Image = parsePath(info.PagePath,  parser.getAttributeValue(null, "src"));
                      }else if(tagName.equals("audio")){
                          String src = parser.getAttributeValue(null, "src");
                          if(src != null){
                              int lastPoint = src.lastIndexOf('.');
                              int languageCode =  src.codePointAt(lastPoint-1);
                              info.Audio = parsePath(info.PagePath,  src);
                              info.mAudios.put(languageCode, info.Audio);
                          }
                                  
                      }
                      break;
                      
                  }
                  
                  event = parser.next();
              }
          } catch (Exception e) {
              e.printStackTrace();
          }
      }
    public static List<NewsPaperPage> parseNewsPaperPage(String RootPath,String path){
        List<NewsPaperPage> list = null;
        try {
            int enterlevel = 0;
            InputStream in = new FileInputStream(path);
            XmlPullParser parser = Xml.newPullParser();  
            parser.setInput(in, "utf-8");  
             NewsPaperPage page = null;
             NewsPaperPage article = null;
            int event = parser.getEventType();//
            while(event!=XmlPullParser.END_DOCUMENT){
                switch (event) {
                case XmlPullParser.START_DOCUMENT:{
                    list = new ArrayList<NewsPaperPage>();
                    break;
                }
                case XmlPullParser.START_TAG:{
                    String tagName = parser.getName();
                    if(TAG_NAVPOINT.equals(tagName)){
                       if(enterlevel == 0){
                           page = new NewsPaperPage();
                           list.add(page);
                       }else{
                           article = new NewsPaperPage();
                       }
                       enterlevel++;
                    }else if(TAG_TEXT.equals(tagName)){
                        if (enterlevel == 1) {
                            if(page != null)
                                page.title = parser.nextText();
                        } else {
                            if(article != null)
                                article.title = parser.nextText();
                        }
                    }else if(TAG_CONTENT.equals(tagName)){
                        if (enterlevel == 1) {
                            if (page != null) {
                                page.path =  getParentPath(path, 1) + parser.getAttributeValue(null, TAG_SRC);
                                if (page.path.contains(".html#ncx")) {
                                    int endIndex = page.path.indexOf("#ncx");
                                    page.path = page.path.substring(0, endIndex);

                                }
                            }
                        } else {
                            if (article != null) {
                                article.path = getParentPath(path, 1) + parser.getAttributeValue(null, TAG_SRC);
                                if (article.path.contains(".html#ncx")) {
                                    int endIndex = article.path.indexOf("#ncx");
                                    article.path = article.path.substring(0, endIndex);

                                }
                                page.mArticles.add(article);
                            }
                        }
                    }
                    break;
                }   
                case XmlPullParser.END_TAG: {
                    String tagName = parser.getName();
                    if (TAG_NAVPOINT.equals(tagName)) {
                        enterlevel--;
                        if (enterlevel == 1)
                            article = null;
                        if (enterlevel == 0)
                            page = null;
                    }
                    break;
                }
                }
                
                event = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  list;
    }

	/**
	 * 将一个字符串转化为输入流
	 */
	public static InputStream getStringStream(String inputString) {
		if (inputString != null && !inputString.trim().equals("")) {
			try {
				ByteArrayInputStream stream = new ByteArrayInputStream(inputString.getBytes());
				return stream;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 将一个输入流转化为字符串
	 */
	public static String getStreamString(InputStream inputStream) {
		if (inputStream != null) {
			try {
				BufferedReader tBufferedReader = new BufferedReader(
						new InputStreamReader(inputStream));
				StringBuffer stringBuffer = new StringBuffer();
				String sTempOneLine = new String("");
				while ((sTempOneLine = tBufferedReader.readLine()) != null) {
					stringBuffer.append(sTempOneLine);
				}
				return stringBuffer.toString();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}
    
    public static NewsPaperArticleContent parseNewsPaperContent(String path){
        NewsPaperArticleContent data = null;
        Title title =null;
          try {
              InputStream in = new FileInputStream(path);
              String inToString = getStreamString(in);
              String newString = inToString.replace("<br>", "\n");
              InputStream inputStream = getStringStream(newString);
              XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
              parser.setInput(inputStream, "utf-8");  
              
              int event = parser.getEventType();//
              while(event!=XmlPullParser.END_DOCUMENT){
                  switch (event) {
                  case XmlPullParser.START_DOCUMENT:
                      break;
                      
                  case XmlPullParser.START_TAG:
                      String tagName = parser.getName();
                      if(tagName.equalsIgnoreCase("body")){
                          data = new NewsPaperArticleContent();
                          data.blocks = new ArrayList<Block>();
                          data.patchs = new ArrayList<Patch>();
                          data.titles = new ArrayList<NewsPaperArticleContent.Title>();
                      }else{
                          if(tagName.equalsIgnoreCase("h2")){
                              title = new Title ();
                              title.level = 2;
                              title.text = parser.nextText();
                              data.titles.add(title);
                          }else if(tagName.equalsIgnoreCase("h3")){
                              title = new Title();
                              title.level = 3;
                              title.text = parser.nextText();
                              data.titles.add(title);
                          }else if(tagName.equalsIgnoreCase("p")){
                        	  
                        	  String content = null;
                        	  if (parser.next() == XmlPullParser.TEXT) {
                        		  content = parser.getText() + "\n";
                            	  parser.nextTag();
                        	  }
                        	  
                              pasreContent(data, content);
                          } else if(tagName.equalsIgnoreCase("img")) {
                              Block block = new Block();
                              block.type = 4;
                              block.value = parsePath(path,  parser.getAttributeValue(null, "src"));
                              data.blocks.add(block);
                          } else {
                        	  /*if(tagName.equalsIgnoreCase("font")) */
                        	  if (parser.next() == XmlPullParser.TEXT) {
                        		  String content = parser.getText() + "\n";
                        		  pasreContent(data, content);                        		  
                        	  }
                          } 
                      }
                      break;
                      
                  case XmlPullParser.END_TAG:
                       tagName = parser.getName();
                  }
                  event = parser.next();
              }
          } catch (Exception e) {
              e.printStackTrace();
          }
          return  data;
      }
    
    private static String getParentPath(String path,int hierarchy){
        if(path == null)
            return null;
        for(int i = 0;i< hierarchy;i++){
            path = path.substring(0,path.lastIndexOf("/"));
        }
        return path + "/";
    }
    private static void pasreContent(NewsPaperArticleContent data,String content){
        
        if(content == null || content.isEmpty())
            return;
        if (data == null)
        	return;
        Patch patch = new Patch();
        if(data.patchs.size() > 0){
            patch.startIndex =  data.patchs.get(data.patchs.size()-1).endIndex +1;
        }else{
            patch.startIndex = 0;
        }
        data.patchs.add(patch);
        Block block = null;
        char v;
        isLeftSymbol = false;
        hasLeftSymbol = false;
        char arr []  = content.toCharArray();
        for(int i = 0;i< arr.length ;i ++){
            v =  arr[i];
            if(isSymbol(v)){
                if(v == '&' && arr[i +1] == '#'){
                    block = new Block();
                    block.type = 2;
                    block.value = String.valueOf(v);
                   i ++;
                   do{ 
                       v = arr[i];
                       block.value = block.value + String.valueOf(v);
                       i ++;
                   }while(v != ';');
                   data.blocks.add(block);
                   i --;
                   continue;
                }else if(isLeftSymbol(v)){
                    if(hasLeftSymbol){
                        block.value = block.value + String.valueOf(v);
                        hasLeftSymbol = false;
                        continue;
                    }
                    isLeftSymbol = true;
                    hasLeftSymbol = true;
                        continue;
                    
                } else if(isRightSymbol(v)){
                    if(hasLeftSymbol){
                        block.value = block.value + String.valueOf(v);
                        hasLeftSymbol = false;
                    }
                    continue;
                }else if(isDelimiter(v)){
                    if(!hasLeftSymbol && !isPoint(arr, i)){
                        patch.endIndex = data.blocks.size() -1;
                        if(i < arr.length-1){
                            patch = new Patch();
                            patch.startIndex =  data.blocks.size();
                            data.patchs.add(patch);
                        }
                    }
                }
                
                block.value = block.value + String.valueOf(v);
               
            }else{
               block = new Block();
               block.type = 0;
               if(isLeftSymbol){
                   if(isRightSymbol(arr[i +1])){
                       block.value = String.valueOf(arr[i -1])+ String.valueOf(v) +String.valueOf(arr[i  + 1]);
                       i ++;
                       hasLeftSymbol =false;
                   }else{
                       block.value = String.valueOf(arr[i -1])+ String.valueOf(v);
                   }
                   isLeftSymbol = false;
               }
               else
                   block.value =String.valueOf(v);
               data.blocks.add(block);
            }
        }  
        patch.endIndex = data.blocks.size() -1;
    }
    private static String parsePath(String filePath,String src){
        URL absoluteUrl;
        try {
            absoluteUrl = new URL("file:"+ filePath);
            URL parseUrl = new URL(absoluteUrl ,src ); 
            String imagSrc = parseUrl.toString();
            return imagSrc.replace("file:", "");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
            return src;
        }
    
    private static boolean isPoint(char arr [] ,int i){
        
        if(i>0 && i < arr.length-1){
            char preChar = arr[i -1];
            char lastChar = arr[i +1];
            
            if(isNumber(preChar) && isNumber(lastChar))
                return true;
        }
        return false;
    }
    private static boolean isSymbol(char c) {
        String str = String.valueOf(c);
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？\"\"]";
        if (str.matches(regEx)) {
            return true;
        } else {
            return false;
        }

    }
    private static boolean isNumber(char c){
       return Character.isDigit(c);
    }
    private static boolean isLeftSymbol(char c) {
        String str = String.valueOf(c);
        String regEx = "[({'\\[<（【‘“\"]";
        if (str.matches(regEx)) {
            return true;
        } else {
            return false;
        }

    }
    private static boolean isRightSymbol(char c) {
        String str = String.valueOf(c);
        String regEx = "[)}'\\]>）}】”’\"]";
        if (str.matches(regEx)) {
            return true;
        } else {
            return false;
        }

    }
    
    private static boolean isDelimiter(char c) {
        String str = String.valueOf(c);
        String regEx = "[.?。？]";
        if (str.matches(regEx)) {
            return true;
        } else {
            return false;
        }

    }
}
