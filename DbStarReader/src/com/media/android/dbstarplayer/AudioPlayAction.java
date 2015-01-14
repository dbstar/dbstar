package com.media.android.dbstarplayer;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.iflytek.tts.TtsService.Tts;
import com.media.dbstarplayer.dbstarplayer.ActionCode;
import com.media.dbstarplayer.dbstarplayer.DbStarPlayerApp;
import com.media.reader.interfaces.AudioSpeekListener;
import com.media.zlibrary.core.application.ZLApplication;
import com.media.zlibrary.core.view.ZLView.Animation;
import com.media.zlibrary.text.view.ZLTextElement;
import com.media.zlibrary.text.view.ZLTextFixedPosition;
import com.media.zlibrary.text.view.ZLTextWord;
import com.media.zlibrary.text.view.ZLTextWordCursor;

public class AudioPlayAction extends DbStarAndroidAction{
	public static final int PLAY=0;
	public static final int PAUSE=1;
	public static final int RESUME=2;
	public static final int CANCEL=3;
	private DbStarPlayer baseActivity;
	boolean isEndText=false;
	private DbStarPlayerApp Reader;
	private int actionCode;
	public static boolean isAudioSpeek = false;
	private boolean isNeedTurnPage = false;
	private ZLTextWordCursor mCurPageEndCursor = null;
	public AudioPlayAction(DbStarPlayer baseActivity,DbStarPlayerApp dbStarPlayerApp,int actionCode) {
		super(baseActivity,dbStarPlayerApp);
		this.baseActivity=baseActivity;
		this.Reader=dbStarPlayerApp;
		this.actionCode=actionCode;
	}
//	enum {
//		PLAY_STATUS_STOP = 0,
//		PLAY_STATUS_SPEAK,
//		PLAY_STATUS_FINISH
//	};
	private void cancelAudioSpeak(){
//		Toast.makeText(baseActivity, "Stop Audio Play Right Now!", Toast.LENGTH_SHORT).show();
		isAudioSpeek = false;
		if(Tts.isInitialized()){
			Tts.JniStop();
		}
//		Tts.JniDestory();
//		Reader.clearTextCaches();
		Reader.getTextView().clearHighlighting();
		Reader.getViewWidget().repaint();
	}
	
