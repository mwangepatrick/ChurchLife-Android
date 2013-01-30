package com.acstech.churchlife;

import com.acstech.churchlife.exceptionhandling.ExceptionHelper;
import com.acstech.churchlife.listhandling.AssignmentSummaryListLoader;
import com.acstech.churchlife.listhandling.ColorCodedListItem;
import com.acstech.churchlife.listhandling.ColorCodedListItemAdapter;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class AssignmentSummaryListActivity extends ChurchlifeBaseActivity {

	static final int DIALOG_PROGRESS = 0;
	
	ProgressDialog _progressD;	
	AssignmentSummaryListLoader _loader;	
	ColorCodedListItemAdapter _itemArrayAdapter;
	
	ListView detailsListview;
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
		 
		 super.onCreate(savedInstanceState);
	
		 try
		 {
			 setTitle(R.string.Menu_Connections);
			 setContentView(R.layout.assignmentlist); 
			 bindControls();
			 
			 loadListWithProgressDialog(true);
			 
			 // assignment click event
			 detailsListview.setOnItemClickListener(new OnItemClickListener() {
				 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {            	
				   	 ColorCodedListItem itemSelected = (ColorCodedListItem)parent.getAdapter().getItem(position);
	            	 ItemSelected(itemSelected);           	
		         }
			 });
			
		}
	 	catch (Exception e) {
	 		if(_progressD != null){
	 			_progressD.cancel();
	 		}	 		
	 		ExceptionHelper.notifyUsers(e, AssignmentSummaryListActivity.this);
	 		ExceptionHelper.notifyNonUsers(e);
	 	}  		
	 }
	 
	  protected Dialog onCreateDialog(int id) {
	        switch(id) {
	        case DIALOG_PROGRESS:
	        	_progressD = new ProgressDialog(AssignmentSummaryListActivity.this);
	        	
	        	String msg = getString(R.string.AssignmentListSummary_ProgressDialog);	        		        
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
		 detailsListview = (ListView)this.findViewById(R.id.detailsListview);    	
	 }
	
	 /**
	 * Displays a progress dialog and launches a background thread to connect to a web service
	 *   to retrieve search results 
	 *   
	 */
	 private void loadListWithProgressDialog(boolean nextResult)
	    {           
		 	//zzz change to use fragments to house dialog 
	    	showDialog(DIALOG_PROGRESS);
	    	
	    	try
	    	{	
	    		// if first time....
	    		if (_loader == null) {	    			
	    			_loader = new AssignmentSummaryListLoader(this);
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
				ExceptionHelper.notifyUsers(e, AssignmentSummaryListActivity.this);
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
		        				        		
		        		//If only 1 type, go directly to the detail list page	  
		        		if (_loader.getList().size() == 1 && item.isTitleOnlyItem() == false) {	    					  				
		        			startAssignmentListActivity(Integer.parseInt(item.getId()), item.getTitle(), true);      			
		        		}
		        		else {
		        			// save index and top position (preserve scroll location)
		    				int index = detailsListview.getFirstVisiblePosition();
		    				View v = detailsListview.getChildAt(0);
		    				int top = (v == null) ? 0 : v.getTop();
		    				
				     		// set items to list
		        			detailsListview.setAdapter(new ColorCodedListItemAdapter(AssignmentSummaryListActivity.this, _loader.getList()));
		        			
		        			detailsListview.setSelectionFromTop(index, top);   // restore scroll position  
		        		}
		        	}
		        	else {
		        		throw _loader.getException();
		        	}
	        	}
	        	catch (Throwable e) {
	        		ExceptionHelper.notifyUsers(e, AssignmentSummaryListActivity.this);
	        		ExceptionHelper.notifyNonUsers(e); 	        	
				} 	        	
	        	finally
	        	{
	        		removeDialog(DIALOG_PROGRESS);
	        	}
	        }
	    };    
	    	    	  	  
	    // Occurs when a user selects an comment type on the listview.    
	    private void ItemSelected(ColorCodedListItem item)
	    {    	    
	    	try {
	    		// Is this a comment type that was selected or a 'more records' item.	    	
	       	 	if (item.isTitleOnlyItem()) {         	 		       	 		
	       	 		loadListWithProgressDialog(true);  
	       	 	}
	       	 	else {	 
	       	 		startAssignmentListActivity(Integer.parseInt(item.getId()), item.getTitle(), false);    		 		       	 	
	       	 	}       	 	       	 	
	    	}
	        catch (Exception e) {
	        	// must NOT raise errors.  called by an event
				ExceptionHelper.notifyUsers(e, AssignmentSummaryListActivity.this);
		    	ExceptionHelper.notifyNonUsers(e)  ; 				    				
			}  
	    }

	    /**
	     * Display the assignment type (list) screen
	     * 
	     * @param individualId
	     * @param individualName
	     * @param commentTypeId
	     */
	    private void startAssignmentListActivity(int assignmentTypeId, String assignmentType, boolean closeThisActivity) {
	    	
	    	Intent intent = new Intent();
		 	intent.setClass(this, AssignmentListActivity.class);
		 	intent.putExtra("assignmenttypeid", assignmentTypeId);
		 	intent.putExtra("assignmenttype", assignmentType);
		 	startActivity(intent);	  
		 	
		 	if (closeThisActivity) {
		 		finish();
		 	}
	    }
	 
}
