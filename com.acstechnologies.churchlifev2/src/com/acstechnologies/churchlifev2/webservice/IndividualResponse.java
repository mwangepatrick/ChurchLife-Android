package com.acstechnologies.churchlifev2.webservice;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

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
	
	//***************************
	// ***   Child Objects 	  ***
	// **************************
	
	//zzz duplication exists here.....refactor to use a standard base class for these children or generics
	
	public ArrayList<IndividualPhone> getPhoneNumbers() throws AppException {
		ArrayList<IndividualPhone> list = new ArrayList<IndividualPhone>();
		
		// Populate the object with the json contained in this object
		try {
			JSONArray phones = getItem(0).getJSONArray("Phones");
			
			for (int i = 0; i < phones.length(); i++) {
				String phoneJson = phones.getString(i);
				list.add(new IndividualPhone(phoneJson));			    			    			   
			}

		} catch (JSONException e) {

			//zzz what about the appException being bubbled up?
			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
					ExceptionInfo.TYPE.UNEXPECTED,
				  	ExceptionInfo.SEVERITY.MODERATE, 
				  	"100", 
				  	"IndividualResponse.getPhoneNumbers", 
				  	"Error creating the arraylist.");

			throw AppException.AppExceptionFactory(e, i); 
		}
		
		return list;			
	}

	public ArrayList<IndividualEmail> getEmails() throws AppException {
		
		ArrayList<IndividualEmail> list = new ArrayList<IndividualEmail>();
		
		// Populate the object with the json contained in this object
		try {
			JSONArray items = getItem(0).getJSONArray("Emails");
			
			for (int i = 0; i < items.length(); i++) {
				String phoneJson = items.getString(i);
				list.add(new IndividualEmail(phoneJson));			    			    			   
			}

		} catch (JSONException e) {

			//zzz what about the appException being bubbled up?
			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
					ExceptionInfo.TYPE.UNEXPECTED,
				  	ExceptionInfo.SEVERITY.MODERATE, 
				  	"100", 
				  	"IndividualResponse.getEmails", 
				  	"Error creating the arraylist.");

			throw AppException.AppExceptionFactory(e, i); 
		}
		
		return list;			
	}
	
	
	public ArrayList<IndividualAddress> getAddresses() throws AppException {
		
		ArrayList<IndividualAddress> list = new ArrayList<IndividualAddress>();
		
		// Populate the object with the json contained in this object
		try {
			JSONArray items = getItem(0).getJSONArray("Addresses");
			
			for (int i = 0; i < items.length(); i++) {
				String phoneJson = items.getString(i);
				list.add(new IndividualAddress(phoneJson));			    			    			   
			}

		} catch (JSONException e) {

			//zzz what about the appException being bubbled up?
			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
					ExceptionInfo.TYPE.UNEXPECTED,
				  	ExceptionInfo.SEVERITY.MODERATE, 
				  	"100", 
				  	"IndividualResponse.getAddresses", 
				  	"Error creating the arraylist.");

			throw AppException.AppExceptionFactory(e, i); 
		}
		
		return list;			
	}
	
	// Family Members
	public ArrayList<IndividualFamilyMember> getFamilyMembers() throws AppException {
		
		ArrayList<IndividualFamilyMember> list = new ArrayList<IndividualFamilyMember>();
		
		// Populate the object with the json contained in this object
		try {
			JSONArray items = getItem(0).getJSONArray("FamilyMembers");
			
			for (int i = 0; i < items.length(); i++) {
				String phoneJson = items.getString(i);
				list.add(new IndividualFamilyMember(phoneJson));			    			    			   
			}

		} catch (JSONException e) {

			//zzz what about the appException being bubbled up?
			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
					ExceptionInfo.TYPE.UNEXPECTED,
				  	ExceptionInfo.SEVERITY.MODERATE, 
				  	"100", 
				  	"IndividualResponse.getFamilyMembers", 
				  	"Error creating the arraylist.");

			throw AppException.AppExceptionFactory(e, i); 
		}
		
		return list;			
	}


	// Constructor
	public IndividualResponse(String responseString) throws AppException {
		super(responseString);	
	}

}
