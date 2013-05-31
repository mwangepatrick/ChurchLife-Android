package com.acstech.churchlife.webservice;

import org.json.JSONException;
import org.json.JSONObject;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;

public class EmailBase extends CoreObject {


	public int EmailId;
	public String EmailType;
	public boolean Preferred;
	public String Email;
	public boolean Listed;
	
    // Factory Method - parse json
    protected void fetchFromJson(String json) throws AppException
    {   
    	_sourceJson = json;
	  	  
  	  	try
	  	{
	  		JSONObject jo = new JSONObject(json);
	  		
	  		EmailId = jo.getInt("EmailId");
	  		EmailType = jo.getString("EmailType");
	  		Preferred = jo.getBoolean("Preferred");
	  		Email = jo.getString("Email");
	  		Listed = jo.getBoolean("Listed");	  	 	   
	  	}
	  	catch (JSONException e) {
				
				ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
									ExceptionInfo.TYPE.UNEXPECTED,
								  	ExceptionInfo.SEVERITY.MODERATE, 
								  	"100", 
								  	"EmailBase.fetchFromJson", 
								  	"Error creating object.");
				
			    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed
	
			    throw AppException.AppExceptionFactory(e, i); 
	  	}	
    }		

}
