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
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost;
import android.content.DialogInterface;

public class EventListActivity extends OptionsActivity {
	
	static final int DIALOG_PROGRESS_EVENTS = 0;
	static final int DIALOG_PROGRESS_EVENT = 1;
	
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
        	
        	loadEventsWithProgressDialog();
         
            // Wire up list on click - display event detail activity
            lv1.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {  
                	EventListItem item = (EventListItem)parent.getAdapter().getItem(position);                 	                
                	ItemSelected(item);               	                 	
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
	    				
	    				// Check for empty
	    				if (_wsEvents.getLength() == 0) {
	    					//TODO view needs to pick up that this is a single item and display a single item (centered) 
	    					//     (it is currently using the same display as the event item)
	    					_itemList.add(new EventListItem("", getResources().getString(R.string.EventList_NoResults), null));
	    				}
	    				else {
		    		    			    		    		    		    
		    				for (int i = 0; i < _wsEvents.getLength(); i++) {	
		    					
		    					_itemList.add(new EventListItem(_wsEvents.getEventId(i),
		    													_wsEvents.getEventName(i), 
		    													_wsEvents.getStartDate(i)));	  
		    				}
		    				
		    				// save index and top position (preserve scroll location)
		    				int index = lv1.getFirstVisiblePosition();
		    				View v = lv1.getChildAt(0);
		    				int top = (v == null) ? 0 : v.getTop();

		    				lv1.setAdapter(getSeparatedListAdapter(_itemList, _wsEvents.getHasMore()));

		    				// restore scroll position
		    				lv1.setSelectionFromTop(index, top);		    				
	    				}	    				
	       			}
	       			else if (msg.what < 0) {	       			
	       				// If < 0, the exception details are in the message bundle.  Throw it 
	       				//  and let the exception handler (below) handle it
	       				Bundle b = msg.getData();
	       				throw ExceptionHelper.getAppExceptionFromBundle(b, "loadEventsWithProgressDialog.handleMessage");	
	       			}	    				
    			}    			
    			catch (Exception e) {
    				
    				if ((e instanceof AppException) && 
    					(((AppException)e).getErrorType() == ExceptionInfo.TYPE.NOCONNECTION)){
    					handleNetworkExceptionWithRetry();    					
    				} 
    				else {    				
    					ExceptionHelper.notifyUsers(e, EventListActivity.this);
    	    			ExceptionHelper.notifyNonUsers(e);
    				}
    			}     			    			    			    			   				    			    		  
    		}
    	};
    	
    	Thread searchThread = new Thread() {  
    		public void run() {
    			try {    				    				
    				//GlobalState gs = (GlobalState) getApplication();
    				// zzz remove after testing 
    				GlobalState gs = GlobalState.getInstance(); 
    				
    				// Search for 1 year's worth of data starting today (at 00:00 time)
    		    	Calendar cal = Calendar.getInstance();    		    	    		    	
    		    	cal.set(Calendar.HOUR, 0);
    		    	cal.set(Calendar.MINUTE, 0);
    		    	cal.set(Calendar.SECOND, 0);
    		    	    
    		    	Date start = cal.getTime();    		    	
    		        cal.roll(Calendar.YEAR, 1);
    		        Date stop = cal.getTime();
    		    	    		    	    		    		    	    	    	   
	    	    	// If we have search results, and the call to this method is 
	    	    	//  for 'more', we pick back up where we left off.
	    	    	int startingRecordId = 0; 							// initialize	  	    	    	
	    	    	if (_wsEvents != null) {
	    	    		startingRecordId = _wsEvents.getMaxResult();
	    	    	}
	    	    	
	    	    	WebServiceHandler wh1 = new WebServiceHandler(_appPrefs.getWebServiceUrl(), config.APPLICATION_ID_VALUE);
	    	    	_wsEvents = wh1.getEvents(gs.getUserName(), gs.getPassword(), gs.getSiteNumber(), start, stop, startingRecordId, 0);

	    	    	handler.sendEmptyMessage(0);
    			}
    			catch (Exception e) {    				
    				ExceptionHelper.notifyNonUsers(e);			// Log the full error, 

    				Message msg = handler.obtainMessage();
    				msg.what = -1;
    				msg.setData(ExceptionHelper.getBundleForException(e));	
    				handler.sendMessage(msg);       				
    			}    			       	    	    	    	
    		 }
    	};
    	searchThread.start();    
	
    }
    
    //new
    /**
     * Iterates over the class level event item list and adds those events to a
     *  separated list adapter (headers for month/year)
     *   
     * @return
     */
    public SeparatedListAdapter getSeparatedListAdapter(ArrayList<EventListItem> allItems, boolean addMoreLink) {
		 
		SeparatedListAdapter separatedAdapter = new SeparatedListAdapter(this);  
		
		String lastHeader = "";
		ArrayList<EventListItem> itemsForMonthYear = null;
		
		for (EventListItem event : allItems) {
			
			// if new month, year...create a new 
			if (event.getMonthYearText().equals(lastHeader) == false) {
				
				if (itemsForMonthYear != null) {
					// add the items (with header) to the separated list adapter (the first time in this is null)
					separatedAdapter.addSection(lastHeader, new EventListItemAdapter(EventListActivity.this, itemsForMonthYear));
				}					
				
				// start a new list/header
				itemsForMonthYear = new ArrayList<EventListItem>();
				lastHeader = event.getMonthYearText();					
			}
			
			//add this item to the current month/year
			itemsForMonthYear.add(event);  		
					
		}
		
		//  If we have 'more results' add that item to the end of the last months items		        		
		if (addMoreLink) {			
			itemsForMonthYear.add(new EventListItem("", getResources().getString(R.string.EventList_More), null));
		}

		// Add the last arraylist built (the for each loop runs out of items before adding it)
		separatedAdapter.addSection(lastHeader, new EventListItemAdapter(EventListActivity.this, itemsForMonthYear));		
		
		return separatedAdapter;
	}
    
    
    // Occurs when a user selects an individual on the listview.    
    //private void ItemSelected(String eventId, String eventName)
    private void ItemSelected(EventListItem itemSelected)
    {    	    
    	try {
    		// First, see if this is an 'event' that was selected or a 'more records' item.
    		//  if more...load the next 50 records.
    		if (itemSelected.getId().equals("")) {    			
       	 		loadEventsWithProgressDialog();
       	 	}
       	 	else {
       	 		// Show the selected event detail
       	 		_progressText = String.format(getString(R.string.Event_ProgressDialog), itemSelected.getTitle());       	 		        	 		
       	 		loadEventWithProgressDialog(itemSelected);       	 		
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
    private void loadEventWithProgressDialog(final EventListItem eventSelected)
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
	           	 		
	           	 		SimpleDateFormat sdf = new SimpleDateFormat(EventListItem.EVENT_FULLDATE_FORMAT);
	           	 		String dateString = sdf.format(eventSelected.getEventDate());	           	 			           	 		           	 			           	 		
	           	 		intent.putExtra("dateselected", dateString); 
	           	 		
	           	 		startActivity(intent);	    				
	       			}
	       			else if (msg.what < 0) {
	       				// If < 0, the exception details are in the message bundle.  Throw it 
	       				//  and let the exception handler (below) handle it	       				
	       				Bundle b = msg.getData();
	       				throw ExceptionHelper.getAppExceptionFromBundle(b, "loadEventWithProgressDialog.handleMessage");	       				
	       			}	    				
    			} 			
    			catch (Exception e) {  				
    					ExceptionHelper.notifyUsers(e, EventListActivity.this);
    	    			ExceptionHelper.notifyNonUsers(e);
    				}
    			}    			    		
    	};
    	
    	Thread searchThread = new Thread() {  
    		public void run() {
    			try {    				    				
    				//GlobalState gs = (GlobalState) getApplication();
    				// zzz remove after testing 
    				GlobalState gs = GlobalState.getInstance(); 
    				
	    	    	WebServiceHandler wh1 = new WebServiceHandler(_appPrefs.getWebServiceUrl(), config.APPLICATION_ID_VALUE);
	    	    	
	    	    	EventResponse er = wh1.getEvent(gs.getUserName(), gs.getPassword(), gs.getSiteNumber(), eventSelected.getId());

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
    				
    				Message msg = handler.obtainMessage();
    				msg.what = -1;
    				msg.setData(ExceptionHelper.getBundleForException(e));	
    				handler.sendMessage(msg);    	    				
    			}    			       	    	    	    	
    		 }
    	};
    	searchThread.start();    	
    }
    
    
    //TODO:  problems here.  need to close just child activity (events listing) but doing so
    //  closes the entire application
    //
    public void handleNetworkExceptionWithRetry() {
    	
		// Show connection error dialog box
    	ChurchLifeDialog dialog = new ChurchLifeDialog(EventListActivity.this);
    	dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //close this activity (so the user can try again) - but take the user
            	//  to the person search activity
            	//finish();    		
            	TabHost host = ((TabActivity)getParent()).getTabHost();
                host.setCurrentTab(0);
                //zzz this closes the app!  only want to close the child activity
                EventListActivity.this.finish();
                //zzz probably show a 'reload' button on this form - or figure out
                //  a way to close this child activity without shutting down the application
                

            }
    	});    			    	
    	dialog.show(); 
    }
        
}
