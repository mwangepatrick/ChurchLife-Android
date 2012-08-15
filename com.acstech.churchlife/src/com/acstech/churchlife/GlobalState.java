package com.acstech.churchlife;

import java.util.Map;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionHelper;
import com.acstech.churchlife.webservice.CoreAcsUser;

import android.app.Application;

public class GlobalState extends Application
{	
	// singleton pattern
	private static GlobalState _globalStateInstance;
	public static GlobalState getInstance() {
		return _globalStateInstance;
	}

	private CoreAcsUser _user;			// currently logged in user
	private String _password;
		
	// SiteName
	public String getSiteName() {
		if (_user != null) {
			return _user.SiteName;
		}
		else {
			return "";
		}
	}
	
	// SiteNumber
	public String getSiteNumber() {
		if (_user != null) {
			return Integer.toString(_user.SiteNumber);
		}
		else {
			return "";
		}
	}
	
	// User name
	public String getUserName() {
		if (_user != null) {
			return _user.UserName;
		}
		else {
			return "";
		}
	}	

	// User's Rights
	public Map<String, String> getRights() {
		if (_user != null) {
			return _user.Rights;
		}
		else {
			return null;
		}
	}

	// Password
	public String getPassword() {
		return _password;
	}	
	public void setPassword(String password) throws AppException {
		_password = password;
		setSavedState();
	}

	// User (last logged in)
	public CoreAcsUser getUser() {
		return _user;
	}	
	public void setUser(CoreAcsUser user) throws AppException {
		_user = user;
		setSavedState();
	}
	
	/**
	 *  Used to clear all application set values
	 */
	public void clearApplicationSettings() throws AppException {	
		_user = null;
		_password = "";		
		setSavedState();
	}
	
	
	// Saves a single string value to represent the current 'state' of this class
	//
	//  Used to save the state of this class to preferences when the OS is destroying this class.
	private void setSavedState() throws AppException
	{		
		String state = "";		
		/*
		if (_siteName.length() > 0 || _siteNumber.length() > 0 ||
			_userName.length() > 0 || _password.length() > 0 || _user != null) {
		*/

		//if (_user != null || _password.length() > 0) {
			
		String userJson = "";
		if (_user != null) {
			userJson = _user.toString();			
		}
		
		state = String.format("%s|%s", userJson, _password);
		//}		

		AppPreferences prefs = new AppPreferences(getApplicationContext()); 
		prefs.setApplicationState(state);		
	}
	
	// Accepts a single string that represents the 'state' of this class and sets variables accordingly.  
	//
	// Used when creating this class after it was destroyed by the OS
	//
	// 2012.07.30 MAS changed format to have a pipe | delimeter instead of comma since some
	//                data (sitename) could contain a comma
	//
	private void getSavedState() throws AppException
	{
		AppPreferences prefs = new AppPreferences(getApplicationContext());
		String state = prefs.getApplicationState();
		
		if (state.length() > 0 && state.indexOf("|") > 0) {
			
			String[] stateValues = state.split("|");					
			if (stateValues[0].length() > 0) {
				_user = CoreAcsUser.GetCoreAcsUser(stateValues[4]);
			}			
			_password = stateValues[1];
			
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