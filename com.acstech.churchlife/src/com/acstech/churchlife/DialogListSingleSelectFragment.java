package com.acstech.churchlife;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;


public class DialogListSingleSelectFragment extends DialogFragment {

	// Event that gets raised when alert dialog is closed
	public interface OnDismissListener
	{
	    public abstract void onDismiss(int itemSelected);
	}
	
	private OnDismissListener _dismissListener = null;
	
	private String _title = "";
	private String[] _items = null;
	private int[] _itemValues = null;	// used if items have a numeric value that is to be returned upon selection
	protected int _selection =-1;
	
	// Event setter
	public void setOnDimissListener(OnDismissListener l) 	{
		_dismissListener = l;
	}
		
	public String getTitle() {
		return _title;
	}
	
	public void setTitle(String value) {
		_title = value;
	}
	
	public String[] getItems() {
		return _items;
	}
	
	public void setItems(String[] value) {
		_items = value;	
	}	

	public int[] getItemValues() {
		return _itemValues;
	}
	
	public void setItemValues(int[] value) {
		_itemValues = value;	
	}
	
	// position of selected
	public int getSelection() {
		return _selection;
	}
		
	// position of selected 
	public void setSelection(int value) {	
		_selection = value;		
	}
	
	// value of selected or -1 if not exists
	public int getSelectionValue()
	{
		if (_itemValues != null) {
			return _itemValues[_selection];			
		}
		else {
			return _selection;
		}		
	}
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
	    setCancelable(true);
	    int style = DialogFragment.STYLE_NORMAL, theme = 0;
	    setStyle(style, theme);
	}
		
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	return new AlertDialog.Builder(getActivity())
	       .setTitle(_title)
	       .setSingleChoiceItems(_items, _selection, new DialogInterface.OnClickListener(){
               public void onClick(DialogInterface dialog, int whichButton) {            	   
            	   _selection = whichButton;
            	   if (_dismissListener != null) {
               		_dismissListener.onDismiss(getSelectionValue());
               	}
                   dialog.dismiss();
               }
           })	       
           /*
	       .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	                @Override
	                public void onClick(DialogInterface dialog, int which) {	                	
	                	if (_dismissListener != null) {
	                		_dismissListener.onDismiss(_selections);
	                	}
	                    dialog.dismiss();
	                }	                       
	       	})
	       	*/	     	     
	       .create();    	
	}
	
}
