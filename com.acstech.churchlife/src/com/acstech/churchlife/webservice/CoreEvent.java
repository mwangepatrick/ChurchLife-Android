package com.acstech.churchlife.webservice;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.acstech.churchlife.DateHelper;
import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;

public class CoreEvent extends CoreObject {	
	
    public String CalendarId;		// guid
    public String Description;
    public String EventDateId; 	// guid
    public String EventId;		// guid
    public String EventName;
    public String EventType;
    public String EventTypeId;
    public boolean IsPublished;
    public String LocationId;
    public String Location;
    public Date StartDate;
    public Date StopDate;
    public String Status;
    public String PrimaryCalendarName;
    public boolean IsRecurringEvent;
    	

    // Factory Method - parse json
    public static CoreEvent GetCoreEventList(String json) throws AppException
    {    	
    	final String NULL = "null";
    	
    	CoreEvent event = new CoreEvent();
  	  	event._sourceJson = json;
  	  
  	  	try
  	  	{
  	  		JSONObject jo = new JSONObject(json);
  		  
  	  		event.CalendarId = jo.getString("CalendarId");
  	  		event.Description = jo.optString("Description");  		  
  	  		event.EventDateId = jo.getString("EventDateId");
  	  		event.EventId = jo.getString("EventId");  		  
  	  		event.EventName = jo.getString("EventName");
  	  		event.EventType = jo.getString("EventType");
  	  		event.EventTypeId = jo.getString("EventTypeId");  		  
  	  		event.IsPublished = jo.optBoolean("IsPublished");
  	  		event.LocationId = jo.optString("LocationId");
  	  		
  	  		if (jo.getString("Location").equals(NULL)) {
  	  			event.Location = "";
  	  		} 
  	  		else {
  	  			event.Location = jo.getString("Location");	
  	  		}
  	  			  	  		  	  	
  	  		event.StartDate = DateHelper.StringToDate(jo.getString("StartDate"), CoreEventDetail.EVENT_DATE_FORMAT);
  	  		event.StopDate = DateHelper.StringToDate(jo.getString("StopDate"), CoreEventDetail.EVENT_DATE_FORMAT);  		  
  	  		event.Status = jo.optString("Status");
  	  		event.PrimaryCalendarName = jo.optString("PrimaryCalendarName");
  	  		event.IsRecurringEvent = jo.optBoolean("IsRecurringEvent");
  	    }
  	  	catch (JSONException e) {
			
			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
								ExceptionInfo.TYPE.UNEXPECTED,
							  	ExceptionInfo.SEVERITY.MODERATE, 
							  	"100", 
							  	"CoreEvent.factory", 
							  	"Error creating object.");
			
		    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed

		    throw AppException.AppExceptionFactory(e, i); 
  	  	}	
  	  	return event;		
    }
    
	// Factory Method for CorePagedResult of CoreEvent objects
    public static CorePagedResult<List<CoreEvent>> GetCoreEventListPagedResult(String json) throws AppException
    { 
    	CorePagedResult<List<CoreEvent>> results = new CorePagedResult<List<CoreEvent>>(json);
  	  	
  	  	try {  	  		
  	  		JSONObject jo = new JSONObject(json);
  	  	 
  	  		results.PageCount = jo.getInt("PageCount");
  	  		results.PageIndex = jo.getInt("PageIndex");
  	  		results.PageSize = jo.getInt("PageSize");
  	  		
  	  		//Page (list of CoreCommentSummary object)  	  		
  	  		JSONArray ja = jo.getJSONArray("Page");
  	  		results.Page = new ArrayList<CoreEvent>();  	  	
  	  		
  	  		for (int i = 0; i < ja.length(); i++) {
  	  			JSONObject cs = ja.getJSONObject(i);  	  			
  	  			results.Page.add(CoreEvent.GetCoreEventList(cs.toString()));  	  			
  	  		}   	  		  	  	
	    }
		catch (JSONException e) {
			
			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
								ExceptionInfo.TYPE.UNEXPECTED,
							  	ExceptionInfo.SEVERITY.MODERATE, 
							  	"100", 
							  	"CoreEvent.factory", 
							  	"Error creating object.");
			
		    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed
	
		    throw AppException.AppExceptionFactory(e, i); 
		}	
  	  	return results;
    }
    
}
