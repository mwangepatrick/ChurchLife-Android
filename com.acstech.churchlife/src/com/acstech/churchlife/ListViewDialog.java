package com.acstech.churchlife;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.os.Bundle;


public class ListViewDialog extends DialogFragment {

	private String _title = "";
	private String[] _items = null;
	
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

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
	    setCancelable(true);
	    int style = DialogFragment.STYLE_NORMAL, theme = 0;
	    setStyle(style, theme);
	}
	
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

    	//android.R.layout.select_dialog_item - works too...default look
    	final ListAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.listitem_header, _items);
    	return new AlertDialog.Builder(getActivity())
            .setCancelable(true)
            .setTitle(_title)
            .setAdapter(adapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // does nothing at this time
                }
            })            
            .create();
    	
    		/*.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            })
    	 	*/
	}
	
}
