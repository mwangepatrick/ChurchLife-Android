package com.acstech.churchlife;


import java.util.ArrayList;
import java.util.List;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionHelper;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;
import com.acstech.churchlife.webservice.Api;
import com.acstech.churchlife.webservice.CoreAcsUser;
import com.acstech.churchlife.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.Toast;
import android.widget.ViewFlipper;

//FUTURE: 
//  1.  change to use animations from xml files


public class LoginActivity extends OptionsActivity {

	AppPreferences _appPrefs;  				
	GestureDetector _gestureDetector;
	List<CoreAcsUser> _wsLogin;				// results of the login web service call (can be a list of 1 for a sitenumber login)
	
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
	
	private static final int DIALOG_CHURCH_LIST = 1;
	private static final int DIALOG_PROGRESS = 2;	
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try
        {
        	_appPrefs = new AppPreferences(getApplicationContext());           
            _gestureDetector = new GestureDetector(new LoginSwipeDetector());                               
                      
        	// Remove login credentials from global state - ALWAYS
        	GlobalState gs = GlobalState.getInstance(); 
        	
        	gs.clearApplicationSettings();  
        	
            logoutCheck(); 						// Was this activity was called with a 'logout' parameter?
                                         
            setContentView(R.layout.login2);		// Present the login form to the user. 
            
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
            		
            		// check for 'developer mode' login
            		if (checkDeveloperMode() == false) {            		
						if (inputIsValid()) {
		       	            // Hide the keyboard            			            		
	            			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		       	            imm.hideSoftInputFromWindow(btnLogin.getWindowToken(), 0);
		       	             
	            			doLoginWithProgressWindow(); // login in with worker thread
						}
            		}
            	}		
    		});     
            
        }
    	catch (Exception e) {
    		ExceptionHelper.notifyUsers(e, LoginActivity.this);
    		ExceptionHelper.notifyNonUsers(e);
    	}       
    }
    
    /**
     * Update the site picklist dialog with the latest login's list of sites
     * 
     */
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
    	try
    	{
	    	if (id == DIALOG_CHURCH_LIST) {	    		
	    		final String formatString = "%s\n   %s";    
	        	final String[] items = new String[_wsLogin.size()];
	        		
	        	// Build a list of select items from the class level login response object
	        	for (int i=0;i< items.length;i++){  
	        		items[i] = String.format(formatString, _wsLogin.get(i).SiteName, _wsLogin.get(i).UserName);               			
	        	}         	               
	    		
	            ListAdapter itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, items);	
	            AlertDialog ad = (AlertDialog) dialog;
	            ad.getListView().setAdapter(itemsAdapter);	           
	    	}
    	}
    	catch (Exception e) {
    		ExceptionHelper.notifyUsers(e, LoginActivity.this);
    		ExceptionHelper.notifyNonUsers(e);   
    	}		
    }
    
    
    /**
     * Shows a dialog to the user.
     *   
     *   DIALOG_CHURCH_LIST 
     *      An alert dialog (modal) that is presented to the user when that users'
     *      credentials map to more than one site.  This dialog displays all of the
     *      sites and allows the user to select one.  
     *  
     *   DIALOG_PROGRESS
     *       A progress dialog displayed when the login operation is running.
     *   
     */
    @Override
    protected Dialog onCreateDialog(int id) {    
    	try
    	{
	        switch (id) {
	        case DIALOG_CHURCH_LIST:
	        	
	        	// see onPrepareDialog for actual items loaded into this dialog
	        	final CharSequence[] items = new CharSequence[0];
	        	
				AlertDialog.Builder b = new AlertDialog.Builder(LoginActivity.this);	        	         	        
		        b.setTitle(R.string.Login_SiteSelectTitle);           
		        b.setItems(items, new DialogInterface.OnClickListener() {
		        	public void onClick(DialogInterface dialog, int which) {		              
		        		try {		        					        			
			            	// Get the login item based on the which (index) selected 
		        			// Do web service login to get user rights
		        			Integer siteNumber = _wsLogin.get(which).SiteNumber;	            		
			            	String userName = _wsLogin.get(which).UserName;
			            			        			
			            	// Do site-specific web service call so a user's rights are returned
			            	vf.setDisplayedChild(1);								//  zzz revisit
			            	txtUserName.setText(userName);
			            	txtPassword.setText(txtEmailPassword.getText());	
			            	txtSiteNumber.setText(Integer.toString(siteNumber));
			            	
			            	doLoginWithProgressWindow();
			            }
		            	catch (Exception e) {
		                		ExceptionHelper.notifyUsers(e, LoginActivity.this);
		                  		ExceptionHelper.notifyNonUsers(e);
		                }	            			            			            			            	
		             }
		        });
		        
		        return b.create();							
              	
	    	case DIALOG_PROGRESS:    	
	    		ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
	    		dialog.setMessage(getString(R.string.Login_ProgressDialog));
	    		dialog.setIndeterminate(true);
	    		dialog.setCancelable(false);
	    		return dialog;	    
	    		    		    		
	    	}
    	}
    	catch (Exception e) {
    		ExceptionHelper.notifyUsers(e, LoginActivity.this);
    		ExceptionHelper.notifyNonUsers(e);   
    	}
		return null;
    }
    
    /**
     *  Links state variables to their respective form controls
     */
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
    }
               
    /** 
     * A user can choose to 'Sign Out' (from options menu) which passes a 'logout'
     *  value in the bundle when this activity is created.  If logout exists, 
     *  this removes any saved credentials from the saved preferences (and then
     *  the user is presented with the login form).
     * 
     * @throws Exception
     */
    private void logoutCheck() throws AppException
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
    
    /**
     * if username is a 'special' login, put the application in 'developer' mode 
     *   and allow the user to login with a normal username/password 
     * @throws AppException 
     */
    private Boolean checkDeveloperMode() {
    	Boolean result = false;
    	try
    	{	    	
	    	String devLogin = (String)this.getResources().getText(R.string.Login_DeveloperLogin);
	    	
	    	if (txtEmail.getText().toString().equals(devLogin)) {     		    		    	
	    		_appPrefs.setDeveloperMode(true);
	    		
	    		// clear user name input and indicate to user
	    		txtEmail.setText("");	    		
	    		String msg = (String)this.getResources().getText(R.string.Login_DeveloperLoginMessage);
	    		Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
	    		
	    		result = true;
			}	    
    	}
		catch (Exception e) {				
			ExceptionHelper.notifyUsers(e, LoginActivity.this);
    		ExceptionHelper.notifyNonUsers(e);	    				
		}
    	return result;
    }
    
    
    /**
     * Ensures all form fields have valid input.  
     * 
     * Should be called on button click before processing.   Displays a message 
     *   to the user indicating which field is invalid.  This procedure stops 
     *   checking for invalid fields once the first invalid field is encountered.
     *   
     * @return true if input fields are valid, otherwise false
     */
    private Boolean inputIsValid()
    {    	
    	String msg = "";
    	
    	if (vf.getCurrentView() == view1) {
    		if (txtEmail.getText().length() == 0) {    			
    			msg = (String)this.getResources().getText(R.string.Login_Validation_Email); 
    		}
    		if (msg.length() == 0 && txtEmailPassword.getText().length() == 0){   
    			msg = (String)this.getResources().getText(R.string.Login_Validation_Password);    			
    		}        	
    	}
    	else { //vf.Currentview() == view2
    		if (txtSiteNumber.getText().length() == 0) {   
    			msg = (String)this.getResources().getText(R.string.Login_Validation_SiteNumber);     		
    		}
    		if (msg.length() == 0 && txtUserName.getText().length() == 0){    			
    			msg = (String)this.getResources().getText(R.string.Login_Validation_UserName);     			
    		}     		
    		if (msg.length() == 0 && txtPassword.getText().length() == 0){    			
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
    
   
    /**
     * Displays a progress dialog and launches a background thread to connect to a web service
     *   to authenticate the users credentials
     *   
     */
    private void doLoginWithProgressWindow()
    {           
    	if (inputIsValid()) {	    			    
	    	showDialog(DIALOG_PROGRESS);
	    	
	    	// This handler is called once the login attempt is complete.  It looks at the class level
	    	//  web service return object.  If successful, the web service response object will have 1 
	    	//  or more logins (with sites) in it.
	    	final Handler handler = new Handler() {
	    		public void handleMessage(Message msg) {
	    		  
	    			removeDialog(DIALOG_PROGRESS);	    		
	    			try {	  
	    				
	    				// check to see if an exception was raised on the background thread
	    				//  that performed the login web service call. 
	    				if (msg.what < 0) {
		       				Bundle b = msg.getData();
		       				throw ExceptionHelper.getAppExceptionFromBundle(b, "doLoginWithProgressWindow.handleMessage");		       					  		       			
	    				}
	    				else {
	    					
	    					// one or more users returned in message (in arraylist of string)
	    					//  - map these to the class level list of CoreAcsUser objects
	    					ArrayList<String> loginsArrayList = msg.getData().getStringArrayList("logins");
	    						    						
	    					_wsLogin = new ArrayList<CoreAcsUser>();
			    	    	for (String userJson : loginsArrayList){
			    	    		_wsLogin.add(CoreAcsUser.GetCoreAcsUser(userJson));
			    	    	}
			    	    	
							if (_wsLogin.size() > 0) {        				
								// In most cases, the return value is for a single site    
								if (_wsLogin.size() == 1) {    					   		
									navigateForward(_wsLogin.get(0));
								}
								else {
									// present a picklist of sites to the user
									showDialog(DIALOG_CHURCH_LIST);    					    				
								}    				    				    			    			
							}
							else
							{        		
								Toast.makeText(LoginActivity.this, R.string.Login_Validation_InvalidLogin, Toast.LENGTH_LONG).show(); 
							}	    					
	    				}	    				
	    			}
	    			catch (Exception e) {
	    				// If unauthorized, show appropriate 'friendly' message for login
	    				if(e instanceof AppException) {	    				    
	    				    AppException ae = (AppException) e;
	    				    if(ae.getErrorType() == ExceptionInfo.TYPE.UNAUTHORIZED) {
	    				    	Toast.makeText(LoginActivity.this, R.string.Login_Validation_InvalidLogin, Toast.LENGTH_LONG).show();
	    				    }
	    				    else  {
	    				    	ExceptionHelper.notifyUsers(e, LoginActivity.this);
		    	    			ExceptionHelper.notifyNonUsers(e);
	    				    }
	    				}
	    				else {
	    					ExceptionHelper.notifyUsers(e, LoginActivity.this);
	    	    			ExceptionHelper.notifyNonUsers(e);
	    				}
	    			}    	    			
	    		}
	    	};
	    	
	    	Thread searchThread = new Thread() {  
	    		public void run() {
	    			try {	    					    			
	    				List<CoreAcsUser> logins = doLogin();
		    	    		    				
		    	    	// Return the object (as arraylist of strings) to the message handler above
		    	    	Message msg = handler.obtainMessage();	
		    	    	msg.what = 0;
		    	    	
		    	    	Bundle b = new Bundle();	
		    	    	
		    	    	ArrayList<String> loginsArrayList = new ArrayList<String>();
		    	    	for (CoreAcsUser user : logins){
		    	    		loginsArrayList.add(user.toString());
		    	    	}
		    	    			    	    	
		    	    	b.putStringArrayList("logins", loginsArrayList);
		    	    	
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
    }
    
        
	/**
	 * Sends credentials to the authentication service and return the results.
     * Assumes input has been validated
     * 
	 * @param auth1  Username or Email address
	 * @param auth2  Password
	 * @param auth3  SiteNumber
	 * @return the object returned from the webservice call
	 * @throws AppException 
	 */    
    private List<CoreAcsUser> doLogin() throws AppException
    {	
    	List<CoreAcsUser> userList = new ArrayList<CoreAcsUser>();
    	
		//zzz WebServiceHandler wh = new WebServiceHandler(_appPrefs.getWebServiceUrl(), config.APPLICATION_ID_VALUE);   			
	   	Api apiCaller = new Api("https://secure.accessacs.com/api_accessacs", config.APPLICATION_ID_VALUE);
	  
	   	// If sitenumber view is complete..use it, otherwise use email address view
		String usernameOrEmail = txtUserName.getText().toString();
		String password = txtPassword.getText().toString();
		String siteNumber = txtSiteNumber.getText().toString();
	   	
		if (usernameOrEmail.length()==0 || password.length() ==0 || siteNumber.length() ==0)
		{
			usernameOrEmail = txtEmail.getText().toString();
			password = txtEmailPassword.getText().toString();			
		}
		
	   	/*
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
		*/
	   	
		// If siteNumber was supplied, do a site-specific login; otherwise, do the normal login
        if (siteNumber.length() > 0){        	
        	CoreAcsUser user = apiCaller.user(usernameOrEmail, password, siteNumber);        	
        	if (user != null) { userList.add(user); }        	
        }
        else {
        	 userList = apiCaller.users(usernameOrEmail, password);        	
        }  
        return userList;
    }
    
    
    /**
     * Upon successful login, route the user to the correct activity.  Pass
     *  the security information to that activity so that it can make web
     *  service calls.  SiteNumber, SiteName, and Username are passed from the caller
     *  (returned from the login web service call).  Password and 'remember
     *   me' are resolved from UI controls
     *   
     *   If the user chose 'Remember Me' their credentials are persisted.
     *   
     *   
     * @param siteNumber
     * @param userName
     * @throws AppException 
     */
    private void navigateForward(CoreAcsUser loggedInUser) throws AppException
    {    	    	
    	// Get control values from the form
    	Boolean remember = chkRemember.isChecked();
    	String password = txtEmailPassword.getText().toString();
    	
    	if (vf.getCurrentView() == view2){
    		password = txtPassword.getText().toString();
    	}    	
    	
    	// If the 'remember me' checkbox was checked, save user preferences 
		if (remember){			
			_appPrefs.setAuth1(loggedInUser.UserName);	
			_appPrefs.setAuth2(password);
			_appPrefs.setAuth3(String.valueOf(loggedInUser.SiteNumber));	
			_appPrefs.setOrganizationName(loggedInUser.SiteName); 
    	} 
		
		// Save login credentials to global state as they are needed for EVERY web service call
		//  (keep in mind that these need to be in state regardless of the 'remember me' setting)
		GlobalState gs = GlobalState.getInstance();
		gs.setUser(loggedInUser);
		gs.setPassword(password);
					
    	//start IndividualListActivity
		Intent intent = new Intent();
		intent.setClass(LoginActivity.this, MainActivity.class); 
		
		finish();				// ensures the user cannot use the 'back' to this activity
		
		startActivity(intent);												
    }

    
    //***************************************************************************
    // View Flipping and Gesture handling
    //***************************************************************************
    
    /**
     * When a view changes, update the 'view switching' buttons so one is light (active) and the other is dark
     */
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
