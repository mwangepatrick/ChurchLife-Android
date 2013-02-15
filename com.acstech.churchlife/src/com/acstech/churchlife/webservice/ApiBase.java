package com.acstech.churchlife.webservice;

import org.apache.http.HttpStatus;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ApiBase {

	static final String APPLICATION_ID_KEY = "ApplicationId";
	
	protected String _baseUrl = null;
	protected String _applicationId = "";					// app-specific key that gets sent with every request	
	protected ConnectivityManager _connectivityManager;		// lazy loaded

	/**
	 * Used to handle responses that are not expected (exceptions mostly)
	 * 
	 * @param client
	 */
	public void handleExceptionalResponse(RESTClient client) throws AppException {
		
		String defaultMsg = String.format("%s %s", client.getResponseCode(), client.getErrorMessage());
		// String.format("An error has occurred attempting this web service request.  The error code is %s.", client.getResponseCode()));			
		
		//status specific errors
		switch (client.getResponseCode()) {
			case HttpStatus.SC_FORBIDDEN:
			case HttpStatus.SC_METHOD_NOT_ALLOWED:
			case HttpStatus.SC_UNAUTHORIZED:
				throw AppException.AppExceptionFactory(ExceptionInfo.TYPE.UNAUTHORIZED,
						 							   ExceptionInfo.SEVERITY.HIGH, 
						 							   "100",           												    
						 							   "Api.connections",
						 							   defaultMsg);		
			default:	
				throw AppException.AppExceptionFactory(ExceptionInfo.TYPE.UNEXPECTED,
						   							   ExceptionInfo.SEVERITY.HIGH, 
						   							   "100",           												    
						   							   "Api.connections",
						   							   defaultMsg);
		}		
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
	public ApiBase(String webServiceUrl, String applicationId)	{
		_baseUrl = webServiceUrl;
		_applicationId = applicationId;
	}
		
}
