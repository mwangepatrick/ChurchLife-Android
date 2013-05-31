package com.acstech.churchlife.webservice;

import com.acstech.churchlife.exceptionhandling.AppException;

public class CoreIndividualAddress extends AddressBase {
	
    // Factory Method - parse json
    public static CoreIndividualAddress GetCoreIndividualAddress(String json) throws AppException
    {    	
    	CoreIndividualAddress address = new CoreIndividualAddress();
    	address.fetchFromJson(json);
    	return address;		
    }	
	
}
