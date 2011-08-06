package com.acstechnologies.churchlifev2.exceptionhandling;

import java.util.HashMap;
import java.util.Map;


public class ExceptionInfo {

	public enum SEVERITY {
		LOW,MODERATE,HIGH,CRITICAL		
	}
	
	public enum TYPE {
		UNEXPECTED,APPLICATION,VALIDATION		
	}
		
	protected Throwable cause                = null;
    protected String    errorId              = null;
    protected String    contextId            = null;
    protected TYPE      errorType            = null;
    protected SEVERITY  severity             = null;

    protected String    userErrorDescription = null;
    protected String    errorDescription     = null;
    protected String    errorCorrection      = null;

    protected Map<String, Object> parameters = new HashMap<String, Object>();
       
	public void setCause(Throwable e) {
		cause = e;		
	}

	// error id
	public void setErrorId(String input) {
		errorId = input;		
	}	
	public Object getErrorId() {		
		return errorId;
	}

	// context id
	public void setContextId(String input) {		
		contextId = input;		
	}
	public Object getContextId() {	
		return contextId;
	}
	
	// error type
	public void setErrorType(TYPE input) {
		errorType = input;		
	}
	public TYPE getErrorType() {	
		return errorType;
	}
	
	// severity
	public void setSeverity(SEVERITY input) {
		severity = input;		
	}	
	public SEVERITY getErrorServirty() {
		return severity;
	}
	
	
	// error description
	public void setErrorDescription(String input) {
		errorDescription = input;		
	}
	public String getErrorDescription() {
		return errorDescription;		
	}
	
	
	// user error description
	public void setUserErrorDescription(String input) {
		userErrorDescription = input;	
	}
	public String getUserErrorDescription() {
		return userErrorDescription;		
	}
	
	
	// correction message (if applicable)
	public void setErrorCorrection(String string) {
		errorCorrection = string;		
	}
	public String getErrorCorrection() {
		return errorCorrection;
	}

	
	public Map<String, Object> getParameters() {
		return parameters;
	}

	// Factory Methods
	public static ExceptionInfo ExceptionInfoFactory(TYPE t,
												   	 SEVERITY s, 	
												   	 String id,
												     String contextId,
												     String description) {
		ExceptionInfo info = new ExceptionInfo();	
		info.setErrorType(t);
		info.setErrorId(id);
		info.setSeverity(s);			
		info.setContextId(contextId);
		info.setErrorDescription(description);
		return info;		
	}
  
}
