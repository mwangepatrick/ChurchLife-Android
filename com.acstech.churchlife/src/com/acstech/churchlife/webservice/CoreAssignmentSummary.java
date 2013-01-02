package com.acstech.churchlife.webservice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.acstech.churchlife.DateHelper;
import com.acstech.churchlife.R;
import com.acstech.churchlife.StringHelper;
import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;

public class CoreAssignmentSummary extends CoreObject {

    public int AssignmentTypeID;
    public String AssignmentType;    
    public Date EarliestDueDate;
    public int TotalConnections;    
    public String AssignmentColor;
    
    public String getDescription() {
    	String DATE_FORMAT = "MMMM d, yyyy";		
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
		
		if (TotalConnections == 0){
			return String.format(sdf.format(EarliestDueDate));			
		}
		else if(TotalConnections == 1){
			return String.format("%s Visit: %s", TotalConnections, sdf.format(EarliestDueDate));
		}
		else {
			return String.format("%s Visits: %s", TotalConnections, sdf.format(EarliestDueDate));
		}		
    }
    
    // currently NO icons for these
    public int getIconResourceId() {
    	return 0;
    }
   
    // Factory Method - parse json
    public static CoreAssignmentSummary GetCoreAssignmentSummary(String json) throws AppException
    {    	
    	CoreAssignmentSummary obj = new CoreAssignmentSummary();
    	obj._sourceJson = json;
  	  
  	  	try
  	  	{
  	  		JSONObject jo = new JSONObject(json);
		
	  		obj.AssignmentTypeID = jo.getInt("ConnectionTypeId");
	  		obj.AssignmentType = jo.getString("ConnectionTypeDescription");
	  		obj.EarliestDueDate = DateHelper.StringToDate(jo.getString("EarliestDueDate"), "MM/dd/yyyy");
	  		obj.TotalConnections = jo.getInt("TotalConnections");
	  		obj.AssignmentColor = StringHelper.NullOrEmpty(jo.optString("ConnectionColor")); 		
  	  	}
  	  	catch (JSONException e) {
			
			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
								ExceptionInfo.TYPE.UNEXPECTED,
							  	ExceptionInfo.SEVERITY.MODERATE, 
							  	"100", 
							  	"CoreAssignmentSummary.factory", 
							  	"Error creating object.");
			
		    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed

		    throw AppException.AppExceptionFactory(e, i); 
  	  	}	
  	  	return obj;		
    }
    
    // Factory Method for CorePagedResult of CoreCommentSummary objects
    public static CorePagedResult<List<CoreAssignmentSummary>> GetCoreAssignmentSummaryPagedResult(String json) throws AppException
    { 
    	CorePagedResult<List<CoreAssignmentSummary>> results = new CorePagedResult<List<CoreAssignmentSummary>>(json);
  	  	
  	  	try {  	  		
  	  		JSONObject jo = new JSONObject(json);
  	  	 
  	  		results.PageCount = jo.getInt("PageCount");
  	  		results.PageIndex = jo.getInt("PageIndex");
  	  		results.PageSize = jo.getInt("PageSize");
  	  		
  	  		//Page (list of CoreAssignmentSummary object)  	  		
  	  		JSONArray ja = jo.getJSONArray("Page");
  	  		results.Page = new ArrayList<CoreAssignmentSummary>();  	  	
  	  		
  	  		for (int i = 0; i < ja.length(); i++) {
  	  			JSONObject cs = ja.getJSONObject(i);  	  			
  	  			results.Page.add(CoreAssignmentSummary.GetCoreAssignmentSummary(cs.toString()));  	  			
  	  		}   	  		  	  	
	    }
		catch (JSONException e) {
			
			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
								ExceptionInfo.TYPE.UNEXPECTED,
							  	ExceptionInfo.SEVERITY.MODERATE, 
							  	"100", 
							  	"CoreAssignmentSummary.factory", 
							  	"Error creating object.");
			
		    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed
	
		    throw AppException.AppExceptionFactory(e, i); 
		}	
  	  	return results;
    }

}
