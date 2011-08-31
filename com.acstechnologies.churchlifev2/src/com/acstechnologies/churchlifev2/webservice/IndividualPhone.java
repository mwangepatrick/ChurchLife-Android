package com.acstechnologies.churchlifev2.webservice;

import org.json.JSONException;
import org.json.JSONObject;

import com.acstechnologies.churchlifev2.BooleanHelper;
import com.acstechnologies.churchlifev2.exceptionhandling.AppException;
import com.acstechnologies.churchlifev2.exceptionhandling.ExceptionInfo;


public class IndividualPhone {

	//{"PhoneId":"452","Active":"Y","PhoneTypeId":"1","PhoneType":"Home","SharedFlag":"Y","PhoneNumber":"111-2222","AreaCode":"121","Extension":"","Listed":"Y","AddrPhone":"True"}
	
	private JSONObject _baseObject = null;						// state variable
	
	
	public int getPhoneId() throws AppException {
		return Integer.parseInt(WebServiceObject.getJsonValue("PhoneId", _baseObject)); 	
	}
	
	public boolean getActive() throws AppException {
		String value = WebServiceObject.getJsonValue("Active", _baseObject);
		return BooleanHelper.ParseBoolean(value);
	}
	
	public int getPhoneTypeId() throws AppException {
		return Integer.parseInt(WebServiceObject.getJsonValue("PhoneTypeId", _baseObject)); 	
	}
	
	public String getPhoneType() throws AppException {
		return WebServiceObject.getJsonValue("PhoneType", _baseObject); 	
	}
	
	public boolean getSharedFlag() throws AppException {
		String value = WebServiceObject.getJsonValue("SharedFlag", _baseObject);
		return BooleanHelper.ParseBoolean(value);
	}
	
	public String getPhoneNumber() throws AppException {
		return WebServiceObject.getJsonValue("PhoneNumber", _baseObject); 	
	}
	
	public String getAreaCode() throws AppException {
		return WebServiceObject.getJsonValue("AreaCode", _baseObject); 	
	}
	
	public String getExtension() throws AppException {
		return WebServiceObject.getJsonValue("Extension", _baseObject); 	
	}
	
	public boolean getListed() throws AppException {
		String value = WebServiceObject.getJsonValue("Listed", _baseObject);
		return BooleanHelper.ParseBoolean(value);
	}
	
	public boolean getAddrPhone() throws AppException {
		String value = WebServiceObject.getJsonValue("AddrPhone", _baseObject);
		return BooleanHelper.ParseBoolean(value);
	}		
	
	// Constructor
	public IndividualPhone(String jsonString) throws AppException {
		try {
			_baseObject = new JSONObject(jsonString);			
		} catch (JSONException e) {
			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
					ExceptionInfo.TYPE.UNEXPECTED,
				  	ExceptionInfo.SEVERITY.MODERATE, 
				  	"100", 
				  	"IndividualPhone.ctor", 
				  	"Error creating the JSON object.");

			i.getParameters().put("response", jsonString);	//for logging, add the json that was passed in .
			throw AppException.AppExceptionFactory(e, i); 
		}	
	}
	
}