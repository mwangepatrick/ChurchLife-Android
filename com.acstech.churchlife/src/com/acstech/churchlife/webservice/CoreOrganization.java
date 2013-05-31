package com.acstech.churchlife.webservice;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;

public class CoreOrganization extends CoreObject {

    public int OrgId;
    public int LevelTypeId;      
    public String Name;
    public String RefName;
    public String Address;
    public String Phone; 
    public String Email;
  
    // Helper(s)
	public String getPhoneNumberToDial()throws AppException {
		return Phone.replace("-", "");		
	}
		
	
	 public static CoreOrganization GetCoreOrganization(String json) throws AppException
     {    	 
		CoreOrganization org = new CoreOrganization();
   	  	org._sourceJson = json;
   	  
   	  	try
   	  	{
   	  		JSONObject jo = new JSONObject(json);
	
   	  		org.OrgId = jo.getInt("OrgId");
   	  		org.LevelTypeId = jo.getInt("LevelTypeId");
   	  		org.Name = jo.getString("Name"); 
   	 		org.RefName = jo.getString("RefName"); 
   			org.Address = jo.getString("Address"); 
   			org.Phone = jo.getString("Phone"); 
   			org.Email = jo.getString("Email"); 
   	    
   	  	}
   	  	catch (JSONException e) {
			
			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
								ExceptionInfo.TYPE.UNEXPECTED,
							  	ExceptionInfo.SEVERITY.MODERATE, 
							  	"100", 
							  	"CoreOrganization.factory", 
							  	"Error creating object.");
			
		    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed

		    throw AppException.AppExceptionFactory(e, i); 
   	  	}	
   	  	return org;		
     }
     
     // Factory Method for list of CoreAcsOrganization objects
     public static CorePagedResult<List<CoreOrganization>> GetCoreOrganizationPagedResult(String json) throws AppException
     { 
     	CorePagedResult<List<CoreOrganization>> results = new CorePagedResult<List<CoreOrganization>>(json);
   	  	
   	  	try {  	  		
   	  		JSONObject jo = new JSONObject(json);
   	  	 
   	  		results.PageCount = jo.getInt("PageCount");
   	  		results.PageIndex = jo.getInt("PageIndex");
   	  		results.PageSize = jo.getInt("PageSize");
   	  		
   	  		//Page (list of CoreIndividual objects)  	  		
   	  		JSONArray ja = jo.getJSONArray("Page");
   	  		results.Page = new ArrayList<CoreOrganization>();  	  	
   	  		
   	  		for (int i = 0; i < ja.length(); i++) {
   	  			JSONObject cs = ja.getJSONObject(i);  	  			
   	  			results.Page.add(CoreOrganization.GetCoreOrganization(cs.toString()));  	  			
   	  		}   	  		  	  	
 	    }
 		catch (JSONException e) {
 			
 			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
 								ExceptionInfo.TYPE.UNEXPECTED,
 							  	ExceptionInfo.SEVERITY.MODERATE, 
 							  	"100", 
 							  	"CoreOrganization.factory", 
 							  	"Error creating object.");
 			
 		    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed
 	
 		    throw AppException.AppExceptionFactory(e, i); 
 		}	
   	  	return results;
   	 }

}
