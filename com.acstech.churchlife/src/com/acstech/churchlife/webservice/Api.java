package com.acstech.churchlife.webservice;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;
import com.acstech.churchlife.webservice.RESTClient.RequestMethod;


/**
 * This class is a wrapper to the acstechnologies RESTful webservices.  
 * 
 * NOTE:  This will (eventually) replace the WebServiceHandler class
 * 
 * @author softwarearchitect
 * 
 */
public class Api {

	static final String APPLICATION_ID_KEY = "ApplicationId";
	
	String _baseUrl = null;
	String _applicationId = "";					// app-specific key that gets sent with every request	
	ConnectivityManager _connectivityManager;	// not required (can be null)
	
	/**
	 * 
	 * 
	 * @param username
	 * @param password
	 * @param siteNumber
	 * @param individualId
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public CorePagedResult<List<CoreCommentSummary>> commentsummary(String username, String password, String siteNumber, int individualId, int pageIndex) throws AppException {

		isOnlineCheck();
		
		CorePagedResult<List<CoreCommentSummary>> comments = null;
    	RESTClient client = new RESTClient(_baseUrl + "/comments");
    	
    	String auth = client.getB64Auth(username,password);     	
		client.AddHeader("Authorization", auth);
    	client.AddHeader(APPLICATION_ID_KEY, _applicationId);    	
    	client.AddHeader("sitenumber", siteNumber);
    	
    	client.AddParam("id", Integer.toString(individualId));
        client.AddParam("pageIndex", Integer.toString(pageIndex));
         
    	try	{
    		client.Execute(RequestMethod.GET);    	
    		
    		if (client.getResponseCode() == HttpStatus.SC_OK) {    			
    			comments = CoreCommentSummary.GetCoreCommentSummaryPagedResult(client.getResponse());
    		}
    	}
    	catch (AppException e)	{
    		// Add some parameters to the error for logging
    		ExceptionInfo info = e.addInfo();
    		info.setContextId("Api.commentsummary");
    		info.getParameters().put("sitenumber", siteNumber);
    		info.getParameters().put("username", username);
    		throw e;
    	}
		return comments;
	}
	
	public CorePagedResult<List<CoreComment>> comments(String username, String password, String siteNumber, int individualId, int commentTypeId, int pageIndex) throws AppException {

		isOnlineCheck();
		
		CorePagedResult<List<CoreComment>> comments = null;
    	RESTClient client = new RESTClient(_baseUrl + "/comments/getComments");
    	
    	String auth = client.getB64Auth(username,password);     	
		client.AddHeader("Authorization", auth);
    	client.AddHeader(APPLICATION_ID_KEY, _applicationId);    	
    	client.AddHeader("sitenumber", siteNumber);
    	
    	client.AddParam("id", Integer.toString(individualId));
    	client.AddParam("commenttypeid", Integer.toString(commentTypeId));
        client.AddParam("pageIndex", Integer.toString(pageIndex));
        
    	try	{
    		client.Execute(RequestMethod.GET);    	
    		
    		if (client.getResponseCode() == HttpStatus.SC_OK) {    			
    			comments = CoreComment.GetCoreCommentPagedResult(client.getResponse());
    		}
    	}
    	catch (AppException e)	{
    		// Add some parameters to the error for logging
    		ExceptionInfo info = e.addInfo();
    		info.setContextId("Api.comments");
    		info.getParameters().put("sitenumber", siteNumber);
    		info.getParameters().put("username", username);
    		info.getParameters().put("individual", individualId);
    		info.getParameters().put("commenttype", commentTypeId);
    		throw e;
    	}
		return comments;
	}

	public List<CoreCommentType> commenttypes(String username, String password, String siteNumber) throws AppException {

		isOnlineCheck();
		
		List<CoreCommentType> list = null;
    	RESTClient client = new RESTClient(_baseUrl + "/comments/getAvailableCommentTypes");
    	
    	String auth = client.getB64Auth(username,password);     	
		client.AddHeader("Authorization", auth);
    	client.AddHeader(APPLICATION_ID_KEY, _applicationId);
    	
    	client.AddHeader("sitenumber", siteNumber);
    	    	
    	try	{
    		client.Execute(RequestMethod.GET);    	
    		    	
    		if (client.getResponseCode() == HttpStatus.SC_OK) {    			
    			list = CoreCommentType.GetCoreCommentTypeList(client.getResponse());
    		}
    	}
    	catch (AppException e)	{
    		// Add some parameters to the error for logging
    		ExceptionInfo info = e.addInfo();
    		info.setContextId("Api.commenttypes");
    		info.getParameters().put("sitenumber", siteNumber);
    		throw e;
    	}
		return list;
	}

	
	public void commentAdd(String username, String password, String siteNumber, CoreCommentChangeRequest comment)  throws AppException {
		
		RESTClient client = new RESTClient(_baseUrl + "/comments/postcommentchangerequest");
    	
    	String auth = client.getB64Auth(username,password);     	
		client.AddHeader("Authorization", auth);
    	client.AddHeader(APPLICATION_ID_KEY, _applicationId);
    	
    	client.AddHeader("sitenumber", siteNumber);
    	    	
    	try	{
    		
    	    client.AddPostEntity(comment.toJsonString());
            
            client.Execute(RequestMethod.POST);    	    		
    		
    		if (client.getResponseCode() != HttpStatus.SC_OK) {
    			 throw AppException.AppExceptionFactory(
            			 ExceptionInfo.TYPE.APPLICATION,
						 ExceptionInfo.SEVERITY.CRITICAL, 
						 "100",           												    
						 "Api.commentAdd",
						 "This comment change request could not be saved.  Please try again.");
    		}
    	}
    	catch (AppException e)	{
    		// Add some parameters to the error for logging
    		ExceptionInfo info = e.addInfo();
    		info.setContextId("Api.commentAdd");
    		info.getParameters().put("sitenumber", siteNumber);
    		info.getParameters().put("username", username);
    		info.getParameters().put("commenttype", comment.CommentType);
    		throw e;
    	}		
	}

	
	/************************************************************/
	/*					 	Events 								*/
	/************************************************************/
	public CorePagedResult<List<CoreEvent>> events(String username, String password, String siteNumber, Date startDate, Date stopDate, int pageIndex) throws AppException {

		isOnlineCheck();
		
		CorePagedResult<List<CoreEvent>> events = null;
    	RESTClient client = new RESTClient(_baseUrl + "/events");
    	
    	String auth = client.getB64Auth(username,password);     	
		client.AddHeader("Authorization", auth);
    	client.AddHeader(APPLICATION_ID_KEY, _applicationId);    	
    	client.AddHeader("sitenumber", siteNumber);
    	
    	SimpleDateFormat dateformater = new SimpleDateFormat("yyyy-MM-dd");

    	client.AddParam("startDate", dateformater.format(startDate));
    	client.AddParam("stopDate", dateformater.format(stopDate));
        client.AddParam("pageIndex", Integer.toString(pageIndex));
                
    	try	{
    		client.Execute(RequestMethod.GET);    	
    		
    		if (client.getResponseCode() == HttpStatus.SC_OK) {    			
    			events = CoreEvent.GetCoreEventListPagedResult(client.getResponse());
    		}
    	}
    	catch (AppException e)	{
    		// Add some parameters to the error for logging
    		ExceptionInfo info = e.addInfo();
    		info.setContextId("Api.events");
    		info.getParameters().put("sitenumber", siteNumber);
    		info.getParameters().put("username", username);
    		throw e;
    	}
		return events;
	}

