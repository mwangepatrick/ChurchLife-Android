package com.acstech.churchlife;

import com.acstech.churchlife.exceptionhandling.ExceptionHelper;
import com.acstech.churchlife.R;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class IndividualListActivity extends OptionsActivity {
	
	static final int DIALOG_PROGRESS_INDIVIDUALS = 0;
	static final int DIALOG_PROGRESS_INDIVIDUAL = 1;
	
	private ProgressDialog _progressD;
	private String _progressText;
		
	IndividualListLoader _loader;
	AppPreferences _appPrefs;
	
	EditText txtSearch;
	Button btnSearch;
	ListView lv1;

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try
        {        	
        	 _appPrefs = new AppPreferences(getApplicationContext());
        	 
        	 setContentView(R.layout.individuallist);
        	         	 
        	 bindControls();								// Set state variables to their form controls
        	         	 
        	 // If a list of people is present, clear it when the user 
        	 //  begins to enter different search text
        	 TextWatcher tw = new TextWatcher() {
        		 public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        		 public void onTextChanged(CharSequence s, int start, int before, int count) {         			
        			 if (lv1.getAdapter() != null ) {        				 
        				 lv1.setAdapter(null);
        			 }        			
        		 }
        		 public void afterTextChanged(Editable arg0) {}        		 
        	 };         		
        	 txtSearch.addTextChangedListener(tw);
        	 
        	
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
             
             // Wire up list on click - display person detail activity
             lv1.setOnItemClickListener(new OnItemClickListener() {
                 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {                 	 
                	 DefaultListItem item = (DefaultListItem)parent.getAdapter().getItem(position);                	                	
                	 ItemSelected(item);                	                 	
                 }
               });             	         	         	         	         	 
        }
    	catch (Exception e) {
    		ExceptionHelper.notifyUsers(e, IndividualListActivity.this);
    		ExceptionHelper.notifyNonUsers(e);
    	}          
    }
    
    
    protected Dialog onCreateDialog(int id) {
        switch(id) {
        case DIALOG_PROGRESS_INDIVIDUAL:
        case DIALOG_PROGRESS_INDIVIDUALS:
        	_progressD = new ProgressDialog(IndividualListActivity.this);
        	
        	String msg = "";
        	if (id == DIALOG_PROGRESS_INDIVIDUALS) {
        		msg = getString(R.string.IndividualList_ProgressDialog);	
        	}
        	else {
        		msg = String.format(getString(R.string.Individual_ProgressDialog), _progressText);        		
        	}
        	
        	_progressD.setMessage(msg);        	
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
    		Toast.makeText(IndividualListActivity.this, msg, Toast.LENGTH_LONG).show(); 
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
    	showDialog(DIALOG_PROGRESS_INDIVIDUALS);
    	
    	try
    	{	
    		if (_loader == null) {    			
    			_loader = new IndividualListLoader(txtSearch.getText().toString());	
    			_loader.setNoResultsMessage(getResources().getString(R.string.IndividualList_NoResults));
    			_loader.setNextResultsMessage(getResources().getString(R.string.IndividualList_More));	       		
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
			ExceptionHelper.notifyUsers(e, IndividualListActivity.this);
    		ExceptionHelper.notifyNonUsers(e); 				    				
		}      	
    }
    
 
    // display the results from the loader operation
    final Runnable onListLoaded = new Runnable() {
        public void run() {
        	
        	try
        	{	        		        	
	        	if (_loader.success())	{	        			        			        
	        		/*
	        		//If only 1 individual, go directly to the detail page
	        		DefaultListItem item =(DefaultListItem)_loader.getList().get(0);
	        			  
	        		if (_loader.getList().size() == 1 && item.isTitleOnlyItem() == false) {	    					  				
	        			//startCommentListActivity(_individualId, _individualName, Integer.parseInt(item.getId()), true);		        			
	        		}
	        		else {
			     		// set items to list
		        		// ... see below
		        	}
	        		*/
	        		
		     		// set items to list
    				// save index and top position (preserve scroll location)
    				int index = lv1.getFirstVisiblePosition();
    				View v = lv1.getChildAt(0);
    				int top = (v == null) ? 0 : v.getTop();
    				
    				lv1.setAdapter(new DefaultListItemAdapter(IndividualListActivity.this, _loader.getList(), R.layout.listitem_default));	
    				
	        		lv1.setSelectionFromTop(index, top);   // restore scroll position  	    			
	        	}
	        	else {
	        		throw _loader.getException();
	        	}
        	}
        	catch (Throwable e) {
        		ExceptionHelper.notifyUsers(e, IndividualListActivity.this);
        		ExceptionHelper.notifyNonUsers(e); 	        	
			} 	        	
        	finally
        	{
        		removeDialog(DIALOG_PROGRESS_INDIVIDUALS);
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
       	 		// Show the selected individuals detail
       	 		String name = item.getTitle().trim();
       	 		String dialogText = String.format(getString(R.string.Individual_ProgressDialog), name); 
 
       	 		IndividualActivityLoader loader = new IndividualActivityLoader(this, dialogText);
       	 		loader.loadIndividualWithProgressWindow(Integer.parseInt(item.getId()));            	 		
       	 	}       	 	       	 	
    	}
        catch (Exception e) {
        	// must NOT raise errors.  called by an event
			ExceptionHelper.notifyUsers(e, IndividualListActivity.this);
	    	ExceptionHelper.notifyNonUsers(e)  ; 				    				
		}  
    }
    	    
}    