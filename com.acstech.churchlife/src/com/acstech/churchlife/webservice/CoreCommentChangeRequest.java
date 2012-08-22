package com.acstech.churchlife.webservice;

import org.json.JSONException;
import org.json.JSONObject;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;

public class CoreCommentChangeRequest extends CoreObject {

    public int IndvID;
    public String CommentType;      //this HAS to be wrong...should be int CommentTypeID 
    public String Comment;
    public int FamilyComment;
    
    
    @Override
    public JSONObject toJsonObject() throws AppException
    {
    	JSONObject jo = null;
    	try
    	{
	  	  	jo = new JSONObject();
	  	  	jo.put("IndvID", IndvID);
	  	  	jo.put("CommentType", CommentType);
	  	  	jo.put("Comment", Comment);
	  	  	jo.put("FamilyComment", FamilyComment);	  	  	
    	}
    	catch (JSONException e) {  		
  			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
  								ExceptionInfo.TYPE.UNEXPECTED,
  							  	ExceptionInfo.SEVERITY.MODERATE, 
  							  	"100", 
  							  	"CoreCommentChangeRequest.toJsonObject", 
  							  	"Error creating JSONObject.");

  		    throw AppException.AppExceptionFactory(e, i); 
    	}
    	return jo;
    }
    
    // Factory Method - parse json
    public static CoreCommentChangeRequest GetCoreCommentChangeRequest(String json) throws AppException
    {    	
      CoreCommentChangeRequest comment = new CoreCommentChangeRequest();
  	  comment._sourceJson = json;
  	  
  	  try
  	  {
  		  JSONObject jo = new JSONObject(json);
  		  comment.IndvID = jo.getInt("IndvID");
  		  comment.CommentType = jo.getString("CommentType");
  		  comment.Comment = jo.getString("Comment");  
  		  comment.FamilyComment = jo.getInt("FamilyComment");  	    
  	  }
  	  catch (JSONException e) {
			
			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
								ExceptionInfo.TYPE.UNEXPECTED,
							  	ExceptionInfo.SEVERITY.MODERATE, 
							  	"100", 
							  	"CoreCommentChangeRequest.factory", 
							  	"Error creating object.");
			
		    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed

		    throw AppException.AppExceptionFactory(e, i); 
  	  }	
  	  return comment;		
    }
    
}
