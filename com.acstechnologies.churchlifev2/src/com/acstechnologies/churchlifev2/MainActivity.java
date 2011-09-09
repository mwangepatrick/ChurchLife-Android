package com.acstechnologies.churchlifev2;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

import com.acstechnologies.churchlifev2.exceptionhandling.ExceptionHelper;

/**
 *  This activity hosts the tabhost/tabwidget for navigation between the 
 *    various activities for this application (people search, calendar, etc.)
 *    
 * @author softwarearchitect
 *
 */
public class MainActivity extends TabActivity {

	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                      
        try
        {                                        
            setContentView(R.layout.main);		             

            Resources res = getResources(); 	// Resource object to get Drawables
            final TabHost tabHost = getTabHost();  	// The activity TabHost
            TabHost.TabSpec spec;  				// Resusable TabSpec for each tab
            Intent intent;  					// Reusable Intent for each tab
            
            // Initialize a TabSpec for each tab and add it to the TabHost
            
            // TAB - People
            intent = new Intent().setClass(this, IndividualListActivity.class);
            spec = tabHost.newTabSpec("people").setIndicator("People", res.getDrawable(R.drawable.ic_tab_people))
                          .setContent(intent);
            tabHost.addTab(spec);

            // TAB - Calendar
            intent = new Intent().setClass(this, EventListActivity.class);
            spec = tabHost.newTabSpec("calendar").setIndicator("Calendar", res.getDrawable(R.drawable.ic_tab_calendar))
                          .setContent(intent);
            tabHost.addTab(spec);

            // Tab - My Info
            intent = new Intent().setClass(this, MyInfoActivity.class);
            spec = tabHost.newTabSpec("myinfo").setIndicator("My Church", res.getDrawable(R.drawable.ic_tab_info))
                          .setContent(intent);
            tabHost.addTab(spec);

            
            // Close the soft input keyboard every time the user changes to a different tab
            tabHost.setOnTabChangedListener(new OnTabChangeListener()
            {
            	public void onTabChanged(String tabId)
                {
            		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            		imm.hideSoftInputFromWindow(tabHost.getApplicationWindowToken(), 0);
                }
            });
                       
            tabHost.setCurrentTab(0);
            
        }
    	catch (Exception e) {
    		ExceptionHelper.notifyUsers(e, MainActivity.this);
    		ExceptionHelper.notifyNonUsers(e);
    	}       
    }
    
    
}
