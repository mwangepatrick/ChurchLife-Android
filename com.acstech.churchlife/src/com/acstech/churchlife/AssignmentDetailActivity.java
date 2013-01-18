package com.acstech.churchlife;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionHelper;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;
import com.acstech.churchlife.webservice.Api;
import com.acstech.churchlife.webservice.CoreAcsUser;
import com.acstech.churchlife.webservice.CoreConnection;

public class AssignmentDetailActivity  extends ChurchlifeBaseActivity {

	//zzz revisit progress dialog to do with fragments
	static final int DIALOG_PROGRESS = 0;
	
	Button viewRecentButton;
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
            	 _title = extraBundle.getString("assignmentname");     
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
		assignmentTextView = (TextView)this.findViewById(R.id.assignmentTextView);
		viewRecentButton = (Button)this.findViewById(R.id.viewRecentButton);
		enterButton = (Button)this.findViewById(R.id.enterButton);
		reassignButton = (Button)this.findViewById(R.id.reassignButton);
				
		// Reassign - check permission and hide/disable button IF the user does NOT have permission
		GlobalState gs = GlobalState.getInstance(); 
		if (gs.getUser().HasPermission(CoreAcsUser.PERMISSION_REASSIGNCONNECTION) == false) {
			reassignButton.setVisibility(View.GONE);
		}
					
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
		 
		// Enter (edit details) button click event
		enterButton.setOnClickListener(new OnClickListener() {				 
			 @Override
			public void onClick(View view) {
				 try {
					 startAssignmentActivity();
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
				 //
			}			
		});		
	 }
	 
	 /**
	  * Called after webservice retrieves the connection (class level variable) object.  
	  *   Sets control properties
	 */
	 private void bindData(){		 		 	
		 assignmentTextView.setText(_connection.getDescription());		  
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
	    private void startAssignmentActivity() throws AppException {	    	
	    	Intent intent = new Intent();
	    	intent.setClass(this, AssignmentActivity.class);
		 	intent.putExtra("assignment", _connection.toJsonString());
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
