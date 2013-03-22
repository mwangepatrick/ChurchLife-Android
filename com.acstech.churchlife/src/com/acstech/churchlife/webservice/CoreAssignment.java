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

public class CoreAssignment extends CoreObject {

	public int AssignmentID;
    public int AssignmentTypeID;
    public String AssignmentType;  
    public String AssignmentColor;
    public String ContactType;    
    public Date DueDate;
    public boolean Completed;
    public CoreIndividual ContactInformation;
    public List<CoreIndividual> TeamMembers;
    
    public String getDescription() {
    	String DATE_FORMAT = "MM/dd/yyyy";		
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);		
		return String.format("due date: %s", sdf.format(DueDate));
    }
    
    public int getIconResourceId() {
    	if (TeamMembers.size() > 1) {
  			return R.drawable.ic_team_color;
  		}
  		else {
  			return 0;	
  		}  		
    }

    // returns a string array of team member names (used when displaying)
    public String[] getTeamMemberList() {
    
    	String[] items = new String[TeamMembers.size()];
		for (int i=0;i< items.length;i++){  
    		items[i] = TeamMembers.get(i).getDisplayNameForList();               			
    	}
		return items;
    }
      	
    // Factory Method - parse json
    public static CoreAssignment GetCoreAssignment(String json) throws AppException
    {    	
    	CoreAssignment obj = new CoreAssignment();
    	obj._sourceJson = json;
  	  
  	  	try
  	  	{
  	  		JSONObject jo = new JSONObject(json);
		
  	  		obj.AssignmentID = jo.getInt("ConnectionId");
	  		obj.AssignmentTypeID = jo.getInt("ConnectionTypeId");
	  		obj.AssignmentType = jo.getString("ConnectionTypeDescription");
	  		obj.AssignmentColor = StringHelper.NullOrEmpty(jo.optString("ConnectionColor"));
	  		obj.ContactType = jo.getString("ContactType");	  		
	  		obj.DueDate = DateHelper.StringToDate(jo.getString("DueDate"), "MM/dd/yyyy");
	  		obj.Completed = jo.getBoolean("Completed");
	  		 	
	  		JSONObject ci = jo.getJSONObject("ContactInformation");
	  		obj.ContactInformation = CoreIndividual.GetCoreIndividual(ci.toString());
	  		
	  		// Team Members (list of CoreIndividual)	  		
	  		JSONArray ja = jo.getJSONArray("TeamMembers");	
	  		obj.TeamMembers = new ArrayList<CoreIndividual>();
	  		  
	  		for (int i = 0; i < ja.length(); i++) {
	  			JSONObject member = ja.getJSONObject(i);  	  			
	  			obj.TeamMembers.add(CoreIndividual.GetCoreIndividual(member.toString()));  	  			
	  		 } 
	  		
  	  	}
  	  	catch (JSONException e) {
			
			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
								ExceptionInfo.TYPE.UNEXPECTED,
							  	ExceptionInfo.SEVERITY.MODERATE, 
							  	"100", 
							  	"CoreAssignment.factory", 
							  	"Error creating object.");
			
		    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed

		    throw AppException.AppExceptionFactory(e, i); 
  	  	}	
  	  	return obj;		
    }
    
    // Factory Method for CorePagedResult of CoreAssignment objects
    public static CorePagedResult<List<CoreAssignment>> GetCoreAssignmentPagedResult(String json) throws AppException
    { 
    	CorePagedResult<List<CoreAssignment>> results = new CorePagedResult<List<CoreAssignment>>(json);
  	  	
  	  	try {  	  		
  	  		JSONObject jo = new JSONObject(json);
  	  	 
  	  		results.PageCount = jo.getInt("PageCount");
  	  		results.PageIndex = jo.getInt("PageIndex");
  	  		results.PageSize = jo.getInt("PageSize");
  	  		
  	  		//Page (list of CoreAssignment object)  	  		
  	  		JSONArray ja = jo.getJSONArray("Page");
  	  		results.Page = new ArrayList<CoreAssignment>();  	  	
  	  		
  	  		for (int i = 0; i < ja.length(); i++) {
  	  			JSONObject cs = ja.getJSONObject(i);  	  			
  	  			results.Page.add(CoreAssignment.GetCoreAssignment(cs.toString()));  	  			
  	  		}   	  		  	  	
	    }
		catch (JSONException e) {
			
			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
								ExceptionInfo.TYPE.UNEXPECTED,
							  	ExceptionInfo.SEVERITY.MODERATE, 
							  	"100", 
							  	"CoreAssignment.factory", 
							  	"Error creating object.");
			
		    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed
	
		    throw AppException.AppExceptionFactory(e, i); 
		}	
  	  	return results;
    }

}
