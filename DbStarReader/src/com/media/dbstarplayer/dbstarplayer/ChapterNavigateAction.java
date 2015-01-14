package com.media.dbstarplayer.dbstarplayer;

import java.util.ArrayList;

import com.media.reader.vo.TocReference;
import com.media.zlibrary.core.application.ZLApplication;

public class ChapterNavigateAction extends DbStarAction {

	private final boolean mAhead;
	
	public ChapterNavigateAction(DbStarPlayerApp dbstarplayer, boolean flag) {
		super(dbstarplayer);
		// TODO Auto-generated constructor stub
		mAhead = flag;
	}

	private int findCurChapteIndexByCurrentPage(ArrayList<TocReference> tocList,int index){
		if(null==Reader.Model){
			return -1;
		}
		final int size = tocList.size();
		if(size<=0){
			return -1;
		}
		if(index==0){
			return -1;
		}
		for(int i=0;i<size;i++){
			final TocReference tocCur = tocList.get(i);
			final TocReference tocNext;
			if(i+1<size){
				tocNext = tocList.get(i+1);
			}else{
				/**
				 * should be the last chapter
				 * */
				if(index>=tocCur.mRef.ParagraphIndex){
					return i;
				}else{
					return i-1;
				}
			}
			if(index<tocCur.mRef.ParagraphIndex&&i==0){
				return -1;
			}
			if(index>=tocCur.mRef.ParagraphIndex&&
					(index<tocNext.mRef.ParagraphIndex||
							/**
							 * this is the last chapter with content
							 * */
							tocNext.mRef.ParagraphIndex<=0)){
				return i;
			}
		}
		return -1;
	}
	
	
	@Override
	protected void run(Object... params) {
		// TODO Auto-generated method stub
		if(ZLApplication.Instance().getViewWidget().isTurningPage()){
			return;
		}
		final ArrayList<TocReference> tocList =  (ArrayList<TocReference>) Reader.getChapterList();
		int curIndex = findCurChapteIndexByCurrentPage(tocList,Reader.getTextView().getStartCursor().getParagraphIndex());
		
		
		if(mAhead){
			if(curIndex<tocList.size()-1){
				curIndex++;
			}
			else{
				curIndex = tocList.size()-1;
			}
		}else{
			if(curIndex>0){
				curIndex--;
			}else{
				Reader.BookTextView.gotoPosition(0, 0, 0);
				Reader.showBookTextView();
				curIndex = 0;
				return;
			}
		}
		Reader.BookTextView.gotoPosition(tocList.get(curIndex).mRef.ParagraphIndex, 0, 0);
		Reader.showBookTextView();
	}

}
