package com.acstech.churchlife.webservice;

import com.acstech.churchlife.exceptionhandling.AppException;


/**
 * 
 * @author softwarearchitect
 *
 */
public class LoginResponse extends WebServiceObject {
	
	// Example of JSON response for a login:
	// {"Email":"my.user@acstechnologies.com",
	// "SiteName":"Support Administration Group",
	// "SiteNumber":123456,
	// "UserName":"myusername"}
			
	// Keep in mind that the base object may be an array.  If that is the case, 
	//  the individual properties will return values for the first login item
	//
	// FUTURE:  Perhaps separate logic out into a collection class or provide iterator	
	
	// Email
	public String getEmail() 				throws AppException { return getStringValue("Email", 0); }	
	public String getEmail(int indexer) 	throws AppException { return getStringValue("Email", indexer); }
	
	// SiteName
	public String getSiteName() 			throws AppException { return getStringValue("SiteName", 0); }	
	public String getSiteName(int indexer) 	throws AppException { return getStringValue("SiteName", indexer); }
	
	// SiteNumber
	public String getSiteNumber() 			throws AppException { return getStringValue("SiteNumber", 0); }	
	public String getSiteNumber(int indexer)throws AppException { return getStringValue("SiteNumber", indexer); }
	
	// UserName
	public String getUserName() 			throws AppException { return getStringValue("UserName", 0); }	
	public String getUserName(int indexer) 	throws AppException { return getStringValue("UserName", indexer); }
	
		
	// Constructor
	public LoginResponse(String responseString) throws AppException {
		super(responseString);	
	}

}
