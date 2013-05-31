package com.acstech.churchlife.webservice;

import org.json.JSONException;
import org.json.JSONObject;

import com.acstech.churchlife.StringHelper;
import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;

public class PhoneBase extends CoreObject {
	
	public int PhoneId;
	public boolean Preferred;
	public int PhoneTypeId;
	public String PhoneType;
	public boolean FamilyPhone;
	public String PhoneNumber;
	public String Extension;
	public boolean Listed;
	public boolean AddrPhone;
	public int PhoneRef;

    // Helper(s)
	public String getPhoneNumberToDisplay() throws AppException {
		
		String unlisted = ""; 
		if (Listed == false){
			unlisted = "(Unl)";
		}
		
		 if (Extension.trim().length() > 0)
         {
             return String.format("%s x%s %s",  PhoneNumber, Extension, unlisted);
         }
         else
         {
             return String.format("%s %s", PhoneNumber, unlisted);
         }		
	}
	
	public String getPhoneNumberToDial()throws AppException {
		return PhoneNumber.replace("-", "");		
	}

    
    protected void fetchFromJson(String json) throws AppException
    {    	
    	_sourceJson = json;
	  	  
  	  	try
	  	{
	  		JSONObject jo = new JSONObject(json);
	  		
	  		PhoneId = jo.getInt("PhoneId");
	  		Preferred = jo.getBoolean("Preferred");
	  		PhoneTypeId = jo.getInt("PhoneTypeId");
	  		PhoneType = jo.getString("PhoneType");
	  		FamilyPhone = jo.getBoolean("FamilyPhone");
	  		PhoneNumber = jo.getString("PhoneNumber");
	  		Extension = StringHelper.NullOrEmpty(jo.getString("Extension"));
	  		Listed = jo.getBoolean("Listed");
	  		AddrPhone = jo.getBoolean("AddrPhone");
	  		PhoneRef = jo.getInt("PhoneRef");	  			 	  
	  	}
	  	catch (JSONException e) {
				
				ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
									ExceptionInfo.TYPE.UNEXPECTED,
								  	ExceptionInfo.SEVERITY.MODERATE, 
								  	"100", 
								  	"PhoneBase.fetchFromJson", 
								  	"Error creating object.");
				
			    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed
	
			    throw AppException.AppExceptionFactory(e, i); 
	  	}	
    }		

}
