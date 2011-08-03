package com.acstechnologies.churchlifev2;

import com.acstechnologies.churchlifev2.R;
import com.acstechnologies.churchlifev2.exceptionhandling.ExceptionHelper;
import com.acstechnologies.churchlifev2.webservice.WebServiceHandler;
import com.acstechnologies.churchlifev2.webservice.WebServiceObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class SplashActivity extends Activity {

	//private final int SPLASH_LENGTH = 3000;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
                        
        new AutoLoginTask().execute();
             
//                //Handler to go to the Login activity and close the Splash after some seconds.
//                new Handler().postDelayed(new Runnable(){
//                        @Override
//                        public void run() {
//                                // Intent to show Login 
//                                Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
//                                SplashActivity.this.startActivity(mainIntent);
//                                SplashActivity.this.finish();
//                        }
//                }, SPLASH_LENGTH);
        }
        

        
        private class AutoLoginTask extends AsyncTask<String, Void, Boolean> {
            protected Boolean doInBackground(String... args) {
            	Boolean result = false;
                try {
                	result = autoLoginIn();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//zzz
					e.printStackTrace();
				}
				return result;
            }

            
            // Executes on the UI thread
            //zzz if error here...route to login...how to catch?
            protected void onPostExecute(Boolean result) {
            	
            	Intent nextIntent = new Intent(SplashActivity.this, LoginActivity.class);
				
				if (result == true)  //if successful login, change the activity to which to navigate	        					
					nextIntent = new Intent(SplashActivity.this, IndividualListActivity.class);	        					
				          					
            	SplashActivity.this.startActivity(nextIntent);
                SplashActivity.this.finish();            	            	
            }
        }

        

        /** A user can choose be automatically logged in (or attempt to)
        *   if they have saved credentials in their preference
        * @return true if login was successful, false otherwise
        * @throws Exception
        */   
        private Boolean autoLoginIn() throws Exception
        {
        	Boolean result = false;        			
//        	try
//        	{        	        	
        		AppPreferences prefs = new AppPreferences(getApplicationContext());  //Initialize 
        		 
        		// get values from preferences and attempt a login
        		String auth1 = prefs.getAuth1();  
            	String auth2 = prefs.getAuth2();
            	String auth3 = prefs.getAuth3();
            	            
            	if (auth1.length() > 0 && auth2.length() > 0){
            		
            		WebServiceHandler wh = new WebServiceHandler();    		            		
            		WebServiceObject wso = wh.login(auth1, auth2, auth3);
            		
            		result = (wso.getStatusCode() == 0);       			            	
            	}
            	
//            	SplashActivity.this.startActivity(nextIntent);
//              SplashActivity.this.finish();            
                
//        	}
//        	catch (Throwable e) {
//        		ExceptionHelper.notifyUsers(e, SplashActivity.this);
//          		ExceptionHelper.notifyNonUsers(e);
//          		
//          		// route to login page
//          		Intent exIntent = new Intent(SplashActivity.this, LoginActivity.class);
//          		SplashActivity.this.startActivity(exIntent);
//        	}  
        	
        	return result;
        }   

    	
}
