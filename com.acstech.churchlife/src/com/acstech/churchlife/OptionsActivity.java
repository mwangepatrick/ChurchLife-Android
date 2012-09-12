package com.acstech.churchlife;

import com.acstech.churchlife.exceptionhandling.ExceptionHelper;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/** 
 * This activity extends the base Activity and is used to provide an 'Options' menu
 *   when the user clicks the hard menu button.  Extend this class instead of Activity
 *   to provide the user with the application menu options (like preferences, signout, etc.)
 *   
 *   FUTURE:  Have each activity hold an array of menu itesm to add/remove.  Poor practice
 *              to have a hardcoded activity name in this class.
 *     
 * @author softwarearchitect
 *
 */
public class OptionsActivity extends Activity {

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);

		try
		{
			// If login activity is the current activity, no need for SignOut, etc.		
			if (this.getLocalClassName().equals("LoginActivity")){ 
				menu.removeItem(R.id.signout);
			}
						
			// Only show the 'select url' settings if in 'developer' mode				
			AppPreferences prefs = new AppPreferences(getApplicationContext());
			if (prefs.getDeveloperMode() == false) {
				menu.removeItem(R.id.settings);
			}
						
		}
    	catch (Exception e) {
    		ExceptionHelper.notifyUsers(e, OptionsActivity.this);
    		ExceptionHelper.notifyNonUsers(e);   
    	}		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
	    if (item.getItemId() == R.id.settings) {
			Intent settingsIntent = new Intent(getBaseContext(), Preferences.class);
			startActivity(settingsIntent);
			return true;
			
		} else if (item.getItemId() == R.id.signout) {
			this.finish();
			Intent loginIntent = new Intent(getBaseContext(), LoginActivity.class);
			loginIntent.putExtra("logout", true);
			startActivity(loginIntent);
			return true;
			
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
	
}
