package com.fb.FileBrower;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.graphics.Bitmap;

class  Photo { 		
    private  Bitmap bm;  
    private  String filename;
    public  Photo(Bitmap bm,String filename) {   
        this .bm = bm;  
        this .filename = filename;
    }   
   
    public  Bitmap getBm() {   
        return  bm;   
    }   
    public  String getFilename() {   
        return  filename;   
    } 
    public   void  setBm(Bitmap bm) {   
        this .bm = bm;   
    }	
    public   void  setFilename(String filename) {   
        this .filename = filename;   
    }	
}   
	interface  PhotoDecodeListener {   
	public   void  onPhotoDecodeListener(Photo photo);   
}	

	class DecodePhoto {
		int decodeNo = 0;
		private static final DecodePhoto decodephoto = new DecodePhoto();
		private static List<String> myImageURL = null;
		private List<Photo> photos = new ArrayList<Photo>();
		public static DecodePhoto getInstance() {
			//setImageURL(filelist);
			return decodephoto;
		}	
		public static List<String> setImageURL(List<String> filelist){
			myImageURL = filelist;
			return myImageURL;
			
		}
		public List<Photo> decodeImage(PhotoDecodeListener listener,List<String> flist){
			Bitmap bm = null;
			Photo photo = null;		
			String filename;
			try{	
				for(int i=0;i<flist.size();i++){
					File f = new File(flist.get(i));
					filename = f.getName();				
					if(FileOp.isPhoto(filename)){
						bm = FileOp.fitSizePic(f);			    	
					}	
					else{
						bm = null;
					}
					photo = new Photo(bm,flist.get(i));					
					listener.onPhotoDecodeListener(photo);	
					photos.add(photo);
				}
			}catch  (Exception e) {   
                throw   new  RuntimeException(e);   
			} 
			return photos;
	
	}
}
	
	
