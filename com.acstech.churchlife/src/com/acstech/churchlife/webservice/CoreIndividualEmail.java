package com.acstech.churchlife.webservice;

import com.acstech.churchlife.exceptionhandling.AppException;

public class CoreIndividualEmail extends EmailBase {
	
	// Factory Method - parse json
    public static CoreIndividualEmail GetCoreIndividualEmail(String json) throws AppException
    {    	
    	CoreIndividualEmail email = new CoreIndividualEmail();
    	email.fetchFromJson(json);
    	return email;
    }
}
