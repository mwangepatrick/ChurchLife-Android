package com.acstechnologies.churchlifev2.exceptionhandling;

import android.content.Context;
import android.widget.Toast;


public class ExceptionHelper {

	  public static void notifyUsers(Throwable e, Context context){

		  if(e instanceof AppException) {
		    
		    AppException ae = (AppException) e;

		    if(ae.getErrorType() == ExceptionInfo.TYPE.VALIDATION) {

		    	Toast.makeText(context, ae.extractErrorDescription(),Toast.LENGTH_LONG).show();
		    	
		    	// The error was caused by the user / client,
		    	// show the cause and how to correct it.

		    } else if(ae.getErrorType() == ExceptionInfo.TYPE.APPLICATION) {

		    	//dialog box here! User must acknowledge.
		    	//FUTURE zzz
		    	Toast.makeText(context, ae.extractErrorDescription(),Toast.LENGTH_LONG).show();
		    	// The error was not caused by user / client.
		    	// Just show a standard error message.
		    	
		    }		  
		    else if(ae.getErrorType() == ExceptionInfo.TYPE.UNEXPECTED) {

		    	//dialog box here! User must acknowledge.
		    	//FUTURE zzz
		    	String msg = String.format("An unexpected error has occurred.  The error is:", ae.extractErrorDescription());
		    	Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
		    	
		
		    	
		    }
		  
		  } else {  
		     //An unknown exception occurred. Show a standard error message.
			  //zzz
			  Toast.makeText(context, e.getMessage(),Toast.LENGTH_LONG).show();
			 
		  }
		}

	  
	  public static void notifyNonUsers(Throwable e){

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
		    	//Log.e(, , ae);
		    	//Log.e("LunchList", "Exception parsing feed", e);
		    	e.printStackTrace();
		    	
		    } else if(ae.getErrorType() == ExceptionInfo.TYPE.UNEXPECTED) {

		       // An internal error occurred. This is serious, and may need
		       // both operations and developers attention, as it may be a
		       // sign of a bug in the application. Log the error.
		    	//Log.e("LunchList", "Exception parsing feed", ae);
		    	e.printStackTrace();
		    }

		  } else {

		    // The exception was not an AppException. This should be fixed,
		    // so that exception is caught and wrapped in an AppException
		    // in the future. Log the error.

		    // This kind of error is also serious, and should get the attention
		    // of both operations and developers, as the cause of the exception
		    // is unknown.
			  //Log.e("LunchList", "Exception parsing feed", e);
			  e.printStackTrace();

		  }
		}
}
