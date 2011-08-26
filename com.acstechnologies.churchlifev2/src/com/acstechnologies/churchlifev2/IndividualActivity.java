package com.acstechnologies.churchlifev2;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.acstechnologies.churchlifev2.exceptionhandling.AppException;
import com.acstechnologies.churchlifev2.exceptionhandling.ExceptionHelper;
import com.acstechnologies.churchlifev2.exceptionhandling.ExceptionInfo;
import com.acstechnologies.churchlifev2.webservice.IndividualPhone;
import com.acstechnologies.churchlifev2.webservice.IndividualResponse;
import com.acstechnologies.churchlifev2.webservice.WebServiceHandler;

/**
 * This activity displays individual information for the passed in individual and allows the user
 *   to interact with that data (call phone, email, add to contacts, etc.) 
 * 
 * NOTE:  The bundle passed to this activity MUST contain a IndividualResponse object (as a string)
 * 
 * @author softwarearchitect
 *
 */
public class IndividualActivity extends OptionsActivity {

	static final int DIALOG_PROGRESS = 0;
	private ProgressDialog _progressD;
	
	IndividualResponse _wsIndividual;					// results of the web service call
	AppPreferences _appPrefs;  	
	
	ImageView individualImageView;						// form controls
	TextView nameTextView;
	ListView emailListView;
	ListView phoneListView;
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        try
	        {        	
	        	 _appPrefs = new AppPreferences(getApplicationContext());
	        	 
	        	 setContentView(R.layout.individual);
	        	 
	        	 bindControls();											// Set state variables to their form controls	        	 	       
	        	 
	        	 // This activity MUST be passed the individual object (as json string)
	        	 Bundle extraBundle = this.getIntent().getExtras();
	             if (extraBundle == null) {
	            	 throw AppException.AppExceptionFactory(
	            			 ExceptionInfo.TYPE.UNEXPECTED,
							 ExceptionInfo.SEVERITY.CRITICAL, 
							 "100",           												    
							 "IndividualActivity.onCreate",
							 "No individual data was passed to the Individual activity.");
	             }
	             else {
		        	 //int individualId = extraBundle.getInt("id");
	            	 _wsIndividual = new IndividualResponse(extraBundle.getString("individual"));
	            	 bindData();
	            	 
		        	 //getIndividualWithProgressWindow(individualId);			// Retrieve individual info on background thread
	             }	        	 
	        }
	    	catch (Exception e) {
	    		ExceptionHelper.notifyUsers(e, IndividualActivity.this);
	    		ExceptionHelper.notifyNonUsers(e);
	    	}  	        
	    }

	 protected Dialog onCreateDialog(int id) {
	        switch(id) {
	        case DIALOG_PROGRESS:
	        	_progressD = new ProgressDialog(IndividualActivity.this);
	        	_progressD.setMessage(getString(R.string.IndividualList_ProgressDialog));
	        	_progressD.setIndeterminate(true);
	        	_progressD.setCancelable(false);
	    		return _progressD;	  
	        default:
	            return null;
	        }
	    }


	  // Instantiating the Handler associated with the main thread
	  private Handler messageHandler = new Handler() {
		  
		  @Override
		  public void handleMessage(Message msg) {  
			  switch(msg.what) {
			  case 1:
				  String s = "12123"; //Toast.makeText(this, "test", 100);
			  case 0:
				  String y = "123123";
			  }
		  }
	   };
	    
	    /**
	     *  Links state variables to their respective form controls
	     */
	    private void bindControls(){	    	
	    	individualImageView = (ImageView)this.findViewById(R.id.individualImageView);
	    	nameTextView = (TextView)this.findViewById(R.id.nameTextView);
	    	emailListView = (ListView)this.findViewById(R.id.emailListview);
	    	phoneListView = (ListView)this.findViewById(R.id.phoneListview);
	    }
	    
	    /**
	     *  Sets the control values to the individual record that was passed to this activity.
	     *  
	     * @throws AppException
	     */
	    private void bindData() throws AppException {
	    	
	    	// image
	    	Drawable image = ImageOperations(_wsIndividual.getPictureUrl());
	    	if (image != null) {
	    		individualImageView.setImageDrawable(image);	    	        
	    	}
	    	
	    	// name											
			nameTextView.setText(_wsIndividual.getFirstName() + " " + _wsIndividual.getLastName());
			
			// emails
			EmailAddressListAdapter ea = new EmailAddressListAdapter(this, _wsIndividual.getEmails());
			emailListView.setAdapter(ea);
								
			// addresses
			
			// family members
			
			// phone numbers						
	        PhoneNumberListAdapter ca = new PhoneNumberListAdapter(this,  _wsIndividual.getPhoneNumbers());
	        phoneListView.setAdapter(ca);
	        
	        
			
			
	    }
	    
	    
	    /**
	     * gets a drawable for a given url
	     * @param url
	     * @return
	     */
	    private Drawable ImageOperations(String url) {
	    	Drawable d = null;
	    	try {	    		
	    		if (url.length() > 0) {
	    			URL imageUrl = new URL(url);
	    			InputStream is = (InputStream) imageUrl.getContent();
	    			d = Drawable.createFromStream(is, "src");
	    		}
	    	   return d;	    	   
	    	} 
	    	catch (MalformedURLException e) {
	    		ExceptionHelper.notifyNonUsers(e);
	    		return null;
	    	} catch (IOException e) {
	    		ExceptionHelper.notifyNonUsers(e);
	    		return null;
	    	}
	    }
	    	    
		  
	    /**
	     * Displays a progress dialog and launches a background thread to connect to a web service
	     *   to retrieve data for an individual 
	     *   
	     */
	    private void getIndividualWithProgressWindow(final int individualId)
	    {           	      			    
		    showDialog(DIALOG_PROGRESS);
		    	
		    /*
		    // This handler is called once the people search is complete.  It looks at the data returned
		    //  from the thread (in the Message) to determine success/failure.  If successful, the results are displayed.
		    final Handler handler = new Handler() {
		    	public void handleMessage(Message msg) {
		    		  
		    		removeDialog(DIALOG_PROGRESS);
	    			
	    			try {
		    			if (msg.what == 0) {	        			
		       	           	// Person retrieved, set form control values
		    				//individualImageView.setImageURI()   //may need to use individualImageView.setImageDrawable(drawable)
		    				Uri u = Uri.parse(_wsIndividual.getPictureUrl());		    						    				
		    				individualImageView.setImageURI(u);
		    				
		    				nameTextView.setText(_wsIndividual.getFirstName() + " " + _wsIndividual.getLastName());
		    						    						    			
		       			}
		       			else if (msg.what < 0) {
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
	    				ExceptionHelper.notifyUsers(e, IndividualActivity.this);
	    	    		ExceptionHelper.notifyNonUsers(e)  ; 				    				
	    			}    			    			    			    			   				    			    		  
	    		}
	    	};
	    	*/
		    
		    
	    	Thread searchThread = new Thread() {  
	    		public void run() {
	    			try {
		    			GlobalState gs = (GlobalState) getApplication();
		    			
		    	    	WebServiceHandler wh = new WebServiceHandler(_appPrefs.getWebServiceUrl());
		    	    	_wsIndividual = wh.getIndividual(gs.getUserName(), gs.getPassword(), gs.getSiteNumber(), individualId);
		    	    			    	    	
		    	    	Runnable r = new Runnable() {
		    	    		@Override
		    	    		public void run() {
		    	    			removeDialog(DIALOG_PROGRESS);		    	    			
		    	    			try
		    	    			{
			    	    			Uri u = Uri.parse(_wsIndividual.getPictureUrl());		    						    				
				    				individualImageView.setImageURI(u);
				    				
				    				nameTextView.setText(_wsIndividual.getFirstName() + " " + _wsIndividual.getLastName());
		    	    			}
		    	    			catch (Exception e) {
		    	    				ExceptionHelper.notifyUsers(e, IndividualActivity.this);
		    	    	    		ExceptionHelper.notifyNonUsers(e); 
		    	    			}
	    	    		    }
		    	    	 };
		    	    	
		    	    	messageHandler.post(r);
		    	    	
		    	    	//handler.sendEmptyMessage(0);
	    			}
	    			catch (Exception e) {    				
	    				ExceptionHelper.notifyNonUsers(e);			// Log the full error, 
	    				
	    				//obtain message and add exception to it?  test
	    				
	    				Message msg = messageHandler.obtainMessage();		// return only the exception string as part of the message
	    				msg.what = -1;
	    				//TODO:  revisit - this could bubble up info to the user that they don't need to see or won't understand.
	    				//  use ExceptionHelper to get a string to show the user based on the exception type/severity, etc.
	    				//  if appexception and not critical, return -1, ...if critical return -2, etc.
	    				
	    				String returnMessage = String.format("An unexpected error has occurred while performing this search.  The error is %s.", e.getMessage());					    				    				    				    				    	
	    				Bundle b = new Bundle();
	    				b.putString("Exception", returnMessage);
	    				
	    				messageHandler.sendMessage(msg);    				
	    			}    			       	    	    	    	
	    		 }
	    	};
	    	searchThread.start();    	    	
	    }
	    
	 
}
