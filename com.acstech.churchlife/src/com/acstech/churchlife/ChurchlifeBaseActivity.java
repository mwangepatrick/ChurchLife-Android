package com.acstech.churchlife;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.acstech.churchlife.exceptionhandling.ExceptionHelper;
import com.acstech.churchlife.listhandling.DefaultListItem;
import com.acstech.churchlife.listhandling.DefaultListItemAdapter;
import com.acstech.churchlife.webservice.CoreAcsUser;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class ChurchlifeBaseActivity extends SlidingFragmentActivity {
	
	FrameLayout menuHeader;
	ListView menuListView;
	
	// default - display title bar (overridden if necessary)
	public boolean showTitleBar() {
		return true;
	}
	
	// default - display title bar (overridden if necessary)
	public boolean showLeftMenu() {
		return true;
	}
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	    	
    	//setTheme(R.style.Theme_Sherlock);    	
    	setTheme(R.style.Theme_MyTheme);
    	
    	super.onCreate(savedInstanceState);
    
    	
    	//est
    	//TypedValue tv = new TypedValue();    	
    	//this.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
    	//int actionBarHeight = getResources().getDimensionPixelSize(tv.resourceId);
    	
    	
    	//---------------------------------------
    	// show/hide title bar
    	//---------------------------------------
    	if (showTitleBar()) {
    		getSupportActionBar().show(); 
    	} 
    	else {
    		getSupportActionBar().hide();
    	}
    	
    	//---------------------------------------
    	// show/hide sliding menu    	
    	//---------------------------------------
    	if (showLeftMenu()) {
    		setBehindContentView(R.layout.menulist);
        	
    		bindControls();	
    		loadMenuItems();
    		 
    		//menuHeader.setMinimumHeight(getSupportActionBar().getHeight()+50);
    		
            // Wire up menu OnClick
            menuListView.setOnItemClickListener(new OnItemClickListener() {
            	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {                 	 
                	 DefaultListItem item = (DefaultListItem)parent.getAdapter().getItem(position);                	                	
                	 listMenuItemSelected(item);
                }
            });
        		    	
        	// setup 'sliding' portion of menu control
        	SlidingMenu sm = getSlidingMenu();
    		sm.setShadowWidthRes(R.dimen.shadow_width);
    		sm.setShadowDrawable(R.drawable.shadow);
    		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
    		sm.setFadeDegree(0.35f);
    		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
    		
    		getSupportActionBar().setHomeButtonEnabled(true);
    		getSupportActionBar().setIcon(R.drawable.ic_menu);
    		
    		sm.setSlidingEnabled(true);    		
    	}
    	else    		
    	{
    		setBehindContentView(R.layout.splashscreen);
    		getSlidingMenu().setSlidingEnabled(false);
    	}
    	
    }

    
    //************************************************************************
    //						LIST MENU (sliding)
    //************************************************************************
    
    /**
     *  Links state variables to their respective form controls
     */
    private void bindControls(){
    	menuHeader = (FrameLayout)this.findViewById(R.id.menuHeader);
    	menuListView = (ListView)this.findViewById(R.id.menuListView01);
    }
    
    /**
     * Load navigation menu options for slide-in left menu 
     */
    private void loadMenuItems() {
    	    	
		GlobalState gs = GlobalState.getInstance(); 		
    	ArrayList<DefaultListItem> itemList = new ArrayList<DefaultListItem>();
    	
    	itemList.add(new DefaultListItem(getResources().getString(R.string.Menu_People), R.drawable.ic_action_people)); 
    	itemList.add(new DefaultListItem(getResources().getString(R.string.Menu_Calendar), R.drawable.ic_action_calendar));
    	
    	// My ToDos - check security
    	if (gs.getUser().HasPermission(CoreAcsUser.PERMISSION_ASSIGNEDCONTACTSONLY) == true) {
        	itemList.add(new DefaultListItem(getResources().getString(R.string.Menu_Connections), R.drawable.ic_action_todo));
    	}
    	
    	itemList.add(new DefaultListItem(getResources().getString(R.string.Menu_MyInfo), R.drawable.ic_action_info));
    	
    	menuListView.setAdapter(new DefaultListItemAdapter(this, itemList, R.layout.listitem_withicon));			   
    }
    
    /**
     * Operate on a seleted navigation item by loading the intent 
     *   (and finishing the current intent)
     */
    private void listMenuItemSelected(DefaultListItem item) {
    	Intent intent = null;
    	boolean closeCurrentActivity = true;
    	
    	if (item.getTitle().equals(getResources().getString(R.string.Menu_People))) {
    		intent = new Intent().setClass(this, IndividualListActivity.class);  	
    	}
    	else if (item.getTitle().equals(getResources().getString(R.string.Menu_Calendar))) {
    		intent = new Intent().setClass(this, EventListActivity.class);
    	}
    	else if (item.getTitle().equals(getResources().getString(R.string.Menu_Connections))) {
    		intent = new Intent().setClass(this, AssignmentSummaryListActivity.class);
    	}
    	else if (item.getTitle().equals(getResources().getString(R.string.Menu_MyInfo))) {
    		intent = new Intent().setClass(this, MyInfoActivity.class);
    		// special case:  do not close the activity if this is the my info splash screen
        	//    as it does not have a title bar
    		closeCurrentActivity = false;
    	}    	
    	        	
    	if (closeCurrentActivity) {
    		finish(); // ensures the user cannot use the 'back' to this activity
    	}
		startActivity(intent);	// launch intent    	
    }

    
    //************************************************************************
    //							OPTIONS MENU
    //************************************************************************
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getSupportMenuInflater().inflate(R.menu.menu, menu);
		
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
    		ExceptionHelper.notifyUsers(e, ChurchlifeBaseActivity.this);
    		ExceptionHelper.notifyNonUsers(e);   
    	}		
		return true;
		
	}
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			return true;
			
		case R.id.settings:
			Intent settingsIntent = new Intent(getBaseContext(), Preferences.class);
			startActivity(settingsIntent);
			return true;
			
		case R.id.signout:
			this.finish();
			Intent loginIntent = new Intent(getBaseContext(), LoginActivity.class);
			loginIntent.putExtra("logout", true);
			startActivity(loginIntent);
			return true;		
		}
		
		return super.onOptionsItemSelected(item);		
	}

}