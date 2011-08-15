package com.acstechnologies.churchlifev2;

import android.app.Application;

public class GlobalState extends Application
{
	private String _siteName;
	private String _siteNumber;
	private String _userName;
	private String _password;	
	
	// SiteName
	public String getSiteName() {
		return _siteName;
	}	
	public void setSiteName(String name) {
		_siteName = name;
	}
	
	// SiteNumber
	public String getSiteNumber() {
		return _siteNumber;
	}	
	public void setSiteNumber(String number) {
		_siteNumber = number;
	}

	// User name
	public String getUserName() {
		return _userName;
	}	
	public void setUserName(String name) {
		_userName = name;
	}

	// Password
	public String getPassword() {
		return _password;
	}	
	public void setPassword(String password) {
		_password = password;
	}
	
	
	/**
	 *  Used to clear all application set values
	 */
	public void clearApplicationSettings() {
		_siteName = "";
		_siteNumber = "";
		_userName = "";
		_password = "";		
	}
	
}