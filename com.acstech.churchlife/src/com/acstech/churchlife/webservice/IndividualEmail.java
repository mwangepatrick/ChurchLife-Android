package com.acstech.churchlife.webservice;

import org.json.JSONException;
import org.json.JSONObject;

import com.acstech.churchlife.BooleanHelper;
import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;


public class IndividualEmail {

	// {"EmailId":"880","EmailType":"Home","Preferred":"True","Email":"its.me@mycompany.com","Listed":"True"}
	
	private JSONObject _baseObject = null;						// state variable
	
	
	public int getEmailId() throws AppException {
		return Integer.parseInt(WebServiceObject.getJsonValue("EmailId", _baseObject)); 	
	}
	
//	public int getEmailTypeId() throws AppException {
//		return Integer.parseInt(WebServiceObject.getJsonValue("EmailTypeId", _baseObject)); 	
//	}
	
	public String getEmailType() throws AppException {
		return WebServiceObject.getJsonValue("EmailType", _baseObject); 	
	}
	
	public boolean getPreferred() throws AppException {
		String value = WebServiceObject.getJsonValue("Preferred", _baseObject);
		return BooleanHelper.ParseBoolean(value);
	}
	
	public String getEmailAddress() throws AppException {
		return WebServiceObject.getJsonValue("Email", _baseObject); 	
	}

	public boolean getListed() throws AppException {
		String value = WebServiceObject.getJsonValue("Listed", _baseObject);
		return BooleanHelper.ParseBoolean(value);
	}
	
	// Constructor
	public IndividualEmail(String jsonString) throws AppException {
		try {
			_baseObject = new JSONObject(jsonString);			
		} catch (JSONException e) {
			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
					ExceptionInfo.TYPE.UNEXPECTED,
				  	ExceptionInfo.SEVERITY.MODERATE, 
				  	"100", 
				  	"IndividualEmail.ctor", 
				  	"Error creating the JSON object.");

			i.getParameters().put("response", jsonString);	//for logging, add the json that was passed in .
			throw AppException.AppExceptionFactory(e, i); 
		}	
	}
	
}
