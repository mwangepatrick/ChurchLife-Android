package com.acstech.churchlife.webservice;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.acstech.churchlife.StringHelper;
import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;

public class CoreIndividualDetail extends CoreObject  {

	public int IndvId;
	public int PrimFamily;
	public String LastName;
	public String FirstName;
	public String MiddleName;
	public String GoesbyName;
	public String Suffix;
	public String Title;
	public String FullName;
	public String FriendlyName;
	public String PictureUrl;
	public String FamilyPictureUrl;	
	public String DateOfBirth;
	public String MemberStatus;
	
	
	public List<CoreIndividualAddress> Addresses;	
	public List<CoreIndividualEmail> Emails;	
	public List<CoreIndividualPhone> Phones;	
	public List<CoreIndividual> FamilyMembers;
	
    public String getDisplayNameForList()
    {
    	String name = "";
        if (StringHelper.NullOrEmpty(FirstName) != "")   { name = FirstName; }
        if (StringHelper.NullOrEmpty(LastName) != "")    { name = name + " " + LastName; }
        if (StringHelper.NullOrEmpty(Suffix) != "")      { name = name + ", " + Suffix; }
        if (StringHelper.NullOrEmpty(GoesbyName) != "")  { name = name + " (" + GoesbyName + ")"; }
          
        return name;        
    }

    public String getPictureUrl() 
    {
    	String imageUrl = PictureUrl;
    	if (imageUrl.trim().length() == 0) {
    		imageUrl = FamilyPictureUrl;
    	}   
    	//testing
    	//imageUrl = "https://secure.accessacs.com/accesspict/14447/acsThumb55.jpg";
    	return imageUrl;
    }
    
    //Helper
    public String getEntireName()
    {
        String name = "";
        if (!Title.equals(""))		{ name = Title + " "; }
        if (!FirstName.equals("")) 	{ name = name + FirstName + " "; }
        if (!GoesbyName.equals("")) { name = name + "(" + GoesbyName + ") "; }
        if (!MiddleName.equals("")) { name = name + MiddleName + " "; }
        if (!LastName.equals(""))	{ name = name + LastName + " "; }
        if (!Suffix.equals("")) 	{ name = name + Suffix; }
        return name;       
    }
        
    // Factory Method - parse json
    public static CoreIndividualDetail GetCoreIndividualDetail(String json) throws AppException
    {    	
    	CoreIndividualDetail individual = new CoreIndividualDetail();
  	  	individual._sourceJson = json;
  	  
  	  	try
  	  	{
  	  	  JSONObject jo = new JSONObject(json);
  		  
  		  individual.IndvId = jo.getInt("IndvId");
  		  individual.PrimFamily = jo.optInt("PrimFamily");
  		  individual.LastName = jo.getString("LastName");
  		  individual.FirstName = jo.getString("FirstName");
  		  individual.MiddleName = StringHelper.NullOrEmpty(jo.getString("MiddleName"));
  		  individual.GoesbyName = StringHelper.NullOrEmpty(jo.getString("GoesbyName")); 
  		  individual.Suffix = StringHelper.NullOrEmpty(jo.getString("Suffix"));
  		  individual.Title = StringHelper.NullOrEmpty(jo.getString("Title"));
  		  individual.FullName = jo.getString("FullName");
  		  individual.FriendlyName = jo.getString("FriendlyName");
  		  individual.PictureUrl = jo.getString("PictureUrl");
  		  individual.FamilyPictureUrl = jo.getString("FamilyPictureUrl");   		  
  		  individual.DateOfBirth = StringHelper.NullOrEmpty(jo.getString("DateOfBirth"));
  		  individual.MemberStatus = StringHelper.NullOrEmpty(jo.getString("MemberStatus"));
 		  
  		  //Addresses   	  		
  		  JSONArray ja = jo.getJSONArray("Addresses");	
  		  individual.Addresses = new ArrayList<CoreIndividualAddress>();
  		  
  		  for (int i = 0; i < ja.length(); i++) {
  			  JSONObject addr = ja.getJSONObject(i);  	  			
  			  individual.Addresses.add(CoreIndividualAddress.GetCoreIndividualAddress(addr.toString()));  	  			
  		  } 

  		  //Emails   	  		
  		  ja = jo.getJSONArray("Emails");	
  		  individual.Emails = new ArrayList<CoreIndividualEmail>();
  		  
  		  for (int i = 0; i < ja.length(); i++) {
  			  JSONObject email = ja.getJSONObject(i);  	  			
  			  individual.Emails.add(CoreIndividualEmail.GetCoreIndividualEmail(email.toString()));  	  			
  		  } 
  		  
  		  // Phones
  		  ja = jo.getJSONArray("Phones");	
  		  individual.Phones = new ArrayList<CoreIndividualPhone>();
  		  
  		  for (int i = 0; i < ja.length(); i++) {
  			  JSONObject phone = ja.getJSONObject(i);  	  			
  			  individual.Phones.add(CoreIndividualPhone.GetCoreIndividualPhone(phone.toString()));  	  			
  		  } 
  		  
  		  // Family Members
  		  ja = jo.getJSONArray("FamilyMembers");	
		  individual.FamilyMembers = new ArrayList<CoreIndividual>();
		  
		  for (int i = 0; i < ja.length(); i++) {
			  JSONObject member = ja.getJSONObject(i);  	  			
			  individual.FamilyMembers.add(CoreIndividual.GetCoreIndividual(member.toString()));  	  			
		  }
		  
  	  	}
  	  	catch (JSONException e) {
			
			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
								ExceptionInfo.TYPE.UNEXPECTED,
							  	ExceptionInfo.SEVERITY.MODERATE, 
							  	"100", 
							  	"CoreIndividualDetail.factory", 
							  	"Error creating object.");
			
		    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed

		    throw AppException.AppExceptionFactory(e, i); 
  	  	}	
  	  	return individual;		
    }

}
