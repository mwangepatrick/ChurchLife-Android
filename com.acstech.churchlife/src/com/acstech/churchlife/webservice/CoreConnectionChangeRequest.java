package com.acstech.churchlife.webservice;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;

public class CoreConnectionChangeRequest extends CoreObject {

	public int ConnectionId;
	public boolean Complete;
	public Date ConnectionDate;
	public int ConnectionTypeId;
	public boolean FamilyConnection;
	public int  ContactIndvId;
	public int OpenCategoryId;
	public boolean Reassign;
	public int NewCallerIndvId;
	public int NewTeamId;
	public String Comment;
	public List<Integer> ResponseIdList;
	
	public String getConnectionDateString() {
    	String DATE_FORMAT = "MM/dd/yyyy";		
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);		
		return sdf.format(ConnectionDate);
    }
	
    @Override
    public JSONObject toJsonObject() throws AppException
    {
    	JSONObject jo = null;
    	try
    	{
	  	  	jo = new JSONObject();
	  	  	
	  	  	jo.put("ConnectionId", ConnectionId);
	  	  	jo.put("Complete", Complete);
	  	  	jo.put("ConnectionDate", getConnectionDateString());
	  	  	jo.put("ConnectionTypeId", ConnectionTypeId);
	  		jo.put("FamilyConnection", FamilyConnection);
	  		jo.put("ContactIndvId", ContactIndvId);
	  		jo.put("OpenCategoryId", OpenCategoryId);
	  		jo.put("Reassign", Reassign);
	  		jo.put("NewCallerIndvId", NewCallerIndvId);
	  		jo.put("NewTeamId", NewTeamId);
	  		jo.put("Comment", Comment);	  		
	  		
	  		// to keep from being enquoted, use JSONArray
	  		//
	  		// ex.  was this:  "ResponseIdList", "[1,2,3]"
	  		//      now this:  "ResponseIdList", [1,2,3]
	  		JSONArray responses = new JSONArray();
	  		for (int i : ResponseIdList) {
	  			responses.put(i);
    		}
	  		jo.put("ResponseIdList", responses);
    		
    	}
    	catch (JSONException e) {  		
  			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
  								ExceptionInfo.TYPE.UNEXPECTED,
  							  	ExceptionInfo.SEVERITY.MODERATE, 
  							  	"100", 
  							  	"CoreConnectionChangeRequest.toJsonObject", 
  							  	"Error creating JSONObject.");

  		    throw AppException.AppExceptionFactory(e, i); 
    	}
    	return jo;
    }
    
    
    //zzz not used?
    // Factory Method - parse json
    public static CoreConnectionChangeRequest GetCoreConnectionChangeRequest(String json) throws AppException
    {    	
    	
      CoreConnectionChangeRequest connection = new CoreConnectionChangeRequest();
      connection._sourceJson = json;
  	  
  	  try
  	  {  		    		 
  		  JSONObject jo = new JSONObject(json);
  		  /*
  		  comment.IndvID = jo.getInt("IndvID");
  		  comment.CommentTypeId = jo.getInt("CommentTypeId");
  		  comment.CommentType = jo.getString("CommentType");
  		  comment.Comment = jo.getString("Comment");  
  		  comment.FamilyComment = jo.getInt("FamilyComment");
  		  */  	    
  	  }
  	  catch (JSONException e) {
			
			ExceptionInfo i = ExceptionInfo.ExceptionInfoFactory(
								ExceptionInfo.TYPE.UNEXPECTED,
							  	ExceptionInfo.SEVERITY.MODERATE, 
							  	"100", 
							  	"CoreConnectionChangeRequest.factory", 
							  	"Error creating object.");
			
		    i.getParameters().put("json", json);	//for logging, add the json that was to be parsed

		    throw AppException.AppExceptionFactory(e, i); 
  	  }	
  	  return connection;		
    }
    
}