	public CoreEventDetail event(String username, String password, String siteNumber, String eventId) throws AppException {

		isOnlineCheck();
		
		CoreEventDetail event = null;
    	RESTClient client = new RESTClient(_baseUrl + "/events/GetDetail");
    	
    	String auth = client.getB64Auth(username,password);     	
		client.AddHeader("Authorization", auth);
    	client.AddHeader(APPLICATION_ID_KEY, _applicationId);    	
    	client.AddHeader("sitenumber", siteNumber);
    	    
    	client.AddParam("id", eventId);
    	
    	try	{
    		client.Execute(RequestMethod.GET);    	
    		
    		if (client.getResponseCode() == HttpStatus.SC_OK) {    			
    			event = CoreEventDetail.GetCoreEventDetail(client.getResponse());
    		}
    	}
    	catch (AppException e)	{
    		// Add some parameters to the error for logging
    		ExceptionInfo info = e.addInfo();
    		info.setContextId("Api.event");
    		info.getParameters().put("sitenumber", siteNumber);
    		info.getParameters().put("username", username);
    		info.getParameters().put("eventId", eventId);
    		throw e;
    	}
		return event;
		
	}
	
	/************************************************************/
	/*					 	Users 								*/
	/************************************************************/
	
	/** 
	 *  Used to validate/authenticate with the acstechnologies web service layer.
	 *  
	 *  @param username 		username for login. 
	 *  @param password 		password for login. 
	 *  @param siteNumber 		site number for login. 
	 *  
	 *  @return CoreAcsUser 
	 *  @throws AppException 
	 *  */ 
	public CoreAcsUser user(String username, String password, String siteNumber) throws AppException {

		isOnlineCheck();
		
		CoreAcsUser u = null;
    	RESTClient client = new RESTClient(_baseUrl + "/accounts/getusersecurity");
    	
    	String auth = client.getB64Auth(username,password);     	
		client.AddHeader("Authorization", auth);
    	client.AddHeader(APPLICATION_ID_KEY, _applicationId);
    	
    	client.AddHeader("sitenumber", siteNumber);
    	    	
    	try	{
    		client.Execute(RequestMethod.GET);    	
    		
    		//zzz 
    		// need to test with other responses to make sure 
    		//  non OK responses get reported correctly
    		
    		if (client.getResponseCode() == HttpStatus.SC_OK) {    			
    			u = CoreAcsUser.GetCoreAcsUser(client.getResponse());
    		}
    	}
    	catch (AppException e)	{
    		// Add some parameters to the error for logging
    		ExceptionInfo info = e.addInfo();
    		info.setContextId("Api.user");
    		info.getParameters().put("sitenumber", siteNumber);
    		info.getParameters().put("username", username);
    		throw e;
    	}
		return u;
	}


