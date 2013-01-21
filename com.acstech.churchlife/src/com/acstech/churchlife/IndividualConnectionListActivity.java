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
import com.acstech.churchlife.listhandling.DefaultListItem;
import com.acstech.churchlife.listhandling.DefaultListItemAdapter;
import com.acstech.churchlife.listhandling.IndividualConnectionListLoader;
import com.acstech.churchlife.webservice.Api;
import com.acstech.churchlife.webservice.CoreCommentType;

public class IndividualConnectionListActivity extends ChurchlifeBaseActivity {

	static final int DIALOG_PROGRESS = 0;		
	private ProgressDialog _progressD;
	
	int _individualId;											// passed via intent
	String _individualName;										// passed via intent
	
	IndividualConnectionListLoader _loader;	
	DefaultListItemAdapter _itemArrayAdapter;
	
	TextView headerTextView;
	ListView lv1;
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        try
	        {      	 
	        	 setContentView(R.layout.commentlist);
	        	 setTitle(R.string.IndividualConnection_Title);
	        	 
	        	 bindControls();							// Set state variables to their form controls	        	 	       
	        	 	        	 
	        	 // This activity MUST be passed data
	        	 Bundle extraBundle = this.getIntent().getExtras();
	             if (extraBundle == null) {
	            	 throw AppException.AppExceptionFactory(
	            			 ExceptionInfo.TYPE.UNEXPECTED,
							 ExceptionInfo.SEVERITY.CRITICAL, 
							 "100",           												    
							 "IndividualConnectionList.onCreate",
							 "No individual id was passed to the Individual Connection List activity.");
	             }
	             else {	       
	            	 _individualId = extraBundle.getInt("id");
	            	 _individualName = extraBundle.getString("name");
	            	 
	            	 headerTextView.setText(_individualName);	            	 
	            	 loadListWithProgressDialog(true);
	             }		             
	        	 	             
	             // Wire up list on click 
	             lv1.setOnItemClickListener(new OnItemClickListener() {
	                 public void onItemClick(AdapterView<?> parent, View view, int position, long id) { 	                	                 	                	
	                	 DefaultListItem itemSelected = (DefaultListItem)parent.getAdapter().getItem(position);
	                	 ItemSelected(itemSelected);          	                	 
	                 }
	             });	             	        	         	
	        }
	    	catch (Exception e) {
	    		removeDialog(DIALOG_PROGRESS);
	    		ExceptionHelper.notifyUsers(e, IndividualConnectionListActivity.this);
	    		ExceptionHelper.notifyNonUsers(e);
	    	}  	        
	    }
	 
	    protected Dialog onCreateDialog(int id) {
	        switch(id) {
	        case DIALOG_PROGRESS:
	        	_progressD = new ProgressDialog(IndividualConnectionListActivity.this);
	        	
	        	String msg = String.format(getString(R.string.IndividualConnection_LoadingWithTitle), _individualName);	        		        
	        	_progressD.setMessage(msg);        	
	        	_progressD.setIndeterminate(true);
	        	_progressD.setCancelable(false);
	    		return _progressD;	  
	        default:
	            return null;
	        }
	    }

	    /*
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
		*/
	    
	    /**
	     *  Links state variables to their respective form controls
	     */
	    private void bindControls(){	    	
	    	lv1 = (ListView)this.findViewById(R.id.ListView01);
	    	headerTextView = (TextView)this.findViewById(R.id.headerTextView);
	    }
	    
	    /*
	    private void setCanAddComments() throws AppException {
	    	
	    	GlobalState gs = GlobalState.getInstance(); 
	    	AppPreferences appPrefs = new AppPreferences(getApplicationContext());
			Api apiCaller = new Api(appPrefs.getWebServiceUrl(), config.APPLICATION_ID_VALUE);	
			
		   	List<CoreCommentType> results = apiCaller.commenttypes(gs.getUserName(), gs.getPassword(), gs.getSiteNumber());
	    	_canAddComments = (results.size() > 0);		   	
	    }
	    */
	    
	    
	    /**
	     * Displays a progress dialog and launches a background thread to connect to a web service
	     *   to retrieve search results 
	     *   
	     */
	    private void loadListWithProgressDialog(boolean nextResult)
	    {           	    
	    	showDialog(DIALOG_PROGRESS);
	    	
	    	try
	    	{	
	    		// if first time....
	    		if (_loader == null) {	    			
	    			_loader = new IndividualConnectionListLoader(this, _individualId);
	    			//setCanAddComments();
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
				ExceptionHelper.notifyUsers(e, IndividualConnectionListActivity.this);
	    		ExceptionHelper.notifyNonUsers(e); 				    				
			}  	    	   	    
	    }
	        
	    // display the results from the loader operation
	    final Runnable onListLoaded = new Runnable() {
	        public void run() {
	        	
	        	try
	        	{	        		        	
		        	if (_loader.success())	{		        		
				     	// set items to list
			        	lv1.setAdapter(new DefaultListItemAdapter(IndividualConnectionListActivity.this, _loader.getList()));		        		
		        	}
		        	else {
		        		throw _loader.getException();
		        	}
	        	}
	        	catch (Throwable e) {
	        		ExceptionHelper.notifyUsers(e, IndividualConnectionListActivity.this);
	        		ExceptionHelper.notifyNonUsers(e); 	        	
				} 	        	
	        	finally
	        	{
	        		removeDialog(DIALOG_PROGRESS);
	        	}
	        }
	    };    
	    	    	  	  
	    // Occurs when a user selects an comment type on the listview.    
	    private void ItemSelected(DefaultListItem item)
	    {    	    
	    	try {
	    		
	    		// Is this a comment type that was selected or a 'more records' item.	    	
	       	 	if (item.isTitleOnlyItem()) {         	 		       	 		
	       	 		loadListWithProgressDialog(true);  
	       	 	}
	       	 	else {	 
	       	 		//nothing to do  		 		       	 	
	       	 	} 
	       	 	      	 	       	 	
	    	}
	        catch (Exception e) {
	        	// must NOT raise errors.  called by an event
				ExceptionHelper.notifyUsers(e, IndividualConnectionListActivity.this);
		    	ExceptionHelper.notifyNonUsers(e)  ; 				    				
			}  
	    }

	    	    
}
