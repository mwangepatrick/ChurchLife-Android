package com.acstech.churchlife.webservice;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
public class Api extends ApiBase {

	/************************************************************/
	/*				 	Assignments (connections)				*/
	/************************************************************/
	
	
	/**
	 * 
	 * 
	 * @param username
	 * @param password
	 * @param siteNumber
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public CorePagedResult<List<CoreAssignmentSummary>> assignmentsummary(String username, String password, String siteNumber, int pageIndex) throws AppException {

		isOnlineCheck();
		
		CorePagedResult<List<CoreAssignmentSummary>> assignments = null;
    	RESTClient client = new RESTClient(String.format("%s/%s/connections/assignments", _baseUrl, siteNumber));
    			
    	String auth = client.getB64Auth(username,password);     	
		client.AddHeader("Authorization", auth);
    	client.AddHeader(APPLICATION_ID_KEY, _applicationId);    	
    	
    	client.AddParam("pageIndex", Integer.toString(pageIndex));
         
    	try	{
    		client.Execute(RequestMethod.GET);    	
    		
    		if (client.getResponseCode() == HttpStatus.SC_OK) {    			
    			assignments = CoreAssignmentSummary.GetCoreAssignmentSummaryPagedResult(client.getResponse());
    		}
    	}
    	catch (AppException e)	{
    		// Add some parameters to the error for logging
    		ExceptionInfo info = e.addInfo();
    		info.setContextId("Api.assignments");
    		info.getParameters().put("sitenumber", siteNumber);
    		info.getParameters().put("username", username);
    		throw e;
    	}
		return assignments;
	}

	public CorePagedResult<List<CoreAssignment>> assignments(String username, String password, String siteNumber, int assignmentTypeId, int pageIndex) throws AppException {

		isOnlineCheck();
		
		CorePagedResult<List<CoreAssignment>> assignments = null;
    	RESTClient client = new RESTClient(String.format("%s/%s/connections/assignments/%s", _baseUrl, siteNumber, assignmentTypeId));
    	
    	String auth = client.getB64Auth(username,password);     	
		client.AddHeader("Authorization", auth);
    	client.AddHeader(APPLICATION_ID_KEY, _applicationId);
    	
    	client.AddParam("pageIndex", Integer.toString(pageIndex));
        
    	try	{
    		client.Execute(RequestMethod.GET);    	
    		
    		if (client.getResponseCode() == HttpStatus.SC_OK) {    			
    			assignments = CoreAssignment.GetCoreAssignmentPagedResult(client.getResponse());
    		}
    	}
    	catch (AppException e)	{
    		// Add some parameters to the error for logging
    		ExceptionInfo info = e.addInfo();
    		info.setContextId("Api.comments");
    		info.getParameters().put("sitenumber", siteNumber);
    		info.getParameters().put("username", username);
    		info.getParameters().put("assignmenttype", assignmentTypeId);
    		throw e;
    	}
		return assignments;
	}

	// Connections
	public CoreConnection connection(String username, String password, String siteNumber, int connectionId) throws AppException {

		isOnlineCheck();
		
		CoreConnection connection = null;
    	RESTClient client = new RESTClient(String.format("%s/%s/connections/%s?includeSelf=true", _baseUrl, siteNumber, connectionId));
    	
    	String auth = client.getB64Auth(username,password);     	
		client.AddHeader("Authorization", auth);
    	client.AddHeader(APPLICATION_ID_KEY, _applicationId);    	
    	       
    	try	{
    		client.Execute(RequestMethod.GET);    	
    		
    		if (client.getResponseCode() == HttpStatus.SC_OK) {    			
    			connection = CoreConnection.GetCoreConnection(client.getResponse());
    		}
    	}
    	catch (AppException e)	{
    		// Add some parameters to the error for logging
    		ExceptionInfo info = e.addInfo();
    		info.setContextId("Api.connection");
    		info.getParameters().put("sitenumber", siteNumber);
    		info.getParameters().put("username", username);
    		info.getParameters().put("connectionId", connectionId);
    		throw e;
    	}
		return connection;	
	}

	public void connectionAdd(String username, String password, String siteNumber, CoreConnectionChangeRequest connection)  throws AppException {
		
		RESTClient client = new RESTClient(String.format("%s/%s/connections", _baseUrl, siteNumber));
    			
    	String auth = client.getB64Auth(username,password);     	
		client.AddHeader("Authorization", auth);
    	client.AddHeader(APPLICATION_ID_KEY, _applicationId);
    	
    	client.AddHeader("Content-Type", "application/x-www-form-urlencoded");    	
    	    	
    	try	{
    		
    	    client.AddPostEntity(connection.toJsonString());
            
            client.Execute(RequestMethod.POST);    	    		
    		
    		if (client.getResponseCode() != HttpStatus.SC_OK) {
    			 throw AppException.AppExceptionFactory(
            			 ExceptionInfo.TYPE.APPLICATION,
						 ExceptionInfo.SEVERITY.CRITICAL, 
						 "100",           												    
						 "Api.connectionAdd",
						 "This connection could not be saved.  Please try again.");
    		}
    	}
    	catch (AppException e)	{
    		// Add some parameters to the error for logging
    		ExceptionInfo info = e.addInfo();
    		info.setContextId("Api.connectionAdd");
    		info.getParameters().put("sitenumber", siteNumber);
    		info.getParameters().put("username", username);
    		info.getParameters().put("connection", connection.toJsonString());
    		throw e;
    	}		
	}

	public List<CoreResponseType> responsetypes(String username, String password, String siteNumber, int connectionTypeId) throws AppException {

		isOnlineCheck();
		
		List<CoreResponseType> list = null;
    	RESTClient client = new RESTClient(String.format("%s/%s/types/responses/%s", _baseUrl, siteNumber, connectionTypeId));
    	
    	String auth = client.getB64Auth(username,password);     	
		client.AddHeader("Authorization", auth);
    	client.AddHeader(APPLICATION_ID_KEY, _applicationId);
    	    	
    	try	{
    		client.Execute(RequestMethod.GET);    	
    		    	
    		if (client.getResponseCode() == HttpStatus.SC_OK) {    			
    			list = CoreResponseType.GetCoreResponseTypeList(client.getResponse());
    		}
    		else {    			
    			handleExceptionalResponse(client);
    		}
    	}
    	catch (AppException e)	{
    		// Add some parameters to the error for logging
    		ExceptionInfo info = e.addInfo();
    		info.setContextId("Api.responsetypes");
    		info.getParameters().put("sitenumber", siteNumber);
    		throw e;
    	}
		return list;
	}

	
	public List<CoreConnectionType> connectiontypes(String username, String password, String siteNumber) throws AppException {

		isOnlineCheck();
		
		List<CoreConnectionType> list = null;
    	RESTClient client = new RESTClient(String.format("%s/%s/types/connections", _baseUrl, siteNumber));
    	
    	String auth = client.getB64Auth(username,password);     	
		client.AddHeader("Authorization", auth);
    	client.AddHeader(APPLICATION_ID_KEY, _applicationId);
    	    	
    	try	{
    		client.Execute(RequestMethod.GET);    	
    		    	
    		if (client.getResponseCode() == HttpStatus.SC_OK) {    			
    			list = CoreConnectionType.GetCoreConnectionTypeList(client.getResponse());
    		}
    		else {    			
    			handleExceptionalResponse(client);
    		}
    	}
    	catch (AppException e)	{
    		// Add some parameters to the error for logging
    		ExceptionInfo info = e.addInfo();
    		info.setContextId("Api.connectiontypes");
    		info.getParameters().put("sitenumber", siteNumber);
    		throw e;
    	}
		return list;
	}

	
	// connections teams
	public List<CoreConnectionTeam> connectionteams(String username, String password, String siteNumber) throws AppException {

		isOnlineCheck();
		
		List<CoreConnectionTeam> list = null;
    	RESTClient client = new RESTClient(String.format("%s/%s/connections/teams", _baseUrl, siteNumber));
    	
    	String auth = client.getB64Auth(username,password);     	
		client.AddHeader("Authorization", auth);
    	client.AddHeader(APPLICATION_ID_KEY, _applicationId);
    	    	
    	try	{
    		client.Execute(RequestMethod.GET);    	
    		    	
    		if (client.getResponseCode() == HttpStatus.SC_OK) {    			
    			list = CoreConnectionTeam.GetCoreConnectionTeamList(client.getResponse());
    		}
    		else {    			
    			handleExceptionalResponse(client);
    		}
    	}
    	catch (AppException e)	{
    		// Add some parameters to the error for logging
    		ExceptionInfo info = e.addInfo();
    		info.setContextId("Api.connectionteams");
    		info.getParameters().put("sitenumber", siteNumber);
    		throw e;
    	}
		return list;
	}
	
	
	// connections by individual
	public CorePagedResult<List<CoreConnection>> connections(String username, String password, String siteNumber, int individualId, int pageIndex) throws AppException {

		isOnlineCheck();
		
		CorePagedResult<List<CoreConnection>> connections = null;
    	RESTClient client = new RESTClient(String.format("%s/%s/individuals/%s/connections", _baseUrl, siteNumber, individualId));
    	
    	String auth = client.getB64Auth(username,password);     	
		client.AddHeader("Authorization", auth);
    	client.AddHeader(APPLICATION_ID_KEY, _applicationId);
    	
    	client.AddParam("pageIndex", Integer.toString(pageIndex));
        
    	try	{
    		client.Execute(RequestMethod.GET);    	
    		
    		if (client.getResponseCode() == HttpStatus.SC_OK) {    			
    			connections = CoreConnection.GetCoreConnectionPagedResult(client.getResponse());
    		}    	
    		else {    			
    			handleExceptionalResponse(client);
    		}
    	}
    	catch (AppException e)	{
    		// Add some parameters to the error for logging
    		ExceptionInfo info = e.addInfo();
    		info.setContextId("Api.connections");
    		info.getParameters().put("sitenumber", siteNumber);
    		info.getParameters().put("username", username);
    		info.getParameters().put("individualdid", individualId);
    		throw e;
    	}
		return connections;
	}

	
	/************************************************************/
	/*					 	Comments							*/
	/************************************************************/
	
	
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
    	RESTClient client = new RESTClient(String.format("%s/%s/individuals/%s/comments", _baseUrl, siteNumber, individualId));
    			
