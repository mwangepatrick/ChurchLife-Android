package com.acstechnologies.churchlifev2.webservice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// This object is returned from calls inside the WebServiceHandler. 
// It wraps common JSON functionality.  
//
//The web services can return a response with a single object in the 
//  "Data" element or an array of objects in the "Data" element.
public class WebServiceObject {

	// state variable
	private JSONObject _mainObject = null;
	
	public int getStatusCode() throws JSONException{
		return _mainObject.getInt("StatusCode");
	}
	
	public String getMessage() throws JSONException{
		return _mainObject.getString("Message");
	}
	
	public Boolean dataIsArray() throws JSONException{
		return (isJSONArray(_mainObject.getString("Data")));		
	}
	
	public int getLength() throws JSONException{ 
		if (dataIsArray()== false){
			return 1;
		}	
		else {
			return (getDataArray().length());
		}		
	}
	
	// can return null if the data is an array
	public JSONObject getData() throws JSONException{
		
		if (dataIsArray()) {
			return null;	
		}
		else{
			return _mainObject.getJSONObject("Data");	
		}			
	}
	
	// can return null if the data is an object
	public JSONArray getDataArray() throws JSONException{
		if (dataIsArray()) {
			return _mainObject.getJSONArray("Data");	
		}
		else{
			return null;				
		}	
	}	
	
	public String toString()
	{
		return _mainObject.toString();		
	}
	
	
	// Constructor
	protected WebServiceObject(String responseString)
	{
		// Parse the request string into the standard parts.		
		try {										
			//TODO what if error? webservice returns 404 or something.... need to catch and indicate!			
			_mainObject = new JSONObject(responseString);			
			
		} catch (JSONException e) {			
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
	}
	
	//http://stackoverflow.com/questions/6118708/determine-whether-json-is-a-jsonobject-or-jsonarray
	public static Boolean isJSONArray(String jsonString){
		return (jsonString.substring(0, 1).equals("["));			
	}
	

	
}
