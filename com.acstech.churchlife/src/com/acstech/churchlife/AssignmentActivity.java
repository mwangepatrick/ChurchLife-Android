package com.acstech.churchlife;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionHelper;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;
import com.acstech.churchlife.listhandling.ResponseTypeListLoader;
import com.acstech.churchlife.webservice.Api;
import com.acstech.churchlife.webservice.CoreConnection;
import com.acstech.churchlife.webservice.CoreConnectionChangeRequest;
import com.acstech.churchlife.webservice.CoreResponseType;

public class AssignmentActivity  extends ChurchlifeBaseActivity {

	//zzz revisit progress dialog to do with fragments
	static final int DIALOG_PROGRESS_LOAD = 0;
	static final int DIALOG_PROGRESS_SAVE = 1;
	
	LinearLayout reassignLayout;
	TextView reassignTextView;
	
	EditText connectionEditText;
	Button responsesButton;	
	CheckBox closeCheckBox;
	Button saveButton;
		
	private int _assignToMode = 0;				// 0 = individual, 1 = team
	private int _assignToId = 0;
	
	CoreConnection _connection;
	ResponseTypeListLoader _responseTypeLoader;
	
	boolean[] _selectedResponses = null;		// holds user selection of response types
	
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
            	 
            	 // check for re-assignment
            	 if (extraBundle.get("assignto") != null) {
            		 _assignToMode = extraBundle.getInt("assignto");
            		 startReAssignmentPickerActivity(_assignToMode);
            	 }            	 
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
		reassignLayout = (LinearLayout)this.findViewById(R.id.reassignLayout);	
		reassignTextView = (TextView)this.findViewById(R.id.reassignTextView);					
		connectionEditText = (EditText)this.findViewById(R.id.connectionEditText);		
		responsesButton = (Button)this.findViewById(R.id.responsesButton);
		closeCheckBox = (CheckBox)this.findViewById(R.id.closeCheckBox);
		saveButton = (Button)this.findViewById(R.id.saveButton);
					
		reassignLayout.setVisibility(View.GONE);
		
		// Responses button click event             
		responsesButton.setOnClickListener(new OnClickListener() {				 
			 @Override
			public void onClick(View view) {
				 DialogListMultiSelectFragment dlg = new DialogListMultiSelectFragment();
				 dlg.setTitle(getResources().getString(R.string.Connection_Responses));				
				 dlg.setItems(_responseTypeLoader.getDescriptionArray());		
				 dlg.setSelections(_selectedResponses);				 
				 dlg.show(getSupportFragmentManager(), "responsepicker");
				 dlg.setOnDimissListener(new DialogListMultiSelectFragment.OnDismissListener() {					
					@Override
					public void onDismiss(boolean[] selection) {
						_selectedResponses = selection;				// update class level 'selections' variable
						setResponseButtonText();					// update button label (looks at 'selections')
					}
				});			 				
			}	 					
		});
		 
