package com.acstech.churchlife.webservice;

import com.acstech.churchlife.exceptionhandling.AppException;


/**
 * 
 * @author softwarearchitect
 *
 */
public class IndividualsResponse extends WebServiceObject {
	
	// Example of JSON response for a single individual:
	// {"IndvId":"001","PrimFamily":"002","LastName":"Smith","FirstName":"Joe",
	//  "MiddleName":"","GoesbyName":"","Suffix":"","Title":"Mr.","PictureUrl":"",
	//  "Unlisted":false}
			
	// Keep in mind that the base object is an array.  Accessing the 
	//  individual properties can only be done with an indexer.
	//
	// FUTURE:  Perhaps separate logic out into a collection class or provide iterator	
	
	// IndvId	
	public int getIndvId(int indexer) 		throws AppException { return Integer.parseInt(getStringValue("IndvId", indexer)); }
	
	// PrimFamily	
	public int getPrimFamily(int indexer) 	throws AppException { return Integer.parseInt(getStringValue("PrimFamily", indexer)); }
	
	// LastName	
	public String getLastName(int indexer)		throws AppException { return getStringValue("LastName", indexer); }
	
	// FirstName
	public String getFirstName(int indexer) 	throws AppException { return getStringValue("FirstName", indexer); }

	// MiddleName
	public String getMiddleName(int indexer) 	throws AppException { return getStringValue("MiddleName", indexer); }
	
	// GoesbyName
	public String getGoesbyName(int indexer) 	throws AppException { return getStringValue("GoesbyName", indexer); }
	
	// Suffix
	public String getSuffix(int indexer) 		throws AppException { return getStringValue("Suffix", indexer); }

	// Title	
	public String getTitle(int indexer) 		throws AppException { return getStringValue("Title", indexer); }
	
	// PictureUrl
	public String getPictureUrl(int indexer)	throws AppException { return getStringValue("PictureUrl", indexer); }
	
	// Unlisted 
	public Boolean getUnlisted(int indexer)		throws AppException { return Boolean.parseBoolean(getStringValue("Unlisted", indexer)); }
	

	// Constructor
	public IndividualsResponse(String responseString) throws AppException {
		super(responseString);	
	}

}

