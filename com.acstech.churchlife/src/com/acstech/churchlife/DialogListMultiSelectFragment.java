package com.acstech.churchlife;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;


public class DialogListMultiSelectFragment extends DialogFragment {

	// Event that gets raised when alert dialog is closed
	public interface OnDismissListener
	{
	    public abstract void onDismiss(boolean[] itemsSelected);
	}
	
	private OnDismissListener _dismissListener = null;
	
	private String _title = "";
	private String[] _items = null;
	protected boolean[] _selections = null;
	
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

	public boolean[] getSelections() {
		return _selections;
	}
	
	public void setSelections(boolean[] value) {	
		_selections = value;		
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
	       .setMultiChoiceItems(_items, _selections, new DialogInterface.OnMultiChoiceClickListener(){
               public void onClick(DialogInterface dialog, int whichButton, boolean isChecked) {            	   
            	   _selections[whichButton] = isChecked;
               }
           })	       
	       .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	                @Override
	                public void onClick(DialogInterface dialog, int which) {	                	
	                	if (_dismissListener != null) {
	                		_dismissListener.onDismiss(_selections);
	                	}
	                    dialog.dismiss();
	                }	                       
	       	})	     	     
	       .create();    	
	}
	
}
