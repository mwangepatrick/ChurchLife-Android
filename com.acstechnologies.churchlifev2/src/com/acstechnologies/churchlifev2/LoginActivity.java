package com.acstechnologies.churchlifev2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.acstechnologies.churchlifev2.R;
import com.acstechnologies.churchlifev2.exceptionhandling.AppException;
import com.acstechnologies.churchlifev2.exceptionhandling.ExceptionHelper;
import com.acstechnologies.churchlifev2.webservice.WebServiceHandler;
import com.acstechnologies.churchlifev2.webservice.LoginResponse;
import com.acstechnologies.churchlifev2.webservice.WebServiceObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ViewFlipper;

//FUTURE: 
//  change to use animations from xml files

//zzz all literals to string resources

public class LoginActivity extends Activity {

	protected AppPreferences _appPrefs;  //zzz test non protected
	GestureDetector _gestureDetector;
	LoginResponse _wsLogin;					// results of the login web service call
	
	// ViewFlipper, Views, and buttons
	ViewFlipper vf;
	Button btnLogin;				
	ImageButton imageButtonView1;
	ImageButton imageButtonView2;
	
	// Input controls for login logic		
	View view1;								// View1 = Email, Password
	EditText txtEmail;
	EditText txtEmailPassword;
	CheckBox chkRemember;
	
	View view2;								// View2 = Site Number, Username, Password
	EditText txtUserName;
	EditText txtPassword;
	EditText txtSiteNumber;	
	CheckBox chkRemember2;
	
	private static final String LOGIN_OBJECT_NAME = "loginObject";
	private static final int DIALOG_CHURCH_LIST = 1;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                      
        try
        {
            _appPrefs = new AppPreferences(getApplicationContext());           
            _gestureDetector = new GestureDetector(new LoginSwipeDetector());            
            
            // See if this activity was called with a 'logout' parameter.
            // (This keeps all login/logout code in the Login activity)
            logoutCheck();
                                         
            setContentView(R.layout.login);		// Present the login form to the user. 
            
            bindControls();						// Set state variables to their form controls
       
            // Wire up 'view1' button         
            imageButtonView1.setOnClickListener(new OnClickListener() {		
            	public void onClick(View v) {	        		             	
            		if (vf.getCurrentView() != view1){
    		  		   	vf.setInAnimation(Animations.inFromLeftAnimation());
    		  		   	vf.setOutAnimation(Animations.outToRightAnimation());		  		   	
            			vf.setDisplayedChild(0);            			
            			viewChanged();					//update button images
            		}        	
            	}		
    		}); 
            
            // Wire up 'view2' button  
            imageButtonView2.setOnClickListener(new OnClickListener() {		
            	public void onClick(View v) {	
            		if (vf.getCurrentView() != view2){      
    		  			vf.setInAnimation(Animations.inFromRightAnimation());
    		  	    	vf.setOutAnimation(Animations.outToLeftAnimation());
            			vf.setDisplayedChild(1);
            			viewChanged();					//update button images            			            		
            		}       	
            	}		
    		}); 
            
            // Wire up the login button                     
            btnLogin.setOnClickListener(new OnClickListener() {		
            	public void onClick(View v) {	            		     
            		if (inputIsValid())
            			doLoginWithProgressWindow();
            	}		
    		});     
            
        }
    	catch (Throwable e) {
    		ExceptionHelper.notifyUsers(e, LoginActivity.this);
      		ExceptionHelper.notifyNonUsers(e);
    	}    
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {    				
        switch (id) {
        case DIALOG_CHURCH_LIST:
        	final String formatString = "%s - %s";    
        	try {        		        	        	
        		final CharSequence[] items = new CharSequence[_wsLogin.getLength()];
        		
        		// Build a list of select items from the class level login response object
        		for (int i=0;i< items.length;i++){         			
        			items[i] = String.format(formatString, _wsLogin.getSiteName(i), _wsLogin.getUserName(i));               			
        		} 
        		         									
				AlertDialog.Builder b = new AlertDialog.Builder(LoginActivity.this);	        	         	        
	            b.setTitle(R.string.Login_SiteSelectTitle);           
	            b.setItems(items, new DialogInterface.OnClickListener() {
	            	public void onClick(DialogInterface dialog, int which) {
	                
	            		// Get the login item based on the which (index) selected and navigate
	            		//  forward with the info from that site number. (The text displayed
	            		//  does not contain site number so we lookup by index.)
	            		String siteNumber = _wsLogin.getSiteNumber(which);	            		
	            		String userName = _wsLogin.getUserName(which);
	            			            			            			            	
	            		// Safety check - should never happen since we build the list of items
	            		//  to display...but just in case the sitename/username the user selected 
	            		//  differs from the sitename of that indexed item in the login response...
	            		String labelShouldBe = String.format(formatString, _wsLogin.getSiteName(which), _wsLogin.getUserName(which));
	            		
	            		if (items[which].toString().equals(labelShouldBe)) {	            			
	            			navigateForward(siteNumber, userName);
	            		}	
	            		else
	            		{
	            			//zzz need to add our own reason here
	            			//throw new AppException();
	            		}
	            		
//	            		/* User selected a site, write to preferences and continue */
//	 			    	String siteNumber = items[which].toString();			   
//	 			    	
//				        Toast.makeText(getApplicationContext(), siteNumber, Toast.LENGTH_SHORT).show();
//				       
//				        //navigateForward(siteNumber);
//				        
//	                     //String[] items = getResources().getStringArray(R.array.select_dialog_items);
//	                     new AlertDialog.Builder(LoginActivity.this)
//	                               .setMessage("You selected: " + which + " , " + items[which])
//	                               .show();
//	                     
	                 }
	             });            
	             return b.create();							
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				//zzz revisit
				e.printStackTrace();
			}

        	
        }
		return null;
    }
    
