package com.acstechnologies.churchlifev2.webservice;

import com.acstechnologies.churchlifev2.exceptionhandling.AppException;
import com.acstechnologies.churchlifev2.exceptionhandling.ExceptionInfo;

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
	
	
	/**
	 * Formats a listing of full names (first + last) of all of the people in this object
	 * 
	 * @param includeMore  if more than the requested number of results exist, a 'more' will be added
	 * 						to the end of this list.
	 * @return String[]
	 */
	public String[] getFullNameList(Boolean includeMore) throws AppException {
		return getFullNameList(includeMore, "...");  //Default text to display
	}
	
	public String[] getFullNameList(Boolean includeMore, String moreTextValue) throws AppException {
		
		try {			
			String[] result;
		
			// Create the names array and assign a 'more' item if specified
			if (this.getHasMore() == true && includeMore == true) {
				result =  new String[getLength()+1];			 						 
				result[getLength()] = moreTextValue;
			}
			else {
				 result =  new String[getLength()];
			}
	
			// Build the name list array and return it
			for (int i = 0; i < getLength(); i++) {		   		   
				result[i] = this.getFirstName(i) + " " + this.getLastName(i);		  
			}							
			
			return result;
		}
		catch (AppException e) {			
			// Add specific exception information
			ExceptionInfo ei = e.addInfo();
			ei.setContextId("WebServiceObject.getFullNameList");
			ei.setSeverity(ExceptionInfo.SEVERITY.CRITICAL);
			ei.setErrorDescription("Error attempting to format names array.");					
			throw e;
		}	
	}
	
	// Constructor
	public IndividualsResponse(String responseString) throws AppException {
		super(responseString);	
	}

}

