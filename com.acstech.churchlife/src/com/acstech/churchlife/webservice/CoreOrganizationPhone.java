package com.acstech.churchlife.webservice;

import com.acstech.churchlife.exceptionhandling.AppException;
 
public class CoreOrganizationPhone extends PhoneBase {

	// Factory Method - parse json
    public static CoreOrganizationPhone GetCoreOrganizationPhone(String json) throws AppException
    {    	
    	CoreOrganizationPhone phone = new CoreOrganizationPhone();
    	phone.fetchFromJson(json);
    	return phone;
    }
    
}
