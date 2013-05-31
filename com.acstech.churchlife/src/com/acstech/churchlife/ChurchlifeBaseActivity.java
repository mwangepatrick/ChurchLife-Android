package com.acstech.churchlife;

import java.util.ArrayList;

import ly.count.android.api.Countly;

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
	
	CoreAcsUser _currentUser;		// lazy loaded
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
	
	public CoreAcsUser getCurrentUser() {
		if (_currentUser == null) {
			GlobalState gs = GlobalState.getInstance();    	
	    	_currentUser = gs.getUser();	
		}
		return _currentUser;
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

    /**
     * Initialize the countly analytics - called once on app startup (splash screen) 
     */
    protected void initializeCountlyAnalytics() {
    	Countly.sharedInstance().init(this, config.COUNTLY_URL_VALUE, config.COUNTLY_APPLICATION_ID);
    }
    
    @Override
	protected void onStart() {
		super.onStart();
		
		Countly.sharedInstance().onStart();		
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		
		getSlidingMenu().showContent();		
    }
    
    protected void onStop() {
    	super.onStop();
    	
    	Countly.sharedInstance().onStop();
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
    	
		// People
		if (showPeopleMenu()) {
			itemList.add(new DefaultListItem(getResources().getString(R.string.Menu_People), R.drawable.ic_action_people_color)); 
		}
    	
		// Organizations
		if (showOrganizationMenu()) {
			itemList.add(new DefaultListItem(getResources().getString(R.string.Menu_Organization), R.drawable.ic_action_people_color)); 
		}
				
		// Calendar
		if (showCalendarMenu()) {
			itemList.add(new DefaultListItem(getResources().getString(R.string.Menu_Calendar), R.drawable.ic_action_calendar_color));
		}
		
    	// My ToDos 
    	if (showMyToDoMenu()) {
        	itemList.add(new DefaultListItem(getResources().getString(R.string.Menu_Connections), R.drawable.ic_action_todo_color));
    	}
    	
    	// Giving    	    	
    	if (showGivingMenu()) {
        	itemList.add(new DefaultListItem(getResources().getString(R.string.Menu_Giving), R.drawable.ic_action_giving_color));
    	}
    	
    	itemList.add(new DefaultListItem(getResources().getString(R.string.Menu_MyInfo), R.drawable.ic_action_info_color));
    	
    	menuListView.setAdapter(new DefaultListItemAdapter(this, itemList, R.layout.listitem_withicon));			   
    }
    

    /**
     * Logic for displaying the People menu
     * @return
     */
    private boolean showPeopleMenu() {
    	boolean result = true;  //by default, show it
    	
    	//user must NOT be a nonmember / user must be a member and must have certain security bits
    	if (getCurrentUser().SecurityRole.equals(CoreAcsUser.SECURITYROLE_NONMEMBER) == true ||
    		(getCurrentUser().HasPermission(CoreAcsUser.PERMISSION_MEMBERSEARCH) == false && getCurrentUser().HasPermission(CoreAcsUser.PERMISSION_LOCATEPERSON) == false) ) {
    			
    		result = false;    		
    	}
    	return result;    	
    }
    
    /**
     * Logic for displaying the Organization menu
     * @return
     */
    private boolean showOrganizationMenu() {
    	boolean result = false;  //by default, hide it
    
    	if (getCurrentUser().OrgsAvailable &&
    	   (getCurrentUser().HasPermission(CoreAcsUser.PERMISSION_LOCATEORGANIZATIONS) && getCurrentUser().HasPermission(CoreAcsUser.PERMISSION_VIEWORGANIZATIONRECORD)) ) {
    			
    		result = true;    		
    	}
    	return result;    	
    }
    
    /**
     * Logic for displaying the Calendar menu
     * @return
     */
    private boolean showCalendarMenu() {
    	
    	boolean result = true;  //by default, show it
    	
    	// check 1 - user must NOT be a nonmember / user must be a member
    	if (getCurrentUser().SecurityRole.equals(CoreAcsUser.SECURITYROLE_NONMEMBER)) {
    		result = false;    		
    	}
    	
    	// check 2 - login must have calendar enabled (only check if previous checks passed) 
    	if (result == true)	{
    		result = !getCurrentUser().CalendarDisabled;
    	}
    	
    	return result;
    }
    

    /**
     * Logic for displaying the My ToDos menu
     * @return
     */
    private boolean showMyToDoMenu() {	    	
    	return (getCurrentUser().HasPermission(CoreAcsUser.PERMISSION_ASSIGNEDCONTACTSONLY) == true && getCurrentUser().IndvId > 0);
    }
    
    /**
     * Logic for displaying the giving menu
     * @return
     */
    private boolean showGivingMenu() {
    	
    	boolean result = true;    	
    	CoreAccountMerchant merchantInfo = getCurrentUser().getMerchantInfo();
    	
    	// must have valid merchant info
    	if (merchantInfo == null) {
    		result = false;
    	}
    	else {
    		if (merchantInfo.AllowACH == false && merchantInfo.AllowCreditDebitCards == false && merchantInfo.DebitCardsOnly == false) {
    			result = false;
    		}	
    	}
    	
    	// only check if result is not already false
    	if (result == true) {		
	    	// if user is staff or admin, they must have an indivdiual id
	    	if (getCurrentUser().SecurityRole.equals(CoreAcsUser.SECURITYROLE_STAFF) || getCurrentUser().SecurityRole.equals(CoreAcsUser.SECURITYROLE_ADMINISTRATOR)) {
	    		if (getCurrentUser().IndvId <= 0) {
	    			result = false;
	    		}
	    	}
    	}
    	    	
    	// only check if result is not already false
    	if (result == true) {
    		// non-member roles must be allowed via merchant info
    		if (getCurrentUser().SecurityRole.equals(CoreAcsUser.SECURITYROLE_NONMEMBER) && merchantInfo.NonMemberGivingEnabled == false) {
	    		result = false;
	    	}
    	}
    	    	   
    	return result;
    }
    
    
    /**
     * public interface for an activity to 'select' a main menu item
     * 
     */
    public void selectMenuItem(String menuName) {
    	listMenuItemSelected( new DefaultListItem(menuName));
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
    	else if (item.getTitle().equals(getResources().getString(R.string.Menu_Organization))) {
    		intent = new Intent().setClass(this, OrganizationListActivity.class);
    	}
    	else if (item.getTitle().equals(getResources().getString(R.string.Menu_Calendar))) {
    		intent = new Intent().setClass(this, EventListActivity.class);
    	}
    	else if (item.getTitle().equals(getResources().getString(R.string.Menu_Connections))) {
    		intent = new Intent().setClass(this, AssignmentSummaryListActivity.class);
    	}
    	else if (item.getTitle().equals(getResources().getString(R.string.Menu_Giving))) {
    		intent = new Intent().setClass(this, GivingActivity.class);
    	}
    	else if (item.getTitle().equals(getResources().getString(R.string.Menu_MyInfo))) {
    		intent = new Intent().setClass(this, MyInfoActivity.class);    		
    	}    	
    	        	
    	if (closeCurrentActivity) {
    		// clear all open activities since we are starting with a new 'root' activity
       		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
    	}
		startActivity(intent);	// launch intent    	
    }


    
    // return the root activity based on permissions, etc.
    public Class<?> getHomeActivity() {

    	if (getCurrentUser().SecurityRole.equals(CoreAcsUser.SECURITYROLE_NONMEMBER)) {
    	
    		// if a non-member, show Tasks or just info
    		if (showMyToDoMenu()) {
        		return AssignmentSummaryListActivity.class;    			
    		}
    		else {
        		return MyInfoActivity.class;
    		}
    	}
    	else {
    		if (showPeopleMenu())
    		{
    			return IndividualListActivity.class;
    		}
    		else
    		{
    			return MyInfoActivity.class;
    		}
    	}    		
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
