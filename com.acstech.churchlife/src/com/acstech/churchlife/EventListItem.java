package com.acstech.churchlife;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.acstech.churchlife.webservice.CoreEvent;

public class EventListItem {

    final static String EVENT_ITEM_ID = "id";  
    final static String EVENT_ITEM_TITLE = "title";  
    final static String EVENT_ITEM_TIME = "time";  
    final static String EVENT_ITEM_DATE = "date"; 
    
	static final String EVENT_FULLDATE_FORMAT = "yyyyMMddHHmmssZ";
	static final String EVENT_MONTHYEAR_FORMAT = "MMMM yyyy";	
	static final String EVENT_DAYNAMEMONTHDAY_FORMAT = "EEE MMM d";	
	static final String EVENT_TIME_FORMAT = "h:mm a";
	
	private String _id = "";
	private String _title = "";
	private Date _eventDate;
	private String _location = "";
	
	// Unique id
	public String getId() {
		return _id;		
	}
	
	public String getTitle() {
		return _title;		
	}
	
	public String getLocation() {
		return _location;
	}
	
	// symptom of using this item as the 'more results' item.  Remove IF statement when we use
	//  a different view item for the 'More Results' item
	public String getDateText() {
		if (_eventDate != null) {
			SimpleDateFormat displayDate = new SimpleDateFormat(EVENT_DAYNAMEMONTHDAY_FORMAT);    	
			return displayDate.format(_eventDate);
		}
		else {
			return "";
		}
	}
	
	// symptom of using this item as the 'more results' item.  Remove IF statement when we use
	//  a different view item for the 'More Results' item
	public String getTimeText() {
		if (_eventDate != null) {
			SimpleDateFormat displayTime = new SimpleDateFormat(EVENT_TIME_FORMAT);
			return displayTime.format(_eventDate);
		}
		else {
			return  "";
		}
	}
		
	// symptom of using this item as the 'more results' item.  Remove IF statement when we use
		//  a different view item for the 'More Results' item
	public String getHeaderText() {
		if (_eventDate != null) {
			String EVENT_HEADER_FORMAT = "EEEE,  MMMM d, yyyy";
			SimpleDateFormat displayTime = new SimpleDateFormat(EVENT_HEADER_FORMAT);
			return displayTime.format(_eventDate);
		}
		else {
			return  "";
		}
	}
	
	public Date getEventDate() {
		return _eventDate;
	}
	
	
	//CTOR
	public EventListItem(String title)
	{
		_title = title;
	}
	
	//CTOR
	public EventListItem(CoreEvent event) {
		
		_id = event.EventId;
		_title = event.EventName;
		_eventDate = event.StartDate;	
		_location = event.Location;
	}		
}
