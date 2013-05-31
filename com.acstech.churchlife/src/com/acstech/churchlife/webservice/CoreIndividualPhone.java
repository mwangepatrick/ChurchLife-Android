package com.acstech.churchlife.webservice;

import com.acstech.churchlife.exceptionhandling.AppException;
 
public class CoreIndividualPhone extends PhoneBase {

	// Factory Method - parse json
    public static CoreIndividualPhone GetCoreIndividualPhone(String json) throws AppException
    {    	
    	CoreIndividualPhone phone = new CoreIndividualPhone();
    	phone.fetchFromJson(json);
    	return phone;
    }
    
}
