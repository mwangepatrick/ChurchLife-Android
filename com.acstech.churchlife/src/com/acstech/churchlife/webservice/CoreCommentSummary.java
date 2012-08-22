package com.acstech.churchlife.webservice;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.acstech.churchlife.DateHelper;
import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;

public class CoreCommentSummary extends CoreObject {

    public int CommentTypeID;
    public String CommentType;
    public Date LastCommentDate;
    public int CommentCount;
    public String CommentColor;
    
   
    // Factory Method - parse json
    public static CoreCommentSummary GetCoreCommentSummary(String json) throws AppException
    {    	
      CoreCommentSummary summary = new CoreCommentSummary();
  	  summary._sourceJson = json;
  	  
  	  try
  	  {
  		  JSONObject jo = new JSONObject(json);
	
  		  summary.CommentTypeID = jo.getInt("CommentTypeID");
  		  summary.CommentType = jo.getString("CommentType");
  		  summary.LastCommentDate = DateHelper.StringToDate(jo.getString("LastCommentDate"), "MM/dd/yyyy");
  		  summary.CommentCount = jo.getInt("CommentCount");
  		  summary.CommentColor = jo.getString("CommentColor");  	    
  	  }
  	  catch (JSONException e) {
			
			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
								ExceptionInfo.TYPE.UNEXPECTED,
							  	ExceptionInfo.SEVERITY.MODERATE, 
							  	"100", 
							  	"CoreCommentSummary.factory", 
							  	"Error creating object.");
			
		    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed

		    throw AppException.AppExceptionFactory(e, i); 
  	  }	
  	  return summary;		
    }
    
    // Factory Method for CorePagedResult of CoreCommentSummary objects
    public static CorePagedResult<List<CoreCommentSummary>> GetCoreCommentSummaryPagedResult(String json) throws AppException
    { 
    	CorePagedResult<List<CoreCommentSummary>> results = new CorePagedResult<List<CoreCommentSummary>>(json);
  	  	
  	  	try {  	  		
  	  		JSONObject jo = new JSONObject(json);
  	  	 
  	  		results.PageCount = jo.getInt("PageCount");
  	  		results.PageIndex = jo.getInt("PageIndex");
  	  		results.PageSize = jo.getInt("PageSize");
  	  		
  	  		//Page (list of CoreCommentSummary object)  	  		
  	  		JSONArray ja = jo.getJSONArray("Page");
  	  		results.Page = new ArrayList<CoreCommentSummary>();  	  	
  	  		
  	  		for (int i = 0; i < ja.length(); i++) {
  	  			JSONObject cs = ja.getJSONObject(i);  	  			
  	  			results.Page.add(CoreCommentSummary.GetCoreCommentSummary(cs.toString()));  	  			
  	  		}   	  		  	  	
	    }
		catch (JSONException e) {
			
			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
								ExceptionInfo.TYPE.UNEXPECTED,
							  	ExceptionInfo.SEVERITY.MODERATE, 
							  	"100", 
							  	"CoreCommentSummary.factory", 
							  	"Error creating object.");
			
		    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed
	
		    throw AppException.AppExceptionFactory(e, i); 
		}	
  	  	return results;
    }

}
