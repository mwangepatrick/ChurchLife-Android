package com.acstech.churchlife.webservice;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;

public class CoreConnectionTeam extends CoreObject{
	
	  public int TeamId;
      public String TeamName;
      public int TeamMemberCount;
      
      @Override
      public String toString()
  	  {
    	  return TeamName;		
  	  }
      
      // Factory Method - parse json
      public static CoreConnectionTeam GetCoreConnectionTeam(String json) throws AppException
      {    	
    	  CoreConnectionTeam ct = new CoreConnectionTeam();
    	  ct._sourceJson = json;
    	  
    	  try
    	  {
    		  JSONObject jo = new JSONObject(json);
    		  
    		  ct.TeamId = jo.getInt("TeamId");
    		  ct.TeamName = jo.getString("TeamName");
    		  ct.TeamMemberCount = jo.getInt("TeamMemberCount");
    	  }
    	  catch (JSONException e) {
  			
  			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
  								ExceptionInfo.TYPE.UNEXPECTED,
  							  	ExceptionInfo.SEVERITY.MODERATE, 
  							  	"100", 
  							  	"CoreConnectionTeam.factory", 
  							  	"Error creating object.");
  			
  		    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed

  		    throw AppException.AppExceptionFactory(e, i); 
    	  }	
    	  return ct;		
      }
      
      
      // Factory Method for list of CoreConnectionTeam objects
      public static List<CoreConnectionTeam> GetCoreConnectionTeamList(String json) throws AppException
      { 
    	  List<CoreConnectionTeam> teams = new ArrayList<CoreConnectionTeam>();
    	  
    	  try {
    		  JSONArray ja = new JSONArray(json);    		    		 
    		  for (int i = 0; i < ja.length(); i++) {
    			  teams.add(CoreConnectionTeam.GetCoreConnectionTeam(ja.getJSONObject(i).toString()));
    		  }    		      		 
	      }
		  catch (JSONException e) {
			
			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
								ExceptionInfo.TYPE.UNEXPECTED,
							  	ExceptionInfo.SEVERITY.MODERATE, 
							  	"100", 
							  	"CoreConnectionTeam.factory", 
							  	"Error creating object.");
			
		    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed
	
		    throw AppException.AppExceptionFactory(e, i); 
		  }	
    	  return teams;
      }
      
}
