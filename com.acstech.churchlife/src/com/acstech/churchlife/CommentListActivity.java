package com.acstech.churchlife;

import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionHelper;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;
import com.acstech.churchlife.listhandling.ColorCodedListItem;
import com.acstech.churchlife.listhandling.ColorCodedListItemAdapter;
import com.acstech.churchlife.listhandling.CommentListItem;
import com.acstech.churchlife.listhandling.CommentListItemAdapter;
import com.acstech.churchlife.listhandling.CommentListLoader;
import com.acstech.churchlife.webservice.Api;
import com.acstech.churchlife.webservice.CoreAcsUser;
import com.acstech.churchlife.webservice.CoreCommentType;

public class CommentListActivity extends ChurchlifeBaseActivity {

	static final int DIALOG_PROGRESS_COMMENT = 1;			// comments for a given type

	private static final int ADD_COMMENT = 100;

	private ProgressDialog _progressD;
	
	int _individualId;										// passed via intent
	String _individualName;									// passed via intent
	int _commentTypeId;										// passed via intent
	boolean _canAddComments = false;
	
	CommentListLoader _loader;
	CommentListItemAdapter _itemArrayAdapter;
	
	TextView headerTextView;
	ListView lv1;
	Button addButton;
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        try
	        {      	 
	        	 setContentView(R.layout.commentlist);
	        	 setTitle(R.string.Comments); 
	        	 
	        	 // This activity MUST be passed the individual id object
	        	 Bundle extraBundle = this.getIntent().getExtras();
	             if (extraBundle == null) {
	            	 throw AppException.AppExceptionFactory(
	            			 ExceptionInfo.TYPE.UNEXPECTED,
							 ExceptionInfo.SEVERITY.CRITICAL, 
							 "100",           												    
							 "CommentListActivity.onCreate",
							 "No comment type was passed to the Comment List activity.");
	             }
	             else {	       
	            	 _individualId = extraBundle.getInt("id");
	            	 _individualName = extraBundle.getString("name");
	            	 _commentTypeId = extraBundle.getInt("commenttypeid");
	            	            		            	
	            	 // See if this user can add comments by making a web service call.
	            	 //  We HAVE to block this thread so that the onCreateOptionsMenu does
	            	 //   not get executed until we set the _canAddComments flag
		        	 setCanAddCommentsTask tsk = new setCanAddCommentsTask();
		        	 tsk.execute();	        	 
		        	 tsk.get();									// suspends UI until done  
		        	 
		        	 if (tsk._ex != null ) {
		        		 throw tsk._ex;	        		
		        	 }
		        
		        	 bindControls();							// Set state variables to their form controls
		        	 
	            	 headerTextView.setText(_individualName);
	            	 
	            	 loadListWithProgressDialog(true);	        	 
	             }	       
	        }
	    	catch (Exception e) {
	    		ExceptionHelper.notifyUsers(e, CommentListActivity.this);
	    		ExceptionHelper.notifyNonUsers(e);
	    	}  	        
	    }
	 
	    protected Dialog onCreateDialog(int id) {
	        switch(id) {
	        case DIALOG_PROGRESS_COMMENT:	        
	        	_progressD = new ProgressDialog(CommentListActivity.this);
	        	_progressD.setMessage(getString(R.string.CommentSummaryList_ProgressDialog));        	
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
	    	headerTextView = (TextView)this.findViewById(R.id.headerTextView);
	    	lv1 = (ListView)this.findViewById(R.id.ListView01);	  
            addButton = (Button)this.findViewById(R.id.addButton);
            
            // Wire up list on click - display comment type activity
            lv1.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) { 	                	                 	                	
                	CommentListItem itemSelected = (CommentListItem)parent.getAdapter().getItem(position);
               	 	ItemSelected(itemSelected);          	                	 
                }
            });	
                                    
	    	// add button shown/hidden by background task
        	addButton.setVisibility(View.GONE);
        	addButton.setOnClickListener(new OnClickListener() {		
            	public void onClick(View v) {	  
            		startCommentAddActivity();
            	}		
    		});	
        	
	    }
	    
	    private class setCanAddCommentsTask extends AsyncTask<Void, Void, Boolean> {
	    	 GlobalState gs = GlobalState.getInstance(); 
	    	 Exception _ex;
	    	
	        @Override
	        protected Boolean doInBackground(Void... args) {	        	
	        	try	{
	        		// Only check api if the user has rights to comments already
	        		if (gs.getUser().HasAddPermission(CoreAcsUser.PERMISSION_VIEWADDCOMMENTS)) {		        	
			        	GlobalState gs = GlobalState.getInstance(); 
				    	AppPreferences appPrefs = new AppPreferences(getApplicationContext());
						Api apiCaller = new Api(appPrefs.getWebServiceUrl(), config.APPLICATION_ID_VALUE);	
						
					   	List<CoreCommentType> results = apiCaller.commenttypes(gs.getUserName(), gs.getPassword(), gs.getSiteNumber());
					   	if (results != null) {
					   		return (results.size() > 0);
					   	}
	        		}
				   	return false;
	        	}
	        	catch (Exception e) {
		    		_ex = e;
		    		return false;
		    	}	                               		        
	        }
	        
	        @Override
	        protected void onPostExecute(Boolean result) {
	        	_canAddComments  = result;
	        	addButton.setVisibility(View.VISIBLE); // update UI
	        }
	    }
		 
	    /**
	     * Displays a progress dialog and launches a background thread to connect to a web service
	     *   to retrieve search results 
	     *   
	     */
	    private void loadListWithProgressDialog(boolean nextResult)
	    {           	    
	    	showDialog(DIALOG_PROGRESS_COMMENT);
	    	
	    	try
	    	{	
	    		// first time...
	    		if (_loader == null) {	    			
	    			_loader = new CommentListLoader(this, _individualId, _commentTypeId); 	            
	    		}
	    		
	    		// see onListLoaded below for the next steps (after load is done)
	    		if (nextResult) {
	    			_loader.LoadNext(onListLoaded);
	    		}
	    		else {
	    			_loader.LoadPrevious(onListLoaded);
	    		}
	    	}
	    	catch (Exception e) {
	    		removeDialog(DIALOG_PROGRESS_COMMENT);
				ExceptionHelper.notifyUsers(e, CommentListActivity.this);
	    		ExceptionHelper.notifyNonUsers(e); 				    				
			}  	    		    	
	    	
	    }
	    
	    // display the results from the loader operation
	    final Runnable onListLoaded = new Runnable() {
	        public void run() {
	        	
	        	try
	        	{
	        		removeDialog(DIALOG_PROGRESS_COMMENT);
	        		
		        	if (_loader.success())	{
		        		
		        		if (_loader.getList().size() > 0 && lv1.getHeaderViewsCount() == 0)
		        		{
		        			// create and format header
		   	        	 	View header = View.inflate(CommentListActivity.this, R.layout.commenttypeheader, null);
			        				   	        	 
		   	        	 	TextView tt = (TextView)header.findViewById(R.id.titleTextView);
		   	        	 	tt.setVisibility(View.VISIBLE);
		   	        	 	tt.setText(_loader.getList().get(0).getCommentType());
			        	 
		   	        	 	ImageView cv = (ImageView)header.findViewById(R.id.colorImageView);
		   	        	 	cv.setVisibility(View.VISIBLE);
		   	        	 	Bitmap b = ImageHelper.getBackground("#" + _loader.getList().get(0).getColor());
		   	        	 	cv.setImageBitmap(b);
			        	 
		   	        	 	lv1.addHeaderView(header);
		        		}			        	 

	        			// save index and top position (preserve scroll location)
	    				int index = lv1.getFirstVisiblePosition();
	    				View v = lv1.getChildAt(0);
	    				int top = (v == null) ? 0 : v.getTop();
	    				
			     		// set items to list and restore scroll position
	    				lv1.setAdapter(new CommentListItemAdapter(CommentListActivity.this, _loader.getList()));		        		
		        		lv1.setSelectionFromTop(index, top);   // restore scroll position 			        				        	
		          	}
		        	else {
		        		throw _loader.getException();
		        	}
	        	}
	        	catch (Throwable e) {
	        		ExceptionHelper.notifyUsers(e, CommentListActivity.this);
	        		ExceptionHelper.notifyNonUsers(e); 	        	
				} 	        		        
	        }
	    };
	    
	    /*
	    @Override
	    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {	
	    	super.onCreateOptionsMenu(menu);
	    	
	    	if(_canAddComments) {
	    		com.actionbarsherlock.view.MenuItem item = menu.add(Menu.NONE, ADD_COMMENT, Menu.FIRST, R.string.Comment_AddMenu);
				//item.setIcon(R.drawable.ic_menu_add);
	    	}
	    	return true;
	    }
	    
		@Override
		public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		    // Handle item selection
		    switch (item.getItemId()) {
		    
		    case ADD_COMMENT:
		    	Intent settingsIntent = new Intent(getBaseContext(), CommentActivity.class);		    							    	 		        	
		    	settingsIntent.putExtra("id", _individualId);
		    	settingsIntent.putExtra("name", _individualName);
		    	settingsIntent.putExtra("commenttypeid", _commentTypeId);	   		 	
	   		 	startActivity(settingsIntent);		    			    
		        return true;
		        		   		        	      
		    default:
		        return super.onOptionsItemSelected(item);
		    }
		}
	     */
		
		// Occurs when a user selects an item on the listview.    
	    private void ItemSelected(CommentListItem item)
	    {    	    
	    	try {
	    		// Is this a 'more records' item.	    	
	       	 	if (item.isTitleOnlyItem()) {         	 		       	 		
	       	 		loadListWithProgressDialog(true); 
	       	 	}	       	 	   	 	       	 
	    	}
	        catch (Exception e) {
	        	// must NOT raise errors.  called by an event
				ExceptionHelper.notifyUsers(e, CommentListActivity.this);
		    	ExceptionHelper.notifyNonUsers(e)  ; 				    				
			}  
	    }
	    
	    private void startCommentAddActivity()
	    {
	    	Intent settingsIntent = new Intent(getBaseContext(), CommentActivity.class);		    							    	 		        	
	    	settingsIntent.putExtra("id", _individualId);
	    	settingsIntent.putExtra("name", _individualName);
	    	settingsIntent.putExtra("commenttypeid", _commentTypeId);	   		 	
   		 	startActivity(settingsIntent);		    			    
	    }
	    
}
