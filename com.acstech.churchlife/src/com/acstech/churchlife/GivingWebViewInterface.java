package com.acstech.churchlife;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class GivingWebViewInterface {
	
    Context _context;

    /** Instantiate the interface and set the context */
    GivingWebViewInterface(Context c) {
        _context = c;
    }

    /** Show a toast from the web page */
    //@JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(_context, toast, Toast.LENGTH_SHORT).show();
    }

    //@JavascriptInterface
    public void givingComplete() {
    	
    	Activity activity = (Activity) _context;
    
    	/*
    	// back to the people search
    	Intent intent = new Intent().setClass(_context, IndividualListActivity.class); 
    	activity.startActivity(intent);
    	*/
    	
    	// close this activity    
    	activity.finish();    	
    }
    
    
}