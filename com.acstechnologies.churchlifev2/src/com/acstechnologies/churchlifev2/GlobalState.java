package com.acstechnologies.churchlifev2;

import com.acstechnologies.churchlifev2.exceptionhandling.AppException;
import com.acstechnologies.churchlifev2.exceptionhandling.ExceptionHelper;

import android.app.Application;

public class GlobalState extends Application
{	
	// singleton pattern
	private static GlobalState _globalStateInstance;
	public static GlobalState getInstance() {
		return _globalStateInstance;
	}
	
	private String _siteName;
	private String _siteNumber;
	private String _userName;
	private String _password;	
	
	// SiteName
	public String getSiteName() {
		return _siteName;
	}	
	public void setSiteName(String name) throws AppException {
		_siteName = name;
		setSavedState();
	}
	
	// SiteNumber
	public String getSiteNumber() {
		return _siteNumber;
	}	
	public void setSiteNumber(String number) throws AppException {
		_siteNumber = number;
		setSavedState();
	}

	// User name
	public String getUserName() {
		return _userName;
	}	
	public void setUserName(String name) throws AppException {
		_userName = name;
		setSavedState();
	}

	// Password
	public String getPassword() {
		return _password;
	}	
	public void setPassword(String password) throws AppException {
		_password = password;
		setSavedState();
	}
	
	/**
	 *  Used to clear all application set values
	 */
	public void clearApplicationSettings() throws AppException {
		_siteName = "";
		_siteNumber = "";
		_userName = "";
		_password = "";	
		setSavedState();
	}
	
	
	// Saves a single string value to represent the current 'state' of this class
	//
	//  Used to save the state of this class to preferences when the OS is destroying this class.
	private void setSavedState() throws AppException
	{		
		String state = "";
		if (_siteName.length() > 0 || _siteNumber.length() > 0 ||
			_userName.length() > 0 || _password.length() > 0) {
			
			state = String.format("%s,%s,%s,%s", _siteName, _siteNumber, _userName, _password);
		}
			
		AppPreferences prefs = new AppPreferences(getApplicationContext()); 
		prefs.setApplicationState(state);		
	}
	
	// Accepts a single string that represents the 'state' of this class and sets variables accordingly.  
	//
	// Used when creating this class after it was destroyed by the OS
	private void getSavedState() throws AppException
	{
		AppPreferences prefs = new AppPreferences(getApplicationContext());
		String state = prefs.getApplicationState();
				
		if (state.length() > 0) {
			String[] stateValues = state.split(",");			 
			_siteName = stateValues[0];
			_siteNumber = stateValues[1];
			_userName = stateValues[2];
			_password = stateValues[3];							
		}		
	}

	
    @Override
    public void onCreate() {
    	super.onCreate();  
    	_globalStateInstance = this;
      
    	try {    	
    		// Fill this class with the last-saved state (ignore if no last state)
    		// Remember:  This application can be destoyed (user navigates away, etc.)  If so, 
    		//            when it is resurrected this class must retrieve these values.
    		this.getSavedState();
    	}
    	 catch (Exception e) {
         	// must NOT raise errors.  called by an event
 			ExceptionHelper.notifyUsers(e, getApplicationContext());
 	    	ExceptionHelper.notifyNonUsers(e)  ; 				    				
 		}      	
    }
	
}