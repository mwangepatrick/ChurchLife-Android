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

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionHelper;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;
import com.acstech.churchlife.webservice.CoreCommentType;


public class CommentActivity extends OptionsActivity  {

	static final int DIALOG_PROGRESS = 1;			
	
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
	             		saveComment();	             		        
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
	    	headerTextView.setText(_individualName);	    		    	    	
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
	    		_loader = new CommentTypeListLoader();	    				    		
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
	    
	    //TODO
	    private void saveComment() {
	    	try {
	    		
	    		CoreCommentType ct = (CoreCommentType)commentTypeSpinner.getSelectedItem();
	    		
	    		String comment = commentText.getText().toString();
	    		
	    		
	    		
	    		
	    	}
	        catch (Exception e) {
	        	// does NOT raise errors.  called by an event
				ExceptionHelper.notifyUsers(e, CommentActivity.this);
		    	ExceptionHelper.notifyNonUsers(e)  ; 	
	        }
	    }
	
}
