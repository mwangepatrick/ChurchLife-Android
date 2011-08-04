package com.acstechnologies.churchlifev2;

import com.acstechnologies.churchlifev2.R;

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
 *   
 * @author softwarearchitect
 *
 */
public class OptionsActivity extends Activity {

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    
	    case R.id.settings:
	    	Intent settingsIntent = new Intent(getBaseContext(), Preferences.class);
			startActivity(settingsIntent);
	        return true;
	        
	    case R.id.signout:	    	
	    	this.finish();
	    	Intent loginIntent = new Intent(getBaseContext(), LoginActivity.class);			
	    	loginIntent.putExtra("logout", true);//shortcut to bundle		
			startActivity(loginIntent);
	        return true;
	        
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
}
