package com.acstechnologies.churchlifev2;

import com.acstechnologies.churchlifev2.exceptionhandling.AppException;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class AppPreferences {
	
    /**
     * This value SHOULD ALWAYS be changed to match your package.
     */
     private static final String APP_SHARED_PREFS = "com.acstechnologies.churchlifev2_preferences"; //  Name of the file -.xml
          
     private SharedPreferences appSharedPrefs;
     private Editor prefsEditor;

     public static String EncryptionSeedValue() {
    	 return config.SEED;
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
     
     // Web Service URI (base)
     public String getWebServiceUrl() {
    	 return appSharedPrefs.getString("serviceurl", "https://api.accessacs.com");
     }
     
     public void setWebServiceUrl(String url) {
    	 prefsEditor.putString("serviceurl", url);
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
     
     //Test     
     public String getTest() {
         return appSharedPrefs.getString("test", "");
     }

     public void setTest(String text) {
         prefsEditor.putString("test", text);
         prefsEditor.commit();
     }
     
     
          
     // Helper methods for encrypted keys
     public String getEncryptedValue(String keyName) throws AppException {
    	        
    	String result = "";
    	
 		String encryptedValue = appSharedPrefs.getString(keyName, "");
 		if (encryptedValue.length() > 0){ 				
 			result = SimpleCryptography.decrypt(EncryptionSeedValue(), encryptedValue);
 		} 		
        return result;        
     }
     
     public void setEncryptedValue(String keyName, String value) throws AppException {
    	    
    	String encryptedString = "";
    		
  		if (value.length() > 0){				
  			encryptedString = SimpleCryptography.encrypt(EncryptionSeedValue(), value);
  		}
  		prefsEditor.putString(keyName, encryptedString);
  	    prefsEditor.commit();   	    	     	
     }



}
