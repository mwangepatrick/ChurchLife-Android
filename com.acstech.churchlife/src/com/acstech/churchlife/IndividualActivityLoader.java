package com.acstech.churchlife;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.acstech.churchlife.exceptionhandling.ExceptionHelper;
import com.acstech.churchlife.webservice.IndividualResponse;
import com.acstech.churchlife.webservice.WebServiceHandler;

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
    				_progressDialog.dismiss();
    				
	    			if (msg.what == 0) {	        	
	    				
	    				// individual found.  load the activity to view details
	           	 		Intent intent = new Intent();
	           	 		intent.setClass(_context, IndividualActivity.class); 		           	 	
	           	 		intent.putExtra("individual", msg.getData().getString("individual"));
	           	 		_context.startActivity(intent);
	           	 			           	 			           	 
	           	 		if (postRun != null) {
	           	 			postRun.run();	
	           	 		}
	           	 		
	       			}
	       			else if (msg.what < 0) {	       				
	       				// If < 0, the exception is in the message bundle.  Throw it
	       				Bundle b = msg.getData();
	       				throw ExceptionHelper.getAppExceptionFromBundle(b, "loadIndividualWithProgressWindow.handleMessage");	    	
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
    				GlobalState gs = GlobalState.getInstance(); 
    				
	    			AppPreferences appPrefs =  new AppPreferences(_context.getApplicationContext());
	    			
	    			WebServiceHandler wh = new WebServiceHandler(appPrefs.getWebServiceUrl(), config.APPLICATION_ID_VALUE);
	    	    	IndividualResponse i = wh.getIndividual(gs.getUserName(), gs.getPassword(), gs.getSiteNumber(), individualId);
	    	    	
	    	    	// Return the response object (as string) to the message handler above
	    	    	Message msg = handler.obtainMessage();	
	    	    	msg.what = 0;
	    	    	
	    	    	Bundle b = new Bundle();
    				b.putString("individual", i.toString());
    				msg.setData(b);
    						    	    		    	    			    	  
	    	    	handler.sendMessage(msg);
    			}
    			catch (Exception e) {    				
    				ExceptionHelper.notifyNonUsers(e);			// Log the full error, 
    				    				
    				Message msg = handler.obtainMessage();
    				msg.what = -1;
    				msg.setData(ExceptionHelper.getBundleForException(e));	
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
