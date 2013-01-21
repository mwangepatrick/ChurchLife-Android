package com.acstech.churchlife;

import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionHelper;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;
import com.acstech.churchlife.listhandling.ColorCodedListItem;
import com.acstech.churchlife.listhandling.ColorCodedListItemAdapter;
import com.acstech.churchlife.listhandling.CommentSummaryListLoader;
import com.acstech.churchlife.webservice.Api;
import com.acstech.churchlife.webservice.CoreCommentType;

public class CommentSummaryListActivity extends ChurchlifeBaseActivity {

	static final int DIALOG_PROGRESS_COMMENTSUMMARY = 0;		// all comment types (summary)
	
	private static final int ADD_COMMENT = 100;
	
	private ProgressDialog _progressD;
	
	int _individualId;											// passed via intent
	String _individualName;										// passed via intent
	boolean _canAddComments = false;
	
	CommentSummaryListLoader _loader;	
	ColorCodedListItemAdapter _itemArrayAdapter;
	
	TextView headerTextView;
	ListView lv1;
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        try
	        {      	 
	        	 setContentView(R.layout.commentlist);
	        	 setTitle("Comments"); //zzz
	        	 
	        	 bindControls();							// Set state variables to their form controls	        	 	       
	        	 	        	 
	        	 // This activity MUST be passed data
	        	 Bundle extraBundle = this.getIntent().getExtras();
	             if (extraBundle == null) {
	            	 throw AppException.AppExceptionFactory(
	            			 ExceptionInfo.TYPE.UNEXPECTED,
							 ExceptionInfo.SEVERITY.CRITICAL, 
							 "100",           												    
							 "CommentSummaryActivity.onCreate",
							 "No individual id was passed to the Comment Summary activity.");
	             }
	             else {	       
	            	 _individualId = extraBundle.getInt("id");
	            	 _individualName = extraBundle.getString("name");
	            	 
	            	 headerTextView.setText(_individualName);	            	 
	            	 loadListWithProgressDialog(true);
	             }		             
	        	 
	             // Wire up list on click - display comment type activity
	             lv1.setOnItemClickListener(new OnItemClickListener() {
	                 public void onItemClick(AdapterView<?> parent, View view, int position, long id) { 	                	                 	                	
	                	 ColorCodedListItem itemSelected = (ColorCodedListItem)parent.getAdapter().getItem(position);
	                	 ItemSelected(itemSelected);          	                	 
	                 }
	             });	             	        	         	
	        }
	    	catch (Exception e) {
	    		removeDialog(DIALOG_PROGRESS_COMMENTSUMMARY);
	    		ExceptionHelper.notifyUsers(e, CommentSummaryListActivity.this);
	    		ExceptionHelper.notifyNonUsers(e);
	    	}  	        
	    }
	 
	    protected Dialog onCreateDialog(int id) {
	        switch(id) {
	        case DIALOG_PROGRESS_COMMENTSUMMARY:
	        	_progressD = new ProgressDialog(CommentSummaryListActivity.this);
	        	
	        	String msg = getString(R.string.CommentSummaryList_ProgressDialog);	        		        
	        	_progressD.setMessage(msg);        	
	        	_progressD.setIndeterminate(true);
	        	_progressD.setCancelable(false);
	    		return _progressD;	  
	        default:
	            return null;
	        }
	    }

	    @Override
	    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {	
	    	super.onCreateOptionsMenu(menu);
	    	
	    	if (_canAddComments) { 
	    		com.actionbarsherlock.view.MenuItem item = menu.add(Menu.NONE, ADD_COMMENT, Menu.FIRST, R.string.Comment_AddMenu); 
				item.setIcon(R.drawable.ic_menu_add);
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
		    	settingsIntent.putExtra("commenttypeid", 0);	   		 	
	   		 	startActivity(settingsIntent);		    			    
		        return true;
		        		   		        	      
		    default:
		        return super.onOptionsItemSelected(item);
		    }
		}
		
	    /**
	     *  Links state variables to their respective form controls
	     */
	    private void bindControls(){	    	
	    	lv1 = (ListView)this.findViewById(R.id.ListView01);
	    	headerTextView = (TextView)this.findViewById(R.id.headerTextView);
	    }
	    
	    private void setCanAddComments() throws AppException {
	    	
	    	GlobalState gs = GlobalState.getInstance(); 
	    	AppPreferences appPrefs = new AppPreferences(getApplicationContext());
			Api apiCaller = new Api(appPrefs.getWebServiceUrl(), config.APPLICATION_ID_VALUE);	
			
		   	List<CoreCommentType> results = apiCaller.commenttypes(gs.getUserName(), gs.getPassword(), gs.getSiteNumber());
		   	if (results != null) 	{
		   		_canAddComments = (results.size() > 0);
		   	}
	    }
	    
	    
	    /**
	     * Displays a progress dialog and launches a background thread to connect to a web service
	     *   to retrieve search results 
	     *   
	     */
	    private void loadListWithProgressDialog(boolean nextResult)
	    {           	    
	    	showDialog(DIALOG_PROGRESS_COMMENTSUMMARY);
	    	
	    	try
	    	{	
	    		// if first time....
	    		if (_loader == null) {	    			
	    			_loader = new CommentSummaryListLoader(this, _individualId);
	    			setCanAddComments();
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
	    		removeDialog(DIALOG_PROGRESS_COMMENTSUMMARY);
				ExceptionHelper.notifyUsers(e, CommentSummaryListActivity.this);
	    		ExceptionHelper.notifyNonUsers(e); 				    				
			}  	    	   	    
	    }
	        
	    // display the results from the loader operation
	    final Runnable onListLoaded = new Runnable() {
	        public void run() {
	        	
	        	try
	        	{	        		        	
		        	if (_loader.success())	{
		        		
		        		ColorCodedListItem item =(ColorCodedListItem)_loader.getList().get(0);
		        		
		        		//If only 1 type, go directly to the comment type (detail) page	  
		        		if (_loader.getList().size() == 1 && item.isTitleOnlyItem() == false) {	    					  				
		        			startCommentListActivity(_individualId, _individualName, Integer.parseInt(item.getId()), true);		        			
		        		}
		        		else {
				     		// set items to list
			        		lv1.setAdapter(new ColorCodedListItemAdapter(CommentSummaryListActivity.this, _loader.getList()));
		        		}
		        	}
		        	else {
		        		throw _loader.getException();
		        	}
	        	}
	        	catch (Throwable e) {
	        		ExceptionHelper.notifyUsers(e, CommentSummaryListActivity.this);
	        		ExceptionHelper.notifyNonUsers(e); 	        	
				} 	        	
	        	finally
	        	{
	        		removeDialog(DIALOG_PROGRESS_COMMENTSUMMARY);
	        	}
	        }
	    };    
	    	    	  	  
	    // Occurs when a user selects an comment type on the listview.    
	    private void ItemSelected(ColorCodedListItem item)
	    {    	    
	    	try {
	    		// Is this a comment type that was selected or a 'more records' item.	    	
	       	 	if (item.isTitleOnlyItem()) {         	 		       	 		
	       	 		loadListWithProgressDialog(true);  // how to tell back/next?
	       	 	}
	       	 	else {	 
	       	 		startCommentListActivity(_individualId, _individualName, Integer.parseInt(item.getId()), false);    		 		       	 	
	       	 	}       	 	       	 	
	    	}
	        catch (Exception e) {
	        	// must NOT raise errors.  called by an event
				ExceptionHelper.notifyUsers(e, CommentSummaryListActivity.this);
		    	ExceptionHelper.notifyNonUsers(e)  ; 				    				
			}  
	    }

	    /**
	     * Display the comment type screen
	     * 
	     * @param individualId
	     * @param individualName
	     * @param commentTypeId
	     */
	    private void startCommentListActivity(int individualId, String individualName, int commentTypeId, boolean closeThisActivity) {
	    	Intent intent = new Intent();
   		 	intent.setClass(this, CommentListActivity.class); 		        	 	
   		 	intent.putExtra("id", _individualId);
   		 	intent.putExtra("name", _individualName);
   		 	intent.putExtra("commenttypeid", commentTypeId);
   		 	startActivity(intent);	  
   		 	
   		 	if (closeThisActivity) {
   		 		finish();
   		 	}
	    }
	    
}
