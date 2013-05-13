package com.acstech.churchlife;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionHelper;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;
import com.acstech.churchlife.listhandling.ConnectionTypeListLoader;
import com.acstech.churchlife.listhandling.ContactTypeListLoader;
import com.acstech.churchlife.listhandling.ResponseTypeListLoader;
import com.acstech.churchlife.webservice.Api;
import com.acstech.churchlife.webservice.CoreConnection;
import com.acstech.churchlife.webservice.CoreConnectionChangeRequest;
import com.acstech.churchlife.webservice.CoreConnectionType;
import com.acstech.churchlife.webservice.CoreResponseType;

// This activity can be used to add a new connection or edit/re-assign an existing one.  
//  Several controls are shown/hidden based on add/edit mode.
public class AssignmentActivity  extends ChurchlifeBaseActivity {
	
	static final int ASSIGNMODE_NONE = -1;
	static final int ASSIGNMODE_INDIVIDUAL = 0;
	static final int ASSIGNMODE_TEAM = 1;
	static final int ASSIGNMODE_ME = 2;
		
	static final int DIALOG_PROGRESS_LOADCONNECTIONTYPES = 0;	
	static final int DIALOG_PROGRESS_LOADRESPONSES = 1;	
	static final int DIALOG_PROGRESS_SAVE = 2;
	private ProgressDialog _progressD;
	
	// layout section controls
	LinearLayout assignLayout;
	LinearLayout dueDateLayout;
	LinearLayout contactTypeLayout;
	LinearLayout connectionTypeLayout;
	LinearLayout responseLayout;
	RelativeLayout familyLayout;
	RelativeLayout closeLayout;
	
	// user input controls
	EditText assignEditText;
	EditText dueDateEditText;
	Spinner contactTypeSpinner;
	Spinner connectionTypeSpinner;
	EditText connectionEditText;	
	Button responsesButton;	
	CheckBox familyCheckBox;
	CheckBox closeCheckBox;
	Button saveButton;
		
	private int _individualId = -1;				 // connection is FOR this person
	private String _individualName = "";		 // connection is FOR this person
	
	private int _assignToMode = ASSIGNMODE_NONE; // 0 = individual, 1 = team, 2 = Me  			(passed in)
	private int _assignToId = 0;				 // person to whom this connection is assigned 	(set by this class)
	
	CoreConnection _connection;	
	ContactTypeListLoader _contactTypeLoader;
	ConnectionTypeListLoader _connectionTypeLoader;
	ResponseTypeListLoader _responseTypeLoader;
	
	boolean[] _selectedResponses = null;		// holds user selection of response types
		
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
		 
		 super.onCreate(savedInstanceState);	
		 disableOrientationChange();
		 
