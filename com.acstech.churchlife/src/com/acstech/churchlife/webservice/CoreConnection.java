package com.acstech.churchlife.webservice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import com.acstech.churchlife.DateHelper;
import com.acstech.churchlife.R;
import com.acstech.churchlife.StringHelper;
import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;

public class CoreConnection  extends CoreObject {

	public int ConnectionId;
	public int ConnectionTypeId;	
	public String ConnectionTypeDescription;
	
	public int FamId;
	public boolean FamilyConnection;
	public Date DateCreated;
	public String Comment;
	//OpenField
	public String PermissionLevel;
	public String ContactType;	
	public Date DueDate;
	public String DueDateLong;
	public boolean Completed;
	public int TeamMemberCount;
	public CoreIndividual ContactInformation;
    public List<CoreIndividual> TeamMembers;
    public List<CoreResponseType> Responses;
    
    public String getDescription() {
    	String DATE_FORMAT = "MM/dd/yyyy";		
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);		
		return String.format("Due date: %s \n%s", sdf.format(DueDate), Comment);
    }

    
    public String getFullTitle() {
    	String DATE_FORMAT = "MM/dd/yyyy";		
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);		
		String result = String.format("%s %s", sdf.format(DueDate), ConnectionTypeDescription);
		
		// list of all people assigned to this connection
		String authorList = "";
		for (CoreIndividual indv : TeamMembers) {
			if (authorList.length() > 0 ) { authorList = authorList + ", "; }
			authorList = authorList + String.format("%s %s", indv.FirstName, indv.LastName);
		}
			
		if (authorList.length() > 0) {
			result = String.format("%s by %s", result, authorList);
		}
	
		/*
		if (Comment.trim().length() > 0) {
			result = String.format("%s \n%s", result, Comment);
		}
		*/	
		return result;
    }
    
    public int getIconResourceId() {
    	if (TeamMembers.size() > 1) {
  			return R.drawable.ic_action_team;
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
        
    public boolean containsResponse(int responseTypeId) {
    	for (CoreResponseType r : Responses) {
    		if (r.RespID == responseTypeId) {
    			return true;
    		}
    	}
    	return false;
    }
    
    //NOTE:  Hardcoded string literal "ALL"
    //  seems to be unique to this object (at present)  
	public boolean hasChangePermission() {    	    	
    	return (PermissionLevel.toUpperCase(Locale.US).equals("ALL"));
    }
    
    // Factory Method - parse json
    //
    // NOTE:  When using this object in a list of an individuals' recent connections, 
    //			some of these properties are NOT supplied.
    public static CoreConnection GetCoreConnection(String json) throws AppException
    {    	
      CoreConnection obj = new CoreConnection();
      obj._sourceJson = json;
  	  
  	  try
  	  {
  		  JSONObject jo = new JSONObject(json);
  		  
  		  obj.ConnectionId = jo.getInt("ConnectionId");  		    		 
  		  obj.ConnectionTypeId = jo.getInt("ConnectionTypeId");
  		  obj.ConnectionTypeDescription = jo.getString("ConnectionTypeDescription");  		  
  		  obj.FamId = jo.optInt("FamId");
  		  obj.FamilyConnection = jo.optBoolean("FamilyConnection");
  		  obj.PermissionLevel = jo.optString("PermissionLevel");
  		  
  		  String createDate = jo.optString("DateCreated");
  		  if (!createDate.equals(""))  {  			  
  			  obj.DateCreated = DateHelper.StringToDate(createDate, "MM/dd/yyyy");
  		  }
  		  
  		  obj.Comment = StringHelper.NullOrEmpty(jo.optString("Comment"));  		    		    		 
  		  obj.ContactType = jo.getString("ContactType");
  		  obj.DueDate = DateHelper.StringToDate(jo.getString("DueDate"), "MM/dd/yyyy");
  		  obj.DueDateLong = jo.getString("DueDateLong");  		
  		  obj.Completed = jo.getBoolean("Completed");
  		  obj.TeamMemberCount = jo.getInt("TeamMemberCount");
  		
  		  JSONObject ci = jo.getJSONObject("ContactInformation");
  		  obj.ContactInformation = CoreIndividual.GetCoreIndividual(ci.toString());
	  		
  		  // Team Members (list of CoreIndividual)	  		
  		  JSONArray ja = jo.getJSONArray("TeamMembers");	
  		  obj.TeamMembers = new ArrayList<CoreIndividual>();
	  		  
  		  for (int i = 0; i < ja.length(); i++) {
  			  JSONObject member = ja.getJSONObject(i);  	  			
  			  obj.TeamMembers.add(CoreIndividual.GetCoreIndividual(member.toString()));  	  			
  		  }
  		  
  		  // Responses (list of CoreResponseType)	  		
  		  JSONArray jar = jo.optJSONArray("Responses");	
  		  obj.Responses = new ArrayList<CoreResponseType>();
	  		  
  		  if (jar != null) {  			  
	  		  for (int i = 0; i < jar.length(); i++) {
	  			  JSONObject r = jar.getJSONObject(i);  	  			
	  			  obj.Responses.add(CoreResponseType.GetCoreResponseType(r.toString()));  	  			
	  		  }
  		  }
  	  }
  	  catch (JSONException e) {
			
			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
								ExceptionInfo.TYPE.UNEXPECTED,
							  	ExceptionInfo.SEVERITY.MODERATE, 
							  	"100", 
							  	"CoreConnection.factory", 
							  	"Error creating object.");
			
		    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed

		    throw AppException.AppExceptionFactory(e, i); 
  	  }	
  	  return obj;		
    }

    // Factory Method for CorePagedResult of CoreConnection objects
    public static CorePagedResult<List<CoreConnection>> GetCoreConnectionPagedResult(String json) throws AppException
    { 
    	CorePagedResult<List<CoreConnection>> results = new CorePagedResult<List<CoreConnection>>(json);
  	  	
  	  	try {  	  		
  	  		JSONObject jo = new JSONObject(json);
  	  	 
  	  		results.PageCount = jo.getInt("PageCount");
  	  		results.PageIndex = jo.getInt("PageIndex");
  	  		results.PageSize = jo.getInt("PageSize");
  	  		
  	  		//Page (list of CoreConnection object)  	  		
  	  		JSONArray ja = jo.getJSONArray("Page");
  	  		results.Page = new ArrayList<CoreConnection>();  	  	
  	  		
  	  		for (int i = 0; i < ja.length(); i++) {
  	  			JSONObject cs = ja.getJSONObject(i);  	  			
  	  			results.Page.add(CoreConnection.GetCoreConnection(cs.toString()));  	  			
  	  		}   	  		  	  	
	    }
		catch (JSONException e) {
			
			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
								ExceptionInfo.TYPE.UNEXPECTED,
							  	ExceptionInfo.SEVERITY.MODERATE, 
							  	"100", 
							  	"CoreConnection.factory", 
							  	"Error creating object.");
			
		    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed
	
		    throw AppException.AppExceptionFactory(e, i); 
		}	
  	  	return results;
    }

}
