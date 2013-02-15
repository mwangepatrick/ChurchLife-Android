package com.acstech.churchlife.webservice;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;
import com.acstech.churchlife.webservice.RESTClient.RequestMethod;

public class ApiGiving extends ApiBase {

	/**
	 *  Currently we are passing the giving functionality to an existing mobile device web page.  
	 *  This function returns the correct path (having auto-logged in) to the giving page.
	 *  
	 * @param username
	 * @param password
	 * @param siteNumber
	 * @return
	 * @throws AppException
	 */
	public String givingUrl(String username, String password, String siteNumber) throws AppException {
		
		isOnlineCheck();
		
		String unlockCode = null;
    	RESTClient client = new RESTClient(String.format("%s/unlock", _baseUrl));
    	     	
		client.AddHeader(APPLICATION_ID_KEY, _applicationId);    	
    	       
    	try	{
    		JSONObject json = new JSONObject();
            json.put("sitenumber", siteNumber);
            json.put("username", username);
            json.put("password", password);
            
            client.AddPostEntity(json.toString());
                		
    		client.Execute(RequestMethod.POST);    	
    		
    		if (client.getResponseCode() == HttpStatus.SC_OK) {    			    			
    			//parse json and get the unock code
    			JSONObject jo = new JSONObject(client.getResponse());    			
        		unlockCode =  jo.getString("unlocktoken");
    		}    		
    		else {
    			super.handleExceptionalResponse(client);
    		}
    	}
    	catch (AppException e)	{
    		// Add some parameters to the error for logging
    		ExceptionInfo info = e.addInfo();
    		info.setContextId("ApiGiving.givingUrl");
    		info.getParameters().put("sitenumber", siteNumber);
    		throw e;
    	}	
    	catch (JSONException e) {    		
    		ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
					ExceptionInfo.TYPE.UNEXPECTED,
				  	ExceptionInfo.SEVERITY.MODERATE, 
				  	"100", 
				  	"ApiGiving.givingUrl", 
				  	"Error creating the JSON object.");

    		i.getParameters().put("sitenumber", siteNumber);	//for logging
    		throw AppException.AppExceptionFactory(e, i); 
		}
    	
    	return String.format("%s/unlock/gotogiving/%s?theme=android", _baseUrl, unlockCode);
	}
	
	
	
	// CTOR
	public ApiGiving(String webServiceUrl, String applicationId) {
		super(webServiceUrl, applicationId);
	}

}
