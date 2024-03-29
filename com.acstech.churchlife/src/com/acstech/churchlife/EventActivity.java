package com.acstech.churchlife;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionHelper;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;
import com.acstech.churchlife.listhandling.DefaultListItem;
import com.acstech.churchlife.listhandling.DefaultListItemAdapter;
import com.acstech.churchlife.listhandling.EventListItem;
import com.acstech.churchlife.webservice.CoreEventDetail;
import com.acstech.churchlife.R;

public class EventActivity extends ChurchlifeBaseActivity {

	CoreEventDetail _event;					// passed in event detail (from web service call)
	Date _dateSelected;						// passed in date selected (used in recurring events)
	
	AppPreferences _appPrefs;  	
	
	TextView titleTextView;
	TextView timeRangeTextView;
	TextView monthNameTextView;
	TextView monthDayTextView;
	ListView detailsListview;
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        try
	        {        	
	        	 _appPrefs = new AppPreferences(getApplicationContext());
	        	 
	        	 setContentView(R.layout.event);
	        	 setTitle(R.string.Menu_Calendar);
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
	            	 _event = CoreEventDetail.GetCoreEventDetail(extraBundle.getString("event"));            	 
	            	 SimpleDateFormat sdf = new SimpleDateFormat(EventListItem.EVENT_FULLDATE_FORMAT);	            	 
	            	 _dateSelected = sdf.parse(extraBundle.getString("dateselected"));
	            	 
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
	    	
	    	detailsListview = (ListView)this.findViewById(R.id.detailsListview);
	    }
	    
	    
	    /**
	     *  Sets the control values to the record that was passed to this activity.
	     *  
	     * @throws AppException
	     */
	    private void bindData() throws AppException {
	    		    	    		    	
	    	SimpleDateFormat displayTime = new SimpleDateFormat(EventListItem.EVENT_TIME_FORMAT);
	    	String start = displayTime.format(_event.StartDate);
	    	String stop = displayTime.format(_event.StopDate);
	    		    	
	    	// passed in selected date - get the month and day portion of the passed in date	    	
	    	monthNameTextView.setText(new SimpleDateFormat("MMM").format(_dateSelected));	    	
	    	monthDayTextView.setText(new SimpleDateFormat("dd").format(_dateSelected));
	    	
			titleTextView.setText(_event.EventName);			
			timeRangeTextView.setText(String.format("%s - %s", start, stop));
			
			// listview of details
			ArrayList<DefaultListItem> itemList = new ArrayList<DefaultListItem> ();
			
			// location
			itemList.add(new DefaultListItem("0", _event.Location, getResources().getString(R.string.Event_Location)));
			
			// description
			DefaultListItem description = new DefaultListItem("1", _event.Description, getResources().getString(R.string.Event_Description));
			description.setContainsHtml(true);
			itemList.add(description);
			
			detailsListview.setAdapter(new DefaultListItemAdapter(this, itemList, R.layout.listitem_withtitle));				
	    }
	    
}
