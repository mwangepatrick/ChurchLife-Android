package com.acstechnologies.churchlifev2.exceptionhandling;

import java.util.ArrayList;
import java.util.List;

import com.acstechnologies.churchlifev2.exceptionhandling.ExceptionInfo.SEVERITY_ERROR;
import com.acstechnologies.churchlifev2.exceptionhandling.ExceptionInfo.TYPE_ERROR;

//http://tutorials.jenkov.com/exception-handling-strategies/template-propagating.html
public class AppException extends Exception {

	 private static final long serialVersionUID = 1L;
	 
	  protected List<ExceptionInfo> errorInfoList = new ArrayList<ExceptionInfo>();

	  public AppException() {
	  }

	  public ExceptionInfo addInfo(ExceptionInfo info){
	    this.errorInfoList.add(info);
	    return info;
	  }

	  public ExceptionInfo addInfo(){
		  ExceptionInfo info = new ExceptionInfo();
	    this.errorInfoList.add(info);
	    return info;
	  }

	  public List<ExceptionInfo> getErrorInfoList() {
	    return errorInfoList;
	  }

	  public TYPE_ERROR getErrorType() {
		  
		  ExceptionInfo.TYPE_ERROR errorType = ExceptionInfo.TYPE_ERROR.UNEXPECTED;

		  for(ExceptionInfo errorInfo : this.getErrorInfoList()){

		      if(errorInfo.getErrorType() != errorType) {
		          errorType = errorInfo.getErrorType();
		      }
		  }

		  return errorType;
	  }
	  
	  public SEVERITY_ERROR getErrorSeverity() {
		  
		  ExceptionInfo.SEVERITY_ERROR severity = ExceptionInfo.SEVERITY_ERROR.CRITICAL;

		  for(ExceptionInfo errorInfo : this.getErrorInfoList()){

		      if(errorInfo.getErrorServirty() != severity) {
		    	  severity = errorInfo.getErrorServirty();
		      }
		  }

		  return severity;
	  }	
	  
	  public String extractErrorId(){
		    StringBuilder builder = new StringBuilder();

		    for(int i=this.getErrorInfoList().size()-1; i>=0; i--){

		    	ExceptionInfo errorInfo = this.getErrorInfoList().get(i);

		        builder.append(errorInfo.getContextId());
		        builder.append(":");
		        builder.append(errorInfo.getErrorId());

		        if(i>0){
		            builder.append("/");
		        }
		    }

		    return builder.toString();
		}

	  
}



