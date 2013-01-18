package com.acstech.churchlife;

import java.util.Arrays;
import java.util.Date;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionHelper;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;
import com.acstech.churchlife.webservice.Api;
import com.acstech.churchlife.webservice.CoreAcsUser;
import com.acstech.churchlife.webservice.CoreCommentChangeRequest;
import com.acstech.churchlife.webservice.CoreCommentType;
import com.acstech.churchlife.webservice.CoreConnection;
import com.acstech.churchlife.webservice.CoreConnectionChangeRequest;

public class AssignmentActivity  extends ChurchlifeBaseActivity {

	//zzz revisit progress dialog to do with fragments
	static final int DIALOG_PROGRESS_LOAD = 0;
	static final int DIALOG_PROGRESS_SAVE = 1;
	
	EditText connectionEditText;
	Button responsesButton;	
	CheckBox closeCheckBox;
	Button saveButton;
		
	CoreConnection _connection;
	
	AppPreferences _appPrefs; 
	private ProgressDialog _progressD;
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
		 
		 super.onCreate(savedInstanceState);
	
		 try
		 {
			 _appPrefs = new AppPreferences(getApplicationContext());
			 
			 setContentView(R.layout.assignment); 
			 bindControls();
			 
        	 // This activity MUST be passed data
        	 Bundle extraBundle = this.getIntent().getExtras();
             if (extraBundle == null) {
            	 throw AppException.AppExceptionFactory(
            			 ExceptionInfo.TYPE.UNEXPECTED,
						 ExceptionInfo.SEVERITY.CRITICAL, 
						 "100",           												    
						 "AssignmentActivity.onCreate",
						 "No assignment was passed to the Assignment edit activity.");
             }
             else {
            	 _connection = CoreConnection.GetCoreConnection(extraBundle.getString("assignment"));
            	 bindData();
            	 //zzz remove after testing
            	 //int assignmentId = extraBundle.getInt("assignmentid");
            	 //loadDataWithProgressDialog(assignmentId);            	    
             }		                         
		}
	 	catch (Exception e) {
	 		ExceptionHelper.notifyUsers(e, AssignmentActivity.this);
	 		ExceptionHelper.notifyNonUsers(e);
	 	}  		
	 }
	 
	    protected Dialog onCreateDialog(int id) {
	        switch(id) {
	        case DIALOG_PROGRESS_LOAD:
	        	_progressD = new ProgressDialog(AssignmentActivity.this);
	        	
	        	String msg = getString(R.string.Dialog_Loading);		        	
	        	_progressD.setMessage(msg);        	
	        	_progressD.setIndeterminate(true);
	        	_progressD.setCancelable(false);
	    		return _progressD;	  
	    		
	        case DIALOG_PROGRESS_SAVE:
	        	_progressD = new ProgressDialog(AssignmentActivity.this);
	        	
	        	String msg2 = getString(R.string.Connection_SaveDialog);		        		        
	        	_progressD.setMessage(msg2);        	
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

		connectionEditText = (EditText)this.findViewById(R.id.connectionEditText);		
		responsesButton = (Button)this.findViewById(R.id.responsesButton);
		closeCheckBox = (CheckBox)this.findViewById(R.id.closeCheckBox);
		saveButton = (Button)this.findViewById(R.id.saveButton);
								
		// Responses button click event             
		responsesButton.setOnClickListener(new OnClickListener() {				 
			 @Override
			public void onClick(View view) {
				 //ItemSelected(itemSelected);
			}			
		});
		 
		// Save  button click event
		saveButton.setOnClickListener(new OnClickListener() {				 
			 @Override
			public void onClick(View view) {
          		if (inputIsValid()) {
         			saveConnection();	
         			finish();	             			
         		}
			}			
		});
		 		
	 }
	 
	 /**
	  * Called after webservice retrieves the connection (class level variable) object.  
	  *   Sets control properties
	 */
	 private void bindData(){
		 
		 setTitle(_connection.ContactInformation.getDisplayNameForList());
		 connectionEditText.setText(_connection.Comment);
		 //responsesButton.setText("test");
	 }
	 
	 
	 /**
     * Displays a progress dialog and launches a background thread to connect to a web service
     *   to retrieve a SINGLE connection. 
     *   
     */
	    private void loadDataWithProgressDialog(final int connectionId)
	    {               	    			   
	    	showDialog(DIALOG_PROGRESS_LOAD);
	    	
	    	// This handler is called once the lookup is complete.  It looks at the data returned from the
	    	//  thread (in the Message) to determine success/failure.  If successful, the values are
	    	//  bound to this activity's layout controls
	    	final Handler handler = new Handler() {
	    		public void handleMessage(Message msg) {
	    		  
	    			removeDialog(DIALOG_PROGRESS_LOAD);
	    			
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
	    					ExceptionHelper.notifyUsers(e, AssignmentActivity.this);
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
	     * Ensures all form fields have valid input.  
	     * 
	     * Should be called on button click before processing.   Displays a message 
	     *   to the user indicating which field is invalid.  This procedure stops 
	     *   checking for invalid fields once the first invalid field is encountered.
	     *   
	     * @return true if input fields are valid, otherwise false
	     */
	    private Boolean inputIsValid()
	    {    	
	    	String msg = "";
	    	
    		if (connectionEditText.getText().length() == 0) {   
    			msg = (String)this.getResources().getText(R.string.Connection_CommentValidation);     		
    		}
    		
	    	// If a validation message exists, show it
	    	if (msg.length() > 0) {
	    		Toast.makeText(AssignmentActivity.this, msg, Toast.LENGTH_LONG).show(); 
	    	}
	    	
	    	// If a validation message exists, the input is invalid
	    	return (msg.length() == 0);    	    
	    }
	    
	    // Input should have already been validated at this point!
	    private void saveConnection() {
	    	try {	    		
	    		showDialog(DIALOG_PROGRESS_SAVE);   			// progress dialog
	    		
	    		CoreConnectionChangeRequest req = new CoreConnectionChangeRequest();
	    		req.ConnectionId = _connection.ConnectionId;
	    		req.Complete = closeCheckBox.isChecked();
	    		req.ConnectionDate = new Date(); //????
	    		req.ConnectionTypeId = _connection.ConnectionTypeId;
	    		req.FamilyConnection = _connection.FamilyConnection;
	    		req.ContactIndvId = _connection.ContactInformation.IndvId;
	    		//req.OpenCategoryId
	    		req.Reassign = false;
	    		//req.NewCallerIndvId
	    		//req.NewTeamId
	    		req.Comment = connectionEditText.getText().toString();
	    		req.ResponseIdList = Arrays.asList(1, 2, 3);
	    
	    		//for (int i : ints) intList.add(i);
	    		//List<Integer> messages = Arrays.asList(1, 2, 3);
	    		
	    		//req.ResponseIdList
	    		
	    		/*    		
	    		req.FamilyComment = BooleanHelper.ParseBoolean(chkFamilyComment.isChecked());
	    		*/
	    		GlobalState gs = GlobalState.getInstance(); 
	    		AppPreferences appPrefs = new AppPreferences(this.getApplicationContext());
	    		
	    		Api apiCaller = new Api(appPrefs.getWebServiceUrl(), config.APPLICATION_ID_VALUE);	
	    		
	    	   	apiCaller.connectionAdd(gs.getUserName(), gs.getPassword(), gs.getSiteNumber(), req);
	    		
	    	   	removeDialog(DIALOG_PROGRESS_SAVE);
	    	   	
	    	   	//zzz chnage to saved
	    	   	Toast.makeText(AssignmentActivity.this, getString(R.string.Connection_Saved), Toast.LENGTH_LONG).show();	    	   	
	    	}
	        catch (Exception e) {
	        	
	        	removeDialog(DIALOG_PROGRESS_SAVE);
	        	
	        	// does NOT raise errors.  called by an event
				ExceptionHelper.notifyUsers(e, AssignmentActivity.this);
		    	ExceptionHelper.notifyNonUsers(e)  ; 	
	        }
	    }

}
