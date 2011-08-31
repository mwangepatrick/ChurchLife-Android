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
 *   from the web server.  This class 'flattens' the different responses to provide
 *   a common interface with which to access web service objects.  
 *   
 *   Think dataset/recordset for dealing with the actual data records.
 *    Item(i), Count, etc.
 *       
 *  NOTE: The web services can return a response with a 'Data' element that contains:
 *    1.  a single object 										(object response)
 *    2.  an array of objects									(array response)
 *    3.  an object that contains an array of child objects 	(listing response)
 *  
 *  Example responses:
 *  
 *  1.  Single object returned
 *    {  "StatusCode":0,
 *       "Message":"Success",
 *       "Data": {
 *      			"Property1":"property one value", "Property2":"property two value"
 *      		 }
 *    }
 *  
 *  2.  Multiple return values, not paged (array response)
 *    {  "StatusCode":0,
 *       "Message":"Success",
 *    	 "Data":[
 *    			  {"Property1":"property value 1","Property2":"property value 2"},
 *    			  {"Property1":"another value 1","Property2":"another value 2"},
 *    			  {"Property1":"something value 1","Property2":"something value 2"}
 *    			]
 *    }
 *  
 *  3.  Listing of multiple, paged records (note, child Data element inside top level Data element)
 *    {	"StatusCode":0,
 *      "Message":"Success"
 *      "Data":	{
 *		    		"MaxResult":0,
 *					"HasMore":false,
 *					"FirstResult":0,
 *					"Data":[
								{"Property1":"property value 1","Property2":"property value 2"},
 *    			  				{"Property1":"another value 1","Property2":"another value 2"}
 *    						]
 *	     		}
 *
 *	  }
 *  
 *  
 * @author softwarearchitect
 *
 */
public class WebServiceObject {

	private JSONObject _mainObject = null;						// state variable
	
	
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
	
	//------------------------------------------------------------------------------
	// Properties specific to a data listing (non-existent in other return types)
	//------------------------------------------------------------------------------

	/** 
	 *  When the service returns multiple records it embeds (in the top level Data element
	 *   several properties for working with the records returned).  The top level Data element
	 *   contains a child-level element named Data that contains the actual records.
	 *   
	 *   This property only applies to a data listing
	 *  
	 *
	 * @return
	 * @throws AppException
	 */
	public Boolean getHasMore() throws AppException {
		try {
			Boolean value = false;
			
			if (_mainObject.optJSONObject("Data") != null) {
				JSONObject dataObj = _mainObject.getJSONObject("Data");					
				if (dataObj.isNull("HasMore") == false) {
					value = dataObj.getBoolean("HasMore");
				}				
			}
			return  value;			
		} catch (JSONException e) {
			throw AppException.AppExceptionFactory(e,
					   ExceptionInfo.TYPE.UNEXPECTED,
					   ExceptionInfo.SEVERITY.CRITICAL, 
					   "100",           												    
					   "WebServiceObject.getHasMore",
					   "Error attempting to retrieve the HasMore property from the web service response."); 
		}
	}
	
	/** 
	 *  When the service returns multiple records it embeds (in the top level Data element
	 *   several properties for working with the records returned).  The top level Data element
	 *   contains a child-level element named Data that contains the actual records.
	 *   
	 *   This property only applies to a data listing
	 *  
	 *
	 * @return
	 * @throws AppException
	 */
	public Integer getFirstResult() throws AppException {
		try {
			Integer value = 0;

			if (_mainObject.optJSONObject("Data") != null) {				
				JSONObject dataObj = _mainObject.getJSONObject("Data");				
				if (dataObj.isNull("FirstResult") == false) {
					value = dataObj.getInt("FirstResult");
				}				
			}
			return value;			
		} catch (JSONException e) {
			throw AppException.AppExceptionFactory(e,
					   ExceptionInfo.TYPE.UNEXPECTED,
					   ExceptionInfo.SEVERITY.CRITICAL, 
					   "100",           												    
					   "WebServiceObject.getFirstResult",
					   "Error attempting to retrieve the FirstResult property from the web service response."); 
		}
	}
	
	/** 
	 *  When the service returns multiple records it embeds (in the top level Data element
	 *   several properties for working with the records returned).  The top level Data element
	 *   contains a child-level element named Data that contains the actual records.
	 *   
	 *   This property only applies to a data listing
	 *  
	 *
	 * @return
	 * @throws AppException
	 */
	public Integer getMaxResult() throws AppException {
		try {
			Integer value = 0;
			
			if (_mainObject.optJSONObject("Data") != null) {
				JSONObject dataObj = _mainObject.getJSONObject("Data");	
				if (dataObj.isNull("MaxResult") == false) {
					value = dataObj.getInt("MaxResult");
				}							
			}										
			return value;			
		} catch (JSONException e) {
			throw AppException.AppExceptionFactory(e,
					   ExceptionInfo.TYPE.UNEXPECTED,
					   ExceptionInfo.SEVERITY.CRITICAL, 
					   "100",           												    
					   "WebServiceObject.getMaxResult",
					   "Error attempting to retrieve the MaxResult property from the web service response."); 
		}
	}
		
