package com.media.reader.dialog;

/**
 * interface need to be implements by any type of dialog
 * @author w00170766
 *
 */
public interface IDialogView
{
    /** 
     * The function is used to show the dialog.
     */
    void show();
    
    /** 
     * The function is used to close the dialog.
     */
    void close();
}
