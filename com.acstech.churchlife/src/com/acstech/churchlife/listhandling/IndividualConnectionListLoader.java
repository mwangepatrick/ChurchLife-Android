package com.acstech.churchlife.listhandling;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.acstech.churchlife.GlobalState;
import com.acstech.churchlife.R;
import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.webservice.CoreConnection;
import com.acstech.churchlife.webservice.CorePagedResult;

public class IndividualConnectionListLoader extends ListLoaderBase<DefaultListItem>{

	private int _individualId;
	
	private CorePagedResult<List<CoreConnection>> _webServiceResults;		// results from webservice call	
		
	/**
	 * calls API and gets json data in return and parses it into an object
	 */
	protected void getWebserviceResults() throws AppException { 	
		GlobalState gs = GlobalState.getInstance(); 		
	   	_webServiceResults = super.getWebServiceCaller().connections(gs.getUserName(), gs.getPassword(), gs.getSiteNumber(), _individualId, _pageIndex);	   	
	}
	
	/**
	 * After web service results are in, this procedure looks at them and creates a list of items
	 *   for a listadapter to use.   Some items may be 'Next' or 'Previous' or 'No Results Found'
	 *   
	 */
	protected void buildItemList() {
		
		// Create an DefaultListItem arraylist 
		if (_itemList == null ) {
			_itemList = new ArrayList<DefaultListItem>();
		}
		
		// check for empty
		if (_webServiceResults.Page.size() == 0) {    					    		
			_itemList.add(new DefaultListItem(getNoResultsMessage()));	    				
		}	    			
		else {		
			
			// Remove the 'More Results' item at the end of the list if this is a subsequent load
			if (_itemList.size() > 1 && _itemList.get(_itemList.size()-1).getTitle().equals(getNextResultsMessage())) {
				_itemList.remove(_itemList.size()-1);				
			}
			
			// Add all items from the latest web service request to the adapter 
			for (CoreConnection item : _webServiceResults.Page) {
				_itemList.add(new DefaultListItem(item.ConnectionId, item.Comment, item.getFullTitle()));
			}
			
  	    	// If the web service indicates more records...Add the 'More Records' item
			if (_webServiceResults.PageIndex < _webServiceResults.PageCount -1) {	    					 	    		
				_itemList.add(new DefaultListItem(getNextResultsMessage()));			    								    				
			}			    					
		}
		
	}
	
	public IndividualConnectionListLoader(Context context, int individualId) {
		super(context);
		_individualId = individualId;
		
		super.setNoResultsMessage(R.string.IndividualConnection_NoResults);
		super.setNextResultsMessage(R.string.IndividualList_More);		
	}
}
