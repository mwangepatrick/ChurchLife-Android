package com.acstechnologies.churchlifev2.exceptionhandling;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


public class ExceptionInfo {

	public enum SEVERITY_ERROR {
		LOW, MODERATE, HIGH, CRITICAL		
	}
	
	public enum TYPE_ERROR {
		UNEXPECTED, APPLICATION,VALIDATION		
	}
		
	protected Throwable 		cause                = null;
    protected String    		errorId              = null;
    protected String    		contextId            = null;
    protected TYPE_ERROR       	errorType            = null;
    protected SEVERITY_ERROR    severity             = null;

    protected String    userErrorDescription = null;
    protected String    errorDescription     = null;
    protected String    errorCorrection      = null;

    protected Map<String, Object> parameters =
            new HashMap<String, Object>();
       
	public void setCause(UnsupportedEncodingException e) {
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
	public void setErrorType(TYPE_ERROR input) {
		errorType = input;		
	}
	public TYPE_ERROR getErrorType() {	
		return errorType;
	}
	
	// severity
	public void setSeverity(SEVERITY_ERROR input) {
		severity = input;		
	}	
	public SEVERITY_ERROR getErrorServirty() {
		return severity;
	}
	
	// error description
	public void setErrorDescription(String input) {
		errorDescription = input;
		
	}
	public String getErrorDescription() {
		return errorDescription;		
	}


    
}
