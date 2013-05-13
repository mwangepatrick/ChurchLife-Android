package com.acstech.churchlife;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionHelper;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;
import com.acstech.churchlife.listhandling.ConnectionTeamsListLoader;
import com.acstech.churchlife.listhandling.DefaultListItem;
import com.acstech.churchlife.listhandling.DefaultListItemAdapter;
import com.acstech.churchlife.listhandling.IndividualListLoader;
import com.acstech.churchlife.listhandling.ListLoaderBase;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class AssignToPickerActivity extends Activity {

	private int _assignToMode = AssignmentActivity.ASSIGNMODE_INDIVIDUAL;
	
	static final int DIALOG_PROGRESS = 0;	
	private ProgressDialog _progressD;
	
	ListLoaderBase<DefaultListItem> _loader;
	
	EditText txtSearch;
	Button btnSearch;
	ListView lv1;
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
		 
		 super.onCreate(savedInstanceState);
	 				 
		 try {		 		        	
        	setTitle(R.string.Connection_AssignDialogTitle); 			
	 		setContentView(R.layout.assigntopicker);       
	 		
	 		bindControls();
			
        	 // This activity MUST be passed data
        	 Bundle extraBundle = this.getIntent().getExtras();
             if (extraBundle == null) {
            	 throw AppException.AppExceptionFactory(
            			 ExceptionInfo.TYPE.UNEXPECTED,
						 ExceptionInfo.SEVERITY.CRITICAL, 
						 "100",           												    
						 "AssignToPickerActivity.onCreate",
						 "No assignment was passed to the AssignToPicker activity.");
             }
             else {          
            	 _assignToMode = extraBundle.getInt("assignto");
            	 
            	 if (_assignToMode == AssignmentActivity.ASSIGNMODE_TEAM) {			// team
            		 txtSearch.setHint(R.string.Connection_AssignHintTeam);      
            	 }
            	 else {																// individual
            		 txtSearch.setHint(R.string.Connection_AssignHintIndividual);      		 
            	 }            	                 	             	   
             }		                         
		 }
		 catch (Exception e) {
		 	ExceptionHelper.notifyUsers(e, AssignToPickerActivity.this);
		 	ExceptionHelper.notifyNonUsers(e);
		 }	        	                                   
	 }
	   
	 protected Dialog onCreateDialog(int id) {
	        switch(id) {
	        case DIALOG_PROGRESS:
	        	_progressD = new ProgressDialog(AssignToPickerActivity.this);	        	
	        	_progressD.setMessage(getString(R.string.Dialog_Loading));        	
	        	_progressD.setIndeterminate(true);
	        	_progressD.setCancelable(false);
	    		return _progressD;	  
	        default:
	            return null;
	        }
	 }

	 
	 /**
	  *  Links state variables to their respective form controls
	 */
	 private void bindControls(){

		txtSearch = (EditText)this.findViewById(R.id.txtSearchFor);
	    btnSearch = (Button)this.findViewById(R.id.btnSearch);	
	    lv1 = (ListView)this.findViewById(R.id.ListView01);
					 
   	 	// Wire up the search button                     
        btnSearch.setOnClickListener(new OnClickListener() {		
        	public void onClick(View v) {	    					
				// Button click always starts a new search (clears out any prior search results)					
				if (_loader != null) {
					_loader.clear();
				}
				
				if (inputIsValid()) {
					loadListWithProgressDialog(true);
				}
        	}		
		 });
        
        // Wire up list on click 
        lv1.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {                 	 
           	 DefaultListItem item = (DefaultListItem)parent.getAdapter().getItem(position);                	                	
           	 ItemSelected(item);                	                 	
            }
        }); 
        			 	
	}

	 /**
     * Ensures all form fields have valid input.  
     * 
     * Should be called on button click before processing.   Displays a message 
     *   to the user indicating which field is invalid.
     *   
     * @return true if input fields are valid, otherwise false
     */
	 private Boolean inputIsValid()
	 {    	
    	String msg = "";
    	    	
    	if (txtSearch.getText().length() == 0) {   
    		msg = (String)this.getResources().getText(R.string.IndividualList_Validation_Search);     		
    	}
    	    	    
    	// If a validation message exists, show it
    	if (msg.length() > 0) {
    		Toast.makeText(AssignToPickerActivity.this, msg, Toast.LENGTH_LONG).show(); 
    	}
    	
    	// If a validation message exists, the input is invalid
    	return (msg.length() == 0);    	    
	 }
    
    /**
     * Displays a progress dialog and launches a background thread to connect to a web service
     *   to retrieve search results 
     *   
     */
    private void loadListWithProgressDialog(boolean nextResult)
    {           	    
    	showDialog(DIALOG_PROGRESS);
    	
    	try
    	{	    		
    		if (_loader == null) {
    
    			if (_assignToMode == AssignmentActivity.ASSIGNMODE_TEAM) {			
    				_loader = new ConnectionTeamsListLoader(this, txtSearch.getText().toString().trim());
           	 	}
           	 	else {								
    				_loader = new IndividualListLoader(this, txtSearch.getText().toString().trim());           	 		
           	 	}    			
    			
    		}
    		else
    		{
    			_loader.setSearchText(txtSearch.getText().toString());
    		}
    		
    		// see onListLoaded below for the next steps (after load is done)
    		if (nextResult) {
    			_loader.LoadNext(onListLoaded);
    		}
    		else {
    			_loader.LoadPrevious(onListLoaded);
    		}
	        
    		// Hide the keyboard
	        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	        imm.hideSoftInputFromWindow(txtSearch.getWindowToken(), 0);	            
    	}
    	catch (Exception e) {
			ExceptionHelper.notifyUsers(e, AssignToPickerActivity.this);
    		ExceptionHelper.notifyNonUsers(e); 				    				
		}      	
    }
	    
 
    // display the results from the loader operation
    final Runnable onListLoaded = new Runnable() {
        public void run() {
        	
        	try
        	{	        		        	
	        	if (_loader.success())	{	        			        			        
		     		// set items to list
    				// save index and top position (preserve scroll location)
    				int index = lv1.getFirstVisiblePosition();
    				View v = lv1.getChildAt(0);
    				int top = (v == null) ? 0 : v.getTop();
    				
    				lv1.setAdapter(new DefaultListItemAdapter(AssignToPickerActivity.this, _loader.getList(), R.layout.listitem_default));	
    				
	        		lv1.setSelectionFromTop(index, top);   // restore scroll position  	    			
	        	}
	        	else {
	        		throw _loader.getException();
	        	}
        	}
        	catch (Throwable e) {
        		ExceptionHelper.notifyUsers(e, AssignToPickerActivity.this);
        		ExceptionHelper.notifyNonUsers(e); 	        	
			} 	        	
        	finally
        	{
        		removeDialog(DIALOG_PROGRESS);
        	}
        }
    };    

	    
	 
    /**
     * Occurs when a user selects an event on the listview.
     * 
     * @param itemSelected
     */
    private void ItemSelected(DefaultListItem item)
    {    	    
    	try {
    		// First, see if this is an 'individual' that was selected or a 'more records' item.
    		//  if 'more records'...load the next set of records.
    		if (item.getId().equals("")) {    			
    			loadListWithProgressDialog(true);
       	 	}
       	 	else {
       	 		// Close this activity (opened by the edit connection activity) passing the selection       	 		
       	 		Intent result = new Intent();
       	 		result.putExtra("id", item.getId());
       	 		result.putExtra("description", item.getTitle());
       	 		setResult(RESULT_OK, result);   
       	 		finish();
       	 	}       	 	       	 	
    	}
        catch (Exception e) {
        	// must NOT raise errors.  called by an event
			ExceptionHelper.notifyUsers(e, AssignToPickerActivity.this);
	    	ExceptionHelper.notifyNonUsers(e)  ; 				    				
		}  
    }
	 	 
}
