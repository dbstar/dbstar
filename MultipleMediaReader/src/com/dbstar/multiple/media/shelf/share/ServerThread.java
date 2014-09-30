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
 * @version $Id: ServerThread.java 727 2011-01-02 13:04:32Z markus $
 */

package com.dbstar.multiple.media.shelf.share;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import android.content.Context;
import android.os.Handler;

import com.dbstar.multiple.media.util.GLog;

public class ServerThread extends Thread {
	private ServerSocket listener = null;
	private boolean running = true;
	private String mIp;
	private int mPort;
	public static LinkedList<Socket> clientList;
	private Context context;
	private String mBookColumnId;
	private String mNewsPaperColumnId;
	
    public ServerThread(String ip, int port, Handler handler,Context context) throws IOException {
		super();
		GLog.getLogger("Futao").i("new ServerThread ip = " + ip);
		InetAddress ipadr = InetAddress.getByName(ip);
		listener = new ServerSocket(port,0,ipadr);
		mIp = ip;
		mPort = port;
		clientList = new LinkedList<Socket>();
		this.context = context;
	}
    
    public void setColumn(String book,String newspaper){
        this.mBookColumnId = book;
        this.mNewsPaperColumnId = newspaper;
    }
    private static void send(String s) {
       GLog.getLogger("Futao").i(s);
    }
    
    public String getIp(){
        return mIp;
    }
    public int getClientCount(){
        if(clientList != null)
            return clientList.size();
        return 0;
    }
	@Override
	public void run() {
		while( running ) {
			try {
				send("Waiting for connections.");
				Socket client = listener.accept();
			    send("New connection fr" +
			    		"om " + client.getInetAddress().toString());
			    
				new ServerHandler(client,mIp,mPort,mBookColumnId,mNewsPaperColumnId,context).start();
				clientList.add(client);
			} catch (IOException e) {
				send(e.getMessage());
			}
		}
	}

	public void stopServer() {
	    GLog.getLogger("Futao").i("stopServer");
		running = false;
		try {
			listener.close();
			clientList.clear();
			clientList = null;
		} catch (IOException e) {
			send(e.getMessage());
		}
	}
	
	public synchronized static void remove(Socket s) {
	    send("Closing connection: " + s.getInetAddress().toString());
        clientList.remove(s);      
    }
}
