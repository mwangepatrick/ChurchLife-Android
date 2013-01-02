package com.acstech.churchlife;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import com.acstech.churchlife.exceptionhandling.ExceptionHelper;
import com.acstech.churchlife.listhandling.EventListItem;
import com.acstech.churchlife.listhandling.EventListItemAdapter;
import com.acstech.churchlife.listhandling.SeparatedListAdapter;
import com.acstech.churchlife.webservice.Api;
import com.acstech.churchlife.webservice.CoreEventDetail;
import com.acstech.churchlife.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class EventListActivity extends ChurchlifeBaseActivity {
	
	static final int DIALOG_PROGRESS_EVENTS = 0;
	static final int DIALOG_PROGRESS_EVENT = 1;
	
	private ProgressDialog _progressD;
	private String _progressText;

	EventListLoader _loader;
	AppPreferences _appPrefs;  		
	PullToRefreshListView _pullToRefreshView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try
        {   
        	_appPrefs = new AppPreferences(getApplicationContext());
        	
        	setTitle(R.string.Menu_Calendar);
        	setContentView(R.layout.eventlist);
        	  
        	bindControls();						// Set state variables to their form controls         

            // Wire up list on click - display event detail activity
            _pullToRefreshView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {  
                	EventListItem item = (EventListItem)parent.getAdapter().getItem(position);                 	                
                	ItemSelected(item);               	                 	
                }
              });
            
            // Wire up pull to refresh 
        	_pullToRefreshView.setOnRefreshListener(new OnRefreshListener<ListView>() {
                @Override
                public void onRefresh(PullToRefreshBase<ListView> refreshView) {  
                	_loader = null; //ensure a new search and not a 'More Items' operation
                	loadListWithProgressDialog(true, false);
                }
            });
        	
        	// would be GREAT to be able to manually tell the control to fire a refresh       
        	loadListWithProgressDialog(true, true);                                              
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
    	_pullToRefreshView = (PullToRefreshListView) findViewById(R.id.pull_to_refresh_listview);
    }
    
    
    /**
     * Displays a progress dialog and launches a background thread to connect to a web service
     *   to retrieve search results 
     *   
     */
    private void loadListWithProgressDialog(boolean nextResult, boolean showDialog)
    {           	    
    	if (showDialog) {
    		showDialog(DIALOG_PROGRESS_EVENTS);
    	}
    	
    	try
    	{	
    		if (_loader == null) {
    			
    			// Search for 1 year's worth of data starting today (at 00:00 time)
		    	Calendar cal = Calendar.getInstance();    		    	    		    	
		    	cal.set(Calendar.HOUR, 0);
		    	cal.set(Calendar.MINUTE, 0);
		    	cal.set(Calendar.SECOND, 0);
		    	    
		    	Date start = cal.getTime();    		    	
		        cal.roll(Calendar.YEAR, 1);
		        Date stop = cal.getTime();
		        
    			_loader = new EventListLoader(this, start, stop);	    				       	
    		}
    		
    		// see onListLoaded below for the next steps (after load is done)
    		if (nextResult) {
    			_loader.LoadNext(onListLoaded);
    		}
    		else {
    			_loader.LoadPrevious(onListLoaded);
    		}
    	}
    	catch (Exception e) {
			ExceptionHelper.notifyUsers(e, EventListActivity.this);
    		ExceptionHelper.notifyNonUsers(e); 				    				
		}      	
    }
    
    // display the results from the loader operation
    final Runnable onListLoaded = new Runnable() {
        public void run() {
        	
        	try
        	{
        		removeDialog(DIALOG_PROGRESS_EVENTS);
        		
	        	if (_loader.success())	{	        	 
	        		
	        		ListView lv1 = _pullToRefreshView.getRefreshableView();			
	        			        		
		     		// set items to list
    				// save index and top position (preserve scroll location)
    				int index = lv1.getFirstVisiblePosition();
    				View v = lv1.getChildAt(0);
    				int top = (v == null) ? 0 : v.getTop();
    				
    				lv1.setAdapter(getSeparatedListAdapter(_loader.getList())); 

    				// restore scroll position
    				lv1.setSelectionFromTop(index, top);
    				
    				_pullToRefreshView.onRefreshComplete();    				
	          	}
	        	else {
	        		throw _loader.getException();
	        	}
        	}
        	catch (Throwable e) {
        		ExceptionHelper.notifyUsers(e, EventListActivity.this);
        		ExceptionHelper.notifyNonUsers(e); 	        	
			} 	        		        
        }
    };

    
    /**
     * Iterates over the class level event item list and adds those events to a
     *  separated list adapter (headers for month/year)
     *   
     * @return
     */
    public SeparatedListAdapter getSeparatedListAdapter(ArrayList<EventListItem> allItems) {
		 
		SeparatedListAdapter separatedAdapter = new SeparatedListAdapter(this);  
		
		String moreItems = getResources().getString(R.string.EventList_More);		
		String lastHeader = "initial";							//can't be "" as the header for a 'No Results Found is empty string'
		ArrayList<EventListItem> itemsForCurrentHeader = null;
		
		for (EventListItem event : allItems) {
			
			if (event.getTitle().equals(moreItems)) {
				itemsForCurrentHeader.add(event);				
			}
			else
			{										
				// if new header...create a new EventListItemAdapter  
				if (event.getHeaderText().equals(lastHeader) == false) {
						
					if (itemsForCurrentHeader != null) {										
						// add the items (with header) to the separated list adapter (the first time in this is null)
						separatedAdapter.addSection(lastHeader, new EventListItemAdapter(EventListActivity.this, itemsForCurrentHeader));
					}					
					
					// start a new list/header
					itemsForCurrentHeader = new ArrayList<EventListItem>();							
					lastHeader = event.getHeaderText();					
				}
				
				//add this item to the current header (always)
				itemsForCurrentHeader.add(event);
			}
		}
		
		// Add the last arraylist built (the for each loop runs out of items before adding it)
		separatedAdapter.addSection(lastHeader, new EventListItemAdapter(EventListActivity.this, itemsForCurrentHeader));		
		
		return separatedAdapter;
	}
    
    
    /**
     * Occurs when a user selects an event on the listview.
     * 
     * @param itemSelected
     */
    private void ItemSelected(EventListItem itemSelected)
    {    	    
    	try {
    		// First, see if this is an 'event' that was selected or a 'more records' item.
    		//  if 'more records'...load the next set of records.
    		if (itemSelected.getId().equals("")) {    			
    			loadListWithProgressDialog(true, true);
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
    				GlobalState gs = GlobalState.getInstance(); 		
    			 	Api apiCaller = new Api(_appPrefs.getWebServiceUrl(), config.APPLICATION_ID_VALUE);		

    			 	CoreEventDetail event = apiCaller.event(gs.getUserName(), gs.getPassword(), gs.getSiteNumber(), eventSelected.getCalendarId(), eventSelected.getId());

	    	    	// Return the response object (as string) to the message handler above
	    	    	Message msg = handler.obtainMessage();		
	    	    	msg.what = 0;
	    	    	
	    	    	Bundle b = new Bundle();
    				b.putString("event", event.toJsonString());   
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
    	
}