    // Links state variables to their respective form controls
    private void bindControls(){
    	
    	// ViewFlipper and direct child controls
    	vf = (ViewFlipper)this.findViewById(R.id.ViewFlipper01);
    	
    	btnLogin = (Button)this.findViewById(R.id.btnLogin);		    	
    	imageButtonView1 = (ImageButton)this.findViewById(R.id.imageButtonView1);
    	imageButtonView2 = (ImageButton)this.findViewById(R.id.imageButtonView2);
    	
    	// Input controls for login logic		    								
        view1 = vf.getChildAt(0);		        
    	txtEmail = (EditText)this.findViewById(R.id.txtEmail);
    	txtEmailPassword = (EditText)this.findViewById(R.id.txtEmailPassword);
    	chkRemember=(CheckBox)this.findViewById(R.id.chkRememberMe);
    	
    	view2 = vf.getChildAt(1); 	
    	txtUserName = (EditText)this.findViewById(R.id.txtUsername);
    	txtPassword = (EditText)this.findViewById(R.id.txtPassword);
    	txtSiteNumber = (EditText)this.findViewById(R.id.txtSiteNumber);
    	chkRemember2 =( CheckBox)this.findViewById(R.id.chkRememberMe2);    	       
    }
    
        
    // A user can choose to 'Sign Out' which removes any saved 
    //  credentials from the saved preferences.
    private void logoutCheck() throws Exception
    {
    	Boolean logout = false;
        Bundle extraBundle = this.getIntent().getExtras();
        if (extraBundle != null) {
        	if (extraBundle.containsKey("logout") == true)
        	{
        		logout = extraBundle.getBoolean("logout");
        	}
        }
        
        // remove any authorization values from preferences
        if (logout==true){        	
        	_appPrefs.setAuth1("");
        	_appPrefs.setAuth2("");
        	_appPrefs.setAuth3("");        	
        }                
    }
    
    
    /* Ensures all form fields have valid input.  
     *   Should be called on button click before processing.
     * 
     */
    private Boolean inputIsValid()
    {    	
    	String msg = "";
    	
    	if (vf.getCurrentView() == view1) {
    		if (txtEmail.getText().length() == 0) {    			
    			msg = (String)this.getResources().getText(R.string.Login_Validation_Email); 
    		}
    		if (txtEmailPassword.getText().length() == 0){   
    			msg = (String)this.getResources().getText(R.string.Login_Validation_Password);    			
    		}        	
    	}
    	else { //vf.Currentview() == view2
    		if (txtSiteNumber.getText().length() == 0) {   
    			msg = (String)this.getResources().getText(R.string.Login_Validation_SiteNumber);     		
    		}
    		if (txtUserName.getText().length() == 0){    			
    			msg = (String)this.getResources().getText(R.string.Login_Validation_UserName);     			
    		}     		
    		if (txtPassword.getText().length() == 0){    			
    			msg = (String)this.getResources().getText(R.string.Login_Validation_Password);     		
    		}     		
    	}
    	
    	// If a validation message exists, show it
    	if (msg.length() > 0) {
    		Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show(); 
    	}
    	
    	// If a validation message exists, the input is invalid
    	return (msg.length() == 0);    	    
    }
    
    
    // Assumes form input has been validated!    
    private void doLoginWithProgressWindow()
    {           
    	final ProgressDialog dialog = ProgressDialog.show(LoginActivity.this, "", this.getResources().getText(R.string.Login_ProgressDialog), true);
 	   
    	// This handler is called once the login is complete.  It looks at the data returned
    	//  from the thread (in msg) to determine success/failure.  If successful, the user may need
    	//  to be routed to a 'pick a site' page if the login is associated with multiple sites. 
    	final Handler handler = new Handler() {
    		public void handleMessage(Message msg) { 
    			
    			dialog.dismiss();
    			
    			// Check for success.  If successful, set the class level login results
    			//  variable.  Then, check for a list of users associated with the  
    			//  credentials given.  If just one, do normal navigation; otherwise, 
    			//  show the user a list and allow them to choose
    			_wsLogin = new LoginResponse(msg.getData().getString(LOGIN_OBJECT_NAME));
    			
    			//zzz nasty exception handling
    			try {
					if (_wsLogin.getStatusCode() == 0) {        				
						// In most cases, the return value is for a single site    
						if (_wsLogin.getLength() == 1) {    					   		
							navigateForward(_wsLogin.getSiteNumber(), _wsLogin.getUserName());																
						}
						else
						{
							// present a picklist of sites to the user
							showDialog(DIALOG_CHURCH_LIST);    					    				
						}    				    				    			    			
					}
					else
					{        		
						Toast.makeText(LoginActivity.this, R.string.Login_Validation_InvalidLogin, Toast.LENGTH_LONG).show(); 
					}
				} catch (NotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	    				    			    		   
    		}
    	};
    	
    	//TODO how to throw an exception up to caller?
    	Thread checkUpdate = new Thread() {  
    		public void run() {
    			
    			LoginResponse loginObj = null;
    			
    			// Get (default) form values (from view1)
    			String usernameOrEmail = txtEmail.getText().toString();
    			String password = txtEmailPassword.getText().toString();
    			String siteNumber = "";
    			    			
    			// Reset values from form if view2 is selected
    			if (vf.getCurrentView() == view2) {
    				usernameOrEmail = txtUserName.getText().toString();
    				password = txtPassword.getText().toString();
    				siteNumber = txtSiteNumber.getText().toString();    				
    			}
    			
    			loginObj = doLogin(usernameOrEmail, password, siteNumber);
    			     			    	
    			// Caller needs the data back to it can know whether or not:
    			//  1.  Login was valid
    			//  2.  Login is associated with multiple sites (so a site picker must be displayed)    			    			    	
    			Message m = Message.obtain();
   							    			
    			Bundle b = new Bundle();   					
				b.putString(LOGIN_OBJECT_NAME, loginObj.toString()); 		//pass back webservice data to the handler
        		m.setData(b); 	
    	
    			handler.sendMessage(m);    		    
    		 }
    	};
    	checkUpdate.start();    		
    }
                   
    
        
	/**
	 * Sends credentials to the authentication service and return the results.
     * Assumes input has been validated
     * 
	 * @param auth1  Username or Email address
	 * @param auth2  Password
	 * @param auth3  SiteNumber
	 * @return the object returned from the webservice call
	 */    
    private LoginResponse doLogin(String auth1, String auth2, String auth3)
    {	
		WebServiceHandler wh = new WebServiceHandler();    		
		LoginResponse wsl = null;
		
		try
    	{   
    		// If auth3 was supplied, do a site-specific login; otherwise, do the normal login
        	if (auth3.length() > 0){
        		wsl = wh.login(auth1, auth2, auth3);
        	}
        	else {
        		wsl = wh.login(auth1, auth2);
        	}        	
    	}
    	catch (Throwable e) {
    		//zzz may should throw this up and let the caller handle it
    		ExceptionHelper.notifyUsers(e, LoginActivity.this);
      		ExceptionHelper.notifyNonUsers(e);      		
    	}       	
    	return wsl;
    }
      
    
    // Upon successful login, route the user to the correct activity.  Pass
    //  the security information to that activity so that it can make web
    //  service calls.  Username and SiteName are passed from the caller
    //  (returned from the login web service call).  Password and 'remember
    //   me' are resolved from UI controls
    //  
    // If the user chose 'Remember Me' we need to persist their credentials.
    //
    // FUTURE:  allow user to set a preference for which page they will start on
    private void navigateForward(String siteNumber, String userName)
    {    	    	
    	// Get control values from the form
    	Boolean remember = chkRemember.isChecked();
    	String password = txtEmailPassword.getText().toString();
    	
    	if (vf.getCurrentView() == view2){
    		password = txtPassword.getText().toString();
    		remember = chkRemember2.isChecked();
    	}    	
    	
    	// If the 'remember me' checkbox was checked, save user preferences 
		if (remember){
			try {
				_appPrefs.setAuth1(userName);	
				_appPrefs.setAuth2(password);
				_appPrefs.setAuth3(siteNumber);   
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//zzz
			}
    	} 
		
    	//start IndividualListActivity
		Intent intent = new Intent();
		intent.setClass(LoginActivity.this, IndividualListActivity.class);
		 
		//Create a bundle and initialize it
		Bundle bundle = new Bundle();

		//Add the parameters to a bundle and attach it to the intent
		bundle.putString("sitenumber",siteNumber);
		bundle.putString("username",userName);
		bundle.putString("password",password);
		
		intent.putExtras(bundle);
		
		finish();				// ensures the user cannot use the 'back' to this activity
		
		startActivity(intent);												
    }

    
    //***************************************************************************
    // View Flipping and Gesture handling
    //***************************************************************************
    
    // When a view changes, update the common view switching buttons so one is light (active) and the other is dark
    private void viewChanged()
    {
    	if (vf.getCurrentView() == view1)
    	{    		
			imageButtonView1.setImageDrawable(LoginActivity.this.getResources().getDrawable(R.drawable.icon_light_dot));
			imageButtonView2.setImageDrawable(LoginActivity.this.getResources().getDrawable(R.drawable.icon_dark_dot));			
    	}
    	else
    	{
    		imageButtonView2.setImageDrawable(LoginActivity.this.getResources().getDrawable(R.drawable.icon_light_dot));
			imageButtonView1.setImageDrawable(LoginActivity.this.getResources().getDrawable(R.drawable.icon_dark_dot));		
    	}    	    	
    }
    
    
    @Override
    public boolean onTouchEvent(MotionEvent touchevent) {
    	return _gestureDetector.onTouchEvent(touchevent);		
    }
           
    class LoginSwipeDetector extends SimpleOnGestureListener {

	  	  private static final int SWIPE_MIN_DISTANCE = 120;
	  	  private static final int SWIPE_MAX_OFF_PATH = 250;
	  	  private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	
	  	  public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {	  		  
		  	   //System.out.println(" in onFling() :: ");		  	   
		  	   if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
		  	    return false;
		  	   
		  	   if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
		  			vf.setInAnimation(Animations.inFromRightAnimation());
		  	    	vf.setOutAnimation(Animations.outToLeftAnimation());
		  	    	vf.showNext();
		  	    	viewChanged();					//update button images
		  	   } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
		  		   	vf.setInAnimation(Animations.inFromLeftAnimation());
		  		   	vf.setOutAnimation(Animations.outToRightAnimation());
		  		   	vf.showPrevious();
		  		   	viewChanged();					//update button images	
		  	   }  	   
		  	   return super.onFling(e1, e2, velocityX, velocityY);
	  	  }  	    	    	 
  	}  //end class LoginSwipeDetector
  

}
