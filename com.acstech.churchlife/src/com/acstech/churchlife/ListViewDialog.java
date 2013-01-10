package com.acstech.churchlife;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
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

	
	final CharSequence[] items = {"Joe User", "Bar Rafaell", "Sally Smith"};

	
	@Override 
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        // Title
        if (_title.length() > 0) {
        	builder.setTitle(_title);
        }
		
		builder.setItems(_items, null);
		/*new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		         // Do something with the selection
		    }		
		});*/
		builder.setPositiveButton(R.string.Dialog_Close, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
               dialog.dismiss();
            }
        });  		
        
		return  builder.create();	
	}
	
	/*
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());        
        
        // Title
        if (_title.length() > 0) {
        	builder.setMessage(_title);
        }
        
        // Items
        //builder.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, _items),  null);
        builder.setItems(_items, null);
        
        /*    .setPositiveButton(R.string.Save, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // do something 
                   }
               })             						           
               .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                   }
               });
        */
        
        // Create the AlertDialog object and return it
        //return builder.create();
   // }
	

}
