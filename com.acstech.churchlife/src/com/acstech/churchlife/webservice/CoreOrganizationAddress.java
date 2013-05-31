package com.acstech.churchlife.webservice;

import com.acstech.churchlife.exceptionhandling.AppException;

public class CoreOrganizationAddress extends AddressBase {
	
    // Factory Method - parse json
    public static CoreOrganizationAddress GetCoreOrganizationAddress(String json) throws AppException
    {    	
    	CoreOrganizationAddress address = new CoreOrganizationAddress();
    	address.fetchFromJson(json);
    	return address;		
    }	
	
}
