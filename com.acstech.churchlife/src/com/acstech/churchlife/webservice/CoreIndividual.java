package com.acstech.churchlife.webservice;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.acstech.churchlife.StringHelper;
import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;

public class CoreIndividual extends CoreObject {

	// these are filled/skipped depending on the api called  
	public int IndvId;
	public int FamId;
	public String FamilyPosition;	
	public String Title;
    public String FirstName;
    public String LastName;               
    public String MiddleName;
    public String GoesByName; 
    public String Suffix;    
	public String PictureUrl;
	
	public String Country;
    public String Company;
    public String Address;
    public String Address2;
    public String City;
    public String State;
    public String Zipcode;
    public String CityStateZip;
    public String Email;
    public Boolean EmailListed;
    public String PhoneNumber;
    public Boolean PhoneListed;
    	
	
    public String getDisplayNameForList()
    {
    	String name = "";
        if (StringHelper.NullOrEmpty(FirstName) != "")   { name = FirstName; }
        if (StringHelper.NullOrEmpty(LastName) != "")    { name = name + " " + LastName; }
        if (StringHelper.NullOrEmpty(Suffix) != "")      { name = name + ", " + Suffix; }
        if (StringHelper.NullOrEmpty(GoesByName) != "")  { name = name + " (" + GoesByName + ")"; }
          
        return name;        
    }
    
    public String getContactAddress() {
    	if (StringHelper.NullOrEmpty(Address) != ""  && StringHelper.NullOrEmpty(CityStateZip) != "")  
    	{ 
    		return String.format("%s \n %s", Address, CityStateZip);
    	}
    	else
    	{
    		return "";
    	}
    }
    
	 public static CoreIndividual GetCoreIndividual(String json) throws AppException
     {    	 
		CoreIndividual indv = new CoreIndividual();
   	  	indv._sourceJson = json;
   	  
   	  	try
   	  	{
   	  		JSONObject jo = new JSONObject(json);
	
   	  		indv.IndvId = jo.getInt("IndvId");
   	  		indv.FamId = jo.getInt("FamId");
   	  		indv.FamilyPosition = jo.getString("FamilyPosition");   	  		
   	  		indv.Title = jo.optString("Title");
   	  		indv.FirstName = jo.getString("FirstName");
   	  		indv.LastName = jo.getString("LastName");
   	  		indv.MiddleName = jo.optString("MiddleName");
   	  		indv.GoesByName = jo.optString("GoesByName");   	  	
   	  		indv.Suffix = jo.optString("Suffix");
   			indv.PictureUrl = jo.optString("PictureUrl");
   			
   			//  2013.04.22 - new for Connection detail
   			indv.Country = jo.optString("Country");
   			indv.Company = jo.optString("Company");
   			indv.Address = StringHelper.NullOrEmpty(jo.optString("Address"));
   			indv.Address2 = jo.optString("Address2");
   			indv.City = jo.optString("City");
   			indv.State = jo.optString("State");
   			indv.Zipcode = jo.optString("Zipcode");
   			indv.CityStateZip = StringHelper.NullOrEmpty(jo.optString("CityStateZip"));
   			indv.Email = StringHelper.NullOrEmpty(jo.optString("Email"));
   			indv.EmailListed = jo.optBoolean("EmailListed");
   		    indv.PhoneNumber = StringHelper.NullOrEmpty(jo.optString("PhoneNumber"));
   		    indv.PhoneListed = jo.optBoolean("PhoneListed");   		    
   		}
   	  	catch (JSONException e) {
			
			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
								ExceptionInfo.TYPE.UNEXPECTED,
							  	ExceptionInfo.SEVERITY.MODERATE, 
							  	"100", 
							  	"CoreIndividual.factory", 
							  	"Error creating object.");
			
		    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed

		    throw AppException.AppExceptionFactory(e, i); 
   	  	}	
   	  	return indv;		
     }
     
     // Factory Method for list of CoreAcsUser objects
     public static CorePagedResult<List<CoreIndividual>> GetCoreIndividualPagedResult(String json) throws AppException
     { 
     	CorePagedResult<List<CoreIndividual>> results = new CorePagedResult<List<CoreIndividual>>(json);
   	  	
   	  	try {  	  		
   	  		JSONObject jo = new JSONObject(json);
   	  	 
   	  		results.PageCount = jo.getInt("PageCount");
   	  		results.PageIndex = jo.getInt("PageIndex");
   	  		results.PageSize = jo.getInt("PageSize");
   	  		
   	  		//Page (list of CoreIndividual objects)  	  		
   	  		JSONArray ja = jo.getJSONArray("Page");
   	  		results.Page = new ArrayList<CoreIndividual>();  	  	
   	  		
   	  		for (int i = 0; i < ja.length(); i++) {
   	  			JSONObject cs = ja.getJSONObject(i);  	  			
   	  			results.Page.add(CoreIndividual.GetCoreIndividual(cs.toString()));  	  			
   	  		}   	  		  	  	
 	    }
 		catch (JSONException e) {
 			
 			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
 								ExceptionInfo.TYPE.UNEXPECTED,
 							  	ExceptionInfo.SEVERITY.MODERATE, 
 							  	"100", 
 							  	"CoreIndividual.factory", 
 							  	"Error creating object.");
 			
 		    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed
 	
 		    throw AppException.AppExceptionFactory(e, i); 
 		}	
   	  	return results;
   	 }

}
