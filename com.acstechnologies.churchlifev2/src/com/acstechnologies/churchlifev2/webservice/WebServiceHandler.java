package com.acstechnologies.churchlifev2.webservice;

import com.acstechnologies.churchlifev2.exceptionhandling.AppException;
import com.acstechnologies.churchlifev2.exceptionhandling.ExceptionInfo;
import com.acstechnologies.churchlifev2.webservice.RESTClient.RequestMethod;

/**
 * This class is a wrapper to the acstechnologies RESTful webservices.  
 * 
 * @author softwarearchitect
 * 
 */
public class WebServiceHandler {
		
	String _baseUrl = null;
	
	/** 
	 *  Used to validate/authenticate with the acstechnologies web service layer.
	 *  
	 * 	Example: 
	 *  {"Email":"joe.user@mysite.com","SiteName":"Community Church","SiteNumber":000000,"UserName":"joe"}
	 *  
	 *  @param username 		username for login. 
	 *  @param password 		password for login. 
	 *  @param siteNumber 		site number for login. 
	 *  @return 				WebServiceObject   
	 *  @throws ? 
	 *  */ 
	public LoginResponse login(String username, String password, String siteNumber) throws AppException {

		LoginResponse wsObject = null;
    	RESTClient client = new RESTClient(_baseUrl + "/account/validate");
    	
    	client.AddParam("sitenumber", siteNumber);
    	client.AddParam("username", username);
    	client.AddParam("password", password);    	
    	     	
    	try	{
    		client.Execute(RequestMethod.POST);
			wsObject= new LoginResponse(client.getResponse());								
    	}
    	catch (AppException e)	{
    		// Add some parameters to the error for logging
    		ExceptionInfo info = e.addInfo();
    		info.getParameters().put("sitenumber", siteNumber);
    		info.getParameters().put("username", username);
    		throw e;
    	}
		return wsObject;
	}
	

	/** 
	 *  Used to validate/authenticate with the acstechnologies web service layer
	 *   for a given email address.  The response data may contain a single object or
	 *   an array of objects (an email/password can be associated with multiple sites).
	 *  
	 *  Example: 
	 *  [{"Email":"joe.user@mysite.com","SiteName":"Community Church","SiteNumber":000000,"UserName":"joe"},
	 *   {"Email":"joe.user@mysite.com","SiteName":"Community Church","SiteNumber":000001,"UserName":"joeuser"}]
	 *  
	 *  @param username 		emailAddress for login. 
	 *  @param password 		password for login. 	 
	 *  @return 				WebServiceObject   
	 *  @throws ? 
	 *  */ 	
	public LoginResponse login(String emailAddress, String password) throws AppException {

		LoginResponse wsObject = null;		    
    	RESTClient client = new RESTClient(_baseUrl + "/account/findbyemail");
    	
    	client.AddParam("email", emailAddress);
    	client.AddParam("password", password);    	
    	 
    	try {
    	    client.Execute(RequestMethod.POST);
			wsObject= new LoginResponse(client.getResponse());								
    	}
    	catch (AppException e)	{
    		// Add some parameters to the error for logging
    		ExceptionInfo info = e.addInfo();
    		info.getParameters().put("emailAddress", emailAddress);
    		info.getParameters().put("password", password);
    		throw e;
    	}    	
		return wsObject;
	}

	
	// Constructor
	public WebServiceHandler(String webServiceUrl)	{
		_baseUrl = webServiceUrl;
	}
	
}
