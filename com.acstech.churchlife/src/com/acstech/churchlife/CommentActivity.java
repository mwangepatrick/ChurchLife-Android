package com.acstech.churchlife;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionHelper;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;
import com.acstech.churchlife.listhandling.CommentTypeListLoader;
import com.acstech.churchlife.webservice.Api;
import com.acstech.churchlife.webservice.CoreCommentChangeRequest;
import com.acstech.churchlife.webservice.CoreCommentType;


public class CommentActivity extends OptionsActivity  {

	static final int DIALOG_PROGRESS = 1;			
	static final int DIALOG_SAVE = 2;

	int _individualId;										// passed via intent
	String _individualName;									// passed via intent
	int _commentTypeId = 0;									// passed via intent
		
	ProgressDialog _progressD;
	CommentTypeListLoader _loader;
	
	TextView headerTextView;
	EditText commentText;
	Spinner commentTypeSpinner;
	CheckBox chkFamilyComment;
	Button btnSave;
	Button btnCancel;
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        try
	        { 
	        	setContentView(R.layout.comment);
	        	 
	        	bindControls();							// Set state variables to their form controls	        	 	       
	        	 
	        	 // This activity MUST be passed some data about the individual and comment type to add
	        	Bundle extraBundle = this.getIntent().getExtras();
	            if (extraBundle == null) {
	            	throw AppException.AppExceptionFactory(
	            			 ExceptionInfo.TYPE.UNEXPECTED,
							 ExceptionInfo.SEVERITY.CRITICAL, 
							 "100",           												    
							 "CommentActivity.onCreate",
							 "No data was passed to the Comment activity.");
	            }
	            else {
	            	 _individualId = extraBundle.getInt("id");
	            	 _individualName = extraBundle.getString("name");
	            	 _commentTypeId = extraBundle.getInt("commenttypeid");	
	            	 
	            	 bindData();
	            }	 	
	             
	            // button click event handlers
	            btnSave.setOnClickListener(new OnClickListener() {		
	             	public void onClick(View v) {	     
	             		if (inputIsValid()) {
	             			saveComment();	
	             			finish();	             			
	             		}
	             	}		
	     		}); 

	            btnCancel.setOnClickListener(new OnClickListener() {		
	             	public void onClick(View v) {	        		             	
	             		finish();	             		        
	             	}		
	     		}); 	           
	        }
	    	catch (Exception e) {
	    		ExceptionHelper.notifyUsers(e, CommentActivity.this);
	    		ExceptionHelper.notifyNonUsers(e);
	    	}  	        
	    }

	    protected Dialog onCreateDialog(int id) {
	        switch(id) {
	        case DIALOG_PROGRESS:	        
	        	_progressD = new ProgressDialog(CommentActivity.this);	        	
	        	_progressD.setMessage(getString(R.string.Comment_ProgressDialog));        	
	        	_progressD.setIndeterminate(true);
	        	_progressD.setCancelable(false);
	    		return _progressD;	
	    	case DIALOG_SAVE:
	        	_progressD = new ProgressDialog(CommentActivity.this);	        	
	        	_progressD.setMessage(getString(R.string.Comment_SaveDialog));        	
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
	    	headerTextView = (TextView)this.findViewById(R.id.headerTextView);
	    	commentText = (EditText)this.findViewById(R.id.commentText);
	    	commentTypeSpinner = (Spinner)this.findViewById(R.id.commentTypeSpinner);
	    	chkFamilyComment = (CheckBox)this.findViewById(R.id.chkFamilyComment);
	    	btnSave = (Button)this.findViewById(R.id.btnSave);
	    	btnCancel = (Button)this.findViewById(R.id.btnCancel);	    	
	    }
	    
	    private void bindData() throws AppException {
	    	GlobalState gs = GlobalState.getInstance();
	    	
	    	headerTextView.setText(_individualName);	
	    	commentText.setText("-" + gs.getUserName());
	    	loadCommentTypes();  						//fill spinner (drop down)	   	    		    	    
	    }
	    
	    /**
	     * Displays a 'please wait' dialog and launches a background thread to connect 
	     *   to a web service to retrieve search results 
	     *   
	     */
	    private void loadCommentTypes()
	    {           	    
	    	showDialog(DIALOG_PROGRESS);
	    	
	    	try
	    	{	
	    		_loader = new CommentTypeListLoader(this);	    				    		
	    		_loader.Load(0, onListLoaded);	    		
	    	}
	    	catch (Exception e) {
				ExceptionHelper.notifyUsers(e, CommentActivity.this);
	    		ExceptionHelper.notifyNonUsers(e); 				    				
			}  	    		    		    	
	    }
	    
	    // display the results from the loader operation
	    final Runnable onListLoaded = new Runnable() {
	        public void run() {	        	
	        	try
	        	{
	        		removeDialog(DIALOG_PROGRESS);
	        		
		        	if (_loader.success())	{
		        		
		        		ArrayAdapter<CoreCommentType> adapter = new ArrayAdapter<CoreCommentType>(CommentActivity.this, android.R.layout.simple_spinner_item, _loader.getList()); 		        				        	
		        		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		        		commentTypeSpinner.setAdapter(adapter);	
		        		
		        		// if comment type was passed in, select it
		        		if (_commentTypeId != 0) {			    	    		
		    	    		int position = _loader.getItemPosition(_commentTypeId);
		    	    		commentTypeSpinner.setSelection(position-1, true);
		    	    	}		    	    			        	
		        	}
		        	else {
		        		throw _loader.getException();
		        	}
	        	}
	        	catch (Throwable e) {
	        		ExceptionHelper.notifyUsers(e, CommentActivity.this);
	        		ExceptionHelper.notifyNonUsers(e); 	        	
				} 	        		        
	        }
	    };
	    
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
	    	
    		if (commentText.getText().length() == 0) {   
    			msg = (String)this.getResources().getText(R.string.Comment_Validation);     		
    		}
    		
	    	// If a validation message exists, show it
	    	if (msg.length() > 0) {
	    		Toast.makeText(CommentActivity.this, msg, Toast.LENGTH_LONG).show(); 
	    	}
	    	
	    	// If a validation message exists, the input is invalid
	    	return (msg.length() == 0);    	    
	    }
	    
	  
	    // Input should have already been validated at this point!
	    private void saveComment() {
	    	try {	    		
	    		showDialog(DIALOG_SAVE);   			// progress dialog
	    		
	    		CoreCommentType ct = (CoreCommentType)commentTypeSpinner.getSelectedItem();
	    		
	    		CoreCommentChangeRequest req = new CoreCommentChangeRequest();
	    		req.IndvID = _individualId;
	    		req.Comment = commentText.getText().toString();
	    		req.CommentTypeId = ct.CommentTypeID;
	    		req.CommentType = ct.CommentTypeDesc;				// this should be type ID	    		
	    		req.FamilyComment = BooleanHelper.ParseBoolean(chkFamilyComment.isChecked());
	    		
	    		GlobalState gs = GlobalState.getInstance(); 
	    		AppPreferences appPrefs = new AppPreferences(this.getApplicationContext());
	    		
	    		Api apiCaller = new Api(appPrefs.getWebServiceUrl(), config.APPLICATION_ID_VALUE);	
	    		
	    	   	apiCaller.commentAdd(gs.getUserName(), gs.getPassword(), gs.getSiteNumber(), req);
	    		
	    	   	removeDialog(DIALOG_PROGRESS);
	    	   	
	    	   	Toast.makeText(CommentActivity.this, getString(R.string.Comment_Saved), Toast.LENGTH_LONG).show();	    	   	
	    	}
	        catch (Exception e) {
	        	
	        	removeDialog(DIALOG_PROGRESS);
	        	
	        	// does NOT raise errors.  called by an event
				ExceptionHelper.notifyUsers(e, CommentActivity.this);
		    	ExceptionHelper.notifyNonUsers(e)  ; 	
	        }
	    }

}
