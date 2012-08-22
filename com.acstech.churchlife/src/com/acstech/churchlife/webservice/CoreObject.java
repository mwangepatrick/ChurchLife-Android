package com.acstech.churchlife.webservice;

import org.json.JSONObject;

import com.acstech.churchlife.exceptionhandling.AppException;

/**
 * Base object for all JSON objects returned by the REST web services
 *  
 * @author softwarearchitect
 *
 */
public abstract class CoreObject {
	
	protected String _sourceJson;
	
    public String toJsonString() throws AppException
	{    	
    	// if this object was built by property setters and NOT from an parsed json string...
    	if (_sourceJson == null || _sourceJson.length() ==0)
    	{
    		_sourceJson = toJsonObject().toString();
    	}
    	return _sourceJson;		
	}
    
    // sub classes don't have to provide...but should override when applicable
    public JSONObject toJsonObject() throws AppException{
    	return null;
    }
   
}
