package com.acstechnologies.churchlifev2;

import com.acstechnologies.churchlifev2.R;
import com.acstechnologies.churchlifev2.exceptionhandling.AppException;
import com.acstechnologies.churchlifev2.exceptionhandling.ExceptionHelper;
import com.acstechnologies.churchlifev2.exceptionhandling.ExceptionInfo;
import com.acstechnologies.churchlifev2.webservice.IndividualResponse;
import com.acstechnologies.churchlifev2.webservice.IndividualsResponse;
import com.acstechnologies.churchlifev2.webservice.WebServiceHandler;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


public class IndividualListActivity extends OptionsActivity {
	
	static final int DIALOG_PROGRESS_INDIVIDUALS = 0;
	static final int DIALOG_PROGRESS_INDIVIDUAL = 1;
	
	private ProgressDialog _progressD;
	private String _progressText;
	
	IndividualsResponse _wsIndividuals;					// results of the web service call
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
        	         	 
        	 // Wire up the search button                     
             btnSearch.setOnClickListener(new OnClickListener() {		
             	public void onClick(View v) {	    					
						_wsIndividuals = null;				// Button click always starts a new search (clears out any prior search results)
						doSearchWithProgressWindow();
             	}		
     		 });     
             
             // Wire up list on click - display person detail activity
             lv1.setOnItemClickListener(new OnItemClickListener() {
                 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {                	 
                	 ItemSelected(position);                	                 	
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
    private void doSearchWithProgressWindow()
    {           
    	if (inputIsValid()) {	    			    
	    	showDialog(DIALOG_PROGRESS_INDIVIDUALS);
	    	
	    	// This handler is called once the people search is complete.  It looks at the data returned
	    	//  from the thread (in the Message) to determine success/failure.  If successful, the results are displayed.
	    	final Handler handler = new Handler() {
	    		public void handleMessage(Message msg) {
	    		  
	    			removeDialog(DIALOG_PROGRESS_INDIVIDUALS);
	    			
	    			try {
		    			if (msg.what == 0) {	        			
		       	             String[] names = _wsIndividuals.getFullNameList(true, getResources().getString(R.string.IndividualList_More));	       	             	       	             	       	           
		       	             lv1.setAdapter(new ArrayAdapter<String>(IndividualListActivity.this, R.layout.list_item, names));	       	                  	         				                  
		       			}
		       			else if (msg.what < 0) {
		       				// If < 0, the exception text is in the message bundle.  Throw it
		       				
		       				//TODO:
		       				//  (we should examine it to see if is is one that should be raised as critical
		       				//   or something that is just a validation message, etc.)
		       				String errMsg = msg.getData().getString("Exception");	       
		       				throw AppException.AppExceptionFactory(
		       					  ExceptionInfo.TYPE.UNEXPECTED,
		 						   ExceptionInfo.SEVERITY.CRITICAL, 
		 						   "100",           												    
		 						   "doSearchWithProgressWindow.handleMessage",
		 						   errMsg);	       				
		       			}	    				
	    			}
	    			catch (Exception e) {
	    				ExceptionHelper.notifyUsers(e, IndividualListActivity.this);
	    	    		ExceptionHelper.notifyNonUsers(e)  ; 				    				
	    			}    			    			    			    			   				    			    		  
	    		}
	    	};
	    	
	    	Thread searchThread = new Thread() {  
	    		public void run() {
	    			try {
		    			GlobalState gs = (GlobalState) getApplication();
		    	    	String searchText = txtSearch.getText().toString();
		    	    	    	    
		    	    	// If we have search results, and the call to this method is 
		    	    	//  for 'more', we pick back up where we left off.
		    	    	int startingRecordId = 0; 							// initialize	  	    	    	
		    	    	if (_wsIndividuals != null) {
		    	    		startingRecordId = _wsIndividuals.getMaxResult();
		    	    	}
		    	    	
		    	    	WebServiceHandler wh = new WebServiceHandler(_appPrefs.getWebServiceUrl());
		    	    	_wsIndividuals = wh.getIndividuals(gs.getUserName(), gs.getPassword(), gs.getSiteNumber(), searchText, startingRecordId, 0);
		    	    	handler.sendEmptyMessage(0);
	    			}
	    			catch (Exception e) {    				
	    				ExceptionHelper.notifyNonUsers(e);			// Log the full error, 
	    				
	    				Message msg = handler.obtainMessage();		// return only the exception string as part of the message
	    				msg.what = -1;
	    				//TODO:  revisit - this could bubble up info to the user that they don't need to see or won't understand.
	    				//  use ExceptionHelper to get a string to show the user based on the exception type/severity, etc.
	    				//  if appexception and not critical, return -1, ...if critical return -2, etc.
	    				
	    				String returnMessage = String.format("An unexpected error has occurred while performing this search.  The error is %s.", e.getMessage());					    				    				    				    				    	
	    				Bundle b = new Bundle();
	    				b.putString("Exception", returnMessage);
	    				
	    				handler.sendMessage(msg);    				
	    			}    			       	    	    	    	
	    		 }
	    	};
	    	searchThread.start();    
    	}
    }
    
    // Occurs when a user selects an item on the listview.    
    private void ItemSelected(int position)
    {    	    
    	try {
    		// First, look for the max position as an item for that position will not exist in 
    		//  the records returned from the webservice.  This is the 'more records' item.  If
    		//  this is selected get the next set of records.  (Remember the array is 0 based)
       	 	if (position == _wsIndividuals.getLength()) {         	 		
       	 		doSearchWithProgressWindow();
       	 	}
       	 	else {
       	 		// display the individual
       	 		_progressText = _wsIndividuals.getFirstName(position) + " " + _wsIndividuals.getLastName(position);
       	 		loadIndividualWithProgressWindow(_wsIndividuals.getIndvId(position));       	 		  										    	     	 		       	 
       	 	}       	 	       	 	
    	}
        catch (Exception e) {
        	// must NOT raise errors.  called by an event
			ExceptionHelper.notifyUsers(e, IndividualListActivity.this);
	    	ExceptionHelper.notifyNonUsers(e)  ; 				    				
		}  
    }
    	    
    
    ///////////// - move this out possibly...same thing has to be done from inside person if thye
    // pick a family member
    /**
     * Displays a progress dialog and launches a background thread to connect to a web service
     *   to retrieve search results 
     *   
     */
    private void loadIndividualWithProgressWindow(final int individualId)
    {           
    	if (inputIsValid()) {	    		
    		
	    	showDialog(DIALOG_PROGRESS_INDIVIDUAL);
	    	
	    	// This handler is called once the people search is complete.  It looks at the data returned
	    	//  from the thread (in the Message) to determine success/failure.  If successful, the results are displayed.
	    	final Handler handler = new Handler() {
	    		public void handleMessage(Message msg) {
	    		  
	    		
	    			
	    			try {
		    			if (msg.what == 0) {	        					           	 	
		           	 		Intent intent = new Intent();
		           	 		intent.setClass(IndividualListActivity.this, IndividualActivity.class); 		           	 	
		           	 		intent.putExtra("individual", msg.getData().getString("individual"));
		           	 		startActivity(intent);
		           	 		
		           	 		removeDialog(DIALOG_PROGRESS_INDIVIDUAL);
		           	 		
		       			}
		       			else if (msg.what < 0) {
		       				
		       				removeDialog(DIALOG_PROGRESS_INDIVIDUAL);
		       				// If < 0, the exception text is in the message bundle.  Throw it
		       				
		       				//TODO:
		       				//  (we should examine it to see if is is one that should be raised as critical
		       				//   or something that is just a validation message, etc.)
		       				String errMsg = msg.getData().getString("Exception");	       
		       				throw AppException.AppExceptionFactory(
		       					  ExceptionInfo.TYPE.UNEXPECTED,
		 						   ExceptionInfo.SEVERITY.CRITICAL, 
		 						   "100",           												    
		 						   "doSearchWithProgressWindow.handleMessage",
		 						   errMsg);	       				
		       			}	    				
	    			}
	    			catch (Exception e) {
	    				ExceptionHelper.notifyUsers(e, IndividualListActivity.this);
	    	    		ExceptionHelper.notifyNonUsers(e)  ; 				    				
	    			}    			    			    			    			   				    			    		  
	    		}
	    	};
	    	
	    	Thread searchThread = new Thread() {  
	    		public void run() {
	    			try {
		    			GlobalState gs = (GlobalState) getApplication();
			    	    	
		    			WebServiceHandler wh = new WebServiceHandler(_appPrefs.getWebServiceUrl());
		    	    	IndividualResponse i = wh.getIndividual(gs.getUserName(), gs.getPassword(), gs.getSiteNumber(), individualId);
		    	    	
		    	    	Message msg = handler.obtainMessage();		// return only the exception string as part of the message
		    	    	msg.what = 0;
		    	    	
		    	    	Bundle b = new Bundle();
	    				b.putString("individual", i.toString());
	    				msg.setData(b);
	    						    	    		    	    			    	  
		    	    	handler.sendMessage(msg);
	    			}
	    			catch (Exception e) {    				
	    				ExceptionHelper.notifyNonUsers(e);			// Log the full error, 
	    				
	    				Message msg = handler.obtainMessage();		// return only the exception string as part of the message
	    				msg.what = -1;
	    				//TODO:  revisit - this could bubble up info to the user that they don't need to see or won't understand.
	    				//  use ExceptionHelper to get a string to show the user based on the exception type/severity, etc.
	    				//  if appexception and not critical, return -1, ...if critical return -2, etc.
	    				
	    				String returnMessage = String.format("An unexpected error has occurred while performing this search.  The error is %s.", e.getMessage());					    				    				    				    				    	
	    				Bundle b = new Bundle();
	    				b.putString("Exception", returnMessage);
	    				msg.setData(b);
	    				
	    				handler.sendMessage(msg);    				
	    			}    			       	    	    	    	
	    		 }
	    	};
	    	searchThread.start();    
    	}
    }
            
    //////////////
}    