	/** 
	 *  Used to validate/authenticate with the acstechnologies web service layer
	 *    when site number is not known.
	 *  
	 *  @param email 			email address for login. 
	 *  @param password 		password for login.   
	 *  @return List<CoreAcsUser>   
	 *  @throws AppException 
	 *  @throws JSONException 
	 *  */ 
	public List<CoreAcsUser> users(String email, String password) throws AppException {

		isOnlineCheck();
				
		List<CoreAcsUser> users = null;
		RESTClient client = new RESTClient(_baseUrl + "/accounts/findbyemail");
    	
    	client.AddHeader(APPLICATION_ID_KEY, _applicationId);
    	    
    	try	{
    		    		
    		JSONObject json = new JSONObject();
            json.put("email", email);
            json.put("password", password);
            
            client.AddPostEntity(json.toString());
                		
    		client.Execute(RequestMethod.POST);    	
    		
    		//zzz 
    		// need to test with other responses to make sure 
    		//  non OK responses get reported correctly
    		
    		if (client.getResponseCode() == HttpStatus.SC_OK) {    			    			
    			users = CoreAcsUser.GetCoreAcsUserList(client.getResponse());    			
    		}
    	}
    	catch (AppException e)	{
    		// Add some parameters to the error for logging
    		ExceptionInfo info = e.addInfo();
    		info.setContextId("Api.users");
    		info.getParameters().put("email", email);
    		throw e;
    		
    	} 
    	catch (JSONException e) {
    		
    		ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
					ExceptionInfo.TYPE.UNEXPECTED,
				  	ExceptionInfo.SEVERITY.MODERATE, 
				  	"100", 
				  	"Api.users", 
				  	"Error creating the JSON object.");

    		i.getParameters().put("email", email);	//for logging, add the response that was returned.
    		throw AppException.AppExceptionFactory(e, i); 
		}			
		
		return users;
	}
	
	
	
	public void isOnlineCheck() throws AppException {
		if (isOnline() == false){
			throw AppException.AppExceptionFactory(
					   ExceptionInfo.TYPE.NOCONNECTION,
					   ExceptionInfo.SEVERITY.CRITICAL, 
					   "100",           												    
					   "WebServiceHandler.isOnlineCheck",
					   "Unable to connect to the network.  Please check your connection and try again.");
		}
	}
	
    public boolean isOnline() {
    	boolean result = true;		//default to true
    	if (_connectivityManager != null) {
		   	 
		   	 NetworkInfo ni = _connectivityManager.getActiveNetworkInfo();
		   	 
		   	 if (ni != null) {
		   		result = ni.isConnectedOrConnecting();  
		   	 }
		   	 else {
		   		result = false;   		 
		   	 }    	    	  
    	}
    	return result;
   }
	
	// Constructor
	public Api(String webServiceUrl, String applicationId)	{
		_baseUrl = webServiceUrl;
		_applicationId = applicationId;
	}
	
	public Api(String webServiceUrl, String applicationId, ConnectivityManager connManager)	{
		_baseUrl = webServiceUrl;
		_applicationId = applicationId;
		_connectivityManager = connManager;
	}	
	
}
