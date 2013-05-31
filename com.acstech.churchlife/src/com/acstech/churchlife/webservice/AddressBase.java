package com.acstech.churchlife.webservice;

import org.json.JSONException;
import org.json.JSONObject;

import com.acstech.churchlife.StringHelper;
import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;

public class AddressBase extends CoreObject {
	
	public int AddrId;
	public int AddrTypeId;
	public String AddrType;
	public boolean FamilyAddress;
	public boolean ActiveAddress;
	public boolean MailAddress;
	public boolean StatementAddress;
	public String Country ;
	public String Company;
	public String Address;
	public String Address2;
	public String City;
	public String State;
	public String Zipcode;
	public String CityStateZip;
	public String Latitude;
	public String Longitude;


	protected void fetchFromJson(String json) throws AppException
	{
  	  	_sourceJson = json;
	  	  
  	  	try
	  	{
	  		JSONObject jo = new JSONObject(json);
	  		  
	  		AddrId = jo.getInt("AddrId");
	  		AddrTypeId = jo.getInt("AddrTypeId");
	  		AddrType = jo.getString("AddrType");
	  		FamilyAddress = jo.getBoolean("FamilyAddress");
	  		ActiveAddress = jo.getBoolean("ActiveAddress");
	  		MailAddress = jo.getBoolean("MailAddress");
	  		StatementAddress = jo.getBoolean("StatementAddress");
	  		Country = jo.getString("Country");
	  		Company =  StringHelper.NullOrEmpty(jo.getString("Company"));
	  		Address = StringHelper.NullOrEmpty(jo.getString("Address"));
	  		Address2 =  StringHelper.NullOrEmpty(jo.getString("Address2"));
	  		City = jo.getString("City");
	  		State = jo.getString("State");
	  		Zipcode = jo.getString("Zipcode");
	  		CityStateZip = jo.getString("CityStateZip");
	  		Latitude = jo.getString("Latitude");
	  		Longitude = jo.getString("Longitude");	  	 	   
	  	}
	  	catch (JSONException e) {
				
				ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
									ExceptionInfo.TYPE.UNEXPECTED,
								  	ExceptionInfo.SEVERITY.MODERATE, 
								  	"100", 
								  	"AddressBase.FetchFromJson", 
								  	"Error creating object.");
				
			    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed
	
			    throw AppException.AppExceptionFactory(e, i); 
	  	}	
	}
}
