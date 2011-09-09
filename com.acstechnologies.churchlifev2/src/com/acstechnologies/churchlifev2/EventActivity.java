package com.acstechnologies.churchlifev2;

import java.text.SimpleDateFormat;

import android.os.Bundle;
import android.widget.TextView;

import com.acstechnologies.churchlifev2.exceptionhandling.AppException;
import com.acstechnologies.churchlifev2.exceptionhandling.ExceptionHelper;
import com.acstechnologies.churchlifev2.exceptionhandling.ExceptionInfo;
import com.acstechnologies.churchlifev2.webservice.EventResponse;

public class EventActivity extends OptionsActivity {

	EventResponse _wsEvent;					// results of the web service call
	AppPreferences _appPrefs;  	
	
	TextView titleTextView;
	TextView timeRangeTextView;
	TextView locationTextView;
	TextView monthNameTextView;
	TextView monthDayTextView;
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        try
	        {        	
	        	 _appPrefs = new AppPreferences(getApplicationContext());
	        	 
	        	 setContentView(R.layout.event);
	        	 
	        	 bindControls();							// Set state variables to their form controls	        	 	       
	        	 
	        	 // This activity MUST be passed the event object (as json string)
	        	 Bundle extraBundle = this.getIntent().getExtras();
	             if (extraBundle == null) {
	            	 throw AppException.AppExceptionFactory(
	            			 ExceptionInfo.TYPE.UNEXPECTED,
							 ExceptionInfo.SEVERITY.CRITICAL, 
							 "100",           												    
							 "EventActivity.onCreate",
							 "No event data was passed to the Event activity.");
	             }
	             else {
	            	 _wsEvent = new EventResponse(extraBundle.getString("event"));
	            	 bindData();
	             }	        	 
	        }
	    	catch (Exception e) {
	    		ExceptionHelper.notifyUsers(e, EventActivity.this);
	    		ExceptionHelper.notifyNonUsers(e);
	    	}  	        
	    }

	    /**
	     *  Links state variables to their respective form controls
	     */
	    private void bindControls(){
	    	
	    	monthNameTextView = (TextView)this.findViewById(R.id.monthNameTextView);
	    	monthDayTextView = (TextView)this.findViewById(R.id.monthDayTextView);
	    	
	    	titleTextView = (TextView)this.findViewById(R.id.titleTextView);
	    	timeRangeTextView = (TextView)this.findViewById(R.id.timeRangeTextView);
	    	locationTextView = (TextView)this.findViewById(R.id.locationTextView);	    	
	    }
	    
	    
	    /**
	     *  Sets the control values to the record that was passed to this activity.
	     *  
	     * @throws AppException
	     */
	    private void bindData() throws AppException {
	    		    	    		    	
	    	SimpleDateFormat displayTime = new SimpleDateFormat(EventListActivity.EVENT_TIME_FORMAT);
	    	String start = displayTime.format(_wsEvent.getStartDate());
	    	String stop = displayTime.format(_wsEvent.getStopDate());
	    		    	
	    	monthNameTextView.setText(new SimpleDateFormat("MMM").format(_wsEvent.getStartDate()));	    	
	    	monthDayTextView.setText(new SimpleDateFormat("dd").format(_wsEvent.getStartDate()));
	    	
			titleTextView.setText(_wsEvent.getEventName());			
			timeRangeTextView.setText(String.format("%s - %s", start, stop));
			locationTextView.setText(_wsEvent.getLocation());
	    }
	    
}
