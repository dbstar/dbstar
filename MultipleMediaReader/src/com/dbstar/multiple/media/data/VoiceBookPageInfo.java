package com.dbstar.multiple.media.data;

import android.util.SparseArray;

import com.dbstar.multiple.media.model.ModelVoicedBook;

public class VoiceBookPageInfo {
    
    public String PageId;
    public String PagePath;
    public String Image;
    public String Audio;
    public String Label = ModelVoicedBook.Label.UNMARKED.value;
    public String Level;
    public String Title;
    public String Order;
    public int PageIndex;
    public SparseArray<String> mAudios = new SparseArray<String>(2);
    
    public String combineAudios(){
        int key;
        String value;
        StringBuilder sb = new StringBuilder();
        if (mAudios.size() > 0) {
        	for(int i = 0;i < mAudios.size();i++){
        		key = mAudios.keyAt(i);
        		value = mAudios.valueAt(i);
        		sb.append(Character.toString((char)key));
        		sb.append(ModelVoicedBook.LANGUAGE_SEPARATOR);
        		sb.append(value);
        		sb.append(ModelVoicedBook.LANGUAGE_SEPARATOR);
        	}
        	return sb.deleteCharAt(sb.length()-1).toString();
        }
          return sb.toString();          
    }
    
    public void departAudios(String combineVudios){
        if(combineVudios != null && !combineVudios.isEmpty()){
            String arr [] = combineVudios.split(ModelVoicedBook.LANGUAGE_SEPARATOR);
            mAudios.clear();
            for(int i = 0;i < arr.length ;i++){
                mAudios.append(arr[i].toCharArray()[0], arr[++i]);
            }
        }
    }
    public String getSpecificAudios(int lanugage){
        String audio = mAudios.get(lanugage);
        if(audio == null)
            audio = mAudios.valueAt(0);
        return audio;
    }
}