	public  synchronized void startReadThread(final Context ctx, final AudioSpeekListener listener){
		class TtsRunThread implements Runnable{			
			@Override
			public void run() {
				int ret;
				while(isAudioSpeek){
					ret = -1;
					if(Reader.mCursorAudioSpeek != null && 
							!Reader.mCursorAudioSpeek.isNull() && 
							!Reader.mCursorAudioSpeek.isEndOfText()){
						String txt=getText(Reader.mCursorAudioSpeek);
						if(!TextUtils.isEmpty(txt)){
							try{
								Tts.initTtsEngine();
								ret = Tts.JniSpeak(txt);
								if(ret!=0){
									listener.onError(ret,"Speek Error");
									break;
								}else{
									if(isNeedTurnPage){
										if(sendAndcheckPageTurnStatus()){
											isNeedTurnPage = false;
											continue;
										}else{
											listener.onError(-1, "turn page failed！");
											break;
										}
									}
								}
							}catch(Exception e){
								listener.onError(-1,e.getMessage());
								e.printStackTrace();
								break;
							}
						}else{
							if(!Reader.mCursorAudioSpeek.isEndOfText()){
								if(isNeedTurnPage){
									if(sendAndcheckPageTurnStatus()){
										isNeedTurnPage = false;
										continue;
									}else{
										listener.onError(-1, "turn page failed！");
										break;
									}
								}
							}
							listener.onError(-1, "get text to read failed!");
							break;
						}
					}else{
						listener.onEnd();
						break;
					}
				}
			}			
		}		
		final Thread ttsRun = (new Thread(new TtsRunThread()));
		ttsRun.setPriority(Thread.MAX_PRIORITY);
		ttsRun.start();
		Log.e("AudioPlayAction", "startReadThread mThreadId="+ttsRun.getId());
	}
	private final int MSG_REACH_END = 0;
	private final int MSG_SPEEK_ERROR=1;
	private final int MSG_TURN_PAGE_FORWARD = 2;
	Handler mHandler = new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
				case MSG_REACH_END:
					Toast.makeText(baseActivity, (String)msg.obj, Toast.LENGTH_SHORT).show();
					cancelAudioSpeak();
					break;
				case MSG_SPEEK_ERROR:
					Toast.makeText(baseActivity, (String)msg.obj, Toast.LENGTH_SHORT).show();
					cancelAudioSpeak();
					break;
				case MSG_TURN_PAGE_FORWARD:
					Reader.runAction(ActionCode.TURN_PAGE_FORWARD);
					break;
			}
		}
	};
	
	private long lastonKeyTime = 0l;
	private String TAG = "AudioPlayAction";
	@Override
	protected void run(Object... params) {
//		Log.d(TAG , "--------------System.currentTimeMillis() - lastonKeyTime = " + (System.currentTimeMillis() - lastonKeyTime));
		if (System.currentTimeMillis() - lastonKeyTime > 2000l) {
//			Log.d(TAG, "--------------System.currentTimeMillis() - lastonKeyTime > " + 2000);
			if(this.actionCode==CANCEL){//如果是取消
				if(isAudioSpeek){
					cancelAudioSpeak();
				}
				baseActivity.cancelAudioPlay();
			}else if(this.actionCode==PLAY){
				Log.e(getClass().getSimpleName(), "Instance:"+this.toString());
				if(ZLApplication.Instance().getViewWidget().isTurningPage()){
					Log.e(getClass().getSimpleName(), "Is turning page, no speak!!!");
					return;
				}
				if(isAudioSpeek){
					cancelAudioSpeak();
					return;
				}
				baseActivity.setAudioPlay();
				if(Reader.mCursorAudioSpeek==null){
					Reader.mCursorAudioSpeek = new ZLTextWordCursor(Reader.BookTextView.getStartCursor());
					if(Reader.mCursorAudioSpeek==null){
						/**
						 * maybe not finish loading the book
						 * 
						 * */
						Log.e(getClass().getSimpleName(), "Reader.mCursorAudioSpeek==null return");
						return;
					}
				}
				isAudioSpeek = true;
				startReadThread(baseActivity,new AudioSpeekListener() {
					
					@Override
					public void onError(int errorCode, String msgStr) {
						// TODO Auto-generated method stub
						if(!TextUtils.isEmpty(msgStr)){
							Message msg = new Message();
							msg.what = MSG_SPEEK_ERROR;
							msg.obj = msgStr;
							msg.arg1 = errorCode;
							mHandler.sendMessage(msg);
						}
					}
					
					@Override
					public void onEnd() {
						// TODO Auto-generated method stub
						Message msg = new Message();
						msg.what = MSG_REACH_END;
						msg.obj = baseActivity.getResources().getString(R.string.tip_read_book_finished);
						msg.arg1 = 0;
						mHandler.sendMessage(msg);
					}
				});
			}else if(this.actionCode==PAUSE){
				cancelAudioSpeak();
			}else if(this.actionCode==RESUME){
			}
			lastonKeyTime = System.currentTimeMillis();
		} else  {
			Log.d(TAG, "--------------System.currentTimeMillis() - lastonKeyTime < 2s");					
		}
	}
	private String getText(ZLTextWordCursor cursor){
		if(cursor==null||cursor.isNull()){
			return null;
		}
		return getSentenceText(cursor);
	}
	private String getSentenceText(ZLTextWordCursor cursor){
		final ZLTextFixedPosition position=new ZLTextFixedPosition(cursor);
		final StringBuilder sentenceBuilder = new StringBuilder();
		final StringBuilder phraseBuilder = new StringBuilder();
		boolean isReadEnd = false;
		while(cursor != null && !cursor.isNull() && !cursor.isEndOfText()){
//			System.out.println("cursor.getParagraphCursor().isLast()="+cursor.getParagraphCursor().isLast());
//			System.out.println("cursor.isEndOfParagraph()="+cursor.isEndOfParagraph());
			while(cursor.isEndOfParagraph()){
				if(!cursor.nextParagraph()){
					break;
				}
			}
			if(cursor.getParagraphCursor().isLast()){
				isReadEnd = true;
				break;
			}
			final ZLTextElement element = cursor.getElement();
//			if(element instanceof ZLTextImageElement){
////				Reader.runAction(ActionCode.TURN_PAGE_FORWARD);
//				mHandler.sendEmptyMessage(MSG_TURN_PAGE_FORWARD);
//			}
			if (element instanceof ZLTextWord) {
				final ZLTextWord word = (ZLTextWord)element;
				phraseBuilder.append(word.Data, word.Offset, word.Length);
				char temp=word.Data[word.Offset + word.Length - 1];
				switch (temp) {
					case '.':
					case '!':
					case '?':
					case '"':
					case '。':
					case '？':
					case '！' :
					case '”':
						sentenceBuilder.append(phraseBuilder);
						phraseBuilder.delete(0, phraseBuilder.length());
						break;
				}
				sentenceBuilder.append(phraseBuilder);
				phraseBuilder.delete(0, phraseBuilder.length());
				if(temp=='.'||temp=='!'||temp=='?'||temp=='。'||temp=='？'||temp=='！'||temp=='"'||temp=='”'){
					phraseBuilder.delete(0, phraseBuilder.length());
					cursor.nextWord();
					break;
				}
			}
			if(isPageEnd(cursor)){
				if(null==mCurPageEndCursor){
					mCurPageEndCursor = new ZLTextWordCursor();
				}
				mCurPageEndCursor.setCursor(getPageEndCursor());
				isNeedTurnPage=true;
				break;
			}
			cursor.nextWord();
		}
		if(!isReadEnd){
			Reader.BookTextView.highlight(position, cursor);
		}
		return sentenceBuilder.toString();
	}	
	
	private boolean isPageEnd(ZLTextWordCursor cursor){
		final ZLTextWordCursor endCursor=getPageEndCursor();
		if(endCursor.compareTo(cursor)<=0){
			return true;
		}else{
			return false;
		}
	}
	
	private ZLTextWordCursor getPageEndCursor(){
		final Animation type = ZLApplication.Instance().getViewWidget().getCurAnimationType();
		if(type==Animation.realdouble){
			return Reader.BookTextView.getDoublePageEndCursor();
		}else{
			return Reader.BookTextView.getEndCursor();
		}
	}
	
	boolean isCheck = false;
	private boolean sendAndcheckPageTurnStatus(){
		if(isCheck){
			return true;
		}else{
			isCheck=true;
		}
		mHandler.sendEmptyMessage(MSG_TURN_PAGE_FORWARD);
		boolean isTurned = false;
		ZLTextWordCursor pageEndCursor=null;
		long beginTime = System.currentTimeMillis();
		//waiting for turning page process
		int getCount=0;
		while(true){
			final long consume = System.currentTimeMillis()-beginTime;
			if(consume<500*getCount){
				if(consume>5000){
					isTurned = false;
					break;
				}
				continue;
			}
			
			pageEndCursor = getPageEndCursor();
			getCount++;
			if(!pageEndCursor.isNull()){
				if(pageEndCursor.compareTo(mCurPageEndCursor)>0){
					//page already turned
					mCurPageEndCursor.setCursor(pageEndCursor);
					isTurned = true;
					break;
				}
			}
		}
		isCheck = false;
		return isTurned;
	}
	
}
