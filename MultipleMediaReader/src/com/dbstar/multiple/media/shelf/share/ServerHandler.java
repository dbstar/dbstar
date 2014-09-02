/*
 * Copyright (C) 2009,2010 Markus Bode Internetl�sungen (bolutions.com)
 * 
 * Licensed under the GNU General Public License v3
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * @author Markus Bode
 * @version $Id: ServerHandler.java 727 2011-01-02 13:04:32Z markus $
 */
package com.dbstar.multiple.media.shelf.share;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.dbstar.multiple.media.common.ShelfController;
import com.dbstar.multiple.media.data.Book;
import com.dbstar.multiple.media.data.NewsPaper;

class ServerHandler extends Thread {
  private BufferedReader in;
  private PrintWriter out;
  private Socket toClient;
  private String IpAddress;
  private Context context;
  private String mBookColumnId;
  private String mNewsPaperColumnId;
  ServerHandler(Socket s,String ip,int port ,String bookColumn,String newspaperColunm,Context context) {
    toClient = s;
    IpAddress = ip;
    this.mBookColumnId = bookColumn;
    this.mNewsPaperColumnId = newspaperColunm;
    this.context = context;
    
  }

  private String formatFileSize(long length) {
      String result = null;
      int sub_string = 0;
      if (length >= 1073741824) {
          sub_string = String.valueOf((float) length / 1073741824).indexOf(
                  ".");
          result = ((float) length / 1073741824 + "000").substring(0,
                  sub_string + 3) + "GB";
      } else if (length >= 1048576) {
          sub_string = String.valueOf((float) length / 1048576).indexOf(".");
          result = ((float) length / 1048576 + "000").substring(0,
                  sub_string + 3) + "MB";
      } else if (length >= 1024) {
          sub_string = String.valueOf((float) length / 1024).indexOf(".");
          result = ((float) length / 1024 + "000").substring(0,
                  sub_string + 3) + "KB";
      } else if (length < 1024)
          result = Long.toString(length) + "B";
      
      return result;
  }

