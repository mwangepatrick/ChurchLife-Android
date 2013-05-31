package com.acstech.churchlife.webservice;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 
import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;

public class CoreOrganizationDetail extends CoreObject {

    public int OrgId;
    public int ParentId;      
    public String Name;
    public String RefName;
    public String ParentName;
    public int LevelTypeId;    
    public String LevelTypeDesc;
    public String PictureUrl;
    public int PrimaryIndvId;
    public String PrimaryContactName;
       
    public List<CoreOrganizationAddress> Addresses;	
	public List<CoreOrganizationEmail> Emails;	
	public List<CoreOrganizationPhone> Phones;
	
    public List<CoreOrganization> Top5Children;
	
	 public static CoreOrganizationDetail GetCoreOrganizationDetail(String json) throws AppException
     {    	 
		CoreOrganizationDetail org = new CoreOrganizationDetail();
   	  	org._sourceJson = json;
   	  
   	  	try
   	  	{
   	  		JSONObject jo = new JSONObject(json);
	
   	  		org.OrgId = jo.getInt("OrgId");
   	  		org.ParentId = jo.optInt("ParentId");   	 	
   	  		org.Name = jo.getString("Name"); 
   	 		org.RefName = jo.getString("RefName"); 
   	 		org.ParentName = jo.optString("ParentName"); 
   	  		org.LevelTypeId = jo.getInt("LevelTypeId");
   	  		org.LevelTypeId = jo.getInt("LevelTypeId");
   	  		org.LevelTypeDesc = jo.getString("LevelTypeDesc");   	  	
   	  		org.PictureUrl = jo.getString("PictureUrl");
   	  		org.PrimaryIndvId = jo.getInt("PrimaryIndvId");   	 
   	  		org.PrimaryContactName = jo.getString("PrimaryContactName");
   	  	   	 	
   	  		//Addresses   	  		
    		JSONArray ja = jo.getJSONArray("Addresses");	
    		org.Addresses = new ArrayList<CoreOrganizationAddress>();
    		  
    		for (int i = 0; i < ja.length(); i++) {
    			  JSONObject addr = ja.getJSONObject(i);  	  			
    			  org.Addresses.add(CoreOrganizationAddress.GetCoreOrganizationAddress(addr.toString()));  	  			
    		} 

    		  //Emails   	  		
    		  ja = jo.getJSONArray("Emails");	
    		  org.Emails = new ArrayList<CoreOrganizationEmail>();
    		  
    		  for (int i = 0; i < ja.length(); i++) {
    			  JSONObject email = ja.getJSONObject(i);  	  			
    			  org.Emails.add(CoreOrganizationEmail.GetCoreOrganizationEmail(email.toString()));  	  			
    		  } 
    		  
    		  // Phones
    		  ja = jo.getJSONArray("Phones");	
    		  org.Phones = new ArrayList<CoreOrganizationPhone>();
    		  
    		  for (int i = 0; i < ja.length(); i++) {
    			  JSONObject phone = ja.getJSONObject(i);  	  			
    			  org.Phones.add(CoreOrganizationPhone.GetCoreOrganizationPhone(phone.toString()));  	  			
    		  } 
    		  
    		  // Top 5 Children
    		  ja = jo.getJSONArray("Top5Children");	
    		  org.Top5Children = new ArrayList<CoreOrganization>();
  		  
	  		  for (int i = 0; i < ja.length(); i++) {
	  			  JSONObject child = ja.getJSONObject(i);  	  			
	  			  org.Top5Children.add(CoreOrganization.GetCoreOrganization(child.toString()));  	  			
	  		  }	  		  
   	  	}
   	  	catch (JSONException e) {
			
			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
								ExceptionInfo.TYPE.UNEXPECTED,
							  	ExceptionInfo.SEVERITY.MODERATE, 
							  	"100", 
							  	"CoreOrganizationDetail.factory", 
							  	"Error creating object.");
			
		    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed

		    throw AppException.AppExceptionFactory(e, i); 
   	  	}	
   	  	return org;		
     }
     
}
