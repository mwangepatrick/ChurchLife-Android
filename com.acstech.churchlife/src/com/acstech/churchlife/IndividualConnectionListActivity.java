package com.acstech.churchlife;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionHelper;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;
import com.acstech.churchlife.listhandling.ConnectionTeamsListLoader;
import com.acstech.churchlife.listhandling.DefaultListItem;
import com.acstech.churchlife.listhandling.DefaultListItemAdapter;
import com.acstech.churchlife.listhandling.IndividualConnectionListLoader;
import com.acstech.churchlife.webservice.CoreAcsUser;


public class IndividualConnectionListActivity extends ChurchlifeBaseActivity {

	static final int DIALOG_PROGRESS = 0;		
	private ProgressDialog _progressD;
	
	int _individualId;											// passed via intent
	String _individualName;										// passed via intent
	
	IndividualConnectionListLoader _loader;	
	DefaultListItemAdapter _itemArrayAdapter;
	
	TextView headerTextView;
	ListView lv1;
	Button addButton;
	
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
	 
	 
	 	private void startAssignmentActivity(int assignTo, int indvId, String indvName) throws AppException {
	    	Intent intent = new Intent();
	    	intent.setClass(this, AssignmentActivity.class);
		 	intent.putExtra("assignment", "");  					//new
		 	intent.putExtra("assignto", assignTo);		 	
		 	intent.putExtra("id", indvId);
		 	intent.putExtra("name", indvName);
		 			 	
		 	startActivity(intent);
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
	    
	    /**
	     *  Links state variables to their respective form controls
	     */
	    private void bindControls(){	    	
	    	lv1 = (ListView)this.findViewById(R.id.ListView01);
	    	headerTextView = (TextView)this.findViewById(R.id.headerTextView);
	    	addButton = (Button)this.findViewById(R.id.addButton);
	    		    		    
	    	// 'Add' button
	 		addButton.setOnClickListener(new OnClickListener() {				 
				@Override
				public void onClick(View view) {
					 try {
						 DialogListSingleSelectFragment dlg = new DialogListSingleSelectFragment();
						 dlg.setTitle(getResources().getString(R.string.Connection_AssignDialogTitle));	
						 
						 //-------------------------------------------------------------------
						 // Add Assignment items and their values  						 
						 // ...if login has an individual id - add the 'Assign to Me' option					 
						 // ...if no teams exist - do not show the 'team' assign option						 
						 //-------------------------------------------------------------------
						 List<String> itemList = new ArrayList<String>();
						 List<Integer> valueList = new ArrayList<Integer>();
						 					
						 // get the team count
						 ConnectionTeamsListLoader teams = new ConnectionTeamsListLoader(IndividualConnectionListActivity.this, "");						 
						 int teamCount = teams.getTeamCount();
						 
						 boolean hasIndividualId = (getCurrentUser().IndvId > 0);
						 
						 // Assign to Me
						 if (hasIndividualId) {
							 itemList.add(getResources().getString(R.string.Connection_AssignDialogMe));
							 valueList.add(AssignmentActivity.ASSIGNMODE_ME);
						 }
						 
						 // Assign to Individual (always available)
						 itemList.add(getResources().getString(R.string.Connection_AssignDialogIndividual));
						 valueList.add(AssignmentActivity.ASSIGNMODE_INDIVIDUAL);
						 
						 // Assign to Team (only if teams)
						 if (teamCount > 0) {
							 itemList.add(getResources().getString(R.string.Connection_AssignDialogTeam));
							 valueList.add(AssignmentActivity.ASSIGNMODE_TEAM);
						 }
						 
						 // convert item list to string array
						 String[] items = itemList.toArray(new String[itemList.size()]);
						 
						 // converting item values (int array) a bit more work to do
						 int[] itemValues = new int[valueList.size()];
						 for (int i=0; i < itemValues.length; i++)  {
							 itemValues[i] = valueList.get(i).intValue();
						 }
						 
						 dlg.setItems(items);
						 dlg.setItemValues(itemValues);						
						 dlg.show(getSupportFragmentManager(), "assignpicker");
						 dlg.setOnDimissListener(new DialogListSingleSelectFragment.OnDismissListener() {					
							@Override
							public void onDismiss(int selectedValue) {
								try {					
									startAssignmentActivity(selectedValue, _individualId, _individualName);
								 }
								 catch (Exception e) {
								 		ExceptionHelper.notifyUsers(e, IndividualConnectionListActivity.this);
								 		ExceptionHelper.notifyNonUsers(e);
								 }	
							}
						});						 
					 }
					 catch (Exception e) {
					 		ExceptionHelper.notifyUsers(e, IndividualConnectionListActivity.this);
					 		ExceptionHelper.notifyNonUsers(e);
					 }				 
				}			
			}); 

	 		// Hide add button if the user doesn't have permissions
	 		if (getCurrentUser().HasPermission(CoreAcsUser.PERMISSION_ASSIGNCONTACTS) == false) {
	    		addButton.setVisibility(View.GONE);	    		
	    	}
	 		
	    }
	    
	    
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
			        	//lv1.setAdapter(new DefaultListItemAdapter(IndividualConnectionListActivity.this, _loader.getList(), R.layout.listitem_withindent));
			        	lv1.setAdapter(new DefaultListItemAdapter(IndividualConnectionListActivity.this, _loader.getList(), R.layout.listitem_withtitle));			        	
		        	}
		        	else {
		        		
		        		//Special case:  The exception may be due to a lack of security (insufficient permissions). 
		        		//  If so, change the message to be more friendl
		        		Throwable e = _loader.getException();
		        		
		        		if(e instanceof AppException) {
		        			if(((AppException)e).getErrorType() == ExceptionInfo.TYPE.UNAUTHORIZED) {
		        				ArrayList<DefaultListItem> l = new ArrayList<DefaultListItem>();
		        				l.add(new DefaultListItem("You do not have permissions to view connections for this individual."));
		        				lv1.setAdapter(new DefaultListItemAdapter(IndividualConnectionListActivity.this, l, R.layout.listitem_withtitle));
		        			}
		        			else
		        			{
		        				throw e;
		        			}
		        		}
		        		else
		        		{
		        			throw e;
		        		}
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
	    		
	    		// Is this a connection type that was selected or a 'more records' item.	    	
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
