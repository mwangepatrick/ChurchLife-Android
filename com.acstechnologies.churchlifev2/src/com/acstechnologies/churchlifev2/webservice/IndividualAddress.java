package com.acstechnologies.churchlifev2.webservice;

import org.json.JSONException;
import org.json.JSONObject;

import com.acstechnologies.churchlifev2.BooleanHelper;
import com.acstechnologies.churchlifev2.exceptionhandling.AppException;
import com.acstechnologies.churchlifev2.exceptionhandling.ExceptionInfo;


public class IndividualAddress {

	// {"AddrId":"111","AddrTypeId":"4", "AddrType":"Home","SharedFlag":"Y","Country":"USA","Company":"",
	//  "Address":"123 E Tree Street","Address2":"","City":"MyCity","State":"AK","Zipcode":"010101-1235",
	//  "Latitude":"","Longitude":""}
	
	private JSONObject _baseObject = null;						// state variable
	
	
	public int getAddressId() throws AppException {
		return Integer.parseInt(WebServiceObject.getJsonValue("AddrId", _baseObject)); 	
	}
	
	public int getAddressTypeId() throws AppException {
		return Integer.parseInt(WebServiceObject.getJsonValue("AddrTypeId", _baseObject)); 	
	}
	
	public String getAddressType() throws AppException {
		return WebServiceObject.getJsonValue("AddrType", _baseObject); 	
	}
	
	public boolean getSharedFlag() throws AppException {
		String value = WebServiceObject.getJsonValue("SharedFlag", _baseObject);
		return BooleanHelper.ParseBoolean(value);
	}
	
	public String getCountry() throws AppException {
		return WebServiceObject.getJsonValue("Country", _baseObject); 	
	}
	
	public String getCompany() throws AppException {
		return WebServiceObject.getJsonValue("Company", _baseObject); 	
	}

	public String getAddress() throws AppException {
		return WebServiceObject.getJsonValue("Address", _baseObject); 	
	}
	
	public String getAddress2() throws AppException {
		return WebServiceObject.getJsonValue("Address2", _baseObject); 	
	}

	public String getCity() throws AppException {
		return WebServiceObject.getJsonValue("City", _baseObject); 	
	}

	public String getState() throws AppException {
		return WebServiceObject.getJsonValue("State", _baseObject); 	
	}
	
	public String getZipcode() throws AppException {
		return WebServiceObject.getJsonValue("Zipcode", _baseObject); 	
	}
	
	public String getLatitude() throws AppException {
		return WebServiceObject.getJsonValue("Latitude", _baseObject); 	
	}
	
	public String getLongitude() throws AppException {
		return WebServiceObject.getJsonValue("Longitude", _baseObject); 	
	}
	
	
	
	// Constructor
	public IndividualAddress(String jsonString) throws AppException {
		try {
			_baseObject = new JSONObject(jsonString);			
		} catch (JSONException e) {
			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
					ExceptionInfo.TYPE.UNEXPECTED,
				  	ExceptionInfo.SEVERITY.MODERATE, 
				  	"100", 
				  	"IndividualAddress.ctor", 
				  	"Error creating the JSON object.");

			i.getParameters().put("response", jsonString);	//for logging, add the json that was passed in .
			throw AppException.AppExceptionFactory(e, i); 
		}	
	}
	
}

