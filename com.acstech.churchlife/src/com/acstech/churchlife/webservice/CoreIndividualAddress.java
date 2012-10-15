package com.acstech.churchlife.webservice;

import org.json.JSONException;
import org.json.JSONObject;

import com.acstech.churchlife.StringHelper;
import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;
 
public class CoreIndividualAddress extends CoreObject {

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
	
    // Factory Method - parse json
    public static CoreIndividualAddress GetCoreIndividualAddress(String json) throws AppException
    {    	
    	CoreIndividualAddress address = new CoreIndividualAddress();
  	  	address._sourceJson = json;
	  	  
  	  	try
	  	{
	  		JSONObject jo = new JSONObject(json);
	  		  
	  		address.AddrId = jo.getInt("AddrId");
	  		address.AddrTypeId = jo.getInt("AddrTypeId");
	  		address.AddrType = jo.getString("AddrType");
	  		address.FamilyAddress = jo.getBoolean("FamilyAddress");
	  		address.ActiveAddress = jo.getBoolean("ActiveAddress");
	  		address.MailAddress = jo.getBoolean("MailAddress");
	  		address.StatementAddress = jo.getBoolean("StatementAddress");
	  		address.Country = jo.getString("Country");
	  		address.Company =  StringHelper.NullOrEmpty(jo.getString("Company"));
	  		address.Address = StringHelper.NullOrEmpty(jo.getString("Address"));
	  		address.Address2 =  StringHelper.NullOrEmpty(jo.getString("Address2"));
	  		address.City = jo.getString("City");
	  		address.State = jo.getString("State");
	  		address.Zipcode = jo.getString("Zipcode");
	  		address.CityStateZip = jo.getString("CityStateZip");
	  		address.Latitude = jo.getString("Latitude");
	  		address.Longitude = jo.getString("Longitude");	  	 	   
	  	}
	  	catch (JSONException e) {
				
				ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
									ExceptionInfo.TYPE.UNEXPECTED,
								  	ExceptionInfo.SEVERITY.MODERATE, 
								  	"100", 
								  	"CoreIndividualAddress.factory", 
								  	"Error creating object.");
				
			    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed
	
			    throw AppException.AppExceptionFactory(e, i); 
	  	}	
	  	return address;		
    }	
	
}