		// Save  button click event
		saveButton.setOnClickListener(new OnClickListener() {				 
			 @Override
			public void onClick(View view) {
				 try {					
					 if (inputIsValid()) {          				          	
	         			saveConnection();	
	         			finish();	             			
	         		 }
				}
          		catch (Exception e) {    	        	    	        	
    	        	ExceptionHelper.notifyUsers(e, AssignmentActivity.this);
    		    	ExceptionHelper.notifyNonUsers(e)  ; 	
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
		 closeCheckBox.setChecked(true);					// default to close this connection
		 
		 loadResponseTypes();		 
	 }
	 

	    /**
	     * Displays a 'please wait' dialog and launches a background thread to connect 
	     *   to a web service to retrieve search results 
	     *   
	     */
	    private void loadResponseTypes()
	    {           	    
	    	showDialog(DIALOG_PROGRESS_LOAD);
	    	
	    	try
	    	{	
	    		_responseTypeLoader = new ResponseTypeListLoader(this, _connection.ConnectionId);	    				    		
	    		_responseTypeLoader.Load(0, onListLoaded);	    		
	    	}
	    	catch (Exception e) {
				ExceptionHelper.notifyUsers(e, AssignmentActivity.this);
	    		ExceptionHelper.notifyNonUsers(e); 				    				
			}  	    		    		    	
	    }
	    
	    // display the results from the loader operation
	    final Runnable onListLoaded = new Runnable() {
	        public void run() {	        	
	        	try
	        	{
	        		//removeDialog(DIALOG_PROGRESS_LOAD);	        		
		        	if (_responseTypeLoader.success())	{
		        		
		        		// update the _selected list to indicate which responses
		        		//  this connection already has (now that we have a list
		        		//  and can resolve the id to a position in the list)
		        		ArrayList<CoreResponseType> responses = _responseTypeLoader.getList();
		        		
		        		_selectedResponses = new boolean[responses.size()];
		        		for (int i=0; i < responses.size()-1; i++) {
		        		
		        			if (_connection.containsResponse(responses.get(i).RespID)) {
			        			_selectedResponses[i] = true;		        				
		        			}
		        			else {
			        			_selectedResponses[i] = false;
		        			}		        			
		        		}		        		
		        		
		        		setResponseButtonText();
		        		
		        	}
		        	else {
		        		throw _responseTypeLoader.getException();
		        	}
	        	}
	        	catch (Throwable e) {
	        		ExceptionHelper.notifyUsers(e, AssignmentActivity.this);
	        		ExceptionHelper.notifyNonUsers(e); 	        	
				} 	        		        
	        	finally {
	        		removeDialog(DIALOG_PROGRESS_LOAD);
	        	}
	        }
	    };
	   
	 // Builds a string of all 'selected' responses.  Uses the class level _selectedResponses
	 //  array and the class level response types list loader to compose.  This keeps all of
	 //  the string building logic in one central place (the UI)
	 private void setResponseButtonText() {
		 
		StringBuilder textBuilder = new StringBuilder();
		 
		for (int i=0; i < _selectedResponses.length-1; i++) {     		
			 if (_selectedResponses[i] == true) {				 
				 if (textBuilder.length() > 0) { textBuilder.append(","); }
				 
				 //append the text for this response (by position in the list)
				 textBuilder.append(_responseTypeLoader.getList().get(i).Resp_Desc);				 
			 }			  					        		
 		}
		 		 
		if (textBuilder.length() == 0) {
			responsesButton.setText(R.string.Connection_ResponseDefault);
		}
		else
		{
			responsesButton.setText(textBuilder.toString());
		}			   
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
	 private void saveConnection() throws AppException {
    	try {
    		showDialog(DIALOG_PROGRESS_SAVE);   			// progress dialog
   
    		CoreConnectionChangeRequest req = new CoreConnectionChangeRequest();
    		req.ConnectionId = _connection.ConnectionId;
    		req.Complete = closeCheckBox.isChecked();
    		req.ConnectionDate = new Date();
    		req.ConnectionTypeId = _connection.ConnectionTypeId;
    		req.FamilyConnection = _connection.FamilyConnection;
    		req.ContactIndvId = _connection.ContactInformation.IndvId;
    		
    		// reassign
    		if (_assignToId > 0) {
    			req.Reassign = true;
    			
    			if (_assignToMode == 0) {				// _assignToId is an individual id
    				req.NewCallerIndvId = _assignToId;
    			}
    			else {									// _assignToId is an team id
    				req.NewTeamId = _assignToId;
    			}
    		}    		
    		else {
    			req.Reassign = false;
    		}
    		
    		req.Comment = connectionEditText.getText().toString();
    		
    		// Iterate over _selectedResponses and add all user selected responses
    		req.ResponseIdList = new ArrayList<Integer>();    		
    		for (int i = 0; i < _selectedResponses.length-1; i++) {
    			if (_selectedResponses[i] == true) {
    				int id = _responseTypeLoader.getList().get(i).RespID;
    				req.ResponseIdList.add(id);
    			}
    		}
    		
    		// send to webservice
    		GlobalState gs = GlobalState.getInstance(); 
    		AppPreferences appPrefs = new AppPreferences(this.getApplicationContext());    		
    		Api apiCaller = new Api(appPrefs.getWebServiceUrl(), config.APPLICATION_ID_VALUE);	
    		
    	   	apiCaller.connectionAdd(gs.getUserName(), gs.getPassword(), gs.getSiteNumber(), req);
    			    		
    	   	removeDialog(DIALOG_PROGRESS_SAVE);
    	   	    	
    	   	Toast.makeText(AssignmentActivity.this, getString(R.string.Connection_Saved), Toast.LENGTH_LONG).show();	    	   	
    	}
    	finally {
    		removeDialog(DIALOG_PROGRESS_SAVE);
    	}        
	 }

	// "assign to" picker	    
    /**
     * Display the re-assignment picker screen
     * 
     * @throws AppException 
     */
    private void startReAssignmentPickerActivity(int assignTo) throws AppException {	    	
    	Intent intent = new Intent();
    	intent.setClass(this, AssignToPickerActivity.class);	 
	 	intent.putExtra("assignto", assignTo);	
	 	startActivityForResult(intent, 0);	 		 	
    }

    // This catches the result (assignment value) returned from the AssignToPicker activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    
    	super.onActivityResult(requestCode, resultCode, data);
    	
        if (resultCode == Activity.RESULT_OK) {
           _assignToId =  Integer.parseInt(data.getStringExtra("id"));
           
           reassignLayout.setVisibility(View.VISIBLE);           
           reassignTextView.setText(data.getStringExtra("description")); 
           closeCheckBox.setChecked(false);
        }        
    }
    
}
