package com.acstech.churchlife.webservice;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;

public class CoreOrganizationType extends CoreObject{
	
    public int TypeId;
    public String LevelDescription;    
    public int LevelTypeId;

    @Override
    public String toString()
  	{
    	return LevelDescription;		
  	}
      
    // Factory Method - parse json
    public static CoreOrganizationType GetCoreOrganizationType(String json) throws AppException
    {    	
    	  CoreOrganizationType ot = new CoreOrganizationType();
    	  ot._sourceJson = json;
    	  
    	  try
    	  {
    		  JSONObject jo = new JSONObject(json);

    		  ot.TypeId = jo.getInt("TypeId");
    		  ot.LevelDescription = jo.getString("LevelDescription");    
    		  ot.LevelTypeId = jo.getInt("LevelTypeId");
    	  }
    	  catch (JSONException e) {
  			
  			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
  								ExceptionInfo.TYPE.UNEXPECTED,
  							  	ExceptionInfo.SEVERITY.MODERATE, 
  							  	"100", 
  							  	"CoreOrganizationType.factory", 
  							  	"Error creating object.");
  			
  		    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed

  		    throw AppException.AppExceptionFactory(e, i); 
    	  }	
    	  return ot;		
    }
      
      
    // Factory Method for list of CoreCommentType objects
    public static List<CoreOrganizationType> GetCoreOrganizationTypeList(String json) throws AppException
    { 
    	  List<CoreOrganizationType> types = new ArrayList<CoreOrganizationType>();
    	  
    	  try {
    		  JSONArray ja = new JSONArray(json);    		    		 
    		  for (int i = 0; i < ja.length(); i++) {
    			  types.add(CoreOrganizationType.GetCoreOrganizationType(ja.getJSONObject(i).toString()));
    		  }    		      		 
	      }
		  catch (JSONException e) {
			
			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
								ExceptionInfo.TYPE.UNEXPECTED,
							  	ExceptionInfo.SEVERITY.MODERATE, 
							  	"100", 
							  	"CoreOrganizationType.factory", 
							  	"Error creating object.");
			
		    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed
	
		    throw AppException.AppExceptionFactory(e, i); 
		  }	
    	  return types;
    }
      
}
