package com.acstech.churchlife;

import com.acstech.churchlife.R;

import android.os.Bundle;  
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;  
import android.preference.PreferenceCategory;
   
/**
 * This activity created from a selection of a menu item on the application
 *   options menu (see OptionsActivity).  This class exists to remove from
 *   preferences the 'remember me' authentication credentials so that the
 *   user does not have access to see them (they are encrypted) on the preferences
 *   edit form.
 *   
 * @author softwarearchitect
 *
 */
public class Preferences extends PreferenceActivity {  
  
    /** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        addPreferencesFromResource(R.xml.preferences);
                
        // Remove the authentication preferences (user cannot set these values from the preferences window)               
        PreferenceCategory mCategory = (PreferenceCategory) findPreference("category_application");
        
        EditTextPreference mPref = (EditTextPreference) findPreference("auth1");        
        mCategory.removePreference(mPref);
        
        mPref = (EditTextPreference) findPreference("auth2");  
        mCategory.removePreference(mPref);
        
        mPref = (EditTextPreference) findPreference("auth3");  
        mCategory.removePreference(mPref);
        
        // listen for changes to developer mode
        CheckBoxPreference cPref = (CheckBoxPreference) findPreference("developermode");  
        cPref.setOnPreferenceChangeListener(checkboxListener);
                      
    }   
    
    // check box change - developer mode.  if false, set url back to default
    private OnPreferenceChangeListener checkboxListener = new OnPreferenceChangeListener() {

        public boolean onPreferenceChange(Preference preference, Object newValue) {
            
        	if (preference.getKey().equals("developermode")) {
        		if (newValue.equals(Boolean.FALSE)) {
        			
        			// keep the logic for setting url in AppPreferences class
        			AppPreferences prefs = new AppPreferences(getApplicationContext());
        			prefs.setWebServiceUrl(config.CORE_SERVICE_URL_VALUE);        			
        		}
        	}        	
            return true;
        }
    };

    
}  
