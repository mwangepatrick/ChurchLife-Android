package com.acstech.churchlife.webservice;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;

public class CoreConnectionType extends CoreObject{
	
	public int ConnectionTypeId;
	public String ConnectionCategory;
	public String ConnectionTypeDesc;
	public String Permission;
		
	@Override
    public String toString()
  	{
		return ConnectionTypeDesc;		
  	}
      
    // Factory Method - parse json
    public static CoreConnectionType GetCoreConnectionType(String json) throws AppException
    {    	
	  CoreConnectionType cc = new CoreConnectionType();
	  cc._sourceJson = json;
	  
	  try
	  {
		  JSONObject jo = new JSONObject(json);
		  cc.ConnectionTypeId = jo.getInt("ConnectionTypeId");
		  cc.ConnectionCategory = jo.getString("ConnectionCategory");
		  cc.ConnectionTypeDesc = jo.getString("ConnectionTypeDesc");
		  cc.Permission = jo.getString("Permission");
	  }
	  catch (JSONException e) {		
		  ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
							ExceptionInfo.TYPE.UNEXPECTED,
						  	ExceptionInfo.SEVERITY.MODERATE, 
						  	"100", 
						  	"CoreConnectionType.factory", 
						  	"Error creating object.");
		
	    	i.getParameters().put("json", json);	//for logging, add the json that was to be parsed

	    	throw AppException.AppExceptionFactory(e, i); 
	  	}	
	  	return cc;		
    }
      
      
      // Factory Method for list of CoreConnectionType objects
      public static List<CoreConnectionType> GetCoreConnectionTypeList(String json) throws AppException
      { 
    	  List<CoreConnectionType> types = new ArrayList<CoreConnectionType>();
    	  
    	  try {
    		  JSONArray ja = new JSONArray(json);    		    		 
    		  for (int i = 0; i < ja.length(); i++) {
    			  types.add(CoreConnectionType.GetCoreConnectionType(ja.getJSONObject(i).toString()));
    		  }    		      		 
	      }
		  catch (JSONException e) {
			
			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
								ExceptionInfo.TYPE.UNEXPECTED,
							  	ExceptionInfo.SEVERITY.MODERATE, 
							  	"100", 
							  	"CoreConnectionType.factory", 
							  	"Error creating object.");
			
		    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed
	
		    throw AppException.AppExceptionFactory(e, i); 
		  }	
    	  return types;
      }
}