  public void run() {
	String dokument = "";

    try {
      in = new BufferedReader(new InputStreamReader(toClient.getInputStream()));

      // Receive data
      while (true) {
        String s = in.readLine().trim();

        if (s.equals("")) {
          break;
        }
        
        if (s.substring(0, 3).equals("GET")) {
        	int leerstelle = s.indexOf(" HTTP/");
        	dokument = s.substring(5,leerstelle);
        	dokument = dokument.replaceAll("[/]+","/");
        }
      }
    }
    catch (Exception e) {
    	ServerThread.remove(toClient);
    	try
		{
    		toClient.close();
		}
    	catch (Exception ex){}
    }
    Log.i("ServerHandler", dokument);

		if (dokument.equals("") || dokument.equalsIgnoreCase("index.html") || dokument.equalsIgnoreCase("e.html")) {
			
			String headerBase = "HTTP/1.1 %code%\n"
					+ "Content-Length: %length%\n" 
					+ "Connection:close \n"
					+ "Content-Type: text/html  charset=utf-8;\n\n";
			
			dokument = "index.html";
			
			File file = new File(ServiceApi.WEB_ROOT + dokument);
			Log.i("ServerHandler", "------dokument-------is null-----file.exists() = " + file.exists());
			if (file.exists()) {
				try {
					// 把文件的内容读取到in对象中
					FileInputStream in = new FileInputStream(file);
					byte content[] = new byte[(int) file.length()];
					in.read(content);

					String header = headerBase;
					header = headerBase.replace("%code%", "200 OK");
					header = header.replace("%length%", String.valueOf(content.length));

					BufferedOutputStream out = null;
					out = new BufferedOutputStream(toClient.getOutputStream());
					out.write(header.getBytes());
					out.write(content);
					out.flush();
					ServerThread.remove(toClient);
					toClient.close();
					out.close();
					in.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			String headerBase = "HTTP/1.1 %code%\n"
					+ "Content-Length: %length%\n"
					+ "Connection:close \n"
					+ "Content-Type: text/html  charset=utf-8;\n\n";
			if (dokument.contains(ServiceApi.ACTION_DOWNLOAD_BOOK_LIST)) {
				Log.i("ServerHandler", "------dokument.contains(ts.html)-");
				List<Book> books = ShelfController.getInstance(this.context).loadAllBooks(mBookColumnId);
				Log.i("ServerHandler", "----------ts.html-and books = " + books);
				List<LoadData> loadDatas = new ArrayList<LoadData>();
				if (books != null) {
					LoadData loadData;
					for (int i = 0; i < books.size(); i++) {
						Book b = books.get(i);
						loadData = new LoadData();
						loadData.Id = b.Id;
						loadData.Name = b.Name;
						Log.i("ServerHandler", "------b.Name========-" + b.Name);
						loadData.Cover = b.Cover;
						loadData.Author = b.Author;
						loadData.FilePath = b.Path;
						// loadData.Date = "2013";
						File file = new File(loadData.FilePath);
						Log.i("ServerHandler", "------FilePath========-" + loadData.FilePath + " is exists (" + file.exists() + ")");
						if (file.exists()) {
							loadData.Size = formatFileSize(file.length());
						} else {
							continue;
						}
						loadDatas.add(loadData);
					}
				}
				populateDataToClient(headerBase, loadDatas);
			} else if (dokument.contains(ServiceApi.ACTION_DOWNLOAD_NEWSPAPER_LIST)) {
				Log.i("ServerHandler", "-----dokument.contains(bz.html)-----");
				List<NewsPaper> papers = ShelfController.getInstance(this.context).loadAllNewsPapers(mNewsPaperColumnId);
				Log.i("ServerHandler", "----------bz.html-and papers = " + papers);
				List<LoadData> loadDatas = new ArrayList<LoadData>();
				if (papers != null) {
					LoadData loadData;
					for (int i = 0; i < papers.size(); i++) {
						NewsPaper b = papers.get(i);
						loadData = new LoadData();
						loadData.Name = b.Name;
						loadData.Author = b.PublishTime;
						loadData.FilePath = b.RootPath + ".epub";
//						loadData.Date = b.PublishTime;
						File file = new File(loadData.FilePath);
						if (file.exists()) {
							loadData.Size = formatFileSize(file.length());
						} else {
							continue;
						}
						loadDatas.add(loadData);
					}
				}
				
				populateDataToClient(headerBase, loadDatas);

			} else /*if(dokument.contains(ServiceApi.ACTION_LOAD_ITEM)) */ {
				String  fileName = null;;
				String  filePath = null;
				String headerBase1 = "HTTP/1.1 %code%\n"+
						"content-disposition:attachment;filename ="+ fileName + "\n" + 
						"Content-Length: %length%\n"+
						"Connection:Keep-Alive \n"+
						"Content-Type: application/octet-stream  charset=utf-8;\n\n";
				String header = headerBase1;
				header = headerBase1.replace("%code%", "200 OK");
				
				filePath = "../../../../" + dokument;
				Log.i("ServerHandler", "filePath = " + filePath);
				try {
					File f = new File(filePath);
					if(f.exists()){
						BufferedInputStream in = new BufferedInputStream(new FileInputStream(f));
						BufferedOutputStream out = new BufferedOutputStream(toClient.getOutputStream());
						header = header.replace("%length%", ""+in.available());
						Log.i("ServerHandler", header);
						out.write(header.getBytes());
						byte[] buf = new byte[4096];
						int count = 0;
						while ((count = in.read(buf)) != -1){
							out.write(buf,0,count);
						}
						out.flush();
						ServerThread.remove(toClient);
						toClient.close();
					}else{
						header = headerBase;
						header = header.replace("%code%", "404 File not found");            
						header = header.replace("%length%", ""+"404 - File not Found".length());            
						out = new PrintWriter(toClient.getOutputStream(), true);
						out.print(header);
						out.print("404 - File not Found");
						out.flush();
						out.flush();
						ServerThread.remove(toClient);
						toClient.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void populateDataToClient(String headerBase, List<LoadData> loadDatas) {
		byte[] content = PageFactory.getDownLoadListPage(loadDatas, IpAddress);
		String header = headerBase;
		header = headerBase.replace("%code%", "200 OK");
		header = header.replace("%length%", "" + content.length);
		BufferedOutputStream out = null;
		try {
			out = new BufferedOutputStream(toClient.getOutputStream());
			out.write(header.getBytes());
			out.write(content);
			out.flush();
			ServerThread.remove(toClient);
			toClient.close();
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
}
	
//	// Don't allow directory traversal
//	if (dokument.indexOf("..") != -1)
//	    dokument = "403.html";
//	
//	dokument = "/storage/external_storage/sda1/com.bolutions.webserver/" + dokument;
//	dokument = dokument.replaceAll("[/]+","/");
//	if(dokument.charAt(dokument.length()-1) == '/') 
//		dokument = "/storage/external_storage/sda1/com.bolutions.webserver/404.html";
//	
//	String headerBase = "HTTP/1.1 %code%\n"+
//	"ServerThread: Bolutions/1\n"+
//	"Content-Length: %length%\n"+
//	"Connection:close \n"+
//	"Content-Type: text/html  charset=utf-8;\n\n";
//
//	String header = headerBase;
//	header = header.replace("%code%", "403 Forbidden");
//
//	try {
//    	File f = new File(dokument);
//        if (!f.exists()) {
//        	header = headerBase;
//        	header = header.replace("%code%", "404 File not found");
//        	dokument = "404.html";
//        }
//    }
//    catch (Exception e) {}
//
//    if (!dokument.equals("/storage/external_storage/sda1/com.bolutions.webserver/403.html")) {
//    	header = headerBase.replace("%code%", "200 OK");
//    }
//	
//    try {
//      File f = new File(dokument);
//      if (f.exists()) {
//    	  BufferedInputStream in = new BufferedInputStream(new FileInputStream(f));
//    	  BufferedOutputStream out = new BufferedOutputStream(toClient.getOutputStream());
//    	  header = header.replace("%length%", ""+in.available());
//    	  Log.i("ServerHandler", header);
//    	  out.write(header.getBytes());
//    	  byte[] buf = new byte[4096];
//    	  int count = 0;
//    	  while ((count = in.read(buf)) != -1){
//    	      out.write(buf,0,count);
//    	  }
//    	  out.flush();
//      }
//      else
//      {
//          // Send HTML-File (Ascii, not as a stream)
//    	  header = headerBase;
//    	  header = header.replace("%code%", "404 File not found");	    	  
//    	  header = header.replace("%length%", ""+"404 - File not Found".length());	    	  
//          out = new PrintWriter(toClient.getOutputStream(), true);
//          out.print(header);
//    	  out.print("404 - File not Found");
//    	  out.flush();
//      }
//
//      ServerThread.remove(toClient);
//      toClient.close();
//    }
//    catch (Exception e) {
//    	
//    }
//  }
}
