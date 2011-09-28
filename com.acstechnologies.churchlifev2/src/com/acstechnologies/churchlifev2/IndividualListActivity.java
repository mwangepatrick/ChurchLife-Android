package com.acstechnologies.churchlifev2;

import com.acstechnologies.churchlifev2.R;
import com.acstechnologies.churchlifev2.exceptionhandling.AppException;
import com.acstechnologies.churchlifev2.exceptionhandling.ExceptionHelper;
import com.acstechnologies.churchlifev2.exceptionhandling.ExceptionInfo;
import com.acstechnologies.churchlifev2.webservice.IndividualsResponse;
import com.acstechnologies.churchlifev2.webservice.WebServiceHandler;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
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
	
	ArrayAdapter<DefaultListItem> _itemArrayAdapter;
	IndividualsResponse _wsIndividuals;		// results of the web service call
	
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
        			 _itemArrayAdapter = null;
        			 if (lv1.getAdapter() != null ) {
        				 ((ArrayAdapter<DefaultListItem>)lv1.getAdapter()).clear();        				
        			 }
        		 }
        		 public void afterTextChanged(Editable arg0) {}        		 
        	 };         		
        	 txtSearch.addTextChangedListener(tw);
        	 
        	
        	 // Wire up the search button                     
             btnSearch.setOnClickListener(new OnClickListener() {		
             	public void onClick(View v) {	    					
						_wsIndividuals = null;				// Button click always starts a new search (clears out any prior search results)						
						_itemArrayAdapter = null;
						doSearchWithProgressWindow();
             	}		
     		 });     
             
             // Wire up list on click - display person detail activity
             lv1.setOnItemClickListener(new OnItemClickListener() {
                 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {                 	 
                	 DefaultListItem itemSelected = (DefaultListItem)parent.getAdapter().getItem(position);                	                	
                	 ItemSelected(itemSelected.getId(), itemSelected.getDescription());                	                 	
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
		    				
		    				// If this is the initial search, create an adapter...otherwise this is the result of
		    				//  the use selecting a 'More Results' item.  If so, remove that item and load more results.
		    				if (_itemArrayAdapter == null ) {
		    					_itemArrayAdapter = new ArrayAdapter<DefaultListItem>(IndividualListActivity.this, R.layout.listitem_default);		    					
		    					lv1.setAdapter(_itemArrayAdapter);
		    				}
		    				else {		    				
		    					// Remove the 'More Results...' item before loading more.
		    					DefaultListItem moreItem = _itemArrayAdapter.getItem(_itemArrayAdapter.getCount()-1);
		    					_itemArrayAdapter.remove(moreItem); 	    						    						    						    						    					
		    				}
		    						    						    						    			
		    				// Check for empty 
		    				if (_wsIndividuals.getLength() == 0) {		    					
		    					_itemArrayAdapter.add(new DefaultListItem("", getResources().getString(R.string.IndividualList_NoResults)));		    					
		    				}
		    				else {
				    		    			    		    		    		    	
		    					// Add all items from the latest web service request to the adapter
			    				for (int i = 0; i < _wsIndividuals.getLength(); i++) {			    								    								    			
			    					_itemArrayAdapter.add(new DefaultListItem(Integer.toString(_wsIndividuals.getIndvId(i)), _wsIndividuals.getFirstName(i) + " " + _wsIndividuals.getLastName(i)));
			    				}
			    				
		    				    // If the web service indicates more records...Add the 'More Records' item
			    				if (_wsIndividuals.getHasMore() == true) {			    					
			    					_itemArrayAdapter.add(new DefaultListItem("", getResources().getString(R.string.IndividualList_More)));
			    				}
			    					    			    		    			  
		    				}	
		    				
		    				// Notify the adapter that the data has been updated/set (so the view will be updated)		    				
		    				((ArrayAdapter<DefaultListItem>)lv1.getAdapter()).notifyDataSetChanged();

		       	             // Hide the keyboard
		       	             InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		       	             imm.hideSoftInputFromWindow(txtSearch.getWindowToken(), 0);

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
		    	    	
		    	    	WebServiceHandler wh = new WebServiceHandler(_appPrefs.getWebServiceUrl(), config.APPLICATION_ID_VALUE);
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
    
    // Occurs when a user selects an individual on the listview.    
    private void ItemSelected(String individualId, String individualName)
    {    	    
    	try {
    		// First, see if this is an 'individual' that was selected or a 'more records' item.
    		//  if more...load the next 50 records.
       	 	if (individualId == "") {         	 		
       	 		doSearchWithProgressWindow();
       	 	}
       	 	else {
       	 		// display the individual
       	 		String name = individualName;
       	 		String dialogText = String.format(getString(R.string.Individual_ProgressDialog), name); 
 
       	 		IndividualActivityLoader loader = new IndividualActivityLoader(this, dialogText);
       	 		loader.loadIndividualWithProgressWindow(Integer.parseInt(individualId));       	 						
       	 	}       	 	       	 	
    	}
        catch (Exception e) {
        	// must NOT raise errors.  called by an event
			ExceptionHelper.notifyUsers(e, IndividualListActivity.this);
	    	ExceptionHelper.notifyNonUsers(e)  ; 				    				
		}  
    }
    
    	    
}    