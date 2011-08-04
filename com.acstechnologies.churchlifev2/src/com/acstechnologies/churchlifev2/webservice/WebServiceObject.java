package com.acstechnologies.churchlifev2.webservice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.acstechnologies.churchlifev2.exceptionhandling.AppException;
import com.acstechnologies.churchlifev2.exceptionhandling.ExceptionInfo;


/**
 * This object is returned from calls inside the WebServiceHandler.  It is also
 *   extended into specific classes (see LoginResponse for an example).
 *    
 *  It serves to wrap common JSON functionality for dealing with the responses
 *   from the web server.
 *       
 *  NOTE: The web services can return a response with a single object in the 
 *   "Data" element or an array of objects in the "Data" element.
 *  
 * @author softwarearchitect
 *
 */
public class WebServiceObject {

	// state variable
	private JSONObject _mainObject = null;
	
	public int getStatusCode() throws AppException {
		try {
			String value = WebServiceObject.getJsonValue("StatusCode", _mainObject);
			return Integer.parseInt(value);				
		} catch (AppException e) {			
			// Add specific exception information
			ExceptionInfo ei = e.addInfo();
			ei.setContextId("WebServiceObject.getStatusCode");
			ei.setSeverity(ExceptionInfo.SEVERITY.CRITICAL);
			ei.setErrorDescription("Error retrieving the status code.");					
			throw e;
		}
	}
	
	public String getMessage() throws AppException{
		try {
			return WebServiceObject.getJsonValue("Message", _mainObject);
		} catch (AppException e) {
			ExceptionInfo ei = e.addInfo();
			ei.setContextId("WebServiceObject.getMessage");
			ei.setSeverity(ExceptionInfo.SEVERITY.CRITICAL);
			ei.setErrorDescription("Error retrieving the message.");					
			throw e; 
		}
	}
	
	// Do NOT use the public getStringValue here - infinite loop
	public Boolean dataIsArray() throws AppException{
		try {
			return (isJSONArray(WebServiceObject.getJsonValue("Data", _mainObject)));		
		} catch (AppException e) {
			ExceptionInfo ei = e.addInfo();
			ei.setContextId("WebServiceObject.dataIsArray");
			ei.setSeverity(ExceptionInfo.SEVERITY.CRITICAL);
			ei.setErrorDescription("Error determining if the data was a JSON array.");					
			throw e; 
		}	
	}
	
	public int getLength() throws AppException{ 
		if (dataIsArray()== false){
			return 1;
		}	
		else {
			return (getDataArray().length());
		}		
	}
	
	// can return null if the data is an array
	public JSONObject getData() throws AppException{
		
		try {
			if (dataIsArray()) {
				return null;	
			}
			else{
				return _mainObject.getJSONObject("Data");	
			}
		} catch (JSONException e) {
			throw AppException.AppExceptionFactory(e,
					   ExceptionInfo.TYPE.UNEXPECTED,
					   ExceptionInfo.SEVERITY.CRITICAL, 
					   "100",           												    
					   "WebServiceObject.getData",
					   "Error attempting to retrieve the JSON object from the web service response."); 
		}
		
	}
	
	// can return null if the data is an object
	public JSONArray getDataArray() throws AppException {
		try {
			if (dataIsArray()) {
				return _mainObject.getJSONArray("Data");
			}
			else{
				return null;		
			}
		} catch (JSONException e) {
			throw AppException.AppExceptionFactory(e,
					   ExceptionInfo.TYPE.UNEXPECTED,
					   ExceptionInfo.SEVERITY.CRITICAL, 
					   "100",           												    
					   "WebServiceObject.getDataArray",
					   "Error attempting to retrieve the JSON array from the web service response."); 
		}
	}	
	
	public String toString()
	{
		return _mainObject.toString();		
	}
	
	// Common Method - results must be cast appropriately
	protected String getStringValue(String keyname, int indexer) throws AppException{
		try {			
			if (dataIsArray()== true && indexer >=0){
				JSONObject o = getDataArray().getJSONObject(indexer);
				return WebServiceObject.getJsonValue(keyname, o);									
			}else {
				// if single object and indexer>0, error!
				if (indexer == 0){
					return WebServiceObject.getJsonValue(keyname, this.getData());					
				}										
				else
				{
					throw AppException.AppExceptionFactory(
							ExceptionInfo.TYPE.APPLICATION, ExceptionInfo.SEVERITY.CRITICAL, 
							"100", "WebServiceObject.getStringValue", 
							"Invalid read attempt.  An index > 0 was specified but a JSON array does not exist.");	
				}
			}
			
		} catch (JSONException e) {
			throw AppException.AppExceptionFactory(e,
					   ExceptionInfo.TYPE.UNEXPECTED,
					   ExceptionInfo.SEVERITY.MODERATE,  //depends on the string and how important it is 
					   "100",           												    
					   "WebServiceObject.getStringValue",
					   String.format("Error retrieving the value for %s.", keyname)); 
		}
	}
		
	
	// Constructor
	protected WebServiceObject(String responseString) throws AppException
	{
		// Parse the request string into the standard parts.		
		try {										
			//TODO what if error? webservice returns 404 or something.... need to catch and indicate!	
			//test with broken connection,etc.
			_mainObject = new JSONObject(responseString);			
			
		} catch (JSONException e) {
			
			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
								ExceptionInfo.TYPE.UNEXPECTED,
							  	ExceptionInfo.SEVERITY.MODERATE, 
							  	"100", 
							  	"WebServiceObject.ctor", 
							  	"Error creating the JSON object.");
			
		    i.getParameters().put("response", responseString);	//for logging, add the response that was returned.

		    throw AppException.AppExceptionFactory(e, i); 
		}			
	}
	
	
	
	//http://stackoverflow.com/questions/6118708/determine-whether-json-is-a-jsonobject-or-jsonarray
	public static Boolean isJSONArray(String jsonString){
		return (jsonString.substring(0, 1).equals("["));			
	}
	
	//Wraps exception handling in one place
	public static String getJsonValue(String keyname, JSONObject o) throws AppException	{
		try
		{		
			return o.getString(keyname);
		}
		catch (JSONException e) {
			throw AppException.AppExceptionFactory(e,
				   ExceptionInfo.TYPE.UNEXPECTED,
				   ExceptionInfo.SEVERITY.MODERATE,  //depends on the string and how important it is 
				   "100",           												    
				   "WebServiceObject.getJsonValue",
				   String.format("Error retrieving the value for %s.", keyname)); 
		}		
	}
	
}
