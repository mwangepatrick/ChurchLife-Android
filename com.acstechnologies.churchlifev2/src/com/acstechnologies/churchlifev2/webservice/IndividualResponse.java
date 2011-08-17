package com.acstechnologies.churchlifev2.webservice;

import com.acstechnologies.churchlifev2.exceptionhandling.AppException;
import com.acstechnologies.churchlifev2.exceptionhandling.ExceptionInfo;

public class IndividualResponse extends WebServiceObject {

	// Example of JSON response for a single individual:
	// {"IndvId":"123","PrimFamily":"456","LastName":"Smith","FirstName":"Bob","MiddleName":"L","GoesbyName":"JJ","Suffix":"","Title":"Mr.",
	//  "PictureUrl":"http://www.me.com/pic/Thumb99.jpg","FamilyPictureUrl":"",
	//	"Addresses":[
	//				  	{"AddrId":"111","AddrTypeId":"4", "AddrType":"Home","SharedFlag":"Y","Country":"USA","Company":"","Address":"123 E Tree Street",
	//                	 "Address2":"","City":"MyCity", "State":"AK","Zipcode":"010101-1235","Latitude":"","Longitude":""}
	//              ],
	//   "Emails":  [
	//				  	{"EmailId":"880","EmailType":"Home","Preferred":"True","Email":"its.me@mycompany.com","Listed":"True"}
	//				],
	//   "Phones":  [
	//					{"PhoneId":"452","Active":"Y","PhoneTypeId":"1","PhoneType":"Home","SharedFlag":"Y","PhoneNumber":"111-2222",
	//                   "AreaCode":"121","Extension":"","Listed":"Y","AddrPhone":"True"}
	//				],
	// 	 "Families":[
	//					{"FamId":"456","FamilyPosition":"Head"}
	//				],
	//   "FamilyMembers":[
	//						{"IndvId":"124","PrimFamily":"456","LastName":"Smith","FirstName":"Sizy","MiddleName":"","GoesbyName":"",
	//						 "Suffix":"","Title":"Mrs.","PictureUrl":"","Unlisted":false}
	//					]
			
	
	// IndvId	
	public int getIndvId() 				throws AppException { return Integer.parseInt(getStringValue("IndvId", 0)); }
	
	// PrimFamily	
	public int getPrimFamily() 			throws AppException { return Integer.parseInt(getStringValue("PrimFamily", 0)); }
	
	// LastName	
	public String getLastName()			throws AppException { return getStringValue("LastName", 0); }
	
	// FirstName
	public String getFirstName() 		throws AppException { return getStringValue("FirstName", 0); }

	// MiddleName
	public String getMiddleName() 		throws AppException { return getStringValue("MiddleName", 0); }
	
	// GoesbyName
	public String getGoesbyName() 		throws AppException { return getStringValue("GoesbyName", 0); }
	
	// Suffix
	public String getSuffix() 			throws AppException { return getStringValue("Suffix", 0); }

	// Title	
	public String getTitle() 			throws AppException { return getStringValue("Title", 0); }
	
	// PictureUrl
	public String getPictureUrl()		throws AppException { return getStringValue("PictureUrl", 0); }

	// FamilyPictureUrl
	public String getFamilyPictureUrl()	throws AppException { return getStringValue("FamilyPictureUrl", 0); }
	
	// *** Child Objects *** 
	

	
	
	
	
	// Constructor
	public IndividualResponse(String responseString) throws AppException {
		super(responseString);	
	}

}
