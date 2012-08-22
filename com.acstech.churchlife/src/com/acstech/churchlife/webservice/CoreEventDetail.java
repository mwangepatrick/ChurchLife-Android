package com.acstech.churchlife.webservice;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.acstech.churchlife.DateHelper;
import com.acstech.churchlife.StringHelper;
import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;

public class CoreEventDetail extends CoreObject {

	public final static String EVENT_DATE_FORMAT = "yyyy-MM-dd hh:mm:ss.SSS";
	
    public String EventDateId;
    public boolean IsAllDay;
    public String ParentId;
    public int Setup;
    public String SiteId;
    public int Teardown;
    public String Note;
    public boolean IsBooked;
    /* commented out - not used by mobile?  couldn't parse
       public Dictionary<string, string> EventTypeFields { get; set; }
    */
    public String Description;               
    public String EventId;
    public String EventName;
    public String EventType; 
    public String EventTypeId;
    public boolean IsPublished;
    public String LocationId;
    public String Location;    
    public Date StartDate;
    public Date StopDate;
    public String Status;
    public String CalendarName;
    public boolean IsRecurringEvent;
    public String CalendarId;
    public boolean AllowRegistration;
    
	
	// Factory Method - parse json
    public static CoreEventDetail GetCoreEventDetail(String json) throws AppException
    {    	        
    	CoreEventDetail event = new CoreEventDetail();
  	  	event._sourceJson = json;
  	  
  	  	try
  	  	{
  	  		JSONObject jo = new JSONObject(json);
  		  
  	    	event.EventDateId= jo.getString("EventDateId");
	    	event.IsAllDay = jo.optBoolean("IsAllDay");
  	    	event.ParentId= jo.optString("ParentId");
	  		event.Setup = jo.optInt("Setup");
	  		event.SiteId = jo.getString("SiteId");
	  		event.Teardown = jo.optInt("Teardown");
	  		event.Note = jo.optString("Note");
	  		event.IsBooked = jo.optBoolean("IsBooked");  	    
  	  		event.Description = StringHelper.NullOrEmpty(jo.getString("Description"));  	  		  	  	
  	  		event.EventDateId = jo.getString("EventDateId");
  	  		event.EventId = jo.getString("EventId");  		  
  	  		event.EventName = jo.getString("EventName");
  	  		event.EventType = jo.getString("EventType");
  	  		event.EventTypeId = jo.getString("EventTypeId");  		  
  	  		event.IsPublished = jo.optBoolean("IsPublished");
  	  		event.LocationId = jo.optString("LocationId");  	  		
  	  		event.Location = StringHelper.NullOrEmpty(jo.getString("Location"));  	  		  	   	  			  	  		  	  
  	  		event.StartDate = DateHelper.StringToDate(jo.getString("StartDate"), EVENT_DATE_FORMAT);
  	  		event.StopDate = DateHelper.StringToDate(jo.getString("StopDate"), EVENT_DATE_FORMAT);  		  
  	  		event.Status = jo.optString("Status");
  	  		event.CalendarName = jo.optString("CalendarName");
  	  		event.IsRecurringEvent = jo.optBoolean("IsRecurringEvent");
  	  		event.CalendarId = jo.optString("CalendarId");
  	    	event.AllowRegistration = jo.optBoolean("AllowRegistration");
  	    }
  	  	catch (JSONException e) {
			
			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
								ExceptionInfo.TYPE.UNEXPECTED,
							  	ExceptionInfo.SEVERITY.MODERATE, 
							  	"100", 
							  	"CoreEventDetail.factory", 
							  	"Error creating object.");
			
		    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed

		    throw AppException.AppExceptionFactory(e, i); 
  	  	}	
  	  	return event;		
    }

}