    	String auth = client.getB64Auth(username,password);     	
		client.AddHeader("Authorization", auth);
    	client.AddHeader(APPLICATION_ID_KEY, _applicationId);    	
    	
    	client.AddParam("pageIndex", Integer.toString(pageIndex));
         
    	try	{
    		client.Execute(RequestMethod.GET);    	
    		
    		if (client.getResponseCode() == HttpStatus.SC_OK) {    			
    			comments = CoreCommentSummary.GetCoreCommentSummaryPagedResult(client.getResponse());
    		}
    		else {    			
    			handleExceptionalResponse(client);
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
    	RESTClient client = new RESTClient(String.format("%s/%s/individuals/%s/comments/%s", _baseUrl, siteNumber, individualId, commentTypeId));
    	
    	String auth = client.getB64Auth(username,password);     	
		client.AddHeader("Authorization", auth);
    	client.AddHeader(APPLICATION_ID_KEY, _applicationId);
    	
    	client.AddParam("pageIndex", Integer.toString(pageIndex));
        
    	try	{
    		client.Execute(RequestMethod.GET);    	
    		
    		if (client.getResponseCode() == HttpStatus.SC_OK) {    			
    			comments = CoreComment.GetCoreCommentPagedResult(client.getResponse());
    		}
    		else {    			
    			handleExceptionalResponse(client);
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
    	RESTClient client = new RESTClient(String.format("%s/%s/types/comments", _baseUrl, siteNumber));
    	
    	String auth = client.getB64Auth(username,password);     	
		client.AddHeader("Authorization", auth);
    	client.AddHeader(APPLICATION_ID_KEY, _applicationId);
    	    	
    	try	{
    		client.Execute(RequestMethod.GET);    	
    		    	
    		if (client.getResponseCode() == HttpStatus.SC_OK) {    			
    			list = CoreCommentType.GetCoreCommentTypeList(client.getResponse());
    		}
    		else {    			
    			handleExceptionalResponse(client);
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
				
		RESTClient client = new RESTClient(String.format("%s/%s/individuals/%s/comments/%s", _baseUrl, siteNumber, comment.IndvID, comment.CommentTypeId));
    			
    	String auth = client.getB64Auth(username,password);     	
		client.AddHeader("Authorization", auth);
    	client.AddHeader(APPLICATION_ID_KEY, _applicationId);
    	
    	client.AddHeader("Content-Type", "application/x-www-form-urlencoded");    	
    	    	
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
    		info.getParameters().put("commenttypeid", comment.CommentTypeId);
    		throw e;
    	}		
	}

	
	/************************************************************/
	/*					 	Events 								*/
	/************************************************************/
	public CorePagedResult<List<CoreEvent>> events(String username, String password, String siteNumber, Date startDate, Date stopDate, int pageIndex) throws AppException {

		isOnlineCheck();
		
		CorePagedResult<List<CoreEvent>> events = null;
    	RESTClient client = new RESTClient(String.format("%s/%s/events", _baseUrl, siteNumber));
    	    	
    	String auth = client.getB64Auth(username,password);     	
		client.AddHeader("Authorization", auth);
    	client.AddHeader(APPLICATION_ID_KEY, _applicationId);    	
    	
    	SimpleDateFormat dateformater = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

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

	public CoreEventDetail event(String username, String password, String siteNumber, String calendarId, String eventId) throws AppException {

		isOnlineCheck();
		
		CoreEventDetail event = null;
    	RESTClient client = new RESTClient(String.format("%s/%s/calendars/%s/events/%s", _baseUrl, siteNumber, calendarId, eventId));
    	
    	String auth = client.getB64Auth(username,password);     	
		client.AddHeader("Authorization", auth);
    	client.AddHeader(APPLICATION_ID_KEY, _applicationId);    	
    	       
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
	/*					 	Individuals							*/
	/************************************************************/
	
	public CorePagedResult<List<CoreIndividual>> individuals(String username, String password, String siteNumber, String searchText, int pageIndex) throws AppException {

		isOnlineCheck();
		
		CorePagedResult<List<CoreIndividual>> individuals = null;    	
    	RESTClient client = new RESTClient(String.format("%s/%s/individuals", _baseUrl, siteNumber));
    	    	
    	String auth = client.getB64Auth(username,password);     	
		client.AddHeader("Authorization", auth);
    	client.AddHeader(APPLICATION_ID_KEY, _applicationId);    	    
    	
    	client.AddParam("q", searchText);
        client.AddParam("pageIndex", Integer.toString(pageIndex));
                
    	try	{
    		client.Execute(RequestMethod.GET);    	
    		
    		if (client.getResponseCode() == HttpStatus.SC_OK) {    			
    			individuals = CoreIndividual.GetCoreIndividualPagedResult(client.getResponse());
    		}
    	}
    	catch (AppException e)	{
    		// Add some parameters to the error for logging
    		ExceptionInfo info = e.addInfo();
    		info.setContextId("Api.individuals");
    		info.getParameters().put("sitenumber", siteNumber);
    		info.getParameters().put("username", username);
    		info.getParameters().put("searchtext", searchText);
    		throw e;
    	}
		return individuals;
	}
	

	public CoreIndividualDetail individual(String username, String password, String siteNumber, int indvId) throws AppException {

		isOnlineCheck();
		
		CoreIndividualDetail individual = null;
    	RESTClient client = new RESTClient(String.format("%s/%s/individuals/%s", _baseUrl, siteNumber, indvId));
    	
    	String auth = client.getB64Auth(username,password);     	
		client.AddHeader("Authorization", auth);
    	client.AddHeader(APPLICATION_ID_KEY, _applicationId);    	
    
    	try	{
    		client.Execute(RequestMethod.GET);    	
    		
    		if (client.getResponseCode() == HttpStatus.SC_OK) {    			
    			individual = CoreIndividualDetail.GetCoreIndividualDetail(client.getResponse());
    		}
    	}
    	catch (AppException e)	{
    		// Add some parameters to the error for logging
    		ExceptionInfo info = e.addInfo();
    		info.setContextId("Api.individuals");
    		info.getParameters().put("sitenumber", siteNumber);
    		info.getParameters().put("username", username);
    		info.getParameters().put("indvid", indvId);
    		throw e;
    	}
		return individual;
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
    	RESTClient client = new RESTClient(String.format("%s/%s/account", _baseUrl, siteNumber));
    	
    	String auth = client.getB64Auth(username,password);     	
		client.AddHeader("Authorization", auth);
    	client.AddHeader(APPLICATION_ID_KEY, _applicationId);
    	    	
    	try	{
    		client.Execute(RequestMethod.GET);    	

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
                		
    		client.Execute(RequestMethod.PUT);    	
    		
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
	
	// Account Merchant
	public CoreAccountMerchant accountmerchant(String username, String password, String siteNumber) throws AppException {

		isOnlineCheck();
		
		CoreAccountMerchant merchantInfo = null;
    	RESTClient client = new RESTClient(String.format("%s/%s/account/merchant", _baseUrl, siteNumber));
    	
    	String auth = client.getB64Auth(username,password);     	
		client.AddHeader("Authorization", auth);
    	client.AddHeader(APPLICATION_ID_KEY, _applicationId);    	
    	       
    	try	{
    		client.Execute(RequestMethod.GET);    	
    		
    		if (client.getResponseCode() == HttpStatus.SC_OK) {    			
    			merchantInfo = CoreAccountMerchant.GetCoreAccountMerchant(client.getResponse());
    		}
    		else if (client.getResponseCode() == HttpStatus.SC_NO_CONTENT) {
    			// do nothing - return null (not an error as some accounts do NOT have merchant info)
    		}
    		else {
    			handleExceptionalResponse(client);
    		}
    	}
    	catch (AppException e)	{
    		// Add some parameters to the error for logging
    		ExceptionInfo info = e.addInfo();
    		info.setContextId("Api.accountmerchant");
    		info.getParameters().put("sitenumber", siteNumber);
    		info.getParameters().put("username", username);
    		throw e;
    	}
		return merchantInfo;	
	}

	
	// CTOR
	public Api(String webServiceUrl, String applicationId) {
		super(webServiceUrl, applicationId);
				
		//test urls
		//_baseUrl = "https://releasing.accessacs.com/api_accessacs/v2";
		//_baseUrl = "http://labs.acstechnologies.com/coreaccessacs/api_accessacs/v2";
	}
	
}
