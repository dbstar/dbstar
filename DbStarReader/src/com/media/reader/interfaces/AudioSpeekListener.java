package com.media.reader.interfaces;

public interface AudioSpeekListener {
	public void onEnd();
	public void onError(int errorCode,String msg);
}
