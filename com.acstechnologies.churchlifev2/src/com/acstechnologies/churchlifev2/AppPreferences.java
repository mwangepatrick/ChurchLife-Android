package com.acstechnologies.churchlifev2;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class AppPreferences {
	
     private static final String APP_SHARED_PREFS = "com.acstechnologies.churchlifev2_preferences"; //  Name of the file -.xml
     
     
     /**
      * This value SHOULD ALWAYS be changed and kept secret!
      */
     private static final String SEED = "##initialseedvalue##";
          
     private SharedPreferences appSharedPrefs;
     private Editor prefsEditor;

     public AppPreferences(Context context)
     {    	 
         this.appSharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);    	
         this.prefsEditor = appSharedPrefs.edit();
     }

    
     // Auth1 - ENCRYPTED
     public String getAuth1() throws Exception {    	   
         return getEncryptedValue("auth1");
     }
     public void setAuth1(String text) throws Exception {
    	 setEncryptedValue("auth1", text);
     }
     
     // auth2 - ENCRYPTED
     public String getAuth2() throws Exception {    	   
         return getEncryptedValue("auth2");
     }
     public void setAuth2(String text) throws Exception {
    	 setEncryptedValue("auth2", text);
     }
     
     // auth3 - ENCRYPTED
     public String getAuth3() throws Exception {    	   
         return getEncryptedValue("auth3");
     }
     public void setAuth3(String text) throws Exception {
    	 setEncryptedValue("auth3", text);
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
     public String getEncryptedValue(String keyName) throws Exception {
    	        
    	String result = "";
    	
 		String encryptedValue = appSharedPrefs.getString(keyName, "");
 		if (encryptedValue.length() > 0){ 				
 			result = SimpleCryptography.decrypt(SEED, encryptedValue);
 		} 		
        return result;        
     }
     
     public void setEncryptedValue(String keyName, String value) throws Exception {
    	    
    	String encryptedString = "";
    		
  		if (value.length() > 0){				
  			encryptedString = SimpleCryptography.encrypt(SEED, value);
  		}
  		prefsEditor.putString(keyName, encryptedString);
  	    prefsEditor.commit();   	    	     	
     }
     
}
