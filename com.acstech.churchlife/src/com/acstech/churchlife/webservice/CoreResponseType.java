package com.acstech.churchlife.webservice;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;

public class CoreResponseType extends CoreObject{
	
	  public int RespID;
      public String Resp_Desc;
      
      @Override
      public String toString()
  	  {
    	  return Resp_Desc;		
  	  }
      
      // Factory Method - parse json
      public static CoreResponseType GetCoreResponseType(String json) throws AppException
      {    	
    	  CoreResponseType ct = new CoreResponseType();
    	  ct._sourceJson = json;
    	  
    	  try
    	  {
    		  JSONObject jo = new JSONObject(json);
    		  
    		  ct.RespID = jo.getInt("RespID");
    		  ct.Resp_Desc = jo.getString("Resp_Desc");  	    
    	  }
    	  catch (JSONException e) {
  			
  			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
  								ExceptionInfo.TYPE.UNEXPECTED,
  							  	ExceptionInfo.SEVERITY.MODERATE, 
  							  	"100", 
  							  	"CoreResponseType.factory", 
  							  	"Error creating object.");
  			
  		    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed

  		    throw AppException.AppExceptionFactory(e, i); 
    	  }	
    	  return ct;		
      }
      
      
      // Factory Method for list of CoreResponseType objects
      public static List<CoreResponseType> GetCoreResponseTypeList(String json) throws AppException
      { 
    	  List<CoreResponseType> types = new ArrayList<CoreResponseType>();
    	  
    	  try {
    		  JSONArray ja = new JSONArray(json);    		    		 
    		  for (int i = 0; i < ja.length(); i++) {
    			  types.add(CoreResponseType.GetCoreResponseType(ja.getJSONObject(i).toString()));
    		  }    		      		 
	      }
		  catch (JSONException e) {
			
			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
								ExceptionInfo.TYPE.UNEXPECTED,
							  	ExceptionInfo.SEVERITY.MODERATE, 
							  	"100", 
							  	"CoreResponseType.factory", 
							  	"Error creating object.");
			
		    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed
	
		    throw AppException.AppExceptionFactory(e, i); 
		  }	
    	  return types;
      }
      
}
