package com.acstechnologies.churchlifev2.exceptionhandling;

import java.util.ArrayList;
import java.util.List;

import com.acstechnologies.churchlifev2.exceptionhandling.ExceptionInfo.SEVERITY;
import com.acstechnologies.churchlifev2.exceptionhandling.ExceptionInfo.TYPE;

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

	
	  public TYPE getErrorType() {
		  
		  ExceptionInfo.TYPE errorType = ExceptionInfo.TYPE.UNEXPECTED;

		  for(ExceptionInfo errorInfo : this.getErrorInfoList()){

		      if(errorInfo.getErrorType() != errorType && errorInfo.getErrorType() != null) {
		          errorType = errorInfo.getErrorType();
		      }
		  }

		  return errorType;
	  }
	  
	  public SEVERITY getErrorSeverity() {
		  
		  ExceptionInfo.SEVERITY severity = ExceptionInfo.SEVERITY.CRITICAL;

		  for(ExceptionInfo errorInfo : this.getErrorInfoList()){

		      if(errorInfo.getErrorServirty() != severity && errorInfo.getErrorServirty() != null) {
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


	  /**
	   *  
	   *   
	   * @return  A period (.) delimited list of error descriptions for all ExceptionInfo
	   *          objects in the list
	   */
	  public String extractErrorDescription(){
			
		  StringBuilder builder = new StringBuilder();

		    for(int i=this.getErrorInfoList().size()-1; i>=0; i--){

		    	ExceptionInfo errorInfo = this.getErrorInfoList().get(i);

		    	if (errorInfo.getErrorDescription() != null && errorInfo.getErrorDescription().length() > 0)
		    	{
		    		builder.append(errorInfo.getErrorDescription());
		        	builder.append(". ");
		    	}
		    }
		    return builder.toString();	  
	  }
	  
	  
	  	// ***  Factory Methods ***
		public static AppException AppExceptionFactory(ExceptionInfo.TYPE t,
													   ExceptionInfo.SEVERITY s, 	
													   String id,
													   String contextId,
													   String description) {		
			AppException appE = new AppException();					
			ExceptionInfo info = ExceptionInfo.ExceptionInfoFactory(t, s, id, contextId, description);					
			appE.addInfo(info);
			
			return appE;					
		}
	  	  	  	 
		public static AppException AppExceptionFactory(Throwable caughtException,
													   ExceptionInfo.TYPE t,
													   ExceptionInfo.SEVERITY s,
													   String id,
													   String contextId,
													   String description) {
			
			AppException appE = new AppException();					
			ExceptionInfo info = ExceptionInfo.ExceptionInfoFactory(t, s, id, contextId, description);
			info.setCause(caughtException);
			appE.addInfo(info);
						
			return appE;
		}
		
		
		public static AppException AppExceptionFactory(Throwable caughtException, ExceptionInfo info) {
			
			AppException appE = new AppException();	
			info.setCause(caughtException);
			appE.addInfo(info);
			
			return appE;
		}
				
}



