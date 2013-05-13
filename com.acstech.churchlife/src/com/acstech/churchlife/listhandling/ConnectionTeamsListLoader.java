package com.acstech.churchlife.listhandling;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.os.AsyncTask;

import com.acstech.churchlife.AppPreferences;
import com.acstech.churchlife.GlobalState;
import com.acstech.churchlife.R;
import com.acstech.churchlife.config;
import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.webservice.Api;
import com.acstech.churchlife.webservice.CoreCommentChangeRequest;
import com.acstech.churchlife.webservice.CoreConnectionTeam;


public class ConnectionTeamsListLoader extends ListLoaderBase<DefaultListItem>{
	
	// Helper Method for team counting 
	public int getTeamCount() 
	{
		TeamCountTask tsk = new TeamCountTask(); 
		tsk.execute();
    	int numberTeams;
    	
		try {
			numberTeams = tsk.get();
		} catch (Exception e) {
			return -1;
		}
    	return numberTeams;
	}
	
	
	private List<CoreConnectionTeam> _webServiceResults;		// results from webservice call	
	
	/**
	 * calls API and gets json data in return and parses it into an object
	 */
	protected void getWebserviceResults() throws AppException { 		
		GlobalState gs = GlobalState.getInstance(); 
		
		// The team list does not change (search just filters the existing list).  So, this is cached after its' 
		//  first retrieval (no need to go back to web service)
		if ( _webServiceResults == null) {
	   	 	_webServiceResults = getWebServiceCaller().connectionteams(gs.getUserName(), gs.getPassword(), gs.getSiteNumber());
		}
	}
	
	/**
	 * After web service results are in, this procedure looks at them and creates a list of items
	 *   for a listadapter to use.   Some items may be 'Next' or 'Previous' or 'No Results Found'
	 *   
	 */
	protected void buildItemList() {
					
		if (_itemList == null ) {
			_itemList = new ArrayList<DefaultListItem>();
		}
		
		// check for empty
		if (_webServiceResults.size() == 0) {    					    		
			_itemList.add(new DefaultListItem(getNoResultsMessage()));	    				
		}
		else {			
			// Add all items from the latest web service request to the adapter			
			// ...if search text is provided, only add those team names that match!					
			for (CoreConnectionTeam item : _webServiceResults) {				
				if (getSearchText().length() == 0 || item.TeamName.toLowerCase(Locale.US).contains(getSearchText().toLowerCase(Locale.US))) {
					_itemList.add(new DefaultListItem(Integer.toString(item.TeamId), "", item.TeamName));	
				}
			}		    					
		}				
	}
	
	public ConnectionTeamsListLoader(Context context, String searchText){
		super(context);
		setSearchText(searchText);

		super.setNextResultsMessage(R.string.IndividualList_More);
		super.setNoResultsMessage(R.string.IndividualList_NoResults);
	}

    // task to load teams and block until loaded     
 	private class TeamCountTask extends AsyncTask<Void, Void, Integer> {
		Exception _ex;
		
		@Override
	    protected Integer doInBackground(Void... args) {	        	
	    	try	{
	    		GlobalState gs = GlobalState.getInstance(); 
		    	AppPreferences appPrefs = new AppPreferences(_context);
				Api apiCaller = new Api(appPrefs.getWebServiceUrl(), config.APPLICATION_ID_VALUE);
				
	    		List<CoreConnectionTeam> _results = apiCaller.connectionteams(gs.getUserName(), gs.getPassword(), gs.getSiteNumber()); 	        		
				return _results.size(); 				   				 
	    	}
	    	catch (Exception e) {
	    		_ex = e;
	    		return -1;
	    	}	        	                               		       
	    }	        	        
 	}
 	
}


