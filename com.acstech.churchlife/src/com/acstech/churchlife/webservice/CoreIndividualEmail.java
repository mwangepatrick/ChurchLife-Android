package com.acstech.churchlife.webservice;

import org.json.JSONException;
import org.json.JSONObject;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;
 
public class CoreIndividualEmail extends CoreObject {

	public int EmailId;
	public String EmailType;
	public boolean Preferred;
	public String Email;
	public boolean Listed;
	
    // Factory Method - parse json
    public static CoreIndividualEmail GetCoreIndividualEmail(String json) throws AppException
    {    	
    	CoreIndividualEmail email = new CoreIndividualEmail();
  	  	email._sourceJson = json;
	  	  
  	  	try
	  	{
	  		JSONObject jo = new JSONObject(json);
	  		
	  		email.EmailId = jo.getInt("EmailId");
	  		email.EmailType = jo.getString("EmailType");
	  		email.Preferred = jo.getBoolean("Preferred");
	  		email.Email = jo.getString("Email");
	  		email.Listed = jo.getBoolean("Listed");	  	 	   
	  	}
	  	catch (JSONException e) {
				
				ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
									ExceptionInfo.TYPE.UNEXPECTED,
								  	ExceptionInfo.SEVERITY.MODERATE, 
								  	"100", 
								  	"CoreIndividualEmail.factory", 
								  	"Error creating object.");
				
			    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed
	
			    throw AppException.AppExceptionFactory(e, i); 
	  	}	
	  	return email;		
    }		
}
