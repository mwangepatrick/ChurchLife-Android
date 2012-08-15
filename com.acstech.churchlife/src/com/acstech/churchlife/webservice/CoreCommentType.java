package com.acstech.churchlife.webservice;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;

public class CoreCommentType {
	  public int CommentTypeID;
      public String CommentTypeDesc;     
      public String CommentColor;
      
      private String _sourceJson;
      public String toJsonString()
  	  {
    	  return _sourceJson;		
  	  }
      
      public String toString()
  	  {
    	  return CommentTypeDesc;		
  	  }
      
      // Factory Method - parse json
      public static CoreCommentType GetCoreCommentType(String json) throws AppException
      {    	
    	  CoreCommentType ct = new CoreCommentType();
    	  ct._sourceJson = json;
    	  
    	  try
    	  {
    		  JSONObject jo = new JSONObject(json);
    		  
    		  ct.CommentTypeID = jo.getInt("CommentTypeID");
    		  ct.CommentTypeDesc = jo.getString("CommentTypeDesc");
    		  ct.CommentColor = jo.getString("CommentColor");  	    
    	  }
    	  catch (JSONException e) {
  			
  			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
  								ExceptionInfo.TYPE.UNEXPECTED,
  							  	ExceptionInfo.SEVERITY.MODERATE, 
  							  	"100", 
  							  	"CoreCommentType.factory", 
  							  	"Error creating object.");
  			
  		    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed

  		    throw AppException.AppExceptionFactory(e, i); 
    	  }	
    	  return ct;		
      }
      
      
      // Factory Method for list of CoreCommentType objects
      public static List<CoreCommentType> GetCoreCommentTypeList(String json) throws AppException
      { 
    	  List<CoreCommentType> types = new ArrayList<CoreCommentType>();
    	  
    	  try {
    		  JSONArray ja = new JSONArray(json);    		    		 
    		  for (int i = 0; i < ja.length(); i++) {
    			  types.add(CoreCommentType.GetCoreCommentType(ja.getJSONObject(i).toString()));
    		  }    		      		 
	      }
		  catch (JSONException e) {
			
			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
								ExceptionInfo.TYPE.UNEXPECTED,
							  	ExceptionInfo.SEVERITY.MODERATE, 
							  	"100", 
							  	"CoreCommentType.factory", 
							  	"Error creating object.");
			
		    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed
	
		    throw AppException.AppExceptionFactory(e, i); 
		  }	
    	  return types;
      }
      
}
