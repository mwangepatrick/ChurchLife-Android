package com.acstech.churchlife.webservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;


public class CoreAcsUser extends CoreObject {
	 
	  public static String PERMISSION_GRANTED = "A";
	  public static String PERMISSION_VIEWADDCOMMENTS = "ViewAddComments";
	  public static String PERMISSION_REASSIGNCONNECTION = "ReassignConnection";
	  public static String PERMISSION_ASSIGNEDCONTACTSONLY = "AssignedContactsOnly";
	  		
	  public int SiteNumber;
      public String Email;
      public String UserName;
      public String FullName;
      public String SiteName;
      public String SecurityRole;
      public String UnifiedLoginID;    
      public Map<String,String> Rights = new HashMap<String,String>();

      // Helper method for evaluating user rights
      public boolean HasPermission(String key)
      {    	      	  
    	  boolean result = false;
    	  
    	  if (Rights != null && Rights.size() > 0){
    		  if (Rights.get(key) != null && Rights.get(key).equals(PERMISSION_GRANTED)) {
    			  result = true;
    		  }   		  
    	  }    	      	  
    	  return result;    	      	 
      }
     
      
      // Factory Method - parse json
      //  NOTE:  This object is used as the return value for different calls.  Some of those
      //          calls may not have all of the values (and they aren't required to be there)
      //
      public static CoreAcsUser GetCoreAcsUser(String json) throws AppException
      {    	 
    	  CoreAcsUser user = new CoreAcsUser();
    	  user._sourceJson = json;
    	  
    	  try
    	  {
    		  JSONObject jo = new JSONObject(json);
	
    		  user.SiteNumber = jo.getInt("SiteNumber");
    		  user.SiteName = jo.getString("SiteName");
    		  
    		  user.Email = jo.optString("Email");
	    	  user.UserName = jo.optString("UserName");
	    	  user.FullName = jo.optString("FullName");	 	    	  
	    	  user.SecurityRole = jo.optString("SecurityRole");
	    	  user.UnifiedLoginID = jo.optString("UnifiedLoginID");
	    			
	    	  // Rights
	    	  JSONObject rights = jo.optJSONObject("Rights");
	    	  
	    	  if (rights != null) {		    	  
	      	    @SuppressWarnings("rawtypes")
				Iterator iter = rights.keys();
		    	  while(iter.hasNext()){
		    	        String key = (String)iter.next();
		    	        String value = rights.getString(key);
		    	        user.Rights.put(key,value);
		    	  }	    	  
	    	  }
    	  }
    	  catch (JSONException e) {
			
			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
								ExceptionInfo.TYPE.UNEXPECTED,
							  	ExceptionInfo.SEVERITY.MODERATE, 
							  	"100", 
							  	"CoreAcsUser.factory", 
							  	"Error creating object.");
			
		    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed

		    throw AppException.AppExceptionFactory(e, i); 
    	  }	
    	  return user;		
      }
      
      // Factory Method for list of CoreAcsUser objects
      public static List<CoreAcsUser> GetCoreAcsUserList(String json) throws AppException
      { 
    	  List<CoreAcsUser> users = new ArrayList<CoreAcsUser>();
    	  
    	  try {
    		  JSONArray ja = new JSONArray(json);    		    		 
    		  for (int i = 0; i < ja.length(); i++) {
    			  users.add(CoreAcsUser.GetCoreAcsUser(ja.getJSONObject(i).toString()));
    		  }    		      		 
	      }
		  catch (JSONException e) {
			
			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
								ExceptionInfo.TYPE.UNEXPECTED,
							  	ExceptionInfo.SEVERITY.MODERATE, 
							  	"100", 
							  	"CoreAcsUser.factory", 
							  	"Error creating object.");
			
		    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed
	
		    throw AppException.AppExceptionFactory(e, i); 
		  }	
    	  return users;
      }
}

