package com.dbstar.guodian.engine;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SendThread extends Thread {

	Lock mCmdLock = null;
	LinkedList<String> mCmdQueue = null;
	boolean mIsWaited = false;
	AtomicBoolean mExit;
	
	private Socket mSocket = null;
	private BufferedWriter mOut = null;

	public SendThread(Socket socket, BufferedWriter out) {
		mCmdLock = new ReentrantLock();
		mCmdQueue = new LinkedList<String>();
		mExit = new AtomicBoolean();
		mExit.set(false);
		
		mSocket = socket;
		mOut = out;
	}

	public void sendCmd(String cmd) {
		try {
			mCmdLock.lock();
			mCmdQueue.add(cmd);

			if (mCmdQueue.size() == 1) {
				notify();
			}
		} finally {
			mCmdLock.unlock();
		}
	}

	private String dequeueCmd() {
		String cmd = null;
		try {
			mCmdLock.lock();
			if (mCmdQueue.size() == 0) {
				wait();
			}
			cmd = mCmdQueue.poll();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			mCmdLock.unlock();
		}

		return cmd;
	}

	public void setExit() {
		mExit.set(true);
	}

	public void run() {

		while(mExit.get()) {
			String cmd = dequeueCmd();
			
			if (mSocket.isConnected()) {
				try {
					mOut.write(cmd);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
