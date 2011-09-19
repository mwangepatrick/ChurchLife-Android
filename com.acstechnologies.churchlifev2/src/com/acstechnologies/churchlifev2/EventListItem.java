package com.acstechnologies.churchlifev2;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EventListItem {

	static final String EVENT_FULLDATE_FORMAT = "yyyyMMddHHmmssZ";	
	static final String EVENT_DAYNAMEMONTHDAY_FORMAT = "EEE MMM d";
	static final String EVENT_TIME_FORMAT = "h:mm a";
	
	private String _id = "";
	private String _title = "";
	private Date _eventDate;
	
	// Unique id
	public String getId() {
		return _id;		
	}
	
	public String getTitle() {
		return _title;		
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
	
	public Date getEventDate() {
		return _eventDate;
	}
	

	public EventListItem(String id, String title, Date eventDate) {
		_id = id;
		_title = title;
		_eventDate = eventDate;
	}
	
}
