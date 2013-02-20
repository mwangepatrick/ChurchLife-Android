package com.acstech.churchlife;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionHelper;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;
import com.acstech.churchlife.listhandling.AssignmentListLoader;
import com.acstech.churchlife.listhandling.ColorCodedListItem;
import com.acstech.churchlife.listhandling.ColorCodedListItemAdapter;
import com.acstech.churchlife.listhandling.ColorCodedListItemAdapter.OnIconClickListener;
import com.acstech.churchlife.webservice.CoreAssignment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class AssignmentListActivity extends ChurchlifeBaseActivity {

	static final int DIALOG_PROGRESS = 0;
	
	int _assignmentTypeId;
	String _assignmentType;
	
	ProgressDialog _progressD;	
	AssignmentListLoader _loader;	
	ColorCodedListItemAdapter _itemArrayAdapter;
	
	ListView detailsListview;
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
		 
		 super.onCreate(savedInstanceState);
	
		 try
		 {
			 setContentView(R.layout.assignmentlist); 
			 bindControls();
			 
        	 // This activity MUST be passed data
        	 Bundle extraBundle = this.getIntent().getExtras();
             if (extraBundle == null) {
            	 throw AppException.AppExceptionFactory(
            			 ExceptionInfo.TYPE.UNEXPECTED,
						 ExceptionInfo.SEVERITY.CRITICAL, 
						 "100",           												    
						 "AssignmentListActivity.onCreate",
						 "No assignment type was passed to the Assignment List activity.");
             }
             else {	       
            	 _assignmentTypeId = extraBundle.getInt("assignmenttypeid");
            	 _assignmentType = extraBundle.getString("assignmenttype");
            	 
            	 setTitle(_assignmentType);        
            	 loadListWithProgressDialog(true);
             }		             
            
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
	 		ExceptionHelper.notifyUsers(e, AssignmentListActivity.this);
	 		ExceptionHelper.notifyNonUsers(e);
	 	}  		
	 }
	 
	  protected Dialog onCreateDialog(int id) {
	        switch(id) {
	        case DIALOG_PROGRESS:
	        	_progressD = new ProgressDialog(AssignmentListActivity.this);
	        	
	        	String msg = getString(R.string.AssignmentList_ProgressDialog);	        	
	        	_progressD.setMessage(String.format(msg, _assignmentType));        	
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
	    			_loader = new AssignmentListLoader(this, _assignmentTypeId);
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
				ExceptionHelper.notifyUsers(e, AssignmentListActivity.this);
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
		        				        	
		        		//If only 1, go directly to the detail page	  
		        		if (_loader.getList().size() == 1 && item.isTitleOnlyItem() == false) {	    					  				
		        			startAssignmentDetailActivity(Integer.parseInt(item.getId()), item.getTitle());      			
		        		}
		        		else {
		        			
		        			int index = detailsListview.getFirstVisiblePosition();
		    				View v = detailsListview.getChildAt(0);
		    				int top = (v == null) ? 0 : v.getTop();
		    				
		        			// Show the list of assignments
		           			ColorCodedListItemAdapter lia = new ColorCodedListItemAdapter(AssignmentListActivity.this, _loader.getList());
		        			
		           			// Add a 'click' listener to show team members in a dialog box		        			
		        			lia.setOnIconClickListener(new OnIconClickListener() {
								@Override
								public void onIconClick(Object id) {
									CoreAssignment asm = _loader.getAssignmentById(Integer.parseInt(id.toString()));									    		
									if (asm.TeamMembers.size() > 0)	{										
										DialogListViewFragment dlg = new DialogListViewFragment();
										dlg.setTitle(getString(R.string.Assignment_ViewTeam));
										dlg.setItems(asm.getTeamMemberList());
										dlg.show(getSupportFragmentManager(), "");
									}
								}		        				
		        			});	
		        			
		        			detailsListview.setAdapter(lia);
		        			detailsListview.setSelectionFromTop(index, top);   // restore scroll position
		        		}
		        	}
		        	else {
		        		throw _loader.getException();
		        	}
	        	}
	        	catch (Throwable e) {
	        		ExceptionHelper.notifyUsers(e, AssignmentListActivity.this);
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
	       	 		startAssignmentDetailActivity(Integer.parseInt(item.getId()), item.getTitle());    		 		       	 	
	       	 	}       	 	       	 	
	    	}
	        catch (Exception e) {
	        	// must NOT raise errors.  called by an event
				ExceptionHelper.notifyUsers(e, AssignmentListActivity.this);
		    	ExceptionHelper.notifyNonUsers(e)  ; 				    				
			}  
	    }

	    /**
	     * Display the assignment screen
	     * 
	     * @param assignmentId	    
	     */
	    private void startAssignmentDetailActivity(int assignmentId, String assignmentName) {	    	
	    	Intent intent = new Intent();
	    	intent.setClass(this, AssignmentDetailActivity.class);
		 	intent.putExtra("assignmentid", assignmentId);
		 	intent.putExtra("assignmentname", assignmentName);
		 	startActivity(intent);	  	 		    	
	    }
	 
		@Override
		protected void onResume() {
			super.onResume();
			
			try
			{
				// check global state for dirty list - if dirty, refresh and clear flag 
				GlobalState gs = GlobalState.getInstance(); 
				String dirtyFlag = getResources().getString(R.string.AssignmentList_DirtyFlag);
				if (gs.getDirtyFlagExists(dirtyFlag)) {
					_loader.clear();
					loadListWithProgressDialog(true);
					gs.clearDirtyFlag(dirtyFlag);					
				}
			}
			catch (Exception e) {
		 		if(_progressD != null){
		 			_progressD.cancel();
		 		}	 		
		 		ExceptionHelper.notifyUsers(e, AssignmentListActivity.this);
		 		ExceptionHelper.notifyNonUsers(e);
		 	}
		}
		
}
