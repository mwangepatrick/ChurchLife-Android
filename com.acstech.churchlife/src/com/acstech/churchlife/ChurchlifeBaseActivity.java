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
import com.acstech.churchlife.webservice.CoreAccountMerchant;
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
    	    	
		ArrayList<DefaultListItem> itemList = new ArrayList<DefaultListItem>();
    	
    	itemList.add(new DefaultListItem(getResources().getString(R.string.Menu_People), R.drawable.ic_action_people)); 
    	itemList.add(new DefaultListItem(getResources().getString(R.string.Menu_Calendar), R.drawable.ic_action_calendar));
    	
    	// My ToDos - check security
    	if (showMyToDoMenu()) {
        	itemList.add(new DefaultListItem(getResources().getString(R.string.Menu_Connections), R.drawable.ic_action_todo));
    	}
    	
    	// Giving - check security    	    	
    	if (showGivingMenu()) {
        	itemList.add(new DefaultListItem(getResources().getString(R.string.Menu_Giving), R.drawable.ic_action_giving));
    	}
    	
    	itemList.add(new DefaultListItem(getResources().getString(R.string.Menu_MyInfo), R.drawable.ic_action_info));
    	
    	menuListView.setAdapter(new DefaultListItemAdapter(this, itemList, R.layout.listitem_withicon));			   
    }
    


    /**
     * Logic for displaying the My ToDos menu
     * @return
     */
    private boolean showMyToDoMenu() {	
    	GlobalState gs = GlobalState.getInstance();    	
    	return (gs.getUser().HasPermission(CoreAcsUser.PERMISSION_ASSIGNEDCONTACTSONLY) == true && gs.getUser().IndvId > 0);
    }
    
    /**
     * Logic for displaying the giving menu
     * @return
     */
    private boolean showGivingMenu() {
    	
    	boolean result = true;    	
    	GlobalState gs = GlobalState.getInstance(); 		
    	CoreAccountMerchant merchantInfo = gs.getUser().getMerchantInfo();
    	
    	// must have valid merchant info
    	if (merchantInfo == null) {
    		result = false;
    	}
    	else {
    		if (merchantInfo.AllowACH == false && merchantInfo.AllowCreditDebitCards == false && merchantInfo.AllowACH == false) {
    			result = false;
    		}	
    	}
    	
    	// only check if result is not already false
    	if (result == true) {		
	    	// if user is staff or admin, they must have an indivdiual id
	    	if (gs.getUser().SecurityRole.equals(CoreAcsUser.SECURITYROLE_STAFF) || gs.getUser().SecurityRole.equals(CoreAcsUser.SECURITYROLE_ADMINISTRATOR)) {
	    		if (gs.getUser().IndvId <= 0) {
	    			result = false;
	    		}
	    	}
    	}
    	    	
    	// only check if result is not already false
    	if (result == true) {
    		// non-member roles must be allowed via merchant info
    		if (gs.getUser().SecurityRole.equals(CoreAcsUser.SECURITYROLE_NONMEMBER) && merchantInfo.NonMemberGivingEnabled == false) {
	    		result = false;
	    	}
    	}
    	    	   
    	return result;
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
    	else if (item.getTitle().equals(getResources().getString(R.string.Menu_Giving))) {
    		intent = new Intent().setClass(this, WebViewActivity.class);
    	}
    	else if (item.getTitle().equals(getResources().getString(R.string.Menu_MyInfo))) {
    		intent = new Intent().setClass(this, MyInfoActivity.class);
    		// special case:  do not close the activity if this is the my info splash screen
        	//    as it does not have a title bar
    		closeCurrentActivity = false;
    	}    	
    	        	
    	if (closeCurrentActivity) {
    		// clear all open activities since we are starting with a new 'root' activity
    		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
