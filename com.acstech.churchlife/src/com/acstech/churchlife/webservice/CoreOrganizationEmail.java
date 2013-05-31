package com.acstech.churchlife.webservice;

import com.acstech.churchlife.exceptionhandling.AppException;

public class CoreOrganizationEmail extends EmailBase {
	
	// Factory Method - parse json
    public static CoreOrganizationEmail GetCoreOrganizationEmail(String json) throws AppException
    {    	
    	CoreOrganizationEmail email = new CoreOrganizationEmail();
    	email.fetchFromJson(json);
    	return email;
    }
}
