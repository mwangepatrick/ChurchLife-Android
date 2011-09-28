package com.acstechnologies.churchlifev2;

import com.acstechnologies.churchlifev2.R;
import com.acstechnologies.churchlifev2.exceptionhandling.AppException;
import com.acstechnologies.churchlifev2.exceptionhandling.ExceptionHelper;
import com.acstechnologies.churchlifev2.webservice.LoginResponse;
import com.acstechnologies.churchlifev2.webservice.WebServiceHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class SplashActivity extends Activity {
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);       
                       
        new AutoLoginTask().execute();	// login in background                               
    }
        

    /**
     *  Executes on a background thread.  If error, log the message and
     *    route the user to the login page.
     *    
     * @author softwarearchitect
     *
     */
    private class AutoLoginTask extends AsyncTask<Void, Void, Boolean> {
      	
     	private Exception e = null;
        	
        protected Boolean doInBackground(Void... args) {
          	Boolean result = false;
            try {
               	result = autoLoginIn();
            }
            catch (Exception e) {
               this.e = e;
            }                
            return result;
        }
            
        // Executes on the UI thread           
        protected void onPostExecute(Boolean result) {
            	
          	Intent nextIntent = new Intent(SplashActivity.this, LoginActivity.class);
            	
           	if (e == null) {			// no error
           		if (result == true)  	// if successful login, change the activity to which to navigate	        					
    				nextIntent = new Intent(SplashActivity.this, MainActivity.class); 
           	}
           	else {           		
           		ExceptionHelper.notifyNonUsers(e);
           		ExceptionHelper.notifyUsers(e, SplashActivity.this);           		
           	}      
           	
       		SplashActivity.this.startActivity(nextIntent);
       		SplashActivity.this.finish();  
       		
        }
    }

        
    /** A user can choose be automatically logged in (or attempt to)
     *   if they have saved credentials in their preference
     *   
     * @return true if login was successful, false otherwise
     * @throws Exception
    */   
    private Boolean autoLoginIn() throws AppException
    {
    	Boolean result = false;        	        	
       	AppPreferences prefs = new AppPreferences(getApplicationContext());  //Initialize 
        		 
       	// get values from preferences and attempt a login
       	String auth1 = prefs.getAuth1();  
        String auth2 = prefs.getAuth2();
        String auth3 = prefs.getAuth3();
           	            
        if (auth1.length() > 0 && auth2.length() > 0){
            	
         	WebServiceHandler wh = new WebServiceHandler(prefs.getWebServiceUrl(), config.APPLICATION_ID_VALUE);    		            		
           	LoginResponse response = wh.login(auth1, auth2, auth3);            	
           	result = (response.getStatusCode() == 0);
           	
           	if (result == true) {
        		// set global application variables	    				
				GlobalState gs = (GlobalState) getApplication();
				gs.setSiteName(response.getSiteName());
				gs.setSiteNumber(response.getSiteNumber());
				gs.setUserName(response.getUserName());
				gs.setPassword(auth2);
         	}           	
        }         	
       	return result;
    }   
    
}
