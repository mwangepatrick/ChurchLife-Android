package com.acstech.churchlife.exceptionhandling;


import com.acstech.churchlife.ChurchLifeDialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class ExceptionHelper {

	//TODO need to use the usererror and set the error description to the technical error and change to display
	//  the user error but to log the technical error
	
	public final static String ERROR_TAG = "ChurchLife";
			
	public static void notifyUsers(Throwable e, Context context){

		if(e instanceof AppException) {
		    
		    AppException ae = (AppException) e;

		    if(ae.getErrorType() == ExceptionInfo.TYPE.VALIDATION) {
		    	
		    	// The error was caused by the user / client,
		    	// show the cause and how to correct it.
		    	ShowAlert(context, ae.extractErrorDescription());
		    } 
		    else if(ae.getErrorType() == ExceptionInfo.TYPE.APPLICATION) {
		    	
		    	// The error was not caused by user / client.
		    	// Just show a standard error message.
		    	ShowAlert(context, ae.extractErrorDescription());			    			    			    	
		    }		  		 
		    else if(ae.getErrorType() == ExceptionInfo.TYPE.NOCONNECTION) {
		    	
		    	// Show connection error dialog box
		    	final ChurchLifeDialog dialog = new ChurchLifeDialog(context);
		    	dialog.show();
		    }
		    else if(ae.getErrorType() == ExceptionInfo.TYPE.UNEXPECTED) {

		    	String msg = String.format("An unexpected error has occurred.  The error is: %s", ae.extractErrorDescription());		    	
		    	ShowAlert(context, msg);		    			    			    	
		    }		 
		}
		else {  
			//An unknown exception occurred. Show a standard error message.
			//zzz
			ShowAlert(context, e.getMessage());
		}
	}

	public static void ShowAlert(Context context, String message) {
	
    	//Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
    	builder.setMessage(message);    	
    	/* Typically these alerts do not have a close button but the user
    	   uses the hard back button to close.
    	       	
    	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {	    	
    	      public void onClick(DialogInterface dialog, int which) {
    	       // nothing to do    		    	 	    	
    	    } });
    	 */ 
    	builder.create().show();   
	}
	
	
	
	public static void notifyNonUsers(Throwable e){

		try {		
			if(e instanceof AppException) {

			    AppException ae = (AppException) e;

			    if(ae.getErrorType() == ExceptionInfo.TYPE.VALIDATION) {

			      // The error was caused by the user / client.
			      // It may not be necessary to log it, or just log
			      // it as info or warning. The application is working 
			      // fine.  It is the user that is malfunctioning.

			    } else if(ae.getErrorType() == ExceptionInfo.TYPE.APPLICATION) {

			      // The error was caused by a failure in an external service.
			      // Notify operations about the external failure (log it).
			      // Also tell if this application will survive the failure,
			      // and continue working once the external service works again.		    	
			    	e.printStackTrace();
			    	Log.e(ERROR_TAG, ae.toXmlString());
			    	
			    } else if(ae.getErrorType() == ExceptionInfo.TYPE.UNEXPECTED) {

			       // An internal error occurred. This is serious, and may need
			       // both operations and developers attention, as it may be a
			       // sign of a bug in the application. Log the error.		    	
			    	Log.e(ERROR_TAG, ae.toXmlString());
			    	e.printStackTrace();
			    }

			} else {

				  // The exception was not an AppException. This should be fixed,
				  // so that exception is caught and wrapped in an AppException
				  // in the future. Log the error.

			      // This kind of error is also serious, and should get the attention
				  // of both operations and developers, as the cause of the exception
				  // is unknown.
				  Log.e(ERROR_TAG, e.getMessage(), e);
				  e.printStackTrace();

			}																					
		}
		catch (Exception unexpected) {
			// Most likely there was an error attempting to log the incoming error.  This
			//   could be due to the xml parse or the log not being available, etc.  
			Log.e(ERROR_TAG, e.getMessage(), e);	//original error - just try to get something from it!
			Log.e(ERROR_TAG, unexpected.getMessage(), unexpected);
			e.printStackTrace();
		}		  		  		  
	}
	
	// When a thread throws an exception, it wraps it in a bundle to send via the message handler
	//   This helper creates the bundle for that purpose
	public static Bundle getBundleForException(Throwable e) {
		Bundle b = new Bundle();
		
		if(e instanceof AppException)  {
			b.putString("exceptiontype", ((AppException)e).getErrorType().toString());
			b.putString("exceptionseverity", ((AppException)e).getErrorSeverity().toString());
			b.putString("exceptionmessage", ((AppException)e).extractErrorDescription());
		}
		else {
			String returnMessage = String.format("An unexpected error has occurred while performing this operation.  The error is %s.", e.getMessage());					    				    				    				    				    	
			
			b.putString("exceptiontype", ExceptionInfo.TYPE.UNEXPECTED.toString());
			b.putString("exceptionseverity", ExceptionInfo.SEVERITY.CRITICAL.toString());
			b.putString("exceptionmessage", returnMessage);
		}
		
		return b;		
	}
	
	public static AppException getAppExceptionFromBundle(Bundle b, String source) {

		ExceptionInfo.SEVERITY s = ExceptionInfo.SEVERITY.valueOf(b.getString("exceptionseverity"));
		ExceptionInfo.TYPE t = ExceptionInfo.TYPE.valueOf(b.getString("exceptiontype"));
		String errormsg = b.getString("exceptionmessage");
			
		return AppException.AppExceptionFactory(t, s, "100", source, errormsg);   			
	}
}
