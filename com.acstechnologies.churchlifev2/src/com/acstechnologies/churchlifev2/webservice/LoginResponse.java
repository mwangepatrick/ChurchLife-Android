package com.acstechnologies.churchlifev2.webservice;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginResponse extends WebServiceObject {
	
	// Example of JSON response for a login:
	// {"Email":"my.user@acstechnologies.com",
	// "SiteName":"Support Administration Group",
	// "SiteNumber":123456,
	// "UserName":"myusername"}
			
	// Keep in mind that the base object may be an array.  If that is the case, 
	//  the individual properties will only return values if an indexer is provided.
	//
	// FUTURE:  Perhaps separate logic out into a collection class or provide iterator	
	
	// Email
	public String getEmail() 				{ return getStringValue("Email", 0); }	
	public String getEmail(int indexer) 	{ return getStringValue("Email", indexer); }
	
	// SiteName
	public String getSiteName() 			{ return getStringValue("SiteName", 0); }	
	public String getSiteName(int indexer) 	{ return getStringValue("SiteName", indexer); }
	
	// SiteNumber
	public String getSiteNumber() 			{ return getStringValue("SiteNumber", 0); }	
	public String getSiteNumber(int indexer){ return getStringValue("SiteNumber", indexer); }
	
	// UserName
	public String getUserName() 			{ return getStringValue("UserName", 0); }	
	public String getUserName(int indexer) 	{ return getStringValue("UserName", indexer); }
	
		
	// Common Method  - results must be cast appropriately
	private String getStringValue(String keyname, int indexer){
		try {			
			if (super.dataIsArray()== true && indexer >=0){
				JSONObject o = super.getDataArray().getJSONObject(indexer);				
				return o.getString(keyname);
			}else {
				if (indexer == 0){
					return super.getData().getString(keyname);
				}													
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			//zzz
			e.printStackTrace();
		}
		return null;
	}
	
	
	// Constructor
	public LoginResponse(String responseString) {
		super(responseString);
	}

}
