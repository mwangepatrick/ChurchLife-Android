package com.acstech.churchlife.webservice;

import java.util.Date;

import com.acstech.churchlife.exceptionhandling.AppException;

//zzz deprecated - remove after testing

/**
 * 
 * @author softwarearchitect
 *
 */
public class EventResponse extends WebServiceObject {
	
	//Example event response:
	// {"EventName":"Hope for Tomorrow","Description":"","ParentId":"",
	//  "StopDate":"8\/2\/2011 9:00:00 AM","IsPublished":"True","Location":"Cafe - Rm 211","Setup":"0",
	//  "EventId":"f263cc58-a89c-4c09-9f4d-13fa4ce1f69f","EventTypeFields":{},"Note":"","Calendar":null,
	//  "EventType":"","EventTypeId":"","LocationId":"853ceda8-7ab5-4387-b380-278bf285954a",
	//  "Status":"Confirmed","StartDate":"8\/2\/2011 8:00:00 AM","Teardown":"0","AllowRegistration":"False",
	//  "CalendarId":"9da617de-e1ca-4bc1-8ca8-9e350a20000c","IsBooked":"True",
	//  "SiteId":"9568e0fa-d942-43e5-aaaa-26e2848ad768"
	// }
		
	
	// Event Id
	public String getEventId() 		throws AppException { return getStringValue("EventId", 0); }
	
	// EventTypeId	
	public int getEventTypeId() 		throws AppException { return Integer.parseInt(getStringValue("EventTypeId", 0)); }
	
	// EventType	
	public String getEventType()		throws AppException { return getStringValue("EventType", 0); }
	
	// LocationId
	public String getLocationId() 	throws AppException { return getStringValue("LocationId", 0); }

	// Location
	public String getLocation() 		throws AppException { return getStringValue("Location", 0); }
	
	// EventName
	public String getEventName() 	throws AppException { return getStringValue("EventName", 0); }
	
	// Description
	public String getDescription() 	throws AppException { return getStringValue("Description", 0); }

	// Note
	public String getNote() 			throws AppException { return getStringValue("Note", 0); }
				
	// Start Date	Example: 9\/7\/2011 6:00:00 PM
	public Date getStartDate() 		throws AppException {  return getDateValue("StartDate", 0); }

	// Stop Date	Example:  "9\/7\/2011 7:30:00 PM
	public Date getStopDate() 		throws AppException {  return getDateValue("StopDate", 0); }
		
	// IsPublished 
	public Boolean getIsPublished()	throws AppException { return Boolean.parseBoolean(getStringValue("IsPublished", 0)); }
		
	//  add more as needed (see above example json string)
	
	// Constructor
	public EventResponse(String responseString) throws AppException {
		super(responseString);	
	}
	
}
