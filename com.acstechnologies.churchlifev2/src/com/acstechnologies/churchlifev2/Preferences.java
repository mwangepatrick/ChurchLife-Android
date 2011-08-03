package com.acstechnologies.churchlifev2;

import com.acstechnologies.churchlifev2.R;

import android.os.Bundle;  
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;  
import android.preference.PreferenceCategory;
   
public class Preferences extends PreferenceActivity {  
  
    /** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        addPreferencesFromResource(R.xml.preferences);
                
        // Remove the authentication preferences (user cannot set these values from the preferences window)               
        PreferenceCategory mCategory = (PreferenceCategory) findPreference("category_authentication");
        
        EditTextPreference mPref = (EditTextPreference) findPreference("auth1");        
        mCategory.removePreference(mPref);
        
        mPref = (EditTextPreference) findPreference("auth2");  
        mCategory.removePreference(mPref);
        
        mPref = (EditTextPreference) findPreference("auth3");  
        mCategory.removePreference(mPref);               
    }              
}  
