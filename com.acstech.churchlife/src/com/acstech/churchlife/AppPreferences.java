package com.acstech.churchlife;

import com.acstech.churchlife.exceptionhandling.AppException;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.provider.Settings;


public class AppPreferences {

    /**
     * This value SHOULD ALWAYS be changed to match your package.
     */
     public static final String APP_SHARED_PREFS = "com.acstech.churchlife_preferences"; //  Name of the file -.xml
          
     private SharedPreferences appSharedPrefs;
     private Editor prefsEditor;

     public static String EncryptionSeedValue() {
    	 return "yourvaluehere";    	 
     }
     
     public AppPreferences(Context context)
     {    	 
         this.appSharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);    	
         this.prefsEditor = appSharedPrefs.edit();         
     }
   
     // Auth1 - ENCRYPTED
     public String getAuth1() throws AppException {    	   
         return getEncryptedValue("auth1");
     }
     public void setAuth1(String text) throws AppException {
    	 setEncryptedValue("auth1", text);
     }
     
     // auth2 - ENCRYPTED
     public String getAuth2() throws AppException {    	   
         return getEncryptedValue("auth2");
     }
     public void setAuth2(String text) throws AppException {
    	 setEncryptedValue("auth2", text);
     }
          
     // auth3 - ENCRYPTED
     public String getAuth3() throws AppException {    	   
         return getEncryptedValue("auth3");
     }
     public void setAuth3(String text) throws AppException {
    	 setEncryptedValue("auth3", text);
     }
     
     // Developer Mode
     public Boolean getDeveloperMode() throws AppException {
    	 return appSharedPrefs.getBoolean("developermode", false);         
     }
     public void setDeveloperMode(Boolean mode) throws AppException {
       	 prefsEditor.putBoolean("developermode", mode);
    	 prefsEditor.commit();
    	 
    	 // when developer mode is turned off, set the url back to default
    	 if (mode == false) {
    		 setWebServiceUrl(config.CORE_SERVICE_URL_VALUE);
    		 setGivingWebServiceUrl(config.GIVING_SERVICE_URL_VALUE);
    	 }
     }
     
     // Web Service URI 
     public String getWebServiceUrl() {    	
    	 
    	 String result = appSharedPrefs.getString("serviceurl", "");
    	
    	 // Handle upgrade from previous version...if old url, replace with new service url    	 
    	 if (result.equals("") || result.equals(config.SERVICE_URL_VALUE)) {
    		 setWebServiceUrl(config.CORE_SERVICE_URL_VALUE);
    	 }
    	 return appSharedPrefs.getString("serviceurl", config.CORE_SERVICE_URL_VALUE);
     }
     
     public void setWebServiceUrl(String url) {
    	 prefsEditor.putString("serviceurl", url);
    	 prefsEditor.commit();
     }

     // Giving Web Service URI
     public String getGivingWebServiceUrl() {    	
    	 
    	 String result = appSharedPrefs.getString("givingserviceurl", "");
    	
    	 // if missing, set to default    	 
    	 if (result.equals("")) {
    		 setGivingWebServiceUrl(config.GIVING_SERVICE_URL_VALUE);
    	 }
    	 return appSharedPrefs.getString("givingserviceurl", config.GIVING_SERVICE_URL_VALUE);
     }
     
     public void setGivingWebServiceUrl(String url) {
    	 prefsEditor.putString("givingserviceurl", url);
    	 prefsEditor.commit();
     }
     
     // Organization Name - set from web service (returned from login)
 	public String getOrganizationName() {
 		return appSharedPrefs.getString("organizationname", "");
	}
    public void setOrganizationName(String name) {
   	 prefsEditor.putString("organizationname", name);
   	 prefsEditor.commit();
    }
     
     // Application State - if application is destroyed by the OS, current state gets written to preferences
     public String getApplicationState() throws AppException {       
         return getEncryptedValue("applicationstate");
     }
     
     // Application State - when application is created (or re-created) by the OS, state gets read from preferences
     public void setApplicationState(String text) throws AppException {
    	 setEncryptedValue("applicationstate", text);       
     }
     
          
     // Helper methods for encrypted keys
     public String getEncryptedValue(String keyName) throws AppException {
    	        
    	String result = "";
    	String arg = formatArg(EncryptionSeedValue());
 		String encryptedValue = appSharedPrefs.getString(keyName, "");
 		if (encryptedValue.length() > 0){ 				
 			result = SimpleCryptography.decrypt(arg, encryptedValue);
 		} 		
        return result;        
     }
     
     public void setEncryptedValue(String keyName, String value) throws AppException {
    	    
    	String encryptedString = "";
    	String arg = formatArg(EncryptionSeedValue());    	
  		if (value.length() > 0){				
  			encryptedString = SimpleCryptography.encrypt(arg, value);
  		}
  		prefsEditor.putString(keyName, encryptedString);
  	    prefsEditor.commit();   	    	     	
     }

     private String formatArg(String input)
     {
     	input = Settings.Secure.ANDROID_ID;     	     	
    	return input;
     }

}
