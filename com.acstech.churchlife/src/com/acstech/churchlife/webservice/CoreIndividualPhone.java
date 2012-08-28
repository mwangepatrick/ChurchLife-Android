package com.acstech.churchlife.webservice;

import org.json.JSONException;
import org.json.JSONObject;

import com.acstech.churchlife.StringHelper;
import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;
 
public class CoreIndividualPhone extends CoreObject {
	
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

    
    // Factory Method - parse json
    public static CoreIndividualPhone GetCoreIndividualPhone(String json) throws AppException
    {    	
    	CoreIndividualPhone phone = new CoreIndividualPhone();
  	  	phone._sourceJson = json;
	  	  
  	  	try
	  	{
	  		JSONObject jo = new JSONObject(json);
	  		
	  		phone.PhoneId = jo.getInt("PhoneId");
	  		phone.Preferred = jo.getBoolean("Preferred");
	  		phone.PhoneTypeId = jo.getInt("PhoneTypeId");
	  		phone.PhoneType = jo.getString("PhoneType");
	  		phone.FamilyPhone = jo.getBoolean("FamilyPhone");
	  		phone.PhoneNumber = jo.getString("PhoneNumber");
	  		phone.Extension = StringHelper.NullOrEmpty(jo.getString("Extension"));
	  		phone.Listed = jo.getBoolean("Listed");
	  		phone.AddrPhone = jo.getBoolean("AddrPhone");
	  		phone.PhoneRef = jo.getInt("PhoneRef");	  			 	  
	  	}
	  	catch (JSONException e) {
				
				ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
									ExceptionInfo.TYPE.UNEXPECTED,
								  	ExceptionInfo.SEVERITY.MODERATE, 
								  	"100", 
								  	"CoreIndividualPhone.factory", 
								  	"Error creating object.");
				
			    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed
	
			    throw AppException.AppExceptionFactory(e, i); 
	  	}	
	  	return phone;		
    }		
}
