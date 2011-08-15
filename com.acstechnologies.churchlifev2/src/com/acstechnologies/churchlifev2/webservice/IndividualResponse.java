package com.acstechnologies.churchlifev2.webservice;

import com.acstechnologies.churchlifev2.exceptionhandling.AppException;
import com.acstechnologies.churchlifev2.exceptionhandling.ExceptionInfo;

/**
 * 
 * @author softwarearchitect
 *
 */
public class IndividualResponse extends WebServiceObject {
	
	// Example of JSON response for an individual:
	// {"IndvId":"001","PrimFamily":"002","LastName":"Smith","FirstName":"Joe",
	//  "MiddleName":"","GoesbyName":"","Suffix":"","Title":"Mr.","PictureUrl":"",
	//  "Unlisted":false}
			
	// Keep in mind that the base object may be an array.  If that is the case, 
	//  the individual properties will only return values if an indexer is provided.
	//
	// FUTURE:  Perhaps separate logic out into a collection class or provide iterator	
	
	// IndvId	
	public int getIndvId() 					throws AppException { return Integer.parseInt(getStringValue("IndvId", 0)); }	
	public int getIndvId(int indexer) 		throws AppException { return Integer.parseInt(getStringValue("IndvId", indexer)); }
	
	// PrimFamily
	public int getPrimFamily() 				throws AppException { return Integer.parseInt(getStringValue("PrimFamily", 0)); }	
	public int getPrimFamily(int indexer) 	throws AppException { return Integer.parseInt(getStringValue("PrimFamily", indexer)); }
	
	// LastName
	public String getLastName() 				throws AppException { return getStringValue("LastName", 0); }	
	public String getLastName(int indexer)		throws AppException { return getStringValue("LastName", indexer); }
	
	// FirstName
	public String getFirstName() 				throws AppException { return getStringValue("FirstName", 0); }	
	public String getFirstName(int indexer) 	throws AppException { return getStringValue("FirstName", indexer); }

	// MiddleName
	public String getMiddleName() 				throws AppException { return getStringValue("MiddleName", 0); }	
	public String getMiddleName(int indexer) 	throws AppException { return getStringValue("MiddleName", indexer); }
	
	// GoesbyName
	public String getGoesbyName() 				throws AppException { return getStringValue("GoesbyName", 0); }	
	public String getGoesbyName(int indexer) 	throws AppException { return getStringValue("GoesbyName", indexer); }
	
	// Suffix
	public String getSuffix() 					throws AppException { return getStringValue("Suffix", 0); }	
	public String getSuffix(int indexer) 		throws AppException { return getStringValue("Suffix", indexer); }

	// Title	
	public String getTitle() 					throws AppException { return getStringValue("Title", 0); }	
	public String getTitle(int indexer) 		throws AppException { return getStringValue("Title", indexer); }
	
	// PictureUrl
	public String getPictureUrl() 				throws AppException { return getStringValue("PictureUrl", 0); }	
	public String getPictureUrl(int indexer)	throws AppException { return getStringValue("PictureUrl", indexer); }
	
	// Unlisted 
	// TODO return boolean
	public String getUnlisted() 				throws AppException { return getStringValue("Unlisted", 0); }	
	public String getUnlisted(int indexer)		throws AppException { return getStringValue("Unlisted", indexer); }
	
	
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
	public IndividualResponse(String responseString) throws AppException {
		super(responseString);	
	}

}