	//------------------------------------------------------------------------------
	// Properties for returning a single record or an array of records
	//------------------------------------------------------------------------------
	/**
	 * Returns the data record for the given indexer.  For responses that only have 
	 *  a single object, returns that one.
	 *  
	 * @param indexer
	 * @return
	 * @throws AppException 
	 */
	public JSONObject getItem(int indexer) throws AppException {
		
		JSONObject result = null;
		
		try{
			// Check for top level "Data" element as a JSON object
			// ...if so, must be single object response or a listing response.
			if (_mainObject.optJSONObject("Data") != null) {
				
				JSONObject topLevelData = _mainObject.getJSONObject("Data");
				
				// Check for a listing response 
				//  ...the top level "Data" object will have a child "List" JSON array										
				if (topLevelData.optJSONArray("List") != null) {
					JSONArray childDataArray = topLevelData.getJSONArray("List");														
					result = childDataArray.getJSONObject(indexer);
				}
				else {	// Set the return to the single object response
					result = topLevelData;
				}								
			}
			// Check for Data as a JSON array (array response)
			else if (_mainObject.optJSONArray("Data") != null) {
				JSONArray dataArray = _mainObject.getJSONArray("Data");
				result = dataArray.getJSONObject(indexer);				
			}							
		
		} catch (JSONException e) {
			throw AppException.AppExceptionFactory(e,
					   ExceptionInfo.TYPE.UNEXPECTED,
					   ExceptionInfo.SEVERITY.CRITICAL, 
					   "100",           												    
					   "WebServiceObject.Item",
					   "Error attempting to retrieve the Item property from the web service response."); 
		}
		return result;
	}
	
	
	/**
	 *  Examines the response object to see how many records were returned.  Keep in mind that the
	 *   response can be a single object, an array of objects, or a record listing. 
	 *   
	 * @return
	 */
	public int getLength() throws AppException {
		
		int result = 0;
		
		try{
			// Check for top level "Data" element as a JSON object
			// ...if so, must be single object response or a listing response.
			if (_mainObject.optJSONObject("Data") != null) {
				
				JSONObject topLevelData = _mainObject.getJSONObject("Data");
				
				// Check for a listing response 
				//  ...the top level "Data" object will have a child "List" JSON array										
				if (topLevelData.optJSONArray("List") != null) {
					JSONArray childDataArray = topLevelData.getJSONArray("List");														
					result = childDataArray.length();
				}
				else {
					result = 1;	// single object response
				}								
			}
			// Check for Data as a JSON array (array response)
			else if (_mainObject.optJSONArray("Data") != null) {
				JSONArray dataArray = _mainObject.getJSONArray("Data");
				result = dataArray.length();			
			}							
		
		} catch (JSONException e) {
			throw AppException.AppExceptionFactory(e,
					   ExceptionInfo.TYPE.UNEXPECTED,
					   ExceptionInfo.SEVERITY.CRITICAL, 
					   "100",           												    
					   "WebServiceObject.Count",
					   "Error attempting to retrieve the Count property from the web service response."); 
		}
		return result;
	}
	
	
	/**
	 *  Helper method to shortcut the getting of a property value from an record in this object.  
	 *  
	 *  Always returns the string (JSON is a string you know), so results must be cast appropriately
	 *  
	 * @param keyname
	 * @param indexer
	 * @return
	 * @throws AppException
	 */
	protected String getStringValue(String keyname, int indexer) throws AppException{
							
		JSONObject o = getItem(indexer);
		
		if (o == null) {
			throw AppException.AppExceptionFactory(
					ExceptionInfo.TYPE.APPLICATION, ExceptionInfo.SEVERITY.CRITICAL, 
					"100", "WebServiceObject.getStringValue", 
					String.format("Invalid read attempt.  No record found at index %s.", indexer));	
		}
		else {
			return WebServiceObject.getJsonValue(keyname, o);
		}	
	}
		


	
	
	
	
	
	
	
	
	
	
	
//	// can return null if the data is an array
//	public JSONObject getData() throws AppException{
//		
//		try {
//			if (dataIsArray()) {
//				return null;	
//			}
//			else{
//				return _mainObject.getJSONObject("Data");	
//			}
//		} catch (JSONException e) {
//			throw AppException.AppExceptionFactory(e,
//					   ExceptionInfo.TYPE.UNEXPECTED,
//					   ExceptionInfo.SEVERITY.CRITICAL, 
//					   "100",           												    
//					   "WebServiceObject.getData",
//					   "Error attempting to retrieve the JSON object from the web service response."); 
//		}
//		
//	}
//	
//	
//	// Do NOT use the public getStringValue here - infinite loop
//	public Boolean dataIsArray() throws AppException{
//		try {
//			//zzz update to use getRecordsData and check that (may be a child of the main Data node)
//			return (isJSONArray(WebServiceObject.getJsonValue("Data", _mainObject)));		
//		} catch (AppException e) {
//			ExceptionInfo ei = e.addInfo();
//			ei.setContextId("WebServiceObject.dataIsArray");
//			ei.setSeverity(ExceptionInfo.SEVERITY.CRITICAL);
//			ei.setErrorDescription("Error determining if the data was a JSON array.");					
//			throw e; 
//		}	
//	}
	
	//zzz remove after testing
//	//zzz update to use getRecordsData
//	public int getLength() throws AppException{ 
//		if (dataIsArray()== false){
//			return 1;
//		}	
//		else {
//			return (getDataArray().length());
//		}		
//	}
	

	
	// can return null if the data is an object
//	public JSONArray getDataArray() throws AppException {
//		try {
//			if (dataIsArray()) {
//				return _mainObject.getJSONArray("Data");
//			}
//			else{
//				return null;		
//			}
//		} catch (JSONException e) {
//			throw AppException.AppExceptionFactory(e,
//					   ExceptionInfo.TYPE.UNEXPECTED,
//					   ExceptionInfo.SEVERITY.CRITICAL, 
//					   "100",           												    
//					   "WebServiceObject.getDataArray",
//					   "Error attempting to retrieve the JSON array from the web service response."); 
//		}
//	}	
	
	public String toString()
	{
		return _mainObject.toString();		
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
	//  currently NOT being used
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
