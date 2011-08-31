package com.acstechnologies.churchlifev2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.acstechnologies.churchlifev2.exceptionhandling.AppException;
import com.acstechnologies.churchlifev2.exceptionhandling.ExceptionHelper;
import com.acstechnologies.churchlifev2.exceptionhandling.ExceptionInfo;
import com.acstechnologies.churchlifev2.webservice.IndividualResponse;
import com.acstechnologies.churchlifev2.webservice.WebServiceHandler;

/**
 * Loads an individual with 'wait' window.  Starts an IndividualActivity after
 *   the data is returned from the web service.
 * 
 * Used from:  IndividualList activity  (when a person in the list is selected)
 *             Individual activity 		(when a family member is selected)
 *             
 * @author softwarearchitect
 *
 */
public class IndividualActivityLoader  {
	
	private Context _context;										//set by constructor	
	private String _dialogText = "Loading data.  Please Wait...";	//set by constructor	
	private ProgressDialog _progressDialog;  
	
	private Runnable postRun;
	
	public void setOnCompleteCallback(Runnable r) {
		postRun = r;
	}
	
	 /**
     * Displays a progress dialog and launches a background thread to connect to a web service
     *   to retrieve search results 
     *   
     */
    public void loadIndividualWithProgressWindow(final int individualId)
    {
    	_progressDialog = ProgressDialog.show(_context, "", _dialogText, true);
 	
    	// This handler is called once the individual retrieve is complete.  It looks at the data returned
    	//  from the thread (in the Message) to determine success/failure.  If successful, the individual
    	//  activity is launched.
    	final Handler handler = new Handler() {
    		public void handleMessage(Message msg) {
    			    		    		
    			try {
	    			if (msg.what == 0) {	        	
	    					    					    			
	           	 		Intent intent = new Intent();
	           	 		intent.setClass(_context, IndividualActivity.class); 		           	 	
	           	 		intent.putExtra("individual", msg.getData().getString("individual"));
	           	 		_context.startActivity(intent);
	           	 		
	           	 		_progressDialog.dismiss();
	           	 		
	           	 		if (postRun != null) {
	           	 			postRun.run();	
	           	 		}
	           	 		
	       			}
	       			else if (msg.what < 0) {

	       				_progressDialog.dismiss();
	       				
	       				// If < 0, the exception text is in the message bundle.  Throw it
	       				
	       				//TODO:
	       				//  (we should examine it to see if is is one that should be raised as critical
	       				//   or something that is just a validation message, etc.)
	       				String errMsg = msg.getData().getString("Exception");	       
	       				throw AppException.AppExceptionFactory(
	       					  ExceptionInfo.TYPE.UNEXPECTED,
	 						   ExceptionInfo.SEVERITY.CRITICAL, 
	 						   "100",           												    
	 						   "doSearchWithProgressWindow.handleMessage",
	 						   errMsg);	       				
	       			}	    				
    			}
    			catch (Exception e) {
    				ExceptionHelper.notifyUsers(e, _context);
    	    		ExceptionHelper.notifyNonUsers(e)  ; 				    				
    			}    			    			    			    			   				    			    		  
    		}
    	};
    	
    	Thread searchThread = new Thread() {  
    		public void run() {
    			try {
	    			GlobalState gs = (GlobalState) _context.getApplicationContext();
	    			AppPreferences appPrefs =  new AppPreferences(_context.getApplicationContext());
	    			
	    			WebServiceHandler wh = new WebServiceHandler(appPrefs.getWebServiceUrl());
	    	    	IndividualResponse i = wh.getIndividual(gs.getUserName(), gs.getPassword(), gs.getSiteNumber(), individualId);
	    	    	
	    	    	Message msg = handler.obtainMessage();		// return only the exception string as part of the message
	    	    	msg.what = 0;
	    	    	
	    	    	Bundle b = new Bundle();
    				b.putString("individual", i.toString());
    				msg.setData(b);
    						    	    		    	    			    	  
	    	    	handler.sendMessage(msg);
    			}
    			catch (Exception e) {    				
    				ExceptionHelper.notifyNonUsers(e);			// Log the full error, 
    				
    				Message msg = handler.obtainMessage();		// return only the exception string as part of the message
    				msg.what = -1;
    				//TODO:  revisit - this could bubble up info to the user that they don't need to see or won't understand.
    				//  use ExceptionHelper to get a string to show the user based on the exception type/severity, etc.
    				//  if appexception and not critical, return -1, ...if critical return -2, etc.
    				
    				String returnMessage = String.format("An unexpected error has occurred while performing this search.  The error is %s.", e.getMessage());					    				    				    				    				    	
    				Bundle b = new Bundle();
    				b.putString("Exception", returnMessage);
    				msg.setData(b);
    				
    				handler.sendMessage(msg);    				
    			}    			       	    	    	    	
    		 }
    	};
    	searchThread.start();        	
    }
	
		
	// Constructor
	public IndividualActivityLoader(Context con, String dialogText) {
		_dialogText = dialogText;
		_context = con;		
	}
	public IndividualActivityLoader(Context con) {
		_context = con;		
	}
	
}