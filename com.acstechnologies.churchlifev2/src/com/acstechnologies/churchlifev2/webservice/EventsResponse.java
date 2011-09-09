package com.acstechnologies.churchlifev2.webservice;

import java.util.Date;

import com.acstechnologies.churchlifev2.exceptionhandling.AppException;


/**
 * 
 * @author softwarearchitect
 *
 */
public class EventsResponse extends WebServiceObject {
	
	// Example of JSON response for a single event:
	// {"LocationId":"", "EventName":"In the Attic", "Description":"this is a test link www.acstechnologies.com",
	//  "StopDate":"9\/7\/2011 7:30:00 PM", "StartDate":"9\/7\/2011 6:00:00 PM",
	//  "IsPublished":true,"Location":"","EventId":"55fcc6ef-e066-478e-9354-0205f742b7f4",
	//  "Note":"", "EventType":"", "EventTypeId":""}
	//
	// Keep in mind that the base object is an array.  Accessing the 
	//  individual properties can only be done with an indexer.
	//
	// FUTURE:  Perhaps separate logic out into a collection class or provide iterator	
	
	// EventId	
	public String getEventId(int indexer) 		throws AppException { return getStringValue("EventId", indexer); }
	
	// EventTypeId	
	public int getEventTypeId(int indexer) 		throws AppException { return Integer.parseInt(getStringValue("EventTypeId", indexer)); }
	
	// EventType	
	public String getEventType(int indexer)		throws AppException { return getStringValue("EventType", indexer); }
	
	// LocationId
	public String getLocationId(int indexer) 	throws AppException { return getStringValue("LocationId", indexer); }

	// Location
	public String getLocation(int indexer) 		throws AppException { return getStringValue("Location", indexer); }
	
	// EventName
	public String getEventName(int indexer) 	throws AppException { return getStringValue("EventName", indexer); }
	
	// Description
	public String getDescription(int indexer) 	throws AppException { return getStringValue("Description", indexer); }

	// Note
	public String getNote(int indexer) 			throws AppException { return getStringValue("Note", indexer); }
			
	// Start Date	Example: 9\/7\/2011 6:00:00 PM
	public Date getStartDate(int indexer) 		throws AppException {  return getDateValue("StartDate", indexer); }

	// Stop Date	Example:  "9\/7\/2011 7:30:00 PM
	public Date getStopDate(int indexer) 		throws AppException {  return getDateValue("StopDate", indexer); }
	
	// IsPublished 
	public Boolean getIsPublished(int indexer)	throws AppException { return Boolean.parseBoolean(getStringValue("IsPublished", indexer)); }
	
	
	// Constructor
	public EventsResponse(String responseString) throws AppException {
		super(responseString);	
	}

}

