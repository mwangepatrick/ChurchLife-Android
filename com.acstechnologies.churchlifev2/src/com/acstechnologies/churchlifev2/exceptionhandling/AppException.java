package com.acstechnologies.churchlifev2.exceptionhandling;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


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
	  
	  /**
	   *  Wraps the entire error in an XML string that can be logged when an error occurs.
	   *  
	   *  NOTE:  Do NOT throw an AppException here as we are handling one and could easily
	   *          get into an infinitie loop.
	   *  See
	   *  http://tutorials.jenkov.com/exception-handling-strategies/template-error-info-list.html
	   */
	  public String toXmlString() throws Exception
	  {
		  String returnValue = "";
		  
		  DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		  DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	
		  Document doc = docBuilder.newDocument();
		  Element rootElement = doc.createElement("error");
		  doc.appendChild(rootElement);
		 
		  // error elements
		  Element errorId = doc.createElement("errorId");
		  errorId.appendChild(doc.createTextNode(this.extractErrorId()));
		  rootElement.appendChild(errorId);
		 
		  Element errorType = doc.createElement("errorType");
		  errorType.appendChild(doc.createTextNode(this.getErrorType().name()));	
		  rootElement.appendChild(errorType);
			  
		  Element severity = doc.createElement("severity");			 
		  severity.appendChild(doc.createTextNode(this.getErrorSeverity().name()));	
		  rootElement.appendChild(severity);			 
			  
		  // Error List
		  Element errorList = doc.createElement("errorInfoList");
		  rootElement.appendChild(errorList);

		  for(int i=this.getErrorInfoList().size()-1; i>=0; i--){
			  
		    	ExceptionInfo errorInfo = this.getErrorInfoList().get(i);
		    	Element elementInfo = doc.createElement("errorInfo");
			   	errorList.appendChild(elementInfo);
			    				    	
			   	// Description
			   	Element desc = doc.createElement("errordescription");			
			   	if (errorInfo.getErrorDescription() != null) {
			   		desc.appendChild(doc.createTextNode(errorInfo.getErrorDescription()));
			   	}			    	
			   	elementInfo.appendChild(desc);
					  			    	
			   	// Parameters
			   	Element parms = doc.createElement("parameters");			    	
			   	elementInfo.appendChild(parms);
				  			    				    				    				    						    
			    for (Map.Entry<String, Object> entry : errorInfo.parameters.entrySet()) {
			        String key = entry.getKey();
			        Object value = entry.getValue();
			    		    
				   	Element parameter = doc.createElement("parameter");
			    	parameter.setAttribute("name", key);
			    	parameter.appendChild(doc.createTextNode(value.toString()));				    	
			    	parms.appendChild(parameter);				    					    		    
			   	}			    				    				    	
		  }
				
		  // return the xml DOM as a String 
          TransformerFactory factory = TransformerFactory.newInstance();
          Transformer transformer = factory.newTransformer();
          DOMSource source = new DOMSource(doc);
          StreamResult result = new StreamResult(new StringWriter());
          transformer.transform(source, result);
		  
          returnValue = result.getWriter().toString();

          // Can throw one of the following:
		  //ParserConfigurationException 
		  //TransformerConfigurationException
          //TransformerException
			  
		  return returnValue;
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



