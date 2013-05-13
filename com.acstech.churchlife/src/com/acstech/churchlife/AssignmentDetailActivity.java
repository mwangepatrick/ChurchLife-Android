package com.acstech.churchlife;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionHelper;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;
import com.acstech.churchlife.listhandling.ConnectionTeamsListLoader;
import com.acstech.churchlife.webservice.Api;
import com.acstech.churchlife.webservice.CoreAcsUser;
import com.acstech.churchlife.webservice.CoreConnection;

public class AssignmentDetailActivity  extends ChurchlifeBaseActivity {

	//zzz revisit progress dialog to do with fragments
	static final int DIALOG_PROGRESS = 0;
	
	ImageView teamImageView;
	Button viewRecentButton;
	
	TextView contactTextView;
	TextView addressTextView;
	TextView phoneTextView;
	TextView emailTextView;
	
	TextView assignmentTextView;
	Button enterButton;
	Button reassignButton;

	String _title = "";	//passed to activity so it can be used in title, dialog 			
	CoreConnection _connection;
	
	AppPreferences _appPrefs; 
	private ProgressDialog _progressD;
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
		 
		 super.onCreate(savedInstanceState);
	
		 try
		 {
			 _appPrefs = new AppPreferences(getApplicationContext());
			 
			 setContentView(R.layout.assignmentdetail); 
			 bindControls();
			 
        	 // This activity MUST be passed data
        	 Bundle extraBundle = this.getIntent().getExtras();
             if (extraBundle == null) {
            	 throw AppException.AppExceptionFactory(
            			 ExceptionInfo.TYPE.UNEXPECTED,
						 ExceptionInfo.SEVERITY.CRITICAL, 
						 "100",           												    
						 "AssignmentDetailActivity.onCreate",
						 "No assignment was passed to the Assignment Detail activity.");
             }
             else {            	
            	 int assignmentId = extraBundle.getInt("assignmentid");            	 
            	 _title = extraBundle.getString("title");     
            	 setTitle(_title);
            	 loadDataWithProgressDialog(assignmentId);            	    
             }		                         
		}
	 	catch (Exception e) {
	 		ExceptionHelper.notifyUsers(e, AssignmentDetailActivity.this);
	 		ExceptionHelper.notifyNonUsers(e);
	 	}  		
	 }
	 
	    protected Dialog onCreateDialog(int id) {
	        switch(id) {
	        case DIALOG_PROGRESS:
	        	_progressD = new ProgressDialog(AssignmentDetailActivity.this);
	        	String msg = String.format(getString(R.string.Connection_LoadingWithTitle), _title);	
	        	
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
	 private void bindControls(){	    			 		
		 
		contactTextView = (TextView)this.findViewById(R.id.contactTextView);	
		
		addressTextView = (TextView)this.findViewById(R.id.addressTextView);
		addressTextView.setOnClickListener(new OnClickListener() {				 
			@Override
			public void onClick(View view) {
				 try {
					 // call or text
					 ExternalActivityHelper activityLauncher = new ExternalActivityHelper(AssignmentDetailActivity.this);
					 String addressString = (String)view.getTag();
		    		 activityLauncher.mapAddress(addressString);		  
				 }
				 catch (Exception e) {
				 		ExceptionHelper.notifyUsers(e, AssignmentDetailActivity.this);
				 		ExceptionHelper.notifyNonUsers(e);
				 }  
			}			
		});
		phoneTextView = (TextView)this.findViewById(R.id.phoneTextView);
		phoneTextView.setOnClickListener(new OnClickListener() {				 
			@Override
			public void onClick(View view) {
				 try {
					 // call or text
					 ExternalActivityHelper activityLauncher = new ExternalActivityHelper(AssignmentDetailActivity.this);
					 String phoneNumber = (String)view.getTag();
		    		 activityLauncher.callPhoneNumber(phoneNumber);		  
				 }
				 catch (Exception e) {
				 		ExceptionHelper.notifyUsers(e, AssignmentDetailActivity.this);
				 		ExceptionHelper.notifyNonUsers(e);
				 }  
			}			
		});
		
		emailTextView = (TextView)this.findViewById(R.id.emailTextView);
		emailTextView.setOnClickListener(new OnClickListener() {				 
			@Override
			public void onClick(View view) {
				 try {
					 // call or text
					 ExternalActivityHelper activityLauncher = new ExternalActivityHelper(AssignmentDetailActivity.this);
					 String emailAddress = (String)view.getTag();
		    		 activityLauncher.sendEmail(emailAddress);		  
				 }
				 catch (Exception e) {
				 		ExceptionHelper.notifyUsers(e, AssignmentDetailActivity.this);
				 		ExceptionHelper.notifyNonUsers(e);
				 }  
			}			
		});
				
		assignmentTextView = (TextView)this.findViewById(R.id.assignmentTextView);
		viewRecentButton = (Button)this.findViewById(R.id.viewRecentButton);
		teamImageView = (ImageView) this.findViewById(R.id.teamImageView);
		enterButton = (Button)this.findViewById(R.id.enterButton);
		reassignButton = (Button)this.findViewById(R.id.reassignButton);
				
		// Reassign - check permission and hide button IF the user does NOT have permission
		if (getCurrentUser().HasPermission(CoreAcsUser.PERMISSION_REASSIGNCONNECTION) == false) {
			reassignButton.setVisibility(View.GONE);
		}
		
		// View Recent History button - check permission and hide button
		if (getCurrentUser().HasPermission(CoreAcsUser.PERMISSION_VIEWOUTREACHHISTORY) == false) {
			viewRecentButton.setVisibility(View.INVISIBLE);
		}
		else
		{
			// view recent button click event             
			viewRecentButton.setOnClickListener(new OnClickListener() {				 
				 @Override
				public void onClick(View view) {
					 try {
						 startConnectionListActivity();
					 }
					 catch (Exception e) {
					 		ExceptionHelper.notifyUsers(e, AssignmentDetailActivity.this);
					 		ExceptionHelper.notifyNonUsers(e);
					 }
				}
			});
		}
		
		// View Team button (if this is a team connection it will be displayed)
		teamImageView.setVisibility(View.GONE);
		teamImageView.setOnClickListener(new OnClickListener() {				 
			@Override
			public void onClick(View view) {
				 try {
					 DialogListViewFragment dlg = new DialogListViewFragment();
					 dlg.setTitle(getString(R.string.Assignment_TeamMembers));					 
					 dlg.setItems(_connection.getTeamMemberList());
					 dlg.show(getSupportFragmentManager(), "");
				 }
				 catch (Exception e) {
					 ExceptionHelper.notifyUsers(e, AssignmentDetailActivity.this);
					 ExceptionHelper.notifyNonUsers(e);
				 }
			}
		});	
			
		// Enter (edit details) button click event
		enterButton.setOnClickListener(new OnClickListener() {				 
			 @Override
			public void onClick(View view) {
				 try {
					 startAssignmentActivity(AssignmentActivity.ASSIGNMODE_NONE, _connection.ContactInformation.IndvId, _connection.ContactInformation.getDisplayNameForList());
				 }
				 catch (Exception e) {
				 		ExceptionHelper.notifyUsers(e, AssignmentDetailActivity.this);
				 		ExceptionHelper.notifyNonUsers(e);
				 }  
			}			
		});
		 		
		// Reassign button click event
		reassignButton.setOnClickListener(new OnClickListener() {				 
			@Override
			public void onClick(View view) {
				 try {
					 DialogListSingleSelectFragment dlg = new DialogListSingleSelectFragment();
					 dlg.setTitle(getResources().getString(R.string.Connection_AssignDialogTitle));	
		
					 //-------------------------------------------------------------------
					 // Add Assignment items and their values  						 
					 // ...if no teams exist - do not show the 'team' assign option						 
					 //-------------------------------------------------------------------
					 List<String> itemList = new ArrayList<String>();
					 List<Integer> valueList = new ArrayList<Integer>();
					 					 
					 ConnectionTeamsListLoader teams = new ConnectionTeamsListLoader(AssignmentDetailActivity.this, "");
					 int teamCount = teams.getTeamCount();
					 
					 // Assign to Individual (always available)
					 itemList.add(getResources().getString(R.string.Connection_AssignDialogIndividual));
					 valueList.add(AssignmentActivity.ASSIGNMODE_INDIVIDUAL);
					 
					 // Assign to Team (only if teams)
					 if (teamCount > 0) {
						 itemList.add(getResources().getString(R.string.Connection_AssignDialogTeam));
						 valueList.add(AssignmentActivity.ASSIGNMODE_TEAM);
					 }
					 										 					 			
					 // convert item list to string array
					 String[] items = itemList.toArray(new String[itemList.size()]);
					 
					 // converting item values (int array) a bit more work to do
					 int[] itemValues = new int[valueList.size()];
					 for (int i=0; i < itemValues.length; i++)  {
						 itemValues[i] = valueList.get(i).intValue();
					 }
					 
					 dlg.setItems(items);
					 dlg.setItemValues(itemValues);					 
					 dlg.show(getSupportFragmentManager(), "reassignpicker");
					 dlg.setOnDimissListener(new DialogListSingleSelectFragment.OnDismissListener() {					
						@Override
						public void onDismiss(int selectedValue) {
							try {													
								startAssignmentActivity(selectedValue, _connection.ContactInformation.IndvId, _connection.ContactInformation.getDisplayNameForList());
							 }
							 catch (Exception e) {
							 		ExceptionHelper.notifyUsers(e, AssignmentDetailActivity.this);
							 		ExceptionHelper.notifyNonUsers(e);
							 }	
						}
					});						 
				 }
				 catch (Exception e) {
				 		ExceptionHelper.notifyUsers(e, AssignmentDetailActivity.this);
				 		ExceptionHelper.notifyNonUsers(e);
				 }				 
			}			
		});		
	 }
	 
	 /**
	  * Called after webservice retrieves the connection (class level variable) object.  
	  *   Sets control properties
	 */
	 private void bindData(){
		 
		 String linkText = "";
		 String actionTag = "";
		 
		 contactTextView.setText(_connection.ContactInformation.getDisplayNameForList());
		
		 // Address
		 if (_connection.ContactInformation.getContactAddress().length() > 0) {
			 linkText = String.format("<a href='#'>%s <br /> %s</a>", _connection.ContactInformation.Address, _connection.ContactInformation.CityStateZip);			 			 
			 actionTag = String.format("%s %s, %s %s", _connection.ContactInformation.Address, 					 										 
					 								   _connection.ContactInformation.City, 
					 								   _connection.ContactInformation.State, 
					 								   _connection.ContactInformation.Zipcode);
			 addressTextView.setText(Html.fromHtml(linkText));
			 addressTextView.setTag(actionTag);
		 }
		 else {
			 addressTextView.setVisibility(View.GONE);
		 }

		 // Phone
		 if (_connection.ContactInformation.PhoneNumber.length() > 0) {
			 linkText = "<a href='tel:" + _connection.ContactInformation.PhoneNumber + "'>" + _connection.ContactInformation.PhoneNumber + "</a>";
			 phoneTextView.setText(Html.fromHtml(linkText));
			 phoneTextView.setTag(_connection.ContactInformation.PhoneNumber);
		 }
		 else {
			 phoneTextView.setVisibility(View.GONE);
		 }

		 // Email
		 if (_connection.ContactInformation.Email.length() > 0) {
			 linkText = String.format("<a href='#'>%s</a>", _connection.ContactInformation.Email);			 			 
			 actionTag = _connection.ContactInformation.Email;					 
			 emailTextView.setText(Html.fromHtml(linkText));
			 emailTextView.setTag(actionTag);
		 }
		 else {
			 emailTextView.setVisibility(View.GONE);
		 }
				 		
		 assignmentTextView.setText(_connection.getDescription());
		 
		 // Further security check - these may already be 'gone' due to login permissions
		 if (_connection.hasChangePermission() == false) {
			 reassignButton.setVisibility(View.GONE);
			 enterButton.setVisibility(View.GONE);
		 }		
		 
		 // if this connection is assigned to a team, show the team button
		 if (_connection.TeamMemberCount  > 1) {
			 teamImageView.setVisibility(View.VISIBLE);
		 }
	 }
	 
	 
	 /**
     * Displays a progress dialog and launches a background thread to connect to a web service
     *   to retrieve a SINGLE connection. 
     *   
     */
    private void loadDataWithProgressDialog(final int connectionId)
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
	    				
	    				_connection = CoreConnection.GetCoreConnection(msg.getData().getString("connection"));
	    					    				
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
    					ExceptionHelper.notifyUsers(e, AssignmentDetailActivity.this);
    	    			ExceptionHelper.notifyNonUsers(e);
    				}
    			}    			    		
    	};
    	
    	Thread searchThread = new Thread() {  
    		public void run() {
    			try {    				    				
    				GlobalState gs = GlobalState.getInstance(); 		
    			 	Api apiCaller = new Api(_appPrefs.getWebServiceUrl(), config.APPLICATION_ID_VALUE);		

    			 	CoreConnection conn = apiCaller.connection(gs.getUserName(), gs.getPassword(), gs.getSiteNumber(), connectionId);

	    	    	// Return the response object (as string) to the message handler above
	    	    	Message msg = handler.obtainMessage();		
	    	    	msg.what = 0;
	    	    	
	    	    	Bundle b = new Bundle();
    				b.putString("connection", conn.toJsonString());   
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
     * Display the assignment screen
     * 
     * @throws AppException 
     */
    private void startAssignmentActivity(int assignToMode, int indvId, String indvName) throws AppException {	    	
    	Intent intent = new Intent();
    	intent.setClass(this, AssignmentActivity.class);
	 	intent.putExtra("assignment", _connection.toJsonString());
	 	intent.putExtra("assignto", assignToMode);
	 	intent.putExtra("id", indvId);
	 	intent.putExtra("name", indvName);
	 	
	 	startActivity(intent);	  		 			
	 	finish();					// always close this activity		 			 		    
    }
    	    
    /**
     * Display a list of recent connections for an individual
     *  
     * @throws AppException
     */
    private void startConnectionListActivity() throws AppException {	    	
    	Intent intent = new Intent();
    	intent.setClass(this, IndividualConnectionListActivity.class);
	 	intent.putExtra("id", _connection.ContactInformation.IndvId);
	 	intent.putExtra("name", _connection.ContactInformation.getDisplayNameForList());
	 	startActivity(intent);		 			 		    
    }	    
}
