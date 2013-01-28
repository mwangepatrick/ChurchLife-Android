package com.acstech.churchlife.listhandling;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.acstech.churchlife.GlobalState;
import com.acstech.churchlife.R;
import com.acstech.churchlife.R.string;
import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.webservice.CoreCommentType;
import com.acstech.churchlife.webservice.CoreConnectionTeam;
import com.acstech.churchlife.webservice.CoreIndividual;
import com.acstech.churchlife.webservice.CorePagedResult;


public class ConnectionTeamsListLoader extends ListLoaderBase<DefaultListItem>{
	
	private List<CoreConnectionTeam> _webServiceResults;		// results from webservice call	

	/* zzzz if we dont' do builditemlist....could do this
	 * 	@Override
	public ArrayList<CoreCommentType> getList(){	
		return (ArrayList<CoreConnectionTeam>) _webServiceResults;
	}
	*/
	
	
	/**
	 * calls API and gets json data in return and parses it into an object
	 */
	protected void getWebserviceResults() throws AppException { 		
		GlobalState gs = GlobalState.getInstance(); 
		//zzz cache?
	   	 _webServiceResults = getWebServiceCaller().connectionteams(gs.getUserName(), gs.getPassword(), gs.getSiteNumber());	   		   
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
				if (getSearchText().length() == 0 || item.TeamName.contains(getSearchText())) {
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
}