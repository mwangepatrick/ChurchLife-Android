package com.acstechnologies.churchlifev2;

import com.acstechnologies.churchlifev2.R;

import android.os.Bundle;  
import android.preference.EditTextPreference;
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
        PreferenceCategory mCategory = (PreferenceCategory) findPreference("category_authentication");
        
        EditTextPreference mPref = (EditTextPreference) findPreference("auth1");        
        mCategory.removePreference(mPref);
        
        mPref = (EditTextPreference) findPreference("auth2");  
        mCategory.removePreference(mPref);
        
        mPref = (EditTextPreference) findPreference("auth3");  
        mCategory.removePreference(mPref);     
        
        //no way to remove the entire category programmatically.
        // TODO revisit
        //http://stackoverflow.com/questions/5605520/how-remove-preferencecategory-programmatically
                
    }              
}  
