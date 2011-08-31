package com.acstechnologies.churchlifev2.webservice;

import org.json.JSONException;
import org.json.JSONObject;

import com.acstechnologies.churchlifev2.BooleanHelper;
import com.acstechnologies.churchlifev2.exceptionhandling.AppException;
import com.acstechnologies.churchlifev2.exceptionhandling.ExceptionInfo;


public class IndividualFamilyMember {

	// {"IndvId":"124","PrimFamily":"456","LastName":"Smith","FirstName":"Suzy","MiddleName":"","GoesbyName":"",
	//  "Suffix":"","Title":"Mrs.","PictureUrl":"","Unlisted":false}
	
	private JSONObject _baseObject = null;						// state variable
		
	public int getIndividualId() throws AppException {
		return Integer.parseInt(WebServiceObject.getJsonValue("IndvId", _baseObject)); 	
	}

	public int getPrimaryFamilylId() throws AppException {
		return Integer.parseInt(WebServiceObject.getJsonValue("PrimFamily", _baseObject)); 	
	}

	public String getLastName() throws AppException {
		return WebServiceObject.getJsonValue("LastName", _baseObject); 	
	}

	public String getFirstName() throws AppException {
		return WebServiceObject.getJsonValue("FirstName", _baseObject); 	
	}
	
	public String getMiddleName() throws AppException {
		return WebServiceObject.getJsonValue("MiddleName", _baseObject); 	
	}

	public String getGoesbyName() throws AppException {
		return WebServiceObject.getJsonValue("GoesbyName", _baseObject); 	
	}
	
	public String getSuffix() throws AppException {
		return WebServiceObject.getJsonValue("Suffix", _baseObject); 	
	}
	
	public String getTitle() throws AppException {
		return WebServiceObject.getJsonValue("Title", _baseObject); 	
	}

	public String getPictureUrl() throws AppException {
		return WebServiceObject.getJsonValue("PictureUrl", _baseObject); 	
	}
	
	public boolean Unlisted() throws AppException {
		String value = WebServiceObject.getJsonValue("Unlisted", _baseObject);
		return BooleanHelper.ParseBoolean(value);
	}
	
	
	// Constructor
	public IndividualFamilyMember(String jsonString) throws AppException {
		try {
			_baseObject = new JSONObject(jsonString);			
		} catch (JSONException e) {
			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
					ExceptionInfo.TYPE.UNEXPECTED,
				  	ExceptionInfo.SEVERITY.MODERATE, 
				  	"100", 
				  	"IndividualFamilyMember.ctor", 
				  	"Error creating the JSON object.");

			i.getParameters().put("response", jsonString);	//for logging, add the json that was passed in .
			throw AppException.AppExceptionFactory(e, i); 
		}	
	}
	
}

