package com.acstech.churchlife;


import java.util.ArrayList;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionHelper;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;
import com.acstech.churchlife.listhandling.IndividualListItem;
import com.acstech.churchlife.listhandling.IndividualListItemAdapter;
import com.acstech.churchlife.webservice.AddressBase;
import com.acstech.churchlife.webservice.ApiOrganizations;
import com.acstech.churchlife.webservice.CoreAcsUser;
import com.acstech.churchlife.webservice.CoreOrganizationDetail;
import com.acstech.churchlife.webservice.EmailBase;
import com.acstech.churchlife.webservice.PhoneBase;

public class OrganizationActivity  extends ChurchlifeBaseActivity {

	static final int DIALOG_PROGRESS = 0;
	
	TextView nameTextView;
	ListView detailsListview;
	
	private float _lastXpercent = 0;				// last x position % touched (for use with phone/text message list item)
	
	private String _orgName;
	CoreOrganizationDetail _organization;			// filled by web service
	 
	private ProgressDialog _progressD;
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
		 
		 super.onCreate(savedInstanceState);
	
		 try
		 {	
			 setTitle(R.string.Organization_Title);    	        
			 setContentView(R.layout.organization); 
			 bindControls();
			 
        	 // This activity MUST be passed data
        	 Bundle extraBundle = this.getIntent().getExtras();
             if (extraBundle == null) {
            	 throw AppException.AppExceptionFactory(
            			 ExceptionInfo.TYPE.UNEXPECTED,
						 ExceptionInfo.SEVERITY.CRITICAL, 
						 "100",           												    
						 "OrganizationActivity.onCreate",
						 "No data was passed to the Organization activity.");
             }
             else {            	
            	 int orgId = extraBundle.getInt("orgid");            	 
            	 _orgName = extraBundle.getString("orgname");                 	 
            	 loadDataWithProgressDialog(orgId);            	    
             }		                         
		}
	 	catch (Exception e) {
	 		ExceptionHelper.notifyUsers(e, OrganizationActivity.this);
	 		ExceptionHelper.notifyNonUsers(e);
	 	}  		
	 }
	 
	 protected Dialog onCreateDialog(int id) {
        switch(id) {
        case DIALOG_PROGRESS:
        	_progressD = new ProgressDialog(OrganizationActivity.this);
        	String msg = String.format(getString(R.string.Dialog_LoadingWithTitle), _orgName);	
        	
        	_progressD.setMessage(msg);        	
        	_progressD.setIndeterminate(true);
        	_progressD.setCancelable(false);
    		return _progressD;	  
        default:
            return null;
        }
	 }

	    
	 /**
	 *  Links state variables to their respective form controls
	 */
	 private void bindControls() {		
		nameTextView = (TextView)this.findViewById(R.id.nameTextView);
		detailsListview = (ListView)this.findViewById(R.id.detailsListview);
		
		// store off the last x touch (in percent of the total width) so that we know what to do 
		//  on a phone entry (call the number of text the number) based on the touch position.
		detailsListview.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {		
				_lastXpercent = event.getX() / v.getWidth();
				return false;
			}
        });
		
		detailsListview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	IndividualListItem item = (IndividualListItem)parent.getAdapter().getItem(position);
            	doAction(item.getActionTag());            	
            }
        });  
				
	 }
	 
	 /**
     * Displays a progress dialog and launches a background thread to connect to a web service
     *   to retrieve a SINGLE connection. 
     *   
     */
    private void loadDataWithProgressDialog(final int orgId)
    {               	    			   
    	showDialog(DIALOG_PROGRESS);
    	
    	// This handler is called once the lookup is complete.  It looks at the data returned from the
    	//  thread (in the Message) to determine success/failure.  If successful, the values are
    	//  bound to this activity's layout controls
    	final Handler handler = new Handler() {
    		public void handleMessage(Message msg) {
    		  
    			removeDialog(DIALOG_PROGRESS);
    			
    			try {
	    			if (msg.what == 0) {		    				
	    				_organization = CoreOrganizationDetail.GetCoreOrganizationDetail(msg.getData().getString("organization"));	    					    			
	    				bindData();		    					    			
	       			}
	       			else if (msg.what < 0) {
	       				// If < 0, the exception details are in the message bundle.  Throw it 
	       				//  and let the exception handler (below) handle it	       				
	       				Bundle b = msg.getData();
	       				throw ExceptionHelper.getAppExceptionFromBundle(b, "loadDataWithProgressDialog.handleMessage");	       				
	       			}	    				
    			} 			
    			catch (Exception e) {  				
    					ExceptionHelper.notifyUsers(e, OrganizationActivity.this);
    	    			ExceptionHelper.notifyNonUsers(e);
    				}
    			}    			    		
    	};
    	
    	Thread searchThread = new Thread() {  
    		public void run() {
    			try {    				    				    				
    				GlobalState gs = GlobalState.getInstance(); 		
    				AppPreferences appPrefs = new AppPreferences(getApplicationContext());		
    				ApiOrganizations api = new ApiOrganizations(appPrefs.getWebServiceUrl(), config.APPLICATION_ID_VALUE, gs.getSiteNumberInt(), gs.getUserName(),  gs.getPassword());
    				
    			 	_organization = api.organization(orgId);
    			 	
	    	    	// Return the response object (as string) to the message handler above
	    	    	Message msg = handler.obtainMessage();		
	    	    	msg.what = 0;
	    	    	
	    	    	Bundle b = new Bundle();
    				b.putString("organization", _organization.toJsonString());   
    				msg.setData(b);
    						    	    		    	    			    	  
	    	    	handler.sendMessage(msg);	    	    	
    			}
    			catch (Exception e) {    				
    				ExceptionHelper.notifyNonUsers(e);			// Log the full error,     				
    				
    				Message msg = handler.obtainMessage();
    				msg.what = -1;
    				msg.setData(ExceptionHelper.getBundleForException(e));	
    				handler.sendMessage(msg);    	    				
    			}    			       	    	    	    	
    		 }
    	};
    	searchThread.start();    	
    }

	 
	 /**
	  * Called after webservice retrieves the (class level variable) object.  
	  *   Sets control properties
	 * @throws AppException 
	 */
	 private void bindData() throws AppException{
		// add the list footer view here (must be added before setting list adapter)
	    addFooterView();
	    
	    nameTextView.setText(_organization.RefName);
	    
	    // list items (demographics, addresses, emails, phones)	    
	    ArrayList<IndividualListItem> listItems = new ArrayList<IndividualListItem>();
	    listItems.add(IndividualListItem.NewSimpleItem("Name", _organization.Name));	    
	    listItems.add(IndividualListItem.NewSimpleItem("Level", _organization.LevelTypeDesc));
	    listItems.add(IndividualListItem.NewSimpleItem("Parent", _organization.ParentName));
	    listItems.add(IndividualListItem.NewSimpleItem("Primary Contact", _organization.PrimaryContactName));
	    	    
	    // Phone Numbers
	 	for (PhoneBase phone : _organization.Phones) {			
 			listItems.add(IndividualListItem.NewPhoneItem(OrganizationActivity.this, phone));					
 		}
 				
 		// Email addresses
 		for (EmailBase email : _organization.Emails) {
 			listItems.add(IndividualListItem.NewEmailItem(OrganizationActivity.this, email));
 		}
 				
 		// Physical addresses
 		for (AddressBase address : _organization.Addresses) {
 			listItems.add(IndividualListItem.NewAddressItem(OrganizationActivity.this, address));
 		}
 		
		detailsListview.setAdapter(new IndividualListItemAdapter(this, listItems));							
	 }
	 
	 
	 private void addFooterView() {	
    	
		 if (getCurrentUser().HasPermission(CoreAcsUser.PERMISSION_VIEWSTAFFROSTER)) {
			 TextView tv = new TextView(this);
			 tv.setClickable(true);
			 tv.setFocusable(true);
			 tv.setTextAppearance(this, android.R.attr.textAppearanceMedium);
			 tv.setTextSize(19);
			 tv.setTextColor(Color.WHITE);
			 tv.setText("Staff");
			 tv.setPadding(5, 12, 0, 12);
			 
			 tv.setOnClickListener(new OnClickListener() {		
				 public void onClick(View v) {	        		             	
					try {
						startStaffActivity();
					}
					catch (Exception e) {  				
	 					ExceptionHelper.notifyUsers(e, OrganizationActivity.this);
	 	    			ExceptionHelper.notifyNonUsers(e);
	 				}	        		        	
				 }		
			 }); 
			 
			 detailsListview.addFooterView(tv);			 
		 }		
    }

	 /**
     * actionTag must be in the format "command:data"
     * 
     * Ex.  phone:(888)123-0987
     *      sms:(888)123-0987
     *      map:123 Treeview Drive, Atlanta, GA 30041
     *      
     * @param actionTag
     */
    private void doAction(String actionTag) {
    
    	if (actionTag != null && actionTag.trim().length() > 0)
    	{	    		   
	    	ExternalActivityHelper activityLauncher = new ExternalActivityHelper(OrganizationActivity.this);
	    	
	    	// get the command
	    	String command = actionTag.substring(0, actionTag.indexOf(":"));
	    	String argument = actionTag.substring(actionTag.indexOf(":")+1);
	    	
	    	if (command.equals("phone")) {
	    		
	    		/* not currently used.
	    		 * 
	    		// ask user to text or dial the number
	        	Bundle args = new Bundle();
	        	args.putString("phonenumber", argument);      
	        	showDialog(DIALOG_PHONE_SELECT, args); 
	    		*/
	    		
	    		// if the user touched the first 85% of the item dial 
	    		//  the phone number; otherwise, do text messaging
	    		if (_lastXpercent <= .85) {
	    			activityLauncher.callPhoneNumber(argument);
	    		}
	    		else {
	    			activityLauncher.sendTextMessage(argument);
	    		}    		
	        	        	        	    		    		    		
	    	}else if (command.equals("phonedial")) {    		    		
	    		activityLauncher.callPhoneNumber(argument);
	    		
	    	}else if (command.equals("phonesms")) {
	    		activityLauncher.sendTextMessage(argument);
	    		
	    	}else if (command.equals("email")) {
	    		activityLauncher.sendEmail(argument);
	    		
	    	}else if (command.equals("map")) {
	    		activityLauncher.mapAddress(argument);	 
			}    	
	    	//else do nothing...command is not known
    	}
    }
	    
    
    /**
     * Display the assignment screen
     * 
     * @throws AppException 
     */
    private void startStaffActivity() throws AppException {
    	/*
    	Intent intent = new Intent();
    	intent.setClass(this, StaffActivity.class);
	 	intent.putExtra("orgid", _organization.OrgId);
	 	
	 	startActivity(intent);	  		 			
	 	finish();					// always close this activity
	 	*/		 			 		    
    }
    	    
    	    
}
