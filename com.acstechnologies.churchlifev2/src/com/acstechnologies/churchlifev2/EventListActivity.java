package com.acstechnologies.churchlifev2;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.acstechnologies.churchlifev2.exceptionhandling.AppException;
import com.acstechnologies.churchlifev2.exceptionhandling.ExceptionHelper;
import com.acstechnologies.churchlifev2.exceptionhandling.ExceptionInfo;
import com.acstechnologies.churchlifev2.webservice.EventResponse;
import com.acstechnologies.churchlifev2.webservice.EventsResponse;
import com.acstechnologies.churchlifev2.webservice.WebServiceHandler;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class EventListActivity extends OptionsActivity {

	static final int DIALOG_PROGRESS_EVENTS = 0;
	static final int DIALOG_PROGRESS_EVENT = 1;
	
	static final String EVENT_DATE_FORMAT = "EEE MMM d";
	static final String EVENT_TIME_FORMAT = "h:mm a";
	
	private ProgressDialog _progressD;
	private String _progressText;
	
	ArrayList<EventListItem> _itemList;			// list of events to display
	EventsResponse _wsEvents;					// results of the latest web service call
	AppPreferences _appPrefs;  	
	
	ListView lv1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try
        {   
        	_appPrefs = new AppPreferences(getApplicationContext());
        	
        	setContentView(R.layout.eventlist);
        	  
        	bindControls();						// Set state variables to their form controls
        	         	
//        	//testing separated list adapter
//        	SeparatedListAdapter adapter = new SeparatedListAdapter(this);  
//        	adapter.addSection("Array test", new ArrayAdapter<String>(this, R.layout.eventlist, new String[]  { "First item", "Item two" }));  
//        	adapter.addSection("Section Two", new ArrayAdapter<String>(this, R.layout.eventlist, new String[] { "Second One", "Item two" }));
//        	
//        	//adapter.addSection("Security", new SimpleAdapter(this, security, R.layout.list_complex,  new String[] { ITEM_TITLE, ITEM_CAPTION }, new int[] { R.id.list_complex_title, R.id.list_complex_caption }));  
//        	   
//        	lv1.setAdapter(adapter);  
        	        	
        	loadEventsWithProgressDialog();
         
            // Wire up list on click - display event detail activity
            lv1.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {                	 
                	ItemSelected(position);                	                 	
                }
              });         	
        }           
        catch (Exception e) {
        	ExceptionHelper.notifyUsers(e, EventListActivity.this);
        	ExceptionHelper.notifyNonUsers(e);
        }          
    }
    

    protected Dialog onCreateDialog(int id) {
        switch(id) {
        case DIALOG_PROGRESS_EVENT:
        case DIALOG_PROGRESS_EVENTS:
        	_progressD = new ProgressDialog(EventListActivity.this);
        	
        	String msg = "";
        	if (id == DIALOG_PROGRESS_EVENTS) {
        		msg = getString(R.string.EventList_ProgressDialog);	
        	}
        	else {
        		msg = _progressText;    
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
    	lv1 = (ListView)this.findViewById(R.id.ListView01);
    }
    

    /**
     * Displays a progress dialog and launches a background thread to connect to a web service
     *   to retrieve search results 
     *   
     */
    private void loadEventsWithProgressDialog()
    {               	    			   
    	showDialog(DIALOG_PROGRESS_EVENTS);
    	
    	// This handler is called once the search is complete.  It looks at the data returned from the
    	//  thread (in the Message) to determine success/failure.  If successful, the results are displayed.
    	final Handler handler = new Handler() {
    		public void handleMessage(Message msg) {
    		  
    			removeDialog(DIALOG_PROGRESS_EVENTS);
    			
    			try {
	    			if (msg.what == 0) {	

	    				// Create an EventListItem arraylist and assign a 'more' item if there are more results to display
	    				if (_itemList == null ) {
	    					_itemList = new ArrayList<EventListItem>();
	    				}
	    				else {
	    					// This is a search from the 'More Results' listitem.  
	    					// Remove that item before loading more.
	    					_itemList.remove(_itemList.size()-1); 	    						    						    						    						    					
	    				}
	    				
	    				// Check for empty
	    				if (_wsEvents.getLength() == 0) {
	    					//just for now - view needs to pick up that this is a single item and display accordingly
	    					_itemList.add(new EventListItem(getResources().getString(R.string.EventList_NoResults), "", ""));
	    				}
	    				else {
		    		    	SimpleDateFormat displayDate = new SimpleDateFormat(EVENT_DATE_FORMAT);    	
		    		    	SimpleDateFormat displayTime = new SimpleDateFormat(EVENT_TIME_FORMAT);
		    		    			    		    		    		    	
		    				for (int i = 0; i < _wsEvents.getLength(); i++) {	
		    					
		    					_itemList.add(new EventListItem(_wsEvents.getEventName(i), 
		    								   displayDate.format(_wsEvents.getStartDate(i)),
		    								   displayTime.format(_wsEvents.getStartDate(i))));	  
		    				}
		    				
	    				    // Add the 'More...' at the bottom of the list
		    				if (_wsEvents.getHasMore() == true) {
		    					_itemList.add(new EventListItem("More Results...", "", ""));		//just for now...change 
		    				}

		    				// Set the adapter to use the arraylist of EventListItem 
		    				//  (if already set...refresh the adapter)
		    			    if (lv1.getAdapter() == null) {
		    			    	lv1.setAdapter(new EventListItemAdapter(EventListActivity.this, _itemList));
		    			    } else {
		    			        ((EventListItemAdapter)lv1.getAdapter()).refill(_itemList);
		    			    }		    			    		    			   
	    				}	    				
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
    				ExceptionHelper.notifyUsers(e, EventListActivity.this);
    	    		ExceptionHelper.notifyNonUsers(e)  ; 				    				
    			}    			    			    			    			   				    			    		  
    		}
    	};
    	
    	Thread searchThread = new Thread() {  
    		public void run() {
    			try {    				    				
    				GlobalState gs = (GlobalState) getApplication();
        	    	    		    	    		    
    				// Search for 1 year's worth of data starting today (at 00:00 time)
    		    	Calendar cal = Calendar.getInstance();
    		    	cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_WEEK), 0, 0, 0);
    		    	
    		    	Date start = cal.getTime();    		    	
    		        cal.roll(Calendar.YEAR, 1);
    		        Date stop = cal.getTime();
    		    	    		    	    		    		    	    	    	   
	    	    	// If we have search results, and the call to this method is 
	    	    	//  for 'more', we pick back up where we left off.
	    	    	int startingRecordId = 0; 							// initialize	  	    	    	
	    	    	if (_wsEvents != null) {
	    	    		startingRecordId = _wsEvents.getMaxResult();
	    	    	}
	    	    	
	    	    	WebServiceHandler wh1 = new WebServiceHandler(_appPrefs.getWebServiceUrl());
	    	    	_wsEvents = wh1.getEvents(gs.getUserName(), gs.getPassword(), gs.getSiteNumber(), start, stop, startingRecordId, 0);

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
    
    // Occurs when a user selects an individual on the listview.    
    private void ItemSelected(int position)
    {    	    
    	try {
    		// First, look for the max position as an item for that position will not exist in 
    		//  the records returned from the webservice.  This is the 'more records' item.  If
    		//  this is selected get the next set of records.  (Remember the array is 0 based)
    		if (position != 0 && position % _wsEvents.getLength() == 0) {               	 		
       	 		loadEventsWithProgressDialog();
       	 	}
       	 	else {
       	 		// Show the selected event detail
       	 		_progressText = String.format(getString(R.string.Event_ProgressDialog), _wsEvents.getEventName(position));
       	 		
       	 		loadEventWithProgressDialog(_wsEvents.getEventId(position));       	 		
       	 	}       	 	       	 	
    	}
        catch (Exception e) {
        	// must NOT raise errors.  called by an event
			ExceptionHelper.notifyUsers(e, EventListActivity.this);
	    	ExceptionHelper.notifyNonUsers(e)  ; 				    				
		}  
    }
        
    
    /**
     * Displays a progress dialog and launches a background thread to connect to a web service
     *   to retrieve a SINGLE event.  Then launches the event activity to display
     *   
     */
    private void loadEventWithProgressDialog(final String eventId)
    {               	    			   
    	showDialog(DIALOG_PROGRESS_EVENT);
    	
    	// This handler is called once the lookup is complete.  It looks at the data returned from the
    	//  thread (in the Message) to determine success/failure.  If successful, the event activity
    	//  is launched, passing the event response
    	final Handler handler = new Handler() {
    		public void handleMessage(Message msg) {
    		  
    			removeDialog(DIALOG_PROGRESS_EVENT);
    			
    			try {
	    			if (msg.what == 0) {	
	    				// Start an Event activity and pass the response to it.
	    				Intent intent = new Intent();
	           	 		intent.setClass(EventListActivity.this, EventActivity.class); 		           	 	
	           	 		intent.putExtra("event", msg.getData().getString("event"));
	           	 		startActivity(intent);	    				
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
	 						   "loadEventWithProgressDialog.handleMessage",
	 						   errMsg);	       				
	       			}	    				
    			}
    			catch (Exception e) {
    				ExceptionHelper.notifyUsers(e, EventListActivity.this);
    	    		ExceptionHelper.notifyNonUsers(e)  ; 				    				
    			}    			    			    			    			   				    			    		  
    		}
    	};
    	
    	Thread searchThread = new Thread() {  
    		public void run() {
    			try {    				    				
    				GlobalState gs = (GlobalState) getApplication();
	    	    	WebServiceHandler wh1 = new WebServiceHandler(_appPrefs.getWebServiceUrl());
	    	    	
	    	    	EventResponse er = wh1.getEvent(gs.getUserName(), gs.getPassword(), gs.getSiteNumber(), eventId);

	    	    	// Return the response object (as string) to the message handler above
	    	    	Message msg = handler.obtainMessage();		
	    	    	msg.what = 0;
	    	    	
	    	    	Bundle b = new Bundle();
    				b.putString("event", er.toString());
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
    				
    				handler.sendMessage(msg);    				
    			}    			       	    	    	    	
    		 }
    	};
    	searchThread.start();    	
    }
        
}
