package com.media.reader.dialog;

import android.app.Service;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * A subclass of LinearLayout.
 * @author 
 *
 */
public abstract class DialogBaseLayout extends LinearLayout implements
        IDialogView
{
    
    /**
     *The system's layout inflater.
     */
    protected LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
    
    /**
     * The system's window manager.
     */
    protected WindowManager mWindowManager = (WindowManager) getContext().getSystemService(Service.WINDOW_SERVICE);
    
    
    public DialogBaseLayout(Context context)
    {
        super(context);
    }
    
    public final void show()
    {
        doShow();
    }
    
    public void doShow()
    {
    	WindowManager.LayoutParams wlp = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);
        wlp.gravity = Gravity.CENTER;
        mWindowManager.addView(this, wlp);
    }
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
    	/**
    	 * close the dialog when menu pressed
    	 * 
    	 * */
    	if(event.getKeyCode()==KeyEvent.KEYCODE_MENU){
//    		close();
		}
    	/*****************************************/
        return super.dispatchKeyEvent(event);
    }
    
    /** 
     * The function is used to remove the view.
     */
    public void close()
    {
        doRemoveView();
    }
    
    /** 
     * The function is used to remove the view.
     */
    protected void doRemoveView()
    {
        if (this.isShown())
        {
            WindowManager mWindowManager = (WindowManager) getContext().getSystemService(Service.WINDOW_SERVICE);
            try{
            	mWindowManager.removeView(this);
            }catch(Exception e){
            	
            }
        }
    }
}
