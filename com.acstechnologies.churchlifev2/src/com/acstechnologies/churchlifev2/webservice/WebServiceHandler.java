package com.acstechnologies.churchlifev2.webservice;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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
	
	static final String APPLICATION_ID_KEY = "ApplicationId";
		
	String _baseUrl = null;
	String _applicationId = "";			// app-specific key that gets sent with every request
	Activity _currentActivity;			// not required (can be null)
	
	/** 
	 *  Used to validate/authenticate with the acstechnologies web service layer.
	 *  
	 * 	Example: 
	 *  {"Email":"joe.user@mysite.com","SiteName":"Community Church","SiteNumber":000000,"UserName":"joe"}
	 *  
	 *  @param username 		username for login. 
	 *  @param password 		password for login. 
	 *  @param siteNumber 		site number for login. 
	 *  @return LoginResponse (WebServiceObject)   
	 *  @throws AppException 
	 *  */ 
	public LoginResponse login(String username, String password, String siteNumber) throws AppException {

		LoginResponse wsObject = null;
    	RESTClient client = new RESTClient(_baseUrl + "/accounts/validate");
    	
    	client.AddHeader(APPLICATION_ID_KEY, _applicationId);
    	
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
    		info.setContextId("WebserviceHandler.login");
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
	 *  @return LoginResponse (WebServiceObject)   
	 *  @throws AppException 
	 *  */ 	
	public LoginResponse login(String emailAddress, String password) throws AppException {

		LoginResponse wsObject = null;		    
    	RESTClient client = new RESTClient(_baseUrl + "/accounts/findbyemail");
    	
    	client.AddHeader(APPLICATION_ID_KEY, _applicationId);
    	
    	client.AddParam("email", emailAddress);
    	client.AddParam("password", password);    	    	 
    	
    	try {
    	    client.Execute(RequestMethod.POST);
			wsObject= new LoginResponse(client.getResponse());								
    	}
    	catch (AppException e)	{
    		// Add some parameters to the error for logging
    		ExceptionInfo info = e.addInfo();
    		info.setContextId("WebserviceHandler.login");
    		info.getParameters().put("emailAddress", emailAddress);    	
    		throw e;
    	}    	
		return wsObject;
	}


	/** 
	 *  Used to retrieve a list of individuals from the acstechnologies web service layer
	 *   for the given search text.  The response data may contain a single object or
	 *   an array of objects (one or more individuals).  TODO: confirm this...or is it always array
	 *  
	 *  Example: 
	 *  [{"IndvId":"001","PrimFamily":"123","LastName":"Smith","FirstName":"Joe","MiddleName":"","GoesbyName":"","Suffix":"","Title":"Mrs.","PictureUrl":"","Unlisted":false},
	 *   {"IndvId":"002","PrimFamily":"456","LastName":"Smith","FirstName":"Sarah","MiddleName":"D","GoesbyName":"","Suffix":"","Title":"Mr.","PictureUrl":"","Unlisted":false}]
	 *  
	 *  @param username 		authentication credential
	 *  @param password 		authentication credential
	 *  @param siteNumber 		siteNumber database in which to perform search 
	 *  @param searchText 		text to search for (partial name, etc.) 	
	 * 	
	 *  @return 				IndividualResponse (WebServiceObject)   
	 *  @throws AppException 
	 *  */ 	
	public IndividualsResponse getIndividuals(String username, String password, String siteNumber, 
											  String searchText, int startingRecordId, int maxRecordId) throws AppException {

		IndividualsResponse wsObject = null;		    
    	RESTClient client = new RESTClient(_baseUrl + "/" + siteNumber + "/individuals");
    	  	    	
    	client.AddParam("searchText", searchText);
    	client.AddParam("firstResult", Integer.toString(startingRecordId));
    	client.AddParam("maxResult", Integer.toString(maxRecordId));    	
    	
    	String auth = client.getB64Auth(username,password);     	
    	client.AddHeader("Authorization", auth);
    	client.AddHeader(APPLICATION_ID_KEY, _applicationId);
    	
    	try {
    	    client.Execute(RequestMethod.GET);
			wsObject= new IndividualsResponse(client.getResponse());								
    	}
    	catch (AppException e)	{
    		// Add some parameters to the error for logging
    		ExceptionInfo info = e.addInfo();
    		info.setContextId("WebserviceHandler.getIndividuals");
    		info.getParameters().put("siteNumber", siteNumber);
    		info.getParameters().put("searchText", searchText);    		
    		throw e;
    	}    	
		return wsObject;
	}
	
	public IndividualResponse getIndividual(String username, String password, String siteNumber, int individualId) throws AppException {
		
		IndividualResponse wsObject = null;		    		
		RESTClient client = new RESTClient(_baseUrl + "/" + siteNumber + "/individuals/" + Integer.toString(individualId));   	
		
		String auth = client.getB64Auth(username,password);     	
		client.AddHeader("Authorization", auth);
		client.AddHeader(APPLICATION_ID_KEY, _applicationId);
		
		try {
			client.Execute(RequestMethod.GET);
			wsObject = new IndividualResponse(client.getResponse());								
		}
		catch (AppException e)	{
			// Add some parameters to the error for logging
			ExceptionInfo info = e.addInfo();
			info.setContextId("WebserviceHandler.getIndividual");
			info.getParameters().put("siteNumber", siteNumber);
			info.getParameters().put("individualId", individualId);    		
			throw e;
		}    	
		return wsObject;
	}

	
	/** 
	 *  Used to retrieve a list of events from the acstechnologies web service layer
	 *   for the given search text.  The response data may contain a single object or
	 *   an array of objects (one or more events).  TODO: confirm this...or is it always array
	 *  
	 *  Example: 
	 *  
	 *  
	 *  @param username 		authentication credential
	 *  @param password 		authentication credential
	 *  @param siteNumber 		siteNumber database in which to perform search 
	 *  @param startDate
	 *  @param stopDate	
	 *  	
	 *  @return 				IndividualResponse (WebServiceObject)   
	 *  @throws AppException 
	 *  */ 	
	public EventsResponse getEvents(String username, String password, String siteNumber, 
								   Date startDate, Date stopDate, int startingRecordId, int maxRecordId) throws AppException {

		isOnlineCheck();
		
		EventsResponse wsObject = null;		    
    	RESTClient client = new RESTClient(_baseUrl + "/" + siteNumber + "/events");
    	  	    	
    	SimpleDateFormat dateformater = new SimpleDateFormat("yyyy-MM-dd");

    	client.AddParam("startDate", dateformater.format(startDate));
    	client.AddParam("stopDate", dateformater.format(stopDate));
    	client.AddParam("firstResult", Integer.toString(startingRecordId));
    	client.AddParam("maxResult", Integer.toString(maxRecordId));    	
    	
    	String auth = client.getB64Auth(username,password);     	
    	client.AddHeader("Authorization", auth);
    	client.AddHeader(APPLICATION_ID_KEY, _applicationId);
    	
    	try {
    	    client.Execute(RequestMethod.GET);
			wsObject= new EventsResponse(client.getResponse());								
    	}
    	catch (AppException e)	{
    		// Add some parameters to the error for logging
    		ExceptionInfo info = e.addInfo();
    		info.setContextId("WebserviceHandler.getEvents");
    		info.getParameters().put("siteNumber", siteNumber);   		
    		throw e;
    	}    	
		return wsObject;
	}
	
	public EventResponse getEvent(String username, String password, String siteNumber, String eventId) throws AppException {
		
		EventResponse wsObject = null;		    		
		RESTClient client = new RESTClient(_baseUrl + "/" + siteNumber + "/events/" + eventId);   	
		
		String auth = client.getB64Auth(username,password);     	
		client.AddHeader("Authorization", auth);
		client.AddHeader(APPLICATION_ID_KEY, _applicationId);
		
		try {
			client.Execute(RequestMethod.GET);
			wsObject = new EventResponse(client.getResponse());								
		}
		catch (AppException e)	{
			// Add some parameters to the error for logging
			ExceptionInfo info = e.addInfo();
			info.setContextId("WebserviceHandler.getEvent");
			info.getParameters().put("siteNumber", siteNumber);
			info.getParameters().put("eventId", eventId);    		
			throw e;
		}    	
		return wsObject;
	}
	
//	
//	// Do the execution of the web request wrapped in a network connection 'retry/exit' dialog
//	//  only do if context of the activity was passed to this class
//	public void ExecuteRequest(final RESTClient client, final RequestMethod method) throws AppException
//	{
//		if (_currentActivity != null)
//		{			
//	        WaitForInternetCallback callback = new WaitForInternetCallback(_currentActivity) {
//	        	public void onConnectionSuccess() { 
//	        		client.Execute(method);				// perform action (connection available)
//	        	}        		 
//	        	public void onConnectionFailure() {        		
//	        		_currentActivity.finish();			// exit this task (user selected 'exit' - connection unavailable)
//	        	}
//	        };          
//	        WaitForInternet.setCallback(callback);  			
//		}
//		else
//		{
//			client.Execute(method);
//		}
//	}
	
	public void isOnlineCheck() throws AppException {
		//if (isOnline() == false){
		if (1 == 1){
			throw AppException.AppExceptionFactory(
					   ExceptionInfo.TYPE.NOCONNECTION,
					   ExceptionInfo.SEVERITY.CRITICAL, 
					   "100",           												    
					   "WebServiceHandler.isOnlineCheck",
					   "");
		}
	}
	
    public boolean isOnline() {    	
   	 ConnectivityManager cm = (ConnectivityManager) _currentActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
   	 NetworkInfo ni = cm.getActiveNetworkInfo();
   	 
   	 if (ni != null) {
   		return ni.isConnectedOrConnecting();  
   	 }
   	 else {
   		 return false;
   	 }    	    	     	 
   }
    
	
	// Constructor
	public WebServiceHandler(String webServiceUrl, String applicationId)	{
		_baseUrl = webServiceUrl;
		_applicationId = applicationId;
	}

	public WebServiceHandler(String webServiceUrl, String applicationId, Activity currentActivity)	{
		_baseUrl = webServiceUrl;
		_applicationId = applicationId;
		_currentActivity = currentActivity;
	}
	
}
