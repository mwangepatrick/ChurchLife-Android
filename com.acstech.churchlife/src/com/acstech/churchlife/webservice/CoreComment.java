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

public class CoreComment extends CoreObject {

	public int CommentKey;
    public int CommentTypeID;
    public String CommentType;
    public Date CommentDate;
    public String CommentBody;
    public String CommentColor;

    
    // Factory Method - parse json
    public static CoreComment GetCoreComment(String json) throws AppException
    {    	
      CoreComment comment = new CoreComment();
  	  comment._sourceJson = json;
  	  
  	  try
  	  {
  		  JSONObject jo = new JSONObject(json);
  		  comment.CommentKey = jo.getInt("CommentKey");
  		  comment.CommentTypeID = jo.getInt("CommentTypeID");
  		  comment.CommentType = jo.getString("CommentType");
  		  comment.CommentDate = DateHelper.StringToDate(jo.getString("CommentDate"), "MM/dd/yyyy");
  		  comment.CommentBody = jo.getString("CommentBody");  
  		  comment.CommentColor = jo.getString("CommentColor");  	    
  	  }
  	  catch (JSONException e) {
			
			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
								ExceptionInfo.TYPE.UNEXPECTED,
							  	ExceptionInfo.SEVERITY.MODERATE, 
							  	"100", 
							  	"CoreComment.factory", 
							  	"Error creating object.");
			
		    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed

		    throw AppException.AppExceptionFactory(e, i); 
  	  }	
  	  return comment;		
    }
    
    // Factory Method for CorePagedResult of CoreCommentSummary objects
    public static CorePagedResult<List<CoreComment>> GetCoreCommentPagedResult(String json) throws AppException
    { 
    	CorePagedResult<List<CoreComment>> results = new CorePagedResult<List<CoreComment>>(json);
  	  	
  	  	try {  	  		
  	  		JSONObject jo = new JSONObject(json);
  	  	 
  	  		results.PageCount = jo.getInt("PageCount");
  	  		results.PageIndex = jo.getInt("PageIndex");
  	  		results.PageSize = jo.getInt("PageSize");
  	  		
  	  		//Page (list of CoreCommentSummary object)  	  		
  	  		JSONArray ja = jo.getJSONArray("Page");
  	  		results.Page = new ArrayList<CoreComment>();  	  	
  	  		
  	  		for (int i = 0; i < ja.length(); i++) {
  	  			JSONObject cs = ja.getJSONObject(i);  	  			
  	  			results.Page.add(CoreComment.GetCoreComment(cs.toString()));  	  			
  	  		}   	  		  	  	
	    }
		catch (JSONException e) {
			
			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
								ExceptionInfo.TYPE.UNEXPECTED,
							  	ExceptionInfo.SEVERITY.MODERATE, 
							  	"100", 
							  	"CoreComment.factory", 
							  	"Error creating object.");
			
		    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed
	
		    throw AppException.AppExceptionFactory(e, i); 
		}	
  	  	return results;
    }
     
}
