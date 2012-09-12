package com.acstech.churchlife.webservice;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.acstech.churchlife.StringHelper;
import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;

public class CoreIndividual extends CoreObject {

	public int IndvId;
	public int FamId;
	public String FamilyPosition;	
	public String Title;
    public String FirstName;
    public String LastName;               
    public String MiddleName;
    public String GoesbyName; 
    public String Suffix;    
	public String PictureUrl;
		
    public String getDisplayNameForList()
    {
    	String name = "";
        if (StringHelper.NullOrEmpty(FirstName) != "")   { name = FirstName; }
        if (StringHelper.NullOrEmpty(LastName) != "")    { name = name + " " + LastName; }
        if (StringHelper.NullOrEmpty(Suffix) != "")      { name = name + ", " + Suffix; }
        if (StringHelper.NullOrEmpty(GoesbyName) != "")  { name = name + " (" + GoesbyName + ")"; }
          
        return name;        
    }
    
	 public static CoreIndividual GetCoreIndividual(String json) throws AppException
     {    	 
		CoreIndividual indv = new CoreIndividual();
   	  	indv._sourceJson = json;
   	  
   	  	try
   	  	{
   	  		JSONObject jo = new JSONObject(json);
	
   	  		indv.IndvId = jo.getInt("IndvId");
   	  		indv.FamId = jo.getInt("FamId");
   	  		indv.FamilyPosition = jo.getString("FamilyPosition");   	  		
   	  		indv.Title = jo.optString("Title");
   	  		indv.FirstName = jo.getString("FirstName");
   	  		indv.LastName = jo.getString("LastName");
   	  		indv.MiddleName = jo.optString("MiddleName");
   	  		indv.GoesbyName = jo.optString("GoesbyName");
   	  		indv.Suffix = jo.optString("Suffix");
   			indv.PictureUrl = jo.optString("PictureUrl");   		
   		}
   	  	catch (JSONException e) {
			
			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
								ExceptionInfo.TYPE.UNEXPECTED,
							  	ExceptionInfo.SEVERITY.MODERATE, 
							  	"100", 
							  	"CoreIndividual.factory", 
							  	"Error creating object.");
			
		    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed

		    throw AppException.AppExceptionFactory(e, i); 
   	  	}	
   	  	return indv;		
     }
     
     // Factory Method for list of CoreAcsUser objects
     public static CorePagedResult<List<CoreIndividual>> GetCoreIndividualPagedResult(String json) throws AppException
     { 
     	CorePagedResult<List<CoreIndividual>> results = new CorePagedResult<List<CoreIndividual>>(json);
   	  	
   	  	try {  	  		
   	  		JSONObject jo = new JSONObject(json);
   	  	 
   	  		results.PageCount = jo.getInt("PageCount");
   	  		results.PageIndex = jo.getInt("PageIndex");
   	  		results.PageSize = jo.getInt("PageSize");
   	  		
   	  		//Page (list of CoreIndividual objects)  	  		
   	  		JSONArray ja = jo.getJSONArray("Page");
   	  		results.Page = new ArrayList<CoreIndividual>();  	  	
   	  		
   	  		for (int i = 0; i < ja.length(); i++) {
   	  			JSONObject cs = ja.getJSONObject(i);  	  			
   	  			results.Page.add(CoreIndividual.GetCoreIndividual(cs.toString()));  	  			
   	  		}   	  		  	  	
 	    }
 		catch (JSONException e) {
 			
 			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
 								ExceptionInfo.TYPE.UNEXPECTED,
 							  	ExceptionInfo.SEVERITY.MODERATE, 
 							  	"100", 
 							  	"CoreIndividual.factory", 
 							  	"Error creating object.");
 			
 		    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed
 	
 		    throw AppException.AppExceptionFactory(e, i); 
 		}	
   	  	return results;
   	 }

}