		 try
		 {
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
            	 _individualId = extraBundle.getInt("id");
            	 _individualName = extraBundle.getString("name");
            	 
            	 _assignToMode = extraBundle.getInt("assignto");
            	 
            	 // if connection json was passed in, use that to edit; 
            	 //   otherwise, create a new connection
            	 String connectionJson = extraBundle.getString("assignment");
            	 if (connectionJson.length() > 0) {            		 
            		 _connection = CoreConnection.GetCoreConnection(connectionJson);
            	 } 
            	 else {
            		 _connection = new CoreConnection();
            	 }               	                              
            	 bindData();            	 		            
         	}	 
		}
	 	catch (Exception e) {
	 		ExceptionHelper.notifyUsers(e, AssignmentActivity.this);
	 		ExceptionHelper.notifyNonUsers(e);
	 	}  		
	 }
	 
	 private void disableOrientationChange() {
		 if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
		     setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		 } else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
		     setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		 } else {
		     setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		 }		 
	 }
	 
	 
	 protected Dialog onCreateDialog(int id) {
        switch(id) {
        
        case DIALOG_PROGRESS_LOADCONNECTIONTYPES:	        
        	String msg = getString(R.string.Connection_ProgressDialogConnectionType);        	
        	_progressD = new ProgressDialog(AssignmentActivity.this);
        	_progressD.setMessage(msg);  
        	_progressD.setIndeterminate(true);
        	_progressD.setCancelable(false);
    		return _progressD;	  

        case DIALOG_PROGRESS_LOADRESPONSES:    	
        	String msg2 = getString(R.string.Connection_ProgressDialogResponses);		        	        	        	        
        	_progressD = new ProgressDialog(AssignmentActivity.this);        	
        	_progressD.setMessage(msg2);        	
        	_progressD.setIndeterminate(true);
        	_progressD.setCancelable(false);
    		return _progressD;	  
    		
        case DIALOG_PROGRESS_SAVE:
        	String msg3 = getString(R.string.Connection_SaveDialog);
        	_progressD = new ProgressDialog(AssignmentActivity.this);        			        		       
        	_progressD.setMessage(msg3);        	
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
		assignLayout = (LinearLayout)this.findViewById(R.id.assignLayout);
		dueDateLayout = (LinearLayout)this.findViewById(R.id.dueDateLayout);	
		contactTypeLayout = (LinearLayout)this.findViewById(R.id.contactTypeLayout);
		connectionTypeLayout = (LinearLayout)this.findViewById(R.id.connectionTypeLayout);
		responseLayout = (LinearLayout)this.findViewById(R.id.responseLayout);
		familyLayout = (RelativeLayout)this.findViewById(R.id.familyLayout);			
		closeLayout = (RelativeLayout)this.findViewById(R.id.closeLayout);	

		// user input controls		
		assignEditText = (EditText)this.findViewById(R.id.assignEditText);
		dueDateEditText = (EditText)this.findViewById(R.id.dueDateEditText);
		contactTypeSpinner = (Spinner)this.findViewById(R.id.contactTypeTypeSpinner);
		connectionTypeSpinner = (Spinner)this.findViewById(R.id.connectionTypeSpinner);		
		connectionEditText = (EditText)this.findViewById(R.id.connectionEditText);	
		responseLayout = (LinearLayout)this.findViewById(R.id.responseLayout);
		responsesButton = (Button)this.findViewById(R.id.responsesButton);		
		familyCheckBox = (CheckBox)this.findViewById(R.id.familyCheckBox);				   
		closeCheckBox = (CheckBox)this.findViewById(R.id.closeCheckBox);
		saveButton = (Button)this.findViewById(R.id.saveButton);
			
		assignEditText.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	try {
					startAssignmentPickerActivity(_assignToMode);
				}          		
	        	catch (Exception e) {    	        	    	        	
    	        	ExceptionHelper.notifyUsers(e, AssignmentActivity.this);
    		    	ExceptionHelper.notifyNonUsers(e); 	
    	        }
	        }
	    });
		
		dueDateEditText.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	Bundle b = new Bundle();
	        	b.putSerializable("date", dueDateEditText.getText().toString());
	        	DialogDatePickerFragment newFragment = new DialogDatePickerFragment(b, onDatePicked);
        	    newFragment.show(getSupportFragmentManager(), "DatePicker");
	        }
	    });
	   
		// contact type drop down change (loads connection types)
		contactTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	ContactTypeListLoader.ContactType item = (ContactTypeListLoader.ContactType)parentView.getItemAtPosition(position);		    			    
		    	loadConnectionTypes(item.Id);		    	
		    }
		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {   }
		});

		// connection type drop down change (load response types for connection type)
		connectionTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	CoreConnectionType item = (CoreConnectionType)parentView.getItemAtPosition(position);
		    	loadResponseTypes(item.ConnectionTypeId);
		    }
		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {   }
		});
	    
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
	         			AssignmentActivity.this.selectMenuItem(getResources().getString(R.string.Menu_Connections));	         			   	         				         	
	         		 }
				}
          		catch (Exception e) {    	        	    	        	
    	        	ExceptionHelper.notifyUsers(e, AssignmentActivity.this);
    		    	ExceptionHelper.notifyNonUsers(e)  ; 	
    	        }
			}			
		});
		 		
	 }
	 
	 // date picker call back (to set the edit text)
	 OnDateSetListener onDatePicked = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			// month is 0 based in java
			dueDateEditText.setText(monthOfYear+1 + "/" + dayOfMonth + "/" + year);
		}
	 };

		 
	 /**
	  * Called after webservice retrieves the connection (class level variable) object.  
	  *   Sets control properties
	  *   
	  * The _connection can be new or exiting; control state changes based on whether we 
	  *   are editing an existing or adding a new one
	 */
	 private void bindData() throws AppException {

		 setTitle(_individualName);		 
		 connectionEditText.setText(_connection.Comment);
		 
		 if (_connection.ConnectionId > 0) {			 
			 // setup form for edit
			 dueDateLayout.setVisibility(View.GONE);	
			 contactTypeLayout.setVisibility(View.GONE);
			 connectionTypeLayout.setVisibility(View.GONE);
			 familyLayout.setVisibility(View.GONE);			

			 // do not allow close on re-assign of an existing
			 if (_assignToMode >= 0 && _connection.ConnectionId > 0) {
				 closeLayout.setVisibility(View.GONE);
			 }
			 
			 // if assignToMode as NOT passed on an existing - don't show (as we aren't re-assigning)
			 if (_assignToMode == ASSIGNMODE_NONE && _connection.ConnectionId > 0) {
				 assignLayout.setVisibility(View.GONE);		 
			 }
			 
			 closeCheckBox.setChecked(true);					// default to close this connection			 
			 loadResponseTypes(_connection.ConnectionTypeId);	// load responses for this connection type					 
		 }
		 else {
			 // setup form for add			 			
			 SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);				
			 dueDateEditText.setText(sdf.format(new Date()));	// default to today's date			 
		 }
	 
		 //----------------------------------------------------
    	 // Assignment dialog picker - if not assigned to self 
		 //----------------------------------------------------
         if (_assignToMode == ASSIGNMODE_ME) {
        	 _assignToId = getCurrentUser().IndvId;
        	 assignEditText.setText(getResources().getString(R.string.Connection_AssignDialogMe));
			 loadContactTypes();  								//fill spinner (drop down) - only in add mode 
         }
         else if (_assignToMode != ASSIGNMODE_NONE) {
         	startAssignmentPickerActivity(_assignToMode);         	
         }		
	 }
	 

	 // Contact Types - note:  no dialog as no web service call for this list of types
	 // ONLY used when in add mode (ignored otherwise)
	 private void loadContactTypes() {
		 	
		 try
	     {	
			 if (_connection.ConnectionId <= 0) {
				
				// Get just first name of connection owner
				String ownerName = _individualName;				 
				int iPos = ownerName.indexOf(" ");
				if (iPos > 0) {
					ownerName = ownerName.substring(0,iPos).trim();
				}
				
				// Get just first name of connection assignee 
				String assigneeName = assignEditText.getText().toString();				
				iPos = assigneeName.indexOf(" ");
				if (iPos > 0) {
					assigneeName = assigneeName.substring(0,iPos).trim();
				}							
				
		    	_contactTypeLoader = new ContactTypeListLoader(this, ownerName, assigneeName, true);	    				    		
		    	_contactTypeLoader.Load(0, onContactTypeListLoaded);
			 }
	    }
	    catch (Exception e) {
			ExceptionHelper.notifyUsers(e, AssignmentActivity.this);
	    	ExceptionHelper.notifyNonUsers(e); 				    				
	    }  	    		
	 }
	 
	 // display the results from the loader operation
	 final Runnable onContactTypeListLoaded = new Runnable() {
		 public void run() {	        	
			 try {
				 if (_contactTypeLoader.success())	{
					
					 contactTypeSpinner.setAdapter(null);		// clear out previous results
					 
			  		ArrayAdapter<ContactTypeListLoader.ContactType> adapter = new ArrayAdapter<ContactTypeListLoader.ContactType>(AssignmentActivity.this, android.R.layout.simple_spinner_item, _contactTypeLoader.getList()); 		        				        	
	        		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        		contactTypeSpinner.setAdapter(adapter);	
	        	
	        		// always set to outward contact type on load
	        		int position = _contactTypeLoader.getItemPosition("O");
	        		contactTypeSpinner.setSelection(position-1, true);	        	
			     }
			     else {
			    	 throw _contactTypeLoader.getException();
			     }
			 }
		     catch (Throwable e) {
		    	 ExceptionHelper.notifyUsers(e, AssignmentActivity.this);
		    	 ExceptionHelper.notifyNonUsers(e); 	        	
			 } 	        		        
		 }
	 };
	 
	 
	 /**
	  * Displays a 'please wait' dialog and launches a background thread to connect 
	  *   to a web service to retrieve search results
	  *    
	  * where connectionCategory = I or O (inward, outward contact type)  
	 */
	 private void loadConnectionTypes(String contactTypeId) {
		 showDialog(DIALOG_PROGRESS_LOADCONNECTIONTYPES);
		 
		 try
	     {	
	    	_connectionTypeLoader = new ConnectionTypeListLoader(this, contactTypeId);	    				    		
	    	_connectionTypeLoader.Load(0, onConnectionTypeListLoaded);	    		
	    }
	    catch (Exception e) {
			ExceptionHelper.notifyUsers(e, AssignmentActivity.this);
	    	ExceptionHelper.notifyNonUsers(e); 				    				
	    }  	    		    		    	
	 }
	  
	 // display the results from the loader operation
	 final Runnable onConnectionTypeListLoaded = new Runnable() {
		 public void run() {	        	
			 try {
				 if (_connectionTypeLoader.success())	{		
					 ArrayAdapter<CoreConnectionType> adapter = new ArrayAdapter<CoreConnectionType>(AssignmentActivity.this, android.R.layout.simple_spinner_item, _connectionTypeLoader.getList()); 		        				        	
					 adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					 connectionTypeSpinner.setAdapter(adapter);	
		         }
			     else {
			    	 throw _connectionTypeLoader.getException();
			     }
			 }
		     catch (Throwable e) {
		    	 ExceptionHelper.notifyUsers(e, AssignmentActivity.this);
		    	 ExceptionHelper.notifyNonUsers(e); 	        	
			 } 	        		        
		     finally {
		    	 removeDialog(DIALOG_PROGRESS_LOADCONNECTIONTYPES);
		     }
		 }
	 };
	 
	 
    /**
     * Displays a 'please wait' dialog and launches a background thread to connect 
     *   to a web service to retrieve search results 
     *   
     */
    private void loadResponseTypes(int connectionTypeId)
    {           	    
    	showDialog(DIALOG_PROGRESS_LOADRESPONSES);
    	
    	try
    	{	
    		_responseTypeLoader = new ResponseTypeListLoader(this, connectionTypeId);	    				    		
    		_responseTypeLoader.Load(0, onResponseTypeListLoaded);	    		
    	}
    	catch (Exception e) {
			ExceptionHelper.notifyUsers(e, AssignmentActivity.this);
    		ExceptionHelper.notifyNonUsers(e); 				    				
		}  	    		    		    	
    }
    
    // display the results from the loader operation
    final Runnable onResponseTypeListLoaded = new Runnable() {
        public void run() {	        	
        	try
        	{	        		
	        	if (_responseTypeLoader.success())	{
	        		
	        		// update the _selected list to indicate which responses
	        		//  this connection already has (now that we have a list
	        		//  and can resolve the id to a position in the list)
	        		ArrayList<CoreResponseType> responses = _responseTypeLoader.getList();
	        		_selectedResponses = new boolean[responses.size()];
	        		
	        		// if there are 0 responses for this connection, hide the response area
	        		if (_selectedResponses.length == 0) {
	        			responseLayout.setVisibility(View.GONE);	
	        		}
	        		else
	        		{			        	
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
        		removeDialog(DIALOG_PROGRESS_LOADRESPONSES);
        	}
        }
    };
	   
	 // Builds a string of all 'selected' responses.  Uses the class level _selectedResponses
	 //  array and the class level response types list loader to compose.  This keeps all of
	 //  the string building logic in one central place (the UI)
	 private void setResponseButtonText() {
		 
		StringBuilder textBuilder = new StringBuilder();
		 
		for (int i=0; i <= _selectedResponses.length-1; i++) {     		
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
	    	
	    	// Comment text is required when editing
    		if (connectionEditText.getText().length() == 0 && _connection.ConnectionId > 0) {   
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
	 //  connection could be a brand new one...or edit of an existing 
	 private void saveConnection() throws Exception {
		 
		showDialog(DIALOG_PROGRESS_SAVE);   			// progress dialog
   		
		CoreConnectionType cc = (CoreConnectionType)connectionTypeSpinner.getSelectedItem();
		
		CoreConnectionChangeRequest req = new CoreConnectionChangeRequest();

		 // -- set properties common to add/edit --
		 if (_assignToId > 0) {			 
			 if (_assignToMode == ASSIGNMODE_TEAM) {
				 req.NewTeamId = _assignToId;
			 }
			 else {
				 req.NewCallerIndvId = _assignToId;
			 }			 
		 }
		 		 	
		 req.Comment = connectionEditText.getText().toString();
		 req.Complete = closeCheckBox.isChecked();
		 
		 // Iterate over _selectedResponses and add all user selected responses
		 req.ResponseIdList = new ArrayList<Integer>();    		
		 for (int i = 0; i <= _selectedResponses.length-1; i++) {
			 if (_selectedResponses[i] == true) {
				 int id = _responseTypeLoader.getList().get(i).RespID;
				req.ResponseIdList.add(id);
			 }
		 }

		 // -- set properties specific to add OR edit --
		 if (_connection.ConnectionId > 0) {							// edit			 			
			 req.ConnectionId = _connection.ConnectionId;
			 req.ConnectionTypeId = _connection.ConnectionTypeId;
			 req.ConnectionDate = new Date();
			 req.FamilyConnection = _connection.FamilyConnection;			 
			 req.ContactIndvId = _connection.ContactInformation.IndvId;
		
			 // if re-assigning an existing, no close
			 if (_assignToId > 0) {
				 req.Reassign = true;
				 req.Complete = false;
			 }
			 else {
				 req.Reassign = false;
			 }
		 }
		 else {															// add			
			 req.ConnectionTypeId = cc.ConnectionTypeId;
			 req.setConnectionDate(dueDateEditText.getText().toString());
			 req.FamilyConnection = familyCheckBox.isChecked();
			 req.ContactIndvId = _individualId;					 			 	
		 }
	
		// send to web service (background thread)    		
		saveConnectionTask tsk = new saveConnectionTask();
		tsk.execute(req);
		boolean result = tsk.get();				//causes thread to block until result is returned
		    	   	
	   	removeDialog(DIALOG_PROGRESS_SAVE);
	   	    
	   	if (result) {
	   		Toast.makeText(AssignmentActivity.this, getString(R.string.Connection_Saved), Toast.LENGTH_LONG).show();	
		}
		else {
			throw tsk._ex;
		}	   		        
	 }

	 // do webservice post in background
	 private class saveConnectionTask extends AsyncTask<CoreConnectionChangeRequest, Void, Boolean> {    	
		 Exception _ex;
		 GlobalState gs = GlobalState.getInstance(); 

 		@Override
        protected Boolean doInBackground(CoreConnectionChangeRequest... args) {	        	
        	try	{
		    	AppPreferences appPrefs = new AppPreferences(getApplicationContext());
				Api apiCaller = new Api(appPrefs.getWebServiceUrl(), config.APPLICATION_ID_VALUE);	
		
				apiCaller.connectionAdd(gs.getUserName(), gs.getPassword(), gs.getSiteNumber(), args[0]);				
				return true; 				   				 
        	}
	    	catch (Exception e) {
	    		_ex = e;
	    		return false;
	    	}	        	                               		       
        }
        	        
        @Override
        protected void onPostExecute(Boolean result) {
    		// after save operation, we navigate back to top level (assignment summary list) 
    		//  activity.  all other activities in the stack are removed
        	if (result == true) {	        
        		gs.setDirtyFlag(getResources().getString(R.string.AssignmentListSummary_DirtyFlag));
        	}
        }	        
	 }
	 	 
	 
	/**
     * Display the re-assignment picker screen
     * 
     * @throws AppException 
     */
    private void startAssignmentPickerActivity(int assignTo) throws AppException {	    	
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
           
           assignLayout.setVisibility(View.VISIBLE);           
           assignEditText.setText(data.getStringExtra("description"));
           
           loadContactTypes();  								//fill spinner (drop down) - only in add mode            
        }        
    }
    
}
