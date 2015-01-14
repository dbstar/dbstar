package com.media.reader.vo;

import com.media.zlibrary.text.view.ZLTextLineInfo;


public class ImageEntity {
	public enum ImageAlign{
		IMAGE_ALIGN_CENTER,//just middle 0
		IMAGE_ALIGN_MID,//real center 1
		IMAGE_ALIGN_RIGHT, // 2
		IMAGE_ALIGN_LEFT,//3
		IMAGE_ALIGN_TEXT//4 no align, for text
	}
	
	public final int width;
	public final int height;
	public final ImageAlign align;
	private float scale;
	private int textHeight = 0;
	public final ZLTextLineInfo lineInfo;
	private boolean isOverHeight = false;
	private int lineHeight = 0;
	private String ID = null;
	public ImageEntity(ZLTextLineInfo info, String id,int w, int h, ImageAlign alignvalue) {
		lineInfo = info;
		ID=id;
		width = w;
		height = h;
		align = alignvalue;
	}
	
	public boolean isEmbedImage(){
		if(align==ImageAlign.IMAGE_ALIGN_LEFT||
				align==ImageAlign.IMAGE_ALIGN_RIGHT){
			return true;
		}else{
			return false;
		}
	}
	
	public void setLineHeight(int h){
		lineHeight = h;
	}
	
	public int getLineHeight(){
		return lineHeight;
	}
	
	public String getPath(){
		return ID;
	}
	public void resetTextAreaHeight(){
		isOverHeight = false;
		textHeight = 0;
	}
	
	public boolean isReduceTextLineWidth(){
		return !isOverHeight;
	}
	
	public void setOverHeight(boolean flag){
		isOverHeight = flag;
	}
	
	public int getCurTextHeight(){
		return textHeight;
	}
	
	public boolean addTextLineHeight(int height){
		if(isOverHeight)
			return false;
		textHeight+=height;
		if(textHeight>this.height){
			isOverHeight = true;
			return false;
		}else{
			return true;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((align == null) ? 0 : align.hashCode());
		result = prime * result + height;
		result = prime * result
				+ ((lineInfo == null) ? 0 : lineInfo.hashCode());
		result = prime * result + width;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
//		if (getClass() != obj.getClass())
//			return false;
		ImageEntity other = (ImageEntity) obj;
		if (lineInfo == null) {
			if (other.lineInfo != null)
				return false;
		} else if (lineInfo.RealStartElementIndex!=other.lineInfo.RealStartElementIndex&&
				lineInfo.ParagraphCursor!=null&&
				other.lineInfo.ParagraphCursor!=null&&
				lineInfo.ParagraphCursor.Index!=
				other.lineInfo.ParagraphCursor.Index){
			return false;
		}
		if (ID==null){
			if (other.ID != null)
				return false;
		}else if(!ID.equals(other.ID)){
			return false;
		}
		
		if (height != other.height)
			return false;
		
		if (width != other.width)
			return false;
		
		if (align != other.align)
			return false;
		return true;
	}
}
